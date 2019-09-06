<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Properties" /></title>	
	<%@ include file="../includes.jsp" %>	
	<link href="${contextpath}/resources/css/management.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/fileuploader.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/properties.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${contextpath}/resources/js/fileuploader.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/properties.js?version=<%@include file="../version.txt" %>"></script>
		
	<script type="text/javascript"> 
		var surveyShortname = '${form.survey.shortname}';
		var surveyUniqueId = '${form.survey.uniqueId}';
	
		var unsavedChanges = false;
		var goToOverview = false;
		var isOPC = ${form.survey.isOPC};
		
		$(function() {
			checkCaptcha();
			
			window.onbeforeunload = function() { 
				  if (unsavedChanges) {
				    return "<spring:message code="message.UnsavedChanges" />";
				  }
				};							
			<c:if test="${tab != null && tab == 2}">
				$('#tablink2').tab('show');
			 	activate($('#tablink2'));
			</c:if>			
			<c:if test="${tab != null && tab == 3}">
				$('#tablink3').tab('show');
		 		activate($('#tablink3'));
			</c:if>			
			<c:if test="${tab != null && tab == 4}">
				$('#tablink4').tab('show');
	 			activate($('#tablink4'));
	 		</c:if>	 		
	 		<c:if test="${tab != null && tab == 5}">
				$('#tablink5').tab('show');
		 		activate($('#tablink5'));
			</c:if>			
			<c:if test="${tab != null && tab == 6}">
				$('#tablink6').tab('show');
		 		activate($('#tablink6'));
			</c:if>
			<c:if test="${tab != null && tab == 7}">
				$('#tablink7').tab('show');
		 		activate($('#tablink7'));
			</c:if>
			
			<c:if test="${editelem != null}">
				editProperties();
				goToOverview = true;
				$("#origin").val("overview");
				 $('#edit-properties-dialog-body').animate({
			         scrollTop: $("#${editelem}").offset().top-230
			     }, 1000);
			</c:if>
			
			checkQuiz(false, false);
		});	
		
		function hasPendingChangesForPublishing()
		{
			var result = false;
			$.ajax({
				type:'GET',
				  url: "${contextpath}/${sessioninfo.shortname}/management/pendingChangesForPublishing",
				  data: "shortname=" + "",
				  dataType: 'json',
				  cache: false,
				  async: false,
				  success: function( data ) {					  
					  result = data;				  
				}});
			
			return result;
		}
		
		function checkPropertiesAndSubmit(regformconfirmed, publishingconfirmed)
		{
			try {				
				$("#edit-properties-dialog").find(".validation-error").remove();
				var tabs;
				var invalid = false;
				if ($("#edit-prop-tabs-1").is(":visible"))
				{
					tabs = $("#edit-prop-tabs-1");
					if (!checkShortname($('#edit-survey-shortname').val(), '${form.survey.id}'))
					{
						$('#edit-survey-shortname').parent().append("<div class='validation-error'>" + shortnameAlreadyExists + "</div>");
						return;
					}
					var value = $("#edit-survey-shortname").val();
					var reg = /^[a-zA-Z0-9-_]+$/;
					if ($("#edit-survey-shortname").parent().find(".validation-error").length == 0)
					{
						if( !reg.test( value ) ) {
					    	$("#edit-survey-shortname").after("<div class='validation-error'>" + shortnameText + "</div>");
							return;
					    } else if( value.indexOf("__") > -1 ) {
					    	$("#edit-survey-shortname").after("<div class='validation-error'>" + shortnameText2 + "</div>");
							return;
					    } ;
					}
					
					var title = $("#edit-survey-title").text().length;
					if (title > 2000)
					{
						$("#edit-survey-title").parent().append("<div class='validation-error'>" + texttoolongText + "</div>");
						return;
					}
				} else if ($("#edit-prop-tabs-2").is(":visible"))
				{
					tabs = $("#edit-prop-tabs-2");	
				} else if ($("#edit-prop-tabs-3").is(":visible"))
				{
					tabs = $("#edit-prop-tabs-3");
				} else if ($("#edit-prop-tabs-4").is(":visible"))
				{
					tabs = $("#edit-prop-tabs-4");
				} else if ($("#edit-prop-tabs-5").is(":visible"))
				{
					tabs = $("#edit-prop-tabs-5");
					if ($("#survey\\.start").val().length > 0 && $("#survey\\.end").val().length > 0)
					{
						var startdate = parseDateTime($("#survey\\.start").val(),$("#startHour").val());
						var enddate =  parseDateTime($("#survey\\.end").val(),$("#endHour").val());
						if (startdate >= enddate)
						{
							$("#survey\\.end").parent().append("<div class='validation-error'>" + invalidStartEnd + "</div>");
							return;
						};					
						<c:if test="${!form.survey.isActive}">
							var now = new Date();						
							var autoPublishEnabled = ($("#autopub").is(":checked"));						
							if(autoPublishEnabled && startdate < now && enddate > now)
							{							
								$('#edit-properties-dialog').modal('hide');
								$('#publishConfirmationDialog').modal('show');
								return;
							};
							
							<c:if test="${!form.survey.automaticPublishing}">
								if(autoPublishEnabled && startdate > now)
								{							
									$('#edit-properties-dialog').modal('hide');
									$('#publishConfirmationDialog3').modal('show');
									return;
								};
							</c:if>
						</c:if>
						<c:if test="${form.survey.isActive}">
							var now = new Date();						
							var autoPublishEnabled = ($("#autopub").is(":checked"));						
							if(autoPublishEnabled && startdate > now)
							{							
								$('#edit-properties-dialog').modal('hide');
								$('#publishConfirmationDialog2').modal('show');
								return;
							};
							
							if(autoPublishEnabled && enddate < now)
							{							
								$('#edit-properties-dialog').modal('hide');
								$('#publishConfirmationDialog4').modal('show');
								return;
							};
							
						</c:if>
					};
					
					if ($('#notificationselector1').is(":checked") && !$("#autopub").is(":checked"))
					{
						$('#notificationselector1').parent().append("<div class='validation-error'>" + endNotificationAutomatedPublishing + "</div>");
						return;
					}
					
					var labels = [];
					$("#edit-prop-tabs-5").find("input[name^='doclabel']").each(function(){
						var label = $(this).val();
						if (label.length > 0)
						{
							for (var i = 0; i < labels.length; i++)
							{
								if (labels[i] == label)
								{
									$(this).parent().append("<div class='validation-error'><spring:message code="error.LabelAlreadyUsed" /></div>");
									invalid = true;
									return;
								}
							}
							labels[labels.length] = label;
						}
					});					
					
				} else if ($("#edit-prop-tabs-6").is(":visible"))
				{
					tabs = $("#edit-prop-tabs-6");
				} else if ($("#edit-prop-tabs-7").is(":visible"))
				{
					tabs = $("#edit-prop-tabs-7");
				}
				
				if (invalid) return;
				
				var result = validateInput(tabs);
				
				if (result == false)
				{
					return;
				}
				
				<c:if test="${!validregform}">
					if ($("#edit-prop-tabs-5").is(":visible"))
					if ($("#survey\\.registrationForm1").is(":checked") && !regformconfirmed)
					{
						$('#edit-properties-dialog').modal('hide');
						$("#confirmregformdialog").modal("show");
						return;
					}					
				</c:if>
				
				if ($("#edit-prop-tabs-6").is(":visible"))
				{
					if ($("#survey\\.publication\\.allContributions1").is(":checked"))
					{
						var counter = $("#contributionsToPublishDiv").find("input[name^='contribution']:checked").length;
						if (counter > 3)
						{
							$("#contributionsToPublishDiv").before("<div class='validation-error'>" + atmost3Selections + "</div>");
							return;
						}					
					}
					
					<c:if test="${!form.survey.publication.showContent && !form.survey.publication.showCharts && !form.survey.publication.showStatistics}">
						if (!publishingconfirmed && ($("#showContent").is(":checked") || $("#showCharts").is(":checked") || $("#showStatistics").is(":checked")))
						{
							$('#edit-properties-dialog').modal('hide');
							$("#confirmpublicationdialog").modal("show");
							return;
						}
					</c:if>
				}				
				
			} catch (e)	{

			}
			
			unsavedChanges=false;			
			
			$(".modal").modal('hide');
			$("#generic-wait-dialog").modal("show");
			$('#save-form').submit();
		}
		
		function cancelDialog()
		{
			unsavedChanges = false;			
			if (goToOverview)
			{
				window.location.href = '<c:url value="/${form.survey.shortname}/management/overview"/>';
			} else {
				window.location.href = '<c:url value="/${form.survey.shortname}/management/properties"/>?tab=' + $('#selected-tab').val();
			}
		}		
	</script>
		
</head>
<body>

	<%@ include file="../header.jsp" %>	
	<%@ include file="../menu.jsp" %>
	<%@ include file="formmenu.jsp" %>
	
	<div class="fixedtitleform">
		<div class="fixedtitleinner">
			<h1><spring:message code="label.Properties" /></h1>		
		</div>
	</div>
	
	<div id="action-bar" class="container action-bar">
		<div class="row">
			<div class="col-md-12" style="text-align:center">
				<c:choose>
					<c:when test="${USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1 || form.survey.owner.id == USER.id}">
						<input id="btnEditPropertiesEnabled" type="button" onclick="editProperties();" class="btn btn-info" value="<spring:message code="label.Edit" />" />
					</c:when>
					<c:otherwise>
						<input type="button" id="btnEditPropertiesDisabled" class="btn disabled btn-default" value="<spring:message code="label.Edit" />" />
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>

	<div class="fullpageform" style="margin-top:0px;">		
		
		<div style="width: 900px; margin-left: auto; margin-right: auto; text-align: right;">	
		
			<div style="float: left; margin-right: 15px; width: 180px;">
				<ul class="nav" >
					<li><a style="padding: 5px;" id="tablink1" onclick="activate(this);" class="grey-background" href="#prop-tabs-1" data-toggle="tab"><spring:message code="label.Basic" /></a></li>
					<li><a style="padding: 5px;" id="tablink5" onclick="activate(this);" href="#prop-tabs-5" data-toggle="tab"><spring:message code="label.Advanced" /></a></li>
					<li><a style="padding: 5px;" id="tablink2" onclick="activate(this);" href="#prop-tabs-2" data-toggle="tab"><spring:message code="label.Security" /></a></li>
					<li><a style="padding: 5px;" id="tablink3" onclick="activate(this);" href="#prop-tabs-3" data-toggle="tab"><spring:message code="label.Appearance" /></a></li>
					<li><a style="padding: 5px;" id="tablink6" onclick="activate(this);" href="#prop-tabs-6" data-toggle="tab"><spring:message code="label.PublishResults" /></a></li>
					<li><a style="padding: 5px;" id="tablink4" onclick="activate(this);" href="#prop-tabs-4" data-toggle="tab"><spring:message code="label.SpecialPages" /></a></li>
					<li><a style="padding: 5px;" id="tablink7" onclick="activate(this);" href="#prop-tabs-7" data-toggle="tab"><spring:message code="label.Quiz" /></a></li>
					<c:if test="${enableopc && USER.getGlobalPrivilegeValue('ECAccess') > 0}">
						<li><a style="padding: 5px;" id="tablink8" onclick="activate(this);" href="#prop-tabs-8" data-toggle="tab"><spring:message code="label.OPC" /></a></li>
					</c:if>
				</ul>
			</div>
			
				<div class="tab-content" style="float: left; width: 700px; border-bottom: 1px solid #ddd; margin-bottom: 15px; text-align: left">
					
					<div id="prop-tabs-1" class="tab-pane active">
						<table class="table table-striped table-bordered">
							<tr>
								<td class="table-label"><spring:message code="label.UniqueIdentifier" /></td>
								<td style="max-width: 400px;" class="shortname">
									<esapi:encodeForHTML>${form.survey.shortname}</esapi:encodeForHTML>
								</td>
							</tr>
							<tr>
								<td class="table-label"><spring:message code="label.Title" /></td>
								<td><div class="surveytitle" style="width: 600px">${form.survey.title}</div></td>
							</tr>
							<tr>
								<td class="table-label"><spring:message code="label.PivotLanguage" /></td>
								<td>
									<esapi:encodeForHTML>${form.survey.language.code}</esapi:encodeForHTML>	
								</td>
							</tr>
							<tr>
								<td class="table-label"><spring:message code="label.Contact" /></td>
								<td>
									<div style="max-width: 600px; word-wrap: break-word">
										<esapi:encodeForHTML>${form.survey.contact}</esapi:encodeForHTML>
										<c:if test='${!form.survey.contact.contains("@") && form.survey.contactLabel != null && form.survey.contactLabel.length() > 0}'>
											(${form.survey.contactLabel})
										</c:if>
									</div>
								</td>
							</tr>
						</table>
					</div>
					<div id="prop-tabs-2" class="tab-pane">
						<table class="table table-striped table-bordered">
							<tr>
								<td class="table-label"><spring:message code="label.Security" /></td>
								<td>
									<c:choose>
										<c:when test='${form.survey.security.equals("open") || form.survey.security.equals("openanonymous")}'>
											<spring:message code="form.Open" />
										</c:when>
										<c:otherwise>
											<spring:message code="form.Secured" />
											<c:if test="${form.survey.password != null && form.survey.password.length() > 0}">
												<br />
												<spring:message code="label.GlobalPassword" />
											</c:if>
											<c:if test="${form.survey.getEcasSecurity()}">
												<br />
												<spring:message code="label.EULogin" />
												<c:choose>
													<c:when test='${form.survey.ecasMode.equals("all")}'>
													&nbsp;(<spring:message code="label.All" />)
													</c:when>
													<c:otherwise>
													&nbsp;(<spring:message code="label.InternalStaffOnly" />)
													</c:otherwise>
												</c:choose>
											</c:if>
										</c:otherwise>
									</c:choose>									
								</td>
							</tr><tr>
								<td class="table-label">
									<spring:message code="label.Privacy" />
									<div class="help"><spring:message code="form.Privacy.Identified" /></div>
								</td>
								<td>
									<c:choose>
										<c:when test='${form.survey.security.equals("openanonymous") || form.survey.security.equals("securedanonymous")}'>
											<spring:message code="label.No" />
										</c:when>
										<c:otherwise>
											<spring:message code="label.Yes" />
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
							<tr>
								<td class="table-label">
									<spring:message code="label.Visibility" />
									<div class="help"><spring:message code="label.AdvertiseYourFormA" />&nbsp;<spring:message code="label.AdvertiseYourFormB" /> <a data-toggle="tooltip" title="<spring:message code="label.LearnMore" />" target="_blank" href="${contextpath}/home/helpauthors#_Toc7-5"><img src="${contextpath}/resources/images/icons/24/help_bubble.png" alt="Help" style="margin-left:10px; margin-right:10px;"></a></div>
								</td>
								<td>
									<c:choose>
										<c:when test="${form.survey.listForm}">
											<spring:message code="label.Public" />
										</c:when>
										<c:otherwise>
											<spring:message code="label.Private" />
										</c:otherwise>
									</c:choose>									
								</td>
							</tr>							
							<tr>
								<td class="table-label"><spring:message code="label.Captcha" /></td>
								<td>
									<c:choose>
										<c:when test="${form.survey.captcha}">
											<spring:message code="label.Yes" />
										</c:when>
										<c:otherwise>
											<spring:message code="label.No" />
										</c:otherwise>
									</c:choose>				
								</td>
							</tr>
							<tr>
								<td class="table-label">
									<spring:message code="label.SaveAsDraft" />
									<div class="help"><spring:message code="label.AllowSaveAsDraft" /></div>
								</td>
								<td>
									<c:choose>
										<c:when test="${form.survey.saveAsDraft}">
											<spring:message code="label.Yes" />
										</c:when>
										<c:otherwise>
											<spring:message code="label.No" />
										</c:otherwise>
									</c:choose>		
								</td>
							</tr>
							<tr>
								<td class="table-label">
									<spring:message code="label.EditContribution" />
									<div class="help"><spring:message code="label.AllowChangeContribution" /></div>
								</td>
								<td>
									<c:choose>
										<c:when test="${form.survey.changeContribution}">
											<spring:message code="label.Yes" />
										</c:when>
										<c:otherwise>
											<spring:message code="label.No" />
										</c:otherwise>
									</c:choose>		
								</td>
							</tr>
							<tr>
								<td class="table-label">
									<spring:message code="label.DownloadContribution" />
									<div class="help"><spring:message code="label.AllowDownloadContributionPDF" /></div>
								</td>
								<td>
									<c:choose>
										<c:when test="${form.survey.downloadContribution}">
											<spring:message code="label.Yes" />
										</c:when>
										<c:otherwise>
											<spring:message code="label.No" />
										</c:otherwise>
									</c:choose>		
								</td>
							</tr>
						</table>
					</div>
					<div id="prop-tabs-3" class="tab-pane">
						<table class="table table-striped table-bordered">
							<tr>
								<td class="table-label">
									<spring:message code="label.MultiPaging" />
									<div class="help"><spring:message code="label.ShowInSeparatePage" /></div>
								</td>
								<td>
									<c:choose>
										<c:when test="${form.survey.multiPaging}">
											<spring:message code="label.Yes" />
										</c:when>
										<c:otherwise>
											<spring:message code="label.No" />
										</c:otherwise>
									</c:choose>					
								</td>
							</tr>
							<tr>
								<td class="table-label">
									<spring:message code="label.Validation" />
									<div class="help"><spring:message code="label.ValidatedInputPerPage" /></div>
								</td>
								<td>
									<c:choose>
										<c:when test="${form.survey.validatedPerPage}">
											<spring:message code="label.Yes" />
										</c:when>
										<c:otherwise>
											<spring:message code="label.No" />
										</c:otherwise>
									</c:choose>				
								</td>
							</tr>
							<tr>
								<td class="table-label">
									<spring:message code="label.WCAGCompliance" />
									<div class="help"><spring:message code="help.WCAGCompliance" /></div>
								</td>
								<td>
									<c:choose>
										<c:when test="${form.survey.wcagCompliance}">
											<spring:message code="label.Yes" />
										</c:when>
										<c:otherwise>
											<spring:message code="label.No" />
										</c:otherwise>
									</c:choose>				
								</td>
							</tr>
							<tr>
								<td class="table-label"><spring:message code="label.Style" /></td>
								<td>&#160;</td>
							</tr>
							<tr>
								<td style="padding-left: 50px;"><spring:message code="label.Logo" /></td>
								<td>
									<c:if test="${form.survey.logo != null}">
										<img src="<c:url value="/files/${form.survey.uniqueId}/${form.survey.logo.uid}" />" /><br />
										<esapi:encodeForHTML>${form.survey.logo.name}</esapi:encodeForHTML><br />
										<br />
										<c:choose>
											<c:when test="${form.survey.logoInInfo}">
												<spring:message code="label.inInformationArea" />
											</c:when>
											<c:otherwise>
												<spring:message code="label.overTitle" />
											</c:otherwise>
										</c:choose>
									</c:if>
								</td>
							</tr>
							<tr>
								<td style="padding-left: 50px;"><spring:message code="label.Skin" /></td>
								<td>
									<c:choose>
										<c:when test="${form.survey.skin != null}"><esapi:encodeForHTML>${form.survey.skin.name}</esapi:encodeForHTML></c:when>
										<c:otherwise>
											<spring:message code="label.default" />	
										</c:otherwise>
									</c:choose>																				
								</td>
							</tr>
							<tr>
								<td class="table-label"><spring:message code="label.AutomaticNumbering" /></td>
								<td>&#160;</td>
							</tr>
							<tr>
								<td style="padding-left: 50px;"><spring:message code="form.Sections" /></td>
								<td>
									<c:choose>
										<c:when test="${form.survey.sectionNumbering == 0}">
											<spring:message code="label.NoNumbering" />
										</c:when>
										<c:when test="${form.survey.sectionNumbering == 1}">
											<spring:message code="label.Numbers" />
										</c:when>
										<c:when test="${form.survey.sectionNumbering == 2}">
											<spring:message code="label.LettersLowerCase" />
										</c:when>
										<c:when test="${form.survey.sectionNumbering ==3}">
											<spring:message code="label.LettersUpperCase" />
										</c:when>
									</c:choose>																		
								</td>
							</tr>
							<tr>
								<td style="padding-left: 50px;"><spring:message code="form.Questions" /></td>
								<td>							
									<c:choose>
										<c:when test="${form.survey.questionNumbering == 0}">
											<spring:message code="label.NoNumbering" />
										</c:when>
										<c:when test="${form.survey.questionNumbering == 1}">
											<spring:message code="label.Numbers" />
										</c:when>
										<c:when test="${form.survey.questionNumbering == 4}">
											<spring:message code="label.Numbers" /> (<spring:message code="label.ignoreSections" />)
										</c:when>
										<c:when test="${form.survey.questionNumbering == 2}">
											<spring:message code="label.LettersLowerCase" />
										</c:when>
										<c:when test="${form.survey.questionNumbering == 5}">
											<spring:message code="label.LettersLowerCase" /> (<spring:message code="label.ignoreSections" />)
										</c:when>
										<c:when test="${form.survey.questionNumbering ==3}">
											<spring:message code="label.LettersUpperCase" />
										</c:when>
										<c:when test="${form.survey.questionNumbering ==6}">
											<spring:message code="label.LettersUpperCase" /> (<spring:message code="label.ignoreSections" />)
										</c:when>
									</c:choose>								
								</td>
							</tr>
						</table>
					</div>
					<div id="prop-tabs-4" class="tab-pane">
						<table class="table table-striped table-bordered">
							<tr>
								<td class="table-label" style="vertical-align: top"><spring:message code="label.ConfirmationPage" /></td>
								<td>
									<c:choose>
										<c:when test="${form.survey.confirmationPageLink != null && form.survey.confirmationPageLink}">
											${form.survey.confirmationLink}
										</c:when>
										<c:otherwise>
											${form.survey.confirmationPage}
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
							<tr>
								<td class="table-label" style="vertical-align: top"><spring:message code="label.UnavailabilityPage" /></td>
								<td>
									<c:choose>
										<c:when test="${form.survey.escapePageLink != null && form.survey.escapePageLink}">
											${form.survey.escapeLink}
										</c:when>
										<c:otherwise>
											${form.survey.escapePage}
										</c:otherwise>
									</c:choose>
								</td>
							</tr>							
							<tr>
								<td class="table-label" style="vertical-align: top">
									<spring:message code="label.ShowPDFOnUnavailabilityPage" />
									<div class="help"><spring:message code="info.ShowPDFOnUnavailabilityPage" /></div>	
								</td>
								<td>
									<c:choose>
										<c:when test="${form.survey.showPDFOnUnavailabilityPage != null && form.survey.showPDFOnUnavailabilityPage}">
											<spring:message code="label.Yes" />
										</c:when>
										<c:otherwise>
											<spring:message code="label.No" />
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
							<tr>
								<td class="table-label" style="vertical-align: top">
									<spring:message code="label.ShowDocsOnUnavailabilityPage" />
									<div class="help"><spring:message code="info.ShowDocsOnUnavailabilityPage" /></div>	
								</td>
								<td>
									<c:choose>
										<c:when test="${form.survey.showDocsOnUnavailabilityPage != null && form.survey.showDocsOnUnavailabilityPage}">
											<spring:message code="label.Yes" />
										</c:when>
										<c:otherwise>
											<spring:message code="label.No" />
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
						</table>
					</div>
					<div id="prop-tabs-5" class="tab-pane">
						<table class="table table-striped table-bordered">
							<tr>
								<td class="table-label">
									<spring:message code="label.AutomaticPublishing" />
									<div class="help"><spring:message code="info.AutomaticPublishing" /></div>
								</td>
								<td>
									<c:choose>
										<c:when test="${form.survey.automaticPublishing}">
											<spring:message code="label.Yes" />
										</c:when>
										<c:otherwise>
											<spring:message code="label.No" />
										</c:otherwise>
									</c:choose>			
								</td>
							</tr>
							<tr>
								<td style="padding-left: 50px;"><spring:message code="label.StartDate" /></td>
								<td>			
									<c:if test="${form.survey.automaticPublishing && form.survey.start != null}">		
										<esapi:encodeForHTML>${form.survey.startString}</esapi:encodeForHTML>					
									</c:if>
								</td>
							</tr>
							<tr>
								<td style="padding-left: 50px;"><spring:message code="label.ExpiryDate" /></td>
								<td>
									<c:if test="${form.survey.automaticPublishing && form.survey.end != null}">			
										<esapi:encodeForHTML>${form.survey.endString}</esapi:encodeForHTML>
									</c:if>
								</td>
							</tr>
							<tr>
								<td class="table-label">
									<spring:message code="label.EndNotification" />
									<div class="help"><spring:message code="info.EndNotification" /></div>
								</td>
								<td>
										<c:choose>
											<c:when test="${form.survey.notificationValue != null && form.survey.notificationValue.length() > 0}">
												<spring:message code="label.NotifyMe" />: <esapi:encodeForHTML>${form.survey.notificationValue}</esapi:encodeForHTML>
											
												<c:choose>
													<c:when test="${form.survey.notificationUnit == null}">
													
													</c:when>
													<c:when test="${form.survey.notificationUnit == 0}">
														<spring:message code="label.hours" />
													</c:when>
													<c:when test="${form.survey.notificationUnit == 1}">
														<spring:message code="label.days" />
													</c:when>
													<c:when test="${form.survey.notificationUnit == 2}">
														<spring:message code="label.weeks" />
													</c:when>
													<c:when test="${form.survey.notificationUnit == 3}">
														<spring:message code="label.months" />
													</c:when>
												</c:choose>	
												<c:choose>
													<c:when test="${form.survey.notifyAll}"><br />(<spring:message code="label.AllFormManagers" />)</c:when>	
													<c:otherwise><br />(<spring:message code="label.FormCreatorOnly" />)</c:otherwise>
												</c:choose>
												
											</c:when>
											<c:otherwise><spring:message code="label.No" /></c:otherwise>
										</c:choose>
								</td>
							</tr>				
							<tr>
								<td class="table-label"><spring:message code="label.CreateContacts" /></td>
								<td>
									<c:choose>
										<c:when test="${form.survey.registrationForm}">
											<spring:message code="label.Yes" />
										</c:when>
										<c:otherwise>
											<spring:message code="label.No" />
										</c:otherwise>
									</c:choose>		
								</td>
							</tr>
							<tr>
								<td class="table-label"><spring:message code="label.UsefulLinks" /></td>
								<td>
									<c:forEach var="link" items="${form.survey.getAdvancedUsefulLinks()}">
										<div style="margin-top: 5px;" ><a class="link visiblelink" target="_blank" rel="noopener noreferrer" href="<esapi:encodeForHTMLAttribute>${link.value}</esapi:encodeForHTMLAttribute>">${link.key}</a></div>
									</c:forEach>	
								</td>
							</tr>
							<tr>
								<td class="table-label"><spring:message code="label.BackgroundDocuments" /></td>
								<td>
									<c:forEach var="link" items="${form.survey.getBackgroundDocumentsAlphabetical()}">
										<div style="margin-top: 5px;" ><a class="link visiblelink" target="_blank" href="${link.value}">${link.key}</a></div>
									</c:forEach>	
								</td>
							</tr>							
						</table>
					</div>
					<div id="prop-tabs-6" class="tab-pane">
						<spring:message code="label.PublishingURL" />: <a class="visiblelink" target="_blank" href="${serverprefix}publication/${form.survey.shortname}">${serverprefix}publication/${form.survey.shortname}</a>
						<table class="table table-striped table-bordered">
							<tr>
								<td class="table-label"><spring:message code="label.Publish" /></td>
								<td>
									<input <c:if test="${form.survey.publication.showContent}">checked="checked"</c:if> type="checkbox" disabled="disabled" readonly="readonly" class="check" /><spring:message code="label.FullSetOfAnswers" /><br />
									<input <c:if test="${form.survey.publication.showStatistics}">checked="checked"</c:if> type="checkbox" disabled="disabled" readonly="readonly" class="check" /><spring:message code="label.Statistics" /><br />
									<input <c:if test="${form.survey.publication.showSearch}">checked="checked"</c:if> type="checkbox" disabled="disabled" readonly="readonly" class="check" /><spring:message code="label.EnableSearch" /><br />
									<input <c:if test="${form.survey.publication.showUploadedDocuments}">checked="checked"</c:if> type="checkbox" disabled="disabled" readonly="readonly" class="check <c:if test="${!form.survey.hasUploadElement}">hideme</c:if>" /><c:if test="${form.survey.hasUploadElement}"><spring:message code="label.UploadedDocuments" /></c:if>
								</td>
							</tr>
							<tr>
								<td class="table-label"><spring:message code="label.QuestionsToPublish" /></td>
								<td>
									<input <c:if test="${form.survey.publication.allQuestions}">checked="checked"</c:if>  disabled="disabled" readonly="readonly" type="radio" class="check" name="questionsToPublish" /><spring:message code="label.AllQuestions" /><br />
									<input <c:if test="${!form.survey.publication.allQuestions}">checked="checked"</c:if>  disabled="disabled" readonly="readonly" type="radio" class="check" name="questionsToPublish" /><spring:message code="label.Selection" /><br />
									<div class="well">
										<c:forEach items="${form.survey.getQuestions()}" var="question">
											<c:if test="${form.survey.publication.isSelected(question.id)}">
												<c:choose>
													<c:when test="${question.getType() == 'Image'}">
														<div>${question.titleOrFilename()}</div>
													</c:when>
													<c:otherwise>
														<div>${question.title}</div>
													</c:otherwise>
												</c:choose>
											</c:if>										
										</c:forEach>
									</div>
								</td>
							</tr>
							<tr>
								<td class="table-label"><spring:message code="label.Contributions" /></td>
								<td>
									<input <c:if test="${form.survey.publication.allContributions}">checked="checked"</c:if>  disabled="disabled" readonly="readonly" type="radio" class="check" name="contributionsToPublish" /><spring:message code="label.AllContributions" /><br />
									<input <c:if test="${!form.survey.publication.allContributions}">checked="checked"</c:if>  disabled="disabled" readonly="readonly" type="radio" class="check" name="contributionsToPublish" /><spring:message code="label.Selection" /><br />
									<div class="well">
										<c:forEach items="${form.survey.getQuestions()}" var="question">
											<c:choose>
												<c:when test="${question.getType() == 'MultipleChoiceQuestion' || question.getType() == 'SingleChoiceQuestion'}">
													<c:if test="${form.survey.publication.filter.containsQuestion(question.id, question.uniqueId)}">
														<div class="well">
															${question.title}
															<div>
																<c:forEach items="${question.possibleAnswers}" var="possibleanswer" varStatus="status">
																	<c:if test="${form.survey.publication.filter.contains(question.id, question.uniqueId, possibleanswer.id, possibleanswer.uniqueId)}">
																		${possibleanswer.title}<br />
																	</c:if>
																</c:forEach>
															</div>
														</div>
													</c:if>
												</c:when>
											</c:choose>
										</c:forEach>
									</div>
								</td>
							</tr>
							<tr>
								<td class="table-label"><spring:message code="label.Security" /></td>
								<td>
									<c:choose>
										<c:when test="${form.survey.publication.password == null || form.survey.publication.password.length() == 0}">
											<spring:message code="form.Open" />
										</c:when>
										<c:otherwise>
											<spring:message code="form.Secured" />
										</c:otherwise>										
									</c:choose>
								</td>
							</tr>
						</table>
					</div>
					
					<div id="prop-tabs-7" class="tab-pane">
						<table class="table table-striped table-bordered">
							<tr>
								<td class="table-label"><spring:message code="label.EnableQuiz" /></td>
								<td>
									<c:choose>
										<c:when test="${form.survey.isQuiz != null && form.survey.isQuiz}">
											<spring:message code="label.Yes" />
										</c:when>
										<c:otherwise>
											<spring:message code="label.No" />
										</c:otherwise>
									</c:choose>		
								</td>
							</tr>							
							<tr>
								<td class="table-label">
									<spring:message code="label.ShowQuizIcons" />
									<div class="help"><spring:message code="info.ShowQuizIcons" /></div>
								</td>
								<td>
									<c:choose>
										<c:when test="${form.survey.showQuizIcons}">
											<spring:message code="label.Yes" />
										</c:when>
										<c:otherwise>
											<spring:message code="label.No" />
										</c:otherwise>
									</c:choose>		
								</td>
							</tr>							
							<tr>
								<td class="table-label">
									<spring:message code="label.ShowTotalScore" />
									<div class="help"><spring:message code="info.ShowTotalScore" /></div>
								</td>
								<td>
									<c:choose>
										<c:when test="${form.survey.showTotalScore != null && form.survey.showTotalScore}">
											<spring:message code="label.Yes" />
										</c:when>
										<c:otherwise>
											<spring:message code="label.No" />
										</c:otherwise>
									</c:choose>		
								</td>
							</tr>
							<tr>
								<td class="table-label">
									<spring:message code="label.ScoresByQuestion" />
									<div class="help"><spring:message code="info.ScoresByQuestion" /></div>
								</td>
								<td>
									<c:choose>
										<c:when test="${form.survey.scoresByQuestion != null && form.survey.scoresByQuestion}">
											<spring:message code="label.Yes" />
										</c:when>
										<c:otherwise>
											<spring:message code="label.No" />
										</c:otherwise>
									</c:choose>		
								</td>
							</tr>
							<tr>
								<td class="table-label" style="vertical-align: top"><spring:message code="label.WelcomeMessage" /></td>
								<td>
									${form.survey.quizWelcomeMessage}
								</td>
							</tr>
							<tr>
								<td class="table-label" style="vertical-align: top"><spring:message code="label.ResultsMessage" /></td>
								<td>
									${form.survey.quizResultsMessage}
								</td>
							</tr>
						</table>					
					</div>
					
					<div id="prop-tabs-8" class="tab-pane">
						<table class="table table-striped table-bordered">
							<tr>
								<td class="table-label"><spring:message code="label.EnableOPC" /></td>
								<td>
									<c:choose>
										<c:when test="${form.survey.isOPC != null && form.survey.isOPC}">
											<spring:message code="label.Yes" />
										</c:when>
										<c:otherwise>
											<spring:message code="label.No" />
										</c:otherwise>
									</c:choose>		
								</td>
							</tr>
						</table>
					</div>
				</div>
				
				<div style="clear: both"></div>
		</div>
	</div>
	

<jsp:include page="propertiesedit.jsp" />
<jsp:include page="properties-dialogs.jsp" />
<jsp:include page="../footer.jsp" />
	
</body>
</html>
