<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Thanks" /></title>	
	<%@ include file="includes.jsp" %>
	<script type="text/javascript" src="${contextpath}/resources/js/runner.js?version=<%@include file="version.txt" %>"></script>
	
	<script type="text/javascript"> 
		clearAllCookies('${surveyprefix}');
	
		window.open('', '_self', '');
    	window.close();
	
		javascript:window.close();
    	opener.window.focus();
	</script>
</head>
<body>
	<spring:message code="info.autoClose" />
</body>
</html>
