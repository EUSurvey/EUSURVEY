<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Administration" /></title>	
	<%@ include file="../includes.jsp" %>	
	
	<script type="text/javascript" src="${contextpath}/resources/js/jquery.stickytableheaders.js?version=<%@include file="../version.txt" %>"></script>
	
	<style type="text/css">
		.ui-datepicker-calendar td {
			padding: 0px;
		}
		
		.filtertools {
			float: right;
		}
		
		.editbutton {
			margin-left: 10px;
		}
	</style>
	
	<script type="text/javascript"> 
		$(function() {
			$("#administration-menu-tab").addClass("active");
			$("#surveysearch-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
			
			switchMode(false);
			
			<c:if test="${mode != null}">
				loadMore();
			</c:if>
			
			<c:if test="${error != null}">
				showError('<esapi:encodeForHTML>${error}</esapi:encodeForHTML>');
			</c:if>
			
			<c:if test="${deleted != null}">
				showSuccess('<spring:message code="info.SurveyFinallyDeleted" />');
			</c:if>
			
			<c:if test="${frozen != null}">
				showSuccess('<spring:message code="info.SurveyFrozen" />');
			</c:if>
			
			<c:if test="${unfrozen != null}">
				showSuccess('<spring:message code="info.SurveyUnfrozen" />');
			</c:if>
			
			$('.filtercell').find("input").keyup(function(e){
			    if(e.keyCode == 13){
			    	$("#resultsForm").submit();
			    }
			});
			
			$(".filtercell").each(function(){
				checkFilterCell(this, true);
			});
			
			$("#surveyTableDivTable").stickyTableHeaders({fixedOffset: 208});
			$(document).mouseup(function (e)
			{
				if ($(e.target).hasClass("overlaybutton") || $(e.target).closest(".overlaybutton").length > 0)
				{
					e.stopPropagation();
 					return;
				}				
				
			    var container = $(".overlaymenu");

			    if (!container.is(e.target) // if the target of the click isn't the container...
			        && container.has(e.target).length === 0) // ... nor a descendant of the container
			    {
			        container.hide();
			    }
			});
			
			$('[data-toggle="tooltip"]').tooltip(); 
		});
		
		function switchMode(cleartable)
		{
			var mode = $("input[name=surveys]:checked").val();
			if (mode == 'archived')
			{
				$(".existingonly").hide();
				$(".deletedonly").hide();
				$(".archivedonly").show();
				$(".reportedonly").hide();
				$(".frozenonly").hide();
			} else if (mode == 'deleted')
			{
				$(".existingonly").hide();
				$(".archivedonly").hide();
				$(".deletedonly").show();
				$(".reportedonly").hide();
				$(".frozenonly").hide();
			} else if (mode == 'reported')
			{
				$(".existingonly").hide();
				$(".archivedonly").hide();
				$(".deletedonly").hide();
				$(".reportedonly").show();
				$(".frozenonly").hide();
			} else if (mode == 'frozen')
			{
				$(".existingonly").hide();
				$(".archivedonly").hide();
				$(".deletedonly").hide();
				$(".reportedonly").hide();
				$(".frozenonly").show();
			} else {
				$(".archivedonly").hide();
				$(".deletedonly").hide();
				$(".existingonly").show();
				$(".reportedonly").hide();
				$(".frozenonly").hide();
			}
			
			if (cleartable)
			$("#surveyTableDivTableBody").empty();
		}
		
		function confirmRestore(id, alias)
		{
			$("#confirm-restore-dialog-target").attr("data-alias",alias);
			
			var mode = $("input[name=surveys]:checked").val();
			if (mode == 'archived')
			{			
				$("#confirm-restore-dialog-target").attr("href","${contextpath}/archive/restore/" + id);
			} else if (mode == 'deleted')
			{			
				$("#confirm-restore-dialog-target").attr("href","${contextpath}/administration/restoredeleted/" + id);
			}
			$("#confirm-restore-dialog").modal("show");
		}
		
		function confirmFinalDelete(id, alias)
		{
			$("#confirm-finaldelete-dialog-target").attr("data-alias",alias);
			$("#confirm-finaldelete-dialog-target").attr("href","${contextpath}/administration/finallydelete/" + id);
			$("#confirm-finaldelete-dialog").modal("show");
		}
		
		var infinitePage = 1;
			
		$(window).scroll(function() {
			    if ($(window).scrollTop() <= $(document).height() - $(window).height() && $(window).scrollTop() >= $(document).height() - $(window).height() - 10) {
			    	loadMore();
			  }
			 });
		
		var endreached = false;
		function loadMore()
		{
			if (endreached) return;
			
			$( "#wheel" ).show();
			var s = "page=" + infinitePage++ + "&rows=50";	
			
			var mode = $("input[name=surveys]:checked").val();
			if (mode == 'archived')
			{
				$.ajax({
					type:'GET',
					url: "${contextpath}/administration/archivedsurveysjson",
					dataType: 'json',
					data: s,
					cache: false,
					success: refreshArchives
					});
			} else if (mode == 'deleted')
			{
				$.ajax({
					type:'GET',
					url: "${contextpath}/administration/deletedsurveysjson",
					dataType: 'json',
					data: s,
					cache: false,
					success: refreshDeleted
					});
			} else if (mode == 'reported')
			{
				$.ajax({
					type:'GET',
					url: "${contextpath}/administration/reportedsurveysjson",
					dataType: 'json',
					data: s,
					cache: false,
					success: refreshReported
					});
			} else if (mode == 'frozen')
			{
				$.ajax({
					type:'GET',
					url: "${contextpath}/administration/frozensurveysjson",
					dataType: 'json',
					data: s,
					cache: false,
					success: refreshFrozen
					});
			} else {
				$.ajax({
					type:'GET',
					url: "${contextpath}/administration/surveysearchJSON",
					dataType: 'json',
					data: s,
					cache: false,
					success: refreshSurveys,
					error: function(jqXHR, textStatus, errorThrown) {
					
					}});
			}
		}
		
		function refreshSurveys( list, textStatus, xhr ) {

			if (list.length == 0)
			{
				infinitePage--;
				endreached = true;
				$("#load-more-div").hide();
				$( "#wheel" ).hide();
				if ($('#surveyTableDivTableBody').find("tr").length == 0)
				{
				  $("#tbllist-empty").show();
				}
				
				return;
			}
			for (var i = 0; i < list.length; i++)
			  {
				var row = document.createElement("tr");
				
				var td = document.createElement("td");				
				$(td).append(list[i].uniqueId);		
				$(row).append(td);
				
				td = document.createElement("td");
				var a = document.createElement("a");
				$(a).attr("href","${contextpath}/" + list[i].shortname + "/management/overview").attr("target","_blank").html(list[i].shortname);
				$(td).append(a);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].title);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].owner.name);		
				var b = document.createElement("a");
				$(b).attr("data-toggle", "tooltip").attr("title", "<spring:message code="label.ChangeOwnership" />").addClass("editbutton").attr("onclick", "changeOwner('" + list[i].uniqueId + "','" + list[i].title + "','" + list[i].owner.name + "')").html("<span class='glyphicon glyphicon-pencil'></span>");
				$(td).append(b);
				$(row).append(td);

				td = document.createElement("td");
				$(td).append(list[i].organisation);
				$(row).append(td);

				td = document.createElement("td");
				if (list[i].isOPC) {
					$(td).append("<spring:message code="label.OPC"/>");
				} else if (list[i].isQuiz) {
					$(td).append("<spring:message code="label.Quiz"/>");
				} else if (list[i].isDelphi) {
					$(td).append("<spring:message code="label.Delphi"/>");
				} else if (list[i].isEVote) {
					$(td).append("<spring:message code="label.eVote"/>");
				} else if (list[i].isECF) {
					$(td).append("<spring:message code="label.ECF"/>");
				} else if (list[i].isSelfAssessment) {
					$(td).append("<spring:message code="label.SelfAssessment"/>");
				} else {
					$(td).append("<spring:message code="label.StandardSurvey"/>");
				}
				$(row).append(td);

				td = document.createElement("td");				
				$(td).append(list[i].niceFirstPublished);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].nicePublished);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].numberOfAnswerSetsPublished);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].numberOfDrafts);		
				$(row).append(td);

				td = document.createElement("td");

				a = document.createElement("a");
				if (list[i].isFrozen) {
					$(a).addClass("iconbutton float-right").attr("data-toggle", "tooltip").attr("title", "<spring:message code="label.Unfreeze" />").attr("onclick","unfreezeSurvey('" + list[i].id + "');").html('<span class="glyphicon glyphicon-ban-circle lightred"></span>');
				} else {
					$(a).addClass("iconbutton float-right").attr("data-toggle", "tooltip").attr("title", "<spring:message code="label.Freeze" />").attr("onclick","showFreezeDialog('" + list[i].id + "', '" + list[i].shortname + "', '${serverprefix}" + list[i].shortname + "/management/overview', '" + list[i].titleSort + "');").html('<span class="glyphicon glyphicon-ban-circle"></span>');
				}
				$(td).append(a);

				a = document.createElement("a");
				$(a).attr("data-state", list[i].state);
				$(a).addClass("iconbutton float-right").attr("data-toggle", "tooltip").attr("title", "<spring:message code="label.Delete" />").attr("onclick","showDeleteDialog('" + list[i].id + "');").html('<span class="glyphicon glyphicon-remove"></span>');
				$(td).append(a);

				a = document.createElement("a");
				$(a).addClass("iconbutton float-right").attr("href", "${contextpath}/" + list[i].shortname + "/management/repairxhtml?from=surveysearch").attr("data-toggle", "tooltip").attr("title", "<spring:message code="label.RepairXhtml" />").attr("onclick","$('#generic-wait-dialog').modal('show');").html('<span class="glyphicon glyphicon-wrench"></span>');
				$(td).append(a);

				if(!list[i].isEVote) {
					var a = document.createElement("a");
					$(a).addClass("iconbutton float-right").attr("data-toggle", "tooltip").attr("title", "<spring:message code="label.ExportWithAnswers" />").attr("onclick", "showExportDialog('Survey','eus','" + list[i].shortname + "')").html('<span class="glyphicon glyphicon-download-alt"></span>');
					$(td).append(a);
				}
				
				a = document.createElement("a");
				$(a).addClass("iconbutton float-right").attr("href", "${contextpath}/" + list[i].shortname + "/management/exportSurvey/true/" + list[i].shortname + "?delete=true").attr("data-toggle", "tooltip").attr("title", "<spring:message code="tooltip.Archive" />").attr("onclick","$('#generic-wait-dialog').modal('show');").html('<span class="glyphicon glyphicon-import"></span>');
				$(td).append(a);
				
				$(row).append(td);
								
				$('#surveyTableDivTableBody').first().append(row);
			  }
			  
			  $( "#wheel" ).hide();
			  
			  $("#surveyTableDivTable").stickyTableHeaders({fixedOffset: 208});
			  $('[data-toggle="tooltip"]').tooltip();
		}
		
		var exportShortname;
		function showExportDialog(type, format, shortname)
		{
			exportType = type;
			exportFormat = format;
			exportShortname = shortname;
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
	           data: {exportName: name, showShortnames: false, allAnswers: false, group: "", shortname: exportShortname},
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
		
		function refreshArchives( list, textStatus, xhr ) {
			
			if (list.length == 0)
			{
				infinitePage--;
				endreached = true;
				$("#load-more-div").hide();
				
				if ($('#surveyTableDivTableBody').find("tr").length == 0)
				{
				  $("#tbllist-empty").show();
				}
			}
			for (var i = 0; i < list.length; i++ )
			  {
				var row = document.createElement("tr");
				if (list[i].error != null && list[i].error.length > 0) {
					$(row).attr("style", "background-color: rgba(255, 96, 96, 0.57)");
				} else if (!(list[i].finished)) {
					$(row).attr("style", "background-color: #FFC6A3");
				} else {
					$(row).attr("style", "");
				}
				
				var td = document.createElement("td");				
				$(td).append(list[i].surveyUID);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].surveyShortname);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(strip_tags(list[i].surveyTitle));		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].formattedCreated);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].owner);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].replies);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].formattedArchived);		
				$(row).append(td);

				if (list[i].finished && list[i].error == null)
				{
				  td = document.createElement("td");
				  var a = document.createElement("a");
				  $(a).attr("data-toggle", "tooltip").attr("title", "<spring:message code="tooltip.Downloadpdf" />").attr("target","_blank").attr("href","${contextpath}/archive/surveypdf/" + list[i].id).html('<img src="${contextpath}/resources/images/file_extension_pdf_small.png" alt="pdf" />');
				  $(td).append(a);
				  $(row).append(td);
				} else {
				  $(row).append("<td>&nbsp;</td>");
				}

				  if (list[i].replies > 0 && list[i].finished && list[i].error == null)
				{
					td = document.createElement("td");

					if (list[i].hasXlsxResults) {
						a = document.createElement("a");
						$(a).attr("data-toggle", "tooltip").attr("title", "<spring:message code="tooltip.Downloadxlsx" />").attr("target","_blank").attr("href","${contextpath}/archive/resultsxlsx/" + list[i].id).html('<img src="${contextpath}/resources/images/file_extension_xlsx_small.png" alt="xlsx" />');
						$(td).append(a);
					} else {
						a = document.createElement("a");
						$(a).attr("data-toggle", "tooltip").attr("title", "<spring:message code="tooltip.Downloadxls" />").attr("target","_blank").attr("href","${contextpath}/archive/resultsxls/" + list[i].id).html('<img src="${contextpath}/resources/images/file_extension_xls_small.png" alt="xls" />');
						$(td).append(a);
					}

					if (list[i].surveyHasUploadedFiles)
					{
						if (list[i].hasXlsxResults) {
							a = document.createElement("a");
							$(a).attr("data-toggle", "tooltip").attr("title", "<spring:message code="tooltip.DownloadzipXlsx" />").attr("target","_blank").attr("href","${contextpath}/archive/resultsxlsxzip/" + list[i].id).html('<img src="${contextpath}/resources/images/file_extension_zip_small.png" alt="xlsx-zip" />');
							$(td).append(a);
						} else {
							a = document.createElement("a");
							$(a).attr("data-toggle", "tooltip").attr("title", "<spring:message code="tooltip.DownloadzipXls" />").attr("target","_blank").attr("href","${contextpath}/archive/resultsxlszip/" + list[i].id).html('<img src="${contextpath}/resources/images/file_extension_zip_small.png" alt="xls-zip" />');
							$(td).append(a);
						}
					}
					
					$(row).append(td);
					
					td = document.createElement("td");
					a = document.createElement("a");
					$(a).attr("data-toggle", "tooltip").attr("title", "<spring:message code="tooltip.Downloadpdf" />").attr("target","_blank").attr("href","${contextpath}/archive/statspdf/" + list[i].id).html('<img src="${contextpath}/resources/images/file_extension_pdf_small.png" alt="pdf" />');
					$(td).append(a);

					if (list[i].hasXlsxResults) {
						a = document.createElement("a");
						$(a).attr("data-toggle", "tooltip").attr("title", "<spring:message code="tooltip.Downloadxlsx" />").attr("target","_blank").attr("href","${contextpath}/archive/statsxlsx/" + list[i].id).html('<img src="${contextpath}/resources/images/file_extension_xlsx_small.png" alt="xlsx" />');
						$(td).append(a);
					} else {
						a = document.createElement("a");
						$(a).attr("data-toggle", "tooltip").attr("title", "<spring:message code="tooltip.Downloadxls" />").attr("target","_blank").attr("href","${contextpath}/archive/statsxls/" + list[i].id).html('<img src="${contextpath}/resources/images/file_extension_xls_small.png" alt="xls" />');
						$(td).append(a);
					}

					$(row).append(td);
				} else {
					$(row).append("<td>&nbsp;</td><td>&nbsp;</td>");
				}
				
				if (list[i].finished && list[i].error == null)
				{			
					td = document.createElement("td");
					var a = document.createElement("a");
					$(a).addClass("btn btn-primary").attr("onclick","confirmRestore(" + list[i].id + ", '" +  list[i].surveyShortname + "')").html("<spring:message code="label.Restore" />");
					$(td).append(a);
					$(row).append(td);	
				} else {
					$(row).append("<td>&nbsp;</td>");
				}
				
				
				$('#surveyTableDivTableBody').first().append(row);
			  }
			  
			  $(window).trigger('resize.stickyTableHeaders');
			  $( "#wheel" ).hide();
			  $('[data-toggle="tooltip"]').tooltip();
		}
		
		function refreshDeleted( list, textStatus, xhr ) {
			
			if (list.length == 0)
			{
				infinitePage--;
				endreached = true;
				$("#load-more-div").hide();
				
				if ($('#surveyTableDivTableBody').find("tr").length == 0)
				{
				  $("#tbllist-empty").show();
				}
			}
			for (var i = 0; i < list.length; i++ )
			  {
				var row = document.createElement("tr");
				
				var td = document.createElement("td");				
				$(td).append(list[i].id);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].uniqueId);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].shortname);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(strip_tags(list[i].title));		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].createdString);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].owner.name);		
				$(row).append(td);

				td = document.createElement("td");
				$(td).append(list[i].organisation);
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].numberOfAnswerSetsPublished);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].deletedString);		
				$(row).append(td);
				
				td = document.createElement("td");
				var a = document.createElement("a");
				$(a).addClass("btn btn-primary").attr("onclick","confirmRestore(" + list[i].id + ", '" +  list[i].shortname + "')").html("<spring:message code="label.Restore" />");
				$(td).append(a);
				
				a = document.createElement("a");
				$(a).addClass("btn btn-danger").attr("onclick","confirmFinalDelete(" + list[i].id + ", '" +  list[i].shortname + "')").html("<spring:message code="label.Delete" />");
				$(td).append("&nbsp;").append(a);
				
				$(row).append(td);	
				
				$('#surveyTableDivTableBody').first().append(row);
			  }
			  
			  $(window).trigger('resize.stickyTableHeaders');
			  $( "#wheel" ).hide();
		}
		
		function refreshReported( list, textStatus, xhr ) {
			
			if (list.length == 0)
			{
				infinitePage--;
				endreached = true;
				$("#load-more-div").hide();
				
				if ($('#surveyTableDivTableBody').find("tr").length == 0)
				{
				  $("#tbllist-empty").show();
				}
			}
			for (var i = 0; i < list.length; i++ )
			  {
				var row = document.createElement("tr");
				
				var td = document.createElement("td");				
				$(td).append(list[i].uniqueId);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].shortname);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(strip_tags(list[i].title));		
				$(row).append(td);
								
				td = document.createElement("td");				
				$(td).append(list[i].owner.name);		
				$(row).append(td);

				td = document.createElement("td");
				$(td).append(list[i].organisation);
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].niceFirstPublished);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].nicePublished);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].numberOfAnswerSetsPublished);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].numberOfDrafts);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].numberOfReports);		
				$(row).append(td);
				
				$('#surveyTableDivTableBody').first().append(row);
			  }
			  
			  $(window).trigger('resize.stickyTableHeaders');
			  $( "#wheel" ).hide();
		}
		
		function refreshFrozen( list, textStatus, xhr ) {
			
			if (list.length == 0)
			{
				infinitePage--;
				endreached = true;
				$("#load-more-div").hide();
				
				if ($('#surveyTableDivTableBody').find("tr").length == 0)
				{
				  $("#tbllist-empty").show();
				}
			}
			for (var i = 0; i < list.length; i++ )
			  {
				var row = document.createElement("tr");
				
				var td = document.createElement("td");				
				$(td).append(list[i].uniqueId);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].shortname);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(strip_tags(list[i].title));		
				$(row).append(td);
								
				td = document.createElement("td");				
				$(td).append(list[i].owner.name);		
				$(row).append(td);

				td = document.createElement("td");
				$(td).append(list[i].organisation);
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].niceFirstPublished);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].nicePublished);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].numberOfAnswerSetsPublished);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].numberOfDrafts);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].numberOfReports);		
				$(row).append(td);
				
				td = document.createElement("td");		
				var a = document.createElement("a");				
				if (list[i].isFrozen) {
					$(a).addClass("iconbutton").attr("data-toggle", "tooltip").attr("title", "<spring:message code="label.Unfreeze" />").attr("onclick","unfreezeSurvey('" + list[i].id + "');").html('<span class="glyphicon glyphicon-ban-circle lightred"></span>');
				} else {				
					$(a).addClass("iconbutton").attr("data-toggle", "tooltip").attr("title", "<spring:message code="label.Freeze" />").attr("onclick","showFreezeDialog('" + list[i].id + "', '" + list[i].shortname + "', '${serverprefix}" + list[i].shortname + "/management/overview', '" + list[i].titleSort + "');").html('<span class="glyphicon glyphicon-ban-circle"></span>');
				}				
				$(td).append(a);
				
				$(row).append(td);
				
				$('#surveyTableDivTableBody').first().append(row);
			  }
			  
			  $(window).trigger('resize.stickyTableHeaders');
			  $( "#wheel" ).hide();
			  $('[data-toggle="tooltip"]').tooltip();
		}
		
		var changeownersurveyuid = "";
		var changeownerid = null;
		function changeOwner(uid, title, owner)
		{
			changeownersurveyuid = uid;
					
			var text = '<spring:message code="question.changeOwnership" />';
			text = text.replace("[SURVEY]", title).replace("[USER]", owner);
			$("#changeownertext").html(text);			
			
			$("#userid").val("");
			$("#changeownererror").hide();
			
			checkUserType();
			
			$("#changeownerdialog").modal("show");
		}
		
		function saveNewOwner()
		{
			if (changeownerid == null)
			{
				$("#changeownererror").show();
			} else {
				var s = "surveyuid=" + changeownersurveyuid + "&userid=" + changeownerid;
								
				$.ajax({
					type:'POST',
					  url: contextpath + "/administration/changeowner",
					  beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
					  data: s,
					  dataType: 'json',
					  cache: false,
					  success: function( ok ) {
					  	if (ok)
					  	{
					  		showSuccess('<spring:message code="info.OwnerChanged" />');
					  	} else {
					  		showError('<spring:message code="error.OperationFailed" />');
					  	}						
					  }, error: function() {
						  showError('<spring:message code="error.OperationFailed" />');
					}});
				
				$("#changeownerdialog").modal("hide");
			}
		}
		
		function searchUser(order)
		{
			var name = $("#add-user-name").val();
			var first = $("#add-first-name").val();
			var last = $("#add-last-name").val();
			var email = $("#add-user-email").val();
			var type = $("#add-user-type-ecas").val();
			var department = $("#add-department-name").val();
			
			var s = "name=" + name + "&type=" + type + "&department=" + department+ "&email=" + email + "&first=" + first + "&last=" + last + "&order=" + order;
			
			$("#add-user-dialog").modal('hide');
			$("#busydialog").modal('show');
			
			$("#search-results-more").hide();
			
			$.ajax({
				type:'GET',
				  url: contextpath + "/logins/usersJSON",
				  data: s,
				  dataType: 'json',
				  cache: false,
				  success: function( users ) {
				  
					  $("#search-results").find("tbody").empty();
					  var body = $("#search-results").find("tbody").first();
					  
					  for (var i = 0; i < users.length; i++ )
					  {
						$(body).append(users[i]);
					  }
                                          
                      var hiddenTableHeaders = $("#search-results th.hideme");
                      for (var i = 0; i < hiddenTableHeaders.length; i++ )
					  {                                              
                      	$('#search-results td:nth-child(' + hiddenTableHeaders[i].cellIndex + ')').hide();
                      }
					  
					  if (type != "system" && users.length >= 100)
					  {
						  $("#search-results-more").show();  
					  }
					  
					  $(body).find("tr").click(function() {
						  if ($(this).attr("data-id"))
						  {
							  changeownerid = $(this).attr("data-id");
						  } else {
							  changeownerid = $(this).attr("id");
						  }
						  $("#changeownererror").hide();
						  $("#search-results").find(".success").removeClass("success");
						  $(this).addClass("success");
						});
					  
					  $("#busydialog").modal('hide');
					  $("#add-user-dialog").modal('show');
				  }, error: function() {
					  $("#busydialog").modal('hide');
					  $("#add-user-dialog").modal('show');
				}});
			
			$("#search-results-none").hide();
			
		}
				
		function checkUserType()
		{
			$("#search-results").find("tbody").empty();
			var thead = document.createElement("thead");
			
			if ($("#add-user-type-ecas").val() != "system" && $("#add-user-type-ecas").val() != "external")
			{
				$("#add-user-department-div").show();
				$("#add-user-firstname-div").show();
				$("#add-user-lastname-div").show();
				$("#add-user-domain-div").css("width", "500px");				
			} else if ($("#add-user-type-ecas").val() == "external")
			{
				$("#add-user-department-div").hide();
				$("#add-user-firstname-div").show();
				$("#add-user-lastname-div").show();
				$("#add-user-domain-div").css("width", "500px");
			} else {
				$("#add-user-department-div").hide();
				$("#add-user-firstname-div").hide();
				$("#add-user-lastname-div").hide();
				$("#add-user-domain-div").css("width", "500px");
			}
		}
		
		function sort(key, ascending)
		{
			$("#sortkey").val(key);
			if (ascending)
			{
				$("#sortorder").val("ASC");
			} else {
				$("#sortorder").val("DESC");
			}
			$("#resultsForm").submit();
		}
	</script>
		
</head>
<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
		<%@ include file="../menu.jsp" %>
		<%@ include file="adminmenu.jsp" %>	
		
		<form:form id="resultsForm" action="surveysearch" method="post" class="noautosubmitonclearfilter">
		
			<input type="hidden" name="sortkey" id="sortkey" value='<esapi:encodeForHTMLAttribute>${filter.sortKey}</esapi:encodeForHTMLAttribute>' />
			<input type="hidden" name="sortorder" id="sortorder" value='<esapi:encodeForHTMLAttribute>${filter.sortOrder}</esapi:encodeForHTMLAttribute>' />
				
		
		<div class="fixedtitleform">
			<div class="fixedtitleinner" style="height: 100px">
								
				<div id="action-bar" class="container action-bar" style="padding-top: 20px">
					<div class="row">
						<div class="col-md-12" style="text-align:center">
							<spring:message code="label.Search" var="labelSearch"/>
							<input rel="tooltip" title="${labelSearch}" class="btn btn-primary" type="submit" value="${labelSearch}" />

							<spring:message code="label.ResetFilter" var="labelResetFilter" />
							<spring:message code="label.Reset" var="labelReset"/>
							<a href="surveysearch" rel="tooltip" title="${labelResetFilter}" class="btn btn-default">${labelReset}</a>

							<c:choose>
								<c:when test='${mode == "archived" && enablearchiving}'>
									<input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="existing" onclick="switchMode(true)" /><spring:message code="label.ExistingSurveys" />
									<input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="archived" onclick="switchMode(true)" checked="checked" /><spring:message code="label.ArchivedSurveys" />
									<input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="deleted" onclick="switchMode(true)" /><spring:message code="label.DeletedSurveys" />
									<input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="reported" onclick="switchMode(true)" /><spring:message code="label.ReportedSurveys" />
									<input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="frozen" onclick="switchMode(true)" /><spring:message code="label.FrozenSurveys" />
								</c:when>
								<c:when test='${mode == "deleted" }'>
									<input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="existing" onclick="switchMode(true)" /><spring:message code="label.ExistingSurveys" />
									<c:if test="${enablearchiving}"><input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="archived" onclick="switchMode(true)" /><spring:message code="label.ArchivedSurveys" /></c:if>
									<input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="deleted" onclick="switchMode(true)" checked="checked" /><spring:message code="label.DeletedSurveys" />
									<input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="reported" onclick="switchMode(true)" /><spring:message code="label.ReportedSurveys" />
									<input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="frozen" onclick="switchMode(true)" /><spring:message code="label.FrozenSurveys" />
								</c:when>
								<c:when test='${mode == "reported" }'>
									<input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="existing" onclick="switchMode(true)" /><spring:message code="label.ExistingSurveys" />
									<c:if test="${enablearchiving}"><input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="archived" onclick="switchMode(true)" /><spring:message code="label.ArchivedSurveys" /></c:if>
									<input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="deleted" onclick="switchMode(true)" /><spring:message code="label.DeletedSurveys" />
									<input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="reported" onclick="switchMode(true)" checked="checked" /><spring:message code="label.ReportedSurveys" />
									<input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="frozen" onclick="switchMode(true)" /><spring:message code="label.FrozenSurveys" />
								</c:when>
								<c:when test='${mode == "frozen" }'>
									<input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="existing" onclick="switchMode(true)" /><spring:message code="label.ExistingSurveys" />
									<c:if test="${enablearchiving}"><input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="archived" onclick="switchMode(true)" /><spring:message code="label.ArchivedSurveys" /></c:if>
									<input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="deleted" onclick="switchMode(true)" /><spring:message code="label.DeletedSurveys" />
									<input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="reported" onclick="switchMode(true)" /><spring:message code="label.ReportedSurveys" />
									<input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="frozen" onclick="switchMode(true)" checked="checked" /><spring:message code="label.FrozenSurveys" />
								</c:when>
								<c:otherwise>
									<input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="existing" onclick="switchMode(true)" checked="checked" /><spring:message code="label.ExistingSurveys" />
									<c:if test="${enablearchiving}"><input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="archived" onclick="switchMode(true)" /><spring:message code="label.ArchivedSurveys" /></c:if>
									<input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="deleted" onclick="switchMode(true)" /><spring:message code="label.DeletedSurveys" />
									<input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="reported" onclick="switchMode(true)" /><spring:message code="label.ReportedSurveys" />
									<input type="radio" style="margin-left: 10px; margin-right: 5px;" name="surveys" value="frozen" onclick="switchMode(true)" /><spring:message code="label.FrozenSurveys" />
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</div>
			
			</div>
		</div>			
			
			<div class="fullpageadmin">			
					<div id="surveyTableDiv" style="min-height: 400px; padding-top: 208px;">
						<table id="surveyTableDivTable" class="table table-bordered table-styled" style="width: auto; margin-left: auto; margin-right: auto;">
							<thead style="border-top: 1px solid #ddd;">
							<tr class="archivedonly hideme">
								<th><spring:message code="label.Survey" /> UID</th>
								<th><spring:message code="label.Alias" /></th>
								<th><spring:message code="label.Title" /></th>
								<th>
									<div style="float: right">
										<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('created',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('created',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
									</div>
									<spring:message code="label.Created" />
								</th>
								<th><spring:message code="label.Owner" /></th>
								<th>
									<div style="float: right">
										<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('replies',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('replies',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
									</div>
									<spring:message code="label.Replies" />
								</th>
								<th>
									<div style="float: right">
										<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('archived',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('archived',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
									</div>
									<spring:message code="label.Archived" />
								</th>
								<th>PDF</th>
								<th><spring:message code="label.Results" /></th>
								<th><spring:message code="label.Statistics" /></th>
								<th><spring:message code="label.Actions" /></th>
							</tr>
							<tr class="deletedonly hideme">
								<th><spring:message code="label.Survey" /> ID</th>
								<th><spring:message code="label.Survey" /> UID</th>
								<th><spring:message code="label.Alias" /></th>
								<th><spring:message code="label.Title" /></th>
								<th>
									<div style="float: right">
										<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('created',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('created',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
									</div>
									<spring:message code="label.Created" />
								</th>
								<th><spring:message code="label.Owner" /></th>
								<th><spring:message code="label.Organisation" /></th>
								<th>
									<div style="float: right">
										<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('replies',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('replies',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
									</div>
									<spring:message code="label.Replies" />
								</th>
								<th><spring:message code="label.Deleted" /></th>
								<th><spring:message code="label.Actions" /></th>
							</tr>
							<tr class="existingonly">
								<th><spring:message code="label.Survey" /> UID</th>
								<th><spring:message code="label.Alias" /></th>
								<th><spring:message code="label.Title" /></th>
								<th><spring:message code="label.Owner" /></th>
								<th><spring:message code="label.Organisation" /></th>
								<th><spring:message code="label.Type" /></th>
								<th>
									<div style="float: right">
										<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('firstPublished',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('firstPublished',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
									</div>
									<spring:message code="label.FirstPublished" />
								</th>
								<th>
									<div style="float: right">
										<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('published',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('published',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
									</div>
									<spring:message code="label.Published" />
								</th>
								<th>
									<div style="float: right">
										<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('replies',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('replies',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
									</div>
									<spring:message code="label.NumberOfResults" />
								</th>
								<th><spring:message code="label.NumberOfDrafts" /></th>
								<th><spring:message code="label.Actions" /></th>
							</tr>
							<tr class="reportedonly">
								<th><spring:message code="label.Survey" /> UID</th>
								<th><spring:message code="label.Alias" /></th>
								<th><spring:message code="label.Title" /></th>
								<th><spring:message code="label.Owner" /></th>
								<th><spring:message code="label.Organisation" /></th>
								<th>
									<div style="float: right">
										<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('firstPublished',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('firstPublished',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
									</div>
									<spring:message code="label.FirstPublished" />
								</th>
								<th>
									<div style="float: right">
										<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('published',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('published',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
									</div>
									<spring:message code="label.Published" />
								</th>
								<th>
									<div style="float: right">
										<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('replies',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('replies',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
									</div>
									<spring:message code="label.NumberOfResults" />
								</th>
								<th><spring:message code="label.NumberOfDrafts" /></th>
								<th>
									<div style="float: right">
										<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('reported',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('reported',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
									</div>
									<spring:message code="label.NumberOfReports" />
								</th>
							</tr>
							<tr class="frozenonly">
								<th><spring:message code="label.Survey" /> UID</th>
								<th><spring:message code="label.Alias" /></th>
								<th><spring:message code="label.Title" /></th>
								<th><spring:message code="label.Owner" /></th>
								<th><spring:message code="label.Organisation" /></th>
								<th>
									<div style="float: right">
										<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('firstPublished',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('firstPublished',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
									</div>
									<spring:message code="label.FirstPublished" />
								</th>
								<th>
									<div style="float: right">
										<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('published',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('published',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
									</div>
									<spring:message code="label.Published" />
								</th>
								<th>
									<div style="float: right">
										<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('replies',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('replies',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
									</div>
									<spring:message code="label.NumberOfResults" />
								</th>
								<th><spring:message code="label.NumberOfDrafts" /></th>
								<th>
									<div style="float: right">
										<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('reported',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('reported',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
									</div>
									<spring:message code="label.NumberOfReports" />
								</th>
								<th><spring:message code="label.Actions" /></th>
							</tr>
							<tr class="table-styled-filter archivedonly hideme">
								<th class="filtercell">
									<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='${archivedfilter.uniqueId}' type="text" maxlength="255" style="margin:0px;" name="archiveuid" />
								</th>
								<th class="filtercell">
									<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${archivedfilter.shortname}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="archiveshortname" />
								</th>
								<th class="filtercell">
									<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${archivedfilter.title}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="archivetitle" />
								</th>
								<th class="filtercell cellcreated">
									<div class="btn-toolbar" style="margin: 0px; text-align: center">
										<div class="datefilter" style="float: left">
								  		<a class="btn btn-default" onclick="showOverlayMenu(this)" >
										     <c:choose>
										     	<c:when test="${archivedfilter.createdFrom != null}">
										     		<spring:eval expression="archivedfilter.createdFrom" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.from" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										  <div class="overlaymenu hideme">
										    	<input type="hidden" name="archivecreatedFrom" class="hiddendate" value="<spring:eval expression="archivedfilter.createdFrom" />" />
										    	<div id="metafiltercreatedfromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										   </div>
										</div>
										<div class="datefilter" style="float: left">	
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										  	<c:choose>
										     	<c:when test="${archivedfilter.createdTo != null}">
										     		<spring:eval expression="archivedfilter.createdTo" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.To" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										 <div class="overlaymenu hideme">
										    	<input type="hidden" name="archivecreatedTo" class="hiddendate" value="<spring:eval expression="archivedfilter.createdTo" />" />
										    	<div id="metafiltercreatedtodiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										    </div>
										</div>	
									</div>	
								</th>
								<th class="filtercell">
									<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${archivedfilter.owner}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="archiveowner" />
								</th>
								<th>&nbsp;</th>
								<th class="filtercell cellarchived">
									<div class="btn-toolbar" style="margin: 0px; text-align: center">
										<div class="datefilter" style="float: left">
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										     <c:choose>
										     	<c:when test="${archivedfilter.archivedFrom != null}">
										     		<spring:eval expression="archivedfilter.archivedFrom" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.from" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										  <div class="overlaymenu hideme">
										    	<input type="hidden" name="archivearchivedFrom" class="hiddendate" value="<spring:eval expression="archivedfilter.archivedFrom" />" />
										    	<div id="metafilterarchivedfromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										   </div>
										</div>
										<div class="datefilter" style="float: left">	
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										  	<c:choose>
										     	<c:when test="${archivedfilter.archivedTo != null}">
										     		<spring:eval expression="archivedfilter.archivedTo" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.To" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										 <div class="overlaymenu hideme">
										    	<input type="hidden" name="archivearchivedTo" class="hiddendate" value="<spring:eval expression="archivedfilter.archivedTo" />" />
										    	<div id="metafilterarchivedtodiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										    </div>
										</div>	
									</div>	
								</th>
								<th>&nbsp;</th>
								<th>&nbsp;</th>
								<th>&nbsp;</th>
								<th>&nbsp;</th>
							</tr>
							<tr class="table-styled-filter deletedonly hideme">
								<th class="filtercell">
									<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='${deletedfilter.id}' type="text" maxlength="255" style="margin:0px;" name="deletedid" />
								</th>
								<th class="filtercell">
									<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='${deletedfilter.uid}' type="text" maxlength="255" style="margin:0px;" name="deleteduid" />
								</th>
								<th class="filtercell">
									<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${deletedfilter.shortname}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="deletedshortname" />
								</th>
								<th class="filtercell">
									<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${deletedfilter.title}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="deletedtitle" />
								</th>
								<th class="filtercell cellcreated">
									<div class="btn-toolbar" style="margin: 0px; text-align: center">
										<div class="datefilter" style="float: left">
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										     <c:choose>
										     	<c:when test="${deletedfilter.generatedFrom != null}">
										     		<spring:eval expression="deletedfilter.generatedFrom" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.from" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										  <div class="overlaymenu hideme">
										    	<input type="hidden" name="deletedcreatedFrom" class="hiddendate" value="<spring:eval expression="deletedfilter.generatedFrom" />" />
										    	<div id="metafilterdeletedcreatedfromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										   </div>
										</div>
										<div class="datefilter" style="float: left">	
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										  	<c:choose>
										     	<c:when test="${deletedfilter.generatedTo != null}">
										     		<spring:eval expression="deletedfilter.generatedTo" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.To" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										 <div class="overlaymenu hideme">
										    	<input type="hidden" name="deletedcreatedTo" class="hiddendate" value="<spring:eval expression="deletedfilter.generatedTo" />" />
										    	<div id="metafilterdeletedcreatedtodiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										    </div>
										</div>	
									</div>	
								</th>
								<th class="filtercell">
									<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${deletedfilter.owner}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="deletedowner" />
								</th>
								<th class="filtercell smallfiltercell">
									<div class="btn-group">
										<a class="btn btn-default dropdown-toggle" data-toggle="dropdown" >
											<spring:message code="label.AllValues" />
											<span style="margin-right: 10px" class="caret"></span>
										</a>
										<ul class="dropdown-menu" style="padding: 10px; padding-bottom: 20px;">
											<li style="text-align: right;">
												<a style="display: inline"  onclick="$('#resultsForm').submit();" class="btn btn-default btn-xs" rel="tooltip" title="update"><spring:message code="label.OK" /></a>
											</li>
										</ul>
										<ul class="dropdown-menu" style="padding: 10px; margin-top: 42px;">
											<b><spring:message code="label.EuropeanCommission" />: <spring:message code="label.DGsAndServices" /></b>
											<c:forEach items="${organisationsDGS}" var="organisation">
												<c:choose>
													<c:when test='${deletedfilter.organisations != null && deletedfilter.containsOrganisation("deleted".concat(organisation.key))}'>
														<li><input checked="checked" onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="deleted${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:when>
													<c:otherwise>
														<li><input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="deleted${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:otherwise>
												</c:choose>
											</c:forEach>
											<b><spring:message code="label.EuropeanCommission" />: <spring:message code="label.ExecutiveAgencies" /></b>
											<c:forEach items="${organisationsEA}" var="organisation">
												<c:choose>
													<c:when test='${deletedfilter.organisations != null && deletedfilter.containsOrganisation("deleted".concat(organisation.key))}'>
														<li><input checked="checked" onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="deleted${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:when>
													<c:otherwise>
														<li><input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="deleted${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:otherwise>
												</c:choose>
											</c:forEach>
											<b><spring:message code="label.OtherEUIs" /></b>
											<c:forEach items="${organisationsOtherEUIs}" var="organisation">
												<c:choose>
													<c:when test='${deletedfilter.organisations != null && deletedfilter.containsOrganisation("deleted".concat(organisation.key))}'>
														<li><input checked="checked" onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="deleted${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:when>
													<c:otherwise>
														<li><input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="deleted${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:otherwise>
												</c:choose>
											</c:forEach>
											<b><spring:message code="label.NonEUIentities" /></b>
											<c:forEach items="${organisationsNonEUIs}" var="organisation">
												<c:choose>
													<c:when test='${deletedfilter.organisations != null && deletedfilter.containsOrganisation("deleted".concat(organisation.key))}'>
														<li><input checked="checked" onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="deleted${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:when>
													<c:otherwise>
														<li><input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="deleted${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:otherwise>
												</c:choose>
											</c:forEach>
										</ul>
									</div>
								</th>
								<th>&nbsp;</th>
								<th class="filtercell celldeleted">
									<div class="btn-toolbar" style="margin: 0px; text-align: center">
										<div class="datefilter" style="float: left">
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										     <c:choose>
										     	<c:when test="${deletedfilter.deletedFrom != null}">
										     		<spring:eval expression="deletedfilter.deletedFrom" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.from" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										  <div class="overlaymenu hideme">
										    	<input type="hidden" name="deleteddeletedFrom" class="hiddendate" value="<spring:eval expression="deletedfilter.deletedFrom" />" />
										    	<div id="metafilterdeletedfromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										   </div>
										</div>
										<div class="datefilter" style="float: left">	
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										  	<c:choose>
										     	<c:when test="${deletedfilter.deletedTo != null}">
										     		<spring:eval expression="deletedfilter.deletedTo" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.To" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										 <div class="overlaymenu hideme">
										    	<input type="hidden" name="deleteddeletedTo" class="hiddendate" value="<spring:eval expression="deletedfilter.deletedTo" />" />
										    	<div id="metafilterdeletedtodiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										    </div>
										</div>	
									</div>	
								</th>
								<th>&nbsp;</th>
							</tr>
							<tr class="table-styled-filter existingonly">
								<th class="filtercell">
									<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${filter.uid}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="uid" />
								</th>
								<th class="filtercell">
									<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${filter.shortname}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="shortname" />
								</th>
								<th class="filtercell">
									<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${filter.title}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="title" />
								</th>
								<th class="filtercell">
									<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${filter.owner}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="owner" />
								</th>
								<th class="filtercell smallfiltercell">
									<div class="btn-group">
										<a class="btn btn-default dropdown-toggle" data-toggle="dropdown" >
											<spring:message code="label.AllValues" />
											<span style="margin-right: 10px" class="caret"></span>
										</a>
										<ul class="dropdown-menu" style="padding: 10px; padding-bottom: 20px;">
											<li style="text-align: right;">
												<a style="display: inline"  onclick="$('#resultsForm').submit();" class="btn btn-default btn-xs" rel="tooltip" title="update"><spring:message code="label.OK" /></a>
											</li>
										</ul>
										<ul class="dropdown-menu" style="padding: 10px; margin-top: 42px;">
											<b><spring:message code="label.EuropeanCommission" />: <spring:message code="label.DGsAndServices" /></b>
											<c:forEach items="${organisationsDGS}" var="organisation">
												<c:choose>
													<c:when test='${filter.organisations != null && filter.containsOrganisation(organisation.key)}'>
														<li><input checked="checked" onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:when>
													<c:otherwise>
														<li><input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:otherwise>
												</c:choose>
											</c:forEach>
											<b><spring:message code="label.EuropeanCommission" />: <spring:message code="label.ExecutiveAgencies" /></b>
											<c:forEach items="${organisationsEA}" var="organisation">
												<c:choose>
													<c:when test='${filter.organisations != null && filter.containsOrganisation(organisation.key)}'>
														<li><input checked="checked" onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:when>
													<c:otherwise>
														<li><input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:otherwise>
												</c:choose>
											</c:forEach>
											<b><spring:message code="label.OtherEUIs" /></b>
											<c:forEach items="${organisationsOtherEUIs}" var="organisation">
												<c:choose>
													<c:when test='${filter.organisations != null && filter.containsOrganisation(organisation.key)}'>
														<li><input checked="checked" onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:when>
													<c:otherwise>
														<li><input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:otherwise>
												</c:choose>
											</c:forEach>
											<b><spring:message code="label.NonEUIentities" /></b>
											<c:forEach items="${organisationsNonEUIs}" var="organisation">
												<c:choose>
													<c:when test='${filter.organisations != null && filter.containsOrganisation(organisation.key)}'>
														<li><input checked="checked" onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:when>
													<c:otherwise>
														<li><input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:otherwise>
												</c:choose>
											</c:forEach>
										</ul>
									</div>
								</th>
								<th class="filtercell smallfiltercell">
									<div class="btn-group">
										<a class="btn btn-default dropdown-toggle" data-toggle="dropdown" >
											<spring:message code="label.AllValues" />
											<span style="margin-right: 10px" class="caret"></span>
										</a>
										<ul class="dropdown-menu" style="padding: 10px; padding-bottom: 20px;">
											<li style="text-align: right;">
												<a style="display: inline"  onclick="$('#resultsForm').submit();" class="btn btn-default btn-xs" rel="tooltip" title="update"><spring:message code="label.OK" /></a>
											</li>
										</ul>
										<ul class="dropdown-menu" style="padding: 10px; margin-top: 42px;">
											<c:choose>
												<c:when test='${filter.surveyTypes != null && filter.containsSurveyType("standard")}'>
													<li><input checked="checked" onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="surveyStandard" type="checkbox" class="check" style="width: auto !important;" value="true" /><spring:message code="label.Standard" /></li>
												</c:when>
												<c:otherwise>
													<li><input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="surveyStandard" type="checkbox" class="check" style="width: auto !important;" value="true" /><spring:message code="label.Standard" /></li>
												</c:otherwise>
											</c:choose>
											<c:choose>
												<c:when test='${filter.surveyTypes != null && filter.containsSurveyType("opc")}'>
													<li><input checked="checked" onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="surveyOPC" type="checkbox" class="check" style="width: auto !important;" value="true" /><spring:message code="label.OPC" /></li>
												</c:when>
												<c:otherwise>
													<li><input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="surveyOPC" type="checkbox" class="check" style="width: auto !important;" value="true" /><spring:message code="label.OPC" /></li>
												</c:otherwise>
											</c:choose>
											<c:choose>
												<c:when test='${filter.surveyTypes != null && filter.containsSurveyType("quiz")}'>
													<li><input checked="checked" onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="surveyQuiz" type="checkbox" class="check" style="width: auto !important;" value="true" /><spring:message code="label.Quiz" /></li>
												</c:when>
												<c:otherwise>
													<li><input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="surveyQuiz" type="checkbox" class="check" style="width: auto !important;" value="true" /><spring:message code="label.Quiz" /></li>
												</c:otherwise>
											</c:choose>
											<c:choose>
												<c:when test='${filter.surveyTypes != null && filter.containsSurveyType("delphi")}'>
													<li><input checked="checked" onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="surveyDelphi" type="checkbox" class="check" style="width: auto !important;" value="true" /><spring:message code="label.Delphi" /></li>
												</c:when>
												<c:otherwise>
													<li><input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="surveyDelphi" type="checkbox" class="check" style="width: auto !important;" value="true" /><spring:message code="label.Delphi" /></li>
												</c:otherwise>
											</c:choose>
											<c:choose>
												<c:when test='${filter.surveyTypes != null && filter.containsSurveyType("evote")}'>
													<li><input checked="checked" onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="surveyEVote" type="checkbox" class="check" style="width: auto !important;" value="true" /><spring:message code="label.eVote" /></li>
												</c:when>
												<c:otherwise>
													<li><input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="surveyEVote" type="checkbox" class="check" style="width: auto !important;" value="true" /><spring:message code="label.eVote" /></li>
												</c:otherwise>
											</c:choose>
											<c:choose>
												<c:when test='${filter.surveyTypes != null && filter.containsSurveyType("ecf")}'>
													<li><input checked="checked" onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="surveyECF" type="checkbox" class="check" style="width: auto !important;" value="true" /><spring:message code="label.ECF" /></li>
												</c:when>
												<c:otherwise>
													<li><input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="surveyECF" type="checkbox" class="check" style="width: auto !important;" value="true" /><spring:message code="label.ECF" /></li>
												</c:otherwise>
											</c:choose>
											<c:choose>
												<c:when test='${filter.surveyTypes != null && filter.containsSurveyType("selfassessment")}'>
													<li><input checked="checked" onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="surveySelfAssessment" type="checkbox" class="check" style="width: auto !important;" value="true" /><spring:message code="label.SelfAssessment" /></li>
												</c:when>
												<c:otherwise>
													<li><input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="surveySelfAssessment" type="checkbox" class="check" style="width: auto !important;" value="true" /><spring:message code="label.SelfAssessment" /></li>
												</c:otherwise>
											</c:choose>
										</ul>
									</div>
								</th>
								<th class="filtercell" style="min-width: 160px !important; max-width: 300px;">
									<div class="btn-toolbar" style="margin: 0px; text-align: center">
										<div class="datefilter" style="float: left">
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										     <c:choose>
										     	<c:when test="${filter.firstPublishedFrom != null}">
										     		<spring:eval expression="filter.firstPublishedFrom" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.from" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										  <div class="overlaymenu hideme">
										    	<input type="hidden" name="firstPublishedFrom" class="hiddendate" value="<spring:eval expression="filter.firstPublishedFrom" />" />
										    	<div id="metafilterfirstpublishedfrom" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										   </div>
										</div>
										<div class="datefilter" style="float: left">	
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										  	<c:choose>
										     	<c:when test="${filter.firstPublishedTo != null}">
										     		<spring:eval expression="filter.firstPublishedTo" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.To" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										 <div class="overlaymenu hideme">
										    	<input type="hidden" name="firstPublishedTo" class="hiddendate" value="<spring:eval expression="filter.firstPublishedTo" />" />
										    	<div id="metafilterfirstpublishedto" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										    </div>
										</div>	
									</div>	
								</th>
								<th class="filtercell" style="min-width: 160px !important; max-width: 300px;">
									<div class="btn-toolbar" style="margin: 0px; text-align: center">
										<div class="datefilter" style="float: left">
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										     <c:choose>
										     	<c:when test="${filter.publishedFrom != null}">
										     		<spring:eval expression="filter.publishedFrom" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.from" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										  <div class="overlaymenu hideme">
										    	<input type="hidden" name="publishedFrom" class="hiddendate" value="<spring:eval expression="filter.publishedFrom" />" />
										    	<div id="metafilterpublishedfrom" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										   </div>
										</div>
										<div class="datefilter" style="float: left">	
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										  	<c:choose>
										     	<c:when test="${filter.publishedTo != null}">
										     		<spring:eval expression="filter.publishedTo" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.To" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										 <div class="overlaymenu hideme">
										    	<input type="hidden" name="publishedTo" class="hiddendate" value="<spring:eval expression="filter.publishedTo" />" />
										    	<div id="metafilterpublishedto" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										    </div>
										</div>	
									</div>	
								</th>
								<th>&nbsp;</th>
								<th>&nbsp;</th>
								<th>&nbsp;</th>
							</tr>
							<tr class="table-styled-filter reportedonly">
								<th class="filtercell">
									<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${reportedfilter.uid}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="reporteduid" />
								</th>
								<th class="filtercell">
									<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${reportedfilter.shortname}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="reportedshortname" />
								</th>
								<th class="filtercell">
									<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${reportedfilter.title}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="reportedtitle" />
								</th>
								<th class="filtercell">
									<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${reportedfilter.owner}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="reportedowner" />
								</th>
								<th class="filtercell smallfiltercell">
									<div class="btn-group">
										<a class="btn btn-default dropdown-toggle" data-toggle="dropdown" >
											<spring:message code="label.AllValues" />
											<span style="margin-right: 10px" class="caret"></span>
										</a>
										<ul class="dropdown-menu" style="padding: 10px; padding-bottom: 20px;">
											<li style="text-align: right;">
												<a style="display: inline"  onclick="$('#resultsForm').submit();" class="btn btn-default btn-xs" rel="tooltip" title="update"><spring:message code="label.OK" /></a>
											</li>
										</ul>
										<ul class="dropdown-menu" style="padding: 10px; margin-top: 42px;">
											<b><spring:message code="label.EuropeanCommission" />: <spring:message code="label.DGsAndServices" /></b>
											<c:forEach items="${organisationsDGS}" var="organisation">
												<c:choose>
													<c:when test='${reportedfilter.organisations != null && reportedfilter.containsOrganisation("reported".concat(organisation.key))}'>
														<li><input checked="checked" onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="reported${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:when>
													<c:otherwise>
														<li><input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="reported${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:otherwise>
												</c:choose>
											</c:forEach>
											<b><spring:message code="label.EuropeanCommission" />: <spring:message code="label.ExecutiveAgencies" /></b>
											<c:forEach items="${organisationsEA}" var="organisation">
												<c:choose>
													<c:when test='${reportedfilter.organisations != null && reportedfilter.containsOrganisation("reported".concat(organisation.key))}'>
														<li><input checked="checked" onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="reported${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:when>
													<c:otherwise>
														<li><input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="reported${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:otherwise>
												</c:choose>
											</c:forEach>
											<b><spring:message code="label.OtherEUIs" /></b>
											<c:forEach items="${organisationsOtherEUIs}" var="organisation">
												<c:choose>
													<c:when test='${reportedfilter.organisations != null && reportedfilter.containsOrganisation("reported".concat(organisation.key))}'>
														<li><input checked="checked" onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="reported${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:when>
													<c:otherwise>
														<li><input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="reported${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:otherwise>
												</c:choose>
											</c:forEach>
											<b><spring:message code="label.NonEUIentities" /></b>
											<c:forEach items="${organisationsNonEUIs}" var="organisation">
												<c:choose>
													<c:when test='${reportedfilter.organisations != null && reportedfilter.containsOrganisation("reported".concat(organisation.key))}'>
														<li><input checked="checked" onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="reported${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:when>
													<c:otherwise>
														<li><input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="reported${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:otherwise>
												</c:choose>
											</c:forEach>
										</ul>
									</div>
								</th>
								<th class="filtercell" style="min-width: 160px !important; max-width: 300px;">
									<div class="btn-toolbar" style="margin: 0px; text-align: center">
										<div class="datefilter" style="float: left">
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										     <c:choose>
										     	<c:when test="${reportedfilter.firstPublishedFrom != null}">
										     		<spring:eval expression="filter.firstPublishedFrom" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.from" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										  <div class="overlaymenu hideme">
										    	<input type="hidden" name="reportedfirstPublishedFrom" class="hiddendate" value="<spring:eval expression="reportedfilter.firstPublishedFrom" />" />
										    	<div id="metafilterreportedfirstpublishedfrom" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										   </div>
										</div>
										<div class="datefilter" style="float: left">	
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										  	<c:choose>
										     	<c:when test="${reportedfilter.firstPublishedTo != null}">
										     		<spring:eval expression="filter.firstPublishedTo" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.To" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										 <div class="overlaymenu hideme">
										    	<input type="hidden" name="reportedfirstPublishedTo" class="hiddendate" value="<spring:eval expression="reportedfilter.firstPublishedTo" />" />
										    	<div id="metafilterreportedfirstpublishedto" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										    </div>
										</div>	
									</div>	
								</th>
								<th class="filtercell" style="min-width: 160px !important; max-width: 300px;">
									<div class="btn-toolbar" style="margin: 0px; text-align: center">
										<div class="datefilter" style="float: left">
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										     <c:choose>
										     	<c:when test="${reportedfilter.publishedFrom != null}">
										     		<spring:eval expression="reportedfilter.publishedFrom" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.from" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										  <div class="overlaymenu hideme">
										    	<input type="hidden" name="reportedpublishedFrom" class="hiddendate" value="<spring:eval expression="reportedfilter.publishedFrom" />" />
										    	<div id="metafilterreportedpublishedfrom" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										   </div>
										</div>
										<div class="datefilter" style="float: left">	
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										  	<c:choose>
										     	<c:when test="${reportedfilter.publishedTo != null}">
										     		<spring:eval expression="filter.publishedTo" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.To" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										 <div class="overlaymenu hideme">
										    	<input type="hidden" name="reportedpublishedTo" class="hiddendate" value="<spring:eval expression="reportedfilter.publishedTo" />" />
										    	<div id="metafilterreportedpublishedto" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										    </div>
										</div>	
									</div>	
								</th>
								<th>&nbsp;</th>
								<th>&nbsp;</th>
								<th>&nbsp;</th>
							</tr>
							
							<tr class="table-styled-filter frozenonly">
								<th class="filtercell">
									<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${frozenfilter.uid}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="frozenuid" />
								</th>
								<th class="filtercell">
									<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${frozenfilter.shortname}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="frozenshortname" />
								</th>
								<th class="filtercell">
									<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${frozenfilter.title}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="frozentitle" />
								</th>
								<th class="filtercell">
									<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${frozenfilter.owner}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="frozenowner" />
								</th>
								<th class="filtercell smallfiltercell">
									<div class="btn-group">
										<a class="btn btn-default dropdown-toggle" data-toggle="dropdown" >
											<spring:message code="label.AllValues" />
											<span style="margin-right: 10px" class="caret"></span>
										</a>
										<ul class="dropdown-menu" style="padding: 10px; padding-bottom: 20px;">
											<li style="text-align: right;">
												<a style="display: inline"  onclick="$('#resultsForm').submit();" class="btn btn-default btn-xs" rel="tooltip" title="update"><spring:message code="label.OK" /></a>
											</li>
										</ul>
										<ul class="dropdown-menu" style="padding: 10px; margin-top: 42px;">
											<b><spring:message code="label.EuropeanCommission" />: <spring:message code="label.DGsAndServices" /></b>
											<c:forEach items="${organisationsDGS}" var="organisation">
												<c:choose>
													<c:when test='${frozenfilter.organisations != null && frozenfilter.containsOrganisation("frozen".concat(organisation.key))}'>
														<li><input checked="checked" onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="frozen${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:when>
													<c:otherwise>
														<li><input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="frozen${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:otherwise>
												</c:choose>
											</c:forEach>
											<b><spring:message code="label.EuropeanCommission" />: <spring:message code="label.ExecutiveAgencies" /></b>
											<c:forEach items="${organisationsEA}" var="organisation">
												<c:choose>
													<c:when test='${frozenfilter.organisations != null && frozenfilter.containsOrganisation("frozen".concat(organisation.key))}'>
														<li><input checked="checked" onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="frozen${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:when>
													<c:otherwise>
														<li><input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="frozen${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:otherwise>
												</c:choose>
											</c:forEach>
											<b><spring:message code="label.OtherEUIs" /></b>
											<c:forEach items="${organisationsOtherEUIs}" var="organisation">
												<c:choose>
													<c:when test='${frozenfilter.organisations != null && frozenfilter.containsOrganisation("frozen".concat(organisation.key))}'>
														<li><input checked="checked" onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="frozen${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:when>
													<c:otherwise>
														<li><input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="frozen${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:otherwise>
												</c:choose>
											</c:forEach>
											<b><spring:message code="label.NonEUIentities" /></b>
											<c:forEach items="${organisationsNonEUIs}" var="organisation">
												<c:choose>
													<c:when test='${frozenfilter.organisations != null && frozenfilter.containsOrganisation("frozen".concat(organisation.key))}'>
														<li><input checked="checked" onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="frozen${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:when>
													<c:otherwise>
														<li><input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="frozen${organisation.key}" type="checkbox" class="check" style="width: auto !important;" value="true" />${organisation.value}</li>
													</c:otherwise>
												</c:choose>
											</c:forEach>
										</ul>
									</div>
								</th>
								<th class="filtercell" style="min-width: 160px !important; max-width: 300px;">
									<div class="btn-toolbar" style="margin: 0px; text-align: center">
										<div class="datefilter" style="float: left">
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										     <c:choose>
										     	<c:when test="${frozenfilter.firstPublishedFrom != null}">
										     		<spring:eval expression="filter.firstPublishedFrom" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.from" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										  <div class="overlaymenu hideme">
										    	<input type="hidden" name="frozenfirstPublishedFrom" class="hiddendate" value="<spring:eval expression="frozenfilter.firstPublishedFrom" />" />
										    	<div id="metafilterfrozenfirstpublishedfrom" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										   </div>
										</div>
										<div class="datefilter" style="float: left">	
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										  	<c:choose>
										     	<c:when test="${frozenfilter.firstPublishedTo != null}">
										     		<spring:eval expression="filter.firstPublishedTo" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.To" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										 <div class="overlaymenu hideme">
										    	<input type="hidden" name="frozenfirstPublishedTo" class="hiddendate" value="<spring:eval expression="frozenfilter.firstPublishedTo" />" />
										    	<div id="metafilterfrozenfirstpublishedto" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										    </div>
										</div>	
									</div>	
								</th>
								<th class="filtercell" style="min-width: 160px !important; max-width: 300px;">
									<div class="btn-toolbar" style="margin: 0px; text-align: center">
										<div class="datefilter" style="float: left">
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										     <c:choose>
										     	<c:when test="${frozenfilter.publishedFrom != null}">
										     		<spring:eval expression="frozenfilter.publishedFrom" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.from" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										  <div class="overlaymenu hideme">
										    	<input type="hidden" name="frozenpublishedFrom" class="hiddendate" value="<spring:eval expression="frozenfilter.publishedFrom" />" />
										    	<div id="metafilterfrozenpublishedfrom" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										   </div>
										</div>
										<div class="datefilter" style="float: left">	
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										  	<c:choose>
										     	<c:when test="${frozenfilter.publishedTo != null}">
										     		<spring:eval expression="filter.publishedTo" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.To" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										 <div class="overlaymenu hideme">
										    	<input type="hidden" name="frozenpublishedTo" class="hiddendate" value="<spring:eval expression="frozenfilter.publishedTo" />" />
										    	<div id="metafilterfrozenpublishedto" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										    </div>
										</div>	
									</div>	
								</th>
								<th>&nbsp;</th>
								<th>&nbsp;</th>
								<th>&nbsp;</th>
								<th>&nbsp;</th>
							</tr>	
											
							</thead>
							<tbody id="surveyTableDivTableBody"></tbody>
						</table>
						
						<div style="text-align: center">
							<img id="wheel" class="hideme" src="${contextpath}/resources/images/ajax-loader.gif" />
						</div>
						
						<div id="tbllist-empty" class="noDataPlaceHolder">
							<p>
								<spring:message code="label.NoDataSearchSurveyText"/>&nbsp;<img src="${contextpath}/resources/images/icons/32/forbidden_grey.png" alt="no data"/>
							<p>
						</div>					
					</div>
					
				<div style="clear: both"></div>
			</div>
		
		</form:form>
	</div>
		
	<%@ include file="../footer.jsp" %>	
	
	<div class="modal" id="changeownerdialog" data-backdrop="static">
		<div class="modal-dialog">
	    	<div class="modal-content">
			<div class="modal-header">
				<spring:message code="label.ChangeOwnership" />
			</div>
			<div class="modal-body">
				<div id="changeownertext" style="margin-bottom: 10px"></div>
				
				<c:choose>
					<c:when test="${USER.getGlobalPrivilegeValue('ECAccess') > 0}">
						<div style="width: 450px" id="add-user-domain-div">
							<label for="add-user-type-ecas"><spring:message code="label.Domain" /></label><br />
							<select class="form-control" id="add-user-type-ecas" onchange="checkUserType()" style="width: 450px" >
								<c:forEach items="${domains}" var="domain" varStatus="rowCounter">
									<option value="${domain.key}">${domain.value} </option>
								</c:forEach>
							</select>	
						</div>
						
						<div style="clear: both"></div>
						
						<div style="float: left; width: 250px; margin-right: 10px; margin-top: 10px;" id="add-user-department-div">
							<label for="add-department-name"><spring:message code="label.Department" /></label><br />
							<input class="form-control" type="text" maxlength="255" id="add-department-name" />
						</div>
						
						<div style="float: left; width: 250px;  margin-right: 10px; margin-top: 10px;" id="add-user-firstname-div">
							<label for="add-department-name"><spring:message code="label.FirstName" /></label><br />
							<input class="form-control" type="text" maxlength="255" id="add-first-name" />
						</div>
						
						<div style="float: left; width: 250px;  margin-right: 10px; margin-top: 10px;" id="add-user-name-div">
							<label for="add-user-name"><spring:message code="label.Login" /></label><br />
							<input class="form-control" type="text" maxlength="255" id="add-user-name" />
						</div>
						
						<div style="float: left; width: 250px;  margin-right: 10px; margin-top: 10px;" id="add-user-lastname-div">						
							<label for="add-last-name"><spring:message code="label.LastName" /></label><br />
							<input class="form-control" type="text" maxlength="255" id="add-last-name" />
						</div>			
						
						<div style="float: left; width: 250px;  margin-right: 10px; margin-top: 10px;">
							<label for="add-user-name"><spring:message code="label.EmailAddress" /></label><br />
							<input class="form-control" type="text" maxlength="255" id="add-user-email" />
						</div>
						
					</c:when>
					<c:when test="${USER.type == 'SYSTEM'}">
						<div style="width: 250px; margin-top: 10px;" id="add-user-domain-div">
							<label for="add-user-type-ecas"><spring:message code="label.Domain" /></label><br />
							<select class="form-control" id="add-user-type-ecas" onchange="checkUserType()">
								<option value="system" selected="selected"><spring:message code="label.System" /></option>
							</select>
						</div>
											
						<div style="clear: both"></div>
						
						<div style="float: left; width: 250px;  margin-right: 10px;" id="add-user-name-div">
							<label for="add-user-name"><spring:message code="label.Login" /></label><br />
							<input type="text" maxlength="255" id="add-user-name" />
						</div>
						
						<div style="float: left; width: 250px;  margin-right: 10px;">
							<label for="add-user-name"><spring:message code="label.EmailAddress" /></label><br />
							<input type="text" maxlength="255" id="add-user-email" />
						</div>
					</c:when>	
					<c:otherwise>
						<div style="width: 250px" id="add-user-domain-div">
							<label for="add-user-type-ecas"><spring:message code="label.Domain" /></label><br />
							<select id="add-user-type-ecas" onchange="checkUserType()">
								<option value="external" selected="selected"><spring:message code="label.EXT" /></option>
							</select>
						</div>
											
						<div style="clear: both"></div>
						
						<div style="float: left; width: 250px;  margin-right: 10px;" id="add-user-firstname-div">
							<label for="add-department-name"><spring:message code="label.FirstName" /></label><br />
							<input type="text" maxlength="255" id="add-first-name" />
						</div>
						
						<div style="float: left; width: 250px;  margin-right: 10px;" id="add-user-name-div">
							<label for="add-user-name"><spring:message code="label.Login" /></label><br />
							<input type="text" maxlength="255" id="add-user-name" />
						</div>
						
						<div style="float: left; width: 250px;  margin-right: 10px;" id="add-user-lastname-div">						
							<label for="add-last-name"><spring:message code="label.LastName" /></label><br />
							<input type="text" maxlength="255" id="add-last-name" />
						</div>	
						
						<div style="float: left; width: 250px;  margin-right: 10px;">
							<label for="add-user-name"><spring:message code="label.EmailAddress" /></label><br />
							<input type="text" maxlength="255" id="add-user-email" />
						</div>
					</c:otherwise>
				</c:choose>
				
				<div style="clear: both"></div>
				
				<div style="text-align: right">
					<div style="float: left; color: #f00; padding: 15px;" id="search-results-more" class="hideme"><spring:message code="message.SearchLimit100" /></div>
					<a id="btnSearchFromAccess"  onclick="searchUser('login');" class="btn btn-default" style="margin: 10px"><spring:message code="label.Search" /></a>	
				</div>		
				
				<div class="round" style="min-height: 323px; max-height: 323px; overflow: auto; width: 100%; overflow: auto; border: 1px solid #ddd" id="search-results-div">
					<table id="search-results" class="table table-bordered table-hover table-styled" style="max-width: none; margin-bottom: 0px">
						<thead>
							<tr>
								<th onclick="searchUser('mail');"><spring:message code="label.Email" /></th>
								<th onclick="searchUser('login');"><spring:message code="label.Login" /></th>
								<th onclick="searchUser('first');"><spring:message code="label.FirstName" /></th>
								<th onclick="searchUser('last',);"><spring:message code="label.LastName" /></th>
	                            <th onclick="searchUser('department');" <c:if test="${oss}">class="hideme"</c:if> ><spring:message code="label.Department" /></th>
							</tr>
						</thead>
						<tbody></tbody>
					</table>
					<div id="search-results-none" class="hideme"><spring:message code="message.NoResultSelected" /></div>	
				</div>
				
				<div id="changeownererror" class="validation-error"><spring:message code="error.nouserselected" /></div>
			</div>
			<div class="modal-footer">
				<a onclick="saveNewOwner();" class="btn btn-primary"><spring:message code="label.OK" /></a>
				<a class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>				
			</div>
			</div>
		</div>
	</div>
	
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
			<a id="confirm-restore-dialog-target"  onclick="checkAliasExistsForRestore(false); return false;" class="btn btn-primary"><spring:message code="label.OK" /></a>
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>				
		</div>
		</div>
		</div>
	</div>
	
	<div class="modal" id="confirm-finaldelete-dialog" data-backdrop="static">
		<div class="modal-dialog">
    	<div class="modal-content">
		<div class="modal-header">
			<b><spring:message code="label.Delete" /></b>
		</div>
		<div class="modal-body">
			<spring:message code="question.FinalDelete" />
		</div>
		<div class="modal-footer">
			<a id="confirm-finaldelete-dialog-target" class="btn btn-primary"><spring:message code="label.OK" /></a>
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>				
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
			<spring:message code="question.RestoreSurveyAlias" /><br /><br />
			<input type="text" id="new-survey-shortname-restore" /><br />
			<spring:message code="message.MeaningfulShortname" />&nbsp;
			<spring:message code="message.MeaningfulShortname2" />
		</div>
		<div class="modal-footer">
			<a  onclick="checkAliasExistsForRestore(true)" class="btn btn-primary"><spring:message code="label.OK" /></a>
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>				
		</div>
		</div>
		</div>
	</div>
	
	<c:if test="${repairedlabels != null}">
		<script>
			showInfo('${repairedlabels}' + ' ' + '<spring:message code="info.LabelsRepaired" />');
		</script>
	</c:if>
	
	<c:if test="${deletedShortname != null}">
		<script type="text/javascript">
			var t = '<spring:message code="message.SurveyDeleted" />';
			showInfo(t.replace(/X/g, '${deletedShortname}'));
		</script>
	</c:if>
	
	<form:form id="load-forms" method="POST" action="${contextpath}/forms" onsubmit="$('#show-wait-image-delete-survey').modal('show');">
		<input type="hidden" name="delete" id="delete" value="" />
		<input type="hidden" name="origin" id="origin" value="surveysearch" />
	</form:form>
</body>
</html>
