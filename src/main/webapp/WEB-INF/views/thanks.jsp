<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Thanks" /></title>	
	<%@ include file="includes.jsp" %>
	<script type="text/javascript" src="${contextpath}/resources/js/Chart.min.js?version=<%@include file="version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/runner.js?version=<%@include file="version.txt" %>"></script>
	<script type="text/javascript"> 
		$(function() {					
			clearLocalBackupForPrefix('${surveyprefix}');
			<c:if test="${redirect != null}">
				setTimeout(function(){ window.location = "${redirect}"; }, 10000);				
			</c:if>		
		});
	</script>
		
	<c:if test='${form.survey.skin != null && form.survey.skin.name.equals("New Official EC Skin")}'>
		<link href="${contextpath}/resources/css/ecnew.css" rel="stylesheet" type="text/css"></link>
	</c:if>
	<c:if test='${form.survey.skin != null && form.survey.skin.name.equals("ECA Skin")}'>
		<link href="${contextpath}/resources/css/ecanew.css" rel="stylesheet" type="text/css"></link>
	</c:if>
	<c:if test='${form.survey.skin != null && form.survey.skin.name.equals("ECA 2023")}'>
		<link href="${contextpath}/resources/css/eca2023.css" rel="stylesheet" type="text/css"></link>
	</c:if>
	
	<link id="runnerCss" href="${contextpath}/resources/css/yellowfocus.css?version=<%@include file="version.txt" %>" rel="stylesheet" type="text/css"></link>
</head>
<body>
	<div class="page-wrap">
		<c:choose>
			<c:when test='${responsive == null && form.survey.skin != null && form.survey.skin.name.equals("New Official EC Skin")}'>
				<div id="top-page" style="width: 1302px; margin-left: auto; margin-right: auto;">
				<%@ include file="headerecnew.jsp" %>	 
			</c:when>
			<c:when test='${responsive == null && form.survey.skin != null && form.survey.skin.name.equals("ECA Skin")}'>
				<div id="top-page" style="width: 1302px; margin-left: auto; margin-right: auto;">
				<%@ include file="headerECAnew.jsp" %>	 
			</c:when>
			<c:when test='${responsive == null && form.survey.skin != null && form.survey.skin.name.equals("ECA 2023")}'>
				<div id="top-page" style="width: 1302px; margin-left: auto; margin-right: auto;">
				<%@ include file="headerECA2023.jsp" %>	 
			</c:when>
			<c:otherwise>
				<%@ include file="header.jsp" %>	 
			</c:otherwise>
		</c:choose>
	
		<c:choose>
			<c:when test="${form.survey.isECF}">
				<div class="fullpagesmaller">
					<%@ include file="thanksinner.jsp" %>
					<%@ include file="generic-messages.jsp" %>
				</div>
			</c:when>
			<c:otherwise>
				<div class="fullpage" style="padding-top: 100px;">
					<%@ include file="thanksinner.jsp" %>
					<%@ include file="generic-messages.jsp" %>
				</div>
			</c:otherwise>
		</c:choose>
		
	</div>
	
	<c:choose>
		<c:when test='${responsive == null && form.survey.skin != null && form.survey.skin.name.equals("New Official EC Skin")}'>
			</div>  
			<div style="text-align: center">
				<%@ include file="footerNoLanguagesECnew.jsp" %>
			</div>
		</c:when>
		<c:when test='${responsive == null && form.survey.skin != null && form.survey.skin.name.equals("ECA Skin")}'>
			</div>  
			<div style="text-align: center">
				<%@ include file="footerNoLanguagesECAnew.jsp" %>
			</div>
		</c:when>
		<c:when test='${responsive == null && form.survey.skin != null && form.survey.skin.name.equals("ECA 2023")}'>
			</div>  
			<div style="text-align: center">
				<%@ include file="footerNoLanguagesECA2023.jsp" %>
			</div>
		</c:when>
		<c:when test="${responsive == null && runnermode == true}">
			<%@ include file="footerSurveyLanguages.jsp" %>
		</c:when>
		<c:otherwise>
			<%@ include file="footer.jsp" %>
		</c:otherwise>
	</c:choose>		

</body>
</html>
