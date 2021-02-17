<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>


<div class="modal save-link-dialog" data-backdrop="static" role="dialog" tabindex="-1">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-body">
				${form.getMessage("info.delphilink")}<br />
				<span class="delphilinkurl"></span><br />
				${form.getMessage("info.delphiLinkInSidebar")}
			</div>
			<div class="modal-footer">
				<a class="btn btn-primary" onclick="openAskEmailToSendLinkDialog()">${form.getMessage("label.SendLinkAsEmail")}</a>
				<a class="btn btn-default" data-dismiss="modal">${form.getMessage("label.Cancel")}</a>
			</div>
		</div>
	</div>
</div>

<div class="modal" id="ask-email-dialog" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-body">
				<p style="margin-bottom: 10px">
					${form.getMessage("info.delphilinkemail")}
				</p>
				<input class="form-control" type="text" maxlength="255" name="delphiemail" id="delphiemail" />
				<span id="ask-delphi-email-dialog-error" class="validation-error-keep hideme">
					${form.getMessage("message.ProvideEmail")}
				</span>
			</div>
			<div class="modal-footer">
				<a class="btn btn-primary" onclick="sendDelphiMailLink()">${form.getMessage("label.Send")}</a>
				<a class="btn btn-default" data-dismiss="modal">${form.getMessage("label.Cancel")}</a>
			</div>
		</div>
	</div>
</div>
