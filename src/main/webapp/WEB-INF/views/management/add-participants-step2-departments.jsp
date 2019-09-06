<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<div class="modal" id="add-participants-dialog2-departments" data-backdrop="static">
	<div class="modal-dialog">
    <div class="modal-content">
	<div class="modal-header">
		<c:choose>
			<c:when test="${readonly == null}">
				<spring:message code="label.CreateNewGuestlist" /> - <spring:message code="label.SelectDepartments" /> (2/2)
			</c:when>
			<c:otherwise>
				<spring:message code="label.GuestList" />
			</c:otherwise>
		</c:choose>
	</div>
	<div class="modal-body" style="height: 670px; max-height:none; overflow-y:auto;">	
	
		<div style="float: left; width: 450px" id="add-group-domain-div">
			<label for="add-participants-type-ecas"><spring:message code="label.Domain" /></label>
			<select id="add-participants-type-ecas" <c:if test="${readonly != null}">disabled="disabled"</c:if> onchange="domainChaged()" class="small-form-control" style="width: 450px" >
				<c:forEach items="${domains}" var="domain" varStatus="rowCounter">
					<option value="${domain.key}">${domain.value} </option>	
				</c:forEach>
			</select>	
		</div>
		<div id="tablediv" style="float: left; min-width: 450px">
			<form:form id="saveFormDepartments" method="POST" action="${contextpath}/${form.survey.shortname}/management/participantsDepartments">
				<ul id="tree" class="dep-tree" style="-moz-user-select: none;">

				</ul>	
			</form:form>
		</div>
		
	</div>
	<div class="modal-footer">
		<img id="add-wait-animation2-departments" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
		<c:if test="${readonly == null}">	
			<div style="float: left; width: 120px; height: 20px;">
				<a  onclick="step1();" class="btn btn-default"><spring:message code="label.Back" /></a>
			</div>
		</c:if>
		
		<div style="float: right; width: 120px; text-align: right;">
			<a href="<c:url value="/${form.survey.shortname}/management/participants" />" class="btn btn-default"><spring:message code="label.Cancel" /></a>
		</div>
		
		<c:if test="${readonly == null}">	
			<a id ="btnSaveFromAddParticipantDepartment"  onclick="saveDepartments();" class="btn btn-info"><spring:message code="label.Save" /></a>
		</c:if>			
	</div>
	</div>
	</div>
</div>
	
	