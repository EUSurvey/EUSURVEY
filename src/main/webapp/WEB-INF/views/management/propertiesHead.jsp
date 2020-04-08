<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

	<script type="text/javascript"> 	
		var offset = 300;
		var surveyShortname = '${form.survey.shortname}';
		var surveyUniqueId = '${form.survey.uniqueId}';
				
		function PropertiesViewModel()
		{
			this.self = this;
			this.contactType = ko.observable("${form.survey.contact.contains("@") ? "email" : "url"}");
			this.automaticPublishing = ko.observable(${form.survey.automaticPublishing});
			this.endNotifications = ko.observable(${form.survey.notificationValue != null && form.survey.notificationValue != -1 && form.survey.notificationValue.length() > 0});
			this.showUsefulLinks =  ko.observable(${form.survey.getAdvancedUsefulLinks().size() > 0});
			this.showBackgroundDocs = ko.observable(${form.survey.getBackgroundDocumentsAlphabetical().size() > 0});
			this.secured = ko.observable(${!(form.survey.security.equals("open") || form.survey.security.equals("openanonymous"))});
			this.ecasSecurity = ko.observable(${form.survey.ecasSecurity});
			this.publishResults = ko.observable(${form.survey.publication.showContent || form.survey.publication.showStatistics});
			this.selectedQuestions = ko.observable(${!form.survey.publication.allQuestions});
			this.selectedContributions = ko.observable(${!form.survey.publication.allContributions});
			this.useConfLink =  ko.observable(${form.survey.confirmationPageLink});
			this.useEscapeLink =  ko.observable(${form.survey.escapePageLink});
			this.quiz = ko.observable(${form.survey.isQuiz});
			this.opc = ko.observable(${form.survey.isOPC});
			this.multiPaging = ko.observable(${form.survey.multiPaging});
			this.isUseMaxNumberContribution = ko.observable(${form.survey.isUseMaxNumberContribution});
			this.isUseMaxNumberContributionLink = ko.observable(${form.survey.isUseMaxNumberContributionLink});
			
			this.addLinksRow = function()
			{
				var tr = document.createElement("tr");
				var td = document.createElement("td");
				var input = document.createElement("input");
				
				$(input).attr("style", "width: 180px").attr("type","text").attr("maxlength","250").addClass("form-control xhtml freetext max250").attr("name","linklabel" + ($("#usefullinkstable").find("tr").length-1));
				$(td).append(input);
				$(tr).append(td);
				
				td = document.createElement("td");
				input = document.createElement("input");
				$(input).attr("style", "width: 180px").attr("type","text").addClass("form-control targeturl").attr("maxlength","255").attr("name","linkurl" + ($("#usefullinkstable").find("tr").length-1));
				$(td).append(input);
				$(tr).append(td);
				
				td = document.createElement("td");
				$(td).css("vertical-align","middle").append('<a data-toggle="tooltip" title="Remove useful link" class="btn btn-default btn-xs" onclick="_properties.removeLinksRow(this)"><span class="glyphicon glyphicon-remove"></span></a>');
				$(tr).append(td);
				$(td).find("a").tooltip();
				
				$("#usefullinkstable").append(tr);
				this.self.showUsefulLinks(true);
			}
			
			this.removeLinksRow = function(link)
			{
				$(link).parent().parent().remove();
				this.self.showUsefulLinks($("#usefullinkstable").find("tr").length > 1);
			}
			
			this.addDocRow = function()
			{
				var tr = document.createElement("tr");
				var td = document.createElement("td");
				var input = document.createElement("input");
				
				$(input).attr("type","text").addClass("form-control xhtml freetext max250").attr("style", "width: 180px").attr("maxlength","255").attr("name","doclabel" + ($("#backgrounddocumentstable").find("tr").length-1));
				$(td).append(input);
				$(tr).append(td);
				
				td = document.createElement("td");
				input = document.createElement("input");
				$(input).attr("type","hidden").attr("name","docurl" + ($("#backgrounddocumentstable").addClass("xhtml").find("tr").length-1));
				$(td).append(input);
				
				var div = document.createElement("div");
				var id = "docid" + ($("#backgrounddocumentstable").find("tr").length-1);
				$(div).attr("id",id);
				$(td).append(div);
				var uploader = new qq.FileUploader({
				    element: div,
				    action: contextpath + '/' + surveyShortname + '/management/upload',
				    uploadButtonText: selectFileForUpload,
				    params: {
				    	'_csrf': csrftoken
				    },
				    multiple: false,
				    cache: false,
				    sizeLimit: 10485760,
				    onComplete: function(id, fileName, responseJSON)
					{
				    	var a = document.createElement("a");
				    	$(a).attr("href", contextpath + "/files/" + surveyUniqueId + "/" + responseJSON.id);
				    	$(a).append(responseJSON.name);
				    	
				    	var i = $(div).closest("tr").find("td").first().find("input[type=text]");
				    	if ($(i).val().length == 0)
				    	$(i).val(responseJSON.name);
				    	
				    	$(div).parent().append(a);
				    	$(div).parent().find("input[type='hidden']").val(contextpath + "/files/" + surveyUniqueId + "/" + responseJSON.id);
				    	$(div).remove();
					}
				});
				
				$(div).find(".qq-upload-button").addClass("btn btn-default").removeClass("qq-upload-button");
				$(".qq-upload-list").hide();
				$(".qq-upload-drop-area").css("margin-left", "-1000px");			
				
				$(tr).append(td);
				
				td = document.createElement("td");
				var a = document.createElement("a");
				$(a).attr("data-toggle","tooltip").attr("title","Remove useful link").addClass("btn btn-default btn-xs").append('<span class="glyphicon glyphicon-remove"></span>').click(function(){
					_properties.removeDocRow(this);
				});
				$(td).css("vertical-align","middle").append(a);
				$(tr).append(td);
				$(a).tooltip();
				
				$("#backgrounddocumentstable").append(tr);
				this.self.showBackgroundDocs(true);
			}
			
			this.removeDocRow = function(link)
			{
				var v = $(this).closest("tr").find("input[type='hidden']").val();
				$(link).parent().parent().remove();
				if (typeof v != 'undefined' && v.length > 0) {
					deleteFile(v);
				}
				this.self.showBackgroundDocs($("#backgrounddocumentstable").find("tr").length > 1);
			}
		}
		
		var _properties = new PropertiesViewModel();
		
		function checkPropertiesAndSubmit(regformconfirmed, publishingconfirmed)
		{
			try {				
				$("#propertiespage").find(".validation-error").remove();
				var tabs;
				var invalid = false;
			
				if (!checkShortname($('#edit-survey-shortname').val(), '${form.survey.id}'))
				{
					$('#edit-survey-shortname').parent().append("<div class='validation-error'>" + shortnameAlreadyExists + "</div>");
					return;
				}
				var value = $("#edit-survey-shortname").val();
				var reg = /^[a-zA-Z0-9-_]+$/;
				if ($("#edit-survey-shortname").parent().find(".validation-error").length == 0)
				{
					if( !reg.test( value ) ) {
				    	$("#edit-survey-shortname").after("<div class='validation-error'>" + shortnameText3 + "</div>");
						return;
				    } else if( value.indexOf("__") > -1 ) {
				    	$("#edit-survey-shortname").after("<div class='validation-error'>" + shortnameText2 + "</div>");
						return;
				    } ;
				}
				
				var title = $("#edit-survey-title").text().length;
				if (title > 2000)
				{
					$("#edit-survey-title").parent().append("<div class='validation-error'>" + texttoolongText + "</div>");
					return;
				}
			
				if ($("#survey\\.start").val().length > 0 && $("#survey\\.end").val().length > 0)
				{
					var startdate = parseDateTime($("#survey\\.start").val(),$("#startHour").val());
					var enddate =  parseDateTime($("#survey\\.end").val(),$("#endHour").val());
					if (startdate >= enddate)
					{
						$("#survey\\.end").parent().append("<div class='validation-error'>" + invalidStartEnd + "</div>");
						return;
					};					
					<c:if test="${!form.survey.isActive}">
						var now = new Date();						
						var autoPublishEnabled = ($("#autopub").is(":checked"));						
						if(autoPublishEnabled && startdate < now && enddate > now)
						{							
							$('#publishConfirmationDialog').modal('show');
							return;
						};
						
						<c:if test="${!form.survey.automaticPublishing}">
							if(autoPublishEnabled && startdate > now)
							{							
								$('#publishConfirmationDialog3').modal('show');
								return;
							};
						</c:if>
					</c:if>
					<c:if test="${form.survey.isActive}">
						var now = new Date();						
						var autoPublishEnabled = ($("#autopub").is(":checked"));						
						if(autoPublishEnabled && startdate > now)
						{							
							$('#publishConfirmationDialog2').modal('show');
							return;
						};
						
						if(autoPublishEnabled && enddate < now)
						{							
							$('#edit-properties-dialog').modal('hide');
							$('#publishConfirmationDialog4').modal('show');
							return;
						};
						
					</c:if>
				};
				
				if ($('#notificationselector1').is(":checked") && !$("#autopub").is(":checked"))
				{
					$('#notificationselector1').parent().append("<div class='validation-error'>" + endNotificationAutomatedPublishing + "</div>");
					return;
				}
				
				var labels = [];
				$("#propertiespage").find("input[name^='doclabel']").each(function(){
					var label = $(this).val();
					if (label.length > 0)
					{
						for (var i = 0; i < labels.length; i++)
						{
							if (labels[i] == label)
							{
								$(this).parent().append("<div class='validation-error'><spring:message code="error.LabelAlreadyUsed" /></div>");
								invalid = true;
								return;
							}
						}
						labels[labels.length] = label;
					}
				});					
				
				
				if (invalid) return;
				
				var result = validateInput(tabs);
				
				if (result == false)
				{
					return;
				}
				
				result = validateInput($("#maxContributionInput").parent());
				
				if (result == false)
				{
					return;
				}
				
				<c:if test="${!validregform}">
					if ($("#survey\\.registrationForm").is(":checked") && !regformconfirmed)
					{
						$("#confirmregformdialog").modal("show");
						return;
					}					
				</c:if>
				

				if ($("#survey\\.publication\\.allContributions1").is(":checked"))
				{
					var counter = $("#contributionsToPublishDiv").find("input[name^='contribution']:checked").length;
					if (counter > 3)
					{
						$("#contributionsToPublishDiv").before("<div class='validation-error'>" + atmost3Selections + "</div>");
						return;
					}					
				}
				
				<c:if test="${!form.survey.publication.showContent && !form.survey.publication.showCharts && !form.survey.publication.showStatistics}">
					if (!publishingconfirmed && ($("#showContent").is(":checked") || $("#showCharts").is(":checked") || $("#showStatistics").is(":checked")))
					{
						$("#confirmpublicationdialog").modal("show");
						return;
					}
				</c:if>		
				
			} catch (e)	{

			}
			
			unsavedChanges=false;			
			
			$("#generic-wait-dialog").modal("show");
			$('#save-form').submit();
		}
		
		function publishConfirmationClose()
		{
			$('#publishConfirmationDialog').modal('hide');
			$('#publishConfirmationDialog2').modal('hide');
			$('#publishConfirmationDialog3').modal('hide');
			$('#publishConfirmationDialog4').modal('hide');
		}

		function publishConfirmationOkClicked()
		{
			$('#publishConfirmationDialog').modal('hide');
			$('#publishConfirmationDialog2').modal('hide');
			$('#publishConfirmationDialog3').modal('hide');
			$('#publishConfirmationDialog4').modal('hide');
			unsavedChanges=false;
			validateInputAndSubmit($('#save-form'));
		}
		
		function checkShowPassword(input)
		{
			if ($(input).is(":checked"))
			{
				$("#survey\\.password").hide();
				$("#clearpassword").show();
			} else {
				$("#clearpassword").hide();
				$("#survey\\.password").show();
			}
		}

		function checkShowPublicationPassword(input)
		{
			if ($(input).is(":checked"))
			{
				$("#survey\\.publication\\.password").hide();
				$("#clearpublicationpassword").show();
			} else {
				$("#clearpublicationpassword").hide();
				$("#survey\\.publication\\.password").show();
			}
		}
		
	</script>
	
	<style type="text/css">
	
		body {
		  position: relative;
		  height: 100%;
		}

		.onoffswitch {
		    position: relative; width: 40px;
		    -webkit-user-select:none; -moz-user-select:none; -ms-user-select: none;
		    margin: 5px;
		}
		.onoffswitch-checkbox {
		    display: none;
		}
		.onoffswitch-label {
		    display: block; overflow: hidden; cursor: pointer;
		    border: 2px solid #999999; border-radius: 20px;
		    height: auto;
		}
		.onoffswitch-inner {
		    display: block; width: 200%; margin-left: -100%;
		    transition: margin 0.3s ease-in 0s;
		}
		.onoffswitch-inner:before, .onoffswitch-inner:after {
		    display: block; float: left; width: 50%; height: 5px; padding: 0; line-height: 5px;
		    font-size: 14px; color: white; font-family: Trebuchet, Arial, sans-serif; font-weight: bold;
		    box-sizing: border-box;
		}
		.onoffswitch-inner:before {
		    content: "";
		    padding-left: 10px;
		    background-color: #337AB7; color: #FFFFFF;
		}
		
		.disabled .onoffswitch-inner:before {
			background-color: #ccc;
		}
		
		.onoffswitch-inner:after {
		    content: "";
		    padding-right: 10px;
		    background-color: #fff; color: #999999;
		    text-align: right;
		}
		
		.disabled .onoffswitch-inner:after {
			background-color: #ccc;
		}
		
		.onoffswitch-switch {
		    display: block; width: 15px; margin: -5px;
		    background: #FFFFFF;
		    position: absolute; top: 0; bottom: 0;
		    right: 31px;
		    border: 2px solid #999999; border-radius: 20px;
		    transition: all 0.3s ease-in 0s; 
		}
		.onoffswitch-checkbox:checked + .onoffswitch-label .onoffswitch-inner {
		    margin-left: 0;
		}
		.onoffswitch-checkbox:checked + .onoffswitch-label .onoffswitch-switch {
		    right: 0px; 
		}
		
		.glyphicon-info-sign {
			color: #aaa;
		}
		
		.table-bordered {
			background-color: #e6e6e6;
		}
		
		.actions {
		    position: fixed;
		    width: 100%;
		    height: 52px;
		    max-height: 52px;
		    overflow: visible;
		    top: 112px;
		    left: 0px;
		    background-color: #fff;
		    padding: 4px;
		    text-align: center;
		    z-index: 999;
		    font-size: 90%;
		}
		
		.navbar-default {
			max-width: 900px;
			margin-left: auto;
			margin-right: auto;
		}
	
		.navbar {
			margin-top: 10px;
			margin-bottom: 10px;
			min-height: 30px;
		}
		
		.nav-tabs {
		    border-bottom: 0px solid #ddd;
		}
	
		.navbar-default, .navbar-default .nav-tabs {
			padding-left: 0px;
			padding-right: 0px;
		}
		
		.nav > li > a {
			padding-left: 10px;
			padding-right: 10px;
		}
		
		.navbar-default .nav-tabs > li.active > a, .navbar-default .nav-tabs > li.active > a:hover, .navbar-default .nav-tabs > li.active > a:focus {
			background-color: #e7e7e7 !important;
			color: #555;
			border-radius: 0px !important; 
			margin-right: 0px;
		}
		
		.navbar-default .nav-tabs > li > a, .navbar-default .nav-tabs > li > a:focus {
			border-color: transparent !important;
			border-bottom-left-radius: 4px;
 			border-bottom-right-radius: 4px;
			padding-top: 9px;
			color: #777;
			background-color: transparent !important;
		}
		
		.navbar-default .nav-tabs > li > a:hover {
			color: #222;	
		}
		
		.preview {
			background-color: #ddd;
			border-color: #ccc;
			padding: 8px;
			border-radius: 5px;
			text-align: left;
		}
				
		.glyphicon-info-sign {
			margin-left: 5px;
		}
		
		.subelement td:first-child {
			padding-left: 20px !important;
		}
		
		.subsubelement td:first-child {
			padding-left: 40px !important;
		}
		
		.subelement p {
			margin: 0;
		}
		
		.subelement.noborder td, .subsubelement.noborder td {
			border: 0px solid #f5f4f4 !important;
			border-top: 1px solid #f5f4f4 !important;
		}
		
		.nobottomborder td {
			border: 0px solid #f5f4f4 !important;
			border-bottom: 1px solid #f5f4f4 !important;
		}
		
		#logo-cell img {
			max-width: 300px;
		}
		
		#propertiespage label {
			font-weight: bold !important;
		}
		
	</style>

