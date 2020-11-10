function switchNiceHelp(a)
{
	var div = $(a).closest(".questionhelp");
	var text = $(div).find(".fullcontent").html().replace("&nbsp;", " ");
	var sign = "<span class='glyphicon glyphicon-question-sign'></span>&nbsp;";
	
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

function propagateChange(element)
{
	if (!$("#btnSaveDraft").hasClass('disabled'))
	{
		$("#btnSaveDraft").removeClass("btn-default").addClass("btn-primary");
	}
	
	var div = $(element).closest(".survey-element");
	$(div).find("a[data-type='delphisavebutton']").removeClass("disabled");
	$(div).find(".delphiupdatemessage").empty();
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
		$("#download-survey-pdf-dialog-result").css("display", "inline-block");
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
	    uploadButtonText: selectFileForUpload,
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
	    	
	    	if (responseJSON.wrongextension)
	    	{
	    		 $(instance).closest(".survey-element").append("<div class='validation-error' aria-live='polite'>" + getWrongExtensionMessage(fileName) + "</div>");
	    	}
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
}

$(function() {
	
	$('#runnerForm').on('submit', function() {
		$('input, select').attr('disabled', false);
	});

	if ($("#nolocalstorage").length > 0) {
		if (!is_local_storage_enabled()) {
			 $("#nolocalstorage").show();
			 $("#localstorageinfo").hide();
		 } else {
			 window.setTimeout("saveCookies()", 60000);
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
	$(".single-choice").each(function(){
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
	
	$(".multiple-choice").each(function(){
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
				updateFileList($(button).parent().parent().siblings(
						".file-uploader").first(), data);
			} else {
				showRunnerError("Not possible to delete file");
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
	var validatedPerPage = $("#validatedPerPage").val().toLowerCase() == "true";
	
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
						$(".single-page").hide();		
						page = val;		
						$("#page" + page).show();
						checkPages();		
								
						//CheckAllTriggers();
						$("html, body").animate({
							scrollTop : 0
						}, "fast");
					}
				} else {
					if (i > 0)
					{
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
					return;
				}
			}			
		} else {
			if (!validate || !validatedPerPage || $("#hfsubmit").val() != 'true' || validateInput($("#page" + page))) {
				
				$(".single-page").hide();		
				page = val;		
				$("#page" + page).show();
				checkPages();		
						
				//CheckAllTriggers();
				$("html, body").animate({
					scrollTop : 0
				}, "fast");
			} else {
				goToFirstValidationError($("#page" + page)[0]);
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
	
	var triggered = active;
	if (!triggered)
		triggered = isTriggered(element, true);
				
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
			}
			
			$(element).find(".matrix-question").each(function(){
				if ($(this).hasClass("untriggered") && isTriggered(this, true))
				{
					$(this).removeClass("untriggered").show();	
				}
			});
			
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
			if (!$(this).hasClass("untriggered") && !isTriggered(this, true))
			{
				$(this).addClass("untriggered").hide();	
			} else if (isTriggered(this, true))
			{
				$(this).removeClass("untriggered").show();	
				$(element).removeClass("untriggered").show();
			}
		});
	}	
	
	if ($(element).hasClass("matrix-question")) {
		//hide matrix if all questions are hidden
		if ($(element).closest("table").length > 0)
		{	
			//special case: all dependent elements are hidden and matrix itself is dependent
			if ($(element).closest(".survey-element").attr("data-triggers") && $(element).closest(".survey-element").attr("data-triggers") != ";")
			{
				var dependents = $(element).closest("table").find("tr[data-triggers!=';']").length - 1;
				var hiddendependents = $(element).closest("table").find("tr[data-triggers!=';'].untriggered").length;
				if (dependents == hiddendependents)
				{
					//also hide other matrix questions
					$(element).closest("table").find("tr[data-triggers=';']").addClass("untriggered").hide();
				}
			}
			
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
	if ($(element).hasClass("matrix-question")) id =  $(element).attr("data-id");
	
	var r = getCachedIsTriggered(id);
	if (r != null) return r;
	
	var triggers = $(element).attr("data-triggers");	
		
	if (triggers != null && triggers.length > 1) { //can be ";"
		var triggerIds = triggers.split(";");
		var i;
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
							cachedIsTriggered[id] = true;
							return true;
						}						
					}
				}
			}			
		}
	} 
	if ($(element).hasClass("matrix-question"))
	{
		//the matrix question itself is not triggered but its parent matrix
		var res =  isTriggered($(element).closest(".survey-element"), true);
		cachedIsTriggered[id] = res;
		if (res) return true;
	}
	
	if (stoprecursion) {
		cachedIsTriggered[id] = false;
		return false;
	}
	
	var result = false;
	$(element).find(".matrix-question").each(function(){
		if (isTriggered(this, true))
		{
			result = true;
			return;
		}
	});
	
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
	submitToChangeLanguageOrView(true), mode;
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
	saveCookies();
	
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
		$("#sessiontimeoutdialog").modal('show');
	} else if (networkproblems) {
		$("#networkproblemsdialog").modal('show');
	} else {			
		$("#busydialog").modal('show');
		$("#runnerForm").submit();
	}
}

function saveDraft(mode) {
	saveCookies();
	
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
		$("#sessiontimeoutdialog").modal('show');
	} else if (networkproblems) {
		$("#networkproblemsdialog").modal('show');
	} else {	
		$("#runnerForm").attr("action", contextpath + "/runner/draft/" + mode);
		
		$("#busydialog").modal('show');
		$("#runnerForm").submit();
	}
}

function readCookies() {
	cachedIsTriggered = {};
	readCookiesForParent($("body"));
}

function readCookiesForParent(parent)
{
	if (!is_local_storage_enabled()) {
		return;
	}
	
	if ($('#saveLocalBackup').length == 0 || $('#saveLocalBackup').is(":checked")) {

		var survey = getSurveyIdentifier();
				
		$(parent).find("input").each(function(index) {
			var type = $(this).attr("type");
			var id = $(this).attr("data-id");
			var value = readCookie(survey+id);

			if (value != null && value.length > 0 && ($(this).attr("id") == null || ($(this).attr("id") != 'hp-7fk9s82jShfgak' && $(this).attr("id") != 'j_captcha_response'))) {
				if (type == "text") {
					$(this).val(value);
				} else if (type == "hidden" && $(this).attr("data-id") && ($(this).attr("id") == null || !strStartsWith($(this).attr("id"), 'regex'))) {
					$(this).val(value);		
					
					if ($(this).attr("data-type") == "rating")
					{
						updateRatingIcons(parseInt(value)-1, $(this).parent());
					}					
				} else if (type == "radio" || type == "checkbox") {
					if (value == "true") {
						$(this).prop("checked","checked");
						$(this).closest(".possible-answer").addClass("selected-choice");
						checkDependencies(this);
					}
				}
			}					
		});
		
		$(parent).find("textarea").each(function(index){
			if ($(this).attr("data-id"))
			{
				var id = $(this).attr("data-id");
				var s = readCookie(survey+id);
				if (s != null && s.length > 0)
				$(this).val(s);
			}
		});
		
		$(parent).find("select").each(function(index){
			
			if ($(this).attr("id") == null || $(this).attr("id") != 'langSelectorRunner')
			{
				var id = $(this).attr("data-id");
				var s = readCookie(survey+id);
				if (s != null && s.length > 0) {
					$(this).val(s);
					checkDependencies(this);
				}
			}
		});
	}	
}

function getSurveyIdentifier() {
	var survey = $(document.getElementById("survey.id")).val();
	var invitation = $(document.getElementById("invitation")).val();
	var uniqueCode = $(document.getElementById("uniqueCode")).val();
	var draftId = $(document.getElementById("draftid")).val();
	
	if (invitation.length > 0 && uniqueCode.length > 0) {
		return survey + "." + uniqueCode;
	} else if (draftId.length > 0) {
		return survey + "." + draftId;
	}
	
	return survey + ".";
}

function saveCookies() {
	
	if (!is_local_storage_enabled() || ($('#saveLocalBackup').length > 0 && !$('#saveLocalBackup').is(":checked"))) {
		return;
	}
		
	var survey = getSurveyIdentifier();	
	
	$("input").each(function(index){
		
		var type = $(this).attr("type");
		var id = $(this).attr("data-id");
		
		if (!$(this).hasClass("comparable-second"))
		{
			if ($(this).attr("id") == null || ($(this).attr("id") != 'hp-7fk9s82jShfgak' && $(this).attr("id") != 'j_captcha_response')) {
				if (type == "text" || type == "hidden" || type == "password") {
					createCookie(survey+id, $(this).val(), 7);
				} else if (type == "radio" || type == "checkbox") {
					createCookie(survey+id, $(this).is(":checked"), 7);		
				}
			}
		}
	});
	
	$("textarea").each(function(index){
		if (!$(this).hasClass("comparable-second"))
		{
			var id = $(this).attr("data-id");
			createCookie(survey+id, $(this).val(), 7);
		}
	});
	
	$("select").each(function(index){
		var id = $(this).attr("data-id");
		createCookie(survey+id, $(this).val(), 7);
	});	
	
	window.setTimeout("saveCookies()", 60000);
}

function clearAllCookies(surveyprefix) {
	if (!is_local_storage_enabled()) {
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

function checkLocalBackup() {
	if (!$('#saveLocalBackup').is(":checked")) {
		clearCookies();
	} else {
		window.setTimeout("saveCookies()", 60000);
	}
}

function clearCookies() {
	if (!is_local_storage_enabled()) {
		return;
	}
	
	var survey = getSurveyIdentifier();
	
	$("input").each(function(index){
		
		var type = $(this).attr("type");
		var id = $(this).attr("data-id");
		
		if (type == "text" || type == "hidden") {
			//createCookie(survey+id, "",-1);
			eraseCookie(survey+id);
		} else if (type == "radio" || type == "checkbox") {
			//createCookie(survey+id,"",-1);		
			eraseCookie(survey+id);
		}
	});
	
	$("textarea").each(function(index){
		var id = $(this).attr("data-id");
		//createCookie(survey+id, "",-1);
		eraseCookie(survey+id);
	});	
	
	window.setTimeout("saveCookies()", 60000);
}

function createCookie(name,value,days) {
	if (value != null && typeof value != "undefined"
			&& (typeof value != "string" || value.length > 0)) {
		try {
			localStorage.setItem(name, value);
		} catch(e) {
		    if(e.name == "NS_ERROR_FILE_CORRUPTED") {
		    	showError("Sorry, it looks like your browser storage has been corrupted. Please clear your storage by going to Tools -> Clear Recent History -> Cookies and set time range to 'Everything'. This will remove the corrupted browser storage across all sites.");
		    }
		}		
	 }
}

function readCookie(name) {
	return localStorage.getItem(name);
}

function eraseCookie(name) {
	localStorage.removeItem(name);
}

function is_local_storage_enabled() {
	if (typeof (Storage) !== "undefined") {
    var ABCD=0;
		try {
			localStorage.setItem("EUSurvey_areCookiesThere", 1234);
		} catch (e) {
		}
		try {
			ABCD = localStorage.getItem("EUSurvey_areCookiesThere");
		} catch (e) {
		}
	  return ABCD==1234;
	} else {
	  return false;
	}
}
