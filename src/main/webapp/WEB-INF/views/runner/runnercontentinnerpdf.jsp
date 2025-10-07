<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>	
<%@ page import="java.util.Map" %>
<%@ page import="com.ec.survey.model.Form" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>	
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>

	<input type="hidden" id="preventGoingBack" value="${form.survey.preventGoingBack}" />
	<input type="hidden" id="validatedPerPage" value="${form.survey.validatedPerPage}" />
	<input type="hidden" id="newlang" name="newlang" value="${form.language.code }" />
	<input type="hidden" id="newcss" name="newcss" value="" />
	<input type="hidden" id="wcagMode" name="wcagMode" value="${form.wcagCompliance}" />

	<c:if test="${form.survey.logo != null}">
		<div style="margin-bottom: 5px; margin-left: 30px;">
			<img src="<c:url value="/files/${form.survey.uniqueId}/${form.survey.logo.uid}" />" alt="logo" style="width: 220px" />
		</div>
	</c:if>
				
	<div class="left-area" style="float: none; width: auto; padding-top: 0px;">
	
		<div style="max-width: 100%">
			<div class="surveytitle">${form.survey.title}</div><br />
		</div>

		<c:if test="${form.survey.containsMandatoryQuestion()}">
			<div class="info-box" style="width: 400px;">${form.getMessage("message.StarMandatory")}</div>
		</c:if>
		
		<span class="introduction">${form.survey.introduction}</span>
		
		<c:forEach var="page" items="${form.getPages()}" varStatus="rowCounter">
		 	
				<div class="single-page" id="page${rowCounter.index}" tabindex="-1">
				
				<c:forEach var="element" items="${page}">
						
					  <c:choose>
					    <c:when test="${element.hasPDFWidth}">
					    	<fieldset class="elem_${element.id}"> 
					    </c:when>
					    <c:otherwise>
					    	<fieldset class="elem_basic">
					    </c:otherwise>
					  </c:choose>

						<div class="elementwrapper">
					
						<c:if test="${(publication == null && !element.isElementHidden())|| publication.isAllQuestions() || publication.isSelected(element.id)}">
					
						<c:choose>
							<c:when test="${element.isDummy() && element.isDependent && ((invisibleElements == null && forpdf == null) || invisibleElements.contains(element.uniqueId))}">
								<div class="survey-element untriggered 1" data-id="${element.id}" data-triggers="${element.triggers}" style="margin-top: 5px; display: none;">
							</c:when>
							<c:when test="${element.getType() == 'Matrix' && element.getAllQuestionsDependent() && ((invisibleElements == null && forpdf == null) || invisibleElements.contains(element.uniqueId))}">
								<div class="survey-element untriggered 2" id="${element.id}" data-id="${element.id}" data-triggers="${element.triggers}" style="display: none;">
							</c:when>
							<c:when test="${((invisibleElements == null && forpdf == null) || invisibleElements.contains(element.uniqueId))}">
								<div class="survey-element untriggered 3" id="${element.id}" data-id="${element.id}" data-triggers="${element.triggers}" style="display: none;">
							</c:when>
							<c:when test="${element.isDependent}">
								<div class="survey-element 3b" id="${element.id}" data-id="${element.id}" data-triggers="${element.triggers}">
							</c:when>
							<c:when test="${element.isDummy()}">
								<div class="survey-element 4" data-id="${element.id}" style="margin-top: 5px;">
							</c:when>
							<c:otherwise>
							   <div class="survey-element 5" id="${element.id}" data-id="${element.id}">
							</c:otherwise>
						</c:choose>
						
						<c:if test="${element.getType() == 'Section'}">
							<div role="heading" aria-level="${element.level}" data-level="${element.level}" class="sectiontitle section${element.level}">${form.getSectionTitle(element)}</div>
						</c:if>
						
						<c:if test="${element.getType() == 'Text'}">
							<div class="text">${form.getQuestionTitle(element)}</div>
						</c:if>
						
						<c:if test="${element.getType() == 'Image'}">
							<div class="alignment-div" style="width: 100%; text-align:${element.align};">																			
								<c:choose>
									<c:when test="${element.scale != null && element.scale != 0 && forpdf != null && (element.scale / 100 * element.width) > 600}">
										<img src="${element.url}" alt="<esapi:encodeForHTMLAttribute>${element.title}</esapi:encodeForHTMLAttribute>" width="600px"/>
									</c:when>
									<c:when test="${element.scale != null && element.scale != 0}">
										<img src="${element.url}" alt="<esapi:encodeForHTMLAttribute>${element.title}</esapi:encodeForHTMLAttribute>" style="width: ${element.scale / 100 * element.width}px; max-width: ${element.scale / 100 * element.width}px" />
									</c:when>
									<c:otherwise>
										<img src="${element.url}" alt="<esapi:encodeForHTMLAttribute>${element.title}</esapi:encodeForHTMLAttribute>" />
									</c:otherwise>
								</c:choose>			
							</div>
						</c:if>
						
						<c:if test="${element.getType() == 'Ruler'}">
							<hr style="border-top: ${element.height}px ${element.style} ${element.color}" />
						</c:if>
						
						<c:if test="${element.getType().endsWith('Question')}">
							<c:if test="${!element.getOptional()}">
								<span class="mandatory" style="position: absolute; margin-left: 17px; margin-top: 2px;">*</span>
							</c:if>
							<label for="input${element.id}"><div class="questiontitle">${form.getQuestionTitle(element)}</div></label>
							<c:choose>
								<c:when test="${element.getType() == 'FreeTextQuestion' && element.getMinCharacters() != null && element.getMinCharacters() > 0 && element.getMaxCharacters() != null && element.getMaxCharacters() > 0}">
									<div class='limits'>${form.getMessage("limits.MinMaxCharacters", element.getMinCharacters(), element.getMaxCharacters())}&nbsp;<span class="charactercounter"></span></div>
								</c:when>
								<c:when test="${element.getType() == 'FreeTextQuestion' && element.getMinCharacters() != null && element.getMinCharacters() > 0}">
									<div class='limits'>${form.getMessage("limits.MinCharacters", element.getMinCharacters())}&nbsp;<span class="charactercounter"></span></div>
								</c:when>
								<c:when test="${element.getType() == 'FreeTextQuestion' && element.getMaxCharacters() != null && element.getMaxCharacters() > 0}">
									<div class='limits'>${form.getMessage("limits.MaxCharacters", element.getMaxCharacters())}&nbsp;<span class="charactercounter"></span></div>
								</c:when>
								<c:when test="${element.getType() == 'MultipleChoiceQuestion' && element.getMinChoices() != null && element.getMinChoices() > 0 && element.getMaxChoices() != null && element.getMaxChoices() > 0}">
									<div class='limits'>${form.getMessage("limits.MinMaxChoices", element.getMinChoices(), element.getMaxChoices())}</div>
								</c:when>
								<c:when test="${element.getType() == 'MultipleChoiceQuestion' && element.getMinChoices() != null && element.getMinChoices() > 0}">
									<div class='limits'>${form.getMessage("limits.MinChoicesNew", element.getMinChoices())}</div>
								</c:when>
								<c:when test="${element.getType() == 'MultipleChoiceQuestion' && element.getMaxChoices() != null && element.getMaxChoices() > 0}">
									<div class='limits'>${form.getMessage("limits.MaxChoicesNew", element.getMaxChoices())}</div>
								</c:when>
								<c:when test="${element.getType() == 'NumberQuestion' && element.getDisplay() != 'Slider' && element.getMin() != null && element.getMax() != null}">
									<div class='limits'>${form.getMessage("limits.MinMaxNumber", element.getMinString(), element.getMaxString())}</div>
								</c:when>
								<c:when test="${element.getType() == 'NumberQuestion' && element.getDisplay() != 'Slider' && element.getMin() != null}">
									<div class='limits'>${form.getMessage("limits.MinNumber", element.getMinString())}</div>
								</c:when>
								<c:when test="${element.getType() == 'NumberQuestion' && element.getDisplay() != 'Slider' && element.getMax() != null}">
									<div class='limits'>${form.getMessage("limits.MaxNumber", element.getMaxString())}</div>
								</c:when>								
								<c:when test="${element.getType() == 'FormulaQuestion' && element.getMin() != null && element.getMax() != null}">
									<div class='limits'>${form.getMessage("limits.MinMaxNumber", element.getMinString(), element.getMaxString())}</div>
								</c:when>
								<c:when test="${element.getType() == 'FormulaQuestion' && element.getMin() != null}">
									<div class='limits'>${form.getMessage("limits.MinNumber", element.getMinString())}</div>
								</c:when>
								<c:when test="${element.getType() == 'FormulaQuestion' && element.getMax() != null}">
									<div class='limits'>${form.getMessage("limits.MaxNumber", element.getMaxString())}</div>
								</c:when>								
								<c:when test="${element.getType() == 'DateQuestion' && element.getMin() != null && element.getMax() != null}">
									<div class='limits'>${form.getMessage("limits.MinMaxDate", element.getMinString(), element.getMaxString())}</div>
								</c:when>
								<c:when test="${element.getType() == 'DateQuestion' && element.getMin() != null}">
									<div class='limits'>${form.getMessage("limits.MinDate", element.getMinString())}</div>
								</c:when>
								<c:when test="${element.getType() == 'DateQuestion' && element.getMax() != null}">
									<div class='limits'>${form.getMessage("limits.MaxDate", element.getMaxString())}</div>
								</c:when>								
								<c:when test="${element.getType() == 'TimeQuestion' && element.getMin() != null && element.getMin().length() > 0 && element.getMax() != null && element.getMax().length() > 0}">
									<div class='limits'>${form.getMessage("limits.MinMaxDate", element.getMin(), element.getMax())}</div>
								</c:when>
								<c:when test="${element.getType() == 'TimeQuestion' && element.getMin() != null && element.getMin().length() > 0}">
									<div class='limits'>${form.getMessage("limits.MinDate", element.getMin())}</div>
								</c:when>
								<c:when test="${element.getType() == 'TimeQuestion' && element.getMax() != null && element.getMax().length() > 0}">
									<div class='limits'>${form.getMessage("limits.MaxDate", element.getMax())}</div>
								</c:when>								
								<c:when test="${element.getType() == 'GalleryQuestion' && element.selection && element.getLimit() != null && element.getLimit() > 0}">
									<div class='limits'>${form.getMessage("limits.MaxSelections", element.getLimit())}</div>
								</c:when>								
							</c:choose>										
						</c:if>
						
						<c:if test="${element.getType().endsWith('Question') && element.getHelp().length() > 0}">
							<div class="questionhelp">${element.help}</div>
						</c:if>
						
						<c:if test="${element.getType() == 'RatingQuestion'}">
							
							<table class="ratingtable">
								<c:forEach items="${element.childElements}" var="ratingQuestion" varStatus="rowCounter">		
									<tr>
										<td>${ratingQuestion.title}</td>
										<td>
											<c:forEach begin="1" end="${element.numIcons}" varStatus="loop">											
												<span class="ratingitem">
												
													<c:choose>
														<c:when test="${element.iconType == 0}">
															<c:choose>
																<c:when test="${form.getRatingValue(ratingQuestion) >= loop.index}">
																	<img src="${contextpath}/resources/images/star_yellow.png" />
																</c:when>
																<c:otherwise>
																	<img src="${contextpath}/resources/images/star_grey.png" />
																</c:otherwise>
															</c:choose>	
														</c:when>
														<c:when test="${element.iconType == 1}">
															<c:choose>
																<c:when test="${form.getRatingValue(ratingQuestion) >= loop.index}">
																	<img src="${contextpath}/resources/images/nav_plain_blue.png" />
																</c:when>
																<c:otherwise>
																	<img src="${contextpath}/resources/images/nav_plain_grey.png" />
																</c:otherwise>
															</c:choose>															
														</c:when>
														<c:when test="${element.iconType == 2}">
															<c:choose>
																<c:when test="${form.getRatingValue(ratingQuestion) >= loop.index}">
																	<img src="${contextpath}/resources/images/heart_red.png" />
																</c:when>
																<c:otherwise>
																	<img src="${contextpath}/resources/images/heart_grey.png" />
																</c:otherwise>
															</c:choose>															
														</c:when>
													</c:choose>
											    </span>
											</c:forEach>
										</td>
									</tr>
								</c:forEach>
							</table>		
							
						</c:if>
																						
						<c:if test="${element.getType() == 'Matrix'}">
							<div class="questiontitle">${form.getQuestionTitle(element)}</div>
							<div class="questionhelp">${element.help}</div>
							
							<c:choose>
								<c:when test="${element.getType() == 'Matrix' && element.getMinRows() != null && element.getMinRows() > 0 && element.getMaxRows() != null && element.getMaxRows() > 0}">
									<div class='limits'>${form.getMessage("limits.MinMaxRows", element.getMinRows(), element.getMaxRows())}</div>
								</c:when>
								<c:when test="${element.getType() == 'Matrix' && element.getMinRows() != null && element.getMinRows() > 0}">
									<div class='limits'>${form.getMessage("limits.MinRows", element.getMinRows())}</div>
								</c:when>
								<c:when test="${element.getType() == 'Matrix' && element.getMaxRows() != null && element.getMaxRows() > 0}">
									<div class='limits'>${form.getMessage("limits.MaxRows", element.getMaxRows())}</div>
								</c:when>
							</c:choose>
							
							<div>
								<table class="matrixtable ${element.css}" style="width: auto">			
								
									<c:set var="counter" value="0" />		
																		
									<c:forEach var="r" begin="1" end="${element.rows}"> 									
										
										<c:choose>
											<c:when test="${r > 1}">
												<c:set var="matrixquestion" value="${element.getChildElementsOrdered().get(element.columns + r - 2)}" />
												
												<c:choose>
													<c:when test="${matrixquestion.isDependentMatrixQuestion && ((invisibleElements == null && forpdf == null) || invisibleElements.contains(matrixquestion.uniqueId))}">
														<tr class="matrix-question 1 untriggered" id="${matrixquestion.id}" data-id="${matrixquestion.id}" data-triggers="${matrixquestion.triggersMatrixQuestion}" style="display: none;">
													</c:when>
													<c:otherwise>
													   <tr class="matrix-question 2" id="${matrixquestion.id}" data-id="${matrixquestion.id}" data-triggers="${matrixquestion.triggersMatrixQuestion}">
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
												<tr>		
											</c:otherwise>
										</c:choose>
						
											<c:forEach var="c" begin="1" end="${element.columns}">
											
												<c:choose>
													<c:when test="${r > 1 && c > 1}">
														<td class="matrix-cell">		
													</c:when>
													<c:when test="${r == 1 && element.getTableType() == 2}">
														<td class="matrix-header" style="width: ${element.getWidth(c-1)}">		
													</c:when>
													<c:otherwise>
														<td class="matrix-header">		
													</c:otherwise>
												</c:choose>
												
													<c:choose>
														<c:when test="${r == 1 && c == 1}">
															<div>${element.firstCellText}</div>
														</c:when>
														<c:when test="${r == 1}">
															<c:set var="entity" value="${element.childElements.get(c-1)}" />
																												
															<c:if test="${entity.getType() == 'Text'}">
																<div>${form.getQuestionTitle(entity)}</div>
															</c:if>
															
															<c:if test="${entity.getType() == 'Image'}">
																<div style="width: 100%; text-align:${entity.align};"><img src="${entity.url}" alt="${entity.title}" /></div>
															</c:if>
														</c:when>
														
														<c:when test="${c == 1}">
															<c:set var="entity" value="${element.getChildElementsOrdered().get(element.columns + r - 2)}" />
														
															<c:if test="${entity.getType() == 'Text'}">
																<c:if test="${!entity.getOptional()}">
																	<span class="mandatory" style="position: absolute; margin-left: 53px; margin-top: 2px;">*</span>
																</c:if>
																<div>${form.getQuestionTitle(entity)}</div>
															</c:if>
															
															<c:if test="${entity.getType() == 'Image'}">
																<div style="width: 100%; text-align:${entity.align};"><img src="${entity.url}" alt="${entity.title}" /></div>
															</c:if>
															
															<c:if test="${form.getValidationMessage(entity).length() > 0}">
																<div style="color: #f00" class="validation-error-server">${form.getValidationMessage(entity)}</div>
															</c:if>
															
														</c:when>
														
														<c:otherwise>
														
															<c:set var="answer" value="${element.childElements.get(c-1)}" />
															<c:set var="question" value="${element.getChildElementsOrdered().get(element.columns + r - 2)}" />
														
															<label for="${question.id}${answer.id}" class="hideme">Matrix answer row ${r} column ${c}</label>
															
															<c:set var="counterordered" value="${(question.position-element.columns)*(element.columns-1)+answer.position-1}" />	
																															
															<c:choose>
																<c:when test="${element.isSingleChoice}">
																	<c:choose>
																		<c:when test="${(form.getValues(question).contains(answer.id.toString()) || form.getValues(question).contains(answer.uniqueId))}">
																			<img align="middle" src="${contextpath}/resources/images/radiobuttonchecked.png" />
																		</c:when>
																		<c:otherwise>
																			<img align="middle" src="${contextpath}/resources/images/radiobutton.png" />
																		</c:otherwise>
																	</c:choose>																		
																</c:when>
																<c:otherwise>
																	<c:choose>
																		<c:when test="${(form.getValues(question).contains(answer.id.toString()) || form.getValues(question).contains(answer.uniqueId))}">
																			<img align="middle" src="${contextpath}/resources/images/checkboxchecked.png" />
																		</c:when>
																		<c:otherwise>
																			<img align="middle" src="${contextpath}/resources/images/checkbox.png" />
																		</c:otherwise>
																	</c:choose>
																</c:otherwise>
															
															</c:choose>
															
															<c:set var="counter" value="${counter+1}" />		
														
														</c:otherwise>
													</c:choose>
													
												</td>											
											</c:forEach>									
										</tr>									
									</c:forEach>
								</table>
							</div>
						</c:if>
						
						<c:if test="${element.getType() == 'Table'}">
							<div class="questiontitle">${form.getQuestionTitle(element)}</div>
							<div class="questionhelp">${element.help}</div>
							
							<div>
								<table data-widths="${element.widths}" id="${element.id}" class="table_${element.id} tabletable" style="table-layout: fixed;">										
									<tbody>
										<c:forEach var="r" begin="1" end="${element.rows}"> 									
											<tr>									
												<c:forEach var="c" begin="1" end="${element.columns}"> 
													<c:choose>
														<c:when test="${(r == 1 || c == 1) && element.tableType == 2}">
															<td style="background-color: #eee; width: ${element.getWidth(c-1)}">
														</c:when>
														<c:when test="${r == 1 || c == 1}">
															<td style="background-color: #eee; padding-left: 10px">
														</c:when>
														<c:otherwise>
															<td style="padding: 2px">
														</c:otherwise>
													</c:choose>
														<c:choose>
															<c:when test="${r == 1 && c == 1}">
																<div>${element.firstCellText}</div>
															</c:when>
															<c:when test="${r == 1}"><c:set var="entity" value="${element.childElements.get(c-1)}" />${entity.title}</c:when>																
															<c:when test="${c == 1}">
																<c:set var="entity" value="${element.childElements.get(element.columns + r - 2)}" />
																<c:if test="${!entity.getOptional()}">
																	<span class="mandatory" style="position: absolute; margin-left: 49px; margin-top: 3px;">*</span>
																</c:if>
																${form.getQuestionTitle(entity)}
															</c:when>																
															<c:otherwise>
																<c:set var="answer" value="${element.childElements.get(c-1)}" />
																<c:set var="question" value="${element.childElements.get(element.columns + r - 2)}" />
																<pre class="prepdf" style="white-space: normal; margin: 0px; background-color: #fff; border: 0px;"><div style="word-wrap: break-word;">${form.answerSets[0].getTableAnswer(element, r-1, c-1, true)}</div></pre>
															</c:otherwise>
														</c:choose>															
													</td>											
												</c:forEach>									
											</tr>									
										</c:forEach>
									</tbody>	
								</table>
							
							</div>						

						</c:if>
						
						<c:if test="${element.getType() == 'ComplexTable'}">
							<div class="questiontitle">${form.getQuestionTitle(element)}</div>
							<div class="questionhelp">${element.help}</div>
							
							<div>
								<table id="${element.id}" class="table_${element.id} table complextable ${element.showHeadersAndBorders ? 'table-bordered' : ''}" style="width: auto">	
									<c:if test="${element.showHeadersAndBorders}">
										<tr>
											<c:forEach var="c" begin="0" end="${element.columns}"> 
												<c:set var="child" value="${element.getChildAt(0, c)}" />
												<th class="headercell cell">${child.title}</th>
											</c:forEach>
										</tr>
									</c:if>
									
									<c:forEach var="r" begin="1" end="${element.rows}"> 
										<c:set var="rowheader" value="${element.getChildAt(r, 0)}" />								
										<tr>
											<c:if test="${element.showHeadersAndBorders}">
												<th class="headercell cell">${rowheader.title}</th>
											</c:if>
											
											<c:forEach var="c" begin="1" end="${element.columns}"> 
												<c:if test="${element.isCellVisible(c, r)}">
													<c:set var="child" value="${element.getChildAt(r, c)}" />
													<td class="cell" colspan="${child == null ? 1 : child.columnSpan}">
														<c:choose>
															<c:when test="${child == null}">
																&nbsp;
															</c:when>
															<c:otherwise>
																<c:if test="${!child.getOptional()}">
																	<span class="mandatory">*</span>
																</c:if>
																<label for="input${child.id}"><span class="questiontitle">${form.getQuestionTitle(child)}</span></label>
																<c:choose>
																	<c:when test="${child.getCellType() == 'FreeText' && child.getMinCharacters() != null && child.getMinCharacters() > 0 && child.getMaxCharacters() != null && child.getMaxCharacters() > 0}">
																		<div class='limits'>${form.getMessage("limits.MinMaxCharacters", child.getMinCharacters(), child.getMaxCharacters())}&nbsp;<span class="charactercounter"></span></div>
																	</c:when>
																	<c:when test="${child.getCellType() == 'FreeText' && child.getMinCharacters() != null && child.getMinCharacters() > 0}">
																		<div class='limits'>${form.getMessage("limits.MinCharacters", child.getMinCharacters())}&nbsp;<span class="charactercounter"></span></div>
																	</c:when>
																	<c:when test="${child.getCellType() == 'FreeText' && child.getMaxCharacters() != null && child.getMaxCharacters() > 0}">
																		<div class='limits'>${form.getMessage("limits.MaxCharacters", child.getMaxCharacters())}&nbsp;<span class="charactercounter"></span></div>
																	</c:when>
																	<c:when test="${child.getCellType() == 'MultipleChoice' && child.getMinChoices() != null && child.getMinChoices() > 0 && child.getMaxChoices() != null && child.getMaxChoices() > 0}">
																		<div class='limits'>${form.getMessage("limits.MinMaxChoicesNew", child.getMinChoices(), child.getMaxChoices())}</div>
																	</c:when>
																	<c:when test="${child.getCellType() == 'MultipleChoice' && child.getMinChoices() != null && child.getMinChoices() > 0}">
																		<div class='limits'>${form.getMessage("limits.MinChoicesNew", child.getMinChoices())}</div>
																	</c:when>
																	<c:when test="${child.getCellType() == 'MultipleChoice' && child.getMaxChoices() != null && child.getMaxChoices() > 0}">
																		<div class='limits'>${form.getMessage("limits.MaxChoicesNew", child.getMaxChoices())}</div>
																	</c:when>
																	<c:when test="${child.getCellType() == 'Number' && child.getMin() != null && child.getMax() != null}">
																		<div class='limits'>${form.getMessage("limits.MinMaxNumber", child.getMinString(), child.getMaxString())}</div>
																	</c:when>
																	<c:when test="${child.getCellType() == 'Number' && child.getMin() != null}">
																		<div class='limits'>${form.getMessage("limits.MinNumber", child.getMinString())}</div>
																	</c:when>
																	<c:when test="${child.getCellType() == 'Number' && child.getMax() != null}">
																		<div class='limits'>${form.getMessage("limits.MaxNumber", child.getMaxString())}</div>
																	</c:when>								
																	<c:when test="${child.getCellType() == 'Formula' && child.getMin() != null && child.getMax() != null}">
																		<div class='limits'>${form.getMessage("limits.MinMaxNumber", child.getMinString(), child.getMaxString())}</div>
																	</c:when>
																	<c:when test="${child.getCellType() == 'Formula' && child.getMin() != null}">
																		<div class='limits'>${form.getMessage("limits.MinNumber", child.getMinString())}</div>
																	</c:when>
																	<c:when test="${child.getCellType() == 'Formula' && child.getMax() != null}">
																		<div class='limits'>${form.getMessage("limits.MaxNumber", child.getMaxString())}</div>
																	</c:when>						
																</c:choose>
																
																<c:if test="${child.getHelp().length() > 0}">
																	<div class="questionhelp">${child.help}</div>
																</c:if>
																
																<c:choose>
																	<c:when test="${child.getCellType() == 'FreeText'}">
																		${form.getValueStripInvalidXML(child)}
																	</c:when>
																	<c:when test="${child.getCellType() == 'SingleChoice'}">
																		<c:choose>
																			<c:when test="${child.getUseRadioButtons() == false && form.getValues(child).size() > 0}">
																				<div class="answer-columns">
																					<div class="answer-column" style="word-wrap: break-word; border: 1px solid #bbb; padding: 5px; min-height: 20px;">
																						<div style="float: right"><i class="icon icon-chevron-down"></i></div>
																						<c:forEach items="${child.orderedPossibleAnswers}" var="possibleanswer">
																							<c:if test="${form.getValues(child).contains(possibleanswer.id.toString()) || form.getValues(child).contains(possibleanswer.uniqueId)}">
																								${possibleanswer.getTitleForDisplayMode(child.displayMode)}
																							</c:if>
																						</c:forEach>
																					</div>
																				</div>
																				<div style="clear: both"></div>
																			</c:when>
																			<c:otherwise>
																				<div class="answer-columns">
																					<table class="answers-table">
																						<tr class="hideme">
																							<th>radio button</th>
																							<th>label</th>
																						</tr>
																						<tr>
																							<c:forEach items="${child.orderedPossibleAnswers}" var="possibleanswer" varStatus="status">
																								<td style="vertical-align: top; border: 0 !important; padding-right: 0px; min-width: 1px;">
																									<c:choose>
																										<c:when test="${possibleanswer == null}"></c:when>
																										<c:when test="${(form.getValues(child).contains(possibleanswer.id.toString()) || form.getValues(child).contains(possibleanswer.uniqueId))}">
																											<div style="margin-bottom: 2px"><img align="middle" src="${contextpath}/resources/images/radiobuttonchecked.png" /></div>
																										</c:when>
																										<c:otherwise>
																											<div style="margin-bottom: 2px"><img align="middle" src="${contextpath}/resources/images/radiobutton.png" /></div>
																										</c:otherwise>												
																									</c:choose>
																								</td>
																								<td style="vertical-align: top; border: 0 !important; padding-right: 10px; padding-left: 0px;">
																									<label for="${possibleanswer.id}">
																										<div class="answertext" style="max-width: ${form.maxColumnWidth(child)}">${possibleanswer.getTitleForDisplayMode(child.displayMode)}</div>
																									</label>
																								</td>					
																								<c:if test="${(child.numColumns == 0 || status.count % child.numColumns == 0) && status.count < child.possibleAnswers.size()}">
																									</tr>
																									<tr>
																								</c:if>
																							</c:forEach>
																						</tr>			
																					</table>	
																				</div>
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:when test="${child.getCellType() == 'MultipleChoice'}">
																		<div class="answer-columns">
																			<c:choose>
																				<c:when test="${child.useCheckboxes || form.wcagCompliance}">											
																					<table class="answers-table">
																						<tr class="hideme">
																							<th>checkbox</th>
																							<th>label</th>
																						</tr>
																						<tr>
																							<c:forEach items="${child.orderedPossibleAnswers}" var="possibleanswer" varStatus="status">
																								<td style="vertical-align: top; border: 0 !important;">
																									<c:choose>
																										<c:when test="${possibleanswer == null}">
																										
																										</c:when>
																										<c:when test="${(form.getValues(child).contains(possibleanswer.id.toString()) || form.getValues(child).contains(possibleanswer.uniqueId))}">
																											<div style="margin-bottom: 2px"><img align="middle" src="${contextpath}/resources/images/checkboxchecked.png" /></div>
																										</c:when>
																										<c:otherwise>
																											<c:if test="${child.useCheckboxes || form.answerSets.size() == 0}">
																												<div style="margin-bottom: 2px"><img align="middle" src="${contextpath}/resources/images/checkbox.png" /></div>
																											</c:if>
																										</c:otherwise>												
																									</c:choose>
																								</td>			
																								<td style="vertical-align: top; border: 0 !important;">
																									<label for="${possibleanswer.id}"><div class="answertext" style="max-width: ${form.maxColumnWidth(child)}">${possibleanswer.title}</div></label>
																								</td>				
																								<c:if test="${(child.numColumns == 0 || status.count % child.numColumns == 0) && status.count < child.possibleAnswers.size()}">
																									</tr>
																									<tr>
																								</c:if>
																							</c:forEach>
																						</tr>			
																					</table>	
																				</c:when>
																				<c:otherwise>
																					<div class="answer-column">													
																						<ul class="${child.css} multiple-choice" style="max-height: none;">
																							<c:forEach items="${child.orderedPossibleAnswers}" var="possibleanswer">															
																								<c:choose>
																									<c:when test="${form.getValues(child).contains(possibleanswer.id.toString()) || form.getValues(child).contains(possibleanswer.uniqueId)}">
																										<li class="possible-answer trigger selected-choice" id="trigger${possibleanswer.id}">
																											<a>
																												<span class="answertext">${possibleanswer.getStrippedTitleNoEscape()}</span>
																											</a>
																			 								<input id="${possibleanswer.id}" data-id="${child.id}${possibleanswer.id}" checked="checked" value="${possibleanswer.id}" style="display: none" type="checkbox" name="answer${child.id}" />
																										</li>
																									</c:when>
																									<c:otherwise>
																										<li class="possible-answer trigger" id="trigger${possibleanswer.id}">
																											<a>
																												<span class="answertext">${possibleanswer.getStrippedTitleNoEscape()}</span>
																											</a>
																			 								<input id="${possibleanswer.id}" data-id="${element.id}${possibleanswer.id}" value="${possibleanswer.id}" style="display: none" type="checkbox" name="answer${child.id}" />
																										</li>
																									</c:otherwise>
																								</c:choose>																									
																							</c:forEach>	
																						</ul>			
																					</div>
																					<div style="clear: both"></div>
																				</c:otherwise>
																			</c:choose>
																		</div>
																	</c:when>
																	<c:when test="${child.getCellType() == 'Number'}">
																		<div>
																			<div style="float: left; width: 206px; word-wrap: break-word; border: 1px solid #bbb; padding: 5px; min-height: 20px;"><esapi:encodeForHTML>${form.getValue(child)}</esapi:encodeForHTML></div>
																			<div style="float: left" class="unit-text">${child.unit}</div>	
																			<div style="clear: both"></div>		
																		</div>																		
																	</c:when>
																	<c:when test="${child.getCellType() == 'Formula'}">
																		<div style="width: 206px; border: 1px solid #bbb; padding: 5px; min-height: 20px;"><esapi:encodeForHTML>${form.getValue(child)}</esapi:encodeForHTML></div>
																	</c:when>
																</c:choose>				
															</c:otherwise>
														</c:choose>
													</td>
												</c:if>
											</c:forEach>
										</tr>
									</c:forEach>									
								</table>
							
							</div>						

						</c:if>
						
						<c:if test="${element.getType() == 'Upload'}">			
							<input type="hidden" name="answer${element.id}"	value="files" />				
							<div class="questiontitle">${form.getQuestionTitle(element)}</div>							
							
							<div class="questionhelp">${element.help}</div>	
							<c:if test="${element.getExtensions() != null && element.getExtensions().length() > 0}">
								<div class="questionhelp">
									<span>${form.getMessage("info.extensions", element.getExtensions().replace(";",","))}</span>
								</div>
							</c:if>							
							
							<div class="uploaded-files">										
								<c:if test="${form.answerSets.size() > 0}">
									<c:set var="answers" value="${form.answerSets[0].getAnswers(element.uniqueId)}" />
									<c:if test="${answers.size() > 0}">												
										<c:forEach items="${answers}" var="answer">	
											<c:forEach items="${answer.files}" var="file" varStatus="rowCounter">		
												<div>${file.uid}/<esapi:encodeForHTML>${file.name}</esapi:encodeForHTML></div>			
											</c:forEach>
										</c:forEach>												
		 							</c:if>											
								</c:if>										
							</div>				
							<div class="${element.css} file-uploader" style="margin-left: 120px; margin-top: 10px;" data-id="${element.id}">
							</div>								
						</c:if>
						
						<c:if test="${element.getType() == 'Download'}">
							<div class="questiontitle">${form.getQuestionTitle(element)}</div>
							<div class="questionhelp">${element.help}</div>									
							<div class="files">
								<c:forEach items="${element.files}" var="file">
									<a class="visiblelink" target="_blank" href="${serverprefix}files/${form.survey.uniqueId}/${file.uid}" style="margin-left: 5px;"><esapi:encodeForHTML>${file.name}</esapi:encodeForHTML></a> <br />
								</c:forEach>
							</div>															
							
						</c:if>
						
						<c:if test="${element.getType() == 'Confirmation'}">
							<div class="questiontitle confirmationelement">${form.getQuestionTitle(element)}</div>																			
								<c:if test="${element.usetext}">
									<div style="margin-left: 20px;">
										${element.confirmationtext}
									</div>
								</c:if>
								<c:if test="${element.isUseupload()}">
									<div class="files" style="margin-left: 40px; margin-top: 10px;">
										<c:forEach items="${element.files}" var="file">
											<a class="visiblelink" target="_blank" href="${serverprefix}files/${form.survey.uniqueId}/${file.uid}"><esapi:encodeForHTML>${file.name}</esapi:encodeForHTML></a> <br />
										</c:forEach>
									</div>			
								</c:if>	
						</c:if>
						
						<c:if test="${element.getType() == 'EmailQuestion'}">
							<div style="border: 1px solid #bbb; padding: 5px; min-height: 20px; margin-left: 20px;"><esapi:encodeForHTML>${form.getValue(element)}</esapi:encodeForHTML></div>											
						</c:if>
						
						<c:if test="${element.getType() == 'GalleryQuestion'}">
							<div class="gallery-div" style="width: 100%; text-align:left; background-color: #fff;">	
								<c:choose>
									<c:when test="${element.files.size() == 0}">[please upload images]</c:when>
									<c:otherwise>
										<table class="gallery-table limit${element.limit}">				
											<tr>
												<c:forEach items="${element.files}" var="file" varStatus="counter">
													<td data-uid="${file.uid}" style="vertical-align: top">
														<div class="galleryinfo">
															<c:if test="${element.selection}">																			
																<c:choose>
																	<c:when test="${form.getValues(element).contains(counter.index.toString())}">
																		<img align="middle" src="${contextpath}/resources/images/checkboxchecked.png" />
																	</c:when>
																	<c:otherwise>
																		<img align="middle" src="${contextpath}/resources/images/checkbox.png" />
																	</c:otherwise>		
																</c:choose>																				
															</c:if>
															<c:if test="${element.numbering}">	
																<span>${counter.index + 1}.</span>
															</c:if>
															<esapi:encodeForHTML>${file.name.replace("%20"," ")}</esapi:encodeForHTML>
														</div>
														<a>																	
															<fmt:formatNumber var="widthRounded" value="${(600 - 20 - (element.columns*30))/element.columns}" maxFractionDigits="0" />
															<img class="gallery-image" alt="<esapi:encodeForHTMLAttribute>${file.cleanComment}</esapi:encodeForHTMLAttribute>" src="${contextpath}/files/${form.survey.uniqueId}/${file.uid}" data-width="${file.width}" data-original-width="${widthRounded}" width="${widthRounded}px" style="width: ${widthRounded}px; max-width: none;" />	
														</a>
														<div class="comment">${file.comment}</div>							
													</td>
													<c:if test="${(counter.index+1) % element.columns == 0 && (counter.index+1) < element.files.size()}">
														</tr>
														<tr>
													</c:if>		
												</c:forEach>
											</tr>
										</table>
										
									</c:otherwise>		
								</c:choose>									
							</div>
						</c:if> 
						
						<c:if test="${(element.getType() == 'FreeTextQuestion' || element.getType() == 'RegExQuestion')}">
							<pre class="prepdf" style="white-space: pre-wrap; margin-left: 20px;"><div style="word-wrap: break-word; min-height: ${element.getNumRows()*20}px;">${form.getValueStripInvalidXML(element)}</div></pre>
						</c:if>
						
						<c:if test="${element.getType() == 'NumberQuestion'}">								
							<div>
								<div style="float: left; width: 206px; word-wrap: break-word; border: 1px solid #bbb; padding: 5px; min-height: 20px; margin-left: 20px;"><esapi:encodeForHTML>${form.getValue(element)}</esapi:encodeForHTML></div>						
								<div style="float: left" class="unit-text">${element.unit}</div>	
								<div style="clear: both"></div>		
							</div>	
						</c:if>
						
						<c:if test="${element.getType() == 'FormulaQuestion'}">								
							<div>
								<div style="width: 206px; border: 1px solid #bbb; padding: 5px; min-height: 20px; margin-left: 20px;"><esapi:encodeForHTML>${form.getValue(element)}</esapi:encodeForHTML></div>					
							</div>	
						</c:if>
						
						<c:if test="${element.getType() == 'DateQuestion' || element.getType() == 'TimeQuestion'}">
							<div style="width: 206px; word-wrap: break-word; border: 1px solid #bbb; padding: 5px; min-height: 20px; margin-left: 20px;">${form.getValue(element)}</div>                                                                                    
                        </c:if>
							
						<c:if test="${element.getType() == 'MultipleChoiceQuestion'}">
							<div class="answer-columns">
								<c:choose>
									<c:when test="${element.useCheckboxes || form.wcagCompliance}">											
										<table class="answers-table">
											<tr class="hideme">
												<th>checkbox</th>
												<th>label</th>
											</tr>
											<tr>
												<c:forEach items="${element.orderedPossibleAnswers}" var="possibleanswer" varStatus="status">
													<td style="vertical-align: top">
														<c:choose>
															<c:when test="${possibleanswer == null}">
															
															</c:when>
															<c:when test="${(form.getValues(element).contains(possibleanswer.id.toString()) || form.getValues(element).contains(possibleanswer.uniqueId))}">
																<div style="margin-bottom: 2px"><img align="middle" src="${contextpath}/resources/images/checkboxchecked.png" /></div>
															</c:when>
															<c:otherwise>
																<c:if test="${element.useCheckboxes || form.answerSets.size() == 0}">
																	<div style="margin-bottom: 2px"><img align="middle" src="${contextpath}/resources/images/checkbox.png" /></div>
																</c:if>
															</c:otherwise>												
														</c:choose>
													</td>			
													<td style="vertical-align: top">
														<label for="${possibleanswer.id}"><div class="answertext" style="max-width: ${form.maxColumnWidth(element)}">${possibleanswer.title}</div></label>
													</td>				
													<c:if test="${(element.numColumns == 0 || status.count % element.numColumns == 0) && status.count < element.possibleAnswers.size()}">
														</tr>
														<tr>
													</c:if>
												</c:forEach>
											</tr>			
										</table>	
									</c:when>
									<c:otherwise>
										<div class="answer-column">													
											<ul class="${element.css} multiple-choice" style="max-height: none;">
												<c:forEach items="${element.orderedPossibleAnswers}" var="possibleanswer">															
													<c:choose>
														<c:when test="${form.getValues(element).contains(possibleanswer.id.toString()) || form.getValues(element).contains(possibleanswer.uniqueId)}">
															<li class="possible-answer trigger" id="trigger${possibleanswer.id}" style="background-color: #bbb;">
																<a>
																	<span class="answertext">${possibleanswer.getStrippedTitleNoEscape()}</span>
																</a>
								 								<input id="${possibleanswer.id}" data-id="${element.id}${possibleanswer.id}" checked="checked" data-dependencies="${possibleanswer.dependentElementsString}" value="${possibleanswer.id}" style="display: none" type="checkbox" name="answer${element.id}" />
															</li>	
														</c:when>
														<c:otherwise>
															<li class="possible-answer trigger" id="trigger${possibleanswer.id}">
																<a>
																	<span class="answertext">${possibleanswer.getStrippedTitleNoEscape()}</span>
																</a>
																<input id="${possibleanswer.id}" data-id="${element.id}${possibleanswer.id}" data-dependencies="${possibleanswer.dependentElementsString}" value="${possibleanswer.id}" style="display: none" type="checkbox" name="answer${element.id}" />
															</li>	
														</c:otherwise>
													</c:choose>																									
												</c:forEach>	
											</ul>			
										</div>
										<div style="clear: both"></div>
									</c:otherwise>
								</c:choose>
							</div>
						</c:if>
						
						<c:if test="${element.getType() == 'SingleChoiceQuestion'}">
							<c:choose>
								<c:when test="${element.getType() == 'SingleChoiceQuestion' && element.getIsTargetDatasetQuestion() == true && form.getValues(element).size() > 0}">
									<div class="answer-columns">
										<div class="answer-column" style="word-wrap: break-word; border: 1px solid #bbb; padding: 5px; min-height: 20px;">
											<div style="float: right"><i class="icon icon-chevron-down"></i></div>
											<c:forEach items="${element.targetDatasets}" var="dataset">												
												<c:if test="${form.getValues(element).contains(dataset.id.toString())}">
													${dataset.name}
												</c:if>																																
											</c:forEach>			
										</div>
									</div>
									<div style="clear: both"></div>
								</c:when>					
								<c:when test="${element.getType() == 'SingleChoiceQuestion' && element.getUseRadioButtons() == false && form.getValues(element).size() > 0 && form.wcagCompliance == false}">
									<div class="answer-columns">
										<div class="answer-column" style="word-wrap: break-word; border: 1px solid #bbb; padding: 5px; min-height: 20px;">
											<div style="float: right"><i class="icon icon-chevron-down"></i></div>
											<c:forEach items="${element.orderedPossibleAnswers}" var="possibleanswer">												
												<c:if test="${form.getValues(element).contains(possibleanswer.id.toString()) || form.getValues(element).contains(possibleanswer.uniqueId)}">
													${possibleanswer.getTitleForDisplayMode(element.displayMode)}
												</c:if>																																
											</c:forEach>			
										</div>
									</div>
									<div style="clear: both"></div>
								</c:when>
								<c:otherwise>
									<div class="answer-columns">
										<table class="answers-table">
											<tr class="hideme">
												<th>radio button</th>
												<th>label</th>
											</tr>
											<tr>
												<c:forEach items="${element.orderedPossibleAnswers}" var="possibleanswer" varStatus="status">
													<td style="vertical-align: top">
														<c:choose>
															<c:when test="${possibleanswer == null}"></c:when>
															<c:when test="${(form.getValues(element).contains(possibleanswer.id.toString()) || form.getValues(element).contains(possibleanswer.uniqueId))}">
																<div style="margin-bottom: 2px"><img align="middle" src="${contextpath}/resources/images/radiobuttonchecked.png" /></div>
															</c:when>
															<c:otherwise>
																<div style="margin-bottom: 2px"><img align="middle" src="${contextpath}/resources/images/radiobutton.png" /></div>
															</c:otherwise>												
														</c:choose>
													</td>
													<td style="vertical-align: top">
														<label for="${possibleanswer.id}">
															<div class="answertext" style="max-width: ${form.maxColumnWidth(element)}">${possibleanswer.getTitleForDisplayMode(element.displayMode)}</div>
														</label>
													</td>					
													<c:if test="${(element.numColumns == 0 || status.count % element.numColumns == 0) && status.count < element.possibleAnswers.size()}">
														</tr>
														<tr>
													</c:if>
												</c:forEach>
											</tr>			
										</table>	
									</div>
								</c:otherwise>
							</c:choose>
						</c:if>						
					
						<c:if test="${element.getType() == 'RankingQuestion'}">
							<div class="ranking-question-initial-answer-message">
								${form.getMessage("label.HintOnInitialRankingOrderPDF")}
							</div>
							<div class="rankingitem-list-container">
								<div class="rankingitem-list">
									<c:forEach items="${form.getRankingItems(element)}" var="child">			
										<div class="rankingitem-form-data">
											<table>
												<tr>
													<td>
														<div class="rankingitem-decoration" style="padding-top: 2px">
															<img src="${contextpath}/resources/images/drag.png" />
														</div>														
													</td>
													<td>
														<div class="rankingitemtext">${child.title}</div>	
													</td>
												</tr>
											</table>							
										</div>
									</c:forEach>
								</div>
							</div>
						</c:if>
					
						<c:if test="${form.getValidationMessage(element).length() > 0}">
							<div style="color: #f00" class="validation-error-server"><esapi:encodeForHTML>${form.getValidationMessage(element)}</esapi:encodeForHTML></div>
						</c:if>
						
						</div>
						
						</c:if>
						
						</div>
						
						</fieldset>
					
					</c:forEach>								
					
				</div>
			
		</c:forEach>
		
	</div>
