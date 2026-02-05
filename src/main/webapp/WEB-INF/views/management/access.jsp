<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Privileges" /></title>
	
	<%@ include file="../includes.jsp" %>
	
	<link href="${contextpath}/resources/css/management.css" rel="stylesheet" type="text/css" />
	
	<style type="text/css">		    

		#search-results-access td, th {
	    	white-space:nowrap;
		}
		
		#search-results-access td {
	    	cursor: pointer;
		}
		
		.draghandle.dragged
		{
			border-left: 1px solid #f33;
		}
		
		.draghandle
		{
			position: absolute; 
			z-index:5; 
			width:5px;
			cursor:e-resize;
		}
		
		input[type="text"] {
		    width: 250px;
		}

		#tblResultPrivileges select {
			background-color: #fff;
		}
		
		a.disabled {
			color: #ccc;
		}

		#tblPrivilegesFromAccess {
		    width: 700px;
		}
		
	</style>
	<script type="text/javascript" src="${contextpath}/resources/js/access.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" >
	
		var surveyUID = "${form.survey.uniqueId}";
		var noFilterMessage = '<spring:message code="label.NoFilter" />';
		var labelreadonly = '<spring:message code="label.ReadingAccess" />';
		var labelreadwrite = '<spring:message code="label.ReadWriteAccess" />';
		var errorOperationFailed = '<spring:message code="error.OperationFailed" />';
		var noMailsFound = '<spring:message code="label.NoMailsFound" />';
		var atLeastOneMail = '<spring:message code="label.AtLeastOneMail" />';
		var noEmptySearch = '<spring:message code="label.NoEmptySearch" />';
		var invalidEmails = '<spring:message code="label.InvalidEmails" />';
		var notFoundEmails = '<spring:message code="label.NotFoundEmails" />';
		var page = ${resultspage};
		var rows = 20;
		
		$(document).ready(function(){
			$("tr.readonly").each(function(){
				$(this).find("img").removeAttr("onclick").css("cursor", "default");
			})
			
			$("tr:not(.readonly)").each(function(){
				
				$(this).find(".roleBulletGrey").hover(function(){
					$(this).attr("src", "${contextpath}/resources/images/bullet_ball_glass_gray_hover.png");
				},
				function(){
					$(this).attr("src", "${contextpath}/resources/images/bullet_ball_glass_gray.png");
				});
				
				$(this).find(".roleBulletGreen").hover(function(){
					$(this).attr("src", "${contextpath}/resources/images/bullet_ball_glass_green_hover.png");
				},
				function(){
					$(this).attr("src", "${contextpath}/resources/images/bullet_ball_glass_green.png");
				});
				
				$(this).find(".roleBulletYellow").hover(function(){
					$(this).attr("src", "${contextpath}/resources/images/bullet_ball_glass_yellow_hover.png");
				},
				function(){
					$(this).attr("src", "${contextpath}/resources/images/bullet_ball_glass_yellow.png");
				});
				
				$(this).find(".roleBulletRed").hover(function(){
					$(this).attr("src", "${contextpath}/resources/images/bullet_ball_glass_red_hover.png");
				},
				function(){
					$(this).attr("src", "${contextpath}/resources/images/bullet_ball_glass_red.png");
				});
				
			})
			
			$('[data-toggle="tooltip"]').tooltip(); 
			
			<c:if test="${selectSecondTab != null}">
				$('#resultsTab').tab('show');
			</c:if>

		})

		const reportingdatabaseused = ${reportingdatabaseused != null};

	</script>
</head>
<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
		<%@ include file="../menu.jsp" %>
		<%@ include file="formmenu.jsp" %>	
		
		<form:form style="display: none" id="update-form" method="POST" action="access">
			<input type="hidden" name="id" id="update-form-id" value="" />
			<input type="hidden" name="privilege" id="update-form-privilege" value="" />
			<input type="hidden" name="value" id="update-form-value" value="" />	
		</form:form>

		<form:form style="display: none" id="add-form" method="POST" action="access">
			<input type="hidden" name="target" value="addUser" />
			<input type="hidden" name="login" id="add-form-login" value="" />
			<input type="hidden" name="ecas" id="add-form-ecas" value="" />
			<input type="hidden" name="givefullaccess" id="add-form-givefullaccess" value="false" />
			<input type="hidden" name="resultMode" id="add-resultMode" value="false" />
		</form:form>
		
		<form:form style="display: none" id="add-form-email" method="POST" action="access">
			<input type="hidden" name="target" value="addUserEmail" />
			<input type="hidden" name="emails" id="add-form-emails" value="" />
			<input type="hidden" name="emailgivefullaccess" id="add-form-emailgivefullaccess" value="false" />
			<input type="hidden" name="resultMode" id="add-resultMode-Email" value="false" />
		</form:form>
		
		<form:form style="display: none" id="add-form-group" method="POST" action="access">
			<input type="hidden" name="target" value="addGroup" />
			<input type="hidden" name="groupgivefullaccess" id="add-form-groupgivefullaccess" value="false" />
			<input type="hidden" name="groupname" id="add-form-group-name" value="" />
		</form:form>
		
		<form:form style="display: none" id="remove-form" method="POST" action="access">
			<input type="hidden" name="target" value="removeUser" />
			<input type="hidden" name="id" id="remove-id" value="" />
			<input type="hidden" name="resultMode" id="remove-resultMode" value="false" />
		</form:form>
		
		<c:if test="${showFirstTab && showSecondTab }">
			<div id="action-bar" class="container action-bar">
				<div class="row">
					<div class="col-md-12" style="text-align: center;">
					 	<ul class="nav nav-tabs white" role="tablist">
						    <li role="presentation" class="active"><a href="#global" aria-controls="global" role="tab" data-toggle="tab"><spring:message code="label.StandardPrivileges" /></a></li>
						    <li role="presentation"><a href="#results" id="resultsTab" aria-controls="results" role="tab" data-toggle="tab"><spring:message code="label.DedicatedResultPrivileges" /></a></li>
						</ul>
					</div>
				</div>
			</div>
		</c:if>
	
		<div class="fullpageform" style="padding-top: 0px;">
		
			<c:choose>
				<c:when test="${showFirstTab && showSecondTab }">
					<div class="tab-content" style="padding-top: 0px; border: 1px solid #ddd; margin-top: -6px; max-width: 1139px; margin-left: auto; margin-right: auto;">
						<div role="tabpanel" class="tab-pane active" id="global">
							<%@ include file="access-general.jsp" %>
						</div>
				
						<div role="tabpanel" class="tab-pane" id="results">
							<%@ include file="access-results.jsp" %>
						</div>
					</div>
				</c:when>
				<c:when test="${showFirstTab}">
					<div style="padding-top: 130px;">
						<%@ include file="access-general.jsp" %>
					</div>
				</c:when>
				<c:when test="${showSecondTab}">
					<div style="display: none">
						<%@ include file="access-general.jsp" %>
					</div>
					<div style="padding-top: 130px;">
						<%@ include file="access-results.jsp" %>
					</div>
				</c:when>
			</c:choose>
			
		</div>
		
	</div>

	<%@ include file="../footer.jsp" %>	
    <%@ include file="access-modals.jsp" %>

	<div class="modal" id="remove-user-dialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
		<div class="modal-body">
			<spring:message code="question.DeletePrivileges" />
		</div>
		<div class="modal-footer">
			<img id="remove-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a id="btnOkDeleteFromAccess" onclick="removeUser();" class="btn btn-primary" data-dismiss="modal"><spring:message code="label.Yes" /></a>
			<a id="btnCancelDeleteFromAccess" class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>			
		</div>
		</div>
		</div>
	</div>
	
	<c:if test="${message != null}">
		<script type="text/javascript">
			showInfo('${message}');
		</script>
	</c:if>
	
	<div class="modal" id="busydialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
		<div class="modal-body" style="padding-left: 30px; text-align: center">		
			<spring:message code="label.PleaseWait" /><br /><br />
			<img id="add-wait-animation" src="${contextpath}/resources/images/ajax-loader.gif" />
		</div>
		</div>
		</div>
	</div>

</body>
</html>
