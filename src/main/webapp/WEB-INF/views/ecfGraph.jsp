<div>
	<h2><spring:message code="label.ECF.SelfAssessmentResults" /></h2>
	<p>
		<spring:message code="label.ECF.PleaseFindResults" />
	</p>
	<c:if test="${!print && !forpdf}">
		<div class="col-xs-12 col-md-6 col-centered">
			<div class="form-group">
				<label for="select-job-profiles"><spring:message code="label.ECF.ProfileFilter" /></label> 
				<select
					onchange="fetchECFResult()" class="form-control"
					name="select-job-profiles" id="select-job-profiles">
					<c:forEach var="profile" items="${ecfProfiles}" varStatus="loop">
						<option value="${profile.profileUid}">
							${profile.name}</option>
					</c:forEach>
				</select>
			</div>
		</div>
	</c:if>
	<canvas class="ecfRespondentChart"></canvas>
	<table class="table table-styled table-striped table-bordered"
		id="ecfResultTable" style="margin-bottom: 10px">
		<thead>
			<tr class="headerrow">
				<th><spring:message code="label.ECF.Competencies" /></th>
				<th	id="targetScore"><spring:message code="label.ECF.Target" /></th>
				<th><spring:message code="label.ECF.Scores" /></th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
</div>


<script type="text/javascript">
	var firstTry = true;
	$(document).ready(function() {
		const result = fetchECFResult();
	});
</script>
<script type="text/javascript"
	src="${contextpath}/resources/js/ecfGraph.js"></script>
