function DashboardViewModel()
{
	this.self = this;
	this.sharedOnly = ko.observable(false);
	this.mode = ko.observable("surveys");
	this.surveysMode = ko.observable("simple");
	this.lastEditedSurveyShortname = ko.observable(null);
	this.lastEditedByMeSurveyShortname = ko.observable(null);
	this.lastEditedByMeSurveyDeleted = ko.observable(false);
	this.lastEditedByMeSurveyArchived = ko.observable(false);
	this.latestReplyOn = ko.observable(null);
	this.contributions = ko.observable(null);
	this.surveyStates = ko.observable(null);
	this.surveyContributionStates = ko.observable(null);
	this.surveys = ko.observable(null);
	this.archives = ko.observable(null);
	this.endDates = ko.observable(null);
	this.endDatesReady = ko.observable(false);
	this.surveyIndex = ko.observable(0);
	this.maxSurveyIndex = ko.observable(-1);
	this.surveysPage = ko.observable(1);
	this.lastSurveysReached = ko.observable(false);
	this.personalContributions = ko.observable(null);
	this.contributionsPage = ko.observable(1);
	this.lastContributionsReached = ko.observable(false);
	this.personalDrafts = ko.observable(null);
	this.draftsPage = ko.observable(1);
	this.lastDraftsReached = ko.observable(false);	
	this.personalInvitations = ko.observable(null);
	this.invitationsPage = ko.observable(1);
	this.lastInvitationsReached = ko.observable(false);
	this.contributionStates = ko.observable(null);
	this.surveyFilterStartFrom = ko.observable("");
	this.surveyFilterStartTo = ko.observable("");
	this.surveyFilterEndFrom = ko.observable("");
	this.surveyFilterEndTo = ko.observable("");
	this.archivedFilterCreatedFrom = ko.observable("");
	this.archivedFilterCreatedTo = ko.observable("");
	this.archivedFilterArchivedFrom = ko.observable("");
	this.archivedFilterArchivedTo = ko.observable("");
	this.lastEditFilterFrom = ko.observable("");
	this.lastEditFilterTo = ko.observable("");
	this.contributionFilterEndFrom = ko.observable("");
	this.contributionFilterEndTo = ko.observable("");
	this.lastEditDraftFilterFrom = ko.observable("");
	this.lastEditDraftFilterTo = ko.observable("");
	this.draftFilterEndFrom = ko.observable("");
	this.draftFilterEndTo = ko.observable("");	
	this.invitationFilterEndFrom = ko.observable("");
	this.invitationFilterEndTo = ko.observable("");
	this.invitationFilterDateFrom = ko.observable("");
	this.invitationFilterDateTo = ko.observable("");
	this.sort = ko.observable("");
	this.asc = ko.observable(false);
	this.invitationDataLoaded = false;
	
	this.delphiSurveys = ko.observable(null);
	this.delphiPage = ko.observable(1);
	this.lastDelphiReached = ko.observable(false);
	this.delphiFilterEndFrom = ko.observable("");
	this.delphiFilterEndTo = ko.observable("");
	this.delphiDataLoaded = false;
	
	this.switchToSurveys = function()
	{
		this.mode("surveys");
		writeToLocalstorage("DASHBOARDMode", "surveys");
	}
	
	this.switchToInvitations = function()
	{
		if (!this.invitationDataLoaded)
		{
			_dashboard.loadPersonalContributions(-2);
			_dashboard.loadPersonalDrafts(-2);
			_dashboard.loadPersonalInvitations(-2);
			_dashboard.loadContributionStates();
			
			this.invitationDataLoaded = true;
		}		
		
		this.mode("invitations");
		writeToLocalstorage("DASHBOARDMode", "invitations");
	}
	
	this.switchToDelphi = function()
	{
		if (!this.delphiDataLoaded)
		{
			_dashboard.loadDelphiSurveys(-2);
			this.delphiDataLoaded = true;
		}
		
		this.mode("delphi");
		
		writeToLocalstorage("DASHBOARDMode", "delphi");
	}
	
	this.switchSurveyMode = function(m)
	{
		var oldmode = this.surveysMode();
		this.surveysMode(m);
		if (m != oldmode)
		{
			this.surveysPage(1)
			this.loadSurveys(0);
		}
			
		$(".datepicker").datepicker( "destroy" );
			
		$(".datepicker").each(function(){			
			createDatePicker(this);						
		});
			
		applyEvents();
		writeToLocalstorage("DASHBOARDSurveyMode", m);
	}
	
	this.reloadSurveyWidgets = function() {	
		this.loadMeta();
		this.loadContributions(-2);
		this.loadSurveyStates(-2);
		//load archived surveys
		let oldSurveyMode = this.surveysMode();
		this.switchSurveyMode('archived');
		this.loadSurveys(-2);
		//load surveys
		this.switchSurveyMode(oldSurveyMode);
		this.loadSurveys(-2);
		this.loadEndDates();
	}
	
	this.loadMeta = function()
	{
		var model = this;
		var request = $.ajax({
			  url: contextpath + "/dashboard/metadata",
			  dataType: "json",
			  data: {type: $("#surveystatesselector").val()},
			  cache: false,
			  success: function(data)
			  {			 
				  model.lastEditedSurveyShortname(data[0]);
				  model.latestReplyOn(data[1]);
				  model.lastEditedByMeSurveyShortname(data[2]);
				  model.lastEditedByMeSurveyDeleted(data[3] == "true");
				  model.lastEditedByMeSurveyArchived(data[4] == "true");
				  if (data[5] == "true")
				  {
					model.surveysMode('archived');
					model.loadSurveys(-2);
				  }
				  model.sharedOnly($("#surveystatesselector").val() == "shared");
			  }
			});
	}
	
	this.loadContributions = function(index)
	{
		var model = this;
		model.contributions(null);
		var survey = 0;
		var i = index;
		if (i == -1) i = this.surveyIndex();
		
		if (i == -2) {
			var surveyid = readFromLocalstorage("DASHBOARDContributionsSurvey");
			if (surveyid != null && surveyid.length > 0)
			{
				survey = surveyid;
			}
			
			var s = readFromLocalstorage("DASHBOARDContributionsOrder");
			if (s != null && s.length > 0)
			{
				$("#contributionsorderselector").val(s);
			}
			
			s = readFromLocalstorage("DASHBOARDContributionsSpan");
			if (s != null && s.length > 0)
			{
				$("#contributionsspanselector").val(s);
			}
			
			i = 0;
		}
		var request = $.ajax({
			  url: contextpath + "/dashboard/contributions",
			  data: {survey: i, surveyid: survey, sort: $("#contributionsorderselector").val(), span: $("#contributionsspanselector").val(), type: $("#surveystatesselector").val()},
			  dataType: "json",
			  cache: false,
			  success: function(data)
			  {			 
				  model.surveyIndex(data.surveyIndex);
				  model.contributions(data);
				  model.surveyContributionStates(data.contributionStates);
				  model.updateContributions();				  
			  }
			});
	}
	
	this.loadSurveyStates = function(index)
	{
		writeToLocalstorage("DASHBOARDSurveyStateSelector", $("#surveystatesselector").val());
		
		var model = this;
		model.surveyStates(null);
		var request = $.ajax({
			  url: contextpath + "/dashboard/surveystates",
			  dataType: "json",
			  data: {type: $("#surveystatesselector").val()},
			  cache: false,
			  success: function(data)
			  {			 
				  model.surveyStates(data);
				  setTimeout(function(){  model.updateSurveyStates(); }, 500);				 
			  },
			  error: function(e)
			  {
				  console.log(e);
			  }
			});
	}
	
	this.loadSurveys = function(index)
	{
		if (index == -2)
		{
			var s = readFromLocalstorage("DASHBOARDSurveyMode");
			if (s != null && s.length > 0)
			{
				this.surveysMode(s);
				if (s == "archived")
				{
					$("#surveytypeselector").val(s);
					$("#surveytypeselector").trigger("change")
				}
			}
				
			s = readFromLocalstorage("DASHBOARDSurveysAlias");
			if (s != null && s.length > 0) $("#shortname").val(s);
			
			s = readFromLocalstorage("DASHBOARDSurveysTitle");
			if (s != null && s.length > 0) $("#title").val(s);
			
			s = readFromLocalstorage("DASHBOARDSurveysOwner");
			if (s != null && s.length > 0) $("#owner").val(s);
			
			s = readFromLocalstorage("DASHBOARDSurveysStatus");
			if (s != null && s.length > 0) $("#status").val(s);
			
			s = readFromLocalstorage("DASHBOARDSurveysSecurity");
			if (s != null && s.length > 0) $("#security").val(s);
			
			s = readFromLocalstorage("DASHBOARDSurveysLanguage");
			if (s != null && s.length > 0) $("#language").val(s);
			
			s = readFromLocalstorage("DASHBOARDSurveysStartFrom");
			if (s != null && s.length > 0) this.surveyFilterStartFrom(s);
			
			s = readFromLocalstorage("DASHBOARDSurveysStartTo");
			if (s != null && s.length > 0) this.surveyFilterStartTo(s);
			
			s = readFromLocalstorage("DASHBOARDSurveysEndFrom");
			if (s != null && s.length > 0) this.surveyFilterEndFrom(s);
			
			s = readFromLocalstorage("DASHBOARDSurveysEndTo");
			if (s != null && s.length > 0) this.surveyFilterEndTo(s);
			
			s = readFromLocalstorage("DASHBOARDSurveysArchiveAlias");
			if (s != null && s.length > 0) $("#archiveshortname").val(s);
			
			s = readFromLocalstorage("DASHBOARDSurveysArchiveTitle");
			if (s != null && s.length > 0) $("#archivetitle").val(s);
			
			s = readFromLocalstorage("DASHBOARDSurveysArchivedCreatedFrom");
			if (s != null && s.length > 0) this.archivedFilterCreatedFrom(s);
			
			s = readFromLocalstorage("DASHBOARDSurveysArchivedCreatedTo");
			if (s != null && s.length > 0) this.archivedFilterCreatedTo(s);
			
			s = readFromLocalstorage("DASHBOARDSurveysArchivedArchivedFrom");
			if (s != null && s.length > 0) this.archivedFilterArchivedFrom(s);
			
			s = readFromLocalstorage("DASHBOARDSurveysArchivedArchivedTo");
			if (s != null && s.length > 0) this.archivedFilterArchivedTo(s);
			
		} else {
			writeToLocalstorage("DASHBOARDSurveysAlias", $("#shortname").val());
			writeToLocalstorage("DASHBOARDSurveysTitle", $("#title").val());
			writeToLocalstorage("DASHBOARDSurveysOwner", $("#owner").val());
			writeToLocalstorage("DASHBOARDSurveysStatus", $("#status").val());
			writeToLocalstorage("DASHBOARDSurveysSecurity", $("#security").val());
			writeToLocalstorage("DASHBOARDSurveysLanguage", $("#language").val());
			
			writeToLocalstorage("DASHBOARDSurveysStartFrom",  this.surveyFilterStartFrom());
			writeToLocalstorage("DASHBOARDSurveysStartTo",  this.surveyFilterStartTo());
			writeToLocalstorage("DASHBOARDSurveysEndFrom",  this.surveyFilterEndFrom());
			writeToLocalstorage("DASHBOARDSurveysEndTo",  this.surveyFilterEndTo());
			
			writeToLocalstorage("DASHBOARDSurveysArchiveAlias", $("#archiveshortname").val());
			writeToLocalstorage("DASHBOARDSurveysArchiveTitle", $("#archivetitle").val());
			writeToLocalstorage("DASHBOARDSurveysArchivedCreatedFrom",  this.archivedFilterCreatedFrom());
			writeToLocalstorage("DASHBOARDSurveysArchivedCreatedTo",  this.archivedFilterCreatedTo());
			writeToLocalstorage("DASHBOARDSurveysArchivedArchivedFrom",  this.archivedFilterArchivedFrom());
			writeToLocalstorage("DASHBOARDSurveysArchivedArchivedTo",  this.archivedFilterArchivedTo());
		}
		
		var model = this;

		var params = "page=" +  model.surveysPage() + "&type=" + $("#surveystatesselector").val();
		
		if (model.sort().length > 0)
		{
			params = params + "&sort=" + model.sort() + "&asc=" + model.asc();
		}
		
		if (model.surveysMode() == 'archived')
		{
			model.archives(null);
			var title = $("#archivetitle").val().trim();
			if (title.length > 0)
			{
				params = params + "&title=" + title;
			}
			
			if (this.archivedFilterCreatedFrom().length > 0)
			{
				params = params + "&createdfrom=" + this.archivedFilterCreatedFrom();
			}
			if (this.archivedFilterCreatedTo().length > 0)
			{
				params = params + "&createdto=" + this.archivedFilterCreatedTo();
			}
			
			if (this.archivedFilterArchivedFrom().length > 0)
			{
				params = params + "&archivedfrom=" + this.archivedFilterArchivedFrom();
			}
			if (this.archivedFilterArchivedTo().length > 0)
			{
				params = params + "&archivedto=" + this.archivedFilterArchivedTo();
			}
			
			var shortname = $("#archiveshortname").val().trim();
			if (shortname.length > 0)
			{
				params = params + "&shortname=" + shortname;
			}
					
			var request = $.ajax({
				  url: contextpath + "/dashboard/archives",
				  data: params, //{page: model.surveysPage()},
				  dataType: "json",
				  cache: false,
				  success: function(data)
				  {
					  if (data.length == 0 && model.surveysPage() > 1)
					  {
						  model.surveysPage(model.surveysPage()-1);
						  model.lastSurveysReached(true);
					  } else {
						  model.archives(data);
						  model.updateSurveys();
						  model.lastSurveysReached(data.length < 10);
					  }
				  }
				});
		} else {
			model.surveys(null);
			var title = $("#title").val().trim();
			if (title.length > 0)
			{
				params = params + "&title=" + title;
			}
			var owner = $("#owner").val().trim();
			if (owner.length > 0)
			{
				params = params + "&owner=" + encodeURIComponent(owner);
			}
			var language = $("#language").val().trim();
			if (language.length > 0)
			{
				params = params + "&slanguage=" + language;
			}
			var shortname = $("#shortname").val().trim();
			if (shortname.length > 0)
			{
				params = params + "&shortname=" + shortname;
			}			
			
			if (model.surveysMode() != 'simple')
			{
				var status = $("#status").val().trim();
				if (status.length > 0)
				{
					params = params + "&status=" + status;
				}
				var security = $("#security").val().trim();
				if (security.length > 0)
				{
					params = params + "&security=" + security;
				}
				if (this.surveyFilterStartFrom().length > 0)
				{
					params = params + "&startfrom=" + this.surveyFilterStartFrom();
				}
				if (this.surveyFilterStartTo().length > 0)
				{
					params = params + "&startto=" + this.surveyFilterStartTo();
				}
				if (this.surveyFilterEndFrom().length > 0)
				{
					params = params + "&endfrom=" + this.surveyFilterEndFrom();
				}
				if (this.surveyFilterEndTo().length > 0)
				{
					params = params + "&endto=" + this.surveyFilterEndTo();
				}
			}
			
			if (model.surveysMode() == 'reported')
			{
				params = params + "&reported=true";
			}
			
			if (model.surveysMode() == 'frozen')
			{
				params = params + "&frozen=true";
			}
			
			params = params + "&type=" + $("#surveystatesselector").val();
					
			var request = $.ajax({
				  url: contextpath + "/dashboard/surveys",
				  data: params, //{page: model.surveysPage()},
				  dataType: "json",
				  cache: false,
				  success: function(data)
				  {
					  if (data.length == 0 && model.surveysPage() > 1)
					  {
						  model.surveysPage(model.surveysPage()-1);
						  model.lastSurveysReached(true);
					  } else {
						  model.surveys(data);
						  model.updateSurveys();
						  model.lastSurveysReached(data.length < 10);
						  model.loadAdvancedData(data);
					  }
				  }
				});
		}
	}
	
	this.loadAdvancedData = function()
	{
		var model = this; 
		var ids = "";
		for (var i = 0; i < model.surveys().length; i++)
		{
			if (ids.length > 0) ids += ";";
			ids += model.surveys()[i].uniqueId;
		}
		var request = $.ajax({
			  url: contextpath + "/dashboard/surveysadvanced",
			  data: "ids=" + ids,
			  dataType: "json",
			  cache: false,
			  success: function(data)
			  {
				  for (var i = 0; i < model.surveys().length; i++)
				  {
					  $('#numberinvitations' + model.surveys()[i].uniqueId).html(data[i][0]);
					  $('#numberdrafts' + model.surveys()[i].uniqueId).html(data[i][1]);
				  }
			  }
			});		
	}
	
	this.loadEndDates = function()
	{
		var model = this;
		var request = $.ajax({
			  url: contextpath + "/dashboard/enddates",
			  dataType: "json",
			  data: {type: $("#surveystatesselector").val()},
			  cache: false,
			  success: function(data)
			  {			 
				  model.endDates(data);		
				  model.updateEndDates();
			  }
			});
	}
	
	this.loadPersonalContributions = function(index)
	{
		if (index == -2)
		{
			var s = readFromLocalstorage("DASHBOARDMyLatestContributionsFrom");
			if (s != null && s.length > 0) this.lastEditFilterFrom(s);
			
			s = readFromLocalstorage("DASHBOARDMyLatestContributionsTo");
			if (s != null && s.length > 0) this.lastEditFilterTo(s);
			
			s = readFromLocalstorage("DASHBOARDMyLatestContributionsSurvey");
			if (s != null && s.length > 0) $("#persconsurvey").val(s);
			
			s = readFromLocalstorage("DASHBOARDMyLatestContributionsSurveyStatus");
			if (s != null && s.length > 0) $("#persconsurveystatus").val(s);
			
			s = readFromLocalstorage("DASHBOARDMyLatestContributionsEndFrom");
			if (s != null && s.length > 0) this.contributionFilterEndFrom(s);
			
			s = readFromLocalstorage("DASHBOARDMyLatestContributionsEndTo");
			if (s != null && s.length > 0) this.contributionFilterEndTo(s);
		} else {
			writeToLocalstorage("DASHBOARDMyLatestContributionsFrom", this.lastEditFilterFrom());
			writeToLocalstorage("DASHBOARDMyLatestContributionsTo", this.lastEditFilterTo());
			writeToLocalstorage("DASHBOARDMyLatestContributionsSurvey", $("#persconsurvey").val());
			writeToLocalstorage("DASHBOARDMyLatestContributionsSurveyStatus", $("#persconsurveystatus").val());
			writeToLocalstorage("DASHBOARDMyLatestContributionsEndFrom", this.contributionFilterEndFrom());
			writeToLocalstorage("DASHBOARDMyLatestContributionsEndTo", this.contributionFilterEndTo());
		}
		
		var model = this;
		model.personalContributions(null);
		var params = "page=" +  model.contributionsPage();
		
		if (this.lastEditFilterFrom().length > 0)
		{
			params = params + "&lasteditfrom=" + this.lastEditFilterFrom();
		}
		if (this.lastEditFilterTo().length > 0)
		{
			params = params + "&lasteditto=" + this.lastEditFilterTo();
		}
		var persconsurvey = $("#persconsurvey").val().trim();
		if (persconsurvey.length > 0)
		{
			params = params + "&survey=" + persconsurvey;
		}
		var surveystatus = $("#persconsurveystatus").val().trim();
		if (surveystatus.length > 0)
		{
			params = params + "&surveystatus=" + surveystatus;
		}
		if (this.contributionFilterEndFrom().length > 0)
		{
			params = params + "&endfrom=" + this.contributionFilterEndFrom();
		}
		if (this.contributionFilterEndTo().length > 0)
		{
			params = params + "&endto=" + this.contributionFilterEndTo();
		}
		
		var request = $.ajax({
			  url: contextpath + "/dashboard/personalcontributions",
			  data: params,
			  dataType: "json",
			  cache: false,
			  success: function(data)
			  {
				  if (data.length == 0 && model.contributionsPage() > 1)
				  {
					  model.contributionsPage(model.contributionsPage()-1);
					  model.lastContributionsReached(true);
				  } else {
					  model.personalContributions(data);
					  model.lastContributionsReached(data.length < 10);
					  
					  $('[data-toggle="tooltip"]').tooltip({
							trigger : 'hover'
						});	
				  }				  
				 
			  }
			});
	}
	
	this.loadPersonalDrafts = function(index)
	{
		if (index == -2)
		{
			var s = readFromLocalstorage("DASHBOARDMyDraftsFrom");
			if (s != null && s.length > 0) this.lastEditDraftFilterFrom(s);
			
			s = readFromLocalstorage("DASHBOARDMyDraftsTo");
			if (s != null && s.length > 0) this.lastEditDraftFilterTo(s);
			
			s = readFromLocalstorage("DASHBOARDMyDraftsSurvey");
			if (s != null && s.length > 0) $("#persdraftsurvey").val(s);
			
			s = readFromLocalstorage("DASHBOARDMyDraftsSurveyStatus");
			if (s != null && s.length > 0) $("#persdraftsurveystatus").val(s);
			
			s = readFromLocalstorage("DASHBOARDMyDraftsEndFrom");
			if (s != null && s.length > 0) this.draftFilterEndFrom(s);
			
			s = readFromLocalstorage("DASHBOARDMyDraftsEndTo");
			if (s != null && s.length > 0) this.draftFilterEndTo(s);
		} else {
			writeToLocalstorage("DASHBOARDMyDraftsFrom", this.lastEditDraftFilterFrom());
			writeToLocalstorage("DASHBOARDMyDraftsTo", this.lastEditDraftFilterTo());
			writeToLocalstorage("DASHBOARDMyDraftsSurvey", $("#persdraftsurvey").val());
			writeToLocalstorage("DASHBOARDMyDraftsSurveyStatus", $("#persdraftsurveystatus").val());
			writeToLocalstorage("DASHBOARDMyDraftsEndFrom", this.draftFilterEndFrom());
			writeToLocalstorage("DASHBOARDMyDraftsEndTo", this.draftFilterEndTo());
		}
		
		var model = this;
		model.personalDrafts(null);
		var params = "page=" +  model.draftsPage();
		
		if (this.lastEditDraftFilterFrom().length > 0)
		{
			params = params + "&lasteditfrom=" + this.lastEditDraftFilterFrom();
		}
		if (this.lastEditDraftFilterTo().length > 0)
		{
			params = params + "&lasteditto=" + this.lastEditDraftFilterTo();
		}
		var persdraftsurvey = $("#persdraftsurvey").val().trim();
		if (persdraftsurvey.length > 0)
		{
			params = params + "&survey=" + persdraftsurvey;
		}
		var surveystatus = $("#persdraftsurveystatus").val().trim();
		if (surveystatus.length > 0)
		{
			params = params + "&surveystatus=" + surveystatus;
		}
		if (this.draftFilterEndFrom().length > 0)
		{
			params = params + "&endfrom=" + this.draftFilterEndFrom();
		}
		if (this.draftFilterEndTo().length > 0)
		{
			params = params + "&endto=" + this.draftFilterEndTo();
		}
		
		var request = $.ajax({
			  url: contextpath + "/dashboard/personaldrafts",
			  data: params,
			  dataType: "json",
			  cache: false,
			  success: function(data)
			  {
				  if (data.length == 0 && model.draftsPage() > 1)
				  {
					  model.draftsPage(model.draftsPage()-1);
					  model.lastDraftsReached(true);
				  } else {
					  model.personalDrafts(data);
					  model.lastDraftsReached(data.length < 10);
					  
					  $('[data-toggle="tooltip"]').tooltip({
							trigger : 'hover'
						});	
				  }					
			  }
			});
	}
	
	this.loadPersonalInvitations = function(index)
	{
		if (index == -2)
		{
			var s = readFromLocalstorage("DASHBOARDOpenInvitationsInvitedFrom");
			if (s != null && s.length > 0) this.invitationFilterDateFrom(s);
			
			s = readFromLocalstorage("DASHBOARDOpenInvitationsInvitedTo");
			if (s != null && s.length > 0) this.invitationFilterDateTo(s);
			
			s = readFromLocalstorage("DASHBOARDOpenInvitationsSurvey");
			if (s != null && s.length > 0) $("#persinvitationsurvey").val(s);
			
			s = readFromLocalstorage("DASHBOARDOpenInvitationsSurveyStatus");
			if (s != null && s.length > 0) $("#persinvitationsurveystatus").val(s);
			
			s = readFromLocalstorage("DASHBOARDOpenInvitationsEndFrom");
			if (s != null && s.length > 0) this.invitationFilterEndFrom(s);
			
			s = readFromLocalstorage("DASHBOARDOpenInvitationsEndTo");
			if (s != null && s.length > 0) this.invitationFilterEndTo(s);
		} else {
			writeToLocalstorage("DASHBOARDOpenInvitationsInvitedFrom", this.invitationFilterDateFrom());
			writeToLocalstorage("DASHBOARDOpenInvitationsInvitedTo", this.invitationFilterDateTo());
			writeToLocalstorage("DASHBOARDOpenInvitationsSurvey", $("#persinvitationsurvey").val());
			writeToLocalstorage("DASHBOARDOpenInvitationsSurveyStatus", $("#persinvitationsurveystatus").val());
			writeToLocalstorage("DASHBOARDOpenInvitationsEndFrom", this.invitationFilterEndFrom());
			writeToLocalstorage("DASHBOARDOpenInvitationsEndTo", this.invitationFilterEndTo());
		}
		
		var model = this;
		model.personalInvitations(null);
		var params = "page=" +  model.invitationsPage();
		
		var persinvitationsurvey = $("#persinvitationsurvey").val().trim();
		if (persinvitationsurvey.length > 0)
		{
			params = params + "&survey=" + persinvitationsurvey;
		}
		var surveystatus = $("#persinvitationsurveystatus").val().trim();
		if (surveystatus.length > 0)
		{
			params = params + "&surveystatus=" + surveystatus;
		}
		if (this.invitationFilterEndFrom().length > 0)
		{
			params = params + "&endfrom=" + this.invitationFilterEndFrom();
		}
		if (this.invitationFilterEndTo().length > 0)
		{
			params = params + "&endto=" + this.invitationFilterEndTo();
		}
		
		if (this.invitationFilterDateFrom().length > 0)
		{
			params = params + "&datefrom=" + this.invitationFilterDateFrom();
		}
		if (this.invitationFilterDateTo().length > 0)
		{
			params = params + "&dateto=" + this.invitationFilterDateTo();
		}
		
		var request = $.ajax({
			  url: contextpath + "/dashboard/personalinvitations",
			  data: params,
			  dataType: "json",
			  cache: false,
			  success: function(data)
			  {
				  if (data.length == 0 && model.invitationsPage() > 1)
				  {
					  model.invitationsPage(model.invitationsPage()-1);
					  model.lastInvitationsReached(true);
				  } else {
					  model.personalInvitations(data);
					  model.lastInvitationsReached(data.length < 10);
					  
					  $('[data-toggle="tooltip"]').tooltip({
							trigger : 'hover'
						});	
				  }	
				 
			  }
			});
	}
	
	this.loadContributionStates = function()
	{
		var model = this;
		var request = $.ajax({
			  url: contextpath + "/dashboard/contributionstates",
			  dataType: "json",
			  cache: false,
			  success: function(data)
			  {			 
				  model.contributionStates(data);
				  model.updateContributionStates();				  
			  }
			});
	}
	
	this.firstSurveyPage = function()
	{
		this.surveysPage(1);
		this.loadSurveys(0);
	}
	
	this.previousSurveyPage = function()
	{
		if (this.surveysPage() > 1)
		{
			this.surveysPage(this.surveysPage()-1);
			this.loadSurveys(0);
		}
	}
	
	this.nextSurveyPage = function()
	{
		if (!this.lastSurveysReached())
		{
			this.surveysPage(this.surveysPage()+1);
			this.loadSurveys(0);
		}
	}
	
	this.previousSurvey = function()
	{
		if (this.surveyIndex() > 0)
		{
			this.surveyIndex(this.surveyIndex()-1);
			this.loadContributions(this.surveyIndex());
		}
	}
	
	this.nextSurvey = function()
	{
		if (this.surveyIndex() < this.contributions().surveyIds.length - 1)
		{
			this.surveyIndex(this.surveyIndex()+1);
			this.loadContributions(this.surveyIndex());
		}
	}
	
	this.firstContributionsPage = function()
	{
		this.contributionsPage(1);
		this.loadPersonalContributions(0);
	}
	
	this.previousContributionsPage = function()
	{
		if (this.contributionsPage() > 1)
		{
			this.contributionsPage(this.contributionsPage()-1);
			this.loadPersonalContributions(0);
		}
	}
	
	this.nextContributionsPage = function()
	{
		if (!this.lastContributionsReached())
		{
			this.contributionsPage(this.contributionsPage()+1);
			this.loadPersonalContributions(0);
		}
	}
	
	this.firstInvitationsPage = function()
	{
		this.invitationsPage(1);
		this.loadPersonalInvitations(0);
	}
	
	this.previousInvitationsPage = function()
	{
		if (this.invitationsPage() > 1)
		{
			this.invitationsPage(this.invitationsPage()-1);
			this.loadPersonalInvitations(0);
		}
	}
	
	this.nextInvitationsPage = function()
	{
		if (!this.lastInvitationsReached())
		{
			this.invitationsPage(this.invitationsPage()+1);
			this.loadPersonalInvitations(0);
		}
	}
	
	this.firstDraftsPage = function()
	{
		this.draftsPage(1);
		this.loadPersonalDrafts(0);
	}
	
	this.previousDraftsPage = function()
	{
		if (this.draftsPage() > 1)
		{
			this.draftsPage(this.draftsPage()-1);
			this.loadPersonalDrafts(0);
		}
	}
	
	this.nextDraftsPage = function()
	{
		if (!this.lastDraftsReached())
		{
			this.draftsPage(this.draftsPage()+1);
			this.loadPersonalDrafts(0);
		}
	}
	
	var myContributionBarChart = null;
	this.updateContributions = function()
	{
		var ctx = $("#contributionsChart");

		var points = [];
		for (var i = 0; i < this.contributions().days.length; i++)
		{
			var point = {};
			point.x = this.contributions().days[i];
			point.y = this.contributions().answers[i];
			points[points.length] = point;
		}

		writeToLocalstorage("DASHBOARDContributionsSurvey", this.contributions().surveyId);
		writeToLocalstorage("DASHBOARDContributionsSpan", $("#contributionsspanselector").val());
		writeToLocalstorage("DASHBOARDContributionsOrder", $("#contributionsorderselector").val());

		var a = document.createElement("a");
		$(a).attr("href", contextpath + "/" + this.contributions().surveyTitle + "/management/overview").css("color","#fff");
		$(a).html(this.contributions().surveyTitle);
		$("#contributionssurvey").empty().append($(a));
		
		this.maxSurveyIndex(this.contributions().surveyIds.length-1);
		
		var color = Chart.helpers.color;
		var config = {
			type: 'line',
			data: {			
				datasets: [{
					label: "answers",
					fill: false,
					data: points,
				}]
			},
			options: {
				responsive: true,
				maintainAspectRatio: false,
				legend: {
				    display: false
				},
	            title:{
	                text: "Chart.js Time Scale"
	            },
				scales: {
				    xAxes: [{
				    	type: "time",
						time: {
					        displayFormats: {
					           'day': 'MM-DD'
					        }
						},
						scaleLabel: {
							display: true,
							labelString: getLabel('Dates')
						},
						 ticks: {
							  beginAtZero: false
					        }
					}, ],
					yAxes: [{
						scaleLabel: {
							display: true,
							labelString: getLabel('NumberOfContributions')
						},
						 ticks: {
					          beginAtZero: true,
					          callback: function(value) {if (value % 1 === 0) {return value;}}
					        }
					}]
				},
				tooltips: { 
		          callbacks: {
		              title: function(tooltipItem, data) {
		                  return tooltipItem[0].xLabel.replace(" 00:00","");
		              },
		          }
		        }
			}
		};
		
		if (myContributionBarChart != null) myContributionBarChart.destroy();
		
		myContributionBarChart = new Chart(ctx, config);
		
		this.updateSurveyContributionsChart();
	}
	
	var mySurveyContributionsBarChart = null;
	this.updateSurveyContributionsChart = function()
	{
		var ctx = $("#surveyContributionsChart");
			
		var data = {
			    datasets: [{
			        data: this.surveyContributionStates(),
			        backgroundColor: ["#E5BB00", "#3DCC00", "#D30000", "#777"] //, "#0ff"]
			    }],

			    // These labels appear in the legend and in the tooltips when hovering different arcs
			    labels: [
			    	getLabel('Contributions'),
			    	getLabel('Drafts'),
			    	getLabel('OpenInvitations')
			    ]
			};
		
		if (this.surveyContributionStates() != null)
		{
			if (mySurveyContributionsBarChart != null) mySurveyContributionsBarChart.destroy();
			
			 mySurveyContributionsBarChart = new Chart(ctx,{
			    type: 'horizontalBar',
			    data: data,
			    options: {
					responsive: true,
					maintainAspectRatio: false,
					legend: {
			            display: false
			        },
			        scales: {
						xAxes: [{
					        ticks: {
						          callback: function(value) {if (value % 1 === 0) {return value;}}
						        }
						}]
			        },
			        showAllTooltips: true,
			        tooltips: {
			          custom: function(tooltip) {
			            if (!tooltip) return;
			            // disable displaying the color box;
			            tooltip.displayColors = false;
			          },
			          callbacks: {
			            // use label callback to return the desired label
			            label: function(tooltipItem, data) {
			              return tooltipItem.yLabel + ": " + tooltipItem.xLabel;
			            },
			            // remove title
			            title: function(tooltipItem, data) {
			              return;
			            }
			          }
			        }
			    }
			});
		}
	}
	
	var myBarChart = null;
	this.updateSurveyStates = function()
	{
		var ctx = $("#surveysChart");
			
		var data = {
			    datasets: [{
			        data: this.surveyStates(),
			        backgroundColor: ["#E5BB00", "#3DCC00", "#D30000", "#777", "#0000D3", "#777777"]
			    }],

			    labels: [
			    	getLabel('Published'),
			    	getLabel('Unpublished'),
			    	getLabel('Archived'),
			    	getLabel('PendingChanges'),
			    	getLabel('All')
			    ]
			};
		
		if (myBarChart != null) myBarChart.destroy();
				
		myBarChart = new Chart(ctx,{
		    type: 'horizontalBar',
		    data: data,
		    options: {
				responsive: true,
				maintainAspectRatio: false,
				legend: {
		            display: false
		        },
		        showAllTooltips: true,
		        tooltips: {
		          custom: function(tooltip) {
		            if (!tooltip) return;
		            // disable displaying the color box;
		            tooltip.displayColors = false;
		          },
		          callbacks: {
		            // use label callback to return the desired label
		            label: function(tooltipItem, data) {
		              return tooltipItem.yLabel + ": " + tooltipItem.xLabel;
		            },
		            // remove title
		            title: function(tooltipItem, data) {
		              return;
		            }
		          }
		        }
		    }
		});
	}
	
	this.updateSurveys = function()
	{
		$('[data-toggle="tooltip"]').tooltip({
			trigger : 'hover'
		});	
	}

	this.updateEndDates = function()
	{
		var container = $("#timelineChart")[0];

		var points = [];		
		for (var i = 0; i < this.endDates().days.length; i++)
		{
			for (var j = 0; j < this.endDates().surveyNames[i].length; j++)
			{
				var point = {};
				point.id = points.length;
				point.start = this.endDates().days[i];
				point.content = this.endDates().surveyNames[i][j];
				points[points.length] = point;			
			}
		}
		
		var items = new vis.DataSet(points);
		
		  // Configuration for the Timeline
		  var options = {
			template: function (item, element, data) {
			    return '<a href="' + contextpath + '/' + item.content + '/management/overview">' + item.content + '</a>';
			}
		  };

		  // Hide old timeline
		  $(".vis-timeline").remove();

		  // Create a Timeline
		  var timeline = new vis.Timeline(container, items, options);
		  
		  var model = this;
		  
		  setTimeout(function () {
			  model.endDatesReady(true);
			}, 0);	  	
	}
	
	var myContributionStatesBarChart = null;
	this.updateContributionStates = function()
	{
		var ctx = $("#personalContributionsChart");
			
		var data = {
			    datasets: [{
			        data: this.contributionStates(),
			        backgroundColor: ["#E5BB00", "#3DCC00", "#D30000", "#777"] //, "#0ff"]
			    }],

			    labels: [
			    	getLabel('OpenInvitations'),
			    	getLabel('Contributions'),
			    	getLabel('Drafts')
			    ]
			};
		
		if (myContributionStatesBarChart != null) myContributionStatesBarChart.destroy();
		
		myContributionStatesBarChart = new Chart(ctx,{
		    type: 'horizontalBar',
		    data: data,
		 	options: {
				responsive: true,
				maintainAspectRatio: false,
				legend: {
		            display: false
		        },
				scales: {
					xAxes: [{
						 ticks: {
					          beginAtZero: true,
					          callback: function(value) {if (value % 1 === 0) {return value;}}
					        }
					}]
				},
				showAllTooltips: true,
		        tooltips: {
		          custom: function(tooltip) {
		            if (!tooltip) return;
		            // disable displaying the color box;
		            tooltip.displayColors = false;
		          },
		          callbacks: {
		            // use label callback to return the desired label
		            label: function(tooltipItem, data) {
		              return tooltipItem.yLabel + ": " + tooltipItem.xLabel;
		            },
		            // remove title
		            title: function(tooltipItem, data) {
		              return;
		            }
		          }
		        }
		    }
		});
	}
	
	this.search = function()
	{
		if (this.mode() == "surveys")
		{
			this.loadSurveys(0);
		} else if (this.mode() == "delphi") {
			this.loadDelphiSurveys(0);
		} else {
			this.loadPersonalContributions(0);
		}
	}
	
	this.resetSurveys = function()
	{
		$("#surveyfilterrow").find("input[type=text]").val('');
		$("#surveyfilterrow").find("select").val('');
		this.surveyFilterStartFrom("");
		this.surveyFilterStartTo("");
		this.surveyFilterEndFrom("");
		this.surveyFilterEndTo("");
		this.archivedFilterCreatedFrom("");
		this.archivedFilterCreatedTo("");
		this.archivedFilterArchivedFrom("");
		this.archivedFilterArchivedTo("");
		this.search();
	}
	
	this.resetContributions = function()
	{
		$("#contributionfilterrow").find("input[type=text]").val('');
		$("#contributionfilterrow").find("select").val('');	
		this.lastEditFilterFrom("");
		this.lastEditFilterTo("");
		this.contributionFilterEndFrom("");
		this.contributionFilterEndTo("");
		this.search();
	}
	
	this.resetDrafts = function()
	{
		$("#draftfilterrow").find("input[type=text]").val('');
		$("#draftfilterrow").find("select").val('');	
		this.lastEditDraftFilterFrom("");
		this.lastEditDraftFilterTo("");		
		this.draftFilterEndFrom("");
		this.draftFilterEndTo("");
		_dashboard.draftsPage(1); _dashboard.loadPersonalDrafts(0);
	}
	
	this.resetInvitations = function()
	{
		$("#invitationfilterrow").find("input[type=text]").val('');
		$("#invitationfilterrow").find("select").val('');		
		this.invitationFilterDateFrom("");
		this.invitationFilterDateTo("");
		this.invitationFilterEndFrom("");
		this.invitationFilterEndTo("");
	
		_dashboard.invitationsPage(1); _dashboard.loadPersonalInvitations(0);
	}
	
	this.firstDelphiPage = function()
	{
		this.delphiPage(1);
		this.loadDelphiSurveys(0);
	}
	
	this.previousDelphiPage = function()
	{
		if (this.delphiPage() > 1)
		{
			this.delphiPage(this.delphiPage()-1);
			this.loadDelphiSurveys(0);
		}
	}
	
	this.nextDelphiPage = function()
	{
		if (!this.lastDelphiReached())
		{
			this.delphiPage(this.delphiPage()+1);
			this.loadDelphiSurveys(0);
		}
	}
	
	this.loadDelphiSurveys = function(index)
	{
		if (index == -2)
		{
			var s = readFromLocalstorage("DASHBOARDDelphiSurvey");
			if (s != null && s.length > 0) $("#delphisurvey").val(s);
			
			s = readFromLocalstorage("DASHBOARDDelphiSurveyStatus");
			if (s != null && s.length > 0) $("#delphisurveystatus").val(s);
			
			s = readFromLocalstorage("DASHBOARDDelphiEndFrom");
			if (s != null && s.length > 0) this.delphiFilterEndFrom(s);
			
			s = readFromLocalstorage("DASHBOARDDelphiEndTo");
			if (s != null && s.length > 0) this.delphiFilterEndTo(s);
		} else {
			writeToLocalstorage("DASHBOARDDelphiSurvey", $("#delphisurvey").val());
			writeToLocalstorage("DASHBOARDDelphiSurveyStatus", $("#delphisurveystatus").val());
			writeToLocalstorage("DASHBOARDDelphiEndFrom", this.delphiFilterEndFrom());
			writeToLocalstorage("DASHBOARDDelphiEndTo", this.delphiFilterEndTo());
		}
		
		var model = this;
		model.delphiSurveys(null);
		var params = "page=" +  model.delphiPage();
		
		var delphisurvey = $("#delphisurvey").val().trim();
		if (delphisurvey.length > 0)
		{
			params = params + "&title=" + delphisurvey;
		}
		var surveystatus = $("#delphisurveystatus").val().trim();
		if (surveystatus.length > 0)
		{
			params = params + "&status=" + surveystatus;
		}
		if (this.delphiFilterEndFrom().length > 0)
		{
			params = params + "&endfrom=" + this.delphiFilterEndFrom();
		}
		if (this.delphiFilterEndTo().length > 0)
		{
			params = params + "&endto=" + this.delphiFilterEndTo();
		}
	
		var request = $.ajax({
			  url: contextpath + "/dashboard/opendelphisurveys",
			  data: params,
			  dataType: "json",
			  cache: false,
			  success: function(data)
			  {
				  if (data.length == 0 && model.invitationsPage() > 1)
				  {
					  model.delphiPage(model.delphiPage()-1);
					  model.lastDelphiReached(true);
				  } else {
					  model.delphiSurveys(data);
					  model.lastDelphiReached(data.length < 10);
					  
					  $('[data-toggle="tooltip"]').tooltip({
							trigger : 'hover'
						});	
				  }	
				 
			  }
			});
	}
	
	this.resetDelphiSurveys = function()
	{
		$("#delphifilterrow").find("input[type=text]").val('');
		$("#delphifilterrow").find("select").val('');
		this.delphiFilterEndFrom("");
		this.delphiFilterEndTo("");
		this.search();
	}
}

var _dashboard = new DashboardViewModel();

$(function() {					
	$("#my-tab").addClass("active");
	
	$('[data-toggle="tooltip"]').tooltip({
		trigger : 'hover'
	});
	
	ko.applyBindings(_dashboard, $('#dashboarddiv')[0]);	
	
	if ($("#surveysarea").length == 0)
	{
		_dashboard.switchToInvitations();
	} else {	
		var s = readFromLocalstorage("DASHBOARDMode");
		if (s != null && s.length > 0)
		{
			_dashboard.mode(s);
			if (s == "invitations") {
				_dashboard.switchToInvitations();
			} else if (s == "delphi") {
				_dashboard.switchToDelphi();
			}
		}
		
		s = readFromLocalstorage("DASHBOARDSurveyStateSelector");
		if (s != null && s.length > 0)
		{
			$("#surveystatesselector").val(s);
		}
		
		_dashboard.reloadSurveyWidgets();
	}
	applyEvents();
	
	 $(window).scroll(function() {$(".overlaymenu").hide();});
	  $(window).resize(function() {
		  $(".overlaymenu").hide();
	  });	
	  
	$(document).mouseup(function (e)
	{
		if ($(e.target).hasClass("overlaybutton") || $(e.target).closest(".overlaybutton").length > 0)
		{
			e.stopPropagation();
			return;
		}				
		
	    var container = $(".overlaymenu");

	    if (!container.is(e.target) // if the target of the click isn't the container...
	        && container.has(e.target).length === 0) // ... nor a descendant of the container
	    {
	        container.hide();
	    }
	});
});

function sort(col, asc)
{
	_dashboard.sort(col);
	_dashboard.asc(asc);
	_dashboard.search();	
}

function applyEvents()
{
	$("input[type=text]").keyup(function(e) {
	    if(e.keyCode == 13){
			_dashboard.search(); 
		 }	
	 });
}
		
function applyDateFilter(id, value)
{
	switch(id)
	{
		case 'metafilterstartdatefromdiv':
			_dashboard.surveyFilterStartFrom(value);
			break;
		case 'metafilterstartdatetodiv':
			_dashboard.surveyFilterStartTo(value);
			break;
		case 'metafilterenddatefromdiv':
			_dashboard.surveyFilterEndFrom(value);
			break;
		case 'metafilterenddatetodiv':
			_dashboard.surveyFilterEndTo(value);
			break;
		case 'metafiltercreateddatefromdiv':
			_dashboard.archivedFilterCreatedFrom(value);
			break;
		case 'metafiltercreateddatetodiv':
			_dashboard.archivedFilterCreatedTo(value);
			break;
		case 'metafilterarchiveddatefromdiv':
			_dashboard.archivedFilterArchivedFrom(value);
			break;
		case 'metafilterarchiveddatetodiv':
			_dashboard.archivedFilterArchivedTo(value);
			break;
		case 'metafilterlasteditdatefromdiv':
			_dashboard.lastEditFilterFrom(value);
			break;
		case 'metafilterlasteditdatetodiv':
			_dashboard.lastEditFilterTo(value);
			break;
		case 'metafiltercontributionenddatefromdiv':
			_dashboard.contributionFilterEndFrom(value);
			break;
		case 'metafiltercontributionenddatetodiv':
			_dashboard.contributionFilterEndTo(value);
			break;
		case 'metafilterlasteditdraftdatefromdiv':
			_dashboard.lastEditDraftFilterFrom(value);
			break;
		case 'metafilterlasteditdraftdatetodiv':
			_dashboard.lastEditDraftFilterTo(value);
			break;
		case 'metafilterdraftenddatefromdiv':
			_dashboard.draftFilterEndFrom(value);
			break;
		case 'metafilterdraftenddatetodiv':
			_dashboard.draftFilterEndTo(value);
			break;
		case 'metafilterinvitationdatefromdiv':
			_dashboard.invitationFilterDateFrom(value);
			break;
		case 'metafilterinvitationdatetodiv':
			_dashboard.invitationFilterDateTo(value);
			break;
		case 'metafilterinvitationenddatefromdiv':
			_dashboard.invitationFilterEndFrom(value);
			break;
		case 'metafilterinvitationenddatetodiv':
			_dashboard.invitationFilterEndTo(value);
			break;
		case 'metafilterdelphienddatefromdiv':
			_dashboard.delphiFilterEndFrom(value);
			break;
		case 'metafilterdelphienddatetodiv':
			_dashboard.delphiFilterEndTo(value);
			break;
	}
	$(".overlaymenu").hide();
}

function writeToLocalstorage(name,value) {	
	if (value != null && typeof value != "undefined"
			&& (typeof value != "string" || value.length > 0)) {
		try {
			localStorage.setItem(name + userid, value);
		} catch(e) {
		    if(e.name == "NS_ERROR_FILE_CORRUPTED") {
		    	showError("Sorry, it looks like your browser storage has been corrupted. Please clear your storage by going to Tools -> Clear Recent History -> Cookies and set time range to 'Everything'. This will remove the corrupted browser storage across all sites.");
		    }
		}		
	 } else {
		 localStorage.removeItem(name + userid);
	 }
}

function readFromLocalstorage(name) {
	return localStorage.getItem(name + userid);
}
				