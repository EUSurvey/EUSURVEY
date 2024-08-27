<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
	
	<link href="${contextpath}/resources/css/yellowfocus.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css"></link>
	
	<style type="text/css">
		.customtext {
		 	margin-top: 20px;
		 	margin-bottom: 20px;
		}
	</style>
		
	<c:choose>
		<c:when test="${forpdf == null && responsive == null}">
			<div id="saresults" class="fullpageform" style="max-width: 1000px; margin-left: auto; margin-right: auto;">
		</c:when>
		<c:when test="${responsive != null}">
			<div id="saresults" style="padding-top: 40px;">
		</c:when>
		<c:otherwise>
			<div id="saresults">
		</c:otherwise>
	</c:choose>
	
		<div style="max-width: 600px; margin-left: auto; margin-right: auto;">
			<h1><spring:message code="label.SelfAssessmentResults" /></h1>		
			
			<div class="customtext">
				${SAReportConfiguration.introduction}
			</div>
			
			<c:choose>
				<c:when test="${SAReportConfiguration.targetDatasetSelection}">				
					<b><spring:message code="label.SelectComparisonDataset" /></b><br />
					<select class="form-control" id="datasetselector" style="display: inline; width: auto;" onchange="saResultModel.loadSAData();">
						<option value="0">${form.getMessage("label.NoComparison")}</option>
						<c:forEach items="${SATargetDatasets}" var="dataset">																							
							<option value="<esapi:encodeForHTMLAttribute>${dataset.id}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML>${dataset.name}</esapi:encodeForHTML></option>
						</c:forEach>
					</select>				
				</c:when>
				<c:otherwise>
					<input type="hidden" id="datasetselector" value="0" />
				</c:otherwise>
			</c:choose>
		</div>
		
		<c:if test="${SAReportConfiguration.charts}">
			<div style="margin-top: 40px;">
			  <canvas id="SAChart"></canvas>
			</div>
			
			<c:if test="${SAReportConfiguration.separateCompetencyTypes}">
				<!-- ko if: selectedDataset() > 0 -->
				<div style="margin-top: 20px;">
				  <canvas id="SAChart2"></canvas>
				</div>
				<!-- /ko -->
			</c:if>
		</c:if>
		
		<c:if test="${SAReportConfiguration.resultsTable}">
			<div>
				<table class="table table-bordered table-striped" style="max-width: 600px; margin-left: auto; margin-right: auto; margin-top: 40px;">
					<thead>
						<tr style="background-color: #245077; color: #fff">
							<th>Human Values</th>
							<c:if test="${SAReportConfiguration.competencyType}">
								<th>${form.getMessage("label.Type")}</th>
							</c:if>
							<c:if test="${SAReportConfiguration.targetScores}">
							<!-- ko if: selectedDataset() > 0 -->
								<th data-bind="text: comparisonDatasetName"></th>
							<!-- /ko -->
							</c:if>
							<th>${form.getMessage("label.you")}</th>
						</tr>
					</thead>
					<tbody data-bind="foreach: criteria()">
						<tr>
							<td data-bind="text: name"></td>
							<c:if test="${SAReportConfiguration.competencyType}">
								<td data-bind="text: type"></td>
							</c:if>
							<c:if test="${SAReportConfiguration.targetScores}">
							<!-- ko if: $parent.selectedDataset() > 0 -->
								<td  data-bind="text: $parent.comparisonValues()[$index()]"></td>
							<!-- /ko -->
							</c:if>
							<td>
								<span data-bind="text: $parent.values()[$index()]"></span>
								<c:if test="${SAReportConfiguration.gaps}">
									<span data-bind="text: $parent.gap($parent.values()[$index()], $parent.comparisonValues()[$index()]), attr: {style: $parent.values()[$index()] > $parent.comparisonValues()[$index()] ? 'color: #02ab05' : 'color: #e90000' }"></span>
								</c:if>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</c:if>
		
		<c:if test="${SAReportConfiguration.performanceTable}">
			<!-- ko if: selectedDataset() > 0 -->
			<div style="max-width: 600px; margin-left: auto; margin-right: auto; margin-top: 40px;">
				<h2>${form.getMessage("label.SAYourOwnValuesPriorities")}</h2>
				<table class="table table-bordered table-striped">
					<thead>
						<tr style="background-color: #245077; color: #fff">
							<th>${form.getMessage("label.SAOwnValuesPriorities")}</th>
							<th>${form.getMessage("label.SAPotentialBlindSpots")}</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td data-bind="foreach: criteriaAboveBelowAverage(true)">
								<div data-bind="text: name"></div>						
							</td>
							<td data-bind="foreach: criteriaAboveBelowAverage(false)">
								<div data-bind="text: name"></div>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			<!-- /ko -->
		</c:if>	
		
		<div style="max-width: 600px; margin-left: auto; margin-right: auto;">
			<div class="customtext">
				${SAReportConfiguration.customFeedback}
			</div>
		</div>
		
		<!-- 
		<c:if test="${forpdf == null && form.survey.downloadContribution}">
			<div style="text-align: center; margin-bottom: 20px;">
				<a href="javascript:;" id="pdfDownloadButtonThanksInner" onclick="showExportDialogAndFocusEmail(this)" class="btn btn-default">${form.getMessage("label.GetPDF")}</a>		
			</div>
		</c:if>
		 -->		
	</div>
	
	<c:if test="${forpdf == null}">
	
	<div class="modal" id="ask-export-dialog" data-backdrop="static" role="dialog">	
			<div class="modal-dialog">
		    <div class="modal-content">
			<div class="modal-header">
				<b><spring:message code="label.Info" /></b>
			</div>
			<div class="modal-body">
				<p>
					<c:choose>
						<c:when test="${runnermode == true}">
							${form.getMessage("question.EmailForPDF")}
						</c:when>
						<c:otherwise>
							<spring:message code="question.EmailForPDF" />
						</c:otherwise>	
					</c:choose>
				</p>
				<input type="text" maxlength="255" name="email" id="email" />
				<span id="ask-export-dialog-error" class="validation-error hideme">
					<c:choose>
						<c:when test="${runnermode == true}">
							${form.getMessage("message.ProvideEmail")}
						</c:when>
						<c:otherwise>
							<spring:message code="message.ProvideEmail" />
						</c:otherwise>	
					</c:choose>
				</span>
				<div class="captcha" style="margin-left: 0px; margin-bottom: 20px; margin-top: 20px;">						
					<c:if test="${captchaBypass !=true}">
					<%@ include file="../captcha.jsp" %>					
					</c:if>
		       	</div>
		       	<span id="ask-export-dialog-error-captcha" class="validation-error hideme">       		
		       		<c:if test="${captchaBypass !=true}">
		       		<c:choose>
						<c:when test="${runnermode == true}">
							${form.getMessage("message.captchawrongnew")}
						</c:when>
						<c:otherwise>
							<spring:message code="message.captchawrongnew" />
						</c:otherwise>	
					</c:choose>
		       		</c:if>
		       	</span>
			</div>
			<div class="modal-footer">
				<c:choose>
					<c:when test="${responsive != null}">
						<button type="button" style="text-decoration: none"  class="btn btn-primary btn-lg" onclick="startExport()">${form.getMessage("label.OK")}</button>
						<button type="button" style="text-decoration: none"  class="btn btn-default btn-lg" onclick="hideModalDialog($('#ask-export-dialog'))">${form.getMessage("label.Cancel")}</button>
					</c:when>
					<c:when test="${runnermode == true}">
						<button type="button" class="btn btn-primary" onclick="startExport()">${form.getMessage("label.OK")}</button>
						<button type="button" class="btn btn-default" onclick="hideModalDialog($('#ask-export-dialog'))">${form.getMessage("label.Cancel")}</button>
					</c:when>
					<c:otherwise>
						<button type="button" class="btn btn-primary" onclick="startExport()"><spring:message code="label.OK" /></button>
						<button type="button" class="btn btn-default" onclick="hideModalDialog($('#ask-export-dialog'))"><spring:message code="label.Cancel" /></button>
					</c:otherwise>	
				</c:choose>				
			</div>
			</div>
			</div>
		</div>
		
		<script type="text/javascript">	
		
			const separateCharts = ${SAReportConfiguration.separateCompetencyTypes};
			const limitTableLines = ${SAReportConfiguration.limitTableLines};
			const selectedChartType = "${SAReportConfiguration.selectedChart}";
			let chartType = "radar";
			if (selectedChartType == "BAR") chartType = "bar";
			if (selectedChartType == "LINE") chartType = "line";
					
			function saResultsViewModel() {
				let root = this;
				
				this.values = ko.observableArray([]);
				this.comparisonValues = ko.observableArray([]);
				this.comparisonDatasetName = ko.observable("");
				this.criteria = ko.observableArray([]);
				this.selectedDataset = ko.observable(${form.initialTargetDataset});
				
				this.loadSAData = function() {
					this.selectedDataset(parseInt($("#datasetselector").val()));
					
					$.ajax({
						type:'GET',
						url: "${contextpath}/${form.survey.shortname}/management/selfassessment/results?dataset=" + root.selectedDataset() + "&contribution=${form.answerSets[0].uniqueCode}",
						dataType: 'json',
						cache: false,
						async: false,
						success: function (result) {
							root.values.removeAll();
							root.comparisonValues.removeAll();
							if (result.comparisonDataset != null) {
								root.comparisonDatasetName(result.comparisonDataset.name);
							}
							root.criteria(result.criteria);
							for (let i = 0; i < result.values.length; i++) {
								root.values.push(result.values[i]);
								if (result.comparisonDataset != null) {
									root.comparisonValues.push(result.comparisonValues[i]);
								}
							}
							
							<c:if test="${SAReportConfiguration.charts}">
								root.createChart(result);
							</c:if>
						}
					});
				}
				
				this.getConfig = function(own, comparison, result) {
					const datasets = [];
					
					if (comparison) {
						datasets.push(
							{
						      label: result.comparisonDataset.name,
						      data: result.comparisonValues,
						      borderColor: 'rgba(255, 0, 0, 1)',
						      backgroundColor: 'rgba(255, 0, 0, 0.25)',
						    }
						);
					};
					if (own) {
						datasets.push(
							{
						      label: '${form.getMessage("label.you")}',
						      data: result.values,
						      borderColor: 'rgba(0, 0, 255. 1)',
						      backgroundColor: 'rgba(0, 0, 255, 0.25)',
						    }
						 );
					}

					const data = {
					  labels: result.criteriaNames,
					  datasets: datasets
					};
					
					const config = {
					  type: chartType,
					  data: data,
					  options: {
					    responsive: true,
					    
					    <c:if test="${!SAReportConfiguration.legend}">
					      legend: {
				            display: false
				          },
				        </c:if>
				          
				        <c:if test="${!SAReportConfiguration.scale}">
				          scale: {
				              ticks: {
				                  display: false
				              },
				          },
				        </c:if>   
				          
					    plugins: {
					      title: {
					        display: true,
					        text: 'Chart.js Radar Chart'
					      },
					      
					    }
					  },
					};
					
					return config;
				}
				
				this.createChart = function(result) {					
					const config = (result.comparisonDataset == null || separateCharts) ? root.getConfig(true, false, result) :  root.getConfig(true, true, result);
					new Chart(document.getElementById('SAChart'), config);		
					
					if (result.comparisonDataset != null && separateCharts) {
						const config2 = root.getConfig(false, true, result);
						new Chart(document.getElementById('SAChart2'), config2);		
					}
				}
				
				this.gap = function (v1, v2) {
					if (v1 == v2) return "";
					if (v1 > v2) return "(+" + (Math.round((v1-v2)*10) / 10) + ")";
					if (v1 < v2) return "(-" + (Math.round((v2-v1)*10) / 10) + ")";
				}
				
				this.criteriaAboveBelowAverage = function(above) {
					var result = [];
					
					for (let i = 0; i < root.criteria().length; i++) {
						var v1 = root.values()[i];
						var v2 = root.comparisonValues()[i];
						if (above && v1 > v2) {
							var entry = {
								name: root.criteria()[i].name,
								value: (v1-v2)
							}
							
							result.push(entry);
						}
						if (!above && v1 < v2) {
							var entry = {
								name: root.criteria()[i].name,
								value: (v1-v2)
							}
							
							result.push(entry);
						}
					}
					
					result.sort((a, b) => a.value - b.value)
					
					if (limitTableLines == 0) {
						return result;
					}
					
					return result.slice(0,limitTableLines);
				}
			}
			
			let saResultModel = new saResultsViewModel();
			ko.applyBindings(saResultModel, $('#saresults')[0]);			
			
			$(function() {	
				$("#datasetselector").val(saResultModel.selectedDataset());
				saResultModel.loadSAData();				
			})	
		</script>
		
	</c:if>
