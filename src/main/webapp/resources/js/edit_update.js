function change(input, event)
{
	if (event.cancelable === false) { //user changed
		 update(input);
	} else { // program changed
	
	}
}

function update(input)
{
	var label = "";

	if ($(input).closest(".firstpropertyrow").length > 0)
	{
		label = $(input).closest(".firstpropertyrow").find(".propertylabel").first().attr("data-label");
	} else {
		label = $(input).attr("data-label");
	}

	var id = $(_elementProperties.selectedelement).attr("data-id");
	var element = _elements[id];
	
	_elementProperties.selectedproperty = $(input).closest("tr");
	var hasInputError = $(input).closest("tr").hasClass("invalidinput");
	$(input).closest("tr").removeClass("invalidinput");
	
	switch (label) {
		case "TabTitle":
			var text = $(input).val();		
			if (!checkMandatory(text) || !checkCharacters(text))
			{
				return;
			}		
			var oldtext = element.tabTitle();
			 element.tabTitle(text);
			_undoProcessor.addUndoStep(["TabTitle", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			break;
		case "Level":
			var text = $(input).val();
			var oldtext = element.level(); 
			element.level(parseInt(text));
			_undoProcessor.addUndoStep(["Level", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			updateTitles();
			break;
		case "Mandatory":
			toggleMandatory(input);
			break;
		case "AutoNumbering":
			var checked = $(input).is(":checked");
			element.numbering(checked);
			_undoProcessor.addUndoStep(["AutoNumbering", id, !checked, checked]);
			break;
		case "ReadOnly":
			var checked = $(input).is(":checked");
			var text = checked ? "true" : "false";
			var oldtext = checked ? "false" : "true";
			element.readonly(checked);
			_undoProcessor.addUndoStep(["ReadOnly", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			break;
		case "Identifier":
			var text = $(input).val();		
			if (!checkMandatory(text) || !checkCharacters(text) || !checkUniqueIdentifier(input))
			{
				return;
			}	
			
			updateIdentifier(element, id, text, false);			
			break;
		case "Rows":
			if(element.type == "Matrix")
			{
				if(element.isInterdependent())
				{
					checkInterdependentMatrix(input);
				}
			} else if(element.type == "Table") {
				break;
			} else {
				var text = $(input).val();
				var oldtext = element.numRows();
				element.numRows(parseInt(text));
				//the following line is needed to reset the height of the textarea
				$(_elementProperties.selectedelement).find("textarea.freetext, textarea.regex").css("height","");
				_undoProcessor.addUndoStep(["Rows", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			}
			break;
		case "Columns":
			var text = $(input).val();
			var oldindex;
			var newindex = parseInt(text);
			if (element.type == "GalleryQuestion")
			{
				oldindex = element.columns();
				element.columns(newindex);
				updateGallery();
			} else if(element.type == "Matrix"){
				if(element.isInterdependent())
				{
					checkInterdependentMatrix(input);
				}
			} else if(element.type == "Table") {
				break;
			} else {
				oldindex = element.numColumns();
				element.numColumns(newindex);
				updateChoice();

				checkNumColumns(input, element);
			}
			_undoProcessor.addUndoStep(["Columns", id, oldindex, newindex]);
			break;
		case "AcceptedNumberOfCharacters":
			
			if (!checkPositive(input))
			{
				return;
			}
			
			if (!checkMinMax(input, hasInputError, null, $(".quizrule").length > 0))
			{
				return;
			}
			
			if ($(input).attr("data-type") == "min")
			{
				var mins = $(input).closest("tr").find("[data-type=min]").val();
				var min = mins.length > 0 ? parseInt(mins) : 0;
				var oldmin = element.minCharacters();
				if (min != oldmin)
				{
					element.minCharacters(min);
					_undoProcessor.addUndoStep(["AcceptedNumberOfCharacters", id, $(_elementProperties.selectedelement).index(), oldmin, min, "min"]);
				}
			} else {
				var maxs = $(input).closest("tr").find("[data-type=max]").val();
				var max = maxs.length > 0 ? parseInt(maxs) : 0;
				var oldmax = element.maxCharacters();
				if (max != oldmax)
				{
					element.maxCharacters(max);
					$(_elementProperties.selectedelement).find("textarea, input").not(":hidden").attr("disabled", "disabled");
					$(_elementProperties.selectedelement).find(".expand").TextAreaExpander();
					_undoProcessor.addUndoStep(["AcceptedNumberOfCharacters", id, $(_elementProperties.selectedelement).index(), oldmax, max, "max"]);
				}
			}
			break;
		case "Unique":
			var checked = $(input).is(":checked");
			var oldtext = element.isUnique();
			element.isUnique(checked);
			_undoProcessor.addUndoStep(["Unique", id, $(_elementProperties.selectedelement).index(), oldtext, checked]);
			break;
		case "Comparable":
			var checked = $(input).is(":checked");
			var oldtext = element.isComparable();
			element.isComparable(checked);
			_undoProcessor.addUndoStep(["Comparable", id, $(_elementProperties.selectedelement).index(), oldtext, checked]);
			break;
		case "Password":
			var checked = $(input).is(":checked");
			var oldtext = element.isPassword();
			element.isPassword(checked);
			_undoProcessor.addUndoStep(["Password", id, $(_elementProperties.selectedelement).index(), oldtext, checked]);
			break;
		case "Attribute":
			var checked = $(input).is(":checked");
			var oldtext = element.isAttribute();
			element.isAttribute(checked);
			_undoProcessor.addUndoStep(["Attribute", id, $(_elementProperties.selectedelement).index(), oldtext, checked]);
			break;
		case "DelphiQuestion":
			var checked = $(input).is(":checked");
			var oldtext = element.isDelphiQuestion();
			element.isDelphiQuestion(checked);
			
			adaptDelphiControls(element);

			$('#' + id).toggleClass("delphi");
			_undoProcessor.addUndoStep(["DelphiQuestion", id, $(_elementProperties.selectedelement).index(), oldtext, checked]);
			break;
		case "DelphiChartType":
			var newValue = $(input).val();
			var oldValue = element.delphiChartType();
			element.delphiChartType(newValue);
			_undoProcessor.addUndoStep(["DelphiChartType", id, $(_elementProperties.selectedelement).index(), oldValue, newValue]);
			break;
		case "ShowExplanationBox":
			var newValue = $(input).is(":checked");
			var oldValue = element.showExplanationBox();
			element.showExplanationBox(newValue);
			_undoProcessor.addUndoStep(["ShowExplanationBox", id, $(_elementProperties.selectedelement).index(), oldValue, newValue]);
			break;
		case "Name":
			var text = $(input).val();
			var oldtext = element.attributeName();
			
			if (text != oldtext)
			{
				if (!checkUniqueAttributeName(input))
				{
					return;
				}
				
				element.attributeName(text);
				_undoProcessor.addUndoStep(["Name", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			}
			break;
		case "Display":
			var text = $(input).val();
			var oldtext = element.displayMode();
			var display = 0;
			if (text == "ISOOnly") display = 1;
			if (text == "ISO+Country") display = 2;
			if (text == "Country+ISO") display = 3;
			element.displayMode(display);
			_undoProcessor.addUndoStep(["DisplayMode", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			addElementHandler($(_elementProperties.selectedelement));
			break;
		case "DisplaySlider":
			var text = $(input).val();
			var oldtext = element.display();
			element.display(text);
			_undoProcessor.addUndoStep(["DisplaySlider", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			addElementHandler($(_elementProperties.selectedelement));
			adaptSliderDisplay(text === "Slider");
			
			if (text === "Slider")
			{
				element.optional(true);
				$('#idPropertyMandatory').removeAttr("checked");
				
				element.unit("");
				$('tr[data-label=Unit]').find("input[type=text]").val("");
				
				if (element.min() == null)
				{
					$("tr[data-label='Values']").find("input[data-type='min']").val("0");
					element.min(0);
					element.minString("0");									
					element.initVal = null;
				}
				if (element.max() == null)
				{
					$("tr[data-label='Values']").find("input[data-type='max']").val("10");
					element.max(10);
					element.maxString("10");
				}
				
				if (element.minLabel() == null)
				{
					element.minLabel("Very unlikely");
					$("tr[data-label='MinLabel']").find("input[type='text']").val("Very unlikely");
				}
				if (element.maxLabel() == null)
				{
					element.maxLabel("Very likely");
					$("tr[data-label='MaxLabel']").find("input[type='text']").val("Very likely");
				}
				
				initSlider($(".selectedquestion").find(".sliderbox").first(), true, element);
			}
			
			break;
		case "MinLabel":
			var text = $(input).val();
			var oldtext = element.minLabel();
			element.minLabel(text);
			_undoProcessor.addUndoStep(["MinLabel", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			addElementHandler($(_elementProperties.selectedelement));
			break;
		case "MaxLabel":
			var text = $(input).val();
			var oldtext = element.maxLabel();
			element.maxLabel(text);
			_undoProcessor.addUndoStep(["MaxLabel", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			addElementHandler($(_elementProperties.selectedelement));
			break;
		case "DisplayGraduationScale":
			var checked = $(input).is(":checked");
			var text = checked ? "true" : "false";
			var oldtext = checked ? "false" : "true";
			element.displayGraduationScale(checked);
			_undoProcessor.addUndoStep(["DisplayGraduationScale", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			$(".selectedquestion").find(".sliderbox").first().bootstrapSlider().bootstrapSlider('destroy');
			initSlider($(".selectedquestion").find(".sliderbox").first(), true, element);
			break;
		case "InitialSliderPosition":
			var text = $(input).val();
			var oldtext = element.initialSliderPosition();
			element.initialSliderPosition(text);
			element.initVal = null;
			_undoProcessor.addUndoStep(["InitialSliderPosition", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			addElementHandler($(_elementProperties.selectedelement));
			const numberfield = $(".selectedquestion").find(".sliderbox").first();
			numberfield.attr("data-slider-value",  element.initialValue());
			initSlider(numberfield, true, element);
			break;			
		case "Order":
			var text = $(input).val();
			var oldtext = element.order();
			var order = 0;
			if (text == "Alphabetical") order = 1;
			if (text == "Random") order = 2;
			element.order(order);
			_undoProcessor.addUndoStep(["Order", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			addElementHandler($(_elementProperties.selectedelement));
			break;
		case "OrderSection":
			var text = $(input).val();
			var oldtext = element.order();
			var order = 0;
			if (text == "Random") order = 1;
			element.order(order);
			_undoProcessor.addUndoStep(["OrderSection", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			addElementHandler($(_elementProperties.selectedelement));
			break;
		case "Style":
			if ($(_elementProperties.selectedelement).hasClass("matrixitem"))
			{
				var text = $(input).val();
				var oldtext = text == "SingleChoice" ? "MultipleChoice" : "SingleChoice";
				if (text == "SingleChoice")
				{
					element.isSingleChoice(true);
				} else {
					element.isSingleChoice(false);
				}
				_undoProcessor.addUndoStep(["Style", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
				checkInputStates();
			} else if ($(_elementProperties.selectedelement).hasClass("ruleritem")) {
				var text = $(input).val();
				var oldtext = element.style();
				element.style(text);
				_undoProcessor.addUndoStep(["Style", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			} else {
				var text = $(input).val();
				var oldtext;
				switch (element.choiceType())
				{
					case "radio":
						oldtext = "RadioButton";
						break;
					case "select":
						oldtext = "SelectBox";
						break;
					case "checkbox":
						oldtext = "CheckBox";
						break;
					case "list":
						oldtext = "ListBox";
						break;
					case "likert":
						oldtext = "LikertScale";
						break;
				}
				
				if (text == "LikertScale")
				{
					element.likert(true);
					element.choiceType("likert");				
				} else if (text == "RadioButton")
				{
					element.useRadioButtons(true);
					element.choiceType("radio");
					element.likert(false);
				} else if (text == "SelectBox")
				{
					element.choiceType("select");
					element.useRadioButtons(false);
					element.likert(false);
				} else if (text == "CheckBox")
				{
					element.useCheckboxes(true);
					element.choiceType("checkbox");
				} else if (text == "ListBox")
				{
					element.choiceType("list");
					element.useCheckboxes(false);					
				}
				updateChoice();
				_undoProcessor.addUndoStep(["Style", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			}
			break;
		case "Unit":
			var text = $(input).val();		
			if (!checkCharacters(text))
			{
				return;
			}			
			var oldtext = element.unit();
			element.unit(text);
			_undoProcessor.addUndoStep(["Unit", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			break;
		case "Size":
			var size = $(input).val();
			if (element.type == "Image")
			{
				if (size == "0") size = "100";			
				if (!checkInteger(input))
				{
					return;
				}

				updateImageSize(element, size, input, false);
			} else {
				updateMatrixSize(element, size, false);
			}
			break;
		case "Values":
			var text = $(input).val();
			var oldtext = $(input).attr("data-type") == "min" ? element.min() : element.max();
			if (element.type == "DateQuestion")
			{
				if (!checkMinMaxDate(input, hasInputError, $(".quizrule").length > 0))
				{
					return;
				}
			} else if (element.type == "TimeQuestion")
			{
				if (!checkMinMaxTime(input, hasInputError, $(".quizrule").length > 0))
				{
					return;
				}
			} else {
				if (!checkMinMax(input, hasInputError, null, $(".quizrule").length > 0))
				{
					return;
				}
			}
			
			var v = parseInt(text);
			
			if ($(input).attr("data-type") == "min")
			{
				if (element.min() == null && text.length == 0)
				{
					//nothing to do
				} else if (element.min() != v)
				{
					element.min(text.length == 0 ? null : text);
					element.minString(text);
					_undoProcessor.addUndoStep(["Values", id, $(_elementProperties.selectedelement).index(), oldtext, text, $(input).attr("data-type")]);	
				}
			} else if ($(input).attr("data-type") == "max")
			{
				if (element.max() == null && text.length == 0)
				{
					//nothing to do
				} else if (element.max() != v)
				{
					element.max(text.length == 0 ? null : text);
					element.maxString(text);
					_undoProcessor.addUndoStep(["Values", id, $(_elementProperties.selectedelement).index(), oldtext, text, $(input).attr("data-type")]);	
				}				
			}
			
			if (element.type == "FormulaQuestion")
			{
				return;
			}
			
			if (element.type == "DateQuestion")
			{
				$(".quiz.invalidinput").each(function(){
					$(this).find("input[data-label=ruleValue]").each(function(){
						update(this);
					});
				});
				
				return;
			}
			
			if (element.display() === "Slider") {
				if (element.min() == null || element.min().length == 0)
				{
					$(input).val("0");
					element.min("0");
					element.minString("0");
				}
				if (element.max() == null || element.max().length == 0)
				{
					$(input).val("10");
					element.max("10");
					element.maxString("10");
				}
				
				initSlider($(".selectedquestion").find(".sliderbox").first(), true, element);				
			}
			
			break;
		case "NumberOfChoices":
			var numanswers = $(_elementProperties.selectedelement).find("input[name^=pashortname]").length;
			if (!checkMinMax(input, hasInputError, numanswers, $(".quizrule").length > 0))
			{
				return;
			}
			var text = $(input).val();
			if (text.length == 0) text = 0;
			var oldtext = $(input).attr("data-type") == "min" ? element.minChoices() : element.maxChoices();
			
			var v = parseInt(text);
			if ($(input).attr("data-type") == "min")
			{
				if (element.minChoices() != v)
				{
					element.minChoices(v);
					_undoProcessor.addUndoStep(["NumberOfChoices", id, $(_elementProperties.selectedelement).index(), oldtext, text, "min"]);
					removeValidationMarkup($("#btnRemovePossibleAnswers").closest("tr"));
				}
			} else if ($(input).attr("data-type") == "max")
			{
				if (element.maxChoices() != v)
				{
					element.maxChoices(v);
					_undoProcessor.addUndoStep(["NumberOfChoices", id, $(_elementProperties.selectedelement).index(), oldtext, text, "max"]);
				}
			}
			break;
		case "NumberOfAnsweredRows":
			var numrows = $(_elementProperties.selectedelement).find(".matrixtable").first().find("tr").length-1;
			if (!checkMinMax(input, hasInputError, numrows, false))
			{
				return;
			}
			var text = $(input).val();
			var oldvalue = $(input).attr("data-type") == "min" ? element.minRows() : element.maxRows();
			if (text.length == 0) text = 0;
			var v = parseInt(text);
			
			if (v != oldvalue)
			{
				if ($(input).attr("data-type") == "min")
				{
					element.minRows(v);
					removeValidationMarkup($("#btnRemoveRows").closest("tr"));								
				} else if ($(input).attr("data-type") == "max")
				{
					element.maxRows(v);
				}
				_undoProcessor.addUndoStep(["NumberOfAnsweredRows", id, $(_elementProperties.selectedelement).index(), oldvalue, v, $(input).attr("data-type")]);
			}
			
			//also update "other" element
			if ($(input).attr("data-to"))
			{
				text = $("#" + $(input).attr("data-to")).val();
				oldvalue = oldvalue = element.maxRows();
			} else {
				text = $("#" + $(input).attr("data-from")).val();
				oldvalue = element.minRows();
			}
			
			if (text.length == 0) text = 0;
			v = parseInt(text);
			var othertype;
			if (v != oldvalue)
			{
				if ($(input).attr("data-type") == "min")
				{
					element.maxRows(v);
					othertype = "max";
				} else if ($(input).attr("data-type") == "max")
				{
					element.minRows(v);
					othertype = "min";
				}
				_undoProcessor.addUndoStep(["NumberOfAnsweredRows", id, $(_elementProperties.selectedelement).index(), oldvalue, v, othertype]);
			}			
			
			break;
		case "Align":
			var text = $(input).val();
			var oldtext = element.align();
			element.align(text);
			
			var td = $(input).closest("td");
			$(td).find("button.active").removeClass("active");
			if (text == "left")
			{
				$(td).find("button[data-id=image-dialog-align-left]").addClass("active");
			} else if (text == "right")
			{
				$(td).find("button[data-id=image-dialog-align-right]").addClass("active");
			} else
			{
				$(td).find("button[data-id=image-dialog-align-center]").addClass("active");
			}		
			_undoProcessor.addUndoStep(["Align", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			break;
		case "DescriptiveText":
			var text = $(input).val();	
			
			if (text != oldtext)
			{
				if (!checkXHTML(text))
				{
					return;
				}
				
				if ($(_elementProperties.selectedelement).is("td"))
				{
					var galleryid =  $(_elementProperties.selectedelement).closest(".survey-element").attr("data-id");
					var gallery = _elements[galleryid];
					var uid = $(_elementProperties.selectedelement).attr("data-uid");
					for (var i = 0; i < gallery.files().length; i++)
					{
						var file = gallery.files()[i];
						if (file.uid() == uid)
						{
							var oldtext = file.desc();
							file.desc(text);
							_undoProcessor.addUndoStep(["DescriptionGallery", galleryid, uid, oldtext, text]);
							break;
						}
					}
				} else {
					var oldtext = element.originalTitle();
					element.originalTitle(text);
					_undoProcessor.addUndoStep(["DescriptiveText", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
					updateNavigation($(_elementProperties.selectedelement), id);
				}
			}
			break;
		case "LongDescription":
			var text = $(input).val();		
			if (!checkURL(text))
			{
				return;
			}
			
			if ($(_elementProperties.selectedelement).is("td"))
			{
				var galleryid =  $(_elementProperties.selectedelement).closest(".survey-element").attr("data-id");
				var gallery = _elements[galleryid];
				var uid = $(_elementProperties.selectedelement).attr("data-uid");
				for (var i = 0; i < gallery.files().length; i++)
				{
					var file = gallery.files()[i];
					if (file.uid() == uid)
					{
						var oldtext = file.longdesc();
						file.longdesc(text);
						_undoProcessor.addUndoStep(["LongDescriptionGallery", galleryid, uid, oldtext, text]);
						break;
					}
				}
			} else {
				var oldtext = element.longdesc();
				
				if (text != oldtext)
				{				
					element.longdesc(text);
					_undoProcessor.addUndoStep(["LongDescription", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
				}
			}
			
			break;
		case "Title":
			var text = $(input).val();		
			if (!checkXHTML(text) || !checkMandatory(text))
			{
				return;
			}
			
			var galleryid =  $(_elementProperties.selectedelement).closest(".survey-element").attr("data-id");
			var gallery = _elements[galleryid];
			
			var uid = $(_elementProperties.selectedelement).attr("data-uid");
			
			for (var i = 0; i < gallery.files().length; i++)
			{
				var file = gallery.files()[i];
				if (file.uid() == uid)
				{
					var oldtext = file.name();
					file.name(text);
					if (oldtext != text)_undoProcessor.addUndoStep(["Title", galleryid, uid, oldtext, text]);
					break;
				}
			}
			break;
		case "RegularExpression":
			var text = $(input).val();
			var oldtext = element.regex();		
			element.regex(text);
			_undoProcessor.addUndoStep(["RegularExpression", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			break;
		case "Visibility":
			var dependent = $(input).val() == "dependent";
			if (dependent)
			{
				$(input).closest("tr").find(".glyphicon-pencil").show();
			} else {
				$(input).closest("tr").find(".glyphicon-pencil").hide();
			}
			break;
		case "ImageSelectable":
			var checked = $(input).is(":checked");
			element.selection(checked);
			_undoProcessor.addUndoStep(["ImageSelectable", id, $(_elementProperties.selectedelement).index(), !checked, checked]);
			updateGallery();
			checkGalleryProperties(true);
			break;
		case "DecimalPlaces":
			var text = $(input).val();
			
			if (text.length == 0) text = "0";
			
			var oldtext = element.decimalPlaces();
			element.decimalPlaces(parseInt(text));
			
			if ($(".quizrule").length > 0)
			addValidationHint(input, "checkRules");
			$(".quiz.invalidinput").each(function(){
				$(this).find("input[data-label=ruleValue]").each(function(){
					update(this);
				});
			});
			
			_undoProcessor.addUndoStep(["DecimalPlaces", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			
			if (element.type == "NumberQuestion" && element.display() === "Slider") {
				initSlider($(".selectedquestion").find(".sliderbox").first(), true, element);				
			}
			
			break;
		case "LabelText":
			var text = $(input).val();		
			if (!checkMandatory(text) || !checkCharacters(text))
			{
				return;
			}
			
			var oldtext = element.confirmationlabel();
			element.confirmationlabel(text);
			
			$(_elementProperties.selectedelement).find("a.confirmationlabel").html(text);
			
			_undoProcessor.addUndoStep(["LabelText", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			break;
		case "MaxSelections":
			var text = $(input).val();
			if (!checkGallerySelections(text))
			{
				return;
			}
			var oldtext = element.limit();
			
			if (oldtext != parseInt(text))
			{
				element.limit(parseInt(text));
				updateGallery();
				_undoProcessor.addUndoStep(["MaxSelections", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			}
			break;
		case "Interdependency":
			var checked = $(input).is(":checked");
			if(element.type == "Matrix") {
				if(checked){
					if (!checkInterdependentMatrix(input)) {
						$(input).removeAttr("checked");
						return;
					}
				} else {
					removeValidationMarkup($(".firstpropertyrow[data-label=Columns]"));
					removeValidationMarkup($(".firstpropertyrow[data-label=Rows]"));
					removeValidationMarkup($("#idPropertyInterdependency").closest(".firstpropertyrow"));
				}
			}
			
			element.isInterdependent(checked);
			_undoProcessor.addUndoStep(["Interdependency", id, $(_elementProperties.selectedelement).index(), !checked, checked]);
			
			break;
		case "QuizQuestion":
			var checked = $(input).is(":checked");
			var oldvalue = element.scoring() + 0;
			if (!checked)
			{
				$("input[name=scoring][type=radio]").removeAttr("checked");
				$("input[name=scoring][type=radio]").closest(".firstpropertyrow").hide();
				element.scoring(0);				
			} else {
				element.scoring(1);		
				$("input[name=scoring][type=radio]").closest(".firstpropertyrow").show();
				$("input[name=scoring][type=radio]").first().prop("checked","checked");
				$("input[name=scoring][type=radio]").first().closest("tr").find("input.scoringpoints").spinner( "option", "disabled", false );				
			}
			_undoProcessor.addUndoStep(["scoring", id, $(_elementProperties.selectedelement).index(), oldvalue, element.scoring()]);
			break;
		case "Scoring":
			var text = $(input).val();
			var newvalue = parseInt(text);
			
			if ($(input).hasClass("scoringpoints"))
			{
				if (!checkPositive(input))
				{
					return;
				}
				
				var oldvalue = element.points();			
				element.points(newvalue);
				_undoProcessor.addUndoStep(["points", id, $(_elementProperties.selectedelement).index(), oldvalue, newvalue]);
			} else if ($(input).hasClass("noNegativeScore"))
			{
				var checked = $(input).is(":checked");
				var id = $(input).closest("tr").attr("data-id");
				var oldvalue = element.noNegativeScore();	
				element.noNegativeScore(checked);
				_undoProcessor.addUndoStep(["noNegativeScore", id, $(_elementProperties.selectedelement).index(), oldvalue, checked]);	
			} else {
				var oldvalue = element.scoring();
				element.scoring(newvalue);
				_undoProcessor.addUndoStep(["scoring", id, $(_elementProperties.selectedelement).index(), oldvalue, newvalue]);
			}
			checkQuizOtherValues();
			
			break;
		case "correct":
			var checked = $(input).attr("data-value") == "true";
			var childid = $(input).closest("tr").attr("data-id");			
			var oldvalue; 			
			
			if (element.type == "SingleChoiceQuestion" || element.type == "MultipleChoiceQuestion")
			{
				var answer = element.getChild(childid);
				oldvalue = answer.scoring.correct();	
				answer.scoring.correct(checked);
			} else  {
				var scoring = element.getScoringItem(childid);
				oldvalue = scoring.correct();	
				scoring.correct(checked);
			}			
			
			_undoProcessor.addUndoStep(["correct", id, childid, oldvalue, checked]);
			break;
		case "points":
			if (!checkValidNumber(input))
			{
				return;
			}
			
			var newvalue = parseInt($(input).val());
			var id = $(input).closest("tr").attr("data-id");
			var oldvalue;
			
			if (element.type == "SingleChoiceQuestion" || element.type == "MultipleChoiceQuestion")
			{
				var answer = element.getChild(id);
				oldvalue = answer.scoring.points();	
				answer.scoring.points(newvalue);
			} else  {
				var scoring = element.getScoringItem(id);
				oldvalue = scoring.points();	
				scoring.points(newvalue);
			}
			checkQuizOtherValues();
			
			_undoProcessor.addUndoStep(["points", id, $(_elementProperties.selectedelement).index(), oldvalue, newvalue]);
			break;
		case "ruleValueType":
			var scoringId = $(input).closest("tr").attr("data-id");
			var scoring = element.getScoringItem(scoringId);
			if (scoring == null) return;

			removeValidationMarkup();
			if ($(input).val() !=  "between")
			{
				scoring.value2("");
				$(input).parent().find("input[data-label='ruleValue2']").val("");
			}
			
			var oldvalue = scoring.type();
			
			switch ($(input).val())
			{
				case "lessThan":
					scoring.type(1);
					break;
				case "lessThanOrEqualTo":
					scoring.type(2);
					break;
				case "equalTo":
					scoring.type(0);
					break;
				case "greaterThan":
					scoring.type(3);
					break;
				case "greaterThanOrEqualTo":
					scoring.type(4);
					break;
				case "between":
					scoring.type(5);
					break;
				case "matches":
					scoring.type(7);
					break;
				case "contains":
					scoring.type(6);
					break;
				case "other":
					scoring.type(-1);
					scoring.correct(false);
					break;
				case "empty":
					scoring.type(8);
					scoring.correct(false);
					break;					
			}
			checkQuizOtherValues();
			_undoProcessor.addUndoStep(["type", id, $(_elementProperties.selectedelement).index(), oldvalue, scoring.type(), scoringId]);
			break;
		case "ruleValue":
			var scoringId = $(input).closest("tr").attr("data-id");
			var scoring = element.getScoringItem(scoringId);
			if (scoring == null) return;
			
			var oldvalue = scoring.value() + "";			
			if ($(input).val() == oldvalue) return;
			
			if (element.type == "NumberQuestion")
			{
				if (!checkNumber($(input), element.decimalPlaces()))
				{
					return;
				}
				if (!checkMinMaxNumberRule(input, hasInputError, element.min(), element.max()))
				{
					return;
				}
			} else if (element.type == "DateQuestion")
			{
				if (!checkMinMaxDateRule(input, hasInputError, element.minString(), element.maxString()))
				{
					return;
				}
			} else if (element.type == "FreeTextQuestion")
			{
				if (!checkMinMaxRule(input, hasInputError, element.minCharacters(), element.maxCharacters()))
				{
					return;
				}
			}
			
			scoring.value($(input).val());
			_undoProcessor.addUndoStep(["ruleValue", id, $(_elementProperties.selectedelement).index(), oldvalue, scoring.value(), scoringId]);
			break;
		case "ruleValue2":
			var scoringId = $(input).closest("tr").attr("data-id");
			var scoring = element.getScoringItem(scoringId);
			if (scoring == null) return;
			
			var oldvalue = scoring.value2() + "";
			if ($(input).val() == oldvalue) return;
			
			if (element.type == "NumberQuestion")
			{
				if (!checkNumber($(input), element.decimalPlaces()))
				{
					return;
				}
				if (!checkMinMaxNumberRule(input, hasInputError, element.min(), element.max()))
				{
					return;
				}
			} else if (element.type == "DateQuestion")
			{
				if (!checkMinMaxDateRule(input, hasInputError, element.minString(), element.maxString()))
				{
					return;
				}
			}
			
			scoring.value2($(input).val());
			_undoProcessor.addUndoStep(["ruleValue2", id, $(_elementProperties.selectedelement).index(), oldvalue, scoring.value2(), scoringId]);
			break;
		case "FileType":
			if (!checkValidExtensions(input))
			{
				return;
			}
			
			var text = $(input).val();
			var oldtext = element.extensions();
			element.extensions(text);
			
			_undoProcessor.addUndoStep(["FileType", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			break;
		case "IconType":			
			var text = 0;
			var v = $(input).val();
			if (v == "Circles")
			{
				text = 1;
			} else if (v == "Hearts")
			{
				text = 2;
			}
			var oldtext = element.iconType();
			
			if (text != oldtext)
			{
				element.iconType(text);
				_undoProcessor.addUndoStep(["IconType", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			}
			break;
		case "NumIcons":			
			var text = parseInt($(input).val());
			
			if (text < 2)
			{
				text = 2;
				$(input).val(text);
			}
			if (text > 10)
			{
				text = 10;
				$(input).val(text);
			}		
			
			var oldtext = element.numIcons();
			
			if (text != oldtext)
			{
				element.numIcons(text);
				_undoProcessor.addUndoStep(["NumIcons", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			}
			break;
		case "Color":
			var text = $(input).val();
			var oldtext = element.color();
			element.color(text);
			_undoProcessor.addUndoStep(["Color", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			break;
		case "Height":
			var text = $(input).val();
			var oldtext = element.height();
			element.height(text);
			_undoProcessor.addUndoStep(["Height", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
			break;
		case "MaximumFileSize":
			var text = $(input).val();			
			if (text.length == 0) text = "1";			
			var oldtext = element.maxFileSize();
			element.maxFileSize(parseInt(text));			
			_undoProcessor.addUndoStep(["MaximumFileSize", id, $(_elementProperties.selectedelement).index(), oldtext, text]);			
			break;
		case "MaxDistanceToMedian":
			var text = $(input).val();	
			var oldtext = element.maxDistance();
			
			if (element.type == "NumberQuestion")
			{
				if (text.trim().length == 0) {
					text = "-1";
				}				
				
				element.maxDistance(parseFloat(text));	
			} else {
				var index = $(input).prop('selectedIndex');
				if (index == 0)
				{
					text = "-1";
				}
				
				element.maxDistance(parseInt(text));			
			}
			
			_undoProcessor.addUndoStep(["MaxDistanceToMedian", id, $(_elementProperties.selectedelement).index(), oldtext, text]);			
		
			break;
		case "Formula":
			var text = $(input).val();	
			var oldtext = element.formula();
			
			if (!checkFormula($(input), element)) {
				return;
			}
			
			element.formula(text);
			
			_undoProcessor.addUndoStep(["Formula", id, $(_elementProperties.selectedelement).index(), oldtext, text]);			
			
			break;
		default:
			throw label + " not implemented"; 
	}	
	
	$(input).removeClass("activeproperty");
}

function updateFeedback(span, reset)
{
	var eid = $(_elementProperties.selectedelement).attr("data-id");
	var element = _elements[eid];
	
	_elementProperties.selectedid = $(span).closest("tr").find("textarea").first().attr("id");
	var text = "";
	if (!reset) text = tinyMCE.get(_elementProperties.selectedid).getContent();
	
	var id = $(span).closest("tr").attr("data-id");
	var oldtext;
	if (element.type == "SingleChoiceQuestion" || element.type == "MultipleChoiceQuestion")
	{
		var answer = element.getChild(id);
		oldtext = answer.scoring.feedback();	
		answer.scoring.feedback(text);
	} else  {
		var scoring = element.getScoringItem(id);
		oldtext = scoring.feedback();	
		scoring.feedback(text);
	}
		
	_undoProcessor.addUndoStep(["Feedback", eid, id, oldtext, text]);
	
	if (!reset)	$(span).closest("tr").hide();
}

var selectedspan;
function updateVisibility(span, reset, ask, dialogresult, noUndo)
{
	var id = $(_elementProperties.selectedelement).attr("data-id");
	var triggers = document.createElement("div");
	$(triggers).addClass("triggers");
	
	var list = $(span).length > 0 ? $(span).closest("td").find(".visibilitylist").first() : null;
	var dependent = false;
	
	var oldtriggers = [];
	$(_elementProperties.selectedproperty).find(".triggers").first().find("a").each(function(){
		oldtriggers[oldtriggers.length] = $(this).attr("data-targetid");
	});
	
	var values = "";
	var selectedquestions = "";
	var oldtext = $(_elementProperties.selectedproperty).attr("data-oldvalues");
	
	var isSection = $(_elementProperties.selectedelement).hasClass("sectionitem");
	var includechildren = false;
	if (isSection && ask && $("#content")) {
		selectedspan = span;
		$("#askSectionVisibilityDialog").modal("show");
		return;
	}
        
	if (isSection && !ask && dialogresult) {
		var level = parseInt($(_elementProperties.selectedelement).find(".sectiontitle").first().attr("data-level"));
   
		var section = _elements[id];
		var useAndLogic = section.useAndLogic();		
		
		$(_elementProperties.selectedelement).nextAll().each(function(){
			if ($(this).hasClass("sectionitem"))
			{
				var level2 = parseInt($(this).find(".sectiontitle").first().attr("data-level"));
				if (level2 <= level)
				{
					return false;
				}
			}
			
			var qid = $(this).attr("data-id");
			var question = _elements[qid];
			question.useAndLogic(useAndLogic);
	 
			$(this).addClass("selectedquestion");
		});
	}
	
	if (!reset)
	{
		//check column selections first
		$(list).find("input:checked").each(function(){
			var id = $(this).val();
			if (id.indexOf("-1|") == 0)
			{
				var answerpart = id.substring("3");
				$(list).find("input[value$=" + answerpart + "]").prop("checked","checked");
				$(this).removeAttr("checked");
			}
		});
						
		$(list).find("input:checked").each(function(){
			var a = document.createElement("a");			
			var label = $(this).nextAll().first().html();			
			$(a).html(label);			
			var id = $(this).val();
			$(a).attr("data-targetid", id);
			
			values = values + id + ";";
			setHoverOnVisibilityLink(a);
			
			$("#content").find(".selectedquestion").each(function(){
				var myid = $(this).attr("id");
				selectedquestions = selectedquestions + myid + ";";
				addVisibility(id, myid);
			});
			
			var index = oldtriggers.indexOf(id);
			if (index > -1)
			{
				oldtriggers.splice(index, 1);
			}		
			
			$(triggers).append(a);
			dependent = true;
		});
	} else {
		oldtext = "";
		list = $(span).closest("tr").find(".triggers").first() 
		$(list).find("a").each(function(){
			var id = $(this).attr("data-targetid");
			oldtext = oldtext + id + ";";
		});
		
		var parentid = $(_elementProperties.selectedelement).closest(".survey-element").attr("data-id");
		var parent = _elements[parentid];
		parent.useAndLogic(false);
		
		$('.visibilityrow').remove();
	}
	
	for (var i = 0; i < oldtriggers.length; i++)
	{
		//remove from "dependentelements" field
		$("#content").find(".selectedquestion").each(function(){
			var myid = $(this).attr("id");
			selectedquestions = selectedquestions + myid + ";";
			removeVisibility(oldtriggers[i], myid);
		});
	}

	if (!dependent)
	{
		$(triggers).append(getPropertyLabel("alwaysVisible"));
	} else {
		var element = _elements[id];
		
		if ($(_elementProperties.selectedelement).hasClass("matrix-header"))
		{
			var parentid = $(_elementProperties.selectedelement).closest(".survey-element").attr("data-id");
			var parent = _elements[parentid];
			if ($(_elementProperties.selectedelement).closest("tr").hasClass("matrix-question"))
			{
				var pos = $(_elementProperties.selectedelement).closest("tr").index();
				element = parent.questionsOrdered()[pos];
			} else {
				var pos = $(_elementProperties.selectedelement).index();
				element = parent.answers()[pos-1];
			}
		}
		
		if (element.useAndLogic())
		{
			$(triggers).prepend("<b>" + getPropertyLabel("visibleIfTriggeredAnd") + "</b>:<br />");
		} else {
			$(triggers).prepend("<b>" + getPropertyLabel("visibleIfTriggered") + "</b>:<br />");
		}
	}
	
	$(_elementProperties.selectedproperty).find(".propertycontent").find(".triggers").remove();
	$(_elementProperties.selectedproperty).find(".propertycontent").append(triggers);
	if (list != null) $(list).closest("tr").hide();
	
	updateDependenciesView();
	
	if (!noUndo) {
		_undoProcessor.addUndoStep(["Visibility", id, $(_elementProperties.selectedelement).index(), oldtext, values, selectedquestions]);
	}
}

function save(span)
{
	_elementProperties.selectedproperty = $(span).closest(".propertyrow").prevAll(".firstpropertyrow").first();
	$(span).closest(".propertyrow").removeClass("invalidinput");
	
	var label = $(_elementProperties.selectedproperty).find(".propertylabel").first().attr("data-label");
	var id = $(_elementProperties.selectedelement).attr("data-id");
	var element = _elements[id];
	var originalLabel = label;
	
	if ((label == "PossibleAnswers" || label == "Columns" || label == "Rows" || label == "Questions") && $(span).closest(".editvaluesbuttons").length > 0)
	{
		label = "EDITVALUES";
	}

	switch (label) {
		case "Visibility":
			updateVisibility(span, false, true, false, false);
			break;
		case "EDITVALUES":
			var oldvalues = [];
			var values = [];
			var ids = [];
			var text = "";
			$(span).closest("td").find("input").each(function(){
				values[values.length] = $(this).val();
				text = text + $(this).val();
			});
			
			if (!checkCharacters(text))
			{
				$(span).closest("tr").addClass("invalidinput");	
				return;
			}
			
			var elements;
			if (element.type == "Matrix" || element.type == "Table")
			{
				if (originalLabel == "Columns")
				{
					elements = element.answers();
				} else if (originalLabel == "Rows")
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
				oldvalues[oldvalues.length] = elements[i].shortname();
				ids[ids.length] = elements[i].id();
			}
			
			$(span).closest("td").find("input").each(function(){
				updateShortname(this);
			});
			
			$(span).closest("tr").hide();
			
			var oldtext = $(_elementProperties.selectedproperty).attr("data-oldvalues");
			_undoProcessor.addUndoStep(["Shortnames", id, ids, oldvalues, values, originalLabel]);
			break;
		case "PossibleAnswers":
			_elementProperties.selectedid = $(span).closest("tr").find("textarea").first().attr("id");
			var text = tinyMCE.get(_elementProperties.selectedid).getContent();
			if (!updatePossibleAnswers(_elementProperties.selectedelement, text, false))
			{
				return;
			}
			$(_elementProperties.selectedproperty).next().hide();
			
			if (isQuiz)
			{
				var element = _elementProperties.selectedelement;
				_elementProperties.deselectAll();
				if (element != null) _elementProperties.showProperties($(element), null, false);
			}
			
			break;
		case "feedback":
			updateFeedback(span, false);
			break;
		default:		
			_elementProperties.selectedid = $(span).closest("tr").find("textarea").first().attr("id");
			var text = tinyMCE.get(_elementProperties.selectedid).getContent({format: 'xhtml'});
			var doc = new DOMParser().parseFromString(text, 'text/html');
			text = new XMLSerializer().serializeToString(doc);
			
			//var titleMatch = new RegExp("<body>([^]*)<\/body>","im");
			//text = text.match(titleMatch)[1];
			
			/<body>([^]*)<\/body>/im.exec(text);
			text = RegExp.$1;
			
			switch(label) {
				case  "Text":
					if (!updateText(_elementProperties.selectedelement, text, false))
					{
						return;
					}
					break;			
				case "ConfirmationText":
					var oldtext = element.confirmationtext();
					updateConfirmationText(element, text);
					_undoProcessor.addUndoStep(["ConfirmationText", id, $(_elementProperties.selectedelement).index(), oldtext, text]);	
					break;
				case "Help":
					var oldtext = element.help();
					element.help(text);
					element.niceHelp(getNiceHelp(element.help()));
					_undoProcessor.addUndoStep(["Help", id, $(_elementProperties.selectedelement).index(), oldtext, text]);	
					break;
				case "Columns":
					var oldtext = getColumnsText(true);
					var columns = splitText(text);
					
					if (!checkColumns(columns))
					{
						return;
					}
					updateColumns(element, columns)
					_undoProcessor.addUndoStep(["Columns", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
					
					addElementHandler($(_elementProperties.selectedelement));
					update($("tr[data-label='Columns']"));
					break;
				case "Rows":
					var oldtext = getRowsText(true);			
					var rows = splitText(text);

					if (!checkRows(rows))
					{
						return;
					}
					updateRows(element, rows);
					_undoProcessor.addUndoStep(["Rows", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
					
					addElementHandler($(_elementProperties.selectedelement));
					update($("tr[data-label='Rows']"));
					break;
				case "Questions":
					var oldtext = getQuestionsText(true);			
					var questions = splitText(text);
					
					if (!checkQuestions(questions))
					{
						return;
					}		
					
					updateQuestions(element, questions);
		
					_undoProcessor.addUndoStep(["Questions", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
					
					addElementHandler($(_elementProperties.selectedelement));
					break;
			}
			$(_elementProperties.selectedproperty).next().hide();
			var shorttext = getShortnedText(text);
			
			if (label != "Columns" && label != "Rows" && label != "Questions"  && label != "Help")
			$(_elementProperties.selectedproperty).find(".propertycontent").html("<div class='propertytextpreview' onclick='edit(this)'>" + shorttext + "</div>").prepend('<div class="rightaligned"><span class="glyphicon glyphicon-pencil" data-toggle="tooltip" title="' + getPropertyLabel("Edit") + '" onclick="edit(this)"></span></div>');
			
			if (label == "Help")
			$(_elementProperties.selectedproperty).find(".propertycontent").html("<div class='propertytextpreview' onclick='edit(this)'>" + shorttext + "</div>").prepend('<div class="rightaligned"><span class="glyphicon glyphicon-pencil" data-toggle="tooltip" title="' + getPropertyLabel("Edit") + '" onclick="edit(this)"></span><span class="glyphicon glyphicon-trash" data-toggle="tooltip" data-placement="left" title="' + getPropertyLabel("Remove") + '" onclick="resetHelp(this)"></span></div>');
				
			
			break;
	}
	
	$("#properties").find('[data-toggle="tooltip"]').tooltip({
	    trigger : 'hover'
	});
}

function updateColumns(element, columns)
{
	var answers = [];
	for (var i = 0; i < element.answers().length; i++)
	{
		answers.push(element.answers()[i]);
	}

	element.answers.removeAll();
	for (var i = 0; i < columns.length; i++)
	{
		var newelement = null;
		for (var j = 0; j < answers.length; j++)
		{
			if (answers[j].originalTitle() == columns[i]) {
				newelement = answers[j];
				answers.splice(j, 1);
				break;
			}
		}
		
		if (newelement == null) {
			newelement = newMatrixItemViewModel(getNewId(), getNewId(), true, getNewShortname(), false, columns[i], columns[i], false, "", element.answers().length);			
		}		
		
		element.answers.push(newelement);
	}

	updateNavigation($(_elementProperties.selectedelement), element.id());
}

function updateRows(element, rows)
{
	var questions = [];
	var usedQuestions = [];
	var allmandatory = true;
	for (var i = 0; i < element.questions().length; i++)
	{
		questions.push(element.questions()[i]);
		if (element.questions()[i].optional()) allmandatory = false;
	}

	element.questions.removeAll();
	for (var i = 0; i < rows.length; i++)
	{
		var newelement = null;
		for (var j = 0; j < questions.length; j++)
		{
			if (questions[j].originalTitle() == rows[i]) {
				newelement = questions[j];
				questions.splice(j, 1);
				break;
			}
		}
		
		if (newelement == null) {
			newelement = newMatrixItemViewModel(getNewId(), getNewId(), !allmandatory, getNewShortname(), false, rows[i], rows[i], false, "", element.questions().length);
		}
		
		
		element.questions.push(newelement);
	}
	
	updateDependenciesView();
	
	updateNavigation($(_elementProperties.selectedelement), element.id());
}

function updateQuestions(element, newquestions)
{
	var questions = [];
	for (var i = 0; i < element.childElements().length; i++)
	{
		questions[element.childElements()[i].originalTitle()] = element.childElements()[i];
	}

	element.childElements.removeAll();
	for (var i = 0; i < newquestions.length; i++)
	{
		if (questions.hasOwnProperty(newquestions[i]))
		{
			element.childElements.push(questions[newquestions[i]]);
		} else {
			var newelement = newBasicViewModel(getBasicElement("Text", false, newquestions[i], null, false));
			element.childElements.push(newelement);
		}
	}
	
	updateNavigation($(_elementProperties.selectedelement), element.id());
}

function setHoverOnVisibilityLink(a)
{
	var id = $(a).attr("data-targetid");
	if (id.indexOf("|") > 0)
	{
		$(a).hover(
		  function() {
			 $("input[data-cellid='" + id + "']").parent().addClass("highlighted");
			 $("a[data-cellid='" + id + "']").addClass("highlighted");
		  }, function() {
			 $("input[data-cellid='" + id + "']").parent().removeClass("highlighted");
			 $("a[data-cellid='" + id + "']").removeClass("highlighted");
		  }
		);
	} else {			
		$(a).hover(
		  function() {
			$("label[for='" + id + "']").parent().addClass("highlighted");
			$("#navanswer" + id).addClass("highlighted");
		  }, function() {
			$("label[for='" + id + "']").parent().removeClass("highlighted");
			$("#navanswer" + id).removeClass("highlighted");
		  }
		);
	}
}

function updateIdentifier(element, id, text, noundo)
{
	if ($(_elementProperties.selectedelement).hasClass("ratingquestion"))
	{
		var parentid = $(_elementProperties.selectedelement).closest(".survey-element").attr("data-id");
		var parent = _elements[parentid];
		element = parent.getChild(id);	
	} else if ($(_elementProperties.selectedelement).hasClass("answertext"))
	{
		var parentid = $(_elementProperties.selectedelement).closest(".survey-element").attr("data-id");
		var parent = _elements[parentid];
		element = parent.getChild(id);		
	} else if ($(_elementProperties.selectedelement).hasClass("matrix-header"))
	{
		var parentid = $(_elementProperties.selectedelement).closest(".survey-element").attr("data-id");
		var parent = _elements[parentid];
		if ($(_elementProperties.selectedelement).closest("tr").hasClass("matrix-question"))
		{
			var pos = $(_elementProperties.selectedelement).closest("tr").index();
			element = parent.questionsOrdered()[pos-1];
		} else {
			var pos = $(_elementProperties.selectedelement).index();
			element = parent.answers()[pos-1];
		}
	} else if ($(_elementProperties.selectedelement).hasClass("table-header"))
	{
		var parentid = $(_elementProperties.selectedelement).closest(".survey-element").attr("data-id");
		var parent = _elements[parentid];
		if ($(_elementProperties.selectedelement).closest("tr").index() > 0)
		{
			var pos = $(_elementProperties.selectedelement).closest("tr").index();
			element = parent.questions()[pos-1];
		} else {
			var pos = $(_elementProperties.selectedelement).index();
			element = parent.answers()[pos-1];
		}
	} else if ($(_elementProperties.selectedelement).hasClass("rankingitemtext"))
	{
		var parentid = $(_elementProperties.selectedelement).closest(".survey-element").attr("data-id");
		var parent = _elements[parentid];
		if (parent.type == "RankingQuestion") {
			element = parent.rankingItems().filter((item) => item.id() == id)[0];
		} else {
			throw "could not find matching parent element";
		}
	}
	
	var oldtext = element.shortname(); 
	element.shortname(text);
	
	if (!noundo && oldtext != text)
	_undoProcessor.addUndoStep(["Identifier", id, $(_elementProperties.selectedelement).index(), oldtext, text]);
}

function updateLogic(radio) {
	var id = $(_elementProperties.selectedelement).closest(".survey-element").attr("data-id");
	var element = _elements[id];
		
	if ($(_elementProperties.selectedelement).hasClass("matrix-header"))
	{
		var parentid = $(_elementProperties.selectedelement).closest(".survey-element").attr("data-id");
		var parent = _elements[parentid];
		if ($(_elementProperties.selectedelement).closest("tr").hasClass("matrix-question"))
		{
			var pos = $(_elementProperties.selectedelement).closest("tr").index();
			element = parent.questionsOrdered()[pos];
		} else {
			var pos = $(_elementProperties.selectedelement).index();
			element = parent.answers()[pos-1];
		}
	}	
	
	var oldValue = element.useAndLogic();
	var newValue = $(radio).val() == "true";
	
	if (oldValue != newValue) {
		element.useAndLogic(newValue);		
		_undoProcessor.addUndoStep(["UseAndLogic", id, $(_elementProperties.selectedelement).index(), oldValue, newValue]);
	}
}
