<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Info" /></title>
	<%@ include file="../includes.jsp" %>
		<c:if test="${runnermode != null }">
			<script type="text/javascript">
				$(function() {
					 $(".headerlink, .header a").each(function(){
						 if (!$(this).hasClass("messageicon") && $(this).attr("id") != 'logoutBtnFromHeader'  && !$(this).hasClass("skipScriptAnchor"))
						 {
							if ($(this).attr("href") && $(this).attr("href").indexOf("?") == -1)
							{
							 $(this).attr("target","_blank").attr("href", $(this).attr("href") + "/runner");
							}
						 }				
					 });
				});
			</script>
		</c:if>
		
		<script type="text/javascript">
			function startExport()
			{
				$("#ask-export-dialog").find(".validation-error").hide();
				$("#ask-export-dialog").find(".validation-error-keep").hide();
				
				var mail = $("#email").val();
				if (mail.trim().length == 0 || !validateEmail(mail))
				{
					$("#ask-export-dialog-error").show();
					return;
				};	
				
				var challenge = getChallenge();
			    var uresponse = getResponse();
			    
			    if (uresponse.trim().length == 0)
			    {
			    	$("#runner-captcha-empty-error").show();
			    	return;
			    }
			    
			    var data = {email : mail, recaptcha_challenge_field : challenge, 'g-recaptcha-response' : uresponse};
				if ($('#captcha_token').length > 0) {
					data["captcha_token"] =  $('#captcha_token').val();
					data["captcha_id"] =  $('#captcha_id').val();
					data["captcha_useaudio"] =  $('#captcha_useaudio').val();
				}
			
				$.ajax({
					type:'GET',
					  url: "${contextpath}/runner/createanswerpdf/${caseidforpdfdownload}",
					  data: data,
					  cache: false,
					  success: function( data ) {
						  
						  if (data == "success") {
								$('#ask-export-dialog').modal('hide');
								showSuccess(message_PublicationExportSuccess);
						  	} else if (data == "errorcaptcha") {
						  		$("#runner-captcha-error").show();
						  		reloadCaptcha();
							} else {
								showExportFailureMessage();
								reloadCaptcha();
							};
					}
				});				
				
			}
		</script>
</head>
<body style="text-align: center;">
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>		
		
		<c:choose>
			<c:when test="${USER != null && noMenu == null && runnermode == null}">			
				<%@ include file="../menu.jsp" %>	
			</c:when>
			<c:otherwise>
				<%@ include file="../generic-messages.jsp" %>
			</c:otherwise>
		</c:choose>			
			
		<div class='${responsive != null ? "responsivepage" : "page"}' style='padding: 20px; padding-top: 80px; padding-bottom: 100px; max-width: 600px; margin-left: auto; margin-right: auto'>
		
			<c:if test="${skipErrorLabel == null}">	
				<div class="hidden-xs" style="float: left; height: 200px;">
					<span class="glyphicon glyphicon-alert" style="font-size: 100px; color: #bd281d; margin-right: 60px; margin-top: 10px"></span>
				</div>
			</c:if>
			<div style="text-align: left;">				
				<c:if test="${skipErrorLabel == null}">				
					<h1 style="margin-bottom: 20px;">
						<div class="visible-xs" style="float: left">
							<span class="glyphicon glyphicon-alert" style="font-size: 30px; color: #bd281d; margin-right: 10px; "></span>
						</div>
						<spring:message code="label.Error" />
					</h1>				
				</c:if>
				<div id="errorMsgFromGeneric" style="margin-bottom: 10px;">
					${message}
				</div>
				<c:if test="${messageComplement != null}">
					<span style="color: #777;border-top: 1px solid #CCC;padding: 7px 15px 5px;">
						<esapi:encodeForHTML>${messageComplement}</esapi:encodeForHTML>
					</span>
				</c:if>
				<br /><br />
			
				<c:if test="${caseidforpdfdownload != null}">			
					<c:choose>
						<c:when test="${responsive != null}">
							<a style="text-decoration: none" onclick="showExportDialogAndFocusEmail(this)" class="btn btn-lg btn-default"><spring:message code="label.Download" /> PDF</a>		
						</c:when>
						<c:otherwise>
							<a onclick="showExportDialogAndFocusEmail(this)" class="btn btn-default"><spring:message code="label.Download" /> PDF</a>
						</c:otherwise>	
					</c:choose>
				</c:if>
				
				<c:if test="${caseidforchangecontribution != null}">			
					<c:choose>
						<c:when test="${responsive != null}">
							<a style="text-decoration: none" href="${contextpath}/editcontribution/${caseidforchangecontribution}" class="btn btn-lg btn-default"><spring:message code="label.EditContribution" /></a>		
						</c:when>
						<c:otherwise>
							<a href="${contextpath}/editcontribution/${caseidforchangecontribution}" class="btn btn-default"><spring:message code="label.EditContribution" /></a>
						</c:otherwise>	
					</c:choose>
				</c:if>
			</div>
		</div>		
	</div>
	
	<%@ include file="../footer.jsp" %>		
</body>

<div class="modal fade" id="ask-export-dialog" data-backdrop="static" style="text-align: left">
	<div class="modal-dialog">
    <div class="modal-content">
	<div class="modal-header">
		<b><spring:message code="label.Info" /></b>
	</div>
	<div class="modal-body">
		<spring:message code="question.EmailForPDF" /><br /><br /> 
		
		<c:choose>
			<c:when test='${participantsemail != null && participantsemail.indexOf("@") > 0}'>
				<input type="text" maxlength="255" name="email" id="email" value="${participantsemail}" />
			</c:when>
			<c:otherwise>
				<input type="text" maxlength="255" name="email" id="email" />
			</c:otherwise>
		</c:choose>
		
		<br />
		<span id="ask-export-dialog-error" class="validation-error hideme">
			<spring:message code="message.ProvideEmail" />
		</span>
		<div class="captcha" style="margin-left: 0px; margin-bottom: 20px; margin-top: 20px;">			
			<c:if test="${!captchaBypass}">
			<%@ include file="../captcha.jsp" %>					
			</c:if>
       	</div>
       	<span id="ask-export-dialog-error-captcha" class="validation-error hideme">
       		<c:if test="${!captchaBypass}">
       		<spring:message code="message.captchawrongnew" />
       		</c:if>
       	</span>
	</div>
	<div class="modal-footer">
		<c:choose>
			<c:when test="${responsive != null && form != null}">
				<a style="text-decoration: none"  class="btn btn-primary btn-lg" onclick="startExport()">${form.getMessage("label.OK")}</a>	
				<a style="text-decoration: none"  class="btn btn-default btn-lg" data-dismiss="modal">${form.getMessage("label.Cancel")}</a>		
			</c:when>
			<c:otherwise>
				<a  class="btn btn-primary" onclick="startExport()"><spring:message code="label.OK" /></a>	
				<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>		
			</c:otherwise>	
		</c:choose>				
	</div>
	</div>
	</div>
</div>