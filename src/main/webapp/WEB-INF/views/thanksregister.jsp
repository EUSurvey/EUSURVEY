<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Thanks" /></title>	
	<%@ include file="includes.jsp" %>
	<script type="text/javascript" src="${contextpath}/resources/js/runner.js?version=<%@include file="version.txt" %>"></script>
	
	<script type="text/javascript"> 
		$(function() {					
			clearLocalBackupForPrefix('${surveyprefix}');
			
			<c:if test="${redirect != null}">
				window.location = "${redirect}";
			</c:if>
		});			
	</script>
</head>
<body>
	<div class="page-wrap">
	<%@ include file="header.jsp" %>	

	<div class="fullpage">
		<div style="text-align: center; margin: 200px;">		
			<spring:message code="message.Registered" />		
		</div>
		</div>
	</div>

<%@ include file="footer.jsp" %>	

</body>
</html>
