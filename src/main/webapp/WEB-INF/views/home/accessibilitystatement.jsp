<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="esapi"
		   uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">

<head>
	<title>Accessibility Statement</title>
	<%@ include file="../includes.jsp"%>
	<link href="${contextpath}/resources/css/yellowfocus.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css"></link>

	<style>
		
		body {
			padding-left: 5px;
			padding-right: 5px;
		}
		
		.asheader {
			font-weight: bold;
			margin-top: 25px;
			margin-bottom: 5px;
			display: block;
		}
		
	</style>

	
</head>

<body>
<div class="page-wrap">
	<c:if test="${readonly != null}">
		<%@ include file="../header.jsp"%>
	</c:if>

	<c:choose>
		<c:when test="${responsive != null}">
			<div class="page" style="width: auto;">
		</c:when>
		<c:when	test="${USER != null && runnermode == null && readonly != null }">
			<%@ include file="../menu.jsp"%>
			<div class="page" style="margin-top: 110px">
		</c:when>
		<c:otherwise>
			<div class="page">
		</c:otherwise>
		</c:choose>
				
		<h1>Accessibility Statement</h1>
		
		<p>This statement applies to content published via the EUSurvey application: <a href="https://ec.europa.eu/eusurvey">https://ec.europa.eu/eusurvey</a></p>
		
		<p>This web application is managed by Digital services (DG DIGIT). It is designed to be used by as many people as possible, including people with disabilities.</p>
		
		<p>Also included are all the other instances managed by DG DIGIT (e.g. Consultation, Acceptance etc.).</p>
		
		<p>It concerns web content of the surveys publicly accessible by the survey participants, the back office of the application is not concerned.</p>
		
		<p>You should be able to:
			<ul>
				<li>Fill in a survey just using a keyboard.</li>
				<li>Navigate through and fill in a survey using a screen reader (e.g. NVDA or Windows Narrator).</li>
				<li>Fill in our web accessibility compliant EU CAPTCHA.</li>				
			</ul>
		</p>
		
		<p><span class="asheader">Keyboard shortcuts:</span>
			<table class="table table-bordered">
				<tr>
					<td>Move forward the yellow focus.</td>
					<td>TAB</td>
				</tr>
				<tr>
					<td>Move backward the yellow focus.<br />Please note that only actionable elements are focusable as per accessibility guidelines.
					</td>
					<td>MAJ + TAB</td>
				</tr>
				<tr>
					<td>To close a dialog when the focus is on the cross top-right icon.</td>
					<td>ENTER</td>
				</tr>
				<tr>
					<td>To activate and access a link such as the 'Contact Form' hyperlink.</td>
					<td>ENTER</td>
				</tr>
				<tr>
					<td>To push a button such as 'Save as a draft' button.</td>
					<td>ENTER</td>
				</tr>
				<tr>
					<td>To select/unselect a combo box (in Single Choice Question) or a checkbox (in Multiple Choice Question).</td>
					<td>SPACE BAR</td>
				</tr>
				<tr>
					<td>To navigate inside a dropdown list such as the 'Help' menu.</td>
					<td>ARROWS UP / DOWN</td>
				</tr>				
			</table>		
		</p>
		
		<p>
			<span class="asheader">Compliance status</span>
			This website is compliant with the <a href="https://www.w3.org/WAI/standards-guidelines/wcag/">Web Content Accessibility Guidelines (WCAG) 2.1 Level AA</a>.
		</p>
				
		<p>The website was last evaluated on 1st November 2024.</p>
		
		<p>
			<span class="asheader">Preparation of this statement</span>
			This statement was reviewed on 5th November 2024.
		</p>
		
		<p>It is based on a self-assessment done by DG DIGIT along with the use of an accessibility scanner: WAVE (https://wave.webaim.org/).</p>
		
		<p>
			<span class="asheader">Feedback</span>
			We welcome your feedback on the accessibility of the EUSurvey service. Please let us know if you encounter accessibility barriers:
			<ul>
				<li><a href="https://ec.europa.eu/eusurvey/home/support">Feedback form</a> choose 'I have an accessibility enquiry'
			</ul>
			We try to respond to feedback within 15 business days from the date of receipt of the enquiry by the responsible Commission department.
		</p>
		
		<p>
			<span class="asheader">Compatibility with browsers and assistive technology</span>
			The EUSurvey service is designed to be compatible with the following most used assistive technologies:
			<ul>
				<li>The latest version of Google Chrome, Mozilla Firefox and Microsoft Edge browsers. Using other browsers might cause compatibility or accessibility issues.</li>
				<li>In combination with the latest versions of NVDA and Windows Narrator.</li>
			</ul>			
		</p>
		
		<p>
			<span class="asheader">Technical specifications</span>
			The accessibility of the EUSurvey service relies on the following technologies to work with the particular combination of web browser and any assistive technologies or plugins installed on your computer:
			<ul>
				<li>HTML</li>
				<li>CSS</li>
				<li>JavaScript</li>
			</ul>
		</p>
		
		<p>
			<span class="asheader">Non-accessible content</span>
			Despite our best efforts to ensure accessibility of the EUSurvey service, we are aware of some limitations, which we are working to fix. Below is a description of known limitations and potential solutions. Please contact us if you encounter an issue not listed below.			
		</p>
		
		<p>The EUSurvey service has no known accessibility limitations at the date of publication of this accessibility statement.</p>
				
	</div>
</div>

	<c:if test="${readonly != null}">
		<%@ include file="../footer.jsp"%>
	</c:if>
	
	<c:if test="${readonly == null}">
		<form:form id="logoutform" action="${contextpath}/j_spring_security_logout" method="post">
	    </form:form>	
	</c:if>		

</body>

</html>