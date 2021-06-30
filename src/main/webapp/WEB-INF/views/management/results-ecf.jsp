<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:choose>
	<c:when test="${forPDF != null}">
		<div id="ecf-results" style="margin-top: 70px">
	</c:when>
	<c:when test="${publication != null}">
		<div id="ecf-results" class="hidden" style="margin-top: 50px">
	</c:when>
	<c:otherwise>
		<div id="ecf-results" class="hidden" style="margin-top: 10px">
			<div style="text-align: center; margin-bottom: 10px;"></div>
	</c:otherwise>
</c:choose>

<div class="container-fluid">
	<div class="row">
		<div class="col-xs-12 col-sm-9 col-md-4 col-lg-4">
			<h2>
				<spring:message code="label.ECF.Results" />
			</h2>
			<div class="form-group">
				<label for="select-contributions"><spring:message
						code="label.ECF.SelectContributions" /></label>
				<table class="table table-styled table-striped table-bordered"
					id="ecfSelectContributionsTable">
					<thead>
						<tr class="headerrow">
							<th><spring:message code="label.ECF.Profile" /></th>
							<th><spring:message code="label.ECF.NumberOfContributions" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="profileResult"
							items="${ecfSummaryResult.profileResults}" varStatus="loop">
							<c:choose>
								<c:when test="${profileResult.isSelected}">
									<tr onclick="selectProfile('${profileResult.profileUid}');"
										class="selectedrow hoverablerow"
										data-profile-uid="${profileResult.profileUid}">
										<th>${profileResult.profileName}</th>
										<th>${profileResult.numberOfContributions}</th>
									</tr>
								</c:when>
								<c:otherwise>
									<tr onclick="selectProfile('${profileResult.profileUid}');"
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
				<input id="select-profile-filter" class="hideme" type="text" />
			</div>
			<div class="form-group">
				<label for="select-job-profiles"><spring:message
						code="label.ECF.ProfileFilter" /></label> <select
					onchange="displayCurrentPageResults()" class="form-control"
					name="select-job-profiles" id="select-job-profiles">
					<c:forEach var="profile" items="${ecfProfiles}" varStatus="loop">
						<option value="${profile.profileUid}" selected="selected">
							${profile.name}</option>
					</c:forEach>
					<option value="" selected="selected">No target profile</option>
				</select>
			</div>
			<div class="form-group">
				<label for="select-orderBy"><spring:message
						code="label.ECF.SortBy" /></label> <select
					onchange="displayCurrentPageResults()" class="form-control"
					name="select-orderBy" id="select-orderBy">
					<option value="ecfGapAsc"><spring:message
							code="label.ECF.SortByGapAsc" /></option>
					<option value="ecfGapDesc"><spring:message
							code="label.ECF.SortByGapDesc" /></option>
					<option value="ecfScoreAsc"><spring:message
							code="label.ECF.SortByScoreAsc" /></option>
					<option value="ecfScoreDesc"><spring:message
							code="label.ECF.SortByScoreDesc" /></option>
					<option value="nameDesc"><spring:message
							code="label.ECF.SortByNameDesc" /></option>
					<option value="nameAsc" selected="selected"><spring:message
							code="label.ECF.SortByNameAsc" /></option>
				</select>
			</div>
		</div>
	</div>

	<div class="row">
		<c:if test="${ecfGlobalResult.numberOfPages > 1}">
				<div class="col-md-4 col-md-offset-5">
					<nav aria-label="table-navigator">
						<ul class="pagination">
							<li class="previousPagesLinks"></li>
							<li class="active"><a class="currentResultPage">1</a></li>
							<li class="nextPagesLinks"><c:forEach var="i"
									begin="${ecfGlobalResult.pageNumber + 1}"
									end="${ecfGlobalResult.numberOfPages}">
									<a onclick="selectResultPage(${i})"><c:out value="${i}" /></a>
								</c:forEach> <a onclick="nextResultPage()" aria-label="Next"><span
									aria-hidden="true">&raquo;</span></a></li>
						</ul>
					</nav>
				</div>
			</c:if>
		<div id="resultsContainer"
			class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
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
								<c:set var = "participantNameTrim" value = "${fn:substring(participantName, 0, 5)}" />
								<c:set var = "contributionUid" value="${ecfGlobalResult.individualResults[0].participantContributionUIDs[loop.index]}"/>
								<th class="individual">
									<a style="color: inherit" href="${contextpath}/contribution/${contributionUid}/preview" target="_blank" data-toggle="tooltip" data-placement="top" title="${participantName}">
										${participantNameTrim}...
									</a>
								</th>
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
								items="${ecfGlobalResult.totalResults.totalScores}" varStatus="loop">
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
			<c:if test="${ecfGlobalResult.numberOfPages > 1}">
				<div class="col-md-4 col-md-offset-5">
					<nav aria-label="table-navigator">
						<ul class="pagination">
							<li class="previousPagesLinks"></li>
							<li class="active"><a class="currentResultPage">1</a></li>
							<li class="nextPagesLinks"><c:forEach var="i"
									begin="${ecfGlobalResult.pageNumber + 1}"
									end="${ecfGlobalResult.numberOfPages}">
									<a onclick="selectResultPage(${i})"><c:out value="${i}" /></a>
								</c:forEach> <a onclick="nextResultPage()" aria-label="Next"><span
									aria-hidden="true">&raquo;</span></a></li>
						</ul>
					</nav>
				</div>
			</c:if>
		</div>
	</div>
</div>
</div>


<script>
	var ecfResultPage = 1;
	var surveyShortname = "${surveyShortname}";
	var ecfMaxResultPage = "${ecfGlobalResult.numberOfPages}";
	var contextpath = "${contextpath}";
</script>
<script type="text/javascript"
	src="${contextpath}/resources/js/results-ecf.js"></script>
