var SendInvitationsPage = function() {
	var self = this;
	
	this.Step = ko.observable(1);
}

var _sendInvitationsPage = new SendInvitationsPage();

$(function() {						
	$("#form-menu-tab").addClass("active");
	$("#participants-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
	$('#myModalCheck').modal('hide');
	
	if ($("tbody").find("input:checkbox:not(:checked)").length > 0)
	{
		$("#checkAll").removeAttr("checked");
	}
	
	ko.applyBindings(_sendInvitationsPage, $("#sendinvitations")[0]);
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

function checkAndSubmit()
{
	$(".validation-error").remove();
	
	var sender = $("#senderAddress").val();
	if (sender != null && sender.length > 0)
	{
		 if( !validateEmail(sender)) {
	    	if ($("#senderAddress").parent().find(".validation-error").length == 0)
			{
    				$("#senderAddress").after("<div class='validation-error'>" + invalidEmail + "</div>");
			};
			_sendInvitationsPage.Step(1);
    		return false;
		 } 			
	}			
	
	var text1 = $("#text1").html();
	if (text1 != null && text1.length > 5000)
	{
		$("#text1").after("<div class='validation-error'>" + texttoolongText + "</div>");
		_sendInvitationsPage.Step(1);
		return false;
	}
	
	var text2 = $("#text2").html();
	if (text1 != null && text1.length > 5000)
	{
		$("#text2").after("<div class='validation-error'>" + texttoolongText + "</div>");
		_sendInvitationsPage.Step(1);
		return false;
	}
	
	$('#myModalCheck').modal('show');
}

function replacePlaceholders(s, currentcontactrow)
{
	var res = s.match(/{.*?}/g);
	
	if (res == null)
	{
		return s;
	}
	
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
	
	return s;
}

function loadPreview(c)
{
	$('#preview-current').val(c+1);
	
	var currentcontactrow = $($("#tblInvitedFromSendInvitation tbody").find("input[type='checkbox']:checked")[c]).closest("tr");
	var s = $("#text1").html();
	if (!$("#text1").html().endsWith("</p>")) {
		s += "<br /><br />";
	}
	s += $("#url").html() + "<br /><br />" + $("#text2").html();

	
	s = replacePlaceholders(s, currentcontactrow);
	
	$("#preview").html(s)
	$("#preview-to").html($(currentcontactrow).find("[data-class='email']").text())
	$("#preview-replyto").html($('#senderAddress').val());
	
	s = $('#txtSubjectFromInvitation').val();
	s = replacePlaceholders(s, currentcontactrow);
	
	$("#preview-subject").html(s);
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
	var selected = $("#tblInvitedFromSendInvitation tbody").find("input[type='checkbox']:checked").length;
	
	if (current < selected)
	{
		loadPreview(current);	
	}
}