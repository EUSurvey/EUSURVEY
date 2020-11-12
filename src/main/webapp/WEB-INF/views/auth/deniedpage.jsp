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
		
			<div class='${responsive != null ? "responsivepage" : "page"}' style='padding: 20px; padding-top: 80px; padding-bottom: 100px; max-width: 600px; margin-left: auto; margin-right: auto'>
				<div class="hidden-xs" style="float: left; height: 200px;">
					<span class="glyphicon glyphicon-ban-circle" style="font-size: 100px; color: #bd281d; margin-right: 60px; margin-top: 10px"></span>
				</div>
				<div style="text-align: left;">
					<h1 style="margin-bottom: 20px;">
						<div class="visible-xs" style="float: left">
							<span class="glyphicon glyphicon-ban-circle" style="font-size: 30px; color: #bd281d; margin-right: 10px; "></span>
						</div>
						<spring:message code="label.AccessDenied" />
					</h1>
					<spring:message code="error.NoAccessPrivileges" />
				</div>
			</div>			
		</div>
	</div>
	
	<%@ include file="../footer.jsp" %>	

</body>
</html>