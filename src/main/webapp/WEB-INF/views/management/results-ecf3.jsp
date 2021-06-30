<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:choose>
	<c:when test="${forPDF != null}">
		<div id="ecf-results3" style="margin-top: 70px">
	</c:when>
	<c:when test="${publication != null}">
		<div id="ecf-results3" class="hidden" style="margin-top: 50px">
	</c:when>
	<c:otherwise>
		<div id="ecf-results3" class="hidden" style="margin-top: 10px">
			<div style="text-align: center; margin-bottom: 10px;"></div>
	</c:otherwise>
</c:choose>

<div class="container-fluid">
	<div class="row">
		<div class="col-xs-12 col-sm-9 col-md-4 col-lg-4">
			<h2>
				<spring:message code="label.ECF.Results3" />
			</h2>
		</div>
	</div>

	<div class="row">
		<div id="resultsContainerMax"
			class="col-xs-12 col-sm-12 col-md-3 col-lg-3">
			<div class="form-group">
				<label for="display-contributions"><spring:message
						code="label.ECF.DisplayContributions" /></label>
				<table class="table table-styled table-striped table-bordered"
					id="ecfSelectContributionsTable3">
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
									<tr class="selectedrow"
										data-profile-uid="${profileResult.profileUid}">
										<th>${profileResult.profileName}</th>
										<th>${profileResult.numberOfContributions}</th>
									</tr>
								</c:when>
								<c:otherwise>
									<tr class="bodyrow"
										data-profile-uid="${profileResult.profileUid}">
										<th>${profileResult.profileName}</th>
										<th>${profileResult.numberOfContributions}</th>
									</tr>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<table class="table table-styled table-striped table-bordered"
				id="ecfResultTable3">
				<thead>
					<tr class="headerrow">
						<th><spring:message code="label.ECF.Competencies" /></th>
						<th><spring:message code="label.ECF.AverageTarget" /></th>
						<th><spring:message code="label.ECF.Average" /></th>
						<th><spring:message code="label.ECF.MaxTarget" /></th>
						<th><spring:message code="label.ECF.Max" /></th>
					</tr>
				</thead>
				<tbody>
					<c:if test="${!empty ecfOrganizationalResult.competencyResults}">
						<c:forEach var="competencyResult"
							items="${ecfOrganizationalResult.competencyResults}">
							<tr class="bodyrow">
								<th>${competencyResult.competencyName}</th>
								<th>${competencyResult.competencyAverageTarget}</th>
								<th>${competencyResult.competencyAverageScore}</th>
								<th>${competencyResult.competencyMaxTarget}</th>
								<th>${competencyResult.competencyMaxScore}</th>
							</tr>
						</c:forEach>
					</c:if>
				</tbody>
			</table>
		</div>
		<div id="chartsContainer"
			class="col-xs-12 col-sm-12 col-md-9 col-lg-9 h-70">
			<div class="inlineChart col-xs-12 col-sm-12 col-md-9 col-lg-9">
				<c:forEach var="competencyType"
					items="${ecfOrganizationalResult.competenciesTypes}" varStatus="loop">
					<div style="margin-top:20px">
						<canvas id="ecfMaxChart_${competencyType.typeUUID}"></canvas>
					</div>
				</c:forEach>
			</div>
		</div>
		<div id="chartsContainer2"
			class="col-xs-12 col-sm-12 col-md-9 col-lg-9 h-70">
			<div class="inlineChart col-xs-12 col-sm-12 col-md-9 col-lg-9">
				<c:forEach var="competencyType"
					items="${ecfOrganizationalResult.competenciesTypes}" varStatus="loop">
					<div style="margin-top:20px">
						<canvas id="ecfAverageChart_${competencyType.typeUUID}"></canvas>
					</div>
				</c:forEach>
			</div>
		</div>
	</div>
	<div class="row"></div>
</div>
</div>


<script>
	var ecfResultPage = 1;
	var surveyShortname = "${surveyShortname}";
	$(document).ready(function() {
		const result = fetchECFOrganizationalResults();
	});
</script>
<script type="text/javascript"
	src="${contextpath}/resources/js/results-ecf.js"></script>
