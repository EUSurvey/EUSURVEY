function showMessage(text)
{
	$("#generic-show-messages-dialog-text").html(text);
	$("#generic-show-messages-dialog").modal('show');
}

function showMessages(messages)
{
	var body = $("#generic-show-multiple-messages-dialog").find(".modal-body").first();
	$(body).empty();
	
	for (var i = 0; i < messages.length; i++)
	{
		$(body).append("<p>" + messages[i] + "</p>");
	}

	$("#generic-show-multiple-messages-dialog").modal('show');
}

function showInfo(text)
{
	$("#generic-info-box-text").html(text);
	$("#generic-info-box").show();
	window.setTimeout("hideGenericInfos()", 10000);
}

function showExportSuccessMessage()
{
	showInfo(message_Export1 + '&nbsp;<a class="visiblelink" href="' + contextpath + '/exports/list">' + label_ExportPage + '</a>');
}

function showPublicationExportSuccessMessage()
{
	showInfo(message_PublicationExportSuccess);
}

function showPublicationExportSuccessMessage2(mail)
{
	showInfo(message_PublicationExportSuccess2.replace('{0}', mail));
}

function showError(text)
{
	$("#generic-error-box-text").html(text);
	$("#generic-error-box").show();
	window.setTimeout("hideGenericInfos()", 10000);
}

function showExportFailureMessage()
{
	showError(message_ExportFailed);
}

function showPublicationExportFailureMessage()
{
	showError(message_PublicationExportFailed);
}

function hideGenericInfos()
{
	$("#generic-info-box").hide(400);
	$("#generic-error-box").hide(400);
}

function hideExports()
{
	$("#export-available-box").hide(400);
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