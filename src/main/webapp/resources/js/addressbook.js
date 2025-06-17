function cancelAttendeesImport()
{
	$("#file-uploader-contacts-file").val("");
	$("#file-uploader-contacts-filename").val("");
	$("#file-uploader-contacts-filename-display").text("");
	$("#loaded-file").text("");
	$("#file-uploader-contacts-delimiter-div").hide();
	$("#import-attendees-step1-dialog").modal("hide");
}

function deleteAttendee(path)
{
	$("#delete-wait-animation").show();
	$.ajax( {
		type: "POST",
		url: path+"/addressbook/deleteAttendee",
		data: { 'id': selectedId },
		success: function(data, textStatus){
			window.location.replace(path+"/addressbook?deleted=" + selectedId);
		},
		error: function(xhr) {
			console.log(xhr);
		},
	});
}
function deleteAttendees(path)
{
	$('#delete-attendees-dialog').modal('hide');
	$.ajax( {
		type: "POST",
		url: path+"/addressbook/deleteAttendees",
		data: $("#load-attendees").serialize(),
		success: function(data, textStatus){
			window.location.replace(path+"/addressbook?deleted=batch")
		},
		error: function(xhr) {
			console.log(xhr);
		},
	});
}

function showAddAttendeeDialog()
{
	$('#add-attendee-dialog').find("#owner").val("");
	$('#add-attendee-dialog').find("#name").val("");
	$('#add-attendee-dialog').find("#email").val("");
	$('#add-attendee-dialog').find("#attributes").find("tbody").find("tr").remove();

	var i = 0;
	while (i < 4)
	{
		addRow();
		if (i < attributeNameIDs.length)
		{
			$('#add-attendee-dialog').find("#attributes").find("tbody").find("tr:last").find("td").first().find("select").val(attributeNameIDs[i]);
		}
		i++;
	};
	$('#add-attendee-dialog').modal();
}

var selectedId = "";
function showDeleteDialog(id, login)
{
	selectedId = id;
	$('#delete-attendee-dialog').modal();
}

var selectedSelect = null;
function showAddAttributeDialog(select)
{
	if ($(select).val() == "new")
	{
		$('#new-attribute-name').val("");
		$('#new-attribute-error').text("");
		selectedSelect = $(select);

		$(".modal:visible").modal("hide");

		$('#add-attribute-dialog').modal();

		$('#new-attribute-name').focus();
	}
}

function cancelAddAttributeDialog()
{
	$('#add-attribute-dialog').modal("hide");
	if (selectedSelect == null)
	{
		$("#batch-attendee-dialog").modal("show");
	} else {
		$(selectedSelect).val('');
		$(selectedSelect).closest(".modal").modal("show");
	}
}

function addAttribute(name)
{
	var found = false;
	var target = null;

	if (name == null || name.length == 0)
	{
		$('#new-attribute-error').text(requiredText);
		return;
	}

	if (name.toLowerCase() == "name" || name.toLowerCase() == "email" || name.toLowerCase() == "owner")
	{
		$('#new-attribute-error').text("This name is not allowed.");
		return;
	}

	if (selectedSelect != null)
	{
		$(selectedSelect).find("option:contains('" + name + "')").each(function(){
			$(this).prop('selected', true);
			found = true;
		});

		if (!found)
		$(".existingkey").each(function() {
			if ($(this).val().toLowerCase() == name.toLowerCase())
			{
				found = true;
				target = $(this).parent().parent().find("input[type='text']");
				$(selectedSelect).val('');
				return;
			}
		});

		if (!found)
		{
			$("select").find("option:contains('" + name + "')").each(function(){

				if ($(this).text() == name)
				{
					$(this).prop('selected', true);
					found = true;
					target = $(this).parent().parent().parent().find("input[type='text']");
					$(selectedSelect).val('');
				}
			});
		}

		if (!found)
		{
			var option = document.createElement("option");
			$(option).text(name).val(name).prop('selected', true);
			$(selectedSelect).append(option).val(name);
		}
	}

	if($(selectedSelect).hasClass( "bulkadded" )){


		$(selectedSelect).parent().find(".existingbatchkey:hidden").remove();

		var selectorId = $(selectedSelect).attr('id').substring(3);

		var input = document.createElement("input");
		$(input).attr("type","hidden").attr("name", "newattribute" + selectorId).val(name);
		$(selectedSelect).parent().append(input);

		var valueId = "#batch-value" + selectorId;

		$(valueId).attr("name","newvalue" + selectorId);
		var defaultValue = $("<option></option>");

		$(valueId).append(defaultValue);
		$(defaultValue).prop('selected', true);
	}

//	else {
//		//called for batch edit table
//		var id = getNewId();
//		var tr = document.createElement("tr");
//		var td = document.createElement("td");
//		$(td).append(name);
//		var input = document.createElement("input");
//		$(input).attr("type","hidden").attr("name", "newattribute" + id).val(name);
//		$(td).append(input);
//		$(tr).append(td);
//		td = document.createElement("td");
//		input = document.createElement("input");
//		$(input).attr("type","text").attr("name", "newvalue" + id);
//		$(td).append(input);
//		$(tr).append(td);
//		
//		var pos = null;
//		$("#batch-attendee-dialog").find("tbody").find("tr").each(function(){
//			var currentname = $(this).find("td").first().text().trim();
//			if (currentname.toLowerCase().localeCompare(name.trim().toLowerCase()) < 0){
//				pos = this;
//			}
//		});
//		
//		if (pos != null)
//		{
//			$(pos).after(tr);
//			found = true;
//			target = input;
//		} else {
//			$("#batch-attendee-dialog").find("tbody").append(tr);	
//		}
//	}

	$('#add-attribute-dialog').modal("hide");

	if (selectedSelect != null)
	{
		$(selectedSelect).closest(".modal").modal();
	} else {
		$("#batch-attendee-dialog").modal("show");
	}

	if (found)
	{
		$(target).focus();
		//$('#batch-attendee-dialog-step2-scrolldiv').scrollTop($(target).position().top + 20);
		scrollIntoView($('#batch-attendee-dialog-step2-scrolldiv')[0], $(target).parent().parent()[0]);
	} else {
		var height = $('#batch-attendee-dialog-step2-scrolldiv')[0].scrollHeight;
		$('#batch-attendee-dialog-step2-scrolldiv').scrollTop(height);
	}
}

function scrollIntoView(element, container) {
	  var containerTop = $(container).scrollTop();
	  var containerBottom = containerTop + $(container).height();
	  var elemTop = element.offsetTop;
	  var elemBottom = elemTop + $(element).height();
	  if (elemTop < containerTop) {
	    $(container).scrollTop(elemTop);
	  } else if (elemBottom > containerBottom) {
	    $(container).scrollTop(elemBottom - $(container).height());
	  }
	}

function showAddAttributeDialogForTable()
{
	selectedSelect = null;

	$('#new-attribute-name').val("");
	$('#new-attribute-error').text("");

	$("#batch-attendee-dialog").modal("hide");
	$('#add-attribute-dialog').modal();
}

function addRow(edit)
{
	var tr =  document.createElement("tr");
	var td =  document.createElement("td");

	var name = getNewId();

	var select = $('#allAttributes').clone().attr("style","width: auto; max-width: 200px;").removeAttr("id");

	//remove entries that are already displayed
	$(".existingkey").each(function(){
		var text = $(this).val();
		$(select).find("option").filter(function () { return $(this).html() == text; }).each(function(){
			//$(this).remove();
		});
	});

	$(select).attr("name", "key" + name);
	$(td).append(select);
	$(tr).append(td);

	var input =  document.createElement("input");
	$(input).addClass("form-control").attr("type","text").attr("style", "width: 250px;").attr("name", "value" + name).attr("maxlength","500");
	td =  document.createElement("td");
	$(td).append(input);
	$(tr).append(td);

	var a = document.createElement("a");
	$(a).addClass("iconbutton").attr("data-toggle", "tooltip").attr("title", labelRemoveAttribute);
	$(a).click(function() {
		  $(this).parent().parent().remove();
		});
	var i =  document.createElement("span");
	$(i).addClass("glyphicon glyphicon-remove");
	$(a).append(i);
	td =  document.createElement("td");
	$(td).append(a);
	$(tr).append(td);

	if (edit)
	{
		$("#edit-attributes").append(tr);
	} else {
		$("#attributes").append(tr);
		 $('#attributes').parent().animate({
	         scrollTop:  $('#attributes').parent()[0].scrollHeight
	     }, 1000);
	}

	$('[data-toggle="tooltip"]').tooltip();
}

function addAttendee()
{
	//check for doubles
	var error = false;
	$("#add-attendee-error-multiple").hide();
	$("#add-attendee-error-no-attribute").hide();
	$("#add-attendee-error-name").hide();
	$("#add-attendee-error-email").hide();
	$("#add-attendee-error-email2").hide();
	$("#add-attendee-dialog").find("select").each(function(){

		var select = $(this);

		//first search for existing keys
		$("#add-attendee-dialog").find(".existingkey").each(function(){
			if ($(this).val().length > 0 && $(this).val() == $(select).val())
			{
				//alert($(this).val() + " used more than once");
				$("#add-attendee-error-multiple-text").text($(this).text());
				$("#add-attendee-error-multiple").show();
				error = true;
			}
		});

		if (error == true) return;

		//then search for new keys
		var selectedOptions = "";
		$("#add-attendee-dialog").find("select").find(":selected").each(function(){

			if ($(this).val().length > 0 && selectedOptions.indexOf("#" + $(this).val() + "#") != -1)
			{
				//alert($(this).text() + " used more than once");
				$("#add-attendee-error-multiple-text").text($(this).text());
				$("#add-attendee-error-multiple").show();
				error = true;
			} else {
				selectedOptions = selectedOptions + "#" + $(this).val() + "#";
			}
		});

	});

	//check values without keys
	$("#add-attendee-dialog").find("input[name^='value']").each(function(){
		if ($(this).val() != "")
		{
			if ($(this).parent().parent().find("td:first").find("select").find(":selected").val() == "")
			{
				$("#add-attendee-error-no-attribute-text").text($(this).val());
				$("#add-attendee-error-no-attribute").show();
				error = true;
			}
		}
	});

	//check for name and email
	if ($("#add-attendee-dialog").find("#name").val() == '')
	{
		$("#add-attendee-error-name").show();
		error = true;
	}
	if ($("#add-attendee-dialog").find("#email").val() == '')
	{
		$("#add-attendee-error-email").show();
		error = true;
	} else if (!validateEmail($("#add-attendee-dialog").find("#email").val()))
	{
		$("#add-attendee-error-email2").show();
		error = true;
	}

	$("#add-attendee-error-ownerdoesnotexist").hide();

	if (error == false)
	{
		$.ajax({
			type:'GET',
			  url: contextpath + "/addressbook/checkNewAttendee",
			  data: "email=" + $("#add-attendee-dialog").find("#email").val() + "&owner=" + $("#add-attendee-dialog").find("#owner").val(),
			  dataType: 'text',
			  async: false,
			  cache: false,
			  success: function( result ) {

				  if (result == "ATTENDEEEXISTS")
				  {
					  $("#add-attendee-dialog").modal("hide");
					  $("#add-attendee-dialog-attendeeexists").modal("show");
				  } else  if (result == "OWNERDOESNOTEXIST")
				  {
					  $("#add-attendee-error-ownerdoesnotexist").show();
				  } else {
					  $("#add-attendee-form").modal("hide");
						$("#add-attendee-form").find("input").each(function(){
							if ($(this).attr("name") != "_csrf")
							{
								$(this).remove();
							}
						});
						$("#add-attendee-dialog").find("input").each(function(){
							$("#add-attendee-form").append($(this).attr("style","display: none;"));
						});
						$("#add-attendee-dialog").find("select").each(function(){
							$("#add-attendee-form").append($(this).attr("style","display: none;"));
						});
						$("#add-attendee-form").submit();
					  return;
				  }

			}});
	}
}

function addAttendeeExistsYes()
{
	$("#add-attendee-dialog-attendeeexists").modal("hide");
	$("#add-attendee-form").modal("hide");
	$("#add-attendee-form").find("input").each(function(){
		if ($(this).attr("name") != "_csrf")
		{
			$(this).remove();
		}
	});
	$("#add-attendee-dialog").find("input").each(function(){
		$("#add-attendee-form").append($(this).attr("style","display: none;"));
	});
	$("#add-attendee-dialog").find("select").each(function(){
		$("#add-attendee-form").append($(this).attr("style","display: none;"));
	});
	$("#add-attendee-form").submit();
}

function addAttendeeExistsNo()
{
	$("#add-attendee-dialog-attendeeexists").modal("hide");
	$("#add-attendee-dialog").modal("show");
}

function editAttendee()
{
	//check for doubles
	var error = false;
	$("#edit-attendee-error-multiple").hide();
	$("#edit-attendee-error-no-attribute").hide();
	$("#edit-attendee-error-name").hide();
	$("#edit-attendee-error-email").hide();
	$("#edit-attendee-error-email2").hide();
	$("#edit-attendee-dialog").find("select").each(function(){

		var select = $(this);

		//first search for existing keys
		$("#edit-attendee-dialog").find(".existingkey").each(function(){
			if ($(this).val().length > 0 && $(this).val() == $(select).val())
			{
				$("#edit-attendee-error-multiple-text").text($(this).text());
				$("#edit-attendee-error-multiple").show();
				error = true;
			}
		});

		if (error == true) return;

		//then search for new keys
		var selectedOptions = "";
		$("#edit-attendee-dialog").find("select").find(":selected").each(function(){

			if ($(this).val().length > 0 && selectedOptions.indexOf("#" + $(this).val() + "#") != -1)
			{
				//alert($(this).text() + " used more than once");
				$("#edit-attendee-error-multiple-text").text($(this).text());
				$("#edit-attendee-error-multiple").show();
				error = true;
			} else {
				selectedOptions = selectedOptions + "#" + $(this).val() + "#";
			}
		});

	});

	//check values without keys
	$("#edit-attendee-dialog").find("input[name^='value']").each(function(){
		if ($(this).val() != "")
		{
			if ($(this).parent().parent().find("td:first").find("select").find(":selected").val() == "")
			{
				$("#edit-attendee-error-no-attribute-text").text($(this).val());
				$("#edit-attendee-error-no-attribute").show();
				error = true;
			}
		}
	});

	//check for name and email
	if ($("#edit-attendee-dialog").find("#name").val() == '')
	{
		$("#edit-attendee-error-name").show();
		error = true;
	}
	if ($("#edit-attendee-dialog").find("#email").val() == '')
	{
		$("#edit-attendee-error-email").show();
		error = true;
	} else if (!validateEmail($("#edit-attendee-dialog").find("#email").val()))
	{
		$("#edit-attendee-error-email2").show();
		error = true;
	}

	if (error == false)
	{
		$("#edit-attendee-dialog").modal("hide");
		$("#add-attendee-form").find("input").each(function(){
			if ($(this).attr("name") != "_csrf")
			{
				$(this).remove();
			}
		});
		$("#edit-attendee-dialog").find("input").each(function(){
			$("#add-attendee-form").append($(this).attr("style","display: none;"));
		});
		$("#edit-attendee-dialog").find("select").each(function(){
			$("#add-attendee-form").append($(this).attr("style","display: none;"));
		});
		$("#add-attendee-form").submit();

	}
}

function checkFile()
{
	$("#import-attendees-step1-error").hide();

	if ($("#file-uploader-contacts-file").val() != "")
	{
		//$("#import-attendees-step1-dialog").modal("hide");
		$("#import-attendees-form").submit();
		return true;
	}

	if ($("#loaded-file").text() != "")
	{
		$("#import-attendees-step1-dialog").modal("hide");
		$("#import-attendees-step2-dialog").modal();
	} else 	{
		$("#import-attendees-step1-error").show();
	}
}

function checkChanged()
{
	if ($("#checkAll").is(":checked"))
	{
		$("#import-attendees-step3-dialog").find("input[type='checkbox']").not(":disabled").prop("checked","checked");
	} else {
		$("#import-attendees-step3-dialog").find("input[type='checkbox']").not(":disabled").removeAttr("checked");
	}
}

function step1()
{
	$("#import-attendees-step2-dialog").modal("hide");
	$("#import-attendees-step1-dialog").modal();
}

function step2()
{
	$("#import-attendees-step3-form-target").val("importAttendeesCheck");
	$("#import-attendees-step3-dialog").modal("hide");
	$("#import-attendees-step2-dialog").modal();
}

function step3()
{
	//check mapped attributes
	var error = false;
	$("#import-attendees-step2-error-multiple").hide();
	$("#import-attendees-step2-error-no-attribute").hide();
	$(".importmappings").each(function(){

		if (error) return;

		var namefound = false;
		var emailfound = false;

		//then search for new keys
		var selectedOptions = "";
		$("#import-attendees-step2-dialog").find(".importmappings").find(":selected").each(function(){
			if (error) return;

			if ($(this).val() == "Name")
			{
				namefound = true;
			} else if ($(this).val() == "Email") {
				emailfound = true;
			}

			if ($(this).val() == "Choose")
			{
//				$("#import-attendees-step2-error-no-attribute-text").text($(this).parent().parent().parent().find("td:first").text());
//				$("#import-attendees-step2-error-no-attribute").show();
//				error = true;
			} else if (selectedOptions.indexOf("#" + $(this).val() + "#") != -1)
			{
				$("#import-attendees-step2-error-multiple-text").text($(this).text());
				$("#import-attendees-step2-error-multiple").show();
				error = true;
			} else {
				selectedOptions = selectedOptions + "#" + $(this).val() + "#";
			}
		});

		if (!namefound)
		{
			$("#import-attendees-step2-error-no-attribute-text").text("Name");
			$("#import-attendees-step2-error-no-attribute").show();
			error = true;
		} else if (!emailfound)
		{
			$("#import-attendees-step2-error-no-attribute-text").text("Email");
			$("#import-attendees-step2-error-no-attribute").show();
			error = true;
		}

		//check values without keys
		$("#import-attendees-step2-dialog").find(".importmappings").find(":selected").each(function(){
			if (error) return;
			if ($(this).val() == null || $(this).val() == "" || $(this).val() == "new" )
			{
				$("#import-attendees-step2-error-no-attribute-text").text($(this).parent().parent().parent().find("td:first").text());
				$("#import-attendees-step2-error-no-attribute").show();
				error = true;
			}
		});

	});

	if (error == false)
	{
		//$("#import-attendees-step2-dialog").modal("hide");

		$("#import-attendees-step3-dialog").find(".mappedheader").each(function(){

			var name = $(this).attr("id");
			var value = $("#import-attendees-step2-dialog").find(".importmappings[name='" + $(this).attr("id") + "']").find(":selected").text();

			$(this).empty();

			if (value == 'Choose')
			{
				$(this).hide();
				var index = $(this).attr("data-index");
				$(".col" + index).hide();
			} else {

				$(this).show();
				var index = $(this).attr("data-index");
				$(".col" + index).show();

				var input = document.createElement("input");
				$(input).attr("type","hidden");
				$(input).attr("name", name);
				$(input).val(value);
				$(this).append(input);

				var span = document.createElement("span");
				$(span).text(value);
				$(this).append(span);
			}
		});

		$("#import-attendees-step3-form").submit();
		//$("#import-attendees-step3-dialog").modal();
	} else {
		$("#import-attendees-step2-dialog").find(".modal-body").scrollTop($("#import-attendees-step2-dialog").find(".modal-body").height()+200);
	}

}

function cancelConfigure()
{
	$("#sortable").find("li").remove();
	$("#originalconfiguration").find("li").each(function(){
		var li = $(this).clone();
		li.find(".originalselectedattrib").addClass("selectedattrib");
		$("#sortable").append(li);
	});

	$("#configure-attributes-dialog").find(".allattrib").removeAttr("checked");
	$("#configure-attributes-dialog").modal("hide");
}

function saveConfiguration()
{
	var s = "";

	$("#configure-attributes-dialog").find(".selectedattrib").each(function(){
		s += $(this).attr("name") + ";";
	});

	$("#selectedAttributesOrder").val(s);

	$("#configure-attributes-form").submit();
}

var exportType;
var exportFormat;

function showExportDialog(type, format)
{
	exportType = type;
	exportFormat = format;
	$('#export-name-dialog').find("input").first().val('');
	$('#export-name-dialog-type').text(format.toUpperCase());
	$('#export-name-dialog').modal();
	$('#export-name-dialog').find("input").first().focus();
}

function startExport(name)
{
	$.ajax({
		type:'POST',
		  url: contextpath + '/exports/start/' + exportType + "/" + exportFormat,
		  data: {exportName: name, showShortnames: false, allAnswers: false, group: ""},
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

var selectedSelect = null;
var scrollPos;
function checkAttributeSelection(select)
{
	if ($(select).val() == '-2')
	{
		selectedSelect = select;
		$('#new-attribute-value').val("");
		scrollPos = $("#batch-attendee-dialog").find(".modal-body").scrollTop();
		$("#batch-attendee-dialog").modal("hide");
		$("#add-attribute-value-dialog").modal("show");
		$("#new-attribute-value").focus();
	}
}

function addAttributeValue(value)
{
	var found = false;
	$(selectedSelect).find("option").each(function(){
		if ($(this).val() == value)
		{
			found = true;
		}
	});

	if (!found) $(selectedSelect).append("<option>" + value + "</option>");

	$(selectedSelect).val(value);
	$("#add-attribute-value-dialog").modal("hide");
	$("#batch-attendee-dialog").modal("show");
	$("#batch-attendee-dialog").find(".modal-body").scrollTop(scrollPos);
}

function cancelAddAttributeValue() {
	$(selectedSelect).val("0");
	$("#add-attribute-value-dialog").modal("hide");
	$("#batch-attendee-dialog").modal("show");
}

function checkOwnerAndSubmit()
{
	var owner = $("#batch-owner").val();
	var name = $("#batch-name").val();
	var email = $("#batch-email").val();

	$('#batch-attendee-dialog-step2-scrolldiv').find(".validation-error").hide();

	var ok = true;

	if (name != "0" && name != "-2")
	{
		if (name.length == 0)
		{
			 $("#batch-name-error").show();
			 ok = false;
		}
	}


	if (email != "0" && email != "-2")
	{
		if (email.length == 0 || !validateEmail(email))
		{
			$("#batch-email-error").show();
			  ok = false;
		}
	}

	if (owner != null && owner.trim().length > 0)
	{
		$.ajax({
			type:'GET',
			  async: false,
			  url: contextpath + "/settings/userExists",
			  data: "login=" + owner,
			  dataType: 'json',
			  cache: false,
			  error: function() {
				ok = false;
				showError(usersTooOftenAddressBook);
			  },
			  success: function( exists ) {
				  if (exists == true)
				  {
					  ok = true;
				  } else {
					  $("#batch-owner-error").show();
					  ok = false;
					  return;
				  }

			}});
	}


	if (ok)
	{
		$("#batch-edit-form").submit();
	} else {
		$('#batch-attendee-dialog-step2-scrolldiv').animate({
	         scrollTop:  0
	     }, 1000);
	}
}

function batchStep1()
{
	$("#batch-attendee-dialog-step2").hide();
	$("#batch-attendee-dialog-step1").show();
	$("#batch-previous-button").hide();
	$("#batch-update-button").hide();
	$("#batch-next-button").show();
	$("#batch-next-button").css("display","inline-block");
}

function batchStep2()
{
	$("#batch-attendee-dialog-step1").hide();
	$("#batch-attendee-dialog-step2").show();
	$("#batch-previous-button").show();
	$("#batch-previous-button").css("display","inline-block");
	$("#batch-update-button").show();
	$("#batch-update-button").attr("style","");
	$("#batch-next-button").hide();
}

function checkAll()
{
	if ($("#checkall").is(":checked"))
	{
		$(".attendee").attr("checked", "checked");
	} else {
		$(".attendee").removeAttr("checked");
	}
}

function uncheck(check)
{
	if (!$(this).is(":checked"))
	{
		$("#checkall").removeAttr("checked");
	}
}

function startOperation()
{
	if ($("#gobutton").attr("disabled"))
	{
		return;
	}

	if ($('#selectOperation').val() == '1')
	{
		$('#operation').val('batchedit');
		$('#load-attendees').submit();
	} else {
		$('#delete-attendees-dialog').modal('show');
	}
}


function checkAttributeKeySelection(attributeSelector)
{
	if ($(attributeSelector).val() == "new")
	{
		showAddAttributeDialog(attributeSelector);
		return;
	}

	var selectorId = $(attributeSelector).attr('id');
	var selectorChoice =  $(attributeSelector).find(":selected")[0];

	var attrId = $(selectorChoice).attr('value');
	if(attrId == null || attrId.length < 1)
		attrId = $(selectorChoice).attr('val');

	if(selectorId.length > 0){

			var valueId = "#batch-value" + selectorId.substring(3);

			$(valueId).attr("name","attribute" + attrId);

			var hiddenInput = $(attributeSelector).parent().children(".existingbatchkey")[0];

			if(hiddenInput == null){
					hiddenInput = $('<input type="hidden" class="existingbatchkey bulkadded" />');
					$(attributeSelector).parent().append($(hiddenInput));
			};

			$(hiddenInput).attr('value',$(selectorChoice).text());

	}
}

function createAttributeSelector(targetTable)
{
	  if (typeof (targetTable) == "string") {
		  targetTable = $(targetTable);
	    }

	var newRow =  $('<tr></tr>');
	var newCell = $('<td></td>');

	var name = getNewId();

	var select = $('#allAttributes').clone().attr("style","width: auto;").removeAttr("id");

	$(select).find("option").filter(function () { return $(this).html() == ""; }).each(function(){
		$(this).remove();
	});

	$(select).find("option").filter(function () { return $(this).attr('value') == "new"; }).each(function(){
		$(this).remove();
	});

	/*
	$(select).append("<optgroup label='Predefined'/>");

	$(select).find("option").each(function()
	{
		$(select).find('optGroup').last().append($(this));
	});
	*/

	$(select).attr("name", "key" + name);
	$(select).attr("id", "key" + name);

	$(".existingbatchkey").each(function(){
		var text = $(this).val();
		$(select).find("option").filter(function () { return $(this).html() == text; }).each(function(){
			$(this).remove();
		});
	});

	var createOnly = false;
	var optionsLeft = 	$(select).find("option").length;

	if(optionsLeft == 0){

		var selectors = $('.optCreate').parent(':not(:disabled)');
		var selectorAvailable = $(selectors).length;

		if(selectorAvailable == 1){
			createOnly = true;
		}

	}
	else {
		$(select).append("<optgroup label='Predefined' class='optPredefined'/>");

		$(select).find("option").each(function()
		{
			$(select).find('optGroup').last().append($(this));
		});
	}



	$(".bulkadded").each(function(){
		//$(this).removeClass('bulkadded');
		$(this).prop('disabled', 'disabled');
	});

	var $optgroup = $("<optgroup label='Create' class='optCreate'><option value='new'>New...</option></optgroup>");

	var selectedOption = $(select).find('optgroup > option:first');
	var selectedValue = $(selectedOption).val();
	var selectedAttrId = $(selectedOption).attr('val');

	$(select).val(selectedValue);

    $optgroup.prependTo($(select));

	$(select).addClass('bulkadded');
	$(newCell).append($(select));
	$(newRow).append($(newCell));
	$(select).attr("onchange","checkAttributeKeySelection(this);");

	var selectValues = $('<select></select>');

	if(selectedAttrId != null)
		$(selectValues).attr("name","attribute" + selectedAttrId);
	else
		$(selectValues).attr("name","value" + name);


	$(selectValues).attr("id","batch-value" + name);
	$(selectValues).attr("onchange","checkAttributeSelection(this);").addClass("form-control");


	var valueOptions = [ {id: 0, action: 'keep value'}, {id: -2, action: 'new value'}, {id: -1, action: 'clear value'} ];

	setSelectOptions($(selectValues),valueOptions,'id', 'action');

	newCell = $('<td></td>');
	$(newCell).append(selectValues);

	checkAttributeKeySelection(select);

	var a = $('<a></a>');
	$(a).addClass("btn btn-default").attr("style","margin-left: 9px;margin-bottom: 3px");
	$(a).click(function() {
		  $(this).parent().parent().remove();
		});
	var i =  $('<span></span>');
	$(i).addClass("glyphicon glyphicon-remove");
	$(a).append($(i));
	$(newCell).append($(a));

	$(newCell).attr("style","white-space: nowrap");

	$(newRow).append($(newCell));

	targetTable.append($(newRow));

	if(createOnly)
		checkAttributeKeySelection($(select));

}


function setSelectOptions(selectElement, values, valueKey, textKey, defaultValue) {

    if (typeof (selectElement) == "string") {
        selectElement = $(selectElement);
    }

    selectElement.empty();

    if (typeof (values) == 'object') {

        if (values.length) {

            var type = typeof (values[0]);
            var html = "";

            if (type == 'object') {
                // values is array of hashes
                var optionElement = null;

                $.each(values, function () {
                    html += '<option value="' + this[valueKey] + '">' + this[textKey] + '</option>';
                });

            } else {
                // array of strings
                $.each(values, function () {
                    var value = this.toString();
                    html += '<option value="' + value + '">' + value + '</option>';
                });
            }

            selectElement.append(html);
        }

        // select the defaultValue is one was passed in
        if (typeof defaultValue != 'undefined') {
            selectElement.children('option[value="' + defaultValue + '"]').attr('selected', 'selected');
        }

    }

    return false;

}
