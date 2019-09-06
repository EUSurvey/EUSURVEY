<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div id="responsivefooter" style="text-align: center; margin-top: 10px; padding: 5px; font-size: small; color: #777;">
	<c:choose>
		<c:when test="${form != null}">
			<c:choose>
				<c:when test="${oss}">${form.getMessage("footer.fundedOSS")}</c:when>
				<c:otherwise>${form.getMessage("footer.funded")}</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${oss}"><spring:message code="footer.fundedOSS" /></c:when>
				<c:otherwise><spring:message code="footer.funded" /></c:otherwise>
			</c:choose>			
		</c:otherwise>
	</c:choose>	

	<div style="margin-top: 20px">
		<c:choose>
			<c:when test="${!oss}">
				<a  target="_blank"  href="https://circabc.europa.eu/sd/a/2e8fd5cf-4095-4413-9aa4-d46bf706aafc/EUSurvey_Quick_Start_Guide.pdf" style="background-image:url('${contextpath}/resources/images/icons/64/flash_on.png');"><spring:message code="label.Userguide" /> (pdf)</a> |
			</c:when>
			<c:otherwise>
				<a  target="_blank"  href="${contextpath}/resources/documents/eusurvey_oss_quickstart_guide.pdf" style="background-image:url('${contextpath}/resources/images/icons/64/right_4.png');"><spring:message code="label.Userguide" /> (pdf)</a> |				
			</c:otherwise>
		</c:choose>
		
		<a href="<c:url value="/home/support"/>"><spring:message code="label.Support" /></a> 
		<c:if test="${showprivacy}">
			| <a href="<c:url value="/home/privacystatement"/>"><spring:message code="label.PrivacyStatement.bis" /></a>
		</c:if>	
	</div>
	
</div>

<%@ include file="includes2.jsp" %>

<script type="text/javascript">
    $(function() {	
    	$("#responsivefooter").find("br").remove();	    	
    });
</script>	