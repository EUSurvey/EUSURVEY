<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>	
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.MyAccount" /></title>
	
	<%@ include file="../includes.jsp" %>
	
	<script>	
		$(function() {					
			$("#settings-menu-tab").addClass("active");
			$("#myaccount-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
		});	
	</script>
</head>
<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
		<%@ include file="../menu.jsp" %>
		<%@ include file="settingsmenu.jsp" %>	
	
		<div class="fixedtitleform">
			<div class="fixedtitleinner" style="width:880px">
				<div style="margin-top: 20px; text-align: center">				
					<c:if test='${!USER.type.equalsIgnoreCase("ECAS")}'>
						<a onclick="$('.validation-error').empty(); $('#change-password-dialog').modal('show');" class="btn btn-default"><spring:message code="label.ChangePassword" /></a>			
						<a onclick="$('.validation-error').empty(); $('#change-email-dialog').modal('show');" class="btn btn-default"><spring:message code="label.ChangeEmailAddress" /></a>
					</c:if>
					<a onclick="$('.validation-error').empty(); $('#change-lang-dialog').modal('show');" class="btn btn-default"><spring:message code="label.ChangeLanguage" /></a>
					<a onclick="$('.validation-error').empty(); $('#change-pivot-lang-dialog').modal('show');" class="btn btn-default"><spring:message code="label.ChangeDefaultPivotLanguage" /></a>
				</div>	
						
			</div>
		</div>
		
		<div class="page880" style="padding-bottom: 0px; padding-top: 210px;">
					
			<div style="margin-left: auto; margin-right: auto; width: 630px; margin-bottom: 20px;">		
			
				<table class="table table-striped table-bordered">
					<tr>
						<td><spring:message code="label.Name" /></td>
						<td><esapi:encodeForHTML>${USER.name}</esapi:encodeForHTML></td>
					</tr>
					<tr>
						<td><spring:message code="label.Email" /></td>
						<td><esapi:encodeForHTML>${USER.email}</esapi:encodeForHTML></td>
					</tr>
					<tr>
						<td><spring:message code="label.Password" /></td>
						<td>*******</td>
					</tr>
					<tr>
						<td><spring:message code="label.Language" /></td>
						<td><esapi:encodeForHTML>${USER.language}</esapi:encodeForHTML></td>
					</tr>
					<tr>
						<td><spring:message code="label.DefaultPivotLanguage" /></td>
						<td><esapi:encodeForHTML>${USER.defaultPivotLanguage}</esapi:encodeForHTML></td>
					</tr>
				</table>		
			</div>
				
		</div>
	</div>

	<%@ include file="../footer.jsp" %>	

	<div class="modal" id="change-password-dialog" data-backdrop="static" style="">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
		<div class="modal-header"><spring:message code="label.ChangePassword" /></div>
		<div class="modal-body">
			<div style="padding: 10px;">
			
				<form:form id="change-password-form" action="myAccount" method="POST">
					<input type="hidden" name="target" value="changePassword" />
			
					<label for="change-password-old-password"><spring:message code="label.OldPassword" /></label><br />
					<input maxlength="255" name="oldpassword" class="required" type="password" autocomplete="off" id="change-password-old-password" style="width:220px;" /><br /><br />
					
					<label for="change-password-new-password"><spring:message code="label.NewPassword" /></label><br />
					<input maxlength="16" name="newpassword" class="required comparable" type="password" autocomplete="off" id="change-password-new-password" style="width:220px;" /><br /><br />
					
					<label for="change-password-new-password2"><spring:message code="label.RepeatNewPassword" /></label><br />
					<input maxlength="16" name="newpassword2" class="required comparable-second" type="password" autocomplete="off" id="change-password-new-password2" style="width:220px;" />		
				
				</form:form>
				
				<div class="validation-error"><esapi:encodeForHTML>${error}</esapi:encodeForHTML></div>
					
			</div>
		</div>
		<div class="modal-footer">
			<img id="add-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a  onclick="validateInputAndSubmit($('#change-password-form'));" class="btn btn-info"><spring:message code="label.OK" /></a>
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>					
		</div>
		</div>
		</div>
	</div>
	
	<div class="modal" id="change-email-dialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
		<div class="modal-header"><spring:message code="label.ChangeEmailAddress" /></div>
		<div class="modal-body">
			<div style="padding: 10px;">
			
				<form:form id="change-email-form" action="myAccount" method="POST">
					<input type="hidden" name="target" value="changeEmail" />
				
					<label for="change-email-password"><spring:message code="label.Password" /></label><br />
					<input name="password" autocomplete="off" class="required" type="password" id="change-email-password" style="width:220px;" /><br /><br />
					
					<label for="change-email-new-email"><spring:message code="label.NewEmailAddress" /></label><br />
					<input name="newemail" class="required comparable" type="text" maxlength="255" id="change-email-new-email" style="width:220px;" /><br /><br />
					
					<label for="change-email-new-email2"><spring:message code="label.RepeatNewEmailAddress" /></label><br />
					<input name="newemail2" class="required comparable-second" type="text" maxlength="255" id="change-email-new-email2" style="width:220px;" />
				
				</form:form>
				
				<div class="validation-error"><esapi:encodeForHTML>${error}</esapi:encodeForHTML></div>
					
			</div>
		</div>
		<div class="modal-footer">
			<img id="add-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
					
			<a  onclick='validateInputAndSubmit($("#change-email-form"));' class="btn btn-info"><spring:message code="label.OK" /></a>
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>			
		</div>
		</div>
		</div>
	</div>
	
	<div class="modal" id="change-lang-dialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
		<div class="modal-header"><spring:message code="label.ChangeLanguage" /></div>
		<div class="modal-body">
			<div style="padding: 10px;">
			
				<form:form id="change-lang-form" action="myAccount" method="POST">
					<input type="hidden" name="target" value="changeLanguage" />
					
					<label for="change-lang"><spring:message code="label.Language" /></label>
					<select name="change-lang">
						<c:forEach items="${languages}" var="language">				
							<c:if test="${language.official}">
								<c:choose>
									<c:when test="${language.code == USER.language}">
										<option selected="selected" value="<esapi:encodeForHTMLAttribute>${language.code}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML><spring:message code="label.lang.${language.englishName}" /></esapi:encodeForHTML></option>
									</c:when>
									<c:otherwise>
										<option value="<esapi:encodeForHTMLAttribute>${language.code}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML><spring:message code="label.lang.${language.englishName}" /></esapi:encodeForHTML></option>
									</c:otherwise>
								</c:choose>
							</c:if>
						</c:forEach>
					</select>
					
				</form:form>
				
				<div class="validation-error"><esapi:encodeForHTML>${error}</esapi:encodeForHTML></div>
					
			</div>
		</div>
		<div class="modal-footer">
			<img id="add-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
					
			<a  onclick='validateInputAndSubmit($("#change-lang-form"));' class="btn btn-info"><spring:message code="label.OK" /></a>
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>			
		</div>
		</div>
		</div>
	</div>
	
	<div class="modal" id="change-pivot-lang-dialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
		<div class="modal-header"><spring:message code="label.ChangeDefaultPivotLanguage" /></div>
		<div class="modal-body">
			<div style="padding: 10px;">
			
				<form:form id="change-pivot-lang-form" action="myAccount" method="POST">
					<input type="hidden" name="target" value="changePivotLanguage" />
				
					<label for="change-lang"><spring:message code="label.Language" /></label>
					<select name="change-lang">
						<c:forEach items="${languages}" var="language">				
							<c:if test="${language.official}">
								<c:choose>
									<c:when test="${language.code == USER.defaultPivotLanguage}">
										<option selected="selected" value="<esapi:encodeForHTMLAttribute>${language.code}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML><spring:message code="label.lang.${language.englishName}" /></esapi:encodeForHTML></option>
									</c:when>
									<c:otherwise>
										<option value="<esapi:encodeForHTMLAttribute>${language.code}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML><spring:message code="label.lang.${language.englishName}" /></esapi:encodeForHTML></option>
									</c:otherwise>
								</c:choose>
							</c:if>
						</c:forEach>
					</select>
					
				</form:form>
				
				<div class="validation-error"><esapi:encodeForHTML>${error}</esapi:encodeForHTML></div>
					
			</div>
		</div>
		<div class="modal-footer">
			<img id="add-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
					
			<a  onclick='validateInputAndSubmit($("#change-pivot-lang-form"));' class="btn btn-info"><spring:message code="label.OK" /></a>
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>			
		</div>
		</div>
		</div>
	</div>
	
	<c:if test="${message != null}">
		<script type="text/javascript">
			showInfo('${message}');
		</script>
	</c:if>
	
	<c:choose>
		<c:when test="${operation != null && operation.equals('changePassword')}">
			<script type="text/javascript">
				$("#change-password-dialog").modal("show");
				$("#change-email-dialog").modal("hide");
				$("#change-name-dialog").modal("hide");
				$("#change-lang-dialog").modal("hide");
			</script>
		</c:when>
		<c:when test="${operation != null && operation.equals('changeEmail')}">
			<script type="text/javascript">
				$("#change-email-dialog").modal("show");
				$("#change-password-dialog").modal("hide");
				$("#change-name-dialog").modal("hide");
				$("#change-lang-dialog").modal("hide");
			</script>
		</c:when>
		<c:when test="${operation != null && operation.equals('changeLanguage')}">
			<script type="text/javascript">
				$("#change-email-dialog").modal("hide");
				$("#change-password-dialog").modal("hide");
				$("#change-name-dialog").modal("hide");
				$("#change-lang-dialog").modal("show");
			</script>
		</c:when>
		<c:otherwise>
			<script type="text/javascript">
				$("#change-email-dialog").modal("hide");
				$("#change-password-dialog").modal("hide");
				$("#change-name-dialog").modal("hide");
				$("#change-lang-dialog").modal("hide");
			</script>
		</c:otherwise>
	</c:choose>

</body>
</html>
