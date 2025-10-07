<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

	<div class="modal" id="askSectionVisibilityDialog" data-backdrop="static">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-body">      
					<spring:message code="question.ApplyVisibilityToChildren" />
				</div>
				<div class="modal-footer">
					<a  class="btn btn-primary" onclick="$('#askSectionVisibilityDialog').modal('hide');updateVisibility(selectedspan, false, false, true, false);"><spring:message code="label.Yes" /></a>
					<a  class="btn btn-default" onclick="$('#askSectionVisibilityDialog').modal('hide');updateVisibility(selectedspan, false, false, false, false);"><spring:message code="label.No" /></a>                
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
	 
	 <div class="modal" id="multipletargetdatasetselectionsdialog" data-backdrop="static">
	 <div class="modal-dialog modal-sm">
     <div class="modal-content">
	  <div class="modal-body">	
	  	<spring:message code="error.multipletargetdatasetselections" />
	  </div>
	  <div class="modal-footer">
		<a  class="btn btn-default" onclick="$('#multipletargetdatasetselectionsdialog').modal('hide');"><spring:message code="label.OK" /></a>		
	  </div>
	 </div>
	 </div>
	 </div>
	 
	  <div class="modal" id="targetdatasetselectionaftersaquestiondialog" data-backdrop="static">
	  <div class="modal-dialog modal-sm">
      <div class="modal-content">
	  <div class="modal-body">	
	  	<spring:message code="error.targetdatasetselectionaftersaquestion" />
	  </div>
	  <div class="modal-footer">
		<a  class="btn btn-default" onclick="$('#targetdatasetselectionaftersaquestiondialog').modal('hide');"><spring:message code="label.OK" /></a>		
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
			<a onclick="createSurveyIgnoreChanges()" id="checkChangesDialogDontSaveButtonEditor" class="btn btn-default" data-dismiss="modal"><spring:message code="label.DontSave" /></a>
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

	<div class="modal" id="warning-eVote" data-backdrop="static" role="dialog">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<spring:message code="label.Warning" />
				</div>
				<div class="modal-body">
					<spring:message code="message.WarningEVote" />
				</div>
				<div class="modal-footer">
					<a class="btn btn-primary" data-dismiss="modal"><spring:message code="label.GotIt" /></a>
				</div>
			</div>
		</div>
	</div>
	
	<div class="modal" id="new-quiz-dialog" data-backdrop="static">
		<div class="modal-dialog">
		    <div class="modal-content">
			  <div class="modal-body">
			  	<div style="float: right">
			  		<img src="${contextpath}/resources/images/logo_Eusurvey.png" alt="EUSurvey logo" style="width:150px;" />
			  	</div>
			  	<div style="font-size: 20px; margin-bottom: 20px;"><spring:message code="label.QuizFunctionality" /></div>
			  	<spring:message code="text.QuizFunctionality" />&nbsp;			  	
			  	<a id="docEditorGuideEN" target="_blank" href="${contextpath}/resources/documents/Quiz_Guide.pdf"><spring:message code="label.QuizDocumentation" /></a>
			  	<a id="docEditorGuideFR" target="_blank" href="${contextpath}/resources/documents/Quiz_Guide_FR.pdf" style="display: none;"><spring:message code="label.QuizDocumentation" /></a>
				<a id="docEditorGuideDE" target="_blank" href="${contextpath}/resources/documents/Quiz_Guide_DE.pdf" style="display: none;"><spring:message code="label.QuizDocumentation" /></a>
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
		var isEVote = ${form.survey.isEVote};
		var isSelfAssessment = ${form.survey.isSelfAssessment};
		var eVoteTemplate = "${form.survey.eVoteTemplate}";
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
			$(window).scroll(function() {$('[data-toggle="tooltip"]').tooltip("hide");});
			
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

			if (isEVote)
			{
				<c:if test="${invalidevote != null}">
					$("#warning-eVote").modal("show");
				</c:if>
			}
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
							var model = getElementViewModel(result[i], true);
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
				SurveyRuleEvaluator.recalculate()
				
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
		
		function getCharacterCountInfo(max)
		{
			var s = '${form.getMessage("info.CharactersUsed", "[current]", "[max]")}';
			return s.replace("[max]", max).replace("[current]", "0");
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
	 		var s = '<spring:message code="limits.MinMaxChoicesNew" arguments="[min],[max]" />';
	 		return s.replace("[min]", min).replace("[max]", max);
	 	}
	 	function getMinChoice(min)
	 	{
	 		var s = '<spring:message code="limits.MinChoicesNew" arguments="[min]" />';
	 		return s.replace("[min]", min);
	 	}
	 	function getMaxChoice(max)
	 	{
	 		var s = '<spring:message code="limits.MaxChoicesNew" arguments="[max]" />';
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
			if (element.hasClass("formulaitem")) return "<spring:message code='label.Formula' />";
			if (element.hasClass("galleryitem")) return "<spring:message code='form.Gallery' />";
			if (element.hasClass("confirmationitem")) return "<spring:message code='form.Confirmation' />";
			if (element.hasClass("ratingitem")) return "<spring:message code='form.Rating' />";
			if (element.hasClass("ratingquestion")) return "<spring:message code='form.RatingQuestion' />";
			if (element.hasClass("matrix-header")) return "<spring:message code='form.MatrixElement' />";
			if (element.hasClass("table-header")) return "<spring:message code='form.Table' />";
			if (element.hasClass("answertext")) return "<spring:message code='label.Answer' />";
			if (element.hasClass("rankingitem")) return "<spring:message code='label.RankingQuestion' />";
			if (element.hasClass("rankingitemtext")) return "<spring:message code='label.RankingItem' />";
			if (element.hasClass("complextableitem")) return "<spring:message code='form.ComplexTable' />";
			if (element.find(".gallery-image").length > 0) return "<spring:message code='form.GalleryImage' />";
			if (element.hasClass("headercell")) {
				if (element.closest("tr").index() == 0) { //Check for row id
					if (element[0].cellIndex !== 0) { // Check for column id
						return "<spring:message code='label.Column' />";
					} else {
						return "<spring:message code='form.Cell' />";
					}
				}
				return "<spring:message code='label.Row' />";
			}
			if (element.hasClass("cell")) return "<spring:message code='form.Cell' />";
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
			if (element.hasClass("formulaitem")) return "idTypeformulaitem";
			if (element.hasClass("galleryitem")) return "idTypeSectionitem";
			if (element.hasClass("confirmationitem")) return "idTypegalleryitem";
			if (element.hasClass("ratingitem")) return "idTyperatingitem";
			if (element.hasClass("ratingquestion")) return "idTyperatingquestion";
			if (element.hasClass("matrix-header")) return "idTypematrix-header";
			if (element.hasClass("table-header")) return "idTypetable-header";
			if (element.hasClass("complextableitem")) return "idTypecomplextableitem";
			if (element.find(".gallery-image").length > 0) return "idTypegallery-image";
			return "idOtherType";
		}	 	
	 	
	 	var labelAddFileForDownload = '<spring:message code="message.AddFileForDownload" />';

		const strings = {};
		strings["Text"] = "<spring:message code="label.Text" />";
		strings["Rows"] = "<spring:message code="label.Rows" />";
		strings["TabTitle"] = "<spring:message code="label.TabTitle" />";
		strings["Level"] = "<spring:message code="label.Level" />";
		strings["Visibility"] = "<spring:message code="label.Visibility" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.visibility" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
		strings["StyleWithInfo"] = "<spring:message code="label.Style" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.StyleMC" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
		strings["alwaysVisible"] = "<spring:message code="label.alwaysVisible" />";
		strings["dependent"] = "<spring:message code="label.dependent" />";
		strings["Mandatory"] = "<spring:message code="label.Mandatory" />";
		strings["Help"] = "<spring:message code="label.HelpMessage" />";
		strings["Identifier"] = "<spring:message code="label.Identifier" />";
		const readonly = '<spring:message code="info.Readonly" />';
		strings["ReadOnly"] = "<spring:message code="label.Readonly" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='" + readonly + "'><span class='glyphicon glyphicon-question-sign'></span></a>";
		const readonlyFormula = '<spring:message code="info.ReadonlyFormula" />';
		strings["ReadOnlyFormula"] = "<spring:message code="label.Readonly" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='" + readonlyFormula + "'><span class='glyphicon glyphicon-question-sign'></span></a>";
		strings["AcceptedNumberOfCharacters"] = "<spring:message code="label.AcceptedNumberOfCharacters" />";
		strings["between"] = "<spring:message code="label.between" />";
		strings["and"] = "<spring:message code="label.and" />";
		strings["Unique"] = "<spring:message code="label.Unique" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.Unique" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
		strings["Comparable"] = "<spring:message code="label.Comparable" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.Comparable" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
		const info = "<spring:message code="info.Password" />".replace("'","`");
		strings["Password"] = "<spring:message code="label.Password" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='" + info + "'><span class='glyphicon glyphicon-question-sign'></span></a>";
		strings["Attribute"] = "<spring:message code="label.Attribute" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.Attribute" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
		strings["Name"] = "<spring:message code="label.Name" />";
		strings["RegistrationForm"] = "<spring:message code="label.RegistrationForm" />";
		strings["Advanced"] = "<spring:message code="label.Advanced" />";
		strings["invalidMinMaxCharacters"] = "<spring:message code="error.invalidMinMaxCharacters" />";
		strings["invalidInterdependencyCriteria"] = "<spring:message code="error.invalidInterdependencyCriteria" />";
		strings["invalidFormula"]= "<spring:message code="error.invalidFormula" />";
		strings["invalidFormulaBrackets"]= "<spring:message code="error.invalidFormulaBrackets" />";
		strings["invalidformulaUnknownID"]= "<spring:message code="error.invalidformulaUnknownID" />";
		strings["Style"] = "<spring:message code="label.Style" />";
		strings["Order"] = "<spring:message code="label.Order" />&nbsp;<a data-toggle='tooltip' data-html='true' data-placement='right' title='<spring:message code="info.Order" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
		strings["OrderSection"] = "<spring:message code="label.Order" />&nbsp;<a data-toggle='tooltip' data-html='true' data-placement='right' title='<spring:message code="info.OrderSection" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
		strings["Columns"] = "<spring:message code="label.Columns" />";
		strings["RadioButton"] = "<spring:message code="html.RadioButton" />";
		strings["SelectBox"] = "<spring:message code="html.SelectBox" />";
		strings["LikertScale"] = "<spring:message code="html.LikertScale" />";
		strings["Buttons"] = "<spring:message code="html.Button" />";
		strings["Original"] = "<spring:message code="label.OriginalOrder" />";
		strings["Alphabetical"] = "<spring:message code="label.AlphabeticalOrder" />";
		strings["Random"] = "<spring:message code="label.RandomOrder" />";
		strings["PossibleAnswers"] = "<spring:message code="label.PossibleAnswers" />";
		strings["RankingItems"] = "<spring:message code="label.RankingItems" />";
		strings["NumberOfChoices"] = "<spring:message code="label.NumberOfChoices" />";
		strings["CheckBox"] = "<spring:message code="html.CheckBox" />";
		strings["ListBox"] = "<spring:message code="html.ListBox" />";
		strings["EVoteList"] = "<spring:message code="html.EVoteList" />";
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
		strings["DescriptiveText"] = "<spring:message code="label.AlternativeText" />";
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
		strings["Formula"] = "<spring:message code="label.Formula" />";
		strings["ShowHeadersAndBorders"] = "<spring:message code="label.ShowHeadersAndBorders" />";
		strings["Empty"] = "<spring:message code="label.empty" />";
		strings["StaticText"] = "<spring:message code="label.StaticText" />";
		strings["FreeText"] = "<spring:message code="form.FreeText" />";
		strings["CellType"] = "<spring:message code="label.CellType" />";
		strings["invalidPaDeletion"] = "<spring:message code="validation.invalidPaDeletion" />";
		strings["invalidRankItemDeletion"] = "<spring:message code="validation.invalidRankItemDeletion" />";
		strings["invalidRatingDeletion"] = "<spring:message code="validation.invalidRatingDeletion" />";
		strings["Exclusive"] = "<spring:message code="label.Exclusive" />&nbsp;<a data-toggle='tooltip' data-html='true' data-placement='right' title='<spring:message code="info.Exclusive" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
		strings["DisplayAllQuestions"] = "<spring:message code="label.DisplayAllQuestions" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='<spring:message code="info.DisplayAllQuestions" />'><span class='glyphicon glyphicon-question-sign'></span></a>";
		strings["EvaluationCriteria"]= "<spring:message code="label.EvaluationCriteria" />";

		const hiddenInfo = '<spring:message code="info.Hidden" />'
		strings["Hidden"] = "<spring:message code="label.Hidden" />&nbsp;<a data-toggle='tooltip' data-placement='right' title='" + hiddenInfo + "'><span class='glyphicon glyphicon-question-sign'></span></a>";

	 	function getPropertyLabel(label)
	 	{
	 		if (!isNaN(label)) return label;
			
			if (label == "ReadOnly") {
				if ($(_elementProperties.selectedelement).hasClass("formulaitem")) {
					return strings["ReadOnlyFormula"];
				}
			}
			
	 		return strings[label];
	 	}
	 	
	 	$('[data-toggle="tooltip"]').ApplyCustomTooltips();
	</script>
