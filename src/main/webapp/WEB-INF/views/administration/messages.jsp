<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.ec.survey.model.administration.GlobalPrivilege" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.SystemMessage" /></title>
	<%@ include file="../includes.jsp" %>	
</head>
<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
		<c:if test="${USER != null && runnermode == null}">
			<%@ include file="../menu.jsp" %>
		</c:if>
		
		<div class="fixedtitle">
			<div class="fixedtitleinner">
				<h1><spring:message code="label.SystemMessage" /></h1>		
			</div>
		</div>
			
		<div class="page880" style="margin-bottom: 0px; padding-top: 160px; min-height: 500px; text-align: center">	
			<div class="alert <esapi:encodeForHTMLAttribute>${message.css}</esapi:encodeForHTMLAttribute>" style="padding: 10px;>
				<img src="${contextpath}/resources/images/<esapi:encodeForHTMLAttribute>${message.icon}</esapi:encodeForHTMLAttribute>" alt="system message icon" />
				<div style="margin-left: 20px;" id="system-message-box-content">${message.text}</div></div>
			</div>
		</div>
	</div>
	<%@ include file="../footer.jsp" %>	

</body>
</html>
