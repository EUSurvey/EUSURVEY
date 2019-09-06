<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<c:choose>
	<c:when test="${publication != null}">
		<div id="results-table" style="margin-top: 60px; min-height: 400px;">		
	</c:when>
	<c:otherwise>
		<div id="results-table" style="margin-top: 200px;">		
	</c:otherwise>
</c:choose>		
	<div id="ResultFilterLimit" style="color: #777; text-align: center;">
		<span class="glyphicon glyphicon-info-sign"></span>
		<spring:message code="info.ResultFilterLimit" />
	</div>

	<div id="scrollareaheader">				
		<table id="contentstable" class="table table-bordered table-striped table-styled">
			<c:set var="count" value="0" scope="page" />
			<thead style="background-position: initial initial; background-repeat: initial initial;">
				<tr>
					<c:if test="${publication == null}">
						<th class="checkDelete"><input name="check-all-delete" id="check-all-delete" class="check" type="checkbox" onclick="checkAllDelete()" /></th>
						<th class="topaligned" style="width: 150px"><div style="width: 133px"><spring:message code="label.Actions" /></div></th>
					</c:if>
					<c:forEach items="${form.getSurvey().getQuestions()}" var="question">
						<c:if test="${publication == null || publication.isAllQuestions() || publication.isSelected(question.id)}">
							<c:if test="${filter == null || filter.visibleQuestions.contains(question.id.toString())}">
								<c:choose>
									<c:when test="${question.getType() == 'Image' || question.getType() == 'Text' || question.getType() == 'Download' || question.getType() == 'Confirmation'}"></c:when>
									<c:when test="${question.getType() == 'GalleryQuestion' && !question.selection}"></c:when>
									<c:when test="${question.getType() == 'Matrix'}">
										<c:forEach items="${question.getQuestions()}" var="matrixquestion">
											<th class="topaligned cell${matrixquestion.id}"><div class="headertitle">${question.getStrippedTitle()}&nbsp;:&nbsp;${matrixquestion.getStrippedTitle()}&nbsp;<span class="assignedValue hideme">(${matrixquestion.shortname})</span></div></th>
											<c:set var="count" value="${count + 1}" scope="page"/>
										</c:forEach>
									</c:when>
									<c:when test="${question.getType() == 'Table'}">
										<c:forEach items="${question.getQuestions()}" var="tablequestion">
											<c:forEach items="${question.getAnswers()}" var="tableanswer">
												<th class="topaligned cell${tablequestion.id}-${tableanswer.id}"><div class="headertitle">${question.getStrippedTitle()}&nbsp;${tablequestion.getStrippedTitle()} <span class="assignedValue hideme">(${tablequestion.shortname})</span> : ${tableanswer.getStrippedTitle()}<span class="assignedValue hideme">(${tableanswer.shortname})</span> </div></th>
												<c:set var="count" value="${count + 1}" scope="page"/>
											</c:forEach>
										</c:forEach>
									</c:when>
									<c:when test="${question.getType() == 'RatingQuestion'}">
										<c:forEach items="${question.getQuestions()}" var="childquestion">
											<th class="topaligned cell${childquestion.id}"><div class="headertitle">${question.getStrippedTitle()}&nbsp;:&nbsp;${childquestion.getStrippedTitle()}&nbsp;<span class="assignedValue hideme">(${childquestion.shortname})</span></div></th>
											<c:set var="count" value="${count + 1}" scope="page"/>
										</c:forEach>
									</c:when>
									<c:when test="${question.getType() == 'Upload'}">
										<c:if test="${publication == null || publication.getShowUploadedDocuments()}">
											<th class="topaligned cell${question.id}"><div class="headertitle">${question.getStrippedTitle()}  <span class="assignedValue hideme">(${question.shortname})</span></div></th>
											<c:set var="count" value="${count + 1}" scope="page"/>
										</c:if>
									</c:when>
									<c:otherwise>
										<th class="topaligned cell${question.id}"><div class="headertitle">${question.getStrippedTitle()}  <span class="assignedValue hideme">(${question.shortname})</span></div></th>
										<c:set var="count" value="${count + 1}" scope="page"/>
									</c:otherwise>
								</c:choose>
							</c:if>
						</c:if>
					</c:forEach>
					<c:if test="${publication == null}">
						<c:if test='${filter.visible("invitation") == true}'><th class="topaligned cellinvitation"><div class="headertitle"><spring:message code="label.InvitationNumber" /></div></th></c:if>
						<c:if test='${filter.visible("case") == true}'><th class="topaligned cellcase"><div class="headertitle"><spring:message code="label.ContributionId" /></div></th></c:if>
						<c:if test='${filter.visible("user") == true}'><th class="topaligned celluser"><div class="headertitle"><spring:message code="label.UserName" /></div></th></c:if>
						<c:if test='${filter.visible("created") == true}'><th class="topaligned cellcreated"><div class="headertitle"><spring:message code="label.CreationDate" /></div></th></c:if>
						<c:if test='${filter.visible("updated") == true}'><th class="topaligned cellupdated"><div class="headertitle"><spring:message code="label.LastUpdate" /></div></th></c:if>
						<c:if test='${filter.visible("languages") == true}'><th class="topaligned celllanguages"><div class="headertitle"><spring:message code="label.Languages" /></div></th></c:if>
						<c:set var="count" value="${count + 7}" scope="page"/>
					</c:if>
					<c:if test="${form.getSurvey().isQuiz}">
						<th class="topaligned cell${question.id}">
							<div class="headertitle">
								<div style="float: right">
									<a data-toggle="tooltip" title="<spring:message code="label.SortDescending" />" onclick="$('#sort').val('scoreDesc');$('#resultsForm').submit();"><span class="glyphicon glyphicon-arrow-down"></span></a>
									<a data-toggle="tooltip" title="<spring:message code="label.SortAscending" />" onclick="$('#sort').val('scoreAsc');$('#resultsForm').submit();"><span class="glyphicon glyphicon-arrow-up"></span></a>
								</div>
								<spring:message code="label.TotalScore" />
							</div>
						</th>
					</c:if>
				</tr>
				<c:if test="${publication == null || publication.isShowSearch()}">
					<tr class="table-styled-filter">
						<c:if test="${publication == null}">
							<th class="checkDelete">&nbsp;</th>
							<th style="width: 150px; text-align: right">&nbsp;</th>
						</c:if>
						<c:forEach items="${form.getSurvey().getQuestions()}" var="question">
							<c:if test="${publication == null || publication.isAllQuestions() || publication.isSelected(question.id)}">
								<c:if test="${filter == null || filter.visibleQuestions.contains(question.id.toString())}">
									<c:choose>
										<c:when test="${question.getType() == 'Image' || question.getType() == 'Text' || question.getType() == 'Download' || question.getType() == 'Confirmation'}"></c:when>
										<c:when test="${question.getType() == 'GalleryQuestion' && !question.selection}"></c:when>
										<c:when test="${question.getType() == 'Matrix'}">
											<c:forEach items="${question.questions}" var="matrixQuestion">
												<th class="filtercell cell${matrixQuestion.id}"<c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>>
													<div>
														<a class="btn btn-default" onclick="showOverlayMenu(this)" >
														    <span class="nobreak"><spring:message code="label.AllValues" /></span>
														    <span class="caret"></span>
														  </a>
														  
														  <div class="overlaymenu hideme maxH">
														  	<a style="margin-bottom: 5px;"   onclick="$('#resultsForm').submit();" class="btn btn-default btn-sm btn-info"><spring:message code="label.ApplyFilter" /></a>
														  	 <c:forEach items="${question.answers}" var="possibleanswer" varStatus="status">
																<div>
																	<c:choose>
																		<c:when test="${filter.contains(matrixQuestion.id, matrixQuestion.uniqueId, possibleanswer.id, possibleanswer.uniqueId) }">
																			<input checked="checked" name="filter${matrixQuestion.id}|${matrixQuestion.uniqueId}" data-stopPropagation="true" type="checkbox" class="check checkFilterCell" value="${possibleanswer.id}|${possibleanswer.uniqueId}" />${possibleanswer.title}
																		</c:when>
																		<c:otherwise>
																			<input name="filter${matrixQuestion.id}|${matrixQuestion.uniqueId}" data-stopPropagation="true" type="checkbox" class="check checkFilterCell" value="${possibleanswer.id}|${possibleanswer.uniqueId}" />${possibleanswer.title}
																		</c:otherwise>
																	</c:choose>
																</div>
															</c:forEach>	
														  </div>
													</div>
												</th>
											</c:forEach>
										</c:when>
										<c:when test="${question.getType() == 'Table'}">									
											<c:forEach var="r" begin="1" end="${question.allRows-1}"> 
												<c:forEach var="c" begin="1" end="${question.allColumns-1}"> 																
													<th class="filtercell cell${question.id}-${r}-${c}"<c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>>
														<input onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${filter.getValue(question.id.toString().concat("-").concat(r.toString()).concat("-").concat(c.toString()), question.uniqueId)}</esapi:encodeForHTMLAttribute>' type="text"  style="margin:0px;" name='filter${question.id}-${r}-${c}|${question.uniqueId}' />
														<a data-toggle="tooltip" data-placement="top" title="<spring:message code="info.SearchWholeWordOnly" />"><span class="glyphicon glyphicon-question-sign black"></span></a>
													</th>										
												</c:forEach>													
											</c:forEach>
										</c:when>
										<c:when test="${question.getType() == 'RatingQuestion'}">
											<c:forEach items="${question.questions}" var="childQuestion">
												<th class="filtercell cell${childQuestion.id}"<c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>>
													<div>
														<a class="btn btn-default" onclick="showOverlayMenu(this)" >
														    <span class="nobreak"><spring:message code="label.AllValues" /></span>
														    <span class="caret"></span>
														  </a>
														  
														  <div class="overlaymenu hideme maxH">
														  	<a style="margin-bottom: 5px;" onclick="$('#resultsForm').submit();" class="btn btn-default btn-sm btn-info"><spring:message code="label.ApplyFilter" /></a>
														  	 <c:forEach begin="1" end="${question.numIcons}" varStatus="loop">
																<div>
																	<c:choose>
																		<c:when test="${filter.contains(childQuestion.id, childQuestion.uniqueId, loop.index, loop.index.toString()) }">
																			<input checked="checked" name="filter${childQuestion.id}|${childQuestion.uniqueId}" data-stopPropagation="true" type="checkbox" class="check checkFilterCell" value="${loop.index}/" />${loop.index}
																		</c:when>
																		<c:otherwise>
																			<input name="filter${childQuestion.id}|${childQuestion.uniqueId}" data-stopPropagation="true" type="checkbox" class="check checkFilterCell" value="${loop.index}/" />${loop.index}
																		</c:otherwise>
																	</c:choose>
																</div>
															</c:forEach>	
														  </div>
													</div>
												</th>
											</c:forEach>
										</c:when>
										<c:when test="${question.getType() == 'Upload' && publication != null && !publication.getShowUploadedDocuments()}">
										
										</c:when>
										<c:otherwise>
											<th class="filtercell cell${question.id}"<c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>>
												<c:choose>
													<c:when test="${question.getType() == 'GalleryQuestion'}">
														<div>
														<a class="btn btn-default" onclick="showOverlayMenu(this)" >
														    <span class="nobreak"><spring:message code="label.AllValues" /></span>
														    <span class="caret"></span>
														  </a>
														  
														  <div class="overlaymenu hideme maxH">
														  	<a style="margin-bottom: 5px;"   onclick="$('#resultsForm').submit();" class="btn btn-default btn-sm btn-info"><spring:message code="label.ApplyFilter" /></a>
														  	 <c:forEach items="${question.files}" var="file" varStatus="status">
														    	<div>
															    	<c:choose>
																		<c:when test="${filter.contains(question.id, question.uniqueId, status.index) }">
																			<input checked="checked" name="filter${question.id}|${question.uniqueId}" data-stopPropagation="true" type="checkbox" class="check checkFilterCell" value="${status.index}"><esapi:encodeForHTML>${file.name}</esapi:encodeForHTML></input>
																		</c:when>
																		<c:otherwise>
																			<input name="filter${question.id}|${question.uniqueId}" data-stopPropagation="true" type="checkbox" class="check checkFilterCell" value="${status.index}"><esapi:encodeForHTML>${file.name}</esapi:encodeForHTML></input>
																		</c:otherwise>
																	</c:choose>
														    	</div>
															</c:forEach>
														  </div>
														</div>
													</c:when>
													<c:when test="${question.getType() == 'Upload'}">
														<c:if test="${(sessioninfo.owner == USER.id || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('AccessResults') > 1 || (form.survey.isDraft && USER.getLocalPrivilegeValue('AccessDraft') > 0) || (publication != null && publication.getShowUploadedDocuments())) && questionswithuploadedfiles != null && questionswithuploadedfiles.contains(question.uniqueId)}">
															<a onclick="showExportDialog('Files${question.uniqueId}${form.survey.isDraft}', 'zip');"  data-toggle="tooltip" data-placement="top" title="<spring:message code="label.DownloadAllFiles" />"><span class="glyphicon glyphicon-save"></span></a>
														</c:if>																		
													</c:when>
													<c:when test="${question.getType() == 'MultipleChoiceQuestion' || question.getType() == 'SingleChoiceQuestion'}">
														<div>
														  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
														    <span class="nobreak"><spring:message code="label.AllValues" /></span>
														    <span class="caret"></span>
														  </a>
														  
														  <div class="overlaymenu hideme maxH">
														  	<a style="margin-bottom: 5px;"   onclick="$('#resultsForm').submit();" class="btn btn-default btn-sm btn-info"><spring:message code="label.ApplyFilter" /></a>
														  	 <c:forEach items="${question.allPossibleAnswers}" var="possibleanswer" varStatus="status">
														    	<div>
															    	<c:choose>
																		<c:when test="${filter.contains(question.id, question.uniqueId, possibleanswer.id, possibleanswer.uniqueId) }">
																			<input checked="checked" name="filter${question.id}|${question.uniqueId}" data-stopPropagation="true" type="checkbox" class="check checkFilterCell" value="${possibleanswer.id}|${possibleanswer.uniqueId}">${possibleanswer.title}</input>
																		</c:when>
																		<c:otherwise>
																			<input name="filter${question.id}|${question.uniqueId}" data-stopPropagation="true" type="checkbox" class="check checkFilterCell" value="${possibleanswer.id}|${possibleanswer.uniqueId}">${possibleanswer.title}</input>
																		</c:otherwise>
																	</c:choose>
														    	</div>
															</c:forEach>
														  </div>
														  
														</div>
													</c:when>
													<c:otherwise>
														<input onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" value='<esapi:encodeForHTMLAttribute>${filter.getValue(question.id, question.uniqueId)}</esapi:encodeForHTMLAttribute>' type="text" maxlength="100"  style="margin:0px;" name="filter${question.id}|${question.uniqueId}" />
														<a  data-toggle="tooltip" data-placement="top" title="<spring:message code="info.SearchWholeWordOnly" />"><span class="glyphicon glyphicon-question-sign black"></span></a>
													</c:otherwise>
												</c:choose>
											</th>
										</c:otherwise>
									</c:choose>		
								</c:if>						
							</c:if>
						</c:forEach>
						<c:if test="${publication == null}">
							<c:if test='${filter.visible("invitation") == true}'>
								<th class="filtercell cellinvitation">
									<c:choose>
										<c:when test='${form.survey.security.equals("openanonymous") || form.survey.security.equals("securedanonymous")}'>
											<input disabled="disabled" type="text" style="margin:0px;" />
										</c:when>
										<c:otherwise>
											<input onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" type="text" maxlength="100" style="margin:0px;" value='<esapi:encodeForHTMLAttribute>${filter.invitation}</esapi:encodeForHTMLAttribute>' name="metafilterinvitation" />
										</c:otherwise>
									</c:choose>
								</th>
							</c:if>
							<c:if test='${filter.visible("case") == true}'>
								<th class="filtercell cellcase">
									<c:choose>
										<c:when test='${form.survey.security.equals("openanonymous") || form.survey.security.equals("securedanonymous")}'>
											<input disabled="disabled" type="text" style="margin:0px;" />
										</c:when>
										<c:otherwise>
											<input onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" type="text" maxlength="100" style="margin:0px;" value='<esapi:encodeForHTMLAttribute>${filter.caseId}</esapi:encodeForHTMLAttribute>' name="metafiltercase" />
										</c:otherwise>
									</c:choose>
								</th>
							</c:if>
							<c:if test='${filter.visible("user") == true}'>
								<th class="filtercell celluser">
									<c:choose>
										<c:when test='${form.survey.security.equals("openanonymous") || form.survey.security.equals("securedanonymous")}'>
											<input disabled="disabled" type="text" style="margin:0px;" />
										</c:when>
										<c:otherwise>
											<input onkeyup="checkFilterCell($(this).closest('.filtercell'), false)" type="text" maxlength="100" style="margin:0px;" value='<esapi:encodeForHTMLAttribute>${filter.user}</esapi:encodeForHTMLAttribute>' name="metafilteruser" />
										</c:otherwise>
									</c:choose>
								</th>
							</c:if>
							<c:if test='${filter.visible("created") == true}'>
								<th class="filtercell cellcreated">
									<div class="btn-toolbar" style="margin: 0px; text-align: center">
										<div class="datefilter" style="float: left">
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										     <c:choose>
										     	<c:when test="${filter.generatedFrom != null}">
										     		<spring:eval expression="filter.generatedFrom" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.from" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										  <div class="overlaymenu hideme">
										  		<spring:message code="label.from" />
										    	<input type="hidden" name="metafilterdatefrom" class="hiddendate" value="<spring:eval expression="filter.generatedFrom" />" />
										    	<div id="metafilterdatefromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										   </div>
										</div>
										<div class="datefilter" style="float: left">	
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										  	<c:choose>
										     	<c:when test="${filter.generatedTo != null}">
										     		<spring:eval expression="filter.generatedTo" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.To" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										 <div class="overlaymenu hideme">
										 		<spring:message code="label.To" />
										    	<input type="hidden" name="metafilterdateto" class="hiddendate" value="<spring:eval expression="filter.generatedTo" />" />
										    	<div id="metafilterdatetodiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										    </div>
										</div>	
									</div>	
								</th>
							</c:if>
							<c:if test='${filter.visible("updated") == true}'>
								<th class="filtercell cellupdated">
									<div class="btn-toolbar" style="min-width: 150px; margin: 0px; text-align: center">
										<div class="datefilter" style="float: left">
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										  	<c:choose>
										     	<c:when test="${filter.updatedFrom != null}">
										     		<spring:eval expression="filter.updatedFrom" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.from" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										   <div class="overlaymenu hideme">
										   		<spring:message code="label.from" />
										    	<input type="hidden" name="metafilterupdatefrom" class="hiddendate" value="<spring:eval expression="filter.updatedFrom" />" />
										    	<div id="metafilterupdatefromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										    </div>
										</div>		
										<div class="datefilter" style="float: left">
										  <a class="btn btn-default" onclick="showOverlayMenu(this)" >
										  	<c:choose>
										     	<c:when test="${filter.updatedTo != null}">
										     		<spring:eval expression="filter.updatedTo" />
										     	</c:when>
										     	<c:otherwise>
										     		<spring:message code="label.To" />
										     	</c:otherwise>
										     </c:choose>
										    <span class="caret"></span>
										  </a>
										   <div class="overlaymenu hideme">
										   		<spring:message code="label.To" />
										    	<input type="hidden" name="metafilterupdateto" class="hiddendate" value="<spring:eval expression="filter.updatedTo" />" />
										    	<div id="metafilterupdatetodiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker"></div>
										   </div>
										</div>	
									</div>										
								</th>
							</c:if>
							<c:if test='${filter.visible("languages") == true}'>
								<th class="filtercell celllanguages">
									<div>
										<a class="btn btn-default" onclick="showOverlayMenu(this)" >
										    <span class="nobreak"><spring:message code="label.AllValues" /></span>
										    <span class="caret"></span>
										  </a>
										  
										  <div class="overlaymenu hideme">
										  	<a style="margin-bottom: 5px;"   onclick="$('#resultsForm').submit();" class="btn btn-default btn-sm btn-info"><spring:message code="label.ApplyFilter" /></a>
										  	 <c:forEach items="${form.getLanguages()}" var="lang" varStatus="status">
												<div>													
													<c:choose>
														<c:when test="${filter.languages.contains(lang.value.code)}">
															<input checked="checked" data-stopPropagation="true" type="checkbox" class="check checkFilterCell" name="metafilterlanguage" data-code="<esapi:encodeForHTMLAttribute>${lang.value.code}</esapi:encodeForHTMLAttribute>" value="${lang.value.code}"><esapi:encodeForHTML>${lang.value.name}</esapi:encodeForHTML></input>
														</c:when>
														<c:otherwise>
															<input data-stopPropagation="true" type="checkbox" class="check checkFilterCell" name="metafilterlanguage" data-code="${lang.value.code}" value="<esapi:encodeForHTMLAttribute>${lang.value.code}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML>${lang.value.name}</esapi:encodeForHTML></input>
														</c:otherwise>
													</c:choose>
												</div>
											</c:forEach>
										  </div>
									</div>
								</th>
							</c:if>
						</c:if>
						<c:if test="${form.getSurvey().isQuiz}">
							<th>
								<c:if test="${publication == null && (sessioninfo.owner == USER.id || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('AccessResults') > 1)}">
									<a href="recalculateScore?id=${form.getSurvey().getId()}" class="btn btn-default"><spring:message code="label.Recalculate" /></a>
								</c:if>
							</th>
						</c:if>
					</tr>
				</c:if>
			</thead>
			</table>
		</div>
		<div id="scrollarea" class="scrollarea">
		<table id="contentstable2" class="table table-bordered table-striped">
		<tbody id="contentstablebody">
			<c:forEach items="${paging.items}" var="answerSet">
				<tr name="${answerSet.uniqueCode}">				
					<c:if test="${publication == null}">		
						<td class="checkDelete" style="min-width: 13px">
							<input name="delete${answerSet.uniqueCode}" class="check checkDelete" type="checkbox" onclick="checkDelete(this)" />
						</td>			
						<td>						
							<c:choose>
								<c:when test="${sessioninfo.owner == USER.id || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('AccessResults') > 1}">
									<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Delete" />" class="iconbutton" onclick="showDeleteContributionDialog('${answerSet.uniqueCode}')" ><span class="glyphicon glyphicon-remove"></span></a>											
									<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Edit" />" class="iconbutton" target="_blank" href="<c:url value='/editcontribution/'/>${answerSet.uniqueCode}?mode=dialog" ><span class="glyphicon glyphicon-pencil"></span></a>
									<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Print" />" class="iconbutton" target="_blank" href="<c:url value='/printcontribution'/>?code=${answerSet.uniqueCode}" ><span class="glyphicon glyphicon-print"></span></a>
									<c:if test="${!allanswers}">
										<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.DownloadPDF" />" class="iconbutton" onclick="downloadAnswerPDF('${answerSet.uniqueCode}')"><img src="${contextpath}/resources/images/file_extension_pdf_small.png"></a>
									</c:if>
								</c:when>
								<c:otherwise>
									<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Delete" />" class="iconbutton disabled"><span class="glyphicon glyphicon-remove"></span></a>											
									<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Edit" />" class="iconbutton disabled"><span class="glyphicon glyphicon-pencil"></span></a>
									<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Print" />" class="iconbutton" target="_blank" href="<c:url value='/printcontribution'/>?code=${answerSet.uniqueCode}" ><span class="glyphicon glyphicon-print"></span></i></a>
									<c:if test="${!allanswers}">
										<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.DownloadPDF" />" class="iconbutton" onclick="downloadAnswerPDF('${answerSet.uniqueCode}')"><img src="${contextpath}/resources/images/file_extension_pdf_small.png"></a>
									</c:if>
								</c:otherwise>
							</c:choose>							
						</td>
					</c:if>		
					<c:forEach items="${form.getSurvey().getQuestions()}" var="question">
						<c:if test="${publication == null || publication.isAllQuestions() || publication.isSelected(question.id)}">
							<c:if test="${filter == null || filter.visibleQuestions.contains(question.id.toString())}">
								<c:choose>
									<c:when test="${question.getType() == 'Image' || question.getType() == 'Text' || question.getType() == 'Download' || question.getType() == 'Confirmation'}"></c:when>
									<c:when test="${question.getType() == 'GalleryQuestion' && !question.selection}"></c:when>
									<c:when test="${question.getType() == 'Matrix'}">
										<c:forEach items="${question.questions}" var="matrixQuestion">										
											<c:choose>
												<c:when test="${matrixQuestion == null}">
													<td <c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>><div class="answercell">&#160;</div></td>
												</c:when>
												<c:otherwise>
													<c:set var="answers" value="${answerSet.getAnswers(matrixQuestion.id, matrixQuestion.uniqueId)}" />
											
													<c:if test="${answers.size() > 0}">
														<td <c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>>
															<div class="answercell">
																<c:forEach items="${answers}" var="answer" varStatus="rowCounter">	
																	<c:if test="${rowCounter.index > 0}"> - </c:if>				
																	${form.getAnswerTitle(answer)}
																	<span class="assignedValue hideme"><esapi:encodeForHTML>${form.getAnswerShortname(answer)}</esapi:encodeForHTML></span>
																</c:forEach>
															</div>
														</td>
						 							</c:if>
													<c:if test="${answers.size() < 1}">
														<td <c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>><div class="answercell">&#160;</div></td>
													</c:if>
												</c:otherwise>
											</c:choose>										
											
										</c:forEach>										
									</c:when>
									<c:when test="${question.getType() == 'RatingQuestion'}">
										<c:forEach items="${question.questions}" var="childQuestion">										
											<c:choose>
												<c:when test="${childQuestion == null}">
													<td <c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>><div class="answercell">&#160;</div></td>
												</c:when>
												<c:otherwise>
													<c:set var="answers" value="${answerSet.getAnswers(childQuestion.id, childQuestion.uniqueId)}" />
											
													<c:if test="${answers.size() > 0}">
														<td <c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>>
															<div class="answercell">
																${answers.get(0).getValue()}
															</div>
														</td>
						 							</c:if>
													<c:if test="${answers.size() < 1}">
														<td <c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>><div class="answercell">&#160;</div></td>
													</c:if>
												</c:otherwise>
											</c:choose>										
											
										</c:forEach>										
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${question.getType() == 'Table'}">											
												<c:forEach var="r" begin="1" end="${question.allRows-1}"> 
													<c:forEach var="c" begin="1" end="${question.allColumns-1}"> 																
														<td class="cell${question.childElements[question.columns + r - 1].id}-${question.childElements[c].id}" <c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>>
															<div class="answercell">
																<esapi:encodeForHTML>${answerSet.getTableAnswer(question, r, c, false)}</esapi:encodeForHTML>
															</div>																								
														</td>																
													</c:forEach>													
												</c:forEach>
											</c:when>
											
											<c:when test="${question.getType() == 'GalleryQuestion'}">
												<c:set var="answers" value="${answerSet.getAnswers(question.id, question.uniqueId)}" />
												<td class="cell${question.id}" <c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>>
													<div class="answercell">												
														<c:forEach items="${question.files}" var="file" varStatus="counter">
															<c:forEach items="${answers}" var="answer">	
																<c:if test="${answer.value == counter.index.toString()}"><esapi:encodeForHTML>${file.name}</esapi:encodeForHTML><br />				
																</c:if>
															</c:forEach>
														</c:forEach>
													</div>
												</td>									
											</c:when>
											
											<c:otherwise>
												<c:set var="answers" value="${answerSet.getAnswers(question.id, question.uniqueId)}" />
										
												<c:choose>
													<c:when test="${question.getType() == 'Upload'}">
														<c:if test="${publication == null || publication.getShowUploadedDocuments()}">
															<c:if test="${answers.size() > 0}">
																<td class="cell${question.id}" <c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>>
																	<div class="answercell" style="overflow: auto">
																		<c:forEach items="${answers}" var="answer">	
																			<c:forEach items="${answer.files}" var="file">		
																				<c:choose>
																					<c:when test="${sessioninfo.owner == USER.id || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('AccessResults') > 1 || (form.survey.isDraft && USER.getLocalPrivilegeValue('AccessDraft') > 0)}">
																						<a target="blank" href="${contextpath}/files/${form.survey.uniqueId}/${file.uid}"><esapi:encodeForHTML>${file.name}</esapi:encodeForHTML></a><br />
																					</c:when>
																					<c:otherwise>
																						<esapi:encodeForHTML>${file.name}</esapi:encodeForHTML><br />
																					</c:otherwise>
																				</c:choose>
																			</c:forEach>
																		</c:forEach>
																	</div>
																</td>
															</c:if>
															<c:if test="${answers.size() < 1}">
																<td class="cell${question.id}" <c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>><div class="answercell">&#160;</div></td>
															</c:if>
							 							</c:if>							 							
													</c:when>
													<c:when test="${question.getType() == 'SingleChoiceQuestion' || question.getType() == 'MultipleChoiceQuestion'}">
														<c:if test="${answers.size() > 1}">
															<td class="cell${question.id}" <c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>>
																<div class="answercell">
																	<c:forEach items="${answers}" var="answer">			
																		${form.getAnswerTitle(answer)}
																		<span class="assignedValue hideme"><esapi:encodeForHTML>${form.getAnswerShortname(answer)}</esapi:encodeForHTML></span><br />
																	</c:forEach>
																</div>
															</td>
							 							</c:if>
														<c:if test="${answers.size() == 1}">
															<td class="cell${question.id}" <c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>>
																<div class="answercell">
																	${form.getAnswerTitle(answers.get(0))}
																	<span class="assignedValue hideme"><esapi:encodeForHTML>${form.getAnswerShortname(answers.get(0))}</esapi:encodeForHTML></span>
																</div>
															</td>
														</c:if>
							 							<c:if test="${answers.size() < 1}">
															<td class="cell${question.id}" <c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>><div class="answercell">&#160;</div></td>
														</c:if>
													</c:when>
													<c:otherwise>
														<c:if test="${answers.size() > 1}">
															<td class="cell${question.id}" <c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>>
																<div class="answercell">
																	<c:forEach items="${answers}" var="answer">			
																		<esapi:encodeForHTML>${form.getAnswerTitle(answer)}</esapi:encodeForHTML>
																		<span class="assignedValue hideme"><esapi:encodeForHTML>${form.getAnswerShortname(answer)}</esapi:encodeForHTML></span><br />
																	</c:forEach>
																</div>
															</td>
							 							</c:if>
														<c:if test="${answers.size() == 1}">
															<td class="cell${question.id}" <c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>>
																<div class="answercell">
																	<esapi:encodeForHTML>${form.getAnswerTitle(answers.get(0))}</esapi:encodeForHTML>
																	<span class="assignedValue hideme"><esapi:encodeForHTML>${form.getAnswerShortname(answers.get(0))}</esapi:encodeForHTML></span>
																</div>
															</td>
														</c:if>
							 							<c:if test="${answers.size() < 1}">
															<td class="cell${question.id}" <c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>><div class="answercell">&#160;</div></td>
														</c:if>
													</c:otherwise>									
												</c:choose>
											</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>			
							</c:if>		
						</c:if>
					</c:forEach>
					
					<c:if test="${publication == null}">
						<c:choose>
							<c:when test='${form.survey.security.equals("openanonymous") || form.survey.security.equals("securedanonymous")}'>
								<c:if test='${filter.visible("invitation") == true}'><td class="cellinvitation"><div class="answercell"><spring:message code="label.Anonymous" /></div></td></c:if>
								<c:if test='${filter.visible("case") == true}'><td class="cellcase"><div class="answercell"><spring:message code="label.Anonymous" /></div></td></c:if>
								<c:if test='${filter.visible("user") == true}'><td class="celluser"><div class="answercell"><spring:message code="label.Anonymous" /></div></td></c:if>
							</c:when>
							<c:otherwise>
								<c:if test='${filter.visible("invitation") == true}'><td class="cellinvitation"><div class="answercell"><esapi:encodeForHTML>${answerSet.invitationId}</esapi:encodeForHTML></div></td></c:if>
								<c:if test='${filter.visible("case") == true}'><td class="cellcase"><div class="answercell"><esapi:encodeForHTML>${answerSet.uniqueCode}</esapi:encodeForHTML></div></td></c:if>
								<c:if test='${filter.visible("user") == true}'><td class="celluser"><div class="answercell"><esapi:encodeForHTML>${answerSet.responderEmail}</esapi:encodeForHTML></div></td></c:if>
							</c:otherwise>
						</c:choose>
					
										
						<c:if test='${filter.visible("created") == true}'><td class="cellcreated"><div class="answercell"><esapi:encodeForHTML><spring:eval expression="answerSet.date" /></esapi:encodeForHTML></div></td></c:if>
						<c:if test='${filter.visible("updated") == true}'><td class="cellupdated"><div class="answercell"><esapi:encodeForHTML><spring:eval expression="answerSet.updateDate" /></esapi:encodeForHTML></div></td></c:if>
						<c:if test='${filter.visible("languages") == true}'><td class="celllanguages"><div class="answercell"><esapi:encodeForHTML>${answerSet.languageCode}</esapi:encodeForHTML></div></td></c:if>
						
					</c:if>
					
					<c:if test="${form.getSurvey().isQuiz}">
						<td class="cellscore"><div class="answercell">${answerSet.score}</div></td>
					</c:if>		
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</div>


	<div id="tbllist-empty" class="noDataPlaceHolder" <c:if test="${paging.items.size() == 0}">style="display:block;"</c:if>>
		<p>
			<spring:message code="label.NoDataContributionText"/>&nbsp;<img src="${contextpath}/resources/images/icons/32/forbidden_grey.png" alt="no data"/>
		<p>
	</div>	
	
	<c:if test="${pagingTable ne false}">
		<div class="RowsPerPage hideme">
			<span><spring:message code="label.RowsPerPage" />&#160;</span>
		    <form:select onchange="moveTo('${paging.currentPage}')" path="itemsPerPage" id="itemsPerPage" style="width:70px; margin-top: 0px;" class="middle">
				<form:options items="${paging.itemsPerPageOptions}" />
			</form:select>		
		</div>
	</c:if>		
		
</div>

<style>
	.overlay-validation-error {
		color: #f00;
		position: absolute;
		margin-top: -50px;
    	max-width: 150px;
    	background-color: #fff;
	}
</style>

<script type="text/javascript"> 
var scrollTimeout = null;
var closeOverlayDivsEnabled = false;

		$(function() {
			
			 $('[data-toggle="tooltip"]').tooltip({
				    trigger : 'hover'
			 });
	
			$("#resultsForm").submit( function() {
									
		        var result = true; 
		        $(".overlay-validation-error").remove();
				
				$("#resultsForm").find(".filtercell").each(function(){
					 $(this).find(".validation-error").remove();
					 $(this).find("input[type=text]").each(function(){
						if ($(this).val().length > 0 && $(this).val().length < 3)
						{
							var offset = $(this).parent().offset().left;
							var scroll = $("#scrollareaheader").scrollLeft();
							var width = $("#scrollareaheader").width();
							
							var marginLeft = 0;
							
							if (result)
							{
								$("#scrollareaheader").scrollLeft(scroll + offset - 210);
								$("#scrollarea").scrollLeft(scroll + offset - 210);
								marginLeft = -1 * parseInt($("#scrollareaheader").scrollLeft()); //(scroll + offset - 210);
								
								//we have to do it this way as the scroll event hides all overlay-validation-errors
								var input = $(this);
								setTimeout(function(){
									var div = document.createElement("div");
									$(div).addClass("overlay-validation-error").css("margin-left", marginLeft + "px").html("<spring:message code="label.atLeast3Characters" />");
									input.before(div);
									if ($(div).height() > 50)
									{
										var t = -1 * $(div).height() - 15;
										$(div).css("margin-top", t + "px");
									}
								},500);								
							}
							
							result = false;
							$("#show-wait-image").modal('hide');
							return;
						};
					 });
					 
					 if (!result) return;
				});
				
				if (result)
				{
					$("#configure-columns-dialog").find("input").each(function(){
						$(this).hide();
						$("#resultsForm").append(this);
					});
				}
				
		         return result;
		     });
			
 			  $(window).scroll(function() {$(".overlaymenu").hide();});
 			  $(window).resize(function() {
 				  $(".overlaymenu").hide();
 				  adaptScrollArea();
 			  });		
			 
			  $("#scrollarea").scroll(function() {
				  $(".overlaymenu").hide();
				  $(".overlay-validation-error").remove();
				  
				  $("#scrollareaheader").scrollLeft($("#scrollarea").scrollLeft());
				  
				  if($(this).scrollTop() + 
                          $(this).innerHeight()
                          >= $(this)[0].scrollHeight)
                       {
					  loadMore();
                       }
				 });
			  
			  $("#scrollareaheader").scroll(function() {
				  $(".overlaymenu").hide();
				  if (scrollTimeout) clearTimeout(scrollTimeout);
				    scrollTimeout = setTimeout(function(){resetSliderPositions($("#contentstable"))},1000);
				 });
			 
			 $(".checkFilterCell").click(function(event) {
				    checkFilterCell($(this).closest('.filtercell'), false);
					checkNoBreaks();
				    event.stopPropagation();
				});
			 
			 $(".overlaymenu").bind('wheel mousewheel', function (e, delta) {
				    // Restricts mouse scrolling to the scrolling range of this element.				    
				    delta = null;
		            if (e.originalEvent) {
		                if (e.originalEvent.wheelDelta) delta = e.originalEvent.wheelDelta / -40;
		                if (e.originalEvent.deltaY) delta = e.originalEvent.deltaY;
		                if (e.originalEvent.detail) delta = e.originalEvent.detail;
		            }
				    
				    if (this.clientHeight + this.scrollTop === this.scrollHeight && delta > 0) {
				        e.preventDefault();
				    }
				});
			 
			 adaptScrollArea();
			 
			<c:if test="${paging.items.size() == 0}">
				$("#scrollarea").hide();
				$("#scrollareaheader").css("overflow-x","auto");
				$("#scrollareaheader").css("overflow-y","auto");
			</c:if>
			
			<c:if test="${paging.items.size() > 0}">
				if ($('#scrollarea').hasScrollBar())
				{
					$("#scrollareaheader").css("overflow-y","scroll");
				} else {
					$("#scrollareaheader").css("overflow-y","auto");
				}
			</c:if>
			
			makeResizable($("#contentstable"));
			
			//call it this way due to a bug in IE
			setTimeout(function(){ checkNoBreaks(); }, 1000);
			
			$(document).mouseup(function (e)
			{
				if ($(e.target).hasClass("overlaybutton") || $(e.target).closest(".overlaybutton").length > 0)
				{
					e.stopPropagation();
 					return;
				}				
				
			    var container = $(".overlaymenu");

			    if (!container.is(e.target) // if the target of the click isn't the container...
			        && container.has(e.target).length === 0) // ... nor a descendant of the container
			    {
			        container.hide();
			    }
			});
			
		});
			
		function synchronizeTableSizes()
		{
			$('#contentstable2').css("width", $('#contentstable').css("width"));
			
			var headerrow = $('#contentstable TR').first();
			var newWidth;
			var widths = [];			
			
			$(headerrow).find('TH').each(function(index){
				
				newWidth = $(this).css('width');
				widths[index] = newWidth;
			});				
			
			$('#contentstable2 TR').each(function() 
			{
				$(this).find('TD').each(function(index){
					
					$(this).css('width',widths[index]);
					var newWidthNoPadding = parseInt(widths[index].replace("px", "")) - 17;
					$(this).find(".answercell").css('width',newWidthNoPadding + "px");
					$(this).find(".headertitle").css('width',newWidthNoPadding + "px");
				});				
			});		
		}
		
		function resetTableSizes (table, change, columnIndex)
		{
			//calculate new width;
			var tableId = table.attr('id'); 
			var myWidth = $('#'+tableId+' TR TH').get(columnIndex).offsetWidth;
			
			if (myWidth + change < 150)
			{
			 	change = 150 - myWidth;	
			}
			
			if ($($('#'+tableId+' TR TH').get(columnIndex)).hasClass("cellcreated") || $($('#'+tableId+' TR TH').get(columnIndex)).hasClass("cellupdated"))
			{
				if (myWidth + change < 277)
				{
				 	change = 277 - myWidth;	
				}
			}
			
			var newWidth = (myWidth+change)+'px';
			var newWidthNoPadding = (myWidth+change-16)+'px';
			
			$('#contentstable').css("width",$('#contentstable')[0].offsetWidth + change);
			$('#contentstable2').css("width",$('#contentstable2')[0].offsetWidth + change);
			
			$('#'+tableId+' TR').each(function() 
			{
				$(this).find('TD').eq(columnIndex).css('width',newWidth);
				$(this).find('TH').eq(columnIndex).css('width',newWidth);
				$(this).find('TD').eq(columnIndex).find(".answercell").css('width',newWidthNoPadding);
				$(this).find('TH').eq(columnIndex).find(".answercell").css('width',newWidthNoPadding);
				$(this).find('TD').eq(columnIndex).find(".headertitle").css('width',newWidthNoPadding);
				$(this).find('TH').eq(columnIndex).find(".headertitle").css('width',newWidthNoPadding);
			});
			
			$('#contentstable2 TR').each(function() 
			{
				$(this).find('TD').eq(columnIndex).css('width',newWidth);
				$(this).find('TH').eq(columnIndex).css('width',newWidth);
				$(this).find('TD').eq(columnIndex).find(".answercell").css('width',newWidthNoPadding);
				$(this).find('TH').eq(columnIndex).find(".answercell").css('width',newWidthNoPadding);
			});			
			
			resetSliderPositions($("#contentstable"));
		};

		function resetSliderPositions(table)
		{
			var tableId = table.attr('id'); 
			//put all sliders on the correct position
			table.find(' TR:first TH').each(function(index)
			{ 
				var td = $(this);
				var newSliderPosition = td.offset().left+td.outerWidth();
				
				if (newSliderPosition > $( window ).width() - 20)
				{
					newSliderPosition = -10;	
				}
				
				$("#"+tableId+"_id"+(index+1)).css({ left:   newSliderPosition , height: table.height() + 'px'}  );
			
			});
		}
		
		function saveSliderPositions()
		{
			if (localStorage != null)
			{
			localStorage.setItem("ResultsTableWidth", $('#contentstable').css("width"));		
			
			var headerrow = $('#contentstable TR').first();
			
			var widths = [];			
			
			$(headerrow).find('TH').each(function(index){
				
				newWidth = $(this).css('width');
				localStorage.setItem("ResultsColumnWidth" + index, newWidth);
			});			
		}
		}
		
		function readSliderPositions()
		{	
			if (localStorage != null)
			{
			$('#contentstable').css("width", localStorage.getItem("ResultsTableWidth"));
			$('#contentstable TR:first').each(function() 
				{
					$(this).find('TH').each(function(index){
						var width = localStorage.getItem("ResultsColumnWidth" + index);
						if (typeof width != 'undefined' && width != null)
						{
							var newWidthNoPadding = parseInt(width.replace("px", "")) - 16;
							$(this).css('width', width);
							$(this).find(".answercell").css('width',newWidthNoPadding + "px");
							$(this).find(".headertitle").css('width',newWidthNoPadding + "px");
						}						
					});				
				});	
 			synchronizeTableSizes();
		}
		}

		function makeResizable(table)
		{		
			//get number of columns
			var numberOfColumns = table.find('TR:first TH').size();

			//id is needed to create id's for the draghandles
			var tableId = table.attr('id'); 
			
			for (var i=0; i<=numberOfColumns; i++)
			{
				$('<div class="draghandle" id="'+tableId+'_id'+i+'"></div>').insertBefore(table).data('tableid', tableId).data('myindex',i).draggable(
				{ axis: "x",
				  start: function () 
				  {
					var tableId = ($(this).data('tableid'));
					$(this).toggleClass( "dragged" );
					//set the height of the draghandle to the current height of the table, to get the vertical ruler
					$(this).css({ height: $('#'+tableId).height() + $('#scrollarea').height() + 2 + 'px'} );
				  },
				  stop: function (event, ui){
					var tableId = ($(this).data('tableid'));
					$( this ).toggleClass( "dragged" ); 
					var oldPos  = ($( this ).data("ui-draggable").originalPosition.left);
					var newPos = ui.position.left;
					var index =  $(this).data("myindex");
					resetTableSizes($('#'+tableId), newPos-oldPos, index-1);
					checkNoBreaks();
				  }		  
				}
				);
			};
			resetSliderPositions(table);
		}

		
		function adaptScrollArea()
		{
			<c:choose>
				<c:when test="${publication != null}">
				var height = $( window ).height() - 530;
				</c:when>
				<c:otherwise>
				var height = $( window ).height() - 430;
				</c:otherwise>
			</c:choose>			
			
			 if (height < 200) height = 200;
			 $('#scrollarea').css("height", height);
			 
			 $('#scrollareastatistics').css("height", height-50);
			 $('#scrollareastatisticsquiz').css("height", height-50);
			 $('#scrollareacharts').css("height", height-50);
			 
			 $('#contentstable2').css("min-height", height-20);
			 $('#contentstable2').css("height", height-20);
			
			 var width = $( window ).width() - 30;
			 
			 $('#scrollarea').css("width", width);
			 			 
			 $('#scrollareaheader').css("width", width);			
		}
		
		function scrollbarWidth() { 
		    var div = $('<div style="width:50px;height:50px;overflow:hidden;position:absolute;top:-200px;left:-200px;"><div style="height:100px;"></div>'); 
		    // Append our div, do our calculation and then remove it 
		    $('body').append(div); 
		    var w1 = $('div', div).innerWidth(); 
		    div.css('overflow-y', 'scroll'); 
		    var w2 = $('div', div).innerWidth(); 
		    $(div).remove(); 
		    return (w1 - w2); 
		}
		
		function getAnswers(list, id, uid)
		{
			var result = new Array();
			
			for (var i = 0; i < list.length; i++)
			{
				if (list[i].questionId == id)
				{
					result[result.length] = list[i];
				} else if (list[i].questionUniqueId == uid)
				{
					result[result.length] = list[i];
				}
			}
			
			return result;
		}
		
		function getTableAnswer(list, id, r, c)
		{
			for (var i = 0; i < list.length; i++)
			{
				if (list[i].questionId == id && list[i].row == r && list[i].column == c)
				{
					return list[i].value;
				}
			}
			
			return null;
		}
		
		var newPage = 6;
		var endreached = false;
		var inloading = false;
		function loadMore()
		{
			if (endreached || inloading)
			{
				return;	
			}
			
			inloading = true;
			
			$( "#wheel" ).show();
			var s = "page=" + newPage++ + "&rows=10";	
			
			<c:if test="${publication != null}">
				s = s + "&publicationmode=true";			
			</c:if>
			
			var answers = null;
						
			$.ajax({
				type:'GET',
				  url: "${contextpath}/${form.survey.shortname}/management/resultsJSON",
				  dataType: 'json',
				  data: s,
				  cache: false,
				  success: function( list ) {
				  
					  if (list.length == 0)
					  {
						  endreached = true;
						  return;
					  }
					  
					  var trm = null;
					  
					  var i = 0;
					  
					  while (i < list.length)
					  {
						 var tr = document.createElement("tr");
						 
						 <c:choose>
							 <c:when test="${publication == null && (sessioninfo.owner == USER.id || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('AccessResults') > 1)}">
							 
							 	var td = document.createElement("td");
							 	$(td).addClass("checkDelete").css("min-width","13px");
							 	var inp = document.createElement("input");
							 	$(inp).attr("name","delete" + list[i]).addClass("check").addClass("checkDelete").attr("type","checkbox").attr("onclick","checkDelete(this)");
							 	$(td).append(inp);
							 	$(tr).append(td);
							 	
							 	if ($("#show-delete-checkboxes").is(":checked"))
								{
									//visible
								} else {
									$(td).addClass("hiddenTableCell");
								}
							 
								td = document.createElement("td");
								var a = document.createElement("a");
								$(a).attr("data-toggle", "tooltip").attr("rel","tooltip").attr("title",'<spring:message code="label.Delete" />').addClass("iconbutton").attr("onclick","showDeleteContributionDialog('" + list[i] + "')").append('<span class="glyphicon glyphicon-remove"></span>');
								$(td).append(a);
								$(td).append("&nbsp;");
								a = document.createElement("a");
								$(a).attr("data-toggle", "tooltip").attr("rel","tooltip").attr("title",'<spring:message code="label.Edit" />').addClass("iconbutton").attr("target", "_blank").attr("href","<c:url value='/editcontribution/'/>" + list[i] + "?mode=dialog").append('<span class="glyphicon glyphicon-pencil"></span>');
								$(td).append(a);
								$(td).append("&nbsp;");
								a = document.createElement("a");
								$(a).attr("data-toggle", "tooltip").attr("rel","tooltip").attr("title",'<spring:message code="label.Print" />').addClass("iconbutton").attr("target","_blank").attr("href","<c:url value='/printcontribution/'/>?code=" + list[i] + "").append('<span class="glyphicon glyphicon-print"></span>');
								$(td).append(a);
								$(td).append("&nbsp;");
								
								<c:if test="${!allanswers}">
								a = document.createElement("a");
								$(a).attr("data-toggle", "tooltip").attr("rel","tooltip").attr("title",'<spring:message code="label.DownloadPDF" />').attr("target","_blank").attr("onclick",'downloadAnswerPDF("' + list[i] + '")').append('<img src="${contextpath}/resources/images/file_extension_pdf_small.png">');
								$(td).append(a);
								</c:if>
								
								$(tr).append(td);
							</c:when>
							<c:when test="${publication == null && (sessioninfo.owner == USER.id || USER.formPrivilege > 0 || USER.getLocalPrivilegeValue('AccessResults') > 0)}">
							 
							 	var td = document.createElement("td");
							 	$(td).addClass("checkDelete").css("min-width","13px");
							 	var inp = document.createElement("input");
							 	$(inp).attr("name","delete" + list[i]).addClass("check").addClass("checkDelete").attr("type","checkbox").attr("onclick","checkDelete(this)");
							 	$(td).append(inp);
							 	$(tr).append(td);
							 	
							 	if ($("#show-delete-checkboxes").is(":checked"))
								{
									$(td).show();
								} else {
									$(td).hide();
								}
							 
								td = document.createElement("td");
								var a = document.createElement("a");
								$(a).attr("data-toggle", "tooltip").attr("rel","tooltip").attr("title",'<spring:message code="label.Delete" />').addClass("btn disabled btn-default").addClass("btn-xs").append('<span class="glyphicon glyphicon-remove"></span>');
								$(td).append(a);
								$(td).append("&nbsp;");
								a = document.createElement("a");
								$(a).attr("data-toggle", "tooltip").attr("rel","tooltip").attr("title",'<spring:message code="label.Edit" />').addClass("btn btn-default btn-xs disabled").append('<span class="glyphicon glyphicon-pencil"></span>');
								$(td).append(a);
								$(td).append("&nbsp;");
								a = document.createElement("a");
								$(a).attr("data-toggle", "tooltip").attr("rel","tooltip").attr("title",'<spring:message code="label.Print" />').addClass("btn btn-default btn-xs").attr("target","_blank").attr("href","<c:url value='/printcontribution/'/>?code=" + list[i] + "").append('<span class="glyphicon glyphicon-print"></span>');
								$(td).append(a);
								$(td).append("&nbsp;");
								a = document.createElement("a");
								$(a).attr("data-toggle", "tooltip").attr("rel","tooltip").attr("title",'<spring:message code="label.DownloadPDF" />').attr("target","_blank").attr("onclick",'downloadAnswerPDF("' + list[i] + '")').append('<img src="${contextpath}/resources/images/file_extension_pdf_small.png">');
								$(td).append(a);
								$(tr).append(td);
							</c:when>
						</c:choose>
						
						i++;
						
						<c:forEach items="${form.getSurvey().getQuestions()}" var="question">
						
							<c:if test="${publication == null || publication.isAllQuestions() || publication.isSelected(question.id)}">
								<c:if test="${filter == null || filter.visibleQuestions.contains(question.id.toString())}">
									<c:if test="${question.getType() != 'Image' && question.getType() != 'Text' && question.getType() != 'Download'}">
										<c:choose>
											<c:when test="${question.getType() == 'Matrix'}">												
												<c:forEach items="${question.questions}" var="matrixQuestion">
												
													var td = document.createElement("td");
													var div = document.createElement("div");
													$(div).addClass("answercell");
													$(div).append(list[i++]);
													$(td).append(div);
													$(tr).append(td);
												
												</c:forEach>	
											</c:when>
											
											<c:when test="${question.getType() == 'Table'}">												
												<c:forEach var="r" begin="1" end="${question.rows-1}"> 
													<c:forEach var="c" begin="1" end="${question.columns-1}"> 
													
														var td = document.createElement("td");
														var div = document.createElement("div");
														$(div).addClass("answercell");
														$(div).append(list[i++]);
														$(td).append(div);
														$(tr).append(td);
														
													</c:forEach>													
												</c:forEach>																		
											</c:when>
											
											<c:when test="${question.getType() == 'GalleryQuestion' && question.selection}">
												var td = document.createElement("td");
												$(td).addClass("cell${question.id}");
												var div = document.createElement("div");
												$(div).addClass("answercell");
												$(div).append(list[i++]);
												$(td).append(div);
												$(tr).append(td);											
											</c:when>											
										
											<c:when test="${question.getType() == 'Upload'}">
												<c:if test="${publication == null || publication.getShowUploadedDocuments()}">
												
													var td = document.createElement("td");
													$(td).addClass("cell${question.id}");															
													var div = document.createElement("div");
													$(div).addClass("answercell").css("overflow", "auto");															
													$(div).append(list[i++]);															
													$(td).append(div);
													$(tr).append(td);	
												
												</c:if>
					 						</c:when>
					 						
					 						<c:when test="${question.getType() == 'RatingQuestion'}">											
												<c:forEach items="${question.childElements}" var="childQuestion">
												
													var td = document.createElement("td");
													var div = document.createElement("div");
													$(div).addClass("answercell");
													$(div).append(list[i++]);
													$(td).append(div);
													$(tr).append(td);
												
												</c:forEach>	
											</c:when>
					 						
											<c:otherwise>
															
													var td = document.createElement("td");
													$(td).addClass("cell${question.id}");															
													var div = document.createElement("div");
													$(div).addClass("answercell");															
													$(div).append(list[i++]);
													$(td).append(div);
													$(tr).append(td);
												
											</c:otherwise>									
										</c:choose>
											
									</c:if>
								</c:if>
							</c:if>
						</c:forEach>
					
						<c:if test="${publication == null}">						
							<c:choose>
								<c:when test='${form.survey.security.equals("openanonymous") || form.survey.security.equals("securedanonymous")}'>
									<c:if test='${filter.visible("invitation") == true}'>$(tr).append('<td class="cellinvitation"><div class="answercell"><spring:message code="label.Anonymous" /></div></td>');i++;</c:if>
									<c:if test='${filter.visible("case") == true}'>$(tr).append('<td class="cellcase"><div class="answercell"><spring:message code="label.Anonymous" /></div></td>');i++;</c:if>
									<c:if test='${filter.visible("user") == true}'>$(tr).append('<td class="celluser"><div class="answercell"><spring:message code="label.Anonymous" /></div></td>');i++;</c:if>
								</c:when>
								<c:otherwise>
									<c:if test='${filter.visible("invitation") == true}'>$(tr).append('<td class="cellinvitation"><div class="answercell">' + list[i++] + '</div></td>');</c:if>
									<c:if test='${filter.visible("case") == true}'>$(tr).append('<td class="cellcase"><div class="answercell">' + list[i++] + '</div></td>');</c:if>
									<c:if test='${filter.visible("user") == true}'>$(tr).append('<td class="celluser"><div class="answercell">' + list[i++] + '</div></td>');</c:if>
								</c:otherwise>
							</c:choose>
						
							<c:if test='${filter.visible("created") == true}'>$(tr).append('<td class="cellcreated"><div class="answercell">' + list[i++] + '</div></td>');</c:if>
							<c:if test='${filter.visible("updated") == true}'>$(tr).append('<td class="cellupdated"><div class="answercell">' + list[i++] + '</div></td>');</c:if>
							<c:if test='${filter.visible("languages") == true}'>$(tr).append('<td class="celllanguages"><div class="answercell">' + list[i++] + '</div></td>');</c:if>
						</c:if>			
						
						<c:if test="${form.getSurvey().isQuiz}">
							$(tr).append('<td class="cellscore"><div class="answercell">' + list[i++] + '</div></td>');
						</c:if>
						
						$( "#contentstablebody").append(tr);						
					  }
					  
					  inloading = false;
					  $( "#wheel" ).hide();		
					  
					  $('[data-toggle="tooltip"]').tooltip({
						    trigger : 'hover'
					 });
				}});
		}
</script>
		
