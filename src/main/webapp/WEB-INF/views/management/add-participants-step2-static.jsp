<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<div class="modal" id="add-participants-dialog2-static" data-backdrop="static">
	<div class="modal-dialog modal-lg">
    <div class="modal-content">
	<div class="modal-header">
		<c:choose>
			<c:when test="${readonly == null}">
				<spring:message code="label.CreateNewGuestlist" /> - <spring:message code="label.SpecifyYourParticipants" /> (2/2)
			</c:when>
			<c:otherwise>
				<spring:message code="label.GuestList" />
			</c:otherwise>
		</c:choose>
	</div>
	<div class="modal-body" style="height: 530px; max-height:none; overflow-y:auto;">
	
		<c:choose>
			<c:when test="${readonly != null}">
				<table id="selectedparticipantsstatic" class="table table-bordered table-styled" >
					<thead>
						<tr>
							<th><spring:message code="label.Name" /> (<spring:message code="label.Email" />)</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${selectedParticipationGroup.attendees}" var="attendee">
							<tr>
								<td><esapi:encodeForHTML>${attendee.name} (${attendee.email})</esapi:encodeForHTML></td>								
							</tr>
						</c:forEach>
					</tbody>
				</table>
							
				<div id="pager2" style="text-align: center; display: none;">
					<a onclick="moveTo('first');" class="middle btn btn-sm <c:if test="${paging.firstItemOnPage == 1}">disabled</c:if>"><i class="icon-step-backward"></i></a>
					<a onclick="moveTo('${paging.currentPage-1}');" class="btn btn-sm <c:if test="${paging.firstItemOnPage == 1}">disabled</c:if>"><span class="glyphicon glyphicon-chevron-left"></span></a>				
					<span>${pagingElementName} ${paging.firstItemOnPage} <spring:message code="label.To" /> ${paging.lastItemOnPage} <spring:message code="label.of" /> ${paging.numberOfItems}</span>		
					<a onclick="moveTo('${paging.currentPage+1}');" class="btn btn-sm <c:if test="${paging.lastItemOnPage == paging.numberOfItems}">disabled</c:if>"><span class="glyphicon glyphicon-chevron-right"></span></a>
					<a onclick="moveTo('last');" class="btn btn-sm <c:if test="${paging.lastItemOnPage == paging.numberOfItems}">disabled</c:if>"><span class="glyphicon glyphicon-step-forward"></span></a>
				</div>
			
			</c:when>
			<c:otherwise>
				<table style="width: 100%">
				<tr>
				
				<td style="min-width: 620px; max-width: 620px;">
				
					<h2 style="margin-top: -10px;"><spring:message code="label.SearchYourAddressBook" /></h2>
					
					<div style="float: right">
						<a rel="tooltip" title="<spring:message code="label.Configure" />" onclick="showConfigure();" class="btn btn-default"><i class="icon-wrench"></i> <spring:message code="label.Configure" /></a>
					</div>
					
					<input rel="tooltip" title="<spring:message code="label.Search" />" type="button" onclick="searchStatic(true, true);" class="btn btn-default" value="<spring:message code="label.Search" />" />
					<input rel="tooltip" title="<spring:message code="label.ResetFilter" />" type="button" onclick="removeContactFilter();searchStatic(false, true);" class="btn btn-default" value="<spring:message code="label.Reset" />" />
					<div class="dialog-wait-image hideme"></div>
					
					<div id="auto-load-contentheader" style="max-width: 532px; overflow: hidden; margin-top: 10px;">
						<table id="participantsstaticheader" class="table table-bordered table-striped table-styled" style="table-layout: fixed; margin-bottom: 0px;">
							<thead>
								<tr>
									<th style="width: 15px; text-align: center"><input class="select-all-searched" onclick="checkCheckedSearchedAttendees(this);" type="checkbox" style="margin: 0px" checked="checked" /></th>
									<th><spring:message code="label.Name" /></th>
									<th><spring:message code="label.Email" /></th>
									<c:forEach items="${attributeNames}" var="attributeName">
										<th class="attribute" data-id="<esapi:encodeForHTMLAttribute>${attributeName.id}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML>${attributeName.name}</esapi:encodeForHTML></th>
									</c:forEach>
								</tr>
								<tr class="table-styled-filter">
									<th class="filtercell" style="min-width: 0px; width: 20px;">&#160;</th>
									<th class="filtercell">
										<input onkeyup="checkFilterCell($(this).closest('.filtercell'), true)" id="namefilterstatic" type="text" maxlength="100" value="<esapi:encodeForHTMLAttribute>${filter['name']}</esapi:encodeForHTMLAttribute>" style="margin:0px;" />
									</th>
									<th class="filtercell">
										<input onkeyup="checkFilterCell($(this).closest('.filtercell'), true)"  id="emailfilterstatic" type="text" maxlength="100" value="<esapi:encodeForHTMLAttribute>${filter['email']}</esapi:encodeForHTMLAttribute>" style="margin:0px;" />
									</th>
									<c:forEach items="${attributeNames}" var="attributeName" varStatus="rowCounter">
										<th class="attributefilter filtercell">
											<c:choose>
												<c:when test='${attributeName.name.equals("Owner")}'>
													<input onkeyup="checkFilterCell($(this).closest('.filtercell'), true)" class="filter" type="text" maxlength="100" name="owner" value='<esapi:encodeForHTMLAttribute>${filter["owner"]}</esapi:encodeForHTMLAttribute>' style="margin:0px;" />
												</c:when>
												<c:otherwise>
													<input onkeyup="checkFilterCell($(this).closest('.filtercell'), true)" class="filter" type="text" maxlength="100" name="<esapi:encodeForHTMLAttribute>${attributeName.id}</esapi:encodeForHTMLAttribute>" value="<esapi:encodeForHTMLAttribute>${filter[attributeName.id.toString()]}</esapi:encodeForHTMLAttribute>" style="margin:0px;" />
												</c:otherwise>
											</c:choose>
										</th>
									</c:forEach>
								</tr>
							</thead>					
						</table>
					</div>
					
					<div id="auto-load-content" style="max-width: 550px; margin-top: 0px; height: 350px; overflow-x: auto; overflow-y: scroll;">		
						<table id="participantsstatic" style="table-layout: fixed">
							<tbody></tbody>
						</table>
					</div>		
				</td>
				
				<td style="width: 50px; padding-top: 200px; padding-left: 10px; padding-right: 10px;">
					<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Add" />" onclick="selectAllAttendees()" class="btn btn-default"><span class="glyphicon glyphicon-forward"></span></a><br /><br />
					<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Remove" />"onclick="removeAttendees()" class="btn btn-default"><span class="glyphicon glyphicon-backward"></span></a>
					
					<div id="pagerStatic" style="text-align: center; margin-bottom: 10px; margin-top: 10px; display: none;">
						<a id="btnFirstStatic" onclick="moveTo('first','static');" class="middle btn btn-sm <c:if test="${paging.firstItemOnPage == 1}">disabled</c:if>"><span class="glyphicon glyphicon-step-backward"></span></a>
						<a id="btnPreviousStatic" onclick="moveTo('previous','static');" class="btn btn-sm <c:if test="${paging.firstItemOnPage == 1}">disabled</c:if>"><span class="glyphicon glyphicon-chevron-left"></span></a>				
						<a id="btnLastStatic" onclick="moveTo('last','static');" class="btn btn-sm <c:if test="${paging.lastItemOnPage == paging.numberOfItems}">disabled</c:if>"><span class="glyphicon glyphicon-step-forward"></span></a>
					</div>
				</td>
				
				<td style="width: 250px; vertical-align: top;">
					<h2 style="margin-top: -10px;"><spring:message code="label.SelectedContacts" /></h2>			
					
					<a rel="tooltip" title="<spring:message code="label.ClearAll" />" onclick="removeAllAttendees()" class="btn btn-default"><spring:message code="label.ClearAll" /></a>
						
					<div class="modal250" style="margin-top: 10px; height: 400px; overflow: auto; border: 1px solid #ddd;">
						<table id="selectedparticipantsstatic" class="table table-bordered table-styled" >
							<thead>
								<tr>
									<th style="width: 15px;"><input onclick="checkCheckedSelectedAttendees(this);" type="checkbox" style="margin: 0px;" /></th>
									<th><spring:message code="label.Name" /> (<spring:message code="label.Email" />)</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${selectedParticipationGroup.attendees}" var="attendee">
									<tr>
										<th><input name="<esapi:encodeForHTMLAttribute>${attendee.id}</esapi:encodeForHTMLAttribute>" type="checkbox" /></th>
										<td><esapi:encodeForHTML>${attendee.name} (${attendee.email})</esapi:encodeForHTML></td>								
									</tr>
								</c:forEach>
				
							</tbody>
						</table>
					</div>
								
					<div id="pager2" style="text-align: center; display: none;">
						<a onclick="moveTo('first');" class="middle btn btn-sm <c:if test="${paging.firstItemOnPage == 1}">disabled</c:if>"><i class="icon-step-backward"></i></a>
						<a onclick="moveTo('${paging.currentPage-1}');" class="btn btn-sm <c:if test="${paging.firstItemOnPage == 1}">disabled</c:if>"><span class="glyphicon glyphicon-chevron-left"></span></a>				
						<span>${pagingElementName} ${paging.firstItemOnPage} <spring:message code="label.To" /> ${paging.lastItemOnPage} <spring:message code="label.of" /> ${paging.numberOfItems}</span>		
						<a onclick="moveTo('${paging.currentPage+1}');" class="btn btn-sm <c:if test="${paging.lastItemOnPage == paging.numberOfItems}">disabled</c:if>"><span class="glyphicon glyphicon-chevron-right"></span></a>
						<a onclick="moveTo('last');" class="btn btn-sm <c:if test="${paging.lastItemOnPage == paging.numberOfItems}">disabled</c:if>"><span class="glyphicon glyphicon-step-forward"></span></a>
					</div>
				</td>
				</tr>
				</table>
				
				<div style="clear: both"></div>
		
				<div style="margin-top: 5px">
					<spring:message code="label.NumberOfResults" />: <span id="totalResultsStatic"></span>
					<a id="btnNextStatic" onclick="moveTo('next','static');" class="visiblelink <c:if test="${paging.lastItemOnPage == paging.numberOfItems}">disabled</c:if>"><spring:message code="label.more" /></a>
				</div>
			</c:otherwise>
		</c:choose>
		
	</div>
	<div class="modal-footer">
		<img id="add-wait-animation2-static" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
		
		<c:if test="${readonly == null}">		
			<div style="float: left; text-align: left; width: 120px; height: 20px;">
				<a rel="tooltip" title="<spring:message code="label.Back" />"  onclick="step1();" class="btn btn-default"><spring:message code="label.Back" /></a>
			</div>
		</c:if>
		
		<div style="float: right; width: 120px; text-align: right;">
			<a rel="tooltip" title="<spring:message code="label.Cancel" />" href="<c:url value="/${form.survey.shortname}/management/participants" />" class="btn btn-default"><spring:message code="label.Cancel" /></a>
		</div>
		
		<c:if test="${readonly == null}">	
			<a rel="tooltip" title="<spring:message code="label.Save" />"  onclick="saveStatic();" class="btn btn-info"><spring:message code="label.Save" /></a>
		</c:if>		
	</div>
	</div>
	</div>
</div>


	