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
	<title>EUSurvey - Data Processing Agreement</title>
	<%@ include file="../includes.jsp"%>
	<link href="${contextpath}/resources/css/yellowfocus.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css"></link>

	<style>
		.priv li {
			margin-bottom: 15px;
		}

		.numbered li {
			margin-bottom: 15px;
		}

		.page ul > li {
			list-style-type: disc !important;
		}

		body {
			padding-left: 5px;
			padding-right: 5px;
		}

		.table-format td {
			border: 1px solid;
			text-align: left;
			padding: 8px;
		}

		.subtitle {

		}
	</style>

	<script type="text/javascript">
		
		function checkAnswer() {
			if ($("#dpa_EN").is(":visible")) {
				if ($('#acceptedEN').is(':checked')) { $('#mustacceptEN').hide(); } else { $('#mustacceptEN').show(); return false };
			} else if ($("#dpa_DE").is(":visible")) {
				if ($('#acceptedDE').is(':checked')) { $('#mustacceptDE').hide(); } else { $('#mustacceptDE').show(); return false };
			} else if ($("#dpa_FR").is(":visible")) {
				if ($('#acceptedFR').is(':checked')) { $('#mustacceptFR').hide(); } else { $('#mustacceptFR').show(); return false };
			}
		}
	</script>
</head>

<body id="dpaBody">
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

				<form:form id="dpa-form" action="${contextpath}/auth/dpa" method="post" onsubmit="return checkAnswer();">
					<c:if test="${readonly == null}">
						<input type="hidden" name="user" value="${user.id}" />
					</c:if>

					<div style="margin-bottom: 20px;">
						<div id="dpa_EN">
							<h1 class="dpapage2">EUSurvey - Data Processing Agreement</h1>

							<c:choose>
								<c:when test="${oss}">
									<%@ include file="dpa_language/dpa.oss_en.jsp"%>
								</c:when>
								<c:otherwise>
									<%@ include file="dpa_language/dpa_en.jsp"%>
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
									<br /> <input type="submit" class="btn btn-primary"
												  value="Submit" id="submitAcceptDpaEN" /> <a role="button"
																							  tabindex="0" onclick="logout()"
																							  class="btn btn-default" style="margin-left: 50px">Cancel</a>
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

</body>

</html>