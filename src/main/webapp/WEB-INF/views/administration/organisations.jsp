<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.ec.survey.model.administration.GlobalPrivilege" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Organisations" /></title>
	<%@ include file="../includes.jsp" %>
	<script type="text/javascript" src="${contextpath}/resources/js/jquery.stickytableheaders.js?version=<%@include file="../version.txt" %>"></script>
	
	<script type="text/javascript"> 
		$(document).ready(function() {
			$("#administration-menu-tab").addClass("active");
			$("#organisations-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
			
			$('[data-toggle="tooltip"]').tooltip();
			
			const currentyear = new Date().getFullYear();
			for (let i = currentyear; i > currentyear - 5; i--) {
				if (i < 2024) break;
				var o = document.createElement("option");
				$(o).append(i);
				$('#year').append(o);
			}
			
			$('#year').val(currentyear);
			
					
			$.ajax({type: "GET",
				url: contextpath + "/utils/Organisations",
				async: false,
			    success :function(result)
			    {		    	
			    	$.each(result.dgs, function(key, data){
			    		var option = document.createElement("option");
			    		$(option).attr("value", key).append(data);
			    		$('#organisation-dgs').append(option);
			    	});	
			    	
			    	$.each(result.executiveAgencies, function(key, data){
			    		var option = document.createElement("option");
			    		$(option).attr("value", key).append(data);
			    		$('#organisation-aex').append(option);
			    	});	
			    	
			    	$.each(result.otherEUIs, function(key, data){
			    		var option = document.createElement("option");
			    		$(option).attr("value", key).append(data);
			    		$('#organisation-euis').append(option);
			    	});	
			    	
			    	$.each(result.nonEUIs, function(key, data){
			    		var option = document.createElement("option");
			    		$(option).attr("value", key).append(data);
			    		$('#organisation-noneuis').append(option);
			    	});	
			    }
			 });
		})
		
		function loadOrganisation() {
			
			if ( $('#organisation').val().length == 0) {
				$('#downloadOrganisationReport').attr("disabled", "disabled");
				$('#totalSurveys').text("");
		    	$('#totalContributions').text("");
		    	$('#surveysThisYear').text("");
		    	$('#surveysLastYear').text("");
		    	$('#contributionsThisYear').text("");
		    	$('#contributionsLastYear').text("");
		    	return;
			}
			
			$('#generic-wait-dialog').modal('show');
			$.ajax({type: "GET",
				url: contextpath + "/administration/organisation/" + $('#organisation').val(),
				dataType: 'json',
				success :function(result)
			    {	
			    	$('#totalSurveys').text(result.allSurveys);
			    	$('#totalContributions').text(result.allContributions);
			    	$('#surveysThisYear').text(result.surveysThisYear);
			    	$('#surveysLastYear').text(result.surveysLastYear);
			    	$('#contributionsThisYear').text(result.contributionsThisYear);
			    	$('#contributionsLastYear').text(result.contributionsLastYear);
			    	$('#generic-wait-dialog').modal('hide');
			    	$('#downloadOrganisationReport').removeAttr("disabled");
			    }, error: function(e) {
					console.log(e);
					$('#generic-wait-dialog').modal('hide');
				}
			 });
		}
		
		function downloadOrganisationReport() {
			$('#monthError').hide();
			if ($('#organisation').val().length == 0) return;
			
			var start = parseInt($('#month').val());
			var end = parseInt($('#monthEnd').val());
			
			if (end > 0 && end < start) {
				$('#monthError').show();
			}
			
			window.location = "${contextpath}/administration/organisationreport/" + $('#organisation').val() + "/" + $('#format').val() + "/" + $('#year').val() + "/" + start + "/" + end + "?min=" + $('#minpublished').val();
		}
		
		function checkFormat() {
			if ($('#format').val() == "JSON") {
				$('#divMinPublishedSurveys').show();
				$('#divMonthEnd').show();
			} else {
				$('#divMinPublishedSurveys').hide();
				$('#divMonthEnd').hide();
			}
		}
	</script>		
</head>
<body>
	<div class="page-wrap">
	<%@ include file="../header.jsp" %>
	<%@ include file="../menu.jsp" %>
	<%@ include file="adminmenu.jsp" %>	
		
	<div class="page1024" style="margin-bottom: 0px;">				
		<div style="width: 680px; margin-left: auto; margin-right: auto; margin-top: 140px; margin-bottom: 100px;">
			<label><spring:message code="label.Organisation" /></label>
			<select class="form-control" id="organisation" style="width: auto; width: 100%;" onchange="loadOrganisation()">
				<option></option>	
				<option value="all"><spring:message code="label.All" /></option>						
			  	<optgroup id="organisation-dgs" label="<spring:message code="label.EuropeanCommission" />: <spring:message code="label.DGsAndServices" />">
			    </optgroup>
			
			    <optgroup id="organisation-aex" label="<spring:message code="label.EuropeanCommission" />: <spring:message code="label.ExecutiveAgencies" />">
			    </optgroup>
			    
			    <optgroup id="organisation-euis" label="<spring:message code="label.OtherEUIs" />">
			    </optgroup>
			    
			    <optgroup id="organisation-noneuis" label="<spring:message code="label.NonEUIentities" />">
				</optgroup>
			</select>
			
			<table class="table table-bordered" style="margin-top: 40px;">
				<tr>
					<td>Total number of surveys:</td>
					<td id="totalSurveys"></td>
					<td>Total number of contributions:</td>
					<td id="totalContributions"></td>
				</tr>
				<tr>
					<td>Number of surveys published this year:</td>
					<td id="surveysThisYear"></td>
					<td>Number of contributions received this year:</td>
					<td id="contributionsThisYear"></td>
				</tr>
				<tr>
					<td>Number of surveys published last year:</td>
					<td id="surveysLastYear"></td>
					<td>Number of contributions received last year:</td>
					<td id="contributionsLastYear"></td>
				</tr>
			</table>
			
			<div style="background-color: #ddd; padding: 10px; border-radius: 5px;">
				<b><spring:message code="label.Report" /></b><br /><br />
				
				<div class="form-group" style="float: left; margin-right: 10px;">
					<label for="format"><spring:message code="label.Format" /></label>
					<select class="form-control" id="format" style="width: auto" onchange="checkFormat()">
						<option selected="selected">CSV</option>
						<option>JSON</option>
					</select>
				</div>
				
				<div class="form-group" style="float: left; margin-right: 10px;">					
					<label for="year"><spring:message code="label.Year" /></label>
					<select class="form-control" id="year" style="width: auto"></select>
				</div>
				
				<div class="form-group" style="float: left; margin-right: 5px;">
					<label for="month"><spring:message code="label.Month" /></label>
					<select class="form-control" id="month" style="width: auto">
						<option value="0"></option>
						<option>1</option>
						<option>2</option>
						<option>3</option>
						<option>4</option>
						<option>5</option>
						<option>6</option>
						<option>7</option>
						<option>8</option>
						<option>9</option>
						<option>10</option>
						<option>11</option>
						<option>12</option>
					</select>
				</div>
				
				<div class="form-group" id="divMonthEnd" style="float: left; margin-right: 10px; padding-top: 25px; display: none;">
					<span>-</span>
					<select class="form-control" id="monthEnd" style="width: auto; display: inline-block;">
						<option value="0"></option>
						<option>1</option>
						<option>2</option>
						<option>3</option>
						<option>4</option>
						<option>5</option>
						<option>6</option>
						<option>7</option>
						<option>8</option>
						<option>9</option>
						<option>10</option>
						<option>11</option>
						<option>12</option>
					</select>
				</div>
				
				<div class="form-group" id="divMinPublishedSurveys" style="float: left; margin-right: 10px; display: none;">
					<label for="month"><spring:message code="label.MinPublishedSurveys" /></label>
					<input type="number" id="minpublished" class="form-control" />
				</div>
				
				<div style="clear: both"></div>
				
				<div id="monthError" class="validation-error" style="display: none"><spring:message code="error.EndMonthSmaller" /></div>
				
				<div style="padding-top: 0px; text-align: right;">
					<button id="downloadOrganisationReport" onclick="downloadOrganisationReport()" class="btn btn-primary" disabled="disabled"><spring:message code="label.GenerateReport" /></button>
				</div>
			</div>
		</div>
	</div>	
		
	</div>
		
	<%@ include file="../footer.jsp" %>		


</body>
</html>
