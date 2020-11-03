$(function() {
	var lastUl = null;
	var i = 0;
	var j = 0;
	$('#faqcontent').find('h1, h2').each(function(index){
		var li = document.createElement("li");
		
		if ($(this).is('h1')) {
			i++;
			j = 0;
			
			$(li).addClass("anchorlink").append("<a href='#_Toc_" + i + "_" + j + "'>" + $(this).text() + "</a>");
			
			if (!$(this).hasClass('empty'))
			{
				$(li).addClass("head");
				lastUl = document.createElement("ul");
				$(li).append(lastUl);
			} else {
				$(li).addClass("empty");
			}
			
			$('#treemenu').append(li);
		} else {
			j++;
			$(li).addClass("anchorlink").append("<a href='#_Toc_" + i + "_" + j + "'>" + $(this).text() + "</a>");
			$(lastUl).append(li);
		}
		
		var a = document.createElement("a");
		$(a).addClass("anchor").attr("name", "_Toc_" + i + "_" + j);
		$(this).prepend(a);
		
		if ($(this).is('h2')) {
			var a2 = document.createElement("a");
			$(a2).attr("href", "#topAnchor").addClass("anchorlink anchorTop").css("text-decoration", "none;").html("Top of the page&nbsp;<i class='icon icon-chevron-up'></i>");
			$(this).append(a2);
		}
		
	});
	
	ddtreemenu.createTree("treemenu", false, 0, contextpath);
	
	 $("a.anchorTop").click(function(){
		 $('html, body').animate({scrollTop : 0},100);
			return false;
	 });
	 
});