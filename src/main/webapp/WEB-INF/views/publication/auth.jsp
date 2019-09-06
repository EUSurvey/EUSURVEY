<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>	
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.PublishedResults" /></title>
	<%@ include file="../includes.jsp" %>
</head>
<body style="text-align: center;">

	<%@ include file="../header.jsp" %>
	
	<div class="page">
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
		
		<spring:message code="message.ResultsPassword" />
		<br /><br />
		
		<div class="login">
		 	<form:form action="${shortname}" method="post" >
		 		<fieldset>			 				 	
		 			<p>
						<label for="p_password"><spring:message code="label.Password" /></label>
						<div class="controls">
							<div class="input-prepend">
								<span class="add-on"><i class="icon-lock"></i></span><input class="span2" id="p_password" name="publicationpassword" autocomplete="off" type="password" style="width: 300px;" />
							</div>
						</div>						
					</p>
					<div style="margin-top: 30px;">
						<input class="btn btn-default" type="submit" value="<spring:message code="label.Login" />"/>
					</div>
		 		</fieldset>
			</form:form>
		</div>	
		
	</div>

	<%@ include file="../footerNoLanguages.jsp" %>
	
	<c:if test="${message != null}">
		<script type="text/javascript">
			showMessage('${message}');
		</script>
	</c:if>

</body>
</html>
