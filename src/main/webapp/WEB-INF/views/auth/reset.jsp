<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
	<title>EUSurvey - <spring:message code="label.ResetYourPassword" /></title>
	<%@ include file="../includes.jsp" %>
</head>
<body>
	<div class="page-wrap">
	<%@ include file="../header.jsp" %>	
	
	<div class="page">
 		<div class="pageheader">
			<h1><spring:message code="label.ResetYourPassword" /></h1>
		</div>
		
	 	<div class="login">
		 	<form:form action="${contextpath}/auth/resetPost" method="post" >
		 		<fieldset>			 				 	
		 			<input type="hidden" name="code" value="${code}" />
					<p>
						<label for="j_password"><spring:message code="label.Password" /></label>
						<div class="controls">
							<div class="input-prepend">
								<span class="add-on"><i class="icon-lock"></i></span><input class="span2" id="password" name="password" type="password" autocomplete="off" style="width: 300px;" />
							</div>
						</div>	
					</p>
					<p>
						<label for="j_password2"><spring:message code="label.RepeatPassword" /></label>
						<div class="controls">
							<div class="input-prepend">
								<span class="add-on"><i class="icon-lock"></i></span><input class="span2" id="password2" name="password2" type="password" autocomplete="off" style="width: 300px;" />
							</div>
						</div>						
					</p>					
					<c:if test="${error != null}">
						<div id="login-error" class="validation-error">
					 		<esapi:encodeForHTML>${error}</esapi:encodeForHTML>
					 	</div>
				 	</c:if>
					<div style="margin-top: 30px;">
						<input class="btn btn-default" type="submit" value="<spring:message code="label.OK" />"/>
					</div>
		 		</fieldset>
			</form:form>
		</div>	
	</div>
	</div>
	
	<%@ include file="../footer.jsp" %>

</body>
</html>