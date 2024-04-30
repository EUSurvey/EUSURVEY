<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>

<div class="loader" data-bind="style: { display: delphiTableLoading() ? 'flex' : 'none' }">
	<img alt="wait animation" src="${contextpath}/resources/images/ajax-loader.gif">
</div>

<!-- ko if: delphiTableEntries().length > 0 -->
<c:if test='${mode != "delphiStartPage" && ismobile != null}'>
<div class="results-table-row__links">
	<a class="results-table-row__link-show" onclick="showResultsTable(this)">
		<span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span> <spring:message code="label.ShowResults" />
	</a>
	<a class="results-table-row__link-hide" onclick="hideResultsTable(this)">
		<span class="glyphicon glyphicon-chevron-down" aria-hidden="true"></span> <spring:message code="label.HideResults" />
	</a>
</div>
</c:if>
<div class="delphi-table" id="delphiDiscussionTable" ${mode != 'delphiStartPage' && ismobile != null ? 'style="display: none;"' : ''}>
	
	<table class="table table-condensed table-striped table-bordered">
		<thead>
		<tr class="area-header">
			<th style="width:33%">
				<span>${form.getMessage("label.DelphiAnswersTableAnswer")}</span>
				<!-- ko if: delphiTableAnswerSorting()-->
					<div style="float: right">
						<a href="javascript:;" data-toggle="tooltip" data-container="body" data-title="${form.getMessage("label.SortAlphanumericalAscending")}" aria-label="${form.getMessage("label.SortAlphanumericalAscending")}" onclick="sortDelphiTable(this,'AnswersAsc');" class="overlaymenu-sortingOptions">
							<span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a>
						<a href="javascript:;" data-toggle="tooltip" data-container="body" data-title="${form.getMessage("label.SortAlphanumericalDescending")}" aria-label="${form.getMessage("label.SortAlphanumericalDescending")}" onclick="sortDelphiTable(this,'AnswersDesc');" class="overlaymenu-sortingOptions">
							<span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
					</div>
				<!-- /ko -->
			</th>
			<th style="min-width:${responsive != null ? "120" : "150"}px">
				<span>${form.getMessage("label.DelphiAnswersTableUpdate")}</span>
				<div style="float: right">
					<a href="javascript:;" data-toggle="tooltip" data-container="body" data-title="${form.getMessage("label.SortDatesAscending")}" aria-label="${form.getMessage("label.SortDatesAscending")}" onclick="sortDelphiTable(this,'UpdateAsc');" class="overlaymenu-sortingOptions">
						<span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a>
					<a href="javascript:;" data-toggle="tooltip" data-container="body" data-title="${form.getMessage("label.SortDatesDescending")}" aria-label="${form.getMessage("label.SortDatesDescending")}" onclick="sortDelphiTable(this,'UpdateDesc');" class="overlaymenu-sortingOptions">
						<span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
				</div>
			</th>
			<!-- ko if: showExplanationBox() -->
			<th style="width:33%">
				<span>${form.getMessage("label.DelphiAnswersTableExplanation")}</span>
				<div style="float: right">
					<a href="javascript:;" data-toggle="tooltip" data-container="body" data-title="${form.getMessage("label.SortExplanationsLessLiked")}" aria-label="${form.getMessage("label.SortExplanationsLessLiked")}" onclick="sortDelphiTable(this,'ExplanationsLessLiked');" class="overlaymenu-sortingOptions">
						<span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span></a>
					<a href="javascript:;" data-toggle="tooltip" data-container="body" data-title="${form.getMessage("label.SortExplanationsMostLiked")}" aria-label="${form.getMessage("label.SortExplanationsMostLiked")}" onclick="sortDelphiTable(this,'ExplanationsMostLiked');" class="overlaymenu-sortingOptions">
						<span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></a>
				</div>
			</th>
			<!-- /ko -->
			<th style="width:33%">
				<span>${form.getMessage("label.Discussion")}</span>
			</th>
		</tr>
		</thead>
		<tbody>
		<!-- ko foreach: delphiTableEntries -->
		<tr>
			<td>
				<!-- ko foreach: answers -->
				<div style="margin-bottom: 5px;">
					<!-- ko if: question -->
					<span data-bind="html: question"></span>:
					<!-- /ko -->
					<input tabindex="-1" class="text-read-more-checkbox" type="checkbox" data-bind="attr: {'id': 'expanded-answer' + uid}">
					<!-- ko if: $parents[1].delphiTableShowQuestionHtml() -->
					<span class="text-to-be-truncated" data-bind="html: value"></span>
					<!-- /ko -->
					<!-- ko ifnot: $parents[1].delphiTableShowQuestionHtml() -->
					<span class="text-to-be-truncated" data-bind="text: value"></span>
					<!-- /ko -->
					<label class="text-read-more-label" role="button" data-bind="attr: {'for': 'expanded-answer' + uid} ">${form.getMessage("label.ShowAll")}</label>
				</div>
				<!-- /ko -->
			</td>
			<td><span data-bind="html: update"></span></td>
			<!-- ko if: $parent.showExplanationBox() -->
			<td data-bind="attr: {'data-id': answerSetId}">
				<!-- ko if: explanation != null -->
				<div class="delphi-explanation" data-bind="attr: {'data-id': explanation.explanationId, 'data-uid': answerSetUniqueCode}">
					<input tabindex="-1" class="text-read-more-checkbox" type="checkbox" data-bind="attr: { 'id': 'expanded-explanation' + uid }">
					<span class="text-to-be-truncated" data-bind="html: explanation.text"></span>
					<span style="float:right">
						<c:choose>
							<c:when test='${mode == "delphiStartPage"}'>
								<span style="white-space:nowrap;" href="javascript:;" tabindex="0" class="focussable" onkeypress="likeDelphiCommentOrExplanationFromStartPage(this, true)" onClick="likeDelphiCommentOrExplanationFromStartPage(this, true)">
							</c:when>
							<c:otherwise>
								<span style="white-space:nowrap;" href="javascript:;" tabindex="0" class="focussable" onkeypress="likeDelphiCommentOrExplanationFromRunner(this, true)" onClick="likeDelphiCommentOrExplanationFromRunner(this, true)">
							</c:otherwise>
						</c:choose>

						<!-- ko if: explanation.numLikes != 0-->
							<span data-bind="html: explanation.numLikes"></span>
						<!-- /ko -->
						<!-- ko if: explanation.likes.includes("${uniqueCode}") -->
							<img class="likeImage" data-bind="attr: {'id': 'unlikeButtonDelphi-' + explanation.explanationId}" data-toggle="tooltip" title="${form.getMessage("label.Unlike")}" aria-label="${form.getMessage("label.Unlike")}" src="${contextpath}/resources/images/hand-thumbs-up-fill.svg" />
						<!-- /ko -->
						<!-- ko ifnot: explanation.likes.includes("${uniqueCode}") -->
							<!-- ko if: answerSetUniqueCode === "${uniqueCode}" -->
								<img class="likeImage" style="cursor: not-allowed;" data-bind="attr: {'id': 'likeButtonDelphi-' + explanation.explanationId}" title="${form.getMessage("label.Like")}" aria-label="${form.getMessage("label.Like")}" src="${contextpath}/resources/images/hand-thumbs-up-disabled.svg" />
							<!-- /ko -->
							<!-- ko ifnot: answerSetUniqueCode === "${uniqueCode}" -->
								<img class="likeImage" data-bind="attr: {'id': 'likeButtonDelphi-' + explanation.explanationId}" data-toggle="tooltip" title="${form.getMessage("label.Like")}" aria-label="${form.getMessage("label.Like")}" src="${contextpath}/resources/images/hand-thumbs-up.svg" />
							<!-- /ko -->
						<!-- /ko -->
						</span>
					</span>

					<label class="text-read-more-label" role="button" data-bind="attr: { 'for': 'expanded-explanation' + uid }">${form.getMessage("label.ShowAll")}</label>
				</div>
				<!-- /ko -->
				<!-- ko if: files.length > 0 && explanation.text.length > 0 -->
				<br />
				<!-- /ko -->
				<!-- ko foreach: files -->
					<a data-bind="attr: {href: '${contextpath}/files/${form.survey.uniqueId}/' + uid}, text: name"></a>
				<!-- /ko -->
			</td>
			<!-- /ko -->
			<td style="padding-top: 0; padding-bottom: 10px;" data-bind="attr: {'data-id': answerSetId}">
				<!-- ko foreach: comments -->
				<div class="delphi-comment" data-bind="attr: {'data-id': id, 'data-uid': answerSetUniqueCode}, css: { 'new-delphi-comment': unread }">
					<div style="margin-top: 5px;">
						<!-- ko if: user && date -->
						<span class="delphi-comment__user" data-bind="html: user"></span>
						<span class="delphi-comment__date" data-bind="html: date"></span>
						<br />
						<!-- /ko -->
						<input tabindex="-1" class="text-read-more-checkbox" type="checkbox" data-bind="attr: {'id': 'expanded' + id}">
						<span class="text-to-be-truncated" data-bind="hidden: isChangedCommentFormVisible, text: text"></span>
						<label class="text-read-more-label" role="button" data-bind="attr: {'for': 'expanded' + id}">${form.getMessage("label.ShowAll")}</label>
						<div class="delphi-comment__change-form" data-bind="visible: isChangedCommentFormVisible">
							<textarea class="form-control" data-bind="hasFocus: hasChangedCommentFieldFocus, value: changedComment"></textarea>
							<c:choose>
								<c:when test='${mode == "delphiStartPage"}'>
									<a href="javascript:;" class="btn btn-xs btn-primary" onClick="saveChangedDelphiCommentFromStartPage(this, false)">${form.getMessage("label.Save")}</a>
								</c:when>
								<c:otherwise>
									<a href="javascript:;" class="btn btn-xs btn-primary" onClick="saveChangedDelphiCommentFromRunner(this, false)">${form.getMessage("label.Save")}</a>
								</c:otherwise>
							</c:choose>
							<a href="javascript:;" class="btn btn-xs btn-default delphi-comment__cancel" data-bind="click: () => { isChangedCommentFormVisible(false); }">${form.getMessage("label.Cancel")}</a>
						</div>
						<div class="delphi-comment__actions">
							<!-- ko if: answerSetUniqueCode === "${uniqueCode}" && ((user && date) || replies.length === 0) -->
								<!-- ko if: user && date -->
								<a href="javascript:;" data-bind="click: editComment, hidden: isChangedCommentFormVisible">${form.getMessage("label.Edit")}</a>
								<!-- /ko -->
								<!-- ko if: (user && date) || replies.length === 0 -->
								<c:choose>
									<c:when test='${mode == "delphiStartPage"}'>
										<a href="javascript:;" onClick="deleteDelphiCommentFromStartPage(this, false)" data-bind="hidden: isChangedCommentFormVisible">${form.getMessage("label.Delete")}</a>
									</c:when>
									<c:otherwise>
										<a href="javascript:;" onClick="deleteDelphiCommentFromRunner(this, false)" data-bind="hidden: isChangedCommentFormVisible">${form.getMessage("label.Delete")}</a>
									</c:otherwise>
								</c:choose>
								<!-- /ko -->
							<!-- /ko -->

							<span style="float:right">
								<c:choose>
									<c:when test='${mode == "delphiStartPage"}'>
										<span style="white-space:nowrap;" href="javascript:;" tabindex="0" class="focussable" onkeypress="likeDelphiCommentOrExplanationFromStartPage(this)" onClick="likeDelphiCommentOrExplanationFromStartPage(this)">
									</c:when>
									<c:otherwise>
										<span style="white-space:nowrap;" href="javascript:;" tabindex="0" class="focussable" onkeypress="likeDelphiCommentOrExplanationFromRunner(this)" onClick="likeDelphiCommentOrExplanationFromRunner(this)">
									</c:otherwise>
								</c:choose>

								<!-- ko if: numLikes -->
									<span data-bind="html: numLikes"></span>
								<!-- /ko -->
								<!-- ko if: likes.includes("${uniqueCode}") -->
									<img class="likeImage" data-bind="attr: {'id': 'unlikeButtonDelphi-' + id}"  data-toggle="tooltip" title="${form.getMessage("label.Unlike")}" aria-label="${form.getMessage("label.Unlike")}" src="${contextpath}/resources/images/hand-thumbs-up-fill.svg" />
								<!-- /ko -->
								<!-- ko ifnot: likes.includes("${uniqueCode}") -->
									<!-- ko if: answerSetUniqueCode === "${uniqueCode}" -->
										<img class="likeImage" style="cursor: not-allowed;" data-bind="attr: {'id': 'likeButtonDelphi-' + id}" title="${form.getMessage("label.Like")}" aria-label="${form.getMessage("label.Like")}" src="${contextpath}/resources/images/hand-thumbs-up-disabled.svg" />
									<!-- /ko -->
									<!-- ko ifnot: answerSetUniqueCode === "${uniqueCode}" -->
										<img class="likeImage" data-bind="attr: {'id': 'likeButtonDelphi-' + id}" data-toggle="tooltip" title="${form.getMessage("label.Like")}" aria-label="${form.getMessage("label.Like")}" src="${contextpath}/resources/images/hand-thumbs-up.svg" />
									<!-- /ko -->
								<!-- /ko -->
								</span>
							</span>
						</div>
					</div>
					<!-- ko foreach: replies -->
					<div class="delphi-comment__reply" data-bind="attr: {'data-id': id}, css: { 'new-delphi-comment': unread }">
						<span class="delphi-comment__user" data-bind="html: user"></span>
						<span class="delphi-comment__date" data-bind="html: date"></span>
						<br/>

						<input tabindex="-1" class="text-read-more-checkbox" type="checkbox" data-bind="attr: {'id': 'expanded' + id}">
						<span class="text-to-be-truncated" data-bind="hidden: isChangedReplyFormVisible, text: text"></span>
						<label class="text-read-more-label" role="button" data-bind="attr: {'for': 'expanded' + id}">${form.getMessage("label.ShowAll")}</label>
						<div class="delphi-comment__change-form" data-bind="visible: isChangedReplyFormVisible">
							<textarea class="form-control" data-bind="hasFocus: hasChangedReplyFieldFocus, value: changedReply"></textarea>
							<c:choose>
								<c:when test='${mode == "delphiStartPage"}'>
									<a href="javascript:;" class="btn btn-xs btn-primary" onClick="saveChangedDelphiCommentFromStartPage(this, true)">${form.getMessage("label.Save")}</a>
								</c:when>
								<c:otherwise>
									<a href="javascript:;" class="btn btn-xs btn-primary" onClick="saveChangedDelphiCommentFromRunner(this, true)">${form.getMessage("label.Save")}</a>
								</c:otherwise>
							</c:choose>
							<a href="javascript:;" class="btn btn-xs btn-default delphi-comment__cancel" data-bind="click: () => { isChangedReplyFormVisible(false); }">${form.getMessage("label.Cancel")}</a>
						</div>
						<!-- ko if: answerSetUniqueCode === "${uniqueCode}" -->
						<div class="delphi-comment__actions">
							<a href="javascript:;" data-bind="click: editReply, hidden: isChangedReplyFormVisible">${form.getMessage("label.Edit")}</a>
							<c:choose>
								<c:when test='${mode == "delphiStartPage"}'>
									<a href="javascript:;" onClick="deleteDelphiCommentFromStartPage(this, true)" data-bind="hidden: isChangedReplyFormVisible">${form.getMessage("label.Delete")}</a>
								</c:when>
								<c:otherwise>
									<a href="javascript:;" onClick="deleteDelphiCommentFromRunner(this, true)" data-bind="hidden: isChangedReplyFormVisible">${form.getMessage("label.Delete")}</a>
								</c:otherwise>
							</c:choose>
						</div>
						<!-- /ko -->
					</div>
					<!-- /ko -->
					<div class="delphi-comment__add-reply">
						<a href="javascript:;" data-bind="click: showCommentArea">${form.getMessage("label.Reply")}</a>
						<div class="delphi-comment__add-reply-form" data-bind="visible: isReplyFormVisible">
							<textarea class="form-control" data-bind="hasFocus: hasReplyFieldFocus"></textarea>
							<c:choose>
								<c:when test='${mode == "delphiStartPage"}'>
									<a href="javascript:;" class="btn btn-xs btn-primary" onClick="saveDelphiCommentFromStartPage(this, true)" data-bind="attr: { 'data-parent': id }">${form.getMessage("label.Save")}</a>
								</c:when>
								<c:otherwise>
									<a href="javascript:;" class="btn btn-xs btn-primary" onClick="saveDelphiCommentFromRunner(this, true)" data-bind="attr: { 'data-parent': id }">${form.getMessage("label.Save")}</a>
								</c:otherwise>
							</c:choose>
							<a href="javascript:;" class="btn btn-xs btn-default delphi-comment__cancel" data-bind="click: () => { isReplyFormVisible(false); }">${form.getMessage("label.Cancel")}</a>
						</div>
					</div>
				</div>
				<!-- /ko -->
				<div class="delphi-comment-add">
					<a href="javascript:;" data-bind="click: showCommentArea">${form.getMessage("label.AddComment")}</a>
					<div class="delphi-comment-add__form" data-bind="visible: isCommentFormVisible">
						<textarea class="form-control" data-bind="hasFocus: hasCommentFieldFocus"></textarea>
						<c:choose>
							<c:when test='${mode == "delphiStartPage"}'>
								<a href="javascript:;" class="btn btn-xs btn-primary" onClick="saveDelphiCommentFromStartPage(this, false)">${form.getMessage("label.Save")}</a>
							</c:when>
							<c:otherwise>
								<a href="javascript:;" class="btn btn-xs btn-primary" onClick="saveDelphiCommentFromRunner(this, false)">${form.getMessage("label.Save")}</a>
							</c:otherwise>
						</c:choose>
						<a href="javascript:;" class="btn btn-xs btn-default delphi-comment__cancel" data-bind="click: () => { isCommentFormVisible(false); }">${form.getMessage("label.Cancel")}</a>
					</div>
				</div>
			</td>
		</tr>
		<!-- /ko -->
		</tbody>
	</table>

	<div style="text-align: center; margin-bottom: 10px;">
		<a data-bind="attr: {style: delphiTableOffset() > 0 ? '' : 'color: #ccc; cursor: default;'}" onclick="firstDelphiTablePage(this)">
			<span class="glyphicon glyphicon-step-backward"></span>
		</a>
		<a data-bind="attr: {style: delphiTableOffset() > 0 ? '' : 'color: #ccc; cursor: default;'}" onclick="previousDelphiTablePage(this)">
			<span class="glyphicon glyphicon-chevron-left"></span>
		</a>
		<span data-bind="html: delphiTableOffset() + 1"></span>&nbsp;
		<spring:message code="label.to" />&nbsp;
		<span data-bind="html: Math.min(delphiTableOffset() + delphiTableLimit(), delphiTableTotalEntries())"></span>
		<a data-bind="attr: {style: (delphiTableOffset() + delphiTableLimit()) >= delphiTableTotalEntries() ? 'color: #ccc; cursor: default;' : ''}" onclick="nextDelphiTablePage(this)">
			<span class="glyphicon glyphicon-chevron-right"></span>
		</a>
		<a data-bind="attr: {style: (delphiTableOffset() + delphiTableLimit()) >= delphiTableTotalEntries() ? 'color: #ccc; cursor: default;' : ''}" onclick="lastDelphiTablePage(this)">
			<span class="glyphicon glyphicon-step-forward"></span>
		</a>
	</div>
</div>
<!-- /ko -->

<c:if test='${mode == "delphiStartPage"}'>
	<!-- ko if: delphiTableEntries().length === 0 -->
	<div>
		<spring:message code="message.DelphiTableNoAnswerGivenYet" />
	</div>
	<!-- /ko -->
</c:if>

<style>
	.likeImage {
		vertical-align: top;
		width: auto !important;
		height: auto !important;
	}

	.overlaymenu-sortingOptions {
		color: #fff !important;
	}

</style>

<script type="text/javascript">

	$(window).scroll(function() {
		$(".overlaymenu").hide();
		$('[data-toggle="tooltip"]').tooltip("hide");
	});
	$(window).resize(function() {
		$(".overlaymenu").hide();
	});

</script>