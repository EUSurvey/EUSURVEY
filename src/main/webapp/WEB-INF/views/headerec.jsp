<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

	<div class="lang-en" id="header" style="width: 1300px; margin-left: auto; margin-right: auto;">
		<div style="float: right">
			<p class="off-screen">Service tools</p>
			<ul class="reset-list" id="services">
				<c:choose>
					<c:when test="${form != null && form.getResources() != null}">
						<li><a href="https://ec.europa.eu/geninfo/query/search_en.html">${form.getMessage("label.Search")}</a></li>
			            <li><a href="https://ec.europa.eu/cookies/index_en.htm">${form.getMessage("label.Cookies")}</a></li>
			            <li><a href="https://ec.europa.eu/geninfo/legal_notices_en.htm">${form.getMessage("label.LegalNotice")}</a></li>
						<li><a href="<c:url value="/home/about"/>">${form.getMessage("label.Contact")}</a></li>
						<li><a href="<c:url value="/home/documentation"/>">${form.getMessage("label.Support")}</a></li>
						<li><a href="<c:url value="/home/download"/>">${form.getMessage("label.Download")}</a></li>
					</c:when>
					<c:otherwise>
						<li><a href="https://ec.europa.eu/geninfo/query/search_en.html"><spring:message code="label.Search" /></a></li>
			            <li><a href="https://ec.europa.eu/cookies/index_en.htm"><spring:message code="label.Cookies" /></a></li>
			            <li><a href="https://ec.europa.eu/geninfo/legal_notices_en.htm"><spring:message code="label.LegalNotice" /></a></li>
						<li><a href="<c:url value="/home/about"/>"><spring:message code="label.Contact" /></a></li>
						<li><a href="<c:url value="/home/documentation"/>"><spring:message code="label.Support" /></a></li>
						<li><a href="<c:url value="/home/download"/>"><spring:message code="label.Download" /></a></li>
					</c:otherwise>
				</c:choose>
				</ul>
		
			<p class="off-screen mob-title" id="language-selector-title">Language selector</p>
			<select id="language-selector" class="reset-list language-selector" onchange="changeLanguageSelector('${mode}','${draftid}');">
				<c:choose>
					<c:when test="${form.getLanguages().size() != 0}">		
						<c:forEach var="lang" items="${form.getLanguages()}">
							<option value="${lang.key}"><esapi:encodeForHTML>${lang.value.name}</esapi:encodeForHTML></option>
						</c:forEach>							
					</c:when>
					<c:otherwise>
						<option>${form.language.name}</option>
					</c:otherwise>
				</c:choose>
			</select>
		</div>
		<p class="banner-flag">
			<img alt="European Commission logo" id="banner-flag" src="${contextpath}/resources/images/logo_en.gif">
		</p>
		<p id="banner-title-text">European Commission<br />
			<span>Title of the site</span>
		</p>
		<span id="banner-image-right"></span><span class="title-en" id="banner-image-title"></span>
		<p class="off-screen">Accessibility tools</p>
		<ul class="reset-list">
			<li class="m-home">
				<a href="<c:url value="/home/welcome"/>"><span>Home</span></a>
			</li>
			<li class="m-menu">
				<a href="#menu" onclick="return components.mobile.menu(this);"><span>Menu</span></a>
			</li>
			<li class="m-hide">
				<a accesskey="1" href="#content">Go to content</a>
			</li>
		</ul>		
	</div>
	<div id="path">
		<p class="off-screen">Navigation path</p>
		<ul class="reset-list">
             <li class="first">
                 <a href="https://ec.europa.eu/index_en.htm">European Commission</a>
             </li>
             <li>EUSurvey</li>
		</ul>
	</div>

	<div class="modal" id="show-wait-image" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
		<div class="modal-body" style="text-align: center; padding-top:30px;;">
			<img alt="wait animation" src="${contextpath}/resources/images/ajax-loader.gif" />
		</div>
		</div>
		</div>
	</div>
	
	<%@ include file="system.jsp" %>