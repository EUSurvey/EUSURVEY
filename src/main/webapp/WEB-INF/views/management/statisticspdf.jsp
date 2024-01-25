<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Statistics" /></title>
	
	<%@ include file="../includesrunner.jsp" %>
	
	<link href="${contextpath}/resources/css/management.css" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/charts.css" rel="stylesheet" type="text/css" />
	
	   <style type="text/css">	
	      body
	      {
	        font-family: FreeSans;
	      }
	  	.statelement {
	  		page-break-inside: avoid;
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
			
		td {
			word-break: break-all;
 			word-wrap: break-word;
 			padding-right: 10px;
		} 
		
		th {
			text-align: left;
			padding-right: 10px;
		} 
		
		.statistics-table {
			max-width: 95% !important;
		}		
		     
        .progress{
         height: 20px;
        }
        .progress-bar{
         height: 20px;
            display: block !important;
            background-color: #337ab7 !important;
            -ms-filter: "progid:DXImageTransform.Microsoft.gradient(GradientType=0,startColorstr='#5BC0DE', endColorstr='#5BC0DE')" !important;
        }

        .progress, .progress > .progress-bar {
            display: block !important;
            -webkit-print-color-adjust: exact !important;

            box-shadow: inset 0 0 !important;
            -webkit-box-shadow: inset 0 0 !important;
            
            height: 20px;
            
            border-radius: 5px;
        }
        
        .statistics-table th {
        	background-color: #ddd;
        }
        
        .statistics-table th, .statistics-table td  {
        	border: 1px solid #999;
        	vertical-align: middle;
        	padding: 5px;
        	font-size: 90%;
        }
        
	  </style>
	
</head>
	<body>
		<h1><spring:message code="label.Statistics" />: ${form.survey.title}</h1>
		<%@ include file="results-statistics.jsp" %>		
	</body>
</html>
