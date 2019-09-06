<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="formmenu">
	<c:if test="${USER.formPrivilege > 0 }">
		<div id="skins-button" class="InactiveLinkButton"><span class="glyphicon glyphicon-play"></span><a href="<c:url value="/settings/skin"/>"><spring:message code="label.Skins" /></a></div>
	</c:if>
	<c:if test="${USER.getGlobalPrivilegeValue('UserManagement') > 0}">
		<div id="myaccount-button" class="InactiveLinkButton"><span class="glyphicon glyphicon-play"></span><a href="<c:url value="/settings/myAccount"/>"><spring:message code="label.MyAccount" /></a></div>
	</c:if>
	<c:if test="${USER.contactPrivilege > 0 }">
		<div id="shares-button" class="InactiveLinkButton"><span class="glyphicon glyphicon-play"></span><a href="<c:url value="/settings/shares"/>"><spring:message code="label.Shares" /></a></div>
	</c:if>
</div>