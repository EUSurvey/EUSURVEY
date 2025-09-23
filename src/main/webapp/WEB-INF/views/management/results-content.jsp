<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>

<c:choose>
	<c:when test="${publication != null}">
		<div id="results-table" style="margin-top: 60px; min-height: 400px;">		
	</c:when>
	<c:otherwise>
		<div id="results-table" style="margin-top: 0px;">		
	</c:otherwise>
</c:choose>

    <c:if test="${reportingdatabaseused == null}">
        <div id="ResultFilterLimit" style="font-size:90%; text-align: center; margin-bottom: 10px;">
            <span class="glyphicon glyphicon-info-sign"></span>
            <spring:message code="info.ResultFilterLimit" />
            <c:choose>
                <c:when test="${USER.resultAccess != null && USER.resultAccess.resultFilter != null && USER.resultAccess.resultFilter.filterValues != null && USER.resultAccess.resultFilter.filterValues.size() > 0}">
                   - <spring:message code="info.ResultFilterLimitPredefined" arguments="${USER.resultAccess.resultFilter.filterValues.size()}" />
                </c:when>
                <c:when test="${publication != null && publication.filter != null && publication.filter.filterValues != null && publication.filter.filterValues.size() > 0}">
                   - <spring:message code="info.ResultFilterLimitPredefined" arguments="${publication.filter.filterValues.size()}" />
                </c:when>
            </c:choose>
        </div>
	</c:if>
	
	<span id="lastupdate"></span>

	<div id="scrollareaheader" style="overflow-x: auto" tabindex="-1">
		<table id="contentstable" class="table table-bordered table-striped table-styled">
			<c:set var="count" value="0" scope="page" />
			<thead style="background-position: initial initial; background-repeat: initial initial;">
				<tr>
					<c:if test="${publication == null}">
						<c:if test="${publication == null && (sessioninfo.owner == USER.id || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('AccessResults') > 1)}">
							<th class="topaligned checkDelete question-title"><span data-toggle="tooltip" class="glyphicon glyphicon-info-sign" style="color:white;" title="<spring:message code="info.LongQuestionNameShortened" />"></span></th>
						</c:if>
						<th class="topaligned question-title" style="width: 150px"><div style="width: 133px"><spring:message code="label.Actions" /></div></th>
					</c:if>
					<c:forEach items="${form.getSurvey().getQuestions()}" var="question">
						<c:if test="${publication == null || publication.isAllQuestions() || publication.isSelected(question.id)}">
							<c:if test="${filter == null || filter.visibleQuestions.contains(question.id.toString())}">
								<c:choose>
									<c:when test="${question.getType() == 'Image' || question.getType() == 'Text' || question.getType() == 'Download' || question.getType() == 'Confirmation' || question.getType() == 'Ruler'}"></c:when>
									<c:when test="${question.getType() == 'GalleryQuestion' && !question.selection}"></c:when>
									<c:when test="${question.getType() == 'Matrix'}">
										<c:forEach items="${question.getQuestions()}" var="matrixquestion">
											<th class="topaligned cell${matrixquestion.id} question-title"><div class="headertitle" title="${question.getStrippedTitle()}&nbsp;:&nbsp;${matrixquestion.getStrippedTitle()}">${question.getStrippedTitle()}&nbsp;:&nbsp;${matrixquestion.getStrippedTitle()}&nbsp;<span class="assignedValue hideme">(${matrixquestion.shortname})</span></div></th>
											<c:set var="count" value="${count + 1}" scope="page"/>
										</c:forEach>
									</c:when>
									<c:when test="${question.getType() == 'Table'}">
										<c:forEach items="${question.getQuestions()}" var="tablequestion">
											<c:forEach items="${question.getAnswers()}" var="tableanswer">
												<th class="topaligned cell${tablequestion.id}-${tableanswer.id} question-title"><div class="headertitle" title="${question.getStrippedTitle()}&nbsp;${tablequestion.getStrippedTitle()}">${question.getStrippedTitle()}&nbsp;${tablequestion.getStrippedTitle()} <span class="assignedValue hideme">(${tablequestion.shortname})</span> : ${tableanswer.getStrippedTitle()}<span class="assignedValue hideme">(${tableanswer.shortname})</span> </div></th>
												<c:set var="count" value="${count + 1}" scope="page"/>
											</c:forEach>
										</c:forEach>
									</c:when>
									<c:when test="${question.getType() == 'RatingQuestion'}">
										<c:forEach items="${question.getQuestions()}" var="childquestion">
											<th class="topaligned cell${childquestion.id}" question-title><div class="headertitle" title="${question.getStrippedTitle()}&nbsp;:&nbsp;${childquestion.getStrippedTitle()}">${question.getStrippedTitle()}&nbsp;:&nbsp;${childquestion.getStrippedTitle()}&nbsp;<span class="assignedValue hideme">(${childquestion.shortname})</span></div></th>
											<c:set var="count" value="${count + 1}" scope="page"/>
										</c:forEach>
									</c:when>
									<c:when test="${question.getType() == 'Upload'}">
										<c:if test="${publication == null || publication.getShowUploadedDocuments()}">
											<th class="topaligned cell${question.id} question-title"><div class="headertitle" title="${question.getStrippedTitle()}">${question.getStrippedTitle()}  <span class="assignedValue hideme">(${question.shortname})</span></div></th>
											<c:set var="count" value="${count + 1}" scope="page"/>
										</c:if>
									</c:when>
									<c:when test="${question.getType() == 'ComplexTable'}">
										<c:forEach items="${question.getQuestionChildElements()}" var="child">
											<th class="topaligned cell${child.id} question-title"><div class="headertitle" title="${child.getResultTitle(question)}">${child.getResultTitle(question)}<span class="assignedValue hideme">(${child.shortname})</span></div></th>
											<c:set var="count" value="${count + 1}" scope="page"/>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<th class="topaligned cell${question.id} question-title"><div class="headertitle" title="${question.getStrippedTitle()}">${question.getStrippedTitle()} <span class="assignedValue hideme">(${question.shortname})</span></div></th>
										<c:set var="count" value="${count + 1}" scope="page"/>
									</c:otherwise>
								</c:choose>
							</c:if>
						</c:if>
						<c:if test="${form.getSurvey().isDelphi && question.getIsDelphiQuestion() && (filter == null || filter.visibleExplanations.contains(question.id.toString()))}">
							<th class="topaligned"><spring:message code="label.Explanation" /></th>
							<th class="topaligned"><spring:message code="label.Likes" /></th>
						</c:if>
						<c:if test="${form.getSurvey().isDelphi && question.getIsDelphiQuestion() && (filter == null || filter.visibleDiscussions.contains(question.id.toString()))}">
							<th class="topaligned"><spring:message code="label.Discussion" /></th>
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
									<button data-toggle="tooltip" title="<spring:message code="label.SortDescending" />" onclick="$('#sort').val('scoreDesc');$('#resultsForm').submit();" class="unstyledbutton"><span class="glyphicon glyphicon-arrow-down" style="color:#FFF"></span></button>
									<button data-toggle="tooltip" title="<spring:message code="label.SortAscending" />" onclick="$('#sort').val('scoreAsc');$('#resultsForm').submit();" class="unstyledbutton"><span class="glyphicon glyphicon-arrow-up" style="color:#FFF"></span></button>
								</div>
								<spring:message code="label.TotalScore" />
							</div>
						</th>
					</c:if>
				</tr>
				
				<tr class="table-styled-filter">
					<c:if test="${publication == null}">
						<c:if test="${publication == null && (sessioninfo.owner == USER.id || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('AccessResults') > 1)}">
							<th class="checkDelete"><span data-toggle="tooltip" data-trigger="hover" data-original-title="<spring:message code="label.SelectAll" />"><input name="check-all-delete" id="check-all-delete" class="check checkDelete" style="margin-bottom: 8px !important;" type="checkbox" onclick="checkAllDelete()" /></span></th>
						</c:if>
						<th>
							<c:if test="${publication == null && (sessioninfo.owner == USER.id || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('AccessResults') > 1)}">
								<a data-toggle="tooltip" title="<spring:message code="label.DeleteAll" />" class="iconbutton disabled" disabled="disabled" id="btnDeleteSelected" onclick="checkAndShowMultiDeleteDialog();"><span class="glyphicon glyphicon-remove"></span></a>
							</c:if>
						</th>
					</c:if>
					<c:forEach items="${form.getSurvey().getQuestions()}" var="question">
						<c:if test="${publication == null || publication.isAllQuestions() || publication.isSelected(question.id)}">
							<c:if test="${filter == null || filter.visibleQuestions.contains(question.id.toString())}">
								<c:choose>
									<c:when test="${filter.getReadOnlyFilterQuestions().contains(question.uniqueId)}">
										<th></th>
									</c:when>
									<c:when test="${question.getType() == 'Image' || question.getType() == 'Text' || question.getType() == 'Download' || question.getType() == 'Confirmation' || question.getType() == 'Ruler'}"></c:when>
									<c:when test="${question.getType() == 'GalleryQuestion' && !question.selection}"></c:when>
									<c:when test="${question.getType() == 'Matrix'}">
										<c:forEach items="${question.questions}" var="matrixQuestion">
											<th class="filtercell cell${matrixQuestion.id}"<c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>>
													<button type="button" class="btn btn-default dropdownFilter" onclick="showOverlayMenu(this)" >
													    <span class="nobreak"><spring:message code="label.AllValues" /></span>
													    <span class="caret"></span>
													  </button>
													  
													  <div class="overlaymenu hideme maxH">
													  	<button type="button" style="margin-bottom: 5px;"   onclick="$('#resultsForm').submit();" class="btn btn-default btn-sm btn-primary"><spring:message code="label.ApplyFilter" /></button>
													  	 <c:forEach items="${question.answers}" var="possibleanswer" varStatus="status">
															<div>
																<c:choose>
																	<c:when test="${filter.contains(matrixQuestion.id, matrixQuestion.uniqueId, possibleanswer.id, possibleanswer.uniqueId) }">
																		<input checked="checked" name="filter${matrixQuestion.id}|${matrixQuestion.uniqueId}" data-stopPropagation="true" type="checkbox" class="check checkFilterCell" value="${possibleanswer.id}|${possibleanswer.uniqueId}" />${possibleanswer.strippedTitle}
																	</c:when>
																	<c:otherwise>
																		<input name="filter${matrixQuestion.id}|${matrixQuestion.uniqueId}" data-stopPropagation="true" type="checkbox" class="check checkFilterCell" value="${possibleanswer.id}|${possibleanswer.uniqueId}" />${possibleanswer.strippedTitle}
																	</c:otherwise>
																</c:choose>
															</div>
														</c:forEach>	
													  </div>
													  
													  <div style="display: inline-block; margin: 0px;">
															<button type="button" class="unstyledbutton" onclick="showOverlayMenu(this)" >
														    	<span class="glyphicon glyphicon-option-vertical"></span>
															</button>
															<div class="resultoverlaymenu overlaymenu hideme">
																<button type="button" class="unstyledbuttonBlack" onclick='clearFilterCellContent(this)'><spring:message code="label.ResetFilter" /></button><br />
																<c:if test="${sessioninfo.owner.equals(USER.id) || USER.formPrivilege == 2 || USER.getLocalPrivilegeValue('AccessResults') == 2}">
                                                 					<a onclick="showDeleteColumnDialog('${question.uniqueId}')"><spring:message code="label.BlankAnswers" /></a>
																</c:if>
														   </div>
													   </div>
											</th>
										</c:forEach>
									</c:when>
									<c:when test="${question.getType() == 'Table'}">									
										<c:forEach var="r" begin="1" end="${question.allRows-1}"> 
											<c:forEach var="c" begin="1" end="${question.allColumns-1}"> 																
												<th class="filtercell cell${question.id}-${r}-${c}"<c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>>
													<input onkeyup="checkFilterCell($(this).closest('.filtercell'), true)" value='<esapi:encodeForHTMLAttribute>${filter.getValue(question.id.toString().concat("-").concat(r.toString()).concat("-").concat(c.toString()), question.uniqueId)}</esapi:encodeForHTMLAttribute>' type="text" class="limitedfilter" style="margin:0px;" name='filter${question.id}-${r}-${c}|${question.uniqueId}' />
											        <div style="display: inline-block; margin: 0px; width: 5px;">
														<button type="button" class="unstyledbutton" onclick="showOverlayMenu(this)" >
													    	<span class="glyphicon glyphicon-option-vertical"></span>
														</button>
														<div class="resultoverlaymenu overlaymenu hideme">
															<button type="button" class="unstyledbuttonBlack" onclick='clearFilterCellContent(this)'><spring:message code="label.ResetFilter" /></><br />
															<c:if test="${sessioninfo.owner.equals(USER.id) || USER.formPrivilege == 2 || USER.getLocalPrivilegeValue('AccessResults') == 2}">
                                                					<a onclick="showDeleteColumnDialog('${question.uniqueId}')"><spring:message code="label.BlankAnswers" /></a>
															</c:if>
													   </div>
												   </div>
		                                        </th>										
											</c:forEach>													
										</c:forEach>
									</c:when>
									<c:when test="${question.getType() == 'RatingQuestion'}">
										<c:forEach items="${question.questions}" var="childQuestion">
											<th class="filtercell cell${childQuestion.id}"<c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>>
													<button type="button" class="btn btn-default dropdownFilter" onclick="showOverlayMenu(this)" >
													    <span class="nobreak"><spring:message code="label.AllValues" /></span>
													    <span class="caret"></span>
													  </button>
													  
													  <div class="overlaymenu hideme maxH">
													  	<button type="button" style="margin-bottom: 5px;" onclick="$('#resultsForm').submit();" class="btn btn-default btn-sm btn-primary"><spring:message code="label.ApplyFilter" /></button>
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
													  
													 <div style="display: inline-block; margin: 0px;">
														<button type="button" class="unstyledbutton" onclick="showOverlayMenu(this)" >
													    	<span class="glyphicon glyphicon-option-vertical"></span>
														</button>
														<div class="resultoverlaymenu overlaymenu hideme">
															<button type="button" class="unstyledbuttonBlack" onclick='clearFilterCellContent(this)'><spring:message code="label.ResetFilter" /></button><br />
															<c:if test="${sessioninfo.owner.equals(USER.id) || USER.formPrivilege == 2 || USER.getLocalPrivilegeValue('AccessResults') == 2}">
                                                					<a onclick="showDeleteColumnDialog('${question.uniqueId}')"><spring:message code="label.BlankAnswers" /></a>
															</c:if>
													   </div>
												   </div>
											</th>
										</c:forEach>
									</c:when>
									<c:when test="${question.getType() == 'Upload' && publication != null && !publication.getShowUploadedDocuments()}">
									
									</c:when>
									<c:when test="${question.getType() == 'ComplexTable'}">
										<c:forEach items="${question.getQuestionChildElements()}" var="child">
											<th class="filtercell cell${child.id}"<c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>>
												<c:choose>
													<c:when test="${child.getCellType() == 'SingleChoice' || child.getCellType() == 'MultipleChoice'}">
														  <button type="button" class="btn btn-default dropdownFilter" onclick="showOverlayMenu(this)" >
														    <span class="nobreak"><spring:message code="label.AllValues" /></span>
														    <span class="caret"></span>
														  </button>
														  
														  <div class="overlaymenu hideme maxH">
														  	<button type="button" style="margin-bottom: 5px;" onclick="$('#resultsForm').submit();" class="btn btn-default btn-sm btn-primary"><spring:message code="label.ApplyFilter" /></button>
														  	 <c:forEach items="${child.possibleAnswers}" var="possibleanswer" varStatus="status">
														    	<div>
															    	<c:choose>
																		<c:when test="${filter.contains(child.id, child.uniqueId, possibleanswer.id, possibleanswer.uniqueId) }">
																			<input checked="checked" name="filter${child.id}|${child.uniqueId}" data-stopPropagation="true" type="checkbox" class="check checkFilterCell" value="${possibleanswer.id}|${possibleanswer.uniqueId}">${possibleanswer.strippedTitle}</input>
																		</c:when>
																		<c:otherwise>
																			<input name="filter${child.id}|${child.uniqueId}" data-stopPropagation="true" type="checkbox" class="check checkFilterCell" value="${possibleanswer.id}|${possibleanswer.uniqueId}">${possibleanswer.strippedTitle}</input>
																		</c:otherwise>
																	</c:choose>
														    	</div>
															</c:forEach>
														  </div>
														  
														  <div style="display: inline-block; margin: 0px;">
																<button type="button" class="unstyledbutton" onclick="showOverlayMenu(this)" >
															    	<span class="glyphicon glyphicon-option-vertical"></span>
																</button>
																<div class="resultoverlaymenu overlaymenu hideme">
																	<button type="button" class="unstyledbuttonBlack" onclick='clearFilterCellContent(this)'><spring:message code="label.ResetFilter" /></button><br />
																	<c:if test="${sessioninfo.owner.equals(USER.id) || USER.formPrivilege == 2 || USER.getLocalPrivilegeValue('AccessResults') == 2}">
	                                                 					<a onclick="showDeleteColumnDialog('${child.uniqueId}')"><spring:message code="label.BlankAnswers" /></a>
																	</c:if>
															   </div>
														   </div>
													</c:when>
													<c:otherwise>
														<input onkeyup="checkFilterCell($(this).closest('.filtercell'), true)" value='<esapi:encodeForHTMLAttribute>${filter.getValue(child.id, child.uniqueId)}</esapi:encodeForHTMLAttribute>' type="text" maxlength="100" class="${child.getCellType() == 'Number' || child.getCellType() == 'Formula' ? 'filter' : 'limitedfilter'}" style="margin:0px;" name="filter${child.id}|${child.uniqueId}" />
														<div style="display: inline-block; margin: 0px; width: 5px;">
															<button type="button" class="unstyledbutton"  onclick="showOverlayMenu(this)" >
														    	<span class="glyphicon glyphicon-option-vertical"></span>
															</button>
															<div class="resultoverlaymenu overlaymenu hideme">
																<button type="button" class="unstyledbuttonBlack" onclick='clearFilterCellContent(this)'><spring:message code="label.ResetFilter" /></button><br />
																<c:if test="${sessioninfo.owner.equals(USER.id) || USER.formPrivilege == 2 || USER.getLocalPrivilegeValue('AccessResults') == 2}">
	                                                					<a onclick="showDeleteColumnDialog('${child.uniqueId}')"><spring:message code="label.BlankAnswers" /></a>
																</c:if>
														   </div>
													   </div>
													</c:otherwise>
												</c:choose>
											</th>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<th class="filtercell cell${question.id}"<c:if test="${filter.visible(question.id.toString()) == false}">style="display: none;"</c:if>>
											<c:choose>
												<c:when test="${question.getType() == 'GalleryQuestion'}">
													  <button type="button" class="btn btn-default dropdownFilter" onclick="showOverlayMenu(this)" >
													    <span class="nobreak"><spring:message code="label.AllValues" /></span>
													    <span class="caret"></span>
													  </button>
													  
													  <div class="overlaymenu hideme maxH">
													  	<button type="button"  style="margin-bottom: 5px;" onclick="$('#resultsForm').submit();" class="btn btn-default btn-sm btn-primary"><spring:message code="label.ApplyFilter" /></button>
													  	 <c:forEach items="${question.allFiles}" var="file" varStatus="status">
													    	<div>
														    	<c:choose>
																	<c:when test="${filter.contains(question.id, question.uniqueId, file.uid) }">
																		<input checked="checked" name="filter${question.id}|${question.uniqueId}" data-stopPropagation="true" type="checkbox" class="check checkFilterCell" value="${file.uid}"><esapi:encodeForHTML>${file.name}</esapi:encodeForHTML></input>
																	</c:when>
																	<c:otherwise>
																		<input name="filter${question.id}|${question.uniqueId}" data-stopPropagation="true" type="checkbox" class="check checkFilterCell" value="${file.uid}"><esapi:encodeForHTML>${file.name}</esapi:encodeForHTML></input>
																	</c:otherwise>
																</c:choose>
													    	</div>
														</c:forEach>
													  </div>
													  														  
													  <div style="display: inline-block; margin: 0px;">
														<button type="button" class="unstyledbutton" onclick="showOverlayMenu(this)" >
													    	<span class="glyphicon glyphicon-option-vertical"></span>
														</button>
														<div class="resultoverlaymenu overlaymenu hideme">
															<button type="button" class="unstyledbuttonBlack" onclick='clearFilterCellContent(this)'><spring:message code="label.ResetFilter" /></button><br />
															<c:if test="${sessioninfo.owner.equals(USER.id) || USER.formPrivilege == 2 || USER.getLocalPrivilegeValue('AccessResults') == 2}">
                                                					<a onclick="showDeleteColumnDialog('${question.uniqueId}')"><spring:message code="label.BlankAnswers" /></a>
															</c:if>
													   </div>
													  </div>
												</c:when>
												<c:when test="${question.getType() == 'Upload'}">
													<div style="float: right">
														<div style="display: inline-block; margin: 0px; margin-bottom: 8px;">
															<c:if test="${sessioninfo.owner.equals(USER.id) || USER.formPrivilege == 2 || USER.getLocalPrivilegeValue('AccessResults') == 2}">
															<button type="button" class="unstyledbutton" onclick="showOverlayMenu(this)" >
														    	<span class="glyphicon glyphicon-option-vertical"></span>
															</button>
															<div class="resultoverlaymenu overlaymenu hideme">
																<a onclick="showDeleteColumnDialog('${question.uniqueId}')"><spring:message code="label.BlankAnswers" /></a>
														   </div>
															</c:if>
													   </div>		
													</div>
													
													<c:if test="${(sessioninfo.owner == USER.id || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('AccessResults') > 1 || USER.getResultAccessWrite() || (form.survey.isDraft && USER.getLocalPrivilegeValue('AccessDraft') > 0) || (publication != null && publication.getShowUploadedDocuments())) && questionswithuploadedfiles != null && questionswithuploadedfiles.contains(question.uniqueId)}">
														<button type="button" class="unstyledbutton" onclick="showExportDialog('Files${question.uniqueId}${form.survey.isDraft}', 'zip');"  data-toggle="tooltip" data-placement="top" title="<spring:message code="label.DownloadAllFiles" />"><span class="glyphicon glyphicon-save"></span></button>
													</c:if>		
													
													<div style="clear: both"></div>				
												</c:when>
												<c:when test="${question.getType() == 'MultipleChoiceQuestion' || question.getType() == 'SingleChoiceQuestion'}">
													  <button type="button" class="btn btn-default dropdownFilter" onclick="showOverlayMenu(this)" >
													    <span class="nobreak"><spring:message code="label.AllValues" /></span>
													    <span class="caret"></span>
													  </button>
													  
													  <div class="overlaymenu hideme maxH">
													  	<button type="button" style="margin-bottom: 5px;"   onclick="$('#resultsForm').submit();" class="btn btn-default btn-sm btn-primary"><spring:message code="label.ApplyFilter" /></button>
													  	<c:if test="${question.getType() == 'SingleChoiceQuestion' && question.getIsTargetDatasetQuestion() }">
															<c:forEach items="${targetdatasets}" var="dataset" varStatus="status">
																<div>
															    	<c:choose>
																		<c:when test="${filter.containsTargetDataset(question.uniqueId, dataset.id) }">
																			<input checked="checked" name="filter${question.id}|${question.uniqueId}" data-stopPropagation="true" type="checkbox" class="check checkFilterCell" value="${question.uniqueId}-${dataset.id}">${dataset.name}</input>
																		</c:when>
																		<c:otherwise>
																			<input name="filter${question.id}|${question.uniqueId}" data-stopPropagation="true" type="checkbox" class="check checkFilterCell" value="${question.uniqueId}-${dataset.id}">${dataset.name}</input>
																		</c:otherwise>
																	</c:choose>
														    	</div>
															</c:forEach>
														</c:if>
													  	
													  	
													  	 <c:forEach items="${question.allPossibleAnswers}" var="possibleanswer" varStatus="status">
													    	<div>
														    	<c:choose>
																	<c:when test="${filter.contains(question.id, question.uniqueId, possibleanswer.id, possibleanswer.uniqueId) }">
																		<input checked="checked" name="filter${question.id}|${question.uniqueId}" data-stopPropagation="true" type="checkbox" class="check checkFilterCell" value="${possibleanswer.id}|${possibleanswer.uniqueId}">${possibleanswer.strippedTitle}</input>
																	</c:when>
																	<c:otherwise>
																		<input name="filter${question.id}|${question.uniqueId}" data-stopPropagation="true" type="checkbox" class="check checkFilterCell" value="${possibleanswer.id}|${possibleanswer.uniqueId}">${possibleanswer.strippedTitle}</input>
																	</c:otherwise>
																</c:choose>
													    	</div>
														</c:forEach>
													  </div>
													  
													  <div style="display: inline-block; margin: 0px;">
															<button type="button" class="unstyledbutton" onclick="showOverlayMenu(this)" >
														    	<span class="glyphicon glyphicon-option-vertical"></span>
															</button>
															<div class="resultoverlaymenu overlaymenu hideme">
																<button type="button" class="unstyledbuttonBlack" onclick='clearFilterCellContent(this)'><spring:message code="label.ResetFilter" /></button><br />
																<c:if test="${sessioninfo.owner.equals(USER.id) || USER.formPrivilege == 2 || USER.getLocalPrivilegeValue('AccessResults') == 2}">
                                                 					<a onclick="showDeleteColumnDialog('${question.uniqueId}')"><spring:message code="label.BlankAnswers" /></a>
																</c:if>
														   </div>
													   </div>
												</c:when>
												<c:when test="${question.getType() == 'RankingQuestion'}">
													  <button type="button" class="btn btn-default dropdownFilter" onclick="showOverlayMenu(this)" >
													    <span class="nobreak"><spring:message code="label.AllValues" /></span>
													    <span class="caret"></span>
													  </button>
													  
													  <div class="overlaymenu hideme maxH">
													  	<button type="button" style="margin-bottom: 5px;"   onclick="$('#resultsForm').submit();" class="btn btn-default btn-sm btn-primary"><spring:message code="label.ApplyFilter" /></button><br />
													  	<i><spring:message code="label.FilterTopRankedItem" /></i>
													  	 <c:forEach items="${question.childElements}" var="child" varStatus="status">
													    	<div>
														    	<c:choose>
																	<c:when test="${filter.contains(question.id, question.uniqueId, child.id, child.uniqueId) }">
																		<input checked="checked" name="filter${question.id}|${question.uniqueId}" data-stopPropagation="true" type="radio" class="check checkFilterCell" value="${child.uniqueId}" onkeyup="singleKeyUp(event, this)" onclick="singleClick(this);" >${child.getStrippedTitle()}</input>
																	</c:when>
																	<c:otherwise>
																		<input name="filter${question.id}|${question.uniqueId}" data-stopPropagation="true" type="radio" class="check checkFilterCell" value="${child.uniqueId}" onkeyup="singleKeyUp(event, this)" onclick="singleClick(this);" >${child.getStrippedTitle()}</input>
																	</c:otherwise>
																</c:choose>
													    	</div>
														</c:forEach>
													  </div>
													  
													  <div style="display: inline-block; margin: 0px;">
															<button type="button" class="unstyledbutton" onclick="showOverlayMenu(this)" >
														    	<span class="glyphicon glyphicon-option-vertical"></span>
															</button>
															<div class="resultoverlaymenu overlaymenu hideme">
																<button type="button" class="unstyledbuttonBlack" onclick='clearFilterCellContent(this)'><spring:message code="label.ResetFilter" /></button><br />
																<c:if test="${sessioninfo.owner.equals(USER.id) || USER.formPrivilege == 2 || USER.getLocalPrivilegeValue('AccessResults') == 2}">
                                                 					<a onclick="showDeleteColumnDialog('${question.uniqueId}')"><spring:message code="label.BlankAnswers" /></a>
																</c:if>
														   </div>
													   </div>
												</c:when>
												<c:when test="${question.getType() == 'DateQuestion'}">
													<div class="btn-toolbar" style="margin: 0px; text-align: center; display: inline-block; vertical-align: middle;">
														<div class="datefilter" style="float: left">
												  		<button type="button" class="btn btn-default" onclick="showOverlayMenu(this)" >
														     <c:choose>
														     	<c:when test="${filter.getFromValue(question.id, question.uniqueId).length() > 0}">
														     		<spring:eval expression="filter.getFromValue(question.id, question.uniqueId)" />
														     	</c:when>
														     	<c:otherwise>
														     		<spring:message code="label.from" />
														     	</c:otherwise>
														     </c:choose>
														    <span class="caret"></span>
														  </button>
														  <div class="overlaymenu hideme">
														    	<input type="hidden" name="filter${question.id}|${question.uniqueId}from" class="hiddendate" value="<spring:eval expression="filter.getFromValue(question.id, question.uniqueId)" />" />
														    	<div id="metafilter${question.id}|${question.uniqueId}fromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker results"></div>
														   </div>
														</div>
														<div class="datefilter" style="float: left">	
														  <button type="button" class="btn btn-default" onclick="showOverlayMenu(this)" >
														  	<c:choose>
														     	<c:when test="${filter.getToValue(question.id, question.uniqueId).length() > 0}">
														     		<spring:eval expression="filter.getToValue(question.id, question.uniqueId)" />
														     	</c:when>
														     	<c:otherwise>
														     		<spring:message code="label.To" />
														     	</c:otherwise>
														     </c:choose>
														    <span class="caret"></span>
														  </button>
														 <div class="overlaymenu hideme">
														    	<input type="hidden" name="filter${question.id}|${question.uniqueId}to" class="hiddendate" value="<spring:eval expression="filter.getToValue(question.id, question.uniqueId)" />" />
														    	<div id="metafilter${question.id}|${question.uniqueId}todiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker results"></div>
														    </div>
														</div>	
													</div>	
													<div style="display: inline-block; margin: 0px; vertical-align: middle;">
														<button type="button" class="unstyledbutton" onclick="showOverlayMenu(this)" >
													    	<span class="glyphicon glyphicon-option-vertical"></span>
														</button>
														<div class="resultoverlaymenu overlaymenu hideme">
															<button type="button" class="unstyledbuttonBlack" onclick='clearFilterCellContent(this)'><spring:message code="label.ResetFilter" /></button><br />
															<c:if test="${sessioninfo.owner.equals(USER.id) || USER.formPrivilege == 2 || USER.getLocalPrivilegeValue('AccessResults') == 2}">
                                                					<a onclick="showDeleteColumnDialog('${question.uniqueId}')"><spring:message code="label.BlankAnswers" /></a>
															</c:if>
													   </div>
												   </div>
												</c:when>													
												<c:otherwise>
													<input onkeyup="checkFilterCell($(this).closest('.filtercell'), true)" value='<esapi:encodeForHTMLAttribute>${filter.getValue(question.id, question.uniqueId)}</esapi:encodeForHTMLAttribute>' type="text" maxlength="100" class="${question.type == 'NumberQuestion' || question.type == 'FormulaQuestion' ? 'filter' : 'limitedfilter'}" style="margin:0px;" name="filter${question.id}|${question.uniqueId}" />
													<div style="display: inline-block; margin: 0px; width: 5px;">
														<button type="button" class="unstyledbutton" onclick="showOverlayMenu(this)" >
													    	<span class="glyphicon glyphicon-option-vertical"></span>
														</button>
														<div class="resultoverlaymenu overlaymenu hideme">
															<button type="button" class="unstyledbuttonBlack" onclick='clearFilterCellContent(this)'><spring:message code="label.ResetFilter" /></button><br />
															<c:if test="${sessioninfo.owner.equals(USER.id) || USER.formPrivilege == 2 || USER.getLocalPrivilegeValue('AccessResults') == 2}">
                                                					<a onclick="showDeleteColumnDialog('${question.uniqueId}')"><spring:message code="label.BlankAnswers" /></a>
															</c:if>
													   </div>
												   </div>
                                               </c:otherwise>
											</c:choose>
										</th>
									</c:otherwise>
								</c:choose>		
								
								<c:if test="${form.getSurvey().isDelphi && question.getIsDelphiQuestion() && (filter == null || filter.visibleExplanations.contains(question.id.toString()))}">
									<th class="filtercell"></th>
									<th class="filtercell"></th>
								</c:if>
								<c:if test="${form.getSurvey().isDelphi && question.getIsDelphiQuestion() && (filter == null || filter.visibleDiscussions.contains(question.id.toString()))}">
									<th class="filtercell"></th>
								</c:if>
								
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
										<input onkeyup="checkFilterCell($(this).closest('.filtercell'), true)" type="text" maxlength="100" style="margin:0px;" value='<esapi:encodeForHTMLAttribute>${filter.invitation}</esapi:encodeForHTMLAttribute>' name="metafilterinvitation" />
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
										<input onkeyup="checkFilterCell($(this).closest('.filtercell'), true)" type="text" maxlength="100" style="margin:0px;" value='<esapi:encodeForHTMLAttribute>${filter.caseId}</esapi:encodeForHTMLAttribute>' name="metafiltercase" />
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
										<input onkeyup="checkFilterCell($(this).closest('.filtercell'), true)" type="text" maxlength="100" style="margin:0px;" value='<esapi:encodeForHTMLAttribute>${filter.user}</esapi:encodeForHTMLAttribute>' name="metafilteruser" />
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
									    	<div id="metafilterdatefromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker results"></div>
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
									    	<div id="metafilterdatetodiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker results"></div>
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
									    	<div id="metafilterupdatefromdiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker results"></div>
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
									    	<div id="metafilterupdatetodiv" data-stopPropagation="true" style="margin:0px; width:auto;" class="datepicker results"></div>
									   </div>
									</div>	
								</div>										
							</th>
						</c:if>
						<c:if test='${filter.visible("languages") == true}'>
							<th class="filtercell celllanguages">
									<button type="button" class="btn btn-default dropdownFilter" onclick="showOverlayMenu(this)" >
									    <span class="nobreak"><spring:message code="label.AllValues" /></span>
									    <span class="caret"></span>
									  </button>
									  
									  <div class="overlaymenu hideme">
									  	<button type="button" style="margin-bottom: 5px;"   onclick="$('#resultsForm').submit();" class="btn btn-default btn-sm btn-primary"><spring:message code="label.ApplyFilter" /></button>
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
				
			</thead>
			</table>
		</div>
		<div id="scrollarea" class="scrollarea" tabindex="-1">
			<div id="tbllist-empty" class="noDataPlaceHolder" style="display:none;">
				<p>
					<spring:message code="label.NoDataContributionText"/>&nbsp;<img src="${contextpath}/resources/images/icons/32/forbidden_grey.png" alt="no data"/>
				<p>
			</div>
			<div id="tbllist-loading" class="noDataPlaceHolder" style="display:block;">
				<p>
					<spring:message code="label.Loading"/>...&nbsp;
				<p>
			</div>
		<table id="contentstable2" class="table table-bordered table-striped">
		<tbody id="contentstablebody">
			
		</tbody>
	</table>
	</div>
			
	<div id="wheel" class="hideme" style="text-align: center">
		<img  style="margin-top: 10px;" src="${contextpath}/resources/images/ajax-loader.gif" />
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
	
	input[type="text"].filter,
	input[type="text"].limitedfilter {
		max-width: 135px;
		font-size: 90%;
	}
	
	.resultoverlaymenu {
		text-align: left;
	}
	
	.resultoverlaymenu a {
		text-decoration: none;
		color: #777;
	}
	
	#contentstablebody .comment {
		margin-bottom: 5px;
	}
	
	#contentstablebody .reply {
		margin-left: 20px;
		margin-bottom: 5px;
	}

	#contentstablebody .comment > a:first-of-type::before,
	#contentstablebody .reply > a:first-of-type::before {
		content: '\A';
		white-space: pre;
	}

	#contentstablebody .comment a:first-of-type,
	#contentstablebody .reply a:first-of-type {
		margin-right: 8px;
	}

	/* Dummy class used as element selector */
	#contentstable .question-title {
	}


</style>

<script type="text/javascript"> 
	var scrollTimeout = null;
	var closeOverlayDivsEnabled = false;

	$(".question-title").on('click', function() {
		$(".question-title").each(function() {
			if($(this).hasClass("nowrap-ellipsis-title")) {
				$(this).removeClass("nowrap-ellipsis-title");
			} else {
				$(this).addClass("nowrap-ellipsis-title");
			}
		});
		adaptScrollArea();
	});

	$(".question-title").each(function() {
		$(this).addClass("nowrap-ellipsis-title");
	});

	adaptScrollArea();


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
						if ($(this).hasClass("limitedfilter") && $(this).val().length > 0 && $(this).val().length < 3)
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

 			  $(window).scroll(function() {
				   $(".overlaymenu").hide();
			   });
 			  $(window).resize(function() {
 				  //$(".overlaymenu").hide();
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
				    checkFilterCell($(this).closest('.filtercell'), true);
					checkNoBreaks();
				    event.stopPropagation();
				});
			 
			 $('.filtercell input[type=text]').keypress(function(event){
				    var keycode = (event.keyCode ? event.keyCode : event.which);
				    if(keycode == '13'){
				    	$('#resultsForm').submit();
				    }
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
			 
			if ($('#scrollarea').hasScrollBar())
			{
				$("#scrollareaheader").css("overflow-y","scroll");
			} else {
				$("#scrollareaheader").css("overflow-y","auto");
			}
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
					$(this).find(".answercell").css("width", newWidthNoPadding + "px");
					$(this).find(".answercell").attr("tabindex", "-1");
					$(this).find(".headertitle").css("width", newWidthNoPadding + "px");
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
				var height = $( window ).height() - $('#scrollarea').offset().top - 10; //530;
				var statheight = $( window ).height() - 580;
				</c:when>
				<c:otherwise>
				var height = $( window ).height() - $('#scrollarea').offset().top - 10;
				var statheight = $( window ).height() - 480;
				</c:otherwise>
			</c:choose>

			 if (height < 200) height = 200;
			 $('#scrollarea').css("height", height);
			 
			 //$('#scrollareastatistics').css("height", statheight);
			 //$('#scrollareastatisticsquiz').css("height", height-50);
			 
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
		
		var newPage = 1;
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
			
			var rows = 40;

			const thisPage = newPage++
			
			var s = "page=" + thisPage + "&rows=" + rows;
			
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

				  error: function(jqXHR) {
                  	 showError('<spring:message code="error.unexpected" />');
                  	 endreached = true;
                     inloading = false;
					 $("#tbllist-loading").hide();
					 $("#tbllist-empty").show();
                     $( "#wheel" ).hide();
                  },

				  success: function( list ) {
				  
					  if (list.length <= 1)
					  {
						  endreached = true;
						  inloading = false;
						  $("#tbllist-loading").hide();
						  if (thisPage === 1) {
							  $("#tbllist-empty").show();
						  } else {
							  $("#tbllist-empty").hide();
						  }
						  $( "#wheel" ).hide();
						  return;
					  }

					  if ('${resultType}' == 'content')
						  $("#scrollareaheader").css({ 'overflow-x' : 'hidden'});
					  
					  $(".deactivatedexports").hide();
					  $(".activatedexports").show();					  
					  
					  var trm = null;
					  
					  if (list[0].length > 0)
					  {
						  $("#lastupdate").html('<spring:message code="info.lastResultsUpdate" />' + list[0] + ' <a class="iconbutton" target="_blank" href="${contextpath}/home/helpauthors#_Toc9-11"><span class="glyphicon glyphicon-info-sign"></span></a>');
					  } else {
						  $("#lastupdate").html("");
					  }
					  
					  var i = 1;
					  
					  while (i < list.length)
					  {
						 var tr = document.createElement("tr");
						 
						 <c:choose>
							 <c:when test="${publication == null &&
							 (sessioninfo.owner == USER.id || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('AccessResults') > 1 || (active eq false && USER.getLocalPrivilegeValue('AccessDraft') > 1) || (USER.getResultAccess() != null && !USER.getResultAccess().isReadonly()))}">
							 
							 	var td = document.createElement("td");
							 	$(td).addClass("checkDelete").css("min-width","13px");
							 	var inp = document.createElement("input");
							 	$(inp).attr("name","delete" + list[i]).addClass("check").addClass("checkDelete").attr("type","checkbox").attr("onclick","checkDelete(this)");
							 	$(td).append(inp);
							 	$(tr).append(td);
							 	
							 	if ($("#show-delete-checkboxes").is(":checked"))
								{
									//visible
									if ($("#check-all-delete").is(":checked"))
									{
										$(inp).attr("checked", "checked");
									}
								} else {
									$(td).addClass("hiddenTableCell");
								}
							 
								td = document.createElement("td");
								var a = document.createElement("a");
								$(a).attr("data-toggle", "tooltip").attr("rel","tooltip").attr("title",'<spring:message code="label.Delete" />').addClass("iconbutton").attr("onclick","showDeleteContributionDialog('" + list[i] + "')").append('<span class="glyphicon glyphicon-remove"></span>');
								$(td).css("max-width","150px").append(a);
								$(td).append("&nbsp;");
								a = document.createElement("a");
								$(a).attr("data-toggle", "tooltip").attr("rel","tooltip").attr("title",'<spring:message code="label.Edit" />').addClass("iconbutton").attr("target", "_blank").attr("href","<c:url value='/editcontribution/'/>" + list[i] + "?mode=dialog" + (${active eq false} ? "&results-source=draft": "")).append('<span class="glyphicon glyphicon-pencil"></span>');
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
									//visible
									if ($("#check-all-delete").is(":checked"))
									{
										$(inp).attr("checked", "checked");
									}
								} else {
									$(td).addClass("hiddenTableCell");
								}
							 								 	
								td = document.createElement("td");
								var a = document.createElement("a");
								$(a).attr("data-toggle", "tooltip").attr("rel","tooltip").attr("title",'<spring:message code="label.Delete" />').addClass("iconbutton disabled").append('<span class="glyphicon glyphicon-remove"></span>');
								$(td).append(a);
								$(td).append("&nbsp;");
								a = document.createElement("a");
								$(a).attr("data-toggle", "tooltip").attr("rel","tooltip").attr("title",'<spring:message code="label.Edit" />').addClass("iconbutton disabled").append('<span class="glyphicon glyphicon-pencil"></span>');
								$(td).append(a);
								$(td).append("&nbsp;");
								a = document.createElement("a");
								$(a).attr("data-toggle", "tooltip").attr("rel","tooltip").attr("title",'<spring:message code="label.Print" />').addClass("iconbutton").attr("target","_blank").attr("href","<c:url value='/printcontribution/'/>?code=" + list[i] + "").append('<span class="glyphicon glyphicon-print"></span>');
								$(td).append(a);
								$(td).append("&nbsp;");
								a = document.createElement("a");
								$(a).attr("data-toggle", "tooltip").attr("rel","tooltip").attr("title",'<spring:message code="label.DownloadPDF" />').attr("target","_blank").attr("onclick",'downloadAnswerPDF("' + list[i] + '")').append('<img src="${contextpath}/resources/images/file_extension_pdf_small.png">');
								$(td).append(a);
								$(tr).append(td);
							</c:when>
						</c:choose>
						
						i++; //0 is unique code
						i++; //1 is id
						
						<c:forEach items="${form.getSurvey().getQuestions()}" var="question">
						
							<c:if test="${publication == null || publication.isAllQuestions() || publication.isSelected(question.id)}">
								<c:if test="${filter == null || filter.visibleQuestions.contains(question.id.toString())}">
									<c:if test="${question.getType() != 'Image' && question.getType() != 'Text' && question.getType() != 'Download' && question.getType() != 'Ruler' && question.getType() != 'Confirmation'}">
										<c:choose>
											<c:when test="${question.getType() == 'Matrix'}">
												<c:forEach items="${question.questions}" var="matrixQuestion">
												
													var td = document.createElement("td");
													var div = document.createElement("div");
													$(div).addClass("answercell maxtrixcell");
													$(div).append(list[i++]);
													$(td).append(div);
													$(tr).append(td);
												
												</c:forEach>	
											</c:when>
											
											<c:when test="${question.getType() == 'Table'}">
												<c:forEach var="r" begin="1" end="${question.allRows-1}"> 
													<c:forEach var="c" begin="1" end="${question.allColumns-1}"> 
													
														var td = document.createElement("td");
														var div = document.createElement("div");
														$(div).addClass("answercell tablecell");
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
												<c:forEach items="${question.questions}" var="childQuestion">
												
													var td = document.createElement("td");
													var div = document.createElement("div");
													$(div).addClass("answercell");
													$(div).append(list[i++]);
													$(td).append(div);
													$(tr).append(td);
												
												</c:forEach>	
											</c:when>
											
											<c:when test="${question.getType() == 'ComplexTable'}">
												<c:forEach items="${question.questionChildElements}" var="child">
												
													var td = document.createElement("td");
													var div = document.createElement("div");
													$(div).addClass("answercell complextablecell");
													
													$(div).append(list[i++]);
													$(td).append(div);
													$(tr).append(td);
												
												</c:forEach>	
											</c:when>

						  					<c:when test="${question.getType() == 'GalleryQuestion' && !question.selection}"></c:when>
					 						
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
										
										<c:if test="${form.survey.isDelphi && question.getIsDelphiQuestion() && (filter == null || filter.visibleExplanations.contains(question.id.toString()))}">
											var td = document.createElement("td");
											td.style["overflow-wrap"] = "anywhere";
											$(td).append(list[i++].stripStyleHtml());
											$(tr).append(td);

						  					td = document.createElement("td");
											td.style["overflow-wrap"] = "anywhere";
											$(td).append(list[i++]);
											$(tr).append(td);
										</c:if>
										
										<c:if test="${form.survey.isDelphi && question.getIsDelphiQuestion() && (filter == null || filter.visibleDiscussions.contains(question.id.toString()))}">
											var td = document.createElement("td");
						  					td.style["overflow-wrap"] = "anywhere";
											$(td).append(list[i++]);
											$(tr).append(td);
										</c:if>
											
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

						$("#tbllist-loading").hide();
						$( "#contentstablebody").append(tr);
					  }

					  <c:if test="${publication == null && (sessioninfo.owner == USER.id || USER.formPrivilege > 1 || USER.getLocalPrivilegeValue('AccessResults') > 1)}">
					  	addDelphiCommentActions();
					  </c:if>
					  
					  inloading = false;
					  $( "#wheel" ).hide();		
					  
					  if ($('#scrollarea').hasScrollBar())
						{
							$("#scrollareaheader").css("overflow-y","scroll");
						} else {
							$("#scrollareaheader").css("overflow-y","auto");
						}
					  
					  <c:if test="${publication == null}">
					  checkAssignedValues();
					  </c:if>
					  synchronizeTableSizes();
					  
					  $('[data-toggle="tooltip"]').tooltip({
						    trigger : 'hover'
					 });
				}});
		}

		function addDelphiCommentActions() {

			const deletedCommentText = '[DELETED]';

			$('.comment').each((index, element) => {
				const text = extractTextFromComment($(element).find('span').find('span').first().text());
				const element_to_append = $(element).find('span').find('div').find('span').first();

				if (text !== deletedCommentText) {
					$(element_to_append).before('<a onclick="editDelphiComment(this)"><spring:message code="label.Edit" /></a>');
				}
				if (text !== deletedCommentText
					|| (text === deletedCommentText
						&& $(element).next().length !== 0
						&& !$(element).next().hasClass('reply'))) {
					$(element_to_append).before('<a onclick="deleteDelphiComment(this)"><spring:message code="label.Delete" /></a>&nbsp;');
				}
				const element_to_append2 = $(element_to_append).find('span');
				$(element_to_append2).before('<img aria-label="${form.getMessage("label.Likes")}" style="vertical-align: top; margin-left: 5px" src="${contextpath}/resources/images/hand-thumbs-up.svg" />');
			});

			$('.reply').each((index, element) => {
				$(element).append('<a onclick="editDelphiComment(this)"><spring:message code="label.Edit" /></a>');
				$(element).append('<a onclick="deleteDelphiComment(this)"><spring:message code="label.Delete" /></a>');
			});
		}

		function extractTextFromComment(comment) {
			// Format: User 1: Text
			const colonIndex = comment.indexOf(':');
			
			if (colonIndex < 0) {
				return comment;
			}
			
			return comment.substring(colonIndex + 2);
		}

		function deleteDelphiComment(button) {

			const container = $(button).parent();
			const commentId = $(container).attr("data-id");
			const answerSetUniqueCode = $(container).attr("data-unique-code");

			$("#show-wait-image").modal("show");

			$.ajax({
				type: "POST",
				url: contextpath + "/runner/deleteDelphiComment/" + encodeURIComponent(commentId),
				data: "uniqueCode=" + answerSetUniqueCode + "&formManager=1",
				beforeSend: (xhr) => { xhr.setRequestHeader(csrfheader, csrftoken); },
				error: () => {
					$("#show-wait-image").modal("hide");
					showError('<spring:message code="error.DelphiCommentCouldNotBeDeleted" />');
				},
				success: () => {
					if ($(container).hasClass("comment")
						&& $(container).next().length !== 0
						&& $(container).next().hasClass('reply')) {
						window.location.reload(true);
					} else {
						if ($(container).hasClass("reply")
							&& $(container).prev().hasClass('comment')
							&& $(container).prev().find('a').length === 0
							&& ($(container).next().length === 0 || $(container).next().hasClass('comment'))) {
							$(container).prev().append(
								'<a onclick="deleteDelphiComment(this)"><spring:message code="label.Delete" /></a>');
						}
						$("#show-wait-image").modal("hide");
						$(container).remove();
					}
				}
			});
		}

		function editDelphiComment(button) {
			
			$('.delphi-comment__cancel').each(function() {
				if ($(this).is(":visible")) {
					$(this).click();
				}
			});

			const container = $(button).parent();
			const textElement = $(container).find('span');
			const text = extractTextFromComment($(textElement).text());
			const actionButtons = $(container).find('a');

			$(textElement).hide();
			$(actionButtons).hide();

			const div = document.createElement('div');
			$(div).addClass('delphi-change-comment');
			$(div).append('<textarea class="form-control">' + text + '</textarea>');
			$(div).append('<a class="btn btn-xs btn-primary" onclick="saveChangedDelphiComment(this)">'
				+ '<spring:message code="label.Save" /></a>');
			$(div).append('<a class="btn btn-xs btn-default delphi-comment__cancel" onclick="cancelEditingDelphiComment(this)">'
				+ '<spring:message code="label.Cancel" /></a>');
			$(container).append(div);
		}

		function cancelEditingDelphiComment(button) {

			$(button).parent().parent().find('span').show();
			$(button).parent().parent().find('a').show();
			$(button).parent().remove();
		}

		function saveChangedDelphiComment(button) {

			const container = $(button).parent().parent();
			const commentId = $(container).attr("data-id");
			const answerSetUniqueCode = $(container).attr("data-unique-code");

			const textElement = $(container).find('textarea');
			const text = $(textElement).val();

			$("#show-wait-image").modal("show");

			$.ajax({
				type: "POST",
				url: contextpath + "/runner/editDelphiComment/" + encodeURIComponent(commentId),
				data: "text=" + encodeURIComponent(text) + "&uniqueCode=" + answerSetUniqueCode + "&formManager=1",
				beforeSend: (xhr) => { xhr.setRequestHeader(csrfheader, csrftoken); },
				error: () => {
					$("#show-wait-image").modal("hide");
					showError('<spring:message code="error.DelphiCommentCouldNotBeChanged" />');
				},
				success: () => { window.location.reload(true); }
			});
		}

		function singleClick(r) {
			var previousValue = $(r).attr('previousValue');
			var name = $(r).attr('name');

			if (previousValue == 'checked') {
				$(r).removeAttr('checked');
				$(r).attr('previousValue', false);
			} else {
				$("input[name="+name+"]:radio").attr('previousValue', false);
				$(r).attr('previousValue', 'checked');
			}
		}

		function singleKeyUp(event, target) {
			if (event.code === "Space" && $(target).is(":checked")) {
				$(target).attr("previousValue", "checked").removeAttr("checked");
			}
		}

		function adaptTableToFilter() {
			var scroll = $("#scrollareaheader").scrollLeft();
			$("#scrollarea").scrollLeft(scroll);
		}

		document.addEventListener('focus', function(e) {
			var potentialFilterCell = $(e.target).closest("th");
			if (potentialFilterCell.length == 0)
				return;

			if (potentialFilterCell.attr("class").includes('filtercell')) {
				adaptTableToFilter();
			}
		}, true);

</script>
		
