var lastshownmessageversion;
var retrievedmessageversion;
var messagedisplaydate;
var currentmessageversion = 0;
var startDate = new Date();
var lastEditDate = new Date();
var usermessage = false;
var usermessageid;

$(function() {
	let lastSystemMessageCheck = 0
	try
	{
		lastshownmessageversion = localStorage.getItem("lastshownmessageversion");
		currentmessageversion = parseInt(localStorage.getItem("currentmessageversion"));

		lastSystemMessageCheck = parseInt(sessionStorage.getItem("lastSystemMessageCheck") ?? "0")
	} catch (e) {};

	const sysMsgTimeout = lastSystemMessageCheck - Date.now() + 5 * 60 * 1000
	window.setTimeout("checkSystemMessages()", Math.max(sysMsgTimeout, 500));
	
	$("input, textarea, select").change(function() {
		  lastEditDate = new Date();
	});
	
	window.onbeforeunload = function(){
		if (messagedisplaydate != null)
		{
			var now = new Date(); 
			var dif = now.getTime() - messagedisplaydate.getTime();
			try
			{
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

	sessionStorage.setItem("lastSystemMessageCheck", Date.now().toString())
	
	$.ajax({
	  url: contextpath + "/administration/system/message",
	  dataType: "json",
	  cache: false,
	  data: d,	  
	  error: function(e)
	  {
		  //this happens when there is no message
		  window.setTimeout("checkSystemMessages()", 5 * 60 * 1000);
		 
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
				  const messageModel = showSystemMessage(message);
				  lastshownmessageversion = retrievedmessageversion;
				  messagedisplaydate = new Date();
				  if (message.time > 0)
				  {
					  window.setTimeout(() => {
						  removeSystemMessage(messageModel)
						  deleteUserMessage()
					  }, message.time * 1000);
				  }		
			  }			  
		  }
		  		  
		  window.setTimeout("checkSystemMessages()", 5 * 60 * 1000);
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

