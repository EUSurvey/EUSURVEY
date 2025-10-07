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
	this.FormulaInputId = ko.observable("");
	this.NumberElements = ko.observableArray(); 
	this.Value = ko.observable("");
	this.NumValue = ko.observable(0);
	this.Edit = ko.observable(false);
	this.EditShortnames = ko.observable(false);
	this.Disabled = ko.observable(false);
	this.Element = ko.observable(null);
	this.IsVisible = ko.observable(true);
	this.Cell = ko.observable(null);
	
	this.MinItems = function() {
		if (this.Label() == "RankingItems") {
			return 2;
		}
		return 1;
	};
	
	this.PreviewItems = function()
	{
		var arr;

		switch (this.Label()) {
			case "Columns":
				arr = getColumnsText(false);
				break;
			case "Rows":
				arr = getRowsText(false);
				break;
			case "cell":
			case "column":
			case "row":
			case "PossibleAnswers":
				arr = getCombinedAnswerText(false);
				break;
			case "Questions":
				arr = getQuestionsText(false);
				break;
			case "RankingItems":
				arr = getCombinedRankingText(false);
				break;
			default:
				arr = ["Undefined PreviewItems Label"] //Helps to find the problem faster
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
				try {
					tinyMCE.execCommand("mceRemoveEditor", true, ed_id);
				} catch {}       	   
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

	this.deselectForPaste = function()
	{
		this.clear();
		_actions.CopyEnabled(false);
		_actions.CutEnabled(false);
		_actions.MoveUpEnabled(false);
		_actions.MoveDownEnabled(false);
		_actions.DeleteEnabled(false);
	}
	
	this.showProperties = function(e, event, doubleclick)
	{
		$('.activeproperty').each(function(){		
			var attr = $(this).attr('data-child');
			if (typeof attr !== 'undefined' && attr !== false) {
			    changeChildren(this, null);
			} else {
				update(this);
			}
		})
	
		var advancedOpen = $(".advancedtogglebutton").find(".glyphicon-plus-sign").length == 0;
				
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
		
		$(".highlightedquestion").removeClass("highlightedquestion");
		
		// ACTIONS
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
			
			if ($(e).hasClass("matrix-header") || $(e).hasClass("table-header") || $(e).hasClass("answertext") || $(e).is("td") || $(e).is("th"))
			{
				$(e).closest(".survey-element.selectedquestion").removeClass("selectedquestion");
			}			
		}

		if (id != null && id.length > 0) {
			if (removeselection)
			{
				$(".navigationitem[data-id=" + id + "]").removeClass("selectedquestion");
				$(".navigationitem[data-id='navanswer" + id + "']").removeClass("selectedquestion");
			} else {
				$(".navigationitem[data-id=" + id + "]").addClass("selectedquestion");
				$(".navigationitem[data-id='navanswer" + id + "']").addClass("selectedquestion");
			}
		}

		_actions.CopyEnabled(true);
		
		if ($("#content").find(".selectedquestion").not(".locked").length > 0)
		{
			_actions.CutEnabled(true);
			_actions.DeleteEnabled(true);
		} else {
			_actions.CutEnabled(false);
			_actions.DeleteEnabled(false);
		}
		
		_actions.ChildSelected(false);

		let selection = $("#content").find(".selectedquestion");
		let filteredSelection = selection.filter((i, el) => SurveyRuleEvaluator.isElementAllowed(el))

		if (selection.length == 0){
			_actions.CopyEnabled(false);
			_actions.CutEnabled(false);
			_actions.DeleteEnabled(false);
			_actions.MoveUpEnabled(false);
			_actions.MoveDownEnabled(false);
		} else if (filteredSelection.length == 0){
			_actions.CopyEnabled(false);
		}
				
		if (selection.length == 1)
		{
			e = selection.first();
			
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

			var parent = _elements[$(e).closest(".survey-element").attr("data-id")];

			if (isOPC && element != null && element.hasOwnProperty("locked") && element.locked())
			{
				_actions.CutEnabled(false);
				_actions.DeleteEnabled(false);
				$("#lockedElementInfo").show();
			} else if (isOPC && parent != null && parent.hasOwnProperty("locked") && parent.locked())
			{
				_actions.CutEnabled(false);
				_actions.DeleteEnabled(false);
				$("#lockedElementInfo").show();
			} else if ($(e).hasClass("sectionitem"))
			{
				getTextPropertiesRow("Text", element.originalTitle(), true);
				
				var level = $(e).find("input[name^='level']").val();
				
				getChoosePropertiesRow("Level", "1,2,3", false, false, level);
				if (level === "1") {
					getChoosePropertiesRow("OrderSection", "Original,Random", false, false, parseInt($(e).find("input[name^='order']").val()));
				}
				
				getTextPropertiesRow("TabTitle", $(e).find("input[name^='tabtitle']").val(), false);
				if (!isDelphi)
					getVisibilityRow(false);
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
			} else if ($(e).hasClass("freetextitem"))
			{
				const isDelphiQuestion = $(e).find("input[name^='delphiquestion']").val() == 'true';
				if (isDelphi)
				{
					getCheckPropertiesRow("DelphiQuestion", isDelphiQuestion);
					getChoosePropertiesRow("DelphiChartType", "None,WordCloud", false, true, $(e).find("input[name^='delphicharttype']").val(), false);
					getCheckPropertiesRow("ShowExplanationBox", $(e).find("input[name^='explanationbox']").val() == 'true');
				}
				getTextPropertiesRow("Text", element.originalTitle(), true);
				getCheckPropertiesRow("Mandatory", $(e).find("input[name^='optional']").val() == 'false', isDelphiQuestion);
				getChoosePropertiesRow("Rows", "1,2,3,4,5,10,20,30,40,50", false, false, $(e).find("input[name^='rows']").val());
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);	
				getMinMaxPropertiesRow("AcceptedNumberOfCharacters", 0, 5000, $(e).find("input[name^='min']").val(), $(e).find("input[name^='max']").val())
				getVisibilityRow(false, !isDelphiQuestion);
				
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				getReadOnlyAndHiddenRow(e)
				getCheckPropertiesRow("Unique", $(e).find("input[name^='unique']").val() == 'true');
				getCheckPropertiesRow("Comparable", $(e).find("input[name^='comparable']").val() == 'true');
				getCheckPropertiesRow("Password", $(e).find("input[name^='password']").val() == 'true');
				getRegistrationFormRow($(e).find("input[name^='attribute']").val(), $(e).find("input[name^='nameattribute']").val());	
				
				if (isQuiz)
				{
					getQuizPropertiesRow();
					getQuizPropertiesContent();
				}
				if (isDelphi)
				{
					adaptDelphiControls(element);
				}
			} else if ($(e).hasClass("singlechoiceitem"))
			{
				const isDelphiQuestion = $(e).find("input[name^='delphiquestion']").val() == 'true';
				if (isDelphi)
				{
					getCheckPropertiesRow("DelphiQuestion", isDelphiQuestion);
					getChoosePropertiesRow("DelphiChartType", "Bar,Column,Line,Pie,Radar,Scatter", false, true, $(e).find("input[name^='delphicharttype']").val(), false);
					getCheckPropertiesRow("ShowExplanationBox", $(e).find("input[name^='explanationbox']").val() == 'true');
				}
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				let editenabled = !element.editorRowsLocked()
				
				if (element.isTargetDatasetQuestion()) {		
					getTargetDatasetsRow();				
				} else {		
					getActionRow("PossibleAnswers", "<span class='glyphicon glyphicon-plus'></span>", "addPossibleAnswer()", "<span class='glyphicon glyphicon-minus'></span>", "removePossibleAnswer($(_elementProperties.selectedelement))", editenabled);
			
					getCheckPropertiesRow("Mandatory", $(e).find("input[name^='optional']").val() == 'false', isDelphiQuestion);

					getChoosePropertiesRow("Style", "RadioButton,SelectBox,Buttons" + (isDelphi ? ",LikertScale" : ""), false, false, element.styleType());
					
					var subType = $(e).find("input[name^='subType']").val()
					if (subType === "euCountries" || subType === "unCountries")
					{
						getChoosePropertiesRow("Display", "CountryOnly,ISOOnly,ISO+Country,Country+ISO", false, false, parseInt($(e).find("input[name^='displayMode']").val()));
					}	 		
				
					if (isDelphi && element.likert())
					{
						getChoosePropertiesRow("MaxDistanceToMedian", "Ignore,0,1,2,3,4,5", false, false, $(e).find("input[name^='maxDistance']").val());
					} else {				
						getChoosePropertiesRow("Order", "Original,Alphabetical,Random", false, false, parseInt($(e).find("input[name^='order']").val()));
					}
						
					getChoosePropertiesRow("Columns", "1,2,3,4", false, false, $(e).find("input[name^='columns']").val());
					getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);
					getVisibilityRow(false, !isDelphiQuestion);
				} 
								
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				getReadOnlyAndHiddenRow(e)
				getRegistrationFormRow($(e).find("input[name^='attribute']").val(), $(e).find("input[name^='nameattribute']").val());

				if (isQuiz)
				{
					getQuizPropertiesRow();
					getQuizPropertiesContent();
				}
				
				if (isDelphi)
				{
					adaptDelphiControls(element);
				}

				if (isECF)
				{
					getECFPropertiesRow();
					getECFPropertiesContent();
				}
				
				if (element.isSAQuestion()) {	
					getSAQuestionRow(element);
				}
				
				if (element.isTargetDatasetQuestion()) {		
					getTargetDatasetsRow2();
					getCheckPropertiesRow("DisplayAllQuestions", $(e).find("input[name^='displayAllQuestions']").val() == 'true');	
				}
				
			} else if ($(e).hasClass("multiplechoiceitem"))
			{
				const isDelphiQuestion = $(e).find("input[name^='delphiquestion']").val() == 'true';
				if (isDelphi)
				{
					getCheckPropertiesRow("DelphiQuestion", isDelphiQuestion);
					getChoosePropertiesRow("DelphiChartType", "Bar,Column,Line,Pie,Radar,Scatter", false, true, $(e).find("input[name^='delphicharttype']").val(), false);
					getCheckPropertiesRow("ShowExplanationBox", $(e).find("input[name^='explanationbox']").val() == 'true');
				}
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				let editenabled = !element.editorRowsLocked()
				getActionRow("PossibleAnswers", "<span class='glyphicon glyphicon-plus'></span>", "addPossibleAnswer()", "<span class='glyphicon glyphicon-minus'></span>", "removePossibleAnswer($(_elementProperties.selectedelement))", editenabled);
				getCheckPropertiesRow("Mandatory", $(e).find("input[name^='optional']").val() == 'false', isDelphiQuestion);
				getChoosePropertiesRow("Style", (isEVote ? "EVoteList" : "CheckBox,ListBox"), false, false,  (isEVote ? "EVoteList" : element.styleType()), true);
				if (!element.isEVoteList()) {
					getChoosePropertiesRow("Order", "Original,Alphabetical,Random", false, false, parseInt($(e).find("input[name^='order']").val()));
				}
				getChoosePropertiesRow("Columns", "1,2,3,4", false, false, $(e).find("input[name^='columns']").val());
				getMinMaxPropertiesRow("NumberOfChoices", 0, 500, $(e).find("input[name^='choicemin']").val(), $(e).find("input[name^='choicemax']").val())
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);
				getVisibilityRow(false, !isDelphiQuestion);
				
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				getReadOnlyAndHiddenRow(e)
				getRegistrationFormRow($(e).find("input[name^='attribute']").val(), $(e).find("input[name^='nameattribute']").val());
				
				if (isQuiz)
				{
					getQuizPropertiesRow();
					getQuizPropertiesContent();
				}
				if (isDelphi)
				{
					adaptDelphiControls(element);
				}
			} else if ($(e).hasClass("rankingitem"))
			{
				const isDelphiQuestion = $(e).find("input[name^='delphiquestion']").val() == 'true';
				if (isDelphi)
				{
					getCheckPropertiesRow("DelphiQuestion", isDelphiQuestion);
					getChoosePropertiesRow("DelphiChartType", "Bar,Column", false, true, $(e).find("input[name^='delphicharttype']").val(), false);
					getCheckPropertiesRow("ShowExplanationBox", $(e).find("input[name^='explanationbox']").val() == 'true');
				}
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				getActionRow("RankingItems", "<span class='glyphicon glyphicon-plus'></span>", "addRankingEntry()", "<span class='glyphicon glyphicon-minus'></span>", "removeRankingEntry($(_elementProperties.selectedelement))", false);
				getCheckPropertiesRow("Mandatory", $(e).find("input[name^='optional']").val() == 'false', isDelphiQuestion);
				getChoosePropertiesRow("Order", "Original,Alphabetical,Random", false, false, parseInt($(e).find("input[name^='order']").val()));
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);
				getVisibilityRow(false, !isDelphiQuestion);

				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				
				if (isDelphi)
				{
					adaptDelphiControls(element);
				}
			} else if ($(e).hasClass("rankingitemtext"))
			{
				_actions.DeleteEnabled(true);

				var id = $(e).attr("data-id");
				var text = $("textarea[name^='rankingitemtitle'][data-id='" + id + "']")
						.first().text();

				getTextPropertiesRow("Text", text, true);
				getAdvancedPropertiesRow();

				var shortname = $(
						"input[name^='rankingitemshortname'][data-id='" + id + "']")
						.first().val();
				getTextPropertiesRow("Identifier", shortname, false);

				_actions.ChildSelected(true);

				_actions.CopyEnabled(false);
				_actions.CutEnabled(false);
				_actions.MoveUpEnabled(false);
				_actions.MoveDownEnabled(false);
			} else if ($(e).hasClass("answertext"))
			{
				if (parent != null && (parent.type === "MultipleChoiceQuestion" || parent.type === "SingleChoiceQuestion")){
					_actions.DeleteEnabled(true)
				} else {
					_actions.DeleteEnabled(false);
				}

				var id = $(e).attr("data-id");
				var text = $("textarea[name^='answer'][data-id='" + id + "']").first().text();

				getTextPropertiesRow("Text", text, true);
				getAdvancedPropertiesRow();

				var shortname = $("input[name^='pashortname'][data-id='" + id + "']").first().val();
				getTextPropertiesRow("Identifier", shortname, false);

				if (parent.type === "MultipleChoiceQuestion" && parent.styleType() == "CheckBox") {
					getCheckPropertiesRow("Exclusive", $("input[name^='exclusive'][data-id='" + id + "']").val() == 'true');
				}

				_actions.ChildSelected(true);

				_actions.CopyEnabled(false);
				_actions.CutEnabled(false);
				_actions.MoveUpEnabled(false);
				_actions.MoveDownEnabled(false);
			} else if ($(e).hasClass("numberitem"))
			{
				const isDelphiQuestion = $(e).find("input[name^='delphiquestion']").val() == 'true';
				if (isDelphi)
				{
					getCheckPropertiesRow("DelphiQuestion", isDelphiQuestion);
					getChoosePropertiesRow("DelphiChartTypeNumber", "Bar,Column,Line,Pie,Radar,Scatter", false, true, $(e).find("input[name^='delphicharttype']").val(), false);
					getCheckPropertiesRow("ShowExplanationBox", $(e).find("input[name^='explanationbox']").val() == 'true');
				}
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				getChoosePropertiesRow("DisplaySlider", "Number,Slider", false, false, $(e).find("input[name^='display']").val());
								
				getCheckPropertiesRow("Mandatory", $(e).find("input[name^='optional']").val() == 'false', isDelphiQuestion);
				getTextPropertiesRow("Unit", $(e).find("input[name^='unit']").val(), false);
				getChoosePropertiesRow("DecimalPlaces", ",1,2,3,4,5,6,7,8,9,10", false, false, $(e).find("input[name^='decimalplaces']").val());
				
				if (isDelphi)
				{
					getNumberPropertiesRow("MaxDistanceToMedian", $(e).find("input[name^='maxDistance']").val());
				}
				
				getMinMaxPropertiesRow("Values", null, null, $(e).find("input[name^='min']").val(), $(e).find("input[name^='max']").val())
				getTextPropertiesRow("MinLabel", $(e).find("input[name^='minLabel']").val(), false);
				getTextPropertiesRow("MaxLabel", $(e).find("input[name^='maxLabel']").val(), false);
				getChoosePropertiesRow("InitialSliderPosition", "Left,Middle,Right", false, false, $(e).find("input[name^='initialSliderPosition']").val());
				//getCheckPropertiesRow("DisplayGraduationScale", $(e).find("input[name^='displayGraduationScale']").val() == 'true');
	
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);
				getVisibilityRow(false, !isDelphiQuestion);
					
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				getReadOnlyAndHiddenRow(e)
				getCheckPropertiesRow("Unique", $(e).find("input[name^='unique']").val() == 'true');
				getRegistrationFormRow($(e).find("input[name^='attribute']").val(), $(e).find("input[name^='nameattribute']").val());	
				
				if (isQuiz)
				{
					getQuizPropertiesRow();
					getQuizPropertiesContent();
				}
				
				if (isDelphi)
				{
					adaptDelphiControls(element);
				}
				
				adaptSliderDisplay($(e).find("input[name^='display']").val() === 'Slider');
				
			} else if ($(e).hasClass("matrixitem"))
			{
				const isDelphiQuestion = $(e).find("input[name^='delphiquestion']").val() == 'true';
				if (isDelphi)
				{
					getCheckPropertiesRow("DelphiQuestion", isDelphiQuestion);
					getChoosePropertiesRow("DelphiChartType", "Bar,Column,Line,Pie,Radar,Scatter", false, true, $(e).find("input[name^='delphicharttype']").val(), false);
					getCheckPropertiesRow("ShowExplanationBox", $(e).find("input[name^='explanationbox']").val() == 'true');
				}
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				
				var rows = $(e).find(".matrixtable").first().find("tr").length-1;
				var mandatoryrows = $(e).find("input[name^='optional'][value=false]").length;
				getCheckPropertiesRow("Mandatory", rows == mandatoryrows, isDelphiQuestion);
				let columnEditenabled = !element.editorColumnsLocked()
				let rowEditenabled = !element.editorRowsLocked()
				getActionRow("Columns", "<span class='glyphicon glyphicon-plus'></span>", "addColumn(false)", "<span class='glyphicon glyphicon-minus'></span>", "removeColumn(false)", columnEditenabled);
				getActionRow("Rows", "<span class='glyphicon glyphicon-plus'></span>", "addRow(false)", "<span class='glyphicon glyphicon-minus'></span>", "removeRow(false)", rowEditenabled);
				getChoosePropertiesRow("Style", "SingleChoice,MultipleChoice", false, false, $(e).find("input[name^='single']").val() == 'true' ? "SingleChoice" : "MultipleChoice");
				getCheckPropertiesRow("Interdependency", $(e).find("input[name^='interdependent']").val() == 'true');
				getChoosePropertiesRow("Order", "Original,Alphabetical,Random", false, false,  parseInt($(e).find("input[name^='order']").val()));
				getMinMaxPropertiesRow("NumberOfAnsweredRows", 0, null, $(e).find("input[name^='rowsmin']").val(), $(e).find("input[name^='rowsmax']").val())
				getChoosePropertiesRow("Size", "fitToContent,fitToPage,manualColumnWidth", false, true, parseInt($(e).find("input[name^='tabletype']").val()));
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);
				getVisibilityRow(false, !isDelphiQuestion);
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				getReadOnlyAndHiddenRow(e)
				getRegistrationFormRow($(e).find("input[name^='attribute']").val(), $(e).find("input[name^='nameattribute']").val());

				if (isDelphi)
				{
					adaptDelphiControls(element);
				}
			} else if ($(e).hasClass("matrix-header"))
			{
				const element = parent.getChild($(e).attr("data-id"));

				var id = $(e).attr("data-id");
				var text = $("textarea[name^='text" + id + "']").first().text();
				
				if ($(e).hasClass("firstCell"))
				{
					text =  $("textarea[name^='firstCellText" + id + "']").first().text();
					getTextPropertiesRow("Text", text, true);
				} else {
					
					var shortname = $("input[name^='shortname" + id + "']").first().val();

					getTextPropertiesRow("Text", text, true);

					if ($(e).closest("thead").length == 0)
					{
						getCheckPropertiesRow("Mandatory", $("input[name^='optional" + id + "']").val() == 'false');
						getVisibilityRow(false);
					}

					getAdvancedPropertiesRow();
					getTextPropertiesRow("Identifier", shortname, false);
					
					
					if ($(e).closest("thead").length == 0)
					{
						// matrix question
						if (isQuiz)
						{
							getQuizPropertiesRow(parent);
							getQuizPropertiesContent(parent);
						}

						$(e).closest("tr").find("td").addClass("highlightedquestion");
					} else {
						// matrix answer
						if (isQuiz)
						{
							var index = $(e).index();
							var table = $(e).closest("table");
							$(table).find("tbody").find("tr").each(function(){
								$($(this).find("td")[index-1]).addClass("highlightedquestion");
							});
						
							getQuizPropertiesRow();
							getQuizPropertiesContentMatrixAnswer(parent, element, index);
						}
					}
				}

				_actions.ChildSelected(true);

				_actions.CopyEnabled(false);
				_actions.CutEnabled(false);
				_actions.MoveUpEnabled(false);
				_actions.MoveDownEnabled(false);

				if (isDelphi) {
					adaptDelphiChildControls(element, parent);
				}
			} else if ($(e).hasClass("mytableitem"))
			{
				const isDelphiQuestion = $(e).find("input[name^='delphiquestion']").val() == 'true';
				if (isDelphi)
				{
					getCheckPropertiesRow("DelphiQuestion", isDelphiQuestion);
					getCheckPropertiesRow("ShowExplanationBox", $(e).find("input[name^='explanationbox']").val() == 'true');
				}
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				
				var rows = $(e).find(".tabletable").first().find("tr").length-1;
				var mandatoryrows = $(e).find("th[data-optional=false]").length;
				getCheckPropertiesRow("Mandatory", rows == mandatoryrows, isDelphiQuestion);
				let columnEditenabled = !element.editorColumnsLocked()
				let rowEditenabled = !element.editorRowsLocked()
				getActionRow("Columns", "<span class='glyphicon glyphicon-plus'></span>", "addColumn(false)", "<span class='glyphicon glyphicon-minus'></span>", "removeColumn(false)", columnEditenabled);
				getActionRow("Rows", "<span class='glyphicon glyphicon-plus'></span>", "addRow(false)", "<span class='glyphicon glyphicon-minus'></span>", "removeRow(false)", rowEditenabled);
			
				getChoosePropertiesRow("Size", "fitToContent,fitToPage,manualColumnWidth", false, true, parseInt($(e).find("input[name^='tabletype']").val()));
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);

				getVisibilityRow(false, !isDelphiQuestion);
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				getReadOnlyAndHiddenRow(e)
				getRegistrationFormRow($(e).find("input[name^='attribute']").val(), $(e).find("input[name^='nameattribute']").val());
				
				if (isDelphi)
				{
					adaptDelphiControls(element);
				}
			} else if ($(e).hasClass("table-header"))
			{
				var parent = _elements[$(e).closest(".survey-element").attr("data-id")];
				const element = parent.getChild($(e).attr("data-id"));

				var text = $(e).html();

				if ($(e).hasClass("firstCell"))
				{
					text =  $("textarea[name^='firstCellText" + id + "']").first().text();
					getTextPropertiesRow("Text", text, true);
				} else {
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
				}

				_actions.ChildSelected(true);

				_actions.CopyEnabled(false);
				_actions.CutEnabled(false);
				_actions.MoveUpEnabled(false);
				_actions.MoveDownEnabled(false);

				if (isDelphi) {
					adaptDelphiChildControls(element, parent);
				}
			} else if ($(e).hasClass("complextableitem"))
			{
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				getActionRow("Columns", "<span class='glyphicon glyphicon-plus'></span>", "addColumn(false)", "<span class='glyphicon glyphicon-minus'></span>", "removeColumn(false)");
				getActionRow("Rows", "<span class='glyphicon glyphicon-plus'></span>", "addRow(false)", "<span class='glyphicon glyphicon-minus'></span>", "removeRow(false)");
				getChoosePropertiesRow("Size", "fitToContent,fitToPage", false, true, parseInt($(e).find("input[name^='size']").val()));
				getCheckPropertiesRow("ShowHeadersAndBorders", $(e).find("input[name^='showHeadersAndBorders']").first().val() == "true", false);
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);
				getVisibilityRow(false, true);
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);				
			} else if ($(e).hasClass("headercell"))
			{	
				const id = $(e).attr("data-id");
				const row = $(e).closest("tr").index();
				const column = $(e).index();
				_actions.DeleteEnabled(false);
				_actions.MoveUpEnabled(false);
				_actions.MoveDownEnabled(false);
				_actions.CopyEnabled(false);
				_actions.CutEnabled(false);
				if (row == 0 && column == 0) {
					getFirstCellPropertiesRow(id);
				} else if (row == 0) { // column header
					var col = $(e).index();
					$(e).closest("tbody").find("tr").each(function(index) {
						if (index > 0) {
							$($(this).find("td[data-col='" + col + "']")).addClass("highlightedquestion"); //column-1 as first cell is a th
						}
					});
					getCellHeaderPropertiesRow(id, false);
				} else { // row header
					$(e).closest("tr").find("td").addClass("highlightedquestion");
					getCellHeaderPropertiesRow(id, true);
				}
			} else if ($(e).hasClass("cell"))
			{	
				var id = $(e).attr("data-id");
				var tableId = $(e).closest(".survey-element").attr("data-id");
				
				if (id.length == 0) {
					//empty cell: create new "empty" child
					element = _elements[tableId];
					const cell =  getBasicElement("ComplexTableItem", true, "", null, false);
					cell.row = $(e).closest("tr").index();
					cell.column = parseInt($(e).attr("data-col"));
					cell.cellType = 0; //empty
					element.childElements.push(newComplexTableItemViewModel(cell));
					id = cell.id;
				}
				_actions.DeleteEnabled(false);
				_actions.MoveUpEnabled(false);
				_actions.MoveDownEnabled(false);
				_actions.CopyEnabled(false);
				_actions.CutEnabled(false);
				getCellPropertiesRow(id);
			} else if ($(e).hasClass("dateitem"))
			{
				const isDelphiQuestion = $(e).find("input[name^='delphiquestion']").val() == 'true';
				if (isDelphi)
				{
					getCheckPropertiesRow("DelphiQuestion", isDelphiQuestion);
					getCheckPropertiesRow("ShowExplanationBox", $(e).find("input[name^='explanationbox']").val() == 'true');
				}
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				getCheckPropertiesRow("Mandatory", $(e).find("input[name^='optional']").val() == 'false', isDelphiQuestion);
				getMinMaxPropertiesRow("Values", 0, null, $(e).find("input[name^='min']").val(), $(e).find("input[name^='max']").val())
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);
				getVisibilityRow(false, !isDelphiQuestion);
				
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				getReadOnlyAndHiddenRow(e)
				getRegistrationFormRow($(e).find("input[name^='attribute']").val(), $(e).find("input[name^='nameattribute']").val());
								
				if (isQuiz)
				{
					getQuizPropertiesRow();
					getQuizPropertiesContent();
				}
				if (isDelphi)
				{
					adaptDelphiControls(element);
				}
			} else if ($(e).hasClass("timeitem"))
			{
				const isDelphiQuestion = $(e).find("input[name^='delphiquestion']").val() == 'true';
				if (isDelphi)
				{
					getCheckPropertiesRow("DelphiQuestion", isDelphiQuestion);
					getCheckPropertiesRow("ShowExplanationBox", $(e).find("input[name^='explanationbox']").val() == 'true');
				}
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				getCheckPropertiesRow("Mandatory", $(e).find("input[name^='optional']").val() == 'false', isDelphiQuestion);
				getMinMaxPropertiesRow("Values", 0, null, $(e).find("input[name^='min']").val(), $(e).find("input[name^='max']").val())
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);
				getVisibilityRow(false, !isDelphiQuestion);
				
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				getReadOnlyAndHiddenRow(e)
				getRegistrationFormRow($(e).find("input[name^='attribute']").val(), $(e).find("input[name^='nameattribute']").val());
				
				if (isDelphi)
				{
					adaptDelphiControls(element);
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
				
				if (isAdmin) {
					getChoosePropertiesRow("MaximumFileSize", "1,2,5,10", false, false, $(e).find("input[name^='maxFileSize']").val());
				}
				
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
				getReadOnlyAndHiddenRow(e)
				getRegistrationFormRow($(e).find("input[name^='attribute']").val(), $(e).find("input[name^='nameattribute']").val());
			} else if ($(e).hasClass("regexitem"))
			{
				const isDelphiQuestion = $(e).find("input[name^='delphiquestion']").val() == 'true';
				if (isDelphi)
				{
					getCheckPropertiesRow("DelphiQuestion", isDelphiQuestion);
					getCheckPropertiesRow("ShowExplanationBox", $(e).find("input[name^='explanationbox']").val() == 'true');
				}
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				getTextPropertiesRow("RegularExpression", $(e).find("input[name^='regex']").first().val(), false);
				getCheckPropertiesRow("Mandatory", $(e).find("input[name^='optional']").val() == 'false', isDelphiQuestion);
				getChoosePropertiesRow("Rows", "1,2,3,4,5,10,20,30,40,50", false, false, $(e).find("input[name^='rows']").val());
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);
				getVisibilityRow(false, !isDelphiQuestion);
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				getReadOnlyAndHiddenRow(e)
				getCheckPropertiesRow("Unique", $(e).find("input[name^='unique']").val() == 'true');
				getCheckPropertiesRow("Comparable", $(e).find("input[name^='comparable']").val() == 'true');
				getCheckPropertiesRow("Password", $(e).find("input[name^='password']").val() == 'true');
				getRegistrationFormRow($(e).find("input[name^='attribute']").val(), $(e).find("input[name^='nameattribute']").val());
				
				if (isDelphi)
				{
					adaptDelphiControls(element);
				}
			} else if ($(e).hasClass("formulaitem"))
			{
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				getFormulaPropertiesRow($(e).find("input[name^='formula']").first().val());
				getChoosePropertiesRow("DecimalPlaces", ",1,2,3,4,5,6,7,8,9,10", false, false, $(e).find("input[name^='decimalplaces']").val());
				getMinMaxPropertiesRow("Values", null, null, $(e).find("input[name^='min']").val(), $(e).find("input[name^='max']").val())
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);
				getVisibilityRow(false);
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				getReadOnlyAndHiddenRow(e)

				if (isDelphi)
				{
					adaptDelphiControls(element);
				}
			} else if ($(e).hasClass("formulaitem"))
			{
				const isDelphiQuestion = $(e).find("input[name^='delphiquestion']").val() == 'true';
				if (isDelphi)
				{
					getCheckPropertiesRow("DelphiQuestion", isDelphiQuestion);
					getCheckPropertiesRow("ShowExplanationBox", $(e).find("input[name^='explanationbox']").val() == 'true');
				}
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				getTextPropertiesRow("Formula", $(e).find("input[name^='formula']").first().val(), false);
				getChoosePropertiesRow("DecimalPlaces", ",1,2,3,4,5,6,7,8,9,10", false, false, $(e).find("input[name^='decimalplaces']").val());
				getMinMaxPropertiesRow("Values", null, null, $(e).find("input[name^='min']").val(), $(e).find("input[name^='max']").val())
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);
				getVisibilityRow(false);
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				getReadOnlyAndHiddenRow(e)

				if (isDelphi)
				{
					adaptDelphiControls(element);
				}
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
				getReadOnlyAndHiddenRow(e)
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
				const isDelphiQuestion = $(e).find("input[name^='delphiquestion']").val() == 'true';
				if (isDelphi)
				{
					getCheckPropertiesRow("DelphiQuestion", isDelphiQuestion);
					getChoosePropertiesRow("DelphiChartType", "Bar,Column,Line,Pie,Radar,Scatter", false, true, $(e).find("input[name^='delphicharttype']").val(), false);
					getCheckPropertiesRow("ShowExplanationBox", $(e).find("input[name^='explanationbox']").val() == 'true');
				}
				getTextPropertiesRow("Text", $(e).find("textarea[name^='text']").first().text(), true);
				
				var rows = $(e).find(".ratingtable").first().find("tr").length;
				var mandatoryrows = $(e).find(".hiddenratingquestions").find("input[name^='questionoptional'][value=false]").length;
				getCheckPropertiesRow("Mandatory", rows == mandatoryrows, isDelphiQuestion);
				let editenabled = !element.editorRowsLocked()
				getActionRow("Questions", "<span class='glyphicon glyphicon-plus'></span>", "addRow(false)", "<span class='glyphicon glyphicon-minus'></span>", "removeRow(false)", editenabled);
				getChoosePropertiesRow("IconType", "Stars,Circles,Hearts", false, false, parseInt($(e).find("input[name^='iconType']").val()));
				getNumberPropertiesRow("NumIcons", $(e).find("input[name^='numIcons']").val());
				getTextPropertiesRow("Help", $(e).find("textarea[name^='help']").first().text(), true);
				getVisibilityRow(false, !isDelphiQuestion);
				getAdvancedPropertiesRow();
				getTextPropertiesRow("Identifier", $(e).find("input[name^='shortname']").val(), false);
				if (isDelphi)
				{
					adaptDelphiControls(element);
				}
			} else if ($(e).hasClass("ratingquestion"))
			{				
				var parent = _elements[$(e).closest(".survey-element").attr("data-id")];
				const element = parent.getChild($(e).attr("data-id"));
				
				_actions.DeleteEnabled(true);

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

				if (isDelphi) {
					adaptDelphiChildControls(element, parent);
				}
			} else if ($(e).find(".gallery-image").length > 0)
			{
				getTextPropertiesRow("Text", $(e).find("textarea[name^='comment']").first().text(), true);
				getTextPropertiesRow("DescriptiveText", $(e).find("input[name^='desc']").first().val(), false, null, 200);
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
				
				if ($(this).hasClass("answertext") || $(this).hasClass("cell"))
				{
					hidevisibility = true;
					_actions.DeleteEnabled(false);
				}
				
				if ($(this).hasClass("cell"))
				{
					_actions.MoveUpEnabled(false);
					_actions.MoveDownEnabled(false);
					_actions.CopyEnabled(false);
					_actions.CutEnabled(false);
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
				_actions.CutEnabled(false);
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

function getReadOnlyAndHiddenRow(e) {
	const readonly = $(e).find("input[name^='readonly']").val() == 'true'
	const hidden = $(e).find("input[name^='hidden']").val() == 'true'
	getCheckPropertiesRow("ReadOnly", readonly);
	getCheckPropertiesRow("Hidden", hidden, !readonly)
}

var _elementProperties = new ElementProperties();

function propertiesFromPreviewIndex(index, event, data){
	if (data.Label() === "Columns"){
		if ($(_elementProperties.selectedelement).hasClass("complextableitem")) {
			let table = getElement();
			let child = table.getChild(index, 0);
			_elementProperties.showProperties($(".headercell[data-id='" + child.id() + "']"), event, false);
		} else {
			_elementProperties.showProperties($(_elementProperties.selectedelement).find(".matrix-header[scope=col], .table-header[scope=col]")[index], event, false);
		}
	} else if (data.Label() === "Rows"){
		if ($(_elementProperties.selectedelement).hasClass("complextableitem")) {
			let table = getElement();
			let child = table.getChild(0, index+1);
			_elementProperties.showProperties($(".headercell[data-id='" + child.id() + "']"), event, false);
		} else {
			_elementProperties.showProperties($(_elementProperties.selectedelement).find(".matrix-header[scope=row], .table-header[scope=row]")[index], event, false);
		}
	} else {
		_elementProperties.showProperties($(_elementProperties.selectedelement).find(".answertext, .rankingitemtext, .ratingquestion")[index], event, false);
	}
}

function propertiesFromSelect(el, event){
	if (el.value !== ""){
		_elementProperties.showProperties(document.getElementById(el.value), event, false);
	}
}

$(function() {
	ko.applyBindings(_elementProperties, $("#properties")[0]);
});
