<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>	
<%@ page import="java.util.Map" %>
<%@ page import="com.ec.survey.model.Form" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>

<script type="text/javascript" src="${contextpath}/resources/js/d3.v3.min.js?version=<%@include file="../version.txt" %>"></script>
<script type="text/javascript" src="${contextpath}/resources/js/d3.layout.cloud.min.js?version=<%@include file="../version.txt" %>"></script>
<script type="text/javascript" src="${contextpath}/resources/js/wordcloud.js?version=<%@include file="../version.txt" %>"></script>

<input type="hidden" id="validatedPerPage" value="${form.survey.validatedPerPage}" />
<input type="hidden" id="preventGoingBack" value="${form.survey.preventGoingBack}" />
<input type="hidden" id="newlang" name="newlang" value="${form.language.code }" />
<input type="hidden" id="newlangpost" name="newlangpost" value="false" />
<input type="hidden" id="newcss" name="newcss" value="" />
<input type="hidden" id="newviewpost" name="newviewpost" value="false" />
<input type="hidden" id="wcagMode" name="wcagMode" value="${form.wcagCompliance}" />
<input type="hidden" id="multipaging" value="${form.survey.multiPaging}" />

<c:if test="${form.survey.isDelphi}">
	<div class="modal" role="dialog" id="delphi-chart-modal" data-backdrop="static">
		<div class="modal-dialog${responsive != null ? "" : " modal-lg"}">
			<div class="modal-content">
				<div class="modal-body">
					<h1><spring:message code="label.Statistics" /></h1>
					<div class="delphi-chart-modal__chart-container"></div>
				</div>
				<div class="modal-footer">
					<a href="javascript:;" class="btn btn-primary" onclick="hideModalDialog($('#delphi-chart-modal'))"><spring:message code="label.Close"/></a>
				</div>
			</div>
		</div>
	</div>
	
	<div id="delphi-hide-survey">
		<div style="font-size: 25px; margin-bottom: 10px"><spring:message code="label.PleaseWait" /></div>
		<img alt="wait animation" src="${contextpath}/resources/images/ajax-loader.gif"/>
	</div>
</c:if>

		<c:choose>
				<c:when test="${publication != null}">
					<div style="width: 220px; max-width: 220px">
						<img alt="${form.survey.logoText}" src="<c:url value="/files/${form.survey.uniqueId}/${form.survey.logo.uid}" />" style="width: 220px" />
					</div>
				</c:when>
				<c:when test="${form.survey.logo != null && !form.survey.logoInInfo}">
					<div style="max-width: 900px">
						<img src="<c:url value="/files/${form.survey.uniqueId}/${form.survey.logo.uid}" />" alt="${form.survey.logoText}" style="max-width: 1300px;" />
					</div>
				</c:when>
				<c:when test="${form.survey.logo != null && responsive != null}">
					<div style="max-width: 100%">
						<img src="<c:url value="/files/${form.survey.uniqueId}/${form.survey.logo.uid}" />" alt="${form.survey.logoText}" style="max-width: 100%;" />
					</div>
				</c:when>
			</c:choose>

				<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
				<div class="left-area">
					<c:if test="${form.survey.motivationPopup}">
						<div class="modal motivation-popup-modal not-shown" id="motivationPopup" style="padding-top: 50px;" role="dialog" data-popup="${form.survey.motivationPopup}" data-type="${form.survey.motivationType}" data-progress="${form.survey.motivationTriggerProgress}" data-timer="${form.survey.motivationTriggerProgress}">
							<div class="modal-dialog modal-sm">
								<div class="modal-content">
									<div class="modal-header" style="font-weight: bold;">
										<c:choose>
											<c:when test="${form.survey.motivationPopupTitle != null && form.survey.motivationPopupTitle.length() > 0}">
												${fn:escapeXml(form.survey.motivationPopupTitle)}
											</c:when>
											<c:otherwise>
												<spring:message code="label.MotivationPopup" />
											</c:otherwise>
										</c:choose>
									</div>
									<div class="modal-body">
										<div class="modal-body" style="word-break: break-all">${form.survey.motivationText}</div>
									</div>
									<div class="modal-footer">
										<a href="javascript:;" class="btn btn-primary" onclick="hideModalDialog('.motivation-popup-modal')">${form.getMessage("label.Close")}</a>
									</div>
								</div>
							</div>
						</div>
					</c:if>

					<c:if test="${form.survey.isEVote}">
						<div class="evote-voter-overview" id="evoteVoterOverview" style="display: none">
							<div>
								<span style="float: left; line-height: 32px">
									<c:choose>
										<c:when	test="${form.survey.geteVoteTemplate() == 'l' || form.survey.geteVoteTemplate() == 'o'}">
											<span>${form.getMessage("label.Votes")}:</span>
										</c:when>
										<c:otherwise>
											<span>${form.getMessage("label.VotedCandidates")}:</span>
										</c:otherwise>
									</c:choose>
										<span id="overviewVotes">
											<span id="votedCandidates"></span>
											/
											<span id="allCandidates">${form.survey.maxPrefVotes}</span>
										</span>

										<c:if test="${form.survey.geteVoteTemplate() != 'l'}">
											<span id="votedListsWrapper" style="display: none">
												<span style="margin-left: 24px;">${form.getMessage("label.VotedLists")}:</span>
												<span id="votedLists"></span>
											</span>
										</c:if>
								</span>
								<span style="color: black">
									<a style="margin-left: 24px; float: right" href="javascript:;" class="btn btn-default" onclick="clearEVoteVotes()">${form.getMessage("label.ClearVotes")}</a>
								</span>
							</div>
						</div>
						<div class="evote-overview-placeholder"></div>
						<div class="modal evote-confirm-modal not-shown" id="evoteConfirmPopup" style="padding-top: 50px; z-index: 10500;" role="dialog">
							<div class="modal-dialog">
								<div class="modal-content">
									<div class="modal-body">
										<div class="container-fluid">
											<div class="row align-items-center">
												<div class="col-sm-4">
													<img alt="cast ballot icon" src="${contextpath}/resources/images/castballoticon.png"/>
												</div>
												<div class="col-sm-8" style="height: 198px; line-height: 20px; padding-top: 85px; font-size: 16px !important;">
													${form.getMessage("label.eVoteConfirmSubmit")}
												</div>
											</div>
										</div>
									</div>
									<div class="modal-footer">
										<a class="btn btn-primary" style="tabindex: 0" onclick="eVoteConfirmResolve(true)">${form.getMessage("label.Imsure")}</a>
										<a class="btn btn-default" style="tabindex: 0" onclick="hideModalDialog('.evote-confirm-modal'); eVoteConfirmResolve(false);">${form.getMessage("label.Cancel")}</a>
									</div>
								</div>
							</div>
						</div>
					</c:if>

					<c:if test="${form.survey.progressBar}">
						<div id="progressBarContainer" class="progressBar" style="display: none">							
							<div class="progress">
							  <div id="progressBar" class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">
							  	 <span id="progressBarLabel">
							  	 <c:if test="${form.survey.progressDisplay != 1}">
									    	<span id="progressBarPercentage"></span>
									  </c:if>
									  <c:if test="${form.survey.progressDisplay == 2}"> (</c:if>
									  <c:if test="${form.survey.progressDisplay != 0}"><span id="progressBarRatio"></span></c:if>
									  <c:if test="${form.survey.progressDisplay == 2}">)</c:if> 
								</span> 
							  </div>						      
							</div>
						</div>
						<div class="progressBarPlaceholder"></div>
					</c:if>
				
					<c:if test="${!(form.survey.isDelphi) && !(form.survey.isEVote)}">
						<div id="nolocalstorage" class="hideme" style="margin-bottom: 10px; text-align: right; margin-right: 10px;">
							<span class="alert-danger" style="padding: 10px;">${form.getMessage("info.LocalStorageDisabled")}</span>
						</div>

						<c:choose>
							<c:when test="${mode == 'editcontribution' }">
								<input style="display: none" class="check" type="checkbox" id="saveLocalBackup" onchange="checkLocalBackup()" />
							</c:when>
							<c:otherwise>
								<div id="localstorageinfo" class="visible-lg" style="margin-bottom: 10px; text-align: right; margin-right: 10px;">
									<span class="focusborder">
										<input class="check" type="checkbox" checked="checked" id="saveLocalBackup" onchange="checkLocalBackup()" />
										<label for="saveLocalBackup">${form.getMessage("info.DeactivateLocalStorage")}</label>
									</span>
								</div>
							</c:otherwise>
						</c:choose>
					</c:if>

					<h1 class="surveytitle">${form.survey.title}</h1><br />

					<c:if test="${form.survey.containsMandatoryQuestion()}">
						<div class="info-box" style="width: 400px; max-width: 100%;">
							<div style="float: right; margin-top: -5px; margin-right: -5px;">
								<button type="button" class="unstyledbutton" onclick="$(this).closest('.info-box').hide();" aria-label="${form.getMessage("label.CloseInfoMessage")}"><span class="glyphicon glyphicon-remove"></span></button>
							</div>		
						
							${form.getMessage("message.StarMandatory")}
						</div>
					</c:if>
					<c:if test="${(form.answerSets.size() == 0 || !form.answerSets[0].disclaimerMinimized)}">
						<c:if test="${!oss && !form.survey.isEVote}">
							<c:if test="${(form.survey.owner.type == 'ECAS' && form.survey.owner.getGlobalPrivilegeValue('ECAccess') == 0) || form.survey.owner.type == 'SYSTEM'  }">
								<div id="ecDisclaimer" class="surveyrunnerinfo">
									<div style="float: left; width: calc(100% - 18px)">
										<b>${form.getMessage("label.Disclaimer")}</b>
										<p>
											${form.getMessage("info.Disclaimer")}
										</p>					
									</div>
									<div style="float: right; margin-top: -15px; margin-right: -15px;">
										<input type="hidden" id="disclaimerMinimized" name="disclaimerMinimized" value="${disclaimerMinimized}" />
										<button type="button" class="unstyledbutton" onclick="$('#disclaimerMinimized').val('true'); $('#ecDisclaimer').hide();" aria-label="${form.getMessage("label.CloseDisclaimer")}"><span class="glyphicon glyphicon-remove"></span></button>
									</div>								
									<div style="clear: both"></div>
								</div>
							</c:if>
						</c:if>	
					</c:if>
					<c:if test='${form.survey.security.equals("openanonymous") || form.survey.security.equals("securedanonymous")}'>
						<div id="anonymousSurveyInfo" class="surveyrunnerinfo focusborder">
							<div tabindex="0" style="float: left; width: calc(100% - 18px)">
								<b>${form.getMessage("label.AnonymousMode")}</b>
								<p>
									<c:choose>
										<c:when test="${form.survey.isEVote}">
											${form.getMessage("info.AnonymousModeEVote")}
										</c:when>
										<c:otherwise>
											${form.getMessage("info.AnonymousMode")}
										</c:otherwise>
									</c:choose>									
								</p>					
							</div>
							<div style="float: right; margin-top: -15px; margin-right: -15px;">
								<button type="button" class="unstyledbutton" onclick="$('#anonymousSurveyInfo').hide();" aria-label="${form.getMessage("label.CloseAnonymousInfo")}"><span class="glyphicon glyphicon-remove"></span></button>
							</div>								
							<div style="clear: both"></div>
						</div>
					</c:if>
					<span class="introduction">${form.survey.introduction}</span>					
					
					<div id="page-tabs" class="panel panel-default${responsive != null ? " visible-lg" : ""}" style="margin-top:20px;">
						<div class="panel-body">
							<div style="font-size: 20px; margin-bottom: 1rem;">${form.getMessage("label.Pages")}</div>
							
							<div>
								<ul class="nav nav-pills">
								<c:forEach var="page" items="${form.getPages()}" varStatus="rowCounter">
									<li data-id="${page[0].id}" id="tab${rowCounter.index}"
										class="pagebutton ${rowCounter.index == 0 ? "active" : ""}"
										style="margin-left: 0; margin-right: 0.3rem; margin-bottom: 0.3rem;"
										data-toggle="${form.survey.isDelphi ? "tooltip" : ""}"
										title="${form.survey.isDelphi ? form.getMessage("label.SwitchPageDelphi") : ""}">
										
										<c:choose>
											<c:when test="${form.survey.preventGoingBack == false}">
												<a href="#page${rowCounter.index}" style="cursor:pointer;" onclick="return selectPage(${rowCounter.index});">
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
											</c:when>
											<c:otherwise>
												<a class="noHover">
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
											</c:otherwise>
										</c:choose>

									</li>
								</c:forEach>
								</ul>
							</div>							
						</div>
					
					</div>	
					<div style="clear: left"></div>
					
					<c:forEach var="page" items="${form.getPages()}" varStatus="rowCounter">
					
					 	<c:choose>
					 		<c:when test="${rowCounter.index == 0}">
								<div class="single-page" tabindex="-1" id="page${rowCounter.index}" onmouseleave="validateLastContainer()">
							</c:when>
							<c:otherwise>
								<div class="single-page" tabindex="-1" id="page${rowCounter.index}" style="display: none" onmouseleave="validateLastContainer()">
							</c:otherwise>
						</c:choose>						
							
							<c:forEach var="element" items="${page}">
								<c:if test="${publication == null || publication.isAllQuestions() || publication.isSelected(element.id)}">
									<fieldset>
										<legend>${element.type}</legend>
										<c:choose>
										<c:when test="${form.survey.isDelphi && element.isDelphiElement()}">
										<div class="elementwrapper delphi">
											</c:when>
											<c:when test="${element.hasPDFWidth}">
											<div class="elementwrapper elem_${element.id}">
												</c:when>
												<c:otherwise>
												<div class="elementwrapper">
													</c:otherwise>
													</c:choose>
													<c:choose>
													<c:when test="${element.isDummy() && element.isDependent && ((invisibleElements == null && forpdf == null) || invisibleElements.contains(element.uniqueId))}">
													<div class="emptyelement survey-element untriggered dependent 1" data-useAndLogic="${element.useAndLogic}"
														 data-id="${element.id}" data-uid="${element.uniqueId}"
														 data-triggers="${element.triggers}"
														 style="margin-top: 5px; display: none;">
														</c:when>
														<c:when test="${element.getType() == 'Matrix' && element.getAllQuestionsDependent() && ((invisibleElements == null && forpdf == null) || invisibleElements.contains(element.uniqueId))}">
														<div class="emptyelement survey-element untriggered 2" data-useAndLogic="${element.useAndLogic}"
															 id="${element.id}" data-id="${element.id}"
															 data-uid="${element.uniqueId}"
															 data-triggers="${element.triggers}" style="display: none;">
															</c:when>
															<c:when test="${element.isDependent && ((invisibleElements == null && forpdf == null) || invisibleElements.contains(element.uniqueId))}">
															<div class="emptyelement survey-element untriggered dependent 3"  data-useAndLogic="${element.useAndLogic}"
																 id="${element.id}" data-id="${element.id}"
																 data-triggers="${element.triggers}"
																 data-uid="${element.uniqueId}"
																 style="display: none;">
																</c:when>
																<c:when test="${element.isDependent}">
																<div class="emptyelement survey-element dependent 3b"  data-useAndLogic="${element.useAndLogic}"
																	 id="${element.id}"
																	 data-id="${element.id}"
																	 data-uid="${element.uniqueId}"
																	 data-triggers="${element.triggers}">
																	</c:when>
																	<c:when test="${element.isDummy()}">
																	<div class="emptyelement survey-element 4"
																		 data-id="${element.id}"
																		 data-uid="${element.uniqueId}"
																		 style="margin-top: 5px;">
																		</c:when>
																		<c:otherwise>
																		<div class="emptyelement survey-element 5"
																			 id="${element.id}"
																			 data-id="${element.id}"
																			 data-uid="${element.uniqueId}">
																			</c:otherwise>
																			</c:choose>
																			<a class="survey-element-anchor"
																			   id="E${element.id}"></a>
																			<img alt="wait animation" src="${contextpath}/resources/images/ajax-loader.gif"/>
																		</div>
																	</div>
									</fieldset>
								</c:if>
							</c:forEach>
								</div>

									</c:forEach>

									<div class="hpdiv">
										<label for="hp-7fk9s82jShfgak">${form.getMessage("info.leaveempty")}</label>
										<textarea tabindex="-1" id="hp-7fk9s82jShfgak" name="hp-7fk9s82jShfgak" class="hp"
												  autocomplete="false"></textarea>
									</div>

									<c:if test="${form.survey.captcha}">
										<%@ include file="../captcha.jsp" %>
									</c:if>

									<c:if test="${submit == true}">
										<div style="text-align: center; margin-top: 20px;">
											
											<c:if test="${form.survey.preventGoingBack == false}">											
												<button type="button" id="btnPrevious" style="display: none;" role="button" aria-label="${form.getMessage("label.GoToPreviousPage")}"
													   data-toggle="${form.survey.isDelphi ? "tooltip" : ""}"
													   title="${form.survey.isDelphi ? form.getMessage("label.PreviousPageDelphi") : ""}"
													   onclick="previousPage();this.blur();" onfocusin="validateLastContainer()" class="btn btn-default">${form.getMessage("label.Previous")}</button>
											</c:if>
											<c:choose>
												<c:when test="${dialogmode != null }">
													<button type="button" id="btnSubmit" role="button"
														   onclick="validateInputAndSubmitRunner($('#runnerForm'));"
														   onfocusin="validateLastContainer()"
														   class="btn btn-primary">${form.getMessage("label.Save")}</button>
													<button type="button" id="btnSubmit2" role="button"
														   onclick="window.open('', '_self', '');window.close();"
														   class="btn btn-default">${form.getMessage("label.Close")}</button>
												</c:when>
												<c:otherwise>
													<button type="button" role="button" id="btnSubmit"
														   onclick="validateInputAndSubmitRunner($('#runnerForm'));"
														   onfocusin="validateLastContainer()"
														   class="btn btn-primary hidden">${form.getMessage("label.Submit")}</button>
												</c:otherwise>
											</c:choose>
											<button type="button" id="btnNext" style="display: none;" role="button" aria-label="${form.getMessage("label.GoToNextPage")}"
											   data-toggle="${form.survey.isDelphi ? "tooltip" : ""}"
											   title="${form.survey.isDelphi ? form.getMessage("label.NextPageDelphi") : ""}"
											   onclick="nextPage(); this.blur();"
											   onfocusin="validateLastContainer()"
											   class="btn btn-default btn-primary">${form.getMessage("label.Next")}</button>

											<c:if test="${responsive != null && mode != 'editcontribution' && dialogmode == null && form.survey.saveAsDraft}">
												<input type="button" id="btnSaveDraftMobile"
													   value="${form.getMessage("label.SaveAsDraft")}"
													   onclick="saveDraft('${mode}');" class="btn btn-default hidden"
													   style="margin-left: 10px"/>
												<c:if test="${form.answerSets.size() > 0}">
													<div style="margin-top: 20px">
															${form.getMessage("label.LastSavedOn")}<br/>
														<spring:eval expression="form.answerSets[0].updateDate"/>
													</div>
												</c:if>
											</c:if>
										</div>
									</c:if>

								</div>

						<c:if test="${publication == null && responsive == null}">
						<div class="right-area" style="z-index: 1; position: relative">

							<c:if test="${form.survey.logo != null && form.survey.logoInInfo}">
								<img style="max-width: 100%; margin-top: 10px;"
									 src="<c:url value="/files/${form.survey.uniqueId}/${form.survey.logo.uid}" />"
									 alt="${form.survey.logoText}"/>
								<hr style="margin-top: 15px;"/>
							</c:if>
							
							<c:if test="${mode != 'editcontribution' && form.survey.timeLimit.length() > 0 && form.survey.showCountdown}">
								<div class="linkstitle">
									${form.getMessage("label.CountdownTimer")}
								</div>
								<div style="font-size: 20px;" id="countdowntimer">${form.survey.timeLimit}</div>
								<hr style="margin-top: 15px;"/>								
							</c:if>

							<c:if test='${runnermode == null  || form.survey.skin == null || !form.survey.skin.name.equals("New Official EC Skin")}'>

							<c:if test='${!form.survey.skin.name.equals("New Official EC Skin") && mode != "editcontribution"}'>

								<div class="linkstitle"
									 style="margin-bottom: 5px;">${form.getMessage("label.Views")}</div>

								<c:choose>
									<c:when test="${readonlyMode != null && readonlyMode == true}">
										<div id="normalcss" style="color: #ccc">
												${form.getMessage("label.Standard")}&#160;
											<button type="button" class="unstyledbutton link visiblelink css-switch disabled" id="css-switch-disabled"
											   style="color: #ccc">${form.getMessage("label.AccessibilityMode")}</button>
										</div>

										<div id="enhancedcss" class="hideme" style="color: #ccc">
											<button type="button" class="unstyledbutton link css-switch normal" id="css-switch-normal"
											   style="color: #ccc">${form.getMessage("label.Standard")}</button>&#160;
												${form.getMessage("label.AccessibilityMode")}
										</div>
									</c:when>
									<c:otherwise>
										<div id="normalcss">
												${form.getMessage("label.Standard")}&#160;
											<button type="button" class="unstyledbutton link visiblelink css-switch disabled" id="css-switch-disabled"
											   onclick="switchCss('${mode}','wcag');">${form.getMessage("label.AccessibilityMode")}</button>
										</div>

										<div id="enhancedcss" class="hideme">
											<button type="button" class="unstyledbutton link css-switch normal" id="css-switch-normal"
											   onclick="switchCss('${mode}','standard');">${form.getMessage("label.Standard")}</button>&#160;
												${form.getMessage("label.AccessibilityMode")}
										</div>
									</c:otherwise>
								</c:choose>

								<hr style="margin-top: 15px;"/>

							</c:if>

							<c:if test='${form.getLanguages().size() != 0 && mode != "editcontribution"}'>
								<span id="runnerLanguageSelector">
									<label for="langSelectorRunner">
										<div class="linkstitle"
											 style="margin-bottom: 5px;">${form.getMessage("label.Languages")}</div>
									</label>

									<c:choose>
										<c:when test="${readonlyMode != null && readonlyMode == true}">
											<select id="langSelectorRunner" name="langSelectorRunner" disabled="disabled">
										</c:when>
										<c:otherwise>
										<select id="langSelectorRunner" name="langSelectorRunner"
												oninput="changeLanguageSelectOption('${mode}')">
											</c:otherwise>
									</c:choose>

									<c:forEach var="lang" items="${form.getLanguagesAlphabetical()}">
										<c:choose>
											<c:when test="${lang.value.code == form.language.code}">
												<option value="<esapi:encodeForHTML>${lang.value.code}</esapi:encodeForHTML>" selected="selected"><esapi:encodeForHTML>${lang.value.name}</esapi:encodeForHTML></option>
											</c:when>
											<c:otherwise>
												<option value="<esapi:encodeForHTML>${lang.value.code}</esapi:encodeForHTML>"><esapi:encodeForHTML>${lang.value.name}</esapi:encodeForHTML></option>
											</c:otherwise>
										</c:choose>
									</c:forEach>
									</select>
									<hr style="margin-top: 15px;" />
								</span>
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
									<a target="_blank" aria-label="${form.getMessage("label.ContactForm")} - ${form.getMessage("label.OpensInNewWindow")}" class="link visibleLink" data-toggle="tooltip" title="${form.getMessage("info.ContactForm")}" href="${contextpath}/runner/contactform/${form.survey.shortname}">${form.getMessage("label.ContactForm")}</a>
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
						
						<c:if test="${form.survey.isDelphi}">
							<div class="contact-and-pdf__delphi-section">
								<div class="linkstitle" style="margin-bottom: 5px;">${form.getMessage("label.Info")}</div>
								<a target="_blank" aria-label="${form.getMessage("label.Delphi")} - ${form.getMessage("label.OpensInNewWindow")}" class="link visibleLink" data-toggle="tooltip" title="${form.getMessage("label.Delphi")}" href="${contextpath}/home/delphi?survey=${form.survey.shortname}">
									${form.getMessage("label.Delphi")}
								</a>
								<c:if test="${form.answerSets.size() > 0}">
									<br /><br />
									<a id="editYourContributionLink" href="javascript:;" onclick="showContributionLinkDialog(this)">${form.getMessage("label.EditYourContributionLater")}</a>
								</c:if>
							</div>
							<hr style="margin-top: 15px;" />
						</c:if>
						
						<c:if test="${!form.survey.isQuiz && form.survey.allowQuestionnaireDownload}">
							<div>
								<a data-toggle="tooltip" title="${form.getMessage("label.DownloadEmptyPDFversion")}" aria-label="${form.getMessage("label.DownloadPDFversion")}" id="download-survey-pdf-link" class="link visiblelink" href="#" onclick="downloadSurveyPDF('${form.survey.id}','${form.language.code}','${uniqueCode}')">${form.getMessage("label.DownloadPDFversion")}</a>
								<span id="download-survey-pdf-dialog-running" class="hideme">${form.getMessage("info.FileCreation")}</span>
								<span id="download-survey-pdf-dialog-ready" class="hideme">${form.getMessage("info.FileCreated")}</span>
								<div id="download-survey-pdf-dialog-spinner" class="hideme" style="padding-left: 5px;"><img alt="wait animation" src="${contextpath}/resources/images/ajax-loader.gif" /></div>
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
						<a data-toggle="tooltip" aria-label="${form.getMessage("label.ReportAbuseLink")} - ${form.getMessage("label.OpensInNewWindow")}" title="${form.getMessage("tooltip.ReportAbuseLink")}" target="_blank" href="${contextpath}/home/reportAbuse?survey=${form.survey.id}" class="link visiblelink">${form.getMessage("label.ReportAbuseLink")}</a>
					</div>												
				</div>
			</c:if>
			
			<div style="clear: both"></div>

			<div class="modal confirm-explanation-deletion-modal" role="dialog" data-backdrop="static">
				<div class="modal-dialog modal-sm">
					<div class="modal-content">
						<div class="modal-body">
							<spring:message code="info.ConfirmExplanationDeletion" />
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default" onclick="confirmExplanationDeletion()"><spring:message code="label.Confirm" /></button>
							<button type="button" class="btn btn-primary" onclick="hideModalDialog('.confirm-explanation-deletion-modal')"><spring:message code="label.Cancel" /></button>
						</div>
					</div>
				</div>
			</div>
			
			<div class="modal" id="quizTimeoutDialog" data-backdrop="static" role="dialog">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-body">
							<spring:message code="info.CountdownExceeded" />
						</div>
						<div class="modal-footer">
							<a tabindex="0" class="btn btn-default" data-dismiss="modal"><spring:message code="label.Close" /></a>
						</div>
					</div>
				</div>
			</div>
				
			<script type="text/javascript" src="${contextpath}/resources/js/jquery.textarea-expander.js?version=<%@include file="../version.txt" %>"></script>
			
			<c:if test="${form.wcagCompliance && responsive == null}">
 				<script type="text/javascript"> 
					switchCss2();				
				</script> 
			</c:if>

		<%@ include file="contributionLinkModals.jsp" %>
		<%@ include file="elementtemplates.jsp" %>
			
	<script type="text/javascript">
		function getCharacterCountInfo(max)
		{
			var s = '${form.getMessage("info.CharactersUsed", "[current]", "[max]")}';
			return s.replace("[max]", max).replace("[current]", "<span class='charactercounter'>0</span>");
		}
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
	 		var s = '${form.getMessage("limits.MinMaxChoicesNew", "[min]","[max]")}';
	 		return s.replace("[min]", min).replace("[max]", max);
	 	}
	 	function getMinChoice(min)
	 	{
	 		var s = '${form.getMessage("limits.MinChoicesNew", "[min]")}';
	 		return s.replace("[min]", min);
	 	}
	 	function getMaxChoice(max)
	 	{
	 		var s = '${form.getMessage("limits.MaxChoicesNew", "[max]")}';
	 		return s.replace("[max]", max);
	 	}
	 	function getMaxSelections(max)
	 	{
	 		var s = '${form.getMessage("limits.MaxSelections", "[max]")}';
	 		return s.replace("[max]", max);
	 	}

		function getRankingQuestionInfo(max)
		{
			var s = '${form.getMessage("label.RankingListInitialState", "[max]")}';
			return s.replace("[max]", max);
		}
		function getInitialOrderInfoText()
		{
			return "${form.getMessage("label.InitialOrder")}";
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
	 		
	 		<c:if test="${form.answerSets.size() > 0}">
	 		
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
	 		
	 		if ($("#survey\\.id").length > 0) {
	 			initializeBackupHelper();
	 			restoreBackup();
	 		}			
			
			<c:forEach items="${form.validationMessageElements}" var="element">
				<c:choose>
					<c:when test="${element.type == 'ComplexTableItem'}">
						validationMessages["${element.id}"] = "${form.getValidationMessage(element)}";
					</c:when>
					<c:otherwise>
						validationMessages["${element.uniqueId}"] = "${form.getValidationMessage(element)}";
					</c:otherwise>
				</c:choose>
			</c:forEach>
			
			<c:forEach items="${invisibleElements}" var="element">
				invisibleElements["${element}"] = true;
			</c:forEach>
	 	}

	 	function initializeBackupHelper(){
	 		backupHelper = {}
			<c:forEach var="element" items="${form.survey.getElementsRecursive(true)}">
				backupHelper["${element.id}"] = {
					uid : "${element.uniqueId}",
					type : "${element.type}"
				}
			</c:forEach>
		}
	 	
	 	function doAnswersExist() {
	 		<c:choose>
		 		<c:when test="${form.answerSets.size() == 0}">
		 			return false;
		 		</c:when>
		 		<c:otherwise>
		 			return true;
		 		</c:otherwise>
		 	</c:choose>	 		
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
	 	function getValueByQuestion(uniqueId, readValueOnce, cellEl)
	 	{
	 		if (typeof values[uniqueId] != 'undefined' && values[uniqueId] != null && values[uniqueId] != "") {
	 			if (cellEl != null && $(cellEl).is(".complex")){
					$(cellEl).closest(".innercell").addClass("answered");
				} else {
					$('.survey-element[data-uid="' + uniqueId + '"]').addClass("answered");
					$('tr[data-uid="' + uniqueId + '"]').closest(".survey-element").addClass("answered");
	 			}
				if (readValueOnce) {
					var ret = values[uniqueId];
					values[uniqueId] = null
					return ret;
				}
 				return values[uniqueId];
	 		}
	 		return "";
	 	}

		var valuesread = {};
		function getValueByQuestionRating(uniqueId, answerUniqueId, cellEl)
		{
			if (typeof values[uniqueId] != 'undefined' && typeof valuesread[answerUniqueId] == 'undefined') {
				if (cellEl != null && $(cellEl).is(".complex")){
					$(cellEl).closest(".innercell").addClass("answered");
				} else {
					$('.survey-element[data-uid="' + uniqueId + '"]').addClass("answered");
					$('tr[data-uid="' + uniqueId + '"]').closest(".survey-element").addClass("answered");
					valuesread[answerUniqueId] += true
				}
				return values[uniqueId];
			}
			return "";
		}

		function getValueByQuestionGallery(uniqueId)
		{
			if (typeof values[uniqueId] != 'undefined') {
				return values[uniqueId];
			}
			return "";
		}

		var pavalues = null;
	 	var pavaluesread = {};
	 	function getPAByQuestion(uniqueId, cellEl)
	 	{
	 		if (typeof pavalues[uniqueId] != 'undefined')
	 		{
	 			if (cellEl != null && $(cellEl).is(".complex")){
					$(cellEl).closest(".innercell").addClass("answered");
				} else {
					$('.survey-element[data-uid="' + uniqueId + '"]').addClass("answered");
				}
	 			return pavalues[uniqueId];
	 		}
	 		return "";
	 	}

		var pavaluesread = {};
	 	var pavaluesread2 = {};
		function getPAByQuestionCheckBox(uniqueId, answerUniqueId, cellEl)
		{
			if (typeof pavalues[uniqueId] != 'undefined' && typeof pavaluesread2[answerUniqueId] == 'undefined')
			{
				if (cellEl != null && $(cellEl).is(".complex")){
					$(cellEl).closest(".innercell").addClass("answered");
				} else {
					$('.survey-element[data-uid="' + uniqueId + '"]').addClass("answered");
				}
				if (typeof pavaluesread[answerUniqueId] != 'undefined') {
					pavaluesread2[answerUniqueId] += true;
				} else {
					pavaluesread[answerUniqueId] += true;
				}
				return pavalues[uniqueId];
			}
			return "";
		}
	 	
	 	var pavaluesid = null;
	 	function getPAIdByQuestion(uniqueId)
	 	{
	 		return typeof pavaluesid[uniqueId] != 'undefined' ? pavaluesid[uniqueId] : "";
	 	}
	 	
	 	function getPAByQuestion2(parentuniqueId, uniqueId, id, cellEl)
	 	{
	 		if (getPAByQuestion(parentuniqueId).indexOf(uniqueId) > -1)
	 		{
	 			if (cellEl != null && $(cellEl).is(".complex, .multiple-choice li input")) {
					$(cellEl).closest(".innercell").addClass("answered");
				} else {
					$('tr[data-uid="' + parentuniqueId + '"]').closest(".survey-element").addClass("answered");
				}

	 			return id.toString();
	 		}
	 		return "";
	 	}

		var pavaluesread3 = {};
	 	function getPAByQuestion3(parentuniqueId, cellEl)
	 	{
			let paValue = (typeof pavalues[parentuniqueId] != "undefined") ? pavalues[parentuniqueId] : "";
	 		if (paValue.length > 0 && pavaluesread3[parentuniqueId] == undefined)
	 		{
	 			var result = getIdForUniqueId(paValue);
				pavaluesread3[parentuniqueId] = true;

	 			if (typeof result != 'undefined' && result != "")
 				{
 					if (cellEl != null && $(cellEl).is("select.complex")){
						$(cellEl).closest(".innercell").addClass("answered");
					} else {
						$('.survey-element[data-uid="' + parentuniqueId + '"]').addClass("answered");
					}
	 				return result;
 				}
	 		}
	 		return "";
	 	}
	 	
	 	function getTargetDatasetByQuestion(uniqueId) {
	 		return values[uniqueId];
	 	}
	 	
	 	function getIdForUniqueId(uniqueId)
	 	{
	 		return typeof idsforuids[uniqueId] != 'undefined' ? idsforuids[uniqueId] : "";
	 	}
	 	
	 	var tablevalues = null;
	 	function getTableAnswer(uniqueId, row, col, readOnce)
	 	{
	 		if (tablevalues[uniqueId + "#" + row + "#" + col] != undefined && tablevalues[uniqueId + "#" + row + "#" + col] != "")
	 		{
	 			$('.survey-element[data-uid="' + uniqueId + '"]').addClass("answered");
	 			if (readOnce) {
	 				var res = tablevalues[uniqueId + "#" + row + "#" + col];
					tablevalues[uniqueId + "#" + row + "#" + col] = null;
					return res;
				}
	 			return tablevalues[uniqueId + "#" + row + "#" + col];
	 		}
	 		
	 		return "";
	 	}
	 	
	 	var filevalues = null;
	 	var filesread = {};
		var filesread2 = {};
	 	function getFileAnswer(uniqueId)
	 	{
	 		const result = filevalues[uniqueId];
	 		if (typeof result != 'undefined' && typeof filesread2[uniqueId] == 'undefined') {
	 			$('.survey-element[data-uid="' + uniqueId + '"]').addClass("answered");
	 			if (typeof filesread[uniqueId] != 'undefined') {
					filesread2[uniqueId] += true;
				} else {
	 				filesread[uniqueId] += true;
				}
	 			return result;
	 		}
	 		
	 		return "";
	 	}

		function deleteDelphiCommentFromRunner(button, isReply) {
			const dialog = $(button).closest(".survey-element").children("div").eq(1).find(".delete-confirmation-dialog");
			showModalDialog(dialog, button);

			var deleteButton = $(dialog).find(".delete-confirmation-dialog__confirmation-button");
			$(deleteButton).off("click");
			$(deleteButton).click(function() {
				const questionUid = $(button).closest(".survey-element").attr("data-uid");
				const viewModel = modelsForDelphiQuestions[questionUid];

				const errorCallback = () => { showError("error"); }
				const successCallback = () => {
					loadTableData(questionUid, viewModel);
				}

				hideModalDialog(dialog);
				deleteDelphiComment(button, viewModel, isReply, errorCallback, successCallback);
			});
		}
		
		var backupLoaded = false;

	 	initializeAnswerData();
	 	initializeTriggers();

		<c:if test="${form.survey.timeLimit.length() > 0 || (form.survey.motivationPopup && form.survey.motivationType)}">

			var passedSeconds = ${form.getPassedTimeInSeconds()};
			var startDateJS = new Date();

			function updateCountdownCombined() {

				var qtimedone = false;
				var mtimedone = false;
				var currentTime = new Date();


				// Quiztimer

				<c:if test="${mode != 'editcontribution' && form.survey.timeLimit.length() > 0}">
					var countdownTimerSeconds = ${form.survey.timeLimitInSeconds};
					var rest = countdownTimerSeconds - passedSeconds - Math.floor((currentTime - startDateJS) / 1000);

					if (rest < 1) {
						//timeout
						qtimedone = true;
						$('#countdowntimer').html("00:00:00")
						$('#btnSubmit').remove();
						$('.single-page').remove();
						$('#quizTimeoutDialog').modal("show");
						return;
					} else if (rest < 61) {
						//red color
						$('#countdowntimer').addClass("lightred");
					}

					//update
					var hours = Math.floor(rest / 3600);
					rest = rest - (hours * 3600);
					var minutes = Math.floor(rest / 60);
					rest = rest - (minutes * 60);

					var minuteSeparator = ":";
					if (minutes < 10) {
						minuteSeparator = ":0";
					}

					var secondSeparator = ":";
					if (rest < 10) {
						secondSeparator = ":0";
					}

					$('#countdowntimer').html(hours + minuteSeparator + minutes + secondSeparator + rest);
				</c:if>



				// Motivationtimer
				if (${form.survey.motivationPopup} && ${form.survey.motivationType}) {
					if($("#motivationPopup").hasClass("not-shown")){
						var countdownTimerSeconds = ${form.survey.motivationTriggerTime} * 60;
						var rest = countdownTimerSeconds - passedSeconds - Math.floor((currentTime - startDateJS) / 1000);

						if (rest < 1) { // Timer has run down
							mtimedone = true;
							showPopup();
						}
					}
				}

				// rerun after 1 second in case not both timers ran out
				if(!mtimedone || !qtimedone){
					window.setTimeout(function() {
						updateCountdownCombined();
					}, 1000);
				}
			}

			updateCountdownCombined();

		</c:if>


		// showPopup
		function showPopup() {
			$("#motivationPopup").modal('show');
			$("#motivationPopup").removeClass('not-shown');
		}
	</script>
