var QuorumResults = function() {
	let self = this;

	this.voterCount = ko.observable(0);
	this.voterQuorum = ko.observable(0);
	this.voteContributions = ko.observable(null);
	this.voterListLoaded = ko.observable(false);
	this.quorum = ko.observable(0);

	this.loadVoterContributions = function(surveyid, scale, quorum) {
		var model = this;
		model.voterQuorum(quorum);
		var request = $.ajax({
			url: contextpath + "/dashboard/quorumContributions",
			data: {surveyid: surveyid, span: scale},
			dataType: "json",
			cache: false,
			success: function(data)
			{
				model.quorum(quorum);
				model.voteContributions(data);
				model.voterCount(data.voters);
				model.updateQuorumChart(scale);

				$("#results-quorum").show()
				$("#results-quorum-loader").hide()

				if (data.voters > 0 && data.numberOfContributions > 0) {
					$('#results-statistics-seats-link').show();
				}
			}
		});
	}

	this.quorumText = function(quorum) {
		return quorum + ' / ' + this.voterCount();
	}

	this.quorumLimit = function(quorum) {
		return Math.ceil(this.voterCount() * (quorum/10000.0));
	}

	this.formatTimeStamps = function(days, span) {
		let formattedDays = [];
		let d;
		for(let i = 0; i < days.length; i++){
			d = new Date(days[i]);
			if(span == "quorumDays"){
				formattedDays[i] = $.datepicker.formatDate("dd.mm", d)
			} else if(span == "quorumHours") {
				formattedDays[i] = String(d.getHours()) + '-' + String(d.getHours()+1);
			}
		}
		return formattedDays;
	}

	let quorumChart;
	this.updateQuorumChart = function(scale) {
		if(quorumChart != null) {
			quorumChart.destroy();
			$("#quorum").find('.chart-download').removeAttr('href');
			$("#quorum").find('.chart-download').find('.glyphicon-save').toggleClass('glyphicon-save').toggleClass('glyphicon-refresh');
			$("#quorum-chart-download").tooltip("disable");
		}

		let ctx = $("#quorumChart");
		
		let max = this.voteContributions().contributionStates[0] + 1000;
		if (max < this.quorum() && max > (this.quorum() / 10.0)) {
			max = this.quorum();
		}

		let config = {
			type: 'bar',
			data: {
				labels: this.formatTimeStamps(this.voteContributions().days, scale),
				datasets: [{
					label: labelVotesReceived,
					data: this.voteContributions().answers,
					backgroundColor: 'rgb(51, 122, 183)',
					borderColor: 'rgb(51, 122, 183)',
					borderWidth: 1,
					barThickness: 40
				}]
			},
			options: {
				plugins: {
			      	labels: false,
			    },
				scales: {
					y: {
						beginAtZero: true
					},
					yAxes: [{
						display: true,
						ticks: {
							beginAtZero: true,
							max: max
						},
						scaleLabel: {
							display: true,
							fontSize: 14,
							labelString: labelNumberOfVotes
						}
					}],
					xAxes: [{
						gridLines: {
							color: "rgba(0, 0, 0, 0)",
						}
					}],
				},
				legend: {
					display: false
				},
				tooltips: {
					displayColors: false
				},
				horizontalLine: {
					y: this.quorum(),
				},
				animation: {
					onComplete: function (animation) {
						$("#quorum").find('.chart-download').attr({href: this.toBase64Image(), download: "quorum.png"});
						$("#quorum").find('.chart-download').find('.glyphicon-refresh').toggleClass('glyphicon-refresh').toggleClass('glyphicon-save');
						$('[data-toggle="tooltip"]').tooltip();
					}
				}
			}
		}
		quorumChart = new Chart(ctx, config);
	}
}

let _quorumResults = new QuorumResults();

$(function() {
	ko.applyBindings(_quorumResults, $("#results-quorum")[0]);
});