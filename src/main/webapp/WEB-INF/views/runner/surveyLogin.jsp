<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>	
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page contentType="text/html; charset=UTF-8" session="true" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Survey" /></title>
	<%@ include file="../includes.jsp" %>
	
	<link href="${contextpath}/resources/css/yellowfocus.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css"></link>
		
	<style type="text/css">
		.authenticationdiv {
			width: 220px; margin-left: auto; margin-right: auto;
		}
				
		input[type=password] {
			width: 100%;
		}
		
		hr {
			width: 220px;
    		display: inline-block;
    		margin-bottom: 3px;
    		margin-right: 5px;
    		margin-left: 5px;
    		border-color: #999;
		}
		
		.btn-primary {
			width: 220px;
			margin-bottom: 5px;
		}
		
		.validation-error {
			position: relative;
			margin-top: 10px;
		}
		
	</style>
</head>
<body style="text-align: center;">
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
	
		<div class='${responsive != null ? "responsivepage" : "page"}' style='padding-top: 40px;'>
	
	 		<h1 style="font-size: 30px; text-align: center;">
				${surveyname}
			</h1>
			
		 	<c:if test="${info != null}">
				<div id="login-info" class="alert alert-success">
			 		<esapi:encodeForHTML>${info}</esapi:encodeForHTML>
			 	</div>
		 	</c:if> 
			
			<br /><br />
			
			<c:if test="${ecassecurity != null}">
				<div id="ecasPanel" class="authenticationdiv" style="margin-bottom: 20px;">
					<div id="ecasPanelContent">
						<form:form action="${ecasurl}">
							<input type="hidden" name="service" value="${serviceurl}"/>
							<c:if test="${require2fa}">
								<input type="hidden" name="acceptStrength" value="PASSWORD_SMS" />
							</c:if>
							
							<c:choose>
								<c:when test="${casoss !=null}">
									<a href="javascript:;" onclick="$(this).closest('form').submit()">
										<img src="${contextpath}/resources/images/cas_logo.png" alt="cas logo" />
									</a>
								</c:when>
								<c:otherwise>
									<a href="javascript:;" class="btn btn-primary" onclick="$(this).closest('form').submit()">
										<spring:message code="login.AccessViaEULogin" />
									</a><br />
									<a target="_blank" href="https://webgate.ec.europa.eu/cas/eim/external/register.cgi"><spring:message code="label.Register" /></a>
								</c:otherwise>
							</c:choose>								
						</form:form>
					</div>
				</div>		
			</c:if>
			
			<c:if test="${ecassecurity != null && hidepassword == null}">
				<div style="text-align: center; color: #999; margin-bottom: 25px;">
					<hr />
					<spring:message code="label.OR" />
					<hr />
				</div>
			</c:if>
			
			<c:if test="${hidepassword == null}">
				<div class="authenticationdiv">
				 	<form:form action="${shortname}?surveylanguage=${lang}&draftid=${draftid}" method="post" >
				 		<fieldset>
							<legend style="display: none"><spring:message code="message.SurveyPassword" /></legend>
				 			<input type="hidden" name="redirectFromCheckPassword" value="true" />
							<input type="hidden" name="shortname" value="<esapi:encodeForHTMLAttribute>${shortname}</esapi:encodeForHTMLAttribute>" />
							<input type="hidden" name="draftid" value="<esapi:encodeForHTMLAttribute>${draftid}</esapi:encodeForHTMLAttribute>" />
												
							<div class="form-group">
								<label class="control-label" for="j_password"><spring:message code="label.Password" /></label>
								<div class="input-group">
									<span class="input-group-addon"><span class="glyphicon glyphicon-lock" aria-hidden="true"></span></span>
		   							<input class="form-control" id="j_password" name="password" autocomplete="off" type="password" />
		 						</div>
		 						
		 						<c:if test="${error != null}">
									<div id="login-error" aria-live="polite" class="validation-error" tabindex="0">
								 		<esapi:encodeForHTML>${error}</esapi:encodeForHTML>
								 	</div>
							 	</c:if>
		 						
							</div>
							
							<div style="margin-top: 10px;">
								<input class="btn btn-primary" type="submit" id="btnLogin2SecureSurvey" value="<spring:message code="label.Access" />"/>
							</div>
				 		</fieldset>
					</form:form>
				</div>
			</c:if>
			
			<c:if test="${contact != null}">
				<div style="margin-top: 40px; margin-bottom: 20px; text-align: center">
					<a href="${contextpath}/runner/contactform/${shortname}"><spring:message code="label.ContactSurveyOwner" /></a>
				</div>
			</c:if>		
		</div>
	</div>

	<c:choose>
		<c:when test="${responsive != null}">
			<%@ include file="../footerresponsive.jsp" %>
		</c:when>
		<c:otherwise>
			<%@ include file="../footer.jsp" %>		
		</c:otherwise>
	</c:choose>
	
	<%@ include file="../generic-messages.jsp" %>
	
	<c:if test="${message != null}">
		<script type="text/javascript">
			showInfo('${message}');
		</script>
	</c:if>
	
	<script type="text/javascript">
		$("#j_password").focus();
		
		<c:if test="${error != null}">
			$("#j_password").attr("aria-invalid", "true").attr("aria-describedby", "login-error");
			$(".validation-error").first().focus();
		</c:if>
	</script>
	
	<c:if test="${internalUsersOnly != null}">
		<script type="text/javascript">
			showError('<spring:message code="error.internalUsersOnly" />');
		</script>
	</c:if>

</body>
</html>
