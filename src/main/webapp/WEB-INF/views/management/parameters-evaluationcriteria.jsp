<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div id="sacriteria" data-bind="childrenComplete: myPostProcessingLogic" style="padding: 20px;">	

	<c:if test="${USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('FormManagement') > 1}">
		<div style="display: flex; flex-direction: row; gap: 60px;">
			<div style="display: flex; flex-direction: row; gap: 20px; background-color: #ddd; border-radius: 5px; padding: 15px;">
				<div>
					<label style="margin-bottom: 0;"><spring:message code="label.CreateNewCriterion" /></label>
					<input class="form-control" type="text" id="sacriterionname" maxlength="120" data-bind="event: { keydown: checkKeyCreateCriterion }" />
				</div>
				<div style="margin-top: 22px; margin-left: 10px;">
					<button data-bind="click: createCriterion" class="btn btn-success"><spring:message code="label.Create" /></button>
				</div>
			</div>
		</div>
	</c:if>
	
	<div>
		<!--  ko if: criteria().length == 0 -->
			<div style="margin: 20px;"><spring:message code="info.NoCriteria" /></div>
		<!-- /ko -->
	
		<!--  ko if: criteria().length > 0 -->
		<table id="sacriteriatable" class="table table-striped table-bordered" style="margin-top: 20px; table-layout:fixed">
			<thead>
				<tr>
					<th style="width: 50%"><spring:message code="label.Name" /></th>
					<th style="width: 80px"><spring:message code="label.Acronym" /></th>
					<th style="width: 50%"><spring:message code="label.Type" /></th>
					<th style="width: 80px"><spring:message code="label.Actions" /></th>
				</tr>
			</thead>
			<tbody data-bind="foreach: criteria()">
				<tr data-bind="attr: {'data-id': id}">
					<td <c:if test="${(USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('FormManagement') > 1)}">data-bind="click: (data, event) => {$parent.enableCriteriaEditMode($data, event.target); }"</c:if> >
						<span class="content" data-bind="text: name" style="word-break: break-all;" <c:if test="${(USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('FormManagement') > 1)}">data-bind="click: $parent.toggleCriteriaEditMode($data.id())"</c:if> ></span>
						<textarea class="form-control inline editmode" style="display: none; word-break: break-all; resize: none;" maxlength="120" type="text" data-bind="textInput: name, event: { keydown: $parent.checkKeyCriteria }" ></textarea>
						<div class="validation-error-already-exists validation-error hideme"><spring:message code="message.CriterionAlreadyExists" /></div>
						<div class="validation-error-empty-name validation-error hideme"><spring:message code="message.CriterionNameEmpty" /></div>
					</td>
					<td <c:if test="${(USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('FormManagement') > 1)}">data-bind="click: (data, event) => {$parent.enableCriteriaEditMode($data, event.target); }"</c:if> >
						<span class="content" data-bind="text: acronym" style="word-break: break-all;" ></span>
						<textarea class="form-control inline editmode" style="display: none; width: 70px; word-break: break-all; resize: none;" maxlength="120" type="text" data-bind="textInput: acronym, event: { keydown: $parent.checkKeyCriteria }" ></textarea>
						<div class="validation-error-empty-acronym validation-error hideme"><spring:message code="message.CriterionAcronymEmpty" /></div>
					</td>
					<td <c:if test="${(USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('FormManagement') > 1)}">data-bind="click: (data, event) => {$parent.enableCriteriaEditMode($data, event.target); }"</c:if> >
						<span class="content" data-bind="text: type" style="word-break: break-all;" ></span>
						<textarea class="form-control inline editmode criteriontype" style="display: none; width: 200px; word-break: break-all; resize: none;" maxlength="120" type="text" data-bind="textInput: type, event: { keydown: $parent.checkKeyCriteria }" ></textarea>
						<div class="validation-error-empty-type validation-error hideme"><spring:message code="message.CriterionTypeEmpty" /></div>
					</td>
					<td>
						<c:choose>
							<c:when test="${USER.formPrivilege > 1 || form.survey.owner.id == USER.id || USER.getLocalPrivilegeValue('FormManagement') > 1}">
								<!-- ko if: $parent.criterionInEditMode() != null && $parent.criterionInEditMode().attr("data-id") == id() -->
									<a class="iconbutton" data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Discard" />" data-bind="click: () => { $parent.discardChanges($data); }"><span class="glyphicon glyphicon-ban-circle"></span></a>
									<a class="iconbutton" data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.SaveAndClose" />" data-bind="click: () => { $parent.checkValidation($parent.criterionInEditMode()); }"><span class="glyphicon glyphicon-ok"></span></a>
								<!-- /ko -->
								<!-- ko if: $parent.criterionInEditMode() == null || ($parent.criterionInEditMode() != null && $parent.criterionInEditMode().attr("data-id") != id()) -->
									<a class="iconbutton" data-toggle="tooltip" title="<spring:message code="label.Edit" />" data-bind="click: () => { $parent.enableCriteriaEditMode($data); }"><span class="glyphicon glyphicon-pencil"></span></a>
									<a class="iconbutton" data-toggle="tooltip" title="<spring:message code="label.Remove" />" data-bind="click: () => { $parent.deleteCriterion(id()); }"><span class="glyphicon glyphicon-remove"></span></a>
								<!-- /ko -->
							</c:when>
							<c:otherwise>
								<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Edit" />"><span class="glyphicon glyphicon-pencil"></span></a>
								<a class="iconbutton disabled" data-toggle="tooltip" title="<spring:message code="label.Remove" />"><span class="glyphicon glyphicon-remove"></span></a>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				
			</tbody>
		</table>
		<!-- /ko -->	
	</div>
	
	<div class="modal" id="delete-sacriterion-dialog">
		<div class="modal-dialog">
	   	<div class="modal-content">
		<div class="modal-body">
			<spring:message code="question.DeleteCriterion" />
		</div>
		<div class="modal-footer">
			<img id="delete-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a id="deleteSingleContributionConfirm" data-bind="click: finallyDeleteCriterion"  class="btn btn-primary"><spring:message code="label.Yes" /></a>
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>					
		</div>
		</div>
		</div>
	</div>
	
		
</div>

<style>

</style>


<script type="text/javascript"> 
	let criterionNameAlreadyExists = '<spring:message code="message.CriterionAlreadyExists" />';
	let criterionNameEmpty =  '<spring:message code="message.CriterionNameEmpty" />';
		
	function saCriteriaViewModel() {
		let root = this;
		
		this.criteria = ko.observableArray([]);
		this.originalCriteria = ko.observableArray([]);
		this.criterionToDelete = ko.observable(0);
		this.originalName = ko.observable("");
		this.originalAcronym = ko.observable("");
		this.originalType = ko.observable("");
		this.criterionInEditMode = ko.observable(null);
			
		this.myPostProcessingLogic = function() {
			$('[data-toggle="tooltip"]').tooltip();
		}
		
		this.checkKeyCriteria = function(data, e) {
			if (e.key === 'Enter' || e.keyCode === 13) {
				e.preventDefault();
				root.checkValidation(root.criterionInEditMode())
		    }
			if (e.key === "Escape" || e.keyCode === 27) {
				root.discardChanges(data)
		    }
			return true;
		}
		
		this.checkKeyCreateCriterion = function(data, e) {
			if (e.key === 'Enter' || e.keyCode === 13) {
				root.createCriterion();
		    }
			return true;
		}

		this.checkValidation = function(element) {
			let selectedCriterionId = $(element).attr("data-id");

			if (selectedCriterionId > 0) {
				let tr = $("#sacriteriatable").find("tr[data-id=" + selectedCriterionId + "]");
				$(tr).find('.validation-error-already-exists').hide();
				$(tr).find('.validation-error-empty-name').hide();
				$(tr).find('.validation-error-empty-acronym').hide();
				$(tr).find('.validation-error-empty-type').hide();

				let updatedName = $(tr).find("td").eq(0).find("textarea").first().val();
				if (updatedName.length == 0) {
					$(tr).find("td").eq(0).find('.validation-error-empty-name').show();
					return false;
				}
				let updatedAcronym = $(tr).find("td").eq(1).find("textarea").first().val();
				if (updatedAcronym.length == 0) {
					$(tr).find('.validation-error-empty-acronym').show();
					return false;
				}
				let updatedType = $(tr).find("td").eq(2).find("textarea").first().val();
				if (updatedType.length == 0) {
					$(tr).find('.validation-error-empty-type').show();
					return false;
				}

				let errorOccured = false;
				if (updatedName != root.originalName() || updatedAcronym != root.originalAcronym() || updatedType != root.originalType()) {
					ko.utils.arrayForEach(root.criteria(), function (criterion) {
						if (criterion.id() != selectedCriterionId && criterion.name() == updatedName) {
							$(tr).find('.validation-error-already-exists').show();
							errorOccured = true;
						}
					});

					if (errorOccured) return false;

					root.postCriterion(updatedName, selectedCriterionId, updatedAcronym, updatedType);
				} else {
					root.toggleCriteriaEditMode(selectedCriterionId);
				}
			}

			return true;
		}

		this.createCriterion = function() {
			if ($('#sacriterionname').val().length == 0) {
				showError(criterionNameEmpty);
				return;
			}

			root.postCriterion($('#sacriterionname').val(), 0, "", "");
		}

		this.deleteCriterion = function(id) {
			root.criterionToDelete(id);
			$('#delete-sacriterion-dialog').modal('show');
		}

		this.discardChanges = function(data) {
			let tr = $("#sacriteriatable").find("tr[data-id=" + data.id() + "]");
			$(tr).find('.validation-error-already-exists').hide();
			$(tr).find('.validation-error-empty-name').hide();
			$(tr).find('.validation-error-empty-acronym').hide();
			$(tr).find('.validation-error-empty-type').hide();

			// reset changes and deactivate edit mode
			let original = root.originalCriteria().find(obj => {
				return obj.id() === data.id();
			})

			data.name(original.name());
			data.acronym(original.acronym());
			data.type(original.type());
			root.toggleCriteriaEditMode(data.id());
		}

		this.enableCriteriaEditMode = function(data, td) {
			if ($(td).is("textarea") || $(td).hasClass("content")) {
				td = $(td).parent();
			}
			
			let index = 0;
			if (td != undefined) {
				index = $(td).index();
			}

			if (root.criterionInEditMode() != null) {
				if (!this.checkValidation(root.criterionInEditMode()))
				{
					return;
				}
			}

			const tr = $("#sacriteriatable").find("tr[data-id=" + data.id() + "]");
			td = tr.find("td")[index];

			root.originalName(data.name());
			root.originalAcronym(data.acronym());
			root.originalType(data.type());

			$(tr).find(".content").hide();
			$(tr).find("textarea").show();
			$(td).find("textarea").focus();

			root.criterionInEditMode(tr);
			$('[data-toggle="tooltip"]').tooltip();
		}

		this.finallyDeleteCriterion = function() {
			var s = "id=" + root.criterionToDelete();
			$.ajax({
				type:'POST',
				beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
				url: contextpath + "/" + surveyUID + "/management/selfassessment/deletecriterion",
				data: s,
				dataType: 'json',
				cache: false,
				success: function(success) {
					if (!success) {
						showError(errorOperationFailed);
					} else {
						root.loadCriteria();
						saScoresModel.loadScores();
					}
					$('#delete-sacriterion-dialog').modal('hide');
				}, error: function (data) {
					showAjaxError(data.status);
					$('#delete-sacriterion-dialog').modal('hide');
				}});
		}

		this.loadCriteria = function() {
			root.criteria.removeAll();
			saScoresModel.criteria.removeAll();
			root.originalCriteria.removeAll();

			$.ajax({
				type:'GET',
				url: "${contextpath}/${form.survey.shortname}/management/selfassessment/criteria",
				dataType: 'json',
				cache: false,
				async: false,
				success: function (criteria) {
					for (let i = 0; i < criteria.length; i++) {
						let c = new saCriterionViewModel();
						c.id(criteria[i].id);
						c.name(criteria[i].name);
						c.acronym(criteria[i].acronym);
						c.type(criteria[i].type);

						root.criteria.push(c);
						saScoresModel.criteria.push(c);

						let copy = new saCriterionViewModel();
						copy.id(criteria[i].id);
						copy.name(criteria[i].name);
						copy.acronym(criteria[i].acronym);
						copy.type(criteria[i].type);

						root.originalCriteria.push(copy);
					}

					$(".criteriontype").autocomplete({
						source: "${contextpath}/${form.survey.uniqueId}/management/selfassessment/types",
						focus: function( event, ui ) {
							$(event.target).val(ui.item.value).change();
						},
						select: function( event, ui ) {
							$(event.target).val(ui.item.value).change();
						}
					});

					root.criterionInEditMode(null);
					root.myPostProcessingLogic();
				}
			});
		}

		this.postCriterion = function(name, id, acronym, type) {
			var s = "name=" + encodeURIComponent(name);

			if (id > 0) {
				s += "&id=" + id;
			}

			if (acronym.length > 0) {
				s += "&acronym=" + encodeURIComponent(acronym);
			}

			if (type.length > 0) {
				s += "&type=" + encodeURIComponent(type);
			}

			var url = contextpath + "/" + surveyUID + "/management/selfassessment/" + (id > 0 ? "updatecriterion" : "createcriterion");

			$.ajax({
				type:'POST',
				beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
				url: url,
				data: s,
				dataType: 'text',
				cache: false,
				async: false,
				success: function(result) {
					if (result == 'NAMEALREADYEXISTS') {
						showError(criterionNameAlreadyExists);
					} else if (result != "OK") {
						showError(errorOperationFailed);
					} else {
						$('#sacriterionname').val("");
						root.loadCriteria();
						saScoresModel.loadScores();
					}
				}, error: function (data) {
					showAjaxError(data.status)
				}});
		}

		this.saveCriterion = function(criterion) {
			root.postCriterion(criterion.name(), criterion.id(), criterion.acronym(), criterion.type());
		}

		this.toggleCriteriaEditMode = function(id) {
			const tr = $("#sacriteriatable").find("tr[data-id=" + id + "]");
			$(tr).find(".content").toggle();
			$(tr).find("textarea").toggle();

			root.criterionInEditMode(null);
			$('[data-toggle="tooltip"]').tooltip();
		}
	}
	
	function saCriterionViewModel() {
		this.id = ko.observable(0)
		this.name = ko.observable('')
		this.acronym = ko.observable('')
		this.type = ko.observable('')
	}

	let saCriteriaModel = new saCriteriaViewModel();
	ko.applyBindings(saCriteriaModel, $('#sacriteria')[0]);
	
	$(function() {
		saCriteriaModel.loadCriteria();
	});
	
</script>

<style>
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
</style>
