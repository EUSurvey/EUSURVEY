<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Documentation" /></title>	
	<%@ include file="../includes.jsp" %>	
	<c:if test="${runnermode != null }">
		<script type="text/javascript">
			$(function() {
				 $(".headerlink, .header a").each(function(){
					 if ($(this).attr("href").indexOf("?") == -1)
					$(this).attr("href", $(this).attr("href") + "/runner");
				 });
			});
		</script>
	</c:if>
	
	<script type="text/javascript">
			$(function() {
				if (globalLanguage.toLowerCase() == "de")
				{
					$("#docEditorGuideEN").hide();
					$("#docEditorGuideDE").show();
				} else if (globalLanguage.toLowerCase() == "fr")
				{
					$("#docEditorGuideEN").hide();
					$("#docEditorGuideFR").show();
				}
				
				if (globalLanguage.toLowerCase() == "en")
				{
					$(".thousandseparator").html(",");
				}
			});
	</script>
	
	<style type="text/css">
	
		table.niceTable
		{
			width:100%;
		}
		
		table.niceTable tr td
		{
			border-right:1px solid #777;
		}
		table.niceTable tr td:FIRST-CHILD
		{
			border-left:1px solid #777;
		}
	
		table.niceTable td
		{
			padding:10px;
		}
	
		tr.headerGrey
		{
			color:#FFF;
			font-weight:bold;
			font-size:140%;
			background-color:#555;
			text-align:center;
		}
		
		tr.headerGrey th
		{
			padding:10px;
		}
	
		th.th20
		{
			width:20%;
		}
		
		td.tdBold
		{
			font-weight:bold;
		}
		
		tr.trOdd
		{
			background-color:#F5F5F5;
		}
		
		tr.trEven
		{
			background-color:#FFF;
		}
		
		table.niceTable tbody
		{
			border:1px solid #777;
		}

		.boxlink
		{
			display:inline-block;
			width:458px;
			max-width: 100%;
			height:64px;
			line-height:64px;
			margin-top:10px;			
			background-color:#aadb1c;
			color: #333;
			font-weight:bold;
			padding-left: 70px;
			background-repeat:no-repeat;
			background-size:64px;
		}
	
	</style>
	
</head>
<body id="bodyHelpDocumentation">

	<%@ include file="../header.jsp" %>

	<c:choose>
		<c:when test="${USER != null && runnermode == null && responsive == null}">
			<%@ include file="../menu.jsp" %>	
			<div class="page" style="margin-top: 110px">
		</c:when>
		<c:when test="${responsive != null}">
			<div class="page" style="margin-top: 40px; width: 100%; padding: 10px;">
		</c:when>
		<c:otherwise>
			<div class="page" style="margin-top: 40px">
		</c:otherwise>
	</c:choose>	

	
		<div class="pageheader">
			<h1><spring:message code="label.Support" /></h1>
		</div>
	
		<a id="docHelpParticipant" class="boxlink" href="${contextpath}/home/helpparticipants" style="background-image:url('${contextpath}/resources/images/icons/64/doc_11.png');"><spring:message code="label.FAQsParticipants" /></a>
		<a id="docHelpAuthor" class="boxlink" href="${contextpath}/home/helpauthors" style="background-image:url('${contextpath}/resources/images/icons/64/pencil.png');"><spring:message code="label.FAQsAuthors" /></a>
		
		<c:choose>
			<c:when test="${!oss}">
				<a id="docQuickStartGuide" target="_blank" class="boxlink hidden-xs" href="https://circabc.europa.eu/sd/a/2e8fd5cf-4095-4413-9aa4-d46bf706aafc/EUSurvey_Quick_Start_Guide.pdf" style="background-image:url('${contextpath}/resources/images/icons/64/flash_on.png');">EUSurvey - <spring:message code="label.QuickstartGuide" /></a>
			</c:when>
			<c:otherwise>
				<a id="docQuickStartGuide" target="_blank" class="boxlink hidden-xs" href="${contextpath}/resources/documents/eusurvey_oss_quickstart_guide.pdf" style="background-image:url('${contextpath}/resources/images/icons/64/right_4.png');"><spring:message code="label.QuickstartGuide" /></a>				
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test="${!oss}">
				<a id="docBestPractices" target="_blank" class="boxlink hidden-xs" href="https://circabc.europa.eu/sd/d/281e626e-279e-45df-8581-1f6e04feff51/BestPractices-EUSurvey.pdf" style="background-image:url('${contextpath}/resources/images/icons/64/right_4.png');"><spring:message code="label.BestPractices" /></a>
			</c:when>
			<c:otherwise>
				<a id="docBestPractices" target="_blank" class="boxlink hidden-xs" href="${contextpath}/resources/documents/eusurvey_oss_best_practices.pdf" style="background-image:url('${contextpath}/resources/images/icons/64/right_4.png');"><spring:message code="label.BestPractices" /></a>				
			</c:otherwise>
		</c:choose>
			
		<%-- <a id="docBestPractices" target="_blank" class="boxlink" href="https://circabc.europa.eu/sd/d/281e626e-279e-45df-8581-1f6e04feff51/BestPractices-EUSurvey.pdf" style="background-image:url('${contextpath}/resources/images/icons/64/right_4.png');"><spring:message code="label.BestPractices" /></a> --%>
		
		<c:choose>
			<c:when test="${!oss}">
				<a id="docTutorial" class="boxlink hidden-xs" href="https://ec.europa.eu${contextpath}/runner/TutorialEUSurvey" style="background-image:url('${contextpath}/resources/images/icons/64/externalLink.png');"><spring:message code="label.Tutorial" /></a>
			</c:when>
			<c:otherwise>
				<a id="docTutorial" class="boxlink hidden-xs" href="https://ec.europa.eu${contextpath}/runner/TutorialEUSurveyOSS" style="background-image:url('${contextpath}/resources/images/icons/64/externalLink.png');"><spring:message code="label.Tutorial" /></a>				
			</c:otherwise>
		</c:choose>
				
		<a id="docEditorGuideEN" class="boxlink" target="_blank" href="${contextpath}/resources/documents/Editor_Guide.pdf" style="background-image:url('${contextpath}/resources/images/icons/64/flash_on.png');"><spring:message code="label.EditorDocumentation" /></a>
		<a id="docEditorGuideFR" class="boxlink" target="_blank" href="${contextpath}/resources/documents/Editor_Guide_FR.pdf" style="display: none; background-image:url('${contextpath}/resources/images/icons/64/flash_on.png');"><spring:message code="label.EditorDocumentation" /></a>
		<a id="docEditorGuideDE" class="boxlink" target="_blank" href="${contextpath}/resources/documents/Editor_Guide_DE.pdf" style="display: none; background-image:url('${contextpath}/resources/images/icons/64/flash_on.png');"><spring:message code="label.EditorDocumentation" /></a>

		<a id="docQuizGuideEN" class="boxlink" target="_blank" href="${contextpath}/resources/documents/Quiz_Guide.pdf" style="background-image:url('${contextpath}/resources/images/icons/64/flash_on.png');"><spring:message code="label.QuizDocumentation" /></a>
				
		<a id="docLimits" onclick="$('.uselimits').show()" class="boxlink hidden-xs" style="background-image:url('${contextpath}/resources/images/icons/64/right_4.png');"><spring:message code="label.usabilityLimits" /></a>
		
		<div class="pageheader hidden-xs uselimits hideme">
			<br /><br />
			<h1><spring:message code="label.usabilityLimits" /></h1>
		</div>
	
		<table class="niceTable hidden-xs uselimits hideme">
			<thead>
				<tr class="headerGrey">
					<th style="background-color:#FFF;"></th>
					<th class="th20">S</th>
					<th class="th20">M</th>
					<th class="th20">L</th>
					<th class="th20">XL</th>
				</tr>
			</thead>
			<tbody>
				<tr class="trOdd">
					<td class="tdBold"><spring:message code="label.usability.nbQuestion" /></td>
					<td>&lt;25</td>
					<td>&lt;100</td>
					<td>&lt;250</td>
					<td>&gt;250</td>
				</tr>
				<tr class="trEven">
					<td class="tdBold"><spring:message code="label.usability.audience" /></td>
					<td>~20<span class="thousandseparator">.</span>000 <spring:message code="label.answers" /><br/><span style="color:#BBB;">&lt; 40<span class="thousandseparator">.</span>000 <spring:message code="label.answers" /></span></td>
					<td>~5<span class="thousandseparator">.</span>000 <spring:message code="label.answers" /><br/><span style="color:#BBB;">&lt; 10<span class="thousandseparator">.</span>000 <spring:message code="label.answers" /></span></td>
					<td>~1<span class="thousandseparator">.</span>000 <spring:message code="label.answers" /><br/><span style="color:#BBB;">&lt; 2<span class="thousandseparator">.</span>000 <spring:message code="label.answers" /></span></td>
					<td>~250 <spring:message code="label.answers" /><br/><span style="color:#BBB;">&lt; 500 <spring:message code="label.answers" /></span></td>
				</tr>
				<tr class="trOdd">
					<td class="tdBold"><spring:message code="label.usability.supported" /></td>
					<td><p style="text-align:center"><img src="${contextpath}/resources/images/icons/48/tick_48.png" alt="check"/></p></td>
					<td><p style="text-align:center"><img src="${contextpath}/resources/images/icons/48/tick_48.png" alt="check"/></p></td>
					<td><p style="text-align:center"><img src="${contextpath}/resources/images/icons/48/tick_48.png" alt="check"/></p></td>
					<td><p style="text-align:center"><img src="${contextpath}/resources/images/icons/48/delete_48.png" alt="delete"/></p></td>
				</tr>
				<tr class="trEven">
					<td class="tdBold"><spring:message code="label.usability.recommended" /></td>
					<td><p style="text-align:center"><img src="${contextpath}/resources/images/icons/48/thumb-up.png" alt="thumb up"/></p></td>
					<td><p style="text-align:center"><img src="${contextpath}/resources/images/icons/48/thumb-up.png" alt="thumb up"/></p></td>
					<td><p style="text-align:center"><img src="${contextpath}/resources/images/icons/48/thumb-down.png" alt="thumb down"/></p></td>
					<td><p style="text-align:center"><img src="${contextpath}/resources/images/icons/48/thumb-down.png" alt="thumb down"/></p></td>
				</tr>
				<tr class="trOdd">
					<td class="tdBold"><spring:message code="label.usability.observations" /></td>
					<td colspan="2">
						<ul>
							<li><spring:message code="label.usability.observations.hint1" /></li>
							<li><spring:message code="label.usability.observations.hint2" /></li>
						</ul>
					</td>
					<td colspan="2">
						<ul>
							<li><spring:message code="label.usability.observations.hint3" /></li>
							<li><spring:message code="label.usability.observations.hint4" /></li>
							<li><spring:message code="label.usability.observations.hint5" /></li>
						</ul>
					</td>
				</tr>
			</tbody>
		</table>
		
		<br /><br /><br />
		
		<div class="well" style="font-size: 20px;">
			<spring:message code="label.contactform" /><br />
			<spring:message code="label.contactform2" arguments="${contextpath}/home/support" />
		</div>
			
	</div>

<%@ include file="../footer.jsp" %>	

</body>
</html>
