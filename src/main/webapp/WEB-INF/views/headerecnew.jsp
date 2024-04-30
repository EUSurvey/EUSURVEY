<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

	<div class="lang-en" id="header">
		<c:if test="${responsive != null && form != null && form.getSurvey() != null && isquizpage == null && mode != 'delphiStartPage' && mode != 'editcontribution' && form.survey.timeLimit.length() > 0  && form.survey.showCountdown}">
			<div style="position: fixed; right: 10px; top: 10px; font-size: 18px; z-index: 1000; background-color: #fff; padding: 5px;">
				${form.getMessage("label.CountdownTimer")}			
				<span style="margin-left: 10px;" id="countdowntimer">${form.survey.timeLimit}</span>
			</div>							
		</c:if>	
	
		<div style="float: left">		
			<a href="https://ec.europa.eu" title="Home - European Commission">
			  <img style="width: 150px; margin-left: 20px;" alt="European Commission logo" id="banner-flag" src="${contextpath}/resources/images/logo.svg">
			</a>
		</div>
		
		<c:if test="${form.getLanguages().size() != 0}">
			<div class="languages">	
				<c:choose>
					<c:when test="${form.getLanguagesAlphabetical().size() > 1}">
						<c:forEach var="lang" items="${form.getLanguagesAlphabetical()}">
							<c:if test="${lang.value.code == form.language.code}">
								<c:if test="${ismobile != null}">
									<div style="padding-top: 45px">
								</c:if>								
							
								<a href="#" onclick="showModalDialog($('#choseRunnerLanguageDialog'), this)">${lang.value.name}
									<span class="icon"></span>
									<span class="langcode">${lang.value.code}</span>
								</a>
								
								<c:if test="${ismobile != null}">
									</div>
								</c:if>	
							</c:if>
						</c:forEach>
					</c:when>
					<c:otherwise>
									
					</c:otherwise>
				</c:choose>
			</div>	
		</c:if>	
	</div>
	<div id="path" style="margin-bottom: 200px;">
		<div id="pathinner">
			<ol>
				<li><a href="http://ec.europa.eu/index_en.htm">European Commission</a></li>
	        </ol> <br />
	        
	        <div class="pathtitle">
	        	EUSurvey
	        </div>
        </div>
	</div>
	<div style="margin-bottom: 130px;"></div>

	<div class="modal" id="show-wait-image" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
		<div class="modal-body" style="text-align: center; padding-top:30px;;">
			<img alt="wait animation" src="${contextpath}/resources/images/ajax-loader.gif" />
		</div>
		</div>
		</div>
	</div>
	
	<div id="choseRunnerLanguageDialog" role="dialog">
		<div class="backgroundhider"></div>
		<div id="choseRunnerLanguageDialogInner">		
		
			<div style="text-align: right; margin-right: 20px;">
				<a href="" class="closedialog" onclick="hideModalDialog($('#choseRunnerLanguageDialog'))">${form.getMessage("label.Close")}</a>
			</div>
			
			<div class="dialogtitle">
				<span class="icon"></span> ${form.getMessage("label.SelectYourLanguage")}
			</div>
			
			<c:forEach var="lang" items="${form.getLanguagesAlphabetical()}">
				<c:choose>
					<c:when test="${lang.value.code == form.language.code}">
						<div tabindex="0" class="selectedlang">
							<div class="checkicon"></div>
							${lang.value.name}
						</div>
					</c:when>
					<c:otherwise>
						<a href="javascript:;" onclick="changeLanguageSelectHeader('${mode}', '<esapi:encodeForHTML>${lang.value.code}</esapi:encodeForHTML>')" class='langlink'>${lang.value.name}</a>
					</c:otherwise>
				</c:choose>
			</c:forEach>
			
		</div>
	</div>
	
	<%@ include file="system.jsp" %>