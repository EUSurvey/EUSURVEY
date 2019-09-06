$(function() {			
	
	$(".file-uploader").each(function(){
		
		var element = this;
		
		var uploader = new qq.FileUploader({
		    element: this,
		    action: contextpath + '/noform/management/importSurvey',
		    uploadButtonText: selectFileForUpload,
		    params: {
		    	'_csrf': csrftoken
		    },
		    multiple: false,
		    sizeLimit: 524288000,
		    cache: false,
		    onComplete: function(id, fileName, responseJSON)
			{	
		    	if (responseJSON.success)
		    	{
		    		surveyID = responseJSON.id;
		    		$("#import-survey-dialog").modal("hide");
		    		$("#import-survey-dialog-2").modal("show");
		    	} else {
		    		
		    		if (responseJSON.exists)
		    		{
		    			uuid = responseJSON.uuid;
		    			title = responseJSON.title;
		    			contact = responseJSON.contact;
		    			contactlabel = responseJSON.contactLabel;
		    			$("#new-survey-contact").val(contact);
		    			$("#new-survey-contact-label").val(contactlabel);
		    			if (contact != null && contact.indexOf("@") > 0)
		    			{
		    				$("#new-survey-contact-type").val("email");
		    			} else {
		    				$("#new-survey-contact-type").val("url");
		    			}
		    			checkNewSurveyContactType();
		    			$("#new-survey-language").val(responseJSON.language);
		    			$("#import-survey-dialog").modal("hide");
			    		$("#show-import-copy-dialog").modal("show");
		    		} else {
		    			$("#import-survey-dialog").modal("hide");
		    			if (responseJSON.message == null || responseJSON.message.length == 0)
		    			{
		    				showMessage("Import not possible.");
		    			} else {
		    				showMessage(responseJSON.message);
				    	}
		    		}
		    	}	    	
			},
			showMessage: function(message){
				$(element).append("<div class='validation-error'>" + message + "</div>");
			},
			onUpload: function(id, fileName, xhr){
				$(element).find(".validation-error").remove();			
			}
		});
	});
	$(".qq-upload-button").attr('id','btnUploadSurvey');
	$("#btnUploadSurvey>input[name='file']").attr('id','txtUploadSurvey');
	$(".qq-upload-button").addClass("btn btn-default btn-info").removeClass("qq-upload-button");	
	$(".qq-upload-drop-area").css("margin-left", "-1000px");
			
});			