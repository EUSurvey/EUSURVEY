<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:if test="${forpdf == null}">
	<script type="text/javascript"> 
		var statisticsrequestid = null;
		function loadStatisticsAsync(publication)
		{
		
			$(".ajaxloaderimage").show();
			$(".loadstatisticsbutton").hide();
			
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
							  
							 if (statistics.maxSectionScore[id] == 0)
							 {
								$(this).closest(".sectionwithratingquestions").hide();
							 }
						  });	
						  
						  $(".sectiontitle").each(function(){
							  var id = $(this).attr("data-id");							  
							 if (statistics.maxSectionScore[id] == 0)
							 {
								$(this).hide();
								$(this).next().hide();
								$(this).next().next().hide();
							 }
						  });	
						  
					  },
					  error: function() {
						//this means the asynchronous computation has been started
						setTimeout(function(){ loadStatisticsAsync(publication); }, 2000);
					  }
				});
			}
		
		function roundToTwo(num) {    
		    return +(Math.round(num + "e+2")  + "e-2");
		}

        function delphiPopulateAllGraphs(resultsStatisticParentElement) {
            var chartwrapperlist = $(resultsStatisticParentElement).find(".chart-wrapper");
            chartwrapperlist.each(function (index) {
                var chartwrapper = $(this);
                var surveyId = chartwrapper.data("survey-id");
                var questionuid = chartwrapper.data("question-uid");
                var languagecode = chartwrapper.data("language-code");
                var answersetuniquecode = ""; // not needed for privileged users like form managers
                loadGraphDataInner(chartwrapper, surveyId, questionuid, languagecode, answersetuniquecode, addChart, true);
            });
        }

        function loadGraphDataInner(div, surveyid, questionuid, languagecode, uniquecode, chartCallback, removeIfEmpty) {
        	var data = "surveyid=" + surveyid + "&questionuid=" + questionuid + "&languagecode=" + languagecode + "&uniquecode=" + uniquecode + "&resultsview=true";

        	$.ajax({
        		type: "GET",
        		url: contextpath + "/runner/delphiGraph",
        		data: data,
        		beforeSend: function (xhr) {
        			xhr.setRequestHeader(csrfheader, csrftoken);
        		},
        		error: function (data) {
        			showError(data.responseText);
        		},
        		success: function (result, textStatus) {
        			if (textStatus === "nocontent") {
        				if (removeIfEmpty) {
        					var elementWrapper = $(div).closest(".elementwrapper, .statelement-wrapper");
        					$(elementWrapper).find(".delphi-chart").remove();
        					$(elementWrapper).find(".chart-wrapper").hide();
        				}

        				return;
        			}

        			var chartData = {};
        			var chartOptions = {
        				scaleShowValues: true,
        				responsive: false,
        				scales: {
        					yAxes: [{ticks: {beginAtZero: true}}],
        					xAxes: [
        						{
        							ticks: {
        								beginAtZero: true,
        								autoSkip: false,
        								callback: function(value, index, values) {
        									if (value.length > 15)
        									{
        										return value.substring(0,10) + "...";
        									}
        			                        return value;
        			                    }
        							}
        						}
        					]
        				},
        				legend: {display: false}
        			};

        			switch (result.questionType) {
        				case "MultipleChoice":
        				case "SingleChoice":
        					var graphData = result.data;

        					chartData = {
        						datasets: [{
        							label: '',
        							data: graphData.map(function (g) {
        								return g.value
        							})
        						}],
        						labels: graphData.map(function (g) {
        							return g.label
        						})
        					};
        					break;

        				case "Matrix":
        				case "Rating":
        					var questions = result.questions;
        					var datasets = [];
        					var labels = undefined;

        					for (var i = 0; i < questions.length; i++) {
        						var question = questions[i];

        						datasets.push({
        							data: question.data.map(function (d) {
        								return d.value;
        							}),
        							label: question.label
        						});

        						if (!labels) {
        							labels = question.data.map(function (d) {
        								return d.label;
        							});
        						}
        					}

        					chartData = {
        						datasets,
        						labels
        					}

        					chartOptions.legend.display = true;
        					break;

        				default:
        					return;
        			}

        			var chart = {
        				data: chartData,
        				options: chartOptions
        			}

        			switch (result.chartType) {
        				case "Bar":
        					chart.type = "horizontalBar";
        					break;
        				case "Column":
        					chart.type = "bar";
        					break;
        				case "Line":
        					chart.type = "line";
        					break;
        				case "Pie":
        					chart.type = "pie";
        					chart.options.legend.display = true;
        					delete chart.options.scales;
        					break;
        				case "Radar":
        					chart.type = "radar";
        					delete chart.options.scales;
        					break;
        				case "Scatter":
        					chart.type = "line";
        					chart.options.showLines = false;
        					break;
        				default:
        					chart.type = "horizontalBar";
        					break;
        			}

        			if (chartCallback instanceof Function) {
        				chartCallback(div, chart);
        			}
        		}
        	 });
        }

        function addChart(div, chart)
        {
        	var elementWrapper = $(div).closest(".elementwrapper, .statelement-wrapper");

        	$(elementWrapper).find(".delphi-chart").remove();
        	$(elementWrapper).find(".delphi-chart-div").append("<canvas class='delphi-chart' width='300' height='220'></canvas>");

        	$(elementWrapper).find(".chart-wrapper").show();

        	var graph = new Chart($(elementWrapper).find(".delphi-chart")[0].getContext('2d'), chart);
        }

	</script>
</c:if>