function showGenericMessages(messages)
{
	var body = $("#generic-show-multiple-messages-dialog").find(".modal-body").first();
	$(body).empty();
	
	for (var i = 0; i < messages.length; i++)
	{
		$(body).append("<p>" + messages[i] + "</p>");
	}

	$("#generic-show-multiple-messages-dialog").modal('show');
}

function showExportSuccessMessage()
{
	showSuccess(message_Export1 + '&nbsp;<a class="visiblelink" href="' + contextpath + '/exports/list">' + label_ExportPage + '</a>');
}

function showPublicationExportSuccessMessage()
{
	showSuccess(message_PublicationExportSuccess);
}

function showPublicationExportSuccessMessage2(mail)
{
	showSuccess(message_PublicationExportSuccess2.replace('{0}', mail));
}

function showExportFailureMessage()
{
	showError(message_ExportFailed);
}

function showPublicationExportFailureMessage()
{
	showError(message_PublicationExportFailed);
}

function downloadAnswerPDF(uid)
{
	$("#download-answer-pdf-dialog-result").hide();
	$("#download-answer-pdf-dialog-spinner").show();
	$("#download-answer-pdf-dialog").attr("data-uid", uid);
	$("#download-answer-pdf-dialog-error").hide();
	$("#download-answer-pdf-dialog-running").show();
	$("#download-answer-pdf-dialog-ready").hide();
	$("#download-answer-pdf-dialog").attr("data-scrolltop", $("body").scrollTop());
	
	$.ajax({
		type:'GET',
		url: contextpath + "/pdf/answerexists/" + uid,
		cache: false,
		success: function( result ) {	
			checkAnswerPDFResult(result);
		},
		error: function( result ) {	
			alert(result);
		}
	});
	
	$("#download-answer-pdf-dialog").modal('show');
	return false;
}

function checkAnswerPDFReady(uid)
{
	$.ajax({
		type:'GET',
		url: contextpath + "/pdf/answerready/" + uid,
		success: function( result ) {	
			checkAnswerPDFResult(result);
		},
		error: function( result ) {	
			alert(result);
		}
	});
}

function checkAnswerPDFResult(result)
{
	if (result == "exists")
	{
		$("#download-answer-pdf-dialog-result").attr("href", contextpath + "/pdf/answer/" + $("#download-answer-pdf-dialog").attr("data-uid")).show();
		$("#download-answer-pdf-dialog-spinner").hide();
		$("#download-answer-pdf-dialog-running").hide();
		$("#download-answer-pdf-dialog-ready").show();
	} else if (result == "wait") {
		window.setTimeout(function(){checkAnswerPDFReady($("#download-answer-pdf-dialog").attr("data-uid"))}, 5000);
	} else if (result == "error") {
		$("#download-answer-pdf-dialog-error").show();
	}
}