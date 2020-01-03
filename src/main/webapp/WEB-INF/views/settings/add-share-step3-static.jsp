<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<div class="modal" id="add-share-dialog3-static" data-backdrop="static" style="">
	<div class="modal-dialog">
    <div class="modal-content">
	<div class="modal-header" style="font-weight: bold;"><spring:message code="label.CreateShareStep3" />: <spring:message code="label.SelectRecipientForShare" /></div>
	<div class="modal-body">	
	
		<spring:message code="label.Name" /><br />
		
		<c:choose>
			<c:when test="${shareToEdit != null}">
				<input id="recipientname" type="text" value="<esapi:encodeForHTMLAttribute>${shareToEdit.recipient.login}</esapi:encodeForHTMLAttribute>" style="width: 160px;" /><br />
			</c:when>
			<c:otherwise>
				<input id="recipientname" type="text" value="<esapi:encodeForHTMLAttribute>${filter['name']}</esapi:encodeForHTMLAttribute>" style="width: 160px;" /><br />
			</c:otherwise>
		</c:choose>
		
		<div id="add-share-dialog3-error" class="error" style="color: #f00;"><spring:message code="message.UserDoesNotExist" /></div>
		
	</div>
	<div class="modal-footer">	
		<div style="float: left; width: 120px; text-align: left;">
			<a  onclick="step2from3();" class="btn btn-default"><spring:message code="label.Back" /></a>
		</div>		
		<div style="float: right; width: 120px; text-align: right;">
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
		</div>	
		<img id="add-wait-animation2-static" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
		<a  onclick="checkRecipientAndSubmit();" class="btn btn-primary"><spring:message code="label.Save" /></a>
	</div>	
	</div>
	</div>
</div>
	
	