<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
	
	<link href="${contextpath}/resources/css/yellowfocus.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css"></link>
			
	<c:choose>
		<c:when test="${forpdf == null && responsive == null}">
			<div id="saresults" class="fullpageform" style="max-width: 1000px; margin-left: auto; margin-right: auto;">
		</c:when>
		<c:when test="${responsive != null}">
			<div id="saresults" style="padding: 5px; padding-top: 40px; max-width: 100%; overflow-x: hidden; overflow-wrap: anywhere;">
		</c:when>
		<c:otherwise>
			<div id="saresults">
		</c:otherwise>
	</c:choose>
	
		<div style="max-width: 600px; margin-left: auto; margin-right: auto;">
			<h1>${form.getMessage("label.SelfAssessmentResults")}</h1>
			
			<div class="customtext">
				${SAReportConfiguration.introduction}
			</div>
			
			<c:choose>
				<c:when test="${forpdf == null && SAReportConfiguration.targetDatasetSelection}">				
					<b>${form.getMessage("label.SelectComparisonDataset")}</b><br />
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
		
			<c:choose>
				<c:when test="${forpdf == null}">
					<c:choose>
						<c:when test="${SAReportConfiguration.separateCompetencyTypes}">
							<!-- ko foreach: competencyTypes()  -->
								<div style="margin-top: 20px;">
								  <canvas data-bind="attr: {id: 'SAChart' + $index()}"></canvas>
								</div>								
							<!-- /ko -->
						</c:when>
						<c:otherwise>
							<div style="margin-top: 40px;">
							  <canvas id="SAChart"></canvas>
							</div>
						</c:otherwise>
					</c:choose>					
				</c:when>
				<c:otherwise>
			
					<c:if test="${charts != null}">
						<c:forEach items="${charts}" var="chart">
							<div style="width: 100%; max-width: 600px; margin-left: auto; margin-right: auto; text-align: left;">
								<img src="data:image/png;base64,${chart}" style="width: 700px; border: 1px solid #333;" />
							</div>
						</c:forEach>
					</c:if>
					
				</c:otherwise>
			</c:choose>
		</c:if>
		
		<c:if test="${SAReportConfiguration.resultsTable}">
			<div style="">
				<table class="table table-bordered table-striped" style="page-break-inside: avoid; width: 100%; max-width: 600px; margin-left: auto; margin-right: auto; margin-top: 40px; text-align: left;">
					<thead>
						<tr style="background-color: #245077; color: #fff">
							<th>${form.getMessage("label.HumanValues")}</th>
							<c:if test="${SAReportConfiguration.competencyType}">
								<th>${form.getMessage("label.Type")}</th>
							</c:if>
							<c:if test="${SAReportConfiguration.targetScores}">
							<!-- ko if: selectedDataset() > 0 -->
								<th data-bind="text: comparisonDatasetName">
									<c:if test="${ComparisonDataset != null}">
										<span>${ComparisonDataset.name}</span>
									</c:if>
								</th>
							<!-- /ko -->
							</c:if>
							<th>${form.getMessage("label.YourAnswer")}</th>
						</tr>
					</thead>
					<tbody data-bind="foreach: criteria()">
						<c:choose>
							<c:when test="${forpdf == null}">
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
							</c:when>
							<c:otherwise>
								<c:forEach items="${SAResult.criteria}" var="criterion" varStatus="counter">
									<tr>
										<td>${criterion.name}</td>
										<c:if test="${SAReportConfiguration.competencyType}">
											<td>${criterion.type}</td>
										</c:if>
										<c:if test="${SAReportConfiguration.targetScores}">
											<td> ${SAResult.comparisonValues.get(counter.index)} </td>
										</c:if>
										<td>
											<span> ${SAResult.values.get(counter.index)}</span>
											<c:if test="${SAReportConfiguration.gaps}">
												<c:choose>
													<c:when test="${SAResult.values.get(counter.index) > SAResult.comparisonValues.get(counter.index) }">
														<span style="color: #02ab05">
													</c:when>
													<c:otherwise>
														<span style="color: #e90000">
													</c:otherwise>
												</c:choose>
													${SAResult.gap(SAResult.values.get(counter.index), SAResult.comparisonValues.get(counter.index))}
												</span>
											</c:if>
										</td>
									</tr>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
			</div>
		</c:if>
		
		<c:if test="${SAReportConfiguration.performanceTable}">
			<!-- ko if: selectedDataset() > 0 -->
			<div style="margin-top: 40px; width: 100%; max-width: 600px; margin-left: auto; margin-right: auto;">
				<h2>${form.getMessage("label.SAYourOwnValuesPriorities")}</h2>
				<table class="table table-bordered table-striped" style="page-break-inside: avoid; margin-top: 10px; width: 100%; max-width: 600px; margin-left: auto; margin-right: auto;">
					<thead>
						<tr style="background-color: #245077; color: #fff">
							<th>${form.getMessage("label.SAOwnValuesPriorities")}</th>
							<th>${form.getMessage("label.SAPotentialBlindSpots")}</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td data-bind="foreach: criteriaAboveBelowAverage(true)">
								<c:choose>
									<c:when test="${forpdf == null}">							
										<div data-bind="text: name"></div>
									</c:when>
									<c:otherwise>
										<c:forEach items="${SAResult.criteriaAboveBelowAverage(true, SAReportConfiguration.limitTableLines)}" var="c">
											<p>${c}</p>
										</c:forEach>
									</c:otherwise>
								</c:choose>		
							</td>
							<td data-bind="foreach: criteriaAboveBelowAverage(false)">
								<c:choose>
									<c:when test="${forpdf == null}">							
										<div data-bind="text: name"></div>
									</c:when>
									<c:otherwise>
										<c:forEach items="${SAResult.criteriaAboveBelowAverage(false, SAReportConfiguration.limitTableLines)}" var="c">
											<p>${c}</p>
										</c:forEach>
									</c:otherwise>
								</c:choose>	
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			<!-- /ko -->
		</c:if>	
		
		<div style="width: 600px; margin-left: auto; margin-right: auto;">
			<div class="customtext">
				${SAReportConfiguration.customFeedback}
			</div>
		</div>
		
		<div id="contribution-id-save-hint" style="color: #777; margin-top: 20px; margin-bottom: 20px; max-width: 616px; margin-left: auto; margin-right: auto;">
			${form.getMessage("label.ContributionSavingHint")}:&nbsp;<esapi:encodeForHTML>${uniqueCode}</esapi:encodeForHTML>
			<button type="button" class="unstyledbutton" style="margin-left: 8px; text-decoration: none; display: inline-block;" id="copyIconButton" onclick="navigator.clipboard.writeText('${uniqueCode}');" data-toggle="tooltip" aria-label='${form.getMessage("label.CopyContributionID")}' title='${form.getMessage("label.CopyContributionID")}'>
				<i class="glyphicon glyphicon-copy copy-icon"></i>
			</button>
			<br />
			${form.getMessage("label.ContributionSavingExplanation")}
		</div>
				
		<c:if test="${forpdf == null && form.survey.downloadContribution}">
			<div style="text-align: center; margin-bottom: 20px;">
				<a href="javascript:;" id="pdfDownloadButtonThanksInner" onclick="showExportDialogAndFocusEmail(this)" class="btn btn-primary">${form.getMessage("label.GetPDF")}</a>		
			</div>
		</c:if>
				
	</div>
	
	<div id="chart-download" style="display: none"></div>
	
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

			function truncateText(text, length) {
				if (text.length <= length) {
					return text;
				}

				return text.substring(0, length) + '\u2026'
			}

			let MAX_NAME_LENGTH = 10;
		
			const separateCharts = ${SAReportConfiguration.separateCompetencyTypes};
			const limitTableLines = ${SAReportConfiguration.limitTableLines};
			const selectedChartType = "${SAReportConfiguration.selectedChart}";
			let chartType = "radar";
			if (selectedChartType == "BAR") chartType = "bar";
			if (selectedChartType == "LINE") chartType = "line";
			
			let charts = [];
					
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
								root.comparisonDatasetName(truncateText(result.comparisonDataset.name,MAX_NAME_LENGTH));
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
				
				this.getConfig = function(all, type, result) {
					const datasets = [];
					const types = root.competencyTypes();
					let labels = [];
					
					if (all) {
						datasets.push(
								{
							      label: '${form.getMessage("label.YourAnswer")}',
							      data: result.values,
							      borderColor: 'rgba(0, 0, 255, 1)',
							      backgroundColor: 'rgba(0, 0, 255, 0.25)',
							    }
							 );
						if (result.comparisonDataset != null) {
							datasets.push(
								{
							      label: truncateText(result.comparisonDataset.name,MAX_NAME_LENGTH),
							      data: result.comparisonValues,
							      borderColor: 'rgba(255, 0, 0, 1)',
							      backgroundColor: 'rgba(255, 0, 0, 0.25)',
							    }
							);
						}
						labels = result.criteriaNames;
					} else {
						datasets.push(
								{
							      label: '${form.getMessage("label.YourAnswer")}',
							      data: result.valuesForTypes[type],
							      borderColor: 'rgba(0, 0, 255, 1)',
							      backgroundColor: 'rgba(0, 0, 255, 0.25)',
							    }
							 );
						if (result.comparisonDataset != null) {
							datasets.push(
								{
							      label: truncateText(result.comparisonDataset.name,MAX_NAME_LENGTH),
							      data: result.comparisonValuesForTypes[type],
							      borderColor: 'rgba(255, 0, 0, 1)',
							      backgroundColor: 'rgba(255, 0, 0, 0.25)',
							    }
							);
						}
						labels = root.criteriaNamesForTypes()[type];
					}
					
					const data = {
					  labels: labels,
					  datasets: datasets
					};
					
					const config = {
					  type: chartType,
					  data: data,
					  options: {
					    responsive: true,
					    
					    <c:if test='${SAReportConfiguration.selectedChart == "SPIDER"}'>	
					    	//this is a fix for a bug in chartjs
						    tooltips: {
					          	callbacks: {
					            	title: (items, data) => {
					              	if (!items.length) {
					              		// no datasets -> do not set a title
					                	return '';
					                }
					              	
					              	var datasetIndexes = [];
					              	for (var i = 0; i < items.length; i++) {
					              		if (datasetIndexes.length > 0 && !datasetIndexes.includes(items[i].datasetIndex)) {
					              			// multiple datasets -> do not set a title
					              			return '';
					              		}
					              		
					              		datasetIndexes.push(items[i].datasetIndex);
					              	}
					              						    				              	
					                return data.datasets[items[0].datasetIndex].label;
					              },
					              label: function(context) {
				                        let label = data.labels[context.index];
				                        if (label) {
				                            label += ': ';
				                        }
				                        return label + context.value;
				                    },
					            }
					          },				          
				         </c:if>
					    
					    <c:if test="${!SAReportConfiguration.legend}">
					      legend: {
				            display: false
				          },
				        </c:if>
				          
				        <c:if test="${!SAReportConfiguration.scale}">
				        
				          <c:if test='${SAReportConfiguration.selectedChart == "SPIDER"}'>				        
					          scale: {
					              ticks: {
					                  display: false
					              },
					          },
				          </c:if>
				          
					     <c:if test='${SAReportConfiguration.selectedChart != "SPIDER"}'>	
				          	scales: {
					            yAxes: [{
					                ticks: {
					                    display: false
					                }
					            }]
					        },
				          </c:if>
				          
				        </c:if>   
				          
				          title: {
				              display: !all,
				              text: types[type],
				          },
				          
			          animation: {
							onComplete: function (animation) {
								var base64 = this.toBase64Image();								
								var a = document.createElement("a");
								$(a).text("chart.png").attr({href: base64, download: "chart.png"});
								
								$("#chart-download").append(a);								
							}
						},
				          
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
					$("#chart-download").empty();
					
					for (let i = 0; i < charts.length; i++) {
						charts[i].destroy();
					}
					
					if (separateCharts) {
						const types = root.competencyTypes();
						for (let i = 0; i < types.length; i++) {
							charts[i] = new Chart(document.getElementById('SAChart' + i),  root.getConfig(false, i, result));								
						}
						
					} else {
						charts[0] = new Chart(document.getElementById('SAChart'),  root.getConfig(true, 0, result));	
					}
				}
				
				this.gap = function (v1, v2) {
					if (v1 == v2) return "";
					if (v1 > v2) return "(+" + (Math.round((v1-v2)*10) / 10) + ")";
					if (v1 < v2) return "(-" + (Math.round((v2-v1)*10) / 10) + ")";
				}
				
				this.criteriaAboveBelowAverage = function(above) {
					const result = [];
					
					for (let i = 0; i < root.criteria().length; i++) {
						const v1 = root.values()[i];
						const v2 = root.comparisonValues()[i];
						if (above && v1 > v2) {
							const entry = {
								name: root.criteria()[i].name,
								value: (v2-v1)
							}
							
							result.push(entry);
						}
						if (!above && v1 < v2) {
							const entry = {
								name: root.criteria()[i].name,
								value: (v1-v2)
							}
							
							result.push(entry);
						}
					}

					const valueThenNameComparer = (a, b) => {
						const diff = a.value - b.value

						//If they are (almost) equal, sort them by name
						if (Math.abs(diff) < 0.0001) {
							if (a.name < b.name) return -1
							if (a.name > b.name) return 1
							return 0
						}

						return Math.sign(diff)
					}

					result.sort(valueThenNameComparer);
					
					if (limitTableLines == 0) {
						return result;
					}
					
					return result.slice(0,limitTableLines);
				}
				
				this.competencyTypes = function() {
					var result = [];
					
					for (let i = 0; i < root.criteria().length; i++) {
						if (!result.includes(root.criteria()[i].type)) {
							result.push(root.criteria()[i].type);
						}						
					}
					
					return result;
				}
				
				this.criteriaNamesForTypes = function() {
					var result = [];
					var types = [];
					
					for (let i = 0; i < root.criteria().length; i++) {
						if (!types.includes(root.criteria()[i].type)) {
							types.push(root.criteria()[i].type);
						}	
						var index = types.indexOf(root.criteria()[i].type);
						if (result.length < index + 1) {
							result.push([]);							
						}
						result[index].push(truncateText(root.criteria()[i].name, MAX_NAME_LENGTH));
					}		
					
					console.log(result)
					
					return result;
				}
			}
			
			let saResultModel = new saResultsViewModel();
			ko.applyBindings(saResultModel, $('#saresults')[0]);			
			
			$(function() {	
				$("#datasetselector").val(saResultModel.selectedDataset());
				saResultModel.loadSAData();				
			})	
			
			function startExport()
			{
				$("#ask-export-dialog").find(".validation-error").hide();
				
				var mail = $("#email").val();
				if (mail.trim().length == 0 || !validateEmail(mail))
				{
					$("#ask-export-dialog-error").show();
					return;
				};	
				
				var dataset = $("#datasetselector").val();
				
				var charts = [];
				
				$('#chart-download a').each(function(){
					var href = $(this).attr("href");
					charts.push(href);
				});
						
				<c:choose>
					<c:when test="${!captchaBypass}">
						var challenge = getChallenge();
					    var uresponse = getResponse();
					    
					    var data = {email : mail, recaptcha_challenge_field : challenge, 'g-recaptcha-response' : uresponse, charts: JSON.stringify(charts)};
						if ($('#captcha_id').length > 0) {
							data["captcha_id"] =  $('#captcha_id').val();
							data["captcha_useaudio"] =  $('#captcha_useaudio').val();
							data["captcha_original_cookies"] = $('#captcha_original_cookies').val();
						}
					
						$.ajax({
							type:'POST',
							  beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
							  url: "${contextpath}/runner/createsapdf/${uniqueCode}/" + dataset,
							  data: data,
							  cache: false,
							  success: function( data ) {
								  
								  if (data == "success") {
										$('#ask-export-dialog').modal('hide');
										showSuccess(message_PublicationExportSuccess2.replace('{0}', mail));
								  	} else if (data == "errorcaptcha") {
								  		$("#ask-export-dialog-error-captcha").show();
								  		reloadCaptcha();
									} else {
										showError(message_PublicationExportFailed);
										reloadCaptcha();
									};
							}
						});							
					</c:when>
					<c:otherwise>			
						$.ajax({				
							type:'POST',
							  beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
							  url: "${contextpath}/runner/createsapdf/${uniqueCode}/" + dataset,
							  data: {email : mail, recaptcha_challenge_field : '', 'g-recaptcha-response' : '', charts: JSON.stringify(charts)},
							  cache: false,
							  success: function( data ) {
								  
								  if (data == "success") {
										$('#ask-export-dialog').modal('hide');
										showSuccess(message_PublicationExportSuccess2.replace('{0}', mail));
									} else {
										showError(message_PublicationExportFailed);
										reloadCaptcha();
									};
							}
						});							
					</c:otherwise>
				</c:choose>
			}
		</script>
		
	</c:if>
