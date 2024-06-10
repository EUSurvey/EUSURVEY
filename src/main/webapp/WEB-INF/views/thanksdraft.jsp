<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <esapi:encodeForHTML>${surveyTitle }</esapi:encodeForHTML> - <spring:message code="label.Thanks" /></title>	
	<%@ include file="includes.jsp" %>
	<script type="text/javascript" src="${contextpath}/resources/js/runner.js?version=<%@include file="version.txt" %>"></script>
	<script type="text/javascript"> 
		$(function() {					
			clearLocalBackupForPrefix('${surveyprefix}');
			
			<c:if test="${redirect != null}">
				window.location = "${redirect}";
			</c:if>			
		});			
	</script>
</head>
<body id="thanksDraftBody">
	<div class="page-wrap">
		<%@ include file="header.jsp" %>
	
		<c:choose>
			<c:when test="${USER != null}">
	
				<%@ include file="menu.jsp" %>		
				
				<c:if test="${sessioninfo != null}">
					<%@ include file="management/formmenu.jsp" %>	
				</c:if>
			</c:when>
	
		</c:choose>	
		
		<div class="fullpage">
			<div class="normal-content">
				
				<div class="draftDescription" style="margin-top:25px">
					<h1><spring:message code="message.draftDoNotForget" /><img src="${contextpath}/resources/images/icons/64/warning.png" alt="warning" width="48" style="margin-left:15px;"/></h1>
					<p>
						<spring:message code="message.draftDescription1" />
						<br/>
						<spring:message code="message.draftDescription2" />
					</p>
				</div>
				<div style="text-align:center;">
					<a class="draftLink visiblelink" id="draftLinkFromThanksDraft" href="<esapi:encodeForHTMLAttribute>${url}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML>${url}</esapi:encodeForHTML></a>
				
					<br/><br/>
				
					<button type="button" class="btn btn-primary aSpaced" onclick="showAskEmailDialog(this);"><spring:message code="label.SendLinkAsEmail" /></button>
					<button type="button" class="unstyledbutton aSpaced" id="copyme" onclick="navigator.clipboard.writeText('${url}');"><spring:message code="label.CopyToClipboard" /></button>
					<c:if test="${downloadContribution}">
						<br /><br />
						<h2><spring:message code="question.needcopydraft" /></h2>
						<a onclick="showExportDialogAndFocusEmail(this)" class="btn btn-default" style="margin-top: 10px"><spring:message code="label.GetPDF" /></a> 
					</c:if>
				</div>
			</div>
		</div>
		
		<div class="modal fade" id="ask-export-dialog" data-backdrop="static">	
			<div class="modal-dialog">
		    <div class="modal-content">
			<div class="modal-header">
				<b><spring:message code="label.Info" /></b>
			</div>
			<div class="modal-body">
				<div class="forexport">
					<p>
						<spring:message code="question.EmailForPDF" />
					</p>
					
					<input type="text" maxlength="255" name="email" id="email" value="${USER.email}" />
					<span id="ask-export-dialog-error" class="validation-error hideme">
						<spring:message code="message.ProvideEmail" />
					</span>
				</div>
				<div class="foremail">
					<p>
						<c:choose>
							<c:when test="${runnermode == true}">
								${form.getMessage("label.EmailAddress")}
							</c:when>
							<c:otherwise>
								<spring:message code="label.EmailAddress" />
							</c:otherwise>	
						</c:choose>
					</p>
					<input type="text" maxlength="255" name="email" id="email" />
					<span id="ask-email-dialog-error" class="validation-error hideme">
						<c:choose>
							<c:when test="${runnermode == true}">
								${form.getMessage("message.ProvideEmail")}
							</c:when>
							<c:otherwise>
								<spring:message code="message.ProvideEmail" />
							</c:otherwise>	
						</c:choose>
					</span>
				</div>
				<div class="captcha" style="margin-left: 0px; margin-bottom: 20px; margin-top: 20px;">						
					<c:if test="${captchaBypass !=true}">
					<%@ include file="captcha.jsp" %>					
					</c:if>
		       	</div>
		       	<span id="ask-export-dialog-error-captcha" class="validation-error hideme">       		
		       		<c:if test="${captchaBypass !=true}">
		       			<spring:message code="message.captchawrongnew" />
		       		</c:if>
		       	</span>
		       	<span id="ask-email-dialog-error-captcha" class="validation-error hideme">
					<spring:message code="message.captchawrongnew" />
		       	</span>
			</div>
			<div class="modal-footer">
				<div class="forexport">
					<button type="button" class="btn btn-primary" onclick="startExport()"><spring:message code="label.OK" /></button>	
					<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></button>		
				</div>
				<div class="foremail">
					<c:choose>
						<c:when test="${runnermode == true}">
							<button type="button" class="btn btn-primary" onclick="sendMailLink()">${form.getMessage("label.OK")}</button>	
							<button type="button" class="btn btn-default" data-dismiss="modal">${form.getMessage("label.Cancel")}</button>		
						</c:when>
						<c:otherwise>
							<button type="button" class="btn btn-primary"" onclick="sendMailLink()"><spring:message code="label.OK" /></button>	
							<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></button>		
						</c:otherwise>	
					</c:choose>	
				</div>
			</div>
			</div>
			</div>
		</div>

		<div id="failureMailLinkMessage" class="alert alert-danger user-info" style="display: none; position: fixed; top: 5px; right: 5px; padding: 10px; z-index: 10001; ">
			<div style="float: right; margin-left: 5px;"><a onclick="$(this).parent().parent().hide();" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-remove"></span></a></div>
				<spring:message code="message.mail.failMailLinkDraft" />
		</div>
		
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
					
			<c:choose>
				<c:when test="${!captchaBypass}">
					var challenge = getChallenge();
				    var uresponse = getResponse();
				    
				    if (uresponse.trim().length == 0)
				    {
				    	$("#runner-captcha-empty-error").show();
				    	return;
				    }
				    
				    var data = {email : mail, recaptcha_challenge_field : challenge, 'g-recaptcha-response' : uresponse};
					if ($('#captcha_id').length > 0) {
						data["captcha_id"] =  $('#captcha_id').val();
						data["captcha_useaudio"] =  $('#captcha_useaudio').val();
						data["captcha_original_cookies"] = $('#captcha_original_cookies').val();
					}
				
					$.ajax({
						type:'GET',
						  url: "${contextpath}/runner/createdraftanswerpdf/${uniqueCode}",
						  data: data,
						  cache: false,
						  success: function( data ) {
							  
						  	if (data == "success") {
								$('#ask-export-dialog').modal('hide');
								showSuccess(message_PublicationExportSuccess2.replace('{0}', mail));
						  	} else if (data == "errorcaptcha") {
						  		$("#runner-captcha-error").show();
							} else {
								showError(message_PublicationExportFailed);
							};
							reloadCaptcha();
						}
					});							
				</c:when>
				<c:otherwise>			
					$.ajax({				
						type:'GET',
						  url: "${contextpath}/runner/createdraftanswerpdf/${uniqueCode}",
						  data: {email : mail, recaptcha_challenge_field : '', 'g-recaptcha-response' : ''},
						  cache: false,
						  success: function( data ) {
							  
						 	if (data == "success") {
								$('#ask-export-dialog').modal('hide');
								showSuccess(message_PublicationExportSuccess2.replace('{0}', mail));
							} else {
								showError(message_PublicationExportFailed);
								
							};
							
							reloadCaptcha();
						}
					});							
				</c:otherwise>
			</c:choose>
		}

		$("#copyme").click(function(){
			if(!document.queryCommandSupported('copy'))
			{
				$("#copyme").hide();
			}

			try
			{
				var result = document.execCommand('copy');

				if(result)
				{
					showSuccess('<spring:message code="message.copy.successCopyClipboardLink" />');
				}
			}
			catch(err)
			{
				$("#copyme").hide();
			}
		});
		
		function sendMailLink()
		{

			$("#ask-export-dialog").find(".validation-error").hide();
			$("#ask-export-dialog").find(".validation-error-keep").hide();

			var mail = $("#ask-export-dialog").find("#email").val();
			var linkDraft = '${url}';

			var challenge = getChallenge();
			var uresponse = getResponse($("#ask-export-dialog"));

			if (mail.trim().length == 0 || !validateEmail(mail))
			{
				$("#ask-email-dialog-error").show();
				return;
			}

			if (uresponse.trim().length == 0)
			{
				$("#ask-export-dialog").find("#runner-captcha-empty-error").show();
				return;
			}

			var id = '${surveyID}';

			var data = {email : mail, link: linkDraft, id : id, recaptcha_challenge_field : challenge, 'g-recaptcha-response' : uresponse};
			if ($('#captcha_id').length > 0) {
				data["captcha_id"] =  $('#captcha_id').val();
				data["captcha_useaudio"] =  $('#captcha_useaudio').val();
				data["captcha_original_cookies"] = $('#captcha_original_cookies').val();
			}

			$.ajax({
				type:'GET',
				  url: "${contextpath}/runner/sendmaillink",
				  data: data,
				  cache: false,
				  success: function( data ) {

					if (data == "success") {
						$('#ask-export-dialog').modal('hide');
						showSuccess(message_SuccessMailLinkDraft);
					}
					else if(data == "errorcaptcha")
					{
						$("#ask-export-dialog").find("#runner-captcha-error").show();
					}
					else {
						showError(message_FailedMailLinkDraft);
					}

					reloadCaptcha();
				}
			});

		}
		</script>
	
	</div>
	
	<c:choose>
		<c:when test="${runnermode == true}">
			<%@ include file="footerSurveyLanguages.jsp" %>
		</c:when>
		<c:otherwise>
			<%@ include file="footer.jsp" %>
		</c:otherwise>
	</c:choose>

</body>
</html>
