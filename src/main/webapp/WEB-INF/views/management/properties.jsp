<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Properties" /></title>	
	<%@ include file="../includes.jsp" %>	
	<link href="${contextpath}/resources/css/fileuploader.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${contextpath}/resources/js/fileuploader.js?version=<%@include file="../version.txt" %>"></script>
	<script type='text/javascript' src='${contextpath}/resources/js/knockout-3.5.1.js?version=<%@include file="../version.txt" %>'></script>
	
	<jsp:include page="propertiesHead.jsp" />
</head>
<body data-spy="scroll" data-target="#navbar-example" data-offset="400">
<div class="page-wrap">

	<jsp:include page="../header.jsp" />
	<jsp:include page="../menu.jsp" />
	<jsp:include page="formmenu.jsp" />
	
	<div id="propertiespage" class="fullpageform" style="padding-top:190px;">		
		<form:form id="save-form" style="width: 730px; margin-left: auto; margin-right: auto;" method="POST" action="${contextpath}/${sessioninfo.shortname}/management/properties?${_csrf.parameterName}=${_csrf.token}" enctype="multipart/form-data" modelAttribute="form">
			<form:hidden path="survey.id" />
			<input type="hidden" id="survey-security" name="survey.security" value="" />
			<form:input type="hidden" path="survey.isEVote" />
			<form:input type="hidden" path="survey.eVoteTemplate" />
			<form:input type="hidden" path="survey.isECF" />
			<form:input type="hidden" path="survey.isSelfAssessment" />
			<c:if test="${!(enablechargeback == 'true')}">
				<form:input type="hidden" path="survey.organisation" />
				<form:input type="hidden" path="survey.validator" />
			</c:if>
			
			<div class="actions">
				<div style="width: 950px; margin-left: auto; margin-right: auto;">
					<div style="float: left">
						<a onclick="checkPropertiesAndSubmit(false, false);" class="btn btn-primary" style="margin-top: 2px; margin-left: 1px;"><spring:message code="label.Save" /></a>
					</div>
						
					<div style="width: auto;">			
						 <nav class="navbar navbar-default" id="navbar-example" style="width: 730px;">	
						    <ul class="nav nav-tabs scrolltabs" role="tablist">
						      <li class="active"><a href="#basic"><spring:message code="label.Basic" /></a></li>
						      <li><a href="#advanced"><spring:message code="label.Advanced" /></a></li>
						      <li><a href="#security"><spring:message code="label.Security" /></a></li>
						      <li><a href="#appearance"><spring:message code="label.Appearance" /></a></li>
						      <c:if test="${!form.survey.isOPC}">
						      	<li><a href="#publishresults"><spring:message code="label.PublishResults" /></a></li>
						      </c:if>
						      <li><a href="#specialpages"><spring:message code="label.SpecialPages" /></a></li>
						      <li><a href="#type"><spring:message code="label.Type" /></a></li>						      
						    </ul>			
						</nav>				
					</div>
				</div>
			</div>
	
			<div class="propertiesbox">
				<a class="anchor" id="basic"></a>
				<label><spring:message code="label.Basic" /></label>
				<table class="table table-bordered">
					<tr>
						<td>
							<div style="float: left">
								<span class="mandatory">*</span><spring:message code="label.Alias" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
							</div>
							<div style="float: right; max-width: 500px;">
								<form:input id="edit-survey-shortname" type="text" maxlength="255" class="form-control required freetext max255" path="survey.shortname" />
							</div>
							<div style="clear: both"></div>
							<div class="help" style="display: none; margin-top: 10px;">
								<span><spring:message code="message.MeaningfulShortnameNew" /></span>	
							</div>
						</td>
					</tr>
					<tr>
						<td>
							<div style="float: left">
								<span class="mandatory">*</span><spring:message code="label.Title" />
							</div>
							<div style="float: right">
								<div class="preview">${form.survey.title} <a class="iconbutton" onclick="$('#tinymcetitle').show();$(this).closest('.preview').hide()" style="margin-left: 10px;"><span class="glyphicon glyphicon-pencil"></span></a></div>
								<div id="tinymcetitle" style="display: none">
									<form:textarea class="tinymcealign required xhtml" id="edit-survey-title" path="survey.title"></form:textarea>
								</div>
							</div>
						</td>
					</tr>
					<c:if test="${enablechargeback == 'true'}">
						<tr>
							<td>
								<div style="float: left">
									<span class="mandatory">*</span><spring:message code="label.Organisation" />
									<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
								</div>
								
								<c:choose>
									<c:when test="${form.survey.getIsPublished()}">
										<div style="float: right; text-align: right; margin-bottom: 10px;">
											<div style="margin-bottom: 5px;">${form.survey.organisation}</div>	
											<form:input type="hidden" path="survey.organisation" />									

											<c:if test="${(form.survey.owner.isExternal() || form.survey.owner.type == 'SYSTEM') && form.survey.validator != null && form.survey.validator.length() > 0}">
												<div style="margin-top: 5px;">
													<spring:message code="label.EmailValidator" />
													<span>${form.survey.validator}</span>														
												</div>
											</c:if>
										</div>
									</c:when>
									<c:otherwise>

										<div style="float: right">
											<span style="margin-left: 10px">
												<c:choose>
													<c:when test="${form.survey.owner.isExternal() || form.survey.owner.type == 'SYSTEM'}">
														<form:select path="survey.organisation" class="form-control survey-organisation" id="survey-organisation" style="width: auto; min-width: 200px; max-width: 700px; display: inline;" onchange="checkValidator()">
															<optgroup id="survey-organisation-dgs" label="<spring:message code="label.EuropeanCommission" />: <spring:message code="label.DGsAndServices" />">
															</optgroup>
		
															<optgroup id="survey-organisation-aex" label="<spring:message code="label.EuropeanCommission" />: <spring:message code="label.ExecutiveAgencies" />">
															</optgroup>
		
															<optgroup id="survey-organisation-euis" label="<spring:message code="label.OtherEUIs" />">
															</optgroup>
		
															<optgroup id="survey-organisation-noneuis" label="<spring:message code="label.NonEUIentities" />">
															</optgroup>
														</form:select>
													</c:when>
													<c:otherwise>
														<form:select path="survey.organisation" class="form-control survey-organisation" style="width: auto; min-width: 200px; display: inline" disabled="disabled">
															<form:option selected="selected" value="${form.survey.owner.organisationCode}"></form:option>
														</form:select>
													</c:otherwise>
												</c:choose>
											</span>
										</div>
		
										<div style="float: right; margin-top: 10px; margin-bottom: 10px;">
											<c:if test="${form.survey.owner.isExternal() || form.survey.owner.type == 'SYSTEM'}">
												<div id="survey-validator-div" style="margin-left: 40px">
													<span>
														<span class="mandatory">*</span><spring:message code="label.EmailValidator" />
														<form:input path="survey.validator" type="text" class="form-control required email" id="survey-validator" style="margin-left: 10px; width: 300px; display: inline;" />
														<a onclick="openReminderDialog();" class="iconbutton" style="margin-left: 10px; vertical-align: middle" data-toggle="tooltip" title="<spring:message code="label.SendReminder" />"><span class='glyphicon glyphicon-envelope'></span></a>
														<div id="survey-validator-invalid" class="hideme validation-error-keep" style=" margin-top: 10px; "><spring:message code="message.ValidatorInvalid" /></div>
													</span>
												</div>
											</c:if>
										</div>		
										
									</c:otherwise>
								</c:choose>
								
								<div class="help" style="float: left; display: none;">
									<span><spring:message code="message.Organisation" /></span>
								</div>
							</td>
						</tr>
					</c:if>
					<tr>
						<td>
							<div style="float: left">
								<span class="mandatory owner">*</span><spring:message code="label.Owner" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
							</div>
							<div style="float: right; text-align: right" id="owner-change-section">
								${form.survey.owner.getFirstLastName()} (${form.survey.owner.email})
								<c:if test="${form.survey.owner.id.equals(USER.id)}">
									<a class="iconbutton" onclick="showChangeOwnerDialog()" style="margin-left: 10px;"><span class="glyphicon glyphicon-pencil"></span></a>
									<br>
									<c:if test="${newOwnerEmail != null}">
										<small><spring:message code="message.OwnerChangeRequestInProgress" arguments="${newOwnerEmail}~${newOwnerRequestDate}" argumentSeparator="~" /></small>
									</c:if>
								</c:if>
							</div>
							<div class="help" style="float: left; display: none;">
								<span><spring:message code="info.changeOwnership" /></span>
							</div>
						</td>
					</tr>
					<tr>
						<td>
							<div style="float: left">
								<span class="mandatory">*</span><spring:message code="label.MainLanguage" />
							</div>
							<div style="float: right">
								<form:select path="survey.language" class="form-control required" style="width: auto;">
									<c:forEach items="${form.survey.completeTranslations}" var="language">
										<c:if test="${language.official}">
											<c:choose>
												<c:when test="${form.survey.language.code.equals(language.code)}">
													<form:option selected="selected" value="${language.code}"><esapi:encodeForHTML>${language.code}</esapi:encodeForHTML></form:option>
												</c:when>
												<c:otherwise>
													<form:option value="${language.code}"><esapi:encodeForHTML>${language.code}</esapi:encodeForHTML></form:option>
												</c:otherwise>
											</c:choose>
										</c:if>
									</c:forEach>
								</form:select>		
							</div>
						</td>
					</tr>
					<tr>
						<td>
							<div style="float: left">
								<span class="mandatory">*</span><spring:message code="label.Contact" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
							</div>
							<div style="float: right">
								<div style="float:left; text-align: right;">
									<select class="form-control" data-bind="value: contactType" id="survey-contact-type" style="width: auto;">
										<option value="form"><spring:message code="label.ContactForm" /></option>
										<option value="email"><spring:message code="label.Email" /></option>
										<option value="url"><spring:message code="label.Webpage" /></option>								
									</select><br />
									<div id="survey-contact-label-label" data-bind="visible: contactType() == 'url'" style="font-weight: bold; margin-top: 5px;"><spring:message code="label.Label" /></div>
								</div>
								<div style="float:left; margin-left: 10px">
									<form:input htmlEscape="false" path="survey.contact" class="form-control required email" type="text" maxlength="255" style="width: 300px;" /><br />
									<form:input data-bind="visible: contactType() == 'url'" htmlEscape="false" path="survey.contactLabel" type="text" class="form-control" style="width: 300px" maxlength="255"  />
								</div>								
							</div>
							<div style="clear: both"></div>
							<div class="help" style="display: none;">
								<span><spring:message code="message.Contact" /></span>	
							</div>
						</td>
					</tr>
					<c:if test="${USER.getSystemManagementPrivilege() == 2}">
						<tr>
							<td>
								<div style="float: left">
									<spring:message code="label.DoNotDelete" />
									<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
									<div class="help hideme"><spring:message code="info.DoNotDelete" /></div>
								</div>
								<div style="float: right">
									<div class="onoffswitch">
										<form:checkbox path="survey.doNotDelete" data-bind="checked: doNotDelete" class="onoffswitch-checkbox" id="myonoffswitchdoNotDelete" />
										<label class="onoffswitch-label" for="myonoffswitchdoNotDelete">
											<span class="onoffswitch-inner"></span>
											<span class="onoffswitch-switch"></span>
										</label>
									</div>
								</div>
							</td>
						</tr>

					</c:if>
				</table>
			</div>
			
			<div class="propertiesbox">
				<a class="anchor" id="advanced"></a>
				<label><spring:message code="label.Advanced" /></label>
				<table class="table table-bordered">
					<tr>
						<td>
							<div style="float: left">
								<spring:message code="label.AutomaticSurveyPublishing" />
							</div>
							<div style="float: right">
								<c:choose>
									<c:when test="${form.survey.isOPC}">
										<div class="onoffswitch">
											<input data-bind="checked: automaticPublishing; enable: false" type="checkbox" name="survey.automaticPublishing" class="onoffswitch-checkbox" id="myonoffswitch" />
											 <label class="onoffswitch-label" for="myonoffswitch">
										        <span class="onoffswitch-inner"></span>
										        <span class="onoffswitch-switch"></span>
										    </label>
										</div>
										<form:hidden path="survey.automaticPublishing" />
									</c:when>
									<c:otherwise>
										<div class="onoffswitch">
											<input data-bind="checked: automaticPublishing" type="checkbox" name="survey.automaticPublishing" class="onoffswitch-checkbox" id="myonoffswitch" />
											 <label class="onoffswitch-label" for="myonoffswitch">
										        <span class="onoffswitch-inner"></span>
										        <span class="onoffswitch-switch"></span>
										    </label>
										</div>
									</c:otherwise>
								</c:choose>	
							</div>
						</td>
					</tr>
					<tr class="subelement" data-bind="visible: automaticPublishing">
						<td>
							<div style="float: left">
								<span class="hideme mandatory autopub">*</span><spring:message code="label.StartDate" />
							</div>
							<div style="float: right">
								<div class="input-group">
									<div class="input-group-addon" onclick="$(this).parent().find('.datepicker').datepicker('show');">
										<span class="glyphicon glyphicon-calendar" aria-hidden="true"></span>										
									</div>			
									<form:input path="survey.start" maxlength="10" style="padding-left: 5px; width: 100px; height: 30px; margin-right: 10px;" class="hourselector datepicker date"/>	
								
									<form:select path="startHour" style="width: auto; height: 30px;">
										<form:option value="0">0:00</form:option>
										<form:option value="1">1:00</form:option>
										<form:option value="2">2:00</form:option>
										<form:option value="3">3:00</form:option>
										<form:option value="4">4:00</form:option>
										<form:option value="5">5:00</form:option>
										<form:option value="6">6:00</form:option>
										<form:option value="7">7:00</form:option>
										<form:option value="8">8:00</form:option>
										<form:option value="9">9:00</form:option>
										<form:option value="10">10:00</form:option>
										<form:option value="11">11:00</form:option>
										<form:option value="12">12:00</form:option>
										<form:option value="13">13:00</form:option>
										<form:option value="14">14:00</form:option>
										<form:option value="15">15:00</form:option>
										<form:option value="16">16:00</form:option>
										<form:option value="17">17:00</form:option>
										<form:option value="18">18:00</form:option>
										<form:option value="19">19:00</form:option>
										<form:option value="20">20:00</form:option>
										<form:option value="21">21:00</form:option>
										<form:option value="22">22:00</form:option>
										<form:option value="23">23:00</form:option>
									</form:select>									
								</div>
							</div>					
						</td>
					</tr>
					<tr class="subelement" data-bind="visible: automaticPublishing">
						<td>
							<div style="float: left">
								<span class="hideme mandatory autopub">*</span><spring:message code="label.ExpiryDate" />
							</div>
							<div style="float: right">
								<div class="input-group">
									<div class="input-group-addon" onclick="$(this).parent().find('.datepicker').datepicker('show');">
										<span class="glyphicon glyphicon-calendar" aria-hidden="true"></span>										
									</div>						
									<form:input path="survey.end" maxlength="10" style="padding-left: 5px; width: 100px; height: 30px; margin-right: 10px;" class="datepicker hourselector date"/>	
									<form:select path="endHour" style="width: auto; height: 30px;">
										<form:option value="0">0:00</form:option>
										<form:option value="1">1:00</form:option>
										<form:option value="2">2:00</form:option>
										<form:option value="3">3:00</form:option>
										<form:option value="4">4:00</form:option>
										<form:option value="5">5:00</form:option>
										<form:option value="6">6:00</form:option>
										<form:option value="7">7:00</form:option>
										<form:option value="8">8:00</form:option>
										<form:option value="9">9:00</form:option>
										<form:option value="10">10:00</form:option>
										<form:option value="11">11:00</form:option>
										<form:option value="12">12:00</form:option>
										<form:option value="13">13:00</form:option>
										<form:option value="14">14:00</form:option>
										<form:option value="15">15:00</form:option>
										<form:option value="16">16:00</form:option>
										<form:option value="17">17:00</form:option>
										<form:option value="18">18:00</form:option>
										<form:option value="19">19:00</form:option>
										<form:option value="20">20:00</form:option>
										<form:option value="21">21:00</form:option>
										<form:option value="22">22:00</form:option>
										<form:option value="23">23:00</form:option>
									</form:select>
								</div>
							</div>
						</td>
					</tr>
					<tr data-bind="visible: automaticPublishing, attr:{class: endNotifications() ? 'subelement nobottomborder' : 'subelement'}">
						<td>
							<div style="float: left">
								<spring:message code="label.Reminder" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>								
							</div>
							<div style="float: right; text-align: right">															
								<!-- <div class="onoffswitch">
									<input type="checkbox" data-bind="checked: endNotifications" name="notification" class="onoffswitch-checkbox" id="myonoffswitchnotification">
									 <label class="onoffswitch-label" for="myonoffswitchnotification">
								        <span class="onoffswitch-inner"></span>
								        <span class="onoffswitch-switch"></span>
								    </label>
								</div> -->
								
								<div class="form-inline" style="margin-bottom: 5px;">
									 <div class="form-group">
										<form:select class="form-control" path="survey.notificationValue" style="width: auto;">
											<form:option value="-1"><spring:message code="label.none" /></form:option>
											<c:forEach var="i" begin="1" end="30">
												<form:option value="${i}"><c:out value="${i}"/></form:option>
											</c:forEach>
										</form:select>	
									</div>
									 <div class="form-group">
										<form:select class="form-control" path="survey.notificationUnit" style="width: auto;">
											<form:option value="-1"><spring:message code="label.none" /></form:option>
											<form:option value="0"><spring:message code="label.hours" /></form:option>
											<form:option value="1"><spring:message code="label.days" /></form:option>
											<form:option value="2"><spring:message code="label.weeks" /></form:option>
											<form:option value="3"><spring:message code="label.months" /></form:option>
										</form:select>
									</div>
									<spring:message code="label.before" />
								</div>	
								<!--<form:radiobutton class="check" path="survey.notifyAll" value="true"/><spring:message code="label.AllFormManagers" />&#160;
								<form:radiobutton class="check" path="survey.notifyAll" value="false"/><spring:message code="label.FormCreatorOnly" />-->
							</div>
							<div style="clear: both"></div>
							<div class="help hideme"><spring:message code="info.Reminder" /></div>
						</td>
					</tr>
					<tr>
						<td>
							<div style="float: left">
								<spring:message code="label.AutomaticConfirmationEmail" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
								<div class="help hideme"><spring:message code="info.AutomaticConfirmationEmail" /></div>
							</div>
							<div style="float: right">
								<div class="onoffswitch">
									<form:checkbox path="survey.sendConfirmationEmail" data-bind="checked: sendConfirmationEmail" class="onoffswitch-checkbox" id="myonoffswitchSendConfirmationEmail" />
									 <label class="onoffswitch-label" for="myonoffswitchSendConfirmationEmail">
								        <span class="onoffswitch-inner"></span>
								        <span class="onoffswitch-switch"></span>
								    </label>
								</div>
							</div>
						</td>
					</tr>
					<tr>
						<td>
							<div style="float: left">
								<spring:message code="label.AutomaticReportEmail" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
							</div>
							<div style="float: right">
								<div class="onoffswitch">
									<form:checkbox path="survey.sendReportEmail" data-bind="checked: sendReportEmail" class="onoffswitch-checkbox" id="myonoffswitchSendReportEmail" />
									<label class="onoffswitch-label" for="myonoffswitchSendReportEmail">
										<span class="onoffswitch-inner"></span>
										<span class="onoffswitch-switch"></span>
									</label>
								</div>
							</div>
							<div style="clear: both"></div>
							<div class="help hideme"><spring:message code="info.AutomaticReportEmail" /></div>
						</td>
					</tr>
					<tr class="subelement" data-bind="visible: sendReportEmail">
						<td>
							<div style="float: left">
								<spring:message code="label.FrequencyReportEmails" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
							</div>
							<div style="float: right">
								<form:radiobutton class="check" path="survey.reportEmailFrequency" value="Never" /><spring:message code="label.Never" />&#160;
								<form:radiobutton class="check" path="survey.reportEmailFrequency" value="Daily" /><spring:message code="label.Daily" />&#160;
								<form:radiobutton class="check" path="survey.reportEmailFrequency" value="Weekly" /><spring:message code="label.Weekly" />&#160;
								<form:radiobutton class="check" path="survey.reportEmailFrequency" value="Monthly" /><spring:message code="label.Monthly" />
							</div>
							<div style="clear: both"></div>
							<div class="help hideme"><spring:message code="info.FrequencyReportEmails" /></div>
						</td>
					</tr>
					<tr class="subelement" data-bind="visible: sendReportEmail">
						<td>
							<div>
								<spring:message code="label.RecipientList" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
								<div class="help hideme"><spring:message code="info.RecipientList" /></div>
							</div>
							<div style="padding-top: 10px;">
								<input type="text" id="reportRecipientList" maxlength="255" class="form-control freetext max255" style="display: inline" />
								<button type="button" class="btn btn-default1" style="display: inline" onclick="_properties.addReportMails()"><spring:message code="label.Add" /></button>
							</div>
							<div>
								<span id="report-duplicate-mails" class="validation-error-server hideme">
									<spring:message code="validation.ReportDuplicateMailAddress" />
								</span>
								<span id="report-invalid-mails" class="validation-error-server hideme">
									<spring:message code="validation.ReportInvalidMailAddress" />
								</span>
								<span id="report-too-many-mails" class="validation-error-server hideme">
									<spring:message code="validation.ReportTooManyMailAddress" />
								</span>
								<span id="report-no-mails" class="validation-error-server hideme">
                                    <spring:message code="validation.ReportNoMailAddress" />
                                </span>
							</div>
							<div style="float: left; padding-left: 5px; padding-top: 5px;">
								<!-- ko if: reportEmails() != '' -->
									<!--  ko foreach: reportEmails().split(';') -->
										<div>
											<span data-bind="text: $data"></span>
											<a style="font-size: inherit !important" class="iconbutton" data-bind="attr: {onclick: '_properties.deleteReportMail(`' + $data + '`)'}" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Delete" />"><span class="glyphicon glyphicon-remove"></span></a>
										</div>
									<!-- /ko -->
								<!-- /ko -->
							</div>
							<form:input type="hidden" path="survey.reportEmails" data-bind="value: reportEmails()" />
						</td>
					</tr>
					<tr>
						<td>
							<div style="float: left; max-width: 500px;">
								<spring:message code="label.UseMaxNumberContribution" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
								<div class="help hideme"><spring:message code="info.MaxNumberContributions" /></div>
							</div>
							<div style="float: right; text-align: right">										
								<div class="onoffswitch">
									<form:checkbox path="survey.isUseMaxNumberContribution" data-bind="checked: isUseMaxNumberContribution, enable: !_properties.eVote()" class="onoffswitch-checkbox" id="myonoffswitchlimitmaxcont" />
									 <label data-bind='class: "onoffswitch-label"+ (_properties.eVote() ? " disabled" : "")' for="myonoffswitchlimitmaxcont">
								        <span class="onoffswitch-inner"></span>
								        <span class="onoffswitch-switch"></span>
								    </label>
								</div>
								<br>							
							</div>
						</td>
					</tr>
					<tr class="subelement" data-bind="visible: isUseMaxNumberContribution">
						<td>
							<div style="float: left; max-width: 500px;">						
								<spring:message code="label.MaxNumberContributions" />
							</div>
							<div style="float: right; text-align: right">
								<div>
									<input id='maxContributionInput' class="form-control number max1000000000" type='number' name='survey.maxNumberContribution' min='0' max='1000000000' value="<esapi:encodeForHTMLAttribute>${form.survey.maxNumberContribution}</esapi:encodeForHTMLAttribute>">
								</div>
							</div>
						</td>
					</tr>
					<tr class="subelement" data-bind="visible: isUseMaxNumberContribution">
						<td>
							<div style="float: left; max-width: 500px;">
								<spring:message code="label.MaxNumberContributionText" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
								<div class="help hideme"><spring:message code="info.MaxNumberContributionText" /></div>
							</div>
							<div style="float: right; text-align: right">
								<form:radiobutton data-bind="click: function() {isUseMaxNumberContributionLink(false); return true;}" class="check" path="survey.isUseMaxNumberContributionLink" value="false"/><spring:message code="label.Text" />&nbsp;		
								<form:radiobutton data-bind="click: function() {isUseMaxNumberContributionLink(true); return true;}" class="check" path="survey.isUseMaxNumberContributionLink" value="true"/><spring:message code="label.Link" />
				
								<div data-bind="hidden: isUseMaxNumberContributionLink">
									<div class="preview">${form.survey.maxNumberContributionText} <a class="iconbutton" onclick="$('#tinymcelimit').show();$(this).closest('.preview').hide()" style="margin-left: 10px;"><span class="glyphicon glyphicon-pencil"></span></a></div>
									<div id="tinymcelimit" style="display: none">
										<form:textarea maxlength="255" class="tinymcealign xhtml max255" id="edit-survey-max-result-page" path="survey.maxNumberContributionText"></form:textarea>
									</div>
								</div>	
								
								<div data-bind="visible: isUseMaxNumberContributionLink" id="useMaxContributionLink">
									<form:input htmlEscape="false" path="survey.maxNumberContributionLink" class="form-control" style="display: inline-block"/>
								</div>
							</div>
						</td>
					</tr>		
					<tr>
						<td>
							<div style="float: left; max-width: 500px;">
								<spring:message code="label.CreateContacts" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
								<div class="help hideme"><spring:message code="info.ContactsCreated" /></div>
							</div>	
							<div style="float: right; text-align: right">
								<div class="onoffswitch">
									<form:checkbox path="survey.registrationForm" class="onoffswitch-checkbox" id="myonoffswitchnregform" />
									 <label class="onoffswitch-label" for="myonoffswitchnregform">
								        <span class="onoffswitch-inner"></span>
								        <span class="onoffswitch-switch"></span>
								    </label>
								</div>
							</div>
						</td>
					</tr>
					<tr>
						<td>
							<div style="float: left">
								<spring:message code="label.UsefulLinks" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
								<div class="help hideme"><spring:message code="info.UsefulLinks" /></div>				
							</div>
						
							<div style="float: right">
														
								<table data-bind="visible: showUsefulLinks" class="table table-bordered" id="usefullinkstable" style="width: 500px">
									<tr>
										<td><spring:message code="label.Label" /></td>
										<td><spring:message code="label.URL" /></td>
										<td style="width: 40px;">
											<a data-toggle="tooltip" title="<spring:message code="label.AddUsefulLink" />" class="btn btn-default btn-xs" data-bind="click: addLinksRow"><span class="glyphicon glyphicon-plus"></span></a>
										</td>
									</tr>
									<c:forEach var="link" items="${form.survey.getAdvancedUsefulLinks()}" varStatus="rowCounter">
										<tr class="usefullink">
											<td>
												<input class="form-control xhtml freetext max250" style="width: 180px" type="text" maxlength="250" name="linklabel${rowCounter.index}" value="<esapi:encodeForHTMLAttribute>${link.key}</esapi:encodeForHTMLAttribute>" />
											</td>
											<td>
												<input type="text" class="form-control targeturl" style="width: 180px" maxlength="255" name="linkurl${rowCounter.index}" value="<esapi:encodeForHTMLAttribute>${link.value}</esapi:encodeForHTMLAttribute>" />
											</td>
											<td style="vertical-align: middle">
												<a data-toggle="tooltip" title="<spring:message code="label.RemoveUsefulLink" />" class="btn btn-default btn-xs"  onclick="_properties.removeLinksRow(this)"><span class="glyphicon glyphicon-remove"></span></a>
											</td>
										</tr>
									</c:forEach>										
								</table>
								
								<a data-bind="visible: !showUsefulLinks(), click: addLinksRow" data-toggle="tooltip" title="<spring:message code="label.AddUsefulLink" />" class="btn btn-default btn-xs"><span class="glyphicon glyphicon-plus"></span></a>
							</div>
						</td>
					</tr>
					<tr>
						<td>
							<div style="float: left">
								<spring:message code="label.BackgroundDocuments" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
								<div class="help hideme"><spring:message code="info.BackgroundDocuments" /></div>
							</div>
							<div style="float: right">
								<table data-bind="visible: showBackgroundDocs" class="table table-bordered" id="backgrounddocumentstable" style="width: 500px">
									<tr>
										<td><spring:message code="label.Label" /></td>
										<td><spring:message code="label.Document" /></td>
										<td style="width: 40px">
											<a data-toggle="tooltip" title="<spring:message code="label.AddBackgroundDocument" />" class="btn btn-default btn-xs"  data-bind="click: addDocRow"><span class="glyphicon glyphicon-plus"></span></a>
										</td>
									</tr>
									<c:forEach var="link" items="${form.survey.getBackgroundDocumentsAlphabetical()}" varStatus="rowCounter">
										<tr>
											<td>
												<input class="xhtml freetext max250" type="text" maxlength="250" name="doclabel${rowCounter.index}" value="<esapi:encodeForHTMLAttribute>${link.key}</esapi:encodeForHTMLAttribute>" />
											</td>
											<td>
												<div style="word-wrap: break-word; max-width: 200px;">
													<a href="<esapi:encodeForHTMLAttribute>${link.value}</esapi:encodeForHTMLAttribute>">${form.survey.getFileNameForBackgroundDocument(link.key)}</a>
												</div>
												<input type="hidden" name="docurl${rowCounter.index}" value="<esapi:encodeForHTMLAttribute>${link.value}</esapi:encodeForHTMLAttribute>" />
											</td>
											<td style="vertical-align: middle">
												<a data-toggle="tooltip" title="<spring:message code="label.RemoveBackgroundDocument" />" class="btn btn-default btn-xs" onclick="_properties.removeDocRow(this);"><span class="glyphicon glyphicon-remove"></span></a>
											</td>
										</tr>
									</c:forEach>										
								</table>
								
								<a data-bind="visible: !showBackgroundDocs(), click: addDocRow" data-toggle="tooltip" title="<spring:message code="label.AddBackgroundDocument" />" class="btn btn-default btn-xs"><span class="glyphicon glyphicon-plus"></span></a>
							</div>
						</td>
					</tr>
					<tr>
						<td>
							<div style="float: left">
								<spring:message code="label.DedicatedResultPrivileges" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
								<div class="help hideme"><spring:message code="info.DedicatedResultPrivileges" /></div>
							</div>
							<div style="float: right">
								<div class="onoffswitch">
									<form:checkbox path="survey.dedicatedResultPrivileges" class="onoffswitch-checkbox" id="myonoffswitchdedicatedResultPrivileges" data-bind="enable: !_properties.eVote()"/>
									<label data-bind='class: "onoffswitch-label"+ (_properties.eVote() ? " disabled" : "")' for="myonoffswitchdedicatedResultPrivileges">
								        <span class="onoffswitch-inner"></span>
								        <span class="onoffswitch-switch"></span>
								    </label>
							    </div>
							</div>
						</td>
					</tr>
					<tr>
						<td>
							<div style="float: left">
								<spring:message code="label.Webhook" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
								<div class="help hideme"><spring:message code="info.Webhook" /></div>
							</div>
							<div style="float: right">
								<form:input id="edit-survey-webhook" type="text" maxlength="255" class="form-control freetext max255" path="survey.webhook" />
							</div>
						</td>
					</tr>
					<tr>
                        <td>
                            <div style="float: left">
                                <spring:message code="label.Tags" />
                                <a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
                                <div class="help hideme"><spring:message code="info.Tags" /></div>
                            </div>
                            <div style="float: right;">
                                <div id="selectedtags">
                                    <!--  ko foreach: sortedTags() -->
                                        <span class='badge' data-bind="attr: {tag: $data}">
                                            <span data-bind="text: $data"></span>&nbsp;<span onclick='_properties.removeTag(this)'>&#10006;</span>
                                        </span>
                                    <!-- /ko -->
                                </div>
                                <div style="float: right">
                                	<img data-bind="style: { display: tagsLoading() ? 'inline' : 'none' }" alt="wait animation" src="${contextpath}/resources/images/ajax-loader.gif" />
                                	<input id="tags" autocomplete="off" type="text" maxlength="16" onkeyup="checkTagKeyUp(event)" class="form-control freetext max255" style="float: right; margin-left: 5px;" />
                                    <input type="hidden" name="tags" data-bind="value: tags" />
                                </div>

                                <div style="clear: both; margin-bottom: 10px;"></div>
                            </div>
                        </td>
                    </tr>
				</table>
			</div>		
			
			<div class="propertiesbox">
				<a class="anchor" id="security"></a>
				<label><spring:message code="label.Security" /></label>
				<table class="table table-bordered">
					<tr>
						<td>
							<div style="float: left">
								<spring:message code="label.SecureYourSurvey" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
							</div>
							<div style="float: right">
								<div class="onoffswitch">
									<c:choose>
										<c:when test='${form.survey.isOPC}'>
											<input data-bind="checked: secured" type="checkbox" disabled name="radio-new-survey-security" class="onoffswitch-checkbox" id="myonoffswitchsecured">
											<label class="onoffswitch-label disabled" for="myonoffswitchsecured">
												<span class="onoffswitch-inner"></span>
								    			<span class="onoffswitch-switch"></span>
								    		</label>
										</c:when>
										<c:otherwise>
											<input data-bind="checked: secured, enable: !_properties.eVote()" type="checkbox" name="radio-new-survey-security" class="onoffswitch-checkbox" id="myonoffswitchsecured">
											<label data-bind='class: "onoffswitch-label"+ (_properties.eVote() ? " disabled" : "")' for="myonoffswitchsecured">
												<span class="onoffswitch-inner"></span>
								    			<span class="onoffswitch-switch"></span>
								    		</label>
										</c:otherwise>
									</c:choose>
								</div>
							</div>
							<div style="clear: both"></div>
							<div class="help hideme"><spring:message code="info.SecureYourSurveyNew" /></div>
						</td>
					</tr>
					<tr class="subelement" data-bind="visible: secured">
						<td>
							<div style="float: left">
								<spring:message code="label.SecureWithPassword" />
							</div>
							<div style="float: right">
								<div id="edit-password">
									<c:choose>
										<c:when test='${form.survey.isOPC}'>
											<input class="form-control" type="text" maxlength="255" disabled="disabled" autocomplete="off" style="margin: 0px; width: 150px;" />
											<input class="check" type="checkbox" disabled="disabled" /><spring:message code="label.ShowPassword" />
										</c:when>
										<c:when test="${form.survey.password != null && form.survey.password.length() > 0}">
											<form:password class="form-control" data-bind="enable: !_properties.eVote()" maxlength="255" autocomplete="off" value="${form.survey.password}" path="survey.password" style="margin: 0px;" onchange="$('#clearpassword').val($(this).val())" />
											<input class="form-control" style="display: none; width: auto" type="text" maxlength="255" id="clearpassword" readonly="readonly" disabled="disabled" value="${form.survey.password}" />
											<input class="check" type="checkbox" onclick="checkShowPassword(this)" /><spring:message code="label.ShowPassword" />
										</c:when>
										<c:otherwise>
											<form:password class="form-control" data-bind="enable: !_properties.eVote()" maxlength="255" autocomplete="off" path="survey.password" style="margin: 0px;" onchange="$('#clearpassword').val($(this).val())" />
											<input class="form-control" style="display: none; width: auto" type="text" maxlength="255" id="clearpassword" readonly="readonly" disabled="disabled" />
											<input class="check" type="checkbox" onclick="checkShowPassword(this)" /><spring:message code="label.ShowPassword" />
										</c:otherwise>
									</c:choose>												
								</div>
							</td>
						</tr>						
						<tr class="subelement" data-bind="visible: secured, attr:{class: ecasSecurity() ? 'nobottomborder subelement' : 'subelement'}">
							<td>
								<div style="float: left">
									<spring:message code="label.SecureWithEULogin" />
								</div>
								<div style="float: right">
									<div class="onoffswitch">
										<c:choose>
											<c:when test='${form.survey.isOPC}'>	
												<input disabled="disabled" checked="checked" type="checkbox" name="survey.ecasSecurity" class="onoffswitch-checkbox" id="myonoffswitchecas" />
												<label class="onoffswitch-label disabled" for="myonoffswitchecas">
											        <span class="onoffswitch-inner"></span>
											        <span class="onoffswitch-switch"></span>
											    </label>
											</c:when>
											<c:otherwise>
												<input data-bind="checked: ecasSecurity, enable: !_properties.eVote()" type="checkbox" name="survey.ecasSecurity" class="onoffswitch-checkbox" id="myonoffswitchecas" />
												<label for="myonoffswitchecas" data-bind='class: "onoffswitch-label"+ (_properties.eVote() ? " disabled" : "")'>
											        <span class="onoffswitch-inner"></span>
											        <span class="onoffswitch-switch"></span>
											    </label>
											</c:otherwise>
										</c:choose>
										 
									</div>
								</div>
							</td>
						</tr>
						<tr class="subsubelement noborder" data-bind="visible: secured() && ecasSecurity()">
							<td>
								<div style="float: left">
									<spring:message code="label.Users" />
								</div>
								<div style="float: right">
									<c:choose>
										<c:when test='${form.survey.isOPC}'>
											<input type="radio" disabled="disabled" checked="checked" name="ecas-mode" class="check" /><spring:message code="label.everybody" /><br />
											<input type="radio" disabled="disabled" name="ecas-mode" class="check" /><spring:message code="label.EuropeanInstitutionsStaff" />
											<form:hidden path="survey.ecasMode" name="ecas-mode" />
										</c:when>
										<c:otherwise>
											<form:radiobutton path="survey.ecasMode" data-bind="enable: !_properties.eVote()" id="ecas-mode-all" name="ecas-mode" value="all" class="check" /><spring:message code="label.everybody" /><br />
											<form:radiobutton path="survey.ecasMode"  data-bind="enable: !_properties.eVote()" id="ecas-mode-internal" name="ecas-mode" value="internal" class="check" /><spring:message code="label.EuropeanInstitutionsStaff" /><br />
										</c:otherwise>
									</c:choose>													
								</div>
							</td>
						</tr>
						<c:if test='${!form.survey.isOPC}'>
							<tr class="subsubelement noborder" data-bind="visible: secured() && ecasSecurity()">
								<td>
									<div style="float: left">
										<spring:message code="label.ContributionsPerUser" />
										<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
										<div class="help hideme"><spring:message code="info.ContributionsPerUser" /></div>
									</div>
									<div style="float: right">
										<form:input htmlEscape="false" data-bind="enable: !_properties.eVote()" path="survey.allowedContributionsPerUser" type="text" class="form-control spinner required number min1 integer" maxlength="10" style="width: 50px" />
									</div>
									<div style="clear: both"></div>																						
								</td>
							</tr>
						</c:if>							
					<tr>
						<td>
							<div style="float: left">
								<spring:message code="label.AnonymousSurveyMode" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
							</div>						
							<div style="float: right">								
							
								<c:choose>
									<c:when test='${form.survey.isOPC}'>
										<div class="onoffswitch">
											<input type="checkbox" disabled="disabled" class="onoffswitch-checkbox" id="myonoffswitchprivacy" />									
											
											<label class="onoffswitch-label disabled" for="myonoffswitchprivacy">
										        <span class="onoffswitch-inner"></span>
										        <span class="onoffswitch-switch"></span>
										    </label>
									    </div>
									</c:when>
									<c:when test='${haspublishedanswers != null && (form.survey.security.equals("openanonymous") || form.survey.security.equals("securedanonymous"))}'>
										<div class="onoffswitch">
											<input type="checkbox" disabled="disabled" class="onoffswitch-checkbox" checked="checked" id="myonoffswitchprivacy" />									
										
											<label class="onoffswitch-label disabled" for="myonoffswitchprivacy">
										        <span class="onoffswitch-inner"></span>
										        <span class="onoffswitch-switch"></span>
										    </label>
									    </div>
									</c:when>
									<c:otherwise>
										<div class="onoffswitch">
											<c:choose>
												<c:when test='${form.survey.security.equals("openanonymous") || form.survey.security.equals("securedanonymous")}'>
													<input type="checkbox" checked="checked" name="radio-new-survey-privacy" class="onoffswitch-checkbox" id="myonoffswitchprivacy" data-bind="enable: !_properties.eVote()">
												</c:when>
												<c:otherwise>
													<input type="checkbox" name="radio-new-survey-privacy" class="onoffswitch-checkbox" id="myonoffswitchprivacy" data-bind="enable: !_properties.eVote()">
												</c:otherwise>
											</c:choose>		
											 <label data-bind='class: "onoffswitch-label"+ (_properties.eVote() ? " disabled" : "")' for="myonoffswitchprivacy">
										        <span class="onoffswitch-inner"></span>
										        <span class="onoffswitch-switch"></span>
										    </label>
										</div>		
										</c:otherwise>
								</c:choose>											
															
							</div>
							<div style="clear: both"></div>
							<div class="help hideme"><spring:message code="info.AnonymousSurveyModeNew" /></div>
						</td>
					</tr>
					<tr>
						<td>
							<div style="float: left">
								<spring:message code="label.Captcha" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
								<div class="help hideme"><spring:message code="info.CaptchaNew" /></div>
							</div>						
							<div style="float: right">		
								<div class="onoffswitch">
									<c:choose>
										<c:when test='${form.survey.isOPC}'>
											<input type="checkbox" disabled="disabled" class="onoffswitch-checkbox" id="myonoffswitchcaptcha" />									
											<form:hidden path="survey.captcha"/>
											<label class="onoffswitch-label disabled" for="myonoffswitchcaptcha">
										        <span class="onoffswitch-inner"></span>
										        <span class="onoffswitch-switch"></span>
										    </label>
										</c:when>
										<c:otherwise>
											<form:checkbox path="survey.captcha" class="onoffswitch-checkbox" id="myonoffswitchcaptcha" />
											<label class="onoffswitch-label" for="myonoffswitchcaptcha">
										        <span class="onoffswitch-inner"></span>
										        <span class="onoffswitch-switch"></span>
										    </label>
										</c:otherwise>
									</c:choose>	
									 
								</div>
							</div>
						</td>
					</tr>
					<tr>
						<td>
							<div style="float: left">
								<spring:message code="label.AllowQuestionnaireDownload" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class='glyphicon glyphicon-info-sign'></span></a>
								<div class="help hideme"><spring:message code="info.AllowQuestionnaireDownload" /></div>
							</div>
							<div style="float: right">
								<div class="onoffswitch">
									<c:choose>
										<c:when test='${form.survey.isQuiz || form.survey.isEVote || form.survey.isSelfAssessment || form.survey.isECF}'>
											<input type="checkbox" disabled="disabled" class="onoffswitch-checkbox" id="myonoffswitchcaptcha" />
											<form:hidden path="survey.allowQuestionnaireDownload"/>
											<label class="onoffswitch-label disabled" for="myonoffswitchquestionnairedwnld">
												<span class="onoffswitch-inner"></span>
												<span class="onoffswitch-switch"></span>
											</label>
										</c:when>
										<c:otherwise>
											<form:checkbox path="survey.allowQuestionnaireDownload" class="onoffswitch-checkbox" id="myonoffswitchquestionnairedwnld" />
											<label class="onoffswitch-label" for="myonoffswitchquestionnairedwnld">
												<span class="onoffswitch-inner"></span>
												<span class="onoffswitch-switch"></span>
											</label>
										</c:otherwise>
									</c:choose>
								</div>
							</div>
						</td>
					</tr>
					<tr>
						<td>
							<div style="float: left">
								<spring:message code="label.AllowSaveAsDraft" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
								<div class="help hideme">
									<!-- ko if: _properties.delphi() -->
									<spring:message code="info.AllowSaveAsDraftDelphi" />
									<!-- /ko -->
									<!-- ko ifnot: _properties.delphi() -->
									<spring:message code="info.AllowSaveAsDraft" />
									<!-- /ko -->
								</div>
							</div>						
							<div style="float: right">							
								<div class="onoffswitch">
									<form:checkbox path="survey.saveAsDraft" class="onoffswitch-checkbox" id="myonoffswitchdraft" data-bind="checked: _properties.saveAsDraft()"  />
									 <label class="onoffswitch-label" data-bind='class: "onoffswitch-label"+ ((_properties.delphi() || _properties.eVote() || _properties.timeLimit().length > 0 || _properties.preventGoingBack()) ? " disabled" : "")' onclick="_properties.toggleSaveAsDraft()">
								        <span class="onoffswitch-inner"></span>
								        <span class="onoffswitch-switch"></span>
								    </label>
								</div>
							</div>		
						</th>
					</tr>
					
					<tr>
						<td>
							<div style="float: left">
								<spring:message code="label.AllowChangeContributionNew" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
								<div class="help hideme">
									<!-- ko if: _properties.delphi() -->
									<spring:message code="info.AllowChangeContributionDelphi" />
									<!-- /ko -->
									<!-- ko ifnot: _properties.delphi() -->
									<spring:message code="info.AllowChangeContribution" />
									<!-- /ko -->
								</div>
							</div>						
							<div style="float: right">							
								<div class="onoffswitch">
									<form:checkbox path="survey.changeContribution" class="onoffswitch-checkbox" data-bind="checked: _properties.changeContribution()" />
									<label class="onoffswitch-label" data-bind='class: "onoffswitch-label"+((_properties.delphi() || _properties.eVote() || _properties.timeLimit().length > 0 || _properties.preventGoingBack()) ? " disabled" : "")' onclick="_properties.toggleChangeContribution()">
								        <span class="onoffswitch-inner"></span>
								        <span class="onoffswitch-switch"></span>
								    </label>
								</div>
							</div>		
						</td>
					</tr>
					<tr>
						<td>
							<div style="float: left">
								<spring:message code="label.AllowDownloadContributionPDFnew" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
								<div class="help hideme">
									<!-- ko if: _properties.delphi() -->
									<spring:message code="info.AllowDownloadContributionPDFDelphi" />
									<!-- /ko -->
									<!-- ko ifnot: _properties.delphi() -->
									<spring:message code="info.AllowDownloadContributionPDFnew" />
									<!-- /ko -->
								</div>
							</div>						
							<div style="float: right">
								<div class="onoffswitch">
									<form:checkbox path="survey.downloadContribution" class="onoffswitch-checkbox" data-bind="checked: _properties.downloadContribution()" />
									 <label class="onoffswitch-label" data-bind='class: "onoffswitch-label"+(_properties.delphi() || _properties.eVote() ? " disabled" : "")' onclick="_properties.toggleDownloadContribution()">
								        <span class="onoffswitch-inner"></span>
								        <span class="onoffswitch-switch"></span>
								    </label>
								</div>
							</div>		
						</td>
					</tr>
				</table>
			</div>
			
			<div class="propertiesbox">
				<a class="anchor" id="appearance"></a>
				<label><spring:message code="label.Appearance" /></label>
				<table class="table table-bordered">
					<tr>
						<td>
							<div style="float: left">
								<spring:message code="label.MultiPaging" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
								<div class="help hideme"><spring:message code="info.MultiPaging" /></div>	
							</div>						
							<div style="float: right">
								<div class="onoffswitch">
									<form:checkbox path="survey.multiPaging" class="onoffswitch-checkbox" id="myonoffswitchmultiPaging" data-bind="checked: _properties.multiPaging()" />
									 <label class="onoffswitch-label" onclick="_properties.toggleMultiPaging()">
								        <span class="onoffswitch-inner"></span>
								        <span class="onoffswitch-switch"></span>
								    </label>
								</div>
							</div>
						</td>
					</tr>
					<tr class="subelement noborder" data-bind="visible: multiPaging">
						<td>
							<div style="float: left">
								<spring:message code="label.ValidatedInputPerPageNew" />
							</div>
							<div style="float: right">							
								<div class="onoffswitch">
									<form:checkbox path="survey.validatedPerPage" class="onoffswitch-checkbox" id="myonoffswitchvalidatedPerPage" />
									 <label class="onoffswitch-label" for="myonoffswitchvalidatedPerPage">
								        <span class="onoffswitch-inner"></span>
								        <span class="onoffswitch-switch"></span>
								    </label>
								</div>
							</div>	
						</td>
					</tr>
					<tr class="subelement noborder" data-bind="visible: multiPaging">
						<td>
							<div style="float: left">
								<spring:message code="label.PreventGoingBack" />
							</div>
							<div style="float: right">							
								<div class="onoffswitch">
									<c:choose>
										<c:when test="${form.survey.getIsEVote() || form.survey.getIsDelphi()}">
											<form:checkbox path="survey.preventGoingBack" class="onoffswitch-checkbox" id="myonoffswitchPreventGoingBack" data-bind="checked: _properties.preventGoingBack()" />
											<label class="onoffswitch-label disabled">
												<span class="onoffswitch-inner"></span>
												<span class="onoffswitch-switch"></span>
											</label>
										</c:when>
										<c:otherwise>
											<form:checkbox path="survey.preventGoingBack" class="onoffswitch-checkbox" id="myonoffswitchPreventGoingBack" data-bind="checked: _properties.preventGoingBack()" />
											<label class="onoffswitch-label" onclick="_properties.togglePreventGoingBack()">
												<span class="onoffswitch-inner"></span>
												<span class="onoffswitch-switch"></span>
											</label>
										</c:otherwise>
									</c:choose>

								</div>
							</div>	
						</td>
					</tr>
					<tr>
						<td>
							<div style="float: left">
								<spring:message code="label.ProgressBar" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
								<div class="help hideme"><spring:message code="info.ProgressBar" /></div>	
							</div>						
							<div style="float: right">
								<div class="onoffswitch">
									<form:checkbox path="survey.progressBar" class="onoffswitch-checkbox" id="myonoffswitchprogressBar" data-bind="checked: _properties.progressBar()" />
									 <label data-bind='class: "onoffswitch-label"+ (_properties.eVote() ? " disabled" : "")' onclick="_properties.toggleProgressBar()">
								        <span class="onoffswitch-inner"></span>
								        <span class="onoffswitch-switch"></span>
								    </label>
								</div>
							</div>
						</td>
					</tr>
					<tr class="subelement noborder" data-bind="visible: progressBar">
						<td>
							<div style="float: left">
								<spring:message code="label.DisplayProgress" />
							</div>
							<div style="float: right">							
								<form:radiobutton class="required check" path="survey.progressDisplay" value="0"/><spring:message code="label.percentage" />&#160;
								<form:radiobutton class="required check" path="survey.progressDisplay" value="1"/><spring:message code="label.ratio" />&#160;
								<form:radiobutton class="required check" path="survey.progressDisplay" value="2"/><spring:message code="label.both" />	
							</div>	
						</td>
					</tr>
					<tr>
						<td>
							<div style="float: left">
								<spring:message code="label.MotivationPopup" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
								<div class="help hideme"><spring:message code="info.MotivationPopup" /></div>
							</div>
							<div style="float: right">
								<div class="onoffswitch">
									<form:checkbox path="survey.motivationPopup" class="onoffswitch-checkbox" id="myonoffswitchMotivationPopup" data-bind="checked: _properties.motivationPopup()" />
									<label class="onoffswitch-label" onclick="_properties.toggleMotivationPopup()">
										<span class="onoffswitch-inner"></span>
										<span class="onoffswitch-switch"></span>
									</label>
								</div>
							</div>
						</td>
					</tr>
					<tr class="subelement noborder" data-bind="visible: motivationPopup">
						<td>
							<div style="float: left">
								<spring:message code="label.MotivationPopupTrigger" />
							</div>
							<div style="float: right">
								<form:radiobutton onclick='_properties.useMotivationTime(false);document.getElementById("motivationtriggertimer").value="${form.survey.motivationTriggerTime}";checkProperties(false,false);' class="required check" path="survey.motivationType" value="false"/><spring:message code="label.progress" />&#160;
								<form:radiobutton onclick='_properties.useMotivationTime(true);document.getElementById("motivationtriggerprogress").value="${form.survey.motivationTriggerProgress}";checkProperties(false,false);' class="required check" path="survey.motivationType" value="true"/><spring:message code="label.timer" />&#160;
							</div>
						</td>
					</tr>
					<tr class="subelement noborder" data-bind="visible: motivationPopup">
						<td>
							<div style="float: left">
								<spring:message code="label.MotivationPopupThreshold" />
							</div>
							<div style="float: right">
								<div data-bind="visible: !useMotivationTime()">
									<input id='motivationtriggerprogress' class="form-control number min1 max99" type='number' name='survey.motivationTriggerProgress' min='1' max='99' value="<esapi:encodeForHTMLAttribute>${form.survey.motivationTriggerProgress}</esapi:encodeForHTMLAttribute>">
								</div>
								<div data-bind="visible: useMotivationTime()">
									<input id='motivationtriggertimer' class="form-control number min5 max60" type='number' name='survey.motivationTriggerTime' min='5' max='60' value="<esapi:encodeForHTMLAttribute>${form.survey.motivationTriggerTime}</esapi:encodeForHTMLAttribute>">
								</div>
							</div>
						</td>
					</tr>
					<tr class="subelement noborder" data-bind="visible: motivationPopup">
						<td>
							<div style="float: left">
								<spring:message code="label.MotivationPopupText" />
							</div>
							<div style="float: right; text-align: right;">
								<div class="preview">${form.survey.motivationText} <a class="iconbutton" onclick="$('#tinymcemotivationpopup').show();$(this).closest('.preview').hide()"><span class="glyphicon glyphicon-pencil"></span></a></div>
								<div id="tinymcemotivationpopup" style="display: none">
									<form:textarea class="tinymce" id="edit-survey-motivation-popup" path="survey.motivationText"></form:textarea>
								</div>
							</div>
						</td>
					</tr>
					<tr class="subelement noborder" data-bind="visible: motivationPopup">
						<td>
							<div style="float: left">
								<spring:message code="label.MotivationPopupTitle" />
							</div>
							<div style="float: right">
								<form:input class="form-control" style="width: 500px" type="text" id="edit-survey-motivation-popup-title" path="survey.motivationPopupTitle"/>
							</div>
						</td>
					</tr>
					<tr>
						<td>
							<div style="float: left">
								<spring:message code="label.AccessibilityMode" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
								<div class="help hideme"><spring:message code="help.AccessibilityMode" /></div>
							</div>
							<div style="float: right">							
								<div class="onoffswitch">
									<form:checkbox path="survey.wcagCompliance" class="onoffswitch-checkbox" id="myonoffswitchwcagCompliance" />
									 <label class="onoffswitch-label" for="myonoffswitchwcagCompliance">
								        <span class="onoffswitch-inner"></span>
								        <span class="onoffswitch-switch"></span>
								    </label>
								</div>
							</div>									
						</td>
					</tr>
					<tr class="nobottomborder">
						<td>
							<div style="float: left; max-width: 400px;">
								<spring:message code="label.Logo" />
								
								<div id="logo-cell" style="margin-left: 20px; margin-top: 20px;">
									<c:if test="${form.survey.logo != null}">
										<img src="<c:url value="/files/${form.survey.uniqueId}/${form.survey.logo.uid}" />" />
										<p><esapi:encodeForHTMLAttribute>${form.survey.logo.name}</esapi:encodeForHTMLAttribute></p>
									</c:if>
								</div>
							</div>
							<div style="float: right; text-align: right">
								
								<input type="hidden" name="logo" id="logo" />
					
								<a id="removelogobutton" style="margin-bottom: 10px; ${form.survey.logo == null ? "display: none" : ""}" class="btn btn-default" onclick="$(this).closest('td').find('img').remove();$(this).closest('td').find('p').remove(); $('#logo').val('deleted'); $(this).addClass('disabled').hide(); $('#file-uploader-area-div').hide();"><spring:message code="label.Remove" /></a>
								
								<div id="file-uploader-logo" style="margin-left: 90px;">
									<noscript>
									    <p>Please enable JavaScript to use file uploader.</p>
									</noscript>
								</div>
								<c:choose>
									<c:when test="${form.survey.logo != null}">
										<div id="file-uploader-area-div">
									</c:when>
									<c:otherwise>
										<div id="file-uploader-area-div" class="hideme">
									</c:otherwise>
								</c:choose>
							</div>										
						</td>
					</tr>
					<tr class="subelement noborder">
						<td>
							<div style="float: left">
								<spring:message code="label.LogoAlternativeText" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
							</div>
							<div style="float: right">
								<form:input htmlEscape="false" path="survey.logoText" type="text" class="form-control" style="width: 500px" />
							</div>
							<div style="clear: both"></div>					
							<div class="help hideme"><spring:message code="help.LogoAlternativeText" /></div>
						</td>
					</tr>	
					<tr class="subelement noborder">
						<td>
							<div style="float: left">
								<spring:message code="label.LogoPosition" />
							</div>
							<div style="float: right">
								<form:radiobutton class="required check" path="survey.logoInInfo" value="true"/><spring:message code="label.inInformationArea" />&#160;
								<form:radiobutton class="required check" path="survey.logoInInfo" value="false"/><spring:message code="label.overTitle" />		
							</div>										
						</td>
					</tr>	
					<tr>
						<td>
							<div style="float: left">
								<spring:message code="label.Skin" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>								
							</div>
							<div style="float: right">
								<select name="newskin" class="form-control" style="width: auto; display: inline-block">
									<option></option>
									<c:forEach items="${skins}" var="skin">
										<option value="${skin.id}" <c:if test="${form.survey.skin.id == skin.id}">selected="selected"</c:if>><esapi:encodeForHTML>${skin.displayName}</esapi:encodeForHTML></option>										
									</c:forEach>
								</select>
								<a href="${contextpath}/settings/skin" class="btn btn-default" style="margin-top: -2px;"><spring:message code="label.Manage" /></a>
							</div>
							<div style="clear: both"></div>
							<div class="help hideme"><spring:message code="help.SkinNew" /></div>
						</td>
					</tr>	
					<tr>
						<td>
							<div style="float: left">
								<spring:message code="label.SectionNumbering" />
							</div>
							<div style="float: right">
								<form:select path="survey.sectionNumbering" class="form-control required" style="width: 300px">
									<form:option value="0"><spring:message code="label.NoNumbering" /></form:option>
									<form:option value="1"><spring:message code="label.Numbers" /></form:option>
									<form:option value="2"><spring:message code="label.LettersLowerCase" /></form:option>
									<form:option value="3"><spring:message code="label.LettersUpperCase" /></form:option>
								</form:select>
							</div>											
						</td>
					</tr>
					<tr>
						<td>
							<div style="float: left">
								<spring:message code="label.QuestionNumbering" />
							</div>
							<div style="float: right">							
								<form:select path="survey.questionNumbering" class="form-control required" style="width: 300px">
									<form:option value="0"><spring:message code="label.NoNumbering" /></form:option>
									<form:option value="1"><spring:message code="label.Numbers" /></form:option>
									<form:option value="4"><spring:message code="label.Numbers" /> (<spring:message code="label.ignoreSections" />)</form:option>
									<form:option value="2"><spring:message code="label.LettersLowerCase" /></form:option>
									<form:option value="5"><spring:message code="label.LettersLowerCase" /> (<spring:message code="label.ignoreSections" />)</form:option>
									<form:option value="3"><spring:message code="label.LettersUpperCase" /></form:option>
									<form:option value="6"><spring:message code="label.LettersUpperCase" /> (<spring:message code="label.ignoreSections" />)</form:option>
								</form:select>
							</div>			
						</td>
					</tr>				
				</table>
			</div>
			
			<c:if test="${!form.survey.isOPC}">			
				<div class="propertiesbox">
					<a class="anchor" id="publishresults"></a>
					<label><spring:message code="label.PublishResults" /></label>
					<table class="table table-bordered" data-bind="visible: !_properties.eVote()">
						<tr>
							<td style="padding-bottom: 20px">
								<spring:message code="label.PublishingURLnew" />: 
								<a class="visiblelink" target="_blank" href="${serverprefix}publication/${form.survey.shortname}">${serverprefix}publication/${form.survey.shortname}</a>						
							</td>
						</tr>
						<tr>
							<td>
								<div style="float: left">
									<spring:message code="label.Publish" />
									<a onclick="$(this).closest('td').find('.help').toggle()"><span class='glyphicon glyphicon-info-sign'></span></a>
									<div class="help hideme"><spring:message code="info.Publish" /></div>	
								</div>
								<div style="float: right; min-width: 150px;">	
									<form:checkbox id="showContent" path="survey.publication.showContent" class="check" /><spring:message code="label.Contributions" /><br />
									<form:checkbox id="showStatistics" path="survey.publication.showStatistics" class="check" /><spring:message code="label.Statistics" /><br />
									<form:checkbox path="survey.publication.showSearch" class="check hidden" /><!--<spring:message code="label.Search" /><br />-->
									<c:choose>
										<c:when test="${!form.survey.hasUploadElement}">
											<form:checkbox path="survey.publication.showUploadedDocuments" class="check hideme" />										
										</c:when>
										<c:otherwise>
											<form:checkbox path="survey.publication.showUploadedDocuments" class="check" /><spring:message code="label.UploadedDocuments" />
										</c:otherwise>
									</c:choose>
								</div>
							</td> 
						</tr>
						<tr data-bind="attr:{class: selectedQuestions() ? 'nobottomborder' : ''}">
							<td>
								<div style="float: left">
									<spring:message code="label.QuestionsToPublish" />
									<a onclick="$(this).closest('td').find('.help').toggle()"><span class='glyphicon glyphicon-info-sign'></span></a>
									<div class="help hideme"><spring:message code="info.QuestionsToPublishNew" /></div>
								</div>
								<div style="float: right;  min-width: 150px;">	
									<form:radiobutton data-bind="click: function() {selectedQuestions(false); return true;}" path="survey.publication.allQuestions" value="true" id="questionsToPublishAll" class="check" name="questionsToPublish" /><spring:message code="label.AllQuestions" /><br />
									<form:radiobutton data-bind="click: function() {selectedQuestions(true); return true;}" path="survey.publication.allQuestions" value="false" class="check" name="questionsToPublish" /><spring:message code="label.Selection" /><br />
								</div>
							</td>
						</tr>
						<tr class="noborder" data-bind="visible: selectedQuestions">
							<td>
								<div style="float: right; max-width: 600px;">	
									<div id="questionsToPublishDiv" class="well scrollablediv">
										<table>
										<c:forEach items="${form.survey.getQuestions()}" var="question">
											<c:if test="${!question.getType().equals('Image') && !question.getType().equals('Text') && !question.getType().equals('Download') && !question.getType().equals('Ruler') && !(question.getType().equals('GalleryQuestion') && !question.selection) && !question.getType().equals('Confirmation')}">
												<tr>
													<td style="vertical-align: top;">
														<input type="checkbox" class="check" name="question${question.id}" value="${question.id}" <c:if test="${form.survey.publication.isSelected(question.id)}">checked="checked"</c:if> />
													</td>
													<td>
														${question.strippedTitle}
													</td>
												</tr>
											</c:if>																
										</c:forEach>
										</table>
									</div>
								</div>
							</td>
						</tr>
						<tr data-bind="attr:{class: selectedContributions() ? 'nobottomborder' : ''}">
							<td>
								<div style="float: left">
									<spring:message code="label.Contributions" />
									<a onclick="$(this).closest('td').find('.help').toggle()"><span class='glyphicon glyphicon-info-sign'></span></a>
									<div class="help hideme"><spring:message code="info.ContributionsNew" /></div>	
								</div>
								<div style="float: right; min-width: 150px;">
									<form:radiobutton data-bind="click: function() {selectedContributions(false); return true;}" path="survey.publication.allContributions" value="true" onclick="checkSelections()" id="contributionsToPublishAll" class="check" name="contributionsToPublish" /><spring:message code="label.AllContributions" /><br />
									<form:radiobutton data-bind="click: function() {selectedContributions(true); return true;}" path="survey.publication.allContributions" value="false" onclick="checkSelections()" class="check" name="contributionsToPublish" /><spring:message code="label.Selection" /><br />
								</div>
							</td>
						</tr>
						<tr class="noborder" data-bind="visible: selectedContributions">
							<td>
							    <c:if test="${reportingdatabaseused == null}">
                                    <div id="ResultFilterLimit" style="font-size:90%; text-align: center; margin-bottom: 10px;">
                                        <span class="glyphicon glyphicon-info-sign"></span>
                                        <spring:message code="info.ResultFilterLimit" />
                                    </div>
                                </c:if>

								<div style="float: right; max-width: 600px;">	
									<div class="scrollablediv" id="contributionsToPublishDiv">
										<c:forEach items="${form.survey.getQuestions()}" var="question">
											<c:choose>
												<c:when test="${question.getType() == 'MultipleChoiceQuestion' || question.getType() == 'SingleChoiceQuestion'}">
													<div class="well">
														${question.strippedTitle}
														<div class="filter">
															<c:forEach items="${question.possibleAnswers}" var="possibleanswer" varStatus="status">
																<input onchange="checkNumberOfFilters(${reportingdatabaseused == null})" type="checkbox" class="check" name="contribution${question.id}|${question.uniqueId}" value="${possibleanswer.id}|${possibleanswer.uniqueId}" <c:if test="${form.survey.publication.filter.contains(question.id, question.uniqueId, possibleanswer.id, possibleanswer.uniqueId)}">checked="checked"</c:if> />${possibleanswer.strippedTitle}<br />
															</c:forEach>
														</div>
													</div>
												</c:when>
											</c:choose>
										</c:forEach>
									</div>
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div style="float: left"><spring:message code="label.Password" /></div>
								<div style="float: right">
									<c:choose>
										<c:when test="${form.survey.publication.password != null && form.survey.publication.password.length() > 0}">
											<form:password class="form-control" maxlength="255" autocomplete="off" value="${form.survey.publication.password}" path="survey.publication.password" style="margin: 0px;" onchange="$('#clearpublicationpassword').val($(this).val())" />
											<input class="form-control" style="display: none; width: auto" type="text" maxlength="255" id="clearpublicationpassword" readonly="readonly" disabled="disabled" value="${form.survey.publication.password}" />
											<input class="check" type="checkbox" onclick="checkShowPublicationPassword(this)" /><spring:message code="label.ShowPassword" />
										</c:when>
										<c:otherwise>
											<form:password class="form-control" maxlength="255" autocomplete="off" path="survey.publication.password" style="margin: 0px;" onchange="$('#clearpublicationpassword').val($(this).val())" />
											<input class="form-control" style="display: none; width: auto" type="text" maxlength="255" id="clearpublicationpassword" readonly="readonly" disabled="disabled" />
											<input class="check" type="checkbox" onclick="checkShowPublicationPassword(this)" /><spring:message code="label.ShowPassword" />
										</c:otherwise>
									</c:choose>
								</div>								
							</td>
						</tr>
					</table>
					<table class="table table-bordered" data-bind="visible: _properties.eVote()">
						<tr>
							<td style="padding-bottom: 20px">
								<spring:message code="label.DisabledForEVoteSurvey" />
							</td>
						</tr>
					</table>
				</div>
			</c:if>
			
			<div class="propertiesbox">
				<a class="anchor" id="specialpages"></a>
				<label><spring:message code="label.SpecialPages" /></label>
				<table class="table table-bordered">
					<tr>
						<td>
							<div style="float: left">
								<span class="mandatory" data-bind="visible: !selfAssessment()">*</span><spring:message code="label.ConfirmationPage" />
								<a onclick="$(this).closest('td').find('.help').toggle()" data-bind="visible: !selfAssessment()"><span class='glyphicon glyphicon-info-sign'></span></a>
								<div class="help hideme"><spring:message code="info.ConfirmationMarkUpPage" /></div>
							</div>
							<div style="float: right; text-align: right;" data-bind="visible: !selfAssessment()">
								<form:radiobutton onclick="_properties.useConfLink(false)" class="check" path="survey.confirmationPageLink" value="false"/><spring:message code="label.Text" />&#160;
								<form:radiobutton onclick="_properties.useConfLink(true)" id="conflink"  class="check" path="survey.confirmationPageLink" value="true"/><spring:message code="label.Link" />
								<br />
								<div data-bind="visible: !useConfLink()">
									<div class="preview">${form.survey.confirmationPage} <a class="iconbutton" onclick="$('#tinymceconfpage').show();$(this).closest('.preview').hide()"><span class="glyphicon glyphicon-pencil"></span></a></div>
									<div id="tinymceconfpage" style="display: none; position: relative;">
										<form:textarea id="edit-survey-confirmation-page" class="tinymcefullscreen" path="survey.confirmationPage"></form:textarea>
									</div>		
								</div>
								<div data-bind="visible: useConfLink" id="confLink">
									<form:input class="form-control" path="survey.confirmationLink" ></form:input>
								</div>
							</div>
							<div style="float: right; text-align: right;" data-bind="visible: selfAssessment()">
								<spring:message code="label.DisabledForSASurvey" />
							</div>
						</td>
					</tr>
					<tr>
						<td>
							<div style="float: left">
								<span class="mandatory">*</span><spring:message code="label.UnavailabilityPage" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class='glyphicon glyphicon-info-sign'></span></a>
								<div class="help hideme"><spring:message code="info.UnavailabilityPage" /></div>	
							</div>
							<div style="float: right; text-align: right;">
								<form:radiobutton onclick="_properties.useEscapeLink(false)" class="check" path="survey.escapePageLink" value="false"/><spring:message code="label.Text" />&#160;
								<form:radiobutton onclick="_properties.useEscapeLink(true)"  id="esclink" class="check" path="survey.escapePageLink" value="true"/><spring:message code="label.Link" />
								<br />
								<div data-bind="visible: !useEscapeLink()">
									<div class="preview">${form.survey.escapePage} <a class="iconbutton" onclick="$('#tinymceescapepage').show();$(this).closest('.preview').hide()"><span class="glyphicon glyphicon-pencil"></span></a></div>
									<div id="tinymceescapepage" style="display: none">
										<form:textarea class="tinymce" path="survey.escapePage"></form:textarea>
									</div>		
								</div>
								<div data-bind="visible: useEscapeLink" id="escapeLink">
									<form:input class="form-control" path="survey.escapeLink" ></form:input>
								</div>
							</div>
						</td>
					</tr>							
					<tr>
						<td>
							<div style="float: left">
								<spring:message code="label.ShowPDFOnUnavailabilityPage" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class='glyphicon glyphicon-info-sign'></span></a>
								<div class="help hideme"><spring:message code="info.ShowPDFOnUnavailabilityPage" /></div>	
							</div>
							<div style="float: right">
 								<div class="onoffswitch">
									<form:checkbox path="survey.ShowPDFOnUnavailabilityPage" class="onoffswitch-checkbox" id="myonoffswitchpdfavail" />
									 <label class="onoffswitch-label" for="myonoffswitchpdfavail">
								        <span class="onoffswitch-inner"></span>
								        <span class="onoffswitch-switch"></span>
								    </label>
								</div>
							</div>
						</td>
					</tr>
					<tr>
						<td>
							<div style="float: left">
								<spring:message code="label.ShowDocsOnUnavailabilityPage" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class='glyphicon glyphicon-info-sign'></span></a>
								<div class="help hideme"><spring:message code="info.ShowDocsOnUnavailabilityPage" /></div>	
							</div>
							<div style="float: right">
								<div class="onoffswitch">
									<form:checkbox path="survey.ShowDocsOnUnavailabilityPage" class="onoffswitch-checkbox" id="myonoffswitchdocavail" />
									 <label class="onoffswitch-label" for="myonoffswitchdocavail">
								        <span class="onoffswitch-inner"></span>
								        <span class="onoffswitch-switch"></span>
								    </label>
								</div>
							</div>
						</td>
					</tr>
				</table>
			</div>
			
			<div class="propertiesbox" style="min-height: 500px">
				<a class="anchor" id="type"></a>
				<label><spring:message code="label.Type" /></label>
				<table class="table table-bordered">
					<tr data-bind="visible: opc">
						<td>
							<div style="float: left"><spring:message code="label.EnableOPC" /></div>
							<div style="float: right">
								<div class="onoffswitch">
									<form:checkbox onchange="if ($('#myonoffswitchopc').prop('checked') == false) {$('#BRPConfirmationDialog').modal('show')}" path="survey.isOPC" class="onoffswitch-checkbox" id="myonoffswitchopc" />
									 <label class="onoffswitch-label" for="myonoffswitchopc">
								        <span class="onoffswitch-inner"></span>
								        <span class="onoffswitch-switch"></span>
								    </label>
								</div>
							</div>
						</td>
					</tr>
					<tr data-bind="visible: !opc() && !eVote() && !selfAssessment()">
						<td>
							<div style="float: left"><spring:message code="label.EnableQuiz" /></div>
							<div style="float: right">
								<div class="onoffswitch">
									<c:choose>
										<c:when test="${form.survey.isOPC}">
											<input type="radio" disabled="disabled" name="survey.isQuiz" class="onoffswitch-checkbox" id="myonoffswitchquiz" />
											<label class="onoffswitch-label disabled" for="myonoffswitchquiz">
										        <span class="onoffswitch-inner"></span>
										        <span class="onoffswitch-switch"></span>
										    </label>
										</c:when>
										<c:otherwise>
											<form:checkbox path="survey.isQuiz" onclick="_properties.toggleQuiz(this)" class="onoffswitch-checkbox" data-bind="enable: (_properties.isNormalSurvey()||_properties.quiz())" id="myonoffswitchquiz" />
											<label class="onoffswitch-label" data-bind='class: "onoffswitch-label"+((_properties.isNormalSurvey()||_properties.quiz()) ? "" : " disabled")' for="myonoffswitchquiz">
										        <span class="onoffswitch-inner"></span>
										        <span class="onoffswitch-switch"></span>
										    </label>
										</c:otherwise>
									</c:choose>									
									 
								</div>
							</div>
						</td>
					</tr>
					<tr class="subelement" data-bind="visible: quiz">
						<td>
							<div style="float: left"><spring:message code="label.ShowQuizIcons" /><a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a></div>
							<div style="float: right">
								<div class="onoffswitch">
									<form:checkbox path="survey.showQuizIcons" class="onoffswitch-checkbox" id="myonoffswitchquizicons" />
									 <label class="onoffswitch-label" for="myonoffswitchquizicons">
								        <span class="onoffswitch-inner"></span>
								        <span class="onoffswitch-switch"></span>
								    </label>
								</div>
							</div>
							
							<div style="clear: both"></div>
							<div class="help" style="display: none">
								<spring:message code="info.ShowQuizIcons" />
							</div>						
						</td>
					</tr>
					<tr class="subelement" data-bind="visible: quiz">
						<td>
							<div style="float: left">
								<spring:message code="label.ShowScore" />
								<a onclick="$('#showtotalscorehelp').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
							</div>
							<div style="float: right; max-width: 500px;">
								<table>
									<tr>
										<td style="padding-right: 15px;"><spring:message code="label.TotalScore" /></td>
										<td>
											<div class="onoffswitch">
												<form:checkbox path="survey.showTotalScore" class="onoffswitch-checkbox" id="myonoffswitchtotalscore" />
												 <label class="onoffswitch-label" for="myonoffswitchtotalscore">
											        <span class="onoffswitch-inner"></span>
											        <span class="onoffswitch-switch"></span>
											    </label>
											</div>							
										</td>
									</tr>
									<tr>
										<td style="padding-right: 15px; padding-top: 15px;">
											<spring:message code="label.ScoresByQuestion" />
										</td>
										<td style="padding-top: 15px;">
											<div class="onoffswitch">
												<form:checkbox path="survey.scoresByQuestion" class="onoffswitch-checkbox" id="myonoffswitchscoresbyquestion" />
												 <label class="onoffswitch-label" for="myonoffswitchscoresbyquestion">
											        <span class="onoffswitch-inner"></span>
											        <span class="onoffswitch-switch"></span>
											    </label>
											</div>									
										</td>
									</tr>
								</table>
							</div>
							
							<div style="clear: both"></div>
							<div class="help" id="showtotalscorehelp" style="display: none">
								<spring:message code="info.ShowTotalScoreNew" />
							</div>		
						</td>
					</tr>
					
					<tr class="subelement" data-bind="visible: quiz">
						<td>
							<div style="float: left">
								<spring:message code="label.SetATimeLimit" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
								<div class="help" style="display: none">
									<spring:message code="info.SetATimeLimit" />
								</div>		
							</div>
							<div style="float: right; max-width: 500px;">
								<table>
									<tr>
										<td colspan="2">
											<div style="text-align: right">
												<form:input style="width: 100px; display: inline" placeholder="HH:mm:ss" class="form-control time" path="survey.timeLimit" oninput="_properties.checkTimeLimit(this)" ></form:input>
											</div>
										</td>
									</tr>
									<tr>
										<td style="padding-right: 15px; padding-top: 15px; text-align: right;">
											<spring:message code="label.ShowCountdownTimer" />
										</td>
										<td style="padding-top: 15px; width: 50px;">
											<div class="onoffswitch">
												<form:checkbox path="survey.showCountdown" class="onoffswitch-checkbox" data-bind="checked: _properties.showCountdown()" />
												 <label class="onoffswitch-label" data-bind='class: "onoffswitch-label"+ ((_properties.timeLimit().length == 0) ? " disabled" : "")' onclick="_properties.toggleShowCountdown()">
											        <span class="onoffswitch-inner"></span>
											        <span class="onoffswitch-switch"></span>
											    </label>
											</div>							
										</td>
									</tr>
								</table>					
							</div>
							
							<div style="clear: both"></div>
						
						</td>
					</tr>
					
					<tr class="subelement" data-bind="visible: quiz">
						<td>
							<div style="float: left">
								<spring:message code="label.WelcomeMessage" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class='glyphicon glyphicon-info-sign'></span></a>
								<div class="help hideme"><spring:message code="info.WelcomeMessage" /></div>	
							</div>
							<div style="float: right">
								<div class="preview">${form.survey.quizWelcomeMessage} <a class="iconbutton" onclick="$('#tinymcewelcome').show();$(this).closest('.preview').hide()" style="margin-left: 10px;"><span class="glyphicon glyphicon-pencil"></span></a></div>
								<div id="tinymcewelcome" style="display: none">
									<form:textarea class="tinymce" path="survey.quizWelcomeMessage"></form:textarea>
								</div>
							</div>
						</td> 
					</tr>
					<tr class="subelement" data-bind="visible: quiz">
						<td>
							<div style="float: left">
								<spring:message code="label.ResultsMessage" />
								<a onclick="$(this).closest('td').find('.help').toggle()"><span class='glyphicon glyphicon-info-sign'></span></a>
								<div class="help hideme"><spring:message code="info.ResultsMessage" /></div>	
							</div>
							<div style="float: right">
								<div class="preview">${form.survey.quizResultsMessage} <a class="iconbutton" onclick="$('#tinymceresult').show();$(this).closest('.preview').hide()" style="margin-left: 10px;"><span class="glyphicon glyphicon-pencil"></span></a></div>
								<div id="tinymceresult" style="display: none">
									<form:textarea class="tinymce" path="survey.quizResultsMessage"></form:textarea>
								</div>
							</div>
						</td> 
					</tr>
					<c:if test="${enabledelphi || form.survey.isDelphi}">
						<tr data-bind="visible: !opc() && !eVote() && !selfAssessment()">
							<td>
								<div style="float: left"><spring:message code="label.EnableDelphi" /></div>
								<div style="float: right">
									<div class="onoffswitch">
										<c:choose>
											<c:when test="${form.survey.isOPC}">
												<input type="radio" disabled="disabled" name="survey.isDelphi" class="onoffswitch-checkbox" id="myonoffswitchdelphi" />
												<label class="onoffswitch-label disabled" for="myonoffswitchdelphi">
													<span class="onoffswitch-inner"></span>
													<span class="onoffswitch-switch"></span>
												</label>
											</c:when>
											<c:otherwise>
												<form:checkbox path="survey.isDelphi" onclick="_properties.toggleDelphi(this)" class="onoffswitch-checkbox" data-bind="enable: (_properties.isNormalSurvey()||_properties.delphi())" id="myonoffswitchdelphi" />
												<label class="onoffswitch-label" data-bind='class: "onoffswitch-label"+((_properties.isNormalSurvey()||_properties.delphi()) ? "" : " disabled")' for="myonoffswitchdelphi">
													<span class="onoffswitch-inner"></span>
													<span class="onoffswitch-switch"></span>
												</label>
											</c:otherwise>
										</c:choose>
									</div>
								</div>
							</td>
						</tr>
						<tr class="subelement" data-bind="visible: delphi">
							<td>
								<div style="float: left">
									<spring:message code="label.ShowDelphiStartPage" />
									<a onclick="$(this).closest('td').find('.help').toggle()"><span class='glyphicon glyphicon-info-sign'></span></a>
									<div class="help hideme"><spring:message code="info.ShowDelphiStartPage" /></div>
								</div>
								<div style="float: right">
									<div class="onoffswitch">
										<form:checkbox path="survey.isDelphiShowStartPage" class="onoffswitch-checkbox" id="isDelphiShowStartPage" />
										<label class="onoffswitch-label" for="isDelphiShowStartPage">
											<span class="onoffswitch-inner"></span>
											<span class="onoffswitch-switch"></span>
										</label>
									</div>
								</div>
								<div style="clear: both"></div>
							</td>
						</tr>
						<tr class="subelement" data-bind="visible: delphi">
							<td>
								<div style="float: left">
									<spring:message code="label.ShowDelphiResultsTableAndStatisticsInstantly" />
									<a onclick="$(this).closest('td').find('.help').toggle()"><span class='glyphicon glyphicon-info-sign'></span></a>
									<div class="help hideme"><spring:message code="info.ShowDelphiResultsTableAndStatisticsInstantly" /></div>
								</div>
								<div style="float: right">
									<div class="onoffswitch">
										<form:checkbox path="survey.isDelphiShowAnswersAndStatisticsInstantly" class="onoffswitch-checkbox" id="isDelphiShowAnswersAndStatisticsInstantly" />
										<label class="onoffswitch-label" for="isDelphiShowAnswersAndStatisticsInstantly">
											<span class="onoffswitch-inner"></span>
											<span class="onoffswitch-switch"></span>
										</label>
									</div>
								</div>
								<div style="clear: both"></div>
							</td>
						</tr>
						<tr class="subelement" data-bind="visible: delphi">
							<td>
								<div style="float: left">
									<spring:message code="label.ShowDelphiAnswerTable" />
									<a onclick="$(this).closest('td').find('.help').toggle()"><span class='glyphicon glyphicon-info-sign'></span></a>
									<div class="help hideme"><spring:message code="info.ShowDelphiAnswerTable" /></div>
								</div>
								<div style="float: right">
									<div class="onoffswitch">
										<form:checkbox path="survey.isDelphiShowAnswers" class="onoffswitch-checkbox" id="myonoffswitchdelphianswers" />
										<label class="onoffswitch-label" for="myonoffswitchdelphianswers">
											<span class="onoffswitch-inner"></span>
											<span class="onoffswitch-switch"></span>
										</label>
									</div>
								</div>
								<div style="clear: both"></div>
							</td>
						</tr>
						<tr class="subelement" data-bind="visible: delphi">
							<td>
								<div style="float: left">
									<spring:message code="label.MinimumResultsForStatistics" />
									<a onclick="$(this).closest('td').find('.help').toggle()"><span class='glyphicon glyphicon-info-sign'></span></a>
									<div class="help hideme"><spring:message code="info.MinimumResultsForStatistics" /></div>
								</div>
								<div style="float: right">
									<div style="float: right; max-width: 500px;">
										<form:input htmlEscape="false" path="survey.minNumberDelphiStatistics" id="minNumberDelphiStatistics" type="number" class="form-control number max1000000000" min='1' max='1000000000' style="display: inline-block" />
									</div>
								</div>
								<div style="clear: both"></div>
							</td>
						</tr>
					</c:if>
					
					<c:if test="${form.survey.isSelfAssessment}">
						<tr>
							<td>
								<div style="float: left"><spring:message code="label.SelfAssessment" /></div>
								<div style="float: right">
									<div class="onoffswitch">
										<input checked="checked" disabled="disabled" type="checkbox" class="onoffswitch-checkbox" id="myonoffswitchselfassessment">
										<label class="onoffswitch-label disabled" for="myonoffswitchselfassessment">
											<span class="onoffswitch-inner"></span>
											<span class="onoffswitch-switch"></span>
										</label>
									</div>
								</div>
							</td>
						</tr>
					</c:if>	

					<c:if test="${form.survey.isEVote}">
						<tr>
							<td>
								<div style="float: left"><spring:message code="label.EnableEVote" /></div>
								<div style="float: right">
									<div class="onoffswitch">
										<input checked="checked" disabled="disabled" type="checkbox" class="onoffswitch-checkbox" id="myonoffswitchevote">
										<label class="onoffswitch-label disabled" for="myonoffswitchevote">
											<span class="onoffswitch-inner"></span>
											<span class="onoffswitch-switch"></span>
										</label>
									</div>
								</div>
							</td>
						</tr>
						<tr class="subelement" data-bind="visible: eVote">
							<td>
								<div style="float: left; max-width: 500px;">
									<spring:message code="label.Template" />
									</div>
								<div style="float: right; text-align: right">
									<spring:message code="${form.survey.geteVoteTemplateTitle()}" />
								</div>
							</td>
						</tr>
						<tr class="subelement" data-bind="visible: eVote">
							<td>
								<div style="float: left; max-width: 500px;">
									<spring:message code="label.Quorum" />
									<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
								</div>
								<div style="float: right; text-align: right">
									<div>
										<input id='quorum' class="form-control number max1000000000" type='number' name='survey.quorum' min='0' max='1000000000' value="<esapi:encodeForHTMLAttribute>${form.survey.quorum}</esapi:encodeForHTMLAttribute>">
									</div>
								</div>
								<div style="clear: both"></div>
								<div class="help" style="display: none; margin-top: 10px;">
									<span><spring:message code="message.Quorum" /></span>
								</div>
							</td>
						</tr>

						<c:if test="${form.survey.geteVoteTemplate() != 'p'}">
							<tr class="subelement" data-bind="visible: eVote">
								<td>
									<div style="float: left; max-width: 500px;">
										<spring:message code="label.EligibleLists" />
										<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
									</div>
									<div style="float: right; text-align: right">
										<div>
											<input id='minListPercent' class="form-control number min0 max100" type='number' name='survey.minListPercent' min='0' max='100' value="<esapi:encodeForHTMLAttribute>${form.survey.minListPercent}</esapi:encodeForHTMLAttribute>">
										</div>
									</div>
									<div style="clear: both"></div>
									<div class="help" style="display: none; margin-top: 10px;">
										<span><spring:message code="message.EligibleLists" /></span>
									</div>
								</td>
							</tr>
						</c:if>

						<tr class="subelement" data-bind="visible: eVote">
							<td>
								<div style="float: left; max-width: 500px;">
									<spring:message code="label.MaximumPreferentialVotes" />
									<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
								</div>
								<div style="float: right; text-align: right">
									<div>
										<input id='maxPrefVotes' class="form-control number min1 max1000" type='number' name='survey.maxPrefVotes' min='1' max='1000' value="<esapi:encodeForHTMLAttribute>${form.survey.maxPrefVotes}</esapi:encodeForHTMLAttribute>">
									</div>
								</div>
								<div style="clear: both"></div>
								<div class="help" style="display: none; margin-top: 10px;">
									<span><spring:message code="message.MaximumPreferentialVotes" /></span>
								</div>
							</td>
						</tr>

						<c:if test="${form.survey.geteVoteTemplate() != 'p' && form.survey.geteVoteTemplate() != 'o'}">
							<tr class="subelement" data-bind="visible: eVote">
								<td>
									<div style="float: left; max-width: 500px;">
										<spring:message code="label.NumberOfSeatsToAllocate" />
										<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
									</div>
									<div style="float: right; text-align: right">
										<div>
											<input id='seatsToAllocate' class="form-control number min1 max1000" type='number' name='survey.seatsToAllocate' min='1' max='1000' value="<esapi:encodeForHTMLAttribute>${form.survey.seatsToAllocate}</esapi:encodeForHTMLAttribute>">
										</div>
									</div>
									<div style="clear: both"></div>
									<div class="help" style="display: none; margin-top: 10px;">
										<span><spring:message code="message.NumberOfSeatsToAllocate" /></span>
									</div>
								</td>
							</tr>
						</c:if>

						<tr class="subelement" data-bind="visible: eVote">
							<td>
								<div style="float: left; max-width: 500px;">
									<spring:message code="label.ShowSimulationPage" />
									<a onclick="$(this).closest('td').find('.help').toggle()"><span class='glyphicon glyphicon-info-sign'></span></a>
									<div class="help hideme"><spring:message code="info.EnableResultsTestPage" /></div>
								</div>
								<div style="float: right; text-align: right">
									<div class="onoffswitch">
										<form:checkbox path="survey.showResultsTestPage" class="onoffswitch-checkbox" id="enableresultstestpage" />
										<label class="onoffswitch-label" for="enableresultstestpage">
											<span class="onoffswitch-inner"></span>
											<span class="onoffswitch-switch"></span>
										</label>
									</div>
								</div>
							</td>
						</tr>
					</c:if>

				</table>	
			</div>			
			
		</form:form>
	</div>
	
    <jsp:include page="properties-dialogs.jsp" />

</div>

<jsp:include page="../footer.jsp" />

<jsp:include page="propertiesFoot.jsp" />

</body>
</html>
