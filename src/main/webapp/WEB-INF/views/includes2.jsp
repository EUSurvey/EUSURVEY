<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
 
	<div class="modal" id="export-name-dialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
   		<div class="modal-content">
		<div class="modal-header" style="font-weight: bold;">
			<spring:message code="label.Start" />&nbsp;<span id="export-name-dialog-type"></span>&nbsp;<spring:message code="label.Export" />
		</div>
		<div class="modal-body" style="padding-left: 30px;">		
			<label for="export-name" style="display:inline"><span class="mandatory">*</span><spring:message code="label.ExportName" /></label>
			<input class="form-control" type="text" id="export-name" maxlength="255" name="export-name" style="width:220px; margin-top: 10px" />
			<span id="validation-error-required" class="validation-error hideme"><br /><spring:message code="validation.required" /></span>
			<span id="validation-error-exportname" class="validation-error hideme"><spring:message code="validation.name2" /></span>
		</div>
		<div class="modal-footer">
			<img alt="wait animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a id="okStartExportButton"  onclick="checkAndStartExport($('#export-name').val());"  class="btn btn-primary"><spring:message code="label.OK" /></a>	
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
		</div>
		</div>
		</div>
	</div>		
	
	<div class="modal" id="generic-wait-dialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">
    	<div class="modal-body" style="text-align: center;">	
			<div id="generic-wait-dialog-div" class="dialog-wait-image" style="padding: 30px">
		 	</div>
		</div>
		</div>
		</div>
	</div>
		
	<form:form id="logoutform" action="${contextpath}/j_spring_security_logout" method="post">
    </form:form>
	
	<script type="text/javascript">
	    $(function() {	
	    	var spinner = new Spinner().spin();
	    	$("#generic-wait-dialog-div").append(spinner.el);			    	
	    });
	</script>	

    <c:if test="${continueWithoutJavascript != 'true'}">
	    <noscript>
			<div style="position: fixed; top: 0pc; left: 0px; width: 100%; height: 100%; background-color: #fff; z-index: 1002; padding: 30px;">
			    <spring:message code="info.JavascriptDisabled" />
			    <br /><br />
				<a href="${contextpath}/">EUSurvey home</a>
		    </div>	
	    </noscript>
	    
	    <div class="modal" id="nocookies2" data-backdrop="static">
	    	<div class="modal-dialog">
	    	<div class="modal-content">
		    <div class="modal-body" style="text-align: center">		
			    <spring:message code="info.CookiesDisabled" />
			    <br /><br />
				<a href="${contextpath}/">EUSurvey home</a>
		    </div>
		    </div>
		    </div>
	    </div>	
	    
	    <script type="text/javascript">
		    $(function() {	
		    	var date = new Date().getFullYear().toString();
		    	document.cookie = "testcookie=" + date + "; path=/";
		    	
		    	var nameEQ = "testcookie=";
		        var ca = document.cookie.split(';');
		        for (var i = 0; i < ca.length; i++) {
		            var c = ca[i];
		            while (c.charAt(0) == ' ') c = c.substring(1, c.length);
		            if (c.indexOf(nameEQ) == 0){
		            	var value = c.substring(nameEQ.length, c.length);
		            	if (value == date)
		            	{
		            		return;	
		            	}
		            }
		        }
		        
		        $("#nocookies2").modal('show');	        
		    	
			});		
		</script>
		
    </c:if>