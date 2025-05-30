<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Test" /></title>
	
	<%@ include file="../includes.jsp" %>
	
	<link id="runnerCss" href="${contextpath}/resources/css/quiz.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	<link id="runnerCss" href="${contextpath}/resources/css/runner.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/management.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${contextpath}/resources/js/runner.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/Chart.min.js?version=<%@include file="../version.txt" %>"></script>
		
	<c:if test="${form.survey.skin != null && !form.wcagCompliance}">
		<style type="text/css">
			${form.survey.skin.getCss()}
		</style>
	</c:if>
	
	<style type="text/css">
		#runner-content {
			background-color: #fff;
		}
		.right-area {
			border-left: 2px solid #eee;
		}
		.page {
		 	background-color: #fff;
		}

		.customtext {
			margin-top: 30px;
			margin-bottom: 30px;
		}
	</style>
	
	<script type="text/javascript"> 
		$(function() {
			clearLocalBackupForPrefix('${surveyprefix}');
			$("#form-menu-tab").addClass("active");
			$("#test-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
		});
	</script>
		
</head>
<body id="bodyManagementTest">
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
		<%@ include file="../menu.jsp" %>
		<%@ include file="formmenu.jsp" %>	
		
		<%@ include file="../runner/saResultInner.jsp" %>	
	</div>

	<%@ include file="../footer.jsp" %>	
	
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
