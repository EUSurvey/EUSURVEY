<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Overview" /></title>
	
	<%@ include file="../includes.jsp" %>
	
	<link href="${contextpath}/resources/css/management.css" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/form.css" rel="stylesheet" type="text/css" />
	
	<script type="text/javascript"> 
		$(function() {					
			$("#form-menu-tab").addClass("active");
			$("#overview-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
			$('[data-toggle="tooltip"]').tooltip(); 
		});
		
		function showExportDialog(type, format)
		{
			exportType = type;
			exportFormat = format;
			$('#export-name').val("");
			$('#export-name-dialog').find(".validation-error").hide();
			$('#export-name-dialog-type').text(format.toUpperCase());
			$('#export-name-dialog').modal();	
			$('#export-name-dialog').find("input").first().focus();
		}
		
		function startExport(name)
		{
			// check again for new exports
			window.checkExport = true;
			
			$.ajax({
	           type: "POST",
	           url: "${contextpath}/exports/start/" + exportType + "/" + exportFormat,
	           data: {exportName: name, showShortnames: false, allAnswers: false, group: ""},
	           beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
	           success: function(data)	           {
	        	   if (data == "success") {
	        		   	showExportSuccessMessage();
					} else {
						showExportFailureMessage();
					}
					$('#deletionMessage').addClass('hidden');
	           }
	         });			
			
			return false;
		}
			
		function checkShowApplyChangesWaitDialog()
		{
			$("#pending-changes-dialog").modal("hide");
			<c:choose>
				<c:when test="${form.survey.publication.showContent || form.survey.publication.showCharts || form.survey.publication.showStatistics}">
					$("#apply-changes-check-dialog").modal("show");
				</c:when>
				<c:otherwise>
					showApplyChangesWaitDialog();
				</c:otherwise>
			</c:choose>
		}		
		
		function showApplyChangesWaitDialog()
		{
			$("#apply-changes-wait-dialog").modal("show");
			$("#overviewform-target").val("applyChanges");
			$("#overviewform").submit();
		}
		
		function checkPublish()
		{
			<c:choose>
				<c:when test="${!form.survey.automaticPublishing}">
					$("#generic-wait-dialog").modal("show");
					$("#overviewform-target").val("publish");
					$("#overviewform").submit();
				</c:when>
				<c:otherwise>
					$("#overviewform-target").val("publish");
					$('#publishConfirmationDialog').modal("show");
				</c:otherwise>
			</c:choose>	
		}
		
		function checkUnpublish()
		{
			<c:choose>
				<c:when test="${!form.survey.automaticPublishing}">
					$("#generic-wait-dialog").modal("show");
					$("#overviewform-target").val("unpublish");
					$("#overviewform").submit();
				</c:when>
				<c:otherwise>
					$("#overviewform-target").val("unpublish");
					$('#publishConfirmationDialog').modal("show");
				</c:otherwise>
			</c:choose>	
		}
		
		function checkActivate()
		{
			<c:choose>
				<c:when test="${!form.survey.automaticPublishing}">
					$("#generic-wait-dialog").modal("show");
					$("#overviewform-target").val("activate");
					$("#overviewform").submit();
				</c:when>
				<c:otherwise>
					$("#overviewform-target").val("activate");
					$('#publishConfirmationDialog').modal("show");
				</c:otherwise>
			</c:choose>
		}
	</script>
		 
</head>
<body id="bodyOverview">
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
		<%@ include file="../menu.jsp" %>		
		<%@ include file="formmenu.jsp" %>			
		
		<div class="fullpageform">		
			
			<div class="surveybox" style="width: 700px; margin-left: auto; margin-right: auto">
			
				<div id="originaltitle" class="hideme">${form.survey.title}</div>
				
				<c:if test="${form.survey.numberOfReports > 0 }">
					<div class="surveywarning">
						<spring:message code="warning.ReportedSurvey" arguments="${form.survey.numberOfReports}" />
					</div>
				</c:if>
				
				<div class="typeicon">
					<c:choose>
						<c:when test="${form.survey.isQuiz}">
							<img data-toggle="tooltip" title="<spring:message code="label.Quiz" />" style="width: 32px" src="${contextpath}/resources/images/icons/64/quiz.png" />
						</c:when>
						<c:when test="${form.survey.isOPC}">
							<img data-toggle="tooltip" title="<spring:message code="label.OPC" />" src="${contextpath}/resources/images/icons/24/people.png" />
						</c:when>
						<c:when test="${form.survey.isECF}">
							<span data-toggle="tooltip" title="<spring:message code="label.ECF" />"  class="glyphicon glyphicon-user" style="font-size: 24px; color: #333"></span>
						</c:when>
						<c:otherwise>
							<img data-toggle="tooltip" title="<spring:message code="label.StandardSurvey" />" style="width: 32px" src="${contextpath}/resources/images/icons/64/survey.png" />
						</c:otherwise>
					</c:choose>
				</div>
				
				<div class="surveyItemHeader">
					<div style="float: right; margin-left: 20px;">
						<c:choose>
							<c:when test='${form.survey.isActive && form.survey.isPublished}'>
								<div class="publishedsurveytag"><div class="arrow-left"></div><spring:message code="label.Published" /></div>
							</c:when>
							<c:otherwise>
								<div class="unpublishedsurveytag"><div class="arrow-left"></div><spring:message code="label.Unpublished" /></div>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test='${form.survey.hasPendingChanges}'>
								<div class="pendingchangessurveytag"><div class="arrow-left"></div><spring:message code="label.PendingChanges" /></div>
							</c:when>
							<c:otherwise>
							</c:otherwise>
						</c:choose>
					</div>		
			
					<div class="surveytitle" style="overflow: hidden; font-weight: bold; font-size: 200%; line-height: normal; margin-bottom: 10px;">${form.survey.mediumCleanTitle()}</div>
			
					<div style="max-width: 400px;" class="shortname">
						<esapi:encodeForHTML>${form.survey.shortname}</esapi:encodeForHTML>
					</div>
				</div>
							
				<table style="width:100%; margin-top: 20px;" class="overviewtable">		
					<tr>
						<td class="overview-label" style="vertical-align: top;"><spring:message code="label.PublishedSurveyLink" /></td>
						<td colspan="2" style="padding-bottom: 20px;">
							<div class="shortname" style="max-width: 500px;">
								<a id="lnkOverviewAccessSurvey" target="_blank" rel="noopener noreferrer" class="visiblelink" href="${serverprefix}runner/${form.survey.shortname}">${serverprefix}runner/<esapi:encodeForHTML>${form.survey.shortname}</esapi:encodeForHTML></a>
								<a style="font-size: 20px; margin: 10px; position: absolute; margin-top: -2px;" data-toggle="tooltip" title="<spring:message code="label.ShowLinksInAllSurveyLanguages" />" onclick="$('#languageLinkDialog').modal('show');"><span class="glyphicon glyphicon-info-sign"></span></a>
							</div>
						</td>
					</tr>	
					<tr>
						<td class="overview-label" style="vertical-align: top;"><spring:message code="label.Owner" /></td>
						<td>
							${form.survey.owner.getFirstLastName()}
						</td>
						<td rowspan="5" style="vertical-align: top; text-align: right;">
							<c:choose>
								<c:when test="${form.survey.isPublished && form.survey.isActive && (sessioninfo.owner.equals(USER.id) || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1)}">
									<a id="btnOverviewUnpublish" onclick="checkUnpublish();" class="btn btn-primary"><spring:message code="label.Unpublish" /></a>
								</c:when>
								<c:when test="${form.survey.isPublished && form.survey.isActive}">
									<a class="btn disabled btn-default"><spring:message code="label.Unpublish" /></a>
								</c:when>
								<c:when test="${form.survey.isPublished && (sessioninfo.owner.equals(USER.id) || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1)}">
									<a id="btnOverviewPublish" onclick="checkPublish()" class="btn btn-primary"><spring:message code="label.Publish" /></a>
								</c:when>
								<c:when test="${form.survey.isPublished}">
									<a class="btn disabled btn-default"><spring:message code="label.Publish" /></a>
								</c:when>
								<c:when test="${(sessioninfo.owner.equals(USER.id) || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1)}">
									<a id="btnOverviewPublish" onclick="checkActivate();" class="btn btn-primary"><spring:message code="label.Publish" /></a>
								</c:when>
								<c:otherwise>
									<a id="btnOverviewPublish" class="btn btn-primary disabled"><spring:message code="label.Publish" /></a>
								</c:otherwise>
							</c:choose>
							<br /><br />
							<c:choose>
								<c:when test="${form.survey.isPublished && form.survey.hasPendingChanges && (sessioninfo.owner.equals(USER.id) || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1)}">
									<a id="btnOverviewApplyEnabled" onclick="$('#pending-changes-dialog').modal('show');" class="btn btn-default btn-primary"><spring:message code="label.ShowPendingChanges" /></a>
								</c:when>
								<c:otherwise>
									<button id="btnOverviewApplyDisabled" class="btn btn-default disabled"><spring:message code="label.ShowPendingChanges" /></button>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>
						<td class="overview-label"><spring:message code="label.StartsOn" /></td>
						<td>
							<c:choose>
								<c:when test="${form.survey.automaticPublishing && form.survey.start != null}">${form.survey.startString}</c:when>
								<c:otherwise><spring:message code="label.Unset" /></c:otherwise>
							</c:choose>
							&nbsp;
							<c:choose>
								<c:when test="${sessioninfo.owner.equals(USER.id) || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1}">
									<a href="properties?tab=5&editelem=autopub" class="visiblelink" title="<spring:message code="label.EditSettings" />" data-toggle="tooltip"><span class="glyphicon glyphicon-pencil"></span></a>
								</c:when>
								<c:otherwise>
									<a class="visiblelinkdisabled"><spring:message code="label.EditSettings" /></a>								
								</c:otherwise>
							</c:choose>		
						</td>
					</tr>
					<tr>
						<td class="overview-label"><spring:message code="label.EndsOn" /></td>
						<td>
							<c:choose>
								<c:when test="${form.survey.automaticPublishing && form.survey.end != null}">${form.survey.endString}</c:when>
								<c:otherwise><spring:message code="label.Unset" /></c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>
						<td class="overview-label"><spring:message code="label.Answers" /></td>
						<td><esapi:encodeForHTML>${form.survey.numberOfAnswerSetsPublished}</esapi:encodeForHTML></td>
					</tr>
					<tr>
						<td class="overview-label"><spring:message code="label.Results" /></td>
						<td>
							<c:choose>
								<c:when test="${form.survey.publication.showContent || form.survey.publication.showCharts || form.survey.publication.showStatistics}">
									<a class="visiblelink" target="_blank" href="${serverprefix}publication/${form.survey.shortname}"><spring:message code="label.Published" /></a>
								</c:when>
								<c:otherwise>
									<spring:message code="label.Unpublished" />
								</c:otherwise>
							</c:choose>
							&nbsp;
							<c:choose>
								<c:when test="${sessioninfo.owner.equals(USER.id) || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1}">
									<a href="properties?tab=6&editelem=showContent" class="visiblelink" title="<spring:message code="label.EditResultPublication" />" data-toggle="tooltip"><span class="glyphicon glyphicon-pencil"></span></a>
								</c:when>
								<c:otherwise>
									<a class="visiblelinkdisabled"><spring:message code="label.EditResultPublication" /></a>								
								</c:otherwise>
							</c:choose>		
						</td>					
					</tr>
					<tr>
						<td colspan="4" class="surveyactions" style="padding-top: 30px;">
							<c:choose>
								<c:when test="${sessioninfo.owner.equals(USER.id) || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1}">
									<a id="btnEditSurvey" class="actionRowAction" href="<c:url value="/${form.survey.shortname}/management/edit"/>" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Edit" />"><span class="glyphicon glyphicon-pencil"></span></a>
								</c:when>
								<c:otherwise>
									<a rel="tooltip" data-toggle="tooltip" class="disabled actionRowAction" title="<spring:message code="label.Edit" />"><span class="glyphicon glyphicon-pencil" style="color: #ccc"></span></a>
								</c:otherwise>
							</c:choose>	
							
							<c:choose>
								<c:when test="${USER.canCreateSurveys}">
									<a class="actionRowAction" onclick="copySurvey('${form.survey.id}', $('#originaltitle').html(), '${form.survey.language.code}', '${form.survey.security}', '${form.survey.isQuiz}')" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Copy" />"><span class="glyphicon glyphicon-copy"></span></a>
								</c:when>
								<c:otherwise>
									<a class="actionRowAction disabled" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Copy" />"><span class="glyphicon glyphicon-copy disabled"></span></a>
								</c:otherwise>
							</c:choose>
							<a class="actionRowAction" href="<c:url value="/${form.survey.shortname}/management/exportSurvey/false/${form.survey.shortname}"/>" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Export" />"><span class="glyphicon glyphicon-download-alt"></span></a>
									
							<c:if test="${enablearchiving}">
								<c:choose>
									<c:when test="${form.survey.state != 'Running' && (sessioninfo.owner.equals(USER.id) || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1)}">
										<a id="btnArchiveSurvey" class="actionRowAction"  onclick="showArchiveDialog('${form.survey.shortname}','${form.survey.id}', false)" rel="tooltip" data-toggle="tooltip" title="<spring:message code="tooltip.Archive" />"><span class="glyphicon glyphicon-import"></span></a>
									</c:when>
									<c:when test="${!form.survey.fullFormManagementRights}">
										<a id="btnArchiveSurvey" class="disabled actionRowAction" rel="tooltip" data-toggle="tooltip" title="<spring:message code="tooltip.Archive" />"><span class="glyphicon glyphicon-import" style="color: #ccc"></span></a>
									</c:when>
									<c:otherwise>
										<a id="btnArchiveSurvey" class="disabled actionRowAction" rel="tooltip" data-toggle="tooltip" title="<spring:message code="tooltip.ArchiveDisabled" />"><span class="glyphicon glyphicon-import" style="color: #ccc"></span></a>
									</c:otherwise>
								</c:choose>				
							</c:if>
														
							<c:choose>
								<c:when test="${form.survey.state != 'Running' && (sessioninfo.owner.equals(USER.id) || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1)}">
									<a class="actionRowAction" id="deleteSurveyButtonOverview" onclick="showDeleteDialog('${form.survey.id}');" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Delete" />" ><span class="glyphicon glyphicon-remove"></span></a>
								</c:when>
								<c:when test="${form.survey.state != 'Running'}">
									<a id="notRunningDeleteSurveyButtonOverview" class="disabled actionRowAction" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Delete" />"><span class="glyphicon glyphicon-remove" style="color: #ccc;"></span></a>
								</c:when>
								<c:otherwise>
									<a id="cannotDeleteSurveyButtonOverview" class="disabled actionRowAction" rel="tooltip" data-toggle="tooltip" title="<spring:message code="info.CannotDeleteRunningSurvey" />"><span class="glyphicon glyphicon-remove" style="color: #ccc;"></span></a>
								</c:otherwise>
							</c:choose>	
						</td>
					</tr>
	
				</table>
				
			</div>		
				
		</div>
	</div>

	<form:form id="overviewform" method="POST">
		<input type="hidden" name="target" id="overviewform-target" value="" />
	</form:form>

<%@ include file="../footer.jsp" %>	

<div class="modal" id="languageLinkDialog" data-backdrop="static">
	<div class="modal-dialog modal-lg">
    <div class="modal-content">
	<div class="modal-header"><spring:message code="label.SurveyLinks" /></div>
	<div class="modal-body" style="max-height: 500px; overflow: auto; word-break: break-all;">	
		<div style="color: #777; margin-bottom: 20px;"><spring:message code="info.SurveyLinks" /></div>
		
		<table class="table table-bordered table-striped">
			<tr>
				<th><spring:message code="label.Language" /></th>
				<th><spring:message code="label.SurveyLink" /></th>
			</tr>
			
			<tr>
				<td>${form.survey.language.code}</td>
				<td>
					<a target="_blank" rel="noopener noreferrer" class="visiblelink" href="${serverprefix}runner/${form.survey.shortname}">${serverprefix}runner/${form.survey.shortname}</a>
				</td>
			</tr>
			
			<c:forEach items="${form.survey.translations}" var="lang">
				<c:if test="${form.survey.language.code != lang}">
		 			<tr>
		 				<td>${lang}</td>
			 			<td>
							<a target="_blank" rel="noopener noreferrer" class="visiblelink" href="${serverprefix}runner/${form.survey.shortname}?surveylanguage=${lang}">${serverprefix}runner/${form.survey.shortname}?surveylanguage=${lang}</a>
						</td>
					</tr>
		 		</c:if>
	 		</c:forEach>			
			
		</table>
	</div>
	<div class="modal-footer">
		<a class="btn btn-default" data-dismiss="modal"><spring:message code="label.Close" /></a>
	</div>
	</div>
	</div>
</div>


<div class="modal" id="pending-changes-dialog" data-backdrop="static">
	<div class="modal-dialog modal-lg">
    <div class="modal-content">
	<div class="modal-header"><spring:message code="label.PendingChanges" /></div>
	<div class="modal-body" style="max-height: 500px; overflow: auto; word-break: break-all;">	
	 	<table class="table table-bordered">
	 		<thead>
	 			<tr>
	 				<th><spring:message code="label.Element" /></th>
					<th style="min-width: 100px"><spring:message code="label.Type" /></th>
	 			</tr>
	 		</thead>
	 		<tbody>
	 			<c:forEach items="${newElements}" var="element">
	 				<tr>
	 					<td><esapi:encodeForHTML>${element.nameOrType}</esapi:encodeForHTML></td>
	 					<td><spring:message code="label.New" /></td>
	 				</tr>
	 			</c:forEach>
	 			<c:forEach items="${changedElements}" var="element">
	 				<tr>
	 					<td>
	 						<c:choose>
	 							<c:when test="${element.nameOrType == 'PropertiesElement' && element.orderChanged}">
	 								<spring:message code="label.ElementOrder" />
	 							</c:when>
	 							<c:when test="${element.nameOrType == 'PropertiesElement'}">
	 								<spring:message code="label.Properties" />
	 							</c:when>
	 							<c:when test="${element.nameOrType == 'TranslationsElement'}">
	 								<spring:message code="label.Translations" />
	 							</c:when>
	 							<c:otherwise>
	 								<esapi:encodeForHTML>${element.nameOrType}</esapi:encodeForHTML>
	 							</c:otherwise>
	 						</c:choose>
	 					</td>
	 					<td><spring:message code="label.Changed" /></td>
	 				</tr>
	 			</c:forEach>
	 			<c:forEach items="${deletedElements}" var="element">
	 				<tr>
	 					<td><esapi:encodeForHTML>${element.nameOrType}</esapi:encodeForHTML></td>
	 					<td><spring:message code="label.Deleted" /></td>
	 				</tr>
	 			</c:forEach>
	 		</tbody>
	 	</table>
	</div>
	<div class="modal-footer">
		<c:if test="${isPublished && (sessioninfo.owner.equals(USER.id) || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1)}">
			<a id="btnClearPendingFromOverview"  class="btn btn-default" onclick="$('#pending-changes-dialog').modal('hide');$('#confirm-clear-changes-dialog').modal('show');"><spring:message code="label.UndoChanges" /></a>	
		</c:if>
		<a  id ="btnApplyChangesFromOverview" class="btn btn-default btn-primary" onclick="checkShowApplyChangesWaitDialog();"><spring:message code="label.ApplyChanges" /></a>
		<a  id ="btnCancelChangesFromOverview" class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>
	</div>
	</div>
	</div>
</div>

<div class="modal" id="confirm-clear-changes-dialog" data-backdrop="static">
	<div class="modal-dialog modal-sm">
    <div class="modal-content">
	<div class="modal-header"><spring:message code="label.PendingChanges" /></div>
	<div class="modal-body">	
	 	<spring:message code="question.PendingChanges" />
	</div>
	<div class="modal-footer">
		<a id="btnConfirmClearChangesFromOverview" onclick="$('#confirm-clear-changes-dialog').modal('hide');$('#clear-changes-wait-dialog').modal('show');$('#overviewform-target').val('clearchanges');$('#overviewform').submit();" class="btn btn-primary"><spring:message code="label.Yes" /></a>	
		<a id="btnCancelClearChangesFromOverview"  class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>	
	</div>
	</div>
	</div>
</div>

<div id="publishConfirmationDialog" class="modal" role="dialog" aria-hidden="true">
	<div class="modal-dialog">
    <div class="modal-content">
	<div class="modal-header">
		<a  class="close" data-dismiss="modal" onclick="publishConfirmationClose();">&times;</a>
		<h3><spring:message code="label.Confirmation" /></h3>
	</div>
	<div class="modal-body">
		<div class="divDialogElements"><spring:message code="message.ManualIntervention" /></div>
	</div>
	<div class="modal-footer">
		<a id="confirmationTarget" onclick="$('#overviewform').submit();"  class="btn btn-primary" ><spring:message code="label.Proceed" /></a>
		<a  class="btn btn-default" onclick="$(this).closest('.modal').modal('hide');"><spring:message code="label.Cancel" /></a>
	</div>
	</div>
	</div>	
</div>

<div class="modal" id="apply-changes-wait-dialog" data-backdrop="static">
	<div class="modal-dialog modal-sm">
    <div class="modal-content">
	<div class="modal-header"><spring:message code="label.ApplyChanges" /></div>
	<div class="modal-body">	
	 	<spring:message code="info.ApplyChanges" />
	</div>
	</div>
	</div>
</div>

<div class="modal" id="clear-changes-wait-dialog" data-backdrop="static">
	<div class="modal-dialog modal-sm">
    <div class="modal-content">
	<div class="modal-header"><spring:message code="label.ClearChanges" /></div>
	<div class="modal-body">	
	 	<spring:message code="info.ClearChanges" />
	</div>
	</div>
	</div>
</div>

<div class="modal" id="apply-changes-check-dialog" data-backdrop="static">
	<div class="modal-dialog modal-sm">
    <div class="modal-content">
	<div class="modal-header"><spring:message code="label.ApplyChanges" /></div>
	<div class="modal-body">	
	 	<spring:message code="question.CheckApplyChanges" />
	</div>
	<div class="modal-footer">
		<a  class="btn btn-primary" onclick="showApplyChangesWaitDialog();" ><spring:message code="label.Yes" /></a>
		<a  class="btn btn-default" onclick="$(this).closest('.modal').modal('hide');"><spring:message code="label.No" /></a>
	</div>
	</div>
	</div>
</div>

<form:form id="load-forms" method="POST" action="${contextpath}/forms" onsubmit="$('#show-wait-image-delete-survey').modal('show');">
	<input type="hidden" name="delete" id="delete" value="" />
</form:form>

</body>
</html>
