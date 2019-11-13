<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>

<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="contribution.edit" /></title>	
	<%@ include file="../includes.jsp" %>
	
	<script>
		function checkReportAbuse()
		{
			$('#ReportAbuse-error-noselection').hide();
			$('#ReportAbuse-error-invalidemail').hide();
			
			if (!$("input[name='abuseType']:checked").val()) {
				$('#ReportAbuse-error-noselection').show();
				return false;
		    }
						
			if ($('#abuseEmail').val().length != 0)
			{
				if (!validateEmail($('#abuseEmail').val())) {
					$('#ReportAbuse-error-invalidemail').show();
					return false;
				}
			}
			
			return true;
		}
	</script>
</head>
<body id="bodyContribution">
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>		
		<c:if test="${responsive != null}">
			<style>
				.page {
					width: 100% !important;
					padding: 10px;
				}
			</style>			
		</c:if>
		
		<div class="page">
			<div class="pageheader" style="margin-top: 40px;">
				<h1><spring:message code="label.ReportAbuseLink" /></h1>
			</div>
			
			<form:form action="${contextpath}/home/reportAbuse" method="POST">	
	 			<spring:message code="info.ReportAbuse" arguments="${contextpath}/home/tos" /><br /><br />
				<spring:message code="info.ReportAbuse2" /><br />
				<div style="padding: 10px;">
					<input type="hidden" name="abuseSurvey" value="${AbuseSurvey}" />
				
					<input type="radio" name="abuseType" class="check" value="fake" /><spring:message code="info.ReportAbuseFake" /><br />
					<input type="radio" name="abuseType" class="check" value="propaganda" /><spring:message code="info.ReportAbusePropaganda" /><br />
					<input type="radio" name="abuseType" class="check" value="hate" /><spring:message code="info.ReportAbuseHate" /><br />
					<input type="radio" name="abuseType" class="check" value="images" /><spring:message code="info.ReportAbuseImages" /><br />
					<input type="radio" name="abuseType" class="check" value="promo" /><spring:message code="info.ReportAbusePromo" /><br />
					<input type="radio" name="abuseType" class="check" value="others" /><spring:message code="info.ReportAbuseOthers" /><br />
					
					<div id="ReportAbuse-error-noselection" class="validation-error hideme"> 
						<spring:message code="message.NoElementSelected" />
					</div>
					
					<br />
					<label style="margin-top: 20px"><spring:message code="label.specifyissue" /></label><br />
					<textarea id="abuseText" name="abuseText" maxlength="255">${AbuseText}</textarea>			
	
					<br />
					<label style="margin-top: 20px"><spring:message code="label.youremail" /></label><br />
					
					<div class="input-group">
						<div class="input-group-addon"><span class="glyphicon glyphicon-envelope"></span></div>
						<input class="form-control" id="abuseEmail" name="abuseEmail" type="text" value="${AbuseEmail}" style="max-width: 400px;" />
					</div>				
					
					<div id="ReportAbuse-error-invalidemail" class="validation-error hideme"> 
						<spring:message code="error.InvalidEmail" />
					</div>
					
					<div class="captcha" style="margin-left: -20px; margin-bottom: 20px; margin-top: 40px;">						
						<c:if test="${captchaBypass !=true}">
						<%@ include file="../captcha.jsp" %>
						
							<c:if test='${error != null && error == "CAPTCHA"}'>
						       	<div id="ReportAbuse-error-captcha" class="validation-error">       		
						       		<c:if test="${captchaBypass !=true}">
						       			<spring:message code="message.captchawrongnew" />
						       		</c:if>
						       	</div>
					       	</c:if>			
						</c:if>
			       	</div>
			       	
			       	
				</div>
			
				<button style="margin-left: 30px; margin-bottom: 50px;" type="submit" onclick="return checkReportAbuse()" class="btn btn-info"><spring:message code="label.Report" /></button>
			</form:form>					
		</div>
		
		<c:if test="${AbuseType != null}">
			<script>
				$('input[name=abuseType][value=${AbuseType}]').prop("checked", true);
			</script>
		</c:if>
	</div>

	<%@ include file="../footer.jsp" %>	
</body>
</html>
