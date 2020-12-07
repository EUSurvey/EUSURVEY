<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Survey" /> </title>
	
	<meta name="subject" content="<esapi:encodeForHTMLAttribute>${uniqueCode}</esapi:encodeForHTMLAttribute>" />
	<meta name="keywords" content="<esapi:encodeForHTMLAttribute>${form.survey.id}, ${form.survey.shortname}</esapi:encodeForHTMLAttribute>" />
	
	<c:choose>
 		<c:when test="${forpdf != null}">	
			<%@ include file="../includesrunner.jsp" %>
		</c:when>
		<c:otherwise>
			<%@ include file="../includes.jsp" %>
		</c:otherwise>
	</c:choose>
	
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
	<script type="text/javascript" src="${contextpath}/resources/js/Chart.min.js?version=<%@include file="../version.txt" %>"></script>
	
	<c:if test="${forpdf==null}">
	<script type="text/javascript" src="${contextpath}/resources/js/runner2.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/runnerviewmodels.js?version=<%@include file="../version.txt" %>"></script>
    <script type='text/javascript' src='${contextpath}/resources/js/knockout-3.5.1.js?version=<%@include file="../version.txt" %>'></script>
   	</c:if>	
	
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
 	
 	<c:if test="${runnermode != null && forpdf==null}">
		<script type="text/javascript">
			$(function() {
				 $(".headerlink, .header a").each(function(){
					 if ($(this).attr("href") && $(this).attr("href").indexOf("?") == -1)
					$(this).attr("href", $(this).attr("href") + "/runner");
				 });
				 
				 <c:if test="${forpdf == null}">
				 
				 var ids = "";
					$(".emptyelement").each(function(){
						ids += $(this).attr("data-id") + '-';
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
							readCookies();
							$("#btnSubmit").removeClass("hidden");
							$("#btnSaveDraft").removeClass("hidden");
							$("#btnSaveDraftMobile").removeClass("hidden");
						},
						error: function( result ) {	
							alert(result);
						}
					});
				</c:if>
			});
		</script>
	</c:if>
	
	<script type="text/javascript">
	//<![CDATA[
	function updateFileList(element, responseJSON) {
		$(element).siblings(".uploaded-files").first().empty();
		
		$(element).siblings(".validation-error").remove();
		
		for (var i = 0; i < responseJSON.files.length; i++) {
			var f = responseJSON.files[i];
			var div = document.createElement("div");
			
			var del = document.createElement("a");
			$(del).attr("href", "#").attr(
					"onclick",
					"deleteFile('" + $(element).attr('data-id') + "','"
							+ $("#uniqueCode").val() + "','" + f + "', this);return false;");
			
			var ic = document.createElement("i");
			$(ic).addClass("glyphicon glyphicon-trash").css("margin-right",
					"10px");
			$(del).append(ic);		
			$(div).html(f);		
			$(div).prepend(del);
			
			$(element).siblings(".uploaded-files").first().append(div);
		}		
	}
	//]]>
	</script>
	
	<c:if test="${forpdf != null}">
    <style type="text/css">	
    
     table tr {
      	page-break-inside: avoid;
      }    
    
     body
      {
        font-family: FreeSans;
      }
      
      pre {
      	background-color: #fff;
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
	
	.elem_basic {
		page: basic;
		width: 18cm;
		max-width: 18cm;
		height:auto;
		padding-left: 0;
		padding-right: 0;
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
          width: ${form.varPagesEltWidths.get(i)}pt;
        }
        
        .table_${form.varPagesEltIds.get(i)} {
          width: ${form.varPagesEltWidths.get(i) - 150}pt !important;
          max-width: ${form.varPagesEltWidths.get(i) - 150}pt !important;
        }
        
   
	    </c:forEach>
	  </c:if>
	  
	  	  
	  .answer-columns {
	  	max-width: 18cm;
	  }
      
    </style>
  </c:if>
	
</head>
<body class="${forpdf == null ? 'grey-background' : ''}" style="text-align: center;">
	<div class="page-wrap">
		<c:set var="mode" value="editcontribution" />
		
		<c:if test="${forpdf == null && form.wcagCompliance}">
			<div style="margin-top: 40px;">
				<a style="color: #000" href="#page0">${form.getMessage("label.SkipToMain")}</a>
			</div>		
		</c:if>
		
		<c:if test="${forpdf == null}">
			<c:choose>
				<c:when test="${responsive != null}">
					<%@ include file="../headerresponsive.jsp" %>	 
				</c:when>
				<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("Official EC Skin")}'>
					<div id="top-page" style="width: 1302px; margin-left: auto; margin-right: auto; border: 1px solid #000">
					<%@ include file="../headerec.jsp" %>	 
				</c:when>
				<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("New Official EC Skin")}'>
					<div id="top-page" style="width: 1302px; margin-left: auto; margin-right: auto;">
					<%@ include file="../headerecnew.jsp" %>	 
				</c:when>
				<c:when test="${USER != null && runnermode == null}">
					<%@ include file="../header.jsp" %>
					<%@ include file="../menu.jsp" %>	
				</c:when>
				<c:otherwise>
					<%@ include file="../header.jsp" %>	 
				</c:otherwise>
			</c:choose>
		</c:if>
		
		 
			<c:if test="${USER != null && runnermode == null}">							
				<%@ include file="../management/formmenu.jsp" %>
			</c:if>			
			
			<c:if test="${forpdf != null}">
				<c:if test='${!form.survey.security.equals("openanonymous") && !form.survey.security.equals("securedanonymous")}'>
					<div style="color:#666; text-align: left;">
						<c:choose>
							<c:when test="${draftid != null}">
								<spring:message code="label.DraftID" />: <esapi:encodeForHTML>${draftid}</esapi:encodeForHTML><br/>
							</c:when>
							<c:otherwise>
								<spring:message code="label.ContributionId" />: <esapi:encodeForHTML>${uniqueCode}</esapi:encodeForHTML><br/>	
							</c:otherwise>
						</c:choose>
						<spring:message code="label.Date" />: <esapi:encodeForHTML>${submittedDate}</esapi:encodeForHTML><br/>
						<hr/>
					</div>
				</c:if>
			</c:if>
			
			<c:choose>
			<c:when test="${forpdf!=null}">
				<%@ include file="../runner/runnercontentpdf.jsp" %>	
			</c:when>
			<c:otherwise>
				<%@ include file="../runner/runnercontent.jsp" %>			
			</c:otherwise>
		</c:choose>
			
			<c:if test="${forpdf != null}">
				<div style="margin-top: 20px; text-align: left">
			
				<c:if test="${form.survey.getUsefulLinks().size() != 0}">					
					<div class="linkstitle"><spring:message code="label.UsefulLinks" /></div>						
					<c:forEach var="link" items="${form.survey.getAdvancedUsefulLinks()}">
						<div style="margin-top: 5px;" ><a target="_blank" rel="noopener noreferrer" style="color: #000;" href="<esapi:encodeForHTMLAttribute>${link.value}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML>${link.key} (${link.value})</esapi:encodeForHTML></a></div>
					</c:forEach>							
					<hr style="margin-top: 15px;" />	
				</c:if>
						
				<c:if test="${form.survey.getBackgroundDocuments().size() != 0}">
					<div class="linkstitle"><spring:message code="label.BackgroundDocuments" /></div>						
					<c:forEach var="link" items="${form.survey.getBackgroundDocumentsAlphabetical()}">
						<div style="margin-top: 5px;" ><a target="_blank" style="color: #000;" href="<esapi:encodeForHTMLAttribute>${link.value}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML>${link.key}</esapi:encodeForHTML></a></div>
					</c:forEach>							
					<hr style="margin-top: 15px;" />
				</c:if>				
				
				<c:if test="${form.survey.contact != null}">
					<div class="linkstitle" style="margin-bottom: 5px;"><spring:message code="label.Contact" /></div>
					
					<c:choose>
						<c:when test="${form.survey.contact.startsWith('form:')}">
							<a class="link visibleLink" target="_blank" data-toggle="tooltip" title="<spring:message code="info.ContactForm" />" href="${contextpath}/runner/contactform/${form.survey.shortname}"><spring:message code="label.ContactForm" /></a>
						</c:when>
						<c:when test="${form.survey.contact.contains('@')}">
							<i class="icon icon-envelope" style="vertical-align: middle"></i>
							<esapi:encodeForHTML>${form.survey.contact}</esapi:encodeForHTML>
						</c:when>
						<c:otherwise>
							<i class="icon icon-globe" style="vertical-align: middle"></i>
							<esapi:encodeForHTML>${form.survey.contact}</esapi:encodeForHTML>
						</c:otherwise>
					</c:choose>
					<hr style="margin-top: 15px;" />
				</c:if>
			</div>			
		</c:if>
	</div>
	
	<c:if test="${forpdf == null }">
		<c:choose>
			<c:when test="${responsive != null}">
				<%@ include file="../footerresponsive.jsp" %>	 
			</c:when>
			<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("Official EC Skin")}'>
				<%@ include file="../footerNoLanguagesEC.jsp" %>
				</div> 
			</c:when>
			<c:when test='${form.survey.skin != null && form.survey.skin.name.equals("New Official EC Skin")}'>
				</div>  
				<%@ include file="../footerNoLanguagesECnew.jsp" %>
			</c:when>
			<c:otherwise>
				<%@ include file="../footerNoLanguages.jsp" %> 
			</c:otherwise>
		</c:choose>
		
		<c:if test="${message != null}">
			<script type="text/javascript">
				showError('<esapi:encodeForHTML>${message}</esapi:encodeForHTML>');
			</script>
		</c:if>
	</c:if>
	
</body>
</html>
