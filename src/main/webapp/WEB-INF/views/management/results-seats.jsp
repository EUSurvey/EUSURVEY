<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>

<style>
	#results-seats h1 {
		margin-top: 40px !important;
	}
	
	#results-seats h2 {
		margin-top: 30px !important;
	}
</style>

<div id="results-seats" style="display: none; width: 730px; max-width:100%; margin-left: auto; margin-right:auto;">
	<a class="btn btn-primary" data-bind="click: function() {toggleResults('${form.survey.uniqueId}')}"><spring:message code="label.DisplayResults" /></a>
	<!-- ko if: counting() != null && counting().template != 'p' -->
		<a class="btn btn-primary" id="btnAllocateSeats" data-bind="visible: showResults() && loaded(), click: function() {toggleSeats('${form.survey.uniqueId}')}"><spring:message code="label.AllocateSeats" /></a>
		<a class="btn btn-primary" id="btnExportSeats" data-bind="visible: showResults() && showSeats(), attr: {href: '${contextpath}/${form.survey.shortname}/management/seatExport' + (useTestData() ? '?testdata=true&surveyuid=${form.survey.uniqueId}' : '?surveyuid=${form.survey.uniqueId}')}"><spring:message code="label.Export" /></a>
	<!-- /ko -->

	<div id="results-seats-counting" data-bind="visible: showResults()">
		<h1><spring:message code="label.seats.Counting" /></h1>
		<img data-bind="visible: counting() == null" class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" />
		<table data-bind="if: counting() != null" class="table table-condensed table-striped table-bordered" style="width: auto;">
			<tr>
				<th><spring:message code="label.Quorum" /></th>
				<td data-bind="text: counting().quorum"></td>
				<td rowspan="8" bgcolor="white">
					<div style="position: relative; min-width: 300px; min-height: 200px; margin-top: 20px;">
						<canvas id="eVoteCountingChart"></canvas>
					</div>
				</td>
			</tr>
			<tr>
				<th><spring:message code="label.seats.ParticipationRate" /></th>
				<td data-bind="text: getSeatPercent(counting().total, counting().voterCount) + ' (' + counting().total + ' / ' + counting().voterCount + ')'"></td>
			</tr>
			<tr>
				<th><spring:message code="label.Votes" /></th>
				<td data-bind="text: counting().votes"></td>
			</tr>
			<tr data-bind="if: counting().template != 'l' && counting().template != 'p' && counting().template != 'o'">
				<th style="padding-left: 20px"><spring:message code="label.seats.ListVotes" /></th>
				<td data-bind="text: counting().listVotes"></td>
			</tr>
			<tr data-bind="if: counting().template != 'l' && counting().template != 'p' && counting().template != 'o'">
				<th style="padding-left: 20px"><spring:message code="label.seats.PreferentialVotes" /></th>
				<td data-bind="text: counting().preferentialVotes"></td>
			</tr>
			<tr>
				<th><spring:message code="label.BlankVotes" /></th>
				<td data-bind="text: counting().blankVotes"></td>
			</tr>
			<tr>
				<th><spring:message code="label.SpoiltVotes" /></th>
				<td data-bind="text: counting().spoiltVotes"></td>
			</tr>
			<tr style="font-weight: bold">
				<td><spring:message code="label.seats.Total" /></td>
				<td data-bind="text: counting().total"></td>
			</tr>
		</table>
	</div>

	<!-- ko if: counting() != null && counting().template != 'p' -->
		<img data-bind="visible: showResults() && showSeats() && (seatsLoaded() == false)" class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" />
	<!-- /ko -->
	<div id="results-seats-allocation" data-bind="visible: showResults() && ((showSeats() && seatsLoaded()) || ${form.survey.geteVoteTemplate() == 'p'})" style="display: none; margin-top: 40px;">
		<!-- ko if: counting() != null && counting().template != 'p' -->
		<c:choose>
			<c:when test="${form.survey.eVoteTemplate == 'o' }">
				
				<!-- Allocation of the first 8 seats (provisional classification) -->
				<h1><spring:message code="label.seats.AllocationFirst" /></h1>
				<table data-bind="if: counting() != null" class="table table-condensed table-striped table-bordered" style="width: auto;">
					<tr style="font-weight: bold">
						<th><spring:message code="label.seats.List" /></th>
						<th><spring:message code="label.seats.Candidate" /></th>
						<th><spring:message code="label.Votes" /></th>
						<th><spring:message code="label.seats.Seats" /></th>
					</tr>
					<!-- ko foreach: counting().candidatesFromPreferentialVotes -->
					<tr>
						<td data-bind="html: list"></td>
						<td data-bind="html: name"></td>
						<td data-bind="text: votes"></td>
						<td data-bind="text: seats"></td>
					</tr>
					<!-- /ko -->
					<tr style="font-weight: bold">
						<td><spring:message code="label.seats.Total" /></td>
						<td></td>
						<td data-bind="text: counting().sumPreferentialVotes"></td>
						<td>						
							<!-- ko if: counting() != null && counting().template != 'o' -->
							<span data-bind="text: counting().preferentialVotesSeatsReal"></span>
							<!-- /ko -->
							<!-- ko if: counting() != null && counting().template == 'o' -->
							<span>8</span>
							<!-- /ko -->						
						</td>
					</tr>
				</table>
				
				<!-- Distributions of the 6 list seats -->
				<h1><spring:message code="label.seats.DistributionListSeats" /></h1>
				
				<table data-bind="if: counting() != null" class="table table-condensed table-striped table-bordered" style="width: auto;">
					<tr style="font-weight: bold">
						<th><spring:message code="label.seats.List" /></th>
						<th><spring:message code="label.Votes" /></th>
						<th>%</th>
					</tr>
					<!-- ko foreach: counting().listSeatDistribution -->
					<tr data-bind="attr: {style: listPercent &lt; $parent.counting().minListPercent ? 'background-color: #ffdada' : ''}">
						<td data-bind="html: name"></td>
						<td data-bind="text: preferentialVotes"></td>
						<td data-bind="text: listPercent"></td>
					</tr>
					<!-- /ko -->
					<tr style="font-weight: bold">
						<td><spring:message code="label.seats.Total" /></td>
						<td data-bind="text: counting().listVotes"></td>
						<td>100%</td>
					</tr>
				</table>
				
				<!-- ko if: counting() != null -->
					<!-- ko foreach: counting().reallocationMessagesForLists -->
						<p data-bind="html: $data"></p>
					<!-- /ko -->
				<!-- /ko -->
				
				<table data-bind="if: counting() != null" class="table table-condensed table-striped table-bordered" style="width: auto;">
					<tr style="font-weight: bold">
						<th><spring:message code="label.seats.List" /></th>
						<th><spring:message code="label.Votes" /></th>
						<th>%</th>
						<th><spring:message code="label.seats.Seats" /></th>
					</tr>
					<!-- ko foreach: counting().listSeatDistribution -->
					<!-- ko if: listPercent >= $parent.counting().minListPercent -->
					<tr>
						<td data-bind="html: name"></td>
						<td data-bind="text: preferentialVotes"></td>
						<td data-bind="text: listPercentFinal"></td>
						<td data-bind="text: listSeats"></td>
					</tr>
					<!-- /ko -->
					<!-- /ko -->
					<tr style="font-weight: bold">
						<td><spring:message code="label.seats.Total" /></td>
						<td data-bind="text: counting().listVotesFinal"></td>
						<td>100%</td>
						<td data-bind="text: counting().listVotesSeats"></td>
					</tr>
				</table>
				
				<button class="btn btn-default" data-bind="click: function() {showDHondt(!showDHondt())}, attr: {class: 'btn ' + (showDHondt() ? 'btn-primary' : 'btn-default')}"><spring:message code="label.seats.ShowDHondtTable" /></button>
				<table data-bind="if: showDHondt" class="table table-condensed table-striped table-bordered" style="width: auto; margin-top: 10px;">
					<tbody>
					<tr>
						<td><spring:message code="label.seats.Round" /></td>
						<!-- ko foreach: counting().listSeatDistribution -->
						<!-- ko if: listPercent >= $parent.counting().minListPercent -->
						<td data-bind="html: name"></td>
						<!-- /ko -->
						<!-- /ko -->
					</tr>			
					
					<!-- ko foreach: counting().dhondtEntries -->
					<tr>
						<td data-bind="text: $data[0].round"></td>
						
						<!-- ko foreach: $data -->
						<td>
							<span data-bind="text: value.toFixed(2)"></span>
							<!-- ko if: seat > 0 -->
							<span data-bind="text: '(' + seat + ')'"></span>
							<!-- /ko -->
						</td>
						<!-- /ko -->
						
					</tr>
					<!-- /ko -->
					</tbody>
				</table>		
			
			</c:when>
			<c:otherwise>		

				<!-- ko if: counting != null && counting().template != 'l' -->
				<h1><spring:message code="label.seats.WeightingOfVotes" /></h1>
				<table class="table table-condensed table-striped table-bordered" style="width: auto;">
					<tr>
						<th></th>
						<th><spring:message code="label.seats.ListVotes" /></th>
						<th><spring:message code="label.seats.ListVotesWeighted" /></th>
						<th><spring:message code="label.seats.CandidateVotes" /></th>
						<th><spring:message code="label.seats.Total" /></th>
						<th>%</th>
					</tr>
					
					<!-- ko foreach: counting().listSeatDistribution -->
					<tr data-bind="attr: {style: listPercentWeighted &lt; $parent.counting().minListPercent ? 'background-color: #ffdada' : ''}">
						<td data-bind="html: name"></td>
						<td data-bind="text: listVotes"></td>
						<td data-bind="text: listVotesWeighted"></td>
						<td data-bind="text: preferentialVotes"></td>
						<td data-bind="text: totalWeighted"></td>
						<td data-bind="text: listPercentWeighted"></td>				
					</tr>
					<!-- /ko -->
					
					<tr style="font-weight: bold">
						<td><spring:message code="label.seats.Total" /></td>
						<td data-bind="text: counting().listVotes"></td>
						<td data-bind="text: counting().listVotesWeighted"></td>
						<td data-bind="text: counting().totalPreferentialVotes"></td>
						<td data-bind="text: counting().totalVotesWeighted"></td>
						<td>100%</td>
					</tr>
				</table>
				<!-- /ko -->
				
				<!-- ko if: counting() != null && counting().template == 'l' -->
				<h1><spring:message code="label.seats.DHondtSeatDistribution" /></h1>
				<table class="table table-condensed table-striped table-bordered" style="width: auto;">
					<tr>
						<th></th>
						<th><spring:message code="label.Votes" /></th>
						<th>%</th>
					</tr>
					<!-- ko foreach: counting().listSeatDistribution -->
					<tr data-bind="attr: {style: preferentialPercent &lt; $parent.counting().minListPercent ? 'background-color: #ffdada' : ''}">
						<td data-bind="html: name"></td>
						<td data-bind="text: luxListVotes"></td>
						<td data-bind="text: listPercent"></td>				
					</tr>
					<!-- /ko -->
					<tr style="font-weight: bold">
						<td><spring:message code="label.seats.Total" /></td>
						<td data-bind="text: counting().luxListVotes"></td>				
						<td>100%</td>
					</tr>
				</table>
				<!-- /ko -->
				
				<!-- ko if: counting() != null && counting().template != 'l' -->
				<h1><spring:message code="label.seats.AllocationOfSeatsPC" /></h1>
				<table class="table table-condensed table-striped table-bordered" style="width: auto;">
					<tr>
						<th></th>
						<th></th>
						<th></th>
						<th><spring:message code="label.seats.Prorata" /></th>
					</tr>
					<tr>
						<td><spring:message code="label.seats.ListVotes" /></td>
						<td data-bind="text: counting().listVotes"></td>
						<td data-bind="text: getSeatPercent(counting().listVotes, counting().listVotes + counting().preferentialVotes)"></td>
						<td data-bind="text: counting().listVotesSeats"></td>
					</tr>
					<tr>
						<td><spring:message code="label.seats.PreferentialVotes" /></td>
						<td data-bind="text: counting().preferentialVotes"></td>
						<td data-bind="text: getSeatPercent(counting().preferentialVotes, counting().listVotes + counting().preferentialVotes)"></td>
						<td data-bind="text: counting().preferentialVotesSeats"></td>
					</tr>
					<tr>
						<td><spring:message code="label.seats.Total" /></td>
						<td data-bind="text: counting().listVotes + counting().preferentialVotes"></td>
						<td>100%</td>
						<td data-bind="text: counting().maxSeats"></td>
					</tr>
				</table>
				
				<h2><spring:message code="label.seats.DistributionListSeats" />:</h2>
				<!-- ko if: counting() != null -->
					<!-- ko foreach: counting().reallocationMessagesForLists -->
						<p data-bind="html: $data"></p>
					<!-- /ko -->
				<!-- /ko -->
				<table data-bind="if: counting() != null" class="table table-condensed table-striped table-bordered" style="width: auto;">
					<tr style="font-weight: bold">
						<th><spring:message code="label.seats.List" /></th>
						<th><spring:message code="label.Votes" /></th>
						<th>%</th>
						<th><spring:message code="label.seats.Seats" /></th>
					</tr>
					<!-- ko foreach: counting().listSeatDistribution -->
					<!-- ko if: listPercentWeighted >= $parent.counting().minListPercent -->
					<tr>
						<td data-bind="html: name"></td>
						<td data-bind="text: listVotes"></td>
						<td data-bind="text: listPercentFinal"></td>
						<td data-bind="text: listSeats"></td>
					</tr>
					<!-- /ko -->
					<!-- /ko -->
					<tr style="font-weight: bold">
						<td><spring:message code="label.seats.Total" /></td>
						<td data-bind="text: counting().listVotesFinal"></td>
						<td>100%</td>
						<td data-bind="text: counting().listVotesSeatsReal"></td>
					</tr>
				</table>
				
				<h2><spring:message code="label.seats.DistributionPreferentialSeats" />:</h2>
				<!-- /ko -->
				<!-- ko if: counting() != null -->
					<!-- ko foreach: counting().reallocationMessages -->
						<p data-bind="html: $data"></p>
					<!-- /ko -->
				<!-- /ko --> 
				<table data-bind="if: counting() != null" class="table table-condensed table-striped table-bordered" style="width: auto;">
					<tr style="font-weight: bold">
						<th><spring:message code="label.seats.List" /></th>
						<th><spring:message code="label.Votes" /></th>
						<th>%</th>
						<th><spring:message code="label.seats.Seats" /></th>
					</tr>
					<!-- ko foreach: counting().listSeatDistribution -->
					<!-- ko if: listPercentWeighted >= $parent.counting().minListPercent -->
					<tr>
						<td data-bind="html: name"></td>
						<td data-bind="text: $parent.counting().template != 'l' ? preferentialVotes : luxListVotes"></td>
						<td data-bind="text: preferentialPercentFinal"></td>
						<td data-bind="text: preferentialSeats"></td>
					</tr>
					<!-- /ko -->
					<!-- /ko -->
					<tr style="font-weight: bold">
						<td><spring:message code="label.seats.Total" /></td>
						<td data-bind="text: counting().preferentialVotesFinal"></td>
						<td>100%</td>
						<td data-bind="text: counting().preferentialVotesSeatsReal"></td>
					</tr>
				</table>
							
				<!-- ko if: counting() != null && counting().template != 'l' -->
				<h2><spring:message code="label.seats.ElectedCandidatesListVotes" />:</h2>
				<table class="table table-condensed table-striped table-bordered" style="width: auto;">
					<tr style="font-weight: bold">
						<th><spring:message code="label.seats.List" /></th>
						<th><spring:message code="label.seats.Candidate" /></th>
						<th><spring:message code="label.Votes" /></th>
						<th><spring:message code="label.seats.Seats" /></th>
					</tr>
					<!-- ko foreach: counting().candidatesFromListVotes -->
					<tr>
						<td data-bind="html: list"></td>
						<td data-bind="html: name"></td>
						<td data-bind="text: votes"></td>
						<td data-bind="text: seats"></td>
					</tr>
					<!-- /ko -->
					<tr style="font-weight: bold">
						<td><spring:message code="label.seats.Total" /></td>
						<td></td>
						<td data-bind="text: counting().sumListVotes"></td>
						<td data-bind="text: counting().listVotesSeatsReal"></td>
					</tr>
				</table>
				<!-- /ko -->
				
				<!-- ko if: counting() != null && counting().template == 'l' -->
				<button class="btn btn-default" data-bind="click: function() {showDHondt(!showDHondt())}, attr: {class: 'btn ' + (showDHondt() ? 'btn-primary' : 'btn-default')}"><spring:message code="label.seats.ShowDHondtTable" /></button>
				<table data-bind="if: showDHondt" class="table table-condensed table-striped table-bordered" style="width: auto; margin-top: 10px;">
					<tbody>
					<tr>
						<td><spring:message code="label.seats.Round" /></td>
						<!-- ko foreach: counting().listSeatDistribution -->
						<!-- ko if: listPercentWeighted >= $parent.counting().minListPercent -->
						<td data-bind="html: name"></td>
						<!-- /ko -->
						<!-- /ko -->
					</tr>			
					
					<!-- ko foreach: counting().dhondtEntries -->
					<tr>
						<td data-bind="text: $data[0].round"></td>
						
						<!-- ko foreach: $data -->
						<td>
							<span data-bind="text: value.toFixed(2)"></span>
							<!-- ko if: seat > 0 -->
							<span data-bind="text: '(' + seat + ')'"></span>
							<!-- /ko -->
						</td>
						<!-- /ko -->
						
					</tr>
					<!-- /ko -->
					</tbody>
				</table>		
				<!-- /ko -->		
			
				<!-- ko if: counting() != null && counting().template == 'l' -->
				<h2><spring:message code="label.seats.ElectedCandidatesProvisionalClassification" />:</h2>
				<!-- /ko -->
				<!-- ko if: counting() != null && counting().template != 'l' -->
				<h2><spring:message code="label.seats.ElectedCandidatesPreferentialVotes" />:</h2>
				<!-- /ko -->
				
				<table data-bind="if: counting() != null" class="table table-condensed table-striped table-bordered" style="width: auto;">
					<tr style="font-weight: bold">
						<th><spring:message code="label.seats.List" /></th>
						<th><spring:message code="label.seats.Candidate" /></th>
						<th><spring:message code="label.Votes" /></th>
						<th><spring:message code="label.seats.Seats" /></th>
					</tr>
					<!-- ko foreach: counting().candidatesFromPreferentialVotes -->
					<tr>
						<td data-bind="html: list"></td>
						<td data-bind="html: name"></td>
						<td data-bind="text: votes"></td>
						<td data-bind="text: seats"></td>
					</tr>
					<!-- /ko -->
					<tr style="font-weight: bold">
						<td><spring:message code="label.seats.Total" /></td>
						<td></td>
						<td data-bind="text: counting().sumPreferentialVotes"></td>
						<td data-bind="text: counting().preferentialVotesSeatsReal"></td>
					</tr>
				</table>
			</c:otherwise>
		</c:choose>

		<div data-bind="if: counting() !=null && counting().ambiguous" style="color: #f00">
			<spring:message code="info.seats.ambiguous" />
		</div>
		
		<h1><spring:message code="label.seats.NumberVotesPerCandidate" /></h1>
		<table data-bind="if: counting() != null" class="table table-condensed table-striped table-bordered" style="width: auto;">
			<tr style="font-weight: bold">
				<td><spring:message code="label.seats.CandidateNumber" /></td>
				<!-- ko foreach: counting().listSeatDistribution -->
					<!-- ko if: listPercentWeighted >= $parent.counting().minListPercent -->
						<td data-bind="html: name"></td>
					<!-- /ko -->
				<!-- /ko -->
				<!-- ko foreach: counting().listSeatDistribution -->
					<!-- ko ifnot: listPercentWeighted >= $parent.counting().minListPercent -->
					<td>
						<span data-bind="html: name" style="color: #777;"></span>
						<a onclick="$(this).closest('td').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign" style="color: #aaa;"></span></a>
						<div class="help hideme"><spring:message code="help.EligibleLists" /></div>
					</td>
					<!-- /ko -->
				<!-- /ko -->
			</tr>
			<tr data-bind="if: counting().template != 'l' && counting().template != 'o'" style="font-weight: bold">
				<td><spring:message code="label.seats.TotalListVotes" /></td>
				<!-- ko foreach: counting().listSeatDistribution -->
					<!-- ko if: listPercentWeighted >= $parent.counting().minListPercent -->
						<td data-bind="text: listVotes"></td>
					<!-- /ko -->
				<!-- /ko -->
				<!-- ko foreach: counting().listSeatDistribution -->
					<!-- ko ifnot: listPercentWeighted >= $parent.counting().minListPercent -->
						<td data-bind="text: listVotes" style="color: #777;"></td>
					<!-- /ko -->
				<!-- /ko -->
			</tr>
			<!-- ko foreach: counting().candidateVotes -->
			<tr>	
				<td data-bind="text: $index()+1"></td>
				
				<!-- ko foreach: $data -->
				<td data-bind="attr: {style: 'background-color: ' + getCellBackground($data) + ';' + 'color: ' + getFontColor($data)}">
					<span data-toggle="tooltip" data-bind="text: votes, attr: {title: name}"></span>
				</td>
				<!-- /ko -->
			</tr>
			<!-- /ko -->
			<tr style="font-weight: bold">
				<td>
					<span data-bind="if: counting().template != 'l' && counting().template != 'o'"><spring:message code="label.seats.TotalPreferentialVotes" /></span>
					<span data-bind="if: counting().template == 'l' || counting().template == 'o'"><spring:message code="label.seats.Total" /></span>
				</td>
				<!-- ko foreach: counting().listSeatDistribution -->
					<!-- ko if: listPercentWeighted >= $parent.counting().minListPercent -->
						<td data-bind="text: $parent.counting().template != 'l' ? preferentialVotes : luxListVotes"></td>
					<!-- /ko -->
				<!-- /ko -->
				<!-- ko foreach: counting().listSeatDistribution -->
					<!-- ko ifnot: listPercentWeighted >= $parent.counting().minListPercent -->
						<td data-bind="text: $parent.counting().template != 'l' ? preferentialVotes : luxListVotes" style="color: #777;"></td>
					<!-- /ko -->
				<!-- /ko -->
			</tr>
		</table>
		<!-- ko if: counting() != null -->
		<table>
			<tr>
				<td style="background-color: #00E266; width: 50px;"></td>
				<td style="padding-left: 10px;">
					<!-- ko if: counting() != null && counting().template != 'o' -->
					<spring:message code="label.seats.ElectedFromListVotes" />
					<!-- /ko -->
					<!-- ko if: counting() != null && counting().template == 'o' -->
					<spring:message code="label.seats.ElectedThroughListVotes" />
					<!-- /ko -->
				</td>
			</tr>			
			<tr>
				<td style="background-color: #FFC000; width: 50px;"></td>
				<td style="padding-left: 10px;">
					<!-- ko if: counting() != null && counting().template != 'o' -->
					<spring:message code="label.seats.ElectedFromPreferentialVotes" />
					<!-- /ko -->
					<!-- ko if: counting() != null && counting().template == 'o' -->
					<spring:message code="label.seats.ElectedByHighest" />
					<!-- /ko -->	
				</td>
			</tr>
			<tr>
				<td style="background-color: #ffabab; width: 50px;"></td>
				<td style="padding-left: 10px;">
					<spring:message code="label.seats.Reallocated" />
				</td>
			</tr>
			<tr>
				<td style="background-color: #E064E3; width: 50px;"></td>
				<td style="padding-left: 10px;">
					<!-- ko if: counting() != null && counting().template == 'o' -->
					<spring:message code="label.seats.Ambiguous" />
					<!-- /ko -->
				</td>
			</tr>
		</table>
		<!-- /ko -->
		<!-- /ko -->
		
		<!-- ko if: counting() != null && counting().template == 'p' -->
		<h1><spring:message code="label.seats.NumberVotesPerCandidate" /></h1>
		<div id="eVoteSinglePresidentChartContainer" style="position: relative; margin-top: 10px; margin-bottom: 10px;">
			<canvas id="eVoteSinglePresidentChart"></canvas>
		</div>

		<div data-bind="if: counting() !=null && counting().ambiguous" style="color: #f00">
			<spring:message code="info.seats.ambiguous" />
		</div>

        <table data-bind="if: counting() != null" class="table table-condensed table-striped table-bordered" style="width: auto; margin-top: 30px;">
            <tr style="font-weight: bold">
                <th><spring:message code="label.seats.List" /></th>
                <th><spring:message code="label.seats.Candidate" /></th>
                <th><spring:message code="label.Votes" /></th>
            </tr>
            <!-- ko foreach: counting().candidatesFromPreferentialVotes -->
				<!-- ko if: $parent.counting().highestVote != null && $parent.counting().highestVote == votes -->
					<tr>
						<td data-bind="html: list" style="background-color: #FFC000"></td>
						<td data-bind="html: name" style="background-color: #FFC000"></td>
						<td data-bind="text: votes" style="background-color: #FFC000"></td>
					</tr>
				<!-- /ko -->
				<!-- ko ifnot: $parent.counting().highestVote != null && $parent.counting().highestVote == votes -->
					<tr>
						<td data-bind="html: list"></td>
						<td data-bind="html: name"></td>
						<td data-bind="text: votes"></td>
					</tr>
				<!-- /ko -->
			<!-- /ko -->
            <tr style="font-weight: bold">
                <td><spring:message code="label.seats.Total" /></td>
                <td></td>
                <td data-bind="text: counting().sumPreferentialVotes"></td>
            </tr>
        </table>

		<table data-bind="if: counting() != null">
			<tr>
				<td style="background-color: #FFC000; width: 50px;"></td>
				<td style="padding-left: 10px;">
					<spring:message code="label.seats.ElectedStandard" />
				</td>
			</tr>
		</table>
		<!-- /ko -->
	</div>
	
</div>

<script src="${contextpath}/resources/js/chartjs-plugin-labels.js"></script>
<script type="text/javascript" src="${contextpath}/resources/js/results-seats.js?version=<%@include file="../version.txt" %>"></script>

<script type="text/javascript">
	const labelEVoteCountVotes = '<spring:message code="label.Votes" />';
	const labelEVoteCountBlankVotes = '<spring:message code="label.BlankVotes" />';
	const labelEVoteCountSpoiltVotes = '<spring:message code="label.SpoiltVotes" />';

	function getSeatPercent(value, voterCount) {
		return (Math.round(value / voterCount * 10000) / 100) + "%";
	}
	
	function getCellBackground(candidate) {
		if (candidate.ambiguous) {
			return "#E064E3";
		}
		
		if (candidate.seats > 0) {
			if (candidate.preferentialSeat) {
				return "#FFC000";
			}
			return "#00E266";
		} else {
			if (candidate.reallocatedSeat) {
				return "#ffabab";
			}
		}
		
		return "";
	}

	function getFontColor(candidate) {
		if (candidate.listNotAccepted) {
			return "#777";
		}

		return "";
	}
</script>
		
