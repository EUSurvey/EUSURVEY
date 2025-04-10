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
				setTimeout(function(){ window.location = "${redirect}"; }, 10000);
			</c:if>
		});			
	</script>
</head>
<body>
	<div class="page-wrap">
	<%@ include file="header.jsp" %>
	<%@ include file="menu.jsp" %>
	<%@ include file="management/formmenu.jsp" %>
	
	<div class="fullpage">
		<%@ include file="thanksinner.jsp" %>			
	</div>
	</div>
	
	<c:choose>
		<c:when test="${runnermode == true}">
			<%@ include file="footerSurveyLanguages.jsp" %>
		</c:when>
		<c:otherwise>
			<%@ include file="footer.jsp" %>
		</c:otherwise>
	</c:choose>

</body>
</html>
