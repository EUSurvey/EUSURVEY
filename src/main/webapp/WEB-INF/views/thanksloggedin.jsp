<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Thanks" /></title>	
	<%@ include file="includes.jsp" %>		
	<script type="text/javascript" src="${contextpath}/resources/js/runner.js?version=<%@include file="version.txt" %>"></script>
	<script type="text/javascript"> 
		$(function() {					
			clearAllCookies('${surveyprefix}');
			
			<c:if test="${redirect != null}">
				window.location = "${redirect}";
			</c:if>
		});			
	</script>
</head>
<body>

	<%@ include file="header.jsp" %>
	<%@ include file="menu.jsp" %>
	<%@ include file="management/formmenu.jsp" %>
	
	<div class="fullpage">
		<%@ include file="thanksinner.jsp" %>			
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
