<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>

<!-- ko if: delphiTableEntries().length > 0 -->
<div class="delphi-table">
	<div class="loader" data-bind="style: { display: delphiTableLoading() ? 'flex' : 'none' }">
		<img src="${contextpath}/resources/images/ajax-loader.gif">
	</div>
	<div class="modal delete-confirmation-dialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-body">
					<spring:message code="message.DelphiConfirmDeleteComment" />
				</div>
				<div class="modal-footer">
					<a class="btn btn-default delete-confirmation-dialog__confirmation-button"><spring:message code="label.Delete" /></a>
					<a class="btn btn-primary delete-confirmation-dialog__cancel-button"><spring:message code="label.Cancel" /></a>
				</div>
			</div>
		</div>
	</div>
	<table class="table table-condensed table-striped table-bordered">
		<thead>
		<tr class="area-header">
			<th style="width:33%">${form.getMessage("label.DelphiAnswersTableAnswer")}</th>
			<th style="min-width:150px">
				<span>${form.getMessage("label.DelphiAnswersTableUpdate")}</span>
				<div style="float: right">
					<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />"
					   onclick="sortDelphiTable(this,'UpdateAsc');" class="">
						<span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span>
					</a>
					<a data-toggle="tooltip" data-title="<spring:message code="label.SortDescending" />" onclick="sortDelphiTable(this,'UpdateDesc');" class="">
						<span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span>
					</a>
				</div>
			</th>
			<th style="width:33%">${form.getMessage("label.DelphiAnswersTableExplanation")}</th>
			<th style="width:33%">${form.getMessage("label.Discussion")}</th>
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
					<span data-bind="html: value"></span>
				</div>
				<!-- /ko -->
			</td>
			<td><span data-bind="html: update"></span></td>
			<td>
				<span data-bind="html: explanation"></span>
				<!-- ko if: files.length > 0 && explanation.length > 0 -->
				<br />
				<!-- /ko -->
				<!-- ko foreach: files -->
				<a data-bind="attr: {href: '${contextpath}/files/${form.survey.uniqueId}/' + uid}, text: name"></a>
				<!-- /ko -->
			</td>
			<td style="padding-top: 0; padding-bottom: 10px;" data-bind="attr: {'data-id': answerSetId}">
				<!-- ko foreach: comments -->
				<div class="delphi-comment" data-bind="attr: {'data-id': id}">
					<div style="margin-top: 5px;">
						<!-- ko if: user && date -->
						<span class="delphi-comment__user" data-bind="html: user"></span> <span class="delphi-comment__date" data-bind="html: date"></span><br />
						<!-- /ko -->
						<span data-bind="hidden: isChangedCommentFormVisible, html: text"></span>
						<div class="delphi-comment__change-form" data-bind="visible: isChangedCommentFormVisible">
							<textarea class="form-control" data-bind="hasFocus: hasChangedCommentFieldFocus, value: changedComment"></textarea>
							<c:choose>
								<c:when test='${mode == "delphiStartPage"}'>
									<a class="btn btn-xs btn-primary" onClick="saveChangedDelphiCommentFromStartPage(this, false)">${form.getMessage("label.Save")}</a>
								</c:when>
								<c:otherwise>
									<a class="btn btn-xs btn-primary" onClick="saveChangedDelphiCommentFromRunner(this, false)">${form.getMessage("label.Save")}</a>
								</c:otherwise>
							</c:choose>
							<a class="btn btn-xs btn-default delphi-comment__cancel" data-bind="click: () => { isChangedCommentFormVisible(false); }">${form.getMessage("label.Cancel")}</a>
						</div>
						<!-- ko if: answerSetUniqueCode === "${uniqueCode}" && ((user && date) || replies.length === 0) -->
						<div class="delphi-comment__actions">
							<!-- ko if: user && date -->
							<a data-bind="click: editComment, hidden: isChangedCommentFormVisible">${form.getMessage("label.Edit")}</a>
							<!-- /ko -->
							<!-- ko if: (user && date) || replies.length === 0 -->
							<c:choose>
								<c:when test='${mode == "delphiStartPage"}'>
									<a onClick="deleteDelphiCommentFromStartPage(this, false)" data-bind="hidden: isChangedCommentFormVisible">${form.getMessage("label.Delete")}</a>
								</c:when>
								<c:otherwise>
									<a onClick="deleteDelphiCommentFromRunner(this, false)" data-bind="hidden: isChangedCommentFormVisible">${form.getMessage("label.Delete")}</a>
								</c:otherwise>
							</c:choose>
							<!-- /ko -->
						</div>
						<!-- /ko -->
					</div>
					<!-- ko foreach: replies -->
					<div class="delphi-comment__reply" data-bind="attr: {'data-id': id}">
						<span class="delphi-comment__user" data-bind="html: user"></span> <span class="delphi-comment__date" data-bind="html: date"></span><br />
						<span data-bind="hidden: isChangedReplyFormVisible, html: text"></span>
						<div class="delphi-comment__change-form" data-bind="visible: isChangedReplyFormVisible">
							<textarea class="form-control" data-bind="hasFocus: hasChangedReplyFieldFocus, value: changedReply"></textarea>
							<c:choose>
								<c:when test='${mode == "delphiStartPage"}'>
									<a class="btn btn-xs btn-primary" onClick="saveChangedDelphiCommentFromStartPage(this, true)">${form.getMessage("label.Save")}</a>
								</c:when>
								<c:otherwise>
									<a class="btn btn-xs btn-primary" onClick="saveChangedDelphiCommentFromRunner(this, true)">${form.getMessage("label.Save")}</a>
								</c:otherwise>
							</c:choose>
							<a class="btn btn-xs btn-default delphi-comment__cancel" data-bind="click: () => { isChangedReplyFormVisible(false); }">${form.getMessage("label.Cancel")}</a>
						</div>
						<!-- ko if: answerSetUniqueCode === "${uniqueCode}" -->
						<div class="delphi-comment__actions">
							<a data-bind="click: editReply, hidden: isChangedReplyFormVisible">${form.getMessage("label.Edit")}</a>
							<c:choose>
								<c:when test='${mode == "delphiStartPage"}'>
									<a onClick="deleteDelphiCommentFromStartPage(this, true)" data-bind="hidden: isChangedReplyFormVisible">${form.getMessage("label.Delete")}</a>
								</c:when>
								<c:otherwise>
									<a onClick="deleteDelphiCommentFromRunner(this, true)" data-bind="hidden: isChangedReplyFormVisible">${form.getMessage("label.Delete")}</a>
								</c:otherwise>
							</c:choose>
						</div>
						<!-- /ko -->
					</div>
					<!-- /ko -->
					<div class="delphi-comment__add-reply">
						<a data-bind="click: showCommentArea">${form.getMessage("label.Reply")}</a>
						<div class="delphi-comment__add-reply-form" data-bind="visible: isReplyFormVisible">
							<textarea class="form-control" data-bind="hasFocus: hasReplyFieldFocus"></textarea>
							<c:choose>
								<c:when test='${mode == "delphiStartPage"}'>
									<a class="btn btn-xs btn-primary" onClick="saveDelphiCommentFromStartPage(this, true)" data-bind="attr: { 'data-parent': id }">${form.getMessage("label.Save")}</a>
								</c:when>
								<c:otherwise>
									<a class="btn btn-xs btn-primary" onClick="saveDelphiCommentFromRunner(this, true)" data-bind="attr: { 'data-parent': id }">${form.getMessage("label.Save")}</a>
								</c:otherwise>
							</c:choose>
							<a class="btn btn-xs btn-default delphi-comment__cancel" data-bind="click: () => { isReplyFormVisible(false); }">${form.getMessage("label.Cancel")}</a>
						</div>
					</div>
				</div>
				<!-- /ko -->
				<div class="delphi-comment-add">
					<a data-bind="click: showCommentArea">${form.getMessage("label.AddComment")}</a>
					<div class="delphi-comment-add__form" data-bind="visible: isCommentFormVisible">
						<textarea class="form-control" data-bind="hasFocus: hasCommentFieldFocus"></textarea>
						<c:choose>
							<c:when test='${mode == "delphiStartPage"}'>
								<a class="btn btn-xs btn-primary" onClick="saveDelphiCommentFromStartPage(this, false)">${form.getMessage("label.Save")}</a>
							</c:when>
							<c:otherwise>
								<a class="btn btn-xs btn-primary" onClick="saveDelphiCommentFromRunner(this, false)">${form.getMessage("label.Save")}</a>
							</c:otherwise>
						</c:choose>
						<a class="btn btn-xs btn-default delphi-comment__cancel" data-bind="click: () => { isCommentFormVisible(false); }">${form.getMessage("label.Cancel")}</a>
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
