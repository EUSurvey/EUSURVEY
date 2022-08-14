<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>	
<%@ page import="java.util.Map" %>
<%@ page import="com.ec.survey.model.Form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>

	<div class="pagepdf">
		
		<div id="runner-content" class="runner-content">

			<form:form action="${action}" id="runnerForm" method="POST" modelAttribute="form">
			
				<form:hidden path="survey.id" />
				<form:hidden path="language.code" />
				
				<input type="hidden" name="uniqueCode" id="uniqueCode" value="<esapi:encodeForHTMLAttribute>${uniqueCode}</esapi:encodeForHTMLAttribute>" />
				<input type="hidden" name="IdAnswerSet" id="IdAnswerSet" value="<esapi:encodeForHTMLAttribute>${answerSet}</esapi:encodeForHTMLAttribute>" />
				<input type="hidden" name="invitation" id="invitation" value="<esapi:encodeForHTMLAttribute>${invitation}</esapi:encodeForHTMLAttribute>" />
				<input type="hidden" name="participationGroup" value="<esapi:encodeForHTMLAttribute>${participationGroup}</esapi:encodeForHTMLAttribute>" />
				<input type="hidden" id="hfsubmit" value="<esapi:encodeForHTMLAttribute>${submit}</esapi:encodeForHTMLAttribute>" />
				<input type="hidden" id="mode" name="mode" value="<esapi:encodeForHTMLAttribute>${mode}</esapi:encodeForHTMLAttribute>" />
				
				<c:if test="${dialogmode != null }">
					<input type="hidden" name="dialogmode" value="true" />
				</c:if>
				
				<c:choose>
					<c:when test="${draftid != null}">
						<input type="hidden" id="draftid" name="draftid" value="${draftid}" />
					</c:when>
					<c:when test="${form.getValidationDraftID().length() > 0}">
						<input type="hidden" id="draftid" name="draftid" value="${form.getValidationDraftID()}" />
					</c:when>
					<c:otherwise>
						<input type="hidden" id="draftid" name="draftid" value="" />
					</c:otherwise>
				</c:choose>
				
				<c:if test="${form.survey.isECF}">
					<div style="max-width: 100%">
						<div class="surveytitle">${form.survey.title}</div><br />
					</div>
								
					<div id="canvasContainerLeft"> 	
						<%@ include file="../ecfGraph.jsp" %>
					</div>
				</c:if>
				
				<%@ include file="../runner/runnercontentinnerpdf.jsp" %>
				
			</form:form>
		
		</div>
		
	</div>
	<script>
		var uniqueCode = "${theUniqueCode}";
		var contextpath = "${contextpath}";
		var surveyShortname = "${surveyShortname}";
	</script>
