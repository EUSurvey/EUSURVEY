<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Survey" /></title>
	
	<%@ include file="../includesrunner.jsp" %>
	<%@ include file="../generic-messages.jsp" %>

	<link id="runnerCss" href="${contextpath}/resources/css/runner.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${contextpath}/resources/js/runner2.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/graph_data_loader.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/runnerviewmodels.js?version=<%@include file="../version.txt" %>"></script>

</head>
<body>
	<c:set var="mode" value="delphiStartPage" />

	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
		<%@ include file="../runner/contributionLinkModals.jsp" %>
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
