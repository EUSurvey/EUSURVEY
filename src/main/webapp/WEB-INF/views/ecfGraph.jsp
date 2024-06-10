<div>
	<h1><spring:message code="label.ECF.SelfAssessmentResults" /></h1>
	<p>
		<spring:message code="label.ECF.PleaseFindResults" />
	</p>
	<c:choose>
		<c:when test="${!print && !forpdf}">
			<div class="col-xs-12 col-md-6 col-centered">
				<div class="form-group">
					<label for="select-job-profiles"><spring:message code="label.ECF.ProfileFilter" /></label> 
					<select
						oninput="fetchECFResult()" class="form-control"
						name="select-job-profiles" id="select-job-profiles">
						<c:forEach var="profile" items="${ecfProfiles}" varStatus="loop">
							<option value="${profile.profileUid}">
								${profile.name}</option>
						</c:forEach>
					</select>
				</div>
			</div>
		</c:when>
		<c:otherwise>
			<br />
			<label for="select-job-profiles"><b><spring:message code="label.ECF.SelectedProfileFilter" /></b></label> 
			<span>${ecfIndividualResult.profileName}</span>
		</c:otherwise>
	</c:choose>

	<c:choose>
         <c:when test="${!forpdf}">
			<c:forEach var="competencyType"
			items="${ecfIndividualResult.competenciesTypes}" varStatus="loop">
				<div class="ecfRespondentChart_${competencyType.typeUUID}" style="margin-top:20px;">
				
				</div>
			</c:forEach>
         </c:when>
         <c:otherwise>
			<c:forEach var="base64ECFSpiderChart"
			items="${base64ECFSpiderCharts}" varStatus="loop">
		 	<div style="margin-top:10px;">
				<img src="data:image/png;base64,${base64ECFSpiderChart}"  alt="spider chart" style="width: 600px;" />
			</div>
			</c:forEach>
         </c:otherwise>
    </c:choose>
	
	<table class="table table-styled table-striped table-bordered"
		id="ecfResultTable" style="margin-top: 10px">
		<thead>
			<tr class="headerrow">
				<th><spring:message code="label.ECF.Competencies" /></th>
				<th	id="targetScore"><spring:message code="label.ECF.Target" /></th>
				<th><spring:message code="label.ECF.Scores" /></th>
			</tr>
		</thead>
		<tbody>
			<c:choose>
				<c:when test="${!print && !forpdf}">
					
				</c:when>
				<c:otherwise>
					<c:forEach var="competencyResult"
					items="${ecfIndividualResult.competencyResultList}" varStatus="loop">
						<tr class="bodyrow">
							<th>${competencyResult.competencyName}</th>
							<th>${competencyResult.competencyTargetScore}</th>
							<th>
								<div class="score">${competencyResult.competencyScore}</div>
								<c:if test="${competencyResult.competencyScoreGap != null}">
									<c:choose>
										<c:when test="${competencyResult.competencyScoreGap > 0}">
											<div class="gap greenScore">(+${competencyResult.competencyScoreGap})</div>
										</c:when>
										<c:when test="${competencyResult.competencyScoreGap == 0}">
											<div class="gap greenScore">(${competencyResult.competencyScoreGap})</div>
										</c:when>
										<c:otherwise>
											<div class="gap redScore">(${competencyResult.competencyScoreGap})</div>
										</c:otherwise>
									</c:choose>
								</c:if>
							</th>
						</tr>
					</c:forEach>
				</c:otherwise>
    		</c:choose>
		</tbody>
	</table>
</div>

<script type="text/javascript">
	var forpdf = "${forpdf}"
</script>

<script type="text/javascript">
	var firstTry = true;
	$(document).ready(function() {
		if(!forpdf) {
			const result = fetchECFResult();
		}
	});
</script>
<script type="text/javascript"
	src="${contextpath}/resources/js/ecfGraph.js"></script>
