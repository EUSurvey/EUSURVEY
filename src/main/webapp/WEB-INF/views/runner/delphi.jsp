<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Survey" /></title>
	
	<%@ include file="../includesrunner.jsp" %>
	
	<link id="runnerCss" href="${contextpath}/resources/css/runner.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
    
	<style type="text/css">
	
	</style>

</head>
<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>	
		<%@ include file="../runner/delphiinner.jsp" %>
	</div>
	<%@ include file="../footerNoLanguages.jsp" %>
	
	<c:if test="${message != null}">
		<script type="text/javascript">
			showError('${message}');
		</script>
	</c:if>

	<c:if test="${form.validation != null && form.validation.size() > 0}">
		<script type="text/javascript">
			goToFirstValidationError($("form"));
		</script>
	</c:if>

</body>
</html>
