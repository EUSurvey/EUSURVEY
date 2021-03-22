<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>

<head>
<title>EUSurvey - <spring:message code="label.Delphi" />
</title>
<%@ include file="../includes.jsp"%>
<c:if test="${runnermode != null }">
	<script type="text/javascript">
		$(function() {
			$(".headerlink, .header a").each(function() {
				if ($(this).attr("href").indexOf("?") == -1)
					$(this).attr("href", $(this).attr("href") + "/runner");
			});
		});
	</script>
</c:if>

</head>

<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp"%>

		<c:choose>
			<c:when test="${USER != null && runnermode == null }">
				<%@ include file="../menu.jsp"%>
				<div class="page" style="padding-top: 110px; width: 800px;">
			</c:when>
			<c:when test="${responsive != null }">
				<div class="page"
					style="padding-top: 40px; width: 100%; padding: 20px;">
			</c:when>
			<c:otherwise>
				<div class="page" style="padding-top: 40px; width: 800px;">
			</c:otherwise>
		</c:choose>

		<h1>About the Online Delphi</h1>

		<p>
			The Online Delphi was designed by the <a target="_blank"
				href="https://knowledge4policy.ec.europa.eu/foresight/about_en">Competence
				Centre on Foresight of the Joint Research Centre</a>, at the European
			Commission, in cooperation with IT and design corporate teams. The
			Online Delphi represents a public online platform, developed under
			the umbrella of the EU Survey. Before using this tool, please consult
			the <a target="_blank"
				href="${contextpath}/resources/documents/Delphi_Guide.pdf">Delphi
				guide</a>.
		</p>

		<p>
			The <a
				href="http://www.foresight-platform.eu/community/forlearn/how-to-do-foresight/methods/classical-delphi/">Delphi
				method</a> is a structured expert survey used to gather opinions on
			different possible developments in the long-term future on a given
			topic. It represents one of the methodologies used by the foresight
			community to gather collective intelligence about the future. It also
			serves to create consensus on a specific topic and quantify
			assumptions regarding the future. Within a specific project, the
			Online Delphi can complement the qualitative approach offered by
			other <a target="_blank"
				href="https://knowledge4policy.ec.europa.eu/foresight_en">foresight
				tools and methods</a>.
		</p>

		<p>Delphi questionnaires can be organised in rounds, where
			participants respond to questions within an established period.
			Results of respondents are computed and sent back for a second round
			where experts have to reassess and justify their answers, if situated
			outside of the group's median.</p>

		<p>Initially administrated by post, the advantage of today's
			Online Delphi is that the results are displayed immediately after
			each entry, so that the participants can see how the assessments
			evolve. Additionally, the respondents can return as many times as
			they want (within a specific period) to see how the responses evolve,
			as well as to change their own entries, if they wish so, and comment
			other answers. They can also attach images or documents to support
			their answers/comments. The respondents can choose what to answer and
			what not to. The Online Delphi is completely anonymous.</p>

		<p>The scope of the Online Delphi to elicit, collect and
			synthesise the opinions of a large group of experts on certain
			forward-looking subjects, in an efficient, user-friendly and dynamic
			way.</p>

		<p>
			If you are interested in how this methodology works in practice,
			please check the Delphi survey carried out within the project <a
				target="_blank"
				href="https://ec.europa.eu/jrc/en/publication/future-customs-eu-2040-results-real-time-delphi-survey">The
				Future of Customs in the EU 2040</a>.
		</p>

		<c:if test="${contact != null}">
			<p>
				<br />
			<c:choose>
				<c:when test="${contact.startsWith('form:')}">
					<a class="btn btn-primary" data-toggle="tooltip" title="<spring:message code='info.ContactForm' />" href="${contextpath}/runner/contactform/${param.survey}">
						<spring:message code='info.ContactForm' />
					</a>
				</c:when>
				<c:when test="${contact.contains('@')}">
					<a class="btn btn-primary" href="mailto:<esapi:encodeForHTMLAttribute>${contact}</esapi:encodeForHTMLAttribute>">
						<span class="glyphicon glyphicon-envelope" aria-hidden="true"></span>
						<esapi:encodeForHTML>${contact}</esapi:encodeForHTML>
					</a>
				</c:when>
				<c:otherwise>
					<a class="btn btn-primary" href="<esapi:encodeForHTMLAttribute>${fixedContact}</esapi:encodeForHTMLAttribute>" target="_blank">
						<span class="glyphicon glyphicon-globe" aria-hidden="true"></span>
						<esapi:encodeForHTML>${fixedContactLabel}</esapi:encodeForHTML>
					</a>
				</c:otherwise>
			</c:choose>
			</p>
		</c:if>

	</div>
	</div>

	<%@ include file="../footer.jsp"%>

</body>

</html>
