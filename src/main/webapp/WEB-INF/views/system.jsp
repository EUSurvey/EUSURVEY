<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div id="system-message-box" class="alert hideme">
	<div style="float: right; margin-left: 10px;"><a onclick="hideSystemMessage()"><span class="glyphicon glyphicon-remove"></span></a></div>
	<div style="float: left; margin: 5px; margin-top: 5px; margin-right: 10px"><img src="" alt="system message icon" /></div>
	<div style="margin-left: 10px; padding-top: 3px; padding-bottom: 5px;" id="system-message-box-content"></div>
	<div style="text-align: right">
		<button id="btnDeleteUserMessage" class="btn btn-default" onclick="deleteUserMessage()"><spring:message code="label.DeleteMessage" /></button>
	</div>
</div>