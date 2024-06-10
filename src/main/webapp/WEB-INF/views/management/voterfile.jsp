<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div id="newvoterfile" data-bind="visible: Page() == 6">
	<div id="action-bar" class="container-fluid action-bar">
		<div class="row">
			<div class="col-md-2" style="text-align: left">
				<a href="${contextpath}/noform/management/participants"><spring:message code="label.Participants" /></a> <span class="glyphicon glyphicon-menu-right" style="font-size: 90%"></span> 
				<!-- ko if: selectedGroup() != null && selectedGroup().id() == 0 -->
				<spring:message code="label.CreateNewVoterFile" />
				<!-- /ko -->
				
				<!-- ko if: selectedGroup() != null && selectedGroup().id() > 0 -->
				<spring:message code="label.EditVoterFile" />
				<!-- /ko -->
			</div>
			<div class="col-md-10" style="text-align: left">
				
				<!-- <button class="btn btn-primary" onclick="openAddVoterDialog()"><spring:message code="label.AddUser" /></button> -->
				<a class="btn btn-primary" onclick="showVoterFileExportDialog()"><spring:message code="label.Export" /></a>
				<span v-if="totalVoters > 0" style="margin-left: 20px">
		      		<span data-bind="text: totalVoters"></span>&nbsp;<spring:message code="label.entries" />
		      	</span>	
				<br />
				
				<br />
				<table id="voterfiletable" class="table table-bordered table-striped table-styled" style="width: auto">
					<thead>
						<tr>
							<th><spring:message code="label.Actions" /></th>
							<th><spring:message code="label.UserName" /></th>
							<th><spring:message code="label.FirstName" /></th>
							<th><spring:message code="label.Surname" /></th>
							<th><spring:message code="label.HasVoted" /></th>
						</tr>
						<tr class="table-styled-filter">
							<th></th>
							<th class="filtercell"><input id="voterUserName" onkeyup="checkFilterCell($(this).closest('.filtercell'), true)" type="text" /></th>
							<th class="filtercell"><input id="voterFirstName" onkeyup="checkFilterCell($(this).closest('.filtercell'), true)" type="text" /></th>
							<th class="filtercell"><input id="voterLastName" onkeyup="checkFilterCell($(this).closest('.filtercell'), true)" type="text" /></th>
							<th class="filtercell">
								<select id="voterVoted" onchange="firstVoterPage()">
									<option value=""><spring:message code="label.All" /></option>
									<option value="true"><spring:message code="label.Yes" /></option>
									<option value="false"><spring:message code="label.No" /></option>
								</select>
							</th>
						</tr>
					</thead>
					
					<!-- ko if: Voters().length == 0 -->
						<tbody>
							<tr>
								<td class="text-center" data-bind="attr: {colspan: 6}"><spring:message code="label.NoData" /></td>
							</tr>
						</tbody>				
					<!-- /ko -->
					<!-- ko if: Voters().length > 0 -->
						<tbody data-bind="foreach: Voters">
							<tr>
								<td>
									<a data-bind="attr: {onclick: 'deleteVoter(' + id + ')'}" class="iconbutton" data-toggle="tooltip" title="<spring:message code="label.Remove" />"><span class='glyphicon glyphicon-remove'></span></a>										
								</td>
								<td data-bind="text: ecMoniker"></td>
								<td data-bind="text: givenName"></td>
								<td data-bind="text: surname"></td>
								<td>
									<span data-bind="visible: voted"><spring:message code="label.Yes" /></span>
									<span data-bind="visible: !voted"><spring:message code="label.No" /></span>
								</td>
							</tr>
						</tbody>
					<!-- /ko -->
					<tfoot>
					    <tr>
					      <td colspan="5" style="text-align: center; padding: 10px;">
							<a data-toggle="tooltip" title="<spring:message code="label.GoToFirstPage" />" data-bind="attr: {style: voterPage() > 1 ? '' : 'color: #ccc', onclick: voterPage() > 1 ? 'firstVoterPage()' : ''}"><span class="glyphicon glyphicon-step-backward"></span></a>
							<a data-toggle="tooltip" title="<spring:message code="label.GoToPreviousPage" />" data-bind="attr: {style: voterPage() > 1 ? '' : 'color: #ccc', onclick: voterPage() > 1 ? 'previousVoterPage()' : ''}"><span class="glyphicon glyphicon-chevron-left"></span></a>
											
							<span data-bind="html: (voterPage() - 1) * 20 + 1"></span>&nbsp;
							<spring:message code="label.to" />&nbsp;
							<span data-bind="html: (voterPage() - 1) * 20 + Voters().length"></span>
							
							<a data-toggle="tooltip" title="<spring:message code="label.GoToNextPage" />" data-bind="attr: {style: lastVoterReached() ? 'color: #ccc' : '', onclick: lastVoterReached() ? '' : 'nextVoterPage()'}"><span class="glyphicon glyphicon-chevron-right"></span></a>
							<a data-toggle="tooltip" title="<spring:message code="label.GoToLastPage" />" data-bind="attr: {style: lastVoterReached() ? 'color: #ccc' : '', onclick: lastVoterReached() ? '' : 'lastVoterPage()'}"><span class="glyphicon glyphicon-step-forward"></span></a>
														
					      </td>
					    </tr>
					  </tfoot>
				</table>
			</div>
		</div>
	</div>	
	
	<div class="fullpageform40">
		<div data-bind="visible: Step() == 1">
		
		</div>
	</div>
</div>

<div class="modal" id="confirm-delete-voter-dialog" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<b><spring:message code="label.Delete" /></b>
			</div>
			<div class="modal-body">
				<spring:message code="question.DeleteVoter" />
			</div>
			<div class="modal-footer">
				<a onclick="deleteVoterContinue(); return false;" class="btn btn-primary"><spring:message code="label.OK" /></a>
				<a class="btn btn-default" data-dismiss="modal"><spring:message	code="label.Cancel" /></a>
			</div>
		</div>
	</div>
</div>

<div class="modal" id="export-voter-file-dialog" data-backdrop="static">
	<div class="modal-dialog modal-sm">
		<div class="modal-content">
			<div class="modal-header" style="font-weight: bold;">
				<spring:message code="label.Start" />&nbsp;<span id="export-voter-file-dialog-type"></span>&nbsp;<spring:message code="label.Export" />
			</div>
			<div class="modal-body" style="padding-left: 30px;">
				<label for="export-name-voter-file" style="display:inline"><span class="mandatory">*</span><spring:message code="label.ExportName" /></label>
				<input class="form-control" type="text" id="export-name-voter-file" maxlength="255" name="export-name" style="width:220px; margin-top: 10px" />
				<span id="validation-error-vf-required" class="validation-error hideme"><br /><spring:message code="validation.required" /></span>
				<span id="validation-error-vf-exportname" class="validation-error hideme"><spring:message code="validation.name2" /></span>
			</div>
			<div class="modal-footer">
				<img alt="wait animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
				<a id="okVFStartExportButton"  onclick="exportVoterFile($('#export-name-voter-file').val());"  class="btn btn-primary"><spring:message code="label.OK" /></a>
				<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>
			</div>
		</div>
	</div>
</div>

<div class="modal" id="import-voter-file-dialog" role="dialog">
	<div class="modal-dialog" role="document">
	    <div class="modal-content">
		    <div class="modal-header"><spring:message code="label.ImportVoterFile" /></div>
			<div class="modal-body">
				<span id="voterfileupload"></span>
				<span id="file-uploader-support"></span>
			</div>
			<div class="modal-footer">
				<a onclick="$('#import-voter-file-dialog').modal('hide')" class="btn btn-default"><spring:message code="label.Cancel" /></a>
			</div>
		</div>
	</div>
</div>

<div class="modal" id="add-voter-dialog" role="dialog">
	<div class="modal-dialog modal-lg" role="document">
		<div class="modal-content">
			<div class="modal-header"><spring:message code="label.AddUser" /></div>
			<div class="modal-body">
				<div class="col-md-5">
					<span class="mandatory" aria-label="Mandatory">*</span>
					Domain
					<select id="voters-domain" class="form-control" style="width: 100%; margin-bottom: 20px;" >
						<option></option>
						<c:forEach items="${domains}" var="domain" varStatus="rowCounter">
							<option value="${domain.key}">${domain.value} </option>
						</c:forEach>
					</select>
				</div>
				<div style="float: right">
					<a onclick="searchVoters(1)" class="btn btn-default"><spring:message code="label.Search" /></a>
				</div>

				<table class="table table-bordered table-styled table-striped ptable" style="overflow-y: hidden">
					<thead>
					<tr class="attribute-names">
																									<!--Save checked in a variable as the v.selected observable will change the checkallvoters:checked state-->
						<th class="checkcell"><input id="checkallvoters" type="checkbox" onclick="let checked = $(this).is(':checked'); _participants.EVoteUsers().forEach((v) => {v.selected(checked)});" /></th>
						<th><spring:message code="label.Name" /></th>
						<th><spring:message code="label.Email" /></th>
						<th><spring:message code="label.Login" /></th>
						<th><spring:message code="label.Department" /></th>
					</tr>
					<tr class="table-styled-filter">
						<th class="checkcell">&nbsp;</th>
						<th class="filtercell">
							<input onkeyup="checkVoterSearchSubmit(event, this); checkFilterCell($(this).closest('.filtercell'), true)" type="text" maxlength="255" style="margin:0px;" id="voters-name" />
						</th>
						<th class="filtercell">
							<input onkeyup="checkVoterSearchSubmit(event, this); checkFilterCell($(this).closest('.filtercell'), true)" type="text" maxlength="255" style="margin:0px;" id="voters-email"  />
						</th>
						<th class="filtercell">
							<input onkeyup="checkVoterSearchSubmit(event, this); checkFilterCell($(this).closest('.filtercell'), true)" type="text" maxlength="255" style="margin:0px;" id="voters-login"  />
						</th>
						<th class="filtercell">
							<input onkeyup="checkVoterSearchSubmit(event, this); checkFilterCell($(this).closest('.filtercell'), true)" type="text" maxlength="255" style="margin:0px;" id="voters-department"  />
						</th>
					</tr>
					</thead>
					<tbody id="voters">
						<tr data-bind="visible: EVoteUsers().length == 0">
							<td class="text-center" colspan="5"><spring:message code="label.NoData" /></td>
						</tr>
						<!-- ko foreach: EVoteUsers() -->
						<tr>
							<td class="checkcell">
								<input type="checkbox" data-bind="checked: selected">
							</td>
							<td data-bind="text: displayName"></td>
							<td data-bind="text: email"></td>
							<td data-bind="text: ecMoniker"></td>
							<td data-bind="text: departmentNumber"></td>
						</tr>
						<!-- /ko -->
					</tbody>
					<tfoot>
					<tr>
						<td colspan="5" style="text-align: center">
							<a data-bind="attr:{style: VotersSearchPage() > 1 ? '' : 'color: #ccc'}" data-toggle="tooltip" title="<spring:message code="label.GoToFirstPage" />" onclick="if (_participants.VotersSearchPage() > 1) searchVoters(1)"><span class="glyphicon glyphicon-step-backward"></span></a>
							<a data-bind="attr:{style: VotersSearchPage() > 1 ? '' : 'color: #ccc'}" data-toggle="tooltip" title="<spring:message code="label.GoToPreviousPage" />" onclick="if (_participants.VotersSearchPage() > 1) searchVoters(_participants.VotersSearchPage() - 1)"><span class="glyphicon glyphicon-chevron-left"></span></a>

							<span data-bind="text: VotersSearchStart"></span>&nbsp;
							<spring:message code="label.to" />&nbsp;
							<span data-bind="text: VotersSearchEnd"></span>

							<a data-bind="attr:{style: EVoteUsers().length >= votersSearchPageSize ? '' : 'color: #ccc'}"  data-toggle="tooltip" title="<spring:message code="label.GoToNextPage" />" onclick="if (_participants.EVoteUsers().length >= votersSearchPageSize) searchVoters(_participants.VotersSearchPage() + 1)"><span class="glyphicon glyphicon-chevron-right"></span></a>
						</td>
					</tr>
					</tfoot>
				</table>
			</div>
			<div class="modal-footer">
				<a onclick="$('#add-voter-dialog').modal('hide')" class="btn btn-default"><spring:message code="label.Cancel" /></a>
				<a onclick="addSelectedVoters()" class="btn btn-primary"><spring:message code="label.AddUser" /></a>
			</div>
		</div>
	</div>
</div>

<script>
	function getVoterParams(page) {
		let params;
		if (page) {
			params = "?page=" + _participants.voterPage();
		} else{
			params = "?all=1";
		}
		
		if ($("#voterUserName").val().length > 0) {
			params += "&user=" + $("#voterUserName").val();
		}
		if ($("#voterFirstName").val().length > 0) {
			params += "&first=" + $("#voterFirstName").val();
		}
		if ($("#voterLastName").val().length > 0) {
			params += "&last=" + $("#voterLastName").val();
		}
		if ($("#voterVoted").val().length > 0) {
			params += "&voted=" + $("#voterVoted").val();
		}
		return params;
	}


	function resetVoterFilterHighlighting() {
		let voterFilterfields = ["#voterUserName", "#voterFirstName", "#voterLastName", "#voterVoted"];
		voterFilterfields.forEach((filterField) => {
			if($(filterField).val().length <= 0){
				$(filterField).parent().css("background-color","");
			}
		});
	}

	function loadVoters() {

		_participants.ShowWait(true);

		resetVoterFilterHighlighting();
		
		const model = this;

		//The server request would understand -1 as last page but this will result in calculation errors for the current page
		//So calculate the last page on the values that we already have
		if (_participants.voterPage() == -1){
			_participants.voterPage(Math.floor(_participants.totalVoters() / 20) + 1)
		}

		let params = getVoterParams(true);
		
		$.ajax({
		  url: contextpath + "/noform/management/votersJSON" + params,
		  dataType: 'json',
		  cache: false,
		  success: function(list){
			  applyVotersLoaded(list)
			  _participants.ShowWait(false);
		  }, error: function() {
			  showGenericError();
				_participants.ShowWait(false);
		  }
		});
	}
	
	function loadTotalVoters() {
		const model = this;
		let params = getVoterParams(false);
		$.ajax({
		  url: contextpath + "/noform/management/totalVotersJSON" + params,
		  dataType: 'json',
		  cache: false,
		  success: function(total){
			  _participants.totalVoters(total);
		  }, error: function() {
			  showGenericError();
		  }
		});
	}

	function showVoterFileExportDialog(){
		$('#export-voter-file-dialog').val("");
		$('#export-voter-file-dialog-type').text("XLSX");
		$('#export-voter-file-dialog').modal();
		$('#export-voter-file-dialog').find("input").first().focus();
	}

	function exportVoterFile(name){
		//check if input valid
		$("#export-voter-file-dialog-dialog").find(".validation-error").hide();

		if (name === null || name.trim().length === 0)
		{
			$("#export-name-type-dialog").find("#validation-error-vf-required").show();
			return;
		}

		var reg = /^[a-zA-Z0-9-_\.]+$/;
		if( !reg.test( name ) ) {
			$("#export-name-type-dialog").find("#validation-error-vf-exportname").show();
			return;
		};

		window.checkExport = true;
		$("#export-voter-file-dialog").modal("hide");


		//export
		$.ajax({
			type: "POST",
			url: "${contextpath}/exports/start/VoterFiles/xlsx",
			data: {exportName: name, showShortnames: false, allAnswers: false, group: ""},
			beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
			success: function(data)
			{
				if (data == "success") {
					showExportSuccessMessage();
				} else {
					showExportFailureMessage();
				}
			}
		});

		return false;
	}
	
	let voterIdToDelete = 0;
	function deleteVoter(id) {
		voterIdToDelete = id;
		$('#confirm-delete-voter-dialog').modal('show');
	}
	
	function deleteVoterContinue() {
		_participants.ShowWait(true);
		$.ajax({
			type:'POST',
			  url: contextpath + '/noform/management/deleteVoter?id=' + voterIdToDelete,
			  contentType: "application/json; charset=utf-8",
			  processData:false, //To avoid making query String instead of JSON
			  beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
			  cache: false,
			  success: function( data ) {						  
				if (data == "success") {
				   loadVoters();
				} else {
					showError("<spring:message code="error.DeletionFailed" />");
				}
				_participants.ShowWait(false);
			}, error: function() {
				  showGenericError();
			  }
		});
		$('#confirm-delete-voter-dialog').modal('hide');
	}
	
	function firstVoterPage() {
		_participants.voterPage(1);
		loadVoters();
	}
	
	function previousVoterPage() {
		_participants.voterPage(_participants.voterPage()-1);
		loadVoters();
	}
		
	function nextVoterPage() {
		_participants.voterPage(_participants.voterPage()+1);
		loadVoters();
	}
	
	function lastVoterPage() {
		_participants.voterPage(-1);
		loadVoters();
	}

	function applyVotersLoaded(list){
		_participants.Voters.removeAll();
		if (list.length < 20 || _participants.voterPage() == -1)
		{
			_participants.lastVoterReached(true);
		} else {
			_participants.lastVoterReached(false);
		}
		if (_participants.voterPage() == -1) {
			_participants.voterPage(Math.floor(_participants.totalVoters() / 20));
		}
		for (var i = 0; i < list.length; i++ ){
			var user = list[i];
			_participants.Voters.push(user);
		}

		$('#import-voter-file-dialog').modal('hide');

		$("[data-toggle]").tooltip();

		loadTotalVoters();
	}

	const votersSearchPageSize = 10;

	function openAddVoterDialog(){
		_participants.EVoteUsers.removeAll();
		let checkAll = document.getElementById('checkallvoters');
		checkAll.checked = false;
		checkAll.indeterminate = false;
		$('#add-voter-dialog').modal();
		searchVoters(1)
	}

	function checkVoterSearchSubmit(event, element){
		if (event.key === "Enter"){
			searchVoters(1)
		}
	}

	function searchVoters(page){

		if ($("#voters-domain").val().length == 0){
			//This helps to understand that a domain should be selected
			_participants.EVoteUsers.removeAll()
			return;
		}

		_participants.VotersSearchPage(page)

		let s = "name=" + $("#voters-name").val() + "&login=" + $("#voters-login").val() +  "&domain=" + $("#voters-domain").val() + "&email=" + $("#voters-email").val() + "&department=" + $("#voters-department").val() + "&newPage=" + _participants.VotersSearchPage() + "&itemsPerPage=" + votersSearchPageSize;

		let checkAll = document.getElementById('checkallvoters');
		checkAll.checked = false;
		checkAll.indeterminate = false;

		_participants.EVoteUsers.removeAll()
		_participants.ShowWait(true)
		$.ajax({
			url: contextpath + "/noform/management/usersJSON",
			data: s,
			dataType: 'json',
			cache: false,
			success: function(paging){
				for (let i = 0; i < paging.items.length; i++ ){
					let user = paging.items[i];
					user.selected = ko.observable(false);
					user.selected.subscribe(function (){
						//Change 'SelectAllCheckbox' visuals
						//This is needed as this nested observable will not cause any view updates
						//apart from the checkbox to select the user
						let l = _participants.EVoteUsers().filter(v => v.selected()).length;
						checkAll.checked = l == _participants.EVoteUsers().length;
						checkAll.indeterminate = l > 0 && l < _participants.EVoteUsers().length;
					})
					_participants.EVoteUsers.push(user)
				}
				_participants.ShowWait(false)
			}, error: function() {
				showGenericError();
				_participants.ShowWait(false)
			}
		});
	}

	function addSelectedVoters(){

		//Filter selected and create an array containing all ecas user ids of the selected ones
		let voters = _participants.EVoteUsers().filter(v => v.selected()).map(v => v.id)

		if (voters.length == 0){
			return;
		}

		$.ajax({
			url: contextpath + "/noform/management/addVoters",
			data: {voters: voters},
			dataType: 'json',
			cache: false,
			async: false,
			method: "POST",
			beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
			success: function(list){
				if (list != null && list.length > 0) {
					applyVotersLoaded(list)
				} else {
					showGenericError();
				}
				_participants.ShowWait(false)
			}, error: function() {
				showGenericError();
				_participants.ShowWait(false)
			}
		});
		$('#add-voter-dialog').modal('hide')
	}
	//Only loadVoters and create an uploader when it actually is an eVote Survey
	<c:if test="${form.survey.isEVote}">
	$(function() {
		loadVoters();
		
		var uploader = new qq.FileUploader({
			element: $("#voterfileupload")[0],
			action: contextpath + '/noform/management/uploadvoterfile',
			uploadButtonText: '<spring:message code="label.UploadFile" />',
			params: {
				'_csrf': csrftoken
			},
			multiple: false,
			cache: false,
			sizeLimit: 10485760,
			onComplete: function(id, fileName, list)
			{
				//console.log(list);
				if (list != null && list.length > 0)
				{					
					_participants.Page(6);
					_participants.Step(1);
					var g = new Guestlist();
					g.type("VoterFile");
					_participants.selectedGroup(g);

					applyVotersLoaded(list)
										
					$('#import-voter-file-dialog').modal('hide');
				} else {
					showError(invalidFileError);
				}
				_participants.ShowWait(false);
			},
			showMessage: function(message){
				$("#file-uploader-support").append("<div class='validation-error'>" + message + "</div>");
				_participants.ShowWait(false);
			},
			onUpload: function(id, fileName, xhr){
				$("#file-uploader-support").find(".validation-error").remove();
				_participants.ShowWait(true);
			}
		});

		$(".qq-uploader").css("display", "inline");
		$(".qq-upload-button").addClass("btn btn-primary").removeClass("qq-upload-button");
		$(".qq-upload-list").hide();
		$(".qq-upload-drop-area").css("margin-left", "-1000px");
		$("input[type=file]").attr("aria-label", "<spring:message code="info.uploadbutton" />");
		
		$(".filtercell input").on('keyup', function (event) {
	      if (event.keyCode === 13) {
	    	  firstVoterPage();
	      }
	    });
	});
	</c:if>
</script>
