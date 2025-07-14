<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ include file="includesrunner.jsp" %>

<link href="${contextpath}/resources/css/fileuploader.css" rel="stylesheet" type="text/css" />
	
<script type="text/javascript" src="${contextpath}/resources/js/fileuploader.js?version=<%@include file="version.txt" %>"></script>
<script type="text/javascript" src="${contextpath}/resources/js/importsurvey.js?version=<%@include file="version.txt" %>"></script>

<script type="text/javascript">	

	var myConfigSetting = {
			
			// Location of TinyMCE script
			forced_root_block : '',
			script_url : '${contextpath}/resources/js/tinymce/tinymce.min.js',
			theme : "modern",
			entity_encoding : "raw",
			element_format : "xhtml",
			menubar : false,
			statusbar: true,
			browser_spellcheck: true,
		    
			toolbar : ["bold italic underline strikethrough | alignleft aligncenter alignright alignjustify | indent outdent | undo redo | bullist numlist | link code | fontsizeselect forecolor fontselect"],
		    
				formats: {
			    	alignleft: {selector: 'p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li,table,img', classes: 'myleft', defaultBlock: 'div'},
			    	aligncenter: {selector: 'p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li,table,img', classes: 'mycenter', defaultBlock: 'div'},
			    	alignright: {selector: 'p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li,table,img', classes: 'myright', defaultBlock: 'div'},
			    	alignfull: {selector: 'p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li,table,img', classes: 'myfull', defaultBlock: 'div'}
				},
				content_style: '.myleft { text-align:left; } .mycenter { text-align:center; } .myright { text-align:right; } .myfull { text-align:justify; }',
				
				plugins : "paste link image code textcolor",
			    
			    font_formats: 
	                "Sans Serif=FreeSans, Arial, Helvetica, Tahoma, Verdana, sans-serif;"+
	                "Serif=FreeSerif,Times,serif;"+
	                "Mono=FreeMono,Courier, mono;",
			    
			    image_advtab: true,
			    language : globalLanguage,
			    width: getWidthForTinyMCE(this),
			    height: getHeightForTinyMCE(this),
				entities: "",
				object_resizing : false,
				paste_auto_cleanup_on_paste : true,
				paste_text_sticky: true,
				paste_text_use_dialog: true,
				content_css : "${contextpath}/resources/css/tinymce.css",
				popup_css_add : "${contextpath}/resources/css/tinymcepopup.css",
				forced_root_block : false,
				
				force_br_newlines : true,
				force_p_newlines : false,
				
				resize: true,
					
				paste_postprocess : function(pl, o) {
					  o.node.innerHTML = strip_tags( o.node.innerHTML,'<p><br>' );
					},
					
				init_instance_callback: function (editor) {
				    editor.on('Change', function (e) {
				    	try {
			        		  $('#savetextbutton').removeAttr('disabled');
			        	  	unsavedChanges = true;
			        	  } catch (e) {}
				    });
				},
							
				setup : function(ed) {		          
			          ed.onresizestart=function(){
			        	  return false;
			          };			          
			   },
			   
			   relative_urls : false,
			   remove_script_host : false,
			   document_base_url : serverPrefix,
			   default_link_target: "_blank",
			   anchor_top: false,
			   anchor_bottom: false,
			   branding: false,
			   valid_classes: 'mycenter myleft myright myfull',
			   invalid_elements : 'html,head,body'

		};
	
	var myConfigSettingAlign = {
			
			// Location of TinyMCE script
			forced_root_block : '',
			script_url : '${contextpath}/resources/js/tinymce/tinymce.min.js',
			theme : "modern",
			entity_encoding : "raw",
			element_format : "xhtml",
			menubar : false,
			statusbar: true,
			browser_spellcheck: true,
		    
			toolbar : ["bold italic underline strikethrough | alignleft aligncenter alignright alignjustify | indent outdent | undo redo | bullist numlist | link code | fontsizeselect forecolor fontselect"],
		    
// 				formats: {
// 			    	alignleft: {selector: 'p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li,table,img', classes: 'myleft', defaultBlock: 'div'},
// 			    	aligncenter: {selector: 'p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li,table,img', classes: 'mycenter', defaultBlock: 'div'},
// 			    	alignright: {selector: 'p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li,table,img', classes: 'myright', defaultBlock: 'div'},
// 			    	alignfull: {selector: 'p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li,table,img', classes: 'myfull', defaultBlock: 'div'}
// 				},
// 				content_style: '.myleft { text-align:left; } .mycenter { text-align:center; } .myright { text-align:right; } .myfull { text-align:justify; }',
				
				plugins : "paste link image code textcolor",
			    
			    font_formats: 
	                "Sans Serif=FreeSans, Arial, Helvetica, Tahoma, Verdana, sans-serif;"+
	                "Serif=FreeSerif,Times,serif;"+
	                "Mono=FreeMono,Courier, mono;",
			    
			    image_advtab: true,
			    language : globalLanguage,
			    width: getWidthForTinyMCE(this),
			    height: getHeightForTinyMCE(this),
				entities: "",
				object_resizing : false,
				paste_auto_cleanup_on_paste : true,
				paste_text_sticky: true,
				paste_text_use_dialog: true,
				content_css : "${contextpath}/resources/css/tinymce.css",
				popup_css_add : "${contextpath}/resources/css/tinymcepopup.css",
				forced_root_block : false,
				
				force_br_newlines : true,
				force_p_newlines : false,
				
				resize: true,
					
				paste_postprocess : function(pl, o) {
					  o.node.innerHTML = strip_tags( o.node.innerHTML,'<p><br>' );
					},
					
				init_instance_callback: function (editor) {
				    editor.on('Change', function (e) {
				    	try {
			        		  $('#savetextbutton').removeAttr('disabled');
			        	  	unsavedChanges = true;
			        	  } catch (e) {}
				    });
				},
							
				setup : function(ed) {		          
			          ed.onresizestart=function(){
			        	  return false;
			          };			          
			   },
			   
			   relative_urls : false,
			   remove_script_host : false,
			   document_base_url : serverPrefix,
			   default_link_target: "_blank",
			   anchor_top: false,
			   anchor_bottom: false,
			   branding: false,
			   valid_classes: 'y',
			   invalid_elements : 'html,head,body'

		};

	var myConfigSettingEditor = {
			
			// Location of TinyMCE script
			forced_root_block : false,
			script_url : '${contextpath}/resources/js/tinymce/tinymce.min.js',
			theme : "modern",
			entity_encoding : "raw",
			element_format : "xhtml",
			menubar : false,
			statusbar: true,
			browser_spellcheck: true,
					
			toolbar : ["bold italic underline strikethrough alignleft aligncenter alignright alignjustify indent outdent | undo redo bullist numlist link code forecolor | fontsizeselect fontselect fullscreen"],
		    
			formats: {
		    	alignleft: {selector: 'p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li,table,img', classes: 'myleft', defaultBlock: 'div'},
		    	aligncenter: {selector: 'p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li,table,img', classes: 'mycenter', defaultBlock: 'div'},
		    	alignright: {selector: 'p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li,table,img', classes: 'myright', defaultBlock: 'div'},
		    	alignfull: {selector: 'p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li,table,img', classes: 'myfull', defaultBlock: 'div'}
			},
			content_style: '.myleft { text-align:left; } .mycenter { text-align:center; } .myright { text-align:right; } .myfull { text-align:justify; }',
			
		    plugins : "paste link image code textcolor fullscreen lists",
		    
		    font_formats: 
                "Sans Serif=FreeSans, Arial, Helvetica, Tahoma, Verdana, sans-serif;"+
                "Serif=FreeSerif,Times,serif;"+
                "Mono=FreeMono,Courier, mono;",
		    
		    image_advtab: true,
		    language : globalLanguage,
		 
			entities: "",
			object_resizing : false,
			paste_auto_cleanup_on_paste : true,
			paste_text_sticky: true,
			paste_text_use_dialog: true,
			content_css : "${contextpath}/resources/css/tinymce.css",
			popup_css_add : "${contextpath}/resources/css/tinymcepopup.css",
			
			force_br_newlines : true,
			force_p_newlines : false,
			
			resize: true,
				
			paste_postprocess : function(pl, o) {
				  o.node.innerHTML = strip_tags( o.node.innerHTML,'<p><br>' );
				},
				
			init_instance_callback: function (editor) {
			    editor.on('Change', function (e) {
			    	try {
		        		  $('#savetextbutton').removeAttr('disabled');
		        	  	unsavedChanges = true;
		        	  } catch (e) {}
			    });
			},
						
			setup : function(ed) {	          
		          ed.onresizestart=function(){
		        	  return false;
		          };
		          
		          ed.on('FullscreenStateChanged', function(e) {
		        	  if (e.state)
		        	  {
		        		  $("#editorheader").hide();
		        		  if ($(".btn-primary1").length == 0)
		        		  {
		        		  	$(".mce-i-fullscreen").closest("div").after("<a class='btn btn-primary1' onclick='closeFullScreen(this, true);'><spring:message code="label.Apply" /></a><a class='btn btn-default1' onclick='closeFullScreen(this, false);'><spring:message code="label.Cancel" /></a>")
		        		  };
		        	  } else {
		        		  $("#editorheader").show();
		        		  $(".btn-primary1").remove();
		        		  $(".btn-default1").remove();
		        	  }
		          });
		          
		          ed.on('keydown', function(e) {
		              if (e.keyCode === 27) { // escape
		            	if (ed.plugins.fullscreen.isFullscreen())
		            	{
		                	ed.execCommand('mceFullScreen');
		            	}
		              }
		          });		         
		   },
		   
		   relative_urls : false,
		   remove_script_host : false,
		   document_base_url : serverPrefix,
		   default_link_target: "_blank",
		   anchor_top: false,
		   anchor_bottom: false,
		   branding: false,
		   valid_classes: 'mycenter myleft myright myfull',
		   invalid_elements : 'html,head,body'
		};
	
	var myConfigSetting2 = {
			
			// Location of TinyMCE script
			script_url : '${contextpath}/resources/js/tinymce/tinymce.min.js',
			theme : "modern",
			entity_encoding : "raw",
			element_format : "xhtml",
			menubar : false,
			statusbar: true,
			toolbar : ["bold italic underline strikethrough | undo redo | bullist numlist | link code | fontsizeselect forecolor fontselect"],
		    plugins : "paste link image code textcolor",
		    font_formats: 
                "Sans Serif=FreeSans, Arial, Helvetica, Tahoma, Verdana, sans-serif;"+
                "Serif=FreeSerif,Times,serif;"+
                "Mono=FreeMono,Courier, mono;",
			language : globalLanguage,
		    image_advtab: true,
			width: "510",
			entities: "",
			content_css : "${contextpath}/resources/css/tinymce.css",
			popup_css_add : "${contextpath}/resources/css/tinymcepopup.css",
			forced_root_block : '',
			resize: true,
			browser_spellcheck: true,
			
			paste_postprocess : function(pl, o) {
				  o.node.innerHTML = replaceBRs(strip_tags( o.node.innerHTML,'<p><br>' ));
				},
				
			init_instance_callback: function (editor) {
			    editor.on('Change', function (e) {
			    	try {
		        		  $('#savetextbutton').removeAttr('disabled');
		        	  	unsavedChanges = true;
		        	  } catch (e) {}
			    });
			},
			
		   relative_urls : false,
		   remove_script_host : false,
		   document_base_url : serverPrefix,
		   default_link_target: "_blank",
		   anchor_top: false,
		   anchor_bottom: false,
		   branding: false,
		   valid_classes: 'y',
		   invalid_elements : 'html,head,body'

		};

    var myConfigSettingFull = {

        // Location of TinyMCE script
        script_url : '${contextpath}/resources/js/tinymce/tinymce.min.js',
        theme : "modern",
        entity_encoding : "raw",
        element_format : "xhtml",
        menubar : false,
        statusbar: true,
        toolbar : ["bold italic underline strikethrough | undo redo | bullist numlist | link code | fontsizeselect forecolor fontselect fullscreen"],
        plugins : "paste link image code textcolor fullscreen",
        font_formats:
            "Sans Serif=FreeSans, Arial, Helvetica, Tahoma, Verdana, sans-serif;"+
            "Serif=FreeSerif,Times,serif;"+
            "Mono=FreeMono,Courier, mono;",
        language : globalLanguage,
        image_advtab: true,
        width: "510",
        entities: "",
        content_css : "${contextpath}/resources/css/tinymce.css",
        popup_css_add : "${contextpath}/resources/css/tinymcepopup.css",
        forced_root_block : '',
        resize: true,
        browser_spellcheck: true,

        paste_postprocess : function(pl, o) {
            o.node.innerHTML = replaceBRs(strip_tags( o.node.innerHTML,'<p><br>' ));
        },

        init_instance_callback: function (editor) {
            editor.on('Change', function (e) {
                try {
                    $('#savetextbutton').removeAttr('disabled');
                    unsavedChanges = true;
                } catch (e) {}
            });
        },

        setup : function(ed) {
            ed.on('FullscreenStateChanged', function(e) {
                if (e.state)
                {
                	$('#tinymceconfpage').css("z-index", "10000");
                    $("#editorheader").hide();
                    if ($(".btn-primary1").length == 0)
                    {
                        $(".mce-i-fullscreen").closest("div").after("<a class='btn btn-primary1' onclick='closeFullScreen(this, true);'><spring:message code="label.Apply" /></a><a class='btn btn-default1' onclick='closeFullScreen(this, false);'><spring:message code="label.Cancel" /></a>")
                    };
                } else {
                	$('#tinymceconfpage').css("z-index", "");
                    $("#editorheader").show();
                    $(".btn-primary1").remove();
                    $(".btn-default1").remove();
                }
            });
        },

        relative_urls : false,
        remove_script_host : false,
        document_base_url : serverPrefix,
        default_link_target: "_blank",
        anchor_top: false,
        anchor_bottom: false,
        branding: false,
        valid_classes: 'y',
        invalid_elements : 'html,head,body'

    };

	var myConfigSetting2Editor = {
			
			// Location of TinyMCE script
			script_url : '${contextpath}/resources/js/tinymce/tinymce.min.js',
			theme : "modern",
			entity_encoding : "raw",
			element_format : "xhtml",
			menubar : false,
			statusbar: true,
			toolbar : ["bold italic underline strikethrough undo redo link code fullscreen | fontsizeselect forecolor fontselect"],
		    plugins : "paste link image code textcolor fullscreen",
		    font_formats: 
                "Sans Serif=FreeSans, Arial, Helvetica, Tahoma, Verdana, sans-serif;"+
                "Serif=FreeSerif,Times,serif;"+
                "Mono=FreeMono,Courier, mono;",
			language : globalLanguage,
		    image_advtab: true,
			entities: "",
			content_css : "${contextpath}/resources/css/tinymce2.css",
			popup_css_add : "${contextpath}/resources/css/tinymcepopup.css",
			forced_root_block : 'p',
			resize: true,
			browser_spellcheck: true,
			
			paste_postprocess : function(pl, o) {
				  o.node.innerHTML = replaceBRs(strip_tags( o.node.innerHTML,'<p><br>' ));
				},
				
			init_instance_callback: function (editor) {
			    editor.on('Change', function (e) {
			    	try {
		        		  $('#savetextbutton').removeAttr('disabled');
		        	  	unsavedChanges = true;
		        	  } catch (e) {}
			    });
			},
			
			setup : function(ed) {	          
		          ed.on('FullscreenStateChanged', function(e) {
		        	  if (e.state)
		        	  {
		        		  $("#editorheader").hide();
		        		  if ($(".btn-primary1").length == 0)
		        		  {
		        		  	$(".mce-i-fullscreen").closest("div").after("<a class='btn btn-primary1' onclick='closeFullScreen(this, true);'><spring:message code="label.Apply" /></a><a class='btn btn-default1' onclick='closeFullScreen(this, false);'><spring:message code="label.Cancel" /></a>")
		        		  };
		        	  } else {
		        		  $("#editorheader").show();
		        		  $(".btn-primary1").remove();
		        		  $(".btn-default1").remove();
		        	  }
		          });
		   },

		   relative_urls : false,
		   remove_script_host : false,
		   document_base_url : serverPrefix,
		   default_link_target: "_blank",
		   anchor_top: false,
		   anchor_bottom: false,
		   branding: false,
		   valid_classes: 'y',
		   invalid_elements : 'html,head,body'

		};
	
	$(document).ready(function(){
		
		$(".filtercell").find("input[type=text]").each(function(){
			if ($(this).hasClass("limitedfilter")) {
				$(this).attr("placeholder", "<spring:message code="label.FilterLimited" />")
			} else {
				$(this).attr("placeholder", "<spring:message code="label.Filter" />")
			}
		});
		
		$('textarea.tinymce').each(function(){
            let config = myConfigSetting2
            if ($(this).is(".nolinks")) {
                config = { ...config, plugins: config.plugins.replace("link", "") }
            }
			$(this).tinymce(config);
		});

        $('textarea.tinymcefullscreen').each(function(){
            $(this).tinymce(myConfigSettingFull);
        });

        $('textarea.tinymcealign').each(function(){
			$(this).tinymce(myConfigSetting);
		});
		
		$('textarea.tinymcealign2').each(function(){
			$(this).tinymce(myConfigSettingAlign);
		});
	
		$('textarea.tinymcemessage').tinymce({
			
			// Location of TinyMCE script
			forced_root_block : '',
			script_url : '${contextpath}/resources/js/tinymce/tinymce.min.js',
			theme : "modern",
			entity_encoding : "raw",
			menubar : false,
			statusbar: true,
			toolbar : ["bold italic underline strikethrough forecolor| undo redo | link"],
		    plugins : "paste  link fullscreen",
		    language : globalLanguage,
		    width: "510",
			entities: "",
			object_resizing : false,
			paste_auto_cleanup_on_paste : true,
			paste_text_sticky: true,
			paste_text_use_dialog: true,
			content_css : "${contextpath}/resources/css/tinymce.css",
			popup_css_add : "${contextpath}/resources/css/tinymcepopup.css",
			forced_root_block : false,
			resize: true,
			browser_spellcheck: true,
				
			paste_postprocess : function(pl, o) {
				  o.node.innerHTML = strip_tags( o.node.innerHTML,'<p><br>' );
				},
						
			setup : function(ed) {
		          ed.on("change",function(ed, l) {
		        	  try {
		        	  	unsavedChanges = true;
		        	  } catch (e) {}
		          });
		          
		          ed.onresizestart=function(){
		        	  return false;
		          };
		          
		   },
		   
		   relative_urls : false,
		   remove_script_host : false,
		   document_base_url : serverPrefix,
		   default_link_target: "_blank",
		   anchor_top: false,
		   anchor_bottom: false,
		   branding: false,
		   valid_classes: 'y',
		   invalid_elements : 'html,head,body'

		});
	});

	function getNumberOfPredefinedFilters() {
	     <c:if test="${USER != null && USER.resultAccess != null && USER.resultAccess.resultFilter != null && USER.resultAccess.resultFilter.filterValues != null && USER.resultAccess.resultFilter.filterValues.size() > 0}">
            return ${USER.resultAccess.resultFilter.filterValues.size()};
         </c:if>
         <c:if test="${publication != null && publication.filter != null && publication.filter.filterValues != null && publication.filter.filterValues.size() > 0}">
            return ${publication.filter.filterValues.size()};
         </c:if>
         return 0;
	}

</script>
