function wordCloud(selector,w,h,scheme) {

    var fill = d3.scale.category20();
    
    switch (scheme) {
    	case "d3.scale.category10":
    		fill = d3.scale.category10();
    		break;
    	case "d3.scale.category20":
    		fill = d3.scale.category20();
    		break;
    	case "d3.scale.category20b":
    		fill = d3.scale.category20b();
    		break;
    	case "d3.scale.category20c":
    		fill = d3.scale.category20c();
    		break;
    }		    

    //Construct the word cloud's SVG element
    var svg1 = d3.select(selector).append("svg")
        .attr("width", w)
        .attr("height", h);
    var svg = svg1.append("g")
        .attr("transform", "translate(" + (w/2).toString() + "," + (h/2).toString() + ")");


    //Draw the word cloud
    function draw(words) {
        var cloud = svg.selectAll("g text")
                        .data(words, function(d) { return d.text; })
                        
        //Entering words
        cloud.enter()
            .append("text")
            .style("font-family", "sans-serif")
            .style("fill", function(d, i) { return fill(i); })
            .attr("text-anchor", "middle")
            .attr('font-size', 1)
            .text(function(d) { return d.text; });

        //Entering and existing words
        cloud
            .transition()
                .duration(600)
                .style("font-size", function(d) { return d.size + "px"; })
                .attr("transform", function(d) {
                    return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
                })
                .style("fill-opacity", 1);

        //Exiting words
        cloud.exit()
            .transition()
                .duration(200)
                .style('fill-opacity', 1e-6)
                .attr('font-size', 1)
                .remove();

    }


    //Use the module pattern to encapsulate the visualisation code. We'll
    // expose only the parts that need to be public.
    return {

        //Recompute the word cloud for a new set of words. This method will
        // asycnhronously call draw when the layout has been computed.
        //The outside world will need to call this function, so make it part
        // of the wordCloud return value.
        update: function(words) {
            d3.layout.cloud().size([w, h])
                .words(words)
                .padding(5)
                .rotate(function() { return ~~(Math.random() * 2) * 90; })
                .font("sans-serif")
                .fontSize(function(d) { return d.size; })
                .on("end", draw)
                .start();
        },
        getSVG: function() {
        	return svg1;
        }
    }

}

function getWordCloudData(result, f)
{
	var data = [];
	var max = result.data[0].value;
	
    for (var i = 0; i < result.data.length; i++)
    {
    	var p = {};
    	p.text = result.data[i].label;
    	p.size = 10 + result.data[i].value / max * f;
    	data[data.length] = p;
    }
    
    return data;
}

function createWordCloud(div, result, chartType, forResults, scheme) {
	if (result.data.length==0 || chartType == 'None')
	{
		if (forResults) {
			var questionuid = div.data("uid");
			$('#wordcloud' + questionuid).empty();
		}
		
		$(div).closest(".elementwrapper, .statelement-wrapper").find(".chart-wrapper-loader").hide();
		
		return;	
	}	
	
	if (div == null) {
		var modal = $("#delphi-chart-modal-start-page");
		
		if (modal.length == 0) {
			modal =  $("#delphi-chart-modal");
		}
		
		$(modal).find(".modal-body").empty();
		
		div = document.createElement("div");
		$(div).attr("id", "wordcloudmodal").css("height", "600px").attr("width", "800px").addClass("center-block");
		$(modal).find(".modal-body").append(div);
				
		var vis = wordCloud('#wordcloudmodal', 800, 600);
		var data = getWordCloudData(result, 40);
		vis.update(data);
		
		$(modal).modal("show");
	} else {
		var questionuid = div.data("uid");
		$('#wordcloud' + questionuid).empty().show().next().hide();
		$(div).find('.no-graph-image').hide();
		$(div).find('.delphi-chart-expand').show();			
				
		var w = 400;
		var h = 295;
		var f = 10;
	
		var elementWrapper = $(div).closest(".elementwrapper, .statelement-wrapper");
				
		if (forResults) {
			w = 300;
			h = 200;
			$(elementWrapper).find(".chart-type").val(chartType);
			$(elementWrapper).find(".chart-scheme").val(scheme);
			
			var size = $(elementWrapper).find(".chart-size").first().val();
			if (size === 'medium') {
				w = 450;
				h = 330;
				f = 20;
			} else if (size === 'large') {
				w = 600;
				h = 440;
				f = 30;
			}     
		}
		
		var vis = wordCloud('#wordcloud' + questionuid, w, h, scheme);
		var data = getWordCloudData(result, f);
	    vis.update(data);
	    
	    $(elementWrapper).find(".delphi-chart").remove();
		$(elementWrapper).find(".chart-wrapper").show();
		$(elementWrapper).find(".chart-wrapper-loader").hide();
	    
	    if (forResults) {
	    
			$(elementWrapper).find(".chart-controls").show();			
			$(elementWrapper).find("option[data-type='textual']").show();
			$(elementWrapper).find("option[data-type='numerical']").hide();
			
			var button = $(elementWrapper).find(".chart-download");	
			
			setTimeout(function(){ 
	
				var svg = $(div).find("svg")[0];
				
				var svgString = getSVGString(svg);
				svgString2Image( svgString, w, h, save ); // passes Blob and filesize String to the callback
				
				function save( dataBlob ){
					$(button).attr('href', dataBlob);
				}			
			
			}, 1000);
	    }
	}
		
}

//Below are the functions that handle actual exporting:
//getSVGString ( svgNode ) and svgString2Image( svgString, width, height, callback )
function getSVGString( svgNode ) {
	svgNode.setAttribute('xlink', 'http://www.w3.org/1999/xlink');
	var cssStyleText = getCSSStyles( svgNode );
	appendCSS( cssStyleText, svgNode );

	var serializer = new XMLSerializer();
	var svgString = serializer.serializeToString(svgNode);
	svgString = svgString.replace(/(\w+)?:?xlink=/g, 'xmlns:xlink='); // Fix root xlink without namespace
	svgString = svgString.replace(/NS\d+:href/g, 'xlink:href'); // Safari NS namespace fix

	return svgString;

	function getCSSStyles( parentElement ) {
		var selectorTextArr = [];

		// Add Parent element Id and Classes to the list
		selectorTextArr.push( '#'+parentElement.id );
		for (var c = 0; c < parentElement.classList.length; c++)
				if ( !contains('.'+parentElement.classList[c], selectorTextArr) )
					selectorTextArr.push( '.'+parentElement.classList[c] );

		// Add Children element Ids and Classes to the list
		var nodes = parentElement.getElementsByTagName("*");
		for (var i = 0; i < nodes.length; i++) {
			var id = nodes[i].id;
			if ( !contains('#'+id, selectorTextArr) )
				selectorTextArr.push( '#'+id );

			var classes = nodes[i].classList;
			for (var c = 0; c < classes.length; c++)
				if ( !contains('.'+classes[c], selectorTextArr) )
					selectorTextArr.push( '.'+classes[c] );
		}

		// Extract CSS Rules
		var extractedCSSText = "";
		for (var i = 0; i < document.styleSheets.length; i++) {
			var s = document.styleSheets[i];
			
			try {
			    if(!s.cssRules) continue;
			} catch( e ) {
		    		if(e.name !== 'SecurityError') throw e; // for Firefox
		    		continue;
		    	}

			var cssRules = s.cssRules;
			for (var r = 0; r < cssRules.length; r++) {
				if ( contains( cssRules[r].selectorText, selectorTextArr ) )
					extractedCSSText += cssRules[r].cssText;
			}
		}
		

		return extractedCSSText;

		function contains(str,arr) {
			return arr.indexOf( str ) === -1 ? false : true;
		}

	}

	function appendCSS( cssText, element ) {
		var styleElement = document.createElement("style");
		styleElement.setAttribute("type","text/css"); 
		styleElement.innerHTML = cssText;
		var refNode = element.hasChildNodes() ? element.children[0] : null;
		element.insertBefore( styleElement, refNode );
	}
}


function svgString2Image( svgString, width, height, callback ) {
	var imgsrc = 'data:image/svg+xml;base64,'+ btoa( unescape( encodeURIComponent( svgString ) ) ); // Convert SVG string to data URL

	var canvas = document.createElement("canvas");
	var context = canvas.getContext("2d");

	canvas.width = width;
	canvas.height = height;

	var image = new Image();
	
	image.onerror = function(e) {
		console.log("error",e);
	}
	
	image.onload = function() {
		context.clearRect ( 0, 0, width, height );
		context.drawImage(image, 0, 0, width, height);
		var blob = canvas.toDataURL('image/png');
		if ( callback ) callback( blob );
	};
	
	image.src = imgsrc;
}