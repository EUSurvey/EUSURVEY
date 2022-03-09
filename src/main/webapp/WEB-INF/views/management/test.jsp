<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Test" /></title>
	
	<%@ include file="../includes.jsp" %>
	
	<link id="runnerCss" href="${contextpath}/resources/css/runner.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/management.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/fileuploader.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	
	<c:choose>
		<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("New Official EC Skin")}'>
			<link href="${contextpath}/resources/css/ecnew.css" rel="stylesheet" type="text/css"></link>
		</c:when>
		<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("ECA Skin")}'>
			<link href="${contextpath}/resources/css/ecanew.css" rel="stylesheet" type="text/css"></link>
		</c:when>
		<c:when test="${form.survey.skin != null && !form.wcagCompliance}">
			<style type="text/css">
				${form.survey.skin.getCss()}
			</style>
		</c:when>
	</c:choose>
	
	<style type="text/css">
		#runner-content {
			background-color: #fff;
		}
		.right-area {
			border-left: 2px solid #eee;
		}
		.page {
		 	background-color: #fff;
		}
		.progressBar {
			top: 120px;
			bottom: 140px;
		}
		.progressBarPlaceholder {
			height: 0px;
		}
	</style>

	<script type="text/javascript" src="${contextpath}/resources/js/fileuploader.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/runner.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/runner2.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/graph_data_loader.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/runnerviewmodels.js?version=<%@include file="../version.txt" %>"></script>
    <script type='text/javascript' src='${contextpath}/resources/js/knockout-3.5.1.js?version=<%@include file="../version.txt" %>'></script>
	
	<script type="text/javascript"> 
	
		function loadElements() {
			var ids = "";
			
			if ($(".emptyelement").length > 0)
			{				
				var counter = 0;
				
				$(".emptyelement").each(function(){
					ids += $(this).attr("data-id") + '-';
					counter++;
					if (counter > 20)
					{
						return false;	
					}
				})	
			 
			 	var s = "ids=" + ids + "&survey=${form.survey.id}&slang=${form.language.code}&as=${answerSet}";
				
				$.ajax({
					type:'GET',
					dataType: 'json',
					url: "${contextpath}/runner/elements/${form.survey.id}",
					data: s,
					cache: false,
					success: function( result ) {	
						for (var i = 0; i < result.length; i++)
						{
							addElement(result[i], false, false);
						}
						applyStandardWidths();
						setTimeout(loadElements, 500);
						selectPageAndScrollToQuestionIfSet();
					},
					error: function( result ) {	
						alert(result);
					}
				});				
			} else {
				applyStandardWidths();
				checkPages();
				updateAllFormulas();
				readCookies();
				updateProgress();
				$("#btnSubmit").removeClass("hidden");
				$("#btnSaveDraft").removeClass("hidden");
				$("#btnSaveDraftMobile").removeClass("hidden");
				$('[data-toggle="tooltip"]').tooltip(); 
			}
		}
	
		$(function() {					
			$("#form-menu-tab").addClass("active");
			$("#test-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
			
			loadElements();
		});			
		
		function updateFileList(element, responseJSON) {
					
			$(element).siblings(".uploaded-files").first().empty();
			
			$(element).siblings(".validation-error").remove();
			
			var surveyElement = $(element).closest(".survey-element");
			enableDelphiSaveButtons(surveyElement);

			for (var i = 0; i < responseJSON.files.length; i++) {
				var f = responseJSON.files[i];
				var div = document.createElement("div");
				
				var del = document.createElement("a");
				$(del).attr("data-toggle","tooltip").attr("title","${form.getMessage("label.RemoveUploadedFile")}").attr("href", "#").attr(
						"onclick",
						"deleteFile('" + $(element).attr('data-id') + "','"
								+ $("#uniqueCode").val() + "','" + f + "', this);return false;");
				$(del).tooltip(); 
				
				var ic = document.createElement("span");
				$(ic).addClass("glyphicon glyphicon-trash").css("margin-right",
						"10px");
				$(del).append(ic);		
				$(div).html(f);		
				$(div).prepend(del);
				
				$(element).siblings(".uploaded-files").first().append(div);
			}
		}
	</script>
		
</head>
<body id="bodyManagementTest">
	<div class="page-wrap">
		<a href="#surveystart">${form.getMessage("label.SkipToMain")}</a>
	
		<%@ include file="../header.jsp" %>
		<%@ include file="../menu.jsp" %>
		<%@ include file="formmenu.jsp" %>	
		
		<div class="fullpageform100">				
			
			<div style="margin-top: 0px;">
			
			<c:set var="mode" value="test" />
			<%@ include file="../runner/runnercontent.jsp" %>	
			
			</div>	
			
		</div>
	</div>

	<%@ include file="../footer.jsp" %>	
	
	<c:if test="${message != null}">
		<script type="text/javascript">
			showError('${message}');
		</script>
	</c:if>

	<c:if test="${form.validation != null && form.validation.size() > 0}">
		<script type="text/javascript">
			goToFirstValidationError($("form"));
		</script>
	</c:if>

</body>
</html>
