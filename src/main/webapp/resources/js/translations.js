var currentTranslation;
var currentLang;
var currentButton;

function checkAddLanguage()
{
	$("#unknown-language-error").hide();
	$("#add-translation-dialog-error").hide();
	$("#unsupported-language-error").hide();
	
	if ($("#lang").val() == "select")
	{
		$("#add-translation-dialog-error").show();
		return false;
	}
	
	if ($("#lang").val() == "other")
	{
		var code = $("#code").val().trim().toUpperCase();
		if (code.length == 0)
		{
			$("#add-translation-dialog-error").show();
			return false;
		} else if (languagecodes.indexOf(code) < 0)
		{
			$("#unknown-language-error").show();
			return false;
		}
		
		if ($('#mtrequestcheck').length > 0 && $('#mtrequestcheck').is(":checked"))
		{
			if ($("#lang").find("option[value='" + code + "']").length == 0) {
				$("#unsupported-language-error").show();
				return false;
			}
		}
	}
	return true;
}

function deleteSingleTranslation(id)
{
	currentTranslation = id;
	$("#ask-delete-translation-dialog").modal("show");
}

function requestSingleTranslation(button,id)
{
	currentButton = button;
	currentTranslation = id;
	
	var tableRow = $("#translationsTable").find("#" +id);
	var requested = tableRow.attr("data-requested");
	currentLang = tableRow.attr("data-lang");
	if (requested ==="true") {
		$("#current-lang-cancel").text(currentLang);
		$("#ask-cancel-translation-dialog").modal("show");
	}else {
		$("#current-lang-request").text(currentLang);
		$("#ask-request-translation-dialog").modal("show");
	}	
}

function cancelTranslation()
{
	$("#ask-cancel-translation-dialog").modal("hide");
	$.ajax({
		  url: contextpath + "/noform/management/canceltranslation",
		  data: {translationId : currentTranslation},
		  dataType: "json",
		  cache: false,
		  success: function(data)
		  {			 
			if (data.success)
			{
				updateCurrentTranslation(true);
			} else {
				if (data.m == 1) 
				{
					showError(deleteTranslation1);
				} else {
					showError(deleteTranslation0);
				}
			}		
		  }
		});
}
function deleteTranslations()
{
	var ids = "";
	$("#translationsTable").find("tr").each(function(){
		var id = $(this).attr("id");
		
		if ($("#check" + id).is(":checked"))
		{
			ids = ids + id + "|";
		}
	});
	$("#ask-delete-translations-dialog").modal("hide");
	$.ajax({
		  url: contextpath + "/noform/management/deletetranslations",
		  data: {translationIds : ids},
		  dataType: "json",
		  cache: false,
		  success: function(data)
		  {			 
			  if (data.success)
				{
					$("#translationsTable").find("tr").each(function(){
						var id = $(this).attr("id");
						
						if ($("#check" + id).is(":checked"))
						{
							 $("#check" + id).closest("tr").remove();
						}
					});
					 
				} else {
				if (data.m == 1) 
				{
					showError(deleteTranslation1);
				} else {
					showError(deleteTranslation0);
				}
			}		
		  }
		});
}

function deleteTranslation()
{
	$("#ask-delete-translation-dialog").modal("hide");
	$.ajax({
		  url: contextpath + "/noform/management/deletetranslation",
		  data: {translationId : currentTranslation},
		  dataType: "json",
		  cache: false,
		  success: function(data)
		  {			 
			if (data.success)
			{
				  $("#check" + currentTranslation).closest("tr").remove();
			} else {
				if (data.m == 1) 
				{
					showError(deleteTranslation1);
				} else {
					showError(deleteTranslation0);
				}
			}		
		  }
		});
}

function translateTranslations()
{
	var ids = "";
	$("#translationsTable").find("tr").each(function(){
		var id = $(this).attr("id");
		
		if ($("#check" + id).is(":checked"))
		{
			ids = ids + id + "|";
		}
	});
	$("#ask-request-translations-dialog").modal("hide");
	$.ajax({
		  url: contextpath + "/noform/management/translatetranslations",
		  data: {translationIds : ids},
		  dataType: "json",
		  cache: false,
		  success: function(data)
		  {	
			  if (data.success)
			  	{
				  $("#translationsTable").find("tr").each(function(){
						var id = $(this).attr("id");
						var active = $(this).attr("data-complete");
						
						if ($("#check" + id).is(":checked") )
						{
							if (active != "true")
							{
								updateTranslation($("#translationsTable").find("#" +currentTranslation), false, $("#check" + id).closest("tr"));
							}
							
						}
					});
				  
				  	showSuccess(requestTranslationSucces);
	            } else {
	            	showError(requestTranslationError);
	            }
			  }
		});
}

function translateTranslation()
{
	var pivotlang = $("#pivotlangs").val();
	var ids = pivotlang + "|" + currentTranslation;
	
	$("#ask-request-translation-dialog").modal("hide");
	$.ajax({
		  url: contextpath + "/noform/management/translatetranslations",
		  data: {translationIds : ids},
		  dataType: "json",
		  cache: false,
		  success: function(data)
		  {	
			  if (data.success)
			  	{
				    updateCurrentTranslation(false);
				    showSuccess(requestTranslationSucces);
	            } else {
	            	showError(requestTranslationError);
	            }
			  }
		});
}

function updateTranslation(invisibleTableRow, canceled, tableRow)
{
	if (canceled) 
	{
		invisibleTableRow.attr("data-requested","false");
	}
	else
	{
		invisibleTableRow.attr("data-requested","true");
	}
	
	if (canceled) 
	{
		tableRow.find(".label").hide();
		tableRow.find(".error").show();
	}else 
	{
		tableRow.find(".label").hide();
		tableRow.find(".requested").show();
	}
}

function updateCurrentTranslation(canceled) {
	
	var tableRow = $("#translationsTable").find("#" +currentTranslation);
	updateTranslation(tableRow, canceled, $("#check" + currentTranslation).closest("tr"));	
}

function editSingleTranslation(button)
{
	$(".translationselector").removeAttr("checked");
	$(button).closest("tr").find(".translationselector").prop("checked", "checked");
	$(button).closest("table").find("tr.pivot").find(".translationselector").prop("checked", "checked");
	initEditMatrix();
}

function executeOperation()
{
	if ($("#translation-action").val() == 'edit')
	{
		//check if at least one active translations is selected
		var ok = false;
		$("#translationsTable").find("tr").each(function(){
			var id = $(this).attr("id");
			var active = $(this).attr("data-complete");
			
			if ($("#check" + id).is(":checked") && active == "true")
			{
				ok = true;
			}
		});
		
		if (!ok)
		{
			$("#translations").find("tr.pivot").find(".translationselector").prop("checked", "checked");
			ok = true;
		}
		
		if (!ok)
		{
			$("#select-active-translation-dialog").modal("show");
		} else {
			initEditMatrix();
		}
	} else if ($("#translation-action").val() == 'delete')
	{	
		//check if at least one active translations is not selected
		var ok = false;
		var found = false;
		var pivot = false;
		$("#translationsTable").find("tr").each(function(){
			var id = $(this).attr("id");
			var active = $(this).attr("data-complete");
			
			if ($("#check" + id).is(":checked"))
			{
				if ($("#check" + id).hasClass("pivot"))
				{
					pivot = true;
					return;
				} else {
					found = true;
				}				
			}
			
			if ($("#check" + id).is(":checked") || active == "false")
			{
				
			} else {
				ok = true;
			}
		});
		
		if (pivot)
		{
			$("#delete-pivot-translation-dialog").modal("show");
			return false;
		}
		
		if (!ok)
		{
			$("#keep-active-translation-dialog").modal("show");
		} else {
			
			if (!found)
			{
				$("#select-one-translation-dialog").modal("show");
				return;
			}
			
			$("#ask-delete-translations-dialog").modal("show");
		}
	}else if ($("#translation-action").val() == 'translate')
	{
		var countActive = 0;
		//check if at least one not active translations is selected
		var countNotActive = 0;
		$("#translationsTable").find("tr").each(function(){
			var id = $(this).attr("id");
			var active = $(this).attr("data-complete");
			
			if ($("#check" + id).is(":checked") )
			{
				if (active == "true")
				{
					++countActive; 
				}else {
					++countNotActive;
				}
				
			}
		});
		
		if ( countActive === 1 && countNotActive >0 )
		{
			$("#ask-request-translations-dialog").modal("show");
		} 
		else {
			$("#select-complete-and-more-incompleate-translation-dialog").modal("show");
		}
	}
	
}

function switchColumns(th, back)
{
	var index = $(th).parent("tr").children().index($(th));
	var stop = false;
	if (back)
	{		
		if (index > 0)
		{
			$(th).parent("tr").find("th").each(function(){
				var myIndex = $(this).parent("tr").children().index($(this));
				
				if (!stop && myIndex == index - 1)
				{
					$(this).before($(this).next());
					stop = true;			
				}
			});
			
			$(th).closest("table").find("tr").each(function(){	
				stop = false;
				$(this).find("td").each(function(){				
					var myIndex = $(this).parent("tr").children().index($(this));
					if (!stop && myIndex == index - 1)
					{
						$(this).before($(this).next());
						stop = true;			
					}				
				});
			});		
		}		
		//addArrows($(th).prev());		
	} else {
		if (index < $(th).parent("tr").children().length -1)
		{
			$(th).parent("tr").find("th").each(function(){
				var myIndex = $(this).parent("tr").children().index($(this));
				
				if (!stop && myIndex == index)
				{
					$(this).next().after($(this));
					stop = true;				
				}
			});
			
			$(th).closest("table").find("tr").each(function(){	
				stop = false;
				$(this).find("td").each(function(){			
					var myIndex = $(this).parent("tr").children().index($(this));
					if (!stop && myIndex == index)
					{
						$(this).next().after($(this));
						stop = true;
					}
				});
			});
		}
		
		//addArrows($(th).next());
		
	}
	
	//addArrows(th);
	var trHead = $(th).parent("tr");
	$(trHead).find(".glyphicon-arrow-left").parent().show();
	$(trHead).find(".glyphicon-arrow-right").parent().show();
	$(trHead).find(".glyphicon-arrow-left").first().parent().hide();
	$(trHead).find(".glyphicon-arrow-right").last().parent().hide();
}

function switchActive(id, input)
{
	var s = "id=" + id;
	if ($(input).find(".glyphicon-pause").length > 0)
	{
		$.ajax({
	         type: "POST",
	         url: contextpath + "/noform/management/deactivatetranslation",
	         data: s,
	         beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
	         success: function(simpleResult)
	         {
	            if (simpleResult.success)
	            {
	            	showSuccess(simpleResult.result);
	            	
	            	$(input).find(".glyphicon-pause").removeClass("glyphicon-pause").addClass("glyphicon-play");
	            	$(input).attr("data-original-title", $(input).attr("data-activetitle")).tooltip();
	            	
	            	$(input).closest("tr").find(".label-success").hide();
	            	$(input).closest("tr").find(".label-warning").not(".requested").show();
	            	$(input).attr("title", labelPublish);
	            	
	            } else {
	            	showError(simpleResult.result);
	            }
	         }
	       });
	} else {
		$.ajax({
	         type: "POST",
	         url: contextpath + "/noform/management/activatetranslation",
	         data: s,
	         beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
	         success: function(simpleResult)
	         {
	        	if (simpleResult.success)
	            {
	            	showSuccess(simpleResult.result);
	            	
	            	$(input).find(".glyphicon-play").removeClass("glyphicon-play").addClass("glyphicon-pause");
	            	$(input).attr("data-original-title", $(input).attr("data-inactivetitle"));
	            	
	            	$(input).closest("tr").find(".label-success").show();
	            	$(input).closest("tr").find(".label-warning").hide();
	            	$(input).attr("title", labelUnpublish);
	            } else {
	            	showError(simpleResult.result);
	            }
	         }
	       });
	}
	
	 
}

function addArrows(th)
{
	var backDiv = document.createElement("div");
	$(backDiv).attr("style", "float: left");
	var back = document.createElement("a");
	$(back).css("cursor", "pointer").click(function(){
		switchColumns(th, true);
	});
	var i = document.createElement("span");
	$(i).addClass("glyphicon glyphicon-arrow-left");
	$(back).append(i);	
	$(backDiv).append(back);	
	
	var nextDiv = document.createElement("div");
	$(nextDiv).attr("style", "float: right");
	var next = document.createElement("a");
	$(next).css("cursor", "pointer").click(function(){
		switchColumns(th, false);
	});
	i = document.createElement("span");
	$(i).addClass("glyphicon glyphicon-arrow-right");
	$(next).append(i);	
	$(nextDiv).append(next);	
	
	$(th).find("a").remove();
	$(th).find("div").remove();
	$(th).prepend(backDiv);
	$(th).prepend(nextDiv);
}

function initEditMatrix()
{
	$("#edit-translations-table").find("thead").empty();
	$("#edit-translations-table").find("tbody").empty();


	var trHead = document.createElement("tr");	
	
	var arrows = false;
	var counter = 0;
	$("#translationsTable").find("tr").each(function(){
		var id = $(this).attr("id");
		
		if ($("#check" + id).is(":checked"))
		{
			counter++;
			if (counter > 1)
			{
				arrows = true;
			}
		}
	});
	
	//if (!arrows)
	//{
		//add info columns
		th = document.createElement("th");
		$(th).attr("style","text-align: center;");		
		$(th).append(LabelType);	
		$(trHead).append(th);
	//}
		
	$("#translationsTable").find("tr").each(function(){
		var id = $(this).attr("id");
		var lang = $(this).attr("data-lang");
		
		if ($("#check" + id).is(":checked"))
		{
			th = document.createElement("th");
			$(th).attr("style","text-align: center; min-width: 100px;").append(lang);
			
			if (arrows)
			{
				addArrows(th);
			}
			
			$(trHead).append(th);
		}
	});
	
	$(trHead).find(".glyphicon-arrow-left").parent().show();
	$(trHead).find(".glyphicon-arrow-right").parent().show();
	$(trHead).find(".glyphicon-arrow-left").first().parent().hide();
	$(trHead).find(".glyphicon-arrow-right").last().parent().hide();
	
	var w = 100 / $(trHead).find("th").length;
	if (!arrows)
	{
		w = 75 / ($(trHead).find("th").length-1);
	}
	
	$(trHead).find("th").css("width", w + "%");
	if (!arrows)
	{
		$(trHead).find("th").first().css("width", "25%");
	}
	
	var pivotRow = $("#translationsTable").find("tr[data-complete='true']").first();
	
	//use pivot language to initialize the table
	for (var i = 0; i < $(pivotRow).find("td").length; i++)
	{
		var cell = $(pivotRow).find("td")[i];
		var key = $(cell).attr("data-key");
		
		var tr = document.createElement("tr");
		var td = document.createElement("td");
		$(td).append(getInfo(key));
		$(tr).append(td);

		$("#translationsTable").find("tr").each(function(index){
			
			var translationsId = $(this).attr("id");
			var active = $(this).attr("data-active");
			var locked = ($(this).find('td[data-key="' + key + '"]').attr("data-locked"));

			if ($("#check" + translationsId).is(":checked"))
			{
				
				if (translationsId != "0" )
				{
					td = document.createElement("td");

					if(locked != null && locked == 'true'){
						$(td).addClass("locked");
					}
					
					if (active != null && active == 'true')
					{
						$(td).addClass("activetranslation");
					}
					
					if ($(this).attr("data-lang") == $('#surveylanguage').val())
					{
						if (!endsWith(key, "UNIT") && !endsWith(key, "help"))
						{
							$(td).addClass("pivotmandatory");
						}
					}
					
					if (key == "CONFIRMATIONLINK" || key == "ESCAPELINK")
					{
						$(td).addClass("translink");
					}

					// create editor
					var input = document.createElement("textarea");
					$(input).attr("style","display:none;");
					var div = document.createElement("div");
					$(div).addClass("questiontitle").css("max-width","600px");

					var translationId = "";
					
					var labels = $(this).find('td[data-key="' + key + '"]');
					if ($(labels).length > 0)
					{
						var t = $(labels).last().find("textarea").first().text();
						
						translationId = $(labels).last().attr("id");
						$(input).text(t);
						$(div).html(t);
						
						if (t.length == 0)
						{
							if (!endsWith(key, "UNIT") && !endsWith(key, "help"))
							{
								$(td).css("background-color","#FFBABA");
							}
							$(div).append("&#160;");							
						} else {
							$(tr).show();
						}
						
					} else {
						if (!endsWith(key, "UNIT") && !endsWith(key, "help"))
						{
							$(td).css("background-color","#FFBABA");
						}
						$(div).append("&#160;");
					}
					
					$(input).attr("name", "trans#" + translationsId + "#" + key + "#" + translationId);

					//open editor onclick wherever cursor is pointer and if not locked

					$(td).css("cursor","pointer").click(function(){
						if($(this).hasClass("locked")){
							return;
						}
						if ($(this).hasClass("translink"))
						{
							editLinkCell($(this));
						} else {
							editCell($(this));
						}
					});

					// only add hover effects when translation is not locked
					if(! $(td).hasClass("locked")){
						//hover effects
						$(td).hover(
							function () {
								$(this).attr("data-bg", $(this).css("background-color"));
								$(this).css("background-color","#BABAFF");
							},
							function () {
								$(this).css("background-color", $(this).attr("data-bg"));
							}
						);
					} else {
						//add locked style and tooltip
						$(div).addClass("translation-locked").attr("title", labellocked).attr("data-toggle", "tooltip");
					}
					
					$(td).append(input);
					$(td).append(div);
					
					$(tr).append(td);
				}
				
			}
		});			
				
		$("#edit-translations-table").find("tbody").first().append(tr);
	}
	
	$("#edit-translations-table").find("thead").append(trHead);
	
	$("#edit-translations-dialog").modal("show");
	
	$('[data-toggle="tooltip"]').tooltip()
}


var selectedCell = null;
function editCell(td)
{
	tinymce.editors['label-editor'].show();
	showEditCell(td);
}

function editLinkCell(td)
{
	showEditCell(td);
	tinymce.editors['label-editor'].hide();
}

var scrollTop = 0;
function showEditCell(td)
{
	scrollTop = $("#edit-translations-dialog").find(".modal-body")[0].scrollTop;
	$("#edit-translations-dialog").modal("hide");
	$("#edit-cell-dialog-invalid").hide();
	$("#edit-cell-dialog-empty").hide();
	selectedCell = td;
	$("#label-editor").html($(td).find("textarea").first().text());
	$("#label-editor-original").html($(td).parent().find(".activetranslation").first().html());
	$("#edit-cell-dialog").modal("show");
	
	 $("#edit-cell-dialog").find(".tinymce, .tinymcealign").each(function() {
   	  if ($(this).hasClass("full"))
   	  {
   		  $(this).parent().find("iframe").css("height", 526 - 250 + "px");
   	  }
     });
	
	tinymce.execCommand('mceFocus',false,'label-editor');
}

function cancelCellEdit()
{
	$("#edit-cell-dialog").modal("hide");
	$("#edit-cell-dialog-invalid").hide();
	$("#edit-cell-dialog-empty").hide();
	$("#edit-translations-dialog").modal("show");
	selectedCell = null;
}

function saveLabel()
{	
	$("#edit-cell-dialog-invalid").hide();
	$("#edit-cell-dialog-empty").hide();
	
	var label = removeIDs($("#label-editor").html());
	
	if ($(selectedCell).hasClass("translink"))
	{
		label = $("#label-editor")[0].value;
	}
	
	//this is a workaround for a bug in IE8
	if (label.indexOf("label-editor-original") > -1)
	{
		label = label.substring(32);
		label = label.substring(0, label.length-6);
	}	
	
	if (label.length == 0 && $(selectedCell).hasClass('pivotmandatory'))
	{
		$("#edit-cell-dialog-empty").show();
		return;
	}
	
	if (!checkXHTMLValidity(label))
	{
		$("#edit-cell-dialog-invalid").show();
		return;
	}
		
	$(selectedCell).find("textarea").first().text(label);
	$(selectedCell).find("div").first().html(label);
	if ($(selectedCell).find("div").first().is(':empty'))
	{
		var key = $(selectedCell).find("textarea").first().attr("name");
		if (!key.indexOf("UNIT#") > 0 && !key.indexOf("help#") > 0)
		{
			$(selectedCell).css("background-color","#FFBABA");
		}
	} else {
		$(selectedCell).css("background-color","");
	}
	$("#edit-cell-dialog").modal("hide");
	$("#edit-translations-dialog").modal("show");
	
	$("#edit-translations-dialog").find(".modal-body")[0].scrollTop = scrollTop;
}

var results = null;
function checkResults(responseJSON)
{
	$("#file-uploader-message-language").hide();
	var lang = responseJSON.language;
	var selectedLang = $("#langupdate").val();
	if (selectedLang == "other")
	{
		selectedLang = $("#codeupdate").val();
	}
	
	if (lang.toLowerCase() != selectedLang.toLowerCase())
	{
		$(".qq-upload-list").empty();
		$("#file-uploader-message-language").show();
	} else if (responseJSON.invalidKeys.length > 0)
	{
		results = responseJSON;
		$("#upload-translation-dialog").modal("hide");
		
		var keys = "";
		
		for (var i = 0; i < responseJSON.invalidKeys.length; i++)
		{
			keys = keys + responseJSON.invalidKeys[i] + " ";
		}
		
		$("#ask-invalid-keys").html(keys);
		$("#ask-invalid-dialog").modal("show");
	} else if (responseJSON.exists)
	{
		results = responseJSON;
		$("#upload-translation-dialog").modal("hide");
		$("#ask-override-dialog").modal("show");
		
	} else {
		$("#upload-translation-dialog").modal("hide");
		showImportPopup(responseJSON);
	}
}

function continueImport(responseJSON)
{
	$("#ask-invalid-dialog").modal("hide");
	if (responseJSON.exists)
	{
		results = responseJSON;
		$("#ask-override-dialog").modal("show");
		
	} else {
		showImportPopup(responseJSON);
	}
}

function showImportPopup(responseJSON)
{
	$("#ask-override-dialog").modal("hide");
	
	$("#import-translation-config-body").empty();
	
	var surveyId = document.createElement("input");
	$(surveyId).attr("type","hidden").attr("name","survey").val(responseJSON.surveyId);
	$("#import-translation-config-body").append(surveyId);
	
	var uid = document.createElement("input");
	$(uid).attr("type","hidden").attr("name","uid").val(responseJSON.uid);
	$("#import-translation-config-body").append(uid);
	
	var lang = document.createElement("input");
	$(lang).attr("type","hidden").attr("name","lang").val(responseJSON.language);
	$("#import-translation-config-body").append(lang);
	
	var table = document.createElement("table");
	$(table).addClass("table").addClass("table-bordered").addClass("table-striped");
	
	var tr = document.createElement("tr");
	$(tr).css("background-color","#999");
	
	var td = document.createElement("th");
	var check = document.createElement("input");
	$(check).attr("type","checkbox").prop("checked","checked").addClass("check").click(function(){
		if ($(this).is(":checked"))
		{
			$("#import-translation-config-body").find(".check").prop("checked","checked");
		} else {
			$("#import-translation-config-body").find(".check").removeAttr("checked");
		}
	});
	$(td).append(check);
	$(tr).append(td);
	
	td = document.createElement("th");
	$(td).append(ExistingLabels);
	$(tr).append(td);
	
	td = document.createElement("th");
	$(td).append(NewLabels);
	$(tr).append(td);		
	
	var head = document.createElement("thead");
	$(head).append(tr);	
	$(table).append(head);
	
	var body = document.createElement("tbody");
	
	for (var i = 0; i < responseJSON.pivotLabels.length; i++)
	{
		tr = document.createElement("tr");
	
		td = document.createElement("td");
		check = document.createElement("input");
		$(check).attr("type","checkbox").attr("name","check" + i).addClass("check").val(responseJSON.keys[i]);
		
		if (responseJSON.labels[i].trim().length > 0)
		{
			$(check).prop("checked","checked");
		} else {
			$(check).addClass("empty").attr("disabled","disabled");
		}
		
		$(td).append(check);
		$(tr).append(td);
		
		td = document.createElement("td");
		$(td).append( responseJSON.pivotLabels[i]);
		$(tr).append(td);
		
		td = document.createElement("td");
		
		if (!responseJSON.labels[i].trim().length > 0)
		{
			$(td).css("background-color", "rgb(255, 186, 186)");
		}
		
		$(td).append( responseJSON.labels[i]);
		$(tr).append(td);		
		
		$(body).append(tr);		
		
		if ((responseJSON.pivotLabels[i] == null || responseJSON.pivotLabels[i] == '') && (responseJSON.labels[i] == null || responseJSON.labels[i] == ''))
		{
			$(tr).hide();
		}
	}
	
	$(table).append(body);
	
	$("#import-translation-config-body").append(table);
	
	$("#upload-translation-dialog").modal("hide");
	$("#import-translation-config-dialog").modal("show");
		
}

function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}