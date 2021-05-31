<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <esapi:encodeForHTML>${surveyTitle}</esapi:encodeForHTML> - <spring:message code="label.Thanks" /></title>	
	<%@ include file="includes.jsp" %>
	<script type="text/javascript" src="${contextpath}/resources/js/runner.js?version=<%@include file="version.txt" %>"></script>
	<script type="text/javascript"> 
		$(function() {					
			clearAllCookies('${surveyprefix}');
		});	
	</script>
	<link id="runnerCss" href="${contextpath}/resources/css/yellowfocus.css?version=<%@include file="version.txt" %>" rel="stylesheet" type="text/css"></link>
</head>
<body>
	<div class="page-wrap">
		<%@ include file="header.jsp" %>
		
			<c:choose>
				<c:when test="${responsive != null}">
					<div class="fullpage" style="padding-top: 50px">
					<div>
					<div class="draftDescription">
				</c:when>
				<c:otherwise>
					<div class="fullpage">
					<div class="normal-content">
					<div class="draftDescription" style="padding-top:25px">
				</c:otherwise>
			</c:choose>
				
					<h1>${form.getMessage("message.draftDoNotForget")}<img src="${contextpath}/resources/images/icons/64/warning.png" alt="warning" width="48" style="margin-left:15px;"/></h1>
					<p>
						${form.getMessage("message.draftDescription1")}
						<br/>
						${form.getMessage("message.draftDescription2")}
					</p>
				</div>
				<div style="text-align:center;">
					<c:choose>
						<c:when test="${responsive != null}">
							<div style="background-color: #0088cc; color: #FFF; word-wrap: break-word; padding: 10px; border-radius: 6px;">
							<a style="color: #FFF; font-weight: bold;" class="visiblelink" id="draftLinkFromThanksDraft" href="<esapi:encodeForHTMLAttribute>${url}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML>${url}</esapi:encodeForHTML></a>
							</div>
							<br/>
							<a href="javascript:;" style="text-decoration: none" class="btn btn-primary btn-lg" onclick="$('#ask-email-dialog').modal('show');">${form.getMessage("label.SendLinkAsEmail")}</a> <br /><br />
							<a id="copyme" class="btn btn-primary btn-lg" href="<esapi:encodeForHTMLAttribute>${url}</esapi:encodeForHTMLAttribute>">${form.getMessage("label.CopyToClipboard")}</a>
						</c:when>
						<c:otherwise>					
							<a class="draftLink visiblelink" id="draftLinkFromThanksDraft" href="<esapi:encodeForHTMLAttribute>${url}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML>${url}</esapi:encodeForHTML></a>
							<br/><br/>	
							<a href="javascript:;" class="btn btn-primary aSpaced" onclick="showModalDialog($('#ask-email-dialog'), this);">${form.getMessage("label.SendLinkAsEmail")}</a>
							<a id="bookmarkme" class="aSpaced" href="<esapi:encodeForHTMLAttribute>${url}</esapi:encodeForHTMLAttribute>">${form.getMessage("label.SaveToBookMark")}</a>
							<a id="copyme"class="aSpaced" href="<esapi:encodeForHTMLAttribute>${url}</esapi:encodeForHTMLAttribute>">${form.getMessage("label.CopyToClipboard")}</a>
						</c:otherwise>
					</c:choose>	
					<c:if test="${downloadContribution}">
						<br /><br />
						<h2><spring:message code="question.needcopydraft" /></h2>
						<a href="javascript:;" onclick="showExportDialogAndFocusEmail(this)" class="btn btn-default" style="margin-top: 10px"><spring:message code="label.GetPDF" /></a>
					</c:if> 
				</div>				
			</div>
		</div>
		
		<div class="modal fade" id="ask-export-dialog" data-backdrop="static" role="dialog">	
			<div class="modal-dialog">
		    <div class="modal-content">
			<div class="modal-header">
				<b><spring:message code="label.Info" /></b>
			</div>
			<div class="modal-body">
				<p>
					<c:choose>
						<c:when test="${runnermode == true}">
							${form.getMessage("question.EmailForPDF")}
						</c:when>
						<c:otherwise>
							<spring:message code="question.EmailForPDF" />
						</c:otherwise>	
					</c:choose>
				</p>
				
				<c:choose>
					<c:when test='${participantsemail != null && participantsemail.indexOf("@") > 0}'>
						<input type="text" maxlength="255" name="email" id="email" value="${participantsemail}" />
					</c:when>
					<c:otherwise>
						<input type="text" maxlength="255" name="email" id="email" />
					</c:otherwise>
				</c:choose>
				<span id="ask-export-dialog-error" class="validation-error hideme">
					<c:choose>
						<c:when test="${runnermode == true}">
							${form.getMessage("message.ProvideEmail")}
						</c:when>
						<c:otherwise>
							<spring:message code="message.ProvideEmail" />
						</c:otherwise>	
					</c:choose>
				</span>
				<div class="captcha" style="margin-left: 0px; margin-bottom: 20px; margin-top: 20px;">						
					<c:if test="${captchaBypass !=true}">
					<%@ include file="captcha.jsp" %>					
					</c:if>
		       	</div>
		       	<span id="ask-export-dialog-error-captcha" class="validation-error hideme">       		
		       		<c:if test="${captchaBypass !=true}">
		       		<c:choose>
						<c:when test="${runnermode == true}">
							${form.getMessage("message.captchawrongnew")}
						</c:when>
						<c:otherwise>
							<spring:message code="message.captchawrongnew" />
						</c:otherwise>	
					</c:choose>
		       		</c:if>
		       	</span>
			</div>
			<div class="modal-footer">
				<c:choose>
					<c:when test="${responsive != null}">
						<a href="javascript:;" style="text-decoration: none"  class="btn btn-primary btn-lg" onclick="startExport()">${form.getMessage("label.OK")}</a>	
						<a href="javascript:;" style="text-decoration: none"  class="btn btn-default btn-lg" data-dismiss="modal">${form.getMessage("label.Cancel")}</a>		
					</c:when>
					<c:when test="${runnermode == true}">
						<a href="javascript:;" class="btn btn-primary" onclick="startExport()">${form.getMessage("label.OK")}</a>	
						<a href="javascript:;" class="btn btn-default" data-dismiss="modal">${form.getMessage("label.Cancel")}</a>		
					</c:when>
					<c:otherwise>
						<a href="javascript:;" class="btn btn-primary" onclick="startExport()"><spring:message code="label.OK" /></a>	
						<a href="javascript:;" class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>		
					</c:otherwise>	
				</c:choose>				
			</div>
			</div>
			</div>
		</div>
		
		<div class="modal" id="ask-email-dialog" data-backdrop="static" role="dialog">
			<div class="modal-dialog">
	    		<div class="modal-content">
					<div class="modal-header">
						<b><spring:message code="label.Info" /></b>
					</div>
					<div class="modal-body">
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
						<input type="text" maxlength="255" name="email" id="linkemail" />
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
						<div class="captcha" style="margin-left: 0px; margin-bottom: 20px; margin-top: 20px;">
							<%@ include file="captcha.jsp" %>								
				       	</div>
				       	<span id="ask-email-dialog-error-captcha" class="validation-error hideme">
							<spring:message code="message.captchawrongnew" />
				       	</span>
					</div>
					<div class="modal-footer">
						<c:choose>
							<c:when test="${responsive != null}">
								<a href="javascript:;" style="text-decoration: none" class="btn btn-primary btn-lg btn-primary" onclick="sendMailLink()">${form.getMessage("label.OK")}</a>	
								<a href="javascript:;" style="text-decoration: none" class="btn btn-lg btn-default" onclick="hideModalDialog($('#ask-email-dialog'))">${form.getMessage("label.Cancel")}</a>		
							</c:when>
							<c:when test="${runnermode == true}">
								<a href="javascript:;" class="btn btn-primary" onclick="sendMailLink()">${form.getMessage("label.OK")}</a>	
								<a href="javascript:;" class="btn btn-default" onclick="hideModalDialog($('#ask-email-dialog'))">${form.getMessage("label.Cancel")}</a>		
							</c:when>
							<c:otherwise>
								<a href="javascript:;" class="btn btn-primary" onclick="sendMailLink()"><spring:message code="label.OK" /></a>	
								<a href="javascript:;" class="btn btn-default"  onclick="hideModalDialog($('#ask-email-dialog'))"><spring:message code="label.Cancel" /></a>		
							</c:otherwise>	
						</c:choose>				
					</div>
				</div>
			</div>
		</div>
		
		<div id="successMailLinkMessage" class="alert alert-success user-info" style="display: none; position: fixed; top: 5px; right: 5px; padding: 10px; z-index: 10001; ">
			<div style="float: right; margin-left: 5px;"><a onclick="$(this).parent().parent().hide();" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-remove"></span></a></div>
			${form.getMessage("message.mail.successMailLinkDraft")}
		</div>
		
		<div id="successCopyClipboardLinkMessage" class="alert alert-success user-info" style="display: none; position: fixed; top: 5px; right: 5px; padding: 10px; z-index: 10001; ">
			<div style="float: right; margin-left: 5px;"><a onclick="$(this).parent().parent().hide();" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-remove"></span></a></div>
			${form.getMessage("message.copy.successCopyClipboardLink")}
		</div>
		
		<div id="failureMailLinkMessage" class="alert alert-danger user-info" style="display: none; position: fixed; top: 5px; right: 5px; padding: 10px; z-index: 10001; ">
			<div style="float: right; margin-left: 5px;"><a onclick="$(this).parent().parent().hide();" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-remove"></span></a></div>
			${form.getMessage("message.mail.failMailLinkDraft")}
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
				
					$.ajax({
						type:'GET',
						  url: "${contextpath}/runner/createdraftanswerpdf/${uniqueCode}",
						  data: {email : mail, recaptcha_challenge_field : challenge, 'g-recaptcha-response' : uresponse},
						  cache: false,
						  success: function( data ) {
							  
							  if (data == "success") {
									$('#ask-export-dialog').modal('hide');
									showSuccess(message_PublicationExportSuccess2.replace('{0}', mail));
							  	} else if (data == "errorcaptcha") {
							  		$("#runner-captcha-error").show();
							  		reloadCaptcha();
								} else {
									showError(message_PublicationExportFailed);
									reloadCaptcha();
								};
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
									reloadCaptcha();
								};
						}
					});							
				</c:otherwise>
			</c:choose>
		}
		
		$(document).ready(function(){
			
			var bookMarkEnabled = false;
			
			if (window.sidebar && window.sidebar.addPanel) {
				bookMarkEnabled = true;
	        } else if ((window.sidebar && (navigator.userAgent.toLowerCase().indexOf('firefox') > -1)) || (window.opera && window.print)) {
	        	bookMarkEnabled = true;
	        } else if (window.external && ('AddFavorite' in window.external)) {
	        	bookMarkEnabled = true;
	        } else {
	        	bookMarkEnabled = false;
	        }
			
			if(!bookMarkEnabled)
			{
				$("#bookmarkme").hide();
			}		
			
			if(!document.queryCommandSupported('copy'))
			{
				$("#copyme").hide();
			}
			
			try 
			{  
				document.execCommand('copy');  
			} 
			catch(err) 
			{  
				$("#copyme").hide();
			}
			
		});
		
			function sendMailLink()
			{
				
				$("#ask-email-dialog").find(".validation-error").hide();
				$("#ask-email-dialog").find(".validation-error-keep").hide();
				
				var mail = $("#linkemail").val();
				var linkDraft = $("#copyme").attr("href");
				
				var challenge = getChallenge();
			    var uresponse = getResponse($("#ask-email-dialog"));
			    
			    var id = '${surveyID}';
				
				if (mail.trim().length == 0 || !validateEmail(mail))
				{
					$("#ask-email-dialog-error").show();
					return;
				}
				
				if (uresponse.trim().length == 0)
			    {
					$("#ask-email-dialog").find("#runner-captcha-empty-error").show();
			    	return;
			    }
	
				$.ajax({
					type:'GET',
					  url: "${contextpath}/runner/sendmaillink",
					  data: {email : mail, link: linkDraft, id : id, recaptcha_challenge_field : challenge, 'g-recaptcha-response' : uresponse},
					  cache: false,
					  success: function( data ) {
				  
						if (data == "success") {
							$('#successMailLinkMessage').show();
							$('#failureMailLinkMessage').hide();
							$('#ask-email-dialog').modal('hide');
						}
						else if(data == "errorcaptcha")
						{
							$("#ask-email-dialog").find("#runner-captcha-error").show();
							reloadCaptcha();
						}
						else {
							$('#successMailLinkMessage').hide();
							$('#failureMailLinkMessage').show();
						}
					}
				});				
				
			}
		    $(function() {
		        $("#bookmarkme").click(function(event) {
		        	
		        	var bookmarkURL = $("#copyme").attr("href");
	                var bookmarkTitle = document.title;
	                var triggerDefault = false;
	
	                if (window.sidebar && window.sidebar.addPanel) {
	                    // Firefox version < 23
	                    window.sidebar.addPanel(bookmarkTitle, bookmarkURL, '');
	                } else if ((window.sidebar && (navigator.userAgent.toLowerCase().indexOf('firefox') > -1)) || (window.opera && window.print)) {
	                    // Firefox version >= 23 and Opera Hotlist
	                    var $this = $(this);
	                    $this.attr('href', bookmarkURL);
	                    $this.attr('title', bookmarkTitle);
	                    $this.attr('rel', 'sidebar');
	                    $this.off(event);
	                    triggerDefault = true;
	                } else if (window.external && ('AddFavorite' in window.external)) {
	                    // IE Favorite
	                    window.external.AddFavorite(bookmarkURL, bookmarkTitle);
	                } else {
	                    // WebKit - Safari/Chrome
	                    alert('Press ' + (navigator.userAgent.toLowerCase().indexOf('mac') != -1 ? 'Cmd' : 'Ctrl') + '+D <spring:message code="label.bookmark.help"/>');
	                }
	
	                return triggerDefault;
		        });
		        
		        
	        	$('#copyme').click(function(event) { 
	        		
	        		if(document.queryCommandSupported('copy'))
	    	        {
			        	event.preventDefault();
						// Select the email link anchor text  
						var emailLink = document.querySelector('#draftLinkFromThanksDraft');  
						var range = document.createRange();  
						range.selectNode(emailLink);  
						window.getSelection().addRange(range);  
						  
						try {  
						  // Now that we've selected the anchor text, execute the copy command  
						  document.execCommand('copy');
						  
						  $("#successCopyClipboardLinkMessage").show();
						  
						} catch(err) {  
						  //console.log('Oops, unable to copy');
						  alert("<spring:message code="label.CopyToClipboard.disabled"/>");
						}  
						  
						// Remove the selections - NOTE: Should use   
						// removeRange(range) when it is supported  
						window.getSelection().removeAllRanges();  
			        	
	        		}
		        	else
	        		{
	        			alert("<spring:message code="label.CopyToClipboard.disabled"/>");
	        		}
		        });
		    });
		</script>
	</div>

	<c:choose>
		<c:when test="${responsive != null}">
			<%@ include file="footerresponsive.jsp" %>
		</c:when>
		<c:otherwise>
			<%@ include file="footerSurveyLanguages.jsp" %>		
		</c:otherwise>
	</c:choose>
	
	<%@ include file="generic-messages.jsp" %>

</body>
</html>
