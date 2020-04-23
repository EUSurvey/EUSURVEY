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
					 height: 110px;
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
	<link rel="stylesheet" href="${contextpath}/resources/js/tree/treeview.css?version=<%@include file="../version.txt" %>" type="text/css">


	<script type="text/javascript">
	
		$(document).ready(function(){
			
			ddtreemenu.createTree("treemenu", false, 0,"${contextpath}");
			
			 $("a.anchorTop").click(function(){
				 $('html, body').animate({scrollTop : 0},100);
					return false;
			 });
			 
		});
	
	</script>
	
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
			<li><a class="anchorlink head" href="#_Toc0">General questions</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc0-1">What is EUSurvey?</a></li>
					<li><a class="anchorlink" href="#_Toc0-2">When do I use EUSurvey?</a></li>
					<li><a class="anchorlink" href="#_Toc0-3">What are the limits of EUSurvey?</a></li>
					<li><a class="anchorlink" href="#_Toc0-4">What are the features of EUSurvey?</a></li>
					<li><a class="anchorlink" href="#_Toc0-5">Form management</a></li>
					<li><a class="anchorlink" href="#_Toc0-6">Result management</a></li>
					<li><a class="anchorlink" href="#_Toc0-7">Where do I find additional information about EUSurvey?</a></li>
					<li><a class="anchorlink" href="#_Toc0-8">Whom do I contact if there are technical problems with EUSurvey?</a></li>
					<li><a class="anchorlink" href="#_Toc0-9">Whom do I contact if I have new ideas for improving EUSurvey?</a></li>
					<li><a class="anchorlink" href="#_Toc0-10">Which browsers are supported by EUSurvey?</a></li>
					<li><a class="anchorlink" href="#_Toc0-11">EUSurvey disclaimer (for non-EU users only)</a></li>
					<li><a class="anchorlink" href="#_Toc0-12">Can my participants answer my survey from a mobile device?</a></li>
					<li><a class="anchorlink" href="#_Toc0-13">Is there a minimum screen size?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc1">Login and EU Login registration</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc1-1">I have an EU Login account. Do I need to register separately for EUSurvey?</a></li>
					<li><a class="anchorlink" href="#_Toc1-2">How do I connect to EUSurvey?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc2">Creating a survey</a>
				<ul>
					<li><a class="anchorlink " href="#_Toc2-1">How do I create a new survey?</a></li>
					<li><a class="anchorlink" href="#_Toc2-2">How do I import an existing survey from my computer?</a></li>
					<li><a class="anchorlink" href="#_Toc2-3">How do I import an existing survey from IPM?</a></li>
					<li><a class="anchorlink" href="#_Toc2-4">Where do I find all the surveys I have created?</a></li>
					<li><a class="anchorlink" href="#_Toc2-5">How do I open an existing survey for editing, etc.?</a></li>
					<li><a class="anchorlink" href="#_Toc2-6">How do I export an existing survey?</a></li>
					<li><a class="anchorlink" href="#_Toc2-7">How do I copy an existing survey?</a></li>
					<li><a class="anchorlink" href="#_Toc2-8">How do I remove an existing survey?</a></li>
					<li><a class="anchorlink" href="#_Toc2-9">How do I create WCAG compliant questionnaires with EUSurvey?</a></li>
					<li><a class="anchorlink" href="#_Toc2-10">How do I create a quiz survey?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc3">Editing a survey</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc3-1">How do I start the Editor?</a></li>
					<li><a class="anchorlink" href="#_Toc3-2">How do I create a questionnaire by using the EUSurvey editor?</a></li>
					<li><a class="anchorlink" href="#_Toc3-3">How do I add or remove questions to my questionnaire?</a></li>
					<li><a class="anchorlink" href="#_Toc3-4">How do I edit elements in my questionnaire?</a></li>
					<li><a class="anchorlink" href="#_Toc3-10">How do I copy elements?</a></li>
					<li><a class="anchorlink" href="#_Toc3-11">How do I add or remove possible answers in choice questions?</a></li>
					<li><a class="anchorlink" href="#_Toc3-12">Can I make a question mandatory?</a></li>
					<li><a class="anchorlink" href="#_Toc3-13">How do I move elements within the questionnaire?</a></li>
					<li><a class="anchorlink" href="#_Toc3-14">How do I use the visibility feature (dependencies)</a></li>
					<li><a class="anchorlink" href="#_Toc3-7">Can I generate the order of answers in a single or multiple choice question?</a></li>
					<li><a class="anchorlink " href="#_Toc3-5">How do I give other users permission to edit my survey?</a></li>
					<li><a class="anchorlink " href="#_Toc3-8">Which languages are supported by the application?</a></li>
					<li><a class="anchorlink " href="#_Toc3-9">So why UTF-8 and which fonts should be used?</a></li>
					<li><a class="anchorlink " href="#_Toc3-6">What does "Complexity" mean?</a></li>			
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc4">Survey security</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc4-1">How do I restrict access to my survey?</a></li>
					<li><a class="anchorlink" href="#_Toc4-3">How do I set a password for my survey?</a></li>
					<li><a class="anchorlink" href="#_Toc4-4">How do I ensure that a user does not submit more than a defined number of contributions to my survey?</a></li>
					<li><a class="anchorlink" href="#_Toc4-5">What do I do to prevent robots from submitting multiple contributions to my survey?</a></li>
					<li><a class="anchorlink" href="#_Toc4-6">Can I enable my participants to access their contributions after submission?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc5">Testing a survey</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc5-1">Can I see how my survey will behave after it has been published?</a></li>
					<li><a class="anchorlink" href="#_Toc5-2">How can my colleagues test my survey before it is published?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc6">Translations</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc6-1">How do I translate a survey?</a></li>
					<li><a class="anchorlink" href="#_Toc6-2">How can I upload an existing translation to my survey?</a></li>
					<li><a class="anchorlink" href="#_Toc6-3">Can I edit an existing translation online?</a></li>
					<li><a class="anchorlink" href="#_Toc6-4">Can I create my translations offline?</a></li>
					<li><a class="anchorlink" href="#_Toc6-6">How do I publish/unpublish my translations? Why can't I publish this translation? What is an "Incomplete" translation?</a></li>
					<li><a class="anchorlink" href="#_Toc6-7">Can I upload a translation in a non-European language?</a></li>	
					<li><a class="anchorlink" href="#_Toc6-8">What does "Request Machine Translation" mean?</a></li>
					<li><a class="anchorlink" href="#_Toc6-5">Instructions for EU staff</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc7">Publishing a survey</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc7-1">How do I publish my survey?</a></li>
					<li><a class="anchorlink" href="#_Toc7-2">Can I customize the URL to my survey?</a></li>
					<li><a class="anchorlink" href="#_Toc7-7">Can I link directly to a translation of my survey?</a></li>
					<li><a class="anchorlink" href="#_Toc7-3">How do I make my survey publish itself while I am on holiday?</a></li>
					<li><a class="anchorlink" href="#_Toc7-4">Can I receive a reminder of when my survey will end?</a></li>
					<c:if test="${enablepublicsurveys}">
						<li><a class="anchorlink" href="#_Toc7-5">How do I advertise my survey on the list of public surveys on the EUSurvey website?</a></li>
					</c:if>
					<li><a class="anchorlink" href="#_Toc7-6">For EU staff: What are the official requirements for launching an open public consultation ("Your Voice in Europe" website)?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc8">Managing your survey</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc8-1">If I discover an error in my survey, can I correct it?</a></li>
					<li><a class="anchorlink" href="#_Toc8-2">Will I lose any answers submitted when I change my form?</a></li>
					<li><a class="anchorlink" href="#_Toc8-3">How can I change the title of my survey?</a></li>
					<li><a class="anchorlink" href="#_Toc8-4">How can I change my survey's contact address?</a></li>
					<li><a class="anchorlink" href="#_Toc8-5">How do I adjust the default confirmation message? </a></li>
					<li><a class="anchorlink" href="#_Toc8-6">How do I adjust the default escape message? </a></li>
					<li><a class="anchorlink" href="#_Toc8-7">Archiving feature</a></li>
					<li><a class="anchorlink" href="#_Toc8-8">How do I give other users access to my survey?</a></li>
					<li><a class="anchorlink" href="#_Toc8-9">What are Activity logs?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc9">Analysing, exporting and publishing results</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc9-1">Where can I find the contributions submitted by my participants?</a></li>
					<li><a class="anchorlink" href="#_Toc9-2">How can I download submitted answers?</a></li>
					<li><a class="anchorlink" href="#_Toc9-3">How can I find and analyse a defined subset of all contributions?</a></li>
					<li><a class="anchorlink" href="#_Toc9-4">How can I get back to the full set of answers, after defining a subset of contributions?</a></li>
					<li><a class="anchorlink" href="#_Toc9-5">How can I publish my results? </a></li>
					<li><a class="anchorlink" href="#_Toc9-6">How can I access the published results?</a></li>
					<li><a class="anchorlink" href="#_Toc9-7">How do I give other users access to the results of my survey?</a></li>
					<li><a class="anchorlink" href="#_Toc9-8">I cannot unzip my exported files</a></li>
					<li><a class="anchorlink" href="#_Toc9-9">Published results - protection of personal information uploaded by participants</a></li>
					<li><a class="anchorlink" href="#_Toc9-10">How do I design a survey to publish the results with or without personal information?</a></li>
					<li><a class="anchorlink" href="#_Toc9-11">Why are my results not up-to-date?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc10">Design and layout</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc10-1">How do I change the general look and feel of my survey?</a></li>
					<li><a class="anchorlink" href="#_Toc10-2">How can I create my own survey themes?</a></li>
					<li><a class="anchorlink" href="#_Toc10-3">How do I add a logo to my survey?</a></li>
					<li><a class="anchorlink" href="#_Toc10-4">How do I add useful links to my survey?</a></li>
					<li><a class="anchorlink" href="#_Toc10-5">Where do I upload background documents for my survey?</a></li>
					<li><a class="anchorlink" href="#_Toc10-6">How do I create a multi-page survey?</a></li>
					<li><a class="anchorlink" href="#_Toc10-7">How do I enable automatic numbering for my survey?</a></li>
					<li><a class="anchorlink" href="#_Toc10-8">Can I create a customised skin for my survey?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc11">Managing contacts and invitations</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc11-1">What is the "Address Book"?</a></li>
					<li><a class="anchorlink" href="#_Toc11-2">What are the "Attributes" of a contact?</a></li>
					<li><a class="anchorlink" href="#_Toc11-3">How do I add new contacts to my address book?</a></li>
					<li><a class="anchorlink" href="#_Toc11-4">What is a "Registration Form"?</a></li>
					<li><a class="anchorlink" href="#_Toc11-5">How do I import multiple contacts from a file to my address book?</a></li>
					<li><a class="anchorlink" href="#_Toc11-6">How do I edit an attribute value for multiple contacts at a time?</a></li>
					<li><a class="anchorlink" href="#_Toc11-7">Can I export contacts from my address book to my computer?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc12">Inviting participants</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc12-1">How do I specify a set of possible participants? What is a "Guest List"?</a></li>
					<li><a class="anchorlink" href="#_Toc12-2">How do I edit/remove an existing guest list?</a></li>
					<li><a class="anchorlink" href="#_Toc12-3">How do I send my participants an invitation email?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc13">Managing your personal account</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc13-1">How do I change my password?</a></li>
					<li><a class="anchorlink" href="#_Toc13-2">How do I change my email address?</a></li>
					<li><a class="anchorlink" href="#_Toc13-3">How do I change my default language?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc14">Privacy</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc14-1">This system uses cookies. What information is saved there?</a></li>
					<li><a class="anchorlink" href="#_Toc14-2">What information is stored by EUSurvey when participants submit a contribution?</a></li>
					<li><a class="anchorlink" href="#_Toc14-3">Do I need to include a privacy statement?</a></li>
				</ul>
			</li>
		</ul>
	</div>
	
	<h1 style="margin-top: 40px"><a class="anchor" name="_Toc0"></a>General questions</h1>
	<h2><a class="anchor" name="_Toc0-1"></a>What is EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>EUSurvey is an online survey management tool to create, publish and manage questionnaires and other interactive forms in most web browsers.</p>
	<h2><a class="anchor" name="_Toc0-2"></a>When do I use EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Use EUSurvey whenever you want to make a questionnaire or interactive form accessible online, or to record a large number of similar datasets.</p>
	<h2><a class="anchor" name="_Toc0-3"></a>What are the limits of EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>EUSurvey may not be the right tool for your project if:</p>
		<ul>
			<li>different participants need to work on the same contribution before it is submitted</li>
			<li>contributions need to be validated before they can be submitted</li>
		</ul>
	<p>Please contact DIGIT-EUSURVEY-SUPPORT for additional information, upcoming features and possible workarounds.</p>
	<h2><a class="anchor" name="_Toc0-4"></a>What are the features of EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p><b>Customizable forms</b><br />In the easy-to-use editor you can choose from a variety of question types; from simple text and multiple-choice questions to spreadsheet questions or multimedia survey elements. Structure your survey using special structural elements.</p>
	<p><b>Dependent questions</b><br />EUSurvey can display additional questions and fields, depending on the answers given by the participant, making the survey interactive.</p>
	<p><b>Scheduled publishing</b><br />Publish and unpublish your survey automatically at a specified point in time.</p>
	<p><b>Modify your form after publication</b><br />You can modify a published survey without losing any contributions.</p>
	<p><b>Languages</b><br />The user interface is available in 23 of the official EU languages, and you can translate your form into any of the 136 languages covered by ISO 639-1, from Abkhaz to Zulu.</p>
	<p><b>Security</b><br />EUSurvey has the infrastructure needed to secure the online forms.</p>
	<p><b>Sending out invitations directly from the application</b><br />Selected contacts can be managed in the 'Address Book' and used to send out individual emails to every single contact containing individual access links.</p>
	<p><b>Advanced privacy</b><br />You can guarantee the participant's privacy by creating an anonymous form. Connection details will then not be available to you as the author of the form.</p>
	<p><b>Customise the look and feel</b><br />With the built-in CSS style editor and embedded rich-text editors for all visible elements, you have full control over the form's layout. A large list of survey themes makes it easy to adapt the form to a project's identity. You can choose between single page and multi-page surveys.</p>
	<p><b>Save a contribution as draft</b><br />You can allow participants to save their contribution as a draft on the server and continue later.</p>
	<p><b>Offline answering</b><br />With EUSurvey, you can answer a form offline before submitting a contribution to the server at a later point in time.</p>
	<p><b>Automatic numbering</b><br />To structure your survey, EUSurvey can number your form's elements for you.</p>
	<p><b>Enhanced contrast</b><br />Visually impaired participants can choose a high-contrast version of the survey. This is created automatically for each form.</p>
	<p><b>Uploading supporting files</b><br />You can add files to your survey by uploading them. These files can be downloaded by every participant in your survey.</p>
	
	<h3><a class="anchor" name="_Toc0-5"></a>Form management</h3>
	<c:if test="${enablepublicsurveys}">
	<p><b>Publishing a survey</b><br />You can choose to publish your survey automatically on the <a href="https://ec.europa.eu${contextpath}/home/publicsurveys" target="_blank">list of public surveys</a> available via the European Commission's EUSurvey application, to give it even more exposure.</p>
	</c:if>
	<p><b>Working together</b><br />For surveys that are managed by multiple users, EUSurvey lets you define advanced permissions for other users to test a survey or to analyse the results.</p>
	
	<h3><a class="anchor" name="_Toc0-6"></a>Result management</h3>
	<p><b>Analysing your results</b><br />EUSurvey offers basic result analysis capabilities and visualization of data in histograms and chart views. You can also export survey results to standard spreadsheet formats in order to import them into statistical applications.</p>
	<p><b>Publishing your results</b><br />Use the possibilities EUSurvey offers to publish a sub-set of all submitted answers on the internal pages of the application. Statistics and charts can be calculated and created automatically by the system.</p>
	<p><b>Editing submitted contributions</b><br />Let your participants modify a contribution after it has been submitted, if they want to.</p>
	
	<h2><a class="anchor" name="_Toc0-7"></a>Where do I find additional information about EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Please find practical information behind the "<a href="https://ec.europa.eu${contextpath}/home/documentation" target="_blank">Documentation</a>" link in the banner of the EUSurvey application. Consult the "<a href="https://ec.europa.eu${contextpath}/home/about" target="_blank">About</a>" page for additional information on the application's background and funding.</p>
	<h2><a class="anchor" name="_Toc0-8"></a>Whom do I contact if there are technical problems with EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>EU staff should contact their IT helpdesk and ask them to forward the problem to DIGIT-EUSURVEY-SUPPORT, describing it as precisely as possible.</p>
	<p>External users should contact the Commission's <a href="mailto:EC-CENTRAL-HELPDESK@ec.europa.eu?subject=Incident%20Creation%20Request%20for%20DIGIT%20EUSURVEY%20SUPPORT%20&amp;body=%20Dear%20Helpdesk,%0D%0DCould%20you%20please%20open%20a%20ticket%20to%20DIGIT%20EUSURVEY%20SUPPORT%20with%20the%20following%20description:" target="_blank">CENTRAL HELPDESK</a>.</p>
	<h2><a class="anchor" name="_Toc0-9"></a>Whom do I contact if I have new ideas for improving EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>We always welcome your comments and feedback! Please ask your IT helpdesk or the CENTRAL HELPDESK to forward your suggestions to the EUSurvey support team. The support team will get back to you as soon as possible to discuss the relevant use cases and whether your idea can be incorporated in a future version of the application.</p>
	<h2><a class="anchor" name="_Toc0-10"></a>Which browsers are supported by EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>The last two versions of Internet Explorer, Mozilla Firefox and Google Chrome are supported by EUSurvey.</p>
	<p>Using other browsers might cause compatibility problems.</p>
	<h2><a class="anchor" name="_Toc0-11"></a>EUSurvey disclaimer (for non-EU users only)<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>For all questionnaires and invitation mails coming from a survey that has been created by a user <b>not working officially for the EU institutions</b>, the following disclaimer will be displayed in the survey and email message:</p>
	<p>Disclaimer<br> 
	<i>The European Commission is not responsible for the content of questionnaires created using the EUSurvey service - it remains the sole responsibility of the form creator and manager. The use of EUSurvey service does not imply a recommendation or endorsement, by the European Commission, of the views expressed within them.</i></p>
	<h2><a class="anchor" name="_Toc0-12"></a>Can my participants answer my survey from a mobile device?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Yes, EUSurvey features a responsive design for the published survey. This means, the design of the questionnaire will adapt to the resolution of the device used to enter your survey. This will allow your participants to conveniently contribute to your survey from a mobile device (mobile phone or tablet PC).</p>
	
	<h2><a class="anchor" name="_Toc0-13"></a>Is there a minimum screen size?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>The online questionnaires are fully responsive and adapt to the size of your device, allowing to fill in the questionnaire with any screen size.
	<br />For creating and managing surveys, we recommend to use a minimum resolution of 1680x1050 pixels for a good user experience.</p>
	
	<h1><a class="anchor" name="_Toc1"></a>Login and EU Login registration</h1>
	<h2><a class="anchor" name="_Toc1-1"></a>I have an EU Login account. Do I need to register separately for EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>No, there is no need to register separately to EUSurvey. An EU Login account is enough. You can access to EUSurvey by clicking on the login button on the <a href="https://ec.europa.eu/eusurvey/home/welcome" target="_blank">EUSurvey homepage</a>. This takes you to the login screen (see below for more details).</p>
	
	<h2><a class="anchor" name="_Toc1-2"></a>How do I connect to EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Please click on the login button on the <a href="https://ec.europa.eu/eusurvey/home/welcome" target="_blank">EUSurvey homepage</a>. You will be redirected to the EUSurvey login screen.</p>
	<p>Once you arrived on the login screen, you have to choose the option corresponding to your personal case:</p>
	<p>
		<ul>
			<li><b>If you work for an EU institution</b>, choose the second option to connect to the EUSurvey application. Your EU Login username and password will then be sufficient.</li>
			<li><b>If you don't work for an EU institution</b>, choose the first option to connect to the EUSurvey application. You will need to have previously registered your mobile phone to pass the <a href="https://en.wikipedia.org/wiki/Help:Two-factor_authentication">two-factor authentication</a>.</li>
		</ul>
	</p>
	<p>If you don't have an EU Login account, please create one by clicking <a href="https://webgate.ec.europa.eu/cas/eim/external/register.cgi">here</a>.</p>
	<p>In case you don't work for an EU institution, please also register your mobile phone by clicking <a href="https://ecas.ec.europa.eu/cas/userdata/mobileApp/manageMyMobileDevices.cgi">here</a>.</p>
	
	<h1><a class="anchor" name="_Toc2"></a>Creating a survey</h1>
	<h2><a class="anchor" name="_Toc2-1"></a>How do I create a new survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p> On the "Welcome" page or the "Surveys" page, click on "Create new Survey NOW!" and a dialogue box will open. Once you have entered all mandatory information, click on "Create". The application will load your new survey into the system and open the "Editor" automatically, so you can start adding elements to your survey right away.</p>
	<h2><a class="anchor" name="_Toc2-2"></a>How do I import an existing survey from my computer?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>On the "Welcome" page or the "Surveys" page, click on "Import Survey" and a dialogue box will open. Once you have selected a survey file from your computer, click on "Import" and your survey will be added to EUSurvey. Note: you can only import surveys as zip files or with the file extension .eus.</p>
	<h2><a class="anchor" name="_Toc2-3"></a>How do I import an existing survey from IPM?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p> First export your survey from IPM. Therefore, log in to IPM and open your questionnaire. On the left hand side of the page click on "Export" and the questionnaire will be saved on your computer as a zip file.</p>
	<p>Log in to EUSurvey. On the "Welcome" page click on "Import Survey". Select the survey you want to import (the whole zip file, usually in the folder "Downloads" if you downloaded it from IPM just before). If the import was successful you can open and use the survey in EUSurvey.</p>
	<h2><a class="anchor" name="_Toc2-4"></a>Where do I find all the surveys I have created?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Go to the "Surveys" page for a list. You can search for surveys by keywords or you can search, filter and sort by other criteria such as Creation Date, Language, Status etc. Don't forget to click on "Search" to apply the search criteria.</p>
	<h2><a class="anchor" name="_Toc2-5"></a>How do I open an existing survey for editing, etc.?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>From the "Surveys" page, click on the "Open" icon of the survey you want and the "Overview" page will open with several new tabs. From here you can go to the "Editor", test your survey, or access the survey's "Results", "Translations", "Properties" etc.</p>
	<h2><a class="anchor" name="_Toc2-6"></a>How do I export an existing survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>On the "Surveys" page, search for the survey you want to export. You can either:</p>
	<p>click on the "Export" icon, OR</p>
	<p>click on the "Open" icon, and from the "Overview" page, click on the "Export" icon.</p>
	<p>Your survey, together with all your settings, will be saved to your computer. The file extension of an EUSurvey form file is ".eus".</p>
	<h2><a class="anchor" name="_Toc2-7"></a>How do I copy an existing survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>On the "Surveys" page, open the survey you want and click on the "Copy" icon. In the dialogue box that opens you can modify the necessary settings, and then click on "Create". Your survey will be added to the list on the "Surveys" page. You can start editing right away.</p>
	<h2><a class="anchor" name="_Toc2-8"></a>How do I remove an existing survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>On the "Surveys" page, open the survey you want and click on the "Delete" icon. Once you have confirmed, your survey will be removed from the list of surveys. Beware: deleting a survey removes every trace of your questions and your results from the EUSurvey system! This cannot be undone!</p>
	<h2><a class="anchor" name="_Toc2-9"></a>How do I create WCAG compliant questionnaires with EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>The Web Content Accessibility Guidelines (WCAG) consist of a set of guidelines for making content accessible, primarily for people with disabilities, but also for software such as mobile phones.</p>
	<p>If you wish to make your survey WCAG compliant, please follow the instructions explained <a href="https://circabc.europa.eu/d/a/workspace/SpacesStore/78b03213-5cf4-4aab-8e90-ada7e2eb1101/WCAG_tutorial%20.pdf" target="_blank">in this document</a>.</p>
	<h2><a class="anchor" name="_Toc2-10"></a>How do I create a quiz survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>When creating a new questionnaire, you can choose between a normal survey and a quiz survey.</p>
	<p>A quiz is a special type of survey that allows calculating a final score for each participant. Such surveys can be used e.g. as skill tests or electronic exams. You will find detailed information about the creation of a quiz survey in the <a href="https://circabc.europa.eu/sd/a/400e1268-1329-413b-b873-b42e41369a07/EUSurvey_Quiz_Guide.pdf" target="_blank">EUSurvey Quiz Guide</a>.</p>
	<p>The quiz mode includes amongst others:</p>
		<ul>
			<li>A scoring feature</li>
			<li>The verification of participants' answers</li>
			<li>The possibility to provide feedback to your participants, depending on their answers</li>
			<li>Additional result analysis that has been specifically designed for quizzes</li>
		</ul>
	
	<h1><a class="anchor" name="_Toc3"></a>Editing a survey</h1>
	<h2><a class="anchor" name="_Toc3-1"></a>How do I start the Editor?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>First make sure that you have opened an existing survey: Go to the "Surveys" page and click the "Open" icon of the survey you want to edit. From the "Overview" page, click "Editor" to open it and to start with the editing of your questionnarie.</p>
	<p>Please make sure to save your work from time to time.</p>
	<h2><a class="anchor" name="_Toc3-2"></a>How do I create a questionnaire by using the EUSurvey editor?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>The editor is used to create a questionnaire. You can use it to add questions and other elements to your questionnaire.</p>
	<p>You will find detailed information about the functionalities of the Editor in the <a href="https://ec.europa.eu/eusurvey/resources/documents/Editor_Guide.pdf" target="_blank">EUSurvey Editor Guide</a>.</p>
	<p>The editor consists of five different areas:</p>
	<p><b>Navigation pane:</b><br>The Navigation pane provides a structured view of the questionnaire. All elements are represented by their respective text label in the questionnaire. When you select an element in the Navigation pane, the Form area jumps to the selected item which is then highlighted in blue.</p>
	<p><b>Toolbox pane:</b><br>The Toolbox pane contains all the element types that you can add to your questionnaire. You can add elements either by using the drag-and-drop feature or by double-clicking them.</p>
	<p><b>Form area:</b><br>Provides a preview of the questionnaire; elements can be added to it and selected for editing.</p>
	<p><b>Element properties pane:</b><br>It displays the settings for selected elements. You can edit the elements here, e.g. by changing the question text, adding help messages and changing all relevant settings to adapt the question to your needs.</p>
	<p><b>Toolbar:</b><br>It includes all available basic tasks that you can perform when creating the questionnaire.</p>
	<h2><a class="anchor" name="_Toc3-3"></a>How do I add or remove questions to my questionnaire?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>To add new elements to your form or remove existing ones, first open the editor.</p>
	<p>In the editor you will find a Toolbox of available elements on the left and the Form area in the middle of the screen. The elements contain default texts, with the name of the element displayed as the question text. To add a new element (question, text field, image, etc.), select one from the Toolbox. You can add elements either by using the drag-and-drop feature or by double-clicking them.</p> 
	<p>To remove an element from the form, you can click the element to select it. Click on "Delete"; as soon as you have confirmed, the element will be removed from the questionnaire.</p>
	<p>See also <a href="#_Toc3-2">"How to create a questionnaire by using the EUSurvey editor?"</a></p> 
	<h2><a class="anchor" name="_Toc3-4"></a>How do I edit elements in my questionnaire?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>The elements in your questionnaire will be <b>selected for editing in the Form area</b> and <b>edited in the Element properties pane</b> of the Editor. See <a href="#_Toc3-2">"How to create a questionnaire by using the EUSurvey editor?"</a></p>
	<p>You can click an element in the Form area to select it. The selected element appears in blue, with the respective options visible in the Element properties pane. You can edit the elements there, e.g. changing the question text, adding help messages and changing all relevant settings to adapt the question to your needs.</p>
	<p>To edit a text:</p>
		<ol>
			<li>Click on the text or the pen icon.</li>
			<li>Modify the text.</li>
			<li>Click "Apply" to see the changes in the Form area.</li>
		</ol>
	<p>By default, the Element properties pane displays all the basic options. To display more options, click "Advanced".</p>
	<p>For matrix and text questions, you can also choose the individual questions/answers/rows/columns of the element by clicking the respective label text as indicated below. This way, you can e.g. select individual questions of a matrix or table element and make them mandatory.</p>
	<h2><a class="anchor" name="_Toc3-10"></a>How do I copy elements?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>To copy elements in your form, first open the Editor.</p>
	<p>Any elements that have been copied or cut are depicted by a placeholder at the top of the Toolbox pane. You can add them to the questionnaire again using the drag-and-drop feature. You can also use the button next to the element to cancel this operation.</p>
		<ol>
			<li>Select the element(s).</li>
			<li>Click "Copy".</li>
			<li>Move the placeholder from the Toolbox to the Form area as described above or select the element in the Form area and click "Paste after".</li>
		</ol>
	<p>See also <a href="#_Toc3-2">"How do I create a questionnaire by using the EUSurvey editor?"</a></p>
	<h2><a class="anchor" name="_Toc3-11"></a>How do I add or remove possible answers in choice questions?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>You can add or remove possible answers by clicking the plus/minus button in the Element properties pane. Edit the text of the existing answer options by clicking the pen icon next to "Possible answers". You can then edit them in the rich text editor.</p>
	<p>See also <a href="#_Toc3-2">"How do I create a questionnaire by using the EUSurvey editor?"</a></p>
	<h2><a class="anchor" name="_Toc3-12"></a>Can I make a question mandatory?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>In the editor, select the check box in the Element properties pane after selecting the respective element.</p>
	<p>Mandatory questions will be marked with a red asterisk to the left of the question text.</p>
	<h2><a class="anchor" name="_Toc3-13"></a>How do I move elements within the questionnaire?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>In the editor, you can change the position of an element in your questionnaire by using one of the following options:</p>
	<p>Drag-and-drop:<br>Select the element in the Form area and drag it to where you want in the questionnaire.</p>
	<p>Move buttons:<br>Select the element you want to move and use the move up/move down buttons in the Toolbar on top of the Form Area to move it up or down.</p>
	<p>Cut-and-paste:<br>Cut the element you want to move and use the drag-and-drop feature to move the placeholder to where you want to put it.</p>
	<h2><a class="anchor" name="_Toc3-14"></a>How do I use the visibility feature (dependencies)<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Using this feature in the editor, you can display and hide elements depending on answers given by participants to either single/multiple choice or matrix questions (see also <a href="#_Toc3-2">"How to create a questionnaire by using the EUSurvey editor?")</a></p>
	<p>By default, all elements are set to always visible, which means that everybody will see the question when answering the survey.</p>
	<p>To create a dependent question:</p>
		<ol>
			<li>Add a single/multiple choice or matrix question to your questionnaire.</li>
			<li>Add further elements to your questionnaire.</li>
			<li>Select an element that follows a single/multiple choice or matrix element and that should only appear when a specific answer has been chosen.</li>
			<li>Click the pen icon to edit the visibility settings. All available questions of the type single choice, multiple choice and matrix that are placed above the selected element(s) are displayed, including the question text and the possible answers.</li>
			<li>Select the answer that, when chosen, will display the selected element.</li>
			<li>Click "Apply" to confirm the visibility setting.</li>
		</ol>
	<p>If multiple elements are selected, you can edit the visibility settings for all of them at once.</p>
	<p><b>Note:</b> This modification will only affect the questionnaire on the Test page and in published mode. All elements will still be visible in the editor.</p>
	<p>When activated, arrows are displayed next to the connected elements to visualise the visibility settings in the Form area. Answers that trigger any element are displayed with an arrow pointing down. Elements that are triggered by any answer are marked with an arrow pointing up.</p>
	<p>When moving the pointer over the arrows or IDs in the Element properties pane, the connected elements are highlighted in the Form area and Navigation pane.</p>
	<p>Elements with visibility settings that have been edited will be hidden in the questionnaire until at least one of the configured answers has been selected by the participant when filling in the questionnaire.</p>
	<h2><a class="anchor" name="_Toc3-7"></a>Can I generate the order of answers in a single or multiple choice question?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>When creating a single or multiple choice question, you will have the option to generate their answers in three different ways:</p>
		<ul>
			<li>Original Order</li>
			<li>Alphabetical Order</li>
			<li>Random Order</li>
		</ul>
	<p>Original Order: This option will display the original order of your answers on the survey.</p>
	<p>Alphabetical Order: You can select this option if the answers should be displayed alphabetically on the survey.</p>
	<p>Random Order: You can choose this option if the answers should be randomly displayed on the survey.</p>
	<h2><a class="anchor" name="_Toc3-5"></a>How do I give other users permission to edit my survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p> Open your survey and open the "Privileges" page. Click on "Add new user" or "Add department". A wizard will pop up that guides you through the process of adding users. Next you can give them specific access rights. Simply click on the colour to change the rights.</p>
		<ul>
			<li>Green: Read and write access</li>
			<li>Yellow: Read access</li>
			<li>Red: No access</li>
		</ul>
	<p>Users you add will automatically see your survey in their list of surveys next time they log into EUSurvey. Read more about this under "<a href="#_Toc8-8">How do I give other users access to my survey?</a>"</p>
	<h2><a class="anchor" name="_Toc3-8"></a>Which languages are supported by the application?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>The languages which can be encoded in '3byte UTF-8' can be used to create a survey.</p>
	<h2><a class="anchor" name="_Toc3-9"></a>So why UTF-8 and which fonts should be used? <a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Keep in mind that the target group of participants can display the survey easily if the font you used is installed in their browser. UTF-8 is the most common encoding for the HTML pages.<br>On the other hand, choosing not-supported fonts may affect the rendering of PDF export.</p>
	<p>We recommend using the <b>supported character sets</b> listed below:</p>
		<ul>
			<li>Freesans <a href="https://circabc.europa.eu/sd/a/36f72861-fc6e-4fe1-87d6-0a8e1c6fa161/EUSurvey-SupportedCharacterSet(freesans).txt" target="_blank">(https://circabc.europa.eu/sd/a/36f72861-fc6e-4fe1-87d6-0a8e1c6fa161/EUSurvey-SupportedCharacterSet(freesans).txt)</a></li>
			<li>Freemono <a href="https://circabc.europa.eu/sd/a/55ce0f35-b3cc-4712-80bf-af42800a278f/EUSurvey-SupportedCharacterSet(freemono).txt" target="_blank">(https://circabc.europa.eu/sd/a/55ce0f35-b3cc-4712-80bf-af42800a278f/EUSurvey-SupportedCharacterSet(freemono).txt)</a></li>
			<li>Freeserif <a href="https://circabc.europa.eu/sd/a/29cd78bb-9eeb-40b1-a22f-b54700750537/EUSurvey-SupportedCharacterSet(freeserif).txt" target="_blank">(https://circabc.europa.eu/sd/a/29cd78bb-9eeb-40b1-a22f-b54700750537/EUSurvey-SupportedCharacterSet(freeserif).txt)</a></li>
			<li>Commonly supported character-set <a href="https://circabc.europa.eu/sd/a/1eb30efd-e2d8-4c3b-9f55-533bb903f7d0/EUSurvey-SupportedCharacterSet(common).txt" target="_blank">(https://circabc.europa.eu/sd/a/1eb30efd-e2d8-4c3b-9f55-533bb903f7d0/EUSurvey-SupportedCharacterSet(common).txt)</a></li>
		</ul>
	<p><b>"Freesans" is the default font used</b></p>
	<p>In case of doubt, run a PDF export of your final survey to check if your survey is rendered correctly in PDF. Beware however that some contributions may not to be rendered correctly in PDF. Your participants, indeed, are free to choose any font among those which are supported by the application. Even though the application is unable not render the characters they have used, these are saved correctly on the EUSurvey's database. Thus they can be exported from the results page.</p>
	<h2><a class="anchor" name="_Toc3-6"></a>What does "Complexity" mean?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Adding too many elements or dependencies to your survey can lead to performance issues for the participants who want to fill out your questionnaire, because it is too "complex".</p>
	<p>That your survey has a high complexity can have several reasons:</p>
		<ul>
			<li>You use too many table/matrix elements</li>
			<li>You use too many dependencies</li>
			<li>You use too many cascading dependencies</li>
		</ul>
	<p>For more information, see our <a href="https://circabc.europa.eu/sd/d/281e626e-279e-45df-8581-1f6e04feff51/BestPractices-EUSurvey.pdf" target="_blank">best practices</a>.</p>
	
	<h1><a class="anchor" name="_Toc4"></a>Survey security</h1>
	<h2><a class="anchor" name="_Toc4-1"></a>How do I restrict access to my survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>By default, an EUSurvey online form is publicly available as soon as it has been published. You can change this and allow only privileged users to access the survey by setting the survey to <b>"Secured"</b> in the "Security settings" on the "Properties" page. You can then allow access to your privileged users by either:</p>
		<ul>
			<li>Inviting your participants with the EUSurvey invitation module (see "<a href="#_Toc12">Inviting participants</a>). Each participant will get a unique access link, OR</li>
			<li>Securing your survey by EU Login. In the "Properties" page, edit "Security" and select "Enable EU Login". If you are a member of an EU body, you can choose either to allow all users with an EU Login account (members of the EU institutions and external EU Login accounts) to access your survey, or to grant access only to members of the EU institutions. OR</li>
			<li>Setting up a password. This will be the same for all participants and you will have to communicate it to your audience. Practically, you will send the survey location link and the global password (see "<a href="#_Toc4-3">How do I set a password for my survey?"</a>).</li>
		</ul>
	<h2><a class="anchor" name="_Toc4-3"></a>How do I set a password for my survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>To protect your survey with a password, edit the "Security Settings" under "Properties". To invite individual contacts to access your secured survey, please read the Help section "<a href="#_Toc12">Inviting participants</a>"</p>
	<h2><a class="anchor" name="_Toc4-4"></a>How do I ensure that a user does not submit more than a defined number of contributions to my survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>As soon as you send out individual access links to your participants, the system will be able to identify any one of them individually.</p>
	<h2><a class="anchor" name="_Toc4-5"></a>What do I do to prevent robots from submitting multiple contributions to my survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Automatic scripts can falsify the outcome of an online survey by submitting a high number of contributions. To prevent this, you can use EUSurvey to make participants solve a  <a href="http://fr.wikipedia.org/wiki/CAPTCHA" target="_blank">CAPTCHA</a> challenge before submitting a contribution.</p>
	<p>You can enable and disable the CAPTCHA in the "Security Settings" under "Properties".</p>
	<p>N.B. Although this won't make fraud impossible, it might discourage people from continuing to try to falsify survey results.</p>
	<h2><a class="anchor" name="_Toc4-6"></a>Can I enable my participants to access their contributions after submission?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Yes! Go to "Security Settings" under "Properties". Participants need to know the contribution-ID that was shown after they submitted their contribution. To change contributions after submission, participants should go to the EUSurvey homepage <a href="https://ec.europa.eu/eusurvey" target="_blank">https://ec.europa.eu/eusurvey</a>. Below the "Register Now!" button is a link to the <a href="${contextpath}/home/editcontribution" target="_blank">access page for individual contributions</a>. On this page participants must fill in their individual contribution-ID and the system will open their contribution. This way they can edit their contribution after it has been submitted.</p>
	
	<h1><a class="anchor" name="_Toc5"></a>Testing a survey</h1>
	<h2><a class="anchor" name="_Toc5-1"></a>Can I see how my survey will behave after it has been published?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Yes. Please open the survey in EUSurvey and click on "Test". You will see the current draft of your survey, and you can access every feature of the published form. You can save the test as a draft or directly submit it as your contribution.</p>
	<h2><a class="anchor" name="_Toc5-2"></a>How can my colleagues test my survey before it is published?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>The "Test"-page of your survey can also be tested by your colleagues. To give access to this page, open your survey in EUSurvey, go to the "Privileges" tab and click on "Add User" or "Add Department". A wizard will guide you through the process of adding your colleagues. To give them appropriate access rights for testing, change the colour of "Access Form Preview" to "Green". Simply click on the colour to change the rights.</p>
	<p>The added users will automatically see the survey in their "Surveys" page once they log into the EUSurvey application. Read more about this under "<a href="#_Toc8-8">How do I give other users access to my survey?</a>"</p>
	
	<h1><a class="anchor" name="_Toc6"></a>Translations</h1>
	<h2><a class="anchor" name="_Toc6-1"></a>How do I translate a survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>EUSurvey offers various ways  to make the survey available in multiple languages. Important: Finish editing and testing your survey before starting on the translations!</p>
	<p>Open your survey and go to the "Translations" page. Click on "Add New Translation" Select the language from the list of supported languages. If the required language does not appear in the list, select "other" and specify a valid two-letter ISO 639-1 language code. Click on "OK" to add the empty translation form to your survey. Please read "<a href="#_Toc6-3">Can I edit an existing translation online?</a>" for more information about how to add new labels to your newly created translation.</p>
	<p>Don't forget to select the box "To Publish" if the translation is to be published along with your survey. Once you add a translation for publishing, participants can choose from the available languages directly from the survey link.</p>
	<h2><a class="anchor" name="_Toc6-2"></a>How can I upload an existing translation to my survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Open your survey and open the "Translations" page. Click on "Upload existing translation". A wizard will guide you through the process of uploading the translation.</p>
	<h2><a class="anchor" name="_Toc6-3"></a>Can I edit an existing translation online?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Yes! Open your survey, go to the "Translations" page and select one or more translations to edit. Select "Edit Translations" from the action menu just below the list of available translations and click on the "Go" button. This will open the online translation editor that will allow you to edit multiple translations at a time. Please click the "Save" button to make sure that your changes are written to the system.</p>
	<h2><a class="anchor" name="_Toc6-4"></a>Can I create my translations offline?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Yes! Open your survey, go to the "Translations" page and export your survey as an XLS, ODS or XML file to perform the translation offline. The translation can then be imported back into your survey.</p>
	<p>The usual workflow is to export a language version with status "Complete" and then translate all available text labels into the new language. Ensure that the new language code is specified at the beginning of the form to ensure that the system recognizes the language of your translation. Once the survey has been translated offline, click "Upload existing translation" to add it to the system. To ensure that no translation is overwritten by accident, you will have to specify the language version you are about to upload. For security reasons you can select individual labels to replace if you do not want all labels to be taken into account.</p>
	<h2><a class="anchor" name="_Toc6-6"></a>How do I publish/unpublish my translations? Why can't I publish this translation? What is an "Incomplete" translation?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>To publish a survey in multiple languages, open your survey, go to the "Translations" page and tick/untick the individual translations you want to publish/unpublish under "To Publish". Then change to the "Overview" page of your survey to publish your survey. If the survey had been published before the translations were added/removed, click on "Apply Changes".</p>
	<p>To ensure that no translations with missing text are published, you cannot publish translations that have empty labels (translations that are not "Complete"). Please make sure your translation has no empty labels by using the online translation editor. Look for cells with a red background.</p>
	<h2><a class="anchor" name="_Toc6-7"></a>Can I upload a translation in a non-European language?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>The application also supports non-European languages. Please select "Other" in the uploading process and introduce a valid two-letter ISO 639-1 language code.</p>
	<h2><a class="anchor" name="_Toc6-8"></a>What does "Request Machine Translation" mean?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>EUSurvey can provide automatic translations of your questionnaire by using a <b>Machine Translation (MT)</b>. The application uses the MT@EC service, provided by the European Commission.</p>
	<p>From the "Translations" page, there are several ways to request machine translations:</p>
		<ul>
			<li>When adding a new translation, tick the checkbox "Request Machine Translation" (for a translation of your survey's pivot language)</li>
			<li>Click the "Request Translations" button in the "Action" column (for a translation of your survey's pivot language)</li>
			<li>Select all languages you want to be translated (including at least one complete translation). Then select ""Request Translations" from the select box below your translations and click "Ok".</li>
		</ul>
	<p>The status of the translations will change to "Requested", until they are finished. To see when the status has changed, check the "Translations" page.</p>
	<p>Machine translations will behave like other translations you added manually, i.e. they will not be published automatically, and adding new elements to your survey will make them incomplete (to complete them, you'll have to request a new translation).</p>
	<p><i>We cannot guarantee the quality of the resulting text or the delivery time for translations.</i></p>
	<p><a href="https://webgate.ec.europa.eu/etranslation/help.html" target="_blank">Machine translation - Help</a> (EU staff only).</p>
	<h2><a class="anchor" name="_Toc6-5"></a>Instructions for EU staff<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>You are encouraged to contact DGT before you finalise your survey. Its Editing unit (email: DGT-EDIT) will check that your survey is clearly drafted and presented. For more info, see <a href="https://myintracomm.ec.europa.eu/serv/en/dgt/Pages/index.aspx" target="_blank">DGT's MyIntraComm pages</a>.</p>
	<p>You can also get your survey translated into the official EU languages by DGT. First export it as an XML file and send it via Poetry with the requester code of the DG concerned. It should be a maximum of 15 000 characters, excl. spaces (Word count).</p>
	
	<h1><a class="anchor" name="_Toc7"></a>Publishing a survey</h1>
	<h2><a class="anchor" name="_Toc7-1"></a>How do I publish my survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>To publish a survey from a current working draft, please go to the "Overview" page and click on "Publish". After confirmation, the system will automatically create a working copy of your survey and put it online, along with the set of translations you have selected for publication on the "Translations" page (See "<a href="#_Toc6-6">How do I publish/unpublish my translations?</a>"). You will find the link to your published survey in "Survey Location" on the "Overview" page.</p>
	<p>To unpublish your survey, just click the "Unpublish" button. The unpublished survey will stay available to you in the form in which it was published, along with your current working draft. This means that the unpublished survey does not have to be replaced by your current working draft, but can be republished if necessary.</p>
	<h2><a class="anchor" name="_Toc7-2"></a>Can I customize the URL to my survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Yes, by changing your survey's "Alias" you can make the URL to your survey more meaningful. Open the survey and go to the "Properties" page. Click on the "Edit" button under the "Basic Settings" item and change your survey's alias. An alias can only contain alphanumeric characters and hyphens. If you change the alias of a published survey, go to the "Overview" page and click on "Apply Changes".</p>
	<p>Please note that aliases have to be unique in EUSurvey. You will get a warning if your alias has already been taken by another survey.</p>
	<h2><a class="anchor" name="_Toc7-7"></a>Can I link directly to a translation of my survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>When you send out invitations, or use the link to the published form on the "Overview" page, by default the link points to the form in the pivot language.</p>
	<p>But you can also <b>redirect respondents directly</b> to the right translation by using this link:<br /><b>https://ec.europa.eu${contextpath}/runner/<span style="color:red">SurveyAlias</span>?surveylanguage=<span style="color:red">LC</span></b></p>
	<p>Just replace:</p>
		<ul>
			<li><b><span style="color:red">SurveyAlias</span></b> with the <b>alias of your survey</b></li>
			<li><b><span style="color:red">LC</span></b> with the appropriate <b>language code</b> (e.g. FR for French, DE for German, etc.)</li>
		</ul>
	<h2><a class="anchor" name="_Toc7-3"></a>How do I make my survey publish itself while I am on holiday?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>You can schedule your survey to be published automatically at any point in time. Open the survey and go to the "Properties" page. Click on the "Edit" button under "Advanced Settings" and specify the start- and expiry-date of your survey.</p>
	<h2><a class="anchor" name="_Toc7-4"></a>Can I receive a reminder of when my survey will end?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>EUSurvey can send you an email reminder when your survey is about to end. This way you can prepare the next steps (e.g. organizing resources for result analysis).</p>
	<p>To enable this option, open the survey and go to the "Properties" page. Select "Advanced Settings", click on the "Edit" button and enable "End Notification", specifying how long in advance you expect an email and whether all other Form Managers should get an email too. Click on "Save".</p>
	<c:if test="${enablepublicsurveys}">
	<h2><a class="anchor" name="_Toc7-5"></a>How do I advertise my survey on the list of public surveys on the EUSurvey website?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>You can publish a link to your survey on EUSurvey's <a href="https://ec.europa.eu${contextpath}/home/publicsurveys" target="_blank">"list of all public surveys"</a>.</p>
	<p>Open your survey and go to the "Properties" page. Select "Security Settings" and click on the "Edit" Button. Under "Public" select "Yes", then "Save".</p>
	<p><b>Please note</b> that a publication of your survey on EUSurvey's list of public surveys requires the validation of the EUSurvey administrative team. When you click on "Publish" in the Overview page, or if pending changes are applied, the system will automatically send an email to EUSurvey's administrative team.</p>
	<p>As soon as the right to publish has been granted, you will receive a confirmation message and your survey will be available in the list of public surveys.</p>
	<p>For all surveys published on EUSurvey's public list of surveys, respondents have to solve a CAPTCHA challenge before submitting a contribution. This CAPTCHA is set automatically.</p>  
	</c:if>
	<h2><a class="anchor" name="_Toc7-6"></a>For EU staff: What are the official requirements for launching an open public consultation ("Your Voice in Europe" website)?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Please carefully follow <a href="https://circabc.europa.eu/sd/d/fc02d2ac-d94f-42ed-b866-b3429e0d717b/Survey_publication_your_voice_in_europe_NEW.pdf" target="_blank">the procedure</a> from SG for launching an open public consultation on the <a href="http://ec.europa.eu/yourvoice/consultations/index_en.htm" target="_blank">Your Voice in Europe</a> website.</p>
	
	<h1><a class="anchor" name="_Toc8"></a>Managing your survey</h1>
	<h2><a class="anchor" name="_Toc8-1"></a>If I discover an error in my survey, can I correct it?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Yes, you can edit and change the survey as often as you want, and add or change any additional (dependent) question. However, please note that the more changes you make the less usable the collected data will be, given that different participants in your survey might have responded to different surveys. So, to make sure that you can still compare the full set of answers, it is recommended that you avoid changing the structure of your survey at all. Please note that you are fully responsible for every change you apply to your survey during its lifetime.</p>
	<p>If you do want to change an already published survey, please remember to click on "Apply Changes" on the survey's "Overview" page to make sure the changes are visible in the published survey.</p>
	<p>If you want to remove answers from your survey, please read: "Will I lose any submitted answers when I change my form?"</p>
	<h2><a class="anchor" name="_Toc8-2"></a>Will I lose any answers submitted when I change my form?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>No answers are lost, unless you delete your survey from the system. However, you might not be able to visualize the full set of collected data if you have removed individual questions from your survey during the active period of your survey. This is because the search mask always represents the most recently published form only. Please read "How do I show the full set of saved questions?" to visualize all answers, even those to questions that were removed during the active period of your survey.</p>
	<h2><a class="anchor" name="_Toc8-3"></a>How can I change the title of my survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Open the survey and go to the "Properties" page. Click on the "Edit" button from the "Basic Settings" and change your survey's title. If you have already published your survey, please remember to go to the "Overview" page and click on "Apply Changes".</p>
	<h2><a class="anchor" name="_Toc8-4"></a>How can I change my survey's contact address?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Open the survey and go to the "Properties" page. Click on the "Edit" button from the "Basic Settings" and change your survey's contact address. If you have already published your survey, please remember to go to the "Overview" page and click on "Apply Changes".</p>
	<h2><a class="anchor" name="_Toc8-5"></a>How do I adjust the default confirmation message? <a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>The confirmation message is what participants see when they have submitted their contribution. To change the default message, open the survey, go to the « Properties » tab, go to « Special Pages », and click on the « Edit » button. If you have already published your survey, please remember to apply your pending changes by clicking on « Show pending changes » on the « Overview » page, and then « Apply Changes ».</p>
	<h2><a class="anchor" name="_Toc8-6"></a>How do I adjust the default escape message?  <a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>The escape page contains the message that your participants see if your survey is not available. To change the default message, open the survey, go to the "Editor" and click on the "Edit Escape Page" button. If you have already published your survey, please remember to go to the "Overview" page and click on "Apply Changes".</p>
	<h2><a class="anchor" name="_Toc8-7"></a>Archiving feature<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>You can archive your survey with all its submitted answers to reload it or to launch the same survey again at a later point in time. To archive your survey, select the "Archive Survey" icon in the action menu of the "Overview" page.</p>
	<p>Archived surveys cannot be edited or collect any data. But you can export results or request a PDF-file of your survey. The archived survey will be available from the "Dashboard" page, from where it can also be restored. A restored survey can be edited again.</p>
	<h2><a class="anchor" name="_Toc8-8"></a>How do I give other users access to my survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>In EUSurvey, you can grant access to other users for different purposes:</p>
		<ul>
			<li>Testing the survey ("Access Form Preview")</li>
			<li>Accessing the results ("Results")</li>
			<li>Editing the survey ("Form Management")</li>
		</ul>
	<p>In order to provide them access, open your survey and go to the "Privileges" page. You can grant access to a person or a department. The following access rights are possible:</p>
		<ul>
			<li>Green: Read and write access</li>
			<li>Yellow: Read access</li>
			<li>Red: No access</li>
		</ul>
	<p>In order to proceed, please click on "Add user" or "Add department" on the "Privileges" page. A window will pop up that guides you through the process of adding users.</p>
	<p>By clicking on "Add user", you will have to select the correct domain (i.e. European Commission), then enter the login, E-mail address or any other field and click on "Search". Then select the user and click on "Ok".</p>
	<p>By selecting "Add department", select the correct domain and navigate to the right department. Then click on "Ok".</p>
	<p>You will then be redirected to the "Privileges" page where you can set the right permissions by clicking on the red icons:</p>
		<ul>
			<li>To grant the right to test your survey:<br>
				Change the colour of "Access Form Preview" to "Green". Simply click on the colour to change the rights. The added users will automatically see the survey in their "Surveys" page once they log in to the EUSurvey application (see also "<a href="#_Toc5-2">How can my colleagues test my survey before it is published?</a>").</li>
			<li>To grant the right to access the results of your survey:<br>
				Change the colour of "Results" to "Yellow". The users can only view the results, but cannot edit or delete anything. If you change the colour to "Green", they can view, edit and delete the answers (see also "<a href="#_Toc9-7">How do I give other users access to the results of my survey?</a>").</li>
			<li>To grant the right to edit your survey:<br>
				By changing the colour to "Yellow", the privileged users can only view your survey. But if you select "Green", they can also edit it. They will automatically see your survey in their list of surveys (see also "<a href="#_Toc3-5">How do I give other users permission to edit my survey?</a>").</li>
		</ul>
	<p>If you set all 3 circles to "Green", the privileged user will have full rights permission to your survey.</p>
	
	<h2><a class="anchor" name="_Toc8-9"></a>What are Activity logs?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>
	Activity logs monitor and log the activity on your survey. This way, you can check which user applied which change to your survey and at what time. You can also export the activity logs into several file formats such as xls, csv and ods. Enter the activity log of your survey by clicking on the "Activity" link, next to "Properties". If the activity logs are empty, it may be that they are deactivated system-wide. Find <a href="${contextpath}/resources/documents/ActivityLogEvents.xlsx">here</a> a list of the logged events.
	</p>
	
	<h1><a class="anchor" name="_Toc9"></a>Analysing, exporting and publishing results</h1>
	<h2><a class="anchor" name="_Toc9-1"></a>Where can I find the contributions submitted by my participants?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Open your survey in EUSurvey (also see "<a href="#_Toc2-5">How do I open an existing survey for editing etc.?</a>") and go to the "Results" page. Initially, you'll see the full content of all submitted contributions in a table. You can view the results in 2 different ways:</p>
		<ul>
			<li>Full Content</li>
			<li>Statistics</li>
		</ul>
	<p>You can switch view modes by clicking the icons in the upper left corner of the screen.</p>
	<h2><a class="anchor" name="_Toc9-2"></a>How can I download submitted answers?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>To export submitted answers from EUSurvey to your computer, open your survey and go to the "Results" page. Various icons in the top right-hand corner of the page show the available export file formats. Clicking on an icon will open a dialogue box to specify a name. Under this name the export file will appear on the "Export" page. Different export file formats are available, depending on the view mode (Full Content/ Charts/Statistics). N.B. The export file will contain the set of configured questions only as well as the current search results of a filter process.</p>
	<h2><a class="anchor" name="_Toc9-3"></a>How can I find and analyse a defined subset of all contributions?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>On the "Results" page (see "<a href="#_Toc9-1">Where can I find the contributions submitted by my participants?</a>") search for keywords in free-text answers or select individual answers from choice-questions in the filter bar. This limits the full set of answers to a subset of contributions. At any point you can change the view mode, so you can carry out an advanced statistical analysis of the data collected. Note: to view and analyse results, you need certain privileges (see "<a href="#_Toc9-7">How do I give other users access to the results of my survey?</a>"). To export a subset of contributions, please read "How can I download submitted contributions?".</p>
	<h2><a class="anchor" name="_Toc9-4"></a>How can I get back to the full set of answers, after defining a subset of contributions?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>To visualize the full set of answers, click the "Reset" button at the top of the "Results" page or deactivate every search you've performed on the filter-bar on this page.</p>
	<h2><a class="anchor" name="_Toc9-5"></a>How can I publish my results?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Open the survey, go to the "Properties" page and select "Publish Results". Here you will find the URL of the published results, and when you click on "Edit" you can choose which questions/answers/contributions you would like to publish. You can also go there directly by clicking on "Edit Results Publication" from the "Overview" page of your survey.</p>
	<p>Please be sure to select something in "Publish Results" under "Publish", otherwise the system will not publish any results.</p>
	<h2><a class="anchor" name="_Toc9-6"></a>How can I access the published results?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Open the "Overview" page and click on the "Published" hyperlink right next to the word "Results". This will direct you to the published results. Everybody who knows this address can access your results.</p>
	<h2><a class="anchor" name="_Toc9-7"></a>How do I give other users access to the results of my survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Open your survey, go to the "Privileges" page and give results access to other users. Read more about this under "<a href="#_Toc8-8">How do I give other users access to my survey?</a>".</p>
	<h2><a class="anchor" name="_Toc9-8"></a>I cannot unzip my exported files<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>This might happen, if the name of the files contained in your folder is too long. Windows has a maximum length for directory locations on the hard drive of 260 characters. Possible solutions for this are:</p>
		<ul>
			<li>Unzip the folder in the root directory of your operating system, e.g.  unpack at "C:" instead of "C:\Users\USERNAME\Desktop"</li>
			<li>Or when unpacking the files, rename the target folder in order to shorten the directory length</li>
		</ul>
	<h2><a class="anchor" name="_Toc9-9"></a>Published results - protection of personal information uploaded by participants<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>For data protection rules, the form manager has to actively make the choice to publish files uploaded to a contribution by the participant along with the other results. To do so, tick the "Uploaded elements" check box in the corresponding section on the "Properties" page. Please note that this check box will only appear if your survey contains an "uploaded element".</p>
	<h2><a class="anchor" name="_Toc9-10"></a>How do I design a survey to publish the results with or without personal information?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>	
	<p>If you want to let your participants choose, whether their personal information will be published along with their answers, follow <a href="https://circabc.europa.eu/sd/d/e68ff760-226f-40e9-b7cb-d3dcdd04bfb1/How_to_publish_survey_results_anonymously.pdf" target="_blank">these instructions</a> to build the survey to fit these requirements.</p>	
	<h2><a class="anchor" name="_Toc9-11"></a>Why are my results not up-to-date?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>	
	<p>A new database has been introduced which shall improve EUSurvey's performance when querying your survey's results. However, this can lead to some delays until the latest data is displayed on the Results page of your survey. This delay should not be more than 12 hours.</p>
	<p>If the displayed data is older than 12 hours, please contact EUSurvey <a href="https://ec.europa.eu/eusurvey/home/support">support</a>.</p>
		
	<h1><a class="anchor" name="_Toc10"></a>Design and layout</h1>
	<h2><a class="anchor" name="_Toc10-1"></a>How do I change the general look and feel of my survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Open your survey, go to the "Properties" page and select "Appearance". Click on "Edit" and choose a new survey skin from the available skins. Click on "Save". If you have already published your survey, please remember to go to the "Overview" page and click on "Apply Changes".</p>
	<h2><a class="anchor" name="_Toc10-2"></a>How can I create my own survey themes?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>On EUSurvey's "Settings" page, at the top of your screen, select "Skins" and click on "Create a new Skin". This will open the skin editor for survey themes. You can copy an existing theme as a basis and use the online skin editor to modify this template as necessary.</p>
	<h2><a class="anchor" name="_Toc10-3"></a>How do I add a logo to my survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>To let your project/company logo appear in the top right-hand corner of your survey, upload an image file to the "Appearance" submenu on the "Properties" page. If you have already published your survey, please remember to go to the "Overview" page and click on "Apply Changes".</p>
	<h2><a class="anchor" name="_Toc10-4"></a>How do I add useful links to my survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Open your survey, go to the "Properties" page and select "Advanced Settings". Click on the "Edit" button to add labels and URLs under "Useful links". These links will appear on every page on the right-hand side of your survey. If you have already published your survey, please remember to go to the "Overview" page and click on "Apply Changes".</p>
	<h2><a class="anchor" name="_Toc10-5"></a>Where do I upload background documents for my survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Open your survey, go to the "Properties" page and select "Advanced Settings". Click on the "Edit" button to add a label and upload a file under "Background Documents". These documents will appear on every page on the right-hand side of your survey. If you have already published your survey, please remember to go to the "Overview" page and click on "Apply Changes".</p>
	<h2><a class="anchor" name="_Toc10-6"></a>How do I create a multi-page survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Top-level sections of your survey can be divided into individual pages automatically. Open your survey, go to the "Properties" page, select "Appearance" and click on "Edit". Enable "Multi-Paging" and click on "Save". If you have already published your survey, please remember to go to the "Overview" page and click on "Apply Changes".</p>
	<h2><a class="anchor" name="_Toc10-7"></a>How do I enable automatic numbering for my survey? <a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>To add auto-numbering to all sections and question elements of your form, open your survey, go to the "Properties" page, select "Appearance" and click on "Edit". Enable and select your preferences for "Automatic Numbering" and save. If you have already published your survey, please remember to go to the "Overview" page and click on "Apply Changes".</p>
	<h2><a class="anchor" name="_Toc10-8"></a>Can I create a customised skin for my survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>To create a new skin for your survey, go to the "Settings" page and select "Skins". Open the "Create a new skin" tab where you can change the look of different elements of your survey: question and answer text, survey title, help text and many more.</p>
	<p>First give a name to your new skin. Then select the item that you want to skin. On the right side of the screen you find a box where you can change the font of your item: foreground and background colour, font style, font family, font size and font weight. Below, in the "Skin Preview Survey", you can immediately see how the modified item looks like in your survey. Then click on "Save".</p>
	<p>If you want to change several items, you can modify one after the other and save them at the end when all items are finalised. It is not necessary to do a save after each modified item.</p> 
	<p>To adapt your survey to your new skin, go to the "Properties" page and select "Appearance". Click on "Edit" and choose your new skin in the "Style/Skin" drop-down menu. Then click on "Save".</p> 
	
	<h1><a class="anchor" name="_Toc11"></a>Managing contacts and invitations</h1>
	<h2><a class="anchor" name="_Toc11-1"></a>What is the "Address Book"?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>In the "Address Book" you can create your own groups of participants. This way you can invite people or organisations who match certain criteria (e.g. "male" and "older than 21"). Every potential participant is stored as a contact in the address book along with an unlimited list of editable attributes. You can save every contact to your address book as long as it has an identifier ("Name") and an email address.</p>
	<h2><a class="anchor" name="_Toc11-2"></a>What are the "Attributes" of a contact?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Every contact saved in the address book can be characterized by a variable set of attributes such as "Country", "Phone", "Remarks" etc. You can create a new attribute by editing a contact. In the "Edit Contact" window, open the attributes menu and select "New...". A new window will pop up where you can define the name of the new attribute. The newly created attribute will appear as a column in the address book and can also be added to a set of contacts.</p>
	<h2><a class="anchor" name="_Toc11-3"></a>How do I add new contacts to my address book?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Go to the "Address Book" page and click on "Add Contact" to add a single contact. You can click on "Import" to upload an existing list of contacts in XLS, ODS, CSV or TXT format. See also "<a href="#_Toc11-5">How do I import multiple contacts from a file to my address book?</a>"</p>
	<h2><a class="anchor" name="_Toc11-4"></a>What is a "Registration Form"?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>A registration form can be understood as a survey that automatically creates contacts from the personal data that participants submit. To enable this, open your survey, go to the "Properties" page and select "Advanced Settings". Click on "Edit", select "Yes" for "Create Contacts" and "Save". As soon as this is selected, the system inserts 2 compulsory free-text questions ("Name" and "Email") to make sure that every participant enters valid personal data.</p>
	<p>By enabling the "Attribute" option for individual questions, the user can choose what other information is stored about the newly created contact (e.g. a Text-Question with the attribute "Telephone" can be used to store the participant's phone number in the address book).</p>
	<h2><a class="anchor" name="_Toc11-5"></a>How do I import multiple contacts from a file to my address book?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>To import a list of contacts into the system, EUSurvey offers a wizard that guides the user to the import procedure. Currently, the following file formats are supported: XLS, ODS, CSV and TXT (with separator).</p>
	<p>To start the wizard, please select "Import" from your "Address Book" page. As a first step, please select the file you have saved your contacts in, specify if your file contains a header row or not, and specify the type of separator you used for CSV and TXT files (the most probable character is suggested by default).</p>
	<p>As a second step, the system will ask you to map the individual columns to new attributes for your contacts in EUSurvey. Please notice that the mandatory attributes "Name" and "Email" must be mapped to be able to proceed. Once you click "Next", the system loads your file into the system and displays the individual contacts that will be imported. You can unselect individual contacts you don't want to import and click "Save" to save your contacts to your address book.</p>
	<h2><a class="anchor" name="_Toc11-6"></a>How do I edit an attribute value for multiple contacts at a time?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>To edit an attribute value for multiple contacts, please search and select the contacts in your address book, select "Bulk Edit" from the action selector and click on "OK".</p>
	<p>In the pop-up you can choose to keep, clear or set values for multiple contacts. By default only the configured attributes will be shown. Click on the green cross to view other attributes. After clicking "Update" and confirming the security message, the application will save your changes to your address book.</p>
	<h2><a class="anchor" name="_Toc11-7"></a>Can I export contacts from my address book to my computer?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Yes, on your "Address Book" page please click on one of the icons in the top right-hand corner representing individual file formats. You will find the exported contacts on the "Exports" page.</p>
	
	<h1><a class="anchor" name="_Toc12"></a>Inviting participants</h1>
	<h2><a class="anchor" name="_Toc12-1"></a>How do I specify a set of possible participants? What is a "Guest List"?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>In EUSurvey you can group selected contacts and send out individual emails to every single contact containing individual access links. This is called a "Guest List". It is the second way, in addition to the general survey password, in which you can give people the opportunity to complete your survey.</p>
	<p>To invite multiple contacts to your survey, open your survey and go to the "Participants" page. Click on "Create new Guest List" to start a wizard that will guide you through the process. Choose a name for the group and select one of the following types of guest lists:</p>
		<ul>
			<li>Contacts from "Address Book" (Default)<br/>Select contacts from the "Address Book" (see "<a href="#_Toc11-1">What is the "Address Book?</a>") to add them to your guest list</li>
			<li>EU institutions and other bodies (EU staff only)<br/>Select multiple departments from your institution/agency to add all persons working in this department to your guest list</li>
			<li>Tokens<br/>Create a list of tokens (or "Access Codes") that can be distributed offline to access a secured online survey</li>
		</ul>
	<p>Please use the search functionality on your address book and click the "Add" button on the next screen to move contacts from your address book to your new guest list. Clicking "Save" will create a new guest list with all the contacts you want to invite to take part in your survey.</p>
	<p>Please keep on reading to learn how you can send emails with individual access links to configured contacts from one of your guest lists.</p>
	<h2><a class="anchor" name="_Toc12-2"></a>How do I edit/remove an existing guest list?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Open your survey and go to the "Participants" page. To edit the guest list, click on the little pen icon. To remove a list, first click on the "Deactivate" button. Now you can click on the "Remove" button to delete the list.</p>
	<h2><a class="anchor" name="_Toc12-3"></a>How do I send my participants an invitation email?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>As soon as you have created a new guest-list, you can send them invitation emails. For 'secured' as well as for 'open' surveys, everyone will receive an individual access link. <b>This means that everyone, who receives an e-mail invitation sent from EUSurvey will only be able to submit one single contribution.</b></p>
	<p>On the "Participants" page, click on the little envelope icon. A window will open where you can choose an e-mail-template from the "Style" box. By default, the used style is "EUSurvey". You can then change the subject and content of your email and define a "reply-to" e-mail address. All replies to your invitation mail will then be sent to this address. Then save your e-mail text. It will be available for all your guest lists and for all your surveys. You will find it in the dropdown list of the "Text" box. Then click on "Next". A wizard will guide you through the invitation process.</p>
	
	<h1><a class="anchor" name="_Toc13"></a>Managing your personal account</h1>
	<h2><a class="anchor" name="_Toc13-1"></a>How do I change my password?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Access to the EUSurvey reference installation at the European Commission is managed using EU Login, so EUSurvey users are asked to change their EU Login password if they have lost it. This can be done by clicking on "Forgot your Password?" on the EU Login page.</p>
	<h2><a class="anchor" name="_Toc13-2"></a>How do I change my email address?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>If you access EUSurvey using an EU Login user account, you cannot change your email address in EUSurvey. Connect to EU Login and select "Modify my personal data" from the "Account information" tab after logging in to EU Login.</p>
	<p>If you are a user of the OSS version of EUSurvey or a business user of the API interface, please connect to the application. Under "Settings" > "My Account" > "Language" click on "Change Email Address".</p>
	<h2><a class="anchor" name="_Toc13-3"></a>How do I change my default language?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>It is possible to change the default language for new surveys. Go to "Settings" -> "My Account" and click on "Change Language". Once the update has been saved, the system will propose the configured language as the primary language for any new surveys you create.</p>
	
	<h1><a class="anchor" name="_Toc14"></a>Privacy</h1>
	<h2><a class="anchor" name="_Toc14-1"></a>This system uses cookies. What information is saved there?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>The system uses session "cookies" in order to ensure reliable communication between the client and the server. Therefore, user's browser must be configured to accept "cookies". The cookies disappear once the session has been terminated.</p>
	<p>The system uses local storage to save copies of the inputs of a participant to a survey  in order to have a backup if the server is not available during submission or the user's computer is switched off accidentally or any other cause. The local storage contains the IDs of the questions and the draft answers. Once a participant has submitted one's answers successfully to the server or has successfully saved a draft on the server, the data is removed from the local storage. There is a checkbox above the survey "Save a backup on your local computer (disable if you are using a public/shared computer)" to disable the feature. In that case, no data will be stored on the used computer.</p>
	<h2><a class="anchor" name="_Toc14-2"></a>What information is stored by EUSurvey when participants submit a contribution?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>The information that is saved by EUSurvey depends on the security settings of your survey as well as on the method you use to invite your participants to contribute to your survey.</p>
	<p><b>Publicly accessible open survey:</b> By default, if your survey is not secured, EUSurvey does not save any user-related information. However, the IP of every connection is saved for security reasons for every server request (see <a href="https://ec.europa.eu/eusurvey/home/privacystatement" target="_blank">privacy statement on the protection of personal data</a>).</p>
	<p><b>Survey secured with a password:</b> When having your survey secured by a password only, EUSurvey does not save any user-related information. However, the IP of every connection is saved for security reasons for every server request (see <a href="https://ec.europa.eu/eusurvey/home/privacystatement" target="_blank">privacy statement on the protection of personal data</a>).</p>
	<p><b>Survey secured with EU Login authentication:</b> When having your survey secured by EU Login authentication, EUSurvey will save the email address of the EU Login account. However, the IP of every connection is saved for security reasons for every server request (see <a href="https://ec.europa.eu/eusurvey/home/privacystatement" target="_blank">privacy statement on the protection of personal data</a>).</p>
	<p><b>Sending invitations using EUSurvey:</b> If you use EUSurvey to send invitations to your participants in a guest-list on the Participants page, EUSurvey will send a unique invitation link to each participant. On submission, EUSurvey will save an invitation number that can be used to match the invited participant with the submitted contributions. This behaviour is independent from your survey's security settings. In addition, the IP of every connection is saved for security reasons for every server request (see <a href="https://ec.europa.eu/eusurvey/home/privacystatement" target="_blank">privacy statement on the protection of personal data</a>).</p>
	<p><b>Creating an anonymous survey:</b> You can choose to create an anonymous survey by setting the "Privacy" in the "Security Settings" of your Properties to "No". Then, all collected user information will be replaced by "Anonymous". However, the IP of every connection is saved for security reasons for every server request (see <a href="https://ec.europa.eu/eusurvey/home/privacystatement" target="_blank">privacy statement on the protection of personal data</a>).</p>
	<h2><a class="anchor" name="_Toc14-3"></a>Do I need to include a privacy statement?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>This depends on the questions you ask and the type of data you collect using your survey. Please note that your participants may not want to take part in your survey if you cannot guarantee the confidentiality of the data submitted.</p>
	<p><b>For EU staff only:</b></p>
	<p>We draw your attention to the policy on "protection of individuals with regards to the processing of personal data" <a href="http://eur-lex.europa.eu/LexUriServ/LexUriServ.do?uri=OJ:L:2001:008:0001:0022:EN:PDF" target="_blank">(regulation (EC) 45/2001)</a>. If personal data is collected, a privacy statement must be drafted and then published together with the questionnaire. Please contact the Data Protection Co-ordinator of your DG in order to validate the privacy statement. Additionally, any collection of personal data must be notified to the Data Protection Officer (DPO). Please contact your Data Protection Co-ordinator if you need assistance for notification to the DPO.</p>
	<p>Please find herewith some templates of a privacy statement that you could use for your surveys. You can change them according to your needs:</p>
		<ul>
			<li>Template <a href="https://circabc.europa.eu/sd/a/a8f80d78-8620-4326-95ee-7bceb5b18fbc/Template_privacy_statement_surveys_or_consultations.doc" target="_blank">"Privacy Statement for surveys or consultations"</a></li>
			<li>Template <a href="https://circabc.europa.eu/sd/a/650ea0ea-79d4-4cf3-93d4-5feb37af10a1/Template_privacy_statement_online_registrations.doc" target="_blank">"Privacy Statement for event and conference registrations"</a></li>
		</ul>
	</div>
	</div>

<%@ include file="../footer.jsp" %>	

</body>
</html>
