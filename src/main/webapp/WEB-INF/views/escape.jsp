<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>	
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.SurveyUnavailable" /></title>	
	<%@ include file="includes.jsp" %>
	<script type="text/javascript" src="${contextpath}/resources/js/runner.js?version=<%@include file="version.txt" %>"></script>
	
	<c:if test='${form.survey.skin != null && form.survey.skin.name.equals("New Official EC Skin")}'>
		<link href="${contextpath}/resources/css/ecnew.css" rel="stylesheet" type="text/css"></link>
	</c:if>
	<c:if test='${form.survey.skin != null && form.survey.skin.name.equals("ECA Skin")}'>
		<link href="${contextpath}/resources/css/ecanew.css" rel="stylesheet" type="text/css"></link>
	</c:if>
	<c:if test='${form.survey.skin != null && form.survey.skin.name.equals("ECA 2023")}'>
		<link href="${contextpath}/resources/css/eca2023.css" rel="stylesheet" type="text/css"></link>
	</c:if>
</head>
<body>
	<div class="page-wrap">
		<c:choose>
			<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("New Official EC Skin")}'>
				<div id="top-page" style="width: 1302px; margin-left: auto; margin-right: auto;">
				<%@ include file="headerecnew.jsp" %>	 
			</c:when>
			<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("ECA Skin")}'>
				<div id="top-page" style="width: 1302px; margin-left: auto; margin-right: auto;">
				<%@ include file="headerECAnew.jsp" %>	 
			</c:when>
			<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("ECA 2023")}'>
				<div id="top-page" style="width: 1302px; margin-left: auto; margin-right: auto;">
				<%@ include file="headerECA2023.jsp" %>	 
			</c:when>
			<c:otherwise>
				<%@ include file="header.jsp" %>	 
			</c:otherwise>
		</c:choose>
	
		<div class="fullpage">	
			<div id="escapeMessageId" style="text-align: center; padding-top: 100px;">${text}</div>	
			
			<c:if test="${oncepublished != null && form.survey.showPDFOnUnavailabilityPage}">
				<div style="text-align: center; margin-top: 40px;">
					<a data-toggle="tooltip" title="${form.getMessage("label.DownloadEmptyPDFversion")}" aria-label="${form.getMessage("label.DownloadPDFversion")}" id="download-survey-pdf-link" class="btn btn-primary" href="#" onclick="downloadSurveyPDF('${form.survey.id}','${form.language.code}','${uniqueCode}')">${form.getMessage("label.DownloadPDFversion")}</a>
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
						<span style="margin: 5px;"><a class="link visiblelink" target="_blank" href="<esapi:encodeForHTMLAttribute>${link.value}</esapi:encodeForHTMLAttribute>">${link.key}</a></span>
					</c:forEach>	
				</div>
			</c:if>
					
		</div>
	
	<c:choose>
		<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("New Official EC Skin")}'>
			</div> 
			</div> 
			<div style="text-align: center">
				<%@ include file="footerNoLanguagesECnew.jsp" %>
			</div>
		</c:when>
		<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("ECA Skin")}'>
			</div> 
			</div> 
			<div style="text-align: center">
				<%@ include file="footerNoLanguagesECAnew.jsp" %>
			</div>
		</c:when>
		<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("ECA 2023")}'>
			</div> 
			</div> 
			<div style="text-align: center">
				<%@ include file="footerNoLanguagesECA2023.jsp" %>
			</div>
		</c:when>
		<c:otherwise>
			</div> 
			<%@ include file="footerSurveyLanguages.jsp" %>	
		</c:otherwise>
	</c:choose>


</body>
</html>
