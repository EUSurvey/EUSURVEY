<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Survey" /></title>
	<%@ include file="../includes.jsp" %>
	<link href="${contextpath}/resources/css/fileuploader.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/runner.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${contextpath}/resources/js/Chart.min.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/runner.js?version=<%@include file="../version.txt" %>"></script>
	
	<c:if test="${form.survey.skin != null && !form.wcagCompliance}">
		<style type="text/css">
			${form.survey.skin.getCss()}
		</style>
	</c:if>
	
	<style type="text/css">
	
		body {
			-webkit-print-color-adjust:exact;
		}
	
		.sectiontitle {
			border-color: #777;
		}
	
		.dataTable table {
			border-top: 1px solid #ccc;
			border-left: 1px solid #ccc;
		}
				
		.dataTable td {
			border-bottom: 1px solid #ccc;
			border-right: 1px solid #ccc;
			padding: 5px;
		}
		
		.limits {
			color: #333;
		}
		
		pre {
			margin-left: 20px;
		}
		
		.survey-element {
			max-width: none;
			overflow-x: visible;
		}
		
		div {
			word-break: break-word;
		}
		
		.complextable.table-bordered .answers-table td {
			border: 0 !important;
		}
		
		.complextable.table-bordered .answer-columns {
			padding-left: 0;
		}

		.complextable pre {
			margin-left: 0;
		}

		@media print {
		/* Removes hundreads of blank pages in Chrome 108 */
			.chartjs-size-monitor {
				display:none !important;
			}
		}
	
	</style>
	
	<script type="text/javascript"> 
		$(function() {	
			$(".handsontableInput").hide();
			if (launchPrint === "true") {
				setTimeout(function(){ window.print(); }, 3000);
			}
			$("input[type=checkbox]").attr("disabled","disabled");
		});			
	</script>
	
	<script type="text/javascript">
		var uniqueCode = "${code}"
		var contextpath = "${contextpath}"
		var launchPrint = "${launchPrint}"
	</script>
</head>
<body id="printFromContribution">

	<div style="margin: 20px;">
		<spring:message code="label.ContributionId" />: <esapi:encodeForHTML>${code}</esapi:encodeForHTML><br/>
		<spring:message code="label.Date" />: <esapi:encodeForHTML>${submittedDate}</esapi:encodeForHTML><br/><br />
		
		<div class="surveytitle" style="color: #000;">${form.survey.title}</div>
		
		<c:if test="${form.survey.containsMandatoryQuestion()}">
			<div style="margin-top: 10px;">
				<spring:message code="message.StarMandatory" />
			</div>
		</c:if>
		
		<c:if test="${form.survey.logo != null}">
			<img src="<c:url value="/files/${form.survey.uniqueId}/${form.survey.logo.uid}" />" alt="logo" />
			<hr style="margin-top: 15px;" />
		</c:if>	
					
		<span class="introduction">${form.survey.introduction}</span>
		<c:if test="${form.survey.isECF}">
			<div id="canvasContainerLeft"> 
				<%@ include file="../ecfGraph.jsp" %>
			</div>
		</c:if>
		<c:forEach var="page" items="${form.getPages()}" varStatus="rowCounter">
					
			<c:forEach var="element" items="${page}">
								
				<c:choose>
					<c:when test="${element.isElementHidden() || (invisibleElements != null && invisibleElements.contains(element.uniqueId))}">
						<div class="survey-element" id="${element.id}" style="margin: 5px; margin-top: 25px; display: none;">
					</c:when>
					<c:otherwise>
						<div class="survey-element" id="${element.id}" style="margin: 5px; margin-top: 25px;">
					</c:otherwise>
				</c:choose>
							
				<c:if test="${element.getType() == 'Section'}">
					<hr class="hr2" />									
					<div class="sectiontitle section<esapi:encodeForHTMLAttribute>${element.level}</esapi:encodeForHTMLAttribute>" style="color: #000;">${form.getSectionTitle(element)}</div>
				</c:if>
								
				<c:if test="${element.getType() == 'Text'}">
					<div>${element.title}</div>
				</c:if>
				
				<c:if test="${element.getType() == 'Image'}">
					<div class="alignment-div" style="width: 100%; text-align:${element.align};">																			
						<c:choose>							
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
						<span class="mandatory" style="position: absolute; margin-left: -7px; margin-top: 3px;">*</span>
					</c:if>
				
					<div class="questiontitle">${form.getQuestionTitle(element)}</div>
					<c:choose>
						<c:when test="${element.getType() == 'FreeTextQuestion' && element.getMinCharacters() != null && element.getMinCharacters() > 0 && element.getMaxCharacters() != null && element.getMaxCharacters() > 0}">
							<div class='limits'><spring:message code="limits.MinMaxCharacters" arguments="${element.getMinCharacters()},${element.getMaxCharacters()}" /></div>
						</c:when>
						<c:when test="${element.getType() == 'FreeTextQuestion' && element.getMinCharacters() != null && element.getMinCharacters() > 0}">
							<div class='limits'><spring:message code="limits.MinCharacters" arguments="${element.getMinCharacters()}" /></div>
						</c:when>
						<c:when test="${element.getType() == 'FreeTextQuestion' && element.getMaxCharacters() != null && element.getMaxCharacters() > 0}">
							<div class='limits'><spring:message code="limits.MaxCharacters" arguments="${element.getMaxCharacters()}" /></div>
						</c:when>
						<c:when test="${element.getType() == 'MultipleChoiceQuestion' && element.getMinChoices() != null && element.getMinChoices() > 0 && element.getMaxChoices() != null && element.getMaxChoices() > 0}">
							<div class='limits'><spring:message code="limits.MinMaxChoicesNew" arguments="${element.getMinChoices()},${element.getMaxChoices()}" /></div>
						</c:when>
						<c:when test="${element.getType() == 'MultipleChoiceQuestion' && element.getMinChoices() != null && element.getMinChoices() > 0}">
							<div class='limits'><spring:message code="limits.MinChoicesNew" arguments="${element.getMinChoices()}" /></div>
						</c:when>
						<c:when test="${element.getType() == 'MultipleChoiceQuestion' && element.getMaxChoices() != null && element.getMaxChoices() > 0}">
							<div class='limits'><spring:message code="limits.MaxChoicesNew" arguments="${element.getMaxChoices()}" /></div>
						</c:when>
						<c:when test="${element.getType() == 'NumberQuestion' && element.getDisplay() != 'Slider' && element.getMin() != null && element.getMin() > 0 && element.getMax() != null && element.getMax() > 0}">
							<div class='limits'><spring:message code="limits.MinMaxNumber" arguments="${element.getMinString()},${element.getMaxString()}" /></div>
						</c:when>
						<c:when test="${element.getType() == 'NumberQuestion' && element.getDisplay() != 'Slider' && element.getMin() != null && element.getMin() > 0}">
							<div class='limits'><spring:message code="limits.MinNumber" arguments="${element.getMinString()}" /></div>
						</c:when>
						<c:when test="${element.getType() == 'NumberQuestion' && element.getDisplay() != 'Slider' && element.getMax() != null && element.getMax() > 0}">
							<div class='limits'><spring:message code="limits.MaxNumber" arguments="${element.getMaxString()}" /></div>
						</c:when>
						<c:when test="${element.getType() == 'DateQuestion' && element.getMin() != null && element.getMax() != null}">
							<div class='limits'><spring:message code="limits.MinMaxDate" arguments="${element.getMinString()},${element.getMaxString()}" /></div>
						</c:when>
						<c:when test="${element.getType() == 'DateQuestion' && element.getMin() != null}">
							<div class='limits'><spring:message code="limits.MinDate" arguments="${element.getMinString()}" /></div>
						</c:when>
						<c:when test="${element.getType() == 'DateQuestion' && element.getMax() != null}">
							<div class='limits'><spring:message code="limits.MaxDate" arguments="${element.getMaxString()}" /></div>
						</c:when>						
						<c:when test="${element.getType() == 'TimeQuestion' && element.getMin() != null && element.getMin().length() > 0 && element.getMax() != null && element.getMax().length() > 0}">
							<div class='limits'><spring:message code="limits.MinMaxDate" arguments="${element.getMin()},${element.getMax()}" /></div>
						</c:when>
						<c:when test="${element.getType() == 'TimeQuestion' && element.getMin() != null && element.getMin().length() > 0}">
							<div class='limits'><spring:message code="limits.MinDate" arguments="${element.getMin()}" /></div>
						</c:when>
						<c:when test="${element.getType() == 'TimeQuestion' && element.getMax() != null && element.getMax().length() > 0}">
							<div class='limits'><spring:message code="limits.MaxDate" arguments="${element.getMax()}" /></div>
						</c:when>
					</c:choose>
				</c:if>					
											
				<c:if test="${element.getType().endsWith('Question') && element.getHelp().length() > 0}">
					<div class="questionhelp">${element.help}</div>
				</c:if>
				
				<c:if test="${element.getType() == 'GalleryQuestion'}">
					<div class="gallery-div" style="width: 100%; text-align:left;">	
						<c:choose>
							<c:when test="${element.files.size() == 0}">[please upload images]</c:when>
							<c:otherwise>
								<table class="gallery-table limit${element.limit}">				
									<tr>
										<c:forEach items="${element.files}" var="file" varStatus="counter">
											<td>
												<div class="galleryinfo">
													<c:if test="${element.selection}">
														<c:choose>
															<c:when test="${form.getValues(element).contains(counter.index.toString())}">
																<input disabled="disabled" readonly="readonly" checked="checked" class="${element.css}" type="checkbox" value="${counter.index}" name="answer${element.id}" />
															</c:when>
															<c:otherwise>
																<input disabled="disabled" readonly="readonly" class="${element.css}" type="checkbox" value="${counter.index}" name="answer${element.id}" />
															</c:otherwise>		
														</c:choose>	
													</c:if>
													<esapi:encodeForHTML>${file.name.replace("%20"," ")}</esapi:encodeForHTML>
												</div>
												<a href="" target="_blank">
													<img class="gallery-image" src="${contextpath}/files/${form.survey.uniqueId}/${file.uid}" data-width="${file.width}" width="150px" />								
												</a>
												<div class="comment"><esapi:encodeForHTML>${file.comment}</esapi:encodeForHTML></div>	
											</td>
											<c:if test="${(counter.index+1) % element.columns == 0 && counter.index < element.files.size()}">
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
				
				<c:if test="${element.getType() == 'Matrix'}">
					<div class="questiontitle">${form.getQuestionTitle(element)}</div>
					<div class="questionhelp">${element.help}</div>						
					<table class="matrixtable">					
															
						<c:forEach var="r" begin="1" end="${element.rows}"> 
						
							<c:choose>
								<c:when test="${r > 1}">
									<c:set var="matrixquestion" value="${element.getChildElementsOrdered().get(element.columns + r - 2)}" />
									<c:choose>
										<c:when test="${matrixquestion.isDependentMatrixQuestion && ((invisibleElements == null && forpdf == null) || invisibleElements.contains(matrixquestion.uniqueId))}">
											<tr style="display: none;">
										</c:when>
										<c:otherwise>
										   <tr>
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
											<td style="width: ${element.getWidth(c-1)}">		
										</c:when>
										<c:otherwise>
											<td>		
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
													<div> style="width: 100%; text-align:${entity.align};"<img src="${entity.url}" alt="${entity.title}" /></div>
												</c:if>
											</c:when>
											
											<c:when test="${c == 1}">
												<c:set var="entity" value="${element.getChildElementsOrdered().get(element.columns + r - 2)}" />
											
												<c:if test="${entity.getType() == 'Text'}">
													<c:if test="${!entity.getOptional()}">
														<span class="mandatory" style="position: absolute; margin-left: -7px; margin-top: 3px;">*</span>
													</c:if>
													<div>${form.getQuestionTitle(entity)}</div>
												</c:if>
												
												<c:if test="${entity.getType() == 'Image'}">
													<div style="width: 100%; text-align:<esapi:encodeForHTMLAttribute>${entity.align}</esapi:encodeForHTMLAttribute>;"><img src="<esapi:encodeForHTMLAttribute>${entity.url}</esapi:encodeForHTMLAttribute>" alt="<esapi:encodeForHTMLAttribute>${entity.title}</esapi:encodeForHTMLAttribute>" /></div>
												</c:if>
												
												<c:if test="${form.getValidationMessage(entity).length() > 0}">
													<div style="color: #f00" class="error"><esapi:encodeForHTML>${form.getValidationMessage(entity)}</esapi:encodeForHTML></div>
												</c:if>
												
											</c:when>											
											<c:otherwise>
											
												<c:set var="answer" value="${element.childElements.get(c-1)}" />
												<c:set var="question" value="${element.getChildElementsOrdered().get(element.columns + r - 2)}" />
											
												<c:choose>
													<c:when test="${element.isSingleChoice}">
														<c:choose>
															<c:when test="${form.getValues(question).contains(answer.id.toString())}">
																<input disabled="disabled" readonly="readonly" checked="checked" id="${question.id}" class="${element.css}" type="radio" name="answer${question.id}" value="${answer.id}" />
															</c:when>
															<c:otherwise>
																<input disabled="disabled" readonly="readonly" id="${question.id}" class="${element.css} " type="radio" name="answer${question.id}" value="${answer.id}" />
															</c:otherwise>		
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${form.getValues(question).contains(answer.id.toString())}">
																<input disabled="disabled" readonly="readonly" checked="checked" id="${question.id}" class="${element.css}" type="checkbox" name="answer${question.id}" value="${answer.id}" />
															</c:when>
															<c:otherwise>
																<input disabled="disabled" readonly="readonly" id="${question.id}" class="${element.css}" type="checkbox" name="answer${question.id}" value="${answer.id}" />
															</c:otherwise>														
														</c:choose>
													</c:otherwise>
												
												</c:choose>
											
											</c:otherwise>
										</c:choose>
										
									</td>											
								</c:forEach>									
							</tr>									
						</c:forEach>
					</table>							
				
				</c:if>
								
				<c:if test="${element.getType() == 'Table'}">
					<div class="questiontitle">${form.getQuestionTitle(element)}</div>
					<div class="questionhelp">${element.help}</div>
			
					<div class="dataTable"></div>
					
					<c:choose>
						<c:when test="${element.tableType == 0}">
							<table data-widths="${element.widths}" style="width: auto; max-width: auto" id="${element.id}" class="tabletable" data-readonly="true">	
						</c:when>
						<c:when test="${element.tableType == 1}">
							<table data-widths="${element.widths}" style="width: 900px" id="${element.id}" class="tabletable" data-readonly="true">	
						</c:when>
						<c:otherwise>
							<table data-widths="${element.widths}" style="width: auto; max-width: auto" id="${element.id}" class="tabletable" data-readonly="true">	
						</c:otherwise>
					</c:choose>
					
						<c:forEach var="r" begin="1" end="${element.rows}"> 									
							<tr>									
								<c:forEach var="c" begin="1" end="${element.columns}"> 		
									<c:choose>
										<c:when test="${(r == 1 || c == 1) && element.tableType == 2}">
											<td style="background-color: #eee; width: ${element.getWidth(c-1)}">
										</c:when>
										<c:when test="${r == 1 || c == 1}">
											<td style="background-color: #eee; padding-left: 10px;">
										</c:when>
										<c:otherwise>
											<td>
										</c:otherwise>
									</c:choose>
								
										<c:choose>
											<c:when test="${r == 1 && c == 1}">
												<div>${element.firstCellText}</div>
											</c:when>
											<c:when test="${r == 1}">
												<c:set var="entity" value="${element.childElements.get(c-1)}" />
												${entity.title}
											</c:when>																
											<c:when test="${c == 1}">
												<c:set var="entity" value="${element.childElements.get(element.columns + r - 2)}" />
												<c:if test="${!entity.getOptional()}">
													<span class="mandatory" style="position: absolute; margin-left: -7px; margin-top: 3px;">*</span>
												</c:if>
												${form.getQuestionTitle(entity)}
											</c:when>																
											<c:otherwise>
												<c:set var="answer" value="${element.childElements.get(c-1)}" />
												<c:set var="question" value="${element.childElements.get(element.columns + r - 2)}" />	
												<esapi:encodeForHTML>
												${form.answerSets[0].getTableAnswer(element, r-1, c-1, false)}								
												</esapi:encodeForHTML>
											</c:otherwise>
										</c:choose>															
									</td>											
								</c:forEach>									
							</tr>									
						</c:forEach>
					
					</table>						
				
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
																<pre style="white-space: pre-wrap; font-family: FreeMono, 'Courier New', Courier, monospace"><div style="word-wrap: break-word; margin-left: 0px;"><esapi:encodeForHTML>${form.getValue(child)}</esapi:encodeForHTML></div></pre>
															</c:when>
															<c:when test="${child.getCellType() == 'SingleChoice'}">
																<div class="answer-columns">
																	<c:choose>
																		<c:when test="${child.getUseRadioButtons() == false && form.getValues(child).size() > 0}">
																			<div class="answer-column" style="word-wrap: break-word; border: 1px solid #bbb; padding: 5px; min-height: 20px;">
																				<div style="float: right"><i class="icon icon-chevron-down"></i></div>
																				<c:forEach items="${child.orderedPossibleAnswers}" var="possibleanswer">												
																					<c:if test="${form.getValues(child).contains(possibleanswer.id.toString()) || form.getValues(child).contains(possibleanswer.uniqueId)}">
																						${possibleanswer.title}
																					</c:if>																																
																				</c:forEach>			
																			</div>
																		</c:when>
																		<c:otherwise>
																			<table class="answers-table">
																				<tr>
																					<c:forEach items="${child.orderedPossibleAnswers}" var="possibleanswer" varStatus="status">
																						<td style="vertical-align: top; padding-right: 0px;">
																							<c:choose>
																								<c:when test="${form.getValues(child).contains(possibleanswer.id.toString())}">
																									<input disabled="disabled" readonly="readonly" checked="checked" id="${possibleanswer.id}" class="${child.css}check " type="radio" name="answer${child.id}" value="${possibleanswer.id}" />
																								</c:when>
																								<c:otherwise>
																									<input disabled="disabled" readonly="readonly" id="${possibleanswer.id}" class="${child.css} check" type="radio" name="answer${child.id}" value="${possibleanswer.id}" />
																								</c:otherwise>														
																							</c:choose>
																						</td>
																						<td style="vertical-align: top; padding-left: 0; padding-right: 10px;">
																							<div class="answertext" style="max-width: ${form.maxColumnWidth(child)}">${possibleanswer.title}</div>
																						</td>						
																						<c:if test="${child.numColumns == 0 || (status.count % child.numColumns == 0)}">
																							</tr>
																							<tr>
																						</c:if>
																					</c:forEach>									
																				</tr>			
																			</table>
																		</c:otherwise>
																	</c:choose>
																</div>
																<div style="clear: both"></div>
															</c:when>
															<c:when test="${child.getCellType() == 'MultipleChoice'}">
																<div class="answer-columns">
																	<table class="answers-table">
																		<tr>
																			<c:forEach items="${child.orderedPossibleAnswers}" var="possibleanswer" varStatus="status">
																				<td style="vertical-align: top; border: 0 !important;">
																					<c:choose>
																						<c:when test="${form.getValues(child).contains(possibleanswer.id.toString())}">
																							<input disabled="disabled" readonly="readonly" checked="checked" id="${possibleanswer.id}" class="${child.css} check" type="checkbox" name="answer${child.id}" value="${possibleanswer.id}" />
																						</c:when>
																						<c:otherwise>
																							<input disabled="disabled" readonly="readonly" id="${possibleanswer.id}" class="${child.css} check" type="checkbox" name="answer${child.id}" value="${possibleanswer.id}" />
																						</c:otherwise>														
																					</c:choose>
																				</td>
																				<td style="vertical-align: top; border: 0 !important;">
																					<div class="answertext" style="max-width: ${form.maxColumnWidth(child)}">${possibleanswer.title}</div>
																				</td>						
																				<c:if test="${child.numColumns == 0 || (status.count % child.numColumns == 0)}">
																					</tr>
																					<tr>
																				</c:if>
																			</c:forEach>
																		</tr>			
																	</table>			
																</div>
															</c:when>
															<c:when test="${child.getCellType() == 'Number'}">
																<pre style="white-space: pre-wrap; font-family: FreeMono, 'Courier New', Courier, monospace"><div style="word-wrap: break-word; margin-left: 0px;"><esapi:encodeForHTML>${form.getValue(child)}</esapi:encodeForHTML><span class="unit-text"><esapi:encodeForHTML>${child.unit}</esapi:encodeForHTML></span></div></pre>
															</c:when>
															<c:when test="${child.getCellType() == 'Formula'}">
																<pre style="white-space: pre-wrap; font-family: FreeMono, 'Courier New', Courier, monospace"><div style="word-wrap: break-word; margin-left: 0px;"><esapi:encodeForHTML>${form.getValue(child)}</esapi:encodeForHTML></div></pre>
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
					<div class="questiontitle">${form.getQuestionTitle(element)}</div>
					<div class="uploaded-files">
						<c:if test="${answerSet != null}">
							<c:set var="answers" value="${form.answerSets[0].getAnswers(element.uniqueId)}" />
							<c:if test="${answers.size() > 0}">												
								<c:forEach items="${answers}" var="answer">	
									<c:forEach items="${answer.files}" var="file">					
										<esapi:encodeForHTML>${file.name}</esapi:encodeForHTML><br /><br />
									</c:forEach>
								</c:forEach>												
 							</c:if>						
						</c:if>
					</div>												
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
				
				<c:if test="${element.getType() == 'Download'}">
					<div class="questiontitle">${form.getQuestionTitle(element)}</div>
														
					<div class="files">
						<c:forEach items="${element.files}" var="file">
							<esapi:encodeForHTML>${file.name}</esapi:encodeForHTML><br />
						</c:forEach>
					</div>
				</c:if>
				
				<c:if test="${element.getType() == 'EmailQuestion'}">
					<pre style="white-space: pre-wrap; font-family: FreeMono, 'Courier New', Courier, monospace"><div style="word-wrap: break-word; margin-left: 0px;"><esapi:encodeForHTML>${form.getValue(element)}</esapi:encodeForHTML></div></pre>
				</c:if>
				
				<c:if test="${element.getType() == 'FreeTextQuestion' || element.getType() == 'RegExQuestion'}">				
					<c:choose>
						<c:when test="${element.isPassword}">
							<input disabled="disabled" readonly="readonly" class="${element.css}" autocomplete="off" type="password" name="answer${element.id}" value="${form.getValue(element)}"></input>
						</c:when>
						<c:otherwise>
							<pre style="white-space: pre-wrap; font-family: FreeMono, 'Courier New', Courier, monospace"><div style="word-wrap: break-word; margin-left: 0px;"><esapi:encodeForHTML>${form.getValue(element)}</esapi:encodeForHTML></div></pre>
						</c:otherwise>
					</c:choose>												
				</c:if>
				
				<c:if test="${element.getType() == 'Confirmation'}">
					<div class="questiontitle">${form.getQuestionTitle(element)}</div>																			
					
					<c:if test="${element.isUseupload()}">
						<div class="files" style="margin-left: 40px;">
							<c:forEach items="${element.files}" var="file">
								<a class="visiblelink" target="_blank" href="${contextpath}/files/${file.uid}"><esapi:encodeForHTML>${file.name}</esapi:encodeForHTML></a> <br />
							</c:forEach>
						</div>			
					</c:if>
					<c:if test="${element.usetext}">
						<div style="margin-left: 40px; margin-top: 10px;">
							${element.confirmationtext}
						</div>
					</c:if>
				</c:if>
								
				<c:if test="${element.getType() == 'NumberQuestion'}">
					<pre style="white-space: pre-wrap; font-family: FreeMono, 'Courier New', Courier, monospace"><div style="word-wrap: break-word; margin-left: 0px;"><esapi:encodeForHTML>${form.getValue(element)}</esapi:encodeForHTML><span class="unit-text"><esapi:encodeForHTML>${element.unit}</esapi:encodeForHTML></span></div></pre>
				</c:if>
				
				<c:if test="${element.getType() == 'DateQuestion' || element.getType() == 'TimeQuestion'}">
					<pre style="white-space: pre-wrap; font-family: FreeMono, 'Courier New', Courier, monospace"><div style="word-wrap: break-word; margin-left: 0px;"><esapi:encodeForHTML>${form.getValue(element)}</esapi:encodeForHTML></div></pre>
				</c:if>
					
				<c:if test="${element.getType() == 'MultipleChoiceQuestion'}">
					<div class="answer-columns">
						<table class="answers-table">
							<tr>
								<c:forEach items="${element.orderedPossibleAnswers}" var="possibleanswer" varStatus="status">
									<td style="vertical-align: top">
										<c:choose>
											<c:when test="${form.getValues(element).contains(possibleanswer.id.toString())}">
												<input disabled="disabled" readonly="readonly" checked="checked" id="${possibleanswer.id}" class="${element.css} check" type="checkbox" name="answer${element.id}" value="${possibleanswer.id}" />
											</c:when>
											<c:otherwise>
												<input disabled="disabled" readonly="readonly" id="${possibleanswer.id}" class="${element.css} check" type="checkbox" name="answer${element.id}" value="${possibleanswer.id}" />
											</c:otherwise>														
										</c:choose>
									</td>
									<td style="vertical-align: top">
										<div class="answertext" style="max-width: ${form.maxColumnWidth(element)}">${possibleanswer.title}</div>
									</td>						
									<c:if test="${element.numColumns == 0 || (status.count % element.numColumns == 0)}">
										</tr>
										<tr>
									</c:if>
								</c:forEach>
							</tr>			
						</table>			
					</div>
				</c:if>
								
				<c:if test="${element.getType() == 'SingleChoiceQuestion'}">
					<div class="answer-columns">
						<c:choose>
							<c:when test="${element.getType() == 'SingleChoiceQuestion' && element.getIsTargetDatasetQuestion() == true}">
								<div class="answer-column" style="word-wrap: break-word; border: 1px solid #bbb; padding: 5px; min-height: 20px;">
									<div style="float: right"><i class="icon icon-chevron-down"></i></div>
									<c:forEach items="${element.targetDatasets}" var="dataset">												
										<c:if test="${form.getValues(element).contains(dataset.id.toString())}">
											${dataset.name}
										</c:if>																																
									</c:forEach>			
								</div>
							</c:when>	
																	
							<c:when test="${element.getUseRadioButtons() == false && form.getValues(element).size() > 0}">
								<div class="answer-column" style="word-wrap: break-word; border: 1px solid #bbb; padding: 5px; min-height: 20px;">
									<div style="float: right"><i class="icon icon-chevron-down"></i></div>
									<c:forEach items="${element.orderedPossibleAnswers}" var="possibleanswer">												
										<c:if test="${form.getValues(element).contains(possibleanswer.id.toString()) || form.getValues(element).contains(possibleanswer.uniqueId)}">
											${possibleanswer.title}
										</c:if>																																
									</c:forEach>			
								</div>
							</c:when>
							<c:otherwise>
								<table class="answers-table">
									<tr>
										<c:forEach items="${element.orderedPossibleAnswers}" var="possibleanswer" varStatus="status">
											<td style="vertical-align: top">
												<c:choose>
													<c:when test="${form.getValues(element).contains(possibleanswer.id.toString())}">
														<input disabled="disabled" readonly="readonly" checked="checked" id="${possibleanswer.id}" class="${element.css}check " type="radio" name="answer${element.id}" value="${possibleanswer.id}" />
													</c:when>
													<c:otherwise>
														<input disabled="disabled" readonly="readonly" id="${possibleanswer.id}" class="${element.css} check" type="radio" name="answer${element.id}" value="${possibleanswer.id}" />
													</c:otherwise>														
												</c:choose>
											</td>
											<td style="vertical-align: top">
												<div class="answertext" style="max-width: ${form.maxColumnWidth(element)}">${possibleanswer.title}</div>
											</td>						
											<c:if test="${element.numColumns == 0 || (status.count % element.numColumns == 0)}">
												</tr>
												<tr>
											</c:if>
										</c:forEach>									
									</tr>			
								</table>
							</c:otherwise>
						</c:choose>
					</div>
					<div style="clear: both"></div>
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

				<c:if test="${element.getType() == 'FormulaQuestion'}">
					<pre style="white-space: pre-wrap; font-family: FreeMono, 'Courier New', Courier, monospace"><div style="word-wrap: break-word; margin-left: 0px;"><esapi:encodeForHTML>${form.getValue(element)}</esapi:encodeForHTML></div></pre>
				</c:if>
				
				</div>
								
								
			</c:forEach>
	
		</c:forEach>
		
		<div style="margin-top: 35px;">
			
			<c:if test="${form.survey.getUsefulLinks().size() != 0}">					
				<div class="linkstitle"><spring:message code="label.UsefulLinks" /></div>						
				<c:forEach var="link" items="${form.survey.getAdvancedUsefulLinks()}">
					<div style="margin-top: 5px;" ><a target="_blank" rel="noopener noreferrer" style="color: #000;" href="<esapi:encodeForHTMLAttribute>${link.value}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML>${link.key} (${link.value})</esapi:encodeForHTML></a></div>
				</c:forEach>							
				<hr style="margin-top: 15px;" />	
			</c:if>
					
			<c:if test="${form.survey.getBackgroundDocuments().size() != 0}">
				<div class="linkstitle"><spring:message code="label.BackgroundDocuments" /></div>						
				<c:forEach var="link" items="${form.survey.getBackgroundDocumentsAlphabetical()}">
					<div style="margin-top: 5px;" ><a target="_blank" style="color: #000;"><esapi:encodeForHTMLAttribute>${link.key}</esapi:encodeForHTMLAttribute></a></div>
				</c:forEach>							
				<hr style="margin-top: 15px;" />
			</c:if>				
			
			<c:if test="${form.survey.contact != null}">
				<div class="linkstitle" style="margin-bottom: 5px;"><spring:message code="label.Contact" /></div>
				
				<c:choose>
					<c:when test="${form.survey.contact.startsWith('form:')}">
						<a class="link visibleLink" href="${contextpath}/runner/contactform/${form.survey.shortname}"><spring:message code="label.ContactForm" /></a>
					</c:when>
					<c:when test="${form.survey.contact.contains('@')}">
						<i class="icon icon-envelope" style="vertical-align: middle"></i>
						<esapi:encodeForHTML>${form.survey.contact}</esapi:encodeForHTML>
					</c:when>
					<c:otherwise>
						<i class="icon icon-globe" style="vertical-align: middle"></i>
						<esapi:encodeForHTML>${form.survey.contact}</esapi:encodeForHTML>
						<c:if test="${form.survey.contactLabel != null}">
							(${form.survey.contactLabel})
						</c:if>
					</c:otherwise>
				</c:choose>
				
				
				<hr style="margin-top: 15px;" />
			</c:if>
		</div>						
	</div>

</body>
</html>
