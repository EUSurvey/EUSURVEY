<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<script type="text/html" id="firstrow-template">
	<tr data-bind="attr: {'data-label' : $data.Label, class:  ContentType() == 'scoring' ? (Value() != '0' ? 'firstpropertyrow quiz' : 'firstpropertyrow quiz hideme') : 'firstpropertyrow'}, visible: IsVisible">
		<td class="propertylabel" data-bind="html: LabelTitle, attr: {'data-label' : $data.Label}"></td>
		
		<!--  ko if: ContentType() == 'html' -->
			<td class="propertycontent">
				<form autocomplete="off">
					<div data-bind="html: Content"></div>
				</form>
			</td>
		<!-- /ko -->
		<!--  ko if: ContentType() == 'filetype' -->
			<td class="propertycontent">
				<form autocomplete="off">
					<div data-bind="html: Content"></div>
					<a class='btn btn-xs btn-default' onclick='$(this).parent().find("input[type=text]").first().val("pdf;doc;docx;odt;txt;rtf");update($(this).parent().find("input[type=text]").first())'><spring:message code="label.TextFiles" /></a>
					<a class='btn btn-xs btn-default' onclick='$(this).parent().find("input[type=text]").first().val("png;jpg;jpeg;gif;bmp");update($(this).parent().find("input[type=text]").first())'><spring:message code="label.ImageFiles" /></a>
					<a class='btn btn-xs btn-default' onclick='$(this).parent().find("input[type=text]").first().val("xls;xlsx;ods");update($(this).parent().find("input[type=text]").first())'><spring:message code="label.TableFiles" /></a>
				</form>
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
					<span class="glyphicon glyphicon-trash" data-toggle="tooltip" data-placement="left" title="<spring:message code="label.Remove" />" onclick="resetVisibility(this, false)" id="idRemoveVisibility"></span>
				</div>
				<div class="triggers" data-bind="html: Content"></div>
			</td>
		<!-- /ko -->
		<!--  ko if: ContentType() == 'checkbox' -->
			<td class="propertycontent">
				<input data-bind="checked: Value, attr: {id: 'idProperty' + Label()}, disable: Disabled" type='checkbox' checked='checked' onclick='update(this)' />
			</td>
		<!-- /ko -->

		<!--  ko if: ContentType() == 'color' -->
			<td class="propertycontent">
				<input class="spectrum" data-bind="value: Value(), attr: {id: 'idProperty' + Label()}" type='text' onchange='update(this)' />
			</td>
		<!-- /ko -->

		<!--  ko if: ContentType() == 'number' -->
			<td class="propertycontent">
				<form autocomplete="off">
					<input class="spinner" type="number" data-bind="value: ContentItems()[0].Value, attr: {id: ContentItems()[0].Id}" onfocus='markActiveProperty(this)' onblur='update(this)'/>
				</form>
			</td>
		<!-- /ko -->

		<!--  ko if: ContentType() == 'minmax' -->
			<td class="propertycontent">
				<form autocomplete="off">
					<table class="minmaxtable">
						<tr>
							<td><spring:message code="label.min" />&nbsp;</td>
							<td><input class="spinner" style="min-width:60px" type="number" data-type="min" data-bind="value: ContentItems()[0].Value, attr: {id: ContentItems()[0].Id, 'data-to' : ContentItems()[1].Id}" onfocus='markActiveProperty(this)' onblur='update(this)'/></td>
						</tr>
						<tr>
							<td><spring:message code="label.max" />&nbsp;</td>
							<td><input class="spinner" style="min-width:60px" type="number" data-type="max" data-bind="value: ContentItems()[1].Value, attr: {id: ContentItems()[1].Id, 'data-from' : ContentItems()[0].Id}" onfocus='markActiveProperty(this)' onblur='update(this)'/></td>
						</tr>
					</table>
				</form>
			</td>

		<!-- /ko -->

		<!--  ko if: ContentType() == 'minmaxnumber' -->
			<td class="propertycontent">
				<form autocomplete="off">
					<table class="minmaxtable">
						<tr>
							<td><spring:message code="label.MinValue" />&nbsp;</td>
							<td><input class="spinner" style="min-width:60px" type="number" data-type="min" data-bind="value: ContentItems()[0].Value, attr: {id: ContentItems()[0].Id, 'data-to' : ContentItems()[1].Id}" onfocus='markActiveProperty(this)' onblur='update(this)'/></td>
						</tr>
						<tr>
							<td><spring:message code="label.MaxValue" />&nbsp;</td>
							<td><input class="spinner" style="min-width:60px" type="number" data-type="max" data-bind="value: ContentItems()[1].Value, attr: {id: ContentItems()[1].Id, 'data-from' : ContentItems()[0].Id}" onfocus='markActiveProperty(this)' onblur='update(this)'/></td>
						</tr>
					</table>
				</form>
			</td>
		<!-- /ko -->

		<!--  ko if: ContentType() == 'minmaxdate' -->
			<td class="propertycontent">
				<form autocomplete="off">
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
				</form>
			</td>
		<!-- /ko -->
		<!--  ko if: ContentType() == 'minmaxtime' -->
			<td class="propertycontent">
				<form autocomplete="off">
					<table class="minmaxtable">
						<tr>
							<td><spring:message code="label.after" />&nbsp;</td>
							<td><input class="" style="min-width:60px" type="text" data-type="min" data-bind="value: ContentItems()[0].Value, attr: {id: ContentItems()[0].Id, 'data-to' : ContentItems()[1].Id}" onfocus='markActiveProperty(this)' onblur='update(this)'/></td>
						</tr>
						<tr>
							<td><spring:message code="label.before" />&nbsp;</td>
							<td><input class="" style="min-width:60px" type="text" data-type="max" data-bind="value: ContentItems()[1].Value, attr: {id: ContentItems()[1].Id, 'data-from' : ContentItems()[0].Id}" onfocus='markActiveProperty(this)' onblur='update(this)'/></td>
						</tr>
					</table>
				</form>
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

				<div class="PreviewItems">
				<!--  ko foreach: PreviewItems() -->
					<div><a href="javascript:void(0)"  data-bind="html: getLimitedText($data), click: function(d, e){propertiesFromPreviewIndex($index(), e, $parent)}"></a></div>
				<!-- /ko -->
				</div>

				<button data-toggle="tooltip" title="<spring:message code="label.Add" />" class="btn btn-default btn-sm" data-bind="html: ContentItems()[0].Label, attr: {id: ContentItems()[0].Id, 'onclick' : ContentItems()[0].Value}"></button>
				<span class="tooltip-wrapper" tabindex="0" data-toggle="tooltip" title="<spring:message code="label.Remove"/>" data-bind="attr: {'onclick': PreviewItems().length > MinItems() && ContentItems()[1].Value}">
					<button data-bind="disable: !(PreviewItems().length > MinItems()),html: ContentItems()[1].Label, attr: {id: ContentItems()[1].Id, 'class': 'btn btn-default btn-sm' + (PreviewItems().length > MinItems() ? '' : '')}"></button>
				</span>
			</td>
		<!-- /ko -->
		<!--  ko if: ContentType() == 'quizquestion' -->
			<td class="propertycontent">
				<input class="check quizquestioncheck" name="scoring" type="checkbox" data-bind="value: '0', checked: Value(), attr: {id: 'scoring0' + Label()}" onclick='update(this);' />
			</td>
		<!-- /ko -->
		<!--  ko if: ContentType() == 'ecfquestion' -->
			<td class="propertycontent">
				<input class="check ecfquestioncheck" name="ecfquestion" type="checkbox" data-bind="value: '0', checked: Value(), attr: {id: 'ecf0' + Label()}" onclick='update(this);' disabled="disabled"/>
			</td>
		<!-- /ko -->
		<!--  ko if: ContentType() == 'ecfCompetencySelection' -->
			<td class="propertycontent">
				<form autocomplete="off">
					<input name="competencySelection" type="text" data-bind="value: Value(), attr: {id: 'ecf0' + Label()}" readonly=true/>
				</form>
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

					<!--  ko if: Element().type == 'MultipleChoiceQuestion' || (typeof Element()['type'] == 'function' && Element().type() == 'matrixitem' && !isSingleChoice) -->
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
		<td class="propertylabel" data-label="Attribute">
			<spring:message code="label.Attribute" />
			<a data-toggle="tooltip" data-placement="right" title="<spring:message code="label.AttributeTooltip" />">
				<span class="glyphicon glyphicon-question-sign"></span>
			</a>
		</td>
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
			<textarea data-bind="text: TinyMCEContent(), attr: {id: TinyMCEId()}"></textarea>
			<div class='edittextbuttons'>
				<button data-bind="attr: {id: 'idBtnSave' + Label()}" class='btn btn-default btn-primary btn-sm' onclick='save(this)'><spring:message code="label.Apply" /></button>
				<button data-bind="attr: {id: 'idBtnCancel' + Label()}" class='btn btn-default btn-sm' onclick='cancel(this);event.stopPropagation()'><spring:message code="label.Cancel" /></button>
			</div>
		</td>
	</tr>
</script>

<script type="text/html" id="editformula-template">
	<tr class="firstpropertyrow">
		<td class="propertylabel" data-label="Formula">
			<spring:message code="label.Formula" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.Formula" />'><span class='glyphicon glyphicon-question-sign'></span></a>
		</td>
		<td class="propertycontent">
			<a data-bind="click: function() {showFormulaDialog(FormulaInputId())}" class="btn btn-default btn-sm" style="height: 30px; padding: 3px;"><span style="font-family: serif; font-style: italic; font-size: 13px; font-weight: bold; margin: 4px;">fx</span></a>
			<textarea class="form-control" maxlength="80" data-bind="value: Value(), attr:{id: FormulaInputId()}" onfocus='markActiveProperty(this)' onblur='update(this)' rows="1" style="display: inline; width: 160px; height: 30px; vertical-align: top;"></textarea>
		</td>
	</tr>
	<tr class="propertyrow">
		<td class="propertylabel" data-label="Attribute">
			<spring:message code="label.Operators" />
		</td>
		<td class="propertycontent">
			<button data-bind="click: function() {addFormulaText('+', FormulaInputId())}" class="btn btn-default btn-sm">＋</button>
			<button data-bind="click: function() {addFormulaText('-', FormulaInputId())}" class="btn btn-default btn-sm">－</button>
			<button data-bind="click: function() {addFormulaText('*', FormulaInputId())}" class="btn btn-default btn-sm">＊</button>
			<button data-bind="click: function() {addFormulaText('/', FormulaInputId())}" class="btn btn-default btn-sm">／</button><br />
			<button data-bind="click: function() {addFormulaText('(', FormulaInputId())}" class="btn btn-default btn-sm">（</button>
			<button data-bind="click: function() {addFormulaText(')', FormulaInputId())}" class="btn btn-default btn-sm">）</button>
			<button data-bind="click: function() {clearFormula(FormulaInputId())}" class="btn btn-default btn-sm"><spring:message code="label.clear" /></button>
		</td>
	</tr>
	<tr class="collapsiblerow advanced">
		<td colspan='2' style="text-align: left">
			<a class='idpropertiestogglebutton' onclick='toggleSelectIDProperties(this)'><span class='glyphicon glyphicon-minus-sign'></span>&nbsp;<spring:message code="label.SelectIdentifiers" /></a>
		</td>
	</tr>
	<tr>
		<td colspan='2' style="text-align: left;">
			<table data-bind="foreach: NumberElements()" style="margin-left: 20px;">
				<tr>
					<td class="innertd" data-bind="html: shortname" style="font-weight: bold; padding-right: 10px;"></td>
					<td class="innertd" style="padding-right: 10px;" data-bind="html: limitedTitle"></td>
					<td class="innertd"><button data-bind="click: function() {addFormulaText(shortname(), $parent.FormulaInputId())}" class="btn btn-default btn-sm"><spring:message code="label.select" /></button></td>
				</tr>
			</table>
		</td>
	</tr>
</script>

<script type="text/html" id="editcomplextableheader-template">
	<tr class="firstpropertyrow">
		<td class="propertylabel" data-label="CellText">
			<spring:message code="label.Text" />
		</td>
		<td class="propertycontent">
			<div class="rightaligned">
				<span class="glyphicon glyphicon-pencil" data-toggle="tooltip" data-placement="left" title="<spring:message code="label.Edit" />" onclick="edit(this)"></span>
			</div>
			<form autocomplete="off">
				<div data-bind="html: getShortnedText(Cell().title())"></div>
			</form>
		</td>
	</tr>

	<tr class="propertyrow tinymcerow hideme">
		<td colspan='2'>
			<textarea data-bind="text: Cell().title, attr: {id: TinyMCEId()}"></textarea>
			<div class='edittextbuttons'>
				<button data-bind="attr: {id: 'idBtnSave' + Label()}" class='btn btn-default btn-primary btn-sm' onclick='save(this)'><spring:message code="label.Apply" /></button>
				<button data-bind="attr: {id: 'idBtnCancel' + Label()}" class='btn btn-default btn-sm' onclick='cancel(this);event.stopPropagation()'><spring:message code="label.Cancel" /></button>
			</div>
		</td>
	</tr>

	<tr class="collapsiblerow" data-bind="attr: {'style': Cell().cellType() > 0 ? '' : 'display: none;'}">
		<td colspan='2' style="text-align: left">
			<a onclick='toggleCellProperties(this)'><span class='glyphicon glyphicon-minus-sign'></span>&nbsp;<spring:message code="label.CellProperties" /></a>
		</td>
	</tr>

	<tr class="firstpropertyrow">
		<td class="propertylabel" data-label="CellType">
			<spring:message code="label.CellType" />
		</td>
		<td class="propertycontent">
			<select data-label="CellType" onchange="changeChildren(this, event)" data-bind="value: Cell().cellTypeChildren()">
				<option value="-1"><spring:message code="label.varying" /></option>
				<option value="0"><spring:message code="label.empty" /></option>
				<option value="1"><spring:message code="label.StaticText" /></option>
				<option value="2"><spring:message code="form.FreeText" /></option>
				<option value="3"><spring:message code="label.Formula" /></option>
				<option value="4"><spring:message code="form.SingleChoice" /></option>
				<option value="5"><spring:message code="form.MultipleChoice" /></option>
				<option value="6"><spring:message code="form.Number" /></option>
			</select>	
		</td>
	</tr>

	<!-- Text -->
	<tr class="firstpropertyrow" data-bind="attr: {'style': Cell().cellTypeChildren() > 0 ? '' : 'display: none;'}">
		<td class="propertylabel" data-label="CellText">
			<spring:message code="label.Text" />
		</td>
		<td class="propertycontent">
			<div class="rightaligned">
				<span class="glyphicon glyphicon-pencil" data-toggle="tooltip" data-placement="left" title="<spring:message code="label.Edit" />" onclick="edit(this)"></span>
			</div>
			<form autocomplete="off">
				<!-- ko if: Cell().titleChildrenMatch() -->
				<div data-bind="html: Cell().titleChildren"></div>
				<!-- /ko -->

				<!-- ko if: !Cell().titleChildrenMatch() -->
				<div><spring:message code="label.varying" /></div>
				<!-- /ko -->
			</form>
		</td>
	</tr>
	<tr class="propertyrow tinymcerow hideme">
		<td colspan='2'>
			<textarea data-bind="text: Cell().titleChildren, attr: {id: TinyMCEId() + 'children'}"></textarea>
			<div class='edittextbuttons'>
				<button data-label="CellText" data-bind="attr: {id: 'idBtnSave' + Label()}" class='btn btn-default btn-primary btn-sm' onclick='changeChildren(this, null)'><spring:message code="label.Apply" /></button>
				<button data-bind="attr: {id: 'idBtnCancel' + Label()}" class='btn btn-default btn-sm' onclick='cancel(this);event.stopPropagation()'><spring:message code="label.Cancel" /></button>
			</div>
		</td>
	</tr>

	<!-- Mandatory -->
	<tr class="propertyrow" data-bind="attr: {'style': Cell().cellTypeChildren() > 1 && Cell().cellTypeChildren() != 3 ? '' : 'display: none;'}">
		<td class="propertylabel" data-label="Mandatory">
			<spring:message code="label.Mandatory" />
		</td>
		<td class="propertycontent">
			<input data-label="Mandatory" data-bind="checked: !Cell().optionalChildren(), indeterminateValue: Cell().optionalIndeterminate()" type='checkbox' onclick='changeChildren(this, null)' />
		</td>
	</tr>

	<!-- Rows -->
	<tr class="propertyrow" data-bind="attr: {'style': Cell().cellTypeChildren() == 2 ? '' : 'display: none;'}">
		<td class="propertylabel" data-label="Rows">
			<spring:message code="label.Rows" />
		</td>
		<td class="propertycontent">
			<select data-label="Rows" onchange="changeChildren(this, event)" data-bind="options: ['', '1', '2', '3', '4', '5', '10', '20', '30', '40', '50'], value: Cell().numRowsChildren()">
			</select>
		</td>
	</tr>
	
	<!-- Free Text -->
	
		
		<!-- min / max -->
	<tr class="propertyrow" data-bind="attr: {'style': Cell().cellTypeChildren() == 2 ? '' : 'display: none;'}">
		<td class="propertylabel" data-label="AcceptedNumberOfCharacters">
			<spring:message code="label.AcceptedNumberOfCharacters" />
		</td>
		<td class="propertycontent">
			<form autocomplete="off">
				<table class="minmaxtable">
					<tr>
						<td><spring:message code="label.min" />&nbsp;</td>
						<td><input class="spinner" data-forcell="true" data-child="true" data-label="MinMax" style="min-width:60px" type="number" data-type="min" data-bind="value: Cell().minChildren, attr: {id: Cell().id() + 'min', 'data-to' : Cell().id() + 'max'}" onfocus='markActiveProperty(this)' onchange="changeChildren(this, event)" onblur="changeChildren(this, event)" /></td>
					</tr>
					<tr>
						<td><spring:message code="label.max" />&nbsp;</td>
						<td><input class="spinner" data-forcell="true" data-child="true" data-label="MinMax" style="min-width:60px" type="number" data-type="max" data-bind="value: Cell().maxChildren, attr: {id: Cell().id() + 'max', 'data-from' : Cell().id() + 'min'}" onfocus='markActiveProperty(this)' onchange="changeChildren(this, event)" onblur="changeChildren(this, event)"/></td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
	<!-- Free Text / -->

	<!-- Formula -->
	<tr class="propertyrow" data-bind="attr: {'style': Cell().cellTypeChildren() == 3 ? '' : 'display: none;'}">
		<td class="propertylabel">
			<spring:message code="label.Formula" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.Formula" />'><span class='glyphicon glyphicon-question-sign'></span></a>
		</td>
		<td class="propertycontent">
			<a data-bind="click: function() {showFormulaDialog(FormulaInputId())}" class="btn btn-default btn-sm" style="height: 30px; padding: 3px;"><span style="font-family: serif; font-style: italic; font-size: 13px; font-weight: bold; margin: 4px;">fx</span></a>
			<textarea class="form-control" data-label="Formula" maxlength="80" data-bind="value: Cell().formulaChildren(), attr:{id: FormulaInputId()}" onfocus='markActiveProperty(this)' onblur='changeChildren(this, event)' rows="1" style="display: inline; width: 160px; height: 30px; vertical-align: top;"></textarea>
		</td>
	</tr>
	<tr class="propertyrow" data-bind="attr: {'style': Cell().cellTypeChildren() == 3 ? '' : 'display: none;'}">
		<td class="propertylabel" data-label="Attribute">
			<spring:message code="label.Operators" />
		</td>
		<td class="propertycontent">
			<button data-bind="click: function() {addFormulaText('+', FormulaInputId())}" class="btn btn-default btn-sm">＋</button>
			<button data-bind="click: function() {addFormulaText('-', FormulaInputId())}" class="btn btn-default btn-sm">－</button>
			<button data-bind="click: function() {addFormulaText('*', FormulaInputId())}" class="btn btn-default btn-sm">＊</button>
			<button data-bind="click: function() {addFormulaText('/', FormulaInputId())}" class="btn btn-default btn-sm">／</button><br />
			<button data-bind="click: function() {addFormulaText('(', FormulaInputId())}" class="btn btn-default btn-sm">（</button>
			<button data-bind="click: function() {addFormulaText(')', FormulaInputId())}" class="btn btn-default btn-sm">）</button>
			<button style="width: 34px" data-bind="click: function() {addFormulaText(',', FormulaInputId())}" class="btn btn-default btn-sm">,</button>
			<button data-bind="click: function() {clearFormula(FormulaInputId())}" class="btn btn-default btn-sm"><spring:message code="label.clear" /></button>
		</td>
	</tr>
	<tr class="collapsiblerow advanced" data-bind="attr: {'style': Cell().cellTypeChildren() == 3 ? '' : 'display: none;'}">
		<td colspan='2' style="text-align: left">
			<a class='idpropertiestogglebutton' onclick='toggleSelectIDProperties(this)'><span class='glyphicon glyphicon-minus-sign'></span>&nbsp;<spring:message code="label.SelectIdentifiers" /></a>
		</td>
	</tr>
	<tr data-bind="attr: {'style': Cell().cellTypeChildren() == 3 ? '' : 'display: none;'}">
		<td colspan='2' style="text-align: left;">
			<table data-bind="foreach: NumberElements()" style="margin-left: 20px;">
				<tr>
					<td class="innertd" data-bind="html: shortname" style="font-weight: bold; padding-right: 10px;"></td>
					<td class="innertd" style="padding-right: 10px;" data-bind="html: limitedTitle"></td>
					<td class="innertd"><button data-bind="click: function() {addFormulaText(shortname(), $parent.FormulaInputId())}" class="btn btn-default btn-sm"><spring:message code="label.select" /></button></td>
				</tr>
			</table>
		</td>
	</tr>
	<!-- Formula / -->

	<!-- Number -->
	<tr class="propertyrow" data-bind="attr: {'style': Cell().cellTypeChildren() == 6 ? '' : 'display: none;'}">
		<td class="propertylabel" data-label="Unit">
			<spring:message code="label.Unit" />
		</td>
		<td class="propertycontent">
			<input type='text' data-label="Unit" data-child="true" data-bind="value: Cell().unitChildren()" onblur="changeChildren(this, event)" onfocus='markActiveProperty(this)' />
		</td>
	</tr>
	<!-- Number / -->

	<!-- Number And Formula -->
	<tr data-label="DecimalPlaces" class="propertyrow" data-bind="attr: {'style': Cell().cellTypeChildren() == 3  || Cell().cellTypeChildren() == 6 ? '' : 'display: none;'}">
		<td class="propertylabel" data-label="DecimalPlaces">
			<spring:message code="label.DecimalPlacesNew" />
		</td>

		<td class="propertycontent">
			<select data-label="DecimalPlaces" data-bind="value: Cell().decimalsChildren()" onchange="changeChildren(this, event)">
				<option></option>
				<option value="1">1</option>
				<option value="2">2</option>
				<option value="3">3</option>
				<option value="4">4</option>
				<option value="5">5</option>
				<option value="6">6</option>
				<option value="7">7</option>
				<option value="8">8</option>
				<option value="9">9</option>
				<option value="10">10</option>
			</select>
		</td>
	</tr>
	<tr class="propertyrow" data-bind="attr: {'style': Cell().cellTypeChildren() == 3 || Cell().cellTypeChildren() == 6 ? '' : 'display: none;'}">
		<td class="propertylabel"><spring:message code="label.Values" /></td>
		<td class="propertycontent">
			<form autocomplete="off">
				<table class="minmaxtable">
					<tr>
						<td><spring:message code="label.min" />&nbsp;</td>
						<td><input class="spinner" data-label="MinMax" data-child="true" style="min-width:60px" type="number" data-type="min" data-bind="value: Cell().minChildren, attr: {id: Cell().id() + 'minValue'}" onfocus='markActiveProperty(this)' onchange="changeChildren(this, event)" onblur="changeChildren(this, event)" role="spinbutton" /></td>
					</tr>
					<tr>
						<td><spring:message code="label.max" />&nbsp;</td>
						<td><input class="spinner" data-label="MinMax" data-child="true" style="min-width:60px" type="number" data-type="max" data-bind="value: Cell().maxChildren, attr: {id: Cell().id() + 'maxValue'}" onfocus='markActiveProperty(this)'  onchange="changeChildren(this, event)" onblur="changeChildren(this, event)" role="spinbutton" /></td>
					</tr>
				</table>
			</form>

		</td>
	</tr>
	<!-- Number And Formula / -->


	<!-- Choice -->
	<tr class="firstpropertyrow" data-bind="attr: {'style': Cell().cellTypeChildren() == 4 || Cell().cellTypeChildren() == 5 ? '' : 'display: none;'}">
		<td class="propertylabel" data-label="PossibleAnswers">
			<spring:message code="label.PossibleAnswers" />
			<!-- ko if: !Cell().possibleAnswersMatch() -->
			<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.PossibleAnswersDiffer" />'><span class='glyphicon glyphicon-question-sign'></span></a>
			<!-- /ko -->
		</td>
		<td class="propertycontent">
			<div class="rightaligned">
				<!-- ko if: Cell().possibleAnswersMatch() -->
				<span data-bind="attr: {id: 'idEdit' + Label()}" data-toggle="tooltip" data-placement="left" title="<spring:message code="label.Edit" />" class="glyphicon glyphicon-pencil" onclick="edit(this)"></span>
				<!-- /ko -->
			</div>

			<div class="PreviewItems">
				<!--  ko foreach: PreviewItems() -->
				<div><span data-bind="html: getLimitedText($data)"></span></div>
				<!-- /ko -->
			</div>

			<span class="tooltip-wrapper" tabindex="0" data-toggle="tooltip" title="<spring:message code="label.Add" />" onclick="addPossibleAnswerChildren()">
				<button data-bind="disable: !Cell().possibleAnswersMatch()" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-plus"></span></button>
			</span>
			<span class="tooltip-wrapper" tabindex="0" data-toggle="tooltip" title="<spring:message code="label.Remove"/>" onclick="removePossibleAnswerChildren()">
				<button data-bind="disable: !(PreviewItems().length > 0) || !Cell().possibleAnswersMatch()" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-minus"></span></button>
			</span>
		</td>
	</tr>
	<tr class="propertyrow tinymcerow hideme">
		<td colspan='2'>
			<textarea data-bind="text: PreviewItems(), attr: {id: TinyMCEId() + 'possibleAnswers'}"></textarea>
			<div class='edittextbuttons'>
				<button data-bind="attr: {id: 'idBtnSavePossibleAnswers'}" class='btn btn-default btn-primary btn-sm' onclick='save(this)'><spring:message code="label.Apply" /></button>
				<button data-bind="attr: {id: 'idBtnCancelPossibleAnswers'}" class='btn btn-default btn-sm' onclick='cancel(this);event.stopPropagation()'><spring:message code="label.Cancel" /></button>
			</div>
		</td>
	</tr>
	<tr class="propertyrow hideme">
		<td class="propertycontent" colspan='2'>
			<table class="table table-bordered propertiessubtable"></table>
			<div class='editvaluesbuttons'><button id='idBtnSaveShortName' class='btn btn-default btn-primary btn-sm' onclick='save(this)'><spring:message code="label.Apply" /></button> <button id='idBtnCancelShortName' class='btn btn-default btn-sm' onclick='cancel(this);event.stopPropagation()'><spring:message code="label.Cancel" /></button></div>
		</td>
	</tr>
	<!-- Choice -->

</script>

<script type="text/html" id="editcomplextable-template">
	<tr class="firstpropertyrow">
		<td class="propertylabel" data-label="CellType">
			<spring:message code="label.CellType" />
		</td>
		<td class="propertycontent">
			<select data-label="CellType" data-forcell="true" onchange="change(this, event)" data-bind="value: Cell().cellType(), attr: {'data-value': Cell().cellType()}">
				<option value="0"><spring:message code="label.empty" /></option>
				<option value="1"><spring:message code="label.StaticText" /></option>
				<option value="2"><spring:message code="form.FreeText" /></option>
				<option value="3"><spring:message code="label.Formula" /></option>
				<option value="4"><spring:message code="form.SingleChoice" /></option>
				<option value="5"><spring:message code="form.MultipleChoice" /></option>
				<option value="6"><spring:message code="form.Number" /></option>
			</select>	
		</td>
	</tr>

	<tr class="collapsiblerow" data-bind="attr: {'style': Cell().cellType() == 0 ? 'display: none;' : ''}">
		<td colspan='2' style="text-align: left">
			<a onclick='toggleCellProperties(this)'><span class='glyphicon glyphicon-minus-sign'></span>&nbsp;<spring:message code="label.CellProperties" /></a>
		</td>
	</tr>

	<tr class="firstpropertyrow" data-bind="attr: {'style': Cell().cellType() == 0 ? 'display: none;' : ''}">
		<td class="propertylabel" data-label="CellText">
			<spring:message code="label.Text" />
		</td>
		<td class="propertycontent">
			<div class="rightaligned">
				<span class="glyphicon glyphicon-pencil" data-toggle="tooltip" data-placement="left" title="<spring:message code="label.Edit" />" onclick="edit(this)"></span>
			</div>
			<form autocomplete="off">
				<div data-bind="html: getShortnedText(Cell().title())"></div>
			</form>
		</td>
	</tr>

	<tr class="propertyrow tinymcerow hideme">
		<td colspan='2'>
			<textarea data-bind="text: Cell().title, attr: {id: TinyMCEId()}"></textarea>
			<div class='edittextbuttons'>
				<button data-bind="attr: {id: 'idBtnSave' + Label()}" class='btn btn-default btn-primary btn-sm' onclick='save(this)'><spring:message code="label.Apply" /></button>
				<button data-bind="attr: {id: 'idBtnCancel' + Label()}" class='btn btn-default btn-sm' onclick='cancel(this);event.stopPropagation()'><spring:message code="label.Cancel" /></button>
			</div>
		</td>
	</tr>

	<!-- Exclude Empty, Static Text and Formula from Mandatory -->
	<tr class="propertyrow" data-bind="attr: {'style': Cell().cellType() > 1 && Cell().cellType() != 3 ? '' : 'display: none;'}">
		<td class="propertylabel" data-label="Mandatory">
			<spring:message code="label.Mandatory" />
		</td>
		<td class="propertycontent">
			<input data-label="Mandatory" data-bind="checked: !Cell().optional()" id="idPropertyMandatory" type='checkbox' onclick='update(this)' />
		</td>
	</tr>
	
	<tr class="propertyrow" data-bind="attr: {'style': Cell().cellType() == 5 ? '' : 'display: none;'}">
		<td class="propertylabel" data-label="Style">
			<spring:message code="label.Style" />
		</td>
		<td class="propertycontent">
			<label>
				<input data-label="Style" data-bind="checked: Cell().cellStyle(), attr: {name: Cell().id() + 'style'}" type='radio' class='check' onclick='update(this)' value="CheckBox" />
				<span><spring:message code="html.CheckBox" /></span>
			</label><br />
			<label>
				<input data-label="Style" data-bind="checked: Cell().cellStyle(), attr: {name: Cell().id() + 'style'}" type='radio' class='check' onclick='update(this)' value="ListBox" />
				<span><spring:message code="html.ListBox" /></span>
			</label>
		</td>
	</tr>

	<tr class="propertyrow" data-bind="attr: {'style': Cell().cellType() == 4 ? '' : 'display: none;'}">
		<td class="propertylabel" data-label="Style">
			<spring:message code="label.Style" />
		</td>
		<td class="propertycontent">
			<label>
				<input data-label="Style" data-bind="checked: Cell().cellStyle(), attr: {name: Cell().id() + 'style'}" type='radio' class='check' onclick='update(this)' value="RadioButton" />
				<span><spring:message code="html.RadioButton" /></span>
			</label><br />
			<label>
				<input data-label="Style" data-bind="checked: Cell().cellStyle(), attr: {name: Cell().id() + 'style'}" type='radio' class='check' onclick='update(this)' value="SelectBox" />
				<span><spring:message code="html.SelectBox" /></span>
			</label>
		</td>
	</tr>

	<tr class="propertyrow" data-bind="attr: {'style': Cell().cellType() == 1 ? '' : 'display: none;'}">
		<td class="propertylabel" data-label="Text">
			<spring:message code="label.ColumnSpan" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.ColumnSpan" />'><span class='glyphicon glyphicon-question-sign'></span></a>
		</td>
		<td class="propertycontent">
			<select data-label="ColumnSpan" onchange="change(this, event)" data-bind="options: Element().availableColumnSpans(Cell().column()), value: Cell().columnSpan()">
			</select>
		</td>
	</tr>

	<!-- Formula Properties -->
	<tr class="propertyrow" data-bind="attr: {'style': Cell().cellType() == 3 ? '' : 'display: none;'}">
		<td class="propertylabel">
			<spring:message code="label.Formula" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.Formula" />'><span class='glyphicon glyphicon-question-sign'></span></a>
		</td>
		<td class="propertycontent">
			<a data-bind="click: function() {showFormulaDialog(FormulaInputId())}" class="btn btn-default btn-sm" style="height: 30px; padding: 3px;"><span style="font-family: serif; font-style: italic; font-size: 13px; font-weight: bold; margin: 4px;">fx</span></a>
			<textarea class="form-control" data-label="Formula" maxlength="80" data-bind="value: Cell().formula(), attr:{id: FormulaInputId()}" onfocus='markActiveProperty(this)' onblur='update(this)' rows="1" style="display: inline; width: 160px; height: 30px; vertical-align: top;"></textarea>
		</td>
	</tr>
	<tr class="propertyrow" data-bind="attr: {'style': Cell().cellType() == 3 ? '' : 'display: none;'}">
		<td class="propertylabel" data-label="Attribute">
			<spring:message code="label.Operators" />
		</td>
		<td class="propertycontent">
			<button data-bind="click: function() {addFormulaText('+', FormulaInputId())}" class="btn btn-default btn-sm">＋</button>
			<button data-bind="click: function() {addFormulaText('-', FormulaInputId())}" class="btn btn-default btn-sm">－</button>
			<button data-bind="click: function() {addFormulaText('*', FormulaInputId())}" class="btn btn-default btn-sm">＊</button>
			<button data-bind="click: function() {addFormulaText('/', FormulaInputId())}" class="btn btn-default btn-sm">／</button><br />
			<button data-bind="click: function() {addFormulaText('(', FormulaInputId())}" class="btn btn-default btn-sm">（</button>
			<button data-bind="click: function() {addFormulaText(')', FormulaInputId())}" class="btn btn-default btn-sm">）</button>
			<button data-bind="click: function() {clearFormula(FormulaInputId())}" class="btn btn-default btn-sm"><spring:message code="label.clear" /></button>
		</td>
	</tr>
	<tr class="collapsiblerow advanced" data-bind="attr: {'style': Cell().cellType() == 3 ? '' : 'display: none;'}">
		<td colspan='2' style="text-align: left">
			<a class='idpropertiestogglebutton' onclick='toggleSelectIDProperties(this)'><span class='glyphicon glyphicon-minus-sign'></span>&nbsp;<spring:message code="label.SelectIdentifiers" /></a>
		</td>
	</tr>
	<tr data-bind="attr: {'style': Cell().cellType() == 3 ? '' : 'display: none;'}">
		<td colspan='2' style="text-align: left;">
			<table data-bind="foreach: NumberElements()" style="margin-left: 20px;">
				<tr>
					<td class="innertd" data-bind="html: shortname" style="font-weight: bold; padding-right: 10px;"></td>
					<td class="innertd" style="padding-right: 10px;" data-bind="html: limitedTitle"></td>
					<td class="innertd"><button data-bind="click: function() {addFormulaText(shortname(), $parent.FormulaInputId())}" class="btn btn-default btn-sm"><spring:message code="label.select" /></button></td>
				</tr>
			</table>
		</td>
	</tr>

	<!-- Formula Properties / -->

	<!-- Number Properties -->
	<tr class="propertyrow" data-bind="attr: {'style': Cell().cellType() == 6 ? '' : 'display: none;'}">
		<td class="propertylabel" data-label="Unit">
			<spring:message code="label.Unit" />
		</td>
		<td class="propertycontent">
			<input type='text' data-label="Unit" data-bind="value: Cell().unit()" onblur="update(this)" onfocus='markActiveProperty(this)' />
		</td>
	</tr>

	<!-- Number Properties / -->

	<!-- Number And Formula Properties -->
	<tr data-label="DecimalPlaces" class="propertyrow" data-bind="attr: {'style': Cell().cellType() == 3  || Cell().cellType() == 6 ? '' : 'display: none;'}">
		<td class="propertylabel" data-label="DecimalPlaces">
			<spring:message code="label.DecimalPlacesNew" />
		</td>

		<td class="propertycontent">
			<select data-label="DecimalPlaces" data-bind="value: Cell().decimalPlaces()" onchange="update(this)">
				<option></option>
				<option value="1">1</option>
				<option value="2">2</option>
				<option value="3">3</option>
				<option value="4">4</option>
				<option value="5">5</option>
				<option value="6">6</option>
				<option value="7">7</option>
				<option value="8">8</option>
				<option value="9">9</option>
				<option value="10">10</option>
			</select>
		</td>
	</tr>
	<tr class="firstpropertyrow" data-bind="attr: {'style': Cell().cellType() == 3 || Cell().cellType() == 6 ? '' : 'display: none;'}">
		<td class="propertylabel" data-label="Values"><spring:message code="label.Values" /></td>
		<td class="propertycontent">
			<form autocomplete="off">
				<table class="minmaxtable">
					<tr>
						<td><spring:message code="label.min" />&nbsp;</td>
						<td><input class="spinner" data-label="Values" style="min-width:60px" type="number" data-type="min" data-bind="value: Cell().min() > 0 ? Cell().min() : '', attr: {id: Cell().id() + 'minValue', 'data-to' : Cell().id() + 'maxValue'}" onfocus='markActiveProperty(this)' onblur='update(this)' role="spinbutton" /></td>
					</tr>
					<tr>
						<td><spring:message code="label.max" />&nbsp;</td>
						<td><input class="spinner" data-label="Values" style="min-width:60px" type="number" data-type="max" data-bind="value: Cell().max() > 0 ? Cell().max() : '', attr: {id: Cell().id() + 'maxValue', 'data-from' : Cell().id() + 'minValue'}" onfocus='markActiveProperty(this)' onblur='update(this)' role="spinbutton" /></td>
					</tr>
				</table>
			</form>

		</td>
	</tr>
	<!-- Number And Formula Properties / -->

	<!-- Choice Properties -->
		<tr class="firstpropertyrow" data-bind="attr: {'style': Cell().cellType() == 4 || Cell().cellType() == 5 ? '' : 'display: none;'}">
			<td class="propertylabel" data-label="PossibleAnswers">
				<spring:message code="label.PossibleAnswers" />
			</td>
			<td class="propertycontent">
				<div class="rightaligned">
					<span data-bind="attr: {id: 'idEdit' + Label()}" data-toggle="tooltip" data-placement="left" title="<spring:message code="label.Edit" />" class="glyphicon glyphicon-pencil" onclick="edit(this)"></span>
					<span style="margin-left: 5px;" data-toggle="tooltip" data-placement="left" title="<spring:message code="label.AssignValues" />" class="glyphicon glyphicon-tag" onclick="editShortnames(this)" id="idEditShortNamePossibleAnswers"></span>
				</div>
			
				<div class="PreviewItems">
				<!--  ko foreach: PreviewItems() -->
					<div><span data-bind="html: getLimitedText($data)"></span></div>
				<!-- /ko -->
				</div>

				<button data-toggle="tooltip" title="<spring:message code="label.Add" />" class="btn btn-default btn-sm" onclick="addPossibleAnswer()"><span class="glyphicon glyphicon-plus"></span></button>
				<span class="tooltip-wrapper" tabindex="0" data-toggle="tooltip" data-bind="attr: {'onclick': PreviewItems().length > 1 ? 'removePossibleAnswer()' : ''}" title="<spring:message code="label.Remove"/>">
					<button data-bind="disable: !(PreviewItems().length > 1)" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-minus"></span></button>
				</span>
			</td>
		</tr>
		<tr class="propertyrow tinymcerow hideme">
			<td colspan='2'>
				<textarea data-bind="text: PreviewItems(), attr: {id: TinyMCEId() + 'possibleAnswers'}"></textarea>
				<div class='edittextbuttons'>
					<button data-bind="attr: {id: 'idBtnSavePossibleAnswers'}" class='btn btn-default btn-primary btn-sm' onclick='save(this)'><spring:message code="label.Apply" /></button>
					<button data-bind="attr: {id: 'idBtnCancelPossibleAnswers'}" class='btn btn-default btn-sm' onclick='cancel(this);event.stopPropagation()'><spring:message code="label.Cancel" /></button>
				</div>
			</td>
		</tr>
		<tr class="propertyrow hideme">
			<td class="propertycontent" colspan='2'>
				<table class="table table-bordered propertiessubtable"></table>
				<div class='editvaluesbuttons'><button id='idBtnSaveShortName' class='btn btn-default btn-primary btn-sm' onclick='save(this)'><spring:message code="label.Apply" /></button> <button id='idBtnCancelShortName' class='btn btn-default btn-sm' onclick='cancel(this);event.stopPropagation()'><spring:message code="label.Cancel" /></button></div>
			</td>
		</tr>

		<tr class="firstpropertyrow" data-bind="attr: {'style': Cell().cellType() == 4 || Cell().cellType() == 5 ? '' : 'display: none;'}">
			<td class="propertylabel" data-label="Order">
				<spring:message code="label.Order" />
				<a style='margin-left: 2px'><span data-toggle='tooltip' data-html="true" data-original-title='<spring:message code="info.Order" />' class='glyphicon glyphicon glyphicon-question-sign'></span></a>
			</td>

			<td class="propertycontent">
				<label><input data-bind="checked: Cell().order() == 0 ? 'Original' : '', attr : { name: Cell().id() + 'order' }" class="check" onclick="update(this)" type="radio" id="selectOriginal" value="Original"><spring:message code="label.OriginalOrder" /></label><br>

				<label><input data-bind="checked: Cell().order() == 1 ? 'Alphabetical' : '', attr : { name: Cell().id() + 'order' }" class="check" onclick="update(this)" type="radio" id="selectAlphabetical" value="Alphabetical"><spring:message code="label.AlphabeticalOrder" /></label><br>

				<label><input data-bind="checked: Cell().order() == 2 ? 'Random' : '', attr : { name: Cell().id() + 'order' }" class="check" onclick="update(this)" type="radio" id="selectRandom" value="Random"><spring:message code="label.RandomOrder" /></label><br>
			</td>
		</tr>

		<tr class="propertyrow" data-bind="attr: {'style': Cell().cellType() == 4 || Cell().cellType() == 5 ? '' : 'display: none;'}">
			<td class="propertylabel" data-label="Text">
				<spring:message code="label.Columns" />
			</td>
			<td class="propertycontent">
				<select data-label="Columns" onchange="change(this, event)" data-bind="options: [1, 2, 3, 4], value: Cell().numColumns(), disable: (Cell().cellType() === 4 && !Cell().useRadioButtons()) || (Cell().cellType() === 5 && !Cell().useCheckboxes())">
				</select>
			</td>
		</tr>

		<tr class="firstpropertyrow" data-bind="attr: {'style': Cell().cellType() == 5 ? '' : 'display: none;'}">
			<td class="propertylabel" data-label="NumberOfChoices">
				<spring:message code="label.NumberOfChoices" />
			</td>
			<td class="propertycontent">
				<form autocomplete="off">
					<table class="minmaxtable">
						<tr>
							<td><spring:message code="label.min" />&nbsp;</td>
							<td><input class="spinner" data-forcell="true" data-label="NumberOfChoices" style="min-width:60px" type="number" data-type="min" data-bind="value: Cell().minChoices(), attr: {id: Cell().id() + 'minC', 'data-to' : Cell().id() + 'maxC'}" onfocus='markActiveProperty(this)' onblur='update(this)'/></td>
						</tr>
						<tr>
							<td><spring:message code="label.max" />&nbsp;</td>
							<td><input class="spinner" data-forcell="true" data-label="NumberOfChoices" style="min-width:60px" type="number" data-type="max" data-bind="value: Cell().maxChoices(), attr: {id: Cell().id() + 'maxC', 'data-from' : Cell().id() + 'minC'}" onfocus='markActiveProperty(this)' onblur='update(this)'/></td>
						</tr>
					</table>
				</form>
			</td>
		</tr>
	<!-- Choice Properties / -->

	<tr class="propertyrow" data-bind="attr: {'style': Cell().cellType() > 1 ? '' : 'display: none;'}">
		<td class="propertylabel" data-label="ResultText">
			<spring:message code="label.ResultText" />
			<a style='margin-left: 2px'><span data-toggle='tooltip' title='<spring:message code="info.ResultText" />' class='glyphicon glyphicon glyphicon-question-sign'></span></a>
		</td>
		<td class="propertycontent">
			<form autocomplete="off">
				<input type="text" data-label="ResultText" data-bind="value: Cell().resultText()" onblur="update(this)" onfocus='markActiveProperty(this)' />
			</form>
		</td>
	</tr>

	<tr class="propertyrow" data-bind="attr: {'style': Cell().cellType() == 2 ? '' : 'display: none;'}">
		<td class="propertylabel" data-label="Rows">
			<spring:message code="label.Rows" />
		</td>
		<td class="propertycontent">
			<select data-label="Rows" data-forcell="true" onchange="change(this, event)" data-bind="options: ['1', '2', '3', '4', '5', '10', '20', '30', '40', '50'], value: Cell().numRows()">
			</select>
		</td>
	</tr>

	<tr class="firstpropertyrow" data-bind="attr: {'style': Cell().cellType() > 1 ? '' : 'display: none;'}">
		<td class="propertylabel" data-label="Help">
			<spring:message code="label.HelpMessage" />
		</td>
		<td class="propertycontent">
			<div class="rightaligned">
				<span class="glyphicon glyphicon-pencil" data-toggle="tooltip" data-placement="left" title="<spring:message code="label.Edit" />" onclick="edit(this)"></span>
				<span class="glyphicon glyphicon-trash" data-toggle="tooltip" data-placement="left" title="<spring:message code="label.Remove" />" onclick="resetHelp(this)"></span>
			</div>
			<form autocomplete="off">
				<div data-bind="html: getShortnedText(Cell().help())"></div>
			</form>
		</td>
	</tr>

	<tr class="propertyrow tinymcerow hideme">
		<td colspan='2'>
			<textarea data-bind="text: Cell().help, attr: {id: TinyMCEId() + 'help'}"></textarea>
			<div class='edittextbuttons'>
				<button data-bind="attr: {id: 'idBtnSave' + Label()}" class='btn btn-default btn-primary btn-sm' onclick='save(this)'><spring:message code="label.Apply" /></button>
				<button data-bind="attr: {id: 'idBtnCancel' + Label()}" class='btn btn-default btn-sm' onclick='cancel(this);event.stopPropagation()'><spring:message code="label.Cancel" /></button>
			</div>
		</td>
	</tr>

	<tr class="firstpropertyrow" data-bind="attr: {'style': Cell().cellType() == 2 ? '' : 'display: none;'}">
		<td class="propertylabel" data-label="AcceptedNumberOfCharacters">
			<spring:message code="label.AcceptedNumberOfCharacters" />
		</td>
		<td class="propertycontent">
			<form autocomplete="off">
				<table class="minmaxtable">
					<tr>
						<td><spring:message code="label.min" />&nbsp;</td>
						<td><input class="spinner" data-forcell="true" data-label="AcceptedNumberOfCharacters" style="min-width:60px" type="number" data-type="min" data-bind="value: Cell().minCharacters() > 0 ? Cell().minCharacters() : '', attr: {id: Cell().id() + 'min', 'data-to' : Cell().id() + 'max'}" onfocus='markActiveProperty(this)' onblur='update(this)'/></td>
					</tr>
					<tr>
						<td><spring:message code="label.max" />&nbsp;</td>
						<td><input class="spinner" data-forcell="true" data-label="AcceptedNumberOfCharacters" style="min-width:60px" type="number" data-type="max" data-bind="value: Cell().maxCharacters() > 0 ? Cell().maxCharacters() : '', attr: {id: Cell().id() + 'max', 'data-from' : Cell().id() + 'min'}" onfocus='markActiveProperty(this)' onblur='update(this)'/></td>
					</tr>
				</table>
			</form>
		</td>
	</tr>

	<!-- ADVANCED -->

	<tr class="firstpropertyrow collapsiblerow advanced" data-bind="attr: {'style': Cell().cellType() < 2 ? 'display: none;' : ''}">
		<td colspan='2' style="text-align: left">
			<a class='advancedtogglebutton' onclick='toggleAdvancedProperties(this)'><span class='glyphicon glyphicon-minus-sign'></span>&nbsp;<spring:message code="label.Advanced" /></a>
		</td>
	</tr>

	<tr class="propertyrow" data-bind="attr: {'style': Cell().cellType() <= 1 ? 'display: none;' : '' }">
		<td class="propertylabel" data-label="Identifier">
			<spring:message code="label.Identifier" />
		</td>
		<td class="propertycontent">
			<form autocomplete="off">
				<input type="text" data-label="Identifier" data-bind="value: Cell().shortname()" onblur="update(this)" />
			</form>
		</td>
	</tr>

	<tr class="propertyrow" data-bind="attr: {'style': Cell().cellType() > 1 ? '' : 'display: none;'}">
		<td class="propertylabel" data-label="ReadOnly">
			<spring:message code="label.Readonly" />&nbsp;
			<!-- ko if: Cell().cellType() == 3 -->
			<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.ReadonlyFormula" />'><span class='glyphicon glyphicon-question-sign'></span></a>
			<!-- /ko -->
			<!-- ko if: Cell().cellType() != 3 -->
			<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.Readonly" />'><span class='glyphicon glyphicon-question-sign'></span></a>
			<!-- /ko -->
		</td>
		<td class="propertycontent">
			<form autocomplete="off">
				<input type="checkbox" data-label="ReadOnly" data-bind="checked: Cell().readonly()" onchange="update(this)" id="idPropertyReadOnly" />
			</form>
		</td>
	</tr>

</script>

<script type="text/html" id="editcomplextablefirst-template">
	<tr class="firstpropertyrow">
		<td class="propertylabel" data-label="CellText">
			<spring:message code="label.Text" />
		</td>
		<td class="propertycontent">
			<div class="rightaligned">
				<span class="glyphicon glyphicon-pencil" data-toggle="tooltip" data-placement="left" title="<spring:message code="label.Edit" />" onclick="edit(this)"></span>
			</div>
			<form autocomplete="off">
				<div data-bind="html: Cell().title"></div>
			</form>
		</td>
	</tr>

	<tr class="propertyrow tinymcerow hideme">
		<td colspan='2'>
			<textarea data-bind="text: Cell().title, attr: {id: TinyMCEId()}"></textarea>
			<div class='edittextbuttons'>
				<button data-bind="attr: {id: 'idBtnSave' + Label()}" class='btn btn-default btn-primary btn-sm' onclick='save(this)'><spring:message code="label.Apply" /></button>
				<button data-bind="attr: {id: 'idBtnCancel' + Label()}" class='btn btn-default btn-sm' onclick='cancel(this);event.stopPropagation()'><spring:message code="label.Cancel" /></button>
			</div>
		</td>
	</tr>
</script>

<script type="text/html" id="PossibleAnswerShortnames-template">
	<tr class="propertyrow hideme">
		<td class="propertycontent" colspan='2'>
			<table class="table table-bordered propertiessubtable"></table>
			<div class='editvaluesbuttons'><button id='idBtnSaveShortName' class='btn btn-default btn-primary btn-sm' onclick='save(this)'><spring:message code="label.Apply" /></button> <button id='idBtnCancelShortName' class='btn btn-default btn-sm' onclick='cancel(this);event.stopPropagation()'><spring:message code="label.Cancel" /></button></div>
		</td>
	</tr>		
</script>

<script type="text/html" id="advanced-template">	
	<tr class="firstpropertyrow collapsiblerow advanced">
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
					<span class="glyphicon glyphicon-chevron-down" onclick="showHideQuizProperties(this)"></span>
					<spring:message code="label.Quiz" />
				</a>
			</div>
		</td>
	</tr>		
</script>

<script type="text/html" id="quizanswers-template">	
	<tr class="firstpropertyrow quiz" data-bind="attr: {style: Element().scoring() == 0 ? 'display:none' : ''}">
		<td class="propertylabel" style="border-bottom: 0px;"><spring:message code="label.Answers" /></td>
		<td style="text-align: right; border-bottom: 0px;">
			<!-- ko if: Element().type != 'MultipleChoiceQuestion' && Element().type != 'SingleChoiceQuestion' && !(typeof Element()['type'] == 'function' && Element().type() == 'matrixitem') -->
				<a data-bind="click: function(data, event) { addExpectedAnswer(true) }" class="btn btn-default btn-sm" title="<spring:message code="label.AddExpectedAnswer" />" data-toggle="tooltip"><span class="glyphicon glyphicon-plus"></span></a>
			<!-- /ko -->
		</td>
	</tr>
	<!--  ko foreach: ContentItems() -->
	<!-- ko if: $parent.Element().type == 'MultipleChoiceQuestion' || $parent.Element().type == 'SingleChoiceQuestion' || (typeof $parent.Element()['type'] == 'function' && $parent.Element().type() == 'matrixitem') -->
		<tr class="quiz" data-bind="attr: {style: $parent.Element().scoring() == 0 ? 'display:none' : ''}">
			<td class="propertylabel" style="border-bottom: 0px; vertical-align: middle !important; padding-bottom: 10px !important;"></td>
			<td class="propertycontent" style="border-bottom: 0px; padding-bottom:10px !important">			
				<span data-bind="html: strip_tags(title())"></span>
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
	
	<!-- ko if: !(typeof $parent.Element()['type'] == 'function' && $parent.Element().type() == 'matrixitem') && $parent.Element().type != 'MultipleChoiceQuestion' && $parent.Element().type != 'SingleChoiceQuestion' -->
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
				<form autocomplete="off">
					<span data-bind="attr: {style: scoring.type() > -1 && scoring.type() != 8 ? 'display: inline-block' : 'display: none'}">
						<input data-label="ruleValue" data-bind="value: scoring.value(), attr: {style: $parent.Element().type == 'FreeTextQuestion' || $parent.Element().type == 'DateQuestion' ? '' : 'width: 50px;'}" type="text" onchange="update(this)" onblur="update(this)" />
						<span data-bind="attr: {style: scoring.type() == 5 ? '' : 'display: none'}"><spring:message code="label.and" /></span>
						<input data-label="ruleValue2" data-bind="value: scoring.value2(), attr: {style: scoring.type() == 5 ? ($parent.Element().type == 'FreeTextQuestion' || $parent.Element().type == 'DateQuestion' ? '' : 'width: 50px;') : 'display: none'}" type="text" onchange="update(this)" onblur="update(this)" />
					</span>
				</form>
			</td>
		</tr>
	<!-- /ko -->
	<tr class="quiz" data-bind="attr: {'data-id': id(), style: $parent.Element().scoring() == 0 ? 'display:none' : ''}">
		<td  class="propertylabel" style="border-bottom: 0px; vertical-align: middle !important;"><spring:message code="label.Points" /></td>
		<td class="propertycontent" style="border-bottom: 0px;">			
			<input data-label="points" data-bind="value: scoring.points(), attr: {'data-pos' : $index()}" class="scoringpointsanswer spinner" type="text" style="width: 100%" onclick="update(this)" />
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
				<button data-bind="attr: {id: 'idBtnSaveFeedback'}" class='btn btn-default btn-primary btn-sm' onclick='save(this)'><spring:message code="label.Apply" /></button>
				<button data-bind="attr: {id: 'idBtnCancelFeedback'}" class='btn btn-default btn-sm' onclick='cancel(this);event.stopPropagation()'><spring:message code="label.Cancel" /></button>
			</div>
		</td>
	</tr>
	<tr data-bind="attr: {style: $parent.Element().scoring() == 0 ? 'display:none' : ''}">
		<td colspan="2" style="border-bottom: 1px dashed #ccc; text-align: right"></td>
	</tr>
	<!-- /ko -->
	<!-- ko if: !(typeof Element()['type'] == 'function' && Element().type() == 'matrixitem') && Element().type != 'MultipleChoiceQuestion' && Element().type != 'SingleChoiceQuestion' && ContentItems().length > 0 -->
	<tr data-bind="attr: {style: Element().scoring() == 0 ? 'display:none' : ''}">
		<td colspan="2" style="text-align: right">	
			<a data-bind="click: function(data, event) { addExpectedAnswer(true) }" class="btn btn-default btn-sm" title="<spring:message code="label.AddExpectedAnswer" />" data-toggle="tooltip"><span class="glyphicon glyphicon-plus"></span></a>
		</td>
	</tr>
	<!-- /ko -->
</script>

<script type="text/html" id="ecf-template">	
	<tr class="firstpropertyrow collapsiblerow quiz">
		<td colspan='2' style="text-align: left; border-bottom: 0px;">
			<div class="toolboxheader">
				<a>
					<span id="ecfpropertiescollapsebutton" class="glyphicon glyphicon-chevron-down" onclick="showHideECFProperties(this)"></span>
					<spring:message code="label.ECFProperties" />
				</a>
			</div>
		</td>
	</tr>		
</script>

<script type="text/html" id="ecfanswerstoscores-template">	
	<tr class="firstpropertyrow ecf">
		<td class="propertylabel" style="border-bottom: 0px;"><spring:message code="label.Answers" /></td>
	</tr>

	<!--  ko foreach: ContentItems() -->
	<tr class="ecf">
		<td class="propertylabel" style="border-bottom: 0px; vertical-align: middle !important; padding-bottom: 10px !important;"></td>
		<td class="propertycontent" style="border-bottom: 0px; padding-bottom:10px !important">			
				<span data-bind="html: title"></span>
		</td>	
	</tr>
	<tr class="ecf">
		<td class="propertylabel" style="border-bottom: 0px; padding-top: 10px !important;"><spring:message code="label.ECF.AnswerScore" /><a style='margin-left: 2px'><span data-toggle='tooltip' title='<spring:message code="label.ECF.AnswerScore" />' class='glyphicon glyphicon glyphicon-question-sign'></span></a></td>
		</td>
		<td class="propertycontent" style="border-bottom: 0px; padding-top: 10px !important;">
				<input name="questionScoreSelection" type="text" data-bind="value: ecfScore" readonly=true/>
		</td>
	</tr>
	<tr>
		<td colspan="2" style="border-bottom: 1px dashed #ccc; text-align: right"></td>
	</tr>
	<!-- /ko -->	
</script>

<script type="text/html" id="ecfanswerstoprofiles-template">	
	<tr class="firstpropertyrow ecf">
		<td class="propertylabel" style="border-bottom: 0px;"><spring:message code="label.Answers" /></td>
	</tr>

	<!--  ko foreach: ContentItems() -->
	<tr class="ecf">
		<td class="propertylabel" style="border-bottom: 0px; vertical-align: middle !important; padding-bottom: 10px !important;"></td>
		<td class="propertycontent" style="border-bottom: 0px; padding-bottom:10px !important">			
				<span data-bind="html: name"></span>
		</td>	
	</tr>
	<tr class="ecf">
		<td class="propertylabel" style="border-bottom: 0px; padding-top: 10px !important;"><spring:message code="label.ECF.AnswerProfile" /><a style='margin-left: 2px'><span data-toggle='tooltip' title='<spring:message code="label.ECF.AnswerProfile" />' class='glyphicon glyphicon glyphicon-question-sign'></span></a></td>
		</td>
		<td class="propertycontent" style="border-bottom: 0px; padding-top: 10px !important;">
			<input name="questionScoreSelection" type="text" data-bind="value: profileUid" readonly=true/>
		</td>
	</tr>
	<tr>
		<td colspan="2" style="border-bottom: 1px dashed #ccc; text-align: right"></td>
	</tr>	
	
	<!-- /ko -->	
</script>

<script type="text/html" id="targetdataset-template">	
	<tr class="firstpropertyrow collapsiblerow selfassessment">
		<td colspan='2' style="text-align: left; border-bottom: 0px;">
			<div style="padding: 10px"><spring:message code="info.targetdatasetcontent" arguments="${contextpath}/${sessioninfo.shortname}/management/parameters?tab=datasets" /></div>
		</td>
	</tr>
</script>

<script type="text/html" id="targetdataset-template2">	
	<tr class="selfassessment">
		<td colspan="2">
			<div class="toolboxheader">
				<a>
					<span class="glyphicon glyphicon-chevron-down" onclick="showHideSAProperties(this)"></span>
					<spring:message code="label.SAProperties" />
				</a>
			</div>
		</td>
	</tr>	
</script>

<script type="text/html" id="saquestion-template">	
	<tr class="firstpropertyrow collapsiblerow selfassessment">
		<td colspan='2' style="text-align: left; border-bottom: 0px;">
			<div class="toolboxheader">
				<a>
					<span class="glyphicon glyphicon-chevron-down" onclick="showHideSAProperties(this)"></span>
					<spring:message code="label.SAProperties" />
				</a>
			</div>
		</td>
	</tr>	
	<tr class="firstpropertyrow collapsiblerow selfassessment">
		<td colspan='2' style="text-align: left; border-bottom: 0px;">
			<div style="padding: 5px"><spring:message code="info.saquestioncontent" arguments="${contextpath}/${sessioninfo.shortname}/management/parameters" /></div>
		</td>
	</tr>
</script>	

<script type="text/html" id="saanswers-template">	
	<tr class="firstpropertyrow selfassessment">
		<td class="propertylabel" style="border-bottom: 0px;"><spring:message code="label.Answers" /></td>
	</tr>

	<!--  ko foreach: ContentItems() -->
	<tr>
		<td class="propertylabel" style="border-bottom: 0px; vertical-align: middle !important; padding-bottom: 10px !important;"></td>
		<td class="propertycontent" style="border-bottom: 0px; padding-bottom:10px !important">			
			<span data-bind="html: title"></span>
		</td>	
	</tr>
	<tr>
		<td class="propertylabel" style="border-bottom: 0px; padding-top: 10px !important;"><spring:message code="label.ECF.AnswerScore" /><a style='margin-left: 2px'><span data-toggle='tooltip' title='<spring:message code="info.ECF.AnswerScore" />' class='glyphicon glyphicon glyphicon-question-sign'></span></a></td>
		</td>

		<td class="propertycontent" style="border-bottom: 0px;">
			<form autocomplete="off">
				<input data-label="sascore" class="saspinner spinner form-control" style="width:80px !important;" type="number" data-bind="value: ecfScore, attr: {'data-id': id}" onfocus='markActiveProperty(this)' onblur='update(this)'/>
			</form>
		</td>
	</tr>
	<tr>
		<td colspan="2" style="border-bottom: 1px dashed #ccc; text-align: right"></td>
	</tr>
	<!-- /ko -->
</script>

<script type="text/html" id="matrixanswerquiz-template">	
	<!--  ko foreach: ContentItems() -->
	<tr class="firstpropertyrow quiz">
        <td class="propertylabel" colspan="2" style="border-bottom: 0px;">
            <spring:message code="label.PointsPerColumn" />
            <a style='margin-left: 2px'><span data-toggle='tooltip' title='<spring:message code="info.PointsPerColumn" />' class='glyphicon glyphicon glyphicon-question-sign'></span></a>
        </td>
    </tr>

	<tr>
		<td colspan="2" class="propertycontent" style="border-bottom: 0px; padding-left: 40px !important; vertical-align: middle !important;"><span data-bind="html: getShortnedText(title())"></span></td>
	</tr>
	<tr>
	    <td class="propertycontent" style="border-bottom: 0px; padding-left: 40px !important;">
			<input id="colpoints" data-label="colpoints" onkeyup="$('#colpointsapply').show()" onchange="$('#colpointsapply').show()" data-bind="value: initialpoints, attr: {'data-pos' : $index()}" class="scoringpointsanswer spinner" type="text" style="width: 100%" />
		</td>
		<td style="border-bottom: 0px; padding-left: 40px !important;">
            <button id="colpointsapply" style="display: none" class="btn btn-primary" onclick="update($('#colpoints'))"><spring:message code="label.Apply" /></button>
        </td>
	</tr>
	<!-- /ko -->
</script>

<div class="modal" id="FormulaDialog" data-backdrop="static">
	<div class="modal-dialog modal-sm">
   		<div class="modal-content">
			<div class="modal-header">
				<spring:message code="label.Function" />
			</div>
			<div class="modal-body">		
				<div style="margin-bottom: 20px; font-weight: bold"><spring:message code="label.SelectAFunction" />:</div>
				<select size="3" class="form-control" id="FormulaFunction" onchange="adaptFormulaInfo()" >
					<option selected="selected" value="mean">mean</option>
					<option value="min">min</option>
					<option value="max">max</option>
				</select>
				<div style="margin-top: 20px">
					<span style="display: none" id="FormulaDialogMean"><spring:message code="info.formulamean" /></span>
					<span style="display: none" id="FormulaDialogMin"><spring:message code="info.formulamin" /></span>
					<span style="display: none" id="FormulaDialogMax"><spring:message code="info.formulamax" /></span>
				</div>
			</div>
			<div class="modal-footer">
				<a id="okStartExportButton" onclick="addFormula($('#FormulaFunction').val());" class="btn btn-primary"><spring:message code="label.OK" /></a>	
				<a class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
			</div>
		</div>
	</div>
</div>