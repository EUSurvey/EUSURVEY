<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

	<div class="modal" id="askSectionVisibilityDialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-body">      
					<spring:message code="question.ApplyVisibilityToChildren" />
				</div>
				<div class="modal-footer">
					<a  class="btn btn-primary" onclick="$('#askSectionVisibilityDialog').modal('hide');updateVisibility(selectedspan, false, false, true);"><spring:message code="label.Yes" /></a>
					<a  class="btn btn-default" onclick="$('#askSectionVisibilityDialog').modal('hide');updateVisibility(selectedspan, false, false, false);"><spring:message code="label.No" /></a>                
				</div>
			</div>
		</div>
	</div>
	
	<div class="modal" id="invalid-regform-dialog" data-backdrop="static">
	<div class="modal-dialog modal-sm">
    <div class="modal-content">
	  <div class="modal-body">	
	  	<spring:message code="message.InvalidRegform" />
	  </div>
	  <div class="modal-footer">
		<a  class="btn btn-primary" onclick="$('#invalid-regform-dialog').modal('hide');internalSave(internalClose)"><spring:message code="label.Yes" /></a>
		<a  class="btn btn-default" onclick="$('#invalid-regform-dialog').modal('hide');"><spring:message code="label.No" /></a>		
	  </div>
	 </div>
	 </div>
	 </div>
	 
	 <div class="modal" id="confirm-delete-dialog" data-backdrop="static">
		<div class="modal-dialog">
	    <div class="modal-content">
	    <div class="modal-header"><spring:message code="label.ConfirmDeletion" /></div>
		  <div class="modal-body" style="overflow-y: auto">
		  	<span style="font-weight: bold;"><spring:message code="info.DeleteElement" /></span>
		  	<div id="confirm-delete-dialog-body"></div>
		  </div>
		  <div class="modal-footer">
			<a id="btnConfirmDeleteElementFromEdit" class="btn btn-primary" onclick="_actions.deleteElement2(false);"><spring:message code="label.OK" /></a>		
			<a id="btnCancelDeleteElementFromEdit" class="btn btn-default" onclick="$('#confirm-delete-dialog').modal('hide');"><spring:message code="label.Cancel" /></a>			
		  </div>
		 </div>
		 </div>
	</div>
	
	<div class="modal" id="confirm-delete-multiple-dialog" data-backdrop="static">
		<div class="modal-dialog">
	    <div class="modal-content">
	    <div class="modal-header"><spring:message code="label.ConfirmDeletion" /></div>
		  <div class="modal-body" class="modal150" style="max-height: 500px; overflow: auto">
		  		<span id="confirm-delete-multiple-count" style="font-weight: bold;"><spring:message code="info.DeleteElements" /></span>
		  		<div id="confirm-delete-multiple-dialog-body"></div>
		  </div>
		  <div class="modal-footer">
			<a  id="btnConfirmDeleteMultiElementFromEdit" class="btn btn-primary" onclick="_actions.deleteElement2(false);"><spring:message code="label.OK" /></a>		
				<a id="btnCancelDeleteElementFromEdit"  class="btn btn-default" onclick="$('#confirm-delete-multiple-dialog').modal('hide');"><spring:message code="label.Cancel" /></a>			
		  </div>
		 </div>
		 </div>
	</div>
	
	<div id="checkChangesDialog" class="modal fade" tabindex="-1" role="dialog">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-body">
	        <spring:message code="question.SaveChanges" />
	      </div>
	      <div class="modal-footer">
	      	<a onclick="_actions.UnsavedChangesConfirmed(true); $('#checkChangesDialog').modal('hide'); saveForm(false);" id="checkChangesDialogSaveButton" class="btn btn-default btn-primary"><spring:message code="label.Save" /></a>
	        <a onclick="_actions.UnsavedChangesConfirmed(true); return true;" id="checkChangesDialogDontSaveButton" href="${contextpath}/${form.survey.shortname}/management/test" class="btn btn-default"><spring:message code="label.DontSave" /></a>
	        <button onclick="$('#editorredirect').val('');" type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></button>
	      </div>
	    </div>
	  </div>
	</div>
	
	<div class="modal" id="invalid-dependency-dialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
	    <div class="modal-content">
		  <div class="modal-body">	
		  	<spring:message code="message.InvalidDependencyNew" />
		  </div>
		  <div class="modal-footer">
			<a  class="btn btn-primary" onclick="$('#invalid-dependency-dialog').modal('hide');"><spring:message code="label.OK" /></a>			
		  </div>
		</div>
		</div>
	</div>	
	
	<div class="modal" id="busydialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
	    <div class="modal-content">
		<div class="modal-body" style="padding-left: 30px; text-align: center">		
			<spring:message code="label.PleaseWait" /><br /><br />
			<img alt="wait animation" src="${contextpath}/resources/images/ajax-loader.gif" />
		</div>
		</div>
		</div>
	</div>
	
	<div class="modal" id="askcutsectiondialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
	    <div class="modal-content">
		  <div class="modal-body">
		  	<spring:message code="question.MoveSectionWithContent" />
		  </div>
		  <div class="modal-footer">
				<a  class="btn btn-primary" onclick="_actions.cutSection(true);"><spring:message code="label.Yes" /></a>		
				<a  class="btn btn-default" onclick="_actions.cutSection(false);"><spring:message code="label.No" /></a>			
		  </div>
		</div>
		</div>
	</div>
	
	<div class="modal" id="askcopysectiondialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
	    <div class="modal-content">
		  <div class="modal-body">
		  	<spring:message code="question.CopySectionWithContent" />
		  </div>
		  <div class="modal-footer">
				<a  class="btn btn-primary" onclick="_actions.copySection(true);"><spring:message code="label.Yes" /></a>		
				<a  class="btn btn-default" onclick="_actions.copySection(false);"><spring:message code="label.No" /></a>			
		  </div>
		</div>
		</div>
	</div>
