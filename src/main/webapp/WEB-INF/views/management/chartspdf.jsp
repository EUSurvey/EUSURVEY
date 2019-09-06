<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Charts" /></title>
	
	<%@ include file="../includes.jsp" %>
	
	<link href="${contextpath}/resources/css/management.css" rel="stylesheet" type="text/css" />
	<link href="${contextpath}/resources/css/charts.css" rel="stylesheet" type="text/css" />
	
	<style type="text/css">	
	  	.chartelement {
	  		page-break-inside: avoid;
	  		margin-bottom: 30px;
	  	}
	  	
	  	.charttable {
	  		width: 60%;
	  		margin-left: auto;
	  		margin-right: auto;
	  		margin-top: 10px;
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
	  </style>
	
</head>
	<body>
		<h1><spring:message code="label.Charts" />: ${form.survey.title}</h1>
		
		<div id="results-charts" style="margin-top: 70px">	
		
			<c:forEach items="${form.getSurvey().getQuestionsAndSections()}" var="question">
				<c:if test="${publication == null || publication.isAllQuestions() || publication.isSelected(question.id)}">
					<c:if test="${question.getType() == 'Section'}">
						<div class="sectiontitle section${question.level}">${form.getSectionTitle(question)}</div>
					</c:if>
					<c:if test="${question.getType() == 'MultipleChoiceQuestion' || question.getType() == 'SingleChoiceQuestion' }">
						<div class="chartelement">
							<b>${question.title} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${question.shortname})</span></b>						
							<table class="charttable">
								<c:forEach items="${question.allPossibleAnswers}" var="answer" varStatus="status">
									<tr>
										<td>
											${answer.title}  <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${answer.shortname})</span>
										</td>
										<td style="width: 210px">
											<div class="progress" style="width: 200px; margin-bottom: 2px;">
											  <div class="bar" style="width: ${statistics.requestedRecordsPercent[answer.id.toString()]}%;"></div>
											</div>
										</td>
									</tr>	
								</c:forEach>					
							</table>
						</div>						
					</c:if>
					<c:if test="${question.getType() == 'GalleryQuestion' && question.selection}">
						<div class="chartelement">
							<b>${question.title} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${question.shortname})</span></b>						
							<table class="charttable">
								<c:forEach items="${question.files}" var="file" varStatus="status">
									<tr>
										<td>
											${file.name}
										</td>
										<td style="width: 210px">
											<div class="progress" style="width: 200px; margin-bottom: 2px;">
											  <div class="bar" style="width: ${statistics.getRequestedRecordsPercentForGallery(question, status.index)}%;"></div>
											</div>
										</td>
									</tr>	
								</c:forEach>					
							</table>
						</div>						
					</c:if>
					<c:if test="${question.getType() == 'Matrix'}">					
						<c:forEach items="${question.questions}" var="matrixQuestion">
							<div class="chartelement">
								<b>${question.getStrippedTitle()} : ${matrixQuestion.title}<span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(matrixQuestion.shortname})</span></b>
								<table class="charttable">
									<c:forEach items="${question.answers}" var="possibleanswer" varStatus="status">
							 		 	<tr>
											<td>${possibleanswer.title} <span class="assignedValue ${showShortnames == null ? 'hideme' : ''}">(${possibleanswer.shortname})</span></td>
											<td style="width: 210px">
												<div class="progress" style="width: 200px; margin-bottom: 2px;">
												  <div class="bar" style="width: ${statistics.getRequestedRecordsPercentForMatrix(matrixQuestion, possibleanswer)}%;"></div>
												</div>
											</td>	
										</tr>							 		
							 		</c:forEach>
							 	</table>
							 </div>
						</c:forEach>											
					</c:if>
				</c:if>
			</c:forEach>
		
		
		</div>
	</body>
</html>
