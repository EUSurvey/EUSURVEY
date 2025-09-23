function toggleMandatory(checkbox)
{
	var element = getElement();
	var checked = $(checkbox).is(":checked");
	var text = checked ? "true" : "false";
	var oldtext = checked ? "false" : "true";
	setMandatoryInner(element, checked, checkbox);
	_undoProcessor.addUndoStep(["Mandatory", element.id(), $(_elementProperties.selectedelement).index(), oldtext, text])
}
	
function setMandatoryInner(element, checked, checkbox)
	{
	var titleprefix = ""; //checked ? "<span class='mandatory'>*</span>" : "<span class='optional'>*</span>";
	
	if ($(_elementProperties.selectedelement).hasClass("matrix-header") || $(_elementProperties.selectedelement).hasClass("table-header"))
	{
		var id = $(_elementProperties.selectedelement).closest(".survey-element").attr("data-id");
		element = _elements[id];

		if ($(_elementProperties.selectedelement).hasClass("table-header") && $(_elementProperties.selectedelement).closest("tr").index() == 0)
		{
			//a table answer
		} else {
			if ($(_elementProperties.selectedelement).hasClass("matrix-header"))
			{
				removeValidationMarkup();
				var nummandatoryrows = $(_elementProperties.selectedelement).closest(".survey-element").find(".matrixtable").first().find("input[name^='optional'][value='false']").length;
				
				if (checked && !checkMandatoryMatrix(checkbox, nummandatoryrows + 1, element.maxRows()))
				{
					return false;
				}
			}			
			
			//a matrix/table question
			var id = $(_elementProperties.selectedelement).closest("tr").attr("data-id");
			
			element.getChild(id).optional(!checked);
			element.getChild(id).title(titleprefix + element.getChild(id).originalTitle());
		}	
	} else if ($(_elementProperties.selectedelement).hasClass("ratingquestion"))
	{
		var id = $(_elementProperties.selectedelement).closest(".survey-element").attr("data-id");
		element = _elements[id];
		
		var i = $(_elementProperties.selectedelement).index();
		element.childElements()[i].optional(!checked);
		element.childElements()[i].title(titleprefix + element.childElements()[i].originalTitle());
	} else if (element.type == "Matrix")
	{
		removeValidationMarkup();
		if (checked && !checkMandatoryMatrix(checkbox, element.questionsOrdered().length, element.maxRows()))
		{
			return false;
		}
		for (var i = 0; i < element.questions().length; i++)
		{
			element.questions()[i].optional(!checked);
			element.questions()[i].title(titleprefix + element.questions()[i].originalTitle());
		}
	} else if (element.type == "Table")
	{
		for (var i = 0; i < element.questions().length; i++)
		{
			element.questions()[i].optional(!checked);
			element.questions()[i].title(titleprefix + element.questions()[i].originalTitle());
		}
	} else if (element.type == "RatingQuestion")
	{
		for (var i = 0; i < element.childElements().length; i++)
		{
			element.childElements()[i].optional(!checked);
			element.childElements()[i].title(titleprefix + element.childElements()[i].originalTitle());
		}
	} else {		
		element.optional(!checked);
		element.title(titleprefix + element.originalTitle());
	}	
}

function updateConfirmationText(element, text)
{
	element.confirmationtext(text);
	element.usetext(text.trim().length > 0);
	
	$(_elementProperties.selectedelement).find(".confirmationlabel").remove();
	if (text.length > 0)
	{
		var a = document.createElement("a");
		var conflabel = element.confirmationlabel();
		$(a).addClass("confirmationlabel").html(conflabel);								
		$(_elementProperties.selectedelement).find(".questiontitle").after(a);
	}
	
}

function updateImageSize(element, size, input, noundo)
{
	var oldsize = element.scale(); 
	var imgwidth = element.width();
	element.scale(parseInt(size));
	
	if (element.scale() / 100 * element.width() > 600)
	{
		element.scale(Math.floor(600 / element.width() * 100));
		if (input != null)
		$(input).val(element.scale());
	}
	
	if (!noundo)
	_undoProcessor.addUndoStep(["Size", element.id(), $(_elementProperties.selectedelement).index(), oldsize, size]);

}

function updateMatrixSize(element, size, noundo)
{
	var type = 1;
	if (size == "fitToContent")
	{
		type = 0;
	} else if (size == "manualColumnWidth")
	{
		type = 2;
	}
	var oldtype = element.tableType();
	var oldsize;
	switch (oldtype)
	{
		case 0:
			oldsize = "fitToContent";
			break;
		case 1:
			oldsize = "fitToPage";
			break;
		case 2:
			oldsize = "manualColumnWidth";
			break;
	}
	element.tableType(type);
	
	if (!noundo)
	_undoProcessor.addUndoStep(["Size", element.id(), $(_elementProperties.selectedelement).index(), oldsize, size]);
				
	var table = $(_elementProperties.selectedelement).find(".tabletable, .matrixtable").first();
	if (type == 1)
	{
		$(table).attr("style","width: 900px");
	} else {
		$(table).attr("style","width: auto; max-width: auto");
	}
	
	$(table).find("tr").first().find("th").each(function(index){
		if ($(this).find(".ui-resizable-handle").length > 0)
		$(this).resizable( "destroy" );								
	});
	
	if (type == 2)
	{
		var widths = $(_elementProperties.selectedelement).find("input[name^='widths']").first().val();
		$(table).find("tr").first().find("th").each(function(index){
			$(this).css("width", getWidth(widths, 0));
		});
		
		$(table).find("tr").first().find("th").each(function(index){
			var cell = this;
			$(this).resizable({
				handles: "e",
				start: function ( event, ui) { $(cell).attr("data-originalwidth", $(cell).width())},
				stop: function( event, ui ) {
					_undoProcessor.addUndoStep(["CELLWIDTH", cell, $(cell).attr("data-originalwidth"), $(cell).width()]);
				} 
			});										
		});
	} else {
		$(table).find("tr").first().find("td").removeAttr("style");
	}
}

function updateShortname(input)
{
	var val = $(input).val();
	var label = $(_elementProperties.selectedproperty).find(".propertylabel").first().attr("data-label");
	var element = getElement();
	var index = $(input).closest("tr").index();

	updateShortnameInner(label, element, index, val);
}

function updateShortnameInner(label, element, index, val)
{
	if (label == "PossibleAnswers")
	{
		element.possibleAnswers()[index].shortname(val);
	} else if (label == "Columns")
	{
		element.answers()[index].shortname(val);
	} else if (label == "Rows")
	{
		element.questions()[index].shortname(val);
	} else if (label == "Questions")
	{
		element.childElements()[index].shortname(val);
	}
}

function getElement() {
	var selectedElement = $(_elementProperties.selectedelement);
	var id = selectedElement.attr("data-id");
	var element = _elements[id];
	
	if (element == null && selectedElement.hasClass("cell")) {
		let parentId = selectedElement.closest("li.complextableitem").attr("data-id");
		element = _elements[parentId].getChildbyId(id);
	}
	
	if (element == null && selectedElement.hasClass("matrix-header")) {
		let parentId = selectedElement.closest(".survey-element").attr("data-id");
		element = _elements[parentId].getChild(id);
	}
	
	return element;
}

function updateChoice(element, answers)
{
	//var element = getElement();
	
	var scorings = [];
	
	if (answers != null)
	{
		for (var i = 0; i < element.possibleAnswers().length; i++)
		{
			scorings[element.possibleAnswers()[i].id()] = element.possibleAnswers()[i].scoring;
		}
		
		element.possibleAnswers.removeAll();
		for (var i = 0; i < answers.length; i++)
		{
			if (scorings.hasOwnProperty(answers[i].id()))
			{
				answers[i].scoring =scorings[answers[i].id()];
			}
			element.possibleAnswers.push(answers[i]);
		}
	}
	
	if ($(_elementProperties.selectedelement).hasClass("cell")) {
		return;
	}
	
	ko.cleanNode($(_elementProperties.selectedelement)[0]);
	$(_elementProperties.selectedelement).empty();
	addElementToContainer(element, $(_elementProperties.selectedelement)[0], true, false);
	addElementHandler($(_elementProperties.selectedelement));
	
	updateDependenciesView();
	checkInputStates();
	var id = $(_elementProperties.selectedelement).attr("data-id");
	updateNavigation($(_elementProperties.selectedelement), id);

	var element = _elementProperties.selectedelement;
	_elementProperties.deselectAll();
	if (element != null) _elementProperties.showProperties($(element), null, false);
}

function checkInputStates()
{
	if ($(_elementProperties.selectedelement).hasClass("singlechoiceitem"))
	{
		if ($("#selectRadioButton").is(":checked"))
		{
			$(".firstpropertyrow[data-label=Columns]").find("select").removeAttr("disabled").removeClass("disabled");
		} else {
			$(".firstpropertyrow[data-label=Columns]").find("select").attr("disabled", "disabled").addClass("disabled");
		}
	} else if ($(_elementProperties.selectedelement).hasClass("multiplechoiceitem"))
	{
		if ($("#selectCheckBox").is(":checked"))
		{
			$(".firstpropertyrow[data-label=Columns]").find("select").removeAttr("disabled").removeClass("disabled");
		} else {
			$(".firstpropertyrow[data-label=Columns]").find("select").attr("disabled", "disabled").addClass("disabled");
		}
	} else if ($(_elementProperties.selectedelement).hasClass("matrixitem"))
	{
		if (!$("#selectMultipleChoice").is(":checked"))
		{
			$("#idPropertyInterdependency").removeAttr("disabled").removeClass("disabled");
		} else {
			$("#idPropertyInterdependency").attr("disabled", "disabled").addClass("disabled").removeAttr("checked");
		}
		checkInterdependentMatrix($("#idPropertyInterdependency"))
	}
	if ($("#idPropertyReadOnly").length > 0 && $("#idPropertyHidden").length > 0) {
		const readonly = $("#idPropertyReadOnly").is(":checked")
		const hiddenEl = $("#idPropertyHidden")
		if (readonly) {
			hiddenEl.removeAttr("disabled").removeClass("disabled")
		} else {
			hiddenEl.attr("disabled", "disabled").addClass("disabled").removeAttr("checked")
		}
	}
}

function checkInterdependentMatrix(input)
{
	var columnstext = getColumnsText(true);
	var columns = splitText(columnstext).length;
	var rowstext = getRowsText(true);
	var rows = splitText(rowstext).length;

	const isMultipleChoice = $("#selectMultipleChoice").is(":checked")
    const isInterdependencyChecked = $("#idPropertyInterdependency").is(":checked")

	if (columns < rows && !isMultipleChoice && isInterdependencyChecked) {
		addValidationInfo(input, "invalidInterdependencyCriteria");
		return false;
	} else {
		removeValidationMarkup($(".firstpropertyrow[data-label=Columns]"));
		removeValidationMarkup($(".firstpropertyrow[data-label=Rows]"));
		removeValidationMarkup($("#idPropertyInterdependency").closest(".firstpropertyrow"));
	}
	
	return true;
}

function updateGallery(file)
{
	var id = $(_elementProperties.selectedelement).attr("data-id");
	var element = _elements[id];
	
	if (file != null)
	{
		element.files.push(newFileViewModel(file.id, file.name,  file.comment, file.longdesc, file.cleanComment, file.width, file.desc));
	}
	
	addElementHandler($(_elementProperties.selectedelement));
}

function addVisibility(triggerid, selectedquestionid)
{
	//triggerid: the id of the trigger (e.g. a possible answer)
	//selectedquestionid: the id of the dependent element
	
	var selectedquestionelement = $("#content").find("[id=" + selectedquestionid + "]").first();
	var field;
	var trigger;
	if (triggerid.indexOf("|") > 0)
	{
		//matrixitem
		trigger = $("input[data-cellid='" + triggerid + "']").parent();
		
		var parentid = $("input[data-cellid='" + triggerid + "']").parent().closest(".survey-element").attr("data-id");
		var parent = _elements[parentid];		
		var answerindex = $(trigger).index() - 1;
		var questionid = triggerid.substring(0, triggerid.indexOf("|"));
		var questionindex = parent.getQuestionIndex(questionid); // $(trigger).closest("tr").index() - 1;
		var index = answerindex + (questionindex * (parent.columns()-1));
		
		while (parent.dependentElementsStrings().length <= index)
		{
			parent.dependentElementsStrings.push(ko.observable(""));
		}
		
		if (parent.dependentElementsStrings()[index]().indexOf(selectedquestionid) == -1)
		{
			var old = parent.dependentElementsStrings()[index]();
			parent.dependentElementsStrings()[index](old + selectedquestionid + ";");
		}
	} else {
		//add id to "dependentelements" field		
		var parentid = $("input[data-id='" + triggerid + "'][name^='dependencies']").closest(".survey-element").attr("data-id");
		var parent = _elements[parentid];
		var child = parent.getChild(triggerid);
		if (child.dependentElementsString().indexOf(selectedquestionid) == -1)
		{
			child.dependentElementsString(child.dependentElementsString()+ selectedquestionid + ";");
		}
		
		trigger = $("input[data-id='" + triggerid + "'][name^='dependencies']").closest(".survey-element");
	}
	
	//the data-triggers attribute is not bound to a viewmodel yet
	if ($(selectedquestionelement).hasClass("matrix-header"))
	{
		if ($(selectedquestionelement).parent().attr("data-triggers"))
		{
			if ($(selectedquestionelement).parent().attr("data-triggers").indexOf(triggerid) == -1)
			{
				$(selectedquestionelement).parent().attr("data-triggers",$(selectedquestionelement).parent().attr("data-triggers") + triggerid + ";");
			}
		} else {
			$(selectedquestionelement).parent().attr("data-triggers", triggerid + ";");
		}
	} else {
		if ($(selectedquestionelement).attr("data-triggers"))
		{
			if ($(selectedquestionelement).attr("data-triggers").indexOf(triggerid) == -1)
			{
				$(selectedquestionelement).attr("data-triggers",$(selectedquestionelement).attr("data-triggers") + triggerid + ";");
			}
		} else {
			$(selectedquestionelement).attr("data-triggers", triggerid + ";");
		}
	}
	
	scanQuestionDependencies(trigger);
}

function removeVisibility(triggerid, selectedquestionid)
{
	var selectedquestionelement = $("#content").find("[id=" + selectedquestionid + "]").first();
	var field;
	var trigger;
	
	if (triggerid.indexOf("|") > 0)
	{
		//matrixitem
		trigger = $("input[data-cellid='" + triggerid + "']").parent();
		
		var parentid = $("input[data-cellid='" + triggerid + "']").parent().closest(".survey-element").attr("data-id");
		var parent = _elements[parentid];		
		//var answerindex = $(trigger).index() - 1;
		//var questionindex = $(trigger).closest("tr").index();
		//var index = answerindex + (questionindex * (parent.columns()-1));
		
		var index = parseInt($(trigger).attr("data-originalposition"));
		
		while (parent.dependentElementsStrings().length <= index)
		{
			parent.dependentElementsStrings.push(ko.observable(""));
		}
		
		if (parent.dependentElementsStrings()[index]().indexOf(selectedquestionid) != -1)
		{
			var old = parent.dependentElementsStrings()[index]();
			parent.dependentElementsStrings()[index](old.replace(selectedquestionid + ";",""));
		}
	} else {
		//add id to "dependentelements" field
		var parentid = $("input[data-id='" + triggerid + "'][name^='dependencies']").closest(".survey-element").attr("data-id");
		var parent = _elements[parentid];
		var child = parent.getChild(triggerid);
		if (child.dependentElementsString().indexOf(selectedquestionid) != -1)
		{
			child.dependentElementsString(child.dependentElementsString().replace(selectedquestionid + ";",""));
		}
		
		trigger = $("input[data-id='" + triggerid + "'][name^='dependencies']").closest(".survey-element");
	}

	if ($(selectedquestionelement).attr("data-triggers"))
	{
		if ($(selectedquestionelement).attr("data-triggers").indexOf(triggerid) > -1)
		{
			$(selectedquestionelement).attr("data-triggers",$(selectedquestionelement).attr("data-triggers").replace(triggerid, "").replace(";;",";"));
		}
	} else if ($(selectedquestionelement).hasClass("matrix-header"))
	{
		if ($(selectedquestionelement).parent().attr("data-triggers"))
		{
			if ($(selectedquestionelement).parent().attr("data-triggers").indexOf(triggerid) > -1)
			{
				$(selectedquestionelement).parent().attr("data-triggers",$(selectedquestionelement).parent().attr("data-triggers").replace(triggerid, "").replace(";;",";"));
			}
		}
	}
	
	scanQuestionDependencies(trigger);
}

function updatePossibleAnswers(selectedelement, text, inundo, element)
{
	var dependenciesfound = false;
	
	var uniqueIDs = [];
	var shortnames = [];
	var dependentElementsStrings = [];
	var ids = [];
	
	var possibleAnswers;
	
	if ($(selectedelement).hasClass("cell")) {
		var id = $(selectedelement).attr("data-id");
		possibleAnswers = $('child' + id).find("input[name^='pauid']");
	} else {	
		possibleAnswers = $(selectedelement).find("input[name^='pauid']");
	}
	
	$(possibleAnswers).each(function(){		
		var parentid = $(this).attr("name").substring(5);
		var id = $(this).attr("data-id");
		
		var title = $("textarea[name='answer" + parentid + "'][data-id='" + id + "']").text();
		var uniqueId = $("input[name='pauid" + parentid + "'][data-id='" + id + "']").first().val();
		var shortname = $("input[name='pashortname" + parentid + "'][data-id='" + id + "']").first().val();
		var dependentElementsString = $("input[name='dependencies" + parentid + "'][data-id='" + id + "']").first().val();
		
		uniqueIDs[title] = uniqueId;
		shortnames[title] = shortname;
		dependentElementsStrings[title] = dependentElementsString;
		
		if (dependentElementsString.length > 0) dependenciesfound = true;
		
		ids[title] = id;
	});
	
	var oldtext = getCombinedAnswerText(true);
	text = text.replaceAll("<BR />","<br />").replaceAll("<BR>","<br />").replaceAll("<br>","<br />").replaceAll("<div", "<p><div").replaceAll("</div>", "</div></p>");
	
	if (!checkText(text))
	{
		return false;
	}
	
	var lines = text.split("</p>");
	for (var i = 0; i < lines.length; i++)
	{
		lines[i] = lines[i].trim();
		if (lines[i].indexOf("<p>") == 0)
		{
			lines[i] = lines[i].replace("<p>","").trim();
		} else if (lines[i].indexOf("<p") == 0) {
			var content = lines[i].substring(lines[i].indexOf(">")+1);
			if (content.trim().length > 0)
			{
				lines[i] = lines[i] + "</p>";
			} else {
				lines[i] = "";
			}			
		}
	}
	
	if (!checkPossibleAnswers(lines))
	{
		return false;
	}
	
	var answers = [];
	
	for (var i = 0; i < lines.length; i++)
	{
		if (lines[i].trim().length > 0)
		{
			var id = getNewId();
			if (ids.hasOwnProperty(lines[i]))
			{
				id = ids[lines[i]];
			}
			
			var pa = newPossibleAnswerViewModel(id, uniqueIDs[lines[i]], shortnames[lines[i]], dependentElementsStrings[lines[i]], lines[i], false);
				
			if (!shortnames.hasOwnProperty(lines[i]))
			{
				pa.shortname(getNewShortname());
			}
			
			if (!dependentElementsStrings.hasOwnProperty(lines[i]))
			{
				pa.dependentElementsString("");
			}
			
			answers.push(pa);
		}
	}
	
	for (var title in ids) {
	    if (ids.hasOwnProperty(title)) {
	        if ($.inArray(title, lines) == -1)
	        {	        	
	        	$(".survey-element[data-triggers], .matrix-question[data-triggers]").each(function(){
	        		var dependencies = $(this).attr("data-triggers");
	        		
	        		if (dependencies.indexOf(ids[title]) > -1)
	        		{
	        			dependencies = dependencies.replace(ids[title] + ";","");
	        			$(this).attr("data-triggers", dependencies);
	        		}	        		
	        	});
	        }
	    }
	}

	updateChoice(element, answers);

	if (!inundo)
	{
		_undoProcessor.addUndoStep(["PossibleAnswers", $(selectedelement).attr("id"), $(selectedelement).index(), oldtext, text]);
		if (dependenciesfound) showInfo(getPropertyLabel("checkVisibilities"));
	}
	return true;
}

function updateText(selectedelement, text, fromundo)
{
	
	var isgalleryimage = $(_elementProperties.selectedelement).find(".gallery-image").length > 0 && $(_elementProperties.selectedelement).closest(".gallery-div").length > 0;
	var isfirstcell = $(_elementProperties.selectedelement).hasClass("firstCell");
	var isComplexTable = $(_elementProperties.selectedelement).hasClass("complextableitem") || $(_elementProperties.selectedelement).hasClass("cell");
	
	if (!fromundo)
	if (!isgalleryimage && !isfirstcell && !isComplexTable)
	if (!checkText(text))
	{
		return false;
	}
	var oldtext;
	
	var id = $(_elementProperties.selectedelement).attr("data-id");
	var uid = "";
	var element = getElement();
	var selectedelement = $(_elementProperties.selectedelement);
	var selectedid = id;
	
	if (text.indexOf("<p>") == 0 && text.lastIndexOf("<p>") == 0 && endsWith(text, "</p>"))
	{
		text = text.substring(3, text.length-4);
	}
	
	if ($(_elementProperties.selectedelement).hasClass("ratingquestion"))
	{
		var parentid = $(_elementProperties.selectedelement).closest(".survey-element").attr("data-id");
		var parent = _elements[parentid];
		var child = parent.getChild(id);
		oldtext = child.title();
		child.title(text);
		child.originalTitle(text);
	} else	if ($(_elementProperties.selectedelement).find(".questiontitle").length > 0)
	{
		oldtext = element.originalTitle();
		element.originalTitle(text);
		
		if (element.type === 'ComplexTableItem') {
			element.title(text);
			selectedelement = $(_elementProperties.selectedelement).closest(".survey-element"); //needed tp update navigation
			selectedid = selectedelement.attr("data-id");
		} else {
			element.title(getQuestionTitle($(_elementProperties.selectedelement), false));
		}		
		
	} else if ($(_elementProperties.selectedelement).find(".sectiontitle").length > 0)
	{		
		oldtext = element.originalTitle();
		element.originalTitle(text);
		element.title(getSectionTitle($(_elementProperties.selectedelement), false));
	} else if ($(_elementProperties.selectedelement).hasClass("matrix-header") || $(_elementProperties.selectedelement).hasClass("table-header"))
	{	
		var parentid = $(_elementProperties.selectedelement).closest(".survey-element").attr("data-id");
		var parent = _elements[parentid];
		selectedelement = $(_elementProperties.selectedelement).closest(".survey-element");
		selectedid = parentid;
		
		if ($(_elementProperties.selectedelement).closest("tr").index() == 0 && !$(_elementProperties.selectedelement).closest("tr").hasClass("matrix-question"))
		{
			//a matrix answer
			var index = $(_elementProperties.selectedelement).index() - 1;
			
			if (index == -1) {
				oldtext = parent.firstCellText();
				parent.firstCellText(text);
			} else {			
				oldtext = parent.answers()[index].originalTitle();
				parent.answers()[index].title(text);
				parent.answers()[index].originalTitle(text);
			}
		} else {
			//a matrix question
			var index = $(_elementProperties.selectedelement).closest("tr").index() - 1;
			if ($(_elementProperties.selectedelement).hasClass("matrix-header"))
			{
				var matrixquestion = parent.getChild(id); // parent.questionsOrdered()[index];
				oldtext = matrixquestion.originalTitle();
				matrixquestion.title(text);
				matrixquestion.originalTitle(text);
			} else {
				var tablequestion = parent.questions()[index];
				oldtext = parent.questions()[index].originalTitle();
				tablequestion.title(text);
				tablequestion.originalTitle(text);
			}
		}
	} else if ($(_elementProperties.selectedelement).hasClass("textitem"))
	{
		oldtext = element.originalTitle(); // $(_elementProperties.selectedelement).find("textarea[name^='text']").first().text();
		element.originalTitle(text);
		element.title(text);
	} else if ($(_elementProperties.selectedelement).hasClass("answertext"))
	{		
		var parentid = $(_elementProperties.selectedelement).closest(".survey-element").attr("data-id");
		var parent = _elements[parentid];
		selectedelement = $(_elementProperties.selectedelement).closest(".survey-element");
		selectedid = parentid;
		
		oldtext = parent.getChild(id).title();	
		
		if (oldtext != text)
		{
			if (!checkPossibleAnswer(text))
			{
				return false;
			}		
			
			parent.getChild(id).title(text);
		}
		
		updateDependenciesView();
	} else if ($(_elementProperties.selectedelement).find(".gallery-image").length > 0)
	{
		uid = $(_elementProperties.selectedelement).attr("data-uid");
		oldtext = $(_elementProperties.selectedelement).find("textarea[name^='comment']").first().text();
		
		var parentid = $(_elementProperties.selectedelement).closest(".survey-element").attr("data-id");
		var parent = _elements[parentid];
		parent.getChild(uid).comment(text);
	} else if ($(_elementProperties.selectedelement).hasClass("rankingitemtext"))
	{
		var parentid = $(_elementProperties.selectedelement).closest(".survey-element").attr("data-id");
		var parent = _elements[parentid];
		var rankingitem = parent.rankingItems().filter(item => item.id() == id)[0];
		oldtext = rankingitem.title();
		rankingitem.title(text);
		uid = rankingitem.uniqueId();
		selectedelement = $(_elementProperties.selectedelement).closest(".survey-element");
		selectedid = parentid;
	} else if ($(_elementProperties.selectedelement).hasClass("cell"))
	{
		var parentID = $("#content").find(".selectedquestion").closest(".survey-element").first().attr("id");
		var table = _elements[parentID];
		var cell = table.getChildbyId(id);
		oldtext = cell.title()
		cell.originalTitle(text);
		cell.title(text);
		selectedelement =  $("#content").find(".selectedquestion").closest(".survey-element");
		selectedid = parentID;
	} else {
		oldtext = $(_elementProperties.selectedelement).html();
		$(_elementProperties.selectedelement).html(text);
	}
	if (!fromundo)
	_undoProcessor.addUndoStep(["Text", id, uid, oldtext, text]);

	deactivateLinks();

	updateNavigation(selectedelement, selectedid);
	return true;
}

function splitText(input)
{
	var answers = input.split("</p>");		
	if (answers.length == 1) answers = input.split("</P>");		
	
	//special check for line breaks inside tags
	for (var i = 0; i < answers.length; i++)
	{
		answers[i] = filterAnswerText(answers[i]);
		answers[i] = filterEmptyLines(answers[i]);
		
		if (i > 0)			
		if (strStartsWith(answers[i], "</"))
		{
			var len = answers[i].indexOf(">");
			var closingTag = answers[i].substring(0, len+1);
			answers[i-1] = answers[i-1] + closingTag;
			answers[i] = answers[i].substring(len+1);
		}
	}
	
	//remove empty lines
	var result = new Array();
	for (var i = 0; i < answers.length; i++)
	{
		if (answers[i].trim().length > 0)
		{
			result[result.length] = answers[i];
		}
	}
	
	return result;
}

function filterEmptyLines(str)
{	
	if (!String.prototype.fulltrim) {
		String.prototype.fulltrim=function(){return this.replace(/(?:(?:^|\n)\s+|\s+(?:$|\n))/g,'').replace(/\s+/g,' ');};
		}
	
	//replace all <br> occurences by blank. Note: this also replaces <br class="" > and <br/>
	str = str.replace(/<br\s*[\/]?>/gi,' ')
	//.replace(/<\/em>/gi,' ')
	//.replace(/<\/strong>/gi,' ')
	.replace(/&nbsp;/gi,' ')
	.replace(/<strong>\s*<\/strong>/gi,' ') //strong with only blanks inbetween
	.replace(/<em>\s*<\/em>/gi,' ') //em with only blanks inbetween
	.fulltrim()
	.replace(/^<strong>$/gi,'') // a single opening strong
	.replace(/^<em>$/gi,''); // a single opening em
	return str;
}

function filterAnswerText(str)
{
	str = str.trim();
	str = replacer("<p>","", str);
	str = replacer("</p>","", str);
	str = replacer("<P>","", str);
	str = replacer("</P>","", str);
	
	var strmin = str.toLowerCase();
	if (strmin.indexOf("<p") > -1)
	{
		var isrightaligned = strmin.indexOf("text-align: right") > 0;
		var isleftaligned = strmin.indexOf("text-align: left") > 0;
		var iscentered = strmin.indexOf("text-align: center") > 0;
		var start = strmin.indexOf("<p");
		var stop = strmin.indexOf(">", start);
		if (stop > start)
		{
			str = str.substring(0,start) + str.substring(stop+1);
		}
		if (str.trim().length > 0)
		{
			if (!(str.indexOf("text-align") > 0))
			{
				if (isrightaligned) str = "<div style='text-align: right'>" + str + "</div>";
				if (isleftaligned) str = "<div style='text-align: left'>" + str + "</div>";
				if (iscentered) str = "<div style='text-align: center'>" + str + "</div>";
			}				
		}
	}
	return str;
}	