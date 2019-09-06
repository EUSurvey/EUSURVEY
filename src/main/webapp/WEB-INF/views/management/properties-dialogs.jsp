<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page trimDirectiveWhitespaces="true" %>

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
		<a  class="btn btn-info" onclick="publishConfirmationOkClicked();"><spring:message code="label.OK" /></a>
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
		<a  class="btn btn-info" onclick="publishConfirmationOkClicked();"><spring:message code="label.Proceed" /></a>
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
		<a  class="btn btn-info" onclick="publishConfirmationOkClicked();"><spring:message code="label.Proceed" /></a>
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
		<a  class="btn btn-info" onclick="publishConfirmationOkClicked();"><spring:message code="label.Proceed" /></a>
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
		<a  class="btn btn-info" onclick="checkPropertiesAndSubmit(true, false);"><spring:message code="label.OK" /></a>
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
		<a  class="btn btn-info" onclick="checkPropertiesAndSubmit(false, true);"><spring:message code="label.OK" /></a>
		<a  class="btn btn-default" onclick="$('#confirmpublicationdialog').modal('hide');$('#edit-properties-dialog').modal('show');"><spring:message code="label.Cancel" /></a>
	</div>
	</div>
	</div>	
</div>
