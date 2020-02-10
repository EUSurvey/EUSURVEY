<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="esapi"
	uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
<title>EUSurvey - Terms of Service</title>
<%@ include file="../includes.jsp"%>

<style>
.priv li {
	list-style-type: none;
	margin-bottom: 15px;
}

.numbered li {
	list-style-type: decimal;
	margin-bottom: 15px;
}

.page ul>li {
	list-style-type: disc !important;
}

body {
	padding-left: 5px;
	padding-right: 5px;
}

</style>

<script type="text/javascript">
		$(document).ready(function () {
			$("#switchEN").click(function () {
				$("#tos_EN").show();
				$("#tos_FR").hide();
				$("#tos_DE").hide();
			});

			$("#switchFR").click(function () {
				$("#tos_EN").hide();
				$("#tos_FR").show();
				$("#tos_DE").hide();
			});

			$("#switchDE").click(function () {
				$("#tos_EN").hide();
				$("#tos_FR").hide();
				$("#tos_DE").show();
			});

			<c:if test="${language != null && language == 'de'}">
				$("#tos_EN").hide();
				$("#tos_FR").hide();
				$("#tos_DE").show();
    		</c:if>

			<c:if test="${language != null && language == 'fr'}">
				$("#tos_EN").hide();
				$("#tos_FR").show();
				$("#tos_DE").hide();
    		</c:if>
    		
    	});

	</script>
</head>

<body id="tosBody">
	<div class="page-wrap">
		<c:if test="${readonly != null}">
			<%@ include file="../header.jsp"%>
		</c:if>
	
		<c:choose>
			<c:when test="${responsive != null}">
				<div class="page" style="width: auto;">
			</c:when>
			<c:when
				test="${USER != null && runnermode == null && readonly != null }">
				<%@ include file="../menu.jsp"%>
				<div class="page" style="margin-top: 110px">
			</c:when>
			<c:otherwise>
				<div class="page">
			</c:otherwise>
		</c:choose>	
			
		<form:form id="logoutform" action="${contextpath}/j_spring_security_logout" method="post">
	    </form:form>
	
		<form:form id="tos-form" action="${contextpath}/auth/ps" method="post">
			<c:if test="${readonly == null}">
				<input type="hidden" name="user" value="${user.id}" />
			</c:if>
	
			<div style="margin-bottom: 20px;">
				<div style="float: right; font-size: 125%">
					[<a id="switchEN">EN</a>] [<a id="switchFR">FR</a>] [<a
						id="switchDE">DE</a>]
				</div>
		
				<div id="tos_EN">
					
					<h1 class="tospage1">EUSurvey - Privacy Statement</h1>
			
					<c:choose>
						<c:when test="${oss}">
							<%@ include file="tos_language/ps.oss_en.jsp"%>							
						</c:when>
						<c:otherwise>
							<%@ include file="tos_language/tos_en.jsp"%>
						</c:otherwise>
					</c:choose>
					<div class="tospage1" style="text-align: center">
						<c:if test="${readonly == null}">
							<input type="submit" class="btn btn-primary" value="I accept" />
							&nbsp;
							<a class="btn btn-default" onclick="logout()">I do not accept</a>
						</c:if>
					</div>		
	
				</div>
	
				<div id="tos_DE" style="display: none;">
					<h1 class="tospage1">EUSurvey Datenschutzerklärung</h1>
									
					<c:choose>
						<c:when test="${oss}">
							<%@ include file="tos_language/ps.oss_de.jsp"%>
						</c:when>
						<c:otherwise>
							<%@ include file="tos_language/tos_de.jsp"%>
						</c:otherwise>
					</c:choose>		
					<div class="tospage1" style="text-align: center">
						<c:if test="${readonly == null}">
							<input type="submit" class="btn btn-primary" value="Ich stimme zu" />
							&nbsp;
							<a class="btn btn-default" onclick="logout()">Ich stimme nicht zu</a>
						</c:if>
					</div>
				</div>
	
				<div id="tos_FR" style="display: none;">
					<h1 class="tospage1">EUSurvey déclaration de confidentialité</h1>
					<c:choose>
						<c:when test="${oss}">
							<%@ include file="tos_language/tos.oss_fr.jsp"%>							
						</c:when>
						<c:otherwise>
							<%@ include file="tos_language/tos_fr.jsp"%>
						</c:otherwise>
					</c:choose>
					<div class="tospage1" style="text-align: center">
						<c:if test="${readonly == null}">
							<input type="submit" class="btn btn-primary" value="J'accepte" />
							&nbsp;
							<a class="btn btn-default" onclick="logout()">Je n'accepte pas</a>
						</c:if>
					</div>
				</div>
		</form:form>
	
		</div>
	</div>
	<c:if test="${readonly != null}">
		<%@ include file="../footer.jsp"%>
	</c:if>

</body>

</html>