<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

	<div style="text-align: center; margin-top: 20px;">
		<c:choose>
			<c:when test="${USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1 || form.survey.owner.id == USER.id}">
				<a id="btnAddUserFromAccess" class="btn btn-default" onclick="showAddUserDialog(false)"><spring:message code="label.AddUser" /></a>
				<c:if test="${USER.getGlobalPrivilegeValue('ECAccess') > 0}">
					<a id="btnAddDptFromAccess" class="btn btn-default" onclick="showAddDepartmentDialog()"><spring:message code="label.AddDepartment" /></a>
				</c:if>
			</c:when>
			<c:otherwise>
				<a class="btn disabled btn-default"><spring:message code="label.AddUser" /></a>
			</c:otherwise>
		</c:choose>
	</div>
	
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
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletGreen" onclick="changePrivilege('ManageInvitations',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
							</c:if>
							<c:if test="${access.getLocalPrivilegeValue('ManageInvitations') == 1}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletYellow" onclick="changePrivilege('ManageInvitations',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read" />
							</c:if>
							<c:if test="${access.getLocalPrivilegeValue('ManageInvitations') == 0}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletRed" onclick="changePrivilege('ManageInvitations',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="none" />
							</c:if>	
						</td>
						<td style="vertical-align: middle; text-align: center">
							<c:if test="${!access.readonly}">
								<a data-toggle="tooltip" title="<spring:message code="label.Remove"/>" class="iconbutton" onclick="showRemoveDialog(${access.id},'${access.user.login}', false);"><span class="glyphicon glyphicon-remove"></span></a>
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
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletGreen" src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
							</c:if>
							<c:if test="${access.getLocalPrivilegeValue('ManageInvitations') == 1}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletYellow" src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read" />
							</c:if>
							<c:if test="${access.getLocalPrivilegeValue('ManageInvitations') == 0}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletRed" src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="none" />
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
	
	<div class="modal" id="add-group-dialog" data-backdrop="static">
		<div class="modal-dialog">
    	<div class="modal-content">
		<div class="modal-header"><spring:message code="label.SelectPrivilegedDepartment" /></div>
		<div class="modal-body">
			
			<div id="add-group-domain-div" style="margin-bottom: 10px;">
				<label for="add-group-type-ecas"><spring:message code="label.Domain" /></label>
				<select id="add-group-type-ecas" onchange="domainChanged()"  style="width: 450px" >
					<c:forEach items="${domains}" var="domain" varStatus="rowCounter">
						<option value="${domain.key}">${domain.value} </option>	
					</c:forEach>
				</select>	
			</div>
			
			<div style="height: 400px; overflow: auto;" id="add-group-tree-div" >
				<label for="add-group-name"><spring:message code="label.SelectAnEntity" />:</label>
				
				<ul id="tree" class="dep-tree" style="-moz-user-select: none;">
					<li>
						<span onclick="disabledEventPropagation(event);" onselectstart="return false;" id="spanRoot">
							<a onclick="openEntities($(this).closest('li').find('ul').first(), true)">
								<img class="folderimage" src="/eusurvey/resources/images/folderclosed.png" />
							</a>
							<input onclick="disabledEventPropagation(event);" type="radio" class="check" name="department" value="ec" style="margin-left: 10px;">
							<spring:message code="label.DGsAndServices" />
							<ul class="dep-tree dep-tree-child" style="display: block;"></ul>
						</span>
					</li>
					<li>
						<span onclick="disabledEventPropagation(event);" onselectstart="return false;" id="spanRoot">
							<a onclick="openEntities($(this).closest('li').find('ul').first(), false)">
								<img class="folderimage" src="/eusurvey/resources/images/folderclosed.png" />
							</a>
							<input onclick="disabledEventPropagation(event);" type="radio" class="check" name="department" value="ec" style="margin-left: 10px;">
							<spring:message code="label.ExecutiveAgencies" />
							<ul class="dep-tree dep-tree-child" style="display: block;"></ul>
						</span>
					</li>
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
	
	<div class="modal" id="ManageInvitations4Externals-dialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
    	<div class="modal-header"><spring:message code="label.Warning" /></div>
		<div class="modal-body">
			<spring:message code="question.ManageInvitations4Externals" />
		</div>
		<div class="modal-footer">
			<a onclick="updatePrivilege2();" class="btn btn-primary"><spring:message code="label.Yes" /></a>
			<a class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>			
		</div>
		</div>
		</div>
	</div>