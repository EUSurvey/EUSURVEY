<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<div class="modal" id="add-participants-dialog1" data-backdrop="static">
	<div class="modal-dialog">
    <div class="modal-content">
	<div class="modal-header" style="font-weight: bold;">
		<spring:message code="label.CreateNewGuestlist" /> - <spring:message code="label.BasicSettings" /> (1/2)
	</div>
	<div class="modal-body" style="padding-left: 30px;">		
		<label for="add-participants-name"><span class="mandatory">*</span><spring:message code="guestlist.name" /></label><br />
		<input class="small-form-control required" type="text" maxlength="255" id="add-participants-name" name="add-participants-name" style="width:220px;" /><br />
		<label for="add-participants-type"><span class="mandatory">*</span><spring:message code="label.Type" /></label><br />
		<select class="required small-form-control" id="add-participants-type" name="add-participants-type" style="width:auto;">
 			<option value="static"><spring:message code="label.FromAddressBook" /></option>
 			<c:if test="${USER.getGlobalPrivilegeValue('ECAccess') > 0}">
 				<option value="departments"><spring:message code="label.EC" /></option>
 			</c:if>
 			<option value="tokens"><spring:message code="label.Tokens" /></option>
		</select>		

	</div>
	<div class="modal-footer">
		<img id="add-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
		
		<div style="float: left; width: 120px; height: 20px;">
		
		</div>
		
		<div style="float: right; width: 120px; text-align: right;">
			<a href="<c:url value="/${form.survey.shortname}/management/participants" />" class="btn btn-default"><spring:message code="label.Cancel" /></a>
		</div>
		
		<a id="btnStep2FromAddParticipant"  onclick="step2();" class="btn btn-info"><spring:message code="label.Next" /></a>
						
	</div>
	</div>
	</div>
</div>
	
	