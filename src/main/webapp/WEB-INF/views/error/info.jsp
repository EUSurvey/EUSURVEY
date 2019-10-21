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
</head>
<body style="text-align: center;">

	<%@ include file="../header.jsp" %>	
	
	<c:choose>
		<c:when test="${USER != null }">
			<%@ include file="../menu.jsp" %>	
		</c:when>
	</c:choose>	
	
	<div style="margin-top: 120px; margin-bottom: 120px;">
		<div class="alert alert-success"><esapi:encodeForHTML>${message}</esapi:encodeForHTML></div>
		<c:if test="${SurveyLink != null}">
			<a class="btn btn-info" href="${SurveyLink}"><spring:message code="label.BackToSurvey" /></a>
		</c:if>
	</div>
	
	<%@ include file="../footer.jsp" %>
</body>