<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page session="true" %>

<div class="hideme">
	<ul id="originalconfiguration">
		<c:forEach items="${attributeNames}" var="attributeName" varStatus="rowCounter">
			<li class="ui-state-default">
				<input class="originalselectedattrib" type="hidden" name="<esapi:encodeForHTMLAttribute>${attributeName.id}</esapi:encodeForHTMLAttribute>" value="<esapi:encodeForHTMLAttribute>${attributeName.id}</esapi:encodeForHTMLAttribute>" />
				<span><a onclick="$(this).parent().parent().remove();"><span class="glyphicon glyphicon-remove"></span></a><esapi:encodeForHTML>${attributeName.name}</esapi:encodeForHTML></span>
				<span></span>								
			</li>
		</c:forEach>					
	</ul>
</div>

<div class="modal" id="configure-attributes-dialog" role="dialog">
	<div class="modal-dialog">
    <div class="modal-content">
	<form:form id="configure-attributes-form" method="POST" action="${contextpath}/addressbook/configureAttributes" style="height: auto; margin: 0px; padding: 0px;">			
		<input type="hidden" name="selectedAttributesOrder" id="selectedAttributesOrder" value="<esapi:encodeForHTMLAttribute>${selectedAttributesOrder}</esapi:encodeForHTMLAttribute>" />
		<input type="hidden" name="selectedAttributesSource" id="selectedAttributesSource" value="attendees" />
		<div class="modal-header">
			<b><spring:message code="message.SelectAttributesForFiltering" /></b>
		</div>
		<div class="modal-body">
			<div style="float: left; margin-left: 10px;  width: 175px;">
				<b><spring:message code="label.AllAttributes" /></b>
			</div>
			<div style="float: left; width: 175px; margin-left: 95px;">
				<b><spring:message code="label.VisibleAttributes" /></b>
			</div>
			<div style="clear: both"></div>

			<div style="float: left;  height: 400px; margin-left: 10px;  width: 220px; overflow:auto;" class="well">
				<c:forEach items="${allAttributeNames}" var="attributeName" varStatus="rowCounter">
					<div>
						<input type="checkbox" class="allattrib check" name="<esapi:encodeForHTMLAttribute>${attributeName.id}</esapi:encodeForHTMLAttribute>" value="true" /><span><esapi:encodeForHTML>${attributeName.name}</esapi:encodeForHTML></span>
					</div>
				</c:forEach>
			</div>
			
			<div style="float: left; margin-left: 10px;">
				<a onclick="moveLeft();" class="btn btn-default btn-sm" style="margin-top: 100px;" ><span class="glyphicon glyphicon-chevron-right"></span></a>
			</div>

			<div id="selectedDiv" style="float: left; height: 400px;  width: 220px; margin-left: 10px;" class="well">
				<input type="checkbox" class="check" checked="checked" disabled="disabled" readonly="readonly" /> <spring:message code="label.Name" /><br />
				<input type="checkbox" class="check" checked="checked" disabled="disabled" readonly="readonly" /> <spring:message code="label.Email" /><br />
				<c:choose>
					<c:when test="${ownerSelected == true}">
						<input id="owner" name="owner" value="selected" checked="checked" type="checkbox" class="check" /> <spring:message code="label.Owner" />
					</c:when>
					<c:otherwise>
						<input id="owner" name="owner" value="selected" type="checkbox" class="check" /> <spring:message code="label.Owner" />
					</c:otherwise>
				</c:choose>
				<br />
				<ul id="sortable" style="height: 360px; overflow: auto;">
					<c:forEach items="${attributeNames}" var="attributeName" varStatus="rowCounter">
						<c:if test='${!attributeName.name.equals("Owner")}'>
							<li class="ui-state-default">
								<input class="selectedattrib" type="hidden" name="<esapi:encodeForHTMLAttribute>${attributeName.id}</esapi:encodeForHTMLAttribute>" value="<esapi:encodeForHTMLAttribute>${attributeName.id}</esapi:encodeForHTMLAttribute>" />
								<span><a onclick="$(this).parent().parent().remove();"><span class="glyphicon glyphicon-remove"></span></a><esapi:encodeForHTML>${attributeName.name}</esapi:encodeForHTML></span>						
							</li>
						</c:if>
					</c:forEach>					
				</ul>
			</div>				
			
			<div style="clear: both"></div>
			
		</div>
		<div class="modal-footer">
			<a onclick="saveConfiguration();" class="btn btn-primary"><spring:message code="label.Save" /></a>		
			<a onclick="cancelConfigure();"  class="btn btn-default"><spring:message code="label.Cancel" /></a>
		</div>	
	</form:form>
	</div>
	</div>
</div>
