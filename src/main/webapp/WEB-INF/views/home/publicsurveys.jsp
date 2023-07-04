<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="header.AllPublicSurveys" /></title>	
	<%@ include file="../includes.jsp" %>	
	<link href="${contextpath}/resources/css/management.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />	
	<link href="${contextpath}/resources/css/form.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	
	<style type="text/css">
	
		 .right-area { 
		 	float: right;  
		 	border-left: 1px solid #ddd;  
		 	padding-left: 20px;  
		 	padding-right: 30px; 
		 	margin-bottom: 30px;
		 	width: 220px; 
		 } 		
		
		 .left-area { 
		 	float: left;  
		 	margin-top: 10px;
		 	<c:if test="${responsive == null}">		 	
		 	padding: 20px;  
		 	width: 580px; 
		 	 </c:if>
		 }		
		 
		 .surveytitle {
		 	font-weight: bold; 
		 	font-size: 200%; 
		 	line-height: normal;
		 }
		 
		 #results td {
		 	padding-left: 10px;
		 }
		 
		 #results table {
		 	margin-top: 10px;
		 }

	</style>
	
	<script type="text/javascript"> 
		$(function() {					
			
			$("#date-options-div").find("input").each(function(){
				if ($(this).val().length > 0)
					dateHidden = true;
			});
			
			showHideDateOptions();
			
			<c:if test="${runnermode != null }">
				 $(".headerlink, .header a").each(function(){
					 if ($(this).attr("href").indexOf("?") == -1)
					$(this).attr("href", $(this).attr("href") + "/runner");
				 });
			</c:if>
			
			$(window).scroll(function() {
			    if ($(window).scrollTop() <= $(document).height() - $(window).height() && $(window).scrollTop() >= $(document).height() - $(window).height() - 10) {
			    	loadMore();
			  }
			 });
		});			
		
		var newPage = 2;
		var inLoadMore = false;
		function loadMore()
		{
			if (inLoadMore)
			{
				return;	
			} else {
				inLoadMore = true;
			}
						
			$( "#wheel" ).show();
			var s = "page=" + newPage++ + "&rows=10";	
			
			$.ajax({
				type:'GET',
				  url: "${contextpath}/home/publicsurveysjson",
				  dataType: 'json',
				  data: s,
				  cache: false,
				  error: function() {
					console.log("error in publicsurveysjson");
					inLoadMore = false;
				  },
				  success: function( list ) {
				  
					  for (var i = 0; i < list.length; i++ )
					  {
						  var div = document.createElement("div");
						  $(div).addClass("surveybox");
						  
						  var titlediv = document.createElement("div");
						  $(titlediv).addClass("surveytitle").html(list[i].title.stripHtml115());
						  $(titlediv).append('<span class="completetitle hide">' + list[i].title + '</span>');
						
						  var outerdiv = document.createElement("div");
						  $(outerdiv).addClass("surveyItemHeader").append(titlediv);
						  
						  $(div).append(outerdiv);
						  
						  var table = document.createElement("table");
						  var tr = document.createElement("tr");
						  
						  $(tr).append('<td><b><spring:message code="label.Contact" /></b></td>');
						  
						  if (list[i].contact.indexOf("@") > -1)
						  {
							  $(tr).append("<td><a href='mailto:" + list[i].contact + "'><i class='icon icon-envelope' style='vertical-align: middle'></i>" + list[i].contact + "</a></td>");
						  } else {
							  $(tr).append("<td><a href='" + list[i].contact + "'>" + list[i].fixedContactLabel + "</a></td>");
						  }
						  $(table).append(tr);
						  
						  if (list[i].start != null)
						  {
							  $(table).append('<tr><td><b><spring:message code="label.StartDate" /></b></td><td>' + list[i].startString + '</td></tr>');							  
						  }
						  if (list[i].end != null)
						  {
							  $(table).append("<tr><td><b><spring:message code="label.ExpiryDate" /></b></td><td>" + list[i].endString + "</td></tr>");							  
						  }
						  
						  $(table).append('</tr>');
						  
						  $(div).append(table);
						  $(div).append("<br />");
						  $(div).append('<div style="padding: 5px;"><a class="btn btn-default" href="${contextpath}/runner/' + list[i].shortname + '"><spring:message code="label.AnswerThisSurvey" /></a></div>');
						
						 $("#results").append(div);
					  }
					  
					  $(".titletooltip").hover(
							  function() {
								    $( this ).parent().find(".completetitle").show();
								  }, function() {
									  $( this ).parent().find(".completetitle").hide();
								  }
								);
					  
					  $( "#wheel" ).hide();
					  inLoadMore = false;
				  
				}});
		}
	
		function resetSearch()
		{
			$("#clearFilter").val("true");
			$("#load-forms").submit();
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
		
	</script>
		
</head>
<body class="grey-background">
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>	
	
		<c:choose>
			<c:when test="${responsive != null}">
				<div class="page" style="width: auto">
			</c:when>
			<c:otherwise>
				<div class="page">
			</c:otherwise>
		</c:choose>		
		
			<form:form id="load-forms" method="POST" action="${contextpath}/home/publicsurveys" onsubmit="if(validateInput($('#load-forms'))) {$('#show-wait-image').modal('show');} else {return false};">
			
				<input type="hidden" name="sort" value="${filter.sortKey}" />
				<input type="hidden" name="itemsPerPage" value="10" />
		
				<div class="white-background grey-border" style="padding: 10px; margin-top: 0px;">
				
					<c:choose>
						<c:when test="${responsive != null}">
							<div style="margin-top: 10px; line-height: normal; font-size: 22px; font-weight: bold;">
						</c:when>
						<c:otherwise>
							<div style="margin-top: 10px; line-height: normal; font-size: 44px; font-weight: bold;">
						</c:otherwise>
					</c:choose>
						<c:choose>
							<c:when test="${oss != false}">
								<!--  OSS VERSION OF TEXT -->
								<spring:message code="publicSurveys.oss.newtitle" />
							</c:when>
							<c:otherwise>
								<!--  ENTERPRISE VERSION OF TEXT -->
								<spring:message code="publicSurveys.newtitle" />
							</c:otherwise>
						</c:choose>
					</div>
					
					<div style="margin-top: 50px;">				
						<c:choose>
							<c:when test="${responsive != null}">
								<div style="float: left; max-width: 70%">
									<c:choose>
										<c:when test="${oss != false}">
											<!--  OSS VERSION OF TEXT -->
											<spring:message code="publicSurveys.oss.newtext" />
										</c:when>
										<c:otherwise>
											<!--  ENTERPRISE VERSION OF TEXT -->
											<spring:message code="publicSurveys.newtext2" />
										</c:otherwise>
									</c:choose>
									
								</div>
								<div style="float: right; max-width: 30%">
									<img style="max-width: 100%" src="<c:url value="/resources/images/pencil.png"/>" />
								</div>
								
							</c:when>
							<c:otherwise>
								<div class="well">
									<div style="float: left">
										<img style="width: 150px" src="<c:url value="/resources/images/pencil.png"/>" />	
									</div>
									<div style="float: right; width: 650px;">
										<c:choose>
											<c:when test="${oss != false}">
												<!--  OSS VERSION OF TEXT -->
												<spring:message code="publicSurveys.oss.newtext" />
											</c:when>
											<c:otherwise>
												<!--  ENTERPRISE VERSION OF TEXT -->
												<spring:message code="publicSurveys.newtext2" />
											</c:otherwise>
										</c:choose>										
									</div>
									<div style="clear: both"></div>								
								</div>
							</c:otherwise>					
						</c:choose>				
					</div>
					
					<c:if test="${responsive == null}">
				
						<div class="right-area" style="margin-top: 40px;">
						
							<h4><spring:message code="publicSurveys.order" />:</h4>
							<a onclick="$('#show-wait-image').modal('show');" href="<c:url value="/home/publicsurveys?sort=publication"/>"><spring:message code="label.PublicationDate" /></a><br />
							<a onclick="$('#show-wait-image').modal('show');" href="<c:url value="/home/publicsurveys?sort=expiration"/>"><spring:message code="label.ExpiryDate" /></a><br />
							<a onclick="$('#show-wait-image').modal('show');" href="<c:url value="/home/publicsurveys?sort=popularity"/>"><spring:message code="label.Popularity" /></a><br /><br />
							
							<h4 style="margin-bottom: 10px"><spring:message code="label.SearchSurveys" /></h4>
							
							<label class="bold"><spring:message code="label.Keywords" /></label>
							<input name="keywords" type="text" maxlength="100" style="width:150px; margin-top: 8px;" value='<esapi:encodeForHTMLAttribute>${filter.keywords}</esapi:encodeForHTMLAttribute>' /><input type="submit" class="btn btn-default" value="OK" />
							
							<br /><br /><label class="bold"><spring:message code="label.Languages" /></label><br />
							
							<c:forEach items="${languages}" var="language">
								<c:if test="${language.official}">	
									<input name="languages" type="checkbox" class="check" value="${language.id}" <c:if test="${filter.containsLanguage(language.id)}">checked="checked"</c:if> /><esapi:encodeForHTML><spring:message code="label.lang.${language.englishName}" /></esapi:encodeForHTML><br />
								</c:if>
							</c:forEach>
		
							<h4 style="margin-bottom: 10px"><spring:message code="label.ByDate" /></h4>
							
							<a id="date-options-a" style="cursor:pointer" onclick="showHideDateOptions();"><spring:message code="label.ShowDateOptions" /></a>
							<div id="date-options-div" style="display: none">
								<label class="bold"><spring:message code="label.StartDate" /></label>
								<div class="controls">
									<div class="input-group">
							    		<div class="input-group-addon" onclick="$(this).parent().find('.datepicker').datepicker('show');"><span class="glyphicon glyphicon-calendar" aria-hidden="true"></span></div>
							    		<input class="form-control datepicker date" name="startFrom" placeholder="from" type="text" maxlength="10" value="<spring:eval expression="filter.startFrom" />" style="width: 105px" />
									</div>
								</div>
								<div class="controls">
									<div class="input-group">
							    		<div class="input-group-addon" onclick="$(this).parent().find('.datepicker').datepicker('show');"><span class="glyphicon glyphicon-calendar" aria-hidden="true"></span></div>
							    		<input class="form-control datepicker date" name="startTo" type="text" placeholder="to" maxlength="10" value="<spring:eval expression="filter.startTo" />" style="width: 105px" />
									</div>
								</div>
								<label class="bold"><spring:message code="label.ExpiryDate" /></label>
								<div class="controls">
									<div class="input-group">
							    		<div class="input-group-addon" onclick="$(this).parent().find('.datepicker').datepicker('show');"><span class="glyphicon glyphicon-calendar" aria-hidden="true"></span></div>
							    		<input class="form-control datepicker date" name="endFrom" type="text" placeholder="from" maxlength="10" value="<spring:eval expression="filter.endFrom" />" style="width: 105px" />
									</div>
								</div>
								<div class="controls">
									<div class="input-group">
							    		<div class="input-group-addon" onclick="$(this).parent().find('.datepicker').datepicker('show');"><span class="glyphicon glyphicon-calendar" aria-hidden="true"></span></div>
							    		<input class="form-control datepicker date" name="endTo" type="text" placeholder="to" maxlength="10" value="<spring:eval expression="filter.endTo" />" style="width: 105px" />
									</div>
								</div>									
							</div>
							
							<br />
						<input type="submit" class="btn btn-default" value="<spring:message code="label.Refresh" />" />
							
							<h4 style="margin-top: 20px;"><spring:message code="label.PopularSurveys" /></h4>
							
							<c:forEach items="${popularSurveys}" var="survey">
								<a href="<c:url value="/runner/${survey.shortname}"/>">${survey.shortCleanTitle()}</a><br />
							</c:forEach>
															
						</div>
					</c:if>
						
					<div id="results" class="left-area">				
						<c:forEach items="${paging.items}" var="survey">				
							<div class="surveybox">
								<div class="surveyItemHeader">
									<div class="surveytitle">${survey.mediumCleanTitle()}<span class="completetitle hide">${survey.cleanTitle()}</span></div>
								</div>
								<table>
								<tr>
									<td><b><spring:message code="label.Contact" /></b></td>
									<td>
										<c:choose>
											<c:when test='${survey.contact.contains("@")}'>
												<a href='mailto:<esapi:encodeForHTMLAttribute>${survey.contact}</esapi:encodeForHTMLAttribute>'><i class="icon icon-envelope" style="vertical-align: middle"></i> <esapi:encodeForHTML>${survey.contact}</esapi:encodeForHTML></a>
											</c:when>
											<c:otherwise>
												<a href='<esapi:encodeForHTMLAttribute>${survey.contact}</esapi:encodeForHTMLAttribute>' target="_blank"><esapi:encodeForHTML>${survey.fixedContactLabel}</esapi:encodeForHTML></a>
											</c:otherwise>
										</c:choose>
									</td>											
								</tr>							
								<c:if test="${survey.start != null}">
									<tr>
										<td><b><spring:message code="label.StartDate" /></b></td>
										<td><spring:eval expression="survey.start" /></td>
									</tr>
								</c:if>	
								<c:if test="${survey.end != null}">
									<tr>
										<td><b><spring:message code="label.ExpiryDate" /></b></td>
										<td><spring:eval expression="survey.end" /></td>
									</tr>
								</c:if>
								</table>					
								<br />
								<div style="padding: 5px;">
									<a href="<c:url value="/runner/${survey.shortname}"/>" class="btn btn-default"><spring:message code="label.AnswerThisSurvey" /></a>
								</div>
							</div>					
						</c:forEach>			
					</div>
					
					<div style="clear: both"></div>
					<img id="wheel" class="hideme" style="margin-left: 50px" src="${contextpath}/resources/images/ajax-loader.gif" />
		
				</div>
				
			</form:form>
		</div>
	</div>

	<%@ include file="../footer.jsp" %>	

</body>
</html>
