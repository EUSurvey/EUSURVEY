<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Documentation" /></title>	
	<%@ include file="../includes.jsp" %>
	<link href="${contextpath}/resources/css/yellowfocus.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css"></link>
		
	<style type="text/css">

		<c:choose>
			<c:when test="${USER != null && runnermode == null }">
				.anchor {
					 display: block;
					 height: 1100px;
					 margin-top: -110px;
					 visibility: hidden;
				}
			</c:when>
			<c:otherwise>
				.anchor {
					 display: block;
					 height: 40px;
					 margin-top: -40px;
					 visibility: hidden;
				}
			</c:otherwise>
		</c:choose>				
		
		.anchorTop
		{
			float: right;
			font-size: 13px;
			font-weight: normal;
			text-decoration: none;
		}
		
		.anchorlink {
			margin-left: 40px;
			text-decoration: underline;
			color: #005580;
		}
		
		.anchorlink a:hover {
			text-decoration: underline;
			color: #005580;
		}
		
		.head {
			margin-left: 20px;
		}
		
		#faqcontent img {
			border: 1px solid #999;
		}
		
		#ulContainer {
			margin-bottom: 50px;
		}

		figcaption {
			font-style: italic;
			padding: 2px;
			text-align: center;
		}
		
		#faqcontent a {
			text-decoration: underline;
		}

	</style>

	<script language="javascript" type="text/javascript" src="${contextpath}/resources/js/tree/treemenu.js?version=<%@include file="../version.txt" %>"></script>
	<script language="javascript" type="text/javascript" src="${contextpath}/resources/js/tree/treemenu2.js?version=<%@include file="../version.txt" %>"></script>
	
	<link rel="stylesheet" href="${contextpath}/resources/js/tree/treeview.css?version=<%@include file="../version.txt" %>" type="text/css">

	
</head>
<body id="bodyDocHelpAuthor">
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
	
		<a name="topAnchor"></a>
	
		<c:choose>
			<c:when test="${USER != null && runnermode == null }">
				<%@ include file="../menu.jsp" %>	
				<div class="page" style="padding-top: 110px">
			</c:when>
			<c:otherwise>
				<div class="page" style="padding-top: 40px">
			</c:otherwise>
		</c:choose>	
	
		<div class="pageheader">
			<div style="float:right; font-size:125%" >
			[<a href="helpauthors?faqlanguage=en">EN</a>] [<a href="helpauthors?faqlanguage=fr">FR</a>] [<a href="helpauthors?faqlanguage=de">DE</a>]
			</div>
			<h1><spring:message code="label.HelpAuthors" /></h1>
		</div>
		
		<h2>Contents</h2>
	
		<div id="ulContainer">
		
			<a href="javascript:ddtreemenu.flatten('treemenu', 'expand')">Expand All</a>&nbsp;|&nbsp;<a href="javascript:ddtreemenu.flatten('treemenu', 'contact')">Collapse All</a>
			<br/><br/>
			<ul id="treemenu" class="treeview" rel="closed">
				
			</ul>
		</div>

		<div id="faqcontent">
			<h1>
				General questions
			</h1>
			<h2>
				What is EUSurvey?
			</h2>
			<p>
				EUSurvey is an online survey management tool to create, publish and manage questionnaires and other
				interactive forms in most web browsers.
			</p>
			<h2>
				When do I use EUSurvey?
			</h2>
			<p>
				Use EUSurvey whenever you want to make a questionnaire or interactive form accessible online, or to
				record a large number of similar datasets.
			</p>
			<h2>
				What are the limits of EUSurvey?
			</h2>
			<p>
				Please open the <a href="https://ec.europa.eu/eusurvey/home/documentation">Support</a> page to find the
				'Usability limits' of the tool.
			</p>
			<p>
				EUSurvey may not be the right tool for your project if:
				<ul>
					<li>
						different participants need to work on the same contribution before it is submitted
					</li>
					<li>
						contributions need to be validated before they can be submitted
					</li>
				</ul>
			</p>
			<p>
				Please contact DIGIT-EUSURVEY-SUPPORT for additional information, upcoming features and possible
				workarounds.
			</p>
			<h2>
				What are the features of EUSurvey?
			</h2>
			<p>
				<strong>Customizable forms</strong>
				<br/>
				In the easy-to-use editor you can choose from a variety of question types; from simple text and
				multiple-choice questions to spreadsheet questions or multimedia survey elements. Structure your survey
				using special structural elements.
			</p>
			<p>
				<strong>Dependent questions</strong>
				<br/>
				EUSurvey can display additional questions and fields, depending on the answers given by the participant,
				making the survey interactive.
			</p>
			<p>
				<strong>Scheduled publishing</strong>
				<br/>
				Publish and unpublish your survey automatically at a specified point in time.
			</p>
			<p>
				<strong>Modify your form after publication</strong>
				<br/>
				You can modify a published survey without losing any contributions.
			</p>
			<p>
				<strong>Languages</strong>
				<br/>
				The user interface is available in 23 of the official EU languages, and you can translate your form into
				any of the 136 languages covered by ISO 639-1, from Abkhaz to Zulu.
			</p>
			<p>
				<strong>Security</strong>
				<br/>
				EUSurvey has the infrastructure needed to secure the online forms.
			</p>
			<p>
				<strong>Sending out invitations directly from the application</strong>
				<br/>
				Selected contacts can be managed in the 'Address Book' and used to send out individual emails to every
				single contact containing individual access links.
			</p>
			<p>
				<strong>Advanced privacy</strong>
				<br/>
				You can guarantee the participant's privacy by creating an anonymous form. Connection details will then
				not be available to you as the author of the form.
			</p>
			<p>
				<strong>Customise the look and feel</strong>
				<br/>
				With the built-in CSS style editor and embedded rich-text editors for all visible elements, you have
				full control over the form's layout. A large list of survey themes makes it easy to adapt the form to a
				project's identity. You can choose between single page and multi-page surveys
			</p>
			<p>
				<strong>Save a contribution as draft</strong>
				<br/>
				You can allow participants to save their contribution as a draft on the server and continue later.
			</p>
			<p>
				<strong>Offline answering</strong>
				<br/>
				With EUSurvey, you can answer a form offline before submitting a contribution to the server at a later
				point in time.
			</p>
			<p>
				<strong>Automatic numbering</strong>
				<br/>
				To structure your survey, EUSurvey can number your form's elements for you.
			</p>
			<p>
				<strong>Enhanced contrast</strong>
				<br/>
				Visually impaired participants can choose a high-contrast version of the survey. This is created
				automatically for each form.
			</p>
			<p>
				<strong>Uploading supporting files</strong>
				<br/>
				You can add files to your survey by uploading them. These files can be downloaded by every participant
				in your survey.
			</p>
			<h2>
				Form management
			</h2>
			<p>
				<strong>Working together</strong>
				<br/>
				For surveys that are managed by multiple users, EUSurvey lets you define advanced permissions for other
				users to test a survey or to analyse the results.
			</p>
			<h2>
				Result management
			</h2>
			<p>
				<strong>Analysing your results</strong>
				<br/>
				EUSurvey offers basic result analysis capabilities and visualization of data in histograms and chart
				views. You can also export survey results to standard spreadsheet formats in order to import them into
				statistical applications.
			</p>
			<p>
				<strong>Publishing your results</strong>
				<br/>
				Use the possibilities EUSurvey offers to publish a sub-set of all submitted answers on the internal
				pages of the application. Statistics and charts can be calculated and created automatically by the
				system.
			</p>
			<p>
				<strong>Editing submitted contributions</strong>
				<br/>
				Let your participants modify a contribution after it has been submitted, if they want to.
			</p>
			<h2>
				Where do I find additional information about EUSurvey?
			</h2>
			<p>
				Please find practical information behind the '
				<a href="https://ec.europa.eu/eusurvey/home/documentation" target="_blank">
					Support
				</a>
				' link under 'Help' at the top-right of the application. Consult the '
				<a href="https://ec.europa.eu/eusurvey/home/about" target="_blank">
					About
				</a>
				' page for additional information on the application's background and funding.
			</p>
			<h2>
				Whom do I contact if there are technical problems with EUSurvey?
			</h2>
			<p>
				EU staff should contact their IT helpdesk and ask them to forward the problem to DIGIT-EUSURVEY-SUPPORT,
				describing it as precisely as possible.
			</p>
			<p>
				External users should contact the Commission's
				<a
						href="mailto:EC-CENTRAL-HELPDESK@ec.europa.eu?subject=Incident%20Creation%20Request%20for%20DIGIT%20EUSURVEY%20SUPPORT%20&amp;body=%20Dear%20Helpdesk,%0D%0DCould%20you%20please%20open%20a%20ticket%20to%20DIGIT%20EUSURVEY%20SUPPORT%20with%20the%20following%20description:"
						target="_blank"
						>
					CENTRAL HELPDESK
				</a>
				.
			</p>
			<h2>
				Whom do I contact if I have new ideas for improving EUSurvey?
			</h2>
			<p>
				We always welcome your comments and feedback! Please ask your IT helpdesk or the CENTRAL HELPDESK to
				forward your suggestions to the EUSurvey support team. The support team will get back to you as soon as
				possible to discuss the relevant use cases and whether your idea can be incorporated in a future version
				of the application.
			</p>
			<h2>
				Which browsers are supported by EUSurvey?
			</h2>
			<p>
				The last two versions of Microsoft Edge, Mozilla Firefox and Google Chrome are supported by EUSurvey.
				Using other browsers might cause compatibility problems.
			</p>
			<h2>
				EUSurvey disclaimer (for non-EU users only)
			</h2>
			<p>
				For all questionnaires and invitation mails coming from a survey that has been created by a user
				<strong>not working officially for the EU institutions</strong>, the following disclaimer will be
				displayed in the survey and email message:
			</p>
			<p>
				Disclaimer
				<br/>
				<em>
					The European Commission is not responsible for the content of
					questionnaires created using the EUSurvey service - it remains the sole
					responsibility of the form creator and manager. The use of EUSurvey
					service does not imply a recommendation or endorsement, by the European
					Commission, of the views expressed within them.
				</em>
			</p>
			<h2>
				Can my participants answer my survey from a mobile device?
			</h2>
			<p>
				Yes, EUSurvey features a responsive design for the published survey. This means, the design of the
				questionnaire will adapt to the resolution of the device used to enter your survey. This will allow
				your participants to conveniently contribute to your survey from a mobile device (mobile phone or
				tablet PC).
			</p>
			<h2>
				Is there a minimum screen size?
			</h2>
			<p>
				The online questionnaires are fully responsive and adapt to the size of your device, allowing to fill in
				the questionnaire with any screen size.
			</p>
			<p>
				For creating and managing surveys, we recommend to use a minimum resolution of 1680x1050 pixels for a
				good user experience.
			</p>
			<h1>
				Login and EU Login registration
			</h1>
			<h2>
				I have an EU Login account. Do I need to register separately for EUSurvey?
			</h2>
			<p>
				No, there is no need to register separately to EUSurvey. An EU Login account is enough. You can access
				EUSurvey by clicking on the login button on the
				<a href="https://ec.europa.eu/eusurvey/home/welcome" target="_blank">
					EUSurvey homepage
				</a>
				. This takes you to the login screen (see below for more details).
			</p>
			<h2>
				How do I connect to EUSurvey?
			</h2>
			<p>
				Please click on the login button on the
				<a href="https://ec.europa.eu/eusurvey/home/welcome" target="_blank">
					EUSurvey homepage
				</a>
				. You will be redirected to the EUSurvey login screen.
			</p>
			<p>
				Once you arrived on the login screen, you have to choose the option corresponding to your personal case:
				<ul>
					<li>
						<strong>If you work for an EU institution, </strong>
						choose the second option to connect to the EUSurvey application.
						Your EU Login username and password will then be sufficient.
					</li>
					<li>
						<strong>If you don't work for an EU institution (Externals), </strong>
						choose the first option to connect to the EUSurvey application. You will need to have previously
						registered your mobile phone to pass the
						<a href="https://en.wikipedia.org/wiki/Help:Two-factor_authentication">
							two-factor authentication
						</a>.
					</li>
				</ul>
			</p>
			<p>
				If you don't have an EU Login account, please create one by clicking
				<a href="https://webgate.ec.europa.eu/cas/eim/external/register.cgi">
					here
				</a>
				.
			</p>
			<p>
				In case you don't work for an EU institution, please also register your mobile phone by clicking
				<a
						href="https://ecas.ec.europa.eu/cas/userdata/mobileApp/manageMyMobileDevices.cgi"
						>
					here
				</a>
				.
			</p>
			<h1>
				Creating a survey
			</h1>
			<h2>
				How do I create a new survey?
			</h2>
			<p>
				On the 'Welcome' page or the 'Surveys' page, click on 'New Survey' 'Create new Survey' option and a
				dialogue box will open. Once you have entered all mandatory information, click on 'Create'. The
				application will load your new survey into the system and open the 'Editor' automatically, so you can
				start adding elements to your survey right away.
			</p>
			<h2>
				What type of surveys can I create?
			</h2>
			<p>
				When creating a new questionnaire, you can choose between the following options:
				<ul>
					<li>
						<strong>Standard survey</strong>
						<br />
						It is the standard type of the survey to create a questionnaire.
					</li>
					<li>
						<strong>Quiz</strong>
						<br/>
						A quiz is a special type of survey that allows calculating a final score for each participant.
						Such surveys can be used e.g. as skill tests or electronic exams. You will find detailed
						information about the creation of a quiz survey in the
						<a
								href="https://circabc.europa.eu/sd/a/400e1268-1329-413b-b873-b42e41369a07/EUSurvey_Quiz_Guide.pdf"
								target="_blank"
								>
							EUSurvey quiz guide
						</a>
						.
						<br />
						The quiz mode includes amongst others:
						<ul>
							<li>
								A scoring feature
							</li>
							<li>
								The verification of participants' answers
							</li>
							<li>
								The possibility to provide feedback to your participants, depending on their answers
							</li>
							<li>
								Additional result analysis that has been specifically designed for quizzes
							</li>
						</ul>
					</li>
					<li>
						<strong>BRP Public Consultation</strong>
						<br/>
						This survey type is used specifically for running public consultations via the Better Regulation
						Portal (published on the '
						<a href="https://ec.europa.eu/info/law/better-regulation/have-your-say">
							Have your say
						</a>
						' Europa website).
						<br/>
						The BRP template provides:
						<ul>
							<li>
								Dedicated, immutable metadata fields allowing uniform identification of respondents
								across different surveys and thus simplifying the reporting
							</li>
							<li>
								Tailored privacy statement taking into account the specific constraints of public
								consultations
							</li>
							<li>
								Automatic opening and closing of the survey from BRP
							</li>
							<li>
								Automatic synchronisation of the respondents&rsquo; answers to BRP for further processing
							</li>
						</ul>
					</li>
				</ul>
			</p>
			<h2>
				How do I import an existing survey from my computer?
			</h2>
			<p>
				On the 'Welcome' page or the 'Surveys' page, click on 'New Survey' 'Import Survey' option and a dialogue
				box will open. Once you have selected a survey file from your computer, click on 'Import' and your
				survey will be added to EUSurvey.
			</p>
			<p>
				Note: you can only import surveys as zip files or with the file extension .eus.
			</p>
			<h2>
				Where can I find all surveys that I have created?
			</h2>
			<p>
				There are two ways to find them. First, on the dashboard page, you will find a list of all the surveys
				you have created. The second way is to go to the 'Surveys' page and select the 'My Surveys' option in
				the search panel.
			</p>
			<h2>
				How do I open an existing survey for editing, etc.?
			</h2>
			<p>
				From the 'Surveys' page, click on the 'Open' icon of the survey you want and the 'Overview' page will
				open with several new tabs. From here you can go to the 'Editor', test your survey, or access the
				survey's 'Results', 'Translations', 'Properties' etc.
			</p>
			<h2>
				How do I export an existing survey?    
			</h2>
			<p>
				On the 'Surveys' page, search for the survey you want to export. You can either

				<ul>
					<li>
						click on the 'Export' icon, OR
					</li>
					<li>
						click on the 'Open' icon, and from the 'Overview' page, click on the 'Export' icon.
					</li>
				</ul>
			</p>
			<p>
				Your survey, together with all your settings, will be saved to your computer.
				The file extension of an EUSurvey form file is '.eus'.
			</p>
			<h2>
				How do I copy an existing survey?    
			</h2>
			<p>
				On the 'Surveys' page, open the survey you want and click on the 'Copy' icon. In the dialogue box that
				opens you can modify the necessary settings, and then click on 'Create'. Your survey will be added to
				the list on the 'Surveys' page. You can start editing right away.
			</p>
			<h2>
				How do I remove an existing survey?    
			</h2>
			<p>
				On the 'Surveys' page, open the survey you want and click on the 'Delete' icon. Once you have confirmed,
				your survey will be removed from the list of surveys.
			</p>
			<p>
				<strong>
					<u>Beware:</u>
				</strong> deleting a survey removes <strong>
					<u>every trace</u>
				</strong> of your questions and your results from the EUSurvey system! <u>This cannot be undone!</u>
			</p>
			<h2>
				How do I create WCAG compliant questionnaires with EUSurvey?
			</h2>
			<p>
				The Web Content Accessibility Guidelines (WCAG) consist of a set of guidelines for making content
				accessible, primarily for people with disabilities, but also for software such as mobile phones.
			</p>
			<p>
				If you wish to make your survey WCAG compliant, please follow the instructions explained
				<a
						href="https://circabc.europa.eu/d/a/workspace/SpacesStore/78b03213-5cf4-4aab-8e90-ada7e2eb1101/WCAG_tutorial%20.pdf"
						target="_blank"
						>
					in this document
				</a>
				.
			</p>
			<h2>
				What is the 'Motivation popup' and how to use it?
			</h2>
			<p>
				The 'Motivation popup' is a dialog window which opens to the survey participant during the completion
				of the form. It displays a message to motivate the participant to continue his progress. This message
				is customisable as well as the time at which the popup is displayed.
			</p>
			<p>
				'Motivation popup' is available in the survey's 'Properties' under the 'Appearance' tab.
			</p>
			<figure>
				<img alt="screenshot motivation popup" src="${contextpath}/resources/images/documentation/motivation_popup_1.png" style="width: 75%"/>
				<figcaption>Motivation popup in the survey's 'Properties'</figcaption>
			</figure>
			<p>
				Once the switch has been activated, the options appear.
			</p>
			<p>
				The 'Trigger' can be based on the progress or a timer.
			</p>
			<p>
				'Progress' is expressed as a percentage. For instance, 50%. This means that the popup will be displayed
				as soon as the survey participant answered 50% of the questions.
			</p>
			<p>
				In case the 'timer' option has been selected, the popup will be displayed after X minutes. X being the
				number of minutes specified in the 'Threshold value' field.
			</p>
			<p>
				Finally, the text is customizable using the 'Motivation text' field.
			</p>
			<figure>
				<img alt="screenshot motivation popup" src="${contextpath}/resources/images/documentation/motivation_popup_2.png" style="width: 75%"/>
				<figcaption>Motivation popup configuration fields</figcaption>
			</figure>
			<h1>
				Editing a survey
			</h1>
			<h2>
				How do I start the Editor?    
			</h2>
			<p>
				First make sure that you have opened an existing survey: Go to the 'Surveys' page and click the 'Open'
				icon of the survey you want to edit. From the 'Overview' page, click 'Editor' to open it and to start
				with the editing of your questionnaire.
			</p>
			<p>
				Please make sure to save your work from time to time.
			</p>
			<h2>
				How do I create a questionnaire via the EUSurvey editor?    
			</h2>
			<p>
				The editor is used to create a questionnaire. You can use it to add questions and other elements to your
				questionnaire.
			</p>
			<p>
				You will find detailed information about the functionalities of the Editor in the
				<a
						href="https://ec.europa.eu/eusurvey/resources/documents/Editor_Guide.pdf"
						target="_blank"
					>
					EUSurvey Editor Guide
				</a>
				.
			</p>
			<p>
				The editor consists of five different areas:
				<ol type="i">
					<li>
						<strong>Navigation pane:</strong>
						<br/>
						The Navigation pane provides a structured view of the questionnaire. All elements are
						represented by their respective text label in the questionnaire. When you select an element in
						the Navigation pane, the Form area jumps to the selected item which is then highlighted in blue.
					</li>
					<li>
						<strong>Toolbox pane:</strong>
						<br/>
						The Toolbox pane contains all the element types that you can add to your questionnaire.
						You can add elements either by using the drag-and-drop feature or by double-clicking them.
					</li>
					<li>
						<strong>Form area:</strong>
						<br/>
						Provides a preview of the questionnaire; elements can be added to it and selected for editing.
					</li>
					<li>
						<strong>Element properties pane:</strong>
						<br/>
						It displays the settings for selected elements. You can edit the elements here, e.g. by changing
						the question text, adding help messages and changing all relevant settings to adapt the question
						to your needs.
					</li>
					<li>
						<strong>Toolbar:</strong>
						<br/>
						It includes all available basic tasks that you can perform when creating the questionnaire.
					</li>
				</ol>
			</p>
			<h2>
				How do I add or remove questions to my questionnaire?    
			</h2>
			<p>
				To add new elements to your form or remove existing ones, first open the editor.
			</p>
			<p>
				In the editor you will find a Toolbox of available elements on the left and the Form area in the middle
				of the screen. The elements contain default texts, with the name of the element displayed as the
				question text. To add a new element (question, text field, image, etc.), select one from the Toolbox.
				You can add elements either by using the drag-and-drop feature or by double-clicking them.
			</p>
			<p>
				To remove an element from the form, you can click the element to select it. Click on 'Delete'; as soon
				as you have confirmed, the element will be removed from the questionnaire.
			</p>
			<p>
				See also
				<a href="#_Toc_4_2">
					'How do I create a questionnaire via the EUSurvey editor?'
				</a>
			</p>
			<h2>
				How do I edit elements in my questionnaire?
			</h2>
			<p>
				The elements in your questionnaire will be <strong>selected for editing in the Form area</strong> and
				<strong>edited in the Element properties pane</strong> of the Editor. See
				<a href="#_Toc_4_2">
					'How do I create a questionnaire via the EUSurvey editor?'
				</a>
			</p>
			<p>
				You can click an element in the Form area to select it. The selected element appears in blue, with the
				respective options visible in the Element properties pane. You can edit the elements there, e.g.
				changing the question text, adding help messages and changing all relevant settings to adapt the
				question to your needs.
			</p>
			<p>
				To edit a text:
			</p>
			<ol>
				<li>
					Click on the text or the pen icon.
				</li>
				<li>
					Modify the text.
				</li>
				<li>
					Click 'Apply' to see the changes in the Form area.
				</li>
			</ol>
			<p>
				By default, the Element properties pane displays all the basic options. To display more options,
				click 'Advanced'.
			</p>
			<p>
				For matrix and text questions, you can also choose the individual questions/answers/rows/columns of the
				element by clicking the respective label text as indicated below. This way, you can e.g. select
				individual questions of a matrix or table element and make them mandatory.
			</p>
			<h2>
				How do I copy items?    
			</h2>
			<p>
				To copy elements in your form, first open the Editor.
			</p>
			<p>
				Any elements that have been copied or cut are depicted by a placeholder at the top of the Toolbox pane.
				You can add them to the questionnaire again using the drag-and-drop feature. You can also use the button
				next to the element to cancel this operation.
			</p>
			<ol>
				<li>
					Select the element(s).
				</li>
				<li>
					Click 'Copy'.
				</li>
				<li>
					Move the placeholder from the Toolbox to the Form area as described above or select the element in
					the Form area and click 'Paste after'.
				</li>
			</ol>
			<p>
				See also
				<a href="#_Toc_4_2">
					'How do I create a questionnaire via the EUSurvey editor?'
				</a>
			</p>
			<h2>
				How do I add or remove possible answers in choice questions?    
			</h2>
			<p>
				You can add or remove possible answers by clicking the plus/minus button in the Element properties pane.
				Edit the text of the existing answer options by clicking the pen icon next to 'Possible answers'.
				You can then edit them in the rich text editor.
			</p>
			<p>
				See also
				<a href="#_Toc_4_2">
					'How do I create a questionnaire via the EUSurvey editor?'
				</a>
			</p>
			<h2>
				Can I make a question mandatory?    
			</h2>
			<p>
				In the editor, select the check box in the Element properties pane after selecting the respective element.
			</p>
			<p>
				Mandatory questions will be marked with a red asterisk to the left of the question text.
			</p>
			<h2>
				How do I move items within the questionnaire?    
			</h2>
			<p>
				In the editor, you can change the position of an element in your questionnaire by using one of the
				following options:
			</p>
			<p>
				<strong>Drag-and-drop:</strong>
				<br/>
				Select the element in the Form area and drag it to where you want in the questionnaire.
			</p>
			<p>
				<strong>Move buttons:</strong>
				<br/>
				Select the element you want to move and use the move up/move down buttons in the Toolbar on top of the
				Form Area to move it up or down.
			</p>
			<p>
				<strong>Cut-and-paste:</strong>
				<br/>
				Cut the element you want to move and use the drag-and-drop feature to move the placeholder to where you
				want to put it.
			</p>
						
			<h2>How do I use the visibility feature (dependencies)?</h2>
			<p>
				Using this feature in the editor, you can display and hide elements depending on answers given by
				participants to either single/multiple choice or matrix questions (see also
				<a href="#_Toc_4_2">
					'How do I create a questionnaire via the EUSurvey editor?'
				</a>
				)
			</p>
			<p>
				By default, all elements are set to always visible, which means that everybody will see the question
				when answering the survey.
			</p>
			<p>
				To create a dependent question:
				<ol>
					<li>Add a single/multiple choice or matrix question to your questionnaire.</li>
					<li>Add further elements to your questionnaire.</li>
					<li>Select an element that follows a single/multiple choice or matrix element and that should only
						appear when a specific answer has been chosen.</li>
					<li>Click the pen icon to edit the visibility settings. All available questions of the type single
						choice, multiple choice and matrix that are placed above the selected element(s) are displayed,
						including the question text and the possible answers.</li>
					<li>Select the answer that, when chosen, will display the selected element.</li>
					<li>Click 'Apply' to confirm the visibility setting.</li>
				</ol>
			</p>
			<p>
				If multiple elements are selected, you can edit the visibility settings for all of them at once.
			</p>
			<p>
				<strong>Note:</strong> This modification will only affect the questionnaire on the Test page and in
				published mode. All elements will still be visible in the editor.
			</p>
			<p>
				When activated, arrows are displayed next to the connected elements to visualise the visibility settings
				in the Form area. Answers that trigger any element are displayed with an arrow pointing down. Elements
				that are triggered by any answer are marked with an arrow pointing up.
			</p>
			<p>
				When moving the pointer over the arrows or IDs in the Element properties pane, the connected elements
				are highlighted in the Form area and Navigation pane.
			</p>			
			<p>
				Elements with visibility settings that have been edited will be hidden in the questionnaire until at
				least one of the configured answers has been selected by the participant when filling in the questionnaire.
			</p>
			<h2>
				Can I generate the order of answers in a single or multiple choice question?
			</h2>
			<p>
				When creating a single or multiple choice question, you will have the option to generate their answers
				in three different ways:
				<ul>
					<li>
						Original Order
					</li>
					<li>
						Alphabetical Order
					</li>
					<li>
						Random Order
					</li>
				</ul>
			</p>
			<p>
				Original Order: This option will display the original order of your answers on the survey.
			</p>
			<p>
				Alphabetical Order: You can select this option if the answers should be displayed alphabetically on the
				survey.
			</p>
			<p>
				Random Order: You can choose this option if the answers should be randomly displayed on the survey.
			</p>
			<h2>
				How do I give other users permission to edit my survey?
			</h2>
			<p>
				Open your survey and open the 'Privileges' page. Click on 'Add new user' or 'Add department'. A wizard
				will pop up that guides you through the process of adding users. Next you can give them specific access
				rights. Simply click on the colour to change the rights.
			</p>
			<p>
				<ul>
					<li>
						Green: Read and Write access
					</li>
					<li>
						Yellow: Read access
					</li>
					<li>
						Red: No access
					</li>
				</ul>
			</p>
			<p>
				Added users will automatically see your survey in their list of surveys next time they log into EUSurvey.
			</p>
			<p>
				External owners or survey organizers cannot see EU domains on the Privileges tab / Add users button,
				therefore, they cannot provide access for these persons directly. Please
				<a href="https://ec.europa.eu/eusurvey/home/support">contact us</a>
				to request privileges for external users.
			</p>
			<p>
				Read more about this under
				<a href="#_Toc_9_8">
					'How do I give other users access to my survey?'
				</a>
			</p>
			<h2>
				Which languages are supported by the application?
			</h2>
			<p>
				Languages which can be encoded in '3byte UTF-8' can be used to create a	survey.
			</p>
			<h2>
				So why UTF-8 and which fonts should be used?
			</h2>
			<p>
				Keep in mind that the target group of participants can display the survey easily if the font you used
				is installed in their browser. UTF-8 is the most common encoding for the HTML pages.
				On the other hand, choosing non-supported fonts may affect the rendering of PDF export.
			</p>
			<p>
				We recommend using the <strong>supported character sets</strong> listed below:
			</p>
			<ul>
				<li>
					Freesans (
					<a
							href="https://circabc.europa.eu/sd/a/36f72861-fc6e-4fe1-87d6-0a8e1c6fa161/EUSurvey-SupportedCharacterSet(freesans).txt"
							target="_blank"
							>
						https://circabc.europa.eu/sd/a/36f72861-fc6e-4fe1-87d6-0a8e1c6fa161/EUSurvey-SupportedCharacterSet(freesans).txt
					</a>
					)
				</li>
				<li>
					Freemono (
					<a
							href="https://circabc.europa.eu/sd/a/55ce0f35-b3cc-4712-80bf-af42800a278f/EUSurvey-SupportedCharacterSet(freemono).txt"
							target="_blank"
							>
						https://circabc.europa.eu/sd/a/55ce0f35-b3cc-4712-80bf-af42800a278f/EUSurvey-SupportedCharacterSet(freemono).txt
					</a>
					)
				</li>
				<li>
					Freeserif (
					<a
							href="https://circabc.europa.eu/sd/a/29cd78bb-9eeb-40b1-a22f-b54700750537/EUSurvey-SupportedCharacterSet(freeserif).txt"
							target="_blank"
							>
						https://circabc.europa.eu/sd/a/29cd78bb-9eeb-40b1-a22f-b54700750537/EUSurvey-SupportedCharacterSet(freeserif).txt
					</a>
					)
				</li>
				<li>
					Commonly supported character-set (
					<a
							href="https://circabc.europa.eu/sd/a/1eb30efd-e2d8-4c3b-9f55-533bb903f7d0/EUSurvey-SupportedCharacterSet(common).txt"
							target="_blank"
							>
						https://circabc.europa.eu/sd/a/1eb30efd-e2d8-4c3b-9f55-533bb903f7d0/EUSurvey-SupportedCharacterSet(common).txt
					</a>
					)
				</li>
			</ul>
			<p>
				<strong>'Freesans' is the default font used</strong>
			</p>
			<p>
				In case of doubt, run a PDF export of your final survey to check
				if your survey is rendered correctly in PDF. Beware however that
				some contributions may not to be rendered correctly in PDF. Your
				participants, indeed, are free to choose any font among those which
				are supported by the application. Even though the application is
				unable to render the characters they have used, these are saved
				correctly on the EUSurvey's database. Thus they can be exported from
				the results page.
			</p>
			<h2>
				Complexity indicator  
			</h2>
			<p>
				Keeping your survey short and simple will ease the filling of your survey by the respondents and lead to a better user experience. Of course, sometimes you need to add branch logic using dependencies (i.e. dependent questions that are hidden/displayed depending on the previous answers given). This is fine, but please bear in mind that adding too many items or dependencies to your survey makes it too 'complex'. This can lead to the system slowing down for participants when filling out your questionnaire.
			</p>
			<p>
				This is why there is a little indicator at the top-right corner of the form editor:<br />
				<img alt="screenshot complexity" src="${contextpath}/resources/images/documentation/complexity.png" />
			</p>
			<p>
				Your survey could have a high level of complexity for several reasons: 
				<ul>
					<li>too many dependencies</li>
					<li>too many cascading dependencies</li>
					<li>too many table/matrix items</li>
				</ul>
			</p>
			<p>
				For more information, see our <a href="https://circabc.europa.eu/sd/d/281e626e-279e-45df-8581-1f6e04feff51/BestPractices-EUSurvey.pdf">best practices guide</a> and do not hesitate to contact the <a href="${contextpath}/home/support?assistance=1">EUSurvey support team</a> for assistance in re-designing your form if needed.
			</p>
			<h2>What is the contribution ID?</h2>
			<p>
				Contribution ID is a code used as unique identifier for a contribution made.
			</p>
			<p>
				It can be used by survey managers to find back a contribution from the 'Results' screen. It can also be used by a survey participant to submit his contribution and access it later.
			</p>
			<h2>How can I find a contribution from the results using a contribution ID?</h2>
			<p>
				Contribution ID can be used by survey owners to find a contribution among all the results:
				<ol>
					<li>
						Open your survey
					</li>
					<li>
						Go to 'Results' tab
					</li>
					<li>
						Click on 'Settings' button
					</li>
					<li>
						Tick 'Contribution ID' in both columns and press 'OK'
					</li>
					<li>
						Go to filter 'Contribution ID'
					</li>
					<li>
						Insert the 'Contribution ID' and click Enter
					</li>
				</ol>
			</p>
			<h2>Randomization feature</h2>
			<p>
				When using a first-level <strong>Section</strong>, you have the possibility to keep the questions/elements underneath as in their original order or to randomize their position.
				Randomization can be selected in the Section Properties just next to Order.
			</p>
			<img alt="screenshot randomization" src="${contextpath}/resources/images/documentation/randomization.png" style="margin-bottom: 1em" />
			<p>Please also note the following:</p>
			<ul>
				<li>All survey elements (including images and static text) are randomized.</li>
				<li>If a question triggers a visibility change or is triggered by a visibility change, it's position is not changed. It is always shown before the other 'randomized' elements.</li>
				<li>Sub-sections and their questions (level 2 and 3) are also randomized, but their order within the level 1 Section they belong to is kept. This means that questions under a sub-section are also randomized when the level 1 Section is randomized.</li>
				<li>PDF version of the survey ('Download PDF version' feature) always shows questions in the original order.</li>
				<li>PDF contributions always show questions in the original order.</li>
				<li>In case the Section/Question numbering is activated in conjunction with the Randomization feature, then the Question numbers will also be randomized along with the questions.
					<div><img alt="screenshot randomization in sections" src="${contextpath}/resources/images/documentation/randomization_sections.png" /></div>
				</li>
			</ul>
			<p>There is another point specific to DELPHI surveys:</p>
			<ul>
				<li>The DELPHI start page shows the questions in their original order.</li>
			</ul>
			<h2>Formula field</h2>
			<p>
				This question type called 'Formula' calculates and displays a value based on the data entered by the participant.
				It allows you to display a total or an average for example and adds an interactive aspect to your survey.
			</p>
			<img alt="screenshot formular element" src="${contextpath}/resources/images/documentation/formular_field_1.png" style="margin-bottom: 1em;" />
			<p>Main points of the question element:</p>
			<ul>
				<li>
					Ready-to-use functions are available for different use cases:<br>
					<img alt="screenshot formular element" src="${contextpath}/resources/images/documentation/formular_field_2.png" style="margin-bottom: 1em" />
				</li>
				<li>
					Users can also type their formula in the 'Formula' field:<br>
					<img alt="screenshot formular element" src="${contextpath}/resources/images/documentation/formular_field_3.png" style="margin-bottom: 1em" />
				</li>
			</ul>
			<p>Element IDs are used to compose the formula. The list of IDs is displayed to allow you to directly select the IDs you need.
				You can also enter them directly in the input field.</p>
			<img alt="screenshot formular element" src="${contextpath}/resources/images/documentation/formular_field_4.png" style="margin-bottom: 1em" />
			<p>Please note that element IDs are visible in the 'Advanced' section of the element properties.</p>
			<img alt="screenshot formular element" src="${contextpath}/resources/images/documentation/formular_field_5.png" style="margin-bottom: 1em" />
			<p><span style="text-decoration: underline;">Example 1:</span><br>
				In the example below, the 'Formula' field is the sum of the two 'Number-Slider' questions above.
				Therefore, as soon as the survey participant has entered the second value (5 in our example),
				the sum is calculated and displayed in real-time.
			</p>
			<img alt="screenshot formular element" src="${contextpath}/resources/images/documentation/formular_field_6.png" style="margin-bottom: 1em" />
			<p><span style="text-decoration: underline;">Example 2:</span><br>
				In the example below, you can ask your survey participant to enter either his monthly or yearly rent.
				The application will calculate automatically the other. Both fields remain editable by the user.
			</p>
			<img alt="screenshot formular element" src="${contextpath}/resources/images/documentation/formular_field_7.png" style="margin-bottom: 1em" />
			<p>
				<span style="font-style: italic;">Survey participant has entered 500 and 6000 has been calculated.</span>
			</p>
			<p><span style="text-decoration: underline;">Example 3 'Read only' option:</span></p>
			<img alt="screenshot formular element" src="${contextpath}/resources/images/documentation/formular_field_8.png" style="margin-bottom: 1em" />
			<p>In the example below, line 5 is showing the total for each column.
				Those fields use the 'Read only' option, so that, the total cannot be modified by the user.</p>
			<img alt="screenshot formular element" src="${contextpath}/resources/images/documentation/formular_field_9.png" style="margin-bottom: 1em" />
			<h2>Complex Table</h2>
			<p>'Complex Table' is a table-like survey element that allows you to compose other survey items in a more complex way.
				It enables visual linking of different questions and layout of text passages (e.g. displaying text in columns).</p>
			<img alt="screenshot complex_table element" src="${contextpath}/resources/images/documentation/complex_table_1.png" style="margin-bottom: 1em; width: 20%" />
			<p><b>How do I configure a 'Complex Table'?</b></p>
			<p>In the survey 'Editor' add a 'Complex Table' element and select it.
				The 'Element Properties' provide the same setting options as the regular 'Table' element.</p>
			<img alt="screenshot complex_table element" src="${contextpath}/resources/images/documentation/complex_table_2.png" style="margin-bottom: 1em; width: 30%" />
			<p><b>How do I configure the 'Complex Table' single cells?</b></p>
			<p>Select a single cell in the survey preview section.</p>
			<p>You can specify different question types into different cells. Those types correspond to the standard question types.</p>
			<img alt="screenshot complex_table element" src="${contextpath}/resources/images/documentation/complex_table_3.png" style="margin-bottom: 1em; width: 25%" />
			<p>'Static Text' can be displayed in columns by using the 'Column Span' property.
				Note that it can only span subsequent cells, thereby removing the contents of those cells if they were already configured.</p>
			<p>The other available cell types ('Free Text', 'Formula', 'Single Choice', 'Multiple Choice', 'Number')
				are basically the same as their regular counterparts outside a 'Complex Table' and can be edited in the same way.</p>
			<p><b>How to display text in columns?</b></p>
			<p>Text passages can be displayed in columns by splitting the text over several 'Complex Table' cells.
				Configure the type of the respective cells to 'Static Text'.
				Under 'Element Properties > Text' the desired text can now be entered for each cell individually.</p>
			<p><b>How can cells be configured so that they are not editable?</b></p>
			<p>The cell types of the 'Complex Table' that cannot be edited are 'Static Text' and 'Empty'.</p>
			<p>For other cell types, the 'Element Properties > Read only' can be used to prevent direct user input.</p>
			<p><b>How can a cell be configured so that its text spans multiple columns?</b></p>
			<p>Text in 'Complex Tables' can be configured to span across multiple columns. Select a cell with the 'Cell Type > Static Text'.
				Under 'Element Properties', the 'Column Span' option is responsible for how many columns are covered.
				Note that this function covers subsequent cells, thereby removing the contents of those cells if they were already configured.</p>
			<p><b>How can I delete a question in a 'Complex Table' cell?</b></p>
			<p>The content of cells in a 'Complex Table' cannot be removed individually using the editor's delete function.
				Instead, the 'Cell Type' of the cell must be reset to 'Empty'.</p>
			<p><b>How can I get a chart in the statistics screen?</b></p>
			<p>Min and Max values must be set; At most 10 values are possible.</p>
			<img alt="screenshot complex_table element" src="${contextpath}/resources/images/documentation/complex_table_4.png" style="margin-bottom: 1em; max-width: 920px" />
			<h1>
				Survey security
			</h1>
			<h2>
				How do I restrict access to my survey?    
			</h2>
			<p>
				By default, an EUSurvey online form is publicly available as soon as it has been published. You can
				change this and allow only privileged users to access the survey by setting the survey to
				<strong>'Secured'</strong> in the 'Security settings' on the 'Properties' page. You can then allow
				access to your privileged users by either:
			</p>
			<ul>
				<li>
					Inviting your participants with the EUSurvey invitation module (see '
					<a href="#_Toc_13_0">
						Inviting participants
					</a>
					'). Each participant will get a unique access link. OR
				</li>
				<li>
					Securing your survey by EU Login. In the 'Properties' page enable the options 'Secure your survey'
					and 'Secure with EU Login'. If you are a member of an EU body, you can choose either to allow all
					users with an EU Login account (members of the EU institutions and external EU Login accounts) to
					access your survey, or to grant access only to members of the EU institutions. OR
				</li>
				<li>
					Setting up a password. This will be the same for all participants and you will have to communicate
					it to your audience. Practically, you will send the survey location link and the global password
					(see '
					<a href="#_Toc_5_2">
						How do I set a password for my survey?
					</a>
					').
				</li>
			</ul>
			<h2>
				How do I set a password for my survey?    
			</h2>
			<p>
				To protect your survey with a password, edit the 'Secured with password' option under 'Properties'. To
				invite individual contacts to access your secured survey, please read the Help section '
				<a href="#_Toc_13_0">
					Inviting participants
				</a>
				'.
			</p>
			<h2>
				How do I ensure that an user does not submit more than a defined number of contributions to my survey?
			</h2>
			<p>
				To ensure that an user does not submit more than a defined number of contributions to your survey, in
				the 'Properties' page enable the options 'Secure your survey' and 'Secure with EU Login' and set the
				number in the 'Contributions per user' option.
			</p>
			<h2>
				What do I do to prevent robots from submitting multiple contributions to my survey?
			</h2>
			<p>
				Automatic scripts can falsify the outcome of an online survey by
				submitting a high number of contributions. To prevent this, you can
				use EUSurvey to make participants solve a
				<a href="https://en.wikipedia.org/wiki/CAPTCHA" target="_blank">CAPTCHA</a> challenge
				before submitting a contribution.
			</p>
			<p>
				You can enable/disable the CAPTCHA in the 'Security' settings under 'Properties'.
			</p>
			<p>
				N.B. Although this won't make fraud impossible, it might discourage people from continuing to try to
				falsify survey results.
			</p>
			<h2>
				Can I enable survey respondents to access their contribution after submission?
			</h2>
			<p>
				Yes. Click on 'Security' under the 'Properties' tab, and then, enable the <b>'Allow participants to change their contribution'</b> option.
			</p>
			<p>
				To edit/change their contribution after submission, participants can go to this page: <a href="https://ec.europa.eu/eusurvey/home/editcontribution" target="_blank">https://ec.europa.eu/eusurvey/home/editcontribution</a>
			</p>
			<p>
				Your survey participants will need to know their contribution ID. This ID is provided to them upon submission of their contribution on the Confirmation page.
			</p>
			<p>
				Please note that the 'Edit contribution' link is accessible on the EUSurvey landing page: <a href="https://ec.europa.eu/eusurvey/home/welcome" target="_blank">https://ec.europa.eu/eusurvey/home/welcome</a>
			</p>
			<h2>
				How can I allow participants to print or download their contribution?
			</h2>
			<p>
				If activated, this feature allows participants to save their answers in PDF format.
			</p>
			<p>
				To activate this feature, follow the steps below:
			<ol>
				<li>
					Open your survey
				</li>
				<li>
					Go to 'Properties' tab
				</li>
				<li>
					Go to 'Security' tab
				</li>
				<li>
					Enable 'Allows participants to print their contribution and receive it as a PDF' option
				</li>
			</ol>
			<img alt="screenshot print view" src="${contextpath}/resources/images/documentation/printdownload.png" />
			</p>
			<h2>
				How can I allow participants to change (edit) their contribution?
			</h2>
			<p>
				If activated, this feature allows participants to change/edit their answers after being submitted.
			</p>
			<p>
				To activate this feature:
				<ol>
					<li>
						Open your survey
					</li>
					<li>
						Go to 'Properties' tab
					</li>
					<li>
						Go to 'Security' tab
					</li>
					<li>
						Enable 'Allows participants to change their contribution' option
					</li>
				</ol>
				<img alt="screenshot change contribution" src="${contextpath}/resources/images/documentation/changecontribution.png" />
			</p>
			<h1>
				Testing a survey
			</h1>
			<h2>
				Can I see how my survey will behave after it has been published?
			</h2>
			<p>
				Yes. Please open the survey in EUSurvey and click on 'Test'. You will see the current draft of your
				survey, and you can access every feature of the published form. You can save the test as a draft or
				directly submit it as your contribution.
			</p>
			<h2>
				How can my colleagues test my survey before it is published?
			</h2>
			<p>
				The 'Test'-page of your survey can also be tested by your colleagues. To give access to this page, open
				your survey in EUSurvey, go to the 'Privileges' tab and click on 'Add User' or 'Add Department'. A
				wizard will guide you through the process of adding your colleagues. To give them appropriate access
				rights for testing, change the colour of 'Access Form Preview' to 'Green'. Simply click on the colour
				to change the rights.
			</p>
			<p>
				The added users will automatically see the survey in their 'Surveys' page once they log into the EUSurvey
				application. Read more about this under '
				<a href="#_Toc_9_8">
					How do I give other users access to my survey?
				</a>
				'
			</p>
			<p>
				External owners or survey organizers cannot see EU domains on the Privileges tab / Add users button,
				therefore, they cannot provide access for these persons directly. Please
				<a href="https://ec.europa.eu/eusurvey/home/support">contact us</a>
				to request privileges for external users.
			</p>
			<h1>
				Translations
			</h1>
			<h2>
				How do I translate a survey?    
			</h2>
			<p>
				EUSurvey offers various ways to make the survey available in multiple languages.
				Important: Be sure to finish editing and testing your survey before
				starting on the translations!
			</p>
			<p>
				Open your survey and go to the 'Translations' page. Click on 'Add
				New Translation'. Select the language from the list of supported
				languages. If the required language does not appear in the list,
				select 'other' and specify a valid two-letter ISO 639-1 language
				code. Click 'OK' to add the empty translation form to your survey.
				Please read '
				<a href="#_Toc_7_3"> Can I edit an existing translation online?
				</a> ' for more information on how to add new labels to your newly
				created translation.
			</p>
			<p>
				Don't forget to select the box 'To Publish' if the translation is to be published along with your survey.
				Once you add a translation for publishing, participants can choose from the available languages directly
				from the survey link.
			</p>
			<h2>
				How can I upload an existing translation to my survey?    
			</h2>
			<p>
				Open your survey and open the 'Translations' page. Click on
				'Upload existing translation'. A wizard will guide you through the
				process of uploading the translation.
			</p>
			<h2>
				Can I edit an existing translation online?    
			</h2>
			<p>
				Yes! Open your survey, go to the 'Translations' page and select
				one or more translations to edit. Select 'Edit Translations' from
				the action menu just above the list of available translations and
				click on the 'OK' button. This will open the online translation
				editor that will allow you to edit multiple translations at a time.
				Please click the 'Save' button to make sure that your changes are
				written to the system.
			</p>
			<p>
				In order to edit only one translation, open your survey, go to the
				'Translations' page and click on the pen icon in the 'Actions'
				column.
			</p>
			<h2>
				Can I create my translations offline?    
			</h2>
			<p>
				Yes! Open your survey, go to the 'Translations' page and export your survey as an XLS, ODS or XML file
				to perform the translation offline. The translation can then be imported back into your survey.
			</p>
			<p>
				The usual workflow is to export a language version with status
				'Complete' and then translate all available text labels into the new
				language. Ensure that the new language code is specified at the
				beginning of the form to ensure that the system recognizes the
				language of your translation. Once the survey has been translated
				offline, click 'Upload existing translation' to add it to the
				system. To ensure that no translation is overwritten by accident,
				you will have to specify the language version you are about to
				upload. For security reasons you can select individual labels to
				replace if you do not want all labels to be taken into account.
			</p>
			<h2>
				How do I publish/unpublish my translations? Why can't I publish this translation? What is an
				'Incomplete' translation?
			</h2>
			<p>
				To publish a survey in multiple languages, open your survey, go to
				the 'Translations' page and tick/untick the individual translations
				you want to publish/unpublish under 'To Publish'. Then change to the
				'Overview' page of your survey to publish your survey. If the survey
				had been published before the translations were added/removed, click
				on 'Apply Changes'.
			</p>
			<p>
				To ensure that no translations with missing text are
				published, you cannot publish translations that have empty labels
				(translations that are not 'Complete'). Please make sure your
				translation has no empty labels by using the online translation
				editor. Look for cells with a red background.
			</p>
			<h2>
				Can I upload a translation in a non-European language?    
			</h2>
			<p>
				The application also supports non-European languages. Select 'Other'
				in the uploading process and introduce a valid two-letter
				<a href="https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes">
					ISO 639-1 </a> language code. It is not possible to select a
				non-European language as the main language of your survey.
			</p>
			<h2>
				What does 'Request Machine Translation' mean?    
			</h2>
			<p>
				EUSurvey can provide automatic translations of your questionnaire by using a <strong>Machine Translation
				(MT)</strong>. The application uses the MT@EC service provided by the European Commission.
			</p>
			<p>
				On the 'Translations' page, there are several ways to request machine translation:
			</p>
			<ul>
				<li>
					When adding a new translation, tick the checkbox 'Request Machine Translation'
					(for a translation of your survey's main language)
				</li>
				<li>
					Click the 'Request Translations' button in the 'Actions' column (for a
					translation of your survey's main language)
				</li>
				<li>
					Select all languages you want to be translated (including at
					least one complete translation). Then select 'Request Translations'
					from the select box below your translations and click 'OK'.
				</li>
			</ul>
			<p>
				The status of the translations will change to 'Requested', until they are
				finished. To see when the status has changed, check the 'Translations' page.
			</p>
			<p>
				Machine translations will behave like other translations you added manually,
				i.e. they will not be published automatically, and adding new elements to your
				survey will make them incomplete (to complete them, you'll have to request
				a new translation).
			</p>
			<p>
				<em>
					We cannot guarantee the quality of the resulting text or the delivery
					time for translations.
				</em>
			</p>
			<p>
				<a
						href="https://webgate.ec.europa.eu/etranslation/help.html"
						target="_blank"
						>
					Machine translation - Help
				</a>
				(EU staff only).
			</p>
			<h2>
				Instructions for EU staff    
			</h2>
			<p>
				You are encouraged to contact DGT before you finalise your survey. Its Editing unit (email: DGT-EDIT)
				will check that your survey is clearly drafted and presented. For more info, see
				<a
						href="https://myintracomm.ec.europa.eu/serv/en/dgt/Pages/index.aspx"
						target="_blank"
						>
					DGT's MyIntraComm pages
				</a>
				.
			</p>
			<p>
				You can also get your survey translated into the official EU
				languages by DGT. First export it as an XML file and send it via
				Poetry with the requester code of the DG concerned. It should be a
				maximum of 15 000 characters, excl. spaces (Word count).
			</p>
			<h1>
				Publishing a survey
			</h1>
			<h2>
				How do I publish my survey?
			</h2>
			<p>
				To publish a survey from a current working draft, please go to the 'Overview' page and click on
				'Publish'. After confirmation, the system will automatically create a working copy of your survey
				and put it online, along with the set of translations you have selected for publication on the
				'Translations' page (See '
				<a href="#_Toc_7_5">
					How do I publish/unpublish my translations?
				</a>
				'). You will find the link to your published survey in 'Survey Location' on the 'Overview' page.
			</p>
			<p>
				To unpublish your survey, just click the 'Unpublish' button. The unpublished survey will stay available
				to you in the form in which it was published, along with your current working draft. This means that the
				unpublished survey does not have to be replaced by your current working draft but can be republished if
				necessary.
			</p>
			<h2>
				Can I customise the URL to my survey?    
			</h2>
			<p>
				Yes! By changing your survey's 'Alias' you can make the URL to
				your survey more meaningful. Open the survey and go to the
				'Properties' page. Change your survey's alias under the 'Basic'
				section. An alias can only contain alphanumeric characters and
				hyphens. If you change the alias of a published survey, go to the
				'Overview' page and click on 'Apply Changes'.
			</p>
			<p>
				Please note that aliases have to be unique in EUSurvey. You will get a warning if your alias has already
				been taken by another survey.
			</p>
			<h2>
				Can I link directly to a translation of my survey?    
			</h2>
			<p>
				When you send out invitations containing the link or use the link to the
				published form on the 'Overview' page, by default the link points to the
				form in the main language.
			</p>
			<p>
				You can also<strong> redirect respondents directly</strong> to a specific
				translation by using this link:
			</p>
			<p>
				https://ec.europa.eu/eusurvey/runner/<strong>SurveyAlias</strong>?surveylanguage=<strong>LC</strong>
			</p>
			<p>
				Just replace:
			</p>
			<ul>
				<li>
					<strong>SurveyAlias</strong> with the <strong>alias of your survey</strong>
				</li>
				<li>
					<strong>LC</strong> with the appropriate <strong>ISO 639-1 language code</strong> (e.g. FR for
					French, DE for German, etc.)
				</li>
			</ul>
			<h2>
				How do I make my survey publish itself while I am on holiday?
			</h2>
			<p>
				You can schedule your survey to be published automatically at any
				time. Open the survey and go to the 'Properties' page. Enable the
				'Automatic survey publishing' option under the 'Advanced' section,
				then specify the start- and expiry-date of your survey.
			</p>
			<h2>
				Can I receive a reminder of when my survey will end?    
			</h2>
			<p>
				EUSurvey can send you an email reminder when your survey is about to end. This
				allows you to prepare the next steps (e.g. organising resources for result
				analysis).
			</p>
			<p>
				To enable this option, open the survey and go to the
				'Properties' page. Select 'Advanced Settings' section and, click on
				the 'Edit' button and enable 'End Notification', specifying how long
				in advance you expect an email. Click on 'Save'. The reminder email
				will be delivered to all form managers
			</p>
			<h2>
				For EU staff: What are the official requirements for launching an open public
				consultation ('Your Voice in Europe' website)?   
			</h2>
			<p>
				Please carefully follow
				<a
						href="https://circabc.europa.eu/sd/d/fc02d2ac-d94f-42ed-b866-b3429e0d717b/Survey_publication_your_voice_in_europe_NEW.pdf"
						target="_blank"
						>
					the procedure
				</a>
				from the SG for launching an open public
				consultation on the
				<a
						href="https://ec.europa.eu/yourvoice/consultations/index_en.htm"
						target="_blank"
						>
					Your Voice in Europe
				</a>
				website.
			</p>
			<h1>
				Managing your survey
			</h1>
			<h2>
				If I discover an error in my survey, can I correct it?    
			</h2>
			<p>
				Yes, you can edit and change the survey as often as you want and
				add or change any additional (dependent) question. However, please
				note that the more changes you make the less usable the collected
				data will be, given that different participants in your survey might
				have responded to different surveys. So, to make sure that you can
				still compare the full set of answers, it is recommended that you
				avoid changing the structure of your survey at all. Please note that
				you are fully responsible for every change you apply to your survey
				during its lifetime.
			</p>
			<p>
				If you do want to change an already published survey, please
				remember to click on 'Apply Changes' on the survey's 'Overview' page
				to make sure the changes are visible in the published survey.
			</p>
			<p>
				If you want to remove answers from your survey, please Read: 'Will I
				lose any answers submitted when I change my form?'.
			</p>
			<h2>
				Will I lose any submitted answers when I change my form?    
			</h2>
			<p>
				No answers are lost, unless you delete your survey from the
				system. However, you might not be able to visualize the full set of
				collected data if you have removed individual questions from your
				survey during the active period of your survey. This is because the
				search mask always represents the most recently published form only.
				Please select the 'Contributions (including deleted questions)'
				option from the drop-down list on the Results tab to visualize all
				answers, even those to questions that were removed during the active
				period of your survey.
			</p>
			<h2>
				How can I change the title of my survey?    
			</h2>
			<p>
				Open the survey and go to the 'Properties' page. Change your survey's
				title under 'Basic' section. If you have already published your
				survey, go to the 'Overview' page and click 'Apply Changes'.
			</p>
			<h2>
				How can I change my survey's contact information?    
			</h2>
			<p>
				Open the survey and go to the 'Properties' page. You can choose
				between the following 'Contact' options under 'Basic' settings:
			</p>
			<p>
				<ul>
					<li>
						Contact Form: This allows users to contact you using a form.
					</li>
					<li>
						E-mail: This allows users to contact you directly by email (your email will be
						disclosed).
					</li>
					<li>
						Web-page: This directs users to a specific webpage.
					</li>
				</ul>
			</p>
			<p>
				If you have already published your survey, go to the 'Overview' page and
				click 'Apply Changes'.
			</p>
			<h2>
				How do I adjust the default confirmation message?    
			</h2>
			<p>
				The confirmation message is what participants see when they
				have submitted their contribution. To change the default message,
				open the survey, go to the 'Properties' page and change the
				confirmation message under 'Special Pages'. If you have already
				published your survey, please remember to go to the 'Overview' page
				and click on 'Apply Changes'.
			</p>
			<h2>
				How do I adjust the default escape message?    
			</h2>
			<p>
				The escape page contains the message that your participants
				see if your survey is not available. To change the default message,
				open the survey, go to the 'Properties' page.
				You will find the 'Unavailability Page' option under 'Special Pages'. If you have already
				published your survey, please remember to go to the 'Overview' page
				and click on 'Apply Changes'.
			</p>
			<h2>
				Archiving feature
			</h2>
			<p>
				You can archive your survey with all its submitted answers to
				reload it or to launch the same survey again at a later point in
				time. To archive your survey, select the 'Archive Survey' icon in
				the action menu of the 'Overview' page.
			</p>
			<p>
				Archived surveys cannot be edited or collect any data. But you
				can export results or request a PDF-file of your survey. The
				archived survey will be available from the 'Dashboard' page, from
				where it can also be restored. A restored survey can be edited
				again.
			</p>
			<h2>
				How do I give other users access to my survey?
			</h2>
			<p>
				In EUSurvey, you can grant access to other users for different purposes:
			</p>
			<ul>
				<li>
					Testing the survey ('Access Form Preview')
				</li>
				<li>
					Accessing the results ('Results')
				</li>
				<li>
					Editing the survey ('Form Management')
				</li>
			</ul>
			<p>
				In order to provide them access, open your survey and go to the
				'Privileges' page. You can grant access to a person or a department.
				The following access rights are possible:
			</p>
			<p>
				<ul>
					<li>
						Green: Read and write access
					</li>
					<li>
						Yellow: Read access
					</li>
					<li>
						Red: No access
					</li>
				</ul>
			</p>
			<p>
				In order to proceed, please click on 'Add user' or 'Add
				department' on the 'Privileges' page. A window will pop up that
				guides you through the process of adding users.
			</p>
			<p>
				By clicking on 'Add user', you will have to select the correct
				domain (i.e. European Commission), then enter the login, E-mail
				address or any other field and click on 'Search'. Then select the
				user and click on 'Ok'.
			</p>
			<p>
				By selecting 'Add department', select the correct domain and
				navigate to the right department. Then click on 'Ok'.
			</p>
			<p>
				External owners or survey organizers cannot see EU domains on
				the Privileges tab / Add users button, therefore, they cannot
				provide access for these persons directly. Please contact us to
				request privileges for external users.
			</p>
			<p>
				You will then be redirected to the 'Privileges' page where you
				can set the right permissions by clicking on the red icons:
			</p>
			<ul>
				<li>
					To grant the right to test your survey:<br> Change the colour of
					'Access Form Preview' to 'Green'. Simply click on the colour to
					change the rights. The added users will automatically see the
					survey in their 'Surveys' page once they log in to the EUSurvey
					application (see also '<a href="#_Toc_6_2"> How can my
						colleagues test my survey before it is published? </a> ').
				</li>
				<li>
					To grant the right to access the results of your survey:<br>
					Change the colour of 'Results' to 'Yellow'. The users can only view
					the results, but cannot edit or delete anything. If you change the
					colour to 'Green', they can view, edit and delete the answers (see
					also '<a href="#_Toc_10_7"> How do I give other users access
						to my survey's results? </a> ').
				</li>
				<li>
					To grant the right to edit your survey:<br> By changing the
					colour to 'Yellow', the privileged users can only view your
					survey. But if you select 'Green', they can also edit it. They
					will automatically see your survey in their list of surveys (see
					also ' <a href="#_Toc_4_11"> How do I give other users
						permission to edit my survey? </a> ').
				</li>
				<li>
					To grant the access right to manage invitations of your
					survey:<br> By changing the colour to 'Yellow', the privileged users
					can only view your invitations. But if you select 'Green', they
					can also edit it. They will automatically see your survey in their
					list of surveys (see also '<a href="#_Toc_4_11"> How do I give
						other users permission to edit my survey? </a> ').
				</li>
			</ul>
			<p>
				If you set all 4 circles to 'Green', the privileged user will
				have full rights permission to your survey.
			</p>
			<h2>
				What are Activity logs?
			</h2>
			<p>
				Activity logs monitor and log the activity on your survey. This way,
				you can check which user applied which change to your survey and at
				what time. You can also export the activity logs into several file
				formats such as xls, csv and ods. Enter the activity log of your
				survey by clicking on the 'Activity' page, next to 'Properties'. If
				the activity logs are empty, it may be that they are deactivated
				system-wide. Find <a
					href="https://ec.europa.eu/eusurvey/resources/documents/ActivityLogEvents.xlsx">
					here </a> a list of the logged events.
			</p>
			<h1>
				Analysing, exporting and publishing results
			</h1>
			<h2>
				Where can I find the contributions submitted by my respondents?
			</h2>
			<p>
				Open your survey in EUSurvey (also see '<a href="#_Toc_3_5"> How
					do I open an existing survey for editing etc.? </a>') and go to the
				'Results' page. Initially, you'll see the full content of all
				submitted contributions in a table. You can view the results in 2
				different ways:
			</p>
			<ul>
				<li>
					Full Content
				</li>
				<li>
					Statistics
				</li>
			</ul>
			<p>
				You can switch view modes by clicking the icons in the upper
				left corner of the screen.
			</p>
			<h2>
				How can I download submitted contributions?
			</h2>
			<p>
				To export submitted answers from EUSurvey to your computer,
				open your survey and go to the 'Results' page. Click on the
				'Export' button in the top right-hand corner of the page, select
				from the available export file formats and specify a name in the
				dialogue box. Under this name the export file will appear on the
				'Export' page. Different export file formats are available,
				depending on the view mode (Full Content/ Statistics). N.B. The
				export file will contain the set of configured questions only as
				well as the current search results of a filter process.
			</p>
			<h2>
				How can I extract the Draft answers?
			</h2>
			<p>
				On your Dashboard you can see the number of the saved Draft
				contributions for your survey. Your participants have the option to
				save their work as a Draft and continue the filling in a later time.
				According with our privacy policy, as a form manager you are not
				allowed to extract the Draft answers.
			</p>
			<h2>
				How can I access and analyse a defined subset of all contributions?
			</h2>
			<p>
				On the 'Results' page (see '<a href="#_Toc_10_1"> Where can I
					find the contributions submitted by my respondents? </a>') search for
				keywords in free-text answers or select individual answers from
				choice-questions in the filter bar. This limits the full set of
				answers to a subset of contributions. For performance reasons you
				can only set a maximum of 3 filters! At any point you can change the
				view mode, so you can carry out an advanced statistical analysis of
				the data collected. Note: to view and analyse results, you need
				certain privileges (see '<a href="#_Toc_10_7"> How do I give
					other users access to the results of my survey? </a>'). To export a
				subset of contributions, please read '<a href="#_Toc_10_2">How
					can I download submitted contributions?</a>'.
			</p>
			<h2>
				How can I get back to the full set of answers, after
				defining a subset of contributions?
			</h2>
			<p>
				To visualize the full set of answers, click the 'Reset' button
				at the top of the 'Results' page or deactivate every search you've
				performed on the filter-bar on this page.
			</p>
			<h2>
				How can I publish my results?
			</h2>
			<p>
				Open the survey, go to the 'Properties' page and select
				'Publish Results' section. Here you will find the URL of the
				published results and will be able to choose which
				questions/answers/contributions you would like to publish. You can
				also go there directly by clicking on 'Edit Results Publication'
				from the 'Overview' page of your survey.
			</p>
			<p>
				Please be sure to select something in 'Publish Results'
				under 'Publish', otherwise the system will not publish any
				results.
			</p>
			<h2>
				How can I access the published results?
			</h2>
			<p>
				Open the 'Overview' page and click on the 'Published'
				hyperlink right next to the word 'Results'. This will direct you
				to the published results. Everybody who knows this address can
				access your results.
			</p>
			<h2>
				How do I give other users access to the results of my survey?
			</h2>
			<p>
				Open your survey, go to the 'Privileges' page and give results
				access to other users. Read more about this under '<a
					href="#_Toc_10_7"> How do I give other users access to my
					survey? </a>'.
			</p>
			<h2>
				I cannot unzip my exported files
			</h2>
			<p>
				This might happen if the name of the files contained in your
				folder is too long. Windows has a maximum length of 260 characters
				for directory locations on the hard drive. Possible solutions for
				this are:
			</p>
			<ul>
				<li>
					Unzip the folder in the root directory of your operating system, e.g.
					unpack at 'C:' instead of 'C:\Users\USERNAME\Desktop'
				</li>
				<li>
					Or when unpacking the files, rename the target folder to shorten the
					directory length.
				</li>
			</ul>
			<h2>
				Published results - protection of personal information uploaded by participants
			</h2>
			<p>
				For data protection rules, the form manager has to actively
				make the choice to publish files uploaded to a contribution by the
				participant along with the other results. To do so, tick the
				'Uploaded elements' check box in the corresponding section on the
				'Properties' page under 'Publish Results' section. Please note that
				this check box will only appear if your survey contains an 'uploaded
				element'.
			</p>
			<h2>
				How do I design a survey to publish the results with or without personal information?
			</h2>
			<p>
				If you want to let your participants choose, whether their personal
				information will be published along with their answers, follow <a
					href="https://circabc.europa.eu/sd/d/e68ff760-226f-40e9-b7cb-d3dcdd04bfb1/How_to_publish_survey_results_anonymously.pdf"
					target="_blank"> these instructions </a> to build the survey
				to fit these requirements.
			</p>
			<h2>
				Why are my results not up-to-date?
			</h2>
			<p>
				A new database has been introduced which shall improve
				EUSurvey's performance when querying your survey's results. However,
				this can lead to some delays until the latest data is displayed on
				the Results page of your survey. This delay should not be more than
				12 hours.
			</p>
			<p>
				If the displayed data is older than 12 hours, please contact
				EUSurvey&nbsp;<a href="https://ec.europa.eu/eusurvey/home/support">support
				</a>.
			</p>
			<h2>How can I retrieve files uploaded
				by contributors?</h2>

			<p>EUSurvey offers different formats of export: XLS, PDF, ODS and XML. <br>

				Depending on the selected format, the structure and content of the
				exported files for the 'File Upload' element is as described below:</p>

			<h4>Results export in XLS</h4>

			<ol>
				<li>
					<p>An Excel file containing the following information:</p>

					<p>Alias: Survey Alias (example: 6459a3c9-e517-4a34-8e5d-70185db022c3)<br>
						Export Date: Date in the format 'dd-mm-yyyy hh:mm' (example: 28-09-2020
						15:28)</p>

					<p>A table composed as below:</p>
					<ul>
						<li>Each column represents a different 'File Upload'
							question.</li>

						<li>Each line represents a different contribution.</li>

						<li>Each cell contains all names of the uploaded
							files.</li>
					</ul><br>
				</li>
				<li>
					<p>Folders corresponding to each contribution and named with the
						contribution ID. It contains sub-folders for each 'File Upload' question (Upload_1,
						Upload_2 etc.). </p>
					<p>For instance:</p>

					<p>Folder: 6cf0463c-29f4-4bea-a195-10e77c61dda1<br>

						Sub-folder: Upload_1 (corresponding to the first 'File Upload'
						question) contains all files uploaded.<br>

						Sub-folder: Upload_2 (corresponding to the second 'File Upload'
						question) contains all files uploaded.</p>
				</li>
			</ol>
			<h4>Results export in PDF</h4>

			<ol>
				<li>
					<p>Folder named 'PDFs' containing all survey contributions as PDF
						documents.</p>
				</li>
				<li>
					<p>Folders corresponding to each contribution and named with the contribution
						ID.<br>
						It contains sub-folders for each 'File Upload' question (Upload_1,
						Upload_2 etc.).</p>
				</li>
			</ol>


			<h4>Results export in ODS</h4>

			<ol>
				<li>
					<p>An Open Office file containing the following information:</p>

					<p>Alias: Survey Alias (example: 6459a3c9-e517-4a34-8e5d-70185db022c3)<br>
						Export Date: Date in the format 'dd-mm-yyyy hh:mm' (example: 28-09-2020 15:28)</p>

					<p>A table composed as below:</p>

					<ul>
						<li>Each column represents a different 'File Upload'
							question.</li>

						<li>Each line represents a different contribution.</li>

						<li>Each cell contains all names of the uploaded
							files.</li>
					</ul><br>
				</li>
				<li>
					<p>Folders corresponding to each contribution and named with the
						contribution ID.<br>
						It contains sub-folders for each 'File Upload' question (Upload_1,
						Upload_2 etc.).</p>
				</li>
			</ol>

			<h4>Results export in XML</h4>

			<p>This export is made of an XML file containing the results in a structured way.<br>

				<b>Uploaded files are not available in that case.</b></p>

			<h2>How the ranking question score are calculated?</h2>
			<p>
				Ranking questions are used to offer the possibility to your survey participants to rank a set of items per order of importance.
				It is recommended to limit items to rank to 5.
				If you ask more to your survey participants it might be hard for them to properly rank all the items.
			</p>
			<p>
				Survey respondents' most preferred choice (which they rank first) get the highest weight, and the least preferred choice (which they rank last) get a weight of 1.
				The weight is therefore proportionally reversed with respect to the ranking of the item.
			</p>
			<p>
				For example, if a Ranking question is made of 5 items, weights are assigned as follows:
			</p>
			<ul>
				<li>The top ranked item has a weight of 5</li>
				<li>The second item has a weight of 4</li>
				<li>The third item has a weight of 3</li>
				<li>The fourth item has a weight of 2</li>
				<li>The fifth item has a weight of 1</li>
			</ul>
			<p>
				The score is calculated as being the average weight given by survey respondents.
			</p>
			<h2>
				Export the dataset (i.e. survey's answers)
			</h2>
			<p>
				You can export your survey's answers using the export feature.
				This can be useful for further data processing in Excel for example.
			</p>
			<p>
				To export your answers:
			</p>
			<ol>
				<li>Go to the 'Results' tab (first screen, the tabular view).</li>
				<li>Click on the 'Export' button (a popup opens).</li>
				<li>Enter a name for your export.</li>
				<li>Select the file format (when export file is generated, you will receive a notification).</li>
				<li>Go to the 'Exports' tab.</li>
				<li>Download your file.</li>
			</ol>


			<h1>
				Design and layout
			</h1>
			<h2>
				How do I change the general look and feel of my survey?    
			</h2>
			<p>
				Open your survey, go to the 'Properties' page and select
				'Appearance' section. Choose a new survey skin from the available
				skins under 'Skin' using the drop-down menu. Click on 'Save'. If you
				have already published your survey, please remember to go to the
				'Overview' page and click on 'Apply Changes'.
			</p>
			<h2>
				How can I create my own survey themes?    
			</h2>
			<p>
				On EUSurvey's 'Settings' page, at the top of your screen,
				select 'Skins' and click on 'Create a new Skin'. This will open the
				skin editor for survey themes. You can copy an existing theme as a
				basis and use the online skin editor to modify this template as
				necessary.
			</p>
			<h2>
				How do I add a logo to my survey?    
			</h2>
			<p>
				To let your project/company logo appear in the top right-hand
				corner of your survey, upload an image file to the 'Appearance'
				section on the 'Properties' page. If you have already published your
				survey, please remember to go to the 'Overview' page and click on
				'Apply Changes'.
			</p>
			<h2>
				How do I add useful links to my survey?    
			</h2>
			<p>
				Open your survey, go to the 'Properties' page and select
				'Advanced' section. Add labels and URLs under 'Useful links'. These
				links will appear on every page on the right-hand side of your
				survey. If you have already published your survey, please remember
				to go to the 'Overview' page and click on 'Apply Changes'.
			</p>
			<h2>
				Where do I upload background documents for my survey?    
			</h2>
			<p>
				Open your survey, go to the 'Properties' page and select
				'Advanced' section. Upload a file under 'Background Documents'.
				These documents will appear on every page on the right-hand side of
				your survey. If you have already published your survey, please
				remember to go to the 'Overview' page and click on 'Apply Changes'.
			</p>
			<h2>
				How do I create a multi-page survey?    
			</h2>
			<p>
				Top-level sections of your survey can be divided into
				individual pages automatically. Open your survey, go to the
				'Properties' page and select 'Appearance' section. Enable
				'Multi-Paging' and click on 'Save'. If you have already published
				your survey, please remember to go to the 'Overview' page and click
				on 'Apply Changes'.
			</p>
			<h2>
				How do I enable automatic numbering for my survey?    
			</h2>
			<p>
				To add auto-numbering to all sections and question elements of
				your form, open your survey, go to the 'Properties' page and select
				'Appearance' section. Enable and select your preferences for
				'Automatic Numbering Sections' and/or 'Automatic Numbering
				Questions', finally click on Save. If you have already published
				your survey, please remember to go to the 'Overview' page and click
				on 'Apply Changes'.
			</p>
			<h2>
				Can I create a customised skin for my survey?    
			</h2>
			<p>
				To create a new skin for your survey, go to the 'Settings'
				page and select 'Skins'. Open the 'Create a new skin' tab where you
				can change the look of different elements of your survey: question
				and answer text, survey title, help text and many more.
			</p>
			<p>
				First give a name to your new skin. Then select the item that
				you want to skin. On the right side of the screen you find a box
				where you can change the font of your item: foreground and
				background colour, font style, font family, font size and font
				weight. Below, in the 'Skin Preview Survey', you can immediately see
				how the modified item looks like in your survey. Then click on
				'Save'.
			</p>
			<p>
				If you want to change several items, you can modify one after
				the other and save them at the end when all items are finalised. It
				is not necessary to do a save after each modified item.
			</p>
			<p>
				To adapt your survey to your new skin, go to the 'Properties'
				page and select 'Appearance' section. Choose your new skin in the
				'Skin' drop-down menu. Then click on 'Save'.
			</p>
			<h1>
				Managing contacts and invitations
			</h1>
			<h2>
				What is the 'Address Book'?    
			</h2>
			<p>
				In the 'Address Book' you can create your own groups of participants.
				This way you can invite people or organisations who match certain criteria
				(e.g. 'male', and 'older than 21').	Every potential participant is stored
				as a contact in the address book along with an unlimited list of editable
				attributes. You can save any contact to your address book as long as they
				have an identifier ('Name') and an email address.
			<h2>
				What are the 'Attributes' of a contact?
			</h2>
			<p>
				Each contact saved in the address book has a variable set of attributes
				such as 'Country', 'Phone', 'Remarks', etc. You can create a new attribute
				by editing a contact.
			</p>
			<p>
				<ol>
					<li>
						In the 'Edit Contact' window, open the 'attributes' menu and select 'New...'.
					</li>
					<li>
						A new window will pop up where you can define the name of the new attribute.
					</li>
					<li>
						The newly created attribute will appear as a column in the address book
						and can also be added to a set of contacts.
					</li>
				</ol>
			</p>
			<h2>
				How do I add new contacts to my address book?    
			</h2>
			<p>
				Go to the 'Address Book' page and click on 'Add Contact' to add a single contact.
				You can click on 'Import' to upload an existing list of contacts in XLS,
				ODS, CSV or TXT format.
			</p>
			<p>
				See also '
				<a href="#_Toc_12_5">
					How do I import multiple contacts from a file to my address book?
				</a>
				'
			</p>
			<h2>
				What is a 'Registration Form'?    
			</h2>
			<p>
				A registration form can be understood as a survey that automatically creates
				contacts from the personal data that participants submit.
			</p>
			<p>
				To enable this feature:
			</p>
			<p>
				<ol>
					<li>
						Open your survey.
					</li>
					<li>
						Go to the 'Properties' page and select 'Advanced'.
					</li>
					<li>
						Enable 'Create Contacts' and click 'Save'.
					</li>
				</ol>
			</p>
			<p>
				When this is selected, the system inserts 2 compulsory free-text questions
				('Name' and 'Email') to ensure that every participant enters valid personal
				data.
			</p>
			<p>
				By enabling the 'Attribute' option for individual questions, you can
				choose what other information is stored about the newly created contact
				(e.g. a text-question with the attribute 'Telephone' can be used to store
				the participant's phone number in the address book).
			</p>
			<h2>
				How do I import multiple contacts from a file to my address book?    
			</h2>
			<p>
				To import a list of contacts into the system, EUSurvey offers a wizard that
				guides you through the import procedure.
			</p>
			<p>
				The following file formats are supported:
			</p>
			<ul>
				<li>
					XLS
				</li>
				<li>
					ODS
				</li>
				<li>
					CSV
				</li>
				<li>
					TXT (with separator).
				</li>
			</ul>
			<p>
				To start the wizard:
			</p>
			<p>
				<ol>
					<li>
						Select 'Import' from your 'Address Book' page.
					</li>
					<li>
						Select the file in which you have saved your contacts.
					</li>
					<li>
						Specify if your file contains a header row or not.
					</li>
					<li>
						Specify the type of separator you used for CSV and TXT files (the
						most probable character is suggested by default).
					</li>
				</ol>
			</p>
			<p>
				As a second step:
			</p>
			<p>
				<ol>
					<li>
						The system will ask you to map the individual columns to new attributes
						for your contacts (the mandatory attributes 'Name' and 'Email' must be
						mapped in order to proceed).
					</li>
					<li>
						Once you click 'Next', the system loads your file into the system,
						displaying the individual contacts that will be imported.
						You can unselect individual contacts you don't want to import
					</li>
					<li>
						Click 'Save' to save your contacts to your address book.
					</li>
				</ol>
			</p>
			<h2>
				How do I edit an attribute for multiple contacts at a time?    
			</h2>
			<p>
				To edit an attribute value for multiple contacts:
			</p>
			<p>
				<ol>
					<li>
						Search and select the contacts in your address book.
					</li>
					<li>
						Select 'Bulk Edit' and click 'OK'.
					</li>
					<li>
						In the pop-up, you can choose to keep, clear or set values for multiple
						contacts - by default, only the configured attributes will be shown.
					</li>
					<li>
						Click on the green cross to view other attributes.
					</li>
					<li>
						After clicking 'Update' and confirming the security message, the
						application will save your changes to your address book.
					</li>
				</ol>
			</p>
			<h2>
				Can I export contacts from my address book to my computer?    
			</h2>
			<p>
				Yes, on your 'Address Book' page, please click an icon in the top right-hand
				corner representing individual file formats.
			</p>
			<p>
				You will find the exported contacts on the 'Exports' page.
			</p>
			<h1>
				Inviting participants
			</h1>
			<h2>
				How do I specify a set of possible participants? What is a 'Guest List'?
			</h2>
			<p>
				In EUSurvey you can group selected contacts and send out individual emails to every single contact containing individual access links. This is called a 'Guest List'. It is the second way, in addition to the general survey password, in which you can give people the opportunity to complete your survey.
			</p>
			<p>
				To invite multiple contacts to your survey, open your survey and go to the 'Participants' page. Choose one of the following types of guest lists to start a wizard that will guide you through the process:
			</p>
			<ul>
				<li>
					<strong>Contact list</strong>
					'Address Book'
					<br/>
					Select contacts from the 'Address Book' (see '
					<a href="#_Toc_12_1">
						What is the 'Address Book'?
					</a>
					') to add them to your guest list
					
				</li>
				<li>
					<strong>EU list</strong>
					'EU institutions and other bodies' (EU staff only)
					<br/>
					Select multiple departments from your institution/agency to add
					all persons working in those departments to your guest list
				</li>
				<li>
					<strong>Token list</strong>
					<br/>
					Create a list of tokens (or 'Unique Codes') that can be distributed
					offline to access a secured online survey
				</li>
			</ul>
			<p>
				Please use the search functionality on your address book and click the '&gt;&gt;'
				button on the middle of the next screen to move contacts from your address book to your new guest list. Clicking 'Save' will create a new guest list with all the contacts you want to invite to take part in your survey.
			</p>		
			<p>
				Please keep on reading to learn how you can send emails with individual access links to configured contacts from one of your guest lists.
			</p>
			<h2>
				How do I edit/remove an existing guest list?    
			</h2>
			<p>
				<ol>
					<li>
						Open your survey.
					</li>
					<li>
						Go to the 'Participants' page.
					</li>
					<li>
						To edit the guest list, click on the little pen icon.
					</li>
					<li>
						To remove a list, first click on the 'Deactivate' button.
						Then you can click on the 'Remove' button to delete the list.
					</li>
				</ol>
			</p>
			<h2>
				How do I send my participants an invitation email?    
			</h2>
			<p>
				Once you have created a new guest list, you can send them invitation
				emails.
			</p>
			<p>
				For 'secured' as well as for 'open' surveys, everyone will receive an
				individual access link.
			</p>
			<p>
				<strong>
					This means that everyone who receives an email invitation sent from
					EUSurvey can only submit one single contribution.
				</strong>
			</p>
			<p>
				<ol>
					<li>
						On the 'Participants' page, click on the little envelope icon.
					</li>
					<li>
						A window will open where you choose an email-template from the 'Select
						mail design' box - by default, the used style is 'EUSurvey'.
					</li>
					<li>
						You may then change the subject and content of your email and add a
						'reply-to' email address - all replies to your invitation email will then
						be sent to this address.
					</li>
					<li>
						Save your email text. It will be available for all your guest lists
						and surveys - you will find it in the dropdown list of the 'Use mail
						template' box.
					</li>
					<li>
						Click on 'Next'. A wizard will guide you through the invitation
						process.
					</li>
				</ol>
			</p>
			<h2>
				How to use tokens to create a link?
			</h2>
			<p>
				In order to create a list of tokens (authentication tokens) that can be distributed to access a secured online questionnaire, open your survey and go to the 'Participants' page. Click on 'Create new guest list' to start a wizard that will guide you through the process. Choose a name for the group and select 'Token list' from the types of guest lists.
			</p>
			<p>
				Use the created tokens to build up individual access links you can send per emails to the participants using the URL below:
			</p>
			<p>
				https://ec.europa.eu/eusurvey/runner/<span style="color: #e50000; font-weight: bold">SurveyAlias</span>/<span style="color: #e50000; font-weight: bold">TOKEN</span>
			</p>
			<p>
				Just replace:
			</p>
			<ul>
				<li>
					<span style="color: #e50000; font-weight: bold">SurveyAlias</span>
					with the <strong>alias of your survey</strong>
				</li>
				<li>
					<span style="color: #e50000; font-weight: bold">TOKEN</span>
					with one of the tokens from the token list
				</li>
			</ul>
			<h1>
				Managing your personal account
			</h1>
			<h2>
				How do I change my password?    
			</h2>
			<p>
				Access to the EUSurvey reference installation at the European Commission is
				managed using EU Login, so EUSurvey users are asked to change their EU Login
				password if they have lost it. This can be done by clicking on 'Forgot your
				Password?' on the EU Login page.
			</p>
			<h2>
				How do I change my email address?    
			</h2>
			<p>
				If you access EUSurvey using an EU Login user account, you cannot change your
				email address in EUSurvey. Instead, connect to EU Login and after login,
				select 'Modify my personal data' from the 'Account information' tab.
			</p>
			<p>
				If you use the OSS version of EUSurvey or you're a business user of the API
				interface, please connect to the application. Under 'Settings' &gt; 'My
				Account' &gt; click on 'E-mail'.
			</p>
			<h2>
				How do I change my default language?    
			</h2>
			<p>
				It is possible to change the default language for new surveys. Go to
				'Settings' &gt; 'My Account' and click on 'Language'.
			</p>
			<p>
				Once the update is saved, the system will propose the configured language
				as the primary language for any new surveys you create.
			</p>
			<h1>
				Data Protection and Privacy
			</h1>
			<h2>
				This system uses cookies. What information is saved there?    
			</h2>
			<p>
				The system uses session 'cookies' to ensure reliable communication between
				the client and the server.
			</p>
			<p>
				Therefore, the user's browser must be configured to accept 'cookies', which
				disappear once the session has been terminated.
			</p>
			<p>
				The system uses local storage to save copies of the participant's input to a
				survey. This is to ensure there is a backup if the server is not available
				during submission, the user's computer is switched off accidentally, etc.
			</p>
			<p>
				The local storage contains the IDs of the questions and the draft answers.
			</p>
			<p>
				Once a participant has submitted their answers to the server or has saved a
				draft on the server, the data is removed from the local storage.
			</p>
			<p>
				There is a checkbox above the survey - 'Save a backup on your local
				computer (disable if you are using a public/shared computer)' - to disable
				this feature. In that case, no data will be stored on the used computer.
			</p>
			<h2>
				What information is stored by EUSurvey when participants submit a
				contribution?
			</h2>
			<p>
				The information that is saved by EUSurvey depends on the security
				settings of your survey as well as on the method you use to invite
				your participants to contribute to your survey.
			</p>
			<p>
				<strong>Publicly accessible open survey:</strong>
			</p>
			<p>
				By default, if your survey is <strong>not secured</strong>, EUSurvey does <strong>not save any user-related information</strong>.
			</p>
			<p>
				However, the IP of every connection is saved for security reasons for every
				server request (see
				<a
						href="https://ec.europa.eu/eusurvey/home/privacystatement"
						target="_blank"
						>
					privacy statement on the protection of personal data
				</a>
				).
			</p>
			<p>
				<strong>Survey secured with a password:</strong>
			</p>
			<p>
				If your survey is secured by a <strong>password only</strong>, EUSurvey
				does <strong>not save</strong> any user-related information.
			</p>
			<p>
				However, the IP of every connection is saved for security reasons for every
				server request (see
				<a
						href="https://ec.europa.eu/eusurvey/home/privacystatement"
						target="_blank"
						>
					privacy statement on the protection of personal data
				</a>
				).
			</p>
			<p>
				<strong>Survey secured with EU Login authentication:</strong>
			</p>
			<p>
				If your survey is secured by <strong>EU Login authentication</strong>,
				EUSurvey will <strong>save</strong> the email address of the EU Login
				account.
			</p>
			<p>
				In addition, the IP of every connection is saved for security reasons for every
				server request (see
				<a
						href="https://ec.europa.eu/eusurvey/home/privacystatement"
						target="_blank"
						>
					privacy statement on the protection of personal data
				</a>
				).
			</p>
			<p>
				<strong>Sending invitations using EUSurvey:</strong>
			</p>
			<p>
				If you use EUSurvey to send invitations to your participants on a
				guest list on the 'Participants' page, it will send
				<strong>an unique invitation link</strong> to each participant.
			</p>
			<p>
				On submission, EUSurvey will save an invitation number that can be used
				<strong>to match</strong> the invited participant with the submitted
				contributions. This action is independent from your survey's security
				settings.
			</p>
			<p>
				Furthermore, the IP of every connection is saved for security reasons for
				every server request (see
				<a
						href="https://ec.europa.eu/eusurvey/home/privacystatement"
						target="_blank"
						>
					privacy statement on the protection of personal data
				</a>
				).
			</p>
			<p>
				<strong>Creating an anonymous survey:</strong>
			</p>
			<p>
				You can choose to create an anonymous survey by using the 'Anonymous survey mode'
				option in the survey properties. If activated, contributions to your survey will
				be anonymous as EUSurvey will not save any personal data such as IP addresses.
				If you want your survey to be fully anonymous, do not include questions
				collecting personal data in your survey design.
			</p>
			<p>
				<img alt="screenshot anonymity option" src="${contextpath}/resources/images/documentation/anonymity.png" />
			</p>
			<h2>
				Do I need to include a privacy statement?    
			</h2>
			<p>
				This depends on the questions you ask and the type of data you collect from
				your survey.
			</p>
			<p>
				Please note that your participants may not want to take part if you cannot
				guarantee the confidentiality of the data submitted.
			</p>
			<p>
				<strong>For EU staff only:</strong>
			</p>
			<p>
				We draw your attention to the policy on the "protection of natural persons with regard to the processing of personal data
				<a
						href="https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=uriserv:OJ.L_.2018.295.01.0039.01.ENG&amp;toc=OJ:L:2018:295:TOC"
						target="_blank"
						>
					Regulation (EU) 2018/1725
				</a>
				.
			</p>
			<p>
				If personal data is collected, a privacy statement must be drafted and then published together with the questionnaire. 
			</p>
			<p>
				Please contact the DPC (Data Protection Coordinator) of your DG in order to validate the privacy statement. 
			</p>
			<p>
				Additionally, any collection of personal data must be notified to the Data Protection Officer (DPO). Please contact your DPC if you need assistance for notification to the DPO.
			</p>
			<p>
				Please find below a privacy statement template that you could use for your surveys. You must change and adapt it to your needs:
			</p>
			<p>
				Template:
				<u>
					<a
							href="https://circabc.europa.eu/ui/group/599f39d2-e0cc-4765-bfdc-c9917c931509/library/dfed4f34-fa25-42ed-af44-e1acc4f0a58f/details"
							>
						'Privacy Statement template for surveys and consultations'
					</a>
				</u>
			</p>
			<h2>
				We need a Data Processing Agreement, where is the EUSurvey DPA?
			</h2>
			<p>
				The EUSurvey Data Processing Agreement (DPA) is available for all entities that are considered as
				Data Controller while we are processing your data through the EUSurvey platform. EUSurvey DPA
				is available <a href = "${contextpath}/home/dpa">here</a>.
			</p>
			<p>
				If you have any questions, please feel free to
				<a href="${contextpath}/home/support?dataprotection=1">contact us</a>.
			</p>
			<h2>
				Someone has contacted me to access, modify or delete all or part of their personal data - what should I do?
			</h2>
			<p>
				<div><b>REGULATION (EU) 2018/1725</b></div>
				<div><b>Right of access by the data subject - Article 17</b></div>
				<div><b>Right to rectification - Article 18</b></div>
				<div><b>Right to erasure ('right to be forgotten') - Article 19</b></div>
			</p>
			<p>
				Survey managers are responsible for managing the personal data collected as part of the survey.
				As a survey manager, you must respond to requests received from your survey participants in relation to data protection.
			</p>
			<p>
				In order to access, rectify or delete personal data collected as part of your survey, you can go to the results screen and search for the personal data in question using the available filters.
				You can then delete or modify them either by editing the contribution or by deleting the contribution entirely.
			</p>
			<p>
				<figure>
					<img alt="screenshot personal data" style="max-width: 920px" src="${contextpath}/resources/images/documentation/personal_data_modification.png">
				</figure>
			</p>
			<p>
				Regarding email addresses as well as first and last names, the other parts of the system where this data may be saved are the guest lists ('Participants' tab) or your 'Address Book'.
			</p>
			<h2>
				Bulk deletion of answers
			</h2>
			<p>
				You can delete an entire column from your survey results.
				This will have the effect of 'emptying' (i.e. permanently deleting) all responses to the corresponding question.
				This can be useful for anonymizing results, for example in the context of GDPR compliance.
			</p>
			<p>
				To bulk delete answers:
			</p>
			<ol>
				<li>Go to the 'Results' tab.</li>
				<li>Find the column for which you want to delete the answers in bulk.</li>
				<li>Click on the 3 dots icon.</li>
				<li>Select 'Blank answers'.</li>
				<li>Confirm the deletion.</li>
			</ol>
			<p>
				All answers collected for the corresponding question will be permanently removed.
			</p>
	</div> <!-- faqcontent -->
	</div>
	</div>

<%@ include file="../footer.jsp" %>	

</body>
</html>
