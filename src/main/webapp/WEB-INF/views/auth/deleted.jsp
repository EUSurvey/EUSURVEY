<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Info" /></title>
	<%@ include file="../includes.jsp" %>
</head>
<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>	
		
		<<div style="margin-top: 120px; margin-bottom: 120px; text-align: center">
	 		<spring:message code="message.AccountDeactivated" arguments="${contextpath}/home/documentation" />	 	
		</div>
	
	</div>
	
	<%@ include file="../footer.jsp" %>	

</body>
</html>