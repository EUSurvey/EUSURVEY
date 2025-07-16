<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<c:choose>
	<c:when test="${publication != null}">
		<div id="results-quorum" style="display: none; width: 730px; margin-top: 60px; min-height: 400px; max-width:100%; margin-left: auto; margin-right:auto;">
	</c:when>
	<c:otherwise>
		<div id="results-quorum" style="display: none; width: 730px; margin-top: 0px; max-width:100%; margin-left: auto; margin-right:auto;">
	</c:otherwise>
</c:choose>
	<div id="no-voters" data-bind="if: voterCount() == 0"><spring:message code="info.NoVoterFile" /></div>
	<div id="no-contributions" data-bind="if: voterCount() > 0 && voteContributions().numberOfContributions == 0"><spring:message code="info.NoContributions" /></div>

	<div id="quorum" data-bind="if: voterCount() > 0 && voteContributions().numberOfContributions > 0">
		<div id="quoruminfo" style="text-align: left; margin-bottom: 40px; color: #337ab7; font-size: 125%" >
			<b id="quorum-info"><spring:message code="info.Quorum" /> <span style="margin-left: 10px;" data-bind="text: quorumText('${form.survey.quorum}')"></span> </b>
		</div>
		<select class="form-control" style="width:auto; margin-bottom: 28px; display: inline;" onchange="loadGraphData(this.value)">
			<option value="quorumDays" selected><spring:message code="label.CountEveryDay" /></option>
			<option value="quorumHours"><spring:message code="label.CountEveryHour" /></option>
		</select>
		<a class="chart-download" id="quorum-chart-download" style="float: none; margin-left: 10px;" target="_blank" download="chart.png" data-toggle="tooltip" title="<spring:message code="label.DownloadPNG" />"><span class="glyphicon glyphicon-save"></span></a>
		<div id="quorum-table" >
			<canvas id="quorumChart"></canvas>
		</div>
	</div>
</div>

<div id="results-quorum-loader" style="text-align: center">
	<img src="${contextpath}/resources/images/ajax-loader.gif" />
</div>

<script type="text/javascript" src="${contextpath}/resources/js/results-quorum.js?version=<%@include file="../version.txt" %>"></script>

<script type="text/javascript">
	const labelNumberOfVotes = '<spring:message code="label.NumberOfVotes" />';
	const labelVotesReceived = '<spring:message code="label.VotesReceived" />';

	let quorumLinePlugin= {
		afterDraw: function(chartInstance) {
			if (chartInstance.options.horizontalLine) {
					var line = chartInstance.options.horizontalLine;
					if (line.y) {
						yValue = chartInstance.scales["y-axis-0"].getPixelForValue(line.y);
					} else {
						yValue = 0;
					}
					var ctx = chartInstance.chart.ctx;
					ctx.lineWidth = 3;
					if (yValue) {
						ctx.beginPath();
						ctx.moveTo(chartInstance.chart.width - 670, yValue);
						ctx.lineTo(chartInstance.chart.width - 3, yValue);
						ctx.strokeStyle = 'rgb(58, 138, 207)';
						ctx.lineWidth = 1;
						ctx.stroke();
					}

					if (line.text) {
						ctx.fontStyle = 'normal';
						ctx.fontSize = 5;
						ctx.fillText(line.text, 0, yValue + ctx.lineWidth - 3);
					}
			}
			return;
		}
	}

	function loadGraphData(selectedScale) {
		_quorumResults.loadVoterContributions('${form.survey.id}', selectedScale, ${form.survey.quorum});
	}

	$(function() {
		Chart.pluginService.register(quorumLinePlugin);
		loadGraphData('quorumDays');
		$('[data-toggle="tooltip"]').tooltip();
	});
</script>
		
