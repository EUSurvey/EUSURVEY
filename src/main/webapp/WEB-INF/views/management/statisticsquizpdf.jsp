<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Statistics" /></title>
	
	<%@ include file="../includesrunner.jsp" %>
	
	<link href="${contextpath}/resources/css/management.css" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/charts.css" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/runner.css" rel="stylesheet" type="text/css" />
	
	   <style type="text/css">	
	      body
	      {
	        font-family: FreeSans;
	      }
	  	.statelement {
	  		page-break-inside: avoid;
	  	}
	  	
	  	   @page {
			  @bottom-right {
			    content: counter(page);
			  }
			}  
			
		td {
			word-break: break-all;
 			word-wrap: break-word;
		}  
		
		@media print{       
            .progress{
                background-color: #F5F5F5 !important;
                -ms-filter: "progid:DXImageTransform.Microsoft.gradient(GradientType=0,startColorstr='#F5F5F5', endColorstr='#F5F5F5')" !important;
            }
            .progress-bar{
                display: block !important;
                background-color: #337ab7 !important;
                -ms-filter: "progid:DXImageTransform.Microsoft.gradient(GradientType=0,startColorstr='#5BC0DE', endColorstr='#5BC0DE')" !important;
            }

            .progress, .progress > .progress-bar {
                display: block !important;
                -webkit-print-color-adjust: exact !important;

                box-shadow: inset 0 0 !important;
                -webkit-box-shadow: inset 0 0 !important;
            }   
        }
	  </style>
	
</head>
	<body>
		<h1><spring:message code="label.TotalScores" />: ${form.survey.title}</h1>
		<%@ include file="results-statistics-quiz.jsp" %>		
	</body>
</html>
