<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<div class="modal" id="view-share-dialog" data-backdrop="static">
	<div class="modal-dialog" role="document">
    <div class="modal-content">
	<div class="modal-header">
		<spring:message code="label.ViewShare" />&nbsp;<i><esapi:encodeForHTML>${shareToEdit.name}</esapi:encodeForHTML></i>
	</div>
	<div class="modal-body" style="padding-left: 30px;">		
		<b><spring:message code="label.Mode" /></b>: 
		<c:choose>
			<c:when test="${shareToEdit.readonly}"><spring:message code="label.ReadingAccess" /></c:when>
			<c:otherwise><spring:message code="label.ReadWriteAccess" /></c:otherwise>
		</c:choose><br />
		
		<b><spring:message code="label.Owner" /></b>: 
		<esapi:encodeForHTML>${shareToEdit.owner.name}</esapi:encodeForHTML><br />
		
		<b><spring:message code="label.Recipient" /></b>: 
		<esapi:encodeForHTML>${shareToEdit.recipient.name}</esapi:encodeForHTML><br />
		
		<div style="margin-top: 10px">
			<table class="table table-bordered table-striped" >
				<thead>
					<tr>
						<th><spring:message code="label.SelectedContacts" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${shareToEdit.attendees}" var="attendee">
						<tr>
							<td><esapi:encodeForHTML>${attendee.name} (${attendee.email})</esapi:encodeForHTML></td>								
						</tr>
					</c:forEach>	
				</tbody>
			</table>
		</div>

	</div>
	<div class="modal-footer">
		<a  class="btn btn-primary" data-dismiss="modal"><spring:message code="label.OK" /></a>			
	</div>
	</div>
	</div>
</div>
	
	