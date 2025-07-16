<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script type="text/javascript">
    $(function() {
		$("#form-menu-tab").addClass("active");
		$("#properties-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");

		$("#save-form").on("submit", function(){
			var sec = "open";
			if ($('#myonoffswitchsecured').is(":checked")) {
				sec = "secured";
			}
			if ($('#myonoffswitchprivacy').is(":checked")) {
				sec += "anonymous";
			}
			$("#survey-security").val(sec);
		});

		var uploader = new qq.FileUploader({
		    element: $("#file-uploader-logo")[0],
		    action: contextpath + '/${sessioninfo.shortname}/management/uploadimage',
		    uploadButtonText: selectFileForUpload,
		    params: {
		    	'_csrf': csrftoken
		    },
		    multiple: false,
		    cache: false,
		    sizeLimit: 1048576,
		    onComplete: function(id, fileName, responseJSON)
			{
		    	if (responseJSON.success)
		    	{
			    	$("#logo-cell").find("img").remove();
			    	var img = document.createElement("img");
			    	$(img).attr("src", contextpath + "/files/" + surveyUniqueId +  "/" + responseJSON.id);
			    	$(img).attr("width",responseJSON.width);
			    	$(img).attr("data-width",responseJSON.width);
			    	$("#logo-cell").find("img").remove();
			    	$("#logo-cell").find("p").remove();
			    	$("#logo-cell").prepend("<p>" + responseJSON.name + "</p>");
			    	$("#logo-cell").prepend(img);
			    	$("#logo").val(responseJSON.id);
			    	$("#logo-cell").find(".disabled").removeClass("disabled").show();
			    	$("#file-uploader-area-div").show();
			    	$("#removelogobutton").removeClass("disabled").show();
		    	} else {
		    		showError(invalidFileError);
		    	}
			},
			showMessage: function(message){
				$("#file-uploader-logo").append("<div class='validation-error'>" + message + "</div>");
			},
			onUpload: function(id, fileName, xhr){
				$("#file-uploader-logo").find(".validation-error").remove();
			}
		});

		$(".qq-upload-button").addClass("btn btn-default").removeClass("qq-upload-button");
		$(".qq-upload-list").hide();
		$(".qq-upload-drop-area").css("margin-left", "-1000px");

		$('.navbar-default li a').click(function(event) {
		    event.preventDefault();
		    $($(this).attr('href'))[0].scrollIntoView();
		    scrollBy(0, -offset);
		});

		ko.applyBindings(_properties, $('#propertiespage')[0]);

		$('#propertiespage').find('input[type="hidden"][name^="_survey"]').each(function(){
			$('#save-form').append(this);
		});

		$(".datepicker").datepicker('option', 'dateFormat', "dd/mm/yy");

		$("#survey\\.contact").val($("#survey\\.contact").val().replace("form:", ""));

		//enablechargeback-feature: set all organisations for select dropdown
		if ('${enablechargeback == 'true'}') {
			var url = "/utils/Organisations";
			var organisation = '${form.survey.organisation}';

			$.ajax({type: "GET",
				url: contextpath + url,
				async: false,
				success :function(result)
				{
					$.each(result.dgs, function(key, data){
						var option = document.createElement("option");
						$(option).attr("value", key);
						if (data.length > 95) {
							$(option).append(data.substring(0, 95) + "...");
						} else {
							$(option).append(data);
						}
						if (organisation == key) {
							$(option).attr("selected", "selected");
						}
						$(option).attr("title", data);
						$('#survey-organisation-dgs').append(option);
					});

					$.each(result.executiveAgencies, function(key, data){
						var option = document.createElement("option");
						$(option).attr("value", key);
						if (data.length > 95) {
							$(option).append(data.substring(0, 95) + "...");
						} else {
							$(option).append(data);
						}
						if (organisation == key) {
							$(option).attr("selected", "selected");
						}
						$(option).attr("title", data);
						$('#survey-organisation-aex').append(option);
					});

					$.each(result.otherEUIs, function(key, data){
						var option = document.createElement("option");
						$(option).attr("value", key);
						if (data.length > 95) {
							$(option).append(data.substring(0, 95) + "...");
						} else {
							$(option).append(data);
						}
						if (organisation == key) {
							$(option).attr("selected", "selected");
						}
						$(option).attr("title", data);
						$('#survey-organisation-euis').append(option);
					});

					$.each(result.nonEUIs, function(key, data){
						var option = document.createElement("option");
						$(option).attr("value", key);
						if (data.length > 95) {
							$(option).append(data.substring(0, 95) + "...");
						} else {
							$(option).append(data);
						}
						if (organisation == key) {
							$(option).attr("selected", "selected");
						}
						$(option).attr("title", data);
						$('#survey-organisation-noneuis').append(option);
					});

					if (organisation.length == 0 || organisation == 'external') organisation = "OTHER";
					$('#survey-organisation').val(organisation);
                    $('#survey-organisation').attr("title", organisation);

					checkValidator();
				}
			});
		}

        $("#survey-organisation").change(function() {
            this.title = $(this).find("option:selected").attr("title");
        }).change();

        $("#tags").autocomplete({
            autoFocus: true,
            source: "${contextpath}/forms/tags?createNewTag=true",
            search: function( event, ui ) {
                let term = event.target.value;
                const reg = /^[a-zA-Z0-9-_]+$/;

                if( !reg.test( term ) ) {
                    if ($("#tags").parent().parent().find(".validation-error").length == 0)
                    {
                        $("#tags").parent().parent().append("<div class='validation-error'>" + tagsText + "</div>");
                    }
                    event.preventDefault();
                    $("#tags").autocomplete("close");
                    _properties.tagsLoading(false);
                    return false;
                } else {
                    $("#tags").parent().parent().find(".validation-error").remove();
                }

                _properties.tagsLoading(true);

            },
            response: function( event, ui ) {
                _properties.tagsLoading(false);
            },
            select: function( event, ui ) {
                let tag = ui.item.value;

                // a tag needs at least 3 characters
                if (tag.length > 2) {

                  if (_properties.tags().length > 9) {
                    showError(tagsPerSurvey);
                  } else {
                      _properties.addTag(tag);
                  }
                  $("#tags").val("");
                }

                _properties.tagsLoading(false);
                return false;
            }
        });
	});

	function checkTagKeyUp(event) {
	    if (event.key === "Enter") return;
	    if(event.target.value.length > 0) {
            _properties.tagsLoading(true);
        }
	}

	function checkValidator() {
        let organisation = $('#survey-organisation').val();
        if (organisation != "" && organisation != "CITIZEN" && organisation != "OTHER" && organisation != "PRIVATEORGANISATION" && organisation != "PUBLICADMINISTRATION") {
            $('#survey-validator-div').show();
            $('#survey-validator').addClass("required");
            $('[data-toggle="tooltip"]').tooltip();
        } else {
            $('#survey-validator-div').hide();
            $('#survey-validator').removeClass("required");
        }
    }

</script>


