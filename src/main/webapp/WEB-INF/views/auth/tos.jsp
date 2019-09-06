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

.tospage2 {
	display: none;
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
		
		function nextPage()
		{
			<c:if test="${readonly == null}">
			if ($("#tos_EN").is(":visible")) {
				if ($('#acceptedPSEN').is(':checked')) { $('#mustacceptPSEN').hide(); } else { $('#mustacceptPSEN').show(); return false };
			} else if ($("#tos_DE").is(":visible")) {
				if ($('#acceptedPSDE').is(':checked')) { $('#mustacceptPSDE').hide(); } else { $('#mustacceptPSDE').show(); return false };
			} else if ($("#tos_FR").is(":visible")) {
				if ($('#acceptedPSFR').is(':checked')) { $('#mustacceptPSFR').hide(); } else { $('#mustacceptPSFR').show(); return false };
			}
			</c:if>
			$('.tospage1').hide();$('.tospage2').show();
			$("html, body").animate({ scrollTop: 0 }, "slow");
		}
	</script>
</head>

<body id="tosBody">
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

	<form:form id="tos-form" action="${contextpath}/auth/tos" method="post"
		onsubmit="return checkAnswer();">
		<c:if test="${readonly == null}">
			<input type="hidden" name="user" value="${user.id}" />
		</c:if>

		<div style="margin-bottom: 20px;">
			<div style="float: right; font-size: 125%">
				[<a id="switchEN">EN</a>] [<a id="switchFR">FR</a>] [<a
					id="switchDE">DE</a>]
			</div>


			<div id="tos_EN">
				<c:if test="${readonly == null}">
					<h1 class="tospage1">EUSurvey - Privacy Statement</h1>
					<h1 class="tospage2">EUSurvey - Terms of Service</h1>
				</c:if>

				<c:choose>
					<c:when test="${oss}">
						<%@ include file="tos_language/tos.oss_en.jsp"%>
					</c:when>
					<c:otherwise>
						<div class="tospage1">
							<%@ include file="tos_language/tos_en.jsp"%>
						</div>
						<div class="tospage1" style="text-align: center">
							<c:if test="${readonly == null}">
								<br />
								<br />
								<div style="text-align: center">
									<input type="checkbox" class="check" name="acceptedPSEN"
										id="acceptedPSEN" value="true" /><span class="mandatory">*</span>
									I accept the privacy statement
									<div class="validation-error hideme" id="mustacceptPSEN">You
										have to accept the privacy statement to be able to use EUSurvey</div>
									<br />
									<br />
								</div>						
							</c:if>
							<a class="btn btn-primary" onclick="nextPage()">next page</a>
						</div>
						<div class="tospage2">
							<%@ include file="tos_language/tos2_en.jsp"%>
						</div>
					</c:otherwise>
				</c:choose>

				<c:if test="${readonly == null}">
					<div class="tospage2">
						<br />
						<br />
						<div style="text-align: center">
							<input type="checkbox" class="check" name="accepted"
								id="acceptedEN" value="true" /><span class="mandatory">*</span>
							I accept the Terms of Service
							<div class="validation-error hideme" id="mustacceptEN">You
								have to accept the Terms of Service to be able to use EUSurvey</div>
							<br />
							<br /> <input type="submit" class="btn btn-primary"
								value="Submit" id="submitAcceptTosEN" /> <a
								onclick="logout()"
								class="btn btn-default" style="margin-left: 50px">Cancel</a>
						</div>
					</div>
				</c:if>

			</div>

			<div id="tos_DE" style="display: none;">
				<c:if test="${readonly == null}">
					<h1 class="tospage1">EUSurvey Datenschutzerklärung</h1>
					<h1 class="tospage2">Nutzungsbedingungen</h1>
				</c:if>

				<c:choose>
					<c:when test="${oss}">
						<%@ include file="tos_language/tos.oss_de.jsp"%>
					</c:when>
					<c:otherwise>
						<div class="tospage1">
							<%@ include file="tos_language/tos_de.jsp"%>
						</div>
						<div class="tospage1" style="text-align: center">
							<c:if test="${readonly == null}">
								<br />
								<br />
								<div style="text-align: center">
									<input type="checkbox" class="check" name="acceptedPSDE"
										id="acceptedPSDE" value="true" /><span class="mandatory">*</span>
									Ich stimme der Datenschutzerklärung zu
									<div class="validation-error hideme" id="mustacceptPSDE">Sie
									müssen die Datenschutzerklärung akzeptieren, um EUSurvey benutzen
									zu können.</div>
									<br />
									<br />
								</div>	
							</c:if>
							<a class="btn btn-primary" onclick="nextPage()">nächste Seite</a>
						</div>
						<div class="tospage2">
							<%@ include file="tos_language/tos2_en.jsp"%>
						</div>
					</c:otherwise>
				</c:choose>
				<c:if test="${readonly == null}">
					<div class="tospage2">
						<br />
						<br />
						<div style="text-align: center">
							<input type="checkbox" class="check" name="accepted"
								id="acceptedDE" value="true" /><span class="mandatory">*</span>
							Ich stimme den Nutzungsbedingungen zu
							<div class="validation-error hideme" id="mustacceptDE">Sie
								müssen die Nutzungsbedingungen akzeptieren, um EUSurvey benutzen
								zu können.</div>
							<br />
							<br /> <input type="submit" class="btn btn-primary"
								value="Abschicken" id="submitAcceptTosEN" /> <a
								onclick="logout()"
								class="btn btn-default" style="margin-left: 50px">Abbrechen</a>
						</div>
					</div>
				</c:if>

			</div>

			<div id="tos_FR" style="display: none;">
				<c:if test="${readonly == null}">
					<h1 class="tospage1">EUSurvey déclaration de confidentialité</h1>
					<h1 class="tospage2">Conditions de service</h1>
				</c:if>
				<c:choose>
					<c:when test="${oss}">
						<%@ include file="tos_language/tos.oss_fr.jsp"%>
					</c:when>
					<c:otherwise>
						<div class="tospage1">
							<%@ include file="tos_language/tos_fr.jsp"%>
						</div>
						<div class="tospage1" style="text-align: center">
							<c:if test="${readonly == null}">
								<br />
								<br />
								<div style="text-align: center">
									<input type="checkbox" class="check" name="acceptedPSFR"
										id="acceptedPSFR" value="true" /><span class="mandatory">*</span>
									J'accepte la déclaration de confidentialité
									<div class="validation-error hideme" id="mustacceptPSFR">Pour
									utiliser EUSurvey, vous devez accepter la déclaration de confidentialité.</div>
									<br />
									<br />
								</div>	
							</c:if>
							<a class="btn btn-primary" onclick="nextPage()">page suivante</a>
						</div>
						<div class="tospage2">
							<%@ include file="tos_language/tos2_en.jsp"%>
						</div>
					</c:otherwise>
				</c:choose>
				<c:if test="${readonly == null}">
					<div class="tospage2">
						<br />
						<br />
						<div style="text-align: center">
							<input type="checkbox" class="check" name="accepted"
								id="acceptedFR" value="true" /><span class="mandatory">*</span>
							J'accepte les conditions d'utilisation
							<div class="validation-error hideme" id="mustacceptFR">Pour
								utiliser EUSurvey, vous devez accepter les conditions
								d'utilisation.</div>
							<br />
							<br /> <input type="submit" class="btn btn-primary"
								value="Soumettre" id="submitAcceptTosFR" /> <a
								onclick="logout()"
								class="btn btn-default" style="margin-left: 50px">Annuler</a>
						</div>
					</div>
				</c:if>
			</div>
	</form:form>

	</div>
	<c:if test="${readonly != null}">
		<%@ include file="../footer.jsp"%>
	</c:if>

</body>

</html>