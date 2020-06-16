var UndoProcessor = function() {
	this.undostack = [];
	this.redostack = [];
	this.positions = [];
	
	this.clear = function()
	{
		this.undostack = [];
		this.redostack = [];
		this.positions = [];
		_actions.UndoEnabled(false);
		_actions.RedoEnabled(false);
	}
	
	this.addUndoStep = function(data)
	{
		this.undostack.push(data);
		_actions.UndoEnabled(true);
		_actions.SaveEnabled(true);
	}

	this.undo = function()
	{
		if (this.undostack.length == 0) return;
		
		var step = this.undostack.pop();
		
		var id = step[1];
		_elementProperties.selectedelement = $("#content").find("li[id='" + id + "']").first();
		var element = _elements[id];
		var position = step[2];
		
		if (element == null && $(_elementProperties.selectedelement).length == 0)
		{
			_elementProperties.selectedelement = $("td[data-id='" + id + "']");
			
			if ($(_elementProperties.selectedelement).length == 0)
			{
				_elementProperties.selectedelement = $("div.answertext[data-id='" + id + "']");
			}
			
			if ($(_elementProperties.selectedelement).length == 0)
			{
				_elementProperties.selectedelement = $("td[data-uid='" + step[2] + "']");	
			}
			
			if ($(_elementProperties.selectedelement).length == 0)
			{
				_elementProperties.selectedelement = $(".ratingquestion[data-id='" + id + "']");	
			}
		}
		
		var skipRedo = false;
		
		switch (step[0]) {
			case "CELLWIDTH":
				$(step[1]).width(step[2]);
				break;
			case "ADD":
				_actions.deletedElements.push(_elementProperties.selectedelement);			
				$(_elementProperties.selectedelement).remove();
				_elementProperties.selectedelement = null;
				removeFromNavigation(id);
				break;
			case "MOVE":
				var oldindex = parseInt(step[2]);
				var newindex = parseInt(step[3]);
				var parent = $(_elementProperties.selectedelement).closest("ul");
				if (oldindex == 0)
				{
					parent.prepend($(_elementProperties.selectedelement));
				} else {
					if (oldindex > newindex)
					{
						$(parent.find("li.survey-element")[oldindex]).after($(_elementProperties.selectedelement));
					} else {
						$(parent.find("li.survey-element")[oldindex-1]).after($(_elementProperties.selectedelement));
					}
				}
				moveItemInNavigation(oldindex, newindex);
				break;
			case "DELETE":
				var idsandpositions = step[1];
				var arrelements = idsandpositions.split(";");
				for (var i = arrelements.length - 1; i >= 0; i--)
				{
					if (arrelements[i].length > 0)
					{
						var elemid = arrelements[i].substring(0, arrelements[i].indexOf("|"));
						var elemposition = arrelements[i].substring(arrelements[i].indexOf("|")+1);
						var selectedelement = _actions.deletedElements.pop();
						
						if (selectedelement.hasClass("matrix-question"))
						{
							var parentid = elemposition.substring(elemposition.indexOf("@")+1);
							var index = elemposition.substring(0, elemposition.indexOf("@"));
							var matrix = _elements[parentid];
							var elem = _actions.deletedModels.pop();
							matrix.questions.splice(index-1, 0, elem);
							
							var dependentElementsStrings = step[2];
							matrix.setDependentElementsStrings(dependentElementsStrings[matrix.id()]);
							
							//recreate dependency hints
							for (var j = 0; j < matrix.answers().length; j++)
							{
								var combinedid = elem.id() + "|" + matrix.answers()[j].id()  ;
								var depindex = (index-1) * matrix.answers().length + j;
								if (dependentElementsStrings[matrix.id()].length >= depindex)
								{
									var dependency = dependentElementsStrings[matrix.id()][depindex];
									if (dependency != null && dependency.trim().length > 0)
									{
										var ids = dependency.split(";");
										for (var k = 0; k < ids.length; k++)
										{
											if (ids[k].trim().length > 0)
											{
												var li = $('li[id=' + ids[k] + ']');
												if ($(li).length > 0)
												{
													var triggers = "";
													if ($(li).attr("data-triggers"))
													{
														triggers = $(li).attr("data-triggers");
													}
													if (triggers.indexOf(combinedid) == -1)
													{
														if (triggers.length > 0)
														{
															triggers = triggers + ";"
														}
														triggers += combinedid;
														$(li).attr("data-triggers", triggers);
													}
												}
											}
										}
									}
								}
							}
							
							updateDependenciesView();
							
							addElementHandler($($("#content").find("li[id=" + parentid + "]").find(".matrixtable").find("tr")[index]).find("td").first());
							updateNavigation($("#content").find("li[id=" + parentid + "]"), parentid);
						} else if (selectedelement[0].tagName.toLowerCase() == "tr")
						{
							var parentid = elemposition.substring(elemposition.indexOf("@")+1);
							var index = elemposition.substring(0, elemposition.indexOf("@"));
							var table = _elements[parentid];
							table.questions.splice(index-1, 0, _actions.deletedModels.pop());
							
							addElementHandler($($("#content").find("li[id=" + parentid + "]").find(".tabletable").find("tr")[index]).find("td").first());
							updateNavigation($("#content").find("li[id=" + parentid + "]"), parentid);
						} else if (selectedelement.hasClass("table-header"))
						{
							var parentid = elemposition.substring(elemposition.indexOf("@")+1);
							var index = elemposition.substring(0, elemposition.indexOf("@"));
							
							var table = _elements[parentid];
							table.answers.splice(index-1, 0, _actions.deletedModels.pop());
							addElementHandler($($("#content").find("li[id=" + parentid + "]").find(".tabletable").find("tr").first().find("td")[index]));
							updateNavigation($("#content").find("li[id=" + parentid + "]"), parentid);
						} else if (selectedelement.hasClass("matrix-header"))
						{
							var parentid = elemposition.substring(elemposition.indexOf("@")+1);
							var index = parseInt(elemposition.substring(0, elemposition.indexOf("@")));

							var matrix = _elements[parentid];
							var elem = _actions.deletedModels.pop();
							matrix.answers.splice(index-1, 0, elem);
							
							matrix.columns(matrix.columns()+1);
							var dependentElementsStrings = step[2];
							matrix.setDependentElementsStrings(dependentElementsStrings[matrix.id()]);
							
							//recreate dependency hints
							for (var j = 0; j < matrix.questionsOrdered().length; j++)
							{
								var combinedid = matrix.questionsOrdered()[j].id() + "|" + elem.id();
								var depindex = j * matrix.answers().length + index;
								if (dependentElementsStrings[matrix.id()].length >= depindex - 1)
								{
									var dependency = dependentElementsStrings[matrix.id()][depindex-1];
									if (dependency != null && dependency.trim().length > 0)
									{
										var ids = dependency.split(";");
										for (var k = 0; k < ids.length; k++)
										{
											if (ids[k].trim().length > 0)
											{
												var li = $('li[id=' + ids[k] + ']');
												if ($(li).length > 0)
												{
													var triggers = "";
													if ($(li).attr("data-triggers"))
													{
														triggers = $(li).attr("data-triggers");
													}
													if (triggers.indexOf(combinedid) == -1)
													{
														if (triggers.length > 0)
														{
															triggers = triggers + ";"
														}
														triggers += combinedid;
														$(li).attr("data-triggers", triggers);
													}
												}
											}
										}
									}
								}
							}
							
							updateDependenciesView();							
							addElementHandler($($("#content").find("li[id=" + parentid + "]").find(".matrixtable").find("tr").first().find("td")[index]));
							updateNavigation($("#content").find("li[id=" + parentid + "]"), parentid);
						} else {					
							if (elemposition < 1)
							{
								$("#content").prepend(selectedelement);
							} else {
								$($("#content").find("li")[elemposition-1]).after(selectedelement);
							}
							addElementHandler(selectedelement);
							_actions.deletedModels.pop();
							addToNavigation(selectedelement, elemposition);
							
							if ($(selectedelement).attr("data-triggers"))
							{
								var strtriggers = $(selectedelement).attr("data-triggers");
								var triggerids =strtriggers.split(";");
							
								for (var i = 0; i < triggerids.length; i++)
								{
									var triggerid = triggerids[i];									
									var selectedquestionid = $(selectedelement).attr("data-id");
									
									if (triggerid.length > 0 && selectedquestionid.length > 0)
									{
										addVisibility(triggerid, selectedquestionid);
									}
								}
								updateDependenciesView();
							}
						}
						
					}
				}
				checkContent();
				break;
			case "COPYPASTE":
				var ids = step[1].split(";");
				this.positions = [];
				for (var i = 0; i < ids.length; i++)
				{
					var item = $("#content").find("li[id='" + ids[i] + "']").first();
					if (item.length > 0)
					{
						this.positions.push(item.index());
						_actions.deletedElements.push($(item));	
						$(item).remove();
						removeFromNavigation(ids[i]);
					}
				}				
				break;
			case "CUTPASTE":
				var ids = step[1].split(";");
				var oldpos = step[2].split(";");
				var newpos = step[3].split(";");
				
				for (var i = ids.length-1; i >= 0; i--)
				{
					var item = $("#content").find("li[id='" + ids[i] + "']").first();
					if (item.length > 0)
					{
						var position = oldpos[i];
						if (position == 0)
						{
							$("#content").prepend(item);
						} else {
							$(item).insertAfter($($("#content").find("li")[position-1]));
						}
						moveItemInNavigation(oldpos[i],newpos[i]);
					}
				}
				break;
			case "Unit":
				element.unit(step[3]);
				break;
			case "Values":
				if (step[5] == "min")
				{
					element.min(step[3]);
					element.minString(step[3]);
				} else {
					element.max(step[3]);
					element.maxString(step[3]);
				}
				
				break;
			case "Text":				
				updateText(element, step[3], true);
				break;
			case "ConfirmationText":
				updateConfirmationText(element, step[3]);
				break;
			case "TabTitle":
				element.tabTitle(step[3]);
				break;
			case "Level":
				element.level(step[3]);
				updateTitles();
				break;
			case "Mandatory":
				var checked = step[3] == "true";
				setMandatoryInner(element, checked, null);
				break;
			case "Rows":
				if (element.type == "Matrix" || element.type == "Table")
				{
					var rows = splitText(step[3]);
					updateRows(element, rows);
				} else {
					element.numRows(step[3]);
					$(_elementProperties.selectedelement).find("textarea.freetext").css("height","");
				}
				break;
			case "Help":
				element.help(step[3]);
				element.niceHelp(getNiceHelp(element.help()));
				break;
			case "AcceptedNumberOfCharacters":
				if (step[5] == "min")
				{
					element.minCharacters(step[3]);
				} else {
					element.maxCharacters(step[3]);
					$(_elementProperties.selectedelement).find("textarea, input").not(":hidden").attr("disabled", "disabled");
					$(_elementProperties.selectedelement).find(".expand").TextAreaExpander();
				}				
				break;
			case "Style":
				if (element.type == "Matrix")
				{
					element.isSingleChoice(step[3] == "SingleChoice");
				} else if (element.type == "Ruler") {				
					element.style(step[3]);
				} else {
					if (step[3] == "RadioButton")
					{
						element.choiceType("radio");
						element.useRadioButtons(true);
					} else if (step[3] == "SelectBox")
					{
						element.useRadioButtons(false);
						element.choiceType("select");
					} else if (step[3] == "CheckBox")
					{
						element.choiceType("checkbox");
						element.useCheckboxes(true);
					} else if (step[3] == "ListBox")
					{
						element.choiceType("list");
						element.useCheckboxes(false);
					}
					updateChoice();
				}
				break;
			case "Display":
				var display = 0;
				if (step[3] == "1") display = 1;
				if (step[3] == "2") display = 2;		
				if (step[3] == "3") display = 3;		
				element.displayMode(display);
				break;
			case "Order":
				var order = 0;
				if (step[3] == "1") order = 1;
				if (step[3] == "2") order = 2;		
				element.order(order);
				break;
			case "Columns":
				if (element.type == "GalleryQuestion")
				{
					element.columns(step[2]);			
					updateGallery();
				} else if (element.type == "Matrix" || element.type == "Table")
				{
					var columns = splitText(step[3]);
					updateColumns(element, columns);
				} else {
					element.numColumns(step[2]);
					updateChoice();
				}
				break;
			case "PossibleAnswers":
				updatePossibleAnswers($(_elementProperties.selectedelement), step[3], true);
				break;
			case "Shortnames":
				var ids = step[2];
				var oldvalues = step[3];
				var label = step[5];
				
				for (var i = 0; i < ids.length; i++)
				{
					updateShortnameInner(label, element, i, oldvalues[i]);
				}
				break;
			case "ADDANSWER":
				element.possibleAnswers.pop();
				updateNavigation($(_elementProperties.selectedelement), $(_elementProperties.selectedelement).attr("id"));
				break;
			case "REMOVEANSWER":
				element.possibleAnswers.push(step[2]);
				updateNavigation($(_elementProperties.selectedelement), $(_elementProperties.selectedelement).attr("id"));
				break;
			case "ADDCOLUMN":
				element.answers.pop();
				updateNavigation($(_elementProperties.selectedelement), $(_elementProperties.selectedelement).attr("id"));
				break;
			case "REMOVECOLUMN":
				element.answers.push(step[2]);
				addElementHandler($(_elementProperties.selectedelement));
				updateNavigation($(_elementProperties.selectedelement), $(_elementProperties.selectedelement).attr("id"));
				break;
			case "ADDROW":
				element.questions.pop();
				updateNavigation($(_elementProperties.selectedelement), $(_elementProperties.selectedelement).attr("id"));
				break;
			case "REMOVEROW":
				element.questions.push(step[2]);
				addElementHandler($(_elementProperties.selectedelement));
				updateNavigation($(_elementProperties.selectedelement), $(_elementProperties.selectedelement).attr("id"));
				break;
			case "Identifier":
				updateIdentifier(element, id, step[3], true);		
				break;
			case "ReadOnly":
				element.readonly(step[3]);
				break;
			case "Attribute":
				element.isAttribute(step[3]);
				break;
			case "Name":
				element.attributeName(step[3]);
				break;
			case "Unique":
				element.isUnique(step[3]);
				break;
			case "Comparable":
				element.isComparable(step[3]);
				break;
			case "Password":
				element.isPassword(step[3]);
				break;
			case "Interdependency":
				element.isInterdependent(step[3]);
				break;
			case "Visibility":
				var oldtriggerids = step[3].split(";");
				var newtriggerids = step[4].split(";");
				var selectedquestionids = step[5].split(";");
				for (var i = 0; i < newtriggerids.length; i++)
				{
					var triggerid = newtriggerids[i];
					for (var j = 0; j < selectedquestionids.length; j++)
					{
						var selectedquestionid = selectedquestionids[j];
						if (triggerid.length > 0 && selectedquestionid.length > 0)
						{
							removeVisibility(triggerid, selectedquestionid);
						}					
					}				
				}
				for (var i = 0; i < oldtriggerids.length; i++)
				{
					var triggerid = oldtriggerids[i];
					for (var j = 0; j < selectedquestionids.length; j++)
					{
						var selectedquestionid = selectedquestionids[j];
						if (triggerid.length > 0 && selectedquestionid.length > 0)
						{
							addVisibility(triggerid, selectedquestionid);
						}
					}				
				}
				updateDependenciesView();
				break;
			case "NumberOfChoices":
				if (step[5] == "min")
				{
					element.minChoices(step[3]);
				} else {
					element.maxChoices(step[3]);
				}
				
				break;
			case "DecimalPlaces":
				$(_elementProperties.selectedelement).find("input[name^='decimalplaces']").first().val(step[3]);
				break;
			case "NumberOfAnsweredRows":
				if (step[5] == "min")
				{
					element.minRows(step[3]);
				} else if (step[5] == "max")
				{
					element.maxRows(step[3]);
				}
				break;
			case "Size":
				if (element.type == "Image")
				{
					updateImageSize(element, step[3], null, true);
				} else {
					updateMatrixSize(element, step[3], true);
				}
				break;
			case "ImageUpload":
				deleteImageFile(contextpath + "/files/" + step[2]);
				skipRedo = true;
				break;
			case "FileUpload":
				if (element.type == "GalleryQuestion")
		    	{
					deleteGalleryFile(step[2], null, true);
		    	} else {
		    		deleteDownloadFile(step[2], null, id, true);
		    	}
				skipRedo = true;
				break;
			case "Align":
				element.align(step[3]);
				break;
			case "DescriptiveText":
				element.originalTitle(step[3]);
				break;
			case "LongDescription":
				element.longdesc(step[3]);
				break;
			case "LongDescriptionGallery":
				var galleryid =  step[1];
				var gallery = _elements[galleryid];
				var uid = step[2];
				
				_elementProperties.selectedelement = $("td[data-uid='" + step[2] + "']");
								
				for (var i = 0; i < gallery.files().length; i++)
				{
					var file = gallery.files()[i];
					if (file.uid() == uid)
					{
						file.longdesc(step[3]);
						break;
					}
				}
				break;
			case "RegularExpression":
				element.regex(step[3]);
				break;
			case "ImageSelectable":
				element.selection(step[3]);
				updateGallery();
				checkGalleryProperties(true);
				//the previous line adds a "mandatory" change to the undo stack
				this.undostack.pop();
				break;
			case "MaxSelections":
				element.limit(step[3]);
				updateGallery();
				break;
			case "AutoNumbering":
				element.numbering(step[2]);
				break;
			case "LabelText":
				element.confirmationlabel(step[3]);
				$(_elementProperties.selectedelement).find("a.confirmationlabel").html(step[3]);
				break;
			case "Title":
				var galleryid =  step[1];
				var gallery = _elements[galleryid];
				var uid = step[2];
				
				_elementProperties.selectedelement = $("td[data-uid='" + step[2] + "']");
				
				for (var i = 0; i < gallery.files().length; i++)
				{
					var file = gallery.files()[i];
					if (file.uid() == uid)
					{
						file.name(step[3]);
						break;
					}
				}
				break;
			case "points":
				element.points(step[3]);
				break;
			case "scoring":
				element.scoring(step[3]);
				break;
			case "type":
				var scoring = element.getScoringItem(step[5]);
				scoring.type(step[3]);
				break;
			case "ruleValue":
				var scoring = element.getScoringItem(step[5]);
				scoring.value(step[3]);
				break;
			case "ruleValue2":
				var scoring = element.getScoringItem(step[5]);
				scoring.value2(step[3]);
				break;
			case "addExpectedAnswer":
				_actions.deletedElements.push(element.scoringItems.pop());
				break;
			case "removeExpectedAnswer":
				element.scoringItems.push(_actions.deletedElements.pop());
				break;
			case "correct":
				if (element.type == "SingleChoiceQuestion" || element.type == "MultipleChoiceQuestion")
				{
					var answer = element.getChild(step[2]);
					answer.scoring.correct(step[3]);
				} else  {
					var scoring = element.getScoringItem(step[2]);
					scoring.correct(step[3]);
				}
				break;
			case "IconType":
				element.iconType(step[3]);
				break;
			case "NumIcons":
				element.numIcons(step[3]);
				break;
			case "Feedback":
				if (element.type == "SingleChoiceQuestion" || element.type == "MultipleChoiceQuestion")
				{
					var answer = element.getChild(step[2]);
					answer.scoring.feedback(step[3]);
				} else  {
					var scoring = element.getScoringItem(step[2]);
					scoring.feedback(step[3]);
				}
				break;
			case "FileMoved":
				var button = $("tr[data-uid='" + step[2] + "']").find("a").first();
				moveGalleryFile(step[2], button, !step[3], true);
				break;
			case "Color":
				element.color(step[3]);
				break;
			case "Height":
				element.height(step[3]);
				break;
		}
		
		var advancedopen = $(".advancedtogglebutton").find(".glyphicon-minus-sign").length > 0;
		var element = _elementProperties.selectedelement;
		_elementProperties.deselectAll();
		
		if (element != null) _elementProperties.showProperties($(element), null, false);
		if (advancedopen) toggleAdvancedProperties($(".advancedtogglebutton").find(".glyphicon").first());
		
		if (!skipRedo)
		{
			_actions.RedoEnabled(true);
			this.redostack.push(step);
		}
		
		if (this.undostack.length == 0)
		{
			_actions.UndoEnabled(false);
		}

	}
	
	this.redo = function()
	{
		if (this.redostack.length == 0) return;
		
		var step = this.redostack.pop();
		var id = step[1];
		var position = step[2];
		_elementProperties.selectedelement = $("#content").find("li[id='" + id + "']").first();
		var element = _elements[id];
		
		if (element == null && $(_elementProperties.selectedelement).length == 0)
		{
			_elementProperties.selectedelement = $("td[data-id='" + id + "']");
			
			if ($(_elementProperties.selectedelement).length == 0)
			{
				_elementProperties.selectedelement = $("div.answertext[data-id='" + id + "']");
			}
			
			if ($(_elementProperties.selectedelement).length == 0)
			{
				_elementProperties.selectedelement = $("td[data-uid='" + step[2] + "']");	
			}
			
			if ($(_elementProperties.selectedelement).length == 0)
			{
				_elementProperties.selectedelement = $(".ratingquestion[data-id='" + id + "']");	
			}
		}
		
		switch (step[0]) {
			case "CELLWIDTH":
				$(step[1]).width(step[3]);
				break;
			case "ADD":
				var element = _actions.deletedElements.pop();
				if ($("#content").find("li").length > position)
				{
					$($("#content").find("li")[position]).before(element);
				} else {
					$("#content").append(element);
				}
				addElementHandler(element);		
				addToNavigation(element, $(element).index());
				break;
			case "MOVE":
				var oldindex = parseInt(step[2]);
				var newindex = parseInt(step[3]);
				var parent = $(_elementProperties.selectedelement).closest("ul");

				if (newindex == 0)
				{
					parent.prepend($(_elementProperties.selectedelement));
				} else {
					if (oldindex < newindex)
					{
						$(parent.find("li.survey-element")[newindex]).after($(_elementProperties.selectedelement));
					} else {
						$(parent.find("li.survey-element")[newindex-1]).after($(_elementProperties.selectedelement));
					}
				}
				moveItemInNavigation(newindex, oldindex);
				break;
			case "DELETE":
				_elementProperties.deselectAll();
				
				var idsandpositions = step[1];
				var arrelements = idsandpositions.split(";");
				for (var i = 0; i < arrelements.length; i++)
				{
					if (arrelements[i].length > 0)
					{
						var elemid = arrelements[i].substring(0, arrelements[i].indexOf("|"));
						var elemposition = arrelements[i].substring(arrelements[i].indexOf("|")+1);
						_elementProperties.selectedelement = $("#content").find("#" + elemid).first();
						
						$(_elementProperties.selectedelement).addClass("selectedquestion");
					}
				}
	
				_actions.deleteElement2(true);
				break;
			case "COPYPASTE":
				var ids = step[1].split(";");
				for (var i = 0; i < ids.length; i++)
				{
					var position = this.positions.pop();
					var item = _actions.deletedElements.pop();
					
					if (position == 0)
					{
						$("#content").prepend(item);
					} else {
						$(item).insertAfter($("#content").find("li")[position-1]);
					}
					addElementHandler(item);
					addToNavigation(item, position);
				}
				break;
			case "CUTPASTE":
				var ids = step[1].split(";");
				var oldpos = step[2].split(";");
				var newpos = step[3].split(";");
				
				for (var i = 0; i < ids.length; i++)
				{
					var item = $("#content").find("li[id='" + ids[i] + "']").first();
					if (item.length > 0)
					{
						var position = newpos[i];
						if (position == 0)
						{
							$("#content").prepend(item);
						} else {
							$(item).insertAfter($($("#content").find("li")[position-1]));
						}	
						moveItemInNavigation(newpos[i],oldpos[i]);
					}
				}
				break;
			case "Unit":
				element.unit(step[4]);
				break;
			case "Values":
				if (step[5] == "min")
				{
					element.min(step[4]);
					element.minString(step[4]);
				} else {
					element.max(step[4]);
					element.maxString(step[4]);
				}
				break;
			case "Text":
				updateText(element, step[4], true);
				break;
			case "ConfirmationText":
				updateConfirmationText(element, step[4]);
				break;
			case "TabTitle":
				element.tabTitle(step[4]);
				break;
			case "Level":
				element.level(step[4]);
				updateTitles();
				break;
			case "Mandatory":
				var checked = step[4] == "true";
				setMandatoryInner(element, checked, null);
				break;
			case "Rows":
				if (element.type == "Matrix" || element.type == "Table")
				{
					var rows = splitText(step[4]);
					updateRows(element, rows);
				} else {
					element.numRows(step[4]);
					$(_elementProperties.selectedelement).find("textarea.freetext").css("height","");
				}
				break;
			case "Help":
				element.help(step[4]);
				element.niceHelp(getNiceHelp(element.help()));
				break;
			case "AcceptedNumberOfCharacters":
				if (step[5] == "min")
				{
					element.minCharacters(step[4]);
				} else {
					element.maxCharacters(step[4]);
					$(_elementProperties.selectedelement).find("textarea, input").not(":hidden").attr("disabled", "disabled");
					$(_elementProperties.selectedelement).find(".expand").TextAreaExpander();
				}
				break;
			case "Style":
				if (element.type == "Matrix")
				{
					element.isSingleChoice(step[4] == "SingleChoice");
				} else if (element.type == "Ruler") {				
					element.style(step[4]);
				} else {
					if (step[4] == "RadioButton")
					{
						element.choiceType("radio");
						element.useRadioButtons(true);
					} else if (step[4] == "SelectBox")
					{
						element.useRadioButtons(false);
						element.choiceType("select");
					} else if (step[4] == "CheckBox")
					{
						element.choiceType("checkbox");
						element.useCheckboxes(true);
					} else if (step[4] == "ListBox")
					{
						element.choiceType("list");
						element.useCheckboxes(false);
					}
					updateChoice();
				}
				break;
			case "Display":
				var display = 0;
				if (step[4] == "1") display = 1;
				if (step[4] == "2") display = 2;		
				if (step[4] == "3") display = 3;		
				element.displayMode(display);
				break;
			case "Order":
				var order = 0;
				if (step[4] == "Alphabetical") order = 1;
				if (step[4] == "Random") order = 2;		
				element.order(order);
				break;
			case "Columns":
				if (element.type == "GalleryQuestion")
				{
					element.columns(step[3]);
					updateGallery();
				} else if (element.type == "Matrix" || element.type == "Table")
				{
					var columns = splitText(step[4]);
					updateColumns(element, columns);
				} else {
					element.numColumns(step[3]);
					updateChoice();
				}
				break;
			case "PossibleAnswers":
				updatePossibleAnswers($(_elementProperties.selectedelement), step[4], true);
				break;
			case "Shortnames":
				var ids = step[2];
				var newvalues = step[4];
				var label = step[5];
				
				for (var i = 0; i < ids.length; i++)
				{
					updateShortnameInner(label, element, i, newvalues[i]);
				}
				break;
			case "ADDANSWER":
				element.possibleAnswers.push(step[2]);
				updateNavigation($(_elementProperties.selectedelement), $(_elementProperties.selectedelement).attr("id"));
				break;
			case "REMOVEANSWER":
				element.possibleAnswers.pop();
				updateNavigation($(_elementProperties.selectedelement), $(_elementProperties.selectedelement).attr("id"));
				break;
			case "ADDCOLUMN":
				element.answers.push(step[2]);
				addElementHandler($(_elementProperties.selectedelement));
				updateNavigation($(_elementProperties.selectedelement), $(_elementProperties.selectedelement).attr("id"));
				break;
			case "REMOVECOLUMN":
				element.answers.pop();
				updateNavigation($(_elementProperties.selectedelement), $(_elementProperties.selectedelement).attr("id"));
				break;
			case "ADDROW":
				element.questions.push(step[2]);
				addElementHandler($(_elementProperties.selectedelement));
				updateNavigation($(_elementProperties.selectedelement), $(_elementProperties.selectedelement).attr("id"));
				break;
			case "REMOVEROW":
				element.questions.pop();
				updateNavigation($(_elementProperties.selectedelement), $(_elementProperties.selectedelement).attr("id"));
				break;
			case "Identifier":
				updateIdentifier(element, id, step[4], true);	
				break;
			case "ReadOnly":
				element.readonly(step[4]);
				break;
			case "Attribute":
				element.isAttribute(step[4]);
				break;
			case "Name":
				element.attributeName(step[4]);
				break;
			case "Unique":
				element.isUnique(step[4]);
				break;
			case "Comparable":
				element.isComparable(step[4]);
				break;
			case "Password":
				element.isPassword(step[4]);
				break;
			case "Interdependency":
				element.isInterdependent(step[4]);
				break;
			case "Visibility":
				var oldtriggerids = step[4].split(";");
				var newtriggerids = step[3].split(";");
				var selectedquestionids = step[5].split(";");
				for (var i = 0; i < newtriggerids.length; i++)
				{
					var triggerid = newtriggerids[i];
					for (var j = 0; j < selectedquestionids.length; j++)
					{
						var selectedquestionid = selectedquestionids[j];
						if (triggerid.length > 0 && selectedquestionid.length > 0)
						{
							removeVisibility(triggerid, selectedquestionid);
						}					
					}				
				}
				for (var i = 0; i < oldtriggerids.length; i++)
				{
					var triggerid = oldtriggerids[i];
					for (var j = 0; j < selectedquestionids.length; j++)
					{
						var selectedquestionid = selectedquestionids[j];
						if (triggerid.length > 0 && selectedquestionid.length > 0)
						{
							addVisibility(triggerid, selectedquestionid);
						}
					}				
				}
				updateDependenciesView();
				break;
			case "NumberOfChoices":
				if (step[5] == "min")
				{
					element.minChoices(step[4]);
				} else {
					element.maxChoices(step[4]);
				}
				break;
			case "DecimalPlaces":
				$(_elementProperties.selectedelement).find("input[name^='decimalplaces']").first().val(step[4]);
				break;
			case "NumberOfAnsweredRows":
				if (step[5] == "min")
				{
					element.minRows(step[4]);
				} else if (step[5] == "max")
				{
					element.maxRows(step[4]);
				}
				break;
			case "Size":
				if (element.type == "Image")
				{
					updateImageSize(element, step[4], null, true);
				} else {
					updateMatrixSize(element, step[4], true);
				}
				break;
			case "Align":
				element.align(step[4]);
				break;
			case "DescriptiveText":
				element.originalTitle(step[4]);
				break;
			case "LongDescription":
				element.longdesc(step[4]);
				break;
			case "LongDescriptionGallery":
				var galleryid =  step[1];
				var gallery = _elements[galleryid];
				var uid = step[2];
				
				_elementProperties.selectedelement = $("td[data-uid='" + step[2] + "']");
								
				for (var i = 0; i < gallery.files().length; i++)
				{
					var file = gallery.files()[i];
					if (file.uid() == uid)
					{
						file.longdesc(step[4]);
						break;
					}
				}
				break;
			case "RegularExpression":
				element.regex(step[4]);
				break;
			case "ImageSelectable":
				element.selection(step[4]);
				updateGallery();
				checkGalleryProperties(true);
				break;
			case "MaxSelections":
				element.limit(parseInt(step[4]));
				updateGallery();
				break;
			case "AutoNumbering":
				element.numbering(step[3]);
				break;
			case "LabelText":
				element.confirmationlabel(step[4]);
				$(_elementProperties.selectedelement).find("a.confirmationlabel").html(step[4]);
				break;
			case "Title":
				var galleryid =  step[1];
				var gallery = _elements[galleryid];				
				var uid = step[2];
				
				_elementProperties.selectedelement = $("td[data-uid='" + step[2] + "']");
				
				for (var i = 0; i < gallery.files().length; i++)
				{
					var file = gallery.files()[i];
					if (file.uid() == uid)
					{
						file.name(step[4]);
						break;
					}
				}
				break;
			case "points":
				element.points(step[4]);
				break;
			case "scoring":
				element.scoring(step[4]);
				break;
			case "type":
				var scoring = element.getScoringItem(step[5]);
				scoring.type(step[4]);
				break;
			case "ruleValue":
				var scoring = element.getScoringItem(step[5]);
				scoring.value(step[4]);
				break;
			case "ruleValue2":
				var scoring = element.getScoringItem(step[5]);
				scoring.value2(step[4]);
				break;
			case "addExpectedAnswer":
				element.scoringItems.push(_actions.deletedElements.pop());		
				break;
			case "removeExpectedAnswer":
				_actions.deletedElements.push(element.scoringItems.pop());
				break;
			case "correct":
				if (element.type == "SingleChoiceQuestion" || element.type == "MultipleChoiceQuestion")
				{
					var answer = element.getChild(step[2]);
					answer.scoring.correct(step[4]);
				} else  {
					var scoring = element.getScoringItem(step[2]);
					scoring.correct(step[4]);
				}
				break;
			case "Feedback":
				if (element.type == "SingleChoiceQuestion" || element.type == "MultipleChoiceQuestion")
				{
					var answer = element.getChild(step[2]);
					answer.scoring.feedback(step[4]);
				} else  {
					var scoring = element.getScoringItem(step[2]);
					scoring.feedback(step[4]);
				}
				break;
			case "FileMoved":
				var button = $("tr[data-uid='" + step[2] + "']").find("a").first();
				moveGalleryFile(step[2], button, step[3], true);
				break;
			case "Color":
				element.color(step[4]);
				break;
			case "Height":
				element.height(step[4]);
				break;
		}
		
		var advancedopen = $(".advancedtogglebutton").find(".glyphicon-minus-sign").length > 0;
		var element = _elementProperties.selectedelement;
		_elementProperties.deselectAll();
		if (element != null) _elementProperties.showProperties($(element), null, false);
		if (advancedopen) toggleAdvancedProperties($(".advancedtogglebutton").find(".glyphicon").first());
		
		_actions.UndoEnabled(true);
		this.undostack.push(step);
		
		if (this.redostack.length == 0)
		{
			_actions.RedoEnabled(false);
		}
		
	}
}

var _undoProcessor = new UndoProcessor();