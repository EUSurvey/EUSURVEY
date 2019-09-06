<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<div class="modal" id="add-participants-dialog2-dynamic" data-backdrop="static" style="width: 930px; margin-left:-465px; height: 650px; margin-top: -325px;">
	<div class="modal-header" style="font-weight: bold; height: 20px;"><spring:message code="label.Step2" /></div>
	<div class="modal-body" style="height: 520px; max-height:none; overflow-y:auto;">	
	
		<h2><spring:message code="label.SearchParticipants" /></h2>
	
		<div id="filter" class="well" style="float: left; margin-top: 10px;">
		
			<span style="font-weight: bold; font-size: 130%;">Filter</span><a style="margin-left: 15px;" onclick="showConfigure();" class="btn btn-default"><i class="icon-wrench"></i> <spring:message code="label.Configure" /></a><br /><br />
			
			<spring:message code="label.Name" /><br />
			<input id="namefilter" type="text" value="${filter['name']}" style="width: 160px;" /><br />
			
			<spring:message code="label.Email" /><br />
			<input id="emailfilter" type="text" value="${filter['email']}" style="width: 160px;" /><br />
		
			<div id="add-participants-dialog2-dynamic-filter">
		
				<c:forEach items="${attributeNames}" var="attributeName" varStatus="rowCounter">
					<span class="filterspan">
						<esapi:encodeForHTML>${attributeName.name}</esapi:encodeForHTML><br />
						<input class="filter" type="text" name="<esapi:encodeForHTMLAttribute>${attributeName.id}</esapi:encodeForHTMLAttribute>" style="width: 160px;" />
					</span><br />
				</c:forEach>
			
			</div>
								
			<input type="button" onclick="search(true, true);" class="btn btn-default" value="<spring:message code="label.Search" />"/>
			<input type="button" onclick="search(false, true);" class="btn btn-default" value="<spring:message code="label.Reset" />"/>
			
		</div>
	
		<div style="float:left; margin-left: 50px; margin-top: 0px; width: 575px;">
					
			<table id="participants" class="table table-bordered table-striped" style="margin-top: 10px;">
				<thead>
					<tr>
						<th><spring:message code="label.Name" /></th>
						<th><spring:message code="label.Email" /></th>
						<c:forEach items="${attributeNames}" var="attributeName">
							<th><esapi:encodeForHTML>${attributeName.name}</esapi:encodeForHTML></th>
						</c:forEach>
					</tr>
				</thead>
				<tbody>
				
					<c:forEach items="${selectedParticipationGroup.attendees}" var="attendee">
						<tr>
							<td><esapi:encodeForHTML>${attendee.name}</esapi:encodeForHTML></td>
							<td><esapi:encodeForHTML>${attendee.email}</esapi:encodeForHTML></td>	
							<c:forEach items="${attributeNames}" var="attributeName">
								<td><esapi:encodeForHTML>${attendee.getAttributeValue(attributeName.id, attributeName.name)}</esapi:encodeForHTML></td>
							</c:forEach>							
						</tr>
					</c:forEach>

				</tbody>
			</table>
			
			<div id="pager" style="text-align: center;">
				<a id="btnFirst" onclick="moveTo('first','dynamic');" class="middle btn btn-sm"><i class="icon-step-backward"></i></a>
				<a id="btnPrevious" onclick="moveTo('previous','dynamic');" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-chevron-left"></span></a>				
				<span><spring:message code="label.Contact" /> <span id="firstResult"></span> <spring:message code="label.To" /> <span id="lastResult"></span> <spring:message code="label.of" /> <span id="totalResults"></span></span>		
				<a id="btnNext" onclick="moveTo('next','dynamic');" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-chevron-right"></span></a>
				<a id="btnLast" onclick="moveTo('last','dynamic');" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-step-forward"></span></a>
			</div>
		</div>			
		
	</div>
	<div class="modal-footer" style="height:30px;">
		<img id="add-wait-animation2" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />

		<div style="float: left; width: 120px; height: 20px;">
			<a  onclick="step1();" class="btn btn-default"><spring:message code="label.Back" /></a>	
		</div>
		
		<div style="float: right; width: 120px; text-align: right;">
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>
		</div>
					
		<a  onclick="save();" class="btn btn-info"><spring:message code="label.Save" /></a>	
	</div>	
</div>
	
	