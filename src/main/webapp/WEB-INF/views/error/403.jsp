<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Error" /></title>
	<%@ include file="../includes.jsp" %>
</head>
<body style="text-align: center;">

	<%@ include file="../header.jsp" %>	
	
	<div class='${responsive != null ? "responsivepage" : "page"}' style='padding: 20px; margin-top: 80px; margin-bottom: 100px; max-width: 600px; margin-left: auto; margin-right: auto'>
	
		<div class="hidden-xs" style="float: left; height: 200px;">
			<span class="glyphicon glyphicon-ban-circle" style="font-size: 100px; color: #bd281d; margin-right: 60px; margin-top: 10px"></span>
		</div>
		<div style="text-align: left;">
			<h1 style="margin-bottom: 20px;">
				<div class="visible-xs" style="float: left">
					<span class="glyphicon glyphicon-ban-circle" style="font-size: 30px; color: #bd281d; margin-right: 10px; "></span>
				</div>
				<spring:message code="label.Forbidden" />
			</h1>
			<spring:message code="support.new403" arguments="${contextpath}/home/support?error=1" />
		</div>
		
	</div>
	
	<%@ include file="../footer.jsp" %>		
</body>