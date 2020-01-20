<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<c:choose>
	<c:when test="${responsive != null}">
		<%@ include file="footerresponsive.jsp" %>
	</c:when>
	<c:otherwise>
		<div class="footer footer-bottom">
			<div class="row footer2" style="height: auto">	
				<div style="max-width: 800px; margin-left: auto; margin-right: auto">
					<div class="col-md-8" style="padding-right: 70px;">
						<c:choose>
							<c:when test="${USER != null}">
								<a class="logolink" href="<c:url value="/dashboard"/>"><img src="${contextpath}/resources/images/logo_Eusurvey-small-white.png" alt="EUSurvey"/></a>
							</c:when>
							<c:otherwise>
								<a class="logolink" href="<c:url value="/home/welcome"/>"><img src="${contextpath}/resources/images/logo_Eusurvey-small-white.png" alt="EUSurvey"/></a>
							</c:otherwise>
						</c:choose>
						
						<br /><br />
						<div class="fundedDiv">
							<c:choose>
								<c:when test="${oss}"><spring:message code="footer.fundedOSS" /></c:when>
								<c:otherwise><spring:message code="footer.funded" /></c:otherwise>
							</c:choose>
						</div>
					</div>
					
					<div class="col-md-4" style="padding-top: 46px;">
						<c:if test="${showprivacy}">
							<a href="<c:url value="/home/privacystatement"/>"><spring:message code="label.PrivacyStatement.bis" /></a><br />
							<a href="<c:url value="/home/tos"/>"><spring:message code="label.TermsOfService" /></a><br />
						</c:if>
						<a href="https://ec.europa.eu/info/cookies_en"><spring:message code="label.Cookies" /></a>
					</div>
				</div>
			</div>
			<div class="row footer2" style="height: auto; padding-bottom: 5px;">
				<div id="footerVersionNumber" style="text-align: center">
					<spring:message code="label.Version" />&nbsp;<%@include file="versionfooter.txt" %>
				</div>
			</div>
		</div>
		
		<%@ include file="includes2.jsp" %>
		
		<c:if test='${param.surveylanguage != null}'>
			<script type="text/javascript">
				$("#footer-content-languages").find("a").each(function(){
					$(this).attr("href", $(this).attr("href") + "&surveylanguage=${param.surveylanguage}");
				});
			</script>
		</c:if>
		
	</c:otherwise>
</c:choose>
