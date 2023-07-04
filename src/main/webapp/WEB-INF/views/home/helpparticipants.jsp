<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" session="true" %>
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

		#ulContainer {
			margin-bottom: 50px;
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
				<div class="page underlined" style="padding-top: 110px">
			</c:when>
			<c:when test="${responsive != null}">
				<div class="page underlined" style="max-width: 100%; padding: 10px; padding-top: 40px; ">
				<div class="alert alert-warning">Important info: To create and manage surveys, please open the EUSurvey website with a computer. It is not recommended to login to EUSurvey with a mobile or tablet device.</div>
			</c:when>
			<c:otherwise>
				<div class="page underlined" style="padding-top: 40px;">
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
					You can contact the survey owner using the 'Contact' option on the survey right panel.
				</p>
				<h1>
				    Accessing a survey
				</h1>
				<h2>
					What does 'The url you entered was not correct' mean?
				</h2>
				<p>
					It means the system cannot authorise you to access the survey. This is mostly the case
					when a once active invitation has been deleted or deactivated as the activation period has expired.
				</p>
				<p>
				    This means that a previously active invitation has been deleted or
				    deactivated after the activation period has expired.
				</p>
				<p>
					If you think you are using a valid access link, please contact the author of the survey.
				</p>
				<h2>
				    What does 'Page not found' mean?
				</h2>
				<p>
				    It means:
				</p>
				<ul>
				    <li>
						you are using an incorrect link to access your survey on EUSurvey, or
				    </li>
				    <li>
						the survey you are looking for has already been removed from the system.
				    </li>
				</ul>
				<p>
				    If you think the link is valid, please inform the author of the survey directly.
					Otherwise, please inform the body responsible for publishing the link that it is incorrect.
				</p>
				<h2>
					What does 'This survey has not yet been published or has already been unpublished in the meantime' mean?
				</h2>
				<p>
					This is mostly the case when the invitation has been sent out, but the survey has not yet been
					published or has already been unpublished by the survey organizers.
				</p>
				<p>
					For further information, please contact the survey owner.
				</p>
				<h2>
				    Which browsers are supported by EUSurvey?
				</h2>
				<p>
					The last two versions of Microsoft Edge and the latest version of Mozilla Firefox and Google Chrome
					are supported by EUSurvey. Using other browsers might cause compatibility issues.
				</p>
				<h2>
					Can I answer to a survey using my mobile phone or my tablet PC?
				</h2>
				<p>
					Yes, EUSurvey features a responsive design for the published survey. This means, the design of the
					questionnaire will adapt to the screen size and resolution of the device you use.
				</p>
				<h1>
				    Submitting a contribution
				</h1>
				<h2>
					What does 'This value is not a valid number/date/e-mail address' mean?
				</h2>
				<p>
					In EUSurvey, the author of a survey can specify special types of questions that expect a certain
					input format, for example, a number, a date or an e-mail address.
					Dates have to be given in a DD/MM/YYYY format.
				</p>
				<h2>
					Why does my selected answer disappear when answering a matrix question?
				</h2>
				<p>
					This is a possible feature of the matrix question where you can only select each possible answer
					once. It can be used to ensure a 'ranking' on given answers.
				</p>
				<h1>
					After contributing
				</h1>
				<h2>
				    Can I view/print my contribution after it has been submitted?
				</h2>
				<p>
					Yes. Right after submitting, the system will offer you a print option on the Confirmation page.
				</p>
				<h2>
				    How can I save a PDF copy of my Contribution?
				</h2>
				<p>
					Right after submitting, the system will offer you the possibility to request to receive an email
					with a PDF copy of your contribution attached.
				</p>
				<p>
					Please use the ‘Get PDF’ button on the 'Confirmation' page for this purpose.
				</p>
				<h2>
				    Can I edit my contribution once it has been submitted?
				</h2>
				<p>
				    This varies for each survey, depending on how it has been set up.
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
					In order to edit your submitted contribution, please use the Contribution ID that you previously
					noted from the 'Confirmation' page. The Contribution ID appears also on some PDF documents related
					to your contribution.
				</p>
				<p>
					In case you did not save the Contribution ID, please use the '<a href="https://ec.europa.eu/eusurvey/home/support">Contact us</a>' form.
				</p>
				<h2>
					What is my contribution ID?
				</h2>
				<p>
					Contribution ID is the individual number of your answer that is automatically generated. It is a unique identifier allowing the system to recognize your contribution.
				</p>
				<p>
					Once your contribution is submitted, your contribution ID is displayed on the confirmation page. If you save the PDF or send it to your email address, you will see the contribution ID in the upper left side of the PDF contribution.
				</p>
				<p>
					Your contribution ID is useful in case you wish to contact the EUSurvey support or the survey organizers.
				</p>
				<h2>
					Where can I find my contribution ID?
				</h2>
				<p>
					It is displayed right after submission of your answers.
					<ol>
						<li>
							Open your survey link and answer the survey
						</li>
						<li>
							Once submitted you will see your contribution ID on the screen
						</li>
						<li>
							Send the contribution PDF to your email
						</li>
						<li>
							Open PDF contribution
						</li>
						<li>
							Contribution ID will be displayed on the left upper-side of PDF
						</li>
					</ol>
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
					My contribution was not submitted on time. What can I do?
				</h2>
				<p>
					Please contact the survey organizers directly. If the survey organizers’ contact details is not available on the survey page, then please <a href="https://ec.europa.eu/eusurvey/home/support">contact</a> the EUSurvey support team.
				</p>

				<h2>
					My PDF viewer gives me an error message saying 'Insufficient Image data'
				</h2>
				<p>
					Uploading corrupted images can lead to the PDF reader not being able to display the image properly.
					<br/>
				    This will lead to an internal error with your PDF reader.
				</p>
				<p>
					To correct this problem, you must either repair or remove the image.
				</p>
				<h2>
					Why are little boxes shown on the PDF export of the survey?
				</h2>
				<p>
					This may occur if the fonts used by the survey authors or participants are not supported by the application.
				</p>
				<p>
					In case the respective character cannot be found on the system, the application replaces it by a
					little box to show that this character is not supported by the PDF rendering engine.
				</p>
				<p>
					Please feel free to report to the contact information located in the information area, that an
					unsupported character has been used.
				</p>
				<p>
					Please notice that this does not have any effect on your contribution. Once your contribution has
					been saved correctly, it can easily be visualized and exported by the responsible authority for the
					survey, even if the PDF engine of the application has not been able to render your PDF correctly.
				</p>
				<h2>
					Where can I find my answers saved as draft?
				</h2>
				<p>
					After clicking on 'save as draft', you will automatically be re-directed to a page showing you a
					link where you can retrieve your draft to edit and submit your answers.
				</p>
				<p>
				    <b>Please save this link!</b>
					You can send it by E-mail, save it to your favourites or copy it to the clipboard.
				</p>
				<h1 class="empty">
					Report Abuse
				</h1>
				<p>
					If a survey contains illegal content or violate the rights of others (including intellectual
					property rights, competition law and general law), please use the ‘Report Abuse’ link on the
					right-side panel.
				</p>
				<p>
					Please refer to the EUSurvey <a href="https://ec.europa.eu/eusurvey/home/tos">Terms of Service</a>
					for more information on this matter.
				</p>
				<h1>
				    Privacy
				</h1>
				<h2>
				    This system uses cookies. What information is saved there?
				</h2>
				<p>
					The system uses session 'cookies' in order to ensure reliable communication between the client and
					the server. Therefore, your browser must be configured to accept 'cookies'. The cookies disappear
					once the session has been terminated.
				</p>
				<p>
					The system uses local storage to save copies of your input to a survey, in order to have a backup if
					the server is not available during submission, or your computer is switched off accidentally, or any
					other cause. The local storage contains the IDs of the questions and the draft answers. Once you
					successfully submitted the survey to the server, or you have successfully saved a draft on the server,
					the data is removed from local storage. There is a checkbox above the survey 'Save a backup on your
					local computer (disable if you are using a public/shared computer)' to disable the feature. In that
					case, no data will be stored on your computer.
				</p>
			</div> <!-- faqcontent -->
		</div>
	</div>
	
	<%@ include file="../footer.jsp" %>	

</body>
</html>
