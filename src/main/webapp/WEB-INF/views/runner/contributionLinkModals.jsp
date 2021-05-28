<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>


<div class="modal" id="contribution-link-dialog" data-backdrop="static" role="dialog" tabindex="-1">
	<div class="modal-dialog non-resizable">
		<div class="modal-content">
			<div class="modal-body">
				<p>${form.getMessage("info.delphilink")}</p>
				<p id="contribution-link-dialog__link"></p>
				<p>${form.getMessage("info.delphiLinkInSidebar")}</p>
			</div>
			<div class="modal-footer">
				<a href="javascript:;" class="btn btn-default" onclick="openAskEmailToSendLinkDialog(this)">
					${form.getMessage("label.SendByEmail")}
				</a>
				<a href="javascript:;" class="btn btn-primary" onclick="hideModalDialog($('#contribution-link-dialog'))">${form.getMessage("label.Continue")}</a>
			</div>
		</div>
	</div>
</div>

<div class="modal" id="ask-email-dialog" data-backdrop="static">
	<div class="modal-dialog non-resizable">
		<div class="modal-content">
			<div class="modal-body">
				<p>${form.getMessage("info.delphilinkemail")}</p>
				<input class="form-control" type="text" maxlength="255" name="delphiemail" id="delphiemail" />
				<p id="ask-delphi-email-dialog-error" class="validation-error-keep hideme">
					${form.getMessage("message.ProvideEmail")}
				</p>
			</div>
			<div class="modal-footer">
				<a href="javascript:;" class="btn btn-primary" onclick="sendDelphiMailLink()">${form.getMessage("label.Send")}</a>
				<a href="javascript:;" class="btn btn-default" data-dismiss="modal">${form.getMessage("label.Cancel")}</a>
			</div>
		</div>
	</div>
</div>
