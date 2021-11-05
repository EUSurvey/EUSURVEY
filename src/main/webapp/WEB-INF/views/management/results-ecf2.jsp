<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:choose>
	<c:when test="${forPDF != null}">
		<div id="ecf-results2" style="margin-top: 70px">
	</c:when>
	<c:when test="${publication != null}">
		<div id="ecf-results2" class="hidden" style="margin-top: 50px">
	</c:when>
	<c:otherwise>
		<div id="ecf-results2" class="hidden" style="margin-top: 10px">
			<div style="text-align: center; margin-bottom: 10px;"></div>
	</c:otherwise>
</c:choose>

<div class="container-fluid">
	<div class="row">
		<div class="col-xs-12 col-sm-9 col-md-4 col-lg-4">
			<h2>
				<spring:message code="label.ECF.Results2" />
			</h2>

			<div class="form-group">
				<label for="select-contributions2"><spring:message
						code="label.ECF.SelectContributions" /></label>
				<table class="table table-styled table-striped table-bordered"
					id="ecfSelectContributionsTable2">
					<thead>
						<tr class="headerrow">
							<th><spring:message code="label.ECF.Profile" /></th>
							<th><spring:message code="label.ECF.NumberOfContributions" />
							</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="profileResult"
							items="${ecfSummaryResult.profileResults}" varStatus="loop">

							<c:choose>
								<c:when test="${profileResult.isSelected}">
									<tr onclick="selectProfile2('${profileResult.profileUid}');"
										class="selectedrow hoverablerow"
										data-profile-uid="${profileResult.profileUid}">
										<th>${profileResult.profileName}</th>
										<th>${profileResult.numberOfContributions}</th>
									</tr>
								</c:when>
								<c:otherwise>
									<tr onclick="selectProfile2('${profileResult.profileUid}');"
										class="bodyrow hoverablerow"
										data-profile-uid="${profileResult.profileUid}">
										<th>${profileResult.profileName}</th>
										<th>${profileResult.numberOfContributions}</th>
									</tr>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</tbody>
				</table>
				<input id="select-job-profiles2" class="hideme" type="text" />
			</div>
		</div>
	</div>
	
	<div class="row">
		<div class="col-lg-6">
			<canvas id="ecfAvgScoreChart" style="display: none;"></canvas>
		</div>
		<div class="col-lg-6">	
			<canvas id="ecfAvgScoreChart2" style="display: none;"></canvas>
		</div>
	</div>
	<div class="row">
		<div class="col-lg-6">
			<canvas id="ecfMaxScoreChart" style="display: none; margin-top: 20px; margin-bottom: 20px;"></canvas>			
		</div>
		<div class="col-lg-6">
			<canvas id="ecfMaxScoreChart2" style="display: none; margin-top: 20px; margin-bottom: 20px;"></canvas>			
		</div>
	</div>

	<div class="row">
		<div id="resultsContainerMax"
			class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
			<table class="table table-styled table-striped table-bordered"
				id="ecfResultTable2">
				<thead>
					<tr class="headerrow">
						<th><spring:message code="label.ECF.Competencies" /></th>
						<th><spring:message code="label.ECF.Target" /></th>
						<th><spring:message code="label.ECF.Average" /></th>
						<th><spring:message code="label.ECF.Max" /></th>
					</tr>
				</thead>
				<tbody>
					<c:if test="${!empty ecfProfileResult.competencyResults}">
						<c:forEach var="competencyResult"
							items="${ecfProfileResult.competencyResults}">
							<tr class="bodyrow">
								<th>${competencyResult.competencyName}</th>
								<th>${competencyResult.competencyTargetScore}</th>
								<th>${competencyResult.competencyAverageScore}</th>
								<th><div>
										<div class="score">${competencyResult.competencyMaxScore}</div>
									</div></th>
							</tr>
						</c:forEach>
					</c:if>
				</tbody>
			</table>
		</div>
	</div>
</div>
</div>


<script>
	var ecfResultPage = 1;
	var surveyShortname = "${surveyShortname}";
</script>
<script type="text/javascript"
	src="${contextpath}/resources/js/results-ecf.js"></script>
