<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Translations" /></title>
	
	<%@ include file="../includes.jsp" %>
	
	<script type="text/javascript" src="${contextpath}/resources/js/translations.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/fileuploader.js?version=<%@include file="../version.txt" %>"></script>
	
	<link href="${contextpath}/resources/css/management.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/fileuploader.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	
	<style>
		#search-replace-dialog-2-table {
			table-layout: fixed;
		}
		
		#search-replace-dialog-2-table td, #search-replace-dialog-2-table th {
			max-width: 50%;
			width: 50%;
		}
		
		.typeahead {
			z-index: 1051;
		}
		
		.label {
			display: block;
			width: 100%;
		}
		
		#sortable {list-style-type: none; margin: 0; padding: 0; }
		#sortable li { float: left; margin: 0 3px 3px 3px; padding: 5px; border: 1px solid #000; background-color: #ddd }
		#sortable li span:not(.glyphicon) { position: absolute; margin-left: -1.3em; }
	</style>
	
	<script type="text/javascript">
	
		var LabelType = "<spring:message code='label.LabelType' />";
		var ExistingLabels = "<spring:message code='label.ExistingLabels' />";
		var NewLabels = "<spring:message code='label.NewLabels' />";
		var deleteTranslation0 = "<spring:message code='message.TranslationCannotBeDeleted' />";
		var deleteTranslation1 = "<spring:message code='message.OneTranslationNeeded' />";
		var requestTranslationSucces = "<spring:message code='info.RequestTranslation' />";
		var requestTranslationError = "<spring:message code='error.RequestTranslation' />";
		var labelPublish = "<spring:message code='label.Publish' />";
		var labelUnpublish = "<spring:message code='label.Unpublish' />";
		var languagecodes = ${languagecodes};
		var labellocked = "<spring:message code='label.lockedTranslation' />";
				
		$(function() {					
			$("#form-menu-tab").addClass("active");
			$("#translations-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");	
			
			var uploader = new qq.FileUploader({
			    element: $("#file-uploader")[0],
			    action: '${contextpath}/noform/management/importtranslation',
			    uploadButtonText: selectFileForUpload,
			    params: {
			    	'_csrf': csrftoken
			    },
			    multiple: false,
			    cache: false,
			    sizeLimit: 1048576,
			    onSubmit: function()
			    {
			    	$("#codeupdate-error").hide();
			    	var selectedLang = $("#langupdate").val();
			    	if (selectedLang == "other" && $("#codeupdate").val().trim() == '')
			    	{
			    		$("#codeupdate-error").show();
			    		return false;
			    	}
			    	
			    },
			    onComplete: function(id, fileName, responseJSON)
				{
			    	if (!responseJSON.success)
			    	{
			    		$("#file-uploader-message").html("<span style='color: #f00'>" + responseJSON.message + "</span>");
			    	} else {
			    		$("#file-uploader-message").html("");
			    		checkResults(responseJSON);
			    	}
				},
				showMessage: function(message){
					$("#file-uploader").append("<div class='validation-error'>" + message + "</div>");
				},
				onUpload: function(id, fileName, xhr){
					$("#file-uploader").find(".validation-error").remove();			
				}
			});
			
			$(".qq-upload-button").addClass("btn btn-default").removeClass("qq-upload-button");
			$(".qq-upload-drop-area").css("margin-left", "-1000px");
			
			checkLanguage();
			checkUpdateLanguage();	
			$('[data-toggle="tooltip"]').tooltip(); 
		});	
		
		function getInfo(id)
		{
			<c:forEach items="${infos}" var="info">				
				if (id.replace('"','') == "${info.key.replace("\"","")}") return "${info.value}";			
			</c:forEach>			
			return id;
		}		
		
		function checkAll(e)
		{
			if ($(e).is(':checked'))
			{
				$(".translationselector").prop("checked","checked");
			} else {
				$(".translationselector").removeAttr("checked");			
			}			
		}
		
		var selectedTranslation = null;
		function showSearchAndReplaceDialog(translationId)
		{
			selectedTranslation = translationId;
			$("#search-replace-dialog-search").val("");
			$("#search-replace-dialog-replace").val("");
			$("#search-replace-dialog").modal("show");
		}
		
		function searchAndReplaceBack()
		{
			$("#search-replace-dialog-2").modal("hide");
			$("#search-replace-dialog").modal("show");
		}
		
		function searchAndReplace()
		{
			var s = encodeURI($("#search-replace-dialog-search").val());
			var r = encodeURI($("#search-replace-dialog-replace").val());
			
			$("#search-replace-dialog-replace").parent().find(".validation-error").remove();
			
			var request = $.ajax({
			  url: "${contextpath}/${sessioninfo.shortname}/management/searchAndReplace",
			  data: {translationId : selectedTranslation, search: s, replace: r},
			  dataType: "json",
			  cache: false,
			  success: function(data)
			  {	
				  if (data.emptyLabels)
				  {
					  $("#search-replace-dialog-replace").parent().append("<div class='validation-error'><spring:message code='error.EmptyLabelsAfterReplace' /></div>");
					  return;
				  }
				  				  
				  $("#search-replace-dialog-2-body").empty();
				  for (var i = 0; i < data.searchResults.length; i++)
				  {
					var tr = document.createElement("tr");
					var td = document.createElement("td");
					$(td).append("<div style='word-wrap: break-word'>" + data.searchResults[i] + "</div>");
					$(tr).append(td);
					td = document.createElement("td");
					$(td).append("<div style='word-wrap: break-word'>" + data.replaceResults[i] + "</div>");
					$(tr).append(td);
					$("#search-replace-dialog-2-body").append(tr);
				  }					  
				  
				  $("#search-replace-dialog-2-id").val(selectedTranslation);
				  $("#search-replace-dialog-2-search").val($("#search-replace-dialog-search").val());
				  $("#search-replace-dialog-2-replace").val($("#search-replace-dialog-replace").val());
				  $("#search-replace-dialog").modal("hide");
				  $("#search-replace-dialog-2").modal("show");
			  }
			});			
		}
		
		function checkLanguage()
		{
			$("#unknown-language-error").hide();
			$("#add-translation-dialog-error").hide();
			$("#unsupported-language-error").hide();
			
			if ($("#lang").val() == "select")
			{
				$('#code').prop("disabled", "disabled").val("");
			} else if ($("#lang").val() == "other")
			{
				$('#code').removeAttr("disabled").val("").focus();
			} else {
				$('#code').prop("disabled", "disabled").val($("#lang").val());
			}
		}
		
		function checkUpdateLanguage()
		{
			if ($("#langupdate").val() == "other")
			{
				$("#otherlangupdate").show();
				$("#otherlangupdate").find("input").focus();
			} else {
				$("#otherlangupdate").hide();
			}
		}
	</script>
		
</head>
<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>	
		<%@ include file="../menu.jsp" %>
		<%@ include file="formmenu.jsp" %>
		
		<input type="hidden" id="surveylanguage" value="${form.survey.language.code}" />
	
		<div class="fullpageform100">
			
			<div id="action-bar" class="container action-bar" style="padding-top: 20px;">
				<div class="row">
					<div class="col-md-4">
						<c:choose>
							<c:when test="${USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1 || form.survey.owner.id == USER.id}">
								<select id="translation-action" name="action" class="small-form-control">
									<option value="edit"><spring:message code="label.EditTranslations" /></option>
									<option value="delete"><spring:message code="label.DeleteTranslations" /></option>							
									<c:if test="${isMTAvailable}">
										<option value="translate"><spring:message code="label.RequestTranslations" /></option>
									</c:if>		
								</select>
								<a id="goBtnFromTranslation" onclick="executeOperation();" class="btn btn-default"><spring:message code="label.OK" /></a><br /><br />					
							</c:when>
							<c:otherwise>
								<select id="translation-action" disabled="disabled" name="action" class="small-form-control">
									<option value="edit"><spring:message code="label.EditTranslations" /></option>
									<option value="delete"><spring:message code="label.DeleteTranslations" /></option>
									<c:if test="${isMTAvailable}">
										<option value="translate"><spring:message code="label.RequestTranslations" /></option>
									</c:if>										
								</select>
								<a id="goDisabledBtnFromTranslation" class="btn disabled btn-default"><spring:message code="label.Go" /></a>				
							</c:otherwise>
						</c:choose>
					</div>
					<div class="col-md-8" style="text-align:right">
						<c:choose>
						<c:when test="${USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1 || form.survey.owner.id == USER.id}">				
							<a id="addBtnFromTranslation" onclick="$('#add-translation-dialog-error').hide();$('#add-translation-dialog').modal('show');" class="btn btn-primary"><spring:message code="label.AddNewTranslation" /></a>
							<a id="uploadBtnFromTranslation" onclick="$('#file-uploader-message').empty();$('#file-uploader-message-language').hide();$('.qq-upload-list').empty();$('#langupdate').val('other');$('#langupdate')[0].selectedIndex=0;$('#otherlangupdate').show();$('#upload-translation-dialog').modal('show');" class="btn btn-default"><spring:message code="label.UploadExistingTranslation" /></a>
						</c:when>
						<c:otherwise>				
							<a id="addDisabledBtnFromTranslation" class="btn disabled btn-default"><spring:message code="label.AddNewTranslation" /></a>
							<a id="uploadDisabledBtnFromTranslation" class="btn disabled btn-default"><spring:message code="label.UploadExistingTranslation" /></a>				
						</c:otherwise>
					</c:choose>
					</div>
				</div>
			</div>
			
			<table id="translations" class="table table-bordered table-striped table-styled" style="margin-top: 40px; width: auto; margin-left: auto; margin-right: auto;">
				<thead>
					<tr>
						<th><input type="checkbox" id="checkAll" onclick="checkAll(this);" /></th>
						<th><spring:message code="label.Language" /></th>
						<th><spring:message code="label.Title" /></th>
						<th><spring:message code="label.Status" /></th>
						<th style="width: 160px; min-width: 160px;"><spring:message code="label.Actions" /></th>
						<th style="width: 85px; min-width: 85px;"><spring:message code="label.Export" /></th>
					</tr>
				</thead>
				<tbody>
				
					<c:forEach items="${translations}" var="translation">
						<c:if test="${translation.language != null}">
							<tr <c:if test="${translation.language == form.survey.language}">class="pivot"</c:if>>
								<td>
									<c:choose>
										<c:when test="${translation.language != form.survey.language}">
											<input class="translationselector" type="checkbox" id="check${translation.id}" name="check${translation.id}" onclick="$('#checkAll').removeAttr('checked')"/>
										</c:when>
										<c:otherwise>
											<input class="translationselector pivot" type="checkbox" id="check${translation.id}" name="check${translation.id}" onclick="$('#checkAll').removeAttr('checked')"/>
										</c:otherwise>
									</c:choose>
								</td>
								<td>${translation.language.code}</td>
								<td><div class="questiontitle" style="width: 600px">${translation.title}</div></td>
								<td style="vertical-align: middle">
									<c:choose>
										<c:when test="${translation.language == form.survey.language}">
											<span class="label label-info"><spring:message code="label.MainLanguage" /></span>
										</c:when>
										<c:when test="${translation.complete && translation.active}">
											<span class="label label-success"><spring:message code="label.Published" /></span>
											<span class="label label-warning" style="display: none"><spring:message code="label.Complete" /></span>
										</c:when>
										<c:when test="${translation.complete && !translation.active}">
											<span class="label label-success" style="display: none"><spring:message code="label.Published" /></span>
											<span class="label label-warning"><spring:message code="label.Complete" /></span>
										</c:when>
										<c:when test="${translation.requested != null && translation.requested}">
											<span class="label label-warning"><spring:message code="label.Requested" /></span>
										</c:when>
										<c:otherwise>
											<span class="label label-danger"><spring:message code="label.Incomplete" /></span>
										</c:otherwise>
									</c:choose>
									<span class="label label-warning requested" style="display: none"><spring:message code="label.Requested" /></span>
									<span class="label label-danger error" style="display: none"><spring:message code="label.Error" /></span>
								</td>
								<td style="text-align: center; width: 180px">
									<c:choose>
										<c:when test="${USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1 || form.survey.owner.id == USER.id}">
											<a id="searchBtnFromTransTable" onclick="showSearchAndReplaceDialog(${translation.id});" class="iconbutton"  data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.SearchAndReplace" />"><span class="glyphicon glyphicon-search"></span></a>
											<a id="editTranslationBtnFromTransTable" onclick="editSingleTranslation(this)" class="iconbutton" data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Edit" />"><span class="glyphicon glyphicon-pencil"></span></a>
											<c:if test="${isMTAvailable}">
												<c:choose>
													<c:when test="${translation.complete || (translation.requested != null && translation.requested)}">
														<a class='iconbutton disabled' data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.RequestTranslation" />"><span class="glyphicon glyphicon-refresh"></span></a>
													</c:when>
													<c:otherwise>													
															<a onclick="requestSingleTranslation(this,${translation.id})" class='iconbutton' data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.RequestTranslation" />"><span class="glyphicon glyphicon-refresh"></span></a>
													</c:otherwise>
												</c:choose>
											</c:if>	
											
											<c:choose>
												<c:when test="${translation.language == form.survey.language}">
													<a class='iconbutton disabled' data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Unpublish" />"><span class="glyphicon glyphicon-pause"></span></a>
												</c:when>
												<c:when test="${translation.active && translation.complete}">
													<c:choose>
														<c:when test="${USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1 || form.survey.owner.id == USER.id}">
													           <a class='iconbutton' data-toggle="tooltip" rel="tooltip" data-activetitle="<spring:message code="label.Publish" />" data-inactivetitle="<spring:message code="label.Unpublish" />" title="<spring:message code="label.Unpublish" />" onclick="switchActive(${translation.id}, this)"><span class="glyphicon glyphicon-pause"></span></a>
                                                       	</c:when>
														<c:otherwise>
															<a class='iconbutton disabled' data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Unpublish" />"><span class="glyphicon glyphicon-pause"></span></a>
														</c:otherwise>
													</c:choose>
												</c:when>
												<c:when test="${translation.complete}">
													<c:choose>
														<c:when test="${USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1 || form.survey.owner.id == USER.id}">
															<a class='iconbutton' data-toggle="tooltip" rel="tooltip" data-activetitle="<spring:message code="label.Publish" />" data-inactivetitle="<spring:message code="label.Unpublish" />" title="<spring:message code="label.Publish" />" onclick="switchActive(${translation.id}, this)"><span class="glyphicon glyphicon-play"></span></a>
														</c:when>
														<c:otherwise>
															<a class='iconbutton disabled' data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Publish" />"><span class="glyphicon glyphicon-play"></span></a>
														</c:otherwise>
													</c:choose>										
					 							</c:when>
												<c:otherwise>
													<a class='iconbutton disabled' data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Publish" />"><span class="glyphicon glyphicon-play"></span></a>
												</c:otherwise>
											</c:choose>											
											
											<c:choose>
												<c:when test="${translation.language != form.survey.language}">
													<a onclick="deleteSingleTranslation(${translation.id})" class='iconbutton' data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Delete" />"><span class="glyphicon glyphicon-remove"></span></a>
												</c:when>
												<c:otherwise>
													<a class='iconbutton disabled' data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Delete" />"><span class="glyphicon glyphicon-remove"></span></a>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<a id="searchBtnDisFromTransTable" class="iconbutton disabled" data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.SearchAndReplace" />"><span class="glyphicon glyphicon-search"></span></a>
											<a id="editTranslationBtnDisFromTransTable" class="iconbutton disabled" data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Edit" />"><span class="glyphicon glyphicon-pencil"></span></a>
											<a id="requestTranslationBtnDisFromTransTable" class="iconbutton disabled" data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.RequestTranslation" />"><span class="glyphicon glyphicon-refresh"></span></a>
											<c:choose>
												<c:when test="${translation.active && translation.complete}">
													<a class='iconbutton disabled' data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Unpublish" />"><span class="glyphicon glyphicon-pause"></span></a>
												</c:when>
												<c:otherwise>
													<a class='iconbutton disabled' data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Unpublish" />"><span class="glyphicon glyphicon-play"></span></a>
												</c:otherwise>
											</c:choose>
											<a id="deleteTranslationBtnFromTransTable" class="iconbutton disabled" data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Delete" />"><span class="glyphicon glyphicon-remove"></span></a>
										</c:otherwise>
									</c:choose>
								</td>
								<td style="width: 130px;">
									<a target="_blank" href="<c:url value="/${sessioninfo.shortname}/management/downloadtranslation?id=${translation.id}&format=xml"/>" data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.DownloadXML" />"><img src="${contextpath}/resources/images/file_extension_xml_small.png" /></a>
									<a target="_blank" href="<c:url value="/${sessioninfo.shortname}/management/downloadtranslation?id=${translation.id}&format=xlsx"/>" data-toggle="tooltip" rel="tooltip" title="<spring:message code="tooltip.Downloadxlsx" />"><img src="${contextpath}/resources/images/file_extension_xlsx_small.png" /></a>
									<a target="_blank" href="<c:url value="/${sessioninfo.shortname}/management/downloadtranslation?id=${translation.id}&format=ods"/>" data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.DownloadODS" />"><img src="${contextpath}/resources/images/file_extension_ods_small.png" /></a>
								</td>
							</tr>
						</c:if>
					</c:forEach>
				</tbody>
			</table>
			
			<table id="translationsTable" style="display:none;">
				<c:forEach items="${translations}" var="translation">
					<tr id="${translation.id}" data-lang="${translation.language.code}" data-active="${translation.active}" data-complete="${translation.complete}" data-requested="${translation.requested}" >
						<c:forEach items="${translation.translations}" var="trans">
							<td id="${trans.id}" data-locked="${trans.locked}" data-label="${trans.label.replace("\"","\'")}" data-key="${trans.key.replace("\"","")}"><textarea><c:out value="${trans.label}" /></textarea></td>
						</c:forEach>
					</tr>
				</c:forEach>
			</table>
			
		</div>
	</div>
	
	<%@ include file="../footer.jsp" %>	
	<%@ include file="translations-dialog.jsp" %>	

</body>
</html>
