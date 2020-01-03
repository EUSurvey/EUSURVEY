<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<div class="modal" id="add-share-dialog1" data-backdrop="static">
	<div class="modal-dialog modal-sm">
    <div class="modal-content">
	<div class="modal-header" style="font-weight: bold;">
		<spring:message code="label.CreateShareStep1" />
	</div>
	<div class="modal-body" style="padding-left: 30px;">		
		<label for="add-share-name"><span class="mandatory">*</span><spring:message code="label.Name" /></label>
		<input class="required" type="text" id="add-share-name" name="add-share-name" maxlength="255" style="width:220px;" value="<esapi:encodeForHTMLAttribute>${shareToEdit.name}</esapi:encodeForHTMLAttribute>" />
		<select class="required" id="add-share-type" name="add-share-type" style="width:auto; display: none;">
 			<option selected="selected" value="static"><spring:message code="label.FromAddressBook" /></option>
		</select>		
		<br /><br />
		<label for="add-share-mode"><span class="mandatory">*</span><spring:message code="label.Mode" /></label><br />
		<select class="required" id="add-share-mode" name="add-share-mode" style="width:auto;">
 			<option <c:if test="${shareToEdit.readonly}">selected="selected"</c:if> value="readonly"><spring:message code="label.ReadingAccess" /></option>
 			<option <c:if test="${!shareToEdit.readonly}">selected="selected"</c:if> value="readwrite"><spring:message code="label.ReadWriteAccess" /></option>
		</select>		

	</div>
	<div class="modal-footer">
		<img id="add-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
		
		<div style="float: right; text-align: right;">
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
		</div>
			
		<a  onclick="step2();" class="btn btn-primary"><spring:message code="label.Next" /></a>			
	</div>
	</div>
	</div>
</div>
	
	