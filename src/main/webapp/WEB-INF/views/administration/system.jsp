<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.ec.survey.model.administration.GlobalPrivilege" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Administration" /></title>	
	<%@ include file="../includes.jsp" %>	
	<style type="text/css">
		#ui-datepicker-div {
			z-index: 1052 !important;
		}
		
		#reportEmails td {
			padding-bottom: 10px;
		}
		
		#configure-trustindicator-dialog input[type=text] {
			width: 150px;
		}
	</style>
	
	<script type="text/javascript">
	
		var invalidNumberText = "<spring:message code='validation.invalidNumber' />";
		var invalidPositiveText = "<spring:message code='validation.invalidPositiveNumber' />";
	
		function checkCriticality()
		{
			setCriticalityImage();
			
			<c:forEach items="${message.types}" var="type">
				if (criticality == '${type.criticality}')
				{
					$("#time").val('${type.defaultTime}');
				}
			</c:forEach>
		}
		
		function setCriticalityImage()
		{
			$(".messageimage").hide();
			var criticality = $("#criticality").val();
			$("#img" + criticality).show();
		}
		
		function showConfiguration()
		{
			$('.validation-error').remove();
			$('#configure-message-dialog').modal('show');
		}
		
		function saveConfiguration()
		{
			var activate = $('#activetrue').is(":checked");
			if (activate)
			{				
				
				$('#configure-message-form').find(".validation-error").remove();
				
				if ($('#messagetext').val().trim().length == 0)
				{
					$('#messagetext').closest(".messagetextdiv").append("<div class='validation-error'>" + requiredText + "</div>");
					return;
				};
				
				var value = $('#time').val();
				if (isNaN(value) || !$.isNumeric(value) || !isFinite(value))
				{
					$('#time').closest(".timediv").append("<div class='validation-error'>" + requiredText + "</div>");
		 			return;
				}			
				
				if ($("#autodeactivate").val().indexOf('/') > 1)
				{
					var parts = $("#autodeactivate").val().split('/');
					var date = new Date(parts[2], parts[1]-1, parts[0], $("#autodeactivatetime").val(),0,0);
					var current = new Date();
					if (current > date)
					{
						var error = '<spring:message code="validation.AutoDeactivateInPast" />';
						$("#autodeactivatetime").parent().parent().append("<div class='validation-error'>" + error + "</div>");
						return;
					}
				}
				
			}
			
			validateInputAndSubmit($('#configure-message-form'));

		}
		
		function cancelConfiguration()
		{
			$('#configure-message-dialog').modal('hide');
		}
		
		function showActivityConfiguration()
		{
			$('.validation-error').remove();
			$('#configure-logging-dialog').modal('show');
		}
		
		function saveActivityConfiguration()
		{
			$('#configure-logging-form').submit();
		}
		
		function cancelActivityConfiguration()
		{
			$('#configure-logging-dialog').modal('hide');
		}
		
		function showComplexityConfiguration()
		{
			$('.validation-error').remove();
			$('#configure-complexity-dialog').modal('show');
		}
		
		function saveComplexityConfiguration()
		{
			$('#configure-complexity-form').find(".validation-error").remove();
			
			var okForm = true;
			
			$(".complexityInput").each(function(){
				
				var value = $(this).val();
				
				if(isNaN(value) || !$.isNumeric(value) || !isFinite(value))
				{
					$(this).after("<div class='validation-error'>" + invalidNumberText + "</div>");
					okForm = false;
				}
				else if(value < 0)
				{
					$(this).after("<div class='validation-error'>" + invalidPositiveText + "</div>");
					okForm = false;
				}
			});

			if(okForm)
			{
				$('#configure-complexity-form').submit();
			}
		}
		
		function cancelComplexityConfiguration()
		{
			$('#configure-complexity-dialog').modal('hide');
		}
		
		function showReportConfiguration()
		{
			$('.validation-error').remove();
			$('#configure-report-dialog').modal('show');
		}
		
		function saveReportConfiguration()
		{
			$('.validation-error').remove();			
			validateInputAndSubmit($('#configure-report-form'));	
		}
		
		function cancelReportConfiguration()
		{
			$('#configure-report-dialog').modal('hide');
		}
		
		function showBanUserConfiguration() {
			$('.validation-error').remove();
			$('#configure-banuser-dialog').modal('show');
		}
		
		function saveBanUserConfiguration()
		{
			$('.validation-error').remove();			
			validateInputAndSubmit($('#configure-banuser-form'));	
		}
		
		function cancelBanUserConfiguration()
		{
			$('#configure-banuser-dialog').modal('hide');
		}
		
		function addBanUserRow(email)
		{
			var tr = document.createElement("tr");
			var td = document.createElement("td");
			$(td).css("padding-right", "10px").text('<spring:message code="label.Email" />');
			$(tr).append(td);
			td = document.createElement("td");			
			var outerdiv = document.createElement("div");
			$(outerdiv).addClass("input-group").append('<div class="input-group-addon"><span class="glyphicon glyphicon-envelope"></span></div>');
			$(outerdiv).append('<input class="form-control" name="messageEmail" type="text" maxlength="255" style="width: 200px;" value="' + email + '" />');
			$(td).append(outerdiv);
			$(tr).append(td);
			$('#banUserEmails').find('tr:last').before(tr);
		}
		
		function checkLogging()
		{
			if ($("#enabled").is(":checked"))
			{
				$("#configure-logging-activities").show();
			} else {
				$("#configure-logging-activities").hide();
			}
		}
		
		function addReportRow(email)
		{
			var tr = document.createElement("tr");
			var td = document.createElement("td");
			$(td).css("padding-right", "10px").text('<spring:message code="label.Email" />');
			$(tr).append(td);
			td = document.createElement("td");			
			var outerdiv = document.createElement("div");
			$(outerdiv).addClass("input-group").append('<div class="input-group-addon"><span class="glyphicon glyphicon-envelope"></span></div>');
			$(outerdiv).append('<input class="form-control" name="messageEmail" type="text" maxlength="255" style="width: 200px;" value="' + email + '" />');
			$(td).append(outerdiv);
			$(tr).append(td);
			$('#reportEmails').find('tr:last').before(tr);
		}
		
		function showTrustIndicatorConfiguration() {
			$('.validation-error').remove();
			$('#configure-trustindicator-dialog').modal('show');
		}
		
		function saveTrustIndicatorConfiguration()
		{
			$('.validation-error').remove();			
			validateInputAndSubmit($('#configure-trustindicator-form'));	
		}
		
		function cancelTrustIndicatorConfiguration()
		{
			$('#configure-trustindicator-dialog').modal('hide');
		}
		
		$(function() {
			$("#administration-menu-tab").addClass("active");
			$("#system-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
			
			setCriticalityImage();
			checkLogging();
			$('#autodeactivatetime').val('${message.getAutoDeactivateTime()}');
			
			var recipients = '${reportRecipients}';
			var res = recipients.split(";");
			for (var i = 0; i < res.length; i++)
			{
				addReportRow(res[i]);
			}
			
			addReportRow("");
			 
			recipients = '${bannedUserRecipients}';
			var res = recipients.split(";");
			for (var i = 0; i < res.length; i++)
			{
				addBanUserRow(res[i]);
			}
			
			addBanUserRow("");
		});	
		
	
	</script>
		
</head>
<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
		<%@ include file="../menu.jsp" %>
		<%@ include file="adminmenu.jsp" %>	
		
		<div class="fixedtitleform">
			<div class="fixedtitleinner">
					
			</div>
		</div>
			
		<div class="page880" style="padding-bottom: 0px; padding-top: 160px; min-height: 500px;">
					
			<div>
				<table style="width: 500px; margin-left: auto; margin-right: auto;">
					<tr>
						<td><span style="font-size: large"><spring:message code="label.StaticSystemMessage" /></span></td>
						<td>
							<c:choose>
								<c:when test="${USER.getGlobalPrivilegeValue('SystemManagement') > 0}">
									<a class="btn btn-default" onclick="showConfiguration()"><spring:message code="label.Configure" /></a>
								</c:when>
								<c:otherwise>
									<a class="btn disabled btn-default"><spring:message code="label.Configure" /></a>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr style="padding-top: 10px;">
						<td>
							<spring:message code="label.CurrentStatus" />:
							<c:choose>
								<c:when test="${message.isActive()}">
									<spring:message code="label.Enabled" />
								</c:when>
								<c:otherwise>
									<spring:message code="label.Disabled" />
								</c:otherwise>
							</c:choose>
						</td>					
					</tr>
					<c:if test="${USER.getGlobalPrivilegeValue('SystemManagement') > 1}">
						<tr>
							<td style="padding-top: 20px;"><span style="font-size: large"><spring:message code="label.SurveyActivityLogging" /></span></td>
							<td style="padding-top: 20px;">
								<c:choose>
									<c:when test="${USER.getGlobalPrivilegeValue('SystemManagement') > 1}">
										<a class="btn btn-default" onclick="showActivityConfiguration()"><spring:message code="label.Configure" /></a>
									</c:when>
									<c:otherwise>
										<a class="btn disabled btn-default"><spring:message code="label.Configure" /></a>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr>
							<td>
								<spring:message code="label.CurrentStatus" />:
								<c:choose>
									<c:when test='${logging.equals("true")}'>
										<spring:message code="label.Enabled" />
									</c:when>
									<c:otherwise>
										<spring:message code="label.Disabled" />
									</c:otherwise>
								</c:choose>
							</td>					
						</tr>
						<tr>
							<td style="padding-top: 20px;"><span style="font-size: large"><spring:message code="label.ConfigureComplexity" /></span></td>
							<td style="padding-top: 20px;">
								<c:choose>
									<c:when test="${USER.getGlobalPrivilegeValue('SystemManagement') > 1}">
										<a class="btn btn-default" onclick="showComplexityConfiguration()"><spring:message code="label.Configure" /></a>
									</c:when>
									<c:otherwise>
										<a class="btn disabled btn-default"><spring:message code="label.Configure" /></a>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr>
							<td style="padding-top: 20px;"><span style="font-size: large"><spring:message code="label.SurveyReports" /></span></td>
							<td style="padding-top: 20px;">
								<c:choose>
									<c:when test="${USER.getGlobalPrivilegeValue('SystemManagement') > 1}">
										<a class="btn btn-default" onclick="showReportConfiguration()"><spring:message code="label.Configure" /></a>
									</c:when>
									<c:otherwise>
										<a class="btn disabled btn-default"><spring:message code="label.Configure" /></a>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr>
							<td style="padding-top: 20px;"><span style="font-size: large"><spring:message code="label.BanUserMessage" /></span></td>
							<td style="padding-top: 20px;">
								<c:choose>
									<c:when test="${USER.getGlobalPrivilegeValue('SystemManagement') > 1}">
										<a class="btn btn-default" onclick="showBanUserConfiguration()"><spring:message code="label.Configure" /></a>
									</c:when>
									<c:otherwise>
										<a class="btn disabled btn-default"><spring:message code="label.Configure" /></a>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr>
							<td style="padding-top: 20px;"><span style="font-size: large"><spring:message code="label.TrustIndicator" /></span></td>
							<td style="padding-top: 20px;">
								<c:choose>
									<c:when test="${USER.getGlobalPrivilegeValue('SystemManagement') > 1}">
										<a class="btn btn-default" onclick="showTrustIndicatorConfiguration()"><spring:message code="label.Configure" /></a>
									</c:when>
									<c:otherwise>
										<a class="btn disabled btn-default"><spring:message code="label.Configure" /></a>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:if>
				</table>			
			</div>	
			
		</div>
		
		<div class="modal" id="configure-logging-dialog" data-backdrop="static">
			<div class="modal-dialog">
	    	<div class="modal-content">
			<div class="modal-header">
				<b><spring:message code="label.ConfigureActivityLogging" /></b>
			</div>
			<div class="modal-body">
				<form:form id="configure-logging-form" method="POST" action="${contextpath}/administration/system/configureLogging" style="height: auto; margin: 0px; padding: 0px;">			
					<c:choose>
						<c:when test='${logging.equals("true")}'>
							<input onclick="checkLogging();" class="check" type="radio" name="enabled" value="false" /><spring:message code="label.DisableActivityLogging" /><br />
							<input onclick="checkLogging();" checked="checked" id="enabled" class="check" type="radio" name="enabled" value="true" /><spring:message code="label.EnableActivityLogging" /><br /><br />
						</c:when>
						<c:otherwise>
							<input onclick="checkLogging();" checked="checked" class="check" type="radio" name="enabled" value="false" /><spring:message code="label.DisableActivityLogging" /><br />
							<input onclick="checkLogging();" class="check" id="enabled" type="radio" name="enabled" value="true" /><spring:message code="label.EnableActivityLogging" /><br /><br />
						</c:otherwise>
					</c:choose>
								
					<ul id="configure-logging-activities" class="multiple-choice" style="width: 90%; max-height: 114px;">
						<c:forEach items="${allActivityIds}" var="id">
							<c:choose>
								<c:when test="${enabledActivityIds.contains(id)}">
									<li class="selected-choice possible-answer trigger" id="trigger${id}">
										<a onclick="selectMultipleChoiceAnswer(this);" >
											<span class="answertext">${id} - ${activity.getObject(id)} - <spring:message code="logging.${id}" /></span>
										</a>
		 								<input id="${id}" checked="checked" value="${id}" style="display: none" type="checkbox" name="activity${id}" />
									</li>	
								</c:when>
								<c:otherwise>
									<li class="possible-answer trigger" id="trigger${id}">
										<a onclick="selectMultipleChoiceAnswer(this);" >
											<span class="answertext">${id} - ${activity.getObject(id)} - <spring:message code="logging.${id}" /></span>
										</a>
		 								<input id="${id}" value="${id}" style="display: none" type="checkbox" name="activity${id}" />
									</li>	
								</c:otherwise>												
							</c:choose>
						</c:forEach>									
					</ul>
				</form:form>
			</div>
			<div class="modal-footer">
				<div style="float: right">
					<a onclick="cancelActivityConfiguration();"  class="btn btn-default"><spring:message code="label.Cancel" /></a>
				</div>
				<div style="padding-left: 65px">
					<a onclick="saveActivityConfiguration();" class="btn btn-primary"><spring:message code="label.Save" /></a>		
				</div>			
			</div>
			</div>
			</div>
		</div>
		
		<div class="modal" id="configure-complexity-dialog" data-backdrop="static">
			<div class="modal-dialog">
	    	<div class="modal-content">
			<div class="modal-header">
				<b><spring:message code="label.ConfigureComplexity" /></b>
			</div>
			<div class="modal-body" style="height: 400px; overflow: auto;">
				<form:form id="configure-complexity-form" method="POST" action="${contextpath}/administration/system/configureComplexity" style="height: auto; margin: 0px; padding: 0px;">			
					
					<label for="lowScore"><spring:message code="label.complexity.lowScore"></spring:message></label><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="lowScore" value="<esapi:encodeForHTMLAttribute>${complexityParameters.lowScore}</esapi:encodeForHTMLAttribute>"/>
					<br/><br />
	
					 <label for="mediumScore"><spring:message code="label.complexity.mediumScore"></spring:message></label><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="mediumScore" value="<esapi:encodeForHTMLAttribute>${complexityParameters.mediumScore}</esapi:encodeForHTMLAttribute>"/>
					<br/><br />
					
					<label for="highScore"><spring:message code="label.complexity.highScore"></spring:message></label><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="highScore" value="<esapi:encodeForHTMLAttribute>${complexityParameters.highScore}</esapi:encodeForHTMLAttribute>"/>
					<br/><br />
					
					<span style="display:none"><label for="criticalScore" ><spring:message code="label.complexity.criticalScore"></spring:message></label><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="criticalScore" value="<esapi:encodeForHTMLAttribute>${complexityParameters.criticalScore}</esapi:encodeForHTMLAttribute>"/>
					<br/><br /></span>
					
					<label for="weightSectionItem"><spring:message code="label.complexity.weightSectionItem"></spring:message></label><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="weightSectionItem" value="<esapi:encodeForHTMLAttribute>${complexityParameters.weightSectionItem}</esapi:encodeForHTMLAttribute>"/>
					<br/><br />
					
					<label for="weightSimpleItem"><spring:message code="label.complexity.weightSimpleItem"></spring:message></label><br />
					<span class="labelInfo"><spring:message code="label.complexity.weightSimpleItem.description"></spring:message></span><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="weightSimpleItem" value="<esapi:encodeForHTMLAttribute>${complexityParameters.weightSimpleItem}</esapi:encodeForHTMLAttribute>"/>
					<br/><br />
					
					<label for="weightSimpleQuestion"><spring:message code="label.complexity.weightSimpleQuestion"></spring:message></label><br />
					<span class="labelInfo"><spring:message code="label.complexity.weightSimpleQuestion.description"></spring:message></span><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="weightSimpleQuestion" value="<esapi:encodeForHTMLAttribute>${complexityParameters.weightSimpleQuestion}</esapi:encodeForHTMLAttribute>"/>
					<br/><br />
					
					<label for="weightChoiceQuestion"><spring:message code="label.complexity.weightChoiceQuestion"></spring:message></label><br />
					<span class="labelInfo"><spring:message code="label.complexity.weightChoiceQuestion.description"></spring:message></span><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="weightChoiceQuestion" value="<esapi:encodeForHTMLAttribute>${complexityParameters.weightChoiceQuestion}</esapi:encodeForHTMLAttribute>"/>
					<br/><br />
					
					<label for="weightGalleryQuestion"><spring:message code="label.complexity.weightGalleryQuestion"></spring:message></label><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="weightGalleryQuestion" value="<esapi:encodeForHTMLAttribute>${complexityParameters.weightGalleryQuestion}</esapi:encodeForHTMLAttribute>"/>
					<br/><br />
					
					<label for="weightTableOrMatrixQuestion"><spring:message code="label.complexity.weightTableOrMatrixQuestion"></spring:message></label><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="weightTableOrMatrixQuestion" value="<esapi:encodeForHTMLAttribute>${complexityParameters.weightTableOrMatrixQuestion}</esapi:encodeForHTMLAttribute>"/>
					<br/><br />
					
					<label for="rowThreshold"><spring:message code="label.complexity.rowThreshold"></spring:message></label><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="rowThreshold" value="<esapi:encodeForHTMLAttribute>${complexityParameters.rowThreshold}</esapi:encodeForHTMLAttribute>"/>
					<br/><br />
					
					<label for="weightTooManyRows"><spring:message code="label.complexity.weightTooManyRows"></spring:message></label><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="weightTooManyRows" value="<esapi:encodeForHTMLAttribute>${complexityParameters.weightTooManyRows}</esapi:encodeForHTMLAttribute>"/>
					<br/><br />
									
					<label for="columnThreshold"><spring:message code="label.complexity.columnThreshold"></spring:message></label><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="columnThreshold" value="<esapi:encodeForHTMLAttribute>${complexityParameters.columnThreshold}</esapi:encodeForHTMLAttribute>"/>
					<br/><br />
					
					<label for="weightTooManyColumns"><spring:message code="label.complexity.weightTooManyColumns"></spring:message></label><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="weightTooManyColumns" value="<esapi:encodeForHTMLAttribute>${complexityParameters.weightTooManyColumns}</esapi:encodeForHTMLAttribute>"/>
					<br/><br />
					
					<label for="possibleAnswersThreshold"><spring:message code="label.complexity.possibleAnswersThreshold"></spring:message></label><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="possibleAnswersThreshold" value="<esapi:encodeForHTMLAttribute>${complexityParameters.possibleAnswersThreshold}</esapi:encodeForHTMLAttribute>"/>
					<br/><br />
									
					<label for="weightTooManyPossibleAnswers"><spring:message code="label.complexity.weightTooManyPossibleAnswers"></spring:message></label><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="weightTooManyPossibleAnswers" value="<esapi:encodeForHTMLAttribute>${complexityParameters.weightTooManyPossibleAnswers}</esapi:encodeForHTMLAttribute>"/>
					<br/><br />
					
					<label for="weightDependency"><spring:message code="label.complexity.weightDependency"></spring:message></label><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="weightDependency" value="<esapi:encodeForHTMLAttribute>${complexityParameters.weightDependency}</esapi:encodeForHTMLAttribute>"/>
					<br/><br />
					
					<label for="weightDoubleDependency"><spring:message code="label.complexity.weightDoubleDependency"></spring:message></label><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="weightDoubleDependency" value="<esapi:encodeForHTMLAttribute>${complexityParameters.weightDoubleDependency}</esapi:encodeForHTMLAttribute>"/>
					<br/><br />
					
					<label for="questionsThreshold"><spring:message code="label.complexity.questionsThreshold"></spring:message></label><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="questionsThreshold" value="<esapi:encodeForHTMLAttribute>${complexityParameters.questionsThreshold}</esapi:encodeForHTMLAttribute>"/>
					<br/><br />
					
					<label for="questionsThresholdScore"><spring:message code="label.complexity.questionsThresholdScore"></spring:message></label><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="questionsThresholdScore" value="<esapi:encodeForHTMLAttribute>${complexityParameters.questionsThresholdScore}</esapi:encodeForHTMLAttribute>"/>
					<br/><br />
					
					<label for="sectionThreshold"><spring:message code="label.complexity.sectionThreshold"></spring:message></label><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="sectionThreshold" value="<esapi:encodeForHTMLAttribute>${complexityParameters.sectionThreshold}</esapi:encodeForHTMLAttribute>"/>
					<br/><br />
					
					<label for="sectionThresholdScore"><spring:message code="label.complexity.sectionThresholdScore"></spring:message></label><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="sectionThresholdScore" value="<esapi:encodeForHTMLAttribute>${complexityParameters.sectionThresholdScore}</esapi:encodeForHTMLAttribute>"/>
					<br/><br />
					
					<label for="dependenciesThreshold"><spring:message code="label.complexity.dependenciesThreshold"></spring:message></label><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="dependenciesThreshold" value="<esapi:encodeForHTMLAttribute>${complexityParameters.dependenciesThreshold}</esapi:encodeForHTMLAttribute>"/>
					<br/><br />
					
					<label for="dependenciesThresholdScore"><spring:message code="label.complexity.dependenciesThresholdScore"></spring:message></label><br />
					<input type="text" class="small-form-control complexityInput required number min0" name="dependenciesThresholdScore" value="<esapi:encodeForHTMLAttribute>${complexityParameters.dependenciesThresholdScore}</esapi:encodeForHTMLAttribute>"/>
					<br/><br />
	
							
				</form:form>
			</div>
			<div class="modal-footer">
				<div style="float: right">
					<a onclick="cancelComplexityConfiguration();"  class="btn btn-default"><spring:message code="label.Cancel" /></a>
				</div>
				<div style="padding-left: 65px">
					<a onclick="saveComplexityConfiguration();" class="btn btn-primary"><spring:message code="label.Save" /></a>		
				</div>			
			</div>
			</div>
			</div>
		</div>
		
		<div class="modal" id="configure-message-dialog" data-backdrop="static">
			<div class="modal-dialog">
	    	<div class="modal-content">
			<div class="modal-header">
				<b><spring:message code="label.ConfigureSystemMessage" /></b>
			</div>
			<div class="modal-body" style="height: 500px">
				<form:form id="configure-message-form" method="POST" action="${contextpath}/administration/system/configureMessage" style="height: auto; margin: 0px; padding: 0px;">			
					<b><spring:message code="label.MessageText" />:</b>
					<div class="messagetextdiv" style="margin-bottom: 15px;">
						<textarea id="messagetext" name="text" class="tinymcemessage required freetext max5000"><esapi:encodeForHTML>${message.text}</esapi:encodeForHTML></textarea>	<br />
					</div>
					
					<div style="float: left">
						<b><spring:message code="label.Criticality" />:</b><br />
						<select class="small-form-control" class="required" name="criticality" id="criticality" onchange="checkCriticality()">
							<c:forEach items="${message.types}" var="type">
								<c:choose>
									<c:when test="${type.criticality == message.criticality}">
										<option value="<esapi:encodeForHTMLAttribute>${type.criticality}</esapi:encodeForHTMLAttribute>" selected="selected"><esapi:encodeForHTML>${type.label}</esapi:encodeForHTML></option>
									</c:when>
									<c:otherwise>
										<option value="<esapi:encodeForHTMLAttribute>${type.criticality}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML>${type.label}</esapi:encodeForHTML></option>
									</c:otherwise>
								</c:choose>						
							</c:forEach>
						</select>
					</div>
					
					<div style="float: left; margin-left: 20px; padding-top: 10px;">
						<c:forEach items="${message.types}" var="type">
							<img class="messageimage" id="img<esapi:encodeForHTMLAttribute>${type.criticality}</esapi:encodeForHTMLAttribute>" src="${contextpath}/resources/images/<esapi:encodeForHTMLAttribute>${type.icon}</esapi:encodeForHTMLAttribute>" />
						</c:forEach>
					</div>
					
					<div class="timediv" style="float: left; margin-left: 50px;">
						<b><spring:message code="label.DisplayTimeSec" />:</b><br />
						<input class="small-form-control required number min0" style="width: 50px" type="text" id="time" name="time" value="<esapi:encodeForHTMLAttribute>${message.time}</esapi:encodeForHTMLAttribute>" />
					</div>				
									
					<div style="clear: both; margin-bottom: 10px;"></div>
					
					<div style="float: left">
						<b><spring:message code="label.MessageShownTo" />:</b><br />
						<select class="small-form-control" name="type" style="width: auto">				
							<c:choose>
								<c:when test="${message.type == 0}">
									<option value="0" selected="selected"><spring:message code="label.FormManagersOnly" /></option>
								</c:when>
								<c:otherwise>
									<option value="0"><spring:message code="label.FormManagersOnly" /></option>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${message.type == 1}">
									<option value="1" selected="selected"><spring:message code="label.ParticipantsOnly" /></option>
								</c:when>
								<c:otherwise>
									<option value="1"><spring:message code="label.ParticipantsOnly" /></option>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${message.type == 2}">
									<option value="2" selected="selected"><spring:message code="label.ParticipantsAndFormManagers" /></option>
								</c:when>
								<c:otherwise>
									<option value="2"><spring:message code="label.ParticipantsAndFormManagers" /></option>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${message.type == 3}">
									<option value="3" selected="selected"><spring:message code="label.Everyone" /></option>
								</c:when>
								<c:otherwise>
									<option value="3"><spring:message code="label.Everyone" /></option>
								</c:otherwise>
							</c:choose>
						</select>
					</div>
					
					<div style="float: right; padding-top: 40px; padding-right: 120px;">
						<c:choose>
							<c:when test="${message.isActive()}">
								<input checked="checked" id="activetrue" class="check" type="radio" name="active" value="true" /><spring:message code="label.Activate" /><br />
								<input class="check" type="radio" name="active" value="false" /><spring:message code="label.Deactivate" />
							</c:when>
							<c:otherwise>
								<input id="activetrue" class="check" type="radio" name="active" value="true" /><spring:message code="label.Activate" /><br />
								<input checked="checked" class="check" type="radio" name="active" value="false" /><spring:message code="label.Deactivate" />
							</c:otherwise>
						</c:choose>
					</div>
					
					<div style="float: left; margin-top: 10px;">
						<b><spring:message code="label.AutoDeactivateOn" />:</b>
						<div class="input-prepend">
							<span class="add-on"><i class="icon-calendar"></i></span>
							<input class="small-form-control span2 datepicker date" id="autodeactivate" name="autodeactivate" placeholder="DD/MM/YYYY" type="text" value="${message.autoDeactivateDate}" style="width: 105px" />
							<select id="autodeactivatetime" name="autodeactivatetime" class="small-form-control" style="width: auto">
								<option value="0">00:00</option>
								<option value="1">01:00</option>
								<option value="2">02:00</option>
								<option value="3">03:00</option>
								<option value="4">04:00</option>
								<option value="5">05:00</option>
								<option value="6">06:00</option>
								<option value="7">07:00</option>
								<option value="8">08:00</option>
								<option value="9">09:00</option>
								<option value="10">10:00</option>
								<option value="11">11:00</option>
								<option value="12">12:00</option>
								<option value="13">13:00</option>
								<option value="14">14:00</option>
								<option value="15">15:00</option>
								<option value="16">16:00</option>
								<option value="17">17:00</option>
								<option value="18">18:00</option>
								<option value="19">19:00</option>
								<option value="20">20:00</option>
								<option value="21">21:00</option>
								<option value="22">22:00</option>
								<option value="23">23:00</option>
							</select>
						</div>
					</div>
				</form:form>
									
			</div>
			<div class="modal-footer">
				<div style="float: right">
					<a onclick="cancelConfiguration();"  class="btn btn-default"><spring:message code="label.Cancel" /></a>
				</div>
				<div style="padding-left: 65px">
					<a onclick="saveConfiguration();" class="btn btn-primary"><spring:message code="label.Save" /></a>		
				</div>			
			</div>
			</div>
			</div>
		</div>
		
		<div class="modal" id="configure-report-dialog" data-backdrop="static">
			<div class="modal-dialog">
	    	<div class="modal-content">
			<div class="modal-header">
				<b><spring:message code="label.ConfigureSurveyReportMessage" /></b>
			</div>
			<div class="modal-body">
				<form:form id="configure-report-form" method="POST" action="${contextpath}/administration/system/configureReports" style="height: auto; margin: 0px; padding: 0px;">			
					<div>
						<b><spring:message code="label.MaxNumberReports" />:</b><br />
						<input name="maxNumber" id="reportMaxNumber" value="${reportMaxNumber}" class="form-control required number min1 max100" style="width: 60px" />
					</div>
					
					<b><spring:message code="label.MessageText" />:</b><br />
					<div class="messagetextdiv" style="margin-bottom: 15px;">
						<textarea id="reportMessageText" name="messageText" class="tinymcemessage required freetext max5000"><esapi:encodeForHTML>${reportMessageText}</esapi:encodeForHTML></textarea>	<br />
					</div>
					
					<b><spring:message code="label.ListOfRecipients" />:</b><br />
					
					<table id="reportEmails" style="margin-top: 10px;">
						<tr>
							<td colspan="2" style="text-align: right">
								<a onclick="addReportRow('')" class="btn btn-default" style="margin-top: 10px; margin-left: 200px"><spring:message code="label.Add" /></a>
							</td>
						</tr>
					</table> 
					 
				</form:form>
			</div>
			<div class="modal-footer">
				<div style="float: right">
					<a onclick="cancelReportConfiguration();"  class="btn btn-default"><spring:message code="label.Cancel" /></a>
				</div>
				<div style="padding-left: 65px">
					<a onclick="saveReportConfiguration();" class="btn btn-primary"><spring:message code="label.Save" /></a>		
				</div>			
			</div>
			</div>
			</div>
		</div>
		
		<div class="modal" id="configure-banuser-dialog" data-backdrop="static">
			<div class="modal-dialog" style="width: 1080px;">
	    	<div class="modal-content">
			<div class="modal-header">
				<b><spring:message code="label.ConfigureBanUserMessage" /></b>
			</div>
			<div class="modal-body">
				<form:form id="configure-banuser-form" method="POST" action="${contextpath}/administration/system/configureBanUsers" style="height: auto; margin: 0px; padding: 0px;">			
					<table>
						<tr>
							<td>
								<b><spring:message code="label.MessageBanningUser" />:</b><br />
								<div class="messagetextdiv" style="margin-bottom: 15px;">
									<textarea id="banUserMessageText" name="banUserMessageText" class="tinymcemessage required freetext max5000"><esapi:encodeForHTML>${banUserMessageText}</esapi:encodeForHTML></textarea>
								</div>
								
								<b><spring:message code="label.MessageUnbanningUser" />:</b><br />
								<div class="messagetextdiv" style="margin-bottom: 15px;">
									<textarea id="unbanUserMessageText" name="unbanUserMessageText" class="tinymcemessage required freetext max5000"><esapi:encodeForHTML>${unbanUserMessageText}</esapi:encodeForHTML></textarea>
								</div>
								
								<b><spring:message code="label.ListOfRecipients" />:</b><br />
								
								<table id="banUserEmails" style="margin-top: 10px;">
									<tr>
										<td colspan="2" style="text-align: right">
											<a onclick="addBanUserRow('')" class="btn btn-default" style="margin-top: 10px; margin-left: 200px"><spring:message code="label.Add" /></a>
										</td>
									</tr>
								</table> 
							</td>
							<td style="padding-left: 20px; vertical-align: top;">
								<b><spring:message code="label.MessageBannedUser" />:</b><br />
								<div class="messagetextdiv" style="margin-bottom: 15px;">
									<textarea id="bannedUserMessageText" name="bannedUserMessageText" class="tinymcemessage required freetext max5000"><esapi:encodeForHTML>${bannedUserMessageText}</esapi:encodeForHTML></textarea>
								</div>
								
								<b><spring:message code="label.MessageUnbannedUser" />:</b><br />
								<div class="messagetextdiv" style="margin-bottom: 15px;">
									<textarea id="unbannedUserMessageText" name="unbannedUserMessageText" class="tinymcemessage required freetext max5000"><esapi:encodeForHTML>${unbannedUserMessageText}</esapi:encodeForHTML></textarea>
								</div>
							</td>
						</tr>
					 </table>
				</form:form>
			</div>
			<div class="modal-footer">
				<div style="float: right">
					<a onclick="cancelBanUserConfiguration();"  class="btn btn-default"><spring:message code="label.Cancel" /></a>
				</div>
				<div style="padding-left: 65px">
					<a onclick="saveBanUserConfiguration();" class="btn btn-primary"><spring:message code="label.Save" /></a>		
				</div>			
			</div>
			</div>
			</div>
		</div>
		
		<div class="modal" id="configure-trustindicator-dialog" data-backdrop="static">
			<div class="modal-dialog" style="width: 500px;">
	    	<div class="modal-content">
			<div class="modal-header">
				<b><spring:message code="label.TrustIndicatorParameters" /></b>
			</div>
			<div class="modal-body">
				<form:form id="configure-trustindicator-form" method="POST" action="${contextpath}/administration/system/configureTrustIndicator" style="height: auto; margin: 0px; padding: 0px;">			
					<table>
						<tr>
							<td>
								<b><spring:message code="label.TrustCreatorInternal" />:</b><br />
								<div class="messagetextdiv" style="margin-bottom: 15px;">
									<input type="text" id="trustIndicatorCreatorInternal" name="trustIndicatorCreatorInternal" class="form-control required number" value="${trustIndicatorCreatorInternal}" />
								</div>
							</td>
							<td style="padding-left: 100px;">
								<b><spring:message code="label.TrustMinimumPassMark" />:</b><br />
								<div class="messagetextdiv" style="margin-bottom: 15px;">
									<input type="text" id="trustIndicatorMinimumPassMark" name="trustIndicatorMinimumPassMark" class="form-control required number" value="${trustIndicatorMinimumPassMark}" />
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<b><spring:message code="label.TrustPastSurveys" />:</b><br />
								<div class="messagetextdiv" style="margin-bottom: 15px;">
									<input type="text" id="trustIndicatorPastSurveys" name="trustIndicatorPastSurveys" class="form-control required number" value="${trustIndicatorPastSurveys}" />
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<b><spring:message code="label.TrustPrivilegedUser" />:</b><br />
								<div class="messagetextdiv" style="margin-bottom: 15px;">
									<input type="text" id="trustIndicatorPrivilegedUser" name="trustIndicatorPrivilegedUser" class="form-control required number" value="${trustIndicatorPrivilegedUser}" />
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<b><spring:message code="label.TrustNbContributions" />:</b><br />
								<div class="messagetextdiv" style="margin-bottom: 15px;">
									<input type="text" id="trustIndicatorNbContributions" name="trustIndicatorNbContributions" class="form-control required number" value="${trustIndicatorNbContributions}" />
								</div>
							</td>
						</tr>
					 </table>
				</form:form>
			</div>
			<div class="modal-footer">
				<div style="float: right">
					<a onclick="cancelTrustIndicatorConfiguration();"  class="btn btn-default"><spring:message code="label.Cancel" /></a>
				</div>
				<div style="padding-left: 65px">
					<a onclick="saveTrustIndicatorConfiguration();" class="btn btn-primary"><spring:message code="label.Save" /></a>		
				</div>			
			</div>
			</div>
			</div>
		</div>
		
		<c:if test="${error != null}">
			<script type="text/javascript">
				showError('${error}');
			</script>
		</c:if>
	</div>

	<%@ include file="../footer.jsp" %>	

</body>
</html>
