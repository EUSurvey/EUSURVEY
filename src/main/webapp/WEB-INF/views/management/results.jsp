<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Results" /></title>
	
	<%@ include file="../includes.jsp" %>
	
	<link href="${contextpath}/resources/css/management.css" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/charts.css" rel="stylesheet" type="text/css" />
	
	<style>
	
		.hiddenTableCell {
			min-width: 0px !important;
			max-width: 0px !important;
			overflow: hidden !important;
			padding: 0px !important;
			border: 0px !important;
			display: none;
		}
	
		.draghandle.dragged
		{
			border-left: 1px solid #f33;
		}
		
		.draghandle
		{
			position: absolute; 
			z-index:5; 
			width:5px;
			cursor:e-resize;
		}
		
		.maxH
		{
			max-height: 200px; 
			overflow-y: scroll;
		}
		
	    .filtertools {
			float: right;
		}
		
		.cellcreated, .cellupdated {
			min-width: 260px !important;
		}		
	
	</style>
	
	<script type="text/javascript"> 
		$(function() {
			<c:if test="${active eq true}">	
				<c:choose>
					<c:when test="${allanswers eq true}">
						$("#results-source").find("option[value='allanswers']").attr('selected','selected');
						$("#results-source").find("option[value='allanswers']").prop('selected','selected');
						$("#resetbutton").attr("href", $("#resetbutton").attr("href") + "&results-source=allanswers");
					</c:when>
					<c:otherwise>
						$("#results-source").find("option[value='active']").attr('selected','selected');
						$("#results-source").find("option[value='active']").prop('selected','selected');
					</c:otherwise>
				</c:choose>
			</c:if>
			
			<c:if test="${active eq false}">
				$("#results-source").find("option[value='draft']").attr('selected','selected');
				$("#results-source").find("option[value='draft']").prop('selected','selected');
				$("#resetbutton").attr("href", $("#resetbutton").attr("href") + "&results-source=draft");
			</c:if>
			
			 <c:choose>
			 	<c:when test="${publication == null && (sessioninfo.owner == USER.id || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('AccessResults') > 1)}">
					$("#show-delete-checkboxes-div").show();
				</c:when>
				<c:otherwise>
					$("#show-delete-checkboxes-div").hide();
				</c:otherwise>
			</c:choose>
			
			checkDeleteBoxes();
		
			<c:if test="${message == 'success'}">
				showInfo('<spring:message code="message.ContributionDeleted" />');
			</c:if>
		
			<c:if test="${message == 'failure'}">
				showError('<spring:message code="message.ContributionNotDeleted" />');
			</c:if>
			
			<c:if test="${message == 'recalculatestarted'}">
				showInfo('<spring:message code="message.recalculatestarted" />');
			</c:if>
				
			<c:if test="${deletedAnswers != null}">
				showInfo('${deletedAnswers}&nbsp;<spring:message code="message.ContributionDeleted" />');
			</c:if>
			
			$("#form-menu-tab").addClass("active");
			$("#results-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
			
			if ('${resultType}' != 'content')
			{
				switchTo('${resultType}');
			}
			
			$("ul.dropdown-menu").on("click", "[data-stopPropagation]", function(e) {
		        e.stopPropagation();
		    });		
			
			$(".filtercell").each(function(){
				checkFilterCell(this, true);
			});
									
			$('#export-name').keydown(function (e) {
			    if (e.keyCode == 13) {
			        e.preventDefault();
			        checkAndStartExport($('#export-name').val());
			        return false;
			    }
			});
			
			sneaky = new ScrollSneak(location.hostname);
			
			checkAssignedValues();
			
			$('.dropdown-menu').click(function(event){
			     event.stopPropagation();
			 });
			
			<c:choose>
			<c:when test="${skipstatistics == null}">
			loadStatisticsAsync(false);
			</c:when>
			<c:otherwise>
			$(".ajaxloaderimage").hide();
			$("#loadstatisticsbutton").show();
			</c:otherwise>
			</c:choose>
			
			$(window).on('resize', doResize);
			
			initCheckAll();
			$('[data-toggle="tooltip"]').tooltip(); 
		});
		
		function checkAndShowMultiDeleteDialog()
		{
			<c:choose>
				<c:when test="${paging.items.size() == 0}">
					$('#noResultsToDeleteDialog').modal('show');
				</c:when>
				<c:otherwise>
				
					var selected = $("input.checkDelete:checked").length;
					if (selected > 0)
					{
						$("#agreedelete").prop('checked', false);
						$('#deleteContributionConfirm').addClass('disabled');
						$('#confirmDeleteMultipleDialog').modal('show')
					}
				</c:otherwise>
			</c:choose>
		}
		
		function initCheckAll()
		{
			var all = true;
			$('input[name^="selected"]').each(function(){
				if (!$(this).is(":checked"))
				{
					all = false;
				}
			});
			if (all) $('#chkallShow').prop("checked","checked");
			
			all = true;
			$('input[name^="exportselected"]').each(function(){
				if (!$(this).is(":checked"))
				{
					all = false;
				}
			});
			if (all) $('#chkallExport').prop("checked","checked");
		}
		
		function checkAllShow()
		{
			if ($('#chkallShow').is(":checked"))
			{
				$('input[name^="selected"]').attr("checked","checked");
				$('input[name^="selected"]').prop("checked","checked");		
			} else {
				$('input[name^="selected"]').removeAttr("checked");
			}
		}
		
		function checkAllExport()
		{
			if ($('#chkallExport').is(":checked"))
			{
				$('input[name^="exportselected"]').attr("checked","checked");
				$('input[name^="exportselected"]').prop("checked","checked");		
			} else {
				$('input[name^="exportselected"]').removeAttr("checked");
			}
		}
		
		function doResize()
		{
			adaptScrollArea();
		}
		
		function checkAssignedValues()
		{
			if ($("#show-assigned-values").is(":checked"))
			{
				$(".assignedValue").show();
			} else {
				$(".assignedValue").hide();
			}
		}
		
		function checkDeleteBoxes()
		{
			if ($("#show-delete-checkboxes").is(":checked") && $('#results-table-link').hasClass("btn-info"))
			{
				$(".checkDelete").removeClass("hiddenTableCell");
			} else {
				$(".checkDelete").addClass("hiddenTableCell");
				$("#contentstable").css("width","auto");
			}
			
			synchronizeTableSizes();
		}
		
		function checkAllDelete()
		{
			if ($("#check-all-delete").is(":checked"))
			{
				$("input.checkDelete").attr("checked","checked");
				$("input.checkDelete").prop("checked","checked");
				$('#btnDeleteSelected').removeClass("disabled");
			} else {
				$("input.checkDelete").removeAttr("checked");
				checkDeleteButtonEnabled();
			}
		}
		
		function checkDelete(input)
		{
			if (!$(input).is(":checked"))
			{
				$("#check-all-delete").removeAttr("checked");
				checkDeleteButtonEnabled();
			} else {
				$('#btnDeleteSelected').removeClass("disabled");				
			}
		}
		
		function checkDeleteButtonEnabled()
		{
			var selected = $("input.checkDelete:checked").length;
			if (selected > 0)
			{
				$('#btnDeleteSelected').removeClass("disabled");
			} else {
				$('#btnDeleteSelected').addClass("disabled");
			}
		}
		
		function checkConfirmationTicked(input)
		{
			if ($(input).is(':checked')) 
			{ 
				$('#deleteContributionConfirm').removeClass('disabled'); 
			} else {
				$('#deleteContributionConfirm').addClass('disabled');
			}
		}
		
		function submitDeleteMultiple()
		{
			if (!$("#agreedelete").is(":checked"))
			{
				return;	
			}
			
			$('#confirmDeleteMultipleDialog').modal('hide');
			$("#operation").val("multidelete");
			$("#resultsForm").submit();
		}
				
		function switchTo(resultType)
		{
			switch(resultType)
			{
				case 'content':
					$("#results-table-link").addClass("btn-info");
					$("#results-statistics-quiz-link").removeClass("btn-info").addClass("btn-default");
					$("#results-statistics-link").removeClass("btn-info").addClass("btn-default");
					
					$("#results-table").find("tbody").removeClass('hidden');
					$("#results-table").find(".RowsPerPage").removeClass('hidden');
					$("#pager").removeClass('hidden');
					$("#results-charts").addClass('hidden');
					$("#results-statistics").addClass('hidden');
					$("#results-statistics-quiz").addClass('hidden');
					
					$("#content-export-buttons").removeClass('hidden');
					$("#charts-export-buttons").addClass('hidden');
					$("#statistics-export-buttons").addClass('hidden');
					$("#statistics-quiz-export-buttons").addClass('hidden');
					
					$("#search-buttons").show();				
					
					<c:if test="${paging.items.size() > 0}">
						$("#scrollarea").show();
					</c:if>
					
					$("#scrollareaheader").css("overflow-x", "hidden");
					$("#scrollareaheader").scrollLeft($("#scrollarea").scrollLeft());
					  
					resetSliderPositions($("#contentstable"));
					break;
				case 'statistics':
					$("#results-table-link").removeClass("btn-info").addClass("btn-default");
					$("#results-statistics-quiz-link").removeClass("btn-info").addClass("btn-default");
					$("#results-statistics-link").addClass("btn-info");
					
					$("#results-table").find(".RowsPerPage").addClass('hidden');
					$("#pager").addClass('hidden');
					$("#results-charts").addClass('hidden');
					$("#results-statistics").removeClass('hidden');
					$("#results-statistics-quiz").addClass('hidden');
					
					$("#content-export-buttons").addClass('hidden');
					$("#charts-export-buttons").addClass('hidden');
					$("#statistics-export-buttons").removeClass('hidden');
					$("#statistics-quiz-export-buttons").addClass('hidden');
					
					$("#scrollarea").hide();
					
					$("#scrollareaheader").css("overflow-x", "auto");
					break;
				case 'statistics-quiz':
					$("#results-table-link").removeClass("btn-info").addClass("btn-default");
					$("#results-statistics-link").removeClass("btn-info").addClass("btn-default");
					$("#results-statistics-quiz-link").addClass("btn-info");
					
					$("#results-table").find(".RowsPerPage").addClass('hidden');
					$("#pager").addClass('hidden');
					$("#results-charts").addClass('hidden');
					$("#results-statistics").addClass('hidden');
					$("#results-statistics-quiz").removeClass('hidden');
					
					$("#content-export-buttons").addClass('hidden');
					$("#charts-export-buttons").addClass('hidden');
					$("#statistics-export-buttons").addClass('hidden');
					$("#statistics-quiz-export-buttons").removeClass('hidden');
					
					$("#scrollarea").hide();
					
					$("#scrollareaheader").css("overflow-x", "auto");
					break;
			}
			
			$('#resultType').val(resultType);
			checkDeleteBoxes();			
		}
		
		var exportType;
		var exportFormat;
		
		function showExportDialog(type, format)
		{
			exportType = type;
			exportFormat = format;
			$('#export-name').val("");
			$('#export-name-dialog-type').text(format.toUpperCase());
			$('#export-name-dialog').find(".validation-error").hide();
			$('#export-name-dialog').modal();	
			$('#export-name-dialog').find("input").first().focus();
		}
		
		function startExport(name)
		{
			var showshortnames = $("#show-assigned-values").is(":checked");
			var allanswers = $("#allAnswers").val();
			// check again for new exports
			window.checkExport = true;
			
			$.ajax({
	           type: "POST",
	           url: "${contextpath}/exports/start/" + exportType + "/" + exportFormat,
	           data: {exportName: name, showShortnames: showshortnames, allAnswers: allanswers, group: ""},
	           beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
	           success: function(data)
	           {
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
		
		function reOrder(target, type)
		{		
			$(target).each(function(){
				var myArray = $(this).find("tbody").find("tr");
				
				if (type == 0)
				{
					myArray.sort(function (a, b) {
					    a = parseInt($(a).attr("data-position"), 10);
					    b = parseInt($(b).attr("data-position"), 10);
					    if(a > b) {
					        return 1;
					    } else if(a < b) {
					        return -1;
					    } else {
					        return 0;
					    }
					});
				} else if (type == 1)
				{
					myArray.sort(function (a, b) {
					    a = $(a).find("td").first().text().toLowerCase();
					    b = $(b).find("td").first().text().toLowerCase();
					    if(a > b) {
					        return 1;
					    } else if(a < b) {
					        return -1;
					    } else {
					        return 0;
					    }
					});
				} else if (type == 2)
				{
					myArray.sort(function (a, b) {
					    a = parseInt($(a).attr("data-value"), 10);
					    b = parseInt($(b).attr("data-value"), 10);
					    if(a > b) {
					        return -1;
					    } else if(a < b) {
					        return 1;
					    } else {
					        return 0;
					    }
					});
				}
				
				$(this).find("tbody").empty();
				$(this).find("tbody").append(myArray);
			});
			
		}
		
		function reOrder2(target, type)
		{		
			var table = $(target).first();
			
			$(table).find(".questiontitle").each(function(){
				
				var id = $(this).attr("id");
				var myArray = $(table).find("tr[class='cell" + id + "']");
				
				if (type == 0)
				{
					myArray.sort(function (a, b) {
					    a = parseInt($(a).attr("data-position"), 10);
					    b = parseInt($(b).attr("data-position"), 10);
					    if(a > b) {
					        return 1;
					    } else if(a < b) {
					        return -1;
					    } else {
					        return 0;
					    }
					});
				} else if (type == 1)
				{
					myArray.sort(function (a, b) {
					    a = $(a).find("td").first().text().toLowerCase();
					    b = $(b).find("td").first().text().toLowerCase();
					    if(a > b) {
					        return 1;
					    } else if(a < b) {
					        return -1;
					    } else {
					        return 0;
					    }
					});
				} else if (type == 2)
				{
					myArray.sort(function (a, b) {
					    a = parseInt($(a).attr("data-value"), 10);
					    b = parseInt($(b).attr("data-value"), 10);
					    if(a > b) {
					        return -1;
					    } else if(a < b) {
					        return 1;
					    } else {
					        return 0;
					    }
					});
				}
				
				$(this).parent().nextAll("tr[class='cell" + id + "']").remove();
				$(this).parent().after(myArray);
			});
			
		}
			
		var deletionCode;
		
		function showDeleteContributionDialog(code) {
			deletionCode = code;
			$('#delete-contribution-dialog').modal('show');			
		}
		
		function deleteContribution() {
		
			$('#delete-contribution-dialog').modal('hide');
			$('#show-wait-image').modal('show');
			
			$.ajax({
				type:'POST',
				  url: '<c:url value="/deletecontribution/"/>' + deletionCode,
				  beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
				  cache: false,
				  success: function( data ) {						  
					  if (data == "success") {
							$('#message').val("success");				
						} else {
							$('#message').val("failure");
						}
						$('#resultsForm').submit();
				}
			});	
			
			return false;
		}
		
		function resetSearch()
		{
			$("input[type='checkbox']").removeAttr("checked");
			$("input[type='text']").val("");
			$("#resultsForm").submit();
		}
		
		function resetSelections(div)
		{
			$(div).find("input[type=checkbox]").each(function(){
				if ($(this)[0].hasAttribute("data-checked"))
				{
					$(this).prop("checked", "checked");
				} else {
					$(this).removeAttr("checked");
				}
			});
		}
		
		$(document).ready(function() {
			
			<c:if test="${reloadScrollPosition != null}">
			
			readSliderPositions();
			
			if (localStorage != null)
			{
				if (!$("#scrollarea").is(":visible"))
				{
					$("#scrollareaheader").scrollLeft(localStorage.getItem("ResultsScrollLeft"));
				} else {
					$("#scrollarea").scrollLeft(localStorage.getItem("ResultsScrollLeft"));
				}
			}						
			
			</c:if>
			
			$("#resultsForm").on("submit",function() {
				
				var scrollLeft = $("#scrollarea").scrollLeft();
				if (scrollLeft == 0) scrollLeft = $("#scrollareaheader").scrollLeft();
				if (localStorage != null)
				{
					try {
						localStorage.setItem("ResultsScrollLeft", scrollLeft);
						saveSliderPositions();
					} catch(e) {
					    if(e.name == "NS_ERROR_FILE_CORRUPTED") {
					    	showError("Sorry, it looks like your browser storage has been corrupted. Please clear your storage by going to Tools -> Clear Recent History -> Cookies and set time range to 'Everything'. This will remove the corrupted browser storage across all sites.");
					    }
					}					
				}				
				
				if($(".modal.in").length===0)
				{
					sneaky.sneak(); 
				}
					
				$('.tableFloatingHeader').empty();
				$('.modal-backdrop').hide();
				$('#show-wait-image').modal('show');
								
				return true; 
			});
		});
		
	</script>
	
	<style type="text/css">
	
		.datepicker td {
			padding: 1px;
		}
		
	</style>
		
</head>
<body>

	<%@ include file="../header.jsp" %>
	<%@ include file="../menu.jsp" %>
	<%@ include file="formmenu.jsp" %>	
	
	<form:form modelAttribute="paging" id="resultsForm" method="POST" action="${contextpath}/${sessioninfo.shortname}/management/results" style="margin-top: 20px; margin-bottom: 0px;">
		<input type="hidden" name="operation" id="operation" />
		<input type="hidden" name="sort" id="sort" />
		
		<div class="fixedtitleform">
			<div class="fixedtitleinner">
				<h1><spring:message code="label.Results" /></h1>
			</div>
		
			<div class="well small-well" style="vertical-align: middle; margin-left: auto; margin-right: auto">		
				<div style="float: left; margin-top: 0px; margin-bottom: 0px;">		
					<a id="results-table-link" class="btn btn-xs btn-info" onclick="switchTo('content');"><img src="${contextpath}/resources/images/icons/24/table.png" /></a>
					<a id="results-statistics-link" class="btn btn-default btn-xs" onclick="switchTo('statistics');"><img src="${contextpath}/resources/images/icons/24/percentage.png" /></a>
					<c:if test="${form.survey.isQuiz}">
						<a id="results-statistics-quiz-link" class="btn btn-default btn-xs" onclick="switchTo('statistics-quiz');"><span class="glyphicon glyphicon-education" style="font-size: 19px; color: #333"></span></a>
					</c:if>
				</div>
				
				<div style="float: left; margin-top: 0px; margin-left: 20px;">
					<b><spring:message code="label.Source" /></b>
					<select onchange="$('#resultsForm').submit();" name="results-source" id="results-source" style="width: auto; margin-bottom: 0px">
						<c:choose>
							<c:when test="${!sessioninfo.owner.equals(USER.id) && USER.formPrivilege < 2 && USER.getLocalPrivilegeValue('AccessResults') < 1}">
									<option selected="selected" value="draft"><spring:message code="label.TestAnswers" /></option>
							</c:when>						
							<c:when test="${sessioninfo.owner.equals(USER.id) || USER.formPrivilege > 1 ||
											USER.getLocalPrivilegeValue('FormManagement') > 1 ||
											USER.getLocalPrivilegeValue('AccessResults') > 0}">											
								<c:choose>
									<c:when test="${!form.getSurvey().getIsDraft() || form.getSurvey().getIsPublished()}">
										<option value="draft"><spring:message code="label.TestAnswers" /></option>
										<option selected="selected" value="active"><spring:message code="label.PublishedSurveyAnswers" /></option>
										<option value="allanswers"><spring:message code="label.PublishedSurveyAnswersAll" /></option>
									</c:when>
									<c:otherwise>
										<option selected="selected" value="draft"><spring:message code="label.TestAnswers" /></option>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<option selected="selected" value="draft"><spring:message code="label.TestAnswers" /></option>			
							</c:otherwise>
						</c:choose>
					</select>
				</div>
				
				<div style="float: left; margin-top: 0px; margin-left: 20px;">
					<input onclick="checkAssignedValues()" name="show-assigned-values" type="checkbox" class="check" id="show-assigned-values" /><spring:message code="label.ShowAssignedValues" />
				</div>
				<div id="show-delete-checkboxes-div" style="float: left; margin-top: 0px; margin-left: 10px;">
					<c:choose>
						<c:when test="${showdeletecheckboxes == true}">
							<input onclick="checkDeleteBoxes()" checked="checked" value="true" name="show-delete-checkboxes" type="checkbox" class="check" id="show-delete-checkboxes" /><spring:message code="label.ShowDeleteCheckboxes" />
						</c:when>
						<c:otherwise>
							<input onclick="checkDeleteBoxes()" value="true" name="show-delete-checkboxes" type="checkbox" class="check" id="show-delete-checkboxes" /><spring:message code="label.ShowDeleteCheckboxes" />
						</c:otherwise>
					</c:choose>
				</div>
								
				<div id="content-export-buttons" style="min-width: 200px; margin-top: 2px; float: right; text-align: center" class="">
					<b><spring:message code="label.Export" /></b>
					
					<c:choose>
						<c:when test="${paging.items.size() == 0}">
							<a data-toggle="tooltip" title="<spring:message code="tooltip.Downloadxls" />" style="cursor: not-allowed" id="startExportContentLinkxls"  ><img src="${contextpath}/resources/images/file_extension_xls_small_grey.png" /></a>
							<a data-toggle="tooltip" title="<spring:message code="tooltip.Downloadods" />" style="cursor: not-allowed" id="startExportContentLinkods" ><img src="${contextpath}/resources/images/file_extension_ods_small_grey.png" /></a>
							<a data-toggle="tooltip" title="<spring:message code="tooltip.Downloadxml" />" style="cursor: not-allowed" id="startExportContentLinkxml" ><img src="${contextpath}/resources/images/file_extension_xml_small_grey.png" /></a>
							<c:if test="${!allanswers}">
		 						<a data-toggle="tooltip" title="<spring:message code="tooltip.Downloadpdf" />" style="cursor: not-allowed" id="startExportContentLinkpdf" ><img src="${contextpath}/resources/images/file_extension_pdf_small_grey.png" /></a> 
							</c:if>
						</c:when>
						<c:otherwise>
							<a data-toggle="tooltip" title="<spring:message code="tooltip.Downloadxls" />" id="startExportContentLinkxls"   onclick="showExportDialog('Content', 'xls')"><img src="${contextpath}/resources/images/file_extension_xls_small.png" /></a>
							<a data-toggle="tooltip" title="<spring:message code="tooltip.Downloadods" />" id="startExportContentLinkods"  onclick="showExportDialog('Content', 'ods')"><img src="${contextpath}/resources/images/file_extension_ods_small.png" /></a>
							<a data-toggle="tooltip" title="<spring:message code="tooltip.Downloadxml" />" id="startExportContentLinkxml"  onclick="showExportDialog('Content', 'xml')"><img src="${contextpath}/resources/images/file_extension_xml_small.png" /></a>
							<c:if test="${!allanswers}">
		 						<a data-toggle="tooltip" title="<spring:message code="tooltip.Downloadpdf" />" id="startExportContentLinkpdf"  onclick="showExportDialog('Content', 'pdf')"><img src="${contextpath}/resources/images/file_extension_pdf_small.png" /></a> 
							</c:if>
						</c:otherwise>
					</c:choose>
					
				</div>
				
				<div id="statistics-export-buttons" style="min-width: 200px; margin-top: 2px; float: right; text-align: center" class="hidden">
					<b><spring:message code="label.Export" /></b>
					
					<c:choose>
						<c:when test="${paging.items.size() == 0 || form.getSurvey().hasNoQuestionsForStatistics()}">
							<a data-toggle="tooltip" title="<spring:message code="tooltip.Downloadpdf" />" style="cursor: not-allowed" id="startExportStatisticsLinkpdf" ><img src="${contextpath}/resources/images/file_extension_pdf_small_grey.png" /></a>				
							<a data-toggle="tooltip" title="<spring:message code="tooltip.Downloadxls" />" style="cursor: not-allowed" id="startExportStatisticsLinkxls" ><img src="${contextpath}/resources/images/file_extension_xls_small_grey.png" /></a>
							<a data-toggle="tooltip" title="<spring:message code="tooltip.Downloadods" />" style="cursor: not-allowed" id="startExportStatisticsLinkods" ><img src="${contextpath}/resources/images/file_extension_ods_small_grey.png" /></a>
							<a data-toggle="tooltip" title="<spring:message code="tooltip.Downloaddoc" />" style="cursor: not-allowed" id="startExportStatisticsLinkdoc" ><img src="${contextpath}/resources/images/file_extension_doc_small_grey.png" /></a>
							<a data-toggle="tooltip" title="<spring:message code="tooltip.Downloadodt" />" style="cursor: not-allowed" id="startExportStatisticsLinkodt" ><img src="${contextpath}/resources/images/file_extension_odt_small_grey.png" /></a>
						</c:when>
						<c:otherwise>
							<a data-toggle="tooltip" title="<spring:message code="tooltip.Downloadpdf" />" id="startExportStatisticsLinkpdf"  onclick="showExportDialog('Statistics', 'pdf')"><img src="${contextpath}/resources/images/file_extension_pdf_small.png" /></a>				
							<a data-toggle="tooltip" title="<spring:message code="tooltip.Downloadxls" />" id="startExportStatisticsLinkxls"  onclick="showExportDialog('Statistics', 'xls')"><img src="${contextpath}/resources/images/file_extension_xls_small.png" /></a>
							<a data-toggle="tooltip" title="<spring:message code="tooltip.Downloadods" />" id="startExportStatisticsLinkods"  onclick="showExportDialog('Statistics', 'ods')"><img src="${contextpath}/resources/images/file_extension_ods_small.png" /></a>
							<a data-toggle="tooltip" title="<spring:message code="tooltip.Downloaddoc" />" id="startExportStatisticsLinkdoc"  onclick="showExportDialog('Statistics', 'doc')"><img src="${contextpath}/resources/images/file_extension_doc_small.png" /></a>
							<a data-toggle="tooltip" title="<spring:message code="tooltip.Downloadodt" />" id="startExportStatisticsLinkodt"  onclick="showExportDialog('Statistics', 'odt')"><img src="${contextpath}/resources/images/file_extension_odt_small.png" /></a>
						</c:otherwise>
					</c:choose>
					
				</div>
				
				<div id="statistics-quiz-export-buttons" style="min-width: 200px; margin-top: 2px; float: right; text-align: center" class="hidden">
					<b><spring:message code="label.Export" /></b>
					
					<c:choose>
						<c:when test="${paging.items.size() == 0}">
							<a style="cursor: not-allowed" id="startExportQuizStatisticsLinkpdf" ><img src="${contextpath}/resources/images/file_extension_pdf_small_grey.png" /></a>				
						</c:when>
						<c:otherwise>
							<a id="startExportQuizStatisticsLinkpdf"  onclick="showExportDialog('StatisticsQuiz', 'pdf')"><img src="${contextpath}/resources/images/file_extension_pdf_small.png" /></a>				
						</c:otherwise>
					</c:choose>
					
				</div>			
				
				<div style="clear: both"></div>	
			</div>
			
			<div style="clear: both"></div>
		</div>
		
		<div id="search-buttons" class="fixedtitlebuttons">
			<input type="submit" class="btn btn-default" value="<spring:message code="label.Search" />" />
			<a id="resetbutton" onclick="$('#show-wait-image').modal('show');" class="btn btn-default" href="${contextpath}/${sessioninfo.shortname}/management/results?reset=true"><spring:message code="label.Reset" /></a>
			<div style="margin-left: 750px; margin-top: -35px;">
				<a class="btn btn-default checkDelete disabled" id="btnDeleteSelected" onclick="checkAndShowMultiDeleteDialog();"><spring:message code="label.Delete" /></a>
				<a class="btn btn-default" id="btnConfigureFromResult" onclick="$('#configure-columns-dialog').modal('show')"><spring:message code="label.Configure" /></a>
			</div>
		</div>
		
		<div class="fullpageform" style="margin-top: 230px; margin-bottom: 0px;">
			<div>			
				<input type="hidden" id="message" name="message" />
				<input type="hidden" id="resultType" name="resultType" />
				<input type="hidden" name="active" value="${active}" />
				<input type="hidden" id="allAnswers" name="allAnswers" value="${allanswers}"/>
				<input type="hidden" id="resultsFormMode" name="resultsFormMode" value="" />
				
				<c:set var="questions" value="${form.getSurvey().getQuestions()}" />			
				<c:set var="pagingElementName" value="Answerset" />		
				
				<%@ include file="results-content.jsp" %>					
				<%@ include file="results-statistics.jsp" %>
				<%@ include file="results-statistics-quiz.jsp" %>
				<%@ include file="results-ajax.jsp" %>	
			</div>
		</div>

	</form:form>
	
	<div style="clear: both; height: 10px;"></div>

<%@ include file="../includes2.jsp" %>

	<div class="modal" id="configure-columns-dialog" data-backdrop="static">
		<div class="modal-dialog">
    	<div class="modal-content">
		<div class="modal-header">
			<spring:message code="message.SelectResultQuestions" />
			<div class="help" style="margin-bottom: -15px;"><spring:message code="info.SelectResultQuestions" /></div>
		</div>
		<div class="modal-body">
			<div style="max-height: 600px; overflow: auto;">
			<table class="table-bordered table-striped table-styled" id="tblConfigurationFromResult">	
				<thead>
					<tr>
						<th style="padding-left: 5px; min-width: 100px"><input class="check" type="checkbox" id="chkallShow" onclick="checkAllShow()" /><spring:message code="label.Show" /></th>
						<th style="padding-left: 5px; min-width: 100px"><input class="check" type="checkbox" id="chkallExport" onclick="checkAllExport()" /><spring:message code="label.Export" /></th>
						<th style="padding-left: 5px; padding-top: 5px"><spring:message code="label.Element" /></th>
					</tr>
				<thead>
				<tbody>
					<c:forEach items="${questions}" var="question">
						<c:if test="${question.getType() != 'Image' && question.getType() != 'Text' && question.getType() != 'Confirmation' && !(question.getType() == 'GalleryQuestion' && !question.selection) }">
							<tr>
								<td style="vertical-align: top; text-align: center"><input name="selected${question.id}" <c:if test="${filter.visible(question.id.toString())}">checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="${question.id}" /></td>
								<td style="vertical-align: top; text-align: center"><input name="exportselected${question.id}" <c:if test="${filter.exported(question.id.toString())}">checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="exported${question.id}" /></td>
								<td>${question.title.length() > 0? question.getStrippedTitleAtMost100() : question.shortname}</td>
							</tr>
						</c:if>
					</c:forEach>
					
					<tr>
						<td style="vertical-align: top; text-align: center"><input name="selectedinvitation" <c:if test='${filter.visible("invitation")}'>checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="invitation" /></td>
						<td style="vertical-align: top; text-align: center"><input name="exportselectedinvitation" <c:if test='${filter.exported("invitation")}'>checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="exportedinvitation" /></td>
						<td><spring:message code="label.InvitationNumber" /></td>
					</tr>
					<tr>
						<td style="vertical-align: top; text-align: center"><input name="selectedcase" <c:if test='${filter.visible("case")}'>checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="case" /></td>
						<td style="vertical-align: top; text-align: center"><input name="exportselectedcase" <c:if test='${filter.exported("case")}'>checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="exportedcase" /></td>
						<td><spring:message code="label.ContributionId" /></td>
					</tr>
					<tr>
						<td style="vertical-align: top; text-align: center"><input name="selecteduser" <c:if test='${filter.visible("user")}'>checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="user" /></td>
						<td style="vertical-align: top; text-align: center"><input name="exportselecteduser" <c:if test='${filter.exported("user")}'>checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="exporteduser" /></td>
						<td><spring:message code="label.UserName" /></td>
					</tr>
					<tr>
						<td style="vertical-align: top; text-align: center"><input name="selectedcreated" <c:if test='${filter.visible("created")}'>checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="created" /></td>
						<td style="vertical-align: top; text-align: center"><input name="exportselectedcreated" <c:if test='${filter.exported("created")}'>checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="exportedcreated" /></td>
						<td><spring:message code="label.CreationDate" /></td>
					</tr>
					<tr>
						<td style="vertical-align: top; text-align: center"><input name="selectedupdated" <c:if test='${filter.visible("updated")}'>checked="checked"</c:if> type="checkbox" class="check" id="updated" /></td>
						<td style="vertical-align: top; text-align: center"><input name="exportselectedupdated" <c:if test='${filter.exported("updated")}'>checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="exportedupdated" /></td>
						<td><spring:message code="label.LastUpdate" /></td>
					</tr>
					<tr>
						<td style="vertical-align: top; text-align: center"><input name="selectedlanguages" <c:if test='${filter.visible("languages")}'>checked="checked"</c:if> type="checkbox" class="check" id="languages" /></td>
						<td style="vertical-align: top; text-align: center"><input name="exportselectedlanguages" <c:if test='${filter.exported("languages")}'>checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="exportedlanguages" /></td>
						<td><spring:message code="label.Languages" /></td>
					</tr>
						
					<c:if test="${form.getSurvey().isQuiz}">
						<tr>
							<td style="vertical-align: top; text-align: center"><input checked="checked" disabled="disabled" type="checkbox" class="check" /></td>
							<td style="vertical-align: top; text-align: center"><input checked="checked" disabled="disabled" type="checkbox" class="check" /></td>
							<td><spring:message code="label.TotalScore" /></td>
						</tr>
					</c:if>
						
					</tbody>
			</table>
			</div>
		</div>
		<div class="modal-footer">
			<a  id="btnOkFromConfigurationResult" onclick="$('#resultsFormMode').val('configure'); $('#configure-columns-dialog').modal('hide'); $('#resultsForm').submit();" class="btn btn-info" ><spring:message code="label.OK" /></a>		
			<a  class="btn btn-default" onclick="resetSelections($('#tblConfigurationFromResult'))" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
		</div>
		</div>
		</div>
	</div>

	<div class="modal" id="delete-contribution-dialog">
		<div class="modal-dialog">
    	<div class="modal-content">
		<div class="modal-body">
			<spring:message code="question.DeleteContribution" />
		</div>
		<div class="modal-footer">
			<img id="delete-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a id="deleteSingleContributionConfirm" onclick="deleteContribution()"  class="btn btn-info"><spring:message code="label.Yes" /></a>
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>					
		</div>
		</div>
		</div>
	</div>
	
	<div class="modal" id="confirmDeleteMultipleDialog" data-backdrop="static">
		<div class="modal-dialog">
    	<div class="modal-content">
		<div class="modal-body">
			<c:choose>
				<c:when test="${active eq true}"><spring:message code="question.DeleteActiveContributions" /></c:when>
				<c:otherwise><spring:message code="question.DeleteContributions" /></c:otherwise>
			</c:choose>
			<br /><br />
			<input style="margin-left: 30px;" type="checkbox" id="agreedelete" onclick="checkConfirmationTicked(this)" /> <spring:message code="label.Iagree" />
			<br /><br />
		</div>
		<div class="modal-footer">
			<img id="delete-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a id="deleteContributionConfirm" onclick="submitDeleteMultiple()"  class="btn disabled btn-info"><spring:message code="label.Yes" /></a>
			<a id="deleteContributionCancel"  class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>					
		</div>
		</div>
		</div>
	</div>
	
	<div class="modal" id="noResultsToDeleteDialog" data-backdrop="static">
		<div class="modal-dialog">
    	<div class="modal-content">
		<div class="modal-body">
			<spring:message code="info.NoContributionsToDelete" />
		</div>
		<div class="modal-footer">
			<a  class="btn btn-info" data-dismiss="modal"><spring:message code="label.Cancel" /></a>			
		</div>
		</div>
		</div>
	</div>
	
</body>
</html>
