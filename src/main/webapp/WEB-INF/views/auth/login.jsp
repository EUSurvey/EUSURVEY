<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<!-- login version -->
	<title>EUSurvey - <spring:message code="label.DoLogin" /></title>
	<%@ include file="../includes.jsp" %>
	
	<script type="text/javascript"> 
	
		function requestLink()
		{
			$("#forgotPasswordForm").find(".validation-error").hide();
			$("#forgotPasswordForm").find(".validation-error-keep").hide();			
			
			if ($("#email").val() == null || $("#email").val() == '' || $("#login").val() == null || $("#login").val() == '' || !validateEmail($("#email").val()) )
			{
				$("#errorMessage").show();
				return;
			}
			
			<c:if test="${!captchaBypass}">
				var challenge = getChallenge();
			    var uresponse = getResponse();
			    
			    if (uresponse.trim().length == 0)
			    {
			    	$("#runner-captcha-empty-error").show();
			    	return;
			    }
			</c:if>
			
			$("#forgotPasswordForm").submit();
		}
		

		$(document).ready(function(){
			
			//verify if there is only the system login option
			var showEcas = $("#ecasPanel").length;
			
			if(showEcas == 0)
			{
				$("#sysLoginForm").show();
			}
			
			$("#sysLaunch").click(function(){
				switchPanels();
			});
			
			$("#sysCancel").click(function(){
				
				if(showEcas != 0)
				{
					switchPanels();
				}
								
				$("input[name='username'").val("");
				$("input[name='password'").val("");
			});
			
			if(window.location.href.indexOf("error=true")>-1)
			{
				if(showEcas != 0)
				{
					switchPanels();
				}
			}
			
			if(window.location.href.indexOf("sessionexpired")>-1)
			{
				showError('<spring:message code="error.Session" />');
			}
			
			<c:if test='${mode != null && mode.equalsIgnoreCase("forgotPassword")}'>
				$('#forgot-password-dialog').modal('show');
			</c:if>
			
			<c:if test="${responsive != null}">
				$("#responsiveinfo-dialog").modal("show");
			</c:if>			
		});
		
		function switchPanels()
		{
			$("#connectionOptions").toggle();
			$("#sysLoginForm").toggle();
			if (!($("#sysLoginForm").css('display') == 'none')) {
				$("#username").focus();
			}
		}	
	
	</script>		
</head>
<body>
    <c:choose>
        <c:when test="${redirecttoeulogin != null}">
            <div style="text-align: center; margin-top: 20px;">forwarding to EULogin...</div>
            <div class="page-wrap" style="display: none">
        </c:when>
        <c:otherwise>
            <div class="page-wrap">
        </c:otherwise>
    </c:choose>


		<%@ include file="../header.jsp" %>	
		
		<div class="page" style="padding-top: 40px">
	 		<div class="pageheader">
				<h1><spring:message code="login.title" /></h1>
			</div>
			
			<div id="connectionOptions" style="width:100%; text-align:center">
		 	
			 	<c:if test="${showecas != null}">
			 	
					<div id="ecasPanel" style="">
						
						<c:if test="${casoss !=null}">					
							<h3 style="color:rgb(0, 79, 152);">						
								<spring:message code="login.useCasTitle"/>							
							</h3>
						</c:if>					
						
						<div id="ecasPanelContent" class="well" style="max-width: 420px; padding:30px; padding-bottom: 5px; margin-bottom:0px; float: left;">
							<form:form id="2faeuloginauthenticationform" action="${ecasurl}" style="margin-bottom: 25px">
								<div style="height: 150px;">
									<input type="hidden" name="service" value="<esapi:encodeForHTMLAttribute>${serviceurl}</esapi:encodeForHTMLAttribute>"/>
									<input type="hidden" name="acceptStrength" value="PASSWORD_SMS" />
									
										<c:choose>
											<c:when test="${casoss !=null}">
											<img src="${contextpath}/resources/images/cas_logo.png" alt="cas logo" />
											</c:when>
											<c:otherwise>
												<span style="font-size: 25px; color: #4198b2;"><spring:message code="login.externalTitle" /></span>
											</c:otherwise>
										</c:choose>
										
									<br /><br />
									<c:choose>
										<c:when test="${casoss !=null}">
											<spring:message code="label.CASInfo" /> 			
										</c:when>
										<c:otherwise>
											<div style="text-align: left">
												<spring:message code="login.externalInfo" />
											</div>		
										</c:otherwise>
									</c:choose>
								</div>
								<a class="btn btn-primary" onclick="$(this).closest('form').submit()"><spring:message code="label.Connect" /></a>
							</form:form>
						</div>
						
						<div style="float: left; width: 80px; padding-top: 120px; text-align: center"><spring:message code="label.or" /></div>
						
						<div id="ecasPanelContent2" class="well" style="max-width: 420px; padding:30px; margin-bottom:0px; float: right">
							<form:form action="${ecasurl}">
								<input type="hidden" name="service" value="<esapi:encodeForHTMLAttribute>${serviceurl}</esapi:encodeForHTMLAttribute>"/>
								<c:if test="${require2fa}">
									<input type="hidden" name="acceptStrength" value="PASSWORD_SMS" />
								</c:if>
								<div style="height: 150px;">
									<c:choose>
										<c:when test="${casoss !=null}">
										<img src="${contextpath}/resources/images/cas_logo.png" alt="cas logo" />
										</c:when>
										<c:otherwise>
											<span style="font-size: 25px; color: #4198b2;"><spring:message code="login.internalTitle" /></span>
										</c:otherwise>
									</c:choose>
									
									<br /><br />
									<c:choose>
										<c:when test="${casoss !=null}">
											<spring:message code="label.CASInfo" /> 			
										</c:when>
										<c:when test="${require2fa}">
											<div style="text-align: left">
												<spring:message code="login.externalInfo" />
											</div>
										</c:when>
									</c:choose>
								</div>
								<a class="btn btn-primary" onclick="$(this).closest('form').submit()"><spring:message code="label.Connect" /></a>
							</form:form>
						</div>
					</div>
					
					<div style="clear: both"></div>
					
					<div class="underlined" style="text-align: left; margin-top: 20px; font-size: 90%">
						<spring:message code="label.NoEULoginAccount" />&nbsp;<a target="_blank" href="https://webgate.ec.europa.eu/cas/eim/external/register.cgi"><spring:message code="label.CreateEULoginAccountNow" /></a><br />
						<spring:message code="label.LearnEULogin" arguments="https://webgate.ec.europa.eu/cas/help.html" /><br />
						<spring:message code="label.RegisterMobile" arguments="https://ecas.ec.europa.eu/cas/userdata/mobileApp/manageMyMobileDevices.cgi" /><br />
						<spring:message code="label.AddMobile" arguments="https://webgate.ec.europa.eu/cas/eim/external/restricted/manageMyMobilePhoneNumbers.cgi" />
					</div>
					
					<div id="systemPanel" style="text-align: right; margin-top: 40px;">
						<div id="systemPanelContent"  style=" margin-bottom: 50px;" >			
							<a  id="sysLaunch"><spring:message code="label.LoginSystem" /></a>
						</div>	
					</div>
				</c:if>
			</div>
			
			<div id="sysLoginForm"  class="login" style="display:none; width:90%; margin-left:auto; margin-right:auto; padding:20px 0px 20px 50px; ">
				<h3><spring:message code="login.useSystemTitle" /></h3>
	
				 	<form:form id="loginForm" action="../login" method="post" >
				 		<fieldset>		
				 			<img src="${contextpath}/resources/images/folder-eusurvey.png" style="float:right; margin-right:75px; width:136px;" alt="login logo">	 				 	
				 			<p>
								<label for="username"><spring:message code="label.UserName" /></label>
								<div class="input-group">
							    	<div class="input-group-addon"><span class="glyphicon glyphicon-user" aria-hidden="true"></span></div>
							    	<input class="form-control" id="username" name="username" type="text" maxlength="255" style="width: 300px;" />
							    </div>
							</p>
							<p>
								<label for="password"><spring:message code="label.Password" /></label>
								<div class="input-group">
							    	<div class="input-group-addon"><span class="glyphicon glyphicon-lock" aria-hidden="true"></span></div>
							    	<input class="form-control" id="password" name="password" type="password" maxlength="255" style="width: 300px;" />
							    </div>				
							</p>
							<div style="margin-top: 30px;">
								<input id="sysLoginFormSubmitButton" class="btn btn-default" type="submit" value="<spring:message code="label.DoLogin" />"/>
								&nbsp;
								<a id="sysCancel" class="btn btn-default" type="button" ><spring:message code="label.Cancel" /></a>
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

	<c:if test="${redirecttoeulogin == null}">
	<%@ include file="../footer.jsp" %>
	</c:if>
	<%@ include file="../generic-messages.jsp" %>
	
	<div class="modal" id="forgot-password-dialog" data-backdrop="static">
		<div class="modal-dialog">
    	<div class="modal-content">
			<form:form id="forgotPasswordForm" action="${contextpath}/auth/login" method="post" style="margin: 0px;" >
				<input type="hidden" name="target" value="forgotPassword" />
				<div class="modal-body">
					<spring:message code="label.PleaseEnterYourLogin" /><br />
					<input id="login" type="text" name="login" maxlength="255"  class="form-control"/><br /><br />
					<spring:message code="label.PleaseEnterYourEmail" /><br />
					<input id="email" type="text" name="email" maxlength="255" class="form-control email" /><br />
					<span id="errorMessage" style="color: #f00; display: none;"><spring:message code="error.PleaseEnterYourNameAndEmail" /></span>
					<div style="margin-left: 0px; margin-bottom: 20px; margin-top: 20px;">
						<%@ include file="../captcha.jsp" %>	
		        	</div>				
				</div>
				<div class="modal-footer">
					<a onclick="requestLink();" class="btn btn-primary"><spring:message code="label.OK" /></a>
					<a class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>			
				</div>	
			</form:form>
		</div>
		</div>
	</div>
		
	<c:if test="${error != null}">
		<script type="text/javascript">
			switchPanels();
			showError('<esapi:encodeForHTML>${error}</esapi:encodeForHTML>');
		</script>
 	</c:if>
 	
 	<c:if test="${info != null}">
 		<script type="text/javascript">
 			switchPanels();
			showInfo('<esapi:encodeForHTML>${info}</esapi:encodeForHTML>');
		</script>
 	</c:if>

 	<c:if test="${redirecttoeulogin != null}">
 	    <script type="text/javascript">
            $('#2faeuloginauthenticationform').submit();
 	    </script>
 	</c:if>

</body>
</html>