<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<c:choose>
	<c:when test="${responsive != null}">
		<%@ include file="footerresponsive.jsp" %>
	</c:when>
	<c:otherwise>
		<div class="footer">		
			<div class="footer-content">
		
				<div style="float: left; width: 550px;">
					<c:choose>
						<c:when test="${oss}"><spring:message code="footer.fundedOSS" /></c:when>
						<c:otherwise><spring:message code="footer.funded" /></c:otherwise>
					</c:choose>
				</div>
				
				<div style="float:right; width: 300px; text-align: right">
			
					<a href="<c:url value="/home/documentation"/>"><spring:message code="label.Support" /></a> 
					<c:if test="${showprivacy}">
						| <a href="<c:url value="/home/privacystatement"/>"><spring:message code="label.PrivacyStatement.bis" /></a>
						| <a href="<c:url value="/home/tos"/>"><spring:message code="label.TermsOfService" /></a>
					</c:if>
					
				</div>
				
				<div style="clear: both"></div>
				
				<div id="footer-content-languages" style="margin-top: 10px;">			
		 			<c:if test="${page != 'welcome' }">
			 			<a href="?language=bg">Български</a>&#160; 
			 			<a href="?language=cs">Čeština</a>&#160; 
			 			<a href="?language=da">Dansk</a>&#160; 
			 			<a href="?language=de">Deutsch</a>&#160; 
			 			<a href="?language=et">Eesti keel</a>&#160; 
			 			<a href="?language=el">Ελληνικά</a>&#160; 
			 			<a href="?language=en">English</a>&#160; 
			 			<a href="?language=es">Español</a>&#160; 
			 			<a href="?language=fr">Français</a>&#160; 
			 			<a href="?language=ga">Gaeilge</a>&#160; 
			 			<a href="?language=hr">Hrvatski jezik</a>&#160; 
			 			<a href="?language=it">Italiano</a>&#160; 
			 			<a href="?language=lv">Latviešu valoda</a>&#160; 
			 			<a href="?language=lt">Lietuvių kalba</a>&#160; 
			 			<a href="?language=hu">Magyar</a>&#160; 
			 			<a href="?language=mt">Malti</a>&#160; 
			 			<a href="?language=nl">Nederlands</a>&#160; 
			 			<a href="?language=pl">Polski</a>&#160; 
			 			<a href="?language=pt">Português</a>&#160; 
			 			<a href="?language=ro">Română</a>&#160; 
			 			<a href="?language=sk">Slovenčina</a>&#160; 
			 			<a href="?language=sl">Slovenščina</a>&#160; 
			 			<a href="?language=fi">Suomi</a>&#160; 
			 			<a href="?language=sv">Svenska</a>&#160;			 
					</c:if>
				</div>
			
			</div>
		</div>
		
		<%@ include file="includes2.jsp" %>
		
		<div id="footerVersionNumber" style="color: #999; text-align: center"><%@include file="versionfooter.txt" %></div>
		
		<c:if test='${param.surveylanguage != null}'>
			<script type="text/javascript">
				$("#footer-content-languages").find("a").each(function(){
					$(this).attr("href", $(this).attr("href") + "&surveylanguage=${param.surveylanguage}");
				});
			</script>
		</c:if>
		
	</c:otherwise>
</c:choose>
