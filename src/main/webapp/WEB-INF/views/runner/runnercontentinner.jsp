<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>	
<%@ page import="java.util.Map" %>
<%@ page import="com.ec.survey.model.Form" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>	
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>

	<input type="hidden" id="validatedPerPage" value="${form.survey.validatedPerPage}" />
	<input type="hidden" id="newlang" name="newlang" value="${form.language.code }" />
	<input type="hidden" id="newlangpost" name="newlangpost" value="false" />
	<input type="hidden" id="newcss" name="newcss" value="" />
	<input type="hidden" id="newviewpost" name="newviewpost" value="false" />
	<input type="hidden" id="wcagMode" name="wcagMode" value="${form.wcagCompliance}" />	
	<input type="hidden" id="multipaging" value="${form.survey.multiPaging}" />			
		<c:choose>
				<c:when test="${publication != null}">
					<div style="width: 220px; max-width: 220px">
						<img src="<c:url value="/files/${form.survey.uniqueId}/${form.survey.logo.uid}" />" alt="logo" style="width: 220px" />
					</div>
				</c:when>
				<c:when test="${form.survey.logo != null && !form.survey.logoInInfo}">
					<div style="max-width: 900px">
						<img src="<c:url value="/files/${form.survey.uniqueId}/${form.survey.logo.uid}" />" alt="logo" style="max-width: 1300px;" />
					</div>
				</c:when>
				<c:when test="${form.survey.logo != null && responsive != null}">
					<div style="max-width: 100%">
						<img src="<c:url value="/files/${form.survey.uniqueId}/${form.survey.logo.uid}" />" alt="logo" style="max-width: 100%;" />
					</div>
				</c:when>
			</c:choose>						
				
				<div class="left-area">				
					
					<div id="nolocalstorage" class="hideme" style="margin-bottom: 10px; text-align: right; margin-right: 10px;">
						<span class="alert-danger" style="padding: 10px;">${form.getMessage("info.LocalStorageDisabled")}</span>						
					</div>
					
					<c:choose>
						<c:when test="${mode == 'editcontribution' }">
							<input style="display: none" class="check" type="checkbox" id="saveLocalBackup" onchange="checkLocalBackup()" /> 
						</c:when>
						<c:otherwise>
							<div id="localstorageinfo" class="visible-lg" style="margin-bottom: 10px; text-align: right; margin-right: 10px;">
								<input class="check" type="checkbox" checked="checked" id="saveLocalBackup" onchange="checkLocalBackup()" /> 
								<label for="saveLocalBackup">${form.getMessage("info.DeactivateLocalStorage")}</label>
							</div>
						</c:otherwise>
					</c:choose>
				
					<div class="surveytitle">${form.survey.title}</div><br />

					<c:if test="${form.survey.containsMandatoryQuestion()}">
						<div class="info-box" style="width: 400px; max-width: 100%">${form.getMessage("message.StarMandatory")}</div>
					</c:if>
					<c:if test="${(form.answerSets.size() == 0 || !form.answerSets[0].disclaimerMinimized)}">
						<c:if test="${!oss}">
							<c:if test="${(form.survey.owner.type == 'ECAS' && form.survey.owner.getGlobalPrivilegeValue('ECAccess') == 0) || form.survey.owner.type == 'SYSTEM'  }">
								<div id="ecDisclaimer">
									<div style="float: right; margin-top: -15px; margin-right: -15px;">
										<input type="hidden" id="disclaimerMinimized" name="disclaimerMinimized" value="${disclaimerMinimized}" />
										<a style="cursor: pointer" onclick="$('#disclaimerMinimized').val('true'); $('#ecDisclaimer').hide();" ><span class="glyphicon glyphicon-remove"></span></a>
									</div>								
									<span class="ecDisclaimerTitle">${form.getMessage("label.Disclaimer")}</span>
									<p>
										${form.getMessage("info.Disclaimer")}
									</p>								
								</div>
							</c:if>
						</c:if>	
					</c:if>
					<span class="introduction">${form.survey.introduction}</span>					
					
					<div id="page-tabs" class="panel panel-default visible-lg" style="margin-top:20px;">
						<div class="panel-body">
							<div style="font-size: 20px;float:left; width:10%">${form.getMessage("label.Pages")}</div>
							
							<div style="float:left; width:90">							
								<ul class="nav nav-pills">
								<c:forEach var="page" items="${form.getPages()}" varStatus="rowCounter">
									<c:choose>
										
								 		<c:when test="${rowCounter.index == 0}">
											<li data-id="${page[0].id}" id="tab${rowCounter.index}" class="pagebutton active" >
										</c:when>
										<c:otherwise>
											<li data-id="${page[0].id}" id="tab${rowCounter.index}" class="pagebutton">
										</c:otherwise>
									</c:choose>
 										<a href="#page${rowCounter.index}" style="cursor:pointer;" onclick="selectPage(${rowCounter.index});" >
 											<c:choose>
 												<c:when test="${page[0].getType() == 'Section' && page[0].tabTitle != null && page[0].tabTitle.length() > 0}">
 													${page[0].tabTitle}
 												</c:when>
 												<c:when test="${page[0].getType() == 'Section'}">
 													<esapi:encodeForHTML>${page[0].shortname}</esapi:encodeForHTML>
 												</c:when>
 												<c:otherwise>
 													${form.getMessage("label.Start")}
 												</c:otherwise>
 											</c:choose>	 										
 										</a>
 										</li>
									</span>
								</c:forEach>
								</ul>
							</div>							
						</div>
					
					</div>	
					<div style="clear: left"></div>
					
					<c:forEach var="page" items="${form.getPages()}" varStatus="rowCounter">
					
					 	<c:choose>
					 		<c:when test="${rowCounter.index == 0}">
								<div class="single-page" id="page${rowCounter.index}" tabindex="-1">
							</c:when>
							<c:otherwise>
								<div class="single-page" id="page${rowCounter.index}" style="display: none">
							</c:otherwise>
						</c:choose>						
							
							<c:forEach var="element" items="${page}">
								<c:if test="${publication == null || publication.isAllQuestions() || publication.isSelected(element.id)}">
								 <fieldset>							
								  <c:choose>
								    <c:when test="${element.hasPDFWidth}">
								    	<div class="elementwrapper elem_${element.id}">
								    </c:when>
								    <c:otherwise>
										 <div class="elementwrapper">
								    </c:otherwise>
								  </c:choose>								  
								  	  <c:choose>
										<c:when test="${element.isDummy() && element.isDependent && ((invisibleElements == null && forpdf == null) || invisibleElements.contains(element.uniqueId))}">
											<div class="emptyelement survey-element untriggered 1" data-id="${element.id}" data-uid="${element.uniqueId}" data-triggers="${element.triggers}" style="margin-top: 5px; display: none;">
										</c:when>
										<c:when test="${element.getType() == 'Matrix' && element.getAllQuestionsDependent() && ((invisibleElements == null && forpdf == null) || invisibleElements.contains(element.uniqueId))}">
											<div class="emptyelement survey-element untriggered 2" id="${element.id}" data-id="${element.id}" data-uid="${element.uniqueId}" data-triggers="${element.triggers}" style="display: none;">
										</c:when>
										<c:when test="${element.isDependent && ((invisibleElements == null && forpdf == null) || invisibleElements.contains(element.uniqueId))}">
											<div class="emptyelement survey-element untriggered 3" id="${element.id}" data-id="${element.id}" data-triggers="${element.triggers}" data-uid="${element.uniqueId}" style="display: none;">
										</c:when>
										<c:when test="${element.isDependent}">
											<div class="emptyelement survey-element 3b" id="${element.id}" data-id="${element.id}" data-uid="${element.uniqueId}" data-triggers="${element.triggers}">
										</c:when>
										<c:when test="${element.isDummy()}">
											<div class="emptyelement survey-element 4" data-id="${element.id}" data-uid="${element.uniqueId}" style="margin-top: 5px;">
										</c:when>
										<c:otherwise>
										    <div class="emptyelement survey-element 5" id="${element.id}" data-id="${element.id}" data-uid="${element.uniqueId}">
										</c:otherwise>
									</c:choose>
								  						  
								  		<img src="${contextpath}/resources/images/ajax-loader.gif" />									
									</div>
									<c:if test="${form.survey.isDelphi}">
										<div class="explanation-section">
											<label class="questiontitle">${form.getMessage("label.ExplainYourAnswer")}</label>
											<textarea class="explanation-editor"></textarea>
										</div>
									</c:if>
									</div>
									</fieldset>
								</c:if>
							</c:forEach>
						</div>						
						
					</c:forEach>
					
					<div class="hpdiv">
						<label for="hp-7fk9s82jShfgak">${form.getMessage("info.leaveempty")}</label>
						<textarea id="hp-7fk9s82jShfgak" name="hp-7fk9s82jShfgak" class="hp" autocomplete="false"></textarea>
 					</div>
					
					<c:if test="${form.survey.captcha}">
						<%@ include file="../captcha.jsp" %>					
					</c:if>
									
				<c:if test="${submit == true}">
					<div style="text-align: center; margin-top: 20px;">
						<input type="button" id="btnPrevious" style="display: none;" value="${form.getMessage("label.Previous")}"  onclick="previousPage();" class="btn btn-default" />
						<c:choose>
							<c:when test="${dialogmode != null }">
								<input type="button" id="btnSubmit" value="${form.getMessage("label.Save")}" onclick="validateInputAndSubmitRunner($('#runnerForm'));" class="btn btn-primary" />
								<input type="button" id="btnSubmit2" value="${form.getMessage("label.Close")}" onclick="window.open('', '_self', '');window.close();" class="btn btn-default" />
							</c:when>
							<c:otherwise>
								<input type="button" id="btnSubmit" value="${form.getMessage("label.Submit")}" onclick="validateInputAndSubmitRunner($('#runnerForm'));" class="btn btn-primary hidden" />
							</c:otherwise>
						</c:choose>
						
						<input type="button" id="btnNext" style="display: none;" value="${form.getMessage("label.Next")}"  onclick="nextPage();" class="btn btn-default btn-primary" />
					
						<c:if test="${responsive != null && mode != 'editcontribution' && dialogmode == null && form.survey.saveAsDraft}">
							<input type="button" id="btnSaveDraftMobile" value="${form.getMessage("label.SaveAsDraft")}" onclick="saveDraft('${mode}');" class="btn btn-default hidden" style="margin-left: 10px" />
							<c:if test="${form.answerSets.size() > 0}">
	 							<div style="margin-top: 20px">
	 								${form.getMessage("label.LastSavedOn")}<br />
	 								<spring:eval expression="form.answerSets[0].updateDate" />
		 						</div>
							</c:if>	
						</c:if>	
					</div>		
				</c:if>
					
				</div>
				
				<c:if test="${publication == null && responsive == null}">
				<div class="right-area" style="z-index: 1; position: relative">
					
					<c:if test="${form.survey.logo != null && form.survey.logoInInfo}">
						<img style="max-width: 100%; margin-top: 10px;" src="<c:url value="/files/${form.survey.uniqueId}/${form.survey.logo.uid}" />" alt="logo" />
						<hr style="margin-top: 15px;" />
					</c:if>			
					
					<c:if test='${runnermode == null  || form.survey.skin == null || !form.survey.skin.name.equals("New Official EC Skin")}'>
					
						<c:if test='${!form.survey.skin.name.equals("New Official EC Skin") && mode != "editcontribution"}'>
					
							<div class="linkstitle" style="margin-bottom: 5px;">${form.getMessage("label.Views")}</div>		
														
							<c:choose>
								<c:when test="${readonlyMode != null && readonlyMode == true}">
									<div id="normalcss" style="color: #ccc">
										${form.getMessage("label.Standard")}&#160;
										<a class="link visiblelink css-switch disabled" id="css-switch-disabled" style="color: #ccc">${form.getMessage("label.AccessibilityMode")}</a>						
									</div>
									
									<div id="enhancedcss" class="hideme" style="color: #ccc">
										<a class="link css-switch normal" id="css-switch-normal" style="color: #ccc">${form.getMessage("label.Standard")}</a>&#160;
										${form.getMessage("label.AccessibilityMode")}						
									</div>
								</c:when>									
								<c:otherwise>
									<div id="normalcss">
										${form.getMessage("label.Standard")}&#160;
										<a class="link visiblelink css-switch disabled" id="css-switch-disabled"  onclick="switchCss('${mode}','wcag');">${form.getMessage("label.AccessibilityMode")}</a>						
									</div>
									
									<div id="enhancedcss" class="hideme">
										<a class="link css-switch normal" id="css-switch-normal"  onclick="switchCss('${mode}','standard');">${form.getMessage("label.Standard")}</a>&#160;
										${form.getMessage("label.AccessibilityMode")}						
									</div>
								</c:otherwise>
							</c:choose>
							
							<hr style="margin-top: 15px;" />
						
						</c:if>
					
						<c:if test='${form.getLanguages().size() != 0 && mode != "editcontribution"}'>		
							<label for="langSelectorRunner">
								<div class="linkstitle" style="margin-bottom: 5px;">${form.getMessage("label.Languages")}</div>	
							</label>
							
							<c:choose>
								<c:when test="${readonlyMode != null && readonlyMode == true}">
									<select id="langSelectorRunner" name="langSelectorRunner" disabled="disabled">	
								</c:when>
								<c:otherwise>
									<select id="langSelectorRunner" name="langSelectorRunner" onchange="changeLanguageSelectOption('${mode}')">	
								</c:otherwise>
							</c:choose>
							
							<c:forEach var="lang" items="${form.getLanguagesAlphabetical()}">
								<c:choose>
									<c:when test="${lang.value.code == form.language.code}">
										<option value="<esapi:encodeForHTML>${lang.value.code}</esapi:encodeForHTML>" selected="selected"><esapi:encodeForHTML>[${lang.value.code}] ${lang.value.name}</esapi:encodeForHTML></option>
									</c:when>
									<c:otherwise>
										<option value="<esapi:encodeForHTML>${lang.value.code}</esapi:encodeForHTML>"><esapi:encodeForHTML>[${lang.value.code}] ${lang.value.name}</esapi:encodeForHTML></option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
							</select>							
							<hr style="margin-top: 15px;" />	
						</c:if>			
					
					</c:if>
					
					<c:if test="${form.survey.getUsefulLinks().size() != 0}">					
						<div class="linkstitle">${form.getMessage("label.UsefulLinks")}</div>						
						<c:forEach var="link" items="${form.survey.getAdvancedUsefulLinks()}">
							<div style="margin-top: 5px;" ><a class="link visiblelink" target="_blank" rel="noopener noreferrer" href="<esapi:encodeForHTMLAttribute>${link.value}</esapi:encodeForHTMLAttribute>">${link.key}</a></div>
						</c:forEach>							
						<hr style="margin-top: 15px;" />	
					</c:if>
					
					<c:if test="${form.survey.getBackgroundDocuments().size() != 0}">
						<div class="linkstitle">${form.getMessage("label.BackgroundDocuments")}</div>						
						<c:forEach var="link" items="${form.survey.getBackgroundDocumentsAlphabetical()}">
							<div style="margin-top: 5px;" ><a class="link visiblelink" target="_blank" href="<esapi:encodeForHTMLAttribute>${link.value}</esapi:encodeForHTMLAttribute>">${link.key}</a></div>
						</c:forEach>							
						<hr style="margin-top: 15px;" />
					</c:if>				
					
					<div id="contact-and-pdf" style="word-wrap: break-word;">
					
						<c:if test="${form.survey.contact != null}">
							<div class="linkstitle" style="margin-bottom: 5px;">${form.getMessage("label.Contact")}</div>
							
							<c:choose>
								<c:when test="${form.survey.contact.startsWith('form:')}">
									<a target="_blank" class="link visibleLink" data-toggle="tooltip" title="${form.getMessage("info.ContactForm")}" href="${contextpath}/runner/contactform/${form.survey.shortname}">${form.getMessage("label.ContactForm")}</a>
								</c:when>
								<c:when test="${form.survey.contact.contains('@')}">
									<i class="icon icon-envelope" style="vertical-align: middle"></i>
									<a class="link" href="mailto:<esapi:encodeForHTMLAttribute>${form.survey.contact}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML>${form.survey.contact}</esapi:encodeForHTML></a>
								</c:when>
								<c:otherwise>
									<i class="icon icon-globe" style="vertical-align: middle"></i>
									<a target="_blank" class="link visiblelink" href="<esapi:encodeForHTMLAttribute>${form.survey.fixedContact}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML>${form.survey.fixedContactLabel}</esapi:encodeForHTML></a>
								</c:otherwise>
							</c:choose>
							
							<hr style="margin-top: 15px;" />
						</c:if>						
						
						<c:if test="${!form.survey.isQuiz}">
							<div>
								<a data-toggle="tooltip" title="${form.getMessage("label.DownloadEmptyPDFversion")}" id="download-survey-pdf-link" class="link visiblelink" onclick="downloadSurveyPDF('${form.survey.id}','${form.language.code}','${uniqueCode}')">${form.getMessage("label.DownloadPDFversion")}</a>
								<span id="download-survey-pdf-dialog-running" class="hideme">${form.getMessage("info.FileCreation")}</span>
								<span id="download-survey-pdf-dialog-ready" class="hideme">${form.getMessage("info.FileCreated")}</span>
								<div id="download-survey-pdf-dialog-spinner" class="hideme" style="padding-left: 5px;"><img src="${contextpath}/resources/images/ajax-loader.gif" /></div>
								<br /><a style="white-space: nowrap; overflow-x: visible; display: none; margin-top: 10px" id="download-survey-pdf-dialog-result" target="_blank" class="btn btn-primary" href="<c:url value="/pdf/survey/${form.survey.id}?lang=${form.language.code}&unique=${uniqueCode}"/>">${form.getMessage("label.Download")}</a>
								<div id="download-survey-pdf-dialog-error" class="hideme">${form.getMessage("error.OperationFailed")}</div>
							</div>
						</c:if>
						
						<c:if test="${mode != 'editcontribution' && dialogmode == null && form.survey.saveAsDraft}">
						
							<c:choose>
								<c:when test="${readonlyMode != null && readonlyMode == true}">
									<input type="button" id="btnSaveDraft" value="${form.getMessage("label.SaveAsDraft")}" class="btn btn-default disabled" disabled="disabled" style="margin-top: 10px" />
								</c:when>									
								<c:otherwise>
									<input type="button" id="btnSaveDraft" value="${form.getMessage("label.SaveAsDraft")}" onclick="saveDraft('${mode}');" class="btn btn-default hidden" style="margin-top: 10px" />
								</c:otherwise>
							</c:choose>						
							
							<c:if test="${form.answerSets.size() > 0}">
	 							<div style="margin-top: 10px">
	 								${form.getMessage("label.LastSavedOn")}<br />
	 								<spring:eval expression="form.answerSets[0].updateDate" />
		 						</div>							
							</c:if>
						</c:if>						
						
						<br /><br />
						<a data-toggle="tooltip" title="${form.getMessage("tooltip.ReportAbuseLink")}" target="_blank" href="${contextpath}/home/reportAbuse?survey=${form.survey.id}" class="link visiblelink">${form.getMessage("label.ReportAbuseLink")}</a>						
					</div>												
				</div>
			</c:if>
			
			<div style="clear: both"></div>
				
			<script type="text/javascript" src="${contextpath}/resources/js/jquery.textarea-expander.js?version=<%@include file="../version.txt" %>"></script>
			
			<c:if test="${form.wcagCompliance && responsive == null}">
 				<script type="text/javascript"> 
					switchCss2();				
				</script> 
			</c:if>
			
		<%@ include file="elementtemplates.jsp" %>	

			
	<script type="text/javascript">
		function getMinMaxCharacters(min,max)
	 	{
	 		var s = '${form.getMessage("limits.MinMaxCharacters", "[min]","[max]")}';
	 		return s.replace("[min]", min).replace("[max]", max);
	 	}
	 	function getMinCharacters(min)
	 	{
	 		var s = '${form.getMessage("limits.MinCharacters", "[min]")}';
	 		return s.replace("[min]", min);
	 	}
	 	function getMaxCharacters(max)
	 	{
	 		var s = '${form.getMessage("limits.MaxCharacters", "[max]")}';
	 		return s.replace("[max]", max);
	 	}	 	
	 	function getMinMaxChoice(min,max)
	 	{
	 		var s = '${form.getMessage("limits.MinMaxChoices", "[min]","[max]")}';
	 		return s.replace("[min]", min).replace("[max]", max);
	 	}
	 	function getMinChoice(min)
	 	{
	 		var s = '${form.getMessage("limits.MinChoices", "[min]")}';
	 		return s.replace("[min]", min);
	 	}
	 	function getMaxChoice(max)
	 	{
	 		var s = '${form.getMessage("limits.MaxChoices", "[max]")}';
	 		return s.replace("[max]", max);
	 	}
	 	function getMaxSelections(max)
	 	{
	 		var s = '${form.getMessage("limits.MaxSelections", "[max]")}';
	 		return s.replace("[max]", max);
	 	}
	 	
	 	function round(value)
	 	{
	 		var d = parseFloat(value);
	 		var s = d.toString();
	 		if (endsWith(s, ".0")) return s.replace(".0","");
	 		return s;
	 	}
	 	
	 	function getMinMax(min,max)
	 	{
	 		var s = '${form.getMessage("limits.MinMaxNumber", "[min]","[max]")}';
	 		return "<div class='limits'>" + s.replace("[min]", round(min)).replace("[max]", round(max)) + "</div>";
	 	}
	 	function getMin(min)
	 	{
	 		var s = '${form.getMessage("limits.MinNumber", "[min]")}';
	 		return "<div class='limits'>" + s.replace("[min]", min) + "</div>";
	 	}
	 	function getMax(max)
	 	{
	 		var s = '${form.getMessage("limits.MaxNumber", "[max]")}';	 		
	 		return "<div class='limits'>" + s.replace("[max]", max) + "</div>";
	 	}
	 	
	 	function getMinMaxDate(min,max)
	 	{
	 		var s = '${form.getMessage("limits.MinMaxDate", "[min]","[max]")}';
	 		return "<div class='limits'>" + s.replace("[min]", min).replace("[max]", max) + "</div>";
	 	}
	 	function getMinDate(min)
	 	{
	 		var s = '${form.getMessage("limits.MinDate", "[min]")}';
	 		return "<div class='limits'>" + s.replace("[min]", min) + "</div>";
	 	}
	 	function getMaxDate(max)
	 	{
	 		var s = '${form.getMessage("limits.MaxDate", "[max]")}';
	 		return "<div class='limits'>" + s.replace("[max]", max) + "</div>";
	 	}
	 	
	 	function getMinMaxRows(min,max)
	 	{
	 		var s = '${form.getMessage("limits.MinMaxRows", "[min]","[max]")}';
	 		return "<div class='limits'>" + s.replace("[min]", min).replace("[max]", max) + "</div>";
	 	}
	 	function getMinRows(min)
	 	{
	 		var s = '${form.getMessage("limits.MinRows", "[min]")}';
	 		return "<div class='limits'>" + s.replace("[min]", min) + "</div>";
	 	}
	 	function getMaxRows(max)
	 	{
	 		var s = '${form.getMessage("limits.MaxRows", "[max]")}';	 		
	 		return "<div class='limits'>" + s.replace("[max]", max) + "</div>";
	 	}
	 	
	 	var idsforuids = null;
	 	function initializeTriggers()
	 	{
	 		triggers = {};
	 		idsforuids = {};
	 		<c:forEach var="element" items="${form.survey.getElementsRecursive(true)}">
	 			triggers["${element.uniqueId}"] = "${element.triggers}";
	 			idsforuids["${element.uniqueId}"] = "${element.id}";
	 		</c:forEach>
	 	}
	 	
	 	function initializeAnswerData()
	 	{
	 		values = {};
	 		pavalues = {};
	 		pavaluesid = {};
	 		tablevalues = {};
	 		filevalues = {};
	 		validationMessages = {};
	 		
	 		<c:if test="${form.answerSets.size() >0}">
	 		
		 		<c:forEach items="${form.answerSets[0].answers}" var="answer" varStatus="rowCounter">	
		 		
		 			<c:choose>
		 				<c:when test="${form.passwordQuestions.contains(answer.questionUniqueId) && draftid != null}">
			 				if (typeof values["${answer.questionUniqueId}"] != 'undefined')
				 			{
				 				values["${answer.questionUniqueId}"] += "********";
				 			} else {
				 				values["${answer.questionUniqueId}"] = "********";
				 			}
		 				</c:when>
		 				<c:otherwise>
			 				if (typeof values["${answer.questionUniqueId}"] != 'undefined')
				 			{
				 				values["${answer.questionUniqueId}"] += "${answer.valueEscaped}";
				 			} else {
				 				values["${answer.questionUniqueId}"] = "${answer.valueEscaped}";
				 			}
			 				
			 				tablevalues["${answer.questionUniqueId}" + "#" + "${answer.row}" + "#" + "${answer.column}"] = "${answer.valueEscaped}"; 			
				 			
							<c:forEach items="${answer.files}" var="file" varStatus="rowCounter2">
								if (typeof filevalues["${answer.questionUniqueId}"] != 'undefined')
					 			{
									filevalues["${answer.questionUniqueId}"].push("${file.name}");
					 			} else {
					 				filevalues["${answer.questionUniqueId}"] = new Array();
					 				filevalues["${answer.questionUniqueId}"].push("${file.name}");
					 			} 
							</c:forEach>
		 				</c:otherwise>		 			
		 			</c:choose>
		 			
		 			if (typeof pavalues["${answer.questionUniqueId}"] != 'undefined')
		 			{
		 				pavalues["${answer.questionUniqueId}"] += "${answer.possibleAnswerUniqueId}";
		 			} else {
		 				pavalues["${answer.questionUniqueId}"] = "${answer.possibleAnswerUniqueId}";
		 			}
		
		 			if (typeof pavaluesid["${answer.questionUniqueId}"] != 'undefined')
		 			{
		 				pavaluesid["${answer.questionUniqueId}"] += "${answer.possibleAnswerId}";
		 			} else {
		 				pavaluesid["${answer.questionUniqueId}"] = "${answer.possibleAnswerId}";
		 			} 
			
				</c:forEach>	
			
			</c:if>
			
			<c:if test="${form.survey.isOPC}">
	 			<c:forEach var="element" items="${form.survey.getElements()}">
		 			<c:if test="${element.getType() == 'FreeTextQuestion'}">
		 				<c:if test='${element.shortname.equalsIgnoreCase("firstName")}'>
			 				if (typeof values["${element.uniqueId}"] == 'undefined')
				 			{
		 						values["${element.uniqueId}"] = "${USER.givenName}";
				 			}
		 				</c:if>
		 				<c:if test='${element.shortname.equalsIgnoreCase("surname")}'>
			 				if (typeof values["${element.uniqueId}"] == 'undefined')
				 			{
		 						values["${element.uniqueId}"] = "${USER.surName}";
				 			}
		 				</c:if>
		 				<c:if test='${element.shortname.equalsIgnoreCase("email")}'>
			 				if (typeof values["${element.uniqueId}"] == 'undefined')
				 			{
	 							values["${element.uniqueId}"] = "${USER.email}";
				 			}
	 					</c:if>
		 			</c:if>
		 		</c:forEach>
	 		</c:if>
 		
			
			<c:forEach items="${form.validationMessageElements}" var="element">
				validationMessages["${element.uniqueId}"] = "${form.getValidationMessage(element)}";
			</c:forEach>
			
			<c:forEach items="${invisibleElements}" var="element">
				invisibleElements["${element}"] = true;
			</c:forEach>
	 	}
	 	
	 	var invisibleElements = [];
	 	function isInvisible(uniqueId)
	 	{
	 		<c:choose>
	 			<c:when test="${invisibleElements == null}">
	 				return true;
	 			</c:when>
	 			<c:otherwise>
	 			return typeof invisibleElements[uniqueId] != 'undefined' ? true : false;
	 			</c:otherwise>
	 		</c:choose>
	 	}
	 	
	 	var validationMessages = null;
	 	function getValidationMessageByQuestion(uniqueId)
	 	{
	 		return typeof validationMessages[uniqueId] != 'undefined' ? validationMessages[uniqueId] : "";
	 	}
	 	
	 	var triggers = null;
	 	function getTriggersByQuestion(uniqueId)
	 	{
	 		return typeof triggers[uniqueId] != 'undefined' ? triggers[uniqueId] : "";
	 	}
	 	
	 	var values = null;
	 	function getValueByQuestion(uniqueId)
	 	{
	 		return typeof values[uniqueId] != 'undefined' ? values[uniqueId] : "";
	 	}
	 	
	 	var pavalues = null;
	 	function getPAByQuestion(uniqueId)
	 	{
	 		return typeof pavalues[uniqueId] != 'undefined' ? pavalues[uniqueId] : "";
	 	}
	 	
	 	var pavaluesid = null;
	 	function getPAIdByQuestion(uniqueId)
	 	{
	 		return typeof pavaluesid[uniqueId] != 'undefined' ? pavaluesid[uniqueId] : "";
	 	}
	 	
	 	function getPAByQuestion2(parentuniqueId, uniqueId, id)
	 	{
	 		if (getPAByQuestion(parentuniqueId).indexOf(uniqueId) > -1)
	 		{
	 			return id.toString();
	 		}
	 		return "";
	 	}
	 	
	 	function getPAByQuestion3(parentuniqueId)
	 	{
	 		if (getPAByQuestion(parentuniqueId).length > 0)
	 		{
	 			var result = getIdForUniqueId(getPAByQuestion(parentuniqueId));
	 			return typeof result != 'undefined' ? result : "";
	 		}
	 		return "";
	 	}
	 	
	 	function getIdForUniqueId(uniqueId)
	 	{
	 		return typeof idsforuids[uniqueId] != 'undefined' ? idsforuids[uniqueId] : "";
	 	}
	 	
	 	var tablevalues = null;
	 	function getTableAnswer(uniqueId, row, col)
	 	{
	 		return typeof tablevalues[uniqueId + "#" + row + "#" + col] != 'undefined' ? tablevalues[uniqueId + "#" + row + "#" + col] : "";
	 	}
	 	
	 	var filevalues = null;
	 	function getFileAnswer(uniqueId)
	 	{
	 		return typeof filevalues[uniqueId] != 'undefined' ? filevalues[uniqueId] : "";
	 	}
	 	
	 	initializeAnswerData();
	 	initializeTriggers();
	</script>
