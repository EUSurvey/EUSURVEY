<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Skins" /></title>
	
	<%@ include file="../includes.jsp" %>
	<link href="${contextpath}/resources/css/fileuploader.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${contextpath}/resources/js/fileuploader.js?version=<%@include file="../version.txt" %>"></script>
	
	<script>	
		$(function() {					
			$("#settings-menu-tab").addClass("active");
			$("#skins-button").removeClass("InactiveLinkButton").addClass("ActiveLinkButton");
			
			var uploader = new qq.FileUploader({
				element: $("#file-uploader-skin")[0],
			    action: '${contextpath}/settings/skin/upload',
			    params: {
			    	'_csrf': csrftoken
			    },
			    multiple: false,
			    cache: false,
			    uploadButtonText: uploadASkin,
			    sizeLimit: 1048576,
			    onComplete: function(id, fileName, responseJSON)
				{		    	
			    	if (responseJSON.success)
			    	{
			    		window.location = "${contextpath}/settings/skin?skinuploaded=true";
			    	} else {
			    		
			    		if (responseJSON.message == "invalidfiletype")
			    		{
			    			showError("<spring:message code='error.SkinUploadFailedInvalidFileType' />");
			    		} else {
			    			showError("<spring:message code='error.SkinUploadFailedInvalidFileContent' />");
			    		}
			    	}
				}
			});
			
			$(".qq-upload-list").hide();
			$(".qq-upload-button").addClass("btn btn-default").removeClass("qq-upload-button");
			$(".qq-upload-drop-area").css("margin-left", "-1000px");
			
			$('[data-toggle="tooltip"]').tooltip(); 
			
			if (window.location.search.toLowerCase().includes("skinuploaded")){
				$('#skintable tr:last')[0].scrollIntoView();
			}
		});	
		
		var selectedId = null;
		function showDeleteDialog(id)
		{
			selectedId = id;
			$("#delete-skin-dialog").modal("show");
		}
		
		function deleteSkin()
		{
			window.location="${contextpath}/settings/skin/delete/" + selectedId;
		}
		
	</script>
</head>
<body>
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
		<%@ include file="../menu.jsp" %>
		<%@ include file="settingsmenu.jsp" %>	
			
		<div class="fixedtitleform">
			<div class="fixedtitleinner" style="width:880px">
				<div id="action-bar" class="container action-bar" style="padding-top: 10px; width: 880px">
					<div class="row">
						<div class="col-md-12" style="text-align:center">	
							<a href="<c:url value="/settings/skin/new"/>" class="btn btn-default"><spring:message code="label.CreateNewSkin" /></a>
							<div id="file-uploader-skin" style="margin-top: 10px; display: inline-block;"></div>
						</div>
					</div>
				</div>	
			</div>
		</div>
		
		<div class="page880" style="padding-bottom: 0px; min-height: 200px; padding-top:220px">
		
			<div style="margin-left: auto; margin-right: auto; width: 630px;">					
				<table id="skintable" class="table table-striped table-bordered table-styled" cellpadding="0" cellspacing="0">
					<thead>
						<tr>
							<th><spring:message code="label.Name" /></th>
							<th><spring:message code="label.Owner" /></th>
							<th><spring:message code="label.Public" /></th>
							<th style="width: 80px;"><spring:message code="label.Actions" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${skins}" var="skin">
							<c:choose>
								<c:when test="${skin.isPublic}"><tr style="background-color: #D6FFDA;"></c:when>
								<c:otherwise><tr></c:otherwise>
							</c:choose>
							
								<td><esapi:encodeForHTML>${skin.displayName}</esapi:encodeForHTML></td>
								<td><esapi:encodeForHTML>${skin.owner.name}</esapi:encodeForHTML></td>
								<td>
									<c:choose>
									   <c:when test="${skin.isPublic}"><spring:message code="label.Yes" /></c:when>
									   <c:otherwise><spring:message code="label.No" /></c:otherwise>
									</c:choose>
								</td>
								<td style="width: 150px; text-align: center">
									<c:choose>
									   <c:when test="${skin.owner.id == USER.id || USER.getGlobalPrivilegeValue('FormManagement') == 2}">
									  	 	<a data-toggle="tooltip" href="${contextpath}/settings/skin/edit/${skin.id}" class="iconbutton" title="<spring:message code="label.Edit" />"><span class="glyphicon glyphicon-pencil"></span></a>
									   </c:when>
									   <c:otherwise>
											<a data-toggle="tooltip" class="iconbutton disabled" title="<spring:message code="label.Edit" />"><span class="glyphicon glyphicon-pencil"></span></a>
									   </c:otherwise>
									</c:choose>
									
									<c:choose>
									   <c:when test="${skin.isPublic || skin.owner.id == USER.id || USER.getGlobalPrivilegeValue('FormManagement') == 2}">
											<a data-toggle="tooltip" href="${contextpath}/settings/skin/download/${skin.id}" class="iconbutton" title="<spring:message code="label.Download" />"><span class="glyphicon glyphicon-download"></span></a>
										</c:when>
									   <c:otherwise>
											<a data-toggle="tooltip" class="iconbutton disabled" title="<spring:message code="label.Download" />"><span class="glyphicon glyphicon-download"></span></a>
										</c:otherwise>
									</c:choose>		
																	
									<c:choose>
									   <c:when test="${skin.isPublic || skin.owner.id == USER.id || USER.getGlobalPrivilegeValue('FormManagement') == 2}">
											<a data-toggle="tooltip" href="${contextpath}/settings/skin/copy/${skin.id}" class="iconbutton" title="<spring:message code="label.Copy" />"><span class="glyphicon glyphicon-share"></span></a>
										</c:when>
									   <c:otherwise>
											<a data-toggle="tooltip" class="iconbutton disabled" title="<spring:message code="label.Copy" />"><span class="glyphicon glyphicon-share"></span></a>
										</c:otherwise>
									</c:choose>								
									
									<c:choose>
									   <c:when test="${skin.owner.id == USER.id || USER.getGlobalPrivilegeValue('FormManagement') == 2}">
											<a data-toggle="tooltip" onclick="showDeleteDialog('${skin.id}');"  class="iconbutton" title="<spring:message code="label.Delete" />"><span class="glyphicon glyphicon-remove"></span></a>
										</c:when>
									   <c:otherwise>
											<a data-toggle="tooltip" class="iconbutton disabled" title="<spring:message code="label.Delete" />"><span class="glyphicon glyphicon-remove"></span></a>
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
						</c:forEach>
					</tbody>
					
				</table>
		
			</div>
			
			<div style="clear: both"></div>
		</div>
	</div>	
	
	<%@ include file="../footer.jsp" %>	

	<c:if test="${message != null}">
		<script type="text/javascript">
			showInfo('${message}');
		</script>
	</c:if>

	<div class="modal" id="delete-skin-dialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
    	<div class="modal-content">		
		<div class="modal-body">
			<spring:message code="question.DeleteSkin" />
		</div>
		<div class="modal-footer">
			<img id="delete-wait-animation" class="hideme" style="margin-right:90px;" src="${contextpath}/resources/images/ajax-loader.gif" />
			<a  onclick="deleteSkin();" class="btn btn-primary" data-dismiss="modal"><spring:message code="label.Yes" /></a>
			<a  class="btn btn-default" data-dismiss="modal"><spring:message code="label.No" /></a>			
					
		</div>
		</div>
		</div>
	</div>

</body>
</html>
