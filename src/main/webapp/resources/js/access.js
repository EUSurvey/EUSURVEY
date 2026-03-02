	var scrollTimeout = null;
	
		$(function() {					
			$("#form-menu-tab").addClass("active");
			$("#access-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
						
			checkUserTypeAccess();
			
			$('#add-user-name').keyup(function(e){
			    if(e.keyCode == 13){
			    	searchUserForAccess();
			    }
			});
			
			$('#add-department-name').keyup(function(e){
			    if(e.keyCode == 13){
			    	searchUserForAccess();
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