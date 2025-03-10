<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Exports" /></title>
	
	<%@ include file="../includes.jsp" %>
	<link href="${contextpath}/resources/css/management.css" rel="stylesheet" type="text/css" />
	
	<script type="text/javascript" src="${contextpath}/resources/js/jquery.stickytableheaders.js?version=<%@include file="../version.txt" %>"></script>
	
	<script type="text/javascript">

		$(function() {
			$('.visiblelink','#load-more-div').on('click', function() { 
				loadMore();
			});
			
			$("#exports-menu-tab").addClass("active");
			
			$('[data-toggle="tooltip"]').tooltip(); 
		});
		
		var exportTooltips = {
				'csv':'<spring:message code="tooltip.Downloadcsv" />',
				'doc':'<spring:message code="tooltip.Downloaddoc" />',
				'docx':'<spring:message code="tooltip.Downloaddocx" />',
				'ods':'<spring:message code="tooltip.Downloadods" />',
				'odt':'<spring:message code="tooltip.Downloadodt" />',
				'xls':'<spring:message code="tooltip.Downloadxls" />',
				'xlsx':'<spring:message code="tooltip.Downloadxlsx" />',
				'xml':'<spring:message code="tooltip.Downloadxml" />',
				'zip':'<spring:message code="tooltip.Downloadzip" />',
				'eus':'<spring:message code="tooltip.Downloadeus" />',
				'pdf':'<spring:message code="tooltip.Downloadpdf" />'};
		
		function getExportTooltip(type)
		{
			if(type in exportTooltips)
				return exportTooltips[type];
			return "";
		}
		
		var exportStates = {
				'Pending':'<spring:message code="label.Pending" />', 
				'Failed':'<spring:message code="label.Failed" />',
				'Finished':'<spring:message code="label.Finished" />'};
		
		function getExportState(state)
		{
			if(state in exportStates)
				return exportStates[state];
			return "";
		}
		
		var infinitePage = 0;
			
		//$(document).ready(loadMore);
		
		$(window).scroll(function() {
			    if ($(window).scrollTop() <= $(document).height() - $(window).height() && $(window).scrollTop() >= $(document).height() - $(window).height() - 10) {
			    	loadMore();
			  }
			 });
		
		$(function(){
		   loadMore();
		   $("#tbllist-exports").stickyTableHeaders({fixedOffset: 145});
		   $(window).trigger('resize.stickyTableHeaders');
		});
		
		function startOperation()
		{
			if ($("#operationselector").val() == "recreate")
			{
				recreateSelectedExports();
			} else if ($("#operationselector").val() == "delete")
			{
				showDeleteSelectedDialog();
			} 
		}
		
		var deletionId;		
		function showDeleteDialog(id) {			
			deletionId = id;	
			$('#delete-export-dialog').modal();
		}
		
		function deleteOneExport() {
			deleteExport(deletionId);
		}	
		
		function deleteExport(id) {
			
			$.ajax({
	            type: "POST",
	             url: '<c:url value="/exports/delete/" />' + id,
				  async: false,
				  cache: false,
				  beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
				  success: function(data)
				  {
					  if (data == "success") {
						showSuccess("<spring:message code="message.ExportsDeletedSuccessfully" />");
						$('#Row' + id).hide();
					} else {
						showError('<spring:message code="message.ExportsDeleteFailed" />');
					}
				  }
				});
			
			
			return false;			
		}	
		
		function showDeleteSelectedDialog() {		
			
			var ids = "";
			$('input[type="checkbox"]:checked').filter(":visible").each( function() {
				if (this.id != "checkAllCheckBox") {
					ids += (ids == "" ? this.id : '-' + this.id);
				}
			});	
			
			if (ids.length == 0)
			{
				$("#no-selection-dialog").modal("show");
				return;
			}
			
			$('#delete-exports-dialog').modal();	
		}		
		
		function deleteSelectedExports() {
			$('input[type="checkbox"]:checked').filter(":visible").each( function() {
				if (this.id != "checkAllCheckBox") {
					deleteExport(this.id);
				}
			});			
			showSuccess("<spring:message code="message.ExportsDeletedSuccessfully" />");
		}
		
		function recreateSelectedExports() {
			var exportIds = "";
			$('input[type="checkbox"]:checked').filter(":visible").each( function() {
				if (this.id != "checkAllCheckBox") {
					exportIds += (exportIds == "" ? this.id : '-' + this.id);
				}
			});	
			
			if (exportIds.length == 0)
			{
				$("#no-selection-dialog").modal("show");
				return;
			}
			
			document.location = '<c:url value="/exports/recreateMany/" />' + exportIds;
		}
		
		$(function () {
		    $('#checkAllCheckBox').click(function () {
		    	$('input[type="checkbox"]').not(":disabled").prop('checked', this.checked);
		    });
		});
		
		function sort(key, ascending)
		{
			$("#sortkey").val(key);
			if (ascending)
			{
				$("#sortorder").val("ASC");
			} else {
				$("#sortorder").val("DESC");
			}
			$("#load-exports").submit();
		}
		
		function loadMore()
		{
			$( "#wheel" ).show();
			var s = "page=" + infinitePage++ + "&rows=50";	
			
			$.ajax({
				type:'GET',
				url: "${contextpath}/exports/exportsjson",
				dataType: 'json',
				data: s,
				cache: false,
				success: refreshExports
				});
		}
		
		function refreshExports( list, textStatus, xhr ) {
				
			if (list.length == 0)
			{
				infinitePage--;
				$("#load-more-div").hide();
			}
			
			refreshExportsBasic( list, textStatus, xhr );
			$(window).trigger('resize.stickyTableHeaders');
		}
			
		function refreshExportsBasic( list, textStatus, xhr ) {
						
			for (var i = 0; i < list.length; i++ )
			  {
				var row = document.createElement("tr");
				
				$(row).attr({'id': 'Row' + list[i].id});

				var td = document.createElement("td");	
				
				$(td).attr({'style': 'vertical-align: middle; text-align: center;'});
				var inputCheck = document.createElement("input");
				
				$(inputCheck).attr({
				      'id': list[i].id,
				      'type': 'checkbox'
				    });
				
				if(list[i].state == 'Pending')
				{
					$(inputCheck).attr({
					      'disabled': 'disabled',
					      'type': 'checkbox'
					    });
				} else {
					$(inputCheck).attr({
					      'id': list[i].id,
					      'type': 'checkbox'
					    });
				}
				
				$(td).append(inputCheck);		
				$(row).append(td);
				
				var t = list[i].surveyTitle;
				if (t != null)
				{
					t = t.stripHtml115();
				}
				
				if (list[i].typeAddressBook)
				{
					t = "<spring:message code="label.AddressBookContacts" />";
				} else {
					t = '<spring:message code="label.Survey" />: <i><a href="${contextpath}/' + list[i].surveyShortname + '/management/overview">' + t + '</a></i>';
				}
				
				$(row).append('<td style="vertical-align: middle;"><div style="width: 400px; overflow: hidden;  word-break: break-all;">' + t + '</div></td>');
				$(row).append('<td style="vertical-align: middle;">' + list[i].name + '</td>');
				$(row).append('<td style="vertical-align: middle;">' + (list[i].displayUsername || "") + '</td>');

				td = document.createElement("td");
				$(td).attr({'style': 'vertical-align: middle; text-align: center;'});
			
				if (list[i].typeContent)
					$(td).append('<img data-toggle="tooltip" title="<spring:message code="label.Results" />" src="${contextpath}/resources/images/icons/24/table.png" name="content" />');

				if (list[i].typeStatistics)
					$(td).append('<img data-toggle="tooltip" title="<spring:message code="label.Statistics" />" src="${contextpath}/resources/images/icons/24/percentage.png" name="stat" />');

				if (list[i].typeStatisticsQuiz)
					$(td).append('<span data-toggle="tooltip" title="<spring:message code="label.QuizResults" />" class="glyphicon glyphicon-education" style="font-size: 25px"></span>');
				
				if (list[i].typeCharts)
					$(td).append('<img data-toggle="tooltip" title="<spring:message code="label.Charts" />" src="${contextpath}/resources/images/icons/24/business-chart.png" name="chart" />');

				if (list[i].typeAddressBook)
					$(td).append('<img data-toggle="tooltip" title="<spring:message code="label.Contacts" />" src="${contextpath}/resources/images/icons/24/people.png" />');
				
				if (list[i].typeActivity)
					$(td).append('<img data-toggle="tooltip" title="<spring:message code="label.Logs" />" src="${contextpath}/resources/images/icons/24/log.png" />');
				
				if (list[i].typeTokens)
					$(td).append('<img data-toggle="tooltip" title="<spring:message code="label.Tokens" />" src="${contextpath}/resources/images/icons/24/document_lock.png" />');

				if (list[i].typeFiles)
					$(td).append('<img data-toggle="tooltip" title="<spring:message code="label.Files" />" src="${contextpath}/resources/images/icons/24/table.png" />');

				if (list[i].typeVoterFiles)
					$(td).append('<span data-toggle="tooltip" title="<spring:message code="label.VoterFiles" />" class="glyphicon glyphicon-check" style="font-size: 25px"></span>');

				if (list[i].typeSurvey)
					$(td).append('<img data-toggle="tooltip" title="<spring:message code="label.Survey" />" src="${contextpath}/resources/images/icons/24/table.png" />');
				
				$(row).append(td);

				$(row).append('<td style="vertical-align: middle;">' + list[i].formattedDate + '</td>')

				td = document.createElement("td");
				$(td).attr({'style': 'vertical-align: middle; text-align: center;'});

				if (list[i].finished)
				{
					$(td).append('<a data-toggle="tooltip" title="' + getExportTooltip(list[i].format) +'" href="${contextpath}/exports/download/'+ list[i].id +'"><img src="${contextpath}/resources/images/file_extension_'+ list[i].format +'_small.png" name="'+list[i].format+'" /></a>');
					$(td).removeClass("export-pending");
				}
				else
				{
					$(td).append('<span class="label label-warning">'+ getExportState(list[i].state) + '</span>');
					$(td).addClass("export-pending");
				}

				$(row).append(td);

				td = document.createElement("td");
				$(td).attr({'style': 'vertical-align: middle; text-align: center;'});

			  	if (list[i].state != 'Pending') {
			  		if (!list[i].typeVoterFiles)
			  		{
						const a = document.createElement("a");
						$(a).attr("data-toggle", "tooltip").addClass("iconbutton").html('<span class="glyphicon glyphicon-refresh"></span>');
	
						if (list[i].valid) {
							$(a).attr("title", "<spring:message code="info.ExportUpToDate" />").addClass("disabled");
						} else {
							$(a).attr({
								title: "<spring:message code="info.UpdateExport" />",
								href: "${contextpath}/exports/recreate/" + list[i].id
							})
						}
	
						$(td).append(a);
			  		}
					$(td).append('<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="tooltip.DeleteThisExport" />"   onclick="showDeleteDialog(' + list[i].id + ')" class="iconbutton"><span class="glyphicon glyphicon-remove"></span></a>');
			  	}
				
				$(row).append(td);

				$('#tbllist-exports tbody').append(row);
			  }
			  
			  if($('#tbllist-exports tbody tr').size()==0)
				{
					$('#tbllist-empty').show();
				}
			  else
				  {
				  	$('#tbllist-empty').hide();
				  }
			
			  $( "#wheel" ).hide();
			  $( "#export-loading" ).hide();
			  
			  $('[data-toggle="tooltip"]').tooltip(); 
		}
		
	</script>
		
</head>
<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
		<%@ include file="../menu.jsp" %>
	
		<div class="fixedtitle" style="padding-bottom: 26px">
			<div class="fixedtitleinner">
				<div style="float: left">
					<select class="form-control" id="operationselector" style="display: inline; width: auto;">
						<option value="recreate"><spring:message code="label.RecreateSelected" /></option>
						<option value="delete"><spring:message code="label.RemoveSelected" /></option>
					</select>
					<a class="btn btn-default" style="margin-bottom: 3px" rel="tooltipp" title="<spring:message code="label.OK" />"  onclick="startOperation();"><spring:message code="label.OK" /></a>
				</div>
			
				<div style="text-align: center; padding-top: 5px;">
					<spring:message code="info.ExportsDeletedAutomatically1" />
				</div>	
			</div>
		</div>	
	
		<div class="fullpage" style="padding-top:145px;">
	
			<form:form id="load-exports" method="POST" action="${contextpath}/exports/list">
				<input type="hidden" name="sortKey" id="sortkey" value='<esapi:encodeForHTMLAttribute>${sortKey}</esapi:encodeForHTMLAttribute>' />
				<input type="hidden" name="sortOrder" id="sortorder" value='<esapi:encodeForHTMLAttribute>${sortOrder}</esapi:encodeForHTMLAttribute>' />
			
				<table id="tbllist-exports" class="table table-bordered table-styled" style="width: 1024px; margin-left: auto; margin-right: auto;">
					<thead style="background-color: white; border-top: 1px solid #eee;">
						<tr>
							<th style="text-align: center;"><input type="checkbox" id="checkAllCheckBox"/></th>
							<th style="max-width: 250px">
								<div style="float: right">
									<a data-toggle="tooltip" title="<spring:message code="label.SortAscending" />" onclick="sort('form',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" title="<spring:message code="label.SortDescending" />" onclick="sort('form',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
								</div>	 
								<spring:message code="label.Source" />
							</th>
							<th style="min-width: 130px">
								<div style="float: right">
									<a data-toggle="tooltip" title="<spring:message code="label.SortAscending" />" onclick="sort('name',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" title="<spring:message code="label.SortDescending" />" onclick="sort('name',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
								</div>	 
								<spring:message code="label.Name" />
							</th>
							<th><spring:message code="label.UserName" /></th>
							<th style="text-align: center;"><spring:message code="label.Type" /></th>
							<th style="min-width: 120px">
								<div style="float: right">
									<a data-toggle="tooltip" title="<spring:message code="label.SortAscending" />" onclick="sort('date',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" title="<spring:message code="label.SortDescending" />" onclick="sort('date',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
								</div>	 
								<spring:message code="label.Date" />
							</th>
							<th style="text-align: center;"><spring:message code="label.Result" /></th>
							<th><spring:message code="label.Actions" /></th>
						</tr>
						
					</thead>
					<tbody>
						<tr id="export-loading">
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
						<spring:message code="label.NoDataExportText"/>&nbsp;<img src="${contextpath}/resources/images/icons/32/forbidden_grey.png" alt="no data"/>
					<p>
				</div>
				
			</form:form>
		</div>
		
		<div class="modal" id="delete-export-dialog" data-backdrop="static">
			<div class="modal-dialog modal-sm">
	    	<div class="modal-content">
			<div class="modal-body">
				<spring:message code="question.DeleteExport" />
			</div>
			<div class="modal-footer">
				<img id="delete-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
				<a id="deleteExportConfirm" onclick="deleteOneExport()"  class="btn btn-primary" data-dismiss="modal"><spring:message code="label.Yes" /></a>	
				<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>											
			</div>
			</div>
			</div>
		</div>
		
		<div class="modal" id="delete-exports-dialog" data-backdrop="static">
			<div class="modal-dialog modal-sm">
	    	<div class="modal-content">
			<div class="modal-body">
				<spring:message code="question.DeleteSelectedExports" />						
			</div>
			<div class="modal-footer">
				<img id="delete-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
				<a id="deleteExportsConfirm" onclick="deleteSelectedExports()"  class="btn btn-primary" data-dismiss="modal"><spring:message code="label.Yes" /></a>		
				<a  class="btn btn-default" data-dismiss="modal" onclick="$('#operationselector').val('recreate');"><spring:message code="label.No" /></a>			
			</div>
			</div>
			</div>
		</div>				
		
		<div class="modal" id="no-selection-dialog" data-backdrop="static">
			<div class="modal-dialog modal-sm">
	    	<div class="modal-content">
			<div class="modal-body">
				<spring:message code="message.NoElementSelected" />						
			</div>
			<div class="modal-footer">
				<a  class="btn btn-primary" data-dismiss="modal"><spring:message code="label.OK" /></a>		
			</div>
			</div>
			</div>
		</div>	
	</div>

	<%@ include file="../footer.jsp" %>	

</body>
</html>
