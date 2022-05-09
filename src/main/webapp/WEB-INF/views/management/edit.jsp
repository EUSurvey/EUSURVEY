<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Edit" /></title>	
	<%@ include file="../includes.jsp" %>
	
	<link href="${contextpath}/resources/css/runner.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
  	<link href="${contextpath}/resources/css/edit.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${contextpath}/resources/js/spectrum.js?version=<%@include file="../version.txt" %>"></script>
	<link href="${contextpath}/resources/css/spectrum.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css"></link>
	<script type="text/javascript" src="${contextpath}/resources/js/moment.js?version=<%@include file="../version.txt" %>"></script>
	<script type='text/javascript' src='${contextpath}/resources/js/knockout-3.5.1.js?version=<%@include file="../version.txt" %>'></script>
  	<script type="text/javascript" src="${contextpath}/resources/js/jquery.textarea-expander.js?version=<%@include file="../version.txt" %>"></script>
  	<script type="text/javascript" src="${contextpath}/resources/js/edit.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/edit_actions.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/edit_add.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/edit_update.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/edit_update_helper.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/edit_navigation.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/edit_properties.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/edit_properties_helper.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/edit_validation.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/edit_complexity.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/edit_undo.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/runner.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/runner2.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/runnerviewmodels.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/menu.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/fileuploader.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/utf8.js?version=<%@include file="../version.txt" %>"></script>
	
	<c:if test="${form.survey.skin != null}">
		<style type="text/css">
			${form.survey.skin.getCss()}
		</style>
	</c:if>
</head>
<body id="surveyeditor">
	
	<div id="editorheader">
	<%@ include file="../header.jsp" %>
	<%@ include file="../menu.jsp" %>
	<%@ include file="formmenu.jsp" %>
	
	<div id="actions" class="actions">
		<div style="float: left; margin-left: 10px; padding-right: 10px; border-right: 1px solid #ccc;">
			<button id="save-button" data-bind="attr: {class: 'btn btn-default btn-primary' + (SaveEnabled() && AllElementsLoaded() ? '' : ' disabled')}" onclick="_actions.UnsavedChangesConfirmed(true); saveForm(false);"><spring:message code="label.Save" /></button>
			<a id="cancel-button" onclick="return checkChanges(this)" href="${contextpath}/${form.survey.shortname}/management/test" class="btn btn-default"><spring:message code="label.Cancel" /></a>
		</div>
		
		<div id="complexitydiv">
			<spring:message code="label.Complexity" />
			<img data-toggle="tooltip" title="<spring:message code="label.Complexity.low" />" id="complexityImageLevel" src="${contextpath}/resources/images/complexity-indicator-low.png" alt="<spring:message code="label.Complexity" />" style="margin-left:5px; margin-right:5px;"/>
		</div>
		
		<div class="actionbuttons" style="padding-right: 8px; border-right: 1px solid #ccc;">
			<span data-toggle="tooltip" title="<spring:message code="label.MultiSelect" />" id="multiselectButton"  data-bind="click: toggleMultiSelection, attr: {class: 'glyphicon glyphicon-check switchbutton' + (MultiSelectionEnabled() ? ' selected' : '')}"></span>
		</div>
		
		<div class="actionbuttons" style="float: right; margin-top: 0px; margin-left: 20px;">
			<span data-toggle="tooltip" title="<spring:message code="info.DeactivateLocalStorageShort" />" id="backupButton" data-bind="click: toggleBackupEnabled, attr: {class: 'glyphicon glyphicon-save switchbutton' + (BackupEnabled() ? ' selected' : '')}"></span>
			
			<span data-toggle="tooltip" title="<spring:message code="label.VisualizeVisibility" />" id="dependenciesButton" data-bind="click: toggleDependencies, attr: {class: 'glyphicon glyphicon-eye-open switchbutton' + (DependenciesEnabled() ? ' selected' : '')}"></span>
			<span data-toggle="tooltip" title="<spring:message code="label.ShowNavigation" />" id="shownavigationbutton" data-bind="click: toggleNavigationPane, attr: {class: 'glyphicon glyphicon-list-alt switchbutton' + (NavigationPaneEnabled() ? ' selected' : '')}"></span>
			<span data-toggle="tooltip" title="<spring:message code="label.ShowToolbox" />" id="showtoolboxbutton" data-bind="click: toggleToolboxPane, attr: {class: 'glyphicon glyphicon-wrench switchbutton' + (ToolboxPaneEnabled() ? ' selected' : '')}"></span>
			<span data-toggle="tooltip" title="<spring:message code="label.ShowProperties" />" id="showpropertiesbutton" data-bind="click: togglePropertiesPane, attr: {class: 'glyphicon glyphicon-cog switchbutton' + (PropertiesPaneEnabled() ? ' selected' : '')}"></span>
		</div>
		
		<div class="actionbuttons" style="padding-right: 8px; border-right: 1px solid #ccc;">
			<span data-toggle="tooltip" title="<spring:message code="label.Undo" />" id="undoButton" data-bind="attr: {class: 'glyphicon glyphicon-share-alt mirrored' + (UndoEnabled() ? '' : ' disabled')}" onclick="_undoProcessor.undo()"></span>
			<span data-toggle="tooltip" title="<spring:message code="label.Redo" />" id="redoButton" data-bind="attr: {class: 'glyphicon glyphicon-share-alt' + (RedoEnabled() ? '' : ' disabled')}" onclick="_undoProcessor.redo()"></span>
		</div>
		<div class="actionbuttons" style="padding-right: 8px; border-right: 1px solid #ccc;">
			<span data-toggle="tooltip" title="<spring:message code="label.Copy" />" id="copyElementButton" data-bind="click: copySelectedElement, attr: {class: 'glyphicon glyphicon-copy' + (CopyEnabled() ? '' : ' disabled')}"></span>
			<span data-toggle="tooltip" title="<spring:message code="label.Cut" />" id="cutElementButton" data-bind="click: cutSelectedElement, attr: {class: 'glyphicon glyphicon-scissors' + (CutEnabled() ? '' : ' disabled')}"></span>
			<span data-toggle="tooltip" title="<spring:message code="label.pasteAfter" />" id="pasteButton" data-bind="click: pasteElementAfter, attr: {class: 'glyphicon glyphicon-paste' + ((PasteEnabled() && !ChildSelected() && ElementSelected()) ? '' : ' disabled')}"></span>
		</div>
		<div class="actionbuttons" style="padding-right: 10px; border-right: 1px solid #ccc;">
			<span data-toggle="tooltip" title="<spring:message code="label.MoveDown" />" id="moveDownButton" data-bind="click: moveElementDown, attr: {class: 'glyphicon glyphicon-triangle-bottom' + (MoveDownEnabled() ? '' : ' disabled')}"></span>
			<span data-toggle="tooltip" title="<spring:message code="label.MoveUp" />" id="moveUpButton" data-bind="click: moveElementUp, attr: {class: 'glyphicon glyphicon-triangle-top' + (MoveUpEnabled() ? '' : ' disabled')}"></span>
		</div>
		<div class="actionbuttons">
			<span data-toggle="tooltip" id="deleteButton" title="<spring:message code="label.Delete" />" data-bind="click: deleteElement, attr: {class: 'glyphicon glyphicon-trash' + (DeleteEnabled() && AllElementsLoaded() ? '' : ' disabled')}"></span>
		</div>
		
		<div style="clear: both"></div>
	</div>
	</div>
	
	<div id="navigation" class="navigation">
		<div class="areaheader">
			<div style="float: right">
				<a onclick="collapseAll(true, this)"><span class="glyphicon glyphicon-expand" data-toggle="tooltip" data-placement="bottom" title="<spring:message code="label.CloseAll" />"></span></a>
				<a onclick="collapseAll(false, this)"><span class="glyphicon glyphicon-collapse-down" data-toggle="tooltip" data-placement="bottom" title="<spring:message code="label.ExpandAll" />"></span></a>
				<a onclick="_actions.toggleNavigationPane()"><span class="glyphicon glyphicon-remove" data-toggle="tooltip" data-placement="bottom" title="<spring:message code="label.closePane" />"></span></a>
			</div>
			<spring:message code="label.Navigation" />
		</div>
		
		<div id="navigationcontent" class="panecontent" style="top: 197px;">
			<span id="navigationwaitimage">
				<img alt="wait animation" src="${contextpath}/resources/images/ajax-loader.gif" />
			</span>
			<!--  ko foreach: items -->
			<div data-bind="attr: {class: 'navigationitem ' + css, 'data-id': id}">
				<!-- ko if: hasChildren() -->
				<span class="glyphicon glyphicon-chevron-down" onclick="showHideElements(this)"></span>
				<!-- /ko -->
				<!-- ko if: hasChildren() == false -->
				<span class="glyphicon glyphicon-chevron-down disabled"></span>
				<!-- /ko -->
			
				<a data-bind="html: title, click: function(data, event) {goTo(data.id, event)}"></a>
						
				<!--  ko foreach: items -->
					<div style="margin-left: 25px;" data-bind="attr: {class: 'navigationitem ' + css, 'data-id': id, id: id}">
						<a data-bind="html: title, click: function(data, event) {goTo(data.id, event)}"></a>					
					
						<!--  ko foreach: items -->
							<div style="margin-left: 20px;" data-bind="attr: {class: 'navigationitem ' + css, 'data-id': id, 'data-cellid': cellid}">
								<a data-bind="html: title, click: function(data, event) {goTo(data.id, event)}, attr: {'data-cellid': cellid}"></a>					
							</div>
						<!-- /ko -->
					
					</div>
				<!-- /ko -->
		
			</div>
			<!-- /ko -->	
		</div>
	</div>
		
	<div class="toolbox">
		<div class="areaheader">
			<div style="float: right">
				<a onclick="collapseAll(true, this)"><span class="glyphicon glyphicon-expand" data-toggle="tooltip" data-placement="bottom" title="<spring:message code="label.CloseAll" />"></span></a>
				<a style="margin-left: 1px" onclick="collapseAll(false, this)"><span class="glyphicon glyphicon-collapse-down" data-toggle="tooltip" data-placement="bottom" title="<spring:message code="label.ExpandAll" />"></span></a>
				<a onclick="_actions.toggleToolboxPane()"><span class="glyphicon glyphicon-remove" data-toggle="tooltip" data-placement="bottom" title="<spring:message code="label.closePane" />"></span></a>
			</div>
			<spring:message code="label.Toolbox" />
		</div>
		
		<div class="panecontent" style="padding-left: 6px;">
		
			<ul style="margin-top: 0px">
				<li id="copiedtoolboxitem" class="toolboxitem copy draggable" style="display: none"><span class="glyphicon glyphicon-copy"></span> <spring:message code="label.Copy" /></li>
				<li id="cancelcopytoolboxitem" style="display: none">
					<a onclick="_actions.cancelCopy();" class="btn btn-xs btn-default"><spring:message code="label.Cancel" /></a>
				</li>
				<li id="cuttoolboxitem" class="toolboxitem cut draggable" style="display: none"><span class="glyphicon glyphicon-copy"></span> <spring:message code="label.Cut" /></li>
				<li id="cancelcuttoolboxitem" style="display: none">
					<a onclick="_actions.cancelCut();" class="btn btn-xs btn-default"><spring:message code="label.Cancel" /></a>
				</li>
			</ul>
			

			
			<div class="toolboxgroup accordion-group" style="margin-top: 5px;">
				<div class="toolboxheader"><a class="accordion-toggle" data-toggle="collapse" href="#collapseStructure" aria-expanded="true"><spring:message code="label.Structure" /></a></div>
				<div id="collapseStructure" class="accordion-body collapse in">
					<ul>
						<li title="<spring:message code="form.Section.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" id="drag_section" class="toolboxitem sectionitem draggable"><span class="glyphicon glyphicon-folder-open"></span> <spring:message code="form.Section" /></li>
					</ul>
				</div>
			</div>			

			<div class="toolboxgroup accordion-group">
				<div class="toolboxheader"><a class="accordion-toggle" data-toggle="collapse" href="#collapseQuestions" aria-expanded="true"><spring:message code="form.Question" /></a></div>
				<div id="collapseQuestions" class="accordion-body collapse in">
					<ul>
						<c:choose>
							<c:when test="${form.survey.isQuiz}">
								<li title='<spring:message code="form.FreeText.Tooltip" arguments="https://ec.europa.eu/eusurvey/runner/TutorialEUSurvey" />' data-html="true" data-toggle="tooltip" data-placement="right" data-container="body" id="drag_freetext" class="toolboxitem freetextitem draggable quiz"><span class="glyphicon glyphicon-pencil"></span> <spring:message code="form.FreeText" /></li>
								<li title='<spring:message code="form.SingleChoice.Tooltip" arguments="https://ec.europa.eu/eusurvey/runner/TutorialEUSurvey" />' data-html="true" data-toggle="tooltip" data-placement="right" data-container="body" id="drag_simplechoice" class="toolboxitem singlechoiceitem draggable quiz"><span class="glyphicon glyphicon-ok-circle"></span> <spring:message code="form.SingleChoice" /></li>
								<li title='<spring:message code="form.MultipleChoice.Tooltip" arguments="https://ec.europa.eu/eusurvey/runner/TutorialEUSurvey" />' data-html="true" data-toggle="tooltip" data-placement="right" data-container="body" id="drag_multiplechoice" class="toolboxitem multiplechoiceitem draggable quiz"><span class="glyphicon glyphicon-check"></span> <spring:message code="form.MultipleChoice" /></li>
								<li title="<spring:message code="form.NumberSlider.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" id="drag_number" class="toolboxitem numberitem draggable quiz"><span class="glyphicon glyphicon-sound-5-1"></span> <spring:message code="form.NumberSlider" /></li>
								<li title="<spring:message code="form.Date.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" id="drag_date" class="toolboxitem dateitem draggable quiz"><span class="glyphicon glyphicon-calendar"></span> <spring:message code="form.Date" /></li>
								<li title="<spring:message code="form.Time.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" id="drag_time" class="toolboxitem timeitem draggable"><span class="glyphicon glyphicon-time"></span> <spring:message code="form.Time" /></li>
								<li title="<spring:message code="form.RankingQuestion.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" id="drag_ranking" class="toolboxitem rankingitem draggable"><span class="glyphicon glyphicon-sort"></span> <spring:message code="form.RankingQuestion" /></li>
							</c:when>
							<c:otherwise>
								<li title='<spring:message code="form.FreeText.Tooltip" arguments="https://ec.europa.eu/eusurvey/runner/TutorialEUSurvey" />' data-html="true" data-toggle="tooltip" data-placement="right" data-container="body" id="drag_freetext" class="toolboxitem freetextitem draggable"><span class="glyphicon glyphicon-pencil"></span> <spring:message code="form.FreeText" /></li>
								<li title='<spring:message code="form.SingleChoice.Tooltip" arguments="https://ec.europa.eu/eusurvey/runner/TutorialEUSurvey" />' data-html="true" data-toggle="tooltip" data-placement="right" data-container="body" id="drag_simplechoice" class="toolboxitem singlechoiceitem draggable"><span class="glyphicon glyphicon-ok-circle"></span> <spring:message code="form.SingleChoice" /></li>
								<li title='<spring:message code="form.MultipleChoice.Tooltip" arguments="https://ec.europa.eu/eusurvey/runner/TutorialEUSurvey" />' data-html="true" data-toggle="tooltip" data-placement="right" data-container="body" id="drag_multiplechoice" class="toolboxitem multiplechoiceitem draggable"><span class="glyphicon glyphicon-check"></span> <spring:message code="form.MultipleChoice" /></li>
								<li title="<spring:message code="form.NumberSlider.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" id="drag_number" class="toolboxitem numberitem draggable"><span class="glyphicon glyphicon-sound-5-1"></span> <spring:message code="form.NumberSlider" /></li>
								<li title="<spring:message code="form.Date.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" id="drag_date" class="toolboxitem dateitem draggable"><span class="glyphicon glyphicon-calendar"></span> <spring:message code="form.Date" /></li>
								<li title="<spring:message code="form.Time.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" id="drag_time" class="toolboxitem timeitem draggable"><span class="glyphicon glyphicon-time"></span> <spring:message code="form.Time" /></li>
								<li title="<spring:message code="form.RankingQuestion.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" id="drag_ranking" class="toolboxitem rankingitem draggable"><span class="glyphicon glyphicon-sort"></span> <spring:message code="form.RankingQuestion" /></li>
							</c:otherwise>
						</c:choose>
						<li title="<spring:message code="form.Matrix.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" id="drag_matrix" class="toolboxitem matrixitem draggable"><span class="glyphicon glyphicon-list-alt"></span> <spring:message code="form.Matrix" /></li>
						<li title="<spring:message code="form.Table.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" id="drag_tableelement" class="toolboxitem mytableitem draggable"><span class="glyphicon glyphicon-list"></span> <spring:message code="form.Table" /></li>
					</ul>
				</div>
			</div>
			
			<div class="toolboxgroup" style="">
				<div class="toolboxheader"><a class="accordion-toggle" data-toggle="collapse" href="#collapseMedia" aria-expanded="true"><spring:message code="label.TextAndMedia" /></a></div>
				<div id="collapseMedia" class="accordion-body collapse in">
					<ul>
						<li title="<spring:message code="form.Text.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" id="drag_text" class="toolboxitem textitem draggable"><span class="glyphicon glyphicon-font"></span> <spring:message code="form.Text" /></li>
						<li title="<spring:message code="form.Image.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" class="toolboxitem imageitem draggable"><span class="glyphicon glyphicon-picture"></span> <spring:message code="form.Image" /></li>
						<li title="<spring:message code="form.Line.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" class="toolboxitem ruleritem draggable"><span class="glyphicon glyphicon-minus"></span> <spring:message code="form.Line" /></li>
					</ul>
				</div>
			</div>
			
			<div class="toolboxgroup" style="">
				<div class="toolboxheader"><a class="accordion-toggle" data-toggle="collapse" href="#collapseSpecial" aria-expanded="true"><spring:message code="label.Special" /></a></div>
				<div id="collapseSpecial" class="accordion-body collapse in">
					<ul>
						<li title="<spring:message code="form.FileUpload.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" class="toolboxitem uploaditem draggable"><span class="glyphicon glyphicon-arrow-up"></span> <spring:message code="form.FileUpload" /></li>
						<li title="<spring:message code="form.FileDownload.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" class="toolboxitem downloaditem draggable"><span class="glyphicon glyphicon-arrow-down"></span> <spring:message code="form.FileDownload" /></li>
						<li title="<spring:message code="form.EmailQuestion.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" class="toolboxitem emailitem draggable"><span class="glyphicon glyphicon-envelope"></span> <spring:message code="label.Email" /></li>
						<li title="<spring:message code="form.RegExQuestion.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" class="toolboxitem regexitem draggable"><span class="glyphicon glyphicon-asterisk"></span> <spring:message code="label.RegEx" /></li>
						<li title="<spring:message code="form.Formula.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" class="toolboxitem formulaitem draggable">
							<span style="font-family: serif; font-style: italic; font-size: 13px; font-weight: bold; margin-right: 5px;">fx</span>
							<spring:message code="label.Formula" /></li>
						<li title="<spring:message code="form.Gallery.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" class="toolboxitem galleryitem draggable"><span class="glyphicon glyphicon-th"></span> <spring:message code="form.Gallery" /></li>
						<li title="<spring:message code="form.Confirmation.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" class="toolboxitem confirmationitem draggable"><span class="glyphicon glyphicon-ok"></span> <spring:message code="form.Confirmation" /></li>
						<li title="<spring:message code="form.Rating.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" class="toolboxitem ratingitem draggable"><span class="glyphicon glyphicon-star"></span> <spring:message code="form.Rating" /></li>
						<li title="<spring:message code="form.ComplexTable.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" class="toolboxitem complextableitem draggable"><span class="glyphicon glyphicon-th-large"></span> <spring:message code="form.ComplexTable" /></li>
					</ul>
				</div>
			</div>
			
			<div class="toolboxgroup" style="">
				<div class="toolboxheader"><a class="accordion-toggle" data-toggle="collapse" href="#collapsePredefined" aria-expanded="true"><spring:message code="label.Predefined" /></a></div>
				<div id="collapsePredefined" class="accordion-body collapse in">
					<ul>
						<li title="<spring:message code="form.predefined.euagencies.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" class="toolboxitem agenciesitem draggable"><span class="glyphicon glyphicon-star"></span> <spring:message code="form.predefined.euagencies" /></li>
						<li title="<spring:message code="form.predefined.eucountries.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" class="toolboxitem countriesitem draggable"><span class="glyphicon glyphicon-globe"></span> <spring:message code="form.predefined.eucountries" /></li>
						<li title="<spring:message code="form.predefined.eulanguages.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" class="toolboxitem languagesitem draggable"><span class="glyphicon glyphicon-flag"></span> <spring:message code="form.predefined.eulanguages" /></li>
                                                <c:if test="${!oss}">
                                                    <li title="<spring:message code="form.predefined.dgsnew.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" class="toolboxitem dgsitem draggable"><span class="glyphicon glyphicon-home"></span> <spring:message code="form.predefined.dgsnew" /></li>
                                                </c:if>                                                
						<li title="<spring:message code="form.predefined.uns.Tooltip" />" data-toggle="tooltip" data-placement="right" data-container="body" class="toolboxitem unsitem draggable"><span class="glyphicon"><img style="vertical-align: bottom;" src="${contextpath}/resources/images/unlogo.png" /></span> <spring:message code="form.predefined.uns" /></li>
					</ul>
				</div>
			</div>
			
		</div>
	</div>
	
	<div class="properties">
		<div class="areaheader">
			<div style="float: right">
				<a onclick="collapseAll(true, this)"><span class="glyphicon glyphicon-expand" data-toggle="tooltip" data-placement="bottom" title="<spring:message code="label.CloseAll" />"></span></a>
				<a onclick="collapseAll(false, this)"><span class="glyphicon glyphicon-collapse-down" data-toggle="tooltip" data-placement="bottom" title="<spring:message code="label.ExpandAll" />"></span></a>
				<a onclick="_actions.togglePropertiesPane()"><span class="glyphicon glyphicon-remove" data-toggle="tooltip" data-placement="bottom" title="<spring:message code="label.closePane" />"></span></a>
			</div>
			<spring:message code="label.Properties" />
		</div>
		<div class="toolboxheader" style="position: absolute; top: 185px; margin-left: -2px;">
			<spring:message code="label.ElementProperties" />
		</div>
		<div class="panecontent" style="width: 100%; min-width: 300px; top: 222px;">
			<div id="lockedElementInfo" style="display: none; padding: 20px;">
				<div style="float: right"><span class="glyphicon glyphicon-lock"></span></div>
				<spring:message code="info.ElementLocked" />				
			</div>
			<table id="properties">
				<tr class="firstpropertyrow" data-bind="attr: {class: Type() != '' ? '' : 'hideme'}">
					<td class="propertylabel"><spring:message code="label.Type" /></td>
					<td class="propertycontent" style="font-style: italic;" data-bind="html: Type"></td>
				</tr>
				<!--  ko foreach: propertyRows() -->
					<!--  ko if: Type() == 'first' -->
						<!-- ko template: { name: 'firstrow-template' } --><!-- /ko -->	
					<!-- /ko -->	
					
					<!--  ko if: Type() == 'registration' -->		
						<!-- ko template: { name: 'registration-template' } --><!-- /ko -->	
					<!-- /ko -->		
				
					<!--  ko if: Type() == 'TinyMCE' -->
						<!-- ko template: { name: 'tinymce-template' } --><!-- /ko -->		
					<!-- /ko -->
					
					<!--  ko if: Type() == 'FormulaOperators' -->
						<!-- ko template: { name: 'editformula-template' } --><!-- /ko -->		
					<!-- /ko -->
					
					<!--  ko if: Type() == 'CellProperties' -->
						<!-- ko template: { name: 'editcomplextable-template', afterRender: function() {$("input.spinner").spinner({ decimals:2, start:"", allowNull: true });} } --><!-- /ko -->
					<!-- /ko -->
					
					<!--  ko if: Type() == 'HeaderCellProperties' -->
						<!-- ko template: { name: 'editcomplextableheader-template', afterRender: function() {$("input.spinner").spinner({ decimals:2, start:"", allowNull: true });} } --><!-- /ko -->
					<!-- /ko -->

					<!--  ko if: Type() == 'FirstCellProperties' -->
						<!-- ko template: { name: 'editcomplextablefirst-template' } --><!-- /ko -->
					<!-- /ko -->
					
					<!--  ko if: Type() == 'PossibleAnswerShortnames' -->
						<!-- ko template: { name: 'PossibleAnswerShortnames-template' } --><!-- /ko -->		
					<!-- /ko -->					
										
					<!--  ko if: Type() == 'advanced' -->
						<!-- ko template: { name: 'advanced-template' } --><!-- /ko -->
					<!-- /ko -->
					
					<!--  ko if: Type() == 'quiz' -->
						<!-- ko template: { name: 'quiz-template' } --><!-- /ko -->
					<!-- /ko -->
					
					<!--  ko if: Type() == 'ecf' -->
						<!-- ko template: { name: 'ecf-template' } --><!-- /ko -->
					<!-- /ko -->
					
					<!--  ko if: Type() == 'ecfAnswersToScores' -->
						<!-- ko template: { name: 'ecfanswerstoscores-template' } --><!-- /ko --> 
					<!-- /ko --> 

					<!--  ko if: Type() == 'ecfAnswersToProfiles' -->
						<!-- ko template: { name: 'ecfanswerstoprofiles-template' } --><!-- /ko --> 
					<!-- /ko --> 

					<!--  ko if: Type() == 'slider' -->
						<!-- ko template: { name: 'slider-template' } --><!-- /ko -->
					<!-- /ko -->
					
					<!--  ko if: Type() == 'quizanswers' -->
						<!-- ko template: { name: 'quizanswers-template' } --><!-- /ko -->
					<!-- /ko -->
				<!-- /ko -->
			</table>
		</div>
	</div>
	
	<form:form id="frmEdit" method="POST" action="edit" commandName="survey" modelAttribute="form">
		<input type="hidden" name="elements" id="elements" value="" />
		<input type="hidden" name="editorredirect" id="editorredirect" value="" />
		<input type="hidden" id="sectionNumbering" value="${form.survey.sectionNumbering}" />
		<input type="hidden" id="questionNumbering" value="${form.survey.questionNumbering}" />
		<input type="hidden" id="regform" value="${form.survey.registrationForm}" />
		<input type="hidden" id="criticalComplexity" name="criticalComplexity" value="" />
		<form:hidden path="survey.id"/>

		<div id="editcontent">
			<div id="empty-content-message">
				<spring:message code="message.InsertElements" />
			</div>
		
			<ul class="sortable" id="content">
				<c:forEach var="element" items="${form.survey.getElements()}">
				
					<c:choose>
						<c:when test="${element.isDependent}">
							<li class="emptyelement survey-element" id="${element.id}" data-id="${element.id}" data-triggers="${element.triggers}">
						</c:when>
						<c:otherwise>
							<li class="emptyelement survey-element" id="${element.id}" data-id="${element.id}">
						</c:otherwise>
					</c:choose>
				
						<img src="${contextpath}/resources/images/ajax-loader.gif" />
					</li>									
				</c:forEach>
			</ul>	
		</div>
	</form:form>
	
	<jsp:include page="../runner/elementtemplates.jsp" />
	<jsp:include page="edittemplates.jsp" />
	<jsp:include page="editdialogs.jsp" />
	<jsp:include page="../includes2.jsp" />		

</body>
</html>
