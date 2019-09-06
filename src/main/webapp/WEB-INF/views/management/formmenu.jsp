<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="formmenu" id="divActionsFromMenu">
	<c:if test="${sessioninfo.owner.equals(USER.id) || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 0}">
		<div id="overview-button" class="InactiveLinkButton"><i class="glyphicon glyphicon-play"></i><a href="<c:url value="/${sessioninfo.shortname}/management/overview"/>"><spring:message code="label.Overview" /></a></div>
	</c:if>
	
	<c:if test="${sessioninfo.owner.equals(USER.id) || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1}">
		<div id="preview-button" class="InactiveLinkButton"><i class="glyphicon glyphicon-play"></i><a href="<c:url value="/${sessioninfo.shortname}/management/edit"/>"><spring:message code="label.Editor" /></a></div>	
	</c:if>

	<c:if test="${sessioninfo.owner == USER.id || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1 || USER.getLocalPrivilegeValue('AccessDraft') > 0}">
		<div id="test-button" class="InactiveLinkButton"><i class="glyphicon glyphicon-play"></i><a href="<c:url value="/${sessioninfo.shortname}/management/test"/>"><spring:message code="label.Test" /></a></div>	
	</c:if>
	
	<c:if test="${sessioninfo.owner == USER.id || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('AccessResults') > 0 || USER.getLocalPrivilegeValue('AccessDraft') > 0}">
		<div id="results-button" class="InactiveLinkButton"><i class="glyphicon glyphicon-play"></i><a href="<c:url value="/${sessioninfo.shortname}/management/results"/>"><spring:message code="label.Results" /></a></div>
	</c:if>
	
	<c:if test="${sessioninfo.owner == USER.id || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('ManageInvitations') > 0}">
		<div id="participants-button" class="InactiveLinkButton"><i class="glyphicon glyphicon-play"></i><a href="<c:url value="/${sessioninfo.shortname}/management/participants"/>"><spring:message code="label.Participants" /></a></div>
	</c:if>
	
	<c:if test="${sessioninfo.owner == USER.id || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 0}">
		<div id="access-button" class="InactiveLinkButton"><i class="glyphicon glyphicon-play"></i><a href="<c:url value="/${sessioninfo.shortname}/management/access"/>"><spring:message code="label.Privileges" /></a></div>
		<div id="translations-button" class="InactiveLinkButton"><i class="glyphicon glyphicon-play"></i><a href="<c:url value="/${sessioninfo.shortname}/management/translations"/>"><spring:message code="label.Translations" /></a></div>
		<div id="properties-button" class="InactiveLinkButton"><i class="glyphicon glyphicon-play"></i><a href="<c:url value="/${sessioninfo.shortname}/management/properties"/>"><spring:message code="label.Properties" /></a></div>
		<div id="activity-button" class="InactiveLinkButton"><i class="glyphicon glyphicon-play"></i><a href="<c:url value="/${sessioninfo.shortname}/management/activity"/>"><spring:message code="label.Activity" /></a></div>
	</c:if>
	
	<div id="sessiontimeout" class="InactiveLinkButton" style="display: none">
		${uisessiontimeout}
	</div>
</div>

	 <div class="modal" id="sessiontimeoutdialog" role="dialog">
		<div class="modal-dialog" role="document">
		    <div class="modal-content">
			    <div class="modal-header">
			    	<spring:message code="label.SessionTimeout" />
			    </div>
				<div class="modal-body">	
					<spring:message code="info.SessionTimeout" />
				</div>
				<div class="modal-footer">
					<a onclick="$('#sessiontimeoutdialog').modal('hide')" target="_blank" class="btn btn-info"><spring:message code="label.OK" /></a>
				</div>
			</div>
		</div>
	</div>

<script type="text/javascript">
	var lastloadingtime = new Date();
	var sessiontimeoutminutes = ${uisessiontimeout};
	
	function updateSessionTimeout(){
		var currenttime = new Date();
		var diffMs = (currenttime - lastloadingtime); // milliseconds
		var minutes = Math.floor((diffMs/1000)/60);
		
		if (minutes > sessiontimeoutminutes)
		{
			$("#sessiontimeoutdialog").modal("show");
			return;
		}
		
		$("#sessiontimeout").html((sessiontimeoutminutes - minutes).toString());
		window.setTimeout(updateSessionTimeout, 10000);
	}

</script>