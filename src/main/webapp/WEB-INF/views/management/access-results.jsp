<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

	<div style="text-align: center; margin-top: 20px;">
		<a class="btn btn-default" onclick="showAddUserDialog(true)"><spring:message code="label.AddUser" /></a>
	</div>
	
	<table id="tblResultPrivileges" class="table table-bordered table-striped table-styled" style="margin-left: auto; margin-right: auto; margin-top: 20px; width: 700px;">
	
		<thead>
			<tr style="text-align: center;">
				<th style="vertical-align: middle;"><spring:message code="label.User" /></th>
				<th style="vertical-align: middle;"><spring:message code="label.Email" /></th>
				<th style="vertical-align: middle;"><spring:message code="label.ResultFilter" /></th>
				<c:if test="${!readOnlyResultPrivileges}">
					<th style="vertical-align: middle;"><spring:message code="label.Access" /></th>
				</c:if>	
				<th style="width: 10%"><spring:message code="label.Actions" /></th>
			</tr>
			<tr>
				<th><input placeholder="<spring:message code="label.Filter" />" class="form-control" id="resacc-user-name" /></th>
				<th><input placeholder="<spring:message code="label.Filter" />" class="form-control" id="resacc-user-email" /></th>
				<th></th>
				<c:if test="${!readOnlyResultPrivileges}">
					<th></th>
				</c:if>
				<th></th>
			</tr>
		</thead>
		
		<tbody>
	
		</tbody>
		
	</table>
	
	<div id="results-paging" style="text-align: center; margin-bottom: 10px;">
		<a id="gotoFirst" data-toggle="tooltip" title="<spring:message code="label.GoToFirstPage" />" onclick="firstPage()"><span class="glyphicon glyphicon-step-backward"></span></a>
		<a id="gotoPrevious" data-toggle="tooltip" title="<spring:message code="label.GoToPreviousPage" />" onclick="previousPage()"><span class="glyphicon glyphicon-chevron-left"></span></a>
		
		<span id="results-first"></span>&nbsp;
		<spring:message code="label.to" />&nbsp;
		<span id="results-last"></span>&nbsp;
		
		<a id="gotoNext" data-toggle="tooltip" title="<spring:message code="label.GoToNextPage" />" onclick="nextPage()"><span class="glyphicon glyphicon-chevron-right"></span></a>
	</div>
	
	<div id="tbllist-empty-results" class="noDataPlaceHolder" style="display:block;">
		<p>
			<spring:message code="label.NoDataPrivilegeText"/>&nbsp;<img src="${contextpath}/resources/images/icons/32/forbidden_grey.png" alt="no data"/>
		<p>
	</div>

	<div class="modal" id="edit-filter-dialog" data-backdrop="static">
		<div class="modal-dialog modal-lg">
    	<div class="modal-content">
		<div class="modal-body" style="max-height: calc(100vh - 212px);  overflow-y: auto;">

		    <c:if test="${reportingdatabaseused == null}">
                <div id="ResultFilterLimit" style="font-size:90%; text-align: center; margin-bottom: 10px;">
                    <span class="glyphicon glyphicon-info-sign"></span>
                    <spring:message code="info.ResultFilterLimit" />
                </div>
        	</c:if>

			<form:form id="updateResultFilterForm" method="POST" action="updateResultFilter">
				<input type="hidden" name="accessid" id="accessid" />
				<input type="hidden" name="resultMode" value="true" />
				<input type="hidden" name="page" id="resultpage" value="true" />
		
				<table class="table table-condensed table-bordered">
					<tr>
						<th>
							<b><spring:message code="form.Question" /></b>
						</th>
						<th>
							<b><spring:message code="label.Answer" /></b>
						</th>
					</tr>
					
				<c:forEach items="${form.survey.getElementsForResultAccessFilter()}" var="entry">								
					<tr class="filterrow">
						<td>${entry.getKey().getStrippedTitleAtMost100()}</td>						
						<td>
							<c:choose>
								<c:when test="${!entry.getValue().isEmpty()}">
									<div id="${entry.getKey().uniqueId}">
										<c:forEach items="${entry.getValue()}" var="child">
											<input onclick="checkNumberOfFilters(${reportingdatabaseused == null})" name="${entry.getKey().uniqueId}" type="checkbox" style="margin: 5px; vertical-align: middle; margin-bottom: 7px;" value="${child.id}|${child.uniqueId}" /> ${child.getStrippedTitleAtMost100()} <br />
										</c:forEach>
									</div>
								</c:when>
								<c:otherwise>
									<input onkeyup="checkNumberOfFilters(${reportingdatabaseused == null})" id="${entry.getKey().uniqueId}" name="${entry.getKey().uniqueId}" type="text" class="form-control" style="width: 400px" />
									<div class="help"><spring:message code="info.separateValuesBySemikolon"/></div>							
								</c:otherwise>
							</c:choose>
						</td>					
					</tr>				
				</c:forEach>
				
				</table>
				
			</form:form>
		
			
		</div>
		<div class="modal-footer">
			<a onclick="updateResultFilter();" class="btn btn-primary"><spring:message code="label.OK" /></a>
		  	<a class="btn btn-primary" data-dismiss="modal"><spring:message code="label.Cancel" /></a>			
		</div>
		</div>
		</div>
	</div>
	
	<script>
		<c:choose>
			<c:when test="${readOnlyResultPrivileges}">
				var readOnlyResultPrivileges = true;
			</c:when>
			<c:otherwise>
				var readOnlyResultPrivileges = false;
			</c:otherwise>
		</c:choose>
	</script>