var Actions = function() {
	//switch buttons
	this.DependenciesEnabled = ko.observable(true);
	this.MultiSelectionEnabled = ko.observable(false);
	this.NavigationPaneEnabled = ko.observable(true);
	this.ToolboxPaneEnabled = ko.observable(true);
	this.PropertiesPaneEnabled = ko.observable(true);
	
	//buttons
	this.UndoEnabled= ko.observable(false);
	this.RedoEnabled= ko.observable(false);
	this.CopyEnabled = ko.observable(false);
	this.PasteEnabled = ko.observable(false);    
    this.ElementSelected = ko.observable(false);
	this.ChildSelected = ko.observable(false);
	this.CutEnabled = ko.observable(false);
	this.MoveDownEnabled = ko.observable(false);
	this.MoveUpEnabled = ko.observable(false);
	this.DeleteEnabled = ko.observable(false);
	this.SaveEnabled = ko.observable(false);
	this.UnsavedChangesConfirmed = ko.observable(false);
	this.BackupEnabled = ko.observable(true);
	
	this.AllElementsLoaded = ko.observable(false);
	
	this.copiedElements = [];
    this.cutElements = [];
    this.deletedElements = [];
    this.deletedModels = [];
    
    this.toggleBackupEnabled= function () {
        this.BackupEnabled(!this.BackupEnabled());
	};
	
	this.toggleDependencies = function () {
        this.DependenciesEnabled(!this.DependenciesEnabled());
    	updateDependenciesView();
	};
	
	this.toggleMultiSelection = function () {
        this.MultiSelectionEnabled(!this.MultiSelectionEnabled());
	};
	
	this.toggleNavigationPane = function () {
        this.NavigationPaneEnabled(!this.NavigationPaneEnabled());
        if ($(".navigation").first().is(":visible"))
    	{
    		$(".navigation").hide();
    		$(".toolbox").css("left","0");
    		$("#shownavigationbutton").removeClass("selected");
    	} else {
    		$(".navigation").show();
    		$(".toolbox").css("left","180px");
    		$("#shownavigationbutton").addClass("selected");
    		createNavigation(false);
    	}
    	setContentMargin();
    };
    
    this.toggleToolboxPane = function () {
        this.ToolboxPaneEnabled(!this.ToolboxPaneEnabled());
        if ($(".toolbox").first().is(":visible"))
    	{
    		$(".toolbox").hide();
    		$("#showtoolboxbutton").removeClass("selected");
    	} else {
    		$(".toolbox").show();
    		$("#showtoolboxbutton").addClass("selected");
    	}
    	setContentMargin();
    };
    
    this.togglePropertiesPane = function () {
        this.PropertiesPaneEnabled(!this.PropertiesPaneEnabled());
        if ($(".properties").first().is(":visible"))
    	{
    		$(".properties").hide();
    		$("#showpropertiesbutton").removeClass("selected");
    	} else {
    		$(".properties").show();
    		$("#showpropertiesbutton").addClass("selected");
    	}
    };
        
    this.copySelectedElement = function () {
    	var model = this;
		if ($("#copyElementButton").hasClass("disabled")) return;
		
		this.copiedElements = [];
		$("#content").find(".selectedquestion").each(function(){
			var oldid = $(this).attr("id");
			var originalmodel = _elements[oldid];
			var copiedmodel = originalmodel.copy();
			model.copiedElements[model.copiedElements.length] = copiedmodel;
		});
		
		if (this.copiedElements.length == 1 && this.copiedElements[0].type == "Section")
		{
			$("#askcopysectiondialog").modal("show");
			return;
		}
		
		$("#copiedtoolboxitem").show();
		$("#cancelcopytoolboxitem").show();
		
		this.CopyEnabled(false);
		this.CutEnabled(false);
		this.PasteEnabled(true);
		
		if (!$("#showtoolboxbutton").hasClass("selected"))
		{
			this.toggleToolboxPane();
		}    	
    };
    
    this.copySection = function(withchildren)
    {
    	if (withchildren)
    	{
    		var model = this;
    		var originalId = this.copiedElements[0].originalId;
    		var section =  $(".sectionitem[data-id='" + originalId + "']").first();
    		var copiedsection = this.copiedElements[0];
    		var level = parseInt($(section).find(".sectiontitle").first().attr("data-level"));
    		this.copiedElements = [];
    		this.copiedElements[0] = copiedsection;		
    		$(section).nextAll().each(function(){
    			if ($(this).hasClass("sectionitem"))
    			{
    				var level2 = parseInt($(this).find(".sectiontitle").first().attr("data-level"));
    				if (level2 <= level)
    				{
    					return false;
    				}
    			}
    			
    			var oldid = $(this).attr("id");
    			var newid = getNewId();
    			var originalmodel = _elements[oldid];
    			var copiedmodel = originalmodel.copy();
    			copiedmodel.id(newid);
    			model.copiedElements[model.copiedElements.length] = copiedmodel;
    		});
    	}
    	
    	$("#copiedtoolboxitem").show();
		$("#cancelcopytoolboxitem").show();
		
		this.CopyEnabled(false);
		this.CutEnabled(false);
		this.PasteEnabled(true);
		
		if (!$("#showtoolboxbutton").hasClass("selected"))
		{
			this.toggleToolboxPane();
		}    
    	
    	$("#askcopysectiondialog").modal("hide");
    }
    
    this.copyElement = function(item)
    {
    	var ids = "";
    	var copiedItems = [];
    	var dict = [];
    	$(this.copiedElements).each(function(){
    		var copiedmodel = this;
    		dict[copiedmodel.originalId] = copiedmodel.id();
    		
    		if (copiedmodel.type.indexOf("ChoiceQuestion") > 0)
		    {
				for (var i = 0; i < copiedmodel.possibleAnswers().length; i++)
		    	{
					dict[copiedmodel.possibleAnswers()[i].originalId] = copiedmodel.possibleAnswers()[i].id();
		    	}
		    } else if (this.type == "Matrix") {
		    	for (var i = 0; i < this.answers().length; i++)
		    	{
		      		dict[this.answers()[i].originalId] = this.answers()[i].id();
		    	}
		    	for (var i = 0; i < this.questionsOrdered().length; i++)
		    	{
		    		dict[this.questionsOrdered()[i].originalId] = this.questionsOrdered()[i].id();
		    	}
		    }
    		
    		var newitem = document.createElement("li");
    		$(newitem).addClass("emptyelement survey-element").attr("id", copiedmodel.id()).attr("data-id", copiedmodel.id())
    		item.before($(newitem));
    		if ($("li[id='" + copiedmodel.originalId + "']").attr("data-triggers"))
    		{
	    		var original_data_triggers = $("li[id='" + copiedmodel.originalId + "']").attr("data-triggers");
	    		var oldtriggers = original_data_triggers.split(/[;|]+/);
	    		var changed = false;
	    		for (var i = 0; i < oldtriggers.length; i++)
	    		{
	    			var id = oldtriggers[i];
	    			if (id.length > 0 && dict.hasOwnProperty(id))
	    			{
	    				original_data_triggers = original_data_triggers.replace(id, dict[id]);
	    				changed = true;
	    			}
	    		}
	    		$(newitem).attr("data-triggers", original_data_triggers);
    		}
    		
    		var copy = addElement(copiedmodel, true, false);
    		
    		$("li[id='" + copiedmodel.originalId + "']").find("tr").each(function(index){
    			if ($(this).attr("data-triggers"))
    			{
	    			var original_data_triggers = $(this).attr("data-triggers");
		    		var oldtriggers = original_data_triggers.split(/[;|]+/);
		    		var changed = false;
		    		for (var i = 0; i < oldtriggers.length; i++)
		    		{
		    			var id = oldtriggers[i];
		    			if (id.length > 0 && dict.hasOwnProperty(id))
		    			{
		    				original_data_triggers = original_data_triggers.replace(id, dict[id]);
		    				changed = true;
		    			}
		    		}
		    		$($(copy).find("tr")[index]).attr("data-triggers", original_data_triggers);
    			}
    		});    		
    		
			_elements[copiedmodel.id()] = copiedmodel;
			copiedItems[copiedItems.length] = copy;
    		ids = ids + copiedmodel.id() + ";";
    		addElementHandler($(newitem));
    		
			addToNavigation(newitem, $(newitem).index());
    	});	    	
    	
    	$(this.copiedElements).each(function(){
    		this.replaceTriggers(dict);
    	});
    	
    	$(copiedItems).each(function(){
    		if (!checkDependenciesAfterMove($(this)))
    		{
    			$(this).find("input[name^=dependencies]").val("");
    			$(this).removeAttr("data-triggers");
    			$(this).find(".trigger").removeClass("trigger");
    		}
    	});
    	
    	item.remove();
    	
    	_elementProperties.deselectAll();
    	
    	_undoProcessor.addUndoStep(["COPYPASTE", ids]);
    	
		updateDependenciesView();
		deactivateLinks();
    }
    
    this.cutSelectedElement = function()
    {
    	if ($("#cutElementButton").hasClass("disabled")) return;
    	
    	this.cutElements = $("#content").find(".selectedquestion");
    	
    	if ($(this.cutElements).length == 1 && $($(this.cutElements)[0]).hasClass("sectionitem"))
		{
			$("#askcutsectiondialog").modal("show");
			return;
		}			
    	
    	$(this.cutElements).hide();
    	
    	$(this.cutElements).each(function(){
    		removeFromNavigation($(this).attr("id"));
    	});
    	
    	$("#cuttoolboxitem").show();
    	$("#cancelcuttoolboxitem").show();
    	
    	_actions.CopyEnabled(false);
    	_actions.CutEnabled(false);
    	this.PasteEnabled(true);
    	
    	if (!$("#showtoolboxbutton").hasClass("selected"))
    	{
    		this.toggleToolboxPane();
    	}
    	
		_actions.ElementSelected(false);				
    }
    
    this.cutSection = function(withchildren)
    {
    	if (withchildren)
    	{
    		var model = this;
    		var section = $($(this.cutElements)[0]);
    		var level = parseInt($(section).find(".sectiontitle").first().attr("data-level"));
    		this.cutElements = [];
    		this.cutElements[0] = section[0];		
    		$(section).nextAll().each(function(){
    			if ($(this).hasClass("sectionitem"))
    			{
    				var level2 = parseInt($(this).find(".sectiontitle").first().attr("data-level"));
    				if (level2 <= level)
    				{
    					return false;
    				}
    			}
    			model.cutElements[model.cutElements.length] = this;
    		});
    	}
    	
    	$(this.cutElements).hide();
    	
    	$(this.cutElements).each(function(){
    		removeFromNavigation($(this).attr("id"));
    	});
    
    	$("#cuttoolboxitem").show();
    	$("#cancelcuttoolboxitem").show();
    	
    	_actions.CopyEnabled(false);
    	_actions.CutEnabled(false);
    	this.PasteEnabled(true);
    	
    	if (!$("#showtoolboxbutton").hasClass("selected"))
    	{
    		this.toggleToolboxPane();
    	}
    	
    	$("#askcutsectiondialog").modal("hide");
    	
    	_actions.ElementSelected(false);
    }    
    
    this.pasteElementAfter = function()
    {    	
    	if ($("#pasteButton").hasClass("disabled")) return;
    	
    	if ($("#copiedtoolboxitem").is(":visible"))
    	{
    		var item = $("#copiedtoolboxitem").clone();
    		
    		if ($(_elementProperties.selectedelement).length > 0)
        	{
        		$(_elementProperties.selectedelement).after(item);	
        	} else {
        		$("#content").append(item);
        	}
    		this.copyElement($(item));	
    	} else {
    		var item = $("#cuttoolboxitem").clone();
    		if ($(_elementProperties.selectedelement).length > 0)
	    	{
	    		$(_elementProperties.selectedelement).after(item);	
	    	} else {
	    		$("#content").append(item);
	    	}
    		this.pasteElement($(item));
    	}
    	
    	updateTitles();
    	deactivateLinks();
    }

    this.pasteElement = function(item)
    {
    	var ids = "";
    	var oldpos = "";
    	var newpos = "";
    	$(this.cutElements).each(function(){
    		ids = ids + $(this).attr("id") + ";";
    		oldpos = oldpos + $(this).index() + ";";
    		newpos = newpos + item.index() + ";";
    		item.before($(this).show());
    		addToNavigation($(this), $(this).index());
    	});	
    	
    	item.remove();
    	var skip = false;
    	
    	$(this.cutElements).each(function(){
    		if (!checkDependenciesAfterMove($(this)))
    		{
    			$('#invalid-dependency-dialog').modal('show');
    			skip = true;
    			var ids2 = ids.split(";");
    			var oldpos2 = oldpos.split(";");
    			for (var i = ids2.length-1; i >= 0; i--)
				{
					var item = $("#content").find("li[id='" + ids2[i] + "']").first();
					if (item.length > 0)
					{
						var position = oldpos2[i];
						if (position == 0)
						{
							$("#content").prepend(item);
						} else {
							$(item).insertAfter($($("#content").find("li")[position-1]));
						}						
					}
				}
    			return;
    		}
    	});	
    	
    	if (!skip)
    	{
	    	_elementProperties.deselectAll();
	    	$("#cuttoolboxitem").hide();
	    	$("#cancelcuttoolboxitem").hide();
	    	this.PasteEnabled(false);
	    	
	    	updateDependenciesView();
	    	_undoProcessor.addUndoStep(["CUTPASTE", ids, oldpos, newpos]);
    	} else {
    		$(this.cutElements).each(function(){
    			$(this).hide();
    		});
    	}
    	
    	deactivateLinks();
    }
    
    this.cancelCut = function()
    {
    	$(_actions.cutElements).show();
    	
    	$(_actions.cutElements).each(function(){
    		addToNavigation($(this), $(this).index())
    	})
    	
    	$("#cuttoolboxitem").hide();
    	$("#cancelcuttoolboxitem").hide();
    	
    	this.CopyEnabled(true);
    	this.CutEnabled(true);
    	this.PasteEnabled(false);
    }

    this.cancelCopy = function()
    {
    	$("#copiedtoolboxitem").hide();
    	$("#cancelcopytoolboxitem").hide();
    	
    	this.CopyEnabled(true);
    	this.CutEnabled(true);
    	this.PasteEnabled(false);
    }
    
    this.moveElementDown = function()
    {
    	if (!this.MoveDownEnabled()) return;
    	$($("#content").find(".selectedquestion").get().reverse()).each(function(){  
    	
	    	var current =  $(this); // $(_elementProperties.selectedelement);
	    	var oldindex = $(this).index();
	
			current.next().after(current);
			
			if (checkDependenciesAfterMove(current))
			{
				_undoProcessor.addUndoStep(["MOVE", $(this).attr("id"), oldindex, $(this).index()]);
		    	moveItemInNavigation(oldindex, $(this).index());
		    	
		    	_actions.MoveUpEnabled(true);
		    	if ($(current).is(':last-child')) _actions.MoveDownEnabled(false);
			} else {
				$('#invalid-dependency-dialog').modal('show');
				current.prev().before(current);
			}
		
    	});
    }
    
    this.moveElementUp = function()
    {
    	if (!this.MoveUpEnabled()) return;
    	$("#content").find(".selectedquestion").each(function(){    	
    	
	    	var current = $(this); // $(_elementProperties.selectedelement);
	    	var oldindex = $(this).index();	    	
	    	
			current.prev().before(current);
			if (checkDependenciesAfterMove(current))
			{
				_undoProcessor.addUndoStep(["MOVE", $(this).attr("id"), oldindex, $(this).index()]);
				moveItemInNavigation(oldindex, $(this).index());
		    	
				if (current.index() == 0) _actions.MoveUpEnabled(false);
				_actions.MoveDownEnabled(true);
			} else {
				$('#invalid-dependency-dialog').modal('show');
				current.next().after(current);
			}
		
    	});
    }
    
    this.deleteElement = function()
    {
    	if (!this.DeleteEnabled() || !this.AllElementsLoaded()) return;
    	
    	var table = document.createElement("table");
    	$(table).addClass("deletetable");
    	
    	if ($("#content").find(".selectedquestion").not(".locked").length > 1)
    	{
    		$("#content").find(".selectedquestion").not(".locked").each(function(){
    			var tr = getDeleteElementRow($(this), true);
    			$(table).append(tr);			
    		});
    		
    		$("#confirm-delete-multiple-count").find("span").first().text($("#content").find(".selectedquestion").length);
    		
    		$("#confirm-delete-multiple-dialog-body").empty().append(table);							
    		$("#confirm-delete-multiple-dialog").modal("show");
    	} else {
    		var tr = getDeleteElementRow($(_elementProperties.selectedelement), false);				
    		$(table).append(tr);
    		
    		$("#confirm-delete-dialog-body").empty().append(table);					
    		$("#confirm-delete-dialog").modal("show");
    	}
    }

    this.deleteElement2 = function(noundo)
    {	
    	var model = this;
    	var idsandpositions = "";
    	var triggerids = [];
    	var ids = [];
    	var dependentElementsStrings = [];
    	var gallery = null;
    	var galleryimagestodelete = [];
    	
    	var selectedquestions = $("#content").find(".selectedquestion");
    	
    	$(selectedquestions).each(function(){
    		
    		if ($(this).hasClass("matrix-header") || $(this).hasClass("table-header"))
    		{
    			$(this).find(".trigger").each(function(){
    				triggerids[triggerids.length] = $(this).attr("data-cellid");
    			})    			
    			
    			var id = $(this).attr("id");
    			var parentid = $(this).closest(".survey-element").attr("id");
    			var parent = _elements[parentid];
    			var cellindex = $(this).index();
    			if (cellindex > 0)
    			{
    				model.deletedElements.push( $(this).clone());
    				model.deletedModels.push(parent.getChild(id));
    				parent.answers.remove( function (item) { return item.id() == id; } );   
    				
    				if ($(this).hasClass("matrix-header"))
    				{
    					var ai = cellindex - 1;
    					parent.columns(parent.columns()-1);
    					dependentElementsStrings[parent.id()] = parent.dependentElementsStringsCopy();
    					
    					//remove dependencies of this matrix answer
    					for (var i = (parent.questionsOrdered().length - 1) * (parent.answers().length + 1) + ai; i >= 0; i-=(parent.answers().length + 1))
    					{
    						parent.dependentElementsStrings.splice(i,1);
    					}    					
    					
    					//remove triggers
    					for (var i = 0; i < parent.questionsOrdered().length; i++)
    					{
    						var qid = parent.questionsOrdered()[i].id();
    						triggerids[triggerids.length] = qid + "|" + id;
    					}    					
    				}
    			} else {
    				//matrix question
    				cellindex = $(this).closest("tr").index();
    				model.deletedElements.push($(this).closest("tr").clone());
    				model.deletedModels.push(parent.getChild(id));
    				if ($(this).hasClass("matrix-header"))
    				{
    					//decrease originalIndex of following questions
    					var start = false;
    					for (var i = 0; i < parent.questions().length; i++)
    					{
    						if (parent.questions()[i].id() == id)
    						{
    							start = true;
    						} else {
    							if (start)
    							{
    								parent.questions()[i].originalIndex(parent.questions()[i].originalIndex()-1);
    							}
    						}
    					}
    					    					
    					parent.questions.remove( function (item) { return item.id() == id; } )
    					
    					var ai = cellindex - 1;
    					dependentElementsStrings[parent.id()] = parent.dependentElementsStringsCopy();
    					
    					//remove dependencies of this matrix question
    					for (var i = (parent.answers().length) * (ai+1) - 1 ; i >= ai * (parent.answers().length); i--)
    					{
    						parent.dependentElementsStrings.splice(i,1);
    					}  
    					
    					//remove triggers
    					for (var i = 0; i < parent.answers().length; i++)
    					{
    						var aid = parent.answers()[i].id();
    						triggerids[triggerids.length] = id + "|" + aid;
    					}
    					
    				} else {
    					parent.questions.remove( function (item) { return item.id() == id; } )
    				}
    			}			
    			idsandpositions = idsandpositions + $(this).attr("id") + "|" + cellindex + "@" + parentid + ";";
    			ids[ids.length] =  $(this).attr("id");
    			
    			removeFromNavigation($(this).attr("id"));
    		} else if ($(this).closest(".gallery-div").length > 0) {
    			var uid = $(this).attr("data-uid");
				var parentid = $(this).closest(".galleryitem").attr("id");
				gallery = _elements[parentid];
				element = gallery.getChild(uid);
				//we delay the real removing to prevent knockout from removing all rows immediately
				galleryimagestodelete[galleryimagestodelete.length] = uid;
				
				model.deletedElements.push($(this));
    			model.deletedModels.push(element);
    			var position = $(this).index();
    			idsandpositions = idsandpositions + uid + "|" + position + ";";
    			ids[ids.length] = uid;
    		} else {    			    		
    			if ($(this).hasClass("matrixitem"))
    			{
    				$(this).find(".trigger").each(function(){
        				triggerids[triggerids.length] = $(this).attr("data-cellid");
        			})
    			} else {    			
	    			$(this).find(".trigger").each(function(){
	    				triggerids[triggerids.length] = $(this).attr("id");
	    			})
    			}
    			
    			var id = $(this).attr("id");
    			var element = _elements[parentid];   			
    			var position = $(this).index();
    			model.deletedElements.push($(this));
    			model.deletedModels.push(element);
    			$(this).remove();
    			idsandpositions = idsandpositions + $(this).attr("id") + "|" + position + ";";
    			ids[ids.length] =  id;
    		}
    		
    		removeElementScore(this);
    		removeFromNavigation($(this).attr("id"));
    	});
    	
    	$("#content").find(".survey-element").each(function(){
    		if ($(this).attr("data-triggers"))
    		{
    			var t = $(this).attr("data-triggers");
    			for (var i = 0; i < triggerids.length; i++)
    			{
    				t = t.replace(triggerids[i] + ";", "");
    			}
    			$(this).attr("data-triggers",t);
    		}
    		
    		var id = $(this).attr("data-id");
    		if (_elements[id].type.indexOf("ChoiceQuestion") > 0)
    		{
    			for (var j = 0; j < ids.length; j++)
    			{
    				_elements[id].removeDependencies(ids[j]);
    			}
    		} else if (_elements[id].type == "Matrix")
    		{
    			for (var j = 0; j < ids.length; j++)
    			{
    				_elements[id].removeDependencies(ids[j]);
    			}
    		}
    	});
    	
    	if (gallery != null && galleryimagestodelete.length > 0)
    	{
    		for (var i = 0; i < galleryimagestodelete.length; i++)
    		{
    			gallery.files.remove( function (item) { return item.uid() == galleryimagestodelete[i]; } )
    		}
    	}
    	
    	if (!noundo)
        	_undoProcessor.addUndoStep(["DELETE", idsandpositions, dependentElementsStrings]);	
    	
    	_elementProperties.deselectAll();
    	$('#confirm-delete-dialog').modal('hide');
    	$('#confirm-delete-multiple-dialog').modal('hide');
    	
    	checkContent();
    	updateDependenciesView();
    }
    
    this.backup = function(){
    	if (!is_local_storage_enabled() || !this.BackupEnabled()) {
    		return;
    	}
    	
    	var survey = $(document.getElementById("survey.id")).val();
    	var name = "SurveyEditorBackup" + survey; 	
    	
    	var value = ko.toJSON(_elements);
    	
    	try {
			localStorage.setItem(name, value);
		} catch(e) {
		    if(e.name == "NS_ERROR_FILE_CORRUPTED") {
		    	showError("Sorry, it looks like your browser storage has been corrupted. Please clear your storage by going to Tools -> Clear Recent History -> Cookies and set time range to 'Everything'. This will remove the corrupted browser storage across all sites.");
		    }
		}	
    }
    
    this.deleteBackup = function() {
    	var survey = $(document.getElementById("survey.id")).val();
    	var name = "SurveyEditorBackup" + survey;
    	var value = localStorage.removeItem(name);
    }
    
    this.restore = function(){
    	if (!is_local_storage_enabled() || !this.BackupEnabled()) {
    		return;
    	}
    	
    	var survey = $(document.getElementById("survey.id")).val();
    	var name = "SurveyEditorBackup" + survey;
    	
    	var value = localStorage.getItem(name);
    	
    	_elements = JSON.parse(value);
    	
    	$("#content").find(".survey-element").each(function(){
    		ko.cleanNode($(this)[0]);
    	})
    	
    	$("#content").empty();
    	
    	for (var elementid in _elements) {
    	    if (_elements.hasOwnProperty(elementid)) {
    	    	var emptyelement = document.createElement("li");
    	    	$(emptyelement).addClass("emptyelement").attr("id", elementid).attr("data-id", elementid);
    	    	$("#content").append(emptyelement);
    	    	
    	    	var element = _elements[elementid];
    	    	element.isViewModel = false;
    	    	var model = getElementViewModel(element);
    	    	var item = addElement(element, true, false);
				addElementHandler(item);
    	    }
    	}
    	
    	_undoProcessor.clear();
    	_elementProperties.deselectAll();
    	_actions.SaveEnabled(true);
    	
    	checkContent();
    }
}

var _actions = new Actions();

$(function() {
	ko.applyBindings(_actions, $("#actions")[0]);
	
	if (localStorage != null)
	{
		if (localStorage.getItem("shownavigationbutton") == "false")
		{
			_actions.toggleNavigationPane();
		}
		if (localStorage.getItem("showtoolboxbutton") == "false")
		{
			_actions.toggleToolboxPane();
		}
		if (localStorage.getItem("showpropertiesbutton") == "false")
		{
			_actions.togglePropertiesPane();
		}
		if (localStorage.getItem("dependenciesButton") == "false")
		{
			_actions.toggleDependencies();
		}
		if (localStorage.getItem("backupButton") == "false")
		{
			_actions.toggleBackupEnabled();
		}
		if (localStorage.getItem("multiselectButton") == "true")
		{
			_actions.toggleMultiSelection();
		}
	}
    
});