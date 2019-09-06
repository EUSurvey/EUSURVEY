<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:choose>
	<c:when test="${forPDF != null}">
		<div id="results-statistics" style="margin-top: 70px">	
	</c:when>
	<c:when test="${publication != null}">
		<div id="results-statistics" class="hidden" style="margin-top: 20px">	
	</c:when>
	<c:otherwise>
		
		<div id="results-statistics" class="hidden" style="margin-top: 10px">
			<c:if test="${paging.items.size() > 0}">
				<div style="text-align: center; margin-bottom: 10px;">
					<input onclick="reOrder('.statistics-table',0);" type="radio" class="check" name="statistics-order" checked="checked" value="original" /><spring:message code="label.OriginalOrder" />&nbsp;
					<input onclick="reOrder('.statistics-table',1);" type="radio" class="check" name="statistics-order" value="alphabetical" /><spring:message code="label.AlphabeticalOrder" />&nbsp;
					<input onclick="reOrder('.statistics-table',2);" type="radio" class="check" name="statistics-order" value="value" /><spring:message code="label.ValueOrder" />
				</div>
			</c:if>
	
	</c:otherwise>
</c:choose>		

	<div id="loadstatisticsbutton" class="hideme" style="text-align: center">
		<a class="btn btn-default" onclick="loadStatisticsAsync(false);"><spring:message code="label.LoadStatistics" /></a>
	</div>

	<c:if test="${paging.items.size() > 0}">
	
		<div id="scrollareastatistics" class="scrollarea">

		<c:forEach items="${form.getSurvey().getQuestionsAndSections()}" var="question">
			<c:if test="${publication == null || publication.isAllQuestions() || publication.isSelected(question.id)}">
				<c:if test="${filter == null || filter.visibleQuestions.contains(question.id.toString())}">
			
					<c:if test="${question.getType() == 'Section'}">
						<div class="statelement cell${question.id}" style="width: 700px; margin-left: auto; margin-right: auto;">
							<div class="sectiontitle section${question.level}">${form.getSectionTitle(question)}</div>
						</div>
					</c:if>			
				
					<c:if test="${question.getType() == 'MultipleChoiceQuestion' || question.getType() == 'SingleChoiceQuestion' }">
					
						<div class="statelement cell${question.id}" style="width: 700px; margin-left: auto; margin-right: auto; margin-bottom: 10px;">
					
							<div class="questiontitle" style="font-weight: bold;">${question.title} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${question.shortname})</span></div>
							
							<table class="statistics-table table table-bordered table-striped" style="margin-top: 5px; margin-left: 20px;">
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
					</c:if>
					
					<c:if test="${question.getType() == 'GalleryQuestion' && question.selection}">
						<div class="statelement cell${question.id}" style="width: 700px; margin-left: auto; margin-right: auto;">
					
							<div class="questiontitle" style="font-weight: bold;">${question.title} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${question.shortname})</span></div>
							
							<table class="statistics-table table table-bordered table-striped" style="margin-top: 5px; margin-left: 20px;">
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
									
									<table class="statistics-table table table-bordered table-striped" style="margin-top: 10px; margin-left: 20px;">	
									
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
					</c:if>
					
					<c:if test="${question.getType() == 'RatingQuestion'}">
					
						<div class="cell${question.id}" style="width: 700px; margin-left: auto; margin-right: auto;">
						
							<c:forEach items="${question.childElements}" var="childQuestion">
							
								<div class="statelement">
							
									<div class="questiontitle" style="font-weight: bold">${question.getStrippedTitleNoEscape()} : ${childQuestion.title} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${childQuestion.shortname})</span></div>
									
									<table class="statistics-table table table-bordered table-striped" style="margin-top: 10px; margin-left: 20px;">	
									
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
					</c:if>
				</c:if>
			</c:if>		
		</c:forEach>
		
		</div>	
		
	</c:if>

</div>