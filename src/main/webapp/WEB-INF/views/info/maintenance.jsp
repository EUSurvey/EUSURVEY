<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.DownForMaintenance" /></title>
	<%@ include file="../includes.jsp" %>
</head>
<body style="text-align: center;">

	<%@ include file="../header-empty.jsp" %>	

	<div class="page" style="padding-top: 60px;">
	
		<img src="${contextpath}/resources/images/logo_Eusurvey.png" alt="EUSurvey logo" />

		<div style="margin-left: 185px;">			
			<h2><hr /><div style="padding-left: 30px;"><spring:message code="error.MaintenanceNew" /></div><hr /></h2>
		</div>

	</div>

</body>