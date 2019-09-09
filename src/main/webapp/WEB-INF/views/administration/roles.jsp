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
	<script type="text/javascript"> 
		var selectedPrivilege = null;
		var selectedId = null;
		
		function changePrivilege(privilege, id)
		{
			<c:if test="${USER.getGlobalPrivilegeValue('RightManagement') > 1}">
			selectedPrivilege = privilege;
			selectedId = id;
			
			if (privilege == 'ECAccess')
			{
				$("#greenArea").hide();
			} else {
				$("#greenArea").show();
			}
			
			if (privilege == 'FormManagement' || privilege == 'UserManagement' || privilege == 'ContactManagement')
			{
				$(".nororw").hide();
				$(".noownall").show();
			} else {
				$(".nororw").show();
				$(".noownall").hide();
			}
			
			$('#role-dialog').modal();
			</c:if>
		}
		
		function updatePrivilege(value)
		{
			$("#wait-animation").show();
			$("#id").val(selectedId);
			$("#privilege").val(selectedPrivilege);
			$("#value").val(value);
			$("#form").submit();
		}
		
		function createRole(name)
		{
			$("#add-role-dialog").find(".validation-error").remove();
			if (name.trim().length == 0)
			{
				$('#add-role-name').after("<div class='validation-error'><spring:message code="validation.required" /></div>");
				return;
			}
			
			$("#add-wait-animation").show();
			$("#name").val(name);
			$("#add-form").submit();
		}
		
		function deleteRole()
		{
			$("#delete-wait-animation").show();
			$("#delete-id").val(selectedId);
			$("#delete-form").submit();
		}
		
		function showDeleteDialog(id, name)
		{
			selectedId = id;
			$('#delete-role-dialog').modal();
		}
		
		$(document).ready(function(){
			$("#administration-menu-tab").addClass("active");
			$("#roles-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
			
			$(".roleBulletGreen").hover(function(){
				$(this).attr("src", "${contextpath}/resources/images/bullet_ball_glass_green_hover.png");
			},
			function(){
				$(this).attr("src", "${contextpath}/resources/images/bullet_ball_glass_green.png");
			});
			
			$(".roleBulletYellow").hover(function(){
				$(this).attr("src", "${contextpath}/resources/images/bullet_ball_glass_yellow_hover.png");
			},
			function(){
				$(this).attr("src", "${contextpath}/resources/images/bullet_ball_glass_yellow.png");
			});
			
			$(".roleBulletRed").hover(function(){
				$(this).attr("src", "${contextpath}/resources/images/bullet_ball_glass_red_hover.png");
			},
			function(){
				$(this).attr("src", "${contextpath}/resources/images/bullet_ball_glass_red.png");
			});
			
			$('[data-toggle="tooltip"]').tooltip();
		})
		
		
	</script>
	
	<style type="text/css">
		.overviewtable td {
			text-align: center;
		}
		
		img {
			max-width: none;
		}
	</style>	
		
</head>
<body>

	<%@ include file="../header.jsp" %>
	<%@ include file="../menu.jsp" %>
	<%@ include file="adminmenu.jsp" %>	
	
	
	<div id="action-bar" class="container action-bar">
		<div class="row">
			<div class="col-md-12" style="text-align:center">
				<c:if test="${USER.getGlobalPrivilegeValue('RightManagement') > 1}">
					<a rel="tooltip" title="<spring:message code="label.AddRole" />" class="btn btn-info" onclick="$('#add-role-dialog').modal();"><spring:message code="label.AddRole" /></a>
				</c:if>
			</div>
		</div>
	</div>
		
	<div class="page1024" style="margin-bottom: 0px;">
				
		<div style="width: 680px; margin-left: auto; margin-right: auto">
				
			<c:if test="${error != null}">
				<span style="margin-left: 200px; color: #f00"><esapi:encodeForHTML>${error}</esapi:encodeForHTML></span>
			</c:if>
			
			<table class="table table-bordered table-styled">				
				<thead>
					<tr>
						<th>&#160;</th>
						<th><spring:message code="label.RightManagement" /></th>
						<th><spring:message code="label.UserManagement" /></th>
						<th><spring:message code="label.FormManagement" /></th>
						<th><spring:message code="label.ContactManagement" /></th>
					
						<c:if test="${showecas != null}">					
							<th><spring:message code="label.ECAccess" /></th>
						</c:if>
					
						<th><spring:message code="label.SystemManagement" /></th>
						<c:if test="${USER.getGlobalPrivilegeValue('RightManagement') > 1}">
							<th>&#160;</th>
						</c:if>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="${roles}" var="role">
					<tr>
						<td class="overview-label" style="text-align: left"><esapi:encodeForHTML>${role.name}</esapi:encodeForHTML></td>
						<td>
							<c:if test="${role.getPrivilegeValue('RightManagement') == 2}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights" />" class="roleBulletGreen" onclick="changePrivilege('RightManagement',${role.id});" src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
							</c:if>
							<c:if test="${role.getPrivilegeValue('RightManagement') == 1}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights" />" class="roleBulletYellow" onclick="changePrivilege('RightManagement',${role.id});" src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read" />
							</c:if>
							<c:if test="${role.getPrivilegeValue('RightManagement') == 0}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights" />" class="roleBulletRed" onclick="changePrivilege('RightManagement',${role.id});" src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="none" />
							</c:if>
						</td>
						<td>
							<c:if test="${role.getPrivilegeValue('UserManagement') == 2}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights" />" class="roleBulletGreen" onclick="changePrivilege('UserManagement',${role.id});" src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
							</c:if>
							<c:if test="${role.getPrivilegeValue('UserManagement') == 1}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights" />" class="roleBulletYellow" onclick="changePrivilege('UserManagement',${role.id});" src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read" />
							</c:if>
							<c:if test="${role.getPrivilegeValue('UserManagement') == 0}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights" />" class="roleBulletRed" onclick="changePrivilege('UserManagement',${role.id});" src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="none" />
							</c:if>	
						</td>
						<td>
							<c:if test="${role.getPrivilegeValue('FormManagement') == 2}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights" />" class="roleBulletGreen" onclick="changePrivilege('FormManagement',${role.id});" src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
							</c:if>
							<c:if test="${role.getPrivilegeValue('FormManagement') == 1}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights" />" class="roleBulletYellow" onclick="changePrivilege('FormManagement',${role.id});" src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read" />
							</c:if>
							<c:if test="${role.getPrivilegeValue('FormManagement') == 0}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights" />" class="roleBulletRed" onclick="changePrivilege('FormManagement',${role.id});" src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="none" />
							</c:if>	
						</td>
						<td>
							<c:if test="${role.getPrivilegeValue('ContactManagement') == 2}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights" />" class="roleBulletGreen" onclick="changePrivilege('ContactManagement',${role.id});" src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
							</c:if>
							<c:if test="${role.getPrivilegeValue('ContactManagement') == 1}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights" />" class="roleBulletYellow" onclick="changePrivilege('ContactManagement',${role.id});" src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read" />
							</c:if>
							<c:if test="${role.getPrivilegeValue('ContactManagement') == 0}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights" />" class="roleBulletRed" onclick="changePrivilege('ContactManagement',${role.id});" src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="none" />
							</c:if>	
						</td>
						<c:if test="${showecas != null}">		
							<td>
								<c:if test="${role.getPrivilegeValue('ECAccess') == 2}">
									<img data-toggle="tooltip" title="<spring:message code="label.EditRights" />" class="roleBulletGreen" onclick="changePrivilege('ECAccess',${role.id});" src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
								</c:if>
								<c:if test="${role.getPrivilegeValue('ECAccess') == 1}">
									<img data-toggle="tooltip" title="<spring:message code="label.EditRights" />" class="roleBulletYellow" onclick="changePrivilege('ECAccess',${role.id});" src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read" />
								</c:if>
								<c:if test="${role.getPrivilegeValue('ECAccess') == 0}">
									<img data-toggle="tooltip" title="<spring:message code="label.EditRights" />" class="roleBulletRed" onclick="changePrivilege('ECAccess',${role.id});" src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="none" />
								</c:if>	
							</td>
						</c:if>
						<td>
							<c:if test="${role.getPrivilegeValue('SystemManagement') == 2}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights" />" class="roleBulletGreen" onclick="changePrivilege('SystemManagement',${role.id});" src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
							</c:if>
							<c:if test="${role.getPrivilegeValue('SystemManagement') == 1}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights" />" class="roleBulletYellow" onclick="changePrivilege('SystemManagement',${role.id});" src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read" />
							</c:if>
							<c:if test="${role.getPrivilegeValue('SystemManagement') == 0}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights" />" class="roleBulletRed" onclick="changePrivilege('SystemManagement',${role.id});" src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="none" />
							</c:if>	
						</td>
						<c:if test="${USER.getGlobalPrivilegeValue('RightManagement') > 1}">
							<td style="vertical-align: middle">
								<a data-toggle="tooltip" title="<spring:message code="label.DeleteRole" />" class="iconbutton" onclick="showDeleteDialog(${role.id},'<esapi:encodeForHTMLAttribute>${role.name}</esapi:encodeForHTMLAttribute>');"><span class="glyphicon glyphicon-remove icon-red"></span></a>
							</td>
						</c:if>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</div>		
	</div>
	
	<form:form id="form" method="POST" action="roles">
		<input type="hidden" name="id" id="id" value="" />
		<input type="hidden" name="privilege" id="privilege" value="" />
		<input type="hidden" name="value" id="value" value="" />	
	</form:form>
	
	<form:form id="add-form" method="POST" action="createRole">
		<input type="hidden" name="name" id="name" value="" />
	</form:form>
	
	<form:form id="delete-form" method="POST" action="deleteRole">
		<input type="hidden" name="id" id="delete-id" value="" />
	</form:form>

	<div class="modal" id="role-dialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
		<div class="modal-body">
			<a onclick="updatePrivilege(0);" class="btn btn-default" style="margin-right: 10px;">
		  		<img src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="read/write" />
		  	</a>
		  	<spring:message code="label.NoAccess" />		  	
		  	<br />
		  
		  	<a onclick="updatePrivilege(1);" class="btn btn-default" style="margin-right: 10px;">
		  		<img src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read/write" />
		  	</a>
		  	<span class="nororw"><spring:message code="label.ReadingAccess" /></span>
		  	<span class="noownall hideme"><spring:message code="label.Own" /></span>
		  	
		  	<br />
		  	<span id="greenArea">
			  	<a onclick="updatePrivilege(2);" class="btn btn-default" style="margin-right: 10px;">
			  		<img src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
			  	</a>
			  	<span class="nororw"><spring:message code="label.ReadWriteAccess" /></span>
			  	<span class="noownall hideme"><spring:message code="label.All" /></span>
		  	</span>
		</div>
		<div class="modal-footer">
			<img id="wait-animation" class="hideme" style="margin-right:120px;" src="${contextpath}/resources/images/ajax-loader.gif" />
		  	<a  class="btn btn-info" data-dismiss="modal"><spring:message code="label.Cancel" /></a>			
		</div>	
		</div>
		</div>
	</div>
	
	<div class="modal" id="add-role-dialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
		<div class="modal-body">
			<label for="add-role-name"><spring:message code="message.NameForNewRole" /></label>
			<input type="text" maxlength="255" id="add-role-name" style="width:220px;" />
		</div>
		<div class="modal-footer">
			<img id="add-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a onclick="createRole($('#add-role-name').val());" class="btn btn-info"><spring:message code="label.OK" /></a>		
			<a class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>			
		</div>
		</div>
		</div>
	</div>
	
	<div class="modal" id="delete-role-dialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
		<div class="modal-body">
			<spring:message code="question.DeleteRole" />
		</div>
		<div class="modal-footer">
			<img id="delete-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>			
			<a  onclick="deleteRole();" class="btn btn-info" data-dismiss="modal"><spring:message code="label.Yes" /></a>		
		</div>
		</div>
		</div>
	</div>

	<%@ include file="../footer.jsp" %>	

</body>
</html>
