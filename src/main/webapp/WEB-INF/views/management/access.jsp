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
	    .dep-tree { list-style-type: none; margin: 0; padding: 0; }
	    .dep-tree-child { margin-left: 30px;}

		#search-results td, th {
	    	white-space:nowrap;
		}
		
		#search-results td {
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
		
		.dep-tree .check {
			margin-right: 5px !important;
		}

		#tblResultPrivileges select {
			background-color: #fff;
		}
		
		a.disabled {
			color: #ccc;
		}

		#add-user-dialog label {
			margin-bottom: 0;
			margin-top: 10px;
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
			<input type="hidden" name="resultMode" id="add-resultMode" value="false" />
		</form:form>
		
		<form:form style="display: none" id="add-form-email" method="POST" action="access">
			<input type="hidden" name="target" value="addUserEmail" />
			<input type="hidden" name="emails" id="add-form-emails" value="" />
			<input type="hidden" name="resultMode" id="add-resultMode-Email" value="false" />
		</form:form>
		
		<form:form style="display: none" id="add-form-group" method="POST" action="access">
			<input type="hidden" name="target" value="addGroup" />
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
					<div style="padding-top: 130px;">
						<%@ include file="access-results.jsp" %>
					</div>
				</c:when>
			</c:choose>
			
		</div>
		
	</div>

	<%@ include file="../footer.jsp" %>	

	<div class="modal" id="add-user-dialog" data-backdrop="static">
		<div class="modal-dialog modal-lg">
    	<div class="modal-content">
		<div class="modal-header">			
			<spring:message code="label.AddUser" />
			&nbsp;
			<a onclick="$(this).closest('.modal-header').find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
													
			<div style="clear: both"></div>
			<c:if test="${!USER.isExternal()}">
				<div class="help" style="display: none; margin-top: 10px;">
					<span><spring:message code="info.AddUserAccess" /></span>
				</div>
			</c:if>
			<c:if test="${USER.isExternal()}">
				<div class="help" style="display: none; margin-top: 10px;">
					<span><spring:message code="info.AddUserAccessFormManager" /></span>
				</div>
			</c:if>
		</div>
		<div class="modal-body">
			<c:choose>
				<c:when test="${USER.isExternal()}">
					<div style="float: left; width: 250px;  margin-right: 10px;">
						<label for="add-user-name"><spring:message code="label.EmailAddress" /></label><br />
						<input type="text" maxlength="500" id="add-user-email" style="min-width: 400px;"/>
						<p class="help" style="min-width: 400px;">
							<spring:message code="info.AddUserFormManagerInstructions" /><br />
							<spring:message code="info.AddUserFormManagerInstructions2" />
						</p>
					</div>
					<div style="clear: both"></div>
					<div style="margin-top: 20px">
						<a id="btnCheck"  style="float: left;" onclick="searchEmailUser('mail');" class="btn btn-default" style="margin: 10px"><spring:message code="label.Check" /></a>
					</div>
					<div style="clear: both"></div>
					<div style="margin-top: 20px">
						<div id="foundEmailUsers" style="color: green; margin-bottom: 0.8rem;"></div>
						<div id="invalidEmails" style="margin-bottom: 0.8rem; display: inline-flex">
							<img id="invalidEmailsIcon" src="${contextpath}/resources/images/exclamation-triangle.svg" style="display: none; height: 2rem; margin-right: 8px;"></img>
							<div id="invalidEmailsText" style="color: red"></div>
						</div>
						<div style="clear: both"></div>
						<div id="notFoundEmails" style="color: red; display: inline-flex">
							<img id="notFoundEmailsIcon" src="${contextpath}/resources/images/exclamation-triangle.svg" style="display: none; height: 2rem; margin-right: 8px;"></img>
							<div id="notFoundEmailsText" style="color: red"></div>
						</div>
					</div>
				</c:when>
				<c:when test="${USER.getGlobalPrivilegeValue('ECAccess') > 0}">
					<div style="float: left; width: 250px;  margin-right: 10px;">
						<label for="add-user-name"><spring:message code="label.EmailAddress" /></label><br />
						<input type="text" maxlength="255" id="add-user-email" />
					</div>
				
					<div style="clear: both"></div>
					
					<div style="float: left; width: 250px;  margin-right: 10px;" id="add-user-firstname-div">
						<label for="add-department-name"><spring:message code="label.FirstName" /></label><br />
						<input type="text" maxlength="255" id="add-first-name" />
					</div>
					
					<div style="float: left; width: 250px;  margin-right: 10px;" id="add-user-lastname-div">						
						<label for="add-last-name"><spring:message code="label.LastName" /></label><br />
						<input type="text" maxlength="255" id="add-last-name" />
					</div>	
					
					<div style="float: left; width: 250px;  margin-right: 10px;" id="add-user-name-div">
						<label for="add-user-name"><spring:message code="label.UserName" /><span id="eulogin-span"> (EU Login)</span></label><br />
						<input type="text" maxlength="255" id="add-user-name" />
					</div>
					
					<div style="clear: both"></div>
					
					<div style="float: left; width: 250px; margin-right: 10px;" id="add-user-department-div">
						<label for="add-department-name"><spring:message code="label.Department" /></label><br />
						<input type="text" maxlength="255" id="add-department-name" />
					</div>
					
					<div style="float: left; width: 510px" id="add-user-domain-div">
						<label for="add-user-type-ecas"><spring:message code="label.Domain" /></label><br />
						<select id="add-user-type-ecas" onchange="checkUserType()" style="width: 510px" >
							<c:forEach items="${domains}" var="domain" varStatus="rowCounter">
								<option value="${domain.key}">${domain.value} </option>
							</c:forEach>
						</select>	
					</div>
					<div style="clear: both"></div>
					<div style="margin-top: 20px">
						<div id="noEmptySearch" style="margin-bottom: 0.8rem; display: inline-flex">
							<img id="noEmptySearchIcon" src="${contextpath}/resources/images/exclamation-triangle.svg" style="display: none; height: 2rem; margin-right: 8px;"></img>
							<div id="noEmptySearchText" style="color: red"></div>
						</div>
					</div>
				</c:when>
				<c:when test="${USER.type == 'SYSTEM'}">
					<div style="float: left; width: 250px;  margin-right: 10px;">
						<label for="add-user-email"><spring:message code="label.EmailAddress" /></label><br />
						<input type="text" maxlength="255" id="add-user-email" />
					</div>

					<div style="float: left; width: 250px;  margin-right: 10px;" id="add-user-name-div">
						<label for="add-user-name"><spring:message code="label.UserName" /></label><br />
						<input type="text" maxlength="255" id="add-user-name" />
					</div>

					<div style="clear: both"></div>

					<div style="width: 250px" id="add-user-domain-div">
						<label for="add-user-type-ecas"><spring:message code="label.Domain" /></label><br />
						<select id="add-user-type-ecas" onchange="checkUserType()">
							<option value="system" selected="selected"><spring:message code="label.System" /></option>
						</select>
					</div>
				</c:when>
				<c:otherwise>
					<div style="float: left; width: 250px;  margin-right: 10px;">
						<label for="add-user-name"><spring:message code="label.EmailAddress" /></label><br />
						<input type="text" maxlength="255" id="add-user-email" />
					</div>
					
					<div style="clear: both"></div>
					
					<div style="float: left; width: 250px;  margin-right: 10px;" id="add-user-firstname-div">
						<label for="add-department-name"><spring:message code="label.FirstName" /></label><br />
						<input type="text" maxlength="255" id="add-first-name" />
					</div>
					
					<div style="float: left; width: 250px;  margin-right: 10px;" id="add-user-lastname-div">						
						<label for="add-last-name"><spring:message code="label.LastName" /></label><br />
						<input type="text" maxlength="255" id="add-last-name" />
					</div>	
					
					<div style="float: left; width: 250px;  margin-right: 10px;" id="add-user-name-div">
						<label for="add-user-name"><spring:message code="label.UserName" /></label><br />
						<input type="text" maxlength="255" id="add-user-name" />
					</div>
					
					<div style="clear: both"></div>		
					
					<div style="width: 250px" id="add-user-domain-div">
						<label for="add-user-type-ecas"><spring:message code="label.Domain" /></label><br />
						<select id="add-user-type-ecas" onchange="checkUserType()">
							<option value="external" selected="selected"><spring:message code="label.EXT" /></option>
						</select>
					</div>
				</c:otherwise>
			</c:choose>
			
			<div style="clear: both"></div>

			<c:if test="${!USER.isExternal()}">
				<div style="text-align: right">
					<div style="float: left; color: #f00; padding: 15px;" id="search-results-more" class="hideme"><spring:message code="message.SearchLimit100" /></div>
					<a id="btnSearchFromAccess"  onclick="searchUser('login');" class="btn btn-default" style="margin: 10px"><spring:message code="label.Search" /></a>
				</div>

				<div class="round" style="min-height: 323px; max-height: 323px; overflow: auto; width: 100%; overflow: auto; border: 1px solid #ddd" id="search-results-div">
					<table id="search-results" class="table table-bordered table-hover table-styled" style="max-width: none; margin-bottom: 0px">
						<thead>
							<tr>
								<th onclick="searchUser('mail');"><spring:message code="label.Email" /></th>
								<th onclick="searchUser('login');"><spring:message code="label.UserName" /></th>
								<th onclick="searchUser('first');"><spring:message code="label.FirstName" /></th>
								<th onclick="searchUser('last',);"><spring:message code="label.LastName" /></th>
								<th onclick="searchUser('department');" <c:if test="${oss}">class="hideme"</c:if> ><spring:message code="label.Department" /></th>
							</tr>
						</thead>
						<tbody></tbody>
					</table>
					<div id="search-results-none" class="hideme"><spring:message code="message.NoResultSelected" /></div>
				</div>
			</c:if>
			
		</div>
		<div class="modal-footer">
			<c:choose>
				<c:when test="${USER.isExternal()}">
					<img id="add-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
					<a id="btnAddUserFromMails"  onclick="addUserByEmail();" class="btn btn-primary"><spring:message code="label.Add" /></a>
					<a id="btnCancelAdddUserFromAccess"  onclick="resetEmailFeedback();" class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>
				</c:when>
				<c:otherwise>
					<img id="add-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
					<a id="btnOkAddUserFromAccess"  onclick="addUser();" class="btn btn-primary"><spring:message code="label.OK" /></a>
					<a id="btnCancelAdddUserFromAccess"  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>
				</c:otherwise>
			</c:choose>
		</div>
		</div>
		</div>
	</div>
	
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
