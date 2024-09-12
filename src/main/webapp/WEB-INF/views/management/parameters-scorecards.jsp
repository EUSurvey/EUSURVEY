<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div id="sascore" style="padding: 20px;">	

	<div style="display: flex; flex-direction: row; justify-content: space-between">
		<!--  ko if: targetDatasets().length > 0 -->
		<div style="margin-top: 10px; margin-right: 10px;">
			<b><spring:message code="label.TargetDataset" /></b><br />
			
			<select data-bind="foreach:targetDatasets()" id="scoredataset" class="form-control" style="display: inline; width: 300px;" onchange="saScoresModel.loadScores();">
				<option data-bind="text: name, value: id"></option>
			</select>					
		</div>
		<!-- /ko -->
		
		<!--  ko if: scoreCard() != null && criteria().length > 0 && targetDatasets().length > 1 -->
		<div style="display: flex; flex-direction: row; gap: 20px; background-color: #ddd; border-radius: 5px; padding: 15px; margin-top: 10px; width: fit-content;">
			<div>
				<label style="margin-bottom: 0;"><spring:message code="label.CopyScoresFrom" /></label><br />		
				<select data-bind="foreach:targetDatasets()" id="scorecopydataset" class="form-control" style="display: inline; width: 300px;">
					<!--  ko if: id() != $parent.scoreCard().datasetID() -->
					<option data-bind="text: name, value: id, attr: {'data-p':  $parent.scoreCard().datasetID()}"></option>
					<!-- /ko -->
				</select>
			</div>
			<div style="margin-top: 20px; margin-left: 5px;">
				<button data-bind="click: copyScores" class="btn btn-primary"><spring:message code="label.Copy" /></button>
			</div>
		</div>
		<!-- /ko -->	
	</div>
	
	<!--  ko if: criteria().length == 0 -->
		<div style="margin: 20px;">
			<spring:message code="info.NoCriteria" /><br />
			<spring:message code="info.NoCriteria2" arguments="$('#criterialink').tab('show')" />
		</div>
	<!-- /ko -->
	
	<!--  ko if: targetDatasets().length == 0 -->
		<div style="margin: 20px;">
			<spring:message code="info.NoTargetDatasets" /><br />
			<spring:message code="info.NoTargetDatasets2" arguments="$('#datasetlink').tab('show')" />
		</div>
	<!-- /ko -->
	
	<!--  ko if: scoreCard() != null && criteria().length > 0 && targetDatasets().length > 0 -->
		<div style="text-align: right">				
	
			<table id="scoretable" class="table table-striped table-bordered" style="margin-top: 20px; table-layout:fixed; text-align: left;">
				<thead>
					<tr>
						<th style="width: 100%"><spring:message code="label.EvaluationCriterion" /></th>
						<th style="width: 120px"><spring:message code="label.Score" /></th>
						<th style="width: 120px">
							<spring:message code="label.NotRelevant" />
							<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="info.NotRelevant" />"><i class="glyphicon glyphicon-info-sign"></i></span>
						</th>
					</tr>
				</thead>
				<tbody data-bind="foreach: scoreCard().scores()">
					<tr>
						<td>
							<span class="content" data-bind="text: criterionName" style="word-break: break-all;" </span>
						</td>
						<td>
							<input class="form-control number" style="maxlength=4" type="number" oninput="this.value = Math.min(Math.abs(Math.round(this.value)), 1000);" min="0" max="1000" step="1" data-bind="value: score, disable: notRelevant" />
						</td>
						<td style="vertical-align: middle">
							<input type="checkbox" data-bind="checked: notRelevant" />
						</td>					
					</tr>
					
				</tbody>
			</table>
			
			<button class="btn btn-success" data-bind="click: saveScores"><spring:message code="label.Save" /></button>			
		
		</div>
	
	<!-- /ko -->	
		
</div>

<style>

</style>


<script type="text/javascript"> 
	var scoresSaved = '<spring:message code="info.ScoresSaved" />';
	
	function saScoreCardViewModel() {
		this.datasetID = ko.observable(0);
		this.scores = ko.observableArray([]);
	}
		
	function saScoreViewModel() {
		this.score = ko.observable(0);
		this.notRelevant = ko.observable(false);
		this.criterion = ko.observable(0);
		this.criterionName = ko.observable("");
	}
	
	function saScoresViewModel() {
		let root = this;
		
		this.criteria = ko.observableArray([]);
		this.targetDatasets = ko.observableArray([]);
		this.scoreCard = ko.observable(null);
		
		this.myPostProcessingLogic = function() {
			$('[data-toggle="tooltip"]').tooltip();
		}	
											
		this.loadScores = function() {
			root.scoreCard(null)

			$.ajax({
				type:'GET',
				url: "${contextpath}/${form.survey.shortname}/management/selfassessment/scores?dataset=" + $("#scoredataset").val(),
				dataType: 'json',
				cache: false,
				success: function (scorecard) {
					const card = new saScoreCardViewModel();
					card.datasetID(scorecard.datasetID);
					for (let i = 0; i < scorecard.scores.length; i++)
					{
						let score = new saScoreViewModel();
						score.criterion(scorecard.scores[i].criterion);
						score.criterionName(scorecard.scores[i].criterionName);
						score.score(scorecard.scores[i].score);
						score.notRelevant(scorecard.scores[i].notRelevant);
						card.scores().push(score);
					}
					
					root.scoreCard(card);
					root.myPostProcessingLogic();
				}
			});
		}
		
		this.saveScores = function() {

			var url = "${contextpath}/${form.survey.shortname}/management/selfassessment/updatescores?dataset=" + $("#scoredataset").val();
			var jsonData = ko.toJSON(root.scoreCard());
			
			$.ajax({
				type:'POST',
				  beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
				  url: url,
				  data: jsonData,
				  contentType: "application/json; charset=utf-8",
				  processData:false, //To avoid making query String instead of JSON
				  cache: false,
				  success: function(result) {
					  if (result != "OK") {
						  showError(errorOperationFailed);
					  } else {
						  root.loadScores();
						  showSuccess(scoresSaved);
					  }
				  }, error: function (data) {
						showAjaxError(data.status)
				}});
		}
		
		this.copyScores = function() {
			$.ajax({
				type:'GET',
				url: "${contextpath}/${form.survey.shortname}/management/selfassessment/scores?dataset=" + $("#scorecopydataset").val(),
				dataType: 'json',
				cache: false,
				success: function (scorecard) {
					for (let i = 0; i < scorecard.scores.length; i++)
					{
						root.scoreCard().scores()[i].score(scorecard.scores[i].score);
						root.scoreCard().scores()[i].notRelevant(scorecard.scores[i].notRelevant);
					}		
				}
			});
		}

		this.setTargetDataset = function(dataset) {
			$("#scoredataset").val(dataset.id());
			root.loadScores();
		}
	}
	
	const saScoresModel = new saScoresViewModel();
	ko.applyBindings(saScoresModel, $('#sascore')[0]);
	
	$(function() {
		saScoresModel.loadScores();
	});
	
</script>
