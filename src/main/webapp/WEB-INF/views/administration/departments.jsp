<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Departments" /></title>	
	<%@ include file="../includes.jsp" %>		
</head>
<body>

 	<div class="page-wrap">
	<%@ include file="../header.jsp" %>
	<%@ include file="../menu.jsp" %>

	<div class="fullpage">					
		<div style="margin-left: auto; margin-right: auto; width: 850px">
			<h1><spring:message code="label.Import" /></h1>
				
			<div style="margin-top: 50px;">
				<form:form action="${contextpath}/administration/departments?${_csrf.parameterName}=${_csrf.token}" modelAttribute="uploadItem" name="frm" id="import-departments-form" method="post" enctype="multipart/form-data" style="margin: 0px;">
					<h5><spring:message code="label.File" /></h5>
					<form:input path="fileData" id="file" type="file" /><br /><br />
					<input type="submit" value="<spring:message code="label.Import" />" />	
				</form:form>
			</div>		
		</div>		
	</div>
	</div>

<%@ include file="../footer.jsp" %>	

	<c:if test="${messages != null}">
		<script type="text/javascript">
			var messages = new Array();
			<c:forEach items="${messages}" var="message">
				messages.push('${message}');
			</c:forEach>
		
			showGenericMessages(messages);
		</script>
	</c:if>

</body>
</html>
