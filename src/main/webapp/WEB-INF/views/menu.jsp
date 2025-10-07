<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<c:if test="${USER.agreedToToS}">
	<div class="fulltitle">
		<c:if test="${USER!= null && runnermode == null && USER.showAdmin}">
			<div style="float: right">
				<ul class="nav nav-tabs" id="menutab3" style="font-size: 10pt; border-bottom: 0px; margin-top: 6px; margin-bottom: 0px; margin-right: 20px;">
				  	<li style="margin-left: 20px" id="administration-menu-tab"><a href="<c:url value="/administration/system"/>"><span class="glyphicon glyphicon-cog"></span> <spring:message code="label.Administration" /></a></li>
				</ul>
			</div>
	    </c:if>
	
		<table style="margin-left: auto; margin-right: auto;">
			<tr>
			<td>
				<ul class="nav nav-tabs" id="menutab1" style="font-size: 10pt; border-bottom: 0px; margin-top: 6px; margin-bottom: 0px;">
					 <c:if test="${sessioninfo != null}">			  
					 	<li id="form-menu-tab" style="">
					 	  <a class="form-tab" style="margin-top: -2px; padding-top: 6px; padding-bottom: 8px;" href="<c:url value="/${sessioninfo.shortname}/management/overview"/>"><esapi:encodeForHTML>${sessioninfo.getShortnameForMenu()}</esapi:encodeForHTML></a>
					 	 </li>
					 </c:if>
				</ul>
			<td>
			<c:if test="${sessioninfo != null}">	
				<td><div style="width: 28px">&nbsp;</div></td>
			</c:if>
			<td>
				<ul class="nav nav-tabs" id="menutab2" style="font-size: 10pt; border-bottom: 0px; margin-top: 6px; margin-bottom: 0px;">
					  <c:if test="${USER.formPrivilege > 0 }">
						  <li id="my-tab"><a  href="<c:url value="/dashboard"/>" ><span class="glyphicon glyphicon-dashboard"></span> <spring:message code="label.Dashboard" /></a></li>
						  <li id="forms-menu-tab"><a  href="<c:url value="/forms"/>"><span class="glyphicon glyphicon-comment"></span> <spring:message code="label.Surveys" /></a></li>
						  <li id="exports-menu-tab"><a  href="<c:url value="/exports/list"/>"><span class="glyphicon glyphicon-export"></span> <spring:message code="label.Exports" /></a></li>
					  </c:if>
		
					  <c:if test="${USER.contactPrivilege > 0 }">
					  	<li id="addressbook-menu-tab"><a  href="<c:url value="/addressbook"/>"><span class="glyphicon glyphicon-book"></span> <spring:message code="label.AddressBook" /></a></li>
					  </c:if>
					  
					  <c:if test="${USER.getGlobalPrivilegeValue('UserManagement') > 0}">
					  	<li id="settings-menu-tab"><a href="<c:url value="/settings/myAccount"/>"><span class="glyphicon glyphicon-user"></span> <spring:message code="label.Settings" /></a></li>
					  </c:if>
					  
					  <c:if test="${USER.formPrivilege > 0 && USER.canCreateSurveys}">
						  <li id="actions-menu-tab" class="dropdown menudropdown">
						  	<a id="actions-menu-tab-toggle" href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><div><spring:message code="label.NewSurvey" /> <span class="caret" style="margin-left: 5px"></span></div></a>
					          <ul class="dropdown-menu" id="actions-menu-dropdown" style="position: absolute">
					            <li><a id="menuShowCreateSurveyDialogButton" class="menudropdownaction" onclick="showCreateSurveyDialog();"><span class="glyphicon glyphicon-plus" style="margin-right: 10px;"></span><spring:message code="label.CreateNewSurvey2" /></a></li>
					            <li><a class="menudropdownaction" onclick="$('.qq-upload-fail').remove();$('#import-survey-dialog').modal('show');"><span class="glyphicon glyphicon-import" style="margin-right: 10px;"></span><spring:message code="label.ImportSurvey" /></a></li>
							  </ul>
						  </li>
					  </c:if>
				</ul>
			</td>
			</tr>
		</table>	
	</div>
</c:if>

<%@ include file="import-survey-dialog.jsp" %>	

<%@ include file="generic-messages.jsp" %>

<div class="modal" id="download-answer-pdf-dialog" role="dialog">
	<div class="modal-dialog" role="document">
    <div class="modal-content">
	<div class="modal-body">	
		<span id="download-answer-pdf-dialog-running"><spring:message code="info.FileCreation" /></span>
		<span id="download-answer-pdf-dialog-ready" class="hideme"><spring:message code="info.FileCreated" /></span>
		<div id="download-answer-pdf-dialog-spinner" class="dialog-wait-image" style="padding: 30px;"></div><br /><br />
		
		<div id="download-answer-pdf-dialog-error"><spring:message code="error.OperationFailed" /></div>
	</div>
	<div class="modal-footer">
		<a onclick="$('#download-answer-pdf-dialog').modal('hide')" id="download-answer-pdf-dialog-result" target="_blank" class="btn btn-primary"><spring:message code="label.Download" /></a>
		<a onclick="$('body').scrollTop($('#download-answer-pdf-dialog').attr('data-scrolltop'))" rel="tooltip" title="<spring:message code="label.Cancel" />" data-dismiss="modal" class="btn btn-default"><spring:message code="label.Cancel" /></a>			
	</div>
	</div>
	</div>
</div>

<script type="text/javascript" src="${contextpath}/resources/js/menu.js?version=<%@include file="version.txt" %>"></script>

<script type="text/javascript">
		
	var surveyID;
	
	$(function() {	
		var spinner = new Spinner().spin();
		$("#download-answer-pdf-dialog-spinner").append(spinner.el);	
				
		$("#closeSurveyTabAction").click(function(){
			window.location.href = "${contextpath}/noform/management/closeCurrentForm";
		});
		
		<c:if test="${responsive != null}">
			$("#responsiveinfo-dialog").modal("show");
		</c:if>
		
		<c:if test="${imported != null}">
			surveyID = '${imported}';
			
			<c:if test="${invalidCodeFound != null}">
				$("#import-survey-dialog-2-invalidCodeFound").show();
			</c:if>	
			
			$("#import-survey-dialog-2").modal("show");
		</c:if>
	});
	
	function goToSurvey()
	{
		window.location.href = "${contextpath}/" + surveyID + "/management/overview";
	}
	
	function reloadSurveys()
	{		
		//dummy; please do not remove
	}
	
	// global boolean variable in java script 
	// if true check fornoew exports  
	window.checkExport = $.parseJSON("${CHECK_EXPORT != null ? CHECK_EXPORT : false}");
	window.setTimeout("checkNewExports()", 4000);
	
	window.setTimeout("checkNewMailTasks()", 10000);
	
	function checkSurveyTypes(){
		let surveyType = $("[name='new-survey-type']:checked").val()
		if (surveyType == "opc" || surveyType == "evote"){
			$("#new-survey-security-secured").prop("checked", "checked");
			$("#new-survey-security-secured").attr("disabled", "disabled");
			$("#new-survey-security-open").attr("disabled", "disabled");
		} else {
			$("#new-survey-security-open").prop("checked", "checked");
			$("#new-survey-security-secured").removeAttr("disabled");
			$("#new-survey-security-open").removeAttr("disabled");
		}

		if (surveyType == "opc"){
			$("#new-survey-contact-type").val("email");
			checkNewSurveyContactType();
		}
		
		if (surveyType == "evote") {
			$("#evote-template").show();
		} else {
			$("#evote-template").hide();
		}
	}
	
	function checkNewSurveyContactType()
	{
		if ($("#new-survey-contact-type").val() != "url")
		{
			$("#new-survey-contact").removeClass("url");	
			$("#new-survey-contact").addClass("email");
			$("#new-survey-contact-label").hide();
			$("#new-survey-contact-label-label").hide();
		} else {
			$("#new-survey-contact").removeClass("email");	
			$("#new-survey-contact").addClass("url");
			$("#new-survey-contact-label").show();
			$("#new-survey-contact-label-label").show();
			$("#new-survey-contact-label").parent().find(".validation-error").remove();
		}
	}
	
	function checkNewMailTasks()
	{
		<c:if test="${sessioninfo.shortname != null}">
			var now = new Date();
			var dif = now.getTime() - lastEditDate.getTime();
			if (dif > 30 * 60 * 1000) //longer than 30 minutes
			{
				return;
			}		
		
			var request = $.ajax({
				  url: "${contextpath}/exports/checkNewMails",
				  dataType: "json",
				  cache: false,
				  success: function(task)
				  {
					  if (task.state != "NONE")
					  {											  
						  if (task.state != "ERROR")
						  {
							  showInfo(task.message);
						  } else {
							  showError(task.message);
						  }
						  window.setTimeout("checkNewMailTasks()", 10000);
					  } else {
						  window.setTimeout("checkNewMailTasks()", 60000);
					  }
				  }, error: function (data) {
						showAjaxError(data.status)
					}
				});	

		</c:if>
	}
	
	function checkNewExports()
	{
		if(window.checkExport) 
		{
			var now = new Date();
			var dif = now.getTime() - lastEditDate.getTime();
			if (dif > 30 * 60 * 1000) //longer than 30 minutes
			{
				return;
			}
			
			var request = $.ajax({
			  url: "${contextpath}/exports/checkNew",
			  data: {uid : "${USER.id}"},
			  dataType: "json",
			  cache: false,
			  success: function(data)
			  {
				  showExportMessages(data);

				  // if need to check again for new exports
				  window.checkExport = data.checkExport;
			  }
			});
		}
		window.setTimeout("checkNewExports()", 60000);
	}

	const sleep = (delay) => new Promise((resolve) => setTimeout(resolve, delay))
	async function showExportMessages(data) {
		for (var i = 0; i < data.newnames.length; i++) {
			var s = '<spring:message code="label.Export" />&nbsp;<b>' + data.newnames[i].newname + '</b> <spring:message code="label.availableForDownload" />. <spring:message code="label.GoTo" />&nbsp;<a class="visiblelink" href="<c:url value="/exports/list"/>"><spring:message code="label.ExportPage" /></a>';
			console.log("Success message")
			showSuccess(s);

			await sleep(2000);
		}
	}
	
	function importCopySurvey(login, email)
	{
		$('#create-survey-uuid').val(uuid);
		$('#create-survey-original').val("");
		$('#new-survey-title').val(title);
		$('#new-survey-security-open').attr('checked','checked');
		$("#add-survey-dialog").find(".hideimport").hide();		
		$('#add-survey-dialog').find("th").first().text("<spring:message code="label.Import" />");
		$('#add-survey-dialog').find("#importbtn").show();
		$('#add-survey-dialog').find("#createbtn").hide();
		$('#add-survey-dialog').modal();
	}
	
	function copySurvey(id, title, lang, security, isQuiz, isDelphi, isEVote, eVoteTemplate, isSelfAssessment)
	{
		let login = '${USER.login}';
		let contact = '${USER.email}';
			
		$('#new-survey-shortname').val(getNewSurveyId(login));
		$('#create-survey-uuid').val("");
		$('#create-survey-original').val(id);
		
		$('#new-survey-title').val(title + "_copy");
		$('#new-survey-language').val(lang);
		$('#new-survey-contact').val(contact);
		$('#new-survey-contact-label').val('');
		
		$("#add-survey-dialog").find(".hideimport").show();
		$("#add-survey-dialog").find(".hidecreate").show();
		$("#add-survey-dialog").find(".hidecopy").hide();
		
		$('#add-survey-dialog').find("#importbtn").hide();
		$('#add-survey-dialog').find("#createbtn").show();
		
		if (security.indexOf("open") == -1)
		{
			$('#new-survey-security-secured').attr('checked','checked');
		} else {
			$('#new-survey-security-open').attr('checked','checked');
		}
		
		if (isQuiz == 'true')
		{
			$("#new-survey-type-selfassessment").closest("label").removeClass("active");
			$("#new-survey-type-normal").closest("label").removeClass("active");
			$("#new-survey-type-delphi").closest("label").removeClass("active");
			$("#new-survey-type-evote").closest("label").removeClass("active");
			$("#new-survey-type-quiz").closest("label").addClass("active");
			$("#new-survey-type-quiz").attr("checked", "checked");
		} else  if (isDelphi == 'true') {
			$("#new-survey-type-selfassessment").closest("label").removeClass("active");
			$("#new-survey-type-normal").closest("label").removeClass("active");
			$("#new-survey-type-quiz").closest("label").removeClass("active");
			$("#new-survey-type-evote").closest("label").removeClass("active");
			$("#new-survey-type-delphi").closest("label").addClass("active");
			$("#new-survey-type-delphi").attr("checked", "checked");
		} else  if (isSelfAssessment == 'true') {
			$("#new-survey-type-normal").closest("label").removeClass("active");
			$("#new-survey-type-quiz").closest("label").removeClass("active");
			$("#new-survey-type-evote").closest("label").removeClass("active");
			$("#new-survey-type-delphi").closest("label").removeClass("active");
			$("#new-survey-type-selfassessment").closest("label").addClass("active");
			$("#new-survey-type-selfassessment").attr("checked", "checked");
		} else if (isEVote == "true") {
			$("#new-survey-type-selfassessment").closest("label").removeClass("active");
			$("#new-survey-type-normal").closest("label").removeClass("active");
			$("#new-survey-type-quiz").closest("label").removeClass("active");
			$("#new-survey-type-delphi").closest("label").removeClass("active");
			$("#new-survey-type-evote").closest("label").addClass("active");
			$("#new-survey-type-evote").attr("checked", "checked");
			$("#evote-template input[value='" + eVoteTemplate +"']").attr("checked", "checked");
			$("#new-survey-security-secured").prop("checked", "checked");
			$("#new-survey-security-secured").attr("disabled", "disabled");
			$("#new-survey-security-open").attr("disabled", "disabled");
			$("#evote-template").show();
		} else {
			$("#new-survey-type-selfassessment").closest("label").removeClass("active");
			$("#new-survey-type-quiz").closest("label").removeClass("active");
			$("#new-survey-type-delphi").closest("label").removeClass("active");
			$("#new-survey-type-evote").closest("label").removeClass("active");
			$("#new-survey-type-normal").closest("label").addClass("active");
			$("#new-survey-type-normal").attr("checked", "checked");
		}

		$('#add-survey-dialog').modal();
	}
	
	function showCreateSurveyDialog()
	{
		$('#create-survey-uuid').val("");
		$('#create-survey-original').val("");
		
		$('#new-survey-shortname').val(getNewSurveyId('${USER.login}'));
		$('#new-survey-title').val('');
		$('#new-survey-security-open').attr('checked','checked');
		$("#new-survey-quiz-no").prop("checked", "checked");
		$('#new-survey-language').val("${USER.defaultPivotLanguage}");
		$('#new-survey-contact-type').val("form");
		$('#new-survey-contact').val("${USER.email}");
		$('#new-survey-contact-label').val("");
		$("#add-survey-dialog").find(".hideimport").show();
		$("#add-survey-dialog").find(".hidecopy").show();
		$("#add-survey-dialog").find(".hidecreate").hide();		
		$('#add-survey-dialog').find("th").first().text("<spring:message code="label.New" />");
		$('#add-survey-dialog').find("#importbtn").hide();
		$('#add-survey-dialog').find("#createbtn").show();
		$('#add-survey-dialog').modal();

	}
	
	function checkShortname(name)
	{
		return checkShortname(name,-1);
	}
	
	function checkShortname(name, id)
	{
		var s = "id=" + id + "&name=" + name;
		
		var result = $.ajax({
			type:'GET',
			  url: "${contextpath}/forms/shortnameexistsjson",
			  dataType: 'json',
			  data: s,
			  cache: false,
			  async: false
		}).responseText;

		return result == "false";
	}
	
	function checkOrganisation(email, organisation)
	{
		var s = "email=" + email + "&organisation=" + organisation;
		
		var result = $.ajax({
			type:'GET',
			  url: "${contextpath}/forms/emailmatchesorganiation",
			  dataType: 'json',
			  data: s,
			  cache: false,
			  async: false
		}).responseText;

		return result == "true";
	}

	// Handle scrollability of multiple open modals in bootstrap
	$('body').on('hidden.bs.modal', function () {
		if($('.modal.in').length > 0)
		{
			$('body').addClass('modal-open');
		}
	});
	
	function checkValidator() {
		let organisation = $('#new-survey-organisation').val();
		if (organisation != "" && organisation != "CITIZEN" && organisation != "OTHER" && organisation != "PRIVATEORGANISATION" && organisation != "PUBLICADMINISTRATION") {
			 $('#new-survey-validator-div').show();
			 $('#new-survey-validator').addClass("required");
		} else {
			$('#new-survey-validator-div').hide();
			$('#new-survey-validator').removeClass("required");
		}
	}
</script>

<div class="modal" id="add-survey-dialog" data-backdrop="static">
	<div class="modal-dialog modal-lg">
    <div class="modal-content">
	<div class="modal-header">
		<span class="hidecopy hidecreate"><spring:message code="label.ImportSurvey" /></span>
		<span class="hideimport hidecopy"><spring:message code="label.NewSurvey" /></span>
		<span class="hideimport hidecreate"><spring:message code="label.CopySurvey" /></span>		
	</div>
	<div class="modal-body" style="padding: 10px; text-align: left;">
		<table class="table table-striped table-bordered" id="new-survey-table">
			<tbody>
				<tr class="hideimport">
					<td class="table-label"><span class="mandatory">*</span><spring:message code="label.Type" /></td>
					<td>					
						<div class="btn-group" data-toggle="buttons">
						  <label class="btn btn-default active">
							<img style="height: 16px" src="${contextpath}/resources/images/icons/64/survey.png" />
						    <input checked="checked" onchange="checkSurveyTypes()" type="radio" name="new-survey-type" id="new-survey-type-normal" value="normal" />&#160;<spring:message code="label.StandardSurvey" />
						  </label>
						  <label class="btn btn-default" title="<spring:message code="info.Quiz" />" aria-label="<spring:message code="info.Quiz" />" data-toggle='tooltip'>
						    <span class="glyphicon glyphicon-education" style="font-size: 15px"></span>
						    <input type="radio" onchange="checkSurveyTypes()" name="new-survey-type" id="new-survey-type-quiz" value="quiz" />&#160;<spring:message code="label.Quiz" />
						  </label>
						  <c:if test="${enableopc && USER.getGlobalPrivilegeValue('ECAccess') > 0}">
							  <label class="btn btn-default hidecopy" title="<spring:message code="info.OPC" />" aria-label="<spring:message code="info.OPC" />" data-toggle='tooltip'>
							    <img style="height: 18px;" src="${contextpath}/resources/images/icons/24/people.png">
							    <input type="radio" onchange="checkSurveyTypes()" name="new-survey-type" id="new-survey-type-opc" value="opc" />&#160;<spring:message code="label.OPC" />
							  </label>
						  </c:if>
						 <c:if test="${enableecf}">
							<label class="btn btn-default hidecopy" title="<spring:message code="info.ECF" />" aria-label="<spring:message code="info.ECF" />" data-toggle='tooltip'>
								<img style="height: 18px;" src="${contextpath}/resources/images/icons/24/table.png">
								<input type="radio" onchange="checkSurveyTypes()" name="new-survey-type" id="new-survey-type-ecf" value="ecf" />&#160;<spring:message code="label.ECF" />
						  	</label>
						  </c:if>
						  <c:if test="${enableselfassessment}">
							<label class="btn btn-default" title="<spring:message code="info.SelfAssessment" />" aria-label="<spring:message code="info.SelfAssessment" />" data-toggle='tooltip'>
								<img style="height: 18px;" src="${contextpath}/resources/images/icons/24/table.png">
								<input type="radio" onchange="checkSurveyTypes()" name="new-survey-type" id="new-survey-type-selfassessment" value="selfassessment" />&#160;<spring:message code="label.SelfAssessment" />
							</label>
					      </c:if>
						  <c:if test="${enabledelphi}">
								<label class="btn btn-default" title="<spring:message code="info.Delphi" />" aria-label="<spring:message code="info.Delphi" />" data-toggle='tooltip'>
									<img style="height: 18px;" src="${contextpath}/resources/images/icons/24/delphi.png">
									<input type="radio" onchange="checkSurveyTypes()" name="new-survey-type" id="new-survey-type-delphi" value="delphi" />&#160;<spring:message code="label.Delphi" />
								</label>
					      </c:if>
					      <c:if test="${enableevote}">
								<label class="btn btn-default" title="<spring:message code="info.eVote" />" aria-label="<spring:message code="info.eVote" />" data-toggle='tooltip'>
									<span class="glyphicon glyphicon-ok" style="font-size: 11px; border: 2px solid #555; padding: 3px;"></span>
									<input type="radio" onchange="checkSurveyTypes()" name="new-survey-type" id="new-survey-type-evote" value="evote" />&#160;<spring:message code="label.eVote" />
								</label>
					      </c:if>
						</div>
					</td>
				</tr>
				<tr id="evote-template" style="display: none">
					<td class="table-label"><span class="mandatory">*</span><spring:message code="label.Template" /></td>
					<td>
					    <c:if test="${enableevotebru}">
						    <input type="radio" name="new-survey-template" value="b" />&#160;<spring:message code="label.Brussels" />
						</c:if>
						<c:if test="${enableevoteispra}">
						    <input type="radio" name="new-survey-template" value="i" style="margin-left: 20px;" />&#160;<spring:message code="label.IspraSeville" />
						</c:if>
						<c:if test="${enableevotelux}">
						    <input type="radio" name="new-survey-template" value="l" style="margin-left: 20px;" />&#160;<spring:message code="label.Luxembourg" />
						</c:if>
						<c:if test="${enableevoteoutside}">
						    <input type="radio" name="new-survey-template" value="o" style="margin-left: 20px;" />&#160;<spring:message code="label.OutsideCommunity" />
						</c:if>
						<c:if test="${enableevotestandard}">
						    <input type="radio" name="new-survey-template" value="p" style="margin-left: 20px;" />&#160;<spring:message code="label.Standard" />
						</c:if>
					</td>
				</tr>				
				<tr>
					<td class="table-label"><span class="mandatory">*</span><spring:message code="label.UniqueIdentifier" /></td>
					<td>
						<input id="new-survey-shortname" maxlength="255" class="required freetext max255 form-control" type="text" value="" style="width: 300px; margin-left: 0; display: inline;" placeholder="<spring:message code="message.SpecifyShortname" />" />
						<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="message.MeaningfulShortname" /><spring:message code="message.MeaningfulShortname2" />"><i class="glyphicon glyphicon-info-sign"></i></span>
						<div id="new-survey-shortname-exists" class="hideme alert-danger" style=" margin-top: 10px; "><spring:message code="message.ShortnameAlreadyExists" /></div>
					</td>
				</tr>
				<tr>
					<td class="table-label"><span class="mandatory">*</span><spring:message code="label.Title" /></td>
					<td>
						<textarea class="tinymcealign2 required xhtml freetext max2000" id="new-survey-title"></textarea>
					</td>
				</tr>
				
				<c:if test="${enablechargeback == 'true'}">
				
					<tr>
						<td class="table-label"><span class="mandatory">*</span><spring:message code="label.Organisation" /></td>
						<td>					
							<div>
								<spring:message code="label.SurveyOnBehalf" /><span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="info.SurveyOnBehalf" />"><i class="glyphicon glyphicon-info-sign"></i></span>
								
								<c:choose>
									<c:when test="${USER.isExternal() || USER.type == 'SYSTEM'}">
										<select class="form-control new-survey-organisation" id="new-survey-organisation" style="width: auto; min-width: 200px" onchange="checkValidator()">
									</c:when>
									<c:otherwise>
										<input type="hidden" id="new-survey-organisation" class="new-survey-organisation" />
										<select class="form-control new-survey-organisation" style="width: auto; min-width: 200px" disabled="disabled" onchange="checkValidator()">
									</c:otherwise>
								</c:choose>							
								
									  	<optgroup id="new-survey-organisation-dgs" label="<spring:message code="label.EuropeanCommission" />: <spring:message code="label.DGsAndServices" />">
									    </optgroup>
									
									    <optgroup id="new-survey-organisation-aex" label="<spring:message code="label.EuropeanCommission" />: <spring:message code="label.ExecutiveAgencies" />">
									    </optgroup>
									    
									    <optgroup id="new-survey-organisation-euis" label="<spring:message code="label.OtherEUIs" />">
									    </optgroup>
									    
									    <optgroup id="new-survey-organisation-noneuis" label="<spring:message code="label.NonEUIentities" />">
									    </optgroup>
								</select>
							</div>
							
							<div style="margin-top: 10px; font-size: 90%; padding-left: 10px;">
								<span style="vertical-align: top">
									<b><spring:message code="label.Owner" /></b>: ${USER.getFirstLastName()} (${USER.email})
								</span>
								<c:if test="${USER.isExternal() || USER.type == 'SYSTEM'}">
									<div id="new-survey-validator-div" style="display: inline-block; margin-left: 40px"> 
										<span>
											<span class="mandatory">*</span><spring:message code="label.EmailValidator" /><span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="info.EmailValidator" />"><i class="glyphicon glyphicon-info-sign"></i></span>
											<input type="text" class="form-control required email" id="new-survey-validator" style="width: 300px; display: inline;" />
											<div id="new-survey-validator-invalid" class="hideme validation-error-keep" style=" margin-top: 10px; "><spring:message code="message.ValidatorInvalid" /></div>
										</span>
									</div>
								</c:if>
							</div>						
						</td>
					</tr>
				
				</c:if>
				
				<tr>
					<td class="table-label"><span class="mandatory">*</span><spring:message code="label.Contact" /></td>
					<td>
						<div style="float:left; text-align: right;">
							<select onchange="checkNewSurveyContactType()" class="form-control" id="new-survey-contact-type" style="width: auto;">
								<option value="form" selected="selected"><spring:message code="label.ContactForm" /></option>
								<option value="email"><spring:message code="label.Email" /></option>
								<option value="url"><spring:message code="label.Webpage" /></option>								
							</select>
							<div id="new-survey-contact-label-label" style="display:none; font-weight: bold; margin-top: 10px;"><spring:message code="label.Label" /></div>
						</div>
						<div style="float:left; margin-left: 10px">
							<input class="required email form-control" style="min-width: 300px" maxlength="255" id="new-survey-contact" value="<esapi:encodeForHTMLAttribute>${USER.email}</esapi:encodeForHTMLAttribute>" type="text" />
							<input type="text" class="form-control" style="display:none; margin-top: 10px;" id="new-survey-contact-label" />
						</div>
						<div style="float:left; margin-left:5px; margin-top: 3px;">
							<span data-toggle="tooltip" class="iconbutton" rel="tooltip" data-html="true" data-placement="right" title="<spring:message code="message.Contact" />"><i class="glyphicon glyphicon-info-sign"></i></span>
						</div>
						<div style="clear: both;"></div>
						
					</td>
				</tr>	
				<tr class="hideimport">
					<td class="table-label"><spring:message code="label.Security" /></td>
					<td>
						<input class="required check" type="radio" name="new-survey-security" id="new-survey-security-open" value="open" /><spring:message code="form.Open" />&#160;
						<input class="required check" style="margin-left: 20px;" type="radio" name="new-survey-security" id="new-survey-security-secured" value="secured" /><spring:message code="form.Secured" />&#160;
					</td>
				</tr>
				<tr class="hideme">
					<td class="table-label"><span class="mandatory">*</span><spring:message code="label.Privacy" /></td>
					<td>
						<input checked="checked" class="required check" type="radio" name="new-survey-privacy" id="new-survey-privacy-secured" value="" /><spring:message code="form.Privacy.Identified" />&#160;
						<input class="required check" type="radio" name="new-survey-privacy" id="new-survey-privacy-anonymous" value="anonymous" /><spring:message code="form.Privacy.Anonymous" />
					</td>
				</tr>
				<tr class="hideimport hidecopy">
					<td class="table-label"><spring:message code="label.Language" /></td>
					<td>
						<select class="required form-control" style="width: 300px" name="new-survey-language" id="new-survey-language">						
							<c:forEach items="${languages}" var="language">				
								<c:if test="${language.official}">		
									<c:choose>
										<c:when test="${language.code.equalsIgnoreCase('en')}">
											<option selected="selected" value="<esapi:encodeForHTMLAttribute>${language.code}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML><spring:message code="label.lang.${language.englishName}" /></esapi:encodeForHTML></option>
										</c:when>
										<c:otherwise>
											<option value="<esapi:encodeForHTMLAttribute>${language.code}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML><spring:message code="label.lang.${language.englishName}" /></esapi:encodeForHTML></option>
										</c:otherwise>
									</c:choose>
								</c:if>
							</c:forEach>
						</select>				
					</td>
				</tr>
				<c:if test="${oss != true}">
					<tr>
						<td class="table-label"><span class="mandatory">*</span><spring:message code="label.Confirmation" /></td>
						<td style="padding-left: 35px">
							<div style="float: left; margin-left: -25px;">
								<input class="required check" type="checkbox" name="radio-new-survey-audience" value="1" />
							</div>
							<spring:message code="message.highaudiencenew" />
						</td>
					</tr>
				</c:if>
				<c:if test="${!USER.isECUser()}">
					<tr>
						<td class="table-label"><span class="mandatory">*</span><spring:message code="label.DPA" /></td>
						<td style="padding-left: 35px">
							<div style="float: left; margin-left: -25px;">
								<input class="required check" type="checkbox" name="radio-new-survey-dpa" value="1" />
							</div>
							<spring:message code="message.dpanew" arguments="${contextpath}/home/dpa"/>
						</td>
					</tr>
				</c:if>
				<tr>
					<td class="table-label"><span class="mandatory">*</span><spring:message code="label.TOS" /></td>
					<td style="padding-left: 35px">
						<div style="float: left; margin-left: -25px;">
							<input class="required check" type="checkbox" name="radio-new-survey-tos" value="1" />
						</div>
						<spring:message code="message.tos" arguments="${contextpath}/home/tos"/>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	<div class="modal-footer">
		<img alt="wait animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
		<a id="createbtn" onclick="createNewSurvey();" class="btn btn-primary"><spring:message code="label.Create" /></a>
		<a id="importbtn" onclick="createNewSurvey();" class="btn btn-primary hideme"><spring:message code="label.Import" /></a>
		<a  onclick="resetValidationErrors($('#new-survey-table'))" class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>			
	</div>	
	</div>
	</div>
</div>	
	
<form:form id="create-survey" method="POST" action="${contextpath}/noform/management/createNewSurvey" style="display: none">
	<input type="hidden" name="shortname" id="create-survey-shortname" value="" />
	<input type="hidden" name="uuid" id="create-survey-uuid" value="" />
	<input type="hidden" name="original" id="create-survey-original" value="" />
	<input type="hidden" name="security" id="create-survey-security" value="" />
	<input type="hidden" name="audience" id="create-survey-audience" value="" />
	<input type="hidden" name="surveylanguage" id="create-survey-language" value="" />
	<textarea style="display: none;" name="title" id="create-survey-title"></textarea>	
	<input type="hidden" name="listform" id="create-survey-listform" value="" />
	<input type="hidden" name="quiz" id="create-survey-quiz" value="" />
	<input type="hidden" name="opc" id="create-survey-opc" value="" />
	<input type="hidden" name="delphi" id="create-survey-delphi" value="" />
	<input type="hidden" name="ecf" id="create-survey-ecf" value="" />
	<input type="hidden" name="selfassessment" id="create-survey-selfassessment" value="" />
	<input type="hidden" name="evote" id="create-survey-evote" value="" />
	<input type="hidden" name="evotetemplate" id="create-survey-template" value="" />
	<input type="hidden" name="contact" id="create-survey-contact" value="" />
	<input type="hidden" name="contactlabel" id="create-survey-contact-label" value="" />
	<input type="hidden" name="organisation" id="create-survey-organisation" value="" />
	<input type="hidden" name="validator" id="create-survey-validator" value="" />
	<input type="hidden" name="origin" value="<esapi:encodeForHTMLAttribute>${origin}</esapi:encodeForHTMLAttribute>" />
</form:form>

	<div class="modal" id="delete-survey-dialog" data-backdrop="static">
		<div class="modal-dialog">
   		<div class="modal-content">
		<div class="modal-body">
			<spring:message code="question.DeleteSurvey" />
		</div>
		<div class="modal-footer">
			<img class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a id="deleteSurveyYesBtn"  onclick="deleteSurvey();" class="btn btn-primary" data-dismiss="modal"><spring:message code="label.Yes" /></a>
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
		</div>
		</div>
		</div>
	</div>
	
	<div class="modal" id="archive-survey-dialog" data-backdrop="static">
		<div class="modal-dialog">
    	<div class="modal-content">
		<div class="modal-body">
			<spring:message code="question.ArchiveSurvey" />
		</div>
		<div class="modal-footer">
			<img class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a id="archiveSurveyYesBtn" onclick="$('#archive-survey-dialog').modal('hide');$('#generic-wait-dialog').modal('show');"  class="btn btn-primary"><spring:message code="label.Yes" /></a>
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
		</div>
		</div>
		</div>
	</div>
	
	<div id="freeze-default-text" style="display: none">
		Dear Sir or Madam,<br /><br />
		Your survey has been frozen due to infringement of our policy.<br /><br />
		[SURVEYDATA]
		<br /><br />
		Please refer to the EUSurvey <a href="${serverprefix}/home/tos">Terms of Service</a> or contact the EUSurvey <a href="${serverprefix}/home/support">Support Team</a> for more information.
	</div>
	
	<form:form id="unfreeze-form" class="hidden" method="POST" action="${contextpath}/administration/unfreezesurvey">
		<input type="hidden" id="unfreezeSurveyId" name="surveyId" />
	</form:form>
	
	<div class="modal" id="freeze-survey-dialog" data-backdrop="static">
		<div class="modal-dialog">
	   		<div class="modal-content">
	   			<form:form id="freeze-form" method="POST" action="${contextpath}/administration/freezesurvey">
	   				<input type="hidden" id="freezeSurveyId" name="surveyId" />
			   		<div class="modal-header">
			   			<spring:message code="label.FreezeSurvey" />
			   		</div>
					<div class="modal-body">
						<spring:message code="info.freeze" />:<br />
						<table style="margin-top: 10px; margin-bottom: 10px">
							<tr> 
			 					<td style="font-weight: bold; padding-right: 10px; vertical-align: top;"><spring:message code="label.Title" />:</td> 
			 					<td><span id="freezeTitle"></span></td> 
			 				</tr> 
						</table>
									
						<textarea id="freezeEmailText" name="emailText" class="tinymce" style="height: 200px">
							
						</textarea><br />
						<input type="checkbox" class="check" id="freezeCheck" /> <spring:message code="label.confirmfreeze" />
						<div id="freezeCheckError" style="color: #f00; display: none"><spring:message code="error.activateCheckbox" /></div>
						<br /><br />
						<span style="color: #f00;"><spring:message code="info.freezeemail" /></span>
					</div>
					<div class="modal-footer">
						<img class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
						<a id="freezeSurveyYesBtn"  onclick="freezeSurvey();" class="btn btn-primary"><spring:message code="label.FreezeSurvey" /></a>
						<a class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
					</div>
				</form:form>
			</div>
		</div>
	</div>
	
	<c:if test="${missingFiles != null}">
	
		<div class="modal" id="missingFilesDuringArchivingDialog" data-backdrop="static">
			<div class="modal-dialog">
		    <div class="modal-content">
			<div class="modal-header"><spring:message code="label.ProblemDuringArchiving" /></div>
			<div class="modal-body" style="max-height: 800px; overflow: auto;">	
			 	<spring:message code="info.missingFilesDuringArchiving" /><br /><br />
			 	
			 	<table class="table table-bordered">
			 		<tr>
			 			<th><spring:message code="label.FileName" /></th>
			 			<th><spring:message code="label.Type" /></th>
			 		</tr>
			 		<c:forEach items="${missingFiles}" var="file">
			 			<tr>
			 				<td>${file.key}</td>
			 				<td>${file.value}</td>		 				
			 			</tr>
			 		</c:forEach>
			 	</table>
			</div>
			<div class="modal-footer">
				<a id="acceptMissingFilesButton"  class="btn btn-primary"><spring:message code="label.Archive" /></a>
				<a  class="btn btn-default" onclick="$(this).closest('.modal').modal('hide');"><spring:message code="label.Cancel" /></a>
			</div>
			</div>
			</div>
		</div>
		
		<script>
			$("#missingFilesDuringArchivingDialog").modal("show");
			$("#acceptMissingFilesButton").attr("href",	contextpath + "/noform/management/exportSurvey/true/${missingFilesSurvey}?delete=true&acceptMissingFiles=true");
		</script>	

	</c:if>
	
	<script>
		$('[data-toggle="tooltip"]').tooltip({
		    trigger : 'hover',
		    container: 'body'
		});
		
		var url = "/utils/Organisations";
		var userOrganisation = '${USER.organisation}';

		$('input[name="new-survey-template"]').first().attr("checked", "checked");

		$.ajax({type: "GET",
			url: contextpath + url,
			async: false,
		    success :function(result)
		    {		    	
		    	$.each(result.dgs, function(key, data){
		    		var option = document.createElement("option");
		    		$(option).attr("value", key).append(data);
		    		if (userOrganisation == key) {
		    			$(option).attr("selected", "selected");
		    		}
		    		$('#new-survey-organisation-dgs').append(option);
		    	});	
		    	
		    	$.each(result.executiveAgencies, function(key, data){
		    		var option = document.createElement("option");
		    		$(option).attr("value", key).append(data);
		    		if (userOrganisation == key) {
		    			$(option).attr("selected", "selected");
		    		}
		    		$('#new-survey-organisation-aex').append(option);
		    	});	
		    	
		    	$.each(result.otherEUIs, function(key, data){
		    		var option = document.createElement("option");
		    		$(option).attr("value", key).append(data);
		    		if (userOrganisation == key) {
		    			$(option).attr("selected", "selected");
		    		}
		    		$('#new-survey-organisation-euis').append(option);
		    	});	
		    	
		    	$.each(result.nonEUIs, function(key, data){
		    		var option = document.createElement("option");
		    		$(option).attr("value", key).append(data);
		    		if (userOrganisation == key) {
		    			$(option).attr("selected", "selected");
		    		}
		    		$('#new-survey-organisation-noneuis').append(option);
		    	});	
		    	
		    	var organisation = "${USER.organisationCode}";
		    	if (organisation.length == 0 || organisation == 'external') organisation = "OTHER";
		    	$('.new-survey-organisation').val(organisation);
		    	
		    	checkValidator();
		    }
		 });
	</script>
