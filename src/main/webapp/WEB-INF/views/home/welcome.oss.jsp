<%@ page contentType="text/html; charset=UTF-8" session="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<!-- welcome.oss version -->
	<title>EUSurvey - <spring:message code="label.Welcome" /></title>	
	<%@ include file="../includes.jsp" %>	
	
	<script type="text/javascript">
		
		<c:if test="${runnermode != null }">
			$(function() {
				 $(".headerlink, .header a").each(function(){
					 if ($(this).attr("href").indexOf("?") == -1)
					$(this).attr("href", $(this).attr("href") + "/runner");
				 });
			});
		</c:if>
		
		function checkEmail()
		{
			var val = $("#email").val();
			var caseid = $("#caseid").val();
			
			$('#download-contribution-dialog').find(".validation-error").hide();
			$('#download-contribution-dialog').find(".validation-error-keep").hide();
			
			if (caseid.trim().length == 0)
			{
				$("#download-contribution-dialog-caseid-error").show();
				return;
			}
			
			if (val.length > 0 && validateEmail(val)) {
				$("#download-contribution-dialog-error").hide();

				var challenge = getChallenge();
			    var uresponse = getResponse();
			    
			    if (uresponse.trim().length == 0)
			    {
			    	$("#runner-captcha-empty-error").show();
			    	return;
			    }
			
				$.ajax({
					type:'POST',
					  url: "${contextpath}/home/downloadcontribution",
					  data: {email : val, caseid : caseid, recaptcha_challenge_field : challenge, 'g-recaptcha-response' : uresponse},
					  cache: false,
					  success: function( data ) {						  
						  if (data == "success") {
								$('#download-contribution-dialog').modal('hide');
								showPublicationExportSuccessMessage2();
						  	} else if (data == "errorcaptcha") {
						  		$("#runner-captcha-error").show();
						  		reloadCaptcha()
						  	} else if (data == "errorcaseid") {
						  		$("#download-contribution-dialog-caseid-error").show();
						  		reloadCaptcha()
							} else {
								showError('<spring:message code="error.unexpected" />');
								reloadCaptcha()
							};
					}
				});				
			} else {
				$("#download-contribution-dialog-error").show();
			}
		}
	
	</script>
	
</head>
<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
	
		<c:choose>
			<c:when test="${USER != null && runnermode == null}">
				<%@ include file="../menu.jsp" %>	
			</c:when>
		</c:choose>	
	
		<div class="page" style="padding-top: 60px;">
		
			<div id="logoPlaceHolder" style="width:700px; margin-left:auto;margin-right:auto;">
				<img src="${contextpath}/resources/images/logo_Eusurvey.png" alt="EUSurvey logo" style="width:480px;" />
				<h2 style="color:#004F98; position:relative; top:-75px; margin-left:255px;"><spring:message code="home.create" /></h2>
			</div>
			
			<div id="loginPlaceHolder" style="margin-bottom:80px">
				<form id="welcomeOssLoginForm" action="${ecasurl}" style="margin: 0px">
					<c:if test="${require2fa}">
						<input type="hidden" name="acceptStrength" value="PASSWORD_SMS" />
					</c:if>
					<input type="hidden" name="service" value="${serviceurl}"/>
					
					<div id="loginAnchor" style="max-width:400px; text-align: center; float:left; margin-left:100px">
						<table id="loginLinkTable">
							<tbody>
								<tr>
									<td>
											<a id="loginInternalLinkFromWelcome" class="bigLinkBoxHighlighted" href="<c:url value="/auth/login"/>"><spring:message code="label.DoLogin" /></a>
									</td>
								</tr>
								<tr >
									<td id="selfRegLinkFromWelcome" style="padding-top:40px; text-align:left;">
										<c:choose>
											<c:when test="${USER != null}">
												<a id="linkSelfRegA" style="" target="_blank" href="<c:url value="/runner/NewSelfRegistrationSurvey"/>"><spring:message code="label.Register" /></a>
											</c:when>
											<c:otherwise>
												<a id="linkSelfRegA" style="" href="<c:url value="/runner/NewSelfRegistrationSurvey"/>"><spring:message code="label.Register" /></a>
											</c:otherwise>
										</c:choose>
									</td>
								<tr>
							</tbody>
						</table>
					</div>
					
					<div style="text-align: center;  float: right; margin-right: 100px; margin-top: -10px;">
						<span style="color:#004F98; font-size:12pt; margin-left:95px; margin-right:20px; font-weight:bold;">
							<a href="<c:url value="/home/editcontribution"/>"><spring:message code="label.EditContribution" /><img style="margin-left: 5px" src="${contextpath}/resources/images/icons/24/link.png" alt="link" width="16px"/></a></span>
						<br /><br />
						<span style="color:#004F98; font-size:12pt; margin-left:95px; margin-right:20px; font-weight:bold;">
							<a  onclick="$('#download-contribution-dialog').modal('show');"><spring:message code="label.DownloadContribution" /><img style="margin-left: 5px" src="${contextpath}/resources/images/file_extension_pdf_small.png" alt="<spring:message code="label.DownloadContribution" />" width="16px"/></a></span>
					</div>
	
				</form>
			</div>
			
			<div id="firstSeparator" class="largeSeparator" style="display:inline-block; margin-top:30px;"></div>
					
			<div id="welcomeContent">
				<h1 style="text-align:center; margin-bottom:35px;"><spring:message code="home.create2" /></h1>
				<img src="${contextpath}/resources/images/icons/128/review-128-blue.png" alt="icon review" style="float:left; width:100px; margin-left:100px;margin-top:30px;"/>	
				<p style="text-align:right; padding-top:25px; line-height:30px; float:right; clear:right;">
					<spring:message code="home.create2bis" />
				</p>
				
				<img src="${contextpath}/resources/images/icons/128/searching-128-blue.png" alt="icon review" style="width:100px; margin-right:100px;margin-top:30px; float:right; clear:right;"/>	
				<p style="padding-top:45px; line-height:30px; float:left; clear:left;">
					<span style="font-weight:bold; font-size:110%;"><spring:message code="info.welcome1" />&nbsp;<a href="<c:url value="/home/editcontribution"/>"><spring:message code="info.welcome3" />&nbsp;<img src="${contextpath}/resources/images/icons/24/link.png" alt="link" style="width: 18px;"/></a>.</span>
					<br />
					<spring:message code="info.welcome2" />
				</p>
				
				<img src="${contextpath}/resources/images/icons/128/earth-128-blue.png" alt="icon review" style="float:left; width:100px; margin-left:100px;margin-top:30px; clear:left;"/>	
				<p style="text-align:right; padding-top:45px; line-height:30px; width:350px; float:right; clear:right;">
					<spring:message code="info.welcome4new" />
				</p>
				
				<br style="clear:both"/><br/><br/>
				<a href="?language=bg">Български</a>&#160; 
				<a href="?language=cs">Čeština</a>&#160; 
				<a href="?language=da">Dansk</a>&#160; 
				<a href="?language=de">Deutsch</a>&#160; 
				<a href="?language=et">Eesti keel</a>&#160; 
				<a href="?language=el">Ελληνικά</a>&#160; 
				<a href="?language=en">English</a>&#160; 
				<a href="?language=es">Español</a>&#160; 
				<a href="?language=fr">Français</a>&#160; 
				<a href="?language=hr">Hrvatski jezik</a>&#160; 
				<a href="?language=it">Italiano</a>&#160; 
				<a href="?language=lv">Latviešu valoda</a>&#160; 
				<a href="?language=lt">Lietuvių kalba</a>&#160; 
				<a href="?language=hu">Magyar</a>&#160; 
				<a href="?language=mt">Malti</a>&#160; 
				<a href="?language=nl">Nederlands</a>&#160; 
				<a href="?language=pl">Polski</a>&#160; 
				<a href="?language=pt">Português</a>&#160; 
				<a href="?language=ro">Română</a>&#160; 
				<a href="?language=sk">Slovenčina</a>&#160; 
				<a href="?language=sl">Slovenščina</a>&#160; 
				<a href="?language=fi">Suomi</a>&#160; 
				<a href="?language=sv">Svenska</a>&#160;
					
			</div>
			
			<br style="clear:both"/><br />
			
			<div class="largeSeparator"></div>	
		</div>
		
		<div class="modal" id="forgot-password-dialog" data-backdrop="static" style="width: 400px; margin-left: -200px;">
			<form id="forgotPasswordForm" action="${contextpath}/auth/login" method="post" style="margin: 0px;" >
				<input type="hidden" name="target" value="forgotPassword" />
				<div class="modal-body">
					<spring:message code="label.PleaseEnterYourLogin" /><br />
					<input id="login" type="text" name="login" maxlength="255" /><br /><br />
					<spring:message code="label.PleaseEnterYourEmail" /><br />
					<input id="email" type="text" name="email" maxlength="255" class="email" /><br />
					<span id="errorMessage" style="color: #f00; display: none;"><spring:message code="error.PleaseEnterYourNameAndEmail" /></span>
					<div style="margin-left: 0px; margin-bottom: 20px; margin-top: 20px;">
					<%@ include file="../captcha.jsp" %>			
		        	</div>
				</div>
				<div class="modal-footer" style="height:32px;">
					<a  onclick="requestLink();" class="btn btn-primary"><spring:message code="label.OK" /></a>
					<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>			
				</div>	
			</form>
		</div>
		
		<div class="modal" id="download-contribution-dialog" data-backdrop="static">
			<form:form action="${contextpath}/home/downloadcontribution" method="post" style="margin: 0px;">
				<div class="modal-header">
					<b><spring:message code="label.DownloadContribution" /></b>
				</div>
				<div class="modal-body">
					<spring:message code="label.EnterContributionId" />
					<input type="text" maxlength="255" name="caseid" id="caseid" />
					<span id="download-contribution-dialog-caseid-error" class="validation-error hideme">
						<spring:message code="validation.invalidContributionId" />
					</span>
					<br />
					
					<spring:message code="question.EmailForPDF" />
					<input type="text" maxlength="255" name="email" id="email" />
					<span id="download-contribution-dialog-error" class="validation-error hideme">
						<spring:message code="message.ProvideEmail" />
					</span>
					
					<div class="captcha" style="margin-left: 0px; margin-bottom: 20px; margin-top: 20px;">
					<%@ include file="../captcha.jsp" %>		
			       	</div>
			       	<span id="download-contribution-dialog-error-captcha" class="validation-error hideme">
			       		<spring:message code="message.captchawrongnew" />
			       	</span>
					
				</div>
				<div class="modal-footer" style="height:32px;">
					<a  onclick="checkEmail()" class="btn btn-primary"><spring:message code="label.OK" /></a>
					<a  class="btn btn-default" onclick="$('#download-contribution-dialog').modal('hide');"><spring:message code="label.Cancel" /></a>
				</div>
			</form:form>
		</div>
	</div>
		
	<%@ include file="../footer.jsp" %>	

</body>
</html>
