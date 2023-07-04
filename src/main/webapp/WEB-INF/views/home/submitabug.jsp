<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>Submit a bug</title>	
	<%@ include file="../includes.jsp" %>	
</head>
<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
		<c:choose>
			<c:when test="${USER != null }">
				<%@ include file="../menu.jsp" %>	
				<div class="page" style="padding-top: 110px">
			</c:when>
			<c:otherwise>
				<div class="page">
			</c:otherwise>
		</c:choose>	
		
			<div class="pageheader">
				<h1>Submit a bug</h1>
			</div>
			
			If you have any problems with using EUSurvey, please contact our support team:<br /><br />
			<a href="mailto:DIGIT-EUSURVEY-SUPPORT@ec.europa.eu">DIGIT-EUSURVEY-SUPPORT@ec.europa.eu</a>
						
		</div>
	</div>
	
	<%@ include file="../footer.jsp" %>	

</body>
</html>
