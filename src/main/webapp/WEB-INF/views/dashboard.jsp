<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="esapi"
	uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API"%>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Dashboard" /></title>
	<%@ include file="includes.jsp"%>
	<link href="${contextpath}/resources/css/vis.min.css?version=<%@include file="version.txt" %>" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/form.css?version=<%@include file="version.txt" %>" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/dashboard.css?version=<%@include file="version.txt" %>" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${contextpath}/resources/js/moment.js?version=<%@include file="version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/Chart.min.js?version=<%@include file="version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/vis.min.js?version=<%@include file="version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/dashboard.js?version=<%@include file="version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/dashboard-add.js?version=<%@include file="version.txt" %>"></script>	
	
	<script type="text/javascript">
		var labelpublished = "<spring:message code='label.Published' />";
		var labelunpublished = "<spring:message code='label.Unpublished' />";
		var labelopen = "<spring:message code='form.Open' />";
		var labelsecured = "<spring:message code='form.Secured' />";
		var userid = ${USER.id};
		
		function clearDashboardFilterCellContent(link)
		{
			var cell = $(link).closest('.filtercell');
			$(cell).find(".check").removeAttr("checked");
			$(cell).find("input[type='text']").val("");
			$(cell).find(".datepicker").each(function(){
				removeDateSelection(this);
			});
			$(cell).find(".activityselect").val("");
		}
	</script>
	
	<style type="text/css">
		 .filtertools {
			float: right;
			text-align: right;
			margin-top: 3px;
		}
	</style>
</head>
<body>
	<div class="page-wrap">
		<%@ include file="header.jsp"%>
		<%@ include file="menu.jsp"%>
		<div id="dashboarddiv">
		
			<div class="formmenu">
				<c:if test="${USER.formPrivilege > 0}">
					<div id="surveys-button" data-bind="attr: {class: mode() == 'surveys' ? 'ActiveLinkButton' : 'InactiveLinkButton'}"><span class="glyphicon glyphicon-play"></span><a data-bind="click: switchToSurveys"><spring:message code="label.ManagedSurveys" /></a></div>
				</c:if>
				<div id="invitations-button" data-bind="attr: {class: mode() == 'invitations' ? 'ActiveLinkButton' : 'InactiveLinkButton'}"><span class="glyphicon glyphicon-play"></span><a data-bind="click: switchToInvitations"><spring:message code="label.PersonalInvitations" /></a></div>
				<c:if test="${enabledelphi}">
					<div id="delphi-button" data-bind="attr: {class: mode() == 'delphi' ? 'ActiveLinkButton' : 'InactiveLinkButton'}"><span class="glyphicon glyphicon-play"></span><a data-bind="click: switchToDelphi"><spring:message code="label.DelphiSurveys" /></a></div>
				</c:if>
			</div>		
	
			<div class="fullpage" style="padding-top:137px;">			
			
				<c:if test="${USER.formPrivilege > 0}">
			
					<div id="surveysarea" data-bind="visible: mode() == 'surveys'">	
					
						<div style="text-align: left; margin-left: 15px; margin-bottom: 20px;">
							<span style="margin-right: 10px;"><spring:message code="label.Surveys" />:</span>
														
							<select id="surveystatesselector" class="form-control" style="display: inline; width: auto;" onchange="_dashboard.reloadSurveyWidgets();">
								<option value="my" selected="selected"><spring:message code="label.MySurveys" /></option>
								<option value="shared"><spring:message code="label.SharedWithMe" /></option>
								<option value="all"><spring:message code="label.BothOfThem" /></option>
							</select>	
						</div>	
				
					<c:if test="${USER.formPrivilege > 0 && USER.canCreateSurveys}">
						<div style="text-align: center" data-bind="visible: ((lastEditedSurveyShortname() == null || lastEditedSurveyShortname().length == 0) && (surveys() == null || surveys().length == 0) && (archives() == null || archives().length == 0)) && sharedOnly() == false">
							<a class="btn btn-primary" onclick="showCreateSurveyDialog();"><spring:message code="label.CreateFirstSurvey" /></a>
						</div>
					</c:if>
					
						<div style="text-align: center" data-bind="visible: (lastEditedSurveyShortname() == null || lastEditedSurveyShortname().length == 0) && sharedOnly() == true">
							<spring:message code="info.NoSharedSurveys" />
						</div>
							
						<div class="container-fluid" style="display: none" data-bind="visible: (lastEditedSurveyShortname() != null && lastEditedSurveyShortname().length > 0) || (surveys() != null && surveys().length != 0) || (archives() != null && archives().length != 0)">
														
							<div class="row" style="margin-bottom: 10px;">				
								<div class="col-md-6" >
									<div data-bind="visible: lastEditedByMeSurveyShortname() != null && lastEditedByMeSurveyShortname().length > 0">
										<spring:message code="label.LastEditedSurvey" />:
										
										<!-- ko if: !lastEditedByMeSurveyArchived() && !lastEditedByMeSurveyDeleted()  -->	
											<a data-bind="html: lastEditedByMeSurveyShortname, attr: {href: '${contextpath}/' + lastEditedByMeSurveyShortname() + '/management/overview'}"></a>
											<a data-toggle="tooltip" title="<spring:message code="label.ContinueEditing" />" data-bind="attr: {href: '${contextpath}/' + lastEditedByMeSurveyShortname() + '/management/edit'}"><span style="color: #333" class="glyphicon glyphicon-pencil"></span></a>
										<!-- /ko -->
										<!-- ko if: lastEditedByMeSurveyDeleted() -->	
											<span data-bind="html: lastEditedByMeSurveyShortname"></span>
											<a data-toggle="tooltip" title="<spring:message code="info.CannotEditDeletedSurvey" />"><span style="color: #ccc" class="glyphicon glyphicon-pencil"></span></a>
										<!-- /ko -->
										<!-- ko if: !lastEditedByMeSurveyDeleted() && lastEditedByMeSurveyArchived() -->	
											<span data-bind="html: lastEditedByMeSurveyShortname"></span>
											<a data-toggle="tooltip" title="<spring:message code="info.CannotEditArchivedSurvey" />"><span style="color: #ccc" class="glyphicon glyphicon-pencil"></span></a>
										<!-- /ko -->
										
									</div>
								</div>
								<div class="col-md-6" style="text-align: right" data-bind="visible: latestReplyOn() != null && latestReplyOn().length > 0">
									<spring:message code="label.LatestReplyOn" />:
									<a data-bind="html: latestReplyOn, attr: {href: '${contextpath}/' + latestReplyOn() + '/management/overview'}"></a>
									<a data-toggle="tooltip" title="<spring:message code="label.ViewResults" />" data-bind="attr: {href: '${contextpath}/' + latestReplyOn() + '/management/results'}"><img style="width: 20px" src="${contextpath}/resources/images/icons/24/table.png"></a>							
								</div>
							</div>				
						
		  					<div class="row">				
								<div class="col-md-8">
									<div class="widget">
										<div class="widgettitle" style="padding-bottom: 7px;">
											<div id="contributionssurveys" style="text-align: center;">
												<div style="float: left">
													<spring:message code="label.Contributions" />
												</div>
												
												<div style="float: right; margin-left: 5px;">											
													<select id="contributionsorderselector" class="dashboardselect" onchange="_dashboard.loadContributions(0)">
														<option value="edited" selected="selected"><spring:message code="label.SortByEditDate" /></option>
														<option value="created"><spring:message code="label.SortByCreationDate" /></option>
														<option value="alphabetical"><spring:message code="label.SortAlphabetically" /></option>
													</select>
												</div>
												
												<div style="float: right">											
													<select id="contributionsspanselector" class="dashboardselect" onchange="_dashboard.loadContributions(-1)">
														<option value="week" selected="selected"><spring:message code="label.LastWeek" /></option>
														<option value="month"><spring:message code="label.LastMonth" /></option>
														<option value="all"><spring:message code="label.AllContributions" /></option>
														<option value="total"><spring:message code="label.SincePublication" /></option>
													</select>
												</div>
												
												<a data-bind="click: previousSurvey, attr: {style: surveyIndex() > 0 ? 'color: #fff' : 'color: #bbb'}"><span class="glyphicon glyphicon-arrow-left"></span></a>
												<span id="contributionssurvey"></span>
												<a data-bind="click: nextSurvey, attr: {style: surveyIndex() < maxSurveyIndex() ? 'color: #fff' : 'color: #bbb'}"><span class="glyphicon glyphicon-arrow-right"></span></a>
												
												<div class="widgetheaderhide" data-bind="visible: contributions() == null">
												</div>
											</div>												
										</div>
										<table style="table-layout: fixed; width: 100%">
											<tr>
												<td>
													<img class="center" data-bind="visible: contributions() == null" src="${contextpath}/resources/images/ajax-loader.gif" />
													<canvas class="widgetcanvas" data-bind="visible: maxSurveyIndex() > -1" id="contributionsChart"></canvas>
													<!-- ko if: contributions() != null && maxSurveyIndex() == -1 -->	
														<spring:message code="message.NoResults" />
													<!-- /ko -->
												</td>
												<td style="width: 300px; padding-left: 15px;">
													<div style="height:250px">
														<canvas id="surveyContributionsChart" data-bind="visible: surveyContributionStates() != null"></canvas>
														<!-- ko if: surveyContributionStates() != null && false -->	
															<spring:message code="message.NoResults" />
														<!-- /ko -->
													</div>	
												</td>
											
										</table>
									</div>			
								</div>
								<div class="col-md-4">
									<div class="widget">
										<div class="widgettitle" style="padding-bottom: 7px;">
											<div id="surveys">												
												<spring:message code="label.SurveyStatistics" />
												<div class="widgetheaderhide" data-bind="visible: surveyStates() == null">
												</div>
											</div>												
										</div>	
										<img class="center" data-bind="visible: surveyStates() == null" src="${contextpath}/resources/images/ajax-loader.gif" />
										<div style="height:250px">
											<canvas id="surveysChart" data-bind="visible: surveyStates() != null && (surveyStates()[0] + surveyStates()[1] + surveyStates()[2] + surveyStates()[3]) > 0"></canvas>
											<!-- ko if: surveyStates() != null && (surveyStates()[0] + surveyStates()[1] + surveyStates()[2] + surveyStates()[3]) == 0 -->	
												<spring:message code="message.NoResults" />
											<!-- /ko -->
										</div>	
									</div>			
								</div>					
							</div>
							
							<div class="row" data-bind="visible: endDates() != null && endDates().days.length > 0">
								<div class="col-md-12">
									<div class="widget" style="min-height: auto; height: auto;">
										<div class="widgettitle">
											<spring:message code="label.SurveysEndSoon" />
										</div>
										<img class="center" data-bind="visible: endDates() == null || !endDatesReady()" src="${contextpath}/resources/images/ajax-loader.gif" />
									
										<div id="timelineChart"></div>
									</div>
								</div>
							</div>
							
							<div class="row">
								<div class="col-md-12">
									<div class="widget" style="min-height: auto; height: auto; padding: 0px;">
										<div class="widgettitle" style="text-align: right; margin: 0px;">
											<div style="float: left">
												<spring:message code="label.Surveys" />
											</div>
											
											<!-- ko if: surveysMode() == 'simple' || surveysMode() == 'advanced' -->
											<a id="simplesurveysselector" data-bind="click: function(data, event) { switchSurveyMode('simple'); }, attr: {style: surveysMode() == 'simple' ? 'color: #fff' : ''}"><spring:message code="label.Simple" /></a>
											<a id="advancedsurveysselector" data-bind="click: function(data, event) { switchSurveyMode('advanced'); }, attr: {style: surveysMode() == 'advanced' ? 'color: #fff' : ''}"><spring:message code="label.Advanced" /></a>									
											<!-- /ko -->
											
											<select id="surveytypeselector" class="dashboardselect" onchange="_dashboard.switchSurveyMode($('#surveytypeselector').val())">
												<!-- ko if: surveysMode() == 'simple' || surveysMode() == 'advanced' -->	
												<option value="simple" selected="selected"><spring:message code="label.ExistingSurveys" /></option>
												<option value="archived"><spring:message code="label.ArchivedSurveys" /></option>
												<option value="frozen"><spring:message code="label.FrozenSurveys" /></option>
												<option value="reported"><spring:message code="label.ReportedSurveys" /></option>
												<!-- /ko -->
												
												<!-- ko if: surveysMode() == 'archived' -->	
												<option value="simple"><spring:message code="label.ExistingSurveys" /></option>
												<option value="archived" selected="selected"><spring:message code="label.ArchivedSurveys" /></option>
												<option value="frozen"><spring:message code="label.FrozenSurveys" /></option>
												<option value="reported"><spring:message code="label.ReportedSurveys" /></option>
												<!-- /ko -->
												
												<!-- ko if: surveysMode() == 'reported' -->	
												<option value="simple"><spring:message code="label.ExistingSurveys" /></option>
												<option value="archived"><spring:message code="label.ArchivedSurveys" /></option>
												<option value="frozen"><spring:message code="label.FrozenSurveys" /></option>
												<option value="reported" selected="selected"><spring:message code="label.ReportedSurveys" /></option>
												<!-- /ko -->
												
												<!-- ko if: surveysMode() == 'frozen' -->	
												<option value="simple"><spring:message code="label.ExistingSurveys" /></option>
												<option value="archived"><spring:message code="label.ArchivedSurveys" /></option>
												<option value="frozen" selected="selected"><spring:message code="label.FrozenSurveys" /></option>
												<option value="reported"><spring:message code="label.ReportedSurveys" /></option>
												<!-- /ko -->
											</select>
											
											<div class="widgetheaderhide" style="height: 110px;" data-bind="visible: surveys() == null  && archives() == null">
											</div>
										</div>
										
										<img class="center" data-bind="visible: surveys() == null && archives() == null" src="${contextpath}/resources/images/ajax-loader.gif" />
										
										<div class="table-responsive">
										
											<table class="table table-striped table-bordered" style="margin-bottom: 10px; max-width: 99.9%">
												<tr class="headerrow">
													<!-- ko if: surveysMode() != 'archived' -->
													<th>
														<div style="float: right">
															<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('SURVEYNAME',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('SURVEYNAME',false);" class=""><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
														</div>
														<spring:message code="label.Alias" />
													</th>
													<th>
														<div style="float: right">
															<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('TITLESORT',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('TITLESORT',false);" class=""><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
														</div>
														<spring:message code="label.Title" />
													</th>
													<th>
														<spring:message code="label.Owner" />
													</th>
														
													<th data-bind="visible: surveysMode() == 'advanced'"><spring:message code="label.Status" /></th>
													<th data-bind="visible: surveysMode() == 'advanced'"><spring:message code="label.Security" /></th>
													<th data-bind="visible: surveysMode() == 'advanced'">
														<div style="float: right">
															<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('Survey_Start_Date',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('Survey_Start_Date',false);" class=""><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
														</div>
														<spring:message code="label.StartDate" />											
													</th>
													<th data-bind="visible: surveysMode() == 'advanced'">
														<div style="float: right">
															<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('Survey_End_Date',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('Survey_End_Date',false);" class=""><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
														</div>
														<spring:message code="label.ExpiryDate" />
													</th>
													<th data-bind="visible: surveysMode() == 'advanced'"><spring:message code="label.Invited" /></th>
													<th data-bind="visible: surveysMode() == 'advanced'"><spring:message code="label.Drafts" /></th>
													<th style="min-width: 130px;">
														<div style="float: right">
															<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('Replies',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('Replies',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
														</div>
														<spring:message code="label.Contributions" />											
													</th>
													<th><spring:message code="label.Languages" /></th>
													<th style="min-width: 280px;"><spring:message code="label.Actions" /></th>
													
													<!-- /ko -->
													
													<!-- ko if: surveysMode() == 'archived' -->	
														<th>
															<div style="float: right">
																<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('surveyShortname',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('surveyShortname',false);" class=""><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
															</div>
															<spring:message code="label.Alias" />
														</th>
														<th>
															<div style="float: right">
																<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('surveyTitle',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('surveyTitle',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
															</div>
															<spring:message code="label.Title" />
														</th>
														<th>
															<div style="float: right">
																<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('created',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('created',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
															</div>
															<spring:message code="label.Created" />
														</th>
														<th>
															<div style="float: right">
																<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('archived',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('archived',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
															</div>
															<spring:message code="label.Archived" />
														</th>
														<th style="width:150px">
															<div style="float: right">
																<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('replies',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('replies',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
															</div>
															<spring:message code="label.Contributions" />
														</th>
														<th>PDF</th>
														<th style="min-width: 70px;"><spring:message code="label.Results" /></th>
														<th style="min-width: 70px;"><spring:message code="label.Statistics" /></th>
														<th><spring:message code="label.Actions" /></th>										
													<!-- /ko -->
													
												</tr>
												
												<tr id="surveyfilterrow" class="filterrow">
													<!-- ko if: surveysMode() != 'archived' -->									
												
													<th><input type="text" id="shortname" placeholder="<spring:message code="label.Filter" />" /></th>
													<th><input type="text" id="title" placeholder="<spring:message code="label.Filter" />" /></th>
													<th><input type="text" id="owner" placeholder="<spring:message code="label.Filter" />" /></th>
														
													<th data-bind="visible: surveysMode() == 'advanced'">
														<select id="status">
															<option value=""><spring:message code="label.All" /></option>
															<option value="Published"><spring:message code="label.Published" /></option>
															<option value="Unpublished"><spring:message code="label.Unpublished" /></option>
														</select>
													</th>
													<th data-bind="visible: surveysMode() == 'advanced'">
														<select id="security">
															<option value=""><spring:message code="label.All" /></option>
															<option value="secured"><spring:message code="form.Secured" /></option>
															<option value="open"><spring:message code="form.Open" /></option>
														</select>
													</th>
													<th data-bind="visible: surveysMode() == 'advanced'" class="filtercell cellstart">
														<div class="btn-toolbar" style="margin: 0px; text-align: center">
															
															<div class="filtertools" data-bind="visible: surveyFilterStartFrom().length > 0 || surveyFilterStartTo().length > 0">
																<a data-toggle="tooltip" title="<spring:message code="label.RemoveFilter" />" data-bind="click: function(data, event) {surveyFilterStartFrom(''); surveyFilterStartTo(''); clearDashboardFilterCellContent(this)}">
																	<span class="glyphicon glyphicon-remove-circle black"></span>
																</a>
															</div>
																									
															<div class="datefilter" style="float: left">
															  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
															  	 <span data-bind="html: surveyFilterStartFrom().length > 0 ? surveyFilterStartFrom() : '<spring:message code="label.from" />'"></span>
															     &nbsp;<span class="caret"></span>
															  </a>
															  <div class="overlaymenu hideme">
															  		<spring:message code="label.from" />
															  		<input type="hidden" name="metafilterstartdatefrom" class="hiddendate" data-bind="value: surveyFilterStartFrom" />
															    	<div id="metafilterstartdatefromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
															   </div>
															</div>
															<div class="datefilter" style="float: left">	
															  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
															  	<span data-bind="html: surveyFilterStartTo().length > 0 ? surveyFilterStartTo() : '<spring:message code="label.to" />'"></span>
															  	&nbsp;<span class="caret"></span>
															  </a>
															 <div class="overlaymenu hideme">
															 		<spring:message code="label.to" /> 
															    	<input type="hidden" name="metafilterstartdateto" class="hiddendate" data-bind="value: surveyFilterStartTo" />
															    	<div id="metafilterstartdatetodiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
															    </div>
															</div>	
														</div>											
													</th>
													<th data-bind="visible: surveysMode() == 'advanced'" class="filtercell cellend">
														<div class="btn-toolbar" style="margin: 0px; text-align: center">
														
															<div class="filtertools" data-bind="visible: surveyFilterEndFrom().length > 0 || surveyFilterEndTo().length > 0">
																<a data-toggle="tooltip" title="<spring:message code="label.RemoveFilter" />" data-bind="click: function(data, event) {surveyFilterEndFrom(''); surveyFilterEndTo(''); clearDashboardFilterCellContent(this)}">
																	<span class="glyphicon glyphicon-remove-circle black"></span>
																</a>
															</div>
														
															<div class="datefilter" style="float: left">
															  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
															     <span data-bind="html: surveyFilterEndFrom().length > 0 ? surveyFilterEndFrom() : '<spring:message code="label.from" />'"></span>
															     &nbsp;<span class="caret"></span>
															  </a>
															  <div class="overlaymenu hideme">
															  		<spring:message code="label.from" />
															    	<input type="hidden" name="metafilterenddatefrom" class="hiddendate" data-bind="value: surveyFilterEndFrom" />
															    	<div id="metafilterenddatefromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
															   </div>
															</div>
															<div class="datefilter" style="float: left">	
															  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
															  	<span data-bind="html: surveyFilterEndTo().length > 0 ? surveyFilterEndTo() : '<spring:message code="label.to" />'"></span>
															  	&nbsp;<span class="caret"></span>
															  </a>
															 <div class="overlaymenu hideme">
															 		<spring:message code="label.to" />
															    	<input type="hidden" name="metafilterenddateto" class="hiddendate" data-bind="value: surveyFilterEndTo" />
															    	<div id="metafilterenddatetodiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
															    </div>
															</div>	
														</div>	
													</th>
													<th data-bind="visible: surveysMode() == 'advanced'"></th>
													<th data-bind="visible: surveysMode() == 'advanced'"></th>
													<th></th>
													<th>
														<select id="language">
															<option value=""><spring:message code="label.All" /></option>
															<c:forEach items="${languages}" var="language">	
																<c:if test="${language.official}">		
																	<option value="<esapi:encodeForHTMLAttribute>${language.id}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML><spring:message code="label.lang.${language.englishName}" /></esapi:encodeForHTML></option>
																</c:if>
															</c:forEach>
														</select>
													</th>
													<th style="min-width: 200px;">
														<a class="btn btn-default" onclick="_dashboard.surveysPage(1); _dashboard.loadSurveys(0);"><spring:message code="label.Search" /></a>
														<a class="btn btn-default" onclick="_dashboard.resetSurveys();"><spring:message code="label.Reset" /></a>
													</th>
													
													<!-- /ko -->
													
													<!-- ko if: surveysMode() == 'archived' -->	
														<th><input type="text" id="archiveshortname" placeholder="<spring:message code="label.Filter" />" /></th>
														<th><input type="text" id="archivetitle" placeholder="<spring:message code="label.Filter" />" /></th>
														<th class="filtercell cellcreated">
															<div class="btn-toolbar" style="margin: 0px; text-align: center">
																<div class="filtertools" data-bind="visible: archivedFilterCreatedFrom().length > 0 || archivedFilterCreatedTo().length > 0">
																	<a data-toggle="tooltip" title="<spring:message code="label.RemoveFilter" />" data-bind="click: function(data, event) {archivedFilterCreatedFrom(''); archivedFilterCreatedTo(''); clearDashboardFilterCellContent(this)}">
																		<span class="glyphicon glyphicon-remove-circle black"></span>
																	</a>
																</div>
															
																<div class="datefilter" style="float: left">
																  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
																  	 <span data-bind="html: archivedFilterCreatedFrom().length > 0 ? archivedFilterCreatedFrom() : '<spring:message code="label.from" />'"></span>
																     &nbsp;<span class="caret"></span>
																  </a>
																  <div class="overlaymenu hideme">
																  		<spring:message code="label.from" />
																  		<input type="hidden" name="metafiltercreateddatefrom" class="hiddendate" data-bind="value: archivedFilterCreatedFrom" />
																    	<div id="metafiltercreateddatefromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
																   </div>
																</div>
																<div class="datefilter" style="float: left">	
																  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
																  	 <span data-bind="html: archivedFilterCreatedTo().length > 0 ? archivedFilterCreatedTo() : '<spring:message code="label.to" />'"></span>&nbsp;
																    <span class="caret"></span>
																  </a>
																 <div class="overlaymenu hideme">
																 		<spring:message code="label.to" /> 
																    	<input type="hidden" name="metafiltercreateddateto" class="hiddendate" data-bind="value: archivedFilterCreatedTo" />
																    	<div id="metafiltercreateddatetodiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
																    </div>
																</div>	
															</div>	
														</th>
														<th class="filtercell cellarchived">
															<div class="btn-toolbar" style="margin: 0px; text-align: center">
																<div class="filtertools" data-bind="visible:archivedFilterArchivedFrom().length > 0 || archivedFilterArchivedTo().length > 0">
																	<a data-toggle="tooltip" title="<spring:message code="label.RemoveFilter" />" data-bind="click: function(data, event) {archivedFilterArchivedFrom(''); archivedFilterArchivedTo(''); clearDashboardFilterCellContent(this)}">
																		<span class="glyphicon glyphicon-remove-circle black"></span>
																	</a>
																</div>
															
																<div class="datefilter" style="float: left">
																  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
																  	 <span data-bind="html: archivedFilterArchivedFrom().length > 0 ? archivedFilterArchivedFrom() : '<spring:message code="label.from" />'"></span>
																     &nbsp;<span class="caret"></span>
																  </a>
																  <div class="overlaymenu hideme">
																  		<spring:message code="label.from" />
																  		<input type="hidden" name="metafilterarchiveddatefrom" class="hiddendate" data-bind="value: archivedFilterArchivedFrom" />
																    	<div id="metafilterarchiveddatefromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
																   </div>
																</div>
																<div class="datefilter" style="float: left">	
																  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
																  	 <span data-bind="html: archivedFilterArchivedTo().length > 0 ? archivedFilterArchivedTo() : '<spring:message code="label.to" />'"></span>&nbsp;
																    <span class="caret"></span>
																  </a>
																 <div class="overlaymenu hideme">
																 		<spring:message code="label.to" /> 
																    	<input type="hidden" name="metafilterarchiveddateto" class="hiddendate" data-bind="value: archivedFilterArchivedTo" />
																    	<div id="metafilterarchiveddatetodiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
																    </div>
																</div>	
															</div>	
														</th>
														<th></th>
														<th></th>
														<th></th>
														<th></th>
														<th style="min-width: 150px;">
															<a class="btn btn-default" onclick="_dashboard.surveysPage(1); _dashboard.loadSurveys(0);"><spring:message code="label.Search" /></a>
															<a class="btn btn-default" onclick="_dashboard.resetSurveys();"><spring:message code="label.Reset" /></a>
														</th>									
													<!-- /ko -->
													
												</tr>
												
												<!-- ko if: surveysMode() != 'archived' -->									
													<!-- ko foreach: surveys -->
													<tr>
														<td data-bind="html: shortname"></td>
														<td>
															<a data-bind="html: title.stripHtml115(), attr: {href: '${contextpath}/' + shortname + '/management/overview'}"></a>
														</td>
														<td data-bind="html: owner.name"></td>
														
														<td data-bind="html: isActive ? labelpublished : labelunpublished, visible: $parent.surveysMode() == 'advanced'"></td>
														<td data-bind="html: security.indexOf('secured') == 0 ? labelsecured : labelopen, visible: $parent.surveysMode() == 'advanced'"></td>
														<td data-bind="html: automaticPublishing ? startString : '', visible: $parent.surveysMode() == 'advanced'"></td>
														<td data-bind="html: automaticPublishing ? endString : '', visible: $parent.surveysMode() == 'advanced'"></td>
																
														<td data-bind="visible: $parent.surveysMode() == 'advanced', attr: {id: 'numberinvitations' + uniqueId}">
															<img src="${contextpath}/resources/images/ajax-loader.gif" />
														</td>
														<td data-bind="visible: $parent.surveysMode() == 'advanced', attr: {id: 'numberdrafts' + uniqueId}">
															<img src="${contextpath}/resources/images/ajax-loader.gif" />
														</td>
																						
														<td data-bind="html: numberOfAnswerSetsPublished"></td>
														<td style="word-break: break-all; word-wrap: break-word;">
															<!-- ko foreach: translations -->
															<div data-toggle="tooltip" title="Available" data-bind="html: $data, attr: {class: $data == $parent.language.code ? 'language pivotlanguage' : $parent.completeTranslations.find(el => el.code == $data) != undefined ? 'language' : ' language languageUnpublished', title: $data == $parent.language.code ? '<spring:message code="label.MainLanguage" />' : $parent.completeTranslations.find(el => el.code == $data) != undefined ? '<spring:message code="label.Available" />' : '<spring:message code="label.NotYetAvailable" />'}"></div>
															<!-- /ko -->
														</td>
														<td class="surveyactions">
															<a data-bind="attr: {href: '${contextpath}/' + shortname + '/management/overview'}" class="actionRowAction" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Open" />"><span class="glyphicon glyphicon-folder-open"></span></a>
															
															<!-- ko if: fullFormManagementRights -->
															<a data-bind="attr: {href: '${contextpath}/' + shortname + '/management/edit'}"  rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Edit" />"><span style="color: #333" class="glyphicon glyphicon-pencil"></span></a>
															<!-- /ko -->
															<!-- ko if: !fullFormManagementRights -->
															<a rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Edit" />"><span style="color: #aaa" class="glyphicon glyphicon-pencil disabled"></span></a>
															<!-- /ko -->
															
														<!-- ko if: formManagementRights && canCreateSurveys -->
														<a data-bind="click: function(data, event) { copySurvey(id, title, language.code, 'open', isQuiz == null ? '' : isQuiz.toString(), isDelphi == null ? '' : isDelphi.toString(), isEVote == null ? '' : isEVote.toString(), isSelfAssessment == null ? '' : isSelfAssessment.toString()); }" class="actionRowAction" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Copy" />"><span class="glyphicon glyphicon-copy"></span></a>
														<!-- /ko -->
														
															<!-- ko if: formManagementRights -->
															<a data-bind="attr: {href: '${contextpath}/noform/management/exportSurvey/false/' + shortname}" class="actionRowAction" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Export" />"><span class="glyphicon glyphicon-download-alt"></span></a>
															<!-- /ko -->
															
															<!-- ko if: !formManagementRights -->
															<a class="actionRowAction" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Copy" />"><span class="glyphicon glyphicon-copy" style="color: #ccc"></span></a>
															<a class="actionRowAction" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Export" />"><span class="glyphicon glyphicon-download-alt" style="color: #ccc"></span></a>
															<!-- /ko -->

															<!-- ko if: fullFormManagementRights && numberOfAnswerSetsPublished < 2001 && state != 'Running' -->
															<a data-bind="click: function(data, event) { showDeleteDialog(id); }" class="actionRowAction" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Delete" />"><span class="glyphicon glyphicon-remove"></span></a>
															<!-- /ko -->

															<!-- ko if: !fullFormManagementRights -->
															<a class="actionRowAction disabled" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Delete" />"><span class="glyphicon glyphicon-remove" style="color: #ccc"></span></a>
															<!-- /ko -->

															<!-- ko if: fullFormManagementRights && (numberOfAnswerSetsPublished > 2000 || state == 'Running')  -->
															<a class="actionRowAction disabled" rel="tooltip" data-toggle="tooltip" title="<spring:message code="info.CannotDeleteRunningSurvey" />"><span class="glyphicon glyphicon-remove" style="color: #ccc"></span></a>
															<!-- /ko -->
															
															<!-- ko if: accessResultsRights -->
															<a rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Results" />" data-bind="attr: {href: '${contextpath}/' + shortname + '/management/results'}"><img style="width: 20px; margin-bottom: 3px;" src="${contextpath}/resources/images/icons/24/table.png"></a>							
															<!-- /ko -->
															
															<!-- ko if: !accessResultsRights -->
															<a class="actionRowAction disabled" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Results" />"><img style="width: 20px; margin-bottom: 3px;" src="${contextpath}/resources/images/icons/24/table_grey.png"></a>							
															<!-- /ko -->
														</td>											
													</tr>		
													<!-- /ko -->	
													
													<!-- ko if: surveys() == null || surveys().length == 0 -->	
													<tr>
														<td style="text-align: center" data-bind="attr: {colspan: surveysMode() == 'advanced' ? 12 : 6}">
															<spring:message code="message.NoResults" />
														</td>
													</tr>
													<!-- /ko -->											
												<!-- /ko -->	
												
												<!-- ko if: surveysMode() == 'archived' -->
													<!-- ko foreach: archives -->
													<tr data-bind="attr: {'data-f': finished, 'data-e': error != null && error.length > 0, style: error != null && error.length > 0 ? 'background-color: rgba(255, 96, 96, 0.57)' : (finished ? '' : 'background-color: #FFC6A3') }">	
														<td data-bind="html: surveyShortname"></td>
														<td data-bind="html: surveyTitle.stripHtml115()"></td>
														<td data-bind="html: formattedCreated"></td>
														<td data-bind="html: formattedArchived"></td>
														<td data-bind="html: replies"></td>
														<td>
															<!-- ko if: finished && error == null -->
															<a rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.DownloadPDF" />" target="_blank" data-bind="attr: {href: '${contextpath}/archive/surveypdf/' + id}"><img src="${contextpath}/resources/images/file_extension_pdf_small.png" alt="pdf"></a>
															<!-- /ko -->	
														</td>
														<td>
															<!-- ko if: finished && error == null && replies > 0 -->
															
															<!-- ko if: surveyHasUploadedFiles -->
																<!-- ko if: !hasXlsxResults -->
																	<a rel="tooltip" data-toggle="tooltip" title="<spring:message code="tooltip.DownloadzipXls" />" target="_blank" data-bind="attr: {href: '${contextpath}/archive/resultsxlszip/' + id}"><img src="${contextpath}/resources/images/file_extension_zip_small.png" alt="xls-zip"></a>
																<!-- /ko -->
																<!-- ko if: hasXlsxResults -->
																	<a rel="tooltip" data-toggle="tooltip" title="<spring:message code="tooltip.DownloadzipXlsx" />" target="_blank" data-bind="attr: {href: '${contextpath}/archive/resultsxlsxzip/' + id}"><img src="${contextpath}/resources/images/file_extension_zip_small.png" alt="xlsx-zip"></a>
																<!-- /ko -->
															<!-- /ko -->
															
															<!-- ko if: !surveyHasUploadedFiles -->
																<!-- ko if: !hasXlsxResults -->
																	<a rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.DownloadXLS" />" target="_blank" data-bind="attr: {href: '${contextpath}/archive/resultsxls/' + id}"><img src="${contextpath}/resources/images/file_extension_xls_small.png" alt="xls"></a>
																<!-- /ko -->
																<!-- ko if: hasXlsxResults -->
																	<a rel="tooltip" data-toggle="tooltip" title="<spring:message code="tooltip.Downloadxlsx" />" target="_blank" data-bind="attr: {href: '${contextpath}/archive/resultsxlsx/' + id}"><img src="${contextpath}/resources/images/file_extension_xlsx_small.png" alt="xlsx"></a>
																<!-- /ko -->
															<!-- /ko -->
															
															<!-- /ko -->	
														</td>
														<td>
															<!-- ko if: finished && error == null && replies > 0 -->
																<a rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.DownloadPDF" />" target="_blank" data-bind="attr: {href: '${contextpath}/archive/statspdf/' + id}"><img src="${contextpath}/resources/images/file_extension_pdf_small.png" alt="pdf" style="margin: 0px;"></a>

																<!-- ko if: !hasXlsxResults -->
																	<a rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.DownloadXLS" />" target="_blank" data-bind="attr: {href: '${contextpath}/archive/statsxls/' + id}"><img src="${contextpath}/resources/images/file_extension_xls_small.png" alt="xls" style="margin: 0px;"></a>
																<!-- /ko -->
																<!-- ko if: hasXlsxResults -->
																	<a rel="tooltip" data-toggle="tooltip" title="<spring:message code="tooltip.Downloadxlsx" />" target="_blank" data-bind="attr: {href: '${contextpath}/archive/statsxlsx/' + id}"><img src="${contextpath}/resources/images/file_extension_xlsx_small.png" alt="xlsx" style="margin: 0px;"></a>
																<!-- /ko -->
															<!-- /ko -->
														</td>
														<td>
															<!-- ko if: finished && error == null -->
															<a class="btn btn-primary" data-bind="click: function(data, event) { confirmRestore(id, surveyShortname); }"><spring:message code="label.Restore" /></a>
															<!-- /ko -->
														</td>
													</tr>
													<!-- /ko -->
													<!-- ko if: archives() == null || archives().length == 0 -->	
													<tr>
														<td style="text-align: center" colspan="9">
															<spring:message code="message.NoResults" />
														</td>
													</tr>
													<!-- /ko -->	
												<!-- /ko -->							
											</table>
										</div>
										<div data-bind="visible: surveysMode() == 'archived' ? (archives()!= null && archives().length > 0) : (surveys()!= null && surveys().length > 0)" style="text-align: center; margin-bottom: 10px;">
											<a data-toggle="tooltip" title="<spring:message code="label.GoToFirstPage" />" data-bind="click: firstSurveyPage, attr: {style: surveysPage() > 1 ? '' : 'color: #ccc'}"><span class="glyphicon glyphicon-step-backward"></span></a>
											<a data-toggle="tooltip" title="<spring:message code="label.GoToPreviousPage" />" data-bind="click: previousSurveyPage, attr: {style: surveysPage() > 1 ? '' : 'color: #ccc'}"><span class="glyphicon glyphicon-chevron-left"></span></a>
											
											<span data-bind="html: (surveysPage() - 1) * 10 + 1"></span>&nbsp;
											<spring:message code="label.to" />&nbsp;
											<span data-bind="html: (surveysPage() - 1) * 10 + (surveysMode() == 'archived' ? (archives() == null ? 0 : archives().length) : (surveys() == null ? 0 : surveys().length))"></span>
											
											<a data-toggle="tooltip" title="<spring:message code="label.GoToNextPage" />" data-bind="click: nextSurveyPage, attr: {style: lastSurveysReached() ? 'color: #ccc' : ''}"><span class="glyphicon glyphicon-chevron-right"></span></a>
										</div>								
									</div>						
								</div>
							</div>
						</div>
								
					</div>
				</c:if>
				
				<div id="invitationsarea" style="display: none" data-bind="visible: mode() == 'invitations'">
					<div class="container-fluid">
						<div class="row">
							<div class="col-md-3">
								<div class="widget" style="min-height: auto">
									<div class="widgettitle">
										<spring:message code="label.Statistics" />										
									</div>
									<img class="center" data-bind="visible: contributionStates() == null" src="${contextpath}/resources/images/ajax-loader.gif" />
									<div style="height:150px">
										<canvas style="height:150px" id="personalContributionsChart" data-bind="visible: contributionStates() != null && (contributionStates()[0] + contributionStates()[1] + contributionStates()[2] > 0)"></canvas>
										<!-- ko if: contributionStates() != null && (contributionStates()[0] + contributionStates()[1] + contributionStates()[2] == 0) -->	
											<spring:message code="message.NoResults" />
										<!-- /ko -->
									</div>								
								</div>	
							</div>
							<div class="col-md-9">
								<div class="widget" style="min-height: auto; height: auto; padding: 0px;">
									<div class="widgettitle" style="margin: 0px;">
										<spring:message code="label.OpenInvitations" />
									</div>
									<img class="center" data-bind="visible: personalInvitations() == null" src="${contextpath}/resources/images/ajax-loader.gif" />
							
									<div class="widgetheaderhide" style="height: 110px;" data-bind="visible: personalInvitations() == null">
									</div>
							
									<table class="table table-striped" style="margin-bottom: 10px">
										<tr class="headerrow">
											<th><spring:message code="label.InvitationDate" /></th>
											<th><spring:message code="label.Survey" /></th>
											<th><spring:message code="label.SurveyStatus" /></th>
											<th><spring:message code="label.ExpiryDate" /></th>
											<th><spring:message code="label.InvitationLink" /></th>
										</tr>
										
										<tr id="invitationfilterrow" class="filterrow">
											<th class="filtercell">
												<div class="btn-toolbar" style="margin: 0px; text-align: center">
													<div class="filtertools" data-bind="visible: invitationFilterDateFrom().length > 0 || invitationFilterDateTo().length > 0">
														<a data-toggle="tooltip" title="<spring:message code="label.RemoveFilter" />" data-bind="click: function(data, event) {invitationFilterDateFrom(''); invitationFilterDateTo(''); clearDashboardFilterCellContent(this)}">
															<span class="glyphicon glyphicon-remove-circle black"></span>
														</a>
													</div>
												
													<div class="datefilter" style="float: left">
													  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
													     <span data-bind="html: invitationFilterDateFrom().length > 0 ? invitationFilterDateFrom() : '<spring:message code="label.from" />'"></span>
													     &nbsp;<span class="caret"></span>
													  </a>
													  <div class="overlaymenu hideme">
													  		<spring:message code="label.from" />
													    	<input type="hidden" name="metafilterinvitationdatefrom" class="hiddendate" data-bind="value: invitationFilterDateFrom" />
													    	<div id="metafilterinvitationdatefromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
													   </div>
													</div>
													<div class="datefilter" style="float: left">	
													  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
													  	<span data-bind="html: invitationFilterDateTo().length > 0 ? invitationFilterDateTo() : '<spring:message code="label.to" />'"></span>
													  	&nbsp;<span class="caret"></span>
													  </a>
													 <div class="overlaymenu hideme">
													 		<spring:message code="label.to" />
													    	<input type="hidden" name="metafilterinvitationdateto" class="hiddendate" data-bind="value: invitationFilterDateTo" />
													    	<div id="metafilterinvitationdatetodiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
													    </div>
													</div>	
												</div>	
											</th>
											<th><input type="text" id="persinvitationsurvey" placeholder="<spring:message code="label.Filter" />" /></th>
											<th>
												<select id="persinvitationsurveystatus">
													<option value=""><spring:message code="label.All" /></option>
													<option value="Published"><spring:message code="label.Published" /></option>
													<option value="Unpublished"><spring:message code="label.Unpublished" /></option>
												</select>
											</th>
											<th class="filtercell">
												<div class="btn-toolbar" style="margin: 0px; text-align: center">
													<div class="filtertools" data-bind="visible: invitationFilterEndFrom().length > 0 || invitationFilterEndTo().length > 0">
														<a data-toggle="tooltip" title="<spring:message code="label.RemoveFilter" />" data-bind="click: function(data, event) {invitationFilterEndFrom(''); invitationFilterEndTo(''); clearDashboardFilterCellContent(this)}">
															<span class="glyphicon glyphicon-remove-circle black"></span>
														</a>
													</div>
													
													<div class="datefilter" style="float: left">
													  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
													     <span data-bind="html: invitationFilterEndFrom().length > 0 ? invitationFilterEndFrom() : '<spring:message code="label.from" />'"></span>
													     &nbsp;<span class="caret"></span>
													  </a>
													  <div class="overlaymenu hideme">
													  		<spring:message code="label.from" />
													    	<input type="hidden" name="metafilterinvitationenddatefrom" class="hiddendate" data-bind="value: invitationFilterEndFrom" />
													    	<div id="metafilterinvitationenddatefromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
													   </div>
													</div>
													<div class="datefilter" style="float: left">	
													  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
													  	<span data-bind="html: invitationFilterEndTo().length > 0 ? invitationFilterEndTo() : '<spring:message code="label.to" />'"></span>
													  	&nbsp;<span class="caret"></span>
													  </a>
													 <div class="overlaymenu hideme">
													 		<spring:message code="label.to" />
													    	<input type="hidden" name="metafilterinvitationenddateto" class="hiddendate" data-bind="value: invitationFilterEndTo" />
													    	<div id="metafilterinvitationenddatetodiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
													    </div>
													</div>	
												</div>	
											</th>
											<th style="min-width: 150px;">
												<a class="btn btn-default" onclick="_dashboard.invitationsPage(1); _dashboard.loadPersonalInvitations();"><spring:message code="label.Search" /></a>
												<a class="btn btn-default" onclick="_dashboard.resetInvitations();"><spring:message code="label.Reset" /></a>
											</th>
										</tr>
										
										<!-- ko foreach: personalInvitations -->
										<tr>
											<td data-bind="html: $data[0]"></td>
											<td data-bind="html: $data[1]"></td>
											<td data-bind="html: $data[2] ? '<spring:message code="label.Published" />' : '<spring:message code="label.Unpublished" />'"></td>
											<td data-bind="html: $data[3]"></td>
											<td>
												<a target="_blank" class="btn btn-primary" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Open" />" data-bind="attr: {'href' : $data[4]}"><spring:message code="label.Open" /></a>
											</td>
										</tr>									
										<!-- /ko -->
										
										<!-- ko if: personalInvitations() == null || personalInvitations().length == 0 -->	
											<tr>
												<td style="text-align: center" colspan="5">
													<spring:message code="message.NoResults" />
												</td>
											</tr>
											<!-- /ko -->	
									</table>
									<div data-bind="visible: personalInvitations() != null && personalInvitations().length > 0" style="text-align: center; margin-bottom: 10px;">
										<a data-bind="click: firstInvitationsPage, attr: {style: invitationsPage() > 1 ? '' : 'color: #ccc'}"><span class="glyphicon glyphicon-step-backward"></span></a>
										<a data-bind="click: previousInvitationsPage, attr: {style: invitationsPage() > 1 ? '' : 'color: #ccc'}"><span class="glyphicon glyphicon-chevron-left"></span></a>
										
										<span data-bind="html: (invitationsPage() - 1) * 10 + 1"></span>&nbsp;
										<spring:message code="label.to" />&nbsp;
										<span data-bind="html: (invitationsPage() ) * 10"></span>
										
										<a data-bind="click: nextInvitationsPage, attr: {style: lastInvitationsReached() ? 'color: #ccc' : ''}"><span class="glyphicon glyphicon-chevron-right"></span></a>
									</div>
								</div>			
							</div>
						</div>
						<div class="row">
							<div class="col-md-3">
								
							</div>
								
							<div class="col-md-9">
								<div class="widget" style="min-height: auto; height: auto; padding: 0px;">
									<div class="widgettitle" style="margin: 0px;">
										<spring:message code="label.MyLatestContributions" />
									</div>
									<img class="center" data-bind="visible: personalContributions() == null" src="${contextpath}/resources/images/ajax-loader.gif" />
									<div class="widgetheaderhide" style="height: 110px;" data-bind="visible: personalContributions() == null">
									</div>
									<table class="table table-striped" style="margin-bottom: 10px">
										<tr class="headerrow">
											<th><spring:message code="label.ContributionDate" /></th>
											<th><spring:message code="label.Survey" /></th>
											<th><spring:message code="label.SurveyStatus" /></th>
											<th><spring:message code="label.ExpiryDate" /></th>
											<th><spring:message code="label.Actions" /></th>
										</tr>
										
										<tr id="contributionfilterrow" class="filterrow">
											<th class="filtercell">
												<div class="btn-toolbar" style="margin: 0px; text-align: center">
													<div class="filtertools" data-bind="visible: lastEditFilterFrom().length > 0 || lastEditFilterTo().length > 0">
														<a data-toggle="tooltip" title="<spring:message code="label.RemoveFilter" />" data-bind="click: function(data, event) {lastEditFilterFrom(''); lastEditFilterTo(''); clearDashboardFilterCellContent(this)}">
															<span class="glyphicon glyphicon-remove-circle black"></span>
														</a>
													</div>
													
													<div class="datefilter" style="float: left">
													  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
													     <span data-bind="html: lastEditFilterFrom().length > 0 ? lastEditFilterFrom() : '<spring:message code="label.from" />'"></span>
													     &nbsp;<span class="caret"></span>
													  </a>
													  <div class="overlaymenu hideme">
													  		<spring:message code="label.from" />
													    	<input type="hidden" name="metafilterlasteditdatefrom" class="hiddendate" data-bind="value: lastEditFilterFrom" />
													    	<div id="metafilterlasteditdatefromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
													   </div>
													</div>
													<div class="datefilter" style="float: left">	
													  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
													  	<span data-bind="html: lastEditFilterTo().length > 0 ? lastEditFilterTo() : '<spring:message code="label.to" />'"></span>
													  	&nbsp;<span class="caret"></span>
													  </a>
													 <div class="overlaymenu hideme">
													 		<spring:message code="label.to" />
													    	<input type="hidden" name="metafilterlasteditdateto" class="hiddendate" data-bind="value: lastEditFilterTo" />
													    	<div id="metafilterlasteditdatetodiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
													    </div>
													</div>	
												</div>	
											</th>
											<th><input type="text" id="persconsurvey" placeholder="<spring:message code="label.Filter" />" /></th>
											<th>
												<select id="persconsurveystatus">
													<option value=""><spring:message code="label.All" /></option>
													<option value="Published"><spring:message code="label.Published" /></option>
													<option value="Unpublished"><spring:message code="label.Unpublished" /></option>
												</select>
											</th>
											<th class="filtercell">
												<div class="btn-toolbar" style="margin: 0px; text-align: center">
													<div class="filtertools" data-bind="visible: contributionFilterEndFrom().length > 0 || contributionFilterEndTo().length > 0">
														<a data-toggle="tooltip" title="<spring:message code="label.RemoveFilter" />" data-bind="click: function(data, event) {contributionFilterEndFrom(''); contributionFilterEndTo(''); clearDashboardFilterCellContent(this)}">
															<span class="glyphicon glyphicon-remove-circle black"></span>
														</a>
													</div>
													
													<div class="datefilter" style="float: left">
													  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
													     <span data-bind="html: contributionFilterEndFrom().length > 0 ? contributionFilterEndFrom() : '<spring:message code="label.from" />'"></span>
													     &nbsp;<span class="caret"></span>
													  </a>
													  <div class="overlaymenu hideme">
													  		<spring:message code="label.from" />
													    	<input type="hidden" name="metafiltercontributionenddatefrom" class="hiddendate" data-bind="value: contributionFilterEndFrom" />
													    	<div id="metafiltercontributionenddatefromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
													   </div>
													</div>
													<div class="datefilter" style="float: left">	
													  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
													  	<span data-bind="html: contributionFilterEndTo().length > 0 ? contributionFilterEndTo() : '<spring:message code="label.to" />'"></span>
													  	&nbsp;<span class="caret"></span>
													  </a>
													 <div class="overlaymenu hideme">
													 		<spring:message code="label.to" />
													    	<input type="hidden" name="metafiltercontributionenddateto" class="hiddendate" data-bind="value: contributionFilterEndTo" />
													    	<div id="metafiltercontributionenddatetodiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
													    </div>
													</div>	
												</div>	
											</th>
											<th style="min-width: 150px;">
												<a class="btn btn-default" onclick="_dashboard.contributionsPage(1); _dashboard.loadPersonalContributions(0);"><spring:message code="label.Search" /></a>
												<a class="btn btn-default" onclick="_dashboard.resetContributions();"><spring:message code="label.Reset" /></a>
											</th>
										</tr>
										
										<!-- ko foreach: personalContributions -->
										<tr>
											<td data-bind="html: $data[2]"></td>
											<td data-bind="html: $data[3]"></td>
											<td data-bind="html: $data[4] ? '<spring:message code="label.Published" />' : '<spring:message code="label.Unpublished" />'"></td>
											<td data-bind="html: $data[5]"></td>
											<td>
												<!-- ko if: $data[8] -->
												<a rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.GetPDF" />" data-bind="click: function() {code = $data[1]; showExportDialogAndFocusEmail(this)}, attr: {'data-uid' : $data[1]}"><img src="${contextpath}/resources/images/file_extension_pdf_small.png"></a>
												<!-- /ko -->
												
												<!-- ko if: $data[6] -->
												<a target="_blank" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Edit" />" data-bind="attr: {'href' : '${contextpath}/editcontribution/' + $data[1]}"><span class="glyphicon glyphicon-pencil"></span></a>
												<!-- /ko -->				
											</td>
										</tr>									
										<!-- /ko -->
										
										<!-- ko if: personalContributions() == null || personalContributions().length == 0 -->	
											<tr>
												<td style="text-align: center" colspan="6">
													<spring:message code="message.NoResults" />
												</td>
											</tr>
											<!-- /ko -->	
									</table>
									<div data-bind="visible: personalContributions() != null && personalContributions().length > 0" style="text-align: center; margin-bottom: 10px;">
										<a data-bind="click: firstContributionsPage, attr: {style: contributionsPage() > 1 ? '' : 'color: #ccc'}"><span class="glyphicon glyphicon-step-backward"></span></a>
										<a data-bind="click: previousContributionsPage, attr: {style: contributionsPage() > 1 ? '' : 'color: #ccc'}"><span class="glyphicon glyphicon-chevron-left"></span></a>
										
										<span data-bind="html: (contributionsPage() - 1) * 10 + 1"></span>&nbsp;
										<spring:message code="label.to" />&nbsp;
										<span data-bind="html: (contributionsPage() ) * 10"></span>
										
										<a data-bind="click: nextContributionsPage, attr: {style: lastContributionsReached() ? 'color: #ccc' : ''}"><span class="glyphicon glyphicon-chevron-right"></span></a>
									</div>
								</div>			
							</div>
						</div>
						<div class="row">
							<div class="col-md-3">
							
							</div>
							<div class="col-md-9">
								<div class="widget" style="min-height: auto; height: auto; padding: 0px;">
									<div class="widgettitle" style="margin: 0px;">
										<spring:message code="label.Drafts" />
									</div>
									<img class="center" data-bind="visible: personalDrafts() == null" src="${contextpath}/resources/images/ajax-loader.gif" />
									<div class="widgetheaderhide" style="height: 110px;" data-bind="visible: personalDrafts() == null">
									</div>
									<table class="table table-striped" style="margin-bottom: 10px">
										<tr class="headerrow">
											<th><spring:message code="label.LastEdit" /></th>
											<th><spring:message code="label.Survey" /></th>
											<th><spring:message code="label.SurveyStatus" /></th>
											<th><spring:message code="label.ExpiryDate" /></th>
											<th><spring:message code="label.DraftLink" /></th>
										</tr>
										
										<tr id="draftfilterrow" class="filterrow">
											<th class="filtercell">
												<div class="btn-toolbar" style="margin: 0px; text-align: center">
													<div class="filtertools" data-bind="visible: lastEditDraftFilterFrom().length > 0 || lastEditDraftFilterTo().length > 0">
														<a data-toggle="tooltip" title="<spring:message code="label.RemoveFilter" />" data-bind="click: function(data, event) {lastEditDraftFilterFrom(''); lastEditDraftFilterTo(''); clearDashboardFilterCellContent(this)}">
															<span class="glyphicon glyphicon-remove-circle black"></span>
														</a>
													</div>
													
													<div class="datefilter" style="float: left">
													  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
													     <span data-bind="html: lastEditDraftFilterFrom().length > 0 ? lastEditDraftFilterFrom() : '<spring:message code="label.from" />'"></span>
													     &nbsp;<span class="caret"></span>
													  </a>
													  <div class="overlaymenu hideme">
													  		<spring:message code="label.from" />
													    	<input type="hidden" name="metafilterlasteditdraftdatefrom" class="hiddendate" data-bind="value: lastEditDraftFilterFrom" />
													    	<div id="metafilterlasteditdraftdatefromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
													   </div>
													</div>
													<div class="datefilter" style="float: left">	
													  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
													  	<span data-bind="html: lastEditDraftFilterTo().length > 0 ? lastEditDraftFilterTo() : '<spring:message code="label.to" />'"></span>
													  	&nbsp;<span class="caret"></span>
													  </a>
													 <div class="overlaymenu hideme">
													 		<spring:message code="label.to" />
													    	<input type="hidden" name="metafilterlasteditdraftdateto" class="hiddendate" data-bind="value: lastEditFilterTo" />
													    	<div id="metafilterlasteditdraftdatetodiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
													    </div>
													</div>	
												</div>	
											</th>
											<th><input type="text" id="persdraftsurvey" placeholder="<spring:message code="label.Filter" />" /></th>
											<th>
												<select id="persdraftsurveystatus">
													<option value=""><spring:message code="label.All" /></option>
													<option value="Published"><spring:message code="label.Published" /></option>
													<option value="Unpublished"><spring:message code="label.Unpublished" /></option>
												</select>
											</th>
											<th class="filtercell">
												<div class="btn-toolbar" style="margin: 0px; text-align: center">
													<div class="filtertools" data-bind="visible: draftFilterEndFrom().length > 0 || draftFilterEndTo().length > 0">
														<a data-toggle="tooltip" title="<spring:message code="label.RemoveFilter" />" data-bind="click: function(data, event) {draftFilterEndFrom(''); draftFilterEndTo(''); clearDashboardFilterCellContent(this)}">
															<span class="glyphicon glyphicon-remove-circle black"></span>
														</a>
													</div>
													
													<div class="datefilter" style="float: left">
													  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
													     <span data-bind="html: draftFilterEndFrom().length > 0 ? draftFilterEndFrom() : '<spring:message code="label.from" />'"></span>
													     &nbsp;<span class="caret"></span>
													  </a>
													  <div class="overlaymenu hideme">
													  		<spring:message code="label.from" />
													    	<input type="hidden" name="metafilterdraftenddatefrom" class="hiddendate" data-bind="value: draftFilterEndFrom" />
													    	<div id="metafilterdraftenddatefromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
													   </div>
													</div>
													<div class="datefilter" style="float: left">	
													  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
													  	<span data-bind="html: draftFilterEndTo().length > 0 ? draftFilterEndTo() : '<spring:message code="label.to" />'"></span>
													  	&nbsp;<span class="caret"></span>
													  </a>
													 <div class="overlaymenu hideme">
													 		<spring:message code="label.to" />
													    	<input type="hidden" name="metafilterdraftenddateto" class="hiddendate" data-bind="value: draftFilterEndTo" />
													    	<div id="metafilterdraftenddatetodiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
													    </div>
													</div>	
												</div>	
											</th>
											<th style="min-width: 150px;">
												<a class="btn btn-default" onclick="_dashboard.draftsPage(1); _dashboard.loadPersonalDrafts(0);"><spring:message code="label.Search" /></a>
												<a class="btn btn-default" onclick="_dashboard.resetDrafts();"><spring:message code="label.Reset" /></a>
											</th>
										</tr>
										
										<!-- ko foreach: personalDrafts -->
										<tr>
											<td data-bind="html: $data[2]"></td>
											<td data-bind="html: $data[3]"></td>
											<td data-bind="html: $data[4] ? '<spring:message code="label.Published" />' : '<spring:message code="label.Unpublished" />'"></td>
											<td data-bind="html: $data[5]"></td>
											<td>
												<a target="_blank" class="btn btn-primary" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.OpenDraft" />" data-bind="attr: {'href' : $data[6]}"><spring:message code="label.OpenDraft" /></a>
											</td>
										</tr>									
										<!-- /ko -->
										
										<!-- ko if: personalDrafts() == null || personalDrafts().length == 0 -->	
											<tr>
												<td style="text-align: center" colspan="5">
													<spring:message code="message.NoResults" />
												</td>
											</tr>
											<!-- /ko -->	
									</table>
									<div data-bind="visible: personalDrafts() != null && personalDrafts().length > 0" style="text-align: center; margin-bottom: 10px;">
										<a data-bind="click: firstDraftsPage, attr: {style: draftsPage() > 1 ? '' : 'color: #ccc'}"><span class="glyphicon glyphicon-step-backward"></span></a>
										<a data-bind="click: previousDraftsPage, attr: {style: draftsPage() > 1 ? '' : 'color: #ccc'}"><span class="glyphicon glyphicon-chevron-left"></span></a>
										
										<span data-bind="html: (draftsPage() - 1) * 10 + 1"></span>&nbsp;
										<spring:message code="label.to" />&nbsp;
										<span data-bind="html: (draftsPage() ) * 10"></span>
										
										<a data-bind="click: nextDraftsPage, attr: {style: lastDraftsReached() ? 'color: #ccc' : ''}"><span class="glyphicon glyphicon-chevron-right"></span></a>
									</div>
								</div>			
							</div>
						</div>				
					</div>
				</div>
		
		
				<div id="delphiarea" style="display: none" data-bind="visible: mode() == 'delphi'">
					<div class="widget" style="min-height: auto; height: auto; padding: 0px;">
						<div class="widgettitle" style="margin: 0px;">
							<spring:message code="label.OpenDelphiSurveys" />
						</div>
						<img class="center" data-bind="visible: delphiSurveys() == null" src="${contextpath}/resources/images/ajax-loader.gif" />
							
						<div class="widgetheaderhide" style="height: 110px;" data-bind="visible: delphiSurveys() == null">
						</div>
							
							<table class="table table-striped" style="margin-bottom: 10px">
								<tr class="headerrow">
									<th><spring:message code="label.Title" /></th>
									<th><spring:message code="label.SurveyStatus" /></th>
									<th><spring:message code="label.ExpiryDate" /></th>
									<th></th>
								</tr>
										
								<tr id="delphifilterrow" class="filterrow">
									<th><input type="text" id="delphisurvey" placeholder="<spring:message code="label.Filter" />" /></th>
									<th>
										<select id="delphisurveystatus">
											<option value=""><spring:message code="label.All" /></option>
											<option value="Published"><spring:message code="label.Published" /></option>
											<option value="Unpublished"><spring:message code="label.Unpublished" /></option>
										</select>
									</th>
									<th class="filtercell">
										<div class="btn-toolbar" style="margin: 0px; text-align: center">
											<div class="filtertools" data-bind="visible: delphiFilterEndFrom().length > 0 || delphiFilterEndTo().length > 0">
												<a data-toggle="tooltip" title="<spring:message code="label.RemoveFilter" />" data-bind="click: function(data, event) {delphiFilterEndFrom(''); delphiFilterEndTo(''); clearDashboardFilterCellContent(this)}">
													<span class="glyphicon glyphicon-remove-circle black"></span>
												</a>
											</div>
											
											<div class="datefilter" style="float: left">
											  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
											     <span data-bind="html: delphiFilterEndFrom().length > 0 ? delphiFilterEndFrom() : '<spring:message code="label.from" />'"></span>
											     &nbsp;<span class="caret"></span>
											  </a>
											  <div class="overlaymenu hideme">
											  		<spring:message code="label.from" />
											    	<input type="hidden" name="metafilterdelphienddatefrom" class="hiddendate" data-bind="value: delphiFilterEndFrom" />
											    	<div id="metafilterdelphienddatefromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
											   </div>
											</div>
											<div class="datefilter" style="float: left">	
											  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
											  	<span data-bind="html: delphiFilterEndTo().length > 0 ? delphiFilterEndTo() : '<spring:message code="label.to" />'"></span>
											  	&nbsp;<span class="caret"></span>
											  </a>
											 <div class="overlaymenu hideme">
											 		<spring:message code="label.to" />
											    	<input type="hidden" name="metafilterdelphienddateto" class="hiddendate" data-bind="value: delphiFilterEndTo" />
											    	<div id="metafilterdelphienddatetodiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
											    </div>
											</div>	
										</div>	
									</th>
									<th style="min-width: 150px;">
										<a class="btn btn-default" onclick="_dashboard.delphiPage(1);_dashboard.loadDelphiSurveys();"><spring:message code="label.Search" /></a>
										<a class="btn btn-default" onclick="_dashboard.delphiPage(1);_dashboard.resetDelphiSurveys();"><spring:message code="label.Reset" /></a>
									</th>
								</tr>
										
								<!-- ko foreach: delphiSurveys -->
								<tr>
									<td data-bind="html: $data[0]"></td>
									<td data-bind="html: $data[1] ? '<spring:message code="label.Published" />' : '<spring:message code="label.Unpublished" />'"></td>
									<td data-bind="html: $data[2]"></td>
									<td>
										<a target="_blank" class="btn btn-primary" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Open" />" data-bind="attr: {'href' : $data[3]}"><spring:message code="label.Open" /></a>
									</td>
								</tr>									
								<!-- /ko -->
										
								<!-- ko if: delphiSurveys() == null || delphiSurveys().length == 0 -->	
								<tr>
									<td style="text-align: center" colspan="4">
										<spring:message code="message.NoResults" />
									</td>
								</tr>
								<!-- /ko -->	
							</table>
						<div data-bind="visible: delphiSurveys != null && delphiSurveys > 0" style="text-align: center; margin-bottom: 10px;">
							<a data-bind="click: firstDelphiPage, attr: {style: delphiPage() > 1 ? '' : 'color: #ccc'}"><span class="glyphicon glyphicon-step-backward"></span></a>
							<a data-bind="click: previousDelphiPage, attr: {style: delphiPage() > 1 ? '' : 'color: #ccc'}"><span class="glyphicon glyphicon-chevron-left"></span></a>
							
							<span data-bind="html: (delphiPage() - 1) * 10 + 1"></span>&nbsp;
							<spring:message code="label.to" />&nbsp;
							<span data-bind="html: (delphiPage() ) * 10"></span>
							
							<a data-bind="click: nextDelphiPage, attr: {style: lastDelphiReached() ? 'color: #ccc' : ''}"><span class="glyphicon glyphicon-chevron-right"></span></a>
						</div>
					</div>	
				</div>
		
			</div>
		</div>
	</div>

	<%@ include file="footer.jsp"%>

	<div class="modal" id="confirm-restore-dialog" data-backdrop="static">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<b><spring:message code="label.Restore" /></b>
				</div>
				<div class="modal-body">
					<spring:message code="question.Restore" />
				</div>
				<div class="modal-footer">
					<a id="confirm-restore-dialog-target" 
						onclick="checkAliasExistsForRestore(false); return false;"
						class="btn btn-primary"><spring:message code="label.OK" /></a> <a
						 class="btn btn-default" data-dismiss="modal"><spring:message
							code="label.Cancel" /></a>
				</div>
			</div>
		</div>
	</div>

	<div class="modal" id="choose-alias-dialog" data-backdrop="static">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<b><spring:message code="label.Restore" /></b>
				</div>
				<div class="modal-body">
					<spring:message code="question.RestoreSurveyAlias" />
					<br />
					<br /> <input type="text" id="new-survey-shortname-restore" /><br />
					<spring:message code="message.MeaningfulShortname" />&nbsp;
					<spring:message code="message.MeaningfulShortname2" />
				</div>
				<div class="modal-footer">
					<a  onclick="checkAliasExistsForRestore(true)"
						class="btn btn-primary"><spring:message code="label.OK" /></a> <a
						 class="btn btn-default" data-dismiss="modal"><spring:message
							code="label.Cancel" /></a>
				</div>
			</div>
		</div>
	</div>

	<c:if test="${archived != null}">
		<script type="text/javascript">
			showSuccess("<spring:message code="info.archived" arguments="${archived}" />")
		</script>
	</c:if>
	
	<c:if test="${deleted != null}">
		<script type="text/javascript">
			showSuccess("<spring:message code="info.SurveyFinallyDeleted" />")
		</script>
	</c:if>
	
	<div class="modal fade" id="ask-export-dialog" data-backdrop="static">	
		<div class="modal-dialog">
	    <div class="modal-content">
		<div class="modal-header">
			<b><spring:message code="label.Info" /></b>
		</div>
		<div class="modal-body">
			<p>
				<spring:message code="question.EmailForPDF" />
			</p>
			<input type="text" maxlength="255" name="email" id="email" />
			<span id="ask-export-dialog-error" class="validation-error hideme">
				<spring:message code="message.ProvideEmail" />
			</span>
			<div class="captcha" style="margin-left: 0px; margin-bottom: 20px; margin-top: 20px;">						
				<c:if test="${captchaBypass !=true}">
				<%@ include file="captcha.jsp" %>					
				</c:if>
	       	</div>
	       	<span id="ask-export-dialog-error-captcha" class="validation-error hideme">       		
	       		<c:if test="${captchaBypass !=true}">
	       		<spring:message code="message.captchawrongnew" />
	       		</c:if>
	       	</span>
		</div>
		<div class="modal-footer">
			<a  class="btn btn-primary" onclick="startExport()"><spring:message code="label.OK" /></a>	
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>		
		</div>
		</div>
		</div>
	</div>
	
	<form:form id="load-forms" method="POST" action="${contextpath}/forms" onsubmit="$('#show-wait-image-delete-survey').modal('show');">
		<input type="hidden" name="delete" id="delete" value="" />
		<input type="hidden" name="origin" value="dashboard" />
	</form:form>
	
	<script type="text/javascript">
		var code = null;
		function startExport()
		{
			$("#ask-export-dialog").find(".validation-error").hide();
			
			var mail = $("#email").val();
			if (mail.trim().length == 0 || !validateEmail(mail))
			{
				$("#ask-export-dialog-error").show();
				return;
			};	
					
			<c:choose>
				<c:when test="${!captchaBypass}">
					var challenge = getChallenge();
				    var uresponse = getResponse();

					let data = {email : mail, recaptcha_challenge_field : challenge, 'g-recaptcha-response' : uresponse};
					if ($('#captcha_id').length > 0) {
						data["captcha_id"] =  $('#captcha_id').val();
						data["captcha_useaudio"] =  $('#captcha_useaudio').val();
						data["captcha_original_cookies"] = $('#captcha_original_cookies').val();
					}

					$.ajax({
						type:'GET',
						  url: "${contextpath}/runner/createanswerpdf/" + code,
						  data: data,
						  cache: false,
						  success: function( data ) {
							  
							  if (data == "success") {
									$('#ask-export-dialog').modal('hide');
									showSuccess(message_PublicationExportSuccess2.replace('{0}', mail));
							  	} else if (data == "errorcaptcha") {
							  		$("#ask-export-dialog-error-captcha").show();
							  		reloadCaptcha();
								} else {
									showError(message_PublicationExportFailed);
									reloadCaptcha();
								};
						}
					});							
				</c:when>
				<c:otherwise>			
					$.ajax({				
						type:'GET',
						  url: "${contextpath}/runner/createanswerpdf/" + code,
						  data: {email : mail, recaptcha_challenge_field : '', 'g-recaptcha-response' : ''},
						  cache: false,
						  success: function( data ) {
							  
							  if (data == "success") {
									$('#ask-export-dialog').modal('hide');
									showSuccess(message_PublicationExportSuccess2.replace('{0}', mail));
								} else {
									showError(message_PublicationExportFailed);
									reloadCaptcha();
								};
						}
					});							
				</c:otherwise>
			</c:choose>
		}
		
		function getLabel(s)
		{
			if (s == "Dates") return '<spring:message code="label.Dates" />';			
			if (s == "NumberOfContributions") return '<spring:message code="label.NumberOfContributions" />';			
			if (s == "Contributions") return '<spring:message code="label.Contributions" />';			
			if (s == "Drafts") return '<spring:message code="label.Drafts" />';
			if (s == "OpenInvitations") return '<spring:message code="label.OpenInvitations" />';				
			if (s == "Published") return '<spring:message code="label.Published" />';			
			if (s == "Unpublished") return '<spring:message code="label.Unpublished" />';			
			if (s == "Archived") return '<spring:message code="label.Archived" />';			
			if (s == "PendingChanges") return '<spring:message code="label.PendingChanges" />';
			if (s == "All") return '<spring:message code="label.All" />';
			
			return "unknown label";
		}
	</script>

</body>
</html>
