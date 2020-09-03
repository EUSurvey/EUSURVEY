<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Info" /></title>
	<%@ include file="../includes.jsp" %>
</head>
<body style="text-align: center;">
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>	
		
		<c:choose>
			<c:when test="${USER != null }">
				<%@ include file="../menu.jsp" %>	
			</c:when>
		</c:choose>	
		
		<div class='${responsive != null ? "responsivepage" : "page"}' style='padding: 20px; padding-top: 80px; padding-bottom: 100px; max-width: 600px; margin-left: auto; margin-right: auto'>
			<div style="text-align: left;">
				
				${message}
				<c:if test="${SurveyLink != null}">
					<br /><br />
					<a class="btn btn-primary" href="${SurveyLink}"><spring:message code="label.BackToSurvey" /></a>
				</c:if>
				
			</div>
		</div>	
		
	</div>
	
	<%@ include file="../footer.jsp" %>
</body>