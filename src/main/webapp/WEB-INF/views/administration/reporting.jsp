<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.ec.survey.model.administration.GlobalPrivilege" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Administration" /></title>
	<%@ include file="../includes.jsp" %>
	<script type="text/javascript" src="${contextpath}/resources/js/jquery.stickytableheaders.js?version=<%@include file="../version.txt" %>"></script>
	
	<script type="text/javascript"> 
		$(document).ready(function(){
			$("#administration-menu-tab").addClass("active");
			$("#reporting-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
			
			$('[data-toggle="tooltip"]').tooltip();
			
			loadMore();
		    $("#tbllist-todos").stickyTableHeaders({fixedOffset: 343});
		    $(window).trigger('resize.stickyTableHeaders');
		   
		    $('#checkAllCheckBox').click(function () {
		    	$('input[type="checkbox"]').not(":disabled").prop('checked', this.checked);
		    });
		})
		
		var infinitePage = 0;
		
		$(window).scroll(function() {
			    if ($(window).scrollTop() <= $(document).height() - $(window).height() && $(window).scrollTop() >= $(document).height() - $(window).height() - 10) {
			    	loadMore();
			  }
			 });
		
		var loadingmore = false;
		var endReached = false;
		function loadMore()
		{
			if (loadingmore || endReached) return;
			
			loadingmore = true;
			
			$( "#wheel" ).show();
			var s = "page=" + infinitePage++ + "&rows=50";	
			
			$.ajax({
				type:'GET',
				url: "${contextpath}/administration/todosjson",
				dataType: 'json',
				data: s,
				cache: false,
				success: refreshTodos
				});
		}

		
		function refreshTodos( list, textStatus, xhr ) {
			if (list.length == 0)
			{
				endReached = true;
				$("#load-more-div").hide();
				$("#wheel").hide();
				$("#todos-loading").hide();
			}
			
			refreshTodosBasic( list, textStatus, xhr );
			$(window).trigger('resize.stickyTableHeaders');
			loadingmore = false;
		}
			
		function refreshTodosBasic( list, textStatus, xhr ) {
						
			for (var i = 0; i < list.length; i++ )
			  {
				var row = document.createElement("tr");
				
				$(row).attr({'id': 'Row' + list[i].Id});
				
				$(row).append('<td>' + list[i].Id + '</td>')
				$(row).append('<td>' + list[i].Type + '</td>')
				$(row).append('<td>' + list[i].UID + '</td>')
				$(row).append('<td>' + (list[i].Code != null ? list[i].Code : '') + '</td>')
				
				td = document.createElement("td");
				
				$(td).append('<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Delete" />" onclick="deleteToDo(' + list[i].Id +')" class="iconbutton"><span class="glyphicon glyphicon-remove"></span></a>');									
				$(td).append('<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Execute" />" onclick="execute(' + list[i].Id +')" class="iconbutton"><span class="glyphicon glyphicon-ok"></span></a>');	
				
				$(row).append(td);

				$('#tbllist-todos tbody').append(row);
			  }
			  
			  if($('#tbllist-todos tbody tr').size()==0)
				{
					$('#tbllist-empty').show();
				}
			  else
				  {
				  	$('#tbllist-empty').hide();
				  }
			
			  $( "#wheel" ).hide();
			  $( "#todos-loading" ).hide();
			  
			  $('[data-toggle="tooltip"]').tooltip(); 
		}
		
		function execute(id) {
			var request = $.ajax({
				  url: contextpath + "/administration/executeToDo/" + id,
				  cache: false,
				  dataType: "json",
				  success: function(data)
				  {
					  if (!data.success)
					  {
						showError("<spring:message code="error.ExecutionFailed" />");	
					  } else {
						showInfo("<spring:message code="info.ExecutionSucceeded" />");	
						$('#Row' + id).remove();
					  }
				  }
				});
		}
		
		function deleteToDo(id) {
			var request = $.ajax({
				  url: contextpath + "/administration/deleteToDo/" + id,
				  cache: false,
				  dataType: "json",
				  success: function(data)
				  {
					  if (!data.success)
					  {
						showError("<spring:message code="error.DeletionFailed" />");		
					  } else {
						showInfo("<spring:message code="info.DeletionSucceeded" />");
						$('#tbllist-todos tbody').empty();
						infinitePage = 0;
						loadMore();
					  }
				  }
				});
		}
		
		function recreateAllOLAPTables()
		{
			$('#recreateDialog').modal('hide');
			
			var request = $.ajax({
				  url: contextpath + "/administration/recreateAllOLAPTables",
				  cache: false,
				  success: function(data)
				  {
					  if (data != "started")
					  {
						showError(data);		
					  } else {
						showInfo("<spring:message code="info.OperationStarted" />");						
					  }
				  }
				});
		}
		
		function createAllOLAPTables()
		{
			$('#createDialog').modal('hide');
			
			var request = $.ajax({
				  url: contextpath + "/administration/createAllOLAPTables",
				  cache: false,
				  success: function(data)
				  {
					  if (data != "started")
					  {
						showError(data);		
					  } else {
						showInfo("<spring:message code="info.OperationStarted" />");						
					  }
				  }
				});
		}
		
		function updateAllOLAPTables()
		{
			$('#updateDialog').modal('hide');
			
			var request = $.ajax({
				  url: contextpath + "/administration/updateAllOLAPTables",
				  cache: false,
				  success: function(data)
				  {
					  if (data != "started")
					  {
						showError(data);		
					  } else {
						showInfo("<spring:message code="info.OperationStarted" />");						
					  }
				  }
				});
		}
		
		function createOLAPTable()
		{
			var uid = $('#uid').val();
			if (uid.length == 0)
			{
				$('#emptyUID').modal('show');
				return;
			}
			
			var request = $.ajax({
				  url: contextpath + "/administration/createOLAPTable/" + uid,
				  cache: false,
				  success: function(data)
				  {
					  if (data != "executed")
					  {
						showError(data);		
					  } else {
						showInfo("<spring:message code="info.OperationExecuted" />");						
					  }
				  }
				});
		}
		
		function updateOLAPTable()
		{
			var uid = $('#uid').val();
			if (uid.length == 0)
			{
				$('#emptyUID').modal('show');
				return;
			}
			
			var request = $.ajax({
				  url: contextpath + "/administration/updateOLAPTable/" + uid,
				  cache: false,
				  success: function(data)
				  {
					  if (data != "executed")
					  {
						showError(data);		
					  } else {
						showInfo("<spring:message code="info.OperationExecuted" />");						
					  }
				  }
				});
		}
		
		function recreateOLAPTable()
		{
			var uid = $('#uid').val();
			if (uid.length == 0)
			{
				$('#emptyUID').modal('show');
				return;
			}
			
			var request = $.ajax({
				  url: contextpath + "/administration/recreateOLAPTable/" + uid,
				  cache: false,
				  success: function(data)
				  {
					  if (data != "executed")
					  {
						showError(data);		
					  } else {
						showInfo("<spring:message code="info.OperationExecuted" />");						
					  }
				  }
				});
		}
		
	</script>		
</head>
<body>
	<div class="page-wrap">
	<%@ include file="../header.jsp" %>
	<%@ include file="../menu.jsp" %>
	<%@ include file="adminmenu.jsp" %>	
	
	<div class="fixedtitleform">
		<div class="fixedtitleinner" style="padding-bottom: 35px;">
			<div id="action-bar">
				<div class="row">
					<div class="col-md-12" style="text-align:center; margin-top: 20px;">
						<c:if test="${USER.getGlobalPrivilegeValue('SystemManagement') > 1}">
							<a onclick="$('#recreateDialog').modal('show')" data-toggle="tooltip" data-placement="bottom" rel=tooltip" title="recreate all tables" class="btn btn-default">recreateAllOLAPTables</a>
							<a onclick="$('#createDialog').modal('show')" data-toggle="tooltip" data-placement="bottom" rel=tooltip" title="create all tables" class="btn btn-default">createAllOLAPTables</a>
							<a onclick="$('#updateDialog').modal('show')" data-toggle="tooltip" data-placement="bottom" rel=tooltip" title="update all tables" class="btn btn-default">updateAllOLAPTables</a><br /><br />
							<input class="form-control" placeholder="uid or shortname" style="display: inline-block; margin-top: 2px;" type="text" id="uid" />
							<a onclick="createOLAPTable();" data-toggle="tooltip" data-placement="bottom" rel=tooltip" title="create table" class="btn btn-default" style="margin-left: 10px; margin-top: -2px;">createOLAPTable</a>
							<a onclick="updateOLAPTable();" data-toggle="tooltip" data-placement="bottom" rel=tooltip" title="update table" class="btn btn-default" style="margin-top: -2px;">updateOLAPTable</a>
							<a onclick="recreateOLAPTable();" data-toggle="tooltip" data-placement="bottom" rel=tooltip" title="update table" class="btn btn-default" style="margin-top: -2px;">recreateOLAPTable</a>
							<br /><br />
						</c:if>
							
						<form:form id="form" method="POST" action="reporting" style="margin-bottom: 10px">
							<spring:message code="label.MigrationEnabled"/>:
							<c:choose>
								<c:when test="${enabled}">
									<input class="check" type="checkbox" value="true" checked="checked" name="enabled" />
								</c:when>
								<c:otherwise>
									<input class="check" type="checkbox" value="true" name="enabled" />
								</c:otherwise>
							</c:choose>&nbsp;&nbsp;&nbsp;
							<spring:message code="label.Start"/>:
							<input type="text" name="start" class="form-control" placeholder="hh:mm" value="${start}" style="display: inline-block; margin-top: 2px; width: 70px" />
							&nbsp;&nbsp;&nbsp;
							<spring:message code="label.Time"/>:
							<input type="text" name="time" class="form-control" placeholder="min" value="${time}" style="display: inline-block; margin-top: 2px; width: 50px" />
							&nbsp;&nbsp;&nbsp;
							<input type="submit" class="btn btn-default" style="margin-top: -3px;" value="<spring:message code="label.Apply"/>" />
							&nbsp;&nbsp;&nbsp;
							<spring:message code="label.CurrentSurvey"/>: ${survey}
						</form:form>
													
						<b># <spring:message code="label.TODOs"/></b>: <span style="margin-right: 10px">${totaltodos}</span>
						<b># <spring:message code="label.Tables"/> (<spring:message code="label.existing"/>)</b>: <span style="margin-right: 10px">${totaltables}</span>
						<b># <spring:message code="label.Tables"/> (<spring:message code="label.expected"/>)</b>: <span style="margin-right: 10px">${totalpublishedsurveys + totalpublishedsurveys + totaldraftsurveys}</span>
						<b># <spring:message code="label.Surveys"/> (<spring:message code="label.neverpublished"/>)</b>: <span>${totaldraftsurveys}</span>
						<b># <spring:message code="label.Surveys"/> (<spring:message code="label.others"/>)</b>: <span> ${totalpublishedsurveys}</span>
					</div>					
				</div>
			</div>
		</div>
	</div>
	
		<div class="page1024" style="width: 1200px; padding-bottom: 0px;overflow-x: visible;">
		<table id="tbllist-todos" class="table table-bordered table-styled" style="width: 1024px; margin-left: auto; margin-right: auto; margin-top: 350px;">
			<thead style="background-color: white; border-top: 1px solid #eee;">
				<tr>
					<th>ID</th>
					<th>Type</th>
					<th>UID</th>
					<th>Code</th>
					<th><spring:message code="label.Actions" /></th>
				</tr>
			</thead>
			<tbody>
				<tr id="todos-loading">
					<td colspan="8"  style="text-align: center">
						<img src="${contextpath}/resources/images/ajax-loader.gif" />
					</td>
				</tr>
			</tbody>
		</table>
		
		<div style="text-align: center">
			<img id="wheel" class="hideme" src="${contextpath}/resources/images/ajax-loader.gif" />
		</div>
		<div id="tbllist-empty" class="noDataPlaceHolder">
			<p>
				<spring:message code="label.NoData"/>&nbsp;<img src="${contextpath}/resources/images/icons/32/forbidden_grey.png" alt="no data"/>
			<p>
		</div>
	</div>
	</div>
	
	<%@ include file="../footer.jsp" %>	
	
	<div class="modal" id="recreateDialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
		<div class="modal-body">
			<spring:message code="question.RecreateReportingTables" />
		</div>
		<div class="modal-footer">
			<img id="wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a onclick="recreateAllOLAPTables();" class="btn btn-primary"><spring:message code="label.Yes" /></a>	
			<a class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>											
		</div>
		</div>
		</div>
	</div>
	
	<div class="modal" id="createDialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
		<div class="modal-body">
			<spring:message code="question.CreateReportingTables" />
		</div>
		<div class="modal-footer">
			<img id="wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a onclick="createAllOLAPTables();" class="btn btn-primary"><spring:message code="label.Yes" /></a>	
			<a class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>											
		</div>
		</div>
		</div>
	</div>
	
	<div class="modal" id="updateDialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
		<div class="modal-body">
			<spring:message code="question.UpdateReportingTables" />
		</div>
		<div class="modal-footer">
			<img id="wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a onclick="updateAllOLAPTables();" class="btn btn-primary"><spring:message code="label.Yes" /></a>	
			<a class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>											
		</div>
		</div>
		</div>
	</div>
	
	<div class="modal" id="emptyUID" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
		<div class="modal-body">
			<spring:message code="info.ProvideUID" />
		</div>
		<div class="modal-footer">
			<a class="btn btn-default" data-dismiss="modal"><spring:message code="label.Close" /></a>											
		</div>
		</div>
		</div>
	</div>

</body>
</html>
