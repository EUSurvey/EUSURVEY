<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Administration" /></title>	
	<%@ include file="../includes.jsp" %>	
	<script type="text/javascript" src="${contextpath}/resources/js/jquery.stickytableheaders.js?version=<%@include file="../version.txt" %>"></script>

	<script type="text/javascript"> 
		$(function() {					
			$("#administration-menu-tab").addClass("active");
			$("#users-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
			
			$(".filtercell").each(function(){
				checkFilterCell(this, true);
			});

		    $("html:not(.legacy) table").stickyTableHeaders("#userTableDiv");
		    updateConfiguration();		    
			
			<c:if test="${error != null}">
				showError('<esapi:encodeForHTML>${error}</esapi:encodeForHTML>');
			</c:if>
			
			<c:if test="${info != null}">
				showInfo('<esapi:encodeForHTML>${info}</esapi:encodeForHTML>');
			</c:if>
			
			<c:if test="${userreferenceserror != null}">
				$("#userreferenceserrordialog").modal("show");
			</c:if>
			
			$('[data-toggle="tooltip"]').tooltip(); 
		});	
		
		var selectedId = null;
		
		function resetSearch()
		{
			$("#clearFilter").val("true");
			$("#load-users").submit();
		}
		
		var loginused = false;
		function createUser()
		{			
			if (validateInput($("#add-user-dialog")))
			{	
				$.ajax({
		              type: "GET",
		              data:  "login=" + $('#add-user-login').val(),			
					  url: "${contextpath}/administration/checkLoginFree",
					  async: false,
					  cache: false,
					  beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
					  success: function(data)
					  {
						 if (data != "free")
						 {
							 $('#add-user-login').after("<div class='validation-error'>" + loginExistsError + "</div>");
							 loginused = true;
						 } else {
							 loginused = false;
						 }
					  },
					  error: function(jqXHR, textStatus, errorThrown)
					  {
						  alert(textStatus);
					  }
					});
			
				if (loginused) return;
				
				$.ajax({
		              type: "POST",
		              data:  "password=" + $('#add-user-password').val(),			
					  url: "${contextpath}/administration/checkPasswordNotWeak",
					  async: false,
					  cache: false,
					  beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
					  success: function(data)
					  {
						 if (data != "weak")
						 {
							 $("#add-wait-animation").show();
							 $("#login").val($('#add-user-login').val());
							 $("#password").val($('#add-user-password').val());
							 $("#firstname").val($('#add-user-firstname').val());
							 $("#lastname").val($('#add-user-lastname').val());
							 $("#email").val($('#add-user-email').val());
							 $("#otheremail").val($('#other-user-email').val());
							 $("#comment").val($('#add-user-comment').val());
							 $("#language").val($('#add-user-language').val());
								
							 var s = "";
							 $("#add-user-dialog").find("input[type='radio']:checked").each(function(){
								if ($(this).hasClass("role"))
								s += $(this).val() + ";";
							 });	
							 $("#roles").val(s);
											
							 $("#create-user").submit();			
						 } else {
							 $('#add-user-password').after("<div class='validation-error'>" + weakPasswordText + "</div>");
						 }
					  },
					  error: function(jqXHR, textStatus, errorThrown)
					  {
						  alert(textStatus);
					  }
					});
			}
		}
		
		function updateUser(id)
		{			
			if (validateInput($("#add-user-dialog")))
			{
				if ($('#add-user-password').val() == "#######")
				{
					updateUserSubmit(id);
					return;
				}
				
				$.ajax({
		              type: "POST",
		              data:  "password=" + $('#add-user-password').val(),			
					  url: "${contextpath}/administration/checkPasswordNotWeak",
					  async: false,
					  cache: false,
					  beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
					  success: function(data)
					  {
						 if (data != "weak")
						 {
							 updateUserSubmit(id);
						 } else {
							 $('#add-user-password').after("<div class='validation-error'>" + weakPasswordText + "</div>");
						 }
					  },
					  error: function(jqXHR, textStatus, errorThrown)
					  {
						  alert(textStatus);
					  }
					});
			}
		}
		
		function updateUserSubmit(id)
		{
			$("#add-wait-animation").show();
			 $("#update-id").val(id);
			 $("#update-password").val($('#add-user-password').val());
			 $("#update-firstname").val($('#add-user-firstname').val());
			 $("#update-lastname").val($('#add-user-lastname').val());
			 $("#update-email").val($('#add-user-email').val());
			 $("#update-otheremail").val($('#other-user-email').val());
			 $("#update-comment").val($('#add-user-comment').val());
			 $("#update-language").val($('#add-user-language').val());
				
			 var s = "";
			 $("#add-user-dialog").find("input[type='radio']:checked").each(function(){
				s += $(this).val() + ";";
			 });	
			 $("#update-roles").val(s);
							
			 $("#update-user").submit();	
		}
		
		function deleteUser()
		{
			$("#delete-wait-animation").show();
			$("#delete-id").val(selectedId);
			$("#delete-user").submit();
		}
		
		function showDeleteDialog(id, login)
		{
			selectedId = id;
			$('#delete-user-dialog').modal();
		}
		
		function showAddDialog()
		{
			$('#add-user-login').val("");
			$('#add-user-login').removeAttr("readonly").removeAttr("disabled");
			$('#add-user-password').val("");
			$('#add-user-password').addClass("required");
			$('#add-user-firstname').val("");
			$('#add-user-lastname').val("");
			$('#add-user-email').val("");
			$('#other-user-email').val("");
			$('#add-user-comment').val("");
			$('#add-user-language').find("option").removeAttr("selected");
			$('#add-user-language').find("option[value='EN']").attr("selected","selected");
			
			$('.role').removeAttr("checked");
			
			$('add-user-button').attr("onclick","createUser()");
			
			$('#add-user-dialog-header1').show();
			$('#add-user-dialog-header2').hide();
			$('#add-user-dialog').modal();
		}
		
		function showEditDialog(id, login, email, otheremail, comment, language, type, roles, first, last, readwrite)
		{
			selectedId = id;
			
			$('#add-user-login').val(login);
			$('#add-user-login').attr("readonly", true).attr("disabled", true);
			$('#add-user-password').val("#######");
			$('#add-user-password').removeClass("required");
			$('#add-user-firstname').val(first);
			$('#add-user-lastname').val(last);
			$('#add-user-email').val(email);
			$('#other-user-email').val(otheremail);
			$('#add-user-comment').val(comment);
			$('#add-user-language').find("option").removeAttr("selected");
			$('#add-user-language').find("option[value='" + language + "']").prop("selected","selected");
			
			if (readwrite)
			{
				$('#add-user-password').removeAttr('readonly').removeAttr('disabled');
				$('#add-user-email').removeAttr('readonly').removeAttr('disabled');
				$('#add-user-comment').removeAttr('readonly').removeAttr('disabled');
				$('#add-user-firstname').removeAttr('readonly').removeAttr('disabled');
				$('#add-user-lastname').removeAttr('readonly').removeAttr('disabled');
				$('#add-user-language').removeAttr('readonly').removeAttr('disabled');
				$('.role').each(function(){
					$(this).removeAttr('disabled');					
				});
			} else {
				$('#add-user-password').attr('readonly', true).attr('disabled', true);
				$('#add-user-email').attr('readonly', true).attr('disabled', true);
				$('#add-user-comment').attr('readonly', true).attr('disabled', true);
				$('#add-user-firstname').attr('readonly', true).attr('disabled', true);
				$('#add-user-lastname').attr('readonly', true).attr('disabled', true);
				$('#add-user-language').attr('readonly', true).attr('disabled', true);
				$('.role').each(function(){
					$(this).attr('disabled', true);					
				});
			}
			
			$('.role').each(function(){
				if (roles.indexOf($(this).val()+";") > -1)
				{
					$(this).prop("checked","checked");
				} else {
					$(this).removeAttr("checked");
				}
			});
			
			$('#add-user-button').attr("onclick","updateUser(" + id + ")");
			
			$('#add-user-dialog-header2').show();
			$('#add-user-dialog-header1').hide();
			$('#add-user-dialog').modal();
		}
		
		function sort(key, ascending)
		{
			$("#sortkey").val(key);
			if (ascending)
			{
				$("#sortorder").val("ASC");
			} else {
				$("#sortorder").val("DESC");
			}
			$("#load-users").submit();
		}
		
		function saveConfiguration()
		{
			var request = $.ajax({
				type: "POST",
	          	 url: "${contextpath}/administration/saveUserConfiguration",
				  data: {name : $("#user-name").is(":checked"), email: $("#user-email").is(":checked"), otherEmail: $("#user-otheremail").is(":checked"), ulang: $("#user-language").is(":checked"), roles: $("#user-roles").is(":checked"), comment: $("#user-comment").is(":checked")},
				  dataType: "json",
				  cache: false,
				  beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},					
				  success: function(data)
				  {			 
					  
				  }
				});
			
			updateConfiguration();
			$("#configure-dialog").modal('hide');
		}
		
		function updateConfiguration()
		{
			
			$("#usertable").find("tr").each(function(){
				if ($("#user-name").is(":checked"))
				{
					$($(this).find("th")[0]).show();
					$($(this).find("td")[0]).show();
				} else {
					$($(this).find("th")[0]).hide();
					$($(this).find("td")[0]).hide();
				}
				if ($("#user-email").is(":checked"))
				{
					$($(this).find("th")[1]).show();
					$($(this).find("td")[1]).show();
				} else {
					$($(this).find("th")[1]).hide();
					$($(this).find("td")[1]).hide();
				}
				if ($("#user-otheremail").is(":checked"))
				{
					$($(this).find("th")[2]).show();
					$($(this).find("td")[2]).show();
				} else {
					$($(this).find("th")[2]).hide();
					$($(this).find("td")[2]).hide();
				}
				if ($("#user-language").is(":checked"))
				{
					$($(this).find("th")[3]).show();
					$($(this).find("td")[3]).show();
				} else {
					$($(this).find("th")[3]).hide();
					$($(this).find("td")[3]).hide();
				}
				if ($("#user-roles").is(":checked"))
				{
					$($(this).find("th")[4]).show();
					$($(this).find("td")[4]).show();
				} else {
					$($(this).find("th")[4]).hide();
					$($(this).find("td")[4]).hide();
				}
				if ($("#user-comment").is(":checked"))
				{
					$($(this).find("th")[5]).show();
					$($(this).find("td")[5]).show();
				} else {
					$($(this).find("th")[5]).hide();
					$($(this).find("td")[5]).hide();
				}
			});
			
		}
		
		function cancelConfiguration()
		{
			if ($($("#usertable").find("tr").find("th")[0] ).is(":visible"))
			{
				$("#user-name").prop("checked","checked");
			} else {
				$("#user-name").removeAttr("checked");
			}
			
			if ($($("#usertable").find("tr").find("th")[1] ).is(":visible"))
			{
				$("#user-email").prop("checked","checked");
			} else {
				$("#user-email").removeAttr("checked");
			}
			
			if ($($("#usertable").find("tr").find("th")[2] ).is(":visible"))
			{
				$("#user-otheremail").prop("checked","checked");
			} else {
				$("#user-otheremail").removeAttr("checked");
			}
			
			if ($($("#usertable").find("tr").find("th")[3] ).is(":visible"))
			{
				$("#user-language").prop("checked","checked");
			} else {
				$("#user-language").removeAttr("checked");
			}
			
			if ($($("#usertable").find("tr").find("th")[4] ).is(":visible"))
			{
				$("#user-roles").prop("checked","checked");
			} else {
				$("#user-roles").removeAttr("checked");
			}
			
			if ($($("#usertable").find("tr").find("th")[5] ).is(":visible"))
			{
				$("#user-comment").prop("checked","checked");
			} else {
				$("#user-comment").removeAttr("checked");
			}					
			
			$("#configure-dialog").modal('hide');
		}
	</script>
	
	<style>
	   .filtertools {
			float: right;
		}
		
		.RowsPerPage
		{
			margin-top: -44px;
		}
    </style>
		
</head>
<body>

	<%@ include file="../header.jsp" %>
	<%@ include file="../menu.jsp" %>
	<%@ include file="adminmenu.jsp" %>	
	
	<form:form modelAttribute="paging" id="load-users" method="POST" action="${contextpath}/administration/users" class="noautosubmitonclearfilter" style="margin-top: 0px;" onsubmit="$('.tableFloatingHeader').empty();$('.modal-backdrop').hide();$('#show-wait-image').modal('show');">
	
		<div class="fixedtitleform">
			<div class="fixedtitleinner">
				<c:set var="pagingElementName" value="User" />
			</div>
		</div>
		
		<div class="page1200" style="margin-left: auto; margin-right: auto; margin-bottom: 0px; overflow-x: visible;">
		
			<div class="action-bar">
				<div style="float: left;">
					<input rel="tooltip" title="<spring:message code="label.Search" />" class="btn btn-info" type="submit" value="<spring:message code="label.Search" />" />
					<a rel="tooltip" title="<spring:message code="label.ResetFilter" />" onclick="resetSearch()" class="btn btn-default"><spring:message code="label.Reset" /></a>
					<a rel="tooltip" title="<spring:message code="label.Configure" />" onclick="$('#configure-dialog').modal('show')" class="btn btn-default"><i class="icon icon-wrench"></i> <spring:message code="label.Configure" /></a>
				</div>
				<div style="text-align:center">
					<a rel="tooltip" title="<spring:message code="label.AddUser" />" class="btn btn-info" onclick="showAddDialog();"><spring:message code="label.AddUser" /></a>
				</div>
			</div>
					
			<div id="userTableDiv">	
			
				<input type="hidden" name="clearFilter" id="clearFilter" value="false" />
				<input type="hidden" name="sortkey" id="sortkey" value='<esapi:encodeForHTMLAttribute>${filter.sortKey}</esapi:encodeForHTMLAttribute>' />
				<input type="hidden" name="sortorder" id="sortorder" value='<esapi:encodeForHTMLAttribute>${filter.sortOrder}</esapi:encodeForHTMLAttribute>' />
			
				<div>
			
					<table id="usertable" class="table table-bordered table-styled" style="z-index:4000;">
						<thead>
							<tr>
								<th>
									<div style="float: right">
										<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('login',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('login',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
									</div>	 
									<spring:message code="label.Login" />
								</th>
								<th>
									<div style="float: right">
										<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sort('email',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sort('email',false);"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
									</div>	 
									<spring:message code="label.Email" />
								</th>				
								<th class="hideme"><spring:message code="label.OtherEmail" /></th>
								<th style="width: 120px;"><spring:message code="label.Language" /></th>
								<th class="hideme"><spring:message code="label.Roles" /></th>
								<th class="hideme"><spring:message code="label.Comment" /></th>
								<th style="width: 65px;"><spring:message code="label.Actions" /></th>
							</tr>
							<tr class="table-styled-filter">
								<th class="filtercell">
									<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${filter.login}</esapi:encodeForHTMLAttribute>' type="text" maxlength="100" style="margin:0px;" name="login" />
								</th>
								<th class="filtercell">
									<input class="small-form-control" onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${filter.email}</esapi:encodeForHTMLAttribute>' type="text" maxlength="100" style="margin:0px;" name="email" />
								</th>
								<th class="filtercell hideme">&#160;</th>
								<th class="filtercell smallfiltercell">
									<div class="btn-group">
									  <a class="btn btn-default dropdown-toggle" data-toggle="dropdown" >
									    <spring:message code="label.AllValues" />
									    <span style="margin-right: 10px" class="caret"></span>
									  </a>
									  <ul class="dropdown-menu" style="padding: 10px; padding-bottom: 20px;">			
								  		<li style="text-align: right;">
										    <a style="display: inline"  onclick="$('#load-users').submit();" class="btn btn-default btn-xs" rel="tooltip" title="update"><spring:message code="label.OK" /></a>
										</li>	
									  </ul>
									  <ul class="dropdown-menu" style="padding: 10px; margin-top: 42px;">			
									  	<c:forEach items="${languages}" var="language">			
									  		<li>	
												<c:if test="${language.official}">				
													<input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="languages" type="checkbox" class="check" style="width: auto !important;" data-code="<esapi:encodeForHTMLAttribute>${language.code}</esapi:encodeForHTMLAttribute>" value="<esapi:encodeForHTMLAttribute>${language.code}</esapi:encodeForHTMLAttribute>" <c:if test="${filter.containsLanguage(language.code)}">checked="checked"</c:if> /><esapi:encodeForHTML>${language.code} - <spring:message code="label.lang.${language.englishName}" /></esapi:encodeForHTML>
												</c:if>
											</li>
										</c:forEach> 				
									  </ul>
									</div>
								</th>
								<th class="filtercell smallfiltercell hideme">
									<div class="btn-group">
									  <a class="btn btn-default dropdown-toggle" data-toggle="dropdown" >
									    <spring:message code="label.AllValues" />
									    <span style="margin-right: 10px" class="caret"></span>
									  </a>
									  
									  <ul class="dropdown-menu" style="padding: 10px; padding-bottom: 20px;">			
								  		<li style="text-align: right;">
										    <a style="display: inline"  onclick="$('#load-users').submit();" class="btn btn-default btn-xs" rel="tooltip" title="update"><spring:message code="label.OK" /></a>
										</li>	
									  </ul>
									  <ul class="dropdown-menu" style="padding: 10px; margin-top: 42px;">						  
									  	<c:forEach items="${ExistingRoles}" var="role">			
									  		<li>	
												<input onclick="checkFilterCell($(this).closest('.filtercell'), false)" name="roles" type="checkbox" class="check" style="width: auto !important;" value="<esapi:encodeForHTMLAttribute>${role.id}</esapi:encodeForHTMLAttribute>" <c:if test="${filter.containsRole(role.id)}">checked="checked"</c:if> /><esapi:encodeForHTMLAttribute>${role.name}</esapi:encodeForHTMLAttribute>
											</li>
										</c:forEach> 
									  </ul>
									</div>
								</th> 
								<th class="filtercell hideme">
									<input onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${filter.comment}</esapi:encodeForHTMLAttribute>' type="text" class="small-form-control" maxlength="100" style="margin:0px;" name="comment" />
								</th>
								<th>&#160;</th>
							</tr>
						</thead>
						<tbody>
						<c:forEach items="${paging.items}" var="user">
							<tr>
								<td>
									<c:choose>
										<c:when test="${user.type == 'SYSTEM'}">
											<esapi:encodeForHTML>${user.name}</esapi:encodeForHTML>
										</c:when>
										<c:otherwise>
											<esapi:encodeForHTML>${user.name}</esapi:encodeForHTML>&#160(<spring:message code="label.EULogin" />)
										</c:otherwise>
									</c:choose>
								</td>
								<td><esapi:encodeForHTML>${user.email}</esapi:encodeForHTML></td>
								<td class="hideme"><esapi:encodeForHTML>${user.otherEmail}</esapi:encodeForHTML></td>
								<td><esapi:encodeForHTML>${user.language}</esapi:encodeForHTML></td>
								<td class="hideme">
									<c:forEach items="${user.roles}" var="role">
										<esapi:encodeForHTML>${role.name}</esapi:encodeForHTML>				
									</c:forEach>	
								</td>
								<td class="hideme"><esapi:encodeForHTML>${user.comment}</esapi:encodeForHTML></td>
								<td style="min-width: 90px;">
									<a data-toggle="tooltip" title="<spring:message code="label.Edit" />" class="iconbutton" onclick='showEditDialog(<esapi:encodeForHTMLAttribute>${user.id},"${user.name}","${user.email}","${user.otherEmail}","${user.comment}","${user.language}","${user.type}","${user.getRolesAsString()}","${user.getGivenName()}","${user.getSurName()}", ${user.type == 'SYSTEM'}</esapi:encodeForHTMLAttribute>);'><span class="glyphicon glyphicon-pencil"></span></a>
									<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Delete" />" class="iconbutton" onclick='showDeleteDialog(<esapi:encodeForHTMLAttribute>${user.id},"${user.name}"</esapi:encodeForHTMLAttribute>);'><span class="glyphicon glyphicon-remove icon-red"></span></a>
								</td>
							</tr>
						</c:forEach>
						<c:if test="${paging.items == null || paging.items.size() == 0}">
							<tr>
								<td colspan="6"><spring:message code="label.NoUsersToDisplay"/></td>
							</tr>
						</c:if>
			
						</tbody>
					
					</table>
					
					<div id="tbllist-empty" class="noDataPlaceHolder" <c:if test="${paging.items.size() == 0 }">style="display:block;"</c:if>>
						<p>
							<spring:message code="label.NoDataUserText"/>&nbsp;<img src="${contextpath}/resources/images/icons/32/forbidden_grey.png" alt="no data"/>
						<p>
					</div>	
					
					<c:if test="${paging.items != null && paging.items.size() > 0}">
						<%@ include file="../paging.jsp" %>	
					
						<c:if test="${pagingTable ne false}">
							<div class="RowsPerPage">
								<span><spring:message code="label.RowsPerPage" />&#160;</span>
							    <form:select onchange="moveTo('${paging.currentPage}')" path="itemsPerPage" id="itemsPerPage" style="width:70px; margin-top: 0px;" class="middle small-form-control">
									<form:options items="${paging.itemsPerPageOptions}" />
								</form:select>
							</div		
						</c:if>		
					</c:if>
				</div>		
			</div>
			
			<div style="clear: both"></div>
			
			<div style="margin-left: auto; margin-right: auto; width: 300px; text-align: center" class="hideme">
				<input type="submit" class="btn btn-default" value="Search"/>
				<a onclick="resetSearch()" class="btn btn-default"><spring:message code="label.Reset" /></a>	
				<a class="btn btn-default" onclick="showAddDialog();"><spring:message code="label.AddUser" /></a>
			</div>
			
		</div>
		
		<div style="clear: both"></div>
		
	</form:form>
			
	<form:form id="create-user" method="POST" action="${contextpath}/administration/users/createUser" class="hideme">
		<input type="hidden" name="add-login" id="login" value="" />
		<input type="hidden" name="add-password" id="password" value="" />	
		<input type="hidden" name="add-email" id="email" value="" />
		<input type="hidden" name="add-other-email" id="otheremail" value="" />
		<input type="hidden" name="add-firstname" id="firstname" value="" />
		<input type="hidden" name="add-lastname" id="lastname" value="" />
		<input type="hidden" name="add-comment" id="comment" value="" />
		<input type="hidden" name="add-language" id="language" value="" />
		<input type="hidden" name="add-roles" id="roles" value="" />
	</form:form>
	
	<form:form id="update-user" method="POST" action="${contextpath}/administration/users/updateUser" class="hideme">
		<input type="hidden" name="update-id" id="update-id" value="" />
		<input type="hidden" name="update-password" id="update-password" value="" />	
		<input type="hidden" name="update-email" id="update-email" value="" />
		<input type="hidden" name="update-other-email" id="update-otheremail" value="" />
		<input type="hidden" name="update-firstname" id="update-firstname" value="" />
		<input type="hidden" name="update-lastname" id="update-lastname" value="" />
		<input type="hidden" name="update-comment" id="update-comment" value="" />
		<input type="hidden" name="update-language" id="update-language" value="" />
		<input type="hidden" name="update-roles" id="update-roles" value="" />
		<input type="hidden" name="newPage" value="${paging.currentPage}" />
	</form:form>
	
	<form:form id="delete-user" method="POST" action="${contextpath}/administration/users/deleteUser" class="hideme">
		<input type="hidden" name="id" id="delete-id" value="" />
	</form:form>

	<div class="modal" id="add-user-dialog" data-backdrop="static" style="">
		<div class="modal-dialog">
    	<div class="modal-content">
		<div class="modal-header">
			<span id="add-user-dialog-header1"><spring:message code="label.AddUser" /></span>
			<span id="add-user-dialog-header2" class="hideme"><spring:message code="label.EditUserSettings" /></span>
		</div>
		<div class="modal-body">
			<div class="row">
				<div class="col-md-6">
					<label for="add-user-login"><spring:message code="label.Login" /></label><br />
					<input tabindex="1" class="form-control required" type="text" maxlength="255" id="add-user-login" /><br />
					<label for="add-user-password"><spring:message code="label.Password" /></label><br />
					<input tabindex="2" class="form-control required" type="password" maxlength="16" autocomplete="off" id="add-user-password" /><br />
					<label for="add-user-email"><spring:message code="label.Email" /></label><br />
					<input tabindex="3" class="form-control required email" type="text" maxlength="255" id="add-user-email" /><br />
					<label for="old-user-email"><spring:message code="label.OtherEmail" /><span style="color: #999; margin-left: 10px"><spring:message code="info.OtherEmail" /></span></label><br />
					<textarea tabindex="4" class="form-control"  maxlength="255" id="other-user-email"></textarea><br />
					<label for="add-user-comment"><spring:message code="label.Comment" /></label><br />
					<textarea class="form-control" tabindex="5" id="add-user-comment" maxlength="255"></textarea><br />
				</div>
				<div class="col-md-6">
			
					<label for="add-user-firstname"><spring:message code="label.FirstName" /></label><br />
					<input tabindex="6" class="form-control required" type="text" maxlength="255" id="add-user-firstname"/><br />
					<label for="add-user-lastname"><spring:message code="label.LastName" /></label><br />
					<input tabindex="7" class="form-control required" type="text" maxlength="255" id="add-user-lastname" />
					<div class="alert alert-warning" style="margin-top: 10px">
						<label><spring:message code="label.Roles" /></label><br />
						<c:forEach items="${ExistingRoles}" var="role">
							<input tabindex="8" class="role required" type="radio" name="add-user-role" id="add-user-role${role.id}" value="${role.id}" />&nbsp;<esapi:encodeForHTML>${role.name}</esapi:encodeForHTML><br />
						</c:forEach>
					</div>
					<label for="add-user-language"><spring:message code="label.Language" /></label><br />
					<select tabindex="9" class="form-control required" id="add-user-language">
						<c:forEach items="${languages}" var="language">				
							<c:if test="${language.official}">
								<c:choose>
									<c:when test="${language.code.equalsIgnoreCase('EN')}">
										<option selected="selected" value="<esapi:encodeForHTMLAttribute>${language.code}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML><spring:message code="label.lang.${language.englishName}" /></esapi:encodeForHTML></option>
									</c:when>
									<c:otherwise>
										<option value="<esapi:encodeForHTMLAttribute>${language.code}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML><spring:message code="label.lang.${language.englishName}" /></esapi:encodeForHTML></option>
									</c:otherwise>
								</c:choose>
							</c:if>
						</c:forEach>
					</select>
				</div>				
			</div>
		</div>
		<div class="modal-footer">
			<img id="add-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a tabindex="9" id="add-user-button" onclick="createUser();" class="btn btn-info"><spring:message code="label.OK" /></a>	
			<a tabindex="10" onblur="$('#add-user-login').focus()" class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>		
		</div>
		</div>
		</div>
	</div>
	
	<div class="modal" id="delete-user-dialog" data-backdrop="static">
		<div class="modal-dialog">
    	<div class="modal-content">
		<div class="modal-body">
			<spring:message code="question.DeleteUser" />
		</div>
		<div class="modal-footer">
			<img id="delete-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>			
			<a  onclick="deleteUser();" class="btn btn-info" data-dismiss="modal"><spring:message code="label.Yes" /></a>		
		</div>
		</div>
		</div>
	</div>

	<div class="modal" id="configure-dialog" data-backdrop="static">
		<form:form id="configure-attributes-form" method="POST" action="${contextpath}/addressbook/configureAttributes" style="height: auto; margin: 0px; padding: 0px;">			
			<div class="modal-dialog">
    		<div class="modal-content">
			<div class="modal-header">
				<b><spring:message code="label.Configure" /></b>
			</div>
			<div class="modal-body" style="height: 470px; max-height: 470px;">				
				<div class="well">
					<input <c:if test="${usersConfiguration.showName}">checked="checked"</c:if> type="checkbox" class="check" id="user-name" name="user-name" value="true" /><span><spring:message code="label.Login" /></span><br />
					<input <c:if test="${usersConfiguration.showEmail}">checked="checked"</c:if> type="checkbox" class="check" id="user-email" name="user-email" value="true" /><span><spring:message code="label.Email" /></span><br />
					<input <c:if test="${usersConfiguration.showOtherEmail}">checked="checked"</c:if> type="checkbox" class="check" id="user-otheremail" name="user-email" value="true" /><span><spring:message code="label.OtherEmail" /></span><br />
					<input <c:if test="${usersConfiguration.showLanguage}">checked="checked"</c:if> type="checkbox" class="check" id="user-language" name="user-language" value="true" /><span><spring:message code="label.Language" /></span><br />
					<input <c:if test="${usersConfiguration.showRoles}">checked="checked"</c:if> type="checkbox" class="check" id="user-roles" name="user-roles" value="true" /><span><spring:message code="label.Roles" /></span><br />
					<input <c:if test="${usersConfiguration.showComment}">checked="checked"</c:if> type="checkbox" class="check" id="user-comment" name="user-comment" value="true" /><span><spring:message code="label.Comment" /></span>
				</div>				
			</div>
			<div class="modal-footer">
				<a onclick="saveConfiguration();" class="btn btn-info"><spring:message code="label.Save" /></a>		
				<a onclick="cancelConfiguration();"  class="btn btn-default"><spring:message code="label.Cancel" /></a>
			</div>
			</div>
			</div>
		</form:form>
	</div>
	
	<div class="modal" id="userreferenceserrordialog">
		<div class="modal-dialog">
    		<div class="modal-content">
    			<div class="modal-body">
    				<spring:message code="info.userreferenceserror" />
    			</div>
    			<div class="modal-footer">
    				<a onclick="$('#userreferenceserrordialog').modal('hide');" class="btn btn-info"><spring:message code="label.OK" /></a>
    			</div>
    		</div>
    	</div>
	</div>

	<%@ include file="../footer.jsp" %>	

</body>
</html>
