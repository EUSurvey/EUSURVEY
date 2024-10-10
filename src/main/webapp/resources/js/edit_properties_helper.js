function checkGalleryProperties(doupdate)
{
	var selection = $(_elementProperties.selectedelement).find("input[name^=selectable]").val() == "true";
	if (selection)
	{
		$("#properties").find("td[data-label=MaxSelections]").closest("tr").find("input").spinner( "option", "disabled", false );
		$("#properties").find("td[data-label=Mandatory]").closest("tr").find("input").removeAttr("disabled");
	} else {
		$("#properties").find("td[data-label=MaxSelections]").closest("tr").find("input").spinner( "option", "disabled", true );
		$("#properties").find("td[data-label=Mandatory]").closest("tr").find("input").prop("disabled", "disabled").removeAttr("checked");
		if (doupdate)
		update($("#properties").find("td[data-label=Mandatory]").closest("tr").find("input").first());
	}
}

function toggleAdvancedProperties(button)
{
	if ($(button).parent().find(".glyphicon-minus-sign").length > 0)
	{
		$(button).parent().find(".glyphicon-minus-sign").removeClass("glyphicon-minus-sign").addClass("glyphicon-plus-sign");
		$(button).closest("tr").nextUntil(".quiz").addClass("hideme");
	} else {
		$(button).parent().find(".glyphicon-plus-sign").removeClass("glyphicon-plus-sign").addClass("glyphicon-minus-sign");
		$(button).closest("tr").nextAll(":not(.tinymcerow, .quiz)").removeClass("hideme");
		//registration form area
		$(button).closest("tr").nextUntil(".quiz").find(".glyphicon-plus-sign").removeClass("glyphicon-plus-sign").addClass("glyphicon-minus-sign");
	}
}

function toggleQuizProperties(button)
{
	if ($(button).parent().find(".glyphicon-minus-sign").length > 0)
	{
		$(button).parent().find(".glyphicon-minus-sign").removeClass("glyphicon-minus-sign").addClass("glyphicon-plus-sign");
		$(button).closest("tr").nextUntil(".collapsiblerow").hide();
	} else {
		$(button).parent().find(".glyphicon-plus-sign").removeClass("glyphicon-plus-sign").addClass("glyphicon-minus-sign");
		$(button).closest("tr").nextUntil(".collapsiblerow").not(".tinymcerow").show();
	}
}

function toggleSelectIDProperties(button)
{
	if ($(button).parent().find(".glyphicon-minus-sign").length > 0)
	{
		$(button).parent().find(".glyphicon-minus-sign").removeClass("glyphicon-minus-sign").addClass("glyphicon-plus-sign");
		$(button).closest("tr").next().hide();
	} else {
		$(button).parent().find(".glyphicon-plus-sign").removeClass("glyphicon-plus-sign").addClass("glyphicon-minus-sign");
		$(button).closest("tr").next().show();
	}
}

function showHideElementProperties(span)
{
	var tr = $(".properties").find("tr").first();
	if ($(span).hasClass("glyphicon-chevron-down"))
	{
		$(tr).hide(400);
		$(tr).nextUntil(".quiz").addClass("hideme2");
		$(span).removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-right");
	} else {
		$(tr).show(400);
		$(tr).nextUntil(".quiz").removeClass("hideme2");
		$(span).removeClass("glyphicon-chevron-right").addClass("glyphicon-chevron-down");
	}
}

var lastQuizPropertiesVisible = true;
function showHideQuizProperties(span)
{
	var tr = $(span).closest("tr");
	if ($(span).hasClass("glyphicon-chevron-down"))
	{
		lastQuizPropertiesVisible = false;
		$(tr).nextAll().hide(400);
		$(span).removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-right");
	} else {
		lastQuizPropertiesVisible = true;
		if ($(tr).next().find(".quizquestioncheck").first().is(":checked"))
		{
			$(tr).nextAll().not(".hideme").show(400);
		} else {
			$(tr).next().show(400);
		}		
		
		$(span).removeClass("glyphicon-chevron-right").addClass("glyphicon-chevron-down");
	}
}

function showHideECFProperties(span)
{
	console.log("showHideECFProperties(span)");
	var tr = $(span).closest("tr");
	if ($(span).hasClass("glyphicon-chevron-down"))
	{
		//lastQuizPropertiesVisible = false;
		$(tr).nextAll().hide(400);
		$(span).removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-right");
	} else {
		//lastQuizPropertiesVisible = true;
		if ($(tr).next().find(".ecfquestioncheck").first().is(":checked"))
		{
			$(tr).nextAll().not(".hideme").show(400);
		} else {
			$(tr).next().show(400);
		}		
		
		$(span).removeClass("glyphicon-chevron-right").addClass("glyphicon-chevron-down");
	}
}

function getAdvancedPropertiesRow()
{
	var row = new PropertyRow();
	row.Type("advanced");
	_elementProperties.propertyRows.push(row);
}

function getQuizPropertiesRow()
{
	var row = new PropertyRow();
	row.Type("quiz");
	_elementProperties.propertyRows.push(row);
}

function getECFPropertiesRow()
{
	var row = new PropertyRow();
	row.Type("ecf");
	_elementProperties.propertyRows.push(row);
}


function getSliderPropertiesRow()
{
	var row = new PropertyRow();
	row.Type("slider");
	_elementProperties.propertyRows.push(row);
}

function toggleRegistrationFormProperties(button)
{
	if ($(button).parent().find(".glyphicon-minus-sign").length > 0)
	{
		$(button).parent().find(".glyphicon-minus-sign").removeClass("glyphicon-minus-sign").addClass("glyphicon-plus-sign");
		$(button).closest("tr").nextUntil(".collapsiblerow").addClass("hideme");
	} else {
		$(button).parent().find(".glyphicon-plus-sign").removeClass("glyphicon-plus-sign").addClass("glyphicon-minus-sign");
		$(button).closest("tr").nextUntil(".collapsiblerow").removeClass("hideme");
	}
}

function toggleCellProperties(button)
{
	if ($(button).parent().find(".glyphicon-minus-sign").length > 0)
	{
		$(button).parent().find(".glyphicon-minus-sign").removeClass("glyphicon-minus-sign").addClass("glyphicon-plus-sign");
		$(button).closest("tr").nextUntil(".collapsiblerow").addClass("hideme");
	} else {
		$(button).parent().find(".glyphicon-plus-sign").removeClass("glyphicon-plus-sign").addClass("glyphicon-minus-sign");
		$(button).closest("tr").nextAll(":not(.tinymcerow)").removeClass("hideme");
	}
}

function markActiveProperty(input) {
	$(input).addClass("activeproperty");
}

var idcounter = 1;
function getTextPropertiesRow(label, content, usetinymce, unit, maxLength)
{
	var row = new PropertyRow();
	row.Type("first");
	row.Label(label);
	row.LabelTitle(getPropertyLabel(label));
	if (label == "Size" && $(_elementProperties.selectedelement).hasClass("imageitem"))
	{
		row.LabelTitle(row.LabelTitle() + getPropertyLabel("SizeInfo"));
	} else if (label == "DescriptiveText" && $(_elementProperties.selectedelement).hasClass("imageitem"))
	{
		row.LabelTitle(row.LabelTitle() + getPropertyLabel("DescriptiveTextInfo"));
	} else if (label == "LongDescription")
	{
		row.LabelTitle(row.LabelTitle() + getPropertyLabel("LongDescriptionInfo"));
	} else if (label == "ConfirmationText")
	{
		row.LabelTitle(row.LabelTitle() + getPropertyLabel("ConfirmationTextInfo"));
	} else if (label == "RegularExpression")
	{
		row.LabelTitle(row.LabelTitle() + getPropertyLabel("RegularExpressionInfo"));
	} else if (label == "FileType")
	{
		row.LabelTitle(row.LabelTitle() + getPropertyLabel("FileTypeInfo"));
	}
	
	var id = "id" + idcounter++;	
	if (usetinymce)
	{
		var rowcontent = '<div class="rightaligned"><span data-toggle="tooltip" data-placement="left" title="' + getPropertyLabel("Edit") + '" class="glyphicon glyphicon-pencil" onclick="edit(this)" id="idEdit' + label +'"></span>';
		
		if (label == "Help")
		{
			rowcontent += '<span class="glyphicon glyphicon-trash" data-toggle="tooltip" data-placement="left" title="' + getPropertyLabel("Remove") + '" onclick="resetHelp(this)"></span>';
		} else if (label == "ConfirmationText")
		{
			rowcontent += '<span class="glyphicon glyphicon-trash" data-toggle="tooltip" data-placement="left" title="' + getPropertyLabel("Remove") + '" onclick="resetConfirmationText(this)"></span>';
		}
		
		rowcontent += '</div>';
		
		rowcontent += "<div class='propertytextpreview' onclick='edit(this)'>" + getShortnedText(content) + "</div>";
		row.Content(rowcontent);
		_elementProperties.propertyRows.push(row);
		
		row = new PropertyRow();
		row.Label(label);
		row.Type("TinyMCE");
		row.TinyMCEId(id);
		row.TinyMCEContent(content);
		_elementProperties.propertyRows.push(row);
		
		tinyMCE.settings = myConfigSettingEditor;
		tinymce.EditorManager.execCommand('mceToggleEditor', true, id);
	} else {
		var rowcontent = "<input type='text' id='" + id + "' value='" + content + "' onfocus='markActiveProperty(this)' onblur='update(this)' "

		if (maxLength) {
			rowcontent += "maxlength='" + maxLength + "'";
		}

		rowcontent += "/>";

		if (unit != null)
		{
			rowcontent += "<span style='margin-left: 5px'>" + unit + "</span>"
		}
		
		if (label == "FileType")
		{
			row.ContentType("filetype");
		}
		
		row.Content(rowcontent);
		_elementProperties.propertyRows.push(row);
	}
}

function InitNumberElements(row) {
	let selectedQuestion = $("#content").find(".selectedquestion").first()
	let selectedelementId = selectedQuestion.attr("id");
	if (selectedelementId == null){
		selectedelementId = selectedQuestion.attr("data-id");
	}
	let highlightedIds = []
	$("#content").find(".highlightedquestion").each(function(){
		highlightedIds.push($(this).attr("data-id"))
	})
	for (let i = 0; i < modelsForNumber.length; i++) {
		let model = modelsForNumber[i];
		model.limitedTitle = getLimitedText(model.title());
		row.NumberElements.push(model);
	}
	for (let i = 0; i < modelsForRegEx.length; i++) {
		let model = modelsForRegEx[i];
		model.limitedTitle = getLimitedText(model.title());
		row.NumberElements.push(model);
	}
	for (let i = 0; i < modelsForFormula.length; i++) {
		let model = modelsForFormula[i];
		if (model.id() != selectedelementId && highlightedIds.indexOf(model.id()) < 0) {
			model.limitedTitle = getLimitedText(model.title());
			row.NumberElements.push(model);
		}
	}
}

function getFormulaPropertiesRow(formulatext) {
	var id = "id" + idcounter++;	
	var row = new PropertyRow();
	row.Label("formula");
	row.LabelTitle(getPropertyLabel("formula"));
	row.Type("FormulaOperators");
	row.FormulaInputId(id)		
	row.Value(formulatext);	
	InitNumberElements(row);
	_elementProperties.propertyRows.push(row);		
}

function getCellPropertiesRow(cellID) {
	var parentID = $("#content").find(".selectedquestion").closest(".survey-element").first().attr("id");
	var table = _elements[parentID];
	var cell = table.getChildbyId(cellID);
	var row = new PropertyRow();
	row.Element(table);
	row.Cell(cell);
	row.Label("cell");
	row.LabelTitle(getPropertyLabel("cell"));
	row.Type("CellProperties");
	let id = "id" + idcounter++;
	row.TinyMCEId(id);
	InitNumberElements(row);
	_elementProperties.propertyRows.push(row);
		
	tinyMCE.settings = myConfigSettingEditor;
	tinymce.EditorManager.execCommand('mceToggleEditor', true, id);
	tinymce.EditorManager.execCommand('mceToggleEditor', true, id + "help");
	
	tinyMCE.settings = myConfigSetting2Editor;
	tinymce.EditorManager.execCommand('mceToggleEditor', true, id + "possibleAnswers");
	
	id = "id" + idcounter++;
	row.FormulaInputId(id)
	
	var input = $("#" + cellID + "min");
	$(input).spinner({ decimals:2, min:0, max:5000, start:"", allowNull: true });
	$(input).parent().find('.ui-spinner-button').click(function() {
		update($("#" + cellID + "min"));
	});
	$(input).blur(function() {
		  update(this)
	});	
	
	input = $("#" + cellID + "max");
	$(input).spinner({ decimals:2, min:0, max:5000, start:"", allowNull: true });
	$(input).parent().find('.ui-spinner-button').click(function() {
		update($("#" + cellID + "max"));
	});
	$(input).blur(function() {
		  update(this)
	});
	
	input = $("#" + cellID + "minValue");
	$(input).spinner({ decimals:0, start:"", allowNull: true });
	$(input).parent().find('.ui-spinner-button').click(function() {
		update($("#" + cellID + "minValue"));
	});
	$(input).blur(function() {
		  update(this)
	});	
	
	input = $("#" + cellID + "maxValue");
	$(input).spinner({ decimals:0, start:"", allowNull: true });
	$(input).parent().find('.ui-spinner-button').click(function() {
		update($("#" + cellID + "maxValue"));
	});
	$(input).blur(function() {
		  update(this)
	});
	
	input = $("#" + cellID + "minC");
	$(input).spinner({ decimals:0, start:"", allowNull: true });
	$(input).parent().find('.ui-spinner-button').click(function() {
		update($("#" + cellID + "minC"));
	});
	$(input).blur(function() {
		  update(this)
	});	
	
	input = $("#" + cellID + "maxC");
	$(input).spinner({ decimals:0, start:"", allowNull: true });
	$(input).parent().find('.ui-spinner-button').click(function() {
		update($("#" + cellID + "maxC"));
	});
	$(input).blur(function() {
		  update(this)
	});
}

function getCellHeaderPropertiesRow(cellID, isRow) {
	var parentID = $("#content").find(".selectedquestion").closest(".survey-element").first().attr("id");
	var table = _elements[parentID];
	var cell = table.getChildbyId(cellID);
	var row = new PropertyRow();
	row.Element(table);
	row.Cell(cell);
	row.Label(isRow ? "row" : "column");
	row.LabelTitle(getPropertyLabel(row.Label()));
	row.Type("HeaderCellProperties");
	let id = "id" + idcounter++;
	row.TinyMCEId(id);

	row.FormulaInputId("id" + idcounter++)

	InitNumberElements(row);
	
	const types = [];
	const titles = [];
	const numRows = [];
	let allMandatorySame = true;
	let lastOptional = null;

	const formulas = [];
	const minValues = [];
	const maxValues = [];
	const decimals = [];
	const units = [];

	let possibleAnswers = null
	let possibleAnswersMatch = true

	$(".highlightedquestion").each(function() {
		const childId = $(this).attr("data-id");
		let type = $(this).attr("data-type");
		if (type.length == 0) type = "0";
		if (types.indexOf(type) < 0) {
			types.push(type);
		}
		const child = table.getChildbyId(childId);
		const title = child == null ? "" : child.title();
		if (titles.indexOf(title) < 0) {
			titles.push(title);
		}
		if (child != null) {
			if (lastOptional != null && child.optional() !== lastOptional){
				allMandatorySame = false
			}
			lastOptional = child.optional()
		}
		const rows = child == null ? "" : child.numRows();
		if (numRows.indexOf(rows) < 0) {
			numRows.push(rows);
		}

		const formula = child == null ? "" : child.formula();
		if (formulas.indexOf(formula) < 0) {
			formulas.push(formula);
		}

		if (child == null){
			//ignore
		} else if (type == 2){
			if (minValues.indexOf(child.minCharacters()) < 0) {
				minValues.push(child.minCharacters());
			}
			if (maxValues.indexOf(child.maxCharacters()) < 0) {
				maxValues.push(child.maxCharacters());
			}
		} else if (type == 4 || type == 5){
			if (minValues.indexOf(child.minChoices()) < 0) {
				minValues.push(child.minChoices());
			}
			if (maxValues.indexOf(child.maxChoices()) < 0) {
				maxValues.push(child.maxChoices());
			}
		} else {
			if (minValues.indexOf(child.min()) < 0) {
				minValues.push(child.min());
			}
			if (maxValues.indexOf(child.max()) < 0) {
				maxValues.push(child.max());
			}
		}

		const decimalPlaces = child == null ? "" : child.decimalPlaces();
		if (decimals.indexOf(decimalPlaces) < 0) {
			decimals.push(decimalPlaces);
		}

		const unit = child == null ? "" : child.unit();
		if (units.indexOf(unit) < 0) {
			units.push(unit);
		}

		const answers = child == null ? null : child.possibleAnswers()
		if (possibleAnswers == null){
			possibleAnswers = answers
		} else if (possibleAnswersMatch && answers != null){
			if (possibleAnswers.length === answers.length){
				for (let i = 0; i < answers.length; i++){
					if (answers[i].title() !== possibleAnswers[i].title()){
						possibleAnswersMatch = false
						break;
					}
				}
			} else {
				possibleAnswersMatch = false;
			}
		}
	})
	
	if (types.length > 1) {
		cell.cellTypeChildren(-1);
	} else if (types.length == 1) {
		cell.cellTypeChildren(types[0]);
	}
	
	if (titles.length > 1) {
		cell.titleChildren("");
	} else if (titles.length == 1) {
		cell.titleChildren(titles[0]);
	}

	if (allMandatorySame) {
		cell.optionalChildren(lastOptional !== false); //!== false removes null case
		cell.optionalIndeterminate(false)
	} else {
		cell.optionalChildren(false);
		cell.optionalIndeterminate(true)
	}

	if (numRows.length > 1) {
		cell.numRowsChildren("");
	} else if (numRows.length == 1) {
		cell.numRowsChildren(numRows[0]);
	}

	if (formulas.length > 1) {
		cell.formulaChildren("");
	} else if (formulas.length === 1) {
		cell.formulaChildren(formulas[0]);
	}

	if (minValues.length > 1) {
		cell.minChildren("");
	} else if (minValues.length === 1) {
		cell.minChildren(minValues[0]);
	}

	if (maxValues.length > 1) {
		cell.maxChildren("");
	} else if (maxValues.length === 1) {
		cell.maxChildren(maxValues[0]);
	}

	if (decimals.length > 1) {
		cell.decimalsChildren("");
	} else if (decimals.length === 1) {
		cell.decimalsChildren(decimals[0]);
	}

	if (units.length > 1) {
		cell.unitChildren("");
	} else if (units.length === 1) {
		cell.unitChildren(units[0]);
	}

	cell.possibleAnswersMatch(possibleAnswersMatch);
	cell.possibleAnswers.removeAll();
	cell.possibleAnswersChildren.removeAll();
	if (possibleAnswersMatch && possibleAnswers != null){
		cell.possibleAnswers.push(...possibleAnswers);
		cell.possibleAnswersChildren.push(...possibleAnswers);
	}

	_elementProperties.propertyRows.push(row);
	tinyMCE.settings = myConfigSettingEditor;
	tinymce.EditorManager.execCommand('mceToggleEditor', true, id);
	tinymce.EditorManager.execCommand('mceToggleEditor', true, id + "children");
	
	tinyMCE.settings = myConfigSetting2Editor;
	tinymce.EditorManager.execCommand('mceToggleEditor', true, id + "possibleAnswers");
}

function getFirstCellPropertiesRow(cellID) {
	var parentID = $("#content").find(".selectedquestion").closest(".survey-element").first().attr("id");
	var table = _elements[parentID];
	var cell = table.getChildbyId(cellID);
	var row = new PropertyRow();
	row.Element(table);
	row.Cell(cell);
	row.Label("cell");
	row.LabelTitle(getPropertyLabel("cell"));
	row.Type("FirstCellProperties");
	let id = "id" + idcounter++;
	row.TinyMCEId(id);
	_elementProperties.propertyRows.push(row);

	tinyMCE.settings = myConfigSettingEditor;
	tinymce.EditorManager.execCommand('mceToggleEditor', true, id);
}

function getCleanArray(arr)
{
	var result = [];
	for (var i = 0; i < arr.length; i++) {
		if (arr[i] != null && arr[i].length > 0)
		{
			result.push(arr[i]);
		}		
	}
	return result;
}

function getVisibilityRow(multiselection, isVisible = true)
{
	var label = "Visibility";
	
	var row = new PropertyRow();
	row.Type("first");
	row.ContentType("visibility");
	row.Label(label);
	row.LabelTitle(getPropertyLabel(label));
	row.IsVisible(isVisible);
	
	var selectedelement = $("#content").find(".selectedquestion").first();
	var element = _elements[$(selectedelement).attr("data-id")];
	
	var mytriggers;
	var empty = true;
	var existingtrigger = false;
	
	if ($(selectedelement).attr("data-triggers") || ($(selectedelement).hasClass("matrix-header") && $(selectedelement).parent().attr("data-triggers")))
	{	
		if ($(selectedelement).hasClass("matrix-header"))
		{
			mytriggers = getCleanArray($(selectedelement).parent().attr("data-triggers").split(";"));
		} else {
			mytriggers = getCleanArray($(selectedelement).attr("data-triggers").split(";"));
		}
		
		if (mytriggers.length > 0) existingtrigger = true;
	
		if (multiselection)
		{		
			$("#content").find(".selectedquestion").each(function(){
				if ($(this).attr("data-triggers") || ($(this).hasClass("matrix-header") && $(this).parent().attr("data-triggers")))
				{				
					var mytriggers2;
					if ($(this).hasClass("matrix-header"))
					{
						mytriggers2 = getCleanArray($(this).parent().attr("data-triggers").split(";"));
					} else {
						mytriggers2 = getCleanArray($(this).attr("data-triggers").split(";"));
					}
					
					if (mytriggers2.length > 0) existingtrigger = true;
					mytriggers = getIntersect(mytriggers, mytriggers2);
				} else {
					mytriggers = [];
				}
			});
		}

		var triggers = document.createElement("div");
		
		if (!multiselection && $(selectedelement).hasClass("matrix-header"))
		{
			$(triggers).addClass("triggers").append("<b>" + getPropertyLabel("visibleIfMatrixOrTriggered") + "</b>:<br />");
		} else {
			
			if (element.useAndLogic())
			{
				$(triggers).addClass("triggers").append("<b>" + getPropertyLabel("visibleIfTriggeredAnd") + "</b>:<br />");
			} else {
				$(triggers).addClass("triggers").append("<b>" + getPropertyLabel("visibleIfTriggered") + "</b>:<br />");
			}
		}
				
		for (var i = 0; i < mytriggers.length; i++)
		{
			var id = mytriggers[i];
			
			if (id.length > 0)
			{
				let separator = (i == mytriggers.length-1) ? "" : ", ";
				if (id.indexOf("|") == -1)
				{	
					//triggered by a possible answer
					var text = strip_tags($("textarea[data-id='" + mytriggers[i] + "']").first().text());
					var pashortname = $("input[data-id='" + mytriggers[i] + "'][name^='pashortname']").val();

					var a = document.createElement("a");			
					$(a).attr("data-targetid", id).html(text + " (" + pashortname + ")" + separator);
					$(triggers).append(a);
				} else {
					//triggered by a matrix cell
					var ids = id.split("|");
					var td = $("td[id=" + ids[1] + "]").first();
					var text = strip_tags($(td).find("textarea[name^='text']").text());
					var pashortname = $(td).find("input[name^='shortname']").val()
					
					var a = document.createElement("a");			
					$(a).attr("data-targetid", id).html(text + " (" + pashortname + ")" + separator);
					$(triggers).append(a);
				}
				empty = false;
			}
		}
		if (!empty)
		{
			row.Content($(triggers).html());
			_elementProperties.propertyRows.push(row);
			
			$(".triggers").find("a[data-targetid]").each(function(){
				setHoverOnVisibilityLink(this);
			});
			
			return;
		}		
	} else {
		if (multiselection)
		{		
			$("#content").find(".selectedquestion").each(function(){
				if ($(this).attr("data-triggers") || ($(this).hasClass("matrix-header") && $(this).parent().attr("data-triggers")))
				{				
					var mytriggers2;
					if ($(this).hasClass("matrix-header"))
					{
						mytriggers2 = $(this).parent().attr("data-triggers").split(";");
					} else {
						mytriggers2 = $(this).attr("data-triggers").split(";");
					}
					
					if (mytriggers2.length > 0) existingtrigger = true;
				}
			});
		}

	}
	
	if (existingtrigger)
	{
		row.Content("<div class='triggers'>" + getPropertyLabel("differentVisibility") + "</div>");
		_elementProperties.propertyRows.push(row);
	} else {
		if (!multiselection && $(selectedelement).hasClass("matrix-header"))
		{
			row.Content("<div class='triggers'>" + getPropertyLabel("visibleIfMatrixVisible") + "</div>");
		} else {
			row.Content("<div class='triggers'>" + getPropertyLabel("alwaysVisible") + "</div>");
		}
		_elementProperties.propertyRows.push(row);
	}
}

var quizanswersrow;

function getECFPropertiesContent() {
	console.log("getECFPropertiesContent()");
	let selectedelement = $("#content").find(".selectedquestion").first();
	let element = _elements[$(selectedelement).attr("data-id")];
		
	let row = new PropertyRow();
	row.Element(element);
	row.Type("first");
	row.ContentType("ecfquestion");
	row.Label("ECFProfileSelection");
	row.LabelTitle(getPropertyLabel("ECFProfileSelection")); 
	let selected = false;

	for (var i = 0; i < element.possibleAnswers().length; i++) {
		selected = element.possibleAnswers()[i].ecfProfile() != null;
	}
	
	row.Value(selected);
	_elementProperties.propertyRows.push(row);

	// ECF PROFILE SELECTION
	if (selected) {
		// row = new PropertyRow();
		// row.Element(element);
		// row.Type("first");
		// row.ContentType("ecfCompetencySelection");
		// row.Label("ECFSelectedProfile");
		// row.LabelTitle(getPropertyLabel("ECFSelectedProfile")); 
		// row.Value("");
		// _elementProperties.propertyRows.push(row);
		
		row = new PropertyRow();
		row.Element(element);
		row.Type("ecfAnswersToProfiles");
		row.ContentType("ecfAnswersToProfiles");
		row.Label("ECFSelectedProfile");
		row.LabelTitle(getPropertyLabel("ECFSelectedProfile")); 
		
		if (element.type === "SingleChoiceQuestion")
		{
			for (let i = 0; i < element.possibleAnswers().length; i++)
			{
				let profile = element.possibleAnswers()[i].ecfProfile();
				row.ContentItems.push(profile);
			}
		} 
		_elementProperties.propertyRows.push(row);
	}
	
	row = new PropertyRow();
	row.Element(element);
	row.Type("first");
	row.ContentType("ecfquestion");
	row.Label("ECFCompetencyQuestion");
	row.LabelTitle(getPropertyLabel("ECFCompetencyQuestion")); 
	selected = element.ecfCompetency() != null;
	row.Value(selected);
	_elementProperties.propertyRows.push(row);
	
	
	if (selected) {
		row = new PropertyRow();
		row.Element(element);
		row.Type("first");
		row.ContentType("ecfCompetencySelection");
		row.Label("ECFSelectedCompetency");
		row.LabelTitle(getPropertyLabel("ECFSelectedCompetency")); 
		row.Value(element.ecfCompetency().name);
		_elementProperties.propertyRows.push(row);
		
		row = new PropertyRow();
		row.Element(element);
		row.Type("ecfAnswersToScores");
		row.ContentType("ecfAnswersToScores");
		row.Label("ECFSelectedCompetency");
		row.LabelTitle(getPropertyLabel("ECFSelectedCompetency")); 
		
		if (element.type === "SingleChoiceQuestion")
		{
			for (let i = 0; i < element.possibleAnswers().length; i++)
			{
				let pa = element.possibleAnswers()[i];
				row.ContentItems.push(pa);
			}
		} 
		_elementProperties.propertyRows.push(row);
	}
}

function getQuizPropertiesContent()
{
	var selectedelement = $("#content").find(".selectedquestion").first();
	var element = _elements[$(selectedelement).attr("data-id")];
	
	var row = new PropertyRow();
	row.Element(element);
	row.Type("first");
	row.ContentType("quizquestion");
	row.Label("QuizQuestion");
	row.LabelTitle(getPropertyLabel("QuizQuestion")); 
	row.NumValue(element.points());
	row.Value(element.scoring() != null ? element.scoring() > 0 : true);
		
	_elementProperties.propertyRows.push(row);
	
	row = new PropertyRow();
	row.Element(element);
	row.Type("first");
	row.ContentType("scoring");
	row.Label("Scoring");
	row.LabelTitle(getPropertyLabel("Points")); 
	row.NumValue(element.points());
	row.Value(element.scoring() != null ? element.scoring().toString() : "0");
		
	_elementProperties.propertyRows.push(row);
	
	$(".scoringpoints").each(function(){
		var input = this;
		$(this).spinner({ decimals:0, min:0, start:"", allowNull: true });
		
		if (element.scoring() != 1)
		{
			$(this).spinner( "option", "disabled", true);
		}
		
		$(this).removeAttr("disabled");
		
		$(this).parent().find('.ui-spinner-button').click(function() {
			update(input);
		});
		
		$(this).blur(function() {
			  update(this)
		});	
	});
	
	row = new PropertyRow();
	row.Element(element);
	row.Type("quizanswers");
	row.ContentType("quizanswers");
	row.Label("Rules");
	row.LabelTitle(getPropertyLabel("Rules")); 
	
	if (element.type == "SingleChoiceQuestion" || element.type == "MultipleChoiceQuestion")
	{
		for (var i = 0; i < element.possibleAnswers().length; i++)
		{
			var pa = element.possibleAnswers()[i];
			row.ContentItems.push(pa);
		}
	} else {
		if (element.scoringItems() != null)
		for (var i = 0; i < element.scoringItems().length; i++)
		{
			var scoring = element.scoringItems()[i];	
			row.ContentItems.push(new newScoringWrapper(scoring));
		}
	}	
	
	_elementProperties.propertyRows.push(row);
	
	if (element.type == 'DateQuestion')
	{
		$("input[data-label=ruleValue]").each(function(){
			createDatePickerForEditor(this, null);
		})
		$("input[data-label=ruleValue2]").each(function(){
			createDatePickerForEditor(this, null);
		})
	}
	
	quizanswersrow = row;
	initQuizElements(element);
	
	if ($(".quizrule").length == 0)
	{
		row.addExpectedAnswer(false);
	}
}

function initQuizElements(element)
{
	$(".scoringpointsanswer").each(function(){
		var input = this;
		$(this).spinner({ decimals:0, start:"", allowNull: true });
		
		if (element.scoring() != 2)
		{
			$(this).spinner( "option", "disabled", true);
		}	
		
		$(this).parent().find('.ui-spinner-button').click(function() {
			update(input);
		});
		
		$(this).blur(function() {
			  update(this)
		});	
	});
	
	checkQuizOtherValues();
	
	for (var i = 0; i < quizanswersrow.ContentItems().length; i++)
	{
		var textarea = $("#feedback" + quizanswersrow.ContentItems()[i].id());
		if ($(textarea).parent().find(".mce-tinymce").length == 0)
		tinymce.EditorManager.execCommand('mceToggleEditor', true, "feedback" + quizanswersrow.ContentItems()[i].id());
	}
}

function checkQuizOtherValues()
{
	var otherFound = $(".ruleValueType").find("option[value='other']:selected").length > 0;
	
	var val = $('input[name=scoring]:checked').last().val();
	if (otherFound)
	{
		$(".ruleValueType").find("option[value='other']:not(:selected)").hide();
	} else {
		$(".ruleValueType").find("option[value='other']").show();
	}
	
	var emptyFound = $(".ruleValueType").find("option[value='empty']:selected").length > 0;
	if (emptyFound)
	{
		$(".ruleValueType").find("option[value='empty']:not(:selected)").hide();
	} else {
		$(".ruleValueType").find("option[value='empty']").show();
	}
}

function getIntersect(arr1, arr2) {
    var r = [], o = {}, l = arr2.length, i, v;
    for (i = 0; i < l; i++) {
        o[arr2[i]] = true;
    }
    l = arr1.length;
    for (i = 0; i < l; i++) {
        v = arr1[i];
        if (v in o) {
            r.push(v);
        }
    }
    return r;
}

function getNumberPropertiesRow(label, value)
{
	var row = new PropertyRow();
	row.Type("first");
	row.Label(label);
	row.LabelTitle(getPropertyLabel(label));
	row.ContentType("number");
	
	if (label == "MaxDistanceToMedian" && value == "-1") 
	{
		value = "";
	}	
	row.Value(value);
	
	var inputid = getNewId();
	
	var item = new ContentItem();
	item.Id(inputid);
	item.Value(value);
	row.ContentItems.push(item);
		
	_elementProperties.propertyRows.push(row);
	
	var input = $("#" + inputid);
	
	if (label == "NumIcons")
	{
		$(input).spinner({ decimals:0, min:2, max:10, start:"", allowNull: true });
	} else if (label == "MaxDistanceToMedian")
	{
		$(input).spinner({ decimals:2, min:0, start:"", allowNull: true });
	} else {
		$(input).spinner({ decimals:0, min:0, start:"", allowNull: true });
	}	
	
	$(input).parent().find('.ui-spinner-button').click(function() {
		update(input);
	});
	
	$(input).blur(function() {
		  update(this)
	});	
}

function getChoosePropertiesRow(label, content, multiple, edit, value, isMultipleChoiceItem)
{
	var row = new PropertyRow();
	row.Type("first");

	row.LabelTitle(getPropertyLabel(label));
	row.Label(label);

	if (label == "DelphiChartTypeNumber") {
		label = "DelphiChartType";
		row.Label(label);
	}

	var options = content.split(",");
	var name = getNewId();
	const radioLabels = ["Style", "Order", "OrderSection", "Display", "DisplaySlider", "InitialSliderPosition"]
	if (radioLabels.includes(label)) {
		row.ContentType("radio");
		row.Content(options);
		for (var i = 0; i < options.length; i++)
		{
			var item = new ContentItem();
			item.Id('select' + options[i]);
			item.Name(name);
			item.Value(options[i]);
			item.Label(getPropertyLabel(options[i]));
			if (options[i] == value || i == value)
			{
				item.Selected(options[i]);
			}
			row.ContentItems.push(item);
		}
	} else if (label == "Align") {
		row.ContentType("align");
		row.Value(value);
		edit = false;	
	} else {
		row.ContentType("select");
		
		for (var i = 0; i < options.length; i++)
		{
			if (label == "Size")
			{
				var tabletype;
				switch (value)
				{
					case 0:
						tabletype = "fitToContent";
						break;
					case 1:
						tabletype = "fitToPage";
						break;					
					case 2:
						tabletype = "manualColumnWidth";
						break;
				}
				value = tabletype;
			}
			
			if (label == "IconType")
			{
				var icontype;
				switch (value)
				{
					case 0:
						icontype = "Start";
						break;
					case 1:
						icontype = "Circles";
						break;					
					case 2:
						icontype = "Hearts";
						break;
				}
				value = icontype;
			}
			
			var item = new ContentItem();
			item.Value(options[i]);
			
			if (label == "MaximumFileSize") {
				item.Label(options[i] + " MB");
			} else {			
				item.Label(getPropertyLabel(options[i]));
			}
			
			if (options[i] == value)
			{
				item.Selected(true);
			}
			
			row.ContentItems.push(item);
		}
	}
	if (edit)
	{
		row.Edit(true);
	}
	
	_elementProperties.propertyRows.push(row);
}

function getChooseColor(label, value)
{
	var row = new PropertyRow();
	row.Type("first");
	row.ContentType("color");
	row.Label(label);
	row.LabelTitle(getPropertyLabel(label));
	row.Value(value);

	var id = "id" + idcounter++;
	_elementProperties.propertyRows.push(row);
	
	$(".spectrum").spectrum({
		preferredFormat: "hex"
	});
}

function getCheckPropertiesRow(label, value, disabled = false)
{
	var row = new PropertyRow();
	row.Type("first");
	row.ContentType("checkbox");
	row.Disabled(disabled);
	row.Label(label);
	row.LabelTitle(getPropertyLabel(label));
	row.Value(value);

	var id = "id" + idcounter++;
	_elementProperties.propertyRows.push(row);

	if(label == "Interdependency" && value){
		checkInterdependentMatrix($("#idPropertyInterdependency").closest(".firstpropertyrow"));
	}
}

function getRegistrationFormRow(attrvalue, namevalue)
{
	var row = new PropertyRow();
	row.Type("registration");
	row.Value(attrvalue);
	
	if ($("#regform").val() != "true")
	{
		row.Disabled(true);
	}
	
	row.Content(namevalue);	
	var id = "id" + idcounter++;	
	_elementProperties.propertyRows.push(row);
}

function createDatePickerForEditor(instance, othervalue)
{
	if (othervalue == "") othervalue = null;
	
	$(instance).addClass("datepicker").attr("placeholder", "DD/MM/YYYY").datepicker({
		dateFormat: 'dd/mm/yy',
		showButtonPanel: true,
		
		 onSelect: function(dateText, inst) {
			update(this);
			
			if ($(this).attr("data-to"))
			{
				var myDate = $(this).datepicker('getDate'); 
	            myDate.setDate(myDate.getDate()+1); 
				
				$("#" + $(this).attr("data-to")).datepicker( "option", "minDate", myDate );
				
				$("#" + $(this).attr("data-to")).removeClass (function (index, css) {
				    return (css.match (/(^|\s)min\S+/g) || []).join(' ');
				});
				
				$("#" + $(this).attr("data-to")).addClass("min" + dateText.replace(/\//g,""));						
			} else if ($(this).attr("data-from"))
			{
				var myDate = $(this).datepicker('getDate'); 
	            myDate.setDate(myDate.getDate()-1); 
				
				$("#" + $(this).attr("data-from")).datepicker( "option", "maxDate", myDate );
				
				$("#" + $(this).attr("data-from")).removeClass (function (index, css) {
				    return (css.match (/(^|\s)max\S+/g) || []).join(' ');
				});
				
				$("#" + $(this).attr("data-from")).addClass("max" + dateText.replace(/\//g,""));
			}
		 }
	});	
	
	if (othervalue != null && $(instance).attr("data-to"))
	{
		//this means the min value
		$(instance).datepicker( "option", "maxDate", othervalue );
		
		$(instance).removeClass (function (index, css) {
		    return (css.match (/(^|\s)max\S+/g) || []).join(' ');
		});
		
		$(instance).addClass("max" + othervalue.replace(/\//g,""));						
	} else if (othervalue != null && $(instance).attr("data-from"))
	{
		//this mean the max value
		$(instance).datepicker( "option", "minDate", othervalue );
		
		$(instance).removeClass (function (index, css) {
		    return (css.match (/(^|\s)min\S+/g) || []).join(' ');
		});
		
		$(instance).addClass("min" + othervalue.replace(/\//g,""));
	}
	
}

function getMinMaxPropertiesRow(label, min, max, valuemin, valuemax)
{
	var row = new PropertyRow();
	row.Type("first");
	row.Label(label);
	row.LabelTitle(getPropertyLabel(label));	
	
	if ($(_elementProperties.selectedelement).hasClass("dateitem"))
	{
		row.ContentType("minmaxdate");
	} else if ($(_elementProperties.selectedelement).hasClass("timeitem")) {
		row.ContentType("minmaxtime");
	} else if ($(_elementProperties.selectedelement).hasClass("numberitem")) {
		row.ContentType("minmaxnumber");
	} else {
		row.ContentType("minmax");
	}
	
	var mininputid = getNewId();
	var maxinputid = getNewId();
	
	if (!$(_elementProperties.selectedelement).hasClass("numberitem"))
	{
		if (valuemin == "0") valuemin = "";
		if (valuemax == "0") valuemax = "";
	}
	
	var item = new ContentItem();
	item.Id(mininputid);
	item.Value(valuemin);
	row.ContentItems.push(item);
	
	item = new ContentItem();
	item.Id(maxinputid);
	item.Value(valuemax);
	row.ContentItems.push(item);
	
	_elementProperties.propertyRows.push(row);
	
	var input = $("#" + mininputid);
	if ($(_elementProperties.selectedelement).hasClass("dateitem"))
	{
		createDatePickerForEditor(input, valuemax);	
	} else if ($(_elementProperties.selectedelement).hasClass("timeitem")) {
		//createTimePickerForEditor(input, valuemax);
		$(input).attr("placeholder", "HH:mm:ss");
	} else {	
		$(input).spinner({ decimals:2, min:min, max:max, start:"", allowNull: true });
		$(input).parent().find('.ui-spinner-button').click(function() {
			update(input);
		});
	}
	$(input).blur(function() {
		  update(this)
	});		
	
	var input2 = $("#" + maxinputid);
	
	if ($(_elementProperties.selectedelement).hasClass("dateitem"))
	{
		createDatePickerForEditor(input2, valuemin);
	} else if ($(_elementProperties.selectedelement).hasClass("timeitem")) {
		//createTimePickerForEditor(input2, valuemin);
		$(input2).attr("placeholder", "HH:mm:ss");
	} else {	
		$(input2).spinner({ decimals:2, min:min, max:max, start:"", allowNull: true });
		$(input2).parent().find('.ui-spinner-button').click(function() {
			  update(input2);
		});
	}
	$(input2).blur(function() {
		  update(this)
	});
}

function getActionRow(label, l1, action, l2, action2, editenabled)
{
	var row = new PropertyRow();
	row.Type("first");
	row.ContentType("action");
	row.Label(label);
	row.LabelTitle(getPropertyLabel(label));
	
	var item = new ContentItem();
	item.Label = l1;
	item.Id("btnAdd" + label);
	item.Value(action);
	row.ContentItems.push(item);
	
	item = new ContentItem();
	item.Label = l2;
	item.Id("btnRemove" + label);
	item.Value(action2);
	row.ContentItems.push(item);
	
	if (!$(_elementProperties.selectedelement).hasClass("complextableitem"))
	{
		if (label == "Columns" || label == "Rows" || label == "PossibleAnswers" || label == "Questions" || label == "RankingItem")
		{
			if (editenabled == null){
				editenabled = true
			}
			row.Edit(editenabled);
			row.EditShortnames(editenabled);
		}
	}
	
	_elementProperties.propertyRows.push(row);
	
	if (label == "Columns" || label == "Rows" || label == "PossibleAnswers" || label == "Questions" || label == "RankingItem")
	{
		var id = "id" + idcounter++;
		
		var row = new PropertyRow();
		row.Label(label);
		row.Type("TinyMCE");
		row.TinyMCEId(id);
		_elementProperties.propertyRows.push(row);
		tinyMCE.settings = myConfigSetting2Editor;
		tinymce.EditorManager.execCommand('mceToggleEditor', true, id);
	
		row = new PropertyRow();
		row.Type("PossibleAnswerShortnames");
		var rowcontent = "<div class='editvaluesbuttons'><button id='idBtnSaveShortName' class='btn btn-default btn-primary btn-sm' onclick='save(this)'>" + getPropertyLabel("Apply") + "</button> <button id='idBtnCancelShortName' class='btn btn-default btn-sm' onclick='cancel(this);event.stopPropagation()'>" + getPropertyLabel("Cancel") + "</button></div>"
		row.Content(rowcontent);
		_elementProperties.propertyRows.push(row);
	}

	var input = $("#btnAdd" + label);
	$(input).click(function() {
		update(this)
	});

	//The Remove Button has an Tooltip Wrapper which also needs the click event
	input = $("#btnRemove" + label + ", .tooltip-wrapper:has(#btnRemove" + label +")");
	$(input).click(function() {
		update(this)
	});
}

function getUploadRow(label)
{
	var row = new PropertyRow();
	row.Type("first");
	row.Label(label);
	row.LabelTitle(label == "Image" ? getPropertyLabel("File") : getPropertyLabel(label));
	
	if ($(_elementProperties.selectedelement).hasClass("confirmationitem"))
	{
		row.LabelTitle(row.LabelTitle() + getPropertyLabel("ConfirmationFileInfo"));
	}
	
	if ($(_elementProperties.selectedelement).hasClass("downloaditem") || $(_elementProperties.selectedelement).hasClass("confirmationitem"))
	{
		var files = $(_elementProperties.selectedelement).find(".files").first();
		
		if (files != null)
		{
			var table = document.createElement("table");
			$(table).addClass("filestable")
			$(files).find("a").each(function(){
				
				var uid = $(this).prevAll().first().val();
				
				var trf = document.createElement("tr");
				var tdf = document.createElement("td");
				$(tdf).append($(this).html());
				$(trf).append(tdf);
				
				tdf = document.createElement("td");
				var a = document.createElement("a");
				
				var id = $(_elementProperties.selectedelement).attr("id");
				$(a).attr("onclick", "deleteDownloadFile('" + uid + "', this,'" + id + "', false);"); 
				
				$(a).append("<span class='glyphicon glyphicon-remove'></span>");
				$(tdf).append(a);
				$(trf).append(tdf);
				
				$(table).append(trf);
			});
			
			row.Content(table.outerHTML);
		}
	} else if ($(_elementProperties.selectedelement).hasClass("galleryitem"))
	{
		var table = document.createElement("table");
		$(table).addClass("filestable")
		$(_elementProperties.selectedelement).find(".gallery-table").find("td").each(function(){			
			if ($(this).find("input[name^='image']").length > 0)
			{			
				var f = [];
				f.width = parseInt($(this).find(".gallery-image").first().attr("data-width"));
				f.name = $(this).find("input[name^='name']").first().val();
				f.uid = $(this).find("input[name^='image']").first().val();
				f.longdesc = $(this).find("input[name^='longdesc']").first().val();
				f.comment = $(this).find("textarea[name^='comment']").first().text();
				f.cleanComment = f.comment;
				
				var trf = document.createElement("tr");
				var tdf = document.createElement("td");
				$(tdf).append(f.name);
				$(trf).attr("data-uid", f.uid).append(tdf);
				
				tdf = document.createElement("td");
				
				var a = document.createElement("a");
				$(a).attr("onclick", "moveGalleryFile('" + f.uid + "', this, true, false);");				
				$(a).append("<span class='glyphicon glyphicon-arrow-up'></span>");
				$(tdf).append(a);
				
				a = document.createElement("a");
				$(a).attr("onclick", "moveGalleryFile('" + f.uid + "', this, false, false);");				
				$(a).append("<span class='glyphicon glyphicon-arrow-down'></span>");
				$(tdf).append(a);
				
				a = document.createElement("a");
				$(a).attr("onclick", "deleteGalleryFile('" + f.uid + "', this, false);");				
				$(a).append("<span class='glyphicon glyphicon-remove'></span>");				
				$(tdf).append(a);
				
				$(trf).append(tdf);
				
				$(table).append(trf);
			}
		});
		row.Content(table.outerHTML);
	} else if ($(_elementProperties.selectedelement).hasClass("imageitem")) {
		var table = document.createElement("table");
		$(table).addClass("filestable")
		
		var trf = document.createElement("tr");
		var tdf = document.createElement("td");
		var filename = $(_elementProperties.selectedelement).find("input[name^='filename']").first().val();
		
		$(tdf).append(filename);
		
		if (filename.length > 0)
		{			
			var a = document.createElement("a");
			var url = $(_elementProperties.selectedelement).find("input[name^='url']").first().val();
			$(a).attr("onclick","deleteImageFile('" + url + "', this);");
		
			$(a).append("<span class='glyphicon glyphicon-remove'></span>");
			$(tdf).append("&nbsp;").append(a);
		}
		
		$(trf).append(tdf);
		$(table).append(trf);
	
		row.Content(table.outerHTML);
	}
	
	var id = getNewId();
	
	row.Content(row.Content() + "<div id='" + id +"'>UploadFile</div>")
	_elementProperties.propertyRows.push(row);
	
	var button = $("#" + id)[0];
		
	if (label == "Image")
	{
		createImageUploader(button);
	} else {
		createFileUploader(button);
		showHideGalleryButtons();
	}
}

function moveGalleryFile(uid, button, up, undo)
{
	var id = $(_elementProperties.selectedelement).attr("data-id");
	var element = _elements[id]
	var row = $(button).closest("tr");
	
	var index;
	for (var i = 0; i < element.files().length; i++)
	{
		var item = element.files()[i];
		if (item.uid() == uid)
		{
			if (up && i == 0) return false;
			if ((!up) && i == element.files().length - 1) return false;
			
			if (up)
			{
				var temp = element.files()[i];
				element.files()[i] = element.files()[i-1];
				element.files()[i-1] = temp;
				row.insertBefore(row.prev());				
			} else {
				var temp = element.files()[i+1];
				element.files()[i+1] = element.files()[i];
				element.files()[i] = temp;
				row.insertAfter(row.next());
			}
			
			element.files.valueHasMutated();
			
			updateGallery(null);	
			if (!undo) _undoProcessor.addUndoStep(["FileMoved", $(_elementProperties.selectedelement).attr("id"), uid, up]);
			
			break;
		}
	}
	
	showHideGalleryButtons();
}

function deleteGalleryFile(uid, button, noundo)
{
	var request = $.ajax({
	  url: contextpath + "/noform/management/deleteFile",
	  data: {uid : uid, suid : surveyUniqueId},
	  cache: false,
	  dataType: "json",
	  success: function(data)
	  {
		  if (!data.success)
		  {
			showError("the file could not be deleted from the server");			 
		  }				
	  }
	});
	
	$(button).closest("tr").remove();
	
	var id = $(_elementProperties.selectedelement).attr("data-id");
	var element = _elements[id];
	element.files.remove( function (item) { return item.uid() == uid; } ) 	
	updateGallery(null);	
	
	if (!noundo) _actions.SaveEnabled(true);
//	_undoProcessor.addUndoStep(["FileDeleted", $(_elementProperties.selectedelement).attr("id"), uid]);
}

function deleteImageFile(url, button)
{
	var uid = url.substring(url.lastIndexOf("/")+1);
	
	var request = $.ajax({
		  url: contextpath + "/noform/management/deleteFile",
		  data: {uid : uid, suid : surveyUniqueId},
		  cache: false,
		  dataType: "json",
		  success: function(data)
		  {
			  if (!data.success)
			  {
				  showError("the file could not be deleted from the server");
			  }				
		  }
		});

	var id = $(_elementProperties.selectedelement).attr("data-id");
	var element = _elements[id];
	element.url(contextpath + "/resources/images/photo_scenery.png");
	element.filename("");
	element.width("128");
	element.scale(100);
	
	if (button != null)
	{
		$(button).closest("td").empty();
		_actions.SaveEnabled(true);
//		_undoProcessor.addUndoStep(["FileDeleted", $(_elementProperties.selectedelement).attr("id"), uid]);
	}
}

function deleteDownloadFile(uid, button, eid, noundo)
{
	var request = $.ajax({
	  url: contextpath + "/noform/management/deleteDownloadFile",
	  data: {uid : uid, eid: eid, suid : surveyUniqueId},
	  cache: false,
	  async: false,
	  dataType: "json",
	  success: function(data)
	  {
		  if (data.success)
		  {
			  if (button != null)
			  $(button).closest("tr").remove();
			  
			  var id = $(_elementProperties.selectedelement).attr("data-id");
			  var element = _elements[id];
			  element.files.remove( function (item) { return item.uid() == uid; } ) 
			  
			  if (!noundo)
			  _actions.SaveEnabled(true);
//			  _undoProcessor.addUndoStep(["FileDeleted", $(_elementProperties.selectedelement).attr("id"), uid]);
		  }				
	  }
	});
}

function createImageUploader(instance)
{
	var row = $(instance).closest("tr");
	var uploader = new qq.FileUploader({
	    element: instance,
	    action: contextpath + "/" + surveyShortname + '/management/uploadimage',
	    uploadButtonText: getPropertyLabel("UploadFile"),
	    params: {
	    	'_csrf': csrftoken
	    },
	    cache: false,
	    multiple: false,
	    sizeLimit: 1048576,
	    onComplete: function(id, fileName, responseJSON)
		{
	    	$(row).find(".validationinfobutton").remove();	
	    	$(row).removeClass("invalidinput");
	    	
	    	$(instance).parent().find(".validation-error").remove();
	    	
	    	if (responseJSON.success)
	    	{
		    	$(instance).attr("data-id", responseJSON.id);
		    	
		    	var td = $(instance).closest("td").find(".filestable").find("td").first();
		    	
		    	$(td).html(fileName);
		    	
		    	var a = document.createElement("a");
		    	$(a).append("<span class='glyphicon glyphicon-remove'></span>");
				$(td).append("&nbsp;").append(a);		    	
		    	
		    	$("#image-dialog-image").attr("src",contextpath + "/files/" + responseJSON.id);
		    	
		    	var width = responseJSON.width;
		    	
		    	var id = $(_elementProperties.selectedelement).attr("data-id");
		    	var element = _elements[id];
		    	element.width(width);
		    	element.scale(100);
		    	element.url(contextpath + "/files/" + surveyUniqueId +  "/" + responseJSON.id);
		    	element.filename(fileName);
		    	//element.uid(responseJSON.id);
		    	
		    	var url = $(_elementProperties.selectedelement).find("input[name^='url']").first().val();
				$(a).attr("onclick","deleteImageFile('" + url + "', this);");
					    	
		    	$("#image-dialog-width").val("100");
		    	
		    	_undoProcessor.addUndoStep(["ImageUpload", $(_elementProperties.selectedelement).attr("id"), responseJSON.id]);
				
			} else {
				addValidationMessage(row, invalidFileError);
	    	}
		},
		showMessage: function(message){
			message = message.replace("1.0MB", "1 MB");
			addValidationMessage(row, message);
		},
		onUpload: function(id, fileName, xhr){
			$(row).find(".validationinfobutton").remove();	
	    	$(row).removeClass("invalidinput");
		},
	    
	});
	
	$(".qq-upload-button").addClass("btn btn-default btn-xs").removeClass("qq-upload-button");
	$(".qq-upload-list").hide();
	$(".qq-upload-drop-area").css("margin-left", "-1000px");
}

function createFileUploader(instance)
{	
	var path = contextpath + "/" + surveyShortname + '/management/upload';
	var row = $(instance).closest("tr");
	
	if ($(_elementProperties.selectedelement).hasClass("galleryitem"))
	{
		path = path + "image";
	}	
	
	var uploader = new qq.FileUploader({
		element: instance,
		action: path,
	    uploadButtonText: getPropertyLabel("UploadFiles"),
	    params: {
	    	'_csrf': csrftoken
	    },
	    multiple: true,
	    cache: false,
	    sizeLimit: 1048576,
	    onComplete: function(id, fileName, responseJSON)
		{
	    	$(row).find(".validationinfobutton").remove();	
	    	$(row).removeClass("invalidinput");
	    	
	    	$(instance).parent().find(".validation-error").remove();
	    	
	    	if (responseJSON.success)
	    	{
	    		_elementProperties.selectedproperty = $(instance).closest("tr");
		    	var table = document.createElement("table");
		    	if ($(instance).closest("td").find(".filestable").length > 0)
		    	{
		    		table = $(instance).closest("td").find(".filestable").first();
		    	}
		    	
		    	if ($(_elementProperties.selectedelement).hasClass("galleryitem"))
		    	{
		    		if (!checkGalleryUploadedFile(responseJSON.name, table))
		    		{
		    			return;
		    		}	    		
		    	}
		    	
		    	//add to files in properties
		    	var tr = document.createElement("tr");
		    	var td = document.createElement("td");
		    	$(td).append(responseJSON.name);
		    	$(tr).attr("data-uid", responseJSON.id).append(td);
		    	td = document.createElement("td");
		    	var a = document.createElement("a");
		    	
		    	if ($(_elementProperties.selectedelement).hasClass("galleryitem"))
		    	{
			    	$(a).attr("onclick", "moveGalleryFile('" + responseJSON.id + "', this, true, false);");				
					$(a).append("<span class='glyphicon glyphicon-arrow-up'></span>");
					$(td).append(a);
					
					a = document.createElement("a");
					$(a).attr("onclick", "moveGalleryFile('" + responseJSON.id + "', this, false, false);");				
					$(a).append("<span class='glyphicon glyphicon-arrow-down'></span>");
					$(td).append(a);
					
					a = document.createElement("a");
		    	}
		    	
				$(a).click(function(){
					if ($(_elementProperties.selectedelement).hasClass("galleryitem"))
			    	{
						deleteGalleryFile(responseJSON.id, this, false);
			    	} else {
			    		deleteDownloadFile(responseJSON.id, this, $(_elementProperties.selectedelement).attr("id"), false);
			    	}
				});			
				$(a).append("<span class='glyphicon glyphicon-remove'></span>");
				$(td).append(a);
				$(tr).append(td);
				$(table).append(tr);	   
				
				if ($(_elementProperties.selectedelement).hasClass("galleryitem"))
		    	{
					showHideGalleryButtons();
		    	}
				
				//add to element
				if ($(_elementProperties.selectedelement).hasClass("galleryitem"))
		    	{
					updateGallery(responseJSON);		    	
		    	} else if ($(_elementProperties.selectedelement).hasClass("downloaditem")) {
		    		var id = $(_elementProperties.selectedelement).attr("data-id");
		    		var element = _elements[id];
		    		var file = newFileViewModel(responseJSON.id, responseJSON.name);
		    		element.files.push(file);
		    	} else if ($(_elementProperties.selectedelement).hasClass("confirmationitem")) {
		    		var id = $(_elementProperties.selectedelement).attr("data-id");
		    		var element = _elements[id];
		    		var file = newFileViewModel(responseJSON.id, responseJSON.name);
		    		element.files.push(file);
		    		element.useupload(true);
		    	}
				
				deactivateLinks();
				
				_undoProcessor.addUndoStep(["FileUpload", $(_elementProperties.selectedelement).attr("id"), responseJSON.id]);
	    	} else {
	    		addValidationMessage(row, invalidFileError);
	    	}			
		},
		showMessage: function(message){
			message = message.replace("1.0MB", "1 MB");
			addValidationMessage(row, message);
		},
		onUpload: function(id, fileName, xhr){
			$(row).find(".validationinfobutton").remove();	
	    	$(row).removeClass("invalidinput");
		},
	});
	$(".qq-upload-button").addClass("btn btn-default btn-xs").removeClass("qq-upload-button");
	$(".qq-upload-list").hide();
	$(".qq-upload-drop-area").css("margin-left", "-1000px");
}

function showHideGalleryButtons()
{
	$(".filestable").each(function(){
		$(this).find(".glyphicon-arrow-up").removeClass("disabled");
		$(this).find(".glyphicon-arrow-down").removeClass("disabled");
		
		$(this).find(".glyphicon-arrow-up").first().addClass("disabled");
		$(this).find(".glyphicon-arrow-down").last().addClass("disabled");
	});
}

function addPossibleAnswer(noundo)
{
	var text;
	var selectedElement = $(_elementProperties.selectedelement);
	var id = selectedElement.attr("data-id");
	var element = getElement();

	const possAns = element.possibleAnswers();

	let answerNum = 0

	possAns.forEach(ans => {
		if (/Answer \d+/.test(ans.title())){
			let num = parseInt(ans.title().split(" ")[1]) + 1
			if (num > answerNum){
				answerNum = num
			}
		}
	})

	if (answerNum === 0){
		answerNum = possAns.length + 1
	}

	text = "Answer " + answerNum

	var newanswer = newPossibleAnswerViewModel(getNewId(), getNewId(), getNewShortname(), "", text, false);
	element.possibleAnswers.push(newanswer);	
	
	if (isQuiz && quizanswersrow != null)
	{
		quizanswersrow.ContentItems.push(newanswer);
		initQuizElements(element);
	}
	if (!noundo) {
		_undoProcessor.addUndoStep(["ADDANSWER", element.id(), newanswer]);
	}
	
	updateDependenciesView();
	addElementHandler(selectedElement);
	
	updateNavigation(selectedElement, id);
}

function removePossibleAnswer()
{
	var selectedElement = $(_elementProperties.selectedelement);
	var id = selectedElement.attr("data-id");
	var element = getElement();
	
	_elementProperties.selectedproperty = $("#btnRemovePossibleAnswers").closest("tr");
	removeValidationMarkup();
	
	var numPossibleAnswers = element.possibleAnswers().length;
	if (element.minChoices() != null && element.minChoices() >= numPossibleAnswers)
	{
		addValidationInfo($("#btnRemovePossibleAnswers"), "checkNumberOfChoices");
		return;
	}
		
	var answer = element.possibleAnswers.pop();
	
	if (isQuiz)
	{
		quizanswersrow.ContentItems.remove(answer);
		initQuizElements(element);
	}
	
	_undoProcessor.addUndoStep(["REMOVEANSWER", element.id(), answer]);
	
	$('[data-triggers]').each(function(){
		if ($(this).attr("data-triggers").indexOf(answer.id() + ";") > -1) {
			$(this).attr("data-triggers", $(this).attr("data-triggers").replace(answer.id() + ";", ""));
		}
	});
	
	updateDependenciesView();
	addElementHandler(selectedElement);
	
	updateNavigation(selectedElement, id);
}

function removeSpecificPossibleAnswer(element, domElement, paid, noundo)
{

	let answerPos = element.possibleAnswers().findIndex(pa => paid == pa.id())
	let answer = element.possibleAnswers.splice(answerPos, 1)[0]

	if (isQuiz)
	{
		quizanswersrow.ContentItems.remove(answer);
		initQuizElements(element);
	}

	if (!noundo)
		_undoProcessor.addUndoStep(["REMOVESPECIFICANSWER", element.id(), answer, answerPos]);

	$('[data-triggers]').each(function(){
		if ($(this).attr("data-triggers").indexOf(answer.id() + ";") > -1) {
			$(this).attr("data-triggers", $(this).attr("data-triggers").replace(answer.id() + ";", ""));
		}
	});

	updateDependenciesView();
	addElementHandler(domElement);

	updateNavigation(domElement, domElement.attr("data-id"));
}

function addPossibleAnswerChildren(){
	const headerCell = getElement();

	if (!headerCell.possibleAnswersMatch()){
		return
	}

	const parentId = $(_elementProperties.selectedelement).closest("li.complextableitem").attr("data-id");
	const table = _elements[parentId];

	let text = "Answer " + (headerCell.possibleAnswersChildren().length + 1);

	function nextAnswer(){
		return newPossibleAnswerViewModel(getNewId(), getNewId(), getNewShortname(), "", text, false);
	}

	headerCell.possibleAnswersChildren.push(nextAnswer());

	$('.highlightedquestion').each(function() {
		let id = $(this).attr("data-id");
		let child = table.getChildbyId(id);

		if (child != null) {
			child.possibleAnswers.push(nextAnswer())
		}
	});
}

function removePossibleAnswerChildren(){
	const headerCell = getElement();

	if (!headerCell.possibleAnswersMatch() || headerCell.possibleAnswersChildren().length <= 0){
		return
	}

	const parentId = $(_elementProperties.selectedelement).closest("li.complextableitem").attr("data-id");
	const table = _elements[parentId];

	headerCell.possibleAnswersChildren.pop();

	$('.highlightedquestion').each(function() {
		let id = $(this).attr("data-id");
		let child = table.getChildbyId(id);

		if (child != null) {
			child.possibleAnswers.pop()
		}
	});
}

function addRankingEntry()
{
	var id = $(_elementProperties.selectedelement).attr("data-id");
	var element = _elements[id];
	var text = "Ranking Item " + (element.rankingItems().length + 1);

	var newrankingitem = newRankingItemViewModel(getNewId(), getNewId(), getNewShortname(), text);
	element.rankingItems.push(newrankingitem);

	if (element.rankingItems().length == element.minItems()+1) {
		var removeButton = $("#btnRemoveRankingItems");
		removeButton.tooltip("enable");
	}

	_undoProcessor.addUndoStep(["ADDRANKINGITEM", element.id(), newrankingitem]);
	updateDependenciesView();
	addElementHandler($(_elementProperties.selectedelement));

	updateNavigation($(_elementProperties.selectedelement), id);
}

function removeRankingEntry()
{
	var id = $(_elementProperties.selectedelement).attr("data-id");
	var element = _elements[id];
	
	removeValidationMarkup();
	var oldrankingitem = element.rankingItems.pop();

	if (element.rankingItems().length <= element.minItems()) {
		var removeButton = $("#btnRemoveRankingItems");
		removeButton.tooltip("hide");
		removeButton.tooltip("disable");
	}

	_undoProcessor.addUndoStep(["REMOVERANKINGITEM", element.id(), oldrankingitem]);
	updateDependenciesView();
	addElementHandler($(_elementProperties.selectedelement));

	updateNavigation($(_elementProperties.selectedelement), id);
}

function removeSpecificRankingEntry(element, domElement, rid, noundo)
{

	let itemPos = element.rankingItems().findIndex(r => rid == r.id())
	let item = element.rankingItems.splice(itemPos, 1)[0]

	if (!noundo)
		_undoProcessor.addUndoStep(["REMOVESPECIFICRANKINGITEM", element.id(), item, itemPos]);
	updateDependenciesView();
	addElementHandler($(_elementProperties.selectedelement));

	updateNavigation($(_elementProperties.selectedelement), domElement.attr("data-id"));
}

function addColumn(noundo)
{
	var text;
	var id = $(_elementProperties.selectedelement).attr("data-id");
	var element = _elements[id];
	
	if (element.type == "Matrix") {
		text = "Answer " + (element.answers().length + 1);
	} else if (element.type == "ComplexTable") {
		text = getBigLetter(element.answers().length);
	} else {
		text = getBigLetter(element.answers().length + 1);
	}
	
	if (element.type == "ComplexTable")
	{
		element.columns(element.columns() + 1);
		var newElement = getBasicElement("ComplexTableItem", true, text, null, false);
		newElement.row = 0;
		newElement.column = element.columns();
		newElement.cellType = 1
		newelement = newComplexTableItemViewModel(newElement);
		element.childElements.push(newelement);
	} else {	
		var newelement = newMatrixItemViewModel(getNewId(), getNewId(), true, getNewShortname(), false, text, text, false, "", element.answers().length);
		element.answers.push(newelement);
		
		if (element.type == "Matrix")
		{
			var oldanswerscount = element.answers().length - 1;
			element.columns(element.columns() + 1);
			//update dependencies
			for (var i = oldanswerscount; i < element.dependentElementsStrings().length; i+=oldanswerscount+1)
			{
				element.dependentElementsStrings.splice(i,0,ko.observable(""));
			}  
			
			if(element.isInterdependent())
			{
				checkInterdependentMatrix($("#idPropertyInterdependency").closest(".firstpropertyrow"));
			}
		}
	}
	
	if (!noundo)
	_undoProcessor.addUndoStep(["ADDCOLUMN", element.id(), newelement]);
	addElementHandler($(_elementProperties.selectedelement));
	
	updateNavigation($(_elementProperties.selectedelement), id);
}

function removeColumn(noundo)
{
	var id = $(_elementProperties.selectedelement).attr("data-id");
	var element = _elements[id];

	if(element.answers().length <= 1) {
		return;
	}
	
	var col;
	
	if (element.type == "ComplexTable")
	{
		col = []
		for (let i = 0; i <= element.rows(); i++) {
			col.push(element.removeChildAt(element.columns(), i));
		}
		element.columns(element.columns() - 1);
	} else {
		col = element.answers.pop();
		
		if (element.type == "Matrix")
		{
			element.columns(element.columns()-1);
			//update dependencies
			for (var i = element.questionsOrdered().length * (element.answers().length + 1)  - 1; i >= 0; i-=(element.answers().length+1))
			{
				element.dependentElementsStrings.splice(i,1);
			} 
			
			if(element.isInterdependent())
			{
				checkInterdependentMatrix($("#idPropertyInterdependency").closest(".firstpropertyrow"));
			}
		}
	}
	
	if (!noundo)
	_undoProcessor.addUndoStep(["REMOVECOLUMN", element.id(), col]);
	
	updateNavigation($(_elementProperties.selectedelement), id);
}

function addRow(noundo)
{
	var id = $(_elementProperties.selectedelement).attr("data-id");
	var element = _elements[id];
	var allmandatory = true;
	var text;
	
	if (element.type == "Matrix")
	{
		text = "Question " + (element.questionsOrdered().length + 1);	
		for (var i = 0; i < element.questionsOrdered().length; i++)
		{
			if (element.questionsOrdered()[i].optional())
			{
				allmandatory = false;
				break;
			}
		}
		
		if(element.isInterdependent())
		{
			checkInterdependentMatrix($("#idPropertyInterdependency").closest(".firstpropertyrow"));
		}
	} else if (element.type == "RatingQuestion")
	{
		text = "Question " + (element.childElements().length + 1);
		for (var i = 0; i < element.childElements().length; i++)
		{
			if (element.childElements()[i].optional())
			{
				allmandatory = false;
				break;
			}
		}
	} else {
		text = (element.questions().length + 1) + "";
		for (var i = 0; i < element.questions().length; i++)
		{
			if (element.questions()[i].optional())
			{
				allmandatory = false;
				break;
			}
		}
	}	
	
	var newelement;
	if (element.type == "RatingQuestion")
	{
		newelement = newBasicViewModel(getBasicElement("Text", false, text, null, false));
		element.childElements.push(newelement);
		
		$(_elementProperties.selectedelement).find("a.ratingitem").removeAttr("onclick");
	} else if (element.type == "ComplexTable")
	{
		element.rows(element.rows() + 1);
		var newElement = getBasicElement("ComplexTableItem", true, text, null, false);
		newElement.row = element.rows();
		newElement.column = 0;
		newElement.cellType = 1
		newelement = newComplexTableItemViewModel(newElement);
		element.childElements.push(newelement);
	} else {
		newelement = newMatrixItemViewModel(getNewId(), getNewId(), !allmandatory, getNewShortname(), false, text, text, false, "", element.questions().length);
		element.questions.push(newelement);
	}
	
	if (!noundo)
	_undoProcessor.addUndoStep(["ADDROW", element.id(), newelement]);
	
	addElementHandler($(_elementProperties.selectedelement));
	updateNavigation($(_elementProperties.selectedelement), id);
}

function removeRow(noundo)
{
	var id = $(_elementProperties.selectedelement).attr("data-id");
	var element = _elements[id];
	var row;
	if (element.type == "Matrix")
	{	
		if (element.questions().length > 1)
		{
			removeValidationMarkup($("#btnRemoveRows").closest("tr"));
			
			var numRows = element.questions().length;
			if (element.minRows() != null && element.minRows() >= numRows)
			{
				addValidationInfo($("#btnRemoveRows"), "checkNumberOfRows");
				return;
			}
			
			row = element.questions.pop();
			
			//update dependencies
			for (var i = (element.questionsOrdered().length+1) * element.answers().length - 1; i > (element.questionsOrdered().length) * element.answers().length - 1; i--)
			{
				element.dependentElementsStrings.splice(i,1);
			}  
			
			if(element.isInterdependent())
			{
				checkInterdependentMatrix($("#idPropertyInterdependency").closest(".firstpropertyrow"));
			}
		}
	} else if (element.type == "RatingQuestion")
	{
		if (element.childElements().length > 1)
		{
			row = element.childElements.pop();
		}
	} else if (element.type == "ComplexTable")
	{

		row = []
		for (let i = 0; i <= element.columns(); i++) {
			row.push(element.removeChildAt(i, element.rows()));
		}
			
		element.rows(element.rows() - 1);
	} else {
		if (element.questions().length > 1)
		{
			row = element.questions.pop();
		}
	}
	if (row != null && !noundo)
	_undoProcessor.addUndoStep(["REMOVEROW", element.id(), row]);
	updateNavigation($(_elementProperties.selectedelement), id);
}

function removeSpecificRating(element, domElement, qid, noundo){

	let rowPos = element.childElements().findIndex(q => qid == q.id())
	let row = element.childElements.splice(rowPos, 1)[0]

	if (!noundo)
		_undoProcessor.addUndoStep(["REMOVESPECIFICRATING", element.id(), row, rowPos]);
	updateNavigation(domElement, domElement.attr("data-id"));
}

function cancel(button)
{
	_elementProperties.selectedproperty = $(button).closest(".propertyrow").prevAll(".firstpropertyrow").first();
	
	var label = $(_elementProperties.selectedproperty).find(".propertylabel").first().attr("data-label");
	if (label == "Visibility")
	{
		
	} else if (label == "PossibleAnswers")
	{
		$(_elementProperties.selectedproperty).removeClass("invalidinput");
	} else {
		if ($(button).closest("tr").find("textarea").length > 0)
		{
			var selectedid = $(button).closest("tr").find("textarea").first().attr("id");
			tinyMCE.get(selectedid).setContent(originaltext, {format : 'xhtml'});
		}
	}

	$(button).closest(".propertyrow").removeClass("invalidinput").hide();
	$(_elementProperties.selectedproperty).removeClass("invalidinput").find(".validationinfobutton").remove();
	//update($("tr[data-label='Columns']"));
}


function showHideVisibilityElements(span)
{
	var sectionheader = $(span).parent();
	var visible = $(span).hasClass("glyphicon-chevron-right");

	if (visible)
	{
		$(sectionheader).nextUntil(".visibilitysection").show();
	} else {
		$(sectionheader).nextUntil(".visibilitysection").hide();
	}
	
	if (!visible)
	{
		$(span).removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-right");
	} else {
		$(span).removeClass("glyphicon-chevron-right").addClass("glyphicon-chevron-down");
	}
}

function resetVisibility(button, noUndo)
{
	_elementProperties.selectedproperty = $(button).closest("tr");
	updateVisibility(button, true, false, false, noUndo);
}

function resetFeedback(button)
{
	_elementProperties.selectedproperty = $(button).closest("tr");
	updateFeedback(button, true);
}

function resetHelp(button)
{
	var id = $(_elementProperties.selectedelement).attr("data-id");
	var element = getElement();
	
	var oldtext = element.help();
	element.help("");
	element.niceHelp(getNiceHelp(element.help()));
	_undoProcessor.addUndoStep(["Help", id, $(_elementProperties.selectedelement).index(), oldtext, ""]);
	
	$(button).closest("tr").find(".propertytextpreview").first().empty();
	
}

function resetConfirmationText(button)
{
	var id = $(_elementProperties.selectedelement).attr("data-id");
	var element = _elements[id];
	
	var oldtext = element.confirmationtext();
	updateConfirmationText(element, "");
	_undoProcessor.addUndoStep(["ConfirmationText", id, $(_elementProperties.selectedelement).index(), oldtext, ""]);
	
	$(button).closest("tr").find(".propertytextpreview").first().empty();
}

function removeInvisibleTags(node) {
	var copy = $(node).clone();
	$(copy).find(".screen-reader-only").remove();
	return copy;
}

var originaltext;
function edit(span)
{
	if ($(span).hasClass("propertytextpreview") || $(span).hasClass("PreviewItems"))
	{
		span = $(span).closest("td").find(".glyphicon").first();
	}
	
	_elementProperties.selectedproperty = $(span).closest("tr");
	
	var label = $(_elementProperties.selectedproperty).find(".propertylabel").first().attr("data-label");
	if (label == "Visibility")
	{
		var oldvalues = "";
		var triggers = [];
		$(span).closest("td").find(".triggers").find("a").each(function(){
			triggers[triggers.length] = $(this).attr("data-targetid");
		});
		
		var div = document.createElement("div");
		$(div).addClass("visibilitylist");
		
		var id = $("#content").find(".selectedquestion").first().attr("id");
		var useAndLogic = $("#content").find(".selectedquestion").first().find("input[name^='useAndLogic']").val() == 'true';
		
		if ($("#content").find(".selectedquestion").first().hasClass("matrix-header") || $("#content").find(".selectedquestion").first().hasClass("table-header"))
		{
			id = $("#content").find(".selectedquestion").first().closest(".survey-element").attr("id");
		}
		
		$("#content").find("li").each(function(){
			var currentid = $(this).attr("id");
			if (currentid == id) return false;
			
			if ($(this).hasClass("sectionitem"))
			{
				$(div).append("<div class='visibilitysection'><span class='glyphicon glyphicon-chevron-down' onclick='showHideVisibilityElements(this)'></span> " + strip_tags(adaptNumbering($(this).find(".sectiontitle"))) + "</div>");
			} else if ($(this).hasClass("singlechoiceitem") || $(this).hasClass("multiplechoiceitem"))
			{
				$(div).append("<div class='visibilityquestion'>" + strip_tags(adaptNumbering(removeInvisibleTags($(this).find(".questiontitle").first()))) + "</div>");
				
				$(this).find("textarea[name^='answer']").each(function(){
					var answerid = $(this).attr("data-id");
					var answershortname = $(this).parent().prevAll("input[name^='pashortname']").first().val();
					if (triggers.indexOf(answerid) > -1)
					{
						$(div).append("<input type='checkbox' checked='checked' style='margin-left: 10px' value='" + answerid + "' /> <span>"  + strip_tags($(this).text()) + " (" + answershortname + ")" + "</span><br />");
						oldvalues = oldvalues + answerid + ";";
					} else {
						$(div).append("<input type='checkbox' style='margin-left: 10px' value='" + answerid + "' /> <span>"  + strip_tags($(this).text()) + " (" + answershortname + ")" + "</span><br />");
					}
				});
			} else if ($(this).hasClass("matrixitem"))
			{
				if ($(_elementProperties.selectedelement).closest(".matrixitem").length > 0 && $(_elementProperties.selectedelement).closest(".matrixitem").attr("id") == $(this).attr("id"))
				{
					//I am a matrix question
				} else {				
				
					$(div).append("<div class='visibilityquestion'>" + strip_tags(adaptNumbering(removeInvisibleTags($(this).find(".questiontitle").first()))) + "</div>");
					
					var answers = $(this).find(".matrixtable").find("tr").first().find(".matrix-header");
					
					$(div).append("<b style='margin-left: 5px'>" + getPropertyLabel("bycolumn") + "</b><br />");
					
					var questionid = "-1";
					
					$(answers).each(function(){						
						if ($(this).find("input[name^='shortname']").length > 0) {						
							var answerid = $(this).find("input[name^='shortname']").attr("name").substring(9);
							var answershortname = $(this).find("input[name^='shortname']").val();
							
							if (triggers.indexOf(questionid + "|" + answerid) > -1)
							{
								$(div).append("<input type='checkbox' checked='checked' style='margin-left: 10px' value='" + questionid + "|" + answerid + "' /> <span>"  + strip_tags($(this).find("textarea[name^='text']").first().text()) + " (" + answershortname + ")" + "</span><br />");
							} else {
								$(div).append("<input type='checkbox' style='margin-left: 10px' value='" + questionid + "|" + answerid + "' /> <span>"  + strip_tags($(this).find("textarea[name^='text']").first().text()) + " (" + answershortname + ")" + "</span><br />");
							}
						}
					});
					
					$(div).append("<b style='margin-left: 5px; margin-top: 5px;'>" + getPropertyLabel("bycell") + "</b><br />");
					
					$(this).find(".hiddenmatrixquestions").find("div").each(function(index){
						var questionid = $(this).attr("data-id");
						var text = $("textarea[name^='text" + questionid + "']").first().text()
						$(div).append("<div class='visibilityquestion' style='margin-left: 10px;'>" + strip_tags(text) + "</div>");
						
						$(answers).each(function(){
							if ($(this).find("input[name^='shortname']").length > 0) {	
								var answerid = $(this).find("input[name^='shortname']").attr("name").substring(9);
								var answershortname = $(this).find("input[name^='shortname']").val();
								
								if (triggers.indexOf(questionid + "|" + answerid) > -1)
								{
									$(div).append("<input type='checkbox' checked='checked' style='margin-left: 15px' value='" + questionid + "|" + answerid + "' /> <span>"  + strip_tags($(this).find("textarea[name^='text']").first().text()) + " (" + answershortname + ")" + "</span><br />");
									oldvalues = oldvalues + questionid + "|" + answerid + ";";
								} else {
									$(div).append("<input type='checkbox' style='margin-left: 15px' value='" + questionid + "|" + answerid + "' /> <span>"  + strip_tags($(this).find("textarea[name^='text']").first().text()) + " (" + answershortname + ")" + "</span><br />");
								}
							}
						});
					});
				}
			}
		});
		
		if ($(div).find("input").length == 0)
		{
			$(div).append(getPropertyLabel("NoTriggersFound"));
		}
		
		$(_elementProperties.selectedproperty).attr("data-oldvalues", oldvalues);
		
		if ($(_elementProperties.selectedproperty).nextAll().first().hasClass("propertyrow"))
		{
			var tr = $(_elementProperties.selectedproperty).nextAll().first();
			$(tr).find(".visibilitylist").empty().append($(div).html());
			$(tr).show();			
		} else {
			var tr = document.createElement("tr");
			$(tr).addClass("propertyrow visibilityrow");
			var td = document.createElement("td");
			$(td).attr("colspan","2").append("<b>" + getPropertyLabel("PleaseChooseLogic") + "<b><br />");
			
			if (!useAndLogic) {
				$(td).append(' &nbsp;&nbsp;<input type="radio" name="logic" value="false" checked="checked" onchange="updateLogic(this)" /> OR &nbsp;&nbsp;&nbsp; <input type="radio" name="logic" value="true" onchange="updateLogic(this)" /> AND<br /><br />');
			} else {
				$(td).append(' &nbsp;&nbsp;<input type="radio" name="logic" value="false" onchange="updateLogic(this)" /> OR &nbsp;&nbsp;&nbsp; <input type="radio" name="logic" value="true" checked="checked" onchange="updateLogic(this)" /> AND<br /><br />');
			}
		
			$(td).append("<b>" + getPropertyLabel("PleaseSelectTriggers") + "<b><br />");
			$(td).append(div);
			$(td).append('<div style="text-align: right"><button id="btnSaveVisibility" class="btn btn-default btn-primary btn-sm" onclick="save(this)">' + getPropertyLabel("Apply") + '</button> <button class="btn btn-default btn-sm" onclick="cancel(this)">' + getPropertyLabel("Cancel") + '</button></div>');
			$(tr).append(td);
			$(_elementProperties.selectedproperty).after(tr);
			
			$("#properties").find('[data-toggle="tooltip"]').tooltip({
			    trigger : 'hover',
			    container: 'body'
			});
		}
	} else if (label == "Columns")
	{
		var s = getColumnsText(true);
		
		_elementProperties.selectedid = $(span).closest("tr").next().find("textarea").first().attr("id");
		tinyMCE.get(_elementProperties.selectedid).setContent(s, {format : 'xhtml'});
		originaltext = s;
		$(span).closest("tr").next().show();
	} else if (label == "Rows")
	{
		var s = getRowsText(true);
		
		_elementProperties.selectedid = $(span).closest("tr").next().find("textarea").first().attr("id");
		tinyMCE.get(_elementProperties.selectedid).setContent(s, {format : 'xhtml'});
		originaltext = s;
		$(span).closest("tr").next().show();
	} else if (label == "Questions")
	{
		var s = getQuestionsText(true);
		
		_elementProperties.selectedid = $(span).closest("tr").next().find("textarea").first().attr("id");
		tinyMCE.get(_elementProperties.selectedid).setContent(s, {format : 'xhtml'});
		originaltext = s;
		$(span).closest("tr").next().show();
	} else if (label == "PossibleAnswers")
	{
		var s = getCombinedAnswerText(true);
		_elementProperties.selectedid = $(span).closest("tr").next().find("textarea").first().attr("id");
		tinyMCE.get(_elementProperties.selectedid).setContent(s, {format : 'xhtml'});
		originaltext = s;
		$(span).closest("tr").next().show();
	} else if (label == "feedback")
	{
		_elementProperties.selectedid = $(span).closest("tr").next().find("textarea").first().attr("id");
		originaltext = tinyMCE.get(_elementProperties.selectedid).getContent();
		$(span).closest("tr").next().show();
	} else if (label == "RankingItems")
	{
		console.log("RankingItem can not be edited via TinyMCE.");
	} else if (label == "Text")
	{
		_elementProperties.selectedid = $(span).closest("tr").next().find("textarea").first().attr("id");
		originaltext = tinyMCE.get(_elementProperties.selectedid).getContent();
		$(span).closest("tr").next().show();
	} else {
		var tr = $(span).closest("tr").next();
		_elementProperties.selectedid = $(tr).find("textarea").first().attr("id");	
		$(tr).show();
		originaltext = tinyMCE.get(_elementProperties.selectedid).getContent({format : 'html'});
	}
}

function editShortnames(span)
{
	_elementProperties.selectedproperty = $(span).closest("tr");
	var label = $(_elementProperties.selectedproperty).find(".propertylabel").first().attr("data-label");
	
	var tr = $(span).closest("tr").next();
	_elementProperties.selectedid = $(tr).find("textarea").first().attr("id");	
	$(tr).hide();
	var tr2 = $(tr).next();
	
	var table = $(tr2).find("table").first();
	$(table).empty();
	
	var element = getElement();
	
	var elements;
	if (element.type == "Matrix" || element.type == "Table")
	{
		if (label == "Columns")
		{
			elements = element.answers();
		} else if (label == "Rows")
		{
			elements = element.questions();
		}
	} else if (element.type == "RatingQuestion") {
		elements = element.childElements();
	} else {
		elements = element.possibleAnswers();
	}
	
	for (var i = 0; i < elements.length; i++)
	{
		var trinner = document.createElement("tr");
		var td = document.createElement("td");
		$(td).append(elements[i].title());
		$(trinner).append(td);
		td = document.createElement("td");
		var input = document.createElement("input");
		$(input).attr("type","text").val(elements[i].shortname());
		$(td).append(input);
		$(trinner).append(td);
		$(table).append(trinner);
	}
	
	$(tr2).show();
}

function getQuestionsText(useparagraphs)
{
	var id = $(_elementProperties.selectedelement).attr("data-id");
	
	var element = _elements[id];
	
	var s = "";
	var arr = [];
	
	for (var i=0; i < element.childElements().length; i++)
	{
		if (useparagraphs)
		{
			var title = element.childElements()[i].originalTitle();
			if (title.indexOf("<div") == 0)
			{
				title = title.replace("<div", "<p").replace("</div>","</p>");
				s += title;
			} else {
				s += "<p>" + title + "</p>";	
			}
		} else {
			arr[arr.length] = element.childElements()[i].originalTitle();
		}
	}
	
	if (useparagraphs)
	{
		return s;
	} else {
		return arr;
	}
}

function getColumnsText(useparagraphs)
{
	var id = $(_elementProperties.selectedelement).attr("data-id");
	
	if ($(_elementProperties.selectedelement).hasClass("matrix-header") || $(_elementProperties.selectedelement).hasClass("table-header"))
	{
		id = $(_elementProperties.selectedelement).closest(".survey-element").attr("data-id");
	}	

	var element = _elements[id];
	
	var s = "";
	var arr = [];
	
	for (var i=0; i < element.answers().length; i++)
	{
		if (useparagraphs)
		{
			var title = element.answers()[i].originalTitle();
			if (title.indexOf("<div") == 0)
			{
				title = title.replace("<div", "<p").replace("</div>","</p>");
				s += title;
			} else {
				s += "<p>" + title + "</p>";	
			}
		} else {
			var title = element.answers()[i].originalTitle();
			if (title.length == 0) {
				title = "-";
			}
			arr[arr.length] = title;
		}
	}
	
	if (useparagraphs)
	{
		return s;
	} else {
		return arr;
	}
}

function getRowsText(useparagraphs)
{
	var id = $(_elementProperties.selectedelement).attr("data-id");
	
	if ($(_elementProperties.selectedelement).hasClass("matrix-header") || $(_elementProperties.selectedelement).hasClass("table-header"))
	{
		id = $(_elementProperties.selectedelement).closest(".survey-element").attr("data-id");
	}
	
	var element = _elements[id];
	var s = "";
	var arr = [];
	
	if (element.type == "Matrix")
	{
		for (var i=0; i < element.questions().length; i++)
		{
			if (useparagraphs)
			{
				var title = element.questions()[i].originalTitle();
				if (title.indexOf("<div") == 0)
				{
					title = title.replace("<div", "<p").replace("</div>","</p>");
					s += title;
				} else {
					s += "<p>" + title + "</p>";	
				}
			} else {
				arr[arr.length] = element.questions()[i].originalTitle();
			}
		}		
	} else {
		for (var i=0; i < element.questions().length; i++)
		{	
			if (useparagraphs)
			{
				var title = element.questions()[i].originalTitle();
				if (title.indexOf("<div") == 0)
				{
					title = title.replace("<div", "<p").replace("</div>","</p>");
					s += title;
				} else {
					s += "<p>" + title + "</p>";	
				}
			} else {
				var title = element.questions()[i].originalTitle();
				if (title.length == 0) {
					title = "-";
				}
				arr[arr.length] = title;
			}
		}
	}
	
	if (useparagraphs)
	{
		return s;
	} else {
		return arr;
	}
}

function adaptSliderDisplay(isSlider)
{
	if (isSlider) {
		if (!isDelphi) {
			$("tr[data-label='Mandatory']").hide();
		}
		$("tr[data-label='Unit']").hide();
		$("tr[data-label='MinLabel']").show();
		$("tr[data-label='MaxLabel']").show();
		$("tr[data-label='InitialSliderPosition']").show();
		$("tr[data-label='DisplayGraduationScale']").show();
		$("tr[data-label='MaxDistanceToMedian']").show();
	} else {
		$("tr[data-label='Mandatory']").show();
		$("tr[data-label='Unit']").show();
		$("tr[data-label='MinLabel']").hide();
		$("tr[data-label='MaxLabel']").hide();
		$("tr[data-label='InitialSliderPosition']").hide();
		$("tr[data-label='DisplayGraduationScale']").hide();
		$("tr[data-label='MaxDistanceToMedian']").hide();
	}
}

function adaptDelphiControls(element) {
	const mandatoryPropertyRow = _elementProperties.propertyRows().find(row => row.Label() === 'Mandatory');
	if (mandatoryPropertyRow) {
		if (element.isDelphiQuestion()) {
			mandatoryPropertyRow.Disabled(true);
			mandatoryPropertyRow.Value(false);
			element.optional(true);
			
			let children = [];
			if (["Matrix", "Table"].includes(element.type)) {
				children = element.questions();
			} else if (element.type === "RatingQuestion") {
				children = element.childElements();
			}
			
			for (let i = 0; i < children.length; i++) {
				children[i].optional(true);
			}
			
		} else {
			mandatoryPropertyRow.Disabled(false);
		}
	}

	const visibilityPropertyRow = _elementProperties.propertyRows().find(row => row.Label() === 'Visibility');
	if (visibilityPropertyRow) {
		if (element.isDelphiQuestion()) {
			visibilityPropertyRow.IsVisible(false);
			resetVisibility($('#idRemoveVisibility')[0], true);
		} else {
			visibilityPropertyRow.IsVisible(true);
		}
	}
	
	if (element.isDelphiQuestion()) {
		$("tr[data-label='ShowExplanationBox']").show();
		$("tr[data-label='DelphiChartType']").show();
	} else {
		$("tr[data-label='ShowExplanationBox']").hide();
		$("tr[data-label='DelphiChartType']").hide();
	}
}

function adaptDelphiChildControls(element, parent) {
	const mandatoryPropertyRow = _elementProperties.propertyRows().find(row => row.Label() === 'Mandatory');
	if (parent.isDelphiQuestion()) {
		if (mandatoryPropertyRow != null) {
			mandatoryPropertyRow.Disabled(true);
			mandatoryPropertyRow.Value(false);
		}
	} else {
		mandatoryPropertyRow.Disabled(false);
	}
}

function addFormulaText(text, id) {
	const formulaElement = $("#" + id);
	
	const cursorPos = formulaElement.prop('selectionStart');
	const oldtext = formulaElement.val();
    const textBefore = oldtext.substring(0,  cursorPos);
    const textAfter  = oldtext.substring(cursorPos, oldtext.length);

    formulaElement.val(textBefore + text + textAfter).focus();
	formulaElement.prop('selectionStart', cursorPos + text.length);
	formulaElement.prop('selectionEnd', cursorPos + text.length)

	$(formulaElement).blur()
}

let formulaDialogId = 0;
function showFormulaDialog(id) {
	formulaDialogId = id;
	
	adaptFormulaInfo();	
	$('#FormulaDialog').modal("show");
}

function adaptFormulaInfo() {
	if ($("#FormulaFunction").val() == "mean") {
		$("#FormulaDialogMean").show();
		$("#FormulaDialogMin").hide();
		$("#FormulaDialogMax").hide();
	} else if ($("#FormulaFunction").val() == "min") {
		$("#FormulaDialogMean").hide();
		$("#FormulaDialogMin").show();
		$("#FormulaDialogMax").hide();
	} else {
		$("#FormulaDialogMean").hide();
		$("#FormulaDialogMin").hide();
		$("#FormulaDialogMax").show();
	}
}

function addFormula(value) {
	const formulaElement = $("#" + formulaDialogId);
	$(formulaElement).val($(formulaElement).val() + value + "(");
	$(formulaElement).blur();
	$('#FormulaDialog').modal("hide");
}

function clearFormula(id) {
	const formulaElement = $("#" + id);
	$(formulaElement).val("");
	$(formulaElement).blur();
}