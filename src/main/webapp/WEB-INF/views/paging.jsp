<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>

<c:if test="${first ne 0}">

	<script type="text/javascript">
		
		$(function() {
			var name = '${pagingElementName}';
			if (name == 'Survey') $("#rows-per-page-div").hide();			
			
			<c:if test="${paging.items.size() == 0}">
				$("#pager").hide();
			</c:if>
		});	
		
		function moveTo(val)
		{
			$("#newPage").val(val);
			$("#newPage").closest('form').submit();
		}
	</script>
	<input type="hidden" name="newPage" id="newPage" value="" />
</c:if>

<div id="pager" style="position: relative; margin-left: auto; margin-right: auto; text-align: center; width:350px; margin-bottom: 10px; margin-top: 10px; background-color: #fff;">
	<c:choose>
		<c:when test="${paging.firstItemOnPage == 1}">
			<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.GoToFirstPage" />" aria-label="<spring:message code="label.GoToFirstPage" />" class="middle iconbutton disabled"><span class="glyphicon glyphicon-step-backward"></span></a>
			<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.GoToPreviousPage" />" aria-label="<spring:message code="label.GoToPreviousPage" />" class="middle iconbutton disabled"><span class="glyphicon glyphicon-chevron-left"></span></a>&#160;
		</c:when>
		<c:otherwise>
			<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.GoToFirstPage" />" aria-label="<spring:message code="label.GoToFirstPage" />" onclick="moveTo('first');" class="middle iconbutton"><span class="glyphicon glyphicon-step-backward"></span></a>
			<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.GoToPreviousPage" />" aria-label="<spring:message code="label.GoToPreviousPage" />" onclick="moveTo('${paging.currentPage-1}');" class="middle iconbutton"><span class="glyphicon glyphicon-chevron-left"></span></a>&#160;
		</c:otherwise>
	</c:choose>	
	<span><esapi:encodeForHTML>${pagingElementName}</esapi:encodeForHTML>&nbsp;<esapi:encodeForHTML>${paging.firstItemOnPage}</esapi:encodeForHTML>&nbsp;<spring:message code="label.To" />&nbsp;<esapi:encodeForHTML>${paging.lastItemOnPage}</esapi:encodeForHTML>&nbsp;</span>
	<c:if test="${paging.numberOfItems != 0 && !paging.hideNumberOfItems}">
		<span><spring:message code="label.of" />&nbsp;<esapi:encodeForHTML>${paging.numberOfItems}</esapi:encodeForHTML></span>		
	</c:if>
	<c:choose>
		<c:when test="${paging.lastItemOnPage == paging.numberOfItems}">
			<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.GoToNextPage" />" aria-label="<spring:message code="label.GoToNextPage" />" class="middle iconbutton disabled"><span class="glyphicon glyphicon-chevron-right"></span></a>
			<c:if test="${paging.enableGoToLastPage}">
				<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.GoToLastPage" />" aria-label="<spring:message code="label.GoToLastPage" />" class="middle iconbutton disabled"><span class="glyphicon glyphicon-step-forward"></span></a>
			</c:if>
		</c:when>
		<c:otherwise>
			<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.GoToNextPage" />" aria-label="<spring:message code="label.GoToNextPage" />" onclick="moveTo('<esapi:encodeForHTMLAttribute>${paging.currentPage+1}</esapi:encodeForHTMLAttribute>');" class="middle iconbutton"><span class="glyphicon glyphicon-chevron-right"></span></a>
			<c:if test="${paging.numberOfItems != 0 && paging.enableGoToLastPage}">
				<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.GoToLastPage" />" aria-label="<spring:message code="label.GoToLastPage" />" onclick="moveTo('last')" class="middle iconbutton"><span class="glyphicon glyphicon-step-forward"></span></a>
			</c:if>
		</c:otherwise>
	</c:choose>
</div>
<c:set var="first">0</c:set>
