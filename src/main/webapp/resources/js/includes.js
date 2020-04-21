function getWidthForTinyMCE(textarea)
{
	if ($(textarea).hasClass("full"))
	{
		return "";
	}
	return "510";
}

function getHeightForTinyMCE(textarea)
{
	if ($(textarea).hasClass("full"))
	{
		return "215";
	} else if ($(textarea).hasClass("half"))
	{
		return "150";
	}
	return "100";
}

function closeFullScreen(button, apply)
{
	var id = $(button).closest("td").find("textarea").last().attr("id");
	tinyMCE.get(id).execCommand('mceFullScreen');
	
	var buttons = $("#" + id).closest("tr").find(".edittextbuttons").first();
	
	if (apply)
	{
		var btn = $(buttons).find(".btn-primary").first();			
		save(btn[0]);
	} else {
		var btn = $(buttons).find(".btn-default").first();	
		cancel(btn[0]);
	}
}


var sneaky = null;
var inPlaceHolderInit = false;
var closeOverlayDivsEnabled = false;

function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}

$(function() {
	$.fn.modal.Constructor.prototype.enforceFocus = function () { };
	
	$('#export-name').keyup(function(e){
	    if(e.keyCode == 13){
	    	checkAndStartExport($('#export-name').val());
	    }
	}) ;
	
	$.fn.hasScrollBar = function() {
        return this.get(0).scrollHeight > this.height();
    };
    
	$(".modal-dialog").each(function(){
		if (!$(this).hasClass("runnerdialog"))
		{
			initModals($(this));
		}
	});
	
	$(document).bind("keydown", "Ctrl+return",function(){
		if ($('.modal:visible .modal-footer .btn-primary').length > 0)
		{
			$('.modal:visible .modal-footer .btn-primary').click();			
		} else {
			$('.modal:visible .modal-body .btn-primary').first().click();
		}
	
	});
});

function initModals(item)
{
	$(item).on("resize", function(event, ui) {
    	//this is raised for all children too but we only want to handle the parents
    	//if (ui.originalElement.hasClass("modal"))
    	//{
		modalResize(ui);
		if (!$(this).hasClass("resized"))
		{
			$(this).addClass("resized");
			
			var w = $(this).width();
			var h = $(this).height() + 15;
			
			$(this).resizable( "option", "minHeight", h );
			$(this).resizable( "option", "minWidth", w );

		};		
    	//}
	});
	
	
	var w = $(item).width();
	var h = $(item).height() + 15;
	
	var iswaitdialog = $(item).find(".modal-body").find("img[src*='ajax-loader']").length > 0 ||  $(item).find(".modal-body").find(".dialog-wait-image").length > 0;
	
	if (!iswaitdialog)
	$(item).resizable({
		maxHeight: $(window).height(),
	    maxWidth: $(window).width(),
	    minWidth: w
	});
}
             
    if (!String.prototype.trim) {
    	String.prototype.trim=function(){return this.replace(/^\s+|\s+$/g, '');};
	}
    
    jQuery.fn.outerHTML = function(s) {
        return s
            ? this.before(s).remove()
            : jQuery("<p>").append(this.eq(0).clone()).html();
    };
    
    if (!String.prototype.stripHtml) {
    	String.prototype.stripHtml=function(){
    		var noHTMLString = this.replace(/(<([^>]+)>)/ig,"");
    		return noHTMLString.trim();
    	};
	}
    
    if (!String.prototype.stripHtml115) {
    	String.prototype.stripHtml115=function(){
    		var noHTMLString = this.stripHtml();
    		if (noHTMLString.length > 115)
    		{
    			noHTMLString = noHTMLString.substring(0, 115) + "<span class='titletooltip'>...</span>";
    		}
    		return noHTMLString;
    	};
	}
    
    if (!String.prototype.replaceAll) {
	    String.prototype.replaceAll = function(search, replacement) {
	        var target = this;
	        return target.replace(new RegExp(search, 'g'), replacement);
	    };
    }
    
    var replacer = function (search, replace, str) {
        return str.split(search).join(replace);
    };
    
	function logout()
	{
		$("#logoutform").submit();
	}
    
 function checkAndStartExport(name)
 {
	 
	$("#export-name-dialog").find(".validation-error").hide();
	 
	if (name === null || name.trim().length === 0)
	{
            $("#export-name-dialog").find("#validation-error-required").show();
            return;
        }
	
        var reg = /^[a-zA-Z0-9-_\.]+$/;
        if( !reg.test( name ) ) {
            $("#export-name-dialog").find("#validation-error-exportname").show();
            return;		  
        };
		
        startExport(name);
        $("#export-name-dialog").modal("hide");	 
 }
 
 function strip_word(s)
 {
	 s = replacer("","",s);
	 return s;
 }
 
 function replaceBRs(str)
 {
//	if (str.indexOf("<p") != 0)
//	{
//		str = "<p>" + str + "</p>";
//	}
	str = replacer("<br />","</p><p>",str);
	str = replacer("<br>","</p><p>",str);    
	str = replacer("<BR />","</p><p>",str); 
	str = replacer("<BR>","</p><p>",str);    
	return str;
 }
 
 function adaptNumbering(original)
 {
	 var div = $(original).clone();
	 $(div).find(".mandatory").remove();
	 $(div).find(".optional").remove();
	 $(div).find(".numbering").append("&nbsp;");
	 return $(div).html();
 }
    
 // Strips HTML and PHP tags from a string 
 // returns 1: 'Kevin <b>van</b> <i>Zonneveld</i>'
 // example 2: strip_tags('<p>Kevin <img src="someimage.png" onmouseover="someFunction()">van <i>Zonneveld</i></p>', '<p>');
 // returns 2: '<p>Kevin van Zonneveld</p>'
 // example 3: strip_tags("<a href='http://kevin.vanzonneveld.net'>Kevin van Zonneveld</a>", "<a>");
 // returns 3: '<a href='http://kevin.vanzonneveld.net'>Kevin van Zonneveld</a>'
 // example 4: strip_tags('1 < 5 5 > 1');
 // returns 4: '1 < 5 5 > 1'
 function strip_tags (str, allowed_tags)
 {	 
	 if (str == null || typeof str == 'undefined') return str;
	 
     var key = '', allowed = false;
     var matches = [];    var allowed_array = [];
     var allowed_tag = '';
     var i = 0;
     var k = '';
     var html = ''; 
     
     str = replacer("<li>·","<li>",str);    
     
     // Build allowes tags associative array
     if (allowed_tags) {
         allowed_array = allowed_tags.match(/([a-zA-Z0-9]+)/gi);
     }
     str += '';

     // Match tags
     matches = str.match(/(<\/?[\S][^>]*>)/gi);
     // Go through all HTML tags
     for (key in matches) {
         if (isNaN(key)) {
                 // IE7 Hack
             continue;
         }

         // Save HTML tag
         html = matches[key].toString();
         // Is tag not in allowed list? Remove from str!
         allowed = false;

         // Go through all allowed tags
         for (k in allowed_array) {            // Init
             allowed_tag = allowed_array[k];
             i = -1;

             if (i != 0) { i = html.toLowerCase().indexOf('<'+allowed_tag+'>');}
             if (i != 0) { i = html.toLowerCase().indexOf('<'+allowed_tag+' ');}
             if (i != 0) { i = html.toLowerCase().indexOf('</'+allowed_tag)   ;}
             
             // Determine
             if (i == 0) {                
            	 allowed = true;
                 break;
             }
         }
         if (!allowed) {
             str = replacer(html, "", str); // Custom replace. No regexing
         }        
         
     }

     str = str.replace(/(&nbsp;)+/g, ' ');
     str = replacer(String.fromCharCode(10)," ",str);
     str = replacer(String.fromCharCode(13),"",str);
     str = replacer("<P></P>","",str);
     return str;
 }
 
 function createDatePicker(instance)
 {
	 	var minD = null;
		var maxD = null;
		var day = null;
		var month = null;
					
		var classes = $(instance).attr('class').split(" ");
		
		for ( var i = 0, l = classes.length; i<l; ++i ) {
		 	if (strStartsWith(classes[i], 'min'))
		 	{
		 		var min = classes[i].substring(3);
		 		minD = parseMinMaxDate(min);	
		 	} else if (strStartsWith(classes[i], 'max'))
		 	{
		 		var max = classes[i].substring(3);
		 		maxD = parseMinMaxDate(max);	 		
		 	}			 	
		}				
		
		var showpanel = typeof $(instance).attr("id") ==  typeof undefined || $(instance).attr("id").indexOf("metafilter") < 0;
		
		$(instance).datepicker({
			dateFormat: 'dd/mm/yy',
			minDate: minD,
			maxDate: maxD,
			showButtonPanel: showpanel,
			
			 onSelect: function(dateText, inst) {
				 
				if ($(this).attr("data-hidden"))
				{
				  $("#" + $(this).attr("data-hidden")).val(dateText);
				} else {
				  $(this).parent().find(".hiddendate").val(dateText);
				}
			    
				if (parent != null) validateInput($(this).parent().parent(), false);	
			    if ($(this).attr("id").indexOf("metafilter") >= 0)
			    {
			    	if ($('#contributionsearchForm').length > 0)
			    	{
			    		//$('#contributionsearchForm').submit();
			    		$(this).parent().parent().find(".overlaybutton").first().html(dateText + "&nbsp;<span class='caret'></span>");
			    		$(this).parent().hide();
			    	} else if ($('#resultsForm').length > 0) {
			    		//$('#resultsForm').submit();
			    		$(this).parent().parent().find(".overlaybutton").first().html(dateText + "&nbsp;<span class='caret'></span>");
			    		$(this).parent().hide();
			    	} else if ($("#surveysarea").length > 0)
			    	{
			    		applyDateFilter($(this).attr("id"), dateText);
			    	}
			    } 				   
		
				if ($(this).attr("data-to"))
				{
					$("#" + $(this).attr("data-to")).datepicker( "option", "minDate", dateText );
					
					$("#" + $(this).attr("data-to")).removeClass (function (index, css) {
					    return (css.match (/(^|\s)min\S+/g) || []).join(' ');
					});
					
					$("#" + $(this).attr("data-to")).addClass("min" + dateText.replace(/\//g,""));						
				} else if ($(this).attr("data-from"))
				{
					$("#" + $(this).attr("data-from")).datepicker( "option", "maxDate", dateText );
					
					$("#" + $(this).attr("data-from")).removeClass (function (index, css) {
					    return (css.match (/(^|\s)max\S+/g) || []).join(' ');
					});
					
					$("#" + $(this).attr("data-from")).addClass("max" + dateText.replace(/\//g,""));
				}
			 }
		});
	
		$(instance).parent().find(".hiddendate").each(function(){
			d = $(this).val();
			$(instance).datepicker( "setDate", d );
		});
 }

	$(document).ready(function(){
				
		inPlaceHolderInit = true;
		$("input[type='text']").placeholder();
		$("input[type='password']").placeholder();
		inPlaceHolderInit = false;
			
		$(".datepicker").each(function(){			
			createDatePicker(this);						
		});
		
		var dpFunc = $.datepicker._generateHTML; //record the original
		$.datepicker._generateHTML = function(inst){
			var thishtml = $( dpFunc.call($.datepicker, inst) ); //call the original
			
			thishtml = $('<div />').append(thishtml); //add a wrapper div for jQuery context
			
			if ($(inst).attr("id").indexOf("metafilter") < 0)
			{
				//locate the button panel and add our button - with a custom css class.
				$('.ui-datepicker-buttonpane', thishtml).empty().append(
						$('<button style="float: none" class=\"btn btn-default\"\>' + cancelLabel + '</button>').click(function(){
							inst.input.datepicker('hide');
						}).hover(function(event){$(this).removeClass("ui-state-hover"); event.stopPropagation();},function(){})
					);
				
				$('.ui-datepicker-buttonpane', thishtml).append(
						$('<button style="float: none" class=\"btn btn-default\"\>' + clearLabel + '</button>').click(function(){
							$(inst.input).val('');
							inst.input.datepicker('hide');
							
							if ($("#frmEdit").length > 0)
							{
								update(inst.input);
							} else {
								validateInput($(inst.input).parent());
							}
							
							if ($(inst.input).attr("data-to"))
							{
								$("#" + $(inst.input).attr("data-to")).datepicker( "option", "minDate", "" );
								
								$("#" + $(inst.input).attr("data-to")).removeClass (function (index, css) {
								    return (css.match (/(^|\s)min\S+/g) || []).join(' ');
								});
							} else if ($(inst.input).attr("data-from"))
							{
								$("#" + $(inst.input).attr("data-from")).datepicker( "option", "maxDate", "" );
								
								$("#" + $(inst.input).attr("data-from")).removeClass (function (index, css) {
								    return (css.match (/(^|\s)max\S+/g) || []).join(' ');
								});
							}
							
						}).hover(function(event){$(this).removeClass("ui-state-hover"); event.stopPropagation();},function(){})
					);
			
			}
			
			$('.ui-datepicker-buttonpane', thishtml).css("text-align","center");
			
			thishtml = thishtml.children(); //remove the wrapper div
			
			return thishtml; //assume okay to return a jQuery
		};
		
		$('.dropdown-menu').click(function(event){
		    event.stopPropagation();
		 });
		
		$(".titletooltip").hover(
		  function() {
			    $( this ).parent().find(".completetitle").show();
			  }, function() {
				  $( this ).parent().find(".completetitle").hide();
			  }
			);
	});

	
	function selectMultipleChoiceAnswer(link)
	{
		$(link).parent().toggleClass("selected-choice"); 

		$(link).parent().find("input[type='checkbox']").each(function(){
			if ($(this).closest(".selected-choice").length > 0)
			{
				$(this).attr("checked","checked");
				$(this).prop("checked","checked");
			} else {
				$(this).removeAttr("checked");
			}
			checkDependenciesAsync(this, true);
		});
	}
	
	function disabledEventPropagation(event)
	{
	   if (event.stopPropagation){
	       event.stopPropagation();
	   }
	   else if(window.event){
	      window.event.cancelBubble=true;
	   }
	}
	
	function removeDateSelection(picker)
	{
		$(picker).datepicker("setDate");
		$(picker).parent().find("input[type=hidden]").val("");
		
		checkFilterCell($(picker).closest(".filtercell"), false);
	}
			
	function checkFilterCell(cell, background)
	{
		checkFilterCell2(cell, true, background);
		
		if ($("#ResultFilterLimit").length > 0)
		{
			var row = $(cell).closest("tr");
			var count = $(row).find(".glyphicon-remove-circle").length;
			if (count > 2)
			{
				$(row).find("input[type=text]").each(function(){
					if ($(this).parent().find(".glyphicon-remove-circle").length == 0)
					{
						$(this).prop("disabled", true);
					}
				});
				$(row).find("a").each(function(){
					if ($(this).closest("th").find(".glyphicon-remove-circle").length == 0)
					{
						$(this).addClass("disabled");
					}
				});
				
				$('#ResultFilterLimit').show();
			} else {
				$(row).find("input").removeAttr("disabled");
				$(row).find("a").removeClass("disabled");
				$('#ResultFilterLimit').hide();
			}
		}
	}
	
	function checkFilterCell2(cell, recursive, background)
	{			
		var text = false;
		 $(cell).find("input[type=text]").each(function(){
			if ($(this).val().length > 0)
			{
				text = true;	
			};
		 });	
		 
		 var hidden = false;
		 $(cell).find("input[type=hidden]").each(function(){
			if ($(this).val().length > 0)
			{
				text = true;	
			};
		 });	
		
		//reset
		if ($(cell).find("input[type='checkbox']").length > 0)
		{
			$(cell).find(".dropdown-toggle").html("All values&nbsp;<span class='caret'></span>");	
			$(cell).removeAttr("title");
		}			
		
		if ($(cell).find(".hiddendate").length > 0)
		{
			$(cell).find(".btn-toolbar").first().find(".datefilter").first().find("a").first().html(labelfrom + "&nbsp;<span class='caret'></span>");	
			$($(cell).find(".btn-toolbar").find(".datefilter")[1]).find("a").first().html(labelto + "&nbsp;<span class='caret'></span>");	
		}
		 
		if ($(cell).find("input:checked").length > 0 || text || hidden)
		{
			if (background) $(cell).css("background-color","#FFE93A");
			$(cell).find(".icon-filter").remove();
			$(cell).find(".glyphicon-remove-circle").parent().parent().remove();
			
			if ($(cell).find(".btn-toolbar").length > 0)
			{
				$(cell).find(".btn-toolbar").first().prepend("<div class='filtertools'><a data-toggle='tooltip' title='Remove filter' onclick='clearFilterCellContent(this)'><span class='glyphicon glyphicon-remove-circle black'></span></a></div>");
				
				$(cell).find(".datefilter").each(function(){
					if ($(this).find(".hiddendate").length > 0 && $(this).find(".hiddendate").val().length > 0)
					{
						$(this).find("a").first().html($(this).find(".hiddendate").val() + "&nbsp;<span class='caret'></span>");
					}						
				});
			} else if ($(cell).find(".dropdown-menu").length > 0) {
				$(cell).prepend("<div class='filtertools'><a data-toggle='tooltip' title='Remove filter' onclick='clearFilterCellContent(this)'><span class='glyphicon glyphicon-remove-circle black'></span></a></div>");
				
				var counter = 0;
				var first = allValues;
				var all = "";
				$(cell).find(".dropdown-menu").find("input:checked").each(function(){
					counter++;
					if (counter == 1)
					{
						if ($(this).attr("data-code") == null)
						{
							first = $(this).parent().text().trim();
						} else {
							first = $(this).attr("data-code");
						}
						if (first != null && first.length > 8) first = first.substring(0,8) + "...";
					}
					if (all.length > 0) all += ", ";
					all += $(this).parent().text().trim();
				});
				if (counter > 1)
				{
					first = counter + " selected";
				}
				$(cell).find(".dropdown-toggle").html(first + "&nbsp;<span class='caret'></span>");		
				$(cell).attr("title",all).attr("rel","tooltip");
			} else if ($(cell).find(".overlaymenu").length > 0) {
				$(cell).prepend("<div class='filtertools'><a data-toggle='tooltip' title='Remove filter' onclick='clearFilterCellContent(this)'><span class='glyphicon glyphicon-remove-circle black'></span></a></div>");
								
				var counter = 0;
				var first = allValues;
				var all = "";
				$(cell).find(".overlaymenu").find("input:checked").each(function(){
					counter++;
					if (counter == 1)
					{
						if ($(this).attr("data-text") == null)
						{
							first = $(this).parent().text().trim();
						} else {
							first = $(this).attr("data-text");
						}
					}
					if (all.length > 0) all += ", ";
					all += $(this).parent().text().trim();
				});
				if (counter > 1)
				{
					first = counter + " selected";
				}
				$(cell).find(".nobreak").html(first);	
				$(cell).find(".nobreak").attr("data-text",first);
				$(cell).attr("title",all).attr("rel","tooltip");
				
				//checkNoBreaks();
				
			} else {
                $(cell).prepend("<div class='filtertools'><a data-toggle='tooltip' title='Remove filter' onclick='clearFilterCellContent(this)'><span class='glyphicon glyphicon-remove-circle black'></span></a></div>");
            }
		} else if ($(cell).find(".activityselect").length > 0) {
			if ($(cell).find(".activityselect").first().val().length > 0)
			{
				if (background) $(cell).css("background-color","#FFE93A");
	            $(cell).prepend("<div class='filtertools'><a data-toggle='tooltip' title='Remove filter' onclick='clearFilterCellContent(this)'><span class='glyphicon glyphicon-remove-circle black'></span></a></div>");
	        }
		} else {
			$(cell).find(".filtertools").remove();
			
			if ($(cell).find(".overlaymenu").length > 0) {
				$(cell).find(".nobreak").html(allValues);	
				$(cell).find(".nobreak").attr("data-text",allValues);
				//checkNoBreaks();
			}		
		}
		
		if ($(cell).closest('.ptable').length > 0)
		{
			$(cell).find(".black").removeClass("black");
		}
		
		$('[data-toggle="tooltip"]').tooltip(); 
		
		var original;
		var clone;
		
		if (recursive)		
		if ($(cell).closest(".tableFloatingHeader").length > 0)
		{
			//synchronize with original
			$(cell).find("input").each(function()
			{
				var name = $(this).attr("name");
				var value =   $(this).val();
				
				if ($(this).hasClass("check"))
				{
					original = $(".tableFloatingHeaderOriginal").find("input[name='" + name + "'][value='" + value + "']");
				} else {
					original = $(".tableFloatingHeaderOriginal").find("input[name='" + name + "']");
				}				
				
				if ($(original).attr("type") == "text") $(original).val($(this).val());
				if ($(original).attr("type") == "hidden") $(original).val($(this).val());
				if ($(original).attr("type") == "checkbox")
				{
					if ($(this).is(":checked"))
					{
						$(original).prop("checked","checked");
					} else {
						$(original).removeAttr("checked");
					}					
				}
			});
			checkFilterCell2($(original).closest(".filtercell"), false);
		} else if ($(cell).closest(".tableFloatingHeaderOriginal").length > 0)
		{
			//synchronize with clone
			$(cell).find("input").each(function()
			{
				var name = $(this).attr("name");
				var value =   $(this).val();
				
				if ($(this).hasClass("check"))
				{
					clone = $(".tableFloatingHeader").find("input[name='" + name + "'][value='" + value + "']");
				} else {
					clone = $(".tableFloatingHeader").find("input[name='" + name + "']");	
				}
				
				if ($(clone).attr("type") == "text") $(clone).val($(this).val());
				if ($(clone).attr("type") == "hidden") $(clone).val($(this).val());
				if ($(clone).attr("type") == "checkbox")
				{
					if ($(this).is(":checked"))
					{
						$(clone).prop("checked","checked");
					} else {
						$(clone).removeAttr("checked");
					}					
				}
			});
			checkFilterCell2($(clone).closest(".filtercell"), false);
		}
	}
	
	function checkNoBreaks()
	{					
		$(".nobreak").each(function(){
			if ($(this).attr("data-text")) {
				$(this).text($(this).attr("data-text"));
				//$(this).removeAttr("data-text");
			}
		});
				
		$(".nobreak").each(function(){
			var text = $(this).text();
			
			if ($(this).parent().height() > 30)
			{
				$(this).attr("data-text",text);
			}
			
			while ($(this).parent().height() > 30  || ($(this).closest("div").width() - $(this).width() < 70))
			{
				text = text.substring(0, text.length - 2);
				$(this).text(text + "...");	
			}
		});
	}
	
	function clearFilterCellContent(link)
	{
		var cell = $(link).closest('.filtercell');
		$(cell).find(".check").removeAttr("checked");
		$(cell).find("input[type='text']").val("");
		$(cell).find(".datepicker").each(function(){
			removeDateSelection(this);
		});
		$(cell).find(".activityselect").val("");
		
		checkFilterCell(cell, true);
		$(cell).css("background-color", "");
		
		if ($("#contributionsearchForm").length > 0 || $(".noautosubmitonclearfilter").length > 0){
			//we do not automatically submit due to performance reasons			
			return;
		}
		
		if ($(cell).closest("#contactshead").length > 0){
			_participants.loadAttendees(true); 
		} else if ($(cell).closest("#selectedcontactshead").length > 0){
			_participants.selectedGroup().filterContacts(); 
		} else if ($(cell).closest("#selectedeccontactshead").length > 0){
			_participants.selectedGroup().filterUsers(); 			
		} else if ($(cell).closest("#eccontactshead").length > 0){
			_participants.loadUsers(true); 
		} else if ($(link).closest('form').length == 0 && $("#resultsForm").length > 0){
			$("#resultsForm").submit();	
		} else if ($(link).closest('form').length == 0 && $("#publishsurveysform").length > 0){
			$("#publishsurveysform").submit();	
		} else {
			var found = false;
			$("form").each(function(){
				if ($(this).find(".filtercell").length > 0)
				{
					$(this).submit();
					found = true;
					return;
				}
			});
			if (!found)	$("form").submit();
		}
	}
	
	function resetValidationErrors(parent)
	{
		$(parent).find(".validation-error").each(function(){
			$(this).remove();
		});
		
		$("#exceptionlogdiv").remove();
	}
	
	function validateInputAndSubmit(form)
	{
		var result = validateInput(form);
		
		if (result == false)
		{
			goToFirstValidationError(form);
		} else {
			
			if ($("#survey\\.start"))
			{
				$("#survey\\.start").removeAttr("disabled"); 
				$("#survey\\.end").removeAttr("disabled"); 
				$("#startHour").removeAttr("disabled"); 
				$("#endHour").removeAttr("disabled"); 
			}
			
			$(".modal").modal('hide');
			$("#generic-wait-dialog").modal("show");
			$(form).submit();
		}

	}
	
	function goToFirstValidationError(form)
	{
		var p = $(form).find(".validation-error, .validation-error-server, .validation-error-keep").first().closest(".single-page").attr("id");	
		if(!(typeof p === "undefined"))
		{
			page = parseInt(p.substring(4));
			$(".single-page").hide();		
			$("#page" + page).show();
			checkPages();
		}
		
		$('html, body').animate({
	         scrollTop: $(form).find(".validation-error, .validation-error-server, .validation-error-keep").first().parent().offset().top - 200
	     }, 2000);

		 $(form).find(".validation-error, .validation-error-server, .validation-error-keep").first().parent().find("input").first().focus();
	}
	
	var validationinfo = "";
	
	function validateInputAndSubmitRunner(form)
	{		
		$("#btnSubmit").hide();
		$("#busydialog").modal('show');
		$("#exceptionlogdiv").remove();
		
		var errorhappened = false;
		var sessiontimeout = false;
		var networkproblems = false;
		var unknownerror = false;
		
		try {
		
			var result = validateInput(form);
			
			if (result == false)
			{
				var div = document.createElement("div");
				$(div).attr("id","exceptionlogdiv").addClass("validation-error").attr("aria-live", "polite").css("color","#999");
				$(div).append(varErrorCheckValidation).append("<br />");
				//$(div).append(varErrorCheckValidation2).append("<br />");
				//$(div).append("<span style='color: #bbb; font-style: italic'>" + validationinfo + "</span>");
				$("#btnSubmit").parent().append(div); 
			} else if ($(".g-recaptcha.unset").length > 0)	{
				$('#runner-captcha-empty-error').show();
			} else {
				
				saveCookies();
				
				$.ajax({type: "POST",
					url: contextpath + "/runner/checksession",
					async: false,
					beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
					error: function(result)
				    {
						var s = result.statusText.toLowerCase();
						if (strStartsWith(s,"forbidden") || result.status == 403)
						{
							sessiontimeout = true;
						} else if (strStartsWith(s,"networkerror"))
						{
							networkproblems = true;
						} else {
							unknownerror = true;
						}
				    },
					success: function(result)
				    {
				    	//everything is ok
				    }
				 });
				
				if (!sessiontimeout && !networkproblems) {
					$(form).submit();
					return;				
				}
			}
		
		} catch(err)
		{
			errorhappened = true;
			var stacktrace = err.stack || err.stacktrace || err;
			$("#btnSubmit").parent().append("<div id='exceptionlogdiv' class='validation-error'>" + varExceptionDuringSave + "&nbsp;" + stacktrace + "</div>");
		}

		$("#btnSubmit").show();
		$("#busydialog").modal('hide');
		
		if (sessiontimeout)
		{
			$("#sessiontimeoutdialog").modal('show');
		} else if (networkproblems) {
			$("#networkproblemsdialog").modal('show');
		} else if (errorhappened)
		{
			$("html, body").animate({
				scrollTop : $("#exceptionlogdiv").offset().top
			}, "fast");		
		} else {
			goToFirstValidationError(form);
		}
	}
	
	function validateInput(parent)
	{
		return validateInput(parent, false);
	}
	
	function validateInput(parent, blur)
	{
		validationinfo = "";
		//this is a workaround for a problem with placeholders in IE8
		if (inPlaceHolderInit) return;
				
		//to switch on/off client side validation (for testing)
		//return true;
		
		var result = true;
		
		resetValidationErrors(parent);		
		
		$(parent).find(".required").each(function(){
			
			if ($(this).closest(".survey-element").hasClass("untriggered"))
			{
				return;
			}
			
			if ($(this).closest(".hidecopy").length > 0 && !$(this).closest(".hidecopy").is(":visible"))
			{
				return;
			}			
			
			//if survey has multi-tab mode enabled and parent section is not visible
			if ($("#multipaging").length > 0 && $("#multipaging").val() == "true")
			{
				var section = $(this).closest("fieldset").siblings("fieldset").find(".sectionitem").last();
				if (section.length > 0)
				{
					if (section.hasClass("untriggered"))
					{
						return;
					}
				}
			}
			
			if ($(this).hasClass("rating"))
			{
				var valid = true;
				
				if ($(this).val().length == 0)
				{
					valid = false;
				} else {
					var v = parseInt($(this).val());
					if (v == 0)
					{
						valid = false;
					}
				}	
				
				if (!valid)
				{
					validationinfo += $(this).closest(".survey-element").attr("id") + " (R) ";
					$(this).parent().append("<div class='validation-error' aria-live='polite'>" + requiredText + "</div>");
					result = false;
				}
			} else if ($(this).hasClass("file-uploader"))
			{
				if ($(this).parent().find(".uploaded-files").find("div").length == 0)
				{
					validationinfo += $(this).closest(".survey-element").attr("id") + " (R) ";
					$(this).after("<div class='validation-error' aria-live='polite'>" + requiredText + "</div>");
					result = false;
				}
			} else if ($(this).attr("type") == "radio")
			{				
				if ($(this).closest(".matrixtable").length > 0)
				{
					var matrix = $(this).closest(".matrixtable");
					var row =  $(this).closest("tr");
					
					if ($(this).closest(".matrix-question").hasClass("untriggered"))
					{
						return;
					}
					
					if (typeof $("input[name='" + $(this).attr("name") + "']:checked").val() == 'undefined' && $(row).find(".validation-error").length == 0)
					{
						validationinfo += $(this).attr("name") + " (R) ";
						$(row).find("th").first().append("<div class='validation-error' aria-live='polite'>" + requiredText + "</div>");
						result = false;
					};
					
				} else {
					
					if ($(this).closest(".matrix-question").length > 0)
					{
						//in mobile view of matrix element
						if ($(this).closest(".matrix-question").hasClass("untriggered"))
						{
							return;
						}
					}
						
					if (typeof $("input[name='" + $(this).attr("name") + "']:checked").val() == 'undefined' && $(this).closest(".answer-columns").find(".validation-error").length == 0)
					{
						validationinfo += $(this).attr("name") + " (R) ";
						if ($("input[name='" + $(this).attr("name") + "']:last").closest(".answer-columns").length > 0)
						{
							$("input[name='" + $(this).attr("name") + "']:last").closest(".answer-columns").append("<div class='validation-error' aria-live='polite'>" + requiredText + "</div>");
						} else {
							if ($("input[name='" + $(this).attr("name") + "']:last").parent().find(".validation-error").length == 0)
							{
								$("input[name='" + $(this).attr("name") + "']:last").parent().append("<div class='validation-error' aria-live='polite'>" + requiredText + "</div>");
							}
						}
						result = false;
					};
				};
			} else if ($(this).prop("tagName").toLowerCase() == "select")
			{
				if ($(this).find(":selected").length == 0 || $(this).find(":selected").val().length == 0)
				{
					validationinfo += $(this).attr("name") + " (R) ";
					$(this).parent().append("<div class='validation-error' aria-live='polite'>" + requiredText + "</div>");
					result = false;
				}
			} else if ($(this).attr("type") == "checkbox")
			{
				if ($(this).closest(".matrixtable").length > 0)
				{
					var matrix = $(this).closest(".matrixtable");
					var row =  $(this).closest("tr");
					
					if ($(this).closest(".matrix-question").hasClass("untriggered"))
					{
						return;
					}
					
					if (typeof $("input[name='" + $(this).attr("name") + "']:checked").val() == 'undefined' && $(row).find(".validation-error").length == 0)
					{
						validationinfo += $(this).attr("name") + " (R) ";
						$(row).find("td").first().append("<div class='validation-error'aria-live='polite'>" + requiredText + "</div>");
						result = false;
					};
				} else if ($(this).closest(".gallery-div").length > 0)
				{
					if (typeof $("input[name='" + $(this).attr("name") + "']:checked").val() == 'undefined' && $(this).closest(".gallery-div").find(".validation-error").length == 0)
					{
						validationinfo += $(this).attr("name") + " (R) ";
						$(this).closest(".gallery-div").append("<div class='validation-error'aria-live='polite'>" + requiredText + "</div>");
						result = false;
					};					
				} else {
					if ( $(this).closest(".answer-columns").length > 0)
					{
						if (typeof $("input[name='" + $(this).attr("name") + "']:checked").val() == 'undefined' && $(this).closest(".answer-columns").find(".validation-error").length == 0)
						{
							validationinfo += $(this).attr("name") + " (R) ";
							$("input[name='" + $(this).attr("name") + "']:last").closest(".answer-columns").append("<div class='validation-error' aria-live='polite'>" + requiredText + "</div>");
							result = false;
						};	
					} else {
						if (typeof $("input[name='" + $(this).attr("name") + "']:checked").val() == 'undefined' && $(this).parent().find(".validation-error").length == 0)
						{
							validationinfo += $(this).attr("name") + " (R) ";
							
							if ($(this).attr("name") == "radio-new-survey-audience")
							{
								$("input[name='" + $(this).attr("name") + "']:last").parent().parent().append("<div class='validation-error' aria-live='polite'>" + requiredText + "</div>");
							} else {							
								$("input[name='" + $(this).attr("name") + "']:last").parent().append("<div class='validation-error' aria-live='polite'>" + requiredText + "</div>");
							}
							result = false;
						};
					}
					
				};
			} else if ($(this).hasClass("listbox"))
			{
				var value = $(this).find(":checked").length;
				if (value == 0) value = $(this).find("input[type='checkbox'][checked='checked']").length;
				if (value == 0)
				{
					validationinfo += $(this).attr("name") + " (R) ";
					$(this).after("<div class='validation-error' aria-live='polite'>" + requiredText + "</div>");
					result = false;
				};
			} else if ($(this).hasClass("single-choice"))
			{
				var value = $(this).find(":checked").length;
				if (value == 0) value = $(this).find("input[type='checkbox'][checked='checked']").length;
				if (value == 0)
				{
					validationinfo += $(this).attr("name") + " (R) ";
					$(this).after("<div class='validation-error' aria-live='polite'>" + requiredText + "</div>");
					result = false;
				};
			} else if ($(this).hasClass("date") || $(this).hasClass("datepicker"))
			{
				if ($(this).val().length == 0 || $(this).val() == 'DD/MM/YYYY')
				{
					validationinfo += $(this).attr("name") + " (R) ";
					if ($(this).parent().find(".ui-datepicker-trigger").length > 0 && !($(this).hasClass("hourselector")) )
					{
						$(this).parent().find(".ui-datepicker-trigger").first().after("<div class='validation-error' aria-live='polite'>" + requiredText + "</div>");
					} else if ($(this).parent().is("td")) {
						$(this).parent().append("<div class='validation-error' aria-live='polite'>" + requiredText + "</div>");						
					} else {
						$(this).parent().parent().append("<div class='validation-error' aria-live='polite'>" + requiredText + "</div>");						
					}
					result = false;
				};				
			} else if ($(this).closest(".tabletable").length > 0) {
				if ($(this).val().length == 0)
				{
					validationinfo += $(this).attr("name") + " (R) ";
					if ($(this).closest(".tabletable").parent().find(".validation-error").length == 0)
					$(this).closest(".tabletable").after("<div class='validation-error' aria-live='polite'>" + requiredText + "</div>");
					result = false;
				}
			} else if ($(this).hasClass("tinymce")) {
				if ($(this).val().length == 0)
				{
					validationinfo += $(this).attr("name") + " (R) ";
					$(this).after("<div class='validation-error' aria-live='polite'>" + requiredText + "</div>");
					result = false;
				}
			} else {			
				if ($(this).val().trim().length == 0 && !$(this).hasClass("comparable-second"))
				{
					validationinfo += $(this).attr("name") + " (R) ";
					if ($(this).attr("name") == "survey.allowedContributionsPerUser")
					{
						$(this).parent().parent().append("<div class='validation-error' aria-live='polite'>" + requiredText + "</div>");
					} else if ($(this).attr("name") == "number-new-tokens")
					{
						$(this).parent().append("<div class='validation-error' aria-live='polite'>" + requiredText + "</div>");
					} else if ($(this).hasClass("email") && ($(this).attr("id") != "add-user-email") && ($(this).attr("id") != "supportemail")) {
						$(this).parent().after("<div class='validation-error' aria-live='polite'>" + requiredText + "</div>");
					} else {
						$(this).after("<div class='validation-error' aria-live='polite'>" + requiredText + "</div>");
					}
					
					result = false;
				};
			};
		});
		
		$(parent).find(".hp").each(function(){
			if ($(this).val().length > 0)
		 	{
		 		validationinfo +=  "honeypot ";
		 		$(this).after("<div class='validation-error'>" + honeypotError + "</div>");
		 		$("#btnSubmit").parent().append("<div id='exceptionlogdiv' class='validation-error'>Text '" + $(this).val() + "' in honeypot element found. Please remove it.</div>");
		 		result = false;
		 	}                      
		});
		
		$(parent).find(".interdependent").each(function(){
			//only if visible
			if ($(this).closest(".survey-element").hasClass("untriggered"))
			{
				return;	
			}
			
			var s = "";
			$(this).find("input:checked").each(function(){
				var value = "#" + $(this).val() + "#";
				
				if (s.indexOf(value) != -1)
				{
					validationinfo += $(this).attr("name") + " (InterDependent) ";
					$(this).after("<div class='validation-error' aria-live='polite'>" + interdependentText + "</div>");
					result = false;
				} else {
					s = s + value;
				}
			});				
			
		});
		
		$(parent).find(".comparable").each(function(){
			
			//only if visible
			if ($(this).closest(".survey-element").hasClass("untriggered"))
			{
				return;	
			}
			if ($(this).hasClass("comparable-second")) return;
			
			var value = $(this).val();
			var second = $(this).parent().find(".comparable-second").first().val();
			
			if ((!blur || value.length > 0 && second.length > 0) && value != "********")
			{
				if ($(this).parent().find(".validation-error").length == 0)
				if (value != second)
				{
					validationinfo += $(this).attr("name") + " (COMP) ";
					$(this).parent().find(".comparable-second").after("<div class='validation-error' aria-live='polite'>" + nomatchText + "</div>");
					//$(this).focus();
					result = false;
				};
			}
		});
		
		$(parent).find(".regex").each(function(){
			
			//only if visible
			if ($(this).closest(".survey-element").hasClass("untriggered"))
			{
				return;	
			}
			
			var value = $(this).val();
			if (value.trim().length == 0) return;
			
			var regex = $(this).closest(".survey-element").find("input[name^='regex']").val();
			
			var validationReg = new RegExp(regex,"g"); ///^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
		    if( !validationReg.test( value ) ) {
		    	if ($(this).parent().find(".validation-error").length == 0)
				{
		    		validationinfo += $(this).attr("name") + " (REGEX) ";
					$(this).after("<div class='validation-error' aria-live='polite'>" + noRegExmatchText + "</div>");
					$(this).focus();
					result = false;
				};
		    } 			
			
		});
		
		$(parent).find(".uuid").each(function(){
			
			//only if visible
			if ($(this).closest(".survey-element").hasClass("untriggered"))
			{
				return;	
			}
			
			var value = $(this).val().trim();
			if (value.trim().length == 0) return;
			
			//we do this to remove blanks before and after the email address
			$(this).val(value);
						
		    if( !validateUUID(value)) {
		    	if ($(this).parent().find(".validation-error").length == 0)
				{
		    		validationinfo += $(this).attr("name") + " (InvalidUUID) ";
		    		$(this).after("<div class='validation-error' aria-live='polite'>" + invalidCaseId + "</div>");
					result = false;
				};
		    } 			
			
		});
		
		$(parent).find(".email").each(function(){
			
			//only if visible
			if ($(this).closest(".survey-element").hasClass("untriggered"))
			{
				return;	
			}
			
			var value = $(this).val().trim();
			if (value.trim().length == 0) return;
			
			//we do this to remove blanks before and after the email address
			$(this).val(value);
			
			if ($(this).attr("id") != "add-user-email" && $(this).attr("id") != "supportemail")
			{
			    if( !validateEmail(value)) {
			    	if ($(this).parent().parent().find(".validation-error").length == 0)
					{
			    		validationinfo += $(this).attr("name") + " (EMAIL) ";
			    		$(this).parent().after("<div class='validation-error' aria-live='polite'>" + invalidEmail + "</div>");
						result = false;
					};
			    } else {
			    	$(this).parent().parent().find(".validation-error").remove();
			    }
			} else {
				if( !validateEmail(value)) {			
					validationinfo += $(this).attr("name") + " (EMAIL) ";
					$(this).after("<div class='validation-error' aria-live='polite'>" + invalidEmail + "</div>");		
					result = false;
				}
			}
			
		});
		
		$(parent).find(".date").each(function(){
			
			//only if visible
			if ($(this).closest(".survey-element").hasClass("untriggered"))
			{
				return;	
			}
			
			var value = $(this).val();
			
			$(this).val(value.trim());
			value = $(this).val();
			
			if (value.length == 0 || value == 'DD/MM/YYYY') return;
			
			var date = parseDate2(value);
			if (date == null)
			{
				validationinfo += $(this).attr("name") + " (DATE) ";
				if ($(this).parent().find(".ui-datepicker-trigger").length > 0 && !($(this).hasClass("hourselector")) )
				{
					$(this).parent().find(".ui-datepicker-trigger").first().after("<div class='validation-error' aria-live='polite'>" + invalidDate + "</div>");
				} else {
					$(this).parent().after("<div class='validation-error' aria-live='polite'>" + invalidDate + "</div>");						
				}
				result = false;
			}
			
			var classes = $(this).attr('class').split(" ");
						
			for ( var i = 0, l = classes.length; i<l; ++i ) {
				if ($(this).parent().find(".validation-error").length == 0)
				{
				 	if (strStartsWith(classes[i], 'min'))
				 	{
				 		var min = classes[i].substring(3);
				 		minD = parseMinMaxDate(min);
				 		
				 		if (minD > date)
				 		{
				 			validationinfo += $(this).attr("name") + " (MinDate) ";
				 			if ($(this).parent().find(".ui-datepicker-trigger").length > 0 && !($(this).hasClass("hourselector")) )
							{
								$(this).parent().find(".ui-datepicker-trigger").first().after("<div class='validation-error' aria-live='polite'>" + valuetoosmall + "</div>");
							} else {
								$(this).parent().after("<div class='validation-error' aria-live='polite'>" + valuetoosmall + "</div>");						
							}
				 			result = false;
				 		};
				 	} else if (strStartsWith(classes[i], 'max'))
				 	{
				 		var max = classes[i].substring(3);
				 		maxD = parseMinMaxDate(max);
				 		
				 		if (maxD < date)
				 		{
				 			validationinfo += $(this).attr("name") + " (MaxDate) ";
				 			if ($(this).parent().find(".ui-datepicker-trigger").length > 0 && !($(this).hasClass("hourselector")) )
							{
								$(this).parent().find(".ui-datepicker-trigger").first().after("<div class='validation-error' aria-live='polite'>" + valuetoolarge + "</div>");
							} else {
								$(this).parent().after("<div class='validation-error' aria-live='polite'>" + valuetoolarge + "</div>");						
							}
				 			result = false;
				 		};
				 	};
				};	
			};	
			
		});
		
		
		$(parent).find(".xhtml").each(function(){
			
			var label = "";
			if ($(this).hasClass("tinymce"))
			{
				label = $(this).html();
			} else {
				
				//only if visible
				if ($(this).closest(".survey-element").hasClass("untriggered"))
				{
					return;	
				}
				
				label = $(this).val();
			}
			
			if (!checkXHTMLValidity(label))
			{
				if ($(this).parent().find(".validation-error").length == 0)
				{
					validationinfo += $(this).attr("name") + " (XHTML) ";
					$(this).after("<div class='validation-error' aria-live='polite'>" + invalidXHTML + "</div>");
		    		
					$(this).focus();
					result = false;
				};
			}
			
		});
		
		$(parent).find(".targeturl").each(function(){
			
			//only if visible
			if ($(this).closest(".survey-element").hasClass("untriggered"))
			{
				return;	
			}
			
			var value = $(this).val();
			if (value.trim().length == 0) return;
						
		    if( !validateUrl(value)) {
		    	if ($(this).parent().find(".validation-error").length == 0)
				{
		    		validationinfo += $(this).attr("name") + " (URL) ";
		    		$(this).after("<div class='validation-error' aria-live='polite'>" + invalidURL + "</div>");
					$(this).focus();
					result = false;
				};
		    } 			
			
		});
		
		$(parent).find(".url").each(function(){
			
			//only if visible
			if ($(this).closest(".survey-element").hasClass("untriggered"))
			{
				return;	
			}
			
			var value = $(this).val();
			if (value.trim().length == 0) return;
						
		    if( !validateUrl(value)) {
		    	if ($(this).parent().find(".validation-error").length == 0)
				{
		    		validationinfo += $(this).attr("name") + " (URL) ";
		    		$(this).after("<span style='margin-left: 10px' class='validation-error' aria-live='polite'>" + invalidURL + "</span>");
					$(this).focus();
					result = false;
				};
		    } 			
			
		});
		
		$(parent).find("#new-survey-shortname").each(function(){
			
			var value = $(this).val();
			
			 var reg = /^[a-zA-Z0-9-_]+$/;
			 if ($(this).parent().find(".validation-error").length == 0)
			    if( !reg.test( value ) ) {
			    	$(this).after("<div class='validation-error' aria-live='polite'>" + shortnameText + "</div>");
					$(this).focus();
					result = false;
			    } else if( value.indexOf("__") > -1 ) {
			    	$(this).after("<div class='validation-error' aria-live='polite'>" + shortnameText2 + "</div>");
					$(this).focus();
					result = false;
			    } ;
		});
		
		$(parent).find("#new-survey-shortname-restore").each(function(){
			
			var value = $(this).val();
			
			 var reg = /^[a-zA-Z0-9-_]+$/;
			 if ($(this).parent().find(".validation-error").length == 0)
			    if( !reg.test( value ) ) {
			    	$(this).after("<div class='validation-error' aria-live='polite'>" + shortnameText + "</div>");
					$(this).focus();
					result = false;
			    } else if( value.indexOf("__") > -1 ) {
			    	$(this).after("<div class='validation-error' aria-live='polite'>" + shortnameText2 + "</div>");
					$(this).focus();
					result = false;
			    } ;
		});
		
		$(parent).find(".freetext").each(function(){
			
			//only if visible
			if ($(this).closest(".survey-element").hasClass("untriggered"))
			{
				return;	
			}
			
			var classes = $(this).attr('class').split(" ");
			var value = $(this).val();
			if (utf8.moreThan3Bytes(value)) {
				$(this).after("<div class='validation-error' aria-live='polite'>" + invalidCharacter + "</div>");
	 			result = false;
			}
			
			if (!($(this).attr("type") == "password" && $(this).val() == "********"))
			{
				for ( var i = 0, l = classes.length; i<l; ++i ) {
					if ($(this).parent().find(".validation-error").length == 0)
					{
					 	if (strStartsWith(classes[i], 'min'))
					 	{
					 		var min = classes[i].substring(3);
					 		if (value.length < parseInt(min) && value.length > 0)
					 		{
					 			validationinfo += $(this).attr("name") + " (MinFT) ";
					 			$(this).after("<div class='validation-error' aria-live='polite'>" + textnotlongenoughText + "</div>");
					 			result = false;
					 		};
					 	} else if (strStartsWith(classes[i], 'max'))
					 	{
					 		var max = classes[i].substring(3);
					 		
					 		if (value.length > parseInt(max))
					 		{
					 			validationinfo += $(this).attr("name") + " (MaxFT) ";
					 			if (max == "5000")
					 			{
					 				$(this).after("<div class='validation-error' aria-live='polite'>" + texttoolong5000Text + "</div>");
						 		} else {
					 				$(this).after("<div class='validation-error' aria-live='polite'>" + texttoolongText + "</div>");
						 		}
					 			
					 			result = false;
					 		};
					 	};
					};	
				};
			};
		});
		
		$(parent).find(".htCore").each(function(){
			
			var correct = true;
			
			//only if visible
			if ($(this).closest(".survey-element").hasClass("untriggered"))
			{
				return;	
			}

			$(this).find("td").each(function()	{
				
				if(utf8.moreThan3Bytes($(this).html()))
				{
					result = false;
					correct = false;
				}
			});
			
			if(!correct)
			{
				validationinfo += $(this).attr("name") + " (InvChar) ";
				$(this).closest(".dataTable").after("<div class='validation-error' aria-live='polite'>" + invalidCharacter + "</div>");
			}

		});
		
		$(parent).find(".matrixtable").each(function(){			
			//only if visible
			if ($(this).closest(".survey-element").hasClass("untriggered"))
			{
				return;	
			}
			
			var classes = $(this).attr('class').split(" ");
			
			var numAnsweredQuestions = 0;
			$(this).find("tr").each(function(index)	{
				if (index > 0)
				{
					if ($(this).find("input:checked").length > 0)
					{
						numAnsweredQuestions += 1;
					}
				}
			});

			for ( var i = 0, l = classes.length; i<l; ++i ) {
				if ($(this).parent().find(".validation-error").length == 0)
				{
				 	if (strStartsWith(classes[i], 'minrows'))
				 	{
				 		var min = classes[i].substring(7);
				 		if (numAnsweredQuestions > 0 && numAnsweredQuestions < parseInt(min))
				 		{
				 			validationinfo += $(this).closest(".survey-element").attr("id") + " (MinMatrixRow) ";
				 			$(this).after("<div class='validation-error' aria-live='polite'>" + notenoughrowsanswerederror + "</div>");
				 			result = false;
				 		};
				 	} else if (strStartsWith(classes[i], 'maxrows'))
				 	{
				 		var max = classes[i].substring(7);
				 		
				 		if (numAnsweredQuestions > parseInt(max))
				 		{
				 			validationinfo += $(this).closest(".survey-element").attr("id") + " (MaxMatrixRow) ";
				 			$(this).after("<div class='validation-error' aria-live='polite'>" + toomanyrowsanswerederror + "</div>");
				 			result = false;
				 		};
				 	};
				};	
			};			
		});
		
		$(parent).find(".matrixdiv").each(function(){			
			//only if visible
			if ($(this).closest(".survey-element").hasClass("untriggered"))
			{
				return;	
			}
			
			var classes = $(this).attr('class').split(" ");
			
			var numAnsweredQuestions = 0;
			$(this).find(".answers-table").each(function(index)	{
				if ($(this).find("input:checked").length > 0)
				{
					numAnsweredQuestions += 1;
				}
			});

			for ( var i = 0, l = classes.length; i<l; ++i ) {
				if ($(this).parent().find(".validation-error").length == 0)
				{
				 	if (strStartsWith(classes[i], 'minrows'))
				 	{
				 		var min = classes[i].substring(7);
				 		if (numAnsweredQuestions > 0 && numAnsweredQuestions < parseInt(min))
				 		{
				 			validationinfo += $(this).closest(".survey-element").attr("id") + " (MinMatrixRow) ";
				 			$(this).after("<div class='validation-error' aria-live='polite'>" + notenoughrowsanswerederror + "</div>");
				 			result = false;
				 		};
				 	} else if (strStartsWith(classes[i], 'maxrows'))
				 	{
				 		var max = classes[i].substring(7);
				 		
				 		if (numAnsweredQuestions > parseInt(max))
				 		{
				 			validationinfo += $(this).closest(".survey-element").attr("id") + " (MaxMatrixRow) ";
				 			$(this).after("<div class='validation-error' aria-live='polite'>" + toomanyrowsanswerederror + "</div>");
				 			result = false;
				 		};
				 	};
				};	
			};			
		});
		
		$(parent).find(".selection").each(function(){
			
			//only if visible
			if ($(this).closest(".survey-element").hasClass("untriggered"))
			{
				return;	
			}
			
			var classes = $(this).attr('class').split(" ");
			var value = $(this).closest(".gallery-table").find(":checked").length;
			for ( var i = 0, l = classes.length; i<l; ++i ) {
				if ($(this).closest(".gallery-table").parent().find(".validation-error").length == 0)
			 	if (strStartsWith(classes[i], 'limit'))
			 	{
			 		var max = classes[i].substring(5);
			 		if (value > parseFloat(max))
			 		{
			 			validationinfo += $(this).attr("name") + " (MaxSelection) ";
			 			$(this).closest(".gallery-table").after("<div class='validation-error' aria-live='polite'>" + toomanyanswers + "</div>");
			 			result = false;
			 		}
			 	};
			 	
			};
		});
		
		$(parent).find(".number").each(function(){
			
			//only if visible
			if ($(this).closest(".survey-element").hasClass("untriggered") || $(this).val() == "")
			{
				return;	
			}			
			
			var classes = $(this).attr('class').split(" ");
			var value = parseFloat($(this).val());
			
			var target = $(this).parent().find(".unit-text");
			if (target.length == 0) target = $(this).parent();

			if ($(this).parent().find(".validation-error").length == 0)
			if (isNaN(value) || !$.isNumeric($(this).val()) || !isFinite($(this).val()) || ($(this).hasClass("integer") && $(this).val().indexOf(".") > -1))
			{
				validationinfo += $(this).attr("name") + " (InvalidNumber) ";
				$(target).after("<div class='validation-error' aria-live='polite'>" + invalidnumberText + "</div>");
	 			result = false;
			} else {
			
				for ( var i = 0, l = classes.length; i<l; ++i ) {
				 	if (strStartsWith(classes[i], 'min'))
				 	{
				 		var min = classes[i].substring(3);
				 		if (value < parseFloat(min))
				 		{
				 			validationinfo += $(this).attr("name") + " (MinNumber) ";
				 			$(target).after("<div class='validation-error' aria-live='polite'>" + valuetoosmall + "</div>");
				 			result = false;
				 		}
				 	} else if (strStartsWith(classes[i], 'max'))
				 	{
				 		var max = classes[i].substring(3);
				 		if (value > parseFloat(max))
				 		{
				 			validationinfo += $(this).attr("name") + " (MaxNumber) ";
				 			$(target).after("<div class='validation-error' aria-live='polite'>" + valuetoolarge + "</div>");
				 			result = false;
				 		}
				 	} else if (strStartsWith(classes[i], 'prec'))
				 	{
				 		var precision = classes[i].substring(4);
				 		
				 		var parsedPrecision = parseInt(precision,10);
				 		var regEx;
				 		if (parsedPrecision > 0)
				 		{
				 			regEx = "^[-+]?[0-9]+((\\.[0-9]{1," + parsedPrecision + "})?)+$";
				 		
					 		var tester=new RegExp(regEx);
					 		
					 		if (!(tester.test($(this).val().trim())))
					 		{
					 			var pattern = /\{(\d+)\}/g;
	
					 			var precAlert = invalidPrecisionText.replace(pattern,function(match, key, value){
					 			    return parsedPrecision;
					 			});
					 			
					 			validationinfo += $(this).attr("name") + " (prec) ";
					 			$(target).after("<div class='validation-error'>" + precAlert + "</div>");
					 			result = false;
					 		}
				 		}
				 	}
				 	
				 	
				}				
			}
		});
		
		$(parent).find(".listbox").each(function(){
			
			//only if visible
			if ($(this).closest(".survey-element").hasClass("untriggered"))
			{
				return;	
			}
			
			var classes = $(this).attr('class').split(" ");
			var value = $(this).find(":checked").length;
			if ($(this).parent().find(".validation-error").length == 0)
			for ( var i = 0, l = classes.length; i<l; ++i ) {
			 	if (strStartsWith(classes[i], 'min'))
			 	{
			 		var min = classes[i].substring(3);
			 		if (value > 0 && value < parseInt(min))
			 		{
			 			validationinfo += $(this).closest(".survey-element").attr("id") + " (MinListbox) ";
			 			$(this).after("<div class='validation-error' aria-live='polite'>" + notenoughanswers + "</div>");
			 			result = false;
			 		}
			 	} else if (strStartsWith(classes[i], 'max'))
			 	{
			 		var max = classes[i].substring(3);
			 		if (value != 0 && value > parseInt(max))
			 		{
			 			validationinfo += $(this).closest(".survey-element").attr("id") + " (MaxListbox) ";
			 			$(this).after("<div class='validation-error' aria-live='polite'>" + toomanyanswers + "</div>");
			 			result = false;
			 		}
			 	}			 	
			}	
		});
		
		$(parent).find(".answer-columns").each(function(){
			
			//only if visible
			if ($(this).closest(".survey-element").hasClass("untriggered"))
			{
				return;	
			}
			
			if ($(this).find(".checkboxes").length > 0)
			{
				var classes = $(this).find(".checkboxes").first().attr('class').split(" ");
				var value = $(this).find(".checkboxes:checked").length;
				if ($(this).parent().find(".validation-error").length == 0)
				for ( var i = 0, l = classes.length; i<l; ++i ) {
				 	if (strStartsWith(classes[i], 'min'))
				 	{
				 		var min = classes[i].substring(3);
				 		if (value != 0 && value < parseFloat(min))
				 		{
				 			validationinfo += $(this).closest(".survey-element").attr("id") + " (MinAnswerCols) ";
				 			$(this).after("<div class='validation-error' aria-live='polite'>" + notenoughanswers + "</div>");
				 			result = false;
				 		}
				 	} else if (strStartsWith(classes[i], 'max'))
				 	{
				 		var max = classes[i].substring(3);
				 		if (value != 0 && value > parseFloat(max))
				 		{
				 			validationinfo += $(this).closest(".survey-element").attr("id") + " (MaxAnswerCols) ";
				 			$(this).after("<div class='validation-error' aria-live='polite'>" + toomanyanswers + "</div>");
				 			result = false;
				 		}
				 	}			 	
				}	
			
			}
		});
		
		return result;
	}
	
	function parseDate2(dateString)
	{
		if (dateString == null)
		{
			return null;
		}
		
		try {
			var parts = dateString.trim().split('/');
			
			if (parts.length != 3) return null;
			
			if (parts[2].length < 4) return null;
			
	 		var date = new Date(parts[2] + "-" + parts[1] + "-" + parts[0]);
	 			 		
	 		if (!isValidDate(date))
	 		{
	 			return null;
	 		}
	 		
	 		date.setHours(0);
	 		
	 		return date;
		} catch (e)
		{
			return null;
		} 		
	}
	
	function isValidDate(d) {
	  if ( Object.prototype.toString.call(d) !== "[object Date]" )
	    return false;
	  return !isNaN(d.getTime());
	}
	
	function parseMinMaxDate(s)
	{
		day = s.substring(4);
 		if (day.indexOf("0") == 0) day = day.substring(1);
 		month = s.substr(2,2);
 		if (month.indexOf("0") == 0) month = month.substring(1);
 		d = new Date(parseInt(day), parseInt(month)-1, parseInt(s.substr(0,2)));
 		return d;
	}
	
	function strStartsWith(str, prefix) {
	    return str.indexOf(prefix) === 0;
	}
	
	function S4() {
	   return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
	}

	function getNewId()
	{
		return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
	}
	
	function getNewSurveyId(username)
	{
		//return username + "_" + printDate();
		return getNewId();
	}
	
	function selectSurvey(id)
	{
		$("#export-survey-dialog-yes").attr("href",contextpath + "/noform/management/exportSurvey/true/" + id);
		$("#export-survey-dialog-no").attr("href",contextpath + "/noform/management/exportSurvey/false/" + id);
	}
	
	var archiveDialogOpenedByForms = false;
	function showArchiveDialog(shortname, id, fromforms)
	{
		archiveDialogOpenedByForms = fromforms;
		$('#archiveSurveyYesBtn').attr("href", contextpath + "/" + shortname + "/management/exportSurvey/true/" + shortname + "?delete=true&fromforms=" + fromforms)
		$('#archive-survey-dialog').modal();
	}
	
	var selectedId = '';
	function showDeleteDialog(id)
	{
		selectedId = id;
		$('#delete-survey-dialog').modal();
	}
	
	var selectedFreezeId = '';
	function showFreezeDialog(id, alias, url, title)
	{
		selectedFreezeId = id;
		
		var link = "<a href='" + url + "'>" + alias + "</a>";
		
		$('#freezeTitle').text(title);
		var text = $('#freeze-default-text').html();
		var surveydata = "<table><tr><td>Alias:</td><td>" + link + "</td></tr><tr><td>Survey name:&nbsp;&nbsp;</td><td>" + title + "</td></tr></table>";
		text = text.replace("[SURVEYDATA]", surveydata);
		$('#freezeEmailText').text(text);
		$('#freeze-survey-dialog').modal();
	}
	
	function freezeSurvey()
	{
		if (!$('#freezeCheck').is(":checked"))
		{
			$('#freezeCheckError').show();
			return;
		} else {
			$('#freezeCheckError').hide();
		}
		
		$("#freezeSurveyId").val(selectedFreezeId);
		$('#freeze-survey-dialog').modal("hide");
		$('#show-wait-image').modal('show');
		//$("#load-forms").attr("onsubmit","$('.tableFloatingHeader').empty(); $('.modal-backdrop').hide();");
		$("#freeze-form").submit();
	}
	
	function unfreezeSurvey(id)
	{
		$("#unfreezeSurveyId").val(id);
		$('#show-wait-image').modal('show');
		$("#unfreeze-form").submit();
	}
	
	function deleteSurvey()
	{
		$("#delete").val(selectedId);
		$('#delete-survey-dialog').modal("hide");
		$('#show-wait-image-delete-survey').modal('show');
		$("#load-forms").attr("onsubmit","$('.tableFloatingHeader').empty(); $('.modal-backdrop').hide();");
		$("#load-forms").submit();
	}
	
	function printDate() {
	    var temp = new Date();
	    var dateStr = padStr(temp.getFullYear()) +
	                  padStr(1 + temp.getMonth()) +
	                  padStr(temp.getDate()) + "_" +
	                  padStr(temp.getHours()) +
	                  padStr(temp.getMinutes()) +
	                  padStr(temp.getSeconds());
	    return dateStr;
	}

	function padStr(i) {
	    return (i < 10) ? "0" + i : "" + i;
	}
	
	function validateEmail(email) {
	   // var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
	   // var emailReg = /^([a-zA-Z0-9_\.\-'])+\@(([a-zA-Z0-9\-'])+\.)+([a-zA-Z0-9]{2,4})+$/;
	    
		var emailReg = /^[a-zA-Z0-9!#$%&'*+\/=?^_`{|}~-]+(?:\.[a-zA-Z0-9!#$%&'*+\/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?$/;
	    
	    if( !emailReg.test( email ) ) {
	        return false;
	    } else {
	        return true;
	    }
	}
	
	function validateRegExTemplate(pattern) {
		try { 
		    new RegExp(pattern);
		    return true;
		}
		catch(e) {
		    return false;
		}
	}
	
	function validateUrl(url) {
		
		 var urlregex = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
	     if( !urlregex.test( url ) ) {
	        return false;
	    } else {
	        return true;
	    }
	}
	
	function validateUUID(uuid) {
		
		 var urlregex = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;
	     if( !urlregex.test( uuid ) ) {
	        return false;
	    } else {
	        return true;
	    }
	}
	
	var setCombinedSecPriv = function(source,target)
	{
			//security/privacy val
			var secVal = $("input[name='" + source + "-security']:checked").val();
			var privVal = $("input[name='" + source + "-privacy']:checked").val();

			
			/* 
			 * hf:
			 * values are:
			 * open --> open
			 * secured --> secured
			 * anonymous --> anonymous
			 * identified --> ""
			 * 
			 * so state result is secVal + privVal
			 * 
			 */
			
			var finalVal = secVal + privVal;
			$(target).val(finalVal);
	};
	
	function createNewSurvey()
	{
		if (!validateInput($("#new-survey-table"))) return false;
		
		$("#create-survey-shortname").val($("#new-survey-shortname").val());
		$("#create-survey-language").val($("#new-survey-language").val());
		$("#create-survey-title").text($("#new-survey-title").html());
		
		setCombinedSecPriv("new-survey","#create-survey-security");
		
		$("#create-survey-audience").val($("input[name='radio-new-survey-audience']:checked").val());
		
		if ($("#new-survey-listform-true:checked").length > 0)
		{
			$("#create-survey-listform").val("true");
		} else {
			$("#create-survey-listform").val("false");
		}
		
		if ($("#new-survey-type-quiz:checked").length > 0)
		{
			$("#create-survey-quiz").val("true");
		} else {
			$("#create-survey-quiz").val("false");
		}
		
		if ($("#new-survey-type-opc:checked").length > 0)
		{
			$("#create-survey-opc").val("true");
		} else {
			$("#create-survey-opc").val("false");
		}		
		
		$("#create-survey-contact").val($("#new-survey-contact").val());
		$("#create-survey-contact-label").val($("#new-survey-contact-label").val());
		
		if (!checkShortname($("#new-survey-shortname").val()))
		{
			$("#new-survey-shortname-exists").show();
			return;
		}		
		
		$("#add-survey-dialog").modal("hide");
		$("#generic-wait-dialog").modal("show");
		
		$("#create-survey").submit();
	}
	
	var csrftoken = $("meta[name='_csrf']").attr("content");
	var csrfheader = $("meta[name='_csrf_header']").attr("content");
	
	function checkXHTMLValidity(s)
	{
		if (s == null || s.length == 0) return true;
		
		var result = false;
		$.ajax({
            type: "POST",
            contentType: "application/json",
            dataType: "json",
            data: s,			
			  url: contextpath + "/noform/management/checkXHTML",
			  dataType: "json",
			  async: false,
			  cache: false,
			  beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
			  success: function(data)
			  {
				  result = !data.invalid;
			  },
			  error: function(jqXHR, textStatus, errorThrown)
			  {
				  if (jqXHR != null && jqXHR.responseText.indexOf('Login') > 0)
				  {
					document.location = contextpath + '/auth/login?sessionexpired=true';
					result = true; //to avoid error message
				  } else {
					throw checkXHTMLValidityError;
				  }
			  }
			});
		
		return result;
	}
	
	function sanitize(str)
	{
		var d = document.createElement('div');
		d.appendChild(document.createTextNode(str));
		return d.innerHTML;		
	}
	
	var originalMargin = 0;
	var originalTop = 0;	
	
	//check if scrollbars required
	var isWindowScrolling = function() {
		
		var docHeight = $(document).height();
		var scroll    = $(window).height() + $(window).scrollTop();
		
		//hf: Fix for missing scrollbars in Editor
		var $object = $("#content","#big-edit-dialog");
		if($object.length) {
			return ($object.height() == scroll);
		}
		else
		{
			return (docHeight == scroll);
		}
		  
		
	};
	
	var applyScrolling = function() {
		applyModalMargin();
	};
	
	var needModalChange = function(el)
	{
		var modalHeight = $(el).height();
		var docHeight =  $(window).height();
		
		return (modalHeight > docHeight);
	};
	
	var applyModalMargin = function()
	{
		if($(".modal.in").length)
		{
			var $this = $(".modal.in");
			if(needModalChange($this))
			{
				var top = parseInt($(".modal.in").css("top"));
				var marginTop = parseInt($(".modal.in").css("margin-top"));
				
				if(top != 0)
				{
					if(marginTop != 0)
					{
						$this.data( "mresized", "true" );
						originalMargin = marginTop;
						originalTop = top;
						
						$this.css({
							"top": "0px",
							"margin-top": "0px"
						});
					}
				}
			}
			else
			{
				if($this.data( "mresized") == "true")
				{
					$this.css({
						"top": "50%",
						"margin-top": originalMargin
					});
					
					jQuery.removeData( $this, "mresized" );
				}
			}
		}
	};
	
	$("html").css("overflow","auto");
	
	//call handlers onload and onresize
	$(window).on("resize", 
			function() {
				applyScrolling();
	});
	$(document).ready(function() {
		applyScrolling();
		
		//call everytime a modal opens
		$("body").on("shown",".modal.in", function() {
			applyModalMargin();
		});
	});
	
	$(window).on("scroll", function(){
		$(".modal.in .tableFloatingHeaderOriginal").css("position","absolute");
	});
	
	var scrollTopBeforeOpen = 0;
	var isOpen = false;
	$(document).on("show",".modal", function(e) {
		if (!isOpen)
		{
			checkScrollTopNeeded();
			isOpen = true;
		}		
	});
	
	$(document).on("hide",".modal", function(e) {
		restoreScrollTopBeforeOpen();
		isOpen = false;
	});
	
	$(document).on('focusin', function(e) {
	    if ($(e.target).closest(".mce-window").length) {
	        e.stopImmediatePropagation();
	    }
	});
	
	function checkScrollTopNeeded()
	{
		var $container = $("body");
		scrollTopBeforeOpen = 0;
		if($container.scrollTop() > 0) {
			//not top
			scrollTopBeforeOpen = $container.scrollTop();
			$('html,body').animate({scrollTop: 0}, 10);
		} else {
			$container = $("html");
			if($container.scrollTop() > 0) {
				//not top
				scrollTopBeforeOpen = $container.scrollTop();
				$('html,body').animate({scrollTop: 0}, 10);
			}
		}
	}
	
	function restoreScrollTopBeforeOpen()
	{
		$('html,body').animate({scrollTop: scrollTopBeforeOpen}, 10);
	}	

	function modalResize(ui)
	{
	    $(ui.element).find(".modal-body").each(function() {
	      $(this).css("max-height", ui.size.height - 118 + "px");
	      $(this).css("height", ui.size.height - 118 + "px");
	      
	      $(this).find(".gallery-image").css("width", ui.size.width - 30 + "px");
	      
	      $(this).find(".dialogscroller:visible").css("max-height", ui.size.height - 127 - 370 + "px");
	      $(this).find(".dialogscroller:visible").css("max-width", ui.size.width - 90 + "px");
	      
	      $(this).find(".handsontable:visible").first().css("height", ui.size.height - 127 - 370 + "px");
	      $(this).find(".handsontable:visible").first().css("width", ui.size.width - 100 + "px");
	      
	      $(this).find("#search-results-div").first().css("height", ui.size.height - 187 - 200 + "px").css("max-height", ui.size.height - 187 - 200 + "px");
	      
	      $(this).find("#configure-logging-activities").first().css("max-height", ui.size.height - 127 - 100 + "px");
	      
	      $(this).find(".tinymce, .tinymcealign").each(function() {
	    	  if ($(this).hasClass("full"))
	    	  {
	    		  $(this).parent().find("iframe").css("height", ui.size.height - 325 + "px");
	    	  }
	      });
	      
	      $(this).find(".modal330").css("max-height", ui.size.height - 330 + "px");
	      $(this).find(".modal330").css("height", ui.size.height - 330 + "px");
	      
	      $(this).find(".modal250").css("max-height", ui.size.height - 250 + "px");
	      $(this).find(".modal250").css("height", ui.size.height - 250 + "px");
	      
	      $(this).find(".modal200").css("max-height", ui.size.height - 200 + "px");
	      $(this).find(".modal200").css("height", ui.size.height - 200 + "px");
	      
	      $(this).find(".modal150").css("max-height", ui.size.height - 150 + "px");
	      $(this).find(".modal150").css("height", ui.size.height - 150 + "px");
	      
	      $(this).find("#batch-attendee-dialog-step2-scrolldiv").css("height",ui.size.height - 127 - 100 + "px");
	      
	      $(this).find("#auto-load-content").css("max-width", ui.size.width - 400 + "px").css("height", ui.size.height - 290 + "px");
	      $(this).find("#auto-load-contentheader").css("max-width", ui.size.width - 400 + "px");
	      
	      $(this).find("#add-group-tree-div").css("height", ui.size.height - 190 + "px");
	    });
	}
	
	function removeIDs(html)
	{
		return html.replace(/id="(\w+)"/g,"");
	}
	
	function showOverlayMenu(btn)
	{
		closeOverlayDivsEnabled = false;
		var overlay = $(btn).parent().find('.overlaymenu').first();		
		
		if ($(overlay).is(":visible"))
		{
			$(overlay).hide();
			return;
		}
		
		$(".overlaymenu").hide();
		
		var rect = $(btn).parent().parent()[0].getBoundingClientRect();	
		
		$(overlay).css("left","0px");
		
		if ($(btn).closest(".widget").length > 0)
		{
			$(overlay).css("top",rect.top);
		}
		
		var realwidth = overlay.width();
		
			if (overlay.width() > $(btn).parent().parent().width() && $(btn).closest("th").is(':last-child'))
			{
				$(overlay).css("left", rect.right - realwidth - 11);	
			} else {
				$(overlay).css("left",rect.left);
				var newwidth = overlay.width();
				if (newwidth < realwidth)
				{
					//the div does not fit to the page, so we also right-align it
					$(overlay).css("left", rect.right - realwidth - 11);	
				};
			}
						
		$(overlay).toggle();
		$(btn).addClass("overlaybutton");
					
		setTimeout(function(){closeOverlayDivsEnabled = true;},1000);			
	}
	
	function hideOverlayMenu(btn)
	{
		var overlay = $(btn).parent().parent();			
		$(overlay).toggle();
		closeOverlayDivsEnabled = false;
	}	
	
	function checkAliasExistsForRestore(fromchangedialog) {
		
		var url = $("#confirm-restore-dialog-target").attr("href");
		var alias = '';
		if (!fromchangedialog)
		{
			alias = $("#confirm-restore-dialog-target").attr("data-alias");
		} else {
			
			if (!validateInput($('#choose-alias-dialog')))
			{
				return;
			}
			
			alias = $("#new-survey-shortname-restore").val();
		}			
		
		var s = "name=" + alias
		var result = $.ajax({
			type:'GET',
			  url: contextpath + "/forms/shortnameexistsjson",
			  dataType: 'json',
			  data: s,
			  cache: false,
			  async: false
		}).responseText;
				  
		$('#confirm-restore-dialog').modal('hide'); 
		 
		if (result == "true")
		{
			$("#new-survey-shortname-restore").val(getNewId());
			$('#choose-alias-dialog').modal('show');
		} else {
			
			$('#generic-wait-dialog').modal('show');
			if (!fromchangedialog)
			{
				window.location = url;
			} else {						
				window.location = url + "?alias=" + alias;
			}
		}
			
		return false;
	}
	
	function getSmallLetter(pcounter)
	{
		var counter = pcounter -1; //we start with 0 and not 1
		var prefix = counter / 26;
		counter = counter % 26;
		var sprefix = "";
		if (prefix > 0) sprefix = "abcdefghijklmnopqrstuvwxyz".substring(prefix-1,prefix);
		if (counter == 25)
		{
			return sprefix + "z";
		}
		return sprefix + "abcdefghijklmnopqrstuvwxyz".substring(counter,counter+1);			
	}
	
	function getBigLetter(pcounter)
	{
		var counter = pcounter -1; //we start with 0 and not 1
		var prefix = counter / 26;
		counter = counter % 26;
		var sprefix = "";
		if (prefix > 0) sprefix = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(prefix-1,prefix);
		if (counter == 25)
		{
			return sprefix + "Z";
		}
		return sprefix + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(counter,counter+1);				
	}