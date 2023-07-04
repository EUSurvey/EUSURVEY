<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Skin" /></title>
	
	<%@ include file="../includes.jsp" %>
	
	<script type="text/javascript" src="${contextpath}/resources/js/spectrum.js?version=<%@include file="../version.txt" %>"></script>
	<link href="${contextpath}/resources/css/spectrum.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css"></link>
	<link id="runnerCss" href="${contextpath}/resources/css/runner.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css"></link>
	<link href="${contextpath}/resources/css/fileuploader.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css"></link>

	<script type="text/javascript" src="${contextpath}/resources/js/fileuploader.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/runner.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/runner2.js?version=<%@include file="../version.txt" %>"></script>
	<script type="text/javascript" src="${contextpath}/resources/js/runnerviewmodels.js?version=<%@include file="../version.txt" %>"></script>
    <script type='text/javascript' src='${contextpath}/resources/js/knockout-3.5.1.js?version=<%@include file="../version.txt" %>'></script>
	
	<script>
	
		$(function() {					
			$("#settings-menu-tab").addClass("active");
			initConfiguration($('#rdo').val());
			sortSelect($('#rdo')[0]);
			initConfiguration($('#rdo').val());
			updateHeight();
		 	
	 		$(window).resize(function () { updateHeight(); });	 		
	 				
			var ids = "";
			$(".emptyelement").each(function(){
				ids += $(this).attr("data-id") + '-';
			})	
		 
		 	var s = "ids=" + ids + "&survey=${form.survey.id}&slang=${form.language.code}&as=${answerSet}";
			
			$.ajax({
				type:'GET',
				dataType: 'json',
				url: "${contextpath}/runner/elements/${form.survey.id}",
				data: s,
				cache: false,
				success: function( result ) {	
					for (var i = 0; i < result.length; i++)
					{
						addElement(result[i], false, true);
					}
					initializePage();
					applyStandardWidths();
				},
				error: function( result ) {	
					alert(result);
				}
			});
			
			$(".spectrum").spectrum({
				preferredFormat: "hex"
			});
			
		});	
		
		function initializePage()
		{
			<c:forEach items="${skin.elements}" var="element">			
				updatePreview('${element.name}', 'color', '${skin.getElementValue(element.name, "color")}');
				<c:if test="${element.name == '.sectiontitle'}">
					updatePreview('${element.name}', 'border-color', '${skin.getElementValue(element.name, "color")}');
				</c:if>
				updatePreview('${element.name}', 'background-color', '${skin.getElementValue(element.name, "background-color")}');
				updatePreview('${element.name}', 'font-family', '${skin.getElementValue(element.name, "font-family")}');
				updatePreview('${element.name}', 'font-size', '${skin.getElementValue(element.name, "font-size")}');
				updatePreview('${element.name}', 'font-weight', '${skin.getElementValue(element.name, "font-weight")}');
				updatePreview('${element.name}', 'font-style', '${skin.getElementValue(element.name, "font-style")}');
			</c:forEach>
				
			$(".link").removeAttr("onclick");
			$(".link").removeAttr("target");
			$(".link").attr("href","#");
			
			$("input.required").removeAttr("required");
	 		$("textarea.required").removeAttr("required");
		}
				
		function sortSelect(select, startAt) {
		    if(typeof startAt === 'undefined') {
		        startAt = 0;
		    }

		    var texts = [];

		    for(var i = startAt; i < select.length; i++) {
		        texts[i] = [
		            select.options[i].text.toUpperCase().trim(),
		            select.options[i].text,
		            select.options[i].value
		        ].join('|');
		    }

		    texts.sort();

		    texts.forEach(function(text, index) {
		        var parts = text.split('|');

		        select.options[startAt + index].text = parts[1];
		        select.options[startAt + index].value = parts[2];
		    });
		}
		
		function getHeight() {
		  var myWidth = 0;
		  var myHeight = 0;
		  if( typeof( window.innerWidth ) == 'number' ) {
		    //Non-IE
		    myWidth = window.innerWidth;
		    myHeight = window.innerHeight;
		  } else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
		    //IE 6+ in 'standards compliant mode'
		    myWidth = document.documentElement.clientWidth;
		    myHeight = document.documentElement.clientHeight;
		  } else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
		    //IE 4 compatible
		    myWidth = document.body.clientWidth;
		    myHeight = document.body.clientHeight;
		  }
		  return myHeight;
		}
	
	
		function updateHeight() {
		    h = getHeight();
		    bh = h - 300;
		    $("#big-skin-dialog").find(".runner-content").css("height", bh + "px").css("min-height", bh + "px");
		    bh += 120;
		    $("#big-skin-dialog").find(".modal-body").css("height", bh + "px").css("min-height", bh + "px");
		}
		
		function initConfiguration(name)
		{
			$(".skinelement").hide();
			$("div[id='div" + name + "']").show();
			
			$(".selectedskinelement").removeClass("selectedskinelement");
			$(name).addClass("selectedskinelement");
		}
		
		function updatePreview(element, csselement, value)
		{
			if (element.indexOf(".") == -1) element = "." + element;								
// 			if (value != '' && csselement.indexOf("color") != -1 && value.indexOf("#") == -1)
// 			{
// 				value = "#" + value;
// 			}			
			$(element).css(csselement, value.replace(";",""));
		}
		
		function updateFontSizeTextfield(name, val)
		{
			$('input[id="txt' + name + '"]').val(val);
		}
		
		function checkAndSubmit(dialog) {
			$("#name-validation-error").hide();
			$("#newname-validation-error").hide();
			$("#newname-exists-error").hide();
			
			var name = "";
			
			if (!dialog){			
				name = $("#name").val();
				if (name.trim().length == 0)
				{
					$("#name-validation-error").show();
					return;
				}
			} else {
				name = $("#newname").val();
				if (name.trim().length == 0)
				{
					$("#newname-validation-error").show();
					return;
				}
			}
			
			if ($("#isPublic1").is(":checked"))
			{
				var id = $("#id").val();
				
				$.ajax({type: "GET",
					url: "${contextpath}/settings/skin/publicnameexists",
					data: {name : name, id : id},
					cache: false,
					dataType: "json",
				    success : function(result)
				    {
				    	if (result == "1")
				    	{
				    		if (dialog)
				    		{
				    			$("#newname-exists-error").show();
				    		} else {
				    			$("#big-skin-name-dialog").modal("show");
				    		}
				    	} else {
				    		$("#name").val(name);
				    		$("#big-skin-form").submit();
				    	}
				    }
				 });
			} else {
				$("#big-skin-form").submit();
			}
		}

	</script>
	
	<style type="text/css">
	
		.selectedskinelement {
			border: 1px solid #f00 !important;
		}
		
		.page {
			margin-top: 0px;
		} 
		
		.fullpage {
			margin-top: 100px;
		}
		
		.skintable td {
			padding-right: 10px;
			padding-left: 10px;
		}
		
		.smallinput {
			width: 80px;
		}
	
	</style>
</head>
<body style="background-color: #666">
			
		<div id="big-skin-dialog" data-backdrop="static" style="width: 1350px; margin-left: auto; margin-right: auto; background-color: #fff">
					
			<div style="position: fixed; top: 0px; left: 0px; height: 30px; width: 100%; z-index: 1010; background-color: #666;"></div>				
					
			<form:form id="big-skin-form" method="POST" action="${contextpath}/settings/skin/save" modelAttribute="skin">
			
				<form:hidden path="id" />
					
				<div class="modal-header" style="margin-top: 30px;">
					<spring:message code="label.EditSkin" />
				</div>
				
				<div class="modal-body" style="padding-top: 0px;">		
					<div style="float: left; margin-left: 25px;">
						<b><span class='mandatory'>*</span><spring:message code="label.Name" /></b><br />
						<form:input maxlength="255" path="name" style="margin-bottom: 10px; margin-left: 0px;" />
						<c:if test="${USER.getGlobalPrivilegeValue('FormManagement') == 2}">	
							<form:checkbox path="isPublic" class="check"/><spring:message code="label.Public" />
						</c:if>
						<br />
						<div id="name-validation-error" class="validation-error hideme"><spring:message code="validation.required" /></div>
						<span style="display:block;margin-top: 5px;"><b><spring:message code="label.SelectItemToSkin" /></b><br /></span>
						<select onchange="initConfiguration($('#rdo').val());" name="rdo" id="rdo" style="margin-bottom: 0px; margin-left: 0px;">						
							 <c:forEach items="${skin.elements}" var="element">
							 	<c:if test="${element.name != '.runner-content'}">
									<option value="<esapi:encodeForHTMLAttribute>${element.name}</esapi:encodeForHTMLAttribute>">
										<c:choose>
											<c:when test="${element.name == '.sectiontitle'}"><spring:message code="skin.SectionText" /></c:when>
											<c:when test="${element.name == '.questiontitle'}"><spring:message code="skin.QuestionText" /></c:when>
											<c:when test="${element.name == '.answertext'}"><spring:message code="skin.AnswerText" /></c:when>
											<c:when test="${element.name == '.matrix-header'}"><spring:message code="skin.MatrixHeader" /></c:when>
											<c:when test="${element.name == '.table-header'}"><spring:message code="skin.TableHeader" /></c:when>
											<c:when test="${element.name == '.questionhelp'}"><spring:message code="skin.QuestionHelp" /></c:when>
											<c:when test="${element.name == '.info-box'}"><spring:message code="skin.InfoBox" /></c:when>
											<c:when test="${element.name == '.linkstitle'}"><spring:message code="skin.MetaInformationTitles" /></c:when>
											<c:when test="${element.name == '.link'}"><spring:message code="skin.Link" /></c:when>
											<c:when test="${element.name == '.surveytitle'}"><spring:message code="skin.SurveyTitle" /></c:when>
											<c:when test="${element.name == '.right-area'}"><spring:message code="skin.ActiveMetaInfo" /></c:when>
											<c:when test="${element.name == '.text'}"><spring:message code="skin.Text" /></c:when>
											<c:otherwise>${element.name}</c:otherwise>
										</c:choose>
									</option>
								</c:if>
							</c:forEach>			 		
						</select>					
					</div>
					
					<div id="elements" style="margin: 0px; margin-right: 30px; float: right; background-color: #eee; padding: 2px;">						
						
						<c:forEach items="${skin.elements}" var="element">
							<div id="div${element.name}" class="skinelement">
							
								<table class="skintable">
									<tr>
										<td><label><spring:message code="html.ForegroundColor" /></label></td>
										<td><input id="color<esapi:encodeForHTMLAttribute>${element.name}</esapi:encodeForHTMLAttribute>" name="color<esapi:encodeForHTMLAttribute>${element.name}</esapi:encodeForHTMLAttribute>" onchange="updatePreview('<esapi:encodeForHTMLAttribute>${element.name}</esapi:encodeForHTMLAttribute>', 'color', this.value);<c:if test="${element.name == '.sectiontitle'}">updatePreview('<esapi:encodeForHTMLAttribute>${element.name}</esapi:encodeForHTMLAttribute>', 'border-color', this.value);</c:if>" class="smallinput spectrum" value="<esapi:encodeForHTMLAttribute>${skin.getElementValue(element.name, "color")}</esapi:encodeForHTMLAttribute>"/></td>
										<td><label><spring:message code="html.FontFamily" /></label></td>
										<td>
											<select name="font-family<esapi:encodeForHTMLAttribute>${element.name}</esapi:encodeForHTMLAttribute>" onchange="updatePreview('<esapi:encodeForHTMLAttribute>${element.name}</esapi:encodeForHTMLAttribute>', 'font-family', this.options[this.selectedIndex].value);">
												<option <c:if test='${skin.getElementValue(element.name, "font-family") == "sans-serif"}'>selected="selected"</c:if> value="sans-serif">sans-serif</option>
												<option <c:if test='${skin.getElementValue(element.name, "font-family") == "serif"}'>selected="selected"</c:if> value="serif">serif</option>
												<option <c:if test='${skin.getElementValue(element.name, "font-family") == "monospace"}'>selected="selected"</c:if> value="monospace">monospace</option>
											</select>
										</td>
									</tr>
									<tr>
										<td><label><spring:message code="html.BackgroundColor" /></label></td>
										<td><input id="background-color<esapi:encodeForHTMLAttribute>${element.name}</esapi:encodeForHTMLAttribute>" name="background-color<esapi:encodeForHTMLAttribute>${element.name}</esapi:encodeForHTMLAttribute>" onchange="updatePreview('<esapi:encodeForHTMLAttribute>${element.name}</esapi:encodeForHTMLAttribute>', 'background-color', this.value);" class="smallinput spectrum" value="<esapi:encodeForHTMLAttribute>${skin.getElementValue(element.name, "background-color")}</esapi:encodeForHTMLAttribute>"/></td>
										<td><label><spring:message code="html.FontSize" /></label></td>
										<td>
											<input style="width: 60px;" id="txt<esapi:encodeForHTMLAttribute>${element.name}</esapi:encodeForHTMLAttribute>" name="font-size<esapi:encodeForHTMLAttribute>${element.name}</esapi:encodeForHTMLAttribute>" type="text" maxlength="10" size="10" value="${skin.getElementValue(element.name, "font-size")}" onchange="updatePreview('<esapi:encodeForHTMLAttribute>${element.name}</esapi:encodeForHTMLAttribute>', 'font-size', this.value); document.getElementById('ff<esapi:encodeForHTMLAttribute>${element.name}</esapi:encodeForHTMLAttribute>').selectedIndex = 0;"/>
											<select style="width: auto" id="ff<esapi:encodeForHTMLAttribute>${element.name}</esapi:encodeForHTMLAttribute>" onchange="updateFontSizeTextfield('<esapi:encodeForHTMLAttribute>${element.name}</esapi:encodeForHTMLAttribute>', this.options[this.selectedIndex].value); updatePreview('<esapi:encodeForHTMLAttribute>${element.name}</esapi:encodeForHTMLAttribute>', 'font-size', this.options[this.selectedIndex].value);">
												<option value="">select</option>
												<option <c:if test='${skin.getElementValue(element.name, "font-size") == "9pt"}'>selected="selected"</c:if> value="9pt">9pt</option>
												<option <c:if test='${skin.getElementValue(element.name, "font-size") == "10pt"}'>selected="selected"</c:if> value="10pt">10pt</option>
												<option <c:if test='${skin.getElementValue(element.name, "font-size") == "small"}'>selected="selected"</c:if> value="small"><spring:message code="html.small" /></option>
												<option <c:if test='${skin.getElementValue(element.name, "font-size") == "large"}'>selected="selected"</c:if> value="large"><spring:message code="html.large" /></option>
											</select>
										</td>
									</tr>
									<tr>
										<td><label><spring:message code="html.FontStyle" /></label></td>
										<td>
											<select style="width: auto" name="font-style<esapi:encodeForHTMLAttribute>${element.name}</esapi:encodeForHTMLAttribute>" onchange="updatePreview('<esapi:encodeForHTMLAttribute>${element.name}</esapi:encodeForHTMLAttribute>', 'font-style', this.options[this.selectedIndex].value);">
												<option <c:if test='${skin.getElementValue(element.name, "font-style") == "normal"}'>selected="selected"</c:if> value="normal"><spring:message code="html.normal" /></option>
												<option <c:if test='${skin.getElementValue(element.name, "font-style") == "italic"}'>selected="selected"</c:if> value="italic"><spring:message code="html.italic" /></option>
												<option <c:if test='${skin.getElementValue(element.name, "font-style") == "oblique"}'>selected="selected"</c:if> value="oblique"><spring:message code="html.oblique" /></option>
											</select>
										</td>
										<td><label><spring:message code="html.FontWeight" /></label></td>
										<td>
											<select style="width: auto" name="font-weight<esapi:encodeForHTMLAttribute>${element.name}</esapi:encodeForHTMLAttribute>" onchange="updatePreview('<esapi:encodeForHTMLAttribute>${element.name}</esapi:encodeForHTMLAttribute>', 'font-weight', this.options[this.selectedIndex].value);">
												<option <c:if test='${skin.getElementValue(element.name, "font-weight") == "normal"}'>selected="selected"</c:if> value="normal"><spring:message code="html.normal" /></option>
												<option <c:if test='${skin.getElementValue(element.name, "font-weight") == "bold"}'>selected="selected"</c:if> value="bold"><spring:message code="html.bold" /></option>
												<option <c:if test='${skin.getElementValue(element.name, "font-weight") == "600"}'>selected="selected"</c:if> value="600">600</option>
												<option <c:if test='${skin.getElementValue(element.name, "font-weight") == "900"}'>selected="selected"</c:if> value="900">900</option>
											</select>
										</td>
									</tr>								
								</table>		
								
							</div>
							
						</c:forEach>
									
					</div>				
					
					<div style="clear: both"></div>
							
					<div class="runner-content" style="position: absolute; margin: 0px; margin-left: auto; margin-right: auto; margin-top: 10px; padding: 0px; height: 640px; max-height: 640px; overflow: auto;">
						<c:set var="mode" value="skin" />
						<%@ include file="../runner/runnercontentinner.jsp" %>	
					</div>
				</div>
		  		
		  		<div style="position: fixed; bottom: 68px; left: 0px; z-index: 1031; width: 100%; height: 30px;">
					<div style="width: 1350px; margin-left: auto; margin-right: auto; min-height: 60px; text-align: center; padding-top: 0px; background-color: #fff;">
					</div>
				</div>
				
				<div style="position: fixed; bottom: 35px; left: 0px; z-index: 1032; width: 100%; height: 60px; text-align: center; padding-top: 15px;">
					<a onclick="checkAndSubmit(false);" class="btn btn-primary"><spring:message code="label.Save" /></a>
					<a class="btn btn-default" onclick="$('#confirm-cancel-dialog').modal('show')"><spring:message code="label.Cancel" /></a> 
				</div>
	
		</form:form>			
	</div>
	
	<div style="clear: both"></div>
	
	<div class="modal" id="confirm-cancel-dialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
   		<div class="modal-content">	
		<div class="modal-body">
			<spring:message code="question.CancelWithoutSave" />
		</div>
		<div class="modal-footer">
			<a class="btn btn-primary" href="${contextpath}/settings/skin"><spring:message code="label.OK" /></a>
			<a  class="btn btn-default" onclick="$('#confirm-cancel-dialog').modal('hide')"><spring:message code="label.Cancel" /></a>			
		</div>
		</div>
		</div>
	</div>
	
	<div class="modal" id="big-skin-name-dialog">
		<div class="modal-dialog">
		    <div class="modal-content">
				<div class="modal-body">	
				 	<spring:message code="info.SkinNameExists" /><br /><br />
				 	<input type="text" id="newname" />
				 	<div id="newname-validation-error" class="validation-error hideme"><spring:message code="validation.required" /></div>
				 	<div id="newname-exists-error" class="validation-error hideme"><spring:message code="error.UniqueName" /></div>
				</div>
				<div class="modal-footer">
					<a onclick="checkAndSubmit(true)" class="btn btn-primary"><spring:message code="label.OK" /></a>	
					<a class="btn btn-default" data-dismiss="modal"><spring:message code="label.Cancel" /></a>	
				</div>
			</div>
		</div>
	</div>
	
	<c:if test="${message != null}">
		<script type="text/javascript">
			showInfo('${message}');
		</script>
	</c:if>

</body>
</html>
