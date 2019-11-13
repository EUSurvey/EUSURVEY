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
	</style>
		
	<script type="text/javascript">
		$(function() {
			 $("a.anchorTop").click(function(){
				 $('html, body').animate({scrollTop : 0},100);
					return false;
			 });
		});
	</script>
		
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
					<li><a class="anchorlink" href="#_Toc1">How can I contact the author of the survey?</a></li>
					<li><a class="anchorlink head" href="#_Toc369865010">Accessing a survey</a>
						<ul>
							<li><a class="anchorlink" href="#_Toc369865012">What does &quot;The url you entered was not correct&quot; mean?</a></li>
							<li><a class="anchorlink" href="#_Toc369865013">What does &quot;Page not found&quot; mean?</a></li>
							<li><a class="anchorlink" href="#_Toc369865026">Which browsers are supported by EUSurvey?</a></li>	
							<li><a class="anchorlink" href="#_Toc369865027">Can I answer to a survey using my mobile phone or my tablet PC?</a></li>
						</ul>
					</li>
					<li><a class="anchorlink head" href="#_Toc369865014">Submitting a contribution</a>
						<ul>
							<li><a class="anchorlink" href="#_Toc369865015">What does &quot;This value is not a valid number/date/e-mail address&quot; mean?</a></li>
							<li><a class="anchorlink" href="#_Toc369865016">Why does my selected answer disappear when answering a matrix question?</a></li>
						</ul>
					</li>
					
					<li><a class="anchorlink head" href="#_Toc369865016a">Report an issue with a survey</a></li>
					
					<li><a class="anchorlink head" href="#_Toc369865017">After contributing</a>
						<ul>
							<li><a class="anchorlink" href="#_Toc369865018">Can I view/print my contribution after it has been submitted?</a></li>
							<li><a class="anchorlink" href="#_Toc369865019">How can I save a PDF copy of my contribution?</a></li>
							<li><a class="anchorlink" href="#_Toc369865020">Can I edit my contribution once it has been submitted?</a></li>
							<li><a class="anchorlink" href="#_Toc369865021">I just contributed to a survey. Can I see what other people answered?</a></li>
							<li><a class="anchorlink" href="#_Toc369865022">My PDF viewer gives me an error message saying &quot;Insufficient Image data&quot;</a></li>
							<li><a class="anchorlink" href="#_Toc369865023">Why are little boxes shown on the PDF export of the survey?</a></li>
							<li><a class="anchorlink" href="#_Toc369865028">Where can I find my answers saved as draft?</a></li>
						</ul>					
					</li>
					<li><a class="anchorlink head" href="#_Toc369865024">Privacy</a>
						<ul>
							<li><a class="anchorlink" href="#_Toc369865025">This system uses cookies. What information is saved there?</a></li>
						</ul>
					</li>
				</ul>
			</div>
			<br/ ><br />
			
			<h2><a class="anchor" name="_Toc1"></a>How can I contact the author of the survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			<p>By email - click <i>Contact</i> (on the right hand side of the 1st page of the survey).</p>
			
			<h1><a class="anchor" name="_Toc369865010"></a>Accessing a survey</h1>
			<h2><a class="anchor" name="_Toc369865012"></a>What does &quot;The url you entered was not correct&quot; mean?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			<p>It means the system cannot authorise you to access the survey. This is mostly the case when a once active invitation has been deleted or deactivated as the activation period has expired.</p> 
			<p>If you think you are using a valid access link, please contact the author of the survey.
			</p>
			<h2><a class="anchor" name="_Toc369865013"></a>What does &quot;Page not found&quot; mean?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			<p>
				It means:
				<ul>
					<li>you are using an incorrect link to access your survey on EUSurvey, or</li>
					<li>the survey you are looking for has already been removed from the system.</li>
				</ul>
		 		If you think the link is valid, please inform the author of the survey directly. Otherwise, please inform the body responsible for publishing the link that it is incorrect.
			</p>
			<h2><a class="anchor" name="_Toc369865026"></a>Which browsers are supported by EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			<p>The last two versions of Internet Explorer, Mozilla Firefox and Google Chrome are supported by EUSurvey.</p>
			<p>Using other browsers might cause compatibility problems.</p>
			<h2><a class="anchor" name="_Toc369865027"></a>Can I answer to a survey using my mobile phone or my tablet PC?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			<p>Yes, EUSurvey features a responsive design for the published survey. This means, the design of the questionnaire will adapt to the resolution of the device you use to enter your survey.</p>
					
			<h1><a class="anchor" name="_Toc369865014"></a>Submitting a contribution</h1>
			<h2><a class="anchor" name="_Toc369865015"></a>What does &quot;This value is not a valid number/date/e-mail address&quot; mean?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			<p>In EUSurvey, the author of a survey can specify special types of questions that expect a certain input format, for example a number, a date or an e-mail address. Dates have to be given in a DD/MM/YYYY format.</p>
			<h2><a class="anchor" name="_Toc369865016"></a>Why does my selected answer disappear when answering a matrix question?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			<p>This is a possible feature of the matrix question where you can only select each possible answer once. It can be used to ensure a &quot;ranking&quot; on given answers.</p>
					
			<h1><a class="anchor" name="_Toc369865016a"></a>Report an issue with a survey<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h1>
			<p>If a survey contains illegal content or violate the rights of others (including intellectual property rights, competition law and general law), please use the ‘Report an issue with this survey’ link on the right-side pane.</p>
			<p>Please refers to the EUSurvey <a href="${contextpath}/home/tos">Terms of Service</a> for more information on this matter.</p>
			
			<h1><a class="anchor" name="_Toc369865017"></a>After contributing</h1>
			<h2><a class="anchor" name="_Toc369865018"></a>Can I view/print my contribution after it has been submitted?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			<p>Yes. Right after submitting, the system will offer you a print option.</p>
			<h2><a class="anchor" name="_Toc369865019"></a>How can I save a PDF copy of my Contribution?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			<p>The system will offer a PDF copy of your contribution for download, right after you have submitted.</p>
			<h2><a class="anchor" name="_Toc369865020"></a>Can I edit my contribution once it has been submitted?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			<p>This varies for each survey, depending on how it has been set up.</p>
			<p>For some surveys, you can <a target="_blank" href="${contextpath}/home/editcontribution">re-access after submitting</a>, but for others this feature might not be enabled.
			</p>
			<h2><a class="anchor" name="_Toc369865021"></a>I just contributed to a survey. Can I see what other people answered?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			<p>This varies for each survey, depending on how it has been set up.</p>
			<p>If, after submitting your contribution, you can't see a link to the published results, this feature might not be available.</p>
			<p>If you think that the results of this survey could be of public interest, please contact the <a href="#_Toc1">author of the survey</a>.</p>
			<h2><a class="anchor" name="_Toc369865022"></a>My PDF viewer gives me an error message saying &quot;Insufficient Image data&quot;<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			<p>
				Uploading corrupted images can lead to the PDF reader not being able to display the image properly.
				<br/>
				This will lead to an internal error with your PDF reader. 
			</p>
			<p>To correct this problem, you must either repair or remove the image.</p>
			<h2><a class="anchor" name="_Toc369865023"></a>Why are little boxes shown on the PDF export of the survey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			<p>
				This may occur if the fonts used by the survey authors or participants are not supported by the application.
				<br/>
				In case the respective character cannot be found on the system, the application replaces it by a little box to show that this character is not supported by the PDF rendering engine. 
			</p>
			<p>Please feel free to report to the contact information located in the information area, that an unsupported character has been used.</p>
			<p>Please notice that this does not have any effect on your contribution. Once your contribution has been saved correctly, it can easily be visualized and exported by the responsible authority for the survey,
			   even if the PDF engine of the application has not been able to render your PDF correctly.</p>
			<h2><a class="anchor" name="_Toc369865028"></a>Where can I find my answers saved as draft?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>   
			<p>After clicking on "save as draft", you will automatically be re-directed to a page showing you a link where you can retrieve your draft to edit and submit your answers. <b>Please save this link!</b> You can send it by Email, save it to your favourites or copy it to the clipboard.</p>
			
			<h1><a class="anchor" name="_Toc369865024"></a>Privacy</h1>
			<h2><a class="anchor" name="_Toc369865025"></a>This system uses cookies. What information is saved there?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			<p>The system uses session &quot;cookies&quot; in order to ensure reliable communication between the client and the server. Therefore, your browser must be configured to accept &quot;cookies&quot;. The cookies disappear once the session has been terminated.</p>
			<p>The system uses local storage to save copies of your input to a survey, in order to have a backup if the server is not available during submission, or your computer is switched off accidentally, or any other cause. 
			   The local storage contains the IDs of the questions and the draft answers. Once you successfully submitted the survey to the server, or you have successfully saved a draft on the server, the data is removed from local storage.
			   There is a checkbox above the survey &quot;Save a backup on your local computer (disable if you are using a public/shared computer)&quot; to disable the feature. In that case, no data will be stored on your computer.</p>
				
		</div>
	</div>
	
	<%@ include file="../footer.jsp" %>	

</body>
</html>
