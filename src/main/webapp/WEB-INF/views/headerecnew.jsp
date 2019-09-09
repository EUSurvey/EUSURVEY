<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

	<div class="visible-xs" style="background-color: #f5f5f5; position: absolute; left: 0; right: 0; margin-bottom: 20px; padding: 0">
		<div style="margin: 0; padding:0;">			
		  <a style="display: inline-block; margin: 0; width: 49%; color: #004494; font-weight: bold; padding: 12px; padding-left: 10px; padding-right: 10px; font-size: 14px" href="https://ec.europa.eu/commission/index_en">Commission and its priorities</a>
		  <a style="display: inline-block; width: 49%; background-color: #004494; color: #fff; font-weight: bold; padding: 12px; padding-left: 10px; padding-right: 10px; font-size: 14px" href="https://ec.europa.eu/info/index_en">Policies, information and services</a>
		</div>
	</div>
	
	<div class="hidden-xs" style="background-color: #f5f5f5; position: absolute; left: 0; right: 0; margin-bottom: 20px;">
		<div style="margin: 0; padding:0;">
			<ul class="site-switcher">	
		  		<li>
		  			<a style="display: inline-block; color: #004494;" href="https://ec.europa.eu/commission/index_en">Commission and its priorities</a>
		  		</li>
		  		<li>		  		
		  			<a style="display: inline-block; background-color: #004494; color: #fff;" href="https://ec.europa.eu/info/index_en">Policies, information and services</a>
				</li>
			</ul>
		</div>
	</div>

	<div class="lang-en" id="header">
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
								<a href="#" onclick="$('#choseRunnerLanguageDialog').show()">${lang.value.name}</a>
								<a href="#" onclick="$('#choseRunnerLanguageDialog').show()">
									<span class="icon"></span>
									<span class="langcode">${lang.value.code}</span>
								</a>
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
			<img src="${contextpath}/resources/images/ajax-loader.gif" />
		</div>
		</div>
		</div>
	</div>
	
	<div id="choseRunnerLanguageDialog">
		<div class="backgroundhider"></div>
		<div id="choseRunnerLanguageDialogInner">		
		
			<div style="text-align: right; margin-right: 20px;">
				<a href="" class="closedialog" onclick="$('#choseRunnerLanguageDialog').hide()">${form.getMessage("label.Close")}</a>
			</div>
			
			<div class="dialogtitle">
				<span class="icon"></span> ${form.getMessage("label.SelectYourLanguage")}
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
						<a onclick="changeLanguageSelectHeader('${mode}', '<esapi:encodeForHTML>${lang.value.code}</esapi:encodeForHTML>')" class='langlink'>${lang.value.name}</a>
					</c:otherwise>
				</c:choose>
			</c:forEach>
			
		</div>
	</div>
	
	<%@ include file="system.jsp" %>