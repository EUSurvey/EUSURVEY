<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div id="responsivefooter" style="text-align: center; margin-top: 10px; padding: 5px; font-size: small; color: #777;">
	<c:choose>
		<c:when test="${form != null}">
			<c:choose>
				<c:when test="${oss}">${form.getMessage("footer.fundedOSSNew")}</c:when>
				<c:otherwise>${form.getMessage("footer.fundedNewDEP")}</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${oss}"><spring:message code="footer.fundedOSSNew" /></c:when>
				<c:otherwise><spring:message code="footer.fundedNewDEP" /></c:otherwise>
			</c:choose>			
		</c:otherwise>
	</c:choose>	

	<div style="margin-top: 20px">
		<a href="<c:url value="/home/documentation"/>"><spring:message code="label.Support" /></a> 
		<c:if test="${showprivacy}">
			| <a href="<c:url value="/home/privacystatement"/>"><spring:message code="label.PrivacyStatement.bis" /></a>
			| <a href="<c:url value="/home/tos"/>"><spring:message code="label.TermsOfService" /></a>
		</c:if>	
	</div>
	
</div>

<%@ include file="includes2.jsp" %>

<script type="text/javascript">
    $(function() {	
    	$("#responsivefooter").find("br").remove();	    	
    });
</script>	