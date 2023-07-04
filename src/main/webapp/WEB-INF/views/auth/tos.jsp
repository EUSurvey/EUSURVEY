<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="esapi"
	uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">

<head>
<title>EUSurvey - Terms of Service</title>
<%@ include file="../includes.jsp"%>
<link href="${contextpath}/resources/css/yellowfocus.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css"></link>

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
		
		function checkAnswer() {
			if ($("#tos_EN").is(":visible")) {
				if ($('#acceptedEN').is(':checked')) { $('#mustacceptEN').hide(); } else { $('#mustacceptEN').show(); return false };
			} else if ($("#tos_DE").is(":visible")) {
				if ($('#acceptedDE').is(':checked')) { $('#mustacceptDE').hide(); } else { $('#mustacceptDE').show(); return false };
			} else if ($("#tos_FR").is(":visible")) {
				if ($('#acceptedFR').is(':checked')) { $('#mustacceptFR').hide(); } else { $('#mustacceptFR').show(); return false };
			}
		}
	</script>
</head>

<body id="tosBody">
	<div class="page-wrap">
		<c:if test="${readonly != null}">
			<%@ include file="../header.jsp"%>
		</c:if>
	
		<c:choose>
			<c:when test="${responsive != null}">
				<div class="page underlined" style="width: auto;">
			</c:when>
			<c:when
				test="${USER != null && runnermode == null && readonly != null }">
				<%@ include file="../menu.jsp"%>
				<div class="page underlined" style="margin-top: 110px">
			</c:when>
			<c:otherwise>
				<div class="page underlined">
			</c:otherwise>
		</c:choose>	
	
		<form:form id="tos-form" action="${contextpath}/auth/tos" method="post" onsubmit="return checkAnswer();">
			<c:if test="${readonly == null}">
				<input type="hidden" name="user" value="${user.id}" />
			</c:if>
	
			<div style="margin-bottom: 20px;">
				<div style="float: right; font-size: 125%">
					[<a href="javascript:;" role="button" aria-label="<spring:message code="label.SwitchToEnglish" />" tabindex="0" id="switchEN">EN</a>] [<a href="javascript:;" role="button" aria-label="<spring:message code="label.SwitchToFrench" />" tabindex="0" id="switchFR">FR</a>] [<a
						href="javascript:;" role="button" aria-label="<spring:message code="label.SwitchToGerman" />" tabindex="0" id="switchDE">DE</a>]
				</div>	
	
				<div id="tos_EN">
					<h1 class="tospage2">EUSurvey - Terms of Service</h1>
	
					<c:choose>
						<c:when test="${oss}">
							<%@ include file="tos_language/tos.oss_en.jsp"%>
						</c:when>
						<c:otherwise>
							<%@ include file="tos_language/tos2_en.jsp"%>
						</c:otherwise>
					</c:choose>
	
					<c:if test="${readonly == null}">
						<br />
						<br />
						<div style="text-align: center">
							<input type="checkbox" class="check" name="accepted"
								id="acceptedEN" value="true" /><span class="mandatory">*</span>
							<label for="acceptedEN">I accept the Terms of Service</label>
							<div class="validation-error hideme" id="mustacceptEN">You
								have to accept the Terms of Service to be able to use EUSurvey</div>
							<br />
							<br /> <input type="submit" class="btn btn-primary"
								value="Submit" id="submitAcceptTosEN" /> <a role="button"
								tabindex="0" onclick="logout()"
								class="btn btn-default" style="margin-left: 50px">Cancel</a>
						</div>
					</c:if>
	
				</div>
	
				<div id="tos_DE" style="display: none;">
					<h1 class="tospage2">Nutzungsbedingungen</h1>
				
					<c:choose>
						<c:when test="${oss}">
							<%@ include file="tos_language/tos.oss_de.jsp"%>
						</c:when>
						<c:otherwise>
							<%@ include file="tos_language/tos2_de.jsp"%>							
						</c:otherwise>
					</c:choose>							
					
					<c:if test="${readonly == null}">
						<br />
						<br />
						<div style="text-align: center">
							<input type="checkbox" class="check" name="accepted"
								id="acceptedDE" value="true" /><span class="mandatory">*</span>
							<label for="acceptedDE">Ich stimme den Nutzungsbedingungen zu</label>
							<div class="validation-error hideme" id="mustacceptDE">Sie
								müssen die Nutzungsbedingungen akzeptieren, um EUSurvey benutzen
								zu können.</div>
							<br />
							<br /> <input type="submit" class="btn btn-primary"
								value="Abschicken" id="submitAcceptTosEN" /> <a role="button"
								tabindex="0" onclick="logout()"
								class="btn btn-default" style="margin-left: 50px">Abbrechen</a>
						</div>
					</c:if>
	
				</div>
	
				<div id="tos_FR" style="display: none;">
					<h1 class="tospage2">Conditions de service</h1>
					<c:choose>
						<c:when test="${oss}">
							<%@ include file="tos_language/tos.oss_fr.jsp"%>						
						</c:when>
						<c:otherwise>
							<%@ include file="tos_language/tos2_fr.jsp"%>
						</c:otherwise>
					</c:choose>
					<c:if test="${readonly == null}">
						<br />
						<br />
						<div style="text-align: center">
							<input type="checkbox" class="check" name="accepted"
								id="acceptedFR" value="true" /><span class="mandatory">*</span>
							<label for="acceptedFR">J'accepte les conditions d'utilisation</label>
							<div class="validation-error hideme" id="mustacceptFR">Pour
								utiliser EUSurvey, vous devez accepter les conditions
								d'utilisation.</div>
							<br />
							<br /> <input type="submit" class="btn btn-primary"
								value="Soumettre" id="submitAcceptTosFR" /> <a role="button"
								tabindex="0" onclick="logout()"
								class="btn btn-default" style="margin-left: 50px">Annuler</a>
						</div>
					</c:if>
				</div>
	
			</div>
	
		</form:form>
	
	</div>
	</div>

	<c:if test="${readonly != null}">
		<%@ include file="../footer.jsp"%>
	</c:if>
	
	<c:if test="${readonly == null}">
		<form:form id="logoutform" action="${contextpath}/j_spring_security_logout" method="post">
	    </form:form>	
	</c:if>
	

</body>

</html>