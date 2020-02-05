<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>	
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Survey" /></title>
	<%@ include file="../includes.jsp" %>
	
	<style type="text/css">
		
		
	</style>
</head>
<body style="text-align: center;">
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
	
		<div class='${responsive != null ? "responsivepage" : "page"}' style='padding-top: 40px;'>
	
	 		<div style="font-size: 30px; text-align: center;">
				${form.survey.title}
			</div>
			
			<br />
			
			<p>
				<spring:message code="info.ContactSurveyOwner" arguments="${contextpath}/home/helpparticipants" />
			</p>
			
			<br />			
			
		 	<form:form action="contact/${shortname}" method="post" >
		 		<fieldset>			 				 	
					<input type="hidden" name="shortname" value="<esapi:encodeForHTMLAttribute>${form.survey.shortname}</esapi:encodeForHTMLAttribute>" />
										
					<div class="form-group">
						<label class="control-label" for="reason"><span class="mandatory">*</span><spring:message code="support.ContactReason" /></label>
						<input class="form-control" id="reason" name="reason" autocomplete="off" type="text" />
 					</div>
 					
 					<div class="form-group">
						<label class="control-label" for="name"><span class="mandatory">*</span><spring:message code="label.yourname" /></label>
						<input class="form-control" id="name" name="name" autocomplete="off" type="text" />
 					</div>
 					
 					<div class="form-group">
						<label class="control-label" for="email"><span class="mandatory">*</span><spring:message code="label.youremail" /></label>
						<input class="form-control" id="email" name="email" autocomplete="off" type="text" />
 					</div>
 					
 					<div class="form-group">
						<label class="control-label" for="subject"><span class="mandatory">*</span><spring:message code="support.subject" /></label>
						<input class="form-control" id="subject" name="subject" autocomplete="off" type="text" />
 					</div>
					
					<div class="form-group">
						<label class="control-label" for="message"><span class="mandatory">*</span><spring:message code="support.yourmessagetosurveyowner" /></label>
						<textarea class="form-control" style="width: 400px;" id="message" name="message" autocomplete="off"></textarea>
 					</div>
					
					<div style="margin-top: 10px;">
						<input class="btn btn-primary" type="submit" value="<spring:message code="Submit" />"/>
					</div>
		 		</fieldset>
			</form:form>				
				
		</div>
	</div>

	<c:choose>
		<c:when test="${responsive != null}">
			<%@ include file="../footerresponsive.jsp" %>
		</c:when>
		<c:otherwise>
			<%@ include file="../footer.jsp" %>		
		</c:otherwise>
	</c:choose>
	
	<%@ include file="../generic-messages.jsp" %>

</body>
</html>
