<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Administration" /></title>	
	<%@ include file="../includes.jsp" %>	
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
			$("#publicsurveys-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
			
			<c:if test="${error != null}">
				showError('<esapi:encodeForHTML>${error}</esapi:encodeForHTML>');
			</c:if>
			
			$('.filtercell').find("input").keyup(function(e){
			    if(e.keyCode == 13){
			    	$("#resultsForm").submit();
			    }
			});
			
			$(".filtercell").each(function(){
				checkFilterCell(this, true);
			});
			
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
		});	
		
		
		function accept(id, button)
		{
			$("#titleaccept").show();
			$("#titledecline").hide();
			$("#id").val(id);
			$("#replyto").val($("#replyToValue").val());
			$("#subject").val("Your publication request for the EUSurvey Homepage");
		
			var name = $(button).attr("data-name");
	    	var body = "Dear " + name + ",<br /><br />Your request for publishing your survey in the list of all public surveys has been accepted.<br /><br />To see your questionnaire in the list, please follow this link:<br /><a href='${host}home/publicsurveys'>${host}home/publicsurveys</a>";
			
			$("#text").text(body);
			$("#signature").text($("#signatureValue").val());
			$("#editorform").attr("action","${contextpath}/administration/publicsurveys/accept");
			$("#maileditordialog").modal("show");			
		}
		
		function decline(id, button)
		{
			$("#titleaccept").hide();
			$("#titledecline").show();
			$("#id").val(id);
			$("#replyto").val($("#replyToValue").val());
			$("#subject").val("Your publication request for the EUSurvey Homepage");
			
			var name = $(button).attr("data-name");
			var body = "Dear " + name + ",<br /><br />Your request for publishing your survey in the list of all public surveys has been declined.<br /><br />Thank you for your understanding.";	
						
			$("#text").text(body);
			$("#signature").text($("#signatureValue").val());
			$("#editorform").attr("action","${contextpath}/administration/publicsurveys/decline");
			$("#maileditordialog").modal("show");
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

	<%@ include file="../header.jsp" %>
	<%@ include file="../menu.jsp" %>
	<%@ include file="adminmenu.jsp" %>		
	
	<!--  set here hidden values coming from the -->
		<c:choose>
			<c:when test="${oss}">
				<input type="hidden" value="${sender}" id="replyToValue"/>
				<input type="hidden" value="Your team" id="signatureValue"/>
			</c:when>
			<c:otherwise>
				<input type="hidden" value="DIGIT-EUSURVEY-SUPPORT@ec.europa.eu" id="replyToValue"/>
				<input type="hidden" value="Your EUSurvey Team" id="signatureValue"/>
				</c:otherwise>
		</c:choose>

		<div class="fixedtitleform">
			<div class="fixedtitleinner">
				<div id="action-bar" class="container">
					<div class="row">
						<div class="col-md-12" style="text-align:center; padding-top: 20px;">
							<a onclick="$('#resultsForm').submit()" rel="tooltip" title="<spring:message code="label.Search" />" class="btn btn-info"><spring:message code="label.Search" /></a>
							<a onclick="resetSearch()" rel="tooltip" title="<spring:message code="label.ResetFilter" />" class="btn btn-default"><spring:message code="label.Reset" /></a>
						</div>
					</div>
				</div>
			</div>
		</div>
	
		<div class="page1024" style="margin-bottom: 0px; margin-top: 205px; overflow-x: visible;">
		
			<form:form id="resultsForm" action="publicsurveys" method="post">
				<div id="surveyTableDiv" style="min-height: 400px;">	
					<table class="table table-bordered table-styled">
					<thead>
						<tr>
							<th><spring:message code="label.Alias" /></th>
							<th><spring:message code="label.Owner" /></th>
							<th><spring:message code="label.RequestDate" /></th>
							<th><spring:message code="label.Actions" /></th>
						</tr>
					
						<tr class="table-styled-filter">
							<th class="filtercell">
								<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='${filteralias}' type="text" maxlength="255" style="margin:0px;" name="filteralias" />
							</th>
							<th class="filtercell">
								<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='${filterowner}' type="text" maxlength="255" style="margin:0px;" name="filterowner" />
							</th>							
							<th class="filtercell" style="width: 260px">
								<div class="btn-toolbar" style="margin: 0px; text-align: center">
									<div class="datefilter" style="float: left">
									  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
									     <c:choose>
									     	<c:when test="${filterrequestdatefrom != null && filterrequestdatefrom.length() > 0}">
									     		<spring:eval expression="filterrequestdatefrom" />
									     	</c:when>
									     	<c:otherwise>
									     		<spring:message code="label.from" />
									     	</c:otherwise>
									     </c:choose>
									    <span class="caret"></span>
									  </a>
									  <div class="overlaymenu hideme">
									    	<input type="hidden" id="filterrequestdatefrom" name="filterrequestdatefrom" class="hiddendate" value="${filterrequestdatefrom}" />
									    	<div id="metafilterdatefromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
									   </div>
									</div>
									<div class="datefilter" style="float: left">	
									  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
									  	<c:choose>
									     	<c:when test="${filterrequestdateto != null && filterrequestdateto.length() > 0}">
									     		<spring:eval expression="filterrequestdateto" />
									     	</c:when>
									     	<c:otherwise>
									     		<spring:message code="label.To" />
									     	</c:otherwise>
									     </c:choose>
									    <span class="caret"></span>
									  </a>
									 <div class="overlaymenu hideme">
									    	<input type="hidden" id="filterrequestdateto" name="filterrequestdateto" class="hiddendate" value="${filterrequestdateto}" />
									    	<div id="metafilterdatetodiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
									    </div>
									</div>	
								</div>
							</th>
							<th style="width: 170px">&nbsp;</th>
						</tr>
						</thead>
						<tbody id="resultTableDivTableBody">
						<c:forEach items="${surveys}" var="survey">
							<tr>
								<td><a target="_blank" href="${contextpath}/${survey.shortname}/management/overview">${survey.shortname}</a></td>
								<td>${survey.owner.login}</td>
								<td>
									<c:choose>
										<c:when test="${survey.publicationRequestedDate != null}">
											<spring:eval expression="survey.publicationRequestedDate" />
										</c:when>
										<c:otherwise>n/a</c:otherwise>
									</c:choose>
								</td>
								<td>
									<a data-name="${survey.owner.firstLastName}" onclick="accept('${survey.id}', this);" class="btn btn-success"><spring:message code="label.Accept" /></a>
									<a data-name="${survey.owner.firstLastName}" onclick="decline('${survey.id}', this);" class="btn btn-danger"><spring:message code="label.Decline" /></a>
								</td>
							</tr>
						</c:forEach>
						</tbody>
					</table>	
					
					<div id="tbllist-empty" class="noDataPlaceHolder" <c:if test="${surveys.size() == 0 }">style="display:block;"</c:if>>
						<p>
							<spring:message code="label.NoDataPublicSurveyValidationText"/>&nbsp;<img src="${contextpath}/resources/images/icons/32/forbidden_grey.png" alt="no data"/>
						<p>
					</div>			
				</div>
			</form:form>
			
			<div style="clear: both"></div>
		</div>
		
		<form:form id="editorform" method="POST">
			<input type="hidden" id="id" name="id" />
			<div id="maileditordialog" class="modal" tabindex="-1" role="dialog">
				<div class="modal-dialog">
    			<div class="modal-content">
			      <div class="modal-header">
			      	<span id="titleaccept"><spring:message code="label.Accept" /></span>
			      	<span id="titledecline"><spring:message code="label.Decline" /></span>
			      </div>
			      <div class="modal-body" style="">
		      	    <span class='mandatory'>*</span><spring:message code="label.ReplyTo" /><br />
			        <input class="required" id="replyto" type="text" name="replyto" style="width: 500px" /><br />
			       	<span class='mandatory'>*</span> <spring:message code="label.Subject" /><br />
			        <input class="required" id="subject" type="text" name="subject" style="width: 500px" /><br />
			        <span class='mandatory'>*</span><spring:message code="label.Text" /><br />
			        <textarea class="tinymce required" name="text" id="text"></textarea>
			        <span class='mandatory'>*</span><spring:message code="label.Signature" /><br />
			        <textarea class="tinymce" name="signature" id="signature"></textarea>     
			      </div>
			      <div class="modal-footer">
			        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></button>
			        <button onclick="$('.validation-error').remove(); return validateInput($('#editorform'))" type="submit" class="btn btn-info"><spring:message code="label.Send" /></button>
			      </div>
			     </div>
			     </div>
			</div>
		</form:form>
		
	<%@ include file="../footer.jsp" %>	

</body>
</html>
