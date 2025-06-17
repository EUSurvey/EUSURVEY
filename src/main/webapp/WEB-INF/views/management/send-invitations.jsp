<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.SendInvitations" /></title>
	
	<%@ include file="../includes.jsp" %>
	
	<link href="${contextpath}/resources/css/management.css" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/progressbar.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	
	<style type="text/css">
		#dialog-step2 label {
			font-weight: bold !important;
			margin-top: 10px;
		}
		
		.glyphicon-info-sign {
			color: #aaa;
			margin-left: 5px;
		}
	</style>
	
	<script type="text/javascript" src="${contextpath}/resources/js/sendinvitations.js?version=<%@include file="../version.txt" %>"></script>
	
	<script type="text/javascript"> 
		$(function() {					
			<c:if test="${participationGroup.lastUsedTemplateID != null}">
				if ($("#mailtext").find("option[value='${participationGroup.lastUsedTemplateID}']").length > 0)
				{
					$("#mailtext").val("${participationGroup.lastUsedTemplateID}");
					setTimeout(function(){ loadTemplate(); }, 500);					
				}
			</c:if>
		});
		
		function saveTemplate()
		{	
			$("#savetextdialog").find(".validation-error").remove();
			
			var name = $('#savetextdialoginput').val();
			if (name.trim().length == 0)
			{
				$('#savetextdialoginput').after("<div class='validation-error'><spring:message code="validation.required" /></div>");
				return;
			}		
			
			var s = new PostTemplate('${participationGroup.id}', $("#text1").html(), $("#text2").html(), $("#txtSubjectFromInvitation").val(), $("#senderAddress").val(), name, $('#mailtemplate').val(), $('#mailtext').val());
			
			$.ajax({
				type:'POST',
				  url: "<c:url value="/${sessioninfo.shortname}/management/saveTemplateJSON" />",
				  data: s,
				  beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
				  cache: false,
				  success: function( result ) {
					  if (result == 'exists')
					  {
						 $('#savetextdialoginput').after("<div class='validation-error'><spring:message code="error.NameAlreadyUsed" /></div>");
					  } else if (result == 'error')
					  {
						  showError("<spring:message code="error.OperationFailed" />");
					  } else {
						  var id = parseInt(result);
						  var option = document.createElement("option");
						  $(option).val(id).html(name);
						  $("#mailtext").append(option);
						  $("#mailtext").val(id);
						  $("#add-wait-animation2").hide();
						  $("#savetextdialog").modal('hide');
						  $("#savetextbutton").attr("disabled","disabled");
						  showSuccess("<spring:message code="label.TextSaved" />");	
					  }
				  },
				  error: function(e)
				  {
					  alert(e);
				  }
			});
		}
		
		function loadTemplate()
		{
			var id = $("#mailtext").val();
			
			if (id == "0")
			{
				$("#mailtemplate").val("eusurvey");
				$("#senderAddress").val("${USER.email}");
				$("#txtSubjectFromInvitation").val("${senderSubject}");
				$("#text1").html($('#text1default').text());
				$("#text2").html($('#text2default').text());	
				$("#deletetextbutton").attr("disabled","disabled");
				return;
			}
			
			$.ajax({
				type:'GET',
				  url: "<c:url value="/${sessioninfo.shortname}/management/loadTemplateJSON" />",
				  data: "id=" + id,
				  dataType: 'json',
				  cache: false,
				  success: function( result ) {
					  $("#mailtemplate").val(result.templateMail);
					  $("#mailtext").val(id);
					  $("#senderAddress").val(result.replyto);
					  $("#txtSubjectFromInvitation").val(result.templateSubject);
					  $("#text1").html(result.template1);
					  $("#text2").html(result.template2);
					  $("#deletetextbutton").removeAttr("disabled");
					  $("#savetextbutton").attr("disabled","disabled");
				  },
				  error: function(e)
				  {
					  alert(e);
				  }
			});
		}
		
		function showDeleteDialog()
		{
			$('#deletetextdialogname').text($("#mailtext").find("option[value='" + $("#mailtext").val() + "']").text());
			$('#deletetextdialog').modal('show')
		}
		
		function deleteTemplate()
		{
			var id = $("#mailtext").val();
			$.ajax({
				type:'GET',
				  url: "<c:url value="/${sessioninfo.shortname}/management/deleteTemplateJSON" />",
				  data: "id=" + id,
				  cache: false,
				  success: function( result ) {					  
					  if (result == "ok")
					  {
					 	$("#mailtext").find("option[value='" + id + "']").remove();
					  	$("#mailtext").val(0);
					  } else {
						  showError("<spring:message code="error.OperationFailed" />");
					  }
					  $('#deletetextdialog').modal('hide')			
				  },
				  error: function (data) {
					  showAjaxError(data.status)
				  }
			});
		}
		
		function step1()
		{
			_sendInvitationsPage.Step(1);
		}
		
		function step2()
		{
			_sendInvitationsPage.Step(2);
		}

		function step3(validate)
		{
			if (validate)
			{
				if ($("#savetextbutton").attr('disabled') != 'disabled')
				{
					$("#ask-save-dialog").modal('show');
					return;
				}
				
				if (!validateInput($('#dialog-step2')))
				{
					return;
				}
			}
			$("#ask-save-dialog").modal('hide');
			
			var selected = $("#tblInvitedFromSendInvitation tbody").find("input[type='checkbox']:checked").length;
			$('#preview-selectedcontacts').text(selected)
			
			$('#preview-current').empty();
			for (var i = 1; i <= selected; i++)
			{
				var option = document.createElement("option");
				$(option).val(i).html(i);
				$('#preview-current').append(option);
			}
						
			if (selected > 0)
			{
				loadPreview(0);
				$('#btnSendFromSendInvitation').removeClass("disabled");
			} else {
				$('#preview-current').val("0");
				$("#preview").html("<spring:message code="info.NoContactsSelected" />");
				$('#btnSendFromSendInvitation').addClass("disabled");
			}
			
			_sendInvitationsPage.Step(3);

			$('[data-toggle="tooltip"]').tooltip();
		}
	</script>
	
	<c:if test="${message != null && message.length() > 0}">
		<script type="text/javascript"> 
		$(function() {
			$('#myModal').modal();
		});
		</script>
	</c:if>
	
	<c:if test="${errorMessage != null && errorMessage.length() > 0}">
		<script type="text/javascript"> 
		$(function() {
			$('#myModalError').modal();
		});
		</script>
	</c:if>
		
</head>
<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
		<%@ include file="../menu.jsp" %>
		<%@ include file="formmenu.jsp" %>			
				
		<div data-backdrop="static" class="modal" id="myModal" tabindex="-1" role="dialog">
		  <div class="modal-dialog">
	      <div class="modal-content">
		  <div class="modal-header">
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		    <h3><spring:message code="label.Info" /></h3>
		  </div>
		  <div class="modal-body">
		    <p><esapi:encodeForHTML>${message}</esapi:encodeForHTML></p>
		  </div>
		  <div class="modal-footer">
		    <a id="btnOkInvitationSendFromInvitation" href="<c:url value="/${sessioninfo.shortname}/management/participants" />" class="btn btn-primary"><spring:message code="label.OK" /></a>
		  </div>
		  </div>
		  </div>
		</div>	
		
		<div data-backdrop="static" class="modal" id="myModalError" tabindex="-1" role="dialog">
		  <div class="modal-dialog">
	      <div class="modal-content">
		  <div class="modal-header">
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		    <h3><spring:message code="label.Error" /></h3>
		  </div>
		  <div class="modal-body">
		    <p><esapi:encodeForHTML>${errorMessage}</esapi:encodeForHTML></p>
		  </div>
		  <div class="modal-footer">
		    <a href="<c:url value="/${sessioninfo.shortname}/management/participants" />" class="btn btn-primary"><spring:message code="label.OK" /></a>
		  </div>
		  </div>
		  </div>
		</div>
		
		<div data-backdrop="static" class="modal" id="myModalCheck" tabindex="-1" role="dialog">
		  <div class="modal-dialog modal-sm">
	      <div class="modal-content">
		  <div class="modal-body">
		    <p><spring:message code="question.SendEmailsNow" /></p>
		  </div>
		  <div class="modal-footer">
				    <a id="btnConfirmFromSendInvitation" onclick="$('#myModalCheck').modal('hide');$('#generic-wait-dialog').modal('show');$('#sendinvitations').submit();"  class="btn btn-primary"><spring:message code="label.OK" /></a>
			<a id="btnCancelConfirmFromSendInvitation" onclick="$('#myModalCheck').modal('hide');$('#dialog-step3').show();"  class="btn btn-default"><spring:message code="label.Cancel" /></a>
		  </div>
		  </div>
		  </div>
		</div>	
		
		<form:form id="sendinvitations" method="POST" action="${contextpath}/${sessioninfo.shortname}/management/sendInvitations">
		
			<input type="hidden" id="selectedAttendee" name="selectedAttendee" value="" />
			<input type="hidden" id="participationGroup" name="participationGroup" value="${participationGroup.id}" />	
			
			<div id="action-bar" class="container action-bar" style="width: 100%;">
				<div class="row">
					<div class="col-md-3" style="text-align: left">
						<a onclick="_participants.Page(1)"><spring:message code="label.Participants" /></a> <span class="glyphicon glyphicon-menu-right" style="font-size: 90%"></span> 
						<spring:message code="label.SendInvitations" />
					</div>
					<div class="col-md-5" style="text-align: center">
						<ul class="progressbar">
							<li class="pb2" data-bind="attr: {class: Step() == 1 ? 'active pb2' : 'pb2'}"><span data-bind="attr: {style: Step() > 1 ? 'color: #43ff43' : ''}" class="glyphicon glyphicon-ok"></span><spring:message code="label.SelectParticipants" /></li>
							<li class="pb2" data-bind="attr: {class: Step() == 2 ? 'active pb2' : 'pb2'}"><span data-bind="attr: {style: Step() > 2 ? 'color: #43ff43' : ''}" class="glyphicon glyphicon-ok"></span><spring:message code="label.EditMessage" /></li>
							<li class="pb2" data-bind="attr: {class: Step() == 3 ? 'active pb2' : 'pb2'}"><spring:message code="label.SendEmails" /></li>
						</ul>
					</div>
					<div class="col-md-3" style="text-align: left">
					
					</div>
				</div>
			</div>	
			
			<div id="dialog-step1" data-bind="visible: Step() == 1">
				<div class="fullpageform" style="padding-top: 40px; max-width: 800px; margin-left: auto; margin-right: auto;">
			  		
					<input id="checkAllUnInvited" type="checkbox" checked="checked" onchange="checkUninvited();" /> <spring:message code="label.SelectUninvitedContacts" /><br />
					<input id="checkAllUnAnswered" type="checkbox" onchange="checkUnanswered();" /> <spring:message code="label.SelectInvited" />
					
					<div class="tabletitle" style="margin-top: 20px;">
						${participationGroup.name}
						<div class="info">
							<spring:message code="info.SelectParticipants" />
						</div>
					</div>
					<table id="tblInvitedFromSendInvitation" class="table table-bordered table-styled">
						<thead>
							<tr>
								<th><input id="checkAll" type="checkbox" checked="checked" onchange="check();" /></th>
								<th><spring:message code="label.Name" /></th>
								<th><spring:message code="label.Email" /></th>
								<th><spring:message code="label.InvitationDate" /></th>
								<th><spring:message code="label.ReminderDate" /></th>
								<th><spring:message code="label.Answers" /></th>
							</tr>
						</thead>
						<tbody>					
							<c:choose>
								<c:when test="${participationGroup.type == 'ECMembers'}">
									<c:set var="attendees" value="${participationGroup.ecasUsers}" />
								</c:when>
								<c:otherwise>
									<c:set var="attendees" value="${participationGroup.attendees}" />
								</c:otherwise>
							</c:choose>						
							<c:forEach items="${attendees}" var="attendee">					
								<tr>		
									<td>
										<c:choose>
											<c:when test="${attendee.invited == null}">
												<c:choose>
													<c:when test="${attendee.answers > 0}">
														<input class="uninvited answered" name="attendee${attendee.id}" type="checkbox" checked="checked" />
													</c:when>
													<c:otherwise>
														<input class="uninvited" name="attendee${attendee.id}" type="checkbox" checked="checked" />
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${attendee.answers > 0}">
														<input class="invited answered" name="attendee${attendee.id}" type="checkbox" />
													</c:when>
													<c:otherwise>
														<input class="invited unanswered" name="attendee${attendee.id}" type="checkbox" />
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
										<c:if test="${participationGroup.type != 'ECMembers'}">	
											<c:forEach items="${attendee.attributes}" var="attribute">
												<span class="hideme" data-id="{${attribute.attributeName.name}}">${attribute.value}</span>
											</c:forEach>
										</c:if>
									</td>
									<td data-class="name"><esapi:encodeForHTML>${attendee.getDisplayName()}</esapi:encodeForHTML></td>
									<td data-class="email"><esapi:encodeForHTML>${attendee.email}</esapi:encodeForHTML></td>
									<td><spring:eval expression="attendee.invited" /></td>
									<td><spring:eval expression="attendee.reminded" /></td>
									<td>
										<c:choose>
											<c:when test="${attendee.answers > 0}">
												<esapi:encodeForHTML>${attendee.answers}</esapi:encodeForHTML>
											</c:when>
											<c:otherwise>&#160;</c:otherwise>			
										</c:choose>
									</td>
								</tr>						
							</c:forEach>					
						</tbody>
					</table>	
				
					<div style="float: right">
						<a id="btnCancelFromSendInvitationStep2" href="<c:url value="/${sessioninfo.shortname}/management/participants" />" class="btn btn-default"><spring:message code="label.Cancel" /></a>
					</div>
					<div style="text-align: center">
						<a id="btnNextFromSendInvitationStep2" onclick="step2()" class="btn btn-primary"><spring:message code="label.Next" /></a>
					</div>
				</div>
			</div>	
		
			<div id="dialog-step2" data-bind="visible: Step() == 2">		
				<div class="fullpageform" style="padding-top: 40px; max-width: 800px; margin-left: auto; margin-right: auto;">
			
			  		<div style="float: right; padding: 8px; border: 2px solid #ddd; background-color: #efefef;">
		  				<spring:message code="label.SelectMailDesign" /><br />
		  				<select onchange="$('#savetextbutton').removeAttr('disabled')" id="mailtemplate" name="mailtemplate" class="small-form-control" style="width: 150px">
							<option value="eusurvey" selected="selected">EUSurvey</option>
							<c:if test="${USER.getGlobalPrivilegeValue('ECAccess') > 0}">
								<option value="ecofficial"><spring:message code="label.ECofficial" /></option>
							</c:if>
							<option value="plaintext"><spring:message code="label.Notemplate" /></option>
						</select><br /><br />
			  				
			  			<spring:message code="label.UseMailTemplate" /><br />
						<select onchange="loadTemplate()" id="mailtext" name="mailtext" class="small-form-control" style="width: 150px">
							<option value="0" selected="selected">Default</option>
							<c:if test="${usertexts != null}">
								<c:forEach items="${usertexts}" var="text">
									<option value="${text.id}">${text.name}</option>
								</c:forEach>
							</c:if>
						</select>
						<div style="margin-top: 5px">
							<button id="savetextbutton" disabled="disabled" onclick="$('#savetextdialog').modal('show'); return false;" class="btn btn-default"  style="margin-top: 10px"><spring:message code="label.SaveAsTemplate" /></button><br />
							<button id="deletetextbutton" disabled="disabled" onclick="showDeleteDialog(); return false;" class="btn btn-default" style="margin-top: 5px"><spring:message code="label.DeleteTemplate" /></button>
						</div>
					</div>
					
					<div style="float: left; margin-bottom: 20px;">					
						<label><spring:message code="label.ReplyTo" /></label><br />
						<c:choose>
							<c:when test='${USER.ECPrivilege == 0 && USER.type == "ECAS"}'>
								<input type="text" class="email form-control disabled" style="background-color: rgb(235, 235, 228);" maxlength="255"  value="<esapi:encodeForHTMLAttribute>${USER.email}</esapi:encodeForHTMLAttribute>" disabled="disabled" />
							</c:when>
							<c:otherwise>
								<input type="text" onchange="$('#savetextbutton').removeAttr('disabled');validateInput($(this).parent());" class="email form-control" maxlength="255"  value="<esapi:encodeForHTMLAttribute>${USER.email}</esapi:encodeForHTMLAttribute>" id="senderAddress" name="senderAddress" />
							</c:otherwise>
						</c:choose>
						<br />
						<label><spring:message code="label.Subject" /> <a onclick="$('#subjecthelp').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a></label>
						<div id="subjecthelp" class="help hideme">
							<spring:message code="help.Subject" />
						</div><br />
						<input onchange="$('#savetextbutton').removeAttr('disabled')" id="txtSubjectFromInvitation" class="required form-control" type="text" maxlength="255" name="senderSubject" value="<esapi:encodeForHTMLAttribute>${senderSubject}</esapi:encodeForHTMLAttribute>" />
						<br />
						<textarea id="text1default" style="display:none">
							Dear {Name},<br /><br />				
							I would like to invite you to the survey '<b>${form.survey.cleanTitle()}</b>' which will be available at this url:
						</textarea>
						
						<textarea id="text2default" style="display:none">
							Note that this is a unique personal link.<br />
							<b>Please do not share it.</b><br /><br />
							Best regards,<br />			
							<esapi:encodeForHTML>${USER.name}</esapi:encodeForHTML>
						</textarea>
						
						<label><spring:message code="label.MessageText" /> <a onclick="$('#messagehelp').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a></label>
						<div id="messagehelp" class="help hideme">
							<spring:message code="help.Message" />
						</div><br />
						<textarea class="tinymce freetext max5000 full ${noInviteLinks != null ? "nolinks" : ""}" name="text1" id="text1" onchange="$('#savetextbutton').removeAttr('disabled')">
							<c:choose>
								<c:when test="${participationGroup.template1 != null}">
									${participationGroup.template1}
								</c:when>
								<c:otherwise>
									Dear {Name},<br /><br />				
									I would like to invite you to the survey '<b>${form.survey.cleanTitle()}</b>' which will be available at this url:
								</c:otherwise>
							</c:choose>			
						</textarea>
						<br />
						
						<span id="url">
							<c:choose>
								<c:when test="${form.survey.security.equalsIgnoreCase('openanonymous')}">
									{host}/runner/${form.survey.uniqueId}
								</c:when>
								<c:otherwise>
									{host}/runner/invited/${participationGroup.id}/{UniqueAccessLink}
								</c:otherwise>
							</c:choose>
						</span>
										
						<br /><br />
						<label><spring:message code="label.Signature" /></label><br />
						<textarea class="tinymce freetext max5000 full ${noInviteLinks != null ? "nolinks" : ""}" name="text2" id="text2" onchange="$('#savetextbutton').removeAttr('disabled')">
							<c:choose>
								<c:when test="${participationGroup.template2 != null}">
									${participationGroup.template2}
								</c:when>
								<c:otherwise>
									Note that this is a unique personal link.<br />
									<b>Please do not share it.</b><br /><br />
									Best regards,<br />			
									<esapi:encodeForHTML>${USER.name}</esapi:encodeForHTML>
								</c:otherwise>
							</c:choose>					
						</textarea>

						<c:if test="${noInviteLinks != null}">
							<br>
							<p style="max-width: 510px"><i><spring:message code="message.NoLinksInInvite"/></i></p>
						</c:if>

					</div>
					
					<div style="clear: both"></div>
					<div style="float: left">
						<a onclick="step1()" class="btn btn-default"><spring:message code="label.Previous" /></a>
					</div>					
					<div style="float: right">
						<a id="btnCancelFromSendInvitationStep1" href="<c:url value="/${sessioninfo.shortname}/management/participants" />" class="btn btn-default"><spring:message code="label.Cancel" /></a>
					</div>
					<div style="text-align: center">
						<a id="btnNextFromSendInvitationStep1"  onclick="step3(true)" class="btn btn-primary"><spring:message code="label.Next" /></a>
					</div>
				</div>						
			</div>
			
			<div id="dialog-step3" data-bind="visible: Step() == 3">
			  	<div class="fullpageform" style="padding-top: 40px; max-width: 800px; margin-left: auto; margin-right: auto;">
			  		<div style="border: 1px solid #333; padding: 20px; border-radius: 5px; margin-bottom: 5px;">
				  		<table>
				  			<tr>
				  				<td><b><spring:message code="label.To" />:</b></td>
				  				<td><span id="preview-to" style="margin-left: 10px"></span></td>
				  			</tr>
				  			<tr>
				  				<td><b><spring:message code="label.ReplyTo" />:</b></td>
				  				<td><span id="preview-replyto" style="margin-left: 10px"></span></td>
				  			</tr>
				  			<tr>
				  				<td><b><spring:message code="label.Subject" />:</b></td>
				  				<td><span id="preview-subject" style="margin-left: 10px"></span></td>
				  			</tr>
				  		</table>
				  		<div id="preview" style="border: 1px solid #ccc; padding: 5px; margin-bottom: 10px; margin-top: 10px; background-color: #eee; height: 370px; overflow: auto"></div>
					</div>
					<div style="text-align: center">
						<a data-toggle="tooltip" title="<spring:message code="label.GoToPreviousMail" />" onclick="previousContact()" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-chevron-left"></span></a>
						<spring:message code="label.Email" />&nbsp;
						<select id="preview-current" style="width: auto; margin: 0px;" onchange="loadPreview(parseInt(this.value)-1);"></select>&nbsp;
						<spring:message code="label.of" />&nbsp;
						<span id="preview-selectedcontacts">${attendees.size()}</span>
						<a data-toggle="tooltip" title="<spring:message code="label.GoToNextMail" />" onclick="nextContact()" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-chevron-right"></span></a>
					</div>
					<div style="text-align: center; margin-top: 20px;">
						<div style="float: left">
							<a id="btnPreviousFromSendInvitationStep3"  onclick="step2(false)" class="btn btn-default"><spring:message code="label.Previous" /></a>
						</div>
						<div style="float: right">
							<a id="btnCancelFromSendInvitationStep2" href="<c:url value="/${sessioninfo.shortname}/management/participants" />" class="btn btn-default"><spring:message code="label.Cancel" /></a>
						</div>				
						<a id="btnSendFromSendInvitation"  data-dismiss="modal" onclick="checkAndSubmit();" class="btn btn-primary"><spring:message code="label.SendEmails" /></a>
					</div>
				</div>
			</div>	
		</form:form>
		
		<div data-backdrop="static" class="modal" id="savetextdialog" tabindex="-1" role="dialog">
			<div class="modal-dialog">
	   		<div class="modal-content">
			<div class="modal-header"><spring:message code="label.SaveAsTemplate" /></div>
		  	<div class="modal-body">
		  		<spring:message code="info.SaveMailTemplate" /><br /><br />
		  		 
				<span class='mandatory'>*</span><spring:message code="label.TemplateName" />
		  		<input type="text" class="form-control" id="savetextdialoginput" />
		  	</div>
			<div class="modal-footer">
				<div style="float: right">
					<a onclick="$('#savetextdialog').modal('hide');" class="btn btn-default"><spring:message code="label.Cancel" /></a>
				</div>				
				<a onclick="saveTemplate();" class="btn btn-primary"><spring:message code="label.SaveTemplate" /></a>
			</div>
			</div>
			</div>
		</div>
		
		<div data-backdrop="static" class="modal" id="deletetextdialog" tabindex="-1" role="dialog">
			<div class="modal-dialog">
	   		<div class="modal-content">
			<div class="modal-header"><spring:message code="label.DeleteText" /></div>
		  	<div class="modal-body">
		  		<span class='mandatory'>*</span>
		  		<spring:message code="info.DeleteText" />&nbsp;<span id="deletetextdialogname" style="font-weight: bold"></span>
		  	</div>
			<div class="modal-footer">
				<div style="float: right">
					<a onclick="$('#deletetextdialog').modal('hide');" class="btn btn-default"><spring:message code="label.Cancel" /></a>
				</div>				
				<a onclick="deleteTemplate();" class="btn btn-primary"><spring:message code="label.OK" /></a>
			</div>
			</div>
			</div>
		</div>
		
		<div data-backdrop="static" class="modal" id="ask-save-dialog" tabindex="-1" role="dialog">
			<div class="modal-dialog">
	   		<div class="modal-content">
			<div class="modal-header"><spring:message code="label.SaveText" /></div>
		  	<div class="modal-body">
		  		<spring:message code="question.SaveEmailTemplate" />
		  	</div>
			<div class="modal-footer">
				<a onclick="$('#ask-save-dialog').modal('hide');$('#savetextdialog').modal('show')" class="btn btn-primary"><spring:message code="label.Yes" /></a>
				<a onclick="step3(false);" class="btn btn-default" id="btnNotSaveMailText"><spring:message code="label.No" /></a>
				<a onclick="$('#ask-save-dialog').modal('hide');" class="btn btn-default"><spring:message code="label.Cancel" /></a>
			</div>
			</div>
			</div>
		</div>
	</div>
<%@ include file="../footer.jsp" %>	

</body>
</html>
