<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Activity" /></title>
	
	<%@ include file="../includes.jsp" %>
	<script type="text/javascript" src="${contextpath}/resources/js/jquery.stickytableheaders.js?version=<%@include file="../version.txt" %>"></script>
	
	<script type="text/javascript"> 
		$(function() {					
			$("#form-menu-tab").addClass("active");
			$("#activity-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
			
			$(".filtercell").each(function(){
				checkFilterCell(this, true);
			});
			
			$("#tblActivities").stickyTableHeaders({fixedOffset: 165});
			$('[data-toggle="tooltip"]').tooltip(); 
		});
			
		var exportType;
		var exportFormat;
		
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
		           url: '${contextpath}/exports/start/' + exportType + "/" + exportFormat,
		           data: {exportName: name, showShortnames: false, allAnswers: false, group: ""},
		           beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
		           success: function(data)
		           {
		        	   if (data == "success") {
							showExportSuccessMessage();
						} else {
							showExportFailureMessage()
						}
						$('#deletionMessage').addClass('hidden');
		           }
		         });
			
			return false;
		}	
		
	</script>
	
	<style type="text/css">
		#tblActivities select {
			width: auto;
			margin: 0px;
			max-width: 100%;
		}	
		
		.ui-datepicker {
			width: auto;
		}
		
		.datepicker td {
			padding: 1px;
		}
		
		.filtertools {
			float: right;
		}
		
		td {
			word-wrap: break-word;
		}
		
		.fixedtitlebuttons {
			padding-bottom: 17px;
			padding-top: 15px;
			top: 115px;
		}
		
		.RowsPerPage {
			margin-top: -37px;
		}
		
		.fixedtitleform {
			padding-bottom: 5px;
		}
		
	</style>
		
</head>
<body>

	<%@ include file="../header.jsp" %>
	<%@ include file="../menu.jsp" %>
	<%@ include file="formmenu.jsp" %>
	
	<div class="fullpageform100">

		<form:form modelAttribute="paging" id="resultsForm" method="POST" action="${contextpath}/${sessioninfo.shortname}/management/activity" style="margin-top: 0px; margin-bottom: 0px;" onsubmit="$('#show-wait-image').modal('show');">
			<input type="hidden" id="resultsFormMode" name="resultsFormMode" value="" />
						
			<div id="action-bar" class="container action-bar">
				<div class="row">
					<div class="col-md-3">
					</div>
					<div class="col-md-4">
						<input type="submit" class="btn btn-info" value="<spring:message code="label.Search" />" />
						<a onclick="$('#show-wait-image').modal('show');" class="btn btn-default" href="${contextpath}/${sessioninfo.shortname}/management/activity?reset=true"><spring:message code="label.Reset" /></a>
					</div>
					<div class="col-md-5">
						<a style="margin-right: 10px" class="btn btn-default" id="btnConfigureFromResult" onclick="$('#configure-columns-dialog').modal('show')"><spring:message code="label.Configure" /></a>
						<b><spring:message code="label.Export" /></b>
						<a data-toggle="tooltip" title="<spring:message code="tooltip.Downloadcsv" />" id="startExportContentLinkcsv"   onclick="showExportDialog('Activity', 'csv')"><img src="${contextpath}/resources/images/file_extension_csv_small.png" /></a>
						<a data-toggle="tooltip" title="<spring:message code="tooltip.Downloadxls" />" id="startExportContentLinkxls"   onclick="showExportDialog('Activity', 'xls')"><img src="${contextpath}/resources/images/file_extension_xls_small.png" /></a>
						<a data-toggle="tooltip" title="<spring:message code="tooltip.Downloadods" />" id="startExportContentLinkods"  onclick="showExportDialog('Activity', 'ods')"><img src="${contextpath}/resources/images/file_extension_ods_small.png" /></a>
					</div>
				</div>
			</div>
	
			<table id="tblActivities" class="table table-bordered table-striped table-styled" style="margin-top: 40px; max-width: none; table-layout:fixed;">
				<thead>
					<tr style="text-align: center; border-top: 1px solid #ddd;">
						<c:if test='${filter.visible("date") == true}'>
							<c:choose>
								<c:when test="${filter.dateFrom != null || filter.dateTo != null}">
									<th style="width: 260px;"><spring:message code="label.Date" /></th>
								</c:when>
								<c:otherwise>
									<th style="width: 190px;"><spring:message code="label.Date" /></th>
								</c:otherwise>
							</c:choose>						
						</c:if>
						<c:if test='${filter.visible("logid") == true}'><th style="width: 150px;"><spring:message code="label.LogID" /></th></c:if>
						<c:if test='${filter.visible("user") == true}'><th style="width: 150px;"><spring:message code="label.User" /></th></c:if>
						<c:if test='${filter.visible("object") == true}'><th style="width: 150px;"><spring:message code="label.Object" /></th></c:if>
						<c:if test='${filter.visible("property") == true}'><th style="width: 280px;"><spring:message code="label.Property" /></th></c:if>
						<c:if test='${filter.visible("event") == true}'><th style="width: 150px;"><spring:message code="label.Event" /></th></c:if>
						<c:if test='${filter.visible("description") == true}'><th style="width: 150px;"><spring:message code="label.Description" /></th></c:if>
						<c:if test='${filter.visible("oldvalue") == true}'><th style="width: 250px;"><spring:message code="label.OldValue" /></th></c:if>
						<c:if test='${filter.visible("newvalue") == true}'><th style="width: 250px;"><spring:message code="label.NewValue" /></th></c:if>
					</tr>
					<tr class="table-styled-filter">
						<c:if test='${filter.visible("date") == true}'>
							<th class="filtercell">
								<div class="btn-toolbar" style="margin: 0px; text-align: center; float: left;">
									
										<div class="datefilter" style="float: left">
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										  	<c:choose>
										  		<c:when test="${filter.dateFrom != null}">
										  			<spring:eval expression="filter.dateFrom" />
										  		</c:when>
										  		<c:otherwise>
										  			<spring:message code="label.from" />
										  		</c:otherwise>
										  	</c:choose>								    
										    <span class="caret"></span>
										  </a>
										  <div class="overlaymenu hideme">
										    	<input type="hidden" name="metafilteractivitydatefrom" class="hiddendate" value="<spring:eval expression="filter.dateFrom" />" />
										    	<div id="metafilteractivitydatefromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										   </div>
										</div>		
									
										<div class="datefilter" style="float: right">
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										    <c:choose>
										  		<c:when test="${filter.dateTo != null}">
										  			<spring:eval expression="filter.dateTo" />
										  		</c:when>
										  		<c:otherwise>
										  			<spring:message code="label.To" />
										  		</c:otherwise>
										  	</c:choose>	
										    <span class="caret"></span>
										  </a>
										 <div class="overlaymenu hideme">
										    	<input type="hidden" name="metafilteractivitydateto" class="hiddendate" value="<spring:eval expression="filter.dateTo" />" />
										    	<div id="metafilteractivitydatetodiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										    </div>
											
								</div>									
							</th>
						</c:if>
						<c:if test='${filter.visible("logid") == true}'>
							<th class="filtercell">
								<select class="activityselect small-form-control" name="metafilteractivityid" onchange="this.form.submit()">
									<option></option>
									<c:forEach items="${allActivityIds}" var="id">
										<c:choose>
											<c:when test="${filter.logId == id}">
												<option selected="selected" value="${id}">${id}</option>
											</c:when>
											<c:otherwise>
												<option value="${id}">${id}</option>
											</c:otherwise>
										</c:choose>									
									</c:forEach>									
								</select>
							</th>
						</c:if>
						<c:if test='${filter.visible("user") == true}'>
							<th class="filtercell">
								<select class="activityselect small-form-control" name="metafilteractivityuser" onchange="this.form.submit()">
									<option></option>
									<c:forEach items="${allUsers}" var="user">
										<c:choose>
											<c:when test="${filter.userId == user.id}">
												<option selected="selected" value="${user.id}">${user.name}</option>
											</c:when>
											<c:otherwise>
												<option value="${user.id}">${user.name}</option>
											</c:otherwise>
										</c:choose>										
									</c:forEach>									
								</select>
							</th>
						</c:if>
						<c:if test='${filter.visible("object") == true}'>
							<th class="filtercell">
								<select class="activityselect small-form-control" name="metafilteractivityobject" onchange="this.form.submit()">
									<option></option>
									<c:forEach items="${filter.getAllObjects()}" var="object">
										<c:choose>
											<c:when test="${filter.object == object}">
												<option selected="selected" value="${object}"><spring:message code="label.${object}" /></option>
											</c:when>
											<c:otherwise>
												<option value="${object}"><spring:message code="label.${object}" /></option>
											</c:otherwise>
										</c:choose>	
									</c:forEach>
								</select>
							</th>
						</c:if>
						<c:if test='${filter.visible("property") == true}'>
							<th class="filtercell">
								<select class="activityselect small-form-control" name="metafilteractivityproperty" onchange="this.form.submit()">
									<option></option>
									<c:forEach items="${filter.getAllProperties()}" var="property">
										<c:choose>
											<c:when test="${filter.property == property}">
												<option selected="selected" value="${property}"><spring:message code="label.${property}" /></option>
											</c:when>
											<c:otherwise>
												<option value="${property}"><spring:message code="label.${property}" /></option>
											</c:otherwise>
										</c:choose>	
									</c:forEach>			
								</select>
							</th>
						</c:if>
						<c:if test='${filter.visible("event") == true}'>
							<th class="filtercell">
								<select class="activityselect small-form-control" name="metafilteractivityevent" onchange="this.form.submit()">
									<option></option>
									<c:forEach items="${filter.getAllEvents()}" var="event">
										<c:choose>
											<c:when test="${filter.event == event}">
												<option selected="selected" value="${event}"><spring:message code="label.${event}" /></option>
											</c:when>
											<c:otherwise>
												<option value="${event}"><spring:message code="label.${event}"/></option>
											</c:otherwise>
										</c:choose>	
									</c:forEach>				
								</select>
							</th>
						</c:if>
						<c:if test='${filter.visible("description") == true}'>
							<th class="filtercell">
<%-- 								<input onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" type="text"  style="margin:0px;" value='${filter.description}' name="metafilteractivitydescription" /> --%>
							</th>
						</c:if>
						<c:if test='${filter.visible("oldvalue") == true}'>
							<th class="filtercell">
								<input onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" type="text" class="small-form-control" style="margin:0px;" value='${filter.oldValue}' name="metafilteractivityoldvalue" />
							</th>
						</c:if>
						<c:if test='${filter.visible("newvalue") == true}'>
							<th class="filtercell">
								<input onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" type="text" class="small-form-control" style="margin:0px;" value='${filter.newValue}' name="metafilteractivitynewvalue" />
							</th>
						</c:if>
					</tr>
				</thead>
				
				<tbody>
			
				<c:forEach items="${paging.items}" var="activity">
					<tr>
						<c:if test='${filter.visible("date") == true}'><td><spring:eval expression="activity.date" /></td></c:if>
						<c:if test='${filter.visible("logid") == true}'><td>${activity.logID}</td></c:if>
						<c:if test='${filter.visible("user") == true}'>
							<c:choose>
								<c:when test="${activity.logID == 403 && activity.userName.length() == 0}">
									<td><spring:message code="label.Participant" /></td>
								</c:when>
								<c:otherwise>
									<td>${activity.userName}</td>								
								</c:otherwise>
							</c:choose>
						</c:if>
						<c:if test='${filter.visible("object") == true}'><td><spring:message code="label.${activity.getObject()}" /></td></c:if>
						<c:if test='${filter.visible("property") == true}'><td><spring:message code="label.${activity.property}" /></td></c:if>
						<c:if test='${filter.visible("event") == true}'><td><spring:message code="label.${activity.event}" /></td></c:if>
						<c:if test='${filter.visible("description") == true}'><td><spring:message code="logging.${activity.logID}" /></td></c:if>
						<c:if test='${filter.visible("oldvalue") == true}'><td>${activity.oldValue}</td></c:if>
						<c:if test='${filter.visible("newvalue") == true}'><td>${activity.newValue}</td></c:if>					
					</tr>
				</c:forEach>
				
				</tbody>
			
			</table>
			
			<c:set var="pagingElementName" value="Activity" />			
			<%@ include file="../paging.jsp" %>	
			
			<c:if test="${paging.items.size() > 0}">
				<div class="RowsPerPage">
					<span><spring:message code="label.RowsPerPage" />&#160;</span>
				    <form:select onchange="moveTo('${paging.currentPage}')" path="itemsPerPage" id="itemsPerPage" style="margin-top: 0px;" class="middle small-form-control">
						<form:options items="${paging.itemsPerPageOptionsActivity}" />
					</form:select>		
				</div>
			</c:if>
			
			<div class="modal" id="configure-columns-dialog" data-backdrop="static">
				<div class="modal-dialog">
    			<div class="modal-content">
				<div class="modal-header"><spring:message code="message.SelectResultQuestions" /></div>
				<div class="modal-body">
					<table class="table-bordered table-striped table-styled" id="tblConfigurationFromResult">	
						<thead>
							<tr>
								<th><spring:message code="label.Show" /></th>
								<th><spring:message code="label.Export" /></th>
								<th><spring:message code="label.Element" /></th>
							</tr>
						<thead>	
						<tbody>
							<tr>
								<td style="vertical-align: top; text-align: center"><input name="selecteddate" <c:if test='${filter.visible("date")}'>checked="checked"</c:if> type="checkbox" class="check" id="date" /></td>
								<td style="vertical-align: top; text-align: center"><input name="exportselecteddate" <c:if test='${filter.exported("date")}'>checked="checked"</c:if> type="checkbox" class="check" id="exporteddate" /></td>
								<td><spring:message code="label.Date" /></td>
							</tr>
							<tr>
								<td style="vertical-align: top; text-align: center"><input name="selectedlogid" <c:if test='${filter.visible("logid")}'>checked="checked"</c:if> type="checkbox" class="check" id="logid" /></td>
								<td style="vertical-align: top; text-align: center"><input name="exportselectedlogid" <c:if test='${filter.exported("logid")}'>checked="checked"</c:if> type="checkbox" class="check" id="exportedlogid" /></td>
								<td><spring:message code="label.LogID" /></td>
							</tr>
							<tr>
								<td style="vertical-align: top; text-align: center"><input name="selecteduser" <c:if test='${filter.visible("user")}'>checked="checked"</c:if> type="checkbox" class="check" id="user" /></td>
								<td style="vertical-align: top; text-align: center"><input name="exportselecteduser" <c:if test='${filter.exported("user")}'>checked="checked"</c:if> type="checkbox" class="check" id="exporteduser" /></td>
								<td><spring:message code="label.User" /></td>
							</tr>
							<tr>
								<td style="vertical-align: top; text-align: center"><input name="selectedobject" <c:if test='${filter.visible("object")}'>checked="checked"</c:if> type="checkbox" class="check" id="object" /></td>
								<td style="vertical-align: top; text-align: center"><input name="exportselectedobject" <c:if test='${filter.exported("object")}'>checked="checked"</c:if> type="checkbox" class="check" id="exportedobject" /></td>
								<td><spring:message code="label.Object" /></td>
							</tr>
							<tr>
								<td style="vertical-align: top; text-align: center"><input name="selectedproperty" <c:if test='${filter.visible("property")}'>checked="checked"</c:if> type="checkbox" class="check" id="property" /></td>
								<td style="vertical-align: top; text-align: center"><input name="exportselectedproperty" <c:if test='${filter.exported("property")}'>checked="checked"</c:if> type="checkbox" class="check" id="exportedproperty" /></td>
								<td><spring:message code="label.Property" /></td>
							</tr>
							<tr>
								<td style="vertical-align: top; text-align: center"><input name="selectedevent" <c:if test='${filter.visible("event")}'>checked="checked"</c:if> type="checkbox" class="check" id="event" /></td>
								<td style="vertical-align: top; text-align: center"><input name="exportselectedevent" <c:if test='${filter.exported("event")}'>checked="checked"</c:if> type="checkbox" class="check" id="exportedevent" /></td>
								<td><spring:message code="label.Event" /></td>
							</tr>
							<tr>
								<td style="vertical-align: top; text-align: center"><input name="selecteddescription" <c:if test='${filter.visible("description")}'>checked="checked"</c:if> type="checkbox" class="check" id="description" /></td>
								<td style="vertical-align: top; text-align: center"><input name="exportselecteddescription" <c:if test='${filter.exported("description")}'>checked="checked"</c:if> type="checkbox" class="check" id="exporteddescription" /></td>
								<td><spring:message code="label.Description" /></td>
							</tr>
							<tr>
								<td style="vertical-align: top; text-align: center"><input name="selectedoldvalue" <c:if test='${filter.visible("oldvalue")}'>checked="checked"</c:if> type="checkbox" class="check" id="oldvalue" /></td>
								<td style="vertical-align: top; text-align: center"><input name="exportselectedoldvalue" <c:if test='${filter.exported("oldvalue")}'>checked="checked"</c:if> type="checkbox" class="check" id="exportedoldvalue" /></td>
								<td><spring:message code="label.OldValue" /></td>
							</tr>
							<tr>
								<td style="vertical-align: top; text-align: center"><input name="selectednewvalue" <c:if test='${filter.visible("newvalue")}'>checked="checked"</c:if> type="checkbox" class="check" id="newvalue" /></td>
								<td style="vertical-align: top; text-align: center"><input name="exportselectednewvalue" <c:if test='${filter.exported("newvalue")}'>checked="checked"</c:if> type="checkbox" class="check" id="exportednewvalue" /></td>
								<td><spring:message code="label.NewValue" /></td>
							</tr>
						</tbody>
					</table>
				
				</div>
				<div class="modal-footer">
					<a  id="btnOkFromConfigurationResult" onclick="$('#resultsFormMode').val('configure'); $('#configure-columns-dialog').modal('hide'); $('#resultsForm').submit();" class="btn btn-info" ><spring:message code="label.OK" /></a>		
					<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
				</div>	
				</div>
				</div>
			</div>
		</form:form>


	<%@ include file="../footer.jsp" %>	

	</body>
</html>
