<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Privileges" /></title>
	
	<%@ include file="../includes.jsp" %>
	
	<link href="${contextpath}/resources/css/management.css" rel="stylesheet" type="text/css" />
	
	<style type="text/css">		    
	    .dep-tree { list-style-type: none; margin: 0; padding: 0; }
	    .dep-tree-child { margin-left: 30px;}

		#search-results td, th {
	    	white-space:nowrap;
		}
		
		#search-results td {
	    	cursor: pointer;
		}
		
		.draghandle.dragged
		{
			border-left: 1px solid #f33;
		}
		
		.draghandle
		{
			position: absolute; 
			z-index:5; 
			width:5px;
			cursor:e-resize;
		}
		
		input[type="text"] {
		    width: 250px;
		}
		
		.dep-tree .check {
			margin-right: 5px !important;
		}
		
	</style>
	<script type="text/javascript" src="${contextpath}/resources/js/access.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" >
		$(document).ready(function(){
			$("tr.readonly").each(function(){
				$(this).find("img").removeAttr("onclick").css("cursor", "default");
			})
			
			$("tr:not(.readonly)").each(function(){
				
				$(this).find(".roleBulletGrey").hover(function(){
					$(this).attr("src", "${contextpath}/resources/images/bullet_ball_glass_gray_hover.png");
				},
				function(){
					$(this).attr("src", "${contextpath}/resources/images/bullet_ball_glass_gray.png");
				});
				
				$(this).find(".roleBulletGreen").hover(function(){
					$(this).attr("src", "${contextpath}/resources/images/bullet_ball_glass_green_hover.png");
				},
				function(){
					$(this).attr("src", "${contextpath}/resources/images/bullet_ball_glass_green.png");
				});
				
				$(this).find(".roleBulletYellow").hover(function(){
					$(this).attr("src", "${contextpath}/resources/images/bullet_ball_glass_yellow_hover.png");
				},
				function(){
					$(this).attr("src", "${contextpath}/resources/images/bullet_ball_glass_yellow.png");
				});
				
				$(this).find(".roleBulletRed").hover(function(){
					$(this).attr("src", "${contextpath}/resources/images/bullet_ball_glass_red_hover.png");
				},
				function(){
					$(this).attr("src", "${contextpath}/resources/images/bullet_ball_glass_red.png");
				});
				
			})
			
			$('[data-toggle="tooltip"]').tooltip(); 
		})
	</script>
</head>
<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
		<%@ include file="../menu.jsp" %>
		<%@ include file="formmenu.jsp" %>	
		
		<form:form style="display: none" id="update-form" method="POST" action="access">
			<input type="hidden" name="id" id="update-form-id" value="" />
			<input type="hidden" name="privilege" id="update-form-privilege" value="" />
			<input type="hidden" name="value" id="update-form-value" value="" />	
		</form:form>
		
		<form:form style="display: none" id="add-form" method="POST" action="access">
			<input type="hidden" name="target" value="addUser" />
			<input type="hidden" name="login" id="add-form-login" value="" />
			<input type="hidden" name="ecas" id="add-form-ecas" value="" />
		</form:form>
		
		<form:form style="display: none" id="add-form-group" method="POST" action="access">
			<input type="hidden" name="target" value="addGroup" />
			<input type="hidden" name="groupname" id="add-form-group-name" value="" />
		</form:form>
		
		<form:form style="display: none" id="remove-form" method="POST" action="access">
			<input type="hidden" name="target" value="removeUser" />
			<input type="hidden" name="id" id="remove-id" value="" />
		</form:form>
		
		<div id="action-bar" class="container action-bar">
			<div class="row">
				<div class="col-md-12" style="text-align: center">
					<c:choose>
						<c:when test="${USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1 || form.survey.owner.id == USER.id}">
							<a  id="btnAddUserFromAccess" class="btn btn-default" onclick="showAddUserDialog()"><spring:message code="label.AddUser" /></a>
							<c:if test="${USER.getGlobalPrivilegeValue('ECAccess') > 0}">
								<a id="btnAddDptFromAccess" class="btn btn-default" onclick="showAddDepartmentDialog()"><spring:message code="label.AddDepartment" /></a>
							</c:if>
						</c:when>
						<c:otherwise>
							<a class="btn disabled btn-default"><spring:message code="label.AddUser" /></a>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>	
	
		<div class="fullpageform" style="padding-top: 0px;">
	
			<table id="tblPrivilegesFromAccess" class="table table-bordered table-striped table-styled" style="margin-left: auto; margin-right: auto; margin-top: 40px; width: 500px;">
			
				<thead>
					<tr style="text-align: center;">
						<th style="vertical-align: middle;"><spring:message code="label.User" />
	                                            <c:if test="${!oss}">/ <spring:message code="label.Department" /></c:if>
	                                        </th>
						<th style="vertical-align: middle;"><spring:message code="label.Type" /></th>
						<th style="vertical-align: middle; width: 20%; text-align: center"><spring:message code="label.AccessFormPreview" /></th>
						<th style="vertical-align: middle; width: 20%; text-align: center"><spring:message code="label.Results" /></th>
						<th style="vertical-align: middle; width: 20%; text-align: center"><spring:message code="label.FormManagement" /></th>
						<th style="vertical-align: middle; width: 20%; text-align: center"><spring:message code="label.ManageInvitations" /></th>
						<th style="width: 10%"><spring:message code="label.Actions" /></th>
					</tr>
				</thead>
				
				<tbody>
			
				<c:forEach items="${accesses}" var="access">
					<c:choose>
						<c:when test="${access.readonly}">
							<tr id="accessrow${access.id}" class="readonly">
						</c:when>
						<c:otherwise>
							<tr id="accessrow${access.id}">
						</c:otherwise>
					</c:choose>			
					
						<td style="vertical-align: middle;">
							<c:choose>
								<c:when test="${access.department != null && access.department.length() > 0}">
									<esapi:encodeForHTML>${access.department}</esapi:encodeForHTML>
								</c:when>
								<c:otherwise>
									<c:if test="${access.user.isExternal() and (!oss)}">
										<span class="externaluser hideme"></span>
									</c:if>
									<esapi:encodeForHTML>${access.user.name} ${access.user.department}</esapi:encodeForHTML>
								</c:otherwise>
							</c:choose>
						</td>
						<td style="text-align: center; vertical-align: middle;">
							<c:choose>
								<c:when test="${access.department != null && access.department.length() > 0}">
									<img data-toggle="tooltip" title="<spring:message code="label.Department"/>" src="${contextpath}/resources/images/group.png" alt="Group" />
								</c:when>
								<c:otherwise>
									<img data-toggle="tooltip" title="<spring:message code="label.User"/>" src="${contextpath}/resources/images/user.png" alt="User" />
								</c:otherwise>
							</c:choose>
						</td>
						<c:choose>
							<c:when test="${USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1 || form.survey.owner.id == USER.id}">
								<td style="text-align: center">
									<c:if test="${access.getLocalPrivilegeValue('AccessDraft') == 2}">
										<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletGreen" onclick="changePrivilege('AccessDraft',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
									</c:if>
									<c:if test="${access.getLocalPrivilegeValue('AccessDraft') == 1}">
										<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletYellow" onclick="changePrivilege('AccessDraft',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read" />
									</c:if>
									<c:if test="${access.getLocalPrivilegeValue('AccessDraft') == 0}">
										<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletRed" onclick="changePrivilege('AccessDraft',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="none" />
									</c:if>
								</td>
								<td style="text-align: center">
									<c:if test="${access.getLocalPrivilegeValue('AccessResults') == 2}">
										<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletGreen" onclick="changePrivilege('AccessResults',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
									</c:if>
									<c:if test="${access.getLocalPrivilegeValue('AccessResults') == 1}">
										<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletYellow" onclick="changePrivilege('AccessResults',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read" />
									</c:if>
									<c:if test="${access.getLocalPrivilegeValue('AccessResults') == 0}">
										<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletRed" onclick="changePrivilege('AccessResults',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="none" />
									</c:if>	
								</td>
								<td style="text-align: center">
									<c:choose>
										<c:when test="${access.user.formPrivilege < 1}">
											<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletGrey" src="${contextpath}/resources/images/bullet_ball_glass_gray.png" alt="none" />
										</c:when>
										<c:when test="${access.getLocalPrivilegeValue('FormManagement') == 2}">
											<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletGreen" onclick="changePrivilege('FormManagement',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
										</c:when>
										<c:when test="${access.getLocalPrivilegeValue('FormManagement') == 1}">
											<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletYellow" onclick="changePrivilege('FormManagement',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read" />
										</c:when>
										<c:when test="${access.getLocalPrivilegeValue('FormManagement') == 0}">
											<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletRed" onclick="changePrivilege('FormManagement',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="none" />
										</c:when>		
									</c:choose>
								</td>
								<td style="text-align: center">
									<c:if test="${access.getLocalPrivilegeValue('ManageInvitations') == 2}">
										<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletGreen" onclick="changePrivilege('ManageInvitations',${access.id});" src="/eusurvey/resources/images/bullet_ball_glass_green.png" alt="read/write" />
									</c:if>
									<c:if test="${access.getLocalPrivilegeValue('ManageInvitations') == 1}">
										<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletYellow" onclick="changePrivilege('ManageInvitations',${access.id});" src="/eusurvey/resources/images/bullet_ball_glass_yellow.png" alt="read" />
									</c:if>
									<c:if test="${access.getLocalPrivilegeValue('ManageInvitations') == 0}">
										<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletRed" onclick="changePrivilege('ManageInvitations',${access.id});" src="/eusurvey/resources/images/bullet_ball_glass_red.png" alt="none" />
									</c:if>	
								</td>
								<td style="vertical-align: middle; text-align: center">
									<c:if test="${!access.readonly}">
										<a data-toggle="tooltip" title="<spring:message code="label.Remove"/>" class="iconbutton" onclick="showRemoveDialog(${access.id},'${access.user.login}');"><span class="glyphicon glyphicon-remove"></span></a>
									</c:if>	
								</td>
							</c:when>
							<c:otherwise>
								<td style="text-align: center">
									<c:if test="${access.getLocalPrivilegeValue('AccessDraft') == 2}">
										<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletGreen" src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
									</c:if>
									<c:if test="${access.getLocalPrivilegeValue('AccessDraft') == 1}">
										<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletYellow" src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read" />
									</c:if>
									<c:if test="${access.getLocalPrivilegeValue('AccessDraft') == 0}">
										<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletRed" src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="none" />
									</c:if>
								</td>
								<td style="text-align: center">
									<c:if test="${access.getLocalPrivilegeValue('AccessResults') == 2}">
										<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletGreen" src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
									</c:if>
									<c:if test="${access.getLocalPrivilegeValue('AccessResults') == 1}">
										<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletYellow" src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read" />
									</c:if>
									<c:if test="${access.getLocalPrivilegeValue('AccessResults') == 0}">
										<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletRed" src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="none" />
									</c:if>	
								</td>
								<td style="text-align: center">
									<c:choose>
										<c:when test="${access.user.formPrivilege < 1}">
											<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletGrey"  src="${contextpath}/resources/images/bullet_ball_glass_gray.png" alt="none" />
										</c:when>
										<c:when test="${access.getLocalPrivilegeValue('FormManagement') == 2}">
											<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletGreen" src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
										</c:when>
										<c:when test="${access.getLocalPrivilegeValue('FormManagement') == 1}">
											<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletYellow" src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read" />
										</c:when>
										<c:when test="${access.getLocalPrivilegeValue('FormManagement') == 0}">
											<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletRed" src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="none" />
										</c:when>		
									</c:choose>
								</td>
								<td style="text-align: center">
									<c:if test="${access.getLocalPrivilegeValue('ManageInvitations') == 2}">
										<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletGreen" src="/eusurvey/resources/images/bullet_ball_glass_green.png" alt="read/write" />
									</c:if>
									<c:if test="${access.getLocalPrivilegeValue('ManageInvitations') == 1}">
										<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletYellow" src="/eusurvey/resources/images/bullet_ball_glass_yellow.png" alt="read" />
									</c:if>
									<c:if test="${access.getLocalPrivilegeValue('ManageInvitations') == 0}">
										<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletRed" src="/eusurvey/resources/images/bullet_ball_glass_red.png" alt="none" />
									</c:if>	
								</td>
								<td style="vertical-align: middle; text-align: center">
									<c:if test="${!access.readonly}">
										<a data-toggle="tooltip" title="<spring:message code="label.Remove"/>" class="iconbutton"><span class="glyphicon glyphicon-remove"></span></a>
									</c:if>
								</td>
							</c:otherwise>
						</c:choose>
					</tr>
				</c:forEach>
				
				</tbody>
			
			</table>
			
				<div id="tbllist-empty" class="noDataPlaceHolder" <c:if test="${accesses.size() == 0}">style="display:block;"</c:if>>
					<p>
						<spring:message code="label.NoDataPrivilegeText"/>&nbsp;<img src="${contextpath}/resources/images/icons/32/forbidden_grey.png" alt="no data"/>
					<p>
				</div>
			
			</div>
		</div>

	<%@ include file="../footer.jsp" %>	

	<div class="modal" id="user-dialog" data-backdrop="static">
		<div class="modal-dialog">
    	<div class="modal-content">
		<div class="modal-body">
			<a onclick="updatePrivilege(0);" id="btnNoAccessPrivilegFromDialog" class="btn btn-default" style="margin-right: 10px;">
		  		<img src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="read/write" />
		  	</a>
		  	<spring:message code="label.NoAccess" /><br />
		  	<span id="yellowArea">
			  	<a onclick="updatePrivilege(1);" id="btnROAccessPrivilegFromDialog" class="btn btn-default" style="margin-right: 10px;">
			  		<img src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read/write" />
			  	</a>
			  	<spring:message code="label.ReadingAccess" /><br />
		  	</span>
		  	<a onclick="updatePrivilege(2);" id="btnRWAccessPrivilegFromDialog" class="btn btn-default" style="margin-right: 10px;">
		  		<img src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
		  	</a>
		  	<spring:message code="label.ReadWriteAccess" />
		</div>
		<div class="modal-footer">
			<img id="wait-animation" class="hideme" style="margin-right:120px;" src="${contextpath}/resources/images/ajax-loader.gif" />
		  	<a  class="btn btn-primary" data-dismiss="modal"><spring:message code="label.Cancel" /></a>			
		</div>
		</div>
		</div>
	</div>

	<div class="modal" id="add-user-dialog" data-backdrop="static">
		<div class="modal-dialog modal-lg">
    	<div class="modal-content">
		<div class="modal-header"><spring:message code="label.SelectPrivilegedUser" /></div>
		<div class="modal-body">
			<c:choose>
				<c:when test="${USER.getGlobalPrivilegeValue('ECAccess') > 0}">
					<div style="width: 450px" id="add-user-domain-div">
						<label for="add-user-type-ecas"><spring:message code="label.Domain" /></label><br />
						<select id="add-user-type-ecas" onchange="checkUserType()" style="width: 450px" >
							<c:forEach items="${domains}" var="domain" varStatus="rowCounter">
								<option value="${domain.key}">${domain.value} </option>
							</c:forEach>
						</select>	
					</div>
					
					<div style="clear: both"></div>
					
					<div style="float: left; width: 250px; margin-right: 10px;" id="add-user-department-div">
						<label for="add-department-name"><spring:message code="label.Department" /></label><br />
						<input type="text" maxlength="255" id="add-department-name" />
					</div>
					
					<div style="float: left; width: 250px;  margin-right: 10px;" id="add-user-firstname-div">
						<label for="add-department-name"><spring:message code="label.FirstName" /></label><br />
						<input type="text" maxlength="255" id="add-first-name" />
					</div>
					
					<div style="float: left; width: 250px;  margin-right: 10px;" id="add-user-name-div">
						<label for="add-user-name"><spring:message code="label.Login" /></label><br />
						<input type="text" maxlength="255" id="add-user-name" />
					</div>
					
					<div style="float: left; width: 250px;  margin-right: 10px;" id="add-user-lastname-div">						
						<label for="add-last-name"><spring:message code="label.LastName" /></label><br />
						<input type="text" maxlength="255" id="add-last-name" />
					</div>			
					
					<div style="float: left; width: 250px;  margin-right: 10px;">
						<label for="add-user-name"><spring:message code="label.EmailAddress" /></label><br />
						<input type="text" maxlength="255" id="add-user-email" />
					</div>
					
				</c:when>
				<c:when test="${USER.type == 'SYSTEM'}">
					<div style="width: 250px" id="add-user-domain-div">
						<label for="add-user-type-ecas"><spring:message code="label.Domain" /></label><br />
						<select id="add-user-type-ecas" onchange="checkUserType()">
							<option value="system" selected="selected"><spring:message code="label.System" /></option>
						</select>
					</div>
										
					<div style="clear: both"></div>
					
					<div style="float: left; width: 250px;  margin-right: 10px;" id="add-user-name-div">
						<label for="add-user-name"><spring:message code="label.Login" /></label><br />
						<input type="text" maxlength="255" id="add-user-name" />
					</div>
					
					<div style="float: left; width: 250px;  margin-right: 10px;">
						<label for="add-user-name"><spring:message code="label.EmailAddress" /></label><br />
						<input type="text" maxlength="255" id="add-user-email" />
					</div>
				</c:when>	
				<c:otherwise>
					<div style="width: 250px" id="add-user-domain-div">
						<label for="add-user-type-ecas"><spring:message code="label.Domain" /></label><br />
						<select id="add-user-type-ecas" onchange="checkUserType()">
							<option value="external" selected="selected"><spring:message code="label.EXT" /></option>
						</select>
					</div>
										
					<div style="clear: both"></div>
					
					<div style="float: left; width: 250px;  margin-right: 10px;" id="add-user-firstname-div">
						<label for="add-department-name"><spring:message code="label.FirstName" /></label><br />
						<input type="text" maxlength="255" id="add-first-name" />
					</div>
					
					<div style="float: left; width: 250px;  margin-right: 10px;" id="add-user-name-div">
						<label for="add-user-name"><spring:message code="label.Login" /></label><br />
						<input type="text" maxlength="255" id="add-user-name" />
					</div>
					
					<div style="float: left; width: 250px;  margin-right: 10px;" id="add-user-lastname-div">						
						<label for="add-last-name"><spring:message code="label.LastName" /></label><br />
						<input type="text" maxlength="255" id="add-last-name" />
					</div>	
					
					<div style="float: left; width: 250px;  margin-right: 10px;">
						<label for="add-user-name"><spring:message code="label.EmailAddress" /></label><br />
						<input type="text" maxlength="255" id="add-user-email" />
					</div>
				</c:otherwise>
			</c:choose>
			
			<div style="clear: both"></div>
			
			<div style="text-align: right">
				<div style="float: left; color: #f00; padding: 15px;" id="search-results-more" class="hideme"><spring:message code="message.SearchLimit100" /></div>
				<a id="btnSearchFromAccess"  onclick="searchUser('login');" class="btn btn-default" style="margin: 10px"><spring:message code="label.Search" /></a>	
			</div>		
			
			<div class="round" style="min-height: 323px; max-height: 323px; overflow: auto; width: 100%; overflow: auto; border: 1px solid #ddd" id="search-results-div">
				<table id="search-results" class="table table-bordered table-hover table-styled" style="max-width: none; margin-bottom: 0px">
					<thead>
						<tr>
							<th onclick="searchUser('login');"><spring:message code="label.Login" /></th>
							<th onclick="searchUser('first');"><spring:message code="label.FirstName" /></th>
							<th onclick="searchUser('last',);"><spring:message code="label.LastName" /></th>
                                                        <th onclick="searchUser('department');" <c:if test="${oss}">class="hideme"</c:if> ><spring:message code="label.Department" /></th>
						</tr>
					</thead>
					<tbody></tbody>
				</table>
				<div id="search-results-none" class="hideme"><spring:message code="message.NoResultSelected" /></div>	
			</div>
			
		</div>
		<div class="modal-footer">
			<img id="add-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a id="btnOkAddUserFromAccess"  onclick="addUser();" class="btn btn-primary"><spring:message code="label.OK" /></a>		
			<a id="btnCancelAdddUserFromAccess"  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>			
		</div>
		</div>
		</div>
	</div>
	
	<div class="modal" id="add-group-dialog" data-backdrop="static">
		<div class="modal-dialog">
    	<div class="modal-content">
		<div class="modal-header"><spring:message code="label.SelectPrivilegedDepartment" /></div>
		<div class="modal-body">
			
			<div id="add-group-domain-div" style="margin-bottom: 10px;">
				<label for="add-group-type-ecas"><spring:message code="label.Domain" /></label>
				<select id="add-group-type-ecas" onchange="domainChaged()"  style="width: 450px" >
					<c:forEach items="${domains}" var="domain" varStatus="rowCounter">
						<option value="${domain.key}">${domain.value} </option>	
					</c:forEach>
				</select>	
			</div>
			
			<div style="height: 400px; overflow: auto;" id="add-group-tree-div" >
				<label for="add-group-name"><spring:message code="label.SelectADepartment" />:</label>
				
				<ul id="tree" class="dep-tree" style="-moz-user-select: none;">
	
				</ul>
			</div>	
						
		</div>
		<div class="modal-footer">
			<img id="add-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a id="btnOkAddDptFromAccess"  onclick="addGroup();" class="btn btn-primary" data-dismiss="modal"><spring:message code="label.OK" /></a>
			<a id="btnCancelAdddDptFromAccess"  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>				
		</div>
		</div>
		</div>
	</div>
	
	<div class="modal" id="remove-user-dialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
		<div class="modal-body">
			<spring:message code="question.DeletePrivileges" />
		</div>
		<div class="modal-footer">
			<img id="remove-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a  id="btnOkDeleteFromAccess" onclick="removeUser();" class="btn btn-primary" data-dismiss="modal"><spring:message code="label.Yes" /></a>
			<a  id="btnCancelDeleteFromAccess" class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>			
		</div>
		</div>
		</div>
	</div>
	
	<div class="modal" id="ManageInvitations4Externals-dialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
    	<div class="modal-header"><spring:message code="label.Warning" /></div>
		<div class="modal-body">
			<spring:message code="question.ManageInvitations4Externals" />
		</div>
		<div class="modal-footer">
			<a  onclick="updatePrivilege2();" class="btn btn-primary"><spring:message code="label.Yes" /></a>
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>			
		</div>
		</div>
		</div>
	</div>
	
	<c:if test="${message != null}">
		<script type="text/javascript">
			showMessage('${message}');
		</script>
	</c:if>
	
	<div class="modal" id="busydialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
		<div class="modal-body" style="padding-left: 30px; text-align: center">		
			<spring:message code="label.PleaseWait" /><br /><br />
			<img id="add-wait-animation" src="${contextpath}/resources/images/ajax-loader.gif" />
		</div>
		</div>
		</div>
	</div>

</body>
</html>
