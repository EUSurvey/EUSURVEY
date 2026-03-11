<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

	<div class="modal" id="user-dialog" data-backdrop="static">
		<div class="modal-dialog">
    	<div class="modal-content">
		<div class="modal-body">
			<a onclick="updatePrivilege(0);" id="btnNoAccessPrivilegFromDialog" class="btn btn-default" style="margin-right: 10px;">
		  		<img src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="read/write" />
		  	</a>
		  	<spring:message code="label.NoAccess" /><br />
		  	<span id="yellowArea">
			  	<a onclick="updatePrivilege(1);" id="btnROAccessPrivilegFromDialog" class="btn btn-default" style="margin-right: 10px;">
			  		<img src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read/write" />
			  	</a>
			  	<spring:message code="label.ReadingAccess" /><br />
		  	</span>
		  	<a onclick="updatePrivilege(2);" id="btnRWAccessPrivilegFromDialog" class="btn btn-default" style="margin-right: 10px;">
		  		<img src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
		  	</a>
		  	<spring:message code="label.ReadWriteAccess" />
		</div>
		<div class="modal-footer">
			<img id="wait-animation" class="hideme" style="margin-right:120px;" src="${contextpath}/resources/images/ajax-loader.gif" />
		  	<a  class="btn btn-primary" data-dismiss="modal"><spring:message code="label.Cancel" /></a>			
		</div>
		</div>
		</div>
	</div>
	
	<div class="modal" id="add-group-dialog" data-backdrop="static">
		<div class="modal-dialog">
    	<div class="modal-content">
		<div class="modal-header"><spring:message code="label.SelectPrivilegedDepartment" /></div>
		<div class="modal-body">
			
			<div id="add-group-domain-div" style="margin-bottom: 10px;">
				<label for="add-group-type-ecas"><spring:message code="label.Domain" /></label>
				<select id="add-group-type-ecas" onchange="domainChanged()"  style="width: 450px" >
					<c:forEach items="${domains}" var="domain" varStatus="rowCounter">
						<option value="${domain.key}">${domain.value} </option>	
					</c:forEach>
				</select>	
			</div>
			
			<div style="height: 400px; overflow: auto;" id="add-group-tree-div" >
				<label for="add-group-name"><spring:message code="label.SelectAnEntity" />:</label>
				
				<ul id="tree" class="dep-tree" style="-moz-user-select: none;">
					<li>
						<span onclick="disabledEventPropagation(event);" onselectstart="return false;" id="spanRoot">
							<a onclick="openEntities($(this).closest('li').find('ul').first(), true)">
								<img class="folderimage" src="/eusurvey/resources/images/folderclosed.png" />
							</a>
							<input onclick="disabledEventPropagation(event);" type="radio" class="check" name="department" value="ec" style="margin-left: 10px;">
							<spring:message code="label.DGsAndServices" />
							<ul class="dep-tree dep-tree-child" style="display: block;"></ul>
						</span>
					</li>
					<li>
						<span onclick="disabledEventPropagation(event);" onselectstart="return false;" id="spanRoot">
							<a onclick="openEntities($(this).closest('li').find('ul').first(), false)">
								<img class="folderimage" src="/eusurvey/resources/images/folderclosed.png" />
							</a>
							<input onclick="disabledEventPropagation(event);" type="radio" class="check" name="department" value="ec" style="margin-left: 10px;">
							<spring:message code="label.ExecutiveAgencies" />
							<ul class="dep-tree dep-tree-child" style="display: block;"></ul>
						</span>
					</li>
				</ul>
			</div>

			<div id="add-department-div-GiveFullAccess" style="padding-top: 10px">
                <input type="checkbox" class="check" id="chkGiveFullAccessGroup" /> <spring:message code="label.GiveFullAccess" />
            </div>
						
		</div>
		<div class="modal-footer">
			<img id="add-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a id="btnOkAddDptFromAccess"  onclick="addGroup();" class="btn btn-primary" data-dismiss="modal"><spring:message code="label.OK" /></a>
			<a id="btnCancelAdddDptFromAccess"  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>				
		</div>
		</div>
		</div>
	</div>
	
	<div class="modal" id="ManageInvitations4Externals-dialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
    	<div class="modal-header"><spring:message code="label.Warning" /></div>
		<div class="modal-body">
			<spring:message code="question.ManageInvitations4Externals" />
		</div>
		<div class="modal-footer">
			<a onclick="updatePrivilege2();" class="btn btn-primary"><spring:message code="label.Yes" /></a>
			<a class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>			
		</div>
		</div>
		</div>
	</div>


	<div class="modal" id="FullAccess4Externals-dialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-header"><spring:message code="label.Warning" /></div>
				<div class="modal-body">
					<spring:message code="question.ManageInvitations4Externals" />
				</div>
				<div class="modal-footer">
					<a onclick="confirmFullAccess();" class="btn btn-primary"><spring:message code="label.Yes" /></a>
					<a class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>
				</div>
			</div>
		</div>
	</div>

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
    						<label for="add-user-name"><spring:message code="label.UserName" /><span id="eulogin-span-access"> (EU Login)</span></label><br />
    						<input type="text" maxlength="255" id="add-user-name" />
    					</div>

    					<div style="clear: both"></div>

    					<div style="float: left; width: 250px; margin-right: 10px;" id="add-user-department-div">
    						<label for="add-department-name"><spring:message code="label.Department" /></label><br />
    						<input type="text" maxlength="255" id="add-department-name" />
    					</div>

    					<div style="float: left; width: 510px" id="add-user-domain-div">
    						<label for="add-user-type-ecas"><spring:message code="label.Domain" /></label><br />
    						<select id="add-user-type-ecas" onchange="checkUserTypeAccess()" style="width: 510px" >
    							<c:forEach items="${domains}" var="domain" varStatus="rowCounter">
    								<option value="${domain.key}">${domain.value} </option>
    							</c:forEach>
    						</select>
    					</div>
    					<div style="clear: both"></div>
    					<div style="margin-top: 20px">
    						<div id="noEmptySearch" style="margin-bottom: 0.8rem; display: inline-flex">
    							<img id="noEmptySearchIconAccess" src="${contextpath}/resources/images/exclamation-triangle.svg" style="display: none; height: 2rem; margin-right: 8px;"></img>
    							<div id="noEmptySearchTextAccess" style="color: red"></div>
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
    						<select id="add-user-type-ecas" onchange="checkUserTypeAccess()">
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
    						<select id="add-user-type-ecas" onchange="checkUserTypeAccess()">
    							<option value="external" selected="selected"><spring:message code="label.EXT" /></option>
    						</select>
    					</div>
    				</c:otherwise>
    			</c:choose>

    			<div style="clear: both"></div>

    			<c:if test="${!USER.isExternal()}">
    				<div style="text-align: right">
    					<div style="float: left; color: #f00; padding: 15px;" id="search-results-more" class="hideme"><spring:message code="message.SearchLimit100" /></div>
    					<a id="btnSearchFromAccess"  onclick="searchUserForAccess('login');" class="btn btn-default" style="margin: 10px"><spring:message code="label.Search" /></a>
    				</div>

    				<div class="round" style="min-height: 323px; max-height: 323px; overflow: auto; width: 100%; overflow: auto; border: 1px solid #ddd" id="search-results-div">
    					<table id="search-results-access" class="table table-bordered table-hover table-styled" style="max-width: none; margin-bottom: 0px">
    						<thead>
    							<tr>
    								<th onclick="searchUserForAccess('mail');"><spring:message code="label.Email" /></th>
    								<th onclick="searchUserForAccess('login');"><spring:message code="label.UserName" /></th>
    								<th onclick="searchUserForAccess('first');"><spring:message code="label.FirstName" /></th>
    								<th onclick="searchUserForAccess('last',);"><spring:message code="label.LastName" /></th>
    								<th onclick="searchUserForAccess('department');" <c:if test="${oss}">class="hideme"</c:if> ><spring:message code="label.Department" /></th>
    							</tr>
    						</thead>
    						<tbody></tbody>
    					</table>
    					<div id="search-results-access-none" class="hideme"><spring:message code="message.NoResultSelected" /></div>
    				</div>
    			</c:if>

                <div id="add-user-div-GiveFullAccess" style="padding-top: 10px">
    			    <input type="checkbox" class="check" id="chkGiveFullAccess" /> <spring:message code="label.GiveFullAccess" />
                </div>
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