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
	
	<script type="text/javascript" src="${contextpath}/resources/js/jquery.stickytableheaders.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/configure.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/participants.js?version=<%@include file="../version.txt" %>"></script>
	
	<style type="text/css">
		.highlighted {
			background-color: #FF9900;
		}
		
		.department {
			-moz-user-select: none;
			-webkit-user-select: none;
			user-select: none;
		}
		
	    #sortable { list-style-type: none; margin: 0; padding: 0; width: 190px; }
	    #sortable li { margin: 0 0px 0px 0px; padding: 0.4em; padding-left: 1.5em; height: 33px; }
	    #sortable li span:not(.glyphicon) { margin-left: -1.3em; }
	    
	    .dep-tree { list-style-type: none; margin: 0; padding: 0; }
	    .dep-tree-child { margin-left: 30px;}
	    
	    #participantsstatic td, #participantsstatic th, #participantsstaticheader th {
	    	padding: 1px !important;
	    	text-align: left;
	    	width: 150px;
	    	border: 1px solid #ddd;
	    	word-wrap: break-word;
	    }
	    
	    #selectedparticipantsstatic td, #selectedparticipantsstatic th {
	    	padding: 1px !important;
	    	text-align: left;
	    	border: 1px solid #ddd;
	    }
	    
	    .filtercell {
	    	text-align: left;
	    }
	    
	    [class^="icon-"], [class*=" icon-"] {
			width: 16px;
		}
		
		.filtertools {
			float: right;
		}
		
		.dep-tree .check {
			margin-right: 5px !important;
		}
	
    </style>
	
	<script type="text/javascript">	
	
		window.setTimeout("checkFinishedGuestlists()", 10000);
	
		var attributeNames = new Array();
		
		<c:forEach items="${attributeNames}" var="attributeName" varStatus="rowCounter">
			attributeNames.push("${attributeName.id}");				
		</c:forEach>
		
		var selectedDepartments = new Array();
		
		<c:if test="${selectedParticipationGroup != null && selectedParticipationGroup.departments != null}">		
			<c:forEach items="${selectedParticipationGroup.departments}" var="department" varStatus="rowCounter">
				selectedDepartments.push("${department}");		
			</c:forEach>
		</c:if>
		var selectedDomain = "${defaultDomain}";   //"cec.eusurvey.int";  //eu.europa.ec
		<c:if test="${selectedParticipationGroup != null && selectedParticipationGroup.domainCode != null}">
			selectedDomain = "${selectedParticipationGroup.domainCode}";
		</c:if>
		
		var selectedgroup = null;
		function showExportDialog(type, format, group)
		{
			selectedgroup = group;
			exportType = type;
			exportFormat = format;
			$('#export-name').val("");
			$('#export-name-dialog').find(".validation-error").hide();
			$('#export-name-dialog-type').text(format.toUpperCase());
			$('#export-name-dialog').modal();	
			$('#export-name-dialog').find("input").first().focus();
		}
		
		function startExport(name)
		{
			// check again for new exports
			window.checkExport = true;
			
			$.ajax({
				type:'POST',
				  url: '${contextpath}/exports/start/' + exportType + "/" + exportFormat,
				  data: {exportName: name, showShortnames: false, allAnswers: false, group: selectedgroup},
				  beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
				  cache: false,
				  success: function( data ) {						  
					  if (data == "success") {
							showExportSuccessMessage();
						} else {
							showExportFailureMessage()
						}
						$('#deletionMessage').addClass('hidden');
				}
			});	
						
			return false;
		}
			
		function checkFinishedGuestlists()
		{
			if ($(".increation").length > 0)
			{
				var ids = "ids=";
				$(".increation").each(function(){
					ids += $(this).attr("data-id") + ";";
				});				
				
				var request = $.ajax({
					 url: "${contextpath}/${sessioninfo.shortname}/management/participants/finishedguestlists",
				  data: ids,
				  dataType: "json",
				  cache: false,
				  success: function(list)
				  {
					  for (var i = 0; i < list.length; i++ )
					  {
						 var id = list[i].substring(0, list[i].indexOf("|"));
						 var participants = list[i].substring(list[i].indexOf("|")+1);
						 
						 var row = $(".increation[data-id='" + id + "']");
						 $(row).css("background-color","#D6FFDA");
						 $(row).find("[data-class='deactivatebutton']").removeAttr("onclick").removeClass("disabled");
						 $(row).find("[data-class='sendbutton']").removeAttr("onclick").removeClass("disabled").attr('href','${contextpath}/${sessioninfo.shortname}/management/sendInvitations/' + id);
						 $(row).find("[data-class='editbutton']").removeAttr("onclick").removeClass("disabled").attr('href','${contextpath}/${sessioninfo.shortname}/management/participantsEdit?id=' + id);
						 
						 if (participants == "error2")
						 {
							 showError("<spring:message code="error.MaxTokenNumberExceeded" />");
						 } else if (participants == "error1")
						 {
							 showError("<spring:message code="error.ProblemDuringSave" />");
						 } else {
							 $(row).find("[data-class='participants']").text(participants);
							 
							 if ($(row).attr("data-type") == "Token")
							 {
								 $(row).find("[data-class='invited']").text(participants);
							 }
							 
						 };
						
					  };
				  },
				  error: function(jqXHR, textStatus, errorThrown)
				  {
					  alert(textStatus);
				  }
				});
				
				window.setTimeout("checkFinishedGuestlists()", 60000);
			};
			
			if ($(".inrunningmails").length > 0)
			{
				var ids = "surveyUid=${form.survey.uniqueId}&ids=";
				$(".inrunningmails").each(function(){
					ids += $(this).attr("data-id") + ";";
				});				
				
				var request = $.ajax({
					 url: "${contextpath}/${sessioninfo.shortname}/management/participants/finishedguestlistsmail",
				  data: ids,
				  dataType: "json",
				  cache: false,
				  success: function(list)
				  {
					  for (var i = 0; i < list.length; i++ )
					  {
						 var id = list[i].key;
						 var count = list[i].value;
						 
						 var row = $(".inrunningmails[data-id='" + id + "']");
						 $(row).css("background-color","#D6FFDA");
						 
						 $(row).find("[data-class='invited']").html(count);
						 
					  };
				  },
				  error: function(jqXHR, textStatus, errorThrown)
				  {
					  alert(textStatus);
				  }
				});
				
				window.setTimeout("checkFinishedGuestlists()", 60000);
			};	
		}
		
		var groupToDelete = null;
		function deleteList()
		{
			$('#delete-list-dialog').modal('hide');
			$('#hider').show();
			window.location = '<c:url value="/${sessioninfo.shortname}/management/participantsDelete" />?id=' + groupToDelete;
		}
		
		function createNewGuestlist()
		{
			$('#add-participants-name').val('');
			$('#token-table').find('tbody').empty();
			$('#add-participants-dialog2-token-groupname').val('');
			$('#add-participants-dialog2-token-groupid').val('');
			$('#add-participants-dialog2-token-edit-form').empty();
			$('#add-participants-dialog1').modal();
		}
		
		var newPage = 1;
		function loadMoreTokens(t)
		{
			$( "#add-participants-dialog2-token-busy" ).show();
			var s = "page=" + newPage++ + "&rows=20&group=" + $("#selectedParticipationGroup").val();	
			
			if (t != null) s = s + "&token=" + t;
			
			$(".dialog-wait-image").show();
			
			$.ajax({
				type:'GET',
				  url: "${contextpath}/${sessioninfo.shortname}/management/tokensjson",
				  dataType: 'json',
				  data: s,
				  cache: false,
				  error: function() {
					  $(".dialog-wait-image").hide();
				  },
				  success: function( list ) {
					  
					  if (list.length < 20)
					  {
						$('#loadmoretokensbutton').hide();  
					  } else {
						$('#loadmoretokensbutton').show();  
					  }
				  
					  for (var i = 0; i < list.length; i++ )
					  {
						 var active = list[i].substring(0,1) == 1;
						 var token = list[i].substring(1);						  
						  
						 addTokenRow(active, token, false);							
					  }
					  
					  $(window).trigger('resize.stickyTableHeaders');
					  
					  $(".dialog-wait-image").hide();
					  $( "#add-participants-dialog2-token-busy" ).hide();				  
				}});
		}
		
		function addTokenRow(active, token, unsaved)
		{			
			var tr = document.createElement("tr");
			 
			 if (!active)
			 {
				 $(tr).css("background-color","#faa");
			 }
			 
			 $(tr).attr("id","row" + token);
			 
			 var td = document.createElement("td");
			 $(td).text(token);
			 
			 var input = document.createElement("input");
			 $(input).attr("type","hidden").attr("id", "token" + token).addClass("tokenrow");
			 
			 if (!unsaved)
			 {
				 $(input).attr("name", "token" + token)
			 } else {
				 $(input).attr("name", "newtoken" + token).val(token);
			 }
			 			 
			 $(td).append(input);
			
			 $(tr).append(td);
			 
			 <c:if test="${readonly == null}">
			 
				 td = document.createElement("td");
				 
				 var a = document.createElement("a");
				 
				 $(a).attr("data-toggle","tooltip").attr("title", "<spring:message code="label.Remove" />").addClass("iconbutton").append('<span class="glyphicon glyphicon-remove"></span>');
				 
				 if (!unsaved)
				 {
					 $(a).attr("onclick","deleteToken('" + token + "','${selectedParticipationGroup.id}')");
				 } else {
					 $(a).attr("onclick","$(this).parent().parent().remove()");
				 }
				
				 $(td).append(a);
				 $(a).tooltip(); 
				 
				 a = document.createElement("a");
				 $(a).attr("id", "activate" + token);
				 
				 if (!unsaved)
				 {
					 if (active)
					 {						 
					 	$(a).attr("data-toggle","tooltip").attr("title", "<spring:message code="label.Deactivate" />").addClass("iconbutton").append('<span class="glyphicon glyphicon-stop"></span>').attr("onclick","deactivateToken('" + token + "','${selectedParticipationGroup.id}')");
					 } else {
						$(a).attr("data-toggle","tooltip").attr("title", "<spring:message code="label.Activate" />").addClass("iconbutton").append('<span class="glyphicon glyphicon-play"></span>').attr("onclick","reactivateToken('" + token + "','${selectedParticipationGroup.id}')");
					 }
				 }			
				 	
				 $(td).append(a);
				 $(a).tooltip(); 
				
				 $(tr).append(td);
			 
			 </c:if>
			 
			 $("#token-table").find("tbody").append(tr);	
		}
		
		function addTokens()
		{
			var tokens = $("#number-new-tokens").val();
			
			var result = validateInput($("#add-participants-dialog2-token-form"));
			
			if (result == false)
			{
				return;
			}
			
			var s = "tokens=" + tokens;
			
			$(".dialog-wait-image").show();
			
			$.ajax({
				type:'GET',
				  url: "${contextpath}/${sessioninfo.shortname}/management/participants/createTokens",
				  dataType: 'json',
				  data: s,
				  cache: false,
				  success: function( list ) {
					  
					  for (var i = 0; i < list.length; i++ )
					  {
					  	addTokenRow(true, list[i], true);
					  }
					  
					  $(".dialog-wait-image").hide();
				  }
			});
		}
		
		function cancelEditTokens()
		{
			$(".tokenrow").remove();
			$("#add-participants-dialog2-token").modal("hide");		
		}
		
		function saveEditTokens()
		{
			$('#add-participants-dialog2-token-edit-form').submit();
		}
		
		function searchToken(reset)
		{
			$("#token-table").find("tbody").empty();

			newPage = 1;
			
			if (reset)
			{
				$("#add-participants-dialog2-search-token").val('');
				loadMoreTokens();
			} else {
				loadMoreTokens($("#add-participants-dialog2-search-token").val().trim());
			}			
		}
		
		function deleteToken(token,id)
		{			
			$("#token" + token).val("delete");
			$("#row" + token).hide();
		}
		
		function deactivateToken(token,id)
		{			
			$("#token" + token).val("deactivate");
			$("#row" + token).css("background-color","rgb(255, 170, 170);");
			
			var a = $("#activate" + token);
			$(a).attr("data-toggle","tooltip").attr("data-original-title", "<spring:message code="label.Activate" />").empty();
			$(a).append('<i class="glyphicon glyphicon-play"></i>').attr("onclick","reactivateToken('" + token + "','${selectedParticipationGroup.id}')");
		}
		
		function reactivateToken(token,id)
		{			
			$("#token" + token).val("activate");
			$("#row" + token).css("background-color","");
			
			var a = $("#activate" + token);
			$(a).attr("data-toggle","tooltip").attr("data-original-title", "<spring:message code="label.Deactivate" />").empty();
			$(a).append('<span class="glyphicon glyphicon-stop"></span>').attr("onclick","deactivateToken('" + token + "','${selectedParticipationGroup.id}')");
		}
		
		var infinitePage = 0;
		
		function loadMoreGroups()
		{
			$( "#wheel" ).show();
			var s = "page=" + infinitePage++ + "&rows=50";	
			
			$.ajax({
				type:'GET',
				url: "<c:url value="/${sessioninfo.shortname}/management/participantsjson" />",
				dataType: 'json',
				data: s,
				cache: false,
				success: refreshGroups
				});
		}
		
		$(function(){
			$("#participantstable").stickyTableHeaders();
			loadMoreGroups();
			$('[data-toggle="tooltip"]').tooltip(); 
		});
		
		$(window).scroll(function() {
		    if ($(window).scrollTop() <= $(document).height() - $(window).height() && $(window).scrollTop() >= $(document).height() - $(window).height() - 10) {
		    	loadMoreGroups();
		  }
		 });
		
		function refreshGroups( list, textStatus, xhr ) {
			
			if (list.length == 0)
			{
				infinitePage--;
				$("#load-more-div").hide();
			}
			refreshGroupsBasic( list, textStatus, xhr );
		}
			
		function refreshGroupsBasic( list, textStatus, xhr ) {
			
			for (var i = 0; i < list.length; i++ )
			  {
				var row = document.createElement("tr");
				
				if (list[i].inCreation)
				{
					$(row).css('background-color','#FFE0BF').addClass("increation").attr("data-id",list[i].id);
				} else if (list[i].runningMails)
				{
					$(row).css('background-color','#D1EBFF').addClass("runningmails").attr("data-id",list[i].id);
				} else if (list[i].error != null)
				{
					$(row).css('background-color','#FFD6DA');
				} else if (list[i].active)
				{
					$(row).css('background-color','#D6FFDA');
				}
			
				var td = document.createElement("td");	
				$(td).html(list[i].name);
				$(row).append(td);
				
				var td = document.createElement("td");	
				$(td).html(list[i].type);
				$(row).append(td);
				
				var td = document.createElement("td");	
				$(td).attr("data-class","participants");
				if (list[i].type == 'ECMembers')
				{
					$(td).html(list[i].children);
				} else if (list[i].type == 'Token')
				{
					$(td).html(list[i].all);
				} else {
					$(td).html(list[i].children);
				}			
				$(row).append(td);
				
				var td = document.createElement("td");
				$(td).attr("data-class","invited");
				$(td).html(list[i].invited);
				$(row).append(td);
				
				var td = document.createElement("td");
				if (list[i].created != null)
				{
					$(td).html(list[i].formattedDate);
				}
				$(row).append(td);
				
				td = document.createElement("td");
				<c:choose>
					<c:when test="${USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('ManageInvitations') > 1}">
					
						var a = document.createElement("a");
						if (list[i].inCreation)
						{
							$(a).attr("id","btnDeactivateFromParticipant").attr("data-class","deactivatebutton").attr("href","<c:url value="/${sessioninfo.shortname}/management/participantsDeactivate" />?id=" + list[i].id).attr("onclick","return false;").addClass("iconbutton disabled").attr("data-toggle","tooltip").attr("title","<spring:message code="label.Deactivate" />").html("<span class='glyphicon glyphicon-stop'></span>");
							$(td).append(a);
							a = document.createElement("a");
							$(a).attr("id","btnDeleteDisabledFromParticipant").addClass("iconbutton disabled").attr("data-toggle","tooltip").attr("title","<spring:message code="label.Remove" />").html("<span class='glyphicon glyphicon-remove'></span>");
							$(td).append(a);
						} else if (list[i].active) 
						{
							$(a).attr("id","btnDeactivateFromParticipant").attr("href","<c:url value="/${sessioninfo.shortname}/management/participantsDeactivate" />?id=" + list[i].id).addClass("iconbutton").attr("data-toggle","tooltip").attr("title","<spring:message code="label.Deactivate" />").html("<span class='glyphicon glyphicon-stop'></span>");
							$(td).append(a);
							a = document.createElement("a");
							$(a).attr("id","btnDeleteDisabledFromParticipant").addClass("iconbutton disabled").attr("data-toggle","tooltip").attr("title","<spring:message code="label.Remove" />").html("<span class='glyphicon glyphicon-remove'></span>");
							$(td).append(a);
						} else {
							$(a).attr("id","btnActivateFromParticipant").attr("href","<c:url value="/${sessioninfo.shortname}/management/participantsActivate" />?id=" + list[i].id).attr("onclick","$('#hider').show();return true;").addClass("iconbutton").attr("data-toggle","tooltip").attr("title","<spring:message code="label.Activate" />").html("<span class='glyphicon glyphicon-play'></span>");
							$(td).append(a);
							a = document.createElement("a");
							$(a).attr("id","btnDeleteEnabledFromParticipant").attr("onclick","groupToDelete = '" + list[i].id +"'; $('#delete-list-dialog').modal('show');").addClass("iconbutton").attr("data-toggle","tooltip").attr("title","<spring:message code="label.Remove" />").html("<span class='glyphicon glyphicon-remove'></span>");
							$(td).append(a);
						}
						
						a = document.createElement("a");
						if (list[i].inCreation && list[i].type != 'Token')
						{					
							$(a).attr("id","btnSendDisabledFromParticipant").attr("data-class","sendbutton").attr("onclick","return false;").addClass("iconbutton disabled").attr("data-toggle","tooltip").attr("title","<spring:message code="label.SendInvitations" />").html("<span class='glyphicon glyphicon-envelope'></span>");
						} else if (list[i].children > 0 && list[i].active)
						{
							$(a).attr("id","btnSendEnabledFromParticipant").attr("href","<c:url value="/${sessioninfo.shortname}/management/sendInvitations" />/" + list[i].id).addClass("iconbutton").attr("data-toggle","tooltip").attr("title","<spring:message code="label.SendInvitations" />").html("<span class='glyphicon glyphicon-envelope'></span>");
						} else if (list[i].children > 0 && list[i].active)
						{
							$(a).attr("id","btnSendEnabledFromParticipant").attr("href","<c:url value="/${sessioninfo.shortname}/management/sendInvitations" />/" + list[i].id).addClass("iconbutton").attr("data-toggle","tooltip").attr("title","<spring:message code="label.SendInvitations" />").html("<span class='glyphicon glyphicon-envelope'></span>");
						} else {
							$(a).attr("id","btnSendDisabledFromParticipant").addClass("iconbutton disabled").attr("data-toggle","tooltip").attr("title","<spring:message code="label.SendInvitations" />").html("<span class='glyphicon glyphicon-envelope'></span>");
						}
						$(td).append(a);
	
						a = document.createElement("a");
						if (list[i].inCreation)
						{
							$(a).attr("id","btnEditDisabledFromParticipant").attr("data-class","editbutton").addClass("iconbutton disabled").attr("data-toggle","tooltip").attr("title","<spring:message code="label.Edit" />").html("<span class='glyphicon glyphicon-pencil'></span>");
						} else {
							$(a).attr("id","btnEditEnabledFromParticipant").attr("href","<c:url value="/${sessioninfo.shortname}/management/participantsEdit" />?id=" + list[i].id).addClass("iconbutton").attr("data-toggle","tooltip").attr("title","<spring:message code="label.Edit" />").html("<span class='glyphicon glyphicon-pencil'></span>");
						}
						$(td).append(a);
						
						if (list[i].type != 'Token' || list[i].type != 'Static')
						{
							a = document.createElement("a");
							$(a).attr("data-toggle", "tooltip").attr("title", "<spring:message code="tooltip.Downloadxls" />").attr("id","startExportTokensxls").addClass("iconbutton").attr("href","#").attr("onclick","showExportDialog('Tokens', 'xls', '" + list[i].id + "')").html("<img src='${contextpath}/resources/images/file_extension_xls_small.png' />");
							$(td).append(a);
							a = document.createElement("a");
							$(a).attr("data-toggle", "tooltip").attr("title", "<spring:message code="tooltip.Downloadods" />").attr("id","startExportTokensods").addClass("iconbutton").attr("href","#").attr("onclick","showExportDialog('Tokens', 'ods', '" + list[i].id + "')").html("<img src='${contextpath}/resources/images/file_extension_ods_small.png' />");
							$(td).append(a);
						}
					</c:when>
					
					<c:when test="${USER.getLocalPrivilegeValue('ManageInvitations') > 0}">
						if (list[i].active) 
						{
							$(td).append('<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Deactivate" />"><span class="glyphicon glyphicon-stop"></span></a>');
							$(td).append('<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Remove" />"><span class="glyphicon glyphicon-remove"></span></a>');
						} else {
							$(td).append('<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Activate" />"><span class="glyphicon glyphicon-play"></span></a>');
							$(td).append('<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Remove" />"><span class="glyphicon glyphicon-remove"></span></a>');
						}
						
						var a = document.createElement("a");
						$(a).attr("id","btnSendDisabledFromParticipant").addClass("iconbutton disabled").attr("data-toggle","tooltip").attr("title","<spring:message code="label.SendInvitations" />").html("<span class='glyphicon glyphicon-envelope'></span>");
						$(td).append(a);
	
						a = document.createElement("a");
						if (list[i].inCreation)
						{
							$(a).attr("id","btnEditDisabledFromParticipant").attr("data-class","editbutton").addClass("iconbutton disabled").attr("data-toggle","tooltip").attr("title","<spring:message code="label.Edit" />").html("<span class='glyphicon glyphicon-pencil'></span>");
						} else {
							$(a).attr("id","btnEditEnabledFromParticipant").attr("href","<c:url value="/${sessioninfo.shortname}/management/participantsEdit" />?id=" + list[i].id).addClass("iconbutton").attr("data-toggle","tooltip").attr("title","<spring:message code="label.Edit" />").html("<span class='glyphicon glyphicon-pencil'></span>");
						}
						$(td).append(a);
						
						if (list[i].type != 'Token' || list[i].type != 'Static')
						{
							a = document.createElement("a");
							$(a).addClass("iconbutton").attr("data-toggle", "tooltip").attr("title", "<spring:message code="tooltip.Downloadxls" />").attr("id","startExportTokensxls").attr("href","#").attr("onclick","showExportDialog('Tokens', 'xls', '" + list[i].id + "')").html("<img src='${contextpath}/resources/images/file_extension_xls_small.png' />");
							$(td).append(a);
							a = document.createElement("a");
							$(a).addClass("iconbutton").attr("data-toggle", "tooltip").attr("title", "<spring:message code="tooltip.Downloadods" />").attr("id","startExportTokensods").attr("href","#").attr("onclick","showExportDialog('Tokens', 'ods', '" + list[i].id + "')").html("<img src='${contextpath}/resources/images/file_extension_ods_small.png' />");
							$(td).append(a);
						}
			
					</c:when>
					
					<c:otherwise>						
						if (list[i].active) 
						{
							$(td).append('<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Deactivate" />"><span class="glyphicon glyphicon-stop"></span></a>');
							$(td).append('<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Remove" />"><span class="glyphicon glyphicon-remove"></span></a>');
						} else {
							$(td).append('<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Activate" />"><span class="glyphicon glyphicon-play"></span></a>');
							$(td).append('<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Remove" />"><span class="glyphicon glyphicon-remove"></span></a>');
						}						
						
						$(td).append('<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.SendInvitations" />"><span class="glyphicon glyphicon-envelope"></span></a>');
						$(td).append('<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Edit" />"><span class="glyphicon glyphicon-pencil"></span></a>');
					</c:otherwise>
				</c:choose>	
				$(row).append(td);				

				$('#participantstable tbody').append(row);
			  }
			
			if($('#participantstable tbody tr').size()==0)
			{
				$('#tbllist-empty').show();
			}
			  
			  $( "#wheel" ).hide();
			  $( "#groups-loading" ).hide();
			  $('[data-toggle="tooltip"]').tooltip();
		}		
		
	
	</script>
		
</head>
<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
		<%@ include file="../menu.jsp" %>
		<%@ include file="formmenu.jsp" %>
		
		<input type="hidden" id="readonlytree" value='${readonly != null ? "true" : "false" }' />
		
		<div id="action-bar" class="container action-bar">
			<div class="row">
				<div class="col-md-12" style="text-align:center">
					<c:choose>
						<c:when test="${USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('ManageInvitations') > 1}">
							<a id="btnCreateGuestListFromParticpant" onclick="createNewGuestlist()" class="btn btn-default"><spring:message code="label.CreateNewGuestlist" /></a>
						</c:when>
						<c:otherwise>
							<a class="btn disabled btn-default"><spring:message code="label.CreateNewGuestlist" /></a>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>	
		
		<div class="fullpageform" style="padding-top:0px">
		
			<table id="participantstable" class="table table-bordered table-styled" style="width: auto; margin-top: 40px; margin-left: auto; margin-right: auto;">
				<thead>
					<tr>
						<th><spring:message code="label.Name" /></th>
						<th><spring:message code="label.Type" /></th>
						<th style="text-align: center;"><spring:message code="label.Participants" /></th>
						<th style="text-align: center;"><spring:message code="label.Invited" /></th>
						<th style="text-align: center;"><spring:message code="label.Created" /></th>
						<th><spring:message code="label.Actions" /></th>
					</tr>
				</thead>
				<tbody></tbody>
			</table>	
			
			<div id="tbllist-empty" class="noDataPlaceHolder" <c:if test="${participants.size() == 0}">style="display:block;"</c:if>>
				<p>
					<spring:message code="label.NoDataParticipantText"/>&nbsp;<img src="${contextpath}/resources/images/icons/32/forbidden_grey.png" alt="no data"/>
				<p>
			</div>	
			
		</div>
		
		<form:form id="saveForm" method="POST" action="${contextpath}/${sessioninfo.shortname}/management/participants">
		
		</form:form>
		
		<form:form id="saveFormStatic" method="POST" action="${contextpath}/${sessioninfo.shortname}/management/participantsStatic">
		
		</form:form>
	</div>

	<%@ include file="../footer.jsp" %>	
	
	<%@ include file="add-participants-step1.jsp" %>
	<%@ include file="add-participants-step2-dynamic.jsp" %>
	<%@ include file="add-participants-step2-static.jsp" %>
	<%@ include file="add-participants-step2-departments.jsp" %>
	<%@ include file="add-participants-step2-token.jsp" %>
	<%@ include file="../addressbook/configure.jsp" %>

	<div class="modal" id="delete-list-dialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
		<div class="modal-body">
			<spring:message code="question.DeleteGuestList" />
		</div>
		<div class="modal-footer">
			<img id="delete-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a id="btnDeleteFromParticipant"  onclick="deleteList();" class="btn btn-info" data-dismiss="modal"><spring:message code="label.Yes" /></a>
			<a id="btnCancelDeleteFromParticipant"  class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>					
		</div>
		</div>
		</div>
	</div>
	
	<div id="hider" class="hideme" style="background-color: #000; opacity:0.7; filter:alpha(opacity=70); width:100%; height:100%; position: absolute; left: 0px; top: 0px; z-index: 3000;">
		<img style="position: fixed; top: 50%; left: 50%; margin-top: -16px; margin-left: -16px;" src="${contextpath}/resources/images/ajax-loader2.gif" />
	</div>
	
<c:if test="${action != null}">
	<c:choose>
		<c:when test='${action == "activated"}'>
			<script type="text/javascript"> 
				showInfo('<spring:message code="message.ParticipantsGroupActivatedSuccessfully" />');
			</script>
		</c:when>
		<c:when test='${action == "deactivated"}'>
			<script type="text/javascript"> 
				showInfo('<spring:message code="message.ParticipantsGroupDeactivatedSuccessfully" />');
			</script>
		</c:when>
		<c:when test='${action == "deleted"}'>
			<script type="text/javascript"> 
				showInfo('<spring:message code="message.ParticipantsGroupDeletedSuccessfully" />');
			</script>
		</c:when>
		<c:when test='${action == "operations"}'>
			<script type="text/javascript"> 
				showInfo('<spring:message code="message.OperationsExecutedSuccessfully" />');
			</script>
		</c:when>
		<c:when test='${action == "guestlistcreated"}'>
			<script type="text/javascript"> 
				showInfo('<spring:message code="info.GuestListCreated" />');
			</script>
		</c:when>
		<c:when test='${action == "mailsstarted"}'>
			<script type="text/javascript"> 
				showInfo('<spring:message code="info.MailsStarted" />');
			</script>
		</c:when>	
	</c:choose>
</c:if>
<c:if test="${error != null}">
	<c:choose>
		<c:when test='${error == "namemissing"}'>
			<script type="text/javascript"> 
				showError('<spring:message code="error.ParticipantsGroupNameMissing" />');
			</script>
		</c:when>
	</c:choose>
</c:if>

<c:if test="${selectedParticipationGroup != null}">

	<input type="hidden" id="selectedParticipationGroup" value="${selectedParticipationGroup.id}" />
	
	<script type="text/javascript"> 
	
	$("#add-participants-name").val('${selectedParticipationGroup.name}');
	$("#add-participants-type").val('${selectedParticipationGroup.type}');
	
	var type = '${selectedParticipationGroup.type}';
	var error = '${grouperror}';
	
	if (type == 'Static')
	{
		$("#add-participants-type").val("static");	
		searchStatic();
		$("#add-participants-dialog2-static").modal();		
	}
	
	if (type == 'Dynamic')
	{
		$("#add-participants-type").val("dynamic");	
		search();
		$("#add-participants-dialog2-dynamic").modal();		
	}
	
	if (type == 'ECMembers')
	{
		$("#add-participants-type").val("departments");	
		
		//open tree to show selected nodes
		openParents();
		
		$("#add-participants-dialog2-departments").modal();		
	}
	
	if (type == 'Token')
	{
		//$("#add-participants-type option[value='tokens']").attr("selected","true");
		$("#add-participants-type").val("tokens");	
		loadMoreTokens();
		$("#add-participants-dialog2-token").modal();		
	}
	
	if (error == '1')
	{
		showError('<spring:message code="error.InvalidTokenNumber" />');
	} else if (error == '2')
	{
		showError("<spring:message code="error.MaxTokenNumberExceeded" />");
	}
	
	</script>
</c:if>

</body>
</html>
