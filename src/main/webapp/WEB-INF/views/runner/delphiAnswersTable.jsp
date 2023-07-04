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
			<th style="width:33%">${form.getMessage("label.DelphiAnswersTableAnswer")}</th>
			<th style="min-width:${responsive != null ? "120" : "150"}px">
				<span>${form.getMessage("label.DelphiAnswersTableUpdate")}</span>
				<div style="float: right">
					<a href="javascript:;" data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" aria-label="<spring:message code="label.SortAscending" />"
					   onclick="sortDelphiTable(this,'UpdateAsc');" class="">
						<span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span>
					</a>
					<a href="javascript:;" data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" aria-label="<spring:message code="label.SortDescending" />" onclick="sortDelphiTable(this,'UpdateDesc');" class="">
						<span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span>
					</a>
				</div>
			</th>
			<!-- ko if: showExplanationBox() -->
			<th style="width:33%">${form.getMessage("label.DelphiAnswersTableExplanation")}</th>
			<!-- /ko -->
			<th style="width:33%">
				<span>${form.getMessage("label.Discussion")}</span>
				<div style="float: right">
					<c:choose>
						<c:when test='${mode == "delphiStartPage"}'>
							<a href="javascript:;" data-toggle="tooltip" data-container="body" data-title="<spring:message code="label.SortComments" />" aria-label="<spring:message code="label.SortComments" />"
						   onclick="showOverlayMenuSortingOptionsStartPage(this)">
						</c:when>
						<c:otherwise>
							<a href="javascript:;" data-toggle="tooltip" data-title="<spring:message code="label.SortComments" />" aria-label="<spring:message code="label.SortComments" />"
							   onclick="showOverlayMenuSortingOptions(this)">
						</c:otherwise>
					</c:choose>
						<span class="glyphicon glyphicon-sort-by-attributes-alt" aria-hidden="true"></span>
					</a>
					<div class="overlaymenu overlaymenu-sortingOptions hideme" role="group" id="sortingOptions">
						<div class="btn-group-vertical">
							<c:choose>
								<c:when test='${mode == "delphiStartPage"}'>
									<a class="btn btn-default" tabindex="0" id="sort-OldestFirst" onkeypress="sortCommentsStartPage(this, 'OldestFirst')" onclick="sortCommentsStartPage(this, 'OldestFirst')">${form.getMessage("label.OldestFirst")}</a>
									<a class="btn btn-default" tabindex="0" id="sort-NewestFirst" onkeypress="sortCommentsStartPage(this, 'NewestFirst')" onclick="sortCommentsStartPage(this, 'NewestFirst')">${form.getMessage("label.NewestFirst")}</a>
									<a class="btn btn-default" tabindex="0" id="sort-TopComments" onkeypress="sortCommentsStartPage(this, 'TopComments')" onclick="sortCommentsStartPage(this, 'TopComments')">${form.getMessage("label.TopComments")}</a>
								</c:when>
								<c:otherwise>
									<a class="btn btn-default" tabindex="0" id="sort-OldestFirst" onkeypress="sortComments(this, 'OldestFirst')" onclick="sortComments(this, 'OldestFirst')">${form.getMessage("label.OldestFirst")}</a>
									<a class="btn btn-default" tabindex="0" id="sort-NewestFirst" onkeypress="sortComments(this, 'NewestFirst')" onclick="sortComments(this, 'NewestFirst')">${form.getMessage("label.NewestFirst")}</a>
									<a class="btn btn-default" tabindex="0" id="sort-TopComments" onkeypress="sortComments(this, 'TopComments')" onclick="sortComments(this, 'TopComments')">${form.getMessage("label.TopComments")}</a>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</div>
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
			<td>
				<input tabindex="-1" class="text-read-more-checkbox" type="checkbox" data-bind="attr: { 'id': 'expanded-explanation' + uid }">
				<span class="text-to-be-truncated" data-bind="html: explanation"></span>
				<label class="text-read-more-label" role="button" data-bind="attr: { 'for': 'expanded-explanation' + uid }">${form.getMessage("label.ShowAll")}</label>
				<!-- ko if: files.length > 0 && explanation.length > 0 -->
				<br />
				<!-- /ko -->
				<!-- ko foreach: files -->
				<a data-bind="attr: {href: '${contextpath}/files/${form.survey.uniqueId}/' + uid}, text: name"></a>
				<!-- /ko -->
			</td>
			<!-- /ko -->
			<td style="padding-top: 0; padding-bottom: 10px;" data-bind="attr: {'data-id': answerSetId}">
				<!-- ko foreach: comments -->
				<div class="delphi-comment" data-bind="attr: {'data-id': id}, css: { 'new-delphi-comment': unread }">
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

							<c:choose>
								<c:when test='${mode == "delphiStartPage"}'>
									<span style="white-space:nowrap;" href="javascript:;" tabindex="0" class="focussable" onkeypress="likeDelphiCommentFromStartPage(this)" onClick="likeDelphiCommentFromStartPage(this)">
								</c:when>
								<c:otherwise>
									<span style="white-space:nowrap;" href="javascript:;" tabindex="0" class="focussable" onkeypress="likeDelphiCommentFromRunner(this)" onClick="likeDelphiCommentFromRunner(this)">
								</c:otherwise>
							</c:choose>

									<!-- ko if: likes.includes("${uniqueCode}") -->
										<img class="likeImage" data-bind="attr: {'id': 'unlikeButtonDelphi-' + id}"  data-toggle="tooltip" title="${form.getMessage("label.Unlike")}" aria-label="${form.getMessage("label.Unlike")}" src="${contextpath}/resources/images/hand-thumbs-up-fill.svg" />
									<!-- /ko -->
									<!-- ko ifnot: likes.includes("${uniqueCode}") -->
										<img class="likeImage" data-bind="attr: {'id': 'likeButtonDelphi-' + id}" data-toggle="tooltip" title="${form.getMessage("label.Like")}" aria-label="${form.getMessage("label.Like")}" src="${contextpath}/resources/images/hand-thumbs-up.svg" />
									<!-- /ko -->
									<!-- ko if: numLikes -->
										<span data-bind="html: numLikes"></span>
									<!-- /ko -->
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
		padding: 0px;
		border: 1px solid #ddd;

		max-height: 200px;
		overflow-y: scroll;
	}

	.overlaymenu-sortingOptions .btn-primary {
		color: #fff;
	}

	.overlaymenu-sortingOptions .btn-primary:hover {
		color: #fff;
	}

	.overlaymenu-sortingOptions .btn {
		width: 100%;
	}

	.overlaymenu-sortingOptions a {
		color: initial;
	}

	.overlaymenu-sortingOptions a:hover {
		color: initial;
	}

</style>

<script type="text/javascript">

	$(window).scroll(function() {$(".overlaymenu").hide();});
	$(window).resize(function() {
		$(".overlaymenu").hide();
	});

	function showOverlayMenuSortingOptions(btn) {
		var surveyElement = $(btn).closest(".survey-element");
		var uid = $(surveyElement).attr("data-uid");
		var viewModel = modelsForDelphiQuestions[uid];

		var overlay = $(btn).parent().find('.overlaymenu').first();
		var rect = $(btn).parent().parent()[0].getBoundingClientRect();
		var rectbtn = $(btn)[0].getBoundingClientRect();

		$(overlay).css("left", rect.right - $(overlay).width() - 2);
		$(overlay).css("top", rectbtn.bottom + 8);

		showOverlayMenuSortingOptionsInner(btn, viewModel);
	}

	function showOverlayMenuSortingOptionsInner(btn, viewModel)
	{
		closeOverlayDivsEnabled = false;
		var overlay = $(btn).parent().find('.overlaymenu').first();

		if ($(overlay).is(":visible"))
		{
			$(overlay).hide();
			return;
		}

		$(".overlaymenu").find("#sort-" + viewModel.delphiCommentOrderBy()).addClass("btn-primary");

		$(".overlaymenu").hide();

		$(overlay).toggle();
		$(btn).addClass("overlaybutton");

		setTimeout(function(){closeOverlayDivsEnabled = true;},1000);
	}
</script>