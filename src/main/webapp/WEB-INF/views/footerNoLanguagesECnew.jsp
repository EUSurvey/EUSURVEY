<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<div style="margin-top: 10px;">
	<c:choose>
		<c:when test="${oss}">${form.getMessage("footer.fundedOSSNew")}</c:when>
		<c:otherwise>${form.getMessage("footer.fundedNewDEP")}</c:otherwise>
	</c:choose>
</div>

<div class="layout-footer ecl-site-footer" style="margin-top: 10px;">
	<div class="ecl-container container-fluid container">
		<div class="row ecl-site-footer__row" style="width: 1206px; max-width: 100%; margin-left: auto; margin-right: auto">
			<div class="col-md-4 ecl-site-footer__column" style="padding-left: 0px; padding-rightt: 0px;">
				<div class="ecl-site-footer__section ecl-site-footer__section--site-info">
					<div class="ecl-site-footer__title">
						<c:choose>
							<c:when test="${USER != null}">
								<a class="logolink" href="<c:url value="/forms"/>"><img src="${contextpath}/resources/images/logo_Eusurvey-small-white.png" alt="EUSurvey"/></a>
							</c:when>
							<c:otherwise>
								<a class="logolink" href="<c:url value="/home/welcome"/>"><img src="${contextpath}/resources/images/logo_Eusurvey-small-white.png" alt="EUSurvey"/></a>
							</c:otherwise>
						</c:choose>
					</div>
					<ul class="ecl-site-footer__list">
						<li class="ecl-site-footer__list-item"><a href="${contextpath}/home/accessibilitystatement" class="ecl-link ecl-link--standalone ecl-link--inverted ecl-site-footer__link"><spring:message code="label.AccessibilityStatement" /></a></li>
					</ul>
				</div>
			</div>
			<div class="col-md-4 ecl-site-footer__column" style="padding-left: 0px; padding-rightt: 0px;">
				<div class="ecl-site-footer__section">
					<div class="ecl-site-footer__title"><spring:message code="label.ContactUs" /></div>
					<ul class="ecl-site-footer__list">
						<li class="ecl-site-footer__list-item"><a href="${contextpath}/home/support" class="ecl-link ecl-link--standalone ecl-link--inverted ecl-site-footer__link"><spring:message code="label.ContactInformation" /></a></li>
					</ul>
				</div>
			</div>
			<div class="col-md-4 ecl-site-footer__column" style="padding-left: 0px; padding-rightt: 0px;">
				<div class="ecl-site-footer__section">
					<div class="ecl-site-footer__title"><spring:message code="label.AboutUs" /></div>
					<ul class="ecl-site-footer__list">
						<li class="ecl-site-footer__list-item"><a href="${contextpath}/home/about" class="ecl-link ecl-link--standalone ecl-link--inverted ecl-site-footer__link"><spring:message code="message.about.title" /></a></li>
						<li class="ecl-site-footer__list-item"><a href="${contextpath}/home/tos" class="ecl-link ecl-link--standalone ecl-link--inverted ecl-site-footer__link"><spring:message code="label.TermsOfService" /></a></li>
					</ul>
				</div>
				<div class="ecl-site-footer__section">
					<div class="ecl-site-footer__title"><spring:message code="label.RelatedSites" /></div>
					<ul class="ecl-site-footer__list">
						<li class="ecl-site-footer__list-item"><a href="${contextpath}/home/documentation" class="ecl-link ecl-link--standalone ecl-link--inverted ecl-site-footer__link"><spring:message code="label.Documentation" /></a></li>
						<li class="ecl-site-footer__list-item"><a href="${contextpath}/home/helpparticipants" class="ecl-link ecl-link--standalone ecl-link--inverted ecl-site-footer__link"><spring:message code="label.FAQ" /></a></li>
						<li class="ecl-site-footer__list-item"><a href="${contextpath}/home/download" class="ecl-link ecl-link--standalone ecl-link--inverted ecl-site-footer__link"><spring:message code="label.Download" /></a></li>
					</ul>
				</div>
			</div>
		</div>

		<div class="row ecl-site-footer__row" style="width: 1206px; max-width: 100%; margin-left: auto; margin-right: auto;">
			<div class="col-md-4 ecl-site-footer__column" style="padding-left: 0px; padding-rightt: 0px;">
				<div class="ecl-site-footer__section"><a href="https://commission.europa.eu/index_en" class="ecl-link ecl-link--standalone ecl-site-footer__logo-link">
					<picture class="ecl-picture ecl-site-footer__picture">
						<img class="ecl-site-footer__logo-image" src="${contextpath}/resources/images/logo-en-new-white.svg" alt="European Commission">
					</picture>
				</a></div>
			</div>
			<div class="col-md-4 ecl-site-footer__column" style="padding-left: 0px; padding-rightt: 0px;">
				<div class="ecl-site-footer__section ecl-site-footer__section--split-list">
					<ul class="ecl-site-footer__list">
						<li class="ecl-site-footer__list-item"><a href="https://commission.europa.eu/about-european-commission/contact_en" class="ecl-link ecl-link--standalone ecl-link--inverted ecl-site-footer__link"><spring:message code="label.ContactEC" /></a></li>
						<li class="ecl-site-footer__list-item"><a href="https://european-union.europa.eu/contact-eu/social-media-channels_en#/search?page=0&institutions=european_commission" class="ecl-link ecl-link--standalone ecl-link--inverted ecl-site-footer__link"><spring:message code="label.FollowECSocialMedia" /></a></li>
						<li class="ecl-site-footer__list-item"><a href="https://commission.europa.eu/resources-partners_en" class="ecl-link ecl-link--standalone ecl-link--inverted ecl-site-footer__link"><spring:message code="label.ResourcesForPartners" /></a></li>
						<li class="ecl-site-footer__list-item"><a href="https://commission.europa.eu/legal-notice/vulnerability-disclosure-policy_en" class="ecl-link ecl-link--standalone ecl-link--inverted ecl-site-footer__link"><spring:message code="label.ReportITVulnerability" /></a></li>
					</ul>
				</div>
			</div>
			<div class="col-md-4 ecl-site-footer__column" style="padding-left: 0px; padding-rightt: 0px;">
				<div class="ecl-site-footer__section ecl-site-footer__section--split-list">
					<ul class="ecl-site-footer__list">
						<li class="ecl-site-footer__list-item"><a href="https://commission.europa.eu/language-policy_en" class="ecl-link ecl-link--standalone ecl-link--inverted ecl-site-footer__link"><spring:message code="label.LanguagesOnWebsites" /></a></li>
						<li class="ecl-site-footer__list-item"><a href="https://commission.europa.eu/cookies_en" class="ecl-link ecl-link--standalone ecl-link--inverted ecl-site-footer__link"><spring:message code="label.Cookies" /></a></li>
						<li class="ecl-site-footer__list-item"><a href="https://commission.europa.eu/privacy-policy_en" class="ecl-link ecl-link--standalone ecl-link--inverted ecl-site-footer__link"><spring:message code="label.PrivacyPolicy" /></a></li>
						<li class="ecl-site-footer__list-item"><a href="https://commission.europa.eu/legal-notice_en" class="ecl-link ecl-link--standalone ecl-link--inverted ecl-site-footer__link"><spring:message code="label.LegalNotice" /></a></li>
					</ul>
				</div>
			</div>
		</div>
	</div>
</div>
 
 <script type="text/javascript">
 	$("#versionfootertarget").text(version);
 </script>

<%@ include file="includes2.jsp" %>


