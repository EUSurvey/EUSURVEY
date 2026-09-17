<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%-- Determine user role for the chatbot widget --%>
<c:set var="chatbotUserRole" value="guest" />
<c:if test="${USER != null}">
    <c:choose>
        <c:when test="${USER.getGlobalPrivilegeValue('SystemManagement') > 0}">
            <c:set var="chatbotUserRole" value="admin" />
        </c:when>
        <c:when test="${USER.formPrivilege > 0}">
            <c:set var="chatbotUserRole" value="survey-manager" />
        </c:when>
        <c:otherwise>
            <c:set var="chatbotUserRole" value="contributor" />
        </c:otherwise>
    </c:choose>
</c:if>

<%-- Determine page context from the request path --%>
<c:set var="chatbotPageContext" value="help" />
<c:set var="requestPath" value="${requestScope['javax.servlet.forward.servlet_path']}" />
<c:choose>
    <c:when test="${fn:contains(requestPath, '/management/edit')}">
        <c:set var="chatbotPageContext" value="survey-editor" />
    </c:when>
    <c:when test="${fn:contains(requestPath, '/management/results') || fn:contains(requestPath, '/management/statistics')}">
        <c:set var="chatbotPageContext" value="survey-results" />
    </c:when>
    <c:when test="${fn:contains(requestPath, '/management/overview') || fn:contains(requestPath, '/management/properties') || fn:contains(requestPath, '/management/participants') || fn:contains(requestPath, '/management/privileges')}">
        <c:set var="chatbotPageContext" value="survey-settings" />
    </c:when>
    <c:when test="${fn:contains(requestPath, '/runner') || runnermode == true}">
        <c:set var="chatbotPageContext" value="survey-runner" />
    </c:when>
    <c:when test="${fn:contains(requestPath, '/forms') || fn:contains(requestPath, '/dashboard')}">
        <c:set var="chatbotPageContext" value="dashboard" />
    </c:when>
    <c:when test="${fn:contains(requestPath, '/settings') || fn:contains(requestPath, '/account')}">
        <c:set var="chatbotPageContext" value="user-settings" />
    </c:when>
    <c:when test="${fn:contains(requestPath, '/administration')}">
        <c:set var="chatbotPageContext" value="admin-panel" />
    </c:when>
    <c:when test="${fn:contains(requestPath, '/home/documentation') || fn:contains(requestPath, '/home/support') || fn:contains(requestPath, '/home/about')}">
        <c:set var="chatbotPageContext" value="help" />
    </c:when>
</c:choose>

<%-- Determine locale --%>
<c:set var="chatbotLocale" value="${pageContext.response.locale.language}" />
<c:if test="${USER != null && USER.language != null}">
    <c:set var="chatbotLocale" value="${USER.language}" />
</c:if>

<%-- Load the ec-chatbot web component --%>
<script type="module" src="${chat_widget_url}"></script>

<%-- Determine optional attributes --%>
<c:set var="chatbotUserId" value="" />
<c:if test="${USER != null}">
    <c:set var="chatbotUserId" value="${USER.login}" />
</c:if>
<c:set var="chatbotEntityType" value="" />
<c:set var="chatbotEntityId" value="" />
<c:if test="${sessioninfo != null}">
    <c:set var="chatbotEntityType" value="survey" />
    <c:set var="chatbotEntityId" value="${sessioninfo.shortname}" />
</c:if>

<div id="ec-chatbot-container"></div>
<script type="module">
    const container = document.getElementById('ec-chatbot-container');
    const chatbot = document.createElement('ec-chatbot');
    chatbot.setAttribute('app-id', 'eusurvey');
    chatbot.setAttribute('api-url', '${chat_api_url}');
    chatbot.setAttribute('mode', 'widget');
    chatbot.setAttribute('position', 'bottom-right');
    chatbot.setAttribute('locale', '${chatbotLocale}');
    chatbot.setAttribute('user-role', '${chatbotUserRole}');
    chatbot.setAttribute('page-context', '${chatbotPageContext}');
    <c:if test="${not empty chatbotUserId}">
    chatbot.setAttribute('user-id', '${chatbotUserId}');
    </c:if>
    <c:if test="${not empty chatbotEntityType}">
    chatbot.setAttribute('entity-type', '${chatbotEntityType}');
    chatbot.setAttribute('entity-id', '${chatbotEntityId}');
    </c:if>
    container.appendChild(chatbot);
</script>

<style>
    ec-chatbot {
        z-index: 100000;
    }
</style>
