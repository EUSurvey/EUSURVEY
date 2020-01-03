<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page trimDirectiveWhitespaces="true" %>
	
<div class="modal" id="edit-properties-dialog" data-backdrop="static">
	<div class="modal-dialog modal-lg">
    <div class="modal-content">
	  <div class="modal-header">
	  	<spring:message code="label.Properties" />: <span id="dialog-title"></span>
	  </div>
	  <div id="edit-properties-dialog-body" class="modal-body">	
	  	
	  		<form:form id="save-form" method="POST" action="${contextpath}/${sessioninfo.shortname}/management/properties?${_csrf.parameterName}=${_csrf.token}" enctype="multipart/form-data" modelAttribute="form">
				<form:hidden path="survey.id" />	
				<input type="hidden" id="selected-tab" name="tab" value="1" />
				<input type="hidden" id="origin" name="origin" value="" />
				<input type="hidden" id="survey-security" name="survey.security" value="" />
			
				<div id="edit-tab-1" style="text-align: left">
					
					<div id="edit-prop-tabs-1">
						<table class="table table-striped table-bordered">
							<tr>
								<td class="table-label"><span class="mandatory">*</span><spring:message code="label.UniqueIdentifier" /></td>
								<td>
									<form:input id="edit-survey-shortname" type="text" maxlength="255" class="required freetext max255" path="survey.shortname" /><br />
									<span><spring:message code="message.MeaningfulShortname" />&nbsp;<spring:message code="message.MeaningfulShortname2" /></span>	
								</td>
							</tr>
							<tr>
								<td class="table-label"><span class="mandatory">*</span><spring:message code="label.Title" /></td>
								<td>
									<form:textarea class="tinymcealign2 required xhtml" id="edit-survey-title" path="survey.title"></form:textarea>
								</td>
							</tr>
							<tr>
								<td class="table-label"><span class="mandatory">*</span><spring:message code="label.PivotLanguage" /></td>
								<td>
									<form:select path="survey.language" class="required">
										<c:forEach items="${form.survey.completeTranslations}" var="language">				
											<c:choose>
												<c:when test="${form.survey.language.code.equals(language)}">
													<form:option selected="selected" value="${language}"><esapi:encodeForHTML>${language}</esapi:encodeForHTML></form:option>
												</c:when>
												<c:otherwise>
													<form:option value="${language}"><esapi:encodeForHTML>${language}</esapi:encodeForHTML></form:option>
												</c:otherwise>
											</c:choose>
										</c:forEach>
									</form:select>				
								</td>
							</tr>
							<tr>
								<td class="table-label"><span class="mandatory">*</span><spring:message code="label.Contact" /></td>
								<td>
									<c:choose>
										<c:when test='${form.survey.contact.contains("@")}'>
											<div style="float:left; text-align: right;">
												<select onchange="checkPropertiesSurveyContactType()" id="survey-contact-type" style="width: 120px;">
													<option value="email" selected="selected"><spring:message code="label.Email" /></option>
													<option value="url"><spring:message code="label.Webpage" /></option>								
												</select><br />
												<div id="survey-contact-label-label" style="display:none; font-weight: bold; margin-top: 5px;"><spring:message code="label.Label" /></div>
											</div>
											<div style="float:left; margin-left: 10px">
												<form:input htmlEscape="false" path="survey.contact" class="required email" type="text" maxlength="255" /><br />
												<form:input htmlEscape="false" path="survey.contactLabel" type="text" style="display: none" maxlength="255" />
											</div>
										</c:when>
										<c:otherwise>
											<div style="float:left; text-align: right;">
												<select onchange="checkPropertiesSurveyContactType()" id="survey-contact-type" style="width: 120px;">
													<option value="email"><spring:message code="label.Email" /></option>
													<option value="url" selected="selected"><spring:message code="label.Webpage" /></option>								
												</select><br />
												<div id="survey-contact-label-label" style="font-weight: bold; margin-top: 5px;"><spring:message code="label.Label" /></div>
											</div>
											<div style="float:left; margin-left: 10px">
												<form:input htmlEscape="false" path="survey.contact" class="required" type="text" maxlength="255" /><br />
												<form:input htmlEscape="false" path="survey.contactLabel" maxlength="255" type="text" />
											</div>									
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
							
						</table>
					</div>
					<div id="edit-prop-tabs-2">
						<table class="table table-striped table-bordered">
							<tr>
								<td class="table-label" style="width:60%"><span class="mandatory">*</span><spring:message code="label.Security" /></td>
								<td>
									<c:choose>
										<c:when test='${form.survey.isOPC}'>
											<input class="required check" disabled="disabled" type="radio" name="radio-new-survey-security" id="radio-new-survey-security-open" value="open" /><spring:message code="form.Open" />&#160;
											<input disabled="disabled" checked="checked" class="required check" type="radio" name="radio-new-survey-security" id="radio-new-survey-security-secured" value="secured" /><spring:message code="form.Secured" />&#160;
										</c:when>
										<c:when test='${form.survey.security.equals("open") || form.survey.security.equals("openanonymous")}'>
											<input onclick="checkSecurity(); checkCaptcha();" checked="checked" class="required check" type="radio" name="radio-new-survey-security" id="radio-new-survey-security-open" value="open" /><spring:message code="form.Open" />&#160;
											<input onclick="checkSecurity(); checkCaptcha();" class="required check" type="radio" name="radio-new-survey-security" id="radio-new-survey-security-secured" value="secured" /><spring:message code="form.Secured" />
										</c:when>										
										<c:otherwise>
											<input onclick="checkSecurity(); checkCaptcha();" class="required check" type="radio" name="radio-new-survey-security" id="radio-new-survey-security-open" value="open" /><spring:message code="form.Open" />&#160;
											<input onclick="checkSecurity(); checkCaptcha();" checked="checked" class="required check" type="radio" name="radio-new-survey-security" id="radio-new-survey-security-secured" value="secured" /><spring:message code="form.Secured" />&#160;
										</c:otherwise>
									</c:choose>
									<div style="margin-top: 10px; margin-bottom: 10px;" id="edit-password">
										<spring:message code="label.Password" /><br />										
										<c:choose>
											<c:when test='${form.survey.isOPC}'>
												<input type="text" maxlength="255" disabled="disabled" autocomplete="off" style="margin: 0px; width: 150px;" /><br />
												<input class="check" type="checkbox" disabled="disabled" /><spring:message code="label.ShowPassword" />
											</c:when>
											<c:when test="${form.survey.password != null && form.survey.password.length() > 0}">
												<form:password maxlength="255" autocomplete="off" value="********" path="survey.password" style="margin: 0px;" onchange="$('#clearpassword').val($(this).val())" />
												<input style="width: auto" type="text" maxlength="255" id="clearpassword" class="hideme" readonly="readonly" disabled="disabled" value="${form.survey.password}" /><br />
												<input class="check" type="checkbox" onclick="checkShowPassword(this)" /><spring:message code="label.ShowPassword" />
											</c:when>
											<c:otherwise>
												<form:password maxlength="255" autocomplete="off" path="survey.password" style="margin: 0px;" /><br />
												<input class="check" type="checkbox" disabled="disabled" /><spring:message code="label.ShowPassword" />
											</c:otherwise>
										</c:choose>												
									</div>
									<c:choose>
										<c:when test='${form.survey.isOPC}'>
											<div style="margin-top: 5px; margin-bottom: 5px;" id="edit-ecas">
												<input type="checkbox" disabled="disabled" checked="checked" class="check" /><spring:message code="label.EnableEULogin" />
												<form:hidden path="survey.ecasSecurity" />		
												<div style="margin-left: 20px">
													<input type="radio" disabled="disabled" checked="checked" name="ecas-mode" class="check" /><spring:message code="label.All" /><br />
													<input type="radio" disabled="disabled" name="ecas-mode" class="check" /><spring:message code="label.InternalStaffOnly" />
													<form:hidden path="survey.ecasMode" name="ecas-mode" />
												</div>									
											</div>
										</c:when>	
										<c:when test="${USER.getGlobalPrivilegeValue('ECAccess') > 0}">
											<div style="margin-top: 5px; margin-bottom: 5px;" id="edit-ecas">
												<form:checkbox path="survey.ecasSecurity" onclick="checkEcasSecurity()" id="enableecas" name="enableecas" class="check" /><spring:message code="label.EnableEULogin" />							
												<div style="margin-left: 20px">
													<form:radiobutton path="survey.ecasMode" id="ecas-mode-all" name="ecas-mode" value="all" /><spring:message code="label.All" /><br />
													<form:radiobutton path="survey.ecasMode" id="ecas-mode-internal" name="ecas-mode" value="internal" /><spring:message code="label.InternalStaffOnly" />
												</div>
												<div style="margin-left: 20px; margin-top: 10px;">
													<spring:message code="label.ContributionsPerUser" />: 
													<form:input htmlEscape="false" path="survey.allowedContributionsPerUser" type="text" class="spinner required number min1 integer" maxlength="10" style="width: 50px" />
												</div>									
											</div>
										</c:when>
										<c:otherwise>
											<form:hidden path="survey.ecasSecurity" id="enableecas" name="enableecas"  />						
											<form:hidden path="survey.ecasMode" name="ecas-mode" />
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
							<tr>
								<td class="table-label">
									<spring:message code="label.Privacy" />
									<div class="help"><spring:message code="form.Privacy.Identified" /></div>
								</td>
								<td>
									<c:choose>
										<c:when test='${form.survey.isOPC}'>
											<input disabled="disabled" checked="checked" class="check" type="radio" name="radio-new-survey-privacy" id="radio-new-survey-privacy-secured" value="" /><spring:message code="label.Yes" />&#160;
											<input disabled="disabled" class="check" type="radio" name="radio-new-survey-privacy" id="radio-new-survey-privacy-anonymous" value="anonymous" /><spring:message code="label.No" />
										</c:when>
										<c:when test='${haspublishedanswers != null && (form.survey.security.equals("openanonymous") || form.survey.security.equals("securedanonymous"))}'>
											<input disabled="disabled" class="required check" type="radio" name="radio-new-survey-privacy" id="radio-new-survey-privacy-secured" value="" /><spring:message code="label.Yes" />&#160;
											<input disabled="disabled" checked="checked" class="required check" type="radio" name="radio-new-survey-privacy" id="radio-new-survey-privacy-anonymous" value="anonymous" /><spring:message code="label.No" />
										</c:when>
										<c:when test='${form.survey.security.equals("openanonymous") || form.survey.security.equals("securedanonymous")}'>
											<input class="required check" type="radio" name="radio-new-survey-privacy" id="radio-new-survey-privacy-secured" value="" /><spring:message code="label.Yes" />&#160;
											<input checked="checked" class="required check" type="radio" name="radio-new-survey-privacy" id="radio-new-survey-privacy-anonymous" value="anonymous" /><spring:message code="label.No" />
										</c:when>
										<c:otherwise>
											<input checked="checked" class="required check" type="radio" name="radio-new-survey-privacy" id="radio-new-survey-privacy-secured" value="" /><spring:message code="label.Yes" />&#160;
											<input class="required check" type="radio" name="radio-new-survey-privacy" id="radio-new-survey-privacy-anonymous" value="anonymous" /><spring:message code="label.No" />
										</c:otherwise>
									</c:choose>										
								</td>
							</tr>
							<c:if test="${enablepublicsurveys}">
								<tr>
									<td class="table-label">
										<spring:message code="label.Visibility" />
										<div class="help"><spring:message code="label.AdvertiseYourFormA" />&nbsp;<spring:message code="label.AdvertiseYourFormB" /> <a data-toggle="tooltip" title="<spring:message code="label.LearnMore" />" target="_blank" href="${contextpath}/home/helpauthors#_Toc7-5"><img src="${contextpath}/resources/images/icons/24/help_bubble.png" alt="Help" style="margin-left:10px; margin-right:10px;"></a></div>
									</td>
									<td>
										<form:radiobutton onclick="checkCaptcha()" class="required check" path="survey.listForm" value="true"/><spring:message code="label.Public" />&#160;
										<form:radiobutton onclick="checkCaptcha()" class="required check" path="survey.listForm" value="false"/><spring:message code="label.Private" />										
									</td>
								</tr>
							</c:if>
							<tr>
								<td class="table-label">
									<spring:message code="label.Captcha" />
									<div class="help"><spring:message code="label.CaptchaForPublicOpen" /></div>
								</td>
								<td>
									<c:choose>
										<c:when test='${form.survey.isOPC}'>
											<input type="radio" disabled="true" class="check" /><spring:message code="label.Yes" />&#160;
											<input type="radio" disabled="true" class="check" checked="checked" /><spring:message code="label.No" />
										
											<form:hidden path="survey.captcha"/>
										</c:when>
										<c:otherwise>
											<form:radiobutton class="required check" path="survey.captcha" value="true"/><spring:message code="label.Yes" />&#160;
											<form:radiobutton class="required check" path="survey.captcha" value="false"/><spring:message code="label.No" />
										</c:otherwise>
									</c:choose>		
								</td>
							</tr>
							<tr>
								<td class="table-label">
									<spring:message code="label.SaveAsDraft" />
									<div class="help"><spring:message code="label.AllowSaveAsDraft" /></div>
								</td>
								<td>
									<form:radiobutton class="required check" path="survey.saveAsDraft" value="true"/><spring:message code="label.Yes" />&#160;
									<form:radiobutton class="required check" path="survey.saveAsDraft" value="false"/><spring:message code="label.No" />		
								</td>
							</tr>
							
							<tr>
								<td class="table-label">
									<spring:message code="label.EditContribution" />
									<div class="help"><spring:message code="label.AllowChangeContribution" /></div>		
								</td>
								<td>
									<form:radiobutton class="required check" path="survey.changeContribution" value="true"/><spring:message code="label.Yes" />&#160;
									<form:radiobutton class="required check" path="survey.changeContribution" value="false"/><spring:message code="label.No" />		
								</td>
							</tr>
							<tr>
								<td class="table-label">
									<spring:message code="label.DownloadContribution" />
									<div class="help"><spring:message code="label.AllowDownloadContributionPDF" /></div>		
								</td>
								<td>
									<form:radiobutton class="required check" path="survey.downloadContribution" value="true"/><spring:message code="label.Yes" />&#160;
									<form:radiobutton class="required check" path="survey.downloadContribution" value="false"/><spring:message code="label.No" />		
								</td>
							</tr>
						</table>
					</div>
					<div id="edit-prop-tabs-3" class="tab-pane">
						<table class="table table-striped table-bordered">
							<tr>
								<td class="table-label">
									<spring:message code="label.MultiPaging" />
									<div class="help"><spring:message code="label.ShowInSeparatePage" /></div>	
								</td>
								<td>
									<form:radiobutton onclick="checkValidationPerPage()" class="required check" path="survey.multiPaging" value="true"/><spring:message code="label.Yes" />&#160;
									<form:radiobutton onclick="checkValidationPerPage()" class="required check" path="survey.multiPaging" value="false"/><spring:message code="label.No" />
								</td>
							</tr>
							<tr>
								<td class="table-label">
									<spring:message code="label.Validation" />
									<div class="help"><spring:message code="label.ValidatedInputPerPage" /></div>	
								</td>
								<td>
									<form:radiobutton class="required check" path="survey.validatedPerPage" value="true"/><spring:message code="label.Yes" />&#160;
									<form:radiobutton class="required check" path="survey.validatedPerPage" value="false"/><spring:message code="label.No" />		
								</td>
							</tr>
							<tr>
								<td class="table-label">
									<spring:message code="label.WCAGCompliance" />
									<div class="help"><spring:message code="help.WCAGCompliance" /></div>
								</td>
								<td>
									<form:radiobutton class="required check" path="survey.wcagCompliance" value="true"/><spring:message code="label.Yes" />&#160;
									<form:radiobutton class="required check" path="survey.wcagCompliance" value="false"/><spring:message code="label.No" />											
								</td>
							</tr>
							<tr>
								<td class="table-label"><spring:message code="label.Style" /></td>
								<td>&#160;</td>
							</tr>
							<tr>
								<td style="padding-left: 50px;"><spring:message code="label.Logo" /></td>
								<td id="logo-cell">
									<c:if test="${form.survey.logo != null}">
										<img src="<c:url value="/files/${form.survey.uniqueId}/${form.survey.logo.uid}" />" />
										<p><esapi:encodeForHTMLAttribute>${form.survey.logo.name}</esapi:encodeForHTMLAttribute></p>
									</c:if>
									<input type="hidden" name="logo" id="logo" />
									<div style="float:left; z-index: 1000">
										<c:choose>
											<c:when test="${form.survey.logo != null}">
												<a class="btn btn-default" onclick="$(this).closest('td').find('img').remove();$(this).closest('td').find('p').remove(); $('#logo').val('deleted'); $(this).addClass('disabled'); $('#file-uploader-area-div').hide();"><spring:message code="label.Remove" /></a>
											</c:when>
											<c:otherwise>
												<a class="btn disabled btn-default" onclick="$(this).closest('td').find('img').remove();$(this).closest('td').find('p').remove(); $('#logo').val('deleted'); $(this).addClass('disabled'); $('#file-uploader-area-div').hide();"><spring:message code="label.Remove" /></a>
											</c:otherwise>
										</c:choose>
									</div>
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
										<form:radiobutton class="required check" path="survey.logoInInfo" value="true"/><spring:message code="label.inInformationArea" />&#160;
										<form:radiobutton class="required check" path="survey.logoInInfo" value="false"/><spring:message code="label.overTitle" />		
									</div>													
								</td>
							</tr>
							<tr>
								<td style="padding-left: 50px;"><spring:message code="label.Skin" /></td>
								<td>
									<select name="newskin">
										<option></option>
										<c:forEach items="${skins}" var="skin">
											<option value="${skin.id}" <c:if test="${form.survey.skin.id == skin.id}">selected="selected"</c:if>><esapi:encodeForHTML>${skin.name}</esapi:encodeForHTML></option>										
										</c:forEach>
									</select>													
								</td>
							</tr>	
							<tr>
								<td class="table-label"><spring:message code="label.AutomaticNumbering" /></td>
								<td>&#160;</td>
							</tr>
							<tr>
								<td style="padding-left: 50px;"><spring:message code="form.Sections" /></td>
								<td>
									<form:select path="survey.sectionNumbering" class="required">
										<form:option value="0"><spring:message code="label.NoNumbering" /></form:option>
										<form:option value="1"><spring:message code="label.Numbers" /></form:option>
										<form:option value="2"><spring:message code="label.LettersLowerCase" /></form:option>
										<form:option value="3"><spring:message code="label.LettersUpperCase" /></form:option>
									</form:select>													
								</td>
							</tr>
							<tr>
								<td style="padding-left: 50px;"><spring:message code="form.Questions" /></td>
								<td>							
									<form:select path="survey.questionNumbering" class="required">
										<form:option value="0"><spring:message code="label.NoNumbering" /></form:option>
										<form:option value="1"><spring:message code="label.Numbers" /></form:option>
										<form:option value="4"><spring:message code="label.Numbers" /> (<spring:message code="label.ignoreSections" />)</form:option>
										<form:option value="2"><spring:message code="label.LettersLowerCase" /></form:option>
										<form:option value="5"><spring:message code="label.LettersLowerCase" /> (<spring:message code="label.ignoreSections" />)</form:option>
										<form:option value="3"><spring:message code="label.LettersUpperCase" /></form:option>
										<form:option value="6"><spring:message code="label.LettersUpperCase" /> (<spring:message code="label.ignoreSections" />)</form:option>
									</form:select>						
								</td>
							</tr>
						</table>
					</div>
					<div id="edit-prop-tabs-4" class="tab-pane">
						<table class="table table-striped table-bordered">
							<tr>
								<td class="table-label" style="vertical-align: top"><span class="mandatory">*</span><spring:message code="label.ConfirmationPage" /></td>
								<td>
									<form:radiobutton onclick="checkConfirmationPage()" class="check" path="survey.confirmationPageLink" value="false"/><spring:message code="label.UseText" />&#160;
									<form:textarea style="width: 500px" path="survey.confirmationPage" class="tinymce required"></form:textarea>									
									<br />
									<form:radiobutton onclick="checkConfirmationPage()" id="conflink"  class="check" path="survey.confirmationPageLink" value="true"/><spring:message code="label.UseLink" /><br />
									<form:input class="targeturl" style="width: 500px" path="survey.confirmationLink" ></form:input>	
								</td>
							</tr>
							<tr>
								<td class="table-label" style="vertical-align: top"><span class="mandatory">*</span><spring:message code="label.UnavailabilityPage" /></td>
								<td>
									<form:radiobutton onclick="checkEscapePage()" class="check" path="survey.escapePageLink" value="false"/><spring:message code="label.UseText" />&#160;
									<form:textarea path="survey.escapePage" class="tinymce required"></form:textarea>
									<br />
									<form:radiobutton onclick="checkEscapePage()"  id="esclink" class="check" path="survey.escapePageLink" value="true"/><spring:message code="label.UseLink" /><br />
									<form:input class="targeturl" style="width: 500px" path="survey.escapeLink" ></form:input>
								</td>
							</tr>							
							<tr>
								<td class="table-label" style="vertical-align: top"><span class="mandatory">*</span><spring:message code="label.ShowPDFOnUnavailabilityPage" /></td>
								<td>
									<form:radiobutton class="check" path="survey.ShowPDFOnUnavailabilityPage" value="false"/><spring:message code="label.No" />&#160;
									<br />
									<form:radiobutton class="check" path="survey.ShowPDFOnUnavailabilityPage" value="true"/><spring:message code="label.Yes" /><br />
								</td>
							</tr>
							<tr>
								<td class="table-label" style="vertical-align: top"><span class="mandatory">*</span><spring:message code="label.ShowDocsOnUnavailabilityPage" /></td>
								<td>
									<form:radiobutton class="check" path="survey.ShowDocsOnUnavailabilityPage" value="false"/><spring:message code="label.No" />&#160;
									<br />
									<form:radiobutton class="check" path="survey.ShowDocsOnUnavailabilityPage" value="true"/><spring:message code="label.Yes" /><br />
								</td>
							</tr>
						</table>
					</div>
					<div id="edit-prop-tabs-5" class="tab-pane">
						<table class="table table-striped table-bordered">
							<tr>
								<td class="table-label">
									<spring:message code="label.AutomaticPublishing" />
									<div class="help"><spring:message code="info.AutomaticPublishing" /></div>
								</td>
								<td>
									<c:choose>
										<c:when test="${form.survey.isOPC}">
											<c:choose>
												<c:when test="${form.survey.automaticPublishing}">
													<input type="radio" class="check" checked="checked" disabled="disabled" /><spring:message code="label.Yes" />&#160;
													<input type="radio" class="check" disabled="disabled" /><spring:message code="label.No" />
												</c:when>
												<c:otherwise>
													<input type="radio" class="check" disabled="disabled" /><spring:message code="label.Yes" />&#160;
													<input type="radio" class="check" checked="checked" disabled="disabled" /><spring:message code="label.No" />
												</c:otherwise>
											</c:choose>
											<form:hidden path="survey.automaticPublishing" />
										</c:when>
										<c:otherwise>
											<form:radiobutton onclick="checkAutomaticPublishing()" id="autopub" class="check" path="survey.automaticPublishing" value="true"/><spring:message code="label.Yes" />&#160;
											<form:radiobutton onclick="checkAutomaticPublishing()" class="check" path="survey.automaticPublishing" value="false"/><spring:message code="label.No" />
										</c:otherwise>
									</c:choose>	
								</td>
							</tr>
							<tr>
								<td style="padding-left: 50px;"><span class="hideme mandatory autopub">*</span><spring:message code="label.StartDate" /></td>
								<td>
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
								</td>
							</tr>
							<tr>
								<td style="padding-left: 50px;"><span class="hideme mandatory autopub">*</span><spring:message code="label.ExpiryDate" /></td>
								<td>
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
								</td>
							</tr>
							<tr>
								<td class="table-label">
									<spring:message code="label.EndNotification" />
									<div class="help"><spring:message code="info.EndNotification" /></div>
								</td>
								<td>
									<spring:message code="label.NotifyMe" /><br />
									<c:choose>
										<c:when test="${form.survey.notificationValue != null && form.survey.notificationValue.length() > 0}">
											<input onchange="checkNotification()" type="radio" id="notificationselector1" class="check" name="notification" value="true" checked="checked"/><spring:message code="label.Yes" />&#160;
											<input onchange="checkNotification()" type="radio" id="notificationselector2" class="check" name="notification" value="false"/><spring:message code="label.No" />	
										</c:when>
										<c:otherwise>
											<input onchange="checkNotification()" type="radio" id="notificationselector1" class="check" name="notification" value="true"/><spring:message code="label.Yes" />&#160;
											<input onchange="checkNotification()" type="radio" id="notificationselector2" class="check" name="notification" value="false" checked="checked"/><spring:message code="label.No" />	
										</c:otherwise>
									</c:choose>																	
									<br />
									<form:select path="survey.notificationValue" style="width: auto;">
										<c:forEach var="i" begin="1" end="30">
											<form:option value="${i}"><c:out value="${i}"/></form:option>
										</c:forEach>
									</form:select>	
									<form:select path="survey.notificationUnit" style="width: auto;">
										<form:option value="0"><spring:message code="label.hours" /></form:option>
										<form:option value="1"><spring:message code="label.days" /></form:option>
										<form:option value="2"><spring:message code="label.weeks" /></form:option>
										<form:option value="3"><spring:message code="label.months" /></form:option>
									</form:select><br />
									
									<form:radiobutton class="check" path="survey.notifyAll" value="true"/><spring:message code="label.AllFormManagers" />&#160;
									<form:radiobutton class="check" path="survey.notifyAll" value="false"/><spring:message code="label.FormCreatorOnly" />		
														
								</td>
							</tr>			
							<tr>
								<td class="table-label">
									<spring:message code="label.CreateContacts" />
									<div class="help"><spring:message code="info.NoContactsCreated" /></div>	
								</td>
								<td>
									<form:radiobutton class="required check" path="survey.registrationForm" value="true"/><spring:message code="label.Yes" />&#160;
									<form:radiobutton class="required check" path="survey.registrationForm" value="false"/><spring:message code="label.No" /><br />
								</td>
							</tr>
							<tr>
								<td class="table-label">
									<spring:message code="label.UsefulLinks" />
								</td>
								<td>
									<table class="table table-bordered" id="usefullinkstable">
										<tr>
											<td><spring:message code="label.Label" /></td>
											<td><spring:message code="label.URL" /></td>
											<td>
												<a data-toggle="tooltip" title="<spring:message code="label.AddUsefulLink" />" class="btn btn-default btn-xs" onclick="addLinksRow()"><span class="glyphicon glyphicon-plus"></span></a>
											</td>
										</tr>
										<c:forEach var="link" items="${form.survey.getAdvancedUsefulLinks()}" varStatus="rowCounter">
											<tr class="usefullink">
												<td>
													<input class="xhtml" type="text" maxlength="255" name="linklabel${rowCounter.index}" value="<esapi:encodeForHTMLAttribute>${link.key}</esapi:encodeForHTMLAttribute>" />
												</td>
												<td>
													<input type="text" class="targeturl" maxlength="255" name="linkurl${rowCounter.index}" value="<esapi:encodeForHTMLAttribute>${link.value}</esapi:encodeForHTMLAttribute>" />
												</td>
												<td style="vertical-align: middle">
													<a data-toggle="tooltip" title="<spring:message code="label.RemoveUsefulLink" />" class="btn btn-default btn-xs"  onclick="$(this).parent().parent().remove()"><span class="glyphicon glyphicon-remove"></span></a>
												</td>
											</tr>
										</c:forEach>										
									</table>
								</td>
							</tr>
							<tr>
								<td class="table-label">
									<spring:message code="label.BackgroundDocuments" />
								</td>
								<td>
									<table class="table table-bordered" id="backgrounddocumentstable">
										<tr>
											<td><spring:message code="label.Label" /></td>
											<td><spring:message code="label.Document" /></td>
											<td>
												<a data-toggle="tooltip" title="<spring:message code="label.AddBackgroundDocument" />" class="btn btn-default btn-xs"  onclick="addDocRow()"><span class="glyphicon glyphicon-plus"></span></a>
											</td>
										</tr>
										<c:forEach var="link" items="${form.survey.getBackgroundDocumentsAlphabetical()}" varStatus="rowCounter">
											<tr>
												<td>
													<input class="xhtml" type="text" maxlength="255" name="doclabel${rowCounter.index}" value="<esapi:encodeForHTMLAttribute>${link.key}</esapi:encodeForHTMLAttribute>" />
												</td>
												<td>
													<div style="word-wrap: break-word; max-width: 200px;">
														<a href="<esapi:encodeForHTMLAttribute>${link.value}</esapi:encodeForHTMLAttribute>">${form.survey.getFileNameForBackgroundDocument(link.key)}</a>
													</div>
													<input type="hidden" name="docurl${rowCounter.index}" value="<esapi:encodeForHTMLAttribute>${link.value}</esapi:encodeForHTMLAttribute>" />
												</td>
												<td style="vertical-align: middle">
													<a data-toggle="tooltip" title="<spring:message code="label.RemoveBackgroundDocument" />" class="btn btn-default btn-xs"  onclick="$(this).parent().parent().remove()"><span class="glyphicon glyphicon-remove"></span></a>
												</td>
											</tr>
										</c:forEach>										
									</table>
								</td>
							</tr>							
						</table>
					</div>
					<div id="publish-results-error" style="display: none">
						<spring:message code="error.PublishResultsPendingChanges" />
					</div>
					<div id="edit-prop-tabs-6" class="tab-pane">
						<table class="table table-striped table-bordered">
							<tr>
								<td class="table-label"><spring:message code="label.Publish" /></td>
								<td>
									<form:checkbox id="showContent" path="survey.publication.showContent" class="check" /><spring:message code="label.FullSetOfAnswers" /><br />
									<form:checkbox id="showStatistics" path="survey.publication.showStatistics" class="check" /><spring:message code="label.Statistics" /><br />
									<form:checkbox path="survey.publication.showSearch" class="check" /><spring:message code="label.Search" /><br />
									<c:choose>
										<c:when test="${!form.survey.hasUploadElement}">
											<form:checkbox path="survey.publication.showUploadedDocuments" class="check hideme" />										
										</c:when>
										<c:otherwise>
											<form:checkbox path="survey.publication.showUploadedDocuments" class="check" /><spring:message code="label.UploadedDocuments" />
										</c:otherwise>
									</c:choose>
								</td> 
							</tr>
							<tr>
								<td class="table-label"><spring:message code="label.QuestionsToPublish" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.QuestionsToPublish" />'><span class='glyphicon glyphicon-question-sign'></span></a></td>
								<td>
									<form:radiobutton path="survey.publication.allQuestions" value="true" onclick="checkSelections()" id="questionsToPublishAll" class="check" name="questionsToPublish" /><spring:message code="label.AllQuestions" /><br />
									<form:radiobutton path="survey.publication.allQuestions" value="false" onclick="checkSelections()" class="check" name="questionsToPublish" /><spring:message code="label.Selection" /><br />
									<div id="questionsToPublishDiv" class="well scrollablediv">
										<c:forEach items="${form.survey.getQuestions()}" var="question">
											<c:if test="${!question.getType().equals('Image') && !question.getType().equals('Text') && !question.getType().equals('Download')}">									
												<div>
													<input type="checkbox" class="check" name="question${question.id}" value="${question.id}" <c:if test="${form.survey.publication.isSelected(question.id)}">checked="checked"</c:if> />
													${question.title}
												</div>
											</c:if>																
										</c:forEach>
									</div>
								</td>
							</tr>
							<tr>
								<td class="table-label"><spring:message code="label.Contributions" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.Contributions" />'><span class='glyphicon glyphicon-question-sign'></span></a></td>
								<td>
									<form:radiobutton path="survey.publication.allContributions" value="true" onclick="checkSelections()" id="contributionsToPublishAll" class="check" name="contributionsToPublish" /><spring:message code="label.AllContributions" /><br />
									<form:radiobutton path="survey.publication.allContributions" value="false" onclick="checkSelections()" class="check" name="contributionsToPublish" /><spring:message code="label.Selection" /><br />
									<div class="scrollablediv" id="contributionsToPublishDiv">
										<c:forEach items="${form.survey.getQuestions()}" var="question">
											<c:choose>
												<c:when test="${question.getType() == 'MultipleChoiceQuestion' || question.getType() == 'SingleChoiceQuestion'}">
													<div class="well">
														${question.title}
														<div>
															<c:forEach items="${question.possibleAnswers}" var="possibleanswer" varStatus="status">
																<input type="checkbox" class="check" name="contribution${question.id}|${question.uniqueId}" value="${possibleanswer.id}|${possibleanswer.uniqueId}" <c:if test="${form.survey.publication.filter.contains(question.id, question.uniqueId, possibleanswer.id, possibleanswer.uniqueId)}">checked="checked"</c:if> />${possibleanswer.title}<br />
															</c:forEach>
														</div>
													</div>
												</c:when>
											</c:choose>
										</c:forEach>
									</div>
								</td>
							</tr>
							<tr>
								<td class="table-label"><spring:message code="label.Security" /></td>
								<td>
									<spring:message code="label.Password" /><br />
									
									<input value="<esapi:encodeForHTMLAttribute>${form.survey.publication.password}</esapi:encodeForHTMLAttribute>" id="survey.publication.password" name="survey.publication.password" style="margin: 0px;" onchange="$('#clearpublicationpassword').text($(this).val())" type="password" maxlength="255" autocomplete="new-password">
									<input class="check" type="checkbox" onclick="checkShowPublicationPassword(this)" /><spring:message code="label.ShowPassword" />
						
									<div id="clearpublicationpassword" class="hideme">${form.survey.publication.password}</div>									
								</td>
							</tr>
						</table>
					</div>	
					<div id="edit-prop-tabs-7" class="tab-pane">
						<table class="table table-bordered">
							<tr style="background-color: rgb(249, 249, 249)">
								<td class="table-label"><spring:message code="label.EnableQuiz" /></td>
								<td colspan="2">
									<c:choose>
										<c:when test='${form.survey.isOPC}'>
											<input type="radio" class="check" disabled="disabled" /><spring:message code="label.Yes" />&#160;
											<input type="radio" class="check" checked="checked" disabled="disabled" /><spring:message code="label.No" />
											<form:hidden path="survey.isQuiz" />
										</c:when>
										<c:otherwise>
											<form:radiobutton class="check" path="survey.isQuiz" value="true" onclick="checkQuiz(true, true)" /><spring:message code="label.Yes" />&#160;
											<form:radiobutton class="check" path="survey.isQuiz" value="false" onclick="checkQuiz(true, true)"/><spring:message code="label.No" />
										</c:otherwise>
									</c:choose>		
								</td>
							</tr>
							<tr>
								<td class="table-label">
									<spring:message code="label.ShowQuizIcons" />
									<div class="help" style="max-width: 300px"><spring:message code="info.ShowQuizIcons" /></div>
								</td>
								<td colspan="2">
									<form:radiobutton class="check" path="survey.showQuizIcons" value="true" /><spring:message code="label.Yes" />&#160;
									<form:radiobutton class="check" path="survey.showQuizIcons" value="false" /><spring:message code="label.No" />								
								</td>
							</tr>
							<tr>
								<td rowspan="2" class="table-label">
									<spring:message code="label.ShowScore" />
									<div class="help" style="max-width: 300px"><spring:message code="info.ShowTotalScore" /></div>
								</td>
								<td>
									<spring:message code="label.TotalScore" />
								</td>
								<td style="min-width: 100px">
									<form:radiobutton class="check" path="survey.showTotalScore" value="true" onclick="checkQuiz(true, false)"/><spring:message code="label.Yes" />&#160;
									<form:radiobutton class="check" path="survey.showTotalScore" value="false" onclick="checkQuiz(true, false)"/><spring:message code="label.No" />								
								</td>
							</tr>
							<tr>
								<td>
									<spring:message code="label.ScoresByQuestion" />
									<div class="help"><spring:message code="info.ScoresByQuestion" /></div>
								</td>
								<td style="min-width: 100px">
									<form:radiobutton class="check" path="survey.scoresByQuestion" value="true"/><spring:message code="label.Yes" />&#160;
									<form:radiobutton class="check" path="survey.scoresByQuestion" value="false"/><spring:message code="label.No" />										
								</td>
							</tr>
							<tr style="background-color: rgb(249, 249, 249)">
								<td class="table-label"><spring:message code="label.WelcomeMessage" /></td>
								<td colspan="2">
									<form:textarea style="width: 500px" path="survey.quizWelcomeMessage" class="tinymce"></form:textarea>
								</td> 
							</tr>
							<tr>
								<td class="table-label"><spring:message code="label.ResultsMessage" /></td>
								<td colspan="2">
									<form:textarea style="width: 500px" path="survey.quizResultsMessage" class="tinymce"></form:textarea>
								</td> 
							</tr>
						</table>
					</div>
					<div id="edit-prop-tabs-8" class="tab-pane">
						<table class="table table-bordered">
							<tr style="background-color: rgb(249, 249, 249)">
								<td class="table-label"><spring:message code="label.EnableOPC" /></td>
								<td colspan="2">
									<c:choose>
										<c:when test='${form.survey.isOPC}'>
											<input type="radio" class="check" checked="checked" disabled="disabled" /><spring:message code="label.Yes" />&#160;
											<input type="radio" class="check" disabled="disabled" /><spring:message code="label.No" />
											<form:hidden path="survey.isOPC" />
										</c:when>
										<c:otherwise>
											<form:radiobutton class="check" path="survey.isOPC" value="true" /><spring:message code="label.Yes" />&#160;
											<form:radiobutton class="check" path="survey.isOPC" value="false"/><spring:message code="label.No" />		
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
						</table>
					</div>
				</div>
			</form:form>
	  	
	  </div>
	  <div class="modal-footer">
	 	<a id="properties-save-button" onclick="checkPropertiesAndSubmit(false, false);"  class="btn btn-primary"><spring:message code="label.Save" /></a>
	 	<a id="properties-cancel-button"  class="btn btn-default" onclick="cancelDialog()"><spring:message code="label.Cancel" /></a>		 			
	  </div>
	</div>
	</div>
</div>
