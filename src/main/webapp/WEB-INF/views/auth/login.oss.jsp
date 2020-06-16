<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
	<!-- login.oss version -->
	<title>EUSurvey - <spring:message code="label.DoLogin" /></title>
	<%@ include file="../includes.jsp" %>	
	<script type="text/javascript"> 
	
		function requestLink()
		{
			if ($("#email").val() == null || $("#email").val() == '' || $("#login").val() == null || $("#login").val() == '' || !validateEmail($("#email").val()))
			{
				$("#errorMessage").show();
				return;
			}
			
			$("#forgotPasswordForm").submit();
		}
		
		$(document).ready(function(){

			$("#sysCancel").click(function(){
				$("input[name='username'").val("");
				$("input[name='password'").val("");
			});
			
			<c:if test='${mode != null && mode.equalsIgnoreCase("forgotPassword")}'>
				$('#forgot-password-dialog').modal('show');
			</c:if>
			
		});

	</script>
		
</head>
<body>
	 <div class="page-wrap">
	<%@ include file="../header.jsp" %>	
	
		<div class="page" style="padding-top: 40px">
 		<div class="pageheader">
			<h1><spring:message code="login.title" /></h1>
		</div>
	 	
	 	<div id="sysLoginForm"  class="login" style="width:90%; margin-left:auto; margin-right:auto; padding:20px 0px 20px 50px; ">
			<h3><spring:message code="login.useSystemTitle" /></h3>

			 	<form:form id="loginForm" action="../j_spring_security_check" method="post" >
			 		<fieldset>		
			 			<img src="${contextpath}/resources/images/folder-eusurvey.png" style="float:right; margin-right:75px; width:136px;" alt="login logo">	 				 	
			 			<p>
							<label for="username"><spring:message code="label.UserName" /></label>
							<div class="controls">
								<div class="input-prepend">
									<span class="add-on"><i class="icon-user"></i></span><input class="span2" id="username" name="username" type="text" maxlength="255" autocomplete="off" style="width: 300px;" />
								</div>
							</div>
						</p>
						<p>
							<label for="password"><spring:message code="label.Password" /></label>
							<div class="controls">
								<div class="input-prepend">
									<span class="add-on"><i class="icon-lock"></i></span><input class="span2" id="password" name="password" type="password" maxlength="255" autocomplete="off" style="width: 300px;" />
								</div>
							</div>						
						</p>
						<div style="margin-top: 30px;">
							<input class="btn btn-default" type="submit" value="<spring:message code="label.DoLogin" />"/>
							&nbsp;
							<a id="sysCancel" class="btn btn-default" type="button" >Cancel</a>
							&#160;&#160;<spring:message code="label.or" />&#160;&#160;<a class="visiblelink disabled" href="${contextpath}/runner/NewSelfRegistrationSurvey"><spring:message code="label.Register" /></a>
							
							<br />
							<div style="margin-left: 200px"><a class="redlink"  onclick="$('#forgot-password-dialog').modal('show');"><spring:message code="label.ForgotYourPassword" /></a></div>
						</div>
			 		</fieldset>
				</form:form>			

			</div>	
		
	</div>
	
	<div style="clear: both"></div>
	</div>
	
	<%@ include file="../footer.jsp" %>
	<%@ include file="../generic-messages.jsp" %>
	
	<div class="modal" id="forgot-password-dialog" data-backdrop="static" style="width: 400px; margin-left: -200px;">
		<form:form id="forgotPasswordForm" action="${contextpath}/auth/login" method="post" style="margin: 0px;" >
			<input type="hidden" name="target" value="forgotPassword" />
			<div class="modal-body">
				<spring:message code="label.PleaseEnterYourLogin" /><br />
				<input id="login" type="text" name="login" maxlength="255" /><br /><br />
				<spring:message code="label.PleaseEnterYourEmail" /><br />
				<input id="email" type="text" name="email" maxlength="255" class="email" /><br />
				<span id="errorMessage" style="color: #f00; display: none;"><spring:message code="error.PleaseEnterYourNameAndEmail" /></span>
				<div style="margin-left: 0px; margin-bottom: 20px; margin-top: 20px;">
					<%@ include file="../captcha.jsp" %>			
	        	</div>
			</div>
			<div class="modal-footer" style="height:32px;">
				<a  onclick="requestLink();" class="btn btn-primary"><spring:message code="label.OK" /></a>
				<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>			
			</div>	
		</form:form>
	</div>
	
	<c:if test="${error != null}">
		<script type="text/javascript">
			showError('<esapi:encodeForHTML>${error}</esapi:encodeForHTML>');
		</script>
 	</c:if>
 	
 	<c:if test="${info != null}">
 		<script type="text/javascript">
			showInfo('<esapi:encodeForHTML>${info}</esapi:encodeForHTML>');
		</script>
 	</c:if>
	

</body>
</html>