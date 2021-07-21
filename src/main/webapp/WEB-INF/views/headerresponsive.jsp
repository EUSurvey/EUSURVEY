<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="header">
	
	<div class="header-content-full" style="padding: 0px;">
		<div style="float: left; padding-top: 3px;">
			<a class="logolink" href="<c:url value="/home/welcome"/>"><span class="logolink" style="padding-top:3px; font-family: steinerregular;"><img src="${contextpath}/resources/images/logo_eusurvey_white-tiny.png" style="margin-top: -3px; max-width:24px;" alt="EUSurvey"/> EUSurvey</span></a>
		</div>
		
		<c:if test="${form != null && form.getSurvey() != null && isquizpage == null && mode != 'delphiStartPage' && mode != 'editcontribution' && form.survey.timeLimit.length() > 0  && form.survey.showCountdown}">
			<div style="float: left; padding-top: 5px; padding-left: 20px; font-size: 18px;">
				${form.getMessage("label.CountdownTimer")}			
				<span style="margin-left: 10px;" id="countdowntimer">${form.survey.timeLimit}</span>
			</div>							
		</c:if>
		
		<div style="float: right; margin-left: 10px;">
			<a class="messageicon" id="systemmessagebutton" style="display: none;"  onclick="$('#system-message-box').show();"><img style="vertical-align: middle; max-width:24px;" src="<c:url value="/resources/images/info24.png"/>" alt="system message" /></a>
			<a class="messageicon" id="warningmessagebutton" style="display: none;"  onclick="$('#generic-warning-box').show();"><img style="vertical-align: bottom; max-width:24px;" src="<c:url value="/resources/images/warning24.png"/>" alt="system message" /></a>
			<button onclick="$('#responsivemenu').slideToggle(500);" class="btn btn-primary" style="width: auto; margin: 0px;" type="button" id="dropdownMenu1"><span class="glyphicon glyphicon-menu-hamburger" aria-hidden="true"></span></button>
		</div>
		
		<c:if test="${form != null && form.getSurvey() != null && isquizpage == null && mode != 'delphiStartPage'}">
			
			<div style="float: right">
				<div class="dropdown" id="page-tabs">
				  <button class="btn btn-primary dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
				    ${form.getMessage("label.Step")}&nbsp;
				    <span class="caret"></span>
				  </button>
				  <ul id="sectionmenu" class="dropdown-menu dropdown-menu-right" style="margin: 0px; padding: 0px;" aria-labelledby="dropdownMenu1">
				    
				    <c:forEach var="page" items="${form.getPages()}" varStatus="rowCounter">
						<c:choose>
					 		<c:when test="${rowCounter.index == 0}">
								<li data-id="${page[0].id}" id="tab${rowCounter.index}" class="pagebuttonli">
							</c:when>
							<c:otherwise>
								<li data-id="${page[0].id}" id="tab${rowCounter.index}" class="pagebuttonli">
							</c:otherwise>
						</c:choose>
							<a class="pagebutton" style="text-decoration: none;" onclick="selectPage(${rowCounter.index});" data-id="${page[0].id}">
								<div style="white-space: normal; word-wrap: break-word">
								<c:choose>
									<c:when test="${page[0].getType() == 'Section' && page[0].tabTitle != null && page[0].tabTitle.length() > 0}">
										${page[0].tabTitle}
									</c:when>
									<c:when test="${page[0].getType() == 'Section'}">
										<esapi:encodeForHTML>${page[0].shortname}</esapi:encodeForHTML>
									</c:when>
									<c:otherwise>
										${form.getMessage("label.Start")}
									</c:otherwise>
								</c:choose>
								</div>												
							</a>
						</li>
					</c:forEach>				    
				    
				  </ul>
				</div>
			</div>	
		
		</c:if>
		
		<div style="clear: both"></div>		
	</div>
	
</div>

<div id="responsivemenu" style="display: none; position: fixed; text-align: left; right: 0px; top: 0px; z-index: 1000; max-height: 100%; overflow: auto;">		

	<div style="<c:if test="${ismobile == null}">float: right; border-bottom: 2px solid #777; border-left: 2px solid #777;</c:if> background-color: #EDF7FF; margin-right: 0px; padding: 10px; padding-top: 35px;">
		
		<c:if test="${form == null && page != 'helpparticipants'}">		
			<b><spring:message code="label.Languages" /></b>
					
			<div style="margin-bottom: 10px; margin-top: 10px;">	
				<select style="margin-left: 15px; padding: 5px;" onchange="if (window.location.href.indexOf('?') > 0) { window.location = window.location.href.substring(0, window.location.href.indexOf('?')) + $(this).val(); } else {window.location = window.location + $(this).val();}">	
					<option value=""><spring:message code="label.Choose" /></option>
					<option value="?language=bg">Български</option>
		 			<option value="?language=cs">Čeština</option> 
		 			<option value="?language=da">Dansk</option> 
		 			<option value="?language=de">Deutsch</option> 
		 			<option value="?language=et">Eesti keel</option> 
		 			<option value="?language=el">Ελληνικά</option>
		 			<option value="?language=en">English</option> 
		 			<option value="?language=es">Español</option> 
		 			<option value="?language=fr">Français</option>
		 			<option value="?language=ga">Gaeilge</option>
		 			<option value="?language=hr">Hrvatski jezik</option> 
		 			<option value="?language=it">Italiano</option>
		 			<option value="?language=lv">Latviešu valoda</option> 
		 			<option value="?language=lt">Lietuvių kalba</option> 
		 			<option value="?language=hu">Magyar</option>
		 			<option value="?language=mt">Malti</option>
		 			<option value="?language=nl">Nederlands</option> 
		 			<option value="?language=pl">Polski</option>; 
		 			<option value="?language=pt">Português</option> 
		 			<option value="?language=ro">Română</option>
		 			<option value="?language=sk">Slovenčina</option> 
		 			<option value="?language=sl">Slovenščina</option> 
		 			<option value="?language=fi">Suomi</option> 
		 			<option value="?language=sv">Svenska</option>		
	 			</select> 			
	 		</div>	 
		</c:if>		
		
		<c:if test="${form != null && isthankspage == null && escapemode == null && isquizresultpage == null}">
		
			<div style="border-bottom: 1px solid #777; padding-bottom: 10px; margin-bottom: 10px; margin-left: -10px; margin-right: -10px; padding-left: 10px; padding-right: 10px;">
				<!-- Languages -->	
				<c:if test="${form.getLanguages().size() != 0}">
					<b>${form.getMessage("label.Languages")}</b>
					<div style="margin-bottom: 10px; margin-top: 10px;">	
						<select style="margin-left: 15px; padding: 5px;" id="langSelectorRunner" name="langSelectorRunner" onchange="changeLanguageSelectOption('${mode}')">	
						<c:forEach var="lang" items="${form.getLanguagesAlphabetical()}">
							<c:choose>
								<c:when test="${lang.value.code == form.language.code}">
									<option value="<esapi:encodeForHTML>${lang.value.code}</esapi:encodeForHTML>" selected="selected"><esapi:encodeForHTML>[${lang.value.code}] ${lang.value.name}</esapi:encodeForHTML></option>
								</c:when>
								<c:otherwise>
									<option value="<esapi:encodeForHTML>${lang.value.code}</esapi:encodeForHTML>"><esapi:encodeForHTML>[${lang.value.code}] ${lang.value.name}</esapi:encodeForHTML></option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
						</select>
					</div>					
				</c:if>
				
				<!-- Useful links -->					
				<c:if test="${form.survey.getUsefulLinks().size() != 0}">			
					<b>${form.getMessage("label.UsefulLinks")}</b>
					<c:forEach var="link" items="${form.survey.getAdvancedUsefulLinks()}">
						<div style="padding: 5px;">
							<a class="link visiblelink" style="padding-bottom: 10px" target="_blank" rel="noopener noreferrer" href="<esapi:encodeForHTMLAttribute>${link.value}</esapi:encodeForHTMLAttribute>">${link.key}</a>
						</div>		
					</c:forEach>								
				</c:if>
				
				<!-- Background documents -->			
				<c:if test="${form.survey.getBackgroundDocuments().size() != 0}">
					<div style="margin-top: 10px">
						<b>${form.getMessage("label.BackgroundDocuments")}</b>
						<c:forEach var="link" items="${form.survey.getBackgroundDocumentsAlphabetical()}">
							<div style="padding: 5px;">
								<a class="link visiblelink" style="padding-bottom: 10px" target="_blank" href="<esapi:encodeForHTMLAttribute>${link.value}</esapi:encodeForHTMLAttribute>">${link.key}</a>
							</div>	
						</c:forEach>
					</div>								
				</c:if>
				
				<!-- contact -->		
				<c:if test="${form.survey.contact != null && escapemode == null}">
					<div style="margin-top: 10px">
						<b>${form.getMessage("label.Contact")}</b>
						<div style="margin-top: 5px">						
							<c:choose>
								<c:when test="${form.survey.contact.startsWith('form:')}">
									<a target="_blank" class="link visibleLink" data-toggle="tooltip" title="${form.getMessage("info.ContactForm")}" aria-label="${form.getMessage("info.ContactForm")}" href="${contextpath}/runner/contactform/${form.survey.shortname}">${form.getMessage("label.ContactForm")}</a>
								</c:when>
								<c:when test="${form.survey.contact.contains('@')}">
									<i class="icon icon-envelope" style="vertical-align: middle"></i>
									<a class="link visiblelink" style="padding-bottom: 10px" href="mailto:<esapi:encodeForHTMLAttribute>${form.survey.contact}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML>${form.survey.contact}</esapi:encodeForHTML></a>
								</c:when>
								<c:otherwise>
									<i class="icon icon-globe" style="vertical-align: middle"></i>
									<a target="_blank" class="link visiblelink" style="padding-bottom: 10px" href="<esapi:encodeForHTMLAttribute>${form.survey.fixedContact}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML>${form.survey.fixedContactLabel}</esapi:encodeForHTML></a>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</c:if>
				
				<c:if test="${form.survey.isDelphi}">
					<div style="margin-top: 10px">
						<b>${form.getMessage("label.Info")}</b>
						<div style="margin-top: 5px">
							<a target="_blank" class="link visibleLink" data-toggle="tooltip" title="${form.getMessage("label.Delphi")}" aria-label="${form.getMessage("label.Delphi")}" href="${contextpath}/home/delphi/runner">${form.getMessage("label.Delphi")}</a>
						</div>
					</div>
					<hr style="margin-top: 15px;" />
				</c:if>
				
				<c:if test="${escapemode == null && !form.survey.isQuiz}">
					<!-- pdf download -->	
					<div style="padding-left: 10px; margin-top: 10px;">
						<button data-toggle="tooltip" title="${form.getMessage("label.DownloadEmptyPDFversion")}" aria-label="${form.getMessage("label.DownloadEmptyPDFversion")}" id="download-survey-pdf-link" class="btn btn-default" href="#" onclick="downloadSurveyPDF('${form.survey.id}','${form.language.code}','${uniqueCode}'); return false;">${form.getMessage("label.DownloadPDFversion")}</button>
						<span id="download-survey-pdf-dialog-running" style="display: none">${form.getMessage("info.FileCreation")}</span>
						<div id="download-survey-pdf-dialog-ready" style="display: none;">${form.getMessage("info.FileCreated")}</div>
						<div id="download-survey-pdf-dialog-spinner" style="display: none; padding-left: 5px;"><img src="${contextpath}/resources/images/ajax-loader.gif" /></div>
						<a style="display: none; white-space: nowrap; overflow-x: visible; text-decoration: none; padding-bottom: 6px; padding-left: 12px; padding-right: 12px; padding-top: 6px;" id="download-survey-pdf-dialog-result" target="_blank" class="btn btn-default" href="<c:url value="/pdf/survey/${form.survey.id}?lang=${form.language.code}&unique=${uniqueCode}"/>">${form.getMessage("label.Download")}</a>
						<div id="download-survey-pdf-dialog-error" style="display: none">${form.getMessage("error.OperationFailed")}</div>
					</div>
				</c:if>
						
			</div>			
		</c:if>
		
		<c:if test="${escapemode != null}">
			<!-- Languages -->	
			<c:if test="${form.getLanguages().size() != 0}">
				<b>${form.getMessage("label.Languages")}</b>
				<div style="margin-bottom: 10px; margin-top: 10px;">	
					<select style="margin-left: 15px; padding: 5px;" onchange="if (window.location.href.indexOf('?') > 0) { window.location = window.location.href.substring(0, window.location.href.indexOf('?')) + $(this).val(); } else {window.location = window.location + $(this).val();}">	
					<c:forEach var="lang" items="${form.getLanguagesAlphabetical()}">
						<c:choose>
							<c:when test="${lang.value.code == form.language.code}">
								<option value="?surveylanguage=<esapi:encodeForHTML>${lang.value.code}</esapi:encodeForHTML>" selected="selected"><esapi:encodeForHTML>[${lang.value.code}] ${lang.value.name}</esapi:encodeForHTML></option>
							</c:when>
							<c:otherwise>
								<option value="?surveylanguage=<esapi:encodeForHTML>${lang.value.code}</esapi:encodeForHTML>"><esapi:encodeForHTML>[${lang.value.code}] ${lang.value.name}</esapi:encodeForHTML></option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
					</select>
				</div>					
			</c:if>
		</c:if>
		
		<c:choose>
			<c:when test="${!enablepublicsurveys}">						
			</c:when>
			<c:when test="${form != null && form.getResources() != null && runnermode == true}">
				<div style="padding: 5px"><a class="link visiblelink" href="<c:url value="/home/publicsurveys"/>">${form.getMessage("header.AllPublicSurveys")}</a></div>
			</c:when>
			<c:otherwise>
				<div style="padding: 5px"><a class="link visiblelink" href="<c:url value="/home/publicsurveys"/>"><spring:message code="header.AllPublicSurveys" /></a></div>
			</c:otherwise>
		</c:choose>	
	
		<c:choose>
			<c:when test="${form != null && form.getResources() != null}">
				<div style="padding: 5px"><a class="link visiblelink" href="<c:url value="/home/about"/>">${form.getMessage("label.About")}</a></div>
				<div style="padding: 5px"><a class="link visiblelink" href="<c:url value="/home/documentation"/>">${form.getMessage("label.Support")}</a></div>
				<div style="padding: 5px"><a class="link visiblelink" href="<c:url value="/home/download"/>">${form.getMessage("label.Download")}</a></div>
			</c:when>
			<c:otherwise>
				<div style="padding: 5px"><a class="link visiblelink" href="<c:url value="/home/about"/>"><spring:message code="label.About" /></a></div>
				<div style="padding: 5px"><a class="link visiblelink" href="<c:url value="/home/documentation"/>"><spring:message code="label.Support" /></a></div>
				<div style="padding: 5px"><a class="link visiblelink" href="<c:url value="/home/download"/>"><spring:message code="label.Download" /></a></div>
			</c:otherwise>
		</c:choose>		
	</div>
</div>

	<div class="modal" id="show-wait-image" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
		<div class="modal-body" style="text-align: center; padding-top:30px;;">
			<img src="${contextpath}/resources/images/ajax-loader.gif" />
		</div>
		</div>
		</div>
	</div>
	
	<script type="text/javascript">
		$(document).ready(function(){
			$("a.logolink").css("font-family","Helvetica");
			$("a.logolink").css("font-family","steinerregular");
		});
	</script>
	
	<%@ include file="system.jsp" %>
