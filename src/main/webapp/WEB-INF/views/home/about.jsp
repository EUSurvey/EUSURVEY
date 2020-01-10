<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>

<head>
	<title>EUSurvey -
		<spring:message code="label.About" />
	</title>
	<%@ include file="../includes.jsp" %>
	<c:if test="${runnermode != null }">
		<script type="text/javascript">
			$(function () {
				$(".headerlink, .header a").each(function () {
					if ($(this).attr("href").indexOf("?") == -1)
						$(this).attr("href", $(this).attr("href") + "/runner");
				});
			});
		</script>
	</c:if>

</head>

<body id="bodyHelpAbout">
	 <div class="page-wrap">
		<%@ include file="../header.jsp" %>
	
		<c:choose>
			<c:when test="${USER != null && runnermode == null }">
				<%@ include file="../menu.jsp" %>
				<div class="page" style="padding-top: 110px; width: 800px;">
			</c:when>
			<c:when test="${responsive != null }">
				<div class="page" style="padding-top: 40px; width: 100%; padding: 20px;">
			</c:when>
			<c:otherwise>
				<div class="page" style="padding-top: 40px; width: 800px;">
			</c:otherwise>
		</c:choose>
		<div style="text-align:center">
			<a href="<c:url value="/home/welcome"/>" target="_blank">
				<img src="${contextpath}/resources/images/logo_Eusurvey.png" alt="EUSurvey logo" style="width:480px; max-width: 90%" /><br />
			</a>
		</div>
		<div class="pageheader">
			<h1>
				<c:choose>
					<c:when test="${oss}">
						<spring:message code="message.about.oss.title"></spring:message>
					</c:when>
					<c:otherwise>
						<spring:message code="message.about.title"></spring:message>
					</c:otherwise>
				</c:choose>
			</h1>
		</div>
	
		<c:choose>
			<c:when test="${oss}">
				<spring:message code="message.about.oss.paragraph.one"></spring:message>
			</c:when>
			<c:otherwise>
				<spring:message code="message.about.paragraph.one"></spring:message>
			</c:otherwise>
		</c:choose>
	
		<a href="https://ec.europa.eu/eusurvey" target="_blank">(https://ec.europa.eu/eusurvey)</a><br />
		<br />
	
		<c:choose>
			<c:when test="${oss}">
				<spring:message code="message.about.oss.paragraph.two" htmlEscape="false"></spring:message>
				<spring:message code="message.about.oss.paragraph.three" htmlEscape="false"></spring:message>
			</c:when>
			<c:otherwise>
				<spring:message code="message.about.paragraph.two.new" htmlEscape="false"></spring:message>
				
				<c:choose>
						<c:when test="${enablepublicsurveys}">
							<spring:message code="message.about.paragraph.three" htmlEscape="false"></spring:message>
						</c:when>
						<c:otherwise>
							<spring:message code="message.about.paragraph.threeB" htmlEscape="false"></spring:message>
						</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	
		<spring:message code="message.about.paragraph.four" htmlEscape="false"></spring:message>
		<a onclick="$('#thrid-party-dialog').modal()">
			<spring:message code="message.about.paragraph.four.linkText"></spring:message>
		</a> <br />
		<br />
		</div>
	
		<div class="modal" id="thrid-party-dialog" data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<img src="${contextpath}/resources/images/ikons/32/globe.png" alt="globe" />
						Third party resources
					</div>
					<div class="modal-body">
						<ul>
							<li>icons made by <a href="http://www.freepik.com" title="Freepik">Freepik</a> from <a href="http://www.flaticon.com"
								 title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/"
								 title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></li>
							<li>Ikon<a href="http://ikons.piotrkwiatkowski.co.uk/"> by Adam Kwiatkowski</a></li>
						</ul>
					</div>
					<div class="modal-footer">
						<a class="btn btn-primary" data-dismiss="modal">
							<spring:message code="label.Close" /></a>
					</div>
				</div>
			</div>
		</div>
	</div>

	<%@ include file="../footer.jsp" %>

</body>

</html>