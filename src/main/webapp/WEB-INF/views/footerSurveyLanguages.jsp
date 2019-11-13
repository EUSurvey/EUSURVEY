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
					<c:when test="${oss}">${form.getMessage("footer.fundedOSS")}</c:when>
					<c:otherwise>${form.getMessage("footer.funded")}</c:otherwise>
				</c:choose>
				</i>
			</div>
			
			<div class="col-md-2">
				<div class="footerlabel">${form.getMessage("label.Navigation")}</div>
				<a href="<c:url value="/home/about"/>">${form.getMessage("label.About")}</a><br />
				<a href="<c:url value="/home/publicsurveys"/>">${form.getMessage("header.AllPublicSurveys")}</a><br />
				<a href="https://ec.europa.eu">${form.getMessage("label.EuropeanCommission")}</a><br />
			</div>
			
			<!-- <div class="col-md-2">
				<div class="footerlabel"><spring:message code="label.News" /></div>
				<a href="<c:url value="/home/about"/>">Release Notes</a><br />
				<a href="<c:url value="/home/publicsurveys"/>">Upcoming versions</a><br />
			</div> -->
			
			<div class="col-md-2">
				<div class="footerlabel">${form.getMessage("label.Help")}</div>
				<c:choose>
					<c:when test="${!oss}">
						<a target="_blank" href="https://circabc.europa.eu/sd/a/2e8fd5cf-4095-4413-9aa4-d46bf706aafc/EUSurvey_Quick_Start_Guide.pdf">${form.getMessage("label.Userguide")}</a>
					</c:when>
					<c:otherwise>
						<a target="_blank" href="${contextpath}/resources/documents/eusurvey_oss_quickstart_guide.pdf">${form.getMessage("label.Userguide")}</a>
					</c:otherwise>
				</c:choose>
				<br />
				<a href="<c:url value="/home/documentation"/>">${form.getMessage("label.FAQs")}</a><br />
				<a href="<c:url value="/home/support"/>">${form.getMessage("label.Support")}</a><br /> 
				<c:if test="${showprivacy}">
					<a href="<c:url value="/home/privacystatement"/>">${form.getMessage("label.PrivacyStatement.bis")}</a>
				</c:if>
			</div>
		</div>
	</div>
	<div class="row footer2">	
		<div style="max-width: 1200px; margin-left: auto; margin-right: auto">
			<div class="col-md-2">

			</div>
			
			<div class="col-md-8" id="footer-content-languages">			
	 			<c:if test="${form.getLanguages().size() != 0}">		
  		
					<span style="margin-left: 0px; margin-right: 4px" >
						<c:choose>
							<c:when test="${isthankspage != null || isdraftinfopage != null}">
								<a><esapi:encodeForHTML>${form.language.name}</esapi:encodeForHTML></a>
							</c:when>
							<c:otherwise>				
								<c:forEach var="lang" items="${form.getLanguagesAlphabetical()}"> 	
									<a href="?surveylanguage=${lang.value.code}"><esapi:encodeForHTML>${lang.value.name}</esapi:encodeForHTML></a>
								</c:forEach>	
							</c:otherwise>
						</c:choose>
					</span>	 							
	
				</c:if>	
			</div>
		
			<div id="footerVersionNumber" class="col-md-2">
				${form.getMessage("label.Version")}&nbsp;<%@include file="versionfooter.txt" %>
			</div>
		</div>
	</div>
</div>

<%@ include file="includes2.jsp" %>

<div id="footerVersionNumber" style="color: #999; text-align: center"><%@include file="versionfooter.txt" %></div>
