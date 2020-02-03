$(function() {	
	$("#form-menu-tab").addClass("active");
	$("#properties-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");

	$("#survey\\.allowedContributionsPerUser").spinner({ decimals:0, min:1, start:"", allowNull: true });

	checkSecurity();
	checkEcasSecurity();
	$("#save-form").on("submit", function(){				
		setCombinedSecPriv("radio-new-survey","input#survey-security");		
	});
	
	$('[data-toggle="tooltip"]').tooltip({
		trigger : 'hover'
	});
		
	$("input").click(function(){
		if ($(this).attr("type") != "button")
		unsavedChanges = true;
	});			
	$("input").keydown(function(){
		unsavedChanges = true;
	});			
	$("input").change(function(){
		unsavedChanges = true;
	});		
	checkSelections();	
	checkNotification();
	
	var uploader = new qq.FileUploader({
	    element: $("#file-uploader-logo")[0],
	    action: contextpath + '/${sessioninfo.shortname}/management/uploadimage',
	    uploadButtonText: selectFileForUpload,
	    params: {
	    	'_csrf': csrftoken
	    },
	    multiple: false,
	    cache: false,
	    sizeLimit: 1048576,
	    onComplete: function(id, fileName, responseJSON)
		{
	    	if (responseJSON.success)
	    	{
		    	$("#logo-cell").find("img").remove();
		    	var img = document.createElement("img");
		    	$(img).attr("src", contextpath + "/files/" + surveyUniqueId +  "/" + responseJSON.id);
		    	$(img).attr("width",responseJSON.width);
		    	$(img).attr("data-width",responseJSON.width);
		    	$("#logo-cell").find("img").remove();
		    	$("#logo-cell").find("p").remove();
		    	$("#logo-cell").prepend("<p>" + responseJSON.name + "</p>");
		    	$("#logo-cell").prepend(img);
		    	$("#logo").val(responseJSON.id);
		    	$("#logo-cell").find(".disabled").removeClass("disabled");
		    	$("#file-uploader-area-div").show();
	    	} else {
	    		showError(invalidFileError);
	    	}
		},
		showMessage: function(message){
			$("#file-uploader-logo").append("<div class='validation-error'>" + message + "</div>");
		},
		onUpload: function(id, fileName, xhr){
			$("#file-uploader-logo").find(".validation-error").remove();			
		}
	});
	
	$(".qq-upload-button").addClass("btn btn-default").removeClass("qq-upload-button");
	$(".qq-upload-list").hide();
	$(".qq-upload-drop-area").css("margin-left", "-1000px");
	
	checkAutomaticPublishing();
	checkValidationPerPage();
	
	if ($("#usefullinkstable").find("tr").length == 1)
	{
		addLinksRow();
	};
	
	if ($("#backgrounddocumentstable").find("tr").length == 1)
	{
		addDocRow();
	};	
	
});

function checkCaptcha()
{
	if ($("#survey\\.captcha1").length == 0) return;
	
	if ($("#radio-new-survey-security-open").is(":checked") && $("#survey\\.listForm1").is(":checked"))
	{
		$("#survey\\.captcha1").prop("checked", "checked");
		$("#survey\\.captcha1")[0].disabled = true;
		$("#survey\\.captcha2")[0].disabled = true;
	} else {
		$("#survey\\.captcha1")[0].disabled = false;
		$("#survey\\.captcha2")[0].disabled = false;
	}
}

function checkQuiz(update, updatetinymce)
{
	if ($("#survey\\.isQuiz1").is(":checked"))
	{
		$("#survey\\.showTotalScore1")[0].disabled = false;
		$("#survey\\.showTotalScore2")[0].disabled = false;
		
		$("#survey\\.showQuizIcons1")[0].disabled = false;
		$("#survey\\.showQuizIcons2")[0].disabled = false;
		
		if (updatetinymce)
			$("#edit-prop-tabs-7").find("textarea").each(function(){
				var id = $(this).attr("id");
				try {
					tinyMCE.get(id).setMode('design');
				} catch (e) {}	
			});
		
		if ($("#survey\\.showTotalScore2").is(":checked"))
		{
			$("#survey\\.scoresByQuestion1")[0].disabled = true;
			$("#survey\\.scoresByQuestion2")[0].disabled = true;
		} else {
			$("#survey\\.scoresByQuestion1")[0].disabled = false;
			$("#survey\\.scoresByQuestion2")[0].disabled = false;
		}
	} else {
		$("#survey\\.showTotalScore1")[0].disabled = true;
		$("#survey\\.showTotalScore2")[0].disabled = true;
		$("#survey\\.scoresByQuestion1")[0].disabled = true;
		$("#survey\\.scoresByQuestion2")[0].disabled = true;
		
		$("#survey\\.showQuizIcons1")[0].disabled = true;
		$("#survey\\.showQuizIcons2")[0].disabled = true;
		
		if (updatetinymce)
			$("#edit-prop-tabs-7").find("textarea").each(function(){
				var id = $(this).attr("id");
				try {
					tinyMCE.get(id).setMode('readonly');
				} catch (e) {}
				$(".mce-content-body").css("color","#777");
			});
	}
}

function addLinksRow()
{
	var tr = document.createElement("tr");
	var td = document.createElement("td");
	var input = document.createElement("input");
	
	$(input).attr("type","text").attr("maxlength","250").addClass("xhtml freetext max250").attr("name","linklabel" + ($("#usefullinkstable").find("tr").length-1));
	$(td).append(input);
	$(tr).append(td);
	
	td = document.createElement("td");
	input = document.createElement("input");
	$(input).attr("type","text").addClass("targeturl").attr("maxlength","255").attr("name","linkurl" + ($("#usefullinkstable").find("tr").length-1));
	$(td).append(input);
	$(tr).append(td);
	
	td = document.createElement("td");
	$(td).css("vertical-align","middle").append('<a data-toggle="tooltip" title="Remove useful link" class="btn btn-default btn-xs" href="#" onclick="$(this).parent().parent().remove()"><span class="glyphicon glyphicon-remove"></span></a>');
	$(tr).append(td);
	$(td).find("a").tooltip();
	
	$("#usefullinkstable").append(tr);
}

function addDocRow()
{
	var tr = document.createElement("tr");
	var td = document.createElement("td");
	var input = document.createElement("input");
	
	$(input).attr("type","text").addClass("xhtml freetext max235").attr("maxlength","235").attr("name","doclabel" + ($("#backgrounddocumentstable").find("tr").length-1));
	$(td).append(input);
	$(tr).append(td);
	
	td = document.createElement("td");
	input = document.createElement("input");
	$(input).attr("type","hidden").attr("name","docurl" + ($("#backgrounddocumentstable").addClass("xhtml").find("tr").length-1));
	$(td).append(input);
	
	var div = document.createElement("div");
	var id = "docid" + ($("#backgrounddocumentstable").find("tr").length-1);
	$(div).attr("id",id);
	$(td).append(div);
	var uploader = new qq.FileUploader({
	    element: div,
	    action: contextpath + '/' + surveyShortname + '/management/upload',
	    uploadButtonText: selectFileForUpload,
	    params: {
	    	'_csrf': csrftoken
	    },
	    multiple: false,
	    cache: false,
	    sizeLimit: 10485760,
	    onComplete: function(id, fileName, responseJSON)
		{
	    	var a = document.createElement("a");
	    	$(a).attr("href", contextpath + "/files/" + surveyUniqueId + "/" + responseJSON.id);
	    	$(a).append(responseJSON.name);
	    	
	    	var i = $(div).closest("tr").find("td").first().find("input[type=text]");
	    	if ($(i).val().length == 0)
	    	$(i).val(responseJSON.name);
	    	
	    	$(div).parent().append(a);
	    	$(div).parent().find("input[type='hidden']").val(contextpath + "/files/" + surveyUniqueId + "/" + responseJSON.id);
	    	$(div).remove();
		}
	});
	
	$(div).find(".qq-upload-button").addClass("btn btn-default").removeClass("qq-upload-button");
	$(".qq-upload-list").hide();
	$(".qq-upload-drop-area").css("margin-left", "-1000px");			
	
	$(tr).append(td);
	
	td = document.createElement("td");
	var a = document.createElement("a");
	$(a).attr("data-toggle","tooltip").attr("title","Remove useful link").addClass("btn btn-default btn-xs").append('<span class="glyphicon glyphicon-remove"></span>').click(function(){
		var v = $(this).closest("tr").find("input[type='hidden']").val();
		$(this).parent().parent().remove();
		if (typeof v != 'undefined' && v.length > 0) {
			deleteFile(v);
		}
	});
	$(td).css("vertical-align","middle").append(a);
	$(tr).append(td);
	$(a).tooltip();
	
	$("#backgrounddocumentstable").append(tr);
}

function deleteFile(url)
{
	var uid = url.substring(url.lastIndexOf("/")+1);
	
	var request = $.ajax({
	  url: contextpath + "/noform/management/deleteFile",
	  data: {uid : uid, suid : surveyUniqueId},
	  cache: false,
	  dataType: "json"
	});
}

function checkPropertiesSurveyContactType()
{
	if ($("#survey-contact-type").val() == "email")
	{
		$("#survey\\.contact").addClass("email");
		$("#survey\\.contact").removeClass("url");
		$("#survey\\.contactLabel").hide();
		$("#survey-contact-label-label").hide();
	} else {
		$("#survey\\.contact").removeClass("email");
		$("#survey\\.contact").addClass("url");
		$("#survey-contact-label-label").show();
		$("#survey\\.contactLabel").show();
	}
}

function parseDateTime(input, time) {
  var parts = input.split('/');
  // new Date(year, month [, date [, hours[, minutes[, seconds[, ms]]]]])
  
  if (time == null)
  {
	  return new Date(parts[2], parts[1]-1, parts[0]); // months are 0-based
  } else {
	  var parts2 = time.split(':')
	  
	  return new Date(parts[2], parts[1]-1, parts[0], parts2[0]); // months are 0-based
  }		
}

function checkConfirmationPage()
{			
	if ($("#conflink").is(":checked"))
	{} else {};
}

function checkEscapePage()
{
	var id = "survey.escapePage";
	if ($("#esclink").is(":checked"))
	{} else {};
}

function publishConfirmationClose()
{
	$('#publishConfirmationDialog').modal('hide');
	$('#publishConfirmationDialog2').modal('hide');
	$('#publishConfirmationDialog3').modal('hide');
	$('#publishConfirmationDialog4').modal('hide');
	$('#edit-properties-dialog').modal('show');
}

function publishConfirmationOkClicked()
{
	$('#publishConfirmationDialog').modal('hide');
	$('#publishConfirmationDialog2').modal('hide');
	$('#publishConfirmationDialog3').modal('hide');
	$('#publishConfirmationDialog4').modal('hide');
	$('#edit-properties-dialog').modal('show');
	unsavedChanges=false;
	validateInputAndSubmit($('#save-form'));
}

function addMaxContributionDisplayer() {
	$('#maxNumberContributionDisplayer').removeClass('hideme');
}

function removeMaxContributionDisplayer() {
	$('#maxNumberContributionDisplayer').addClass('hideme');
}

function checkAutomaticPublishing()
{
	if ($("#autopub").is(":checked"))
	{
		$(".autopub").show();
		$("#survey\\.start").addClass("required");
		$("#survey\\.start").removeAttr("disabled"); 
		$("#survey\\.end").addClass("required");
		$("#survey\\.end").removeAttr("disabled"); 
		$("#startHour").removeAttr("disabled"); 
		$("#endHour").removeAttr("disabled"); 
		$("#survey\\.start").datepicker( "option", "disabled", false ); 
		$("#survey\\.end").datepicker( "option", "disabled", false );
	} else {
		$(".autopub").hide();
		$("#survey\\.start").removeClass("required");
		$("#survey\\.end").removeClass("required");
		$("#survey\\.start").parent().find(".validation-error").remove();
		$("#survey\\.end").parent().find(".validation-error").remove();
		$("#survey\\.start").attr("disabled", "disabled");  
		$("#survey\\.end").attr("disabled", "disabled"); 
		$("#startHour").attr("disabled", "disabled");
		
		if (!isOPC)
		{
			$("#endHour").attr("disabled", "disabled"); 
			$("#survey\\.end").datepicker( "option", "disabled", true );
		} else {
			$("#endHour").removeAttr("disabled"); 
			$("#survey\\.end").datepicker( "option", "disabled", false );
		}
		
		$("#survey\\.start").datepicker( "option", "disabled", true ); 	
	}
	

	$("#survey\\.start").datepicker('option', 'dateFormat', "dd/mm/yy");
	$("#survey\\.end").datepicker('option', 'dateFormat', "dd/mm/yy");
}


function checkValidationPerPage()
{
	if ($('#survey\\.multiPaging1').is(":checked"))
	{
		$('#survey\\.validatedPerPage1').removeAttr("disabled");
		$('#survey\\.validatedPerPage2').removeAttr("disabled");
		$('#survey\\.validatedPerPage1').addClass("required");
		$('#survey\\.validatedPerPage2').addClass("required");
		
		if (!$('#survey\\.validatedPerPage1').is(":checked"))
		{
			$('#survey\\.validatedPerPage2').prop("checked","checked");
		}
	} else {
		$('#survey\\.validatedPerPage1').attr("disabled", "disabled"); 
		$('#survey\\.validatedPerPage2').attr("disabled", "disabled"); 
		$('#survey\\.validatedPerPage1').removeAttr("checked");
		$('#survey\\.validatedPerPage2').removeAttr("checked");
		$('#survey\\.validatedPerPage1').removeClass("required");
		$('#survey\\.validatedPerPage2').removeClass("required");
	}
}

function checkShowPassword(input)
{
	if ($(input).is(":checked"))
	{
		$("#survey\\.password").hide();
		$("#clearpassword").show();
	} else {
		$("#clearpassword").hide();
		$("#survey\\.password").show();
	}
}

function checkShowPublicationPassword(input)
{
	if ($(input).is(":checked"))
	{
		$("#clearpublicationpassword").show();
	} else {
		$("#clearpublicationpassword").hide();
	}
}


function activate(link)
{
	$("#tablink1").removeClass("grey-background");
	$("#tablink2").removeClass("grey-background");
	$("#tablink3").removeClass("grey-background");
	$("#tablink4").removeClass("grey-background");
	$("#tablink5").removeClass("grey-background");
	$("#tablink6").removeClass("grey-background");
	$("#tablink7").removeClass("grey-background");
	$("#tablink8").removeClass("grey-background");
	$(link).addClass("grey-background");
}

function checkNotification()
{
	if ($("#notificationselector1").prop("checked"))
	{			
		$("#notificationselector1").parent().find("select").removeAttr('disabled');
		$("#notificationselector1").parent().find(".check").last().removeAttr('disabled');
		$("#notificationselector1").parent().find(".check").last().prev().removeAttr('disabled');
		
		$("#survey\\.start").addClass("required");
	} else {
		$("#notificationselector1").parent().find("select").prop('disabled','disabled');
		$("#notificationselector1").parent().find(".check").last().prop('disabled','disabled');
		$("#notificationselector1").parent().find(".check").last().prev().prop('disabled','disabled');
		
		$("#survey\\.start").removeClass("required");
	}
}

function checkSecurity()
{
	var i = document.getElementById("radio-new-survey-security-open");

	if ($(i).prop("checked"))
	{	
		$("#edit-prop-tabs-2").find("input[type='password']").prop('disabled','disabled');	
		$("#ecas-mode-all").prop('disabled','disabled');
		$("#ecas-mode-internal").prop('disabled','disabled');
		$("#enableecas").prop('disabled','disabled');
	} else {
		$("#edit-prop-tabs-2").find("input[type='password']").removeAttr('disabled');		
		$("#enableecas").removeAttr('disabled');
	}
}

function checkEcasSecurity()
{
	var i = document.getElementById("enableecas");

	if ($(i).prop("checked"))
	{			
		$("#ecas-mode-all").removeAttr('disabled');
		$("#ecas-mode-internal").removeAttr('disabled');
		
		if (!$("#ecas-mode-internal").is(":checked"))
		{
			$("#ecas-mode-all").prop("checked","checked");
		}
		
		$("#survey\\.allowedContributionsPerUser").removeAttr('disabled');
		$("#survey\\.allowedContributionsPerUser").spinner( "option", "disabled", false);
		
	} else {
		$("#ecas-mode-all").prop('disabled','disabled');
		$("#ecas-mode-internal").prop('disabled','disabled');
		$("#survey\\.allowedContributionsPerUser").prop('disabled','disabled');
		$("#survey\\.allowedContributionsPerUser").spinner( "option", "disabled", true);
	}
}

function editProperties()
{			
	$('#edit-prop-tabs-1').hide();
	$('#edit-prop-tabs-2').hide();
	$('#edit-prop-tabs-3').hide();
	$('#edit-prop-tabs-4').hide();
	$('#edit-prop-tabs-5').hide();
	$('#edit-prop-tabs-6').hide();
	$('#edit-prop-tabs-7').hide();
	$('#edit-prop-tabs-8').hide();
	$("#publish-results-error").hide();
	$("#properties-save-button").show();
	
	if ($('#prop-tabs-1').hasClass("active"))
	{
		$('#selected-tab').val("1");
		$('#edit-prop-tabs-1').show();
		$('#dialog-title').text($('#tablink1').text());
	} else if ($('#prop-tabs-2').hasClass("active")) {
		$('#selected-tab').val("2");
		$('#edit-prop-tabs-2').show();
		$('#dialog-title').text($('#tablink2').text());
	} else if ($('#prop-tabs-3').hasClass("active")) {
		$('#selected-tab').val("3");
		$('#edit-prop-tabs-3').show();
		$('#dialog-title').text($('#tablink3').text());
	} else if ($('#prop-tabs-4').hasClass("active")) {
		$('#selected-tab').val("4");
		$('#edit-prop-tabs-4').show();
		$('#dialog-title').text($('#tablink4').text());
	} else if ($('#prop-tabs-5').hasClass("active")) {
		$('#selected-tab').val("5");
		$('#edit-prop-tabs-5').show();
		$('#dialog-title').text($('#tablink5').text());
	} else if ($('#prop-tabs-7').hasClass("active")) {
		$('#selected-tab').val("7");
		$('#edit-prop-tabs-7').show();
		$('#dialog-title').text($('#tablink7').text());
		checkQuiz(false, true);
	} else if ($('#prop-tabs-8').hasClass("active")) {
		$('#selected-tab').val("8");
		$('#edit-prop-tabs-8').show();
		$('#dialog-title').text($('#tablink8').text());
		checkQuiz(false, true);
	} else {
		$('#selected-tab').val("6");
				  
	    if (!hasPendingChangesForPublishing())
	    {
			$('#edit-prop-tabs-6').show();
			$('#dialog-title').text($('#tablink6').text());
	    } else {
		  $("#publish-results-error").show();
		  $("#properties-save-button").hide();
		  $("#properties-cancel-button").addClass("btn-primary");
	    }		
	
	}
	$('#edit-properties-dialog').modal('show');
}

function checkSelections()
{
	if ($("#questionsToPublishAll").is(":checked"))
	{
		$("#questionsToPublishDiv").find("input").each(function(){
			$(this).attr("disabled","disabled");
		});
	} else {
		$("#questionsToPublishDiv").find("input").each(function(){
			$(this).removeAttr("disabled");
		});
	}
	
	if ($("#contributionsToPublishAll").is(":checked"))
	{
		$("#contributionsToPublishDiv").find("input").each(function(){
			$(this).attr("disabled","disabled");
		});
	} else {
		$("#contributionsToPublishDiv").find("input").each(function(){
			$(this).removeAttr("disabled");
		});
	}
}