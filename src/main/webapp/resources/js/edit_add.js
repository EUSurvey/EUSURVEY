function getNewElement(item)
{
	var element;
	
	if (item.hasClass("sectionitem"))
	{
		element = getBasicElement("Section", false, "Section Title", item.attr("id"), false);
		element.level = 1;
		element.tabTitle = "[Section]";
		updateComplexityScore("addSectionItem");
	} else if (item.hasClass("freetextitem"))
	{
		element = getBasicElement("FreeTextQuestion", true, "Free Text Question", item.attr("id"), true);
		element.isPassword = false;
		element.maxCharacters = "";
		element.minCharacters = "";
		element.isComparable = false;
		element.css = "freetext";
		element.numRows = 1;
		element.isDelphiQuestion = isDelphi;
		updateComplexityScore("addSimpleQuestion");
	} else if (item.hasClass("singlechoiceitem"))
	{
		element = getBasicElement("SingleChoiceQuestion", true, "Single Choice Question", item.attr("id"), true);
		element.maxChoices = 0;
		element.minChoices = 0;
		element.useRadioButtons = true;
		element.numColumns = 1;
		element.order = 0;
		element.possibleAnswers = [getBasicElement("PossibleAnswer", false, "Answer 1", null, false), getBasicElement("PossibleAnswer", false, "Answer 2", null, false)];
		element.orderedPossibleAnswers = element.possibleAnswers;
		element.isDelphiQuestion = isDelphi;
		updateComplexityScore("addChoiceQuestion");
		updateListSummary(item.attr("id"),"init", 2);
	} else if (item.hasClass("rankingitem"))
	{
		element = getBasicElement("RankingQuestion", true, "Ranking Question", item.attr("id"), true);
		element.childElements = [
			getBasicElement("RankingItem", false, "Ranking Item 1", null, false),
			getBasicElement("RankingItem", false, "Ranking Item 2", null, false),
			getBasicElement("RankingItem", false, "Ranking Item 3", null, false),
			];
		element.isDelphiQuestion = isDelphi;
		updateComplexityScore("addSimpleQuestion");
	} else if (item.hasClass("multiplechoiceitem"))
	{
		element = getBasicElement("MultipleChoiceQuestion", true, "Multiple Choice Question", item.attr("id"), true);
		element.maxChoices = 0;
		element.minChoices = 0;
		element.useCheckboxes = true;
		element.noNegativeScore = false;
		element.numColumns = 1;
		element.order = 0;
		element.possibleAnswers = [getBasicElement("PossibleAnswer", false, "Answer 1", null, false), getBasicElement("PossibleAnswer", false, "Answer 2", null, false)];
		element.orderedPossibleAnswers = element.possibleAnswers;
		element.isDelphiQuestion = isDelphi;	
		updateComplexityScore("addChoiceQuestion");
		updateListSummary(item.attr("id"),"init", 2);
	} else if (item.hasClass("numberitem"))
	{
		element = getBasicElement("NumberQuestion", true, "Number-Slider Question", item.attr("id"), true);
		element.decimalPlaces = 0;
		element.unit = "";
		element.min = null;
		element.max = null;
		element.css = "number";
		element.isDelphiQuestion = isDelphi;	
		updateComplexityScore("addSimpleQuestion");
	} else if (item.hasClass("matrixitem"))
	{
		element = getBasicElement("Matrix", true, "Matrix", item.attr("id"), true);
		element.tableType = 0;
		element.isSingleChoice = true;
		element.isInterdependent = false;
		element.order = 0;
		element.minRows = "";
		element.maxRows = "";
		element.widths = "";
		element.completeWidth = "auto";
		element.rows = 4;
		element.columns = 4;
		element.childElements = [
		                         getBasicElement("Text", true, "empty", null, false),
		                         getBasicElement("Text", true, "Answer 1", null, false),
		                         getBasicElement("Text", true, "Answer 2", null, false),
		                         getBasicElement("Text", true, "Answer 3", null, false),
		                         getBasicElement("Text", true, "Question 1", null, true),
		                         getBasicElement("Text", true, "Question 2", null, true),
		                         getBasicElement("Text", true, "Question 3", null, true)
		                        ];
		element.answers = getMatrixAnswers(element);
		element.questions = getMatrixQuestions(element);
		element.questionsOrdered = element.questions;
		element.dependentElementsStrings = [""];
		element.isDelphiQuestion = isDelphi;	
		updateComplexityScore("addTableOrMatrixQuestion");
	} else if (item.hasClass("mytableitem"))
	{
		element = getBasicElement("Table", true, "Table", item.attr("id"), true);
		element.tableType = 0;
		element.order = 0;
		element.widths = "";
		element.completeWidth = "auto";
		element.rows = 4;
		element.columns = 4;
		element.childElements = [
		                         getBasicElement("Text", true, "empty", null, false),
		                         getBasicElement("Text", true, "A", null, false),
		                         getBasicElement("Text", true, "B", null, false),
		                         getBasicElement("Text", true, "C", null, false),
		                         getBasicElement("Text", true, "1", null, true),
		                         getBasicElement("Text", true, "2", null, true),
		                         getBasicElement("Text", true, "3", null, true)
		                        ];
		element.answers = getMatrixAnswers(element);
		element.questions = getMatrixQuestions(element);
		element.dependentElementsStrings = [""];
		element.isDelphiQuestion = isDelphi;	
		updateComplexityScore("addTableOrMatrixQuestion");
	} else if (item.hasClass("dateitem"))
	{
		element = getBasicElement("DateQuestion", true, "Date", item.attr("id"), true);
		element.min = null;
		element.minString = null;
		element.max = null;
		element.maxString = null;
		element.css = "date";
		element.isDelphiQuestion = isDelphi;	
		updateComplexityScore("addSimpleQuestion");
	} else if (item.hasClass("timeitem"))
	{
		element = getBasicElement("TimeQuestion", true, "Time", item.attr("id"), true);
		element.min = null;
		element.minString = null;
		element.max = null;
		element.maxString = null;
		element.css = "time";
		element.isDelphiQuestion = isDelphi;	
		updateComplexityScore("addSimpleQuestion");
	} else if (item.hasClass("textitem"))
	{
		element = getBasicElement("Text", false, "Text", item.attr("id"), false);
		updateComplexityScore("addSimpleItem");
	} else if (item.hasClass("imageitem"))
	{
		element = getBasicElement("Image", false, "Image", item.attr("id"), false);
		element.title = ""
		element.optional = true;
		element.scale = 100;
		element.longdesc = "";
		element.width = 128;
		element.align = "left";
		element.url = contextpath + "/resources/images/photo_scenery.png";
		element.filename = "";
		updateComplexityScore("addSimpleItem");
	} else if (item.hasClass("ruleritem"))
	{
		element = getBasicElement("Ruler", false, "Ruler", item.attr("id"), false);
		element.color = "#004F98";
		element.style = "solid";
		element.height = 1;
		updateComplexityScore("addSimpleItem");
	} else if (item.hasClass("uploaditem"))
	{
		element = getBasicElement("Upload", true, "Please upload your file(s)", item.attr("id"), true);
		//element.help = "The maximum file size is 1 MB";
		
		if (isOPC)
		{
			element.extensions = "pdf;txt;doc;docx;odt;rtf";
		}
		
		updateComplexityScore("addSimpleQuestion");
	} else if (item.hasClass("downloaditem"))
	{
		element = getBasicElement("Download", true, "Download", item.attr("id"), true);
		element.files = [];
		updateComplexityScore("addSimpleQuestion");
	} else if (item.hasClass("emailitem"))
	{
		element = getBasicElement("EmailQuestion", true, "Email", item.attr("id"), true);
		updateComplexityScore("addSimpleQuestion");
	} else if (item.hasClass("regexitem"))
	{
		element = getBasicElement("RegExQuestion", true, "RegEx Question", item.attr("id"), true);
		element.isPassword = false;
		element.maxCharacters = 0;
		element.minCharacters = 0;
		element.isComparable = false;
		element.css = "regex";
		element.numRows = 1;
		element.regex = "";
		element.isDelphiQuestion = isDelphi;	
		updateComplexityScore("addSimpleQuestion");
	} else if (item.hasClass("galleryitem"))
	{
		element = getBasicElement("GalleryQuestion", true, "Gallery", item.attr("id"), true);
		element.columns = 3;
		element.selection = false;
		element.limit = "";
		element.count = 0;
		element.css = "gallery";
		element.files = [];
		updateComplexityScore("addGalleryQuestion");
	} else if (item.hasClass("confirmationitem"))
	{
		element = getBasicElement("Confirmation", true, "I accept your Terms", item.attr("id"), true);
		element.optional = false;
		element.confirmationtext = "";
		element.confirmationlabel = "Show";
		element.usetext = false;
		element.useupload = false;
		updateComplexityScore("addSimpleQuestion");	
	} else if (item.hasClass("ratingitem"))
	{
		element = getBasicElement("RatingQuestion", true, "Rating", item.attr("id"), true);
		element.numIcons = 5;
		element.iconType = 0;
		element.confirmationlabel = "Show";
		element.childElements = [getBasicElement("Text", false, "Question 1", null, false), getBasicElement("Text", false, "Question 2", null, false)];
		element.isDelphiQuestion = isDelphi;	
		updateComplexityScore("addSimpleQuestion");	
	} else if (item.hasClass("countriesitem") || item.hasClass("languagesitem") || item.hasClass("dgsitem") || item.hasClass("unsitem") || item.hasClass("agenciesitem")) {
		item.addClass("singlechoiceitem");

		// use original title for "Predefined" elements and make sure that special characters don't break anything; otherwise, use "Single Choice Question"
		let questionTitle = $('<span>').text(item.attr("data-original-text")).html().trim() || "Single Choice Question";
		
		element = getBasicElement("SingleChoiceQuestion", true, questionTitle, item.attr("id"), true);
		element.maxChoices = 0;
		element.minChoices = 0;
		element.useRadioButtons = true;
		element.numColumns = 1;
		element.order = 1;
		element.isDelphiQuestion = isDelphi;	
		element.possibleAnswers = [];
		
		updateComplexityScore("addChoiceQuestion");
		updateListSummary(updateListSummary,"init", 32);	
		
		var url = "/utils/euCountries";
		if (item.hasClass("languagesitem"))
		{
			url = "/utils/euLanguages";
		} else if (item.hasClass("dgsitem"))
		{
			url = "/utils/euDGs";
		} else if (item.hasClass("agenciesitem"))
		{
			url = "/utils/euAgencies";
		} else if (item.hasClass("unsitem"))
		{
			url = "/utils/unCountries";
			element.useRadioButtons = false;
			element.subType = "unCountries";
			element.displayMode = 2;
		} else {
			element.subType = "euCountries";
			element.displayMode = 2;
		}
		
		$.ajax({type: "GET",
			url: contextpath + url,
			data: {lang: surveyLanguage},
			async: false,
		    success :function(result)
		    {
		    	$.each(result, function(key, data){
		    		var newpa = getBasicElement("PossibleAnswer", false, data, null, false);
		    		newpa.shortname = key;
		    		element.possibleAnswers[element.possibleAnswers.length] = newpa;
		    	});		   
		    }
		 });		
	}
	
	if (item.hasClass("quiz"))
	{
		element.scoring = 1;
		element.points = 1;
	} else {
		element.scoring = 0;
	}
	
	return element;
}

function addNewElement(item, element)
{
	item.attr("data-original-text", item.text()).empty().removeClass('toolboxitem draggable ui-draggable ui-draggable-handle').addClass('survey-element').css("height","").css("width","");
	
	if (element == null)
	{
		var id = getNewId();
		item.attr("id", id).attr("data-id", id);
		element = getNewElement(item);
	}
	
	elemcounter++;
	
	var model = getElementViewModel(element);
	var model = addElementToContainer(model, item, true, false);	
	_elements[model.id()] = model;
	
	if (item.hasClass("confirmationitem")) {
		item.find(".questiontitle").prepend("<input disabled='disabled' type='checkbox' />")
	}
	
//	if (!element.optional)
//	{
//		item.find(".questiontitle").prepend("<span class='mandatory'>*</span>")
//	}
	
	addElementHandler(item);
	checkContent();
}

function addElementHandler(item)
{
	item.click(function(e){
		_elementProperties.showProperties(this, e, false);
	});
	item.dblclick(function(e){
		_elementProperties.showProperties(this, e, true);
	});
	
	$(item).find(".matrix-header, .table-header").click(function(e){
		_elementProperties.showProperties(this, e, false);
	});	
	$(item).find(".matrix-header, .table-header").dblclick(function(e){
		_elementProperties.showProperties(this, e, true);
	});
	
	$(item).find(".possibleanswerrow").find(".answertext").click(function(e){
		_elementProperties.showProperties(this, e, false);
	});	
	$(item).find(".possibleanswerrow").find(".answertext").dblclick(function(e){
		_elementProperties.showProperties(this, e, true);
	});	

	$(item).find(".rankingitem-form-data").click(function(e) {
		_elementProperties.showProperties($(this).find(".rankingitemtext")[0], e, false);
	});	

	$(item).find(".gallery-table").find("td").click(function(e) {
		_elementProperties.showProperties(this, e, false);
    });
	
	$(item).find(".ratingtable").find(".ratingquestion").click(function(e) {
		_elementProperties.showProperties(this, e, false);
    });
	
	$(item).hover(
	  function() {
		    $(this).addClass("survey-element-hovered");
		  }, function() {
		    $(this).removeClass("survey-element-hovered");
		  }
		);
	
	$(item).find(".possibleanswerrow").find("td:has('.answertext')").hover(
			  function() {
				  	$(this).closest(".survey-element").removeClass("survey-element-hovered");
				    $(this).addClass("survey-element-hovered");
				  }, function() {
				    $(this).removeClass("survey-element-hovered");
				  }
				);
	$(item).find(".rankingitem-form-data").hover(
			function() {
				$(this).closest(".survey-element").removeClass(
						"survey-element-hovered");
				$(this).addClass("survey-element-hovered");
			}, function() {
				$(this).removeClass("survey-element-hovered");
			});

	$(item).find(".matrix-header, .table-header").hover(
			  function() {
				  	$(this).closest(".survey-element").removeClass("survey-element-hovered");
				    $(this).addClass("survey-element-hovered");
				  }, function() {
				    $(this).removeClass("survey-element-hovered");
				  }
				);	
	
	$(item).find(".ratingquestion").hover(
			  function() {
				  	$(this).closest(".survey-element").removeClass("survey-element-hovered");
				    $(this).addClass("survey-element-hovered");
				  }, function() {
				    $(this).removeClass("survey-element-hovered");
				  }
				);
	
}

function getMatrixAnswers(element)
{
	var result = [];
	for (var i = 1; i < element.columns; i++)
	{
		result[result.length] = element.childElements[i];
	}
	return result;
}

function getMatrixQuestions(element)
{
	var result = [];
	for (var i = element.columns; i < element.childElements.length; i++)
	{
		result[result.length] = element.childElements[i];
	}
	return result;
}

function getBasicElement(type, isquestion, title, id, addoptionalplaceholder)
{
	var element = {
			"title" : title,
			"type" : type,
			"shortname" : getNewShortname(),
			"uniqueId" : getNewId(),
			"id" : getNewId(),
			"css" : "",
			"isDependentMatrixQuestion" : false,
			"subType" : "",
			"displayMode" : 0
		}
	
	element.originalTitle = title;
	
	if (id != null)
	{
		element.id = id;
	}
	
	if (isquestion)
	{
		element.help = "";
		if (type == "Confirmation")
		{
			element.optional = false;
		} else {
			element.optional = true;
		}
		element.isAttribute = false;
		element.isUnique = false;
		element.attributeName = element.shortname;
		element.readonly = false;
		
		element.isDelphiQuestion = false;
		element.showExplanationBox = true;
	}
	
	if (type == "PossibleAnswer")
	{
		element.dependentElementsString = "";
	}
	
	return element;
}

var usedIDs = new Array();
var lastIdTried = 0;

function getNewShortname()
{
	if (usedIDs.length == 0)
	{
		$("input[name^='shortname']").each(function(){
			usedIDs[usedIDs.length] = $(this).val();
		});
		
		$("input[name^='pashortname']").each(function(){
			usedIDs[usedIDs.length] = $(this).val();
		});
		
		$("td").each(function(){
			if ($(this).attr("data-shortname"))
			usedIDs[usedIDs.length] = $(this).attr("data-shortname");
		});
	}
	
	//the 200000 are a security limit to prevent endless loops
	for (var i = lastIdTried + 1; i < 200000; i++)
	{
		var s = "[ID" + i + "]";
		var found = false;
		
		if (usedIDs.indexOf(s) > -1)
		{
			found = true;
		}
		
		if (!found)
		{
			lastIdTried = i;
			usedIDs[usedIDs.length] = s;
			return s;
		}
	}
}