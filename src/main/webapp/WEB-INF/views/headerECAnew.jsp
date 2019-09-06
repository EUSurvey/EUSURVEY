<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="esapi"
	uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<div id="pathheader">
	<div class="lang-en" id="header">
		<div style="float: left">
			<a href="http://www.eca.europa.eu"
				title="Home - European court of auditors"> <img id="logoImg"
				style="height: 85px;margin-left: 15px;max-width: 85px;max-height: 85px;margin-top: 3px;"
				alt="European court of auditors logo" id="banner-flag"
				src="${contextpath}/resources/images/logoeca.svg">
			</a>
		</div>
		<div id="logo_text">
			<div class="logo_title">EUROPEAN COURT OF AUDITORS</div>
			<br>
			<div class="logo_slogan">Guardians of the EU finances</div>
		</div>
	</div>
</div>
<div id="path">
	<div id="pathtop">
		<div class="pathinner top">
			<ol>
				<a class="textInHeader2" href="https://www.eca.europa.eu">European
					Court of Auditors</a>
			</ol>
		</div>
	</div>
	<div id="pathbottom">
		<div class="pathinner bottom">
			<div class="pathtitle textInHeader3">Guardians of the EU
				finances</div>
		</div>
	</div>
</div>
<div style="margin-bottom: 130px;"></div>

<div class="modal" id="show-wait-image" data-backdrop="static">
	<div class="modal-dialog modal-sm">
		<div class="modal-content">
			<div class="modal-body"
				style="text-align: center; padding-top: 30px;">
				<img src="${contextpath}/resources/images/ajax-loader.gif" />
			</div>
		</div>
	</div>
</div>

<div id="choseRunnerLanguageDialog">
	<div class="backgroundhider"></div>
	<div id="choseRunnerLanguageDialogInner">

		<div style="text-align: right; margin-right: 20px;">
			<a class="closedialog"
				onclick="$('#choseRunnerLanguageDialog').hide()">${form.getMessage("label.Close")}</a>
		</div>

		<div class="dialogtitle">
			<span class="icon"></span>
			${form.getMessage("label.SelectYourLanguage")}
		</div>

		<c:forEach var="lang" items="${form.getLanguagesAlphabetical()}">
			<c:choose>
				<c:when test="${lang.value.code == form.language.code}">
					<div class="selectedlang">
						<div class="checkicon"></div>
						${lang.value.name}
					</div>
				</c:when>
				<c:otherwise>
					<a
						onclick="changeLanguageSelectHeader('${mode}', '<esapi:encodeForHTML>${lang.value.code}</esapi:encodeForHTML>')"
						class='langlink'>${lang.value.name}</a>
				</c:otherwise>
			</c:choose>
		</c:forEach>

	</div>
</div>

<%@ include file="system.jsp"%>