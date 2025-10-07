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
			this.eVote = ko.observable(${form.survey.isEVote});
			this.opc = ko.observable(${form.survey.isOPC});
			this.selfAssessment = ko.observable(${form.survey.isSelfAssessment});
			this.multiPaging = ko.observable(${form.survey.multiPaging});
			this.isUseMaxNumberContribution = ko.observable(${form.survey.isUseMaxNumberContribution});
			this.isUseMaxNumberContributionLink = ko.observable(${form.survey.isUseMaxNumberContributionLink});
			this.sendConfirmationEmail = ko.observable(${form.survey.sendConfirmationEmail});
			this.sendReportEmail = ko.observable(${form.survey.sendReportEmail});
			this.reportEmailFrequency = ko.observable("${form.survey.reportEmailFrequency}");
			this.reportEmails = ko.observable("${form.survey.reportEmails}");
			this.changeContribution = ko.observable(${form.survey.changeContribution});
			this.downloadContribution = ko.observable(${form.survey.downloadContribution});
			this.saveAsDraft = ko.observable(${form.survey.saveAsDraft});
			this.timeLimit = ko.observable("${form.survey.timeLimit}");
			this.showCountdown = ko.observable(${form.survey.showCountdown});
			this.preventGoingBack = ko.observable(${form.survey.preventGoingBack});
			this.progressBar = ko.observable(${form.survey.progressBar});
			this.motivationPopup = ko.observable(${form.survey.motivationPopup});
			this.progressDisplay = ko.observable(${form.survey.progressDisplay});
			this.tags = ko.observableArray(${form.survey.tagsAsArray()});
			this.doNotDelete = ko.observable(${form.survey.doNotDelete});
			this.tagsLoading = ko.observable(false);
			
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
				} else if (this.timeLimit().length > 0 || this.self.preventGoingBack() || this.self.eVote()) {
					this.self.changeContribution(false); // should always be deactivated if a timelimit is set or preventGoingBack is set
				} else {
					this.self.changeContribution(!this.self.changeContribution());
				}
			}
			
			this.toggleSaveAsDraft = function()
			{
				if (this.self.delphi() || this.self.eVote() || this.timeLimit().length > 0 || this.self.preventGoingBack()) {
					this.self.saveAsDraft(false); // should always be deactivated for delphi surveys or if a timelimit is set or if preventGoingBack is set
				} else {
					this.self.saveAsDraft(!this.self.saveAsDraft());
				}
			}
			
			this.toggleDownloadContribution = function()
			{
				if (this.self.delphi() || this.self.eVote()) {
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
				if (this.self.eVote()){
					this.self.progressBar(false);
				} else {
					this.self.progressBar(!this.self.progressBar());
				}
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

			this.toggleEVote = function() {
				this.self.eVote(!this.self.eVote());
				if (this.self.eVote()){
					this.self.changeContribution(false)
					this.self.isUseMaxNumberContribution(false)
					this.self.saveAsDraft(false)
					this.self.secured(true)
					this.self.downloadContribution(false)
					$("input[name='radio-new-survey-privacy']").prop('checked', true)
					$("input[name='survey.allowedContributionsPerUser']").val("1")
					$("input[name='survey.dedicatedResultPrivileges']").prop('checked', false);
					this.self.progressBar(false)
					this.self.ecasSecurity(true)
					$("input[name='survey.ecasMode'][value='all']").prop("checked", true)
					$("input[name='survey.password']").val("")
					$("#clearpassword").val("")
				}

			}
			
			this.isNormalSurvey = function(){
				let s = this.self;
				return !s.quiz() && !s.delphi() && !s.opc() && !s.eVote();
			}

			this.addReportMails = function()
			{
				$("#report-duplicate-mails").hide();
				$("#report-invalid-mails").hide();
				$("#report-too-many-mails").hide();
				$("#report-no-mails").hide();

				let mail = $("#reportRecipientList")[0].value;
				let previousMails = this.reportEmails();

				//check input if mails are valid
				let invalid_mail = false;
				mail.split(";").forEach((mail_address) => {
					if (mail_address.trim() && !validateEmail(mail_address.trim())) {
						invalid_mail = true;
					}
				});
				if (invalid_mail) {
					$("#report-invalid-mails").show();
					return;
				}

				if ((this.reportEmails() != "" ? this.reportEmails().split(";").length : 0) + mail.split(";").map((element) => element.trim()).filter((element) => element).length <= 10) {
					this.reportEmails(this.reportEmails().concat(";" + mail));

					//filter out empty strings
					let nonEmptyReportEmails = this.reportEmails().split(";").map((element) => element.trim()).filter((element) => element);

					//look for duplicates
					let nonDuplicateReportEmails = [...new Set(nonEmptyReportEmails)].join(";");
					if (nonEmptyReportEmails.join(";") != nonDuplicateReportEmails) {
						$("#report-duplicate-mails").show();
						this.reportEmails(previousMails);
						return;
					}

					this.reportEmails(nonDuplicateReportEmails);
					$("#reportRecipientList")[0].value = "";
				} else {
					$("#report-too-many-mails").show();
				}
			}

			this.deleteReportMail = function (mail) {
				if (this.reportEmails().includes(mail)) {
					//filter out empty strings
					let nonEmptyReportEmails = this.reportEmails().replace(mail, "").split(";").map((element) => element.trim()).filter((element) => element).join(";");
					this.reportEmails(nonEmptyReportEmails)
				}
			}

            this.addTag = function(tag) {
                if (tag.indexOf(" ") > -1) {
                    tag = tag.substring(0, tag.indexOf(" ")); // remove " (new tag)"
                };

                // no duplicates
                if (this.tags.indexOf(tag) > -1) {
                    return;
                }

                this.tags.push(tag);
            }

            this.removeTag = function(icon) {
                const tag = $(icon).closest('.badge').attr("tag");
                this.tags.remove(function(item) {
                    return item == tag;
                });
            }

            this.sortedTags = function() {
                return this.tags().sort(function (a, b) {
                                            return a.toLowerCase().localeCompare(b.toLowerCase());
                                        });
            }
		}

		var _properties = new PropertiesViewModel();

		function checkProperties(regformconfirmed, publishingconfirmed) {
			try {
				$("#propertiespage").find(".validation-error").remove();
				$("#survey-validator-invalid").hide();
				var invalid = false;

				if (!checkShortname($('#edit-survey-shortname').val(), '${form.survey.id}'))
				{
					$('#edit-survey-shortname').parent().append("<div class='validation-error'>" + shortnameAlreadyExists + "</div>");
					return false;
				}
				var value = $("#edit-survey-shortname").val();
				var reg = /^[a-zA-Z0-9-_]+$/;
				if ($("#edit-survey-shortname").parent().find(".validation-error").length == 0)
				{
					if( !reg.test( value ) ) {
						$("#edit-survey-shortname").after("<div class='validation-error'>" + shortnameText3 + "</div>");
						return false;
					} else if( value.indexOf("__") > -1 ) {
						$("#edit-survey-shortname").after("<div class='validation-error'>" + shortnameText2 + "</div>");
						return false;
					} ;
				}

				var title = $("#edit-survey-title").text().length;
				if (title > 2000)
				{
					$("#edit-survey-title").parent().append("<div class='validation-error'>" + texttoolongText + "</div>");
					return false;
				}

				if ($('#survey-validator').is(":visible")) {
					if ($("#survey-validator").val().length == 0) {
						$("#survey-validator").parent().append("<div class='validation-error'>" + requiredText + "</div>");
						return false;
					}
					
					if (!checkOrganisation($("#survey-validator").val(), $("#survey-organisation").val()))
					{
						$("#survey-validator-invalid").show();
						return false;
					}
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
						return false;
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
						return false;
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
						return false;
					};
					<c:if test="${!form.survey.isActive}">
					var now = new Date();
					var autoPublishEnabled = ($("#autopub").is(":checked"));
					if(autoPublishEnabled && startdate < now && enddate > now)
					{
						$('#publishConfirmationDialog').modal('show');
						return false;
					};

					<c:if test="${!form.survey.automaticPublishing}">
					if(autoPublishEnabled && startdate > now)
					{
						$('#publishConfirmationDialog3').modal('show');
						return false;
					};
					</c:if>
					</c:if>
					<c:if test="${form.survey.isActive}">
					var now = new Date();
					var autoPublishEnabled = ($("#autopub").is(":checked"));
					if(autoPublishEnabled && startdate > now)
					{
						$('#publishConfirmationDialog2').modal('show');
						return false;
					};

					if(autoPublishEnabled && enddate < now)
					{
						$('#edit-properties-dialog').modal('hide');
						$('#publishConfirmationDialog4').modal('show');
						return false;
					};

					</c:if>
				};

				if ($('#notificationselector1').is(":checked") && !$("#autopub").is(":checked"))
				{
					$('#notificationselector1').parent().append("<div class='validation-error'>" + endNotificationAutomatedPublishing + "</div>");
					return false;
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
								return false;
							}
						}
						labels[labels.length] = label;
					}
				});

				if (!$('#survey-validator').hasClass("required")) {
					$('#survey-validator').val("");
				}

				if (invalid) return false;
				if ($("#survey-contact-type").val() == "url") {
					$("#survey\\.contact").removeClass("email").addClass("url");
					result = validateInput($("#survey\\.contact").parent());

					if (result == false)
					{
						return false;
					}
				} else {
					$("#survey\\.contact").removeClass("url").addClass("email");
					result = validateInput($("#survey\\.contact").parent());

					if (result == false)
					{
						return false;
					}
				}

				var result = validateInput($('#save-form'));

				if (result == false)
				{
					return false;
				}

				result = validateInput($("#maxContributionInput").parent());

				if (result == false)
				{
					return false;
				}

				result = validateInput($("#minNumberDelphiStatistics").parent());

				if (result == false)
				{
					return false;
				}

				<c:if test="${!validregform}">
				if ($("#survey\\.registrationForm").is(":checked") && !regformconfirmed)
				{
					$("#confirmregformdialog").modal("show");
					return false;
				}
				</c:if>


				if ($("#survey\\.publication\\.allContributions1").is(":checked"))
				{
					var counter = $("#contributionsToPublishDiv").find("input[name^='contribution']:checked").length;
					if (counter > 3)
					{
						$("#contributionsToPublishDiv").before("<div class='validation-error'>" + atmost3Selections + "</div>");
						return false;
					}
				}

				<c:if test="${!form.survey.publication.showContent && !form.survey.publication.showCharts && !form.survey.publication.showStatistics}">
				if (!publishingconfirmed && ($("#showContent").is(":checked") || $("#showCharts").is(":checked") || $("#showStatistics").is(":checked")))
				{
					$("#confirmpublicationdialog").modal("show");
					return false;
				}
				</c:if>

				if ($('#survey\\.timeLimit').val().length > 0) {
					var v = $('#survey\\.timeLimit').val().replaceAll("0", "").replaceAll(":", "");
					if (v.length == 0) {
						$("#survey\\.timeLimit").after("<div class='validation-error'>" + timeLimitNotZero + "</div>");
						return false;
					}
				}

				if($("[name='survey\\.webhook']").val().length > 0 && isURLNotValid("[name='survey\\.webhook']")) {
					$("#edit-survey-webhook").after("<div class='validation-error'>" +invalidURL + "</div>");
					return false;
				}

				if (_properties.sendReportEmail() && _properties.reportEmails().length == 0){
				    $("#report-no-mails").show();
				    return false;
                } else {
                    $("#report-no-mails").hide();
                }
				
			} catch (e)	{

			}
			return true;
		}
		
		function checkPropertiesAndSubmit(regformconfirmed, publishingconfirmed)
		{
			if (!checkProperties(regformconfirmed, publishingconfirmed)) {
				return
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

			const validIDs_string = "${form.survey.getValidMarkupIDs()}";
			const validIDs = validIDs_string.slice(1, validIDs_string.length - 1).split(', ');

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
						if(validIDs.indexOf(markNoBrackets) != -1)
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

        function openReminderDialog() {
            if (!checkOrganisation($("#survey-validator").val(), $("#survey-organisation").val()))
            {
                $("#survey-validator-invalid").show();
                return;
            }

            $("#survey-validator-invalid").hide();
            $('#sendReminderDialog').modal('show');
        }

        var message_SuccessMailValidationReminder = "<spring:message code='message.mail.successMailValidationReminder' />";
        var message_FailedMailValidationReminder = "<spring:message code='message.mail.failMailLinkDraft' />";
        function sendReminder(surveyId) {
            $.ajax({
                type:'GET',
                url: "${contextpath}/${sessioninfo.shortname}/management/sendOrganisationValidationReminder",
                data: {surveyId : surveyId},
                cache: false,
                success: function( data ) {
                    if (data == "success") {
                        $('#sendReminderDialog').modal('hide');
                        showSuccess(message_SuccessMailValidationReminder);
                    } else {
                        showError(message_FailedMailValidationReminder);
                    }
                }
            });
        }

		var noMailsFound = '<spring:message code="label.NoMailsFound" />';
		var atLeastOneMail = '<spring:message code="label.AtLeastOneMail" />';
		var noEmptySearch = '<spring:message code="label.NoEmptySearch" />';
		var invalidEmail = '<spring:message code="label.InvalidEmail" />';
		var notFoundEmail = '<spring:message code="label.NotFoundEmail" />';
		let changeRequestJustSent = '<spring:message code="message.OwnerChangeRequestJustSent" />';
		function showChangeOwnerDialog() {
			resetEmailFeedback();

			// select european commision if exists
			var exists = false;
			$('#change-owner-type-ecas option').each(function(){
				if (this.value == "eu.europa.ec") {
					exists = true;
					return false;
				}
			});
			if (exists)
			{
				$("#change-owner-type-ecas").val("eu.europa.ec");
			}

			checkUserType();
			$('#change-department-name').val('');
			$('#change-owner-name').val('');
			$('#change-first-name').val('');
			$('#change-last-name').val('');
			$('#change-owner-email').val('');
			$("#search-results-more").hide();
			$("#btnOkChangeOwnerFromAccess").attr("disabled", true);
			$('#change-owner-dialog').modal();
		}

		function searchEmailUser(order) {
			let mailInput = $("#change-owner-email").val();
			if (mailInput.length <= 0) {
				$("#invalidEmailsIcon").show();
				$("#invalidEmailsText").text(atLeastOneMail);
				return;
			}

			if (!validateEmail(mailInput.trim()) && mailInput != "") {
				setEmailCheckFeedback(0, 1, 0);
				return;
			}

			$.ajax({
				type:'GET',
				url: contextpath + "/logins/usersEmailJSON",
				data: {emails: $("#change-owner-email").val()},
				dataType: 'json',
				cache: false,
				success: function( foundMails ) {
					if (foundMails.length > 0) {
						setEmailCheckFeedback(foundMails.length, 0, 0);
					} else {
						setEmailCheckFeedback(0, 0, 1);
					}
					$("#btnOkChangeOwnerFromAccess").removeAttr("disabled");
				}, error: function(e) {
					
				}});
		}

		function resetEmailFeedback() {
			$("#foundEmailUsers").text("");
			$("#invalidEmailsText").text("");
			$("#invalidEmailsIcon").hide();
			$("#notFoundEmailsIcon").hide();
			$("#notFoundEmailsText").text("");
		}

		function setEmailCheckFeedback(foundCount, invalidMails, notFoundMails) {
			resetEmailFeedback();

			$("#foundEmailUsers").text(noMailsFound.replace("{0}", foundCount));

			if(invalidMails > 0) {
				$("#invalidEmailsIcon").show();
				$("#invalidEmailsText").html(invalidEmail);
			}

			if(notFoundMails > 0) {
				$("#notFoundEmailsIcon").show();
				$("#notFoundEmailsText").html(notFoundEmail);
			}
		}

		function checkUserType()
		{
			$("#noEmptySearchIcon").hide();
			$("#noEmptySearchText").text('');

			$("#search-results").find("tbody").empty();

			if ($("#change-owner-type-ecas").val() != "system" && $("#change-owner-type-ecas").val() != "external")
			{
				$("#change-owner-department-div").show();
				$("#change-owner-firstname-div").show();
				$("#change-owner-lastname-div").show();
				$("#eulogin-span").show();
			} else if ($("#change-owner-type-ecas").val() == "external")
			{
				$("#change-owner-department-div").hide();
				$("#change-owner-firstname-div").show();
				$("#change-owner-lastname-div").show();
				$("#eulogin-span").show();
			} else {
				$("#change-owner-department-div").hide();
				$("#change-owner-firstname-div").hide();
				$("#change-owner-lastname-div").hide();
				$("#eulogin-span").hide();
			}
		}

		function searchUser(order)
		{
			$("#btnOkChangeOwnerFromAccess").attr("disabled", true);
			$("#noEmptySearchIcon").hide();
			$("#noEmptySearchText").text("");

			var name = $("#change-owner-name").val();
			var first = $("#change-first-name").val();
			var last = $("#change-last-name").val();
			var email = $("#change-owner-email").val();
			var department = $("#change-department-name").val();
			var type = $("#change-owner-type-ecas").val();

            $('#change-owner-dialog').find(".validation-error").remove();

			if (email != '' && !validateEmail(email)) {
			    addValidationError.afterElementAndFocus($("#change-owner-email"), $("#change-owner-email"), invalidEmail);
			    return;
			}

			if (type != "system" && type != "external")
			{
				//case eu.europa.ec: Admin and form manager EC
				if (!(email != '' || department != '' || first != '' || last != '' || name != '')) {
					$("#noEmptySearchIcon").show();
					$("#noEmptySearchText").text(noEmptySearch);
					return;
				}
			} else if (type == "system")
			{
				//case system
				if (!(email != '' || name != '')) {
					$("#noEmptySearchIcon").show();
					$("#noEmptySearchText").text(noEmptySearch);
					return;
				}
			}

			var s = "name=" + name + "&type=" + type + "&department=" + department+ "&email=" + email + "&first=" + first + "&last=" + last + "&order=" + order;

			$("#change-owner-dialog").modal('hide');
			$("#busydialog").modal('show');

			$("#search-results-more").hide();

			$.ajax({
				type:'GET',
				url: contextpath + "/logins/usersJSON",
				data: s,
				dataType: 'json',
				cache: false,
				success: function( users ) {
					$("#search-results").find("tbody").empty();
					var body = $("#search-results").find("tbody").first();

					for (var i = 0; i < users.length; i++ )
					{
						$(body).append(users[i]);
					}

					var hiddenTableHeaders = $("#search-results th.hideme");
					for (var i = 0; i < hiddenTableHeaders.length; i++ )
					{
						$('#search-results td:nth-child(' + hiddenTableHeaders[i].cellIndex + ')').hide();
					}

					if (type != "system" && users.length >= 100)
					{
						$("#search-results-more").show();
					}

					$(body).find("tr").click(function() {
						$("#search-results").find(".success").removeClass("success");
						$(this).addClass("success");
						$("#btnOkChangeOwnerFromAccess").removeAttr("disabled");
					});

					$("#busydialog").modal('hide');
					$("#change-owner-dialog").modal('show');
				}, error: function() {
					$("#busydialog").modal('hide');
					$("#change-owner-dialog").modal('show');
				}});

			$("#search-results-none").hide();

		}

		$(function() {
			checkUserType();

			$('#change-owner-name').keyup(function(e){
				if(e.keyCode == 13){
					searchUser();
				}
			});

			$('#change-department-name').keyup(function(e){
				if(e.keyCode == 13){
					searchUser();
				}
			});

            <c:if test="${reportingdatabaseused == null}">
              checkNumberOfFilters(true);
            </c:if>
		});

		function changeOwner() {
			if ($("#btnOkChangeOwnerFromAccess").attr("disabled") == "disabled") {
				return;
			}

			if ($("#search-results").find(".success").length == 0)
			{
				$("#search-results-none").show();
				return;
			}
			$("#search-results-none").hide();

			var login = $("#search-results").find(".success").first().attr("id");
			let mail = $("#search-results").find(".success").find("td").first().text();
			var addAsFormManager = $("#add-as-form-manager").is(":checked");
			var s = "change-owner-type=loginAndEcas&change-owner-login=" + login + "&change-owner-email=" + mail + "&add-as-form-manager=" + addAsFormManager;

			if (login == '${form.survey.owner.getLogin()}') {
				showError('<spring:message code="message.ChangeRequestSameUser" />')
				return;
			}

			$.ajax({
				type:'POST',
				beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
				url: "${contextpath}/${sessioninfo.shortname}/management/changeOwner",
				data: s,
				cache: false,
				success: function(response) {
					if (response == "success"){
						$("#change-owner-dialog").modal('hide');
						showSuccess('<spring:message code="message.ChangeRequestSend" />');
						$("#owner-change-section small").remove()
						$("#owner-change-section").append("<small>" + changeRequestJustSent.replace("{0}", mail) + "</small>");
					} else {
						showError('<spring:message code="message.ChangeRequestError" />');
					}
				}, error: function() {
					showError('<spring:message code="message.ChangeRequestError" />');
				}});
		}

		function changeOwnerByEmail() {
			if ($("#btnOkChangeOwnerFromAccess").attr("disabled") == "disabled") {
				return;
			}

			let mail = $("#change-owner-email").val();
			var addAsFormManager = $("#add-as-form-manager").is(":checked");
			var s = "change-owner-type=email&change-owner-mail=" + mail + "&add-as-form-manager=" + addAsFormManager;

			$.ajax({
				type:'POST',
				url: "${contextpath}/${sessioninfo.shortname}/management/changeOwner",
				data: s,
				beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
				cache: false,
				success: function(response) {
					if (response == "success"){
						$("#change-owner-dialog").modal('hide');
						showSuccess('<spring:message code="message.ChangeRequestSend" />');
						$("#owner-change-section small").remove()
						$("#owner-change-section").append("<small>" + changeRequestJustSent.replace("{0}", mail) + "</small>");
					} else {
						showError('<spring:message code="message.ChangeRequestError" />');
					}
				}, error: function() {
					showError('<spring:message code="message.ChangeRequestError" />');
				}});
		}

		function checkNumberOfFilters(reportingDBDisabled) {
            if (!reportingDBDisabled) return;

            let counter = 0;

            $('#contributionsToPublishDiv').find(".filter").each(function(){
                let valfound = false;

                $(this).find("input[type=checkbox]").each(function(){
                    if ($(this).is(":checked")) {
                        valfound = true;
                    }
                });

                if (valfound) {
                    $(this).attr("data-filterset", "true");
                    counter++;
                } else {
                    $(this).attr("data-filterset", "false");
                }
            });

            if (counter > 2) {
                $('#contributionsToPublishDiv').find(".filter").each(function(){
                    if ($(this).attr("data-filterset") == "false") {
                        $(this).find("input").attr("disabled", "disabled").addClass("disabled");
                    }
                });
            } else {
                $('#contributionsToPublishDiv').find("input.disabled").each(function(){
                    $(this).removeAttr("disabled").removeClass("disabled");
                });
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

        .ui-menu-item {
          font-size: 13px !important;
        }

        .ui-state-hover,
        .ui-state-active,
        .ui-state-focus {
          text-decoration: none !important;
          color: #fff !important;
          background-image: none !important;
          background-color: #767676 !important;
          border-color: #555 !important;
          font-weight: normal !important;
        }

        #selectedtags {
            text-align: right;
            max-width: 670px;
        }

        #selectedtags .badge {
            margin-left: 7px;
            margin-bottom: 10px;
            cursor: pointer;
            background-color: #337ab7;
            color: #fff;
        }

        #change-owner-dialog label {
            margin-bottom: 0;
            margin-top: 10px;
        }


	</style>

