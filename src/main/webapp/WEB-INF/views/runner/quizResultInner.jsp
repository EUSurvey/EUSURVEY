<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
	
	<link href="${contextpath}/resources/css/yellowfocus.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css"></link>
		
	<c:choose>
		<c:when test="${forpdf == null && responsive == null}">
			<div class="fullpageform">
		</c:when>
		<c:when test="${responsive != null}">
			<div style="padding-top: 40px;">
		</c:when>
		<c:otherwise>
			<div>
		</c:otherwise>
	</c:choose>
	
		<div class="quizresultsdiv">
			<img src="${contextpath}/resources/images/logo_Eusurvey.png" style="width: 200px" /><br />
		
			<h1 class="surveytitle">${form.survey.title} - ${form.getMessage("label.Results")} </h1><br />
			<div style="margin-bottom: 20px;">${form.survey.quizResultsMessage}</div>
		
			<c:if test="${forpdf == null && form.survey.downloadContribution}">
				<div style="text-align: center; margin-bottom: 20px;">
					<a href="javascript:;" id="pdfDownloadButtonThanksInner" onclick="showExportDialogAndFocusEmail(this)" class="btn btn-default">${form.getMessage("label.GetPDF")}</a>		
				</div>
			</c:if>
			
			<c:if test="${form.survey.showTotalScore}">
		
				<div style="font-size: 25px;">${form.getMessage("label.Summary")}:</div>

				<div style="float: right; width: 130px; height: 130px; margin-top: 0px;">
					
				</div>
				
				<table class="scoretable" style="margin-left: 30px;">
					<tr>
						<th class="sr-only" colspan="2">${form.getMessage("label.Score")}</th>
						<th class="sr-only">${form.getMessage("label.DelphiChartPie")}</th>
					</tr>
					<tr style="font-size: 130%;">
						<td style="padding: 15px; color: #337ab7;"><b>${form.getMessage("label.YourScore")}</b></td>
						<td style="padding: 15px; color: #337ab7;">${quiz.score}</td>
						<td rowspan="2" style="padding-left: 50px;">
							<img style="margin-top: -18px" src="${contextpath}/graphics/pie.png?v=${quiz.score}&amp;m=${quiz.maximumScore}" />
						</td>
					</tr>
					<tr>
						<td style="padding: 15px; padding-top: 10px; border: 0px;"><b>${form.getMessage("label.MaximumScore")}</b></td>
						<td style="padding: 15px; padding-top: 10px; border: 0px;">${quiz.maximumScore}</td>
					</tr>
				</table>			
			
				<c:if test="${form.survey.scoresByQuestion}">
				
					<c:if test="${form.survey.hasSections()}">
					<div style="margin-top: 20px; margin-bottom: 30px;">
						<table class="quizsectionresults" <c:if test="${ismobile == null}">style="width: 500px;"</c:if>>
							<tr>
								<th>${form.getMessage("form.Section")}</th>
								<th colspan="2">${form.getMessage("label.ScoreForThisSection")}</th>
							</tr>
								<c:forEach var="page" items="${form.getPages()}" varStatus="rowCounter">
				 					<c:forEach var="element" items="${page}">
				 						<c:if test="${element.getType() == 'Section'}">
					 						<c:if test='${!quiz.getSectionScore(element.uniqueId).equals("0/0") && !(invisibleElements != null && invisibleElements.contains(element.uniqueId))}'>
												<tr>
													<c:set var="scoring" value="${quiz.getSectionScore(element.uniqueId)}" />
													<td style="width: 225px">${element.getStrippedTitle()}</td>
													<td style="width: 50px">${scoring}</td>
													<td style="width: 225px">
														<div class="progress" style="width: 200px; margin-bottom: 2px;">
														  <div class="progress-bar" style="width: ${quiz.getSectionScoreValue(element.uniqueId) / quiz.getMaxSectionScore(element.uniqueId) * 100}%;"></div>
														</div>
													</td>
												</tr>
											</c:if>
										</c:if>
									</c:forEach>
								</c:forEach>								
						</table>
					</div>
					</c:if>				
				
					<div style="font-size: 25px;">${form.getMessage("label.ScoresByQuestion")}:</div>
					
					<table style="margin-top: 20px; margin-bottom: 20px; table-layout:fixed; max-width: 100%">
						<tr>
							<th class="sr-only" colspan="2">${form.getMessage("label.Answer")}</th>
							<th class="sr-only">${form.getMessage("label.Score")}</th>
						</tr>
						<c:forEach var="page" items="${form.getPages()}" varStatus="rowCounter">
				 			<c:forEach var="element" items="${page}">
				 				<c:choose>
				 					<c:when test="${invisibleElements != null && invisibleElements.contains(element.uniqueId)}">
				 					
				 					</c:when>		 				
				 					<c:when test="${element.getType() == 'Section'}">
				 						<c:set var="scoring" value="${quiz.getSectionScore(element.uniqueId)}" />
						 				<c:if test='${!scoring.equals("0/0")}'>		
					 						<tr class="sectiontitle section${element.getLevel()}">
					 							<td colspan="2" style="padding-top: 20px">
													${element.getStrippedTitle()}
					 							</td>
												<td colspan="2" style="padding-top: 20px;">
													<c:if test="${scoring != null}">
														<div style="float: right; font-size: 14px !important">
															${form.getMessage("label.ScoreForThisSection")}: ${scoring}
														</div>
													</c:if>
												</td>
					 						</tr>
				 						</c:if>
				 					</c:when>
				 					<c:when test="${element.getType() == 'Matrix'}">
				 						<c:forEach var="matrixQuestion" items="${element.questions}">
				 						    <c:choose>
				 						    	<c:when test="${invisibleElements != null && invisibleElements.contains(matrixQuestion.uniqueId)}">

                                            	</c:when>
                                                <c:when test="${matrixQuestion.scoring > 0}">
                                                    <tr>
                                                        <td colspan="4" style="padding-top: 20px">
                                                            <c:choose>
                                                                <c:when test="${forpdf != null || (element.getStrippedTitleNoEscape().length() + matrixQuestion.getStrippedTitleNoEscape().length()) < 200}">
                                                                    <div class="quizquestion">${element.getStrippedTitleNoEscape()}: ${matrixQuestion.getStrippedTitleNoEscape()}</div>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <div class="quizquestion">
                                                                        <div class="fullcontent hideme">${element.getStrippedTitle()}: ${matrixQuestion.getStrippedTitle()}<a class='lessbutton' onclick='switchQuestionTitle(this);'>${form.getMessage("label.less")}</a>
                                                                        </div>
                                                                        <div class='shortcontent'>${(element.getStrippedTitle() + ': ' + matrixQuestion.getStrippedTitle()).substring(0,190)}<a class="morebutton" onclick="switchQuestionTitle(this);">${form.getMessage("label.more")}</a>
                                                                        </div>
                                                                    </div>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                    </tr>

                                                    <c:set var="answers" value="${form.answerSets[0].getAnswers(matrixQuestion.uniqueId)}" />

                                                    <tr class="scorerow">
                                                        <td>${form.getMessage("label.YourAnswer")}</td>
                                                        <td>
                                                            <c:forEach items="${answers}" var="answer">
                                                                <div class="quizanswer">
                                                                    <c:set var="pos" value="${quiz.getPositionForAnswerUID().get(answer.getPossibleAnswerUniqueId())}" />
                                                                    <c:set var="scoringItem" value="${matrixQuestion.getScoringItems().get(pos)}" />

                                                                    <c:choose>
                                                                        <c:when test="${!form.survey.showQuizIcons}">

                                                                        </c:when>
                                                                        <c:when test="${forpdf != null && scoringItem.correct}">
                                                                            <img style="width: 20px;vertical-align: text-top;" src="${contextpath}/resources/images/correct.png" />
                                                                        </c:when>
                                                                        <c:when test="${scoringItem.correct}">
                                                                            <span class="glyphicon glyphicon-ok" style="color: #0f0"></span>
                                                                        </c:when>
                                                                        <c:when test="${forpdf != null}">
                                                                            <img style="width: 20px;vertical-align: text-top;" src="${contextpath}/resources/images/incorrect.png" />
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <span class="glyphicon glyphicon-remove" style="color: #f00"></span>
                                                                        </c:otherwise>
                                                                    </c:choose>

                                                                    ${fn:escapeXml(form.getAnswerTitle(answer))}
                                                                    <div class="quizfeedback">${scoringItem.feedback}</div>
                                                                </div>
                                                            </c:forEach>

                                                            <c:if test="${quiz.getPartiallyAnswersMultipleChoiceQuestions().contains(matrixQuestion.getUniqueId())}">
                                                                <div class="quizanswer">
                                                                    <c:choose>
                                                                        <c:when test="${!form.survey.showQuizIcons}">

                                                                        </c:when>
                                                                        <c:when test="${forpdf != null}">
                                                                            <img style="width: 20px;vertical-align: text-top;" src="${contextpath}/resources/images/incorrect.png")}
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <span class="glyphicon glyphicon-remove" style="color: #f00"></span>
                                                                        </c:otherwise>
                                                                    </c:choose>

                                                                    ${form.getMessage("info.NotAllCorrectAnswers")}
                                                                </div>
                                                            </c:if>
                                                        </td>
                                                        <td class="score">${quiz.getQuestionScore(matrixQuestion.uniqueId)}&#x20;${form.getMessage("label.outOf")}&#x20;${quiz.getQuestionMaximumScore(matrixQuestion.uniqueId)}&#x20;${form.getMessage("label.points")}</td>
                                                        <td>
                                                            <div class="progress hidden-xs hidden-md" style="width: 200px; margin-bottom: 2px;">
                                                              <div class="chartRequestedRecordsPercent progress-bar" style="width: ${quiz.getQuestionScore(matrixQuestion.uniqueId) / quiz.getQuestionMaximumScore(matrixQuestion.uniqueId) * 100}%;"></div>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                    <c:if test="${forpdf == null}">
                                                        <tr class="visible-xs visible-md">
                                                            <td colspan="3">
                                                                <div class="progress" style="width: 200px; margin-bottom: 2px;">
                                                                  <div class="chartRequestedRecordsPercent progress-bar" style="width: ${quiz.getQuestionScore(matrixQuestion.uniqueId) / quiz.getQuestionMaximumScore(matrixQuestion.uniqueId) * 100}%;"></div>
                                                                </div>
                                                            </td>
                                                        </tr>
                                                    </c:if>
                                                </c:when>
                                            </c:choose>
				 						</c:forEach>
				 					</c:when>				 					
				 					<c:when test="${element.getType() == 'SingleChoiceQuestion' || element.getType() == 'MultipleChoiceQuestion' || element.getType() == 'FreeTextQuestion' || element.getType() == 'NumberQuestion' || element.getType() == 'DateQuestion'}">
						 				<c:if test="${element.scoring > 0}">
						 					<tr>
						 						<td colspan="4" style="padding-top: 20px">
					 								<c:choose>
														<c:when test="${forpdf != null || element.getStrippedTitleNoEscape().length() < 200}">
															<div class="quizquestion">${element.getStrippedTitleNoEscape()}</div>
														</c:when>
														<c:otherwise>
															<div class="quizquestion">
																<div class="fullcontent hideme">${element.getStrippedTitle()}<a class='lessbutton' onclick='switchQuestionTitle(this);'>${form.getMessage("label.less")}</a>
																</div>
																<div class='shortcontent'>${element.getStrippedTitle().substring(0,190)}<a class="morebutton" onclick="switchQuestionTitle(this);">${form.getMessage("label.more")}</a>
																</div>
															</div>
														</c:otherwise>
													</c:choose>			 							
						 						</td>
						 					</tr>				 				
								 				
						 					<c:set var="answers" value="${form.answerSets[0].getAnswers(element.uniqueId)}" />
						 					
						 					<tr class="scorerow">
								 				<td>${form.getMessage("label.YourAnswer")}</td>
						 						<td>
						 							<c:choose>
							 							<c:when test="${element.getType() == 'SingleChoiceQuestion' || element.getType() == 'MultipleChoiceQuestion'}">
							 								<c:forEach items="${answers}" var="answer">	
							 									<div class="quizanswer">
								 									<c:set var="pa" value="${element.getPossibleAnswerByUniqueId(answer.possibleAnswerUniqueId)}" />
								 									<c:choose>
								 										<c:when test="${!form.survey.showQuizIcons}">
								 										
								 										</c:when>
								 										<c:when test="${forpdf != null && pa.scoring.correct}">
								 											<img style="width: 20px;vertical-align: text-top;" src="${contextpath}/resources/images/correct.png" />
								 										</c:when>
								 										<c:when test="${pa.scoring.correct}">
								 											<span class="glyphicon glyphicon-ok" style="color: #0f0"></span>
								 										</c:when>
								 										<c:when test="${forpdf != null}">
								 											<img style="width: 20px;vertical-align: text-top;" src="${contextpath}/resources/images/incorrect.png" />
								 										</c:when>
								 										<c:otherwise>
								 											<span class="glyphicon glyphicon-remove" style="color: #f00"></span>
								 										</c:otherwise>
								 									</c:choose>
								 									
																	${fn:escapeXml(form.getAnswerTitle(answer))}
																	<div class="quizfeedback">${pa.scoring.feedback}</div>
																</div>
															</c:forEach>		
															
															<c:if test="${element.getType() == 'MultipleChoiceQuestion' && quiz.getPartiallyAnswersMultipleChoiceQuestions().contains(element.getUniqueId())}">
							 									<div class="quizanswer">
							 										<c:choose>
							 											<c:when test="${!form.survey.showQuizIcons}">
								 										
								 										</c:when>
							 											<c:when test="${forpdf != null}">
								 											<img style="width: 20px;vertical-align: text-top;" src="${contextpath}/resources/images/incorrect.png" />
								 										</c:when>
								 										<c:otherwise>
								 											<span class="glyphicon glyphicon-remove" style="color: #f00"></span>
								 										</c:otherwise>
							 										</c:choose>
							 										
							 										${form.getMessage("info.NotAllCorrectAnswers")}
							 									</div>
							 								</c:if> 							
							 							</c:when>
							 							<c:otherwise>
							 								<c:if test="${answers == null || answers.size() == 0}">
							 									<c:set var="scoring" value="${quiz.getQuestionScoringItem(element.uniqueId)}" />
						 										<c:if test="${scoring != null}">
						 											<div class="quizanswer">
						 												<c:choose>
						 													<c:when test="${!form.survey.showQuizIcons}">
								 										
								 											</c:when>
									 										<c:when test="${forpdf != null && scoring.correct}">
									 											<img style="width: 20px;vertical-align: text-top;" src="${contextpath}/resources/images/correct.png" />
									 										</c:when>
									 										<c:when test="${scoring.correct}">
									 											<span class="glyphicon glyphicon-ok" style="color: #0f0"></span>
									 										</c:when>
									 										<c:when test="${forpdf != null}">
									 											<img style="width: 20px;vertical-align: text-top;" src="${contextpath}/resources/images/incorrect.png" />
									 										</c:when>
									 										<c:otherwise>
									 											<span class="glyphicon glyphicon-remove" style="color: #f00"></span>
									 										</c:otherwise>
									 									</c:choose>
																		${form.getMessage("label.emptyAnswer")}
																		<div class="quizfeedback">${scoring.feedback}</div>
						 											</div>
						 										</c:if>
							 								</c:if>
							 								<c:forEach items="${answers}" var="answer">	
							 									<div class="quizanswer">
							 										<c:set var="scoring" value="${quiz.getQuestionScoringItem(element.uniqueId)}" />
							 										<c:if test="${scoring != null}">
								 										<c:choose>
								 											<c:when test="${!form.survey.showQuizIcons}">
								 										
								 											</c:when>
									 										<c:when test="${forpdf != null && scoring.correct}">
									 											<img style="width: 20px;vertical-align: text-top;" src="${contextpath}/resources/images/correct.png" />
									 										</c:when>
									 										<c:when test="${scoring.correct}">
									 											<span class="glyphicon glyphicon-ok" style="color: #0f0"></span>
									 										</c:when>
									 										<c:when test="${forpdf != null}">
									 											<img style="width: 20px;vertical-align: text-top;" src="${contextpath}/resources/images/incorrect.png" />
									 										</c:when>
									 										<c:otherwise>
									 											<span class="glyphicon glyphicon-remove" style="color: #f00"></span>
									 										</c:otherwise>
									 									</c:choose>
																		${fn:escapeXml(form.getAnswerTitle(answer))}
																		<div class="quizfeedback">${scoring.feedback}</div>
																	</c:if>
																	<c:if test="${scoring == null && forpdf == null}">
																		<c:if test="${form.survey.showQuizIcons}">
																		<span class="glyphicon glyphicon-remove" style="color: #f00"></span>
																		</c:if>
																		${fn:escapeXml(form.getAnswerTitle(answer))}
																	</c:if>
																	<c:if test="${scoring == null && forpdf != null}">
																		<img style="width: 20px;vertical-align: text-top;" src="${contextpath}/resources/images/incorrect.png" />
																		${fn:escapeXml(form.getAnswerTitle(answer))}
																	</c:if>
							 									</div>
							 								</c:forEach>
							 							</c:otherwise>
						 							</c:choose>
						 						</td>
						 						<td class="score">${quiz.getQuestionScore(element.uniqueId)}&#x20;${form.getMessage("label.outOf")}&#x20;${quiz.getQuestionMaximumScore(element.uniqueId)}&#x20;${form.getMessage("label.points")}</td>
						 						<td>
						 							<div class="progress hidden-xs hidden-md" style="width: 200px; margin-bottom: 2px;">
													  <div class="chartRequestedRecordsPercent progress-bar" style="width: ${quiz.getQuestionScore(element.uniqueId) / quiz.getQuestionMaximumScore(element.uniqueId) * 100}%;"></div>
													</div>
						 						</td>
								 			</tr>
						 					<c:if test="${forpdf == null}">
							 					<tr class="visible-xs visible-md">
							 						<td colspan="3">
							 							<div class="progress" style="width: 200px; margin-bottom: 2px;">
														  <div class="chartRequestedRecordsPercent progress-bar" style="width: ${quiz.getQuestionScore(element.uniqueId) / quiz.getQuestionMaximumScore(element.uniqueId) * 100}%;"></div>
														</div>
							 						</td>
							 					</tr>
							 				</c:if>
									</c:if>
						 	</c:when>
				 		</c:choose>
				 	</c:forEach>
				 </c:forEach>
			</table>
				</c:if>
			</c:if>
			
			<hr />
			<table style="margin-left: 20px;">
				<tr>
					<th class="sr-only" colspan="2">${form.getMessage("label.ResultDetails")}</th>
				</tr>
				<tr>
					<td style="padding-right: 10px">${form.getMessage("label.Contact")}</td>
					<td>
						<c:choose>
							<c:when test="${form.survey.contact.startsWith('form:')}">
								<a target="_blank" class="link visibleLink" data-toggle="tooltip" title="${form.getMessage("info.ContactForm")}" aria-label="${form.getMessage("info.ContactForm")}" href="${serverprefix}runner/contactform/${form.survey.shortname}">${form.getMessage("label.ContactForm")}</a>
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
					</td>
				</tr>
				
				<c:if test="${form.survey.getUsefulLinks().size() != 0}">	
					<tr>
						<td style="padding-right: 10px">${form.getMessage("label.UsefulLinks")}</td>
						<td>					
							<c:forEach var="link" items="${form.survey.getAdvancedUsefulLinks()}">
								<div style="margin-top: 5px;" ><a class="link visiblelink" target="_blank" rel="noopener noreferrer" href="<esapi:encodeForHTMLAttribute>${link.value}</esapi:encodeForHTMLAttribute>">${link.key}</a></div>
							</c:forEach>							
						</td>
					</tr>
				</c:if>
				
				<c:if test="${form.survey.getBackgroundDocuments().size() != 0}">
					<tr>
						<td style="padding-right: 10px">${form.getMessage("label.BackgroundDocuments")}</td>
						<td>
							<c:forEach var="link" items="${form.survey.getBackgroundDocumentsAlphabetical()}">
								<div style="margin-top: 5px;" ><a class="link visiblelink" target="_blank" href="<esapi:encodeForHTMLAttribute>${link.value}</esapi:encodeForHTMLAttribute>">${link.key}</a></div>
							</c:forEach>							
						</td>
					</tr>
				</c:if>	
				
				<tr>
					<td style="padding-right: 10px; padding-top: 10px;">${form.getMessage("label.ContributionId")}</td>
					<td style="padding-top: 10px;">${form.answerSets[0].uniqueCode}</td>
				</tr>
				<tr>
					<td style="padding-right: 10px">${form.getMessage("label.CompletedAt")}</td>
					<td>${form.answerSets[0].niceDate}</td>
				</tr>
				<tr>
					<td style="padding-right: 10px">${form.getMessage("label.CompletionTime")}</td>
					<td>${form.answerSets[0].completionTime()}</td>
				</tr>
				
			</table>			
			
		</div>
		
		<c:if test="${forpdf == null && form.survey.downloadContribution}">
			<div style="text-align: center; margin-bottom: 20px;">
				<a href="javascript:;" id="pdfDownloadButtonThanksInner" onclick="showExportDialogAndFocusEmail(this)" class="btn btn-default">${form.getMessage("label.GetPDF")}</a>		
			</div>
		</c:if>
	</div>
	
	<c:if test="${forpdf == null}">
	
	<div class="modal" id="ask-export-dialog" data-backdrop="static" role="dialog">	
			<div class="modal-dialog">
		    <div class="modal-content">
			<div class="modal-header">
				<b>${form.getMessage("label.Info")}</b>
			</div>
			<div class="modal-body">
				<p>
					<c:choose>
						<c:when test="${runnermode == true}">
							${form.getMessage("question.EmailForPDF")}
						</c:when>
						<c:otherwise>
							${form.getMessage("question.EmailForPDF")}
						</c:otherwise>	
					</c:choose>
				</p>
				<input type="text" maxlength="255" name="email" id="email" />
				<span id="ask-export-dialog-error" class="validation-error hideme">
					<c:choose>
						<c:when test="${runnermode == true}">
							${form.getMessage("message.ProvideEmail")}
						</c:when>
						<c:otherwise>
							${form.getMessage("message.ProvideEmail")}
						</c:otherwise>	
					</c:choose>
				</span>
				<div class="captcha" style="margin-left: 0px; margin-bottom: 20px; margin-top: 20px;">						
					<c:if test="${captchaBypass !=true}">
					<%@ include file="../captcha.jsp" %>					
					</c:if>
		       	</div>
		       	<span id="ask-export-dialog-error-captcha" class="validation-error hideme">       		
		       		<c:if test="${captchaBypass !=true}">
		       		<c:choose>
						<c:when test="${runnermode == true}">
							${form.getMessage("message.captchawrongnew")}
						</c:when>
						<c:otherwise>
							${form.getMessage("message.captchawrongnew")}
						</c:otherwise>	
					</c:choose>
		       		</c:if>
		       	</span>
			</div>
			<div class="modal-footer">
				<c:choose>
					<c:when test="${responsive != null}">
						<button type="button" style="text-decoration: none"  class="btn btn-primary btn-lg" onclick="startExport()">${form.getMessage("label.OK")}</button>
						<button type="button" style="text-decoration: none"  class="btn btn-default btn-lg" onclick="hideModalDialog($('#ask-export-dialog'))">${form.getMessage("label.Cancel")}</button>
					</c:when>
					<c:when test="${runnermode == true}">
						<button type="button" class="btn btn-primary" onclick="startExport()">${form.getMessage("label.OK")}</button>
						<button type="button" class="btn btn-default" onclick="hideModalDialog($('#ask-export-dialog'))">${form.getMessage("label.Cancel")}</button>
					</c:when>
					<c:otherwise>
						<button type="button" class="btn btn-primary" onclick="startExport()">${form.getMessage("label.OK")}</button>
						<button type="button" class="btn btn-default" onclick="hideModalDialog($('#ask-export-dialog'))">${form.getMessage("label.Cancel")}</button>
					</c:otherwise>	
				</c:choose>				
			</div>
			</div>
			</div>
		</div>
		
		<script type="text/javascript">
			
			function switchQuestionTitle(a)
			{
				var div = $(a).closest(".quizquestion");
				var text = $(div).find(".fullcontent").html().replace("&nbsp;", " ");
				
				if ($(a).hasClass("morebutton"))
				{
					$(div).find(".shortcontent").hide();
					$(div).find(".fullcontent").show();
				} else {
					$(div).find(".shortcontent").show();
					$(div).find(".fullcontent").hide();
				}
			}
		
			function startExport()
			{
				$("#ask-export-dialog").find(".validation-error").hide();
				
				var mail = $("#email").val();
				if (mail.trim().length == 0 || !validateEmail(mail))
				{
					$("#ask-export-dialog-error").show();
					return;
				};	
						
				<c:choose>
					<c:when test="${!captchaBypass}">
						var challenge = getChallenge();
					    var uresponse = getResponse();
					    
					    var data = {email : mail, recaptcha_challenge_field : challenge, 'g-recaptcha-response' : uresponse};
						if ($('#captcha_id').length > 0) {
							data["captcha_id"] =  $('#captcha_id').val();
							data["captcha_useaudio"] =  $('#captcha_useaudio').val();
							data["captcha_original_cookies"] = $('#captcha_original_cookies').val();
						}
					
						$.ajax({
							type:'GET',
							  url: "${contextpath}/runner/createquizpdf/${uniqueCode}",
							  data: data,
							  cache: false,
							  success: function( data ) {
								  
								  if (data == "success") {
										$('#ask-export-dialog').modal('hide');
										showSuccess(message_PublicationExportSuccess2.replace('{0}', mail));
								  	} else if (data == "errorcaptcha") {
								  		$("#ask-export-dialog-error-captcha").show();
								  		reloadCaptcha();
									} else {
										showError(message_PublicationExportFailed);
										reloadCaptcha();
									};
							}
						});							
					</c:when>
					<c:otherwise>			
						$.ajax({				
							type:'GET',
							  url: "${contextpath}/runner/createquizpdf/${uniqueCode}",
							  data: {email : mail, recaptcha_challenge_field : '', 'g-recaptcha-response' : ''},
							  cache: false,
							  success: function( data ) {
								  
								  if (data == "success") {
										$('#ask-export-dialog').modal('hide');
										showSuccess(message_PublicationExportSuccess2.replace('{0}', mail));
									} else {
										showError(message_PublicationExportFailed);
										reloadCaptcha();
									};
							}
						});							
					</c:otherwise>
				</c:choose>
			}
		</script>
		
		</c:if>
