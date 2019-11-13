<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Error" /></title>
	<%@ include file="../includes.jsp" %>
</head>
<body id="error404" style="text-align: center;">
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>	
	
		<div class='${responsive != null ? "responsivepage" : "page"}' style='padding-top: 40px;'>
			<div class="pageheader">
				<h1><spring:message code="label.PageNotFound" /></h1>
			</div>
		
			<spring:message code="error.405" /><br /><br />
			<spring:message code="support.text" />
		</div>
	</div>
	<%@ include file="../footer.jsp" %>
</body>