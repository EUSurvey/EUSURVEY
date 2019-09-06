<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<li>
	<span onclick="disabledEventPropagation(event);" class="department" onselectstart="return false;" id="span<esapi:encodeForHTMLAttribute>${node.name}</esapi:encodeForHTMLAttribute>">
		<c:choose>
			<c:when test="${selectedParticipationGroup != null && selectedParticipationGroup.departments.contains(node.name)}">
				<input <c:if test="${readonly != null}">disabled="disabled"</c:if> checked="checked" onclick="disabledEventPropagation(event); checkChildren(this);" type="checkbox" class="check" name="node<esapi:encodeForHTMLAttribute>${node.name}</esapi:encodeForHTMLAttribute>" value="<esapi:encodeForHTMLAttribute>${node.name}</esapi:encodeForHTMLAttribute>" /><esapi:encodeForHTML>${node.name}</esapi:encodeForHTML>
			</c:when>
			<c:otherwise>
				<input <c:if test="${readonly != null}">disabled="disabled"</c:if> onclick="disabledEventPropagation(event); checkChildren(this);" type="checkbox" class="check" name="node<esapi:encodeForHTMLAttribute>${node.name}</esapi:encodeForHTMLAttribute>" value="<esapi:encodeForHTMLAttribute>${node.name}</esapi:encodeForHTMLAttribute>" /><esapi:encodeForHTML>${node.name}</esapi:encodeForHTML>
			</c:otherwise>
		</c:choose>		
	</span>
	
	<c:if test="${node.children.size() > 0}">
		<ul>
			<c:forEach items="${node.children}" var="child">				
				<c:set var="node" value="${child}" scope="request"/>
				<jsp:include page="renderTree.jsp"/>				
			</c:forEach>
		</ul>
	</c:if>		
</li>