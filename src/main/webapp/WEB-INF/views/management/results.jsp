<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Results" /></title>
	<script type="text/javascript" src="${contextpath}/resources/js/Chart.min.js"></script>
	<%@ include file="../includes.jsp" %>
	
	<link href="${contextpath}/resources/css/management.css" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/charts.css" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/results.css" rel="stylesheet" type="text/css" />
	
	<script type="text/javascript" src="${contextpath}/resources/js/d3.v3.min.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/d3.layout.cloud.min.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/wordcloud.js?version=<%@include file="../version.txt" %>"></script>
	
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
		
		.filtercell {
			position: relative;
		}
		
		.filterinfo {
			position: absolute;
			right: 2px;
    		top: -24px;
		}
		
		.white {
			color: #fff !important;
		}
		
	    .filtertools {
			position: absolute;
			right: 12px;
    		top: 18px;
    		margin-top: 0;
		}
		
		.cellcreated, .cellupdated {
			min-width: 260px !important;
		}
		
		.check2 {
			margin-top: -2px !important;
    		vertical-align: middle;
		}
		
		table.table-styled > thead .glyphicon
		{
			 color:#333;
		}
		
		table.table-styled > thead > tr.table-styled-filter th {
		    padding: 8px;
		}
		
		.glyphicon-question-sign, .glyphicon-info-sign, th .glyphicon-remove {
			font-size: 18px;
			vertical-align: bottom;
		}
		
		.statelement-wrapper {
			background: #f8f8f8;
			border: 1px solid #dddddd;
			margin-bottom: 10px;
			margin-left: auto;
			margin-right: auto;
			padding: 16px;
			width: 700px;
		}
		
		.chart-wrapper, .chart-controls {
			float: left;
			display: none;
		}
		
		.chart-controls select {
			width: 100%;
			display: inline-block;
			margin-bottom: 15px;
		}
	
	</style>
	
	<script type="text/javascript"> 
		$(function() {
			
			$("#form-menu-tab").addClass("active");
			$("#results-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");

			<c:if test="${form.survey.isEVote}">
				$(window).on('resize', doResize);
				sneaky = new ScrollSneak(location.hostname);
				$("#results-table").addClass('hidden');
				$('[data-toggle="tooltip"]').tooltip();
				return;
			</c:if>

			loadMore();
			
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
				
			<c:if test="${message == 'success'}">
				showSuccess('<spring:message code="message.ContributionDeleted" />');
			</c:if>
		
			<c:if test="${message == 'failure'}">
				showError('<spring:message code="message.ContributionNotDeleted" />');
			</c:if>
			
			<c:if test="${message == 'recalculatestarted'}">
				showInfo('<spring:message code="message.recalculatestarted" />');
			</c:if>
				
			<c:if test="${deletedAnswers != null}">
				showSuccess('${deletedAnswers}&nbsp;<spring:message code="message.ContributionDeleted" />');
			</c:if>
			
			<c:if test="${columnDeleted != null}">
				showSuccess('<spring:message code="message.ColumnDeleted" />');
			</c:if>
			
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

			$(".ajaxloaderimage").hide();

			<c:if test="${form.survey.isDelphi}">
				loadDelphiStatisticsAsync();
			</c:if>
			
			$(window).on('resize', doResize);
			
			initCheckAll();

			$('[data-toggle="tooltip"]').tooltip();

		});
		
		function showDeleteColumnDialog(uid)
		{
			$('.resultoverlaymenu').hide();
			$('#deleteColumnUID').val(uid);
			$('#confirmDeleteColumnDialog').modal('show')
		}
		
		function checkAndShowMultiDeleteDialog()
		{
			if ($('#btnDeleteSelected').hasClass('disabled'))
			{
				return;	
			}
			
			var selected = $("input.checkDelete:checked").length;
			if (selected > 0)
			{
				$("#agreedelete").prop('checked', false);
				$('#deleteContributionConfirm').addClass('disabled');
				$('#confirmDeleteMultipleDialog').modal('show')
			} else {
				$('#noResultsToDeleteDialog').modal('show');
			}
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
				$("#show-assigned-values-icon").removeClass("disabled");
				$("#dialog-show-assigned-values-true").prop("checked", "checked");
			} else {
				$(".assignedValue").hide();
				$("#show-assigned-values-icon").addClass("disabled");
				$("#dialog-show-assigned-values-false").prop("checked", "checked");
			}
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
		
		function checkColumnConfirmationTicked(input)
		{
			if ($(input).is(':checked')) 
			{ 
				$('#deleteColumnConfirm').removeClass('disabled'); 
			} else {
				$('#deleteColumnConfirm').addClass('disabled');
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
		
		function submitDeleteColumn()
		{

			if (!$("#agreedeletecolumn").is(":checked"))
			{
				return;	
			}
			
			$('#confirmDeleteColumnDialog').modal('hide');
			$("#operation").val("deletecolumn");
			$("#resultsForm").submit();
		}
				
		function switchTo(resultType)
		{
			$.ajax({
				type : "GET",
				url : '${contextpath}/${form.survey.shortname}/management/results/access?type=' + resultType,
				dataType : "json",
				crossDomain:true,
				success : function(data) {
				},
				error : function(data) {
				}
			});
			switch(resultType)
			{
				case 'content':
					hideECF();
					hideECF2();
					hideECF3();
					hideStatisticsQuiz();
					hideStatistics();
					hideStatisticsDelphi();
					showContent();
					adaptScrollArea();
					break;
				case 'statistics':
					hideECF();
					hideECF2();
					hideECF3();
					hideStatisticsQuiz();
					hideStatisticsDelphi();
					hideContent();
					showStatistics();
					break;
				case 'statistics-quiz':
					hideECF();
					hideECF2();
					hideECF3();
					hideStatistics();
					hideContent();
					hideStatisticsDelphi();
					showStatisticsQuiz();
					break;
				case 'statistics-delphi':
					hideECF();
					hideECF2();
					hideECF3();
					hideStatistics();
					hideContent();
					hideStatisticsQuiz();
					showStatisticsDelphi();
					break;
				case 'ecf':
					hideStatisticsQuiz();
					hideStatisticsDelphi();
					hideContent();
					hideStatistics();
					hideECF2();
					hideECF3();
					showECF();
					break;					
				case 'ecf2':
					hideECF();
					hideECF3();
					hideStatisticsQuiz();
					hideStatisticsDelphi();
					hideContent();
					hideStatistics();
					showECF2();
					break;				
				case 'ecf3':
					hideECF();
					hideECF2();
					hideContent();
					hideStatistics();
					hideStatisticsQuiz();
					hideStatisticsDelphi();
					showECF3();
					break;
				case 'quorum':
					$('#results-quorum').show();
					$('#results-seats').hide();
					$('#results-test').hide();
					$('#results-statistics-quorum-link').removeClass("btn-default").addClass("btn-primary");
					$('#results-statistics-seats-link').addClass("btn-default").removeClass("btn-primary");
					$('#results-statistics-test-link').addClass("btn-default").removeClass("btn-primary");
					break;
				case 'seats':
					$('#results-quorum').hide();
					$('#results-seats').show();
					$('#results-test').hide();
					$('#results-statistics-seats-link').removeClass("btn-default").addClass("btn-primary");
					$('#results-statistics-quorum-link').addClass("btn-default").removeClass("btn-primary");
					$('#results-statistics-test-link').addClass("btn-default").removeClass("btn-primary");
					break;
				case 'test':
					$('#results-test').show();
					$('#results-quorum').hide();
					$('#results-seats').hide();
					$('#results-statistics-seats-link').addClass("btn-default").removeClass("btn-primary");
					$('#results-statistics-quorum-link').addClass("btn-default").removeClass("btn-primary");
					$('#results-statistics-test-link').removeClass("btn-default").addClass("btn-primary");
					break;
			}
			
			$('#resultType').val(resultType);
			//checkDeleteBoxes();			
		}
		
		// Shows all the elements present in the content
		function showContent() {
			$("#results-table-link").addClass("btn-primary");
			$("#results-table").find("tbody").removeClass('hidden');
			$("#results-table").find(".RowsPerPage").removeClass('hidden');
			$("#pager").removeClass('hidden');
					$("#results-statistics-delphi").addClass('hidden');
			$("#content-export-buttons").removeClass('hidden');
			$("#scrollarea").show();
			
			// Scrollareaheader
			$("#scrollareaheader").css({ 'overflow-x' : 'hidden'});

			$("#scrollareaheader").scrollLeft($("#scrollarea").scrollLeft());
			
			if ($('#scrollarea').hasScrollBar())
			{
				$("#scrollareaheader").css("overflow-y","scroll");
			} else {
				$("#scrollareaheader").css("overflow-y","auto");
			}
			  
			resetSliderPositions($("#contentstable"));
					$("#results-statistics-delphi-link").removeClass("btn-primary").addClass("btn-default");
			
			// Nothing in hide
			$("#search-buttons").show();				
					$("#results-statistics-delphi").addClass('hidden');
			
			// Strange common to all?
			$("#results-charts").addClass('hidden');
			$("#charts-export-buttons").addClass('hidden');
		}
		
		
		// Hides all the elements present in the content
		function hideContent() {
			$("#results-table-link").removeClass("btn-primary").addClass("btn-default");
			// $("#results-table").find("tbody").addClass('hidden');
			$("#results-table").find(".RowsPerPage").addClass('hidden');
			$("#pager").addClass('hidden');
			$("#content-export-buttons").addClass('hidden');
			$("#scrollarea").hide();
			
			$("#scrollareaheader").css("overflow-x", "auto");
			$("#scrollareaheader").css("overflow-y","auto");
		}

		var statisticsLoaded = false;

		function showStatistics() {
			$("#results-statistics-link").addClass("btn-primary");
			$("#results-statistics").removeClass('hidden');
			$("#statistics-export-buttons").removeClass('hidden');
			$("#results-statistics-delphi-link").removeClass("btn-primary").addClass("btn-default");
			
			$("#results-charts").addClass('hidden');
			$("#charts-export-buttons").addClass('hidden');

			$("#scrollareaheader").css({ 'overflow-x' : 'auto'})

            if (!statisticsLoaded) {
		        loadStatisticsAsync(false);
			    delphiPopulateAllGraphs($("#results-statistics"));
			    statisticsLoaded = true;
			}
			doResize();
		}
		
		function hideStatistics() {
			$("#results-statistics-link").removeClass("btn-primary").addClass("btn-default");
			$("#results-statistics").addClass('hidden');
			$("#statistics-export-buttons").addClass('hidden');
		}
		
		function showStatisticsQuiz() {
			$("#results-statistics-quiz-link").addClass("btn-primary");
			$("#results-statistics-quiz").removeClass('hidden');
			$("#statistics-quiz-export-buttons").removeClass('hidden');

			$("#scrollareaheader").css({ 'overflow-x' : 'auto'})

			// Strange?
			$("#results-charts").addClass('hidden');
			$("#charts-export-buttons").addClass('hidden');
		}
		function hideStatisticsQuiz() {
			$("#results-statistics-quiz-link").removeClass("btn-primary").addClass("btn-default");
			$("#results-statistics-quiz").addClass('hidden');
			$("#statistics-quiz-export-buttons").addClass('hidden');
		}
			
		function showStatisticsDelphi() {
			$("#results-statistics-delphi-link").addClass("btn-primary");
			$("#results-statistics-delphi").removeClass('hidden');

			$("#scrollareaheader").css({ 'overflow-x' : 'auto'})
		}
		
		function hideStatisticsDelphi() {
			$("#results-statistics-delphi-link").removeClass("btn-primary").addClass("btn-default");
			$("#results-statistics-delphi").addClass('hidden');
		}
		
		function showECF() {
			$("#results-table").addClass('hidden');
			$("#results-ecf").addClass("btn-primary");
			$("#ecf-results").removeClass('hidden');
			$("#ecf1-export-buttons").removeClass('hidden');
   
			// Strange?
			$("#results-charts").addClass('hidden');
			$("#charts-export-buttons").addClass('hidden');			
		}
		
		function hideECF() {
			$("#results-table").removeClass('hidden');
			$("#ecf-results").addClass('hidden');
			$("#results-ecf").removeClass("btn-primary").addClass("btn-default");
			$("#ecf1-export-buttons").addClass('hidden');
		}
		
		function showECF2() {
			$("#results-table").addClass('hidden');
			$("#results-ecf2").addClass("btn-primary");
			$("#ecf-results2").removeClass('hidden');
			$("#ecf2-export-buttons").removeClass('hidden');
   
			// Strange?
			$("#results-charts").addClass('hidden');
			$("#charts-export-buttons").addClass('hidden');
		}
		
		function hideECF2() {
			$("#results-table").removeClass('hidden');
			$("#ecf-results2").addClass('hidden');
			$("#results-ecf2").removeClass("btn-primary").addClass("btn-default");
			$("#ecf2-export-buttons").addClass('hidden');
		}
		
		function showECF3() {
			$("#results-table").addClass('hidden');
			$("#results-ecf3").addClass("btn-primary");
			$("#ecf-results3").removeClass('hidden');
			$("#ecf3-export-buttons").removeClass('hidden');

			// Strange?
			$("#results-charts").addClass('hidden');
			$("#charts-export-buttons").addClass('hidden');
		}

		function hideECF3() {
			$("#results-table").removeClass('hidden');
			$("#ecf-results3").addClass('hidden');
			$("#results-ecf3").removeClass("btn-primary").addClass("btn-default");
			$("#ecf3-export-buttons").addClass('hidden');
		}
		
		var exportType;
		//var exportFormat;
		
		function showExportDialog(type)
		{
			exportType = type;
			$('#exportnt-name').val("");
			
			if (type == "Content")
			{
				$('#exportnt-format-ecf1').hide();
				$('#exportnt-format-ecf2').hide();
				$('#exportnt-format-ecf3').hide();
				$('#exportnt-format-content').show();
				$('#exportnt-format-statistics').hide();
				$('#exportnt-format-statistics-quiz').hide();		
				$('#exportnt-format-files').hide();

				$('#exportnt-format-content').trigger("change");
			} else if (type == "Statistics")
			{
				$('#exportnt-format-ecf1').hide();
				$('#exportnt-format-ecf2').hide();
				$('#exportnt-format-ecf3').hide();
				$('#exportnt-format-content').hide();
				$('#export-split-mcq-row').hide()
				$('#exportnt-format-statistics').show();
				$('#exportnt-format-statistics-quiz').hide();
				$('#exportnt-format-files').hide();
			} else if (type == "StatisticsQuiz")
			{
				$('#exportnt-format-ecf1').hide();
				$('#exportnt-format-ecf2').hide();
				$('#exportnt-format-ecf3').hide();
				$('#exportnt-format-content').hide();
				$('#export-split-mcq-row').hide()
				$('#exportnt-format-statistics').hide();
				$('#exportnt-format-statistics-quiz').show();
				$('#exportnt-format-files').hide();
			} else if (type == "ECFGlobalResults") {
				$('#exportnt-format-content').hide();
				$('#export-split-mcq-row').hide()
				$('#exportnt-format-ecf1').show();
				$('#exportnt-format-ecf2').hide();
				$('#exportnt-format-ecf3').hide();
				$('#exportnt-format-statistics').hide();
				$('#exportnt-format-statistics-quiz').hide();
				$('#exportnt-format-files').hide();
			} else if (type == "ECFProfileResults") {
				$('#exportnt-format-ecf1').hide();
				$('#exportnt-format-ecf2').show();
				$('#exportnt-format-ecf3').hide();
				$('#exportnt-format-content').hide();
				$('#export-split-mcq-row').hide()
				$('#exportnt-format-statistics').hide();
				$('#exportnt-format-statistics-quiz').hide();
				$('#exportnt-format-files').hide();
			} else if (type == "ECFOrganizationResults") {
				$('#exportnt-format-ecf1').hide();
				$('#exportnt-format-ecf2').hide();
				$('#exportnt-format-ecf3').show();
				$('#exportnt-format-content').hide();
				$('#export-split-mcq-row').hide()
				$('#exportnt-format-statistics').hide();
				$('#exportnt-format-statistics-quiz').hide();
				$('#exportnt-format-files').hide();
			} else if (type.indexOf("Files") == 0) {
				$('#exportnt-format-ecf1').hide();
				$('#exportnt-format-ecf2').hide();
				$('#exportnt-format-ecf3').hide();
				$('#exportnt-format-content').hide();
				$('#export-split-mcq-row').hide()
				$('#exportnt-format-statistics').hide();
				$('#exportnt-format-statistics-quiz').hide();
				$('#exportnt-format-files').show();
			}
			
			$('#export-name-type-dialog').find(".validation-error").hide();
			$('#export-name-type-dialog').modal();	
			$('#export-name-type-dialog').find("input").first().focus();
		}
		
		function checkAndStartExportNT(name)
		 {			 
			$("#export-name-type-dialog").find(".validation-error").hide();
			 
			if (name === null || name.trim().length === 0)
			{
	            $("#export-name-type-dialog").find("#validation-error-required").show();
	            return;
	        }
		
	        var reg = /^[a-zA-Z0-9-_\.]+$/;
	        if( !reg.test( name ) ) {
	            $("#export-name-type-dialog").find("#validation-error-exportname").show();
	            return;		  
	        };
	        
	        var format = "";
	        if (exportType === "Content")
	        {
	        	format = $('#exportnt-format-content').val();
	        } else if (exportType === "Statistics")
	        {
	        	format = $('#exportnt-format-statistics').val();
	        } else if (exportType === "StatisticsQuiz")
	        {
	        	format = $('#exportnt-format-statistics-quiz').val();
	        }
	        else if (exportType === "ECFGlobalResults")
	        {
	        	format = $('#exportnt-format-ecf1').val();
	        } else if (exportType === "ECFProfileResults")
	        {
	        	format = $('#exportnt-format-ecf2').val();
	        } else if (exportType === "ECFOrganizationResults")
	        {
	        	format = $('#exportnt-format-ecf3').val();
	        } else if (exportType.indexOf("Files") == 0) {
	        	format = "zip";
	        }

	        if (format === 'pdfReport') {
				createPDFReport(name);
			} else {
				startExport(name, format);
			}

	        $("#export-name-type-dialog").modal("hide");
		 }
		
		function startExport(name, format)
		{
			var showshortnames = $("#show-assigned-values").is(":checked");
			var allanswers = $("#allAnswers").val();
			var splitMCQ = $("#export-split-mcq").is(":checked")
			// check again for new exports
			window.checkExport = true;
			
			$.ajax({
	           type: "POST",
	           url: "${contextpath}/exports/start/" + exportType + "/" + format,
	           data: {exportName: name, showShortnames: showshortnames, allAnswers: allanswers, splitMCQ: splitMCQ, group: ""},
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
		
		function createPDFReport(exportName) {
			var showshortnames = $("#show-assigned-values").is(":checked");
			var allanswers = $("#allAnswers").val();
			
			var charts = {};

			if (exportName == null) {
				exportName = "PDF Report";
			}
			
			$('.chart-download:visible').each(function(){
				var questionUid = $(this).closest(".chart-wrapper").attr("data-question-uid");
				var href = $(this).attr("href");
				charts[questionUid] = href;
			});
			
			$.ajax({
	           type: "POST",
	           url: "${contextpath}/exports/start/PDFReport/pdf",
	           data: {exportName: exportName, showShortnames: showshortnames, allAnswers: allanswers, group: "", charts: JSON.stringify(charts)},
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
					    a = parseFloat($(a).attr("data-value"));
					    b = parseFloat($(b).attr("data-value"));
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
		
		function deleteContribution(active) {
		
			$('#delete-contribution-dialog').modal('hide');
			$('#show-wait-image').modal('show');

			var appendToURL = "";
			if (active == false)
				appendToURL = "?results-source=draft"
			// DELETE <url/eusurvey/contribution/UID>
			$.ajax({
				type:'DELETE',
				  url: '<c:url value="/contribution/"/>' + deletionCode + appendToURL,
				  beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
				  cache: false,
				  success: function( data ) {						  
					$('#message').val("success");	
				  },
				  error: function(data) {
					$('#message').val("failure");
				  },
				  complete: function(data) {
					$('#resultsForm').submit();
				  }			
				}
			);	
			
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
			checkAssignedValues();

			$(div).find("input[type=checkbox]").each(function(){
				if ($(this)[0].hasAttribute("data-checked"))
				{
					$(this).prop("checked", "checked");
				} else {
					if (!$($(this)[0]).hasClass("always-checked")) {
						$(this).removeAttr("checked");
					}
				}
			});

			initCheckAll();
		}

		function requestCodaDashboard(){
			$("#request-coda-dashboard-dialog").modal("hide");
			$("#coda-link-button").addClass("disabled");
			$("#coda-link-button").attr("onclick", "");
			$.ajax({
				type: "POST",
				url: "${contextpath}/${form.survey.shortname}/management/requestCodaDashboard",
				beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
				success: function(data){
					if (data === true) {
						showSuccess("<spring:message code="message.CodaRequestSuccess" />")
					} else {
						showError("<spring:message code="message.CodaRequestError" />")
					}
				},
				error: function () {
					showError("<spring:message code="message.CodaRequestError" />")
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

		function getLabel(s){
			if (s == "NumberOfReceivedVotes") return '<spring:message code="label.NumberOfReceivedVotes" />';

			return "unknown label";
		}
		
		function copyBase64ImageToClipboard(base64) {
			base64 = base64.replace("data:image/png;base64,", "");
			const byteCharacters = atob(base64);
			const byteNumbers = new Array(byteCharacters.length);
			for (let i = 0; i < byteCharacters.length; i++) {
			    byteNumbers[i] = byteCharacters.charCodeAt(i);
			}
			const byteArray = new Uint8Array(byteNumbers);
			const blob = new Blob([byteArray], {type: 'image/png'});
			
			try {
				navigator.clipboard.write([new ClipboardItem({'image/png': blob})]);				
			} catch (e) {
				showError("<spring:message code="error.CopyToClipboardFailed" />");
			}
			
		}

		function settingsSubmit(){

			//Find tds which are the first in their row (first-child, aka first column) that have a checked child (has(:checked))
			const areAnyShown = $("#tblConfigurationFromResult").find("tr > td:first-child:has(:checked)").length > 0

			//Same as above but for second column
			const areAnyExported = $("#tblConfigurationFromResult").find("tr > td:nth-child(2):has(:checked)").length > 0

			if (!areAnyShown) {
				showError('<spring:message code="message.PleaseShowAnyQuestion" />')
				return
			}

			if (!areAnyExported) {
				showError('<spring:message code="message.PleaseExportAnyQuestion" />')
				return
			}

			$('#resultsFormMode').val('configure');
			$('#configure-columns-dialog').modal('hide');
			$('#resultsForm').submit();
		}
		
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
	
	<form:form modelAttribute="paging" id="resultsForm" method="POST" action="${contextpath}/${sessioninfo.shortname}/management/results" style="padding-top: 20px; padding-bottom: 0px;">
		<input type="hidden" name="operation" id="operation" />
		<input type="hidden" name="sort" id="sort" />
		<input type="hidden" id="deleteColumnUID" name="deleteColumnUID" />
		
		<div class="fixedtitleform" style="padding-left: 10px; padding-right: 10px; padding-bottom:7px; border-bottom: 0px solid #ddd;">
					
			<div style="vertical-align: middle; margin-left: auto; margin-right: auto; margin-top: 10px;">		
				<div style="float: left; margin-top: 0px; margin-bottom: 0px;">
					<c:choose>
						<c:when test="${form.survey.isEVote}">
							<a data-toggle="tooltip" data-placement="bottom" title="<spring:message code="label.Quorum" />" id="results-statistics-quorum-link" class="btn btn-default btn-primary" onclick="switchTo('quorum');"><img src="${contextpath}/resources/images/icons/24/quorum.png" /></a>
							
							<c:if test="${!published}">
								<a data-toggle="tooltip" data-placement="bottom" title="<spring:message code="label.Results" />" id="results-statistics-seats-link" style="display: none" class="btn btn-default btn-default" onclick="switchTo('seats');"><img src="${contextpath}/resources/images/icons/24/people.png" /></a>
							</c:if>

							<c:if test="${form.survey.showResultsTestPage && !published}">
									<a data-toggle="tooltip" data-placement="bottom" title="<spring:message code="label.Simulation" />" id="results-statistics-test-link" style="width: 50px; height: 38px; color: #555;" class="btn btn-default btn-default" onclick="switchTo('test');">
										<svg xmlns="http://www.w3.org/2000/svg" width="19" height="19" style="vertical-align: middle" fill="currentColor" class="bi bi-clipboard2-check" viewBox="0 0 16 16">
										  <path d="M9.5 0a.5.5 0 0 1 .5.5.5.5 0 0 0 .5.5.5.5 0 0 1 .5.5V2a.5.5 0 0 1-.5.5h-5A.5.5 0 0 1 5 2v-.5a.5.5 0 0 1 .5-.5.5.5 0 0 0 .5-.5.5.5 0 0 1 .5-.5h3Z"/>
										  <path d="M3 2.5a.5.5 0 0 1 .5-.5H4a.5.5 0 0 0 0-1h-.5A1.5 1.5 0 0 0 2 2.5v12A1.5 1.5 0 0 0 3.5 16h9a1.5 1.5 0 0 0 1.5-1.5v-12A1.5 1.5 0 0 0 12.5 1H12a.5.5 0 0 0 0 1h.5a.5.5 0 0 1 .5.5v12a.5.5 0 0 1-.5.5h-9a.5.5 0 0 1-.5-.5v-12Z"/>
										  <path d="M10.854 7.854a.5.5 0 0 0-.708-.708L7.5 9.793 6.354 8.646a.5.5 0 1 0-.708.708l1.5 1.5a.5.5 0 0 0 .708 0l3-3Z"/>
										</svg>
									</a>
							</c:if>			
						</c:when>
						<c:otherwise>
							<a data-toggle="tooltip" data-placement="bottom" title="<spring:message code="label.Results" />" id="results-table-link" class="btn btn-xs btn-primary" onclick="switchTo('content');"><img src="${contextpath}/resources/images/icons/24/table.png" /></a>
							<a data-toggle="tooltip" data-placement="bottom" title="<spring:message code="label.Statistics" />" id="results-statistics-link" class="btn btn-default btn-xs" onclick="switchTo('statistics');"><img src="${contextpath}/resources/images/icons/24/percentage.png" /></a>
							<c:if test="${form.survey.isQuiz}">
								<a data-toggle="tooltip" data-placement="bottom" title="<spring:message code="label.QuizResultPage" />" id="results-statistics-quiz-link" class="btn btn-default btn-xs" onclick="switchTo('statistics-quiz');"><span class="glyphicon glyphicon-education" style="font-size: 19px; color: #333"></span></a>
							</c:if>
							<c:if test="${form.survey.isDelphi}">
								<a data-toggle="tooltip" data-placement="bottom" title="<spring:message code="label.CompletionRate" />" id="results-statistics-delphi-link" class="btn btn-default btn-xs" onclick="switchTo('statistics-delphi');"><img src="${contextpath}/resources/images/icons/24/delphi.png" /></a>
							</c:if>
							<c:if test="${form.survey.isECF}">
								<a data-toggle="tooltip" data-placement="bottom" title="<spring:message code="label.ECF.Results" />" id="results-ecf" class="btn btn-default btn-xs" onclick="switchTo('ecf');"><span class="glyphicon glyphicon-user" style="font-size: 19px; color: #333"></span></a>
								<a data-toggle="tooltip" data-placement="bottom" title="<spring:message code="label.ECF.Results2" />" id="results-ecf2" class="btn btn-default btn-xs" onclick="switchTo('ecf2');"><span class="glyphicon glyphicon-eye-open" style="font-size: 19px; color: #333"></span></a>
								<a data-toggle="tooltip" data-placement="bottom" title="<spring:message code="label.ECF.Results3" />" id="results-ecf3" class="btn btn-default btn-xs" onclick="switchTo('ecf3');"><span class="glyphicon glyphicon-globe" style="font-size: 19px; color: #333"></span></a>
							</c:if>
							<c:if test="${form.codaEnabled}">
								<c:choose>
								<c:when test="${form.survey.codaWaiting}" >
									<button type="button" data-toggle="tooltip" data-placement="bottom" title="<spring:message code="message.CodaRequestSuccess" />" id="coda-link-button" class="btn btn-default disabled" ><spring:message code="label.CodaCreateAnalytics" /></button>
								</c:when>
								<c:when test="${form.survey.codaLink != null && form.survey.codaLink.length() > 0}">
									<a href="${form.survey.codaLink}" target="_blank" data-placement="bottom" id="coda-link-a" class="btn btn-default" ><spring:message code="label.CodaOpenAnalytics" /></a>
								</c:when>
								<c:otherwise>
									<c:if test="${form.survey.owner.id == userid}">
										<button type="button" onclick="$('#request-coda-dashboard-dialog').modal();" data-placement="bottom" id="coda-link-button" class="btn btn-default" ><spring:message code="label.CodaCreateAnalytics" /></button>
									</c:if>
								</c:otherwise>
								</c:choose>
							</c:if>
						</c:otherwise>
					</c:choose>
				</div>
				
				<div style="float: left; margin-top: 0px; margin-right: 20px;" id="results-source-dropdown">
					<c:if test="${!form.survey.isEVote}">
						<select onchange="$('#resultsForm').submit();" class="form-control" name="results-source" id="results-source" style="width: auto; margin-bottom: 0px; margin-left: 10px;">
							<c:choose>
								<c:when test="${!sessioninfo.owner.equals(USER.id) && USER.formPrivilege < 2 && USER.getLocalPrivilegeValue('AccessResults') < 1 && USER.getResultAccess() == null}">
									<option selected="selected" value="draft"><spring:message code="label.TestAnswers" /></option>
								</c:when>
								<c:when test="${sessioninfo.owner.equals(USER.id) || USER.formPrivilege > 1 ||
												USER.getLocalPrivilegeValue('FormManagement') > 1 ||
												USER.getLocalPrivilegeValue('AccessResults') > 0 ||
												USER.getResultAccess() != null}">
									<c:choose>
										<c:when test="${!form.getSurvey().getIsDraft() || form.getSurvey().getIsPublished()}">
											<option value="draft"><spring:message code="label.TestAnswers" /></option>
											<option selected="selected" value="active"><spring:message code="label.Contributions" /></option>
											<option value="allanswers"><spring:message code="label.ContributionsIncludingDeletedQuestions" /></option>
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
					</c:if>
				</div>
				<div style="margin-top: 2px; margin-right: 10px; float: right;" id="results-view-settings">
					<c:if test="${!form.survey.isEVote}">
						<a class="btn btn-default" id="btnConfigureFromResult" onclick="$('#configure-columns-dialog').modal('show')"><spring:message code="label.Settings" /></a>

						<c:choose>
							<c:when test="${resultsShowAssignedValues}">
								<input type="checkbox" style="display: none" id="show-assigned-values" checked="checked" />
							</c:when>
							<c:otherwise>
								<input type="checkbox" style="display: none" id="show-assigned-values" />
							</c:otherwise>
						</c:choose>

						 <c:choose>
						 	<c:when test="${publication == null && (sessioninfo.owner == USER.id || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('AccessResults') > 1)}">
								<input checked="checked" value="true" name="show-delete-checkboxes" type="checkbox" class="hideme" id="show-delete-checkboxes" />
							</c:when>
							<c:otherwise>
								<input value="true" name="show-delete-checkboxes" type="checkbox" class="hideme" id="show-delete-checkboxes" />
							</c:otherwise>
						</c:choose>

						<span id="content-export-buttons">
							<span class="deactivatedexports">
								<a class="btn btn-default disabled"><spring:message code="label.Export" /></a>
							</span>
							<span class="activatedexports hideme">
								<a class="btn btn-default" onclick="showExportDialog('Content')"><spring:message code="label.Export" /></a>
							</span>
						</span>

						<span id="statistics-export-buttons" class="hidden">
							<c:choose>
								<c:when test="${form.getSurvey().hasNoQuestionsForStatistics()}">
									<a class="btn btn-default disabled"><spring:message code="label.Export" /></a>
								</c:when>
									<c:otherwise>
									<span class="deactivatedexports">
										<a class="btn btn-default disabled"><spring:message code="label.Export" /></a>
									</span>
									<c:if test="${!form.survey.isDelphi}">
										<span class="deactivatedexports">
											<a class="btn btn-default disabled"><spring:message code="label.PDFReport" /></a>
										</span>
									</c:if>
									<span class="activatedexports hideme">
										<a class="btn btn-default" onclick="showExportDialog('Statistics')"><spring:message code="label.Export" /></a>
									</span>
									<c:if test="${!form.survey.isDelphi}">
										<span class="activatedexports hideme">
											<a class="btn btn-default" onclick="createPDFReport()"><spring:message code="label.PDFReport" /></a>
										</span>
									</c:if>
								</c:otherwise>
							</c:choose>
						</span>

						<span id="statistics-quiz-export-buttons" class="hidden">
							<span class="deactivatedexports">
								<a class="btn btn-default disabled"><spring:message code="label.Export" /></a>
							</span>
							<span class="activatedexports hideme">
								<a class="btn btn-default" onclick="showExportDialog('StatisticsQuiz')"><spring:message code="label.Export" /></a>
							</span>
						</span>

						<span id="ecf1-export-buttons" class="hidden">
							<span class="deactivatedexports">
								<a class="btn btn-default disabled"><spring:message code="label.Export" /></a>
							</span>
							<span class="activatedexports hideme">
								<a class="btn btn-default" onclick="showExportDialog('ECFGlobalResults')"><spring:message code="label.Export" /></a>
							</span>
						</span>

						<span id="ecf2-export-buttons" class="hidden">
							<span class="deactivatedexports">
								<a class="btn btn-default disabled"><spring:message code="label.Export" /></a>
							</span>
							<span class="activatedexports hideme">
								<a class="btn btn-default" onclick="showExportDialog('ECFProfileResults')"><spring:message code="label.Export" /></a>
							</span>
						</span>

						<span id="ecf3-export-buttons" class="hidden">
							<span class="deactivatedexports">
								<a class="btn btn-default disabled"><spring:message code="label.Export" /></a>
							</span>
							<span class="activatedexports hideme">
								<a class="btn btn-default" onclick="showExportDialog('ECFOrganizationResults')"><spring:message code="label.Export" /></a>
							</span>
						</span>
					</c:if>
				</div>	
				
				<div style="clear: both"></div>	
			</div>
			
			<div style="clear: both"></div>
		</div>			
		
		<div class="fullpageform" style="padding-top: 170px; padding-bottom: 0px;">
			<div>
				<input type="hidden" id="message" name="message" />
				<input type="hidden" id="resultType" name="resultType" />
				<input type="hidden" name="active" value="${active}" />
				<input type="hidden" id="allAnswers" name="allAnswers" value="${allanswers}"/>
				<input type="hidden" id="resultsFormMode" name="resultsFormMode" value="" />
				
				<c:set var="questions" value="${form.getSurvey().getQuestions()}" />			
				<c:set var="pagingElementName" value="Answerset" />		

				<c:if test="${form.survey.isEVote}">
					<%@ include file="results-quorum.jsp" %>
					
					<c:if test="${!published}">
						<%@ include file="results-seats.jsp" %>
					</c:if>
					<c:if test="${form.survey.showResultsTestPage}">
						<%@ include file="results-test.jsp" %>
					</c:if>
					
				</c:if>
				<%@ include file="results-content.jsp" %>
				<%@ include file="results-statistics.jsp" %>
				<%@ include file="results-statistics-quiz.jsp" %>
				<%@ include file="results-statistics-delphi.jsp" %>
				<%@ include file="results-ajax.jsp" %>
				<c:if test="${form.survey.isECF}">
					<%@ include file="results-ecf.jsp" %>
					<%@ include file="results-ecf2.jsp" %>
					<%@ include file="results-ecf3.jsp" %>
				</c:if>
			</div>
		</div>

	</form:form>
	
	<div style="clear: both; height: 10px;"></div>

<%@ include file="../includes2.jsp" %>

	<div class="modal" id="configure-columns-dialog" data-backdrop="static">
		<div class="modal-dialog">
    	<div class="modal-content">
		<div class="modal-header">
			<spring:message code="label.Settings" />
		</div>
		<div class="modal-body">
			<table class="table table-condensed table-bordered">
				<tr>
					<td><spring:message code="label.ShowAssignedValues" /></td>
					<td style="padding-left: 10px;">
						<input id="dialog-show-assigned-values-true" value="true" name="dialog-show-assigned-values" type="radio" class="check"><spring:message code="label.Yes" />
						&nbsp;&nbsp;
						<input id="dialog-show-assigned-values-false" value="false" name="dialog-show-assigned-values" type="radio" class="check"><spring:message code="label.No" />
					</td>
				</tr>
			</table>
		
			<div style="margin-top: 10px; margin-bottom: 10px">
				<b><spring:message code="message.SelectResultQuestions" /></b>
				<div class="help"><spring:message code="info.SelectResultQuestions" /></div>
			</div>
		
			<div style="max-height: 500px; overflow: auto;">
			
			<table class="table table-bordered table-striped table-styled" id="tblConfigurationFromResult">	
				<thead>
					<tr>
						<th style="min-width: 100px;">
							<input type="checkbox" class="check2" id="chkallShow" onclick="checkAllShow()" />
							<spring:message code="label.Show" />
						</th>
						<th style="min-width: 100px;">
							<input type="checkbox" class="check2" id="chkallExport" onclick="checkAllExport()" />
							<spring:message code="label.Export" />
						</th>
						<th><spring:message code="label.Element" /></th>
					</tr>
				<thead>
				<tbody>
					<c:forEach items="${questions}" var="question">
						<c:if test="${question.getType() != 'Image' && question.getType() != 'Text' && question.getType() != 'Download' && question.getType() != 'Confirmation'  && question.getType() != 'Ruler' && !(question.getType() == 'GalleryQuestion' && !question.selection) }">
							<tr>
								<td style="vertical-align: top;"><input name="selected${question.id}" <c:if test="${filter.visible(question.id.toString())}">checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="${question.id}" /></td>
								<td style="vertical-align: top; "><input name="exportselected${question.id}" <c:if test="${filter.exported(question.id.toString())}">checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="exported${question.id}" /></td>
								<td>${question.title.length() > 0? question.getStrippedTitleAtMost100() : question.shortname}</td>
							</tr>
						</c:if>
						
						<c:if test="${question.getIsDelphiQuestion()}">
							<tr>
								<td style="vertical-align: top;"><input name="selectedexplanation${question.id}" <c:if test="${filter.explanationVisible(question.id.toString())}">checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="explanation${question.id}" /></td>
								<td style="vertical-align: top; "><input name="exportselectedexplanation${question.id}" <c:if test="${filter.explanationExported(question.id.toString())}">checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="explanationexported${question.id}" /></td>

								<td style="padding-left: 20px;"><spring:message code="label.Explanation" />, <spring:message code="label.Likes" /></td>
							</tr>
							<tr>
								<td style="vertical-align: top;"><input name="selecteddiscussion${question.id}" <c:if test="${filter.discussionVisible(question.id.toString())}">checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="discussion${question.id}" /></td>
								<td style="vertical-align: top; "><input name="exportselecteddiscussion${question.id}" <c:if test="${filter.discussionExported(question.id.toString())}">checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="discussionexported${question.id}" /></td>

								<td style="padding-left: 20px;"><spring:message code="label.Discussion" /></td>
							</tr>
						</c:if>
					</c:forEach>
					
					<tr>
						<td style="vertical-align: top;"><input name="selectedinvitation" <c:if test='${filter.visible("invitation")}'>checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="invitation" /></td>
						<td style="vertical-align: top;"><input name="exportselectedinvitation" <c:if test='${filter.exported("invitation")}'>checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="exportedinvitation" /></td>
						<td><spring:message code="label.InvitationNumber" /></td>
					</tr>
					<tr>
						<td style="vertical-align: top;"><input name="selectedcase" <c:if test='${filter.visible("case")}'>checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="case" /></td>
						<td style="vertical-align: top;"><input name="exportselectedcase" <c:if test='${filter.exported("case")}'>checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="exportedcase" /></td>
						<td><spring:message code="label.ContributionId" /></td>
					</tr>
					<tr>
						<td style="vertical-align: top;"><input name="selecteduser" <c:if test='${filter.visible("user")}'>checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="user" /></td>
						<td style="vertical-align: top;"><input name="exportselecteduser" <c:if test='${filter.exported("user")}'>checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="exporteduser" /></td>
						<td><spring:message code="label.UserName" /></td>
					</tr>
					<tr>
						<td style="vertical-align: top;"><input name="selectedcreated" <c:if test='${filter.visible("created")}'>checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="created" /></td>
						<td style="vertical-align: top;"><input name="exportselectedcreated" <c:if test='${filter.exported("created")}'>checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="exportedcreated" /></td>
						<td><spring:message code="label.CreationDate" /></td>
					</tr>
					<tr>
						<td style="vertical-align: top;"><input name="selectedupdated" <c:if test='${filter.visible("updated")}'>checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="updated" /></td>
						<td style="vertical-align: top;"><input name="exportselectedupdated" <c:if test='${filter.exported("updated")}'>checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="exportedupdated" /></td>
						<td><spring:message code="label.LastUpdate" /></td>
					</tr>
					<tr>
						<td style="vertical-align: top;"><input name="selectedlanguages" <c:if test='${filter.visible("languages")}'>checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="languages" /></td>
						<td style="vertical-align: top;"><input name="exportselectedlanguages" <c:if test='${filter.exported("languages")}'>checked="checked" data-checked="checked"</c:if> type="checkbox" class="check" id="exportedlanguages" /></td>
						<td><spring:message code="label.Languages" /></td>
					</tr>
						
					<c:if test="${form.getSurvey().isQuiz}">
						<tr>
							<td style="vertical-align: top;"><input checked="checked" disabled="disabled" type="checkbox" class="check always-checked" /></td>
							<td style="vertical-align: top;"><input checked="checked" disabled="disabled" type="checkbox" class="check always-checked" /></td>
							<td><spring:message code="label.TotalScore" /></td>
						</tr>
					</c:if>
						
					</tbody>
			</table>
			</div>
		</div>
		<div class="modal-footer">
			<a id="btnOkFromConfigurationResult" onclick="settingsSubmit()" class="btn btn-primary" ><spring:message code="label.OK" /></a>
			<a class="btn btn-default" onclick="resetSelections($('#tblConfigurationFromResult'))" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
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
			<a id="deleteSingleContributionConfirm" onclick="deleteContribution(${active})"  class="btn btn-primary"><spring:message code="label.Yes" /></a>
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
			<a id="deleteContributionConfirm" onclick="submitDeleteMultiple()"  class="btn disabled btn-primary"><spring:message code="label.Yes" /></a>
			<a id="deleteContributionCancel"  class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>					
		</div>
		</div>
		</div>
	</div>
	
	<div class="modal" id="confirmDeleteColumnDialog" data-backdrop="static">
		<div class="modal-dialog">
    	<div class="modal-content">
		<div class="modal-body">
			<spring:message code="question.DeleteColumns" />
			<br /><br />
			<span style="color: #c11c1c;  font-size: 40px; margin-left: 20px;" class="glyphicon glyphicon-exclamation-sign"></span>
			<div style="display: inline-block; vertical-align: top; padding-top: 10px;">
				<input style="margin-left: 30px;" type="checkbox" id="agreedeletecolumn" onclick="checkColumnConfirmationTicked(this)" /> <spring:message code="label.Imsure" />
			</div>
			<br /><br />
		</div>
		<div class="modal-footer">
			<img id="delete-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a id="deleteColumnConfirm" onclick="submitDeleteColumn()"  class="btn disabled btn-primary"><spring:message code="label.Delete" /></a>
			<a id="deleteColumnCancel"  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>					
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
			<a  class="btn btn-primary" data-dismiss="modal"><spring:message code="label.Cancel" /></a>			
		</div>
		</div>
		</div>
	</div>
	
	<div class="modal" id="export-name-type-dialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
   		<div class="modal-content">
		<div class="modal-header" style="font-weight: bold;">
			<spring:message code="label.Export" />
		</div>
		<div class="modal-body" style="padding-left: 10px;">
			<table>
				<tr>
					<td style="vertical-align: top;">
						<span class="mandatory">*</span>
					</td>
					<td style="vertical-align: top;">
						<label for="exportnt-name" style="display:inline"><spring:message code="label.ExportName2" /></label>
					</td>
					<td style="vertical-align: top;">
						<div style="margin-left: 20px; margin-bottom: 10px;">
							<input class="form-control" type="text" id="exportnt-name" maxlength="255" style="width:150px;" />
							<span id="validation-error-required" class="validation-error hideme"><spring:message code="validation.required" /></span>
							<span id="validation-error-exportname" class="validation-error hideme"><spring:message code="validation.name2" /></span>
						</div>
					</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<label for="exportnt-name" style="display:inline"><spring:message code="label.Format" /></label>
					</td>
					<td>
						<div style="margin-left: 20px">
							<select class="form-control" style="width:150px;" id="exportnt-format-content"
								onchange="if (this.value.startsWith('xls')) $('#export-split-mcq-row').show(); else $('#export-split-mcq-row').hide();">

								<option value="xlsx">XLSX</option>
								<option value="ods">ODS</option>
								<option value="xml">XML</option>
								<c:if test="${!allanswers}">
									<option value="pdf">PDF</option>
			 					</c:if>
							</select>
							<select class="form-control" id="exportnt-format-statistics">
								<option value="pdf">PDF</option>
								<c:if test="${form.survey.isDelphi}">
									<option value="pdfReport">PDF (+graphs)</option>
								</c:if>
								<option value="xlsx">XLSX</option>
								<option value="ods">ODS</option>
								<option value="docx">DOCX</option>
								<option value="odt">ODT</option>
							</select>
							<select class="form-control" id="exportnt-format-statistics-quiz">
								<option value="pdf">PDF</option>
							</select>
							<select class="form-control" id="exportnt-format-ecf1">
								<option value="xlsx">XLSX</option>
							</select>
							<select class="form-control" id="exportnt-format-ecf2">
								<option value="xlsx">XLSX</option>
							</select>
							<select class="form-control" id="exportnt-format-ecf3">
								<option value="xlsx">XLSX</option>
							</select>
							<select class="form-control" id="exportnt-format-files">
								<option value="zip">ZIP</option>
							</select>
						</div>			
					</td>
				</tr>
				<tr id="export-split-mcq-row">
					<td></td>
					<td colspan="2" style="padding-top: 5px;">
						<input class="check" type="checkbox" id="export-split-mcq" />
						<label for="export-split-mcq">
							<spring:message code="label.ExportSplitMCQ" />
							<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="left" title="<spring:message code="message.ExportSplitMCQInfo" />"><i class="glyphicon glyphicon-question-sign" style="color: #286090; top: -5px;"></i></span>
						</label>
					</td>
				</tr>
			</table>	
			
			
		</div>
		<div class="modal-footer">
			<img alt="wait animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a id="okStartExportButton"  onclick="checkAndStartExportNT($('#exportnt-name').val());"  class="btn btn-primary"><spring:message code="label.OK" /></a>
			<a class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
		</div>
		</div>
		</div>
	</div>

	<div class="modal" id="request-coda-dashboard-dialog">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-body">
					<spring:message code="question.CodaDashboardRequest" />
				</div>
				<div class="modal-footer">
					<a id="request-coda-dashboard-confirm" onclick="requestCodaDashboard()" class="btn btn-primary"><spring:message code="label.Imsure" /></a>
					<a class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>
				</div>
			</div>
		</div>
	</div>
	
</body>
</html>
