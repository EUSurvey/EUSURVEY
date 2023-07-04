<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Shares" /></title>
	
	<%@ include file="../includes.jsp" %>
	
	<script type="text/javascript" src="${contextpath}/resources/js/jquery.stickytableheaders.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/configure.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/shares.js?version=<%@include file="../version.txt" %>"></script>	
	
	<script type="text/javascript">
		var usersTooOftenShares = '<spring:message code="error.UsersTooOftenShares" />';
		$(function() {					
			$("#settings-menu-tab").addClass("active");
			$("#shares-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
			
			$('[data-toggle="tooltip"]').tooltip(); 
		});	
	</script>
	
	<style type="text/css">
		.highlighted {
			background-color: #FF9900;
		}
		
		.filtertools {
			float: right;
		}
		
		.department {
			-moz-user-select: none;
			-webkit-user-select: none;
			user-select: none;
		}
		
	    #sortable { list-style-type: none; margin: 0; padding: 0; width: 190px; }
	    #sortable li { margin: 0 0px 0px 0px; padding: 0.4em; padding-left: 1.5em; height: 33px; }
	    #sortable li span:not(.glyphicon) { position: absolute; margin-left: -1.3em; }
	    
	    #participantsstatic td, #participantsstatic th, #participantsstaticheader th {
	    	padding: 1px !important;
	    	text-align: left;
	    	width: 150px;
	    	word-wrap: break-word;
	    }
	    
	    #selectedparticipantsstatic td, #selectedparticipantsstatic th {
	    	padding: 1px !important;
	    	text-align: left;
	    	word-wrap: break-word;
	    }
    </style>

</head>
<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
		<%@ include file="../menu.jsp" %>
		<%@ include file="settingsmenu.jsp" %>	
		
		<div class="fixedtitleform">
			<div class="fixedtitleinner" style="width:880px">
				
				<div id="action-bar" class="container action-bar" style="padding-top: 10px; width: 880px">
					<div class="row">
						<div class="col-md-12" style="text-align:center">
							<a onclick="$('#add-share-name').val(''); $('#add-share-dialog1').modal('show')"  class="btn btn-default"><spring:message code="label.CreateNewShare" /></a>
						</div>
					</div>
				</div>
			</div>
		</div>
	
		<div class="page880" style="padding-bottom: 0px; padding-top: 210px;">
					
			<div style="margin-left: auto; margin-right: auto; width: 630px;">				
			
				<h2 style="margin-top: 0px; line-height: normal;"><spring:message code="label.MyShares" /></h2>
				
				<c:choose>
					<c:when test="${shares == null || shares.size() == 0}">
						<div><spring:message code="message.NoResults" /></div>
					</c:when>
					<c:otherwise>
				
						<table class="table table-striped table-bordered table-styled">
							<thead>
								<tr>
									<th><spring:message code="label.Name" /></th>
									<th><spring:message code="label.Contacts" /></th>
									<th><spring:message code="label.Recipient" /></th>
									<th><spring:message code="label.Mode" /></th>
									<th style="width:100px"><spring:message code="label.Actions" /></th>
								</tr>
							</thead>
							<tbody>					
								<c:forEach items="${shares}" var="share">
									<tr>
										<td><esapi:encodeForHTML>${share.name}</esapi:encodeForHTML></td>
										<td><esapi:encodeForHTML>${share.attendees.size()}</esapi:encodeForHTML></td>
										<td><esapi:encodeForHTML>${share.recipient.name}</esapi:encodeForHTML></td>					
										<td>
											<c:choose>
												<c:when test="${share.readonly}"><spring:message code="label.ReadingAccess" /></c:when>
												<c:otherwise><spring:message code="label.ReadWriteAccess" /></c:otherwise>
											</c:choose>
										</td>		
										<td>
											<c:choose>
												<c:when test="${share.readonly && share.owner != user}">
													<a data-toggle="tooltip" href="<c:url value="/settings/shareEdit/${share.id}" />" class="iconbutton" rel="tooltip" title="<spring:message code="label.Show" />"><i class="glyphicon glyphicon-info-sign"></i></a>
												</c:when>
												<c:otherwise>
													<a data-toggle="tooltip" href="<c:url value="/settings/shareEdit/${share.id}" />" class="iconbutton" rel="tooltip" title="<spring:message code="label.Edit" />"><span class="glyphicon glyphicon-pencil"></span></a>
												</c:otherwise>
											</c:choose>
											<a data-toggle="tooltip" onclick="showDeleteDialog('${share.id}');" class="iconbutton" rel="tooltip" title="<spring:message code="label.Delete" />"><span class="glyphicon glyphicon-remove"></span></a>
										</td>
									</tr>						
								</c:forEach>						
							</tbody>
						</table>		
						
					</c:otherwise>
				</c:choose>
				
				<h2 style="margin-top: 20px"><spring:message code="label.ReceivedShares" /></h2>
				
				<c:choose>
					<c:when test="${passiveShares == null || passiveShares.size() == 0}">
						<div><spring:message code="message.NoResults" /></div>
					</c:when>
					<c:otherwise>
				
						<table class="table table-striped table-bordered table-styled" style="width: auto; min-width: 600px;">
							<thead>
								<tr>
									<th><spring:message code="label.Name" /></th>
									<th><spring:message code="label.Contacts" /></th>
									<th><spring:message code="label.Owner" /></th>
									<th><spring:message code="label.Mode" /></th>
									<th><spring:message code="label.Actions" /></th>
								</tr>
							</thead>
							<tbody>					
								<c:forEach items="${passiveShares}" var="share">
									<tr>
										<td><esapi:encodeForHTML>${share.name}</esapi:encodeForHTML></td>
										<td><esapi:encodeForHTML>${share.attendees.size()}</esapi:encodeForHTML></td>
										<td><esapi:encodeForHTML>${share.owner.name}</esapi:encodeForHTML></td>					
										<td>
											<c:choose>
												<c:when test="${share.readonly}"><spring:message code="label.ReadingAccess" /></c:when>
												<c:otherwise><spring:message code="label.ReadWriteAccess" /></c:otherwise>
											</c:choose>
										</td>		
										<td>
											<c:choose>
												<c:when test="${!share.readonly}">
													<a data-toggle="tooltip" href="<c:url value="/settings/shareEdit/${share.id}" />" class="iconbutton" rel="tooltip" title="<spring:message code="label.Edit" />"><span class="glyphicon glyphicon-pencil"></span></a>
												</c:when>
												<c:otherwise>
													<a data-toggle="tooltip" href="<c:url value="/settings/shareEdit/${share.id}" />" class="iconbutton" rel="tooltip" title="<spring:message code="label.Show" />"><i class="glyphicon glyphicon-info-sign"></i></a>
												</c:otherwise>
											</c:choose>
										</td>
									</tr>						
								</c:forEach>						
							</tbody>
						</table>		
						
					</c:otherwise>
				</c:choose>			
			
			</div>
						
		</div>
		
		<div style="clear: both; margin-bottom: 30px;"></div>
		
		<form:form id="saveFormStatic" method="POST" action="${contextpath}/settings/shares">
			<input type="hidden" name="target" value="createStaticShare" />
			<c:if test="${shareToEdit != null}">
				<input type="hidden" name="shareToEdit" value="${shareToEdit.id}" />
			</c:if>
		</form:form>
		
		<form:form id="load-shares" method="POST" action="${contextpath}/settings/shares">		
			<input type="hidden" name="delete" id="delete" value="" />
		</form:form>
	</div>

	<%@ include file="../footer.jsp" %>	
	<%@ include file="add-share-step1.jsp" %>	
	<%@ include file="add-share-step2-static.jsp" %>	
	<%@ include file="add-share-step3-static.jsp" %>	
	<%@ include file="view-share.jsp" %>	
	<%@ include file="../addressbook/configure.jsp" %>

	<div class="modal" id="delete-share-dialog" data-backdrop="static">
		<div class="modal-dialog">
    	<div class="modal-content">
		<div class="modal-body">
			<spring:message code="question.DeleteShare" />
		</div>
		<div class="modal-footer">
			<img id="delete-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a  onclick="deleteShare();" class="btn btn-primary" data-dismiss="modal"><spring:message code="label.Yes" /></a>
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>						
		</div>
		</div>
		</div>
	</div>
	
	<c:if test="${shareToEdit != null && readonly}">
		<script type="text/javascript">
			$('#view-share-dialog').modal('show');
		</script>
	</c:if>
	
	<c:if test="${shareToEdit != null && !readonly}">
		<script type="text/javascript">
			$('#add-share-dialog1').modal('show');
		</script>
	</c:if>
	
	<c:if test="${message != null}">
		<script type="text/javascript">
			showInfo('${message}');
		</script>
	</c:if>

</body>
</html>
