<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<c:choose>
	<c:when test="${responsive != null}">
		<%@ include file="headerresponsive.jsp" %>
	</c:when>
	<c:otherwise>

		<div class="header">	
			<div class="header-content-full">
				<div style="float: left; line-height: 20px;">
					<c:choose>
						<c:when test="${USER != null}">
							<a class="logolink" href="<c:url value="/dashboard"/>" style="float: left; padding-top:3px;"><img src="${contextpath}/resources/images/logo_Eusurvey-small-white.png" style="margin-top: -3px;" alt="EUSurvey"/></a>&#160;&#160;&#160;&#160;
						</c:when>
						<c:otherwise>
							<a class="logolink" href="<c:url value="/home/welcome"/>" style="float: left; padding-top:3px;"><img src="${contextpath}/resources/images/logo_Eusurvey-small-white.png" style="margin-top: -3px;" alt="EUSurvey"/></a>&#160;&#160;&#160;&#160;
						</c:otherwise>
					</c:choose>
				
					<c:choose>
						<c:when test="${!enablepublicsurveys}">
						
						</c:when>
						<c:when test="${form != null && form.getResources() != null && runnermode == true}">
							<a href="<c:url value="/home/publicsurveys"/>">${form.getMessage("header.AllPublicSurveys")}</a>
						</c:when>
						<c:otherwise>
							<a href="<c:url value="/home/publicsurveys"/>"><spring:message code="header.AllPublicSurveys" /></a>
						</c:otherwise>
					</c:choose>		
					&#160;&#160;
				</div>
					
				<div style="float: right; line-height: 22px;">		
				
					<span tabindex="0" id="messages-button" onclick="$('#messages-log-div').show()" onfocus="$('#loginBtnFromHeader').focus()">
						<!-- ko if: messages().length > 0 || systemMessages.length > 0 -->
						<span class="glyphicon glyphicon-bell" style="font-size: 17px;"></span>
						<span class="badge" style="background-color: #e90000; margin-left: -10px; margin-top: -3px; z-index: 100;" data-bind="visible: totalMessages() > 0, text: totalMessages"></span>
						<!-- /ko -->
					</span>	
					
					<c:choose>
						<c:when test="${form != null && form.getResources() != null && runnermode == true}">
							<c:choose>
								<c:when test="${passwordauthenticated != null }">
									<a id="loginBtnFromHeader" href="<c:url value="/auth/login"/>">${form.getMessage("label.DoLogin")}</a> |
								</c:when>
								<c:when test='${pageContext.request.getParameter("pw") != null || pw != null}'>
									<a id="loginBtnFromHeader" href="<c:url value="/auth/login"/>">${form.getMessage("label.DoLogin")}</a> |
								</c:when>
								<c:when test="${USER != null}">
									<span style="font-size: 13px; font-weight: normal;">${form.getMessage("label.Hello")}<b>&nbsp;<esapi:encodeForHTML>${USER.givenNameOrLogin}</esapi:encodeForHTML>&nbsp;<esapi:encodeForHTML>${fn:toUpperCase(USER.surName)}</esapi:encodeForHTML></b>&#160;&#160;(<a href="#" id="logoutBtnFromHeader"  onclick="logout()" class="visiblelink">${form.getMessage("label.logout")}</a>)</span>&#160;|
								</c:when>
								<c:otherwise>
									<a id="loginBtnFromHeader" href="<c:url value="/auth/login"/>">${form.getMessage("label.DoLogin")}</a> |
								</c:otherwise>
							</c:choose>
					
							<div id="dropDownHelp" class="dropdown" style="display: inline-block;">
								<a href="javascript:;" class="dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
									${form.getMessage("label.Help")}&nbsp;
									<span class="caret"></span>
		  						</a>
								<ul id="dropDownHelpHeader" class="dropdown-menu" aria-labelledby="dropdownMenu1">
									<li><a id="linkHelpAbout" href="<c:url value="/home/about"/>">${form.getMessage("label.About" )}</a></li>
									<li><a id="linkHelpSupport" href="<c:url value="/home/documentation"/>">${form.getMessage("label.Support" )}</a></li>
									<li><a id="linkHelpDownload" href="<c:url value="/home/download"/>">${form.getMessage("label.Download" )}</a></li>
								</ul>
							</div>
		 					
							<c:if test='${form.getLanguages().size() != 0 && (readonlyMode == null || readonlyMode == false) && mode != "editcontribution"}'>	
							
								|
		
								<div class="dropdown" >
									<a href="javascript:;" class="dropdown-toggle" id="dropdownLang" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" >
										${form.getMessage("label.Language")}&nbsp;
										<span class="caret"></span>
			  						</a>	
			  						
			  						<ul class="dropdown-menu dropdown-menu-right" id="dropdownLangElements" aria-labelledby="dropdownLang">	 						
			  							<c:choose>	  							
											<c:when test="${isthankspage != null || isdraftinfopage != null}">
												<li>
													<button type="button"><esapi:encodeForHTML>[${form.language.code}] ${form.language.name}</esapi:encodeForHTML></button>
												</li>
											</c:when>
											<c:otherwise>																				
												<c:forEach var="lang" items="${form.getLanguagesAlphabetical()}">	
												<li> 
													<c:choose>						  									
														<c:when test="${escapemode != null}">
															<a href="?surveylanguage=${lang.value.code}"><esapi:encodeForHTML>${lang.value.name}</esapi:encodeForHTML></a>
														</c:when>
														<c:when test="${publication != null}">
															<a href="?language=${lang.value.code}&surveylanguage=${lang.value.code}"><esapi:encodeForHTML>${lang.value.name}</esapi:encodeForHTML></a>
														</c:when>
														<c:when test="${lang.value.code == form.language.code}">
															<button type="button" class="unstyledbuttonlanguage skipScriptAnchor" onclick="$('.dropdown.open .dropdown-toggle').dropdown('toggle')"><esapi:encodeForHTML>${lang.value.name}</esapi:encodeForHTML></button>
			  											</c:when>
														<c:otherwise>
															<button type="button" class="unstyledbuttonlanguage skipScriptAnchor" onclick="changeLanguageSelectHeader('${mode}','<esapi:encodeForHTML>${lang.value.code}</esapi:encodeForHTML>');"><esapi:encodeForHTML>${lang.value.name}</esapi:encodeForHTML></button>
			  											</c:otherwise>	
			  										</c:choose>	 
			  									</li> 									  									
												</c:forEach>
											</c:otherwise>					
										</c:choose>
						 			</ul>							
								</div>	
							</c:if>	
		
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${USER != null && runnermode == null}">
									<span style="font-size: 13px; font-weight: normal;"><spring:message code="label.Hello" /><b>&nbsp;<esapi:encodeForHTML>${USER.givenNameOrLogin}</esapi:encodeForHTML>&nbsp;<esapi:encodeForHTML>${fn:toUpperCase(USER.surName)}</esapi:encodeForHTML></b>&#160;&#160;(<a href="javascript:;" id="logoutBtnFromHeader" onclick="logout()"  class="visiblelink"><spring:message code="label.logout" /></a>)</span>&#160;|
								</c:when>
								<c:otherwise>
									<a id="loginBtnFromHeader" href="<c:url value="/auth/login"/>"><spring:message code="label.DoLogin"></spring:message></a> |
								</c:otherwise>
							</c:choose>
							
							<div id="dropDownHelp" class="dropdown" style="display: inline-block;">
								<button type="button" class="unstyledbutton dropdown-toggle" style="color: #fff; font-size: 13px;" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" >
									<spring:message code="label.Help" />&nbsp;
									<span class="caret"></span>
		  						</button>
								<ul id="dropDownHelpHeader" class="dropdown-menu dropdown-menu-header" aria-labelledby="dropdownMenu1">
									
										<li><a id="linkHelpAbout" href="<c:url value="/home/about"/>"><spring:message code="label.About" /></a></li>
										<li><a id="linkHelpSupport" href="<c:url value="/home/documentation"/>"><spring:message code="label.Support" /></a></li>
										<li><a id="linkHelpDownload" href="<c:url value="/home/download"/>"><spring:message code="label.Download" /></a></li>
									
								</ul>
							</div>
							
							 |
		
							<div class="dropdown">
								<button type="button" class="unstyledbutton dropdown-toggle" id="dropdownLang" style="color: #fff; font-size: 13px;" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" >
									<spring:message code="label.Language" />&nbsp;
									<span class="caret"></span>
		  						</button>
		  						<ul class="dropdown-menu dropdown-menu-right" id="dropdownLangElements" aria-labelledby="dropdownLang">				
						 			<li><a href="?language=bg">Български</a></li>
						 			<li><a href="?language=cs">Čeština</a></li>
						 			<li><a href="?language=da">Dansk</a></li>
						 			<li><a href="?language=de">Deutsch</a></li>
						 			<li><a href="?language=et">Eesti keel</a></li>
						 			<li><a href="?language=el">Ελληνικά</a></li>
						 			<li><a href="?language=en">English</a></li>
						 			<li><a href="?language=es">Español</a></li>
						 			<li><a href="?language=fr">Français</a></li>
						 			<li><a href="?language=ga">Gaeilge</a></li>
						 			<li><a href="?language=hr">Hrvatski jezik</a></li> 
						 			<li><a href="?language=it">Italiano</a></li>
						 			<li><a href="?language=lv">Latviešu valoda</a></li> 
						 			<li><a href="?language=lt">Lietuvių kalba</a></li>
						 			<li><a href="?language=hu">Magyar</a></li>
						 			<li><a href="?language=mt">Malti</a></li>
						 			<li><a href="?language=nl">Nederlands</a></li>
						 			<li><a href="?language=pl">Polski</a></li>
						 			<li><a href="?language=pt">Português</a></li>
						 			<li><a href="?language=ro">Română</a></li>
						 			<li><a href="?language=sk">Slovenčina</a></li>
						 			<li><a href="?language=sl">Slovenščina</a></li>
						 			<li><a href="?language=fi">Suomi</a></li>
						 			<li><a href="?language=sv">Svenska</a></li>
					 			</ul>
							</div>	
						</c:otherwise>
					</c:choose>
				
					<a class="messageicon" id="systemmessagebutton" style="display: none;"  onclick="$('#system-message-box').show();"><img style="max-width:24px;" src="<c:url value="/resources/images/info24.png"/>" alt="system message" /></a>
					<a class="messageicon" id="warningmessagebutton" style="display: none;"  onclick="$('#generic-warning-box').show();"><img style="max-width:24px;" src="<c:url value="/resources/images/warning24.png"/>" alt="system message" /></a>
				</div>
					
				<div style="clear: both"></div>
				
			</div>
				
			<c:if test="${serverEnv != null}">
				<c:if test="${fn:length(serverEnv)> 0}">
					<img src="${contextpath}/resources/images/ribbon_<c:out value="${serverEnv}"/>.svg" alt="environment ribbon ${serverEnv}" style="position:absolute; pointer-events: none;" width="80px"/>
				</c:if>
			</c:if>	
		</div>
		
		<div class="modal" id="show-wait-image" data-backdrop="static">
			<div class="modal-dialog modal-sm">
	    	<div class="modal-content">
			<div class="modal-body" style="text-align: center; padding-top:30px;">
				<img alt="wait animation" src="${contextpath}/resources/images/ajax-loader.gif" />
			</div>
			</div>
			</div>
		</div>
		
		<div class="modal" id="show-wait-image-delete-survey" data-backdrop="static">
			<div class="modal-dialog modal-sm">
	    	<div class="modal-content">
			<div class="modal-body" style="text-align: center; padding-top:30px;">
				<spring:message code="info.SurveyDeleted" /><br /><br />
				<img alt="wait animation" src="${contextpath}/resources/images/ajax-loader.gif" />
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
		
	</c:otherwise>
</c:choose>
			