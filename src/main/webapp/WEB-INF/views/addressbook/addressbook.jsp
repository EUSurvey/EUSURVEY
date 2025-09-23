<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.AddressBook" /></title>
	
	<%@ include file="../includes.jsp" %>

	<meta name="_csrf" content="${_csrf.token}"/>
	<meta name="_csrf_header" content="${_csrf.headerName}"/>

	<script>
		$(function () {
			var token = $("meta[name='_csrf']").attr("content");
			var header = $("meta[name='_csrf_header']").attr("content");
			$(document).ajaxSend(function(e, xhr, options) {
				xhr.setRequestHeader(header, token);
			});
		});
	</script>
	
	<link href="${contextpath}/resources/css/management.css" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/fileuploader.css" rel="stylesheet" type="text/css" />
	
	<script type="text/javascript" src="${contextpath}/resources/js/addressbook.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/configure.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/jquery.stickytableheaders.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/fileuploader.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/menu.js?version=<%@include file="../version.txt" %>"></script>
		
	<style>
	    #sortable { list-style-type: none; margin: 0; padding: 0; width: 190px; }
	    #sortable li { margin: 0 0px 0px 0px; padding: 0.4em; padding-left: 1.5em; min-height: 18px; }
	    #sortable li span:not(.glyphicon) {  margin-left: -1.3em; }	    
	    
	    .filtertools {
			float: right;
		}
		
		#batch-attendee-dialog-table .form-control {
			display: inline;
			max-width: 300px;
			width: 300px;
		}
    </style>
	
	<script type="text/javascript"> 
		var labelRemoveAttribute = '<spring:message code="label.RemoveAttribute" />';
		var usersTooOftenAddressBook = '<spring:message code="error.UsersTooOftenAddressBook" />';
	
		$(function() {					
			$("#addressbook-menu-tab").addClass("active");
			
			sneaky = new ScrollSneak(location.hostname);
			
			addRow(false);
			
			while ($("#attributes").find("tr").length < 4)
			{
				addRow(false);
			}
			
			$( "#sortable" ).sortable();
		    $( "#sortable" ).disableSelection();
		    
		    $("#batch-owner").autocomplete({
				source: "../logins"
			});
		    
		    $(".filtercell").each(function(){
				checkFilterCell(this, true);
			});
		    
		    $("#attendeestable").stickyTableHeaders({fixedOffset: 137});
		    
		    $('.checkAllCheckBox').click(function () {
		    	if ($(this).is(":checked"))
		    	{
		    		$('.selectedAttendee').prop("checked", 'checked');
		    		$('.checkAllCheckBox').prop("checked", 'checked');
		    		$("#gobutton").removeAttr("disabled");
		    	} else {
		    		$('.selectedAttendee').removeAttr('checked');
		    		$('.checkAllCheckBox').removeAttr('checked');
		    		$("#gobutton").attr("disabled","disabled");
		    	}
			});		    

		    $('#new-attribute-name').keyup(function(e) {
		    	if(e.keyCode == 13) {
		    		addAttribute($('#new-attribute-name').val());
		    	}
		    });
		    
		    $(window).scroll(function() {
			    if ($(window).scrollTop() <= $(document).height() - $(window).height() && $(window).scrollTop() >= $(document).height() - $(window).height() - 10) {
			    	loadMore();
			  }
			 });
		    
		    var uploader = new qq.FileUploader({
			    element: $("#file-uploader-contacts")[0],
			    action: '${contextpath}/addressbook/uploadAJAX',
			    uploadButtonText: selectFileForUpload,
			    params: {
			    	'_csrf': csrftoken
			    },
			    multiple: false,
			    cache: false,
			    sizeLimit: 2048576,
			    onComplete: function(id, fileName, responseJSON)
				{
			    	if (responseJSON.success)
			    	{
			    		$("#import-attendees-step1-error").hide();
			    		$("#file-uploader-contacts-file").val(responseJSON.uid);
			    		$("#file-uploader-contacts-filename").val(fileName);
			    		$("#file-uploader-contacts-filename-display").text(fileName);
			    		$("#loaded-file").hide();
			    		
			    		if (responseJSON.delimiter == "hide")
			    		{
			    			$("#file-uploader-contacts-delimiter").parent().hide();
			    		} else {
			    			$("#file-uploader-contacts-delimiter").parent().show();
			    			$("#file-uploader-contacts-delimiter").val(responseJSON.delimiter);
			    		}			    		
			    		
			    	}
				},
				showMessage: function(message){
					$("#file-uploader-contacts").append("<div class='validation-error'>" + message + "</div>");
				},
				onUpload: function(id, fileName, xhr){
					$("#file-uploader-contacts").find(".validation-error").remove();			
				}	    
			});
		    
			$(".qq-upload-button").addClass("btn btn-default").removeClass("qq-upload-button");
			$(".qq-upload-list").hide();
			$(".qq-upload-drop-area").css("margin-left", "-1000px");		
			
			$('[data-toggle="tooltip"]').tooltip(); 
			
		});			
		
		function checkGoButton()
		{
			var checked = $(".selectedAttendee:checked").length > 0;
			if (checked)
			{
				$("#gobutton").removeAttr("disabled");
			} else {
				$("#gobutton").attr("disabled", "disabled");
			}
			
			if ($(".selectedAttendee:not(checked)").length > 0)
			{
				$('.checkAllCheckBox').removeAttr('checked');
			}
		}
		
		function getAttributeValue(attributes, id, name)
		{
			for (var i = 0; i < attributes.length; i++ )
			{
				if (attributes[i].attributeName.id == id && attributes[i].value != null) return attributes[i].value;				
			}
			for (var i = 0; i < attributes.length; i++ )
			{
				if (attributes[i].attributeName.name == name && attributes[i].value != null) return attributes[i].value;				
			}
			return "&#160;";
		}
		
		var newPage = 2;
		var loadingmore = false;
        var endReached = false;
		function loadMore()
		{
			if (loadingmore || endReached) return;
			
			loadingmore = true;
			
			$( "#wheel" ).show();
			var s = "page=" + newPage++ + "&rows=50";	
			
			$.ajax({
				type:'GET',
				  url: "${contextpath}/addressbook/attendeesjson",
				  dataType: 'json',
				  data: s,
				  cache: false,
				  success: function( list ) {
					  
					if (list.length === 0) {
                        endreached = true;
                        $( "#wheel" ).hide();
                        return;
                    }	                        
				  					  
					  for (var i = 0; i < list.length; i++ )
					  {
						 var tr = document.createElement("tr");
						 
						 var td = document.createElement("td");
						 $(td).css("text-align","center");
						 
						 if (!list[i].readonly)
						 {						 
							 if ($(".checkAllCheckBox").first().is(":checked"))
							 {
								 $(td).append('<input onclick="checkGoButton()" checked="checked" style="margin-bottom: 6px;" class="selectedAttendee" name="selectedAttendee' + list[i].id + '" value="' + list[i].id + '" type="checkbox" id="' + list[i].id + '"/>');
							 } else {
								 $(td).append('<input onclick="checkGoButton()" style="margin-bottom: 6px;" class="selectedAttendee" name="selectedAttendee' + list[i].id + '" value="' + list[i].id + '" type="checkbox" id="' + list[i].id + '"/>');
							 }
							 
							 $(td).append('<input type="hidden" name="visibleAttendee' + list[i].id + '" value="' + list[i].id + '" />');
							
						 } else {
							 $(td).append('<input disabled="disabled" readonly="readonly" class="disabled" style="margin-bottom: 6px;" class="selectedAttendee" type="checkbox" id="' + list[i].id + '"/>');
						 }
						 
						 $(tr).append(td);
						 
						 td = document.createElement("td");
						 $(td).append(list[i].name);
						 $(tr).append(td);
						 
						 td = document.createElement("td");
						 $(td).append(list[i].email);
						 $(tr).append(td);
						 
						 <c:forEach items="${attributeNames}" var="attributeName">
						 	td = document.createElement("td");
						 
							 <c:choose>
								<c:when test="${attributeName.name.equals('Owner')}">
								 	$(td).append(list[i].owner);
								</c:when>
								<c:otherwise>
								 	$(td).append(getAttributeValue(list[i].attributes, '${attributeName.id}', '${HtmlUtils.htmlEscape(attributeName.name)}'));
								</c:otherwise>
							</c:choose>
							
							 $(tr).append(td);
						 </c:forEach>
						 
						 td = document.createElement("td");
						 $(td).css("width","90px");
						 if (!list[i].readonly)
						 {
							 $(td).append('<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.EditContact" />" href="${contextpath}/addressbook/editAttendee/' + list[i].id + '" class="iconbutton"><span class="glyphicon glyphicon-pencil"></span></a>');
							 $(td).append('<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.DeleteContact" />" onclick="showDeleteDialog(' + list[i].id + ');" class="iconbutton"><span class="glyphicon glyphicon-remove"></a>');
						 } else {
							 $(td).append('<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.EditContact" />" class="iconbutton disabled"><span class="glyphicon glyphicon-pencil"></span></i></a>');
							 $(td).append('<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.DeleteContact" />" class="iconbutton disabled"><span class="glyphicon glyphicon-remove"></span></a>');
						 }
						 $(tr).append(td);
						 
						 $("#attendeestable").find("tbody").append(tr);								
					  }
					  
					  if($("#attendeestable tbody tr").length > 0)
					  {
						 	 $("#tbllist-empty").hide();
					  }
					  
					  $(window).trigger('resize.stickyTableHeaders');
					  
					  $( "#wheel" ).hide();
					  $('[data-toggle="tooltip"]').tooltip(); 
					  
					  loadingmore = false;
				}});
		}
		
		$(document).ready(function() {
			
			$("#load-attendees").on("submit",function() {
				if($(".modal.in").lenght===0)
				{
					sneaky.sneak(); 
				}

				$('.modal-backdrop').hide();
				$('#show-wait-image').modal('show');
			});
		});
		
		var attributeNameIDs = new Array();
		<c:forEach items="${attributeNames}" var="attributeName">
			<c:if test='${!attributeName.name.equals("Owner")}'>
				attributeNameIDs.push("${attributeName.id}");		
			</c:if>		
		</c:forEach>
		
		function executeBulkEdit() {
			var count = $(".selectedAttendee:checked").length;
			if (count == 0)
			{
				$("#no-selection-dialog").modal("show");
				return;
			}
			
			$('#operation').val('batchedit');
			$('#load-attendees').submit();
		}
		
		function executeBulkDelete() {
			var count = $(".selectedAttendee:checked").length;
			if (count == 0)
			{
				$("#no-selection-dialog").modal("show");
				return;
			}
			
			$('#delete-attendees-dialog').modal('show');
		}
		
	</script>
		
</head>
<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
		
		<select class="form-control" style="display:none" id="allAttributes" onchange="showAddAttributeDialog(this);">
			<option></option>
			<option value="new"><spring:message code="label.New" />...</option>
			<c:forEach items="${allAttributeNames}" var="attributeName">																							
				<option value="<esapi:encodeForHTMLAttribute>${attributeName.id}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML>${attributeName.name}</esapi:encodeForHTML></option>
			</c:forEach>
		</select>
	
		<%@ include file="../menu.jsp" %>	
		
		<form:form modelAttribute="paging" id="load-attendees" method="POST" action="${contextpath}/addressbook" >		
			<input type="hidden" id="operation" name="operation" value=""></input>
			<div class="fixedtitle" style="padding: 0; padding-bottom: 15px;">
				<div class="fixedtitleinner" style="width: 100%">
					<spring:message code="label.Contact" var="contact" />
					<c:set var="pagingElementName" value="${contact}" />			
					<div class="hideme"><%@ include file="../paging.jsp" %></div>	
					
					<div id="action-bar" style="margin-bottom: 15px; max-width: 900px; margin-left: auto; margin-right: auto;">					
						
						<div style="float: left; padding-top:5px; margin-left: 10px; padding-right: 10px;">
							<a data-placement="bottom" class="iconbutton green" data-toggle="tooltip" title="<spring:message code="label.AddContact" />" onclick="showAddAttendeeDialog();" ><span class="glyphicon glyphicon-plus"></span></a>
							<a data-placement="bottom" class="iconbutton green" data-toggle="tooltip" title="<spring:message code="label.Import" />" onclick="$('#import-attendees-step1-error').hide();$('#import-attendees-step1-dialog').modal();"><span class="glyphicon glyphicon-import"></span></a>
						</div>
						
						<div style="float: left; padding-left: 10px; padding-top:5px; border-left: 1px solid #ddd;">
							<a data-placement="bottom" data-toggle="tooltip" title="<spring:message code="label.BulkEdit" />" onclick="executeBulkEdit()" class="iconbutton"><span class="glyphicon glyphicon-pencil"></span></a>
							<a data-placement="bottom" data-toggle="tooltip" title="<spring:message code="label.Delete" />" onclick="executeBulkDelete()" class="iconbutton"><span class="glyphicon glyphicon-trash"></span></a>
						</div>
						
						<div style="float: left; padding-top:5px; margin-left: 10px; padding-left: 10px; border-left: 1px solid #ddd;">
							<a data-placement="bottom" class="iconbutton" data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.ConfigureDisplayedAttributes" />" onclick="$('#configure-attributes-dialog').modal();"><span class="glyphicon glyphicon-wrench"></span></a>
						</div>
						
						<div style="float: left; border-left:1px solid #ddd; padding-left: 10px; margin-left: 10px; height: 30px; padding-top:5px;">
							<spring:message code="label.NumberOfResults" />: <esapi:encodeForHTML>${paging.numberOfItems}</esapi:encodeForHTML>
						</div>
						
						<div style="float: left; padding-top:-5px; margin-left: 10px; padding-left: 10px;">
							<a onclick="$('#operation').val('');$('#load-attendees').submit();" class="btn btn-primary"><spring:message code="label.Search" /></a>
							<a onclick="$('#show-wait-image').modal('show');" href="<c:url value="/addressbook?clear=true"/>" class="btn btn-default"><spring:message code="label.ResetFilter" /></a>
						</div>

						<c:if test="${filterSet}">
							<div style="float: left; padding-top:7px; margin-left: 10px; padding-left: 10px;">
								<b><spring:message code="label.CurrentFiltersApplied" /></b>
							</div>
						</c:if>
												
						<div style="float: right; text-align: right">
							<span style="padding: 0px 10px;"><spring:message code="label.Export" />:</span>
							<a data-placement="bottom" style="padding: 0px; display: inline;" data-toggle="tooltip" title="<spring:message code="tooltip.Exportcsv" />" onclick="showExportDialog('AddressBook', 'csv')" ><img src="${contextpath}/resources/images/file_extension_csv_small.png" /></a>
							<a data-placement="bottom" style="padding: 0px; display: inline;" data-toggle="tooltip" title="<spring:message code="tooltip.Exportxlsx" />" onclick="showExportDialog('AddressBook', 'xlsx')" ><img src="${contextpath}/resources/images/file_extension_xlsx_small.png" /></a>
							<a data-placement="bottom" style="padding: 0px; display: inline;" data-toggle="tooltip" title="<spring:message code="tooltip.Exportods" />" onclick="showExportDialog('AddressBook', 'ods')" ><img src="${contextpath}/resources/images/file_extension_ods_small.png" /></a>
						</div>				
					</div>				
				</div>
			</div>
		
			<div class="fullpage" style="padding-top:137px;">
				
				<table id="attendeestable" class="table table-bordered table-striped table-styled <c:if test="${paging.items.size() == 0}">hideme</c:if>" style="max-width: none; width: auto; margin-left: auto; margin-right: auto; min-width: 600px;">
					<thead>
						<tr>
							<th style="text-align: center;"><input style="margin-bottom: 4px;" value="true" type="checkbox" class="checkAllCheckBox" name="checkAllCheckBox" /></th>
							<th><spring:message code="label.Name" /></th>
							<th><spring:message code="label.Email" /></th>
							
							<c:forEach items="${attributeNames}" var="attributeName">
								<th><esapi:encodeForHTML>${attributeName.name}</esapi:encodeForHTML></th>
							</c:forEach>
							<th style="min-width: 70px"><spring:message code="label.Actions" /></th>
						</tr>
						<tr class="table-styled-filter">
							<th >&nbsp;</th>
							<th class="filtercell">
								<input onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${filter['name']}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="name" />
							</th>
							<th class="filtercell">
								<input onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${filter['email']}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="email" />
							</th>												
							<c:forEach items="${attributeNames}" var="attributeName">
								<th class="filtercell">
									<c:choose>
										<c:when test="${attributeName.name.equals('Owner')}">
											<input onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${filter["owner"]}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="owner" />
										</c:when>								
										<c:otherwise>
											<input onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${filter[attributeName.id.toString()]}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="<esapi:encodeForHTMLAttribute>${attributeName.id}</esapi:encodeForHTMLAttribute>" />
										</c:otherwise>
									</c:choose>
								</th>
							</c:forEach>
							<th style="width: 70px">&nbsp;</th>
						</tr>
					</thead>
					<tbody>				
						<c:forEach items="${paging.items}" var="attendee">
							<tr>
								<td style="text-align: center;">
									<c:choose>
										<c:when test="${attendee.readonly}">
											<input type="checkbox" disabled="disabled" class="disabled" value="" readonly="readonly" />
										</c:when>
										<c:otherwise>
											<input onclick="checkGoButton()" style="margin-bottom: 6px;" class="selectedAttendee" name="selectedAttendee<esapi:encodeForHTMLAttribute>${attendee.id}</esapi:encodeForHTMLAttribute>" value="<esapi:encodeForHTMLAttribute>${attendee.id}</esapi:encodeForHTMLAttribute>" type="checkbox" id="<esapi:encodeForHTMLAttribute>${attendee.id}</esapi:encodeForHTMLAttribute>"/>
											<input type="hidden" name="visibleAttendee<esapi:encodeForHTMLAttribute>${attendee.id}</esapi:encodeForHTMLAttribute>" value="<esapi:encodeForHTMLAttribute>${attendee.id}</esapi:encodeForHTMLAttribute>" />
										</c:otherwise>
									</c:choose>
								</td>
								<td><esapi:encodeForHTML>${attendee.name}</esapi:encodeForHTML></td>
								<td><esapi:encodeForHTMLAttribute>${attendee.email}</esapi:encodeForHTMLAttribute></td>
								
								<c:forEach items="${attributeNames}" var="attributeName">
									<td>
										<c:choose>
											<c:when test="${attributeName.name.equals('Owner')}">
												<esapi:encodeForHTML>${attendee.owner}</esapi:encodeForHTML>
											</c:when>
											<c:otherwise>
												<esapi:encodeForHTML>${attendee.getAttributeValue(attributeName.id, attributeName.name)}</esapi:encodeForHTML>
											</c:otherwise>
										</c:choose>									
									</td>
								</c:forEach>
								
								<td style="width: 90px">
									<c:choose>
										<c:when test="${attendee.readonly}">
											<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.EditContact" />"  class="iconbutton disabled"><span class="glyphicon glyphicon-pencil"></span></a>
											<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.DeleteContact" />" class="iconbutton disabled"><span class="glyphicon glyphicon-remove"></span></a>
										</c:when>
										<c:otherwise>
											<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.EditContact" />" href="<c:url value="/addressbook/editAttendee/${attendee.id}" />" class="iconbutton"><span class="glyphicon glyphicon-pencil"></span></a>
											<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.DeleteContact" />"onclick="showDeleteDialog(${attendee.id});" class="iconbutton"><span class="glyphicon glyphicon-remove"></a>
										</c:otherwise>
									</c:choose>						
								</td>
								
							</tr>
						</c:forEach>
		
					</tbody>
				</table>
				
				<div style="text-align: center">
					<img id="wheel" class="hideme" src="${contextpath}/resources/images/ajax-loader.gif" />
				</div>

				<div id="tbllist-empty-filter" class="noDataPlaceHolder" <c:if test="${paging.items.size() == 0 && filterSet}">style="display:block; margin-top:250px;"</c:if>>
					<p>
							<spring:message code="label.NoDataAddressBookTextFilter"/>
					<p>
				</div>

				<div id="tbllist-empty" class="noDataPlaceHolder" <c:if test="${paging.items.size() == 0 && !filterSet}">style="display:block; margin-top:250px;"</c:if>>
					<p>
							<spring:message code="label.NoDataAddressBookText"/>
					<p>
				</div>
				
				<c:if test="${pagingTable ne false}">
					<div class="RowsPerPage hide">
						<span><spring:message code="label.RowsPerPage" />&#160;</span>
					    <form:select onchange="moveTo('${paging.currentPage}')" path="itemsPerPage" id="itemsPerPage" style="width:70px; margin-top: 0px;" class="middle">
							<form:options items="${paging.itemsPerPageOptions}" />
						</form:select>		
					</div>
				</c:if>	
															
			</div>
			
			<div style="clear: both"></div>
	
		</form:form>
		
		<form:form id="add-attendee-form" method="POST" action="${contextpath}/addressbook/addAttendee">
			
		</form:form>
		
		<div class="modal" id="delete-attendee-dialog" data-backdrop="static">
			<div class="modal-dialog">
	   		<div class="modal-content">
			<div class="modal-body">
				<spring:message code="question.DeleteContact" />
			</div>
			<div class="modal-footer">
				<img id="delete-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
				<a  onclick="deleteAttendee('${contextpath}');" class="btn btn-primary" data-dismiss="modal"><spring:message code="label.Yes" /></a>
				<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>		
			</div>
			</div>
			</div>
		</div>
		
		<div class="modal" id="delete-attendees-dialog" data-backdrop="static">
			<div class="modal-dialog">
	   		<div class="modal-content">
			<div class="modal-body">
				<spring:message code="question.DeleteContacts" />
			</div>
			<div class="modal-footer">
				<img id="delete-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
				<a  onclick="deleteAttendees('${contextpath}');" class="btn btn-primary" data-dismiss="modal"><spring:message code="label.Yes" /></a>
				<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>			
			</div>
			</div>
			</div>
		</div>
		
		<div class="modal" id="add-attendee-dialog-attendeeexists" data-backdrop="static">
			<div class="modal-dialog">
	   		<div class="modal-content">
			<div class="modal-body">
				<spring:message code="error.AttendeeExists" />
			</div>
			<div class="modal-footer">
				<img id="add-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
				<a  onclick="addAttendeeExistsYes();" class="btn btn-primary"><spring:message code="label.Yes" /></a>	
				<a  onclick="addAttendeeExistsNo();" class="btn btn-default" ><spring:message code="label.No" /></a>			
			</div>
			</div>
			</div>
		</div>
		
		<div class="modal" id="add-attendee-dialog" data-backdrop="static">
			<div class="modal-dialog">
	    	<div class="modal-content">
			<div class="modal-header">
				<b><spring:message code="label.CreateNewContact" /></b>
			</div>
			<div class="modal-body">
				<table style="margin-left: 20px;">
					<c:if test="${USER.getGlobalPrivilegeValue('ContactManagement') == 2}">			
						<tr>
							<td style="min-width: 100px; vertical-align: top;"><spring:message code="label.Owner" /></td>
							<td>
								<div class="input-group">
							      	<div class="input-group-addon"><img src="${contextpath}/resources/images/People.png" style="margin-top:-3px; margin-right: 2px;" /></div>
							      	<input id="owner" class="form-control" name="owner" type="text" maxlength="255" style="width: 200px;" />
							    </div>
							    <div id="add-attendee-error-ownerdoesnotexist" class="hideme" style="color: #f00; margin: 10px;">
							    	<spring:message code="error.unknownowner" />
							    </div>
							</td>
							<td>&#160;</td>
						</tr>
					</c:if>
					<tr>
						<td style="min-width: 100px; vertical-align: top"><spring:message code="label.Name" /></td>
						<td>
							<div class="input-group">
						      	<div class="input-group-addon"><span class="glyphicon glyphicon-user"></span></div>
						      	<input id="name" class="form-control" name="name" type="text" maxlength="255" style="width: 200px;" />
						    </div>
							<div id="add-attendee-error-name" class="hideme" style="color: #f00; margin: 10px;">
								<spring:message code="message.ProvideName" />
							</div>
						</td>
						<td>&#160;</td>
					</tr>
					<tr>
						<td style="vertical-align: top"><spring:message code="label.Email" /></td>
						<td>
							<div class="input-group">
						      	<div class="input-group-addon"><span class="glyphicon glyphicon-envelope"></span></div>
						      	<input id="email" class="form-control" name="email" type="text" maxlength="255" style="width: 200px;" />
						    </div>
							<div id="add-attendee-error-email" class="hideme" style="color: #f00; margin: 10px;">
								<spring:message code="message.ProvideEmail" />
							</div>
							<div id="add-attendee-error-email2" class="hideme" style="color: #f00; margin: 10px;">
								<spring:message code="error.InvalidEmail" />
							</div>
						</td>
						<td>&#160;</td>
					</tr>
				</table>
				<div class="well" style="margin-top: 10px; margin-bottom: 0px;">
					<b><spring:message code="label.Attributes" /></b>
					<div id="add-attendee-error-multiple" class="hideme" style="color: #f00; margin: 10px;">
						<spring:message code="label.Attribute" />&#32; <span id="add-attendee-error-multiple-text"></span> <spring:message code="label.usedMoreThanOnce" />
					</div>			
					<div id="add-attendee-error-no-attribute" class="hideme" style="color: #f00; margin: 10px;">
						<spring:message code="label.NoAttributeNameFor" />&#32; <span id="add-attendee-error-no-attribute-text"></span>
					</div>
					<div style="height: 180px; overflow: auto;">
						<table id="attributes" style="margin-left: 20px;">
						</table>
					</div>
					<a data-toggle="tooltip" title="<spring:message code="label.AddAttribute" />" onclick="addRow(false);" class="iconbutton"><img src="${contextpath}/resources/images/add2.png"></a>
				</div>
			</div>
			<div class="modal-footer">
				<img id="add-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
				<a onclick="addAttendee();" class="btn btn-primary"><spring:message code="label.Save" /></a>	
				<a class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>			
			</div>
			</div>
			</div>
		</div>
		
		<c:if test="${attendee != null}">
		
			<div class="modal" id="edit-attendee-dialog" data-backdrop="static">
				<div class="modal-dialog">
	   			<div class="modal-content">
				<div class="modal-header">
					<b><spring:message code="label.EditContact" /></b>
					<input type="hidden" name="id" value="<esapi:encodeForHTMLAttribute>${attendee.id}</esapi:encodeForHTMLAttribute>" />
				</div>
				<div class="modal-body">
					<table style="margin-left: 20px;">
						<c:if test="${USER.getGlobalPrivilegeValue('ContactManagement') == 2}">
							<tr>
								<td style="min-width: 100px; vertical-align: top;"><spring:message code="label.Owner" /></td>
								<td>
									<div class="input-group">
								      	<div class="input-group-addon"><img src="${contextpath}/resources/images/People.png" style="margin-top:-3px; margin-right: 2px;" /></div>
								      	<input id="owner" class="form-control" name="owner" type="text" maxlength="255" style="width: 200px;" value="<esapi:encodeForHTMLAttribute>${owner}</esapi:encodeForHTMLAttribute>" />
								    </div>
								</td>
								<td>&#160;</td>
							</tr>
						</c:if>
						<tr>
							<td style="min-width: 100px; vertical-align: top;"><spring:message code="label.Name" /></td>
							<td>
								<div class="input-group">
							      	<div class="input-group-addon"><span class="glyphicon glyphicon-user"></span></div>
							      	<input id="name" class="form-control" name="name" type="text" maxlength="255" style="width: 200px;" value="<esapi:encodeForHTMLAttribute>${attendee.name}</esapi:encodeForHTMLAttribute>" />
							    </div>
								<div id="edit-attendee-error-name" class="hideme" style="color: #f00; margin: 10px;">
									<spring:message code="message.ProvideName" />
								</div>
							</td>
							<td>&#160;</td>
						</tr>
						<tr>
							<td style="min-width: 100px; vertical-align: top;"><spring:message code="label.Email" /></td>
							<td>
								<div class="input-group">
							      	<div class="input-group-addon"><span class="glyphicon glyphicon-envelope"></span></div>
							      	<input id="email" class="form-control" name="email" type="text" maxlength="255" style="width: 200px;" value="<esapi:encodeForHTMLAttribute>${attendee.email}</esapi:encodeForHTMLAttribute>" />
							    </div>
								<div id="edit-attendee-error-email" class="hideme" style="color: #f00; margin: 10px;">
									<spring:message code="message.ProvideEmail" />
								</div>
								<div id="edit-attendee-error-email2" class="hideme" style="color: #f00; margin: 10px;">
									<spring:message code="error.InvalidEmail" />
								</div>
							</td>
							<td>&#160;</td>
						</tr>
					</table>
					<div class="well" style="margin-top: 10px; margin-bottom: 0px;">
						<b><spring:message code="label.Attributes" /></b>
						<div id="edit-attendee-error-multiple" class="hideme" style="color: #f00; margin: 10px;">
							<spring:message code="label.Attribute" />&#32; <span id="edit-attendee-error-multiple-text"></span> <spring:message code="label.usedMoreThanOnce" />
						</div>
						<div id="edit-attendee-error-no-attribute" class="hideme" style="color: #f00; margin: 10px;">
							<spring:message code="label.NoAttributeNameFor" />&#32; <span id="edit-attendee-error-no-attribute-text"></span>
						</div>
						<div style="height: 170px; overflow: auto; margin-bottom: 2px;">
							<table id="edit-attributes" style="margin-left: 20px;">
								<c:forEach items="${attendee.attributes}" var="attribute">
									<tr>
										<td><esapi:encodeForHTML>${attribute.attributeName.name}</esapi:encodeForHTML><input type="hidden" class="existingkey" value="<esapi:encodeForHTMLAttribute>${attribute.attributeName.name}</esapi:encodeForHTMLAttribute>" /></td>
										<td><input type="text" class="form-control" maxlength="500" name="attribute<esapi:encodeForHTMLAttribute>${attribute.attributeName.id}</esapi:encodeForHTMLAttribute>" value="<esapi:encodeForHTMLAttribute>${attribute.value}</esapi:encodeForHTMLAttribute>" style="width: 250px;" /></td>
										<td><a data-toggle="tooltip" title="<spring:message code="label.RemoveAttribute" />" onclick="$(this).parent().parent().remove();" class="iconbutton" style="margin-bottom: 9px;"><span class="glyphicon glyphicon-remove"></a></td>
									</tr>
								</c:forEach>				
							</table>
						</div>
						<a data-toggle="tooltip" title="<spring:message code="label.AddAttribute" />" onclick="addRow(true);" class="iconbutton"><img src="${contextpath}/resources/images/add2.png"></a>
					</div>
				</div>
				<div class="modal-footer">
					<img id="add-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
					<a onclick="editAttendee();" class="btn btn-primary"><spring:message code="label.Save" /></a>		
					<a class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
				</div>
				</div>
				</div>
			</div>
		</c:if>
		
		<div class="modal" id="add-attribute-dialog" data-backdrop="static">
			<div class="modal-dialog">
	   		<div class="modal-content">
			<div class="modal-header">
				<b><spring:message code="label.CreateNewAttribute" /></b>
			</div>
			<div class="modal-body">
				<input type="text" id="new-attribute-name" maxlength="255" style="width: 250px;" /><br />
				<span style="color: #f00; margin-left: 10px;" id="new-attribute-error"></span>
			</div>
			<div class="modal-footer">
				<a  onclick="addAttribute($('#new-attribute-name').val());" class="btn btn-primary"><spring:message code="label.Create" /></a>
				<a  onclick="cancelAddAttributeDialog()" class="btn btn-default"><spring:message code="label.Cancel" /></a>			
			</div>	
			</div>
			</div>
		</div>
		
		<%@ include file="configure.jsp" %>	
		
		<c:if test="${messages != null}">
			<c:choose>
				<c:when test="${messages.size() == 1}">
					<script type="text/javascript">
						showInfo('${messages.get(0)}');
					</script>
				</c:when>
				<c:otherwise>
					<div class="modal" id="show-messages-dialog" data-backdrop="static">
						<div class="modal-dialog">
			   			<div class="modal-content">
						<div class="modal-header">
							<b><spring:message code="label.Result" /></b>
						</div>
						<div class="modal-body">
							<c:forEach items="${messages}" var="message">
								<p><esapi:encodeForHTML>${message}</esapi:encodeForHTML></p>
							</c:forEach>
							<c:if test="${summary != null}">
								<b><spring:message code="label.Summary" /></b>
								${summary}
							</c:if>
						</div>
						<div class="modal-footer">
							<a  class="btn btn-primary" data-dismiss="modal"><spring:message code="label.OK" /></a>				
						</div>
						</div>
						</div>
					</div>
				</c:otherwise>
			</c:choose>	
		</c:if>
	</div>
</div>

<%@ include file="../footer.jsp" %>	
<%@ include file="addressbook-import.jsp" %>
<%@ include file="addressbook-batch.jsp" %>	
	
	<c:choose>
		<c:when test="${editedParticipationGroups != null}">
			<div class="modal" id="update-guestlists-dialog" data-backdrop="static">
				<div class="modal-dialog">
	   			<div class="modal-content">
				<div class="modal-body">
					<c:choose>
						<c:when test="${editedParticipationGroupsDeleted == null}">
							<spring:message code="info.UpdateGuestlists" arguments="${editedAttendees},${editedParticipationGroupsSize}" />
						</c:when>
						<c:otherwise>
							<spring:message code="info.UpdateGuestlistsDeleted" arguments="${editedAttendees},${editedParticipationGroupsSize}" />
						</c:otherwise>
					</c:choose>				
				
					<div style="max-height: 400px; overflow: auto; margin-top: 20px">
						<table class="table table-bordered table-striped">
							<tr>
								<th><spring:message code="label.ContactName" /></th>
								<th><spring:message code="label.ContactEmail" /></th>
								<th><spring:message code="label.GuestList" /></th>
								<th><spring:message code="label.SurveyAlias" /></th>
							</tr>
						<c:forEach items="${editedParticipationGroups}" var="item">
							<tr>
								<td>${item.attendeeName}</td>
								<td>${item.attendeeEmail}</td>
								<td><a target="_blank" href="${contextpath}/${item.surveyAlias}/management/participantsEdit?id=${item.participationGroupId}">${item.participationGroupName}</a></td>
								<td><a target="_blank" href="${contextpath}/${item.surveyAlias}/management/overview">${item.surveyAlias}</a></td>
							</tr>
						</c:forEach>
						</table>
					</div>
					
				</div>
				<div class="modal-footer">
					<c:choose>
						<c:when test="${editedParticipationGroupsDeleted == null}">
							<a  onclick="updateGuestlists(true)" class="btn btn-default" data-dismiss="modal"><spring:message code="label.Update" /></a>				
						</c:when>
						<c:otherwise>
							<a  onclick="updateGuestlists(false)" class="btn btn-default" data-dismiss="modal"><spring:message code="label.Delete" /></a>				
						</c:otherwise>
					</c:choose>				
					<a  class="btn btn-default btn-primary" data-dismiss="modal"><spring:message code="label.Close" /></a>
				</div>
				</div>
				</div>
			</div>
			
			<script type="text/javascript">
				$("#update-guestlists-dialog").modal("show");
				
				function updateGuestlists(showinfo)
				{
					$.ajax({
						type:'POST',
						  url: '${contextpath}/addressbook/updateguestlists',
						  data: {ids: '${editedattendeesids}'},
						  beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
						  cache: false,
						  success: function( data ) {						  
							if (data != "OK") {								
								showError(data);								
							} else {
								showSuccess('<spring:message code="info.GuestListsUpdated" />')
							}
						}
					});	
				}
			</script>
		</c:when>
		<c:when test="${editedAttendeesBatch != null}">
			<script type="text/javascript">
				showSuccess('<spring:message code="message.ContactsUpdatedBatch" />');
			</script>
		</c:when>
		<c:when test="${editedAttendees != null && deletedcontacts != null}">
			<script type="text/javascript">
				showSuccess('<spring:message code="message.ContactsDeleted" />');
			</script>
		</c:when>
		<c:when test="${editedAttendees != null}">
			<script type="text/javascript">
				showSuccess('<spring:message code="message.ContactsUpdated" />');
			</script>
		</c:when>
		<c:when test="${deletedAttendeesBatch != null}">
			<script type="text/javascript">
				showSuccess('<spring:message code="message.ContactsDeletedBatch" />');
			</script>
		</c:when>
	</c:choose>
	
	<c:if test="${attendee != null}">
		<script type="text/javascript">
			addRow(true);
			while ($("#edit-attributes").find("tr").length < 4)
			{
				addRow(true);
			}
			$("#edit-attendee-dialog").modal();	
		</script>
	</c:if>
	
	<c:choose>
		<c:when test="${fileheaders != null}">
			<script type="text/javascript">
				addRow(true);
				
				<c:choose>
					<c:when test='${target.equalsIgnoreCase("importAttendees2")}'>
						$("#import-attendees-step3-dialog").modal();	
					</c:when>
					<c:when test='${target.equalsIgnoreCase("importAttendeesCheck")}'>
						$("#import-attendees-step2-dialog").modal();	
					</c:when>
					<c:when test='${messages != null}'>
						$("#show-messages-dialog").modal();	
					</c:when>
				</c:choose>
			</script>
		</c:when>
		<c:when test='${messages != null}'>
			<script type="text/javascript">
				showError('${messages[0]}');
			</script>
		</c:when>
	</c:choose>
	
	<c:if test="${error != null && error.length() > 0}">
		<script type="text/javascript">
			showError('${error }');
		</script>
	</c:if>
	
	<c:if test="${added != null}">
		<script type="text/javascript">
			showInfo('<spring:message code="label.Contact" />&nbsp;<spring:message code="message.AttendeeAdded" />');
		</script>
	</c:if>

</body>
</html>
