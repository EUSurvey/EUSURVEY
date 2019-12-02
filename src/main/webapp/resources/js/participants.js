var Guestlist = function() {
	var self = this;
	this.id = ko.observable("");
	this.type = ko.observable("");
	this.name = ko.observable("");
	this.created = ko.observable("");
	this.children = ko.observable(0);
	this.invited = ko.observable(0);
	this.inCreation = ko.observable(false);
	this.active = ko.observable(false);
	this.attendees = ko.observableArray();
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
		this.loadChildren();
	}
	
	this.edit = function() {
		_participants.selectedGroup(this);
		this.loadChildren();
		
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
		}		
		
		_participants.Step(1);
		$('#newcontactlist').find('[data-toggle="tooltip"]').tooltip();
		
		_participants.loadAttendees(true);
	}
	
	this.loadChildren = function() {
		var model = this;
		
		model.attendees.removeAll();
		model.users.removeAll();
		model.tokens.removeAll();
				
		$.ajax({
			type:'GET',
			url: contextpath + "/noform/management/children?id=" + model.id(),
			dataType: 'json',
			cache: false,
			success: function( items ) {			  
				for (var i = 0; i < items.length; i++ )
				{
				  if (self.type() == 'Static') {
					  var attendee = items[i];
					  attendee["selected"] = ko.observable(false);
					  self.attendees.push(attendee);
				  } else if (self.type() == 'Token') {
					  var token = items[i];
					  token["selected"] = ko.observable(false);
					  token["deactivated"] = ko.observable(token["deactivated"]);
					  self.tokens.push(token);
				  } else {
					  self.users.push(items[i]);
				  }
				}			  
			}
		});		
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
			  }
		});
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
	this.Attendees = ko.observableArray();
	this.Users = ko.observableArray();
	this.attributeNames = ko.observableArray();
	this.Domain = ko.observable("");
	
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
					  g.active(result[i].active);
					  
					  model.Guestlists.push(g);
					  $('[data-toggle="tooltip"]').tooltip();
				  };
				  
				  model.DataLoaded(true);
			  }
			});
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
			  }
			});
	}
	
	this.loadUsers = function(first) {
		var model = this;
		
		if (first)
		{
			currentPage = 1;
			model.Users.removeAll();
		} else {
			currentPage++;
		}
		
		var s = "name=" + $("#ecnamefilter").val() + "&email=" + $("#ecemailfilter").val() + "&department=" + $("#ecdepartmentfilter").val() + "&newPage=" + currentPage + "&itemsPerPage=" + 20;
		
		var request = $.ajax({
			  url: contextpath + "/noform/management/usersJSON",
			  data: s,
			  dataType: 'json',
			  cache: false,
			  success: function(paging)
			  {			 
				  for (var i = 0; i < paging.items.length; i++ )
				  {	
					  var user = paging.items[i];
					  user["selected"] = ko.observable($('#checkalleccontacts').is(":checked"));
					  model.Users.push(user);
				  };
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
					model.GuestLists.remove( function (item) { return item.id() == model.groupToDelete(); } )
					showSuccess(p_deleted);
				  } else {
					showError(result);
				  }
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
				this.selectedGroup().attendees.push(copy);
			}			
		}
		
		self.ShowWait(false);
	}
	
	this.moveECContacts = function(){
		self.ShowWait(true);
		
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
					this.selectedGroup().users.push(copy);
				}			
			}
		}
	}
	
	this.Save = function() {
		if (!validateInput($('#create-step-2')))
		{
			return;
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
				  if (data == "success") {
					    showSuccess(p_guestlistcreated);
					    self.Page(1);
					    self.loadGuestlists();
					} else {
						showExport(data);
					}
					self.ShowWait(false);
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
	ko.applyBindings(_participants, $("#wait-dialog")[0]);
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
	
	var $col = $("#eccontactsdiv");
	$col.scroll(function(){
		if ($col.innerHeight() >= $col.prop('scrollHeight') - $col.scrollTop())
			_participants.loadUsers(false); 
		
		$('#eccontactshead').scrollLeft($("#eccontactsdiv").scrollLeft());
	});
		
	$("#selectedeccontactsdiv").scroll(function(){	
		$('#selectedeccontactshead').scrollLeft($("#selectedeccontactsdiv").scrollLeft());
	});
	
	$("#numtokens").spinner({ decimals:0, min:1, start:"", allowNull: true });
});

function checkReturn(event, box)
{
	var keycode = (event.keyCode ? event.keyCode : event.which);
    if (keycode == '13') {
        _participants.loadAttendees(true);
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
		}});
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
}

function uncheckallselectedtokens () {
	$('#checkallselectedtokens').removeAttr('checked');	
}
