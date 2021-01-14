<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>

<!-- ko if: delphiTableEntries().length > 0 -->
<div class="delphi-table">
	<table class="table table-condensed table-striped table-bordered">
		<thead>
		<tr class="area-header">
			<th style="width:33%">${form.getMessage("label.DelphiAnswersTableAnswer")}</th>
			<th style="min-width:150px">
				<span>${form.getMessage("label.DelphiAnswersTableUpdate")}</span>
				<div style="float: right">
					<a data-toggle="tooltip" data-title="<spring:message code="label.SortAscending" />" onclick="sortDelphiTable(this,'UpdateAsc');" class="">
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
				<!-- ko if: files.length > 0 -->
				<br>
				<!-- /ko -->
				<!-- ko foreach: files -->
				<a data-bind="attr: {href: '${contextpath}/files/${form.survey.uniqueId}/' + uid}, text: name"></a>
				<!-- /ko -->
			</td>
			<td style="padding-top: 0; padding-bottom: 10px;" data-bind="attr: {'data-id': answerSetId}">
				<!-- ko foreach: comments -->
				<div class="delphicommentsdiv">
					<div style="margin-top: 5px;">
						<span style="font-weight: bold" data-bind="html: user"></span> <span class="delphicommentdate" data-bind="html: date"></span><br />
						<span data-bind="html: text"></span>
					</div>
					<!-- ko foreach: replies -->
					<div style="margin-top: 10px; margin-left: 20px;">
						<span style="font-weight: bold" data-bind="html: user"></span> <span class="delphicommentdate" data-bind="html: date"></span><br />
						<span data-bind="html: text"></span>
					</div>
					<!-- /ko -->
					<div style="margin-left: 20px; margin-top: 10px;">
						<a data-bind="click: () => { showCommentArea(); }">${form.getMessage("label.Reply")}</a>
						<div class="delphireply" data-bind="visible: delphiTableIsReplyFormVisible">
							<textarea class="form-control" data-bind="hasFocus: delphiTableHasReplyFieldFocus"></textarea>
							<c:choose>
								<c:when test='${mode == "delphiStartPage"}'>
									<a class="btn btn-xs btn-primary" onClick="saveDelphiCommentWrapper(this, true)" data-bind="attr: { 'data-parent': id }">${form.getMessage("label.Save")}</a>
								</c:when>
								<c:otherwise>
									<a class="btn btn-xs btn-primary" onClick="saveDelphiComment(this, true)" data-bind="attr: { 'data-parent': id }">${form.getMessage("label.Save")}</a>
								</c:otherwise>
							</c:choose>
							<a class="btn btn-xs btn-default delphicommentcancel" data-bind="click: () => { delphiTableIsReplyFormVisible(false); }">${form.getMessage("label.Cancel")}</a>
						</div>
					</div>
				</div>
				<!-- /ko -->
				<div style="margin-top: 5px">
					<a data-bind="click: () => {showCommentArea(); }">${form.getMessage("label.AddComment")}</a>
					<div class="delphicomment" data-bind="visible: delphiTableIsCommentFormVisible">
						<textarea class="form-control" data-bind="hasFocus: delphiTableHasCommentFieldFocus"></textarea>
						<c:choose>
							<c:when test='${mode == "delphiStartPage"}'>
								<a class="btn btn-xs btn-primary" onClick="saveDelphiCommentWrapper(this, false)">${form.getMessage("label.Save")}</a>
							</c:when>
							<c:otherwise>
								<a class="btn btn-xs btn-primary" onClick="saveDelphiComment(this, false)">${form.getMessage("label.Save")}</a>
							</c:otherwise>
						</c:choose>
						<a class="btn btn-xs btn-default delphicommentcancel" data-bind="click: () => { delphiTableIsCommentFormVisible(false); }">${form.getMessage("label.Cancel")}</a>
					</div>
				</div>
			</td>
		</tr>
		<!-- /ko -->
		</tbody>
	</table>
</div>
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
<!-- /ko -->

<c:if test='${mode == "delphiStartPage"}'>
	<!-- ko if: delphiTableEntries().length === 0 -->
	<div>
		<spring:message code="message.DelphiTableNoAnswerGivenYet" />
	</div>
	<!-- /ko -->
</c:if>
