var lastshownmessageversion;
var retrievedmessageversion;
var alreadydoneseconds = 0;
var messagedisplaydate;
var currentmessageversion = 0;
var startDate = new Date();
var lastEditDate = new Date();
var usermessage = false;
var usermessageid;

$(function() {
	try
	{
		lastshownmessageversion = localStorage.getItem("lastshownmessageversion");
		alreadydoneseconds = parseInt(localStorage.getItem("alreadydoneseconds"));
		currentmessageversion = parseInt(localStorage.getItem("currentmessageversion"));
	} catch (e) {};
	window.setTimeout("checkSystemMessages()", 500);
	
	$("input, textarea, select").change(function() {
		  lastEditDate = new Date();
	});
	
	window.onbeforeunload = function(){
		if (messagedisplaydate != null)
		{
			var now = new Date(); 
			var dif = now.getTime() - messagedisplaydate.getTime();
			alreadydoneseconds = alreadydoneseconds + Math.round(dif / 1000);
			try
			{
				localStorage.setItem("alreadydoneseconds", alreadydoneseconds);
				localStorage.setItem("currentmessageversion", currentmessageversion);
			} catch (e) {};
		}
	};
});

function checkSystemMessages()
{
	var now = new Date();
	var dif = now.getTime() - lastEditDate.getTime();
	if (dif > 30 * 60 * 1000) //longer than 30 minutes
	{
		return;
	}
	
	var d = "runnermode=false";
	
	if ($("#mode").length > 0 && $("#mode").val() == 'runner')
	{
		d = "runnermode=true";
	}
	
	$.ajax({
	  url: contextpath + "/administration/system/message",
	  dataType: "json",
	  cache: false,
	  data: d,	  
	  error: function(e)
	  {
		  //this happens when there is no message
		  window.setTimeout("checkSystemMessages()", 60000);
	  },	  
	  success: function(message)
	  {
		  if (message != null)
		  {
			  if (currentmessageversion != message.version)
			  {
				  alreadydoneseconds = 0;
				  try
				  {
					localStorage.setItem("alreadydoneseconds", alreadydoneseconds);
				  } catch (e) {};
			  }
			  
			  retrievedmessageversion = message.version;
			  currentmessageversion = retrievedmessageversion;
			  
			  usermessage = message.userId != null;
			  			  
			  if (usermessage)
			  {
				  $("#btnDeleteUserMessage").show();
				  usermessageid = message.id;
			  } else {
				  $("#btnDeleteUserMessage").hide();
			  }
			  
			  $("#system-message-box").addClass(message.css);
			  $("#system-message-box-icon").attr("src", contextpath + "/resources/images/" + message.icon);
			  $("#system-message-box-content").html(message.text);
			  
			  if (usermessage || retrievedmessageversion != lastshownmessageversion)
			  {
				  $("#system-message-box").show();
				  messagedisplaydate = new Date();
				  if (message.time > 0)
				  {
					  window.setTimeout("hideSystemMessage()", (message.time - alreadydoneseconds) * 1000);
				  }		
				  
				  $(".user-info").css("top", ($("#system-message-box").outerHeight() + 25) + "px");
			  } else {
				  $(".user-info").css("top", "5px");
			  }
			  
			  $("#systemmessagebutton").show();
		  } else {
			  $("#systemmessagebutton").hide();
		  }
		  
		  window.setTimeout("checkSystemMessages()", 60000);
	  }
	});
}

function hideSystemMessage()
{
	try
	{
		localStorage.setItem("lastshownmessageversion", retrievedmessageversion);
	} catch (e) {};
	lastshownmessageversion = retrievedmessageversion;
	messagedisplaydate = null;
	$("#system-message-box").hide();
}
	
function deleteUserMessage()
{
	if (usermessage)
	{
		var d = "id=" + usermessageid;		
	
		$.ajax({
			  url: contextpath + "/administration/system/deletemessage",
			  dataType: "json",
			  cache: false,
			  data: d
		});
	}
	
	hideSystemMessage();
}

