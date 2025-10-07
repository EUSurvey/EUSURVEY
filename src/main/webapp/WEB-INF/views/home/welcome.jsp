<%@ page contentType="text/html; charset=UTF-8" session="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<!-- welcome version -->
	<title>EUSurvey - <spring:message code="label.Welcome" /></title>	
	<%@ include file="../includes.jsp" %>	
	<script type="text/javascript" src="${contextpath}/resources/js/menu.js?version=<%@include file="../version.txt" %>"></script>

	<link href="${contextpath}/resources/css/welcome.css" rel="stylesheet" type="text/css" />
	
	<script type="text/javascript">
		
		function logoutECAS()
		{
			$('#ecas-dialog').modal('hide');
			 $.ajax({
				 cache: false,
	           url: "${ECASLOGOUT}"
	         });
		}
		
		<c:if test="${runnermode != null }">
			$(function() {
				 $(".headerlink, .header a").each(function(){
					 if ($(this).attr("href").indexOf("?") == -1)
					$(this).attr("href", $(this).attr("href") + "/runner");
				 });
			});
		</c:if>
		
		<c:if test="${showDownloadPdfDialog != null }">
			$(function() {
				$('#caseid').val('${code}');
				$('#emailcontribution').val('${email}');
				showModalDialog($('#download-contribution-dialog'), $('#linkDownloadContribution'));
				setTimeout(function() {createCaptcha();}, 100);
			});			
		</c:if>
		
		function checkEmail()
		{
			var val = $("#emailcontribution").val();
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

				var challenge = getChallenge($('#download-contribution-dialog'));
			    var uresponse = getResponse($('#download-contribution-dialog'));
			    
			    if (uresponse.trim().length == 0)
			    {
			    	$("#runner-captcha-empty-error").show();
			    	return;
			    }
			   			    
			    var csrftoken = $("meta[name='_csrf']").attr("content");
				var csrfheader = $("meta[name='_csrf_header']").attr("content");
				
				var data = {email : val, caseid : caseid, recaptcha_challenge_field : challenge, 'g-recaptcha-response' : uresponse};
				if ($('#captcha_id').length > 0) {
					data["captcha_id"] =  $('#captcha_id').val();
					data["captcha_useaudio"] =  $('#captcha_useaudio').val();
					data["captcha_original_cookies"] = $('#captcha_original_cookies').val();
				}
							
				$.ajax({
					type:'POST',
					  url: "${contextpath}/home/downloadcontribution",
					  data: data,
					  cache: false,
					  beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
					  success: function( data ) {						  
						  if (data == "success") {
							  	hideModalDialog($('#download-contribution-dialog'));
								showSuccess(message_PublicationExportSuccess2.replace('{0}', val));
						  	} else if (data == "errorcaptcha") {
						  		$("#runner-captcha-error").show();
						  		reloadCaptcha();
						  	} else if (data == "errorcaseid") {
						  		$("#download-contribution-dialog-caseid-error").show();
						  		reloadCaptcha();
							} else if (data == "errorcaseidforbidden") {
								window.location = window.location.href = "${contextpath}/errors/403.html";
							} else if (data == "errorcaseidinvitation") {
								$("#download-contribution-dialog-caseidinvitation-error").show();
						  		reloadCaptcha();
							} else {
								showError('<spring:message code="error.unexpected" />');
								reloadCaptcha();
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
			<c:when test="${USER != null && runnermode == null && responsive == null}">
				<%@ include file="../menu.jsp" %>	
			</c:when>
			<c:otherwise>
				<%@ include file="../generic-messages.jsp" %>
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test="${responsive != null}">
				<div style="text-align: center; margin-top: 40px; margin-bottom: 20px;">
					<img src="${contextpath}/resources/images/logo_Eusurvey.png" style="max-width: 100%;" alt="EUSurvey logo" />
					<div style="font-weight: bold"><spring:message code="home.create" /></div>
				</div>
				
				<div style="text-align: center; margin-bottom: 40px;">
					<a class="btn btn-default btn-sm" id="linkEditContribution" href="<c:url value="/home/editcontribution"/>"><spring:message code="label.EditContribution" /> <span class="glyphicon glyphicon-pencil" style="margin-left: 5px;"></span></a><br /><br />
					<a class="btn btn-default btn-sm" id="linkDownloadContribution" href="javascript:;" onclick="showModalDialog($('#download-contribution-dialog'), this); setTimeout(function() {createCaptcha();}, 100);"><spring:message code="label.DownloadContribution" /> <img style="margin-left: 5px" src="${contextpath}/resources/images/file_extension_pdf_small.png" alt="<spring:message code="label.DownloadContribution" />" width="16px"/></a>
				</div>
				
				<div class="container" style="text-align: left">
				  <div class="row">
				  	<div class="col-xs-12" style="margin-bottom: 20px">
				    	<img style="width: 40px; float: left; margin: 5px;" src="${contextpath}/resources/images/icons/128/review-128-blue.png" alt="icon review"/>	
						<spring:message code="home.create2bis" />
					</div>
					<div class="col-xs-12" style="margin-bottom: 20px">
						<img style="width: 40px; float: left; margin: 5px;" src="${contextpath}/resources/images/icons/128/searching-128-blue.png" alt="icon review"/>	
						<span><spring:message code="info.welcome1" />&nbsp;<a href="<c:url value="/home/editcontribution"/>"><spring:message code="info.welcome3" />&nbsp;<img src="${contextpath}/resources/images/icons/24/link.png" alt="link" /></a>.</span>
						<br />
						<spring:message code="info.welcome2" />
					</div>
					<div class="col-xs-12" style="margin-bottom: 10px">				
						<img style="width: 40px; float: left; margin: 5px;" src="${contextpath}/resources/images/icons/128/earth-128-blue.png" alt="icon review"/>	
						<spring:message code="info.welcome4new" />
					</div>				
				  </div>
				</div>
				
			</c:when>
			<c:otherwise>
	
				<div class="page" style="padding-top: 60px;">
				
					<div id="logoPlaceHolder" style="width:700px; margin-left:auto;margin-right:auto;">
				<img src="${contextpath}/resources/images/logo_Eusurvey.png" alt="EUSurvey logo" style="width:480px;" />
						<h2 style="color:#004F98; position:relative; top:-75px; margin-left:255px;"><spring:message code="home.create" /></h2>
					</div>
					
					<div id="loginPlaceHolder" style="margin-bottom:80px">
						<form:form id="welcomeEcasLoginForm" action="${ecasurl}" style="margin: 0px">
							<input type="hidden" name="service" value="${serviceurl}"/>
							<input type="hidden" name="acceptStrength" value="PASSWORD_SMS" />
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
												<a id="linkSelfRegA" style="" target="_blank" href="<c:url value="/runner/NewSelfRegistrationSurvey"/>"><spring:message code="label.Register" /></a>	  																			
											</td>
										<tr>
									</tbody>
								</table>
							</div>
							
							<div style="text-align: center;  float: right; margin-right: 100px; margin-top: -10px;">
								<span style="color:#004F98; font-size:12pt; margin-left:95px; margin-right:20px; font-weight:bold;">
							<a id="linkEditContribution" href="<c:url value="/home/editcontribution"/>"><spring:message code="label.EditContribution" /><img style="margin-left: 5px" src="${contextpath}/resources/images/icons/24/link.png" alt="link" width="16px"/></a>
								</span><br /><br />
								<span style="color:#004F98; font-size:12pt; margin-left:95px; margin-right:20px; font-weight:bold;">
							<a id="linkDownloadContribution" href="javascript:;" onclick="showModalDialog($('#download-contribution-dialog'), this); setTimeout(function() {createCaptcha();}, 100);"><spring:message code="label.DownloadContribution" /><img style="margin-left: 5px" src="${contextpath}/resources/images/file_extension_pdf_small.png" alt="<spring:message code="label.DownloadContribution" />" width="16px"/></a>
								</span>
							</div>
			
						</form:form>
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
							
						<a class="language-link" href="?language=bg">Български</a>&#160;
			 			<a class="language-link" href="?language=cs">Čeština</a>&#160;
			 			<a class="language-link" href="?language=da">Dansk</a>&#160;
			 			<a class="language-link" href="?language=de">Deutsch</a>&#160;
			 			<a class="language-link" href="?language=et">Eesti keel</a>&#160;
			 			<a class="language-link" href="?language=el">Ελληνικά</a>&#160;
			 			<a class="language-link" href="?language=en">English</a>&#160;
			 			<a class="language-link" href="?language=es">Español</a>&#160;
			 			<a class="language-link" href="?language=fr">Français</a>&#160;
			 			<a class="language-link" href="?language=ga">Gaeilge</a>&#160;
			 			<a class="language-link" href="?language=hr">Hrvatski jezik</a>&#160;
			 			<a class="language-link" href="?language=it">Italiano</a>&#160;
			 			<a class="language-link" href="?language=lv">Latviešu valoda</a>&#160;
			 			<a class="language-link" href="?language=lt">Lietuvių kalba</a>&#160;
			 			<a class="language-link" href="?language=hu">Magyar</a>&#160;
			 			<a class="language-link" href="?language=mt">Malti</a>&#160;
			 			<a class="language-link" href="?language=nl">Nederlands</a>&#160;
			 			<a class="language-link" href="?language=pl">Polski</a>&#160;
			 			<a class="language-link" href="?language=pt">Português</a>&#160;
			 			<a class="language-link" href="?language=ro">Română</a>&#160;
			 			<a class="language-link" href="?language=sk">Slovenčina</a>&#160;
			 			<a class="language-link" href="?language=sl">Slovenščina</a>&#160;
			 			<a class="language-link" href="?language=fi">Suomi</a>&#160;
			 			<a class="language-link" href="?language=sv">Svenska</a>&#160;
							
					</div>
					
					<br style="clear:both"/><br />
					
					<div class="largeSeparator"></div>
					
				</div>
			</c:otherwise>
		</c:choose>
		
		<c:if test="${ECASLOGOUT != null}">
			<div class="modal" id="ecas-dialog" data-backdrop="static">
			 <div class="modal-dialog modal-sm">
	   		 <div class="modal-content">
			  <div class="modal-body">
			  	<c:choose>
			  		<c:when test="${casoss != null}">
			  			<spring:message code="question.LogoutCASOSS" />
			  		</c:when>
			  		<c:otherwise>
			  			<spring:message code="question.LogoutEULogin" />
			  		</c:otherwise>
			  	</c:choose>			  	
			  </div>
			  <div class="modal-footer">
			    <a id="btnLogoutEcasFromWelcome" href="${ECASLOGOUT}" target="_blank" class="btn btn-primary" onclick="$('#ecas-dialog').modal('hide');"><spring:message code="label.Yes" /></a>			
				<button id="btnNotLogoutEcasFromWelcome" class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></button>			
			  </div>
			  </div>
			  </div>
			</div>
			
			<script type="text/javascript">
				$("#ecas-dialog").modal('show');
			</script>
		</c:if>
		
		<div class="modal" role="dialog" aria-modal="true" id="download-contribution-dialog" data-backdrop="static">
			<div class="modal-dialog">
	   		<div class="modal-content">
			<form:form action="${contextpath}/home/downloadcontribution" method="post" style="margin: 0px;">
				<div class="modal-header">
					<b><spring:message code="label.DownloadContribution" /></b>
				</div>
				<div class="modal-body">
					<spring:message code="label.EnterContributionId" />
					<input type="text" maxlength="255" name="caseid" id="caseid" /><br />
					<span id="download-contribution-dialog-caseid-error" class="validation-error hideme">
						<spring:message code="validation.invalidContributionId" />
					</span>
					<span id="download-contribution-dialog-caseidinvitation-error" class="validation-error hideme">
						<spring:message code="error.downloadcontributioninvitation" />
					</span>
					<br />
					
					<spring:message code="question.EmailForPDF" />
					<input type="text" maxlength="255" name="email" id="emailcontribution" />
					<span id="download-contribution-dialog-error" class="validation-error hideme">
						<spring:message code="message.ProvideEmail" />
					</span>
					
					<div class="captcha" style="margin-left: 0px; margin-bottom: 20px; margin-top: 20px;">
						<%@ include file="../captcha.jsp" %>
				       	<span id="download-contribution-dialog-error-captcha" class="validation-error hideme">
				       		<spring:message code="message.captchawrongnew" />
				       	</span>
			       	</div>
					
				</div>
				<div class="modal-footer">
					<a href="javascript:;" onclick="checkEmail()" class="btn btn-primary"><spring:message code="label.OK" /></a>
					<a href="javascript:;" class="btn btn-default" onclick="hideModalDialog($('#download-contribution-dialog'));"><spring:message code="label.Cancel" /></a>
				</div>
			</form:form>
			</div>
			</div>
		</div>
	</div>
	
	<%@ include file="../footer.jsp" %>	
</body>
</html>
