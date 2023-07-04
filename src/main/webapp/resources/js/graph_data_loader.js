function loadGraphDataInnerForResults(div, chartCallback, chartType, scheme, legend, canvasWidth, score) {

	const queryParams = {
		surveyid : div.data("survey-id"),
		questionuid : div.data("question-uid"),
		languagecode : div.data("language-code"),
		uniquecode : "",// not needed for privileged users like form managers
		resultsview : true
	}

	const flags = {
		forResults : true,
		removeIfEmpty : true,
		forModal : false,
		forStartpage : false,
	}

	loadGraphDataInnerCommon(div, queryParams, flags, chartCallback, chartType, scheme, legend, canvasWidth, score);
}

function loadGraphDataInnerForRunner(div, surveyid, questionuid, languagecode, uniquecode, chartCallback, removeIfEmpty, forModal, forStartpage, canvasWidth) {

	const queryParams = {
		surveyid : surveyid,
		questionuid : questionuid,
		languagecode : languagecode,
		uniquecode : uniquecode,
		resultsview : null
	}

	const flags = {
		forResults : false,
		removeIfEmpty : removeIfEmpty,
		forModal : forModal,
		forStartpage : forStartpage,
	}

	const chartType = null;
	const scheme = null;
	const legend = false;

	loadGraphDataInnerCommon(div, queryParams, flags, chartCallback, chartType, scheme, legend, canvasWidth, null);
}


function loadGraphDataInnerCommon(div, queryParams, flags, chartCallback, chartType, scheme, legend, canvasWidth, score) {

	var data =
		"surveyid=" + queryParams.surveyid +
		"&questionuid=" + queryParams.questionuid +
		"&languagecode=" + queryParams.languagecode +
		"&uniquecode=" + queryParams.uniquecode +
		(queryParams.resultsview ? "&resultsview=true" : "");

	$.ajax({
		type: "GET",
		url: contextpath + "/runner/delphiGraph",
		data: data,
		beforeSend: function (xhr) {
			xhr.setRequestHeader(csrfheader, csrftoken);
		},
		error: function (data) {
			showAjaxError(data.status)
		},
		success: function (result, textStatus) {
			const elementWrapper = $(div).closest(".elementwrapper, .statelement-wrapper");
			if (textStatus === "nocontent") {
				if (flags.removeIfEmpty) {
					$(elementWrapper).find(".delphi-chart").remove();
					$(elementWrapper).find(".chart-wrapper").hide();
					if (flags.forResults) {
						$(elementWrapper).find(".chart-controls").hide();
						$(elementWrapper).find(".no-chart-results-message").text(infoNoData);
						$(elementWrapper).find(".chart-wrapper").data("has-no-data", "true");
					} else {
						$(elementWrapper).find(".chart-wrapper-loader").hide();
					}
				}

				if (!flags.forResults) {
					addStatisticsToAnswerText(div, null);
				}
				return;
			}

			if (chartType == null) {
				chartType = result.chartType;
			} else {
				result.chartType = chartType;
			}

			const forModal = flags.forModal === true;

			if (result.questionType === "FreeText") {
				if (flags.forResults && scheme == null) {
					scheme = "d3.scale.category20";
				}

				createWordCloud(forModal ? null : div, result, result.chartType, flags.forResults, flags.forStartpage, scheme);
				return;
			}

			if (flags.forResults) {
				if (scheme == null) {
					scheme = "tableau.Tableau10";
				}
				$(elementWrapper).find("option[data-type='textual']").hide();
				$(elementWrapper).find("option[data-type='numerical']").show();

				if (elementWrapper.find(".chart-hide-unanswered:checked").length > 0 && result.data != null){
					result.data = result.data.filter(el => el.value > 0)
				}

				var showLegendBox = result.questionType === "Matrix" || result.questionType === "Rating" || chartType === "Pie";
				legend = legend == undefined ? showLegendBox : showLegendBox && legend;

				if (chartType === "Pie" && legend && result.data != null){
					let dataSize = result.data.length
					let sizeSelect = elementWrapper.find(".chart-size")

					let sizeChanged = false

					let selectedOption = sizeSelect.find(":selected")
					if (selectedOption.attr("value") == "small" && dataSize > 10){
						selectedOption.removeAttr("selected")
						selectedOption = sizeSelect.find("[value=medium]")
						selectedOption.attr("selected", "selected")
						sizeChanged = true;
					}

					if (selectedOption.attr("value") == "medium" && dataSize > 20){
						selectedOption.removeAttr("selected")
						selectedOption = sizeSelect.find("[value=large]")
						selectedOption.attr("selected", "selected")
						sizeChanged = true;
					}

					if (sizeChanged) {
						canvasWidth = getChartCanvasHeightAndWidth(sizeSelect.find(":selected").attr("value")).width;
					}

					let legendCheck = elementWrapper.find(".chart-legend")
					if (dataSize > 30 && !legendCheck.prop("auto-uncheck")){
						legend = false

						legendCheck.removeAttr("checked")
						legendCheck.prop("auto-uncheck", true)
					}
				}
			}

			var chartData = {};
			var chartOptions = {};
			if (flags.forResults) {
				chartOptions = {
						scaleShowValues: true,
						responsive: false,
						scales: {
							yAxes: [{ticks: {beginAtZero: true, autoSkip: false, callback: chartLabelCallback}}],
							xAxes: [{ticks: {beginAtZero: true, autoSkip: false, callback: chartLabelCallback}}]
						},
						legend: {display: legend},
						animation: {
							onComplete: function (animation) {
								var base64 = this.toBase64Image();
								
								$(div).closest(".statelement-wrapper").find('.chart-download').attr({href: base64, download: result.questionType + ".png"});
								
								$(div).closest(".statelement-wrapper").find('.chart-clipboard').unbind('click');
								$(div).closest(".statelement-wrapper").find('.chart-clipboard').click(function(){
									 copyBase64ImageToClipboard(base64);
								})
							}
						},
						plugins: {
							colorschemes: {
								scheme: scheme
							}
						}
				};
			} else {
				chartOptions = {
						maintainAspectRatio: false,
						scaleShowValues: true,
						scales: {
							yAxes: [{ticks: {beginAtZero: true, autoSkip: false}}],
							xAxes: [{ticks: {beginAtZero: true, autoSkip: false}}]
						},
						legend: {display: false}
					};

				chartOptions.scales.xAxes[0].ticks.callback = chartOptions.scales.yAxes[0].ticks.callback = forModal ? wrapChartLabelCallback : chartLabelCallback;
			}

			if (result.questionType === "Ranking") {								
				//only bar chart and line chart are available for ranking questions
				$(elementWrapper).find(".chart-type option[data-type='numerical']").each(function(index) {
					if (index > 1) {
						$(this).remove();
					}
				});
			}

			switch (result.questionType) {
				case "MultipleChoice":
				case "SingleChoice":
				case "Number":
				case "Formula":
				case "Ranking":
					var graphData = result.data;

					chartData = {
						datasets: [{
							label: '',
							originalLabel: '',
							data: graphData.map(function (g) {
								return g.value
							})
						}],
						labels: graphData.map(function (g) {
							return truncateLabel(normalizeLabel(g.label), canvasWidth);
						}),
						originalLabels: graphData.map(function (g) {
							return normalizeLabel(g.label);
						})
					};
					break;

				case "Matrix":
				case "Rating":
					var questions = result.questions;
					var datasets = [];
					var labels = undefined;
					var originalLabels = undefined;

					for (var i = 0; i < questions.length; i++) {
						var question = questions[i];

						datasets.push({
							data: question.data.map(function (d) {
								return d.value;
							}),
							label: truncateLabel(normalizeLabel(question.label), canvasWidth),
							originalLabel: normalizeLabel(question.label)
						});

						if (!labels) {
							labels = question.data.map(function (d) {
								return truncateLabel(normalizeLabel(d.label), canvasWidth);
							});
							originalLabels = question.data.map(function (d) {
								return normalizeLabel(d.label);
							});
						}
					}

					chartData = {
						datasets,
						labels,
						originalLabels
					}

					if (!flags.forResults) {
						chartOptions.legend.display = forModal || (result.questions.length < 6);
					}

					break;

				default:
					if (!flags.forResults) {
						addStatisticsToAnswerText(div, result);
						$(elementWrapper).find(".chart-wrapper-loader").hide();
					}
					return;
			}

			var chart = {
				data: chartData,
				options: chartOptions
			}

			switch (result.chartType) {
				case "Bar":
					chart.type = "horizontalBar";
					chart.options.scales.xAxes[0].gridLines = {drawBorder: false};
					chart.options.scales.yAxes[0].gridLines = {drawOnChartArea: false, display: false};
					chart.options.layout = {padding: {right: 20}};
					Chart.helpers.each(chart.data.datasets.forEach(function (dataset) {
						dataset.maxBarThickness = 32;
					}));
					if (score == undefined || score) {
						let chainOnComplete = function() {};
						if (!!chart.options.animation) {
							if (!!chart.options.animation.onComplete) {
								chainOnComplete = chart.options.animation.onComplete;
							}
						} else {
							chart.options.animation = {};
						}
						chart.options.animation.onComplete = function(animation) {
							const chartInstance = this.chart;
							const ctx = chartInstance.ctx;
							ctx.fillStyle= "#666";
							ctx.font = "bold";
							ctx.textAlign = "left";
							ctx.textBaseline = "middle";
							Chart.helpers.each(this.data.datasets.forEach(function (dataset, i) {
								const meta = chartInstance.controller.getDatasetMeta(i);
								Chart.helpers.each(meta.data.forEach(function (bar, index) {
									let xr = bar._model.x+5;
									const value = dataset.data[index];
									const valueMeasure = ctx.measureText(value);
									const textWidth = valueMeasure.width;
									if (xr+textWidth > chartInstance.width) {
										ctx.fillStyle= "#eee";
										xr = bar._model.x-textWidth-8;
										ctx.fillText(value, xr, bar._model.y);
									} else {
										ctx.fillStyle= "#666";
										ctx.fillText(value, xr, bar._model.y);
									}
								}), this);
							}), this);
							chainOnComplete.call(this, animation);
						};
					}
					break;
				case "Column":
					chart.type = "bar";
					break;
				case "Line":
					chart.type = "line";
					break;
				case "Pie":
					chart.type = "pie";
					if (!flags.forResults) {
						chart.options.legend.display = true;
					}
					delete chart.options.scales;
					break;
				case "Radar":
					chart.type = "radar";
					delete chart.options.scales;
					chart.options.scale = {pointLabels: {callback: forModal ? wrapChartLabelCallback : chartLabelCallback}, ticks: {beginAtZero: true, precision: 0}};
					break;
				case "Scatter":
					chart.type = "line";
					chart.options.showLines = false;
					break;
				default:
					chart.type = "horizontalBar";
					break;
			}

			if (!flags.forResults && !forModal && chart.data.labels.length > 5) {
				chart.options.legend.display = false;
			}

			chart.options.tooltips = {
				 // Disable the on-canvas tooltip
				enabled: false,

				custom: function(tooltipModel) {
					if (["Matrix", "SingleChoice"].includes(result.questionType) && chart.type === "radar") {
						if (tooltipModel.dataPoints && tooltipModel.dataPoints.length > 0 && tooltipModel.dataPoints[0].value === "0") {
							return;
						}
					}
					// Tooltip Element
					var tooltipEl = document.getElementById('chartjs-tooltip');

					// Create element on first render
					if (!tooltipEl) {
						tooltipEl = document.createElement('div');
						tooltipEl.id = 'chartjs-tooltip';
						tooltipEl.innerHTML = '<table></table>';
						document.body.appendChild(tooltipEl);
					}

					// Hide if no tooltip
					if (tooltipModel.opacity === 0) {
						tooltipEl.style.opacity = 0;
						return;
					}

					// Set caret Position
					tooltipEl.classList.remove('above', 'below', 'no-transform');
					if (tooltipModel.yAlign) {
						tooltipEl.classList.add(tooltipModel.yAlign);
					} else {
						tooltipEl.classList.add('no-transform');
					}

					function getBody(bodyItem, index) {
						if (result.questionType === "SingleChoice" && ["horizontalBar", "bar", "line", "radar"].includes(chart.type)) {
							const olabels = chartData.originalLabels;
							const dataIndex = tooltipModel.dataPoints[index].index;
							return [olabels[dataIndex]+": "+tooltipModel.dataPoints[index].value];
						}
						if (result.questionType === "Matrix" && chart.type === "radar" && tooltipModel.dataPoints[0].value === 0) {
							const olabels = chartData.originalLabels;
							const oquestions = chartData.datasets.map(ds => ds.label);
							const colI = tooltipModel.dataPoints[index].index;
							const rowI = tooltipModel.dataPoints[index].datasetIndex;
							return [oquestions[rowI]+": "+olabels[colI]];
						}
						return bodyItem.lines;
					}

					function getTitles(titleLines_, tooltipModel_) {
						if (result.questionType === "Matrix") {
							if (chart.type === "radar" && tooltipModel_.dataPoints[0].value === 0) {
								return [result.label];
							}
							if (["horizontalBar", "bar", "line", "radar"].includes(chart.type)) {
								return [result.label].concat(titleLines_);
							}
						}
						if (result.questionType === "SingleChoice" || result.questionType === "MultipleChoice"  || result.questionType === "Number"  || result.questionType === "Formula" || result.questionType === "Ranking") {
							return [result.label];
						}
						return titleLines_;
					}

					// Set Text
					if (tooltipModel.body) {
						var titleLines = tooltipModel.title || [];
						var bodyLines = tooltipModel.body.map(getBody);

						var innerHtml = '<thead>';

						getTitles(titleLines, tooltipModel).forEach(function(title) {
							innerHtml += '<tr><th>' + title + '</th></tr>';
						});
						innerHtml += '</thead><tbody>';

						bodyLines.forEach(function(body, i) {
							var colors = tooltipModel.labelColors[i];
							var style = 'background:' + colors.backgroundColor;
							style += '; border-color:' + colors.borderColor;
							style += '; border-width: 2px';
							var span = '<div class="chartjs-line" style="' + style + '"></div>';
							innerHtml += '<tr><td>' + span + body.join(" ") + '</td></tr>';
						});
						innerHtml += '</tbody>';

						var tableRoot = tooltipEl.querySelector('table');
						tableRoot.innerHTML = innerHtml;
					}

					// `this` will be the overall tooltip
					var position = this._chart.canvas.getBoundingClientRect();

					// Display, position, and set styles for font
					tooltipEl.style.opacity = 1;
					tooltipEl.style.position = 'absolute';
					tooltipEl.style.left = position.left + window.pageXOffset + tooltipModel.caretX + 'px';
					tooltipEl.style.top = position.top + window.pageYOffset + tooltipModel.caretY + 'px';
					tooltipEl.style.fontFamily = tooltipModel._bodyFontFamily;
					tooltipEl.style.fontSize = tooltipModel.bodyFontSize + 'px';
					tooltipEl.style.fontStyle = tooltipModel._bodyFontStyle;
					tooltipEl.style.padding = tooltipModel.yPadding + 'px ' + tooltipModel.xPadding + 'px';
					tooltipEl.style.pointerEvents = 'none';
				},

				callbacks: {
					title: chart.data.datasets.length === 1
							? function (item, data) {
								var label = chart.type === "radar" ? data.originalLabels[item[0].index] : item[0].originalLabel;
								return wrapLabel(label, 30);
							} : function (item, data) {
								var label = chart.type === "pie" ? data.datasets[item[0].datasetIndex].originalLabel : data.originalLabels[item[0].index];
								return wrapLabel(label, 30);
							},
					label: chart.data.datasets.length === 1
							? function (item, data) {
								var label =  data.originalLabels[item.index] + ": " + data.datasets[item.datasetIndex].data[item.index]

								return wrapLabel(label, 30);
							} : function (item, data) {
								var label = chart.type === "pie"
										? data.originalLabels[item.index] + ": " + data.datasets[item.datasetIndex].data[item.index]
										: data.datasets[item.datasetIndex].originalLabel + ": " + item.value;
								return wrapLabel(label, 30);
							}
				}
			}

			if (flags.forResults) {
				if (chartCallback instanceof Function) {
					chartCallback(div, chart, chartType, showLegendBox, canvasWidth);
				}
			} else {
				if (chartCallback instanceof Function) {
					chartCallback(div, chart);
				}
				addStatisticsToAnswerText(div, result);
			}
		}
	});
}
