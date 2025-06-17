<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page trimDirectiveWhitespaces="true" %>

<div id="BRPConfirmationDialog" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="windowTitleLabel" aria-hidden="true">
	<div class="modal-dialog">
    <div class="modal-content">
	<div class="modal-header">
		<h3><spring:message code="label.Confirmation" /></h3>
	</div>
	<div class="modal-body">
		<div class="divDialogElements"><spring:message code="message.BRPConfirmation" /></div>
	</div>
	<div class="modal-footer">
		<a class="btn btn-primary" onclick="$('#BRPConfirmationDialog').modal('hide');"><spring:message code="label.OK" /></a>
		<a class="btn btn-default" onclick="$('#myonoffswitchopc').prop('checked', true); $('#BRPConfirmationDialog').modal('hide');"><spring:message code="label.Cancel" /></a>
	</div>
	</div>
	</div>	
</div>

<div id="publishConfirmationDialog" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="windowTitleLabel" aria-hidden="true">
	<div class="modal-dialog">
    <div class="modal-content">
	<div class="modal-header">
		<a  class="close" data-dismiss="modal" onclick="publishConfirmationClose();">&times;</a>
		<h3><spring:message code="label.Confirmation" /></h3>
	</div>
	<div class="modal-body">
		<div class="divDialogElements"><spring:message code="message.StartDatePastNow" /></div>
	</div>
	<div class="modal-footer">
		<a  class="btn btn-primary" onclick="publishConfirmationOkClicked();"><spring:message code="label.OK" /></a>
		<a  class="btn btn-default" onclick="publishConfirmationClose();"><spring:message code="label.Cancel" /></a>
	</div>
	</div>
	</div>	
</div>

<div id="publishConfirmationDialog2" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="windowTitleLabel" aria-hidden="true">
	<div class="modal-dialog">
    <div class="modal-content">
    <div class="modal-header">
		<a  class="close" data-dismiss="modal" onclick="publishConfirmationClose();">&times;</a>
		<h3><spring:message code="label.Confirmation" /></h3>
	</div>
	<div class="modal-body">
		<div class="divDialogElements"><spring:message code="message.StartDateFutureNow" /></div>
	</div>
	<div class="modal-footer">
		<a  class="btn btn-primary" onclick="publishConfirmationOkClicked();"><spring:message code="label.Proceed" /></a>
		<a  class="btn btn-default" onclick="publishConfirmationClose();"><spring:message code="label.Cancel" /></a>
	</div>
	</div>
	</div>			
</div>

<div id="publishConfirmationDialog3" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="windowTitleLabel" aria-hidden="true">
	<div class="modal-dialog">
    <div class="modal-content">
    <div class="modal-header">
		<a  class="close" data-dismiss="modal" onclick="publishConfirmationClose();">&times;</a>
		<h3><spring:message code="label.Confirmation" /></h3>
	</div>
	<div class="modal-body">
		<div class="divDialogElements"><spring:message code="message.StartDateFutureSoon" /></div>
	</div>
	<div class="modal-footer">
		<a  class="btn btn-primary" onclick="publishConfirmationOkClicked();"><spring:message code="label.Proceed" /></a>
		<a  class="btn btn-default" onclick="publishConfirmationClose();"><spring:message code="label.Cancel" /></a>
	</div>
	</div>
	</div>		
</div>

<div id="publishConfirmationDialog4" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="windowTitleLabel" aria-hidden="true">
	<div class="modal-dialog">
    <div class="modal-content">
    <div class="modal-header">
		<a  class="close" data-dismiss="modal" onclick="publishConfirmationClose();">&times;</a>
		<h3><spring:message code="label.Confirmation" /></h3>
	</div>
	<div class="modal-body">
		<div class="divDialogElements"><spring:message code="message.EndDatePast" /></div>
	</div>
	<div class="modal-footer">
		<a  class="btn btn-primary" onclick="publishConfirmationOkClicked();"><spring:message code="label.Proceed" /></a>
		<a  class="btn btn-default" onclick="publishConfirmationClose();"><spring:message code="label.Cancel" /></a>
	</div>
	</div>
	</div>	
</div>

<div id="confirmregformdialog" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="windowTitleLabel" aria-hidden="true">
	<div class="modal-dialog">
    <div class="modal-content">
    <div class="modal-body">
		<div class="divDialogElements"><spring:message code="question.AutoRegFormElements" /></div>
	</div>
	<div class="modal-footer">
		<a  class="btn btn-primary" onclick="checkPropertiesAndSubmit(true, false);"><spring:message code="label.OK" /></a>
		<a  class="btn btn-default" onclick="$('#confirmregformdialog').modal('hide');$('#edit-properties-dialog').modal('show');"><spring:message code="label.Cancel" /></a>
	</div>
	</div>
	</div>	
</div>

<div id="confirmpublicationdialog" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="windowTitleLabel" aria-hidden="true">
	<div class="modal-dialog">
    <div class="modal-content">
    <div class="modal-body">
		<div class="divDialogElements"><spring:message code="question.PublishResults" /></div>
	</div>
	<div class="modal-footer">
		<a  class="btn btn-primary" onclick="checkPropertiesAndSubmit(false, true);"><spring:message code="label.OK" /></a>
		<a  class="btn btn-default" onclick="$('#confirmpublicationdialog').modal('hide');$('#edit-properties-dialog').modal('show');"><spring:message code="label.Cancel" /></a>
	</div>
	</div>
	</div>	
</div>

<div id="sendReminderDialog" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="windowTitleLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-body">
				<div class="divDialogElements"><spring:message code="question.SendReminder" /></div>
			</div>
			<div class="modal-footer">
				<a class="btn btn-primary" onclick="sendReminder('${form.survey.id}');"><spring:message code="label.OK" /></a>
				<a class="btn btn-default" onclick="$('#sendReminderDialog').modal('hide');"><spring:message code="label.Cancel" /></a>
			</div>
		</div>
	</div>
</div>

<div class="modal" id="change-owner-dialog" data-backdrop="static">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<spring:message code="label.changeOwnership" />
				<a onclick="$(this).closest('.modal-header').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign" style="color: #337ab7"></span></a>

				<div style="clear: both"></div>
				<div class="help" style="display: none; margin-top: 10px;">
					<span><spring:message code="info.changeOwnership" /></span>
				</div>
			</div>
			<div class="modal-body">
				<c:choose>
					<c:when test="${USER.isExternal()}">
						<div style="float: left; width: 250px;  margin-right: 10px;">
							<label for="change-owner-email"><spring:message code="label.EmailAddress" /></label><br />
							<input type="text" maxlength="500" id="change-owner-email" style="min-width: 400px;"/>
						</div>
						<div style="clear: both"></div>
						<div style="margin-top: 20px">
							<a id="btnCheck"  style="float: left;" onclick="searchEmailUser('mail');" class="btn btn-default" style="margin: 10px"><spring:message code="label.Check" /></a>
						</div>
						<div style="clear: both"></div>
						<div style="margin-top: 20px">
							<div id="foundEmailUsers" style="color: green; margin-bottom: 0.8rem;"></div>
							<div id="invalidEmails" style="margin-bottom: 0.8rem; display: inline-flex">
								<img id="invalidEmailsIcon" src="${contextpath}/resources/images/exclamation-triangle.svg" style="display: none; height: 2rem; margin-right: 8px;"></img>
								<div id="invalidEmailsText" style="color: red"></div>
							</div>
							<div style="clear: both"></div>
							<div id="notFoundEmails" style="color: red; display: inline-flex">
								<img id="notFoundEmailsIcon" src="${contextpath}/resources/images/exclamation-triangle.svg" style="display: none; height: 2rem; margin-right: 8px;"></img>
								<div id="notFoundEmailsText" style="color: red"></div>
							</div>
						</div>
					</c:when>
					<c:when test="${USER.getGlobalPrivilegeValue('ECAccess') > 0}">
						<div style="float: left; width: 250px;  margin-right: 10px;">
							<label for="change-owner-email"><spring:message code="label.EmailAddress" /></label><br />
							<input type="text" maxlength="255" id="change-owner-email" />
						</div>

						<div style="clear: both"></div>

						<div style="float: left; width: 250px;  margin-right: 10px;" id="change-owner-firstname-div">
							<label for="change-department-name"><spring:message code="label.FirstName" /></label><br />
							<input type="text" maxlength="255" id="change-first-name" />
						</div>

						<div style="float: left; width: 250px;  margin-right: 10px;" id="change-owner-lastname-div">
							<label for="change-last-name"><spring:message code="label.LastName" /></label><br />
							<input type="text" maxlength="255" id="change-last-name" />
						</div>

						<div style="float: left; width: 250px;  margin-right: 10px;" id="change-owner-name-div">
							<label for="change-owner-name"><spring:message code="label.UserName" /><span id="eulogin-span"> (EU Login)</span></label><br />
							<input type="text" maxlength="255" id="change-owner-name" />
						</div>

						<div style="clear: both"></div>

						<div style="float: left; width: 250px; margin-right: 10px;" id="change-owner-department-div">
							<label for="change-department-name"><spring:message code="label.Department" /></label><br />
							<input type="text" maxlength="255" id="change-department-name" />
						</div>

						<div style="float: left; width: 510px" id="change-owner-domain-div">
							<label for="change-owner-type-ecas"><spring:message code="label.Domain" /></label><br />
							<select id="change-owner-type-ecas" onchange="checkUserType()" style="width: 510px" >
								<c:forEach items="${domains}" var="domain" varStatus="rowCounter">
									<option value="${domain.key}">${domain.value} </option>
								</c:forEach>
							</select>
						</div>
						<div style="clear: both"></div>
						<div style="margin-top: 10px">
							<div id="noEmptySearch" style="margin-bottom: 0.8rem; display: inline-flex">
								<img id="noEmptySearchIcon" src="${contextpath}/resources/images/exclamation-triangle.svg" style="display: none; height: 2rem; margin-right: 8px;"></img>
								<div id="noEmptySearchText" style="color: red"></div>
							</div>
						</div>
					</c:when>
					<c:when test="${USER.type == 'SYSTEM'}">
						<div style="float: left; width: 250px;  margin-right: 10px;">
							<label for="change-owner-email"><spring:message code="label.EmailAddress" /></label><br />
							<input type="text" maxlength="255" id="change-owner-email" />
						</div>

						<div style="float: left; width: 250px;  margin-right: 10px;" id="change-owner-name-div">
							<label for="change-owner-name"><spring:message code="label.UserName" /></label><br />
							<input type="text" maxlength="255" id="change-owner-name" />
						</div>

						<div style="clear: both"></div>

						<div style="width: 250px" id="change-owner-domain-div">
							<label for="change-owner-type-ecas"><spring:message code="label.Domain" /></label><br />
							<select id="change-owner-type-ecas" onchange="checkUserType()">
								<option value="system" selected="selected"><spring:message code="label.System" /></option>
							</select>
						</div>
					</c:when>
					<c:otherwise>
						<div style="float: left; width: 250px;  margin-right: 10px;">
							<label for="change-owner-email"><spring:message code="label.EmailAddress" /></label><br />
							<input type="text" maxlength="255" id="change-owner-email" />
						</div>

						<div style="clear: both"></div>

						<div style="float: left; width: 250px;  margin-right: 10px;" id="change-owner-firstname-div">
							<label for="change-first-name"><spring:message code="label.FirstName" /></label><br />
							<input type="text" maxlength="255" id="change-first-name" />
						</div>

						<div style="float: left; width: 250px;  margin-right: 10px;" id="change-owner-lastname-div">
							<label for="change-last-name"><spring:message code="label.LastName" /></label><br />
							<input type="text" maxlength="255" id="change-last-name" />
						</div>

						<div style="float: left; width: 250px;  margin-right: 10px;" id="change-owner-name-div">
							<label for="change-owner-name"><spring:message code="label.UserName" /></label><br />
							<input type="text" maxlength="255" id="change-owner-name" />
						</div>

						<div style="clear: both"></div>

						<div style="width: 250px" id="change-owner-domain-div">
							<label for="change-owner-type-ecas"><spring:message code="label.Domain" /></label><br />
							<select id="change-owner-type-ecas" onchange="checkUserType()">
								<option value="external" selected="selected"><spring:message code="label.EXT" /></option>
							</select>
						</div>
					</c:otherwise>
				</c:choose>

				<div style="clear: both"></div>

				<c:if test="${!USER.isExternal()}">
					<div style="margin-bottom: 20px">
						<a id="btnSearchFromAccess"  onclick="searchUser('login');" class="btn btn-primary"><spring:message code="label.Search" /></a>
						<span style="color: #f00; padding: 15px;" id="search-results-more" class="hideme"><spring:message code="message.SearchLimit100" /></span>
					</div>

					<div class="round" style="min-height: 323px; max-height: 323px; overflow: auto; width: 100%; overflow: auto; border: 1px solid #ddd" id="search-results-div">
						<table id="search-results" class="table table-bordered table-hover table-styled" style="max-width: none; margin-bottom: 0px">
							<thead>
							<tr>
								<th onclick="searchUser('mail');"><spring:message code="label.Email" /></th>
								<th onclick="searchUser('login');"><spring:message code="label.UserName" /></th>
								<th onclick="searchUser('first');"><spring:message code="label.FirstName" /></th>
								<th onclick="searchUser('last',);"><spring:message code="label.LastName" /></th>
								<th onclick="searchUser('department');" <c:if test="${oss}">class="hideme"</c:if> ><spring:message code="label.Department" /></th>
							</tr>
							</thead>
							<tbody></tbody>
						</table>
						<div id="search-results-none" class="hideme"><spring:message code="message.NoResultSelected" /></div>
					</div>
				</c:if>
				<div style="margin-top: 10px">
				    <input type="checkbox" class="check" id="add-as-form-manager" /> <spring:message code="label.addAsFormManager" />
				</div>
			</div>
			<div class="modal-footer">
				<c:choose>
					<c:when test="${USER.isExternal()}">
						<img id="add-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
						<a id="btnOkChangeOwnerFromAccess"  onclick="changeOwnerByEmail()" class="btn btn-primary" disabled><spring:message code="label.OK" /></a>
						<a id="btnCancelChangeOwnerFromAccess"  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>
					</c:when>
					<c:otherwise>
						<img id="add-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
						<a id="btnOkChangeOwnerFromAccess"  onclick="changeOwner()" class="btn btn-primary" disabled><spring:message code="label.OK" /></a>
						<a id="btnCancelChangeOwnerFromAccess"  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</div>
