var elemcounter = 1;
var originalindex = -1;
var draggedelements = null;
$(function() {
	$(".sortable").sortable({
		placeholder: "draggable-active",
		scroll: true,
		scrollSensitivity: 100,
		handle: "div",
		start: function(e, ui) {
			originalindex = $(ui.item).index();
			$(".draggable-active").append("<div class='droppable-text'>" + getPropertyLabel("dropelementhere") + "</div>");
			
			if ($(".survey-element.selectedquestion").length > 1)
			{
				draggedelements = $("#content").find(".survey-element.selectedquestion");
			} else {
				draggedelements = null;
			}
		},
		stop: function(event, ui) {
			
			if (ui.item.hasClass("copy"))
			{
				_actions.copyElement(ui.item);
				checkContent();	
			} else if (ui.item.hasClass("cut"))
			{
				_actions.pasteElement(ui.item);
				checkContent();	
			} else if (ui.item.hasClass("toolboxitem"))
			{
				if (addNewElement(ui.item, null)) {
					_undoProcessor.addUndoStep(["ADD", $(ui.item).attr("id"), $(ui.item).index()]);
					goTo($(ui.item).attr("id"));
					addToNavigation(ui.item, $(ui.item).index());
				}
			} else {
				if (checkDependenciesAfterMove(ui.item))
				{
					_elementProperties.showProperties(ui.item, null, false);
					_undoProcessor.addUndoStep(["MOVE", $(ui.item).attr("id"), originalindex, $(ui.item).index()]);
					moveItemInNavigation(originalindex, $(ui.item).index());
					
					if (draggedelements != null)
					{
						var found = false;
					 	var lastitem = $(ui.item);
					 	draggedelements.each(function(i){
					 		var id = $(this).attr("id");
					 		originalindex = $(this).index();
					 		if (id != $(ui.item).attr("id"))
					 		{
					 			if (!found)
					 			{
					 				//above item
					 				$(this).insertBefore($(ui.item));
					 			} else {
					 				//below item
					 				$(this).insertAfter(lastitem);
					 				lastitem = $(this);
					 				_undoProcessor.addUndoStep(["MOVE", $(this).attr("id"), originalindex, $(this).index()]);
					 			}
					 		} else {
					 			found = true;
					 		}
					 	});
					 		                                                
					 	createNavigation(false);
					 }
				} else {
					$('#invalid-dependency-dialog').modal('show');
					$( "#content" ).sortable( "cancel" );
				}
			}

			updateTitles();
			deactivateLinks();
		}
	});
    $(".sortable").disableSelection();
	
	$(".draggable" ).draggable({
      connectToSortable: "#content",
	  appendTo: "#content",
      helper: "clone"	  
    });
	
	$(".toolboxitem").dblclick(function(e){
		
		if ($(this).hasClass("copy"))
		{
			var item = $(this).clone();
			$("#content").append(item);
			_actions.copyElement(item);
			checkContent();	
		} else if ($(this).hasClass("cut"))
		{
			var item = $(this).clone();
			$("#content").append(item);
			_actions.pasteElement(item);
			checkContent();
		} else {
			var item = $(this).clone();
			$("#content").append(item);
			if (addNewElement(item, null)) {
				_undoProcessor.addUndoStep(["ADD", $(item).attr("id"), $(item).index()]);
				goTo($(item).attr("id"));
				addToNavigation(item, $(item).index());
			}
		}
		
		updateTitles();
	});
	
	$( ".properties" ).resizable({
		handles: "w",
		minWidth: 330,
		 resize: function( event, ui ) {
			    ui.element.css("left","");
			  }
	});
	
	window.onbeforeunload = function() { 		
		  if (_actions.SaveEnabled() && !_actions.UnsavedChangesConfirmed())
		  {
		    return getPropertyLabel("UnsavedChanges");
		  }	
	};
	
	$(window).resize(function() {
		setContentMargin();
	});
	
	setContentMargin();
	
	$('.accordion-toggle').each(function(){
    	$(this).prepend("<span class='glyphicon glyphicon-chevron-down'></span>");
    	$(this).click(function(){
    		if (!$(this).hasClass('collapsed'))
    		{
    			$(this).find('.glyphicon').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-right');
    		} else {
    			$(this).find('.glyphicon').removeClass('glyphicon-chevron-right').addClass('glyphicon-chevron-down');
    		}
    	});
    });	
	
	$("#divActionsFromMenu, #menutab2, .header").find("a").each(function(){
		if (!$(this).attr("onclick") && $(this).attr("href") && $(this).attr("href") != "#" && !$(this).hasClass("dropdown-toggle"))
		$(this).attr("onclick", "return checkChanges(this)");
	});

	$("#menuShowCreateSurveyDialogButton").attr("onclick", "checkChangesNewSurvey()");
	
	checkContent();	
});

$(document).keydown(function(event){
    if(event.which=="16")
    	shiftIsPressed = true;
    if(event.which=="17")
        cntrlIsPressed = true;
    
    if(event.which=="27")
    {
    	$(".btn-primary1").each(function(){
    		var id = $(this).closest("td").find("textarea").last().attr("id");
    		var ed = tinyMCE.get(id);
    		if (ed != null) ed.execCommand('mceFullScreen');
    	});
    }
});

$(document).keyup(function(event){   
	if(event.which=="16")
	    shiftIsPressed = false;
	if(event.which=="17")
	    cntrlIsPressed = false;
	
    if (event.which=="13")
    {
    	var focused = $(document.activeElement);
    	if (typeof focused != 'undefined' && focused.length > 0 && focused.closest(".properties").length > 0)
    	{
    		update(focused);
    	}
    }
    
    if ($('#mce-modal-block').is(":visible")) {
    	return;
    }
    
    if (event.which=="46" && _actions.DeleteEnabled())
    {
    	var focused = $(document.activeElement);
    	if (typeof focused != 'undefined' && focused.length > 0 && focused.closest(".properties").length > 0)
    	{
    		return;
    	}
        _actions.deleteElement();
    }
    
    if (cntrlIsPressed && event.which=="67" && _actions.CopyEnabled())
    	_actions.copySelectedElement();
    
    if (cntrlIsPressed && event.which=="86" && _actions.PasteEnabled() && !_actions.DialogOpen())
    	_actions.pasteElementAfter();
    
});

function deactivateLinks()
{
	$("#content").find("a").removeAttr("href").removeAttr("onkeypress").removeAttr("onclick");
}

var cntrlIsPressed = false;
var shiftIsPressed = false;

function checkContent()
{
	if ($("#editcontent").find("li").length > 0)
	{
		$("#empty-content-message").hide();
	} else {
		$("#empty-content-message").show();
	}

	SurveyRuleEvaluator.recalculate()
}

function checkDependenciesAfterMove(item){
	 var myId = item.attr("id");
	 var result = true;
	 
	 item.find("input[name^='dependencies']").each(function(){
		 var choiceFound = false;
		 var dependencies = $(this).val();
		if (dependencies != null && dependencies.length > 0)
		{
			
			$("#content").find(".survey-element").each(function(){
				if ($(this).attr("id") == myId)
				{
					choiceFound = true;
					return;
				}
				if (dependencies.indexOf($(this).attr("id")) > -1 && !choiceFound)
				{
					result = false;
					return;															
				}
				
				$(this).find(".matrix-header").each(function(){
					if (dependencies.indexOf($(this).attr("id")) > -1 && !choiceFound)
					{
						result = false;
						return;															
					}
				});
				
			});
		}
	 });
	 
	if (item.attr("data-triggers"))
	{
		var triggers = item.attr("data-triggers").split(";");
		
		for (var i = 0; i < triggers.length; i++)
		{		
			var triggerId = triggers[i];
			
			var trigger;
			if (triggerId.indexOf("|") > 0)
			{
				trigger = $("input[data-cellid='" + triggerId + "']");
			} else {
				trigger = $("#" + triggerId);
			}
			
			if (!$(trigger).hasClass("survey-element"))
			{
				trigger = $(trigger).closest(".survey-element");
			}
			
			if ($(trigger).index() > item.index())
			{
				result = false;
				return result;
			}			

		};
	} 
	
	$(item).find('[data-triggers]').each(function(){
		var triggers = $(this).attr("data-triggers").split(";");
		
		for (var i = 0; i < triggers.length; i++)
		{		
			var triggerId = triggers[i];
			if (triggerId.length > 0)
			{
				var trigger;
				if (triggerId.indexOf("|") > 0)
				{
					trigger = $("input[data-cellid='" + triggerId + "']");
				} else {
					trigger = $("#" + triggerId);
				}
				
				if (!$(trigger).hasClass("survey-element"))
				{
					trigger = $(trigger).closest(".survey-element");
				}
				
				if ($(trigger).index() > $(this).closest(".survey-element").index())
				{
					result = false;
					return result;
				}	
			}
		};
	})
 
	return result;
}


function checkChanges(link)
{
	if (!_actions.SaveEnabled()) return true;
	
	$("#checkChangesDialogDontSaveButton").show().attr("href", $(link).attr("href"));
	$("#checkChangesDialogDontSaveButtonEditor").hide();
	$("#editorredirect").val($(link).attr("href"));
	
	$('#checkChangesDialog').modal("show");
	return false;
}

function checkChangesNewSurvey() {
	if (checkChanges(undefined)) {
		// no changes detected
		showCreateSurveyDialog();
		return;
	}

	$("#checkChangesDialogDontSaveButton").hide();
	$("#checkChangesDialogDontSaveButtonEditor").show();
}

function createSurveyIgnoreChanges() {
	window.ignoreUnsavedChanges = true;
	showCreateSurveyDialog();
}

function getIcon(element)
{
	var span = document.createElement("span");
	
	if (element.hasClass("sectionitem")) {
		$(span).addClass("glyphicon glyphicon-folder-open");				
	} else if (element.hasClass("freetextitem")) {
		$(span).addClass("glyphicon glyphicon-pencil");
	} else if (element.hasClass("singlechoiceitem")) {
		$(span).addClass("glyphicon glyphicon-ok-circle");
	} else if (element.hasClass("multiplechoiceitem")) {
		$(span).addClass("glyphicon glyphicon-check");
	} else if (element.hasClass("numberitem")) {
		$(span).addClass("glyphicon glyphicon-sound-5-1");
	} else if (element.hasClass("matrixitem")) {
		$(span).addClass("glyphicon glyphicon-list-alt");
	} else if (element.hasClass("mytableitem")) {
		$(span).addClass("glyphicon glyphicon-list");
	} else if (element.hasClass("dateitem")) {
		$(span).addClass("glyphicon glyphicon-calendar");
	} else if (element.hasClass("timeitem")) {
		$(span).addClass("glyphicon glyphicon-time");
	} else if (element.hasClass("textitem")) {
		$(span).addClass("glyphicon glyphicon-font");
	} else if (element.hasClass("imageitem")) {
		$(span).addClass("glyphicon glyphicon-picture");
	} else if (element.hasClass("ruleritem")) {
		$(span).addClass("glyphicon glyphicon-minus");
	} else if (element.hasClass("uploaditem")) {
		$(span).addClass("glyphicon glyphicon-arrow-up");
	} else if (element.hasClass("downloaditem")) {
		$(span).addClass("glyphicon glyphicon-arrow-down");
	} else if (element.hasClass("emailitem")) {
		$(span).addClass("glyphicon glyphicon-envelope");
	} else if (element.hasClass("regexitem")) {
		$(span).addClass("glyphicon glyphicon-asterisk");
	} else if (element.hasClass("formulaitem")) {
		$(span).append('<span style="font-family: serif; font-style: italic; font-size: 13px; font-weight: bold; margin-right: 5px;">fx</span>');
	} else if (element.hasClass("galleryitem")) {
		$(span).addClass("glyphicon glyphicon-th");
	} else if (element.hasClass("confirmationitem")) {
		$(span).addClass("glyphicon glyphicon-ok");
	} else if (element.hasClass("euCountries")) {
		$(span).addClass("glyphicon glyphicon-globe");
	} else if (element.hasClass("euLanguages")) {
		$(span).addClass("glyphicon glyphicon-flag");
	} else if (element.hasClass("euDGs")) {
		$(span).addClass("glyphicon glyphicon-home");
	} 
	return span;
}

function getDeleteElementRow(element, addignorebutton)
{

	element = $(element)

	let title = element.find("textarea[name^='text']").first().text();
	let id = element.attr("id");

	let parent = element.is(".answertext, .rankingitemtext, .ratingquestion") ? _elements[element.closest(".survey-element").attr("data-id")] : null;


	let isChoiceAnswer = parent != null && (parent.type === "MultipleChoiceQuestion" || parent.type === "SingleChoiceQuestion")
	let isRankingAnswer = parent != null && parent.type === "RankingQuestion"
	let isRatingQuestion = parent != null && parent.type === "RatingQuestion"
	
	if (element.hasClass("table-header"))
	{
		title = element.find("textarea").first().text();
	}
	
	if (element.hasClass("matrix-header"))
	{
		title = $("textarea[name^='text" + id + "']").first().text();
	}

	if (isChoiceAnswer || isRankingAnswer || isRatingQuestion){
		title = element.text().trim()
	}
	
	if (title.length == 0)
	{
		title = element.find("input[name^='type']").first().val();
	}
	
	if (typeof title == 'undefined')
	{
		return;
	}
	
	var icon = getIcon(element);
	var tr = document.createElement("tr");
	var td = document.createElement("td");
	$(td).addClass("deleteicontd").append(icon);				
	$(tr).addClass("deletetr").append(td);
	td = document.createElement("td");
	$(td).addClass("deleteinfotd");
	
	if (element.hasClass("sectionitem")) {
		$(td).append("<b>" + sectionTitleLabel + "</b>:");
	} else if (element.hasClass("textitem")) {
		$(td).append("<b>" + alternativeTextLabel + "</b>:");
	} else if (element.hasClass("imageitem")) {
		$(td).append("<b>" + alternativeTextLabel + "</b>:");
	} else if (element.hasClass("ruleritem")) {
		$(td).append("<b>" + alternativeTextLabel + "</b>:");
	} else if (element.hasClass("uploaditem")) {
		$(td).append("<b>" + alternativeTextLabel + "</b>:");
	} else if (element.hasClass("downloaditem")) {
		$(td).append("<b>" + alternativeTextLabel + "</b>:");
	} else {
		$(td).append("<b>" + questionTextLabel + "</b>:");
	}
		
	if (title.length > 50)
	{
		var titlediv = document.createElement("div");
		$(titlediv).addClass("titlediv").attr("style", "display: none").append(title)
		$(td).append(titlediv);
		
		var title2 = document.createElement("div");
		title = title.substr(0,50) + "...";
		$(title2).addClass("titledivsmall").append(title)
		
		$(td).hover(
			function() {
				$(this).find(".titledivsmall").hide();
				$(this).find(".titlediv").show();
			}, function() {
				$(this).find(".titlediv").hide();
				$(this).find(".titledivsmall").show();
			}		
		);
		
		$(td).append(title2);
	} else {
		$(td).append("&nbsp;" + title);
	}		
	
	var stop = false;
	if (element.hasClass("matrix-header") || element.hasClass("table-header"))
	{
		var isAnswer = element.closest("thead").length > 0 || (element.hasClass("table-header") &&  element.closest("tr").index() == 0);
		if (isAnswer)
		{
			var remainingAnswers = element.closest("tr").find("td").length - 1 - element.closest("tr").find("td.selectedquestion").length;
			if (remainingAnswers < 1)
			{
				stop = true;
				element.removeClass("selectedquestion");
			}
		} else {
			var remainingQuestions = 0;
			element.closest("tbody").find("tr").each(function(index){
				if (element.hasClass("matrix-header") || index > 0)
				{				
					if (!$(this).find("th").first().hasClass("selectedquestion"))
					{
						remainingQuestions++;
					}
				}
			});
			if (remainingQuestions < 1)
			{
				stop = true;
				element.removeClass("selectedquestion");
			}
		}
		if (stop)
		{
			if (element.hasClass("matrix-header"))
			{
				$(td).append("<div class='validation-error'>" + getPropertyLabel("invalidMatrixChildren") + "</div>");
			} else {
				$(td).append("<div class='validation-error'>" + getPropertyLabel("invalidTableChildren") + "</div>");
			}
		}			
	} else if (isChoiceAnswer){
		if (parent.possibleAnswers().length <= 2) {
			element.removeClass("selectedquestion");
			stop = true;
			$(td).append("<div class='validation-error'>" + getPropertyLabel("invalidPaDeletion") + "</div>");
		}
	} else if (isRankingAnswer){
		if (parent.rankingItems().length <= 2){
			element.removeClass("selectedquestion");
			stop = true;
			$(td).append("<div class='validation-error'>" + getPropertyLabel("invalidRankItemDeletion") + "</div>");
		}
	} else if (isRatingQuestion){
		if (parent.childElements().length <= 1){
			element.removeClass("selectedquestion");
			stop = true;
			$(td).append("<div class='validation-error'>" + getPropertyLabel("invalidRatingDeletion") + "</div>");
		}
	}
	
	if (addignorebutton)
	{	
		if (!stop)
		{
			var a = document.createElement("a");
			$(a).addClass("btn btn-default").css("float","right").css("margin", "5px").html(ignoreLabel);
			
			$(a).click(function(){
				$(".selectedquestion[data-id=" + id + "]").removeClass("selectedquestion");
				$(this).closest("tr").remove();
				
				var count = parseInt($("#confirm-delete-multiple-count").find("span").first().text());
				$("#confirm-delete-multiple-count").find("span").first().text(--count);					
			});
			
			$(td).append(a);
		}
	}
	
	var infodiv = document.createElement("div");
	var type = getElementType(element);
	$(infodiv).append(typeLabel + ":&nbsp;" + type).addClass("deletetypediv");
	$(td).append(infodiv);
	
	$(tr).append(td);
	return tr;
}

function setContentMargin()
{
	var margin = 0; 
	if ($(".navigation").first().is(":visible"))
	{
		margin += 180;
	}
	
	if ($(".toolbox").first().is(":visible"))
	{
		margin += 180;
	}
	
	var width = $(window).width();
	var propertieswidth = $(".properties").width();
	
	if (width > margin + propertieswidth + 1000)
	{
		$("#content").css("margin-left", "auto");
	} else {
		$("#content").css("margin-left", margin + "px");
	}
	
	var left = $("#content").css("margin-left").replace("px","");
	var l = parseInt(left) + 25;
	$("#empty-content-message").css("left", l + "px");
}

function round(value)
{
	var d = parseFloat(value);
	var s = d.toString();
	if (endsWith(s, ".0")) return s.replace(".0","");
	return s;
}

// dummy methods for the question renderer

function getValidationMessageByQuestion(uniqueId)
{
	return "";
}

function getTableAnswer(uniqueId, row, col)
{
	return "";
}

var triggers = null;
function getTriggersByQuestion(uniqueId)
{
	return typeof triggers[uniqueId] != 'undefined' ? triggers[uniqueId] : "";
}

function getFileAnswer()
{
	return [];
}

function getValueByQuestion(s)
{
	return "";
}

function getValueByQuestionGallery(s)
{
	return "";
}

function getPAByQuestion(a,b,c)
{
	return "";
}

function getPAByQuestion2(a,b,c)
{
	return "";
}

function getPAByQuestion3(a)
{
	return "";
}

function getPAByQuestionCheckBox(a,b,c)
{
	return "";
}

function getTargetDatasetByQuestion(a)
{
	return "";
}

function checkSingleClick(t)
{}

function singleClick(t)
{}

function checkDependenciesAsync()
{}

function countChar(s)
{}

function isInvisible(uniqueId)
{ return false; }


var internalClose = false;
function saveForm(close)
{
	if (!_actions.SaveEnabled()) return;
	
	if ($("#regform").val() == "true")
	{
		var namefound = false;
		var emailfound = false;
		
		$(".survey-element").each(function(){
			
			if ($(this).find("input[name^='nameattribute']").length > 0)
			{
				if ($(this).find("input[name^='nameattribute']").first().val().toLowerCase() == 'name')
				{
					namefound = true;
					if ($(this).find("input[name^='optional']").first().val() != 'false')
					{
						namefound = false;
					} else if ($(this).find("input[name^='attribute']").first().val() != 'true')
					{
						namefound = false;
					}						
				} else if ($(this).find("input[name^='nameattribute']").first().val().toLowerCase() == 'email')
				{
					emailfound = true;
					if ($(this).find("input[name^='optional']").first().val() != 'false')
					{
						emailfound = false;
					} else if ($(this).find("input[name^='attribute']").first().val() != 'true')
					{
						emailfound = false;
					}						
				}
			}
		});
		
		if (!(namefound && emailfound))
		{
			internalClose = close;
			$("#invalid-regform-dialog").modal('show');
			return;
		}
	}
	
	if (isSelfAssessment) {
		let targetDatasetSelectionFound = false;
		let saquestionFound = false;
		let problemFound = false;
		
		// check that there is only one target dataset selection
		$(".survey-element").each(function(){
			if ($(this).hasClass("targetdatasetquestion")) {
				if (targetDatasetSelectionFound) {
					$("#multipletargetdatasetselectionsdialog").modal('show');
					problemFound = true;
					return;
				}
				if (saquestionFound) {
					$("#targetdatasetselectionaftersaquestiondialog").modal('show');
					problemFound = true;
					return;
				}
				targetDatasetSelectionFound = true;
			}
			if ($(this).hasClass("saquestion")) {
				saquestionFound = true;
			}
		});
		
		// check that there is no SA single choice question before a target dataset selection
		
		if (problemFound) return;
	}

	let matrixQuestionsWithError = [];
	let lastMatrixWithError;
	let surveyElements = Array.from($(".matrixitem"));

	for (let i = 0; i < surveyElements.length; i++) {
		if ($(surveyElements[i]).hasClass("survey-element") && $(surveyElements[i]).find(".interdependent").length > 0) {
			if ($(surveyElements[i]).find(".matrix-answer").length < $(surveyElements[i]).find(".matrix-question").length) {
				matrixQuestionsWithError.push(strip_tags($(surveyElements[i]).find("textarea[name^='text']").first().text()));
				lastMatrixWithError = surveyElements[i];
			}
		}
	}

	if (matrixQuestionsWithError.length > 0) {
		let listOfMatrixQuestions = "<ul>";
		for (let i = 0; i < matrixQuestionsWithError.length; i++) {
			if (i == 4) {
				listOfMatrixQuestions = `${listOfMatrixQuestions} <li> ${severalMore.replace("{0}", (matrixQuestionsWithError.length - 4).toString())} </li>`;
				break;
			}
			listOfMatrixQuestions = `${listOfMatrixQuestions} <li> ${matrixQuestionsWithError[i]} </li>`;
		}
		listOfMatrixQuestions = `${listOfMatrixQuestions} </ul>`;

		if (lastMatrixWithError != null) {
			//show properties of and focus on element with interdependency error
			_elementProperties.showProperties($(lastMatrixWithError), null, false);
			$(".selectedquestion").removeClass("selectedquestion");
			$(lastMatrixWithError).addClass("selectedquestion");
			$(".navigationitem[data-id=" + $(lastMatrixWithError).attr("data-id") + "]").addClass("selectedquestion");
		}

		showError(errorInterdependencyMatrix.replace("{0}", listOfMatrixQuestions));
		return;
	}
	
	internalSave(close);
}

function internalSave(close)
{
	_actions.backup();
	
	var ids = "";
	$('#content').children().each(function() { 
		var id = $(this).attr("id");
		ids += id + ";";	
	});
	$("#elements").val(ids);
	
	if (close)
	{
		$("#stay").val("false");
	} else {
		$("#stay").val("true");
	}
	
	//matrices
	$("#content").find('.matrixitem').each(function(index) { 
		var id = $(this).attr("id");
		ids = "";
		
		var matrix = $(this).find(".matrixtable").first();
		
		input = document.createElement("input");
		var rows = $(matrix).find("tr").length;
		$(input).attr("type", "hidden").attr("name", "matrixrows" + id).val(rows);
		$(this).append(input);
		
		input = document.createElement("input");
		var cols = $(matrix).find("tr").first().find("td, th").length;
		$(input).attr("type", "hidden").attr("name", "matrixcols" + id).val(cols);
		$(this).append(input);
		
		var widths = "";
		var questioncounter = 0;
		for (var r = 0; r < rows; r++)
		{
			for (var c = 0; c < cols; c++)
			{
				var row = $(matrix).find("tr")[r];
				var cell = $(row).find("td, th")[c];
				
				if (r == 0)
				{
					widths += $(cell).css("width").replace("px","") + ";";
				}
				
				if (r == 0 || c == 0)
				{
					if ((r == 0 && c > 0) && $(cell).hasClass('matrix-header'))
					{
						//answer
						ids += $(cell).attr("id") + ";";
					} else if ((r > 0 && c == 0) && $(cell).hasClass('matrix-header'))
					{
						//question
						var questionid = $(this).find(".hiddenmatrixquestions").find("div[pos=" + questioncounter + "]").first().attr("data-id");
						questioncounter++;
						ids += questionid + ";";
					} else {
						ids += "null;";
					};
				};
			};
		}
		
		var input = document.createElement("input");
		$(input).attr("type", "hidden").attr("name", "matrixelements" + id).val(ids);
		$(this).append(input);
		
		$(this).find("input[name^='widths']").val(widths);
	});
		
	//tables
	$("#content").find('.mytableitem').each(function(index) { 
		var id = $(this).attr("id");
		ids = "";
		var shortnames = "";
		var uids = "";
		var optionals = "";
		
		var table = $(this).find(".tabletable").first();
		
		input = document.createElement("input");
		var rows = $(table).find("tr").length;
		$(input).attr("type", "hidden").attr("name", "rows" + id).val(rows);
		$(this).append(input);
		
		input = document.createElement("input");
		var cols = $(table).find("tr").first().find("td, th").length;
		$(input).attr("type", "hidden").attr("name", "columns" + id).val(cols);
		$(this).append(input);
		
		$(table).find("tr").first().find("td, th").each(function(index){
			if ($(this).find(".ui-resizable-handle").length > 0)
			$(this).resizable( "destroy" );								
		});
		
		var widths = "";
		
		for (var r = 0; r < rows; r++)
		{
			for (var c = 0; c < cols; c++)
			{
				var row = $(table).find("tr")[r];
				var cell = $(row).find("td, th")[c];
				
				if (r == 0)
				{
					widths += $(cell).css("width").replace("px","") + ";";
				}
				
				if (r == 0 && c == 0)
				{
					ids += "null;";
				} else if (r == 0 || c == 0)
				{
					var id2 = getNewId();
					
					if ($(cell).attr("id"))
					{
						id2 = $(cell).attr("id");
					}
					
					var input2 = document.createElement("input");
					
					var title = $(cell).html();
					if ($(cell).find("textarea").length > 0)
					{
						title = $(cell).find("textarea").first().text();
					}
					
					$(input2).attr("type", "hidden").attr("name", "text" + id2).val(title);
					$(this).append(input2);
					ids += id2 + ";";
					
					var shortname = "";
					if ($(cell).attr("data-shortname"))
					{
						shortname = $(cell).attr("data-shortname");
					}
					
					var optional = "";
					if ($(cell).attr("data-optional"))
					{
						optional = $(cell).attr("data-optional");
					}
					
					var uid = "";
					if ($(cell).attr("data-uid"))
					{
						uid = $(cell).attr("data-uid");
					}
					
					input2 = document.createElement("input");
					$(input2).attr("type", "hidden").attr("name", "shortname" + id2).val(shortname);
					$(this).append(input2);
					shortnames += shortname + ";";
					
					input2 = document.createElement("input");
					$(input2).attr("type", "hidden").attr("name", "optional" + id2).val(optional);
					$(this).append(input2);
					optionals += optional + ";";
					
					input2 = document.createElement("input");
					$(input2).attr("type", "hidden").attr("name", "uid" + id2).val(uid);
					$(this).append(input2);
					uids += uid + ";";
					
				};
			};
		}
		
		var input = document.createElement("input");
		$(input).attr("type", "hidden").attr("name", "tableelements" + id).val(ids);
		$(this).append(input);

		$(this).find("input[name^='widths']").val(widths);
	});	
	
	if (localStorage != null)
	{
		localStorage.setItem("shownavigationbutton", $("#shownavigationbutton").hasClass("selected"));
		localStorage.setItem("showtoolboxbutton", $("#showtoolboxbutton").hasClass("selected"));
		localStorage.setItem("showpropertiesbutton", $("#showpropertiesbutton").hasClass("selected"));
		localStorage.setItem("dependenciesButton", $("#dependenciesButton").hasClass("selected"));
		localStorage.setItem("backupButton", $("#backupButton").hasClass("selected"));
		localStorage.setItem("multiselectButton", $("#multiselectButton").hasClass("selected"));
	}		
	
	$("#busydialog").modal('show');
	
	$("#frmEdit").submit();
}

function updateDependenciesView()
{
	$(".glyphicon-arrow-down.red").remove();
	$(".glyphicon-arrow-up.black").remove();
	$(".matrix-cell").removeAttr("style");
	
	if (!$("#dependenciesButton").hasClass("selected")) return;
	
	$("input[name^=dependencies]").filter(function() { return $(this).val(); }).each(function(){
		var dependencies = $(this).val();
		var div = document.createElement("div");
		$(div).addClass("rightaligned glyphicon glyphicon-arrow-down red").hover(function(){
			var ids = dependencies.split(";");
			for (var i = 0; i < ids.length; i++)
			{
				if (ids[i].length > 0)
				{
					$("#content").find("#" + ids[i]).addClass("highlighted");
					$(".navquestion[data-id=" + ids[i] + "]").addClass("highlighted");
					$(".navigationitemsection[data-id=" + ids[i] + "]").addClass("highlighted");
				}
			}
		}, function(){
			var ids = dependencies.split(";");
			for (var i = 0; i < ids.length; i++)
			{
				if (ids[i].length > 0)
				{
					$("#content").find("#" + ids[i]).removeClass("highlighted");
					$(".navquestion[data-id=" + ids[i] + "]").removeClass("highlighted");
					$(".navigationitemsection[data-id=" + ids[i] + "]").removeClass("highlighted");
				}
			}
		});
		
		var id = $(this).attr("data-id");
		
		$(".answertext[data-id='" + id + "']").first().prepend(div);
		var cellid = $(this).attr("data-qaid");
		$(this).closest("li").find("input[data-cellid='" + cellid + "']").parent().css("padding-left","23px").prepend(div);
	});
	
	$(".survey-element[data-triggers], .matrix-question[data-triggers]").each(function(){
		var dependencies = $(this).attr("data-triggers");
		if (dependencies.replace(";","").trim().length > 0)
		{
			var div = document.createElement("div");
			var ids = dependencies.split(";");
			
			$(div).addClass("leftaligned padright10 glyphicon glyphicon-arrow-up black").hover(function(){
				for (var i = 0; i < ids.length; i++)
				{
					if (ids[i].length > 0)
					{
						if (ids[i].indexOf("|") == -1)
						{						
							$("label[for='" + ids[i] + "']").parent().addClass("highlighted");
							$("li[data-id='" + ids[i] + "']").addClass("highlighted");
							$("#navanswer" + ids[i]).addClass("highlighted");
						} else {
							$("input[data-cellid='" + ids[i] + "']").parent().addClass("highlighted");
							$(".navigationitem[data-cellid='" + ids[i] + "']").addClass("highlighted");
						}
					}
				}
			}, function(){
				for (var i = 0; i < ids.length; i++)
				{
					if (ids[i].length > 0)
					{
						if (ids[i].indexOf("|") == -1)
						{
							$("label[for='" + ids[i] + "']").parent().removeClass("highlighted");
							$("li[data-id='" + ids[i] + "']").removeClass("highlighted");
							$("#navanswer" + ids[i]).removeClass("highlighted");
						} else {
							$("input[data-cellid='" + ids[i] + "']").parent().removeClass("highlighted");
							$(".navigationitem[data-cellid='" + ids[i] + "']").removeClass("highlighted");
						}
					}
				}
			});
			if ($(this).hasClass("matrix-question"))
			{
				$(this).find("td").first().prepend(div);
			} else {
				$(this).prepend(div);
			}
		}
	});		
}

function updateTitles()
{
	if (automaticNumbering)
	{
		$("#editcontent").find(".sectionitem").each(function(){
			$(this).find(".sectiontitle").first().html(getSectionTitle(this, false));
		});
		
		$("#editcontent").find(".survey-element").each(function(){
			if (!$(this).hasClass("sectionitem") && !$(this).hasClass("galleryitem") && !$(this).hasClass("imageitem") && !$(this).hasClass("ruleritem"))
			{
				$(this).find(".questiontitle").first().html(getQuestionTitle(this, false));
			}
		});
	}
}

function getQuestionTitle(question, dep)
{		
	if ($(question).hasClass("sectionitem"))
	{
		return getSectionTitle(question, dep);
	}
	
	var id =  $(question).attr("id");
	var title = $(question).find("textarea[name^='text']").first().text();
	var titleprefix = "";
	
	if ($(question).hasClass("confirmationitem"))
	{
		titleprefix = "<input type='checkbox' disabled='disabled' class='check' />";
	}
	
	if (dep) {
		var title2 = jQuery("<div>" + title + "</div>").text();
		if (title2 != undefined && title2.length > 0)
		{
			title = title2;
		}
	}
	
	var shortname = $(question).find("input[name^='shortname']").first().val();
	var optional = $(question).find("input[name^='optional']").length == 0 || $(question).find("input[name^='optional']").first().val() == "true";
		
	if ($('#questionNumbering').val() == 0){
		if (title.indexOf("<p") == 0)
		{
			var index = title.indexOf(">");				
			return title.substring(0, index+1) + titleprefix + " " + title.substring(index+1);
		} else {
			return titleprefix + title;
		}
	}
			
	var lastSection = null;
	var counter = 0;
	var first = true;	
	
	$("#editcontent").find(".survey-element").each(function(){
		if ($(this).hasClass("sectionitem"))
		{
			lastSection = this;
			if ($('#questionNumbering').val() < 4){
				counter = 0;
			}
			first = false;
		} else {
			if (!$(this).hasClass("textitem") && !$(this).hasClass("imageitem") && !$(this).hasClass("ruleritem") && !$(this).hasClass("confirmationitem"))
			{
				counter++;
				var qid = $(this).attr("id");
				first = false;
				if (qid == id)
				{
					var result = "";
					
					if ($('#questionNumbering').val() < 4){
						result = getSectionNumbering(lastSection);
						if (result.length > 0) result += ".";
					}
					
					switch (parseInt($('#questionNumbering').val()))
					{
						case 1:
							titleprefix += "<span class='numbering'>" + result + counter + "</span>";
							break;
						case 2:
							titleprefix += "<span class='numbering'>" + result +  getSmallLetter(counter) + "</span>";
							break;
						case 3:
							titleprefix += "<span class='numbering'>" + result +  getBigLetter(counter) + "</span>";
							break;
						case 4:
							titleprefix += "<span class='numbering'>" + result + counter + "</span>";
							break;
						case 5:
							titleprefix += "<span class='numbering'>" + result +  getSmallLetter(counter) + "</span>";
							break;
						case 6:
							titleprefix += "<span class='numbering'>" + result +  getBigLetter(counter) + "</span>";
							break;
					}					
				}			
			}
		}				
		
	});
	
	if (title.indexOf("<p") == 0)
	{
		var index = title.indexOf(">");				
		return title.substring(0, index+1) + titleprefix + " " + title.substring(index+1);
	} else {
		return titleprefix + title;
	}
}

function getElementLevel(question)
{		
	if ($(question).hasClass("section"))
	{
		return parseInt($(question).find("input[name^='level']").val()) - 1;
	}
	
	var id =  $(question).find(".elementtitle").attr("id");
						
	var currentSectionLevel = 0;
	
	$(".content-element").each(function(){
		if ($(this).hasClass("section"))
		{
			currentSectionLevel = parseInt($(this).find("input[name^='level']").val());
		} else {
			var qid = $(this).find(".elementtitle").attr("id");
			if (qid == id)
			{
				return false;					
			}			
			
		}				
		
	});
	
	return currentSectionLevel;
}

function getSectionTitle(section, dep)
{
	var title = $(section).find("textarea[name^='text']").first().text();
	
	if (dep) {
		var title2 =  jQuery("<div>" + title + "</div>").text();
		if (title2 != undefined && title2.length > 0)
		{
			title = title2;
		}
	}
	
	var numbering =  getSectionNumbering(section);
	
	if (numbering.length > 0)
	{
		if (title.indexOf("<p") == 0)
		{
			var index = title.indexOf(">");				
			return title.substring(0, index+1) + "<span class='numbering'>" + numbering + "</span>" + title.substring(index+1);
		} else {
			return "<span class='numbering'>" + numbering + "</span>" + title;
		}
	} else {
		return title;
	}
}

function getSectionNumbering(section)
{
	var sid =  $(section).attr("id");
	var slevel = parseInt($(section).find("input[name^='level']").val());
	
	if (section == null || $('#sectionNumbering').val() == 0) return "";
	
	var result = "";
	var n1 = 0;
	var n2 = 0;
	var n3 = 0;
	var n4 = 0;
	var n5 = 0;
	
	$("#content").find(".sectionitem").each(function(){
		var level = parseInt($(this).find("input[name^='level']").val());
		var id =  $(this).attr("id");
		
		if (level <= slevel)
		{
			//if section is deeper than s
			switch(level)
			{
				case 1:
					n1++;
					n2 = 0;
					n3 = 0;
					n4 = 0;
					n5 = 0;
					break;
				case 2:
					n2++;
					n3 = 0;
					n4 = 0;
					n5 = 0;
					break;
				case 3:
					n3++;
					n4 = 0;
					n5 = 0;
					break;
				case 4:
					n4++;
					n5 = 0;
					break;
				case 5:
					n5++;
					break;
			}
		} 
						
		if (id == sid)
		{
			result = getCounter(slevel, n1, n2, n3, n4, n5);
		}	
	});
	
	return result;
}

function getCounter(level, n1, n2, n3, n4, n5)
{
	var type = parseInt($('#sectionNumbering').val());
	var result = "";
	
	if (type == 1)
	{
		result += n1;
		
		if (level > 1)
		{
			if (n2 == 0) n2 = 1;
			result += "." + n2;
		}
		
		if (level > 2)
		{
			if (n3 == 0) n3 = 1;
			result += "." + n3;
		}
		
		if (level > 3)
		{
			if (n4 == 0) n4 = 1;
			result += "." + n4;
		}
		
		if (level > 4)
		{
			if (n5 == 0) n5 = 1;
			result += "." + n5;
		}
	}
	
	if (type == 2)
	{
		result += getSmallLetter(n1);
		
		if (level > 1)
		{
			result += "." + getSmallLetter(n2);
		}
		
		if (level > 2)
		{
			result += "." + getSmallLetter(n3);
		}
		
		if (level > 3)
		{
			result += "." + getSmallLetter(n4);
		}
		
		if (level > 4)
		{
			result += "." + getSmallLetter(n5);
		}		
		
	}
	
	if (type == 3)
	{
		result += getBigLetter(n1);
		
		if (level > 1)
		{
			result += "." + getBigLetter(n2);
		}
		
		if (level > 2)
		{
			result += "." + getBigLetter(n3);
		}
		
		if (level > 3)
		{
			result += "." + getBigLetter(n4);
		}
		
		if (level > 4)
		{
			result += "." + getBigLetter(n5);
		}		
	}	
	
	return result;
}

const SurveyRuleEvaluator = {

	isElementAllowed(element){

		if (!isEVote)
			return true

		const classes = $(element).prop("classList")
		const itemClass = Array.from(classes).find(name => name.includes("item"))
		const toolboxItem = $(".toolboxitem." + itemClass)
		return toolboxItem.length > 0 && !toolboxItem.is(".disallowed")
	},

	recalculate(){
		const toolbox = $(".toolbox");
		toolbox.find(".disallowed").removeClass("disallowed")

		if (isEVote) {
			this.evaluateRule(eVoteTemplate)
		}
		
		if (isSelfAssessment) {
			this.evaluateSARule()
		}
	},

	disallowElement(elType){
		$(".toolboxitem." + elType).addClass("disallowed")
	},

	evaluateRule(template){
		if ($(".survey-element.singlechoiceitem").length > 0) {
			this.disallowElement("singlechoiceitem")
		}
	},
	
	evaluateSARule(){
		if ($(".survey-element.targetdatasetquestion").length > 0) {
			this.disallowElement("targetdatasetitem ")
		}
	}
}