<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

	<script type="text/javascript"> 	
		var offset = 300;
		var surveyShortname = '${form.survey.shortname}';
		var surveyUniqueId = '${form.survey.uniqueId}';
				
		function PropertiesViewModel()
		{
			this.self = this;
			
			var contact = "${form.survey.contact}";
			if (contact.indexOf("form:") > -1)
			{
				this.contactType = ko.observable("form");
			} else if (contact.indexOf("@") > -1)
			{
				this.contactType = ko.observable("email");
			} else {
				this.contactType = ko.observable("url");
			}
			
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
			this.useMotivationTime = ko.observable(${form.survey.motivationType});
			this.useEscapeLink =  ko.observable(${form.survey.escapePageLink});
			this.quiz = ko.observable(${form.survey.isQuiz});
			this.delphi = ko.observable(${form.survey.isDelphi});
			this.opc = ko.observable(${form.survey.isOPC});
			this.multiPaging = ko.observable(${form.survey.multiPaging});
			this.isUseMaxNumberContribution = ko.observable(${form.survey.isUseMaxNumberContribution});
			this.isUseMaxNumberContributionLink = ko.observable(${form.survey.isUseMaxNumberContributionLink});
			this.sendConfirmationEmail = ko.observable(${form.survey.sendConfirmationEmail});
			this.changeContribution = ko.observable(${form.survey.changeContribution});
			this.downloadContribution = ko.observable(${form.survey.downloadContribution});
			this.saveAsDraft = ko.observable(${form.survey.saveAsDraft});
			this.timeLimit = ko.observable("${form.survey.timeLimit}");
			this.showCountdown = ko.observable(${form.survey.showCountdown});
			this.preventGoingBack = ko.observable(${form.survey.preventGoingBack});
			this.progressBar = ko.observable(${form.survey.progressBar});
			this.motivationPopup = ko.observable(${form.survey.motivationPopup});
			this.progressDisplay = ko.observable(${form.survey.progressDisplay});
			
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

			this.toggleChangeContribution = function()
			{
				if (this.self.delphi()) {
					this.self.changeContribution(true); // should always be activated for delphi surveys
				} else if (this.timeLimit().length > 0 || this.self.preventGoingBack()) {
					this.self.changeContribution(false); // should always be deactivated if a timelimit is set or preventGoingBack is set
				} else {
					this.self.changeContribution(!this.self.changeContribution());
				}
			}
			
			this.toggleSaveAsDraft = function()
			{
				if (this.self.delphi() || this.timeLimit().length > 0 || this.self.preventGoingBack()) {
					this.self.saveAsDraft(false); // should always be deactivated for delphi surveys or if a timelimit is set or if preventGoingBack is set
				} else {
					this.self.saveAsDraft(!this.self.saveAsDraft());
				}
			}
			
			this.toggleDownloadContribution = function()
			{
				if (this.self.delphi()) {
					this.self.downloadContribution(false); // should always be deactivated for delphi surveys
				} else {
					this.self.downloadContribution(!this.self.downloadContribution());
				}
			}
			
			this.toggleMultiPaging = function()
			{
				this.self.multiPaging(!this.self.multiPaging());
				
				if (!this.self.multiPaging()) {
					this.self.preventGoingBack(false);
				}
			}
			
			this.togglePreventGoingBack = function()
			{
				if (this.self.delphi()) {
					this.self.preventGoingBack(false); // should always be deactivated for delphi surveys
				} else {
					this.self.preventGoingBack(!this.self.preventGoingBack());
					if (this.self.preventGoingBack()) {
						this.self.changeContribution(false);
						this.self.saveAsDraft(false);
					}
				}
			}
			
			this.toggleProgressBar = function()
			{
				this.self.progressBar(!this.self.progressBar());
			}

			this.toggleMotivationPopup = function()
			{
				this.self.motivationPopup(!this.self.motivationPopup());
			}
			
			this.toggleQuiz = function()
			{
				this.self.quiz(!this.self.quiz());
			}
			
			this.checkTimeLimit = function(i) 
			{
				this.timeLimit($(i).val());
				
				if (this.timeLimit().length > 0) {
					this.self.saveAsDraft(false);
					this.self.changeContribution(false);
				} else {
					this.self.showCountdown(false);
				}
			}
			
			this.toggleShowCountdown = function()
			{
				if (this.timeLimit().length == 0) {
					this.self.showCountdown(false); // should always be deactivated if no timelimit is set
				} else {
					this.self.showCountdown(!this.self.showCountdown());
				}
			}
			
			this.toggleShowCountdown = function()
			{
				if (this.timeLimit().length == 0) {
					return;
				}
				this.self.showCountdown(!this.self.showCountdown());
			}
			
			this.toggleDelphi = function()
			{
				if (this.self.delphi()) { // switch to standard survey
					this.self.delphi(false);
				} else { // switch to delphi survey
					this.self.delphi(true);
					this.self.changeContribution(true); // should always be activated for delphi surveys
					this.self.preventGoingBack(false);
				}
			}
			
			this.isNormalSurvey = function()
			{
				if (this.self.quiz()) return false;
				if (this.self.delphi()) return false;
				if (this.self.opc()) return false;
				return true;
			}
		}
		
		var _properties = new PropertiesViewModel();
		
		function checkPropertiesAndSubmit(regformconfirmed, publishingconfirmed)
		{
			try {				
				$("#propertiespage").find(".validation-error").remove();
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

				if(_properties.isUseMaxNumberContribution()){
					if (_properties.isUseMaxNumberContributionLink()){
						if(isPropEmpty("[name='survey.maxNumberContributionLink']")){
							$("#useMaxContributionLink").append("<div class='validation-error'>" + requiredText + "</div>")
							return
						}
						if(isURLNotValid("[name='survey.maxNumberContributionLink']")) {
							$("#useMaxContributionLink").append("<div class='validation-error'>" +invalidURL + "</div>")
							return
						}
					} else {
						if(isPropEmpty("[name='survey.maxNumberContributionText']")){
							$("#tinymcelimit").append("<div class='validation-error'>" + requiredText + "</div>")
							return
						}
					}
				}

				if(_properties.motivationPopup()){
					if(isPropEmpty("[name='survey.motivationText']")){
						$("#tinymcemotivationpopup").append("<div class='validation-error'>" + requiredText + "</div>")
						return
					}

					var motivation = $("#edit-survey-motivation-popup").text().length;
					if (motivation > 255)
					{
						$("#tinymcemotivationpopup").append("<div class='validation-error'>" + texttoolongText + "</div>")
						return;
					}
				}

				if (_properties.useConfLink()){
					if (isPropEmpty("[name='survey.confirmationLink']")){
						$("#confLink").append("<div class='validation-error'>" + requiredText + "</div>")
						return
					}
					if(isURLNotValid("[name='survey.confirmationLink']")) {
						$("#confLink").append("<div class='validation-error'>" +invalidURL + "</div>")
						return
					}
				} else {
					let confirmationText = $("#edit-survey-confirmation-page").text();
					if (confirmationText.length > 3000)
					{
						$("#tinymceconfpage").append("<div class='validation-error'>" + texttoolongText + "</div>")
						return;
					}

					if(!isNumberOpenClosedBracketsEqual(confirmationText)){
						$("#tinymceconfpage").append("<div class='validation-error'>" + bracketCountNotMatching + "</div>")
						return
					}

					const confirmationCheckResult = isConfirmationPageMarkupValid("[name='survey.confirmationPage']");
					if(confirmationCheckResult != '') {
						$("#tinymceconfpage").append("<div class='validation-error'>" + confirmationMarkupError.replace('{0}', confirmationCheckResult) + "</div>")
						return
					};
				}

				if (_properties.useEscapeLink()){
					if (isPropEmpty("[name='survey.escapeLink']")){
						$("#escapeLink").append("<div class='validation-error'>" + requiredText + "</div>")
						return
					}
					if(isURLNotValid("[name='survey.escapeLink']")) {
						$("#escapeLink").append("<div class='validation-error'>" +invalidURL + "</div>")
						return
					}
				} else {
					if (isPropEmpty("[name='survey.escapePage']")){
						$("#tinymceescapepage").append("<div class='validation-error'>" + requiredText + "</div>")
						return
					}
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
				if ($("#survey-contact-type").val() == "url") {
					$("#survey\\.contact").removeClass("email").addClass("url");
					result = validateInput($("#survey\\.contact").parent());
					
					if (result == false)
					{
						return;
					}
				} else {
					$("#survey\\.contact").removeClass("url").addClass("email");
					result = validateInput($("#survey\\.contact").parent());
					
					if (result == false)
					{
						return;
					}
				}				
				
				var result = validateInput($('#save-form'));
				
				if (result == false)
				{
					return;
				}

				result = validateInput($("#maxContributionInput").parent());
				
				if (result == false)
				{
					return;
				}
				
				result = validateInput($("#minNumberDelphiStatistics").parent());
				
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
				
				if ($('#survey\\.timeLimit').val().length > 0) {
					var v = $('#survey\\.timeLimit').val().replaceAll("0", "").replaceAll(":", "");
					if (v.length == 0) {
						$("#survey\\.timeLimit").after("<div class='validation-error'>" + timeLimitNotZero + "</div>");
						return;
					}
				}
				
			} catch (e)	{

			}
			
			if ($("#survey-contact-type").val() == "form")
			{
				$("#survey\\.contact").val("form:" + $("#survey\\.contact").val());
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

		function isPropEmpty(select){
			let elem = $(select)
			return elem.text().trim().length <= 0 && elem.val().trim().length <= 0
		}

		function isConfirmationPageMarkupValid(select){
			// get all strings inside curly brackets
			let markupRegex = /{([^}]+)}/g;

			let matchMarkupRegex = $(select).text().match(markupRegex);
			if (matchMarkupRegex === null) {
				return '';
			}

			const markups = [...matchMarkupRegex];
			if(markups.length <= 0)
				return '';

			const validIDs = "${form.survey.getValidMarkupIDs()}";

			// check if all markups are valid
			for(const mark of markups){
				let markNoBrackets = mark.slice(1,-1);
				switch(markNoBrackets) {
					case 'InvitationNumber':
					case 'ContributionID':
					case 'UserName':
					case 'CreationDate':
					case 'LastUpdate':
					case 'Language':
						break;
					default:
						// is element in question Ids?
						if(validIDs.includes(markNoBrackets))
							break;

						// invalid markup
						return mark;
				}
			}

			return '';	// all markup valid
		}

		function isURLNotValid(select){
			let value = $(select).val();
			var urlregex = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
			return !urlregex.test( value  );
		}

		function isNumberOpenClosedBracketsEqual(text){
			let openBrackets = 0;
			let closedBrackets = 0;
			for(let i = 0; i < text.length; i++){
				let c = text.charAt(i);
				if(c === '{')
					openBrackets++;
				if(c === '}')
					closedBrackets++;
			}
			return openBrackets == closedBrackets;
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
			border: 0px !important;			
		}
		
		.nobottomborder td {
			border: 0px !important;
		}
		
		#logo-cell img {
			max-width: 300px;
		}
		
		#propertiespage label {
			font-weight: bold !important;
		}
		
		.propertiesbox .anchor {
			display: block;
		    position: relative;
		    top: 120px;
		    visibility: hidden;
		}
		
	</style>

