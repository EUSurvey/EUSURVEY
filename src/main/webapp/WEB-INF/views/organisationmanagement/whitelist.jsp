<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.ec.survey.model.administration.GlobalPrivilege" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page session="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.OrganisationManagement" /></title>
	<%@ include file="../includes.jsp" %>	
	<style type="text/css">
	    h1 {
            font-size: 18px !important;
            margin-top: 30px !important;
        }

        #OrganisationManagersTable td, #AuthorisedUsersTable td {
            vertical-align: middle;
        }

        .ui-menu-item {
          font-size: 13px !important;
        }

        .ui-state-hover,
        .ui-state-active,
        .ui-state-focus {
          text-decoration: none !important;
          color: #fff !important;
          background-image: none !important;
          background-color: #767676 !important;
          border-color: #555 !important;
          font-weight: normal !important;
        }

        .badge {
            margin-bottom: 10px; margin-right: 5px;
        }

        .badge span {
            cursor: pointer;
        }

        .filtercell {
            position: relative;
        }

        .filtercell input {
            color: #333;
        }

        .datefilter {
            margin-right: 10px;
        }

         .filtertools {
            position: absolute;
            right: 12px;
        }

        .labelinput {
            margin-top: 5px !important;
        }

	</style>
	
	<script type="text/javascript">
	    var noEmptySearch = '<spring:message code="label.NoEmptySearch" />';

		$(function() {
			$("#organisationmanagement-menu-tab").addClass("active");
			loadOrganisation();

			$('.filtercell').find("input").keyup(function(e){
                if(e.keyCode == 13){
                    loadOrganisation();
                }
            });
		});

		let userIdToDelete = 0;
		let sortCol = null;
		let sortAsc = true;

		function sortAuthorisedUsers(col,asc) {
		    sortCol = col;
		    sortAsc = asc;
		    loadOrganisation();
		}

		function loadOrganisation() {

            if ( $('#organisation').val().length == 0) {
                return;
            }

            $(".filtercell").each(function(){
                checkFilterCell(this, true);
            });

            $('#busydialog').modal('show');
            $.ajax({type: "POST",
                url: contextpath + "/organisationmanagement/organisation/" + $('#organisation').val(),
                dataType: 'json',
                data: {username: $('#FilterUserName').val(), email: $('#FilterEmail').val(), label: $('#FilterLabel').val(), from: $('#metafilterfrom').val(), to: $('#metafilterto').val(), sort: sortCol, asc: sortAsc},
                beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
                success :function(result)
                {
                    $('#organisation-content').show();

                    $('#AuthorisedUsersTableBody').empty();

                    if (result.length > 0) {
                        $('#NoAuthorisedUser').hide();

                        for (const user of result) {
                            let tr = document.createElement("tr");
                            let td = document.createElement("td");
                            $(td).append(user.name);
                            $(tr).append(td);

                            td = document.createElement("td");
                            $(td).append(user.email);
                            $(tr).append(td);

                            td = document.createElement("td");
                            $(td).attr("data-user-id", user.id);

                            let labeldiv = document.createElement("div");

                            for (let i = 0; i < user.labelsSorted.length; i++) {
                                let l = document.createElement("span");
                                $(l).text(user.labelsSorted[i].name).addClass("badge");

                                let x = document.createElement("span");
                                $(x).attr("onclick", "deleteLabel("+ user.id + "," + user.labelsSorted[i].id + ")").text(" ✖");

                                $(l).append(x);

                                $(labeldiv).append(l);
                            }

                            $(td).append(labeldiv);

                            var b = document.createElement("button");
                            $(b).addClass("btn btn-primary btn-xs").text("<spring:message code="label.Add" />").attr("onclick", "insertAddLabelBox(" + user.id + ", this)");
                            $(td).append(b);
                            $(tr).append(td);

                            td = document.createElement("td");
                            $(td).append(user.surveys);
                            $(tr).append(td);

                            td = document.createElement("td");
                            $(td).append(user.addedString);
                            $(tr).append(td);

                            td = document.createElement("td");
                            let button = document.createElement("button");
                            $(button).addClass("btn btn-link").html("<span class='glyphicon glyphicon-remove'></span>");
                            $(button).click(function() {
                                userIdToDelete = user.id;
                                $('#confirm-delete-user-dialog').modal('show');
                            });
                            $(td).css("width", "50px").append(button);
                            $(tr).append(td);

                            $('#AuthorisedUsersTableBody').append(tr);
                        }

                        $('#AuthorisedUsersTable').show();
                    } else {
                        $('#NoAuthorisedUser').show();
                        //$('#AuthorisedUsersTable').hide();
                    }

                    $('#busydialog').modal('hide');

                }, error: function(e) {
                    console.log(e);
                    $('#busydialog').modal('hide');
                }
             });
        }

        function addOrganisationUser(login, ecas) {
            $("#busydialog").modal('show');

            $.ajax({
                type: "POST",
                data: {organisation: $('#organisation').val(), login: login, ecas: ecas},
                url: contextpath + "/organisationmanagement/addAuthorisedUser",
                async: false,
                cache: false,
                beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
                success: function(data)
                {
                    if (data) {
                        showSuccess("<spring:message code="info.AuthorisedUserAdded" />");
                        loadOrganisation();
                    } else {
                        showError("<spring:message code="error.AuthorisedUserExists" />");
                    }
                    $("#busydialog").modal('hide');
                },
                error: function(e)
                {
                    console.log(e)
                    showError(getPropertyLabel("error.OperationFailed"));
                    $("#busydialog").modal('hide');
                }
            });
        }

        function deleteAuthorisedUser() {
            $("#busydialog").modal('show');

            $.ajax({
                type: "POST",
                data: {organisation: $('#organisation').val(), id: userIdToDelete},
                url: contextpath + "/organisationmanagement/deleteAuthorisedUser",
                async: false,
                cache: false,
                beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
                success: function(data)
                {
                    if (data) {
                        showSuccess("<spring:message code="info.AuthorisedUserRemoved" />");
                        loadOrganisation();
                    } else {
                        showError("<spring:message code="error.AuthorisedUserNotFound" />");
                    }
                    $("#busydialog").modal('hide');
                    $('#confirm-delete-user-dialog').modal('hide');
                },
                error: function(e)
                {
                    console.log(e)
                    showError("<spring:message code="error.OperationFailed" />");
                    $("#busydialog").modal('hide');
                    $('#confirm-delete-user-dialog').modal('hide');
                }
            });
        }

        function insertAddLabelBox(id, button) {
            const td = $(button).parent();
            const box = document.createElement("input");
            $(box).attr("type", "text").addClass("form-control labelinput");
            $(td).append(box);

            $(box).autocomplete({
                autoFocus: true,
                appendTo: $(td),
                source: "${contextpath}/forms/whitelistlabels?createNewLabel=true",
                search: function( event, ui ) {
                    let term = event.target.value;
                    const reg = /^[a-zA-Z0-9-_]+$/;

                    if( !reg.test( term ) ) {
                        if ($(td).find(".validation-error").length == 0)
                        {
                            $(td).append("<div class='validation-error'>" + tagsText + "</div>");
                        }
                        event.preventDefault();
                        $(box).autocomplete("close");
                        $("#bulkEditTagsLoading").hide();
                        return false;
                    } else {
                        $("#bulktagserrors").find(".validation-error").remove();
                    }

                    $("#bulkEditTagsLoading").show();
                },
                response: function( event, ui ) {
                    $("#bulkEditTagsLoading").hide();
                },
                select: function( event, ui ) {
                    let label = ui.item.value;

                    // a label needs at least 3 characters
                    if (label.length > 2) {
                      addLabel(id, label);
                      $(box).val("");
                    }

                    $("#bulkEditTagsLoading").hide();
                    return false;
                }
            });
        }

        function addLabel(id, label) {

            if (label.indexOf(" ") > -1) {
                label = label.substring(0, label.indexOf(" ")); // remove " (new label)"
            };

            var s = "user=" + id + "&label=" + label;

            $('#busydialog').modal('show');

            $.ajax({
                type:'POST',
                  url: "${contextpath}/organisationmanagement/whitelist/addlabel",
                  data: s,
                  beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
                  cache: false,
                  success: function( result ) {
                      if (result == 'exists')
                      {
                         showError("<spring:message code="error.NameAlreadyUsed" />");
                      } else if (result == 'error')
                      {
                          showError("<spring:message code="error.OperationFailed" />");
                      } else {
                          showSuccess("<spring:message code="info.LabelCreated" />");
                          loadOrganisation();
                      }
                      $('#busydialog').modal('hide');
                  },
                  error: function(e)
                  {
                      console.log(e);
                      $('#busydialog').modal('hide');
                  }
            });
        }

        function deleteLabel(userid, labelid) {

            var s = "user=" + userid + "&label=" + labelid;

            $('#busydialog').modal('show');

            $.ajax({
                type:'POST',
                  url: "${contextpath}/organisationmanagement/whitelist/removelabel",
                  data: s,
                  beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
                  cache: false,
                  success: function( result ) {
                      if (result == 'error')
                      {
                          showError("<spring:message code="error.OperationFailed" />");
                      } else {
                          showSuccess("<spring:message code="info.LabelRemoved" />");
                          loadOrganisation();
                      }
                      $('#busydialog').modal('hide');
                  },
                  error: function(e)
                  {
                      console.log(e);
                      $('#busydialog').modal('hide');
                  }
            });
        }
	
	</script>
		
</head>
<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
		<%@ include file="../menu.jsp" %>
		<%@ include file="../management/access-modals.jsp" %>
        <%@ include file="../management/access-general-js.jsp" %>

		<div class="page880" style="padding-bottom: 0px; padding-top: 100px; min-height: 500px;">
		    <h1><spring:message code="label.Organisation" /></h1>
            <select class="form-control" name="organisation" id="organisation" onchange="loadOrganisation()">
                <c:forEach items="${organisations}" var="organisation">
                    <option value="${organisation.key}">${organisation.value}</option>
                </c:forEach>
            </select>

            <div id="organisation-content" style="display: none">

                <h1><spring:message code="label.AuthorisedUsers" /></h1>

                <table id="AuthorisedUsersTable" class="table table-condensed table-bordered table-styled">
                    <thead>
                        <tr>
                            <th>
                                <div style="float: right">
                                    <a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sortAuthorisedUsers('username',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sortAuthorisedUsers('username',false);" class=""><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
                                </div>
                                <spring:message code="label.UserName" />
                            </th>
                            <th>
                                <spring:message code="label.Email" />
                                <div style="float: right">
                                    <a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sortAuthorisedUsers('email',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sortAuthorisedUsers('email',false);" class=""><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
                                </div>
                            </th>
                            <th style="width: 200px"><spring:message code="label.Label" /></th>
                            <th>
                                <spring:message code="label.Surveys" />
                                <div style="float: right">
                                    <a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sortAuthorisedUsers('surveys',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sortAuthorisedUsers('surveys',false);" class=""><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
                                </div>
                            </th>
                            <th>
                                <spring:message code="label.Added" />
                                <div style="float: right">
                                    <a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sortAuthorisedUsers('added',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sortAuthorisedUsers('added',false);" class=""><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
                                </div>
                            </th>
                            <th></th>
                        </tr>
                        <tr class="filterrow">
                            <th class="filtercell"><input type="text" id="FilterUserName" placeholder="<spring:message code="label.Filter" />" /></th>
                            <th class="filtercell"><input type="text" id="FilterEmail" placeholder="<spring:message code="label.Filter" />" /></th>
                            <th class="filtercell"><input type="text" id="FilterLabel" placeholder="<spring:message code="label.Filter" />" /></th>
                            <th></td>
                            <th class="filtercell">
                                <div class="btn-toolbar" style="margin: 0px; text-align: center">
                                    <div class="datefilter" style="float: left">
                                        <a class="btn btn-default" onclick="showOverlayMenu(this)">
                                            <spring:message code="label.from" />
                                            <span class="caret"></span>
                                        </a>
                                        <div class="overlaymenu hideme">
                                            <input type="hidden" id="metafilterfrom" class="hiddendate" value="" />
											<div id="metafilterfromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
                                        </div>
                                    </div>
                                    <div class="datefilter" style="float: left">
                                        <a class="btn btn-default" onclick="showOverlayMenu(this)" >
                                             <spring:message code="label.to" />
                                             <span class="caret"></span>
                                        </a>
                                         <div class="overlaymenu hideme">
                                             <input type="hidden" id="metafilterto" class="hiddendate" value="" />
                                             <div id="metafiltertodiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
                                        </div>
                                    </div>
                                </div>
                            </th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody id="AuthorisedUsersTableBody">

                    </tbody>
                    <tfoot id="NoAuthorisedUser">
                        <tr>
                            <td colspan="6" style="padding: 10px">
                                <span class="glyphicon glyphicon-alert" style="margin-right: 10px;" aria-hidden="true"></span>
                                <spring:message code="info.NoAuthorisedUserFound" />
                            </td>
                        </tr>
                    </tfoot>
                </table>

                <button onclick="showAddUserDialog(false,true)" class="btn btn-primary"><spring:message code="label.AddUser" /></button>

            </div>
		</div>
		
		<c:if test="${error != null}">
			<script type="text/javascript">
				showError('${error}');
			</script>
		</c:if>
	</div>

	<%@ include file="../footer.jsp" %>

	<div class="modal" id="confirm-delete-user-dialog" data-backdrop="static">
        <div class="modal-dialog">
        <div class="modal-content">
        <div class="modal-header"><spring:message code="label.ConfirmDeletion" /></div>
          <div class="modal-body" style="overflow-y: auto">
            <spring:message code="info.DeleteAuthorisedUser" />
          </div>
          <div class="modal-footer">
            <a class="btn btn-primary" onclick="deleteAuthorisedUser()"><spring:message code="label.OK" /></a>
            <a class="btn btn-default" onclick="$('#confirm-delete-user-dialog').modal('hide');"><spring:message code="label.Cancel" /></a>
          </div>
         </div>
         </div>
    </div>

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
