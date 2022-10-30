var SeatResults = function() {
	let self = this;

	this.counting = ko.observable(null);
	this.loaded = ko.observable(false);
	this.seatsLoaded = ko.observable(false);
	this.showResults = ko.observable(false);
	this.showSeats = ko.observable(false);
	this.showDHondt = ko.observable(false);
	this.useTestData = ko.observable(false);
	
	this.toggleResults = function(surveyUid) {
		if (this.showResults()) {
			this.showResults(false);
		} else {
			this.showResults(true);
			if (this.useTestData()) {
				this.updateEVoteCountingChart();
				this.loaded(true);
			} else {
				this.loadCounting(surveyUid);
			}	
		}
	}
	
	this.loadCounting = function(surveyuid) {
		var model = this;
		var request = $.ajax({
			url: contextpath + "/noform/management/seatCounting",
			data: {surveyuid: surveyuid},
			dataType: "json",
			cache: false,
			success: function(data)
			{
				model.counting(data);
				model.updateEVoteCountingChart();
				model.loaded(true);
				$('[data-toggle="tooltip"]').tooltip();
			}
		});
	}
	
	this.toggleSeats = function(surveyuid) {
		if (this.showSeats()) {
			this.showSeats(false);
		} else {
			this.showSeats(true);
			
			if (this.useTestData()) {
				this.seatsLoaded(true);
			} else {
				this.loadSeats(surveyuid);	
			}
		}
	}
	
	this.loadSeats = function(surveyuid) {
		var model = this;
		var request = $.ajax({
			url: contextpath + "/noform/management/seatAllocation",
			data: {surveyuid: surveyuid},
			dataType: "json",
			cache: false,
			success: function(data)
			{
				model.counting(data);
				model.seatsLoaded(true);
				$('[data-toggle="tooltip"]').tooltip();
			}
		});
	}

	let eVoteCountingChart = null;
	this.updateEVoteCountingChart = function()
	{
		if (eVoteCountingChart != null) eVoteCountingChart.destroy();

		let ctx = $("#eVoteCountingChart");

		if(this.counting() == null)
			return;
		
		let graphColors = ['rgb(91, 155, 213)', 'rgb(237, 125, 49)', 'rgb(165, 165, 165)'];

		const countData = {
			labels: [
				labelEVoteCountVotes,
				labelEVoteCountBlankVotes,
				labelEVoteCountSpoiltVotes
			],
			datasets: [{
				data: [
					this.counting().votes,
					this.counting().blankVotes,
					this.counting().spoiltVotes,
				],
				backgroundColor: graphColors,
			}]
		};

		let config = {
			type: 'pie',
			data: countData,
			options: {
				responsive: true,
				maintainAspectRatio: false,
				legend: {
					position: 'bottom',
					labels:{
						boxWidth: 12
					}
				},
				plugins: {
					labels: {
						render: 'percentage',
						precision: 2,
						position: 'outside',
						outsidePadding: 4,
						textMargin: 4,
						fontColor: graphColors
					}
				}
			},
		};

		eVoteCountingChart = new Chart(ctx, config);
	}
}

let _seatResults = new SeatResults();

$(function() {
	ko.applyBindings(_seatResults, $("#results-seats")[0]);
});