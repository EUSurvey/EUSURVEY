<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>	
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Survey" /></title>
	<%@ include file="../includes.jsp" %>
	
	<style type="text/css">
		.authenticationdiv {
			background-color: #eee;
			margin-top: 10px;
			margin-bottom: 10px;
			margin-left: auto;
			margin-right: auto;
			padding: 20px;
			width: 100%;
			max-width: 400px;
		}
				
		input[type=password] {
			width: 100%;
		}
		
	</style>
</head>
<body style="text-align: center;">
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
	
		<div class='${responsive != null ? "responsivepage" : "page"}' style='padding-top: 40px;'>
	
	 		<div class="pageheader">
				<h1><spring:message code="label.Authentication" /></h1>
			</div>
			
			<c:if test="${error != null}">
				<div id="login-error" class="alert alert-danger">
			 		<esapi:encodeForHTML>${error}</esapi:encodeForHTML>
			 	</div>
		 	</c:if>
		 	
		 	<c:if test="${info != null}">
				<div id="login-info" class="alert alert-success">
			 		<esapi:encodeForHTML>${info}</esapi:encodeForHTML>
			 	</div>
		 	</c:if> 
			
			<div style="text-align: center">
				<c:choose>
					<c:when test="${ecassecurity != null}">
						<spring:message code="message.SurveyAuthentication" />
					</c:when>
					<c:otherwise>
						<spring:message code="message.SurveyPassword" />
					</c:otherwise>
				</c:choose>
			</div>
			
			<br /><br />
			
			<c:if test="${ecassecurity != null}">
				<div id="ecasPanel" class="authenticationdiv" style="text-align: center;">
					<div id="ecasPanelContent">
						<form:form action="${ecasurl}">
							<input type="hidden" name="service" value="${serviceurl}"/>
							
							<a  onclick="$(this).closest('form').submit()">
								<c:choose>
									<c:when test="${casoss !=null}">
									<img src="${contextpath}/resources/images/cas_logo.png" alt="cas logo" />
									</c:when>
									<c:otherwise>
										<span style="font-size: 25px"><spring:message code="login.useEULoginTitle" /></span>
									</c:otherwise>
								</c:choose>
								
							</a><br /><br />
							<c:choose>
								<c:when test="${casoss !=null}">
									<spring:message code="label.CASInfo" /> 			
								</c:when>
								<c:otherwise>
							<spring:message code="label.ECASInfo" />			
								</c:otherwise>
							</c:choose>
							
						</form:form>
					</div>
				</div>		
			</c:if>
			
			<c:if test="${ecassecurity != null && hidepassword == null}">
				<div style="text-align: center">
					<spring:message code="label.or" />
				</div>
			</c:if>
			
			<c:if test="${hidepassword == null}">
				<div class="authenticationdiv">
				 	<form:form action="${shortname}?surveylanguage=${lang}" method="post" >
				 		<fieldset>			 				 	
				 			<input type="hidden" name="redirectFromCheckPassword" value="true" />
							<input type="hidden" name="shortname" value="<esapi:encodeForHTMLAttribute>${shortname}</esapi:encodeForHTMLAttribute>" />
							<input type="hidden" name="draftid" value="<esapi:encodeForHTMLAttribute>${draftid}</esapi:encodeForHTMLAttribute>" />
						
							<p>
								<label for="j_password"><spring:message code="label.Password" /></label>
								<div class="controls">
									<div class="input-prepend">
										<span class="add-on"><i class="icon-lock"></i></span><input class="span2" id="j_password" name="password" autocomplete="off" type="password" />
									</div>
								</div>						
							</p>
							<div style="margin-top: 30px;">
								<input class="btn btn-default" type="submit" id="btnLogin2SecureSurvey" value="<spring:message code="label.DoLogin" />"/>
							</div>
				 		</fieldset>
					</form:form>
				</div>
			</c:if>
			
			<br /><br />
			
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
	</script>
	
	<c:if test="${internalUsersOnly != null}">
		<script type="text/javascript">
			showError('<spring:message code="error.internalUsersOnly" />');
		</script>
	</c:if>

</body>
</html>
