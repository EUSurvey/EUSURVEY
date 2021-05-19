<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>	
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.SurveyUnavailable" /></title>	
	<%@ include file="includes.jsp" %>
		<script type="text/javascript" src="${contextpath}/resources/js/runner.js?version=<%@include file="version.txt" %>"></script>
</head>
<body>
	<div class="page-wrap">
		<%@ include file="headerresponsive.jsp" %>	
	
		<div class="fullpage">	
			<div id="escapeMessageId" style="text-align: center; padding-top: 100px;">${text}</div>			
			
			<c:if test="${oncepublished != null && form.survey.showPDFOnUnavailabilityPage}">
				<div style="text-align: center; margin-top: 40px;">
					<a data-toggle="tooltip" title="${form.getMessage("label.DownloadEmptyPDFversion")}" id="download-survey-pdf-link" class="btn btn-primary" href="#" onclick="downloadSurveyPDF('${form.survey.id}','${form.language.code}','${uniqueCode}')">${form.getMessage("label.DownloadPDFversion")}</a>
					<span id="download-survey-pdf-dialog-running" class="hideme">${form.getMessage("info.FileCreation")}</span>
					<span id="download-survey-pdf-dialog-ready" class="hideme">${form.getMessage("info.FileCreated")}</span>
					<div id="download-survey-pdf-dialog-spinner" class="hideme" style="padding-left: 5px;"><img src="${contextpath}/resources/images/ajax-loader.gif" /></div>
					<br /><a style="white-space: nowrap; overflow-x: visible; display: none; margin-top: 10px" id="download-survey-pdf-dialog-result" target="_blank" class="btn btn-primary" href="<c:url value="/pdf/pubsurvey/${form.survey.id}?lang=${form.language.code}&unique=${uniqueCode}"/>">${form.getMessage("label.Download")}</a>
					<div id="download-survey-pdf-dialog-error" class="hideme">${form.getMessage("error.OperationFailed")}</div>
				</div>
			</c:if>
			
			<c:if test="${oncepublished != null && form.survey.showDocsOnUnavailabilityPage && form.survey.getBackgroundDocuments().size() != 0}">
				<div style="text-align: center; margin-top: 40px;">
					<div style="font-size: 20px"><spring:message code="label.BackgroundDocuments" /></div>
					
					<c:forEach var="link" items="${form.survey.getBackgroundDocumentsAlphabetical()}">
						<div style="margin-top: 5px;" ><a class="link visiblelink" target="_blank" href="<esapi:encodeForHTMLAttribute>${link.value}</esapi:encodeForHTMLAttribute>">${link.key}</a></div>
					</c:forEach>	
				</div>
			</c:if>
		</div>
	</div>

	<%@ include file="footerresponsive.jsp" %>
	
	<script type="text/javascript">
		$("#page-tabs").hide();
	</script>

</body>
</html>
