<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div id="satargetdatasets" data-bind="childrenComplete: myPostProcessingLogic" style="padding: 20px;">	

	<div style="display: flex; flex-direction: row; gap: 60px;">
		<div style="display: flex; flex-direction: row; gap: 20px; background-color: #ddd; border-radius: 5px; padding: 15px;">
			<div>
				<label style="margin-bottom: 0;"><spring:message code="label.CreateNewDataset" /></label>			
				<input class="form-control" type="text" id="satargetdatasetname" maxlength="120" data-bind="event: { keydown: checkKeyCreateTargetDataset }" />			
			</div>
			<div style="margin-top: 22px; margin-left: 10px;">
				<button data-bind="click: createTargetDataset" class="btn btn-success"><spring:message code="label.Create" /></button>
			</div>
		</div>
	</div>
	
	<div>
		<!--  ko if: targetDatasets().length == 0 -->
			<div style="margin: 20px;"><spring:message code="info.NoTargetDatasets" /></div>
		<!-- /ko -->
	
		<!--  ko if: targetDatasets().length > 0 -->
		<table id="satargetdatasetstable" class="table table-striped table-bordered" style="margin-top: 20px; table-layout:fixed">
			<thead>
				<tr>
					<th><spring:message code="label.Name" /></th>
					<th style="width: 170px"><spring:message code="label.Actions" /></th>
				</tr>
			</thead>
			<tbody data-bind="foreach: targetDatasets()">
				<tr data-bind="attr: {'data-id': id}">
					<td data-bind="click: (data, event) => { $parent.enableTargetDatasetEditMode($data); }">
						<span class="content" data-bind="text: name"></span>
						<input class="form-control inline editmode" style="display: none" type="text" maxlength="120" data-bind="textInput: name, event: { keydown: $parent.checkKeyTargetDataset }" />
						<div class="validation-error-already-exists validation-error hideme"><spring:message code="message.TargetDatasetAlreadyExists" /></div>
						<div class="validation-error-empty-name validation-error hideme"><spring:message code="message.TargetDatasetNameEmpty" /></div>
					</td>
					<td>
						<a data-bind="click: () => openScoreCardsTab($data)" data-toggle="tooltip" title="<spring:message code='label.OpenScoreCard'/>" style="padding-right: 5px"><spring:message code='label.ScoreCard'/></a>
						<!-- ko if: $parent.targetDatasetInEditMode() != null && $parent.targetDatasetInEditMode().parent().parent().attr("data-id") == id() -->
							<a class="iconbutton" data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Discard" />" data-bind="click: () => { $parent.discardChanges($data); }"><span class="glyphicon glyphicon-ban-circle"></span></a>
							<a class="iconbutton" data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.SaveAndClose" />" data-bind="click: () => { $parent.checkValidation($parent.targetDatasetInEditMode()); }"><span class="glyphicon glyphicon-ok"></span></a>
						<!-- /ko -->
						<!-- ko if: $parent.targetDatasetInEditMode() == null || ($parent.targetDatasetInEditMode() != null && $parent.targetDatasetInEditMode().parent().parent().attr("data-id") != id()) -->
							<a class="iconbutton" data-toggle="tooltip" title="<spring:message code="label.Edit" />" data-bind="click: () => { $parent.enableTargetDatasetEditMode($data); }"><span class="glyphicon glyphicon-pencil"></span></a>
							<a class="iconbutton" data-toggle="tooltip" title="<spring:message code="label.Remove" />" data-bind="click: () => { $parent.deleteTargetDataset(id()); }"><span class="glyphicon glyphicon-remove"></span></a>
						<!-- /ko -->
					</td>
				</tr>
				
			</tbody>
		</table>
		<!-- /ko -->	
	</div>
	
	<div class="modal" id="delete-satargetdataset-dialog">
		<div class="modal-dialog">
	   	<div class="modal-content">
		<div class="modal-body">
			<spring:message code="question.DeleteTargetDataset" />
		</div>
		<div class="modal-footer">
			<img id="delete-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a id="deleteSingleContributionConfirm" data-bind="click: finallyDeleteTargetDataset"  class="btn btn-primary"><spring:message code="label.Yes" /></a>
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>					
		</div>
		</div>
		</div>
	</div>
		
</div>

<style>

</style>


<script type="text/javascript"> 
	const targetDatasetNameAlreadyExists = '<spring:message code="message.TargetDatasetAlreadyExists" />';
	const targetDatasetNameEmpty =  '<spring:message code="message.TargetDatasetNameEmpty" />';
		
	function saTargetDatasetsViewModel() {
		let root = this;
		
		this.targetDatasets = ko.observableArray([]);
		this.originalTargetDatasets = ko.observableArray([]);
		this.targetDatasetToDelete = ko.observable(0);
		this.originalName = ko.observable("");
		this.targetDatasetInEditMode = ko.observable(null);
			
		this.myPostProcessingLogic = function() {
			$('[data-toggle="tooltip"]').tooltip();
		}
		
		this.checkKeyTargetDataset = function(data, e) {
			if (e.key === 'Enter' || e.keyCode === 13) {
				root.checkValidation(root.targetDatasetInEditMode());
		    }
			if (e.key === "Escape" || e.keyCode === 27) {
				root.discardChanges(data);
		    }
			return true;
		}
		
		this.checkKeyCreateTargetDataset = function(data, e) {
			if (e.key === 'Enter' || e.keyCode === 13) {
				root.createTargetDataset();
		    }
			return true;
		}

		this.checkValidation = function(element) {
			const selectedTargetDatasetId = $(element).parent().parent().attr("data-id");
			if (selectedTargetDatasetId > 0) {
				const tr = $("#satargetdatasetstable").find("tr[data-id=" + selectedTargetDatasetId + "]").first();
				$(tr).find('.validation-error-already-exists').hide();
				$(tr).find('.validation-error-empty-name').hide();

				const updatedName = $("#satargetdatasetstable").find("tr[data-id=" + selectedTargetDatasetId + "]").first().find("input").first().val();
				if (updatedName.length == 0) {
					$("#satargetdatasetstable").find("tr[data-id=" + selectedTargetDatasetId + "]").first().find('.validation-error-empty-name').show();
					return false;
				}

				if (updatedName != root.originalName()) {
					let result = true;
					ko.utils.arrayForEach(root.targetDatasets(), function (targetDataset) {
						if (targetDataset.id() != selectedTargetDatasetId && targetDataset.name() == updatedName) {
							$("#satargetdatasetstable").find("tr[data-id=" + selectedTargetDatasetId + "]").first().find('.validation-error-already-exists').show();
							result = false;
						}
					});

					if (!result) return false;

					root.postTargetDataset(updatedName, selectedTargetDatasetId)
				} else {
					root.toggleTargetDatasetEditMode(selectedTargetDatasetId);
				}
			}

			return true;
		}

		this.createTargetDataset = function() {
			if ($('#satargetdatasetname').val().length == 0) {
				showError(targetDatasetNameEmpty);
				return;
			}

			root.postTargetDataset($('#satargetdatasetname').val(), 0, "", "");
		}

		this.deleteTargetDataset = function(id) {
			root.targetDatasetToDelete(id);
			$('#delete-satargetdataset-dialog').modal('show');
		}

		this. discardChanges = function(data) {
			const tr = $("#satargetdatasetstable").find("tr[data-id=" + data.id() + "]").first();
			$(tr).find('.validation-error-already-exists').hide();
			$(tr).find('.validation-error-empty-name').hide();

			// reset changes and deactivate edit mode
			const original = root.originalTargetDatasets().find(obj => {
				return obj.id() === data.id();
			})

			data.name(original.name());
			root.toggleTargetDatasetEditMode(data.id());
		}

		this.enableTargetDatasetEditMode = function(data) {
			if (root.targetDatasetInEditMode() != null) {
				if (!this.checkValidation(root.targetDatasetInEditMode()))
				{
					return;
				}
			}
						
			const tr = $("#satargetdatasetstable").find("tr[data-id=" + data.id() + "]").first();
			const input = $(tr).find("input").first();

			root.originalName(data.name());
			$(tr).find(".content").hide();
			$(tr).find("input").first().show().focus();

			root.targetDatasetInEditMode(input);
			$('[data-toggle="tooltip"]').tooltip();
		}

		this.finallyDeleteTargetDataset = function() {
			var s = "id=" + root.targetDatasetToDelete();
			$.ajax({
				type:'POST',
				beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
				url: contextpath + "/" + surveyUID + "/management/selfassessment/deletetargetdataset",
				data: s,
				dataType: 'json',
				cache: false,
				success: function(success) {
					if (!success) {
						showError(errorOperationFailed);
					} else {
						root.loadTargetDatasets();
						saScoresModel.loadScores();
					}
					$('#delete-satargetdataset-dialog').modal('hide');
				}, error: function (data) {
					showAjaxError(data.status);
					$('#delete-satargetdataset-dialog').modal('hide');
				}});
		}

		this.loadTargetDatasets = function() {
			root.targetDatasets.removeAll();
			saScoresModel.targetDatasets.removeAll();
			root.originalTargetDatasets.removeAll();

			$.ajax({
				type:'GET',
				url: "${contextpath}/${form.survey.shortname}/management/selfassessment/targetdatasets",
				dataType: 'json',
				cache: false,
				async: false,
				success: function (datasets) {
					for (let i = 0; i < datasets.length; i++) {
						const c = new saTargetDatasetViewModel();
						c.id(datasets[i].id);
						c.name(datasets[i].name);

						root.targetDatasets.push(c);
						saScoresModel.targetDatasets.push(c);

						const copy = new saTargetDatasetViewModel();
						copy.id(datasets[i].id);
						copy.name(datasets[i].name);

						root.originalTargetDatasets.push(copy);
					}

					root.targetDatasetInEditMode(null);
					root.myPostProcessingLogic();
				}
			});
		}

		this.postTargetDataset = function(name, id) {
			var s = "name=" + encodeURIComponent(name);

			if (id > 0) {
				s += "&id=" + id;
			}

			var url = contextpath + "/" + surveyUID + "/management/selfassessment/" + (id > 0 ? "updatetargetdataset" : "createtargetdataset");

			$.ajax({
				type:'POST',
				beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
				url: url,
				data: s,
				dataType: 'text',
				async: false,
				cache: false,
				success: function(result) {
					if (result == 'NAMEALREADYEXISTS') {
						showError(targetDatasetNameAlreadyExists);
					} else if (result != "OK") {
						showError(errorOperationFailed);
					} else {
						$('#satargetdatasetname').val("");
						root.loadTargetDatasets();
						saScoresModel.loadScores();
			  		}
				}, error: function (data) {
					showAjaxError(data.status)
				}});
		}

		this.saveTargetDataset = function(dataset) {
			root.postTargetDataset(dataset.name(), "", dataset.id());
		}

		this.toggleTargetDatasetEditMode = function(id) {
			const tr = $("#satargetdatasetstable").find("tr[data-id=" + id + "]").first();
			$(tr).find(".content").toggle();
			$(tr).find("input").toggle();

			root.targetDatasetInEditMode(null);
			$('[data-toggle="tooltip"]').tooltip();
		}
	}
	
	function saTargetDatasetViewModel() {
		this.id = ko.observable(0);
		this.name = ko.observable('');
	}

	let saTargetDatasetsModel = new saTargetDatasetsViewModel();
	ko.applyBindings(saTargetDatasetsModel, $('#satargetdatasets')[0]);
	
	$(function() {
		saTargetDatasetsModel.loadTargetDatasets();
	});
	
</script>
