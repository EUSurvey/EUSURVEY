<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
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
		<span class="alert alert-danger"><spring:message code="error.FileTooLarge" /></span>
	</div>
	
	<%@ include file="../footer.jsp" %>
</body>