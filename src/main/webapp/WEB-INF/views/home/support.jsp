<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>

<head>
<title>EUSurvey - <spring:message code="label.Support" />
</title>
<%@ include file="../includes.jsp"%>

<c:if test="${runnermode != null }">
	<script type="text/javascript">
			$(function () {
				$(".headerlink, .header a").each(function () {
					if ($(this).attr("href").indexOf("?") == -1)
						$(this).attr("href", $(this).attr("href") + "/runner");
				});
			});
		</script>
</c:if>
</head>

<body id="bodyHelpSupport">

	<%@ include file="../header.jsp"%>

	<c:choose>
		<c:when test="${USER != null && runnermode == null }">
			<%@ include file="../menu.jsp"%>
			<div class="page" style="margin-top: 110px">
		</c:when>
		<c:when test="${responsive != null}">
			<div class="page"
				style="margin-top: 40px; width: 100%; padding: 10px;">
		</c:when>
		<c:otherwise>
			<div class="page" style="margin-top: 40px">
		</c:otherwise>
	</c:choose>

	<div class="pageheader">
		<h1>
			<spring:message code="label.Support" />
		</h1>
	</div>
	<c:choose>
		<c:when test="${oss}">
			<spring:message code="support.oss.text" />
		</c:when>
		<c:otherwise>
			<spring:message code="support.text" />
		</c:otherwise>
	</c:choose>
	</div>

	<%@ include file="../footer.jsp"%>
</body>

</html>