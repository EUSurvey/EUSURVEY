<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.ec.survey.model.administration.GlobalPrivilege" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Organisations" /></title>
	<%@ include file="../includes.jsp" %>
	<script type="text/javascript" src="${contextpath}/resources/js/jquery.stickytableheaders.js?version=<%@include file="../version.txt" %>"></script>

	<style>
	    h1 {
	        font-size: 18px !important;
	        margin-top: 30px !important;
	    }

	    #OrganisationManagersTable td, #AuthorisedUsersTable td {
            vertical-align: middle;
	    }
	</style>

	<script type="text/javascript">
	    var noEmptySearch = '<spring:message code="label.NoEmptySearch" />';

		$(document).ready(function() {
			$("#administration-menu-tab").addClass("active");
			$("#organisations-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
			
			$('[data-toggle="tooltip"]').tooltip();
			
			const currentyear = new Date().getFullYear();
			for (let i = currentyear; i > currentyear - 5; i--) {
				if (i < 2024) break;
				var o = document.createElement("option");
				$(o).append(i);
				$('#year').append(o);
			}
			
			$('#year').val(currentyear);

			$.ajax({type: "GET",
				url: contextpath + "/utils/Organisations",
				async: false,
			    success :function(result)
			    {		    	
			    	$.each(result.dgs, function(key, data){
			    		var option = document.createElement("option");
			    		$(option).attr("value", key).append(data);
			    		$('#organisation-dgs').append(option);
			    	});	
			    	
			    	$.each(result.executiveAgencies, function(key, data){
			    		var option = document.createElement("option");
			    		$(option).attr("value", key).append(data);
			    		$('#organisation-aex').append(option);
			    	});	
			    	
			    	$.each(result.otherEUIs, function(key, data){
			    		var option = document.createElement("option");
			    		$(option).attr("value", key).append(data);
			    		$('#organisation-euis').append(option);
			    	});	
			    	
			    	$.each(result.nonEUIs, function(key, data){
			    		var option = document.createElement("option");
			    		$(option).attr("value", key).append(data);
			    		$('#organisation-noneuis').append(option);
			    	});

			    	<c:if test="${organisation != null}">
			    	    $('#organisation').val('${organisation}');
			    	    loadOrganisation();
			    	</c:if>
			    }
			 });
		})
		
		function loadOrganisation() {
			
			if ( $('#organisation').val().length == 0) {
				$('#downloadOrganisationReport').attr("disabled", "disabled");
				$('#totalSurveys').text("");
		    	$('#totalContributions').text("");
		    	$('#surveysThisYear').text("");
		    	$('#surveysLastYear').text("");
		    	$('#contributionsThisYear').text("");
		    	$('#contributionsLastYear').text("");
		    	$('#organisation-content').hide();
		    	return;
			}
			
			$('#busydialog').modal('show');
			$.ajax({type: "GET",
				url: contextpath + "/administration/organisation/" + $('#organisation').val(),
				dataType: 'json',
				success :function(result)
			    {	
			    	$('#totalSurveys').text(result.allSurveys);
			    	$('#totalContributions').text(result.allContributions);
			    	$('#surveysThisYear').text(result.surveysThisYear);
			    	$('#surveysLastYear').text(result.surveysLastYear);
			    	$('#contributionsThisYear').text(result.contributionsThisYear);
			    	$('#contributionsLastYear').text(result.contributionsLastYear);
			    	$('#busydialog').modal('hide');
			    	$('#downloadOrganisationReport').removeAttr("disabled");
			    	$('#organisation-content').show();

			    	if (result.managers != null) {
			    	    $('#NoOrganisationManager').hide();
			    	    $('#OrganisationManagersTable').empty();

			    	    for (const property in result.managers) {
			    	        let tr = document.createElement("tr");
			    	        let td = document.createElement("td");
			    	        $(td).append(result.managers[property]);
			    	        $(tr).append(td);

			    	        td = document.createElement("td");
			    	        let button = document.createElement("button");
			    	        $(button).addClass("btn btn-link").html("<span class='glyphicon glyphicon-remove'></span>");
			    	        $(button).click(function() {
			    	            managerIdToDelete = property;
			    	            $('#confirm-delete-manager-dialog').modal('show');
			    	        });
			    	        $(td).css("width", "50px").append(button);
			    	        $(tr).append(td);

			    	        $('#OrganisationManagersTable').append(tr);
			    	    }

			    	    $('#OrganisationManagersTable').show();
			    	} else {
			    	    $('#NoOrganisationManager').show();
			    	    $('#OrganisationManagersTable').hide();
			    	}

			    	if (result.whitelist != null) {
                        $('#NoAuthorisedUser').hide();
                        $('#AuthorisedUsersTable').empty();

                        for (const property in result.whitelist) {
                            let tr = document.createElement("tr");
                            let td = document.createElement("td");
                            $(td).append(result.whitelist[property]);
                            $(tr).append(td);

                            td = document.createElement("td");
                            let button = document.createElement("button");
                            $(button).addClass("btn btn-link").html("<span class='glyphicon glyphicon-remove'></span>");
                            $(button).click(function() {
                                userIdToDelete = property;
                                $('#confirm-delete-user-dialog').modal('show');
                            });
                            $(td).css("width", "50px").append(button);
                            $(tr).append(td);

                            $('#AuthorisedUsersTable').append(tr);
                        }

                        $('#AuthorisedUsersTable').show();
                    } else {
                        $('#NoAuthorisedUser').show();
                        $('#AuthorisedUsersTable').hide();
                    }

			    	if ($('#organisation').val() == "all") {
			    	   $('#organisation-notall').hide();
			    	} else {
			    	    $('#organisation-notall').show();
			    	}
			    }, error: function(e) {
					console.log(e);
					$('#busydialog').modal('hide');
				}
			 });
		}

		function downloadOrganisationReport() {
			$('#monthError').hide();
			if ($('#organisation').val().length == 0) return;
			
			var start = parseInt($('#month').val());
			var end = parseInt($('#monthEnd').val());
			
			if (end > 0 && end < start) {
				$('#monthError').show();
			}
			
			window.location = "${contextpath}/administration/organisationreport/" + $('#organisation').val() + "/" + $('#format').val() + "/" + $('#year').val() + "/" + start + "/" + end + "?min=" + $('#minpublished').val();
		}
		
		function checkFormat() {
			if ($('#format').val() == "JSON") {
				$('#divMinPublishedSurveys').show();
				$('#divMonthEnd').show();
			} else {
				$('#divMinPublishedSurveys').hide();
				$('#divMonthEnd').hide();
			}
		}

		let addToOrganisationMode = "manager";
		function addOrganisationUser(login, ecas) {
		    if (addToOrganisationMode === "manager") {
		        addOrganisationManager(login, ecas);
		    } else {
		        addAuthorisedUser(login, ecas);
		    }
		}

		function addOrganisationManager(login, ecas) {
		    $("#busydialog").modal('show');

		    $.ajax({
                type: "POST",
                data: {organisation: $('#organisation').val(), login: login, ecas: ecas},
                url: contextpath + "/administration/addOrganisationManager",
                async: false,
                cache: false,
                beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
                success: function(data)
                {
                    if (data) {
                        showSuccess("<spring:message code="info.OrganisationManagerAdded" />");
                        loadOrganisation();
                    } else {
                        showError("<spring:message code="error.OrganisationManagerExists" />");
                    }
                    $("#busydialog").modal('hide');
                },
                error: function(e)
                {
                    console.log(e)
                    showError("<spring:message code="error.OperationFailed" />");
                    $("#busydialog").modal('hide');
                }
            });
		}

		function addAuthorisedUser(login, ecas) {
            $("#busydialog").modal('show');

            $.ajax({
                type: "POST",
                data: {organisation: $('#organisation').val(), login: login, ecas: ecas},
                url: contextpath + "/administration/addAuthorisedUser",
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
                    showError("<spring:message code="error.OperationFailed" />");
                    $("#busydialog").modal('hide');
                }
            });
        }

		let managerIdToDelete = 0;
		let userIdToDelete = 0;

		function deleteOrganisationManager() {
            $("#busydialog").modal('show');

            $.ajax({
                type: "POST",
                data: {organisation: $('#organisation').val(), id: managerIdToDelete},
                url: contextpath + "/administration/deleteOrganisationManager",
                async: false,
                cache: false,
                beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
                success: function(data)
                {
                    if (data) {
                        showSuccess("<spring:message code="info.OrganisationManagerRemoved" />");
                        loadOrganisation();
                    } else {
                        showError("<spring:message code="error.OrganisationManagerNotFound" />");
                    }
                    $("#busydialog").modal('hide');
                    $('#confirm-delete-manager-dialog').modal('hide');
                },
                error: function(e)
                {
                    console.log(e)
                    showError("<spring:message code="error.OperationFailed" />");
                    $("#busydialog").modal('hide');
                    $('#confirm-delete-manager-dialog').modal('hide');
                }
            });
        }

        function deleteAuthorisedUser() {
            $("#busydialog").modal('show');

            $.ajax({
                type: "POST",
                data: {organisation: $('#organisation').val(), id: userIdToDelete},
                url: contextpath + "/administration/deleteAuthorisedUser",
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
	</script>		
</head>
<body>
	<div class="page-wrap">
	<%@ include file="../header.jsp" %>
	<%@ include file="../menu.jsp" %>
	<%@ include file="adminmenu.jsp" %>
	<%@ include file="../management/access-modals.jsp" %>
	<%@ include file="../management/access-general-js.jsp" %>
		
	<div class="page1024" style="margin-bottom: 0px;">				
		<div style="width: 800px; margin-left: auto; margin-right: auto; margin-top: 120px; margin-bottom: 100px;">
			<h1><spring:message code="label.Organisation" /></h1>
			<select class="form-control" id="organisation" style="width: auto; width: 100%;" onchange="loadOrganisation()">
				<option></option>	
				<option value="all"><spring:message code="label.All" /></option>						
			  	<optgroup id="organisation-dgs" label="<spring:message code="label.EuropeanCommission" />: <spring:message code="label.DGsAndServices" />">
			    </optgroup>
			
			    <optgroup id="organisation-aex" label="<spring:message code="label.EuropeanCommission" />: <spring:message code="label.ExecutiveAgencies" />">
			    </optgroup>
			    
			    <optgroup id="organisation-euis" label="<spring:message code="label.OtherEUIs" />">
			    </optgroup>
			    
			    <optgroup id="organisation-noneuis" label="<spring:message code="label.NonEUIentities" />">
				</optgroup>
			</select>

			<div id="organisation-content" style="display: none">

			    <div id="organisation-notall">

                    <h1><spring:message code="label.OrganisationManager" /></h1>

                    <div id="NoOrganisationManager" class="alert alert-warning">
                        <span class="glyphicon glyphicon-alert" style="margin-right: 10px;" aria-hidden="true"></span>
                        <spring:message code="info.NoOrganisationManager" />
                    </div>

                    <table id="OrganisationManagersTable" class="table table-condensed table-bordered">

                    </table>

                    <button onclick="addToOrganisationMode = 'manager'; showAddUserDialog(false,true)" class="btn btn-primary"><spring:message code="label.AssignAManager" /></button>

                    <h1><spring:message code="label.AuthorisedUsers" /></h1>

                    <div id="NoAuthorisedUser" class="alert alert-warning">
                        <span class="glyphicon glyphicon-alert" style="margin-right: 10px;" aria-hidden="true"></span>
                        <spring:message code="info.NoAuthorisedUser" />
                    </div>

                    <table id="AuthorisedUsersTable" class="table table-condensed table-bordered">

                    </table>

                    <button onclick="addToOrganisationMode = 'user'; showAddUserDialog(false,true)" class="btn btn-primary"><spring:message code="label.AddUser" /></button>

                </div>

                <h1><spring:message code="label.Monitoring" /></h1>

                <table class="table table-bordered" style="margin-top: 10px;">
                    <tr>
                        <td>Total number of surveys:</td>
                        <td id="totalSurveys"></td>
                        <td>Total number of contributions:</td>
                        <td id="totalContributions"></td>
                    </tr>
                    <tr>
                        <td>Number of surveys published this year:</td>
                        <td id="surveysThisYear"></td>
                        <td>Number of contributions received this year:</td>
                        <td id="contributionsThisYear"></td>
                    </tr>
                    <tr>
                        <td>Number of surveys published last year:</td>
                        <td id="surveysLastYear"></td>
                        <td>Number of contributions received last year:</td>
                        <td id="contributionsLastYear"></td>
                    </tr>
                </table>

                <div style="background-color: #ddd; padding: 10px; border-radius: 5px;">
                    <b><spring:message code="label.Report" /></b><br /><br />

                    <div class="form-group" style="float: left; margin-right: 10px;">
                        <label for="format"><spring:message code="label.Format" /></label>
                        <select class="form-control" id="format" style="width: auto" onchange="checkFormat()">
                            <option selected="selected">CSV</option>
                            <option>JSON</option>
                        </select>
                    </div>

                    <div class="form-group" style="float: left; margin-right: 10px;">
                        <label for="year"><spring:message code="label.Year" /></label>
                        <select class="form-control" id="year" style="width: auto"></select>
                    </div>

                    <div class="form-group" style="float: left; margin-right: 5px;">
                        <label for="month"><spring:message code="label.Month" /></label>
                        <select class="form-control" id="month" style="width: auto">
                            <option value="0"></option>
                            <option>1</option>
                            <option>2</option>
                            <option>3</option>
                            <option>4</option>
                            <option>5</option>
                            <option>6</option>
                            <option>7</option>
                            <option>8</option>
                            <option>9</option>
                            <option>10</option>
                            <option>11</option>
                            <option>12</option>
                        </select>
                    </div>

                    <div class="form-group" id="divMonthEnd" style="float: left; margin-right: 10px; padding-top: 25px; display: none;">
                        <span>-</span>
                        <select class="form-control" id="monthEnd" style="width: auto; display: inline-block;">
                            <option value="0"></option>
                            <option>1</option>
                            <option>2</option>
                            <option>3</option>
                            <option>4</option>
                            <option>5</option>
                            <option>6</option>
                            <option>7</option>
                            <option>8</option>
                            <option>9</option>
                            <option>10</option>
                            <option>11</option>
                            <option>12</option>
                        </select>
                    </div>

                    <div class="form-group" id="divMinPublishedSurveys" style="float: left; margin-right: 10px; display: none;">
                        <label for="month"><spring:message code="label.MinPublishedSurveys" /></label>
                        <input type="number" id="minpublished" class="form-control" />
                    </div>

                    <div style="clear: both"></div>

                    <div id="monthError" class="validation-error" style="display: none"><spring:message code="error.EndMonthSmaller" /></div>

                    <div style="padding-top: 0px; text-align: right;">
                        <button id="downloadOrganisationReport" onclick="downloadOrganisationReport()" class="btn btn-primary" disabled="disabled"><spring:message code="label.GenerateReport" /></button>
                    </div>
                </div>
            </div>
		</div>
	</div>	
		
	</div>
		
	<%@ include file="../footer.jsp" %>

    <div class="modal" id="confirm-delete-manager-dialog" data-backdrop="static">
        <div class="modal-dialog">
        <div class="modal-content">
        <div class="modal-header"><spring:message code="label.ConfirmDeletion" /></div>
          <div class="modal-body" style="overflow-y: auto">
            <spring:message code="info.DeleteOrganisationManager" />
          </div>
          <div class="modal-footer">
            <a id="btnConfirmDeleteElementFromEdit" class="btn btn-primary" onclick="deleteOrganisationManager()"><spring:message code="label.OK" /></a>
            <a id="btnCancelDeleteElementFromEdit" class="btn btn-default" onclick="$('#confirm-delete-manager-dialog').modal('hide');"><spring:message code="label.Cancel" /></a>
          </div>
         </div>
         </div>
    </div>

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
