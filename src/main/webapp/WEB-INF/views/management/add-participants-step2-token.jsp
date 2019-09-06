<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<div class="modal" id="add-participants-dialog2-token" data-backdrop="static">
	<div class="modal-dialog">
    <div class="modal-content">
	<div class="modal-header">
		<c:choose>
			<c:when test="${readonly == null}">
				<spring:message code="label.EditGuestlist" /> - <spring:message code="label.Tokens" /> (2/2)
			</c:when>
			<c:otherwise>
				<spring:message code="label.GuestList" />
			</c:otherwise>
		</c:choose>
	</div>
	<div class="modal-body" style="max-height: 450px; overflow: auto;">	
		
		<form:form id="add-participants-dialog2-token-form" style="height: 400px" method="POST" action="${contextpath}/${sessioninfo.shortname}/management/participants/saveTokens">
			<c:if test="${readonly == null}">
				<div>
					<spring:message code="label.Add" />
					<input type="text" class="small-form-control required number integer min1 max1000" style="width: 50px; margin: 0px; margin-left: 5px;" id="number-new-tokens" name="number-new-tokens" value="10" />
					<spring:message code="label.Tokens" />
					<a id="btnOkAddTokenFromTokenDialog" onclick="addTokens()" class="btn btn-default" style="margin-left: 5px;"><spring:message code="label.OK" /></a>
					<div class="dialog-wait-image hideme"></div>
				</div>
				<div style="margin-top: 10px; margin-bottom: 10px;">
					<spring:message code="label.SearchToken" /><br />
					<input type="text" maxlength="100" class="small-form-control" style="margin-bottom: 0px; width: 272px" id="add-participants-dialog2-search-token" name="add-participants-dialog2-search-token" />
					<a onclick="searchToken(false)" class="btn btn-default"><spring:message code="label.Search" /></a>
					<a onclick="searchToken(true)" class="btn btn-default"><spring:message code="label.Reset" /></a>
				</div>
			</c:if>
			
			<table id="token-table" class="table table-bordered">
				<tbody></tbody>
			</table>	

			<div id="tokentablediv" style="margin-top: 10px;" >
				<c:choose>
					<c:when test="${selectedParticipationGroup != null}">				
						<input type="hidden" name="groupid" id="add-participants-dialog2-token-groupid" value="<esapi:encodeForHTMLAttribute>${selectedParticipationGroup.id}</esapi:encodeForHTMLAttribute>" />
						<input type="hidden" name="groupname" id="add-participants-dialog2-token-groupname" value="<esapi:encodeForHTMLAttribute>${selectedParticipationGroup.name}</esapi:encodeForHTMLAttribute>" />
						
						<div style="text-align: right; margin-right: 10px; margin-bottom: 10px;">				
							<img id="add-participants-dialog2-token-busy" style="margin-right: 250px;" src="${contextpath}/resources/images/ajax-loader.gif" />
							<a id="loadmoretokensbutton" class="btn btn-default" onclick="loadMoreTokens()"><spring:message code="label.more" /></a>
						</div>
					</c:when>
					<c:otherwise>
						<input type="hidden" name="groupname" id="add-participants-dialog2-token-groupname" value="" />
					</c:otherwise>
				</c:choose>
			</div>		
		</form:form>
						
	</div>
	<div class="modal-footer">
		<img id="add-wait-animation2-departments" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
		
		<c:if test="${readonly == null}">	
			<div style="float: left; width: 120px; height: 20px;">
				<a rel="tooltip" title="<spring:message code="label.Back" />"  onclick="step1();" class="btn btn-default"><spring:message code="label.Back" /></a>
			</div>
		</c:if>
		
		<div style="float: right; width: 120px; text-align: right;">
			<a id="btnCancelFromParticipantToken" onclick="cancelEditTokens()" class="btn btn-default"><spring:message code="label.Cancel" /></a>
		</div>
		
		<c:if test="${readonly == null}">	
			<a id="btnSaveFromParticipantToken" onclick="saveTokens();" class="btn btn-info"><spring:message code="label.Save" /></a>
		</c:if>
	</div>
	</div>
	</div>
</div>
	