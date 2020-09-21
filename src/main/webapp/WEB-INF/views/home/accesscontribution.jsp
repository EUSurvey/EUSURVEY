<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>

<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="contribution.edit" /></title>	
	<%@ include file="../includes.jsp" %>
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
				<h1><spring:message code="contribution.edit" /></h1>
			</div>
			
			<form:form id="form" method="POST" action="editcontribution" commandName="form">	
				<div>
					<h5><spring:message code="label.ContributionId" />:</h5>
					<input type="text" class="required uuid" name="uniqueCode" maxlength="36" value="${uniqueid}" />
						<c:if test="${message != null}">
							<div class="validation-error">${message}</div>
						</c:if>
				</div>			
				<div style="clear: both"></div>		
				
				<%@ include file="../captcha.jsp" %>	
					        
			    <a onclick="validateInputAndSubmit($('#form'))" class="btn btn-default"><spring:message code="label.EditMyContribution" /></a>
		
			</form:form>
						
		</div>
	</div>

	<%@ include file="../footer.jsp" %>	
</body>
</html>
