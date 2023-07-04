<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Confirmation" /></title>
	<%@ include file="../includes.jsp" %>
</head>
<body style="text-align: center;">
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
	
		<c:choose>
			<c:when test="${USER != null }">
				<%@ include file="../menu.jsp" %>	
			</c:when>
		</c:choose>	
		
		<div style="padding-top: 120px; padding-bottom: 120px;">
			<div class="alert alert-success"><spring:message code="info.EmailChanged" /></div>
		</div>
	</div>
	
	<%@ include file="../footer.jsp" %>
</body>
