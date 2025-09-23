<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:choose>
	<c:when test="${forpdf != null}">
		<div id="results-statistics" style="margin-top: 70px">	
	</c:when>
	<c:when test="${publication != null}">
		<div id="results-statistics" class="hidden" style="margin-top: 50px">	
	</c:when>
	<c:otherwise>
		
		<div id="results-statistics" class="hidden" style="margin-top: 10px">			
			<div style="text-align: center; margin-bottom: 10px;">
				<input onclick="reOrder('.statistics-table',0);" type="radio" class="check" name="statistics-order" checked="checked" value="original" /><spring:message code="label.OriginalOrder" />&nbsp;
				<input onclick="reOrder('.statistics-table',1);" type="radio" class="check" name="statistics-order" value="alphabetical" /><spring:message code="label.AlphabeticalOrder" />&nbsp;
				<input onclick="reOrder('.statistics-table',2);" type="radio" class="check" name="statistics-order" value="value" /><spring:message code="label.ValueOrder" />
			</div>		
	
	</c:otherwise>
</c:choose>		

	<div id="scrollareastatistics" class="scrollarea" style="height: auto">

		<c:forEach items="${form.getSurvey().getQuestionsAndSections()}" var="question">			
		
			<c:if test="${form.getSurvey().isDelphi && question.getType() == 'Section' && filter.visibleSection(question.getId(), form.getSurvey())}">
				<div class="statelement cell${question.id}" style="width: 700px; margin-left: auto; margin-right: auto;">
					<div class="sectiontitle section${question.level}">${form.getCleanSectionTitle(question)}</div>
				</div>
			</c:if>		
		
			<c:if test="${publication == null || publication.isAllQuestions() || publication.isSelected(question.id)}">
						
				<c:if test="${filter == null || filter.visibleQuestions.contains(question.id.toString())}">
				
					<c:choose>
						
						<c:when test="${question.getType() == 'MultipleChoiceQuestion' || question.getType() == 'SingleChoiceQuestion' }">
						
							<div class="statelement cell${question.id}" style="width: 700px; margin-left: auto; margin-right: auto; margin-bottom: 10px;">
						
								<div class="questiontitle" style="font-weight: bold;">${question.getStrippedTitleNoEscape()} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${question.shortname})</span></div>
								
								<table class="statistics-table table table-bordered table-striped" style="margin-top: 5px;">
									<thead>
										<tr>								
											<th style="width: 300px">&#160;</th>
											<th style="width: 200px">&#160;</th>
											<th style="width: 1px"><spring:message code="label.Answers" /></th>
											<th style="width: 90px"><spring:message code="label.Ratio" /></th>								
										</tr>
									</thead>						
									<tbody>
										<c:forEach items="${question.allPossibleAnswers}" var="answer" varStatus="status">
											<tr data-position="${status.count}" data-value="${statistics.requestedRecordsPercent[answer.id.toString()]}">
												<td>${answer.getStrippedTitleNoEscape()}  <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${answer.shortname})</span></td>
								
												<td>
													<div class="progress" style="width: 200px; margin-bottom: 2px;">
													  <div class="chartRequestedRecordsPercent progress-bar" data-id="${answer.id}" style="width: ${statistics.requestedRecordsPercent[answer.id.toString()]}%;"></div>
													</div>
												</td>	
								
												<c:choose>
													<c:when test="${statistics != null}">						
														<td class="statRequestedRecords" data-id="${answer.id}">${statistics.requestedRecords[answer.id.toString()]}</td>			
														<td class="statRequestedRecordsPercent" data-id="${answer.id}"><fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.requestedRecordsPercent[answer.id.toString()]}"/> %</td>		
													</c:when>
													<c:otherwise>
														<td id="awaitingResult" class="statRequestedRecords" data-id="${answer.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>			
														<td id="awaitingResultPercent" class="statRequestedRecordsPercent" data-id="${answer.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
													</c:otherwise>
												</c:choose>		
											</tr>
										</c:forEach>
										<c:if test="${question.getType() == 'SingleChoiceQuestion' && question.getIsTargetDatasetQuestion() }">
											<c:forEach items="${targetdatasets}" var="dataset" varStatus="status">
												<tr data-position="${status.count}" data-value="${statistics.getRequestedRecordsPercentForTargetDataset(question, dataset)}">
													<td><c:out value="${dataset.getName()}"/></td>
									
													<td>
														<div class="progress" style="width: 200px; margin-bottom: 2px;">
														  <div class="chartRequestedRecordsPercent progress-bar" data-id="${question.uniqueId}-${dataset.id}" style="width: ${statistics.getRequestedRecordsPercentForTargetDataset(question, dataset)}%;"></div>
														</div>
													</td>	
									
													<c:choose>
														<c:when test="${statistics != null}">						
															<td class="statRequestedRecords" data-id="${dataset.id}">${statistics.getRequestedRecordsForTargetDataset(question, dataset)}</td>			
															<td class="statRequestedRecordsPercent" data-id="${dataset.id}"><fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.getRequestedRecordsPercentForTargetDataset(question, dataset)}"/> %</td>		
														</c:when>
														<c:otherwise>
															<td id="awaitingResult" class="statRequestedRecords" data-id="${question.uniqueId}-${dataset.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>			
															<td id="awaitingResultPercent" class="statRequestedRecordsPercent" data-id="${question.uniqueId}-${dataset.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
														</c:otherwise>
													</c:choose>		
												</tr>
											</c:forEach>
										</c:if>				
										<tr data-position="10000" class="noanswer">
											<td><spring:message code="label.NoAnswer" /></td>
											<td>
												<div class="progress" style="width: 200px; margin-bottom: 2px;">											
													<c:choose>
														<c:when test="${statistics != null}">
															<div class="chartRequestedRecordsPercent progress-bar" data-id="${question.id}" style="width: ${statistics.requestedRecordsPercent[question.id.toString()]}%;"></div>
														</c:when>
														<c:otherwise>
															<div class="chartRequestedRecordsPercent progress-bar" data-id="${question.id}"></div>
														</c:otherwise>
													</c:choose>
												</div>
											</td>
											
											<c:choose>
												<c:when test="${statistics != null}">						
													<td class="statRequestedRecords" data-id="${question.id}">${statistics.requestedRecords[question.id.toString()]}</td>			
													<td class="statRequestedRecordsPercent" data-id="${question.id}"><fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.requestedRecordsPercent[question.id.toString()]}"/> %</td>		
												</c:when>
												<c:otherwise>
													<td id="awaitingResult" class="statRequestedRecords" data-id="${question.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>			
													<td id="awaitingResult" class="statRequestedRecordsPercent" data-id="${question.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
												</c:otherwise>
											</c:choose>										
											
										</tr>
									</tbody>
								</table>
								
							</div>
	
	                        <div class="statelement-wrapper choice-chart">
	                        	<div class="chart-controls"></div>
	                            <div class='chart-wrapper' data-survey-id="${form.getSurvey().id}" data-question-uid="${question.uniqueId}" data-uid="${question.uniqueId}" data-language-code="${form.getSurvey().language.code}">
	                                 <table class='table table-condensed table-bordered' style="width: auto; margin-bottom: 0; background-color: #fff;">
	                                     <tr>
	                                         <th class='statistics-area-header'>${form.getMessage("label.DelphiChartTitle")}
												<a class="chart-download" target="_blank" download="chart.png" data-toggle="tooltip" title="<spring:message code="label.DownloadPNG" />"><span class="glyphicon glyphicon-save"></span></a>
												<a class="chart-clipboard" data-toggle="tooltip" title="<spring:message code="label.CopyToClipboard" />">
													<i class="glyphicon glyphicon-copy"></i>
												</a>
								 			</th>
	                                     </tr>
	                                     <tr>
	                                         <td style='padding-top:10px; padding-bottom:10px'>
	                                             <div class="delphi-chart-div"></div>
	                                         </td>
	                                     </tr>
	                                 </table>
	                                 <div style="clear: both"></div>
	                            </div>
	                            <div style="clear: both"></div>
	                            <div class="no-chart-results-message"></div>
	                        </div>
	
						</c:when>
						
						<c:when test="${question.getType() == 'GalleryQuestion' && question.selection}">
							<div class="statelement cell${question.id}" style="width: 700px; margin-left: auto; margin-right: auto;">
						
								<div class="questiontitle" style="font-weight: bold;">${question.getStrippedTitleNoEscape()} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${question.shortname})</span></div>
								
								<table class="statistics-table table table-bordered table-striped" style="margin-top: 5px;">
									<thead>
										<tr>								
											<th style="width: 300px">&#160;</th>
											<th style="width: 200px">&#160;</th>
											<th style="width: 1px"><spring:message code="label.Answers" /></th>
											<th style="width: 90px"><spring:message code="label.Ratio" /></th>								
										</tr>
									</thead>						
									<tbody>
										<c:forEach items="${question.allFiles}" var="file" varStatus="status">
											<tr data-position="${status.index}" data-value="${statistics.getRequestedRecordsPercentForGallery(question, file.uid)}">
												<td>${file.name}</td>
												<td>
													<div class="progress" style="width: 200px; margin-bottom: 2px;">
													  <div class="chartRequestedRecordsPercent progress-bar" data-id="${question.id}-${file.uid}" style="width: ${statistics.getRequestedRecordsPercentForGallery(question, file.uid)}%;"></div>
													</div>
												</td>					
												<c:choose>
													<c:when test="${statistics != null}">						
														<td class="statRequestedRecords" data-id="${question.id}-${file.uid}">${statistics.getRequestedRecordsForGallery(question, file.uid)}</td>			
														<td class="statRequestedRecordsPercent" data-id="${question.id}-${file.uid}"><fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.getRequestedRecordsPercentForGallery(question, file.uid)}"/> %</td>		
													</c:when>
													<c:otherwise>
														<td id="awaitingResult" class="statRequestedRecords" data-id="${question.id}-${file.uid}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>			
														<td id="awaitingResult" class="statRequestedRecordsPercent" data-id="${question.id}-${file.uid}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
													</c:otherwise>
												</c:choose>		
											</tr>
										</c:forEach>
										<tr data-position="10000" class="noanswer">
											<td><spring:message code="label.NoAnswer" /></td>
											<td>
												<div class="progress" style="width: 200px; margin-bottom: 2px;">											
													<c:choose>
														<c:when test="${statistics != null}">
															<div class="chartRequestedRecordsPercent progress-bar" data-id="${question.id}" style="width: ${statistics.requestedRecordsPercent[question.id.toString()]}%;"></div>
														</c:when>
														<c:otherwise>
															<div class="chartRequestedRecordsPercent progress-bar" data-id="${question.id}"></div>
														</c:otherwise>
													</c:choose>
												</div>
											</td>
											
											<c:choose>
												<c:when test="${statistics != null}">						
													<td class="statRequestedRecords" data-id="${question.id}">${statistics.requestedRecords[question.id.toString()]}</td>			
													<td class="statRequestedRecordsPercent" data-id="${question.id}"><fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.requestedRecordsPercent[question.id.toString()]}"/> %</td>		
												</c:when>
												<c:otherwise>
													<td id="awaitingResult" class="statRequestedRecords" data-id="${question.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>			
													<td id="awaitingResult" class="statRequestedRecordsPercent" data-id="${question.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
												</c:otherwise>
											</c:choose>										
											
										</tr>
									</tbody>
								</table>						
							</div>				
						</c:when>
						
						<c:when test="${question.getType() == 'Matrix'}">
						
							<div class="cell${question.id}" style="width: 700px; margin-left: auto; margin-right: auto;">
							
								<c:forEach items="${question.questions}" var="matrixQuestion">
								
									<div class="statelement">
								
										<div class="questiontitle" style="font-weight: bold">${question.getStrippedTitleNoEscape()} : ${matrixQuestion.getStrippedTitleNoEscape()} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${matrixQuestion.shortname})</span></div>
										
										<table class="statistics-table table table-bordered table-striped" style="margin-top: 10px;">
										
											<thead>
												<tr>								
													<th style="width: 300px">&#160;</th>
													<th style="width: 200px">&#160;</th>
													<th style="width: 1px"><spring:message code="label.Answers" /></th>
													<th style="width: 90px"><spring:message code="label.Ratio" /></th>							
												</tr>
											</thead>
															
											<tbody>
									
											<c:forEach items="${question.answers}" var="possibleanswer" varStatus="status">
											
												<tr data-position="${status.count}" data-value="${statistics.getRequestedRecordsForMatrix(matrixQuestion, possibleanswer)}">
													<td>${possibleanswer.getStrippedTitleNoEscape()} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${possibleanswer.shortname})</span></td>
													<td>
														<div class="progress" style="width: 200px; margin-bottom: 2px;">
														  <div class="chartRequestedRecordsPercent progress-bar" data-id="${matrixQuestion.id}${possibleanswer.id}" style="width: ${statistics.getRequestedRecordsPercentForMatrix(matrixQuestion, possibleanswer)}%;"></div>
														</div>
													</td>	
													<c:choose>
														<c:when test="${statistics != null}">						
															<td class="statRequestedRecords" data-id="${matrixQuestion.id}${possibleanswer.id}">${statistics.getRequestedRecordsForMatrix(matrixQuestion, possibleanswer)}</td>			
															<td class="statRequestedRecordsPercent" data-id="${matrixQuestion.id}${possibleanswer.id}"><fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.getRequestedRecordsPercentForMatrix(matrixQuestion, possibleanswer)}"/> %</td>	
														</c:when>
														<c:otherwise>
															<td id="awaitingResult" class="statRequestedRecords" data-id="${matrixQuestion.id}${possibleanswer.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>			
															<td id="awaitingResult" class="statRequestedRecordsPercent" data-id="${matrixQuestion.id}${possibleanswer.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>	
														</c:otherwise>
													</c:choose>											
												</tr>
											
											</c:forEach>
											
											<tr data-position="10000" class="noanswer">
												<td><spring:message code="label.NoAnswer" /></td>
												<td>
													<div class="progress" style="width: 200px; margin-bottom: 2px;">											
														<c:choose>
															<c:when test="${statistics != null}">
																<div class="chartRequestedRecordsPercent progress-bar" data-id="${matrixQuestion.id}" style="width: ${statistics.requestedRecordsPercent[matrixQuestion.id.toString()]}%;"></div>
															</c:when>
															<c:otherwise>
																<div class="chartRequestedRecordsPercent progress-bar" data-id="${matrixQuestion.id}"></div>
															</c:otherwise>
														</c:choose>
													</div>
												</td>
												
												<c:choose>
													<c:when test="${statistics != null}">						
														<td class="statRequestedRecords" data-id="${matrixQuestion.id}">${statistics.requestedRecords[matrixQuestion.id.toString()]}</td>			
														<td class="statRequestedRecordsPercent" data-id="${matrixQuestion.id}"><fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.requestedRecordsPercent[matrixQuestion.id.toString()]}"/> %</td>		
													</c:when>
													<c:otherwise>
														<td id="awaitingResult" class="statRequestedRecords" data-id="${matrixQuestion.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>			
														<td id="awaitingResult" class="statRequestedRecordsPercent" data-id="${matrixQuestion.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
													</c:otherwise>
												</c:choose>										
												
											</tr>
											
											</tbody>
										</table>
										
									</div>
								
								</c:forEach>
								
							</div>
	
	                        <div class="statelement-wrapper">
	                        	   <div class="chart-controls"></div>
	                               <div class='chart-wrapper' data-survey-id="${form.getSurvey().id}" data-question-uid="${question.uniqueId}" data-uid="${question.uniqueId}" data-language-code="${form.getSurvey().language.code}">
	                                   <table class='table table-condensed table-bordered' style="width: auto; margin-bottom: 0; background-color: #fff;">
	                                       <tr>
	                                           <th class='statistics-area-header'>${form.getMessage("label.DelphiChartTitle")}
												<a class="chart-download" target="_blank" download="chart.png" data-toggle="tooltip" title="<spring:message code="label.DownloadPNG" />"><span class="glyphicon glyphicon-save"></span></a>
												<a class="chart-clipboard" data-toggle="tooltip" title="<spring:message code="label.CopyToClipboard" />">
													<i class="glyphicon glyphicon-copy"></i>
												</a>										
											</th>
	                                       </tr>
	                                       <tr>
	                                           <td style='padding-top:10px; padding-bottom:10px'>
	                                               <div class="delphi-chart-div"></div>
	                                           </td>
	                                       </tr>
	                                   </table>
	                                   <div style="clear: both"></div>
	                               </div>
	                               <div style="clear: both"></div>
	                               <div class="no-chart-results-message"></div>
	                        </div>
	
						</c:when>
											
						<c:when test="${forpdf == null && publication == null && question.getType() == 'FreeTextQuestion'}">
							<div style="width: 700px; margin-left: auto; margin-right: auto;">
								<div class="questiontitle" style="font-weight: bold">${question.getStrippedTitleNoEscape()} : ${childQuestion.getStrippedTitleNoEscape()} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${childQuestion.shortname})</span></div>
							</div>
	
	                        <div class="statelement-wrapper">
	                        	   <div class="chart-controls"></div>
	                               <div class='chart-wrapper' data-survey-id="${form.getSurvey().id}" data-question-uid="${question.uniqueId}" data-uid="${question.uniqueId}" data-language-code="${form.getSurvey().language.code}" data-initial-chart-type="${question.getDelphiChartType()}" data-chart-data-type="${question.getDelphiChartDataType()}">
	                                    <table class='table table-condensed table-bordered' style="width: auto; margin-bottom: 0; background-color: #fff;">
	                                        <tr>
	                                            <th class='statistics-area-header'>${form.getMessage("label.DelphiChartTitle")}
												    <a class="chart-download" target="_blank" download="chart.png" data-toggle="tooltip" title="<spring:message code="label.DownloadPNG" />"><span class="glyphicon glyphicon-save"></span></a>
											   		<a class="chart-clipboard" data-toggle="tooltip" title="<spring:message code="label.CopyToClipboard" />">
														<i class="glyphicon glyphicon-copy"></i>
													</a>											   
											    </th>
	                                        </tr>
	                                        <tr>
	                                            <td style='padding-top:10px; padding-bottom:10px'>
	                                                <div id="wordcloud${question.uniqueId}" class="delphi-chart-div" style="min-width: 300px; min-height: 220px"></div>
	                                            </td>
	                                        </tr>
	                                    </table>
	                                    <div style="clear: both"></div>
	                               </div>
	                               <div style="clear: both"></div>
	                               <div class="no-chart-results-message"></div>
	                        </div>
						</c:when>
						
						<c:when test="${question.getType() == 'RatingQuestion'}">
						
							<div class="cell${question.id}" style="width: 700px; margin-left: auto; margin-right: auto;">
							
								<c:forEach items="${question.questions}" var="childQuestion">
								
									<div class="statelement">
								
										<div class="questiontitle" style="font-weight: bold">${question.getStrippedTitleNoEscape()} : ${childQuestion.getStrippedTitleNoEscape()} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${childQuestion.shortname})</span></div>
										
										<table class="statistics-table table table-bordered table-striped" style="margin-top: 10px;">
										
											<thead>
												<tr>								
													<th style="width: 300px">&#160;</th>
													<th style="width: 200px">&#160;</th>
													<th style="width: 1px"><spring:message code="label.Answers" /></th>
													<th style="width: 90px"><spring:message code="label.Ratio" /></th>							
												</tr>
											</thead>
															
											<tbody>
									
											 <c:forEach begin="1" end="${question.numIcons}" varStatus="loop">
											
												<tr data-position="${loop.index}" data-value="${statistics.getRequestedRecordsForRatingQuestion(childQuestion, loop.index)}">
													<td>${loop.index}/${question.numIcons}</td>
													<td>
														<div class="progress" style="width: 200px; margin-bottom: 2px;">
														  <div class="chartRequestedRecordsPercent progress-bar" data-id="${childQuestion.id}${loop.index}" style="width: ${statistics.getRequestedRecordsPercentForRatingQuestion(childQuestion, loop.index)}%;"></div>
														</div>
													</td>	
													<c:choose>
														<c:when test="${statistics != null}">						
															<td class="statRequestedRecords" data-id="${childQuestion.id}${loop.index}">${statistics.getRequestedRecordsForRatingQuestion(childQuestion, loop.index)}</td>			
															<td class="statRequestedRecordsPercent" data-id="${childQuestion.id}${loop.index}"><fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.getRequestedRecordsPercentForRatingQuestion(childQuestion, loop.index)}"/> %</td>	
														</c:when>
														<c:otherwise>
															<td id="awaitingResult" class="statRequestedRecords" data-id="${childQuestion.id}${loop.index}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>			
															<td id="awaitingResult" class="statRequestedRecordsPercent" data-id="${childQuestion.id}${loop.index}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>	
														</c:otherwise>
													</c:choose>											
												</tr>
											
											</c:forEach>
											
											<tr data-position="10000" class="noanswer">
												<td><spring:message code="label.NoAnswer" /></td>
												<td>
													<div class="progress" style="width: 200px; margin-bottom: 2px;">											
														<c:choose>
															<c:when test="${statistics != null}">
																<div class="chartRequestedRecordsPercent progress-bar" data-id="${childQuestion.id}" style="width: ${statistics.requestedRecordsPercent[childQuestion.id.toString()]}%;"></div>
															</c:when>
															<c:otherwise>
																<div class="chartRequestedRecordsPercent progress-bar" data-id="${childQuestion.id}"></div>
															</c:otherwise>
														</c:choose>
													</div>
												</td>
												
												<c:choose>
													<c:when test="${statistics != null}">						
														<td class="statRequestedRecords" data-id="${childQuestion.id}">${statistics.requestedRecords[childQuestion.id.toString()]}</td>			
														<td class="statRequestedRecordsPercent" data-id="${childQuestion.id}"><fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.requestedRecordsPercent[childQuestion.id.toString()]}"/> %</td>		
													</c:when>
													<c:otherwise>
														<td id="awaitingResult" class="statRequestedRecords" data-id="${childQuestion.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>			
														<td id="awaitingResult" class="statRequestedRecordsPercent" data-id="${childQuestion.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
													</c:otherwise>
												</c:choose>										
												
											</tr>
											
											</tbody>
										</table>
										
									</div>
								
								</c:forEach>
								
							</div>
	
	                        <div class="statelement-wrapper">
	                               <div class="chart-controls"></div>
	                               <div class='chart-wrapper' data-survey-id="${form.getSurvey().id}" data-question-uid="${question.uniqueId}" data-uid="${question.uniqueId}" data-language-code="${form.getSurvey().language.code}">
	                                   <table class='table table-condensed table-bordered' style="width: auto; margin-bottom: 0; background-color: #fff;">
	                                       <tr>
	                                           <th class='statistics-area-header'>${form.getMessage("label.DelphiChartTitle")}
												<a class="chart-download" target="_blank" download="chart.png" data-toggle="tooltip" title="<spring:message code="label.DownloadPNG" />"><span class="glyphicon glyphicon-save"></span></a>
												<a class="chart-clipboard" data-toggle="tooltip" title="<spring:message code="label.CopyToClipboard" />">
													<i class="glyphicon glyphicon-copy"></i>
												</a>											
											</th>
	                                       </tr>
	                                       <tr>
	                                           <td style='padding-top:10px; padding-bottom:10px'>
	                                               <div class="delphi-chart-div"></div>
	                                           </td>
	                                       </tr>
	                                   </table>
	                                   <div style="clear: both"></div>
	                               </div>
	                               <div style="clear: both"></div>
	                               <div class="no-chart-results-message"></div>
	                        </div>
	
						</c:when>		
			
						<c:when test="${(question.type == 'NumberQuestion' || question.type == 'FormulaQuestion') && question.showStatisticsForNumberQuestion()}">
							<div class="statelement cell${question.id}" style="width: 700px; margin-left: auto; margin-right: auto; margin-bottom: 10px;">
						
								<div class="questiontitle" style="font-weight: bold;">${question.getStrippedTitleNoEscape()} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${question.shortname})</span></div>
								
								<table class="statistics-table table table-bordered table-striped" style="margin-top: 5px; ">
									<thead>
										<tr>								
											<th style="width: 300px">&#160;</th>
											<th style="width: 200px">&#160;</th>
											<th style="width: 1px"><spring:message code="label.Answers" /></th>
											<th style="width: 90px"><spring:message code="label.Ratio" /></th>								
										</tr>
									</thead>						
									<tbody>
										<c:forEach items="${question.allPossibleAnswers}" var="answer" varStatus="status">
											<tr data-position="${status.count}" data-value="${statistics.requestedRecordsPercent[question.id.toString() + answer]}">
												<td>${answer}</td>
								
												<td>
													<div class="progress" style="width: 200px; margin-bottom: 2px;">
													  <div class="chartRequestedRecordsPercent progress-bar" data-id="${question.id.toString()}${answer}" style="width: ${statistics.requestedRecordsPercent[question.getAnswerWithPrefix(answer)]}%;"></div>
													</div>
												</td>	
								
												<c:choose>
													<c:when test="${statistics != null}">						
														<td class="statRequestedRecords" data-id="${question.id.toString()}${answer}">${statistics.requestedRecords[question.getAnswerWithPrefix(answer)]}</td>			
														<td class="statRequestedRecordsPercent" data-id="${question.id.toString()}${answer}"><fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.requestedRecordsPercent[question.getAnswerWithPrefix(answer)]}"/> %</td>		
													</c:when>
													<c:otherwise>
														<td id="awaitingResult" class="statRequestedRecords" data-id="${question.id.toString()}${answer}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>			
														<td id="awaitingResultPercent" class="statRequestedRecordsPercent" data-id="${question.id.toString()}${answer}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
													</c:otherwise>
												</c:choose>		
											</tr>
										</c:forEach>
										<tr data-position="10000" class="noanswer">
											<td><spring:message code="label.NoAnswer" /></td>
											<td>
												<div class="progress" style="width: 200px; margin-bottom: 2px;">											
													<c:choose>
														<c:when test="${statistics != null}">
															<div class="chartRequestedRecordsPercent progress-bar" data-id="${question.id}" style="width: ${statistics.requestedRecordsPercent[question.id.toString()]}%;"></div>
														</c:when>
														<c:otherwise>
															<div class="chartRequestedRecordsPercent progress-bar" data-id="${question.id}"></div>
														</c:otherwise>
													</c:choose>
												</div>
											</td>
											
											<c:choose>
												<c:when test="${statistics != null}">						
													<td class="statRequestedRecords" data-id="${question.id}">${statistics.requestedRecords[question.id.toString()]}</td>			
													<td class="statRequestedRecordsPercent" data-id="${question.id}"><fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.requestedRecordsPercent[question.id.toString()]}"/> %</td>		
												</c:when>
												<c:otherwise>
													<td id="awaitingResult" class="statRequestedRecords" data-id="${question.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>			
													<td id="awaitingResult" class="statRequestedRecordsPercent" data-id="${question.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
												</c:otherwise>
											</c:choose>										
											
										</tr>
									</tbody>
								</table>
								
							</div>
							
	                        <div class="statelement-wrapper">
	                               <div class="chart-controls"></div>
	                               <div class='chart-wrapper' data-survey-id="${form.getSurvey().id}" data-question-uid="${question.uniqueId}" data-uid="${question.uniqueId}" data-language-code="${form.getSurvey().language.code}">
	                                    <table class='table table-condensed table-bordered' style="width: auto; margin-bottom: 0; background-color: #fff;">
	                                        <tr>
	                                            <th class='statistics-area-header'>${form.getMessage("label.DelphiChartTitle")}
												<a class="chart-download" target="_blank" download="chart.png" data-toggle="tooltip" title="<spring:message code="label.DownloadPNG" />"><span class="glyphicon glyphicon-save"></span></a>
												<a class="chart-clipboard" data-toggle="tooltip" title="<spring:message code="label.CopyToClipboard" />">
													<i class="glyphicon glyphicon-copy"></i>
												</a>											
											 </th>
	                                        </tr>
	                                        <tr>
	                                            <td style='padding-top:10px; padding-bottom:10px'>
	                                                <div class="delphi-chart-div"></div>
	                                            </td>
	                                        </tr>
	                                    </table>
	                                    <div style="clear: both"></div>
	                               </div>
	                               <div style="clear: both"></div>
	                               <div class="no-chart-results-message"></div>
	                        </div>
						</c:when>
						
						<c:when test="${question.getType() == 'RankingQuestion'}">
						
							<div class="cell${question.id}" style="width: 700px; margin-left: auto; margin-right: auto;">	
							
								<div class="questiontitle" style="font-weight: bold;">${question.getStrippedTitleNoEscape()} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${question.shortname})</span></div>
										
								<table class="statistics-table table table-bordered table-striped" style="margin-top: 10px;">
								
									<thead>
										<tr>								
											<th style="width: 300px">&#160;</th>
											
											<c:forEach items="${question.getAllChildElements()}" var="child" varStatus="status">
												<th>
													<span style="float: right">
														<button type="button" class="unstyledbutton" data-toggle="tooltip" title="<spring:message code="label.SortDescending" />" onclick="sortRankingStatistics(this, true)"><span class="glyphicon glyphicon-arrow-down"></span></button>
														<button type="button" class="unstyledbutton" data-toggle="tooltip" title="<spring:message code="label.SortAscending" />" onclick="sortRankingStatistics(this, false)"><span class="glyphicon glyphicon-arrow-up"></span></button>
													</span>
													${status.index+1}
												</th>
											</c:forEach>
											
											<th style="min-width: 100px">
												<span style="float: right">
													<button type="button" class="unstyledbutton" data-toggle="tooltip" title="<spring:message code="label.SortDescending" />" onclick="sortRankingStatistics(this, true)"><span class="glyphicon glyphicon-arrow-down"></span></button>
													<button type="button" class="unstyledbutton" data-toggle="tooltip" title="<spring:message code="label.SortAscending" />" onclick="sortRankingStatistics(this, false)"><span class="glyphicon glyphicon-arrow-up"></span></button>
												</span>
												${form.getMessage("label.Score")}											
											</th>									
																	
										</tr>
									</thead>
													
									<tbody>
							
									 <c:forEach items="${question.getAllChildElements()}" var="child" varStatus="loop">
									
										<tr data-position="${loop.index}" data-value="${statistics == null ? "" : statistics.requestedRecordsRankingPercentScore[child.id.toString()]}">
											<td>${child.getStrippedTitleNoEscape()}</td>
											<c:forEach items="${question.getAllChildElements()}" varStatus="status">
											
												<c:choose>
													<c:when test="${statistics != null}">						
														<td class="statRequestedRecordsRankingScore" data-parent-id="${question.id}" data-id="${child.id}-${status.index}">
															<b>${statistics.requestedRecordsRankingPercentScore[child.id.toString().concat("-").concat(status.index)]}%</b><br />														
															${statistics.requestedRecordsRankingScore[child.id.toString().concat("-").concat(status.index)]}
														</td>			
													</c:when>
													<c:otherwise>
														<td id="awaitingResult" class="statRequestedRecordsRankingScore" data-parent-id="${question.id}" data-id="${child.id}-${status.index}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>			
													</c:otherwise>
												</c:choose>											
												
											</c:forEach>
											
											<c:choose>
												<c:when test="${statistics != null}">						
													<td class="statRequestedRecordsRankingScore" data-parent-id="${question.id}" data-id="${child.id}">
														<b>${statistics.requestedRecordsRankingPercentScore[child.id.toString()]}</b><br />
														${statistics.requestedRecordsRankingScore[question.id.toString()]}
													</td>			
												</c:when>
												<c:otherwise>
													<td id="awaitingResult" class="statRequestedRecordsRankingScore" data-parent-id="${question.id}" data-id="${child.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>			
												</c:otherwise>
											</c:choose>							
										</tr>
									
									</c:forEach>
									
										<tr>
											<td><spring:message code="label.NoAnswer" /></td>
											<td colspan="4">
												<c:choose>
													<c:when test="${statistics != null}">						
														<span class="statRequestedRecordsPercent" data-id="${question.id}"><fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.requestedRecordsPercent[question.id.toString()]}"/> %</span><br />
														<span class="statRequestedRecords" data-id="${question.id}">${statistics.requestedRecords[question.id.toString()]}</span>
													</c:when>
													<c:otherwise>
														<span id="awaitingResult" class="statRequestedRecordsPercent" data-id="${question.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></span><br />
														<span id="awaitingResult" class="statRequestedRecords" data-id="${question.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></span>
													</c:otherwise>
												</c:choose>		
											</td>
										</tr>
									
									</tbody>
								</table>
							</div>
	
	                        <div class="statelement-wrapper ranking-chart">
	                               <div class="chart-controls"></div>
	                               <div class='chart-wrapper' data-survey-id="${form.getSurvey().id}" data-question-uid="${question.uniqueId}" data-uid="${question.uniqueId}" data-language-code="${form.getSurvey().language.code}">
	                                   <table class='table table-condensed table-bordered' style="width: auto; margin-bottom: 0; background-color: #fff;">
	                                       <tr>
	                                           <th class='statistics-area-header'>${form.getMessage("label.DelphiChartTitle")}
												<a class="chart-download" target="_blank" download="chart.png" data-toggle="tooltip" title="<spring:message code="label.DownloadPNG" />"><span class="glyphicon glyphicon-save"></span></a>
												<a class="chart-clipboard" data-toggle="tooltip" title="<spring:message code="label.CopyToClipboard" />">
													<i class="glyphicon glyphicon-copy"></i>
												</a>											
											</th>
	                                       </tr>
	                                       <tr>
	                                           <td style='padding-top:10px; padding-bottom:10px'>
	                                               <div class="delphi-chart-div"></div>
	                                           </td>
	                                       </tr>
	                                   </table>
	                                   <div style="clear: both"></div>
	                               </div>
	                               <div style="clear: both"></div>
	                               <div class="no-chart-results-message"></div>
	                        </div>
	
						</c:when>
						
						<c:when test="${question.getType() == 'ComplexTable'}">
							<c:forEach items="${question.getQuestionChildElements()}" var="child">
								<c:choose>										
									<c:when test="${child.getCellType() == 'SingleChoice' || child.getCellType() == 'MultipleChoice'}">
										<div class="cell${child.id}" style="width: 700px; margin-left: auto; margin-right: auto;">
											<div class="statelement cell${child.id}" style="width: 700px; margin-left: auto; margin-right: auto; margin-bottom: 10px;">
						
												<div class="questiontitle" style="font-weight: bold;">${child.getResultTitle(question)} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${child.shortname})</span></div>
												
												<table class="statistics-table table table-bordered table-striped" style="margin-top: 5px;">
													<thead>
														<tr>								
															<th style="width: 300px">&#160;</th>
															<th style="width: 200px">&#160;</th>
															<th style="width: 1px"><spring:message code="label.Answers" /></th>
															<th style="width: 90px"><spring:message code="label.Ratio" /></th>								
														</tr>
													</thead>						
													<tbody>
														<c:forEach items="${child.allPossibleAnswers}" var="answer" varStatus="status">
															<tr data-position="${status.count}" data-value="${statistics.requestedRecordsPercent[answer.id.toString()]}">
																<td>${answer.getStrippedTitleNoEscape()}  <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${answer.shortname})</span></td>
												
																<td>
																	<div class="progress" style="width: 200px; margin-bottom: 2px;">
																	  <div class="chartRequestedRecordsPercent progress-bar" data-id="${answer.id}" style="width: ${statistics.requestedRecordsPercent[answer.id.toString()]}%;"></div>
																	</div>
																</td>	
												
																<c:choose>
																	<c:when test="${statistics != null}">						
																		<td class="statRequestedRecords" data-id="${answer.id}">${statistics.requestedRecords[answer.id.toString()]}</td>			
																		<td class="statRequestedRecordsPercent" data-id="${answer.id}"><fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.requestedRecordsPercent[answer.id.toString()]}"/> %</td>		
																	</c:when>
																	<c:otherwise>
																		<td id="awaitingResult" class="statRequestedRecords" data-id="${answer.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>			
																		<td id="awaitingResultPercent" class="statRequestedRecordsPercent" data-id="${answer.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
																	</c:otherwise>
																</c:choose>		
															</tr>
														</c:forEach>
														<tr data-position="10000" class="noanswer">
															<td><spring:message code="label.NoAnswer" /></td>
															<td>
																<div class="progress" style="width: 200px; margin-bottom: 2px;">											
																	<c:choose>
																		<c:when test="${statistics != null}">
																			<div class="chartRequestedRecordsPercent progress-bar" data-id="${child.id}" style="width: ${statistics.requestedRecordsPercent[child.id.toString()]}%;"></div>
																		</c:when>
																		<c:otherwise>
																			<div class="chartRequestedRecordsPercent progress-bar" data-id="${child.id}"></div>
																		</c:otherwise>
																	</c:choose>
																</div>
															</td>
															
															<c:choose>
																<c:when test="${statistics != null}">						
																	<td class="statRequestedRecords" data-id="${child.id}">${statistics.requestedRecords[child.id.toString()]}</td>			
																	<td class="statRequestedRecordsPercent" data-id="${child.id}"><fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.requestedRecordsPercent[child.id.toString()]}"/> %</td>		
																</c:when>
																<c:otherwise>
																	<td id="awaitingResult" class="statRequestedRecords" data-id="${child.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>			
																	<td id="awaitingResult" class="statRequestedRecordsPercent" data-id="${child.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
																</c:otherwise>
															</c:choose>										
															
														</tr>
													</tbody>
												</table>
												
											</div>
					
					                        <div class="statelement-wrapper choice-chart">
					                        	<div class="chart-controls"></div>
					                            <div class='chart-wrapper' data-survey-id="${form.getSurvey().id}" data-question-uid="${child.uniqueId}" data-uid="${child.uniqueId}" data-language-code="${form.getSurvey().language.code}">
					                                 <table class='table table-condensed table-bordered' style="width: auto; margin-bottom: 0; background-color: #fff;">
					                                     <tr>
					                                         <th class='statistics-area-header'>${form.getMessage("label.DelphiChartTitle")}
																<a class="chart-download" target="_blank" download="chart.png" data-toggle="tooltip" title="<spring:message code="label.DownloadPNG" />"><span class="glyphicon glyphicon-save"></span></a>
																<a class="chart-clipboard" data-toggle="tooltip" title="<spring:message code="label.CopyToClipboard" />">
																	<i class="glyphicon glyphicon-copy"></i>
																</a>														
															 </th>
					                                     </tr>
					                                     <tr>
					                                         <td style='padding-top:10px; padding-bottom:10px'>
					                                             <div class="delphi-chart-div"></div>
					                                         </td>
					                                     </tr>
					                                 </table>
					                                 <div style="clear: both"></div>
					                            </div>
					                            <div style="clear: both"></div>
					                            <div class="no-chart-results-message"></div>
					                        </div>
										</div>
									</c:when>
									
									<c:when test="${forpdf == null && publication == null && child.getCellType() == 'FreeText'}">
										<div style="width: 700px; margin-left: auto; margin-right: auto;">
											<div class="questiontitle" style="font-weight: bold">${child.getResultTitle(question)} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${child.shortname})</span></div>
										</div>
				
				                        <div class="statelement-wrapper">
				                        	   <div class="chart-controls"></div>
				                               <div class='chart-wrapper' data-survey-id="${form.getSurvey().id}" data-question-uid="${child.uniqueId}" data-uid="${child.uniqueId}" data-language-code="${form.getSurvey().language.code}" data-initial-chart-type="${child.getDelphiChartType()}" data-chart-data-type="${child.getDelphiChartDataType()}">
				                                    <table class='table table-condensed table-bordered' style="width: auto; margin-bottom: 0; background-color: #fff;">
				                                        <tr>
				                                            <th class='statistics-area-header'>${form.getMessage("label.DelphiChartTitle")}
															    <a class="chart-download" target="_blank" download="chart.png" data-toggle="tooltip" title="<spring:message code="label.DownloadPNG" />"><span class="glyphicon glyphicon-save"></span></a>
														    	<a class="chart-clipboard" data-toggle="tooltip" title="<spring:message code="label.CopyToClipboard" />">
																	<i class="glyphicon glyphicon-copy"></i>
																</a>														    
														    </th>
				                                        </tr>
				                                        <tr>
				                                            <td style='padding-top:10px; padding-bottom:10px'>
				                                                <div id="wordcloud${child.uniqueId}" class="delphi-chart-div" style="min-width: 300px; min-height: 220px"></div>
				                                            </td>
				                                        </tr>
				                                    </table>
				                                    <div style="clear: both"></div>
				                               </div>
				                               <div style="clear: both"></div>
				                               <div class="no-chart-results-message"></div>
				                        </div>
									</c:when>
									
									<c:when test="${(child.getCellType() == 'Number' || child.getCellType() == 'Formula') && child.showStatisticsForNumberQuestion()}">
										<div class="statelement cell${child.id}" style="width: 700px; margin-left: auto; margin-right: auto; margin-bottom: 10px;">
									
											<div class="questiontitle" style="font-weight: bold;">${child.getResultTitle(question)} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${child.shortname})</span></div>
											
											<table class="statistics-table table table-bordered table-striped" style="margin-top: 5px; ">
												<thead>
													<tr>								
														<th style="width: 300px">&#160;</th>
														<th style="width: 200px">&#160;</th>
														<th style="width: 1px"><spring:message code="label.Answers" /></th>
														<th style="width: 90px"><spring:message code="label.Ratio" /></th>								
													</tr>
												</thead>						
												<tbody>
													<c:forEach items="${child.getPossibleNumberAnswers()}" var="answer" varStatus="status">
														<tr data-position="${status.count}" data-value="${statistics.requestedRecordsPercent[child.id.toString() + answer]}">
															<td>${answer}</td>
											
															<td>
																<div class="progress" style="width: 200px; margin-bottom: 2px;">
																  <div class="chartRequestedRecordsPercent progress-bar" data-id="${child.id.toString()}${answer}" style="width: ${statistics.requestedRecordsPercent[child.getAnswerWithPrefix(answer)]}%;"></div>
																</div>
															</td>	
											
															<c:choose>
																<c:when test="${statistics != null}">						
																	<td class="statRequestedRecords" data-id="${child.id.toString()}${answer}">${statistics.requestedRecords[child.getAnswerWithPrefix(answer)]}</td>			
																	<td class="statRequestedRecordsPercent" data-id="${child.id.toString()}${answer}"><fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.requestedRecordsPercent[child.getAnswerWithPrefix(answer)]}"/> %</td>		
																</c:when>
																<c:otherwise>
																	<td id="awaitingResult" class="statRequestedRecords" data-id="${child.id.toString()}${answer}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>			
																	<td id="awaitingResultPercent" class="statRequestedRecordsPercent" data-id="${child.id.toString()}${answer}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
																</c:otherwise>
															</c:choose>		
														</tr>
													</c:forEach>
													<tr data-position="10000" class="noanswer">
														<td><spring:message code="label.NoAnswer" /></td>
														<td>
															<div class="progress" style="width: 200px; margin-bottom: 2px;">											
																<c:choose>
																	<c:when test="${statistics != null}">
																		<div class="chartRequestedRecordsPercent progress-bar" data-id="${child.id}" style="width: ${statistics.requestedRecordsPercent[child.id.toString()]}%;"></div>
																	</c:when>
																	<c:otherwise>
																		<div class="chartRequestedRecordsPercent progress-bar" data-id="${child.id}"></div>
																	</c:otherwise>
																</c:choose>
															</div>
														</td>
														
														<c:choose>
															<c:when test="${statistics != null}">						
																<td class="statRequestedRecords" data-id="${child.id}">${statistics.requestedRecords[child.id.toString()]}</td>			
																<td class="statRequestedRecordsPercent" data-id="${child.id}"><fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.requestedRecordsPercent[child.id.toString()]}"/> %</td>		
															</c:when>
															<c:otherwise>
																<td id="awaitingResult" class="statRequestedRecords" data-id="${child.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>			
																<td id="awaitingResult" class="statRequestedRecordsPercent" data-id="${child.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
															</c:otherwise>
														</c:choose>										
														
													</tr>
												</tbody>
											</table>
											
										</div>
										
				                        <div class="statelement-wrapper">
				                               <div class="chart-controls"></div>
				                               <div class='chart-wrapper' data-survey-id="${form.getSurvey().id}" data-question-uid="${child.uniqueId}" data-uid="${child.uniqueId}" data-language-code="${form.getSurvey().language.code}">
				                                    <table class='table table-condensed table-bordered' style="width: auto; margin-bottom: 0; background-color: #fff;">
				                                        <tr>
				                                            <th class='statistics-area-header'>${form.getMessage("label.DelphiChartTitle")}
																<a class="chart-download" target="_blank" download="chart.png" data-toggle="tooltip" title="<spring:message code="label.DownloadPNG" />"><span class="glyphicon glyphicon-save"></span></a>
																<a class="chart-clipboard" data-toggle="tooltip" title="<spring:message code="label.CopyToClipboard" />">
																	<i class="glyphicon glyphicon-copy"></i>
																</a>														
														 	</th>
				                                        </tr>
				                                        <tr>
				                                            <td style='padding-top:10px; padding-bottom:10px'>
				                                                <div class="delphi-chart-div"></div>
				                                            </td>
				                                        </tr>
				                                    </table>
				                                    <div style="clear: both"></div>
				                               </div>
				                               <div style="clear: both"></div>
				                               <div class="no-chart-results-message"></div>
				                        </div>
									</c:when>
									
								</c:choose>								
							
								<c:if test="${charts.containsKey(child.uniqueId) && child.getCellType() != 'FreeText'}">
									<div style="margin: 10px">
										<img src="data:image/png;base64,${charts.get(child.uniqueId)}" />
									</div>
								</c:if>
							
							</c:forEach>															
						</c:when>						
					</c:choose>	
					
					<c:if test="${charts != null && charts.containsKey(question.uniqueId)}">
						<c:if test="${question.getType() != 'FreeTextQuestion'}">
							<div style="margin: 10px">
								<img src="data:image/png;base64,${charts.get(question.uniqueId)}" />
							</div>
						</c:if>
					</c:if>
					
				</c:if>
			</c:if>		
		</c:forEach>		
	</div>
</div>

<div id="chart-controls-template" style="display: none">
	<div style="float: left; margin-right: 10px;">
		<label><spring:message code="label.DelphiChartType" /></label><br />
	    <select onchange="changeChart(this)" class="chart-type form-control" style="width: auto">
	    	<option data-type="numerical" value="Bar"><spring:message code="label.DelphiChartBar" /></option>
	    	<option data-type="numerical" value="Column"><spring:message code="label.DelphiChartColumn" /></option>
	    	<option data-type="numerical" value="Line"><spring:message code="label.DelphiChartLine" /></option>
	    	<option data-type="numerical" value="Pie"><spring:message code="label.DelphiChartPie" /></option>
	    	<option data-type="numerical" value="Radar"><spring:message code="label.DelphiChartRadar" /></option>
	    	<option data-type="numerical" value="Scatter"><spring:message code="label.DelphiChartScatter" /></option>
	    	<option data-type="none" value="None"><spring:message code="label.None" /></option>
	    	<option data-type="textual" value="WordCloud"><spring:message code="label.DelphiChartWordCloud" /></option>
	    </select>
    </div>
    
    <div class="chart-scheme-group" style="float: left; display: none; margin-right: 10px;">
	    <label><spring:message code="label.ColorScheme" /></label><br />
	    <select onchange="changeChart(this)" class="chart-scheme form-control" style="width: auto;">
			<option data-type="numerical" selected="selected" value="tableau.Tableau10">Default</option>
			<option data-type="numerical" value="tableau.Blue20">Blue</option>
			<option data-type="numerical" value="tableau.BlueLight7">Blue (Light)</option>
			<option data-type="numerical" value="tableau.BlueGreen7">Blue/Green</option>
			<option data-type="numerical" value="tableau.BlueTeal20">Blue/Teal</option>
			<option data-type="numerical" value="tableau.Brown20">Brown</option>
			<option data-type="numerical" value="tableau.Classic10">Classic</option>
			<option data-type="numerical" value="tableau.Classic20">Classic (Alternative)</option>
			<option data-type="numerical" value="tableau.ClassicAreaBrown11">Classic (Area Brown)</option>
			<option data-type="numerical" value="tableau.ClassicAreaGreen11">Classic (Area Green)</option>
			<option data-type="numerical" value="tableau.ClassicAreaRed11">Classic (Area Red)</option>
			<option data-type="numerical" value="tableau.ClassicAreaRedGreen21">Classic (Area Red/Green)</option>
			<option data-type="numerical" value="tableau.ClassicBlue7">Classic (Blue)</option>
			<option data-type="numerical" value="tableau.ClassicBlueRed12">Classic (Blue/Red Alt.)</option>
			<option data-type="numerical" value="tableau.ClassicBlueRed6">Classic (Blue/Red)</option>
			<option data-type="numerical" value="tableau.ClassicColorBlind10">Classic (Colorblind)</option>
			<option data-type="numerical" value="tableau.ClassicCyclic13">Classic (Cyclic)</option>
			<option data-type="numerical" value="tableau.ClassicGray13">Classic (Gray)</option>
			<option data-type="numerical" value="tableau.ClassicGray5">Classic (Gray)</option>
			<option data-type="numerical" value="tableau.ClassicGreenBlue11">Classic (Green Blue)</option>
			<option data-type="numerical" value="tableau.ClassicGreen7">Classic (Green)</option>
			<option data-type="numerical" value="tableau.ClassicGreenOrange12">Classic (Green/Orange Alt.)</option>
			<option data-type="numerical" value="tableau.ClassicGreenOrange6">Classic (Green/Orange)</option>
			<option data-type="numerical" value="tableau.ClassicLight10">Classic (Light)</option>
			<option data-type="numerical" value="tableau.ClassicMedium10">Classic (Medium)</option>
			<option data-type="numerical" value="tableau.ClassicOrange7">Classic (Orange)</option>
			<option data-type="numerical" value="tableau.ClassicOrangeBlue13">Classic (Orange/Blue)</option>
			<option data-type="numerical" value="tableau.ClassicOrangeWhiteBlueLight11">Classic (Orange/White/Blue Light)</option>
			<option data-type="numerical" value="tableau.ClassicOrangeWhiteBlue11">Classic (Orange/White/Blue)</option>
			<option data-type="numerical" value="tableau.ClassicPurpleGray12">Classic (Purple/Gray Alt.)</option>
			<option data-type="numerical" value="tableau.ClassicPurpleGray6">Classic (Purple/Gray)</option>
			<option data-type="numerical" value="tableau.ClassicRed9">Classic (Red)</option>
			<option data-type="numerical" value="tableau.ClassicRedBlack11">Classic (Red/Black)</option>
			<option data-type="numerical" value="tableau.ClassicRedBlue11">Classic (Red/Blue)</option>
			<option data-type="numerical" value="tableau.ClassicRedGreenLight11">Classic (Red/Green Light)</option>
			<option data-type="numerical" value="tableau.ClassicRedGreen11">Classic (Red/Green)</option>
			<option data-type="numerical" value="tableau.ClassicRedWhiteBlackLight10">Classic (Red/White/Black Light)</option>
			<option data-type="numerical" value="tableau.ClassicRedWhiteBlack11">Classic (Red/White/Black)</option>
			<option data-type="numerical" value="tableau.ClassicRedWhiteGreenLight11">Classic (Red/White/Green Light)</option>
			<option data-type="numerical" value="tableau.ClassicRedWhiteGreen11">Classic (Red/White/Green)</option>
			<option data-type="numerical" value="tableau.ClassicTrafficLight9">Classic (Traffic Light)</option>
			<option data-type="numerical" value="tableau.ColorBlind10">Colorblind</option>
			<option data-type="numerical" value="tableau.GoldPurple7">Gold/Purple</option>
			<option data-type="numerical" value="tableau.Gray20">Gray</option>
			<option data-type="numerical" value="tableau.GrayWarm20">Gray (Warm)</option>
			<option data-type="numerical" value="tableau.Green20">Green</option>
			<option data-type="numerical" value="tableau.GreenBlue7">Green/Blue</option>
			<option data-type="numerical" value="tableau.GreenBlueWhite7">Green/Blue/White</option>
			<option data-type="numerical" value="tableau.GreenGold20">Green/Gold</option>
			<option data-type="numerical" value="tableau.GreenOrangeTeal12">Green/Orange/Teal</option>
			<option data-type="numerical" value="tableau.HueCircle19">Hue Circle</option>
			<option data-type="numerical" value="tableau.JewelBright9">Jewel Bright</option>
			<option data-type="numerical" value="tableau.MillerStone11">Miller Stone</option>
			<option data-type="numerical" value="tableau.NurielStone9">Nuriel Stone</option>
			<option data-type="numerical" value="tableau.Orange20">Orange</option>
			<option data-type="numerical" value="tableau.OrangeLight7">Orange (Light)</option>
			<option data-type="numerical" value="tableau.OrangeBlue7">Orange/Blue</option>
			<option data-type="numerical" value="tableau.OrangeBlueLight7">Orange/Blue (Light)</option>
			<option data-type="numerical" value="tableau.OrangeBlueWhite7">Orange/Blue/White</option>
			<option data-type="numerical" value="tableau.OrangeGold20">Orange/Gold</option>
			<option data-type="numerical" value="tableau.Purple20">Purple</option>
			<option data-type="numerical" value="tableau.PurplePinkGray12">Purple/Pink/Gray</option>
			<option data-type="numerical" value="tableau.Red20">Red</option>
			<option data-type="numerical" value="tableau.RedBlack7">Red/Black</option>
			<option data-type="numerical" value="tableau.RedBlackWhite7">Red/Black/White</option>
			<option data-type="numerical" value="tableau.RedBlue7">Red/Blue</option>
			<option data-type="numerical" value="tableau.RedBlueBrown12">Red/Blue/Brown</option>
			<option data-type="numerical" value="tableau.RedBlueWhite7">Red/Blue/White</option>
			<option data-type="numerical" value="tableau.RedGold21">Red/Gold</option>
			<option data-type="numerical" value="tableau.RedGreen7">Red/Green</option>
			<option data-type="numerical" value="tableau.RedGreenGold7">Red/Green/Gold</option>
			<option data-type="numerical" value="tableau.RedGreenWhite7">Red/Green/White</option>
			<option data-type="numerical" value="tableau.SeattleGrays5">Seattle Grays</option>
			<option data-type="numerical" value="tableau.Summer8">Summer</option>
			<option data-type="numerical" value="tableau.SunsetSunrise7">Sunset/Sunrise</option>
			<option data-type="numerical" value="tableau.SuperfishelStone10">Superfishel Stone</option>
			<option data-type="numerical" value="tableau.Tableau20">Tableau</option>
			<option data-type="numerical" value="tableau.Temperature7">Temperature</option>
			<option data-type="numerical" value="tableau.Traffic9">Traffic</option>
			<option data-type="numerical" value="tableau.Winter10">Winter</option>
			<option data-type="textual" value="d3.scale.category10">Style A</option>
			<option data-type="textual" value="d3.scale.category20" >Style B</option>
			<option data-type="textual" value="d3.scale.category20b">Style C</option>
			<option data-type="textual" value="d3.scale.category20c">Style D</option>
	    </select>
    </div>
    
    <div class="chart-size-group" style="float: left; display: none; margin-right: 10px;">
	    <label><spring:message code="label.Size" /></label><br />
	    <select onchange="changeChart(this)" class="chart-size form-control" style="width: auto;">
	    	<option selected="selected" value="small"><spring:message code="label.Small" /></option>
	    	<option value="medium"><spring:message code="label.Middle" /></option>
	    	<option value="large"><spring:message code="label.Large" /></option>
	    </select>
    </div>
    
    <div style="clear: both"></div>
    
    <div class="chart-legend-group" style="float: left; display: none; margin-bottom: 10px; margin-right: 10px;">
		<input class="chart-legend" onchange="changeChart(this)" type="checkbox" checked="checked" /> <spring:message code="label.Legend" />
    </div>

	<div class="chart-score-group" style="float: left; display: none; margin-bottom: 10px;">
		<input class="chart-score" onchange="changeChart(this)" type="checkbox" checked="checked" /> <spring:message code="label.ShowScore" />
	</div>

	<div class="chart-hide-unanswered-group" style="float: left; display: none; margin-bottom: 10px;">
		<input class="chart-hide-unanswered" onchange="changeChart(this)" type="checkbox" /> <spring:message code="label.HideUnusedAnswers" />
	</div>
</div>
