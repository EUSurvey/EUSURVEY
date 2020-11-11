function getNiceHelp(help)
{
	if (help == null || help.trim().length == 0) return "<br />";

	return addIconToHelp(help);	
}

function addIconToHelp(help)
{
	//if (help.indexOf("<span") == 0 || help.indexOf("<div") == 0)
	//{
	//	return help.substring(0, help.indexOf(">")+1) + "<span class='glyphicon glyphicon-question-sign'></span>&nbsp;" + help.substring(help.indexOf(">")+1);
	//}
	
	return "<span onclick='$(this).next().toggle()' class='glyphicon glyphicon-question-sign'></span><div style='display: none; padding-top: 5px;'>" + help + "</div><br />";
}

function newFileViewModel(uid, name, comment, longdesc, cleanComment, width)
{
	var viewModel = [];
	viewModel.uid =  ko.observable(uid == "null" ? "" : uid);
	viewModel.name = ko.observable(name == "null" ? "" : name);
	viewModel.comment = ko.observable(comment == "null" ? "" : comment);
	viewModel.longdesc = ko.observable(longdesc == "null" ? "" : longdesc);
	viewModel.cleanComment = ko.observable(cleanComment);
	viewModel.width = ko.observable(width);
		
	viewModel.copy = function()
	{
		var copy = [];
		
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
		viewModel.push(newFileViewModel(files[i].uid, files[i].name, files[i].comment, files[i].longdesc, files[i].cleanComment, files[i].width));
	}
	
	return viewModel;
}

function newMatrixItemViewModel(id, uniqueId, optional, shortname, readonly, title, originalTitle, isDependentMatrixQuestion, css, index)
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
	return viewModel;
}

function newMatrixItemsViewModel(items)
{
	var viewModel = ko.observableArray();
	for (var i = 0; i < items.length; i++)
	{
		viewModel.push(newMatrixItemViewModel(items[i].id, items[i].uniqueId, items[i].optional, items[i].shortname, items[i].readonly, items[i].title, items[i].originalTitle, items[i].isDependentMatrixQuestion, items[i].css, i));
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

function newPossibleAnswerViewModel(id, uniqueId, shortname, dependentElementsString, title, scoring)
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
	
	viewModel.titleForDisplayMode = function(displayMode)
	{
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
		viewModel.push(newPossibleAnswerViewModel(answers[i].id, answers[i].uniqueId, answers[i].shortname, answers[i].dependentElementsString, answers[i].title, answers[i].scoring));
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
	var viewModel = [];
	
	viewModel.isViewModel = true;
	viewModel.id = ko.observable(element.id());
	viewModel.title = ko.observable("");
	viewModel.points = ko.observable(0);
	viewModel.scoring = element;
	
	return viewModel;
}

function newScoringViewModel(element)
{
	var viewModel = [];
	
	viewModel.isViewModel = true;
	
	if (element != null)
	{
		if (element.hasOwnProperty("isViewModel"))
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

function newBasicViewModel(element)
{
	var viewModel = {};
	
	viewModel.isViewModel = true;
	
	viewModel.scoringItems = ko.observableArray();
	viewModel.optional = ko.observable(true);
	viewModel.css = ko.observable(true);	
	
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
		viewModel.title = ko.observable(element.title);
		viewModel.originalTitle = ko.observable(element.originalTitle);
		viewModel.type = element.type;
		viewModel.id = ko.observable(element.id);
		viewModel.uniqueId = ko.observable(element.uniqueId);
		viewModel.shortname = ko.observable(element.shortname);
		viewModel.scoring = ko.observable(element.scoring);
		viewModel.points = ko.observable(element.points);
		viewModel.locked = ko.observable(element.locked);
		viewModel.css = ko.observable(element.css);
		viewModel.optional = ko.observable(element.optional);
		viewModel.isDelphiQuestion = ko.observable(element.isDelphiQuestion);
		
		if (element.scoringItems != null)
		{
			for (var i = 0; i < element.scoringItems.length; i++)
			{
				viewModel.scoringItems.push(newScoringViewModel(element.scoringItems[i]));
			}
		}
	}
	
	viewModel.copy = function()
	{
		var copy = [];
		
	    for (var prop in this) {
	        var propVal = this[prop];
	        if (ko.isObservable(propVal)) {
	            var val = propVal();
	            if (prop == "dependentElementsStrings")
	            {
	            	copy[prop] = ko.observableArray();
	            	for (var i = 0; i < this.dependentElementsStrings().length; i++)
			    	{
			    		var deps = this.dependentElementsStrings()[i]();
			    		copy[prop].push(ko.observable(deps));
			    	}
	            	continue;
	            } else if (prop == "questions")
	            {
	            	copy[prop] = ko.observableArray();
	            	for (var i = 0; i < this.questions().length; i++)
			    	{
			    		var copiedquestion = this.questions()[i];
			    		var newmatrixquestion = newMatrixItemViewModel(getNewId(), getNewId(), copiedquestion.optional(), getNewShortname(), copiedquestion.readonly(), copiedquestion.title(), copiedquestion.originalTitle(), copiedquestion.isDependentMatrixQuestion(), copiedquestion.css(), i);
			    		newmatrixquestion.originalId = copiedquestion.id();
			    		copy[prop].push(newmatrixquestion);
			    	}
	            	continue;
	            } else if (prop == "answers")
	            {
	            	copy[prop] = ko.observableArray();
	            	for (var i = 0; i < this.answers().length; i++)
			    	{
			    		var copiedanswer = this.answers()[i];
			    		var newanswer = newMatrixItemViewModel(getNewId(), getNewId(), copiedanswer.optional(), getNewShortname(), copiedanswer.readonly(), copiedanswer.title(), copiedanswer.originalTitle(), copiedanswer.isDependentMatrixQuestion(), copiedanswer.css(), i)
			    		newanswer.originalId = copiedanswer.id();
			    		copy[prop].push(newanswer);
			    	}
	            	continue;
	            } else if (prop == "childElements")
	            {
	            	copy[prop] = ko.observableArray();
	            	for (var i = 0; i < this.childElements().length; i++)
			    	{
			    		var copiedquestion = this.childElements()[i];
			    		var newquestion = newRatingItemViewModel(getNewId(), getNewId(), copiedquestion.optional(), getNewShortname(), copiedquestion.title(), copiedquestion.originalTitle(), copiedquestion.css())
			    		newquestion.originalId = copiedquestion.id();
			    		copy[prop].push(newquestion);
			    	}
	            	continue;
	            } else if (prop == "possibleAnswers")
	            {
	            	copy[prop] = ko.observableArray();
	            	for (var i = 0; i < this.possibleAnswers().length; i++)
			    	{
			    		var copiedanswer = this.possibleAnswers()[i];
			    		var newanswer = newPossibleAnswerViewModel(getNewId(), getNewId(), getNewShortname(), copiedanswer.dependentElementsString(), copiedanswer.title(), copiedanswer.scoring);
			    		newanswer.originalId = copiedanswer.id();
			    		copy[prop].push(newanswer);
			    	}
	            	continue;
	            } else if (prop == "scoringItems")
	            {
	            	copy[prop] = ko.observableArray();
	            	for (var i = 0; i < this.scoringItems().length; i++)
			    	{
			    		var copieditem = this.scoringItems()[i];
			    		var newitem = newScoringViewModel(copieditem);
			    		newitem.originalId = copieditem.id();
			    		copy[prop].push(newitem);
			    	}
	            	continue;
	            }
	            
	            copy[prop] = ko.observable(val);
	        } else {
	        	copy[prop] = propVal;
	        }
	    }	
		
	    copy.id(getNewId());
	    copy.uniqueId(getNewId());
	    copy.shortname(getNewShortname());
	    copy.originalId = this.id();
	    copy.locked(false);
	    
	    if (this.type == "GalleryQuestion")
	    {
	    	for (var i = 0; i < copy.files().length; i++)
	    	{
	    		copy.files()[i] = copy.files()[i].copy();
	    	}
	    	
	    	copy.rows = ko.dependentObservable(function() {
	    	    var result = [],
	    	        colLength = parseInt(copy.columns(), 10),
	    	        row;

	    	    //loop through items and push each item to a row array that gets pushed to the final result
	    	    for (var i = 0, j = copy.files().length; i < j; i++) {
	    	        if (i % colLength === 0) {
	    	            if (row) {
	    	              result.push(row);     
	    	            }
	    	            row = [];
	    	        }
	    	        row.push(copy.files()[i]);
	    	    }

	    	    //push the final row  
	    	    if (row) {
	    	        result.push(row);
	    	    }

	    	    return result;
	    	}, viewModel);
	    }
	    
	    copy.replaceTriggers = function(dict)
		{			
			if (this.type.indexOf("ChoiceQuestion") > 0)
		    {
				for (var i = 0; i < this.possibleAnswers().length; i++)
		    	{
					var deps = this.possibleAnswers()[i].dependentElementsString();
					var changed = false;
					if (deps.length > 0)
					{
						var dependentIds = deps.split(";");
						for (var j = 0; j < dependentIds.length; j++)
						{
							var id = dependentIds[j];
							if (id.length > 0 && dict.hasOwnProperty(id))
							{
								deps = deps.replace(id + ";", dict[id] + ";");
								changed = true;
							}
						}					
					}
					if (changed)
					{
						this.possibleAnswers()[i].dependentElementsString(deps);
					}
		    	}				
		    } else if (this.type == "Matrix")
		    {
		    	for (var i = 0; i < this.dependentElementsStrings().length; i++)
		    	{
		    		var deps = this.dependentElementsStrings()[i]();
		    		if (deps.length > 0)
					{
						var dependentIds = deps.split(";");
						for (var j = 0; j < dependentIds.length; j++)
						{
							var id = dependentIds[j];
							if (id.length > 0 && dict.hasOwnProperty(id))
							{
								deps = deps.replace(id + ";", dict[id] + ";");
								changed = true;
							}
						}					
					}
					if (changed)
					{
						this.dependentElementsStrings()[i](deps);
					}
		    	}
		    }
		};	    
	    
		return copy;
	}
	
	return viewModel;
}

function newSectionViewModel(element)
{
	var viewModel = newBasicViewModel(element)
	viewModel.level = ko.observable(element.level);	
	viewModel.tabTitle = ko.observable(element.tabTitle);	
	
	return viewModel;
}

function newTextViewModel(element)
{
	var viewModel = newBasicViewModel(element)
	viewModel.optional = ko.observable(element.optional);	
	
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
	
	return viewModel;
}

function newRulerViewModel(element)
{
	var viewModel = newBasicViewModel(element)
	viewModel.height = ko.observable(element.height);
	viewModel.style = ko.observable(element.style);	
	viewModel.color = ko.observable(element.color);	
	viewModel.optional = true;	

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
		if (this.order() != null && this.order() == 1)
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
		} else if (this.order() != null && this.order() == 2)
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
		if (responsive) limit = 3;
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
	viewModel.useRadioButtons = ko.observable(element.useRadioButtons);	
	viewModel.minChoices = ko.observable(0);
	viewModel.maxChoices = ko.observable(0);
	viewModel.choiceType = ko.observable(element.useRadioButtons ? "radio" : "select");
	
	return viewModel;
}

function newMultipleChoiceViewModel(element)
{
	var viewModel = newChoiceViewModel(element)
	
	viewModel.single = false;
	viewModel.useCheckboxes = ko.observable(element.useCheckboxes);	
	viewModel.minChoices = ko.observable(element.minChoices);
	viewModel.maxChoices = ko.observable(element.maxChoices);	
	viewModel.choiceType = ko.observable(element.useCheckboxes ? "checkbox" : "list");
	viewModel.noNegativeScore = ko.observable(element.noNegativeScore);	
	
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
	
	return viewModel;
}

function newRegExViewModel(element)
{
	var viewModel = newFreeTextViewModel(element);
	viewModel.regex = ko.observable(element.regex);	
	return viewModel;
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
	viewModel.initialSliderPosition = ko.observable(element.initialSliderPosition != null ? element.initialSliderPosition : "Left");
	viewModel.displayGraduationScale = ko.observable(element.displayGraduationScale);
	
	viewModel.labels = function()
	{
		var result =[];
		result[result.length] = this.min().toString();
		var v = this.min();
		
//		if (this.displayGraduationScale())
//		{
//			var distance = this.max() - this.min();
//			var tickStep = 1; 
//			while (distance / tickStep > 10) {
//				tickStep *= 2;
//			}
//			
//			while (v < this.max())
//			{
//				v = Math.round((v + tickStep)* 100) / 100;
//				result[result.length] = v;
//			}
//		}
		
		result[result.length] = this.max().toString();		
			
		return result;
	}
	
	viewModel.ticks = function()
	{
//		if (!this.displayGraduationScale())
//		{
			return "[" + this.min() + "," + this.max() + "]";
//		}
//		
//		var distance = this.max() - this.min();
//		var tickStep = 1; 
//		while (distance / tickStep > 10) {
//			tickStep *= 2;
//		}
//		
//		
//		var result = "[";
//		var v = this.min();
//		result += v;
//		
//		while (v < this.max())
//		{
//			v = Math.round((v + tickStep)* 100) / 100;
//			result += ",";
//			result += v;
//		}
//		
//		//result += ",";
//		//result += this.max();
//		
//		result += "]";
//		
//		return result;
	}
	
	viewModel.step = function()
	{
//		var result = 1;
//		for (var i = 0; i < this.decimalPlaces(); i++)
//		{
//			result = result / 10;
//		}
//		
//		return result;	
		
		var decimals = parseInt(this.decimalPlaces());
		if (decimals === 0)
		{
			return 1;
		}
		return 10 ** (-1 * decimals);
	}
	
	viewModel.increase = function(element)
	{
		var min = parseFloat(this.min());
		var max = parseFloat(this.max());
		var input = $("#answer" + element.id());
		var value = parseFloat($(input).bootstrapSlider().bootstrapSlider('getValue'));
		if (value < max) {
			$(input).bootstrapSlider().bootstrapSlider('setValue', value + this.step());
		}
	}
	
	viewModel.decrease = function(element)
	{
		var min = parseFloat(this.min());
		var max = parseFloat(this.max());
		var input = $("#answer" + element.id());
		var value = parseFloat($(input).bootstrapSlider().bootstrapSlider('getValue'));
		if (value > min) {
			$(input).bootstrapSlider().bootstrapSlider('setValue', value - this.step());
		}
	}
	
	viewModel.initialValue = function() {
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
	}
	
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
	if (columns == 1) return answers;
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