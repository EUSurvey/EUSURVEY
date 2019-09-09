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
				<c:when test='${captcha == "internal"}'>
					<div class="internalcaptcha">
						 <img src="<c:url value="/captcha.html"/>"/><br />
						 
						 <c:choose>
							<c:when test="${form != null}">
								 ${form.getMessage("info.entertext")}
							</c:when>
							<c:otherwise>
								<spring:message code="info.entertext" />
							</c:otherwise>
						</c:choose>		
						 
						<br />					 
						<input id="j_captcha_response" type="text" class="required" autocomplete="off" name="j_captcha_response" />
						
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
								<div id="runner-captcha-error" class="validation-error-keep hideme">${form.getMessage("message.captchawrongnew")}</div>	
							</c:when>
							<c:otherwise>
								<div id="runner-captcha-error" class="validation-error-keep hideme"><spring:message code="message.captchawrongnew" /></div>	
							</c:otherwise>
						</c:choose>	
					
	  				</div>
				</c:when>
				<c:otherwise>
					<script src="https://www.google.com/recaptcha/api.js?hl=${form.language.code}" async defer></script>
	
					<div class="g-recaptcha unset" <c:if test="${responsive != null}">data-size="compact"</c:if> data-callback="hidecaptchaerror" data-sitekey="<c:out value="${captchaKey}"/>"></div>
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
							<div id="runner-captcha-error" class="validation-error-keep hideme">${form.getMessage("message.captchawrongnew")}</div>	
						</c:when>
						<c:otherwise>
							<div id="runner-captcha-error" class="validation-error-keep hideme"><spring:message code="message.captchawrongnew" /></div>	
						</c:otherwise>
					</c:choose>	
  			  		
					<script type="text/javascript">
						function doesConnectionExist() {
				  		    if ($(".g-recaptcha").first().find("div").length == 0)
				  		    {
				  		    	showRunnerWarning('${form.getMessage("error.InternetConnection")}<br /><span style="color: #f00">${form.getMessage("error.InternetConnectionCaptcha")}</span>');
				  		  	}
				  		}				  		
				  		$(function() {		
				  			setTimeout(function(){ doesConnectionExist(); }, 10000);
				  		});
					</script>
				
				</c:otherwise>
			</c:choose>	
						
	  		<c:if test="${wrongcaptcha != null && wrongcaptcha == true }">
	  			<script type="text/javascript">
					$(function() {	
						$("#runner-captcha-error").show();
	  					showRunnerError('${form.getMessage("error.captcha")}');
	  				});
	  			</script>	  		
	  		</c:if>
	  		
        </div>
	</c:otherwise>	
</c:choose>
