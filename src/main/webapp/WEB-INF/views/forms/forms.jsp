<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Surveys" /></title>
	
	<%@ include file="../includes.jsp" %>
	
	<link href="${contextpath}/resources/css/management.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/fileuploader.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/form.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	
	<script type="text/javascript" src="${contextpath}/resources/js/jquery.stickytableheaders.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/importsurvey.js?version=<%@include file="../version.txt" %>"></script>
		
	<style type="text/css">		
			    
	    .datepicker td {
			padding: 1px;
		}
		
		 .right-area { 
		 	float: right;  
		 	border-left: 1px solid #ddd;  
		 	padding-left: 20px;  
		 	padding-right: 30px; 
		 	margin-bottom: 30px;
		 	width: 250px; 
		 	margin-top: 50px;
		 } 
		
		 .left-area { 
			float: left;  
		 	padding: 20px;  
		 	width: 700px; 
		 	margin-top: 40px;
		 } 
		 
		 .labelcell {
		 	font-weight: bold;
		 }
		 
		 .surveytitle {
		 	font-weight: bold;
		 	font-size: 28px;
		 	line-height: normal;
		 	max-width: 600px;
		 	overflow: hidden;
		 }
		 
		 #results td {
		 	vertical-align: top;
		 	min-width: 100px;
		 }
		 
		 .language {
		 	width: 26px;
		 }
		 
	</style>
	
	<script type="text/javascript" src="${contextpath}/resources/js/fileuploader.js"></script>
	
	<script type="text/javascript"> 
	
		var surveyID = null;
		var uuid = null;
		var title = null;
		var contact = null;
	
		$(function() {					
			$("#forms-menu-tab").addClass("active");
			
			$(".filtercell").each(function(){
				checkFilterCell(this, true);
			});
			
			$("#formstable").stickyTableHeaders();
					
			$("#date-options-div").find("input").each(function(){
				if ($(this).val().length > 0)
					dateHidden = true;
			});
			
			showHideDateOptions();
			
			$(window).scroll(function() {
			    if ($(window).scrollTop() <= $(document).height() - $(window).height() && $(window).scrollTop() >= $(document).height() - $(window).height() - 10) {
			    	loadMore();
			  }
			 });
			
			<c:if test="${imported != null}">
				surveyID = '${imported}';
				
				<c:if test="${invalidCodeFound != null}">
					$("#import-survey-dialog-2-invalidCodeFound").show();
				</c:if>				
				
    			$("#import-survey-dialog-2").modal("show");
			</c:if>
			
			$('[data-toggle="tooltip"]').tooltip(); 
		});	
		
		function showExportDialog(type, format)
		{
			exportType = type;
			exportFormat = format;
			$('#export-name').val("");
			$('#export-name-dialog-type').text(format.toUpperCase());
			$('#export-name-dialog').modal();	
			$('#export-name-dialog').find("input").first().focus();
		}
		
		var shortnameForExport = "";
		
		function startExport(name)
		{
			// check again for new exports
			window.checkExport = true;
			
			$.ajax({
	           type: "POST",
	           url: "${contextpath}/exports/start/" + exportType + "/" + exportFormat,
	           data: {exportName: name, showShortnames: false, allAnswers: false, group: "", shortname: shortnameForExport},
	           beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
	           success: function(data)	           {
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
		
		var newPage = 2;
		var loadingmore = false;
        var endReached = false;
		function loadMore()
		{
			if (loadingmore || endReached) return;
			
			loadingmore = true;
			
			$( "#wheel" ).show();
			var s = "page=" + newPage++ + "&rows=10";	
			
			var reportText = '<spring:message code="warning.ReportedSurvey" arguments="[X]" />'
			
			$.ajax({
				type:'GET',
				  url: "${contextpath}/forms/surveysjson",
				  dataType: 'json',
				  data: s,
				  cache: false,
				  success: function( list ) {
				  	if (list.length === 0) {
                        endreached = true;
                        $( "#wheel" ).hide();
                        return;
                    }
                                  
                                  
					  for (var i = 0; i < list.length; i++ )
					  {
						  var div = document.createElement("div");
						  
						  $(div).addClass("surveybox");
						  
						  if (list[i].numberOfReports > 0)
						  {
							  var divReport = document.createElement("div");
							  $(divReport).addClass("surveywarning").html(reportText.replace("[X]", list[i].numberOfReports));
							  $(div).append(divReport);
						  }
						  						  
						  var divHeader = document.createElement("div");
						  $(divHeader).addClass("surveyItemHeader");
						  
						  var divicon = document.createElement("div");
						  $(divicon).addClass("typeicon");
						  var img = document.createElement("img");
						  if (list[i].isQuiz)
						  {							 
							  $(img).attr("data-toggle", "tooltip").attr("title", "<spring:message code="label.Quiz" />").css("width","32px").attr("src", contextpath + "/resources/images/icons/64/quiz.png");
						  } else if (list[i].isOPC)
						  {							 
							  $(img).attr("data-toggle", "tooltip").attr("title", "<spring:message code="label.OPC" />").attr("src", contextpath + "/resources/images/icons/24/people.png");
						  } else if (list[i].isDelphi)
						  {							 
							  $(img).attr("data-toggle", "tooltip").attr("title", "<spring:message code="label.Delphi" />").attr("src", contextpath + "/resources/images/icons/24/delphi.png");
						  } else {
							  $(img).attr("data-toggle", "tooltip").attr("title", "<spring:message code="label.StandardSurvey" />").css("width","32px").attr("src", contextpath + "/resources/images/icons/64/survey.png");
						  }
						  $(divicon).append(img);
						  $(div).append(divicon);
						  
						  var alertdiv = document.createElement("div");
						  $(alertdiv).css("float", "right").css("margin-left","20px");
						  if (list[i].isActive && list[i].isPublished)
						  {
							  $(alertdiv).append('<div class="publishedsurveytag"><div class="arrow-left"></div><spring:message code="label.Published" /></div>');
						  } else {
							  $(alertdiv).append('<div class="unpublishedsurveytag"><div class="arrow-left"></div><spring:message code="label.Unpublished" /></div>');
						  }
						  
						  if (list[i].isPublished && list[i].hasPendingChanges)
						  {
							  $(alertdiv).append('<div class="pendingchangessurveytag"><div class="arrow-left"></div><spring:message code="label.PendingChanges" /></div>');
						  }
						  $(divHeader).append(alertdiv);
						  
						  $(divHeader).append('<div class="surveytitle">' + list[i].title.stripHtml115() + '<span class="completetitle hideme">' + list[i].title + '</span></div>');
						  $(divHeader).append('<div class="shortname">' + list[i].shortname + '</div>');
						  $(div).append(divHeader).append("<br />");
						  
						  var table = document.createElement("table");
						  $(table).addClass("formsSurveyItem");
						  var tbody = document.createElement("tbody");
						  
						  var tr = document.createElement("tr");
						  var td = document.createElement("td");						  
						  $(td).addClass("labelcell").append('<spring:message code="label.Created" />');						  
						  $(tr).append(td);						  
						  td = document.createElement("td");
						  $(td).append(list[i].createdString);						  
						  $(tr).append(td);		
						  td = document.createElement("td");
						  $(td).addClass("labelcell").append('<spring:message code="label.Owner" />');						  
						  $(tr).append(td);						  
						  td = document.createElement("td");
						  if (list[i].owner == null)
						  {
							  $(td).append('<spring:message code="label.NotSpecified" />');
						  } else {
							  $(td).append(list[i].owner.name);
						  }						  
						  $(tr).append(td);	
						  $(tbody).append(tr);
						  
						  tr = document.createElement("tr");
						  td = document.createElement("td");
						  $(td).addClass("labelcell").append('<spring:message code="label.StartDate" />');						  
						  $(tr).append(td);						  
						  td = document.createElement("td");	
						  
						  if (!list[i].automaticPublishing || list[i].start == null)
						  {
							  $(td).append('<spring:message code="label.NotSpecified" />');
						  } else {
							  $(td).append(list[i].startString);
						  }						  						  						  
						  $(tr).append(td);	
						  td = document.createElement("td");
						  $(td).addClass("labelcell").append("<spring:message code="label.ExpiryDate" />");						  
						  $(tr).append(td);						  
						  td = document.createElement("td");
						  if (!list[i].automaticPublishing || list[i].end == null)
						  {
							  $(td).append('<spring:message code="label.NotSpecified" />');
						  } else {
							  $(td).append(list[i].endString);
						  }						  
						  $(tr).append(td);							  
						  $(tbody).append(tr);
						  
						  tr = document.createElement("tr");
						  td = document.createElement("td");
						  $(td).addClass("labelcell").append('<spring:message code="label.Replies" />');						  
						  $(tr).append(td);						  
						  td = document.createElement("td");
						  $(td).append(list[i].numberOfAnswerSetsPublished);						  
						  $(tr).append(td);	
						  td = document.createElement("td");
						  $(td).addClass("labelcell").append('<spring:message code="label.Translations" />');						  
						  $(tr).append(td);						  
						  td = document.createElement("td");
						 
						  for (var t = 0; t < list[i].translations.length; t++ )
						  {
							  if (list[i].translations[t] ==  list[i].language.code)
							  {
								$(td).append('<div data-toggle="tooltip" title="<spring:message code="label.PivotLanguage" />" class="language pivotlanguage">' + list[i].translations[t] + '</div>');
							  } else if (list[i].completeTranslations != null && $.inArray(list[i].translations[t], list[i].completeTranslations) > -1)
							  {
								$(td).append('<div data-toggle="tooltip" title="<spring:message code="label.Available" />" class="language">' + list[i].translations[t] + '</div>');  
							  } else {
								$(td).append('<div data-toggle="tooltip" title="<spring:message code="label.NotYetAvailable" />" class="language languageUnpublished">' + list[i].translations[t] + '</div>');
							  }
						  }
						  $(tr).append(td);							  
						  $(tbody).append(tr);

						  tr = document.createElement("tr");
						  td = document.createElement("td");
						  $(td).attr("colspan", 5);
						  $(td).addClass("separatorCell");
						  $(tr).append(td);							  
						  $(tbody).append(tr);
							  
						  if (list[i].owner.id == ${USER.id} || ${USER.formPrivilege > 0})
						  {			
							  tr = document.createElement("tr");
							  td = document.createElement("td");
							  $(td).attr("colspan", 5);
							  $(td).addClass("surveyactions");
							  
							  $(td).append('<a data-toggle="tooltip" title="<spring:message code="label.Open" />" id="lnk'+list[i].shortname+'" href="${contextpath}/' + list[i].shortname + '/management/overview"><span class="glyphicon glyphicon-folder-open"></span></a>');
							  
							  var acopy = document.createElement("a");
							  
							  if(list[i].formManagementRights && list[i].canCreateSurveys)
							  {
							     $(acopy).addClass("actionRowAction").append("<span class='glyphicon glyphicon-copy'></span>");
							     $(acopy).attr('onclick', "copySurvey('" + list[i].id + "','" + list[i].title + "','" + list[i].language.code + "', '" + list[i].security+ "', '" + list[i].isQuiz + "', '" + list[i].isDelphi + "');");
							  } else {
								  $(acopy).addClass("disabled actionRowAction").append("<span style='color: #ccc' class='glyphicon glyphicon-copy'></span>");
							  }							     
							     
							  $(acopy).attr('rel','tooltip').attr("data-toggle","tooltip").attr('title','<spring:message code="label.Copy" />');
							  $(td).append(acopy);
							  
							  if(list[i].formManagementRights)
							  {
							  	$(td).append("<a href='${contextpath}/noform/management/exportSurvey/false/" + list[i].shortname + "' class='actionRowAction' rel='tooltip' data-toggle='tooltip' title='<spring:message code="label.Export" />'><span class='glyphicon glyphicon-download-alt'></span></a> ");
							  } else {
							  	$(td).append("<a class='disabled actionRowAction' rel='tooltip' data-toggle='tooltip' title='<spring:message code="label.Export" />'><span class='glyphicon glyphicon-download-alt' style='color: #ccc'></span></a> ");
							  }
							  
							  $(td).append("&nbsp;");
							  
								if(list[i].state != 'Running' && (list[i].fullFormManagementRights))
								{
									var a = document.createElement("a");
									$(a).addClass("actionRowAction");
									$(a).attr("href", "#");
									$(a).attr("onclick", "showArchiveDialog('"+list[i].shortname+"','"+list[i].id+"', true)");
									$(a).attr("rel", "tooltip").attr("data-toggle","tooltip");
									$(a).attr("title", "<spring:message code="label.Archive" />");
									
									$(a).append("<span class='glyphicon glyphicon-import'></span>");
									$(td).append(a);
								}
								else
								{
									var a = document.createElement("a");
									$(a).addClass("actionRowAction").addClass("disabled");
									$(a).attr("rel", "tooltip").attr("data-toggle","tooltip");
									
									if (list[i].fullFormManagementRights)
									{
										$(a).attr("title", '<spring:message code="tooltip.ArchiveDisabled" />');
									} else {
										$(a).attr("title", '<spring:message code="tooltip.Archive" />');
									}
									
									$(a).append("<span class='glyphicon glyphicon-import' style='color: #ccc'></span>");
									$(td).append(a);
								}
								
								 $(td).append("&nbsp;");
								
								if (list[i].state != 'Running')
								{
									var a = document.createElement("a");
									$(a).attr("onclick","showDeleteDialog('" + list[i].id  + "')").addClass("actionRowAction").attr("rel","tooltip").attr("data-toggle","tooltip").attr("title", '<spring:message code="label.Delete" />').append("<span class='glyphicon glyphicon-remove'></span></a>");
									$(a).attr("href", "#");
									$(td).append(a);
								} else if (!list[i].fullFormManagementRights) {
									$(td).append("<a class='actionRowAction disabled' rel='tooltip' data-toggle='tooltip' title='<spring:message code="label.Delete" />'><span class='glyphicon glyphicon-remove' style='color: #ccc'></span>");
								} else {
									$(td).append("<a class='actionRowAction disabled' rel='tooltip' data-toggle='tooltip' title='<spring:message code="info.CannotDeleteRunningSurvey" />'><span class='glyphicon glyphicon-remove' style='color: #ccc'></span>");
							 	}
								
								$(td).append("&nbsp;");
								
								$(tr).append(td);
						  }	
						  $(tbody).append(tr);
						  $(table).append(tbody);						  
						  $(div).append(table);
						
						 $("#results").append(div);
						 
						 $(div).find('[data-toggle="tooltip"]').tooltip(); 
					  }
					  
					  $(".titletooltip").hover(
						  function() {
							    $( this ).parent().find(".completetitle").show();
							  }, function() {
								  $( this ).parent().find(".completetitle").hide();
							  }
							);
					  
					  $( "#wheel" ).hide();
					  
					  loadingmore = false;
				  
				}});
		}
		
		var dateHidden = false;
		function showHideDateOptions()
		{
			if (dateHidden)
			{
				$("#date-options-div").show();
				$("#date-options-a").html('<spring:message code="label.hideDateOption" />');
			} else {
				$("#date-options-div").hide();
				$("#date-options-a").html('<spring:message code="label.ShowDateOptions" />');
			}
			dateHidden = !dateHidden;
		}
		
		function goToSurvey()
		{
			window.location.href = "${contextpath}/" + surveyID + "/management/overview";
		}
		
		function reloadSurveys()
		{
			$("#load-forms").submit();
		}
				
		function resetSearch()
		{
			$("#clearFilter").val("true");
			$("#load-forms").submit();
		}
		
		function sort(key, ascending)
		{
			$("#sortkey").val(key);
			if (ascending)
			{
				$("#sortorder").val("ASC");
			} else {
				$("#sortorder").val("DESC");
			}
			$("#load-forms").submit();
		}
		
		var dateHidden = true;
		function showHideDateOptions()
		{
			if (dateHidden)
			{
				$("#date-options-div").show();
				$("#date-options-a").html('<spring:message code="label.hideDateOption" />');
			} else {
				$("#date-options-div").hide();
				$("#date-options-a").html('<spring:message code="label.ShowDateOptions" />');
			}
			dateHidden = !dateHidden;
		}
		
		function hideMessages()
		{
			$('#deletedMessage').hide(400);
		}
		
		function checkDependencies(input)
		{
			//dummy
		}
		
	</script>
		
</head>
<body>
	 <div class="page-wrap">
		<%@ include file="../header.jsp" %>		
		<%@ include file="../menu.jsp" %>	
		
		<form:form modelAttribute="paging" id="load-forms" method="POST" action="${contextpath}/forms" onsubmit="if(validateInput($('#load-forms'))) {$('.tableFloatingHeader').empty(); $('.modal-backdrop').hide(); $('#generic-wait-dialog').modal('show');} else {return false};">
		
		<div class="hideme">
			<c:set var="pagingElementName" value="Form" />			
			<div><%@ include file="../paging.jsp" %></div>	
		</div>	
		
		<div class="fullpagesmall" style="padding-top: 25px; width: 1024px; margin-left: auto; margin-right: auto;">
			<div>		
				<input type="hidden" name="delete" id="delete" value="" />
				<input type="hidden" name="clearFilter" id="clearFilter" value="false" />
				<input type="hidden" name="sortkey" id="sortkey" value="${filter.sortKey}" />
				<input type="hidden" name="sortorder" id="sortorder" value="${filter.sortOrder}" />
						
				<div style="margin-top: 10px;">			
					
						<div class="right-area">
						
							<div style="font-size: 20px; font-weight: bold; margin-bottom: 10px;"><spring:message code="label.SearchCriteria" /></div>
						
							<input rel="tooltip" title="<spring:message code="label.Search" />" type="submit" class="btn btn-primary" value="<spring:message code="label.Search" />" />
							<a rel="tooltip" title="<spring:message code="label.ResetFilter" />" onclick="resetSearch()" class="btn btn-default"><spring:message code="label.Reset" /></a><br /><br />
							
							<h4 style="margin-top: 20px;"><spring:message code="label.Surveys" />:</h4>     
 							<c:choose>
								<c:when test='${filter.selector == "all" || filter.selector == "my"}'>
 									<input class="check" checked="checked" value="own" type="checkbox" name="surveysOwn"/> <spring:message code="label.MySurveys" />
								</c:when>
								<c:otherwise>                                                                                                                   
									<input class="check" value="own" type="checkbox" name="surveysOwn"/> <spring:message code="label.MySurveys" />
								</c:otherwise>                                                                                                          
							</c:choose>                     
							<br />
							<c:choose>
								<c:when test='${filter.selector == "all" || filter.selector == "shared"}'>
									<input class="check" checked="checked" value="shared" type="checkbox" name="surveysShared" /> <spring:message code="label.SharedWithMe" />
								</c:when>
								<c:otherwise>                                                                                                                   
									<input class="check" value="shared" type="checkbox" name="surveysShared" /> <spring:message code="label.SharedWithMe" />      
								</c:otherwise>                                                                                                          
							</c:choose>					
									
							<h4 style="margin-top: 20px;"><spring:message code="publicSurveys.order" />:</h4>
							
							<spring:message code="label.UniqueIdentifier" />
							<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" class="sortlink" onclick="sort('surveyname',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" class="sortlink" onclick="sort('surveyname',false);" class=""><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a><br />
							
							<spring:message code="label.CreationDate" />
							<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" class="sortlink" onclick="sort('survey_created',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" class="sortlink" onclick="sort('survey_created',false);" class=""><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a><br />
							
							<spring:message code="label.ExpiryDate" />
							<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" class="sortlink" onclick="sort('survey_end_date',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" class="sortlink" onclick="sort('survey_end_date',false);" class=""><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a><br />
						
							<spring:message code="label.NumberOfReplies" />
							<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" class="sortlink" onclick="sort('replies',true);" class=""><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a><a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" class="sortlink" onclick="sort('replies',false);" class=""><span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a><br />
											
							<h4 style="margin-top: 20px; margin-bottom: 5px"><spring:message code="label.Title" /> / <spring:message code="label.Alias" /></h4>
							<input id="txtKeywordSearch" class="form-control" name="keywords" type="text" maxlength="100" style="width:150px; margin-top: 7px; display:inline;" value='<esapi:encodeForHTMLAttribute>${filter.keywords}</esapi:encodeForHTMLAttribute>' /><input rel="tooltip" title="<spring:message code="label.Search" />" type="submit" class="btn btn-default" style="margin-bottom: 3px;" value="OK" id="btnSearchSurveys"/>
							
							<h4 style="margin-top: 20px;"><spring:message code="label.Status" />:</h4>					
					
							<c:choose>
								<c:when test='${filter.status.contains("Unpublished")}'>
									<input class="check" checked="checked" value="Unpublished" type="checkbox" name="statusUnpublished"/> <spring:message code="label.Unpublished" />
								</c:when>
								<c:otherwise>															
									<input class="check" value="Unpublished" type="checkbox" name="statusUnpublished"/> <spring:message code="label.Unpublished" />
								</c:otherwise>														
							</c:choose>			
							<br />
							<c:choose>
								<c:when test='${filter.status.contains("Published")}'>
									<input class="check" checked="checked" value="Published" type="checkbox" name="statusPublished" /> <spring:message code="label.Published" />
								</c:when>
								<c:otherwise>															
									<input class="check" value="Published" type="checkbox" name="statusPublished" /> <spring:message code="label.Published" />	
								</c:otherwise>														
							</c:choose>			
		
							<h4 style="margin-top: 20px; margin-bottom: 5px"><spring:message code="label.Date" /></h4>
								
							<a id="date-options-a" style="cursor:pointer" onclick="showHideDateOptions();"><spring:message code="label.ShowDateOptions" /></a>
							<div id="date-options-div" style="display: none; margin-top: 5px;">
								<label class="bold"><spring:message code="label.CreationDate" /></label>								
								<div class="input-group">
							    	<div class="input-group-addon" onclick="$(this).parent().find('.datepicker').datepicker('show');"><span class="glyphicon glyphicon-calendar" aria-hidden="true"></span></div>
							      	<input class="form-control datepicker date" name="generatedFrom" placeholder="<spring:message code="label.from" />" type="text" maxlength="10" value="<spring:eval expression="filter.generatedFrom" />" style="width: 105px" />
							    </div>							
								<div class="input-group">
							    	<div class="input-group-addon" onclick="$(this).parent().find('.datepicker').datepicker('show');"><span class="glyphicon glyphicon-calendar" aria-hidden="true"></span></div>
							      	<input class="form-control datepicker date" name="generatedTo" placeholder="<spring:message code="label.To" />" type="text" maxlength="10" value="<spring:eval expression="filter.generatedTo" />" style="width: 105px" />
							    </div><br />
								
								<label class="bold"><spring:message code="label.StartDate" /></label>
								<div class="input-group">
							    	<div class="input-group-addon" onclick="$(this).parent().find('.datepicker').datepicker('show');"><span class="glyphicon glyphicon-calendar" aria-hidden="true"></span></div>
							      	<input class="form-control datepicker date" name="startFrom" placeholder="<spring:message code="label.from" />" type="text" maxlength="10" value="<spring:eval expression="filter.startFrom" />" style="width: 105px" />
							    </div>							
								<div class="input-group">
							    	<div class="input-group-addon" onclick="$(this).parent().find('.datepicker').datepicker('show');"><span class="glyphicon glyphicon-calendar" aria-hidden="true"></span></div>
							      	<input class="form-control datepicker date" name="startTo" placeholder="<spring:message code="label.To" />" type="text" maxlength="10" value="<spring:eval expression="filter.startTo" />" style="width: 105px" />
							    </div><br />
							    
								<label class="bold"><spring:message code="label.ExpiryDate" /></label>
								<div class="input-group">
							    	<div class="input-group-addon" onclick="$(this).parent().find('.datepicker').datepicker('show');"><span class="glyphicon glyphicon-calendar" aria-hidden="true"></span></div>
							      	<input class="form-control datepicker date" name="endFrom" placeholder="<spring:message code="label.from" />" type="text" maxlength="10" value="<spring:eval expression="filter.endFrom" />" style="width: 105px" />
							    </div>
								<div class="input-group">
							    	<div class="input-group-addon" onclick="$(this).parent().find('.datepicker').datepicker('show');"><span class="glyphicon glyphicon-calendar" aria-hidden="true"></span></div>
							      	<input class="form-control datepicker date" name="endTo" placeholder="<spring:message code="label.To" />" type="text" maxlength="10" value="<spring:eval expression="filter.endTo" />" style="width: 105px" />
							    </div>								
							</div>
							
							<h4 style="margin-top: 20px;"><spring:message code="label.Languages" /></h4>
						
							<c:forEach items="${languages}" var="language">
								<c:if test="${language.official}">	
									<input name="languages" type="checkbox" class="check" data-code="<esapi:encodeForHTMLAttribute>${language.code}</esapi:encodeForHTMLAttribute>" value="<esapi:encodeForHTMLAttribute>${language.id}</esapi:encodeForHTMLAttribute>" <c:if test="${filter.containsLanguage(language.id)}">checked="checked"</c:if> /><esapi:encodeForHTML><spring:message code="label.lang.${language.englishName}" /></esapi:encodeForHTML><br />
								</c:if>
							</c:forEach>
							
							<c:if test="${survey.owner.id == USER.id || USER.formPrivilege > 1}">
								<h4 style="margin-top: 20px; margin-bottom: 5px"><spring:message code="label.Owner" /></h4>
								<input class="form-control" name="owner" type="text" maxlength="255" style="width:150px; margin-top: 9px; display:inline" value='<esapi:encodeForHTMLAttribute>${filter.owner}</esapi:encodeForHTMLAttribute>' /><input rel="tooltip" title="<spring:message code="label.Search" />" style="margin-bottom: 3px" type="submit" class="btn btn-default" value="OK" />
							</c:if>
							
							<br /><br />
							<input rel="tooltip" title="<spring:message code="label.Search" />" type="submit" class="btn btn-primary" value="<spring:message code="label.Search" />" />
							<a rel="tooltip" title="<spring:message code="label.ResetFilter" />" onclick="resetSearch()" class="btn btn-default"><spring:message code="label.Reset" /></a><br /><br />
							
							<div class="hideme">
								<span><spring:message code="label.RowsPerPage" />&#160;</span>
							    <form:select onchange="moveTo('${paging.currentPage}')" path="itemsPerPage" id="itemsPerPage" style="width:70px; margin-top: 0px;" class="middle">
									<form:options items="${paging.itemsPerPageOptions}" />
								</form:select>		
							</div>
						</div>
							
						<div class="left-area">
						
							<c:if test="${paging.items.size() == 0}">
								<div id="tbllist-empty" class="noDataPlaceHolder" style="display:block;">
									<p>
										<spring:message code="label.NoDataSearchSurveyText"/>&nbsp;<img src="${contextpath}/resources/images/icons/32/forbidden_grey.png" alt="no data"/>
									<p>
								</div>
							</c:if>
							
							<div id="results"> 
						
								<c:forEach items="${paging.items}" var="survey">
								
								<div class="surveybox">
								
									<c:if test="${survey.numberOfReports > 0 }">
										<div class="surveywarning">
											<spring:message code="warning.ReportedSurvey" arguments="${survey.numberOfReports}" />
										</div>
									</c:if>
								
									<div class="typeicon">
										<c:choose>
											<c:when test="${survey.isQuiz}">
												<img data-toggle="tooltip" title="<spring:message code="label.Quiz" />" style="width: 32px" src="${contextpath}/resources/images/icons/64/quiz.png" />
											</c:when>
											<c:when test="${survey.isOPC}">
												<img data-toggle="tooltip" title="<spring:message code="label.OPC" />" src="${contextpath}/resources/images/icons/24/people.png" />
											</c:when>
											<c:when test="${survey.isDelphi}">
												<img data-toggle="tooltip" title="<spring:message code="label.Delphi" />" src="${contextpath}/resources/images/icons/24/delphi.png" />
											</c:when>
											<c:otherwise>
												<img data-toggle="tooltip" title="<spring:message code="label.StandardSurvey" />" style="width: 32px" src="${contextpath}/resources/images/icons/64/survey.png" />
											</c:otherwise>
										</c:choose>
									</div>
																	
									<div class="surveyItemHeader">
										<div style="float: right; margin-left: 20px;">
											<c:choose>
												<c:when test='${survey.isActive && survey.isPublished}'>
													<div class="publishedsurveytag">
														<div class="arrow-left"></div>
														<spring:message code="label.Published" />
													</div>
												</c:when>
												<c:otherwise>												
													<div class="unpublishedsurveytag">
														<div class="arrow-left"></div>
														<spring:message code="label.Unpublished" />
													</div>
												</c:otherwise>
											</c:choose>
											<c:choose>
												<c:when test='${survey.isPublished && survey.hasPendingChanges}'>
													<div class="pendingchangessurveytag">
														<div class="arrow-left"></div>
														<spring:message code="label.PendingChanges" />
													</div>
												</c:when>
												<c:otherwise>
												</c:otherwise>
											</c:choose>
										</div>				
										<div class="surveytitle">${survey.mediumCleanTitle()}<span class="completetitle hideme">${survey.cleanTitle()}</span></div>
										<div class="originalsurveytitle hideme">${survey.title}</div>
										<div class="shortname"><esapi:encodeForHTML>${survey.shortname}</esapi:encodeForHTML></div>
									</div>
									<br />
									<table class="formsSurveyItem">
										<tr>
											<td class="labelcell"><spring:message code="label.Created" /></td>
											<td><spring:eval expression="survey.created" /></td>
											<td class="labelcell"><spring:message code="label.Owner" /></td>
											<td><spring:eval expression="survey.owner.name" /></td>
										</tr>
										<tr>
											<td class="labelcell"><spring:message code="label.StartDate" /></td>
											<td>
												<c:choose>
													<c:when test="${!survey.automaticPublishing || survey.start == null}">
														<spring:message code="label.NotSpecified" />
													</c:when>
													<c:otherwise>
														<spring:eval expression="survey.start" />
													</c:otherwise>
												</c:choose>												
											</td>								
											<td class="labelcell"><spring:message code="label.ExpiryDate" /></td>
											<td>
												<c:choose>
													<c:when test="${!survey.automaticPublishing || survey.end == null}">
														<spring:message code="label.NotSpecified" />
													</c:when>
													<c:otherwise>
														<spring:eval expression="survey.end" />
													</c:otherwise>
												</c:choose>									
											</td>
										</tr>
										<tr>
											<td class="labelcell"><spring:message code="label.Replies" /></td>
											<td><esapi:encodeForHTML>${survey.numberOfAnswerSetsPublished}</esapi:encodeForHTML></td>									
											<td class="labelcell"><spring:message code="label.Translations" /></td>
											<td>
												<c:forEach items="${survey.translations}" var="language">
													<c:choose>
														<c:when test="${survey.language.code == language}">
															<div data-toggle="tooltip" title="<spring:message code="label.PivotLanguage" />" class="language pivotlanguage"><esapi:encodeForHTML>${language}</esapi:encodeForHTML></div>
														</c:when>
														<c:when test="${survey.completeTranslations != null && survey.containsCompleteTranslations(language)}">
															<div data-toggle="tooltip" title="<spring:message code="label.Available" />" class="language"><esapi:encodeForHTML>${language}</esapi:encodeForHTML></div>
														</c:when>
														<c:otherwise>
															<div data-toggle="tooltip" title="<spring:message code="label.NotYetAvailable" />" class="language languageUnpublished"><esapi:encodeForHTML>${language}</esapi:encodeForHTML></div>
														</c:otherwise>
													</c:choose>											
												</c:forEach>		
											</td>
										</tr>
										<tr>
											<td colspan="5" class="separatorCell"></td>
										</tr>
										
										<c:if test="${survey.owner.id == USER.id || USER.formPrivilege > 0}">
										<tr>
											<td colspan="5"class="surveyactions">
												<a class="actionRowAction" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Open" />" id="lnk${survey.shortname}" href="<c:url value="/${survey.shortname}/management/overview" />"><span class="glyphicon glyphicon-folder-open"></</span></a>
											
												<c:choose>
													<c:when test="${survey.formManagementRights && survey.canCreateSurveys}">
														<a onclick="copySurvey('${survey.id}', $(this).closest('.surveybox').find('.originalsurveytitle').html(), '${survey.language.code}', '${survey.security}', '${survey.isQuiz}', '${survey.isDelphi}')" class="actionRowAction" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Copy" />"><span class="glyphicon glyphicon-copy"></</span></a>
														<a href="<c:url value="/noform/management/exportSurvey/false/${survey.shortname}"/>" class="actionRowAction" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Export" />"><span class="glyphicon glyphicon-download-alt"></</span></a>
													</c:when>
													<c:otherwise>
														<a class="disabled actionRowAction" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Copy" />"><span class="glyphicon glyphicon-copy disabled"></</span></a>
														<a class="disabled actionRowAction" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Export" />"><span class="glyphicon glyphicon-download-alt disabled"></</span></a>
													</c:otherwise>
												</c:choose>
												
												<c:if test="${enablearchiving}">
													<c:choose>
														<c:when test="${survey.state != 'Running' && (survey.owner.id.equals(USER.id) || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('FormManagement') > 1)}">
															<a id="btnArchiveSurvey" class="actionRowAction"  onclick="showArchiveDialog('${survey.shortname}','${survey.id}', true)" rel="tooltip" data-toggle="tooltip" title="<spring:message code="tooltip.Archive" />"><span class="glyphicon glyphicon-import"></</span></a>
														</c:when>
														<c:when test="${!survey.fullFormManagementRights}">
															<a id="btnArchiveSurvey" class="disabled actionRowAction" rel="tooltip" data-toggle="tooltip" title="<spring:message code="tooltip.Archive" />"><span class="glyphicon glyphicon-import" style="color: #ccc"></</span></a>
														</c:when>
														<c:otherwise>
															<a id="btnArchiveSurvey" class="disabled actionRowAction" rel="tooltip" data-toggle="tooltip" title="<spring:message code="tooltip.ArchiveDisabled" />"><span class="glyphicon glyphicon-import" style="color: #ccc"></</span></a>
														</c:otherwise>
													</c:choose>				
												</c:if>
	
												<c:choose>
													<c:when test="${survey.state != 'Running' && (survey.fullFormManagementRights)}">
														<a id="deleteBtnEnabledFromListSurvey" onclick="showDeleteDialog('${survey.id}');"  class="actionRowAction" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Delete" />"><span class="glyphicon glyphicon-remove"></</span></a>
													</c:when>
													<c:when test="${!survey.fullFormManagementRights}">
														<a id="deleteBtnDisabledFromListSurvey" class="actionRowAction disabled" rel="tooltip" data-toggle="tooltip" title="<spring:message code="label.Delete" />"><span class="glyphicon glyphicon-remove" style="color: #ccc"></</span></a>
													</c:when>
													<c:otherwise>
														<a id="deleteBtnDisabledFromListSurvey" class="actionRowAction disabled" rel="tooltip" data-toggle="tooltip" title="<spring:message code="info.CannotDeleteRunningSurvey" />"><span class="glyphicon glyphicon-remove" style="color: #ccc"></</span></a>
													</c:otherwise>
												</c:choose>
								
											</td>
										</tr>
										</c:if>	
										
									</table>	
	
								</div>					
								
								</c:forEach>
					
							</div>
						
							<img id="wheel" class="hideme" style="margin-left: 50px" src="${contextpath}/resources/images/ajax-loader.gif" />
		
						</div>
	
					</div>			
				</div>
		</div>
		
		<div style="clear: both"> </div>
	
		</form:form>
	</div>

	<%@ include file="../footer.jsp" %>	
	<%@ include file="../import-survey-dialog.jsp" %>	
	
	<c:if test="${showDates != null}">
	
		<script type="text/javascript"> 
			showHideDateOptions();
		</script>
		
	</c:if>	
	
	<c:if test="${message != null}">
		<script type="text/javascript">
			showMessage('${message}');
		</script>
	</c:if>
	
	<c:if test="${deleted != null}">
		<script type="text/javascript">
			var t = '<spring:message code="message.SurveyDeleted" />';
			showSuccess(t.replace(/X/g, '${deletedShortname}'));
		</script>
	</c:if>

</body>
</html>
