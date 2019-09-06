		var selectAll = true;
		// array used to avoid multiple opening of children 
		var openedChildren = [];

		$(function() {					
			$("#form-menu-tab").addClass("active");
			$("#participants-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
			
			$(document).keypress(function(event) {
			    var keycode = (event.keyCode ? event.keyCode : event.which);
			    if (keycode == '13') {
			        search(true,false);
			    }
			});
			
			$("#add-participants-type-ecas").val(selectedDomain);
			loadTopDepartments(selectedDomain);	
			
			$( "#sortable" ).sortable();
		    $( "#sortable" ).disableSelection();
			
			$( "#wheel" ).hide();
			
			$("#selectedAttributesSource").val("participants");
			
			var $col =$("#auto-load-content");
			
			$col.scroll(function(){
				if ($col.innerHeight() >= $col.prop('scrollHeight') - $col.scrollTop())
					moveTo('next','static'); 
			});
			
			$('.filtercell').keyup(function(e){
			    if(e.keyCode == 13){
			    	searchStatic(true,true);
			    }
			}) ;
			
			$(function() {				    	
		    	$(".dialog-wait-image").each(function(){
		    		var spinner = new Spinner().spin();
		    		$(this).append(spinner.el);	
		    	}) 		    	
		    });
			 
			 $("#auto-load-content").scroll(function(){
	    		$("#auto-load-contentheader").scrollLeft($("#auto-load-content").scrollLeft());	    		
	    	});
		});		
		
		function domainChaged()
		{
			loadTopDepartments($("#add-participants-type-ecas").val());
		}
		
		function loadTopDepartments(domain)
		{
			$( "#wheel" ).show();
			$("#tree").empty();			
			
			var selectedValue = domain.replace("eu.europa.","");
			var selectedText = $("#add-participants-type-ecas :selected").text();
			if (domain == "eu.europa.ec")
			{
				selectedText = "European Commission";
			}
			
			var li = document.createElement("li");
			var span = document.createElement("span");
			var link = document.createElement("a");
			var input = document.createElement("input");
			$(input).css("margin-left","10px").attr("onclick","disabledEventPropagation(event); checkChildren(this);").attr("type","checkbox").addClass("check").attr("name","rootnode"+selectedValue).val(selectedValue);
			
			if ($("#readonlytree").length > 0 && $("#readonlytree").val() == 'true')
			{
				$(input).attr("disabled", "disabled");
			}
			
			if ($.inArray("rootnode"+selectedValue, selectedDepartments) > -1)
			{
				$(input).prop("checked","checked");
			}
			
			$(link).attr("onclick","openChildren($(this).closest('li').find('ul').first(), '" + selectedValue + "')").html("<img class='folderimage' src='" + contextpath + "/resources/images/folderclosed.png'></img>");
			$(span).append(link).append(input).append(selectedText);
			$(li).append(span);
			var ulEC = document.createElement("ul");
			$(ulEC).addClass("dep-tree").addClass("dep-tree-child").hide();
			$(span).append(ulEC);
			$("#tree").append(li);
			
			$.ajax({
				type:'GET',
				  url: contextpath + "/noform/management/topDepartmentsJSON?domain=" +domain,
				  dataType: 'json',
				  success: function( list ) {
					  
				   for (var i = 0; i < list.length; i++ )
				  {
					var li = document.createElement("li");
					var span = document.createElement("span");
					
					$(span).attr("onclick","disabledEventPropagation(event);").attr("onselectstart","return false;").attr("id","span" + list[i].key);
					
					var input = document.createElement("input");
					$(input).css("margin-left","10px").attr("onclick","disabledEventPropagation(event); checkChildren(this);").attr("type","checkbox").addClass("check").attr("name","node" + list[i].key).val(list[i].key);
					
					if ($("#readonlytree").length > 0 && $("#readonlytree").val() == 'true')
					{
						$(input).attr("disabled", "disabled");
					}
					
					if ($.inArray(list[i].key, selectedDepartments) > -1)
					{
						$(input).prop("checked","checked");
					}
					
					if (list[i].value == '0')
					{
						//this means there are children
						var link = document.createElement("a");
						$(link).attr("onclick","openChildren($(this).closest('li').find('ul').first(), '" + list[i].key + "')").html("<img class='folderimage' src='" + contextpath + "/resources/images/folderclosed.png'></img>");
						$(span).append(link);
					} else {
						$(span).append("<img class='folderitemimage' src='" + contextpath + "/resources/images/folderitem.png' />");
					}
					
					$(span).append(input);
					$(span).append(list[i].key);
					
					$(li).append(span);
					
					if (list[i].value == '0')
					{
						//this means there are children
						var ul = document.createElement("ul");
						$(ul).addClass("dep-tree").addClass("dep-tree-child").hide();
						$(span).append(ul);
					}
					
					$(ulEC).append(li); 
				  }
				  // empty array
				  openedChildren.splice(0,openedChildren.length);
				  for (var i = 0; i < selectedDepartments.length; i++)
				  {
					recursiveOpenChildren(selectedDepartments[i]);  
				  }
				  //if (selectedDepartments.length == 0)
				  //{
					  openChildren($(".dep-tree-child").first(), selectedValue);
				  //}
				  				  
				  $( "#wheel" ).hide();
				  
				}});
		}
		
		function recursiveOpenChildren(child)
		{	
			var  dotPosition = child.indexOf("."); 
			var  dashPosition = child.indexOf("-");
			if (dotPosition == -1 && dashPosition == -1 )
			{
				$("input[name='node" + child + "']").each(function(){
					openChildren($(this).closest('li').find('ul').first(), child);
				});
			}else {
					var separators = ['\\\.','-'];
					var departments =  child.split( new RegExp(separators.join('|'), 'g') );
					var currentDeparement = "";
					 for (var i = 0; i < departments.length; i++)
					  {
						 currentDeparement = currentDeparement + departments[i] ;
						 if ($.inArray(currentDeparement, openedChildren) == -1)
						 {
							 $("input[name='node" + currentDeparement + "']").each(function(){
									openChildren($(this).closest('li').find('ul').first(), currentDeparement);
								});
							 
						 }
						 openedChildren.push(currentDeparement);
						 if (child.indexOf(currentDeparement + ".") > -1 ) {
							 currentDeparement = currentDeparement + ".";
						 }else if (child.indexOf(currentDeparement + "-") > -1 ) {
							 currentDeparement = currentDeparement + "-";
						 }
					  } 
				  }
		}
		
		function openChildren(targetul, department)
		{
			
			if ($(targetul).closest("span").find("a").first().find(".folderimage").length > 0)
			{
				//open
				if ($(targetul).children().length > 0)
				{
					$(targetul).show();
					  $(targetul).closest("span").find("a").first().find(".folderimage").each(function(){
							$(this).removeClass("folderimage").addClass("folderopenimage").attr("src",contextpath + "/resources/images/folderopen.png");
						});
				} else {
					$.ajax({
						type:'GET',
						  url: contextpath + "/noform/management/departmentsJSON",
						  data: {term:department},
						  dataType: 'json',
						  async: false,
						  success: function( list ) {
							  
						  var parentChecked = $(targetul).closest("span").find("input").first().is(":checked");
						  
						  for (var i = 0; i < list.length; i++ )
						  {
							var li = document.createElement("li");
							var span = document.createElement("span");
							
							$(span).attr("onclick","disabledEventPropagation(event);").attr("onselectstart","return false;").attr("id","span" + list[i].key);
							
							var input = document.createElement("input");
							$(input).css("margin-left","10px").attr("onclick","disabledEventPropagation(event); checkChildren(this);").attr("type","checkbox").addClass("check").attr("name","node" + list[i].key).val(list[i].key);
							
							if ($("#readonlytree").length > 0 && $("#readonlytree").val() == 'true')
							{
								$(input).attr("disabled", "disabled");
							}
							
							if (parentChecked || $.inArray(list[i].key, selectedDepartments) > -1)
							{
								$(input).prop("checked","checked");
							}
							
							if (list[i].value == '0')
							{
								//this means there are children
								var link = document.createElement("a");
								$(link).attr("onclick","openChildren($(this).closest('li').find('ul').first(), '" + list[i].key + "')").html("<img class='folderimage' src='" + contextpath + "/resources/images/folderclosed.png' />");
								$(span).append(link);
							} else {
								$(span).append("<img class='folderitemimage' src='" + contextpath + "/resources/images/folderitem.png' />");
							}
							
							$(span).append(input);
							$(span).append(list[i].key);
							
							$(li).append(span);
							
							if (list[i].value == '0')
							{
								//this means there are children
								var ul = document.createElement("ul");
								$(ul).addClass("dep-tree").addClass("dep-tree-child").hide();
								$(span).append(ul);
							}
							
							$(targetul).append(li);
							
					
							if (list[i].value == '0' && $.inArray(list[i].key, selectedDepartments) > -1)
							{
								openChildren($(span).closest('ul'), list[i].key);
							}
				
						  }
						  
						  $(targetul).closest("span").find("a").first().find(".folderimage").each(function(){
								$(this).removeClass("folderimage").addClass("folderopenimage").attr("src",contextpath + "/resources/images/folderopen.png");
							});

						  $(targetul).show();
						  $( "#wheel" ).hide();
						  
						}});
				}
			} else {
				//close
				$(targetul).hide();
				$(targetul).closest("span").find("a").first().find(".folderopenimage").each(function(){
					$(this).removeClass("folderopenimage").addClass("folderimage").attr("src",contextpath + "/resources/images/folderclosed.png");
				});
			}
			
			
		}
		
		var currentPage = '1';
		var totalItems;
		
		function cancelCreateGuestList()
		{
			$("#add-participants-dialog2-dynamic").modal('hide');
			$("#add-participants-dialog2-static").modal('hide');
			$("#add-participants-dialog2-departments").modal('hide');	
			$("#add-participants-dialog2-token").modal('hide');
			$("#add-participants-dialog1").modal('hide');	
			
			$("#add-participants-name").val("");
			$("#add-participants-type").val("static");
			$("#selectedparticipantsstatic").find("tbody").empty();
			$("#participantsstaticheader").find(".filtercell").find("input").each(function(){
				$(this).val("");
				checkFilterCell($(this).closest('.filtercell'), true);
			});
			
			var domainSelected;
			domainSelected=$("#add-user-type-ecas").val();
			if(domainSelected==null)
				domainSelected="eu.europa.ec";

			loadTopDepartments(domainSelected);
		}
				
		function step1()
		{			
			$("#add-participants-dialog2-dynamic").modal('hide');
			$("#add-participants-dialog2-static").modal('hide');
			$("#add-participants-dialog2-departments").modal('hide');
			$("#add-participants-dialog2-token").modal("hide");
			$("#add-participants-dialog1").modal();	
		}
		
		function step2()
		{			
			if (validateInput($("#add-participants-dialog1")))
			{			
				$("#add-participants-dialog1").modal('hide');		
				
				if ($("#add-participants-type").val() == 'dynamic')
				{
					$("#add-participants-dialog2-dynamic").modal();
					$("#add-wait-animation2").show();
					search(true, true);
				} else if ($("#add-participants-type").val() == 'static') {
					$("#add-participants-dialog2-static").modal();
					$("#add-wait-animation2-static").show();
					searchStatic(true, true);
				} else if ($("#add-participants-type").val() == 'tokens') {
					$("#add-participants-dialog2-token-groupname").val($("#add-participants-name").val());
					$("#add-participants-dialog2-token").modal();
				} else {
					$("#add-participants-dialog2-departments").modal();
					$("#add-participants-type-ecas").val("eu.europa.ec");
				}
				
			}
		}
		
		function showConfigure()
		{
			$("#add-participants-dialog2-dynamic").modal("hide");	
			$("#add-participants-dialog2-static").modal("hide");	
			$('#configure-attributes-dialog').modal();			
		}
		
		function cancelConfigure()
		{
			$("#configure-attributes-dialog").modal("hide");
			if ($("#add-participants-type").val() == 'dynamic')
			{
				$("#add-participants-dialog2-dynamic").modal();	
			} else if ($("#add-participants-type").val() == 'static') {
				$("#add-participants-dialog2-static").modal();				
			} else {
				$("#add-participants-dialog2-departments").modal();
			}
		}
		
		function moveTo(val, t)
		{
			if (val == 'first')
			{
				currentPage = 'first';
			} else if (val == 'previous')
			{
				currentPage = currentPage - 1;
			} else if (val == 'next')
			{
				currentPage = currentPage + 1;
			} else if (val == 'last')
			{
				currentPage = 'last';
			}
			
			if (t == 'static')
			{
				searchStatic(true, false);
			} else if (t == 'dynamic') {
				search(true, false);
			} 
		}
		
		function saveConfiguration()
		{
			var s = "selectedAttributesOrder=";
			
			$("#configure-attributes-dialog").find(".selectedattrib").each(function(){
				s += $(this).attr("name") + ";";
			});
			
			if ($('#owner').is(":checked"))
			{
				s = s + "&owner=selected";
			}
						
			$.ajax({
				type:'GET',
				  url: contextpath + "/addressbook/configureAttributesJSON",
				  data: s,
				  dataType: 'json',
				  cache: false,
				  success: function( list ) {

					var span;
					var input;					  
					  
					if ($("#add-participants-type").val() == 'dynamic')
					{
						$("#add-participants-dialog2-dynamic-filter").empty();						  
					} else {
						$("#add-participants-dialog2-static-filter").empty();
						$("#participantsstaticheader").find(".attribute").remove();
						$("#participantsstaticheader").find(".attributefilter").remove();
					}
											
					for (var i = 0; i < list.length; i++ )
					{
						span = document.createElement("span");
						$(span).addClass("filterspan");
						
						$(span).append(list[i].name);
						$(span).append("<br />");
						
						input = document.createElement("input");
						$(input).addClass("filter").attr("type","text").attr("name",list[i].id).attr("style","width:160px");
						
						$(span).append(input);
						
						if ($("#add-participants-type").val() == 'dynamic')
						{
							$("#add-participants-dialog2-dynamic-filter").append(span).append("<br />");						  
						} else {
							//update tables
							$("#participantsstaticheader").find("thead").each(function(){
								var th = document.createElement("th");
								$(th).addClass("attribute").attr("data-id", list[i].id).append(list[i].name);
								$(this).find("tr").first().append(th);
							});							
							
							$("#participantsstaticheader").find("thead").each(function(){
								th = document.createElement("th");
								$(th).addClass("filtercell").addClass("attributefilter");
								var input = document.createElement("input");
								$(input).attr("name",list[i].id).addClass("filter").attr("type","text").attr("placeholder", "filter").attr("style","margin: 0px;").attr("onkeyup", "checkFilterCell($(this).closest('.filtercell'), false)");
								$(th).append(input);
								$(this).find("tr").first().next().append(th);
							});
							
							$(th).keyup(function(e){
							    if(e.keyCode == 13){
							    	searchStatic(true,true);
							    }
							}) ;							
						}
					}
					
					$('#configure-attributes-dialog').modal("hide");
					$("#add-wait-animation2").hide();
					
					if ($("#add-participants-type").val() == 'dynamic')
					{
						search(true, true);
						$("#add-participants-dialog2-dynamic").modal();						  
					} else {
						searchStatic(true, true);						
						$("#add-participants-dialog2-static").modal();
												
						var $col =$("#auto-load-content");
						$col.scroll(function(){
						if ($col.innerHeight() == $col.prop('scrollHeight') - $col.scrollTop())
							moveTo('next','static'); 
						});
					}
					
				}});
		}
	
		
		function search(filter, reset)
		{
			if (reset) currentPage = '1';
			
			var s = "";
			
			if (filter)
			{
				s = "name=" + $("#namefilter").val() + "&email=" + $("#emailfilter").val() + "&newPage=" + currentPage;
				
				$(".filter").each(function(){
					s += "&" + $(this).attr("name") + "=" + $(this).val();
				});
			} else {
				$("#emailfilter").val("");
				$("#namefilter").val("");
				$(".filter").each(function(){
					 $(this).val("");
				});
			}
						
			$.ajax({
				type:'GET',
				  url: contextpath + "/noform/management/participantsJSON",
				  data: s,
				  dataType: 'json',
				  cache: false,
				  success: function( paging ) {
				  
				  $("#participants").find("tbody").find("tr").each(function(){$(this).remove();});
				  
				  for (var i = 0; i < paging.items.length; i++ )
				  {
					var tr = document.createElement("tr");
					var td = document.createElement("td");
					$(td).html(sanitize(paging.items[i].name));
					$(tr).append(td);
					td = document.createElement("td");
					$(td).html(paging.items[i].email);
					$(tr).append(td);				
					
					for (var j = 0; j < attributeNames.length; j++ )
					{
						td = document.createElement("td");
						
						for (var k = 0; k < paging.items[i].attributes.length; k++)
						{
							if (paging.items[i].attributes[k].attributeName.id == attributeNames[j])
							{
								$(td).html(paging.items[i].attributes[k].value);
								break;
							}
						}
						$(tr).append(td);
					}
					
					$("#participants").find("tbody").append(tr);
					
				  }
				  
				  $("#firstResult").text(paging.firstItemOnPage);
				  $("#lastResult").text(paging.lastItemOnPage);
				  $("#totalResults").text(paging.numberOfItems);
				  
				  currentPage = paging.currentPage;
				  totalItems = paging.numberOfItems;
				  
				  if (paging.currentPage == 1)
				  {
					  $("#btnFirst").addClass("disabled");
					  $("#btnPrevious").addClass("disabled");
				  } else {
					  $("#btnFirst").removeClass("disabled");
					  $("#btnPrevious").removeClass("disabled");
				  }
				  
				  if (paging.lastItemOnPage == paging.numberOfItems)
				  {
					  $("#btnLast").addClass("disabled");
					  $("#btnNext").addClass("disabled");
				  } else {
					  $("#btnLast").removeClass("disabled");
					  $("#btnNext").removeClass("disabled");
				  }
				  
				  $("#add-wait-animation2").hide();
				  
				}});
		}
		
		function removeContactFilter()
		{
			$(".glyphicon-remove-circle").each(function(){
				clearFilterCellContent($(this).parent());
			});
			$(".filtercell").each(function(){
				checkFilterCell(this, true);
			});
		}
		
		var lastStaticFilter = "";
		function searchStatic(filter, reset)
		{
			if (reset)
			{
				currentPage = '1';
			}
			
			var s = "";
			var i = 2;
			var header = $("#participantsstaticheader").first();
			
			if (filter)
			{
				s = "name=" + header.find("#namefilterstatic").val() + "&email=" + header.find("#emailfilterstatic").val() + "&newPage=" + currentPage + "&itemsPerPage=25";
				
				header.find(".filter").each(function(){
					s += "&" + $(this).attr("name") + "=" + $(this).val();
					i++;
				});
			} else {
				$("#emailfilterstatic").val("");
				$("#namefilterstatic").val("");
				$("#add-participants-dialog2-static").find(".filter").each(function(){
					 $(this).val("");
					 i++;
				});
				s = "itemsPerPage=25";
			}
			
			lastStaticFilter = s;
			
			$("#participantsstaticheader").css("width", (20 + i * 150) + "px");
			$("#participantsstatic").css("width", $("#participantsstaticheader").css("width"));
			
			$(".dialog-wait-image").show();
			
			$.ajax({
				type:'GET',
				  url: contextpath + "/noform/management/participantsJSON",
				  data: s,
				  dataType: 'json',
				  cache: false,
				  error: function() {
					  $(".dialog-wait-image").hide();
				  },
				  success: function( paging ) {
				  
				  if (reset)
				  {
					  $("#participantsstatic").find("tbody").find("tr").each(function(){$(this).remove();});
					  $("#participantsstaticheader").find("input").first().attr("checked", "checked");
				  }
				  
				  for (var i = 0; i < paging.items.length; i++ )
				  {
					  	var tr = document.createElement("tr");
						var td = document.createElement("td");
						var check = document.createElement("input");
						$(check).attr("type","checkbox").attr("data-id",paging.items[i].id).addClass("check").addClass("search-result").attr("id","searched-" + paging.items[i].id);
						
						if(selectAll){
							$(check).attr("checked", "checked");
						}					
						
						$(check).attr("onclick","$('.select-all-searched').removeAttr('checked')");
						$(td).css("width","20px").append(check);
						$(tr).append(td);						
						
						td = document.createElement("td");
						$(td).html(sanitize(paging.items[i].name));
						$(tr).append(td);
						
						td = document.createElement("td");
						$(td).html(paging.items[i].email);
						$(tr).append(td);
						
						$("#participantsstaticheader").find("thead").first().find(".attribute").each(function(){
							var id = $(this).attr("data-id");
							var name = $(this).text();
							
							td = document.createElement("td");
							
							if (name == "Owner")
							{
								$(td).html(paging.items[i].owner);
							} else {							
								var found = false;
								for (var j = 0; j < paging.items[i].attributes.length; j++)
								{
									if (paging.items[i].attributes[j].attributeName.id == parseInt(id))
									{
										$(td).html(paging.items[i].attributes[j].value);
										found = true;
										break;
									}
								}
								
								if (!found)
								{
									for (var j = 0; j < paging.items[i].attributes.length; j++)
									{
										if (paging.items[i].attributes[j].attributeName.name == name)
										{
											$(td).html(paging.items[i].attributes[j].value);
											break;
										}
									}
								}
							}
							
							$(tr).append(td);
						});
					
					$("#participantsstatic").find("tbody").append(tr);
					
				  }
				  
				  $("#firstResultStatic").text(paging.firstItemOnPage);
				  $("#lastResultStatic").text(paging.lastItemOnPage);
				  $("#totalResultsStatic").text(paging.numberOfItems);
				  
				  currentPage = paging.currentPage;
				  totalItems = paging.numberOfItems;
				  
				  if (paging.currentPage == 1)
				  {
					  $("#btnFirstStatic").addClass("disabled");
					  $("#btnPreviousStatic").addClass("disabled");
				  } else {
					  $("#btnFirstStatic").removeClass("disabled");
					  $("#btnPreviousStatic").removeClass("disabled");
				  }
				  
				  if (paging.lastItemOnPage == paging.numberOfItems)
				  {
					  $("#btnLastStatic").addClass("disabled");
					  $("#btnNextStatic").addClass("disabled");
				  } else {
					  $("#btnLastStatic").removeClass("disabled");
					  $("#btnNextStatic").removeClass("disabled");
				  }
				  
				  $("#add-wait-animation2-static").hide();
				  				  
				  $(".dialog-wait-image").hide();
				  
				}});
			
			$(".filtercell").each(function(){
				checkFilterCell(this, true);
			});
		}
		
		function save()
		{
			$("#saveForm").find("input").each(function(){ $(this).remove(); });
			
			addHiddenField("groupName", $("#add-participants-name").val());
			addHiddenField("groupOwner", $("#add-participants-owner").val());
			addHiddenField("groupType", $("#add-participants-type").val());
			addHiddenField("name", $("#namefilter").val());
			addHiddenField("email", $("#emailfilter").val());
			
			$("#add-participants-dialog2-dynamic").find(".filter").each(function(){
				addHiddenField($(this).attr("name"), $(this).val());
			});
			
			if ($("#selectedParticipationGroup").length > 0)
			{
				addHiddenField("id", $("#selectedParticipationGroup").val());
			}
			
			$("#generic-wait-dialog").modal("show");
			$("#saveForm").submit();		
		}
		
		function saveTokens()
		{
			$("#add-participants-dialog2-token").modal("hide");
			$("#generic-wait-dialog").modal("show");
			$('#add-participants-dialog2-token-form').submit();
		}
		
		function saveStatic()
		{
			$("#saveFormStatic").find("input").each(function(){ 
				if ($(this).attr("name") != "_csrf")
				{
					$(this).remove(); 					
				}
			});
			
			addHiddenFieldStatic("groupName", $("#add-participants-name").val());
			addHiddenFieldStatic("groupOwner", $("#add-participants-owner").val());
			addHiddenFieldStatic("groupType", $("#add-participants-type").val());
			
			$("#selectedparticipantsstatic").find("tbody").find("input").each(function(){
				addHiddenFieldStatic("att" + $(this).attr("name"), $(this).attr("name"));
			});
			
			if ($("#selectedParticipationGroup").length > 0)
			{
				addHiddenFieldStatic("id", $("#selectedParticipationGroup").val());
			}
			
			$("#add-participants-dialog2-static").modal("hide");
			$("#generic-wait-dialog").modal("show");
			$("#saveFormStatic").submit();		
		}
		
		function saveDepartments()
		{
			addHiddenFieldDepartments("groupName", $("#add-participants-name").val());
			addHiddenFieldDepartments("groupOwner", $("#add-participants-owner").val());
			addHiddenFieldDepartments("groupType", $("#add-participants-type").val());
					
			if ($("#selectedParticipationGroup").length > 0)
			{
				addHiddenFieldDepartments("id", $("#selectedParticipationGroup").val());
			}
			
			addHiddenFieldDepartments("domain", $("#add-participants-type-ecas").val());

			$("#add-participants-dialog2-departments").modal("hide");
			$("#generic-wait-dialog").modal("show");
			$("#saveFormDepartments").submit();		
		}
		
		function addHiddenField(name, value)
		{
			var input = document.createElement("input");
			$(input).attr("type","hidden").attr("name",name).val(value);
			$("#saveForm").append(input);
		}
		
		function addHiddenFieldStatic(name, value)
		{
			var input = document.createElement("input");
			$(input).attr("type","hidden").attr("name",name).val(value);
			$("#saveFormStatic").append(input);
		}
		
		function addHiddenFieldDepartments(name, value)
		{
			var input = document.createElement("input");
			$(input).attr("type","hidden").attr("name",name).val(value);
			$("#saveFormDepartments").append(input);
		}
		
		function selectAttendees()
		{
			$("#participantsstatic").find("tbody").find("input:checked").each(function(){
				var id = $(this).attr("name");
				
				if ($("#selectedparticipantsstatic").find("input[name='" + id + "']").length == 0)
				{
					var tr = $(this).parent().parent().clone();
					$("#selectedparticipantsstatic").find("tbody").append(tr);
				};
				
			});
			
			$("#participantsstaticheader").find("input:checked").removeAttr("checked");
			$("#selectedparticipantsstatic").find("input:checked").removeAttr("checked");
		}
		
		function selectAllAttendees()
		{
			
			if (selectAll)
			{
				//take all except those that are unchecked				
				$.ajax({
					type:'GET',
					  url: contextpath + "/noform/management/participantsJSONAll",
					  data: lastStaticFilter,
					  dataType: 'json',
					  cache: false,
					  success: function( paging ) {
					  
					  for (var i = 0; i < paging.items.length; i++ )
					  {
						  var id = paging.items[i].id;
						  
						  if ($("#searched-" + id).length == 0 || $("#searched-" + id).first().is(":checked"))
						  {
							if ($("#selectedparticipantsstatic").find("input[name='" + id + "']").length == 0)
							{
								var tr = document.createElement("tr");
								var td = document.createElement("td");
								var input = document.createElement("input");
								$(input).attr("type","checkbox").attr("name",paging.items[i].id).val("true");
								$(td).append(input);
								$(tr).append(td);
								
								td = document.createElement("td");
								$(td).html(sanitize(paging.items[i].name + " (" + paging.items[i].email + ")"));
								$(tr).append(td);
								$("#selectedparticipantsstatic").find("tbody").append(tr);
							};
						  }
					  };  
					 				  
					}
				});
				
			} else {
				//take only checked
				$(".search-result").each(function(){
					if ($(this).is(":checked"))
					{
						var id = $(this).attr("data-id");
						if ($("#selectedparticipantsstatic").find("input[name='" + id + "']").length == 0)
						{
							var tr = document.createElement("tr");
							var td = document.createElement("td");
							var input = document.createElement("input");
							$(input).attr("type","checkbox").attr("name",id).val("true");
							$(td).append(input);
							$(tr).append(td);
							
							td = document.createElement("td");
							
							var name = $(this).parent().parent().find("td")[1];
							var email = $(this).parent().parent().find("td")[2];
							
							$(td).html(sanitize($(name).text() + " (" + $(email).text() + ")"));
							$(tr).append(td);
							$("#selectedparticipantsstatic").find("tbody").append(tr);
						};
					}
				});
			}
		}
		
		function removeAttendees()
		{
			$("#selectedparticipantsstatic").find("tbody").find("input:checked").each(function(){
				var tr = $(this).parent().parent();
				$(tr).remove();				
			});
		}
		
		function removeAllAttendees()
		{
			$("#selectedparticipantsstatic").find("tbody").find("tr").each(function(){
				$(this).remove();				
			});
		}

		function checkCheckedAttendees(checkbox)
		{
			if ($(checkbox).is(":checked"))
			{
				$("#participantsstatic").find("tbody").find("input").prop("checked","checked");
			} else {
				$("#participantsstatic").find("tbody").find("input").removeAttr("checked");
			}
		}
		
		function checkCheckedSelectedAttendees(checkbox)
		{
			if ($(checkbox).is(":checked"))
			{
				$("#selectedparticipantsstatic").find("tbody").find("input").prop("checked","checked");
			} else {
				$("#selectedparticipantsstatic").find("tbody").find("input").removeAttr("checked");
			}
		}
		
		function checkCheckedSearchedAttendees(checkbox)
		{
			if ($(checkbox).is(":checked"))
			{
				selectAll = true;
				$("#participantsstatic").find("tbody").find("input").prop("checked","checked");
			} else {
				selectAll = false;
				$("#participantsstatic").find("tbody").find("input").removeAttr("checked");
			}
		}
		
//DEPARTMENTS
		
	function clearSelection() {
		var sel;
		if(document.selection && document.selection.empty){
			document.selection.empty();
		} else if(window.getSelection) {
			sel=window.getSelection();
			if(sel && sel.removeAllRanges)
				sel.removeAllRanges();
		};
	};
	
	(function($){
		
		$.fn.disableSelection = function() {
		    return this.each(function() {           
		        $(this).attr('unselectable', 'on')
		               .css({
		                   '-moz-user-select':'none',
		                   '-webkit-user-select':'none',
		                   'user-select':'none'
		               })
		               .each(function() {
		                   this.onselectstart = function() { return false; };
		               });
		    });
		};
		
	})(jQuery);
	
	function setValue(value)
	{
		$("#add-participants-departments").val(value);
		$('span[id^="span"]').removeClass("highlighted");						
		if (value.length > 0)
		{					
			$('span[id="span' + value + '"]').addClass("highlighted");
		}

		clearSelection();
		
		$( "#wheel" ).hide();
	}
	
	function removeHighlighting()
	{
		$('span[id^="span"]').removeClass("highlighted");
	}
	
	function checkChildren(check)
	{
		if ($(check).is(":checked"))
		{
			$(check).parent().parent().find("input").prop("checked", "checked");
			if ($(check).parent().find("a").first().find(".folderimage").length > 0)
			{
				$(check).parent().find("a").first().click(); //this also opens the element in case it is closed
			}			
		} else {
			$(check).parent().parent().find("input").removeAttr("checked");
			
			//uncheck parents
			var li = $(check).parent().parent();
			var ul = $(li).closest("ul");
			while ($(ul).hasClass("dep-tree-child"))
			{
				li = $(ul).closest("li");
				$(li).find("input").first().removeAttr("checked");
				ul = $(li).closest("ul");
			}
			
		}
	}
	
	function openParents()
	{
		$("#DemoTree").find("input:checked").each(function(){
			var ul = $(this).parent().parent().parent();			
			ddtreemenu.expandSubTree("DemoTree", ul[0]);
		});
	}		