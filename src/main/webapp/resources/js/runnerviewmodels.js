function getNiceHelp(help)
{
	if (help == null || help.trim().length == 0) return "<br />";

	return addIconToHelp(help);	
}

function toggleVisibility(span) {
	$(span).toggleClass("sr-only");
}

function addIconToHelp(help)
{
	return "<button type='button' aria-label='Help' onclick='toggleVisibility($(this).next().next())' class='unstyledbutton glyphicon glyphicon-question-sign focussable'></button><br />" +
		"<div class='questionhelp__text sr-only'>" + help + "</div>" +
		"";
}

function newFileViewModel(uid, name, comment, longdesc, cleanComment, width, desc)
{
	var viewModel = {};
	viewModel.uid =  ko.observable(uid == "null" ? "" : uid);
	viewModel.name = ko.observable(name == "null" ? "" : name);
	viewModel.comment = ko.observable(comment == "null" ? "" : comment);
	viewModel.longdesc = ko.observable(longdesc == "null" ? "" : longdesc);
	viewModel.desc = ko.observable(desc == "null" ? "" : desc);
	viewModel.cleanComment = ko.observable(cleanComment);
	viewModel.width = ko.observable(width);
		
	viewModel.copy = function()
	{
		var copy = {};
		
	    for (var prop in this) {
	        var propVal = this[prop];
	        if (ko.isObservable(propVal)) {
	            var val = propVal();
	            copy[prop] = ko.observable(val);
	        } else {
	        	copy[prop] = propVal;
	        }
	    }
	    
	    $.ajax({type: "GET",
			url: contextpath + "/noform/management/copyfile",
			data: {uid: copy.uid()},
			dataType: 'json',
			async: false,
		    success :function(result)
		    {
		    	copy.uid(result.newuid);	    	
		    }
		 });
		
	    copy.originalId = this.uid();
	    
	    return copy;
	}
	
	return viewModel;
}

function newFilesViewModel(files)
{
	var viewModel = ko.observableArray();
	
	if (files != null)
	for (var i = 0; i < files.length; i++)
	{
		if (files[i].name != null)
		viewModel.push(newFileViewModel(files[i].uid, files[i].name, files[i].comment, files[i].longdesc, files[i].cleanComment, files[i].width, files[i].description));
	}
	
	return viewModel;
}

function newMatrixItemViewModel(id, uniqueId, optional, shortname, readonly, title, originalTitle, isDependentMatrixQuestion, css, index, useAndLogic)
{
	var viewModel = newBasicViewModel();
	viewModel.type = 'matrixitem';
	viewModel.id =  ko.observable(id);
	viewModel.uniqueId = ko.observable(uniqueId);
	viewModel.optional = ko.observable(optional);
	viewModel.shortname = ko.observable(shortname);
	viewModel.readonly = ko.observable(readonly);
	viewModel.title = ko.observable(title);
	viewModel.originalTitle = ko.observable(originalTitle == null ? title : originalTitle);
	viewModel.isDependentMatrixQuestion = ko.observable(isDependentMatrixQuestion);
	viewModel.css = ko.observable(css);
	viewModel.originalIndex = ko.observable(index);
	viewModel.useAndLogic = ko.observable(useAndLogic);
	return viewModel;
}

function newMatrixItemsViewModel(items)
{
	var viewModel = ko.observableArray();
	for (var i = 0; i < items.length; i++)
	{
		viewModel.push(newMatrixItemViewModel(items[i].id, items[i].uniqueId, items[i].optional, items[i].shortname, items[i].readonly, items[i].title, items[i].originalTitle, items[i].isDependentMatrixQuestion, items[i].css, i, items[i].useAndLogic));
	}
	return viewModel;
}

function newRatingItemViewModel(id, uniqueId, optional, shortname, title, originalTitle, css)
{
	var viewModel = newBasicViewModel();
	viewModel.type = 'text';
	viewModel.id =  ko.observable(id);
	viewModel.uniqueId = ko.observable(uniqueId);
	viewModel.optional = ko.observable(optional);
	viewModel.shortname = ko.observable(shortname);
	viewModel.title = ko.observable(title);
	viewModel.originalTitle = ko.observable(originalTitle == null ? title : originalTitle);
	viewModel.css = ko.observable(css);
	return viewModel;
}

function newPossibleAnswerViewModel(id, uniqueId, shortname, dependentElementsString, title, scoring, ecfScore, ecfProfile, exclusive)
{
	var viewModel = newBasicViewModel();
	viewModel.type = 'PossibleAnswer';
	viewModel.id =  ko.observable(id);
	viewModel.uniqueId = ko.observable(uniqueId);
	viewModel.shortname = ko.observable(shortname);
	viewModel.dependentElementsString = ko.observable(dependentElementsString);
	viewModel.title = ko.observable(title);
	viewModel.originalTitle = ko.observable(title);
	viewModel.scoring = newScoringViewModel(scoring);
	viewModel.ecfScore = ko.observable(ecfScore);
	viewModel.ecfProfile = ko.observable(ecfProfile);
	viewModel.exclusive = ko.observable(exclusive);
	viewModel.delphiAnswerCount = ko.observable(0);
	viewModel.useSavedDisplayMode = ko.observable(false);
	
	viewModel.titleForDisplayMode = function(displayMode)
	{
		if(this.useSavedDisplayMode()) return this.title() + " (" + this.delphiAnswerCount() + ")";

		switch (displayMode)
		{
			case 0:
				return this.title();
			case 1:
				return this.shortname();
			case 2:
				return this.shortname() + " - " + this.title();
			case 3:
				return this.title() + " (" + this.shortname() + ")";
		}
	}
	
	return viewModel;
}

function newPossibleAnswersViewModel(answers)
{
	var viewModel = ko.observableArray();
	for (var i = 0; i < answers.length; i++)
	{
		viewModel.push(newPossibleAnswerViewModel(answers[i].id, answers[i].uniqueId, answers[i].shortname, answers[i].dependentElementsString, answers[i].title, answers[i].scoring, answers[i].ecfScore, answers[i].ecfProfile, answers[i].exclusive));
	}
	return viewModel;
}

function newPossibleAnswersByColumnsViewModel(list)
{
	var viewModel = ko.observableArray();
	for (var i = 0; i < list.length; i++)
	{
		var answers = list[i];
		var innerlist = newPossibleAnswersViewModel(answers);
		viewModel.push(innerlist);
	}
	return viewModel;
}

function newScoringWrapper(element)
{
	var viewModel = {};
	
	viewModel.isViewModel = true;
	viewModel.id = ko.observable(element.id());
	viewModel.title = ko.observable("");
	viewModel.points = ko.observable(0);
	viewModel.scoring = element;
	
	return viewModel;
}

function newScoringViewModel(element)
{
	var viewModel = {};
	
	viewModel.isViewModel = true;
	
	if (element != null)
	{
		if (element.hasOwnProperty("isViewModel") && element.isViewModel)
		{
			viewModel.id = ko.observable(element.id());
			viewModel.type = ko.observable(element.type());
			viewModel.value = ko.observable(element.value());
			viewModel.value2 = ko.observable(element.value2());
			viewModel.feedback = ko.observable(element.feedback());
			viewModel.min = ko.observable(element.min());
			viewModel.max = ko.observable(element.max());
			viewModel.minDate = ko.observable(element.minDate());
			viewModel.maxDate = ko.observable(element.maxDate());
			viewModel.points = ko.observable(element.points());
			viewModel.position = ko.observable(element.position());
			viewModel.correct = ko.observable(element.correct());
		} else {
			viewModel.id = ko.observable(element.id);
			viewModel.type = ko.observable(element.type);
			viewModel.value = ko.observable(element.value);
			viewModel.value2 = ko.observable(element.value2);
			viewModel.feedback = ko.observable(element.feedback);
			viewModel.min = ko.observable(element.min);
			viewModel.max = ko.observable(element.max);
			viewModel.minDate = ko.observable(element.minDate);
			viewModel.maxDate = ko.observable(element.maxDate);
			viewModel.points = ko.observable(element.points);
			viewModel.position = ko.observable(element.position);
			viewModel.correct = ko.observable(element.correct);
		}
	} else {
		viewModel.id = ko.observable("");
		viewModel.type = ko.observable(0);
		viewModel.value = ko.observable("");
		viewModel.value2 = ko.observable("");
		viewModel.feedback = ko.observable("");
		viewModel.min = ko.observable(0);
		viewModel.max = ko.observable(0);
		viewModel.minDate = ko.observable();
		viewModel.maxDate = ko.observable();
		viewModel.points = ko.observable(0);
		viewModel.position = ko.observable(0);
		viewModel.correct = ko.observable(false);
	}
	
	viewModel.typeAsOption = function(){
		switch(this.type())
		{
			case -1:
				return "other";
			case 0:
				return "equalTo";
			case 1:
				return "lessThan";
			case 2:
				return "lessThanOrEqualTo";
			case 3:
				return "greaterThan";
			case 4:
				return "greaterThanOrEqualTo";
			case 5:
				return "between";
			case 6:
				return "contains";
			case 7:
				return "matches";		
			case 8:
				return "empty";
		}
		
		return "";
	}
	
	return viewModel;
}

function notAViewModel(el){
	//Set all .isViewModel properties of this el all childs to false
	if (el != null && typeof el == "object"){
		if (el.isViewModel){
			el.isViewModel = false;
		}
		for (let propKey in el) {
			notAViewModel(el[propKey]);
		}
	}
}

function createNewDelphiBasicViewModel() {
	const self = this;
	self.delphiTableEntries = ko.observableArray();
	self.delphiTableLoading = ko.observable(false);
	self.delphiTableLimit = ko.observable(20);
	self.delphiTableNewComments = ko.observable(false);
	self.delphiTableOffset = ko.observable(0);
	self.delphiTableQuestionType = ko.observable("");
	self.delphiTableAnswerSorting = ko.computed(function () {
		switch (self.delphiTableQuestionType()) {
			case "SingleChoice":
			case "MultipleChoice":
				return true;
			default:
				return false;
		}
	});
	self.delphiTableShowQuestionHtml = ko.computed(function () {
		// used to decide whether Knockout's html or text binding should be used
		switch (self.delphiTableQuestionType()) {
			case "SingleChoice":
			case "MultipleChoice":
			case "Matrix":
			case "Ranking":
				// only allow HTML for these three question types, as others don't need it or potentially allow XSS
				return true;

			default:
				return false;
		}
	});
	self.delphiTableTotalEntries = ko.observable(0);
	self.delphiTableOrder = ko.observable("UpdateDesc");
	self.showExplanationBox = ko.observable(true);
}

function newBasicViewModel(element)
{
	const viewModel = new createNewDelphiBasicViewModel();
	
	viewModel.isViewModel = true;
	
	viewModel.scoringItems = ko.observableArray();
	viewModel.optional = ko.observable(true);
	viewModel.css = ko.observable(true);
	viewModel.maxDistanceExceeded = ko.observable(false);
	viewModel.useAndLogic = ko.observable(false);
	viewModel.median = ko.observable(0);
	viewModel.changedForMedian = ko.observable(false);
	
	viewModel.getScoringItem = function(id)
	{
		if (this.scoringItems != null)
		{
			for (var i = 0; i < this.scoringItems().length; i++)
			{
				if (this.scoringItems()[i].id() == id)
				return this.scoringItems()[i];
			}
		}
		return null;
	}
		
	if (element != null)
	{
		if (element.hasOwnProperty("isViewModel") && element.isViewModel)
		{
			viewModel.title = ko.observable(element.title());
			viewModel.originalTitle = ko.observable(element.originalTitle());
			viewModel.type = element.type;
			viewModel.id = ko.observable(element.id());
			viewModel.uniqueId = ko.observable(element.uniqueId());
			viewModel.shortname = ko.observable(element.shortname());
			viewModel.scoring = ko.observable(element.scoring());
			//viewModel.points = ko.observable(element.quizPoints());
			viewModel.locked = ko.observable(element.locked());
			viewModel.css = ko.observable(element.css());
			viewModel.optional = ko.observable(element.optional());
			viewModel.isDelphiQuestion = ko.observable(element.isDelphiQuestion());
			viewModel.useAndLogic = ko.observable(element.useAndLogic());
			viewModel.showExplanationBox = ko.observable(element.showExplanationBox());
			viewModel.delphiChartType = ko.observable(element.delphiChartType());
			viewModel.editorRowsLocked = ko.observable(element.editorRowsLocked());
			viewModel.editorColumnsLocked = ko.observable(element.editorColumnsLocked());
	
			if (element.scoringItems != null) {
				for (var i = 0; i < element.scoringItems.length; i++) {
					viewModel.scoringItems.push(newScoringViewModel(element.scoringItems[i]));
				}
			}
		} else {
		
			viewModel.title = ko.observable(element.title);
			viewModel.originalTitle = ko.observable(element.originalTitle);
			viewModel.type = element.type;
			viewModel.id = ko.observable(element.id);
			viewModel.uniqueId = ko.observable(element.uniqueId);
			viewModel.shortname = ko.observable(element.shortname);
			viewModel.scoring = ko.observable(element.scoring);
			viewModel.points = ko.observable(element.quizPoints);
			viewModel.locked = ko.observable(element.locked);
			viewModel.css = ko.observable(element.css);
			viewModel.optional = ko.observable(element.optional);
			viewModel.isDelphiQuestion = ko.observable(element.isDelphiQuestion);
			viewModel.useAndLogic = ko.observable(element.useAndLogic);
			viewModel.showExplanationBox = ko.observable(element.showExplanationBox);
			viewModel.delphiChartType = ko.observable(element.delphiChartType);
			viewModel.editorRowsLocked = ko.observable(element.editorRowsLocked);
			viewModel.editorColumnsLocked = ko.observable(element.editorColumnsLocked);

			if (element.scoringItems != null) {
				for (var i = 0; i < element.scoringItems.length; i++) {
					viewModel.scoringItems.push(newScoringViewModel(element.scoringItems[i]));
				}
			}
		
		}
	}
	
	viewModel.copy = function()
	{
		//Create a deep clone that transforms all observables to default js values
		let copy = ko.toJS(viewModel);


		notAViewModel(copy);

		//Use the viewmodels constructor to create a new viewmodel of the same type
		let created = viewModel.creator(copy, true);

		function copyValues(el){

			el.originalId = el.id();
			el.foreditor = true

			//Only set if the respective observables are present
			if (el.id) el.id(getNewId());
			if (el.uniqueId) el.uniqueId(getNewId());
			if (el.shortname) el.shortname(getNewShortname());
			if (el.locked) el.locked(false);

			for (let propKey in el) {
				//Go through all properties
				let propVal = el[propKey];

				if (ko.isObservable(propVal)){
					propVal = propVal(); //If prop is an observable retrieve the value
				}
				//If prop is an array go through all childs
				if (Array.isArray(propVal)){
					for (let i = 0; i < propVal.length; i++) {
						let childEl = propVal[i];
						if (childEl?.isViewModel){
							copyValues(childEl);
						}
					}
				} else {
					if (propVal?.isViewModel){
						copyValues(propVal);
					}
				}
			}
		}

		copyValues(created);

		created.replaceTriggers = function(dict)
		{
			function mapDependency(dependencyObservable){
				let deps = dependencyObservable();

				if (deps.length > 0){
					let dependentIds = deps.split(";");
					let mappedIds = dependentIds.map((oldId) => {
						if (oldId.length > 0 && dict.hasOwnProperty(oldId)){
							return dict[oldId]; //This id was also copied
						}
						return -1; //Mark all non copied elements for removal

					}).filter(id => id !== -1); //Filter out non copied ids

					let newDeps = "";
					if (mappedIds.length > 0){
						newDeps = mappedIds.join(";") + ";";
					}
					if (deps != newDeps) {
						dependencyObservable(newDeps);
					}
				}
			}

			if (this.type.indexOf("ChoiceQuestion") > 0)
			{
				this.possibleAnswers().forEach((posAns) => {
					mapDependency(posAns.dependentElementsString);
				})
			}
			else if (this.type == "Matrix") {

				this.dependentElementsStrings().forEach((dependencyObservable) => {
					mapDependency(dependencyObservable);
				});
			}
		};

		return created;
	}

	viewModel.creator = newBasicViewModel;

	return viewModel;
}

function newSectionViewModel(element)
{
	var viewModel = newBasicViewModel(element)
	viewModel.level = ko.observable(element.level);	
	viewModel.tabTitle = ko.observable(element.tabTitle);
	viewModel.order = ko.observable(element.order);

	viewModel.creator = newSectionViewModel;

	return viewModel;
}

function newTextViewModel(element)
{
	var viewModel = newBasicViewModel(element)
	viewModel.optional = ko.observable(element.optional);

	viewModel.creator = newTextViewModel;

	return viewModel;
}

function newImageViewModel(element)
{
	var viewModel = newBasicViewModel(element)
	viewModel.scale = ko.observable(element.scale);
	viewModel.width = ko.observable(element.width);	
	viewModel.align = ko.observable(element.align);	
	viewModel.url = ko.observable(element.url);	
	viewModel.filename = ko.observable(element.filename);	
	viewModel.longdesc = ko.observable(element.longdesc);
	viewModel.optional = true;	
	
	viewModel.usedwidth = function()
	{
		if (this.scale() != null && this.scale() != 0)
		{
			if ((this.scale() / 100 * this.width()) > 600)
			{
				return 600;
			} else {
				return (this.scale() / 100 * this.width());
			}			
		} else {
			return this.width();
		}
	}

	viewModel.creator = newImageViewModel;

	return viewModel;
}

function newRulerViewModel(element)
{
	var viewModel = newBasicViewModel(element)
	viewModel.height = ko.observable(element.height);
	viewModel.style = ko.observable(element.style);	
	viewModel.color = ko.observable(element.color);	
	viewModel.optional = true;	

	viewModel.creator = newRulerViewModel;

	return viewModel;
}

function newChoiceViewModel(element)
{
	var viewModel = newBasicViewModel(element)

	viewModel.subType = ko.observable(element.subType);
	viewModel.displayMode = ko.observable(element.displayMode);
	viewModel.possibleAnswers = newPossibleAnswersViewModel(element.possibleAnswers);
	viewModel.css = element.css;
	viewModel.optional = ko.observable(element.optional);
	viewModel.order = ko.observable(element.order);	
	
	viewModel.order.subscribe(function(newValue) {
		viewModel.orderedPossibleAnswers();
	});
	
	viewModel.isAttribute = ko.observable(element.isAttribute);
	viewModel.attributeName = ko.observable(element.attributeName);	
	viewModel.readonly = ko.observable(element.readonly);	
	viewModel.numColumns = ko.observable(element.numColumns);
	viewModel.help = ko.observable(element.help);
	viewModel.niceHelp = ko.observable(getNiceHelp(element.help));	
	
	viewModel.orderedPossibleAnswers = function(mobile, responsive)
	{
		var usecolumns = !mobile && (this.choiceType() == 'radio' || this.choiceType() == 'checkbox');
		//If it has the isEVoteList function and it is true
		let useInitial = viewModel.isEVoteList && viewModel.isEVoteList()
		useInitial |= this.order() == null

		if (!useInitial && this.order() == 1)
		{
			var dic = [];
			for (var i = 0; i < this.possibleAnswers().length; i++)
			{
				var item = [];
				item[0] = this.possibleAnswers()[i].title().stripHtml();
				item[1] = this.possibleAnswers()[i];
				dic[dic.length] = item;
			}
			
			dic.sort(sortFunction);
			
			var answers = [];
			for (var i = 0; i < dic.length; i++)
			{
				answers[answers.length] = dic[i][1];
			}

			return usecolumns ? sortByColumn(answers, responsive ? 3 : this.numColumns()) : answers;			
		} else if (!useInitial && this.order() == 2)
		{
			var answers = this.possibleAnswers().slice();
			answers = shuffle(answers);
			return usecolumns ? sortByColumn(answers, responsive ? 3 : this.numColumns()) : answers;	
		} else {
			return usecolumns ? sortByColumn(this.possibleAnswers(), responsive ? 3 : this.numColumns()) : this.possibleAnswers();	
		}
	}
	
	viewModel.orderedPossibleAnswersByRows = function(mobile, responsive)
	{
		var orderedPossibleAnswers = this.orderedPossibleAnswers(mobile, responsive);
		var result = [];
		var list = [];
		result[result.length] = list;
		
		var limit = this.numColumns();
		if (responsive && limit > 3) limit = 3;
		if (mobile) limit = 1;
		
		for (var i = 0; i < orderedPossibleAnswers.length; i++)
		{
			if (list.length >= limit)
			{
				list = [];
				result[result.length] = list;
			}
			list[list.length] = orderedPossibleAnswers[i];
		}
		return result;
	}
	
	viewModel.hasEmptyLastColumn = function()
	{
		if (this.numColumns() < 3) return false;
		
		var answersByRows = this.orderedPossibleAnswersByRows(false, false)[0];
		return answersByRows.length < this.numColumns() || answersByRows[this.numColumns()-1].id() == 'dummy';
	}
	
	viewModel.getChild = function(id)
	{		
		for (var j = 0; j < this.possibleAnswers().length; j++)
		{
			if (this.possibleAnswers()[j].id() == id)
			{
				return this.possibleAnswers()[j];
			}
		}		
	}
	
	viewModel.removeDependencies = function(id) {
		for (var j = 0; j < this.possibleAnswers().length; j++)
		{
			var deps = this.possibleAnswers()[j].dependentElementsString();
			if (deps != null && deps.indexOf(id) > -1)
			{
				this.possibleAnswers()[j].dependentElementsString(deps.replace(id + ";", ""));
			}
		}		
	}

	viewModel.creator = newChoiceViewModel;
	
	return viewModel;
}

function sortFunction(a, b) {
	try {
		return Intl.Collator().compare(a,b);
	} catch (e) {
		//ignore
	}
	
	//fallback
    if (a[0] === b[0]) {
        return 0;
    }
    else {
        return (a[0] < b[0]) ? -1 : 1;
    }
}

function newSingleChoiceViewModel(element)
{
	var viewModel = newChoiceViewModel(element)
	
	viewModel.single = true;
	viewModel.minChoices = ko.observable(0);
	viewModel.maxChoices = ko.observable(0);
	viewModel.choiceType = ko.observable(element.choiceType);
	viewModel.ecfCompetency = ko.observable(element.ecfCompetency);
	viewModel.maxDistance = ko.observable(element.maxDistance);

	viewModel.useRadioButtons = ko.pureComputed(function() {
		return viewModel.choiceType() === "radio"
	});
	viewModel.likert = ko.pureComputed(function() {
		return viewModel.choiceType() === "likert"
	});
	viewModel.useSelectBox = ko.pureComputed(function() {
		return viewModel.choiceType() === "select"
	});
	viewModel.useButtons = ko.pureComputed(function() {
		return viewModel.choiceType() === "buttons"
	});


	viewModel.styleType = ko.pureComputed(function() {
		switch (viewModel.choiceType()){
			case "radio":
				return "RadioButton";
			case "select":
				return "SelectBox";
			case "likert":
				return "LikertScale";
			case "buttons":
				return "Buttons"
		}
	});

	//Knockout when so this is not just a one time if but a constant rule
	//This rules removes the choice type likert if this is not a delphi
	ko.when(
		function (){
			return viewModel.likert() && !isdelphi
		}, //Then
		function (){
			viewModel.choiceType("radio")
		}
	)

	viewModel.creator = newSingleChoiceViewModel;

	return viewModel;
}

function newMultipleChoiceViewModel(element)
{
	var viewModel = newChoiceViewModel(element)
	
	viewModel.single = false;
	viewModel.minChoices = ko.observable(element.minChoices);
	viewModel.maxChoices = ko.observable(element.maxChoices);
	viewModel.choiceType = ko.observable(element.choiceType);
	viewModel.noNegativeScore = ko.observable(element.noNegativeScore);

	viewModel.useCheckboxes = ko.pureComputed(function() {
		return viewModel.choiceType() === "checkbox"
	});

	viewModel.useListBox = ko.pureComputed(function() {
		return viewModel.choiceType() === "list"
	});

	viewModel.isEVoteList = ko.pureComputed(function() {
		return viewModel.choiceType().startsWith("evote")
	});

	viewModel.choiceTypeWithEVote = function(template){
		if (viewModel.isEVoteList()){
			switch (template) {
				case "b": return "evote-brussels";
				case "i": return "evote-ispra";
				case "l": return "evote-luxembourg";
				case "o": return "evote-outside";
				case "p": return "evote-president";
			}
		} else {
			return viewModel.choiceType()
		}
	}

	viewModel.styleType = ko.pureComputed(function() {
		if (viewModel.choiceType() === "checkbox")
			return "CheckBox";
		if (viewModel.choiceType() === "list")
			return "ListBox";
		return "EVoteList";
	});

	//Knockout when so this is not just a one time if but a constant rule
	//This rules removes the choice type evote-... if this is not an eVote
	ko.when(
		function (){
			return viewModel.isEVoteList() && !isevote
		}, //Then
		function (){
			viewModel.choiceType("checkbox")
		}
	)

	viewModel.creator = newMultipleChoiceViewModel;

	return viewModel;
}

function newRankingViewModel(element)
{
	var viewModel = newBasicViewModel(element);

	viewModel.rankingItems = newRankingItemViewModelForEach(element.childElements || element.rankingItems, viewModel);
	viewModel.help = ko.observable(element.help);
	viewModel.niceHelp = ko.observable(getNiceHelp(element.help));
	viewModel.minItems = function() { return 2; };
	viewModel.itemIdtoUniqueIdLookup = {};
	$.each(element.childElements, (_, that) => {
		viewModel.itemIdtoUniqueIdLookup[that.id] = that.uniqueId;
	});
	viewModel.isAnswered = ko.observable(false);
	viewModel.css = ko.observable(element.css);
	viewModel.itemCount = function() { return viewModel.rankingItems().length;}
	viewModel.order = ko.observable(element.order);

	viewModel.order.subscribe(function() {
		viewModel.orderedRankingItems();
	});

	viewModel.initialOrder = null;

	viewModel.orderedRankingItems = ko.computed(function() {
		if (viewModel.foreditor || viewModel.initialOrder == null) {
			let result = viewModel.rankingItems.slice();

			if (viewModel.order() === 1) {
				result = result.sort((e1, e2) => e1.title().stripHtml().localeCompare(e2.title().stripHtml()));
			} else if (viewModel.order() === 2) {
				shuffle(result);
			}

			if (viewModel.initialOrder == null) {
				viewModel.initialOrder = result;
			}

			return result;
		}

		return viewModel.initialOrder;
	});

	viewModel.answervalues = ko.observable($.map(viewModel.orderedRankingItems(), item => item.uniqueId()));
	viewModel.answervalues.subscribe(function() {
		viewModel.isAnswered(true);
	});

	viewModel.acceptInitialAnswer = function(data, event) {
		const rankingItemList = $(event.target).closest(".rankingitem").find(".rankingitem-list")[0];
		viewModel.isAnswered(true);
		propagateChange(rankingItemList);
		if (isdelphi) {
			tinyMCE.get('explanation' + data.id()).execCommand('mceFocus', false);
		}
	}

	viewModel.resetOrder = function(_, event) {
		const thatRanking = $(event.target).closest(".rankingitem");
		
		//.slice() because originalItemUniqueIdOrder would otherwise be changed by moving the items
		viewModel.answervalues(viewModel.originalItemUniqueIdOrder().slice());
		viewModel.isAnswered(false);

		const rankingItemsTexts = thatRanking.find(".rankingitemtext");
		const rankingItemUniqueIds = $.map(rankingItemsTexts, that => viewModel.itemIdtoUniqueIdLookup[Number(that.id)]);
		const permutation = $.map(viewModel.originalItemUniqueIdOrder(), uniqueId => rankingItemUniqueIds.indexOf(uniqueId));

		const rankingItemList = thatRanking.find(".rankingitem-list")[0];
		const rankingItemFormDatas = thatRanking.find(".rankingitem-form-data");
		$.each(permutation, function(_, permuteIndex) {
			const thatItemDiv = $(rankingItemFormDatas).eq(permuteIndex);
			$(rankingItemList).append(thatItemDiv);
		});
		propagateChange(rankingItemList);
		const surveyElement = $(rankingItemList).parents(".survey-element").last();
		enableDelphiSaveButtons(surveyElement);
	};

	viewModel.originalItemUniqueIdOrder = ko.computed(() => $.map(viewModel.orderedRankingItems(), item => item.uniqueId()));

	viewModel.itemTitleLookup = {};
	$.each(element.childElements, (_, that) => {
		viewModel.itemTitleLookup[that.uniqueId] = that.title;
	});

	viewModel.getAnswerValuesString = ko.computed(function() {
		if (!viewModel.isAnswered()) return "";
		return viewModel.answervalues().join(';');
	});
	viewModel.getAnswerValuesTitleList = ko.computed(function() {
		return $.map(viewModel.answervalues(), uniqueId => viewModel.itemTitleLookup[uniqueId]);
	});

	viewModel.initOn = function(domElement) {
		const viewmodel = this;
		if (viewmodel.foreditor) return;
		const formeranswervaluesstringly = getValueByQuestion(viewmodel.uniqueId());
		const formeranswervalues = formeranswervaluesstringly.split(';');
		const isAnswerFormatSemicolon = formeranswervalues.join(';') == formeranswervaluesstringly;
		const isAnswerFormatLength = formeranswervalues.length == viewmodel.answervalues().length;
		let allIdsValid = false;
		if (isAnswerFormatSemicolon && isAnswerFormatLength) {
			allIdsValid = true;
			$.each(formeranswervalues, function(index, uniqueId) {
				if (!(uniqueId in viewmodel.itemTitleLookup)) {
					allIdsValid = false;
				}
			});
		}
		const self = $(domElement).find(".rankingitem-list")[0];

		if (allIdsValid) {
			const permutation = $.map(formeranswervalues, uniqueId => viewmodel.originalItemUniqueIdOrder().indexOf(uniqueId));
			const rankingItemReordered = $.map(permutation, index => viewmodel.rankingItems()[index]);
			viewmodel.rankingItems(rankingItemReordered);
			viewmodel.answervalues(formeranswervalues);
			let rankingitemFormData = $(self).find(".rankingitem-form-data");
			let rankingitemFormDataReOrdered = $.map(permutation, value => rankingitemFormData.get(value));
			$.each(rankingitemFormDataReOrdered, (_, that) => self.append(that));
			propagateChange(self);
		}

		$(self).sortable({
			start: function(event, ui) {
				const width = $(ui.item).width();
				const rankingItemFormDatas = $(domElement).find(".rankingitem-form-data");
				rankingItemFormDatas.each(function() {
					$(this).width(2 + width);
				});
			},
			stop: function(event, ui) {
				const rankingItemFormDatas = $(domElement).find(".rankingitem-form-data");
				rankingItemFormDatas.each(function() {
					$(this).width("");
				});
			},
			update: function(event, ui) {
				const rankingitems = $(self).find(".rankingitemtext");
				const newanswervalues = $.map(rankingitems, that => viewmodel.itemIdtoUniqueIdLookup[Number(that.id)]);
				const parentUID = viewmodel.uniqueId();
				values[parentUID] = newanswervalues.join(';');
				viewmodel.answervalues(newanswervalues);
				propagateChange(self);
			},
		});
		$(domElement).disableSelection();
	};

	viewModel.creator = newRankingViewModel;

	return viewModel;
}

function newRankingItemViewModelForEach(childElements, parent)
{
	var viewModel = ko.observableArray();
	$.each(childElements, (index, that) => {
		viewModel.push(newRankingItemViewModel(that.id, that.uniqueId, that.shortname, that.title, parent));
	});
	return viewModel;
}

var ArrayElementMovingTool = {
	move : function(self, from, to) {
		if (0 > from) return false;
		if (0 > to) return false;
		if (self.length <= from) return false;
		if (self.length <= to) return false;
		if (from == to) return false;
		self.splice(to, 0, self.splice(from, 1)[0]);
		return true;
	},
	moveRelative : function(self, from, steps) {
		if (0 == steps) return false;
		const to = from + steps;
		return ArrayElementMovingTool.move(self, from, to);
	},
	moveItemRelative : function(self, item, steps) {
		const index = self.indexOf(item);
		if (-1 == index) return false;
		return ArrayElementMovingTool.moveRelative(self, index, steps);
	}
};

function newRankingItemViewModel(id, uniqueId, shortname, title, parent)
{
	var viewModel = newBasicViewModel();
	viewModel.type = 'RankingItem';
	viewModel.title = ko.observable(title);
	viewModel.id = ko.observable(id);
	viewModel.id.parent = parent;
	viewModel.uniqueId = ko.observable(uniqueId);
	viewModel.shortname = ko.observable(shortname);
	viewModel.originalTitle = ko.observable(title);

	viewModel.onMoveItem = function(_, event, steps) {
		if (this.id.parent.foreditor) return;
		var target = event.target;
		var rankingitemList = $(target).closest(".rankingitem-list");
		var rankingitems = $(rankingitemList).find(".rankingitemtext");
		const actualOrder = $.map(rankingitems, that => this.id.parent.itemIdtoUniqueIdLookup[Number(that.id)]);
		const answervalues = this.id.parent.answervalues();
		if (ArrayElementMovingTool.moveItemRelative(answervalues, this.uniqueId(), steps)) {
			this.id.parent.answervalues(answervalues);
			const reIndex = $.map(answervalues, that => actualOrder.indexOf(that));
			var rankingitemFormData = $(rankingitemList).find(".rankingitem-form-data");
			var rankingitemFormDataReOrdered = $.map(reIndex, value => rankingitemFormData.get(value));
			$.each(rankingitemFormDataReOrdered, (_, that) => $(rankingitemList).append(that));
			target.focus();
			propagateChange(rankingitemList[0]);
		}
	}

	viewModel.onMoveUp = function(data, event) {
		this.onMoveItem(data, event, -1);
	}
	viewModel.onMoveDown = function(data, event) {
		this.onMoveItem(data, event, 1);
	}
	viewModel.onKeyDownMoveItem = function(data, event, steps) {
		if ([" ", "Enter"].includes(event.key)) {
			this.onMoveItem(data, event, steps);
		} else {
			return true; // allow default action
		}
	}
	viewModel.onKeyDownMoveItemUp = function(data, event) {
		return viewModel.onKeyDownMoveItem(data, event, -1);
	}
	viewModel.onKeyDownMoveItemDown = function(data, event, steps) {
		return viewModel.onKeyDownMoveItem(data, event, 1);
	}

	viewModel.creator = newRankingItemViewModel;

	return viewModel;
}

function newFreeTextViewModel(element)
{
	var viewModel = newBasicViewModel(element);
	
	viewModel.optional = ko.observable(element.optional);	
	viewModel.readonly = ko.observable(element.readonly);	
	viewModel.isAttribute = ko.observable(element.isAttribute);
	viewModel.attributeName = ko.observable(element.attributeName);	
	viewModel.numRows = ko.observable(element.numRows);	
	viewModel.minCharacters = ko.observable(element.minCharacters);	
	viewModel.maxCharacters = ko.observable(element.maxCharacters);	
	viewModel.isPassword = ko.observable(element.isPassword);	
	viewModel.isUnique = ko.observable(element.isUnique);	
	viewModel.isComparable = ko.observable(element.isComparable);
	viewModel.help = ko.observable(element.help);
	viewModel.niceHelp = ko.observable(getNiceHelp(element.help));
	viewModel.css = ko.observable(element.css);

	viewModel.values = {first:{}, second:{}};
	viewModel.values.first.onLastValidation = ko.observable();
	viewModel.values.first.hasChangedOnValidation = ko.observable(false);
	viewModel.values.second.onLastValidation = ko.observable();
	viewModel.values.second.hasChangedOnValidation = ko.observable(false);

	viewModel.values.first.onValidation = function(newVal) {
		const that = viewModel.values.first;
		const oldVal = that.onLastValidation();
		that.onLastValidation(newVal);
		const hasChanged = newVal != oldVal;
		if (hasChanged) {
			that.hasChangedOnValidation(hasChanged);
		}
	};

	viewModel.values.second.onValidation = function(newVal) {
		const that = viewModel.values.second;
		const oldVal = that.onLastValidation();
		that.onLastValidation(newVal);
		const hasChanged = newVal != oldVal;
		if (hasChanged) {
			that.hasChangedOnValidation(hasChanged);
		}
	};

	viewModel.creator = newFreeTextViewModel;

	return viewModel;
}

function newRegExViewModel(element)
{
	var viewModel = newFreeTextViewModel(element);
	viewModel.regex = ko.observable(element.regex);

	viewModel.creator = newRegExViewModel;

	return viewModel;
}

function getVariablesForFormula(formula) {
	let variables = [];
	const root = math.parse(formula);
	root.traverse(function (node) {
	  if (node.type === 'SymbolNode' && !(['mean', 'min', 'max'].indexOf(node.name) > -1)) {
	      variables.push(node.name);
	  }
	})
	
	return variables;
}

function newFormulaViewModel(element)
{
	const viewModel = newBasicViewModel(element);
	viewModel.readonly = ko.observable(element.readonly);	
	viewModel.formula = ko.observable(element.formula);
	viewModel.help = ko.observable(element.help);
	viewModel.niceHelp = ko.observable(getNiceHelp(element.help));	
	viewModel.decimalPlaces = ko.observable(element.decimalPlaces != null ? element.decimalPlaces : 0);	
	viewModel.min = ko.observable(element.min);
	viewModel.minString = ko.observable(element.minString);	
	viewModel.max = ko.observable(element.max);
	viewModel.maxString = ko.observable(element.maxString);
	viewModel.variables = [];
				
	viewModel.result = ko.observable("");
	if (!viewModel.foreditor) {
		viewModel.result(getValueByQuestion(viewModel.uniqueId()));
	}

	viewModel.dependsOn = function(alias) {
		if (this.variables.length == 0) {
			this.variables = getVariablesForFormula(this.formula());
		}
		return this.variables.indexOf(alias) >= 0;
	}
		
	viewModel.refreshResult = function() {
		refreshResult(this);
	}

	viewModel.creator = newFormulaViewModel;

	return viewModel;
}

function refreshResult(viewModel) {
	try {		
		const parser = math.parser();
		if (viewModel.variables.length == 0) {
			viewModel.variables = getVariablesForFormula(viewModel.formula());
		}		
		let allEmpty = true;			
		viewModel.variables.forEach (a => {
			let input = $("input[data-shortname='" + a + "']");
			if (input.length == 0) {
				input = $("textarea[data-shortname='" + a + "']");
			}
			let value = 0;
			if (input.length > 0 && input.attr("data-is-answered") !== "false") {
				value = $(input).val();
				if (value.length == 0) {
					value = "0";
				} else {
					allEmpty = false;
				}
			}
			if (isNaN(value) || !isFinite(value)) {
				viewModel.result("");
				return; //invalid input
			} 
			parser.set(a, parseFloat(value));
		});
		
		if (allEmpty) {
			viewModel.result("");
			return;
		}
		
		let result = parser.evaluate(viewModel.formula());
		if (isNaN(result) || !isFinite(result)) { //Divide by 0 will result in non Finite
			viewModel.result("");
			return; //invalid input
		} 
		
		viewModel.result(math.round(result, viewModel.decimalPlaces())); 
	} catch (e) {
		console.log(e);
		viewModel.result("");
		return; //invalid input
	}
}

function newConfirmationViewModel(element)
{
	var viewModel = newBasicViewModel(element);
	
	viewModel.optional = ko.observable(element.optional);	
	viewModel.readonly = ko.observable(element.readonly);	
	viewModel.confirmationlabel = ko.observable(element.confirmationlabel);
	viewModel.confirmationtext = ko.observable(element.confirmationtext);
	viewModel.files = newFilesViewModel(element.files);	
	viewModel.usetext = ko.observable(element.usetext);	
	viewModel.useupload = ko.observable(element.useupload);	
	viewModel.help = ko.observable(element.help);
	viewModel.niceHelp = ko.observable(getNiceHelp(element.help));
	viewModel.css = ko.observable(element.css);

	viewModel.creator = newConfirmationViewModel;

	return viewModel;
}

function newRatingViewModel(element)
{
	var viewModel = newBasicViewModel(element);
	
	viewModel.optional = ko.observable(element.optional);	
	viewModel.readonly = ko.observable(element.readonly);	
	viewModel.numIcons = ko.observable(element.numIcons);	
	viewModel.iconType = ko.observable(element.iconType);	
	viewModel.help = ko.observable(element.help);
	viewModel.niceHelp = ko.observable(getNiceHelp(element.help));
	viewModel.css = ko.observable(element.css);
	viewModel.childElements = ko.observableArray();
	for (var i = 0; i < element.childElements.length; i++)
	{
		viewModel.childElements.push(newBasicViewModel(element.childElements[i]));
	}
	
	viewModel.getChild = function(id)
	{		
		for (var j = 0; j < this.childElements().length; j++)
		{
			if (this.childElements()[j].id() == id)
			{
				return this.childElements()[j];
			}
		}		
	}

	viewModel.creator = newRatingViewModel;

	return viewModel;
}

function newNumberViewModel(element)
{
	var viewModel = newBasicViewModel(element);
	
	viewModel.optional = ko.observable(element.optional);	
	viewModel.readonly = ko.observable(element.readonly);	
	viewModel.isUnique = ko.observable(element.isUnique);	
	viewModel.isAttribute = ko.observable(element.isAttribute);
	viewModel.attributeName = ko.observable(element.attributeName);	
	viewModel.help = ko.observable(element.help);
	viewModel.niceHelp = ko.observable(getNiceHelp(element.help));
	viewModel.css = ko.observable(element.css);	
	viewModel.decimalPlaces = ko.observable(element.decimalPlaces != null ? element.decimalPlaces : 0);	
	viewModel.unit = ko.observable(element.unit);	
	viewModel.min = ko.observable(element.min);	
	viewModel.minString = ko.observable(element.minString);	
	viewModel.max = ko.observable(element.max);
	viewModel.maxString = ko.observable(element.maxString);	
	
	viewModel.display = ko.observable(element.display);
	viewModel.minLabel = ko.observable(element.minLabel);
	viewModel.maxLabel = ko.observable(element.maxLabel);
	viewModel.initVal = null;
	viewModel.initialSliderPosition = ko.observable(element.initialSliderPosition != null ? element.initialSliderPosition : "Left");
	viewModel.displayGraduationScale = ko.observable(element.displayGraduationScale);
	
	viewModel.maxDistance = ko.observable(element.maxDistance);

	viewModel.isAnswered = ko.observable(false);
	viewModel.isAnswered.subscribe(function () {
		const input = $("#answer" + viewModel.id());
		propagateChange($(input));
	});

	viewModel.markAsAnswered = function (data) {
		const input = $("#answer" + viewModel.id());
		input.val(viewModel.initialDefaultValue())
		this.isAnswered(true);
		var explanationBox = tinyMCE.get('explanation' + data.id());
		if (explanationBox != null) {
			explanationBox.execCommand('mceFocus',false);
		}
		updateFormulas(this.id(), this.shortname());
	};

	viewModel.resetToInitialPosition = function(_, event) {
		const input = $("#answer" + viewModel.id());
		const initialDefaultValue = viewModel.initialDefaultValue();

		$(input).bootstrapSlider().bootstrapSlider("setValue", initialDefaultValue);
		$(input).val("");
		viewModel.isAnswered(false);
		propagateChange($(input));
		const surveyElement = $(input).parents(".survey-element").last();
		enableDelphiSaveButtons(surveyElement);
	};

	if (viewModel.display() == 'Slider')
	{
		if (viewModel.min() == null)
		{
			viewModel.min(0);
		}
		
		if (viewModel.max() == null)
		{
			viewModel.max(10)
		}
	}
	
	viewModel.labels = function()
	{	
		var result = [];
		result[result.length] = this.min().toString();
		var v = this.min();
			
		result[result.length] = this.max().toString();		
			
		return result;
	}
	
	viewModel.ticks = function()
	{
		return "[" + this.min() + "," + this.max() + "]";
	}
	
	viewModel.step = function()
	{
		var decimals = parseInt(this.decimalPlaces());
		if (decimals === 0)
		{
			return 1;
		}
		return 10 ** (-1 * decimals);
	}
	
	viewModel.getBootstrapSlider = function(inputelement) {
		return $(inputelement).bootstrapSlider();
	}

	viewModel.increase = function(element)
	{
		var min = parseFloat(this.min());
		var max = parseFloat(this.max());
		var input = $("#answer" + element.id());
		var value = parseFloat($(input).bootstrapSlider().bootstrapSlider('getValue'));
		if (value < max) {
			$(input).bootstrapSlider().bootstrapSlider('setValue', value + this.step(), true);
		}
		
		propagateChange($(input));
	}
	
	viewModel.decrease = function(element)
	{
		var min = parseFloat(this.min());
		var max = parseFloat(this.max());
		var input = $("#answer" + element.id());
		var value = parseFloat($(input).bootstrapSlider().bootstrapSlider('getValue'));
		if (value > min) {
			$(input).bootstrapSlider().bootstrapSlider('setValue', value - this.step(), true);
		}
		
		propagateChange($(input));
	}
	
	viewModel.initialValue = function() {
		if (this.initVal === null) {
			let ovalue = getValueByQuestion(this.uniqueId());
			if (ovalue.length > 0) {
				this.isAnswered(true);
				this.initVal = ovalue;
			} else {
				this.initVal = viewModel.initialDefaultValue();
			}
		}
		return this.initVal
	};

	viewModel.initialDefaultValue = function() {
		if (this.initialSliderPosition() === "Middle")
		{
			var min = parseInt(this.min());
			var max = parseInt(this.max());
			var distance = max-min;
			if (this.decimalPlaces() > 0) {
				return Math.round(100 * (min + (distance / 2))) / 100;
			}
			return Math.round(min + (distance / 2));
		}
		
		if (this.initialSliderPosition() === "Right")
		{
			return this.max();
		}
		
		return this.min();
	};

	viewModel.creator = newNumberViewModel;
	
	return viewModel;
}

function newEmailViewModel(element)
{
	var viewModel = newBasicViewModel(element);
	
	viewModel.optional = ko.observable(element.optional);	
	viewModel.readonly = ko.observable(element.readonly);	
	viewModel.isAttribute = ko.observable(element.isAttribute);
	viewModel.attributeName = ko.observable(element.attributeName);	
	viewModel.help = ko.observable(element.help);
	viewModel.niceHelp = ko.observable(getNiceHelp(element.help));
	viewModel.css = ko.observable(element.css);

	viewModel.creator = newEmailViewModel;

	return viewModel;
}

function newDateViewModel(element)
{
	var viewModel = newBasicViewModel(element);
	
	viewModel.optional = ko.observable(element.optional);	
	viewModel.readonly = ko.observable(element.readonly);	
	viewModel.isAttribute = ko.observable(element.isAttribute);
	viewModel.attributeName = ko.observable(element.attributeName);	
	viewModel.help = ko.observable(element.help);
	viewModel.niceHelp = ko.observable(getNiceHelp(element.help));
	viewModel.css = ko.observable(element.css);	
	viewModel.decimalPlaces = ko.observable(element.decimalPlaces);	
	viewModel.unit = ko.observable(element.unit);	
	viewModel.min = ko.observable(element.min);	
	viewModel.minString = ko.observable(element.minString);	
	viewModel.max = ko.observable(element.max);	
	viewModel.maxString = ko.observable(element.maxString);

	viewModel.creator = newDateViewModel;

	return viewModel;
}

function newTimeViewModel(element)
{
	var viewModel = newBasicViewModel(element);
	
	viewModel.optional = ko.observable(element.optional);	
	viewModel.readonly = ko.observable(element.readonly);	
	viewModel.isAttribute = ko.observable(element.isAttribute);
	viewModel.attributeName = ko.observable(element.attributeName);	
	viewModel.help = ko.observable(element.help);
	viewModel.niceHelp = ko.observable(getNiceHelp(element.help));
	viewModel.css = ko.observable(element.css);	
	viewModel.min = ko.observable(element.min);	
	viewModel.minString = ko.observable(element.minString);	
	viewModel.max = ko.observable(element.max);	
	viewModel.maxString = ko.observable(element.maxString);

	viewModel.creator = newTimeViewModel;

	return viewModel;
}

function newUploadViewModel(element)
{
	var viewModel = newBasicViewModel(element);
	
	viewModel.optional = ko.observable(element.optional);	
	viewModel.readonly = ko.observable(element.readonly);	
	viewModel.isAttribute = ko.observable(element.isAttribute);
	viewModel.attributeName = ko.observable(element.attributeName);	
	viewModel.help = ko.observable(element.help);
	viewModel.niceHelp = ko.observable(getNiceHelp(element.help));
	viewModel.css = ko.observable(element.css);
	viewModel.extensions = ko.observable(element.extensions);
	viewModel.maxFileSize = ko.observable(element.maxFileSize);

	viewModel.creator = newUploadViewModel;

	return viewModel;
}

function newDownloadViewModel(element)
{
	var viewModel = newBasicViewModel(element);
	
	viewModel.optional = ko.observable(element.optional);	
	viewModel.readonly = ko.observable(element.readonly);	
	viewModel.isAttribute = ko.observable(element.isAttribute);
	viewModel.attributeName = ko.observable(element.attributeName);	
	viewModel.help = ko.observable(element.help);
	viewModel.niceHelp = ko.observable(getNiceHelp(element.help));
	viewModel.css = ko.observable(element.css);
	viewModel.files = newFilesViewModel(element.files);

	viewModel.creator = newDownloadViewModel;

	return viewModel;
}

function newGalleryViewModel(element)
{
	var viewModel = newBasicViewModel(element);
	viewModel.selection = ko.observable(element.selection);
	viewModel.numbering = ko.observable(element.numbering);
	viewModel.limit = ko.observable(element.limit);
	viewModel.columns = ko.observable(element.columns);
	viewModel.files = newFilesViewModel(element.files);
	viewModel.help = ko.observable(element.help);
	viewModel.niceHelp = ko.observable(getNiceHelp(element.help));
	viewModel.optional = ko.observable(element.optional);
	viewModel.readonly = ko.observable(element.readonly);
	viewModel.css = ko.observable(element.css);
	
	viewModel.getChild = function(uid)
	{
		for (var i = 0; i < this.files().length; i++)
		{
			if (this.files()[i].uid() == uid)
			{
				return this.files()[i];
			}
		}		
	}
	
	viewModel.rows = ko.dependentObservable(function() {
	    var result = [],
	        colLength = parseInt(this.columns(), 10),
	        row;

	    //loop through items and push each item to a row array that gets pushed to the final result
	    for (var i = 0, j = this.files().length; i < j; i++) {
	        if (i % colLength === 0) {
	            if (row) {
	              result.push(row);     
	            }
	            row = [];
	        }
	        row.push(this.files()[i]);
	    }

	    //push the final row  
	    if (row) {
	        result.push(row);
	    }

	    return result;
	}, viewModel);

	viewModel.creator = newGalleryViewModel;

	return viewModel;
}

function newMatrixViewModel(element)
{
	var viewModel = newBasicViewModel(element);
	
	viewModel.optional = ko.observable(element.optional);	
	viewModel.readonly = ko.observable(element.readonly);	
	viewModel.isAttribute = ko.observable(element.isAttribute);
	viewModel.attributeName = ko.observable(element.attributeName);	
	viewModel.help = ko.observable(element.help);
	viewModel.niceHelp = ko.observable(getNiceHelp(element.help));
	viewModel.css = ko.observable(element.css);
	viewModel.tableType = ko.observable(element.tableType);
	viewModel.order = ko.observable(element.order);
	viewModel.isInterdependent = ko.observable(element.isInterdependent);
	viewModel.isSingleChoice = ko.observable(element.isSingleChoice);
	viewModel.minRows = ko.observable(element.minRows);
	viewModel.maxRows = ko.observable(element.maxRows);
	viewModel.widths = ko.observable(element.widths);
	viewModel.columns = ko.observable(element.columns);
	viewModel.answers = newMatrixItemsViewModel(element.answers);
	viewModel.questions = newMatrixItemsViewModel(element.questions);
	viewModel.dependentElementsStrings = ko.observableArray();
	viewModel.firstCellText = ko.observable(element.firstCellText);
	
	for (var i = 0; i < element.dependentElementsStrings.length; i++)
	{
		viewModel.dependentElementsStrings.push(ko.observable(element.dependentElementsStrings[i]));
	}
	
	viewModel.dependentElementsStringsCopy = function()
	{
		var result = [];
		for (var i = 0; i < this.dependentElementsStrings().length; i++)
		{
			result[result.length] = this.dependentElementsStrings()[i]();
		}
		return result;
	}
	
	viewModel.setDependentElementsStrings = function(arr)
	{
		viewModel.dependentElementsStrings.removeAll();
		for (var i = 0; i < arr.length; i++)
		{
			viewModel.dependentElementsStrings.push(ko.observable(arr[i]));
		}
	}
	
	viewModel.questionsOrdered = function()
	{
		if (this == null) return null;
		
		if (this.order() != null && this.order() == 1)
		{
			var questions = this.questions().slice();
			questions.sort(function(a, b) { 
			    return a.originalTitle().localeCompare(b.originalTitle());
			})
			return questions;			
		} else if (this.order() != null && this.order() == 2)
		{
			var questions = this.questions().slice();
			questions = shuffle(questions);
			return questions;
		} else {
			return this.questions();	
		}
	}
		
	viewModel.getChild = function(id)
	{
		for (var i = 0; i < this.questions().length; i++)
		{
			if (this.questions()[i].id() == id)
			{
				return this.questions()[i];
			}
		}
		for (var i = 0; i < this.answers().length; i++)
		{
			if (this.answers()[i].id() == id)
			{
				return this.answers()[i];
			}
		}
	}
	
	viewModel.removeDependencies = function(id) {
		for (var i = 0; i < this.dependentElementsStrings().length; i++)
		{
			var deps = this.dependentElementsStrings()[i]();
			if (deps != null && deps.indexOf(id) > -1)
			{
				this.dependentElementsStrings()[i](deps.replace(id + ";", ""));
			}			
		}
	}
	
	viewModel.getQuestionIndex = function(id) {
		for (var i = 0; i < this.questions().length; i++)
		{
			if (this.questions()[i].id() == id)
			{
				return i;
			}
		}		
	}

	viewModel.creator = newMatrixViewModel;

	return viewModel;
}

function newTableViewModel(element)
{
	var viewModel = newBasicViewModel(element);
	
	viewModel.optional = ko.observable(element.optional);	
	viewModel.readonly = ko.observable(element.readonly);	
	viewModel.isAttribute = ko.observable(element.isAttribute);
	viewModel.attributeName = ko.observable(element.attributeName);	
	viewModel.help = ko.observable(element.help);
	viewModel.niceHelp = ko.observable(getNiceHelp(element.help));
	viewModel.css = ko.observable(element.css);
	viewModel.tableType = ko.observable(element.tableType);
	viewModel.widths = ko.observable(element.widths);
	viewModel.answers = newMatrixItemsViewModel(element.answers);
	viewModel.questions = newMatrixItemsViewModel(element.questions);
	viewModel.firstCellText = ko.observable(element.firstCellText);
	
	viewModel.getChild = function(id)
	{
		for (var i = 0; i < this.questions().length; i++)
		{
			if (this.questions()[i].id() == id)
			{
				return this.questions()[i];
			}
		}
		for (var i = 0; i < this.answers().length; i++)
		{
			if (this.answers()[i].id() == id)
			{
				return this.answers()[i];
			}
		}
	}

	viewModel.creator = newTableViewModel;

	return viewModel;
}

function newComplexTableViewModel(element, foreditor)
{
	var viewModel = newBasicViewModel(element);
	
	if (element != null)
	{
		if (element.hasOwnProperty("isViewModel") && element.isViewModel)
		{
			viewModel.help = ko.observable(element.help());
			viewModel.niceHelp = ko.observable(getNiceHelp(element.help()));
			viewModel.css = ko.observable(element.css());
			viewModel.rows = ko.observable(element.rows());
			viewModel.columns = ko.observable(element.columns());
			viewModel.showHeadersAndBorders = ko.observable(element.showHeadersAndBorders());
			viewModel.size = ko.observable(element.size());
			viewModel.childElements = newComplexTableItemsViewModel(element.childElements(), foreditor);		
		} else {	
			viewModel.help = ko.observable(element.help);
			viewModel.niceHelp = ko.observable(getNiceHelp(element.help));
			viewModel.css = ko.observable(element.css);
			viewModel.rows = ko.observable(element.rows);
			viewModel.columns = ko.observable(element.columns);
			viewModel.showHeadersAndBorders = ko.observable(element.showHeadersAndBorders);
			viewModel.size = ko.observable(element.size);
			viewModel.childElements = newComplexTableItemsViewModel(element.childElements, foreditor);
		}	
	}
	
	viewModel.orderedChildElements = ko.pureComputed(() => {
		return viewModel.childElements.sorted(function(left, right) {
			if (left.row() < right.row()) {
				return -1;
			}

			if (left.row() > right.row()) {
				return 1;
			}

			if (left.column() < right.column()) {
				return -1;
			}

			if (left.column() > right.column()) {
				return 1;
			}

			return 0;
		})
	})
		
	viewModel.answers = ko.computed(function() {
		var result = ko.observableArray();
		for (let i = 0; i < viewModel.childElements().length; i++) {
			if (viewModel.childElements()[i].row() === 0) {
				result.push(viewModel.childElements()[i]);
			}
		}
		return result();
	}, this);
	
	viewModel.questions = ko.computed(function() {
		var result = ko.observableArray();
		for (let i = 0; i < viewModel.childElements().length; i++) {
			if (viewModel.childElements()[i].column() === 0 && viewModel.childElements()[i].row() > 0) {
				result.push(viewModel.childElements()[i]);
			}
		}
		return result();
	}, this);
	
	viewModel.removeChildAt = function (col, row) {
		let child = viewModel.getChild(col, row);
		if (child != null) {
			viewModel.childElements.remove(child);
			return child
		}
	}
	
	viewModel.isCellVisible = function (col, row) {
		if (col == 1) return true;
		
		for (let i = 1; i < viewModel.columns(); i++) {
			if (col > i) {
				previous = viewModel.getChild(col-i, row);
				if (previous != null) {
					return previous.columnSpan() == null || previous.columnSpan() < (i+1);
				}
			} else {
				return true;
			}
		}
		
		return true;
	}
	
	viewModel.getChild = function (col, row) {
		for (let i = 0; i < viewModel.childElements().length; i++) {
			if (viewModel.childElements()[i].column() === col && viewModel.childElements()[i].row() === row) {
				return viewModel.childElements()[i];
			}
		}
		return null;
	}
	
	viewModel.getChildbyId = function (id) {
		for (let i = 0; i < viewModel.childElements().length; i++) {
			if (viewModel.childElements()[i].id().toString() === id.toString()) {
				return viewModel.childElements()[i];
			}
		}
		return null;
	}
			
	viewModel.availableColumnSpans = function(column) {
		const result = [];
		for (let i = 1; i <= viewModel.columns() - column + 1; i++) {
			result.push(i);	
		}
		return result;
	}
	
	viewModel.childIds = function() {
		let result = "";
		for (let i = 0; i < viewModel.childElements().length; i++) {
			result += viewModel.childElements()[i].id() + ";";
		}
		return result;
	}

	viewModel.creator = newComplexTableViewModel;

	return viewModel;
}

function newComplexTableItemsViewModel(items, foreditor)
{
	var viewModel = ko.observableArray();
	if (items != null) {
		for (var i = 0; i < items.length; i++)
		{
			const child = newComplexTableItemViewModel(items[i]);
			child.foreditor = foreditor;
			viewModel.push(child);
		}
	}
	return viewModel;
}

function newComplexTableItemViewModel(element)
{
	var viewModel = newBasicViewModel(element);
	
	if (element != null)
	{
		if (element.hasOwnProperty("isViewModel") && element.isViewModel)
		{
			viewModel.help = ko.observable(element.help());
			viewModel.niceHelp = ko.observable(getNiceHelp(element.help()));
			viewModel.css = ko.observable(element.css());
			viewModel.row = ko.observable(element.row());
			viewModel.column = ko.observable(element.column());
			viewModel.cellType = ko.observable(element.cellType());
			viewModel.columnSpan = ko.observable(element.columnSpan());
			viewModel.minCharacters = ko.observable(element.minCharacters());
			viewModel.maxCharacters = ko.observable(element.maxCharacters());
			viewModel.minChoices = ko.observable(element.minChoices() == null ? 0 : element.minChoices());
			viewModel.maxChoices = ko.observable(element.maxChoices() == null ? 0 : element.maxChoices());
			viewModel.numRows = ko.observable(element.numRows() == null ? 1 : element.numRows());
		
			viewModel.possibleAnswers = newComplexPossibleAnswersViewModel(element.possibleAnswers());
			
			viewModel.useRadioButtons = ko.observable(element.useRadioButtons());
			viewModel.useCheckboxes = ko.observable(element.useCheckboxes());	
			viewModel.numColumns = ko.observable(element.numColumns());
			viewModel.order = ko.observable(element.order() == null ? 0 : element.order());
			viewModel.resultText = ko.observable(element.resultText());
			viewModel.decimalPlaces = ko.observable(element.decimalPlaces != null ? element.decimalPlaces() : 0);	
			viewModel.unit = ko.observable(element.unit());	
			viewModel.min = ko.observable(element.min());	
			viewModel.max = ko.observable(element.max());	
			viewModel.minString = ko.observable(element.minString());	
			viewModel.maxString = ko.observable(element.maxString());
			viewModel.formula = ko.observable(element.formula());
			viewModel.readonly = ko.observable(element.readonly());
		} else {	
			viewModel.help = ko.observable(element.help);
			viewModel.niceHelp = ko.observable(getNiceHelp(element.help));
			viewModel.css = ko.observable(element.css);
			viewModel.row = ko.observable(element.row);
			viewModel.column = ko.observable(element.column);
			viewModel.cellType = ko.observable(element.cellType);
			viewModel.columnSpan = ko.observable(element.columnSpan);
			viewModel.minCharacters = ko.observable(element.minCharacters);
			viewModel.maxCharacters = ko.observable(element.maxCharacters);
			viewModel.minChoices = ko.observable(element.minChoices == null ? 0 : element.minChoices);
			viewModel.maxChoices = ko.observable(element.maxChoices == null ? 0 : element.maxChoices);
			viewModel.numRows = ko.observable(element.numRows == null || element.numRows < 1 ? 1 : element.numRows);
		
			viewModel.possibleAnswers = newComplexPossibleAnswersViewModel(element.possibleAnswers);
			
			viewModel.useRadioButtons = ko.observable(element.useRadioButtons);
			viewModel.useCheckboxes = ko.observable(element.useCheckboxes);	
			viewModel.numColumns = ko.observable(element.numColumns == null || element.numColumns < 1 ? 1 : element.numColumns);
			viewModel.order = ko.observable(element.order == null ? 0 : element.order);
			viewModel.resultText = ko.observable(element.resultText);
			viewModel.decimalPlaces = ko.observable(element.decimalPlaces != null ? element.decimalPlaces : 0);	
			viewModel.unit = ko.observable(element.unit);	
			viewModel.min = ko.observable(element.min);	
			viewModel.max = ko.observable(element.max);	
			viewModel.minString = ko.observable(element.minString);	
			viewModel.maxString = ko.observable(element.maxString);
			viewModel.formula = ko.observable(element.formula);
			viewModel.readonly = ko.observable(element.readonly);
		}

		viewModel.variables = [];				
		viewModel.result = ko.observable("");

		viewModel.result.subscribe(function (){
			setTimeout(() => {
				validateInput($(".cell[data-id=" + viewModel.id() + "]"))
			})
		})

	viewModel.orderedPossibleAnswers = function(mobile, responsive){
		if (this.order() != null && this.order() === 1){
			let dic = [];
			for (let i = 0; i < this.possibleAnswers().length; i++){
				let item = [];
				item[0] = this.possibleAnswers()[i].title().stripHtml();
				item[1] = this.possibleAnswers()[i];
				dic[dic.length] = item;
			}

			dic.sort(sortFunction);

			let answers = [];
			for (let i = 0; i < dic.length; i++)
			{
				answers[answers.length] = dic[i][1];
			}

			return answers;
		} else if (this.order() != null && this.order() === 2)
		{
			let answers = this.possibleAnswers().slice();
			answers = shuffle(answers);
			return answers;
		} else {
			return this.possibleAnswers();
		}
	}

	viewModel.orderedPossibleAnswersByColumn = function(mobile, responsive){
		let orderedPossibleAnswers = this.orderedPossibleAnswers(mobile, responsive);
		let result = [];

		let limit = this.numColumns();
		if (limit == 0) limit = 1;
		
		if (responsive && limit > 3) limit = 3;
		if (mobile) limit = 1;

		let itemsLeft = orderedPossibleAnswers.length
		let sizeMap = {}
		let pos = 0
		while (itemsLeft > 0){
			if (sizeMap[pos % limit] == null){
				sizeMap[pos % limit] = 1
			} else {
				sizeMap[pos % limit]++
			}
			pos++;
			itemsLeft--;
		}
		pos = 0;
		for (let i = 0; i < limit; i++){
			let column = []
			for(let j = 0; j < sizeMap[i]; j++) {
				let el = orderedPossibleAnswers[pos]
				if (el != null && el.id() !== "dummy") {
					column.push(el)
				}
				pos++;
			}
			result.push(column)
		}

		return result;
	}

	viewModel.hasEmptyLastColumn = function(){
		if (this.numColumns() < 3) return false;

		return this.possibleAnswers().length < this.numColumns()
	}
		
		//properties for rows / columns
		viewModel.cellTypeChildren = ko.observable("");
		viewModel.titleChildren = ko.observable("");
		viewModel.optionalChildren = ko.observable(true);
		viewModel.optionalIndeterminate = ko.observable(false); //Visual Checkbox indicator for different values
		viewModel.numRowsChildren = ko.observable("");
		viewModel.minChildren = ko.observable(""); //minCharacters, minChoices or min depending on child type
		viewModel.maxChildren = ko.observable("");
		viewModel.formulaChildren = ko.observable("");
		viewModel.decimalsChildren = ko.observable("");
		viewModel.unitChildren = ko.observable("");
		viewModel.possibleAnswersChildren = ko.observableArray();
		viewModel.possibleAnswersMatch = ko.observable(false);
	}

	viewModel.cellTypeChanged = function (newType){
		if (newType == 3){
			if (!modelsForFormula.includes(viewModel)){
				modelsForFormula.push(viewModel)
			}
		} else if (newType == 6){
			if (!modelsForNumber.includes(viewModel)){
				modelsForNumber.push(viewModel)
			}
		} else {
			let index = modelsForFormula.indexOf(viewModel)
			if (index >= 0){
				modelsForFormula.splice(index, 1)
			}
			index = modelsForNumber.indexOf(viewModel)
			if (index >= 0){
				modelsForNumber.splice(index, 1)
			}
		}
	}
	
	viewModel.cellStyle = function() {
		if (viewModel.cellType() == 4) {
			return viewModel.useRadioButtons() ? 'RadioButton' : 'SelectBox';
		}
		
		if (viewModel.cellType() == 5) {
			return viewModel.useCheckboxes() ? 'CheckBox' : 'ListBox';
		}
	}

	viewModel.cellType.subscribe(viewModel.cellTypeChanged);

	viewModel.cellTypeChanged(viewModel.cellType());
	
	viewModel.dependsOn = function(alias) {
		if (this.variables.length == 0) {
			this.variables = getVariablesForFormula(this.formula());
		}
		return this.variables.indexOf(alias) >= 0;
	}
		
	viewModel.refreshResult = function() {
		refreshResult(this);
	}

	return viewModel;
}

function newComplexPossibleAnswersViewModel(answers)
{
	var viewModel = ko.observableArray();
	if (answers != null) {		
		for (var i = 0; i < answers.length; i++)
		{
			if (answers[i].hasOwnProperty("isViewModel") && answers[i].isViewModel)
			{
				viewModel.push(newPossibleAnswerViewModel(answers[i].id(), answers[i].uniqueId(), answers[i].shortname(), answers[i].dependentElementsString(), answers[i].title(), answers[i].scoring, answers[i].ecfScore(), answers[i].ecfProfile(), answers[i].exclusive));
			} else {
				viewModel.push(newPossibleAnswerViewModel(answers[i].id, answers[i].uniqueId, answers[i].shortname, answers[i].dependentElementsString, answers[i].title, answers[i].scoring, answers[i].ecfScore, answers[i].ecfProfile, answers[i].exclusive));
			}
		}
	}
	return viewModel;
}

function shuffle(array) {
    var counter = array.length;

    // While there are elements in the array
    while (counter > 0) {
        // Pick a random index
        var index = Math.floor(Math.random() * counter);

        // Decrease counter by 1
        counter--;

        // And swap the last element with it
        var temp = array[counter];
        array[counter] = array[index];
        array[index] = temp;
    }

    return array;
}

function sortByColumn(answers, columns)
{	
	var counter = answers.length;
	if (columns <= 1) return answers;
	var rows = Math.ceil( answers.length / columns, 10);
	
	var answersByColumn = [];
	for (var i = 0; i < columns; i++)
	{
		var innerArray = [];
		answersByColumn.push([]);
	}
	var currentcolumn = 0;
	var currentcolumnsize = rows;
	
	for (var i = 0; i < answers.length; i++)
	{
		if (answersByColumn[currentcolumn].length == currentcolumnsize)
		{
			currentcolumn++;
		}
		answersByColumn[currentcolumn].push(answers[i]);
	}
	
	for (var i = 0; i < answersByColumn.length; i++)
	{
		while (answersByColumn[i].length < currentcolumnsize)
		{
			var dummy = [];
			dummy.id = ko.observable("dummy");
			answersByColumn[i].push(dummy);
			counter++;
		}
	}
	
	var result = [];
	for (var i = 0; i < counter; i++)
	{
		result.push(answersByColumn[i % columns][0]);
		answersByColumn[i % columns].shift();
	}
	
	return result;
}
