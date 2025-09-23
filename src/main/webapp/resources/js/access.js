	var scrollTimeout = null;
	
		$(function() {					
			$("#form-menu-tab").addClass("active");
			$("#access-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
						
			checkUserType();	
			
			$('#add-user-name').keyup(function(e){
			    if(e.keyCode == 13){
			    	searchUser();
			    }
			});
			
			$('#add-department-name').keyup(function(e){
			    if(e.keyCode == 13){
			    	searchUser();
			    }
			});
			
			$('#add-group-name').keyup(function(e){
			    if(e.keyCode == 13){
			    	addGroup();
			    	$('#add-group-dialog').modal("hide");
			    }
			});
			
			$("input[type='text']").placeholder();
			
			loadResultAccesses("date");
			
			$('#resacc-user-name').keyup(function(e){
			    if(e.keyCode == 13){
			    	firstPage();
			    }
			});
			
			$('#resacc-user-email').keyup(function(e){
			    if(e.keyCode == 13){
			    	firstPage();
			    }
			});
		});
		
		function openEntities(targetul, isDGs) {
			if ($(targetul).closest("span").find("a").first().find(".folderimage").length > 0)
			{
				//open
				if ($(targetul).children().length > 0)
				{
					$(targetul).show();
					$(targetul).closest("span").find("a").first().find(".folderimage").each(function(){
					  $(this).removeClass("folderimage").addClass("folderopenimage").attr("src", contextpath + "/resources/images/folderopen.png");
					});
				} else {
					$( "#wheel" ).show();
					$.ajax({
						type:'GET',
						  url: contextpath + "/noform/management/departmentsJSON",
						  data: {term: isDGs ? "dgs" : "aex", isdgs: isDGs},
						  dataType: 'json',
						  success: function( list ) {
							  
						  for (var i = 0; i < list.length; i++ )
						  {
							var li = document.createElement("li");
							var span = document.createElement("span");
							
							$(span).attr("onclick","disabledEventPropagation(event);").attr("onselectstart","return false;").attr("id","span" + list[i].key);
							
							var input = document.createElement("input");
							$(input).css("margin-left","10px").attr("onclick","disabledEventPropagation(event);").attr("type","radio").addClass("check").attr("name","department").val(list[i].key);
							
							if ($("#readonlytree").length > 0 && $("#readonlytree").val() == 'true')
							{
								$(input).attr("disabled", "disabled");
							}
							
							if (list[i].value == '0')
							{
								//this means there are children
								var link = document.createElement("a");
								$(link).attr("onclick","openChildren($(this).closest('li').find('ul').first(), '" + list[i].key + "'," + isDGs + ")").html("<img class='folderimage' src='" + contextpath + "/resources/images/folderclosed.png'></img>");
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
						  }
						  
						  $(targetul).closest("span").find("a").first().find(".folderimage").each(function(){
								$(this).removeClass("folderimage").addClass("folderopenimage").attr("src", contextpath + "/resources/images/folderopen.png");
							});
						  					  
						  
						  $(targetul).show();
						  $( "#wheel" ).hide();
						  
						}});
				}
			} else {
				//close
				$(targetul).hide();
				$(targetul).closest("span").find("a").first().find(".folderopenimage").each(function(){
					$(this).removeClass("folderopenimage").addClass("folderimage").attr("src", contextpath + "/resources/images/folderclosed.png");
				});
			}
		}
		
		function loadTopDepartments(domain)
		{
			if (domain != "eu.europa.ec") {
				
			} else {
				
			}
		}
		
		function recursiveOpenChildren(child, globalprefix)
		{
			if (child.indexOf(".") > -1)
			{
				var prefix = child.substring(0, child.indexOf("."));
				
				$("input[name='node" + globalprefix + prefix + "']").each(function(){
					if ($(this).parent().find(".folderopenimage").length == 0)
					{
						 openChildren($(this).closest("li").find('ul'), $(this).val());
					}
				});
				recursiveOpenChildren(child.substring(child.indexOf(".")+1), globalprefix + prefix + ".");
			}
		}
		
		function openChildren(targetul, department, isDGs)
		{			
			if ($(targetul).closest("span").find("a").first().find(".folderimage").length > 0)
			{
				//open
				if ($(targetul).children().length > 0)
				{
					$(targetul).show();
					  $(targetul).closest("span").find("a").first().find(".folderimage").each(function(){
						  $(this).removeClass("folderimage").addClass("folderopenimage").attr("src", contextpath + "/resources/images/folderopen.png");
						});
				} else {
					$.ajax({
						type:'GET',
						  url: contextpath + "/noform/management/departmentsJSON",
						  data: {term:department, isdgs:isDGs},
						  dataType: 'json',
						  success: function( list ) {
							  
						  for (var i = 0; i < list.length; i++ )
						  {
							var li = document.createElement("li");
							var span = document.createElement("span");
							
							$(span).attr("onclick","disabledEventPropagation(event);").attr("onselectstart","return false;").attr("id","span" + list[i].key);
							
							var input = document.createElement("input");
							$(input).css("margin-left","10px").attr("onclick","disabledEventPropagation(event);").attr("type","radio").addClass("check").attr("name","department").val(list[i].key);
							
							if ($("#readonlytree").length > 0 && $("#readonlytree").val() == 'true')
							{
								$(input).attr("disabled", "disabled");
							}
							
							if (list[i].value == '0')
							{
								//this means there are children
								var link = document.createElement("a");
								$(link).attr("onclick","openChildren($(this).closest('li').find('ul').first(), '" + list[i].key + "', " + isDGs + ")").html("<img class='folderimage' src='" + contextpath + "/resources/images/folderclosed.png'></img>");
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
						  }
						  
						  $(targetul).closest("span").find("a").first().find(".folderimage").each(function(){
								$(this).removeClass("folderimage").addClass("folderopenimage").attr("src", contextpath + "/resources/images/folderopen.png");
							});
						  					  
						  
						  $(targetul).show();
						  $( "#wheel" ).hide();
						  
						}});
				}
			} else {
				//close
				$(targetul).hide();
				$(targetul).closest("span").find("a").first().find(".folderopenimage").each(function(){
					$(this).removeClass("folderopenimage").addClass("folderimage").attr("src", contextpath + "/resources/images/folderclosed.png");
				});
			}
		}
		
		function showAddUserDialog(results)
		{
			$("#add-resultMode").val(results);
			$("#add-resultMode-Email").val(results);
			
			// select european commision if exists
			var exists = false;
			$('#add-user-type-ecas option').each(function(){
			    if (this.value == "eu.europa.ec") {
			        exists = true;
			        return false;
			    }
			});
			if (exists) 
			{
				$("#add-user-type-ecas").val("eu.europa.ec");
			}
			
			checkUserType();
			$('#add-department-name').val('');
			$('#add-user-name').val('');
			$('#add-first-name').val('');
			$('#add-last-name').val('');
			$('#add-user-email').val('');
			$("#search-results-more").hide();
			$('#add-user-dialog').modal();
		}
		
		function showAddDepartmentDialog()
		{
			$('#add-group-dialog').modal();
			$("#add-group-type-ecas").val("eu.europa.ec");
			
			var domainSelected;
			domainSelected=$("#add-group-type-ecas").val();
			if(domainSelected==null)
				domainSelected="eu.europa.ec";

			loadTopDepartments(domainSelected);
		}
	   		
		var selectedPrivilege = null;
		var selectedId = null;
		
		function domainChanged()
		{
			const domain = $("#add-group-type-ecas").val();
			
			if (domain != 'eu.europa.ec') {
				$('#add-group-tree-div').hide();
				return;
			}
			
			$('#add-group-tree-div').show();
			loadTopDepartments(domain);
		}
		
		function checkUserType()
		{
			$("#noEmptySearchIcon").hide();
			$("#noEmptySearchText").text('');

			$("#search-results").find("tbody").empty();

			if ($("#add-user-type-ecas").val() != "system" && $("#add-user-type-ecas").val() != "external")
			{
				$("#add-user-department-div").show();
				$("#add-user-firstname-div").show();
				$("#add-user-lastname-div").show();
				$("#eulogin-span").show();
			} else if ($("#add-user-type-ecas").val() == "external")
			{
				$("#add-user-department-div").hide();
				$("#add-user-firstname-div").show();
				$("#add-user-lastname-div").show();
				$("#eulogin-span").show();
			} else {
				$("#add-user-department-div").hide();
				$("#add-user-firstname-div").hide();
				$("#add-user-lastname-div").hide();
				$("#eulogin-span").hide();
			}
		}
		
		function changePrivilege(privilege, id)
		{
			selectedPrivilege = privilege;
			selectedId = id;
			
			if (privilege == 'AccessDraft')
			{
				$("#yellowArea").hide();
			} else {
				$("#yellowArea").show();
			}
					
			$('#user-dialog').modal();
		}
		
		function updatePrivilege(value)
		{
			$("#update-form-value").val(value);
			
			if (value == 2 && selectedPrivilege == "ManageInvitations")
			{
				if ($("#accessrow" + selectedId).find(".externaluser").length > 0)
				{
					$("#user-dialog").modal("hide");
					$("#ManageInvitations4Externals-dialog").modal("show");
					return;
				}
			}
		
			updatePrivilege2();			
		}
		
		function updatePrivilege2()
		{
			$("#wait-animation").show();
			$("#update-form-id").val(selectedId);
			$("#update-form-privilege").val(selectedPrivilege);			
			$("#update-form").submit();
		}
		
		function addUser()
		{			
			if ($("#search-results").find(".success").length == 0)
			{
				$("#search-results-none").show();	
				return;
			} 
			
			var login = $("#search-results").find(".success").first().attr("id");
			var ecas = $("#add-user-type-ecas").val() != "system";
			
			$("#search-results-none").hide();	
			
			$("#add-wait-animation").show();
			$("#add-form-login").val(login);
			$("#add-form-ecas").val(ecas);
			$("#add-form").submit();
		}

		function addUserByEmail()
		{
			let mailInput = $("#add-user-email").val();
			if (mailInput.length <= 0) {
				$("#invalidEmailsIcon").show();
				$("#invalidEmailsText").text(atLeastOneMail);
				return;
			}

			let emails = mailInput.split(";").map(s => s.trim());

			$("#add-wait-animation").show();
			$("#add-form-emails").val(emails);
			$("#add-form-email").submit();
		}
		
		function addGroup()
		{
			$("#add-wait-animation").show();
			
			if ($("input[name='department']:checked").length == 0) {
				$("#add-form-group-name").val($('#add-group-type-ecas').val());
			} else {
				$("#add-form-group-name").val($("input[name='department']:checked").val());
			}			
		
			$("#add-form-group").submit();
		}
		
		function removeUser()
		{
			$("#remove-wait-animation").show();
			$("#remove-id").val(selectedId);
			$("#remove-form").submit();
		}
		
		function showRemoveDialog(id, login, results)
		{
			$("#remove-resultMode").val(results);
			
			selectedId = id;
			$('#remove-user-dialog').modal();
		}

		function setEmailCheckFeedback(foundCount, invalidMails, notFoundMails) {
			resetEmailFeedback();

			$("#foundEmailUsers").text(noMailsFound.replace("{0}", foundCount));

			if(invalidMails.length > 0) {
				$("#invalidEmailsIcon").show();
				$("#invalidEmailsText").html(invalidEmails.replace("{0}", invalidMails.length).concat(' ', invalidMails.join(', ')));
			}

			if(notFoundMails.length > 0) {
				$("#notFoundEmailsIcon").show();
				$("#notFoundEmailsText").html(notFoundEmails.replace("{0}", notFoundMails.length).concat(' ', notFoundMails.join(', ')));
			}
		}

		function searchEmailUser(order) {
			let mailInput = $("#add-user-email").val();
			if (mailInput.length <= 0) {
				$("#invalidEmailsIcon").show();
				$("#invalidEmailsText").text(atLeastOneMail);
				return;
			}

			let allMails = mailInput.split(";").map(s => s.trim());
			let validMails = allMails.filter(mail => validateEmail(mail));
			let invalidMails = allMails.filter(mail => !validateEmail(mail) && mail != "");
			
			if (invalidMails.length > 0) {
				setEmailCheckFeedback(0, invalidMails, 0);
				return;
			}

			$.ajax({
				type:'GET',
				url: contextpath + "/logins/usersEmailJSON",
				data: {emails: $("#add-user-email").val()},
				dataType: 'json',
				cache: false,
				success: function( foundMails ) {
					let notFoundMails = [];
					for (let i = 0; i < validMails.length; i++) {
						if (i > 4) break; // we only return at most 5 users
						if(!foundMails.includes(validMails[i])) {
							notFoundMails.push(validMails[i]);
						}
					}
					setEmailCheckFeedback(foundMails.length, invalidMails, notFoundMails);
				}, error: function(e) {
					console.log(e);
					$("#busydialog").modal('hide');
					$("#add-user-dialog").modal('show');
				}});
		}

		function resetEmailFeedback() {
			$("#foundEmailUsers").text("");
			$("#invalidEmailsText").text("");
			$("#invalidEmailsIcon").hide();
			$("#notFoundEmailsIcon").hide();
			$("#notFoundEmailsText").text("");
		}

		function searchUser(order)
		{
			$("#noEmptySearchIcon").hide();
			$("#noEmptySearchText").text("");

			var name = $("#add-user-name").val();
			var first = $("#add-first-name").val();
			var last = $("#add-last-name").val();
			var email = $("#add-user-email").val();
			var department = $("#add-department-name").val();
			var type = $("#add-user-type-ecas").val();

			if (type != "system" && type != "external")
			{
				//case eu.europa.ec: Admin and form manager EC
				if (!(email != '' || department != '' || first != '' || last != '' || name != '')) {
					$("#noEmptySearchIcon").show();
					$("#noEmptySearchText").text(noEmptySearch);
					return;
				}
			} else if (type == "system")
			{
				//case system
				if (!(email != '' || name != '')) {
					$("#noEmptySearchIcon").show();
					$("#noEmptySearchText").text(noEmptySearch);
					return;
				}
			}

			var s = "name=" + name + "&type=" + type + "&department=" + department+ "&email=" + email + "&first=" + first + "&last=" + last + "&order=" + order;
			
			$("#add-user-dialog").modal('hide');
			$("#busydialog").modal('show');
			
			$("#search-results-more").hide();
			
			$.ajax({
				type:'GET',
				  url: contextpath + "/logins/usersJSON",
				  data: s,
				  dataType: 'json',
				  cache: false,
				  success: function( users ) {
					  $("#search-results").find("tbody").empty();
					  var body = $("#search-results").find("tbody").first();
					  
					  for (var i = 0; i < users.length; i++ )
					  {
						$(body).append(users[i]);
					  }
                                          
                      var hiddenTableHeaders = $("#search-results th.hideme");
                      for (var i = 0; i < hiddenTableHeaders.length; i++ )
					  {                                              
                    	  $('#search-results td:nth-child(' + hiddenTableHeaders[i].cellIndex + ')').hide();
                      }
					  
					  if (type != "system" && users.length >= 100)
					  {
						  $("#search-results-more").show();  
					  }
					  
					  $(body).find("tr").click(function() {
						  $("#search-results").find(".success").removeClass("success");
						  $(this).addClass("success");
						});
					  
					  $("#busydialog").modal('hide');
					  $("#add-user-dialog").modal('show');
				  }, error: function() {
					  $("#busydialog").modal('hide');
					  $("#add-user-dialog").modal('show');
				}});
			
			$("#search-results-none").hide();
			
		}
				
	var cachedAccesses = null;
	
	function firstPage() {
		page = 1;
		loadResultAccesses("date");
	}
	
	function previousPage() {
		if (page == 1) return;
		
		page--;
		loadResultAccesses("date");
	}
	
	function nextPage() {
		if (cachedAccesses.length < rows) return;
		
		page++;
		loadResultAccesses("date");
	}
	
	function loadResultAccesses(order)
	{
		var name = $("#resacc-user-name").val();
		var email = $("#resacc-user-email").val();
		
		var s = "name=" + name + "&email=" + email + "&order=" + order + "&page=" + page + "&rows=" + rows;
		
		$.ajax({
			type:'GET',
			  url: contextpath + "/" + surveyUID + "/management/resultAccessesJSON",
			  data: s,
			  dataType: 'json',
			  cache: false,
			  success: function( accesses ) {
			  
				  if (accesses.length > 0) {
					  $('#tbllist-empty-results').hide();
					  $('#results-paging').show();
				  } else {
					  $('#tbllist-empty-results').show();
					  $('#results-paging').hide();
				  }
				  
				  $("#tblResultPrivileges").find("tbody").empty();
				  var body = $("#tblResultPrivileges").find("tbody").first();
				  
				  for (var i = 0; i < accesses.length; i++ )
				  {
					  var tr = document.createElement("tr");
					  addTableCell(tr, accesses[i].userName);
					  addTableCell(tr, accesses[i].userEmail);
					  addFilterTableCell(tr, accesses[i]);
					  
					  if (!readOnlyResultPrivileges) {
						  addReadonlyTableCell(tr, accesses[i]);
					  }					  
			
					  addActionTableCell(tr, accesses[i]);					  				  
										  
					  $(body).append(tr);
				  }
				  
				  $('[data-toggle="tooltip"]').tooltip();
				  cachedAccesses = accesses;
				  
				  $('#results-first').text((page-1)*rows + 1);
				  $('#results-last').text((page-1)*rows + accesses.length);
				  
				  if (page == 1) {
					  $('#gotoFirst').attr("disabled", "disabled").addClass("disabled");
					  $('#gotoPrevious').attr("disabled", "disabled").addClass("disabled");
				  } else {
					  $('#gotoFirst').removeAttr("disabled").removeClass("disabled");
					  $('#gotoPrevious').removeAttr("disabled").removeClass("disabled");
				  }
				  
				  if (accesses.length == rows) {
					  $('#gotoNext').removeAttr("disabled").removeClass("disabled");
				  } else {
					  $('#gotoNext').attr("disabled", "disabled").addClass("disabled");
				  }
              
			  }, error: function (data) {
					showAjaxError(data.status)
			}});
	}
	
	function addFilterTableCell(tr, access) {
		 var td = document.createElement("td");
		 $(td).css("vertical-align", "middle");
		 
		 if (access.filter == null || access.filter.length == 0) {
			 $(td).html(noFilterMessage);
		 } else {
			 $(td).html(access.filter);
		 }
		 
		 var btn = document.createElement("a");
		 $(btn).addClass("iconbutton").attr("data-toggle", "tooltip").attr("title", "Edit").html('<span class="glyphicon glyphicon-pencil"></span>').css("float", "right");
		 $(btn).attr("onclick", "showEditFilterDialog(" + access.id + ")");
		 $(td).prepend(btn);
		 
		 $(tr).append(td);
	}
	
	function addActionTableCell(tr, access) {
		var td = document.createElement("td");
		$(td).css("vertical-align", "middle");
		
		var btn = document.createElement("a");
		$(btn).addClass("iconbutton").attr("data-toggle", "tooltip").attr("title", "Remove").html('<span class="glyphicon glyphicon-remove"></span>').attr("onclick", "showRemoveDialog(" + access.id + ",'" + access.userName + "', true);").css("float", "right");
		$(td).append(btn);
		
		$(tr).append(td);
	}
	
	function addReadonlyTableCell(tr, access) {
		var value = access.readonly;
		
		var td = document.createElement("td");
		$(td).css("vertical-align", "middle");
		 
		var select = document.createElement("select");
		$(select).addClass("form-control").attr("data-id", access.id);
		$(select).attr("onchange", "changeAccess(this)")
		 
		var option = document.createElement("option");
		$(option).val("false").html(labelreadwrite);
				 
		$(select).append(option);
		option = document.createElement("option");
		$(option).val("true").html(labelreadonly);
		
		$(select).append(option);		 
		$(td).append(select);		 
		$(tr).append(td);
		
		if (value) {
			$(select).val(value ? "true" : "false");
		}
	}
	
	function addTableCell(tr, text) {
		 var td = document.createElement("td");
		 $(td).css("vertical-align", "middle");
		 $(td).html(text);
		 $(tr).append(td);
	}
	
	function showEditFilterDialog(id) {
		$('#accessid').val(id);
		
		$('#edit-filter-dialog').find("input[type='checboc']").removeAttr("checked");
		$('#edit-filter-dialog').find("input[type='text']").val("");
		
		if (cachedAccesses != null) {
			for (var i = 0; i < cachedAccesses.length; i++) {
				if (cachedAccesses[i].id == id) {
					if (cachedAccesses[i].resultFilter != null) {
						for (var quid in cachedAccesses[i].resultFilter.filterValues) {
							$('#edit-filter-dialog').find("#" + quid).each(function(){
								if ($(this).is("div")) {
									var filter = cachedAccesses[i].resultFilter.filterValues[quid];
									// div with checkboxes
									var boxes = $(this).find("input");
									var readonly = cachedAccesses[i].readonlyFilterQuestions != null && cachedAccesses[i].readonlyFilterQuestions.indexOf(quid) > -1;
									$(boxes).each(function(){
										if (filter.indexOf($(this).val()) > -1) {
											$(this).prop("checked", "checked");
										}
										if (readonly) {
											$(this).prop("disabled", "disabled");
										}
									});
								} else {
									//textbox
									$(this).val(cachedAccesses[i].resultFilter.filterValues[quid]);
									
									if (cachedAccesses[i].readonlyFilterQuestions != null && cachedAccesses[i].readonlyFilterQuestions.indexOf(quid) > -1) {
										$('#edit-filter-dialog').find("#" + quid).attr("disabled", "disabled");
									}
								}
							});							
							
							//$('#edit-filter-dialog').find("#" + quid).val(cachedAccesses[i].resultFilter.filterValues[quid]);
							
//							if (cachedAccesses[i].readonlyFilterQuestions != null && cachedAccesses[i].readonlyFilterQuestions.indexOf(quid) > -1) {
//								$('#edit-filter-dialog').find("#" + quid).attr("disabled", "disabled");
//							}
						}
					}
					break;
				}
			}
		}
		
		$('#edit-filter-dialog').modal('show');

		if (!reportingdatabaseused) {
		    checkNumberOfFilters(true);
		}
	}
	
	function updateResultFilter() {
		$('#resultpage').val(page);
		$('#updateResultFilterForm').submit();
	}
	
	function changeAccess(select) {
		var id = $(select).attr("data-id");
		var value = $(select).val();
		var s = "accessid=" + id + "&readonly=" + value;
		
		$.ajax({
			type:'POST',
			  beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
			  url: contextpath + "/" + surveyUID + "/management/updateResultAccessTypeJSON",
			  data: s,
			  dataType: 'json',
			  cache: false,
			  success: function(success) {
				  if (!success) {
					  showError(errorOperationFailed);
				  }
			  }, error: function (data) {
					showAjaxError(data.status)
			}});
		
	}

	function checkNumberOfFilters(reportingDBDisabled) {
	    if (!reportingDBDisabled) return;

	    let counter = 0;

	    $('#updateResultFilterForm').find("table").find(".filterrow").each(function(){
	        let valfound = false;
	        $(this).find("input[type=text]").each(function(){
	            if ($(this).val() != null && $(this).val().length > 0) {
	                valfound = true;
	            }
	        });
	        $(this).find("input[type=checkbox]").each(function(){
                if ($(this).is(":checked")) {
                    valfound = true;
                }
            });

	        if (valfound) {
	            $(this).attr("data-filterset", "true");
	            counter++;
	        } else {
	            $(this).attr("data-filterset", "false");
	        }
	    });

	    if (counter > 2) {
	        $('#updateResultFilterForm').find("table").find(".filterrow").each(function(){
	            if ($(this).attr("data-filterset") == "false") {
	                $(this).find("input").attr("disabled", "disabled").addClass("disabled");
	            }
	        });
	    } else {
	        $('#updateResultFilterForm').find("input.disabled").each(function(){
	            $(this).removeAttr("disabled").removeClass("disabled");
	        });
	    }
	}