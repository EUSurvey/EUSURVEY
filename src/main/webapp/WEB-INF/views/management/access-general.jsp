<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<style>
    .dep-tree { list-style-type: none; margin: 0; padding: 0; }
    .dep-tree-child { margin-left: 30px;}

    .dep-tree .check {
        margin-right: 5px !important;
    }

    #add-user-dialog label {
        margin-bottom: 0;
        margin-top: 10px;
    }
</style>

	<div style="text-align: center; margin-top: 20px;">
		<c:choose>
			<c:when test="${USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1 || form.survey.owner.id == USER.id}">
				<a id="btnAddUserFromAccess" class="btn btn-default" onclick="showAddUserDialog(false,false)"><spring:message code="label.AddUser" /></a>
				<c:if test="${USER.getGlobalPrivilegeValue('ECAccess') > 0}">
					<a id="btnAddDptFromAccess" class="btn btn-default" onclick="showAddDepartmentDialog()"><spring:message code="label.AddDepartment" /></a>
				</c:if>
			</c:when>
			<c:otherwise>
				<a class="btn disabled btn-default"><spring:message code="label.AddUser" /></a>
			</c:otherwise>
		</c:choose>
	</div>
	
	<table id="tblPrivilegesFromAccess" class="table table-bordered table-striped table-styled" style="margin-left: auto; margin-right: auto; margin-top: 40px;">
	
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
				<th style="vertical-align: middle; width: 10%; text-align: center"><spring:message code="label.Actions" /></th>
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
						    <div class="icon-wrapper" data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" onclick="changePrivilege('AccessDraft',${access.id});">
						        <jsp:include page="../management/access-icon.jsp">
						            <jsp:param name="value" value="${access.getLocalPrivilegeValue('AccessDraft')}" />
						        </jsp:include>
							 </div>
						</td>
						<td style="text-align: center">
						    <div class="icon-wrapper" data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" onclick="changePrivilege('AccessResults',${access.id});">
                                <jsp:include page="../management/access-icon.jsp">
                                    <jsp:param name="value" value="${access.getLocalPrivilegeValue('AccessResults')}" />
                                </jsp:include>
							</div>
						</td>
						<td style="text-align: center;">
                            <c:choose>
                                <c:when test="${access.user.formPrivilege < 1}">
                                    <div class="icon-wrapper">
                                        <jsp:include page="../management/access-icon.jsp">
                                            <jsp:param name="value" value="-1" />
                                        </jsp:include>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                 <div class="icon-wrapper" data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" onclick="changePrivilege('FormManagement',${access.id});">
                                     <jsp:include page="../management/access-icon.jsp">
                                        <jsp:param name="value" value="${access.getLocalPrivilegeValue('FormManagement')}" />
                                     </jsp:include>
                                 </div>
                                </c:otherwise>
                            </c:choose>
						</td>
						<td style="text-align: center">
						    <div class="icon-wrapper" data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" onclick="changePrivilege('ManageInvitations',${access.id});">
                                <jsp:include page="../management/access-icon.jsp">
                                    <jsp:param name="value" value="${access.getLocalPrivilegeValue('ManageInvitations')}" />
                                </jsp:include>
							</div>
						</td>
						<td style="vertical-align: middle; text-align: center">
							<c:if test="${!access.readonly}">
								<a data-toggle="tooltip" title="<spring:message code="label.Remove"/>" class="iconbutton" onclick="showRemoveDialog(${access.id},'${access.user.login}', false);"><span class="glyphicon glyphicon-remove"></span></a>
							</c:if>	
						</td>
					</c:when>
					<c:otherwise>
						<td style="text-align: center">
							<div class="icon-wrapper" data-toggle="tooltip" title="<spring:message code="label.EditRights"/>">
                                <jsp:include page="../management/access-icon.jsp">
                                    <jsp:param name="value" value="${access.getLocalPrivilegeValue('AccessDraft')}" />
                                </jsp:include>
                             </div>
						</td>
						<td style="text-align: center">
							<div class="icon-wrapper" data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" onclick="changePrivilege('AccessResults',${access.id});">
                                <jsp:include page="../management/access-icon.jsp">
                                    <jsp:param name="value" value="${access.getLocalPrivilegeValue('AccessResults')}" />
                                </jsp:include>
                            </div>
						</td>
						<td style="text-align: center">
							<c:choose>
								<c:when test="${access.user.formPrivilege < 1}">
								     <div class="icon-wrapper">
                                         <jsp:include page="../management/access-icon.jsp">
                                            <jsp:param name="value" value="-1" />
                                         </jsp:include>
                                     </div>
								</c:when>
								<c:otherwise>
								    <div class="icon-wrapper" data-toggle="tooltip" title="<spring:message code="label.EditRights"/>">
                                         <jsp:include page="../management/access-icon.jsp">
                                            <jsp:param name="value" value="${access.getLocalPrivilegeValue('FormManagement')}" />
                                         </jsp:include>
                                     </div>
								</c:otherwise>
							</c:choose>
						</td>
						<td style="text-align: center">
							 <div class="icon-wrapper" data-toggle="tooltip" title="<spring:message code="label.EditRights"/>">
                                <jsp:include page="../management/access-icon.jsp">
                                    <jsp:param name="value" value="${access.getLocalPrivilegeValue('ManageInvitations')}" />
                                 </jsp:include>
                            </div>
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
