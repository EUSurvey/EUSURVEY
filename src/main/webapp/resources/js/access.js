	var scrollTimeout = null;
	
		$(function() {					
			$("#form-menu-tab").addClass("active");
			$("#access-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
						
			checkUserType();	
			
			$('#add-user-name').keyup(function(e){
			    if(e.keyCode == 13){
			    	searchUser();
			    }
			}) ;
			
			$('#add-department-name').keyup(function(e){
			    if(e.keyCode == 13){
			    	searchUser();
			    }
			}) ;
			
			$('#add-group-name').keyup(function(e){
			    if(e.keyCode == 13){
			    	addGroup($('#add-group-name').val());
			    	$('#add-group-dialog').modal("hide");
			    }
			}) ;
			
		});
		
		function loadTopDepartments(domain)
		{
			$( "#wheel" ).show();
			$("#tree").empty();
			
			$.ajax({
				type:'GET',
				  url: contextpath + "/noform/management/topDepartmentsJSON?domain=" +domain,
				  dataType: 'json',
				  success: function( list ) {					  
					
					var selectedValue = domain.replace("eu.europa.","");

					var selectedText = $("#add-group-type-ecas :selected").text();
						
				  	var liRoot = document.createElement("li");
				  	var spanRoot = document.createElement("span");
				  	$(spanRoot).attr("onclick","disabledEventPropagation(event);").attr("onselectstart","return false;").attr("id","spanRoot");
					var inputRoot = document.createElement("input");
					$(inputRoot).css("margin-left","10px").attr("onclick","disabledEventPropagation(event);").attr("type","radio").addClass("check").attr("name","department").val(selectedValue);
					if ($("#readonlytree").length > 0 && $("#readonlytree").val() == 'true')
					{
						$(inputRoot).attr("disabled", "disabled");
					}
					
					var linkRoot = document.createElement("a");
					$(linkRoot).attr("onclick","openChildren($(this).closest('li').find('ul').first(),'"+  selectedValue +"')").html("<img class='folderimage' src='" + contextpath + "/resources/images/folderclosed.png'></img>");
					$(spanRoot).append(linkRoot);
					
					$(spanRoot).append(inputRoot);
					$(spanRoot).append(selectedText);
					
					$(liRoot).append(spanRoot);
					
					var ulRoot = document.createElement("ul");
					$(ulRoot).addClass("dep-tree").addClass("dep-tree-child").hide();
					$(spanRoot).append(ulRoot);
					
					$("#tree").append(liRoot);
									  
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
					
					$(ulRoot).append(li);
				  }
				  
				  openChildren($(".dep-tree-child").first(), selectedValue);
				  				  				  				  
				  $( "#wheel" ).hide();
				  
				}});
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
		
		function openChildren(targetul, department)
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
						  data: {term:department},
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
		
		function showAddUserDialog()
		{
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
		
		function domainChaged()
		{
			loadTopDepartments($("#add-group-type-ecas").val());
		}
		
		function checkUserType()
		{
			$("#search-results").find("tbody").empty();
			var thead = document.createElement("thead");
			
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
		
		function addGroup(name)
		{
			$("#add-wait-animation").show();
			$("#add-form-group-name").val($("input[name='department']:checked").val());
			$("#add-form-group").submit();
		}
		
		function removeUser()
		{
			$("#remove-wait-animation").show();
			$("#remove-id").val(selectedId);
			$("#remove-form").submit();
		}
		
		function showRemoveDialog(id, login)
		{
			selectedId = id;
			$('#remove-user-dialog').modal();
		}
                
		function searchUser(order)
		{
			var name = $("#add-user-name").val();
			var first = $("#add-first-name").val();
			var last = $("#add-last-name").val();
			var email = $("#add-user-email").val();
			var type = $("#add-user-type-ecas").val();
			var department = $("#add-department-name").val();
			
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