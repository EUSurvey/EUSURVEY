<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Participants" /></title>
	
	<%@ include file="../includes.jsp" %>
	
	<link href="${contextpath}/resources/css/management.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/simpletree.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/progressbar.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	
	<script>
		var surveyuid = '${form.survey.uniqueId}';
		var attributeIDs = new Array();
		var attributeNames = new Array();
		
		<c:forEach items="${attributeNames}" var="attributeName" varStatus="rowCounter">
			attributeIDs.push(${attributeName.id});		
			attributeNames.push("${attributeName.name}");	
		</c:forEach>
		
		var errorMaxTokenNumberExceeded = "<spring:message code="error.MaxTokenNumberExceeded" />";
		var errorProblemDuringSave = "<spring:message code="error.ProblemDuringSave" />";
	</script>
	
	<script type="text/javascript" src="${contextpath}/resources/js/jquery.stickytableheaders.js?version=<%@include file="../version.txt" %>"></script>
	<script type='text/javascript' src='${contextpath}/resources/js/knockout-3.5.1.js?version=<%@include file="../version.txt" %>'></script>
	<script type="text/javascript" src="${contextpath}/resources/js/participants.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/configure.js?version=<%@include file="../version.txt" %>"></script>
	
	<style type="text/css">
		.createlist {
			width: 200px;
			height: 150px;
			padding: 20px;
			white-space: normal;
			font-weight: bold;
			margin-top: 30px;
		}
		
		.createlist .info {
			font-weight: normal;
			font-size: 12px;
		}
		
		.createlist .glyphicon {
			font-size: 40px;
			color: #000;
		}
		
		.stepseparator {
			margin: 0px; 
			padding: 0px; 
			border-color: #777;
			width: 20px; 
			margin-left: -4px; 
			margin-right: -4px;
			margin-bottom: 4px; 
			display: inline-block;
		}
		
		.checkcell {
			max-width: 30px !important;
			width: 30px !important;
			min-width: 30px !important;
			text-align: center;
		}
		
		.ptable {
			table-layout: fixed;
			margin-bottom: 0px;
			min-height: 500px;
		}
		
		.ptable tr{height:1px;}
		.ptable tr:last-child{height:auto;}
		
		.ptable th, .ptable td {
			width: 150px;
			max-width: 150px;
			min-width: 150px;
			word-wrap: break-word;	
		}
		
		.ptable .filtertools {
			margin-top: -60px !important;
    		padding-bottom: 10px !important;
		}
		  
		  #wait-dialog {
		  	position: fixed;
		  	left: 0;
		  	right: 0;
		  	top: 110px;
		  	bottom: 0;
		  	background-color: #cccccce6;
		  	text-align: center;
		  	vertical-align: middle;
		  	padding-top: 300px;
		  	z-index: 100000;
		  }
		  
		  #wait-dialog-inner {
		  	width: 150px; 
		  	height: 150px; 
		  	background-color: #fff; 
		  	margin-left: auto; 
		  	margin-right: auto; 
		  	padding: 55px;
		  	border-radius: 10px;
		  }
		  
		  .col-md-5x {
		  	float: left;
		  	width: 48%;
		  }
		  
		  .col-md-2x {
		  	float: left;
		  	width: 4%;
		  }
		  
		  .increation {
		  	background-color: #FFE0BF;
		  }
		  
		  .runningmails {
		  	background-color: #D1EBFF;
		  }
		  
		  tr.error {
		  	background-color: #FFD6DA;
		  }
		  
		  .fixedtitleform {
			top: 0;
		  }
		  
		  .participantstable {
		  	width: 800px;
		  	margin-left: auto;
		  	margin-right: auto;
		  	margin-bottom: 30px;
		  }
		  
		  #participantstablecontacts, #participantstabletokens, #participantstableec {		  	
		  	table-layout: fixed;
		  }
		  
		  .participantstablediv {
		  	max-height: 450px;
		  	height: 450px;
		  	max-width: 100%;
		  	overflow-x: auto;
		  	overflow-y: scroll;"
		  }
    </style>
    
    <script>
    
    	function resize()
    	{
    		var totalHeight = $(window).height();
    		var height = totalHeight - 280 - 280;
    		if (height < 200) height = 200;
    		if (height > 450) height = 450;
    		$('.participantstablediv').height(height);
    		$('.participantstablediv table').css("min-height", height - 1);
    		
    	}
    	
    	$( window ).resize(function() {
    		resize();
   		});
    
    	$(function() {	
	    <c:choose>
			<c:when test="${USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('ManageInvitations') > 1}">
				_participants.Access(2);
			</c:when>
			<c:when test="${USER.getLocalPrivilegeValue('ManageInvitations') > 0}">
				_participants.Access(1);
			</c:when>
			<c:otherwise>		
				_participants.Access(0);
			</c:otherwise>
		</c:choose>	
		
			resize();
    	});
	</script>
</head>
<body>
	<div class="page-wrap" style="padding-bottom: 0; margin-bottom: -272px;">
		<%@ include file="../header.jsp" %>
		<%@ include file="../menu.jsp" %>
		<%@ include file="formmenu.jsp" %>
				
		<div id="wait-dialog" data-bind="visible: ShowWait()">
			<div id="wait-dialog-inner">
				<img class="center" src="${contextpath}/resources/images/ajax-loader.gif" />
			</div>												
		</div>				
		
		<div id="participants" data-bind="visible: Page() == 1">		
			<div id="action-bar" class="container action-bar" data-bind="visible: DataLoaded() && Guestlists().length > 0">
				<div class="row">
					<div class="col-md-12" style="text-align:center">
						<c:if test="${USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('ManageInvitations') > 1}">
							
						<span style="margin-right: 20px"><spring:message code="label.CreateNew" /></span>
						<a onclick="_participants.newContactList()" class="btn btn-success"><span class="glyphicon glyphicon-book"></span> <spring:message code="label.ContactList" /></a>
						<a onclick="_participants.newEUList()" class="btn btn-success"><span class="glyphicon glyphicon-user"></span> <spring:message code="label.EUList" /></a>
						<a onclick="_participants.newTokenList()" class="btn btn-success"><span class="glyphicon glyphicon-barcode"></span> <spring:message code="label.TokenList" /></a>
						
						</c:if>
					</div>
				</div>
			</div>	
			
			<div class="fullpageform" style="padding-top: 10px">
			
					<div class="participantstable" data-bind="visible: ContactGuestlists().length > 0">
			
					<h2><spring:message code="label.ContactList" /></h2>
					
					<table id="participantstablecontacts" class="table table-bordered table-styled table-striped" data-bind="visible: DataLoaded() && Guestlists().length > 0">
					<thead>
						<tr>
							<th style="width: 17%"><spring:message code="label.Name" /></th>
							<th style="width: 20%"><spring:message code="label.Created" /></th>
							<th style="width: 17%"><spring:message code="label.Participants" /></th>
							<th style="width: 17%"><spring:message code="label.Invited" /></th>						
							<th style="width: 29%"><spring:message code="label.Actions" /></th>
						</tr>
					</thead>
					<tbody>
						<!-- ko foreach: ContactGuestlists() -->
							<tr data-bind="attr: {'data-id': id(), class: inCreation() ? 'increation' : (runningMails() ? 'runningmails' : (error() ? 'error' : ''))}">
								<td data-bind="text: name"></td>
								<td data-bind="text: created"></td>
								<td data-bind="text: children"></td>
								<td data-bind="text: type() == 'Token' ? children : invited"></td>
								<td>
									<!-- ko if: $parent.Access() == 2 -->
										<!-- ko if: activateEnabled() -->
											<a id="btnActivateFromParticipant" class="iconbutton" data-bind="click: activate" data-toggle="tooltip" title="<spring:message code="label.Activate" />"><span class='glyphicon glyphicon-play'></span></a>
										<!-- /ko -->
										<!-- ko if: deactivateEnabled() -->
											<a id="btnDeactivateFromParticipant" class="iconbutton" data-bind="click: deactivate" data-class="deactivatebutton" data-toggle="tooltip" title="<spring:message code="label.Deactivate" />"><span class='glyphicon glyphicon-stop'></span></a>
										<!-- /ko -->
										<!-- ko if: !activateEnabled() && !deactivateEnabled() -->
											<a id="btnDeactivateFromParticipant" class="iconbutton disabled" data-class="deactivatebutton" onclick="return false;" data-toggle="tooltip" title="<spring:message code="label.Deactivate" />"><span class='glyphicon glyphicon-stop'></span></a>
										<!-- /ko -->
											<!-- ko if: editEnabled() -->
											<a id="btnEditEnabledFromParticipant" class="iconbutton" data-toggle="tooltip" title="<spring:message code="label.Edit" />" data-bind="click: edit"><span class='glyphicon glyphicon-pencil'></span></a>
										<!-- /ko -->
										<!-- ko if: !editEnabled() -->
											<a id="btnEditDisabledFromParticipant" class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Edit" />"><span class='glyphicon glyphicon-pencil'></span></a>
										<!-- /ko -->
										<!-- ko if: sendEnabled() -->
											<a id="btnSendEnabledFromParticipant" class="iconbutton" data-toggle="tooltip" title="<spring:message code="label.SendInvitations" />" data-bind="attr: {href: '<c:url value="/${sessioninfo.shortname}/management/sendInvitations" />/' + id()}"><span class='glyphicon glyphicon-envelope'></span></a>
										<!-- /ko -->
										<!-- ko if: !sendEnabled() && type() != 'Token' -->
											<a id="btnSendDisabledFromParticipant" data-class="sendbutton" class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.SendInvitations" />"><span class='glyphicon glyphicon-envelope'></span></a>
										<!-- /ko -->
										<!-- ko if: exportEnabled() && type() == 'Token' -->
											<a id="startExportTokensxls" class="iconbutton" data-toggle="tooltip" title="<spring:message code="tooltip.Downloadxls" />" data-bind="click: exportxls"><img src='${contextpath}/resources/images/file_extension_xls_small.png' /></a>
											<a id="startExportTokensods" class="iconbutton" data-toggle="tooltip" title="<spring:message code="tooltip.Downloadods" />" data-bind="click: exportods"><img src='${contextpath}/resources/images/file_extension_ods_small.png' /></a>
										<!-- /ko -->
										<!-- ko if: detailsEnabled() -->
											<a class="iconbutton" data-toggle="tooltip" data-bind="click: showDetails" title="<spring:message code="label.ViewDetails" />"><span class="glyphicon glyphicon-info-sign"></span></a>
										<!-- /ko -->
										<!-- ko if: !detailsEnabled() -->
											<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.ViewDetails" />"><span class="glyphicon glyphicon-info-sign"></span></a>
										<!-- /ko -->	
										<!-- ko if: deleteEnabled() -->
											<a id="btnDeleteEnabledFromParticipant" data-bind="click: deleteList" class="iconbutton" data-toggle="tooltip" title="<spring:message code="label.Remove" />"><span class='glyphicon glyphicon-remove'></span></a>										
										<!-- /ko -->
										<!-- ko if: !deleteEnabled() -->
											<a id="btnDeleteDisabledFromParticipant" class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Remove" />"><span class='glyphicon glyphicon-remove'></span></a>
										<!-- /ko -->
									<!-- /ko -->
									<!-- ko if: $parent.Access() == 1 -->
										<!-- ko if: !activateEnabled() -->
											<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Deactivate" />"><span class="glyphicon glyphicon-stop"></span></a>
										<!-- /ko -->
										<!-- ko if: activateEnabled() -->
											<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Activate" />"><span class="glyphicon glyphicon-play"></span></a>
										<!-- /ko -->
										<!-- ko if: type() != 'Token' -->
										<a id="btnSendDisabledFromParticipant" data-class="sendbutton" class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.SendInvitations" />"><span class='glyphicon glyphicon-envelope'></span></a>
										<!-- /ko -->
										<!-- ko if: editEnabled() -->
											<a id="btnEditEnabledFromParticipant" class="iconbutton" data-toggle="tooltip" title="<spring:message code="label.Edit" />" data-bind="attr: {href: '<c:url value="/${sessioninfo.shortname}/management/participantsEdit" />?id=' + id()}"><span class='glyphicon glyphicon-pencil'></span></a>
										<!-- /ko -->
										<!-- ko if: !editEnabled() -->
											<a id="btnEditDisabledFromParticipant" class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Edit" />"><span class='glyphicon glyphicon-pencil'></span></a>
										<!-- /ko -->
										<!-- ko if: exportEnabled() && type() == 'Token' -->
											<a id="startExportTokensxls" class="iconbutton" data-toggle="tooltip" title="<spring:message code="tooltip.Downloadxls" />" data-bind="click: exportxls"><img src='${contextpath}/resources/images/file_extension_xls_small.png' /></a>
											<a id="startExportTokensods" class="iconbutton" data-toggle="tooltip" title="<spring:message code="tooltip.Downloadods" />" data-bind="click: exportods"><img src='${contextpath}/resources/images/file_extension_ods_small.png' /></a>
										<!-- /ko -->
										<!-- ko if: detailsEnabled() -->
											<a class="iconbutton" data-toggle="tooltip" title="<spring:message code="label.ViewDetails" />"><span class="glyphicon glyphicon-info-sign"></span></a>
										<!-- /ko -->
										<!-- ko if: !detailsEnabled() -->
											<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.ViewDetails" />"><span class="glyphicon glyphicon-info-sign"></span></a>
										<!-- /ko -->										
										<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Remove" />"><span class="glyphicon glyphicon-remove"></span></a>
									<!-- /ko -->
									<!-- ko if: $parent.Access() == 0 -->
										<!-- ko if: !activateEnabled() -->
											<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Deactivate" />"><span class="glyphicon glyphicon-stop"></span></a>
										<!-- /ko -->
										<!-- ko if: activateEnabled() -->
											<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Activate" />"><span class="glyphicon glyphicon-play"></span></a>
										<!-- /ko -->
										<a id="btnEditDisabledFromParticipant" class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Edit" />"><span class='glyphicon glyphicon-pencil'></span></a>		
										<!-- ko if: type() != 'Token' -->			
										<a id="btnSendDisabledFromParticipant" data-class="sendbutton" class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.SendInvitations" />"><span class='glyphicon glyphicon-envelope'></span></a>
										<!-- /ko -->

										<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.ViewDetails" />"><span class="glyphicon glyphicon-info-sign"></span></a>
										<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Remove" />"><span class="glyphicon glyphicon-remove"></span></a>
									<!-- /ko -->
								</td>
							</tr>					
						<!-- /ko -->					
					</tbody>
				</table>
			</div>
			
			<div class="participantstable" data-bind="visible: TokenGuestlists().length > 0">
				<h2><spring:message code="label.TokenList" /></h2>
				
				<table id="participantstabletokens" class="table table-bordered table-styled table-striped" data-bind="visible: DataLoaded() && Guestlists().length > 0">
					<thead>
						<tr>
							<th style="width: 17%"><spring:message code="label.Name" /></th>
							<th style="width: 20%"><spring:message code="label.Created" /></th>
							<th style="width: 17%"><spring:message code="label.Participants" /></th>
							<th style="width: 17%"><spring:message code="label.Invited" /></th>						
							<th style="width: 29%"><spring:message code="label.Actions" /></th>
						</tr>
					</thead>
					<tbody>
						<!-- ko foreach: TokenGuestlists() -->
							<tr data-bind="attr: {'data-id': id(), class: inCreation() ? 'increation' : (runningMails() ? 'runningmails' : (error() ? 'error' : ''))}">
								<td data-bind="text: name"></td>								
								<td data-bind="text: created"></td>
								<td data-bind="text: children"></td>
								<td data-bind="text: type() == 'Token' ? children : invited"></td>
								<td>
									<!-- ko if: $parent.Access() == 2 -->
										<!-- ko if: activateEnabled() -->
											<a id="btnActivateFromParticipant" class="iconbutton" data-bind="click: activate" data-toggle="tooltip" title="<spring:message code="label.Activate" />"><span class='glyphicon glyphicon-play'></span></a>
										<!-- /ko -->
										<!-- ko if: deactivateEnabled() -->
											<a id="btnDeactivateFromParticipant" class="iconbutton" data-bind="click: deactivate" data-class="deactivatebutton" data-toggle="tooltip" title="<spring:message code="label.Deactivate" />"><span class='glyphicon glyphicon-stop'></span></a>
										<!-- /ko -->
										<!-- ko if: !activateEnabled() && !deactivateEnabled() -->
											<a id="btnDeactivateFromParticipant" class="iconbutton disabled" data-class="deactivatebutton" onclick="return false;" data-toggle="tooltip" title="<spring:message code="label.Deactivate" />"><span class='glyphicon glyphicon-stop'></span></a>
										<!-- /ko -->
											<!-- ko if: editEnabled() -->
											<a id="btnEditEnabledFromParticipant" class="iconbutton" data-toggle="tooltip" title="<spring:message code="label.Edit" />" data-bind="click: edit"><span class='glyphicon glyphicon-pencil'></span></a>
										<!-- /ko -->
										<!-- ko if: !editEnabled() -->
											<a id="btnEditDisabledFromParticipant" class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Edit" />"><span class='glyphicon glyphicon-pencil'></span></a>
										<!-- /ko -->
										<!-- ko if: sendEnabled() -->
											<a id="btnSendEnabledFromParticipant" class="iconbutton" data-toggle="tooltip" title="<spring:message code="label.SendInvitations" />" data-bind="attr: {href: '<c:url value="/${sessioninfo.shortname}/management/sendInvitations" />/' + id()}"><span class='glyphicon glyphicon-envelope'></span></a>
										<!-- /ko -->
										<!-- ko if: !sendEnabled() && type() != 'Token' -->
											<a id="btnSendDisabledFromParticipant" data-class="sendbutton" class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.SendInvitations" />"><span class='glyphicon glyphicon-envelope'></span></a>
										<!-- /ko -->
										<!-- ko if: exportEnabled() && type() == 'Token' -->
											<a id="startExportTokensxls" class="iconbutton" data-toggle="tooltip" title="<spring:message code="tooltip.Downloadxls" />" data-bind="click: exportxls"><img src='${contextpath}/resources/images/file_extension_xls_small.png' /></a>
											<a id="startExportTokensods" class="iconbutton" data-toggle="tooltip" title="<spring:message code="tooltip.Downloadods" />" data-bind="click: exportods"><img src='${contextpath}/resources/images/file_extension_ods_small.png' /></a>
										<!-- /ko -->
										<!-- ko if: detailsEnabled() -->
											<a class="iconbutton" data-toggle="tooltip" data-bind="click: showDetails" title="<spring:message code="label.ViewDetails" />"><span class="glyphicon glyphicon-info-sign"></span></a>
										<!-- /ko -->
										<!-- ko if: !detailsEnabled() -->
											<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.ViewDetails" />"><span class="glyphicon glyphicon-info-sign"></span></a>
										<!-- /ko -->	
										<!-- ko if: deleteEnabled() -->
											<a id="btnDeleteEnabledFromParticipant" data-bind="click: deleteList" class="iconbutton" data-toggle="tooltip" title="<spring:message code="label.Remove" />"><span class='glyphicon glyphicon-remove'></span></a>										
										<!-- /ko -->
										<!-- ko if: !deleteEnabled() -->
											<a id="btnDeleteDisabledFromParticipant" class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Remove" />"><span class='glyphicon glyphicon-remove'></span></a>
										<!-- /ko -->
									<!-- /ko -->
									<!-- ko if: $parent.Access() == 1 -->
										<!-- ko if: !activateEnabled() -->
											<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Deactivate" />"><span class="glyphicon glyphicon-stop"></span></a>
										<!-- /ko -->
										<!-- ko if: activateEnabled() -->
											<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Activate" />"><span class="glyphicon glyphicon-play"></span></a>
										<!-- /ko -->
										<!-- ko if: type() != 'Token' -->
										<a id="btnSendDisabledFromParticipant" data-class="sendbutton" class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.SendInvitations" />"><span class='glyphicon glyphicon-envelope'></span></a>
										<!-- /ko -->
										<!-- ko if: editEnabled() -->
											<a id="btnEditEnabledFromParticipant" class="iconbutton" data-toggle="tooltip" title="<spring:message code="label.Edit" />" data-bind="attr: {href: '<c:url value="/${sessioninfo.shortname}/management/participantsEdit" />?id=' + id()}"><span class='glyphicon glyphicon-pencil'></span></a>
										<!-- /ko -->
										<!-- ko if: !editEnabled() -->
											<a id="btnEditDisabledFromParticipant" class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Edit" />"><span class='glyphicon glyphicon-pencil'></span></a>
										<!-- /ko -->
										<!-- ko if: exportEnabled() && type() == 'Token' -->
											<a id="startExportTokensxls" class="iconbutton" data-toggle="tooltip" title="<spring:message code="tooltip.Downloadxls" />" data-bind="click: exportxls"><img src='${contextpath}/resources/images/file_extension_xls_small.png' /></a>
											<a id="startExportTokensods" class="iconbutton" data-toggle="tooltip" title="<spring:message code="tooltip.Downloadods" />" data-bind="click: exportods"><img src='${contextpath}/resources/images/file_extension_ods_small.png' /></a>
										<!-- /ko -->
										<!-- ko if: detailsEnabled() -->
											<a class="iconbutton" data-toggle="tooltip" title="<spring:message code="label.ViewDetails" />"><span class="glyphicon glyphicon-info-sign"></span></a>
										<!-- /ko -->
										<!-- ko if: !detailsEnabled() -->
											<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.ViewDetails" />"><span class="glyphicon glyphicon-info-sign"></span></a>
										<!-- /ko -->										
										<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Remove" />"><span class="glyphicon glyphicon-remove"></span></a>
									<!-- /ko -->
									<!-- ko if: $parent.Access() == 0 -->
										<!-- ko if: !activateEnabled() -->
											<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Deactivate" />"><span class="glyphicon glyphicon-stop"></span></a>
										<!-- /ko -->
										<!-- ko if: activateEnabled() -->
											<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Activate" />"><span class="glyphicon glyphicon-play"></span></a>
										<!-- /ko -->
										<a id="btnEditDisabledFromParticipant" class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Edit" />"><span class='glyphicon glyphicon-pencil'></span></a>		
										<!-- ko if: type() != 'Token' -->			
										<a id="btnSendDisabledFromParticipant" data-class="sendbutton" class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.SendInvitations" />"><span class='glyphicon glyphicon-envelope'></span></a>
										<!-- /ko -->

										<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.ViewDetails" />"><span class="glyphicon glyphicon-info-sign"></span></a>
										<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Remove" />"><span class="glyphicon glyphicon-remove"></span></a>
									<!-- /ko -->
								</td>
							</tr>					
						<!-- /ko -->					
					</tbody>
				</table>
				
				</div>
				
				<div class="participantstable" data-bind="visible: ECGuestlists().length > 0">
				
					<h2><spring:message code="label.EUList" /></h2>
				
					<table id="participantstableec" class="table table-bordered table-styled table-striped" data-bind="visible: DataLoaded() && Guestlists().length > 0">
						<thead>
							<tr>
								<th style="width: 17%"><spring:message code="label.Name" /></th>
								<th style="width: 20%"><spring:message code="label.Created" /></th>
								<th style="width: 17%"><spring:message code="label.Participants" /></th>
								<th style="width: 17%"><spring:message code="label.Invited" /></th>						
								<th style="width: 29%"><spring:message code="label.Actions" /></th>
							</tr>
						</thead>
						<tbody>
							<!-- ko foreach: ECGuestlists() -->
								<tr data-bind="attr: {'data-id': id(), class: inCreation() ? 'increation' : (runningMails() ? 'runningmails' : (error() ? 'error' : ''))}">
									<td data-bind="text: name"></td>
									<td data-bind="text: created"></td>
									<td data-bind="text: children"></td>
									<td data-bind="text: type() == 'Token' ? children : invited"></td>
									<td>
										<!-- ko if: $parent.Access() == 2 -->
											<!-- ko if: activateEnabled() -->
												<a id="btnActivateFromParticipant" class="iconbutton" data-bind="click: activate" data-toggle="tooltip" title="<spring:message code="label.Activate" />"><span class='glyphicon glyphicon-play'></span></a>
											<!-- /ko -->
											<!-- ko if: deactivateEnabled() -->
												<a id="btnDeactivateFromParticipant" class="iconbutton" data-bind="click: deactivate" data-class="deactivatebutton" data-toggle="tooltip" title="<spring:message code="label.Deactivate" />"><span class='glyphicon glyphicon-stop'></span></a>
											<!-- /ko -->
											<!-- ko if: !activateEnabled() && !deactivateEnabled() -->
												<a id="btnDeactivateFromParticipant" class="iconbutton disabled" data-class="deactivatebutton" onclick="return false;" data-toggle="tooltip" title="<spring:message code="label.Deactivate" />"><span class='glyphicon glyphicon-stop'></span></a>
											<!-- /ko -->
												<!-- ko if: editEnabled() -->
												<a id="btnEditEnabledFromParticipant" class="iconbutton" data-toggle="tooltip" title="<spring:message code="label.Edit" />" data-bind="click: edit"><span class='glyphicon glyphicon-pencil'></span></a>
											<!-- /ko -->
											<!-- ko if: !editEnabled() -->
												<a id="btnEditDisabledFromParticipant" class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Edit" />"><span class='glyphicon glyphicon-pencil'></span></a>
											<!-- /ko -->
											<!-- ko if: sendEnabled() -->
												<a id="btnSendEnabledFromParticipant" class="iconbutton" data-toggle="tooltip" title="<spring:message code="label.SendInvitations" />" data-bind="attr: {href: '<c:url value="/${sessioninfo.shortname}/management/sendInvitations" />/' + id()}"><span class='glyphicon glyphicon-envelope'></span></a>
											<!-- /ko -->
											<!-- ko if: !sendEnabled() && type() != 'Token' -->
												<a id="btnSendDisabledFromParticipant" data-class="sendbutton" class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.SendInvitations" />"><span class='glyphicon glyphicon-envelope'></span></a>
											<!-- /ko -->
											<!-- ko if: exportEnabled() && type() == 'Token' -->
												<a id="startExportTokensxls" class="iconbutton" data-toggle="tooltip" title="<spring:message code="tooltip.Downloadxls" />" data-bind="click: exportxls"><img src='${contextpath}/resources/images/file_extension_xls_small.png' /></a>
												<a id="startExportTokensods" class="iconbutton" data-toggle="tooltip" title="<spring:message code="tooltip.Downloadods" />" data-bind="click: exportods"><img src='${contextpath}/resources/images/file_extension_ods_small.png' /></a>
											<!-- /ko -->
											<!-- ko if: detailsEnabled() -->
												<a class="iconbutton" data-toggle="tooltip" data-bind="click: showDetails" title="<spring:message code="label.ViewDetails" />"><span class="glyphicon glyphicon-info-sign"></span></a>
											<!-- /ko -->
											<!-- ko if: !detailsEnabled() -->
												<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.ViewDetails" />"><span class="glyphicon glyphicon-info-sign"></span></a>
											<!-- /ko -->	
											<!-- ko if: deleteEnabled() -->
												<a id="btnDeleteEnabledFromParticipant" data-bind="click: deleteList" class="iconbutton" data-toggle="tooltip" title="<spring:message code="label.Remove" />"><span class='glyphicon glyphicon-remove'></span></a>										
											<!-- /ko -->
											<!-- ko if: !deleteEnabled() -->
												<a id="btnDeleteDisabledFromParticipant" class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Remove" />"><span class='glyphicon glyphicon-remove'></span></a>
											<!-- /ko -->
										<!-- /ko -->
										<!-- ko if: $parent.Access() == 1 -->
											<!-- ko if: !activateEnabled() -->
												<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Deactivate" />"><span class="glyphicon glyphicon-stop"></span></a>
											<!-- /ko -->
											<!-- ko if: activateEnabled() -->
												<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Activate" />"><span class="glyphicon glyphicon-play"></span></a>
											<!-- /ko -->
											<!-- ko if: type() != 'Token' -->
											<a id="btnSendDisabledFromParticipant" data-class="sendbutton" class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.SendInvitations" />"><span class='glyphicon glyphicon-envelope'></span></a>
											<!-- /ko -->
											<!-- ko if: editEnabled() -->
												<a id="btnEditEnabledFromParticipant" class="iconbutton" data-toggle="tooltip" title="<spring:message code="label.Edit" />" data-bind="attr: {href: '<c:url value="/${sessioninfo.shortname}/management/participantsEdit" />?id=' + id()}"><span class='glyphicon glyphicon-pencil'></span></a>
											<!-- /ko -->
											<!-- ko if: !editEnabled() -->
												<a id="btnEditDisabledFromParticipant" class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Edit" />"><span class='glyphicon glyphicon-pencil'></span></a>
											<!-- /ko -->
											<!-- ko if: exportEnabled() && type() == 'Token' -->
												<a id="startExportTokensxls" class="iconbutton" data-toggle="tooltip" title="<spring:message code="tooltip.Downloadxls" />" data-bind="click: exportxls"><img src='${contextpath}/resources/images/file_extension_xls_small.png' /></a>
												<a id="startExportTokensods" class="iconbutton" data-toggle="tooltip" title="<spring:message code="tooltip.Downloadods" />" data-bind="click: exportods"><img src='${contextpath}/resources/images/file_extension_ods_small.png' /></a>
											<!-- /ko -->
											<!-- ko if: detailsEnabled() -->
												<a class="iconbutton" data-toggle="tooltip" title="<spring:message code="label.ViewDetails" />"><span class="glyphicon glyphicon-info-sign"></span></a>
											<!-- /ko -->
											<!-- ko if: !detailsEnabled() -->
												<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.ViewDetails" />"><span class="glyphicon glyphicon-info-sign"></span></a>
											<!-- /ko -->										
											<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Remove" />"><span class="glyphicon glyphicon-remove"></span></a>
										<!-- /ko -->
										<!-- ko if: $parent.Access() == 0 -->
											<!-- ko if: !activateEnabled() -->
												<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Deactivate" />"><span class="glyphicon glyphicon-stop"></span></a>
											<!-- /ko -->
											<!-- ko if: activateEnabled() -->
												<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Activate" />"><span class="glyphicon glyphicon-play"></span></a>
											<!-- /ko -->
											<a id="btnEditDisabledFromParticipant" class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Edit" />"><span class='glyphicon glyphicon-pencil'></span></a>		
											<!-- ko if: type() != 'Token' -->			
											<a id="btnSendDisabledFromParticipant" data-class="sendbutton" class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.SendInvitations" />"><span class='glyphicon glyphicon-envelope'></span></a>
											<!-- /ko -->
	
											<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.ViewDetails" />"><span class="glyphicon glyphicon-info-sign"></span></a>
											<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Remove" />"><span class="glyphicon glyphicon-remove"></span></a>
										<!-- /ko -->
									</td>
								</tr>					
							<!-- /ko -->					
						</tbody>
					</table>
				</div>
				
				<div style="text-align: center; margin-top: 140px;" data-bind="visible: DataLoaded() && Guestlists().length == 0">
					<h1 style="margin-bottom: 20px;"><spring:message code="message.noguestlistyet" /></h1>
					
					<c:if test="${USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('ManageInvitations') > 1}">
						<spring:message code="label.createguestlist" /><br />
						
						<a onclick="_participants.newContactList()" class="btn btn-success createlist">
							<span class="glyphicon glyphicon-book"></span><br />
							<spring:message code="label.ContactList" /><br /><br />
							<span class="info"><spring:message code="info.ContactList" /></span>
						</a>
						
						<a onclick="_participants.newEUList()" class="btn btn-success createlist">
							<span class="glyphicon glyphicon-user"></span><br />
							<spring:message code="label.EUList" /><br /><br />
							<span class="info"><spring:message code="info.EUList" /></span>
						</a>
						
						<a onclick="_participants.newTokenList()" class="btn btn-success createlist">
							<span class="glyphicon glyphicon-barcode"></span><br />
							<spring:message code="label.TokenList" /><br /><br />
							<span class="info"><spring:message code="info.TokenList" /></span>
						</a>						
					</c:if>
				</div>
				
				<div style="text-align: center" data-bind="visible: !DataLoaded()">
					<img src="${contextpath}/resources/images/ajax-loader.gif" />
				</div>
				
			</div>			
		</div>
		
		<div id="details" data-bind="visible: Page() == 2">
			<div class="fixedtitleform">
				<div class="fixedtitleinner">
					<div id="action-bar" class="container action-bar" style="max-width: 900px; margin-left: auto; margin-right: auto" data-bind="visible: DataLoaded() && Guestlists().length > 0">
						<div class="row">
							<div class="col-md-4" style="text-align: left">
								<a onclick="_participants.Page(1)"><spring:message code="label.Participants" /></a> > <spring:message code="label.ViewGuestList" />
							</div>
							<div class="col-md-4" style="text-align: center">
								<spring:message code="label.GuestListEntries" /> <span style="margin-left: 10px;" data-bind="text: selectedGroup() != null ? selectedGroup().children() : ''"></span>
							</div>
							<div class="col-md-4" style="text-align: right">
								<!-- ko if: selectedGroup() != null && selectedGroup().exportEnabled() -->
									<a id="startExportTokensxls" class="iconbutton" data-toggle="tooltip" data-placement="bottom" title="<spring:message code="tooltip.Downloadxls" />" data-bind="click: selectedGroup().exportxls"><img src='${contextpath}/resources/images/file_extension_xls_small.png' /></a>
									<a id="startExportTokensods" class="iconbutton" data-toggle="tooltip" data-placement="bottom" title="<spring:message code="tooltip.Downloadods" />" data-bind="click: selectedGroup().exportods"><img src='${contextpath}/resources/images/file_extension_ods_small.png' /></a>
								<!-- /ko -->
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<div class="fullpageform" style="padding-top: 160px">
			
				<table id="participantdetailstable" class="table table-bordered table-styled table-striped" style="max-width: none; width: auto; margin-left: auto; margin-right: auto;" data-bind="visible: DataLoaded() && Guestlists().length > 0">
					<thead>
						<tr>
							<!-- ko if: selectedGroup() != null && (selectedGroup().type() == 'Static' || selectedGroup().type() == 'ECMembers') -->
									<th style="width: 150px"><spring:message code="label.Name" /></th>
									<th style="width: 150px"><spring:message code="label.Email" /></th>
									 <c:if test="${!form.survey.isAnonymous()}">
										<th style="width: 150px"><spring:message code="label.InvitationDate" /></th>
										<th style="width: 150px"><spring:message code="label.ReminderDate" /></th>
										<th style="width: 150px"><spring:message code="label.Answers" /></th>
									</c:if>
							<!-- /ko -->
							<!-- ko if: selectedGroup() != null && (selectedGroup().type() == 'Token') -->
									<th style="width: 150px"><spring:message code="label.Token" /></th>
									<c:if test="${!form.survey.isAnonymous()}">
 										<th style="width: 150px"><spring:message code="label.Answers" /></th>
 									</c:if>
 									<th style="width: 150px"><spring:message code="label.CreationDate" /></th>
							<!-- /ko -->							
						</tr>
					</thead>
					
						<!-- ko if: selectedGroup() != null && selectedGroup().type() == 'Static' -->
						<tbody data-bind="foreach: selectedGroup().attendees()">
							<tr>
								<td data-bind="text: name"></td>
								<td data-bind="text: email"></td>
								<c:if test="${!form.survey.isAnonymous()}">
									<td data-bind="text: niceInvited"></td>
									<td data-bind="text: niceReminded"></td>
									<td data-bind="text: answers"></td>
								</c:if>
							</tr>
						</tbody>
						<!-- /ko -->
						<!-- ko if: selectedGroup() != null && selectedGroup().type() == 'ECMembers' -->
						<tbody data-bind="foreach: selectedGroup().users()">
							<tr>
								<td data-bind="text: name"></td>
								<td data-bind="text: email"></td>
								<c:if test="${!form.survey.isAnonymous()}">
									<td data-bind="text: niceInvited"></td>
									<td data-bind="text: niceReminded"></td>
									<td data-bind="text: answers"></td>
								</c:if>
							</tr>
						</tbody>
						<!-- /ko -->
						<!-- ko if: selectedGroup() != null && selectedGroup().type() == 'Token' -->
						<tbody data-bind="foreach: selectedGroup().tokens()">
							<tr>
								<td data-bind="text: uniqueId"></td>
								<c:if test="${!form.survey.isAnonymous()}">
								 	<td data-bind="text: answers"></td>
								</c:if>
								<td data-bind="text: niceInvited"></td>
							</tr>
						</tbody>
						<!-- /ko -->
				</table>			
				
				<div style="text-align: center">
					<a class="btn btn-primary" onclick="_participants.Page(1)"><spring:message code="label.Back" /></a>	
				</div>
			</div>
		</div>
		
		<div id="newcontactlist" data-bind="visible: Page() == 3">		
			<c:choose>
				<c:when test="${numberOfAttendees == 0}">
					<div style="padding-top: 160px; text-align: center">
						<b><spring:message code="info.NoContacts" /></b><br /><br />
						<a href="${contextpath}/addressbook" class="btn btn-default"><span class="glyphicon glyphicon-book"></span> <spring:message code="label.CreateContacts" /></a>
					</div>
				</c:when>
				<c:otherwise>
					<div id="action-bar" class="container action-bar" style="width: 100%;">
						<div class="row">
							<div class="col-md-4" style="text-align: left">
								<a onclick="_participants.Page(1)"><spring:message code="label.Participants" /></a> <span class="glyphicon glyphicon-menu-right" style="font-size: 90%"></span> 
								<!-- ko if: selectedGroup() != null && selectedGroup().id() == 0 -->
								<spring:message code="label.CreateNewContactGuestlist" />
								<!-- /ko -->
								
								<!-- ko if: selectedGroup() != null && selectedGroup().id() > 0 -->
								<spring:message code="label.EditGuestlist" />
								<!-- /ko -->
							</div>
							<div class="col-md-4" style="text-align: center">
								<ul class="progressbar">
									<li data-bind="attr: {class: Step() == 1 ? 'active' : ''}"><span data-bind="attr: {style: Step() == 2 ? 'color: #43ff43' : ''}" class="glyphicon glyphicon-ok"></span><spring:message code="label.SelectContacts" /></li>
									<li data-bind="attr: {class: Step() == 2 ? 'active' : ''}"><spring:message code="label.SaveGuestlist" /></li>
								</ul>
							</div>
							<div class="col-md-4" style="text-align: right">
							</div>
						</div>
					</div>	
				
					<div class="fullpageform" style="padding-top: 10px">
						<div data-bind="visible: Step() == 1">						
							<div>
								<div class="row">
									<div class="col-md-5x">										
										<div class="tabletitle">
											<spring:message code="label.AddressBookContacts" />
											<div class="info">
												<spring:message code="info.SelectContacts" />
											</div>
										</div>
										<div id="contactshead" style="max-width: 100%; overflow-y: scroll; overflow-x: hidden;">
											<table class="table table-bordered table-styled table-striped ptable" style="overflow-y: hidden">
												<thead>
													<tr>
														<th class="checkcell"><input id="checkallcontacts" type="checkbox" onclick="_participants.checkAll($(this).is(':checked'))" /></th>
														<th><spring:message code="label.Name" /></th>
														<th><spring:message code="label.Email" /></th>
														<!-- ko foreach: attributeNames() -->
															<th class="attribute" data-bind="text: name, attr: {'data-id': id}"></th>
														<!-- /ko -->
													</tr>
													<tr class="table-styled-filter">
														<th class="checkcell">&nbsp;</th>
														<th class="filtercell">
															<input onkeyup="checkReturn(event, this); checkFilterCell($(this).closest('.filtercell'), true)" type="text" maxlength="255" style="margin:0px;" id="namefilter" />
														</th>
														<th class="filtercell">
															<input onkeyup="checkReturn(event, this); checkFilterCell($(this).closest('.filtercell'), true)" type="text" maxlength="255" style="margin:0px;" id="emailfilter" />
														</th>												
														<!-- ko foreach: attributeNames() -->
															<th class="filtercell">
																<input onkeyup="checkReturn(event, this); checkFilterCell($(this).closest('.filtercell'), true)" data-bind="attr: {id: id}" class="attributefilter" type="text" maxlength="255" style="margin:0px;" />
															</th>
														<!-- /ko -->
													</tr>
												</thead>
											</table>
										</div>
										<div id="contactsdiv" class="participantstablediv"> 
											<table id="contacts" class="table table-bordered table-styled table-striped ptable">
												<!-- ko if: Attendees().length == 0 -->
												<tbody>
													<tr>
														<td style="padding-top: 100px; text-align: center;" data-bind="attr: {colspan: attributeNames().length + 3}"><spring:message code="label.NoData" /></td>
													</tr>
												</tbody>
												<!-- /ko -->
												<!-- ko if: Attendees().length > 0 -->
												<tbody data-bind="foreach: Attendees">
													<tr>
														<td class="checkcell"><input type="checkbox" class="checkcontact" data-bind="checked: selected, attr: {'data-id': id, onclick: 'uncheckall()'}" /></td>
														<td data-bind="text: name"></td>
														<td data-bind="text: email"></td>
														<!-- ko foreach: $parent.attributeNames() -->
															<td data-bind="text: $data.name() == 'Owner' ? $parent.owner : $parents[1].getAttributeValue($parent, $data.name())"></td>
														<!-- /ko -->
													</tr>
												</tbody>
												<!-- /ko -->						
											</table>
										</div>
									</div>
									<div class="col-md-2x" style="text-align: center; margin-top: 55px;">
										<a class="iconbutton" onclick="$('#configure-attributes-dialog').modal();" data-toggle="tooltip" title="<spring:message code="label.ConfigureTables" />"><span class="glyphicon glyphicon-wrench"></span></a><br />
										<div style="margin-top: 230px;">
											<a data-bind="click: moveContacts" class="iconbutton" data-toggle="tooltip" style="font-size: 20px;" title="<spring:message code="label.AddToGuestlist" />"><span class="glyphicon glyphicon-chevron-right"></span><span class="glyphicon glyphicon-chevron-right" style="margin-left: -9px;"></span></a>
										</div>
									</div>
									<div class="col-md-5x">
										<div class="tabletitle">
											<spring:message code="label.GuestList" />
											<div class="info">
												<spring:message code="info.CollectContacts" />
											</div>	
										</div>
										<div id="selectedcontactshead" style="max-width: 100%; overflow-y: scroll; overflow-x: hidden;">
											<table class="table table-bordered table-styled table-striped ptable" style="overflow-y: hidden; min-height: 500px;">
												<thead>
													<tr>
														<th class="checkcell"><input id="checkallselectedcontacts" type="checkbox" onclick="_participants.selectedGroup().checkAll($(this).is(':checked'))" /></th>
														<th><spring:message code="label.Name" /></th>
														<th><spring:message code="label.Email" /></th>
														<!-- ko foreach: attributeNames() -->
															<th class="attribute" data-bind="text: name, attr: {'data-id': id}"></th>
														<!-- /ko -->
													</tr>
													<tr class="table-styled-filter">
														<th class="checkcell">&nbsp;</th>
														<th class="filtercell">
															<input onkeyup="checkReturn(event, this); checkFilterCell($(this).closest('.filtercell'), true)" type="text" maxlength="255" style="margin:0px;" id="selectednamefilter" />
														</th>
														<th class="filtercell">
															<input onkeyup="checkReturn(event, this); checkFilterCell($(this).closest('.filtercell'), true)" type="text" maxlength="255" style="margin:0px;" id="selectedemailfilter" />
														</th>												
														<!-- ko foreach: attributeNames() -->
															<th class="filtercell">
																<input onkeyup="checkReturn(event, this); checkFilterCell($(this).closest('.filtercell'), true)" class="selectedattributefilter" type="text" maxlength="255" style="margin:0px;" data-bind="attr: {id: id, 'data-name': name}" />
															</th>
														<!-- /ko -->
													</tr>
												</thead>
											</table>
										</div>
										<div id="selectedcontactsdiv" class="participantstablediv" style="margin-bottom: 10px;">
											<table class="table table-bordered table-styled table-striped ptable">											
												<!-- ko if: selectedGroup() != null && selectedGroup().attendees().length == 0 -->
												<tbody>
													<tr>
														<td style="padding-top: 100px; text-align: center;" data-bind="attr: {colspan: attributeNames().length + 3}"><spring:message code="label.NoContactsSelected" /></td>
													</tr>
												</tbody>
												<!-- /ko -->
												
												<!-- ko if: selectedGroup() != null && selectedGroup().attendees().length > 0 -->
												<tbody data-bind="foreach: selectedGroup().attendees">
													<!-- ko if: hidden() == false -->
													<tr>
														<td class="checkcell"><input type="checkbox" class="checkcontact" data-bind="checked: selected, attr: {'data-id': id, onclick: 'uncheckallselected()'}" /></td>
														<td data-bind="text: name"></td>
														<td data-bind="text: email"></td>
														<!-- ko foreach: $parent.attributeNames() -->
															<td data-bind="text: $data.name() == 'Owner' ? $parent.owner : $parents[1].getAttributeValue($parent, $data.name())"></td>
														<!-- /ko -->
													</tr>
													<!-- /ko -->												
												</tbody>
												<!-- /ko -->									
											</table>
										</div>
										<!-- ko if: selectedGroup() != null -->
										<a class="iconbutton" style="margin-left: 20px;" data-bind="click: selectedGroup().removeSelected"><span class="glyphicon glyphicon-trash"></span></a>
										<spring:message code="label.RemoveFromGuestList" /><br />
										<!-- /ko -->	
									</div>
								</div>							
							</div>
						
							<div style="text-align: center; margin-top: 30px; margin-bottom: 20px; width: 800px; margin-left: auto; margin-right: auto;">
								<div style="float: left; width: 200px; height: 20px; text-align: left;"></div>
								<div style="float: right; width: 200px; text-align: right;">
									<a class="btn btn-default" onclick="_participants.Page(1)"><spring:message code="label.Cancel" /></a>	
								</div>
								<a class="btn btn-primary" onclick="_participants.Step(2)"><spring:message code="label.NextStep" /></a>	
							</div>
						</div>
						
						<div id="create-step-2-contacts" data-bind="visible: Step() == 2" style="max-width: 900px; margin-left: auto; margin-right: auto;" >
							<!-- ko if: selectedGroup() != null -->
							<span class='mandatory' aria-label='Mandatory'>*</span><spring:message code="label.NameYourGuestList" />
							<input type="textbox" style="width: 400px" class="form-control required" data-bind="value: selectedGroup().name" />
							<!-- /ko -->
							<div class="tabletitle" style="margin-top: 20px;">
								<spring:message code="label.GuestList" />
								<div class="info">
									<spring:message code="info.CurrentGuestlistEntries" />: <span data-bind="text: selectedGroup() != null ? selectedGroup().attendees().length : '0'"></span>
								</div>	
							</div>
							<div id="selectedcontactshead2" style="max-width: 900px; overflow-y: scroll; overflow-x: hidden;">
								<table class="table table-bordered table-styled table-striped ptable" style="overflow-y: hidden; min-height: 500px;">
									<thead>
										<tr>
											<th><spring:message code="label.Name" /></th>
											<th><spring:message code="label.Email" /></th>
											<!-- ko foreach: attributeNames() -->
												<th class="attribute" data-bind="text: name, attr: {'data-id': id}"></th>
											<!-- /ko -->
										</tr>
									</thead>
								</table>
							</div>
							<div id="selectedcontactsdiv2" class="participantstablediv" style="margin-bottom: 10px; max-width: 900px;">
								<table class="table table-bordered table-styled table-striped ptable">											
									<!-- ko if: selectedGroup() != null && selectedGroup().attendees().length == 0 -->
									<tbody>
										<tr>
											<td style="padding-top: 100px; text-align: center;" data-bind="attr: {colspan: attributeNames().length + 2}"><spring:message code="label.NoContactsSelected" /></td>
										</tr>
									</tbody>
									<!-- /ko -->
									
									<!-- ko if: selectedGroup() != null && selectedGroup().attendees().length > 0 -->
									<tbody data-bind="foreach: selectedGroup().attendees">
										<tr>
											<td data-bind="text: name"></td>
											<td data-bind="text: email"></td>
											<!-- ko foreach: $parent.attributeNames() -->
												<td data-bind="text: $parents[1].getAttributeValue($parent, $data.name())"></td>
											<!-- /ko -->
										</tr>													
									</tbody>
									<!-- /ko -->									
								</table>
							</div>
							
							<div style="text-align: center; margin-top: 30px; margin-bottom: 20px; width: 800px; margin-left: auto; margin-right: auto;">
								<div style="float: left; width: 200px; text-align: left;">
									<a class="btn btn-default" onclick="_participants.Step(1)"><spring:message code="label.PreviousStep" /></a>	
								</div>
								<div style="float: right; width: 200px; text-align: right;">
									<a class="btn btn-default" onclick="_participants.Page(1)"><spring:message code="label.Cancel" /></a>	
								</div>
								<a class="btn btn-primary" onclick="_participants.Save()"><spring:message code="label.SaveGuestlist" /></a>	
							</div>						
						</div>
						
					</div>
				</c:otherwise>
			</c:choose>
		</div>
		
		<div id="neweclist" data-bind="visible: Page() == 4">
			<div id="action-bar" class="container action-bar" style="width: 100%;">
				<div class="row">
					<div class="col-md-4" style="text-align: left">
						<a onclick="_participants.Page(1)"><spring:message code="label.Participants" /></a> <span class="glyphicon glyphicon-menu-right" style="font-size: 90%"></span> 
						<!-- ko if: selectedGroup() != null && selectedGroup().id() == 0 -->
						<spring:message code="label.CreateNewEUGuestlist" />
						<!-- /ko -->
						
						<!-- ko if: selectedGroup() != null && selectedGroup().id() > 0 -->
						<spring:message code="label.EditGuestlist" />
						<!-- /ko -->
					</div>
					<div class="col-md-4" style="text-align: center">
						<ul class="progressbar">
							<li data-bind="attr: {class: Step() == 1 ? 'active' : ''}"><span data-bind="attr: {style: Step() == 2 ? 'color: #43ff43' : ''}" class="glyphicon glyphicon-ok"></span><spring:message code="label.SelectContacts" /></li>
							<li data-bind="attr: {class: Step() == 2 ? 'active' : ''}"><spring:message code="label.SaveGuestlist" /></li>
						</ul>
					</div>
					<div class="col-md-4" style="text-align: right">
					</div>
				</div>
			</div>	
		
			<div class="fullpageform" style="padding-top: 0px">
				<div data-bind="visible: Step() == 1">						
					<div class="row">
						<div class="col-md-5x">	
							<span class='mandatory' aria-label='Mandatory'>*</span><spring:message code="label.Domain" /><br />
							<select id="domain" data-bind="value: Domain" onchange="_participants.loadUsers(true)" class="small-form-control" style="width: auto; min-width: 450px; margin-bottom: 20px;" >
								<option></option>
								<c:forEach items="${domains}" var="domain" varStatus="rowCounter">
									<option value="${domain.key}">${domain.value} </option>	
								</c:forEach>
							</select>	
						</div>
					</div>
					<div class="row">
						<div class="col-md-5x">										
							<div class="tabletitle">
								<spring:message code="label.Contacts" />
								<div class="info">
									<spring:message code="info.SelectContacts" />
								</div>
							</div>
							<div id="eccontactshead" style="max-width: 100%; overflow-y: scroll; overflow-x: hidden;">
								<table class="table table-bordered table-striped table-styled ptable" style="overflow-y: hidden">
									<thead>
										<tr>
											<th class="checkcell"><input id="checkalleccontacts" type="checkbox" onclick="_participants.checkAll($(this).is(':checked'))" /></th>
											<th><spring:message code="label.Name" /></th>
											<th><spring:message code="label.Email" /></th>
											<th><spring:message code="label.Department" /></th>
										</tr>
										<tr class="table-styled-filter">
											<th class="checkcell">&nbsp;</th>
											<th class="filtercell">
												<input onkeyup="checkReturn(event, this); checkFilterCell($(this).closest('.filtercell'), true)" type="text" maxlength="255" style="margin:0px;" id="ecnamefilter" />
											</th>
											<th class="filtercell">
												<input onkeyup="checkReturn(event, this); checkFilterCell($(this).closest('.filtercell'), true)" type="text" maxlength="255" style="margin:0px;" id="ecemailfilter" />
											</th>												
											<th class="filtercell">
												<input onkeyup="checkReturn(event, this); checkFilterCell($(this).closest('.filtercell'), true)" type="text" maxlength="255" style="margin:0px;" id="ecdepartmentfilter" />
											</th>	
										</tr>
									</thead>
								</table>
							</div>
							<div id="eccontactsdiv"class="participantstablediv"> 
								<table id="eccontacts" class="table table-bordered table-styled table-striped ptable">
									<!-- ko if: Users().length == 0 -->
									<tbody>
										<tr>
											<td style="padding-top: 100px; text-align: center;" data-bind="attr: {colspan: 4}"><spring:message code="label.NoData" /></td>
										</tr>
									</tbody>
									<!-- /ko -->
									<!-- ko if: Users().length > 0 -->
									<tbody data-bind="foreach: Users">
										<tr>
											<td class="checkcell"><input type="checkbox" class="checkcontact" data-bind="checked: selected, attr: {'data-id': id, onclick: 'uncheckall()'}" /></td>
											<td data-bind="text: givenName + ' ' + surname"></td>
											<td data-bind="text: email"></td>
											<td data-bind="text: departmentNumber"></td>
										</tr>
									</tbody>
									<!-- /ko -->						
								</table>
							</div>
						</div>
						<div class="col-md-2x" style="text-align: center; margin-top: 75px;">
							<div style="margin-top: 230px;">
								<a data-bind="click: moveECContacts" class="iconbutton" data-toggle="tooltip" style="font-size: 20px;" title="<spring:message code="label.AddToGuestlist" />"><span class="glyphicon glyphicon-chevron-right"></span><span class="glyphicon glyphicon-chevron-right" style="margin-left: -9px;"></span></a>
							</div>
						</div>
						<div class="col-md-5x">
							<div class="tabletitle">
								<spring:message code="label.GuestList" />
								<div class="info">
									<spring:message code="info.CollectContacts" />
								</div>	
							</div>
							<div id="selectedeccontactshead" style="max-width: 100%; overflow-y: scroll; overflow-x: hidden;">
								<table class="table table-bordered table-styled table-striped ptable" style="overflow-y: hidden; min-height: 500px;">
									<thead>
										<tr>
											<th class="checkcell"><input id="checkallselectedeccontacts" type="checkbox" onclick="_participants.selectedGroup().checkAll($(this).is(':checked'))" /></th>
											<th><spring:message code="label.Name" /></th>
											<th><spring:message code="label.Email" /></th>
											<th><spring:message code="label.Department" /></th>
										</tr>
										<tr class="table-styled-filter">
											<th class="checkcell">&nbsp;</th>
											<th class="filtercell">
												<input onkeyup="checkReturn(event, this); checkFilterCell($(this).closest('.filtercell'), true)" type="text" maxlength="255" style="margin:0px;" id="selectedecnamefilter" />
											</th>
											<th class="filtercell">
												<input onkeyup="checkReturn(event, this); checkFilterCell($(this).closest('.filtercell'), true)" type="text" maxlength="255" style="margin:0px;" id="selectedecemailfilter" />
											</th>												
											<th class="filtercell">
												<input onkeyup="checkReturn(event, this); checkFilterCell($(this).closest('.filtercell'), true)" type="text" maxlength="255" style="margin:0px;" id="selectedecdepartmentfilter" />
											</th>
										</tr>
									</thead>
								</table>
							</div>
							<div id="selectedeccontactsdiv" class="participantstablediv" style="margin-bottom: 10px;">
								<table class="table table-bordered table-styled table-striped ptable">											
									<!-- ko if: selectedGroup() != null && selectedGroup().users().length == 0 -->
									<tbody>
										<tr>
											<td style="padding-top: 100px; text-align: center;" colspan="4"><spring:message code="label.NoContactsSelected" /></td>
										</tr>
									</tbody>
									<!-- /ko -->
									
									<!-- ko if: selectedGroup() != null && selectedGroup().users().length > 0 -->
									<tbody data-bind="foreach: selectedGroup().users">
										<!-- ko if: hidden() == false -->
										<tr>
											<td class="checkcell"><input type="checkbox" class="checkcontact" data-bind="checked: selected, attr: {'data-id': id, onclick: 'uncheckallselected()'}" /></td>
											<td data-bind="text: givenName + ' ' + surname"></td>
											<td data-bind="text: email"></td>
											<td data-bind="text: departmentNumber"></td>
										</tr>
										<!-- /ko -->												
									</tbody>
									<!-- /ko -->									
								</table>
							</div>
							<!-- ko if: selectedGroup() != null -->
							<a class="iconbutton" style="margin-left: 20px;" data-bind="click: selectedGroup().removeSelected"><span class="glyphicon glyphicon-trash"></span></a>
							<spring:message code="label.RemoveFromGuestList" /><br />
							<!-- /ko -->	
						</div>
					</div>							
				</div>
				
				<div data-bind="visible: Step() == 1" style="text-align: center; margin-top: 30px; margin-bottom: 20px; width: 800px; margin-left: auto; margin-right: auto;">
					<div style="float: left; width: 200px; height: 20px; text-align: left;"></div>
					<div style="float: right; width: 200px; text-align: right;">
						<a class="btn btn-default" onclick="_participants.Page(1)"><spring:message code="label.Cancel" /></a>	
					</div>
					<a class="btn btn-primary" onclick="_participants.Step(2)"><spring:message code="label.NextStep" /></a>	
				</div>
			</div>
			
			<div id="create-step-2-ec" data-bind="visible: Step() == 2" style="max-width: 900px; margin-left: auto; margin-right: auto;" >
				<!-- ko if: selectedGroup() != null -->
				<span class='mandatory' aria-label='Mandatory'>*</span><spring:message code="label.NameYourGuestList" />
				<input type="textbox" style="width: 400px" class="form-control required" data-bind="value: selectedGroup().name" />
				<!-- /ko -->
				<div class="tabletitle" style="margin-top: 20px;">
					<spring:message code="label.GuestList" />
					<div class="info">
						<spring:message code="info.CurrentGuestlistEntries" />: <span data-bind="text: selectedGroup() != null ? selectedGroup().users().length : '0'"></span>
					</div>	
				</div>
				<div id="selectedcontactshead2" style="max-width: 900px; overflow-y: scroll; overflow-x: hidden;">
					<table class="table table-bordered table-styled table-striped ptable" style="overflow-y: hidden; min-height: 500px;">
						<thead>
							<tr>
								<th><spring:message code="label.Name" /></th>
								<th><spring:message code="label.Email" /></th>
								<th><spring:message code="label.Department" /></th>
							</tr>
						</thead>
					</table>
				</div>
				<div id="selectedcontactsdiv2" style="margin-bottom: 10px; max-height: 500px; height: 500px; max-width: 900px; overflow-x: auto; overflow-y: scroll;">
					<table style="height: 500px;" class="table table-bordered table-styled table-striped ptable">											
						<!-- ko if: selectedGroup() != null && selectedGroup().users().length == 0 -->
						<tbody>
							<tr>
								<td style="padding-top: 100px; text-align: center;" colspan="3"><spring:message code="label.NoData" /></td>
							</tr>
						</tbody>
						<!-- /ko -->
						
						<!-- ko if: selectedGroup() != null && selectedGroup().users().length > 0 -->
						<tbody data-bind="foreach: selectedGroup().users">
							<tr>
								<td data-bind="text: givenName + ' ' + surname"></td>
								<td data-bind="text: email"></td>
								<td data-bind="text: departmentNumber"></td>
							</tr>													
						</tbody>
						<!-- /ko -->									
					</table>
				</div>
				
				<div style="text-align: center; margin-top: 30px; margin-bottom: 20px; width: 800px; margin-left: auto; margin-right: auto;">
					<div style="float: left; width: 200px; text-align: left;">
						<a class="btn btn-default" onclick="_participants.Step(1)"><spring:message code="label.PreviousStep" /></a>	
					</div>
					<div style="float: right; width: 200px; text-align: right;">
						<a class="btn btn-default" onclick="_participants.Page(1)"><spring:message code="label.Cancel" /></a>	
					</div>
					<a class="btn btn-primary" onclick="_participants.Save()"><spring:message code="label.SaveGuestlist" /></a>	
				</div>			
			</div>
		</div>
		
		<div id="newtokenlist" data-bind="visible: Page() == 5">
			<div id="action-bar" class="container action-bar" style="width: 100%;">
				<div class="row">
					<div class="col-md-4" style="text-align: left">
						<a onclick="_participants.Page(1)"><spring:message code="label.Participants" /></a> <span class="glyphicon glyphicon-menu-right" style="font-size: 90%"></span> 
						<!-- ko if: selectedGroup() != null && selectedGroup().id() == 0 -->
						<spring:message code="label.CreateNewTokenGuestlist" />
						<!-- /ko -->
						
						<!-- ko if: selectedGroup() != null && selectedGroup().id() > 0 -->
						<spring:message code="label.EditGuestlist" />
						<!-- /ko -->
					</div>
					<div class="col-md-4" style="text-align: center">
						<ul class="progressbar">
							<li data-bind="attr: {class: Step() == 1 ? 'active' : ''}"><span data-bind="attr: {style: Step() == 2 ? 'color: #43ff43' : ''}" class="glyphicon glyphicon-ok"></span><spring:message code="label.CreateTokens" /></li>
							<li data-bind="attr: {class: Step() == 2 ? 'active' : ''}"><spring:message code="label.SaveGuestlist" /></li>
						</ul>
					</div>
					<div class="col-md-4" style="text-align: right">
					</div>
				</div>
			</div>	
		
			<div class="fullpageform" style="padding-top: 40px; width: 700px; margin-left: auto; margin-right: auto;">
				<div data-bind="visible: Step() == 1">						
					<div class="row">
						<div class="col-md-12">	
							<spring:message code="label.AddTokens" />:
							<input type="text" id="numtokens" value="10" class="form-control" style="margin: 0; width: 80px" />
							<!-- ko if: selectedGroup() != null -->
							<a class="iconbutton" data-bind="click: selectedGroup().addTokens" data-toggle="tooltip" title="<spring:message code="label.Add" />"><span class="glyphicon glyphicon-plus"></span></a>
							<!-- /ko -->
						</div>
					</div>
					<div class="row">
						<div class="col-md-12">
							<div class="tabletitle" style="margin-top: 20px">
								<spring:message code="label.GuestList" />
								<div class="info">
									<spring:message code="info.AddTokens" />
								</div>	
							</div>
							<div id="selectedtokenshead" style="max-width: 100%; overflow-y: scroll; overflow-x: hidden;">
								<table class="table table-bordered table-styled table-striped ptable" style="overflow-y: hidden; min-height: 500px;">
									<thead>
										<tr>
											<th class="checkcell"><input id="checkallselectedtokens" type="checkbox" onclick="_participants.selectedGroup().checkAll($(this).is(':checked'))" /></th>
											<th><spring:message code="label.Token" /></th>
											<th><spring:message code="label.Active" /></th>
										</tr>
									</thead>
								</table>
							</div>
							<div id="selectedtokenssdiv" style="margin-bottom: 10px; max-height: 500px; height: 500px; max-width: 100%; overflow-x: auto; overflow-y: scroll;">
								<table style="height: 500px" class="table table-bordered table-styled table-striped ptable">											
									<!-- ko if: selectedGroup() != null && selectedGroup().tokens().length == 0 -->
									<tbody>
										<tr>
											<td style="padding-top: 100px; text-align: center;" colspan="4"><spring:message code="label.NoData" /></td>
										</tr>
									</tbody>
									<!-- /ko -->
									
									<!-- ko if: selectedGroup() != null && selectedGroup().tokens().length > 0 -->
									<tbody data-bind="foreach: selectedGroup().tokens">
										<tr>
											<td class="checkcell"><input type="checkbox" class="checkcontact" data-bind="checked: selected, attr: {'data-id': id, onclick: 'uncheckallselected()'}" /></td>
											<td data-bind="text: uniqueId"></td>
											<td data-bind="text: deactivated() ? '0' : '1', attr: {id: 'active' + $data.uniqueId}"></td>
										</tr>													
									</tbody>
									<!-- /ko -->									
								</table>
							</div>
							<!-- ko if: selectedGroup() != null -->
							<a class="iconbutton" style="margin-left: 20px;" data-bind="click: selectedGroup().removeSelected"><span class="glyphicon glyphicon-trash"></span></a>
							<spring:message code="label.RemoveTokensFromGuestList" /><br />
														
							<!-- ko if: selectedGroup().id() != 0 -->
							<a class="iconbutton" style="margin-left: 20px;" data-bind="click: selectedGroup().activateSelected"><span class="glyphicon glyphicon-play"></span></a>
							<spring:message code="label.ActivateSelectedTokens" /><br />
							
							<a class="iconbutton" style="margin-left: 20px;" data-bind="click: selectedGroup().deactivateSelected"><span class="glyphicon glyphicon-stop"></span></a>
							<spring:message code="label.DeactivateSelectedTokens" /><br />
							<!-- /ko -->	

							<!-- /ko -->
						</div>
					</div>
					
					<div style="text-align: center; margin-top: 30px; margin-bottom: 20px; width: 660px; margin-left: auto; margin-right: auto;">
						<div style="float: left; width: 200px; height: 20px; text-align: left;"></div>
						<div style="float: right; width: 200px; text-align: right;">
							<a class="btn btn-default" onclick="_participants.Page(1)"><spring:message code="label.Cancel" /></a>	
						</div>
						<a class="btn btn-primary" onclick="_participants.Step(2)"><spring:message code="label.NextStep" /></a>	
					</div>
			    </div>
			
				<div id="create-step-2-tokens" data-bind="visible: Step() == 2" style="max-width: 900px; margin-left: auto; margin-right: auto;" >
					<!-- ko if: selectedGroup() != null -->
					<span class='mandatory' aria-label='Mandatory'>*</span><spring:message code="label.NameYourGuestList" />
					<input type="textbox" style="width: 400px" class="form-control required" data-bind="value: selectedGroup().name" />
					<!-- /ko -->
					<div class="tabletitle" style="margin-top: 20px;">
						<spring:message code="label.GuestList" />
						<div class="info">
							<spring:message code="info.CurrentGuestlistEntries" />: <span data-bind="text: selectedGroup() != null ? selectedGroup().tokens().length : '0'"></span>
						</div>	
					</div>
					<div id="selectedtokenshead2" style="max-width: 900px; overflow-y: scroll; overflow-x: hidden;">
						<table class="table table-bordered table-styled table-striped ptable" style="overflow-y: hidden; min-height: 500px;">
							<thead>
								<tr>
									<th><spring:message code="label.Token" /></th>
									<th><spring:message code="label.Active" /></th>
								</tr>
							</thead>
						</table>
					</div>
					<div id="selectedtokensdiv2" style="margin-bottom: 10px; max-height: 500px; height: 500px; max-width: 900px; overflow-x: auto; overflow-y: scroll;">
						<table class="table table-bordered table-styled table-striped ptable">											
							<!-- ko if: selectedGroup() != null && selectedGroup().tokens().length == 0 -->
							<tbody>
								<tr>
									<td style="padding-top: 100px; text-align: center;" colspan="2"><spring:message code="label.NoData" /></td>
								</tr>
							</tbody>
							<!-- /ko -->
							
							<!-- ko if: selectedGroup() != null && selectedGroup().tokens().length > 0 -->
							<tbody data-bind="foreach: selectedGroup().tokens">
								<tr>
									<td data-bind="text: uniqueId"></td>
									<td data-bind="text: deactivated() ? '0' : '1'"></td>
								</tr>													
							</tbody>
							<!-- /ko -->									
						</table>
					</div>	
				</div>
				<div data-bind="visible: Step() == 2"style="text-align: center; margin-top: 30px; margin-bottom: 20px; width: 660px; margin-left: auto; margin-right: auto;">
					<div style="float: left; width: 200px; text-align: left;">
						<a class="btn btn-default" onclick="_participants.Step(1)"><spring:message code="label.PreviousStep" /></a>	
					</div>
					<div style="float: right; width: 200px; text-align: right;">
						<a class="btn btn-default" onclick="_participants.Page(1)"><spring:message code="label.Cancel" /></a>	
					</div>
					<a class="btn btn-primary" onclick="_participants.Save()"><spring:message code="label.SaveGuestlist" /></a>	
				</div>	
			</div>
		</div>
	</div>

	<%@ include file="../footer.jsp" %>	
	<%@ include file="../addressbook/configure.jsp" %>
	
	<script type="text/javascript">
		var p_activated = '<spring:message code="message.ParticipantsGroupActivatedSuccessfully" />';
		var p_deactivated = '<spring:message code="message.ParticipantsGroupDeactivatedSuccessfully" />';
		var p_deleted = '<spring:message code="message.ParticipantsGroupDeletedSuccessfully" />';
		var p_operations = '<spring:message code="message.OperationsExecutedSuccessfully" />';
		var p_guestlistcreated = '<spring:message code="info.GuestListCreated" />';
		var p_mailsstarted = '<spring:message code="info.MailsStarted" />';
	</script>
	
	<c:if test="${action != null}">
		<c:choose>
			<c:when test='${action == "activated"}'>
				<script type="text/javascript"> 
					showSuccess(p_activated);
				</script>
			</c:when>
			<c:when test='${action == "deactivated"}'>
				<script type="text/javascript"> 
					showSuccess(p_deactivated);
				</script>
			</c:when>
			<c:when test='${action == "deleted"}'>
				<script type="text/javascript"> 
					showSuccess(p_deleted);
				</script>
			</c:when>
			<c:when test='${action == "operations"}'>
				<script type="text/javascript"> 
					showSuccess(p_operations);
				</script>
			</c:when>
			<c:when test='${action == "guestlistcreated"}'>
				<script type="text/javascript"> 
					showSuccess(p_guestlistcreated);
				</script>
			</c:when>
			<c:when test='${action == "mailsstarted"}'>
				<script type="text/javascript"> 
					showInfo(p_mailsstarted);
				</script>
			</c:when>	
		</c:choose>
	</c:if>
	<c:if test="${error != null && error == 'namemissing'}">
		<script type="text/javascript"> 
			showError('<spring:message code="error.ParticipantsGroupNameMissing" />');
		</script>
	</c:if>
	
	<div class="modal" id="delete-list-dialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
		<div class="modal-body">
			<b><spring:message code="question.DeleteGuestList" /></b><br /><br />
			<spring:message code="label.GuestListToDelete" /><span style="margin-left: 10px" id="guestlisttobedeleted"></span>
		</div>
		<div class="modal-footer">
			<img id="delete-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a id="btnDeleteFromParticipant"  onclick="_participants.deleteGuestList();" class="btn btn-primary" data-dismiss="modal"><spring:message code="label.Yes" /></a>
			<a id="btnCancelDeleteFromParticipant"  class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>					
		</div>
		</div>
		</div>
	</div>

</body>
</html>