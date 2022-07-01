<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="modal" id="add-translation-dialog" data-backdrop="static">
	<form:form action="${contextpath}/${sessioninfo.shortname}/management/addtranslation" method="post" style="margin: 0px;">
		<div class="modal-dialog">
	    	<div class="modal-content">
				<div class="modal-header"><spring:message code="label.AddNewTranslation" /></div>
				<div class="modal-body">
					<spring:message code="label.SelectLanguageForNewTranslation" /><br /><br />
					
					<div style="float: left">
						<select class="form-control" style="display: inline; width: auto" id="lang" name="lang" onchange="checkLanguage()">
							<option value="select"><spring:message code="label.SelectLanguage" /></option>
							<c:forEach items="${languages}" var="language">				
								<c:if test="${language.official}">				
									<option value="<esapi:encodeForHTMLAttribute>${language.code}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML>${language.code} - <spring:message code="label.lang.${language.englishName}" /></esapi:encodeForHTML></option>
								</c:if>
							</c:forEach>
							<option value="other"><spring:message code="label.Other" /></option>							
						</select>
					</div>	
					
					<div id="otherlang" style="float: left; margin-left: 10px;">
						<spring:message code="label.ISOCode" />
						<input type="text" class="form-control" maxlength="2" name="code" id="code" style="display: inline; width: 60px; margin-left: 5px;" />
					</div>
					
					<div style="clear: both"></div>
					
					<div id="unknown-language-error" class="validation-error hideme" style="margin-top: 5px"><spring:message code="error.LanguageCodeNotRecognized" /></div>
					<div id="add-translation-dialog-error" class="validation-error hideme" style="margin-top: 5px"><spring:message code="error.selectlanguagetoadd" /></div>
					<div id="unsupported-language-error" class="validation-error hideme" style="margin-top: 5px"><spring:message code="error.LanguageNotSupported" /></div>
					
					<c:if test="${isMTAvailable}">					
						<div id="requestTranslation" style="margin-top: 20px">
							<input type="checkbox" class="check" name="request" id="mtrequestcheck" />
							<spring:message code="label.RequestLanguageTranslation" />
							<a style="margin-left: 5px" onclick="$(this).parent().find('.help').toggle()"><span class="glyphicon glyphicon-info-sign"></span></a>
							<div class="help" style="color: #000; text-align: justify; display: none; margin-top: 10px;">
								<span><spring:message code="message.RequestLanguageTranslation" /></span>	
							</div>
						</div>						
					</c:if>					
					
				</div>
				<div class="modal-footer">						
					<input onclick="return checkAddLanguage()" type="submit" class="btn btn-primary" value="<spring:message code="label.OK" />" />	
					<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
				</div>
			</div>
		</div>
	</form:form>
</div>

<div class="modal" id="search-replace-dialog" data-backdrop="static">
	<div class="modal-dialog modal-sm">
    <div class="modal-content">
	<div class="modal-header"><spring:message code="label.SearchAndReplace" /></div>
	<div class="modal-body">
		<spring:message code="label.SearchWhat" />
		<input id="search-replace-dialog-search" type="text" /><br /><br />
		<spring:message code="label.ReplaceWith" />
		<input id="search-replace-dialog-replace" type="text" />
	</div>
	<div class="modal-footer">
				
		<a onclick="searchAndReplace();" class="btn btn-primary"><spring:message code="label.OK" /></a>
		<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>			
	</div>
	</div>
	</div>
</div>

<div class="modal" id="search-replace-dialog-2" data-backdrop="static">
	<div class="modal-dialog">
    <div class="modal-content">
	<form:form action="${contextpath}/${sessioninfo.shortname}/management/replace" method="post" style="margin: 0px;">
		<input type="hidden" name="id" id="search-replace-dialog-2-id" />
		<input type="hidden" name="search" id="search-replace-dialog-2-search" />
		<input type="hidden" name="replace" id="search-replace-dialog-2-replace" />
	
		<div class="modal-header"><spring:message code="label.SearchAndReplace" />: <spring:message code="label.Preview" /></div>
		<div class="modal-body">
			<div style="max-height: 400px; overflow: auto;" class="modal150">
				<table id="search-replace-dialog-2-table" class="table table-striped table-bordered">
					<thead>
						<tr>
							<th style="width: 50%"><spring:message code="label.Original" /></th>
							<th style="width: 50%"><spring:message code="label.New" /></th>
						</tr>
					</thead>
					<tbody id="search-replace-dialog-2-body"></tbody>
				</table>
			</div>
		</div>
		<div class="modal-footer">
			<a onclick="searchAndReplaceBack();" class="btn btn-default"><spring:message code="label.Back" /></a>	
			<input class="btn btn-primary" type="submit" value="<spring:message code="label.Save" />" />	
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>		
		</div>
	</form:form>
	</div>
	</div>
</div>

<div class="modal" id="upload-translation-dialog" data-backdrop="static">
	<div class="modal-dialog">
    <div class="modal-content">
	<form:form modelAttribute="uploadItem" name="frm" action="${contextpath}/management/importtranslation?${_csrf.parameterName}=${_csrf.token}" enctype="multipart/form-data" method="post" style="margin: 0px;">
		<div class="modal-header"><spring:message code="label.UploadExistingTranslation" /></div>
		<div class="modal-body">
			<spring:message code="label.Language" /><br />
			<select id="langupdate" name="lang" onchange="checkUpdateLanguage()">
				<option value="other"><spring:message code="label.other" /></option>
				<c:forEach items="${languages}" var="language">				
					<c:if test="${language.official}">				
						<option value="<esapi:encodeForHTMLAttribute>${language.code}</esapi:encodeForHTMLAttribute>"><esapi:encodeForHTML><spring:message code="label.lang.${language.englishName}" /></esapi:encodeForHTML></option>
					</c:if>
				</c:forEach>				
			</select>		
			<div id="otherlangupdate" style="margin-top: 10px;">
				<spring:message code="label.ProvideLanguageCode" /><br />
				<input type="text" maxlength="2" name="code" id="codeupdate" style="width: 30px;" />
				<span id="codeupdate-error" style="color: #f00" class="hideme"><spring:message code="validation.required" /></span>
			</div>

			<div id="file-uploader" style="margin-top: 10px;">
				<noscript>
				    <p>Please enable JavaScript to use file uploader.</p>
				</noscript>
			</div><br />
			
			<div id="file-uploader-message">
				
			</div>
			<div id="file-uploader-message-language" class="hideme" style='color: #f00'>
				<spring:message code="message.LanguageDoesNotMatchUploadedFile" />
			</div>
			
		</div>
		<div class="modal-footer">
			<a  class="btn btn-primary" data-dismiss="modal"><spring:message code="label.Cancel" /></a>			
		</div>
	</form:form>
	</div>
	</div>
</div>	

<c:if test="${message != null}">
	<script type="text/javascript">
		showInfo('${message}');
	</script>
</c:if>

<c:if test="${error != null}">
	<script type="text/javascript">
		showError('${error}');
	</script>
</c:if>

<div class="modal" id="edit-translations-dialog" data-backdrop="static">
	<div class="modal-dialog">
    <div class="modal-content">
	  <div class="modal-header"><spring:message code="label.EditTranslation" /></div>	  
	  <form:form action="${contextpath}/${form.survey.shortname}/management/savetranslations" method="post" style="margin: 0px;">	
	  	  <input type="hidden" name="survey" value="${form.survey.id}" />  
		  <div class="modal-body" style="height: 470px; max-height: 470px; overflow: auto">	
		  		  	
	  		<table class="table table-bordered table-striped table-styled" id="edit-translations-table">		  		
	  			<thead></thead>
	  			<tbody></tbody>		  		
	  		</table>

		  </div>
		  <div class="modal-footer">
		  		<input id="btnSaveFromEditTranslationDialog" type="submit" class="btn btn-primary" value="<spring:message code="label.Save" />" />
		  		<a id="btnCancelFromEditTranslationDialog"  class="btn btn-default" onclick="selectedElement = null;" data-dismiss="modal"><spring:message code="label.Cancel" /></a>		  				
		  </div>	  
	  </form:form>
	  </div>
	  </div>
</div>

<div class="modal" id="ask-override-dialog" data-backdrop="static">
	<div class="modal-dialog">
    <div class="modal-content">
	  <div class="modal-header"><spring:message code="label.OverrideTranslations" /></div>	  
	  	
	  <div class="modal-body">			 
	  	<spring:message code="question.ReplaceTranslation" /> 	
	  </div>
	  <div class="modal-footer">
	  	<a  class="btn btn-primary" onclick="showImportPopup(results);"><spring:message code="label.Replace" /></a>
		<a  class="btn btn-default" onclick="results = null;" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
	  </div>	  
	</div>
	</div>
</div>

<div class="modal" id="ask-invalid-dialog" data-backdrop="static">
	<div class="modal-dialog">
    <div class="modal-content">
	  <div class="modal-header"><spring:message code="label.Translations" /></div>	  
	  	
	  <div class="modal-body">			  	
	  	<spring:message code="question.ContinueInvalidLabels" />
  		<br /><br/>
  		<spring:message code="label.Keys" />: 
  		<div id="ask-invalid-keys"></div> 
	  </div>
	  <div class="modal-footer">
	  	<a  class="btn btn-primary" onclick="continueImport(results);"><spring:message code="label.Continue" /></a>
	  	<a  class="btn btn-default" onclick="results = null;" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
	  </div>
	 </div>
	 </div>
</div>

<div class="modal" id="edit-cell-dialog" data-backdrop="static">
	<div class="modal-dialog modal-lg">
    <div class="modal-content">
	<form:form action="${contextpath}/management/addtranslation" method="post" style="margin: 0px;">
		<div class="modal-header"><spring:message code="label.EditLabel" /></div>
		<div class="modal-body" style="height: 499px; max-height: 499px; overflow: auto;">
			<table style="width: 100%; max-height: 100%">
				<tr>
					<td style="vertical-align: top; width: 50%; padding: 10px;">
						<div class="modal150" id="label-editor-original" style="max-height: 450px; overflow: auto;">
						</div>				
					</td>
					<td style="vertical-align: top; width: 50%;">
						<div id="edit-cell-dialog-invalid" class="validation-error hideme"><spring:message code="label.InvalidXHTML" /></div>
						<div id="edit-cell-dialog-empty" class="validation-error hideme"><spring:message code="validation.required" /></div>
						<textarea id="label-editor" class="tinymcealign full"></textarea>
					</td>
				</tr>
			</table>			
		</div>
		<div class="modal-footer">
			<a id="btnOkFrmEditCellTranslationDialog" onclick="saveLabel();" class="btn btn-primary"><spring:message code="label.OK" /></a>	
			<a id="btnCancelFrmEditCellTranslationDialog" onclick='cancelCellEdit()' class="btn btn-default"><spring:message code="label.Cancel" /></a>
		</div>
	</form:form>
	</div>
	</div>
</div>

<div class="modal" id="import-translation-config-dialog" data-backdrop="static">
	<div class="modal-dialog">
    <div class="modal-content">
	<form:form action="${contextpath}/noform/management/importtranslation2" method="post" style="margin: 0px;">
		<div class="modal-header"><spring:message code="label.ImportTranslation" /></div>
		<div id="import-translation-config-body" class="modal-body" style="max-height: 500px; height: 500px; overflow: auto;">
		
		</div>
		<div class="modal-footer">
			<input type="submit" class="btn btn-primary" value="<spring:message code="label.Save" />" />
			<a class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>			
		</div>
	</form:form>
	</div>
	</div>
</div>

<div class="modal" id="select-active-translation-dialog" data-backdrop="static">
	<div class="modal-dialog">
    <div class="modal-content">
	<div class="modal-body">
		<spring:message code="info.SelectOneCompleteTranslation" />
	</div>
	<div class="modal-footer">
		<a class="btn btn-primary" data-dismiss="modal"><spring:message code="label.OK" /></a>			
	</div>
	</div>
	</div>
</div>

<div class="modal" id="select-complete-and-more-incompleate-translation-dialog" data-backdrop="static">
	<div class="modal-dialog">
    <div class="modal-content">
	<div class="modal-body">
		<spring:message code="info.SelectOneCompleteTranslationAndOneOrMoreTranslation" />
	</div>
	<div class="modal-footer">
		<a class="btn btn-primary" data-dismiss="modal"><spring:message code="label.OK" /></a>			
	</div>
	</div>
	</div>
</div>

<div class="modal" id="select-one-translation-dialog" data-backdrop="static">
	<div class="modal-dialog">
    <div class="modal-content">
	<div class="modal-body">
		<spring:message code="info.SelectOneTranslation" />
	</div>
	<div class="modal-footer">
		<a class="btn btn-primary" data-dismiss="modal"><spring:message code="label.OK" /></a>			
	</div>
	</div>
	</div>
</div>

<div class="modal" id="keep-active-translation-dialog" data-backdrop="static">
	<div class="modal-dialog">
    <div class="modal-content">
	<div class="modal-body">
		<spring:message code="info.KeepOneCompleteTranslation" />
	</div>
	<div class="modal-footer">
		<a class="btn btn-primary" data-dismiss="modal"><spring:message code="label.OK" /></a>			
	</div>
	</div>
	</div>
</div>

<div class="modal" id="delete-pivot-translation-dialog" data-backdrop="static">
	<div class="modal-dialog">
    <div class="modal-content">
	<div class="modal-body">
		<spring:message code="info.KeepMainTranslation" />
	</div>
	<div class="modal-footer">
		<a class="btn btn-primary" data-dismiss="modal"><spring:message code="label.OK" /></a>			
	</div>
	</div>
	</div>
</div>

<div class="modal" id="ask-delete-translation-dialog" data-backdrop="static">
	<div class="modal-dialog">
    <div class="modal-content">
	  <div class="modal-body">			 
	  	<spring:message code="question.DeleteTranslation" /> 	
	  </div>
	  <div class="modal-footer">
	  	<a  class="btn btn-primary" onclick="deleteTranslation();"><spring:message code="label.OK" /></a>
		<a  class="btn btn-default" onclick="results = null;" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
	  </div>
	 </div>
	 </div>
</div>

<div class="modal" id="ask-delete-translations-dialog" data-backdrop="static">
	<div class="modal-dialog">
    <div class="modal-content">
	  <div class="modal-body">			 
	  	<spring:message code="question.DeleteTranslations" /> 	
	  </div>
	  <div class="modal-footer">
	  	<a  class="btn btn-primary" onclick="deleteTranslations();"><spring:message code="label.OK" /></a>
		<a  class="btn btn-default" onclick="results = null;" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
	  </div>
	 </div>
	 </div>
</div>

<div class="modal" id="ask-request-translations-dialog" data-backdrop="static">
	<div class="modal-dialog">
    <div class="modal-content">
	  <div class="modal-body">			 
	  	<spring:message code="question.RequestTranslations" /> 	
	  </div>
	  <div class="modal-footer">
	  	<a  class="btn btn-primary" onclick="translateTranslations();"><spring:message code="label.OK" /></a>
		<a  class="btn btn-default" onclick="results = null;" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
	  </div>
	 </div>
	 </div>  
</div>
<div class="modal" id="ask-request-translation-dialog" data-backdrop="static">
	<div class="modal-dialog">
    <div class="modal-content">
	  <div class="modal-header"> 
	  	<spring:message code="label.RequestTranslationHeader" /><span id="current-lang-request" name ="current-lang-request"></span> 
	  </div>
	  <div class="modal-body">
	  	<spring:message code="label.MachineTranslationInstructions"/>
	  	<br />
	  	<select id="pivotlangs" name="pivotlangs" style="margin-top: 6px; width: auto" class="form-control">
				<c:forEach items="${completedTranslations}" var="trans">				
					<option value="${trans.key}"><esapi:encodeForHTML>${trans.value}</esapi:encodeForHTML></option>
 				</c:forEach>
		</select>	 	
	  </div>
	  <div class="modal-footer">
	  	<a  class="btn btn-primary" onclick="translateTranslation();"><spring:message code="label.RequestTranslation" /></a>
		<a  class="btn btn-default" onclick="results = null;" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
	  </div>
	</div>
	</div>
</div>
<div class="modal" id="ask-cancel-translation-dialog" data-backdrop="static">
	<div class="modal-dialog">
    <div class="modal-content">
	  <div class="modal-header"> 
	  	<spring:message code="label.CancelTranslationHeader" /><span id="current-lang-cancel" name ="current-lang-cancel"></span> 
	  </div>	
	  <div class="modal-body">			 
	  	<spring:message code="label.CancelTranslationText1" />
	  	<br /> 	
	  	<spring:message code="label.CancelTranslationText2" />
	  	<br />
	  	<a  class="btn btn-default" onclick="cancelTranslation();"><spring:message code="label.CancelTranslation" /></a>
	  </div>
	  <div class="modal-footer">
		<a  class="btn btn-primary" onclick="results = null;" data-dismiss="modal"><spring:message code="label.Close" /></a>	
	  </div>
	</div>
	</div>
</div>
