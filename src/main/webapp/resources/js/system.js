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
		 
		  // refresh if available
		  if (typeof refreshTimeout === "function") { 
			  refreshTimeout();
		  }
	  },	  
	  success: function(message)
	  {
		  // refresh if available
		  if (typeof refreshTimeout === "function") { 
			  refreshTimeout();
		  }
		  
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
				  if (usermessageid == message.id)
				  {
					  //the message was already shown
					  return;
				  }
				  usermessageid = message.id;
			  }
			  
			  if (usermessage || retrievedmessageversion != lastshownmessageversion)
			  {
				  showSystemMessage(message);
				  lastshownmessageversion = retrievedmessageversion;
				  messagedisplaydate = new Date();
				  if (message.time > 0)
				  {
					  window.setTimeout("hideSystemMessage()", (message.time - alreadydoneseconds) * 1000);
				  }		
			  }			  
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

