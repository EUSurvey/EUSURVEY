<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.FAQ" /></title>	
	<%@ include file="../includes.jsp" %>	
		
	<style type="text/css">

		<c:choose>
			<c:when test="${USER != null && runnermode == null }">
				.anchor {
					 display: block;
					 height: 110px;
					 margin-top: -110px;
					 visibility: hidden;
				}
			</c:when>
			<c:otherwise>
				.anchor {
					 display: block;
					 height: 40px;
					 margin-top: -40px;
					 visibility: hidden;
				}
			</c:otherwise>
		</c:choose>				
		
		.anchorlink {
			margin-left: 40px;
			text-decoration: underline;
			color: #005580;
		}
		
		.anchorlink a:hover {
			text-decoration: underline;
			color: #005580;
		}
		
		.head {
			margin-left: 20px;
		}
	</style>
	
	<c:if test="${runnermode != null }">
		<script type="text/javascript">
			$(function() {
				 $(".headerlink, .header a").each(function(){
					 if ($(this).attr("href").indexOf("?") == -1)
					$(this).attr("href", $(this).attr("href") + "/runner");
				 });
			});
		</script>
	</c:if>
</head>
<body>

	<%@ include file="../header.jsp" %>

	<c:choose>
		<c:when test="${USER != null && runnermode == null }">
			<%@ include file="../menu.jsp" %>	
			<div class="page" style="margin-top: 110px">
		</c:when>
		<c:otherwise>
			<div class="page" style="margin-top: 40px;">
		</c:otherwise>
	</c:choose>	
	
		<div class="pageheader">
			<h1>EUSurvey - FAQ</h1>
		</div>

		<h2>Contents</h2>
			
		<br/ ><br />
		
			
	</div>

<%@ include file="../footer.jsp" %>	

</body>
</html>
