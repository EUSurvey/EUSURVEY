<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Administration" /></title>	
	<%@ include file="../includes.jsp" %>	
	
	<script type="text/javascript" src="${contextpath}/resources/js/jquery.stickytableheaders.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/Chart.min.js?version=<%@include file="../version.txt" %>"></script>
	
	<script type="text/javascript"> 
		$(function() {
			
			$(function () {
			    $('#checkall').click(function () {
			    	$('#fileTableDivTable').find('input[type="checkbox"]').not(":disabled").prop('checked', this.checked);
			    });
			    $('[data-toggle="tooltip"]').tooltip();
			});
			
			var ctx = $("#barChart");
			var barChart = new Chart(ctx, {
			    type: 'horizontalBar',
			    data: {
			        labels: [ "Free [${totalDirFreeNice}]", "Used [${totalDirSizeNice}]"],
			        datasets: [{
			            data: [ ${totalDirFree}, ${totalDirSize}],
			            backgroundColor: [
							"#51DB15",
							"#36A2EB",
							"#FFCE56"
			            ],
			            hoverBackgroundColor: [
							"#51DB15",
							"#36A2EB",
							"#FFCE56"
			                               ]
			        }]
			    },
			    options: {
			    	tooltips: {
			    		enabled: false
			    	},
			    	scales: {
			    		display: false,
			    		xAxes: [{
			    			display: false,
			    			ticks: {
			    				beginAtZero: true
			                }			    			
			            }],
			            yAxes: [{
			    			display: true  
			            }],
			            gridLines: [{
			    			display: false
			            }]
			    	},
			    	legend: {
		    			display: false
		            }
			    }
			});
			
			$("#administration-menu-tab").addClass("active");
			$("#files-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
			
			<c:if test="${error != null}">
				showError('<esapi:encodeForHTML>${error}</esapi:encodeForHTML>');
			</c:if>
			
			<c:if test="${info != null}">
				showInfo('<esapi:encodeForHTML>${info}</esapi:encodeForHTML>');
			</c:if>
				
			$("#fileTableDivTable").stickyTableHeaders({fixedOffset: 160});
			
			document.onkeypress = stopRKey; 		
			
			$("#userid").keyup(function(e) {
			    if(e.keyCode == 13){
			    	submitUserSearch(); 
				 }	
			 });
			
			$("#surveyuid").keyup(function(e) {
			    if(e.keyCode == 13){
			    	$('#mode').val('surveys');
			    	$('#surveyform').submit();
				 }	
			 });
			
			$("#surveyalias").keyup(function(e) {
			    if(e.keyCode == 13){
			    	$('#mode').val('surveys');
			    	$('#surveyform').submit();
				 }	
			 });
			
			$("#archivesurveyuid").keyup(function(e) {
			    if(e.keyCode == 13){
			    	$('#mode').val('archive');
			    	$('#surveyform').submit();
				 }	
			 });
			
			$("#archivesurveyalias").keyup(function(e) {
			    if(e.keyCode == 13){
			    	$('#mode').val('archive');
			    	$('#surveyform').submit();
				 }	
			 });
		});
		
		function stopRKey(evt) { 
		  var evt = (evt) ? evt : ((event) ? event : null); 
		  var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null); 
		  if ((evt.keyCode == 13) && (node.type=="text"))  {return false;} 
		} 
		
		function confirmDelete(path)
		{
			$('#confirmdeletefiledialogpath').val(path);
			$('#confirmdeletefiledialogpathnice').html(path);
			$('#confirmdeletefiledialog').modal('show');
		}
		
		function submitUserSearch()
		{
			var id = $("#userid").val();			
			var value = parseFloat(id);
			
			$("#invaliduserid").hide();
		
			if (isNaN(id) || isNaN(value) || !$.isNumeric(value) || !isFinite(value) || id.indexOf(".") > -1 || id.indexOf("-") > -1)
			{
				$("#invaliduserid").show();
				return;
			}
			
			$('#mode').val('users');
			$("#surveyform").submit();
		}
		
		function showConfirmDeleteFilesDialog() {
			if ($('#checkall').is(":checked")) {
				var text = '<spring:message code="question.DeleteAllFiles" />';
				$('#confirmdeletefilesdialog').find(".modal-body").html(text);
			} else {
				var text = '<spring:message code="question.DeleteFiles" arguments="[FILES]" />';
				var selectedFiles = $('.selectedfile:checked').length;
				text = text.replace('[FILES]', selectedFiles);
				$('#confirmdeletefilesdialog').find(".modal-body").html(text);
			}
			$('#confirmdeletefilesdialog').modal('show')
		}
	</script>
		
</head>
<body>

	<%@ include file="../header.jsp" %>
	<%@ include file="../menu.jsp" %>
	<%@ include file="adminmenu.jsp" %>	
	
	<div class="page-wrap">
	
		<div class="fixedtitleform">
			<div class="fixedtitleinner">
				
			</div>
		</div>
			
		<div class="fullpage">
			
			<div style="float: right; width: 300px; height: 60px; margin-top: -20px;">
				<canvas id="barChart" width="300" height="60"></canvas>
			</div>	
				
			 <ul class="nav nav-tabs" role="tablist">
			    <li role="presentation" <c:if test='${mode.equals("surveys")}'>class="active"</c:if>><a href="#surveys" aria-controls="surveys" role="tab" data-toggle="tab"><spring:message code="label.Surveys" /></a></li>
			    <li role="presentation" <c:if test='${mode.equals("archive")}'>class="active"</c:if>><a href="#archives" aria-controls="archives" role="tab" data-toggle="tab"><spring:message code="label.ArchivedSurveys" /></a></li>
			    <li role="presentation" <c:if test='${mode.equals("users")}'>class="active"</c:if>><a href="#users" aria-controls="users" role="tab" data-toggle="tab"><spring:message code="label.Users" /></a></li>
			  </ul>
			
			<form:form id="surveyform" action="files" method="post">
				<input type="hidden" name="mode" id="mode" value="${mode}" />
				
				<div class="tab-content" style="z-index: 4; position: relative">
				    <div role="tabpanel" class="tab-pane <c:if test='${mode.equals("surveys")}'>active</c:if>" id="surveys">			    	
						<div style="float: right; margin-right: 20px; margin-top: 10px;">
					    	<input type="radio" <c:if test="${filter.searchInFileSystem == true}">checked="checked"</c:if> name="surveytarget" value="fs" class="check" /> search in file system<br />
					    	<input type="radio" <c:if test="${filter.searchInFileSystem == false}">checked="checked"</c:if> name="surveytarget" value="db"  class="check" /> search in database
						</div>
				
				    	<div style="float: left; margin-top: 10px;">		    		
					    	<spring:message code="label.Survey" />&nbsp;UID
					    	<input type="text" id="surveyuid" name="surveyuid" value="${filter.archivedSurveys == false ? filter.surveyUid : ""}" class="form-control" />			    	
					    	<spring:message code="label.Survey" />&nbsp;<spring:message code="label.Alias" />
					    	<input type="text" id="surveyalias" name="surveyalias" value="${filter.archivedSurveys == false ? filter.surveyShortname : ""}" class="form-control" />
					    	
					    	<button type="submit" onclick="$('#mode').val('surveys')" class="btn btn-default" style="margin-top: 10px"><spring:message code="label.Search" /></button>
					    	<button type="submit" onclick="$('#mode').val('surveysbulkdownload')" class="btn btn-default" style="margin-top: 10px"><spring:message code="label.DownloadSelectedFiles" /></button>
					    	<a onclick="showConfirmDeleteFilesDialog()" class="btn btn-default" style="margin-top: 10px"><spring:message code="label.DeleteSelectedFiles" /></a>
				    		<button type="submit" onclick="$('#mode').val('surveysreset')" class="btn btn-default" style="margin-top: 10px"><spring:message code="label.Reset" /></button>
				    	</div>
				    	<div style="float: left; margin-left: 20px; margin-top: 10px;">
				    		<input type="checkbox" name="surveyexports" value="true" class="check" <c:if test="${filter.systemExports}">checked="checked"</c:if> /> <spring:message code="label.Exports" /><br />
					    	<input type="checkbox" name="surveyfiles" value="true" class="check" <c:if test="${filter.surveyFiles}">checked="checked"</c:if> /> <spring:message code="label.Files" /><br />
					    	<input type="checkbox" name="surveytemp" value="true" class="check" <c:if test="${filter.temporaryFiles}">checked="checked"</c:if> /> <spring:message code="label.UploadedDocuments" /><br /><br />
						</div>			    	
				    	<div style="clear: both"></div>
				    </div>
				    <div role="tabpanel" class="tab-pane <c:if test='${mode.equals("archive")}'>active</c:if>" id="archives">
						<div style="float: left; margin-top: 10px;">		    		
					    	<spring:message code="label.Survey" />&nbsp;UID
					    	<input type="text" id="archivesurveyuid" name="archivesurveyuid" value="${filter.archivedSurveys == true ? filter.surveyUid :  ""}" class="form-control" />			    	
					    	<spring:message code="label.Survey" />&nbsp;<spring:message code="label.Alias" />
					    	<input type="text" id="archivesurveyalias" name="archivesurveyalias" value="${filter.archivedSurveys == true ? filter.surveyShortname :  ""}" class="form-control" />
					    	
					    	<button type="submit" onclick="$('#mode').val('archive')" class="btn btn-default" style="margin-top: 10px"><spring:message code="label.Search" /></button>
					    	<button type="submit" onclick="$('#mode').val('archivebulkdownload')" class="btn btn-default" style="margin-top: 10px"><spring:message code="label.DownloadSelectedFiles" /></button>
				    		<button type="submit" onclick="$('#mode').val('archivereset')" class="btn btn-default" style="margin-top: 10px"><spring:message code="label.Reset" /></button>
				    	</div>
				    	<div style="clear: both"></div>
				    </div>			    
				    <div role="tabpanel" class="tab-pane <c:if test='${mode.equals("users")}'>active</c:if>" id="users">
			    		<div style="float: left; margin-top: 10px;">		    		
					    	ID
					    	<input type="text" name="userid" id="userid" value='${filter.userId > 0 ? filter.userId : ""}' class="form-control" />			    	
					    	<span id="invaliduserid" style="display: none; color: #f00"><spring:message code="validation.invalidNumber" /><br /></span>
					    	<a onclick="submitUserSearch()" class="btn btn-default" style="margin-top: 10px"><spring:message code="label.Search" /></a>
					    	<button type="submit" onclick="$('#mode').val('usersbulkdownload')" class="btn btn-default" style="margin-top: 10px"><spring:message code="label.DownloadSelectedFiles" /></button>
				    		<button type="submit" onclick="$('#mode').val('usersreset')" class="btn btn-default" style="margin-top: 10px"><spring:message code="label.Reset" /></button>
				    	</div>
				    	<div style="clear: both"></div>
				    </div>
			   	</div>
				    	
		    	<table id="fileTableDivTable" class="table table-bordered table-styled" style="margin-top: 20px;">
					<thead style="border-top: 1px solid #ddd;box-shadow: 0 -40px 0 20px white;">
						<tr>
							<th><input name="checkall" id="checkall" class="checkall" value="true" type="checkbox" /></th>
							<th><spring:message code="label.FilePath" /></th>
							<th><spring:message code="label.FileName" /></th>
							<th><spring:message code="label.File" />&nbsp;UID</th>
							<th><spring:message code="label.FileType" /></th>
							<th><spring:message code="label.FileExtension" /></th>
							<th><spring:message code="label.Created" /></th>
							<th><spring:message code="label.FileSize" /></th>
							<th style="min-width: 140px;"><spring:message code="label.Actions" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${paging.items}" var="file">
							<tr>
								<td><input class="selectedfile" name="checkfile" onclick="$('.checkall').removeAttr('checked')" value="<esapi:encodeForHTMLAttribute>${file.filePath}</esapi:encodeForHTMLAttribute>" type="checkbox" /></td>
								<td><div class="limitedtext filepath">${file.filePath}</div></td>
								<td><div class="limitedtext">${file.fileName}</div></td>
								<td><div class="limitedtext">${file.fileUid}</div></td>
								<td>${file.fileType}</td>
								<td>${file.fileExtension}</td>
								<td><spring:eval expression="file.created" /></td>
								<td>${file.fileSize}</td>
								<td>
									<c:choose>
										<c:when test="${file.fileUid != null && file.fileUid.length() > 0}">
											<a href="${contextpath}/files/${filter.surveyUid}/<esapi:encodeForURL>${file.fileUid}</esapi:encodeForURL>?fromfmc=true" data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Download"/>" class='iconbutton'><span class="glyphicon glyphicon-arrow-down"></span></a>
										</c:when>
										<c:otherwise>
											<a href="files/get?path=<esapi:encodeForURL>${file.filePath}</esapi:encodeForURL>&fromfmc=true" data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Download"/>" class='iconbutton'><span class="glyphicon glyphicon-arrow-down"></span></a>
										</c:otherwise>									
									</c:choose>
								
									<c:choose>
										<c:when test="${USER.getGlobalPrivilegeValue('SystemManagement') == 2}">
											<c:choose>
												<c:when test="${file.recreatePossible}">
													<a href="files/recreate?path=<esapi:encodeForURL>${file.filePath}</esapi:encodeForURL>" data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Recreate"/>" class='iconbutton'><span class="glyphicon glyphicon-refresh"></span></a>
												</c:when>
												<c:otherwise>
													<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Recreate"/>" class='iconbutton disabled'><span class="glyphicon glyphicon-refresh"></span></a>
												</c:otherwise>
											</c:choose>
											<c:choose>
												<c:when test="${file.isArchive()}">
													<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Delete"/>" class='iconbutton disabled'><span class="glyphicon glyphicon-remove"></span></a>
												</c:when>
												<c:otherwise>
													<a onclick="confirmDelete($(this).closest('tr').find('.filepath').first().html())" data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Delete"/>" class='iconbutton'><span class="glyphicon glyphicon-remove"></span></a>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Recreate"/>" class='iconbutton disabled'><span class="glyphicon glyphicon-refresh"></span></a>
											<a data-toggle="tooltip" rel="tooltip" title="<spring:message code="label.Delete"/>" class='iconbutton disabled'><span class="glyphicon glyphicon-remove"></span></a>
										</c:otherwise>
									</c:choose>									
								</td>
							</tr>
						</c:forEach>
						
						<c:if test="${paging.items == null || paging.items.size() == 0}">
							<tr>
								<td colspan="12"><spring:message code="label.NoFilesToDisplay"/></td>
							</tr>
						</c:if>					
					</tbody>
				</table>
				
				<c:if test="${paging != null}">				
					<%@ include file="../paging.jsp" %>					
				</c:if>		
				
				<div class="modal" id="confirmdeletefilesdialog" data-backdrop="static">
					<div class="modal-dialog">
			    	<div class="modal-content">	    	
					<div class="modal-body">
						<spring:message code="question.DeleteFiles" />
					</div>
					<div class="modal-footer">
						<input type="submit" onclick="$('#mode').val('surveysbulkdelete')" class="btn btn-primary" value="<spring:message code="label.Yes" />"/>		
						<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>		
					</div>
					</div>
					</div>			
				</div>
		  	
		  	</form:form>
		</div>	
			
		<div class="modal" id="confirmdeletefiledialog" data-backdrop="static">
			<form:form id="deleteForm" action="files/delete" method="post">
				<input type="hidden" id="confirmdeletefiledialogpath" name="path" />
				<div class="modal-dialog">
		    	<div class="modal-content">	    	
				<div class="modal-body">
					<spring:message code="question.DeleteFile" />
					<div id="confirmdeletefiledialogpathnice"></div>
				</div>
				<div class="modal-footer">
					<input type="submit" class="btn btn-primary" value="<spring:message code="label.Yes" />"/>		
					<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>		
				</div>
				</div>
				</div>			
			</form:form>
		</div>
	</div>
		
	<%@ include file="../footer.jsp" %>	

</body>
</html>
