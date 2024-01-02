<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<div style="width: 730px; max-width:100%; margin-left: auto; margin-right:auto;" id="divThanksInner" name="${uniqueCode}">

	<c:choose>
		<c:when test="${form.survey.isECF}">
			<div style="text-align: center; margin-top: 20px;">
			<!--  no text -->
		</c:when>
		<c:when test="${text != null}">
			${form.replacedMarkupConfirmationPage()}
		</c:when>
		<c:when test="${runnermode == true}">
			${form.getMessage("label.Thanks")}
		</c:when>
		<c:otherwise>
			<spring:message code="label.Thanks" />
		</c:otherwise>
	</c:choose>
	
	<c:if test="${redirect != null}">
		<br /><br />
		<c:choose>
			<c:when test="${runnermode == true}">
				${form.getMessage("info.redirect")}
			</c:when>
			<c:otherwise>
				<spring:message code="info.redirect" />
			</c:otherwise>
		</c:choose>
	</c:if>
	<c:if test="${isECF}">
		<div id="canvasContainer"> 
			<%@ include file="ecfGraph.jsp" %>
		</div>
	</c:if>
	<c:if test="${notificationemailtext != null}">
		<br /><br />
		${notificationemailtext}
	</c:if>
	
	<c:if test="${opcredirection != null}">
		<br /><br />
		<a class="btn btn-primary" href="${opcredirection}"><spring:message code="label.ConsultationPage" /></a>
	</c:if>	
		
	<br /><br />
	<div id="contribution-id-save-hint" style="color: #777; margin: 6px 0 0px 0;">
		<spring:message code="label.ContributionSavingHint" />: <esapi:encodeForHTML>${uniqueCode}</esapi:encodeForHTML>
		<a href="javascript:;" style="margin-left: 8px; text-decoration: none; display: inline-block;" id="copyIconButton" onclick="navigator.clipboard.writeText(uniqueCode);" data-toggle="tooltip" aria-label='${form.getMessage("label.CopyContributionID")}' title='${form.getMessage("label.CopyContributionID")}'>
			<i class="glyphicon glyphicon-copy copy-icon"></i>
		</a>
		<br />
		<spring:message code="label.ContributionSavingExplanation" />
	</div>
	
	<br />
	
	<c:if test="${form.survey.downloadContribution}">
		<c:choose>
			<c:when test="${responsive != null}">
				<a style="text-decoration: none" id="printButtonThanksInner" target="_blank" href="<c:url value="/printcontribution?code=${uniqueCode}"/>" class="btn btn-lg btn-primary">${form.getMessage("label.Print")}</a>
			</c:when>
			<c:when test="${runnermode == true}">
				<a id="printButtonThanksInner" target="_blank" href="<c:url value="/printcontribution?code=${uniqueCode}"/>" class="btn btn-primary">${form.getMessage("label.Print")}</a>
			</c:when>
			<c:otherwise>
				<a id="printButtonThanksInner" target="_blank" href="<c:url value="/printcontribution?code=${uniqueCode}"/>" class="btn btn-primary"><spring:message code="label.Print" /></a>
			</c:otherwise>	
		</c:choose>
		<c:choose>
			<c:when test="${responsive != null}">
				<a href="javascript:;" style="text-decoration: none" id="pdfDownloadButtonThanksInner" onclick="showExportDialogAndFocusEmail(this)" class="btn btn-lg btn-primary">${form.getMessage("label.GetPDF")}</a>
			</c:when>
			<c:when test="${runnermode == true}">
				<a href="javascript:;" id="pdfDownloadButtonThanksInner" onclick="showExportDialogAndFocusEmail(this)" class="btn btn-primary">${form.getMessage("label.GetPDF")}</a>
			</c:when>
			<c:otherwise>
				<a href="javascript:;" id="pdfDownloadButtonThanksInner" onclick="showExportDialogAndFocusEmail(this)" class="btn btn-primary"><spring:message code="label.GetPDF" /></a>
			</c:otherwise>	
		</c:choose>
	</c:if>

	<c:if test="${form.survey.changeContribution}">
		<c:choose>
			<c:when test="${responsive != null}">
				<a href="${contextpath}/editcontribution/${uniqueCode}" style="text-decoration: none" id="contributionEditInner" class="btn btn-lg btn-primary">${form.getMessage("label.Edit")}</a>
			</c:when>
			<c:when test="${runnermode == true}">
				<a href="${contextpath}/editcontribution/${uniqueCode}" class="btn btn-primary">${form.getMessage("label.Edit")}</a>
			</c:when>
			<c:otherwise>
				<a href="${contextpath}/editcontribution/${uniqueCode}" id="contributionEditInner" class="btn btn-primary"><spring:message code="label.Edit" /></a>
			</c:otherwise>
		</c:choose>
	</c:if>




	<c:if test="${asklogout != null}">
		<div id="ask-logout-div" style="margin-top: 30px;">
			${form.getMessage("question.logout")}<br /><br />	
			<a href="javascript:;" class="btn btn-primary btn-default" onclick="logout()">${form.getMessage("label.Yes")}</a>	
			<a href="javascript:;" class="btn btn-default" onclick="$('#ask-logout-div').hide()">${form.getMessage("label.No")}</a>		
		</div>
	</c:if>

</div>

<c:if test="${form.survey.isECF}">
	</div>
</c:if>

<div class="modal" id="ask-export-dialog" data-backdrop="static" role="dialog">	
	<div class="modal-dialog">
    <div class="modal-content">
	<div class="modal-header">
		<b><spring:message code="label.Info" /></b>
	</div>
	<div class="modal-body">
		<p>
			<c:choose>
				<c:when test="${runnermode == true}">
					${form.getMessage("question.EmailForPDF")}
				</c:when>
				<c:otherwise>
					<spring:message code="question.EmailForPDF" />
				</c:otherwise>	
			</c:choose>
		</p>
		
		<c:choose>
			<c:when test='${participantsemail != null && participantsemail.indexOf("@") > 0}'>
				<input type="text" maxlength="255" name="email" id="email" value="${participantsemail}" />
			</c:when>
			<c:otherwise>
				<input type="text" maxlength="255" name="email" id="email" />
			</c:otherwise>
		</c:choose>
		<span id="ask-export-dialog-error" class="validation-error hideme">
			<c:choose>
				<c:when test="${runnermode == true}">
					${form.getMessage("message.ProvideEmail")}
				</c:when>
				<c:otherwise>
					<spring:message code="message.ProvideEmail" />
				</c:otherwise>	
			</c:choose>
		</span>
		<c:if test="${!form.survey.captcha}">
			<div class="captcha" style="margin-left: 0px; margin-bottom: 20px; margin-top: 20px;">						
				<c:if test="${captchaBypass !=true}">
				<%@ include file="captcha.jsp" %>					
				</c:if>
	       	</div>
	       	<span id="ask-export-dialog-error-captcha" class="validation-error hideme">       		
	       		<c:if test="${captchaBypass !=true}">
	       		<c:choose>
					<c:when test="${runnermode == true}">
						${form.getMessage("message.captchawrongnew")}
					</c:when>
					<c:otherwise>
						<spring:message code="message.captchawrongnew" />
					</c:otherwise>	
				</c:choose>
	       		</c:if>
	       	</span>
	    </c:if>
	</div>
	<div class="modal-footer">
		<c:choose>
			<c:when test="${responsive != null}">
				<a href="javascript:;" style="text-decoration: none" class="btn btn-primary btn-lg" onclick="startExport()">${form.getMessage("label.OK")}</a>	
				<a href="javascript:;" style="text-decoration: none" class="btn btn-default btn-lg" onclick="hideModalDialog($('#ask-export-dialog'))">${form.getMessage("label.Cancel")}</a>		
			</c:when>
			<c:when test="${runnermode == true}">
				<a href="javascript:;" class="btn btn-primary" onclick="startExport()">${form.getMessage("label.OK")}</a>	
				<a href="javascript:;" class="btn btn-default" onclick="hideModalDialog($('#ask-export-dialog'))">${form.getMessage("label.Cancel")}</a>		
			</c:when>
			<c:otherwise>
				<a href="javascript:;" class="btn btn-primary" onclick="startExport()"><spring:message code="label.OK" /></a>	
				<a href="javascript:;" class="btn btn-default" onclick="hideModalDialog($('#ask-export-dialog'))"><spring:message code="label.Cancel" /></a>		
			</c:otherwise>	
		</c:choose>				
	</div>
	</div>
	</div>
</div>

<script type="text/javascript">
	var uniqueCode = "${uniqueCode}";
	var contextpath = "${contextpath}";
	var surveyShortname = "${surveyShortname}";

	$(function() {
		$('[data-toggle="tooltip"]').ApplyCustomTooltips();
	});

	function startExport()
	{
		$("#ask-export-dialog").find(".validation-error").hide();
		$("#ask-export-dialog").find(".validation-error-keep").hide();
		
		var mail = $("#email").val();
		if (mail.trim().length == 0 || !validateEmail(mail))
		{
			$("#ask-export-dialog-error").show();
			return;
		};	
				
		<c:choose>
			<c:when test="${!captchaBypass && !form.survey.captcha}">
				var challenge = getChallenge();
			    var uresponse = getResponse();
			    
			    if (uresponse.trim().length == 0)
			    {
			    	$("#runner-captcha-empty-error").show();
			    	return;
			    }

				var data = {email : mail, recaptcha_challenge_field : challenge, 'g-recaptcha-response' : uresponse};
				if ($('#captcha_token').length > 0) {
					data["captcha_token"] =  $('#captcha_token').val();
					data["captcha_id"] =  $('#captcha_id').val();
					data["captcha_useaudio"] =  $('#captcha_useaudio').val();
				}
			
				$.ajax({
					type:'GET',
					  url: "${contextpath}/runner/createanswerpdf/${uniqueCode}",
					  data: data,
					  cache: false,
					  success: function( data ) {
						  
						  if (data == "success") {
								$('#ask-export-dialog').modal('hide');
								showSuccess(message_PublicationExportSuccess2.replace('{0}', mail));
						  	} else if (data == "errorcaptcha") {
						  		$("#runner-captcha-error").show();
						  		reloadCaptcha();
							} else {
								showError(message_PublicationExportFailed);
								reloadCaptcha();
							};
					}
				});							
			</c:when>
			<c:otherwise>			
				$.ajax({				
					type:'GET',
					  url: "${contextpath}/runner/createanswerpdf/${uniqueCode}",
					  data: {email : mail, recaptcha_challenge_field : '', 'g-recaptcha-response' : ''},
					  cache: false,
					  success: function( data ) {
						  
						  if (data == "success") {
								$('#ask-export-dialog').modal('hide');
								showSuccess(message_PublicationExportSuccess2.replace('{0}', mail));
							} else {
								showError(message_PublicationExportFailed);
								reloadCaptcha();
							};
					}
				});							
			</c:otherwise>
		</c:choose>
	}
</script>
