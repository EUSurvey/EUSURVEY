	$(function() {					
			$( "#wheel" ).hide();
			$("#settings-menu-tab").addClass("active");
			
			$(".filtercell").each(function(){
				checkFilterCell(this, true);
			});
			
			var $col =$("#auto-load-content");
			
			$col.scroll(function(){
				if ($col.innerHeight() >= $col.prop('scrollHeight') - $col.scrollTop())	moveTo('next','static'); 
			});
			
			$('.filtercell').keyup(function(e){
			    if(e.keyCode == 13){
			    	searchStatic(true,true, false);
			    }
			});
			
	    	$(".dialog-wait-image").each(function(){
	    		var spinner = new Spinner().spin();
	    		$(this).append(spinner.el);	
	    	}); 	
	
	    	$("#auto-load-content").scroll(function(){
	    		$("#auto-load-contentheader").scrollLeft($("#auto-load-content").scrollLeft());	    		
	    	});
		});			
		
		var currentPage = '1';
		var totalItems;
		var selectAll = true;
		
		function step1()
		{			
			$("#add-share-dialog2-static").modal('hide');
			$("#add-share-dialog1").modal();						
			//$("#add-share-dialog2-dynamic").modal('hide');			
		}
		
		function step2()
		{			
			if (validateInput($("#add-share-dialog1")))
			{			
				$("#add-share-dialog1").modal('hide');						
				$("#add-share-dialog2-static").modal();
				$("#add-wait-animation2-static").show();
				searchStatic(true, true, false);		
			}
		}
		
		function step2from3() {
			$("#add-share-dialog3-static").modal("hide");
			$("#add-share-dialog2-static").modal("show");
		}
		
		function step3()
		{
			$("#add-share-dialog2-static").modal('hide');
			$("#add-share-dialog3-static").modal();
			$("#add-share-dialog3-error").hide();
		}
		
		function showConfigure()
		{
			$("#add-share-dialog2-static").modal("hide");	
			$('#configure-attributes-dialog').modal();
			//$("#add-share-dialog2-dynamic").modal("hide");	
		}
		
		function cancelConfigure()
		{
			$("#configure-attributes-dialog").modal("hide");
			if ($("#add-share-type").val() == 'dynamic')
			{
				$("#add-share-dialog2-dynamic").modal();	
			} else if ($("#add-share-type").val() == 'static') {
				$("#add-share-dialog2-static").modal();
			} else {
				$("#add-share-dialog2-departments").modal();
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
				searchStatic(true, false, false);
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

			let ownerElement = document.querySelector("#configure-attributes-dialog #selectedDiv #owner");
			let ownerSelected = false
			if (ownerElement && ownerElement.checked) {
				s += "&owner=selected"
				ownerSelected = true
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
					  
					if ($("#add-share-type").val() == 'dynamic')
					{
						$("#add-share-dialog2-dynamic-filter").empty();						  
					} else {
						$("#add-share-dialog2-static-filter").empty();
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
						
						if ($("#add-share-type").val() == 'dynamic')
						{
							$("#add-share-dialog2-dynamic-filter").append(span).append("<br />");						  
						} else {
							//update tables
							var th = document.createElement("th");
							$(th).addClass("attribute").attr("data-id", list[i].id).append(list[i].name);
							$("#participantsstaticheader").find("thead").find("tr").first().append(th);
							
							th = document.createElement("th");
							$(th).addClass("filtercell").addClass("attributefilter");
							var input = document.createElement("input");
							$(input).attr("name",list[i].name == "Owner" ? "owner" : list[i].id).addClass("filter").attr("type","text").attr("placeholder", "filter").attr("style","margin: 0px;").attr("onkeyup", "checkFilterCell($(this).closest('.filtercell'), false)");
							$(th).append(input);
							$("#participantsstaticheader").find("thead").find("tr").first().next().append(th);
							
							$(th).keyup(function(e){
							    if(e.keyCode == 13){
							    	searchStatic(true,true,false);
							    }
							}) ;
						}						
						
					}
					
					$('#configure-attributes-dialog').modal("hide");
					
					if ($("#add-share-type").val() == 'dynamic')
					{
						search(true, true);
						$("#add-share-dialog2-dynamic").modal();						  
					} else {
						$("#add-share-dialog2-static").modal();	
						searchStatic(true, true, true);
					}					
					
					$("#add-wait-animation2").hide();
				  
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
					$(td).html(paging.items[i].name);
					$(tr).append(td);
					td = document.createElement("td");
					$(td).html(paging.items[i].email);
					$(tr).append(td);					
					
//					for (var j = 0; j < paging.items[i].attributes.length; j++ )
//					{
//						td = document.createElement("td");
//						$(td).html(paging.items[i].attributes[j].value);
//						$(tr).append(td);
//					}					
					
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
		
		var lastStaticFilter = "";
		function searchStatic(filter, reset, afterconfigure)
		{
			if (reset) currentPage = '1';
			
			var s = "";
			var i = 2;
			
			if (filter)
			{
				s = "name=" + $("#namefilterstatic").first().val() + "&email=" + $("#emailfilterstatic").first().val() + "&newPage=" + currentPage + "&itemsPerPage=25";
				
				$("#add-share-dialog2-static").find(".filter").each(function(){
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
			
			$("#participantsstaticheader").css("width", (20 + i * 150) + "px");
			$("#participantsstatic").css("width", $("#participantsstaticheader").css("width"));
					
			lastStaticFilter = s;
			
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
				  
				  if (reset) $("#participantsstatic").find("tbody").find("tr").each(function(){$(this).remove();});
				  
				  for (var i = 0; i < paging.items.length; i++ )
				  {
					var tr = document.createElement("tr");
					var td = document.createElement("td");
					
					var check = document.createElement("input");
					$(check).attr("type","checkbox").attr("data-id",paging.items[i].id).addClass("check").addClass("search-result").attr("id","searched-" + paging.items[i].id);
					
					if(selectAll){
						$(check).attr("checked", "checked");
					}					
					
					$(check).attr("onclick","$('.select-all-searched').removeAttr('checked'); selectAll = false;");
					$(td).css("width", "20px").append(check);
					$(tr).append(td);		
					
					td = document.createElement("td");
					$(td).html(paging.items[i].name.stripHtml());
					$(tr).append(td);
					
					td = document.createElement("td");
					$(td).html(paging.items[i].email.stripHtml());
					$(tr).append(td);
					
					$("#participantsstaticheader").find("thead").first().find(".attribute").each(function(){
						if ($(this).text() === "Owner") {
							td = document.createElement("td");
							$(td).text(paging.items[i].owner);
						} else {
							var id = $(this).attr("data-id");

							td = document.createElement("td");

							for (var j = 0; j < paging.items[i].attributes.length; j++)
							{
								if (paging.items[i].attributes[j].attributeName.id == parseInt(id))
								{
									if (paging.items[i].attributes[j].value != null)
									{
										$(td).html(paging.items[i].attributes[j].value.stripHtml());
									}
									break;
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
		}
			
		function checkRecipientAndSubmit()
		{			
			var recipient = $("#recipientname").val();
			
			if (recipient == null || recipient.trim().length == 0)
			{
				$("#add-share-dialog3-error").show();
				return;
			}
						
			$.ajax({
				type:'GET',
				  url: contextpath + "/settings/userExists",
				  data: "login=" + recipient,
				  dataType: 'json',
				  cache: false,
				  error: function() {
					showError(usersTooOftenShares);
				  },
				  success: function( exists ) {
					  
					  if (exists == true)
					  {
						  saveStatic();
					  } else {
						  $("#add-share-dialog3-error").show();
						  return;
					  }
				  
				}});
		}
		
		function saveStatic()
		{
			//$("#saveFormStatic").find("input").each(function(){ $(this).remove(); });
			
			addHiddenFieldStatic("shareName", $("#add-share-name").val());
			addHiddenFieldStatic("shareMode", $("#add-share-mode").val());
			addHiddenFieldStatic("recipientName", $("#recipientname").val());
			
			$("#selectedparticipantsstatic").find("tbody").find("input").each(function(){
				addHiddenFieldStatic("att" + $(this).attr("name"), $(this).attr("name"));
			});
			
			if ($("#selectedParticipationGroup").length > 0)
			{
				addHiddenFieldStatic("id", $("#selectedParticipationGroup").val());
			}
			
			$("#saveFormStatic").submit();		
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
						  if ($("#selectedparticipantsstatic").find("input[name='" + id + "']").length == 0)
							{
							  var tr = document.createElement("tr");
								var td = document.createElement("td");
								var input = document.createElement("input");
								$(input).attr("type","checkbox").attr("name",paging.items[i].id).val("true");
								$(td).append(input);
								$(tr).append(td);
								
								td = document.createElement("td");
								$(td).css("max-width","180px").html(paging.items[i].name.stripHtml() + " (" + paging.items[i].email.stripHtml() + ")");
								$(tr).append(td);
								$("#selectedparticipantsstatic").find("tbody").append(tr);
							};
						
					  };  
					  
						$("#participantsstatic").width("100%");
						$("#participantsstaticheader").width("100%");		  
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
							
							$(td).css("max-width","180px").html(sanitize($(name).text() + " (" + $(email).text() + ")"));
							$(tr).append(td);
							$("#selectedparticipantsstatic").find("tbody").append(tr);
						};
					}
				});
				
				$("#participantsstatic").width("100%");
				$("#participantsstaticheader").width("100%");
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
		
		var selectedId = '';
		function showDeleteDialog(id)
		{
			selectedId = id;
			$('#delete-share-dialog').modal();
		}
		
		function deleteShare()
		{
			$("#delete").val(selectedId);
			$("#load-shares").submit();
		}
		