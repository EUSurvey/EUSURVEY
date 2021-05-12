<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Statistics" /></title>
	
	<%@ include file="../includesrunner.jsp" %>
	
	<link href="${contextpath}/resources/css/management.css" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/charts.css" rel="stylesheet" type="text/css" />
</head>

<body>
	<h1><spring:message code="label.Statistics" />: ${form.survey.title}</h1>
	<%@ include file="results-ecf.jsp" %>	
</body>
	