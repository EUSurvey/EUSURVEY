<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.SendInvitations" /></title>
	
	<%@ include file="../includes.jsp" %>
	
	<link href="${contextpath}/resources/css/management.css" rel="stylesheet" type="text/css" />
	
	<script type="text/javascript"> 
		$(function() {					
			<c:if test="${participationGroup.lastUsedTemplateID != null}">
				if ($("#mailtext").find("option[value='${participationGroup.lastUsedTemplateID}']").length > 0)
				{
					$("#mailtext").val("${participationGroup.lastUsedTemplateID}");
					setTimeout(function(){ loadTemplate(); }, 500);					
				}
			</c:if>
			
			$("#form-menu-tab").addClass("active");
			$("#participants-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
			$('#myModalCheck').modal('hide');
			
			if ($("tbody").find("input:checkbox:not(:checked)").length > 0)
			{
				$("#checkAll").removeAttr("checked");
			}
			
			step1();
		});				
		
		function check()
		{
			if ($("#checkAll").is(":checked"))
			{
				$("tbody").find("input[type='checkbox']").prop("checked","checked");	
				$("#checkAllUnInvited").prop("checked","checked");
				$("#checkAllUnAnswered").prop("checked","checked");
			} else {
				$("tbody").find("input[type='checkbox']").removeAttr("checked");
				$("#checkAllUnInvited").removeAttr("checked");
				$("#checkAllUnAnswered").removeAttr("checked");
			}
		}
		
		function checkUninvited()
		{
			if ($("#checkAllUnInvited").is(":checked"))
			{
				$(".uninvited").prop("checked","checked");	
			} else {
				$(".uninvited").removeAttr("checked");
				$("#checkAll").removeAttr("checked");
			}
		}
		
		function checkUnanswered()
		{
			if ($("#checkAllUnAnswered").is(":checked"))
			{
				$(".unanswered").prop("checked","checked");	
			} else {
				$(".unanswered").removeAttr("checked");
				$("#checkAll").removeAttr("checked");
			}
		}
		
		function PostTemplate(id,text1,text2,subject,replyto,name,template,texttemplate) {
		    // build json object
		    var t = {
		        id: id,
		        text1: text1,
		        text2: text2,
		        subject: subject,
		        replyto: replyto,
		        name: name,
		        template: template,
		        texttemplate: texttemplate
		    };

		    return t;
		}
		
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
					  if (result == 'EXISTS')
					  {
						 $('#savetextdialoginput').after("<div class='validation-error'><spring:message code="error.NameAlreadyUsed" /></div>");
					  } else if (result == 'ERROR')
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
						  $('#dialog-step1').modal('show');
						  $("#savetextbutton").attr("disabled","disabled");
						  showInfo("<spring:message code="label.TextSaved" />");	
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
			$('#dialog-step1').modal('hide');
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
					  if (result == "OK")
					  {
					 	$("#mailtext").find("option[value='" + id + "']").remove();
					  	$("#mailtext").val(0);
					  } else {
						  showError("<spring:message code="error.OperationFailed" />");
					  }
					  $('#deletetextdialog').modal('hide')
					  $('#dialog-step1').modal('show');						
				  },
				  error: function(e)
				  {
					  alert(e);
				  }
			});
		}
		
		function checkAndSubmit()
		{
			$(".validation-error").remove();
			
			var sender = $("#senderAddress").val();
			if (sender.length > 0)
			{
				 if( !validateEmail(sender)) {
			    	if ($("#senderAddress").parent().find(".validation-error").length == 0)
					{
		    				$("#senderAddress").after("<div class='validation-error'>" + invalidEmail + "</div>");
					};
					step1();
		    		return false;
				 } 			
			}			
			
			var text1 = $("#text1").html();
			if (text1 != null && text1.length > 5000)
			{
				$("#text1").after("<div class='validation-error'>" + texttoolongText + "</div>");
				step1();
	    		return false;
			}
			
			var text2 = $("#text2").html();
			if (text1 != null && text1.length > 5000)
			{
				$("#text2").after("<div class='validation-error'>" + texttoolongText + "</div>");
				step1();
	    		return false;
			}
			
			$('#myModalCheck').modal('show');
		}
		
		function step1()
		{
			$('#dialog-step2').modal('hide');
			$('#dialog-step3').modal('hide');
			$('#dialog-step1').modal('show');
		}
		
		function step2(validate)
		{
			if (validate)
			{
				if ($("#savetextbutton").attr('disabled') != 'disabled')
				{
					$('#dialog-step1').modal('hide');
					$("#ask-save-dialog").modal('show');
					return;
				}
				
				if (!validateInput($('#dialog-step1')))
				{
					return;
				}
			}
			$("#ask-save-dialog").modal('hide');
			$('#dialog-step1').modal('hide');
			$('#dialog-step3').modal('hide');
			$('#dialog-step2').modal('show');
		}
		
		function step3()
		{
			var selected = $("tbody").find("input[type='checkbox']:checked").length;
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
			
			$('#dialog-step2').modal('hide');
			$('#dialog-step1').modal('hide');
			$('#dialog-step3').modal('show');

			$('[data-toggle="tooltip"]').tooltip();
		}
		
		function loadPreview(c)
		{
			$('#preview-current').val(c+1);
			
			var currentcontactrow = $($("tbody").find("input[type='checkbox']:checked")[c]).closest("tr");
			var s = $("#text1").html() + "<br /><br />" + $("#url").html() + "<br /><br />" + $("#text2").html();
			
			var res = s.match(/{.*?}/g);
			
			for (var i = 0; i < res.length; i++)
			{
				var placeholder = res[i];
				if (placeholder == "{Name}" || placeholder == "{name}")
				{
					s = s.replace(placeholder, $(currentcontactrow).find("[data-class='name']").text());
				} else if (placeholder == "{Email}" || placeholder == "{email}")
				{
					s = s.replace(placeholder, $(currentcontactrow).find("[data-class='email']").text());
				} else if (placeholder == "{host}")
				{
					s = s.replace(placeholder + "/", '${serverprefix}');
				} else if (placeholder == "{UniqueAccessLink}")
				{
					//keep
				} else {
					if ($(currentcontactrow).find("[data-id='" + placeholder + "']"))
					{
						s = s.replace(placeholder, $(currentcontactrow).find("[data-id='" + placeholder + "']").first().text());
					}					
				}
			}
			
			$("#preview").html(s)
			$("#preview-to").html($(currentcontactrow).find("[data-class='email']").text())
			$("#preview-replyto").html($('#senderAddress').val());
			$("#preview-subject").html($('#txtSubjectFromInvitation').val());
		}
		
		function previousContact()
		{
			var current = parseInt($('#preview-current').val());
			
			if (current > 1)
			{
				loadPreview(current - 2);	
			}
		}
		
		function nextContact()
		{
			var current = parseInt($('#preview-current').val());
			var selected = $("tbody").find("input[type='checkbox']:checked").length;
			
			if (current < selected)
			{
				loadPreview(current);	
			}
		}
		
	</script>
	
	<c:if test="${message != null && message.length() > 0}">
		<script type="text/javascript"> 
		$(function() {
			$('#dialog-step1').modal('hide');
			$('#myModal').modal();
		});
		</script>
	</c:if>
	
	<c:if test="${errorMessage != null && errorMessage.length() > 0}">
		<script type="text/javascript"> 
		$(function() {
			$('#dialog-step1').modal('hide');
			$('#myModalError').modal();
		});
		</script>
	</c:if>
		
</head>
<body>

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
	    <a id="btnOkInvitationSendFromInvitation" href="<c:url value="/${sessioninfo.shortname}/management/participants" />" class="btn btn-info"><spring:message code="label.OK" /></a>
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
	    <a href="<c:url value="/${sessioninfo.shortname}/management/participants" />" class="btn btn-info"><spring:message code="label.OK" /></a>
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
			    <a id="btnConfirmFromSendInvitation" onclick="$('#myModalCheck').modal('hide');$('#generic-wait-dialog').modal('show');$('#mailForm').submit();"  class="btn btn-info"><spring:message code="label.OK" /></a>
		<a id="btnCancelConfirmFromSendInvitation" onclick="$('#myModalCheck').modal('hide');$('#dialog-step3').modal('show');"  class="btn btn-default"><spring:message code="label.Cancel" /></a>
	  </div>
	  </div>
	  </div>
	</div>	
	
	<form:form id="mailForm" method="POST" action="${contextpath}/${sessioninfo.shortname}/management/sendInvitations">
	
		<input type="hidden" id="selectedAttendee" name="selectedAttendee" value="" />
		<input type="hidden" id="participationGroup" name="participationGroup" value="${participationGroup.id}" />	
	
		<div data-backdrop="static" class="modal" id="dialog-step1" tabindex="-1" role="dialog">
			<div class="modal-dialog">
    		<div class="modal-content">
			<div class="modal-header">
		       	<spring:message code="label.SendInvitations" /> - <spring:message code="label.Step1" />: <spring:message code="label.EmailText" />
		  	</div>
		  	<div class="modal-body">
		  		<div style="padding: 8px; border: 2px solid #ddd; background-color: #efefef; margin-bottom: 10px;">
		  				
				<table style="margin-bottom: 5px; width: 100%;">
					<tr>
						<td style="padding-bottom: 5px; vertical-align: top">
			  				<spring:message code="label.Style" /><br />
			  				<select onchange="$('#savetextbutton').removeAttr('disabled')" id="mailtemplate" name="mailtemplate" class="small-form-control" style="width: 270px">
								<option value="eusurvey" selected="selected">EUSurvey</option>
								<c:if test="${USER.getGlobalPrivilegeValue('ECAccess') > 0}">
									<option value="ecofficial"><spring:message code="label.ECofficial" /></option>
								</c:if>
								<option value="plaintext"><spring:message code="label.Notemplate" /></option>
							</select>
		  				</td>
		  			
						<td style="vertical-align: top; padding-left: 10px;">
							<spring:message code="label.Text" /><br />
							<select onchange="loadTemplate()" id="mailtext" name="mailtext" class="small-form-control" style="width: 270px">
								<option value="0" selected="selected">Default</option>
								<c:if test="${usertexts != null}">
									<c:forEach items="${usertexts}" var="text">
										<option value="${text.id}">${text.name}</option>
									</c:forEach>
								</c:if>
							</select>
							<div style="margin-top: 5px">
								<button id="savetextbutton" disabled="disabled" onclick="$('#dialog-step1').modal('hide');$('#savetextdialog').modal('show'); return false;" class="btn btn-default"><spring:message code="label.SaveText" /></button>
								<button id="deletetextbutton" disabled="disabled" onclick="showDeleteDialog(); return false;" class="btn btn-default"><spring:message code="label.DeleteText" /></button>
							</div>		
						</td>		  	
					</tr>
				</table>
				
				</div>
				
				<table>					
					<tr>
						<td style="padding-right: 10px; padding-top: 5px; vertical-align: top"><spring:message code="label.ReplyTo" /></td>
						<td id="txtSenderFromInvitation" style="vertical-align: top; padding-bottom: 5px"><input type="text" onchange="$('#savetextbutton').removeAttr('disabled');validateInput($(this).parent());" class="email small-form-control" maxlength="255"  value="<esapi:encodeForHTMLAttribute>${USER.email}</esapi:encodeForHTMLAttribute>" id="senderAddress" name="senderAddress" /></td>
					</tr>
					<tr>
						<td style="padding-right: 10px; padding-top: 5px; vertical-align: top"><spring:message code="label.Subject" /></td>
						<td  style="vertical-align: top; padding-bottom: 5px"><input onchange="$('#savetextbutton').removeAttr('disabled')" id="txtSubjectFromInvitation" class="required small-form-control" type="text" maxlength="255" name="senderSubject" value="<esapi:encodeForHTMLAttribute>${senderSubject}</esapi:encodeForHTMLAttribute>" /></td>
					</tr>
				</table>
				
				<textarea id="text1default" style="display:none">
					Dear {Name},<br /><br />				
					I would like to invite you to the survey '<b>${form.survey.cleanTitle()}</b>' which will be available at this url:
				</textarea>
				
				<textarea id="text2default" style="display:none">
					Best regards,<br />			
					<esapi:encodeForHTML>${USER.name}</esapi:encodeForHTML>
				</textarea>
				
				<textarea class="tinymce freetext max5000" name="text1" id="text1" onchange="$('#savetextbutton').removeAttr('disabled')">
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
				<textarea class="tinymce freetext max5000" name="text2" id="text2" onchange="$('#savetextbutton').removeAttr('disabled')">
					<c:choose>
						<c:when test="${participationGroup.template2 != null}">
							${participationGroup.template2}
						</c:when>
						<c:otherwise>
							Best regards,<br />			
							<esapi:encodeForHTML>${USER.name}</esapi:encodeForHTML>
						</c:otherwise>
					</c:choose>					
				</textarea>
				
			</div>
			<div class="modal-footer">
				<div style="float: right">
					<a id="btnCancelFromSendInvitationStep1" href="<c:url value="/${sessioninfo.shortname}/management/participants" />" class="btn btn-default"><spring:message code="label.Cancel" /></a>
				</div>
				<a id="btnNextFromSendInvitationStep1"  onclick="step2(true)" class="btn btn-info"><spring:message code="label.Next" /></a>						
			</div>
			</div>
			</div>
		</div>	
		<div data-backdrop="static" class="modal" id="dialog-step2" tabindex="-1" role="dialog">
			<div class="modal-dialog">
    		<div class="modal-content">
			<div class="modal-header">
		       	<spring:message code="label.SendInvitations" /> - <spring:message code="label.Step2" />: <spring:message code="label.Contacts" />
		  	</div>
		  	<div class="modal-body" style="max-height: 550px; height: 550px; overflow: auto;">
				<input id="checkAllUnInvited" type="checkbox" checked="checked" onchange="checkUninvited();" /> <spring:message code="label.SelectUninvitedContacts" /><br />
				<input id="checkAllUnAnswered" type="checkbox" onchange="checkUnanswered();" /> <spring:message code="label.SelectInvited" />
				
				<table id="tblInvitedFromSendInvitation" class="table table-bordered table-styled" style="margin-top: 10px; width: auto;">
					<thead>
						<tr>
							<th><input id="checkAll" type="checkbox" checked="checked" onchange="check();" /></th>
							<th><spring:message code="label.Name" /></th>
							<th><spring:message code="label.Email" /></th>
							<th><spring:message code="label.InvitationDate" /></th>
							<th><spring:message code="label.ReminderDate" /></th>
							<th><spring:message code="label.Answers" /></th>
							<!-- <th>&#160;</th> -->
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
			</div>
			<div class="modal-footer">
				<div style="float: right">
					<a id="btnCancelFromSendInvitationStep2" href="<c:url value="/${sessioninfo.shortname}/management/participants" />" class="btn btn-default"><spring:message code="label.Cancel" /></a>
				</div>
				<div style="float: left">
					<a id="btnPreviousFromSendInvitationStep2"  onclick="step1()" class="btn btn-default"><spring:message code="label.Previous" /></a>
				</div>				
				<a id="btnNextFromSendInvitationStep2"  onclick="step3()" class="btn btn-info"><spring:message code="label.Next" /></a>
			</div>
			</div>
			</div>
		</div>	
		<div data-backdrop="static" class="modal" id="dialog-step3" tabindex="-1" role="dialog">
			<div class="modal-dialog">
    		<div class="modal-content">
			<div class="modal-header">
		       	<spring:message code="label.SendInvitations" /> - <spring:message code="label.Step3" />: <spring:message code="label.Preview" />
		  	</div>
		  	<div class="modal-body" style="max-height: 550px; height: 550px">
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
				<a data-toggle="tooltip" title="<spring:message code="label.GoToPreviousMail" />" onclick="previousContact()" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-chevron-left"></span></a>
				<spring:message code="label.Contact" />&nbsp;
				<select id="preview-current" style="width: auto; margin: 0px;" onchange="loadPreview(parseInt(this.value)-1);"></select>&nbsp;
				<spring:message code="label.of" />&nbsp;
				<span id="preview-selectedcontacts">${attendees.size()}</span>
				<a data-toggle="tooltip" title="<spring:message code="label.GoToNextMail" />" onclick="nextContact()" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-chevron-right"></span></a>
			</div>
			<div class="modal-footer">
				<div style="float: left">
					<a id="btnPreviousFromSendInvitationStep3"  onclick="step2(false)" class="btn btn-default"><spring:message code="label.Previous" /></a>
				</div>
				<div style="float: right">
					<a id="btnCancelFromSendInvitationStep2" href="<c:url value="/${sessioninfo.shortname}/management/participants" />" class="btn btn-default"><spring:message code="label.Cancel" /></a>
				</div>				
				<a id="btnSendFromSendInvitation"  data-dismiss="modal" onclick="checkAndSubmit();" class="btn btn-info"><spring:message code="label.SendEmails" /></a>
			</div>
			</div>
			</div>
		</div>	
	</form:form>
	
	<div data-backdrop="static" class="modal" id="savetextdialog" tabindex="-1" role="dialog">
		<div class="modal-dialog">
   		<div class="modal-content">
		<div class="modal-header"><spring:message code="label.SaveText" /></div>
	  	<div class="modal-body">
	  		<span class='mandatory'>*</span>
	  		<spring:message code="info.SaveText" /><br />	  		
	  		<input type="text" id="savetextdialoginput" />
	  	</div>
		<div class="modal-footer">
			<div style="float: right">
				<a onclick="$('#savetextdialog').modal('hide');$('#dialog-step1').modal('show');" class="btn btn-default"><spring:message code="label.Cancel" /></a>
			</div>				
			<a onclick="saveTemplate();" class="btn btn-info"><spring:message code="label.OK" /></a>
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
				<a onclick="$('#deletetextdialog').modal('hide');$('#dialog-step1').modal('show');" class="btn btn-default"><spring:message code="label.Cancel" /></a>
			</div>				
			<a onclick="deleteTemplate();" class="btn btn-info"><spring:message code="label.OK" /></a>
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
			<a onclick="$('#ask-save-dialog').modal('hide');$('#savetextdialog').modal('show')" class="btn btn-info"><spring:message code="label.Yes" /></a>
			<a onclick="step2(false);" class="btn btn-default" id="btnNotSaveMailText"><spring:message code="label.No" /></a>
			<a onclick="$('#ask-save-dialog').modal('hide');$('#dialog-step1').modal('show')" class="btn btn-default"><spring:message code="label.Cancel" /></a>
		</div>
		</div>
		</div>
	</div>

<%@ include file="../footer.jsp" %>	

</body>
</html>
