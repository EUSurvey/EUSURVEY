<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:choose>
	<c:when test="${forpdf != null}">
		<div id="results-statistics-delphi" style="margin-top: 70px">	
	</c:when>
	<c:when test="${publication != null}">
		<div id="results-statistics-delphi" class="hidden" style="margin-top: 20px">	
	</c:when>
	<c:otherwise>
		<div id="results-statistics-delphi" class="hidden" style="margin-top: 10px">
	</c:otherwise>
</c:choose>		
	<div id="scrollareastatisticsquiz" class="scrollarea"  style="height: auto">
		<div style="width: 700px; margin-left: auto; margin-right: auto">
		
			<table class="table table-bordered table-striped">
				<thead>
					<tr>
						<th><spring:message code="label.Element" /></td>
						<th><spring:message code="label.CompletionRate" /></td>
						<th><spring:message code="label.Median" /></td>
					</tr>
				</thead>
				<tbody>
					<tr style="background-color: #eee">
						<td style="font-weight: bold"><spring:message code="label.Survey" /></td>
						<td class="statDelphi" data-uid="0"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
						<td></td>
					</tr>
					
					<c:forEach items="${form.getSurvey().getQuestionsAndSections()}" var="element" varStatus="loop">
						<c:if test="${publication == null || publication.isAllQuestions() || publication.isSelected(element.id)}">
						    <c:if test="${filter == null || filter.visibleQuestions.contains(element.id.toString())}">
                                <c:choose>
                                    <c:when test="${element.getType() == 'Section'}">
                                        <tr>
                                            <td style="font-weight: bold">${element.getStrippedTitleNoEscape()}</td>
                                            <td class="statDelphi" data-uid="${element.uniqueId}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
                                            <td></td>
                                        </tr>
                                    </c:when>
                                    <c:when test="${element.isDelphiElement()}">
                                        <tr>
                                            <td style="padding-left: 20px;">${element.getStrippedTitleNoEscape()}</td>
                                            <td class="statDelphi" data-uid="${element.uniqueId}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
                                            <td class="statDelphiMedian" data-uid="${element.uniqueId}"><img class="ajaxloaderimage" src="${contextpath}/resources/images/ajax-loader.gif" /></td>
                                        </tr>
                                    </c:when>
                                </c:choose>
                            </c:if>
						</c:if>
					</c:forEach>
				</tbody>
			</table>
						
			</div>
			
		</div>
	</div>