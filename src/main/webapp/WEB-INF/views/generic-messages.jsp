<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>

<div id="export-available-box" class="alert message-success-right hideme">
	<div style="float: right; margin-left: 10px;"><a onclick="$(this).parent().parent().hide();"><span class="glyphicon glyphicon-remove"></span></a></div>
	<div style="float: left; margin: 5px; margin-top: 5px; margin-right: 10px""><img src="${contextpath}/resources/images/check.png" id="system-message-box-icon" alt="system message icon"></div>
	<div class="generic-box-text">
		<spring:message code="label.Export" />&nbsp;<span id="export-available-box-name" style="font-weight: bold;"></span>&nbsp;<spring:message code="label.availableForDownload" /><br />
		<spring:message code="label.GoTo" />&nbsp;<a class="visiblelink" href="<c:url value="/exports/list"/>"><spring:message code="label.ExportPage" /></a>
	</div>
</div>

<div id="generic-info-box" class="alert message-success-right hideme">
	<div style="float: right; margin-left: 10px;"><a onclick="$(this).parent().parent().hide();"><span class="glyphicon glyphicon-remove"></span></a></div>
	<div style="float: left; margin: 5px; margin-top: 5px; margin-right: 10px"><img src="${contextpath}/resources/images/check.png" id="system-message-box-icon" alt="system message icon"></div>
	<div style="margin-left: 10px; padding-top: 3px; padding-bottom: 5px;"class="generic-box-text" id="generic-info-box-text"></div>
</div>

<div id="generic-error-box" class="alert alert-danger hideme" style="display: none; position: fixed; top: 5px; right: 5px; padding: 5px; z-index: 10001;">
	<div style="float: left;"><img src="${contextpath}/resources/images/warning.png" id="system-message-box-icon" alt="system message icon"></div>
	<div style="float: right; margin-left: 5px;"><a onclick="$(this).parent().parent().hide();"><span class="glyphicon glyphicon-remove"></span></a></div>
	<div class="generic-box-text" id="generic-error-box-text"></div>
</div>
	
<div class="modal" id="generic-show-messages-dialog" data-backdrop="static">
	<div class="modal-dialog">
 	<div class="modal-content">
	<div class="modal-body">
		<span id="generic-show-messages-dialog-text"></span>
	</div>
	<div class="modal-footer">
		<a  class="btn btn-info" data-dismiss="modal">OK</a>				
	</div>
	</div>
	</div>
</div>

<div class="modal" id="generic-show-multiple-messages-dialog" data-backdrop="static">
	<div class="modal-dialog modal-sm">
  		<div class="modal-content">
	<div class="modal-header">
		<b><spring:message code="label.Result" /></b>
	</div>
	<div class="modal-body">
		
	</div>
	<div class="modal-footer">
		<a  class="btn btn-info" data-dismiss="modal"><spring:message code="label.OK" /></a>				
	</div>
	</div>
	</div>	
</div>


<script type="text/javascript">
	
	function showInfo(text)
	{
		$("#generic-info-box-text").html(text);
		$("#generic-info-box").show();
		window.setTimeout("hideGenericInfos()", 10000);
	}
	
	function showSuccess(text)
	{
		showInfo(text);
	}
	
	function showError(text)
	{
		$("#generic-error-box-text").html(text);
		$("#generic-error-box").show();
		window.setTimeout("hideGenericInfos()", 10000);
	}
	
	function showGenericError()
	{
		showError('<spring:message code="error.OperationFailed" />');
	}
	
	function hideGenericInfos()
	{
		$("#generic-info-box").hide(400);
		$("#generic-error-box").hide(400);
	}	
	
	function showExportDialogAndFocusEmail()
	{
		$('#ask-export-dialog').modal('show');		
		setTimeout(function() { $('#email').focus(); }, 1000);
	}
	
</script>