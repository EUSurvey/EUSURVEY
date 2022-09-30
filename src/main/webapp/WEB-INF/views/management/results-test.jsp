<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>

<style>
	#results-test input {
		width: 100px;		
		border: 1px solid #ccc;
	}
	
	#results-test td {
		padding: 2px;
	}
</style>

<script type="text/javascript">
	var listUids = [];
	<c:forEach var="question" items="${form.survey.questions}">
		<c:if test="${question.getType() == 'MultipleChoiceQuestion'}">
		listUids.push("${question.getUniqueId()}");
		</c:if>
	</c:forEach>

	function startTestComputation(surveyuid) {
		var data = {surveyuid: surveyuid};
		
		$('#results-test').find("input[type=number]").each(function(){
			var id = $(this).attr("id");
			var value = $(this).val();
			data[id] = value;
		});
		
		var request = $.ajax({
			url: contextpath + "/noform/management/seatCountingTest",
			data:data,
			dataType: "json",
			cache: false,
			success: function(data)
			{
				_seatResults.counting(data);
				_seatResults.updateEVoteCountingChart();
				_seatResults.loaded(true);
				_seatResults.useTestData(true);
				$('[data-toggle="tooltip"]').tooltip();
				$('#results-statistics-seats-link').show();
				switchTo('seats');
			}
		});
	}
	
	$(function() {	
		var uploader = new qq.FileUploader({
			element: $("#seatTestUpload")[0],
			action: contextpath + '/${form.survey.shortname}/management/uploadSeatTest',
			uploadButtonText: '<spring:message code="label.UploadFile" />',
			params: {
				'_csrf': csrftoken
			},
			multiple: false,
			cache: false,
			sizeLimit: 10485760,
			onComplete: function(id, fileName, result)
			{
				console.log(result);
				$('#blankvotes').val(result.blankVotes);
				$('#spoiltvotes').val(result.spoiltVotes);
				$('#preferentialvotes').val(result.preferentialVotes);
				
				<c:if test="${form.survey.geteVoteTemplate() != 'l'}">
					//list votes not for Luxembourg templates
					for (let i = 0; i < listUids.length; i++) {
						$('#listvotes' + listUids[i]).val(result.lists[listUids[i]].listVotes);
					}
				</c:if>
				
				for (let i = 0; i < listUids.length; i++) {
					var candidatesArray = Object.entries(result.lists[listUids[i]].candidateVotes);
					for (let c = 1; c <= candidatesArray.length; c++) {
						$('#' + listUids[i] + "-" + c).val(candidatesArray[c-1][1]);	
					}
				}
				
			},
			showMessage: function(message){
				$("#uploadSeatTestError").append("<div class='validation-error'>" + message + "</div>");
			},
			onUpload: function(id, fileName, xhr){
				$("#uploadSeatTestError").find(".validation-error").remove();
			}
		});

		$(".qq-uploader").css("display", "inline");
		$(".qq-upload-button").addClass("btn btn-default").removeClass("qq-upload-button");
		$(".qq-upload-list").hide();
		$(".qq-upload-drop-area").css("margin-left", "-1000px");
		$("input[type=file]").attr("aria-label", "<spring:message code="info.uploadbutton" />");
		
		$(".filtercell input").on('keyup', function (event) {
	      if (event.keyCode === 13) {
	    	  firstVoterPage();
	      }
	    });
	});
</script>

<div id="results-test" style="display: none; width: 1000px; max-width:100%; margin-left: auto; margin-right:auto;">
	<table class="table table-bordered table-striped table-condensed" style="width: auto;">
		<tr>
			<th><spring:message code="label.BlankVotes" /></th>
			<td>
				<input type="number" id="blankvotes" />
			</td>
		</tr>
		<tr>
			<th><spring:message code="label.SpoiltVotes" /></th>
			<td>
				<input type="number" id="spoiltvotes" />
			</td>
		</tr>
		<tr>
			<th><spring:message code="label.seats.PreferentialVotes" /></th>
			<td>
				<input type="number" id="preferentialvotes" />
			</td>
		</tr>
	</table>	
	
	<table class="table table-bordered table-striped table-condensed" style="width: auto;">
		<tr>
			<th></th>
			<c:forEach var="question" items="${form.survey.questions}">
				<c:if test="${question.getType() == 'MultipleChoiceQuestion'}">
				<th>${question.getStrippedTitle()}</th>
				</c:if>
			</c:forEach>
		</tr>
		
		<c:if test="${form.survey.geteVoteTemplate() != 'l'}">
		<tr>
			<th><spring:message code="label.seats.ListVotes" /></th>
			<c:forEach var="question" items="${form.survey.questions}">
				<c:if test="${question.getType() == 'MultipleChoiceQuestion'}">
				<td>
					<input type="number" id="listvotes${question.getUniqueId()}" />
				</td>
				</c:if>
			</c:forEach>
		</tr>
		</c:if>
		
		<c:forEach begin="1" end="${form.survey.maxCandidatesCount}" varStatus="candidateindex">
		   <tr>
			   <th><spring:message code="label.seats.Candidate" />&nbsp;${candidateindex.index}</th>
			   <c:forEach var="question" items="${form.survey.questions}">
					<c:if test="${question.getType() == 'MultipleChoiceQuestion'}">
					<td>
						<input type="number" id="${question.getUniqueId()}-${candidateindex.index}" />
					</td>
					</c:if>
				</c:forEach>
		   </tr>
		</c:forEach>		
	</table>
	
	<a class="btn btn-primary" onclick="startTestComputation('${form.survey.getUniqueId()}')"><spring:message code="label.ExecuteTest" /></a>
	<a class="btn btn-default" href="${contextpath}/${form.survey.shortname}/management/seatTestDownload"><spring:message code="label.DownloadTemplateFile" /></a>
	<span id="seatTestUpload"></span>
	<span id="uploadSeatTestError"></span>
	
</div>	
