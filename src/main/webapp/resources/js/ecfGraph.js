
/**
 * !!!!!!!!!!!!!!!!!
 * Needs as VAR
 * contextpath
 * surveyShortname
 * uniqueCode
 * !!!!!!!!!!!!!!!!!
 */
function fetchECFResult() {
	if (contextpath.charAt(contextpath.length - 1) === '/') {
		contextpath = contextpath.slice(0,-1);
	}
	
	let profileUUID = $( "#select-job-profiles" ).val();
	let profileUUIDParameter = profileUUID != null ? "&profileUUID=" + profileUUID : "";
	
	if (firstTry) {
		profileUUIDParameter = "";
		firstTry = false;
	}

	$.ajax({
		type:'GET',
		url: contextpath + "/ecfResultJSON?answerSetId=" + uniqueCode + profileUUIDParameter,
		cache: false,
		success: function(ecfResult) {
			if (ecfResult == null) {
				setTimeout(function(){ fetchECFResult(); }, 10000);
				return;
			} else {
				changeSelectedValue(ecfResult);
				displayECFTable(ecfResult);
				displayECFChart(ecfResult);
				return ecfResult;
			}
		}
	});
}

function changeSelectedValue(result) {
	if (result && result.profileUUID) {
		$('#select-job-profiles option[value="'+result.profileUUID+'"]').prop('selected', true);
	}
}

function displayECFTable(result) {
	$("#ecfResultTable > tbody").empty();
	result.competencies.forEach(competency => {
	let gapColor = (competency.scoreGap>=0) ? 'greenScore' : 'redScore';
	let displayedGap = (competency.scoreGap>0) ? "+" + competency.scoreGap : competency.scoreGap;
	$("#ecfResultTable > tbody:last-child").append('<tr class="bodyrow"><th>' + competency.name + '</th>'
			+ '<th>' + competency.targetScore + '</th>'
			+ '<th>' + '<div class="score">' + competency.score + '</div>'
			+ '<div class="gap ' + gapColor + '">&nbsp;('
			+ displayedGap + ')</div>' + '</th>'
			+ '</tr>')
			});
}

function displayECFChart(result) {
	if (result) {
		result.competenciesTypes.forEach(
			competencyTypeNameAndUUID => {
				displayOneTypeChart(result, competencyTypeNameAndUUID);
			});
	}
}

function displayOneTypeChart(result, typeNameAndUUID) {
	let profileName = result.name;		
	let scores = [];
	let competencies = [];
	let targetScores = [];

	result.competencies.forEach(competency => {
		if (competency.typeUUID === typeNameAndUUID.typeUUID) {
			scores.push(competency.score);
			competencies.push(competency.name);
			targetScores.push(competency.targetScore);
		}
	});

	let chartTitle = 'Comparison for ' + typeNameAndUUID.typeName;

	$('.ecfRespondentChart_' + typeNameAndUUID.typeUUID).each(function(index, element){
		
		$(this).empty();
		var canvas = document.createElement("canvas");
		$(this).append(canvas);
		
		var ctx = canvas.getContext("2d");
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
				text: chartTitle,
				fontSize: 14
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
				labels: competencies,
				datasets: [
				{
					label: 'Target score ',
					data: targetScores,
					backgroundColor: 'rgba(0, 139, 219, 0.2)',
					borderColor: 'rgba(0, 116, 184, 1)',
					borderWidth: 1
				},
				{
					label: 'Your score',
					data: scores,
					backgroundColor: 'rgba(204, 0, 44, 0.2)',
					borderColor: 'rgba(179, 0, 39, 1)',
					borderWidth: 1
				}
				]
			},
			options: options
		});
	});
}