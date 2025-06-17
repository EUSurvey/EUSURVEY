<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>

	<link href="${contextpath}/resources/css/yellowfocus.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css"></link>
	
	<div class="fullpageform">	
	
		<c:if test="${responsive == null}">
	
			<div class="right-area" style="z-index: 1; position: relative; float: right;">
						
				<c:if test="${form.survey.logo != null && form.survey.logoInInfo}">
					<img style="max-width: 100%" src="<c:url value="/files/${form.survey.uniqueId}/${form.survey.logo.uid}" />" alt="${form.survey.logoText}" />
					<hr style="margin-top: 15px;" />
				</c:if>			
				
				<c:if test="${form.getLanguages().size() != 0}">		
					<label for="langSelectorRunner">
						<div class="linkstitle" style="margin-bottom: 5px;">${form.getMessage("label.Languages")}</div>	
					</label>
					<select id="langSelectorRunner" name="langSelectorRunner" oninput="changeLanguageSelectOption('${mode}')">
					<c:forEach var="lang" items="${form.getLanguagesAlphabetical()}">
						<c:choose>
							<c:when test="${lang.value.code == form.language.code}">
								<option value="<esapi:encodeForHTML>${lang.value.code}</esapi:encodeForHTML>" selected="selected"><esapi:encodeForHTML>${lang.value.name}</esapi:encodeForHTML></option>
							</c:when>
							<c:otherwise>
								<option value="<esapi:encodeForHTML>${lang.value.code}</esapi:encodeForHTML>"><esapi:encodeForHTML>${lang.value.name}</esapi:encodeForHTML></option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
					</select>							
					<hr style="margin-top: 15px;" />	
				</c:if>			
						
				
				<div id="contact-and-pdf" style="word-wrap: break-word;">
				
					<c:if test="${form.survey.contact != null}">
						<div class="linkstitle" style="margin-bottom: 5px;">${form.getMessage("label.Contact")}</div>
						
						<c:choose>
							<c:when test="${form.survey.contact.startsWith('form:')}">
								<a target="_blank" class="link visibleLink" data-toggle="tooltip" title="${form.getMessage("info.ContactForm")}" href="${contextpath}/runner/contactform/${form.survey.shortname}" aria-label="${form.getMessage("label.ContactForm")} - ${form.getMessage("label.OpensInNewWindow")}">${form.getMessage("label.ContactForm")}</a>
							</c:when>
							<c:when test="${form.survey.contact.contains('@')}">
								<i class="icon icon-envelope" style="vertical-align: middle"></i>
								<a class="link" href="mailto:<esapi:encodeForHTMLAttribute>${form.survey.contact}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML>${form.survey.contact}</esapi:encodeForHTML></a>
							</c:when>
							<c:otherwise>
								<i class="icon icon-globe" style="vertical-align: middle"></i>
								<a target="_blank" class="link visiblelink" href="<esapi:encodeForHTMLAttribute>${form.survey.fixedContact}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML>${form.survey.fixedContactLabel}</esapi:encodeForHTML></a>
							</c:otherwise>
						</c:choose>
					</c:if>
				</div>												
			</div>
		</c:if>
		
		<c:choose>
			<c:when test="${responsive == null}">	
				<div class="quizstartdiv">
			</c:when>
			<c:otherwise>
				<div>
			</c:otherwise>
		</c:choose>
			<c:if test="${form.survey.logo != null && !form.survey.logoInInfo}">
				<div style="max-width: 900px">
					<img src="<c:url value="/files/${form.survey.uniqueId}/${form.survey.logo.uid}" />" alt="${form.survey.logoText}" style="max-width: 900px;" />
				</div>
			</c:if>
		
			<h1 class="surveytitle">${form.survey.title}</h1><br />
			
			<div style="margin-bottom: 20px;">${form.survey.quizWelcomeMessage}</div>
			
			<c:if test="${form.survey.automaticPublishing && form.survey.start != null && form.survey.end != null}">
				<table style="margin-bottom: 10px">
					<tr>
						<td style="font-weight: bold; padding-right: 10px;">${form.getMessage("label.StartsOn")}</td>
						<td>
							${form.survey.startString}
						</td>					
					</tr>
					<tr>
						<td style="font-weight: bold; padding-right: 10px;">${form.getMessage("label.EndsOn")}</td>
						<td>
							${form.survey.endString}
						</td>
					</tr>
				</table>
			</c:if>
			
			<a class="btn btn-primary" href="?startQuiz=true&surveylanguage=${form.language.code}">${form.getMessage("label.Start")}</a>
		</div>
		
		<div style="clear: both"></div>
	</div>
	
	<script type="text/javascript">
		function changeLanguageSelectOption(mode) {
			window.location = "?surveylanguage=" + $('#langSelectorRunner').val();
		}
		
		function changeLanguageSelectHeader(mode, headerLang) {
			window.location = "?surveylanguage=" + headerLang;
		}
	</script>
