<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:if test="${forpdf == null}">
	<script type="text/javascript" src="${contextpath}/resources/js/graph_data_loader.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript"> 
		var statisticsrequestid = null;
		function loadStatisticsAsync(publication)
		{

			$(".ajaxloaderimage").show();

				var s = "active=${active eq true}&allanswers=${allanswers eq true}";	
				
				if (publication)
				{
					s += "&publicationmode=true";	
				}
				
				if (statisticsrequestid != null)
				{
					s += "&statisticsrequestid=" + statisticsrequestid;	
				}
				
				$.ajax({
					type:'GET',
					  url: "${contextpath}/${form.survey.shortname}/management/statisticsJSON",
					  dataType: 'json',
					  data: s,
					  cache: false,
					  success: function( statistics ) {
						  if (statistics == null)
						  {
							//this means the asynchronous computation has been started
							setTimeout(function(){ loadStatisticsAsync(publication); }, 10000);
							return;
						  }
						  
						  if (statistics.requestID != null)
						  {
							  statisticsrequestid = statistics.requestID;
							  setTimeout(function(){ loadStatisticsAsync(publication); }, 10000);
							  return;
						  }

						  //If there are no contributions, maxSectionScore is empty
						  //Sections visibility is dependent on maxSectionScore, so if there are no quiz questions in the table
						  // -> set maxSectionScore to 0
						  if (JSON.stringify(statistics.maxSectionScore) === "{}"){
							  $(".sectiontitle").each(function () {
								  let section = $(this)
								  let questionsTable = section.nextAll("table.table:first")
								  if (questionsTable.find("tr").length <= 1){
									  statistics.maxSectionScore[section.attr("data-id")] = 0
								  }
							  })
						  }
						  
						  $(".deactivatedstatexports").hide();
						  $(".activatedstatexports").show();	
						  
						  $(".statRequestedRecords").each(function(){
							 var id = $(this).attr("data-id");
							 if (statistics.requestedRecords[id] != null)
							 {
							 	$(this).html(statistics.requestedRecords[id]);
							 	$(this).closest("tr").attr("data-value",statistics.requestedRecords[id]);
							 }
						  });
						  
						  $(".statRequestedRecordsPercent").each(function(){
							 var id = $(this).attr("data-id");
							 if (statistics.requestedRecords[id] != null)
							 $(this).html(statistics.requestedRecordsPercent[id].toFixed(2) + " %");
						  });
						  
						  $(".chartRequestedRecordsPercent").each(function(){
								 var id = $(this).attr("data-id");
								 if (statistics.requestedRecords[id] != null)
								 {
									 $(this).css("width", statistics.requestedRecordsPercent[id].toFixed(2) + "%");
									 $(this).closest("tr").attr("data-value",statistics.requestedRecords[id]);
								 }								
							  });
						  
						  $(".statRequestedRecordsScore").each(function(){
								 var id = $(this).attr("data-id");
								 if (statistics.requestedRecordsScore[id] != null)
								 {
								 	$(this).html(statistics.requestedRecordsScore[id]);
								 	$(this).closest("tr").attr("data-value",statistics.requestedRecordsScore[id]);
								 }
							  });
						  
						  $(".statRequestedRecordsRankingScore").each(function(){
								 var id = $(this).attr("data-id");
								 var parentId = $(this).attr("data-parent-id");
							
								 if (statistics.requestedRecordsRankingScore[id] != null)
								 {
									var value = parseInt(statistics.requestedRecordsRankingScore[id]);
									var total = parseInt(statistics.requestedRecordsRankingScore[parentId]);
									var percentage = statistics.requestedRecordsRankingPercentScore[id];
									 
									if (id.indexOf("-") < 0) {
										// the score column
										$(this).html("<b><span>" + percentage + "</span></b><br />" + total);
										$(this).closest("tr").attr("data-value", percentage);
									} else {
									 	// other column
									 	$(this).html("<b>" + percentage + "%</b><br /><span>" + value + "</span>");
									}
								 }
							  });
						  
						  $(".statRequestedRecordsPercentScore").each(function(){
								 var id = $(this).attr("data-id");
								 if (statistics.requestedRecordsScore[id] != null)
								 $(this).html(statistics.requestedRecordsPercentScore[id].toFixed(2) + " %");
							  });
						  
						  $(".chartRequestedRecordsPercentScore").each(function(){
								 var id = $(this).attr("data-id");
								 if (statistics.requestedRecordsScore[id] != null)
								 {
									 $(this).css("width", statistics.requestedRecordsPercentScore[id].toFixed(2) + "%");
									 $(this).closest("tr").attr("data-value",statistics.requestedRecordsScore[id]);
								 }								
							  });
						 
						  $(".statMeanScore").html(roundToTwo(statistics.meanScore) + '&#x20;<spring:message code="label.of" />&#x20;' + statistics.maxScore + '&#x20;<spring:message code="label.points" /> (' + roundToTwo(statistics.maxScore == 0 ? 0 : statistics.meanScore / statistics.maxScore * 100) + '%)');
						  $(".statBestScore").html(statistics.bestScore + '&#x20;<spring:message code="label.of" />&#x20;' + statistics.maxScore + '&#x20;<spring:message code="label.points" /> (' + roundToTwo(statistics.maxScore == 0 ? 0 : statistics.bestScore / statistics.maxScore * 100) + '%)');
						  $(".statTotal").html(statistics.total);

						  $(".statMeanSectionScore").each(function(){
							  var id = $(this).attr("data-id");							  
							  $(this).html(roundToTwo(statistics.meanSectionScore[id]) + '&#x20;<spring:message code="label.of" />&#x20;' + statistics.maxSectionScore[id] + '&#x20;<spring:message code="label.points" /> (' + roundToTwo(statistics.maxSectionScore[id] == 0 ? 0 : statistics.meanSectionScore[id] / statistics.maxSectionScore[id] * 100) + '%)');
						  });
						  
						  $(".statBestSectionScore").each(function(){
							  var id = $(this).attr("data-id");							  
							  $(this).html(roundToTwo(statistics.bestSectionScore[id]) + '&#x20;<spring:message code="label.of" />&#x20;' + statistics.maxSectionScore[id] + '&#x20;<spring:message code="label.points" /> (' + roundToTwo(statistics.maxSectionScore[id] == 0 ? 0 : statistics.bestSectionScore[id] / statistics.maxSectionScore[id] * 100) + '%)');

							  if (statistics.maxSectionScore[id] == 0) {
								  $(this).closest(".sectionwithratingquestions").hide();
							  }
						  });

						  $(".sectiontitle").each(function () {
							  var id = $(this).attr("data-id");
							  if (statistics.maxSectionScore[id] == 0) {
								  $(this).hide();
								  $(this).next().hide();
								  $(this).next().next().hide();
							  }
						  });

					  },
					  error: function () {
						//this means the asynchronous computation has been started
						setTimeout(function () {
							loadStatisticsAsync(publication);
						}, 2000);
					  }
				});
		}

		function loadDelphiStatisticsAsync() {
			var s = "allanswers=${allanswers eq true}";

			$.ajax({
				type:'GET',
				  url: "${contextpath}/${form.survey.shortname}/management/statisticsDelphiJSON",
				  dataType: 'json',
				  data: s,
				  cache: false,
				  success: function( statistics ) {
					  $(".statDelphi").each(function(){
						 var id = $(this).attr("data-uid");
						 if (statistics[id] != null)
						 {
						 	$(this).html(statistics[id]);
						 }
					  });
				  },
				  error: function (data) {
					  showAjaxError(data.status)
				  }
			});
			
			$.ajax({
				type:'GET',
				  url: "${contextpath}/${form.survey.shortname}/management/statisticsDelphiMedianJSON",
				  dataType: 'json',
				  data: s,
				  cache: false,
				  success: function( statistics ) {
					  $(".statDelphiMedian").each(function(){
						 var id = $(this).attr("data-uid");
						 if (statistics[id] != null)
						 {
						 	$(this).html(statistics[id]);
						 } else {
							 $(this).empty();
						 }
					  });
				  },
				error: function (data) {
					showAjaxError(data.status)
				}
			});
		}
		
		function roundToTwo(num) {
			return +(Math.round(num + "e+2") + "e-2");
		}

		function delphiPopulateAllGraphs(resultsStatisticParentElement) {
			var chartwrapperlist = $(resultsStatisticParentElement).find(".chart-wrapper");
			chartwrapperlist.each(function (index) {

				if ($(this).parent().find(".chart-controls").find("select").length == 0) {
					$(this).parent().find(".chart-controls").append($("#chart-controls-template").html());
				}

				$(this).parent().find('[data-toggle="tooltip"]').tooltip();

				const chartwrapper = $(this);

				if ($(chartwrapper).data("initial-chart-type") == 'None') {
					addChart(this, null, "None", false);
				} else {
					loadGraphDataInnerForResults(chartwrapper, addChart, null, null, null, 300, null);
				}
			});
		}

		function changeChart(select) {
			const controls = $(select).closest(".chart-controls");
			
			const chartwrapper = $(select).closest(".statelement-wrapper").find(".chart-wrapper").first();
			const chartType = $(controls).find(".chart-type").first().val();
			const chartTypeSet = new Set(["Bar", "Column", "Line", "Pie", "Radar", "Scatter", "WordCloud"]);
			if (!chartTypeSet.has(chartType)) {
				$(controls).find(".chart-scheme-group").first().hide();
				$(controls).find(".chart-size-group").first().hide();
				$(controls).find(".chart-legend-group").first().hide();
				$(controls).find(".chart-score-group").hide();
				$(chartwrapper).hide();
				return;
			} else {
				$(controls).find(".chart-scheme-group").first().show();
				$(controls).find(".chart-size-group").first().show();
				$(chartwrapper).show();
			}
			const scheme = $(controls).find(".chart-scheme").first().val();
			const legend = $(controls).find(".chart-legend").first().is(":checked");
			const score = $(controls).find(".chart-score").first().is(":checked");

			const size = $(chartwrapper).closest(".elementwrapper, .statelement-wrapper").find(".chart-size").first().val();
			const canvasWidth = getChartCanvasHeightAndWidth(size).width;

			loadGraphDataInnerForResults(chartwrapper, addChart, chartType, scheme, legend, canvasWidth, score);
		}

		function getChartCanvasHeightAndWidth(size) {
			if (size === 'large') {
				return {
					height: 440,
					width: 600
				};
			}
			if (size === 'medium') {
				return {
					height: 330,
					width: 450
				};
			}
			return {
				height: 220,
				width: 300
			};
		}

		function chartLabelCallback(value, index, values) {
			return value.length > 15 ? value.substring(0, 10) + "..." : value;
		}

		/**
		 * Labels may be returned with some HTML code. This method returns the text content without any markup or an empty string.
		 * @param {string} value
		 */
		function normalizeLabel(value) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(value, "text/html");
			return doc.body.textContent || "";
		}

		/**
		 * Tries to nicely split a label into multiple lines for Chart.js
		 * @param value
		 * @param {number} lineLength - Maximum number of characters per line
		 * @returns {string | string[]}
		 */
		function wrapLabel(value, lineLength) {
			// try to coerce value into a string
			value = value == undefined ? "" : value.toString();

			// split by whitespace
			var words = value.trim().split(/\s+/g);

			if (words.length === 0) {
				// only whitespace
				return "";
			}

			var result = [];
			var currentLine = "";
			var i = 0;

			while (true) {
				var word = words[i];

				if (i >= words.length) {
					// out-of-bounds, add current line to result (if not empty) and stop
					if (currentLine) {
						result.push(currentLine);
					}

					break;
				}

				if (!currentLine) {
					if (word.length > lineLength) {
						// word is longer than allowed length => split word in between and create new line
						result.push(word.substr(0, lineLength));

						// remove first lineLength characters in word and try again
						words[i] = word.substr(lineLength);
						continue;
					}

					// set current line to current word and increase index
					currentLine = word;
					i++;
					continue;
				}

				if (currentLine.length + 1 + word.length <= lineLength) {
					// word fits in current line, separated by space character
					currentLine += " " + word;
					i++;
					continue;
				}

				// word does not fit into current line anymore => retry on current word with new line (index not increased)
				result.push(currentLine);
				currentLine = "";
			}

			if (result.length === 1) {
				// only one line => return as string
				return result[0];
			}

			// multiple lines => return as array
			return result;
		}
		
		function addChart(div, chart, chartType, showLegendBox) {
			const chartTypeSet = new Set(["Bar", "Column", "Line", "Pie", "Radar", "Scatter", "WordCloud"]);
			const isDrawChart = chartTypeSet.has(chartType);
			const elementWrapper = $(div).closest(".elementwrapper, .statelement-wrapper");
			const controls = $(elementWrapper).find(".chart-controls");

			$(elementWrapper).find(".delphi-chart").remove();

			const size = $(elementWrapper).find(".chart-size").first().val();
			const dimensions = getChartCanvasHeightAndWidth(size);

			const canvasElement = "<canvas class='delphi-chart' width='" + dimensions.width + "' height='"
				+ dimensions.height + "' style='background-color: #fff;'></canvas>";
			$(elementWrapper).find(".delphi-chart-div").append(canvasElement);

        	$(elementWrapper).find(".chart-wrapper").show();
        	$(elementWrapper).find(".chart-controls").show();
        	
        	$(elementWrapper).find(".chart-type").each(function(){
        		$(this).val(chartType);
        	});

			if (showLegendBox && isDrawChart) {
				$(elementWrapper).find(".chart-legend-group").show();
			} else {
				$(elementWrapper).find(".chart-legend-group").hide();
			}
			
			if (isDrawChart && $(elementWrapper).hasClass("ranking-chart") && chartType === "Bar") {
				$(elementWrapper).find(".chart-score-group").show();
			} else {
				$(elementWrapper).find(".chart-score-group").hide();
			}

			if (isDrawChart && $(elementWrapper).hasClass("choice-chart")) {
				$(elementWrapper).find(".chart-hide-unanswered-group").show();
			} else {
				$(elementWrapper).find(".chart-hide-unanswered-group").hide();
			}

			if (!!chart) {
				new Chart($(elementWrapper).find(".delphi-chart")[0].getContext('2d'), chart);
			}

			if (!isDrawChart) {
				$(controls).find(".chart-scheme-group").first().hide();
				$(controls).find(".chart-size-group").first().hide();
				$(controls).find(".chart-legend-group").first().hide();
				$(controls).find(".chart-score-group").first().hide();
				const chartDataType = $(elementWrapper).find(".chart-wrapper").data("chart-data-type");
				if (chartDataType == "Textual") {
					$(elementWrapper).find("option[data-type='numerical']").hide();
					$(elementWrapper).find(".chart-scheme").first().val("Style B");
				} else {
					$(elementWrapper).find("option[data-type='textual']").hide();
				}				
				$(elementWrapper).find(".chart-wrapper").hide();
			} else {
				$(controls).find(".chart-scheme-group").first().show();
				$(controls).find(".chart-size-group").first().show();
				$(elementWrapper).find(".chart-wrapper").show();
			}
			const hasNoData = $(elementWrapper).find(".chart-wrapper").data("has-no-data");
			if (hasNoData === "true") {
				$(elementWrapper).find(".chart-controls").hide();
			}
        }
		
		function sortRankingStatistics(button, descending) {
 			var index = $(button).closest("th").index();
 			var table = $(button).closest("table")[0];

			var rows, switching, i, x, y, shouldSwitch;
			switching = true;
			while (switching) {
			    switching = false;
			    rows = table.rows;
			    for (i = 1; i < (rows.length - 2); i++) {
			      shouldSwitch = false;
			      x = rows[i].getElementsByTagName("TD")[index].getElementsByTagName("SPAN")[0];
			      y = rows[i + 1].getElementsByTagName("TD")[index].getElementsByTagName("SPAN")[0];
			      if ((descending && parseFloat(x.innerText) < parseFloat(y.innerText)) || (!descending && parseFloat(x.innerText) > parseFloat(y.innerText))) {
			        shouldSwitch = true;
			        break;
			      }
			    }
			    if (shouldSwitch) {
			      rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
			      switching = true;
			    }
			}
		}

	</script>
</c:if>
