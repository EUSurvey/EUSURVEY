<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:choose>
	<c:when test="${forpdf != null}">
		<div id="results-charts" style="margin-top: 70px">	
	</c:when>
	<c:when test="${publication != null}">
		<div id="results-charts" class="hideme" style="margin-top: -30px">	
	</c:when>
	<c:otherwise>
		<div id="results-charts" class="hideme" style="margin-top: 10px;">
			<c:if test="${paging.items.size() > 0}">
				<div style="text-align: center; margin-bottom: 10px;">
					<input onclick="reOrder2('.charts-table',0);" type="radio" class="check" name="charts-order" checked="checked" value="original" /><spring:message code="label.OriginalOrder" />&nbsp;
					<input onclick="reOrder2('.charts-table',1);" type="radio" class="check" name="charts-order" value="alphabetical" /><spring:message code="label.AlphabeticalOrder" />&nbsp;
					<input onclick="reOrder2('.charts-table',2);" type="radio" class="check" name="charts-order" value="value" /><spring:message code="label.ValueOrder" />
				</div>
			</c:if>
		
	</c:otherwise>
</c:choose>	

	<c:if test="${paging.items.size() > 0}">
	
		<div id="scrollareacharts" class="scrollarea">

		<table class="charts-table" style="margin-left: auto; margin-right: auto;">		
	
			<c:forEach items="${form.getSurvey().getQuestionsAndSections()}" var="question">
				<c:if test="${publication == null || publication.isAllQuestions() || publication.isSelected(question.id)}">
					<c:if test="${question.getType() == 'Section'}">
						<tr class="cell${question.id}">
							<td colspan="2">
								<div class="sectiontitle section${question.level}">${form.getSectionTitle(question)}</div>
							</td>
						</tr>
					</c:if>					
					<c:if test="${question.getType() == 'MultipleChoiceQuestion' || question.getType() == 'SingleChoiceQuestion' }">					
						<tr class="cell${question.id}">
							<td colspan="2" id="${question.id}" class="questiontitle" style="margin-left: 250px; font-weight: bold;"><div style="width: 700px; overflow: visible;">${question.title} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${question.shortname})</span></div></td>
						</tr>
						<c:forEach items="${question.allPossibleAnswers}" var="answer" varStatus="status">
							<tr class="cell${question.id}" data-position="${status.count}" data-value="${statistics.requestedRecordsPercent[answer.id.toString()]}">
								<td style="vertical-align: top; padding-right: 10px; padding-left: 50px;">
									${answer.title}  <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${answer.shortname})</span>
								</td>
								<td>
									<div class="progress" style="width: 200px; margin-bottom: 2px;">
									  <div class="chartRequestedRecordsPercent bar" data-id="${answer.id}" style="width: ${statistics.requestedRecordsPercent[answer.id.toString()]}%;"></div>
									</div>
								</td>	
							</tr>
						</c:forEach>
					
					</c:if>
					
					<c:if test="${question.getType() == 'GalleryQuestion' && question.selection}">
					
						<tr class="cell${question.id}">
							<td colspan="2" id="${question.id}" class="questiontitle" style="margin-left: 250px; font-weight: bold;">
								<div style="width: 700px; overflow: visible;">
									${question.title} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${question.shortname})</span>
								</div>
							</td>
						</tr>
						 <c:forEach items="${question.allFiles}" var="file" varStatus="status">
							<tr class="cell${question.id}" data-position="${status.index}" data-value="${statistics.getRequestedRecordsPercentForGallery(question, status.index)}">
								<td style="vertical-align: top; padding-right: 10px; padding-left: 50px;">
									<esapi:encodeForHTML>${file.name}</esapi:encodeForHTML>
								</td>
								<td>
									<div class="progress" style="width: 200px; margin-bottom: 2px;">
									  <div class="chartRequestedRecordsPercent bar" data-id="${question.id}-${status.index}" style="width: ${statistics.getRequestedRecordsPercentForGallery(question, status.index)}%;"></div>
									</div>
								</td>	
							</tr>
						</c:forEach>
					
					</c:if>
					
					<c:if test="${question.getType() == 'Matrix'}">					
						<c:forEach items="${question.questions}" var="matrixQuestion">
							<tr class="cell${matrixQuestion.id}" style="padding-top: 10px;">						
								<td colspan="2" id="${matrixQuestion.id}" class="questiontitle" style="padding-top: 20px; margin-left: 250px; font-weight: bold;"><div style="width: 700px; overflow: visible;">${question.getStrippedTitle()} : ${matrixQuestion.title}<span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(matrixQuestion.shortname})</span></div></td>
							</tr>							
							 <c:forEach items="${question.answers}" var="possibleanswer" varStatus="status">
							 	<tr class="cell${matrixQuestion.id}" data-position="${status.count}" data-value="${statistics.getRequestedRecordsPercentForMatrix(matrixQuestion, possibleanswer)}">
									<td style="vertical-align: top; padding-right: 10px; padding-left: 50px;">${possibleanswer.title} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${possibleanswer.shortname})</span></td>
									<td>
										<div class="progress" style="width: 200px; margin-bottom: 2px;">
										  <div class="chartRequestedRecordsPercent bar" data-id="${matrixQuestion.id}${possibleanswer.id}" style="width: ${statistics.getRequestedRecordsPercentForMatrix(matrixQuestion, possibleanswer)}%;"></div>
										</div>
									</td>	
								</tr>							 
							 </c:forEach>
						</c:forEach>
											
					</c:if>
				</c:if>
			</c:forEach>
		
		</table>
		
		</div>
		
	</c:if>
	
</div>
