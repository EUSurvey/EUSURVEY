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

	<div id="loadstatisticsbutton" class="hideme" style="text-align: center">
		<a class="btn btn-default" onclick="loadStatisticsAsync(false);"><spring:message code="label.LoadStatistics" /></a>
	</div>
	
	<div id="scrollareastatistics" class="scrollarea" style="height: auto">

		<c:forEach items="${form.getSurvey().getQuestionsAndSections()}" var="question">
			
		
			<c:if test="${form.getSurvey().isDelphi && question.getType() == 'Section' && filter.visibleSection(question.getId(), form.getSurvey())}">
				<div class="statelement cell${question.id}" style="width: 700px; margin-left: auto; margin-right: auto;">
					<div class="sectiontitle section${question.level}">${form.getSectionTitle(question)}</div>
				</div>
			</c:if>		
		
			<c:if test="${publication == null || publication.isAllQuestions() || publication.isSelected(question.id)}">
						
				<c:if test="${filter == null || filter.visibleQuestions.contains(question.id.toString())}">
				
					<c:if test="${question.getType() == 'MultipleChoiceQuestion' || question.getType() == 'SingleChoiceQuestion' }">
					
						<div class="statelement cell${question.id}" style="width: 700px; margin-left: auto; margin-right: auto; margin-bottom: 10px;">
					
							<div class="questiontitle" style="font-weight: bold;">${question.title} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${question.shortname})</span></div>
							
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
                               <div class='chart-wrapper' data-survey-id="${form.getSurvey().id}" data-question-uid="${question.uniqueId}" data-uid="${question.uniqueId}" data-language-code="${form.getSurvey().language.code}">
                                    <table class='table table-condensed table-bordered' style="width: auto; margin-bottom: 0; background-color: #fff;">
                                        <tr>
                                            <th class='statistics-area-header'>${form.getMessage("label.DelphiChartTitle")}
											 <a class="chart-download" target="_blank" download="chart.png" data-toggle="tooltip" title="<spring:message code="label.DownloadPNG" />"><span class="glyphicon glyphicon-save"></span></a>
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
                               <div class="chart-controls"></div>
                               <div style="clear: both"></div>
                        </div>

					</c:if>
					
					<c:if test="${question.getType() == 'GalleryQuestion' && question.selection}">
						<div class="statelement cell${question.id}" style="width: 700px; margin-left: auto; margin-right: auto;">
					
							<div class="questiontitle" style="font-weight: bold;">${question.title} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${question.shortname})</span></div>
							
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
									<c:forEach items="${question.files}" var="file" varStatus="status">
										<tr data-position="${status.index}" data-value="${statistics.getRequestedRecordsPercentForGallery(question, status.index)}">
											<td>${file.name}</td>
											<td>
												<div class="progress" style="width: 200px; margin-bottom: 2px;">
												  <div class="chartRequestedRecordsPercent progress-bar" data-id="${question.id}-${status.index}" style="width: ${statistics.getRequestedRecordsPercentForGallery(question, status.index)}%;"></div>
												</div>
											</td>					
											<c:choose>
												<c:when test="${statistics != null}">						
													<td class="statRequestedRecords" data-id="${question.id}-${status.index}">${statistics.getRequestedRecordsForGallery(question, status.index)}</td>			
													<td class="statRequestedRecordsPercent" data-id="${question.id}-${status.index}"><fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.getRequestedRecordsPercentForGallery(question, status.index)}"/> %</td>		
												</c:when>
												<c:otherwise>
													<td id="awaitingResult" class="statRequestedRecords" data-id="${question.id}-${status.index}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>			
													<td id="awaitingResult" class="statRequestedRecordsPercent" data-id="${question.id}-${status.index}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
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
					</c:if>
					
					<c:if test="${question.getType() == 'Matrix'}">
					
						<div class="cell${question.id}" style="width: 700px; margin-left: auto; margin-right: auto;">
						
							<c:forEach items="${question.questions}" var="matrixQuestion">
							
								<div class="statelement">
							
									<div class="questiontitle" style="font-weight: bold">${question.getStrippedTitleNoEscape()} : ${matrixQuestion.title} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${matrixQuestion.shortname})</span></div>
									
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
                               <div class='chart-wrapper' data-survey-id="${form.getSurvey().id}" data-question-uid="${question.uniqueId}" data-uid="${question.uniqueId}" data-language-code="${form.getSurvey().language.code}">
                                   <table class='table table-condensed table-bordered' style="width: auto; margin-bottom: 0; background-color: #fff;">
                                       <tr>
                                           <th class='statistics-area-header'>${form.getMessage("label.DelphiChartTitle")}
											<a class="chart-download" target="_blank" download="chart.png" data-toggle="tooltip" title="<spring:message code="label.DownloadPNG" />"><span class="glyphicon glyphicon-save"></span></a>
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
                               <div class="chart-controls"></div>
                               <div style="clear: both"></div>
                        </div>

					</c:if>
										
					<c:if test="${question.getType() == 'FreeTextQuestion'}">				
						<div style="width: 700px; margin-left: auto; margin-right: auto;">
							<div class="questiontitle" style="font-weight: bold">${question.getStrippedTitleNoEscape()} : ${childQuestion.title} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${childQuestion.shortname})</span></div>
						</div>

                        <div class="statelement-wrapper">
                               <div class='chart-wrapper' data-survey-id="${form.getSurvey().id}" data-question-uid="${question.uniqueId}" data-uid="${question.uniqueId}" data-language-code="${form.getSurvey().language.code}" data-initial-chart-type="${question.getDelphiChartType()}" data-chart-data-type="${question.getDelphiChartDataType()}">
                                    <table class='table table-condensed table-bordered' style="width: auto; margin-bottom: 0; background-color: #fff;">
                                        <tr>
                                            <th class='statistics-area-header'>${form.getMessage("label.DelphiChartTitle")}
											    <a class="chart-download" target="_blank" download="chart.png" data-toggle="tooltip" title="<spring:message code="label.DownloadPNG" />"><span class="glyphicon glyphicon-save"></span></a>
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
                               <div class="chart-controls"></div>
                               <div style="clear: both"></div>
                        </div>
					</c:if>
					
					<c:if test="${question.getType() == 'RatingQuestion'}">
					
						<div class="cell${question.id}" style="width: 700px; margin-left: auto; margin-right: auto;">
						
							<c:forEach items="${question.childElements}" var="childQuestion">
							
								<div class="statelement">
							
									<div class="questiontitle" style="font-weight: bold">${question.getStrippedTitleNoEscape()} : ${childQuestion.title} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${childQuestion.shortname})</span></div>
									
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
                               <div class='chart-wrapper' data-survey-id="${form.getSurvey().id}" data-question-uid="${question.uniqueId}" data-uid="${question.uniqueId}" data-language-code="${form.getSurvey().language.code}">
                                   <table class='table table-condensed table-bordered' style="width: auto; margin-bottom: 0; background-color: #fff;">
                                       <tr>
                                           <th class='statistics-area-header'>${form.getMessage("label.DelphiChartTitle")}
											<a class="chart-download" target="_blank" download="chart.png" data-toggle="tooltip" title="<spring:message code="label.DownloadPNG" />"><span class="glyphicon glyphicon-save"></span></a>
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
                               <div class="chart-controls"></div>
                               <div style="clear: both"></div>
                        </div>

					</c:if>		
		
					<c:if test="${question.getType() == 'NumberQuestion' && question.showStatisticsForNumberQuestion()}">
						<div class="statelement cell${question.id}" style="width: 700px; margin-left: auto; margin-right: auto; margin-bottom: 10px;">
					
							<div class="questiontitle" style="font-weight: bold;">${question.title} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${question.shortname})</span></div>
							
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
                               <div class='chart-wrapper' data-survey-id="${form.getSurvey().id}" data-question-uid="${question.uniqueId}" data-uid="${question.uniqueId}" data-language-code="${form.getSurvey().language.code}">
                                    <table class='table table-condensed table-bordered' style="width: auto; margin-bottom: 0; background-color: #fff;">
                                        <tr>
                                            <th class='statistics-area-header'>${form.getMessage("label.DelphiChartTitle")}
											 <a class="chart-download" target="_blank" download="chart.png" data-toggle="tooltip" title="<spring:message code="label.DownloadPNG" />"><span class="glyphicon glyphicon-save"></span></a>
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
                               <div class="chart-controls"></div>
                               <div style="clear: both"></div>
                        </div>
					</c:if>
		
				</c:if>
			</c:if>		
		</c:forEach>
		
		</div>
</div>

<div id="chart-controls-template" style="display: none">
	<label><spring:message code="label.DelphiChartType" /></label><br />
    <select onchange="changeChart(this)" class="chart-type form-control">
    	<option data-type="numerical" value="Bar"><spring:message code="label.DelphiChartBar" /></option>
    	<option data-type="numerical" value="Column"><spring:message code="label.DelphiChartColumn" /></option>
    	<option data-type="numerical" value="Line"><spring:message code="label.DelphiChartLine" /></option>
    	<option data-type="numerical" value="Pie"><spring:message code="label.DelphiChartPie" /></option>
    	<option data-type="numerical" value="Radar"><spring:message code="label.DelphiChartRadar" /></option>
    	<option data-type="numerical" value="Scatter"><spring:message code="label.DelphiChartScatter" /></option>
    	<option data-type="none" value="None"><spring:message code="label.None" /></option>
    	<option data-type="textual" value="WordCloud"><spring:message code="label.DelphiChartWordCloud" /></option>
    </select>
    
    <span class="chart-scheme-group" style="display: none">
    <br />
    <label><spring:message code="label.ColorScheme" /></label><br />
    <select onchange="changeChart(this)" class="chart-scheme form-control">
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
    </span>
    
    <span class="chart-size-group" style="display: none">
    <br />
    <label><spring:message code="label.Size" /></label><br />
    <select onchange="changeChart(this)" class="chart-size form-control">
    	<option selected="selected" value="small"><spring:message code="label.Small" /></option>
    	<option value="medium"><spring:message code="label.Middle" /></option>
    	<option value="large"><spring:message code="label.Large" /></option>
    </select>
    </span>
    
    <span class="chart-legend-group" style="display: none">
	    <br />
    	<input class="chart-legend" onchange="changeChart(this)" type="checkbox" checked="checked" /> <spring:message code="label.Legend" />
    </span>
</div>
