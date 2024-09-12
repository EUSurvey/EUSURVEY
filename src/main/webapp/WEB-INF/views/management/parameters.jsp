<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Parameters" /></title>
	
	<%@ include file="../includes.jsp" %>
	
	<link href="${contextpath}/resources/css/management.css" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/form.css" rel="stylesheet" type="text/css" />
		
	<script type="text/javascript">
		var errorOperationFailed = '<spring:message code="error.OperationFailed" />';
		var surveyUID = "${form.survey.uniqueId}";

		$(function() {
			$("#form-menu-tab").addClass("active");
			$("#parameters-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
			$("[rel='tooltip']").tooltip();
			
			<c:if test='${tab == "datasets"}'>
				$('#datasetlink').tab('show');
			</c:if>
		});

		function openScoreCardsTab(targetDatabase) {
			$('#scorecardlink').tab('show');
			saScoresModel.setTargetDataset(targetDatabase);
		}
	
	</script>
	
	<style type="text/css">
		.nav > li > a {
		    padding: 5px 10px;
		}
		
		#sanavtabs > li:not(.active) > a {
			background-color: #337ab7;
		}
	</style>
		 
</head>
<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
		<%@ include file="../menu.jsp" %>		
		<%@ include file="formmenu.jsp" %>			
		
		<div class="fullpageform" style="padding-top: 125px;">		
			
			<div style="margin-left: auto; margin-right: auto; max-width: 1000px;">

			  <!-- Nav tabs -->
			  <ul class="nav nav-tabs" id="sanavtabs" role="tablist" style="font-size: 13px;">
			    <li role="presentation" class="active"><a href="#criteria" id="criterialink" aria-controls="criteria" role="tab" data-toggle="tab"><spring:message code="label.EvaluationCriteria" /></a></li>
			    <li role="presentation"><a href="#dataset" id="datasetlink" aria-controls="dataset" role="tab" data-toggle="tab"><spring:message code="label.TargetDataset" /></a></li>
			    <li role="presentation"><a href="#score" id="scorecardlink" aria-controls="score" role="tab" data-toggle="tab"><spring:message code="label.ScoreCards" /></a></li>
			    <li role="presentation"><a href="#report" aria-controls="report" role="tab" data-toggle="tab"><spring:message code="label.EvaluationReport" /></a></li>
			  </ul>
			
			  <!-- Tab panes -->
			  <div class="tab-content" style="border: 1px solid #ddd; border-top: 0; min-height: 200px; font-size: 13px;">
			    <div role="tabpanel" class="tab-pane active" id="criteria"><%@ include file="parameters-evaluationcriteria.jsp" %></div>
			    <div role="tabpanel" class="tab-pane" id="dataset"><%@ include file="parameters-targetdatasets.jsp" %></div>
			    <div role="tabpanel" class="tab-pane" id="score"><%@ include file="parameters-scorecards.jsp" %></div>
			     <div role="tabpanel" class="tab-pane" id="report"><%@ include file="parameters-report.jsp" %></div>
			  </div>
			
			</div>
				
		</div>
	</div>

<%@ include file="../footer.jsp" %>	

</body>
</html>
