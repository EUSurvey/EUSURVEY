<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>

<div id="messages-box-div" style="display: none">
	<div id="messages-box">
		<!-- ko foreach: systemMessages -->
			<!-- ko if: Hidden() == false -->
			<div class="message" style="display: none">
				<!-- ko if: Criticality() == '1' -->
				<div class="message-success-header">
					<div style="float: left; margin-right: 20px;"><span class="glyphicon glyphicon-ok"></span></div>
					<div style="float: right"><span data-bind="click: removeMessage" class="glyphicon glyphicon-remove"></span></div>
					<div style="clear: both"></div>
				</div>
				<!-- /ko -->
				<!-- ko if: Criticality() == '2' -->
				<div class="message-info-header">
					<div style="float: left; margin-right: 20px;"><span class="glyphicon glyphicon-info-sign"></span></div>
					<div style="float: right"><span data-bind="click: removeMessage" class="glyphicon glyphicon-remove"></span></div>
					<div style="clear: both"></div>
				</div>
				<!-- /ko -->
				<!-- ko if: Criticality() == '3' -->
				<div class="message-error-header">
					<div style="float: left; margin-right: 20px;"><span class="glyphicon glyphicon-exclamation-sign"></span></div>
					<div style="float: right"><span data-bind="click: removeMessage" class="glyphicon glyphicon-remove"></span></div>
					<div style="clear: both"></div>
				</div>
				<!-- /ko -->

				<div data-bind="html: Content"></div>
			</div>
			<!-- /ko -->
		<!-- /ko -->

		<!-- ko foreach: messages -->
			<!-- ko if: Hidden() == false -->
			<div class="message" style="display: none">
				<!-- ko if: Type() == 'success' -->
				<div class="message-success-header">
					<div style="float: left"><span class="glyphicon glyphicon-ok"></span></div>
					<div style="float: right"><span data-bind="click: removeMessage" class="glyphicon glyphicon-remove"></span></div>
					<div style="clear: both"></div>
				</div>
				<!-- /ko -->
				<!-- ko if: Type() == 'info' -->
				<div class="message-info-header">
					<div style="float: left"><span class="glyphicon glyphicon-info-sign"></span></div>
					<div style="float: right"><span data-bind="click: removeMessage" class="glyphicon glyphicon-remove"></span></div>
					<div style="clear: both"></div>
				</div>
				<!-- /ko -->
				<!-- ko if: Type() == 'error' -->
				<div class="message-error-header">
					<div style="float: left"><span class="glyphicon glyphicon-exclamation-sign"></span></div>
					<div style="float: right"><span data-bind="click: removeMessage" class="glyphicon glyphicon-remove"></span></div>
					<div style="clear: both"></div>
				</div>
				<!-- /ko -->
				<div data-bind="html: Content"></div>
			</div>
			<!-- /ko -->
		<!-- /ko -->
	</div>
</div>

<div id="messages-log-div" style="display: none" >
	<div id="messages-log">
		<div id="messages-log-header">
			<div style="float: left"><spring:message code="label.Notifications" /></div>
			<div style="float: right"><span data-bind="click: allRead" class="glyphicon glyphicon-remove"></span></div>
			<div style="clear: both"></div>
		</div>

		<div style="max-height: 500px; overflow-y: auto">

			<div data-bind="foreach: systemMessages">
				<div class="log-message">
					<div style="float: left; margin-right: 20px;">
						<!-- ko if: Criticality() == '1' -->
						<span style="color: #4caf50" class="glyphicon glyphicon-ok"></span>
						<!-- /ko -->
						<!-- ko if: Criticality() == '2' -->
						<span style="color: #337ab7" class="glyphicon glyphicon-info-sign"></span>
						<!-- /ko -->
						<!-- ko if: Criticality() == '3' -->
						<span style="color: #c11c1c" class="glyphicon glyphicon-exclamation-sign"></span>
						<!-- /ko -->
					</div>
					<div data-bind="html: Content"></div>
				</div>
			</div>

			<table class="table" style="margin-bottom: 0px">
				<thead>
					<tr>
						<th class="sr-only">${form.getMessage("label.MessageType")}</th>
						<th class="sr-only">${form.getMessage("label.MessageText")}</th>
						<th class="sr-only">${form.getMessage("label.Close")}</th>
					</tr>
				</thead>
				<tbody>
				<!-- ko foreach: messages -->

				<!-- ko if: Deleted() == false -->

				<tr class="log-message">
					<td style="width: 30px; padding-right: 0px;">
						<!-- ko if: Type() == 'success' -->
						<span style="color: #4caf50" class="glyphicon glyphicon-ok"></span>
						<!-- /ko -->
						<!-- ko if: Type() == 'info' -->
						<span style="color: #337ab7" class="glyphicon glyphicon-info-sign"></span>
						<!-- /ko -->
						<!-- ko if: Type() == 'error' -->
						<span style="color: #c11c1c" class="glyphicon glyphicon-exclamation-sign"></span>
						<!-- /ko -->
					</td>
					<td data-bind="html: Content"></td>
					<td style="width: 40px;">
						<span data-bind="click: deleteMessage" class="glyphicon glyphicon-remove"></span>
					</td>
				</tr>

				<!-- /ko -->

				<!-- /ko -->
				</tbody>
			</table>

		</div>
	</div>
</div>

<div class="modal" id="generic-show-messages-dialog" data-backdrop="static">
	<div class="modal-dialog">
 	<div class="modal-content">
	<div class="modal-body">
		<span id="generic-show-messages-dialog-text"></span>
	</div>
	<div class="modal-footer">
		<a  class="btn btn-primary" data-dismiss="modal">OK</a>
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
		<a  class="btn btn-primary" data-dismiss="modal"><spring:message code="label.OK" /></a>
	</div>
	</div>
	</div>
</div>

<div class="modal" id="timeout-dialog" data-backdrop="static">
	<div class="modal-dialog">
  		<div class="modal-content">
			<div class="modal-header">
				<b><spring:message code="label.SessionTimeout" /></b>
			</div>
			<div class="modal-body">
				<div id="timeout-dialog-info">
					<spring:message code="message.SessionTimeout" />&nbsp;<span style="color:#f00" id="timeoutleft"></span>.<br />
					<spring:message code="message.SessionTimeout2" />
				</div>
				<div id="timeout-dialog-error" style="display: none">
					<spring:message code="message.SessionTimeout3" />
				</div>
			</div>
			<div class="modal-footer">
				<a class="btn btn-primary" id="timeout-dialog-extend" onclick="extend()"><spring:message code="label.Extend" /></a>
			</div>
		</div>
	</div>
</div>

<c:if test="${responsive != null}">
	<div class="modal" id="responsiveinfo-dialog" data-backdrop="static">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-body">
					<spring:message code="info.useDesktopPC" />
				</div>
				<div class="modal-footer">
					<a class="btn btn-primary" data-dismiss="modal"><spring:message code="label.Close" /></a>
				</div>
			</div>
		</div>
	</div>
</c:if>

<script type="text/javascript">

	var Message = function() {
		this.Type = ko.observable("");
		this.Content = ko.observable("");
		this.Hidden = ko.observable(false);
		this.Deleted = ko.observable(false);
		this.Read = ko.observable(false);
		this.Icon = ko.observable("");
		this.Criticality = ko.observable(1);

		this.removeMessage = function () {
			this.Hidden(true);
			if (this.Type() == "system")
			{
				deleteUserMessage();
			}
			_messages.saveToLocalStorage();
		};

		this.deleteMessage = function() {
			this.Deleted(true);
			_messages.saveToLocalStorage();
		};
	}

	var Messages = function() {
		var self = this;
		this.messages = ko.observableArray();
		this.systemMessages = ko.observableArray();

		this.totalMessages = ko.computed(function(){
			var counter = 0;
			for (var i = 0; i < self.messages().length; i++)
			{
				if (!self.messages()[i].Read()) counter++;
			}
			for (var i = 0; i < self.systemMessages().length; i++)
			{
				if (!self.systemMessages()[i].Read()) counter++;
			}
		   return counter;
		});

		this.allRead = function() {
			for (var i = 0; i < self.messages().length; i++)
			{
				self.messages()[i].Read(true);
			}
			for (var i = 0; i < self.systemMessages().length; i++)
			{
				self.systemMessages()[i].Read(true);
			}
			$('#messages-log-div').hide();
			self.saveToLocalStorage();
		}

		this.showAll = function() {
			for (var i = 0; i < this.messages().length; i++)
			{
				this.messages()[i].Hidden(false);
			}
			window.setTimeout("showMessages()", 100);
		}

		this.hideSystemMessages = function() {
			for (var i = 0; i < this.systemMessages().length; i++)
			{
				this.systemMessages()[i].Hidden(true);
			}
		}

		this.addMessage = function(message, fromLoad)
		{
			this.messages.splice(0, 0, message);

			window.setTimeout("showMessages()", 100);
			window.setTimeout(function() {
			    message.removeMessage();
			}, 5000)

			if (!fromLoad)
			{
				self.saveToLocalStorage();
			}
		}

		this.saveToLocalStorage = function() {
			try
			{
				localStorage.setItem("messagelog", ko.toJSON(this.messages()));
			} catch (e) {};
		}

		this.addSuccessMessage = function (text) {
			text = self.sanitizeErrorPageText(text);
			var message = new Message();
			message.Type("success");
			message.Content(text);
			self.addMessage(message, false);
		}

		this.addInfoMessage = function (text) {
			text = self.sanitizeErrorPageText(text);
			var message = new Message();
			message.Type("info");
			message.Content(text);
			self.addMessage(message, false);
		}

		this.addErrorMessage = function (text) {
			text = self.sanitizeErrorPageText(text);
			var message = new Message();
			message.Type("error");
			message.Content(text);
			self.addMessage(message, false);
		}

		this.addSystemMessage = function (m)
		{
			var message = new Message();
			message.Type("system");
			message.Content(self.sanitizeErrorPageText(m.text));
			message.Icon(m.icon)
			message.Criticality(m.criticality);
			this.systemMessages.splice(0, 0, message);

			window.setTimeout("showMessages()", 100);

			return message
		}

		this.removeSystemMessage = function (m) {
			const mIndex = this.systemMessages.indexOf(m)
			if (mIndex >= 0) {
				this.systemMessages.splice(mIndex, 1)
			}

		}

		this.sanitizeErrorPageText = function (text){
			//Tomcat error responses always start with <!doctype html>
			//Like this they can't be added to the notifications
			if (text.toLowerCase().startsWith("<!doctype html>")){
				let sanitizerNode = document.createElement("div")
				sanitizerNode.innerHTML = text
				return sanitizerNode.innerText.substr(0, 60) + "..."
			}
			return text
		}

		try
		{
			var messagelog = localStorage.getItem("messagelog");
			var parsed = JSON.parse(messagelog);
			for (var i = 0; i < parsed.length; i++)
			{
				var message = new Message();
				message.Type(parsed[i].Type);
				message.Content(parsed[i].Content);
				message.Deleted(parsed[i].Deleted);
				message.Read(parsed[i].Read);
				message.Hidden(parsed[i].Hidden);
				self.addMessage(message, true);
			}
		} catch (e) {};
	}

	var _messages = new Messages();

	$(function() {
		ko.applyBindings(_messages, $("#messages-box")[0]);
		ko.applyBindings(_messages, $("#messages-log")[0]);

		if ($("#messages-button").length > 0) {
			ko.applyBindings(_messages, $("#messages-button")[0]);
		}

		checkTimeout();

		$('#messages-box-div').show();
	});

	function showInfo(text)
	{
		_messages.addInfoMessage(text);
	}

	function showSuccess(text)
	{
		_messages.addSuccessMessage(text);
	}

	function showError(text)
	{
		_messages.addErrorMessage(text);
	}

	let lastTimeAjaxError = new Date(0)
	let stopAjaxErrors = false
	function showAjaxError(statuscode){
		if (!stopAjaxErrors && new Date().getTime() - lastTimeAjaxError.getTime() > 2500) { //Prevent Spam
			showError("Connection Error " + statuscode);
			lastTimeAjaxError = new Date()
		}
	}

	//When unloading the page, prevent ajaxErrors, as they might be thrown when the request is interrupted by the browser
	window.addEventListener("beforeunload", () => {stopAjaxErrors = true})

	function showSystemMessage(message)
	{
		return _messages.addSystemMessage(message);
	}

	function removeSystemMessage(message)
	{
		return _messages.removeSystemMessage(message);
	}

	function showMessages() {
		$('.message:hidden').show();
	}

	function hideMessages() {
		for (var i = 0; i < _messages.messages().length; i++)
		{
			_messages.messages()[i].Hidden(true);
		}
	}

	function showExportDialogAndFocusEmail(caller)
	{
		$('#ask-export-dialog').find(".foremail").hide();
		$('#ask-export-dialog').find(".forexport").show();
		showModalDialog($('#ask-export-dialog'), caller);
		setTimeout(function() { $('#email').focus(); }, 1000);
	}

	function showAskEmailDialog(caller)
	{
		$('#ask-export-dialog').find(".foremail").show();
		$('#ask-export-dialog').find(".forexport").hide();
		showModalDialog($('#ask-export-dialog'), caller);
	}

	var sessiontimeout = ${uisessiontimeout * 60};
	var timeoutTime = new Date();
	refreshTimeout();

	function refreshTimeout()
	{
	  timeoutTime = new Date();
	  timeoutTime.setSeconds(timeoutTime.getSeconds() + sessiontimeout);
	}

	function checkTimeout()
	{
		if (sessiontimeout == 0) return;

		var diffTimeMilliseconds = getTimeoutMilliseconds();
		if (diffTimeMilliseconds < 5 * 60 * 1000) {
			$('#timeout-dialog').modal('show');
			updateTimeout();
		} else {
			window.setTimeout(function() {
			    checkTimeout();
			}, 60000);
		}
	}

	function getTimeoutMilliseconds()
	{
		var currentTime = new Date();
		return timeoutTime - currentTime;
	}

	function showSessionError()
	{
		//forcing timeout by setting the timeoutTime to yesterday
		timeoutTime.setDate(timeoutTime.getDate() - 1);
		$('#timeout-dialog').modal('show');
		updateTimeout();
	}

	function showLocalBackupFilesInfo(){
		showInfo("<spring:message code="info.LocalBackupFiles"/>")
	}

	function updateTimeout()
	{
		var diffTimeMilliseconds = getTimeoutMilliseconds();

		if (diffTimeMilliseconds <= 0) {
			//this means the session has timed out
			$('#timeout-dialog-info').hide();
			$('#timeout-dialog-error').show();
			$('#timeout-dialog-extend').hide();
			return;
		}

		if (diffTimeMilliseconds > 5 * 60 * 1000) {
			//this means there was a request in the meantime that extended the session
			$('#timeout-dialog').modal('hide');
			checkTimeout();
			return;
		}

		var diffTimeSeconds = diffTimeMilliseconds / 1000;
		var minutes = Math.floor(diffTimeSeconds / 60);
		var seconds = Math.floor(diffTimeSeconds - (minutes * 60));
		$('#timeoutleft').html(minutes + ":" + (seconds < 10 ? "0" + seconds : seconds));

		window.setTimeout(function() {
		    updateTimeout();
		}, 1000);
	}

	function extend()
	{
		$.ajax({
			  url: contextpath + "/info/renewsession",
			  cache: false,
			  error: function(e)
			  {
				  //this happens when there is no message
			  },
			  success: function(message)
			  {
				  //session timeout reset
				  $('#timeout-dialog').modal('hide');
				  refreshTimeout();
				  checkTimeout();
			  }
			});
	}

	function testTimeoutExpiration(restTime) {
		lastEditDate.setDate(lastEditDate.getDate()-1);
		let originalTimeout = sessiontimeout
		sessiontimeout = 300;
		if (Number.isInteger(restTime)) {
			sessiontimeout = restTime;
		}
		refreshTimeout();
		checkTimeout();
		console.log("Function for testing purposes. Set Timeout to", timeoutTime);
		sessiontimeout = originalTimeout
	}

</script>