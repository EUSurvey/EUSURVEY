function switchNiceHelp(a)
{
	var div = $(a).closest(".questionhelp");
	
	if ($(a).hasClass("morebutton"))
	{
		$(div).find(".shortcontent").hide();
		$(div).find(".fullcontent").show();
	} else {
		$(div).find(".shortcontent").show();
		$(div).find(".fullcontent").hide();
	}
}

var myWidth = 0;
var myHeight = 0;

function getHeight() {
 
  if( typeof( window.innerWidth ) == 'number' ) {
    //Non-IE
    myWidth = window.innerWidth;
    myHeight = window.innerHeight;
  } else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
    //IE 6+ in 'standards compliant mode'
    myWidth = document.documentElement.clientWidth;
    myHeight = document.documentElement.clientHeight;
  } else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
    //IE 4 compatible
    myWidth = document.body.clientWidth;
    myHeight = document.body.clientHeight;
  }
  return myHeight;
}

function maximize()
{
	h = getHeight();
	$(".page").css("width","100%");
	$(".left-area").css("width","auto");
	$(".left-area").css("max-width", myWidth - 450 + "px");
	$("#maximizebutton").hide();
	$("#restoredownbutton").show();
}

function restoreDown()
{
	$(".page").css("width","1300px");
	$(".left-area").css("max-width", "920px");
	$("#maximizebutton").show();
	$("#restoredownbutton").hide();
}

function updateHeight() {
    h = getHeight();
    
    if ($("#restoredownbutton").is(":visible"))
    {
    	$(".left-area").css("max-width", myWidth - 450 + "px");
    }
}

function returnTrueForSpace(event)
{
	var x = event.keyCode;
	if (x == 32) {
		return true;
	}
	
	return false;
}

function preventScrollOnSpaceInput(event){
	if(event.keyCode == 32)
		event.preventDefault();
}

function ratingClick(link)
{	
	var pos = $(link).index();
	var icons = $(link).attr("data-icons");
	
	//special case: when first icon is selected, clicking on the first icon deselects it
	if (pos == 0 && $(link).closest("td").find("input").first().val().indexOf("1/") == 0)
	{
		pos = -1;
		$(link).closest("td").find("input").first().val("");
	} else {
		$(link).closest("td").find("input").first().val(pos+1 + "/" + icons);
	}
	
	updateRatingIcons(pos, $(link).parent());
	propagateChange(link);
}

function updateRatingIcons(pos, parent)
{
	$(parent).find("img").each(function(index){	
		var src = $(this).attr("src");
		
		if (index <= pos)
		{
			if (src.indexOf("star_grey") > 0)
			{
				src = src.replace("star_grey", "star_yellow");
			} else if (src.indexOf("nav_plain_grey") > 0)
			{
				src = src.replace("nav_plain_grey", "nav_plain_blue");
			} else if (src.indexOf("heart_grey") > 0)
			{
				src = src.replace("heart_grey", "heart_red");
			}
		} else {
			if (src.indexOf("star_yellow") > 0)
			{
				src = src.replace("star_yellow", "star_grey");
			} else if (src.indexOf("nav_plain_blue") > 0)
			{
				src = src.replace("nav_plain_blue", "nav_plain_grey");
			} else if (src.indexOf("heart_red") > 0)
			{
				src = src.replace("heart_red", "heart_grey");
			}
		}
		$(this).attr("src", src);
	});
}

function singleClick(r) {
	var previousValue = $(r).attr('previousValue');
	var name = $(r).attr('name');

	if (previousValue == 'checked') {
	  $(r).removeAttr('checked');
	  $(r).attr('previousValue', false);
	} else {
	  $("input[name="+name+"]:radio").attr('previousValue', false);
	  $(r).attr('previousValue', 'checked');
	}
	
	propagateChange(r);
}

function singleKeyUp(event, target, isParentReadOnly) {
	if (isParentReadOnly) {
		return;
	}
	if (event.code === "Space" && $(target).is(":checked")) {
		$(target).attr("previousValue", "checked").removeAttr("checked");
	}
}

function checkHasValue(element) {
	
	if (element == null || $(element).length == 0)
	{
		return false;
	}
		
	if ($(element).hasClass("rankingitem-list")) {
		return ko.dataFor(element).isAnswered();
	}
	
	if ($(element).hasClass("ratingitem")) {
		return $(element).closest('.ratingtable').find("input[value*='/']").length > 0;
	}
	
	if ($(element).is(":radio")) {		
		if ($(element).closest('.matrix-cell').length > 0) {
			return $(element).closest('.matrixtable').find("input:checked").length > 0;
		}

		if ($(element).closest('.cell[data-type="4"]').length > 0) {
			return $(element).closest('.cell[data-type="4"]').find("input:checked").length > 0;
		}
		
		return $(element).is(":checked");
	}

	if ($(element).hasClass("confirmationCheckbox")) {
		return $(element).is(":checked");
	}

	if ($(element).is(":checkbox")) {
		if ($(element).closest('.matrix-cell').length > 0) {
			return $(element).closest('.matrixtable').find("input:checked").length > 0;
		}

		if ($(element).closest('.cell[data-type="5"]').length > 0) {
			return $(element).closest('.cell[data-type="5"]').find("input:checked").length > 0;
		}
		
		return $(element).closest('.answers-table').find("input:checked").length > 0;
	}
	
	if ($(element).is("a") || $(element).is("button") || $(element).is("li.possible-answer")) {
		return $(element).closest(".listbox,.multiple-choice[role=listbox]").find("input:checked").length > 0;
	}
	
	if ($(element).closest('.tabletable').length > 0) {
		return $(element).closest('.tabletable').find("textarea").filter(function() {
			  return $(this).val() != "";
		}).length > 0;
	}
	
	return $(element).val().length > 0;
}

function propagateChange(element)
{
	if (!$("#btnSaveDraft").hasClass('disabled'))
	{
		$("#btnSaveDraft").removeClass("btn-default").addClass("btn-primary");
	}
	
	var div = $(element).parents(".survey-element").last();
	
	enableDelphiSaveButtons(div);
	
	if ($(element).hasClass("exclusive")) {		
		if ($(element).is(":checked")) {		
			var id = $(element).attr("id");
			$(div).find("input[type=checkbox]").each(function(){
				if ($(this).attr("id") != id) {
					$(this).removeAttr("checked").prop("disabled", true).addClass("disabled").attr('previousValue', false);
					checkDependenciesAsync(this);
				}
			});
		} else {
			$(div).find("input[type=checkbox]").each(function(){
				$(this).removeAttr("disabled", true).removeClass("disabled");
			});
		}
	}
	
	if (!checkHasValue(element)) {
		//disableDelphiSaveButtons(div);
		$(div).find(".explanation-section").hide();
		$(div).find(".explanation-file-upload-section").hide();
		$(element).closest(".forprogress").removeClass("answered");
	} else {
		const surveyElement = $(element).closest(".survey-element");
		if ($(surveyElement).find(".slider-div").length) {
			const questionUid = $(surveyElement).attr("data-uid");
			const viewModel = modelsForSlider[questionUid];
			viewModel.isAnswered(true);
		}
		
		if ($(surveyElement).find("textarea.unique").length > 0) {
			$(surveyElement).find(".validation-error-server").remove();
		}	
		
		$(div).find(".explanation-section").show();
		$(div).find(".explanation-file-upload-section").show();
		$(div).find(".delphiupdatemessage").attr("class","delphiupdatemessage").empty();
		
		$(element).closest(".forprogress").addClass("answered");
	}
	
	updateProgress();
	updateEVoteStatus();
	
	if ($(div).hasClass("numberitem") || $(div).hasClass("formulaitem") || $(div).hasClass("regexitem") || $(element).hasClass("number") || $(element).hasClass("formula")) { //.number and .formula are for complex table cells
		updateFormulas($(div).attr("id"), $(element).attr("data-shortname"));
	}
}

function updateAllExclusiveAnswers() {
	$('.exclusive').each(function() {
		var element = this;
		var div = $(element).parents(".survey-element").last();
			
		if ($(element).is(":checked")) {		
			var id = $(element).attr("id");
			$(div).find("input[type=checkbox]").each(function(){
				if ($(this).attr("id") != id) {
					$(this).removeAttr("checked").prop("disabled", true).addClass("disabled").attr('previousValue', false);
				}
			});
		} else {
			$(div).find("input[type=checkbox]").each(function(){
				$(this).removeAttr("disabled", true).removeClass("disabled");
			});
		}
	});
}

function updateAllFormulas() {
	for (let i = 0; i < modelsForFormula.length; i++) {
		if (modelsForFormula[i].readonly()) {
			modelsForFormula[i].refreshResult();
		} else {
			var value = getValueByQuestion(modelsForFormula[i].uniqueId());
			if (value.length > 0) {
				modelsForFormula[i].result(parseInt(value));
			} else {
				modelsForFormula[i].refreshResult();
			}
		}
	}
}

let checkedFormularAliases = [];
function updateFormulas(id, alias) {
	checkedFormularAliases = [];
	checkedFormularAliases.push(alias);
	updateFormulasRecursive(id, alias);
}
	
function updateFormulasRecursive(id, alias) {	
	for (let i = 0; i < modelsForFormula.length; i++) {
		if (id == null || checkedFormularAliases.indexOf(modelsForFormula[i].shortname()) < 0) { //check not to update itself
			if (modelsForFormula[i].dependsOn(alias)) { //check that the change influences the formula
				modelsForFormula[i].refreshResult();
				checkedFormularAliases.push(modelsForFormula[i].shortname());
				updateFormulasRecursive(modelsForFormula[i].id().toString(), modelsForFormula[i].shortname())
			}
		}
	}
}

function updateProgress() {

	var percent = null;

	// progressbar
	if ($('#progressBar').length != 0) {
		var totalForProgress = $('.forprogress').not(".untriggered,.sahidden,.saDependentHidden,:has(.hidden-element-marker)").length;
		var answered = $('.forprogress.answered').not(".untriggered,.sahidden,.saDependentHidden,:has(.hidden-element-marker)").length;
		percent = answered == 0 ? 0 : Math.round(answered / totalForProgress * 100);

		$('#progressBar').css('width', percent + '%').attr('aria-valuenow', percent);
		$('#progressBarPercentage').html(percent + '%');
		$('#progressBarRatio').html(answered + '/' + totalForProgress);
		$('#progressBarContainer').show();

		var totalwidth = $('#progressBarContainer').width();

		if ((percent * totalwidth / 100) < 80) {
			$('#progressBarLabel').addClass("blacktext");
		} else {
			$('#progressBarLabel').removeClass("blacktext");
		}
	}

	// motivationprogress
	if(!$("#motivationPopup").data("type") && $("#motivationPopup").data("popup") && $("#motivationPopup").hasClass("not-shown")){
		if(percent == null) percent = calculateProgressPercentage(); //only calculate percent again when it hasnt been for the progressbar;
		if(percent >= $("#motivationPopup").data("progress")){
			showPopup();
		}
	}
}

function calculateProgressPercentage() {
	var totalForProgress = $('.forprogress').not(".untriggered").length;
	var answered = $('.forprogress.answered').not(".untriggered").length;
	return answered == 0 ? 0 : Math.round(answered / totalForProgress * 100);
}

var downloadsurveypdflang;
var downloadsurveypdfid;
var downloadsurveypdfunique;
function downloadSurveyPDF(id, lang, unique) {
	$("#download-survey-pdf-dialog-spinner").show();
	$("#download-survey-pdf-dialog-running").show();
	$("#download-survey-pdf-running").show();
	$("#download-survey-pdf-link").hide();
	downloadsurveypdflang = lang;
	downloadsurveypdfunique = unique;
	downloadsurveypdfid = id;
	
	$.ajax({
		type:'GET',
		url: contextpath + "/pdf/surveyexists/" + id,
		data : {
			lang : lang,
			unique : unique
		},
		cache: false,
		success: function( result ) {	
			checkSurveyPDFResult(result);
		},
		error: function( result ) {	
			alert(result);
		}
	});
}

function checkSurveyPDFReady(id) {
	$.ajax({
		type:'GET',
		data : {
			lang : downloadsurveypdflang,
			unique : downloadsurveypdfunique
		},
		url: contextpath + "/pdf/surveyready/" + id,
		success: function( result ) {	
			checkSurveyPDFResult(result);
		},
		error: function( result ) {	
			alert(result);
		}
	});
}

function checkSurveyPDFResult(result) {
	if (result == "exists") {
		$("#download-survey-pdf-dialog-result").css("display", "inline-block").focus();
		$("#download-survey-pdf-dialog-spinner").hide();
		$("#download-survey-pdf-dialog-running").hide();
		$("#download-survey-pdf-dialog-ready").show();
	} else if (result == "wait") {
		window.setTimeout(function() { checkSurveyPDFReady(downloadsurveypdfid); }, 5000);
	} else if (result == "error") {
		$("#download-survey-pdf-dialog-error").show();
	}
}

function clearStars(input) {
	if ($(input).val() == "********") {
		$(input).val("");
	}
}

var page = 0;

var currentUploader;

function createUploader(instance, maxSize)
{
	var uploader = new qq.FileUploader({
	    element: instance,
	    uploadButtonText: selectFilesForUpload,
				action : contextpath + '/runner/upload/'
				+ $(instance).attr('data-id') + "/"
				+ $("#uniqueCode").val() + "?survey=" + $(document.getElementById("survey.uniqueId")).val(),
	    multiple: true,
	    params: {
	    	'_csrf': csrftoken
	    },
	    cache: false,
	    sizeLimit: (maxSize * 1048576),
	    onSubmit: function() {
	    	$(this.element).parent().find(".uploadinfo").show();
	    },
		onComplete : function(id, fileName, responseJSON) {
			$(this.element).parent().find(".uploadinfo").hide();
			updateFileList($(this.element), responseJSON);
			enableDelphiSaveButtons($(this.element).closest(".survey-element"));
			$(this.element).closest(".forprogress").addClass("answered");
			updateProgress();
			updateEVoteStatus();

			if (responseJSON.wrongextension)
	    	{
	    		 $(instance).closest(".survey-element").append("<div class='validation-error' aria-live='polite'>" + getWrongExtensionMessage(fileName) + "</div>");
	    	}

			$(".qq-uploader input[type='file']").attr("title", " ");
			$(instance).find(".btn").first().find("input").focus();
		},
		onError: function() {
			$(this.element).parent().find(".uploadinfo").hide();
			showError(messageuploadnoconnection);
		},
		showMessage: function(message){
			message = message.replace(".0MB", " MB");
		    $(instance).closest(".survey-element").append("<div class='validation-error' aria-live='polite'>" + message + "</div>");
		},
		onUpload: function(id, fileName, xhr){
			$(instance).closest(".survey-element").find(".validation-error").remove();			
		},
	});

	$(".qq-uploader input[type='file']").attr("title", " ");
	
	setTimeout(function(){ 
		$(".file-uploader[data-id='" + $(instance).attr('data-id') + "']").find(".qq-uploader input[type='file']").removeAttr("aria-label").attr("aria-labelledby", "questiontitle" + $(instance).attr('data-id')).attr("aria-describedby", "questionhelp" +  $(instance).attr('data-id'));
	}, 3000);
}

$(function() {
	
	$('#runnerForm').on('submit', function() {
		$('input, select, textarea').attr('disabled', false);
	});

	if ($("#nolocalstorage").length > 0) {
		if (!isLocalStorageEnabled()) {
			 $("#nolocalstorage").show();
			 $("#localstorageinfo").hide();
		 } else {
			 recheckLocalBackup();
			 window.addEventListener("beforeunload", checkLocalBackup)
		 }
	}
	
	$(".file-uploader").each(function() {
		if (!$(this).hasClass("importsurveyuploader"))
		{
			createUploader(this, 1);
		}
	});

	$(".qq-upload-button").addClass("btn btn-default").removeClass("qq-upload-button");
	$(".qq-upload-drop-area").css("margin-left", "-1000px");
	$(".qq-upload-list").hide();
	
	$(".matrixtable").each(function(){
		var matrix = this;
		$(this).find("input").click(function(){
			$(matrix).addClass("clicked");
		});
		
		$(this).hover(function() {
		}, function() {
			if ($(this).hasClass("clicked")) {
						validateInput($(this).parent(),true);
						$(this).removeClass("clicked");
					}
	});
	});
	
	$(".answer-columns").each(function(){
		var cols = this;
		$(this).find("input").click(function(){
			$(cols).addClass("clicked");
		});
		$(this).find("a").click(function(){
			$(cols).addClass("clicked");
		});
		
		$(this).hover(function() {
		}, function() {
			if ($(this).hasClass("clicked")) {
						validateInput($(this).parent(),true);
						$(this).removeClass("clicked");
					}
		});
	});
	
	$(".confirmationelement").each(function(){
		var cols = this;
		$(this).find("input").click(function(){
			$(cols).addClass("clicked");
		});
		$(this).find("a").click(function(){
			$(cols).addClass("clicked");
		});
		
		$(this).hover(function() {
		}, function() {
			if ($(this).hasClass("clicked")) {
						validateInput($(this).parent(),true);
						$(this).removeClass("clicked");
					}
		});
	});
	
	resetValidationErrors($("#runnerForm"));
	
	$("#runner-content").find(".freetext").each(function(){
		countChar(this);
		
		$(this).bind('paste', function(event) {
	        var _this = this;
	        // Short pause to wait for paste to complete
	        setTimeout( function() {
	        	countChar(_this);
	        }, 100);
	    });
	});
	
	$(".pagebutton").each(function(){
		var id = $(this).attr("data-id");
		var elem = document.getElementById(id);
		if ($(elem).hasClass("untriggered")) {
			$(this).addClass("untriggered").hide();
		}
	});
		
	checkPages();	
	
	applyStandardWidths();
	
	$(window).resize(function () { updateHeight(); }); 	
});	

function applyStandardWidths()
{
	$(".single-choice").not(".complex").each(function(){
		var w = $(this).width();
		if (w <= 215) {
			$(this).css("width", "215px");
		} else if (w <= 442) {
			$(this).css("width", "442px");
		} else if (w <= 645) {
			$(this).css("width", "645px");
		} else {
			$(this).css("width", "860px");
		}
	});
	
	$(".multiple-choice").not(".complex").each(function(){
		var w = $(this).width();
		if (w <= 215) {
			$(this).css("width", "215px");
		} else if (w <= 430) {
			$(this).css("width", "430px");
		} else if (w <= 645) {
			$(this).css("width", "645px");
		} else {
			$(this).css("width", "860px");
		}
	});
}

function CheckAllTriggers() {
	$(".trigger").each(function(){
		checkDependencies(this);
	});
		
	$("input").filter("[data-dependencies]").each(function(){
		checkDependencies(this);
	});
	
	$("select").each(function(){
		checkDependencies(this);
	});
}

function resizeToMax(img){
    myImage = new Image();
    myImage.src = img.src; 
    if(myImage.width > myImage.height){
    	img.style.width = "840px";
    } else {
    	img.style.height = "570px";
    }
}

function showGalleryBrowser(td) {
	$(td).closest(".gallery-div").parent().find(".modal").each(function(){
		var selected = $(td).find("input.selection").length > 0 && $(td).find("input.selection").first().is(":checked");
		
		$(this).find(".glyphicon-chevron-right").parent().removeClass("disabled");
		$(this).find(".glyphicon-chevron-left").parent().addClass("disabled");
		
		$(this).find(".modal-body").find(".gallery-image").hide();
		var uid = $(td).attr("data-uid");
		
		$(this).find(".modal-body").find("div[data-uid='" + uid + "']").show();
		
		if (selected)
		{
			$(this).find(".modal-body").find("div[data-uid='" + uid + "']").find("input[type=checkbox]").prop("checked","checked");
		} else {
			$(this).find(".modal-body").find("div[data-uid='" + uid + "']").find("input[type=checkbox]").removeAttr("checked");
		}
		
		$(this).modal("show");
	});
}

function openPreviousImage(dialog) {
	var current = $(dialog).find(".modal-body").find(".gallery-image").filter(
			':visible').first();
	if ($(current).prev().length > 0) {
		$(current).hide();
		$(current).prev().show();
		
		var uid = $(current).prev().attr("data-uid");
		var td = $(".gallery-table").find("td[data-uid=" + uid + "]").first();
		var selected = $(td).find("input.selection").length > 0 && $(td).find("input.selection").first().is(":checked");
		if (selected)
		{
			$(current).prev().find("input[type=checkbox]").prop("checked","checked");
		} else {
			$(current).prev().find("input[type=checkbox]").removeAttr("checked");
		}
	}
	
	$(dialog).find(".glyphicon-chevron-right").parent().removeClass("disabled");
	if ($(current).prev().prev().length == 0) {
		$(dialog).find(".glyphicon-chevron-left").parent().addClass("disabled");
	}
}

function openNextImage(dialog) {
	var current = $(dialog).find(".modal-body").find(".gallery-image").filter(
			':visible').first();
	if ($(current).next().length > 0) {
		$(current).hide();
		$(current).next().show();
	} 
	
	var uid = $(current).next().attr("data-uid");
	var td = $(".gallery-table").find("td[data-uid=" + uid + "]").first();
	var selected = $(td).find("input.selection").length > 0 && $(td).find("input.selection").first().is(":checked");
	if (selected)
	{
		$(current).next().find("input[type=checkbox]").prop("checked","checked");
	} else {
		$(current).next().find("input[type=checkbox]").removeAttr("checked");
	}
	
	$(dialog).find(".glyphicon-chevron-left").parent().removeClass("disabled");
	if ($(current).next().next().length == 0) {
		$(dialog).find(".glyphicon-chevron-right").parent().addClass("disabled");
	}
}

function synchronizeGallerySelection(checkbox)
{
	var selected = $(checkbox).is(":checked");
	var uid = $(checkbox).closest(".gallery-image").attr("data-uid");
	var td = $(".gallery-table").find("td[data-uid=" + uid + "]").first();
	if (selected)
	{
		$(td).find("input[type=checkbox]").prop("checked","checked");
	} else {
		$(td).find("input[type=checkbox]").removeAttr("checked");
	}
}

function closeGalleryBrowser(dialog) {

}

function switchCss(mode, l) {
	$("#newcss").val(l);
	
	if (l == 'wcag')
	{
		$("#wcagMode").val("true");
	} else {
		$("#wcagMode").val("false");
	}
	
	submitToChangeLanguageOrView(false, mode);
}

function switchCss2() {
	$(".css-switch").removeClass("visiblelink");
	
	$("#enhancedcss").show();
	$("#normalcss").hide();
	
	$("#runnerCss").attr("href", contextpath + "/resources/css/disabled.css");
	$("#css-switch-normal").addClass("visiblelink");
	$("#css-switch-disabled").removeClass("visiblelink");
}

function checkEmail(input) {
	var val = $(input).val();
	
	if (val.length == 0 || validateEmail(val)) {
		$(input).parent().parent().find(".emailvalidation").hide();
	} else {
		$(input).parent().parent().find(".emailvalidation").show();
	}
}

function deleteFile(id, uniqueCode, fileName, button) {
	var request = $.ajax({
	  url: contextpath + "/runner/delete/" + id + "/" + uniqueCode + "/" + surveyUniqueId,
	  contentType: "application/json; charset=utf-8",
		data : {
			fileName : fileName
		},
	  dataType: "json",
	  cache: false,
		success : function(data) {
			if (data.success) {
				let instance = $(button).parent().parent().siblings(".file-uploader").first();				
				updateFileList(instance, data);						
				$(instance).find(".btn").first().find("input").focus();
			} else {
				showError("Not possible to delete file");
			}
	  }
	});

}

function previousPage() {
	var i = 1;
	
	while (page - i >= 0)
	{
		if (!$("#tab" + (page - i)).hasClass("untriggered")  && $("#tab" + (page - i)).find(".untriggered").length == 0)
		{
			selectPage(page - i);
			return;
		} else {
			i++;
		}
	}
}

function nextPage() {
	var max = $(".single-page").length;
	var i = 1;
	
	while (page + i < max)
	{
		if (!$("#tab" + (page + i)).hasClass("untriggered") && $("#tab" + (page + i)).find(".untriggered").length == 0)
		{
			selectPage(page + i);		
			return;
		} else {
			i++;
		}
	}
}

function lastPage() {
	selectPage($(".single-page").length -1);
}

function selectPage(val) {
	
	if (val == page) {
		return;
	}
	
	var validatedPerPage = $("#validatedPerPage").val().toLowerCase() == "true";
	var preventGoingBack = $("#preventGoingBack").val().toLowerCase() == "true";
	
	var validate = val > page;
		
	var max = $(".single-page").length;
	if (val < max && val >= 0) {

		if (validate && val > page && val - page > 1)
		{
			//this means a jump to a page, also validate all pages between them
			for (var i = 0; i < val-page; i++)
			{
				if (!validate || !validatedPerPage || $("#hfsubmit").val() != 'true' || validateInput($("#page" + (page + i)))) {
					//ok
					if (i == val-page-1)
					{
						updateQuestionsOnNavigation(page);
						$(".single-page").hide();		
						page = val;		
						$("#page" + page).show();
						checkPages();
						$("#page" + page).focus();
								
						//CheckAllTriggers();
						$("html, body").animate({
							scrollTop : 0
						}, "fast");
					}
				} else {
					if (i > 0)
					{
						updateQuestionsOnNavigation(page);
						$(".single-page").hide();		
						page = page + i;		
						$("#page" + page).show();
						checkPages();
								
						//CheckAllTriggers();
						$("html, body").animate({
							scrollTop : 0
						}, "fast");
					}
					goToFirstValidationError($("#page" + (page))[0]);
					return false;
				}
			}			
		} else {
			if (!validate || !validatedPerPage || $("#hfsubmit").val() != 'true' || validateInput($("#page" + page))) {
				updateQuestionsOnNavigation(page);
				$(".single-page").hide();		
				page = val;		
				$("#page" + page).show();
				checkPages();
				$("#page" + page).focus();
						
				//CheckAllTriggers();
				$("html, body").animate({
					scrollTop : 0
				}, "fast");
			} else {
				goToFirstValidationError($("#page" + page)[0]);
				return false;
			}
		}
	}
}

jQuery.fn.reverse = [].reverse;

function scrollTable(button, right)
{
	var table = $(button).parent().find(".tabletable, .matrixtable").first();
	if (right)
	{
		var tw = $(table).width();
		var w = $(window).width();
		
		if (tw < w) return;
		
		$(table).find("tr").each(function(){
			var done = false;
			$(this).find("td").each(function(index){
				if (index > 0 && !done && $(this).is(":visible"))
				{
					$(this).hide();
					done = true;
					
					if ($(table).hasClass("matrixtable"))
					{
						$(table).width($(table).width() - $(this).width());
					}
				}
			});
		});
	} else {
		$(table).find("tr").each(function(){
			var done = false;
			$(this).find("td").reverse().each(function(index){
				if (index > 0 && !done && !$(this).is(":visible"))
				{
					$(this).show();
					done = true;
				}
			});
		});
	}
	
	checkTableScrollButtons(table);
}

function checkTableScrollButtons(table)
{
	var tw = $(table).width();
	var w = $(window).width();
	
	if (tw > w)
	{
		$(table).parent().find(".scrolltableright").show();
	} else {
		$(table).parent().find(".scrolltableright").hide();
	}
	
	var td = $(table).find("tr").first().find("td")[1];
	if ($(td).is(":visible"))
	{
		$(table).parent().find(".scrolltableleft").hide();
	} else {
		$(table).parent().find(".scrolltableleft").show();
		
		var l = $(table).find("tr").first().find("td").first().width() + 10;		
		$(table).parent().find(".scrolltableleft").css("left",l);		
	}
}

function checkPages() {
	if (page > 0) {
		$("#btnPrevious").show();
	} else {
		$("#btnPrevious").hide();
	}
	
	$(".pagebutton").removeClass("active");
	$(".pagebutton").find("a").removeAttr("title");
	
	$("#sectionmenu").find("li").removeClass("active");	
	$("#tab" + page).addClass("active");
	
	$("#tab" + page).find("a").attr("title", "selected page");
	
	if ($(".single-page").length < 2) {
		$("#page-tabs").removeClass("visible-lg").hide();
	}
	
	//enable submit button if all following sections are not triggered
	var followingSectionFound = false;
	for (var p = page + 1; p < $(".single-page").length; p++)
	{
		if (!$("#tab" + p).hasClass("untriggered") && $("#tab" + p).find(".untriggered").length == 0)
		{
			followingSectionFound = true;
			break;
		}
	}
	
	if (page < $(".single-page").length - 1 && followingSectionFound) {
		$("#btnNext").show();
	} else {
		$("#btnNext").hide();
	}
	
	if (page == $(".single-page").length - 1 || !followingSectionFound) {
		$("#captchadiv").show();
		$("#btnSubmit").show();
	} else {
		$("#captchadiv").hide();
		$("#btnSubmit").hide();
	}
	try {
		if ($("#sectionmenu").is(":visible"))
		$("#sectionmenu").dropdown('hide');
	} catch (e) {
		//ignore
	}
	
	$(".tabletable").each(function(){
		 checkTableScrollButtons(this); 
	 });
	 $(".matrixtable").each(function(){
		 checkTableScrollButtons(this); 
	 });
	 
	 //this is a fix for a bug in the bootstrapSlider library
	 //https://github.com/seiyria/bootstrap-slider/issues/673
	 if ($(".single-page").length > 1) {
		 $(".single-page:visible").find(".sliderbox").each(function () {
			if ($(this).closest(".survey-element").length > 0) {
				refreshSlider(this);
			}
		 });
	 }
}

function refreshSlider(input) {
	var questionUid = $(input).closest(".survey-element").attr("data-uid");
	var viewModel = modelsForSlider[questionUid]; 
	var value = $(input).bootstrapSlider().bootstrapSlider('getValue');
    initSlider(input, false, viewModel);
	$(input).bootstrapSlider().bootstrapSlider('setValue', value);
}

function checkSingleClick(answer){
	singleClick(answer);
	checkInterdependent(answer);
	checkDependenciesAsync(answer);
}

function checkInterdependent(input) {
	if ($(input).closest(".interdependent").length > 0) {
		var value = $(input).val();
		
		$(input).closest(".interdependent").find("input:checked").each(function() {
				if ($(this).val() == value && $(this).attr("id") != $(input).attr("id")) {
				{
					$(this).removeAttr("checked");
					//$(this).attr('previousValue', false);				
					singleClick(this);
					checkDependenciesAsync(this);
				}
			}
		});
	}
	return true;
}

function checkTargetDataset(input) {
	if (!$(input).closest(".survey-element").hasClass("targetdatasetquestion")) return;
	
	const v = $(input).val();
	
	if (v.length == 0) {
		// show all SA questions
		$(".saquestion").removeClass("sahidden");

		$(".survey-element.saDependentHidden").each(function() {
			$(this).removeClass("saDependentHidden");
		})
	} else {
		const c = "sahidden" + v;
		
		$(".saquestion").each(function(){
			 let saquestion_paId = [ ...$(this).find("input.check")].filter(e => e.id != "").map(e => e.id);

			 if ($(this).hasClass(c)) {
				 $(this).addClass("sahidden");

				 //also hide questions which are dependent on a SAQuestion answeroption
				 if (saquestion_paId.length > 0) {
					 $(".survey-element.dependent").each(function () {
						 for (let id of saquestion_paId) {
							 if ($(this).attr("data-triggers").split(";").filter(elem => elem === id).length > 0) {
								 $(this).addClass("saDependentHidden")
							 }
						 }
					 })
				 }
			 } else {
				 $(this).removeClass("sahidden");

				 if (saquestion_paId.length > 0) {
					 $(".survey-element.saDependentHidden").each(function() {
						 for (let id of saquestion_paId) {
							 if ($(this).attr("data-triggers").split(";").filter(elem => elem === id).length > 0) {
								 $(this).removeClass("saDependentHidden");
							 }
						 }
					 })
				 }
			 }
		 });
	}

	propagateChange(input)
}

function checkDependenciesAsync(input, override) {
	if ($(input).closest(".elementwrapper").find("input[data-dependencies], option[data-dependencies]").length > 0 )
	{
		var deps = "";
		
		$(input).closest(".elementwrapper").find("input[data-dependencies], option[data-dependencies]").each(function(){
			deps += $(this).attr("data-dependencies");
		});
		
		if (deps == "") return;
		$(input).closest(".elementwrapper").addClass("waiting").append('<div class="waitingdiv"><span class="glyphicon glyphicon-hourglass"></span>' + varwaitfordependencies + "</div>");
		setTimeout(function() {
			cachedIsTriggered = {};
			try {
				checkDependencies(input, override != null && override);
				updateProgress();
				updateEVoteStatus();
			} catch (e) {}
			
			$(input).closest(".elementwrapper").removeClass("waiting").find(".waitingdiv").remove();
		}, 100);
	}
}

function checkDependencies(input) {
	checkDependencies(input, false);
}

function checkDependencies(input, overrideinvisible) {
	
	var dependencies = $(input).attr("data-dependencies");
	var type = $(input).attr("type");
	var active = $(input).is(":checked");
	var child = false;
	
	if ($(input).prop("tagName").toLowerCase() == "option") {
		input = $(input).parent();
		child = true;
	}
	
	if ($(input).prop("tagName").toLowerCase() == "select") {
		var option = $(input).find(":selected").first();
		type = "select";
		dependencies =  $(option).attr("data-dependencies");
		active = true;
	}
	
	active = active || $(input).is(":checked");
	
	//only visible elements 
	if (active && !$(input).is(":visible") && !overrideinvisible) {
		if (!$(input).parent().is(":visible")) {
			active = false;
		}
	}
	
	if (dependencies != null && dependencies.length > 0) {
		var elementIds = dependencies.split(";");
		handleElements(active, elementIds);
	}	
	
	if (active && type == 'radio') {
		hideMultipleDependencies($(input).closest(".answers-table").find(".trigger"));
		
		if ($(input).parent().hasClass("matrix-cell")) {
			hideMultipleDependencies($(input).parent().siblings().find(".trigger"));
		}		
	}
	
	if (!child && active && type == 'select') {
		hideMultipleDependencies($(input).closest(".answer-column").find(".trigger"));
		
		if ($(input).closest(".matrixtable").length > 0) {
			hideMultipleDependencies($(input).find(".trigger"));
		}		
	}
	
}

function handleElements(active, elementIds) {
	var i;
	for (i = 0; i < elementIds.length; ++i) {
		handleElement(active, elementIds, i)
	}
}

function handleElement(active, elementIds, i) {
	
	if (elementIds[i].length == 0) return;
	
	var element = $("[data-id=" + elementIds[i] + "]:not(.pagebutton, .pagebuttonli)").first();
	var useAndLogic = $(element).attr("data-useAndLogic") == "true";
	
	var triggered = active;
	
	if (useAndLogic || !triggered) {
		triggered = isTriggered(element, true, false);
	}
				
	if (triggered) {
		if ($(element).hasClass("untriggered")) {
			$(element).removeClass("untriggered").show();	
			
			//show page button if element is section
			if ($(".pagebutton[data-id='" + elementIds[i] + "']").length > 0)
			{
				$(".pagebutton[data-id='" + elementIds[i] + "']").removeClass("untriggered").show();
				$(".pagebutton[data-id='" + elementIds[i] + "']").parent().removeClass("untriggered");
				checkPages();
			} else {
				$(element).find(".tabletable").each(function(){
					 checkTableScrollButtons(this); 
				 });
				$(element).find(".matrixtable").each(function(){
					 checkTableScrollButtons(this); 
				 });
				$(element).find(".sliderbox").each(function(){
					refreshSlider(this);
				});
			}

			var atLeastOneSubQuestion = false;
			$(element).find(".matrix-question").each(function(){
				var triggered = isTriggered(this, true);
				if (triggered) {
					atLeastOneSubQuestion = true;
				}
				if ($(this).hasClass("untriggered") && triggered)
				{
					$(this).removeClass("untriggered").show();
					$(element).find(".trigger").each(function() {
						checkDependencies(this);
					});
				}
			});
			if ($(element).hasClass("matrixitem") && !atLeastOneSubQuestion) {
				$(element).addClass("untriggered").hide();
			}
			
			// if the target contains other single/multiple choice answers, also
			// check those dependents
			$(element).find(".trigger").each(function() {
				checkDependencies(this);
			});
			
			$(element).find(".tabletable").find("textarea").each(function(){
				var height = $(this).parent().height();
				if (height < 35) height = 35;
				$(this).height(height);
			});
		} else {
			$(element).find(".matrix-question").each(function(){
				if ($(this).hasClass("untriggered") && isTriggered(this, true))
				{
					$(this).removeClass("untriggered").show();
					$(element).find(".trigger").each(function() {
						checkDependencies(this);
					});
				}
			});
		}		
		
	} else {
		if (!$(element).hasClass("untriggered")) {
			$(element).addClass("untriggered").hide();				
				
			if ($(".pagebutton[data-id='" + elementIds[i] + "']").length > 0)
			{
				//hide page button if element is section
				$(".pagebutton[data-id='" + elementIds[i] + "']").addClass("untriggered").hide();
				checkPages();
			}
			
			// if the target contains other single/multiple choice answers, also
			// check those dependents
			$(element).find(".trigger").each(function() {
				hideDependencies(this);
			});
		}
		
		$(element).find(".matrix-question").each(function(){
			$(this).addClass("untriggered").hide();
		});
	}	

	if ($(element).hasClass("matrix-question")) {
		var matrixItem = $(element).closest(".matrixitem");
		if (matrixItem.length > 0 && $(matrixItem).hasClass("dependent") && !isTriggered(matrixItem, true)) {
			//hide matrix if dependencies of matrix itself aren't fulfilled although a subquestion may be visible
			matrixItem.addClass("untriggered").hide();
			$(element).find(".matrix-question").each(function(){
				$(this).addClass("untriggered").hide();
			});
		} else {
			//hide matrix if all questions are hidden
			if ($(element).closest("table").length > 0)
			{
				//normal runner
				if ($(element).closest("table").find("tr.untriggered").length == $(element).closest("table").find("tr").length - 1)
				{
					if (!triggered)
						$(element).closest(".survey-element").addClass("untriggered").hide();
				} else {
					if (triggered)
					{
						$(element).closest(".survey-element").removeClass("untriggered").show();

						// if the target contains other single/multiple choice answers, also
						// check those dependents
						$(element).find(".trigger").each(function() {
							checkDependencies(this);
						});
					}
				}
			} else {
				//mobile runner
				if ($(element).closest(".matrixdiv").find(".matrix-question.untriggered").length == $(element).closest(".matrixdiv").find(".matrix-question").length)
				{
					if (!triggered)
						$(element).closest(".survey-element").addClass("untriggered").hide();
				} else {
					if (triggered)
						$(element).closest(".survey-element").removeClass("untriggered").show();
				}
			}
		}
	}
	
}

function checkSectionDependencies(element, triggered) {
	var level = parseInt($(element).find(".sectiontitle").first().attr("data-level"));
	
	var go = false;
	var id = $(element).attr("id");
	
	$(".survey-element").each(
			function() {
				if ($(this).attr("id") == id) {
					//ignore everything before the current section
					go = true;
				} else {
					if ($(this).find(".sectiontitle").length > 0) {
						var level2 = parseInt($(this).find(".sectiontitle").first().attr("data-level"));
						if (level2 <= level) {
							// stop as soon as a section with equal or larger
							// level is found
							go = false;
						}
				}
			
				if (go) {
					if (triggered) {
						$(this).show();				
						// if the target contains other single/multiple
						// choice answers, also check those dependents
						$(this).find(".trigger").each(function() {
							checkDependencies(this);
						});
					} else {
						$(this).hide();				
						// if the target contains other single/multiple
						// choice answers, also check those dependents
						$(this).find(".trigger").each(function() {
							hideDependencies(this);
						});
				}	
			}
		}
	});
}

var cachedElements = {};
function getCachedElementById(id)
{
	if (cachedElements[id] == null)
	{
		cachedElements[id] = document.getElementById(id);
	}
	if (cachedElements[id] == null && id.indexOf("|") > 0)
	{
		cachedElements[id] = $("input[data-cellid='" + id + "']");
	}
	return cachedElements[id];
}

var cachedIsTriggered = {};
function getCachedIsTriggered(id)
{
	return cachedIsTriggered[id];
}

function isTriggered(element, stoprecursion) {
		
	var id =  $(element).attr("id");
	if ($(element).hasClass("matrix-question")) id = $(element).attr("data-id");
	var useAndLogic = $(element).attr("data-useAndLogic") == "true";
	
	var r = getCachedIsTriggered(id);
	if (r != null) return r;
	
	var triggers = $(element).attr("data-triggers");	
		
	if (triggers != null && triggers.length > 1) { //can be ";"
		var triggerIds = triggers.split(";");
		while (triggerIds[triggerIds.length - 1] == "") {
			triggerIds.pop(); 	//last element is only an empty String
		}

		var i;
		var result = false;
		for (i = 0; i < triggerIds.length; ++i) {
			var trigger = getCachedElementById(triggerIds[i]);
			
			if (trigger == null)
				trigger = getCachedElementById('trigger' + triggerIds[i]);
			
			if (trigger != null) {
				if (!$(trigger).closest(".survey-element").hasClass("untriggered")) {
					if ($(trigger).is(":checked")) {
						if ($(trigger).closest(".matrix-question").length == 0
								|| !$(trigger).closest(".matrix-question")
										.hasClass("untriggered")) {
							if (!useAndLogic) {
								cachedIsTriggered[id] = true;
								return true;
							} else {
								result = true;
							}
						}						
					} else if (useAndLogic) {
						cachedIsTriggered[id] = false;
						return false;
					}
				} else if (useAndLogic) {
					cachedIsTriggered[id] = false;
					return false;
				}
			}
		}
			
		if (useAndLogic) {
			cachedIsTriggered[id] = result;
			return result;
		}
	} 
	if ($(element).hasClass("matrix-question"))
	{
		if (triggers == ";") {
			$(element).removeClass("untriggered").show();
			cachedIsTriggered[id] = true;
			return true;
		} else {
			//the matrix question itself is not triggered; independent of parent!!!
			cachedIsTriggered[id] = false;
			return false;
		}
	}
	
	if (stoprecursion) {
		cachedIsTriggered[id] = false;
		return false;
	}
	
	cachedIsTriggered[id] = result;
	return result;
}

function hideDependencies(input) {
	var dependencies = $(input).attr("data-dependencies");
	hideDependenciesInner(dependencies)
}

function hideMultipleDependencies(list) {
	cachedIsTriggered = {};
	
	var d = "";
	$(list).each(function(){
		var dependencies = $(this).attr("data-dependencies");
		if (dependencies != null && dependencies.length > 0) {
			d = d + dependencies;
		}		
	})
	hideDependenciesInner(d);
}

function hideDependenciesInner(dependencies) {
	if (dependencies != null && dependencies.length > 0) {
		var elementIds = dependencies.split(";");

		var i;
		for (i = 0; i < elementIds.length; ++i) {
			if (elementIds[i].length > 0)
				handleHideEntry(elementIds[i]);
		}
	}
}

function handleHideEntry(entry) {
	var element = $("[data-id=" + entry + "]:not(.pagebutton, .pagebuttonli)").first();
		var triggered = isTriggered(element, false); 
		
		if (!triggered && !$(element).hasClass("untriggered")) {
			$(element).hide();	
			$(element).addClass("untriggered");
			
			//hide page button if element is section
			if ($(".pagebutton[data-id='" + entry + "']").length > 0)
			{
				$(".pagebutton[data-id='" + entry + "']").addClass("untriggered").hide();
				checkPages();
			}
			
			if ($(element).hasClass("matrix-question")) {
				//hide matrix if all questions are hidden
				if ($(element).closest("table").length > 0)
				{	
					//normal runner
					if ($(element).closest("table").find("tr.untriggered").length == $(element).closest("table").find("tr").length - 1) 
					{
						$(element).closest(".survey-element").addClass("untriggered").hide();
					} else {
						if (triggered)
						$(element).closest(".survey-element").removeClass("untriggered").show();
					}
				} else {
					//mobile runner
					if ($(element).closest(".matrixdiv").find(".matrix-question.untriggered").length == $(element).closest(".matrixdiv").find(".matrix-question").length) 
					{
						$(element).closest(".survey-element").addClass("untriggered").hide();
					} else {
						if (triggered)
						$(element).closest(".survey-element").removeClass("untriggered").show();
					}
				}
			}
		
			// if the target contains other single/multiple choice answers, also
			// hide those dependents
			hideMultipleDependencies($(element).find(".trigger"));
		} else {
			$(element).find(".matrix-question").each(function(){
				if (!isTriggered(this, false))
				{
					$(this).hide();	
					$(this).addClass("untriggered");
	
					$(this).find(".trigger").each(function() {
						checkDependencies(this);
					});				
				}
			});
		}
		
		if (!triggered && $(element).hasClass("matrix-question")) {
			//hide matrix if all questions are hidden
			if ($(element).closest("table").find("tr.untriggered").length == $(element).closest("table").find("tr").length - 1) 
			{
				$(element).closest(".survey-element").addClass("untriggered").hide();
			}
		}
		
		
	}

function changeLanguage(mode, lang, draftid) {
	$("#newlang").val(lang);
	submitToChangeLanguageOrView(true, mode);
}

function changeLanguageSelectOption(mode) {
	$("#newlang").val($('#langSelectorRunner').val());
	submitToChangeLanguageOrView(true, mode);
}

function changeLanguageSelectHeader(mode, headerLang) {
	$("#newlang").val(headerLang);
	submitToChangeLanguageOrView(true, mode);
}

function changeLanguageSelector(mode, draftid) {
	$("#newlang").val($('#language-selector').val());
	submitToChangeLanguageOrView(true, mode);
}

function submitToChangeLanguageOrView(lang, mode)
{
	checkLocalBackup()
	
	if (lang)
	{
		$("#newlangpost").val("true");
	} else {
		$("#newviewpost").val("true");
	}
	
	var sessiontimeout = false;
	var networkproblems = false;
	var unknownerror = false;
	
	if (mode == "runner")
	{
		$.ajax({type: "POST",
			url: contextpath + "/runner/checksession",
			async: false,
			beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
			error: function(result)
		    {
				var s = result.statusText.toLowerCase();
				if (strStartsWith(s,"forbidden") || result.status == 403)
				{
					sessiontimeout = true;
				} else if (strStartsWith(s,"networkerror"))
				{
					networkproblems = true;
				} else {
					unknownerror = true;
				}
		    },
			success: function(result)
		    {
		    	//everything is ok
		    }
		 });
	}
	
	if (sessiontimeout)
	{
		showSessionError();
	} else if (networkproblems) {
		$("#networkproblemsdialog").modal('show');
	} else {		
		$("#busydialog").modal('show');
		$("#runnerForm").find("input[data-is-answered='false'].sliderbox").val('');
		$("#runnerForm").submit();
	}
}

function saveDraft(mode) {
	checkLocalBackup()
	
	var sessiontimeout = false;
	var networkproblems = false;
	var unknownerror = false;
	
	if (mode == "runner")
	{
		$.ajax({type: "POST",
			url: contextpath + "/runner/checksession",
			async: false,
			beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
			error: function(result)
		    {
				var s = result.statusText.toLowerCase();
				if (strStartsWith(s,"forbidden") || result.status == 403)
				{
					sessiontimeout = true;
				} else if (strStartsWith(s,"networkerror"))
				{
					networkproblems = true;
				} else {
					unknownerror = true;
				}
		    },
			success: function(result)
		    {
		    	//everything is ok
		    }
		 });
	}
	
	if (sessiontimeout)
	{
		showSessionError();
	} else if (networkproblems) {
		$("#networkproblemsdialog").modal('show');
	} else {
		let form = $("#runnerForm")
		form.attr("action", contextpath + "/runner/draft/" + mode);
		
		$("#busydialog").modal('show');

		form.find("input[data-is-answered='false'].sliderbox").val('');

		form.submit();
	}
}

var backupHelper = {
	/*
	 <id>: {
		 uid: "..."
		 type: "..."
	 },
	 */
}

var _surveyBackupIdentifier
function getSurveyIdentifier() {

	if (_surveyBackupIdentifier == null){
		if ($("#survey\\.id").length == 0) return; // for example on the skin page
		
		let survey = $(document.getElementById("survey.id")).val();
		let invitation = $(document.getElementById("invitation")).val();
		let uniqueCode = $(document.getElementById("uniqueCode")).val();
		let draftId = $(document.getElementById("draftid")).val();

		if (invitation.length > 0 && uniqueCode.length > 0) {
			_surveyBackupIdentifier = survey + "." + uniqueCode;
		} else if (draftId.length > 0) {
			_surveyBackupIdentifier = survey + "." + draftId;
		} else {
			_surveyBackupIdentifier = survey
		}
	}

	return _surveyBackupIdentifier
}

function shouldSaveLocalBackup(){
	//jquery automatically return false if length == 0
	return isLocalStorageEnabled() && $('#saveLocalBackup').is(":checked");
}

function checkLocalBackup() {
	if (shouldSaveLocalBackup()) {
		$("#runnerForm").find("input[data-is-answered='false'].sliderbox").val('');
		saveLocalBackup();
	} else {
		clearLocalBackup();
	}
}

function recheckLocalBackup(){
	checkLocalBackup();
	window.setTimeout(recheckLocalBackup, 60000);
}

function clearLocalBackup(){
	if (!checkLocalStorageEnabled()) {
		return;
	}
	
	const key = getSurveyIdentifier();
	if (key != null) {
		window.localStorage.removeItem(key);
	}
}

function clearLocalBackupForPrefix(surveyprefix) {
	if (!checkLocalStorageEnabled(false)) {
		return;
	}
	
	var keystodelete = [];
	for (var i = 0; i < localStorage.length; i++){
	    var key = localStorage.key(i);
		if (strStartsWith(key, surveyprefix)) {
	    	keystodelete[keystodelete.length] = key;
	    }
	}
	
	for	(var i = 0; i < keystodelete.length; i++) {
		localStorage.removeItem(keystodelete[i]);
	}
}

function saveLocalBackup(){
	const backup = $("#runnerForm").serializeArray()

	let filesUploaded = $(".uploaditem .uploaded-files > div").length > 0

	backup.push({name: "__filesUploaded", value: filesUploaded})

	const key = getSurveyIdentifier();
	if (key != null) {
		window.localStorage.setItem(key, JSON.stringify(backup));
	}
}

function restoreBackup(){
	const key = getSurveyIdentifier();

	const store = window.localStorage.getItem(key);

	if (store == null)
		return;

	const backup = JSON.parse(store);

	const merger = {}

	backup.forEach(entry => {
		const name = entry.name;
		let value = entry.value;

		if (merger.hasOwnProperty(name)){
			value = merger[name] + ";" + value
		}
		merger[name] = value;

		if (name.startsWith("answer")){
			backupLoaded = true;
			const id = name.slice(6);
			if (id.includes("|")){
				const [newid, row, column] = id.split("|");
				const elem = backupHelper[newid];

				tablevalues[elem.uid + "#" + row + "#" + column] = value;
			} else {
				const elem = backupHelper[id];
				values[elem.uid] = value;
				pavalues[elem.uid] = value.split(";").map(id => backupHelper.hasOwnProperty(id) ? backupHelper[id].uid : "");
			}
		} else if (name == "__filesUploaded" && value){
			window.setTimeout(() => showLocalBackupFilesInfo(), 400)
		}
	})
}

function isLocalStorageEnabled() {
	return checkLocalStorageEnabled(true, true);
}

var _localStorageEnabled
function checkLocalStorageEnabled(checkDelphi, checkEVote) {
	if (checkDelphi && checkEVote && $("#saveLocalBackup").length === 0) {
		// local backup checkbox is not displayed => Delphi/ EVote survey => disable local storage
		return false;
	}

	if (_localStorageEnabled == null){
		if (typeof (Storage) == "undefined"){
			_localStorageEnabled = false;
		} else {
			let testValue = "";
			try {
				window.localStorage.setItem("EUSurvey.LocalStorageTest", "abcdefg");
				testValue = window.localStorage.getItem("EUSurvey.LocalStorageTest");
				window.localStorage.removeItem("EUSurvey.LocalStorageTest");
			} catch (e) {
				// local storage not available
			} finally {
				_localStorageEnabled = testValue === "abcdefg";
			}
		}
	}

	return _localStorageEnabled;
}

function fetchECFResult() {
	$.ajax({
		type:'GET',
		url: contextpath + "/" + surveyShortname + "/management/ecfResultJSON?answerSetId=" + uniqueCode,
		cache: false,
		success: function(ecfResult) {
			if (ecfResult == null) {
				setTimeout(function(){ fetchECFResult(); }, 10000);
				return;
			} else {
				displayECFTable(ecfResult);
				displayECFChart(ecfResult);
				return ecfResult;
			}
		}
	});
}

function displayECFTable(result) {
	result.competencies.forEach(competency => {
	$("#ecfResultTable > tbody:last-child").append('<tr><td>' + competency.name + '</td>'
			+ '<td>' + competency.score + '</td>'
			+ '<td>' + competency.targetScore + '</td>'
			+ '<td>' + competency.scoreGap + '</td></tr>')
			});
}

function displayECFChart(result) {
	if (result) {
		profileName = result.name;		
		scores = [];
		competencies = [];
		targetScores = [];
		result.competencies.forEach(competency => {
			scores.push(competency.score);
			competencies.push(competency.name);
			targetScores.push(competency.targetScore);
		});
		
		$('.ecfRespondentChart').each(function(index, element) { 
			var ctx = element.getContext("2d");
			var options = {
				scale: {
					angleLines: {
						display: false
					},
						ticks: {
							suggestedMin: 0,
							suggestedMax: 4
						}
					},
				maintainAspectRatio: true,
				spanGaps: false,
				elements: {
					line: {
						tension: 0.000001
					}
				},
				plugins: {
					filler: {
						propagate: false
					},
					'samples-filler-analyser': {
						target: 'chart-analyser'
					}
				}
			};
		
			var myRadarChart = new Chart(ctx, {
				type: 'radar',
				data: {
					labels: competencies,
					datasets: [{
						label: 'Your results',
						data: scores,
						backgroundColor: 'rgba(255, 99, 132, 0.2)',
						borderColor: 'rgba(255, 99, 132, 1)',
						borderWidth: 1
					},
					{
						label: 'Target results for profile ' + profileName,
						data: targetScores,
						backgroundColor: 'rgba(97, 197, 255, 0.2)',
						borderColor: 'rgba(97, 197, 255, 1)',
						borderWidth: 1
					}
					]
				},
				options: options
			});
		});	
	}
}

function eVoteEntireListClick(checkbox) {
	let table = $(checkbox).closest(".evote-table")

	if (table.is(".evote-brussels")){
		if (checkbox.checked) {
			//Uncheck all other lists
			$(".evote-brussels .entire-list:checked").not(checkbox).click();
			//Disable all other lists
			$(".evote-brussels .entire-list").not(checkbox).prop("disabled", true);
			//And uncheck + disable all candidates
			$(".evote-brussels .evote-candidate").prop("checked", false).prop("disabled", true);
		} else {
			//Reenable all other lists and candidates
			$(".evote-brussels .entire-list, .evote-brussels .evote-candidate").prop("disabled", false);
		}

	} else if (table.is(".evote-ispra")){
		if (checkbox.checked) {
			//Uncheck all other lists
			$(".evote-ispra .entire-list:checked").not(checkbox).click();
			//Disable all other lists
			$(".evote-ispra .entire-list").not(checkbox).prop("disabled", true);
			//And uncheck + disable all candidates
			$(".evote-ispra .evote-candidate").prop("checked", false).prop("disabled", true);
		} else {
			//Reenable all other lists and candidates
			$(".evote-ispra .entire-list, .evote-ispra .evote-candidate").prop("disabled", false);
		}

	} else {
		//Adjust all candidate checkboxes of this list
		table.find(".evote-candidate").each(function () {
			this.checked = checkbox.checked;
			this.setAttribute("previousValue", this.checked ? false : "checked");
			singleClick(this);
		})
		checkbox.indeterminate = false;
	}
	updateEVoteList(checkbox);
}

//Clears all votes made so far
function clearEVoteVotes() {
	$(".evote-candidate, .entire-list")
		.attr('previousValue', "checked").prop("checked", false)
		.each(function (){ singleClick(this); } );
	$(".entire-list").prop('indeterminate', false);
	$(".evote-brussels .entire-list, .evote-brussels .evote-candidate").prop("disabled", false);
	$(".evote-ispra .entire-list, .evote-ispra .evote-candidate").prop("disabled", false);
	$(".evote-validation").remove();

	$('#votedCandidates').html(0);
	$('#votedLists').html(0);
	updateEVoteStatus();
}

//Updates the display for the Entire List Checkbox (Non Brussels)
//And updates statistics
function updateEVoteList(element) {
	let table = $(element).closest(".evote-table");
	if (!(table.is(".evote-brussels") || table.is(".evote-ispra"))) {
		let checkedCount = table.find(".evote-candidate:checked").length;
		let entireListCheckbox = table.find(".entire-list")
		if (checkedCount == 0) { //Decide whether to show the Entire List as checked or indeterminate
			entireListCheckbox.prop('checked', false);
			entireListCheckbox.prop('indeterminate', false);
		} else if (checkedCount == table.find(".evote-candidate").length) {
			entireListCheckbox.prop('checked', true);
			entireListCheckbox.prop('indeterminate', false);
		} else {
			entireListCheckbox.prop('checked', false);
			entireListCheckbox.prop('indeterminate', true);
		}
	}

	$(".evote-validation").remove();

	updateEVoteStatus();
}

var votedLists = 0
var votedCandidates = 0;

//Updates the eVote Status Bar
function updateEVoteStatus() {

	if ($(".survey-element:not(.untriggered) .evote-table").length > 0){
		$("#evoteVoterOverview").css("display", "")
	} else {
		$("#evoteVoterOverview").css("display", "none")
	}

	if ($(".evote-brussels, .evote-ispra, .evote-luxembourg").length <= 0){
		$("#votedListsWrapper").css("display", "none")
	} else {
		$("#votedListsWrapper").css("display", "")
	}

	votedLists = 0;
	//list votes for brussels and ispra
	votedLists += $(".evote-brussels .entire-list:checked").length;
	votedLists += $(".evote-ispra .entire-list:checked").length;

	//no list votes for outside, luxembourg and standard
	votedCandidates = 0;
	votedCandidates += $(".survey-element:not(.untriggered) .evote-candidate:checked").length;

	let validation = validateEVote();
	if (validation.error && !(validation.candidatesInvalid2)){
		$("#evoteVoterOverview").attr("error", true);

		$('#votedLists').css("font-weight", validation.listsInvalid ? "bold" : "")
		$('#votedCandidates').css("font-weight", validation.candidatesInvalid1 ? "bold" : "")
	} else {
		$("#evoteVoterOverview").removeAttr("error");
		$(".evote-validation").remove();
		$('#votedLists').css("font-weight", "")
		$('#votedCandidates').css("font-weight", "")
	}

	$('#votedLists').html(votedLists);
	$('#votedCandidates').html(votedCandidates);
}

function validateEVote(){
	let candidateError1 = votedCandidates > maxEVoteCandidates;
	let candidateError2 = false;
	if ($(".survey-element:not(.untriggered) .evote-table").length > 0) {
		candidateError2 = votedCandidates === 0 & votedLists === 0;
	}
	let listError = votedLists > 1;
	return {error:  listError || candidateError1 || candidateError2, candidatesInvalid1 : candidateError1, candidatesInvalid2 : candidateError2, listsInvalid: listError}
}