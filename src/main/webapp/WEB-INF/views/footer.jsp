<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<c:choose>
	<c:when test="${responsive != null}">
		<%@ include file="footerresponsive.jsp" %>
	</c:when>
	<c:otherwise>
		<div class="footer2 footer-bottom" style="height: auto">
			<div class="row">
				<div style="max-width: 1000px; margin-left: auto; margin-right: auto">
					<div class="col-md-6">
						<c:choose>
							<c:when test="${USER != null}">
								<a class="logolink" href="<c:url value="/dashboard"/>"><img src="${contextpath}/resources/images/logo_Eusurvey-small-white.png" alt="EUSurvey"/></a>
							</c:when>
							<c:otherwise>
								<a class="logolink" href="<c:url value="/home/welcome"/>"><img src="${contextpath}/resources/images/logo_Eusurvey-small-white.png" alt="EUSurvey"/></a>
							</c:otherwise>
						</c:choose>
						
						<br /><br />
						<i>						
							<c:choose>
								<c:when test="${form != null && useUILanguage == null}">
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
						</i>
					</div>

					<div class="col-md-3" style="padding-top: 40px;">
						<c:set var="localeCode" value="${fn:toLowerCase(pageContext.response.locale.language)}" />
						
						<c:choose>
							<c:when test="${form != null && useUILanguage == null}">
								<c:if test="${showprivacy}">
									<a href="<c:url value="/home/privacystatement"/>">${form.getMessage("label.PrivacyStatement.bis")}</a><br />
									<a href="<c:url value="/home/accessibilitystatement"/>">${form.getMessage("label.AccessibilityStatement")}</a><br />
									<a href="<c:url value="/home/tos"/>">${form.getMessage("label.TermsOfService")}</a><br />
								</c:if>
								
								<a href="https://ec.europa.eu/info/cookies_${localeCode}">${form.getMessage("label.Cookies")}</a><br />
							</c:when>
							<c:otherwise>
								<c:if test="${showprivacy}">
									<a href="<c:url value="/home/privacystatement"/>"><spring:message code="label.PrivacyStatement.bis" /></a><br />
									<a href="<c:url value="/home/accessibilitystatement"/>"><spring:message code="label.AccessibilityStatement" /></a><br />
									<a href="<c:url value="/home/tos"/>"><spring:message code="label.TermsOfService" /></a><br />
								</c:if>
								
								<a href="https://ec.europa.eu/info/cookies_${localeCode}"><spring:message code="label.Cookies" /></a><br />
							</c:otherwise>
						</c:choose>
					</div>
					<div class="col-md-3" style="padding-top: 40px;">
						<c:set var="localeCode" value="${fn:toLowerCase(pageContext.response.locale.language)}" />

						<c:choose>
							<c:when test="${form != null && useUILanguage == null}">
								<a href="https://commission.europa.eu/languages-our-websites_${localeCode}">${form.getMessage("label.LanguagesPolicy")}</a><br />
								<a href="https://commission.europa.eu/privacy-policy-websites-managed-european-commission_${localeCode}">${form.getMessage("label.PrivacyPolicy")}</a><br />
								<a href="https://commission.europa.eu/legal-notice_${localeCode}">${form.getMessage("label.LegalNotice")}</a><br />
							</c:when>
							<c:otherwise>
								<a href="https://commission.europa.eu/languages-our-websites_${localeCode}"><spring:message code="label.LanguagesPolicy" /></a><br />
								<a href="https://commission.europa.eu/privacy-policy-websites-managed-european-commission_${localeCode}"><spring:message code="label.PrivacyPolicy" /></a><br />
								<a href="https://commission.europa.eu/legal-notice_${localeCode}"><spring:message code="label.LegalNotice" /></a><br />
							</c:otherwise>
						</c:choose>
					</div>

				</div>
			</div>
			<div class="row" id="footer-content-languages">
				<div style="max-width: 970px; margin-left: auto; margin-right: auto">
					<c:if test="${page != 'welcome'}">
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
			<div class="row" id="footer-content-languages-form" style="display: none">
				<div style="max-width: 970px; margin-left: auto; margin-right: auto">		
					<c:if test="${form != null && form.getLanguages().size() != 0}">
						<c:choose>
							<c:when test="${isthankspage != null || isdraftinfopage != null}">
								<a><esapi:encodeForHTML>${form.language.name}</esapi:encodeForHTML></a>
							</c:when>
							<c:otherwise>				
								<c:forEach var="lang" items="${form.getLanguagesAlphabetical()}"> 	
									<a href="?language=${lang.value.code}&surveylanguage=${lang.value.code}"><esapi:encodeForHTML>${lang.value.name}</esapi:encodeForHTML></a>
								</c:forEach>	
							</c:otherwise>
						</c:choose>						
					</c:if>	
				</div>
			</div>
			<div class="row" style="text-align: center">					
				<div id="footerVersionNumber">
					<c:choose>
						<c:when test="${form != null && useUILanguage == null}">
							${form.getMessage("label.Version")}
						</c:when>
						<c:otherwise>
							<spring:message code="label.Version" />
						</c:otherwise>
					</c:choose>

					&nbsp<%@include file="versionfooter.txt" %>
				</div>
			</div>
		</div>
		
		<%@ include file="includes2.jsp" %>
				
	</c:otherwise>
</c:choose>
