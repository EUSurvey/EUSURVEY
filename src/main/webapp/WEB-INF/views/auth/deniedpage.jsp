<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.AccessDenied" /></title>
	<%@ include file="../includes.jsp" %>
</head>
<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>	
		
		<div class="page">
	 		<div class="pageheader">
				<h1><spring:message code="label.AccessDenied" />!</h1>
			</div>		
		
			<div id="login-error" class="alert alert-danger">
		 		<spring:message code="error.NoAccessPrivileges" />
		 	</div>	 		 	
		</div>
	</div>
	
	<%@ include file="../footer.jsp" %>	

</body>
</html>