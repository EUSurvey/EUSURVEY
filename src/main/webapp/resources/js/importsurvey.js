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
		    		uuid = responseJSON.uuid;
		    		title = responseJSON.title;
		    		var shortname = responseJSON.shortname;
		    		contact = responseJSON.contact;
		    		
		    		if (!responseJSON.exists)
		    		{
		    			$('#new-survey-shortname').val(shortname);
		    		} else {
		    			$('#new-survey-shortname').val("");
		    		}
		    		
		    		$('#new-survey-title').val(title);
		    		$("#new-survey-contact").val(contact.replace("form:", ""));
	    			$("#new-survey-contact-label").val("");
	    			if (contact != null && contact.indexOf("form:") >= 0)
	    			{
	    				$("#new-survey-contact-type").val("form");
	    			} else if (contact != null && contact.indexOf("@") > 0)
	    			{
	    				$("#new-survey-contact-type").val("email");
	    			} else {
	    				$("#new-survey-contact-type").val("url");
	    			}
	    			checkNewSurveyContactType();
	    			$("#new-survey-language").val(responseJSON.language);
	    			$("#import-survey-dialog").modal("hide");
	    			importCopySurvey(responseJSON.login, contact);
		    	} else {
	    			$("#import-survey-dialog").modal("hide");
	    			if (responseJSON.message == null || responseJSON.message.length == 0)
	    			{
	    				showInfo("Import not possible.");
	    			} else {
	    				showInfo(responseJSON.message);
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
	$(".qq-upload-button").addClass("btn btn-default btn-primary").removeClass("qq-upload-button");	
	$(".qq-upload-drop-area").css("margin-left", "-1000px");
			
});			