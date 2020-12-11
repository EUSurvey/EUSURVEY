<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:og="http://ogp.me/ns#" xml:lang="${form.language.code}" lang="${form.language.code}">
<head>
	<title>EUSurvey - <spring:message code="label.Survey" /></title>
	<meta property="og:title" content="${form.survey.cleanTitle()}"></meta>
	<%@ include file="../includesrunner.jsp" %>
	
	<c:choose>
		<c:when test="${responsive != null}">
			<link id="runnerCss" href="${contextpath}/resources/css/runnerresponsive.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css"></link>
		</c:when>
		<c:otherwise>
			<link id="runnerCss" href="${contextpath}/resources/css/runner.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css"></link>
		</c:otherwise>
	</c:choose>
	
	<link href="${contextpath}/resources/css/fileuploader.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css"></link>

	<script type="text/javascript" src="${contextpath}/resources/js/fileuploader.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/runner.js?version=<%@include file="../version.txt" %>"></script>
      
	<c:if test="${forpdf==null}">
		<script type="text/javascript" src="${contextpath}/resources/js/runner2.js?version=<%@include file="../version.txt" %>"></script>
		<script type="text/javascript" src="${contextpath}/resources/js/runnerviewmodels.js?version=<%@include file="../version.txt" %>"></script>
	    <script type='text/javascript' src='${contextpath}/resources/js/knockout-3.5.1.js?version=<%@include file="../version.txt" %>'></script>
   	</c:if>		
   
    <script type="text/javascript">
		//<![CDATA[
	
		$(function() {
			 $(".headerlink, .header a").each(function(){
				 if (!$(this).hasClass("messageicon") && $(this).attr("id") != 'logoutBtnFromHeader'  && !$(this).hasClass("skipScriptAnchor"))
				 {
					if ($(this).attr("href") && $(this).attr("href").indexOf("?") == -1)
					{
					 $(this).attr("target","_blank").attr("href", $(this).attr("href") + "/runner");
					}
				 }				
			 });
			 
			 loadElements();
		});
		
		function loadElements()
		{
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
						 
			 	var s = "ids=" + ids.substring(0, ids.length-1) + "&survey=${form.survey.id}&slang=${form.language.code}&as=${answerSet}";
				
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
					}
				});
			} else {
				checkPages();
				readCookies();
				
				<c:if test="${form.validation != null && form.validation.size() > 0}">
					goToFirstValidationError($("form"));
				</c:if>
								
				$("#btnSubmit").removeClass("hidden");
				$("#btnSaveDraft").removeClass("hidden");
				$("#btnSaveDraftMobile").removeClass("hidden");
				$('[data-toggle="tooltip"]').tooltip(); 
			}
		}
		
		function updateFileList(element, responseJSON) {
						
			$(element).siblings(".uploaded-files").first().empty();
			
			$(element).siblings(".validation-error").remove();
			
			var surveyElement = $(element).closest(".survey-element");
			$(surveyElement).find("a[data-type='delphisavebutton']").removeClass("disabled");

			for (var i = 0; i < responseJSON.files.length; i++) {
				var f = responseJSON.files[i];
				var div = document.createElement("div");
				
				
				
				var del = document.createElement("a");
				$(del).attr("data-toggle","tooltip").attr("title","${form.getMessage("label.RemoveUploadedFile")}").attr("href", "#").attr(
						"onclick",
						'deleteFile("' + $(element).attr('data-id') + '","'
								+ $("#uniqueCode").val() + '","' + f + '", this);return false;');
				$(del).tooltip(); 
				
				var ic = document.createElement("span");
				$(ic).addClass("glyphicon glyphicon-trash").css("margin-right",	"10px");
				$(del).append(ic);		
				$(div).html(f);		
				$(div).prepend(del);
				
				$(element).siblings(".uploaded-files").first().append(div);
			}
		}
		
		//]]>
	</script>
	
	<c:choose>
		<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("Official EC Skin")}'>
			<style type="text/css">
			
					body {
	 					background-color: #fff !important;
	 				}
	 				.page {
	 					margin-top:0px;
	 				}
	 				#header {
	 					font-size: 12px;
	 				}
	 				.layout-footer {
	 					font-size: 12px;
	 				}
	 				#services {
	 					position: relative !important;
	 					width: auto;
	 					display: inline;
	 				}
	 				.language-selector {
	 					width: auto;
	 					height : auto;
	 					padding: 0px !important;
	 					top: 0px !important;
	 					position: relative !important;
	 					display: inline;
	 				}
 			</style>
		
			<link href="${contextpath}/resources/css/ec.css" rel="stylesheet" type="text/css"></link>
		</c:when>
		<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("New Official EC Skin")}'>
			<link href="${contextpath}/resources/css/ecnew.css" rel="stylesheet" type="text/css"></link>
		</c:when>
		<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("ECA Skin")}'>
			<link href="${contextpath}/resources/css/ecanew.css" rel="stylesheet" type="text/css"></link>
		</c:when>
		<c:when test="${form.survey.skin != null && !form.wcagCompliance && ismobile == null}">
			<style type="text/css">
				<c:choose>
	 				<c:when test="${forpdf != null}">
	 					 ${form.survey.skin.getCss(true)}
	 				</c:when>
	 				<c:otherwise>
	 					 ${form.survey.skin.getCss(false)}
	 				</c:otherwise>
	 			</c:choose>
			</style>
		</c:when>
	</c:choose>
		
		
	<c:if test="${forpdf != null}">
    <style type="text/css">	
    
      table tr {
      	page-break-inside: avoid;
      }
      
/*       div { */
/*      	 page-break-inside: avoid; */
/*       } */
    
      .optional {
      	visibility: hidden;
      }
    
      body
      {
        font-family: FreeSans;
      }

      @page basic { size: 21cm 29.7cm; } /* A4 */
      
      @page {
		  @bottom-right {
		    content: counter(page);
		  }
		}
      
      
      body {
        page: basic;
      }
      .page {
        page:basic;
        font-size: 10pt;
        margin-top: 9pt;
        margin-left: 2pt;
        margin-right: 28.34pt;
        background-color: #fff;        
      }
      
      #runner-content {
		background-color: #fff;
	}

      .matrixtable td, .matrixtable th { padding: 4pt; padding-left: 9pt; padding-right: 9pt; border: 1pt solid #bbb; }
      .matrixtable { margin-left: 18pt; }			
      
    	.tabletable {
      		width: 95%;
      	}
          
      <c:if test="${form.varPagesEltNb > 0}">
      	
    	<c:forEach var="i" begin="0" end="${form.varPagesEltNb -1}">     
             @page wide_${form.varPagesEltIds.get(i)} { size: ${form.varPagesEltWidths.get(i)}pt 21cm; }
        .elem_${form.varPagesEltIds.get(i)} {
          page: wide_${form.varPagesEltIds.get(i)};
			max-width: ${form.varPagesEltWidths.get(i)}pt;
        }
        
         .table_${form.varPagesEltIds.get(i)} {
          width: ${form.varPagesEltWidths.get(i) - 150}pt !important;
          max-width: ${form.varPagesEltWidths.get(i) - 150}pt;
        }                
        
          </c:forEach>      
	  
	  </c:if>
	  
	  .answer-columns {
	  	max-width: 18cm;
	  }
      
    </style>
  </c:if>
	
</head>
<body id="body" class="${forpdf == null && !form.wcagCompliance && responsive == null ? 'grey-background' : ''}" style="text-align: center;">
	<div class="page-wrap">
		<c:if test="${forpdf == null && form.wcagCompliance}">
			<div class="skipdiv">
				<a href="#page0">${form.getMessage("label.SkipToMain")}</a>
			</div>		
		</c:if>
		
		<c:set var="mode" value="runner" />
		
		<c:if test="${forpdf == null}">
			<c:choose>
				<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("Official EC Skin")}'>
					<div id="top-page" style="width: 1302px; margin-left: auto; margin-right: auto; border: 1px solid #000">
					<%@ include file="../headerec.jsp" %>	 
				</c:when>
				<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("New Official EC Skin") && responsive != null}'>
					<div id="top-page" style="width: 100%;">
					<%@ include file="../headerecnew.jsp" %>	 
				</c:when>
				<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("New Official EC Skin")}'>
					<div id="top-page" style="width: 1302px; margin-left: auto; margin-right: auto;">
					<%@ include file="../headerecnew.jsp" %>	 
				</c:when>
				<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("ECA Skin")}'>
					<%@ include file="../headerECAnew.jsp" %>
					<div id="top-page" style="width: 1302px; margin-left: auto; margin-right: auto;">
				</c:when>
				<c:when test="${responsive != null}">
					<%@ include file="../headerresponsive.jsp" %>	 
				</c:when>
				<c:otherwise>
					<%@ include file="../header.jsp" %>	 
				</c:otherwise>
			</c:choose>
		</c:if>
		
		<c:choose>
			<c:when test="${forpdf!=null}">
				<%@ include file="runnercontentpdf.jsp" %>	
			</c:when>
			<c:otherwise>
				<%@ include file="runnercontent.jsp" %>			
			</c:otherwise>
		</c:choose>
		
	</div>
	
	<c:if test="${forpdf == null}">
		<c:choose>
			<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("New Official EC Skin")}'>
				</div>  
				<%@ include file="../footerNoLanguagesECnew.jsp" %>
			</c:when>
			<c:when test="${responsive != null}">
				<%@ include file="../footerresponsive.jsp" %>	 
			</c:when>
			<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("Official EC Skin")}'>
				<%@ include file="../footerNoLanguagesEC.jsp" %>
				</div> 
			</c:when>			
			<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("ECA Skin")}'>
				</div>  
				<%@ include file="../footerNoLanguagesECAnew.jsp" %>
			</c:when>
			<c:otherwise>
				<%@ include file="../footerNoLanguages.jsp" %> 
			</c:otherwise>
		</c:choose>
		
		<%@ include file="../generic-messages.jsp" %>
		
		<script type="text/javascript">
		
			$(document).ready(function(){
				$("#language-selector option").each(function(){
					if($(this).val() == $("#newlang").val())
					{
						$(this).attr("selected", "selected");
					}
					else
					{
						$(this).removeAttr("selected");	
					}
				});
			});
		
		</script>
		
		<c:if test="${message != null}">
			<script type="text/javascript">
				showError('<esapi:encodeForHTML>${message}</esapi:encodeForHTML>');
			</script>
		</c:if>
	
	</c:if>
		
	<div class="modal" id="sessiontimeoutdialog" role="dialog">
		<div class="modal-dialog" role="document">
		    <div class="modal-content">
			    <div class="modal-header">
			    	<spring:message code="label.SessionTimeout" />
			    </div>
				<div class="modal-body">	
					<spring:message code="info.SessionTimeout" />
				</div>
				<div class="modal-footer">
					<a onclick="$('#sessiontimeoutdialog').modal('hide')" target="_blank" class="btn btn-primary"><spring:message code="label.OK" /></a>
				</div>
			</div>
		</div>
	</div>
	
	<div class="modal" id="networkproblemsdialog" role="dialog">
		<div class="modal-dialog" role="document">
		    <div class="modal-content">
			    <div class="modal-header">
			    	<spring:message code="label.NetworkProblems" />
			    </div>
				<div class="modal-body">	
					<spring:message code="info.NetworkProblems" />
				</div>
				<div class="modal-footer">
					<a onclick="$('#networkproblemsdialog').modal('hide')" target="_blank" class="btn btn-primary"><spring:message code="label.OK" /></a>
				</div>
			</div>
		</div>
	</div>
	
</body>
</html>
