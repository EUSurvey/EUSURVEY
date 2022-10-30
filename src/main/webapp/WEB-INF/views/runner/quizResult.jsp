<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:og="http://ogp.me/ns#" xml:lang="${form.language.code}" lang="${form.language.code}">
<head>
	<title>EUSurvey - <spring:message code="label.Survey" /></title>
	
	<%@ include file="../includesrunner.jsp" %>
	
	<link id="runnerCss" href="${contextpath}/resources/css/quiz.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	<link id="runnerCss" href="${contextpath}/resources/css/runner.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${contextpath}/resources/js/runner.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/Chart.min.js?version=<%@include file="../version.txt" %>"></script>
	
	<style type="text/css">	
		<c:if test="${forpdf != null}">    
			 body
		      {
		        font-family: FreeSans;
		      }
		
            .progress{
            	height: 22px;
            
                background-color: #C0C0C0 !important;
                -ms-filter: "progid:DXImageTransform.Microsoft.gradient(GradientType=0,startColorstr='#F5F5F5', endColorstr='#F5F5F5')" !important;
            }
            .progress-bar{
                display: block !important;
                background-color: #337ab7 !important;
                -ms-filter: "progid:DXImageTransform.Microsoft.gradient(GradientType=0,startColorstr='#5BC0DE', endColorstr='#5BC0DE')" !important;
            }

            .progress, .progress > .progress-bar {
           		border-radius: 4px;
           		height: 22px;
                display: block !important;
                -webkit-print-color-adjust: exact !important;

                box-shadow: inset 0 0 !important;
                -webkit-box-shadow: inset 0 0 !important;
            }   
            
            tr, .quizanswer, .quizquestion, img, .quizsectionresults {
            	page-break-inside: avoid;
            }
        </c:if>
	</style>
	
	<script type="text/javascript"> 
		$(function() {
			clearLocalBackupForPrefix('${surveyprefix}');
		});
	</script>
		
</head>
<body>
	<div class="page-wrap">
		<c:if test="${forpdf == null}">
			<%@ include file="../header.jsp" %>	
		</c:if>
		
		<%@ include file="../runner/quizResultInner.jsp" %>	
	</div>

	<c:if test="${forpdf == null}">
		<%@ include file="../footerNoLanguages.jsp" %>		
		<%@ include file="../generic-messages.jsp" %>
		
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
	
	</c:if>

</body>
</html>
