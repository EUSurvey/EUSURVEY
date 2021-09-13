<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
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
					'How do I create a questionnaire by using the EUSurvey editor?'
				</a>
			</p>
			<h2>
				How do I edit elements in my questionnaire?
			</h2>
			<p>
				The elements in your questionnaire will be <strong>selected for editing in the Form area</strong> and
				<strong>edited in the Element properties pane</strong> of the Editor. See
				<a href="#_Toc_4_2">
					'How do I create a questionnaire by using the EUSurvey editor?'
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
					'How do I create a questionnaire by using the EUSurvey editor?'
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
					'How do I create a questionnaire by using the EUSurvey editor?'
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
					'How do I create a questionnaire by using the EUSurvey editor?'
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
				<a href="https://ec.europa.eu/eusurvey/home/support">Contact us</a>
				to request privileges for external users.
			</p>
			<p>
				Read more about this under
				<a href="#_Toc_10_7">
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
				What does 'Complexity' mean?    
			</h2>
			<p>
				Adding too many elements or dependencies to your survey can lead to performance issues for the
				participants who want to fill out your questionnaire, because it is too 'complex'.
			</p>
			<p>
				Your survey could have a high level of complexity for several reasons:
			</p>
			<ul>
				<li>
					You use too many table/matrix elements
				</li>
				<li>
					You use too many dependencies
				</li>
				<li>
					You use too many cascading dependencies
				</li>
			</ul>
			<p>
				For more information, see our
				<a
						href="https://circabc.europa.eu/sd/d/281e626e-279e-45df-8581-1f6e04feff51/BestPractices-EUSurvey.pdf"
						target="_blank"
						>
					best practices guide
				</a>.
			</p>
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
				<a href="http://en.wikipedia.org/wiki/CAPTCHA" target="_blank">CAPTCHA</a> challenge
				before submitting a contribution.
			</p>
			<p>
				You can enable/disable the CAPTCHA in the 'Security' under 'Properties'.
			</p>
			<p>
				N.B. Although this won't make fraud impossible, it might discourage people from continuing to try to
				falsify survey results.
			</p>
			<h2>
				Can I enable my participants to access their contributions after submission?
			</h2>
			<p>
				Yes! Go to 'Security' under 'Properties' and enable 'Allow
				participants to change their contribution' option. Participants need
				to know the contribution-ID that was shown after they submitted
				their contribution. To change contributions after submission,
				participants should go to the EUSurvey homepage <a
					href="https://ec.europa.eu/eusurvey" target="_blank">https://ec.europa.eu/eusurvey</a>.
				Below the 'Register Now!' button is a link to the <a
					href="https://ec.europa.eu/eusurvey/home/editcontribution"
					target="_blank"> access page for individual
					contributions </a>. On this page participants must fill in their
				individual contribution-ID and the system will open their
				contribution. This way they can edit their contribution after it has
				been submitted.
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
				<a href="#_Toc_10_7">
					How do I give other users access to my survey?
				</a>
				'
			</p>
			<p>
				External owners or survey organizers cannot see EU domains on the Privileges tab / Add users button,
				therefore, they cannot provide access for these persons directly. Please
				<a href="https://ec.europa.eu/eusurvey/home/support">Contact us</a>
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
				the action menu just below the list of available translations and
				click on the 'Ok' button. This will open the online translation
				editor that will allow you to edit multiple translations at a time.
				Please click the 'Save' button to make sure that your changes are
				written to the system.
			</p>
			<p>
				In order to edit only one translation, open your survey, go to the
				'Translations' page and click on the Pen icon in the 'Action'
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
				non-European language as the pivot language of your survey.
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
					(for a translation of your survey's pivot language)
				</li>
				<li>
					Click the 'Request Translations' button in the 'Action' column (for a
					translation of your survey's pivot language)
				</li>
				<li>
					Select all languages you want to be translated (including at
					least one complete translation). Then select 'Request Translations'
					from the select box below your translations and click 'Ok'.
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
				Yes!
			</p>
			<p>
				By changing your survey's 'Alias' you can make the URL to your survey more meaningful.
			</p>
			<p>
				<ol>
					<li>
						Open the survey and go to the 'Properties' page.
					</li>
					<li>
						Change your survey's alias under the 'Basic' section.
					</li>
				</ol>
			</p>
			<p>
				An alias can only contain alphanumeric characters and hyphens.
			</p>
			<p>
				If you change the alias of a published survey, go to the 'Overview' page
				and click on 'Apply Changes'.
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
				form in the pivot language.
			</p>
			<p>
				You can also<strong> redirect respondents directly</strong> to a specific
				translation by using this link:
			</p>
			<p>
				https://ec.europa.eu/eusurvey/runner/<strong>SurveyAlias</strong>?surveylanguage=<strong>LC</strong>
			</p>
			<p>
				Where:
			</p>
			<ul>
				<li>
					<strong>SurveyAlias</strong>
					is your <strong>survey's alias</strong>
				</li>
				<li>
					<strong>LC</strong>
					is the appropriate <strong>ISO 639-1 language code</strong> (e.g. FR for
					French, DE for German, etc.)
				</li>
			</ul>
			<h2>
				How can I schedule my survey to be published while I'm on holiday?
			</h2>
			<p>
				You can schedule your survey to be published automatically at any time.
			</p>
			<p>
				<ol>
					<li>
						Open the survey and go to the 'Properties' page.
					</li>
					<li>
						Enable the 'Automatic survey publishing' option under the 'Advanced'
						section.
					</li>
					<li>
						Specify your surveys' start- and end-date.
					</li>
				</ol>
			</p>
			<h2>
				Can I receive a reminder of when my survey will end?    
			</h2>
			<p>
				Yes, EUSurvey can send you an email reminder when your survey is about to end. This
				allows you to prepare the next steps (e.g. organising resources for result
				analysis).
			</p>
			<p>
				To enable this option:
			</p>
			<p>
				<ol>
					<li>
						open the survey and go to the 'Properties' page
					</li>
					<li>
						go to the 'Advanced' section and under 'reminder' specify how long in
						advance you want a reminder
					</li>
					<li>
						click on 'Save'.
					</li>
				</ol>
			</p>
			<p>
				The reminder email will be sent to all form managers.
			</p>
			<h2>
				For EU staff: What are the official requirements for launching an open public
				consultation ('Your Voice in Europe' website)?   
			</h2>
			<p>
				Follow
				<a
						href="https://circabc.europa.eu/sd/d/fc02d2ac-d94f-42ed-b866-b3429e0d717b/Survey_publication_your_voice_in_europe_NEW.pdf"
						target="_blank"
						>
					the procedure
				</a>
				from the Commission's Secretariat-General for launching an open public
				consultation on the
				<a
						href="http://ec.europa.eu/yourvoice/consultations/index_en.htm"
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
				Yes, you can edit and change the survey as often as you want and add or
				change any additional (dependent) questions.
			</p>
			<p>
				However, the more changes you make the less usable the collected data will
				be, as different respondents to your survey might have responded to
				different surveys.
			</p>
			<p>
				So, to ensure that you can still compare all of the answers, you should
				avoid changing your survey's structure.
			</p>
			<p>
				Please note that you are fully responsible for every change you apply to
				your survey during its lifetime.
			</p>
			<p>
				To change an already published survey &#8594; click on 'Apply Changes' on the
				'Overview' page so the changes are visible in the published survey.
			</p>
			<p>
				If you want to remove answers from your survey, please Read: 'Will I
				lose any answers submitted when I change my form?'.
			</p>
			<h2>
				Will I lose any submitted answers when I change my form?    
			</h2>
			<p>
				No, unless you delete your survey from the system.
			</p>
			<p>
				However, all data collected might not be visible if you remove individual
				questions while your survey is active, as the search only shows the most
				recently published form.
			</p>
			<p>
				To view all answers, even to questions removed while your survey was
				active:
			</p>
			<p>
				&#8594; select 'Contributions (including deleted questions)' from the drop-down
				list on the 'Results' tab.
			</p>
			<h2>
				How can I change the title of my survey?    
			</h2>
			<p>
				Open the survey, go to the 'Properties' page &#8594; change your survey's title
				under 'Basic' section.
			</p>
			<p>
				If you have already published your survey, go to the 'Overview' page and
				click 'Apply Changes'.
			</p>
			<h2>
				How can I change my survey's contact information?    
			</h2>
			<p>
				Open the survey and go to the 'Properties' page.
			</p>
			<p>
				Choose between the following 'Contact' options under 'Basic Settings':
			</p>
			<p>
				<ul>
					<li>
						contact form: users can contact you using a form
					</li>
					<li>
						email: users can contact you directly by email (your email will be
						disclosed)
					</li>
					<li>
						webpage: this direct users to a specific webpage.
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
				After respondents submit their answers, they receive a confirmation
				message.
			</p>
			<p>
				To change the default message:
			</p>
			<p>
				<ol>
					<li>
						open the survey, go to the 'Properties' page
					</li>
					<li>
						change the confirmation message under the 'Special Pages' section.
					</li>
				</ol>
			</p>
			<p>
				If you have already published your survey, go to the 'Overview' page and
				click 'Apply Changes'.
			</p>
			<h2>
				How do I adjust the default escape message?    
			</h2>
			<p>
				The escape page contains the message that your respondents see if your
				survey is not available.
			</p>
			<p>
				To change the default message:
			</p>
			<p>
				<ol>
					<li>
						open the survey, go to the 'Properties'
					</li>
					<li>
						click on 'Edit' under the 'Special Pages' section &#8594; change the option on
						the 'Unavailability Page'.
					</li>
				</ol>
			</p>
			<p>
				If you have already published your survey, go to the 'Overview' page and
				click on 'Apply Changes'.
			</p>
			<h2>
				Can I archive a survey?<u> </u>
			</h2>
			<p>
				Yes, you can archive your survey, as well as reload it or to relaunch it on
				a later date.
			</p>
			<p>
				To archive your survey &#8594; select the 'Archive Survey' icon in the action
				menu on the 'Overview' page.
			</p>
			<p>
				Archived surveys cannot be edited or be used to collect any data.
			</p>
			<p>
				However, you can export results or request a PDF-file of your survey.
			</p>
			<p>
				From the 'Dashboard' page, you can access your archived survey as well as
				restore it.
			</p>
			<p>
				A restored survey can be edited again.
			</p>
			<h2>
				How do I grant other users access to my survey?    
			</h2>
			<p>
				You can grant access to other users for different purposes:
			</p>
			<ul>
				<li>
					testing the survey ('Access Form Preview')
				</li>
				<li>
					accessing the results ('Results')
				</li>
				<li>
					editing the survey ('Form Management')
				</li>
			</ul>
			<p>
				To grant access to an individual or a department: open your survey, go to
				the 'Privileges' page.
			</p>
			<p>
				The following access rights are possible:
			</p>
			<p>
				<ul>
					<li>
						green: access to read and write
					</li>
					<li>
						yellow: access to read
					</li>
					<li>
						red: No access
					</li>
				</ul>
			</p>
			<p>
				<ol>
					<li>
						Click on 'Add user' or 'Add department' on the 'Privileges' page.
					</li>
					<li>
						A window pops up to show you how to add users.
					</li>
					<li>
						When clicking on 'Add user', you must select the correct Domain (for
						example 'European Commission').
					</li>
					<li>
						Enter the login, email address or any other field and click on 'Search'.
					</li>
					<li>
						Select the user and click 'Ok'.
					</li>
					<li>
						When clicking on 'Add department', select the correct field.
					</li>
					<li>
						Find the right department, then click 'Ok'.
					</li>
				</ol>
			</p>
			<p>
				You will then be redirected to the 'Privileges' page.
			</p>
			<p>
				Here, click on the red icons to set the appropriate access rights:
			</p>
			<ul type="disc">
				<li>
					To grant the right to test your survey:

					<ul>
						<li>
							Change the colour of 'Access Form Preview' to 'Green' (click on the
							colour to change the rights)
						</li>
						<li>
							The survey will automatically appear in their 'Surveys' page once they
							log into EUSurvey (see also '
							<a href="#_Toc_6_2">
								How can my colleagues test my survey before it is published?
							</a>
							').
						</li>
					</ul>
				</li>
				<li>
					To grant the right to access your survey's results:

					<ul>
						<li>
							If the colour of 'Results' is 'Yellow', users can only view the results,
							not edit or delete anything.
						</li>
						<li>
							If you change the colour to 'Green', they can view, edit and delete the
							answers (see also '
							<a href="#_Toc_10_7">
								How do I give other users access to my survey's results?
							</a>
							').
						</li>
					</ul>
				</li>
				<li>
					To grant the right to edit your survey:

					<ul>
						<li>
							Select 'Green' &#8594; users can now edit it.
						</li>
						<li>
							They will automatically see your survey in their list of surveys (see
							also '
							<a href="#_Toc_4_11">
								How do I give other users permission to edit my survey?
							</a>
							').
						</li>
					</ul>
				</li>
				<li>
					To grant the right to manage invitations of your survey:

					<ul>			
						<li>
							If the colour is 'Yellow' &#8594; users can only view your invitations.
						</li>
						<li>
							Select 'Green' &#8594; users can edit it.
						</li>
						<li>
							They will automatically see your survey in their list of surveys (see
							also '
							<a href="#_Toc_4_11">
								How do I give other users permission to edit my survey?
							</a>
							').
						</li>
					</ul>
				</li>
			</ul>


			<p>
				Setting all 4 circles to 'Green' will grant the user full access rights to
				your survey.
			</p>
			<p>
				External owners or survey organisers cannot see EU fields on the
				'Privileges' tab/'Add users' button. Therefore, they cannot directly grant
				access to these users.
			</p>
			<p>
				Please <a href="https://ec.europa.eu/eusurvey/home/support">contact us</a>
				to request privileges for external users.
			</p>
			<h2>
				What are activity logs?    
			</h2>
			<p>
				Activity logs monitor and log the activity on your survey. You can check
				which user applied which change to your survey and when.
			</p>
			<p>
				You can also export the activity logs into several file formats such as
				xls, csv and ods.
			</p>
			<p>
				To enter the activity log of your survey &#8594; click on the 'Activity' page,
				next to 'Properties'.
			</p>
			<p>
				If the activity logs are empty, they may have been deactivated across the
				system.
			</p>
			<p>
				Find
				<a
						href="https://ec.europa.eu/eusurvey/resources/documents/ActivityLogEvents.xlsx"
						>
					here
				</a>
				a list of the logged events.
			</p>
			<h1>
				Analysing, exporting and publishing results
			</h1>
			<h2>
				Where can I find the answers submitted by my respondents?    
			</h2>
			<p>
				Open your survey in EUSurvey &#8594; click the 'Results' page.
			</p>
			<p>
				Initially, you'll see the full content of all submitted answers in a table.
			</p>
			<p>
				You can view the results in 2 different ways:
			</p>
			<ul>
				<li>
					full content
				</li>
				<li>
					statistics
				</li>
			</ul>
			<p>
				To switch view modes &#8594; click the icons in the upper left corner of the
				screen.
			</p>
			<p>
				See also '
				<a href="#_Toc_3_5">
					How do I open an existing survey for editing etc.?
				</a>
				'
			</p>
			<h2>
				How can I download submitted answers?    
			</h2>
			<p>
				<ol>
					<li>
						Open your survey, go to the 'Results' page.
					</li>
					<li>
						Click 'Export' in the top right-hand corner.
					</li>
					<li>
						Select from the available export file formats.
					</li>
					<li>
						Specify a name in the dialogue box - under this name the export file
						will appear on the 'Export' page.
					</li>
				</ol>
			</p>
			<p>
				Different export file formats are available, depending on the view mode
				(full content/statistics).
			</p>
			<p>
				N.B. The export file will contain the set of questions defined as
				exportable and the filtered search results.
			</p>
			<h2>
				How can I extract the draft answers?    
			</h2>
			<p>
				This is currently not allowed by our privacy policy.
			</p>
			<p>
				On your Dashboard, you can see the number of saved draft answers to your
				survey.
			</p>
			<h2>
				How can I access and analyse a defined subset of answers?    
			</h2>
			<p>
				On the 'Results' page:
			</p>
			<p>
				<ul>
					<li>
						search for keywords in free-text answers or
					</li>
					<li>
						select individual answers from choice-questions in the filter bar.
					</li>
				</ul>
			</p>
			<p>
				This limits the full set of answers to a subset of answers.
			</p>
			<p>
				For performance reasons you can only set a maximum of 3 filters!
			</p>
			<p>
				You can change the view mode anytime, so you can carry out an advanced
				statistical analysis of the data collected.
			</p>
			<p>
				Note: to view and analyse results, you need certain privileges (see '
				<a href="#_Toc_10_7">
					How do I give other users access to my survey's results?
				</a>
				').
			</p>
			<p>
				To export a subset of contributions, read 'How can I download submitted
				contributions?'.
			</p>
			<p>
				See also '
				<a href="#_Toc_10_1">
					Where can I find the answers submitted by my respondents?
				</a>
				'
			</p>
			<h2>
				How can I publish my results?    
			</h2>
			<p>
				<ol>
					<li>
						Open the survey.
					</li>
					<li>
						Go to the 'Properties' page and select 'Publish Results'.
					</li>
					<li>
						Here you will find the URL of the published results.
					</li>
					<li>
						Choose which questions/answers/contributions you would like to publish.
					</li>
					<li>
						To go there directly &#8594; click 'Edit Results Publication' from the
						'Overview' page.
					</li>
					<li>
						Be sure to select something in 'Publish Results' under 'Publish',
						otherwise the system won't publish any results.
					</li>
				</ol>
			</p>
			<h2>
				How can I access the published results?    
			</h2>
			<p>
				Open the 'Overview' page &#8594; click on the 'Published' hyperlink right next to
				'Results'.
			</p>
			<p>
				Everybody who knows this address can access your results.
			</p>
			<h2>
				How do I give other users access to my survey's results?    
			</h2>
			<p>
				Open your survey &#8594; go to the 'Privileges' page and give other users access
				to your results.
			</p>
			<p>
				Read more about this under '
				<a href="#_Toc_10_7">
					How do I give other users access to my survey?
				</a>
				'.
			</p>
			<h2>
				I cannot unzip my exported files - can I solve this?    
			</h2>
			<p>
				This might happen if the name of the files contained in your folder is too
				long.
			</p>
			<p>
				Windows has a maximum length of 260 characters for directory locations on
				the hard drive.
			</p>
			<p>
				Possible solutions for this are:
			</p>
			<ul>
				<li>
					unzip the folder in the root directory of your operating system, e.g.
					unpack at 'C:' instead of 'C:\Users\USERNAME\Desktop'; or
				</li>
				<li>
					when unpacking the files, rename the target folder to shorten the
					directory length.
				</li>
			</ul>
			<h2>
				Published results - how to protect personal information uploaded by respondents    
			</h2>
			<p>
				Under data protection rules, the form manager can publish files uploaded
				with a respondent's answer along with the other results.
			</p>
			<p>
				To do so &#8594; tick the 'Uploaded elements' check box.
			</p>
			<p>
				This is in the corresponding section on the 'Properties' page under the
				'Publish Results' section.
			</p>
			<p>
				This check box will only appear if your survey contains an uploaded file.
			</p>
			<h2>
				How do I design a survey to publish the results either with or without
				personal information?    
			</h2>
			<p>
				If you want your respondents to choose whether or not their personal
				information can be published, follow
				<a
						href="https://circabc.europa.eu/sd/d/e68ff760-226f-40e9-b7cb-d3dcdd04bfb1/How_to_publish_survey_results_anonymously.pdf"
						target="_blank"
						>
					these instructions
				</a>
				to build the survey to fit what is required.
			</p>
			<h2>
				Why are my results not up-to-date?    
			</h2>
			<p>
				A new database was introduced to improve EUSurvey's performance on queries
				on surveys' results.
			</p>
			<p>
				However, this results sometimes in some delays in displaying latest data on
				the 'Results' page.
			</p>
			<p>
				This delay should not be more than 12 hours; if it is more than 12 hours, &#8594;
				contact EUSurvey    <a href="https://ec.europa.eu/eusurvey/home/support">support</a>.
			</p>
			<h1>
				Design and layout
			</h1>
			<h2>
				How do I change the general look and feel of my survey?    
			</h2>
			<p>
				<ol>
					<li>
						Open your survey, go to the 'Properties' page.
					</li>
					<li>
						Select 'Appearance' section.
					</li>
					<li>
						Choose a new survey skin using the drop-down menu under 'Skin' &#8594; click
						'Save'.
					</li>
				</ol>
			</p>
			<p>
				If you've already published your survey &#8594; go to the 'Overview' page and
				click on 'Apply Changes'.
			</p>
			<h2>
				How can I create my own survey themes?    
			</h2>
			<p>
				<ol>
					<li>
						Go to the 'Settings' page, at the top of your screen &#8594; select 'Skins'.
					</li>
					<li>
						Click on 'Create a new Skin'.
					</li>
					<li>
						This will open the skin editor for survey themes.
					</li>
				</ol>
			</p>
			<p>
				You can copy an existing theme and use the online skin editor to change
				this template if needed.
			</p>
			<h2>
				How do I add a logo to my survey?    
			</h2>
			<p>
				To place your project/company logo in the top right-hand corner of your
				survey &#8594; upload an image file to the 'Appearance' section on the
				'Properties' page.
			</p>
			<p>
				If you've already published your survey &#8594; go to the 'Overview' page and
				click on 'Apply Changes'.
			</p>
			<h2>
				How do I add useful links to my survey?    
			</h2>
			<p>
				<ol>
					<li>
						Open your survey.
					</li>
					<li>
						Go to the 'Properties' page and select 'Advanced'.
					</li>
					<li>
						Add labels and URLs under 'Useful links'.
					</li>
					<li>
						These links will appear on the right-hand side of each page of your
						survey.
					</li>
				</ol>
			</p>
			<p>
				If you've already published your survey &#8594; go to the 'Overview' page and
				click on 'Apply Changes'.
			</p>
			<h2>
				Where do I upload background documents for my survey?    
			</h2>
			<p>
				<ol>
					<li>
						Open your survey.
					</li>
					<li>
						Go to the 'Properties' page and select 'Advanced'.
					</li>
					<li>
						Upload a file under 'Background Documents'.
					</li>
					<li>
						These documents will appear on the right-hand side of each page of your
						survey.
					</li>
				</ol>
			</p>
			<p>
				If you've already published your survey &#8594; go to the 'Overview' page and
				click on 'Apply Changes'.
			</p>
			<h2>
				How do I create a multi-page survey?    
			</h2>
			<p>
				Top-level sections of your survey can be divided into individual pages
				automatically.
			</p>
			<p>
				<ol>
					<li>
						Open your survey.
					</li>
					<li>
						Go to the 'Properties' page and select 'Appearance' section.
					</li>
					<li>
						Enable 'Multi-Paging' and click on 'Save'.
					</li>
				</ol>
			</p>
			<p>
				If you've already published your survey &#8594; go to the 'Overview' page and
				click on 'Apply Changes'.
			</p>
			<h2>
				How do I enable automatic numbering for my survey?    
			</h2>
			<p>	
				To add auto-numbering to all Sections/uestions:
			</p>
			<p>
				<ol>
					<li>
						Open your survey, go to the 'Properties' page and select 'Appearance'.
					</li>
					<li>
						Enable and select your preferences for 'Automatic Numbering Sections'
						and/or 'Automatic Numbering Questions'.
					</li>
					<li>
						Click on 'Save'.
					</li>
				</ol>
			</p>
			<p>
				If you've already published your survey &#8594; go to the 'Overview' page and
				click on 'Apply Changes'.
			</p>
			<h2>
				Can I create a customised skin for my survey?    
			</h2>
			<p>
				Yes, follow the steps here to create a new skin for your survey.
			</p>
			<p>
				<ol>
					<li>
						Go to the 'Settings' page &#8594; select 'Skins'.
					</li>
					<li>
						Open the 'Create a new skin' tab &#8594; change the look of different items in
						your survey: question and answer text, survey title, help text and many
						more.
					</li>
					<li>
						Give your new skin a name.
					</li>
					<li>
						Select the item that you want to skin.
					</li>
					<li>
						On the right of the screen you find a box where you can change the font
						of your item:

						<ul>
							<li>
								foreground and background colour
							</li>
							<li>
								font style, family, size and weight.
							</li>
						</ul>
					</li>
					<li>
						Below, in the 'Skin Preview Survey', you can see how the changed item
						looks in your survey.
					</li>
					<li>
						Click on 'Save'.
					</li>
				</ol>
			</p>
			<p>
				If you want to change several items  change one after the other &#8594; save
				them at the end when all items are finalised (you don't have to click save
				after you change each item).
			</p>
			<p>
				To adapt your survey to your new skin &#8594; go to the 'Properties' page and
				select 'Appearance'.
			</p>
			<p>
				Choose your new skin in the 'Skin' drop-down menu &#8594; click on 'Save'.
			</p>
			<h1>
				Managing contacts and invitations
			</h1>
			<h2>
				What is the 'Address Book'?    
			</h2>
			<p>
				In the 'Address Book' you can create your own groups of respondents.
			</p>
			<p>
				This way you can invite people or organisations who match certain criteria
				(e.g. male, over 21).
			</p>
			<p>
				Every potential respondent is stored as a contact in the address book along
				with an unlimited list of attributes which can be edited.
			</p>
			<p>
				To save contacts to your address book they must have an identifier ('Name')
				and an email address.
			</p>
			<h2>
				What are a contact's 'Attributes'?    
			</h2>
			<p>
				Each contact saved in the address book has a variable set of attributes
				such as 'Country', 'Phone', 'Remarks', etc.
			</p>
			<p>
				You can create a new attribute by editing a contact.
			</p>
			<p>
				<ol>
					<li>
						Go to the 'Edit Contact' window &#8594; 'attributes' menu and select 'New...'.
					</li>
					<li>
						A new window will pop up where you can edit the new attribute.
					</li>
					<li>
						The newly created attribute will appear as a column in the address book
						- it can also be added to a set of contacts.
					</li>
				</ol>
			</p>
			<h2>
				How do I add new contacts to my address book?    
			</h2>
			<p>
				Go to the 'Address Book' &#8594; click on 'Add Contact' to add a single contact.
			</p>
			<p>
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
				This is a form that automatically creates contacts from the personal data
				that respondents submit.
			</p>
			<p>
				You can create one by following these steps.
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
				('Name' and 'Email').
			</p>
			<p>
				This is to ensure every participant enters valid personal data.
			</p>
			<p>
				By enabling the 'Attribute' option for individual questions, the user can
				choose what other information is stored about the newly created contact
				(e.g. a text-question with the attribute 'Telephone' can be used to store
				the respondent's phone number in the address book).
			</p>
			<h2>
				How do I import multiple contacts from a file to my address book?    
			</h2>
			<p>
				To import a list of contacts into the system, EUSurvey has a wizard that
				guides you to the import procedure.
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
						select 'Import' from your 'Address Book' page
					</li>
					<li>
						select the file in which you have saved your contacts
					</li>
					<li>
						specify if your file contains a header row or not
					</li>
					<li>
						specify the type of separator you used for CSV and TXT files (most
						probable character is suggested by default).
					</li>
				</ol>
			</p>
			<p>
				As a second step:
			</p>
			<p>
				<ol>
					<li>
						the system will ask you to map the individual columns to new attributes
						for your contacts (the mandatory attributes 'Name' and 'Email' must be
						mapped in order to proceed)
					</li>
					<li>
						once you click 'Next', the system loads your file into the system,
						displaying the individual contacts to be imported
					</li>
					<li>
						you can unselect individual contacts you don't want to import
					</li>
					<li>
						click 'Save' to save your contacts to your address book.
					</li>
				</ol>
			</p>
			<h2>
				How do I edit an attribute for multiple contacts at a time?    
			</h2>
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
				Yes.
			</p>
			<p>
				On your 'Address Book' page, click an icon in the top right-hand corner
				representing individual file formats.
			</p>
			<p>
				You will find the exported contacts on the 'Exports' page.
			</p>
			<h1>
				Inviting respondents
			</h1>
			<h2>
				How do I specify a set of possible respondents? What is a 'Guest List'?    
			</h2>
			<p>
				You can group selected contacts and send each one an email with individual
				access links. This group is called a 'Guest List'.
			</p>
			<p>
				Aside from the survey password, this is an another way to allow people to
				take part in your survey.
			</p>
			<p>
				To invite multiple contacts &#8594; open your survey, go to the 'Participants'
				page.
			</p>
			<p>
				Choose one of the following types of guest lists to start a wizard that
				will guide you through the process:
			</p>
			<ul>
				<li>
					<strong>Contact list</strong>
					'Address Book'
					<br/>
					Select contacts from the 'Address Book' to add them to your guest list
					(see '
					<a href="#_Toc_12_1">
						What is the "Address Book?
					</a>
					')
				</li>
				<li>
					<strong>EU list</strong>
					'EU institutions and other bodies' (EU staff only)
					<br/>
					Select multiple departments from your institution/agency to add
					everyone working in those department to your guest list
				</li>
				<li>
					<strong>Token list</strong>
					<br/>
					Create a list of tokens (or 'Unique Codes') to be distributed offline
					to access a secured online survey.
				</li>
			</ul>
			<p>
				Use the search function on your address book &#8594; click the '&gt;&gt;' button
				on the next screen to move contacts from your address book to your new
				guest list.
			</p>
			<p>
				Click 'Save' to create a new guest list with all the contacts you want for
				your survey.
			</p>
			<p>
				See below to learn how to send emails with individual access links to
				configured contacts from one of your guest lists.
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
						To edit the guest list &#8594; click on the little pen icon.
					</li>
					<li>
						To remove a list &#8594; first click on the 'Deactivate' button.
					</li>
					<li>
						Then click on the 'Remove' button to delete the list.
					</li>
				</ol>
			</p>
			<h2>
				How do I send my participants an invitation email?    
			</h2>
			<p>
				Once you have created a new guest-list, you can send them invitation
				emails.
			</p>
			<p>
				For 'secured' as well as for 'open' surveys, everyone will receive an
				individual access link.
			</p>
			<p>
				<strong>
					This means that everyone who receives an email invitation sent from
					EUSurvey can only submit one single contribution (answer).
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
						Then you can change the subject and content of your email and add a
						'reply-to' email address - all replies to your invitation email will then
						be sent to this address.
					</li>
					<li>
						Then save your email text &#8594; it will be available for all your guest
						lists and surveys - you will find it in the dropdown list of the 'Use mail
						template' box.
					</li>
					<li>
						Then click on 'Next' &#8594; a wizard will guide you through the invitation
						process.
					</li>
				</ol>
			</p>
			<h2>
				How to use tokens to create a link?
			</h2>
			<p>
				Follow the steps below to create a list of tokens (i.e. unique
				authentication codes) to be distributed offline to access a secured online
				questionnaire.
			</p>
			<p>
				<ol>
					<li>
						Open your survey.
					</li>
					<li>
						Go to the 'Participants' page.
					</li>
					<li>
						Click on 'Token list' to start a wizard to guide you through the
						process.
					</li>
					<li>
						Choose a name for the group and select 'Tokens' from the different types
						of guest lists.
					</li>
				</ol>
			</p>
			<p>
				In the way outlined below, use the created tokens to build up individual
				access links to be sent out in emails to the participants:
			</p>
			<p>
				https://ec.europa.eu/eusurvey/runner/<strong>SurveyAlias</strong>/<strong>TOKEN</strong>
			</p>
			<p>
				Just replace:
			</p>
			<ul>
				<li>
					<strong>SurveyAlias</strong>
					with your <strong>survey's</strong>
					<strong>alias</strong>
				</li>
				<li>
					<strong>TOKEN</strong>
					with the token you choose from the token list
				</li>
			</ul>
			<h1>
				Managing your personal account
			</h1>
			<h2>
				How do I change my password?    
			</h2>
			<p>
				Users need to change their EU Login password if they lose it.
			</p>
			<p>
				To do this: go to the EU Login page &#8594; click on 'Forgot your Password?'.
			</p>
			<h2>
				How do I change my email address?    
			</h2>
			<p>
				If you access EUSurvey using an EU Login user account, you can change your
				email address by following the steps below:
			</p>
			<p>
				Connect to EU Login &#8594; after logging on, select 'Modify my personal data'
				from the 'Account information' tab.
			</p>
			<p>
				If you use the OSS version of EUSurvey or you're a business user of the API
				interface v connect to the application &gt; go to 'Settings' &gt; 'My
				Account' &gt; click on 'E-mail'.
			</p>
			<h2>
				How do I change my default language?    
			</h2>
			<p>
				Go to 'Settings' &gt; 'My Account' and click on 'Language'.
			</p>
			<p>
				Once the update is saved, the system will propose the configured language
				as the primary language for any new surveys you create.
			</p>
			<h1>
				Privacy
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
				disappear once the session is finished.
			</p>
			<p>
				The system uses local storage to save copies of the respondent's input to a
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
				the feature, so no data is stored on the used computer.
			</p>
			<h2>
				What information is stored by EUSurvey when respondents submit a
				contribution (answer)?    
			</h2>
			<p>
				This depends on your survey's security settings and on the method used to
				invite your participants to take part.
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
				<strong>Sending invitations using EUSurvey:</strong>
			</p>
			<p>
				If you use EUSurvey to send invitations to your participants on a
				guest-list on the 'Participants' page, it will send    <strong>a unique invitation link</strong> to each participant.
			</p>
			<p>
				On submission, EUSurvey will save an invitation number that can be used    <strong>to match</strong> the invited participant with the submitted
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
				You can choose to create an anonymous survey by using the 'Anonymous survey mode' option in the survey properties. If activated, contributions to your survey will be anonymous as EUSurvey will not save any personal data such as IP addresses. If you want your survey to be fully anonymous, do not include questions collecting personal data in your survey design.
			</p>
			<p>
				<img src="${contextpath}/resources/images/documentation/anonymity.png" />
			</p>
			<h2>
				Do I need to include a privacy statement?    
			</h2>
			<p>
				This depends on the questions you ask and the type of data you collect from
				your survey.
			</p>
			<p>
				Be aware that your participants may not want to take part if you cannot
				guarantee the confidentiality of the data submitted.
			</p>
			<p>
				<strong>For EU staff only:</strong>
			</p>
			<p>
				Note the policy on the 'protection of natural persons with regard to the
				processing of personal data...' under
				<a
						href="https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=uriserv:OJ.L_.2018.295.01.0039.01.ENG&amp;toc=OJ:L:2018:295:TOC"
						target="_blank"
						>
					Regulation (EU) 2018/1725
				</a>
				.
			</p>
			<p>
				If personal data is collected, a privacy statement must be published
				together with the questionnaire.
			</p>
			<p>
				Contact the DPC (Data Protection Coordinator) of your DG to validate the
				privacy statement.
			</p>
			<p>
				Furthermore, any collection of personal data must be notified to the Data
				Protection Officer (DPO). Contact your DPC if you need assistance on
				notifying the DPO.
			</p>
			<p>
				Below is a privacy statement template that you could use for your surveys.
				You must change and adapt it to your needs:
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
	</div> <!-- faqcontent -->
	</div>
	</div>

<%@ include file="../footer.jsp" %>	

</body>
</html>
