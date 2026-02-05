<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<style>
    #tblPrivilegesFromAccess {
	    width: 100%;
	}

    #tblPrivilegesFromAccess td {
        text-align: center;
        vertical-align: middle;
    }
</style>

<div class="modal" id="bulk-edit-wizard" data-backdrop="static">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <spring:message code="label.BulkChange" />
                -
                <span id="bulk-header-step1" style="display: none"><spring:message code="label.Step" /> 1 <spring:message code="label.of" /> 4: <spring:message code="label.ChooseOperation" /></span>
                <span id="bulk-header-step2" style="display: none"><spring:message code="label.Step" /> 2 <spring:message code="label.of" /> 4: <spring:message code="label.SelectTheSurveys" /></span>
                <span id="bulk-header-step3" style="display: none"><spring:message code="label.Step" /> 3 <spring:message code="label.of" /> 4: <spring:message code="label.OperationDetails" /></span>
                <span id="bulk-header-step4" style="display: none"><spring:message code="label.Step" /> 4 <spring:message code="label.of" /> 4: <spring:message code="label.Confirmation" /></span>
            </div>
            <div class="modal-body" style="min-width: 800px; min-height: 600px;">
                <div id="bulk-edit-step-1">
                    <div style="padding-left: 30px;">
                        <div class="radio">
                          <label style="font-size: 14px">
                            <input type="radio" name="rdoOperation" id="rdoOperationAddRemoveUsers" value="1" checked>
                            <spring:message code="label.AddRemovePrivilegedUsers" />
                          </label>
                        </div>
                        <div class="radio">
                          <label style="font-size: 14px">
                            <input type="radio" name="rdoOperation" id="rdoOperationAddRemoveTags" value="2">
                            <spring:message code="label.AddRemoveTags" />
                          </label>
                        </div>
                        <div class="radio">
                          <label style="font-size: 14px">
                            <input type="radio" name="rdoOperation" id="rdoOperationPublishUnpublish" value="3">
                            <spring:message code="label.PublishUnpublish" />
                          </label>
                        </div>
                         <div class="radio">
                          <label style="font-size: 14px">
                            <input type="radio" name="rdoOperation" id="rdoOperationChangeOwner" value="4">
                             <spring:message code="label.ChangeOwner" />
                          </label>
                        </div>
                         <div class="radio">
                          <label style="font-size: 14px">
                            <input type="radio" name="rdoOperation" id="rdoOperationDeleteSurveys" value="5">
                            <spring:message code="label.DeleteSurveys" />
                          </label>
                        </div>
                    </div>
                </div>

                <div id="bulk-edit-step-2">
                     <div style="max-height: 570px; overflow: auto">
                     <div id="bulkEditSurveysTableLoading" style="padding: 30px;">
                        <img alt="wait animation" src="${contextpath}/resources/images/ajax-loader.gif" />
                     </div>
                     <div id="infoOnlyOwnSurveys" style="margin-bottom: 10px;"><spring:message code="info.OnlyOwnSurveys" /></div>
                     <div id="infoOnlyUnpublishedSurveys" style="margin-bottom: 10px;"><spring:message code="info.OnlyUnpublishedSurveys" /></div>
                     <table class="table table-bordered table-striped" style="display: none" id="bulkEditSurveysTable">
                        <thead>
                            <tr>
                                <th><input id="chkToggleBulkEditSurveys" type="checkbox" onchange="toggleBulkEditSurveys()" /></th>
                                <th><b><spring:message code="label.Alias" /></b></th>
                                <th><b><spring:message code="label.Title" /></b></th>
                                <th><b><spring:message code="label.Owner" /></b></th>
                                <th><b><spring:message code="label.Type" /></b></th>
                                <th><b><spring:message code="label.Status" /></b></th>
                                <th><b><spring:message code="label.FirstPublished" /></b></th>
                                <th><b><spring:message code="label.Replies" /></b></th>
                            </tr>
                        </thead>
                        <tbody id="bulkEditSurveysTableBody">

                        </tbody>
                     </table>
                     </div>
                </div>

                <div id="bulk-edit-step-3-addremoveusers">
                    <div style="padding: 10px;">
                        <div id="addRemoveUsersInfo" style="font-weight: bold; margin-bottom: 20px;"></div>
                        <%@ include file="../management/access-general.jsp" %>

                         <div style="padding: 30px;">
                            <div class="radio">
                              <label style="font-size: 14px">
                                <input type="radio" name="rdoPrivilegeAction" id="rdoPrivilegeActionAdd" value="1" checked>
                                 <spring:message code="label.AddSelected" />
                              </label>
                            </div>
                            <div class="radio">
                              <label style="font-size: 14px">
                                <input type="radio" name="rdoPrivilegeAction" id="rdoPrivilegeActionReplace" value="2">
                                 <spring:message code="label.ReplaceSelected" />
                              </label>
                            </div>
                            <div class="radio">
                              <label style="font-size: 14px">
                                <input type="radio" name="rdoPrivilegeAction" id="rdoPrivilegeActionRemove" value="3">
                                 <spring:message code="label.RemoveSelectedPrivileges" />
                              </label>
                            </div>
                         </div>

                    </div>
                </div>

                <div id="bulk-edit-step-3-addremovetags">
                    <div style="padding-left: 30px;">
                        <div id="addRemoveTagsInfo" style="font-weight: bold; margin-bottom: 20px;"></div>

                        <b><spring:message code="label.Tags" /></b><br />
                        <input id="bulktags" autocomplete="off" type="text" maxlength="16" onkeyup="checkTagKeyUp(event)" class="form-control freetext max255" style="margin-top: 5px; display: inline-block;" />
                        <span id="bulkEditTagsLoading" style="margin-left: 5px; display: none;">
                            <img alt="wait animation" src="${contextpath}/resources/images/ajax-loader.gif" />
                        </span>

                        <div id="bulktagserrors" style="margin-top: 5px"></div>

                        <input type="hidden" id="txtBulkTags" name="tags" value="" />
                        <div id="BulkTags" style="margin-top: 5px;">
                            <script>
                                <c:forEach items="${filter.tags}" var="tag">
                                    addTagBulk("${tag}");
                                </c:forEach>
                            </script>
                        </div>

                        <div style="margin-top: 20px;">
                            <div class="radio">
                              <label style="font-size: 14px">
                                <input type="radio" name="rdoTagsDetails" id="rdoTagsDetailsAdd" checked>
                                <spring:message code="label.AddTagsToExisting" />
                              </label>
                            </div>
                            <div class="radio">
                              <label style="font-size: 14px">
                                <input type="radio" name="rdoTagsDetails" id="rdoTagsDetailsRemove">
                                <spring:message code="label.RemoveTagsFromExisting" />
                              </label>
                            </div>
                            <div class="radio">
                              <label style="font-size: 14px">
                                <input type="radio" name="rdoTagsDetails" id="rdoTagsDetailsReplace">
                                <spring:message code="label.ReplaceAllExistingTags" />
                              </label>
                            </div>
                        </div>
                    </div>
                </div>

                <div id="bulk-edit-step-3-publishunpublish">
                    <div style="padding-left: 30px;">
                        <div id="publishSurveysInfo" style="font-weight: bold; margin-bottom: 20px;"></div>
                        <div>
                            <div class="radio">
                              <label style="font-size: 14px">
                                <input type="radio" name="rdoPublishDetails" id="rdoPublishDetailsUnpublishAll" checked>
                                <spring:message code="label.UnpublishAll" />
                              </label>
                            </div>
                            <div class="radio">
                              <label style="font-size: 14px">
                                <input type="radio" name="rdoPublishDetails" id="rdoPublishDetailsPublishNoPendingChanges">
                                <spring:message code="label.PublishNoPendingChanges" />
                              </label>
                            </div>
                            <div class="radio">
                              <label style="font-size: 14px">
                                <input type="radio" name="rdoPublishDetails" id="rdoPublishDetailsPublishApply">
                                <spring:message code="label.PublishAndApplyPendingChanges" />
                              </label>
                            </div>
                        </div>
                    </div>
                </div>

                <div id="bulk-edit-step-3-changeowner">
                    <div style="padding-left: 30px;">
                        <spring:message code="info.OnlyOwnSurveys" /><br /><br />
                        <button class="btn btn-primary" onclick="showChangeOwnerDialog()"><spring:message code="label.SelectNewOwner" /></button><br /><br />
                        <spring:message code="label.NewOwner" />: <span id="newOwner"></span><br /><br />

                        <div id="transferOwnershipInfo" style="font-weight: bold;"></div>
                    </div>
                </div>

                <div id="bulk-edit-step-3-deletesurveys">
                    <div style="padding-left: 30px;">
                        <div id="deleteSurveysInfo" style="font-weight: bold; margin-bottom: 20px;"></div>
                        <input type="checkbox" id="deleteSurveysSendEmail" class="check" /> <spring:message code="label.SendEmailToFormManagers" />
                    </div>
                </div>

                <div id="bulk-edit-step-4">
                    <div id="step4-wait-message" style="padding: 30px;">
                        <spring:message code="info.OperationStarted" /><br /><br />
                        <img style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
                    </div>
                    <div id="BulkEditResult" style="display: none; padding: 40px;"></div>
                    <div id="BulkEditError" style="display: none; color: #f00; padding: 40px;"><spring:message code="error.OperationFailed" /></div>
                </div>

            </div>
            <div class="modal-footer" style="text-align: right !important">
                <img class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />

                <a id="bulk-edit-cancel-button" class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>
                <a id="bulk-edit-close-button" class="btn btn-default" data-dismiss="modal"><spring:message code="label.Close" /></a>
                <a onclick="previousBulkEditStep();" id="bulkEditWizardBack" class="btn btn-default"><spring:message code="label.Back" /></a>
                <a onclick="nextBulkEditStep();" id="bulkEditWizardNext" class="btn btn-primary"><spring:message code="label.Next" /></a>
            </div>
        </div>
    </div>
</div>

<%@ include file="../management/change-ownership.jsp" %>
<%@ include file="../management/access-modals.jsp" %>

<script type="text/javascript">
    let selectedBulkEditWizardStep = 1;
    let showAllSurveysInBulkEditWizard = false;
    let newOwner = "";
    let addAsFormManager = false;
    let changeId = 0;

    function showBulkEditWizard(all) {
        selectedBulkEditWizardStep = 1;
        showAllSurveysInBulkEditWizard = all;
        newOwner = "";
        addAsFormManager = false;

        initBulkEditWizard(true);
        $("#bulk-edit-wizard").modal("show");
    }

    function getSelectedSurveysForBulkChange() {
        let surveys = [];
        $('#bulkEditSurveysTableBody').find("tr").each(function(){
            let checkbox = $(this).find("input[type=checkbox]").first();
            if ($(checkbox).is(":checked")) {
                surveys.push(parseInt($(checkbox).val()));
            }
        });
        return surveys;
    }

    function initBulkEditWizard(loadSurveys) {
        $('#bulk-header-step1').hide();
        $('#bulk-header-step2').hide();
        $('#bulk-header-step3').hide();
        $('#bulk-header-step4').hide();
        $('#bulk-edit-step-1').hide();
        $('#bulk-edit-step-2').hide();
        $('#bulk-edit-step-3-addremoveusers').hide();
        $('#bulk-edit-step-3-addremovetags').hide();
        $('#bulk-edit-step-3-publishunpublish').hide();
        $('#bulk-edit-step-3-changeowner').hide();
        $('#bulk-edit-step-3-deletesurveys').hide();
        $('#bulk-edit-step-4').hide();
        $('#bulkEditWizardNext').removeClass("disabled");
        $('#BulkEditResult').text("");
        $('#step4-wait-message').show();
        $('#bulk-edit-cancel-button').show();
        $('#bulk-edit-close-button').hide();

        if (selectedBulkEditWizardStep == 1) {
            $('#bulk-header-step1').show();
            $('#bulk-edit-step-1').show();
        } else if (selectedBulkEditWizardStep == 2) {
            if (loadSurveys) {
                initBulkEditStep2();
                $('#bulkEditWizardNext').addClass("disabled");
            }
            $('#bulk-header-step2').show();
            $('#bulk-edit-step-2').show();
        } else if (selectedBulkEditWizardStep == 3) {
            const surveyCount = getSelectedSurveysForBulkChange().length;
            $('#bulk-header-step3').show();
            if ($('#rdoOperationAddRemoveUsers').is(":checked")) {
                $('#addRemoveUsersInfo').text("<spring:message code="info.AddRemoveUsers" />".replace("[X]", surveyCount));
                $('#bulkEditWizardNext').addClass("disabled");
                $('#rdoPrivilegeActionAdd').prop("checked", "checked");
                $('#tblPrivilegesFromAccess').find("tbody").empty();
                $('#bulk-edit-step-3-addremoveusers').show();
            } else if ($('#rdoOperationAddRemoveTags').is(":checked")) {
                $("#addRemoveTagsInfo").text("<spring:message code="info.AddRemoveTags" />".replace("[X]", surveyCount));
                $("#txtBulkTags").val("");
                $("#BulkTags").empty();
                $('#bulk-edit-step-3-addremovetags').show();
            } else if ($('#rdoOperationPublishUnpublish').is(":checked")) {
                $("#publishSurveysInfo").text("<spring:message code="info.PublishUnpublish" />".replace("[X]", surveyCount));
                $('#bulk-edit-step-3-publishunpublish').show();
            } else if ($('#rdoOperationChangeOwner').is(":checked")) {
                $("#newOwner").text("-");
                $("#transferOwnershipInfo").text("<spring:message code="info.RequestTransferOwnership" />".replace("[X]", surveyCount));
                $('#bulkEditWizardNext').addClass("disabled");
                $('#bulk-edit-step-3-changeowner').show();
            } else if ($('#rdoOperationDeleteSurveys').is(":checked")) {
               $("#deleteSurveysInfo").text("<spring:message code="info.AboutToDeleteSurveys" />".replace("[X]", surveyCount));
               $('#bulk-edit-step-3-deletesurveys').show();
           }
        } else if (selectedBulkEditWizardStep == 4) {
            let data = null;
            let surveys = getSelectedSurveysForBulkChange();
            if ($('#rdoOperationAddRemoveUsers').is(":checked")) {
                let mode;
                if ($("#rdoPrivilegeActionAdd").is(":checked")) {
                    mode = "ADD";
                } else if ($("#rdoPrivilegeActionReplace").is(":checked")) {
                    mode = "REPLACE";
                } else if ($("#rdoPrivilegeActionRemove").is(":checked")) {
                    mode = "REMOVE";
                }

                const privileges = [];
                $('#tblPrivilegesFromAccess').find("tbody").find("tr").each(function(){
                    const p = {
                        name: $(this).find("td").first().text(),
                        type: $(this).attr("data-type"),
                        accessDraft: $($(this).find("td")[2]).attr("data-value"),
                        accessResults: $($(this).find("td")[3]).attr("data-value"),
                        formManagement: $($(this).find("td")[4]).attr("data-value"),
                        manageInvitations: $($(this).find("td")[5]).attr("data-value")
                    }
                    privileges.push(p);
                });

                data = {
                    operation: "ADDREMOVEUSERS",
                    mode: mode,
                    surveys: surveys,
                    privileges: privileges
                };

            } else if ($('#rdoOperationPublishUnpublish').is(":checked")) {
                let mode;
                if ($("#rdoPublishDetailsUnpublishAll").is(":checked")) {
                    mode = "UNPUBLISHALL";
                } else if ($("#rdoPublishDetailsPublishNoPendingChanges").is(":checked")) {
                    mode = "PUBLISHNOPENDINGCHANGES";
                } else if ($("#rdoPublishDetailsPublishApply").is(":checked")) {
                    mode = "PUBLISHAPPLYPENDINGCHANGES";
                }

                data = {
                    operation: "PUBLISHUNPUBLISH",
                    mode: mode,
                    surveys: surveys
                };
            } else if ($('#rdoOperationAddRemoveTags').is(":checked")) {
                let mode;
                if ($("#rdoTagsDetailsAdd").is(":checked")) {
                    mode = "ADD";
                } else if ($("#rdoTagsDetailsRemove").is(":checked")) {
                    mode = "REMOVE";
                } else if ($("#rdoTagsDetailsReplace").is(":checked")) {
                    mode = "REPLACE";
                }

                const tags = $("#txtBulkTags").val().split(";")

                data = {
                    operation: "ADDREMOVETAGS",
                    mode: mode,
                    surveys: surveys,
                    tags: tags
                };

            } else if ($('#rdoOperationChangeOwner').is(":checked")) {
                data = {
                    operation: "CHANGEOWNER",
                    newOwner: newOwner,
                    addAsFormManager: addAsFormManager,
                    surveys: surveys
                };
            } else if ($('#rdoOperationDeleteSurveys').is(":checked")) {
                 data = {
                     operation: "DELETESURVEYS",
                     sendEmails: $('#deleteSurveysSendEmail').is(":checked"),
                     surveys: surveys
                 };
             }

            $.ajax({type: "POST",
                url: contextpath + "/forms/bulkchange",
                data: JSON.stringify(data),
                contentType: "application/json; charset=utf-8",
                processData:false, //To avoid making query String
                async: false,
                beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
                error: function(result)
                {
                    var s = result.statusText.toLowerCase();
                    if (strStartsWith(s,"forbidden") || result.status == 403)
                    {
                        sessiontimeout = true;
                    } else if (strStartsWith(s,"networkerror"))
                    {
                        networkproblems = true;
                    } else {
                        unknownerror = true;
                    }
                    showError("<spring:message code="error.ExecutionFailed" />");
                },
                success: function(result)
                {
                    if (result < 0) {
                        showError("<spring:message code="error.ExecutionFailed" />");
                    } else {
                        changeId = result;
                        window.setTimeout("checkBulkChangeStatus()", 5000);
                    }
                }
            });

            $('#bulk-edit-cancel-button').hide();
            $('#bulk-edit-close-button').show();

            $('#bulk-header-step4').show();
            $('#bulk-edit-step-4').show();
        }

        if (selectedBulkEditWizardStep == 4) {
            $('#bulkEditWizardBack').hide();
            $('#bulkEditWizardNext').hide();
        } else if (selectedBulkEditWizardStep > 1) {
            $('#bulkEditWizardBack').show();
            $('#bulkEditWizardNext').show();
        } else {
            $('#bulkEditWizardBack').hide();
            $('#bulkEditWizardNext').show();
        }
    }

    function toggleBulkEditSurveys() {
        if ($("#chkToggleBulkEditSurveys").is(":checked")) {
            $('input[name="bulkEditSurvey"]').prop("checked", "checked");
            $('#bulkEditWizardNext').removeClass("disabled");
        } else {
            $('input[name="bulkEditSurvey"]').removeAttr("checked");
            $('#bulkEditWizardNext').addClass("disabled");
        }
    }

    function checkSurveySelection() {
        if (getSelectedSurveysForBulkChange().length > 0) {
            $('#bulkEditWizardNext').removeClass("disabled");
        } else {
            $('#bulkEditWizardNext').addClass("disabled");
        }
    }

    function checkUserSelection() {
        if ($('#tblPrivilegesFromAccess').find("tbody").find("tr").length > 0) {
            $('#bulkEditWizardNext').removeClass("disabled");
        } else {
            $('#bulkEditWizardNext').addClass("disabled");
        }
    }

    function initBulkEditStep2() {

        let onlyOwnSurveys = $('#rdoOperationChangeOwner').is(":checked");
        let onlyUnPublished = $('#rdoOperationDeleteSurveys').is(":checked");
        $('#bulkEditSurveysTableLoading').show();
        $('#bulkEditSurveysTable').hide();
        $('#bulkEditSurveysTableBody').empty();
        $("#chkToggleBulkEditSurveys").removeAttr("checked");

        let s = "page=1&rows=" + ((newPage-1) * 10);
        if (showAllSurveysInBulkEditWizard) {
            s = "page=1&rows=100";
        }

        if (onlyOwnSurveys) {
            $('#infoOnlyOwnSurveys').show();
        } else {
            $('#infoOnlyOwnSurveys').hide();
        }

        if (onlyUnPublished) {
            $('#infoOnlyUnpublishedSurveys').show();
        } else {
            $('#infoOnlyUnpublishedSurveys').hide();
        }

        $.ajax({
            type:'GET',
            url: "${contextpath}/forms/surveysjson",
            dataType: 'json',
            data: s,
            cache: false,
            success: function( list ) {
                for (var i = 0; i < list.length; i++ )
                {
                    if (onlyOwnSurveys && list[i].owner.id != ${USER.id}) {
                        continue;
                    }

                    if (onlyUnPublished && list[i].isActive && list[i].isPublished) {
                       continue;
                   }

                    let tr = document.createElement("tr");
                    let td = document.createElement("td");

                    let checkbox = document.createElement("input");
                    $(checkbox).attr("type", "checkbox").attr("name", "bulkEditSurvey").attr("onclick","checkSurveySelection()").val(list[i].id);
                    $(td).append(checkbox);
                    $(tr).append(td);

                    td = document.createElement("td");
                    $(td).append(list[i].shortname);
                    $(tr).append(td);

                    td = document.createElement("td");
                    $(td).append(list[i].title.stripHtml115());
                    $(tr).append(td);

                    td = document.createElement("td");
                    $(td).append(list[i].owner.name);
                    $(tr).append(td);

                    td = document.createElement("td");
                    if (list[i].isQuiz)
                    {
                       $(td).append("<spring:message code="label.Quiz" />");
                    } else if (list[i].isOPC)
                    {
                       $(td).append("<spring:message code="label.OPC" />");
                    } else if (list[i].isDelphi)
                    {
                         $(td).append("<spring:message code="label.Delphi" />");
                    }
                    else if (list[i].isECF)
                    {
                         $(td).append("<spring:message code="label.ECF" />");
                    }
                    else if (list[i].isSelfAssessment)
                    {
                         $(td).append("<spring:message code="label.SelfAssessment" />");
                    }
                    else if (list[i].isEVote)
                    {
                         $(td).append("<spring:message code="label.eVote" />");
                    }
                    else {
                         $(td).append("<spring:message code="label.StandardSurvey" />");
                    }
                    $(tr).append(td);

                    td = document.createElement("td");
                    if (list[i].isActive && list[i].isPublished)
                    {
                         $(td).append("<spring:message code="label.Published" />");
                    } else {
                         $(td).append("<spring:message code="label.Unpublished" />");
                    }
                    $(tr).append(td);

                    td = document.createElement("td");
                    $(td).append(list[i].niceFirstPublished);
                    $(tr).append(td);

                    td = document.createElement("td");
                    $(td).append(list[i].numberOfAnswerSetsPublished);
                    $(tr).append(td);

                    $('#bulkEditSurveysTableBody').append(tr);
                }

                $('#bulkEditSurveysTableLoading').hide();
                $('#bulkEditSurveysTable').show();
            }
        });
    }

    function nextBulkEditStep() {
        if ($("#bulkEditWizardNext").hasClass("disabled")) {
            return;
        }

        selectedBulkEditWizardStep++;
        initBulkEditWizard(true);
    }

    function previousBulkEditStep() {
        selectedBulkEditWizardStep--;
        initBulkEditWizard(false);
    }

    function changeOwner() {
        if ($("#btnOkChangeOwnerFromAccess").attr("disabled") == "disabled") {
            return;
        }
        if ($("#search-results").find(".success").length == 0)
        {
            $("#search-results-none").show();
            return;
        }
        $("#search-results-none").hide();

        newOwner = $("#search-results").find(".success").first().attr("id");
        let name = $("#search-results").find(".success").find("td")[2].innerText + " " + $("#search-results").find(".success").find("td")[3].innerText +  " (" + $("#search-results").find(".success").find("td")[0].innerText + ")";
        addAsFormManager = $("#add-as-form-manager").is(":checked");

        $("#newOwner").text(name);
        $('#change-owner-dialog').modal("hide");
        $('#bulkEditWizardNext').removeClass("disabled");
    }

    function changeOwnerByEmail() {
        if ($("#btnOkChangeOwnerFromAccess").attr("disabled") == "disabled") {
            return;
        }

        newOwner = $("#change-owner-email").val();
        addAsFormManager = $("#add-as-form-manager").is(":checked");
        $('#change-owner-dialog').modal("hide");
        $('#bulkEditWizardNext').removeClass("disabled");
    }

    function checkBulkChangeStatus()
    {
        $.ajax({
            url: "${contextpath}/forms/checkBulkChange/" + changeId,
            dataType: "json",
            cache: false,
            success: function(data)
            {
                if (data.error) {
                    $('#BulkEditError').show();
                    $('#step4-wait-message').hide();
                } else if (data.finished) {
                    if ($('#rdoOperationAddRemoveUsers').is(":checked")) {
                        $('#BulkEditResult').text("<spring:message code="bulkresultaddremoveuser" />".replace("[X]", data.successes.length));
                    } else if ($('#rdoOperationAddRemoveTags').is(":checked")) {
                        $('#BulkEditResult').text("<spring:message code="bulkresultaddremovetags" />".replace("[X]", data.successes.length));
                    } else if ($('#rdoOperationPublishUnpublish').is(":checked")) {
                        if ($('#rdoPublishDetailsUnpublishAll').is(":checked")) {
                            $('#BulkEditResult').text("<spring:message code="bulkresultunpublish" />".replace("[X]", data.successes.length));
                        } else {
                            $('#BulkEditResult').text("<spring:message code="bulkresultpublish" />".replace("[X]", data.successes.length));
                        }
                    } else if ($('#rdoOperationChangeOwner').is(":checked")) {
                        $('#BulkEditResult').text("<spring:message code="bulkresultchangeowner" />".replace("[X]", data.successes.length));
                    } else if ($('#rdoOperationDeleteSurveys').is(":checked")) {
                        $('#BulkEditResult').text("<spring:message code="bulkresultdeletesurveys" />".replace("[X]", data.successes.length));
                    }

                    if (data.fails.length > 0) {
                        $('#BulkEditResult').append("<br />" + "<spring:message code="bulkresultfails" />".replace("[X]", data.fails.length))
                    }

                    if (data.skippedAliases.length > 0) {
                        $('#BulkEditResult').append("<br /><br /><b><spring:message code="label.SkippedSurveys" />:</b>");
                        for (const s of data.skippedAliases) {
                            $('#BulkEditResult').append("<br />" + s);
                        }
                    }

                    if (data.failsAliases.length > 0) {
                        $('#BulkEditResult').append("<br /><br /><b><spring:message code="label.SurveysWithErrors" />:</b>");
                        for (const s of data.failsAliases) {
                            $('#BulkEditResult').append("<br />" + s);
                        }
                    }

                    $('#BulkEditResult').show();
                    $('#step4-wait-message').hide();
                } else {
                    window.setTimeout("checkBulkChangeStatus()", 5000);
                }
            },
            error: function(result)
            {
                 $('#BulkEditError').show();
                 $('#step4-wait-message').hide();
            }
        });
    }

    function updatePrivilegeInBulkChangeDetails(img, value) {
        switch(value) {
            case 0:
                $(img).attr("src", "${contextpath}/resources/images/bullet_ball_glass_red.png").attr("alt","none");
                $(img).closest("td").attr("data-value", "0");
                break;
            case 1:
                $(img).attr("src", "${contextpath}/resources/images/bullet_ball_glass_yellow.png").attr("alt","read");
                $(img).closest("td").attr("data-value", "1");
                break;
            case 2:
                $(img).attr("src", "${contextpath}/resources/images/bullet_ball_glass_green.png").attr("alt","read/write");
                $(img).closest("td").attr("data-value", "2");
                break;
        }
    }

    function addEntryToBulkChangeDetails(name, isUser, isEcas) {
        const tr = document.createElement("tr");
        let td = document.createElement("td");

        if (isUser && !isEcas) {
            $(td).append("<span class='hideme externaluser'></span>");
        }

        $(td).append(name);
        $(tr).append(td);

        td = document.createElement("td");
        let img = document.createElement("img");

        if (isUser) {
            $(img).attr("data-toggle", "tooltip").attr("title", "<spring:message code="label.User"/>");
            $(img).attr("src", "${contextpath}/resources/images/user.png").attr("alt","User");
            $(tr).attr("data-type", "User");
        } else {
            $(img).attr("data-toggle", "tooltip").attr("title", "<spring:message code="label.Department"/>");
            $(img).attr("src", "${contextpath}/resources/images/group.png").attr("alt","Department");
            $(tr).attr("data-type", "Department");
        };

        $(td).append(img);
        $(tr).append(td);

        td = document.createElement("td");
        img = document.createElement("img");
        $(img).attr("data-toggle", "tooltip").attr("title", "<spring:message code="label.EditRights"/>").addClass("roleBulletRed").attr("onclick", "changePrivilege('AccessDraft', this)").attr("src", "${contextpath}/resources/images/bullet_ball_glass_red.png").attr("alt","none");
        $(td).append(img);
        $(tr).append(td);

        td = document.createElement("td");
        img = document.createElement("img");
        $(img).attr("data-toggle", "tooltip").attr("title", "<spring:message code="label.EditRights"/>").addClass("roleBulletRed").attr("onclick", "changePrivilege('AccessResults', this)").attr("src", "${contextpath}/resources/images/bullet_ball_glass_red.png").attr("alt","none");
        $(td).append(img);
        $(tr).append(td);

        td = document.createElement("td");
        img = document.createElement("img");
        $(img).attr("data-toggle", "tooltip").attr("title", "<spring:message code="label.EditRights"/>").addClass("roleBulletRed").attr("onclick", "changePrivilege('FormManagement', this)").attr("src", "${contextpath}/resources/images/bullet_ball_glass_red.png").attr("alt","none");
        $(td).append(img);
        $(tr).append(td);

        td = document.createElement("td");
        img = document.createElement("img");
        $(img).attr("data-toggle", "tooltip").attr("title", "<spring:message code="label.EditRights"/>").addClass("roleBulletRed").attr("onclick", "changePrivilege('ManageInvitations', this)").attr("src", "${contextpath}/resources/images/bullet_ball_glass_red.png").attr("alt","none");
        $(td).append(img);
        $(tr).append(td);

        td = document.createElement("td");
        const b = document.createElement("a");
        $(b).attr("data-toggle", "tooltip").attr("title", "<spring:message code="label.Remove"/>").addClass("iconbutton").attr("onclick", "removePrivilege(this)").append("<span class='glyphicon glyphicon-remove'></span>");
        $(td).append(b);
        $(tr).append(td);

        $('#tblPrivilegesFromAccess').find("tbody").append(tr);
        $('#add-user-dialog').modal("hide");
        checkUserSelection();
    }

    function removePrivilege(button) {
        $(button).closest("tr").remove();
        checkUserSelection();
    }

    $(function() {
        $("#bulktags").autocomplete({
            autoFocus: true,
            appendTo: $("#bulktags").parent(),
            source: "${contextpath}/forms/tags?createNewTag=true",
            search: function( event, ui ) {
                let term = event.target.value;
                const reg = /^[a-zA-Z0-9-_]+$/;

                if( !reg.test( term ) ) {
                    if ($("#bulktagserrors").find(".validation-error").length == 0)
                    {
                        $("#bulktagserrors").append("<div class='validation-error'>" + tagsText + "</div>");
                    }
                    event.preventDefault();
                    $("#bulktags").autocomplete("close");
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
                let tag = ui.item.value;

                // a tag needs at least 3 characters
                if (tag.length > 2) {
                  addTagBulk(tag);
                  $("#bulktags").val("");
                }

                $("#bulkEditTagsLoading").hide();
                return false;
            }
        });

    });

	function checkTagKeyUp(event) {
	    if (event.key === "Enter") return;
	    if(event.target.value.length > 0) {
            //_properties.tagsLoading(true);
        }
	}

    function addTagBulk(tag) {
        if (tag.indexOf(" ") > -1) {
            tag = tag.substring(0, tag.indexOf(" ")); // remove " (new tag)"
        };

        let storedTags = $("#txtBulkTags").val().split(";").filter((element) => element);

        // no duplicates
        if (storedTags.indexOf(tag) > -1) {
            return;
        }

        if (storedTags.length > 9) {
            showError(tagsPerSurvey);
            return;
        }

        storedTags.push(tag);
        $("#txtBulkTags").val(storedTags.join(";"));

        let tagElement = "<span class=\"badge\" tag=\"" + tag + "\" >" + tag + "&nbsp;<span onClick='removeTagBulk(this)'>&#10006;</span> </span>";
        $("#BulkTags").append(tagElement);
    }

    function removeTagBulk(element) {
        let storedTags = $("#txtBulkTags").val().split(";");

        let text = $($(element).parent()).attr("tag");
        let index = storedTags.indexOf(text);
        storedTags.splice(index, 1);
        $("#txtBulkTags").val(storedTags.join(";"));

        $(element).parent().remove();
    }

</script>