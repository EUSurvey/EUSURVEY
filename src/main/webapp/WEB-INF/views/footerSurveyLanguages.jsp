<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	
<div class="footer">

	<div class="footer-content">

		<div style="float: left; width: 550px;">
			<c:choose>
				<c:when test="${oss}">${form.getMessage("footer.fundedOSS")}</c:when>
				<c:otherwise>${form.getMessage("footer.funded")}</c:otherwise>
			</c:choose>
		</div>
		
		<div style="float:right; width: 300px; text-align: right">
			<a target="_blank" href="https://circabc.europa.eu/sd/a/2e8fd5cf-4095-4413-9aa4-d46bf706aafc/EUSurvey_Quick_Start_Guide.pdf">${form.getMessage("label.Userguide")} (pdf)</a> |
			<a href="<c:url value="/home/support"/>">${form.getMessage("label.Support")}</a>
		</div>
		
		<div style="clear: both"></div>
		
		<div id="footer-content-languages" style="margin-top: 10px; text-align: center">
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
	
	</div>
</div>

<%@ include file="includes2.jsp" %>

<div id="footerVersionNumber" style="color: #999; text-align: center"><%@include file="versionfooter.txt" %></div>
