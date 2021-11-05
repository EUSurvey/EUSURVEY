
	function selectProfile(profileUid) {
		$("#select-profile-filter").val(profileUid);
		ecfResultPage = 1;
		displayCurrentPageResults();
		$("#ecfSelectContributionsTable > tbody > .selectedrow").removeClass("selectedrow");
		$("#ecfSelectContributionsTable > tbody > tr[data-profile-uid='" + profileUid + "']").addClass("selectedrow");
	}

	function fetchECFResults(pageNumber, pageSize) {
		if (contextpath.charAt(contextpath.length - 1) === '/') {
			contextpath = contextpath.slice(0,-1);
		}
		
		let orderBy = $( "#select-orderBy" ).val();
		let orderByParam = "";
		if (orderBy && orderBy != "") {
			orderByParam = "&orderBy=" + orderBy;
		}
		
		let profileComparisonUUID = $( "#select-job-profiles" ).val();
		let profileComparisonUUIDParam = "";
		if (profileComparisonUUID && profileComparisonUUID != "") {
			profileComparisonUUIDParam = "&profileComparison=" + profileComparisonUUID;
		}
		
		let profileFilterUUID = $( "#select-profile-filter" ).val();
		let profileFilterUUIDParam = "";
		if (profileFilterUUID && profileFilterUUID != "") {
			profileFilterUUIDParam = "&profileFilter=" + profileFilterUUID;
		}
		
			$.ajax({
				type:'GET',
				url: contextpath + "/" + surveyShortname + "/management/ecfGlobalResultsJSON"
				+"?pageNumber=" + pageNumber
				+"&pageSize=" + pageSize
				+ profileComparisonUUIDParam
				+ profileFilterUUIDParam
				+ orderByParam,
				cache: false,
				success: function(ecfGlobalResult) {
					if (ecfGlobalResult == null) {
						setTimeout(function(){ fetchECFResults(pageNumber, pageSize); }, 10000);
						return;
					} else {
						displayECFTable(ecfGlobalResult);
						displayECFChart(ecfGlobalResult);
						displayNumberOfResults(ecfGlobalResult)
						return ecfGlobalResult;
					}
				}
			});
	}
	
	function displayNumberOfResults(ecfGlobalResult) {
		if (ecfGlobalResult && ecfGlobalResult.numberOfResults) {
			$("#individualNumberOfAnswers").text(ecfGlobalResult.numberOfResults);
		}
	}
	
	function displayECFTable(ecfGlobalResult) {
		$("#ecfResultTable > tbody").empty();
		let totalResults = ecfGlobalResult.totalResults;
		ecfMaxResultPage = ecfGlobalResult.numberOfPages; 
		
		displayPreviousPageLinks();
		displayCurrentPageLink();
		displayNextPageLinks();
		
		let totalTarget = (totalResults.totalTargetScore != null && totalResults.totalTargetScore != undefined) ? totalResults.totalTargetScore : "";
		
		$("#ecfResultTable > tbody:last-child")
		.append('<tr class="bodyrow">'
				+ '<th>' + totalResults.competencyName + '</th>'
				+ '<th>' + totalTarget + '</th>'
				+ displayScoresColumn(totalResults.totalScores, totalResults.totalGaps)
				+ '</tr>');
		
		
		ecfGlobalResult.individualResults.forEach(individualResult => {
			let targetScore = "";
			if (individualResult.targetScore) {
				targetScore = individualResult.targetScore;
			}
			
			$("#ecfResultTable > tbody:last-child")
			.append('<tr class="bodyrow">'
					+ '<th>' + individualResult.name + '</th>'
					+ '<th>' + targetScore + '</th>'
					+ displayScoresColumn(individualResult.scores, individualResult.scoreGaps)
					+ '</tr>');
			displayIndividuals(individualResult.participantNames, individualResult.participantContributionUIDs);
		});
		
		
	}
	
	function displayScoresColumn(competencyScores, competencyScoreGaps) {
		let restOfARow = '';
		let i = 0;
		competencyScores.forEach(competencyScore => {
			restOfARow = restOfARow.concat(oneScoreWithGapTH(competencyScore,competencyScoreGaps[i]));
			i++;
		});
		return restOfARow;
	}
	
	function oneScoreWithGapTH(score, gap) {
		let oneTh = '';
		let displayGap = true;
		let displayScore = true;
		
		let scoreClass = displayScore ? 'score' : 'score hidden';
		let gapClass = displayGap ? 'gap ' : 'gap hidden ';
		let gapColor = (gap>=0) ? 'greenScore' : 'redScore';
		let gapDisplayed = (gap>0) ? '+' + gap : gap; 
		
		let scoreDiv = '<div class="'
			+ scoreClass
			+ '">'
			+ score 
			+ '</div>';
		
		let gapDiv = '';
		
		if (gap != null && gap != undefined) {
			gapDiv = '<div class="' 
				+ gapClass
				+ gapColor 
				+ '">&nbsp; (' 
				+ gapDisplayed
				+ ')</div>';
		}
		
		oneTh = oneTh.concat('<th><div>'
				+ scoreDiv
				+ gapDiv
				+ '</div></th>');
		
		return oneTh;
	}
	
	function nextResultPage() {
		if (ecfResultPage < ecfMaxResultPage) {
			ecfResultPage = ecfResultPage + 1;
			displayCurrentPageResults();
		}
	}
	
	function previousResultPage() {
		if (ecfResultPage > 1) {
			ecfResultPage = ecfResultPage - 1;
			displayCurrentPageResults();
		}
	}
	
	function selectResultPage(pageNumber) {
		ecfResultPage = pageNumber;
		displayCurrentPageResults();
	}
	
	function displayCurrentPageLink() {
		$("a.currentResultPage").text(ecfResultPage);
	}
	
	function displayPreviousPageLinks() {
		$(".previousPagesLinks").empty();
		if (ecfResultPage > 1) {
			$(".previousPagesLinks").append('<a onclick="previousResultPage()" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a>');
		}
		for (let i = 1; i < ecfResultPage; i++){
			$(".previousPagesLinks").append('<a onclick="selectResultPage(' + i +')">' + i +'</a>');
		}
	}
	
	function displayNextPageLinks() {
		$(".nextPagesLinks").empty();
		for (let i = (ecfResultPage + 1); i <= ecfMaxResultPage; i++){
			$(".nextPagesLinks").append('<a onclick="selectResultPage(' + i +')">' + i +'</a>');
		}
		
		if (ecfResultPage < ecfMaxResultPage) {
			$(".nextPagesLinks").append('<a onclick="nextResultPage()" aria-label="Next"><span aria-hidden="true">&raquo;</span></a>');
		}
	}
	
	function displayCurrentPageResults() {
		fetchECFResults(ecfResultPage, 10);
	}
	
	function displayIndividuals(participantNames, participantsContributionUids) {
		$("#ecfResultTable > thead > .headerrow > th.individual").remove();
		let i = (ecfResultPage - 1) * 10;
		participantNames.forEach(function(participantName, i) {
			let participantNameSub = participantName.substring(0,5);
			$("#ecfResultTable > thead > .headerrow").append(
			'<th class="individual">' 
			+ '<a style="color: inherit" href="' 
			+ contextpath + '/contribution/' + participantsContributionUids[i] + '/preview'
			+ '" target="_blank" data-toggle="tooltip" data-placement="top" title="' + participantName + '">'
			+ participantNameSub + '...'
			+ '</a>'
			+ '</th>');
			i++;
		});
		$('[data-toggle="tooltip"]').tooltip(); 
	}
// ======================= PAGE 2 ============================= // 
	function selectProfile2(profileUid) {
		$("#select-job-profiles2").val(profileUid);
		fetchECFProfileAssessmentResults();
		$("#ecfSelectContributionsTable2 > tbody > .selectedrow").removeClass("selectedrow");
		$("#ecfSelectContributionsTable2 > tbody > tr[data-profile-uid='" + profileUid + "']").addClass("selectedrow");
	}
	
	function fetchECFProfileAssessmentResults() {
		if (contextpath.charAt(contextpath.length - 1) === '/') {
			contextpath = contextpath.slice(0,-1);
		}
		
		let profileComparisonUUID = $( "#select-job-profiles2" ).val();
		let profileComparisonUUIDParam = "";
		if (profileComparisonUUID && profileComparisonUUID != "") {
			profileComparisonUUIDParam = "?profile=" + profileComparisonUUID;
		}
			$.ajax({
				type:'GET',
				url: contextpath + "/" + surveyShortname + "/management/ecfProfileAssessmentResultsJSON"
				+profileComparisonUUIDParam,
				cache: false,
				success: function(profileAssessmentResult) {
					if (profileAssessmentResult == null) {
						setTimeout(function(){ fetchECFProfileAssessmentResults(); }, 10000);
						return;
					} else {
						displayECFTable2(profileAssessmentResult);
						
						if (profileAssessmentResult.name == null) {
							$('#ecfAvgScoreChart').hide();
							$('#ecfAvgScoreChart2').hide();
							$('#ecfMaxScoreChart').hide();
							$('#ecfMaxScoreChart2').hide();
						} else {						
							displayECFAvgScoreChartByProfileResults(profileAssessmentResult, profileComparisonUUID);
							displayECFMaxScoreChartByProfileResults(profileAssessmentResult, profileComparisonUUID);
						}
						return profileAssessmentResult;
					}
				}
			});
	}
	
	function displayECFTable2(profileAssessmentResult) {
		$("#ecfResultTable2 > tbody").empty();
		profileAssessmentResult.competencyResults.forEach(competencyResult => {
			let targetScore = "";
			let averageScore = "";
			
			if (competencyResult.competencyTargetScore || competencyResult.competencyTargetScore === 0) {
				targetScore = competencyResult.competencyTargetScore;
			}
			
			if (competencyResult.competencyAverageScore || competencyResult.competencyAverageScore === 0) {
				averageScore = competencyResult.competencyAverageScore;
			}
			
			let competencyMaxScoreDiv = '';
				
			if (competencyResult.competencyMaxScore || competencyResult.competencyMaxScore === 0) {
				competencyMaxScoreDiv = '<div class="'
					+ 'score'
					+ '">'
					+ competencyResult.competencyMaxScore 
					+ '</div>';
			}
			
			let gapDiv = '';
			
			let gap = competencyResult.competencyScoreGap;
			let gapColor = (gap>=0) ? 'greenScore' : 'redScore';
			let gapDisplayed = (gap>0) ? '+' + gap : gap; 
			
			if (gap != null && gap != undefined) {
				gapDiv = '<div class="' 
					+ 'gap '
					+ gapColor 
					+ '">&nbsp; (' 
					+ gapDisplayed
					+ ')</div>';
			}
			
			$("#ecfResultTable2 > tbody:last-child")
			.append('<tr class="bodyrow">'
					+ '<th>' + competencyResult.competencyName + '</th>'
					+ '<th>' + targetScore + '</th>'
					+ '<th>' + averageScore + '</th>'
					+ '<th>' + '<div>' + competencyMaxScoreDiv + gapDiv + '</div>' + '</th>'
					+ '</tr>');
		});
	}
	// ======================= PAGE 3 ============================= // 
	function fetchECFOrganizationalResults() {
		if (contextpath.charAt(contextpath.length - 1) === '/') {
			contextpath = contextpath.slice(0,-1);
		}
		
		$.ajax({
			type:'GET',
			url: contextpath + "/" + surveyShortname + "/management/ecfOrganizationalResultsJSON",
			cache: false,
			success: function(organizationalResult) {
				if (organizationalResult == null) {
					setTimeout(function(){ fetchECFOrganizationalResults(); }, 10000);
					return;
				} else {
					displayECFMaxChartByOrganizationalResult(organizationalResult);
					displayECFAverageChartByOrganizationalResult(organizationalResult);
					return organizationalResult;
				}
			}
		});
	}	
	
	function displayECFAverageChartByOrganizationalResult(organizationalResult) {
		if (organizationalResult) {
			organizationalResult.competenciesTypes.forEach(competenciesType => {
				averageScores = [];
				competencies = [];
				averageTargetScores = [];
				organizationalResult.competencyResults.forEach(competencyResult => {
					if (competencyResult.competencyTypeUid === competenciesType.typeUUID) {
						averageScores.push(competencyResult.competencyAverageScore);
						competencies.push(competencyResult.competencyName);
						averageTargetScores.push(competencyResult.competencyAverageTarget);
					}
				});
				displayECFChart("Average scores vs Average target scores for " + competenciesType.typeName, "#ecfAverageChart_" + competenciesType.typeUUID, averageScores, averageTargetScores, competencies, "Average scores", "Average target scores");
			});
		}
	}
	
	
	function displayECFMaxChartByOrganizationalResult(organizationalResult) {
		if (organizationalResult) {
			organizationalResult.competenciesTypes.forEach(competenciesType => {
				maxScores = [];
				competencies = [];
				maxTargetScores = [];
				organizationalResult.competencyResults.forEach(competencyResult => {
					if (competencyResult.competencyTypeUid === competenciesType.typeUUID){
						maxScores.push(competencyResult.competencyMaxScore);
						competencies.push(competencyResult.competencyName);
						maxTargetScores.push(competencyResult.competencyMaxTarget);
					}
				});
				displayECFChart("Max scores vs Max target scores for " + competenciesType.typeName, "#ecfMaxChart_" + competenciesType.typeUUID, maxScores, maxTargetScores, competencies, "Max scores", "Max target scores");
			});
		}
	}
	
	function displayECFAvgScoreChartByProfileResults(profileAssessmentResult, profileComparisonUUID, profileName) {
		if (profileAssessmentResult) {
			avgScores = [];
			competencies = [];
			targetScores = [];
			
			avgScores2 = [];
			competencies2 = [];
			targetScores2 = [];
			
			profileAssessmentResult.competencyResults.forEach(competencyResult => {
				//first 19 entries are for procurement specific
				// rest is for professional competencies
				
				if (avgScores.length < 19)
				{
					avgScores.push(competencyResult.competencyAverageScore);
					competencies.push(competencyResult.competencyName);
					targetScores.push(competencyResult.competencyTargetScore);
				} else {
					avgScores2.push(competencyResult.competencyAverageScore);
					competencies2.push(competencyResult.competencyName);
					targetScores2.push(competencyResult.competencyTargetScore);
				}
			});
			displayECFChart("Procurement specific competencies - Average score", "#ecfAvgScoreChart", avgScores, targetScores, competencies, "Avg scores", "Target scores");
			$('#ecfAvgScoreChart').show();
			displayECFChart("Professional competencies - Average score", "#ecfAvgScoreChart2", avgScores2, targetScores2, competencies2, "Avg scores", "Target scores");
			$('#ecfAvgScoreChart2').show();
		}
	}
	
	function displayECFMaxScoreChartByProfileResults(profileAssessmentResult, profileComparisonUUID, profileName) {
		if (profileAssessmentResult) {
			maxScores = [];
			competencies = [];
			targetScores = [];
			
			maxScores2 = [];
			competencies2 = [];
			targetScores2 = [];
			
			profileAssessmentResult.competencyResults.forEach(competencyResult => {
				//first 19 entries are for procurement specific
				// rest is for professional competencies
				
				if (maxScores.length < 19)
				{
					maxScores.push(competencyResult.competencyMaxScore);
					competencies.push(competencyResult.competencyName);
					targetScores.push(competencyResult.competencyTargetScore);
				} else {
					maxScores2.push(competencyResult.competencyMaxScore);
					competencies2.push(competencyResult.competencyName);
					targetScores2.push(competencyResult.competencyTargetScore);
				}
			});
			displayECFChart("Procurement specific competencies - Maximum score", "#ecfMaxScoreChart", maxScores, targetScores, competencies, "Max scores", "Target scores");
			$('#ecfMaxScoreChart').show();
			displayECFChart("Professional competencies - Maximum score", "#ecfMaxScoreChart2", maxScores2, targetScores2, competencies2, "Max scores", "Target scores");
			$('#ecfMaxScoreChart2').show();
		}
	}
	
	function displayECFChart(chartTitle, canvasIdCssSelector, firstLineArray, secondLineArray, headerNamesArray, firstLineLegendName, secondLineLegendName) {
		if (chartTitle && canvasIdCssSelector && firstLineArray && secondLineArray && headerNamesArray && firstLineLegendName && secondLineLegendName) {
			if (firstLineArray.length === secondLineArray.length && secondLineArray.length === headerNamesArray.length) {
				var ctx = $(canvasIdCssSelector);
				var options = {
					scale: {
						angleLines: {
					         display: false
					       },
					        ticks: {
					            suggestedMin: 0,
					            suggestedMax: 4
					        }
						},
					title: {
						display: true,
				        text: chartTitle
					},
					maintainAspectRatio: true,
					spanGaps: false,
					elements: {
						line: {
							tension: 0.000001
						}
					},
					plugins: {
						filler: {
							propagate: false
						},
						'samples-filler-analyser': {
							target: 'chart-analyser'
						}
					}
				};
			
				var myRadarChart = new Chart(ctx, {
					type: 'radar',
					data: {
						labels: headerNamesArray,
						datasets: [{
							label: firstLineLegendName,
							data: firstLineArray,
							backgroundColor: 'rgba(255, 99, 132, 0.2)',
							borderColor: 'rgba(255, 99, 132, 1)',
							borderWidth: 1
						},
						{
							label: secondLineLegendName,
							data: secondLineArray,
							backgroundColor: 'rgba(97, 197, 255, 0.2)',
							borderColor: 'rgba(97, 197, 255, 1)',
							borderWidth: 1
						}
						]
					},
					options: options
				});
			}
		}
	}
	
