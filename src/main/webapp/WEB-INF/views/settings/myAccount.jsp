<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>	
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.MyAccount" /></title>
	
	<%@ include file="../includes.jsp" %>
	
	<script>	
		$(function() {					
			$("#settings-menu-tab").addClass("active");
			$("#myaccount-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
			$('[data-toggle="tooltip"]').tooltip();
			
			<c:if test="${organisationSet != null}">
				showInfo('<spring:message code="info.OrganisationSet" arguments="${contextpath}/home/support?organisation" />');
			</c:if>
		});	
		
		function toggleInline(td) {
			if ($(td).find(".iconbutton:visible").first().hasClass("disabled")) return;
			
			if ($(td).find("form").first().is(":visible"))
			{
				$(td).find("form").first().hide();
				$(td).find("span").first().show();
				$(td).find(".glyphicon-remove").first().parent().hide();
				$(td).find(".glyphicon-pencil").first().parent().show();
				$(td).closest("table").find(".glyphicon-pencil").parent().removeClass("disabled");
			} else {
				$(td).find("form").first().show();
				$(td).find("span").first().hide();
				$(td).find(".glyphicon-remove").first().parent().show();
				$(td).find(".glyphicon-pencil").first().parent().hide();
				$(td).closest("table").find(".glyphicon-pencil").parent().addClass("disabled");
			}			
		}
	</script>
	
	<style type="text/css">
		.table-bordered td {
			border-right: 0px !important;
			border-left: 0px !important;
			
			padding-bottom: 15px !important;
		}	
	</style>
</head>
<body>
	<div class="page-wrap">
	<%@ include file="../header.jsp" %>
	<%@ include file="../menu.jsp" %>
	<%@ include file="settingsmenu.jsp" %>	

	<div class="fixedtitleform">
		<div class="fixedtitleinner" style="width:880px">				
		</div>
	</div>
	
	<div class="page880" style="margin-bottom: 0px; margin-top: 150px;">
				
		<div style="margin-left: auto; margin-right: auto; width: 700px; margin-bottom: 20px;">		
		
			<table class="table table-bordered table-striped" style="width: 700px; margin-bottom: 20px;">
				<tr>
					<td style="width: 200px;"><label><b><spring:message code="label.Name" /></b></label></td>
					<td style="width: 500px;"><esapi:encodeForHTML>${USER.name}</esapi:encodeForHTML></td>
				</tr>
				<tr>
					<td><label><b><spring:message code="label.Email" /></b></label></td>
					<td id="change-email-dialog">
						<span><esapi:encodeForHTML>${USER.email}</esapi:encodeForHTML></span>
						
						<c:if test='${!USER.getType().equalsIgnoreCase("ECAS")}'>
							<div style="float: right">
								<a data-toggle="tooltip" title="<spring:message code="label.Edit" />" class="iconbutton" onclick="toggleInline($(this).closest('td'))"><span class="glyphicon glyphicon-pencil"></span></a>
								<a data-toggle="tooltip" title="<spring:message code="label.Cancel" />" class="iconbutton hideme" onclick="toggleInline($(this).closest('td'))"><span class="glyphicon glyphicon-remove"></span></a>
							</div>
							<form:form style="display: none" id="change-email-form" action="myAccount" method="POST">
								<input type="hidden" name="target" value="changeEmail" />
								
								<label for="change-email-password"><spring:message code="label.Password" /></label><br />
								<input name="password" autocomplete="off" class="form-control required" type="password" id="change-email-password" style="width:220px;" /><br />
								
								<label for="change-email-new-email"><spring:message code="label.NewEmailAddress" /></label><br />
								<input name="newemail" class="form-control required comparable" type="text" maxlength="255" id="change-email-new-email" style="width:220px;" /><br />
								
								<label for="change-email-new-email2"><spring:message code="label.RepeatNewEmailAddress" /></label><br />
								<input name="newemail2" class="form-control required comparable-second" type="text" maxlength="255" id="change-email-new-email2" style="width:220px;" />
							
								<div class="validation-error"><esapi:encodeForHTML>${operation != null && operation.equals('changeEmail') ? error : ""}</esapi:encodeForHTML></div>
				
								<br />
				
								<img id="add-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
								<a onclick='validateInputAndSubmit($("#change-email-form"));' class="btn btn-primary"><spring:message code="label.Save" /></a>
							</form:form>
						</c:if>
					</td>
				</tr>
				<tr>
					<td style="width: 200px;"><label><b><spring:message code="label.Organisation" /></b></label></td>
					<td style="width: 500px;">
						<esapi:encodeForHTML>${organisation}</esapi:encodeForHTML>
						<div style="float: right">
							<a data-toggle="tooltip" title="<spring:message code="label.Edit" />" class="iconbutton" href="${contextpath}/home/support?organisation=1"><span class="glyphicon glyphicon-pencil"></span></a>
						</div>
					</td>
				</tr>
				<tr>
					<td><label><b><spring:message code="label.Password" /></b></label></td>
					<td id="change-password-dialog">
						<span original">*******</span>
						<c:if test='${!USER.getType().equalsIgnoreCase("ECAS")}'>
							<div style="float: right">
								<a data-toggle="tooltip" title="<spring:message code="label.Edit" />" class="iconbutton" onclick="toggleInline($(this).closest('td'))"><span class="glyphicon glyphicon-pencil"></span></a>
								<a data-toggle="tooltip" title="<spring:message code="label.Cancel" />" class="iconbutton hideme" onclick="toggleInline($(this).closest('td'))"><span class="glyphicon glyphicon-remove"></span></a>
							</div>
							<form:form style="display: none" id="change-password-form" action="myAccount" method="POST">
								<input type="hidden" name="target" value="changePassword" />
							
								<label for="change-password-old-password"><spring:message code="label.OldPassword" /></label><br />
								<input maxlength="255" name="oldpassword" class="form-control required" type="password" autocomplete="off" id="change-password-old-password" style="width:220px;" /><br />
								
								<label for="change-password-new-password"><spring:message code="label.NewPassword" /></label><br />
								<input maxlength="16" name="newpassword" class="form-control required comparable" type="password" autocomplete="off" id="change-password-new-password" style="width:220px;" /><br />
								
								<label for="change-password-new-password2"><spring:message code="label.RepeatNewPassword" /></label><br />
								<input maxlength="16" name="newpassword2" class="form-control required comparable-second" type="password" autocomplete="off" id="change-password-new-password2" style="width:220px;" />
							
								<div class="validation-error"><esapi:encodeForHTML>${operation != null && operation.equals('changePassword') ? error : ""}</esapi:encodeForHTML></div>							
								<br />							
								<img id="add-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
								<a onclick="validateInputAndSubmit($('#change-password-form'));" class="btn btn-primary"><spring:message code="label.Save" /></a>
							</form:form>
						</c:if>
					</td>
				</tr>
				<tr>
					<td><label><b><spring:message code="label.Language" /></b></label></td>
					<td id="change-lang-dialog">
						<span><esapi:encodeForHTML>${USER.language}</esapi:encodeForHTML></span>
						<div style="float: right">
							<a data-toggle="tooltip" title="<spring:message code="label.Edit" />" class="iconbutton" onclick="toggleInline($(this).closest('td'))"><span class="glyphicon glyphicon-pencil"></span></a>
							<a data-toggle="tooltip" title="<spring:message code="label.Cancel" />" class="iconbutton hideme" onclick="toggleInline($(this).closest('td'))"><span class="glyphicon glyphicon-remove"></span></a>
						</div>
						<form:form style="display: none" id="change-lang-form" action="myAccount" method="POST">
							<input type="hidden" name="target" value="changeLanguage" />
					
							<select class="form-control" style="width: auto;" name="change-lang">
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
							
							<div class="validation-error"><esapi:encodeForHTML>${operation != null && operation.equals('changeLanguage') ? error : ""}</esapi:encodeForHTML></div>
							
							<br />
							
							<img id="add-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />							
							<a onclick='validateInputAndSubmit($("#change-lang-form"));' class="btn btn-primary"><spring:message code="label.Save" /></a>
						</form:form>
					</td>
				</tr>
				<tr>
					<td><label><b><spring:message code="label.DefaultMainLanguage" /></b></label></td>
					<td id="change-pivotlang-dialog">
						<span><esapi:encodeForHTML>${USER.defaultPivotLanguage}</esapi:encodeForHTML></span>
						<div style="float: right">
							<a data-toggle="tooltip" title="<spring:message code="label.Edit" />" class="iconbutton" onclick="toggleInline($(this).closest('td'))"><span class="glyphicon glyphicon-pencil"></span></a>
							<a data-toggle="tooltip" title="<spring:message code="label.Cancel" />" class="iconbutton hideme" onclick="toggleInline($(this).closest('td'))"><span class="glyphicon glyphicon-remove"></span></a>
						</div>
						<form:form class="hideme" id="change-pivot-lang-form" action="myAccount" method="POST">
							<input type="hidden" name="target" value="changePivotLanguage" />
							<select class="form-control" style="width: auto;" name="change-lang">
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
							
							<div class="validation-error"><esapi:encodeForHTML>${error}</esapi:encodeForHTML></div>
							
							<br />
														
							<img id="add-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />							
							<a onclick='validateInputAndSubmit($("#change-pivot-lang-form"));' class="btn btn-primary"><spring:message code="label.Save" /></a>
						</form:form>
					</td>
				</tr>
			</table>
			<div style="text-align: center; margin-bottom: 50px">
				<a class="btn btn-default" onclick="$('#deleteAccountDialog').modal('show')"><spring:message code="label.DeleteAccount" /></a>
			</div>
		</div>

	</div>
	</div>
	
	<div class="modal" id="deleteAccountDialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
    	<div class="modal-header"><spring:message code="label.Warning" /></div>
		<div class="modal-body">
			<spring:message code="question.DeleteAccount" />
		</div>
		<div class="modal-footer">
			<img id="delete-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<form:form action="myAccount" method="POST">
				<input type="hidden" name="target" value="deleteAccount" />
				<input type="submit" class="btn btn-default" value="<spring:message code="label.DeleteAccount" />" />
				<a class="btn btn-primary" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
			</form:form>
		</div>
		</div>
		</div>
	</div>
	
	<%@ include file="../footer.jsp" %>		
	
	<c:if test="${message != null}">
		<script type="text/javascript">
			showInfo('${message}');
		</script>
	</c:if>
	
	<c:choose>
		<c:when test="${operation != null && operation.equals('changePassword')}">
			<script type="text/javascript">
				toggleInline($("#change-password-dialog"));
			</script>
		</c:when>
		<c:when test="${operation != null && operation.equals('changeEmail')}">
			<script type="text/javascript">
				toggleInline($("#change-email-dialog"));
			</script>
		</c:when>
		<c:when test="${operation != null && operation.equals('changeLanguage')}">
			<script type="text/javascript">
				toggleInline($("#change-lang-dialog"));
			</script>
		</c:when>
		<c:when test="${operation != null}">
			<script type="text/javascript">
				toggleInline($("#change-pivotlang-dialog"));
			</script>
		</c:when>
	</c:choose>

</body>
</html>
