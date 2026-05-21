<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script>
    function initChatWidget() {
        new ChatWidget(
        {
            title: 'EUSurvey Help',
            apiUrl: '${chat_api_url}',
            datasources: ['a4_eusurvey'],
            modelId: 'mistral-medium-2508',
            temperature: 0,
            debugMode: false,
            topOffset: 31,
            pageContext: '${requestScope["javax.servlet.forward.servlet_path"]}',
            <c:if test="${USER != null}">
                userId: '${USER.login}',
                userRole: '${USER.roles[0].name}',
                locale: '${USER.language != null ? USER.language : "en"}'
            </c:if>
            <c:if test="${USER == null}">
                locale: 'en'
            </c:if>
        });
    }

    var chatWidgetScript = document.createElement('script');
    chatWidgetScript.src = '${chat_widget_url}';
    chatWidgetScript.onload = initChatWidget;
    document.head.appendChild(chatWidgetScript);
 </script>

<style>
    .chat-widget-sidebar {
        z-index: 100000;
    }
</style>