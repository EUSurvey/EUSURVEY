<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<table class="table table-styled table-striped table-bordered"
	id="ecfResultTable">
	<thead>
		<tr class="headerrow">
			<th><spring:message code="label.ECF.Competence" /></th>
			<th><spring:message code="label.ECF.Target" /></th>
			<c:if test="${!empty ecfGlobalResult.individualResults}">
				<c:forEach var="participantName"
					items="${ecfGlobalResult.individualResults[0].participantsNames}"
					varStatus="loop">
					<th class="individual">${participantName}</th>
				</c:forEach>
			</c:if>
		</tr>
	</thead>
	<tbody>
		<c:if test="${ecfGlobalResult.totalResults !=null}">
			<tr class="bodyrow">
				<th>${ecfGlobalResult.totalResults.competencyName}</th>
				<th>${ecfGlobalResult.totalResults.totalTargetScore}</th>
				<c:forEach var="totalScore"
					items="${ecfGlobalResult.totalResults.totalScores}"
					varStatus="loop">
					<th>
						<div>
							<div class="score">${totalScore}</div>
							<c:if test="${!empty ecfGlobalResult.totalResults.totalGaps}">
								<div
									class=${ecfGlobalResult.totalResults.totalGaps[loop.index]>=0 ? "score greenScore" : "score redScore"}>
									&nbsp; (${ecfGlobalResult.totalResults.totalGaps[loop.index]})</div>
							</c:if>
						</div>
					</th>
				</c:forEach>
		</c:if>
		<c:forEach var="individualResult"
			items="${ecfGlobalResult.individualResults}" varStatus="loop">
			<tr class="bodyrow">
				<th>${individualResult.competencyName}</th>
				<th>${individualResult.competencyTargetScore}</th>
				<c:forEach var="competencyScore"
					items="${individualResult.competencyScores}" varStatus="loop">
					<th>
						<div>
							<div class="score">${competencyScore}</div>
							<c:if test="${!empty individualResult.competencyScoreGaps}">
								<div
									class=${individualResult.competencyScoreGaps[loop.index]>=0 ? "score greenScore" : "score redScore"}>
									&nbsp; (${individualResult.competencyScoreGaps[loop.index]})</div>
							</c:if>
						</div>
					</th>
				</c:forEach>
			</tr>
		</c:forEach>
	</tbody>
</table>