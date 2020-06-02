<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page contentType="text/html; charset=UTF-8" session="false" trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.PublishedResults" /></title>
	<%@ include file="../includes.jsp" %>
	
	<script type="text/javascript" src="${contextpath}/resources/js/jquery.stickytableheaders.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/menu.js?version=<%@include file="../version.txt" %>"></script>
	<link id="runnerCss" href="${contextpath}/resources/css/runner.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css"></link>
	<link href="${contextpath}/resources/css/charts.css" rel="stylesheet" type="text/css?version=<%@include file="../version.txt" %>" />
	<script type="text/javascript" src="${contextpath}/resources/js/runner.js?version=<%@include file="../version.txt" %>"></script>
	
	<style>
		.dropdown-menu {
			max-height: 150px;
			overflow-y: auto;
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
	
	</style>
	
	<script type="text/javascript">
		$(function() {	
			
			<c:if test="${publication.showContent}">
				loadMore();
				individualsMoveTo("first", null);
			</c:if>
			
			$(".hidden").removeClass("hidden");		
			
			$(".filtercell").each(function(){
				checkFilterCell(this, true);
			});
			
			$("ul.dropdown-menu").on("click", "[data-stopPropagation]", function(e) {
		        e.stopPropagation();
		    });		
			
			<c:choose>
				<c:when test="${tab != null && tab == '1'}">
				 	$('#tab1').tab('show');
			  	</c:when>
				<c:when test="${publication.showContent}">

			  	</c:when>
			  	<c:when test="${publication.showStatistics}">
			  		$('#tab2').tab('show');
			  	</c:when>
		  	</c:choose>
		  	
		  	<c:if test="${success != null}">
			  	showPublicationExportSuccessMessage();
		  	</c:if>
		  	
		  	<c:if test="${failure != null}">
		  		showPublicationExportFailureMessage();
		  	</c:if>
		  	
		  	sneaky = new ScrollSneak(location.hostname);
		  	
		  	<c:if test="${publication.showStatistics || publication.showCharts}">
		  	<c:choose>
			<c:when test="${skipstatistics == null}">
			loadStatisticsAsync(true);
			</c:when>
			<c:otherwise>
			$(".ajaxloaderimage").hide();
			$("#loadstatisticsbutton").find("a").attr("onclick", "loadStatisticsAsync(true)");
			$("#loadstatisticsbutton").show();
			</c:otherwise>
			</c:choose>
		  	</c:if>
		  	
		  	$('[data-toggle="tooltip"]').tooltip(); 
		});
		
		function showResults()
		{		
			$("#scrollarea").show();
			$("#results-table").css("min-height", "400px");
		
			$(".contentonly").show();
			$(".tab-pane-x").show();
			$("#scrollareaheader").css("overflow-x", "hidden");
			

			if ($('#scrollarea').hasScrollBar())
			{
				$("#scrollareaheader").css("overflow-y","scroll");
			} else {
				$("#scrollareaheader").css("overflow-y","auto");
			}
		}
		
		function hideResults()
		{
			<c:choose>
				<c:when test="${publication.isShowSearch()}">
					$("#scrollarea").hide();
					$("#results-table").css("min-height", "0px");
					$(".contentonly").hide();
					
					$("#scrollareaheader").css("overflow-x", "auto");
				</c:when>
				<c:otherwise>
					$(".tab-pane-x").hide();
				</c:otherwise>
			</c:choose>
			
			if ($('#scrollarea').hasScrollBar())
			{
				$("#scrollareaheader").css("overflow-y","scroll");
			} else {
				$("#scrollareaheader").css("overflow-y","auto");
			}
		}
			
		var currentIndividual = 0;
		function individualsMoveTo(val, element)
		{
			if (val == 'previous' && currentIndividual > 0)
			{
				currentIndividual = currentIndividual - 1;
			} else if (val == 'next')
			{
				currentIndividual = currentIndividual + 1;
			}
			
			var s = "survey=${form.survey.id}&counter=" + currentIndividual;
			
			$(".add-wait-animation-individual").show();
			
			$.ajax({
				type:'GET',
				  url: "${contextpath}/publication/individualJSON",
				  data: s,
				  dataType: 'json',
				  cache: false,
				  error: function() {
					  $("#add-wait-animation-individual").hide();
					  showError('<spring:message code="error.PleaseReload" />');
				  },
				  success: function( answers ) {
					  
					  if (answers.hasOwnProperty("noresults") && answers["noresults"] == "noresults")
					  {
						  $("#individuals-div").find(".widget-move-next").addClass("disabled");
						  $(".add-wait-animation-individual").hide();
						  return;
					  }
				  
				  $("#individuals-table").find(".questioncell").each(function(){
					var key = $(this).attr("data-id");
					var ukey = $(this).attr("data-uid");
					if (answers.hasOwnProperty(key))
					{
						$(this).html(answers[key]);
					} else if (answers.hasOwnProperty(ukey))
					{
						$(this).html(answers[ukey]);
					} else {
						$(this).html("");
					};
				  });
				  
				  $("#individuals-table").find(".tablequestioncell").each(function(){
					var key = $(this).attr("data-row") + $(this).attr("data-id") + $(this).attr("data-column");
					var ukey = $(this).attr("data-row") + $(this).attr("data-uid") + $(this).attr("data-column");
					if (answers.hasOwnProperty(key))
					{
						$(this).html(answers[key]);
					} else if (answers.hasOwnProperty(ukey))
					{
						$(this).html(answers[ukey]);
					} else {
						$(this).html("");
					};
				  });				  
				 				  
				  $(".firstResultIndividual").text(currentIndividual+1);
				  
				  if (currentIndividual > 0)
				  {
					  $("#individuals-div").find(".widget-move-previous").removeClass("disabled");
				  } else {
					  $("#individuals-div").find(".widget-move-previous").addClass("disabled");
				  }
				  
				  if (currentIndividual < ${paging.numberOfItems-1} || ${paging.numberOfItems-1} == -2 )
				  {
					  $("#individuals-div").find(".widget-move-next").removeClass("disabled");
				  } else {
					  $("#individuals-div").find(".widget-move-next").addClass("disabled");
				  }
				  
				  $(".add-wait-animation-individual").hide();
				  
				}});
		}
		
		function showExportDialog(type, format)
		{
			exporttype = type;
			$("#ask-export-dialog-error").hide();
			$("#email").val("");
			reloadCaptcha();
			$('#ask-export-dialog').modal('show')
		}
			
		var exporttype = "individuals";
		function startExport()
		{
			$("#ask-export-dialog").find(".validation-error").hide();
			$("#ask-export-dialog").find(".validation-error-keep").hide();
			
			var mail = $("#email").val();
			if (mail.trim().length == 0 || !validateEmail(mail))
			{
				$("#ask-export-dialog-error").show();
				return;
			};
			
			var challenge = getChallenge($("#ask-export-dialog"));
			
			<c:choose>
				<c:when test="${!captchaBypass}">			
		   		 var uresponse = getResponse();
		   		 
		   		if (uresponse.trim().length == 0)
			    {
			    	$("#runner-captcha-empty-error").show();
			    	return;
			    }
		   		</c:when>
		   		<c:otherwise>
		   		var uresponse = '';
		   		</c:otherwise>
		   	</c:choose>
		    		
		    var url = null;
		    var data = {email : mail, recaptcha_challenge_field : challenge, 'g-recaptcha-response' : uresponse};
			if (exporttype == "individuals" || exporttype == "singleindividual")
			{
				
				if (exporttype == "singleindividual")
				{
					data = {email : mail, recaptcha_challenge_field : challenge, 'g-recaptcha-response' : uresponse, selectedContribution : currentIndividual};
				} else {
					data = {email : mail, recaptcha_challenge_field : challenge, 'g-recaptcha-response' : uresponse, selectedContribution : ""};
				}
				
				url = "${contextpath}/publication/exportallindividuals/${form.survey.id}";
			} else if (exporttype == "resultsxls")
			{
				url = "${contextpath}/publication/exportresultsxls/${form.survey.id}";
			} else if (exporttype == "resultsods")
			{
				url = "${contextpath}/publication/exportresultsods/${form.survey.id}";
			} else if (exporttype == "statspdf")
			{
				url = "${contextpath}/publication/export/Statistics/pdf/${form.survey.id}";
			} else if (exporttype == "statsquizpdf")
			{
				url = "${contextpath}/publication/export/StatisticsQuiz/pdf/${form.survey.id}";
			} else if (exporttype == "statsxls")
			{
				url = "${contextpath}/publication/export/Statistics/xls/${form.survey.id}";
			} else if (exporttype == "statsods")
			{
				url = "${contextpath}/publication/export/Statistics/ods/${form.survey.id}";
			} else if (exporttype == "statsdoc")
			{
				url = "${contextpath}/publication/export/Statistics/doc/${form.survey.id}";
			} else if (exporttype == "statsodt")
			{
				url = "${contextpath}/publication/export/Statistics/odt/${form.survey.id}";
			} else if (exporttype == "chartsxls")
			{
				url = "${contextpath}/publication/export/Charts/xls/${form.survey.id}";
			} else if (exporttype == "chartsods")
			{
				url = "${contextpath}/publication/export/Charts/ods/${form.survey.id}";
			} else if (exporttype.indexOf("Files") == 0)
			{
				url = "${contextpath}/publication/exportfiles/${form.survey.id}";
				data = {email : mail, recaptcha_challenge_field : challenge, 'g-recaptcha-response' : uresponse, 'question' : exporttype.substring(5)};
			}
			
			if (url != null)
			{
				var request = $.ajax({
					  url: url,
					  data: data,
					  cache: false,
					  success: function(data)
					  {
						  if (data == "success") {
							  	$('#ask-export-dialog').modal('hide');
								showPublicationExportSuccessMessage();
						  } else if (data == "errorcaptcha") {
							  $("#runner-captcha-error").show();
							} else {
								showPublicationExportFailureMessage();
							};
							reloadCaptcha();
					  }
					});
			}
			
		
		}
				
		$(document).ready(function() {
			
			$("#resultsForm").on("submit",function() {
				if($(".modal.in").lenght===0)
				{
					sneaky.sneak(); 
				}
					
				$('.tableFloatingHeader').empty();
			});
			
			<c:if test="${selectedtab > 1}">
				hideResults();
			</c:if>
		});
		
		function updateColumns(){		
			try {
				$.fn.stickyTableHeaders.resizeMe();
			} catch (e)
			{}
		}
						
	</script>
</head>
<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
		
		<div class="fullpage" style="padding-top: 50px; padding-bottom: 0px;">
		
			<div class="fixedtitlepublication" style="background-color: #012d56; left:0">
				<div class="fixedtitleinner">
					
					<ul id="publicationtab" class="nav nav-tabs" style="background-color: #012d56; font-weight: 700; border-bottom: 0px;">
						<c:if test="${publication.showContent}">
					  		<li class="<c:if test="${selectedtab == 1}">active</c:if>"><a href="#content" data-toggle="tab" onclick="$('#selectedtab').val('1'); showResults();"><spring:message code="label.Results" /></a></li>
					  		<li class="<c:if test="${selectedtab == 2}">active</c:if>"><a id="tab1" href="#individual" data-toggle="tab" onclick="$('#selectedtab').val('2'); hideResults();"><spring:message code="label.IndividualResults" /></a></li>
					  	</c:if>
					  	<c:if test="${publication.showStatistics}">
					  		<li class="<c:if test="${selectedtab == 3}">active</c:if>"><a id="tab2" href="#statistics" data-toggle="tab" onclick="$('#selectedtab').val('3'); hideResults();"><spring:message code="label.Statistics" /></a></li>
				
							<c:if test="${form.survey.isQuiz}">
								<li><a id="tab3" href="#statisticsquiz" data-toggle="tab" onclick="$('#selectedtab').val('4'); hideResults();"><spring:message code="label.Quiz" /></a></li>
							</c:if>			
					  	</c:if>
					</ul>
				</div>
			</div>
			
			<c:set var="answerSet" target="${paging.items[0]}" />
			
			<form:form modelAttribute="paging" id="resultsForm" method="POST" action="${contextpath}/publication/${form.survey.shortname}" style="margin-top: 95px;">
				<h1><spring:message code="label.PublishedResults" />:
					<c:choose>
						<c:when test='${form.survey.shortname.length() > 22}'>
							${form.survey.shortname.substring(0,20)}...
						</c:when>
						<c:otherwise>
							${form.survey.shortname}
						</c:otherwise>
					</c:choose>
				</h1>
			
				<input type="hidden" id="selectedtab" name="selectedtab" value="${selectedtab}" />
				<div class="tab-content" style="overflow: visible;">
					<c:if test="${publication.showContent}">
						<div class="tab-pane-x <c:if test="${selectedtab == 1}">active</c:if>" id="content" style="min-width: 800px">
							<c:if test="${publication == null || publication.isShowSearch()}">
								<div class="contentonly" style="text-align: center; position: fixed; top: 130px; padding: 10px; width: 100%; height: 46px; left:0px; background-color: #fff; z-index: 1010">
									<div style="width: 850px; margin-left: auto; margin-right: auto">
										<div style="text-align: right; height: 36px; float: right; width: 200px;">
											<b><spring:message code="label.Export" /></b>										
											<span class="deactivatedexports">
												<a data-toggle="tooltip" title="<spring:message code="tooltip.Exportxls" />"><img src="${contextpath}/resources/images/file_extension_xls_small_grey.png" /></a>
												<a data-toggle="tooltip" title="<spring:message code="tooltip.Exportods" />"><img src="${contextpath}/resources/images/file_extension_ods_small_grey.png" /></a>
											</span>
											<span class="activatedexports" style="display: none">
												<a data-toggle="tooltip" title="<spring:message code="tooltip.Exportxls" />" onclick="showExportDialog('resultsxls');" ><img src="${contextpath}/resources/images/file_extension_xls_small.png" /></a>
												<a data-toggle="tooltip" title="<spring:message code="tooltip.Exportods" />" onclick="showExportDialog('resultsods');" ><img src="${contextpath}/resources/images/file_extension_ods_small.png" /></a>
											</span>										
								  		</div>
										<div style="text-align: center; margin-left: 200px">
											<input type="submit" class="btn btn-default" value="<spring:message code="label.Search" />" />
											<a class="btn btn-default" href="${contextpath}/publication/${form.survey.shortname}"><spring:message code="label.Reset" /></a>
										</div>
									</div>
								</div>
							</c:if>
							
					  		<%@ include file="../management/results-content.jsp" %>	
					  	</div>
					  	<div class="tab-pane <c:if test="${selectedtab == 2}">active</c:if>" id="individual">
				  			<%@ include file="../management/results-individual.jsp" %>					  		
				  			<c:if test="${paging.items.size() == 0 && !publication.isShowSearch()}">
								<div style="min-width: 400px; font-size: 20px; text-align: center; padding: 10px; color: #36B500;"><spring:message code="message.NoResults" /></div>
							</c:if>
					  	</div>
					 </c:if>
					 <c:if test="${publication.showStatistics}">
					 	 <div class="tab-pane <c:if test="${selectedtab == 3}">active</c:if>" id="statistics">							
				 		  	<div style="text-align: center; position: fixed; top: 130px; padding: 10px; width: 100%; left:0px; background-color: #fff;">
				  		  		<b><spring:message code="label.Export" /></b>
								
								<span class="deactivatedstatexports">
									<a data-toggle="tooltip" title="<spring:message code="tooltip.Exportpdf" />" id="startExportStatisticsLinkpdf" ><img src="${contextpath}/resources/images/file_extension_pdf_small_grey.png" /></a>				
									<a data-toggle="tooltip" title="<spring:message code="tooltip.Exportxls" />" id="startExportStatisticsLinkxls" ><img src="${contextpath}/resources/images/file_extension_xls_small_grey.png" /></a>
									<a data-toggle="tooltip" title="<spring:message code="tooltip.Exportods" />" id="startExportStatisticsLinkods" ><img src="${contextpath}/resources/images/file_extension_ods_small_grey.png" /></a>
									<a data-toggle="tooltip" title="<spring:message code="tooltip.Exportdoc" />" id="startExportStatisticsLinkdoc" ><img src="${contextpath}/resources/images/file_extension_doc_small_grey.png" /></a>
									<a data-toggle="tooltip" title="<spring:message code="tooltip.Exportodt" />" id="startExportStatisticsLinkodt" ><img src="${contextpath}/resources/images/file_extension_odt_small_grey.png" /></a>
								</span>
								<span class="activatedstatexports">
									<a data-toggle="tooltip" title="<spring:message code="tooltip.Exportpdf" />" onclick="showExportDialog('statspdf')" ><img src="${contextpath}/resources/images/file_extension_pdf_small.png" /></a>
					  				<a data-toggle="tooltip" title="<spring:message code="tooltip.Exportxls" />" onclick="showExportDialog('statsxls')" ><img src="${contextpath}/resources/images/file_extension_xls_small.png" /></a>
									<a data-toggle="tooltip" title="<spring:message code="tooltip.Exportods" />" onclick="showExportDialog('statsods')" ><img src="${contextpath}/resources/images/file_extension_ods_small.png" /></a>
					  				<a data-toggle="tooltip" title="<spring:message code="tooltip.Exportdoc" />" onclick="showExportDialog('statsdoc')" ><img src="${contextpath}/resources/images/file_extension_doc_small.png" /></a>
									<a data-toggle="tooltip" title="<spring:message code="tooltip.Exportodt" />" onclick="showExportDialog('statsodt')"><img src="${contextpath}/resources/images/file_extension_odt_small.png" /></a>
				  				</span>
							</div>
					  		<%@ include file="../management/results-statistics.jsp" %>									  		  		
					  	</div>
					  	
					  	<c:if test="${form.survey.isQuiz}">
					  		<div class="tab-pane <c:if test="${selectedtab == 4}">active</c:if>" id="statisticsquiz">							
						 		<c:if test="${paging.items.size() > 0}">			  	
						  		  	<div style="text-align: center; position: fixed; top: 130px; padding: 10px; width: 100%; left:0px; background-color: #fff;">
						  		  		<b><spring:message code="label.Export" /></b>
										
										<c:choose>
											<c:when test="${paging.items.size() == 0 || form.getSurvey().hasNoQuestionsForStatistics()}">
												<a data-toggle="tooltip" title="<spring:message code="tooltip.Exportpdf" />" id="startExportStatisticsLinkpdf" ><img src="${contextpath}/resources/images/file_extension_pdf_small_grey.png" /></a>				
											</c:when>
											<c:otherwise>
												<a data-toggle="tooltip" title="<spring:message code="tooltip.Exportpdf" />" onclick="showExportDialog('statsquizpdf')" ><img src="${contextpath}/resources/images/file_extension_pdf_small.png" /></a>
								  			</c:otherwise>
										</c:choose>
																		
									</div>
							  		<%@ include file="../management/results-statistics-quiz.jsp" %>									  		  		
					  			</c:if>	
						  	</div>
					  	
					  	</c:if>
					  			 
					</c:if>
					<%@ include file="../management/results-ajax.jsp" %>	
				</div>
			
			</form:form>
		
		</div>
	</div>

	<%@ include file="../footerSurveyLanguages.jsp" %>
	<%@ include file="../generic-messages.jsp" %>
		
	<div class="modal" id="ask-export-dialog">
		<div class="modal-dialog">
    	<div class="modal-content">		
		<div class="modal-header">
			<b><spring:message code="label.Info" /></b>
		</div>
		<div class="modal-body">
			<p><spring:message code="question.ExportIndividuals" /></p>
			<input type="text" name="email" id="email" maxlength="255" />
			<span id="ask-export-dialog-error" class="validation-error hideme"><spring:message code="message.ProvideEmail" /></span>
			
			<div class="captcha" style="margin-left: 0px; margin-bottom: 20px; margin-top: 20px;">
				<%@ include file="../captcha.jsp" %>			
	        </div>				
		        
		   <div id="ask-export-dialog-all-captcha-error" class="alert-danger hideme"><spring:message code="message.captchawrongnew" /></div>
		</div>
		<div class="modal-footer">
			<a  class="btn btn-primary" onclick="startExport()"><spring:message code="label.OK" /></a>	
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>				
		</div>
		</div>
		</div>
	</div>
			
	<c:if test="${message != null}">
		<script type="text/javascript">
			showError('${message}');
			<c:if test="${errorsource != null && errorsource == 'exportallindividuals'}">
				$('#tab1').tab('show');
				$('#ask-export-dialog-all').modal('show');
				$('#ask-export-dialog-all-captcha-error').show();
			</c:if>
		</script>
	</c:if>
	
</body>
</html>
