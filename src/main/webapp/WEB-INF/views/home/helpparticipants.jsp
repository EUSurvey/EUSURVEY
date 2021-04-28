<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" session="true" %>
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
			margin: 20px;			
		}		
		
		.anchorlink {
			margin-left: 40px;
			color: #005580;
		}
		
		.anchorlink a:hover {
			text-decoration: underline;
			color: #005580;
		}
		
		.head {
			margin-left: 20px;
		}
		
		.empty {
			margin-left: 0px;
			text-decoration: none;
		}
	</style>
		
	<script language="javascript" type="text/javascript" src="${contextpath}/resources/js/tree/treemenu.js?version=<%@include file="../version.txt" %>"></script>
	<link rel="stylesheet" href="${contextpath}/resources/js/tree/treeview.css?version=<%@include file="../version.txt" %>" type="text/css">
	<script language="javascript" type="text/javascript" src="${contextpath}/resources/js/tree/treemenu2.js?version=<%@include file="../version.txt" %>"></script>
		
</head>
<body id="bodyDocHelpParticipant">
	<div class="page-wrap">
		<a name="topAnchor" id="topAnchor"></a>
	
		<%@ include file="../header.jsp" %>		
	
		<c:choose>
			<c:when test="${USER != null && runnermode == null }">
				<%@ include file="../menu.jsp" %>	
				<div class="page" style="padding-top: 110px">
			</c:when>
			<c:when test="${responsive != null}">
				<div class="page" style="max-width: 100%; padding: 10px; padding-top: 40px; ">
				<div class="alert alert-warning">Important info: To create and manage surveys, please open the EUSurvey website with a computer. It is not recommended to login to EUSurvey with a mobile or tablet device.</div>
			</c:when>
			<c:otherwise>
				<div class="page" style="padding-top: 40px;">
			</c:otherwise>
		</c:choose>	
		
			<div class="pageheader">
				<div style="float:right; font-size:125%" >
				[<a href="helpparticipants?faqlanguage=en">EN</a>] [<a href="helpparticipants?faqlanguage=fr">FR</a>] [<a href="helpparticipants?faqlanguage=de">DE</a>]
				</div>
				<h1><spring:message code="label.HelpParticipants" /></h1>
			</div>
	
			<h2>Contents</h2>
			<div id="ulContainer">
		
				<a href="javascript:ddtreemenu.flatten('treemenu', 'expand')">Expand All</a>&nbsp;|&nbsp;<a href="javascript:ddtreemenu.flatten('treemenu', 'contact')">Collapse All</a>
				<br/><br/>
				<ul id="treemenu" class="treeview" rel="closed">
					
				</ul>
			</div>
			
			<div  id="faqcontent">
				<h1>Logging in/creating an account</h1>
				<h2>I have an EU Login account. Do I need to register separately for EUSurvey?</h2>
				<p>
					No, an EU Login account is enough.
				</p>
				<p>
					To access EUSurvey, click on the login button on the <a href="https://ec.europa.eu/eusurvey/home/welcome">EUSurvey homepage</a>.
				</p>
				<h2>
					How do I connect to EUSurvey?
				</h2>
				<p>
					After you click on ‘login’ on the <a href="https://ec.europa.eu/eusurvey/home/welcome">EUSurvey homepage</a>, you will be redirected to the EUSurvey login screen.
				</p>
				<p>
					There, choose the option that matches your personal case:
					<ul>
						<li>
							<b>If you work for an EU institution</b> – choose the second option to connect, using your EU Login username and password.
						</li>
						<li>
							<b>If you don't work for an EU institution (externals)</b> – choose the first option to connect. You’ll need to have previously registered your mobile phone to pass the <a href="https://en.wikipedia.org/wiki/Help:Two-factor_authentication">two-factor authentication</a>.
						</li>
					</ul>
				</p>
				<p>
					<a href="https://webgate.ec.europa.eu/cas/eim/external/register.cg">Create an EU Login account</a> (if you don’t already have one)
				</p>
				<p>
					<a href="https://ecas.ec.europa.eu/cas/userdata/mobileApp/manageMyMobileDevices.cgi">Register your mobile phone</a> (If you don't work for an EU institution)
				</p>
				<h1>Contacting the survey owner</h1>
				<h2>
				    How can I contact the survey owner?
				</h2>
				<p>
				    Use the ‘Contact’ option on the panel on the right-hand side.
				</p>
				<h1>
				    Accessing a survey
				</h1>
				<h2>
				    What does the following message mean? ‘The url you entered was not
				    correct’.
				</h2>
				<p>
			        EUSurvey cannot authorise your access to the survey.
				</p>
				<p>
				    This means that a previously active invitation has been deleted or
				    deactivated after the activation period has expired.
				</p>
				<p>
				    If you think you are using a valid access link, contact the survey author.
				</p>
				<h2>
				    What does ‘Page not found’ mean?
				</h2>
				<p>
				    It means either that:
				</p>
				<ul>
				    <li>
				        you’re using an invalid link to access your survey ; or
				    </li>
				    <li>
				        the survey you want to access to has already been removed.
				    </li>
				</ul>
				<p>
				    If you think the link is valid, inform the survey author. Otherwise, inform
				    the body that published the invalid link.
				</p>
				<h2>
			        What does the following message mean: ‘This survey has not yet been
			        published or has already been unpublished in the meantime’?
				</h2>
				<p>
				    It means that the invitation to participate has been sent out but the
				    survey organisers have not yet published or have already unpublished the
				    survey.
				</p>
				<p>
				    For more details, contact the survey owner.
				</p>
				<h2>
				    Which browsers are supported by EUSurvey?
				</h2>
				<p>
				    Microsoft Edge (last 2 versions) and Mozilla Firefox and Google Chrome
				    (latest versions).
				</p>
				<p>
				    Using other browsers might cause compatibility issues.
				</p>
				<h2>
				    Can I answer a survey using my mobile phone or tablet PC?
				</h2>
				<p>
				    Yes, the questionnaire’s design will adapt to the size and resolution of
				    your device’s screen..
				</p>
				<h1>
				    Submitting a contribution
				</h1>
				<h2>
				    What does the following message mean: ‘This value is not a valid
				    number/date/e-mail address’?
				</h2>
				<p>
				    The survey author can specify certain types of questions that involve a
				    certain input format, e.g. a number, a date or an e-mail address.
				</p>
				<p>
				    Dates must be in DD/MM/YYYY format.
				</p>
				<h2>
				    When answering a matrix question why does the answer I selected disappear?
				</h2>
				<p>
				    Some matrix questions are configured in such a way so that you can only
				    select each possible answer once. This is sometimes used for ranking the
				    answers given.
				</p>
				<h1>
				    After submitting your contribution
				</h1>
				<h2>
				    Can I view/print my contribution after it has been submitted?
				</h2>
				<p>
				    Yes.
				</p>
				<p>
				    After submitting, click the print option on the confirmation page.
				</p>
				<h2>
				    How can I save a PDF copy of my Contribution?
				</h2>
				<p>
				    Yes.
				</p>
				<p>
				    After submitting, you can request via email a PDF copy of your
				    contribution.
				</p>
				<p>
				    Click ‘Get PDF’ on the confirmation page.
				</p>
				<h2>
				    Can I edit my contribution once it has been submitted?
				</h2>
				<p>
				    This varies for each survey, depending on its set-up.
				</p>
				<p>
				    For some surveys, you can
				    <a
				        href="https://ec.europa.eu/eusurvey/home/editcontribution"
				        target="_blank"
				    >
				        reaccess after submitting
				    </a>
				    , but for others this feature might not be enabled.
				</p>
				<p>
				    To edit your submitted contribution, use the contribution ID that you got
				    from the confirmation page. This ID also appears on some PDF documents
				    related to your contribution.
				</p>
				<p>
				If you didn’t save the contribution ID, click the ‘    <a href="https://ec.europa.eu/eusurvey/home/support">Contact us</a>’ form.
				</p>
				<h2>
				    I just contributed to a survey. Can I see other people’s answers?
				</h2>
				<p>
				    This varies for each survey, depending on its settings.
				</p>
				<p>
				    If, after submitting your contribution, you don't see a link to the
				    published results, this feature might not be available.
				</p>
				<p>
				    If you think that the results of this survey could be of public interest,
				    contact the survey
				    <a href="#_Toc_2_0">
				        author
				    </a>
				    .
				</p>
				<h2>
				    Why my PDF viewer gives me an error message saying ‘Insufficient Image
				    data’?
				</h2>
				<p>
				    If you upload corrupted images, the PDF reader cannot display the image
				    properly.
				    <br/>
				    This will lead to an internal error with your PDF reader.
				</p>
				<p>
				    To correct this, you must either repair or remove the image.
				</p>
				<h2>
				    Why are the little boxes displayed on the PDF export of the survey?
				</h2>
				<p>
				    These may appear if the survey authors or participants use fonts that
				    aren’t supported by EUSurvey.
				</p>
				<p>
				    If EUSurvey doesn’t have the character you require, it is replaced with a
				    little box to show that the PDF rendering engine does not support this
				    character.
				</p>
				<p>
				    If you use an unsupported character, then you are advised to report it via
				    the ‘Contact’ section on the right hand-side.
				</p>
				<p>
				    This does not affect your contribution. Once your answers have been saved
				    correctly, they can be easily viewed and exported by the survey author,
				    even if the PDF rendering engine is unable to render your PDF correctly.
				</p>
				<h2>
				    Where can I find my saved drafts of my answers?
				</h2>
				<p>
				    After clicking on ‘save as draft’, you will be automatically redirected to
				    a page with a link to where you can retrieve your draft to edit and submit
				    your answers.
				</p>
				<p>
				    <b>Be sure to save this link!</b>
				    Send it by email, save it to your favourites or copy it to the clipboard.
				</p>
				<h1 class="empty">
				    Reporting an abuse
				</h1>
				<p>
				    Click ‘Report Abuse’ on the right-hand side of the panel if a survey
				    contains illegal content or violates the rights of others (including
				    intellectual property rights, competition law and general law).
				</p>
				<p>
				For more details, see EUSurvey’s    <a href="https://ec.europa.eu/eusurvey/home/tos">Terms of Service</a>.
				</p>
				<h1>
				    Privacy
				</h1>
				<h2>
				    This system uses cookies. What information is saved there?
				</h2>
				<p>
				    EUSurvey uses session cookies to ensure reliable communication between the
				    client and the server. Your browser must be configured to accept cookies.
				    They disappear once the session is finished.
				</p>
				<p>
				    The system saves copies of your input to a survey locally, so there is a
				    backup in cases such as the server not being available during submission,
				    or your computer being switched off accidentally.
				</p>
				<p>
				    The local storage contains the question IDs and your draft answers.
				</p>
				<p>
				    Once you successfully submit the survey to the server, or have successfully
				    saved a draft on the server, the data is removed from local storage.
				</p>
				<p>
				    You can disable this feature by clicking the checkbox above the survey:
				    ‘Save a backup on your local computer (disable if you are using a
				    public/shared computer)’. Subsequently, no data will be stored on your
				    computer.
				</p>
				<h1>
				    ECF surveys
				</h1>
				<h2>
				    What is an ECF survey?
				</h2>
				<p>
					ECF stands for European Competency Framework. ECF survey are self-assessment surveys allowing people to assess their level of skills and knowledge in comparison to target scores pre-defined for each job profiles.
				</p>

				<h2>
				    How to fill up an ECF self-assessment survey ?
				</h2>
				<p>
					It is fairly easy and straightforward to fill-up an ECF survey. You first need to select your current job profile except if you want to go for the full self-assessment. The latter will display all questions for all skills and knowledge independently of your current job profile. It takes longer to answer of course but will allow you to compare your scores to any other job profiles at the end.
				</p>
				<p>
					After having selected your job profile, the questionnaire will adapt and only display the corresponding questions. This means that questions related to skills and knowledge not required fro your job profile will not be displayed, and so, do not need to be answered.
				</p>
				<p>
					All questions displayed need to be answered. This is for giving a complete picture of your profile and allowing you to get a comparison against the target scores defined for your job profile. If you do not have time to complete your self assessment, you can simply save it as a draft and continue later.
				</p>
				<p>
					For saving a draft, you need to click on the ‘Save as a Draft’ button on the right panel. You will be taken to a screen showing a link that can be used later to access and edit your answers.
				</p>
				<p>
					When you have completed your self-assessment you will see a ‘Submit’ button at the bottom of the questionnaire. You need to click on to send your answers and access to the final results screen.
				</p>

				<h2>
				    How to interpret my ECF results ?
				</h2>
				<p>
					<em><strong>Individual results:</strong> Based on the individual answers to the self-assessment questionnaire and on the target proficiency levels corresponding to the job profiles, individual scores for all competencies can be obtained. Using these scores, individuals can identify their competency gaps, i.e. their strengths and weaknesses in some competencies.</em>
				</p>
				<p>
					At the end of your self-assessment, after having submitted your answers, you will be taken to the results screen. This screen aims to present your scores against the defined target scores.
				</p>
				<p>
					This page is made of two parts :
					<ul>
						<li>Scorecard – a table showing your scores and gaps (positive or negative) against the corresponding target scores ; and</li>
						<li>Spider charts</li>
					</ul>
					<img class="center" src="${contextpath}/resources/images/illustrations/ecfResult.png" />
				</p>
				<p>
					There are several settings you can change for changing the visualization :
				</p>
				<p>
					At the top, there is a selection table for the job profile. This allows you to compare your answers (skills & knowledge) to another job profile than the one you have selected in the questionnaire. The initial default value is the one selected in the questionnaire.
					<img class="center" src="${contextpath}/resources/images/illustrations/ecfSelectTargetProfile.png" />
				</p>
				<p>
					If you choose another profile, the scorecard table and spider charts will be automatically refreshed.
				</p>
				<p>
					You can also print or download ('Get PDF' button) your results as a PDF document.
					<img class="center" src="${contextpath}/resources/images/illustrations/ecfContributionFinished.png" />
				</p>
				<p>
					It might be useful to note your Contribution ID and store it in a safe place. This is the  unique ID that allow the survey owner to find your contribution if needed.
				</p>
				

			</div> <!-- faqcontent -->
		</div>
	</div>
	
	<%@ include file="../footer.jsp" %>	

</body>
</html>
