<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<c:choose>
	<c:when test='${captcha == null || captcha == "off"}'>
		
	</c:when>	
	<c:otherwise>
		<div id="captchadiv" class="captcha" style="margin-left: 20px; margin-bottom: 10px; margin-top: 20px;">
			<c:choose>
				<c:when test="${form != null}">
					${form.getMessage("info.verifyhuman")}
				</c:when>
				<c:otherwise>
					<spring:message code="info.verifyhuman" />
				</c:otherwise>
			</c:choose>					
			<br />
			
			<c:choose>
				<c:when test='${captcha == "eucaptcha"}'>
					<div class="internalcaptcha">
					  <div style="margin-bottom: 10px;">
					    <img style="max-width: 100%; margin-bottom: 10px" alt="Captcha Loading" src="${contextpath}/resources/images/ajax-loader.gif" id="captchaImg" captchaId="">
					    <audio controls autostart="1" src="" id="audioCaptcha" onplay="onPlayAudio()"></audio>
					  </div>
					  
					  <input type="hidden" name="captcha_token" id="captcha_token" />
					  <input type="hidden" name="captcha_id" id="captcha_id" />
					  <input type="hidden" name="captcha_useaudio" id="captcha_useaudio" />
					  
					    <c:choose>
							<c:when test="${form != null}">
								<input type="text" id="internal_captcha_response" name="internal_captcha_response" autocomplete="off" placeholder="${form.getMessage("info.entertext")}" style="width: 260px">								 
						
								<a title='${form.getMessage("label.ReloadCaptcha")}' data-toggle="tooltip" href="javascript:;" class="btn btn-primary btn-sm" id="captchaReload"><span class="glyphicon glyphicon-refresh"></span></a>
						
							</c:when>
							<c:otherwise>
								<input type="text" id="internal_captcha_response" name="internal_captcha_response" autocomplete="off" placeholder="<spring:message code="info.entertext" />" style="width: 260px">								
							
								<a title='<spring:message code="label.ReloadCaptcha" />' href="javascript:;" class="btn btn-primary btn-sm" id="captchaReload"><span class="glyphicon glyphicon-refresh"></span></a>
							
							</c:otherwise>
						</c:choose>

						<c:if test="${captchaDynatraceSrc != null && captchaDynatraceSrc.length() > 0}">
							<script type="text/javascript" src="${captchaDynatraceSrc}" crossorigin="anonymous"></script>
						</c:if>
					</div>
				</c:when>
				<c:when test='${captcha == "internal"}'>
					<div class="internalcaptcha">
						 <img id="captchaImage" src="<c:url value="/captcha.html?1"/>"/><br />
						 
						 <c:choose>
							<c:when test="${form != null}">
								 ${form.getMessage("info.entertext")}
							</c:when>
							<c:otherwise>
								<spring:message code="info.entertext" />
							</c:otherwise>
						</c:choose>		
						 
						<br />					 
						<input id="internal_captcha_response" type="text" class="required" autocomplete="off" name="internal_captcha_response" />
					
	  				</div>
				</c:when>
				<c:otherwise>
					<script src="https://www.google.com/recaptcha/api.js?hl=${form.language.code}" async="async" defer="defer"></script>
	
					<div class="g-recaptcha unset" <c:if test="${responsive != null}">data-size="compact"</c:if> data-callback="hidecaptchaerror" data-sitekey="<c:out value="${captchaKey}"/>"></div>
				  			  		
					<script type="text/javascript">
						function doesConnectionExist() {
				  		    if ($(".g-recaptcha").first().find("div").length == 0)
				  		    {
				  		  		showError('${form.getMessage("error.InternetConnection")}<br /><span style="color: #f00">${form.getMessage("error.InternetConnectionCaptcha")}</span>');
				  		  	}
				  		}				  		
				  		$(function() {		
				  			setTimeout(function(){ doesConnectionExist(); }, 10000);
				  		});
					</script>
				
				</c:otherwise>
			</c:choose>	
			
				<c:choose>
						<c:when test="${form != null}">
							<div id="runner-captcha-empty-error" class="validation-error-keep hideme">${form.getMessage("validation.required")}</div>
						</c:when>
						<c:otherwise>
							<div id="runner-captcha-empty-error" class="validation-error-keep hideme"><spring:message code="validation.required" /></div>
						</c:otherwise>
					</c:choose>		
	  		
	  				<c:if test="${captchaerror != null}">
			        	<div class="validation-error">${captchaerror}</div>
			        </c:if>			
					
			   		<c:choose>
						<c:when test="${form != null}">
							<div id="runner-captcha-error" tabindex="0" class="validation-error-keep hideme">${form.getMessage("message.captchawrongnew")}</div>	
						</c:when>
						<c:otherwise>
							<div id="runner-captcha-error" tabindex="0" class="validation-error-keep hideme"><spring:message code="message.captchawrongnew" /></div>	
						</c:otherwise>
					</c:choose>	
						
	  		<c:if test="${wrongcaptcha != null && wrongcaptcha == true }">
	  			<script type="text/javascript">
					$(function() {	
						$("#runner-captcha-error").show().attr("aria-live", "polite").focus();
						$("#internal_captcha_response").attr("aria-invalid", "true").attr("aria-describedby", "runner-captcha-error");
		  			});
	  			</script>	  		
	  		</c:if>
	  		
        </div>
	</c:otherwise>	
</c:choose>
