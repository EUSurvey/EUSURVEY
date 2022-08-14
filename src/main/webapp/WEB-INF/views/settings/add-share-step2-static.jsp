<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<div class="modal" id="add-share-dialog2-static" data-backdrop="static">
	<div class="modal-dialog modal-lg">
    <div class="modal-content">
	<div class="modal-header" style="font-weight: bold;"><spring:message code="label.CreateShareStep2" />: <spring:message code="label.SelectContactsToShare" /></div>
	<div class="modal-body">	
		
		<table style="width: 100%">
			<tr>
				<td style="vertical-align: top;">
					<h2><spring:message code="label.SearchYourAddressBook" /></h2>		
					<div style="float: right">
						<a onclick="showConfigure();" class="btn btn-default"><i class="icon-wrench"></i> <spring:message code="label.Configure" /></a>
					</div>
					
					<input type="button" onclick="searchStatic(true, true, false);" class="btn btn-default" value="Search"/>
					<div class="dialog-wait-image hideme"></div>
				
					<div id="auto-load-contentheader" style="max-width: 532px; overflow: hidden; margin-top: 10px;">
						<table id="participantsstaticheader" class="table table-bordered table-striped table-styled" style="table-layout: fixed; margin-bottom: 0px;">
							<thead>
								<tr>
									<th style="width: 20px; text-align: center"><input class="select-all-searched" onclick="checkCheckedSearchedAttendees(this);" type="checkbox" style="margin: 0px" checked="checked" /></th>
									<th><spring:message code="label.Name" /></th>
									<th><spring:message code="label.Email" /></th>
									<c:forEach items="${attributeNames}" var="attributeName">
										<th class="attribute" data-id="<esapi:encodeForHTMLAttribute>${"Owner".equals(attributeName.name) ? "owner" : attributeName.id}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML>${attributeName.name}</esapi:encodeForHTML></th>
									</c:forEach>
								</tr>
								<tr class="table-styled-filter">
									<th class="filtercell" style="width: 20px;">&#160;</th>
									<th class="filtercell">
										<input  onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" id="namefilterstatic" type="text" value="<esapi:encodeForHTMLAttribute>${filter['name']}</esapi:encodeForHTMLAttribute>" style="margin:0px;" />
									</th>
									<th class="filtercell">
										<input  onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" id="emailfilterstatic" type="text" value="<esapi:encodeForHTMLAttribute>${filter['email']}</esapi:encodeForHTMLAttribute>" style="margin:0px;" />
									</th>
									<c:forEach items="${attributeNames}" var="attributeName" varStatus="rowCounter">
										<th class="attributefilter filtercell">
											<input  onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" class="filter" type="text" name="<esapi:encodeForHTMLAttribute>${"Owner".equals(attributeName.name) ? "owner" : attributeName.id}</esapi:encodeForHTMLAttribute>" value="<esapi:encodeForHTMLAttribute>${filter[attributeName.id.toString()]}</esapi:encodeForHTMLAttribute>" style="margin:0px;" />
										</th>
									</c:forEach>
								</tr>
							</thead>
						</table>
					</div>
				
					<div class="modal330" id="auto-load-content" style="max-width: 550px; overflow-x: auto; overflow-y: scroll; height: 520px">
								
						<table id="participantsstatic" class="table table-bordered table-striped" style="table-layout: fixed;">
							<tbody>	
							</tbody>
						</table>
						
						<div id="pagerStatic" style="display: none">
							<a id="btnFirstStatic" onclick="moveTo('first','static');" class="middle btn btn-sm <c:if test="${paging.firstItemOnPage == 1}">disabled</c:if>"><i class="icon-step-backward"></i></a>
							<a id="btnPreviousStatic" onclick="moveTo('previous','static');" class="btn btn-sm <c:if test="${paging.firstItemOnPage == 1}">disabled</c:if>"><span class="glyphicon glyphicon-chevron-left"></span></a>				
							<span><spring:message code="label.Contact" /> <span id="firstResultStatic"></span> <spring:message code="label.To" /> <span id="lastResultStatic"></span> <spring:message code="label.of" /> <span id="totalResultsStaticOld"></span></span>
							<a id="btnNextStatic" onclick="moveTo('next','static');" class="btn btn-sm <c:if test="${paging.lastItemOnPage == paging.numberOfItems}">disabled</c:if>"><span class="glyphicon glyphicon-chevron-right"></span></a>
							<a id="btnLastStatic" onclick="moveTo('last','static');" class="btn btn-sm <c:if test="${paging.lastItemOnPage == paging.numberOfItems}">disabled</c:if>"><i class="icon-step-forward"></i></a>
						</div>
					</div>
				
				</td>
				<td style="vertical-align: top; padding-top: 200px; padding-left: 30px; padding-right: 30px">
					<a data-toggle="tooltip" title="<spring:message code="label.Add" />" onclick="selectAllAttendees()" class="btn btn-default"><span class="glyphicon glyphicon-forward"></span></a><br /><br />
					<a data-toggle="tooltip" title="<spring:message code="label.Remove" />" onclick="removeAttendees()" class="btn btn-default"><span class="glyphicon glyphicon-backward"></span></a>				
				</td>
				<td style="vertical-align: top">
					<h2><spring:message code="label.SelectedContacts" /></h2>
					<a onclick="removeAllAttendees()" class="btn btn-default"><spring:message code="label.Clear" /></a>
				
					<div class="modal330" style="margin-top: 10px; height: 520px; max-height: 520px; max-width: 200px; overflow-y: auto;">
					<table id="selectedparticipantsstatic" class="table table-bordered table-striped table-styled" >
						<thead>
							<tr>
								<th style="width: 15px;"><input onclick="checkCheckedSelectedAttendees(this);" type="checkbox" /></th>
								<th><spring:message code="label.Name" /> (<spring:message code="label.Email" />)</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${shareToEdit.attendees}" var="attendee">
								<tr>
									<td><input name="${attendee.id}" type="checkbox" /></td>
									<td style="max-width: 180px"><esapi:encodeForHTML>${attendee.name} (${attendee.email})</esapi:encodeForHTML></td>								
								</tr>
							</c:forEach>	
						</tbody>
					</table>
					</div>
					
					<div id="pager2" style="text-align: center; display: none;">
						<a onclick="moveTo('first');" class="middle btn btn-sm <c:if test="${paging.firstItemOnPage == 1}">disabled</c:if>"><span class="glyphicon glyphicon-step-backward"></span></a>
						<a onclick="moveTo('${paging.currentPage-1}');" class="btn btn-sm <c:if test="${paging.firstItemOnPage == 1}">disabled</c:if>"><span class="glyphicon glyphicon-chevron-left"></span></a>				
						<span>${pagingElementName} ${paging.firstItemOnPage} <spring:message code="label.To" /> ${paging.lastItemOnPage} <spring:message code="label.of" /> ${paging.numberOfItems}</span>		
						<a onclick="moveTo('${paging.currentPage+1}');" class="btn btn-sm <c:if test="${paging.lastItemOnPage == paging.numberOfItems}">disabled</c:if>"><span class="glyphicon glyphicon-chevron-right"></span></a>
						<a onclick="moveTo('last');" class="btn btn-sm <c:if test="${paging.lastItemOnPage == paging.numberOfItems}">disabled</c:if>"><span class="glyphicon glyphicon-step-forward"></span></a>
					</div>				
				</td>			
			
			</tr>
		</table>
		
		<div style="clear: both"></div>
		
		<div style="margin-top: 10px">
			<spring:message code="label.NumberOfResults" />: <span id="totalResultsStatic"></span>
			<a id="btnNextStatic" onclick="moveTo('next','static');" class="visiblelink <c:if test="${paging.lastItemOnPage == paging.numberOfItems}">disabled</c:if>"><spring:message code="label.more" /></a>
		</div>
		
	</div>
	<div class="modal-footer">
	
		<div style="float: left; width: 120px; text-align: left;">
			<a  onclick="step1();" class="btn btn-default"><spring:message code="label.Back" /></a>	
		</div>
		
		<div style="float: right; width: 120px; text-align: right;">
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
		</div>
			
		<img id="add-wait-animation2-static" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			
		<a  onclick="step3();" class="btn btn-primary"><spring:message code="label.Next" /></a>
				
	</div>
	</div>
	</div>
</div>
	
	