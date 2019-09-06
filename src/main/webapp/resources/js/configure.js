function configureAttributes()
{
	$("#configure-atttributes-dialog").modal("hide");	
	$("#configure-attributes-form").find("input").each(function(){
		$(this).remove();
	});
	$("configure-atttributes-dialog").find("input:checked").each(function(){
		$("configure-attributes-form").append($(this).attr("style","display: none;"));
	});
	$("#add-attendee-form").submit();
}

function moveLeft()
{
	var attribs = new Array();
	$(".selectedattrib").each(function(){
		attribs.push($(this).attr("name"));
	});	
	
	$(".allattrib").each(function(){
		if ($(this).is(':checked'))
		{
			var id = $(this).attr("name");
			
			if ($.inArray(id, attribs) == -1) //($(".selectedattrib[name=" + id + "]").length == 0)
			{
				var input = document.createElement("input");
				$(input).attr("type","hidden").attr("name", id).attr("value", id).addClass("selectedattrib");
				var span = $(this).parent().find("span").first().clone();
				$(span).prepend('<a onclick="$(this).parent().parent().remove();"><span class="glyphicon glyphicon-remove"></span></a>');
				
				var li = document.createElement("li");
				$(li).addClass("ui-state-default");
				$(li).append(input);
				$(li).append(span);
				$("#sortable").append(li);
			}
		}
		
		
	});
}