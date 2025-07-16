<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div id="sareport" data-bind="childrenComplete: myPostProcessingLogic" style="padding: 20px; padding-left: 40px;">
	
	<div style="display: flex; flex-direction: row; gap: 60px; justify-content: space-between; padding-right: 60px;">
	
		<div style="display: flex; flex-direction: column; gap: 40px;">
		
			<div style="display: flex; flex-direction: row; gap: 20px;">
			
				<div>
					<b><spring:message code="label.Algorithm" /></b>
					<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="info.Algorithm" />"><i class="glyphicon glyphicon-info-sign"></i></span>
					<br />
						<select <c:if test="${!(USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('FormManagement') > 1)}">disabled</c:if> data-bind="value: configuration().algorithm" class="form-control" style="width: auto;">
							<option value="AVG"><spring:message code="label.Average" /></option>
							<option value="MRAT"><spring:message code="label.MRATAverage" /></option>
						</select>
				</div>
				
				<div data-bind="if: configuration().algorithm() == 'MRAT'">
					<b><spring:message code="label.Variable" /></b>
					<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="info.Variable" />"><i class="glyphicon glyphicon-info-sign"></i></span>
					<br />
					<input <c:if test="${!(USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('FormManagement') > 1)}">disabled</c:if> class="form-control number" maxlength="4" type="number" oninput="this.value = Math.min(1000, Math.abs(Math.round(this.value)));" min="0" max="1000" step="1" data-bind="value: configuration().coefficient" />
				</div>
			
			</div>
			
			<div>
				<b><spring:message code="label.Introduction" /></b>
				<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="info.Introduction" />"><i class="glyphicon glyphicon-info-sign"></i></span>		
				<br />
				<c:choose>
					<c:when test="${USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('FormManagement') > 1}">
						<textarea id="report_introduction" class="tinymce" data-bind="value: configuration().introduction"></textarea>
					</c:when>
					<c:otherwise>
						<div class="tinymceSimilar" id="report_introduction" data-bind="html: configuration().introduction"></div>
					</c:otherwise>
				</c:choose>
			</div>
			
			<div>
				<b><spring:message code="label.CustomFeedback" /></b>
				<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="info.CustomFeedback" />"><i class="glyphicon glyphicon-info-sign"></i></span>		
				<br />
				<c:choose>
					<c:when test="${USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('FormManagement') > 1}">
						<textarea id="report_customfeedback" class="tinymce" data-bind="value: configuration().customFeedback"></textarea>
					</c:when>
					<c:otherwise>
						<div class="tinymceSimilar" id="report_customfeedback" data-bind="html: configuration().customFeedback"></div>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		
		<div style="display: flex; flex-direction: column; gap: 20px; border-left: 1px solid #ddd; padding: 10px; padding-left: 40px; padding-right: 40px;">
			<div style="font-weight: bold; margin-left: -30px;"><spring:message code="label.Include" /></div>

			<div style="align-content: center; border-bottom: 1px solid #ddd; padding-bottom: 20px;">
				<input <c:if test="${!(USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('FormManagement') > 1)}">disabled</c:if> type="checkbox" data-bind="checked: configuration().targetDatasetSelection" class="check" /> <spring:message code="label.TargetDatasetSelection" />
				<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="info.TargetDatasetSelection" />"><i class="glyphicon glyphicon-info-sign"></i></span>
			</div>

			<div>
				<input <c:if test="${!(USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('FormManagement') > 1)}">disabled</c:if> type="checkbox" class="check" data-bind="checked: configuration().charts" /> <spring:message code="label.Charts" />
				<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="info.Charts" />"><i class="glyphicon glyphicon-info-sign"></i></span>
				<c:choose>
					<c:when test="${USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('FormManagement') > 1}">
						<select class="form-control" data-bind="value: configuration().selectedChart, enable: configuration().charts" style="display: inline; width: auto; margin-left: 10px;">
							<option value="SPIDER"><spring:message code="label.SpiderChart" /></option>
							<option value="BAR"><spring:message code="label.DelphiChartBar" /></option>
							<option value="LINE"><spring:message code="label.DelphiChartLine" /></option>
						</select>
					</c:when>
					<c:otherwise>
						<select disabled class="form-control" data-bind="value: configuration().selectedChart" style="display: inline; width: auto; margin-left: 10px;">
							<option value="SPIDER"><spring:message code="label.SpiderChart" /></option>
							<option value="BAR"><spring:message code="label.DelphiChartBar" /></option>
							<option value="LINE"><spring:message code="label.DelphiChartLine" /></option>
						</select>
					</c:otherwise>
				</c:choose>
			</div>

			<c:choose>
				<c:when test="${USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('FormManagement') > 1}">
					<div style="padding-left: 10px; align-content: center;">
						<input type="checkbox" class="check" data-bind="checked: configuration().legend, enable: configuration().charts" /> <spring:message code="label.Legend" />
						<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="info.Legend" />"><i class="glyphicon glyphicon-info-sign"></i></span>
					</div>

					<div style="padding-left: 10px; align-content: center;">
						<input type="checkbox" class="check" data-bind="checked: configuration().scale, enable: configuration().charts" /> <spring:message code="label.Scale" />
						<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="info.Scale" />"><i class="glyphicon glyphicon-info-sign"></i></span>
					</div>

					<div style="padding-left: 10px; align-content: center; border-bottom: 1px solid #ddd; padding-bottom: 20px;">
						<input type="checkbox" class="check" data-bind="checked: configuration().separateCompetencyTypes, enable: configuration().charts" /> <spring:message code="label.SeparatePerType" />
						<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="info.SeparateCompetencyTypes" />"><i class="glyphicon glyphicon-info-sign"></i></span>
					</div>
				</c:when>
				<c:otherwise>
					<div style="padding-left: 10px; align-content: center;">
						<input disabled type="checkbox" class="check" data-bind="checked: configuration().legend" /> <spring:message code="label.Legend" />
						<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="info.Legend" />"><i class="glyphicon glyphicon-info-sign"></i></span>
					</div>

					<div style="padding-left: 10px; align-content: center;">
						<input disabled type="checkbox" class="check" data-bind="checked: configuration().scale" /> <spring:message code="label.Scale" />
						<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="info.Scale" />"><i class="glyphicon glyphicon-info-sign"></i></span>
					</div>

					<div style="padding-left: 10px; align-content: center; border-bottom: 1px solid #ddd; padding-bottom: 20px;">
						<input disabled type="checkbox" class="check" data-bind="checked: configuration().separateCompetencyTypes" /> <spring:message code="label.SeparatePerType" />
						<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="info.SeparateCompetencyTypes" />"><i class="glyphicon glyphicon-info-sign"></i></span>
					</div>
				</c:otherwise>
			</c:choose>

			<div style="align-content: center;">
				<input <c:if test="${!(USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('FormManagement') > 1)}">disabled</c:if> type="checkbox" class="check" data-bind="checked: configuration().resultsTable" /> <spring:message code="label.ResultsTable" />
				<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="info.ResultsTable" />"><i class="glyphicon glyphicon-info-sign"></i></span>
			</div>

			<c:choose>
				<c:when test="${USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('FormManagement') > 1}">
					<div style="padding-left: 10px; align-content: center;">
						<input type="checkbox" class="check" data-bind="checked: configuration().competencyType, enable: configuration().resultsTable" /> <spring:message code="label.CompetencyType" />
						<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="info.CompetencyType" />"><i class="glyphicon glyphicon-info-sign"></i></span>
					</div>

					<div style="padding-left: 10px; align-content: center;">
						<input type="checkbox" class="check" data-bind="checked: configuration().targetScores, enable: configuration().resultsTable" /> <spring:message code="label.TargetScores" />
						<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="info.TargetScores" />"><i class="glyphicon glyphicon-info-sign"></i></span>
					</div>

					<div style="padding-left: 10px; align-content: center; border-bottom: 1px solid #ddd; padding-bottom: 20px;">
						<input type="checkbox" class="check" data-bind="checked: configuration().gaps, enable: configuration().resultsTable" /> <spring:message code="label.Gaps" />
						<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="info.Gaps" />"><i class="glyphicon glyphicon-info-sign"></i></span>
					</div>
				</c:when>
				<c:otherwise>
					<div style="padding-left: 10px; align-content: center;">
						<input disabled type="checkbox" class="check" data-bind="checked: configuration().competencyType" /> <spring:message code="label.CompetencyType" />
						<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="info.CompetencyType" />"><i class="glyphicon glyphicon-info-sign"></i></span>
					</div>

					<div style="padding-left: 10px; align-content: center;">
						<input disabled type="checkbox" class="check" data-bind="checked: configuration().targetScores" /> <spring:message code="label.TargetScores" />
						<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="info.TargetScores" />"><i class="glyphicon glyphicon-info-sign"></i></span>
					</div>

					<div style="padding-left: 10px; align-content: center; border-bottom: 1px solid #ddd; padding-bottom: 20px;">
						<input disabled type="checkbox" class="check" data-bind="checked: configuration().gaps" /> <spring:message code="label.Gaps" />
						<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="info.Gaps" />"><i class="glyphicon glyphicon-info-sign"></i></span>
					</div>
				</c:otherwise>
			</c:choose>

			<div style="align-content: center;">
				<input <c:if test="${!(USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('FormManagement') > 1)}">disabled</c:if> type="checkbox" class="check" data-bind="checked: configuration().performanceTable" /> <spring:message code="label.PerformanceTable" />
				<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="info.PerformanceTable" />"><i class="glyphicon glyphicon-info-sign"></i></span>
			</div>

			<div style="padding-left: 10px;">
				<spring:message code="label.LimitTableLines" />
				<c:choose>
					<c:when test="${USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('FormManagement') > 1}">
						<input class="form-control number" style="display: inline; width: auto; margin-left: 10px;" maxlength="4" type="number" oninput="this.value = Math.min(Math.abs(Math.round(this.value)), 1000);" min="0" max="1000" step="1" data-bind="value: configuration().limitTableLines, enable: configuration().performanceTable()" />
					</c:when>
					<c:otherwise>
						<input disabled class="form-control number" style="display: inline; width: auto; margin-left: 10px;" maxlength="4" type="number" oninput="this.value = Math.min(Math.abs(Math.round(this.value)), 1000);" min="0" max="1000" step="1" data-bind="value: configuration().limitTableLines" />
					</c:otherwise>
				</c:choose>
			</div>
		</div>
			
	</div>
	
	<div style="text-align: right; margin-top: 20px;">
		<button class="btn btn-success" <c:if test="${!(USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('FormManagement') > 1)}">disabled</c:if> data-bind="click: saveReportConfiguration"><spring:message code="label.Save" /></button>
	</div>		
	
</div>

<style>
	.glyphicon-info-sign {
	    color: #aaa;
	    font-size: 15px;
	    margin-top: 1px;
	}
	
	ol {
		text-align: left;
	}
</style>


<script type="text/javascript"> 

	var configurationSaved = '<spring:message code="info.ConfigurationSaved" />';
	var errorIntroTooLong = '<spring:message code="error.IntroTooLong" />';
	var errorFeedbackTooLong = '<spring:message code="error.FeedbackTooLong" />';
		
	function saReportConfigurationViewModel() {
		this.id = ko.observable(0);
		this.algorithm = ko.observable("AVG");
		this.coefficient = ko.observable(5);
		this.introduction = ko.observable("");
		this.customFeedback = ko.observable("");
		this.targetDatasetSelection = ko.observable(true);
		this.charts = ko.observable(true);
		this.selectedChart = ko.observable("spider");
		this.legend = ko.observable(true);
		this.scale = ko.observable(true);
		this.separateCompetencyTypes = ko.observable(true);
		this.resultsTable = ko.observable(true);
		this.competencyType = ko.observable(true);
		this.targetScores = ko.observable(true);
		this.gaps = ko.observable(true);
		this.performanceTable = ko.observable(true);
		this.limitTableLines = ko.observable(0);
	}
		
	function saReportViewModel() {
		let root = this;		
	
		this.configuration = ko.observable(new saReportConfigurationViewModel());
						
		this.myPostProcessingLogic = function() {
			$('[data-toggle="tooltip"]').tooltip();
		}		

		this.loadReportConfiguration = function() {
			$.ajax({
				type:'GET',
				url: "${contextpath}/${form.survey.shortname}/management/selfassessment/reportConfiguration",
				dataType: 'json',
				cache: false,
				success: function (configuration) {
					root.configuration().id(configuration.id);
					root.configuration().algorithm(configuration.algorithm);
					root.configuration().coefficient(configuration.coefficient);
					root.configuration().introduction(configuration.introduction);
					root.configuration().customFeedback(configuration.customFeedback);
					root.configuration().targetDatasetSelection(configuration.targetDatasetSelection);
					root.configuration().charts(configuration.charts);
					root.configuration().selectedChart(configuration.selectedChart);
					root.configuration().legend(configuration.legend);
					root.configuration().scale(configuration.scale);
					root.configuration().separateCompetencyTypes(configuration.separateCompetencyTypes);
					root.configuration().resultsTable(configuration.resultsTable);
					root.configuration().competencyType(configuration.competencyType);
					root.configuration().targetScores(configuration.targetScores);
					root.configuration().gaps(configuration.gaps);
					root.configuration().performanceTable(configuration.performanceTable);
					root.configuration().limitTableLines(configuration.limitTableLines);
					root.myPostProcessingLogic();
					
					// TinyMCE sometimes does not work with Knockout data-binding, so we set the data manually
					if (configuration.introduction.length > 0) {
						tinyMCE.get("report_introduction").setContent(configuration.introduction, {format : 'xhtml'});
					}
					if (configuration.customFeedback.length > 0) {
						tinyMCE.get("report_customfeedback").setContent(configuration.customFeedback, {format : 'xhtml'});
					}
				}
			});
		}

		this.saveReportConfiguration = function() {
			
			//tinymce does not update the viewmodels itself
			var text = tinyMCE.get("report_introduction").getContent();
			root.configuration().introduction(text);
			text = tinyMCE.get("report_customfeedback").getContent();
			root.configuration().customFeedback(text);
			
			var jsonData = ko.toJSON(root.configuration());

			var url = contextpath + "/" + surveyUID + "/management/selfassessment/updateReportConfiguration";

			$.ajax({
				type:'POST',
				beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
				url: url,
				data: jsonData,
				contentType: "application/json; charset=utf-8",
				processData:false, //To avoid making query String 
				cache: false,
				success: function(result) {
					if (result == "INTROTOOLONG") {
						showError(errorIntroTooLong);
					} else if (result == "FEEDBACKTOOLONG") {
						showError(errorFeedbackTooLong);
					} else if (result != "OK") {
						showError(errorOperationFailed);
					} else {
						$('#sacriterionname').val("");
						root.loadReportConfiguration();
						showSuccess(configurationSaved);
					}
				}, error: function (data) {
					showAjaxError(data.status)
				}});
		}

	}
	

	let saReportModel = new saReportViewModel();
	ko.applyBindings(saReportModel, $('#sareport')[0]);
	
	$(function() {
		saReportModel.loadReportConfiguration();
	});
	
</script>
