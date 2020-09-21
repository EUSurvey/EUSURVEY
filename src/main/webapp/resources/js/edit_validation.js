function removeValidationMarkup(row)
{
	if (row == null) row = $(_elementProperties.selectedproperty);
	row.find(".validationinfobutton").tooltip('hide');
	row.find(".validationinfobutton").remove();
	row.closest(".invalidinput").removeClass("invalidinput");
	row.find(".validationhintbutton").tooltip('hide');
	row.find(".validationhintbutton").remove();
	row.closest("inputhint").removeClass("inputhint");
	row.closest(".firstpropertyrow").find(".validationinfobutton").remove();
	row.closest(".firstpropertyrow").find(".validationinfobutton").remove();
}

function checkMinMaxDate(input, hasInputError, showrulehint)
{
	removeValidationMarkup();
	var minstring = null;
	var maxstring = null;
	if ($(input).attr("data-to"))
	{
		minstring = $(input).val();
		maxstring = $("#" + $(input).attr("data-to")).val();
		if (minstring.length > 0)
		{
			var min = parseDate2(minstring);
			
			if (min == null || isNaN(min))
			{
				addValidationInfo(input, "mindateinvalid");
				return false;
			}
			
			if (maxstring.length > 0)
			{
				var max = parseDate2(maxstring);
				
				if (max == null || isNaN(max))
				{
					addValidationInfo(input, "maxdateinvalid");
					return false;
				}
				
				if (min >= max)
				{
					addValidationInfo(input, "invalidMinMaxDate");
					return false;
				}
			}
		}	
	} else if ($(input).attr("data-from"))
	{
		maxstring = $(input).val();
		minstring = $("#" + $(input).attr("data-from")).val();
		if (maxstring.length > 0)
		{
			var max = parseDate2(maxstring);
			
			if (max == null || isNaN(max))
			{
				addValidationInfo(input, "maxdateinvalid");
				return false;
			}
						
			if (minstring.length > 0)
			{
				var min = parseDate2(minstring);
				
				if (min == null || isNaN(min))
				{
					addValidationInfo(input, "mindateinvalid");
					return false;
				}
				
				if (min >= max)
				{
					addValidationInfo(input, "invalidMinMaxDate");
					return false;
				}
			}
		}	
	}
	
	if (hasInputError)
	{
		if ($(input).attr("data-from"))
		{
			update($("#" + $(input).attr("data-from")));
		} else if ($(input).attr("data-to"))
		{
			update($("#" + $(input).attr("data-to")));
		}
	}
	
	if (showrulehint)
	{
		addValidationHint(input, "checkRules");
	}
	
	return true;
}

function checkMinMaxTime(input, hasInputError, showrulehint)
{
	removeValidationMarkup();
	var minstring = null;
	var maxstring = null;
	if ($(input).attr("data-to"))
	{
		minstring = $(input).val();
		maxstring = $("#" + $(input).attr("data-to")).val();
		if (minstring.length > 0)
		{
			var isValid = isValidTime(minstring);
			
			if (!isValid)
			{
				addValidationInfo(input, "mintimeinvalid");
				return false;
			}
			
			if (maxstring.length > 0)
			{
				isValid = isValidTime(maxstring);
				
				if (!isValid)
				{
					addValidationInfo(input, "maxtimeinvalid");
					return false;
				}
				
				if (minstring >= maxstring)
				{
					addValidationInfo(input, "invalidMinMaxTime");
					return false;
				}
			}
		}	
	} else if ($(input).attr("data-from"))
	{
		maxstring = $(input).val();
		minstring = $("#" + $(input).attr("data-from")).val();
		if (maxstring.length > 0)
		{
			var isValid = isValidTime(maxstring);
			
			if (!isValid)
			{
				addValidationInfo(input, "maxtimeinvalid");
				return false;
			}
						
			if (minstring.length > 0)
			{
				isValid = isValidTime(minstring);
				
				if (!isValid)
				{
					addValidationInfo(input, "mintimeinvalid");
					return false;
				}
				
				if (minstring >= maxstring)
				{
					addValidationInfo(input, "invalidMinMaxTime");
					return false;
				}
			}
		}	
	}
	
	if (hasInputError)
	{
		if ($(input).attr("data-from"))
		{
			update($("#" + $(input).attr("data-from")));
		} else if ($(input).attr("data-to"))
		{
			update($("#" + $(input).attr("data-to")));
		}
	}
	
	if (showrulehint)
	{
		addValidationHint(input, "checkRules");
	}
	
	return true;
}

function addValidationMessage(row, label)
{
	$(row).find(".validationinfobutton").remove();	
	$(row).addClass("invalidinput");
	var b = document.createElement("a");
	$(b).addClass("validationinfobutton").attr("data-toggle","tooltip").attr("data-placement","right").attr("title", label).html('<span class="glyphicon glyphicon-question-sign"></span>');
	$(row).find(".propertylabel").first().append(b);	
	$(b).tooltip({
	    trigger : 'hover'
	});
}

function checkURL(url)
{
	removeValidationMarkup();
	var tr = $(_elementProperties.selectedproperty).closest("tr");
	$(tr).removeClass("invalidinput");	
	if (url.length > 0 && !validateUrl(url))
	{
		$(tr).addClass("invalidinput");					
		var label = getPropertyLabel("invalidURL");					
		var b = document.createElement("a");
		$(b).addClass("validationinfobutton").attr("data-toggle","tooltip").attr("data-placement","right").attr("title", label).html('<span class="glyphicon glyphicon-question-sign"></span>');
		$(_elementProperties.selectedproperty).find(".propertylabel").first().append(b);	
		$(b).tooltip({
		    trigger : 'hover'
		});
		return false;
	}
	return true;
}

function checkText(text)
{
	var tr = $(_elementProperties.selectedproperty).closest("tr");
	removeValidationMarkup();
	if (strip_tags(text).trim().length == 0)
	{
		$(tr).addClass("invalidinput");					
		var label = getPropertyLabel("required");					
		var b = document.createElement("a");
		$(b).addClass("validationinfobutton").attr("data-toggle","tooltip").attr("data-placement","right").attr("title", label).html('<span class="glyphicon glyphicon-question-sign"></span>');
		$(_elementProperties.selectedproperty).find(".propertylabel").first().append(b);	
		$(b).tooltip({
		    trigger : 'hover'
		});
		return false;
	}
	return true;
}

function checkXHTML(text)
{
	var tr = $(_elementProperties.selectedproperty).closest("tr");
	removeValidationMarkup();
	if (!checkXHTMLValidity(text))
	{	
		$(tr).addClass("invalidinput");					
		var label = getPropertyLabel("xhtmlinvalid");					
		var b = document.createElement("a");
		$(b).addClass("validationinfobutton").attr("data-toggle","tooltip").attr("data-placement","right").attr("title", label).html('<span class="glyphicon glyphicon-question-sign"></span>');
		$(_elementProperties.selectedproperty).find(".propertylabel").first().append(b);	
		$(b).tooltip({
		    trigger : 'hover'
		});
		return false;
	}
	return true;	
}

function checkCharacters(text)
{
	var tr = $(_elementProperties.selectedproperty).closest("tr");
	removeValidationMarkup();
	if (text.indexOf('&') > -1 || text.indexOf('<') > -1 || text.indexOf('>') > -1)
	{
		$(tr).addClass("invalidinput");					
		var label = getPropertyLabel("NameInvalid");					
		var b = document.createElement("a");
		$(b).addClass("validationinfobutton").attr("data-toggle","tooltip").attr("data-placement","right").attr("title", label).html('<span class="glyphicon glyphicon-question-sign"></span>');
		$(_elementProperties.selectedproperty).find(".propertylabel").first().append(b);	
		$(b).tooltip({
		    trigger : 'hover'
		});
		return false;
	}
	return true;
}

function checkLessThan255Characters(text, span)
{
	var tr = $(_elementProperties.selectedproperty).closest("tr");
	removeValidationMarkup();
	
	if (text.length > 254)
	{		
		$(span).closest("tr").addClass("invalidinput");	
		
		$(tr).addClass("invalidinput");					
		var label = getPropertyLabel("max255Characters");					
		var b = document.createElement("a");
		$(b).addClass("validationinfobutton").attr("data-toggle","tooltip").attr("data-placement","right").attr("title", label).html('<span class="glyphicon glyphicon-question-sign"></span>');
		$(_elementProperties.selectedproperty).find(".propertylabel").first().append(b);	
		$(b).tooltip({
		    trigger : 'hover'
		});
		return false;		
	}
	
	return true;
}

function checkMandatoryMatrix(input, numquestions, max)
{
	if (max > 0 && max < numquestions)
	{
		addValidationInfo(input, "maxinvalidmatrix");
		return false;
	}
	return true;
}

function checkMandatory(text)
{
	var tr = $(_elementProperties.selectedproperty).closest("tr");
	removeValidationMarkup();
	if (text.trim().length == 0)
	{
		$(tr).addClass("invalidinput");					
		var label = getPropertyLabel("required");					
		var b = document.createElement("a");
		$(b).addClass("validationinfobutton").attr("data-toggle","tooltip").attr("data-placement","right").attr("title", label).html('<span class="glyphicon glyphicon-question-sign"></span>');
		$(_elementProperties.selectedproperty).find(".propertylabel").first().append(b);	
		$(b).tooltip({
		    trigger : 'hover'
		});
		return false;
	}
	return true;
}

function checkColumns(columns)
{
	var tr = $(_elementProperties.selectedproperty).closest("tr");
	removeValidationMarkup();
	if (columns.length < 1)
	{
		$(tr).addClass("invalidinput");					
		var label = getPropertyLabel("TableAnswers1");					
		var b = document.createElement("a");
		$(b).addClass("validationinfobutton").attr("data-toggle","tooltip").attr("data-placement","right").attr("title", label).html('<span class="glyphicon glyphicon-question-sign"></span>');
		$(_elementProperties.selectedproperty).find(".propertylabel").first().append(b);	
		$(b).tooltip({
		    trigger : 'hover'
		});
		return false;
	}
	return true;
}

function checkRows(rows)
{
	var tr = $(_elementProperties.selectedproperty).closest("tr");
	removeValidationMarkup();
	if (rows.length < 1)
	{
		$(tr).addClass("invalidinput");					
		var label = getPropertyLabel("TableQuestions1");					
		var b = document.createElement("a");
		$(b).addClass("validationinfobutton").attr("data-toggle","tooltip").attr("data-placement","right").attr("title", label).html('<span class="glyphicon glyphicon-question-sign"></span>');
		$(_elementProperties.selectedproperty).find(".propertylabel").first().append(b);	
		$(b).tooltip({
		    trigger : 'hover'
		});
		return false;
	}
	
	var id = $(_elementProperties.selectedelement).attr("data-id");
	var element = _elements[id];
	if (element.type != "Table" && element.minRows() != null && element.minRows() > rows.length)
	{
		addValidationInfo($("#btnRemoveRows"), "checkNumberOfRows");
		return false;
	}
	
	return true;
}

function checkQuestions(questions)
{
	var tr = $(_elementProperties.selectedproperty).closest("tr");
	removeValidationMarkup();
	if (questions.length < 1)
	{
		$(tr).addClass("invalidinput");					
		var label = getPropertyLabel("RatingQuestions");					
		var b = document.createElement("a");
		$(b).addClass("validationinfobutton").attr("data-toggle","tooltip").attr("data-placement","right").attr("title", label).html('<span class="glyphicon glyphicon-question-sign"></span>');
		$(_elementProperties.selectedproperty).find(".propertylabel").first().append(b);	
		$(b).tooltip({
		    trigger : 'hover'
		});
		return false;
	}
	return true;
}


function checkGalleryUploadedFile(file, table)
{
	removeValidationMarkup();
	
	var found = false;
	$(table).find("tr").each(function(){
		var filename = $(this).find("td").first().html();
		if (filename == file)
		{
			found = true;
			return;
		}
	});
	
	if (found)
	{
		$(_elementProperties.selectedproperty).closest("tr").addClass("invalidinput");					
		var label = getPropertyLabel("FileNameAlreadyExists");					
		var b = document.createElement("a");
		$(b).addClass("validationinfobutton").attr("data-toggle","tooltip").attr("data-placement","right").attr("title", label).html('<span class="glyphicon glyphicon-question-sign"></span>');
		$(_elementProperties.selectedproperty).find(".propertylabel").first().append(b);	
		$(b).tooltip({
		    trigger : 'hover'
		});
		return false;
	}
	return true;
}

function checkGallerySelections(strnum)
{
	var tr = $(_elementProperties.selectedproperty).closest("tr");
	removeValidationMarkup();
	var num = parseInt(strnum);
	var images = $(_elementProperties.selectedelement).find("input[name^=longdesc]").length;
	if (num > images)
	{
		$(tr).addClass("invalidinput");					
		var label = getPropertyLabel("GallerySelections");					
		var b = document.createElement("a");
		$(b).addClass("validationinfobutton").attr("data-toggle","tooltip").attr("data-placement","right").attr("title", label).html('<span class="glyphicon glyphicon-question-sign"></span>');
		$(_elementProperties.selectedproperty).find(".propertylabel").first().append(b);	
		$(b).tooltip({
		    trigger : 'hover'
		});
		return false;
	}
	return true;
}

function checkInteger(input)
{
	var tr = $(_elementProperties.selectedproperty).closest("tr");
	removeValidationMarkup();
	
	var v = $(input).val();
	if (v.trim().length == 0)
	{
		$(tr).addClass("invalidinput");					
		var label = getPropertyLabel("required");					
		var b = document.createElement("a");
		$(b).addClass("validationinfobutton").attr("data-toggle","tooltip").attr("data-placement","right").attr("title", label).html('<span class="glyphicon glyphicon-question-sign"></span>');
		$(_elementProperties.selectedproperty).find(".propertylabel").first().append(b);	
		$(b).tooltip({
		    trigger : 'hover'
		});
		return false;
	}
	
	var i = parseInt(v);
	
	if (i == null || isNaN(i) || i < 0)
	{
		$(tr).addClass("invalidinput");
		addValidationInfo(input, "numberinvalid");
		return false;
	}
	
	return true;
}

function countDecimals(value) {
    if (Math.floor(value) !== value)
        return value.toString().split(".")[1].length || 0;
    return 0;
}

function checkNumber(input, decimalPlaces)
{
	var tr = $(_elementProperties.selectedproperty).closest("tr");
	removeValidationMarkup();
	
	var v = $(input).val();
	if (v.trim().length == 0)
	{
		return true;
	}
	
	var i = parseFloat(v);
	
	if (i == null || isNaN(i))
	{
		$(tr).addClass("invalidinput");
		addValidationInfo(input, "numberinvalid");
		return false;
	}
	
	var decimals = countDecimals(i);
	if (decimalPlaces != null && decimalPlaces > 0 && decimalPlaces < decimals)
	{
		$(tr).addClass("invalidinput");
		addValidationInfo(input, "numberinvaliddecimals");
		return false;
	}
	
	return true;
}

function checkPossibleAnswer(answer)
{
	var result = true;
	var tr = $(_elementProperties.selectedproperty).closest("tr");
	removeValidationMarkup();
	$(_elementProperties.selectedelement).closest(".survey-element").find("textarea[name^='answer']").each(function(){
		var text = $(this).text();
		if (text == answer)
		{
			$(tr).addClass("invalidinput");					
			var label = getPropertyLabel("NotUniqueAnswers");					
			var b = document.createElement("a");
			$(b).addClass("validationinfobutton").attr("data-toggle","tooltip").attr("data-placement","right").attr("title", label).html('<span class="glyphicon glyphicon-question-sign"></span>');
			$(_elementProperties.selectedproperty).find(".propertylabel").first().append(b);	
			$(b).tooltip({
			    trigger : 'hover'
			});
			result = false;
			return;
		}
	});
	return result;
}

function checkPossibleAnswers(lines)
{
	removeValidationMarkup();
	
	var count = 0;
	
	for (var i = 0; i < lines.length; i++)
	{
		if (lines[i].trim().length > 0)
		{			
			for (var j = 0; j < i; j++)
			{
				if (lines[j] == lines[i])
				{
					$(_elementProperties.selectedproperty).closest("tr").addClass("invalidinput");					
					var label = getPropertyLabel("NotUniqueAnswers");					
					var b = document.createElement("a");
					$(b).addClass("validationinfobutton").attr("data-toggle","tooltip").attr("data-placement","right").attr("title", label).html('<span class="glyphicon glyphicon-question-sign"></span>');
					$(_elementProperties.selectedproperty).find(".propertylabel").first().append(b);	
					$(b).tooltip({
					    trigger : 'hover'
					});
					return false;
				}
			}
			count++;
		}
	}
	
	if (count < 2)
	{
		$(_elementProperties.selectedproperty).closest("tr").addClass("invalidinput");					
		var label = getPropertyLabel("TableAnswers");					
		var b = document.createElement("a");
		$(b).addClass("validationinfobutton").attr("data-toggle","tooltip").attr("data-placement","right").attr("title", label).html('<span class="glyphicon glyphicon-question-sign"></span>');
		$(_elementProperties.selectedproperty).find(".propertylabel").first().append(b);	
		$(b).tooltip({
		    trigger : 'hover'
		});
		return false;
	}
	
	var id = $(_elementProperties.selectedelement).attr("data-id");
	var element = _elements[id];
	if (element.minChoices() != null && element.minChoices() > count)
	{
		addValidationInfo($("#btnRemovePossibleAnswers"), "checkNumberOfChoices");
		return;
	}
	
	return true;
}

function checkNumColumns(input, element)
{
	removeValidationMarkup();
	if (element.hasEmptyLastColumn())
	{
		addValidationHint(input, "emptylastcolumn");
	}
}

function addValidationHint(input, type)
{
	var label = getPropertyLabel(type);
	$(input).closest(".firstpropertyrow").addClass("inputhint");
	$(input).closest(".firstpropertyrow").addClass("hint");
	$(input).closest(".firstpropertyrow").find(".validationhintbutton").remove();
	var b = document.createElement("a");
	$(b).addClass("validationhintbutton").attr("data-placement","right").attr("title", label).html('<span class="glyphicon glyphicon-info-sign"></span>');
	$(b).tooltip({
	    trigger : 'hover'
	});	
	$(input).closest(".firstpropertyrow").find(".propertylabel").first().append(b);	
}

function addValidationInfo(input, type)
{
	var label = "unknown";
	if (type == "mininvalid" || type == "maxinvalid" || type == "numberinvalid")
	{
		label = getPropertyLabel("invalidNumber");
	}  else if (type == "mininvalid5k" || type == "maxinvalid5k") {
		label = getPropertyLabel("invalidNumber5k");
	} else if (type == "mindateinvalid" || type == "maxdateinvalid") {
		label = getPropertyLabel("invalidDate");
	} else if (type == "mintimeinvalid" || type == "maxtimeinvalid") {
		label = getPropertyLabel("invalidTime");
	} else if (type == "invalidMinMax") {
		if ($(_elementProperties.selectedelement).hasClass("freetextitem"))
		{
			label = getPropertyLabel("invalidMinMaxCharacters");
		} else if ($(_elementProperties.selectedelement).hasClass("multiplechoiceitem")) {
			label = getPropertyLabel("invalidMinMaxChoice");
		} else if ($(_elementProperties.selectedelement).hasClass("matrixitem")) {
			label = getPropertyLabel("invalidMatrixRows2");
		} else {
			label = getPropertyLabel("invalidMinMaxNumber");
		}
	} else if (type == "invalidMinMaxDate") {
		label = getPropertyLabel("invalidStartEnd");
	} else if (type == "invalidMinMaxTime") {
		label = getPropertyLabel("invalidStartEndTime");
	} else if (type == "maxinvalidmatrix") {
		label = getPropertyLabel("invalidMatrixRows");
	} else {
		label = getPropertyLabel(type);
	}
	
	var tr = $(input).closest(".firstpropertyrow");
	if ($(tr).length == 0)
	{
		tr = $(input).closest("tr");
	}
	
	$(tr).addClass("invalidinput");
	$(tr).find(".validationinfobutton").remove();
	var b = document.createElement("a");
	$(b).addClass("validationinfobutton").attr("data-placement","right").attr("title", label).html('<span class="glyphicon glyphicon-question-sign"></span>');
	$(b).tooltip({
	    trigger : 'hover'
	});	
	$(tr).find(".propertylabel").first().append(b);	
}

function checkValidExtensions(input)
{
	if ($(input).val().length == 0) return true;
	
	if ($(input).val().trim().indexOf(" ") > 0)
	{
		addValidationInfo(input, "noblanks");
		return false;
	}
	
	return true;
}

function checkValidNumber(input)
{
	removeValidationMarkup();
	
	if ($(input).val().length == 0) return true;
	
	var v = parseInt($(input).val());
	
	if (v == null || isNaN(v) || !isFinite($(input).val()) || v > 100000000)
	{
		addValidationInfo(input, "mininvalid");
		return false;
	}
	
	return true;
}

function checkPositive(input)
{
	if (!checkValidNumber(input)) return false;
	
	var v = parseInt($(input).val());
	
	if (v < 0)
	{
		addValidationInfo(input, "invalidPositiveNumber");
		return false;
	}
	
	return true;
}

function checkUniqueAttributeName(input)
{
	var text = $(input).val();
	removeValidationMarkup();
	var result = true;
	if (strip_tags(text).trim().length > 0)
	{
		$("input[name^='nameattribute']").each(function(){
			var name = $(this).val();
			if (name == text) {
				addValidationInfo(input, "duplicateattributename");
				result = false;
				return;
			} else if (name.toLowerCase() == text.toLowerCase()) {
				if (name.toLowerCase() == "name" || name.toLowerCase() == "email") {
					addValidationInfo(input, "duplicateattributename");
					result = false;
					return;
				}
			}
		})
	}
	
	return result;
}

function checkMinMax(input, hasInputError, globalmax, showrulehint)
{
	removeValidationMarkup();
	
	var minstring = null;
	var maxstring = null;
	if ($(input).attr("data-to"))
	{
		minstring = $(input).val();
		maxstring = $("#" + $(input).attr("data-to")).val();
		if (minstring.length > 0)
		{
			var min = parseInt(minstring);
			
			if (min == null || isNaN(min) || !isFinite(minstring))
			{
				addValidationInfo(input, "mininvalid");
				return false;
			}
			
			if ($(_elementProperties.selectedelement).hasClass("freetextitem") && min > 5000)
			{
				addValidationInfo(input, "mininvalid5k");
				return false;
			}
			
			if ($(_elementProperties.selectedelement).hasClass("multiplechoiceitem") && min < 0)
			{
				addValidationInfo(input, "mininvalid");
				return false;
			}
			
			if (globalmax != null && min > globalmax)
			{
				addValidationInfo(input, "globalmininvalid");
				return false;
			}
			
			if (maxstring.length > 0)
			{
				var max = parseInt(maxstring);
				
				if (max == null || isNaN(max) || !isFinite(maxstring))
				{
					addValidationInfo(input, "maxinvalid");
					return false;
				}
				
				if ($(_elementProperties.selectedelement).hasClass("multiplechoiceitem") && max < 0)
				{
					addValidationInfo(input, "mininvalid");
					return false;
				}
				
				if (min > max)
				{
					addValidationInfo(input, "invalidMinMax");
					return false;
				}
				
				if (min == max)
				{
					addValidationInfo(input, "invalidMinMaxEqual");
					return false;
				}
				
			}
		}	
	} else if ($(input).attr("data-from"))
	{
		maxstring = $(input).val();
		minstring = $("#" + $(input).attr("data-from")).val();
		if (maxstring.length > 0) // && maxstring != "0")
		{
			var max = parseInt(maxstring);
			
			if (max == null || isNaN(max) || !isFinite(maxstring))
			{
				addValidationInfo(input, "maxinvalid");
				return false;
			}
			
			if ($(_elementProperties.selectedelement).hasClass("freetextitem") && max > 5000)
			{
				addValidationInfo(input, "maxinvalid5k");
				return false;
			}
			
			if ($(_elementProperties.selectedelement).hasClass("multiplechoiceitem") && max < 0)
			{
				addValidationInfo(input, "mininvalid");
				return false;
			}
			
			if ($(_elementProperties.selectedelement).hasClass("matrixitem"))
			{
				var nummandatoryrows = $(_elementProperties.selectedelement).find(".matrixtable").first().find("input[name^='optional'][value='false']").length;
				if (max < nummandatoryrows)
				{
					addValidationInfo(input, "maxinvalidmatrix");
					return false;
				}
			}
						
			if (minstring.length > 0)
			{
				var min = parseInt(minstring);
				
				if (min == null || isNaN(min) || !isFinite(minstring))
				{
					addValidationInfo(input, "mininvalid");
					return false;
				}
				
				if ($(_elementProperties.selectedelement).hasClass("multiplechoiceitem") && min < 0)
				{
					addValidationInfo(input, "mininvalid");
					return false;
				}
				
				if (min > max)
				{
					addValidationInfo(input, "invalidMinMax");
					return false;
				}
				
				if (min == max)
				{
					addValidationInfo(input, "invalidMinMaxEqual");
					return false;
				}
			}
			
			if (globalmax != null && max > globalmax)
			{
				addValidationInfo(input, "globalmaxinvalid");
				return false;
			}
		}	
	}
	
	if (hasInputError)
	{
		$(input).closest("tr").find(".validationinfobutton").remove();
		
		if ($(input).attr("data-from"))
		{
			update($("#" + $(input).attr("data-from")));
		} else if ($(input).attr("data-to"))
		{
			update($("#" + $(input).attr("data-to")));
		}
	}
	
	if (showrulehint)
	{
		addValidationHint(input, "checkRules");
		
		$(".quiz.invalidinput").each(function(){
			$(this).find("input[data-label=ruleValue]").each(function(){
				update(this);
			});
		});
	}
	
	return true;
}

function checkMinMaxNumberRule(input, hasInputError, globalminstr, globalmaxstr)
{
	removeValidationMarkup();
	
	var minstring = null;
	var maxstring = null;
	
	var globalmin = globalminstr != null ? parseFloat(globalminstr) : null; 
	var globalmax = globalmaxstr != null ? parseFloat(globalmaxstr) : null;
	
	if ($(input).attr("data-label") == "ruleValue")
	{
		minstring = $(input).val();
		maxstring = $(input).parent().find("[data-label='ruleValue2']").val();
		if (minstring.length > 0)
		{
			var min = parseFloat(minstring);
			
			if (min == null || isNaN(min) || !isFinite(minstring))
			{
				addValidationInfo(input, "mininvalid");
				return false;
			}
						
			if (maxstring.length > 0)
			{
				var max = parseFloat(maxstring);
				
				if (max == null || isNaN(max) || !isFinite(maxstring))
				{
					addValidationInfo(input, "maxinvalid");
					return false;
				}
				
				if (min > max)
				{
					addValidationInfo(input, "invalidMinMax");
					return false;
				}				
			}
			
			if (globalmin != null && globalmin > min)
			{
				addValidationInfo(input, "invalidrulelimit");
				return false;
			}
			
			if (globalmax != null && globalmax < min)
			{
				addValidationInfo(input, "invalidrulelimit");
				return false;
			}
			
		}	
	} else if ($(input).attr("data-label") == "ruleValue2")
	{
		maxstring = $(input).val();
		minstring = $(input).parent().find("[data-label='ruleValue']").val();
		if (maxstring.length > 0 && maxstring != "0")
		{
			var max = parseFloat(maxstring);
			
			if (max == null || isNaN(max) || !isFinite(maxstring))
			{
				addValidationInfo(input, "maxinvalid");
				return false;
			}
								
			if (minstring.length > 0)
			{
				var min = parseFloat(minstring);
				
				if (min == null || isNaN(min) || !isFinite(minstring))
				{
					addValidationInfo(input, "mininvalid");
					return false;
				}
				
				if (min > max)
				{
					addValidationInfo(input, "invalidMinMax");
					return false;
				}
			}
			
			if (globalmin != null && globalmin > max)
			{
				addValidationInfo(input, "invalidrulelimit");
				return false;
			}
			
			if (globalmax != null && globalmax < max)
			{
				addValidationInfo(input, "invalidrulelimit");
				return false;
			}
		}	
	}
	
	if (hasInputError)
	{
		$(input).closest("tr").find(".validationinfobutton").remove();
		
		if ($(input).attr("data-from"))
		{
			update($("#" + $(input).attr("data-from")));
		} else if ($(input).attr("data-to"))
		{
			update($("#" + $(input).attr("data-to")));
		}
	}
	return true;
}

function checkMinMaxDateRule(input, hasInputError, globalminstr, globalmaxstr)
{
	removeValidationMarkup();
	var minstring = null;
	var maxstring = null;
	
	var globalmin = globalminstr != null ? parseDate2(globalminstr) : null; 
	var globalmax = globalmaxstr != null ? parseDate2(globalmaxstr) : null;
	
	if ($(input).attr("data-label") == "ruleValue")
	{
		minstring = $(input).val();
		maxstring = $(input).parent().find("[data-label='ruleValue2']").val();
		if (minstring.length > 0)
		{
			var min = parseDate2(minstring);
			
			if (min == null || isNaN(min))
			{
				addValidationInfo(input, "mindateinvalid");
				return false;
			}
			
			if (maxstring.length > 0)
			{
				var max = parseDate2(maxstring);
				
				if (max == null || isNaN(max))
				{
					addValidationInfo(input, "maxdateinvalid");
					return false;
				}
				
				if (min >= max)
				{
					addValidationInfo(input, "invalidMinMaxDate");
					return false;
				}
			}
			
			if (globalmin != null && globalmin > min)
			{
				addValidationInfo(input, "invalidrulelimit");
				return false;
			}
			
			if (globalmax != null && globalmax < min)
			{
				addValidationInfo(input, "invalidrulelimit");
				return false;
			}
		}	
	} else if ($(input).attr("data-label") == "ruleValue2")
	{
		maxstring = $(input).val();
		minstring = $(input).parent().find("[data-label='ruleValue']").val();
		if (maxstring.length > 0)
		{
			var max = parseDate2(maxstring);
			
			if (max == null || isNaN(max))
			{
				addValidationInfo(input, "maxdateinvalid");
				return false;
			}
						
			if (minstring.length > 0)
			{
				var min = parseDate2(minstring);
				
				if (min == null || isNaN(min))
				{
					addValidationInfo(input, "mindateinvalid");
					return false;
				}
				
				if (min >= max)
				{
					addValidationInfo(input, "invalidMinMaxDate");
					return false;
				}
			}
			
			if (globalmin != null && globalmin > max)
			{
				addValidationInfo(input, "invalidrulelimit");
				return false;
			}
			
			if (globalmax != null && globalmax < max)
			{
				addValidationInfo(input, "invalidrulelimit");
				return false;
			}
		}	
	}
	
	if (hasInputError)
	{
		if ($(input).attr("data-from"))
		{
			update($("#" + $(input).attr("data-from")));
		} else if ($(input).attr("data-to"))
		{
			update($("#" + $(input).attr("data-to")));
		}
	}
	return true;
}

function checkMinMaxRule(input, hasInputError, globalminstr, globalmaxstr)
{
	removeValidationMarkup();
	var minstring = null;
	var maxstring = null;
	
	var globalmin = globalminstr != null && globalminstr != '0' ? parseInt(globalminstr) : null; 
	var globalmax = globalmaxstr != null && globalmaxstr != '0' ? parseInt(globalmaxstr) : null;
	
	if ($(input).attr("data-label") == "ruleValue")
	{
		minstring = $(input).val();
		maxstring = $(input).parent().find("[data-label='ruleValue2']").val();
		if (minstring.length > 0)
		{
			var len = minstring.length;
			
			if (globalmin != null && globalmin > len)
			{
				addValidationInfo(input, "invalidrulelimit");
				return false;
			}
			
			if (globalmax != null && globalmax < len)
			{
				addValidationInfo(input, "invalidrulelimit");
				return false;
			}
		}	
	}
	
	return true;
}

function checkUniqueIdentifier(input)
{
	var id = $(input).val();
	var result = true;
	
	$("li.survey-element:not(.selectedquestion)").each(function(){
		var qid = $(this).find("input[name^='shortname']").first().val();
		if (qid == id)
		{
			addValidationInfo(input, "identifierExists");
			result = false;
			return false;
		}
	})

	return result;
}
