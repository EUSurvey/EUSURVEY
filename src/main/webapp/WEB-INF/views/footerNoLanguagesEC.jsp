<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="layout-footer" style="margin-top: 10px;">
	
	<c:choose>
		<c:when test="${oss}">${form.getMessage("footer.fundedOSS")}</c:when>
		<c:otherwise>${form.getMessage("footer.funded")}</c:otherwise>
	</c:choose>
	
	<br /><br />

     <ul class="footer-items">
         <li class="modification-date">
             <span>${form.getMessage("label.LastUpdate")}: <span id="versionfootertarget"></span></span>
         </li>
		<li>
           <a class="first" href="<c:url value="/home/about"/>">${form.getMessage("label.About")}</a>
        </li>
        <li>
             <a href="http://ec.europa.eu/geninfo/query/search_en.html">${form.getMessage("label.Search")}</a>
        </li>
        <li>
             <a href="http://ec.europa.eu/cookies/index_en.htm">${form.getMessage("label.Cookies")}</a>
        </li>
         <li>
             <a href="http://ec.europa.eu/geninfo/legal_notices_en.htm">${form.getMessage("label.LegalNotice")}</a>
        </li>
		<li>
           <a href="<c:url value="/home/documentation"/>">${form.getMessage("label.Contact")}</a>
		</li>
		<c:if test="${showprivacy}">
			<li>
			 <a href="<c:url value="/home/privacystatement"/>"><spring:message code="label.PrivacyStatement.bis" /></a>
			</li>
			<li>
				<a href="<c:url value="/home/tos"/>"><spring:message code="label.TermsOfService" /></a>
			</li>
		</c:if>
		<li>
           <a href="<c:url value="/home/download"/>">${form.getMessage("label.Download")}</a>
		</li>
		<li>
           <a style="margin-riht: 10px;" href="<c:url value="/home/documentation"/>">${form.getMessage("label.Documentation")}</a>
		</li>	
         <li>
         	<a target="_blank" href="<c:url value="/home/helpparticipants"/>">${form.getMessage("label.FAQ")}
         </li>
         <li class="top-link">
             <a href="#top-page">Top</a>
         </li>
     </ul>
 </div>
 
 <script type="text/javascript">
 	$("#versionfootertarget").text(version);
 </script>

<%@ include file="includes2.jsp" %>


