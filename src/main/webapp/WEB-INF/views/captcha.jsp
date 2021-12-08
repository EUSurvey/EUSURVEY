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
					const getCaptchaUrl = $.ajax({
			            type: "GET",
			            url: serverprefix + 'captchaImg',
			            beforeSend: function (xhr) {
			                xhr.withCredentials = true;
			                xhr.crossDomain = true;
			            },
			            success: function (data, textStatus, request) {
			                EuCaptchaToken = getCaptchaUrl.getResponseHeader("x-jwtString");  //"token");
			                const jsonData = data;
			                $("#captchaImg").attr("src", "data:image/png;base64," + jsonData.captchaImg);
			                $("#captchaImg").attr("captchaId", jsonData.captchaId);
			                $("#audioCaptcha").attr("src", "data:audio/wav;base64," + jsonData.audioCaptcha);
			                $('#captcha_token').val(EuCaptchaToken);
			                $('#captcha_id').val(jsonData.captchaId);
			                $('#captcha_useaudio').val(false);
			            }
			        });
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
				 const reloadCaptchaUrl = $.ajax({
			            type: "GET",
			            url: serverprefix + 'reloadCaptchaImg/' + $("#captchaImg").attr("captchaId"),
			            crossDomain: true,
			            beforeSend: function (xhr) {
			                xhr.setRequestHeader("Accept", "application/json");
			                xhr.setRequestHeader("Content-Type", "application/json");
			                xhr.setRequestHeader("x-jwtString", EuCaptchaToken);
			                xhr.withCredentials = true;
			            },
			            success: function (data) {
			                EuCaptchaToken = reloadCaptchaUrl.getResponseHeader("x-jwtString"); 
			                const jsonData = data;
			                $("#captchaImg").attr("src", "data:image/png;base64," + jsonData.captchaImg);
			                $("#captchaImg").attr("captchaId", jsonData.captchaId);
			                $("#audioCaptcha").attr("src", "data:audio/wav;base64," + jsonData.audioCaptcha);
			                $("#internal_captcha_response").val("");
			                $('#captcha_token').val(EuCaptchaToken);
			                $('#captcha_id').val(jsonData.captchaId);
			                $('#captcha_useaudio').val(false);
			                useAudio = false;
			            }
			        });
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
					return  $('#captcha_id').val() + "|" + EuCaptchaToken + "|" + useAudio;
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
					if (parent != null)
					{
						return parent.find("#internal_captcha_response").val();
					}					
				
					return $("#internal_captcha_response").val();
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
		
		<c:if test='${captcha == "eucaptcha"}'>
			let language = "Change Language ...";
			let useAudio = false;
			let EuCaptchaToken;
	
			let serverprefix = "<c:out value="${captchaServerPrefix}"/>";
	
			function onPlayAudio(){
			    useAudio = true;
			    $('#captcha_useaudio').val(true);
			}		
		
	    	createCaptcha();
	    	
	        $("#captchaReload").click(function(){
	      	  reloadCaptcha();
	      });
	    </c:if>
</script>
</c:if>	

