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
				<li id="cuttoolboxitem" class="toolboxitem cut draggable" style="display: none"><span class="glyphicon glyphicon-copy"></span> <spring:message code="label.Cut" /></li>
			</ul>
			
			<div id="cancelcuttoolboxitem" style="display: none">
				<a onclick="_actions.cancelCut();" class="btn btn-xs btn-default"><spring:message code="label.Cancel" /></a>
			</div>
			<div id="cancelcopytoolboxitem" style="display: none">
				<a onclick="_actions.cancelCopy();" class="btn btn-xs btn-default"><spring:message code="label.Cancel" /></a>
			</div>
			
			<div class="toolboxgroup accordion-group" style="margin-top: 5px;">
				<div class="toolboxheader"><a class="accordion-toggle" data-toggle="collapse" href="#collapseStructure" aria-expanded="true"><spring:message code="label.Structure" /></a></div>
				<div id="collapseStructure" class="accordion-body collapse in">
					<ul>
						<li id="drag_section" class="toolboxitem sectionitem draggable"><span class="glyphicon glyphicon-folder-open"></span> <spring:message code="form.Section" /></li>
					</ul>
				</div>
			</div>			

			<div class="toolboxgroup accordion-group">
				<div class="toolboxheader"><a class="accordion-toggle" data-toggle="collapse" href="#collapseQuestions" aria-expanded="true"><spring:message code="form.Question" /></a></div>
				<div id="collapseQuestions" class="accordion-body collapse in">
					<ul>
						<c:choose>
							<c:when test="${form.survey.isQuiz}">
								<li id="drag_freetext" class="toolboxitem freetextitem draggable quiz"><span class="glyphicon glyphicon-pencil"></span> <spring:message code="form.FreeText" /></li>
								<li id="drag_simplechoice" class="toolboxitem singlechoiceitem draggable quiz"><span class="glyphicon glyphicon-ok-circle"></span> <spring:message code="form.SingleChoice" /></li>
								<li id="drag_multiplechoice" class="toolboxitem multiplechoiceitem draggable quiz"><span class="glyphicon glyphicon-check"></span> <spring:message code="form.MultipleChoice" /></li>
								<li id="drag_number" class="toolboxitem numberitem draggable quiz"><span class="glyphicon glyphicon-sound-5-1"></span> <spring:message code="form.NumberSlider" /></li>
								<li id="drag_date" class="toolboxitem dateitem draggable quiz"><span class="glyphicon glyphicon-calendar"></span> <spring:message code="form.Date" /></li>
								<li id="drag_time" class="toolboxitem timeitem draggable"><span class="glyphicon glyphicon-time"></span> <spring:message code="form.Time" /></li>
							</c:when>
							<c:otherwise>
								<li id="drag_freetext" class="toolboxitem freetextitem draggable"><span class="glyphicon glyphicon-pencil"></span> <spring:message code="form.FreeText" /></li>
								<li id="drag_simplechoice" class="toolboxitem singlechoiceitem draggable"><span class="glyphicon glyphicon-ok-circle"></span> <spring:message code="form.SingleChoice" /></li>
								<li id="drag_multiplechoice" class="toolboxitem multiplechoiceitem draggable"><span class="glyphicon glyphicon-check"></span> <spring:message code="form.MultipleChoice" /></li>
								<li id="drag_number" class="toolboxitem numberitem draggable"><span class="glyphicon glyphicon-sound-5-1"></span> <spring:message code="form.NumberSlider" /></li>
								<li id="drag_date" class="toolboxitem dateitem draggable"><span class="glyphicon glyphicon-calendar"></span> <spring:message code="form.Date" /></li>
								<li id="drag_time" class="toolboxitem timeitem draggable"><span class="glyphicon glyphicon-time"></span> <spring:message code="form.Time" /></li>
								<c:if test="${form.survey.isDelphi}">
									<li id="drag_ranking" class="toolboxitem rankingitem draggable"><span class="glyphicon glyphicon-sort"></span> <spring:message code="form.RankingQuestion" /></li>
								</c:if>
							</c:otherwise>
						</c:choose>
						<li id="drag_matrix" class="toolboxitem matrixitem draggable"><span class="glyphicon glyphicon-list-alt"></span> <spring:message code="form.Matrix" /></li>
						<li id="drag_tableelement" class="toolboxitem mytableitem draggable"><span class="glyphicon glyphicon-list"></span> <spring:message code="form.Table" /></li>
					</ul>
				</div>
			</div>
			
			<div class="toolboxgroup" style="">
				<div class="toolboxheader"><a class="accordion-toggle" data-toggle="collapse" href="#collapseMedia" aria-expanded="true"><spring:message code="label.TextAndMedia" /></a></div>
				<div id="collapseMedia" class="accordion-body collapse in">
					<ul>
						<li id="drag_text" class="toolboxitem textitem draggable"><span class="glyphicon glyphicon-font"></span> <spring:message code="form.Text" /></li>
						<li class="toolboxitem imageitem draggable"><span class="glyphicon glyphicon-picture"></span> <spring:message code="form.Image" /></li>
						<li class="toolboxitem ruleritem draggable"><span class="glyphicon glyphicon-minus"></span> <spring:message code="form.Line" /></li>
					</ul>
				</div>
			</div>
			
			<div class="toolboxgroup" style="">
				<div class="toolboxheader"><a class="accordion-toggle" data-toggle="collapse" href="#collapseSpecial" aria-expanded="true"><spring:message code="label.Special" /></a></div>
				<div id="collapseSpecial" class="accordion-body collapse in">
					<ul>
						<li class="toolboxitem uploaditem draggable"><span class="glyphicon glyphicon-arrow-up"></span> <spring:message code="form.FileUpload" /></li>
						<li class="toolboxitem downloaditem draggable"><span class="glyphicon glyphicon-arrow-down"></span> <spring:message code="form.FileDownload" /></li>
						<li class="toolboxitem emailitem draggable"><span class="glyphicon glyphicon-envelope"></span> <spring:message code="label.Email" /></li>
						<li class="toolboxitem regexitem draggable"><span class="glyphicon glyphicon-asterisk"></span> <spring:message code="label.RegEx" /></li>
						<li class="toolboxitem galleryitem draggable"><span class="glyphicon glyphicon-th"></span> <spring:message code="form.Gallery" /></li>
						<li class="toolboxitem confirmationitem draggable"><span class="glyphicon glyphicon-ok"></span> <spring:message code="form.Confirmation" /></li>
						<li class="toolboxitem ratingitem draggable"><span class="glyphicon glyphicon-star"></span> <spring:message code="form.Rating" /></li>
					</ul>
				</div>
			</div>
			
			<div class="toolboxgroup" style="">
				<div class="toolboxheader"><a class="accordion-toggle" data-toggle="collapse" href="#collapsePredefined" aria-expanded="true"><spring:message code="label.Predefined" /></a></div>
				<div id="collapsePredefined" class="accordion-body collapse in">
					<ul>
						<li class="toolboxitem agenciesitem draggable"><span class="glyphicon glyphicon-star"></span> <spring:message code="form.predefined.euagencies" /></li>
						<li class="toolboxitem countriesitem draggable"><span class="glyphicon glyphicon-globe"></span> <spring:message code="form.predefined.eucountries" /></li>
						<li class="toolboxitem languagesitem draggable"><span class="glyphicon glyphicon-flag"></span> <spring:message code="form.predefined.eulanguages" /></li>
                                                <c:if test="${!oss}">
                                                    <li class="toolboxitem dgsitem draggable"><span class="glyphicon glyphicon-home"></span> <spring:message code="form.predefined.dgsnew" /></li>
                                                </c:if>                                                
						<li class="toolboxitem unsitem draggable"><span class="glyphicon"><img style="vertical-align: bottom;" src="${contextpath}/resources/images/unlogo.png" /></span> <spring:message code="form.predefined.uns" /></li>
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
			<a>
				<span id="elementpropertiescollapsebutton" class="glyphicon glyphicon-chevron-down" onclick="showHideElementProperties(this)"></span>
				<spring:message code="label.ElementProperties" />
			</a>
		</div>
		<div class="panecontent" style="width: 100%; min-width: 300px; top: 222px;">
			<div id="lockedElementInfo" style="display: none; padding: 20px;">
				<div style="float: right"><span class="glyphicon glyphicon-lock"></span></div>
				<spring:message code="info.ElementLocked" />				
			</div>
			<table id="properties">
				<tr class="firstpropertyrow" data-bind="attr: {class: Type() != '' ? '' : 'hideme'}">
					<td class="propertylabel"><spring:message code="label.Type" /></td>
					<td class="propertycontent" id="idTypesectionitem" style="font-style: italic;" data-bind="html: Type"></td>
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
	
	<%@ include file="../runner/elementtemplates.jsp" %>
	<%@ include file="edittemplates.jsp" %>
	<%@ include file="editdialogs.jsp" %>
	<%@ include file="../includes2.jsp" %>
	 
	<div class="modal" id="new-quiz-dialog" data-backdrop="static">
		<div class="modal-dialog">
		    <div class="modal-content">
			  <div class="modal-body">
			  	<div style="float: right">
			  		<img src="${contextpath}/resources/images/logo_Eusurvey.png" alt="EUSurvey logo" style="width:150px;" />
			  	</div>
			  	<div style="font-size: 20px; margin-bottom: 20px;">New Quiz Functionality</div>
			  	<b>This is the new Quiz mode.</b><br /><br /></li></ul><br />For further information, read our short documentation:
			  	<a id="docEditorGuideEN" target="_blank" href="${contextpath}/resources/documents/Quiz_Guide.pdf">Quiz Documentation</a>
			  	<a id="docEditorGuideFR" target="_blank" href="${contextpath}/resources/documents/Quiz_Guide_FR.pdf" style="display: none;">Quiz Documentation</a>
				<a id="docEditorGuideDE" target="_blank" href="${contextpath}/resources/documents/Quiz_Guide_DE.pdf" style="display: none;">Quiz Documentation</a>
			  </div>
			  <div class="modal-footer">
				<a class="btn btn-primary" onclick="disableNewQuizDialog()"><spring:message code="label.GotIt" /></a>		
			  </div>
			 </div>
		 </div>
	 </div>
	 
	 <div class="modal" id="askRestoreDialog" data-backdrop="static">
		<div class="modal-dialog">
		    <div class="modal-content">
			  <div class="modal-body">
			 	 <spring:message code="question.askRestoreSurvey" />
			  </div>
			  <div class="modal-footer">
				<a class="btn btn-primary" onclick="_actions.restore(); $('#askRestoreDialog').modal('hide')"><spring:message code="label.Yes" /></a>
				<a class="btn btn-default" onclick="$('#askRestoreDialog').modal('hide')"><spring:message code="label.No" /></a>		
				<a class="btn btn-default" onclick="_actions.deleteBackup(); $('#askRestoreDialog').modal('hide')"><spring:message code="label.DeleteLocalBackup" /></a>		
			  </div>
			 </div>
		 </div>
	 </div>
	 
	<script type="text/javascript">
		var surveyLanguage = "${form.survey.language.code}";
		var surveyShortname = "${form.survey.shortname}";
		var surveyUniqueId = "${form.survey.uniqueId}";
		var isQuiz = ${form.survey.isQuiz};
		var isECF = ${form.survey.isECF};
		var isOPC = ${form.survey.isOPC};
		var isDelphi = ${form.survey.isDelphi};
		var automaticNumbering = ${form.survey.sectionNumbering != 0 || form.survey.questionNumbering != 0};
		
		var lowLevel = '<spring:message code="label.Complexity.low" />';
		var mediumLevel = '<spring:message code="label.Complexity.medium" />';
		var highLevel = '<spring:message code="label.Complexity.high" />';
		var criticLevel = '<spring:message code="label.Complexity.toohigh" />';
		
		var isAdmin = false;
		<c:if test="${USER.getGlobalPrivilegeValue('FormManagement') == 2}">
			isAdmin = true;
		</c:if>
		
		$(function() {
			if (globalLanguage.toLowerCase() == "de")
			{
				$("#docEditorGuideEN").hide();
				$("#docEditorGuideDE").show();
			} else if (globalLanguage.toLowerCase() == "fr")
			{
				$("#docEditorGuideEN").hide();
				$("#docEditorGuideFR").show();
			}
		
			$("#form-menu-tab").addClass("active");
			$("#preview-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
			
			$('[data-toggle="tooltip"]').tooltip({
			    trigger : 'hover'
			});
			
			triggers = {};
	 		<c:forEach var="element" items="${form.survey.getElementsRecursive(true)}">
	 			triggers["${element.uniqueId}"] = "${element.triggers}";
	 		</c:forEach>
			
			loadElements();
			
			if (isQuiz)
			{
				$(".quiz").prepend("<span data-toggle='tooltip' title='<spring:message code='label.QuizQuestion' />' class='glyphicon glyphicon-education' style='float: right'></span>");
				$('.quiz').find('[data-toggle="tooltip"]').tooltip();
				
				if (localStorage != null)
				{
					if (localStorage.getItem("newquizmessageshown") == null)
					{
						$("#new-quiz-dialog").modal("show");
					}
				}
			}

			window.setTimeout(updateSessionTimeout, 10000);		
			
			<c:choose>
				<c:when test="${saved != null}">
					showSuccess("<spring:message code='message.SurveySaved' />");	
					
					//delete backup from local storage
					_actions.deleteBackup();
				</c:when>
				<c:otherwise>
					//restore backup
					var survey = $(document.getElementById("survey.id")).val();
					var name = "SurveyEditorBackup" + survey;   
					var value = localStorage.getItem(name);
					if (value != null)
					{
						$("#askRestoreDialog").modal("show");
					}
				</c:otherwise>
			</c:choose>
		});
		
		var _elements = {};
		
		function disableNewQuizDialog()
		{
			$("#new-quiz-dialog").modal("hide");
			if (localStorage != null)
			{
				localStorage.setItem("newquizmessageshown", true);
			}
		}
		
		function loadElements()
		{
			var ids = "";
			
			if ($(".emptyelement").length > 0)
			{
				var counter = 0;
				
				$(".emptyelement").each(function(){
					ids += $(this).attr("data-id") + '-';
					counter++;
					if (counter > 20)
					{
						return false;	
					}
				})	
					 
			 	var s = "ids=" + ids.substring(0, ids.length-1) + "&survey=${form.survey.id}&slang=${form.language.code}&as=${answerSet}&foreditor=true";
				
				$.ajax({
					type:'GET',
					dataType: 'json',
					url: "${contextpath}/runner/elements/${form.survey.id}",
					data: s,
					cache: false,
					success: function( result ) {	
						for (var i = 0; i < result.length; i++)
						{
							var model = getElementViewModel(result[i]);
							var item = addElement(model, true, false);
							_elements[model.id()] = model;
							addElementHandler(item);
						}
						
						//applyStandardWidths();
						setTimeout(loadElements, 500);
					}
				});
			} else {
				createNavigation(true);
				updateDependenciesView();
				_actions.AllElementsLoaded(true);
				
				$.ajax({type: "GET",
					url: contextpath + "/administration/system/complexity",
				    success :function(result)
				    {
				    	complexitySettings = result;			    	
				    	scanSurveyComplexity();
				    }
				 });
			}
		}
		
		function getMinMaxCharacters(min,max)
	 	{
	 		var s = '<spring:message code="limits.MinMaxCharacters" arguments="[min],[max]" />';
	 		return s.replace("[min]", min).replace("[max]", max);
	 	}
	 	function getMinCharacters(min)
	 	{
	 		var s = '<spring:message code="limits.MinCharacters" arguments="[min]" />';
	 		return s.replace("[min]", min);
	 	}
	 	function getMaxCharacters(max)
	 	{
	 		var s = '<spring:message code="limits.MaxCharacters" arguments="[max]" />';
	 		return s.replace("[max]", max);
	 	}	 	
	 	function getMinMaxChoice(min,max)
	 	{
	 		var s = '<spring:message code="limits.MinMaxChoices" arguments="[min],[max]" />';
	 		return s.replace("[min]", min).replace("[max]", max);
	 	}
	 	function getMinChoice(min)
	 	{
	 		var s = '<spring:message code="limits.MinChoices" arguments="[min]" />';
	 		return s.replace("[min]", min);
	 	}
	 	function getMaxChoice(max)
	 	{
	 		var s = '<spring:message code="limits.MaxChoices" arguments="[max]" />';
	 		return s.replace("[max]", max);
	 	}
	
		function getMinMax(min,max)
	 	{
	 		var s = '<spring:message code="limits.MinMaxNumber" arguments="[min],[max]" />';
	 		return s.replace("[min]", round(min)).replace("[max]", round(max));
	 	}
	 	function getMin(min)
	 	{
	 		var s = '<spring:message code="limits.MinNumber" arguments="[min]" />';
	 		return s.replace("[min]", min);
	 	}
	 	function getMax(max)
	 	{
	 		var s = '<spring:message code="limits.MaxNumber" arguments="[max]" />';
	 		return s.replace("[max]", max);
	 	}	 	
		
	 	function getMinMaxDate(min,max)
	 	{
	 		var s = '<spring:message code="limits.MinMaxDate" arguments="[min],[max]" />';
	 		return s.replace("[min]", min).replace("[max]", max);
	 	}
	 	function getMinDate(min)
	 	{
	 		var s = '<spring:message code="limits.MinDate" arguments="[min]" />';
	 		return s.replace("[min]", min);
	 	}
	 	function getMaxDate(max)
	 	{
	 		var s = '<spring:message code="limits.MaxDate" arguments="[max]" />';
	 		return s.replace("[max]", max);
	 	}
	 	
	 	function getMinMaxRows(min,max)
	 	{
	 		var s = '${form.getMessage("limits.MinMaxRows", "[min]","[max]")}';
	 		return s.replace("[min]", min).replace("[max]", max);
	 	}
	 	function getMinRows(min)
	 	{
	 		var s = '${form.getMessage("limits.MinRows", "[min]")}';
	 		return s.replace("[min]", min);
	 	}
	 	function getMaxRows(max)
	 	{
	 		var s = '${form.getMessage("limits.MaxRows", "[max]")}';	 		
	 		return s.replace("[max]", max);
	 	}
	 	
	 	function getMaxSelections(max)
	 	{
	 		var s = '<spring:message code="limits.MaxSelections" arguments="[max]" />';
	 		return s.replace("[max]", max);
	 	}
	 	
	 	function getElementType(element) {
			if (element.hasClass("sectionitem")) return "<spring:message code='form.Section' />";
			if (element.hasClass("freetextitem")) return "<spring:message code='form.FreeText' />";
			if (element.hasClass("singlechoiceitem")) return "<spring:message code='form.SingleChoice' />";
			if (element.hasClass("multiplechoiceitem")) return "<spring:message code='form.MultipleChoice' />";
			if (element.hasClass("numberitem")) return "<spring:message code='form.NumberSlider' />";
			if (element.hasClass("matrixitem")) return "<spring:message code='form.Matrix' />";
			if (element.hasClass("mytableitem")) return "<spring:message code='form.Table' />";
			if (element.hasClass("dateitem")) return "<spring:message code='form.Date' />";
			if (element.hasClass("timeitem")) return "<spring:message code='form.Time' />";
			if (element.hasClass("textitem")) return "<spring:message code='form.Text' />";
			if (element.hasClass("imageitem")) return "<spring:message code='form.Image' />";
			if (element.hasClass("ruleritem")) return "<spring:message code='form.Line' />";
			if (element.hasClass("uploaditem")) return "<spring:message code='form.FileUpload' />";
			if (element.hasClass("downloaditem")) return "<spring:message code='form.FileDownload' />";
			if (element.hasClass("emailitem")) return "<spring:message code='label.Email' />";
			if (element.hasClass("regexitem")) return "<spring:message code='label.RegEx' />";
			if (element.hasClass("galleryitem")) return "<spring:message code='form.Gallery' />";
			if (element.hasClass("confirmationitem")) return "<spring:message code='form.Confirmation' />";
			if (element.hasClass("ratingitem")) return "<spring:message code='form.Rating' />";
			if (element.hasClass("ratingquestion")) return "<spring:message code='form.RatingQuestion' />";
			if (element.hasClass("matrix-header")) return "<spring:message code='form.MatrixElement' />";
			if (element.hasClass("table-header")) return "<spring:message code='form.Table' />";
			if (element.hasClass("answertext")) return "<spring:message code='label.Answer' />";
			if (element.hasClass("rankingitem")) return "<spring:message code='label.RankingQuestion' />";
			if (element.hasClass("rankingitemtext")) return "<spring:message code='label.RankingItem' />";
			if (element.find(".gallery-image").length > 0) return "<spring:message code='form.GalleryImage' />";
			return "Template";
		}
	 	
	 	function getElementTypeAsId(element) {
			if (element.hasClass("sectionitem")) return "idTypesectionitem";
			if (element.hasClass("freetextitem")) return "idTypefreetextitem";
			if (element.hasClass("singlechoiceitem")) return "idTypesinglechoiceitem";
			if (element.hasClass("multiplechoiceitem")) return "idTypemultiplechoiceitem";
			if (element.hasClass("numberitem")) return "idTypenumberitem";
			if (element.hasClass("matrixitem")) return "idTypematrixitem";
			if (element.hasClass("mytableitem")) return "idTypemytableitem";
			if (element.hasClass("dateitem")) return "idTypedateitem";
			if (element.hasClass("timeitem")) return "idTypetimeitem";
			if (element.hasClass("textitem")) return "idTypetextitem";
			if (element.hasClass("imageitem")) return "idTypeimageitem";
			if (element.hasClass("ruleritem")) return "idTyperuleritem";
			if (element.hasClass("uploaditem")) return "idTypeuploaditem";
			if (element.hasClass("downloaditem")) return "idTypedownloaditem";
			if (element.hasClass("emailitem")) return "idTypeemailitem";
			if (element.hasClass("regexitem")) return "idTyperegexitem";
			if (element.hasClass("galleryitem")) return "idTypeSectionitem";
			if (element.hasClass("confirmationitem")) return "idTypegalleryitem";
			if (element.hasClass("ratingitem")) return "idTyperatingitem";
			if (element.hasClass("ratingquestion")) return "idTyperatingquestion";
			if (element.hasClass("matrix-header")) return "idTypematrix-header";
			if (element.hasClass("table-header")) return "idTypetable-header";
			if (element.find(".gallery-image").length > 0) return "idTypegallery-image";
			return "idOtherType";
		}	 	
	 	
	 	var labelAddFileForDownload = '<spring:message code="message.AddFileForDownload" />';
	 	
	 	function getPropertyLabel(label)
	 	{
	 		if (!isNaN(label)) return label;
	 		
	 		var strings = new Array();
	 		strings["Text"] = "<spring:message code="label.Text" />";
	 		strings["Rows"] = "<spring:message code="label.Rows" />";
	 		strings["TabTitle"] = "<spring:message code="label.TabTitle" />";
	 		strings["Level"] = "<spring:message code="label.Level" />";
	 		strings["Visibility"] = "<spring:message code="label.Visibility" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.visibility" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
	 		strings["alwaysVisible"] = "<spring:message code="label.alwaysVisible" />";
	 		strings["dependent"] = "<spring:message code="label.dependent" />";
	 		strings["Mandatory"] = "<spring:message code="label.Mandatory" />";
	 		strings["Help"] = "<spring:message code="label.HelpMessage" />";
	 		strings["Identifier"] = "<spring:message code="label.Identifier" />";
	 		var readonly = '<spring:message code="info.Readonly" />'.replace('"',"`");
	 		strings["ReadOnly"] = "<spring:message code="label.Readonly" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='" + readonly + "'><span class='glyphicon glyphicon-question-sign'></span></a>";
	 		strings["AcceptedNumberOfCharacters"] = "<spring:message code="label.AcceptedNumberOfCharacters" />";
	 		strings["between"] = "<spring:message code="label.between" />";
	 		strings["and"] = "<spring:message code="label.and" />";
	 		strings["Unique"] = "<spring:message code="label.Unique" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.Unique" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
	 		strings["Comparable"] = "<spring:message code="label.Comparable" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.Comparable" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
	 		var info = "<spring:message code="info.Password" />".replace("'","`");
	 		strings["Password"] = "<spring:message code="label.Password" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='" + info + "'><span class='glyphicon glyphicon-question-sign'></span></a>";
	 		strings["Attribute"] = "<spring:message code="label.Attribute" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.Attribute" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
	 		strings["Name"] = "<spring:message code="label.Name" />";
	 		strings["RegistrationForm"] = "<spring:message code="label.RegistrationForm" />";
	 		strings["Advanced"] = "<spring:message code="label.Advanced" />";
	 		strings["invalidMinMaxCharacters"] = "<spring:message code="error.invalidMinMaxCharacters" />";
	 		strings["Style"] = "<spring:message code="label.Style" />";
	 		strings["Order"] = "<spring:message code="label.Order" />&nbsp;<a data-toggle='tooltip' data-html='true' data-placement='right' title='<spring:message code="info.Order" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
	 		strings["Columns"] = "<spring:message code="label.Columns" />";
	 		strings["RadioButton"] = "<spring:message code="html.RadioButton" />";
	 		strings["SelectBox"] = "<spring:message code="html.SelectBox" />";	 
	 		strings["LikertScale"] = "<spring:message code="html.LikertScale" />";
	 		strings["Original"] = "<spring:message code="label.OriginalOrder" />";
	 		strings["Alphabetical"] = "<spring:message code="label.AlphabeticalOrder" />";
	 		strings["Random"] = "<spring:message code="label.RandomOrder" />";
	 		strings["PossibleAnswers"] = "<spring:message code="label.PossibleAnswers" />";
	 		strings["RankingItems"] = "<spring:message code="label.RankingItems" />";
	 		strings["NumberOfChoices"] = "<spring:message code="label.NumberOfChoices" />";
	 		strings["CheckBox"] = "<spring:message code="html.CheckBox" />";
	 		strings["ListBox"] = "<spring:message code="html.ListBox" />";	 
	 		strings["Unit"] = "<spring:message code="label.Unit" />";	 
	 		strings["DecimalPlaces"] = "<spring:message code="label.DecimalPlacesNew" />";
	 		strings["DisplaySlider"] = "<spring:message code="label.Display" />";	 
	 		strings["MinLabel"] = "<spring:message code="label.MinLabel" />";	 
	 		strings["MaxLabel"] = "<spring:message code="label.MaxLabel" />";	 
	 		strings["InitialSliderPosition"] = "<spring:message code="label.InitialSliderPosition" />";	 
	 		strings["DisplayGraduationScale"] = "<spring:message code="label.DisplayGraduationScale" />";	 
	 		strings["Number"] = "<spring:message code="label.Number" />";	 
	 		strings["Slider"] = "<spring:message code="label.Slider" />";
	 		strings["Left"] = "<spring:message code="label.Left" />";
	 		strings["Middle"] = "<spring:message code="label.Middle" />";
	 		strings["Right"] = "<spring:message code="label.Right" />";
	 		
	 		strings["Values"] = "<spring:message code="label.Values" />";
	 		strings["NumberOfAnsweredRows"] = "<spring:message code="label.NumberOfAnsweredRows" />";
	 		strings["Interdependency"] = "<spring:message code="label.Interdependency" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.Interdependency" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
	 		strings["Size"] = "<spring:message code="label.Size" />";
	 		strings["SizeInfo"] = "&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.Size" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
	 		strings["fitToContent"] = "<spring:message code="label.FitToContent" />";
	 		strings["fitToPage"] = "<spring:message code="label.FitToPage" />";
	 		strings["manualColumnWidth"] = "<spring:message code="label.ManualColumnWidth" />";
	 		strings["dropelementhere"] = "<spring:message code="label.DropElementHere" />";
	 		strings["Align"] = "<spring:message code="label.Align" />";
	 		strings["LongDescription"] = "<spring:message code="label.LongDesc" />";
	 		strings["LongDescriptionInfo"] = "&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.LongDescription" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
	 		strings["DescriptiveText"] = "<spring:message code="label.DescriptiveText" />";
	 		strings["DescriptiveTextInfo"] = "&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.DescriptiveText" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
	 		strings["UploadFile"] = "<spring:message code="label.UploadFile" />";
	 		strings["UploadFiles"] = "<spring:message code="label.UploadFiles" />";
	 		strings["left"] = "<spring:message code="label.left" />";
	 		strings["right"] = "<spring:message code="label.right" />";
	 		strings["center"] = "<spring:message code="label.center" />";
	 		strings["File"] = "<spring:message code="label.File" />";
	 		strings["RegularExpression"] = "<spring:message code="label.RegEx" />";
	 		strings["RegularExpressionInfo"] = "&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.RegEx" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
	 		strings["MaxSelections"] = "<spring:message code="label.MaxNumberOfSelections" />";
	 		strings["ImageSelectable"] = "<spring:message code="label.ImageSelectable" />";
	 		strings["ConfirmationText"] = "<spring:message code="label.ConfirmationText" />";
	 		strings["LabelText"] = "<spring:message code="label.LabelText" />";
	 		strings["PleaseSelectTriggers"] = "<spring:message code="label.PleaseSelectTriggers" />";
	 		strings["PleaseChooseLogic"] = "<spring:message code="label.PleaseChooseLogic" /> &nbsp;<a data-toggle='tooltip' data-html='true' data-placement='bottom' title='<spring:message code="info.PleaseChooseLogic" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
	 		strings["visibleIfTriggered"] = "<spring:message code="label.visibleIfTriggered" />";
	 		strings["visibleIfTriggeredAnd"] = "<spring:message code="label.visibleIfTriggeredAnd" />"
	 		strings["Dependencies"] = "<spring:message code="label.Dependencies" />";
	 		strings["invalidMinMaxCharacters"] = "<spring:message code="error.invalidMinMaxCharacters" />";
	 		strings["invalidMinMaxChoice"] = "<spring:message code="error.invalidMinMaxChoices" />";
	 		strings["invalidMatrixRows2"] = "<spring:message code="error.invalidMatrixRows2" />";
	 		strings["invalidNumber"] = "<spring:message code="validation.invalidNumber" />";
	 		strings["invalidNumber5k"] = "<spring:message code="validation.textTooLong5000" />";
	 		strings["invalidDate"] = "<spring:message code="validation.invalidDate" />";
	 		strings["invalidTime"] = "<spring:message code="validation.invalidTime" />";
	 		strings["invalidStartEnd"] = "<spring:message code="validation.invalidStartEnd" />";
	 		strings["invalidStartEndTime"] = "<spring:message code="validation.invalidStartEndTime" />";
	 		strings["invalidMinMaxNumber"] = "<spring:message code="error.invalidMinMaxNumber" />";
	 		strings["invalidMinMaxEqual"] = "<spring:message code="error.invalidMinMaxEqual" />";
	 		strings["NotUniqueAnswers"] = "<spring:message code="validation.NotUniqueAnswers" />";
	 		strings["required"] = "<spring:message code="validation.required" />";
	 		strings["Edit"] = "<spring:message code="label.Edit" />";
	 		strings["AssignValues"] = "<spring:message code="label.AssignValues" />";
	 		strings["Type"] = "<spring:message code="label.Type" />";
	 		strings["NoTriggersFound"] = "<spring:message code="info.NoTriggersFound" />";
	 		strings["SingleChoice"] = "<spring:message code="form.SingleChoice" />";
	 		strings["MultipleChoice"] = "<spring:message code="form.MultipleChoice" />";
	 		strings["globalmaxinvalid"] = "<spring:message code="error.MaxBiggerThanElements" />";
	 		strings["globalmininvalid"] = "<spring:message code="error.MinBiggerThanElements" />";
		 	strings["bycolumn"] = "<spring:message code="label.bycolumn" />";
	 		strings["bycell"] = "<spring:message code="label.bycell" />";
	 		strings["TableAnswers1"] = "<spring:message code="validation.TableAnswers1" />";
	 		strings["TableAnswers"] = "<spring:message code="validation.TableAnswers" />";
	 		strings["TableQuestions1"] = "<spring:message code="validation.TableQuestions1" />";
	 		strings["TableQuestions"] = "<spring:message code="validation.TableQuestions" />";
	 		strings["invalidURL"] = "<spring:message code="validation.invalidURL" />";
	 		strings["ConfirmationFileInfo"]= "&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.ConfirmationFileInfo" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
	 		strings["ConfirmationTextInfo"]= "&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.ConfirmationTextInfo" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
	 		strings["GallerySelections"] = "<spring:message code="validation.invalidGallerySelections" />";
	 		strings["xhtmlinvalid"] = "<spring:message code="label.InvalidXHTML" />";
	 		strings["FileNameAlreadyExists"] = "<spring:message code="error.FileNameAlreadyExists" />";
	 		strings["Save"] = "<spring:message code="label.Save" />";
	 		strings["Cancel"] = "<spring:message code="label.Cancel" />";
	 		strings["Apply"] = "<spring:message code="label.Apply" />";
	 		strings["before"] = "<spring:message code="label.before" />";
	 		strings["after"] = "<spring:message code="label.after" />";
	 		strings["min"] = "<spring:message code="label.min" />";
	 		strings["max"] = "<spring:message code="label.max" />";
	 		strings["File"] = "<spring:message code="label.File" />";
	 		strings["UnsavedChanges"] = "<spring:message code="message.UnsavedChanges" />";
	 		strings["NameInvalid"] = "<spring:message code="error.NameInvalid" />".replace("&lt;","<").replace("&gt;",">").replace("&amp;","&");
	 		strings["differentVisibility"] = "<spring:message code="info.differentVisibilitySelections" />";
	 		strings["Title"] = "<spring:message code="label.Title" />";
	 		strings["AutoNumbering"] = "<spring:message code="label.AutomaticNumbering" />";
	 		strings["visibleIfMatrixVisible"] = "<spring:message code="label.visibleIfMatrixVisible" />";
	 		strings["visibleIfMatrixOrTriggered"] = "<spring:message code="label.visibleIfMatrixOrTriggered" />";
	 		strings["checkVisibilities"] = "<spring:message code="info.checkVisibilities" />";
	 		strings["invalidMatrixChildren"] = "<spring:message code="validation.invalidMatrixChildren1" />";
	 		strings["invalidTableChildren"] = "<spring:message code="validation.invalidTableChildren1" />";
	 		strings["invalidMatrixRows"] = "<spring:message code="error.invalidMatrixRows" />";
	 		strings["Scoring"] = "<spring:message code="label.Scoring" />";
	 		strings["Answers"] = "<spring:message code="label.Answers" />";
	 		strings["Rules"] = "<spring:message code="label.Rules" />";
	 		strings["lessThan"] = "<spring:message code="quiz.lessThan" />";
	 		strings["lessThanDate"] = "<spring:message code="label.before" />";
	 		strings["lessThanOrEqualTo"] = "<spring:message code="quiz.lessThanOrEqualTo" />";
	 		strings["greaterThan"] = "<spring:message code="quiz.greaterThan" />";
	 		strings["greaterThanDate"] = "<spring:message code="label.after" />";
	 		strings["greaterThanOrEqualTo"] = "<spring:message code="quiz.greaterThanOrEqualTo" />";
	 		strings["equalTo"] = "<spring:message code="quiz.equalTo" />";
	 		strings["equalToDate"] = "<spring:message code="quiz.equalTo" />";
	 		strings["other"] = "<spring:message code="quiz.other" />";
	 		strings["otherDate"] = "<spring:message code="quiz.other" />";
	 		strings["betweenDate"] = "<spring:message code="label.between" />";
	 		strings["invalidrulelimit"] = "<spring:message code="quiz.invalidrulelimit" />";
	 		strings["checkRules"] = "<spring:message code="quiz.checkRules" />";
	 		strings["identifierExists"] = "<spring:message code="validation.identifierExists" />";
	 		strings["emptylastcolumn"] = "<spring:message code="validation.emptylastcolumn" />";
	 		strings["numberinvaliddecimals"] = "<spring:message code="validation.numberinvaliddecimals" />";
	 		strings["invalidPositiveNumber"] = "<spring:message code="validation.invalidPositiveNumber" />";
	 		strings["max255Characters"] = "<spring:message code="validation.max255Characters" />";
	 		strings["ECFProfileSelection"] = "<spring:message code="label.ECFProfileSelection" />";
	 		strings["ECFCompetencyQuestion"] = "<spring:message code="label.ECFCompetencyQuestion" />";
			strings["ECFSelectedCompetency"] = "<spring:message code="label.ECFSelectedCompetency" />";
			strings["ECFSelectedProfile"] = "<spring:message code="label.ECFSelectedProfile" />";
	 		strings["QuizQuestion"] = "<spring:message code="label.QuizQuestion" />";
	 		strings["Points"] = "<spring:message code="label.Points" />";
	 		strings["empty"] = "<spring:message code="label.empty" />";
	 		strings["emptyDate"] = "<spring:message code="label.empty" />";
	 		strings["FileType"] = "<spring:message code="label.FileType" />";
	 		strings["FileTypeInfo"]= "&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.FileTypeInfo" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
	 		strings["noblanks"] = "<spring:message code="validation.noblanks" />";
	 		strings["NumIcons"] = "<spring:message code="label.NumIcons" />";
	 		strings["IconType"] = "<spring:message code="label.IconType" />";
	 		strings["Stars"] = "<spring:message code="label.Stars" />";
	 		strings["Circles"] = "<spring:message code="label.Circles" />";
	 		strings["Hearts"] = "<spring:message code="label.Hearts" />";
	 		strings["RatingQuestions"] = "<spring:message code="validation.RatingQuestions" />";
	 		strings["Questions"] = "<spring:message code="label.Questions" />";
	 		strings["Remove"] = "<spring:message code="label.Remove" />";
	 		strings["solid"] = "<spring:message code="html.solid" />";
	 		strings["dashed"] = "<spring:message code="html.dashed" />";
	 		strings["dotted"] = "<spring:message code="html.dotted" />";
	 		strings["Height"] = "<spring:message code="label.Height" />";
	 		strings["Color"] = "<spring:message code="html.Color" />";
	 		strings["duplicateattributename"] = "<spring:message code="validation.duplicateattributename" />";
	 		
	 		strings["Display"] = "<spring:message code="label.Display" />&nbsp;<a data-toggle='tooltip' data-html='true' data-placement='right' title='<spring:message code="info.Display" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
	 		strings["checkNumberOfChoices"] = "<spring:message code="validation.checkNumberOfChoices" />";
	 		strings["checkNumberOfRows"] = "<spring:message code="validation.checkNumberOfRows" />";
	 		strings["CountryOnly"] = "<spring:message code="label.CountryOnly" />";
	 		strings["Country+ISO"] = "<spring:message code="label.Country+ISO" />";
	 		strings["ISO+Country"] = "<spring:message code="label.ISO+Country" />";
	 		strings["ISOOnly"] = "<spring:message code="label.ISOOnly" />";	 		
	 		
	 		strings["MaximumFileSize"] = "<spring:message code="label.MaximumFileSize" />";
	 		strings["DelphiQuestion"] = "<spring:message code="label.DelphiQuestion" />";
			strings["DelphiChartType"] = "<spring:message code="label.DelphiChartType" />";
			strings["DelphiChartTypeNumber"] = "<spring:message code="label.DelphiChartType" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.DelphiChartTypeNumber" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
	 		strings["Bar"] ="<spring:message code="label.DelphiChartBar" />";
			strings["Column"] ="<spring:message code="label.DelphiChartColumn" />";
			strings["Line"] ="<spring:message code="label.DelphiChartLine" />";
			strings["Pie"] ="<spring:message code="label.DelphiChartPie" />";
			strings["Radar"] ="<spring:message code="label.DelphiChartRadar" />";
			strings["Scatter"] ="<spring:message code="label.DelphiChartScatter" />";
			strings["MaxDistanceToMedian"] ="<spring:message code="label.MaxDistanceToMedian" />&nbsp;<a data-toggle='tooltip' data-html='true' data-placement='right' title='<spring:message code="info.MaxDistanceToMedian" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
			strings["Ignore"] ="<spring:message code="label.Ignore" />";
			strings["None"] ="<spring:message code="label.None" />";
			strings["WordCloud"] ="<spring:message code="label.DelphiChartWordCloud" />";
			strings["ShowExplanationBox"] = "<spring:message code="label.ShowExplanationBox" />";
			
	 		return strings[label];
	 	}
	</script>

</body>
</html>
