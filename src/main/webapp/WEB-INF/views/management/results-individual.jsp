<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<div id="individuals-div">						
	
	<c:choose>
		<c:when test="${publication != null}">
			<div style="position: relative; margin-left: auto; margin-right: auto; text-align: center; margin-bottom: 10px; margin-top: 20px;">
		</c:when>
		<c:otherwise>
			<div style="position: relative; margin-left: auto; margin-right: auto; text-align: center; margin-bottom: 10px; margin-top: 10px;">
		</c:otherwise>
	</c:choose>		

		<div style="position: relative; margin-left: auto; margin-right: auto; text-align: center; width:350px; margin-bottom: 10px; margin-top: 10px;">
			<button data-toggle="tooltip" type="button" title="<spring:message code="label.GoToPreviousPage"/>" onclick="individualsMoveTo('previous', this);" class="widget-move-previous unstyledbuttonBlack" disabled><span class="glyphicon glyphicon-chevron-left"></span></button>
			<span class="widget-first firstResultIndividual" style="margin-left: 10px; margin-right: 10px;">1</span>	
			<button data-toggle="tooltip" type="button" title="<spring:message code="label.GoToNextPage"/>" onclick="individualsMoveTo('next', this);" class="widget-move-next unstyledbuttonBlack" ><span class="glyphicon glyphicon-chevron-right"></span></button>
			<img class="add-wait-animation-individual hideme" style="position: absolute; right: 0px; top: 0px;" src="${contextpath}/resources/images/ajax-loader.gif" />
		</div>
		
		<table id="individuals-table" class="table table-striped table-bordered" style="width: auto; margin-left: auto; margin-right: auto;">
			<c:forEach items="${form.survey.getQuestions()}" var="question">
				<c:choose>
					<c:when test="${question.getType() == 'Image' || question.getType() == 'Text' || question.getType() == 'Download' || question.getType() == 'Confirmation' || question.getType() == 'Ruler' }"></c:when>
					<c:when test="${question.getType() == 'GalleryQuestion' && !question.selection}"></c:when>
					<c:when test="${question.getType() == 'Upload' && publication != null && !publication.getShowUploadedDocuments()}"></c:when>
				
					<c:otherwise>
			
						<c:if test="${publication == null || publication.isAllQuestions() || publication.isSelected(question.id)}">
						
							<tr>
								<td>${question.getStrippedTitle()}</td>
									<c:choose>
										<c:when test="${question.getType() == 'Matrix'}">
											<td>
												<table class="table table-bordered">						
													<c:forEach items="${question.questions}" var="matrixQuestion">
														<tr>
															<td>${matrixQuestion.getStrippedTitle()}</td>
															<td class="questioncell" data-id="${matrixQuestion.id}" data-uid="${matrixQuestion.uniqueId}">
																<!-- Set through JS; publication.jsp - individualsMoveTo(...) -->
															</td>
														</tr>
													</c:forEach>							
												</table>
											</td>
										</c:when>						
										<c:when test="${question.getType() == 'Table'}">
											<td>
												<table class="table table-bordered">						
													<c:forEach var="r" begin="1" end="${question.rows}"> 												
													<tr>							
														<c:forEach var="c" begin="1" end="${question.columns}"> 	
															<c:choose>
																<c:when test="${r == 1}">
																	<td>${question.childElements[c-1].getStrippedTitle()}</td>
																</c:when>
																<c:when test="${c == 1}">
																	<td>${question.childElements[question.columns + r - 2].getStrippedTitle()}</td>
																</c:when>
																<c:otherwise>
																	<td class="tablequestioncell" data-id="${question.id}" data-uid="${question.uniqueId}" data-row="${r-1}" data-column="${c-1}">
																		<!-- Set through JS; publication.jsp - individualsMoveTo(...) -->
																	</td>
																</c:otherwise>
															</c:choose>
														</c:forEach>
													</tr>
													</c:forEach>
												</table>		
											</td>				
										</c:when>
										
										<c:when test="${question.getType() == 'RatingQuestion'}">
											<td>
												<table class="table table-bordered">						
													<c:forEach items="${question.questions}" var="childQuestion">	
														<tr>
															<td>${childQuestion.getStrippedTitle()}</td>
															<td class="questioncell" data-id="${childQuestion.id}" data-uid="${childQuestion.uniqueId}">
																<!-- Set through JS; publication.jsp - individualsMoveTo(...) -->
															</td>
														</tr>
													</c:forEach>							
												</table>
											</td>
										</c:when>
										
										<c:when test="${question.getType() == 'ComplexTable'}">
											<td>
												<table class="table table-bordered">						
													<c:forEach items="${question.getQuestionChildElements()}" var="child">
														<tr>
															<td>${child.getResultTitle(question)}</td>
															<td class="questioncell" data-id="${child.id}" data-uid="${child.uniqueId}">
																<!-- Set through JS; publication.jsp - individualsMoveTo(...) -->
															</td>
														</tr>
													</c:forEach>							
												</table>
											</td>
										</c:when>	
										
										<c:otherwise>
											<td class="questioncell" data-id="${question.id}" data-uid="${question.uniqueId}">
												<!-- Set through JS; publication.jsp - individualsMoveTo(...) -->
											</td>
										</c:otherwise>
									</c:choose>								
								</td>
							</tr>
						</c:if>
					</c:otherwise>
				</c:choose>		
			</c:forEach>
		</table>
		
		<div style="position: relative; margin-left: auto; margin-right: auto; text-align: center; width:350px; margin-bottom: 10px; margin-top: 10px;">
			<button data-toggle="tooltip" type="button" title="<spring:message code="label.GoToPreviousPage"/>" onclick="individualsMoveTo('previous', this);" class="widget-move-previous unstyledbuttonBlack" disabled><span class="glyphicon glyphicon-chevron-left"></span></button>
			<span class="widget-first firstResultIndividual" style="margin-left: 10px; margin-right: 10px;">1</span>
			<button data-toggle="tooltip" type="button" title="<spring:message code="label.GoToNextPage"/>" onclick="individualsMoveTo('next', this);" class="widget-move-next unstyledbuttonBlack" ><span class="glyphicon glyphicon-chevron-right"></span></button>
			<img class="add-wait-animation-individual hideme" style="position: absolute; right: 0px; top: 0px;" src="${contextpath}/resources/images/ajax-loader.gif" />
		</div>
	</div>	

</div>
