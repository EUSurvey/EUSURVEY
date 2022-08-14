<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>	
<%@ page import="java.util.Map" %>
<%@ page import="com.ec.survey.model.Form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
	
	<c:choose>
		<c:when test="${responsive == null}">
			<div class="page" style="width: 1300px;">	
		</c:when>
		<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("New Official EC Skin")}'>
			<div style="padding-top: 15px; max-width: 100%;">	
		</c:when>
		<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("ECA Skin")}'>
			<div style="padding-top: 35px; max-width: 100%;">		
		</c:when>
		<c:otherwise>
			<div style="padding-top: 35px; max-width: 100%; overflow: hidden;">	
		</c:otherwise>
	</c:choose>	
		
		<div id="runner-content" class="runner-content">

			<form:form action="${action}" id="runnerForm" method="POST" modelAttribute="form" autocomplete="off">
			
				<form:hidden path="survey.id" />
				<form:hidden path="survey.uniqueId" />
				<form:hidden path="language.code" />
				
				<input type="hidden" name="uniqueCode" id="uniqueCode" value="<esapi:encodeForHTMLAttribute>${uniqueCode}</esapi:encodeForHTMLAttribute>" />
				<input type="hidden" name="IdAnswerSet" id="IdAnswerSet" value="<esapi:encodeForHTMLAttribute>${answerSet}</esapi:encodeForHTMLAttribute>" />
				<input type="hidden" name="invitation" id="invitation" value="<esapi:encodeForHTMLAttribute>${invitation}</esapi:encodeForHTMLAttribute>" />
				<input type="hidden" name="participationGroup" value="<esapi:encodeForHTMLAttribute>${participationGroup}</esapi:encodeForHTMLAttribute>" />
				<input type="hidden" id="hfsubmit" value="<esapi:encodeForHTMLAttribute>${submit}</esapi:encodeForHTMLAttribute>" />
				<input type="hidden" id="mode" name="mode" value="<esapi:encodeForHTMLAttribute>${mode}</esapi:encodeForHTMLAttribute>" />
				
				<c:choose>
					<c:when test="${passwordauthenticated != null}">
						<input type="hidden" name="passwordauthenticated" value="true" />				
					</c:when>
					<c:when test='${pageContext.request.getParameter("pw") != null || pw != null}'>
						<input type="hidden" name="passwordauthenticated" value="true" />
					</c:when>
				</c:choose>
				
				<c:if test="${dialogmode != null}">
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
				
				<%@ include file="../runner/runnercontentinner.jsp" %>	
			</form:form>
		
		</div>
		
		<div class="modal" id="busydialog" data-backdrop="static">
			<div class="modal-dialog">
    		<div class="modal-content">
			<div class="modal-body" style="padding-left: 30px; text-align: center">		
				<spring:message code="label.PleaseWait" /><br /><br />
				<img alt="wait animation" src="${contextpath}/resources/images/ajax-loader.gif" />
			</div>
			</div>
			</div>
		</div>
		
		<c:if test='${pageContext.request.getParameter("holf") != null || holf != null}'>
			<div id="holf" class="modal" data-backdrop="static">
				<div class="modal-dialog modal-sm">
    			<div class="modal-content">
				<div class="modal-body" style="padding: 30px; text-align: center">		
					<spring:message code="error.holf" />
				</div>
				<div class="modal-footer">
					<a  class="btn btn-primary" data-dismiss="modal"><spring:message code="label.OK" /></a>				
				</div>
				</div>
				</div>	
			</div>
			<script type="text/javascript">
				$("#holf").modal("show");
			</script>
		</c:if>		
		
		<div class="modal" id="unsaveddelphichangesdialog" role="dialog" data-backdrop="static">
			<div class="modal-dialog modal-sm">
    		<div class="modal-content">
			<div class="modal-body">		
				<spring:message code="info.UnsavedDelphiChange" />
			</div>
			<div class="modal-footer">
				<a href="javascript:;" class="btn btn-default" id="unsaveddelphichangesdialoglink"><spring:message code="label.Yes" /></a>				
				<a href="javascript:;" class="btn btn-primary" onclick="hideModalDialog($('#unsaveddelphichangesdialog'))"><spring:message code="label.Cancel" /></a>				
			</div>
			</div>
			</div>
		</div>
	</div>
