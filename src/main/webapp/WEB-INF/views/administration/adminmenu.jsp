<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="formmenu">
	<c:if test="${USER.getGlobalPrivilegeValue('SystemManagement') > 0}">
		<div id="system-button" class="InactiveLinkButton"><a href="<c:url value="/administration/system"/>"><spring:message code="label.System" /></a></div>  
	</c:if>
	<c:if test="${USER.getGlobalPrivilegeValue('UserManagement') > 1}">
		<div id="users-button" class="InactiveLinkButton"><a href="<c:url value="/administration/users"/>"><spring:message code="label.Users" /></a></div>
	</c:if>
	<c:if test="${USER.getGlobalPrivilegeValue('RightManagement') > 0}">
		<div id="roles-button" class="InactiveLinkButton"><a href="<c:url value="/administration/roles"/>"><spring:message code="label.Roles" /></a></div>
	</c:if>
	<c:if test="${enablepublicsurveys && USER.getGlobalPrivilegeValue('FormManagement') > 1}">
		<div id="publicsurveys-button" class="InactiveLinkButton"><a href="<c:url value="/administration/publicsurveys"/>"><spring:message code="label.PublicSurveys" /></a></div>
	</c:if>
	<c:if test="${USER.getGlobalPrivilegeValue('FormManagement') > 1}">
		<div id="surveysearch-button" class="InactiveLinkButton"><a href="<c:url value="/administration/surveysearch"/>"><spring:message code="label.SurveySearch" /></a></div>
		<div id="contributionmanagement-button" class="InactiveLinkButton"><a href="<c:url value="/administration/contributionsearch"/>"><spring:message code="label.Contributions" /></a></div>
	</c:if>
	<c:if test="${USER.getGlobalPrivilegeValue('SystemManagement') > 0}">
		<div id="files-button" class="InactiveLinkButton"><a href="<c:url value="/administration/files"/>"><spring:message code="label.Files" /></a></div>  
	</c:if>
	<c:if test="${USER.getGlobalPrivilegeValue('FormManagement') > 0 && enablereportingdatabase == 'true'}">
		<div id="reporting-button" class="InactiveLinkButton"></span><a href="<c:url value="/administration/reporting"/>"><spring:message code="label.ReportingDatabase" /></a></div>  
	</c:if>
</div>