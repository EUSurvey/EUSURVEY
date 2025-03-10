<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:choose>
	<c:when test="${forpdf != null}">
		<div id="results-statistics-quiz" style="margin-top: 70px">	
	</c:when>
	<c:when test="${publication != null}">
		<div id="results-statistics-quiz" class="hidden" style="margin-top: 20px">	
	</c:when>
	<c:otherwise>
		<div id="results-statistics-quiz" class="hidden" style="margin-top: 10px">
	</c:otherwise>
</c:choose>		

	<div id="loadstatisticsbutton" class="hideme" style="text-align: center">
		<a class="btn btn-default" onclick="loadStatisticsAsync(false);"><spring:message code="label.LoadStatistics" /></a>
	</div>
	
		<div id="scrollareastatisticsquiz" class="scrollarea"  style="height: auto">
		<div style="width: 700px; margin-left: auto; margin-right: auto">
			<h1><spring:message code="label.TotalScores" /></h1>
			<table style="margin-bottom: 20px;">
				<tr>
					<td style="padding-right: 10px;"><spring:message code="label.NumberOfReplies" /></td>
					<c:choose>
						<c:when test="${statistics != null}">						
							<td class="statTotal">${statistics.total}</td>		
						</c:when>
						<c:otherwise>
							<td id="awaitingResult" class="statTotal"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
						</c:otherwise>
					</c:choose>	
				</tr>
				<tr>
					<td style="padding-right: 10px;">
						<spring:message code="label.AverageScore" />&#x20;
						<a data-toggle="tooltip" data-placement="top" title="<spring:message code="info.TotalMeanScore" />"><span class="glyphicon glyphicon-question-sign black"></span></a>
					</td>
					<c:choose>
						<c:when test="${statistics != null}">						
							<td class="statMeanScore" style="white-space: nowrap"><fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.meanScore}"/>&#x20;<spring:message code="label.of" />&#x20;${statistics.maxScore}&#x20;<spring:message code="label.points" />&#x20;(<fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.maxScore == 0 ? 0 : statistics.meanScore / statistics.maxScore * 100}"/>%)</td>		
						</c:when>
						<c:otherwise>
							<td id="awaitingResult" class="statMeanScore" style="white-space: nowrap"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
						</c:otherwise>
					</c:choose>	
				</tr>
				<tr>
					<td style="padding-right: 10px;">
						<spring:message code="label.BestScore" />&#x20;
						<a data-toggle="tooltip" data-placement="top" title="<spring:message code="info.TotalBestScore" />"><span class="glyphicon glyphicon-question-sign black"></span></a>	
					</td>
					<c:choose>
						<c:when test="${statistics != null}">						
							<td class="statBestScore" style="white-space: nowrap">
								${statistics.bestScore}&#x20;<spring:message code="label.of" />&#x20;${statistics.maxScore}&#x20;<spring:message code="label.points" />&#x20;(<fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.maxScore == 0 ? 0 : statistics.bestScore / statistics.maxScore * 100}"/>%)
							</td>			
						</c:when>
						<c:otherwise>
							<td id="awaitingResult" class="statBestScore" style="white-space: nowrap"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
						</c:otherwise>
					</c:choose>	
				</tr>
			</table>
	
			<c:set var="openSection" value="false"/>
			
			<c:forEach items="${form.getSurvey().getQuestionsAndSections()}" var="element" varStatus="loop">
				<c:if test="${publication == null || publication.isAllQuestions() || publication.isSelected(question.id)}">
					<c:choose>
						<c:when test="${element.getType() == 'Section'}">

							<c:if test='${statistics == null || statistics.maxSectionScore[element.uniqueId] > 0}'>
								<c:if test="${openSection eq true}">
									</table>
									<c:set var="openSection" value="false"/>
								</c:if>

								<div class="sectiontitle section${element.getLevel()}" data-id="${element.uniqueId}" style="margin-top: 20px;">${form.getCleanSectionTitle(element)}</div>

								<table style="margin-bottom: 15px">
									<tr>
										<td style="padding-right: 10px; white-space: nowrap">
											<spring:message code="label.AverageScorePerSection" />&#x20;
											<a data-toggle="tooltip" data-placement="top" title="<spring:message code="info.TotalMeanScorePerSection" />"><span class="glyphicon glyphicon-question-sign black"></span></a>
										</td>
										<c:choose>
											<c:when test="${statistics != null}">
												<td class="statMeanSectionScore" style="white-space: nowrap"><fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.meanSectionScore[element.uniqueId]}"/>&#x20;<spring:message code="label.of" />&#x20;${statistics.maxSectionScore[element.uniqueId]}&#x20;<spring:message code="label.points" />&#x20;(<fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.maxSectionScore[element.uniqueId] == 0 ? 0 : statistics.meanSectionScore[element.uniqueId] / statistics.maxSectionScore[element.uniqueId] * 100}"/>%)</td>
											</c:when>
											<c:otherwise>
												<td id="awaitingResult" data-id="${element.uniqueId}" class="statMeanSectionScore" style="white-space: nowrap"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
											</c:otherwise>
										</c:choose>
									</tr>
									<tr>
										<td style="padding-right: 10px; white-space: nowrap">
											<spring:message code="label.BestScorePerSection" />&#x20;
											<a data-toggle="tooltip" data-placement="top" title="<spring:message code="info.TotalBestScorePerSection" />"><span class="glyphicon glyphicon-question-sign black"></span></a>
										</td>
										<c:choose>
											<c:when test="${statistics != null}">
												<td class="statBestSectionScore" style="white-space: nowrap">
													<fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.bestSectionScore[element.uniqueId]}"/>&#x20;<spring:message code="label.of" />&#x20;${statistics.maxSectionScore[element.uniqueId]}&#x20;<spring:message code="label.points" />&#x20;(<fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.maxSectionScore[element.uniqueId] == 0 ? 0 : statistics.bestSectionScore[element.uniqueId] / statistics.maxSectionScore[element.uniqueId] * 100}"/>%)
												</td>
											</c:when>
											<c:otherwise>
												<td id="awaitingResult" data-id="${element.uniqueId}" class="statBestSectionScore" style="white-space: nowrap"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
											</c:otherwise>
										</c:choose>
									</tr>
								</table>

								<table class="table table-striped table-bordered">
									<tr>
										<th><spring:message code="form.Question" /></th>
										<th>&nbsp;</th>
										<th><spring:message code="label.MaxScores" /></th>
										<th><spring:message code="label.Ratio" /></th>
									</tr>
									<c:set var="openSection" value="true"/>
							</c:if>
						</c:when>
						<c:otherwise>
							<c:if test="${loop.index == 0}">
								<table class="table table-striped table-bordered">
									<tr>
										<th><spring:message code="form.Question" /></th>
										<th>&nbsp;</th>
										<th><spring:message code="label.MaxScores" /></th>
										<th><spring:message code="label.Ratio" /></th>
									</tr>
									<c:set var="openSection" value="true"/>
							</c:if>

							<c:if test="${publication == null || publication.isAllQuestions() || publication.isSelected(element.id)}">
								<c:if test="${element.getType() == 'Matrix'}">
									<c:forEach var="matrixQuestion" items="${element.questions}">									
										<c:if test="${matrixQuestion.getScoring() > 0 && (filter == null || filter.visibleQuestions.contains(element.id.toString()))}">
											<tr>
												<td>${element.getStrippedTitleNoEscape()}: ${matrixQuestion.getStrippedTitleNoEscape()}</td>
		
												<td>
													<div class="progress" style="width: 200px; margin-bottom: 2px;">
													  <div class="chartRequestedRecordsPercentScore progress-bar" data-id="${matrixQuestion.id}" style="width: ${statistics.requestedRecordsPercentScore[matrixQuestion.id.toString()]}%;"></div>
													</div>
												</td>
		
												<c:choose>
													<c:when test="${statistics != null}">
														<td class="statRequestedRecordsScore" data-id="${matrixQuestion.id}">${statistics.requestedRecordsScore[matrixQuestion.id.toString()]}</td>
														<td class="statRequestedRecordsPercentScore" data-id="${matrixQuestion.id}"><fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.requestedRecordsPercentScore[matrixQuestion.id.toString()]}"/> %</td>
													</c:when>
													<c:otherwise>
														<td id="awaitingResult" class="statRequestedRecordsScore" data-id="${matrixQuestion.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
														<td id="awaitingResultPercent" class="statRequestedRecordsPercentScore" data-id="${matrixQuestion.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
													</c:otherwise>
												</c:choose>
											</tr>
										</c:if>
									</c:forEach>
								</c:if>
														
								<c:if test="${element.getScoring() > 0 && (filter == null || filter.visibleQuestions.contains(element.id.toString()))}">
									<tr>
										<td>${element.getStrippedTitleNoEscape()}</td>

										<td>
											<div class="progress" style="width: 200px; margin-bottom: 2px;">
											  <div class="chartRequestedRecordsPercentScore progress-bar" data-id="${element.id}" style="width: ${statistics.requestedRecordsPercentScore[element.id.toString()]}%;"></div>
											</div>
										</td>

										<c:choose>
											<c:when test="${statistics != null}">
												<td class="statRequestedRecordsScore" data-id="${element.id}">${statistics.requestedRecordsScore[element.id.toString()]}</td>
												<td class="statRequestedRecordsPercentScore" data-id="${element.id}"><fmt:formatNumber type="number" maxFractionDigits="2" value="${statistics.requestedRecordsPercentScore[element.id.toString()]}"/> %</td>
											</c:when>
											<c:otherwise>
												<td id="awaitingResult" class="statRequestedRecordsScore" data-id="${element.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
												<td id="awaitingResultPercent" class="statRequestedRecordsPercentScore" data-id="${element.id}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
											</c:otherwise>
										</c:choose>
									</tr>
								</c:if>
							</c:if>
						</c:otherwise>
					</c:choose>
				</c:if>
			</c:forEach>
			</table>
						
			</div>
		</div>			
</div>