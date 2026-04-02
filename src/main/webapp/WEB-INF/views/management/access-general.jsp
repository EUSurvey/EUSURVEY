<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<style>
    .dep-tree { list-style-type: none; margin: 0; padding: 0; }
    .dep-tree-child { margin-left: 30px;}

    .dep-tree .check {
        margin-right: 5px !important;
    }

    #add-user-dialog label {
        margin-bottom: 0;
        margin-top: 10px;
    }
</style>

	<div style="text-align: center; margin-top: 20px;">
		<c:choose>
			<c:when test="${USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1 || form.survey.owner.id == USER.id}">
				<a id="btnAddUserFromAccess" class="btn btn-default" onclick="showAddUserDialog(false)"><spring:message code="label.AddUser" /></a>
				<c:if test="${USER.getGlobalPrivilegeValue('ECAccess') > 0}">
					<a id="btnAddDptFromAccess" class="btn btn-default" onclick="showAddDepartmentDialog()"><spring:message code="label.AddDepartment" /></a>
				</c:if>
			</c:when>
			<c:otherwise>
				<a class="btn disabled btn-default"><spring:message code="label.AddUser" /></a>
			</c:otherwise>
		</c:choose>
	</div>
	
	<table id="tblPrivilegesFromAccess" class="table table-bordered table-striped table-styled" style="margin-left: auto; margin-right: auto; margin-top: 40px;">
	
		<thead>
			<tr style="text-align: center;">
				<th style="vertical-align: middle;"><spring:message code="label.User" />
                                           <c:if test="${!oss}">/ <spring:message code="label.Department" /></c:if>
                                       </th>
				<th style="vertical-align: middle;"><spring:message code="label.Type" /></th>
				<th style="vertical-align: middle; width: 20%; text-align: center"><spring:message code="label.AccessFormPreview" /></th>
				<th style="vertical-align: middle; width: 20%; text-align: center"><spring:message code="label.Results" /></th>
				<th style="vertical-align: middle; width: 20%; text-align: center"><spring:message code="label.FormManagement" /></th>
				<th style="vertical-align: middle; width: 20%; text-align: center"><spring:message code="label.ManageInvitations" /></th>
				<th style="vertical-align: middle; width: 10%; text-align: center"><spring:message code="label.Actions" /></th>
			</tr>
		</thead>
		
		<tbody>
	
		<c:forEach items="${accesses}" var="access">
			<c:choose>
				<c:when test="${access.readonly}">
					<tr id="accessrow${access.id}" class="readonly">
				</c:when>
				<c:otherwise>
					<tr id="accessrow${access.id}">
				</c:otherwise>
			</c:choose>			
			
				<td style="vertical-align: middle;">
					<c:choose>
						<c:when test="${access.department != null && access.department.length() > 0}">
							<esapi:encodeForHTML>${access.department}</esapi:encodeForHTML>
						</c:when>
						<c:otherwise>
							<c:if test="${access.user.isExternal() and (!oss)}">
								<span class="externaluser hideme"></span>
							</c:if>
							<esapi:encodeForHTML>${access.user.name} ${access.user.department}</esapi:encodeForHTML>
						</c:otherwise>
					</c:choose>
				</td>
				<td style="text-align: center; vertical-align: middle;">
					<c:choose>
						<c:when test="${access.department != null && access.department.length() > 0}">
							<img data-toggle="tooltip" title="<spring:message code="label.Department"/>" src="${contextpath}/resources/images/group.png" alt="Group" />
						</c:when>
						<c:otherwise>
							<img data-toggle="tooltip" title="<spring:message code="label.User"/>" src="${contextpath}/resources/images/user.png" alt="User" />
						</c:otherwise>
					</c:choose>
				</td>
				<c:choose>
					<c:when test="${USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1 || form.survey.owner.id == USER.id}">
						<td style="text-align: center">
							<c:if test="${access.getLocalPrivilegeValue('AccessDraft') == 2}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletGreen" onclick="changePrivilege('AccessDraft',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
							</c:if>
							<c:if test="${access.getLocalPrivilegeValue('AccessDraft') == 1}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletYellow" onclick="changePrivilege('AccessDraft',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read" />
							</c:if>
							<c:if test="${access.getLocalPrivilegeValue('AccessDraft') == 0}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletRed" onclick="changePrivilege('AccessDraft',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="none" />
							</c:if>
						</td>
						<td style="text-align: center">
							<c:if test="${access.getLocalPrivilegeValue('AccessResults') == 2}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletGreen" onclick="changePrivilege('AccessResults',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
							</c:if>
							<c:if test="${access.getLocalPrivilegeValue('AccessResults') == 1}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletYellow" onclick="changePrivilege('AccessResults',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read" />
							</c:if>
							<c:if test="${access.getLocalPrivilegeValue('AccessResults') == 0}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletRed" onclick="changePrivilege('AccessResults',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="none" />
							</c:if>	
						</td>
						<td style="text-align: center">
							<c:choose>
								<c:when test="${access.user.formPrivilege < 1}">
									<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletGrey" src="${contextpath}/resources/images/bullet_ball_glass_gray.png" alt="none" />
								</c:when>
								<c:when test="${access.getLocalPrivilegeValue('FormManagement') == 2}">
									<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletGreen" onclick="changePrivilege('FormManagement',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
								</c:when>
								<c:when test="${access.getLocalPrivilegeValue('FormManagement') == 1}">
									<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletYellow" onclick="changePrivilege('FormManagement',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read" />
								</c:when>
								<c:when test="${access.getLocalPrivilegeValue('FormManagement') == 0}">
									<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletRed" onclick="changePrivilege('FormManagement',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="none" />
								</c:when>		
							</c:choose>
						</td>
						<td style="text-align: center">
							<c:if test="${access.getLocalPrivilegeValue('ManageInvitations') == 2}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletGreen" onclick="changePrivilege('ManageInvitations',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
							</c:if>
							<c:if test="${access.getLocalPrivilegeValue('ManageInvitations') == 1}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletYellow" onclick="changePrivilege('ManageInvitations',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read" />
							</c:if>
							<c:if test="${access.getLocalPrivilegeValue('ManageInvitations') == 0}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletRed" onclick="changePrivilege('ManageInvitations',${access.id});" src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="none" />
							</c:if>	
						</td>
						<td style="vertical-align: middle; text-align: center">
							<c:if test="${!access.readonly}">
								<a data-toggle="tooltip" title="<spring:message code="label.Remove"/>" class="iconbutton" onclick="showRemoveDialog(${access.id},'${access.user.login}', false);"><span class="glyphicon glyphicon-remove"></span></a>
							</c:if>	
						</td>
					</c:when>
					<c:otherwise>
						<td style="text-align: center">
							<c:if test="${access.getLocalPrivilegeValue('AccessDraft') == 2}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletGreen" src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
							</c:if>
							<c:if test="${access.getLocalPrivilegeValue('AccessDraft') == 1}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletYellow" src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read" />
							</c:if>
							<c:if test="${access.getLocalPrivilegeValue('AccessDraft') == 0}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletRed" src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="none" />
							</c:if>
						</td>
						<td style="text-align: center">
							<c:if test="${access.getLocalPrivilegeValue('AccessResults') == 2}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletGreen" src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
							</c:if>
							<c:if test="${access.getLocalPrivilegeValue('AccessResults') == 1}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletYellow" src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read" />
							</c:if>
							<c:if test="${access.getLocalPrivilegeValue('AccessResults') == 0}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletRed" src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="none" />
							</c:if>	
						</td>
						<td style="text-align: center">
							<c:choose>
								<c:when test="${access.user.formPrivilege < 1}">
									<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletGrey"  src="${contextpath}/resources/images/bullet_ball_glass_gray.png" alt="none" />
								</c:when>
								<c:when test="${access.getLocalPrivilegeValue('FormManagement') == 2}">
									<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletGreen" src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
								</c:when>
								<c:when test="${access.getLocalPrivilegeValue('FormManagement') == 1}">
									<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletYellow" src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read" />
								</c:when>
								<c:when test="${access.getLocalPrivilegeValue('FormManagement') == 0}">
									<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletRed" src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="none" />
								</c:when>		
							</c:choose>
						</td>
						<td style="text-align: center">
							<c:if test="${access.getLocalPrivilegeValue('ManageInvitations') == 2}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletGreen" src="${contextpath}/resources/images/bullet_ball_glass_green.png" alt="read/write" />
							</c:if>
							<c:if test="${access.getLocalPrivilegeValue('ManageInvitations') == 1}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletYellow" src="${contextpath}/resources/images/bullet_ball_glass_yellow.png" alt="read" />
							</c:if>
							<c:if test="${access.getLocalPrivilegeValue('ManageInvitations') == 0}">
								<img data-toggle="tooltip" title="<spring:message code="label.EditRights"/>" class="roleBulletRed" src="${contextpath}/resources/images/bullet_ball_glass_red.png" alt="none" />
							</c:if>	
						</td>
						<td style="vertical-align: middle; text-align: center">
							<c:if test="${!access.readonly}">
								<a data-toggle="tooltip" title="<spring:message code="label.Remove"/>" class="iconbutton"><span class="glyphicon glyphicon-remove"></span></a>
							</c:if>
						</td>
					</c:otherwise>
				</c:choose>
			</tr>
		</c:forEach>
		
		</tbody>
	
	</table>
	
	<div id="tbllist-empty" class="noDataPlaceHolder" <c:if test="${accesses.size() == 0}">style="display:block;"</c:if>>
		<p>
			<spring:message code="label.NoDataPrivilegeText"/>&nbsp;<img src="${contextpath}/resources/images/icons/32/forbidden_grey.png" alt="no data"/>
		<p>
	</div>

	<script>
	    function showAddUserDialog(results)
        {
            $("#add-resultMode").val(results);
            $("#add-resultMode-Email").val(results);

            // select european commision if exists
            var exists = false;
            $('#add-user-type-ecas option').each(function(){
                if (this.value == "eu.europa.ec") {
                    exists = true;
                    return false;
                }
            });
            if (exists)
            {
                $("#add-user-type-ecas").val("eu.europa.ec");
            }

            checkUserTypeAccess();

			$("#chkGiveFullAccess").prop("checked", false)
			$('#add-department-name').val('');
            $('#add-user-name').val('');
            $('#add-first-name').val('');
            $('#add-last-name').val('');
            $('#add-user-email').val('');
            $("#search-results-more").hide();
            $('#add-user-dialog').modal();
        }

        function showAddDepartmentDialog()
        {
			$("#chkGiveFullAccessGroup").prop("checked", false)
            $('#add-group-dialog').modal();
            $("#add-group-type-ecas").val("eu.europa.ec");

            var domainSelected;
            domainSelected=$("#add-group-type-ecas").val();
            if(domainSelected==null)
                domainSelected="eu.europa.ec";

            loadTopDepartments(domainSelected);
        }

        function searchUserForAccess(order)
        {
            $("#noEmptySearchIconAccess").hide();
            $("#noEmptySearchTextAccess").text("");

            var name = $("#add-user-name").val();
            var first = $("#add-first-name").val();
            var last = $("#add-last-name").val();
            var email = $("#add-user-email").val();
            var department = $("#add-department-name").val();
            var type = $("#add-user-type-ecas").val();

            if (type != "system" && type != "external")
            {
                //case eu.europa.ec: Admin and form manager EC
                if (!(email != '' || department != '' || first != '' || last != '' || name != '')) {
                    $("#noEmptySearchIconAccess").show();
                    $("#noEmptySearchTextAccess").text(noEmptySearch);
                    return;
                }
            } else if (type == "system")
            {
                //case system
                if (!(email != '' || name != '')) {
                    $("#noEmptySearchIconAccess").show();
                    $("#noEmptySearchTextAccess").text(noEmptySearch);
                    return;
                }
            }

            var s = "name=" + name + "&type=" + type + "&department=" + department+ "&email=" + email + "&first=" + first + "&last=" + last + "&order=" + order;

            $("#add-user-dialog").modal('hide');
            $("#busydialog").modal('show');

            $("#search-results-more").hide();

            $.ajax({
                type:'GET',
                  url: contextpath + "/logins/usersJSON",
                  data: s,
                  dataType: 'json',
                  cache: false,
                  success: function( users ) {
                      $("#search-results-access").find("tbody").empty();
                      var body = $("#search-results-access").find("tbody").first();

                      for (var i = 0; i < users.length; i++ )
                      {
                        $(body).append(users[i]);
                      }

                      var hiddenTableHeaders = $("#search-results-access th.hideme");
                      for (var i = 0; i < hiddenTableHeaders.length; i++ )
                      {
                          $('#search-results-access td:nth-child(' + hiddenTableHeaders[i].cellIndex + ')').hide();
                      }

                      if (type != "system" && users.length >= 100)
                      {
                          $("#search-results-more").show();
                      }

                      $(body).find("tr").click(function() {
                          $("#search-results-access").find(".success").removeClass("success");
                          $(this).addClass("success");
                        });

                      $("#busydialog").modal('hide');
                      $("#add-user-dialog").modal('show');
                  }, error: function() {
                      $("#busydialog").modal('hide');
                      $("#add-user-dialog").modal('show');
                }});

            $("#search-results-access-none").hide();

        }

        function checkUserTypeAccess()
        {
            $("#noEmptySearchIconAccess").hide();
            $("#noEmptySearchTextAccess").text('');

            $("#search-results-access").find("tbody").empty();

            if ($("#add-user-type-ecas").val() != "system" && $("#add-user-type-ecas").val() != "external")
            {
                $("#add-user-department-div").show();
                $("#add-user-firstname-div").show();
                $("#add-user-lastname-div").show();
                $("#eulogin-span-access").show();
            } else if ($("#add-user-type-ecas").val() == "external")
            {
                $("#add-user-department-div").hide();
                $("#add-user-firstname-div").show();
                $("#add-user-lastname-div").show();
                $("#eulogin-span-access").show();
            } else {
                $("#add-user-department-div").hide();
                $("#add-user-firstname-div").hide();
                $("#add-user-lastname-div").hide();
                $("#eulogin-span-access").hide();
            }
        }

        function addUser()
        {
            if ($("#search-results-access").find(".success").length == 0)
            {
                $("#search-results-access-none").show();
                return;
            }

			const user = $("#search-results-access").find(".success").first()
            var login = user.attr("id");
            var ecas = $("#add-user-type-ecas").val() != "system";
			const external = user.hasClass("externaluser")

            var chkGiveFullAccess = $("#chkGiveFullAccess").is(":checked");

            $("#search-results-access-none").hide();

            if ($("#add-form").length > 0) {
                // in access page
                $("#add-wait-animation").show();
                $("#add-form-login").val(login);
                $("#add-form-ecas").val(ecas);
                $("#add-form-givefullaccess").val(chkGiveFullAccess);
				if (chkGiveFullAccess && external) {
					$('#add-user-dialog').modal("hide");
					$("#FullAccess4Externals-dialog").modal("show");
				} else {
					$("#add-form").submit();
				}
            } else {
				// in bulk change dialog
				if (chkGiveFullAccess && external) {
					$('#add-user-dialog').modal("hide");
					$("#FullAccess4Externals-dialog").modal("show");
				} else {
					addEntryToBulkChangeDetails(login, true, ecas, chkGiveFullAccess);
				}
            }
        }

        //TODO: adapt for bulk change
        function addUserByEmail()
        {
            var chkGiveFullAccess = $("#chkGiveFullAccess").is(":checked");

            let mailInput = $("#add-user-email").val();
            if (mailInput.length <= 0) {
                $("#invalidEmailsIcon").show();
                $("#invalidEmailsText").text(atLeastOneMail);
                return;
            }

            let emails = mailInput.split(";").map(s => s.trim());

            $("#add-wait-animation").show();
            $("#add-form-emails").val(emails);
            $("#add-form-emailgivefullaccess").val(chkGiveFullAccess);
            $("#add-form-email").submit();
        }

        function addGroup()
        {
           $("#add-wait-animation").show();
           var chkGiveFullAccess = $("#chkGiveFullAccessGroup").is(":checked");

           let department = "";
           if ($("input[name='department']:checked").length == 0) {
               department = $('#add-group-type-ecas').val();
           } else {
               department = $("input[name='department']:checked").val();
           }

           if ($("#add-form").length > 0) {
              // in access page
              $("#add-form-group-name").val(department);
              $("#add-form-groupgivefullaccess").val(chkGiveFullAccess);
              $("#add-form-group").submit();
           } else {
              // in bulk change dialog
              addEntryToBulkChangeDetails(department, false, undefined, chkGiveFullAccess);
           }
        }

        function removeUser()
        {
            $("#remove-wait-animation").show();
            $("#remove-id").val(selectedId);
            $("#remove-form").submit();
        }

		function openEntities(targetul, isDGs) {
        if ($(targetul).closest("span").find("a").first().find(".folderimage").length > 0)
        {
            //open
            if ($(targetul).children().length > 0)
            {
                $(targetul).show();
                $(targetul).closest("span").find("a").first().find(".folderimage").each(function(){
                  $(this).removeClass("folderimage").addClass("folderopenimage").attr("src", contextpath + "/resources/images/folderopen.png");
                });
            } else {
                $( "#wheel" ).show();
                $.ajax({
                    type:'GET',
                      url: contextpath + "/noform/management/departmentsJSON",
                      data: {term: isDGs ? "dgs" : "aex", isdgs: isDGs},
                      dataType: 'json',
                      success: function( list ) {

                      for (var i = 0; i < list.length; i++ )
                      {
                        var li = document.createElement("li");
                        var span = document.createElement("span");

                        $(span).attr("onclick","disabledEventPropagation(event);").attr("onselectstart","return false;").attr("id","span" + list[i].key);

                        var input = document.createElement("input");
                        $(input).css("margin-left","10px").attr("onclick","disabledEventPropagation(event);").attr("type","radio").addClass("check").attr("name","department").val(list[i].key);

                        if ($("#readonlytree").length > 0 && $("#readonlytree").val() == 'true')
                        {
                            $(input).attr("disabled", "disabled");
                        }

                        if (list[i].value == '0')
                        {
                            //this means there are children
                            var link = document.createElement("a");
                            $(link).attr("onclick","openChildren($(this).closest('li').find('ul').first(), '" + list[i].key + "'," + isDGs + ")").html("<img class='folderimage' src='" + contextpath + "/resources/images/folderclosed.png'></img>");
                            $(span).append(link);
                        } else {
                            $(span).append("<img class='folderitemimage' src='" + contextpath + "/resources/images/folderitem.png' />");
                        }

                        $(span).append(input);
                        $(span).append(list[i].key);

                        $(li).append(span);

                        if (list[i].value == '0')
                        {
                            //this means there are children
                            var ul = document.createElement("ul");
                            $(ul).addClass("dep-tree").addClass("dep-tree-child").hide();
                            $(span).append(ul);
                        }

                        $(targetul).append(li);
                      }

                      $(targetul).closest("span").find("a").first().find(".folderimage").each(function(){
                            $(this).removeClass("folderimage").addClass("folderopenimage").attr("src", contextpath + "/resources/images/folderopen.png");
                        });


                      $(targetul).show();
                      $( "#wheel" ).hide();

                    }});
            }
        } else {
            //close
            $(targetul).hide();
            $(targetul).closest("span").find("a").first().find(".folderopenimage").each(function(){
                $(this).removeClass("folderopenimage").addClass("folderimage").attr("src", contextpath + "/resources/images/folderclosed.png");
            });
        }
    }

    function domainChanged()
    {
        const domain = $("#add-group-type-ecas").val();

        if (domain != 'eu.europa.ec') {
            $('#add-group-tree-div').hide();
            return;
        }

        $('#add-group-tree-div').show();
        loadTopDepartments(domain);
    }

    function loadTopDepartments(domain)
    {
        if (domain != "eu.europa.ec") {

        } else {

        }
    }

    function recursiveOpenChildren(child, globalprefix)
    {
        if (child.indexOf(".") > -1)
        {
            var prefix = child.substring(0, child.indexOf("."));

            $("input[name='node" + globalprefix + prefix + "']").each(function(){
                if ($(this).parent().find(".folderopenimage").length == 0)
                {
                     openChildren($(this).closest("li").find('ul'), $(this).val());
                }
            });
            recursiveOpenChildren(child.substring(child.indexOf(".")+1), globalprefix + prefix + ".");
        }
    }

    function openChildren(targetul, department, isDGs)
    {
        if ($(targetul).closest("span").find("a").first().find(".folderimage").length > 0)
        {
            //open
            if ($(targetul).children().length > 0)
            {
                $(targetul).show();
                  $(targetul).closest("span").find("a").first().find(".folderimage").each(function(){
                      $(this).removeClass("folderimage").addClass("folderopenimage").attr("src", contextpath + "/resources/images/folderopen.png");
                    });
            } else {
                $.ajax({
                    type:'GET',
                      url: contextpath + "/noform/management/departmentsJSON",
                      data: {term:department, isdgs:isDGs},
                      dataType: 'json',
                      success: function( list ) {

                      for (var i = 0; i < list.length; i++ )
                      {
                        var li = document.createElement("li");
                        var span = document.createElement("span");

                        $(span).attr("onclick","disabledEventPropagation(event);").attr("onselectstart","return false;").attr("id","span" + list[i].key);

                        var input = document.createElement("input");
                        $(input).css("margin-left","10px").attr("onclick","disabledEventPropagation(event);").attr("type","radio").addClass("check").attr("name","department").val(list[i].key);

                        if ($("#readonlytree").length > 0 && $("#readonlytree").val() == 'true')
                        {
                            $(input).attr("disabled", "disabled");
                        }

                        if (list[i].value == '0')
                        {
                            //this means there are children
                            var link = document.createElement("a");
                            $(link).attr("onclick","openChildren($(this).closest('li').find('ul').first(), '" + list[i].key + "', " + isDGs + ")").html("<img class='folderimage' src='" + contextpath + "/resources/images/folderclosed.png'></img>");
                            $(span).append(link);
                        } else {
                            $(span).append("<img class='folderitemimage' src='" + contextpath + "/resources/images/folderitem.png' />");
                        }

                        $(span).append(input);
                        $(span).append(list[i].key);

                        $(li).append(span);

                        if (list[i].value == '0')
                        {
                            //this means there are children
                            var ul = document.createElement("ul");
                            $(ul).addClass("dep-tree").addClass("dep-tree-child").hide();
                            $(span).append(ul);
                        }

                        $(targetul).append(li);
                      }

                      $(targetul).closest("span").find("a").first().find(".folderimage").each(function(){
                            $(this).removeClass("folderimage").addClass("folderopenimage").attr("src", contextpath + "/resources/images/folderopen.png");
                        });


                      $(targetul).show();
                      $( "#wheel" ).hide();

                    }});
            }
        } else {
            //close
            $(targetul).hide();
            $(targetul).closest("span").find("a").first().find(".folderopenimage").each(function(){
                $(this).removeClass("folderopenimage").addClass("folderimage").attr("src", contextpath + "/resources/images/folderclosed.png");
            });
        }
    }

	var selectedPrivilege = null;
	var selectedId = null;

    function changePrivilege(privilege, id)
    {
        selectedPrivilege = privilege;
        selectedId = id;

        if (privilege == 'AccessDraft')
        {
            $("#yellowArea").hide();
        } else {
            $("#yellowArea").show();
        }

        $('#user-dialog').modal();
    }

    let selectedPrivilegeValue;
    function updatePrivilege(value)
    {
        $("#update-form-value").val(value);
        selectedPrivilegeValue = value;

        if (value == 2 && selectedPrivilege == "ManageInvitations")
        {
            let row
            if ($("#update-form-privilege").length > 0) {
                row = $("#accessrow" + selectedId)
            } else {
                row = $(selectedId).closest("tr")
            }

            if (${!USER.isExternal()} && row.find(".externaluser").length > 0)
            {
                $("#user-dialog").modal("hide");
                $("#ManageInvitations4Externals-dialog").modal("show");
                return;
            }
        }

        updatePrivilege2();
    }

    function updatePrivilege2()
    {
        $("#ManageInvitations4Externals-dialog").modal("hide");
        if ($("#update-form-privilege").length > 0) {
			$("#wait-animation").show();
			$("#update-form-id").val(selectedId);
			$("#update-form-privilege").val(selectedPrivilege);
			$("#update-form").submit();
        } else {
            updatePrivilegeInBulkChangeDetails(selectedId, selectedPrivilegeValue);
            $("#user-dialog").modal("hide");
        }
    }

	function confirmFullAccess() {
		$("#FullAccess4Externals-dialog").modal("hide");
		if ($("#update-form-privilege").length > 0) {
			$("#add-form").submit();
		} else {
			var login = $("#search-results-access").find(".success").first().attr("id");
			var ecas = $("#add-user-type-ecas").val() != "system";
			addEntryToBulkChangeDetails(login, true, ecas, true);
		}
	}
</script>