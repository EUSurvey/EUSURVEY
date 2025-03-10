<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">

<head>
<title>EUSurvey - <spring:message code="label.Support" />
</title>
	<%@ include file="../includes.jsp" %>	
	<link href="${contextpath}/resources/css/yellowfocus.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css"></link>
		
	<script type="text/javascript">
		function checkAndSubmit()
		{
			let supportForm = $("#supportForm")
			$(".validation-error").remove();
			var result = validateInput(supportForm);

			saveFormInputs(supportForm)
			
			 if ($(".g-recaptcha.unset").length > 0)	{
				$('#runner-captcha-error').show();
				return;
			 }
			
			if (result == false)
			{
				goToFirstValidationError(supportForm);
			} else {
				supportForm.submit();
			}			
		}

		function saveFormInputs(form){
			let formInputs = form.find("input, textarea, select")

			let saveObject = {}

			formInputs.each(function(){
				let element = $(this);
				let id = element.attr("id")
				if (id != null && !element.is("#captchadiv *, [type='hidden']")){
					saveObject[id] = element.val()
				}
			});

			saveObject["file-uploader-support-div"] = $("#file-uploader-support-div").html()

			window.sessionStorage.setItem("supportFormSave", JSON.stringify(saveObject))
		}

		$(document).ready(function(){
			//Run async because the runner-captcha-error is set visible in another document.ready function
			window.setTimeout(function() {
				let captchaError = $("#runner-captcha-error")
				if (captchaError.is(":visible")) {

					let supportFormSave = window.sessionStorage.getItem("supportFormSave")
					if (supportFormSave) {
						supportFormSave = JSON.parse(supportFormSave)

						$.each(supportFormSave, function (key, value) {
							if (key == "file-uploader-support-div") {
								$("#" + key).html(value)
							} else {
								$("#" + key).val(value)
							}

							if (key == "contactreason"){
								showHideAdditionalInfo()
							}
						})
					}
				}
			}, 100) //0 works too, tested in ff and chrome but you never know
		})
		
		function showHideAdditionalInfo()
		{
			if ($("#erroroption").is(":selected"))
			{
				$("#additionalinfodiv").show();
				$("#additionalsurveyinfodiv").show();
			} else if ($("#highaudienceoption").is(":selected")){
				$("#additionalinfodiv").hide();
				$("#additionalsurveyinfodiv").show();
			} else {
				$("#additionalinfodiv").hide();
				$("#additionalsurveyinfodiv").hide();
			}
		}
	
		function toggleAdditionalInfo(checkbox)
		{
			if ($(checkbox).is(":checked"))
			{
				$("#additionalinfo").removeAttr("readonly");
			} else {
				$("#additionalinfo").prop("readonly", "readonly");
			}
		}
		
		function deleteUploadedFile(button)
		{
			var uid = $(button).closest(".uploadedfile").attr("data-id");
			var request = $.ajax({
				  url: contextpath + "/home/support/deletefile",
				  data: {uid : uid},
				  cache: false,
				  dataType: "json",
				  success: function(data)
				  {
					  if (!data.success)
					  {
						showError("the file could not be deleted from the server");			 
					  } else {
						  $(button).closest(".uploadedfile").remove();
						  
						  if ($(".uploadedfile").length < 2)
				    	  {
				    		$("#file-uploader-support").show();
				    	  }
					  }		
				  }
				});
		}
		
		$(function() {
			$("[data-toggle]").tooltip();
			showHideAdditionalInfo();
			
			var uploader = new qq.FileUploader({
			    element: $("#file-uploader-support")[0],
			    action: contextpath + '/home/support/uploadfile',
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
			    		$("#file-uploader-support-div").append("<div class='uploadedfile' data-id='" + responseJSON.uid + "'>" + responseJSON.name + "<a href='#' onclick='deleteUploadedFile(this); return false;'><span class='glyphicon glyphicon-trash'></span></a><input type='hidden' name='uploadedfile' value='" + responseJSON.uid + "' /><input type='hidden' name='uploadedfilename' value='" + responseJSON.name + "' /></div>")
				    	$("#file-uploader-support-div").show();
			    		
			    		if ($(".uploadedfile").length > 1)
			    		{
			    			$("#file-uploader-support").hide();
			    		}
			    	} else {
			    		showError(invalidFileError);
			    	}
				},
				showMessage: function(message){
					$("#file-uploader-support").append("<div class='validation-error'>" + message + "</div>");
				},
				onUpload: function(id, fileName, xhr){
					$("#file-uploader-support").find(".validation-error").remove();			
				}
			});
			
			$(".qq-upload-button").addClass("btn btn-default").removeClass("qq-upload-button");
			$(".qq-upload-list").hide();
			$(".qq-upload-drop-area").css("margin-left", "-1000px");
		});
	</script>
	
	<style type="text/css">
		.uploadedfile {
			margin-top: 10px;
		}
		
		.uploadedfile a {
			color: #f00;
			margin-left: 15px;
		}
	</style>
	
	<c:if test="${runnermode != null }">
		<script type="text/javascript">
			$(function() {
				 $(".headerlink, .header a").each(function(){
					 if ($(this).attr("href").indexOf("?") == -1)
					$(this).attr("href", $(this).attr("href") + "/runner");
				 });
			});
		</script>
	</c:if>
</head>

<body id="bodyHelpSupport">
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>		
	
		<c:choose>
			<c:when test="${USER != null && runnermode == null && responsive == null}">
				<%@ include file="../menu.jsp" %>	
				<div class="page" style="padding-top: 110px">
			</c:when>
			<c:when test="${responsive != null}">
				<div class="page"
					style="width: 100%; padding: 10px; padding-top: 40px;">
			</c:when>
			<c:otherwise>
				<div class="page" style="padding-top: 40px">
			</c:otherwise>
		</c:choose>	
		
			<div class="pageheader">
			<h1>
				<spring:message code="label.Support" />
			</h1>
			</div>
		<c:choose>
			<c:when test="${oss}">
				<spring:message code="support.oss.text" />
			</c:when>
			<c:otherwise>
				<span class="underlined">
				<spring:message code="support.checkfaq" arguments="${contextpath}/home/documentation" /></span>	<br /><br />
				
				<form:form id="supportForm" method="POST" action="${contextpath}/home/support?${_csrf.parameterName}=${_csrf.token}" enctype="multipart/form-data" modelAttribute="form">
					<label id="contactreasonlabel"><span class="mandatory">*</span><spring:message code="support.ContactReason" /></label><br />
					<select class="form-control" onchange="showHideAdditionalInfo()" style="max-width: 425px" name="contactreason" id="contactreason" aria-labelledby="contactreasonlabel">
						<option value="generalquestion" id="generaloption"><spring:message code="support.GeneralQuestion" /></option>
						<option value="technicalproblem" id="erroroption"><spring:message code="support.TechnicalProblem" /></option>
						<option value="idea"><spring:message code="support.idea" /></option>
						<option value="assistance" id="assistanceoption"><spring:message code="support.assistance" /></option>
						<option value="accessibility"><spring:message code="support.Accessibility" /></option>
						<option value="dataprotection" id="dataprotectionoption"><spring:message code="support.DataProtection" /></option>
						<option value="highaudience" id="highaudienceoption"><spring:message code="support.HighAudience" /></option>
						<option value="organisation" id="organisationoption"><spring:message code="support.Organisation" /></option>
						<option value="otherreason"><spring:message code="support.otherreason" /></option>						
					</select><br /><br />
					<script>
						if (window.location.search.toLowerCase().includes("error")){
							document.getElementById("erroroption").setAttribute("selected", "selected")
						} else if (window.location.search.toLowerCase().includes("assistance")){
							document.getElementById("assistanceoption").setAttribute("selected", "selected")
						} else if (window.location.search.toLowerCase().includes("highaudience")){
							document.getElementById("highaudienceoption").setAttribute("selected", "selected")
						} else if (window.location.search.toLowerCase().includes("dataprotection")) {
							document.getElementById("dataprotectionoption").setAttribute("selected", "selected")
						} else if (window.location.search.toLowerCase().includes("organisation")) {
							document.getElementById("organisationoption").setAttribute("selected", "selected")
						} else {
							document.getElementById("generaloption").setAttribute("selected", "selected")
						}
					</script>
					
					<div id="additionalsurveyinfodiv">
						<p><spring:message code="support.additionalsurveyinfonew" />:</p>
						<label><spring:message code="skin.SurveyTitle" /></label><br />
						<textarea class="form-control" rows="3" style="width: 425px" name="additionalsurveyinfotitle" id="additionalsurveyinfotitle" ></textarea><br />
						<label><spring:message code="label.SurveyAlias" /></label><br />
						<input type="text" class="form-control" style="width: 425px" name="additionalsurveyinfoalias" id="additionalsurveyinfoalias" /><br /><br />
					</div>
					
					<label id="namelabel"><span class="mandatory">*</span><spring:message code="label.yourname" /></label><br />
					<input type="text" class="form-control required" style="width: 425px" name="name" id="yourname" aria-labelledby="namelabel" value='${USER != null ? USER.getFirstLastName() : "" }' /><br /><br />
					
					<label id="emaillabel"><span class="mandatory">*</span><spring:message code="label.youremail" /></label> <span class="helptext">(<spring:message code="support.forlatercontact" />)</span><br />
					<input type="text" class="form-control required" style="width: 425px" id="supportemail" name="email" aria-labelledby="emaillabel" value='${USER != null ? USER.getEmail() : "" }' /><br /><br />
					
					<label id="subjectlabel"><span class="mandatory">*</span><spring:message code="support.subject" /></label><br />
					<input type="text" class="form-control required" name="subject" aria-labelledby="subjectlabel" style="width: 425px" id="supportsubject" /><br /><br />
							
					<label id="messagelabel"><span class="mandatory">*</span><spring:message code="support.yourmessagetous" /></label>
					<div class="helptext"><spring:message code="support.yourmessagetoushelp" /></div>
					<textarea class="form-control required" rows="10" name="message" id="supportmessage" aria-labelledby="messagelabel"></textarea><br /><br />
					
					<div id="additionalinfodiv">
						<label><spring:message code="support.additionalinfo" /></label>
						<div class="helptext"><spring:message code="support.additionalinfohelp" /></div>
						<textarea class="form-control" rows="6" name="additionalinfo" id="additionalinfo" readonly="readonly">${additionalinfo}</textarea>
						<input type="checkbox" onclick="toggleAdditionalInfo(this)" id="additionalinfoedit"/> <spring:message code="label.ClickToEdit" /><br /><br />
					</div>
							
					<label><spring:message code="support.upload" /></label>
					<a role="button" data-toggle="tooltip" title="<spring:message code="support.maxfilesize" />" aria-label="<spring:message code="support.maxfilesize" />"><span class="glyphicon glyphicon-question-sign"></span></a>
					<div id="file-uploader-support"></div>
					<div id="file-uploader-support-div"></div>
					
					<div class="captcha" style="margin-left: 0px; margin-bottom: 20px; margin-top: 40px;">			
						<c:if test="${!captchaBypass}">
						<%@ include file="../captcha.jsp" %>					
						</c:if>
			       	</div>
			       	<span id="error-captcha" class="validation-error hideme">
			       		<c:if test="${!captchaBypass}">
			       		<spring:message code="message.captchawrongnew" />
			       		</c:if>
			       	</span>
			       	
			       	<div style="text-align: center; margin: 50px;">
			       		<a href="javascript:;" class="btn btn-primary" onclick="checkAndSubmit()"><spring:message code="label.Submit" /></a>
			       	</div>
			    </form:form>
		    </c:otherwise>
		</c:choose>
		</div>
	</div>

	<%@ include file="../footer.jsp" %>
	
	<c:if test="${USER == null || runnermode != null || responsive != null}">
		<%@ include file="../generic-messages.jsp" %>
	</c:if>
</body>

</html>