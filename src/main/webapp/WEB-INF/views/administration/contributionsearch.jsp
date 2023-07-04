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
	</style>
	
	<script type="text/javascript"> 
		$(function() {
			$("#administration-menu-tab").addClass("active");
			$("#contributionmanagement-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
			
			<c:if test="${error != null}">
				showError('<esapi:encodeForHTML>${error}</esapi:encodeForHTML>');
			</c:if>
			
			$('.filtercell').find("input").keyup(function(e){
			    if(e.keyCode == 13){
			    	$("#contributionsearchForm").submit();
			    }
			});
			
			$(".filtercell").each(function(){
				checkFilterCell(this, true);
			});
			
			$("#resultTableDivTable").stickyTableHeaders({fixedOffset: 200});
		    
			$("div:not(.overlaymenu)").click(function(e) {
				
				if ($(this).hasClass("ui-datepicker"))
				{
					e.stopPropagation();
					return;
				}
				
				if (closeOverlayDivsEnabled)
				{
					$(".overlaymenu").hide();
					closeOverlayDivsEnabled = false;
				}
			});
			
			$('[data-toggle="tooltip"]').tooltip();
			
			<c:if test="${filter.status != null}">
		 		$("#contributiontype").val("${filter.status}");
		 	</c:if>
		});
		
		var infinitePage = 6;
			
		$(window).scroll(function() {
			    if ($(window).scrollTop() <= $(document).height() - $(window).height() && $(window).scrollTop() >= $(document).height() - $(window).height() - 10) {
			    	loadMore();
			  }
			 });
		
		function resetContribution(uid, button)
		{
			$.ajax({
				type:'POST',
				url: "${contextpath}/administration/resetcontribution",
				beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
				data: {uid: uid},
				cache: false,
				 success: function( data ) {						  
					  if (data != "error") {
							var row = $(button).closest("tr");
							var surveyUid = $($(row).find("td")[0]).html();
							var a = document.createElement("a");
							
							if ($(row).hasClass("invitation")) {
								$(a).attr("target", "_blank").attr("href","${contextpath}/runner/" + surveyUid + '/' + $(row).attr("data-invitationId")).html(data);											
							} else {
								$(a).attr("target", "_blank").attr("href","${contextpath}/runner/" + surveyUid + '?draftid=' + data).html(data);
							}
							
							$($(row).find("td")[3]).html(a);
							$($(row).find("td")[4]).empty();
														
							var td = $($(row).find("td")[6]);
							$(td).empty();	
							$(td).append('<img src="${contextpath}/resources/images/file_extension_pdf_small_grey.png">');
							$(td).append('<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Edit" />" class="iconbutton disabled" ><span class="glyphicon glyphicon-pencil"></span></a>');
							$(td).append('<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.MakeDraftAgain" />" class="iconbutton disabled"><span class="glyphicon glyphicon-refresh"></span></a>');
							$('[data-toggle="tooltip"]').tooltip();
						} else {
							showError("<spring:message code="error.OperationFailed" />");
						}
				 },
				error: function(jqXHR) {
					  showAjaxError(jqXHR.status);
				}});
		}
		
		var endreached = false;
		var inloading = false;
		function loadMore()
		{
			if (endreached || inloading) return;
			
			inloading = true;
			
			$( "#wheel" ).show();
			var s = "page=" + infinitePage++ + "&rows=10";	
			
			$.ajax({
				type:'GET',
				url: "${contextpath}/administration/contributionsearchJSON",
				dataType: 'json',
				data: s,
				cache: false,
				success: refreshContributions,
				error: function(jqXHR) {
					showAjaxError(jqXHR.status);
				}});
		}
		
		function refreshContributions( list, textStatus, xhr ) {
			
			if (list.length == 0)
			{
				infinitePage--;
				endreached = true;
				inloading = false;
			    $("#wheel").hide();
				$("#load-more-div").hide();
				return;
			}
			for (var i = 0; i < list.length; i++ )
			  {
				var row = document.createElement("tr");
				
				if (list[i].invitationId != null && list[i].invitationId.length > 0) {
					$(row).addClass("invitation").attr("data-invitationId", list[i].invitationId);
				}
				
				var td = document.createElement("td");				
				$(td).append(list[i].surveyUID);		
				$(row).append(td);
				
				td = document.createElement("td");
				var a = document.createElement("a");
				$(a).attr("href","${contextpath}/" + list[i].surveyAlias + "/management/overview").attr("target","_blank").html(list[i].surveyAlias);
				$(td).append(a);		
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].surveyTitle);		
				$(row).append(td);
				
				td = document.createElement("td");		
				
				if (list[i].draft && list[i].invitationId != null && list[i].invitationId.length > 0) {
					$(td).append('<a target="_blank" href="${contextpath}/runner/' + list[i].surveyUID + '/' + list[i].invitationId + '">' + list[i].draftId  + '</a>');
					$(td).append('<div style="float: right"><a data-toggle="tooltip" title="<spring:message code="tooltip.readonlypreview" />" target="_blank" href="${contextpath}/runner/' + list[i].surveyUID + '/' + list[i].invitationId + '?readonly=true"><span class="glyphicon glyphicon-eye-open"></span></a></div>');
				} else if (list[i].draft) {				
					$(td).append('<a target="_blank" href="${contextpath}/runner/' + list[i].surveyUID + '?draftid=' + list[i].draftId + '">' + list[i].draftId  + '</a>');	
					$(td).append('<div style="float: right"><a data-toggle="tooltip" title="<spring:message code="tooltip.readonlypreview" />" target="_blank" href="${contextpath}/runner/' + list[i].surveyUID + '?draftid=' + list[i].draftId + '&readonly=true"><span class="glyphicon glyphicon-eye-open"></span></a></div>');
				} else {
					$(td).append(list[i].draftId);									
				}				
								
				$(row).append(td);
				
				td = document.createElement("td");	
				if (!list[i].draft)
				{
					$(td).append(list[i].uniqueCode);	
				}						
				$(row).append(td);
				
				td = document.createElement("td");				
				$(td).append(list[i].updateDate);		
				$(row).append(td);
				
				td = document.createElement("td");	
				
				if (list[i].draft)
				{
					$(td).append('<img data-toggle="tooltip" rel="tooltip" title="<spring:message code="tooltip.Downloadpdf" />" src="${contextpath}/resources/images/file_extension_pdf_small_grey.png">');
				} else {
					$(td).append('<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="tooltip.Downloadpdf" />"><img src="${contextpath}/resources/images/file_extension_pdf_small.png"></a>');
				}
				
				$(td).append('&nbsp;');
				
				if (!list[i].draft)
				{
					$(td).append('<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Edit" />" class="iconbutton" target="_blank" href="<c:url value="/editcontribution/"/>' + list[i].uniqueCode + '?mode=dialog" ><span class="glyphicon glyphicon-pencil"></span></a>');
				} else {
					$(td).append('<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Edit" />" class="iconbutton disabled" ><span class="glyphicon glyphicon-pencil"></span></a>');
				}
				
				$(td).append('&nbsp;');
				
				if (!list[i].draft)
				{
					$(td).append('<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.MakeDraftAgain" />" class="iconbutton" onclick="resetContribution(\x27' + list[i].uniqueCode + '\x27, this)" ><span class="glyphicon glyphicon-refresh"></span></a>');
				} else {
					$(td).append('<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.MakeDraftAgain" />" class="iconbutton disabled"><span class="glyphicon glyphicon-refresh"></span></a>');
				}
				
				$(td).append('&nbsp;');
				
				if (!list[i].draft)
				{
					$(td).find("a").first().attr("data-toggle","tooltip").attr("title", "<spring:message code="tooltip.Downloadpdf" />").attr("onclick","downloadAnswerPDF('" + list[i].uniqueCode + "')");
				}				
				
				$(row).append(td);
				
				$('#resultTableDivTableBody').first().append(row);
			  }
			 inloading = false;
			  $( "#wheel" ).hide();
			  $('[data-toggle="tooltip"]').tooltip();
		}
		
		function resetSearch()
		{
			$(".filtertools").each(function(){
				clearFilterCellContent($(this).find("a").first());
			});
			
			$(".filtercell").css("background-color","");
			
			$('#resultTableDivTableBody').empty();
		}
	</script>
		
</head>
<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
		<%@ include file="../menu.jsp" %>
		<%@ include file="adminmenu.jsp" %>	
		
		<form:form id="contributionsearchForm" action="contributionsearch" method="post" class="noautosubmitonclearfilter" onsubmit="$('#generic-wait-dialog').modal('show');">
		
			<div class="fixedtitleform">
				<div class="fixedtitleinner" style="padding-bottom: 35px;">
					<div id="action-bar" class="container">
						<div class="row">
							<div class="col-md-12" style="text-align:center; margin-top: 20px;">
								<div style="float: left">
									<select name="contributiontype" id="contributiontype" class="form-control" style="width: auto">
										<option value="All"><spring:message code="label.DraftsAndSubmittedContributions" /></option>
 		                                <option value="Drafts"><spring:message code="label.DraftContributionsOnly" /></option>
 	                                    <option value="Submitted"><spring:message code="label.SubmittedContributionsOnly" /></option>                                                   
 	                                </select>
 	                            </div>
								<input rel="tooltip" title="<spring:message code="label.Search" />" class="btn btn-primary" type="submit" value="<spring:message code="label.Search" />" />
								<a  onclick="resetSearch()" rel="tooltip" title="<spring:message code="label.ResetFilter" />" class="btn btn-default"><spring:message code="label.Reset" /></a>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<div class="page1024" style="width: 1300px; margin-bottom: 0px;overflow-x: visible;">
				
					<div id="resultTableDiv" style="min-height: 400px; padding-top: 200px">
						<table id="resultTableDivTable" class="table table-bordered table-styled">
							<thead style="border-top: 1px solid #ddd;">
								<tr>
									<th><spring:message code="label.Survey" /> UID</th>
									<th><spring:message code="label.Survey" />&nbsp;<spring:message code="label.Alias" /></th>
									<th><spring:message code="label.Survey" />&nbsp;<spring:message code="label.Title" /></th>
									<th><spring:message code="label.Draft" />&nbsp;<spring:message code="label.ID" /></th>
									<th><spring:message code="label.Contribution" />&nbsp;<spring:message code="label.ID" /></th>
									<th><spring:message code="label.LastUpdate" /></th>
									<th style="min-width: 140px"><spring:message code="label.Actions" /></th>
								</tr>
								<tr class="table-styled-filter">
									<th class="filtercell">
										<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${filter.surveyUid}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="surveyUid" />
									</th>
									<th class="filtercell">
										<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${filter.surveyShortname}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="surveyShortname" />
									</th>
									<th class="filtercell">
										<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${filter.surveyTitle}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="surveyTitle" />
									</th>
									<th class="filtercell">
										<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${filter.draftId}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="draftId" />
									</th>
									<th class="filtercell">
										<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${filter.caseId}</esapi:encodeForHTMLAttribute>' type="text" maxlength="255" style="margin:0px;" name="caseId" />
									</th>
									<th class="filtercell" style="min-width: 260px !important; max-width: 300px;">
										<div class="btn-toolbar" style="min-width: 150px; margin: 0px; text-align: center">
											<div class="datefilter" style="float: left">
											  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
											  	<c:choose>
											     	<c:when test="${filter.updatedFrom != null}">
											     		<spring:eval expression="filter.updatedFrom" />
											     	</c:when>
											     	<c:otherwise>
											     		<spring:message code="label.from" />
											     	</c:otherwise>
											     </c:choose>
											    <span class="caret"></span>
											  </a>
											   <div class="overlaymenu hideme">
											    	<input type="hidden" name="metafilterupdatefrom" class="hiddendate" value="<spring:eval expression="filter.updatedFrom" />" />
											    	<div id="metafilterupdatefromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
											    </div>
											</div>		
											<div class="datefilter" style="float: left">
											  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
											  	<c:choose>
											     	<c:when test="${filter.updatedTo != null}">
											     		<spring:eval expression="filter.updatedTo" />
											     	</c:when>
											     	<c:otherwise>
											     		<spring:message code="label.To" />
											     	</c:otherwise>
											     </c:choose>
											    <span class="caret"></span>
											  </a>
											   <div class="overlaymenu hideme">
											    	<input type="hidden" name="metafilterupdateto" class="hiddendate" value="<spring:eval expression="filter.updatedTo" />" />
											    	<div id="metafilterupdatetodiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
											   </div>
											</div>	
										</div>				
									</th>						
									<th>&nbsp;</th>							
								</tr>					
							</thead>
							<tbody id="resultTableDivTableBody">
							<c:if test="${paging != null}">
								<c:forEach items="${paging.items}" var="answerSet">
									<c:choose>
										<c:when test="${answerSet.invitationId != null && answerSet.invitationId.length() > 0}">
											<tr class="invitation" data-invitationId="${answerSet.invitationId}">
										</c:when>
										<c:otherwise>
											<tr>
										</c:otherwise>
									</c:choose>
									
										<td>${answerSet.survey.uniqueId}</td>
										<td><a href='${contextpath}/${answerSet.survey.shortname}/management/overview' target='_blank'>${answerSet.survey.shortname}</a></td>
										<td>${answerSet.survey.cleanTitle()}</td>
										<td>
											<c:choose>
												<c:when test="${(answerSet.isDraft) && answerSet.invitationId != null && answerSet.invitationId.length() > 0}">
													<a target="_blank" href="${contextpath}/runner/${answerSet.survey.uniqueId}/${answerSet.invitationId}">${answerSet.draftId}</a>											
													<div style="float: right">
														<a data-toggle="tooltip" title="<spring:message code="tooltip.readonlypreview" />" target="_blank" href="${contextpath}/runner/${answerSet.survey.uniqueId}/${answerSet.invitationId}?readonly=true"><span class="glyphicon glyphicon-eye-open"></span></a>
													</div>	
												</c:when>
												<c:when test="${answerSet.isDraft}">
													<a target="_blank" href="${contextpath}/runner/${answerSet.survey.uniqueId}?draftid=${answerSet.draftId}">${answerSet.draftId}</a>
													<div style="float: right">
														<a data-toggle="tooltip" title="<spring:message code="tooltip.readonlypreview" />" target="_blank" href="${contextpath}/runner/${answerSet.survey.uniqueId}?draftid=${answerSet.draftId}&readonly=true"><span class="glyphicon glyphicon-eye-open"></span></a>
													</div>								
												</c:when>
												<c:otherwise>
													${answerSet.draftId}										
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											<c:if test="${!answerSet.isDraft}">
												${answerSet.uniqueCode}
											</c:if>						
										</td>
										<td>
											<spring:eval expression="answerSet.updateDate" />	
										</td>
										<td>
											<c:choose>
												<c:when test="${answerSet.isDraft || answerSet.survey.isEVote}">
													<img data-toggle="tooltip" rel="tooltip" title="<spring:message code="tooltip.Downloadpdf" />" src="${contextpath}/resources/images/file_extension_pdf_small_grey.png">
												</c:when>
												<c:otherwise>
													<a style="pading-bottom: 10px;" data-toggle="tooltip" rel="tooltip" title="<spring:message code="tooltip.Downloadpdf" />" onclick="downloadAnswerPDF('${answerSet.uniqueCode}')"><img src="${contextpath}/resources/images/file_extension_pdf_small.png"></a>
												</c:otherwise>
											</c:choose>
											
											<c:choose>
												<c:when test="${!answerSet.isDraft && answerSet.uniqueCode.length() > 0 && !answerSet.survey.getIsEVote()}">
													<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Edit" />" class="iconbutton" target="_blank" href="<c:url value='/editcontribution/'/>${answerSet.uniqueCode}?mode=dialog" ><span class="glyphicon glyphicon-pencil"></span></a>
												</c:when>
												<c:otherwise>
													<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Edit" />" class="iconbutton disabled" ><span class="glyphicon glyphicon-pencil"></span></a>
												</c:otherwise>
											</c:choose>
											
											<c:choose>
												<c:when test="${!answerSet.isDraft && !answerSet.survey.isEVote}">
													<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.MakeDraftAgain" />" class="iconbutton" onclick="resetContribution('${answerSet.uniqueCode}', this)" ><span class="glyphicon glyphicon-refresh"></span></a>
												</c:when>
												<c:otherwise>
													<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.MakeDraftAgain" />" class="iconbutton disabled"><span class="glyphicon glyphicon-refresh"></span></a>
												</c:otherwise>
											</c:choose>
										</td>									
									</tr>
								</c:forEach>
							</c:if>
							</tbody>
						</table>
						<div style="text-align: center">
							<img id="wheel" class="hideme" src="${contextpath}/resources/images/ajax-loader.gif" />
						</div>
						<div id="tbllist-empty" class="noDataPlaceHolder" <c:if test="${paging == null }">style="display:block;"</c:if>>
							<p>
								<spring:message code="label.NoDataContributionText"/>&nbsp;<img src="${contextpath}/resources/images/icons/32/forbidden_grey.png" alt="no data"/>
							<p>
						</div>					
					</div>
					
				<div style="clear: both"></div>
			</div>
		
		</form:form>
	</div>
	<%@ include file="../footer.jsp" %>	
	
</body>
</html>
