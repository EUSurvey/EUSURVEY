var ContentItem = function()
{
	this.Label = ko.observable("");
	this.Id = ko.observable("");
	this.Name = ko.observable("");
	this.Value = ko.observable("");
	this.NumValue = ko.observable(0);
	this.Selected = ko.observable(false);
};

var PropertyRow = function()
{
	var self = this;
	
	this.Type = ko.observable("");
	this.Label = ko.observable("");
	this.LabelTitle = ko.observable("");
	this.Content = ko.observable("");
	this.ContentItems = ko.observableArray();
	this.ContentType = ko.observable("html");
	this.TinyMCEId = ko.observable("");
	this.TinyMCEContent = ko.observable("");
	this.Value = ko.observable("");
	this.NumValue = ko.observable(0);
	this.Edit = ko.observable(false);
	this.EditShortnames = ko.observable(false);
	this.Disabled = ko.observable(false);
	this.Element = ko.observable(null);
	
	this.PreviewItems = function()
	{
		var arr;
		if (this.Label() == "Columns")
		{	
			arr = getColumnsText(false);
		} else if (this.Label() == "Rows")
		{	
			arr = getRowsText(false);
		} else if (this.Label() == "PossibleAnswers")
		{	
			arr = getCombinedAnswerText(false);
		} else if (this.Label() == "Questions")
		{	
			arr = getQuestionsText(false);
		}
		
		if (arr.length > 5)
		{
			var arrsmall = [];
			arrsmall[0] = arr[0];
			arrsmall[1] = arr[1];
			arrsmall[2] = arr[2];
			arrsmall[3] = arr[3];
			arrsmall[4] = arr[4];
			arrsmall[5] = "...";
			return arrsmall;
		}
		
		return arr;
	}
	
	this.addSpecificExpectedAnswer = function(scoring)
	{
		var element = this.Element();
		
		this.Element().scoringItems.push(scoring);		
		this.ContentItems.push(newScoringWrapper(scoring));
		
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
		
		if (this.Element().type == 'DateQuestion')
		{
			$("input[data-label=ruleValue]").each(function(){
				createDatePickerForEditor(this, null);
			})
			$("input[data-label=ruleValue2]").each(function(){
				createDatePickerForEditor(this, null);
			})
		}
		
		tinymce.EditorManager.execCommand('mceToggleEditor', true, "feedback" + scoring.id());
	}
	
	this.addExpectedAnswer = function(createundostep)
	{
		var scoring = newScoringViewModel();
		scoring.correct(true);
		scoring.id(getNewId());
		
		this.addSpecificExpectedAnswer(scoring);
		
		if (createundostep)	_undoProcessor.addUndoStep(["addExpectedAnswer", this.Element().id()]);
		
		$("#properties").find('[data-toggle="tooltip"]').tooltip({
		    trigger : 'hover',
		    container: 'body'
		});
		
		checkQuizOtherValues();
	}
	
	this.removeExpectedAnswer = function(id)
	{
		var element = self.Element();
		
		var rule = element.scoringItems.remove( function (item) { return item.id() == id.id(); } );
		_actions.deletedElements.push(rule[0]);
		
		self.ContentItems.remove( function (item) { return item.id() == id.id(); } );
		
		_undoProcessor.addUndoStep(["removeExpectedAnswer", element.id()]);
		$(".tooltip").remove();
		
		checkQuizOtherValues();
	}
	
	this.availableValueTypes = function() {
		var result;
		switch (this.Element().type)
		{
			case "NumberQuestion":
				result = ko.observableArray(["lessThan", "lessThanOrEqualTo", "equalTo", "greaterThan", "greaterThanOrEqualTo", "between", "other", "empty"]);
				break;
			case "DateQuestion":
				result = ko.observableArray(["lessThan", "equalTo", "greaterThan", "between", "other", "empty"]);
				break;
			case "FreeTextQuestion":
				result = ko.observableArray(["equalTo", "other", "empty"]);
				break;
		}
		
		return result;
	}
};

var ElementProperties = function() {
	this.selectedelement = null;
	this.selectedproperty = null;
	this.selectedid;
	this.Type = ko.observable("");
	this.Id = ko.observable("");	
	this.propertyRows = ko.observableArray();
	
	this.clear = function()
	{
		for (var i = tinymce.editors.length - 1 ; i > -1 ; i--) {
           var ed_id = tinymce.editors[i].id;
           if (ed_id != "new-survey-title")
           {
        	   tinyMCE.execCommand("mceRemoveEditor", true, ed_id);
           }           
        }
		
		this.Type("");
		this.Id("");
		this.propertyRows.removeAll();
		this.selectedelement = null;
		$(".properties").find(".areaheader").find(".glyphicon-chevron-right").removeClass("glyphicon-chevron-right").addClass("glyphicon-chevron-down");
	}
	
	this.deselectAll = function()
	{
		this.clear();
		$(".selectedquestion").removeClass("selectedquestion");
		_actions.CopyEnabled(false);
		_actions.CutEnabled(false);
		_actions.MoveUpEnabled(false);
		_actions.MoveDownEnabled(false);
		_actions.DeleteEnabled(false);
	}
	
	this.showProperties = function(e, event, doubleclick)
	{			
		var advancedOpen = $(".advancedtogglebutton").find(".glyphicon-minus-sign").length > 0;
		
		_actions.ElementSelected(false);
		
		if ($(e).hasClass("selectedquestion") && !cntrlIsPressed && !shiftIsPressed && !$("#multiselectButton").hasClass("selected"))
		{
			this.deselectAll();
			return;
		}
		
		this.clear();	
		this.selectedelement = e;
		var id = $(e).attr("data-id");
		var removeselection = false;
		
		if (!cntrlIsPressed && !shiftIsPressed && !$("#multiselectButton").hasClass("selected"))
		{
			$(".selectedquestion").removeClass("selectedquestion");
			$(e).addClass("selectedquestion");
		} else if ((cntrlIsPressed || $("#multiselectButton").hasClass("selected")) && $(e).hasClass("selectedquestion")) {
			$(e).removeClass("selectedquestion");
			removeselection = true;
		} else if (shiftIsPressed && $("#content").find(".selectedquestion").length > 0) {
			var first = $("#content").find(".selectedquestion").first();
			var last = e;
			if ($(first).index() > $(e).index())
			{
				last = first;
				first = e;			
			}
			
			var started = false;
			$(".survey-element").each(function(){
				if ($(this).attr("data-id") == $(first).attr("data-id"))
				{
					started = true;
				} else if ($(this).attr("data-id") == $(last).attr("data-id"))
				{
					started = false;
				} else {
					if (started)
					{
						$(this).addClass("selectedquestion");
						$(".navigationitem[data-id=" + $(this).attr("data-id") + "]").addClass("selectedquestion");
					} else {
						$(this).removeClass("selectedquestion");
						$(".navigationitem[data-id=" + $(this).attr("data-id") + "]").removeClass("selectedquestion");
					}
				}
			});
			$(e).addClass("selectedquestion");
		} else {
			$(e).addClass("selectedquestion");
			
			$(e).find(".selectedquestion").removeClass("selectedquestion");
			
			if ($(e).hasClass("matrix-header") || $(e).hasClass("table-header") || $(e).hasClass("answertext") || $(e).is("td"))
			{
				$(e).closest(".survey-element.selectedquestion").removeClass("selectedquestion");
			}			
		}

		if (removeselection)
		{
			$(".navigationitem[data-id=" + id + "]").removeClass("selectedquestion");
			$(".navigationitem[data-id='navanswer" + id + "']").removeClass("selectedquestion");
		} else {
			$(".navigationitem[data-id=" + id + "]").addClass("selectedquestion");
			$(".navigationitem[data-id='navanswer" + id + "']").addClass("selectedquestion");
		}
		
		if (!$("#cancelcuttoolboxitem").is(":visible") && !$("#cancelcopytoolboxitem").is(":visible"))
		{
			_actions.CopyEnabled(true);
			_actions.CutEnabled(true);
		}
		
		if ($("#content").find(".selectedquestion").not(".locked").length > 0)
		{
			_actions.DeleteEnabled(true);
		} else {
			_actions.DeleteEnabled(false);
		}
		
		_actions.ChildSelected(false);
		
		if ($("#content").find(".selectedquestion").length == 0)
		{
			_actions.CopyEnabled(false);
			_actions.CutEnabled(false);
			_actions.DeleteEnabled(false);
			_actions.MoveUpEnabled(false);
			_actions.MoveDownEnabled(false);
		}
				
		if ($("#content").find(".selectedquestion").length == 1)
		{
			e = $("#content").find(".selectedquestion").first();
			
			if (e.is(":visible"))
			{
				_actions.ElementSelected(true);				
			}
			
			var element = _elements[$(e).attr("data-id")];
			
			_actions.MoveUpEnabled(e.index() > 0);
			_actions.MoveDownEnabled(!$(e).is(':last-child'));
			
			this.selectedelement = e;
			_elementProperties.Type(getElementType($(e)));
			_elementProperties.Id(getElementTypeAsId($(e)));
			
			$("#lockedElementInfo").hide();
			
			if (isOPC && element != null && element.hasOwnProperty("locked") && element.locked())
			{
				$("#lockedElementInfo").show();
			} else	if ($(e).hasClass("sectionitem"))
			{
				getTextPropertiesRow("Text", element.originalTitle(), true);
				getChoosePropertiesRow("Level", "1,2,3", false, false, $(e).find("input[name^='level']").val());
				getTextPropertiesRow("TabTitle", $(e).find("input[name^='tabtitle']").val(), false);
				getVisibilityRow(false);
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
			} else if ($(e).hasClass("freetextitem"))
			{
				getTextPropertiesRow("Text", element.originalTitle(), true);
				getCheckPropertiesRow("Mandatory", $(e).find("input[name^='optional']").val() == 'false');
				getChoosePropertiesRow("Rows", "1,2,3,4,5,10,20,30,40,50", false, false, $(e).find("input[name^='rows']").val());
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);	
				getMinMaxPropertiesRow("AcceptedNumberOfCharacters", 0, 5000, $(e).find("input[name^='min']").val(), $(e).find("input[name^='max']").val())
				getVisibilityRow(false);
				
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				getCheckPropertiesRow("ReadOnly", $(e).find("input[name^='readonly']").val() == 'true');
				getCheckPropertiesRow("Unique", $(e).find("input[name^='unique']").val() == 'true');
				getCheckPropertiesRow("Comparable", $(e).find("input[name^='comparable']").val() == 'true');
				getCheckPropertiesRow("Password", $(e).find("input[name^='password']").val() == 'true');
				getRegistrationFormRow($(e).find("input[name^='attribute']").val(), $(e).find("input[name^='nameattribute']").val());	
				
				if (isQuiz)
				{
					getQuizPropertiesRow();
					getQuizPropertiesContent();
				}
			} else if ($(e).hasClass("singlechoiceitem"))
			{
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				getActionRow("PossibleAnswers", "<span class='glyphicon glyphicon-plus'></span>", "addPossibleAnswer()", "<span class='glyphicon glyphicon-minus'></span>", "removePossibleAnswer($(_elementProperties.selectedelement))");
				getCheckPropertiesRow("Mandatory", $(e).find("input[name^='optional']").val() == 'false');
				getChoosePropertiesRow("Style", "RadioButton,SelectBox", false, false, $(e).find("input[name^='choicetype']").val() == 'radio' ? "RadioButton" : "SelectBox");
				getChoosePropertiesRow("Order", "Original,Alphabetical,Random", false, false, parseInt($(e).find("input[name^='order']").val()));
				getChoosePropertiesRow("Columns", "1,2,3,4", false, false, $(e).find("input[name^='columns']").val());
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);		
				getVisibilityRow(false);			
				
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				getCheckPropertiesRow("ReadOnly", $(e).find("input[name^='readonly']").val() == 'true');
				getRegistrationFormRow($(e).find("input[name^='attribute']").val(), $(e).find("input[name^='nameattribute']").val());
								
				if (isQuiz)
				{
					getQuizPropertiesRow();
					getQuizPropertiesContent();
				}
			} else if ($(e).hasClass("multiplechoiceitem"))
			{
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				getActionRow("PossibleAnswers", "<span class='glyphicon glyphicon-plus'></span>", "addPossibleAnswer()", "<span class='glyphicon glyphicon-minus'></span>", "removePossibleAnswer($(_elementProperties.selectedelement))");
				getCheckPropertiesRow("Mandatory", $(e).find("input[name^='optional']").val() == 'false');
				getChoosePropertiesRow("Style", "CheckBox,ListBox", false, false, $(e).find("input[name^='choicetype']").val() == 'checkbox' ? "CheckBox" : "ListBox");
				getChoosePropertiesRow("Order", "Original,Alphabetical,Random", false, false,  parseInt($(e).find("input[name^='order']").val()));
				getChoosePropertiesRow("Columns", "1,2,3,4", false, false, $(e).find("input[name^='columns']").val());
				getMinMaxPropertiesRow("NumberOfChoices", 0, 500, $(e).find("input[name^='choicemin']").val(), $(e).find("input[name^='choicemax']").val())
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);	
				getVisibilityRow(false);
				
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				getCheckPropertiesRow("ReadOnly", $(e).find("input[name^='readonly']").val() == 'true');
				getRegistrationFormRow($(e).find("input[name^='attribute']").val(), $(e).find("input[name^='nameattribute']").val());		
				
				if (isQuiz)
				{
					getQuizPropertiesRow();
					getQuizPropertiesContent();
				}
			} else if ($(e).hasClass("answertext"))
			{
				var parent = _elements[$(e).closest(".survey-element").attr("data-id")];
				
				if (isOPC && parent != null && parent.hasOwnProperty("locked") && parent.locked())
				{
					$("#lockedElementInfo").show();
				} else {
				
					var cell = $(e).closest("td").prev();
					_actions.DeleteEnabled(false);
					
					var id = $(e).attr("data-id");
					var text = $("textarea[name^='answer'][data-id='" + id + "']").first().text();
					
					getTextPropertiesRow("Text", text, true);
					getAdvancedPropertiesRow();
					
					var shortname = $("input[name^='pashortname'][data-id='" + id + "']").first().val();
					getTextPropertiesRow("Identifier", shortname, false);
					
					_actions.ChildSelected(true);
					
					_actions.CopyEnabled(false);
					_actions.CutEnabled(false);		
					_actions.MoveUpEnabled(false);
					_actions.MoveDownEnabled(false);
				}
			} else if ($(e).hasClass("numberitem"))
			{
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				getCheckPropertiesRow("Mandatory", $(e).find("input[name^='optional']").val() == 'false');
				getTextPropertiesRow("Unit", $(e).find("input[name^='unit']").val(), false);
				getChoosePropertiesRow("DecimalPlaces", ",1,2,3,4,5,6,7,8,9,10", false, false, $(e).find("input[name^='decimalplaces']").val());
				getMinMaxPropertiesRow("Values", 0, null, $(e).find("input[name^='min']").val(), $(e).find("input[name^='max']").val())
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);		
				getVisibilityRow(false);
				
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				getCheckPropertiesRow("ReadOnly", $(e).find("input[name^='readonly']").val() == 'true');
				getCheckPropertiesRow("Unique", $(e).find("input[name^='unique']").val() == 'true');		
				getRegistrationFormRow($(e).find("input[name^='attribute']").val(), $(e).find("input[name^='nameattribute']").val());	
				
				if (isQuiz)
				{
					getQuizPropertiesRow();
					getQuizPropertiesContent();
				}
			} else if ($(e).hasClass("matrixitem"))
			{
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				
				var rows = $(e).find(".matrixtable").first().find("tr").length-1;
				var mandatoryrows = $(e).find("input[name^='optional'][value=false]").length;
				getCheckPropertiesRow("Mandatory", rows == mandatoryrows);
				
				getActionRow("Columns", "<span class='glyphicon glyphicon-plus'></span>", "addColumn(false)", "<span class='glyphicon glyphicon-minus'></span>", "removeColumn(false)");
				getActionRow("Rows", "<span class='glyphicon glyphicon-plus'></span>", "addRow(false)", "<span class='glyphicon glyphicon-minus'></span>", "removeRow(false)");
				getChoosePropertiesRow("Style", "SingleChoice,MultipleChoice", false, false, $(e).find("input[name^='single']").val() == 'true' ? "SingleChoice" : "MultipleChoice");
				getCheckPropertiesRow("Interdependency", $(e).find("input[name^='interdependent']").val() == 'true');
				getChoosePropertiesRow("Order", "Original,Alphabetical,Random", false, false,  parseInt($(e).find("input[name^='order']").val()));
				getMinMaxPropertiesRow("NumberOfAnsweredRows", 0, null, $(e).find("input[name^='rowsmin']").val(), $(e).find("input[name^='rowsmax']").val())
				getChoosePropertiesRow("Size", "fitToContent,fitToPage,manualColumnWidth", false, true, parseInt($(e).find("input[name^='tabletype']").val()));
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);		
				getVisibilityRow(false);
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				getCheckPropertiesRow("ReadOnly", $(e).find("input[name^='readonly']").val() == 'true');
				getRegistrationFormRow($(e).find("input[name^='attribute']").val(), $(e).find("input[name^='nameattribute']").val());
			} else if ($(e).hasClass("matrix-header"))
			{
				var parent = _elements[$(e).closest(".survey-element").attr("data-id")];
				
				if (isOPC && parent != null && parent.hasOwnProperty("locked") && parent.locked())
				{
					$("#lockedElementInfo").show();
				} else {
					var id = $(e).attr("data-id");
		 	 		var text = $("textarea[name^='text" + id + "']").first().text();
		 	 		var shortname = $("input[name^='shortname" + id + "']").first().val();
		 	 		                                
					getTextPropertiesRow("Text", text, true);
					
					if ($(e).closest("thead").length == 0)
					{
						getCheckPropertiesRow("Mandatory", $("input[name^='optional" + id + "']").val() == 'false');
						getVisibilityRow(false);
					}
					
					getAdvancedPropertiesRow();
					getTextPropertiesRow("Identifier", shortname, false);
					
					_actions.ChildSelected(true);
					
					_actions.CopyEnabled(false);
					_actions.CutEnabled(false);		
					_actions.MoveUpEnabled(false);
					_actions.MoveDownEnabled(false);
				}
			} else if ($(e).hasClass("mytableitem"))
			{
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				
				var rows = $(e).find(".tabletable").first().find("tr").length-1;
				var mandatoryrows = $(e).find("td[data-optional=false]").length;
				getCheckPropertiesRow("Mandatory", rows == mandatoryrows);
				getActionRow("Columns", "<span class='glyphicon glyphicon-plus'></span>", "addColumn(false)", "<span class='glyphicon glyphicon-minus'></span>", "removeColumn(false)");
				getActionRow("Rows", "<span class='glyphicon glyphicon-plus'></span>", "addRow(false)", "<span class='glyphicon glyphicon-minus'></span>", "removeRow(false)");
			
				getChoosePropertiesRow("Size", "fitToContent,fitToPage,manualColumnWidth", false, true, parseInt($(e).find("input[name^='tabletype']").val()));
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);	
				
				getVisibilityRow(false);
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				getCheckPropertiesRow("ReadOnly", $(e).find("input[name^='readonly']").val() == 'true');
				getRegistrationFormRow($(e).find("input[name^='attribute']").val(), $(e).find("input[name^='nameattribute']").val());
			} else if ($(e).hasClass("table-header"))
			{
				var parent = _elements[$(e).closest(".survey-element").attr("data-id")];
				
				if (isOPC && parent != null && parent.hasOwnProperty("locked") && parent.locked())
				{
					$("#lockedElementInfo").show();
				} else {
					var text = $(e).html();
					if ($(e).find("textarea").length > 0)
					{
						text = $(e).find("textarea").first().text();
					}
					
					getTextPropertiesRow("Text", text, true);
					
					if ($(e).closest("tr").index() > 0)
					{
						getCheckPropertiesRow("Mandatory", $(e).attr("data-optional") == 'false');
					}
					
					getAdvancedPropertiesRow();
					getTextPropertiesRow("Identifier", $(e).attr("data-shortname"), false);
					
					_actions.ChildSelected(true);
					
					_actions.CopyEnabled(false);
					_actions.CutEnabled(false);		
					_actions.MoveUpEnabled(false);
					_actions.MoveDownEnabled(false);
				}
			} else if ($(e).hasClass("dateitem"))
			{
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				getCheckPropertiesRow("Mandatory", $(e).find("input[name^='optional']").val() == 'false');
				getMinMaxPropertiesRow("Values", 0, null, $(e).find("input[name^='min']").val(), $(e).find("input[name^='max']").val())
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);		
				getVisibilityRow(false);
				
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				getCheckPropertiesRow("ReadOnly", $(e).find("input[name^='readonly']").val() == 'true');
				getRegistrationFormRow($(e).find("input[name^='attribute']").val(), $(e).find("input[name^='nameattribute']").val());
								
				if (isQuiz)
				{
					getQuizPropertiesRow();
					getQuizPropertiesContent();
				}
			} else if ($(e).hasClass("textitem"))
			{
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				getVisibilityRow(false);
			} else if ($(e).hasClass("imageitem"))
			{
				getUploadRow("Image", null);
				getChoosePropertiesRow("Align", "left,center,right", false, true, $(e).find("input[name^='align']").first().val());		
				getTextPropertiesRow("Size", $(e).find("input[name^='scale']").first().val(), false, "%");
				getTextPropertiesRow("DescriptiveText", $(e).find("textarea[name^='text']").first().text(), false);
				getTextPropertiesRow("LongDescription", $(e).find("input[name^='longdesc']").first().val(), false);
				getVisibilityRow(false);
			} else if ($(e).hasClass("ruleritem"))
			{
				getChoosePropertiesRow("Style", "solid,dashed,dotted", false, true, $(e).find("input[name^='style']").first().val());		
				getChoosePropertiesRow("Height", "1,2,3,4,5,6,7,8,9,10", false, false, $(e).find("input[name^='height']").val());
				getChooseColor("Color", $(e).find("input[name^='color']").val());
				getVisibilityRow(false);
			} else if ($(e).hasClass("uploaditem"))
			{
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				getCheckPropertiesRow("Mandatory", $(e).find("input[name^='optional']").val() == 'false');
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);	
				getVisibilityRow(false);
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				getTextPropertiesRow("FileType", $(e).find("input[name^='extensions']").first().val(), false);
			} else if ($(e).hasClass("downloaditem"))
			{
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				getUploadRow("File");
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);	
				getVisibilityRow(false);
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
			} else if ($(e).hasClass("emailitem"))
			{
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				getCheckPropertiesRow("Mandatory", $(e).find("input[name^='optional']").val() == 'false');
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);	
				getVisibilityRow(false);
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				getCheckPropertiesRow("ReadOnly", $(e).find("input[name^='readonly']").val() == 'true');
				getRegistrationFormRow($(e).find("input[name^='attribute']").val(), $(e).find("input[name^='nameattribute']").val());	
			} else if ($(e).hasClass("regexitem"))
			{
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				getTextPropertiesRow("RegularExpression", $(e).find("input[name^='regex']").first().val(), false);
				getCheckPropertiesRow("Mandatory", $(e).find("input[name^='optional']").val() == 'false');
				getChoosePropertiesRow("Rows", "1,2,3,4,5,10,20,30,40,50", false, false, $(e).find("input[name^='rows']").val());
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);
				getVisibilityRow(false);
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				getCheckPropertiesRow("ReadOnly", $(e).find("input[name^='readonly']").val() == 'true');
				getCheckPropertiesRow("Unique", $(e).find("input[name^='unique']").val() == 'true');
				getCheckPropertiesRow("Comparable", $(e).find("input[name^='comparable']").val() == 'true');
				getCheckPropertiesRow("Password", $(e).find("input[name^='password']").val() == 'true');
				getRegistrationFormRow($(e).find("input[name^='attribute']").val(), $(e).find("input[name^='nameattribute']").val());	
			} else if ($(e).hasClass("galleryitem"))
			{
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				getUploadRow("File", $(e).find(".files").first());
				getCheckPropertiesRow("ImageSelectable", $(e).find("input[name^='selectable']").val() == 'true');
				getNumberPropertiesRow("MaxSelections", $(e).find("input[name^='limit']").val());
				getCheckPropertiesRow("Mandatory", $(e).find("input[name^='optional']").val() == 'false');
				getChoosePropertiesRow("Columns", "1,2,3,4", false, false, $(e).find("input[name^='columns']").val());
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);
				getVisibilityRow(false);
				getAdvancedPropertiesRow();
				getCheckPropertiesRow("AutoNumbering", $(e).find("input[name^='numbering']").val() == 'true');
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				getCheckPropertiesRow("ReadOnly", $(e).find("input[name^='readonly']").val() == 'true');
				checkGalleryProperties(false);
			} else if ($(e).hasClass("confirmationitem"))
			{
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				getTextPropertiesRow("ConfirmationText", $(e).find("textarea[name^='confirmationtext']").first().text(), true);
				getUploadRow("File");
				getVisibilityRow(false);
				getAdvancedPropertiesRow();
				getTextPropertiesRow("LabelText", $(e).find("textarea[name^='confirmationlabel']").first().text(), false);
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
			} else if ($(e).hasClass("ratingitem"))
			{
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				
				var rows = $(e).find(".ratingtable").first().find("tr").length;
				var mandatoryrows = $(e).find(".hiddenratingquestions").find("input[name^='questionoptional'][value=false]").length;
				getCheckPropertiesRow("Mandatory", rows == mandatoryrows);
				
				getActionRow("Questions", "<span class='glyphicon glyphicon-plus'></span>", "addRow(false)", "<span class='glyphicon glyphicon-minus'></span>", "removeRow(false)");
				getChoosePropertiesRow("IconType", "Stars,Circles,Hearts", false, false, parseInt($(e).find("input[name^='iconType']").val()));
				getNumberPropertiesRow("NumIcons", $(e).find("input[name^='numIcons']").val());
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);
				getVisibilityRow(false);
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
			} else if ($(e).hasClass("ratingquestion"))
			{				
				var parent = _elements[$(e).closest(".survey-element").attr("data-id")];
				
				if (isOPC && parent != null && parent.hasOwnProperty("locked") && parent.locked())
				{
					$("#lockedElementInfo").show();
				} else {
				
					_actions.DeleteEnabled(false);
					
					var id = $(e).attr("data-id");
					var text = $("textarea[name^='question'][data-id='" + id + "']").first().text();
					
					getTextPropertiesRow("Text", text, true);
					
					getCheckPropertiesRow("Mandatory", $("input[name^='questionoptional'][data-id='" + id + "']").val() == 'false');
					
					getAdvancedPropertiesRow();
					
					var shortname = $("input[name^='questionshortname'][data-id='" + id + "']").first().val();
					getTextPropertiesRow("Identifier", shortname, false);
					
					_actions.ChildSelected(true);
					
					_actions.CopyEnabled(false);
					_actions.CutEnabled(false);		
					_actions.MoveUpEnabled(false);
					_actions.MoveDownEnabled(false);
				}
			} else if ($(e).find(".gallery-image").length > 0)
			{
				getTextPropertiesRow("Text", $(e).find("textarea[name^='comment']").first().text(), true);
				getTextPropertiesRow("LongDescription", $(e).find("input[name^='longdesc']").first().val(), false);
				getTextPropertiesRow("Title", $(e).find("input[name^='name']").first().val(), false);
				
				_actions.ChildSelected(true);
				
				_actions.CopyEnabled(false);
				_actions.CutEnabled(false);
				_actions.MoveUpEnabled(false);
				_actions.MoveDownEnabled(false);
				_actions.DeleteEnabled(false);
			}
		} else {
			_actions.MoveUpEnabled(true);
			_actions.MoveDownEnabled(true);
			
			var childselected = false;
			var hidevisibility = false;
			var galleryselected = false;
			var locked = false;
			var firstselected = false;
			var lastselected = false;
			var numelements = $("#content").find("li").length;
			$("#content").find(".selectedquestion").each(function(){
				
				if ($(this).index() == 0)
				{
					firstselected = true;
				}
				if ($(this).index() == numelements - 1)
				{
					lastselected = true;
				}				
				
				if ($(this).hasClass("matrix-header") || $(this).hasClass("table-header") || $(this).hasClass("answertext"))
				{
					childselected = true;
				} else if ($(this).is("td")) {
					if ($(this).closest(".galleryitem").length > 0)
					{
						childselected = true;
						hidevisibility = true;
						_actions.DeleteEnabled(false);
					}
				}
				
				if ($(this).hasClass("answertext"))
				{
					hidevisibility = true;
					_actions.DeleteEnabled(false);
				}
				
				if ($(this).hasClass("matrix-header") || $(this).hasClass("table-header"))
				{
					if ($(this).closest("tr").index() == 0)
					{
						hidevisibility = true;
					}
				}
				
				if ($(this).is(":visible"))
				{
					_actions.ElementSelected(true);				
				}
				
				var element = _elements[$(this).attr("data-id")];
				
				if (isOPC && element != null && element.hasOwnProperty("locked") && element.locked())
				{
					locked = true;
				}
			});
			
			if (locked)
			{
				$("#lockedElementInfo").show();
				hidevisibility = true;
				_actions.DeleteEnabled(false);
			} else if (childselected)
			{
				_actions.ChildSelected(true);
				_actions.CopyEnabled(false);
				_actions.CutEnabled(false);
				_actions.MoveUpEnabled(false);
				_actions.MoveDownEnabled(false);
			}
			
			if (firstselected)
			{
				_actions.MoveUpEnabled(false);
			}
			if (lastselected)
			{
				_actions.MoveDownEnabled(false);
			}
			
			if (!hidevisibility)
			getVisibilityRow(true);
		}
		
		if (event != null)
		{
			event.stopImmediatePropagation();
			event.preventDefault();
		}
		
		checkInputStates();
		if (!advancedOpen)
		{
			toggleAdvancedProperties($("#properties").find(".advancedtogglebutton").first());
		}
		
		var span = $("#elementpropertiescollapsebutton");
		if ($(span).hasClass("glyphicon-chevron-right"))
		{
			var tr = $(".properties").find("tr").first();
			$(tr).hide(400);
			$(tr).nextUntil(".quiz").addClass("hideme2");
		}
		
		if (isQuiz && !lastQuizPropertiesVisible && $("#quizpropertiescollapsebutton").is(":visible"))
		{
			showHideQuizProperties($("#quizpropertiescollapsebutton"));
		}		
		
		$("#properties").find('[data-toggle="tooltip"]').tooltip({
		    trigger : 'hover',
		    container: 'body'
		});
	}	
};

var _elementProperties = new ElementProperties();

$(function() {
	ko.applyBindings(_elementProperties, $("#properties")[0]);
});