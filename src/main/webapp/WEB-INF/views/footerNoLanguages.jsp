<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	
<div class="footer">

<c:choose>
	<c:when test="${responsive != null}">
		<div>
	</c:when>
	<c:otherwise>
		<div class="footer-content">
		
		<div style="float:right; margin-left: 10px; ">
			<a target="_blank" href="<c:url value="/home/helpparticipants"/>">${form.getMessage("label.FAQ")}</a> |
			<a class="headerlink"  href="<c:url value="/home/support"/>">${form.getMessage("label.Support")}</a>
			<c:if test="${showprivacy}">
				| <a href="<c:url value="/home/privacystatement"/>"><spring:message code="label.PrivacyStatement.bis" /></a>
			</c:if> 
		</div>
	</c:otherwise>
</c:choose>		
		
		<c:choose>
			<c:when test="${oss}">${form.getMessage("footer.fundedOSS")}</c:when>
			<c:otherwise>${form.getMessage("footer.funded")}</c:otherwise>
		</c:choose>
				
		<div style="clear: both"></div>	
	</div>
</div>

<%@ include file="includes2.jsp" %>


