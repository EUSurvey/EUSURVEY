<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Info" /></title>	
	<%@ include file="includes.jsp" %>
</head>
<body>
	<img src="${contextpath}/files/<esapi:encodeForHTMLAttribute>${file.uid}</esapi:encodeForHTMLAttribute>" width="<esapi:encodeForHTMLAttribute>${file.width}</esapi:encodeForHTMLAttribute>" style="max-width: none;" />
	<div><esapi:encodeForHTML>${file.comment}</esapi:encodeForHTML></div>
</body>
</html>
