<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Registration" /></title>	
	<%@ include file="../includes.jsp" %>	
</head>
<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>	
	
		<div class="page">
			<div class="pageheader">
				<h1><spring:message code="label.RegistrationComplete" /></h1>
			</div>
			
			<spring:message code="registration.AccountReady" />
			<br /><br />
			
			<a class="btn btn-default" href="<c:url value="/auth/login"/>"><spring:message code="label.GoToLogin" /></a>
			
		</div>
	</div>
	
	<%@ include file="../footer.jsp" %>	

</body>
</html>
