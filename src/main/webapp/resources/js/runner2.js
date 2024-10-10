Chart.defaults.global.plugins.colorschemes.scheme = 'tableau.Tableau10';

function getElementViewModel(element, foreditor)
{
	if (element.hasOwnProperty("isViewModel") && element.isViewModel)
	{
		return element;
	}

	var viewModel;
	
	switch (element.type)
	{
		case 'Section':
			viewModel = newSectionViewModel(element);
			break;
		case 'GalleryQuestion':
			viewModel = newGalleryViewModel(element);
			break;
		case 'Text':
			viewModel = newTextViewModel(element);
			break;
		case 'Image':
			viewModel = newImageViewModel(element);
			break;
		case 'Ruler':
			viewModel = newRulerViewModel(element);
			break;
		case 'FreeTextQuestion':
			viewModel = newFreeTextViewModel(element);
			break;
		case 'RegExQuestion':
			viewModel = newRegExViewModel(element);
			break;
		case 'FormulaQuestion':
			viewModel = newFormulaViewModel(element);
			break;
		case 'SingleChoiceQuestion':
			viewModel = newSingleChoiceViewModel(element);
			break;
		case 'MultipleChoiceQuestion':
			viewModel = newMultipleChoiceViewModel(element);
			break;
		case 'RankingQuestion':
			viewModel = newRankingViewModel(element);
			break;
		case 'NumberQuestion':
			viewModel = newNumberViewModel(element);
			break;
		case 'DateQuestion':
			viewModel = newDateViewModel(element);
			break;
		case 'TimeQuestion':
			viewModel = newTimeViewModel(element);
			break;
		case 'EmailQuestion':
			viewModel = newEmailViewModel(element);
			break;
		case 'Matrix':
			viewModel = newMatrixViewModel(element);
			break;
		case 'Table':
			viewModel = newTableViewModel(element);
			break;
		case 'Confirmation':
			viewModel = newConfirmationViewModel(element);
			break;
		case 'RatingQuestion':
			viewModel = newRatingViewModel(element);
			break;
		case 'Upload':
			viewModel = newUploadViewModel(element);
			break;
		case 'Download':
			viewModel = newDownloadViewModel(element);
			break;
		case 'ComplexTable':
			viewModel = newComplexTableViewModel(element, foreditor);
			break;
	} 	
		
	viewModel.foreditor = foreditor;
	return viewModel;
}

function addElement(element, foreditor, forskin)
{
	var id;
	var uniqueId;
	if (element.hasOwnProperty("isViewModel") && element.isViewModel)
	{
		id = element.id()
		uniqueId = element.uniqueId();
	} else {
		id = element.id;
		uniqueId = element.uniqueId;
	}
	var container = $(".emptyelement[data-id=" + id + "]");
	$(container).removeClass("emptyelement");
	$(container).find("img").remove();
	
	addElementToContainer(element, container, foreditor, forskin);
	
	if ($(container).hasClass("matrixitem"))
	{
		$(container).find(".matrix-question.untriggered").each(function(){
			if (isTriggered(this, true))
			{
				$(this).removeClass("untriggered").show();
			}
		});
	}
	
	var validation = getValidationMessageByQuestion(uniqueId);
	if (validation.length > 0)
	{
		$(container).append('<div style="color: #f00" tabindex="0" class="validation-error-server" aria-live="polite">' + validation + '</div>');
		$(container).find(".validation-error-server").first().focus();
	}
	
	if (!foreditor && doAnswersExist())
	{       
 		checkTriggersAfterLoad(container);
 	 		                
 	 	//dependent matrix rows
 	 	$(container).find(".matrix-question.untriggered").each(function(){
 	 		checkTriggersAfterLoad(this);
 	 	});     
 	 }
	
	return container;
}

function checkTriggersAfterLoad(container)
{
	var dtriggers = $(container).attr("data-triggers");
	if (typeof dtriggers !== typeof undefined && dtriggers !== false && dtriggers.length > 0) {
		var triggers = dtriggers.split(";")
		for (var i = 0; i < triggers.length; i++) {
			if (triggers[i].length > 0)
			{
				if (triggers[i].indexOf("|") > -1) {
					//matrix cell
					checkDependenciesAsync($("[data-cellid='" + triggers[i] + "']")[0]);
				} else {
					//radio/checkbox/listbox/selectbox
					checkDependenciesAsync($("#" + triggers[i])[0]);
				}
			}
		}
	}
}

function addDelphiClassToContainerIfNeeded(element, container) {
	if ((element.hasOwnProperty("isViewModel") && element.isViewModel)) {
		if (element.isDelphiQuestion()) {
			$(container).addClass("delphi");
		}
	} else {
		if (element.isDelphiQuestion) {
			$(container).addClass("delphi");
		}
	}
}

var modelsForDelphiQuestions = [];
var lastFocusedContainer = null;

function addElementToContainer(element, container, foreditor, forskin) {
	addDelphiClassToContainerIfNeeded(element, container);

	var viewModel = getElementViewModel(element, foreditor);

	viewModel.ismobile = false;
	viewModel.isresponsive = false;
	viewModel.istablet = false;
	
	try {
	
		if (isresponsive)
		{
			var isMobile = window.matchMedia("only screen and (max-width: 760px)");	
			viewModel.ismobile = isMobile.matches;
			
			var isResponsive = window.matchMedia("only screen and (max-width: 1000px)");	
			viewModel.isresponsive = isResponsive.matches;
			
			viewModel.istablet = isResponsive.matches && !isMobile.matches;
		}
	
	} catch (e) {}

	if (viewModel.type == 'Section') {
		$(container).addClass("sectionitem");
		var s = $("#section-template").clone().attr("id", "");
		$(container).append(s);
	} else if (viewModel.type == 'Text') {
		$(container).addClass("textitem");
		var s = $("#text-template").clone().attr("id", "");
		$(container).append(s);
	} else if (viewModel.type == 'Image') {
		$(container).addClass("imageitem");
		var s = $("#image-template").clone().attr("id", "");
		$(container).append(s);
	} else if (viewModel.type == 'Ruler') {
		$(container).addClass("ruleritem");
		var s = $("#ruler-template").clone().attr("id", "");
		$(container).append(s);
	} else if (viewModel.type == 'FreeTextQuestion' || viewModel.type == 'RegExQuestion') {
		if (viewModel.type == 'RegExQuestion') {
			$(container).addClass("regexitem forprogress");
			modelsForNumber[modelsForNumber.length] = viewModel;
		} else {
			$(container).addClass("freetextitem forprogress");
		}

		if (viewModel.isPassword()) {
			var s = $("#password-template").clone().attr("id", "");
			$(container).append(s);
		} else {
			var s = $("#freetext-template").clone().attr("id", "");
			$(container).append(s);
		}
	} else if (viewModel.type == 'FormulaQuestion') {
		$(container).addClass("formulaitem");
		var s = $("#formula-template").clone().attr("id", "");
		$(container.append(s));
		modelsForFormula[modelsForFormula.length] = viewModel;
	} else if (viewModel.type == 'NumberQuestion') {
		$(container).addClass("numberitem forprogress");
		var s = $("#number-template").clone().attr("id", "");
		$(container.append(s));
		modelsForNumber[modelsForNumber.length] = viewModel;
	} else if (viewModel.type == 'SingleChoiceQuestion') {
		$(container).addClass("singlechoiceitem forprogress");
		var s = $("#single-choice-template").clone().attr("id", "");
		$(container).append(s);
	} else if (viewModel.type == 'MultipleChoiceQuestion') {
		$(container).addClass("multiplechoiceitem forprogress");
		var s = $("#multiple-choice-template").clone().attr("id", "");
		$(container).append(s);
	} else if (viewModel.type == 'RankingQuestion') {
		$(container).addClass("rankingitem forprogress");
		var s = $("#ranking-question-template").clone().attr("id", "");
		$(container).append(s);
	} else if (viewModel.type == 'DateQuestion') {
		$(container).addClass("dateitem forprogress");
		var s = $("#date-template").clone().attr("id", "");
		$(container.append(s));
	} else if (viewModel.type == 'TimeQuestion') {
		$(container).addClass("timeitem forprogress");
		var s = $("#time-template").clone().attr("id", "");
		$(container.append(s));
	} else if (viewModel.type == 'EmailQuestion') {
		$(container).addClass("emailitem forprogress");
		var s = $("#email-template").clone().attr("id", "");
		$(container.append(s));
	} else if (viewModel.type == 'Matrix') {
		$(container).addClass("matrixitem forprogress");
		var s = $("#matrix-template").clone().attr("id", "");
		$(container.append(s));
	} else if (viewModel.type == 'Table') {
		$(container).addClass("mytableitem forprogress");
		var s = $("#table-template").clone().attr("id", "");
		$(container.append(s));
	} else if (viewModel.type == 'Upload') {
		$(container).addClass("uploaditem forprogress");
		var s = $("#upload-template").clone().attr("id", "");
		$(container.append(s));
	} else if (viewModel.type == 'Download') {
		$(container).addClass("downloaditem");
		var s = $("#download-template").clone().attr("id", "");
		$(container.append(s));
	} else if (viewModel.type == 'GalleryQuestion') {
		$(container).addClass("galleryitem");
		var s = $("#gallery-template").clone().attr("id", "");
		$(container.append(s));
	} else if (viewModel.type == 'Confirmation') {
		$(container).addClass("confirmationitem forprogress");
		var s = $("#confirmation-template").clone().attr("id", "");
		$(container.append(s));
	} else if (viewModel.type == 'RatingQuestion') {
		$(container).addClass("ratingitem forprogress");
		var s = $("#rating-template").clone().attr("id", "");
		$(container.append(s));
	} else if (viewModel.type == 'ComplexTable') {
		$(container).addClass("complextableitem");
		var s = $("#complextable-template").clone().attr("id", "");
		//forprogress is set in elementtemplates .innercell[data-bind]
		$(container.append(s));
	}

	if (isdelphi) {
		var d = $("#delphi-template").clone().attr("id", "");
		$(container).append(d);
	}
	
	ko.applyBindings(viewModel, $(container)[0]);

	if ((viewModel.type == 'Upload') || (viewModel.isDelphiQuestion())) {
		const maxFileSizeBytes = (viewModel.isDelphiQuestion()) ? (1*1024*1024) : (viewModel.maxFileSize());
		$(container).find(".file-uploader").each(function() {
			createUploader(this, maxFileSizeBytes);
		});
		
		$(container).find(".qq-upload-button").addClass("btn btn-default").removeClass("qq-upload-button");
		$(container).find(".qq-upload-drop-area").css("margin-left", "-1000px");
		$(container).find(".qq-upload-list").hide();
		$(container).find("input[type=file]").attr("aria-label", infolabeluploadbutton);
	} 
	
	if (element.type == 'DateQuestion') {
		$(container).find(".datepicker").each(function(){			
			createDatePicker(this);						
		});
	} else if (element.type == 'Confirmation') {
		if (getValueByQuestion(viewModel.uniqueId()) == 'on')
		{
			$(container).find("input").first().prop("checked", "checked");
		}
	} else if (viewModel.type == 'GalleryQuestion' && !viewModel.foreditor) {
		if (viewModel.ismobile)
		{
			viewModel.columns(1);
		} else if (viewModel.istablet)
		{
			if (viewModel.columns() > 2) {
				viewModel.columns(2);
			}
		}
	}
	
	$(container).find(".matrixtable").each(function(){
		var matrix = this;
		$(this).find("input").click(function(){
			$(matrix).addClass("clicked");
		});
		
		$(this).hover(function() {
		}, function() {
			if ($(this).hasClass("clicked")) {
				validateInput($(this).parent(),true);
				$(this).removeClass("clicked");
			}
		});
		
		if (foreditor && viewModel.tableType() == 2)
		{
			$(matrix).find("tr").first().find("th").each(function(index){	
				var cell = this;
				$(this).resizable({
					handles: "e",
					start: function ( event, ui) { $(cell).attr("data-originalwidth", $(cell).width())},
					stop: function( event, ui ) {
						_undoProcessor.addUndoStep(["CELLWIDTH", cell, $(cell).attr("data-originalwidth"), $(cell).width()]);
					} 
				});										
			});
		}
	});
	
	$(container).find(".tabletable").each(function(){
		var table = this;
		if (foreditor && viewModel.tableType() == 2)
		{
			$(table).find("tr").first().find("th").each(function(index){
				var cell = this;
				$(this).resizable({
					handles: "e",
					start: function ( event, ui) { $(cell).attr("data-originalwidth", $(cell).width())},
					stop: function( event, ui ) {
						_undoProcessor.addUndoStep(["CELLWIDTH", cell, $(cell).attr("data-originalwidth"), $(cell).width()]);
					} 
				});										
			});
		}
	});
	
	$(container).find(".answer-columns").each(function(){
		var cols = this;
		$(this).find("input").click(function(){
			$(cols).addClass("clicked");
		});
		$(this).find("a").click(function(){
			$(cols).addClass("clicked");
		});
		
		$(this).hover(function() {
		}, function() {
			if ($(this).hasClass("clicked")) {
						validateInput($(this).parent(),true);
						$(this).removeClass("clicked");
					}
		});
	});
	
	$(container).focusin(function() {
		if (lastFocusedContainer == null || $(container).attr("data-id") != lastFocusedContainer.attr("data-id")) {
			if (lastFocusedContainer != null) {
				validateInput(lastFocusedContainer,true);
			}
			lastFocusedContainer = $(container);
		}
	})
	
	$(container).find(".confirmationelement").each(function(){
		var cols = this;
		$(this).find("input").click(function(){
			$(cols).addClass("clicked");
		});
		$(this).find("a").click(function(){
			$(cols).addClass("clicked");
		});
		
		$(this).hover(function() {
		}, function() {
			if ($(this).hasClass("clicked")) {
						validateInput($(this).parent(),true);
						$(this).removeClass("clicked");
					}
		});		
	});
	
	initModals($(container).find(".modal-dialog").first());

	$(container).find(".rankingitem-list-container").each(function() {
		const viewmodel = ko.dataFor(this);
		viewmodel.initOn(this);
	});
	
	$(container).find(".expand").TextAreaExpander();
	
	$(container).find("input, textarea, select").change(function() {
		  lastEditDate = new Date();
	});	
	
	$(container).find("select.single-choice").not(".complex").prepend("<option value=''></option>");
				
	$(container).find(".tabletable").find("textarea").each(function(){
		var height = $(this).parent().height();
			if (height < 35) height = 35;
		$(this).height(height);
	});
	
	$(container).find(".ratingquestion").each(function(){
		var val = $(this).find("input").first().val();
		if (val != null && val.length > 0)
		{
			var pos = parseInt(val);
			if (pos > 0)
			{
				updateRatingIcons(pos-1, $(this));
			}
		}
	});
	
	if (foreditor)
	{
		$(container).find("textarea, input").not(":hidden").attr("disabled", "disabled");
		$(container).find("select").click(function(event){
			event.stopImmediatePropagation();
			event.preventDefault();
		}).change(function(event){
			propertiesFromSelect(this, event)
			this.value = ""
		});
		$(container).find("a").removeAttr("href").removeAttr("onkeypress").removeAttr("onclick");
		
		if (viewModel.locked())
		{
			$(container).addClass("locked");
		}
	}
	
	$(container).find(".freetext").each(function(){
		countChar(this);

		$(this).bind('paste', function (event) {
			var _this = this;
			// Short pause to wait for paste to complete
			setTimeout(function () {
				countChar(_this);
			}, 100);
		});
	});

	$(container).find(".sliderbox").each(function () {
		initSlider(this, foreditor, viewModel);
	});

	$(container).find('.explanation-editor').each(function () {
		$(this).tinymce(explanationEditorConfig);
	});

	if (isdelphi && !foreditor && !forskin && viewModel.isDelphiQuestion()) {
		modelsForDelphiQuestions[viewModel.uniqueId()] = viewModel;
		var surveyElement = $(container).closest(".survey-element");
		if (surveyElement) {
			loadGraphData(surveyElement);
			loadTableData($(surveyElement).attr("data-uid"), viewModel);

			$(surveyElement).find(".likert-div.median, .slider-div.median").each(function () {
				loadMedianData(surveyElement, viewModel);
			});
		}
	}
	
	if (isdelphi && !foreditor && !forskin && !viewModel.isDelphiQuestion()) {
		if ($(container).hasClass("dependent") && $(container).hasClass("freetextitem")) {
			var triggers = $(container).attr("data-triggers").split(";");
			
			if (triggers.length == 2)
			{
				//radio or checkbox
				var triggeringElement = $(".trigger[id='" + triggers[0] + "']");

				//select
				if (triggeringElement.length == 0) {
					triggeringElement = $(".trigger[id='trigger" + triggers[0] + "']");
				}
				
				//list
				if (triggeringElement.length == 0) {
					triggeringElement = $(".trigger[data-id='" + triggers[0] + "']");
				}
				
				if (triggeringElement.length == 1 && $(triggeringElement).closest(".delphi")) {
					//move this question inside the delphi element that triggers it
					var delphi = $(triggeringElement).closest(".delphi");
					delphi.find(".delphichildren").append(container);
				}
			}
		}		
	}

	return viewModel;
}

var modelsForSlider = [];
var modelsForFormula = [];
var modelsForNumber = [];
var modelsForRegEx = [];

function initSlider(input, foreditor, viewModel)
{
	try {
		$(input).bootstrapSlider().bootstrapSlider('destroy');
	} catch (e) {
		//ignore
	}
		
	$(input).bootstrapSlider({
		tooltip: 'always',
		ticks_labels: viewModel.labels(),
		enabled: !foreditor
	});	
	
	modelsForSlider[viewModel.uniqueId()] = viewModel;
}

function getWidth(widths, index) {
	if (widths != null) {
		var w = widths.split(";")
		return w[index] + "px";
	}

	return "50px";
}

function delphiPrefill(editorElement) {
	var answerSetId = $('#IdAnswerSet').val();
	if (!answerSetId) {
		return; // Cannot prefill when answers have not been submitted yet.
	}
	// The editor element needs to be retrieved again. Otherwise, closest() will return no elements.
	var surveyElement = $('#' + editorElement[0].id).closest('.survey-element');
	var questionUid =  $(surveyElement).attr("data-uid");
	var data = {
		answerSetId: answerSetId,
		questionUid: questionUid
	};
	$.ajax({
		url: contextpath + '/runner/delphiGet',
		data: data,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfheader, csrftoken);
		},
		error: function(message)
		{
			showAjaxError(message.status);
			$('#' + editorElement[0].id).closest(".explanation-section").show();
			surveyElement.find(".explanation-file-upload-section").show();
		},
		success: function(currentExplanationText, textStatus)
		{
			if (textStatus === "nocontent") {
				return;
			}
			
			if (currentExplanationText) {
				editorElement[0].setContent(currentExplanationText.text);
				var uploaderElement = surveyElement.find(".explanation-file-upload-section").children(".file-uploader").first();
				var updateinfo = {"success":true, "files":currentExplanationText.fileList, "wrongextension":false};
				updateFileList(uploaderElement, updateinfo);
				disableDelphiSaveButtons(surveyElement);
			}
			$('#' + editorElement[0].id).closest(".explanation-section").show();
			surveyElement.find(".explanation-file-upload-section").show();
			
			var viewModel = modelsForDelphiQuestions[questionUid];			
			viewModel.changedForMedian(currentExplanationText && currentExplanationText.changedForMedian);
		}
	});
}

function chartLabelCallback(value, index, values) {
	return value.length > 15 ? value.substring(0, 10) + "..." : value;
}

/**
 * Labels may be returned with some HTML code. This method returns the text content without any markup or an empty string.
 * @param {string} value
 */
function normalizeLabel(value) {
	var parser = new DOMParser();
	var doc = parser.parseFromString(value, "text/html");
	return doc.body.textContent || "";
}

/**
 * Tries to nicely split a label into multiple lines for Chart.js
 * @param value
 * @param {number} lineLength - Maximum number of characters per line
 * @returns {string | string[]}
 */
function wrapLabel(value, lineLength) {
	// try to coerce value into a string
	value = value == undefined ? "" : value.toString();

	// split by whitespace
	var words = value.trim().split(/\s+/g);

	if (words.length === 0) {
		// only whitespace
		return "";
	}

	var result = [];
	var currentLine = "";
	var i = 0;

	while (true) {
		var word = words[i];

		if (i >= words.length) {
			// out-of-bounds, add current line to result (if not empty) and stop
			if (currentLine) {
				result.push(currentLine);
			}

			break;
		}

		if (!currentLine) {
			if (word.length > lineLength) {
				// word is longer than allowed length => split word in between and create new line
				result.push(word.substr(0, lineLength));

				// remove first lineLength characters in word and try again
				words[i] = word.substr(lineLength);
				continue;
			}

			// set current line to current word and increase index
			currentLine = word;
			i++;
			continue;
		}

		if (currentLine.length + 1 + word.length <= lineLength) {
			// word fits in current line, separated by space character
			currentLine += " " + word;
			i++;
			continue;
		}

		// word does not fit into current line anymore => retry on current word with new line (index not increased)
		result.push(currentLine);
		currentLine = "";
	}

	if (result.length === 1) {
		// only one line => return as string
		return result[0];
	}

	// multiple lines => return as array
	return result;
}

function wrapChartLabelCallback(value, index, values) {
	return wrapLabel(value, 20);
}

function addStatisticsToAnswerText(div, result) {
	const remove = !result; // cast to boolean
	var elementWrapper = $(div).closest(".elementwrapper");
	var surveyElement = elementWrapper.find(".survey-element");

	var viewModel = ko.dataFor(surveyElement[0]);
	if (undefined === viewModel ) {
		return;
	}
	var viewModelType = viewModel.type;
	if (false === remove) {
		var questionType = result["questionType"];
		if (!viewModelType.startsWith(questionType)) {
			return;
		}
	}
	if (["SingleChoiceQuestion", "MultipleChoiceQuestion"].includes(viewModelType)) {
		var possibleAnswersArray = viewModel.possibleAnswers;
		if (undefined === possibleAnswersArray) {
			return;
		}
		var len = possibleAnswersArray().length;
		if (false === remove) {
			for (var i = 0; (result.data.length > i) && (len > i); ++i) {
				possibleAnswersArray()[i].delphiAnswerCount(result.data[i].value);
				possibleAnswersArray()[i].useSavedDisplayMode(true);
			}
		} else {
			for (var j = 0; len > j; ++j) {
				var origtitle = possibleAnswersArray()[j].originalTitle();
				possibleAnswersArray()[j].title(origtitle);
			}
		}
	}
	if (["NumberQuestion"].includes(viewModelType)) {
		if (!["Slider"].includes(viewModel.display())) {
			return;
		}
		var sliderbox = elementWrapper.find(".sliderbox");
		if (1 != sliderbox.size()) {
			return;
		}
		var painttooltipcallback = function() {};
		var bootstrapSlider = viewModel.getBootstrapSlider(sliderbox);
		if (false === remove) {
			var tooltipinner = elementWrapper.find("div.tooltip-inner");
			var map = {};
			result.data.forEach((entry) => map[entry.label] = entry.value);
			painttooltipcallback = function() {
				var votes = 0;
				var sliderValue = bootstrapSlider.bootstrapSlider("getValue");
				if (sliderValue in map) {
					votes = map[sliderValue];
				}
				tooltipinner.html(sliderValue+' <span class="slidertooltipdelphivotes">('+votes+')</span>');
			}
		}
		bootstrapSlider.bootstrapSlider("relayout");
		painttooltipcallback(); // repaint now
		bootstrapSlider.on("slide slideStart slideStop change", painttooltipcallback);
		var sliderhandle = elementWrapper.find("div.slider-handle");
		sliderhandle.on('mousedown', function(event) {
			requestAnimationFrame(function() { // when user presses mouse button without moving, tooltip is updated by slider
				painttooltipcallback(); // no event generated, but after that we need to repaint
			});
		});
	}
}

function addChart(div, chart) {
	var elementWrapper = $(div).closest(".elementwrapper");

	$(elementWrapper).find(".delphi-chart").remove();
	$(elementWrapper).find(".chart-wrapper__chart-container").show().append("<canvas class='delphi-chart'></canvas>");

	$(elementWrapper).find(".chart-wrapper").show();

	new Chart($(elementWrapper).find(".delphi-chart")[0].getContext('2d'), chart);
	
	$(elementWrapper).find(".chart-wrapper-loader").hide();
}

function addChartModal(_, chart) {
	var modal = $("#delphi-chart-modal");
	$(modal).find("canvas").remove();
	$('#wordcloudmodal').remove();
	$(modal).find(".delphi-chart-modal__chart-container").show().append("<canvas></canvas>");
	new Chart($(modal).find("canvas")[0].getContext('2d'), chart);
	showModalDialog($(modal), callerAddChartModal);
}

function addChartModalStartPage(_, chart) {
	var modal = $("#delphi-chart-modal-start-page");
	$(modal).find("canvas").remove();
	$('#wordcloudmodal').remove();
	$(modal).find(".delphi-chart-modal__chart-container").show().append("<canvas></canvas>");
	new Chart($(modal).find("canvas")[0].getContext('2d'), chart);
	showModalDialog($(modal), callerAddChartModalStartPage);
}

function addStructureChart(div, chart) {
	new Chart($(div).find("canvas")[0].getContext('2d'), chart);

	$(div).find('.delphi-chart-expand').show();
	$(div).find('.no-graph-image').hide();
}

function loadGraphData(div) {
	var surveyId = $('#survey\\.id').val();
	var questionuid = $(div).attr("data-uid");
	var languagecode = $('#language\\.code').val();
	var uniquecode = $('#uniqueCode').val();

	// Briefly show the wrapper to get the real width of the chart container.
	const chartWrapper = $(div).closest('.elementwrapper').find('.chart-wrapper');
	$(chartWrapper).show();
	const canvasContainer = $(div).find('.chart-wrapper__chart-container')[0];
	$(canvasContainer).show();
	const canvasContainerWidth = canvasContainer.clientWidth;
	$(canvasContainer).hide();
	$(chartWrapper).hide();

	loadGraphDataInnerForRunner(div, surveyId, questionuid, languagecode, uniquecode, addChart, true, false, false, canvasContainerWidth);
}

var callerAddChartModal = null;

function loadGraphDataModal(link) {
	var surveyElement = $(link).closest(".survey-element");
	var surveyId = $('#survey\\.id').val();
	var questionuid = $(surveyElement).attr("data-uid");
	var languagecode = $('#language\\.code').val();
	var uniquecode = $('#uniqueCode').val();

	// Briefly show the modal to get the real width of the chart container.
	const modal = $('#delphi-chart-modal');
	showModalDialog(modal, link);
	
	const canvasContainer = $(modal).find('.delphi-chart-modal__chart-container')[0];
	$(canvasContainer).show();
	const canvasContainerWidth = canvasContainer.clientWidth;
	$(canvasContainer).hide();
	$(modal).modal('hide');

	callerAddChartModal = link;
	loadGraphDataInnerForRunner(surveyElement, surveyId, questionuid, languagecode, uniquecode, addChartModal, false, true, false, canvasContainerWidth);
}

function scrollClosestDelphiTableIntoView(element) {
	let delphiTable = $(element).closest(".delphi-table")[0];
	let rect = delphiTable.getBoundingClientRect();
	window.scrollBy(0, rect.top - 120); // offset for header
}

function firstDelphiTablePage(element) {
	var uid = getDelphiQuestionUid(element);
	var viewModel = getDelphiViewModel(element);

	viewModel.delphiTableOffset(0);
	scrollClosestDelphiTableIntoView(element);
	loadTableData(uid, viewModel);
}

function lastDelphiTablePage(element) {
	var uid = getDelphiQuestionUid(element);
	var viewModel = getDelphiViewModel(element);

	var overflow = viewModel.delphiTableTotalEntries() % viewModel.delphiTableLimit();

	if (overflow === 0) {
		overflow = viewModel.delphiTableLimit();
	}

	var newOffset = viewModel.delphiTableTotalEntries() - overflow;
	viewModel.delphiTableOffset(newOffset);
	scrollClosestDelphiTableIntoView(element);
	loadTableData(uid, viewModel);
}

function previousDelphiTablePage(element) {
	var uid = getDelphiQuestionUid(element);
	var viewModel = getDelphiViewModel(element);

	viewModel.delphiTableOffset(Math.max(viewModel.delphiTableOffset() - viewModel.delphiTableLimit(), 0));
	scrollClosestDelphiTableIntoView(element);
	loadTableData(uid, viewModel);
}

function nextDelphiTablePage(element) {
	var uid = getDelphiQuestionUid(element);
	var viewModel = getDelphiViewModel(element);
	
	var newOffset = viewModel.delphiTableOffset() + viewModel.delphiTableLimit();

	if (newOffset < viewModel.delphiTableTotalEntries()) {
		viewModel.delphiTableOffset(newOffset);
		scrollClosestDelphiTableIntoView(element);
		loadTableData(uid, viewModel);
	}
}

function getDelphiViewModel(element)
{
	if ($(element).closest(".modal-body").length > 0)
	{
		return answersTableViewModel;
	} else {
		var surveyElement = $(element).closest(".survey-element");
		var uid = $(surveyElement).attr("data-uid");
		return modelsForDelphiQuestions[uid];
	}
}

function getDelphiQuestionUid(element)
{
	if ($(element).closest(".modal-body").length > 0)
	{
		return currentQuestionUidInModal;
	} else {
		return $(element).closest(".survey-element").attr("data-uid");
	}
}

function sortDelphiTable(element, direction) {
	$('[data-toggle="tooltip"]').tooltip("hide");
	var surveyElement = $(element).closest(".survey-element");
	var uid = $(surveyElement).attr("data-uid");
	var viewModel = modelsForDelphiQuestions[uid];
	
	if (viewModel == null && typeof answersTableViewModel !== 'undefined') {
		viewModel = answersTableViewModel;
		uid = currentQuestionUidInModal;
	}

	viewModel.delphiTableOrder(direction);
	viewModel.delphiTableOffset(0);
	loadTableData(uid, viewModel);
}

function loadTableData(questionUid, viewModel) {

	const surveyId = $('#survey\\.id').val();
	const languageCode = $('#language\\.code').val();
	const uniqueCode = $('#uniqueCode').val();
	loadTableDataInner(languageCode, questionUid, surveyId, uniqueCode, viewModel);
}

function hideCommentAndReplyForms() {
	$('.delphi-comment__cancel').each(function() {
		if ($(this).is(":visible")) {
			$(this).click();
		}
	});
}

function loadTableDataInner(languageCode, questionUid, surveyId, uniqueCode, viewModel) {
	const orderBy = viewModel.delphiTableOrder();
	const offset = viewModel.delphiTableOffset();
	const limit = viewModel.delphiTableLimit();

	const data = "surveyid=" + surveyId + "&questionuid=" + questionUid + "&languagecode=" + languageCode
		+ "&uniquecode=" + uniqueCode + "&orderby=" + orderBy + "&offset=" + offset + "&limit=" + limit;

	$.ajax({
		type: "GET",
		url: contextpath + "/runner/delphiTable",
		data: data,
		beforeSend: function (xhr) {
			viewModel.delphiTableLoading(true);
			xhr.setRequestHeader(csrfheader, csrftoken);
		},
		error: function (data) {
			showAjaxError(data.status)
		},
		complete: function () {
			viewModel.delphiTableLoading(false);
		},
		success: function (result, textStatus) {
			viewModel.delphiTableEntries.removeAll();
			viewModel.delphiTableNewComments(false);
			
			if (textStatus === "nocontent") {
				return;
			}

			viewModel.delphiTableQuestionType(result.questionType);
			viewModel.showExplanationBox(result.showExplanationBox);
			
			for (let i = 0; i < result.entries.length; i++) {
				const entry = result.entries[i];
				
				entry.uid = getNewId(); // create uuid
				
				for (let j = 0; j < entry.answers.length; j++) {
					entry.answers[j].uid = getNewId(); // create uuid
				}
				
				entry.showCommentArea = function() {
					hideCommentAndReplyForms();
					this.isCommentFormVisible(true);
					this.hasCommentFieldFocus(true);
				}

				entry.isCommentFormVisible = ko.observable(false);
				entry.hasCommentFieldFocus = ko.observable(false);

				for (let j = 0; j < entry.comments.length; j++) {
					entry.comments[j].isReplyFormVisible = ko.observable(false);
					entry.comments[j].hasReplyFieldFocus = ko.observable(false);
					entry.comments[j].isChangedCommentFormVisible = ko.observable(false);
					entry.comments[j].hasChangedCommentFieldFocus = ko.observable(false);
					entry.comments[j].changedComment = ko.observable('');

					entry.comments[j].editComment = function() {
						hideCommentAndReplyForms();
						entry.comments[j].changedComment(entry.comments[j].text);
						entry.comments[j].isChangedCommentFormVisible(true);
						entry.comments[j].hasChangedCommentFieldFocus(true);
					}
					
					entry.comments[j].showCommentArea = function() {
						hideCommentAndReplyForms();
						this.isReplyFormVisible(true);
						this.hasReplyFieldFocus(true);
					}

					for (let k = 0; k < entry.comments[j].replies.length; k++) {
						entry.comments[j].replies[k].isChangedReplyFormVisible = ko.observable(false);
						entry.comments[j].replies[k].hasChangedReplyFieldFocus = ko.observable(false);
						entry.comments[j].replies[k].changedReply = ko.observable('');

						entry.comments[j].replies[k].editReply = function() {
							hideCommentAndReplyForms();
							entry.comments[j].replies[k].changedReply(entry.comments[j].replies[k].text);
							entry.comments[j].replies[k].isChangedReplyFormVisible(true);
							entry.comments[j].replies[k].hasChangedReplyFieldFocus(true);
						}
					}
				}
				viewModel.delphiTableEntries.push(entry);
			}

			viewModel.delphiTableOffset(result.offset);
			viewModel.delphiTableTotalEntries(result.total);
			viewModel.delphiTableNewComments(result.hasNewComments);

			$('[data-toggle="tooltip"]').ApplyCustomTooltips();
			addTruncatedClassIfNeededForExplanationsAndDelphiCommentTexts(questionUid);
		}
	 });
}

function addTruncatedClassIfNeededForExplanationsAndDelphiCommentTexts(questionUid) {
	let textToBeTruncatedFields = $('[data-uid="' + questionUid + '"]').find('.text-to-be-truncated');
	if (textToBeTruncatedFields.length === 0) {
		// If no fields are found, the start page is probably shown, on which the first selector does not work.
		// Therefore, get all the ones that are shown.
		textToBeTruncatedFields = $('.text-to-be-truncated');
	}
	$(textToBeTruncatedFields).each(function() {
		if (this.scrollHeight > Math.round(this.getBoundingClientRect().height))
			this.classList.add('truncated');
	});
}

function loadMedianData(div, viewModel) {
	const surveyId = $('#survey\\.id').val();
	const questionUid = $(div).attr("data-uid");
	const uniqueCode = $('#uniqueCode').val();
	
	const data = "surveyid=" + surveyId + "&questionuid=" + questionUid + "&uniquecode=" + uniqueCode;

	$.ajax({
		type: "GET",
		url: contextpath + "/runner/delphiMedian",
		data: data,
		beforeSend: function (xhr) {
			xhr.setRequestHeader(csrfheader, csrftoken);
		},
		error: function () {
			showError("Not possible to retrieve median data");
		},
		success: function (result, textStatus) {
			
			if (textStatus === "nocontent") {
				return;
			}			
			
			viewModel.maxDistanceExceeded(result != undefined && result.maxDistanceExceeded);
			viewModel.median(result != undefined ? result.median : 0);
			
			$(div).find(".medianpa").removeClass("medianpa");
			
			if (viewModel.maxDistanceExceeded())
			{
				for (let i = 0; i < result.medianUids.length; i++) {				
					$('.answertext[data-pa-uid="' + result.medianUids[i] + '"]').closest(".likert-pa").addClass("medianpa");
				}
			}
		}
	 });		
}

function selectPageAndScrollToQuestionIfSet() {
	if (window.location.hash) {
		
		if (window.location.hash.indexOf("page") > -1) {
			return;
		}
		
		$('#delphi-hide-survey').show();
		
		//select correct page in case of multi-paging		
		if ($(".single-page").length > 1)
		{
			const elementAnchorId = location.hash.substr(1);
			const element = document.getElementById(elementAnchorId);	
			const p = $(element).closest(".single-page");
			page = parseInt(p.attr("id").substring(4));
			$(".single-page").hide();		
			$(p).show();
			checkPages();
		}
		
		setTimeout(checkMissingElementsAndScroll, 2000);
	}
}

function checkMissingElementsAndScroll()
{
	var loaders = $('.delphi-table').find(".loader:visible").length;
	
	if (loaders == 0) {
		loaders = $('.chart-wrapper-loader:visible').length;
	}
	
	if (loaders > 0) {
		setTimeout(checkMissingElementsAndScroll, 2000);
		return;
	}
	
	scrollToQuestionIfSet();
}

function scrollToQuestionIfSet() {
	const elementAnchorId = location.hash.substr(1);
	try {
		document.getElementById(elementAnchorId).scrollIntoView();
	} catch (error) {
		console.log(error);
	}
	$('#delphi-hide-survey').hide();
}

var delphiUpdateFinished = false;

const DELPHI_UPDATE_TYPE = {
	ONE_QUESTION: 1,
	ENTIRE_FORM: 2
};
Object.freeze(DELPHI_UPDATE_TYPE);

let currentDelphiUpdateType;
let currentDelphiUpdateContainer;

function delphiUpdate(div) {

	// check sessiontimeout
	if(checkSessionTimeout()) {
		showSessionError();
		window.setTimeout(() => {
			window.location.replace(window.location);
		}, 2000)
		return;
	}

	const message = $(div).find(".delphiupdatemessage").first();
	$(message).attr("class", "delphiupdatemessage");

	if (!$(div).hasClass("single-page")) {
		if (validateInput(div) == false) {
			return;
		}
	}

	if (isOneAnswerEmptyWhileItsExplanationIsNot(div)) {
		currentDelphiUpdateType = DELPHI_UPDATE_TYPE.ONE_QUESTION;
		currentDelphiUpdateContainer = div;
		showModalDialog($('.confirm-explanation-deletion-modal'), $(div).find("[data-type='delphisavebutton']").first());
		return;
	}

	delphiUpdateContinued(div);
}

function confirmExplanationDeletion() {

	hideModalDialog($('.confirm-explanation-deletion-modal'));
	if (currentDelphiUpdateType === DELPHI_UPDATE_TYPE.ONE_QUESTION) {
		delphiUpdateContinued(currentDelphiUpdateContainer, () => {
			$(currentDelphiUpdateContainer).find("textarea[name^='explanation']").val("");
			$(currentDelphiUpdateContainer).find(".uploaded-files").children().remove();
		});
	} else if (currentDelphiUpdateType === DELPHI_UPDATE_TYPE.ENTIRE_FORM) {
		validateInputAndSubmitRunnerContinued(currentDelphiUpdateContainer);
	}
}

const HAS_SHOWN_SURVEY_LINK = "hasShownSurveyLink";

function checkSessionTimeout(){
	var sessiontimeout = false;

	try{

		checkLocalBackup()

		$.ajax({type: "POST",
			url: contextpath + "/runner/checksession",
			async: false,
			beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
			error: function(result)
			{
				var s = result.statusText.toLowerCase();
				if (strStartsWith(s,"forbidden") || result.status == 403)
				{
					sessiontimeout = true;
				}
			},
			success: function(result)
			{
				//everything is ok
			}
		});

	} catch (err)
	{
		var stacktrace = err.stack || err.stacktrace || err;
		$("#btnSubmit").parent().append("<div id='exceptionlogdiv' class='validation-error'>" + varExceptionDuringSave + "&nbsp;" + stacktrace + "</div>");
	}

	return sessiontimeout;
}

function delphiUpdateContinued(div, successCallback) {

	const message = $(div).find(".delphiupdatemessage").first();

	var loader = $(div).find(".inline-loader").first();
	$(loader).show();
	
	var form = document.createElement("form");
	$(form).append($(div).clone());
	
	$(form).find("input[data-is-answered='false'].sliderbox").val('');
	
	var surveyId = $('#survey\\.id').val();
	$(form).append('<input type="hidden" name="surveyId" value="' + surveyId + '" />');
	var ansSetUniqueCode = $('#uniqueCode').val();
	$(form).append('<input type="hidden" name="ansSetUniqueCode" value="' + ansSetUniqueCode + '" />');
	var invitation = $('#invitation').val();
	$(form).append('<input type="hidden" name="invitation" value="' + invitation + '" />');
	var lang = $('#language\\.code').val();
	$(form).append('<input type="hidden" name="languageCode" value="' + lang + '" />');
	var id = $(div).attr("data-id");	
	var uid = $(div).attr("data-uid");	
	
	if ($(div).hasClass("single-page")) {
		//this can happen on page change
		var section = $(div).find(".survey-element.sectionitem").first();
		id = $(section).attr("data-id");
		uid = $(section).attr("data-uid");
	}
	
	$(form).append('<input type="hidden" name="questionId" value="' + id + '" />');
	$(form).append('<input type="hidden" name="questionUid" value="' + uid + '" />');

	//this is a workaround for a bug in jquery
	// see https://bugs.jquery.com/ticket/1294
	$(form).find("select").each(function () {
		var id = $(this).attr("id");
		$(this).val($(div).find("#" + id).first().val());
	});

	var data = $(form).serialize();

	$.ajax({
		type: "POST",
		url: contextpath + "/runner/delphiUpdate",
		data: data,
		beforeSend: function (xhr) {
			xhr.setRequestHeader(csrfheader, csrftoken);
		},
		error: function(data)
	    {
			$(message).html(data.responseJSON.message).addClass("update-error label label-danger");
			$(loader).hide();
		},
		success: function (data) {
			$(message).html(data.message).addClass("label label-success");
			disableDelphiSaveButtons(div);
			
			const uniqueCode = $("#uniqueCode").val();
			const key = HAS_SHOWN_SURVEY_LINK + uniqueCode;
			let dialogShown = false;
			if (localStorage.getItem(key) == null) {
				localStorage.setItem(key, "true");
				appendShowContributionLinkDialogToSidebar(div, data);
				dialogShown = true;
			}
			
			if (data.changedForMedian) {
				var viewModel = modelsForDelphiQuestions[uid];			
				viewModel.changedForMedian(true);
			}
			
			if (data.changeExplanationText) {
				var ed = tinyMCE.get("explanation" + id);		
				var old = ed.getContent();
				var text = labelnewexplanation + ":<br /><br/><br />" + labeloldexplanation + ":<br /><br /><span style='color: #999;'>" + old + "</span>";
				ed.setContent(text);
			}
			
			$(loader).hide();

			if ($(div).hasClass("single-page")) {
				$(div).find(".survey-element").not(".sectionitem").each(function () {
					updateDelphiElement(this, successCallback);
				})
			} else {
				updateDelphiElement(div, successCallback);
				if (!dialogShown) {
					if ($(div).find("button[data-type='delphireturntostart']").length > 0) {
						$(div).find("button[data-type='delphireturntostart']").focus();
					} else if ($(div).find("button[data-type='delphitonextquestion']:visible").length > 0) {
						$(div).find("button[data-type='delphitonextquestion']").focus();
					} else if ($('#btnNext').is(":visible")) {
						$('#btnNext').focus();
					} else {
						$('#btnSubmit').focus();
					}
				}
			}

			delphiUpdateFinished = true;
		}
	});
}

function appendShowContributionLinkDialogToSidebar(div, data) {
	if ($('#editYourContributionLink').length == 0) {	
		$("<br />").appendTo(".contact-and-pdf__delphi-section");
		$("<br />").appendTo(".contact-and-pdf__delphi-section");
		$('<a id="editYourContributionLink" href="javascript:;" onclick="showContributionLinkDialog(this)">' + labelEditYourContributionLater + '</a>')
			.appendTo(".contact-and-pdf__delphi-section");
			
		if ($(div).find("a[data-type='delphireturntostart']").length > 0) {
			showContributionLinkDialog($(div).find("button[data-type='delphireturntostart']"), data.link);
		} else {
			showContributionLinkDialog($(div).find("button[data-type='delphitonextquestion']"), data.link);
		}
	}
}

let targetToDocus = null;
function showContributionLinkDialog(caller, url) {
	if (!url) {
		const uniqueCode = $("#uniqueCode").val();
		url = serverPrefix + "editcontribution/" + uniqueCode;
	}
	const link = document.createElement("a");
	$(link).attr("href", url).html(url);
	$("#contribution-link-dialog__link").empty().append(link);
	showModalDialog($("#contribution-link-dialog"), caller);
	targetToDocus = $(caller);
}

function updateDelphiElement(element, successCallback) {
	var uid = $(element).attr("data-uid");
	if (!uid) {
		return;
	}

	var viewModel = modelsForDelphiQuestions[uid];
	if (!viewModel) {
		return;
	}

	loadGraphData(element);
	loadMedianData(element, viewModel)
	loadTableData(uid, viewModel);

	if (typeof successCallback === "function") {
		successCallback()
	}
}

function updateQuestionsOnNavigation(page) {
	if (isdelphi) {
		$(".delphiupdatemessage").attr("class","delphiupdatemessage").empty();
		var section = $("#page" + page);
		var found = $(section).find(".survey-element").is(function () {
			return $(this).hasClass("sectionitem") === false;
		});

		if (found) {
			delphiUpdate(section);
		}
	}
}

function saveDelphiCommentFromRunner(button, reply) {

	const td = $(button).closest("td");
	const questionUid = $(td).closest(".survey-element").attr("data-uid");
	const surveyId = $('#survey\\.id').val();
	const viewModel = modelsForDelphiQuestions[questionUid];

	const errorCallback = () => { showError("error"); }
	const successCallback = () => { loadTableData(questionUid, modelsForDelphiQuestions[questionUid]); }
	saveDelphiComment(button, viewModel, reply, questionUid, surveyId, errorCallback, successCallback);
}

function saveDelphiComment(button, viewModel, reply, questionUid, surveyId, errorCallback, successCallback) {

	hideCommentAndReplyForms();

	let text;
	if (reply) {
		text = $(button).closest(".delphi-comment__add-reply-form").find("textarea").val();
	} else {
		text = $(button).closest(".delphi-comment-add__form").find("textarea").val();
	}

	const td = $(button).closest("td");
	const answerSetId = $(td).attr("data-id");
	const answerSetUniqueCode = $("#uniqueCode").val();

	let data = "surveyid=" + surveyId + "&uniquecode=" + answerSetUniqueCode + "&answersetid=" + answerSetId
		+ "&questionuid=" + questionUid + "&text=" + encodeURIComponent(text);
	
	if (reply) {
		const parent = $(button).attr("data-parent");
		data += "&parent=" + parent;
	}
	
	$.ajax({type: "POST",
		url: contextpath + "/runner/delphiAddComment",
		data: data,
		beforeSend: function(xhr) {
			if (viewModel && viewModel.delphiTableLoading) {
				viewModel.delphiTableLoading(true);
			}
			xhr.setRequestHeader(csrfheader, csrftoken);
		},
		error: () => {
			if (viewModel && viewModel.delphiTableLoading) {
				viewModel.delphiTableLoading(false);
			}
			errorCallback();
		},
		success: () => {
			if (viewModel && viewModel.delphiTableLoading) {
				viewModel.delphiTableLoading(false);
			}
			successCallback();
		}
	 });
}

function saveChangedDelphiCommentFromRunner(button, isReply) {
	const questionUid = $(button).closest(".survey-element").attr("data-uid");
	const viewModel = modelsForDelphiQuestions[questionUid];

	const errorCallback = () => {
		showError("error");
	}
	const successCallback = () => {
		loadTableData(questionUid, viewModel);
	}

	saveChangedDelphiComment(button, viewModel, isReply, errorCallback, successCallback);
}

function saveChangedDelphiComment(button, viewModel, isReply, errorCallback, successCallback) {
	const actions = $(button).parent().parent().find(".delphi-comment__actions");
	$(actions).hide();
	hideCommentAndReplyForms();

	let commentId;
	if (isReply) {
		commentId = $(button).closest(".delphi-comment__reply").attr("data-id");
	} else {
		commentId = $(button).closest(".delphi-comment").attr("data-id");
	}

	const text = $(button).closest(".delphi-comment__change-form").find("textarea").val();
	const answerSetUniqueCode = $("#uniqueCode").val();

	$.ajax({
		type: "POST",
		url: contextpath + "/runner/editDelphiComment/" + encodeURIComponent(commentId),
		data: "text=" + encodeURIComponent(text) + "&uniqueCode=" + answerSetUniqueCode,
		beforeSend: function (xhr) {
			if (viewModel && viewModel.delphiTableLoading) {
				viewModel.delphiTableLoading(true);
			}
			xhr.setRequestHeader(csrfheader, csrftoken);
		},
		complete: function () {
			$(actions).show();
		},
		error: function() {
			if (viewModel && viewModel.delphiTableLoading) {
				viewModel.delphiTableLoading(false);
			}
			errorCallback();
		},
		success: function() {
			if (viewModel && viewModel.delphiTableLoading) {
				viewModel.delphiTableLoading(false);
			}
			successCallback();
		}
	});
}

function deleteDelphiComment(button, viewModel, isReply, errorCallback, successCallback) {
	const actions = $(button).parent().parent().find(".delphi-comment__actions");
	$(actions).hide();
	hideCommentAndReplyForms();

	let commentId;
	if (isReply) {
		commentId = $(button).closest(".delphi-comment__reply").attr("data-id");
	} else {
		commentId = $(button).closest(".delphi-comment").attr("data-id");
	}

	const answerSetUniqueCode = $("#uniqueCode").val();

	$.ajax({
		type: "POST",
		url: contextpath + "/runner/deleteDelphiComment/" + encodeURIComponent(commentId),
		data: "uniqueCode=" + answerSetUniqueCode + "&uniqueCode=" + answerSetUniqueCode,
		beforeSend: function (xhr) {
			if (viewModel && viewModel.delphiTableLoading) {
				viewModel.delphiTableLoading(true);
			}
			xhr.setRequestHeader(csrfheader, csrftoken);
		},
		complete: function () {
			$(actions).show();
		},
		error: function() {
			if (viewModel && viewModel.delphiTableLoading) {
				viewModel.delphiTableLoading(false);
			}
			errorCallback();
		},
		success: function() {
			if (viewModel && viewModel.delphiTableLoading) {
				viewModel.delphiTableLoading(false);
			}
			successCallback();
		}
	});
}

function likeDelphiCommentOrExplanationFromRunner(image, isExplanation) {
	var increaseLike = $(image).find(".likeImage").attr("id").startsWith("likeButtonDelphi");

	const questionUid = $(image).closest(".survey-element").attr("data-uid");
	const viewModel = modelsForDelphiQuestions[questionUid];

	const errorCallback = () => {
		showError("error");
	}
	const successCallback = () => {
		loadTableData(questionUid, viewModel);
	}

	if (isExplanation) {
		likeDelphiExplanation(image, viewModel, increaseLike, errorCallback, successCallback);
	} else {
		likeDelphiComment(image, viewModel, increaseLike, errorCallback, successCallback);
	}
}

function likeDelphiComment(image, viewModel, increaseLike, errorCallback, successCallback) {
	const actions = $(image).parent().parent().find(".delphi-comment__actions");

	const commentId = $(image).closest(".delphi-comment").attr("data-id");
	const commentUid = $(image).closest(".delphi-comment").attr("data-uid");
	const answerSetUniqueCode = $("#uniqueCode").val();

	if (commentUid === answerSetUniqueCode) {
		//cant like comments/ explanations of own answer sets
		showInfo(likeOwnCommentOrExplanation);
		return;
	}

	$.ajax({
		type: "POST",
		url: contextpath + "/runner/likeDelphiComment/" + encodeURIComponent(commentId),
		data: "increaseLike=" + encodeURIComponent(increaseLike) + "&uniqueCode=" + answerSetUniqueCode,
		beforeSend: function (xhr) {
			if (viewModel && viewModel.delphiTableLoading) {
				viewModel.delphiTableLoading(true);
			}
			xhr.setRequestHeader(csrfheader, csrftoken);
		},
		complete: function () {
			$(actions).show();
		},
		error: function() {
			if (viewModel && viewModel.delphiTableLoading) {
				viewModel.delphiTableLoading(false);
			}
			errorCallback();
		},
		success: function() {
			if (viewModel && viewModel.delphiTableLoading) {
				viewModel.delphiTableLoading(false);
			}
			successCallback();
		}
	});
}

function likeDelphiExplanation(image, viewModel, increaseLike, errorCallback, successCallback) {
	const explanation = $(image).closest("td");

	const explanationId = $(image).closest(".delphi-explanation").attr("data-id");
	const explanationUid = $(image).closest(".delphi-explanation").attr("data-uid");
	const answerSetUniqueCode = $("#uniqueCode").val();

	if (explanationUid === answerSetUniqueCode) {
		//cant like comments/ explanations of own answer sets
		showInfo(likeOwnCommentOrExplanation);
		return;
	}


	$.ajax({
		type: "POST",
		url: contextpath + "/runner/likeDelphiExplanation/" + encodeURIComponent(explanationId),
		data: "increaseLike=" + encodeURIComponent(increaseLike) + "&uniqueCode=" + answerSetUniqueCode,
		beforeSend: function (xhr) {
			if (viewModel && viewModel.delphiTableLoading) {
				viewModel.delphiTableLoading(true);
			}
			xhr.setRequestHeader(csrfheader, csrftoken);
		},
		complete: function () {
			$(explanation).show();
		},
		error: function() {
			if (viewModel && viewModel.delphiTableLoading) {
				viewModel.delphiTableLoading(false);
			}
			errorCallback();
		},
		success: function() {
			if (viewModel && viewModel.delphiTableLoading) {
				viewModel.delphiTableLoading(false);
			}
			successCallback();
		}
	});
}

function checkGoToDelphiStart(link)
{
	// check sessiontimeout
	if(checkSessionTimeout()) {
		showSessionError();
		window.setTimeout(() => {
			window.location.replace(window.location);
		}, 2000)
		return;
	}

	var button = $("a[data-type='delphisavebutton']:visible").not(".disabled").first();

	var ansSetId = $('#IdAnswerSet').val();
	var ansSetUniqueCode = $('#uniqueCode').val();

	var url;

	if (ansSetId == '' && !delphiUpdateFinished)
	{
		url = delphiStartPageUrl;
	} else {
		url = contextpath + "/editcontribution/" + ansSetUniqueCode;
	}

	if ($(button).length > 0) {
		$('#unsaveddelphichangesdialoglink').attr("href", url);
		showModalDialog($('#unsaveddelphichangesdialog'), button);
		return;
	}

	window.location = url;
}

function showResultsTable(button) {
	$(button).hide();
	const container = $(button).closest('.results-table-row');
	$(container).find('.results-table-row__link-hide').show();
	$(container).find('.delphi-table').show();
	const questionUid = $(button).closest(".survey-element").attr("data-uid");
	addTruncatedClassIfNeededForExplanationsAndDelphiCommentTexts(questionUid);
}

function hideResultsTable(button) {
	$(button).hide();
	const container = $(button).closest('.results-table-row');
	$(container).find('.results-table-row__link-show').show();
	$(container).find('.delphi-table').hide();
}

function openAskEmailToSendLinkDialog(button) {
	$(button).closest('.modal').modal('hide');
	showModalDialog($('#ask-email-dialog'), button);
}

function sendDelphiMailLink() {
	
	var mail = $("#delphiemail").val();
	
	if (mail.trim().length == 0 || !validateEmail(mail))
	{
		$("#ask-delphi-email-dialog-error").show();
		return;
	}
	
	$("#ask-delphi-email-dialog-error").hide();
	
	var answerSetUniqueCode = $('#uniqueCode').val();
	
	$.ajax({
		type: "POST",
		url: contextpath + "/runner/sendDelphiLink",
		data: "uniqueCode=" + answerSetUniqueCode + "&email=" + mail,
		beforeSend: function(xhr) { xhr.setRequestHeader(csrfheader, csrftoken); },
		error: function (data) {
			showAjaxError(data.status)
		},
		success: function(data)
	    {
			showSuccess(data);
			if (targetToDocus != null) {
				$(targetToDocus).focus();
			}
	    }
	});
	
	$('#ask-email-dialog').modal('hide');
}

function cancelDelphiMailLink() {
	hideModalDialog($('#ask-email-dialog'));
	if (targetToDocus != null) {
		$(targetToDocus).focus();
	}
}

function hideTooltipsOnEscape(e) {
	if ("Escape" === e.key) {
		$('[data-toggle="tooltip"]').tooltip("hide");
	}
}

function validateLastContainer() {
	if (lastFocusedContainer != null) {
		validateInput(lastFocusedContainer, true);
	}
}

(function () {
	$(document).keyup(hideTooltipsOnEscape);
})();
