<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<div class="modal" id="import-attendees-step1-dialog" data-backdrop="static">
	<div class="modal-dialog">
    <div class="modal-content">
	<form:form action="${contextpath}/addressbook?${_csrf.parameterName}=${_csrf.token}" modelAttribute="uploadItem" name="frm" id="import-attendees-form" method="post" enctype="multipart/form-data" style="margin: 0px;">
		<input type="hidden" name="target" value="importAttendees" />
		<div class="modal-header">
			<b><spring:message code="label.ImportContactsStep1" /></b>
		</div>
		<div class="modal-body">
			<div id="file-uploader-contacts">
				<noscript>
				    <p>Please enable JavaScript to use file uploader.</p>
				</noscript>
			</div>		
			<input type="hidden" name="file" id="file-uploader-contacts-file" />
			<input type="hidden" name="filename" id="file-uploader-contacts-filename" />
			<span id="loaded-file"><c:if test="${file != null}"><esapi:encodeForHTML>${file}</esapi:encodeForHTML></c:if></span>
			<span id="file-uploader-contacts-filename-display"></span>
			<div id="file-uploader-contacts-delimiter-div" class="hideme"">
				<spring:message code="label.Delimiter" /><br />
				<select id="file-uploader-contacts-delimiter" name="delimiter" style="width:60px">
					<option value="comma">,</option>
					<option value="semicolon">;</option>
				</select>
			</div>
			<div style="color: #f00; margin-right: 20px;" class="hideme" id="import-attendees-step1-error"><spring:message code="message.SelectFile" /></div>
			<br /><input type="checkbox" class="check" value="header" name="header" checked="checked" /><spring:message code="label.DocumentContainsAHeaderRow" />
		</div>
		<div class="modal-footer">
			<div style="right: 10px; position: absolute;">
				<a  class="btn btn-default" onclick="cancelAttendeesImport()"><spring:message code="label.Cancel" /></a>
			</div>
			<a onclick="checkFile();" class="btn btn-primary"><spring:message code="label.Next" /></a>					
		</div>
	</form:form>
	</div>
	</div>
</div>

<c:if test="${fileheaders != null}">

	<div class="modal" id="import-attendees-step2-dialog" data-backdrop="static">
		<div class="modal-dialog">
    	<div class="modal-content">
		<div class="modal-header">
			<b><spring:message code="label.ImportContactsStep2" /></b>
		</div>
		<div class="modal-body">
			<table class="table table-bordered table-striped">
				<tr>
					<th>
						<c:choose>
							<c:when test="${hasHeaderRow != null && hasHeaderRow == false }">
								<spring:message code="label.Example" />
							</c:when>
							<c:otherwise>
								<spring:message code="label.Header" />
							</c:otherwise>
						</c:choose>
					</th>
					<th><spring:message code="label.MappedAttribute" /></th>
				</tr>
				
				<c:forEach items="${fileheaders}" var="fileheader" varStatus="rowCounter">
					<tr>
						<td><esapi:encodeForHTML>${fileheader}</esapi:encodeForHTML></td>
						<td>
							<select name="header${rowCounter.index}" class="importmappings" onchange="showAddAttributeDialog(this);">
								<option value="Choose"><spring:message code="label.Choose" /></option>
								<option value="new"><spring:message code="label.New" />...</option>
								<c:forEach items="${allAttributeNames}" var="n">
									<c:choose>
										<c:when test="${headermappings.containsKey(fileheader) && headermappings.get(fileheader).equalsIgnoreCase(n.name) }">
											<option value="<esapi:encodeForHTMLAttribute>${n.name}</esapi:encodeForHTMLAttribute>" selected="selected"><esapi:encodeForHTML>${n.name}</esapi:encodeForHTML></option>
										</c:when>
										<c:otherwise>
											<option value="<esapi:encodeForHTMLAttribute>${n.name}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML>${n.name}</esapi:encodeForHTML></option>
										</c:otherwise>
									</c:choose>
									<c:if test='${n.name.equals("Name")}'>
										<option disabled="disabled">&#8212;&#8212;&#8212;&#8212;&#8212;&#8212;&#8212;</option>
									</c:if>
								</c:forEach>
							</select>
						</td>
					</tr>
				</c:forEach>				
			</table>
			<span id="import-attendees-step2-error-multiple" class="hideme" style="color: #f00; margin-right: 20px;">
				<spring:message code="label.Attribute" />&#32; <span id="import-attendees-step2-error-multiple-text"></span> <spring:message code="label.usedMoreThanOnce" />
			</span>			
			<span id="import-attendees-step2-error-no-attribute" class="hideme" style="color: #f00; margin-right: 20px;">
				<spring:message code="label.NoAttributeNameFor" />&#32; <span id="import-attendees-step2-error-no-attribute-text"></span>
			</span>	
		</div>
		<div class="modal-footer">
			<div style="float: left">
				<a onclick="step1();" class="btn btn-default"><spring:message code="label.Back" /></a>
			</div>
			<div style="float: right">
				<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
			</div>		
			<a onclick="step3();" class="btn btn-primary"><spring:message code="label.Next" /></a>		
		</div>
		</div>
		</div>
	</div>
	
	<div class="modal" id="import-attendees-step3-dialog" data-backdrop="static">
		<div class="modal-dialog">
    	<div class="modal-content">
		<form:form action="${contextpath}/addressbook" name="frm" id="import-attendees-step3-form" method="post" style="margin: 0px;">
			<input type="hidden" name="target" id="import-attendees-step3-form-target" value="${target}" />
		
			<div class="modal-header">
				<b><spring:message code="label.ImportContactsStep3" /></b>
			</div>
			<div class="modal-body" style="overflow: overlay;">
				<div class="modal200" style="max-height: 610px; overflow: auto">
				<table class="table table-bordered table-styled">
					<thead>
						<tr>
							<th><input id="checkAll" onchange="checkChanged();" checked="checked" type="checkbox" /></th>
							<c:forEach items="${fileheaders}" var="fileheader" varStatus="rowCounter">
								<th id="header${rowCounter.index}" data-index="${rowCounter.index}" class="mappedheader"><esapi:encodeForHTML>${fileheader}</esapi:encodeForHTML></th>
							</c:forEach>
						</tr>
					</thead>
					<tbody>					
						<c:forEach items="${rows}" var="row" varStatus="rowCounter">
							<c:choose>
								<c:when test="${existing[rowCounter.index]}">
									<tr style="background-color: rgb(255, 232, 117)">					
										<td><input name="row${rowCounter.index}" type="checkbox" /></td>
										<c:forEach items="${row}" var="item" varStatus="rowCounter2">					
											<td class="col${rowCounter2.index}"><esapi:encodeForHTML>${item}</esapi:encodeForHTML></td>
										</c:forEach>
									</tr>
								</c:when>
								<c:when test="${valid[rowCounter.index]}">
									<tr>	
										<td><input name="row${rowCounter.index}" type="checkbox" checked="checked" /></td>
										<c:forEach items="${row}" var="item" varStatus="rowCounter2">					
											<td class="col${rowCounter2.index}"><esapi:encodeForHTML>${item}</esapi:encodeForHTML></td>
										</c:forEach>
									</tr>
								</c:when>
								<c:otherwise>
									<tr style="background-color: rgb(255, 186, 186)">					
										<td><input type="checkbox" disabled="disabled" /></td>
										<c:forEach items="${row}" var="item">					
											<td><esapi:encodeForHTML>${item}</esapi:encodeForHTML></td>
										</c:forEach>
									</tr>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</tbody>	
				</table>
				</div>
				<c:if test="${invalidAttendees.size() > 0}"><span style="color: #f00; margin-left: 10px;"><spring:message code="error.invalidContacts" /></span></c:if>	
				<c:if test="${existingContact}"><spring:message code="info.exitingContacts" /></c:if>
				<span style="color: #f00; margin-left: 10px;" id="import-attendees-step3-error"></span>
			</div>
			<div class="modal-footer">
				<div style="float: left">
					<a onclick="step2();" class="btn btn-default"><spring:message code="label.Back" /></a>
				</div>
				<div style="float: right">
					<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
				</div>		
				<input type="submit" class="btn btn-primary" value="<spring:message code="label.Save" />" />		
			</div>
		
		</form:form>
		</div>
		</div>
	</div>

</c:if>
	
	<c:if test="${importmessages != null}">
		<script type="text/javascript">
		$("#import-attendees-step1-dialog").modal("show");
		$("#import-attendees-step1-error").html("${importmessages.get(0)}").show();
		</script>
	</c:if>