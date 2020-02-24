<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<div class="modal" id="batch-attendee-dialog" data-backdrop="static">
	<div class="modal-dialog">
    <div class="modal-content">
	<form:form action="${contextpath}/addressbook/batchEdit" name="frm"
		id="batch-edit-form" method="post" style="margin: 0px;">
		<div class="modal-header">
			<b><spring:message code="label.EditSelectedContacts" /></b>
		</div>
		<div class="modal-body">

			<div id="batch-attendee-dialog-step1">
				<input type="checkbox" class="check" id="checkall"
					onclick="checkAll();" /><b><spring:message code="label.SelectAll" /></b><br />

				<c:forEach items="${batchAttendees}" var="attendee">
					<c:choose>
						<c:when test="${selectedAttendees.contains(attendee.id)}">
							<input checked="checked" onclick="uncheck(this);" type="checkbox"
								class="check attendee" name="batchAttendee<esapi:encodeForHTMLAttribute>${attendee.id}</esapi:encodeForHTMLAttribute>"
								value="<esapi:encodeForHTMLAttribute>${attendee.id}</esapi:encodeForHTMLAttribute>" /><esapi:encodeForHTML>${attendee.name} (${attendee.email})</esapi:encodeForHTML><br />
						</c:when>
						<c:otherwise>
							<input onclick="uncheck(this);" type="checkbox"
								class="check attendee" name="batchAttendee<esapi:encodeForHTMLAttribute>${attendee.id}</esapi:encodeForHTMLAttribute>"
								value="<esapi:encodeForHTMLAttribute>${attendee.id}</esapi:encodeForHTMLAttribute>" /><esapi:encodeForHTML>${attendee.name} (${attendee.email})</esapi:encodeForHTML><br />
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</div>

			<div id="batch-attendee-dialog-step2" style="display: none">
				<div id="batch-attendee-dialog-step2-scrolldiv"
					style="height: 320px; overflow-y: auto; margin-bottom: 15px;">
					<table id="batch-attendee-dialog-table"	class="table table-bordered">
						<tbody>
							<tr>
								<td style="background-color: #eee"><spring:message code="label.Name" /></td>
								<td style="width: 250px; background-color: #eee"><select class="form-control" onchange="checkAttributeSelection(this)" name="name" id="batch-name">
										<option value="0" selected="selected">
											<spring:message code="label.KeepValue" />
										</option>
										<option value="-2">
											<spring:message code="label.NewValue" />
										</option>

										<option disabled="disabled">&#8212;&#8212;&#8212;&#8212;&#8212;&#8212;&#8212;</option>

										<c:forEach items="${names}" var="value">
											<option><esapi:encodeForHTML>${value}</esapi:encodeForHTML></option>
										</c:forEach>
								</select>
									<div id="batch-name-error" class="validation-error hideme">
										<spring:message code="validation.required" />
									</div></td>
							</tr>
							<tr>
								<td style="background-color: #eee"><spring:message code="label.Email" /></td>
								<td style="width: 250px; background-color: #eee"><select class="form-control" onchange="checkAttributeSelection(this)" name="email"
									id="batch-email">
										<option value="0" selected="selected">
											<spring:message code="label.KeepValue" />
										</option>
										<option value="-2">
											<spring:message code="label.NewValue" />
										</option>

										<option disabled="disabled">&#8212;&#8212;&#8212;&#8212;&#8212;&#8212;&#8212;</option>

										<c:forEach items="${emails}" var="value">
											<option><esapi:encodeForHTML>${value}</esapi:encodeForHTML></option>
										</c:forEach>
								</select>
									<div id="batch-email-error" class="validation-error hideme">
										<spring:message code="error.InvalidEmail" />
									</div></td>
							</tr>

							<c:if test="${USER.getGlobalPrivilegeValue('ContactManagement') == 2}">
								<tr>
									<td style="background-color: #eee"><spring:message code="label.Owner" /></td>
									<td style="width: 250px; background-color: #eee">
										<input class="form-control"	id="batch-owner" type="text" name="owner" />
										<div id="batch-owner-error" class="validation-error hideme">
											<spring:message code="message.UserDoesNotExist" />
										</div></td>
								</tr>
							</c:if>

							<c:forEach items="${attributeNames}" var="attributeName">
								<c:if test="${attributeName.name != 'Owner'}">
									<tr>
										<td><esapi:encodeForHTML>${attributeName.name}</esapi:encodeForHTML><input type="hidden" class="existingbatchkey" value="<esapi:encodeForHTMLAttribute>${attributeName.name}</esapi:encodeForHTMLAttribute>" /></td>
										<td style="width: 250px;"><select class="form-control" onchange="checkAttributeSelection(this)"
											name="attribute<esapi:encodeForHTMLAttribute>${attributeName.id}</esapi:encodeForHTMLAttribute>">
												<option value="0" selected="selected">
													<spring:message code="label.KeepValue" />
												</option>
												<option value="-1">
													<spring:message code="label.ClearValue" />
												</option>
												<option value="-2">
													<spring:message code="label.NewValue" />
												</option>

												<c:if test="${attributeValues.get(attributeName.id).size() > 0}">
													<option disabled="disabled">&#8212;&#8212;&#8212;&#8212;&#8212;&#8212;&#8212;</option>
												</c:if>

												<c:forEach items="${attributeValues.get(attributeName.id)}" var="value">
													<option><esapi:encodeForHTML>${value}</esapi:encodeForHTML></option>
												</c:forEach>
										</select></td>
									</tr>
								</c:if>
							</c:forEach>

						</tbody>
					</table>
				</div>

				<a onclick="createAttributeSelector('#batch-attendee-dialog-table')" data-toggle="tooltip"
					title="<spring:message code="label.AddAttribute" />" class="btn btn-default"><img
					src="${contextpath}/resources/images/add2.png" /></a>

			</div>

		</div>
		<div class="modal-footer">
			<span style="color: #f00; margin-right: 20px;" class="hideme"
				id="import-attendees-step1-error"><spring:message
					code="message.SelectFile" /></span>
			<a id="batch-update-button"  onclick="checkOwnerAndSubmit()"
				class="btn btn-primary"><spring:message code="label.Update" /></a> <a
				 class="btn btn-default" data-dismiss="modal"><spring:message
					code="label.Cancel" /></a> <a id="batch-next-button" 
				class="btn btn-default" onclick="batchStep2()"><spring:message
					code="label.Next" /></a>
		</div>
	</form:form>
	</div>
	</div>
</div>

<c:if test="${attributeValues != null}">
	<script type="text/javascript">
		$("#batch-attendee-dialog-num").text('${selectedAttendees.size()}');
		$("#batch-attendee-dialog").modal();
		batchStep2();
	</script>
</c:if>

<div class="modal" id="add-attribute-value-dialog"data-backdrop="static">
	<div class="modal-dialog">
    <div class="modal-content">
	<div class="modal-header">
		<b><spring:message code="label.CreateNewValue" /></b>
	</div>
	<div class="modal-body">
		<input type="text" id="new-attribute-value" style="width: 250px;" /><br />
		<span style="color: #f00; margin-left: 10px;"
			id="new-attribute-value-error"></span>
	</div>
	<div class="modal-footer">
		<a 
			onclick="addAttributeValue($('#new-attribute-value').val());"
			class="btn btn-primary"><spring:message code="label.Set" /></a> <a
			 class="btn btn-default" onclick="cancelAddAttributeValue()"><spring:message
				code="label.Cancel" /></a>
	</div>
	</div>
	</div>
</div>

	<div class="modal" id="no-selection-dialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
		<div class="modal-body">
			<spring:message code="message.NoElementSelected" />						
		</div>
		<div class="modal-footer">
			<a  class="btn btn-primary" data-dismiss="modal"><spring:message code="label.OK" /></a>		
		</div>
		</div>
		</div>
	</div>	
