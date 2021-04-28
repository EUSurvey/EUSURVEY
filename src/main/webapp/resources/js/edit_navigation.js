var NavigationItem = function(type, css, id, title) {
	this.items = ko.observableArray([]);
	this.type = type;
	this.css = css;
	this.id = id;
	this.title = title;
	
	this.addItem = function (item) {
        this.items.push(item);
    };
    
    this.hasChildren = function()
    {
    	if (this.type == "section" || this.type== "matrix" || this.type== "table" || this.type == "choicequestion" || this.type == "ratingquestion" || this.type == "rankingquestion")
    	{
    		return true;
    	}
    	return false;
    }
}

var NavigationModel = function () {
	this.items = ko.observableArray([]);
	
	this.addItem = function (item) {
        this.items.push(item);
    };
    
    this.clearItems = function() {
    	this.items.removeAll();
    };
    
    this.removeItem = function(id) {
    	this.items.remove(function(item) {
            return item.id == id;
        });
    }
    
    this.getNavigationItem = function(e, isanswer, parentid)
    {
    	var id = $(e).attr("id");
    	var ni = new NavigationItem("question","",id,"");
    	
    	if ($(e).hasClass("sectionitem"))
    	{
    		ni.type = "section";
    		ni.css = "navigationitemsection";
    		ni.title = getLimitedText(adaptNumbering($(e).find(".sectiontitle").first()));
    	} else if (isanswer) {
    		ni.type = "navanswer";
    		ni.css = "navanswer";
    		ni.id = "navanswer" + $(e).attr("data-id");
    		ni.title = getLimitedText($(e).text());
    	} else  {
    		if ($(e).hasClass("matrix-question"))
    		{
    			ni.title = getLimitedText(adaptNumbering($(e).find(".matrixheadertitle").first()));
    		} else if ($(e).hasClass("matrix-header"))
    		{
    			ni.title = getLimitedText($(e).find("textarea[name^=text]").first().text());
    		} else if ($(e).hasClass("imageitem") || $(e).hasClass("textitem") || $(e).hasClass("ruleritem"))
    		{
    			ni.title = getLimitedText($(e).find("textarea[name^=text]").first().text());
    		} else {
    			ni.title = getLimitedText(adaptNumbering($(e).find(".questiontitle").first()));
    		}
    		if ($(e).hasClass("singlechoiceitem") || $(e).hasClass("multiplechoiceitem"))
    		{
    			ni.type = "choicequestion";
    			ni.css = "navigationitemchoice navquestion";
    		} else if ($(e).hasClass("matrixitem")) {
    			ni.type = "matrix";
    			ni.css = "navigationitemmatrix navquestion";
    		} else if ($(e).hasClass("matrix-question")) {
    			ni.id = $(e).attr("data-id");
    			ni.type = "matrix-question";
    			ni.css = "navigationitemmatrixquestion navquestion";
    		} else if ($(e).hasClass("matrix-header")) {
    			ni.type = "matrix-answer";
    			ni.cellid = parentid + "|" + id;
    			ni.css = "navanswer navmatrixanswer";
    		} else if ($(e).hasClass("mytableitem")) {
    			ni.type = "table";
    			ni.css = "navigationitemtable navquestion";
    		} else if ($(e).hasClass("table-header")) {
    			ni.type = "table-answer";
    			ni.cellid = parentid + "|" + id;
    			ni.css = "navanswer navtableanswer";
    			
    			if ($(e).index() == 0)
    			{
    				ni.css += " navigationitemtablequestion";
    			}
    			
    			ni.title = getLimitedText($(e).find("textarea").first().text());
    		} else if ($(e).hasClass("ratingitem")) {
    			ni.type = "ratingquestion";
    			ni.css = "navigationitemrating navquestion";
    		} else if ($(e).hasClass("rankingitem")) {
    			ni.type = "rankingquestion";
    			ni.css = "navigationitemranking navquestion";
    		} else {
    			ni.css += " navquestion";
    		}
    		
    	};
    	
    	if ($(e).hasClass("selectedquestion"))
    	{
    		ni.css += " selectedquestion";
    	}
    	
    	return ni;
    }
    
    this.createNavigationItem = function(element)
    {
    	var model = this;
    	var ni = model.getNavigationItem(element, false);
		
		if ($(element).hasClass("singlechoiceitem") || $(element).hasClass("multiplechoiceitem"))
		{	
			$(element).find("textarea[name^=answer]").each(function(){
				ni.addItem(model.getNavigationItem(this, true));
			});
		} else if ($(element).hasClass("matrixitem"))
		{
			var matrix = element;
			$(matrix).find(".matrix-question").each(function(){
				var parentid = $(this).attr("data-id");
				var niq = model.getNavigationItem(this, false);
				ni.addItem(niq);
				$(matrix).find("tr").first().find(".matrix-header").each(function(){
					niq.addItem(model.getNavigationItem(this, false, parentid));
				});
			});
		} else if ($(element).hasClass("mytableitem"))
		{
			var table = element;
			$(table).find("tr").each(function(index){
				if (index > 0)
				{
					var parentid = $(this).find("td").first().attr("data-id");
					var niq = model.getNavigationItem($(this).find("td").first(), false);
					ni.addItem(niq);
					$(table).find("tr").first().find(".table-header").each(function(){
						niq.addItem(model.getNavigationItem(this, false, parentid));
					});
				}
			});
		} else if ($(element).hasClass("ratingitem"))
		{
			$(element).find("textarea[name^=question]").each(function(){
				ni.addItem(model.getNavigationItem(this, true));
			});
		} else if ($(element).hasClass("rankingitem"))
		{
			$(element).find("textarea[name^=rankingitemtitle]").each(function(){
				ni.addItem(model.getNavigationItem(this, true));
			});
		}
		
		return ni;
    }
    
    this.createNavigation = function(first)
    {    	
    	//save closed items
    	var closedids = [];
    	$("#navigation").find(".glyphicon-chevron-right").each(function(){
    		closedids[closedids.length] = $(this).parent().attr("data-id");
    	});	
    	
    	this.clearItems();
    	var model = this;
    	
    	$("#navigationwaitimage").show();
    	
    	$("#content").find(".survey-element").each(function(){    	
    		if ($(this).is(":visible"))
    		{
    			var ni = model.createNavigationItem(this);    			
    			model.addItem(ni);
    		} 
    	});
    	
    	if (first)
    	ko.applyBindings(this, $("#navigation")[0]);
    	
    	for (var i = 0; i < closedids.length; i++)
    	{
    		$(".navigationitem[data-id=" + closedids[i] + "]").each(function(){
    			showHideElements($(this).find(".glyphicon").first());
    		});
    	}
    	
    	$("#navigationwaitimage").hide();
    }
    
    this.updateNavigation = function(element, id)
    {
    	var model = this;
    	
    	var oldItem = ko.utils.arrayFirst(model.items(), function (item) {
    	    return item.id == id;
    	});
    	
    	var hidden = $(".navigationitem[data-id='" + id + "']").find(".glyphicon-chevron-right").length > 0;
    	
    	model.items.replace(oldItem, model.createNavigationItem(element));
    	
    	if (hidden)
    	{
    		showHideElements($(".navigationitem[data-id='" + id + "']").find(".glyphicon-chevron-down").first());
    	}
    }
    
    this.addToNavigation = function(element, position)
    {
    	var model = this;    	
    	model.items.splice(position, 0, model.createNavigationItem(element));
    }
    
    this.moveItemInNavigation = function(oldposition, newposition)
    {
    	var model = this;
    	var oldItem = model.items()[oldposition];
    	
    	var visible = $(".navigationitem[data-id='" + oldItem.id + "']").is(":visible");
    	    	
    	model.items.remove(oldItem);    	
    	model.items.splice(newposition, 0, oldItem);
    	
    	if (!visible)
    	{
    		$(".navigationitem[data-id='" + oldItem.id + "']").hide();
    	}
    }
    
    this.removeFromNavigation = function(id)
    {
    	var model = this;
    	var oldItem = ko.utils.arrayFirst(model.items(), function (item) {
    	    return item.id == id;
    	});
    	
    	if (oldItem == null)
    	{
    		for (var i = 0; i < model.items().length; i++)
    		{
    			if (model.items()[i].items().length > 0)
    			{
    				for (var j = 0; j < model.items()[i].items().length; j++)
    	    		{
    					if (model.items()[i].items()[j].id == id)
    					{
    						oldItem = model.items()[i].items()[j];
    						model.items()[i].items.remove(oldItem);  
    						return;
    					}
    	    		}
    			}
    		}
    	}
    	
    	model.items.remove(oldItem);   
    }
};

var _navigationModel = new NavigationModel();

function createNavigation(first)
{	
	if (!_actions.NavigationPaneEnabled()) return;
	_navigationModel.createNavigation(first);
}

function updateNavigation(element, id)
{
	if (!_actions.NavigationPaneEnabled()) return;
	_navigationModel.updateNavigation(element, id);
}

function addToNavigation(element, position)
{
	if (!_actions.NavigationPaneEnabled()) return;
	_navigationModel.addToNavigation(element, position);
}

function moveItemInNavigation(oldposition, newposition)
{
	if (!_actions.NavigationPaneEnabled()) return;
	_navigationModel.moveItemInNavigation(oldposition, newposition);
}

function removeFromNavigation(id)
{
	if (!_actions.NavigationPaneEnabled()) return;
	_navigationModel.removeFromNavigation(id);
}

function getLimitedText(e)
{
	if (e == null) return null;
	var s = e.replace(/<br\s*[\/]?>/gi, " ");
	s = strip_tags(s);
	if (s.length < 25) return s;
	
	return s.substring(0,23) + "...";
}

function getShortnedText(text)
{
	var s = text.replace(/<br\s*[\/]?>/gi, " ");
	s = strip_tags(s);
	if (s.length > 105)
	{
		s = s.substring(0, 100) + "...";
	}
	return s;
}

function getCombinedAnswerText(useparagraphs)
{
	var id = $(_elementProperties.selectedelement).attr("data-id");
	
	if ($(_elementProperties.selectedelement).hasClass("answertext"))
	{
		id = $(_elementProperties.selectedelement).closest(".survey-element").attr("data-id");
	}	
	
	var element = _elements[id];
	
	var result = "";
	var arr = [];
	
	for (var i = 0; i < element.possibleAnswers().length; i++)
	{
		if (useparagraphs)
		{
			var title = element.possibleAnswers()[i].title();
			if (title.indexOf("<p") == 0)
			{
				result += title;
			} else {
				result += "<p>" + title + "</p> ";
			}
		} else {
			arr[arr.length] = element.possibleAnswers()[i].title();
		}
	}
	
	if (useparagraphs)
	{
		return result;
	} else {
		return arr;
	}
}

function getCombinedRankingText(useparagraphs)
{
	var id = $(_elementProperties.selectedelement).attr("data-id");
	
	if ($(_elementProperties.selectedelement).hasClass("rankingitemtext"))
	{
		id = $(_elementProperties.selectedelement).closest(".survey-element").attr("data-id");
	}

	var element = _elements[id];

	var result = "";
	var arr = [];

	$.each(element.rankingItems(), function(index, thatrankingitem) {
		var title = thatrankingitem.title();
		if (useparagraphs) {
			if (title.startsWith("<p")) {
				result += title;
			} else {
				result += "<p>" + title + "</p> ";
			}
		} else {
			arr.push(title);
		}
	});

	if (useparagraphs) {
		return result;
	} else {
		return arr;
	}
}

function goTo(id, event)
{
	var elem = $("#content").find("#" + id).last();
	if (id.indexOf("navanswer") == 0)
	{
		id=id.replace("navanswer","");
		elem = $("#content").find(".answertext[data-id=" + id + "]").first();
		
		if ($(elem).length == 0)
		{
			elem = $("#content").find(".rankingitemtext[data-id=" + id + "]").first();
		}
		
		if ($(elem).length == 0)
		{
			elem = $("#content").find(".ratingquestion[data-id=" + id + "]").first();
		}
	}	
	
	$('html, body').animate({
        scrollTop: elem.offset().top - 200
    }, 2000);
	
	_elementProperties.showProperties(elem, event, false);
}

function showHideElements(span)
{
	var ni = $(span).parent();
	if ($(ni).hasClass("navigationitemsection"))
	{
		if ($(span).hasClass("glyphicon-chevron-down"))
		{
			$(ni).nextAll().each(function(){
				if ($(this).hasClass("navigationitemsection")) return false;
				if ($(this).hasClass("navigationitem")) $(this).hide(400);
			});
			$(span).removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-right");
		} else {
			$(ni).nextAll().each(function(){
				if ($(this).hasClass("navigationitemsection")) return false;
				if ($(this).hasClass("navigationitem")) $(this).show(400);
			});
			$(span).removeClass("glyphicon-chevron-right").addClass("glyphicon-chevron-down");
		}
	} else if ($(ni).hasClass("navigationitemchoice"))
	{
		if ($(span).hasClass("glyphicon-chevron-down"))
		{
			$(ni).find(".navanswer").hide(400);
			$(span).removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-right");
		} else {
			$(ni).find(".navanswer").show(400);
			$(span).removeClass("glyphicon-chevron-right").addClass("glyphicon-chevron-down");
		}
	} else if ($(ni).hasClass("navigationitemmatrix"))
	{
		if ($(span).hasClass("glyphicon-chevron-down"))
		{
			$(ni).find(".navigationitemmatrixquestion").hide(400);
			$(span).removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-right");
		} else {
			$(ni).find(".navigationitemmatrixquestion").show(400);
			$(span).removeClass("glyphicon-chevron-right").addClass("glyphicon-chevron-down");
		}
	} else if ($(ni).hasClass("navigationitemmatrixquestion"))
	{
		if ($(span).hasClass("glyphicon-chevron-down"))
		{
			$(ni).find(".navanswer").hide(400);
			$(span).removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-right");
		} else {
			$(ni).find(".navanswer").show(400);
			$(span).removeClass("glyphicon-chevron-right").addClass("glyphicon-chevron-down");
		}
	} else if ($(ni).hasClass("navigationitemtable"))
	{
		if ($(span).hasClass("glyphicon-chevron-down"))
		{
			$(ni).find(".navigationitem").hide(400);
			$(span).removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-right");
		} else {
			$(ni).find(".navigationitem").show(400);
			$(span).removeClass("glyphicon-chevron-right").addClass("glyphicon-chevron-down");
		}
	}  else if ($(ni).hasClass("accordion-toggle"))
	{
		var target = $(ni).parent().next();
		if ($(span).hasClass("glyphicon-chevron-down"))
		{
			$(target).collapse('hide');
			$(span).removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-right");
		} else {
			$(target).collapse('show');
			$(span).removeClass("glyphicon-chevron-right").addClass("glyphicon-chevron-down");
		}
	} else if ($(ni).hasClass("navigationitemrating"))
	{
		if ($(span).hasClass("glyphicon-chevron-down"))
		{
			$(ni).find(".navigationitem").hide(400);
			$(span).removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-right");
		} else {
			$(ni).find(".navigationitem").show(400);
			$(span).removeClass("glyphicon-chevron-right").addClass("glyphicon-chevron-down");
		}
	} else if ($(ni).hasClass("navigationitemranking"))
	{
		if ($(span).hasClass("glyphicon-chevron-down"))
		{
			$(ni).find(".navigationitem").hide(400);
			$(span).removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-right");
		} else {
			$(ni).find(".navigationitem").show(400);
			$(span).removeClass("glyphicon-chevron-right").addClass("glyphicon-chevron-down");
		}
	} 
}

function collapseAll(close, button)
{
	var parent = $("#navigationcontent");
	if ($(button).closest(".toolbox").length > 0)
	{
		parent = $(".toolbox").first();
	}
	if ($(button).closest(".properties").length > 0)
	{
		parent = $(".properties").first();		
	} 
	if (close)
	{
		$(parent).find(".glyphicon-chevron-down").each(function(){
			$(this).click();
		});
	} else {
		$(parent).find(".glyphicon-chevron-right").each(function(){
			$(this).click();
		});
	}
}
