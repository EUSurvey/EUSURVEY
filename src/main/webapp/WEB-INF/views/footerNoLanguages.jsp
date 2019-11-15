<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

	<div class="footer footer-bottom">
		<div class="row" style="background-color: #245077">
			<div style="max-width: 1200px; margin-left: auto; margin-right: auto">
				<div class="col-md-8">
					<a class="logolink" href="<c:url value="/home/welcome"/>"><img src="${contextpath}/resources/images/logo_Eusurvey-small-white.png" alt="EUSurvey"/></a>
					<br /><br />
					<i>
					<c:choose>
						<c:when test="${oss}"><spring:message code="footer.fundedOSS" /></c:when>
						<c:otherwise><spring:message code="footer.funded" /></c:otherwise>
					</c:choose>
					</i>
				</div>
				
				<div class="col-md-2">
					<div class="footerlabel"><spring:message code="label.Navigation" /></div>
					<a href="<c:url value="/home/about"/>"><spring:message code="label.About" /></a><br />
					<a href="<c:url value="/home/publicsurveys"/>"><spring:message code="header.AllPublicSurveys" /></a><br />
					<a href="https://ec.europa.eu"><spring:message code="label.EuropeanCommission" /></a><br />
				</div>
				
				<div class="col-md-2">
					<div class="footerlabel"><spring:message code="label.Help" /></div>
					<c:choose>
						<c:when test="${!oss}">
							<a target="_blank" href="https://circabc.europa.eu/sd/a/2e8fd5cf-4095-4413-9aa4-d46bf706aafc/EUSurvey_Quick_Start_Guide.pdf"><spring:message code="label.Userguide" /></a>
						</c:when>
						<c:otherwise>
							<a target="_blank" href="${contextpath}/resources/documents/eusurvey_oss_quickstart_guide.pdf"><spring:message code="label.Userguide" /></a>
						</c:otherwise>
					</c:choose>
					<br />
					<a href="<c:url value="/home/documentation"/>"><spring:message code="label.Support" /></a><br />
					<c:if test="${showprivacy}">
						<a href="<c:url value="/home/privacystatement"/>"><spring:message code="label.PrivacyStatement.bis" /></a><br />
						<a href="<c:url value="/home/tos"/>"><spring:message code="label.TermsOfService" /></a>
					</c:if>
				</div>
			</div>
		</div>
		<div class="row footer2">	
			<div style="max-width: 1200px; margin-left: auto; margin-right: auto">
				<div class="col-md-2">

				</div>
				
				<div class="col-md-8" id="footer-content-languages">			
		 			
				</div>
			
				<div id="footerVersionNumber" class="col-md-2">
					<spring:message code="label.Version" />&nbsp;<%@include file="versionfooter.txt" %>
				</div>
			</div>
		</div>
	</div>

<%@ include file="includes2.jsp" %>


