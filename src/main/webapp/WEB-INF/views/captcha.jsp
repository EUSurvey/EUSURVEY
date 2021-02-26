<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ include file="captchainternal.jsp" %>

<c:if test='${captcha != null && captcha != "off"}'>
			  		
	<script type="text/javascript">		  			
		function hidecaptchaerror() {
			$('#runner-captcha-error').hide();
			$('#runner-captcha-empty-error').hide();
			$(".g-recaptcha.unset").removeClass("unset");
		}
		
		function createCaptcha() {
			<c:choose>
				<c:when test='${captcha == "eucaptcha"}'>
					
				</c:when>
				<c:when test='${captcha == "internal"}'>
				
				</c:when>
				<c:otherwise>
					grecaptcha.render("recaptcha", {sitekey: "<c:out value="${captchaKey}"/>", theme: "light"});
				</c:otherwise>
			</c:choose>
		}
		
		function reloadCaptcha()
		{
			<c:choose>
				<c:when test='${captcha == "eucaptcha"}'>
				
				</c:when>
				<c:when test='${captcha == "internal"}'>
					$(".internalcaptcha").find("img").each(function(){
						var oldSrc = $(this).attr("src");
						oldSrc = oldSrc.slice(0, oldSrc.indexOf("?"));
						let newSrc = oldSrc + "?" + new Date().getTime().toString();
						$(this).attr("src", newSrc);
					});
					$("#internal_captcha_response").val("");
				</c:when>
				<c:otherwise>
					grecaptcha.reset();
				</c:otherwise>
			</c:choose>
		}
		
		function getChallenge(parent)
		{
			<c:choose>
				<c:when test='${captcha == "eucaptcha"}'>
					return "";
				</c:when>
				<c:when test='${captcha == "internal"}'>
					return "";
				</c:when>
				<c:otherwise>
					if (parent != null)
					{
						return parent.find("#recaptcha_challenge_field").val();
					}
				
					return $("#recaptcha_challenge_field").val();
				</c:otherwise>
			</c:choose>
		}
		
		function getResponse(parent)
		{
			<c:choose>
				<c:when test='${captcha == "eucaptcha"}'>
				
				</c:when>
				<c:when test='${captcha == "internal"}'>
					if (parent != null)
					{
						return parent.find("#internal_captcha_response").val();
					}					
				
					return $("#internal_captcha_response").val();
				</c:when>
				<c:otherwise>
					return grecaptcha.getResponse();
				</c:otherwise>
			</c:choose>
		}
</script>
</c:if>	

