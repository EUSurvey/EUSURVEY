<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<div style="margin-top: 10px;">
	<c:choose>
		<c:when test="${oss}">${form.getMessage("footer.fundedOSS")}</c:when>
		<c:otherwise>${form.getMessage("footer.funded")}</c:otherwise>
	</c:choose>
</div>

<div class="layout-footer" style="margin-top: 10px;">
	<div style="width: 1206px; margin-left: auto; margin-right: auto">
		<div class="container-fluid">
			<div class="row">
	        	<div class="col-md-4 ecl-footer__column2" style="padding-left: 15px; padding-top: 8px;">
			        <h4 style="font-weight: bold">EUSurvey</h4>
	            </div>
	            <div class="col-md-4 ecl-footer__column2">
	            	 <ul class="footer-items ecl-footer__menu ecl-list--inline">
			          	<li class="ecl-footer__menu-item">
				           <a style="margin-riht: 10px;" href="<c:url value="/home/documentation"/>">${form.getMessage("label.Documentation")}</a>
						</li>	
				         <li class="ecl-footer__menu-item">
				         	<a target="_blank" href="<c:url value="/home/helpparticipants"/>">${form.getMessage("label.FAQ")}
				         </li>
				      </ul>
	            </div>
	            <div class="col-md-4 ecl-footer__column2">
	                <ul class="footer-items ecl-footer__menu ecl-list--inline">
	                	<li>
							<a class="ecl-footer__menu-item" href="<c:url value="/home/publicsurveys"/>">${form.getMessage("header.AllPublicSurveys")}</a>
						</li>
	                	<li class="ecl-footer__menu-item">
				           <a href="<c:url value="/home/about"/>">${form.getMessage("label.About")}</a>
				        </li>
				        <li class="ecl-footer__menu-item">
				             <a href="http://ec.europa.eu/geninfo/query/search_en.html">${form.getMessage("label.Search")}</a>
				        </li>
				        <li class="ecl-footer__menu-item">
				           <a href="<c:url value="/home/download"/>">${form.getMessage("label.Download")}</a>
						</li>					
	                </ul>
	        	</div>
	    	</div>
	    </div>
	</div>     
 </div>
 
<div class="ecl-footer__site-corporate" style="">
	<div style="width: 1206px; margin-left: auto; margin-right: auto">
		<div class="container-fluid">
	  		<div class="row">
	        	<div class="col-md-4 ecl-footer__column">
			        <h4 class="ecl-h4 ecl-footer__title">European Commission</h4>
	                <ul class="ecl-footer__menu">
		            	<li class="ecl-footer__menu-item">
		        	    	<a class="ecl-link ecl-footer__link" href="https://ec.europa.eu/commission/index_en">Commission and its priorities</a>
		            	</li>
		                <li class="ecl-footer__menu-item">
		              		<a class="ecl-link ecl-footer__link" href="https://ec.europa.eu/info/index_en">Policies information and services</a>
		            	</li>
	                </ul>
	            </div>
	            <div class="col-md-4 ecl-footer__column">
		            <h4 class="ecl-h4 ecl-footer__title">Follow the European Commission</h4>
	                <ul class="ecl-footer__menu ecl-list--inline ecl-footer__social-links" style="margin-left: -8px; padding-right: 80px;">
	    				<li class="ecl-footer__menu-item">
	              			<a class="ecl-link ecl-footer__link" href="https://www.facebook.com/EuropeanCommission"><span class="ecl-icon ecl-icon--facebook ecl-footer__social-icon"></span>Facebook</a>
	            		</li>
	                   	<li class="ecl-footer__menu-item">
	              			<a class="ecl-link ecl-footer__link" href="https://twitter.com/EU_commission"><span class="ecl-icon ecl-icon--twitter ecl-footer__social-icon"></span>Twitter</a>
	            		</li>
	                    <li class="ecl-footer__menu-item">
	              			<a class="ecl-link ecl-footer__link ecl-link--external" href="https://europa.eu/european-union/contact/social-networks_en">Other social media</a>
	            		</li>
	               	</ul>
	            </div>
	            <div class="col-md-4 ecl-footer__column">
	                <h4 class="ecl-h4 ecl-footer__title">European Union</h4>
	                <ul class="ecl-footer__menu">
	                	<li class="ecl-footer__menu-item">
	          				<a class="ecl-link ecl-footer__link ecl-link--external" href="https://europa.eu/european-union/about-eu/institutions-bodies_en">EU institutions</a>
	        			</li>
	                    <li class="ecl-footer__menu-item">
	          				<a class="ecl-link ecl-footer__link ecl-link--external" href="https://europa.eu/european-union/index_en">European Union</a>
	        			</li>
	                </ul>
	        	</div>
	    	</div>
		</div>
	</div>
</div>


<div class="bottom-footer-items">
	<div style="width: 1206px; margin-left: auto; margin-right: auto; text-align: left;">
		<ul style="padding: 0px;">
			<li>
		        <a href="http://ec.europa.eu/info/about-commissions-new-web-presence_en">About the Commission's new web presence</a>
			</li>
			<li>
		        <a href="http://ec.europa.eu/info/resources-partners_en">Resources for partners  </a>
			</li>   
		    <li>
		        <a href="http://ec.europa.eu/cookies/index_en.htm">${form.getMessage("label.Cookies")}</a>
			</li>
			 <li>
		     	<a href="http://ec.europa.eu/geninfo/legal_notices_en.htm">${form.getMessage("label.LegalNotice")}</a>
		    </li>
			<li>
		       <a href="<c:url value="/home/support"/>">${form.getMessage("label.Contact")}</a>
			</li>
			<c:if test="${showprivacy}">
				<li>
					<a href="<c:url value="/home/privacystatement"/>"><spring:message code="label.PrivacyStatement.bis" /></a>
				</li>
			</c:if>
		</ul>
	</div>
 </div>
 
 <script type="text/javascript">
 	$("#versionfootertarget").text(version);
 </script>

<%@ include file="includes2.jsp" %>


