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
		
		<p>The EUSurvey application is managed by Digital services (DG DIGIT) and is designed to be accessible to as many people as possible, including those with disabilities.</p>
		
		<p>This accessibility statement concerns:
			<ul>
				<li>Surveys and their web content, accessible to survey participants</li>
				<li>All EUSurvey instances managed by DG DIGIT (e.g. Consultation, Acceptance)</li>				
			</ul>
		</p>

		<p>Please note that this statement does not cover the back-office or administrative features of the application.</p>

		<span class="asheader">Accessibility features</span>		
		<p>You should be able to:
			<ul>
				<li>Zoom up to 200% without problems.</li>
				<li>Fill out a survey using only a keyboard.</li>
				<li>Navigate through and complete surveys using a screen reader (e.g. NVDA or Windows Narrator).</li>
				<li>Fill out our web accessibility-compliant EU CAPTCHA.</li>				
			</ul>
		</p>
		
		<p><span class="asheader">Keyboard Navigation:</span>
			The EUSurvey application supports keyboard navigation with the following shortcuts:
			<table class="table table-bordered">
				<thead>
					<tr>
						<th>Action</th>
						<th>Keyboard Shortcut</th>
						<th>Example</th>					
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>Move forward the focus</td>
						<td>TAB</td>
						<td>Navigate to the next actionable element.</td>
					</tr>
					<tr>
						<td>Move backward the focus</td>
						<td>MAJ + TAB</td>
						<td>Return to the previous actionable element.</td>
					</tr>
					<tr>
						<td>Activate or close a link, button, or dialog </td>
						<td>ENTER</td>
						<td>Open the 'Contact Form' link or close a dialog</td>
					</tr>
					<tr>
						<td>Select/unselect combo boxes, checkboxes, or radio buttons</td>
						<td>SPACE BAR</td>
						<td>Check or uncheck an option.</td>
					</tr>
					<tr>
						<td>Navigate dropdown menus</td>
						<td>ARROWS UP / DOWN</td>
						<td>Scroll through options in dropdown menus.</td>
					</tr>		
				</tbody>		
			</table>
			<b>Note:</b> Only actionable elements (e.g., buttons, links) are focusable in line with the accessibility guidelines.		
		</p>
		
		<p>
			<span class="asheader">Compliance and Testing</span>
			The EUSurvey application complies with <a target="_blank" href="https://www.w3.org/WAI/standards-guidelines/wcag/">Web Content Accessibility Guidelines (WCAG) 2.1 Level AA</a>.
		</p>
		
		<p>
			To ensure compliance:
			<ul>
				<li>A self-assessment was conducted by DG DIGIT using both automated tools (e.g., WAVE: <a target="_blank" href=" https://wave.webaim.org">https://wave.webaim.org</a>) and manual testing.</li>
				<li>Feedback from users, including people with disabilities, was incorporated to improve accessibility.</li>
				<li><b>Last evaluation date:</b> 1st February 2024.</li>
				<li><b>Last review of this statement:</b> 13th December 2024.</li>
			</ul>
		</p>
		
		<p>
			<span class="asheader">Feedback Mechanism</span>
			We welcome your feedback on the accessibility of the EUSurvey service. If you encounter accessibility barriers, please use our <a href="https://ec.europa.eu/eusurvey/home/support">Feedback form</a> and select the option '<i>I have an accessibility enquiry</i>'.		
		</p>
		
		<p>We aim to respond to all enquiries within 15 business days.</p>
		
		<p>
			<span class="asheader">Compatibility with browsers and assistive technology</span>
			The EUSurvey service is designed to work with:
			<ul>
				<li>The latest version of Google Chrome, Mozilla FireFox and Microsoft Edge browsers.</li>
				<li>Assistive technologies such as NVDA (NonVisual Desktop Access) and Windows Narrator.</li>
			</ul>
		</p>
		
		<p>The service also supports mobile web browsers and screen readers, including VoiceOver (iOS) and TalkBack (Android). However, some advanced survey layouts may not display as intended on smaller screens.</p>
			
		<p>
			<span class="asheader">Non-accessible content</span>
			Despite our commitment to accessibility, some limitations may exist: 
			<ul>
				<li><b>Browser-specific quirks:</b> Older browser versions may not fully support all accessibility features.</li>
				<li><b>Mobile navigation challenges:</b> Complex survey layouts may be difficult to navigate using mobile screen readers.</li>
				<li><b>CAPTCHA usability:</b> While the CAPTCHA is designed to be accessible, users may experience occasional compatibility issues with certain assistive technologies.</li>
			</ul>
		</p>
		
		<p>If you encounter any issues not listed above, please contact us so we can address them.</p>
		
		<p>
			<span class="asheader">Technical specifications</span>
			The accessibility of the EUSurvey service relies on the following web technologies:
			<ul>
				<li><b>HTML:</b> For structuring content.</li>
				<li><b>CSS:</b> For layout and design.</li>
				<li><b>JavaScript:</b> For interactive elements.</li>
			</ul>
		</p>
		
		<p>These technologies are designed to ensure compatibility with assistive tools and modern design browsers.</p>
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