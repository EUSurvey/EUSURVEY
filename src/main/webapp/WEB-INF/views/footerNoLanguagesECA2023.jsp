<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="bottom-footer-items">
	<div style="width: 1206px; margin-left: auto; margin-right: auto; text-align: auto;">
		<ul style="padding: 0px;">
			<li>
		        <a class="bottom-footer-text">${form.getMessage("eca.address")}</a>
			</li>
		</ul>
	</div>
 </div>

<%@ include file="includes2.jsp" %>


