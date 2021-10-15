<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="esapi"
	uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
<title>EUSurvey - Terms of Service</title>
<%@ include file="../includes.jsp"%>
	<link href="${contextpath}/resources/css/yellowfocus.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css"></link>

<style>

body {
	padding-left: 5px;
	padding-right: 5px;
}

#tos h1 {
	font-size: 110% !important;
	margin-top: 30px !important;
	color: #000;
	font-weight: bold;
}

#tos h2 {
	font-size: 105% !important;
	color: #000;
}

#tos h1 a, #tos h2 a {
	color: #000;
}

#tos h1 a:hover, #tos h2 a:hover {
	text-decoration: none;
	cursor: default;
}

</style>

</head>

<body id="tosBody">
	<div class="page-wrap" style="padding-bottom: 0">
		<c:if test="${readonly != null}">
			<%@ include file="../header.jsp"%>
		</c:if>
	
		<c:choose>
			<c:when test="${responsive != null}">
				<div class="page" style="width: auto;">
			</c:when>
			<c:when
				test="${USER != null && runnermode == null && readonly != null }">
				<%@ include file="../menu.jsp"%>
				<div class="page" style="margin-top: 110px">
			</c:when>
			<c:otherwise>
				<div class="page">
			</c:otherwise>
		</c:choose>	
			
		<form:form id="logoutform" action="${contextpath}/j_spring_security_logout" method="post">
	    </form:form>
	
		<form:form id="tos-form" action="${contextpath}/auth/ps" method="post">
			<c:if test="${readonly == null}">
				<input type="hidden" name="user" value="${user.id}" />
			</c:if>
	
			<div style="margin-bottom: 20px; margin-top: 20px;">
					
				<div id="tos">
								
					<!-- <h1 class="tospage1">EUSurvey - <spring:message code="label.PrivacyStatement" /></h1> -->
			
					<c:choose>
						<c:when test="${oss}">
							<%@ include file="tos_language/ps.oss_en.jsp"%>							
						</c:when>
						<c:otherwise>
							<jsp:include page="tos_language/tos_${pageContext.response.locale.language}.jsp" flush="true" />
						</c:otherwise>
					</c:choose>
					<div class="tospage1" style="text-align: center">
						<c:if test="${readonly == null}">
							<input type="submit" class="btn btn-primary" value="<spring:message code="label.Iaccept" />" />
							&nbsp;
							<a aria-label="<spring:message code="label.Idonotaccept" />" tabindex="0" class="btn btn-default" onclick="logout()"><spring:message code="label.Idonotaccept" /></a>
						</c:if>
					</div>		
	
				</div>
			
		</form:form>
	
		</div>
	</div>
	<c:if test="${readonly != null}">
		<%@ include file="../footer.jsp"%>
	</c:if>

</body>

</html>