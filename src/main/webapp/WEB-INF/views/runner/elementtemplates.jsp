<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script type="text/javascript">
	var surveyUniqueId = "${form.survey.uniqueId}";
	var labelOf = " ${form.getMessage("label.of")} ";
	function tabpress(event){
		if (event.key === "Tab"){
			if (event.shiftKey){
				//If tabbing backwards and this is not the first radio button
				if (!event.target.matches(":first-of-type")){
					//Focus the previous radio button
					event.target.previousElementSibling.previousElementSibling.focus()
					event.preventDefault()
				}
			} else {
				//If tabbing forwards and this is not the last radio button
				if (!event.target.matches(":last-of-type")){
					//Focus the next radio button
					event.target.nextElementSibling.nextElementSibling.focus()
					event.preventDefault()
				}
			}
		}
	}
	
	function goToNextQuestion(link) {
		let next_question = $(link).closest("fieldset").next();
		 $('html, body').animate({
	        'scrollTop' : next_question.position().top - 20
	    });
		let focusable = [...next_question.get(0).querySelectorAll('button, [href], input, select, textarea, img, [tabindex]:not([tabindex="-1"])')];
		focusable= focusable.filter(
				el => !el.hasAttribute('disabled') && !el.getAttribute('aria-hidden') && el.type != "hidden",
		);
		focusable[0].focus();
	}
</script>

<div style="display: none">

	<div id="section-template">
		<div role="heading" data-bind="html: title, attr: {'data-level': level, 'class':'sectiontitle section' + level()}"></div>
		<!-- ko if: foreditor -->
			<input type="hidden" data-bind="value: 'section', attr: {'name': 'type' + id()}" />	
			<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />	
			<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />
			<input type="hidden" data-bind="value: useAndLogic, attr: {'name': 'useAndLogic' + id()}" />
			<input type="hidden" data-bind="value: order, attr: {'name': 'order' + id()}" />	
			<input type="hidden" data-bind="value: tabTitle, attr: {'name': 'tabtitle' + id()}" />	
			<input type="hidden" data-bind="value: level, attr: {'name': 'level' + id()}" />
			<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
		<!-- /ko -->
	</div>
	
	<div id="text-template">
		<div class="text" data-bind="html: title"></div>
		<!-- ko if: foreditor -->
			<input type="hidden" data-bind="value: 'text', attr: {'name': 'type' + id()}" />	
			<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />	
			<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />	
			<input type="hidden" data-bind="value: useAndLogic, attr: {'name': 'useAndLogic' + id()}" />	
			<input type="hidden" data-bind="value: true, attr: {'name': 'optional' + id()}" />
			<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
		<!-- /ko -->
	</div>
	
	<div id="formula-template">
		<label for="defaultFormulaTemplateID" class='questiontitle' data-bind='attr: {for: "answer" + id(), id: "questiontitle" + id()}'>
			<span class="screen-reader-only">${form.getMessage("form.Question")}</span>
			<span data-bind='html: title'></span>
			<span class="screen-reader-only" data-bind="if: help">${form.getMessage("form.HelpAvailable")}</span>
		</label>
		<span class='questionhelp' data-bind="html: niceHelp, attr:{id: 'questionhelp' + id()}"></span>
		
		<!-- ko if: min() != null && min() != 0 && max() != null && max() != 0 -->
			<div class='limits' data-bind="html: getMinMax(minString(), maxString()), attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
		<!-- ko if: min() != 0 && min() != null && (max() == 0 || max() == null) -->
			<div class='limits' data-bind="html: getMin(minString()), attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
		<!-- ko if: (min() == 0 || min() == null) && max() != null && max() != 0 -->
			<div class='limits' data-bind="html: getMax(maxString()), attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
		<!-- ko if: (min() == 0 || min() == null) && (max() == 0 || max() == null) -->
			<div class='limits' data-bind="attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
		
		<input id="defaultFormulaTemplateID" data-bind="enable: !readonly(), value: result, attr: {'id': 'answer' + id(), 'data-id':id(), 'data-shortname': shortname(), 'name' : (readonly() ? '' : 'answer' + id()), 'class':css(), 'aria-labelledby':'questiontitle' + id(), 'aria-describedby':'questioninfo' + id() + ' questionhelp' + id()}" oninput="propagateChange(this);" onblur="validateInput($(this).parent())" type="text" autocomplete="off" />
		
		<!-- ko if: readonly() -->
		<input type="hidden" data-bind="value: result, attr: {'name': 'answer' + id()}" />
		<!-- /ko -->
		
		<!-- ko if: foreditor -->
			<input type="hidden" data-bind="value: 'formula', attr: {'name': 'type' + id()}" />	
			<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />	
			<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />
			<input type="hidden" data-bind="value: useAndLogic, attr: {'name': 'useAndLogic' + id()}" />	
			<input type="hidden" data-bind="value: readonly, attr: {'name': 'readonly' + id()}" />
			<input type="hidden" data-bind="value: formula, attr: {'name': 'formula' + id()}" />	
			<input type="hidden" data-bind="value: min, attr: {'name': 'min' + id()}" />	
			<input type="hidden" data-bind="value: max, attr: {'name': 'max' + id()}" />
			<input type="hidden" data-bind="value: decimalPlaces, attr: {'name': 'decimalplaces' + id()}" />
			<input type="hidden" data-bind="value: true, attr: {'name': 'optional' + id()}" />
			<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
			<label hidden><textarea data-bind="text: help, attr: {'name': 'help' + id()}" ></textarea>${form.getMessage("label.Help")}</label>
		<!-- /ko -->
	</div>
	
	<div id="image-template">
		
		<div class='alignment-div' data-bind="attr: {'style': 'width: 920px; max-width: 100%; text-align:' + align()}">
			<img style="max-width: 100%" alt="${form.getMessage("form.ImageItem")}" data-bind="attr: {'src': url, 'alt': originalTitle() + (longdesc != '' ? '; URL ' + longdesc() : ''), 'width': usedwidth() > 0 ? usedwidth() : ''}" />
		</div>
		
		<!-- ko if: foreditor -->
			<input type="hidden" data-bind="value: 'image', attr: {'name': 'type' + id()}" />	
			<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />	
			<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />	
			<input type="hidden" data-bind="value: useAndLogic, attr: {'name': 'useAndLogic' + id()}" />	
			<input type="hidden" data-bind="value: title, attr: {'name': 'name' + id()}" />	
			<input type="hidden" data-bind="value: scale, attr: {'name': 'scale' + id()}" />	
			<input type="hidden" data-bind="value: width, attr: {'name': 'width' + id()}" />	
			<input type="hidden" data-bind="value: align, attr: {'name': 'align' + id()}" />	
			<input type="hidden" data-bind="value: url, attr: {'name': 'url' + id()}" />	
			<input type="hidden" data-bind="value: filename, attr: {'name': 'filename' + id()}" />	
			<input type="hidden" data-bind="value: longdesc, attr: {'name': 'longdesc' + id()}" />	
			<input type="hidden" data-bind="value: true, attr: {'name': 'optional' + id()}" />
			<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
		<!-- /ko -->
	</div>

	<div id="ruler-template">
		<hr data-bind="attr: {'style': 'border-top: ' + height() + 'px ' + style() + ' ' + color() }" />
		<!-- ko if: foreditor -->
			<input type="hidden" data-bind="value: 'ruler', attr: {'name': 'type' + id()}" />	
			<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />	
			<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />	
			<input type="hidden" data-bind="value: useAndLogic, attr: {'name': 'useAndLogic' + id()}" />	
			<input type="hidden" data-bind="value: true, attr: {'name': 'optional' + id()}" />
			<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
			<input type="hidden" data-bind="value: color, attr: {'name': 'color' + id()}" />
			<input type="hidden" data-bind="value: height, attr: {'name': 'height' + id()}" />
			<input type="hidden" data-bind="value: style, attr: {'name': 'style' + id()}" />			
		<!-- /ko -->
	</div>
	
	<div id="single-choice-template">
		<!-- ko if: optional() == false -->
			<span class="mandatory">*</span>
		<!-- /ko -->

		<span class='questiontitle' data-bind="attr: {id: 'questiontitle' + id()}">
			<span class="screen-reader-only">${form.getMessage("form.Question")}</span>
			<span data-bind='html: title'></span>
			<span class="screen-reader-only" data-bind="if: help">${form.getMessage("form.HelpAvailable")}</span>
		</span>
		<span class='questionhelp' data-bind="html: niceHelp, attr:{id: 'questionhelp' + id()}"></span>
		<div class="answer-columns" style="position: relative; overflow-x:auto; padding-bottom: 8px; padding-top: 4px;">
		
			<!-- ko if: likert() && !(ismobile || istablet) -->
						
				<div style="margin-top: 30px; display: inline-block; position: relative;" role="radiogroup" data-bind="attr: {'class' : maxDistance() > -1 ? 'likert-div median answers-table' : 'likert-div answers-table', 'aria-labelledby':'questiontitle' + id(), 'aria-describedby':' questionhelp' + id()}, style: { width: possibleAnswers().length * 100 + 'px' }">

					<div class="likert-bar" data-bind="attr: {'style' : 'width: ' + (possibleAnswers().length - 1) + '00px;'}"></div>
				
					<!-- ko foreach: possibleAnswers() -->
					
					<div class="likert-pa">
						<input id="defaultSCLikertTemplateID" data-bind="enable: !$parents[0].readonly() && !$parents[0].foreditor, checked: getPAByQuestion2($parents[0].uniqueId(), uniqueId(), id()), attr: {'data-id': $parents[0].id() + '' + id(), 'data-shortname': shortname(), 'data-dependencies': dependentElementsString(), onkeyup: 'singleKeyUp(event, this, '+$parents[0].readonly()+')', onclick: $parents[0].readonly() ? 'return false;' : 'singleClick(this); checkDependenciesAsync(this);', class: $parents[0].css + ' trigger check', name: 'answer' + $parents[0].id(), id: 'answer' + id(), value: id(), 'aria-labelledby': 'answerlabel' + id()}" type="radio" />
						<div><label for="defaultSCLikertTemplateID" class="answertext" style="margin-left: 0; padding-left: 10px; padding-right: 10px;" data-bind="attr: {'data-id' : id(), 'data-pa-uid' : uniqueId(), id: 'answerlabel' + id(), for: 'answer' + id()}">
							<span class="screen-reader-only">${form.getMessage("label.Answer")} </span>
							<span data-bind="html: titleForDisplayMode($parents[0].displayMode())"></span>
						</label></div>
					</div>
					<!-- /ko -->
					
					<div style="clear: both"></div>						
					
				</div>		
			
				<!-- ko if: foreditor -->
						<!-- ko foreach: possibleAnswers() -->
							<div class="possibleanswerrow hidden">
								<div class="answertext" data-bind="html: title, attr: {'id' : id(), 'data-id' : id()}"></div>
							</div>
						<!-- /ko -->
					<!-- /ko -->		
			<!-- /ko -->
			
			<!-- ko if: ismobile || istablet || !likert() -->
			
				<!-- ko if: likert() || useRadioButtons() -->
				
				<!-- ko if: likert() -->
					<div class="likert-table-div"></div>
				<!-- /ko -->	
									
				<table class="answers-table" role="radiogroup" data-bind="attr: {'aria-labelledby':'questiontitle' + id(), 'aria-describedby':'questionhelp' + id()}">
					<tr class="hideme">
						<th>radio button</th>
						<th>label</th>
					</tr>
					
					<!-- ko foreach: orderedPossibleAnswersByRows(${ismobile != null}, ${responsive != null}) -->
					<tr class="possibleanswerrow">				
						<!-- ko foreach: $data -->					
											
						<td style="vertical-align: top">
							<!-- ko ifnot: id() == 'dummy' -->
								<input id="defaultSCRadioTemplateID" style="position: relative" data-bind="enable: !$parents[1].readonly() && !$parents[1].foreditor, checked: getPAByQuestion2($parents[1].uniqueId(), uniqueId(), id()), attr: {'data-id': $parents[1].id() + '' + id(), 'id': id(), 'data-shortname': shortname(), 'data-dependencies': dependentElementsString(), onkeyup: 'singleKeyUp(event, this, '+$parents[1].readonly()+')', onclick: $parents[1].readonly() ? 'return false;' : 'singleClick(this); checkDependenciesAsync(this);', class: $parents[1].css + ' trigger check', name: 'answer' + $parents[1].id(), value: id(), 'aria-labelledby': 'answerlabel' + id(), 'previousvalue': getPAByQuestion2($parents[1].uniqueId(), uniqueId(), id()) != '' ? 'checked' : 'false'}", type="radio" />
							<!-- /ko -->	
						</td>
						<td style="vertical-align: top; padding-right: 15px;">
							<!-- ko ifnot: id() == 'dummy' -->
							<label for="defaultSCRadioTemplateID" data-bind="attr: {'for': id, 'id': 'answerlabel' + id()}">
								<span class="screen-reader-only">${form.getMessage("label.Answer")} </span>			
							
								<div class="answertext" data-bind="html: titleForDisplayMode($parents[1].displayMode()), attr: {'data-id' : id()}"></div>
							</label>
							<!-- /ko -->							
						</td>					
					
						<!-- /ko -->
					</tr>
					<!-- /ko -->
				</table>
				<!-- /ko -->
				<!-- ko if: useSelectBox -->
				<div class="answer-column">		
					<select id="defaultSCSelectTemplateID" data-bind="foreach: orderedPossibleAnswers(false), enable: !readonly(), valueAllowUnset: true, value: getPAByQuestion3(uniqueId()), attr: {'id': 'answer' + id(), 'oninput': !foreditor ? 'validateInput($(this).parent(),true); checkDependenciesAsync(this); propagateChange(this);' : '', 'data-id':id(), 'data-shortname': shortname(), 'name' : 'answer' + id(), 'class': css + ' single-choice', 'aria-labelledby':'questiontitle' + id(), 'aria-describedby':'questionhelp' + id()}" >
						<option data-bind="html: strip_tags(titleForDisplayMode($parents[0].displayMode())), attr: {value: id(), 'data-dependencies': dependentElementsString(), 'id': 'trigger'+id()}" class="possible-answer trigger"></option>
					</select>
					<label for="defaultSCSelectTemplateID" data-bind="attr: {'for': 'answer' + id()}" hidden>
						<span class="screen-reader-only">${form.getMessage("html.SelectBox")}</span>
					</label>
					<!-- ko if: foreditor -->
						<!-- ko foreach: possibleAnswers() -->
							<div class="possibleanswerrow hidden">
								<div class="answertext" data-bind="html: title, attr: {'id' : id(), 'data-id' : id()}"></div>
							</div>
						<!-- /ko -->
					<!-- /ko -->					
				</div>
				<!-- /ko -->
			<!-- /ko -->
			<!-- ko if: useButtons -->
			<table class="answers-table" role="radiogroup" data-bind="attr: {'aria-labelledby':'questiontitle' + id(), 'aria-describedby':'questionhelp' + id()}">

				<tr class="possibleanswerrow">
					<th style='padding: 2px; display: flex; align-items: center; flex-wrap: wrap'>
						<!-- ko foreach: orderedPossibleAnswers(false) -->

							<!-- ko ifnot: id() == 'dummy' -->
								<input id="defaultSCButtonTemplateID" tabindex="0" style="clip-path: circle(0); position: absolute;" type="radio" onkeydown="tabpress(event)"
									   data-bind="enable: !$parent.readonly() && !$parent.foreditor, checked: getPAByQuestion2($parent.uniqueId(), uniqueId(), id()), attr: {'data-id': $parent.id() + '' + id(), 'id': id(), 'data-shortname': shortname(), 'data-dependencies': dependentElementsString(), onkeyup: 'singleKeyUp(event, this, '+$parent.readonly()+')', onclick: $parent.readonly() ? 'return false;' : 'singleClick(this); checkDependenciesAsync(this);', class: $parent.css + ' trigger check', name: 'answer' + $parent.id(), value: id(), 'aria-labelledby': 'answerlabel' + id(), 'previousvalue': getPAByQuestion2($parent.uniqueId(), uniqueId(), id()) != '' ? 'checked' : 'false'}" />
								<label for="defaultSCButtonTemplateID" class="choice-button-label answertext" data-bind="attr: {'for': id, 'id': 'answerlabel' + id(), 'data-id' : id()}">
									<span class="screen-reader-only">${form.getMessage("label.Answer")} </span>
									<span data-bind="html: titleForDisplayMode($parent.displayMode())"></span>
								</label>
							<!-- /ko -->
						<!-- /ko -->
					</th>
				</tr>

			</table>
			<!-- /ko -->
			<input type="hidden" data-bind="value: choiceType, attr: {'name': 'choicetype' + id()}" />
					
			<!-- ko if: foreditor -->
				<input type="hidden" data-bind="value: 'choice', attr: {'name': 'type' + id()}" />
				<input type="hidden" data-bind="value: 'true', attr: {'name': 'single' + id()}" />
				<input type="hidden" data-bind="value: optional, attr: {'name': 'optional' + id()}" />
				<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />
				<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />
				<input type="hidden" data-bind="value: useAndLogic, attr: {'name': 'useAndLogic' + id()}" />	
				<input type="hidden" data-bind="value: order, attr: {'name': 'order' + id()}" />
				<input type="hidden" data-bind="value: readonly, attr: {'name': 'readonly' + id()}" />
				<input type="hidden" data-bind="value: isAttribute, attr: {'name': 'attribute' + id()}" />
				<input type="hidden" data-bind="value: isDelphiQuestion, attr: {'name': 'delphiquestion' + id()}" />
				<input type="hidden" data-bind="value: showExplanationBox, attr: {'name': 'explanationbox' + id()}" />
				<input type="hidden" data-bind="value: delphiChartType, attr: {'name': 'delphicharttype' + id()}" />
				<input type="hidden" data-bind="value: attributeName, attr: {'name': 'nameattribute' + id()}" />
				<input type="hidden" data-bind="value: numColumns, attr: {'name': 'columns' + id()}" />
				<input type="hidden" data-bind="value: 0, attr: {'name': 'choicemin' + id()}" />
				<input type="hidden" data-bind="value: 0, attr: {'name': 'choicemax' + id()}" />
				<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
				<label hidden><textarea data-bind="text: help, attr: {'name': 'help' + id()}" ></textarea>${form.getMessage("label.Help")}</label>

				<input type="hidden" data-bind="value: scoring, attr: {'name': 'scoring' + id()}" />
				<input type="hidden" data-bind="value: points, attr: {'name': 'points' + id()}" />
				
				<input type="hidden" data-bind="value: subType, attr: {'name': 'subType' + id()}" />
				<input type="hidden" data-bind="value: displayMode, attr: {'name': 'displayMode' + id()}" />
				
				<input type="hidden" data-bind="value: maxDistance, attr: {'name': 'maxDistance' + id()}" />
				<input type="hidden" data-bind="value: editorRowsLocked(), attr: {'name': 'editorRowsLocked' + id()}" />


				<tr class="hideme">
					<td>
						<!-- ko foreach: possibleAnswers() -->
						<input type="hidden" data-bind="value: dependentElementsString(), attr: {'name': 'dependencies' + $parent.id(), 'data-id' : id()}" />
						<input type="hidden" data-bind="value: shortname, attr: {'name': 'pashortname' + $parent.id(), 'data-id' : id()}" />
						<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'pauid' + $parent.id(), 'data-id' : id()}" />
						<label hidden><textarea data-bind="text: title, attr: {'name': 'answer' + $parent.id(), 'data-id' : id()}" ></textarea>${form.getMessage("label.Title")}</label>
						<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'originalAnswer' + $parent.id(), 'data-id' : id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
						<input type="hidden" data-bind="value: scoring.correct, attr: {'name': 'correct' + $parent.id(), 'data-id' : id()}" />
						<input type="hidden" data-bind="value: scoring.points, attr: {'name': 'answerpoints' + $parent.id(), 'data-id' : id()}" />
						<input type="hidden" data-bind="value: scoring.feedback, attr: {'name': 'feedback' + $parent.id(), 'data-id' : id()}" />
						<!-- /ko -->
					</td>
				</tr>
			<!-- /ko -->		
		</div>
	</div>
	
	<div id="multiple-choice-template">
		<!-- ko if: optional() == false -->
			<span class="mandatory">*</span>
		<!-- /ko -->
	
		<span class='questiontitle' data-bind='attr: {id: "questiontitle" + id()}'>
			<span class="screen-reader-only">${form.getMessage("form.Question")}</span>
			<span data-bind='html: title'></span>
			<span class="screen-reader-only" data-bind="if: help">${form.getMessage("form.HelpAvailable")}</span>
		</span>
		
		<span class='questionhelp' data-bind="html: niceHelp, attr:{id: 'questionhelp' + id()}"></span>
			
		<!-- ko if: minChoices() != 0 && maxChoices() != 0 -->
			<div class='limits' data-bind="html: getMinMaxChoice(minChoices(), maxChoices()), attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
		<!-- ko if: minChoices() != 0 && maxChoices() == 0 -->
			<div class='limits' data-bind="html: getMinChoice(minChoices()), attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
		<!-- ko if: minChoices() == 0 && maxChoices() != 0 -->
			<div class='limits' data-bind="html: getMaxChoice(maxChoices()), attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
		<!-- ko if: minChoices() == 0 && maxChoices() == 0 -->
			<div class='limits' data-bind="attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
	
		<div class="answer-columns" style="overflow-x:auto;padding-top:4px;padding-bottom:8px;">
			<!-- ko if: useCheckboxes -->
			<table class="answers-table" role="list" data-bind="attr: {'aria-labelledby':'questiontitle' + id(), 'aria-describedby':'questioninfo' + id() + ' questionhelp' + id()}">
				<tr class="hideme">
					<th>checkbox</th>
					<th>label</th>
				</tr>
				
				<!-- ko foreach: orderedPossibleAnswersByRows(${ismobile != null}, ${responsive != null}) -->
				<tr class="possibleanswerrow" role="listitem">					
					<!-- ko foreach: $data -->
					<td style="vertical-align: top">
						<!-- ko ifnot: id() == 'dummy' -->
							<input id="defaultMCCheckBoxTemplateID" data-bind="enable: !$parents[1].readonly() && !$parents[1].foreditor, checked: !$parents[1].foreditor && getPAByQuestionCheckBox($parents[1].uniqueId(), uniqueId()).indexOf(uniqueId()) > -1, attr: {'data-id': $parents[1].id() + '' + id(), 'id': id(), 'data-shortname': shortname(), 'data-exclusive': exclusive(), 'data-dependencies': dependentElementsString(), onclick: $parents[1].readonly() ? 'return false;' : 'findSurveyElementAndResetValidationErrors(this); singleClick(this); checkDependenciesAsync(this);', class: $parents[1].css + ' trigger check' + (exclusive() ? ' exclusive' : ''), name: 'answer' + $parents[1].id(), value: id(), 'aria-labelledby': 'answerlabel' + id()}" type="checkbox" />
						<!-- /ko -->
					</td>
					<td style="vertical-align: top; padding-right: 10px;">
						<!-- ko ifnot: id() == 'dummy' -->
						<label for="defaultMCCheckBoxTemplateID" data-bind="attr: {'for': id, 'id': 'answerlabel' + id()}">
							<span class="screen-reader-only">${form.getMessage("label.Answer")} </span>			
						
							<!-- ko ifnot: id() == 'dummy' -->
							<div class="answertext" data-bind="html: title, attr: {'data-id' : id()}"></div>
							<!-- /ko -->	
						</label>						
						<!-- /ko -->
					</td>	
					<!-- /ko -->
				</tr>		
				<!-- /ko -->	
			</table>
			<!-- /ko -->
			<!-- ko if: useListBox -->
			<div class="answer-column">													
				<ul role="listbox" data-bind="foreach: orderedPossibleAnswers(false), attr: {'class':css + ' multiple-choice', 'aria-labelledby':'questiontitle' + id(), 'aria-describedby':'questioninfo' + id() + ' questionhelp' + id()}">
					<li role="listitem" data-bind="attr: { 'data-id': id(), 'class': 'possible-answer trigger ' + (getPAByQuestion($parent.uniqueId()).indexOf(uniqueId()) > -1 ? 'selected-choice' : '') }">
						<label for="defaultMCListBoxTemplateID" data-bind="attr: {for: id()}">
							<a tabindex="0" data-bind="attr: {'data-shortname': shortname(), 'onkeypress': $parent.readonly() || $parent.foreditor ? 'return false;' : 'preventScrollOnSpaceInput(event);findSurveyElementAndResetValidationErrors(this);selectMultipleChoiceAnswer(this);propagateChange(this);', 'onclick' : $parent.readonly() || $parent.foreditor ? 'return false;' : 'selectMultipleChoiceAnswer($(this)); propagateChange($(this)); event.stopImmediatePropagation();'}" >
								<span class="screen-reader-only">${form.getMessage("label.Answer")} </span>
								<span data-bind="html: strip_tags(title()), attr: {'data-id' : id(), 'id': 'answerlabel' + id()}" class="answertext"></span>
							</a>
						</label>
						<input id="defaultMCListBoxTemplateID" data-bind="value: id(), checked: getPAByQuestion2($parent.uniqueId(), uniqueId(), id), attr: {'name': 'answer' + $parent.id(), 'id':id(), 'data-id': $parent.id() + id(), 'data-dependencies': dependentElementsString, 'aria-labelledby': 'answerlabel' + id()}" style="display: none" type="checkbox" />
					</li>	
				</ul>
				
				<!-- ko if: foreditor -->
					<!-- ko foreach: possibleAnswers() -->
					<div class="possibleanswerrow hidden">
						<div class="answertext" data-bind="html: title, attr: {'id' : id(), 'data-id' : id()}"></div>
					</div>
					<!-- /ko -->
				<!-- /ko -->		
			</div>
			<div style="clear: both"></div>
			<!-- /ko -->
			<!-- ko if: isEVoteList -->
			<table role="list" collapsed data-bind="attr: {class:'answers-table evote-table ' + choiceTypeWithEVote('${form.survey.geteVoteTemplate()}'), 'aria-labelledby':'questiontitle' + id(), 'aria-describedby':'questioninfo' + id() + ' questionhelp' + id()}">
				<tr>
					<th style="width: 20px">
						<c:if test="${form.survey.geteVoteTemplate() == 'b' ||form.survey.geteVoteTemplate() == 'i'}">
							<input id="defaultMCEVoteTemplateID" data-bind="enable: !readonly() && !foreditor, attr: {'data-id': id() + 'evote-all', 'id': id() + 'evote-all', onclick: readonly() ? 'return false;' : 'eVoteEntireListClick(this)', class: css + ' trigger check entire-list', name: 'answer' + id(), value: 'EVOTE-ALL'}" type="checkbox" />
						</c:if>
						<c:if test="${form.survey.geteVoteTemplate() == 'o'}">
							<input id="defaultMCEVoteTemplateID" data-bind="enable: !readonly() && !foreditor, attr: {'id': id() + 'evote-all', onclick: readonly() ? 'return false;' : 'eVoteEntireListClick(this)', class: css + ' trigger check entire-list'}" type="checkbox" />
						</c:if>
						<span class="sr-only">Checkbox</span>
					</th>
					<th style="display: flex; flex-flow: row nowrap; justify-content: space-between; height: inherit; min-width: 155px;">
						<div style="padding-right: 24px; align-self: center">
							<c:if test="${form.survey.geteVoteTemplate() == 'b' || form.survey.geteVoteTemplate() == 'i' || form.survey.geteVoteTemplate() == 'o'}">
								<label for="defaultMCEVoteTemplateID" data-bind="attr: {'for': id() + 'evote-all'}">
									<c:if test="${form.survey.geteVoteTemplate() == 'b' || form.survey.geteVoteTemplate() == 'i'}">
										${form.getMessage("label.EntireList")}
									</c:if>
									<c:if test="${form.survey.geteVoteTemplate() == 'o'}">
										${form.getMessage("label.eVoteSelectAll")}
									</c:if>
								</label>
							</c:if>
						</div>
						<div class="evote-collapse" tabindex="0" onclick="$(this).closest('.evote-table').attr('collapsed', (_, val) => val == null ? '' : null); event.stopImmediatePropagation(); event.preventDefault()" onkeypress="$(this).closest('.evote-table').attr('collapsed', (_, val) => val == null ? '' : null); event.stopImmediatePropagation(); event.preventDefault()">
							<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48"><path d="m24 30.75-12-12 2.15-2.15L24 26.5l9.85-9.85L36 18.8Z"></path></svg>
						</div>
						<span class="sr-only">label</span>
					</th>
				</tr>			

				<!-- ko foreach: orderedPossibleAnswers(false) -->
				<tr class="possibleanswerrow" role="listitem">
					<td>
						<!-- ko ifnot: id() == 'dummy' -->
							<input id="defaultMCEVoteElementsTemplateID" data-bind="enable: !$parent.readonly() && !$parent.foreditor, checked: !$parent.foreditor && getPAByQuestion($parent.uniqueId()).indexOf(uniqueId()) > -1, event: { evoteuncheck: ()=>{ element.checked } }, attr: {'data-id': $parent.id() + '' + id(), 'id': id(), 'data-shortname': shortname(), 'data-dependencies': dependentElementsString(), onclick: $parent.readonly() || $element.disabled ? 'return false;' : 'findSurveyElementAndResetValidationErrors(this); singleClick(this); checkDependenciesAsync(this); updateEVoteList(this);', class: $parent.css + ' trigger check evote-candidate', name: 'answer' + $parent.id(), value: id(), 'aria-labelledby': 'answerlabel' + id()}" type="checkbox" />
						<!-- /ko -->
					</td>
					<td style="padding-right: 10px;">
						<!-- ko ifnot: id() == 'dummy' -->
						<label for="defaultMCEVoteElementsTemplateID" data-bind="attr: {'for': id, 'id': 'answerlabel' + id()}">
							<span class="screen-reader-only">${form.getMessage("label.Answer")} </span>

							<!-- ko ifnot: id() == 'dummy' -->
							<div class="answertext" data-bind="html: title, attr: {'data-id' : id()}"></div>
							<!-- /ko -->
						</label>
						<!-- /ko -->
					</td>
				</tr>
				<!-- /ko -->
			</table>
			<!-- /ko -->
		
			<input type="hidden" data-bind="value: choiceType, attr: {'name': 'choicetype' + id()}" />

			<!-- ko if: foreditor -->
				<input type="hidden" data-bind="value: 'choice', attr: {'name': 'type' + id()}" />
				<input type="hidden" data-bind="value: 'false', attr: {'name': 'single' + id()}" />
				<input type="hidden" data-bind="value: optional, attr: {'name': 'optional' + id()}" />
				<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />
				<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />
				<input type="hidden" data-bind="value: useAndLogic, attr: {'name': 'useAndLogic' + id()}" />	
				<input type="hidden" data-bind="value: order, attr: {'name': 'order' + id()}" />
				<input type="hidden" data-bind="value: readonly, attr: {'name': 'readonly' + id()}" />
				<input type="hidden" data-bind="value: isAttribute, attr: {'name': 'attribute' + id()}" />
				<input type="hidden" data-bind="value: isDelphiQuestion, attr: {'name': 'delphiquestion' + id()}" />
				<input type="hidden" data-bind="value: showExplanationBox, attr: {'name': 'explanationbox' + id()}" />
				<input type="hidden" data-bind="value: delphiChartType, attr: {'name': 'delphicharttype' + id()}" />
				<input type="hidden" data-bind="value: attributeName, attr: {'name': 'nameattribute' + id()}" />
				<input type="hidden" data-bind="value: numColumns, attr: {'name': 'columns' + id()}" />
				<!--input type="hidden" data-bind="value: choiceType, attr: {'name': 'choicetype' + id()}" /-->
				<input type="hidden" data-bind="value: minChoices, attr: {'name': 'choicemin' + id()}" />
				<input type="hidden" data-bind="value: maxChoices, attr: {'name': 'choicemax' + id()}" />
				<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
				<label hidden><textarea data-bind="text: help, attr: {'name': 'help' + id()}" ></textarea>${form.getMessage("label.Help")}</label>

				<input type="hidden" data-bind="value: scoring, attr: {'name': 'scoring' + id()}" />
				<input type="hidden" data-bind="value: points, attr: {'name': 'points' + id()}" />
				<input type="hidden" data-bind="value: noNegativeScore, attr: {'name': 'noNegativeScore' + id()}" />
		
				<input type="hidden" data-bind="value: subType, attr: {'name': 'subType' + id()}" />
				<input type="hidden" data-bind="value: displayMode, attr: {'name': 'displayMode' + id()}" />

				<input type="hidden" data-bind="value: editorRowsLocked(), attr: {'name': 'editorRowsLocked' + id()}" />

				<!-- ko foreach: possibleAnswers() -->
				<input type="hidden" data-bind="value: dependentElementsString(), attr: {'name': 'dependencies' + $parent.id(), 'data-id' : id()}" />
				<input type="hidden" data-bind="value: shortname, attr: {'name': 'pashortname' + $parent.id(), 'data-id' : id()}" />
				<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'pauid' + $parent.id(), 'data-id' : id()}" />
				<label hidden><textarea data-bind="text: title, attr: {'name': 'answer' + $parent.id(), 'data-id' : id()}" ></textarea>${form.getMessage("label.Title")}</label>
				<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'originalAnswer' + $parent.id(), 'data-id' : id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
				<input type="hidden" data-bind="value: scoring.correct, attr: {'name': 'correct' + $parent.id(), 'data-id' : id()}" />
				<input type="hidden" data-bind="value: scoring.points, attr: {'name': 'answerpoints' + $parent.id(), 'data-id' : id()}" />
				<input type="hidden" data-bind="value: scoring.feedback, attr: {'name': 'feedback' + $parent.id(), 'data-id' : id()}" />
				<input type="hidden" data-bind="value: exclusive, attr: {'name': 'exclusive' + $parent.id(), 'data-id' : id()}" />
				<!-- /ko -->
			<!-- /ko -->
		</div>
	</div>
	
	<div id="ranking-question-template">
		<!-- ko if: optional() == false -->
			<span class="mandatory">*</span>
		<!-- /ko -->
		<span class='questiontitle' data-bind='attr: {id: "questiontitle" + id()}'>
			<span class="screen-reader-only">${form.getMessage("form.Question")}</span>
			<span data-bind='html: title'></span>
			<span class="screen-reader-only" data-bind="if: help">${form.getMessage("form.HelpAvailable")}</span>
		</span>
		<span class='questionhelp' data-bind="html: niceHelp, attr:{id: 'questionhelp' + id()}"></span>

		<!-- ko if: foreditor -->
			<input type="hidden" data-bind="value: 'rankingquestion', attr: {'name': 'type' + id()}" />
			<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />
			<input type="hidden" data-bind="value: optional, attr: {'name': 'optional' + id()}" />
			<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />
			<input type="hidden" data-bind="value: isDelphiQuestion, attr: {'name': 'delphiquestion' + id()}" />
			<input type="hidden" data-bind="value: delphiChartType, attr: {'name': 'delphicharttype' + id()}" />
			<input type="hidden" data-bind="value: showExplanationBox, attr: {'name': 'explanationbox' + id()}" />
			<input type="hidden" data-bind="value: order, attr: {'name': 'order' + id()}" />
			<input type="hidden" data-bind="value: useAndLogic, attr: {'name': 'useAndLogic' + id()}" />

			<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
			<label hidden><textarea data-bind="text: help, attr: {'name': 'help' + id()}" ></textarea>${form.getMessage("label.Help")}</label>

			<div class="ranking-question-initial-answer-message" data-bind="hidden: isAnswered">
				${form.getMessage("label.HintOnInitialRankingOrderEditor")}
			</div>
		<!-- /ko -->
		
		<div role="group" data-bind="attr: {id: 'answer' + id(), 'aria-labelledby': 'questiontitle' + id(), 'aria-describedby' : 'questionhelp' + id() + ' listorderinfo' + id()}">

			<!-- ko ifnot: foreditor -->
				<div class="ranking-question-initial-answer-message" data-bind="hidden: isAnswered">
					${form.getMessage("label.HintOnInitialRankingOrder")}
				</div>
				<div class="question-reset-answer-message" data-bind="hidden: !isAnswered()">
					<a data-bind="click: resetOrder">${form.getMessage("label.ResetOrder")}</a>
				</div>
			<!-- /ko -->

			<div class="rankingitem-list-container" data-bind="attr: {id: 'ranking-item-list-container' + id()}">

				<!-- ko ifnot: foreditor -->
					<span class="screen-reader-only" data-bind="attr: {id: 'listorderinfo' + id()}">
						<span data-bind="html: getInitialOrderInfoText()"></span>
						<!-- ko foreach: orderedRankingItems() -->
						<span data-bind="html: title()"></span>
						<!-- /ko -->
					</span>
				<!-- /ko -->

				<div class="rankingitem-list" role="list">					

					<!-- ko foreach: orderedRankingItems() -->
					<div role="listitem" class="rankingitem-form-data focussable" data-bind="attr: {'aria-labelledby': id()}">
						<div class="rankingitem-decoration">&#x283F;</div>
						<a tabindex="0" role="button" class="rankingitem-button" data-toggle="tooltip" title="${form.getMessage("label.MoveUp")}" data-bind="click: onMoveUp, event: { keydown: onKeyDownMoveItemUp }, attr: {'aria-label' : title() + ' ${form.getMessage("label.MoveUp")}'}"><span class="glyphicon glyphicon-arrow-up"></span></a>
						<a tabindex="0" role="button" class="rankingitem-button" data-toggle="tooltip" title="${form.getMessage("label.MoveDown")}" data-bind="click: onMoveDown, event: { keydown: onKeyDownMoveItemDown }, attr: {'aria-label' : title() + ' ${form.getMessage("label.MoveDown")}'}"><span class="glyphicon glyphicon-arrow-down"></span></a>
						<div class="rankingitemtext" data-bind="html: title(), attr: {'id' : id(), 'data-id' : id()}"></div>
					</div>
					<!-- /ko -->
				</div>
			</div>

			<!-- ko if: foreditor -->
				<!-- ko foreach: rankingItems() -->
					<div class="possibleanswerrow hidden">
						<input type="hidden" data-bind="value: shortname, attr: {'name': 'rankingitemshortname' + $parents[0].id(), 'data-id' : id()}" />
						<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'rankingitemuid' + $parents[0].id(), 'data-id' : id()}" />
						<label hidden><textarea data-bind="text: title(), attr: {'name': 'rankingitemtitle' + $parents[0].id(), 'data-id' : id()}" ></textarea>${form.getMessage("label.Title")}</label>
						<label hidden><textarea data-bind="text: originalTitle(), attr: {'name': 'rankingitemoriginaltitle' + $parent.id(), 'data-id' : id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
					</div>
				<!-- /ko -->
			<!-- /ko -->

			<!-- ko ifnot: foreditor -->
				<input type="hidden" data-bind="value:getAnswerValuesString(), attr: {'id': 'answer' + id(), 'data-id':id(), 'data-shortname': shortname(), 'name' : 'answer' + id(), 'class':css()}" type="text"></input>
			<!-- /ko -->

		</div>
	</div>
	
	<div id="password-template">
		<!-- ko if: optional() == false -->
			<span class="mandatory">*</span>
		<!-- /ko -->
		<label for="defaultPasswordTemplateID" class='questiontitle' data-bind='attr: {for: "answer" + id(), id: "questiontitle" + id()}'>
			<span class="screen-reader-only">${form.getMessage("form.Question")}</span>
			<span data-bind='html: title'></span>
		</label>
		<span class='questionhelp' data-bind="html: niceHelp, attr:{id: 'questionhelp' + id()}"></span>
		<input id="defaultPasswordTemplateID" data-bind="enable: !readonly(), value:getValueByQuestion(uniqueId()), attr: {'id': 'answer' + id(), 'data-id':id(), 'data-shortname': shortname(), 'name' : 'answer' + id(), 'class':css(), 'aria-labelledby':'questiontitle' + id(), 'aria-describedby': 'questionhelp' + id()}" onfocus="clearStars(this);" onkeyup="countChar(this); propagateChange(this);" onblur="validateInput($(this).parent(), true)" autocomplete="off" type="password"></input>
		<!-- ko if: isComparable -->		
			<br /><label for="defaultPasswordSecondTemplateID" style="margin-left: 20px" data-bind="attr: {'for' : 'secondanswer' + id()}">${form.getMessage("label.PleaseRepeat")}</label>:<br />
			<input id="defaultPasswordSecondTemplateID" data-bind="enable: !readonly(), attr: {'id': 'answer' + id() + '2', 'data-id':id() + '2', 'name' : 'secondanswer' + id(), 'class': 'comparable-second ' + css()}" onfocus="clearStars(this);" autocomplete="off" type="password"></input>
		<!-- /ko -->
		<!-- ko if: foreditor -->
			<input type="hidden" data-bind="value: type == 'RegExQuestion' ? 'regex' : 'freetext', attr: {'name': 'type' + id()}" />	
			<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />	
			<input type="hidden" data-bind="value: optional, attr: {'name': 'optional' + id()}" />	
			<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />	
			<input type="hidden" data-bind="value: useAndLogic, attr: {'name': 'useAndLogic' + id()}" />	
			<input type="hidden" data-bind="value: readonly, attr: {'name': 'readonly' + id()}" />	
			<input type="hidden" data-bind="value: isAttribute, attr: {'name': 'attribute' + id()}" />	
			<input type="hidden" data-bind="value: isDelphiQuestion, attr: {'name': 'delphiquestion' + id()}" />
			<input type="hidden" data-bind="value: showExplanationBox, attr: {'name': 'explanationbox' + id()}" />
			<input type="hidden" data-bind="value: attributeName, attr: {'name': 'nameattribute' + id()}" />
			<input type="hidden" data-bind="value: numRows, attr: {'name': 'rows' + id()}" />
			<input type="hidden" data-bind="value: minCharacters, attr: {'name': 'min' + id()}" />
			<input type="hidden" data-bind="value: maxCharacters, attr: {'name': 'max' + id()}" />
			<input type="hidden" data-bind="value: isPassword, attr: {'name': 'password' + id()}" />
			<input type="hidden" data-bind="value: isUnique, attr: {'name': 'unique' + id()}" />
			<input type="hidden" data-bind="value: isComparable, attr: {'name': 'comparable' + id()}" />
			<label hidden><textarea data-bind="text: help, attr: {'name': 'help' + id()}" ></textarea>${form.getMessage("label.Help")}</label>
			<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
	
			<!--  ko foreach: scoringItems() -->
			<input type="hidden" data-bind="value: id, attr: {'name': 'scoringitem' + $parent.id()}" />
			<input type="hidden" data-bind="value: type, attr: {'name': 'type' + id()}" />
			<input type="hidden" data-bind="value: correct, attr: {'name': 'correct' + id()}" />	
			<input type="hidden" data-bind="value: value, attr: {'name': 'value' + id()}" />
			<input type="hidden" data-bind="value: feedback, attr: {'name': 'feedback' + id()}" />
			<input type="hidden" data-bind="value: points, attr: {'name': 'points' + id()}" />
			<!-- /ko -->
		<!-- /ko -->
	</div>
	
	<div id="freetext-template">	
		<!-- ko if: optional() == false -->
			<span class="mandatory">*</span>
		<!-- /ko -->

		<!-- ko if: maxCharacters() > 0 -->
			<label for="defaultFreetextBiggerZeroTemplateID" class='questiontitle' data-bind='attr: {for: "answer" + id(), id: "questiontitle" + id()}'>
				<span class="screen-reader-only">${form.getMessage("form.Question")}</span>
				<span data-bind='html: title'></span>
				<span class="screen-reader-only" data-bind="if: help">${form.getMessage("form.HelpAvailable")}</span>
			</label>
		<!-- /ko -->
		<!-- ko if: maxCharacters() == 0 -->
		<label for="defaultFreetextZeroTemplateID" class='questiontitle' data-bind='attr: {for: "answer" + id(), id: "questiontitle" + id()}'>
			<span class="screen-reader-only">${form.getMessage("form.Question")}</span>
			<span data-bind='html: title'></span>
			<span class="screen-reader-only" data-bind="if: help">${form.getMessage("form.HelpAvailable")}</span>
		</label>
		<!-- /ko -->
		<span class='questionhelp' data-bind="html: niceHelp, attr:{id: 'questionhelp' + id()}"></span>
				
		<!-- ko if: minCharacters() != 0 && maxCharacters() != 0 -->
			<div class='limits' data-bind="html: getMinMaxCharacters(minCharacters(), maxCharacters()), attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
		<!-- ko if: minCharacters() != 0 && maxCharacters() == 0 -->
			<div class='limits' data-bind="html: getMinCharacters(minCharacters()), attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
		<!-- ko if: minCharacters() == 0 && maxCharacters() != 0 -->
			<div class='limits' data-bind="html: getMaxCharacters(maxCharacters()), attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
		<!-- ko if: minCharacters() == 0 && maxCharacters() == 0 -->
			<div class='limits' data-bind="attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
	
		<!-- ko if: type == "RegExQuestion" -->
			<input type="hidden" data-bind="value: regex, attr: {'name': 'regex' + id()}" />
		<!-- /ko -->
	
		<!-- ko if: foreditor -->
			<!-- ko if: type == "RegExQuestion" -->
				<input type="hidden" data-bind="value: 'regex', attr: {'name': 'type' + id()}" />
			<!-- /ko -->
			<!-- ko ifnot: type == "RegExQuestion" -->
				<input type="hidden" data-bind="value: 'freetext', attr: {'name': 'type' + id()}" />	
			<!-- /ko -->
			
			<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />	
			<input type="hidden" data-bind="value: optional, attr: {'name': 'optional' + id()}" />	
			<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />	
			<input type="hidden" data-bind="value: useAndLogic, attr: {'name': 'useAndLogic' + id()}" />	
			<input type="hidden" data-bind="value: readonly, attr: {'name': 'readonly' + id()}" />	
			<input type="hidden" data-bind="value: isAttribute, attr: {'name': 'attribute' + id()}" />	
			<input type="hidden" data-bind="value: isDelphiQuestion, attr: {'name': 'delphiquestion' + id()}" />
			<input type="hidden" data-bind="value: showExplanationBox, attr: {'name': 'explanationbox' + id()}" />
			<input type="hidden" data-bind="value: delphiChartType, attr: {'name': 'delphicharttype' + id()}" />
			<input type="hidden" data-bind="value: attributeName, attr: {'name': 'nameattribute' + id()}" />
			<input type="hidden" data-bind="value: numRows, attr: {'name': 'rows' + id()}" />
			<input type="hidden" data-bind="value: minCharacters, attr: {'name': 'min' + id()}" />
			<input type="hidden" data-bind="value: maxCharacters, attr: {'name': 'max' + id()}" />
			<input type="hidden" data-bind="value: isPassword, attr: {'name': 'password' + id()}" />
			<input type="hidden" data-bind="value: isUnique, attr: {'name': 'unique' + id()}" />
			<input type="hidden" data-bind="value: isComparable, attr: {'name': 'comparable' + id()}" />
			<label hidden><textarea data-bind="text: help, attr: {'name': 'help' + id()}" ></textarea>${form.getMessage("label.Help")}</label>
			<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
			
			<input type="hidden" data-bind="value: scoring, attr: {'name': 'scoring' + id()}" />
			<input type="hidden" data-bind="value: points, attr: {'name': 'points' + id()}" />
			
			<!--  ko foreach: scoringItems() -->
				<input type="hidden" data-bind="value: id, attr: {'name': 'scoringitem' + $parent.id()}" />
				<input type="hidden" data-bind="value: type, attr: {'name': 'type' + id()}" />
				<input type="hidden" data-bind="value: correct, attr: {'name': 'correct' + id()}" />
				<input type="hidden" data-bind="value: value, attr: {'name': 'value' + id()}" />
				<input type="hidden" data-bind="value: feedback, attr: {'name': 'feedback' + id()}" />
				<input type="hidden" data-bind="value: points, attr: {'name': 'points' + id()}" />
			<!-- /ko -->
	
		<!-- /ko -->
	
		<!-- ko if: maxCharacters() > 0 -->
			<textarea id="defaultFreetextBiggerZeroTemplateID" class="data" data-bind="enable: !readonly(), value:getValueByQuestion(uniqueId(), true), attr: {'id': 'answer' + id(), 'data-id':id(), 'data-shortname': shortname(), 'name' : 'answer' + id(), 'class':css() + ' expand', 'maxlength':maxCharacters(), 'data-rows':numRows(), 'rows':numRows(), 'aria-labelledby':'questiontitle' + id(), 'aria-describedby':'questioninfo' + id() + ' questionhelp' + id(), 'aria-required':!optional()}"  onkeyup="countChar(this);" oninput="propagateChange(this);" onblur="validateInput($(this).parent(),true)" ></textarea>
			<!-- ko if: !foreditor -->
				<div class="charactercounterdiv limits" style="max-width: 645px; text-align: right; margin-left: 20px;" aria-live="polite" aria-atomic="true">
					<span class="glyphicon glyphicon-alert" style="display: none; margin-right: 5px;" data-toggle="tooltip" title="${form.getMessage("info.charactercounter")}" aria-label="${form.getMessage("info.charactercounter")}"></span>
					<span class="charactersused">
						<span data-bind="html: getCharacterCountInfo(maxCharacters()), attr: {id: 'countinfo' + id()}"></span>
					</span>
					<span class="characterlimitreached" data-toggle="tooltip" aria-label="${form.getMessage("info.AllCharactersUsed")}">${form.getMessage("info.AllCharactersUsed")}</span>
				</div>
			<!-- /ko -->
		<!-- /ko -->
		<!-- ko if: maxCharacters() == 0 -->
		     <textarea id="defaultFreetextZeroTemplateID" data-bind="enable: !readonly(), value:getValueByQuestion(uniqueId(), true), attr: {'id': 'answer' + id(), 'data-id':id(), 'data-shortname': shortname(), 'name' : 'answer' + id(), 'class':css() + ' expand', 'data-rows':numRows(), 'rows':numRows(), 'aria-labelledby':'questiontitle' + id(), 'aria-describedby':'questioninfo' + id() + ' questionhelp' + id(), 'aria-required':!optional()}" onkeyup="countChar(this);" oninput="propagateChange(this);" onblur="validateInput($(this).parent(),true)"></textarea>
		<!-- /ko -->
		
		<!-- ko if: isComparable() -->		
			<br /><label for="defaultFreetextSecondTemplateID" style="margin-left: 20px" data-bind="attr: {'for' : 'secondanswer' + id()}">${form.getMessage("label.PleaseRepeat")}</label>:<br />
			<textarea id="defaultFreetextSecondTemplateID" data-bind="enable: !readonly(), attr: {'data-id':id() + '2', 'class': 'comparable-second ' + css() + ' expand', 'data-rows':numRows, 'rows':numRows(), 'name' : 'secondanswer' + id(), 'id' : 'secondanswer' + id()}"  onblur="validateInputForSecondAnswer($(this))"></textarea>
		<!-- /ko -->
	</div>
	
	<div id="confirmation-template">
		<!-- ko if: optional() == false -->
			<span class="mandatory">*</span>
		<!-- /ko -->
		<span class='questionhelp' data-bind="html: niceHelp, attr:{id: 'questionhelp' + id()}"></span>
		
		<label for="defaultConfirmationTemplateID" class='questiontitle confirmationelement' data-bind='attr: {for: "answer" + id(), id: "questiontitle" + id()}'>
			<span class="screen-reader-only">${form.getMessage("form.Question")}</span>
			<span data-bind='html: title'></span>
		</label>
		
		<!-- ko if: usetext -->																					
			<a class="confirmationlabel" style="margin-left: 40px; cursor: pointer;" onclick="$(this).parent().find('.confirmation-dialog').modal('show')" data-bind="html:confirmationlabel"></a>
			<div class="modal confirmation-dialog">
				  <div class="modal-dialog modal-sm runnerdialog">
					  <div class="modal-content">
						  <div class="modal-header">${form.getMessage("label.Confirmation")}</div>
						  <div class="modal-body" data-bind="html: confirmationtext"></div>
						  <div class="modal-footer">
							<a style="cursor: pointer" class="btn btn-primary" onclick="$(this).closest('.confirmation-dialog').modal('hide');">${form.getMessage("label.Cancel")}</a>		
						  </div>
					  </div>
				  </div>
			</div>
		<!-- /ko -->
		<!-- ko if: useupload -->		
			<div class="files" style="margin-left: 40px; margin-top: 10px;" data-bind="foreach: files">
				<!-- ko if: $parent.foreditor -->
				<input id="defaultConfirmationTemplateID" type="hidden" data-bind="value: uid(), attr: {'name': 'files' + $parent.id()}" />
				<!-- /ko -->
				<a class="visiblelink" target="_blank" data-bind="html: name, attr: {'href':'${contextpath}/files/${form.survey.uniqueId}/' + uid()}"></a> <br />
			</div>			
		<!-- /ko -->
		
		<!-- ko if: foreditor -->
			<input type="hidden" data-bind="value: 'confirmation', attr: {'name': 'type' + id()}" />	
			<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />	
			<input type="hidden" data-bind="value: optional, attr: {'name': 'optional' + id()}" />
			<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />	
			<input type="hidden" data-bind="value: useAndLogic, attr: {'name': 'useAndLogic' + id()}" />	
			<input type="hidden" data-bind="value: usetext, attr: {'name': 'usetext' + id()}" />
			<input type="hidden" data-bind="value: useupload, attr: {'name': 'useupload' + id()}" />
			<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
			<label hidden><textarea data-bind="text: help, attr: {'name': 'help' + id()}" ></textarea>${form.getMessage("label.Help")}</label>
			<label hidden><textarea data-bind="text: confirmationtext, attr: {'name': 'confirmationtext' + id()}" ></textarea>${form.getMessage("label.ConfirmationText")}</label>
			<label hidden><textarea data-bind="text: confirmationlabel, attr: {'name': 'confirmationlabel' + id()}" ></textarea>${form.getMessage("label.ConfirmationLabel")}</label>
		<!-- /ko -->
	</div>
	
	<div id="rating-template">
		<div class='questiontitle' data-bind='attr: {id: "questiontitle" + id()}'>
			<span class="screen-reader-only">${form.getMessage("form.Question")}</span>
			<span data-bind='html: title'></span>
			<span class="screen-reader-only" data-bind="if: help">${form.getMessage("form.HelpAvailable")}</span>
		</div>
		<span class='questionhelp' data-bind="html: niceHelp, attr:{id: 'questionhelp' + id()}"></span>

		<!-- ko if: foreditor -->
			<input type="hidden" data-bind="value: 'rating', attr: {'name': 'type' + id()}" />
			<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />
			<input type="hidden" data-bind="value: optional, attr: {'name': 'optional' + id()}" />
			<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />
			<input type="hidden" data-bind="value: useAndLogic, attr: {'name': 'useAndLogic' + id()}" />	
			<input type="hidden" data-bind="value: numIcons, attr: {'name': 'numIcons' + id()}" />
			<input type="hidden" data-bind="value: isDelphiQuestion, attr: {'name': 'delphiquestion' + id()}" />
			<input type="hidden" data-bind="value: showExplanationBox, attr: {'name': 'explanationbox' + id()}" />
			<input type="hidden" data-bind="value: delphiChartType, attr: {'name': 'delphicharttype' + id()}" />
			<input type="hidden" data-bind="value: iconType, attr: {'name': 'iconType' + id()}" />
			<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
			<label hidden><textarea data-bind="text: help, attr: {'name': 'help' + id()}" ></textarea>${form.getMessage("label.Help")}</label>
			<input type="hidden" data-bind="value: editorRowsLocked(), attr: {'name': 'editorRowsLocked' + id()}" />

			<div class="hiddenratingquestions hideme">
				<!-- ko foreach: childElements() -->
				<div data-bind="attr: {'pos': $index, 'data-id': id}">
					<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'questionuid' + $parent.id(), 'data-id' : id()}" />
					<input type="hidden" data-bind="value: shortname, attr: {'name': 'questionshortname' + $parent.id(), 'data-id' : id()}" />
					<input type="hidden" data-bind="value: optional, attr: {'name': 'questionoptional' + $parent.id(), 'data-id' : id()}" />
					<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'question' + $parent.id(), 'data-id' : id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
				</div>
				<!-- /ko -->
			</div>
		<!-- /ko -->
		
		<table class="ratingtable" role="list" data-bind="attr: {'aria-labelledby':'questiontitle' + id(), 'aria-describedby':'questionhelp' + id()}">
			<thead>
				<tr>
					<th class="sr-only">${form.getMessage("form.RatingItem")}</th>
					<th class="sr-only">${form.getMessage("form.Rating")}</th>
				</tr>
			</thead>
			<tbody data-bind="foreach: childElements()">
				<tr class="ratingquestion" data-bind="attr: {'data-id': id, 'data-uid': uniqueId}">
					<td>
						<!-- ko if: optional() == false -->
							<span class="mandatory">*</span>
						<!-- /ko -->
							<span data-bind="html: title, attr:{id: 'answerlabel' + id()}"></span>
						<!-- ko if: $parents[0].ismobile || $parents[0].istablet -->
							<input data-bind="value:getValueByQuestion(uniqueId(), true), attr: {'id': 'input' + id(), 'data-id':id(), 'name' : 'answer' + id(), 'class' : 'rating ' + css()}" data-type="rating" type="hidden"></input>
			
							<div data-bind="foreach: new Array($parent.numIcons())">
								<a class="ratingitem" role="listitem" tabindex="0" onclick="ratingClick(this)" data-bind="attr: {'data-icons' : $parents[1].numIcons(), 'data-shortname': $parents[1].shortname()}">
									<!-- ko if: $parents[1].iconType() == 0 -->
								    <img src="${contextpath}/resources/images/star_grey.png" alt="${form.getMessage("form.RatingItem")}" data-bind="title: $index()+1, attr: {'alt': $index()+1 + ' / ' + $parents[1].numIcons(), 'aria-label': $parent.title() + ' ' + ($index()+1) + labelOf + $parents[1].numIcons()}" />
								    <!-- /ko -->
								    <!-- ko if: $parents[1].iconType() == 1 -->
								    <img src="${contextpath}/resources/images/nav_plain_grey.png" alt="${form.getMessage("form.RatingItem")}" data-bind="title: $index()+1, attr: {'alt': $index()+1 + ' / ' + $parents[1].numIcons(), 'aria-label': $parent.title() + ' ' + ($index()+1) + labelOf + $parents[1].numIcons()}" />
								    <!-- /ko -->
								    <!-- ko if: $parents[1].iconType() == 2 -->
								    <img src="${contextpath}/resources/images/heart_grey.png" alt="${form.getMessage("form.RatingItem")}" data-bind="title: $index()+1, attr: {'alt': $index()+1 + ' / ' + $parents[1].numIcons(), 'aria-label': $parent.title() + ' ' + ($index()+1) + labelOf + $parents[1].numIcons()}" />
								    <!-- /ko -->
							    </a>
							</div>
						<!-- /ko -->
					</td>
					<!-- ko if: !$parents[0].ismobile && !$parents[0].istablet -->
					<td>
						<input data-bind="value:getValueByQuestion(uniqueId(), true), attr: {'id': 'input' + id(), 'data-id':id(), 'name' : 'answer' + id(), 'class' : 'rating ' + css()}" data-type="rating" type="hidden"></input>
		
						<div data-bind="foreach: new Array($parent.numIcons())">
							<a class="ratingitem" role="listitem" tabindex="0" onclick="ratingClick(this)" data-bind="attr: {'data-icons' : $parents[1].numIcons(), 'data-shortname': $parents[1].shortname()}">
								<!-- ko if: $parents[1].iconType() == 0 -->
							    <img src="${contextpath}/resources/images/star_grey.png" alt="${form.getMessage("form.RatingItem")}" data-bind="title: $index()+1, attr: {'alt': $index()+1 + ' / ' + $parents[1].numIcons(), 'aria-label': $parent.title() + ' ' + ($index()+1) + labelOf + $parents[1].numIcons()}" />
							    <!-- /ko -->
							    <!-- ko if: $parents[1].iconType() == 1 -->
							    <img src="${contextpath}/resources/images/nav_plain_grey.png" alt="${form.getMessage("form.RatingItem")}" data-bind="title: $index()+1, attr: {'alt': $index()+1 + ' / ' + $parents[1].numIcons(), 'aria-label': $parent.title() + ' ' + ($index()+1) + labelOf + $parents[1].numIcons()}" />
							    <!-- /ko -->
							    <!-- ko if: $parents[1].iconType() == 2 -->
							    <img src="${contextpath}/resources/images/heart_grey.png" alt="${form.getMessage("form.RatingItem")}" data-bind="title: $index()+1, attr: {'alt': $index()+1 + ' / ' + $parents[1].numIcons(), 'aria-label': $parent.title() + ' ' + ($index()+1) + labelOf + $parents[1].numIcons()}" />
							    <!-- /ko -->
						    </a>
						</div>
					</td>
					<!-- /ko -->
				</tr>
			</tbody>
		</table>						
	
	</div>
	
	<div id="number-template">
		<!-- ko if: optional() == false -->
			<span class="mandatory">*</span>
		<!-- /ko -->

		<!-- ko if: display() == 'Slider' -->
			<label for="defaultNumberSliderTemplateID" class='questiontitle' data-bind='attr: {for: "answer" + id(), id: "questiontitle" + id()}'>
				<span class="screen-reader-only">${form.getMessage("form.Question")}</span>
				<span data-bind='html: title'></span>
				<span class="screen-reader-only" data-bind="if: help">${form.getMessage("form.HelpAvailable")}</span>
			</label>
			<span class='questionhelp' data-bind="html: niceHelp, attr:{id: 'questionhelp' + id()}"></span>

			<div role="group" data-bind="hidden: isAnswered, attr: {'aria-labelledby': 'questiontitle' + id(), 'aria-describedby' : 'questioninfo' + id() + ' questionhelp' + id()}">
		
				<div class="limits" data-bind="hidden: isAnswered, attr: {id: 'questioninfo' + id()}">
					<!-- ko ifnot: foreditor -->
						${form.getMessage("info.MoveTheSliderOrAccept", "tabindex=\"0\" data-bind=\"click: markAsAnswered\"")}
					<!-- /ko -->
					<!-- ko if: foreditor -->
						${form.getMessage("info.MoveTheSliderOrAccept", "")}
					<!-- /ko -->
				</div>
			
			</div>
		<!-- /ko -->
		
		<!-- ko if: display() != 'Slider' -->
			<label for="defaultNumberTemplateID" class='questiontitle' data-bind='attr: {for: "answer" + id(), id: "questiontitle" + id()}'>
				<span class="screen-reader-only">${form.getMessage("form.Question")}</span>
				<span data-bind='html: title'></span>
				<span class="screen-reader-only" data-bind="if: help">${form.getMessage("form.HelpAvailable")}</span>
			</label>
			<span class='questionhelp' data-bind="html: niceHelp, attr:{id: 'questionhelp' + id()}"></span>

			<!-- ko if: min() != null && min() != 0 && max() != null && max() != 0 -->
				<div class='limits' data-bind="html: getMinMax(minString(), maxString()), attr: {id: 'questioninfo' + id()}"></div>
			<!-- /ko -->
			<!-- ko if: min() != 0 && min() != null && (max() == 0 || max() == null) -->
				<div class='limits' data-bind="html: getMin(minString()), attr: {id: 'questioninfo' + id()}"></div>
			<!-- /ko -->
			<!-- ko if: (min() == 0 || min() == null) && max() != null && max() != 0 -->
				<div class='limits' data-bind="html: getMax(maxString()), attr: {id: 'questioninfo' + id()}"></div>
			<!-- /ko -->
			<!-- ko if: (min() == 0 || min() == null) && (max() == 0 || max() == null) -->
				<div class='limits' data-bind="attr: {id: 'questioninfo' + id()}"></div>
			<!-- /ko -->

			<input id="defaultNumberTemplateID" data-bind="enable: !readonly(), value:getValueByQuestion(uniqueId(), true), attr: {'id': 'answer' + id(), 'data-id':id(), 'data-shortname': shortname(), 'name' : 'answer' + id(), 'class':css(), 'aria-labelledby':'questiontitle' + id(), 'aria-describedby':'questioninfo' + id() + ' questionhelp' + id()}" oninput="propagateChange(this);" onblur="validateInput($(this).parent())" type="text" ></input><span class="unit-text" data-bind="html: unit"></span>
		<!-- /ko -->
		
		<!-- ko if: display() == 'Slider' -->
			<div class="question-reset-answer-message" data-bind="hidden: !isAnswered()">
				<a tabindex="0" data-bind="click: resetToInitialPosition, attr: {'aria-describedby' : 'questiontitle' + id()}">${form.getMessage("label.ResetToInitialPosition")}</a>
			</div>
			<div data-bind="attr: {'class' : maxDistance() > -1 ? 'slider-div median' : 'slider-div'}">
				<div style="float: left; margin-left: -20px; padding-bottom: 20px; max-width: 45%; text-align: center;" data-bind="html: minLabel()"></div>
				<div style="float: right; padding-bottom: 20px;  max-width: 45%; text-align: center;" data-bind="html: maxLabel()"></div>
				<div style="clear: both"></div>

				<div class="slider-widget-box" role="group" data-bind="attr: {'aria-labelledby':'questiontitle' + id(), 'aria-describedby':'questioninfo' + id() + ' questionhelp' + id()}">
				<a data-bind='click: decrease'><svg aria-label="${form.getMessage("info.DecreaseSliderValue")}" xmlns="http://www.w3.org/2000/svg" width="13" height="13" fill="currentColor" class="bi bi-chevron-left" viewBox="0 0 16 16">
					  <path stroke="#337ab7" stroke-width="3" fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/></svg></a>

				<input id="defaultNumberSliderTemplateID" type="text"
					   onchange="propagateChange(this);"
					   data-bind="enable: !readonly(), value:getValueByQuestion(uniqueId()), attr: {'class': css() + ' sliderbox', 'id': 'answer' + id(), 'data-id':id(), 'data-shortname': shortname(), 'name' : 'answer' + id(), 'data-slider-min' : min(), 'data-slider-max' : max(), 'precision' : decimalPlaces(), 'data-slider-step' : step(),'data-slider-ticks' : ticks(), 'data-slider-value' : initialValue(), 'data-is-answered': isAnswered() ? 'true' : 'false' }"
				/>

				<a data-bind='click: increase'><svg aria-label="${form.getMessage("info.IncreaseSliderValue")}" xmlns="http://www.w3.org/2000/svg" width="13" height="13" fill="currentColor" class="bi bi-chevron-right" viewBox="0 0 16 16">
						<path stroke="#337ab7" stroke-width="3" fill-rule="evenodd" d="M4.646 1.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1 0 .708l-6 6a.5.5 0 0 1-.708-.708L10.293 8 4.646 2.354a.5.5 0 0 1 0-.708z"/></svg></a>
				</div>
			</div>
		<!-- /ko -->
		
		<!-- ko if: foreditor -->
			<input type="hidden" data-bind="value: 'number', attr: {'name': 'type' + id()}" />	
			<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />	
			<input type="hidden" data-bind="value: optional, attr: {'name': 'optional' + id()}" />
			<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />
			<input type="hidden" data-bind="value: useAndLogic, attr: {'name': 'useAndLogic' + id()}" />	
			<input type="hidden" data-bind="value: decimalPlaces, attr: {'name': 'decimalplaces' + id()}" />
			<input type="hidden" data-bind="value: unit, attr: {'name': 'unit' + id()}" />
			<input type="hidden" data-bind="value: min, attr: {'name': 'min' + id()}" />	
			<input type="hidden" data-bind="value: max, attr: {'name': 'max' + id()}" />	
			<input type="hidden" data-bind="value: readonly, attr: {'name': 'readonly' + id()}" />	
			<input type="hidden" data-bind="value: isAttribute, attr: {'name': 'attribute' + id()}" />	
			<input type="hidden" data-bind="value: isDelphiQuestion, attr: {'name': 'delphiquestion' + id()}" />
			<input type="hidden" data-bind="value: delphiChartType, attr: {'name': 'delphicharttype' + id()}" />
			<input type="hidden" data-bind="value: showExplanationBox, attr: {'name': 'explanationbox' + id()}" />
			<input type="hidden" data-bind="value: attributeName, attr: {'name': 'nameattribute' + id()}" />	
			<input type="hidden" data-bind="value: isUnique, attr: {'name': 'unique' + id()}" />	
			<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
			<label hidden><textarea data-bind="text: help, attr: {'name': 'help' + id()}" ></textarea>${form.getMessage("label.Help")}</label>

			<input type="hidden" data-bind="value: scoring, attr: {'name': 'scoring' + id()}" />
			<input type="hidden" data-bind="value: points, attr: {'name': 'points' + id()}" />
			
			<!--  ko foreach: scoringItems() -->
			<input type="hidden" data-bind="value: id, attr: {'name': 'scoringitem' + $parent.id()}" />
			<input type="hidden" data-bind="value: type, attr: {'name': 'type' + id()}" />
			<input type="hidden" data-bind="value: correct, attr: {'name': 'correct' + id()}" />	
			<input type="hidden" data-bind="value: value, attr: {'name': 'value' + id()}" />
			<input type="hidden" data-bind="value: value2, attr: {'name': 'value2' + id()}" />
			<input type="hidden" data-bind="value: feedback, attr: {'name': 'feedback' + id()}" />
			<input type="hidden" data-bind="value: min, attr: {'name': 'min' + id()}" />
			<input type="hidden" data-bind="value: max, attr: {'name': 'max' + id()}" />
			<input type="hidden" data-bind="value: points, attr: {'name': 'points' + id()}" />
			<!-- /ko -->
			
			<input type="hidden" data-bind="value: minLabel, attr: {'name': 'minLabel' + id()}" />
			<input type="hidden" data-bind="value: maxLabel, attr: {'name': 'maxLabel' + id()}" />
			<input type="hidden" data-bind="value: display, attr: {'name': 'display' + id()}" />
			<input type="hidden" data-bind="value: initialSliderPosition, attr: {'name': 'initialSliderPosition' + id()}" />
			<input type="hidden" data-bind="value: displayGraduationScale, attr: {'name': 'displayGraduationScale' + id()}" />
			
			<input type="hidden" data-bind="value: maxDistance, attr: {'name': 'maxDistance' + id()}" />
		<!-- /ko -->
	</div> 
	
	<div id="email-template">
		<!-- ko if: optional() == false -->
			<span class="mandatory">*</span>
		<!-- /ko -->
		<label for="defaultEmailTemplateID" class='questiontitle' data-bind='attr: {for: "answer" + id(), id: "questiontitle" + id()}'>
			<span class="screen-reader-only">${form.getMessage("form.Question")}</span>
			<span data-bind='html: title'></span>
			<span class="screen-reader-only" data-bind="if: help">${form.getMessage("form.HelpAvailable")}</span>
		</label>
		<span class='questionhelp' data-bind="html: niceHelp, attr:{id: 'questionhelp' + id()}"></span>
		<div class="input-group" style="margin-left: 20px;">
	    	<div class="input-group-addon" style="margin-bottom: 5px">@</div>
	      	<input id="defaultEmailTemplateID" data-bind="enable: !readonly(), value:getValueByQuestion(uniqueId(), true), attr: {'id': 'answer' + id(), 'data-id':id(), 'data-shortname': shortname(), 'name' : 'answer' + id(), 'class':css(), 'aria-labelledby':'questiontitle' + id(), 'aria-describedby':'questionhelp' + id()}"  onblur="validateInput($(this).parent().parent())" onkeyup="propagateChange(this);" onchange="validateInput($(this).parent());" style="width: 180px; margin-left: 0px; margin-bottom: 0px !important;" type='email' maxlength="255" />
	    </div>
	    <!-- ko if: foreditor -->
			<input type="hidden" data-bind="value: 'email', attr: {'name': 'type' + id()}" />	
			<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />	
			<input type="hidden" data-bind="value: optional, attr: {'name': 'optional' + id()}" />
			<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />
			<input type="hidden" data-bind="value: useAndLogic, attr: {'name': 'useAndLogic' + id()}" />
			<input type="hidden" data-bind="value: readonly, attr: {'name': 'readonly' + id()}" />	
			<input type="hidden" data-bind="value: isAttribute, attr: {'name': 'attribute' + id()}" />	
			<input type="hidden" data-bind="value: isDelphiQuestion, attr: {'name': 'delphiquestion' + id()}" />
			<input type="hidden" data-bind="value: showExplanationBox, attr: {'name': 'explanationbox' + id()}" />
			<input type="hidden" data-bind="value: attributeName, attr: {'name': 'nameattribute' + id()}" />	
			<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
			<label hidden><textarea data-bind="text: help, attr: {'name': 'help' + id()}" ></textarea>${form.getMessage("label.Help")}</label>
		<!-- /ko -->
	</div>
	
	<div id="date-template">
		<!-- ko if: optional() == false -->
			<span class="mandatory">*</span>
		<!-- /ko -->
		<label for="defaultDateTemplateID" class='questiontitle' data-bind='attr: {for: "answer" + id(), id: "questiontitle" + id()}'>
			<span class="screen-reader-only">${form.getMessage("form.Question")}</span>
			<span data-bind='html: title'></span>
			<span class="screen-reader-only" data-bind="if: help">${form.getMessage("form.HelpAvailable")}</span>
		</label>
		<span class='questionhelp' data-bind="html: niceHelp, attr:{id: 'questionhelp' + id()}"></span>
		
		<!-- ko if: min() != null && max() != null -->
			<div class='limits' data-bind="html: getMinMaxDate(minString(), maxString()), attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
		<!-- ko if: min() != null && max() == null -->
			<div class='limits' data-bind="html: getMinDate(minString()), attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
		<!-- ko if: min() == null && max() != null -->
			<div class='limits' data-bind="html: getMaxDate(maxString()), attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
		<!-- ko if: min() == null && max() == null -->
			<div class='limits' data-bind="attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
		
		<div class="input-group">
			<!-- ko if: !foreditor && !readonly() -->
				<div class="input-group-addon" tabindex="0" onclick='$(this).parent().find(".datepicker").datepicker( "show" );' onfocus='$(this).parent().find(".datepicker").datepicker( "show" );'><span class="glyphicon glyphicon-calendar" aria-hidden="true"></span></div>
			<!-- /ko -->
			
			<!-- ko if: foreditor || readonly() -->
				<div class="input-group-addon"><span class="glyphicon glyphicon-calendar" aria-hidden="true"></span></div>
			<!-- /ko -->
			<input id="defaultDateTemplateID" data-bind="enable: !readonly(), value:getValueByQuestion(uniqueId(), true), attr: {'id': 'answer' + id(), 'data-id':id(), 'data-shortname': shortname(), 'name' : 'answer' + id(), 'class': 'datepicker ' + css(), 'aria-labelledby':'questiontitle' + id(), 'aria-describedby':'questioninfo' + id() + ' questionhelp' + id()}" onblur="if($(this).val().length > 0 && validateInput($(this).parent().parent())) { propagateChange(this); }" oninput="propagateChange(this);" type="text" placeholder="DD/MM/YYYY" style="display: inline; margin-left:0px; margin-bottom:0px !important;"></input>
		</div>
		
		<!-- ko if: foreditor -->
			<input type="hidden" data-bind="value: 'date', attr: {'name': 'type' + id()}" />	
			<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />	
			<input type="hidden" data-bind="value: optional, attr: {'name': 'optional' + id()}" />
			<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />	
			<input type="hidden" data-bind="value: useAndLogic, attr: {'name': 'useAndLogic' + id()}" />	
			<input type="hidden" data-bind="value: minString(), attr: {'name': 'min' + id()}" />	
			<input type="hidden" data-bind="value: maxString(), attr: {'name': 'max' + id()}" />	
			<input type="hidden" data-bind="value: readonly, attr: {'name': 'readonly' + id()}" />	
			<input type="hidden" data-bind="value: isAttribute, attr: {'name': 'attribute' + id()}" />	
			<input type="hidden" data-bind="value: isDelphiQuestion, attr: {'name': 'delphiquestion' + id()}" />
			<input type="hidden" data-bind="value: showExplanationBox, attr: {'name': 'explanationbox' + id()}" />	
			<input type="hidden" data-bind="value: attributeName, attr: {'name': 'nameattribute' + id()}" />	
			<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
			<label hidden><textarea data-bind="text: help, attr: {'name': 'help' + id()}" ></textarea>${form.getMessage("label.Help")}</label>

			<input type="hidden" data-bind="value: scoring, attr: {'name': 'scoring' + id()}" />
			<input type="hidden" data-bind="value: points, attr: {'name': 'points' + id()}" />			
			
			<!--  ko foreach: scoringItems() -->
			<input type="hidden" data-bind="value: id, attr: {'name': 'scoringitem' + $parent.id()}" />
			<input type="hidden" data-bind="value: type, attr: {'name': 'type' + id()}" />
			<input type="hidden" data-bind="value: correct, attr: {'name': 'correct' + id()}" />	
			<input type="hidden" data-bind="value: value, attr: {'name': 'value' + id()}" />
			<input type="hidden" data-bind="value: value2, attr: {'name': 'value2' + id()}" />
			<input type="hidden" data-bind="value: feedback, attr: {'name': 'feedback' + id()}" />
			<input type="hidden" data-bind="value: minDate, attr: {'name': 'minDate' + id()}" />
			<input type="hidden" data-bind="value: maxDate, attr: {'name': 'maxDate' + id()}" />
			<input type="hidden" data-bind="value: points, attr: {'name': 'points' + id()}" />
			<!-- /ko -->
		<!-- /ko -->		
	</div>
	
	<div id="time-template">
		<!-- ko if: optional() == false -->
			<span class="mandatory">*</span>
		<!-- /ko -->
		<label for="defaultTimeTemplateID" class='questiontitle' data-bind='attr: {for: "answer" + id(), id: "questiontitle" + id()}'>
			<span class="screen-reader-only">${form.getMessage("form.Question")}</span>
			<span data-bind='html: title'></span>
			<span class="screen-reader-only" data-bind="if: help">${form.getMessage("form.HelpAvailable")}</span>
		</label>
		<span class='questionhelp' data-bind="html: niceHelp, attr:{id: 'questionhelp' + id()}"></span>
		
		<!-- ko if: min() != null && max() != null && min() != '' && max() != ''  -->
			<div class='limits' data-bind="html: getMinMaxDate(min(), max()), attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
		<!-- ko if: min() != null && min() != '' && (max() == null || max() == '') -->
			<div class='limits' data-bind="html: getMinDate(min()), attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
		<!-- ko if: max() != null && max() != '' && (min() == null || min() == '') -->
			<div class='limits' data-bind="html: getMaxDate(max()), attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
		<!-- ko if: (max() == null || max() == '') && (min() == null || min() == '') -->
			<div class='limits' data-bind="attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
		
		<div class="input-group">
			<div class="input-group-addon"><span class="glyphicon glyphicon-time" aria-hidden="true"></span></div>
			<input id="defaultTimeTemplateID" data-bind="enable: !readonly(), value:getValueByQuestion(uniqueId()), attr: {'id': 'answer' + id(), 'data-id':id(), 'data-shortname': shortname(), 'name' : 'answer' + id(), 'class': 'timepicker ' + css(), 'aria-labelledby':'questiontitle' + id(), 'aria-describedby':'questioninfo' + id() + ' questionhelp' + id()}" onblur="if(validateInput($(this).parent().parent())) { propagateChange(this); }" oninput="propagateChange(this);" type="text" placeholder="HH:mm:ss" style="display: inline; margin-left:0px; margin-bottom:0px !important;"></input>
		</div>
		
		<!-- ko if: foreditor -->
			<input type="hidden" data-bind="value: 'time', attr: {'name': 'type' + id()}" />	
			<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />	
			<input type="hidden" data-bind="value: optional, attr: {'name': 'optional' + id()}" />
			<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />	
			<input type="hidden" data-bind="value: useAndLogic, attr: {'name': 'useAndLogic' + id()}" />	
			<input type="hidden" data-bind="value: min(), attr: {'name': 'min' + id()}" />	
			<input type="hidden" data-bind="value: max(), attr: {'name': 'max' + id()}" />	
			<input type="hidden" data-bind="value: readonly, attr: {'name': 'readonly' + id()}" />	
			<input type="hidden" data-bind="value: isAttribute, attr: {'name': 'attribute' + id()}" />	
			<input type="hidden" data-bind="value: isDelphiQuestion, attr: {'name': 'delphiquestion' + id()}" />
			<input type="hidden" data-bind="value: showExplanationBox, attr: {'name': 'explanationbox' + id()}" />
			<input type="hidden" data-bind="value: attributeName, attr: {'name': 'nameattribute' + id()}" />	
			<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
			<label hidden><textarea data-bind="text: help, attr: {'name': 'help' + id()}" ></textarea>${form.getMessage("label.Help")}</label>
		<!-- /ko -->		
	</div>
	
	<div id="upload-template">
		<!-- ko if: optional() == false -->
			<span class="mandatory">*</span>
		<!-- /ko -->
		<label for="defaultUploadTemplateID" class='questiontitle' data-bind='attr: {for: "answer" + id(), id: "questiontitle" + id()}'>
			<span class="screen-reader-only">${form.getMessage("form.Question")}</span>
			<span data-bind='html: title'></span>
			<span class="screen-reader-only" data-bind="if: help">${form.getMessage("form.HelpAvailable")}</span>
		</label>
		<span class="questionhelp" data-bind="html: niceHelp, attr:{id: 'questionhelp' + id()}"></span>	
		<!-- ko if: extensions() != null && extensions().length > 0 -->
			<div class="questionhelp">
				<span class='glyphicon glyphicon-question-sign'></span>&nbsp;<span data-bind="html: getExtensionsHelp(extensions())"></span>
			</div>
		<!-- /ko -->
		
		<div class="uploadinfo" style="display: none; padding: 10px; color: #777;">
			${form.getMessage("label.UploadStarted")}			
		</div>
		
		<input id="defaultUploadTemplateID" type="hidden" data-bind="attr: {'id': 'answer' + id(), 'name':'answer' + id()}" value="files" />
		<div class="uploaded-files" data-bind="foreach: getFileAnswer(uniqueId(), true)">
			<div>
				<a data-toggle="tooltip" title="${form.getMessage("label.RemoveUploadedFile")}" data-bind="click: function() {deleteFile($parent.id(),'${uniqueCode}',$data,$('#uploadlink' + $parent.id()));return false;}, attr: {'id' : 'uploadlink' + $parent.id(), 'aria-label' : $data}">
					<span style="margin-right: 10px;" class="glyphicon glyphicon-trash"></span>
				</a>
				<span data-bind="html: $data"></span>
			</div>				
		</div>				
		<div data-bind="attr: {'class': css() + ' file-uploader', 'data-id':id}" style="margin-left: 10px; margin-top: 10px;"></div>
		
		<!-- ko if: foreditor -->
			<input type="hidden" data-bind="value: 'upload', attr: {'name': 'type' + id()}" />	
			<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />	
			<input type="hidden" data-bind="value: optional, attr: {'name': 'optional' + id()}" />
			<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />	
			<input type="hidden" data-bind="value: useAndLogic, attr: {'name': 'useAndLogic' + id()}" />	
			<input type="hidden" data-bind="value: readonly, attr: {'name': 'readonly' + id()}" />	
			<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
			<label hidden><textarea data-bind="text: help, attr: {'name': 'help' + id()}" ></textarea>${form.getMessage("label.Help")}</label>
			<input type="hidden" data-bind="value: extensions, attr: {'name': 'extensions' + id()}" />
			<input type="hidden" data-bind="value: maxFileSize, attr: {'name': 'maxFileSize' + id()}" />
		<!-- /ko -->
	</div>
	
	<div id="download-template">
		<div class='questiontitle' data-bind='attr: {id: "questiontitle" + id()}'>
			<span class="screen-reader-only">${form.getMessage("form.Question")}</span>
			<span data-bind='html: title'></span>
			<span class="screen-reader-only" data-bind="if: help">${form.getMessage("form.HelpAvailable")}</span>
		</div>
		<span class="questionhelp" data-bind="html: niceHelp, attr:{id: 'questionhelp' + id()}"></span>	
		<div class="files" role="list" data-bind="foreach: files, attr: {'aria-labelledby':'questiontitle' + id(), 'aria-describedby':'questionhelp' + id()}">
			<!-- ko if: $parent.foreditor -->
			<input type="hidden" data-bind="value: uid(), attr: {'name': 'files' + $parent.id()}" />	
			<!-- /ko -->
			<a class="visiblelink" target="_blank" data-bind="attr: {'href': '${contextpath}/files/${form.survey.uniqueId}/' + uid(), 'aria-label' : '${form.getMessage("label.DownloadFile")} ' + name()}, html: name"></a> <br />
		</div>
		<!-- ko if: foreditor -->
		
			<!-- ko if: files().length == 0 -->
			<div class="files">
				<i>[${form.getMessage("message.AddFileForDownload")}]</i>
			</div>
			<!-- /ko -->
		
			<input type="hidden" data-bind="value: 'download', attr: {'name': 'type' + id()}" />	
			<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />	
			<input type="hidden" data-bind="value: optional, attr: {'name': 'optional' + id()}" />
			<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />	
			<input type="hidden" data-bind="value: useAndLogic, attr: {'name': 'useAndLogic' + id()}" />	
			<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
			<label hidden><textarea data-bind="text: help, attr: {'name': 'help' + id()}" ></textarea>${form.getMessage("label.Help")}</label>
		<!-- /ko -->
	</div>
	
	<div id="gallery-template">
		<!-- ko if: optional() == false -->
			<span class="mandatory">*</span>
		<!-- /ko -->
		<span id="defaultGalleryTemplateID" class='questiontitle' data-bind='attr: {id: "questiontitle" + id()}'>
			<span class="screen-reader-only">${form.getMessage("form.Question")}</span>
			<span data-bind='html: title'></span>
			<span class="screen-reader-only" data-bind="if: help">${form.getMessage("form.HelpAvailable")}</span>
		</span>
		<span class='questionhelp' data-bind="html: niceHelp, attr:{id: 'questionhelp' + id()}"></span>
		
		<!-- ko if: selection() && limit != null && limit() > 0 -->
			<div class='limits' data-bind="html: getMaxSelections(limit()), attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
		<!-- ko ifnot: selection() && limit != null && limit() > 0 -->
			<div class='limits' data-bind="attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
		
		<div class="gallery-div" style="width: 920px; max-width: 100%; text-align:left;">				
			<!-- ko if: files().length == 0 -->
				<table data-bind="attr: {'class':'gallery-table limit' + limit()}">
					<thead>
						<tr>
							<th class="sr-only">${form.getMessage("label.PhotoScenery")}"</th>
							<th class="sr-only">${form.getMessage("label.PhotoScenery")}"</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>
								<img alt="${form.getMessage("label.PhotoScenery")}" style="max-width: none;" src="${contextpath}/resources/images/photo_scenery.png" data-width="128" data-original-width="247" width="247px">
							</td>
							<td>
								<img alt="${form.getMessage("label.PhotoScenery")}" style="max-width: none;" src="${contextpath}/resources/images/photo_scenery.png" data-width="128" data-original-width="247" width="247px">
							</td>
						</tr>		
					</tbody>
				</table>			
			<!-- /ko -->
			
			<!-- ko if: files().length > 0 -->
			<table style="width: 100%" data-bind="attr: {'class':'gallery-table limit' + limit(), 'aria-rowcount': rows().length, 'aria-labelledby':'questiontitle' + id(), 'aria-describedby':'questioninfo' + id() + ' questionhelp' + id()}" >
				<thead data-bind="foreach: rows">
					<!-- ko if: $index() == 0 -->
						<tr data-bind="foreach: $data">
							<th class="sr-only">${form.getMessage("form.GalleryImageItem")}</th>
						</tr>
					<!-- /ko -->
				</thead>
				<tbody data-bind="foreach: rows">
					<tr data-bind="foreach: $data">
						<td data-bind="attr: {'data-uid':uid()}" style="vertical-align: top">
							<div class="galleryinfo">
								<span data-bind="if: $parents[1].selection()">																			
									<input aria-labelledby="defaultGalleryTemplateID" data-bind="value: $parentContext.$index() * $parents[1].columns() + $index(), checked: getValueByQuestionGallery($parents[1].uniqueId()).indexOf(($parentContext.$index() * $parents[1].columns() + $index()).toString()) > -1, attr: {'onclick': $parents[1].readonly() ? 'return false;':'propagateChange(this);', 'data-shortname': $parents[1].shortname(), 'class': $parents[1].css() + ' selection', 'name':'answer'+$parents[1].id(), 'aria-labelledby': 'answerlabel' + $parents[1].id() + $index()}" type="checkbox" />
								</span>
								<!-- ko if: $parents[1].numbering() -->
								<span data-bind='html: ($parentContext.$index() * $parents[1].columns() + $index()+1) + "."'></span>
								<!-- /ko -->
								<span data-bind='html: name().replace("%20"," "), attr: {id: "answerlabel" + $parents[1].id() + $index()}'></span>
							</div>
							<a onclick="showGalleryBrowser($(this).parent())">																	
								<img class="gallery-image" alt="${form.getMessage("form.GalleryImageItem")}" data-bind="attr: {'alt': (desc() != '' ? desc() : 'Gallery Image' + $index()) + (longdesc != '' ? '; URL ' + longdesc() : ''), 'src':'${contextpath}/files/${form.survey.uniqueId}/'+ uid(), 'data-width': width(), 'data-original-width': Math.round((850-20-($parents[1].columns()*30))/$parents[1].columns()), 'width': Math.round((850-20-($parents[1].columns()*30))/$parents[1].columns())+'px'}"  style="max-width: 100%;" />
							</a>
							<div class="comment" data-bind="html: comment"></div>	
							<!-- ko if: $parents[1].foreditor -->
								<input type="hidden" data-bind="value: name, attr: {'name': 'name' + ($parentContext.$index() * $parents[1].columns() + $index() + 1) + $parents[1].id()}" />	
								<input type="hidden" data-bind="value: uid, attr: {'name': 'image' + ($parentContext.$index() * $parents[1].columns() + $index() + 1) + $parents[1].id()}" />	
								<input type="hidden" data-bind="value: longdesc, attr: {'name': 'longdesc' + ($parentContext.$index() * $parents[1].columns() + $index() + 1) + $parents[1].id()}" />	
								<input type="hidden" data-bind="value: desc, attr: {'name': 'desc' + ($parentContext.$index() * $parents[1].columns() + $index() + 1) + $parents[1].id()}" />	
								<label hidden><textarea data-bind="text: comment, attr: {'name': 'comment' + ($parentContext.$index() * $parents[1].columns() + $index() + 1) + $parents[1].id()}" ></textarea>${form.getMessage("label.Comment")}</label>
							<!-- /ko -->						
						</td>
					</tr>
				</tbody>
			</table>
			<!-- /ko -->								
		</div>
		<!-- ko if: foreditor -->
			<input type="hidden" data-bind="value: 'gallery', attr: {'name': 'type' + id()}" />	
			<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />	
			<input type="hidden" data-bind="value: optional, attr: {'name': 'optional' + id()}" />
			<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />	
			<input type="hidden" data-bind="value: useAndLogic, attr: {'name': 'useAndLogic' + id()}" />	
			<input type="hidden" data-bind="value: readonly, attr: {'name': 'readonly' + id()}" />
			<input type="hidden" data-bind="value: columns, attr: {'name': 'columns' + id()}" />
			<input type="hidden" data-bind="value: selection, attr: {'name': 'selectable' + id()}" />
			<input type="hidden" data-bind="value: numbering, attr: {'name': 'numbering' + id()}" />
			<input type="hidden" data-bind="value: limit, attr: {'name': 'limit' + id()}" />
			<input type="hidden" data-bind="value: files().length, attr: {'name': 'count' + id()}" />
			<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
			<label hidden><textarea data-bind="text: help, attr: {'name': 'help' + id()}" ></textarea>${form.getMessage("label.Help")}</label>
		<!-- /ko -->
		<!-- ko ifnot: foreditor -->
			<div class="modal" data-backdrop="static">
				<div data-bind="attr: {'data-mobile': '' + ismobile, 'class': 'modal-dialog runnerdialog ' + (ismobile ? 'modal-sm' : (istablet ? 'modal-md' : 'modal-lg'))}">
				<div class="modal-content">
				  <div class="modal-header">${form.getMessage("label.BrowseGallery")}</div>
				  <div data-bind="foreach: files()" class="modal-body"  data-bind="attr: {'style': 'overflow: auto; height: ' + ismobile ? '400px' : '600px;'}">
				  		<div class="gallery-image hideme" style="text-align: center" data-bind="attr: {'data-uid': uid()}">
							<div class="galleryinfo">
								<span data-bind="if: $parent.selection()">
									<input aria-labelledby="defaultGalleryTemplateID" onclick="synchronizeGallerySelection(this)" type="checkbox" data-bind="attr: {'aria-labelledby': 'answerlabel' + $parent.id() + $index()}" />
								</span>
								<!-- ko if: $parent.numbering() -->
								<span data-bind='html: ($index()+1) + "."'></span>
								<!-- /ko -->
								<span data-bind='html: name().replace("%20"," "), attr: {id: "answerlabel" + $parent.id() + $index()}'></span>
							</div>
						
							<img style="width: 95%;" alt="${form.getMessage("form.GalleryImageItem")}" data-bind="attr: {'alt': (desc() != '' ? desc() : 'Gallery Image' + $index()) + (longdesc != '' ? '; URL ' + longdesc() : ''), 'src':'${contextpath}/files/${form.survey.uniqueId}/'+uid()}" />
							<div class="gallery-image-comment" style="text-align: center; padding: 15px;" data-bind="html: comment()"></div>							
						</div>
				  </div>
				  <div class="modal-footer">
				  	<a class="btn btn-default" onclick="openPreviousImage($(this).closest('.modal'))"><span class="glyphicon glyphicon-chevron-left"></span></a>
					<a class="btn btn-primary" onclick="$(this).closest('.modal').modal('hide');">${form.getMessage("label.Close")}</a>			
				  	<a class="btn btn-default" onclick="openNextImage($(this).closest('.modal'))"><span class="glyphicon glyphicon-chevron-right"></span></a>
				  </div>
				 </div>
				 </div>
			</div>
		<!-- /ko -->		
	</div>
	
	<div id="matrix-template">
		<!-- ko if: foreditor -->
			<input type="hidden" data-bind="value: 'matrix', attr: {'name': 'type' + id()}" />
			<input type="hidden" data-bind="value: tableType, attr: {'name': 'tabletype' + id()}" />
			<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />
			<input type="hidden" data-bind="value: optional, attr: {'name': 'optional' + id()}" />
			<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />
			<input type="hidden" data-bind="value: useAndLogic, attr: {'name': 'useAndLogic' + id()}" />	
			<input type="hidden" data-bind="value: order, attr: {'name': 'order' + id()}" />
			<input type="hidden" data-bind="value: readonly, attr: {'name': 'readonly' + id()}" />
			<input type="hidden" data-bind="value: isAttribute, attr: {'name': 'attribute' + id()}" />
			<input type="hidden" data-bind="value: isDelphiQuestion, attr: {'name': 'delphiquestion' + id()}" />
			<input type="hidden" data-bind="value: showExplanationBox, attr: {'name': 'explanationbox' + id()}" />
			<input type="hidden" data-bind="value: delphiChartType, attr: {'name': 'delphicharttype' + id()}" />
			<input type="hidden" data-bind="value: attributeName, attr: {'name': 'nameattribute' + id()}" />
			<input type="hidden" data-bind="value: isInterdependent, attr: {'name': 'interdependent' + id()}" />
			<input type="hidden" data-bind="value: isSingleChoice, attr: {'name': 'single' + id()}" />
			<input type="hidden" data-bind="value: minRows, attr: {'name': 'rowsmin' + id()}" />
			<input type="hidden" data-bind="value: maxRows, attr: {'name': 'rowsmax' + id()}" />
			<input type="hidden" data-bind="value: widths, attr: {'name': 'widths' + id()}" />
			<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
			<label hidden><textarea data-bind="text: help, attr: {'name': 'help' + id()}" ></textarea>${form.getMessage("label.Help")}</label>
			<input type="hidden" data-bind="value: editorColumnsLocked(), attr: {'name': 'editorColumnsLocked' + id()}" />
			<input type="hidden" data-bind="value: editorRowsLocked(), attr: {'name': 'editorRowsLocked' + id()}" />
		<!-- /ko -->		
	
		<!-- ko if: optional() == false -->
			<span class="mandatory">*</span>
		<!-- /ko -->
		<div id="defaultMatrixTemplateID" class='questiontitle' data-bind='attr: {id: "questiontitle" + id()}'>
			<span class="screen-reader-only">${form.getMessage("form.Question")}</span>
			<span data-bind='html: title'></span>
			<span class="screen-reader-only" data-bind="if: help">${form.getMessage("form.HelpAvailable")}</span>
		</div>
		<span class="questionhelp" data-bind="html: niceHelp, attr:{id: 'questionhelp' + id()}"></span>
		
		<!-- ko if: minRows() != 0 && maxRows() != 0 -->
			<div class='limits' data-bind="html: getMinMaxRows(minRows(), maxRows()), attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
		<!-- ko if: minRows() != 0 && maxRows() == 0 -->
			<div class='limits' data-bind="html: getMinRows(minRows()), attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
		<!-- ko if: minRows() == 0 && maxRows() != 0 -->
			<div class='limits' data-bind="html: getMaxRows(maxRows()), attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->
		<!-- ko if: minRows() == 0 && maxRows() == 0 -->
			<div class='limits' data-bind="attr: {id: 'questioninfo' + id()}"></div>
		<!-- /ko -->

		<div style="width: 100%">
			
			<!-- ko if: foreditor -->
			<div class="hiddenmatrixquestions hideme">
				<!-- ko foreach: questions() -->
				<div data-bind="attr: {'pos': $index, 'data-id': id}">
					<input type="hidden" data-bind="value: 'text', attr: {'name': 'type' + id()}" />
					<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />	
					<input type="hidden" data-bind="value: optional, attr: {'name': 'optional' + id()}" />
					<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />
					<input type="hidden" data-bind="value: useAndLogic, attr: {'name': 'useAndLogic' + id()}" />	
					<input type="hidden" data-bind="value: readonly, attr: {'name': 'readonly' + id()}" />
					<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
					
					<!-- ko foreach: $parent.answers() -->
						<input type="hidden" data-bind="attr: {'name': 'dependencies' + $parents[1].id(), 'value': $parents[1].dependentElementsStrings()[$index() + ($parent.originalIndex() * ($parents[1].columns()-1))], 'data-qaid': $parent.id() + '|' + id()}" />
					<!-- /ko -->
					
				</div>
				<!-- /ko -->
			</div>
			<!-- /ko -->
		
			<div class="table-responsive">
				<table data-bind="attr: {'class':'matrixtable ' + css(), 'style': tableType() == 1 ? 'width: 900px' : 'width: auto; max-width: auto', 'aria-labelledby':'questiontitle' + id(), 'aria-describedby':'questioninfo' + id() + ' questionhelp' + id()}">			
					<thead>
						<tr>
							<td class="matrix-header firstCell" data-bind="attr: {'data-id': id(), 'style': tableType() != 2 ? '' : 'width: ' + getWidth(widths(), 0)}">
								<!-- ko if: foreditor -->
									<textarea aria-labelledby="defaultMatrixTemplateID" style="display: none" data-bind="text: firstCellText, attr: {'name': 'firstCellText' + id()}"></textarea>
								<!-- /ko -->
								<span class="matrixheadertitle" data-bind="html: firstCellText"></span>
							</td>
							<!-- ko foreach: answers -->
							<td class="matrix-header" scope="col" data-bind="attr: {'id' : id(), 'data-id': id(), 'style': $parent.tableType() != 2 ? '' : 'width: ' + getWidth($parent.widths(), $index()+1)}">
								<!-- ko if: $parent.foreditor -->
								<input type="hidden" data-bind="value: 'text', attr: {'name': 'type' + id()}" />
								<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />	
								<input type="hidden" data-bind="value: optional, attr: {'name': 'optional' + id()}" />
								<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />
								<input type="hidden" data-bind="value: readonly, attr: {'name': 'readonly' + id()}" />
								<label hidden><textarea data-bind="text: title, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.Title")}</label>
								 <!-- /ko -->
								<span class="matrixheadertitle" data-bind="html: title"></span>
							</td>
							 <!-- /ko -->
						</tr>
					</thead>
					<tbody>					
						<!-- ko foreach: questionsOrdered() -->			
						<tr data-bind="attr: {'class': $data.isDependentMatrixQuestion() && isInvisible($data.uniqueId()) ? 'matrix-question untriggered hideme':'matrix-question', 'data-id': id(), 'data-uid': uniqueId(), 'data-triggers': getTriggersByQuestion(uniqueId()) + ';' + ($parent.foreditor ? '' : getTriggersByQuestion($parent.uniqueId)), 'data-useAndLogic': useAndLogic()}"> 
							<th class="matrix-header" scope="row" data-bind="attr: {'id' : id(), 'data-id': id}">
								<!-- ko if: optional() == false -->
									<span class="mandatory" style="position: absolute; margin-left: -7px; margin-top: 3px;">*</span>
								<!-- /ko -->
								<span class="matrixheadertitle" data-bind="html: title"></span>
							</th>
							<!-- ko foreach: $parent.answers -->
								<td class="matrix-cell">
									<input aria-labelledby="defaultMatrixTemplateID" type="radio" data-bind="enable: !$parents[1].readonly() && !$parents[1].foreditor, checked: getPAByQuestion2($parent.uniqueId(), uniqueId(), id()), attr: {value: id(), 'data-shortname': $parent.shortname() + '|' + shortname(), onkeyup: 'singleKeyUp(event, this, '+$parents[1].readonly()+')', 'onclick': $parents[1].readonly() ? 'return false;' : 'findSurveyElementAndResetValidationErrors(this); checkSingleClick(this); event.stopImmediatePropagation();propagateChange(this);', 'id': $parent.id().toString() + id().toString(), 'data-id': $parent.id().toString() + id().toString(), 'aria-labelledby': $parent.id().toString() + ' ' + id().toString(), 'class': $parent.css() + ' trigger', 'name': 'answer' + $parent.id(), 'data-dependencies': $parents[1].dependentElementsStrings()[$index() + ($parent.originalIndex() * ($parents[1].columns()-1))], 'data-cellid' : $parent.id() + '|' + id(), type: $parents[1].isSingleChoice() ? 'radio' : 'checkbox', role: $parents[1].isSingleChoice() ? 'radio' : 'checkbox', 'data-dummy': getPAByQuestion2($parent.uniqueId(), uniqueId(), id())}" />
								</td>
							 <!-- /ko -->
						</tr>
						<!-- /ko -->
					</tbody>
				</table>
			</div>
		</div>

	</div>
	
	<div id="table-template">
		<!-- ko if: foreditor -->
			<input type="hidden" data-bind="value: 'table', attr: {'name': 'type' + id()}" />
			<input type="hidden" data-bind="value: tableType, attr: {'name': 'tabletype' + id()}" />	
			<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />	
			<input type="hidden" data-bind="value: optional, attr: {'name': 'optional' + id()}" />
			<input type="hidden" data-bind="value: isDelphiQuestion, attr: {'name': 'delphiquestion' + id()}" />
			<input type="hidden" data-bind="value: showExplanationBox, attr: {'name': 'explanationbox' + id()}" />
			<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />
			<input type="hidden" data-bind="value: useAndLogic, attr: {'name': 'useAndLogic' + id()}" />	
			<input type="hidden" data-bind="value: readonly, attr: {'name': 'readonly' + id()}" />
			<input type="hidden" data-bind="value: widths, attr: {'name': 'widths' + id()}" />
			<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
			<label hidden><textarea data-bind="text: help, attr: {'name': 'help' + id()}" ></textarea>${form.getMessage("label.Help")}</label>
		    <input type="hidden" data-bind="value: editorColumnsLocked(), attr: {'name': 'editorColumnsLocked' + id()}" />
			<input type="hidden" data-bind="value: editorRowsLocked(), attr: {'name': 'editorRowsLocked' + id()}" />
		<!-- /ko -->
		<!-- ko if: optional() == false -->
			<span class="mandatory">*</span>
		<!-- /ko -->
		<div id="defaultTableTemplateID" class='questiontitle' data-bind='attr: {id: "questiontitle" + id()}'>
			<span class="screen-reader-only">${form.getMessage("form.Question")}</span>
			<span data-bind='html: title'></span>
			<span class="screen-reader-only" data-bind="if: help">${form.getMessage("form.HelpAvailable")}</span>
		</div>
		<span class="questionhelp" data-bind="html: niceHelp, attr:{id: 'questionhelp' + id()}"></span>
		
		<div class="table-responsive">

			<table data-bind="attr: {'data-widths':widths(), 'id':id(), 'data-readonly': readonly, 'style': tableType() == 1 ? 'width: 900px' : 'width: auto; max-width: auto', 'aria-labelledby':'questiontitle' + id(), 'aria-describedby':'questionhelp' + id()}" class="tabletable">
				<tbody>
					<tr style="background-color: #eee;">
						<td class="table-header firstCell" data-bind="attr: {'data-id': id(), 'style': tableType() != 2 ? '' : 'width: ' + getWidth(widths(), 0)}">
							<!-- ko if: foreditor -->
							<textarea aria-labelledby="defaultTableTemplateID" style="display: none" data-bind="text: firstCellText, attr: {'name': 'firstCellText' + id()}"></textarea>
							<!-- /ko -->
							<span class="matrixheadertitle" data-bind="html: firstCellText"></span>
						</td>
						<!-- ko foreach: answers -->
						<td class="table-header" scope="col" data-bind="attr: {'id' : id(), 'data-id' : id(), 'data-shortname' : shortname, 'data-uid' : uniqueId(), 'style': $parent.tableType() != 2 ? '' : 'width: ' + getWidth($parent.widths(), $index()+1)}">
							<span data-bind="html: title"></span>
							<!-- ko if: $parent.foreditor -->
							<textarea aria-labelledby="defaultTableTemplateID" style="display: none" data-bind="text: originalTitle" ></textarea>
							 <!-- /ko -->
						</td>
						 <!-- /ko -->
					</tr>
					<!-- ko foreach: questions -->
					<tr data-bind="attr: {'data-id': id()}"> 
						<th scope="row" style="padding-left: 10px" class="table-header" data-bind="attr: {'id' : id(), 'data-id' : id(),'data-shortname' : shortname, 'data-uid' : uniqueId(), 'data-optional' : optional().toString()}">
							<!-- ko if: optional() == false -->
								<span class="mandatory" style="position: absolute; margin-left: -7px; margin-top: 3px;">*</span>
							<!-- /ko -->
							
							<span data-bind="html: title"></span>
							<!-- ko if: $parent.foreditor -->
							<textarea aria-labelledby="defaultTableTemplateID" style="display: none" data-bind="text: originalTitle" ></textarea>
							 <!-- /ko -->
						</th>
						<!-- ko foreach: $parent.answers -->
							<td style="padding: 2px;">
								<textarea aria-labelledby="defaultTableTemplateID" onblur="validateInput($(this).closest('.tabletable').parent(), true)" oninput="propagateChange(this);" data-bind="enable: !$parents[1].readonly(), value: getTableAnswer($parents[1].uniqueId(), $parentContext.$index()+1, $index()+1, true), attr: {'data-id': $parents[1].id() + $parentContext.$index() + '' + $index(), 'data-shortname': $parent.shortname() + '|' + shortname(), 'class':$parents[1].css() + ' ' + $parents[0].css(), 'name':'answer' + $parents[1].id() + '|' + ($parentContext.$index()+1) + '|' + ($index()+1), 'aria-labelledby': $parent.id().toString() + ' ' + id().toString()}"></textarea>
							</td>
						 <!-- /ko -->
					</tr>
					<!-- /ko -->
				</tbody>
			</table>
		</div>
	</div>
	
	<div id="delphi-template" data-bind="class: ismobile || istablet ? 'delphi-template-mobile' : 'delphi-template'">
		<!-- ko if: isDelphiQuestion() -->
		
		<div class="delphichildren"></div>
		
		<!-- ko if: maxDistanceExceeded() && !changedForMedian() -->
		<div class="maxDistanceExceededMessage">
			${form.getMessage("info.MaxDistanceExceeded")}&nbsp;${form.getMessage("info.MaxDistanceExceededExplain")}
			<input type="hidden" name="medianWarningVisible" value="true" />
			
			<!-- ko if: median() -->
			<div>${form.getMessage("label.GroupMedian")}: <span data-bind="html: median()"></span></div>
			<!-- /ko -->
		</div>	
			
		<!-- /ko -->
				
		<div class="row" style="margin-left: 0; margin-right: 0; margin-top: 20px;">					
			<div class="col-md-6" data-bind="style: {'padding-right': ismobile ? 0 : undefined, 'padding-left': 0}">
				<!-- ko if: showExplanationBox() -->
				<div class="explanation-section">
				
					<table class='table table-condensed table-bordered minh355' style="width: auto; margin-bottom: 0; background-color: #fff">
						<tr>
							<th class='area-header'>${form.getMessage("label.OptionalAdditionalComments")}</th>
						</tr>
						<tr>
							<td>
								<label hidden for="explanationTemplateID" data-bind="attr: {'for': 'explanation' + id()}">${form.getMessage("label.Explanation")}</label>
								<textarea id="explanationTemplateID" style="height: 125px" class="explanation-editor" data-bind="attr: {'id': 'explanation' + id(), name: 'explanation' + id()}" ></textarea>
							</td>
						</tr>
						<tr>
							<td>
								<div class="explanation-file-upload-section">
									<div class="text" style="margin-bottom: 5px;">${form.getMessage("label.SupplyFileToExplainYourAnswer")}</div>
									<div class="uploadinfo"
										style="display: none; padding: 10px; color: #777;">${form.getMessage("label.UploadStarted")}</div>
									<input type="hidden"
										value="files" />
									<div class="uploaded-files"
										data-bind="foreach: getFileAnswer(uniqueId())">
										<div>
											<a data-toggle="tooltip" title="${form.getMessage("label.RemoveUploadedFile")}" data-bind="attr: {'id' : 'uploadlink' + $parent.id(), 'aria-label' : $data}, click: function() {deleteFile($parent.id(),'${uniqueCode}',$data,$('#uploadlink' + $parent.id()));return false;}">
												<span style="margin-right: 10px;"
												class="glyphicon glyphicon-trash"></span>
											</a> <span data-bind="html: $data"></span>
										</div>
									</div>
									<div data-bind="attr: {'class': 'file-uploader', 'data-id': id()}"
										style="margin-left: 10px; margin-top: 10px;"></div>
								</div>		
							</td>
						</tr>
					</table>
				</div>
				<!-- /ko -->		
			</div>
			<div class="col-md-6" style="padding:0;">
				<!-- ko if: !foreditor -->
				<div class="chart-wrapper-loader">
					<img alt="wait animation" src="${contextpath}/resources/images/ajax-loader.gif">
				</div>
				<!-- /ko -->
				<div class="chart-wrapper" data-bind="style: {float: ismobile || istablet ? 'left' : undefined}">
					<table class="table table-condensed table-bordered chart-wrapper__table">
						<tr>
							<th class="area-header">
								<span>${form.getMessage("label.DelphiChartTitle")}</span>
								<a onclick="loadGraphDataModal(this)" class="glyphicon glyphicon-resize-full delphi-chart-expand" data-toggle="tooltip" title="${form.getMessage("tooltip.ExpandChart")}" aria-label="${form.getMessage("tooltip.ExpandChart")}"></a>
							</th>
						</tr>
						<tr>
							<td class="chart-wrapper__chart-cell">
								<div class="chart-wrapper__chart-container"></div>
								<div data-bind="attr: {id: 'wordcloud' + uniqueId()}" class="chart-wrapper__word-cloud-container"></div>
							</td>
						</tr>
					</table>
					<div style="clear: both"></div>
				</div>
			</div>
		</div>
		
		<div class="row" style="margin-left: 0; margin-right: 0; margin-top: 0px;">
			<div class="col-md-12" style="padding:0;">
				<div class="explanation-update-section">
					<a class="btn btn-primary disabled" data-type="delphisavebutton" onclick="if (!$(this).hasClass('disabled')) { delphiUpdate($(this).closest('.survey-element')) }">${form.getMessage("label.Save")}</a>
					<span class="inline-loader">
						<img alt="wait animation" class="center" src="${contextpath}/resources/images/ajax-loader.gif"/>
					</span>
					
					<br /><br />
					<c:if test="${form.survey.isDelphiShowStartPage}">
						<a data-type="delphireturntostart" class="link" style="margin-right: 20px;"  onclick="return checkGoToDelphiStart(this)">${form.getMessage("label.ReturnToDelphiStart")}</a>
					</c:if>
					
					<a data-type="delphitonextquestion" class="link delphitonextquestion" onclick="goToNextQuestion(this)">${form.getMessage("label.GoToNextQuestion")}</a>
				</div>
		
				<div class="delphiupdatemessage"></div>

				<!-- ko if: delphiTableNewComments() -->
				<div class="newdelphicomments label">${form.getMessage("label.DelphiAnswersTableNewComments")}</div>
				<!-- /ko -->
			</div>
		</div>

		<c:if test="${form.survey.isDelphi}">
			<div class="row results-table-row" style="margin-left: 0; margin-right: 0; margin-top: 20px;">
				<div class="col-md-12" style="padding:0;">
					<%@ include file="delphiAnswersTable.jsp" %>
				</div>
			</div>
		</c:if>

		<div class="modal delete-confirmation-dialog" role="dialog" data-backdrop="static">
			<div class="modal-dialog modal-sm">
				<div class="modal-content">
					<div class="modal-body">
						<spring:message code="message.DelphiConfirmDeleteComment" />
					</div>
					<div class="modal-footer">
						<a class="btn btn-default delete-confirmation-dialog__confirmation-button"><spring:message code="label.Delete" /></a>
						<a class="btn btn-primary" onclick="hideModalDialog($(this).closest('.modal'))"><spring:message code="label.Cancel" /></a>
					</div>
				</div>
			</div>
		</div>
		
		<!-- /ko -->
	</div>
	
	<div id="complextable-template">
		<div class='questiontitle' data-bind='attr: {id: "questiontitle" + id()}'>
			<span class="screen-reader-only">${form.getMessage("form.Question")}</span>
			<span data-bind='html: title'></span>
			<span class="screen-reader-only" data-bind="if: help">${form.getMessage("form.HelpAvailable")}</span>
		</div>
		<span class='questionhelp' data-bind="html: niceHelp, attr:{id: 'questionhelp' + id()}"></span>
		
		<div class="table-responsive">
		
			<table class="table complextable" data-bind="css: { 'table-bordered': showHeadersAndBorders() || foreditor }, attr: {'style': size() == 0 ? 'width: auto' : 'width: 900px', 'aria-labelledby':'questiontitle' + id(), 'aria-describedby':'questionhelp' + id()}">
				<tr data-bind="if: showHeadersAndBorders() || foreditor">
					<!-- ko foreach: answers() -->
						<!-- ko if: $index() == 0-->
							<td class="headercell cell" data-bind="html: title, attr:{'data-id': id(), colspan: columnSpan()}">placeholder</td>
						<!-- /ko -->
						<!-- ko ifnot: $index() == 0-->
							<th class="headercell cell" data-bind="html: title, attr:{'data-id': id(), colspan: columnSpan()}">placeholder</th>
						<!-- /ko -->
					<!-- /ko -->
				</tr>
				
				<!-- ko foreach: questions() -->
				<tr>
					<!-- ko if: $parent.showHeadersAndBorders() || $parent.foreditor -->
						<th class="headercell cell" data-bind="html: title, attr:{'data-id': id(), 'data-type': cellType(), colspan: columnSpan()}">placeholder</th>
					<!-- /ko -->
	
					<!-- ko foreach: new Array($parent.columns()) -->
					  	
					  	<!-- ko if: $parents[1].isCellVisible($index()+1, $parentContext.$index()+1) -->
					  	
					  	<!--ko let: {child: $parents[1].getChild($index()+1, $parentContext.$index()+1)}-->
					  	<td class="cell" data-bind="attr:{'data-id': child == null ? '' : child.id(), 'data-type': child == null ? '' : child.cellType(), colspan: child && child.columnSpan(), 'data-col': $index()+1}">
							<!-- ko if: child == null || child.cellType() == 0 -->
							<span>&nbsp;</span>
							<!-- /ko -->
	
							<!-- ko if: child && child.cellType() > 0 -->						
								<!-- ko if: child.optional() == false -->
									<span class="mandatory">*</span>
								<!-- /ko -->
						
								<!-- ko if: child.title() -->
									<span id="defaultComplextableChildTemplateID" class='questiontitle' data-bind="attr: {id: 'questiontitle' + child.id()}">
										<span class="screen-reader-only">${form.getMessage("form.Question")}</span>
										<span data-bind='html: child.title()'></span>
										<span class="screen-reader-only" data-bind="if: child.help()">${form.getMessage("form.HelpAvailable")}</span>
									</span>
								<!-- /ko -->
								<!-- ko ifnot: child.title() -->
									<div hidden class='questiontitle' data-bind="attr: {id: 'questiontitle' + child.id()}">${form.getMessage("form.Question")}</div>
								<!-- /ko -->
								
								<!-- ko if: child.help() -->
									<span class='questionhelp' data-bind="html: child.niceHelp(), attr:{id: 'questionhelp' + child.id()}"></span>
								<!-- /ko -->
								<!-- ko ifnot: child.help() -->
									<span hidden class='questionhelp' data-bind="attr:{id: 'questionhelp' + child.id()}"></span>
								<!-- /ko -->
							<!-- /ko -->
							
							<!-- ko if: child && child.cellType() > 1 -->
							<div data-bind="attr: {class: child.cellType() == 3 ? 'innercell' : 'innercell forprogress',
								onfocusout: !$parents[1].foreditor ? 'validateInput($(this).closest(\'.cell\'))' : '',
								onmouseleave: !$parents[1].foreditor ? 'validateInput($(this).closest(\'.cell\'))' : ''}">
								<!-- focusout bubbles while blur does not; this combination of mouseleave and focusout covers all Tab/Mouse cases of leaving the cell -->
	
								<!-- ko if: child && child.cellType() == 2 -->
								
								<!-- ko if: child.minCharacters() > 0 && child.maxCharacters() > 0 -->
									<div class='limits' data-bind="html: getMinMaxCharacters(child.minCharacters(), child.maxCharacters()), attr: {id: 'questioninfo' + child.id()}"></div>
								<!-- /ko -->
								<!-- ko if: child.minCharacters() > 0 && (child.maxCharacters() == 0 || child.maxCharacters() == null) -->
									<div class='limits' data-bind="html: getMinCharacters(child.minCharacters()), attr: {id: 'questioninfo' + child.id()}"></div>
								<!-- /ko -->
								<!-- ko if: (child.minCharacters() == 0 || child.minCharacters() == null) && child.maxCharacters() > 0 -->
									<div class='limits' data-bind="html: getMaxCharacters(child.maxCharacters()), attr: {id: 'questioninfo' + child.id()}"></div>
								<!-- /ko -->
								<!-- ko if: (child.minCharacters() == 0 || child.minCharacters() == null) && (child.maxCharacters() == 0 || child.maxCharacters() == null) -->
									<div class='limits' data-bind="attr: {id: 'questioninfo' + child.id()}"></div>
								<!-- /ko -->

								<textarea aria-labelledby="defaultComplextableChildTemplateID" oninput="propagateChange(this)" data-bind="enable: child.foreditor == false && !child.readonly(), class: child.css(), value:getValueByQuestion(child.uniqueId(), true, $element), attr: {'name' : 'answer' + child.id(), rows: child.numRows(), maxlength: child.maxCharacters() > 0 ? child.maxCharacters() : '', onkeyup: child.maxCharacters() > 0 ? 'countChar(this);' : '', 'aria-labelledby':'questiontitle' + child.id(), 'aria-describedby':'questioninfo' + child.id() + ' questionhelp' + child.id()}"></textarea>

								<!-- ko if: child.maxCharacters() > 0 && !$parent.foreditor -->
									<div class="charactercounterdiv limits" style="max-width: 645px; text-align: right; margin-left: 20px;" aria-live="polite" aria-atomic="true">
										<span class="glyphicon glyphicon-alert" style="display: none; margin-right: 5px;" data-toggle="tooltip" title="${form.getMessage("info.charactercounter")}" aria-label="${form.getMessage("info.charactercounter")}"></span>
										<span class="charactersused">
											<span data-bind="html: getCharacterCountInfo(child.maxCharacters()), attr: {id: 'countinfo' + child.id()}"></span>
										</span>
										<span class="characterlimitreached" data-toggle="tooltip" aria-label="${form.getMessage("info.AllCharactersUsed")}">${form.getMessage("info.AllCharactersUsed")}</span>
									</div>
								<!-- /ko -->


								<!-- /ko -->
		
								<!-- ko if: child && child.cellType() == 3 -->
									<!-- ko if: child.min() != null && child.min() != 0 && child.max() != null && child.max() != 0 -->
										<div class='limits' data-bind="html: getMinMax(child.min(), child.max()), attr: {id: 'questioninfo' + child.id()}"></div>
									<!-- /ko -->
									<!-- ko if: child.min() != 0 && child.min() != null && (child.max() == 0 || child.max() == null) -->
										<div class='limits' data-bind="html: getMin(child.min()), attr: {id: 'questioninfo' + child.id()}"></div>
									<!-- /ko -->
									<!-- ko if: (child.min() == 0 || child.min() == null) && child.max() != null && child.max() != 0 -->
										<div class='limits' data-bind="html: getMax(child.max()), attr: {id: 'questioninfo' + child.id()}"></div>
									<!-- /ko -->
									<!-- ko if: (child.min() == 0 || child.min() == null) && (child.max() == 0 || child.max() == null) -->
										<div class='limits' data-bind="attr: {id: 'questioninfo' + child.id()}"></div>
									<!-- /ko -->
									
								
									<input aria-labelledby="defaultComplextableChildTemplateID" data-bind="enable: child.foreditor == false && !child.readonly(), value: child.result, attr: {'id': 'answer' + child.id(), 'data-id':child.id(), 'data-shortname': child.shortname(), 'name' : (child.readonly() ? '' : 'answer' + child.id()), 'class': child.css(), 'aria-labelledby':'questiontitle' + child.id(), 'aria-describedby':'questioninfo' + child.id() + ' questionhelp' + child.id()}" oninput="propagateChange(this);" onblur="resetValidationErrors($(this).closest('.cell'));validateInput($(this).parent())" type="text" autocomplete="off" />
									
									<!-- ko if: child.readonly() -->
									<input type="hidden" data-bind="value: child.result, attr: {'name': 'answer' + child.id()}" />
									<!-- /ko -->							
								<!-- /ko -->
		
								<!-- ko if: child && child.cellType() == 4 -->
									<!-- ko if: child && child.useRadioButtons() -->
										<div style="display: table" role="radiogroup" data-bind="attr: {'aria-labelledby':'questiontitle' + child.id(), 'aria-describedby':'questionhelp' + child.id()}">
											<div style="display: table-row">
												<!-- ko foreach: child.orderedPossibleAnswersByColumn(${ismobile != null}, ${responsive != null}) -->
												<div style="display: table-cell; padding-right: 10px">
													<!-- ko foreach: $data -->
													<input aria-labelledby="defaultComplextableChildTemplateID" type="radio" data-bind="enable: child.foreditor == false && !child.readonly(), checkedValue: true, checked: !child.foreditor && getPAByQuestion(child.uniqueId(), $element).indexOf(uniqueId()) > -1, value: id(), attr: {'name' : 'answer' + child.id(), 'id': 'answer' + child.id(), 'aria-labelledby': 'questiontitle' + child.id(), class: child.css(), 'onclick': child.readonly() ? 'return false;' : 'checkSingleClick(this); propagateChange(this);', onkeyup: 'singleKeyUp(event, this, '+child.readonly()+')', 'previousvalue': getPAByQuestion(child.uniqueId(), $element).indexOf(uniqueId()) > -1 ? 'checked' : 'false'}" /> <span data-bind="html: title()"></span><br />
													<!-- /ko -->
												</div>
												<!-- /ko -->
											</div>
										</div>
									<!-- /ko -->
									<!-- ko if: child && !child.useRadioButtons() -->
										<select aria-labelledby="defaultComplextableChildTemplateID" data-bind="enable: child.foreditor == false && !child.readonly(), value: getPAByQuestion3(child.uniqueId(), $element), attr: {'id': 'answer' + child.id(), 'onclick': !child.foreditor ? 'validateInput($(this).parent(),true); checkDependenciesAsync(this); propagateChange(this);' : '', 'data-id':child.id(), 'data-shortname': child.shortname(), 'name' : child.foreditor ? '' : ('answer' + child.id()), 'class': child.css(), 'aria-labelledby':'questiontitle' + child.id(), 'aria-describedby':'questionhelp' + child.id()}">
											<option selected="selected" value=''></option>
											<!-- ko foreach: child.orderedPossibleAnswers(false) -->
												<option data-bind="html: title(), attr: {value: id(), 'data-dependencies': dependentElementsString(), 'id': 'trigger'+id()}" class="possible-answer trigger"></option>
											<!-- /ko -->
										</select>
									<!-- /ko -->
								<!-- /ko -->
		
								<!-- ko if: child && child.cellType() == 5 -->
									<!-- ko if: child.minChoices() != 0 && child.maxChoices() != 0 -->
										<div class='limits' data-bind="html: getMinMaxChoice(child.minChoices(), child.maxChoices()), attr: {id: 'questioninfo' + child.id()}"></div>
									<!-- /ko -->
									<!-- ko if: child.minChoices() != 0 && child.maxChoices() == 0 -->
										<div class='limits' data-bind="html: getMinChoice(child.minChoices()), attr: {id: 'questioninfo' + child.id()}"></div>
									<!-- /ko -->
									<!-- ko if: child.minChoices() == 0 && child.maxChoices() != 0 -->
										<div class='limits' data-bind="html: getMaxChoice(child.maxChoices()), attr: {id: 'questioninfo' + child.id()}"></div>
									<!-- /ko -->
									<!-- ko if: child.minChoices() == 0 && child.maxChoices() == 0 -->
										<div class='limits' data-bind="attr: {id: 'questioninfo' + child.id()}"></div>
									<!-- /ko -->
								
									<!-- ko if: child && child.useCheckboxes() -->
										<div class="complex-multitable" style="display: table">
											<div style="display: table-row"  role="list" data-bind="attr: {'aria-labelledby':'questiontitle' + child.id(), 'aria-describedby':'questioninfo' + child.id() + ' questionhelp' + child.id()}">
											<!-- ko foreach: child.orderedPossibleAnswersByColumn(${ismobile != null}, ${responsive != null}) -->
												<div style="display: table-cell; padding-right: 10px">
													<!-- ko foreach: $data -->
													<input aria-labelledby="defaultComplextableChildTemplateID" type="checkbox" onclick="resetValidationErrors($(this).closest('.cell'));propagateChange(this)" data-bind="enable: child.foreditor == false && !child.readonly(), checked: !child.foreditor && getPAByQuestionCheckBox(child.uniqueId(), uniqueId(), $element).indexOf(uniqueId()) > -1, value: id(), attr: {'name' : 'answer' + child.id(), 'id': 'answer' + child.id(), 'aria-labelledby': 'questiontitle' + child.id(), class: child.css()}"/> <span data-bind="html: title()"></span><br />
													<!-- /ko -->
												</div>
											<!-- /ko -->
											</div>
										</div>
									<!-- /ko -->
									<!-- ko if: child && !child.useCheckboxes() -->	
										<ul role="listbox" data-bind="attr: {'class': child.css() + ' multiple-choice', 'aria-labelledby':'questiontitle' + child.id(), 'aria-describedby':'questioninfo' + child.id() + ' questionhelp' + child.id()}, foreach: child.orderedPossibleAnswers(false),">
											<li role="listitem" data-bind="attr: { 'data-id': id(), 'class': 'possible-answer trigger ' + (getPAByQuestion(child.uniqueId()).indexOf(uniqueId()) > -1 ? 'selected-choice' : '')}">
												<a tabindex="0" data-bind="attr: {'data-shortname': shortname(), 'onkeypress': child.readonly() || child.foreditor ? 'return false;' : 'preventScrollOnSpaceInput(event);findSurveyElementAndResetValidationErrors(this);selectMultipleChoiceAnswer(this);propagateChange(this);', 'onclick' : child.readonly() || child.foreditor ? 'return false;' : 'selectMultipleChoiceAnswer($(this)); propagateChange($(this)); event.stopImmediatePropagation();'}" >
													<span class="screen-reader-only">${form.getMessage("label.Answer")} </span>
													<span data-bind="html: strip_tags(title()), attr: {'data-id' : id(), 'id': 'answerlabel' + id()}" class="answertext"></span>
												</a>
												<input aria-labelledby="defaultComplextableChildTemplateID" data-bind="value: id(), checked: getPAByQuestion2(child.uniqueId(), uniqueId(), id, $element), attr: {'name': 'answer' + child.id(), 'id':id(), 'data-id': child.id() + id(), 'data-dependencies': dependentElementsString, 'aria-labelledby': 'answerlabel' + id()}" style="display: none" type="checkbox" />
											</li>	
										</ul>
									<!-- /ko -->
								<!-- /ko -->
		
								<!-- ko if: child && child.cellType() == 6 -->
								
								<!-- ko if: child.min() != null && child.min() != 0 && child.max() != null && child.max() != 0 -->
									<div class='limits' data-bind="html: getMinMax(child.min(), child.max()), attr: {id: 'questioninfo' + child.id()}"></div>
								<!-- /ko -->
								<!-- ko if: child.min() != 0 && child.min() != null && (child.max() == 0 || child.max() == null) -->
									<div class='limits' data-bind="html: getMin(child.min()), attr: {id: 'questioninfo' + child.id()}"></div>
								<!-- /ko -->
								<!-- ko if: (child.min() == 0 || child.min() == null) && child.max() != null && child.max() != 0 -->
									<div class='limits' data-bind="html: getMax(child.max()), attr: {id: 'questioninfo' + child.id()}"></div>
								<!-- /ko -->
								<!-- ko if: (child.min() == 0 || child.min() == null) && (child.max() == 0 || child.max() == null) -->
									<div class='limits' data-bind="attr: {id: 'questioninfo' + child.id()}"></div>
								<!-- /ko -->
								
								<input aria-labelledby="defaultComplextableChildTemplateID" type="number" oninput="propagateChange(this);" onblur="resetValidationErrors($(this).closest('.cell'));validateInput($(this).parent())" data-bind="enable: child.foreditor == false && !child.readonly(), class: child.css(), value:getValueByQuestion(child.uniqueId(), true, $element), attr: {'name' : 'answer' + child.id(), min: child.min(), max: child.max(), 'data-shortname': child.shortname(), 'aria-labelledby':'questiontitle' + child.id(), 'aria-describedby':'questioninfo' + child.id() + ' questionhelp' + child.id()}"/>
								<!-- ko if: child.unit -->
								<span data-bind="text: child.unit"></span>
								<!-- /ko -->
								<!-- /ko -->
							</div>
							<!-- /ko -->
					  	</td>
					  	<!-- /ko -->
					  	<!-- /ko -->
					  	
					<!-- /ko -->
				</tr>
				<!-- /ko -->	
			</table>
		</div>
			
		<!-- ko if: foreditor -->
			<input type="hidden" data-bind="value: 'complextable', attr: {'name': 'type' + id()}" />	
			<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />	
			<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />
			<input type="hidden" data-bind="value: useAndLogic, attr: {'name': 'useAndLogic' + id()}" />	
			<input type="hidden" data-bind="value: optional, attr: {'name': 'optional' + id()}" />
			<input type="hidden" data-bind="value: rows, attr: {'name': 'rows' + id()}" />
			<input type="hidden" data-bind="value: columns, attr: {'name': 'columns' + id()}" />
			<input type="hidden" data-bind="value: size, attr: {'name': 'size' + id()}" />
			<input type="hidden" data-bind="value: showHeadersAndBorders, attr: {'name': 'showHeadersAndBorders' + id()}" />
			<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
			<label hidden><textarea data-bind="text: help, attr: {'name': 'help' + id()}" ></textarea>${form.getMessage("label.Help")}</label>
			<input type="hidden" data-bind="value: childIds(), attr: {'name': 'childelements' + id()}" />
			
			<div class="children">
			<!-- ko foreach: orderedChildElements -->
				<div data-bind="attr: {'id' : 'child' + id()}">
					<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'text' + id(), 'data-id': id()}" ></textarea>${form.getMessage("label.OriginalTitle")}</label>
					<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'uid' + id()}" />	
					<input type="hidden" data-bind="value: shortname, attr: {'name': 'shortname' + id()}" />
					<input type="hidden" data-bind="value: optional, attr: {'name': 'optional' + id()}" />
					<input type="hidden" data-bind="value: cellType, attr: {'name': 'cellType' + id()}" />
					<input type="hidden" data-bind="value: row, attr: {'name': 'row' + id()}" />
					<input type="hidden" data-bind="value: column, attr: {'name': 'column' + id()}" />
					<input type="hidden" data-bind="value: columnSpan, attr: {'name': 'columnSpan' + id()}" />
					<label hidden><textarea data-bind="text: help, attr: {'name': 'help' + id()}" ></textarea>${form.getMessage("label.Help")}</label>
					<input type="hidden" data-bind="value: minCharacters, attr: {'name': 'minCharacters' + id()}" />
					<input type="hidden" data-bind="value: maxCharacters, attr: {'name': 'maxCharacters' + id()}" />
					<input type="hidden" data-bind="value: minChoices, attr: {'name': 'minChoices' + id()}" />
					<input type="hidden" data-bind="value: maxChoices, attr: {'name': 'maxChoices' + id()}" />
					<input type="hidden" data-bind="value: numRows, attr: {'name': 'numRows' + id()}" />			
					<input type="hidden" data-bind="value: useRadioButtons, attr: {'name': 'useRadioButtons' + id()}" />
					<input type="hidden" data-bind="value: useCheckboxes, attr: {'name': 'useCheckboxes' + id()}" />
					<input type="hidden" data-bind="value: numColumns, attr: {'name': 'numColumns' + id()}" />
					<input type="hidden" data-bind="value: order, attr: {'name': 'order' + id()}" />
					<input type="hidden" data-bind="value: resultText, attr: {'name': 'resultText' + id()}" />
					<input type="hidden" data-bind="value: decimalPlaces, attr: {'name': 'decimalPlaces' + id()}" />
					<input type="hidden" data-bind="value: unit, attr: {'name': 'unit' + id()}" />
					<input type="hidden" data-bind="value: min, attr: {'name': 'min' + id()}" />
					<input type="hidden" data-bind="value: max, attr: {'name': 'max' + id()}" />
					<input type="hidden" data-bind="value: formula, attr: {'name': 'formula' + id()}" />
					<input type="hidden" data-bind="value: readonly, attr: {'name': 'readonly' + id()}" />
									
					<!-- ko foreach: possibleAnswers() -->
						<input type="hidden" data-bind="value: shortname, attr: {'name': 'pashortname' + $parents[0].id(), 'data-id' : id()}" />	
						<input type="hidden" data-bind="value: uniqueId(), attr: {'name': 'pauid' + $parents[0].id(), 'data-id' : id()}" />	
						<label hidden><textarea data-bind="text: title, attr: {'name': 'answer' + $parents[0].id(), 'data-id' : id()}" ></textarea>${form.getMessage("label.Title")}</label>
						<label hidden><textarea data-bind="text: originalTitle, attr: {'name': 'originalAnswer' + $parent.id(), 'data-id' : id()}" ></textarea>${form.getMessage("label.OriginalAnswer")}</label>
					<!-- /ko -->
				</div>
			<!-- /ko -->
			</div>
			
		<!-- /ko -->
	</div>
</div>
