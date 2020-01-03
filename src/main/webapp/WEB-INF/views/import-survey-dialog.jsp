<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>	

<div class="modal" id="import-survey-dialog" data-backdrop="static">
	 <div class="modal-dialog">
    <div class="modal-content">
	<div class="modal-header">
		<spring:message code="label.SelectSurveyToImport" />
	</div>
	  <div class="modal-body">	
		  	<div class="file-uploader importsurveyuploader">
			</div>
	  </div>
	  <div class="modal-footer">
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
	  </div>
	  </div>
	  </div>
</div>

<div class="modal" id="import-survey-dialog-2" data-backdrop="static">
	   <div class="modal-dialog">
    <div class="modal-content">
	  <div class="modal-header">
	  	<spring:message code="info.importsuccessful" />
	  </div>
	  <div class="modal-body">	
	  	<p class="hideme" id="import-survey-dialog-2-invalidCodeFound"><spring:message code="info.invalidCodeFound" /></p>
		<p><spring:message code="question.FormImported" /></p>
	  </div>
	  <div class="modal-footer">
	  	<a onclick="goToSurvey();" id="import-survey-dialog-2-yes" class="btn btn-primary"><spring:message code="label.Yes" /></a>	
	  	<a onclick="$('#import-survey-dialog-2').modal('hide'); reloadSurveys();" id="import-survey-dialog-2-no" class="btn btn-default"><spring:message code="label.No" /></a>	
	  </div>
	  </div>
	  </div>
</div>