<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<script type="text/html" id="firstrow-template">
	<tr data-bind="attr: {'data-label' : $data.Label, class:  ContentType() == 'scoring' ? (Value() != '0' ? 'firstpropertyrow quiz' : 'firstpropertyrow quiz hideme') : 'firstpropertyrow'}">
		<td class="propertylabel" data-bind="html: LabelTitle, attr: {'data-label' : $data.Label}"></td>
		
		<!--  ko if: ContentType() == 'html' -->							
			<td class="propertycontent" data-bind="html: Content"></td>
		<!-- /ko -->
		<!--  ko if: ContentType() == 'filetype' -->							
			<td class="propertycontent">
				<div data-bind="html: Content"></div>
				<a class='btn btn-xs btn-default' onclick='$(this).parent().find("input[type=text]").first().val("pdf;doc;docx;odt;txt;rtf");update($(this).parent().find("input[type=text]").first())'><spring:message code="label.TextFiles" /></a>
				<a class='btn btn-xs btn-default' onclick='$(this).parent().find("input[type=text]").first().val("png;jpg;jpeg;gif;bmp");update($(this).parent().find("input[type=text]").first())'><spring:message code="label.ImageFiles" /></a>
				<a class='btn btn-xs btn-default' onclick='$(this).parent().find("input[type=text]").first().val("xls;xlsx;ods");update($(this).parent().find("input[type=text]").first())'><spring:message code="label.TableFiles" /></a>
			</td>
		<!-- /ko -->
		<!--  ko if: ContentType() == 'radio' -->							
			<td class="propertycontent" data-bind="foreach: ContentItems">
				<label><input data-bind="checked: Selected, attr: {id: Id, name: Name, value: Value}" class='check' onclick='update(this)' type='radio' /><span data-bind="html: Label"></span></label><br />
			</td>
		<!-- /ko -->
		<!--  ko if: ContentType() == 'align' -->
			<td class="propertycontent">
				<div class="btn-group" role="group">
					<button data-bind="attr: {class: 'btn btn-default' + (Value() == 'left' ? ' active' : '' )}" onclick="update(this)" data-id="image-dialog-align-left" value="left"><span class="glyphicon glyphicon-align-left"></span></button>
					<button data-bind="attr: {class: 'btn btn-default' + (Value() == 'center' ? ' active' : '' )}" onclick="update(this)" data-id="image-dialog-align-center" value="center"><span class="glyphicon glyphicon-align-center"></span></button>
					<button data-bind="attr: {class: 'btn btn-default' + (Value() == 'right' ? ' active' : '' )}" onclick="update(this)" data-id="image-dialog-align-right" value="right"><span class="glyphicon glyphicon-align-right"></span></button>
				</div>
			</td>
		<!-- /ko -->
		<!--  ko if: ContentType() == 'select' -->
			<td class="propertycontent">
				<select data-bind="foreach: ContentItems" onchange="update(this)">
					<!--  ko if: Selected() -->
					<option data-bind="value: Value, html: Label, attr: {selected: Value}"></option>									
					<!-- /ko -->
					<!--  ko if: !Selected() -->
					<option data-bind="value: Value, html: Label"></option>									
					<!-- /ko -->
				</select>
			</td>
		<!-- /ko -->
		<!--  ko if: ContentType() == 'visibility' -->
			<td class="propertycontent">
				<div class="rightaligned">
					<span class="glyphicon glyphicon-pencil" data-toggle="tooltip" data-placement="left" title="<spring:message code="label.Edit" />" onclick="edit(this)" id="idEditVisibility"></span>
					<span class="glyphicon glyphicon-trash" data-toggle="tooltip" data-placement="left" title="<spring:message code="label.Remove" />" onclick="resetVisibility(this)" id="idRemoveVisibility"></span>
				</div>
				<div class="triggers" data-bind="html: Content"></div>
			</td>
		<!-- /ko -->
		<!--  ko if: ContentType() == 'checkbox' -->
			<td class="propertycontent">
				<input data-bind="checked: Value() == true, attr: {id: 'idProperty' + Label()}" type='checkbox' checked='checked' onclick='update(this)' />
			</td>
		<!-- /ko -->

		<!--  ko if: ContentType() == 'number' -->
			<td class="propertycontent">
				<input class="spinner" type="text" data-bind="value: ContentItems()[0].Value, attr: {id: ContentItems()[0].Id}" />
			</td>
		<!-- /ko -->

		<!--  ko if: ContentType() == 'minmax' -->
			<td class="propertycontent">
				<table class="minmaxtable">
					<tr>
						<td><spring:message code="label.min" />&nbsp;</td>
						<td><input class="spinner" style="min-width:60px" type="text" data-type="min" data-bind="value: ContentItems()[0].Value, attr: {id: ContentItems()[0].Id, 'data-to' : ContentItems()[1].Id}" /></td>
					</tr>
					<tr>
						<td><spring:message code="label.max" />&nbsp;</td>
						<td><input class="spinner" style="min-width:60px" type="text" data-type="max" data-bind="value: ContentItems()[1].Value, attr: {id: ContentItems()[1].Id, 'data-from' : ContentItems()[0].Id}" /></td>
					</tr>
				</table>
			</td>
		<!-- /ko -->
		<!--  ko if: ContentType() == 'minmaxdate' -->
			<td class="propertycontent">
				<table class="minmaxtable">
					<tr>
						<td><spring:message code="label.after" />&nbsp;</td>
						<td><input class="datepicker" type="text" data-type="min" data-bind="value: ContentItems()[0].Value, attr: {id: ContentItems()[0].Id, 'data-to' : ContentItems()[1].Id}" /></td>
					</tr>
					<tr>		
						<td><spring:message code="label.before" />&nbsp;</td>
						<td><input class="datepicker" type="text" data-type="max" data-bind="value: ContentItems()[1].Value, attr: {id: ContentItems()[1].Id, 'data-from' : ContentItems()[0].Id}" /></td>
					</tr>
				</table>
			</td>
		<!-- /ko -->
		<!--  ko if: ContentType() == 'action' -->
			<td class="propertycontent">
				<!--  ko if: Edit() -->
					<div class="rightaligned">
						<span data-bind="attr: {id: 'idEdit' + Label()}" data-toggle="tooltip" data-placement="left" title="<spring:message code="label.Edit" />" class="glyphicon glyphicon-pencil" onclick="edit(this)"></span>
						<!--  ko if: EditShortnames() -->
						<span style="margin-left: 5px;" data-toggle="tooltip" data-placement="left" title="<spring:message code="label.AssignValues" />" class="glyphicon glyphicon-tag" onclick="editShortnames(this)" id="idEditShortNamePossibleAnswers"></span>
						<!-- /ko -->
					</div>
				<!-- /ko -->				

				<div class="PreviewItems" onclick="edit(this)">
				<!--  ko foreach: PreviewItems() -->
					<div data-bind="html: getLimitedText($data)"></div>
				<!-- /ko -->
				</div>

				<button data-toggle="tooltip" title="<spring:message code="label.Add" />" class="btn btn-default btn-sm" data-bind="html: ContentItems()[0].Label, attr: {id: ContentItems()[0].Id, 'onclick' : ContentItems()[0].Value}"></button>
				<button data-toggle="tooltip" title="<spring:message code="label.Remove" />" data-bind="disable: (PreviewItems().length < 2), html: ContentItems()[1].Label, attr: {id: ContentItems()[1].Id, 'onclick' : ContentItems()[1].Value, 'class': 'btn btn-default btn-sm' + (PreviewItems().length < 2 ? ' disabled' : '')}"></button>
			</td>
		<!-- /ko -->
		<!--  ko if: ContentType() == 'quizquestion' -->
			<td class="propertycontent">
				<input class="check quizquestioncheck" name="scoring" type="checkbox" data-bind="value: '0', checked: Value(), attr: {id: 'scoring0' + Label()}" onclick='update(this);' />
			</td>
		<!-- /ko -->
		<!--  ko if: ContentType() == 'scoring' -->
			<td class="propertycontent">
				<table class="innerpropertiestable">
					<tr>
						<td class="innertd"><input class="check" name="scoring" data-bind="value: '1', checked: Value(), attr: {id: 'scoring1' + Label()}" type='radio' onclick='update(this);$(this).closest(".innerpropertiestable").find(".scoringpoints").spinner( "option", "disabled", false );$(".scoringpointsanswer").spinner( "option", "disabled", true );' /></td>
						<td class="innertd">
							<span><spring:message code="label.ForWholeQuestion" /></span>
							<a style='margin-left: 2px'><span data-toggle='tooltip' title='<spring:message code="info.ForWholeQuestion" />' class='glyphicon glyphicon glyphicon-question-sign'></span></a>
						</td>
						<td class="innertd"><input style='margin-left: 10px' type="text" data-bind="value: NumValue(), enable: Value() == '0',  attr: {id: 'scoringPoints' + Label(), class: Value() != '0' ? 'spinner scoringpoints' : 'scoringpoints'}" /></td>
					</tr>
					<tr>
						<td class="innertd"><input name="scoring" data-bind="value: '2', checked: Value(), attr: {id: 'scoring2' + Label()}" type='radio' onclick='update(this);$(this).closest(".innerpropertiestable").find(".scoringpoints").spinner( "option", "disabled", true );$(".scoringpointsanswer").spinner( "option", "disabled", false );' /></td>
						<td class="innertd" colspan="2">
							<spring:message code="label.ForEachAnswer" />		
							<a style='margin-left: 2px'><span data-toggle='tooltip' title='<spring:message code="info.ForEachAnswer" />' class='glyphicon glyphicon glyphicon-question-sign'></span></a>				
						</td>
					</tr>
					<!--  ko if: Element().type == 'MultipleChoiceQuestion' -->
						<tr>
							<td class="innertd" colspan="3" style="padding-left: 10px;">					
							<input data-label="noNegativeScore" onclick="update(this)" class="check noNegativeScore" type="checkbox" data-bind="checked: Element().noNegativeScore(), enable: Element().scoring() == 2" />
							<spring:message code="label.noNegativeScore" />
							<a style='margin-left: 2px'><span data-toggle='tooltip' title='<spring:message code="info.noNegativeScore" />' class='glyphicon glyphicon glyphicon-question-sign'></span></a>
							</td>
						</tr>
					<!-- /ko -->
				</table>
			</td>
		<!-- /ko -->	
	</tr>
</script>

<script type="text/html" id="registration-template">
	<tr class="firstpropertyrow collapsiblerow">
		<td colspan='2' style="text-align: left">
			<a onclick='toggleRegistrationFormProperties(this)'><span class='glyphicon glyphicon-minus-sign'></span>&nbsp;<spring:message code="label.RegistrationForm" /></a>
		</td>
	</tr>
	<tr class="firstpropertyrow">
		<td class="propertylabel" data-label="Attribute"><spring:message code="label.Attribute" /></td>
		<td class="propertycontent">
			<!--  ko if: Value() == "true" -->
			<input data-bind="disable: Disabled()" type='checkbox' checked='checked' onclick='update(this)' />
			<!-- /ko -->	
			<!--  ko if: Value() != "true" -->
			<input data-bind="disable: Disabled()" type='checkbox' onclick='update(this)' />
			<!-- /ko -->
		</td>
	</tr>
	<tr class="firstpropertyrow">
		<td class="propertylabel" data-label="Name"><spring:message code="label.Name" /></td>
		<td class="propertycontent">
			<input type="text" data-bind="disable: Disabled(), value: Content(), attr: {class: Disabled() ? 'disabled' : ''}" onblur='update(this)' />
		</td>
	</tr>
</script>

<script type="text/html" id="tinymce-template">
	<tr class="propertyrow tinymcerow hideme">
		<td colspan='2'>
			<textarea data-bind="html: TinyMCEContent(), attr: {id: TinyMCEId()}"></textarea>
			<div class='edittextbuttons'>
				<button data-bind="attr: {id: 'idBtnSave' + Label()}" class='btn btn-default btn-info btn-sm' onclick='save(this)'><spring:message code="label.Apply" /></button>
				<button data-bind="attr: {id: 'idBtnCancel' + Label()}" class='btn btn-default btn-sm' onclick='cancel(this);event.stopPropagation()'><spring:message code="label.Cancel" /></button>
			</div>
		</td>
	</tr>
</script>

<script type="text/html" id="PossibleAnswerShortnames-template">
	<tr class="propertyrow hideme">
		<td class="propertycontent" colspan='2'>
			<table class="table table-bordered propertiessubtable"></table>
			<div class='editvaluesbuttons'><button id='idBtnSaveShortName' class='btn btn-default btn-info btn-sm' onclick='save(this)'><spring:message code="label.Apply" /></button> <button id='idBtnCancelShortName' class='btn btn-default btn-sm' onclick='cancel(this);event.stopPropagation()'><spring:message code="label.Cancel" /></button></div>
		</td>
	</tr>		
</script>

<script type="text/html" id="advanced-template">	
	<tr class="firstpropertyrow collapsiblerow">
		<td colspan='2' style="text-align: left">
			<a class='advancedtogglebutton' onclick='toggleAdvancedProperties(this)'><span class='glyphicon glyphicon-minus-sign'></span>&nbsp;<spring:message code="label.Advanced" /></a>
		</td>
	</tr>		
</script>

<script type="text/html" id="quiz-template">	
	<tr class="firstpropertyrow collapsiblerow quiz">
		<td colspan='2' style="text-align: left; border-bottom: 0px;">
			<div class="toolboxheader">
				<a>
					<span id="quizpropertiescollapsebutton" class="glyphicon glyphicon-chevron-down" onclick="showHideQuizProperties(this)"></span>
					<spring:message code="label.QuizProperties" />
				</a>
			</div>
		</td>
	</tr>		
</script>

<script type="text/html" id="quizanswers-template">	
	<tr class="firstpropertyrow quiz" data-bind="attr: {style: Element().scoring() == 0 ? 'display:none' : ''}">
		<td class="propertylabel" style="border-bottom: 0px;"><spring:message code="label.Answers" /></td>
		<td style="text-align: right; border-bottom: 0px;">
			<!-- ko if: Element().type != 'MultipleChoiceQuestion' && Element().type != 'SingleChoiceQuestion' -->
				<a data-bind="click: function(data, event) { addExpectedAnswer(true) }" class="btn btn-default btn-sm" title="<spring:message code="label.AddExpectedAnswer" />" data-toggle="tooltip"><span class="glyphicon glyphicon-plus"></span></a>
			<!-- /ko -->
		</td>
	</tr>
	<!--  ko foreach: ContentItems() -->
	<!-- ko if: $parent.Element().type == 'MultipleChoiceQuestion' || $parent.Element().type == 'SingleChoiceQuestion' -->
		<tr class="quiz" data-bind="attr: {style: $parent.Element().scoring() == 0 ? 'display:none' : ''}">
			<td class="propertylabel" style="border-bottom: 0px; vertical-align: middle !important; padding-bottom: 10px !important;"></td>
			<td class="propertycontent" style="border-bottom: 0px; padding-bottom:10px !important">			
				<span data-bind="html: title"></span>
			</td>
		</tr>	
		<tr class="quiz quizrule" data-bind="attr: {'data-id': id(), style: $parent.Element().scoring() == 0 ? 'display:none' : ''}">
			<td class="propertylabel" style="border-bottom: 0px; padding-top: 10px !important;"><spring:message code="label.MarkAs" /><a style='margin-left: 2px'><span data-toggle='tooltip' title='<spring:message code="info.MarkAs" />' class='glyphicon glyphicon glyphicon-question-sign'></span></a></td>
			<td class="propertycontent" style="border-bottom: 0px; padding-top: 10px !important;">
				<input class="check" value="1" type="radio" data-bind="checkedValue: true, checked: scoring.correct, attr: {name: 'correct' + id()}" data-label="correct" data-value="true"  onclick="update(this)" /><spring:message code="label.Correct" /><span style="color: #0f0; margin-left: 5px;" class="glyphicon glyphicon-ok"></span><br />
				<input class="check" value="0" type="radio" data-bind="checkedValue: false, checked: scoring.correct, attr: {name: 'correct' + id()}" data-label="correct" data-value="false" onclick="update(this)" /><spring:message code="label.Incorrect" /><span style="color: #f00; margin-left: 5px;" class="glyphicon glyphicon-remove"></span>
			</td>
		</tr>		
	<!-- /ko -->
	<!-- ko if: $parent.Element().type != 'MultipleChoiceQuestion' && $parent.Element().type != 'SingleChoiceQuestion' -->
		<tr class="quiz quizrule" data-bind="attr: {'data-id': id(), style: $parent.Element().scoring() == 0 ? 'display:none' : ''}">
			<td class="propertylabel" style="border-bottom: 0px; padding-top: 10px !important;">
				<spring:message code="label.MarkAs" /><a style='margin-left: 2px'><span data-toggle='tooltip' title='<spring:message code="info.MarkAs" />' class='glyphicon glyphicon glyphicon-question-sign'></span></a>
			</td>
			<td style="border-bottom: 0px;">
				<div style="float: right">
					<a class="btn btn-default btn-sm" data-toggle='tooltip' title="<spring:message code="label.RemoveExpectedAnswer" />" data-bind="click: $parent.removeExpectedAnswer">
					<span class="glyphicon glyphicon-minus"></span>
				</a>
				</div>
				<div>
					<input class="check" value="1" type="radio" data-bind="checkedValue: true, checked: scoring.correct(), attr: {name: 'correct' + id()}" data-label="correct" data-value="true"  onclick="update(this)" /><spring:message code="label.Correct" /><span style="color: #0f0; margin-left: 5px;" class="glyphicon glyphicon-ok"></span><br />
					<input class="check" value="0" type="radio" data-bind="checkedValue: false, checked: scoring.correct(), attr: {name: 'correct' + id()}" data-label="correct" data-value="false" onclick="update(this)" /><spring:message code="label.Incorrect" /><span style="color: #f00; margin-left: 5px;" class="glyphicon glyphicon-remove"></span>
				</div>				
			</td>
		</tr>
		<tr class="quiz" data-bind="attr: {'data-id': id(), style: $parent.Element().scoring() == 0 ? 'display:none' : ''}">
			<td class="propertylabel" style="border-bottom: 0px; padding-top: 12px !important;"><spring:message code="label.ValueIs" /></td>
			<td class="propertycontent" style="border-bottom: 0px;">			
				<select class="ruleValueType" data-label="ruleValueType" onchange="change(this, event)" data-bind="options: $parent.availableValueTypes(), optionsText: function(item) { return $parent.Element().type == 'DateQuestion' ? getPropertyLabel(item + 'Date') : getPropertyLabel(item)}, value: scoring.typeAsOption(), attr: {'data-value': scoring.typeAsOption()}">
				</select>
				<span data-bind="attr: {style: scoring.type() > -1 && scoring.type() != 8 ? 'display: inline-block' : 'display: none'}">		
					<input data-label="ruleValue" data-bind="value: scoring.value(), attr: {style: $parent.Element().type == 'FreeTextQuestion' || $parent.Element().type == 'DateQuestion' ? '' : 'width: 50px;'}" type="text" onchange="update(this)" onblur="update(this)" />
					<span data-bind="attr: {style: scoring.type() == 5 ? '' : 'display: none'}"><spring:message code="label.and" /></span>
					<input data-label="ruleValue2" data-bind="value: scoring.value2(), attr: {style: scoring.type() == 5 ? ($parent.Element().type == 'FreeTextQuestion' || $parent.Element().type == 'DateQuestion' ? '' : 'width: 50px;') : 'display: none'}" type="text" onchange="update(this)" onblur="update(this)" />
				</span>		
			</td>
		</tr>
	<!-- /ko -->
	<tr class="quiz" data-bind="attr: {'data-id': id(), style: $parent.Element().scoring() == 0 ? 'display:none' : ''}">
		<td  class="propertylabel" style="border-bottom: 0px; vertical-align: middle !important;"><spring:message code="label.Points" /></td>
		<td class="propertycontent" style="border-bottom: 0px;">			
			<input data-label="points" data-bind="value: scoring.points()" class="scoringpointsanswer spinner" type="text" style="width: 100%" onclick="update(this)" />
		</td>
	</tr>
	<tr class="firstpropertyrow quiz" data-bind="attr: {'data-id': id(), style: $parent.Element().scoring() == 0 ? 'display:none' : ''}">
		<td class="propertylabel" data-label="feedback" style="padding-top:10px !important; border-bottom: 0px;">
			<spring:message code="label.Feedback" />
			<a style='margin-left: 2px'><span data-toggle='tooltip' title='<spring:message code="info.Feedback" />' class='glyphicon glyphicon glyphicon-question-sign'></span></a>
		</td>
		<td class="propertycontent" style="padding-top:10px !important; border-bottom: 0px;">
			<div class="rightaligned">
				<span class="glyphicon glyphicon-pencil" data-toggle="tooltip" data-placement="left" title="<spring:message code="label.Edit" />" onclick="edit(this)"></span>
				<span class="glyphicon glyphicon-trash" data-toggle="tooltip" data-placement="left" title="<spring:message code="label.Remove" />" onclick="resetFeedback(this)"></span>
			</div>
			<div data-bind="html: scoring.feedback()"></div>
		</td>
	</tr>
	<tr class="propertyrow tinymcerow hideme quiz" data-bind="attr: {'data-id':id()}">
		<td colspan='2' style="border-bottom: 0px;">
			<textarea data-bind="html: scoring.feedback(), attr: {id: 'feedback' + id()}"></textarea>
			<div class='edittextbuttons'>
				<button data-bind="attr: {id: 'idBtnSaveFeedback'}" class='btn btn-default btn-info btn-sm' onclick='save(this)'><spring:message code="label.Apply" /></button>
				<button data-bind="attr: {id: 'idBtnCancelFeedback'}" class='btn btn-default btn-sm' onclick='cancel(this);event.stopPropagation()'><spring:message code="label.Cancel" /></button>
			</div>
		</td>
	</tr>
	<tr data-bind="attr: {style: $parent.Element().scoring() == 0 ? 'display:none' : ''}">
		<td colspan="2" style="border-bottom: 1px dashed #ccc; text-align: right"></td>
	</tr>
	<!-- /ko -->
	<!-- ko if: Element().type != 'MultipleChoiceQuestion' && Element().type != 'SingleChoiceQuestion' && ContentItems().length > 0 -->
	<tr data-bind="attr: {style: Element().scoring() == 0 ? 'display:none' : ''}">
		<td colspan="2" style="text-align: right">	
			<a data-bind="click: function(data, event) { addExpectedAnswer(true) }" class="btn btn-default btn-sm" title="<spring:message code="label.AddExpectedAnswer" />" data-toggle="tooltip"><span class="glyphicon glyphicon-plus"></span></a>
		</td>
	</tr>
	<!-- /ko -->
</script>