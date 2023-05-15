<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<meta charset="utf-8"></meta>
<meta http-equiv="X-UA-Compatible" content="IE=edge"></meta>
<c:choose>
	<c:when test="${ismobile != null}">
		<meta name="viewport" content="width=device-width, initial-scale = 1.0, maximum-scale=1.0, user-scalable=no" />
	</c:when>
	<c:otherwise>
		<meta name="viewport" content="width=device-width, initial-scale=1"></meta>
	</c:otherwise>
</c:choose>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
<meta http-equiv="Pragma" content="no-cache, no-store"></meta>
<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate"></meta>
<meta http-equiv="Expires" content="-1"></meta>
<meta name="_csrf" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<meta name="Description" content="EUSurvey is an online survey-management system built for the creation and publishing of globally accessible forms, such as user satisfaction surveys and public consultations." />

<c:if test="${allowIndex == null}">
	<meta name="robots" content="noindex"></meta>
</c:if>

<link href="${contextpath}/resources/css/jquery-ui.css?version=<%@include file="version.txt" %>" rel="stylesheet" type="text/css"></link>
<link href="${contextpath}/resources/css/jquery-ui.structure.min.css?version=<%@include file="version.txt" %>" rel="stylesheet" type="text/css"></link>
<link href="${contextpath}/resources/css/jquery-ui.theme.min.css?version=<%@include file="version.txt" %>" rel="stylesheet" type="text/css"></link>
<link href="${contextpath}/resources/css/common.css?version=<%@include file="version.txt" %>" rel="stylesheet" type="text/css"></link>
<link href="${contextpath}/resources/css/bootstrap-slider.min.css?version=<%@include file="version.txt" %>" rel="stylesheet" type="text/css"></link>

<link href="${contextpath}/resources/css/Chart.min.css?version=<%@include file="version.txt" %>" rel="stylesheet" type="text/css"></link>

<c:if test="${ismobile != null}">
	<link href="${contextpath}/resources/css/commonmobile.css?version=<%@include file="version.txt" %>" rel="stylesheet" type="text/css"></link>
</c:if>

<c:choose>
	<c:when test="${forpdf == null}">
		<link href="${contextpath}/resources/css/bootstrap.css?version=<%@include file="version.txt" %>" rel="stylesheet" type="text/css"></link>
	</c:when>
	<c:otherwise>
		<style type="text/css">
			.modal, .hide {
				display: none;
			}
		</style>
	</c:otherwise>
</c:choose>

<link href="${contextpath}/resources/css/common-extension.css?version=<%@include file="version.txt" %>" rel="stylesheet" type="text/css"></link>

<style type="text/css">
/* 	html { overflow: hidden; } */
	#participantsstatic thead { background-color: #fff; }
	
	@font-face {
	    font-family: 'steinerregular';
	    src: url('${contextpath}/resources/fonts/steinerlight-webfont.eot');
	    src: url('${contextpath}/resources/fonts/steinerlight-webfont.eot?#iefix') format('embedded-opentype'),
	         url('${contextpath}/resources/fonts/steinerlight-webfont.woff') format('woff'),
	         url('${contextpath}/resources/fonts/steinerlight-webfont.ttf') format('truetype'),
	         url('${contextpath}/resources/fonts/steinerlight-webfont.svg#steinerregular') format('svg');
	    font-weight: normal;
	    font-style: normal;
	}
	
</style>

<meta itemprop="image" content="${contextpath}/resources/images/favicon5.ico" />
<link rel="shortcut icon" href="${contextpath}/resources/images/favicon5.ico" type="image/x-icon"></link>

<script type='text/javascript' src='${contextpath}/resources/js/knockout-3.5.1.js?version=<%@include file="version.txt" %>'></script>
<script type="text/javascript" src="${contextpath}/resources/js/jquery-1.12.3.min.js?version=<%@include file="version.txt" %>"></script>
<script type="text/javascript" src="${contextpath}/resources/js/jquery-ui.min.js?version=<%@include file="version.txt" %>"></script>
<script type="text/javascript" src="${contextpath}/resources/js/jquery.ui.touch-punch.min.js?version=<%@include file="version.txt" %>"></script>
<script type="text/javascript" src="${contextpath}/resources/js/spin.min.js?version=<%@include file="version.txt" %>"></script>
<script type="text/javascript" src="${contextpath}/resources/js/jquery.hotkeys.js?version=<%@include file="version.txt" %>"></script>
<script type="text/javascript" src="${contextpath}/resources/js/bootstrap.js?version=<%@include file="version.txt" %>"></script>
<script type="text/javascript" src="${contextpath}/resources/js/jquery.addplaceholder.js?version=<%@include file="version.txt" %>"></script>
<script type="text/javascript" src="${contextpath}/resources/js/jquery.validate.js?version=<%@include file="version.txt" %>"></script>
<script type="text/javascript" src="${contextpath}/resources/js/scroll-sneak.js?version=<%@include file="version.txt" %>"></script>
<script type="text/javascript" src="${contextpath}/resources/js/system.js?version=<%@include file="version.txt" %>"></script>
<c:if test="${enablecookieconsentkit == 'true'}">
	<script defer="defer" src="https://europa.eu/webtools/load.js" type="text/javascript"></script>
	<script type="application/json">{
  "utility": "cck"
}</script>
</c:if>
<script type="text/javascript" src="${contextpath}/resources/js/bootstrap-slider.min.js?version=<%@include file="version.txt" %>"></script>
<script type="text/javascript" src="${contextpath}/resources/js/tinymce/jquery.tinymce.min.js?version=<%@include file="version.txt" %>"></script>
<script type="text/javascript" src="${contextpath}/resources/js/tinymce/tinymce.min.js?version=<%@include file="version.txt" %>"></script>
<script type="text/javascript" src="${contextpath}/resources/js/Chart.min.js?version=<%@include file="version.txt" %>"></script>
<script type="text/javascript" src="${contextpath}/resources/js/chartjs-plugin-colorschemes.min.js?version=<%@include file="version.txt" %>"></script>
<script type="text/javascript" src="${contextpath}/resources/js/math.js?version=<%@include file="version.txt" %>"></script>
 
<script type="text/javascript">
	//if (top != self) top.location=location;
	
	var contextpath = "${contextpath}";
	var isresponsive = ${responsive != null};
	var isdelphi = ${form != null && form.survey.getIsDelphi()};
	var delphiStartPageUrl = '${pageContext.request.getAttribute("javax.servlet.forward.request_uri")}?${pageContext.request.getQueryString().replace("startDelphi=true&", "").replace("startDelphi=true", "?")}';

	<c:choose>
		<c:when test="${form != null && form.getResources() != null && resultType == null}">
			var unsavedChangesText = "${form.getMessage("message.UnsavedChanges")}";	
			var requiredText = "${form.getMessage("validation.required")}";
			var confirmationMarkupError = "${form.getMessage("validation.confirmationMarkupError")}";
			var nomatchText =  "${form.getMessage("validation.nomatch")}";
			var shortnameText = "${form.getMessage("validation.name2")}";
			var shortnameText2 = "${form.getMessage("validation.shortname2")}";
			var shortnameText3 = "${form.getMessage("validation.shortname3")}";
			var textnotlongenoughText = "${form.getMessage("validation.textNotLongEnough")}";
			var texttoolongText = "${form.getMessage("validation.textTooLong")}";
			var bracketCountNotMatching = "${form.getMessage("validation.numberBracketsNotMatching")}";
			var texttoolong5000Text = "${form.getMessage("validation.textTooLong5000")}";
			var invalidnumberText = "${form.getMessage("validation.invalidNumber")}";
			var invalidCharacter = "${form.getMessage("validation.invalidCharacter")}";
			var valuetoosmall = "${form.getMessage("validation.valueTooSmall")}";
			var valuetoolarge = "${form.getMessage("validation.valueTooLarge")}";
			var timevaluetoosmall = "${form.getMessage("validation.valueTooSmallTime")}";
			var timevaluetoolarge = "${form.getMessage("validation.valueTooLargeTime")}";
			var notenoughanswers = "${form.getMessage("validation.notEnoughAnswers")}";
			var toomanyanswers = "${form.getMessage("validation.tooManyAnswers")}";
			var noRegExmatchText = "${form.getMessage("validation.noRegExMatch")}";
			var invalidDate = "${form.getMessage("validation.invalidDate")}";
			var invalidTime = "${form.getMessage("validation.invalidTime")}";
			var invalidEmail = "${form.getMessage("validation.invalidEmail")}";
			var invalidCaseId = "${form.getMessage("validation.invalidContributionId")}";
			var invalidStartEnd = "${form.getMessage("validation.invalidStartEnd")}";
			var invalidStartEndTime = "${form.getMessage("validation.invalidStartEndTime")}";
			var interdependentText = "${form.getMessage("validation.interdependentText")}";
			var invalidURL = "${form.getMessage("validation.invalidURL")}";
			var invalidPrecisionText = "${form.getMessage("validation.invalidPrecisionNumber")}";
			var invalidXHTML = "${form.getMessage("label.InvalidXHTML")}";
			var serverPrefix='${serverprefix}';//+'runner/';
			var selectFileForUploadRunner = "${form.getMessage("label.SelectFileForUpload")}";
			var selectFilesForUpload = "${form.getMessage("label.SelectFilesForUploadButton")}";
			var uploadASkin = "${form.getMessage("label.uploadASkin")}";
			var globalLanguage = '${requestContext.locale.language}';
			var questionTextLabel = "${form.getMessage("label.QuestionText")}";
			var typeLabel = "${form.getMessage("label.Type")}";
			var ignoreLabel = "${form.getMessage("label.Ignore")}";
			var sectionTitleLabel = "${form.getMessage("label.SectionText")}";
			var alternativeTextLabel = "${form.getMessage("label.AlternativeText")}";
			var modifyElementLabel = "${form.getMessage("label.modifyElement")}";
			var deleteElementLabel = "${form.getMessage("label.deleteElement")}";
			var cutElementLabel = "${form.getMessage("label.cutElement")}";
			var copyElementLabel = "${form.getMessage("label.copyElement")}";
			var weakPasswordText = "${form.getMessage("error.PasswordWeak")}";
			var shortnameAlreadyExists = "${form.getMessage("message.ShortnameAlreadyExists")}";
			var endNotificationAutomatedPublishing = "${form.getMessage("error.endNotificationAutomatedPublishing")}";
			var allValues = "${form.getMessage("label.AllValues")}";
			var checkXHTMLValidityError = "${form.getMessage("error.checkXHTMLValidityError")}";
			var mandatoryLabel = "${form.getMessage("label.Mandatory")}";
			var optionalLabel = "${form.getMessage("label.Optional")}";
			var invalidFileError = "${form.getMessage("error.FileNotValid")}";
			var IndentElementLabel = "${form.getMessage("label.IndentElement")}";
			var LabelExternalImage = "${form.getMessage("warning.ExternalImageLink")}";
			var UnindentElementLabel = "${form.getMessage("label.UnindentElement")}";
			var notenoughrowsanswerederror = "${form.getMessage("validation.invalidRowsMin")}";
			var toomanyrowsanswerederror = "${form.getMessage("validation.invalidRowsMax")}";
			var honeypotError = "${form.getMessage("validation.honeypotError")}";
			var clearLabel = "${form.getMessage("label.ClearValue")}";
			var cancelLabel = "${form.getMessage("label.Cancel")}";
			var showLabel = "${form.getMessage("label.Show")}";
			var atLeast3Characters = "${form.getMessage("label.atLeast3Characters")}";
			var loginExistsError = "${form.getMessage("error.LoginExists")}";
			var message_Export1 = "${form.getMessage("message.Export1")}";
			var label_ExportPage = "${form.getMessage("label.ExportPage")}";
			var message_PublicationExportSuccess = "${form.getMessage("message.PublicationExportSuccess")}";
			var message_PublicationExportSuccess2 = "${form.getMessage("message.PublicationExportSuccess2")}";
			var message_ExportFailed = "${form.getMessage("message.ExportFailed")}";
			var message_PublicationExportFailed = "${form.getMessage("message.PublicationExportFailed")}";
			var atmost3Selections = "${form.getMessage("validation.atmost3Selections")}";
			var varExceptionDuringSave = "${form.getMessage("error.ExceptionDuringSave")}";
			var varwaitfordependencies = "${form.getMessage("info.WaitForDependencies")}";
			var varErrorCheckValidation = "${form.getMessage("error.CheckValidation")}";
			var varErrorCheckValidation2 = "${form.getMessage("error.CheckValidation2")}";
			var labelEditYourContributionLater =  '${form.getMessage("label.EditYourContributionLater")}';
			var labelmore = "${form.getMessage("label.more")}";
			var labelless = "${form.getMessage("label.less")}";
			var labelfrom = "${form.getMessage("label.from")}";
			var labelto = "${form.getMessage("label.To")}";
			var messageuploadnoconnection = "${form.getMessage("message.uploadnoconnection")}";
			var messageuploadwrongextension = "${form.getMessage("message.messageuploadwrongextension")}";
			var labelnewexplanation = "${form.getMessage("label.NewExplanation")}";
			var labeloldexplanation = "${form.getMessage("label.OldExplanation")}";
			var timeLimitNotZero = "${form.getMessage("error.timeLimitNotZero")}";
			var infolabeluploadbutton = "${form.getMessage("info.uploadbutton")}";
			var infoNoData = "${form.getMessage("info.NoData")}";
		</c:when>
		<c:otherwise>
			var unsavedChangesText = "<spring:message code='message.UnsavedChanges' />";	
			var requiredText = "<spring:message code='validation.required' />";
			var confirmationMarkupError = "<spring:message code='validation.confirmationMarkupError' />";
			var nomatchText =  "<spring:message code='validation.nomatch' />";
			var shortnameText = "<spring:message code='validation.name2' />";
			var shortnameText2 = "<spring:message code='validation.shortname2' />";
			var shortnameText3 = "<spring:message code='validation.shortname3' />";
			var textnotlongenoughText = "<spring:message code='validation.textNotLongEnough' />";
			var texttoolongText = "<spring:message code='validation.textTooLong' />";
			var bracketCountNotMatching = "<spring:message code='validation.numberBracketsNotMatching' />";
			var texttoolong5000Text = "<spring:message code='validation.textTooLong5000' />";
			var invalidnumberText = "<spring:message code='validation.invalidNumber' />";
			var valuetoosmall = "<spring:message code='validation.valueTooSmall' />";
			var valuetoolarge = "<spring:message code='validation.valueTooLarge' />";
			var timevaluetoosmall = "<spring:message code='validation.valueTooSmallTime' />";
			var timevaluetoolarge = "<spring:message code='validation.valueTooLargeTime' />";
			var notenoughanswers = "<spring:message code='validation.notEnoughAnswers' />";
			var toomanyanswers = "<spring:message code='validation.tooManyAnswers' />";
			var noRegExmatchText = "<spring:message code='validation.noRegExMatch' />";
			var invalidDate = "<spring:message code='validation.invalidDate' />";
			var invalidTime = "<spring:message code='validation.invalidTime' />";
			var invalidEmail = "<spring:message code='validation.invalidEmail' />";
			var invalidCaseId = "<spring:message code='validation.invalidContributionId' />";
			var invalidStartEnd = "<spring:message code='validation.invalidStartEnd' />";
			var invalidStartEndTime = "<spring:message code='validation.invalidStartEndTime' />";
			var interdependentText = "<spring:message code='validation.interdependentText' />";
			var invalidURL = "<spring:message code='validation.invalidURL' />";
			var invalidPrecisionText = "<spring:message code='validation.invalidPrecisionNumber' />";
			var invalidXHTML = "<spring:message code='label.InvalidXHTML' />";
			var serverPrefix='${serverprefix}';//+'runner/';
			var selectFilesForUpload = "<spring:message code='label.SelectFilesForUploadButton' />";
			var uploadASkin = "<spring:message code='label.uploadASkin' />";
			var globalLanguage = '${requestContext.locale.language}';
			var questionTextLabel = "<spring:message code='label.QuestionText' />";
			var typeLabel = "<spring:message code='label.Type' />";
			var ignoreLabel = "<spring:message code='label.Ignore' />";
			var sectionTitleLabel = "$<spring:message code='label.SectionText' />";
			var alternativeTextLabel = "<spring:message code='label.AlternativeText' />";
			var modifyElementLabel = "<spring:message code='label.modifyElement' />";
			var deleteElementLabel = "<spring:message code='label.deleteElement' />";
			var cutElementLabel = "<spring:message code='label.cutElement' />";
			var copyElementLabel = "<spring:message code='label.copyElement' />";
			var weakPasswordText = "<spring:message code='error.PasswordWeak' />";
			var shortnameAlreadyExists = "<spring:message code='message.ShortnameAlreadyExists' />";
			var endNotificationAutomatedPublishing = "<spring:message code='error.endNotificationAutomatedPublishing' />";
			var allValues = "<spring:message code='label.AllValues' />";
			var checkXHTMLValidityError = "<spring:message code='error.checkXHTMLValidityError' />";
			var mandatoryLabel = "<spring:message code='label.Mandatory' />";
			var optionalLabel = "<spring:message code='label.Optional' />";
			var invalidFileError = "<spring:message code='error.FileNotValid' />";
			var IndentElementLabel = "<spring:message code='label.IndentElement' />";
			var LabelExternalImage = "<spring:message code='warning.ExternalImageLink' />";
			var UnindentElementLabel = "<spring:message code='label.UnindentElement' />";
			var notenoughrowsanswerederror = "<spring:message code='validation.invalidRowsMin' />";
			var toomanyrowsanswerederror = "<spring:message code='validation.invalidRowsMax' />";
			var honeypotError = "<spring:message code='validation.honeypotError' />";
			var clearLabel = "<spring:message code='label.ClearValue' />";
			var cancelLabel = "<spring:message code='label.Cancel' />";
			var showLabel = "<spring:message code='label.Show' />";
			var atLeast3Characters = "<spring:message code='label.atLeast3Characters' />";
			var loginExistsError = "<spring:message code='error.LoginExists' />";
			var message_Export1 = "<spring:message code='message.Export1' />";
			var label_ExportPage = "<spring:message code='label.ExportPage' />";
			var message_PublicationExportSuccess = "<spring:message code='message.PublicationExportSuccess' />";
			var message_PublicationExportSuccess2 = "<spring:message code='message.PublicationExportSuccess2' />";
			var message_ExportFailed = "<spring:message code='message.ExportFailed' />";
			var message_PublicationExportFailed = "<spring:message code='message.PublicationExportFailed' />";
			var atmost3Selections =  "<spring:message code='validation.atmost3Selections' />";
			var varExceptionDuringSave = "<spring:message code='error.ExceptionDuringSave' />";
			var varwaitfordependencies = "<spring:message code='info.WaitForDependencies' />";
			var varErrorCheckValidation = "<spring:message code='error.CheckValidation' />";
			var varErrorCheckValidation2 = "<spring:message code='error.CheckValidation2' />";
			var labelmore = "<spring:message code='label.more' />";
			var labelless = "<spring:message code='label.less' />";
			var labelfrom = "<spring:message code='label.from' />";
			var labelto = "<spring:message code='label.To' />";
			var messageuploadnoconnection = "<spring:message code='message.uploadnoconnection' />";
			var messageuploadwrongextension =  "<spring:message code='message.messageuploadwrongextension' />";
			var labelnewexplanation = "<spring:message code='label.NewExplanation' />";
			var labeloldexplanation = "<spring:message code='label.OldExplanation' />";
			var selectFileForUploadRunner = "<spring:message code='label.SelectFileForUpload' />";
			var timeLimitNotZero = "<spring:message code='error.timeLimitNotZero' />";
			var infolabeluploadbutton = "<spring:message code='info.uploadbutton' />";
			var infoNoData = "<spring:message code='info.NoData' />";
		</c:otherwise>
	</c:choose>
	
	var selectFileForUpload = "<spring:message code='label.SelectFileForUpload' />";
		
	var versionfootersource = "<%@include file="versionfooter.txt" %>";
	var version = versionfootersource.substring(versionfootersource.indexOf("(")+1);
	version = version.substring(0, version.indexOf(" "));
	
	<c:if test="${surveyeditorsaved != null}">
	 	localStorage.removeItem("SurveyEditorBackup${surveyeditorsaved}");
	</c:if>
	
	<c:if test="${forpdf == null}">
	
	var explanationEditorConfig = {
			script_url: '${contextpath}/resources/js/tinymce/tinymce.min.js',
			theme: 'modern',
			entity_encoding: 'raw',
			menubar: false,
			toolbar: ['bold italic underline strikethrough | undo redo | bullist numlist | link code | fontsizeselect forecolor fontselect'],
			plugins: 'paste link image code textcolor',
			font_formats:
				'Sans Serif=FreeSans, Arial, Helvetica, Tahoma, Verdana, sans-serif;' +
				'Serif=FreeSerif,Times,serif;' +
				'Mono=FreeMono,Courier, mono;',
			language : globalLanguage,
			image_advtab: true,
			entities: '',
			content_css: '${contextpath}/resources/css/tinymceyellowfocus.css',
			popup_css_add: '${contextpath}/resources/css/tinymcepopup.css',
			forced_root_block: false,
			browser_spellcheck: true,
			paste_postprocess: function(pl, o) {
				o.node.innerHTML = replaceBRs(strip_tags(o.node.innerHTML, '<p><br>'));
			},
			setup: function(editor) {
				editor.on('init', function(event) {
					delphiPrefill($(event.target));
				});
				editor.on('Change', function (event) {
					try {
					    // The editor element needs to be retrieved again. Otherwise, closest() will return no elements.
					    enableDelphiSaveButtons($('#' + event.target.id).closest('.survey-element'));
					} catch (e) {}
				});
			},
			relative_urls: false,
			remove_script_host: false,
			document_base_url: serverPrefix,
			default_link_target: '_blank',
			anchor_top: false,
			anchor_bottom: false,
			branding: false,
			invalid_elements: 'html,head,body',
			object_resizing: false
		};
	</c:if>
	
</script>
<script type="text/javascript" src="${contextpath}/resources/js/utf8.js?version=<%@include file="version.txt" %>"></script>
<script type="text/javascript" src="${contextpath}/resources/js/includes.js?version=<%@include file="version.txt" %>"></script>

<script type="text/javascript">
	
	//<![CDATA[
		
	function getWrongExtensionMessage(filename)
	{
		var s;
		
		 <c:choose>
			<c:when test="${form != null && form.getResources() != null}">
				s = '${form.getMessage("message.messageuploadwrongextension", "[fn]")}';
			</c:when>
			<c:otherwise>
			s = '<spring:message code="message.messageuploadwrongextension" arguments="[fn]" />';
			</c:otherwise>
		</c:choose>
		
		return s.replace("[fn]", filename);
	}
	
	function getExtensionsHelp(extensions)
	{
		var e = extensions.replace(new RegExp(';', 'g'), ',');
		var s;
		
		 <c:choose>
			<c:when test="${form != null && form.getResources() != null}">
				s = '${form.getMessage("info.extensions", "[ex]")}';
			</c:when>
			<c:otherwise>
			s = '<spring:message code="info.extensions" arguments="[ex]" />';
			</c:otherwise>
		</c:choose>
		
		return s.replace("[ex]", e);
	}
		
	function countChar(input)
	 {	
		 var cs = getCharacterCount(input);
		 
		 var attr = $(input).attr('class');
		 
		 let el = $(input).closest(".survey-element, .innercell");
		 
		 if (attr != null) {
			 let min = 0;
			 let max = 0;
			 attr.split(/\s+/).forEach((cla)=>{
				if (cla.startsWith("min")){
					min = parseInt(cla.substring(3));
				} else if (cla.startsWith("max")){
					max = parseInt(cla.substring(3));
				}
			 })

			 el.find(".charactercounter").text(cs);
			 
			 if (max > 0 && max - cs < 5)
			 {
				 el.find(".glyphicon-alert").show();
			 } else {
				 el.find(".glyphicon-alert").hide();
			 }
			 
			 if (max > 0 && max - cs < 0)
			 {
				 el.find(".charactercounterdiv").css("color", "#f00");
			 } else {
				 el.find(".charactercounterdiv").css("color", "#777");
			 }

			 if(max > 0 && max - cs <= 0)
			 {
				 el.find(".glyphicon-alert").hide();
				 el.find(".characterlimitreached").show();
				 el.find(".charactersused").hide();
			 } else {
				 el.find(".characterlimitreached").hide();
				 el.find(".charactersused").show();
			 }
		 }
	 }
	
	//]]>
	
	var browser = '';
	
</script>

<c:choose>
	<c:when test="${forpdf == null && !oss && piwik && is404}">
		<script defer="defer" src="//europa.eu/webtools/load.js" type="text/javascript"></script>
		<script type="application/json">
		{ "utility":"analytics", "siteID":"63", "sitePath":["ec.europa.eu/eusurvey"], "is404":true, "is403":false, "instance":"ec.europa.eu"}
		</script>
	</c:when>
	<c:when test="${forpdf == null && !oss && piwik}">
		<script defer="defer" src="//europa.eu/webtools/load.js" type="text/javascript"></script>
		<script type="application/json">
		{ "utility":"analytics", "siteID":"63", "sitePath":["ec.europa.eu/eusurvey"], "is404":false, "is403":false, "instance":"ec.europa.eu"} 
		</script>
	</c:when>
</c:choose>

<c:choose>
	<c:when test="${runnermode != null && form != null && form.language != null}">
		<script type="text/javascript" src="${contextpath}/resources/js/jqueryui/i18n/datepicker-${form.language.code.toLowerCase()}.js?version=<%@include file="version.txt" %>"></script>	
		
		<script type="text/javascript">
			$(function() {
				$( ".datepicker" ).datepicker( "option", $.datepicker.regional[ '${form.language.code.toLowerCase()}' ] );
				$( ".datepicker" ).datepicker( "option", "dateFormat", "dd/mm/yy");
			});
		</script>		
	</c:when>
	<c:otherwise>
		<script type="text/javascript" src="${contextpath}/resources/js/jqueryui/i18n/datepicker-${pageContext.response.locale}.js?version=<%@include file="version.txt" %>"></script>	
		
		<script type="text/javascript">
			$(function() {
				$( ".datepicker" ).datepicker( "option", $.datepicker.regional[ '${pageContext.response.locale}' ] );
				$( ".datepicker" ).datepicker( "option", "dateFormat", "dd/mm/yy");
			});
		</script>	
	</c:otherwise>
</c:choose>

	<!--[if IE 7]>
	<link rel="stylesheet" href="${contextpath}/resources/css/bootstrap-ie7buttonfix.css">
	<style type="text/css">	
		.add-on {
			height: 19px;
			margin-top: 1px;
		}	
	</style>
	<![endif]-->
	
	<!--[if IE 8]>
		<link rel="stylesheet" href="${contextpath}/resources/css/bootstrap-ie8buttonfix.css">
		<style>
			.check {
				margin-bottom: 3px !important;
			}
		</style>
	<![endif]--> 
	
	<!--[if IE 9 ]>  
		<script type="text/javascript">
			browser = 'IE9';
		</script>	
	 <![endif]-->
