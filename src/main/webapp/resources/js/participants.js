var Guestlist = function() {
	var self = this;
	this.id = ko.observable("");
	this.type = ko.observable("");
	this.name = ko.observable("");
	this.created = ko.observable("");
	this.children = ko.observable(0);
	this.invited = ko.observable(0);
	this.inCreation = ko.observable(false);
	this.runningMails = ko.observable(false);
	this.error = ko.observable(false);
	this.active = ko.observable(false);
	this.attendees = ko.observableArray().extend({ deferred: true });
	this.tokens = ko.observableArray();
	this.users = ko.observableArray();
	
	this.activateEnabled = ko.computed(function() {
		return !this.inCreation() && !this.active();
	}, this);
	
	this.deactivateEnabled = ko.computed(function() {
		return !this.inCreation() && this.active();
	}, this);
	
	this.deleteEnabled = ko.computed(function() {
		return !this.inCreation() && !this.active();
	}, this);
	
	this.sendEnabled = ko.computed(function() {
		return !this.inCreation() && this.type() != "Token" && this.children() > 0 && this.active();
	}, this);
	
	this.editEnabled = ko.computed(function() {
		return !this.inCreation();
	}, this);
	
	this.exportEnabled = ko.computed(function() {
		return this.type() == "Token" || this.type() == "Static";
	}, this);
	
	this.detailsEnabled = ko.computed(function() {
		return !this.inCreation();
	}, this);
	
	this.exportxls = function() {
		showExportDialog('Tokens', 'xls', self.id());
	}
	
	this.exportods = function() {
		showExportDialog('Tokens', 'ods', self.id());
	}
	
	this.activate = function() {
		var model = this;
		var request = $.ajax({
			  url: contextpath + "/noform/management/participantsActivate?id=" + model.id(),
			  cache: false,
			  success: function(result)
			  {			 
				  if (result == "ok")
				  {
					model.active(true);  
					showSuccess(p_activated);
				  } else {
					showError(result);
				  }
			  }
			});
	}
	
	this.deactivate = function() {
		var model = this;
		var request = $.ajax({
			  url: contextpath + "/noform/management/participantsDeactivate?id=" + model.id(),
			  cache: false,
			  success: function(result)
			  {			 
				  if (result == "ok")
				  {
					model.active(false);  
					showSuccess(p_deactivated);
				  } else {
					showError(result);
				  }
			  }, error: function() {
				  showGenericError();
			  }
			});
	}
	
	this.deleteList = function() {
		_participants.groupToDelete(this.id());
		$('#guestlisttobedeleted').text(this.name());
		$('#delete-list-dialog').modal('show');
	}
	
	this.showDetails = function() {
		_participants.selectedGroup(this);
		_participants.Page(2);
		$('#details').find('[data-toggle="tooltip"]').tooltip();
		this.loadChildren(true, false);
	}
	
	this.edit = function() {
		_participants.selectedGroup(this);
		this.loadChildren(true, true);
		
		switch(self.type())
		{
			case "Token":
				_participants.Page(5);
				break;
			case "Static":
				_participants.Page(3);
				break;
			case "ECMembers":
				_participants.Page(4);
				break;
			case "VoterFile":
				_participants.Page(6);
				break;
		}		
		
		_participants.Step(1);
		$('#newcontactlist').find('[data-toggle="tooltip"]').tooltip();
		
		_participants.loadAttendees(true);
	}
	
	var currentChildrenPage = 1;
	var endChildrenReached = false;
	this.loadChildren = function(first, all) {
		var model = this;
		
		if (first)
		{
			currentChildrenPage = 1;
			model.attendees.removeAll();
			model.users.removeAll();
			model.tokens.removeAll();
			endChildrenReached = false;
		} else {
			if (endChildrenReached) return;			
			currentChildrenPage++;
		}
				
		var s = "newPage=" + currentChildrenPage + "&itemsPerPage=" + 50;
		if (all) s += "&all=true";
				
		$.ajax({
			type:'GET',
			url: contextpath + "/noform/management/children?id=" + model.id(),
			data: s,
			dataType: 'json',
			cache: false,
			success: function( items ) {	
				
				if (items.length < 50)
				{
					endChildrenReached = true;
				}
				
				for (var i = 0; i < items.length; i++ )
				{
				  if (self.type() == 'Static') {
					  var attendee = items[i];
					  attendee["selected"] = ko.observable(false);
					  attendee["hidden"] = ko.observable(false);
					  self.attendees.push(attendee);
				  } else if (self.type() == 'Token') {
					  var token = items[i];
					  token["selected"] = ko.observable(false);
					  token["deactivated"] = ko.observable(token["deactivated"]);
					  self.tokens.push(token);
				  } else {
					  var user = items[i];
					  user["selected"] = ko.observable(false);
					  user["hidden"] = ko.observable(false);
					  self.users.push(user);
				  }
				}
				model.initStickyTableHeaders();
			}, error: function() {
				  showGenericError();
			}
		});		
	}
	
	this.initStickyTableHeaders = function() {
		$('#participantdetailstable').stickyTableHeaders('destroy');
		
		$("#participantdetailstable").stickyTableHeaders({fixedOffset: 112});	
	}
	
	this.activateSelected = function()
	{
		for (var i = 0; i < self.tokens().length; i++)
		{
			if (self.tokens()[i].selected())
			{
				self.tokens()[i].deactivated(false);				
			}
		}
	}	
	
	this.deactivateSelected = function()
	{
		for (var i = 0; i < self.tokens().length; i++)
		{
			if (self.tokens()[i].selected())
			{
				self.tokens()[i].deactivated(true);				
			}
		}
	}
	
	this.removeSelected = function()
	{
		var todelete = [];
		for (var i = 0; i < self.attendees().length; i++)
		{
			if (self.attendees()[i].selected())
			{
				todelete[todelete.length] = self.attendees()[i];
			}
		}
		
		for (var i = 0; i < todelete.length; i++)
		{
			self.attendees.remove(todelete[i]);
		}
		
		todelete = [];
		for (var i = 0; i < self.tokens().length; i++)
		{
			if (self.tokens()[i].selected())
			{
				todelete[todelete.length] = self.tokens()[i];
			}
		}
		
		for (var i = 0; i < todelete.length; i++)
		{
			self.tokens.remove(todelete[i]);
		}
		
		todelete = [];
		for (var i = 0; i < self.users().length; i++)
		{
			if (self.users()[i].selected())
			{
				todelete[todelete.length] = self.users()[i];
			}
		}
		
		for (var i = 0; i < todelete.length; i++)
		{
			self.users.remove(todelete[i]);
		}
	}	
	
	this.checkAll = function(checked)
	{
		for (var i = 0; i < self.attendees().length; i++)
		{
			self.attendees()[i].selected(checked);
		}
		for (var i = 0; i < self.users().length; i++)
		{
			self.users()[i].selected(checked);
		}
		for (var i = 0; i < self.tokens().length; i++)
		{
			self.tokens()[i].selected(checked);
		}
	}
	
	this.addTokens = function()
	{
		var tokens = parseInt($('#numtokens').val());
		var s = "tokens=" + tokens;
		
		$.ajax({
			type:'GET',
			  url: contextpath + "/noform/management/participants/createTokens",
			  dataType: 'json',
			  data: s,
			  cache: false,
			  success: function( list ) {				  
				  for (var i = 0; i < list.length; i++ )
				  {
					  var token = list[i];
					  token["selected"] = ko.observable(false);
					  token["deactivated"] = ko.observable(false);
				  	  self.tokens.push(token);
				  }
			  }, error: function() {
				  showGenericError();
			  }
		});
	}
	
	this.filterContacts = function() {
		var namefilter = $('#selectednamefilter').val();
		var emailfilter = $('#selectedemailfilter').val();
		for (var i = 0; i < self.attendees().length; i++)
		{
			var hidden = false;
			
			if (namefilter.length > 0 && self.attendees()[i].name.indexOf(namefilter) == -1)
			{
				hidden = true;
			}
			
			if (!hidden && emailfilter.length > 0 && self.attendees()[i].email.indexOf(emailfilter) == -1)
			{
				hidden = true;
			}
			
			if (!hidden)
			{
				$('input.selectedattributefilter').each(function(){
					var filter = $(this).val();
					if (filter.length > 0)
					{
						var attributename = $(this).attr("data-name");
						
						if (attributename == "Owner")
						{
							if (self.attendees()[i].owner.indexOf(filter) == -1)
							{
								hidden = true;
							}
						} else {						
							var value = _participants.getAttributeValue(self.attendees()[i], attributename);
							if (value.indexOf(filter) == -1)
							{
								hidden = true;
								return;
							}
						}
					}
				});
			}
			
			self.attendees()[i].hidden(hidden);
		}
	}
	
	this.filterUsers = function() {
		var namefilter = $('#selectedecnamefilter').val();
		var emailfilter = $('#selectedecemailfilter').val();
		var departmentfilter = $('#selectedecdepartmentfilter').val();
		for (var i = 0; i < self.users().length; i++)
		{
			var hidden = false;
			
			if (namefilter.length > 0 && self.users()[i].displayName.indexOf(namefilter) == -1)
			{
				hidden = true;
			}
			
			if (!hidden && emailfilter.length > 0 && self.users()[i].email.indexOf(emailfilter) == -1)
			{
				hidden = true;
			}
			
			if (!hidden && departmentfilter.length > 0 && self.users()[i].departmentNumber.indexOf(departmentfilter) == -1)
			{
				hidden = true;
			}
			
			self.users()[i].hidden(hidden);
		}
	}
}

var AttributeName = function() {
	this.id =  ko.observable(0);
	this.name =  ko.observable("");
}

var Participants = function() {
	var self = this;
	
	this.Page = ko.observable(1);
	this.Step = ko.observable(1);
	this.DataLoaded = ko.observable(false);
	this.ShowWait = ko.observable(false);
	this.Guestlists = ko.observableArray();
	this.Access = ko.observable(0);
	this.groupToDelete = ko.observable(0);
	this.selectedGroup = ko.observable(null);
	this.Attendees = ko.observableArray().extend({ deferred: true });
	this.Users = ko.observableArray();
	this.Voters = ko.observableArray();
	this.voterPage = ko.observable(1);
	this.firstVoter = ko.observable(0);
	this.lastVoter = ko.observable(0);
	this.lastVoterReached = ko.observable(false);
	this.totalVoters = ko.observable(0);
	this.attributeNames = ko.observableArray();
	this.Domain = ko.observable("");
	this.EVoteUsers = ko.observableArray();
	this.VotersSearchPage = ko.observable(1);
	this.VotersSearchStart = ko.observable(0);
	this.VotersSearchEnd = ko.observable(0);

	function recalcStartEnd(){
		let start = (self.VotersSearchPage() - 1) * votersSearchPageSize + 1
		let end = start + self.EVoteUsers().length - 1
		if (self.EVoteUsers().length === 0){
			start = end = votersSearchPageSize * (self.VotersSearchPage() - 1)
		}
		self.VotersSearchStart(start)
		self.VotersSearchEnd(end)
	}
	this.VotersSearchPage.subscribe(recalcStartEnd)
	this.EVoteUsers.subscribe(recalcStartEnd)

	for (var i = 0; i < attributeNames.length; i++)
	{
		var attname = new AttributeName();
		attname.id(attributeIDs[i]);
		attname.name(attributeNames[i]);
		this.attributeNames.push(attname);
	}
	
	this.loadGuestlists = function() {
		var model = this;
		var request = $.ajax({
			  url: contextpath + "/noform/management/participantsjson",
			  dataType: "json",
			  cache: false,
			  success: function(result)
			  {
				  model.Guestlists.removeAll();
				  for (var i = 0; i < result.length; i++)
				  {
					  var g = new Guestlist();
					  g.id(result[i].id);
					  g.type(result[i].type);
					  g.name(result[i].name);
					  g.created(result[i].formattedDate);
					  g.children(result[i].children);
					  g.invited(result[i].invited)
					  g.inCreation(result[i].inCreation);
					  g.runningMails(result[i].runningMails);
					  g.error(result[i].error);
					  g.active(result[i].active);
					  
					  model.Guestlists.push(g);
					  $('[data-toggle="tooltip"]').tooltip();
				  };
				  
				  model.DataLoaded(true);
				  window.setTimeout("checkFinishedGuestlists()", 10000);
			  }, error: function() {
				  showGenericError();
			  }
			});
	}
	
	this.ContactGuestlists =  ko.computed(function()
	{
		var result = [];
		for (var i = 0; i < self.Guestlists().length; i++)
		{
			if (self.Guestlists()[i].type() == "Static")
			{
				result[result.length] = self.Guestlists()[i];
			}
		}
		
		return result;
	});
	
	this.TokenGuestlists =  ko.computed(function()
	{
		var result = [];
		for (var i = 0; i < self.Guestlists().length; i++)
		{
			if (self.Guestlists()[i].type() == "Token")
			{
				result[result.length] = self.Guestlists()[i];
			}
		}
		
		return result;
	});
	
	this.ECGuestlists =  ko.computed(function()
	{
		var result = [];
		for (var i = 0; i < self.Guestlists().length; i++)
		{
			if (self.Guestlists()[i].type() == "ECMembers")
			{
				result[result.length] = self.Guestlists()[i];
			}
		}
		
		return result;
	});
	
	this.VoterFiles =  ko.computed(function()
	{
		var result = [];
		for (var i = 0; i < self.Guestlists().length; i++)
		{
			if (self.Guestlists()[i].type() == "VoterFile")
			{
				result[result.length] = self.Guestlists()[i];
			}
		}
		
		return result;
	});
	
	this.getChildByID = function(id) {
		for (var i = 0; i < self.Guestlists().length; i++)
		{
			if (self.Guestlists()[i].id() == id)
			{
				return self.Guestlists()[i];
			}
		}
		return null;
	}
	
	var currentPage = 1;
	this.loadAttendees = function(first) {
		var model = this;
		
		if (first)
		{
			currentPage = 1;
			model.Attendees.removeAll();
		} else {
			currentPage++;
		}
		
		var s = "name=" + $("#namefilter").val() + "&email=" + $("#emailfilter").val() + "&newPage=" + currentPage + "&itemsPerPage=" + 20;
		
		$('.attributefilter').each(function(){
			if ($(this).val() != null && $(this).val().length > 0)
			{
				s += "&" + $(this).attr("id") + "=" + $(this).val();
			}
		});		
		
		var request = $.ajax({
			  url: contextpath + "/noform/management/participantsJSON",
			  data: s,
			  dataType: 'json',
			  cache: false,
			  success: function(paging)
			  {			 
				  for (var i = 0; i < paging.items.length; i++ )
				  {	
					  var attendee = paging.items[i];
					  attendee["selected"] = ko.observable($('#checkallcontacts').is(":checked"));
					  model.Attendees.push(attendee);
				  };
			  }, error: function() {
				  showGenericError();
			  }
			});
	}
	
	var usersEndReached = false;
	this.loadUsers = function(first) {
		var model = this;
		
		if (model.Page() == 1)
		{
			return;
		}
		
		if ($("#domain").val().length == 0)
		{
			return;
		}
		
		model.ShowWait(true);
		
		if (first)
		{
			usersEndReached = false;
			currentPage = 1;
			model.Users.removeAll();
		} else {
			if (usersEndReached)
			{
				model.ShowWait(false);
				return;
			}
			
			currentPage++;
		}
		
		var s = "name=" + $("#ecnamefilter").val()+ "&domain=" + $("#domain").val() + "&email=" + $("#ecemailfilter").val() + "&department=" + $("#ecdepartmentfilter").val() + "&newPage=" + currentPage + "&itemsPerPage=" + 100;
		
		var request = $.ajax({
			  url: contextpath + "/noform/management/usersJSON",
			  data: s,
			  dataType: 'json',
			  cache: false,
			  success: function(paging)
			  {	
				  if (paging.items.length == 0)
				  {
					  usersEndReached = true;
				  }
				  for (var i = 0; i < paging.items.length; i++ )
				  {	
					  var user = paging.items[i];
					  user["selected"] = ko.observable($('#checkalleccontacts').is(":checked"));
					  model.Users.push(user);
				  };
				  
				  model.ShowWait(false);
			  }, error: function() {
				  showGenericError();
			  }
			});
	}
	
	this.getAttributeValue = function(attendee, name)
	{
		for (var k = 0; k < attendee.attributes.length; k++)
		{
			if (attendee.attributes[k].attributeName.name == name)
			{
				return attendee.attributes[k].value;
			}
		}		
		
		return "";
	}
	
	this.newContactList = function() {
		this.Page(3);
		this.Step(1);
		$('#newcontactlist').find('[data-toggle="tooltip"]').tooltip();
		var g = new Guestlist();
		g.type("Static");
		this.selectedGroup(g);
		this.loadAttendees(true);
	}
	
	this.newEUList = function() {
		this.Page(4);
		this.Step(1);
		$('#neweclist').find('[data-toggle="tooltip"]').tooltip();
		var g = new Guestlist();
		g.type("ECMembers");
		this.selectedGroup(g);
	}
	
	this.newTokenList = function() {
		this.Page(5);
		this.Step(1);
		$('#newtokenlist').find('[data-toggle="tooltip"]').tooltip();
		var g = new Guestlist();
		g.type("Token");
		this.selectedGroup(g);
	}
	
	this.deleteGuestList = function() {
		var model = this;
		var request = $.ajax({
			  url: contextpath + "/noform/management/participantsDelete?id=" + model.groupToDelete(),
			  cache: false,
			  success: function(result)
			  {			 
				  if (result == "ok")
				  {
					model.Guestlists.remove( function (item) { return item.id() == model.groupToDelete(); } )
					showSuccess(p_deleted);
				  } else {
					showError(result);
				  }
			  }, error: function() {
				  showGenericError();
			  }
			});
	}
	
	this.delete = function(id) {
		this.groupToDelete(id);
		$('#delete-list-dialog').modal('show');
	}
	
	this.checkAll = function(checked) 
	{
		for (var i = 0; i < this.Attendees().length; i++)
		{
			this.Attendees()[i].selected(checked);
		}
		for (var i = 0; i < this.Users().length; i++)
		{
			this.Users()[i].selected(checked);
		}
	}
	
	this.moveContacts = function(){
		self.ShowWait(true);
		setTimeout(function(){ self.moveContactsInner(); }, 500);		
	}
	
	this.moveContactsInner = function(){
		var selectedattendees = [];
		
		if ($('#checkallcontacts').is(":checked"))
		{
			self.ShowWait(true);
			var s = "name=" + $("#namefilter").val() + "&email=" + $("#emailfilter").val() + "&newPage=" + 1 + "&itemsPerPage=" + 2000000;
			
			$('.attributefilter').each(function(){
				if ($(this).val() != null && $(this).val().length > 0)
				{
					s += "&" + $(this).attr("id") + "=" + $(this).val();
				}
			});		
			
			var request = $.ajax({
				  url: contextpath + "/noform/management/participantsJSON",
				  data: s,
				  dataType: 'json',
				  async: false,
				  cache: false,
				  success: function(paging)
				  {			 
					  for (var i = 0; i < paging.items.length; i++ )
					  {	
						  selectedattendees[selectedattendees.length] = paging.items[i];
					  };
				  }, error: function() {
					  showGenericError();
				  }
				});
		} else {
			for (var i = 0; i < this.Attendees().length; i++)
			{
				if (this.Attendees()[i].selected())
				{
					selectedattendees[selectedattendees.length] = this.Attendees()[i];
				}
			}
		}
		
		for (var i = 0; i < selectedattendees.length; i++)
		{
			//check if it is not already there
			var found = false;
			for (var j = 0; j < this.selectedGroup().attendees().length; j++)
			{
				if (this.selectedGroup().attendees()[j].id == selectedattendees[i].id)
				{
					found = true;
					break;
				}
			}
			
			if (!found)
			{
				var copy = JSON.parse(JSON.stringify(selectedattendees[i]));
				copy["selected"] = ko.observable(false);
				copy["hidden"] = ko.observable(false);
				this.selectedGroup().attendees.push(copy);
			}			
		}
		
		self.ShowWait(false);
	}
	
	this.moveECContacts = function(){		
		for (var i = 0; i < this.Users().length; i++)
		{
			if (this.Users()[i].selected())
			{
				//check if it is not already there
				var found = false;
				for (var j = 0; j < this.selectedGroup().users().length; j++)
				{
					if (this.selectedGroup().users()[j].id == this.Users()[i].id)
					{
						found = true;
						break;
					}
				}
				
				if (!found)
				{
					var copy = JSON.parse(JSON.stringify(this.Users()[i]));
					copy["selected"] = ko.observable(false);
					copy["hidden"] = ko.observable(false);
					this.selectedGroup().users.push(copy);
				}			
			}
		}
	}
	
	this.Save = function() {
		
		if (self.selectedGroup().type() == "Static")
		{
			if (!validateInput($('#create-step-2-contacts')))
			{
				return;
			}
		} else if (self.selectedGroup().type() == "Token")
		{
			if (!validateInput($('#create-step-2-tokens')))
			{
				return;
			}
		} else
		{
			if (!validateInput($('#create-step-2-ec')))
			{
				return;
			}
		}
		
		self.ShowWait(true);
		
		var jsonData = ko.toJSON(self.selectedGroup());
		
		$.ajax({
			type:'POST',
			  url: contextpath + '/noform/management/saveguestlist',
			  contentType: "application/json; charset=utf-8",
			  processData:false, //To avoid making query String instead of JSON
			  data: jsonData,
			  beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
			  cache: false,
			  success: function( data ) {						  
				if (data == "successcreated") {
				   showSuccess(p_guestlistcreated);
				   self.Page(1);
				   self.loadGuestlists();
				} else if (data == "successsaved") {
					   showSuccess(p_guestlistsaved);
					   self.Page(1);
					   self.loadGuestlists();
				} else {
					showExport(data);
				}
				self.ShowWait(false);
			}, error: function() {
				  showGenericError();
			  }
		});	
	}
}

var _participants = new Participants();

$(function() {					
	$("#form-menu-tab").addClass("active");
	$("#participants-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
	ko.applyBindings(_participants, $("#participants")[0]);
	ko.applyBindings(_participants, $("#details")[0]);
	ko.applyBindings(_participants, $("#newcontactlist")[0]);
	ko.applyBindings(_participants, $("#neweclist")[0]);
	ko.applyBindings(_participants, $("#newtokenlist")[0]);
	ko.applyBindings(_participants, $("#newvoterfile")[0]);
	ko.applyBindings(_participants, $("#wait-dialog")[0]);
	ko.applyBindings(_participants, $("#add-voter-dialog")[0]);
	_participants.loadGuestlists();	
	
	var $col = $("#contactsdiv");
	$col.scroll(function(){
		if ($col.innerHeight() >= $col.prop('scrollHeight') - $col.scrollTop())
			_participants.loadAttendees(false); 
		
		$('#contactshead').scrollLeft($("#contactsdiv").scrollLeft());
	});
		
	$("#selectedcontactsdiv").scroll(function(){	
		$('#selectedcontactshead').scrollLeft($("#selectedcontactsdiv").scrollLeft());
	});
	
	$("#selectedcontactsdiv2").scroll(function(){	
		$('#selectedcontactshead2').scrollLeft($("#selectedcontactsdiv2").scrollLeft());
	});
	
	var $eccol = $("#eccontactsdiv");
	$eccol.scroll(function(){
		if ($eccol.innerHeight() >= $eccol.prop('scrollHeight') - $eccol.scrollTop())
			_participants.loadUsers(false); 
		
		$('#eccontactshead').scrollLeft($("#eccontactsdiv").scrollLeft());
	});
		
	$("#selectedeccontactsdiv").scroll(function(){	
		$('#selectedeccontactshead').scrollLeft($("#selectedeccontactsdiv").scrollLeft());
	});
	
	$("#numtokens").spinner({ decimals:0, min:1, start:"", allowNull: true });
	
	 $(window).scroll(function() {
		    if ($(window).scrollTop() <= $(document).height() - $(window).height() && $(window).scrollTop() >= $(document).height() - $(window).height() - 10) {
		    	loadMore();
		  }
		 });
});

function loadMore()
{
	if ($("#details").is(":visible"))
	{
		_participants.selectedGroup().loadChildren(false, false);
	}
}

function checkReturn(event, box)
{
	var keycode = (event.keyCode ? event.keyCode : event.which);
    if (keycode == '13') {
    	if (_participants.Page() == 3)
    	{
    		if ($(box).closest('#selectedcontactshead').length > 0)
    		{
    			_participants.selectedGroup().filterContacts();
    		} else {
    			_participants.loadAttendees(true);
    		}
    	} else if (_participants.Page() == 4)
    	{    		
    		if ($(box).closest('#selectedeccontactshead').length > 0)
    		{
    			_participants.selectedGroup().filterUsers();
    		} else {
    			_participants.loadUsers(true);
    		}
    	}
    }
}

var selectedgroup;
var exportType;
var exportFormat;
function showExportDialog(type, format, group)
{
	selectedgroup = group;
	exportType = type;
	exportFormat = format;
	$('#export-name').val("");
	$('#export-name-dialog').find(".validation-error").hide();
	$('#export-name-dialog-type').text(format.toUpperCase());
	$('#export-name-dialog').modal();	
	$('#export-name-dialog').find("input").first().focus();
}

function startExport(name)
{
	// check again for new exports
	window.checkExport = true;
	
	$.ajax({
		type:'POST',
		  url: contextpath + '/exports/start/' + exportType + "/" + exportFormat,
		  data: {exportName: name, showShortnames: false, allAnswers: false, group: selectedgroup},
		  beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
		  cache: false,
		  success: function( data ) {						  
			  if (data == "success") {
					showExportSuccessMessage();
				} else {
					showExportFailureMessage();
				}
				$('#deletionMessage').addClass('hidden');
		}, error: function() {
			  showGenericError();
		  }
	});	
				
	return false;
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
			_participants.attributeNames.removeAll();	
			for (var i = 0; i < list.length; i++ )
			{
				var attname = new AttributeName();
				attname.id(list[i].id);
				attname.name(list[i].name);
				 _participants.attributeNames.push(attname);
			}
			
			$("#contacts").stickyTableHeaders({ scrollableArea: $("#contactsdiv")[0], "fixedOffset": 0});
			$('#configure-attributes-dialog').modal("hide");
			$("#add-wait-animation2").hide();
		  }, error: function() {
			  showGenericError();
		  }
		});
}

function cancelConfigure()
{
	$("#configure-attributes-dialog").modal("hide");
}

function uncheckall() {
	$('#checkallcontacts').removeAttr('checked');	
}

function uncheckallselected() {
	$('#checkallselectedcontacts').removeAttr('checked');
	$('#checkallselectedeccontacts').removeAttr('checked');	
	$('#checkallselectedtokens').removeAttr('checked');	
}

function checkFinishedGuestlists()
{
	if ($(".increation").length > 0)
	{
		var ids = "ids=";
		$(".increation").each(function(){
			ids += $(this).attr("data-id") + ";";
		});				
		
		var request = $.ajax({
		  type:'GET',
		  dataType: 'json',
		  url: contextpath + "/noform/management/participants/finishedguestlists?" + ids,
		  cache: false,
		  success: function(list)
		  {
			  for (var i = 0; i < list.length; i++ )
			  {
				 var id = list[i].substring(0, list[i].indexOf("|"));
				 var participants = list[i].substring(list[i].indexOf("|")+1);
				 
				 var guestlist = _participants.getChildByID(id);
				 if (guestlist != null)
				 {
					 guestlist.inCreation(false);
					 guestlist.children(participants);
				 }
				
				 if (participants == "error2")
				 {
					 showError(errorMaxTokenNumberExceeded);
				 } else if (participants == "error1")
				 {
					 showError(errorProblemDuringSave);
				 }				
			  };
		  },
		  error: function(jqXHR, textStatus, errorThrown)
		  {
			  alert(textStatus);
		  }
		});
		
		window.setTimeout("checkFinishedGuestlists()", 60000);
	};
	
	if ($(".inrunningmails").length > 0)
	{
		var ids = "surveyUid=" + surveyuid + "&ids=";
		$(".inrunningmails").each(function(){
			ids += $(this).attr("data-id") + ";";
		});				
		
		var request = $.ajax({
			 url: contextpath + "/noform/management/participants/finishedguestlistsmail",
		  data: ids,
		  dataType: "json",
		  cache: false,
		  success: function(list)
		  {
			  for (var i = 0; i < list.length; i++ )
			  {
				 var id = list[i].key;
				 var count = list[i].value;
				 
				 var guestlist = _participants.getChildByID(id);
				 if (guestlist != null)
				 {
					 guestlist.runningMails(false);
					 guestlist.invited(count);
				 }			 
			  };
		  },
		  error: function(jqXHR, textStatus, errorThrown)
		  {
			  alert(textStatus);
		  }
		});
		
		window.setTimeout("checkFinishedGuestlists()", 60000);
	};	
}

function showGenericError() {
	showError("Operation not possible");
}
