<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Documentation" /></title>	
	<%@ include file="../includes.jsp" %>	
	
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
				It's an online tool that you can use to create, publish and manage
				questionnaires and other interactive forms.
			</p>
			<h2>
				When should I use EUSurvey?    
			</h2>
			<p>
				Whenever you want to:
				<ul>
					<li>
						put a questionnaire or interactive form online
					</li>
					<li>
						record a large number of similar datasets.
					</li>
				</ul>
			</p>
			<h2>
				What are EUSurvey's technical limits?    
			</h2>
			<p>
				It may not be suitable for your project if:
				<ul>
					<li>
						different respondents work on the same contribution (answer) before
						it's submitted
					</li>
					<li>
						answers need to be validated before being submitted.
					</li>
				</ul>
			</p>
			<p>
				For more details:
				<ul>
					<li>
						see the <a href="https://ec.europa.eu/eusurvey/home/documentation">support</a>
						page for 'usability limits' of the tool
					</li>
					<li>
						Contact the EUSurvey support team <a href="https://ec.europa.eu/eusurvey/home/support">here</a>.
					</li>
				</ul>
			</p>
			<h2>
				Features of EUSurvey    
			</h2>
			<p>
				<strong>Customisable forms</strong>
				<br/>
				You can choose various types of questions, e.g.:

				<ul>
					<li>
						simple text and multiple-choice questions
					</li>
					<li>
						spreadsheet questions
					</li>
					<li>
						multimedia survey items.
					</li>
				</ul>
			</p>
			<p>
				Structure your survey using special structural elements.
			</p>
			<p>
				<strong>Dependent questions</strong>
				<br/>
				EUSurvey can display further questions and fields, depending on the
				respondent's answers, making the survey more interactive.
			</p>
			<p>
				<strong>Scheduled publishing</strong>
				<br/>
				Publish and unpublish your survey automatically whenever you want.
			</p>
			<p>
				<strong>Change after publishing</strong>
				<br/>
				Change a published survey without losing any answers.
			</p>
			<p>
				<strong>Languages</strong>
				<br/>
				The user interface is available in 23 EU languages.
			</p>
			<p>
				You can translate your form into any of the 136 languages covered by the
				ISO 639-1 classification (ISO 639 is a standardized nomenclature used to
				classify languages.).
			</p>
			<p>
				<strong>Security</strong>
				<br/>
				EUSurvey has the required set-up to secure online forms.
			</p>
			<p>
				<strong>Send invitations directly </strong>
				<br/>
				You can manage your contacts and send out emails with individual access
				links to your survey.
			</p>
			<p>
				<strong>Advanced privacy</strong>
				<br/>
				By creating an anonymous form, you guarantee your respondents' privacy.
			</p>
			<p>
				You won't be able to access their connection details.
			</p>
			<p>
				<strong>Customised look/feel</strong>
			</p>
			<ul>
				<li>
					change any aspect of the form's layout using flexible tools
				</li>
				<li>
					adapt your form to a specific project using our large list of survey
					themes
				</li>
				<li>
					choose between single page and multi-page surveys.
				</li>
			</ul>
		</p>
		<p>
			<strong>Save answer as a draft</strong>
			<br/>
			Respondents can save their answer as a draft on the server and continue
			later.
		</p>
		<p>
			<strong>Answer offline </strong>
			<br/>
			Respondents can answer a form offline before submitting it to the server
			when completed.
		</p>
		<p>
			<strong>Automatic numbering</strong>
			<br/>
			To structure your survey, EUSurvey can number the different sections.
		</p>
		<p>
			<strong>High-contrast version</strong>
			<br/>
			Visually-impaired respondents can choose a high-contrast version of the
			survey. This is created automatically for each form.
		</p>
		<p>
			<strong>Uploading supporting files</strong>
			<br/>
			You can upload files to your survey, which every respondent can download.
		</p>
		<h2>
			Form management
		</h2>
		<p>
			<strong>Working together</strong>
			<br/>
			For surveys managed by multiple users, EUSurvey lets you provide permission
			for other users to test a survey or to analyse the results.
		</p>
		<h2>
			Result management
		</h2>
		<p>
			<strong>Analyse your results</strong>
			<br/>
			You can analyse basic results and present data in histograms and chart
			views.
		</p>
		<p>
			You can also create standard spreadsheet formats of the survey results, to
			use in statistical applications.
		</p>
		<p>
			<strong>Publish your results</strong>
			<br/>
			You can publish a sub-set of all submitted answers on the application's
			internal pages. The system can automatically calculate and create
			statistics and charts.
		</p>
		<p>
			<strong>Edit submitted answers</strong>
			<br/>
			Respondents can change their answer after submission, if needed.
		</p>
		<h2>
			Where do I get more information about EUSurvey    
		</h2>
		<p>
			Need<strong> practical help</strong>? Click '
			<a href="https://ec.europa.eu/eusurvey/home/documentation" target="_blank">
				Support
			</a>
			' (under <em>'Help'</em> at the top-right of the screen).
		</p>
		<p>
			Want details about EUSurvey's background and funding? Click '
			<a href="https://ec.europa.eu/eusurvey/home/about" target="_blank">
				About
			</a>
			'.
		</p>
		<h2>
			Who do I contact if there are technical problems?    
		</h2>
		<p>
			<strong>EU staff</strong>
			- contact your IT helpdesk and ask them to forward the problem to the
			EUSurvey support team (describing it as precisely as possible).
		</p>
		<p>
			External users - contact the Commission's
			<a
					href="mailto:EC-CENTRAL-HELPDESK@ec.europa.eu?subject=Incident%20Creation%20Request%20for%20DIGIT%20EUSURVEY%20SUPPORT%20&amp;body=%20Dear%20Helpdesk,%0D%0DCould%20you%20please%20open%20a%20ticket%20to%20DIGIT%20EUSURVEY%20SUPPORT%20with%20the%20following%20description:"
					target="_blank"
					>
				Central Helpdesk
			</a>
			.
		</p>
		<h2>
			How can I leave feedback on improving EUSurvey?    
		</h2>
		<p>
			To leave comments and feedback, ask your IT helpdesk or the Central
			Helpdesk to forward them to the EUSurvey support team.
		</p>
		<p>
			The support team will get back to you as soon as possible.
		</p>
		<h2>
			Which browsers are supported by EUSurvey?    
		</h2>
		<p>
			Microsoft Edge, Mozilla Firefox and Google Chrome (the last 2 versions).
		</p>
		<p>
			Using other browsers might cause compatibility problems.
		</p>
		<h2>
			EUSurvey disclaimer (for non-EU users only)    
		</h2>
		<p>
			In all questionnaires and invitation emails sent by a user who is not an <strong> EU official</strong>, the following disclaimer will be displayed:
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
			Can respondents use a mobile device to answer?    
		</h2>
		<p>
			Yes, anyone can answer using a mobile phone or tablet PC.
		</p>
		<h2>
			Is there a minimum screen size?
		</h2>
		<p>
			No, the questionnaires will adapt to the size of the screen used by
			respondents.
		</p>
		<p>
			However, to<em> create</em> and <em>manage</em> surveys, we recommend a
			minimum resolution of 1680x1050 pixels for a good user experience.
		</p>
		<h1>
			Logging in/creating an account
		</h1>
		<h2>
			I have an EU Login account. Do I need to register separately for EUSurvey?    
		</h2>
		<p>
			No, an EU Login account is enough.
		</p>
		<p>
			To access EUSurvey, click on the login button on the
			<a href="https://ec.europa.eu/eusurvey/home/welcome" target="_blank">
				EUSurvey homepage
			</a>.
		</p>
		<h2>
			How do I connect to EUSurvey?<u> </u>
		</h2>
		<p>
			After you click on 'login' on the <u>EUSurvey homepage</u>, you will be
			redirected to the EUSurvey login screen.
		</p>
		<p>
			There, choose the option that matches your personal case:
			<ul>
				<li>
					<strong>If you work for an EU institution </strong>
					- choose the second option to connect, using your EU Login username and
					password.
				</li>
				<li>
					<strong>If you don't work for an EU institution (externals)</strong>
					- choose the first option to connect. You'll need to have previously
					registered your mobile phone to pass the
					<a href="https://en.wikipedia.org/wiki/Help:Two-factor_authentication">
						two-factor authentication
					</a>.
				</li>
			</ul>
		</p>
		<p>
			<a href="https://webgate.ec.europa.eu/cas/eim/external/register.cg">
				Create an EU Login account
			</a>
			(if you don't already have one)
		</p>
		<p>
			<a
					href="https://ecas.ec.europa.eu/cas/userdata/mobileApp/manageMyMobileDevices.cgi?ticket=ST-21002212-zzlP2O5Vk7v3Lrybvgu0tmdvnRA0XlwiJHbSfmzmifWYFm6R6x8wLNtKCziamsRjpHDNxt0piYplSsJkaRelQ9S-rS0vSrmBGYCBAqIgQI5dC4-SYzvmgEAHJps6zYyn5lODmUR3BUcznA8ENrwzMnuwoj7ocalACSrpk4iD6QMHyJSBuhF98UCNUhkyYu25tH0I20"
					>
				Register your mobile phone
			</a>
			(If you don't work for an EU institution)
		</p>
		<h1>
			Creating a survey
		</h1>
		<h2>
			How do I create a new survey?    
		</h2>
		<p>
			On the 'Welcome' or the 'Surveys' page:
			<ol>
				<li>
					Click <strong>New Survey</strong> &#8594; then <strong>Create new Survey</strong> - and a dialogue box will open.
				</li>
				<li>
					Once you have entered all mandatory information, click 'Create'.
				</li>
				<li>
					The tool will load your new survey into the system and open the 'Editor'
					automatically so you can start adding the detail.
				</li>
			</ul>
		</p>
		<h2>
			What type of surveys can I create?    
		</h2>
		<p>
			You can choose between the following options:
			<ul>
				<li>
					<strong>Standard survey</strong>
					<br />
					A conventional questionnaire.
				</li>
				<li>
					<strong>Quiz</strong>
					<br/>
					In a quiz, a final score is calculated for each participant. These can be
					used e.g. as skill tests or electronic exams. For more details see the
					<a
							href="https://circabc.europa.eu/sd/a/400e1268-1329-413b-b873-b42e41369a07/EUSurvey_Quiz_Guide.pdf"
							target="_blank"
							>
						EUSurvey quiz guide
					</a>.
					<br />
					<br />
					The quiz includes:
					<ul>
						<li>
							a scoring feature
						</li>
						<li>
							verification of participants' answers
						</li>
						<li>
							the possibility to provide feedback to your participants, depending on
							their answers
						</li>
						<li>
							additional result analysis designed specifically for quizzes.
						</li>
					</ul>
				</li>
				<li>
					<strong>BRP public consultation</strong>
					<br/>
					Specifically for public consultations run via the Better Regulation Portal
					(published on the
					<a href="https://ec.europa.eu/info/law/better-regulation/have-your-say">
						'Have your say'
					</a>
					Europa website).
					<br/>
					<br/>
					The BRP template provides:
					<ul>
						<li>
							set <strong>metadata fields</strong> allowing uniform identification of
							respondents across different surveys, thus simplifying the reporting;
						</li>
						<li>
							tailored<strong> privacy statement</strong> accounting for the specific
							constraints of public consultations;
						</li>
						<li>
							<strong>automatic opening and closing</strong> of the survey from BRP;
						</li>
						<li>
							automatic synchronisation (sending of data) of the respondents' answers
							to BRP for further processing.
						</li>
					</ul>
				</li>
			</p>
			<h2>
				How do I import an existing survey from my computer?    
			</h2>
			<p>
				<ol>
					<li>
						Go to the 'Welcome' page or the 'Surveys' page.
					</li>
					<li>
						Click on the 'New Survey' 'Import Survey' option - a dialogue box will open.
					</li>
					<li>
						Once you have selected the survey file from your computer, click on
						'Import' and it will be added to EUSurvey.
					</li>
				</ol>

				Note: you can only import surveys as zip files or with the file extension '.eus'.
			</p>
			<h2>
				Where can I find all the surveys that I have created?    
			</h2>
			<p>
				There are 2 ways:
				<ul>
					<li>
						on the dashboard page, you will find a list of all the surveys you have created; or
					</li>
					<li>
						go to the 'Surveys' page &#8594; select the 'My Surveys' option in the search panel.
					</li>
				</ul>
			</p>
			<h2>
				How do I open an existing survey for editing, etc.?
			</h2>
			<p>
				Go to the 'Surveys' page.
				<ol>
					<li>
						Click on the 'Open' icon of the survey you want - the 'Overview' page
						will open with several new tabs.
					</li>
					<li>
						Go to the 'Editor'.
					</li>
					<li>
						Test your survey, or access the survey's 'Results', 'Translations', 'Properties', etc.
					</li>
				</ol>
			</p>
			<h2>
				How do I export an existing survey?    
			</h2>
			<p>
				On the 'Surveys' page, search for the survey you want to export. You can
				either:

				<ul>
					<li>
						click on the 'Export' icon; or
					</li>
					<li>
						click on the 'Open' icon &#8594; from the 'Overview' page, click on the 'Export' icon.
					</li>
				</ul>
			</p>
			<p>
				Your survey, together with all your settings, will be saved to your
				computer.
			</p>
			<p>
				The file extension of an EUSurvey form file is '.eus'.
			</p>
			<h2>
				How do I copy an existing survey?    
			</h2>
			<p>
				Go to the 'Surveys' page.
				<ol>
					<li>
						Open the survey you want and click on the 'Copy' icon.
					</li>
					<li>
						In the dialogue box that opens you can change the necessary settings.
					</li>
					<li>
						Click on 'Create'.
					</li>
					<li>
						Your survey will be added to the list on the 'Surveys' page &#8594; you can
						start editing.
					</li>
				</ol>
			</p>
			<h2>
				How do I remove an existing survey?    
			</h2>
			<p>
				Go to the 'Surveys' page.
				<ol>
					<li>
						Open the survey you want.
					</li>
					<li>
						Click on the 'Delete' icon.
					</li>
				</ol>

				Once you have confirmed, your survey will be removed from the list of
				surveys.
			</p>
			<p>
				<strong>
					<u>Beware</u>
				</strong>: deleting a survey removes <strong>
					<u>all traces</u>
				</strong> of your
				questions and your results from the EUSurvey system! <u>This cannot be undone</u>!
			</p>
			<h2>
				How do I create WCAG compliant questionnaires with EUSurvey?    
			</h2>
			<p>
				The Web Content Accessibility Guidelines (WCAG) are guidelines for making
				content accessible, primarily for people with disabilities, but also for
				software such as mobile phones.
			</p>
			<p>
				If you want your survey to be WCAG-compliant, please follow the
				instructions explained
				<a
						href="https://circabc.europa.eu/d/a/workspace/SpacesStore/78b03213-5cf4-4aab-8e90-ada7e2eb1101/WCAG_tutorial%20.pdf"
						target="_blank"
						>
					in this document
				</a>.
			</p>
			<h1>
				Editing a survey
			</h1>
			<h2>
				How do I start the Editor?    
			</h2>
			<p>
				First make sure that you have opened an existing survey then go to the 'Surveys' page:
				<ol>
					<li>
						Click the 'Open' icon for the survey you want to edit.
					</li>
					<li>
						From the 'Overview' page, click 'Editor' to open it and start editing.
					</li>
				</ol>

				Make sure you regularly save your work.
			</p>
			<h2>
				How do I create a questionnaire via the EUSurvey editor?    
			</h2>
			<p>
				The editor consists of 5 different areas.

				<ol type="i">
					<li>
						<strong>Navigation panel:</strong>
						provides a structured view of the questionnaire, in which all items are
						represented by their respective text label.
						<br />
						When you select an item in the navigation panel, the Form area jumps to the
						selected item, which is then highlighted in blue.
					</li>
					<li>
						<strong>Toolbox pane:</strong>
						contains all the various items that you can add to your questionnaire,
						either by using the drag-and-drop feature or by double-clicking them.
					</li>
					<li>
						<strong>Form area:</strong>
						provides a preview of the questionnaire; items can be added to it and
						selected for editing.
					</li>
					<li>
						<strong>Element properties:</strong>
						displays the settings for selected items.
						<br />
						You can edit the items here, e.g. by changing the question text, adding
						help messages and changing all relevant settings to adapt the question to
						your needs.
					</li>
					<li>
						<strong>Toolbar:</strong>
						includes all available basic tasks that you can perform when creating the
						questionnaire.
					</li>
				</ol>

				For detailed information on how to use the 'Editor', go to the
				<a
						href="https://ec.europa.eu/eusurvey/resources/documents/Editor_Guide.pdf"
						target="_blank"
						>
					EUSurvey Editor Guide
				</a>.
			</p>
			<h2>
				How do I add or remove questions to my questionnaire?    
			</h2>
			<p>
				To add new items to your form or remove existing ones, first:
			</p>
			<p>
				&#8594; open the editor.
			</p>
			<p>
				Here, you will find a toolbox of available items on the left and the form
				area in the middle of the screen.
			</p>
			<p>
				The items contain default texts, with the item's name displayed as the
				question text.
			</p>
			<p>
				To add a new item (question, text field, image, etc.):
			</p>
			<p>
				&#8594; select one from the toolbox - you can either use the drag-and-drop
				feature or double-click on it.
			</p>
			<p>
				To remove an item from the form:
			</p>
			<p>
				&#8594; click the item to select it and click 'Delete'; as soon as you have
				confirmed, the item will be removed.
			</p>
			<p>
				See also
				<a href="#_Toc_4_2">
					'How to create a questionnaire via the EUSurvey editor?')
				</a>
			</p>
			<h2>
				How do I edit items in my questionnaire?    
			</h2>
			<p>
				Items can be <strong>selected for editing in the form area</strong> and    <strong>edited in the element properties pane</strong> of the Editor - see
				<a href="#_Toc_4_2">
					'How to create a questionnaire via the EUSurvey editor?')
				</a>
			</p>
			<p>
				Click an item in the form area to select it.
			</p>
			<p>
				Your selected item appears in blue, with the respective options visible in
				the element properties pane. You can edit the elements there, e.g.
				changing/editing the text in the question, adding help messages and
				changing all relevant settings to adapt the question to your needs.
			</p>
			<p>
				To edit a text:
			</p>
			<ol>
				<li>
					click on the text or the pen icon
				</li>
				<li>
					change the text.
				</li>
				<li>
					click "Apply" to see the changes in the form area.
				</li>
			</ol>
			<p>
				By default, the element properties pane displays all the basic options.
			</p>
			<p>
				To display more options, click 'Advanced'.
			</p>
			<p>
				For matrix and text questions, you can also choose the individual
				questions/answers/rows/columns for the item by clicking the respective
				label text as indicated below. This way, you can e.g. select individual
				questions of a matrix or table item and make them mandatory.
			</p>
			<h2>
				How do I copy items?    
			</h2>
			<p>
				To copy items in your form:
			</p>
			<p>
				&#8594; open the Editor.
			</p>
			<ol>
				<li>
					Select the item(s).
				</li>
				<li>
					Click 'Copy'.
				</li>
				<li>
					Move the placeholder from the toolbox to the form area as described
					above or select the item in the form area and click 'Paste after'.
				</li>
			</ol>
			<p>
				Any items copied or cut are depicted by an icon at the top of the toolbox
				pane.
			</p>
			<p>
				v add them to the questionnaire again using the drag-and-drop feature.
			</p>
			<p>
				To cancel:
			</p>
			<p>
				&#8594; use the button next to the item.
			</p>
			<p>
				See also
				<a href="#_Toc_4_2">
					'How to create a questionnaire via the EUSurvey editor?')
				</a>
			</p>
			<h2>
				How do I add or remove possible answers in choice questions?    
			</h2>
			<p>
				<ol>
					<li>
						Click the plus button in the element properties pane to add answers; click the minus button to remove them.
					</li>
					<li>
						Edit the existing answers by clicking the pen icon next to 'Possible
						answers'.
					</li>
					<li>
						Edit them in the rich text editor.
					</li>
				</ol>

				See also
				<a href="#_Toc_4_2">
					'How to create a questionnaire via the EUSurvey editor?')
				</a>
			</p>
			<h2>
				Can I make a question mandatory?    
			</h2>
			<p>
				<ol>
					<li>
						In the editor, select the question element that you want to make mandatory.
					</li>
					<li>
						Then, go to the element properties pane.
					</li>
					<li>
						Tick the Mandatory check-box.
					</li>
				</ol>

				The mandatory question will be preceded with a red asterisk.
			</p>
			<h2>
				How do I move items within the questionnaire?    
			</h2>
			<p>
				In the editor, you can change the position of an item in your questionnaire
				by using one of the following options:
			</p>
			<p>
				&#8594; Drag-and-drop:
				<br/>
				Select the item in the form area; drag it to where you want in the
				questionnaire.
			</p>
			<p>
				&#8594; Move buttons:
				<br/>
				Select the item you want to move; use the move up/move down buttons in the
				Toolbar on top of the form area.
			</p>
			<p>
				&#8594; Cut-and-paste:
				<br/>
				Cut the item you want to move and use the drag-and-drop feature to move the
				placeholder to where you want to paste it.
			</p>
			<h2>
				How do I use the visibility feature (dependencies)    
			</h2>
			<p>
				With this feature, you can display and hide items depending on the answers
				participants give to either single/multiple choice or matrix questions (see
				also
				<a href="TODO">
					'How to create a questionnaire by using the EUSurvey editor?')
				</a>
			</p>
			<p>
				By default, all items are set to always being visible, meaning that
				everybody will see the question when answering the survey.
			</p>
			<p>
				Follow these steps to create a dependent question.
			</p>
			<ol>
				<li>
					Add a single/multiple choice or matrix question to your questionnaire.
				</li>
				<li>
					Add further items to your questionnaire.
				</li>
				<li>
					Select an item that follows a single/multiple choice or matrix item and
					that should only appear when a specific answer has been chosen.
				</li>
				<li>
					Click the pen icon to edit the visibility settings. All available
					single choice, multiple choice and matrix questions that are placed
					above the selected item(s) are displayed, including the question text
					and the possible answers.
				</li>
				<li>
					Select the answer that, when chosen, will display the selected item.
				</li>
				<li>
					Click 'Apply' to confirm the visibility setting.
				</li>
			</ol>
			<p>
				If multiple items are selected, you can edit the visibility settings for
				all of them at once.
			</p>
			<p>
				<strong>Note:</strong>
				This change will only affect the questionnaire on the test page and in
				published mode. All items will still be visible in the editor.
			</p>
			<p>
				When activated, arrows are displayed next to the connected items to show
				the visibility settings in the form area.
			</p>
			<p>
				Answers that trigger any item are marked with an arrow pointing down.
			</p>
			<p>
				Items that are triggered by any answer are marked with an arrow pointing
				up.
			</p>
			<p>
				When moving the pointer over the arrows or IDs in the element properties
				pane, the connected items are highlighted in the form area and navigation
				pane.
			</p>
			<p>
				Items with visibility settings that have been edited will be hidden in the
				questionnaire until the respondent selects at least one of the configured
				answers.
			</p>
			<h2>
				Can I change the order of answers in a single or multiple choice question?
			</h2>
			<p>
				When creating a single or multiple choice question, you can generate their
				answers in three different ways:

				<ul>
					<li>
						original order
					</li>
					<li>
						alphabetical order
					</li>
					<li>
						random order
					</li>
				</ul>

			</p>
			<p>
				Original order: this option displays the original order of your answers.
			</p>
			<p>
				Alphabetical order: select this option if the answers should be displayed
				alphabetically.
			</p>
			<p>
				Random order: select this option if the answers should be randomly
				displayed.
			</p>
			<h2>
				How do I give other users permission to edit my survey?    
			</h2>
			<p>
				<ol>
					<li>
						Open your survey and open the 'Privileges' page.
					</li>
					<li>
						Click on 'Add new user' or 'Add department'.
					</li>
					<li>
						A wizard pops up that guides you through the process of adding users.
					</li>
					<li>
						You can give them specific access rights - simply click on the colour to
						change the rights.

						<ul>
							<li>
								green: access to read and write
							</li>
							<li>
								yellow: access to read
							</li>
							<li>
								red: no access
							</li>
						</ul>
					</li>
				</ul>
			</p>
			<p>
				Added users will automatically see your survey in their list of surveys
				next time they log into EUSurvey.
			</p>
			<p>
				External owners or survey organisers cannot see EU fields on the
				'Privileges' tab/'Add users' button. Therefore, they cannot provide access
				to these individuals directly.
			</p>
			<p>
				<a href="https://ec.europa.eu/eusurvey/home/support">Contact us</a>
				to request access for external users.
			</p>
			<p>
				Read more about this under '
				<a href="#_Toc_10_7">
					How do I give other users access to my survey?
				</a>'
			</p>
			<h2>
				Which languages are supported by the application?    
			</h2>
			<p>
				Languages which can be encoded in '3byte UTF-8' can be used to create a
				survey.
			</p>
			<h2>
				Why UTF-8 and which fonts should be used?    
			</h2>
			<p>
				Targeted participants can easily display the survey if they have the font
				you used installed in their browser. UTF-8 is the most common encoding for
				the HTML pages. By contrast, choosing non-supported fonts may affect the
				rendering of PDF export.
			</p>
			<p>
				We recommend using these <strong>supported character sets</strong>:
			</p>
			<ul>
				<li>
					Freesans
					<a
							href="https://circabc.europa.eu/d/a/workspace/SpacesStore/602784e1-bb06-4b0d-a474-eae77dbe2d11/EUSurvey-SupportedCharacterSet(freesans).txt"
							target="_blank"
							>
						(
						https://circabc.europa.eu/sd/a/36f72861-fc6e-4fe1-87d6-0a8e1c6fa161/EUSurvey-SupportedCharacterSet(freesans).txt)
					</a>
				</li>
				<li>
					Freemono
					<a
							href="https://circabc.europa.eu/d/a/workspace/SpacesStore/dfc640e9-56ac-4d25-8361-4b07dbbd0579/EUSurvey-SupportedCharacterSet(freemono).txt"
							target="_blank"
							>
						(
						https://circabc.europa.eu/sd/a/55ce0f35-b3cc-4712-80bf-af42800a278f/EUSurvey-SupportedCharacterSet(freemono).txt)
					</a>
				</li>
				<li>
					Freeserif
					<a
							href="https://circabc.europa.eu/d/a/workspace/SpacesStore/5b98b11a-f306-4d97-aab3-ec1c7a24965f/EUSurvey-SupportedCharacterSet(freeserif).txt"
							target="_blank"
							>
						(
						https://circabc.europa.eu/sd/a/29cd78bb-9eeb-40b1-a22f-b54700750537/EUSurvey-SupportedCharacterSet(freeserif).txt)
					</a>
				</li>
				<li>
					Commonly supported character-set
					<a
							href="https://circabc.europa.eu/d/a/workspace/SpacesStore/621396c0-92d3-49a3-acd0-546b0c1a170b/EUSurvey-SupportedCharacterSet(common).txt"
							target="_blank"
							>
						(
						https://circabc.europa.eu/sd/a/1eb30efd-e2d8-4c3b-9f55-533bb903f7d0/EUSurvey-SupportedCharacterSet(common).txt
					</a>
				</li>
			</ul>
			<p>
				<strong>'Freesans' is the default font used</strong>
			</p>
			<p>
				If in doubt, run a PDF export of your final survey to check if it is
				rendered correctly in PDF.
			</p>
			<p>
				However, be aware that some answers may not to be rendered correctly in
				PDF. Your respondents can choose any font from those supported by the
				application.
			</p>
			<p>
				Even though the tool cannot render the characters respondents use, these
				will be saved correctly on the EUSurvey's database. Thus, they can be
				exported from the results page.
			</p>
			<h2>
				What does 'Complexity' mean?    
			</h2>
			<p>
				Adding too many items or dependencies to your survey makes it too
				'complex'. This can lead to the system slowing down for participants when
				filling out your questionnaire.
			</p>
			<p>
				Your survey could have a high level of complexity for several reasons:
			</p>
			<ul>
				<li>
					too many table/matrix items
				</li>
				<li>
					too many dependencies
				</li>
				<li>
					too many cascading dependencies
				</li>
			</ul>
			<p>
				For more information, see our
				<a
						href="https://circabc.europa.eu/sd/d/281e626e-279e-45df-8581-1f6e04feff51/BestPractices-EUSurvey.pdf"
						target="_blank"
						>
					best practices
				</a>
				<u> guide</u>
				.
			</p>
			<h1>
				Survey security
			</h1>
			<h2>
				How do I restrict access to my survey?    
			</h2>
			<p>
				By default, a survey is publicly available as soon as it has been
				published.
			</p>
			<p>
				To allow only privileged users to access the survey:
			</p>
			<p>
				&#8594; set it to <strong>'Secured'</strong> in the 'Security settings' on the
				'Properties' page.
			</p>
			<p>
				You can then allow access to your privileged users by means of the
				following actions below.
			</p>
			<ul>
				<li>
					You can invite your respondents with the EUSurvey invitation module
					(see '
					<a href="#_Toc_13_0">
						Inviting respondents
					</a>
					<u>'</u>
					). Each respondent will get a unique access link. OR
				</li>
				<li>
					You can secure your survey via EU Login. In the 'Properties' page
					enable the options 'Secure your survey' and 'Secure with EU Login'. If
					you are an EU staff member, you can choose either to:

					<ul>
						<li>
							allow all users with an EU Login account (EU staff and external EU Login
							accounts) to access your survey, or
						</li>
						<li>
							grant access only to EU staff. OR
						</li>
					</ul>

				</li>
				<li>
					Set up a password which will be the same for all respondents to whom
					you will send the survey location link and the global password (see "
					<a href="#_Toc_5_2">
						How do I set a password for my survey?"
					</a>
					).
				</li>
			</ul>

			<h2>
				How do I set a password for my survey?    
			</h2>
			<p>
				Go to the 'Secured with password' option under 'Properties'.
			</p>
			<p>
				To invite individuals to access your secured survey, see '
				<a href="#_Toc_13_0">
					Inviting respondents
				</a>
				'.
			</p>
			<h2>
				How do I ensure that a user does not submit an excessive number of answers?    
			</h2>
			<p>
				In the 'Properties' page, enable the options 'Secure your survey' and
				'Secure with EU Login' .
			</p>
			<p>
				Set the number in the 'Contribution per user' option.
			</p>
			<h2>
				How do I prevent robots from submitting multiple answers?    
			</h2>
			<p>
				Automatic scripts can falsify an online survey's outcome by submitting a
				high number of answers. To prevent this, you can use EUSurvey to make
				respondents solve a    <a href="http://fr.wikipedia.org/wiki/CAPTCHA" target="_blank">CAPTCHA</a>
				challenge before submitting an answer.
			</p>
			<p>
				You can enable/disable the CAPTCHA in the 'Security' under 'Properties'.
			</p>
			<p>
				N.B. Although this won't prevent all fraud, it might discourage people
				continually trying to falsify survey results.
			</p>
			<h2>
				Can I enable respondents to access their answers after submission?    
			</h2>
			<p>
				Yes!
			</p>
			<p>
				Go to 'Security' under 'Properties' &#8594; enable the 'Allow participants to
				change their contribution' option.
			</p>
			<p>
				Participants need to know their contribution-ID, provided after they
				submitted their answer.
			</p>
			<p>
				To edit/change their answers after submission, participants should go to
				the EUSurvey homepage
				<a href="https://ec.europa.eu/eusurvey" target="_blank">
					https://ec.europa.eu/eusurvey
				</a>
				.
			</p>
			<p>
				Below the 'Register Now!' button is a link to the
				<a
						href="https://ec.europa.eu/eusurvey/home/editcontribution"
						target="_blank"
						>
					access page for individual contributions
				</a>
				. Here, participants must fill in their individual contribution-ID for the
				system to open it for editing.
			</p>
			<h1>
				Testing a survey
			</p>
			<h2>
				Can I test my survey and see what it looks like once it has been published?    
			</h2>
			<p>
				Yes. Open it in EUSurvey and click on 'Test'.
			</p>
			<p>
				You will see its current draft, and you can test every feature of the
				published form.
			</p>
			<p>
				You can also save your test answer as a draft or directly submit it.
			</p>
			<h2>
				How can my colleagues test my survey before it's published?    
			</h2>
			<p>
				To give your colleagues access to your survey's 'test' page:
				<ol>
					<li>
						open your survey in EUSurvey
					</li>
					<li>
						go to the 'Privileges' tab and click on 'Add User' or 'Add Department'
					</li>
					<li>
						A wizard will guide you through the process of adding your colleagues.
					</li>
				</ol>
			</p>
			<p>
				To give them appropriate access rights for testing:
			</p>
			<p>
				&#8594; change the colour of 'Access Form Preview' to green - simply click on the
				colour to change the rights.
			</p>
			<p>
				The added users will automatically see the survey in their 'Surveys' page
				once they log in.
			</p>
			<p>
				Read more about this under "
				<a href="#_Toc_10_7">
					How do I give other users access to my survey?
				</a>
				"
			</p>
			<p>
				External owners or survey organisers cannot see EU fields on the
				'Privileges' tab/'Add users' button. Therefore, they cannot provide access
				for these individuals directly.
			</p>
			<p>
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
				Important: Be sure to finish editing and testing your survey before
				starting on translations!

				<ol>
					<li>
						Open your survey and go to the 'Translations' page.
					</li>
					<li>
						Click on 'Add New Translation'.
					</li>
					<li>
						Select the language from the list of supported languages.
					</li>
					<li>
						If the required language does not appear in the list, select 'other' and
						specify a valid two-letter ISO 639-1 language code.
					</li>
					<li>
						Click 'OK' to add the empty translation form to your survey.
					</li>
				</ol>
			</p>
			<p>
				For more information on how to add new labels to your newly created
				translation, read '
				<a href="#_Toc_7_3">
					Can I edit an existing translation online?
				</a>
				'.
			</p>
			<p>
				Select the 'To Publish' box if the translation is to be published along
				with your survey.
			</p>
			<p>
				Once you add a translation for publishing, respondents can choose from the
				available languages directly from the survey link.
			</p>
			<h2>
				How can I upload an existing translation to my survey?    
			</h2>
			<p>
				<ol>
					<li>
						Open your survey and open the 'Translations' page.
					</li>
					<li>
						Click on 'Upload existing translation'.
					</li>
					<li>
						A wizard will guide you through the process of uploading the
						translation.
					</li>
				</ol>
			</p>
			<h2>
				Can I edit an existing translation online?    
			</h2>
			<p>
				Yes!
			</p>
			<p>
				<ol>
					<li>
						Open your survey, go to the 'Translations' page.
					</li>
					<li>
						Select one or more translations to edit.
					</li>
					<li>
						Select 'Edit Translations' from the action icons.
					</li>
					<li>
						Click 'Ok' &#8594; this will open the online translation editor allowing you
						to edit multiple translations at a time.
					</li>
					<li>
						Click 'Save' to make sure that your changes are written to the system.
					</li>
				</ol>
			</p>
			<p>
				To edit just one translation:
			</p>
			<p>
				<ol>
					<li>
						open your survey
					</li>
					<li>
						go to the 'Translations' page
					</li>
					<li>
						click on the Pen icon in the 'Action' column.
					</li>
				</ol>
			</p>
			<h2>
				Can I create my translations offline?    
			</h2>
			<p>
				Yes! The workflow to follow is:
			</p>
			<p>
				<ol>
					<li>
						Go to 'Translations'
					</li>
					<li>
						Export a language having the status 'Complete' into XLS.
					</li>
					<li>
						Change the language code (ISO 639-1) at the top of the file (Cell B1).
					</li>
					<li>
						Translate all available text labels into the new language (Column C).
					</li>
					<li>
						Once the survey has been translated offline, save it.
					</li>
					<li>
						Click 'Upload existing translation' to import the translation.
					</li>
				</ol>
			</p>
			<p>
				That's done. Verify the translation from the 'Test' tab.
			</p>
			<h2>
				How do I publish/unpublish my translations?
			</h2>
			<p>
				To publish a survey in multiple languages,:
			</p>
			<p>
				<ol>
					<li>
						open your survey
					</li>
					<li>
						go to the "Translations" page
					</li>
					<li>
						tick/untick the individual translations you want to publish/unpublish
						under "To Publish".
					</li>
					<li>
						Then go to the 'Overview' page of your survey to publish your survey.
					</li>
				</ol>
			</p>

			<p>
				If the survey had been published before the translations were
				added/removed, click on "Apply Changes".
			</p>
			<p>
				To ensure that no translations with missing text are published, you cannot
				publish translations that have empty labels (translations that are not
				'Complete').
			</p>
			<p>
				To ensure your translation has no empty labels, use the online translation
				editor. Look for cells with a red background.
			</p>
			<h2>
				Can I upload a translation in a non-European language?    
			</h2>
			<p>
				The application also supports non-European languages.
			</p>
			<p>
				Select 'Other' in the uploading process and introduce a valid two-letter
				<a href="https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes">
					ISO 639-1
				</a>
				language code.
			</p>
			<h2>
				What does 'Request Machine Translation' mean?    
			</h2>
			<p>
				A <strong>Machine Translation (MT)</strong> can automatically translate
				your questionnaire on EUSurvey. The tool uses the Commission eTranslation
				service.
			</p>
			<p>
				On the 'Translations' page, you can request machine translations in several
				ways:
			</p>
			<ul>
				<li>
					when adding a new translation, tick the 'Request Machine Translation'
					checkbox (for a translation of your survey's pivot language)
				</li>
				<li>
					click the 'Request Translations' button in the 'Action' column (for a
					translation of your survey's pivot language)
				</li>
				<li>
					select all languages you want to translate your survey into (including
					at least one complete translation); then select 'Request Translations'
					and click 'Ok'.
				</li>
			</ul>
			<p>
				The status of the translations will change to 'Requested', until they are
				completed.
			</p>
			<p>
				For changes in the status, check the 'Translations' page.
			</p>
			<p>
				Machine translations will work like other translations you added manually,
				i.e. they won't be published automatically, and adding new items to your
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
				You should contact DGT's editing unit before finalising your survey. Its
				editors will check that your survey is clearly drafted and presented. For
				more info, see
				<a
						href="https://myintracomm.ec.europa.eu/serv/en/dgt/Pages/index.aspx"
						target="_blank"
						>
					DGT's MyIntraComm pages
				</a>
				.
			</p>
			<p>
				DGT can also translate your survey into the official EU languages.
			</p>
			<p>
				Export it as an XML file and send it via Poetry with the requester code of
				the DG concerned. It should be a maximum of 15,000 characters, excl. spaces
				(Word count).
			</p>
			<h1>
				Publishing a survey
			</h1>
			<h2>
				How do I publish my survey?    
			</h2>
			<p>
				To publish a survey from a current working draft:
			</p>
			<p>
				&#8594; go to the 'Overview' page and click on 'Publish'.
			</p>
			<p>
				After confirmation, the system will automatically create a working copy of
				your survey and put it online, along with the translations you selected for
				publication (See '
				<a href="#_Toc_7_5">
					How do I publish/unpublish my translations?
				</a>
				').
			</p>
			<p>
				Find the link to your published survey under 'Survey Location' on the
				'Overview' page.
			</p>
			<p>
				To unpublish your survey &#8594; click the 'Unpublish' button.
			</p>
			<p>
				You can still access the unpublished survey, along with your current
				working draft.
			</p>
			<p>
				This means that the unpublished survey doesn't have to be replaced by your
				current working draft but can be republished if necessary.
			</p>
			<h2>
				Can I customise the URL to my survey?    
			</h2>
			<p>
				Yes!
			</p>
			<p>
				By changing your survey's 'Alias' you can get a more meaningful URL for
				your survey.
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
				If you change the alias of a published survey &#8594; go to the 'Overview' page
				and click on 'Apply Changes'.
			</p>
			<p>
				Aliases must be unique in EUSurvey. If another survey has already taken
				your alias, you will get a warning
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
				You can also<strong> bring respondents directly</strong> to a specific
				translation:
			</p>
			<p>
				<strong>https://ec.europa.eu/eusurvey/runner/SurveyAlias?surveylanguage=LC</strong>
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
					is the wished <strong>ISO 639-1 language code</strong> (e.g. FR for
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
				Yes, EUSurvey can send you a reminder email before your survey ends. This
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
				If you want to remove answers from your survey, please Read: "Will I
				lose any answers submitted when I change my form?".
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
						example "European Commission").
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
				You can create an anonymous survey by setting the 'Privacy' in the
				'Security Settings' of your properties to <strong>'No'.</strong>
			</p>
			<p>
				Then, all collected user information will be <strong>replaced</strong> by    <strong>'Anonymous'</strong>.
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

		</div>
	</div>
	</div>

<%@ include file="../footer.jsp" %>	

</body>
</html>
