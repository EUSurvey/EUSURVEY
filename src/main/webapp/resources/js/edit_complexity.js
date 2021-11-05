/*
 * Complexity vars
 */
var complexityScore = 0;
var surveySummary = {"nbSections": 0, "nbQuestions": 0, "nbDependencies":0, "nbDoubleDependencies":0, 
		"thresholdQuestionsReached":false, "thresholdSectionsReached":false, "thresholdDependenciesReached": false};
var complexitySettings = {
		"weightSectionItem": 1,
		"weightSimpleItem": 1,
		"weightSimpleQuestion": 1,
		"weightChoiceQuestion": 1,
		"weightGalleryQuestion": 5,
		"weightTableOrMatrixQuestion": 5,
		"weightTooManyRows": 10,
		"weightTooManyColumns": 10,
		"rowThreshold":10,
		"columnThreshold":10,
		"weightTooManyPossibleAnswers": 10,
		"possibleAnswersThreshold":10,
		"weightDependency": 5,
		"weightDoubleDependency": 15,
		"questionsThreshold": 50,
		"questionsThresholdScore": 5,
		"sectionThreshold": 5,
		"sectionThresholdScore": 5,
		"dependenciesThreshold": 10,
		"dependenciesThresholdScore": 10,
		"lowScore": 50,
		"mediumScore": 100,
		"highScore": 150,
		"criticalScore": 200,		
};

var tableAndMatrixDefinitions = {};
var choiceDefinitions = {};
var sourceDependeciesDefinitions = {};

function updateComplexityScore(type)
{
	var oldScore = complexityScore;
	
	if( type == "addSectionItem")
	{
		complexityScore += complexitySettings.weightSectionItem;
		surveySummary.nbSections += 1;
	}
	else if(type=="addSimpleItem")
	{
		complexityScore += complexitySettings.weightSimpleItem;
	}
	else if(type=="addSimpleQuestion")
	{
		complexityScore += complexitySettings.weightSimpleQuestion;
		surveySummary.nbQuestions += 1;
	}
	else if(type == "addChoiceQuestion")
	{
		complexityScore += complexitySettings.weightChoiceQuestion;
		surveySummary.nbQuestions += 1;
	}
	else if(type=="addGalleryQuestion")
	{
		complexityScore += complexitySettings.weightGalleryQuestion;
		surveySummary.nbQuestions += 1;
	}
	else if(type == "addTableOrMatrixQuestion")
	{
		complexityScore += complexitySettings.weightTableOrMatrixQuestion;
		surveySummary.nbQuestions += 1;
	}
	else if(type=="removeSimpleQuestion")
	{
		complexityScore -= complexitySettings.weightSimpleQuestion;
		surveySummary.nbQuestions -= 1;
	}
	else if(type=="removeSimpleItem")
	{
		complexityScore -= complexitySettings.weightSimpleItem;
	}
	else if(type=="removeSection")
	{
		complexityScore -= complexitySettings.weightSectionItem;
		surveySummary.nbSections -= 1;
	}
	else if(type=="removeTableOrMatrixItem")
	{
		complexityScore -= complexitySettings.weightTableOrMatrixQuestion;
		surveySummary.nbQuestions -= 1;
	}
	else if(type=="removeGalleryQuestion")
	{
		complexityScore -= complexitySettings.weightGalleryQuestion;
		surveySummary.nbQuestions -= 1;
	}
	else if(type=="addTooManyRows")
	{
		complexityScore += complexitySettings.weightTooManyRows;
	}
	else if(type=="removeTooManyRows")
	{
		complexityScore -= complexitySettings.weightTooManyRows;
	}
	else if(type=="addTooManyColumns")
	{
		complexityScore += complexitySettings.weightTooManyColumns;
	}
	else if(type=="removeTooManyColumns")
	{
		complexityScore -= complexitySettings.weightTooManyColumns;
	}
	else if(type=="addTooManyPossibleAnswers")
	{
		complexityScore += complexitySettings.weightTooManyPossibleAnswers;
	}
	else if(type=="removeTooManyPossibleAnswers")
	{
		complexityScore -= complexitySettings.weightTooManyPossibleAnswers;
	}
	else if(type=="addDependency")
	{
		complexityScore += complexitySettings.weightDependency;
	}
	else if(type=="addDoubleDependency")
	{
		complexityScore += complexitySettings.weightDoubleDependency;
	}
	else if(type=="removeDependency")
	{
		complexityScore -= complexitySettings.weightDependency;
		surveySummary.nbDependencies = surveySummary.nbDoubleDependencies - 1;
	}
	else if(type=="removeDoubleDependency")
	{
		complexityScore -= complexitySettings.weightDoubleDependency;
		surveySummary.nbDoubleDependencies = surveySummary.nbDoubleDependencies - 1;
	}
	
	/*Compute limits excesses*/
	if(surveySummary.nbQuestions  > complexitySettings.questionsThreshold &&  !surveySummary.thresholdQuestionsReached)
	{
		complexityScore += complexitySettings.questionsThresholdScore;
		surveySummary.thresholdQuestionsReached = true;
	}
	
	if(surveySummary.nbQuestions  <= complexitySettings.questionsThreshold &&  surveySummary.thresholdQuestionsReached)
	{
		complexityScore -= complexitySettings.questionsThresholdScore;
		surveySummary.thresholdQuestionsReached = false;
	}
	
	if(surveySummary.nbSections  > complexitySettings.sectionThreshold &&  !surveySummary.thresholdSectionsReached)
	{
		complexityScore += complexitySettings.sectionThresholdScore;
		surveySummary.thresholdSectionsReached = true;
	}
	
	if(surveySummary.nbSections  <= complexitySettings.sectionThreshold &&  surveySummary.thresholdSectionsReached)
	{
		complexityScore -= complexitySettings.sectionThresholdScore;
		surveySummary.thresholdSectionsReached = false;
	}
	
	if(surveySummary.nbDependencies > complexitySettings.dependenciesThreshold &&  !surveySummary.thresholdDependenciesReached)
	{
		complexityScore += complexitySettings.dependenciesThresholdScore;
		surveySummary.thresholdDependenciesReached = true;
	}
	
	if(surveySummary.nbDependencies <= complexitySettings.dependenciesThreshold &&  surveySummary.thresholdDependenciesReached)
	{
		complexityScore -= complexitySettings.dependenciesThresholdScore;
		surveySummary.thresholdDependenciesReached = false;
	}
	
	//console.log(complexityScore);
		
	if(complexityScore <= complexitySettings.lowScore && oldScore > complexitySettings.lowScore)
	{
		$("#complexityImageLevel").attr("src", contextpath + "/resources/images/complexity-indicator-low.png").attr("title", lowLevel).attr("data-original-title", lowLevel);;
		$("#complexity-message-box").hide();
		$('#criticalComplexity').val("false");
	}
	else if((complexityScore > complexitySettings.lowScore && complexityScore <= complexitySettings.mediumScore) && (oldScore <= complexitySettings.lowScore || oldScore > complexitySettings.mediumScore))
	{
		$("#complexityImageLevel").attr("src", contextpath + "/resources/images/complexity-indicator-medium.png").attr("title", mediumLevel).attr("data-original-title", mediumLevel);
		$("#complexity-message-box").hide();
		$('#criticalComplexity').val("false");
	}
	else if(complexityScore > complexitySettings.mediumScore && complexityScore <= complexitySettings.highScore && (oldScore <= complexitySettings.mediumScore || oldScore > complexitySettings.highScore))
	{
		$("#complexityImageLevel").attr("src", contextpath + "/resources/images/complexity-indicator-high.png").attr("title", highLevel).attr("data-original-title", highLevel);;
		$("#complexity-message-box").hide();
		$('#criticalComplexity').val("false");
	}
	else if(complexityScore > complexitySettings.highScore && oldScore <= complexitySettings.highScore)
	{
		$("#complexityImageLevel").attr("src", contextpath + "/resources/images/complexity-indicator-critic.png").attr("title", criticLevel).attr("data-original-title", criticLevel);;
		$("#complexity-message-box").show();
		$('#criticalComplexity').val("true");
		
//		if($("#generic-info-box").css("display") == "block")
//		{
//			$("#generic-info-box").css("top", "65px");
//		}		
	}
	
}

function removeElementScore(element)
{
	var tmpId = $(element).find("div.questiontitle").attr("id");
	
	removeElementFromDependencies(tmpId);
	
	if($(element).hasClass("freetextitem") || $(element).hasClass("numberitem")
			|| $(element).hasClass("dateitem") || $(element).hasClass("timeitem") || $(element).hasClass("uploaditem") || $(element).hasClass("emailitem")
			|| $(element).hasClass("regexitem"))
	{
		updateComplexityScore("removeSimpleQuestion");
	}
	else if($(element).hasClass("sectionitem"))
	{
		updateComplexityScore("removeSection");
	}
	else if($(element).hasClass("textitem") || $(element).hasClass("imageitem") || $(element).hasClass("ruleritem") || $(element).hasClass("downloaditem") || $(element).hasClass("confirmationitem") || $(element).hasClass("ratingitem"))
	{
		updateComplexityScore("removeSimpleItem");
	}
	else if($(element).hasClass("mytableitem") || $(element).hasClass("matrixitem"))
	{
		updateComplexityScore("removeTableOrMatrixItem");
		
		if (tableAndMatrixDefinitions[tmpId] != null)
		{
			if(tableAndMatrixDefinitions[tmpId].row > complexitySettings.rowThreshold)
			{
				updateComplexityScore("removeTooManyRows");
			}
			
			if(tableAndMatrixDefinitions[tmpId].col > complexitySettings.columnThreshold)
			{
				updateComplexityScore("removeTooManyColumns");
			}
			delete tableAndMatrixDefinitions[tmpId];
		}
	}
	else if($(element).hasClass("singlechoiceitem") || $(element).hasClass("multiplechoiceitem"))
	{
		updateComplexityScore("removeSimpleQuestion");
		if (choiceDefinitions[tmpId] != null)
		{
			if(choiceDefinitions[tmpId].row > complexitySettings.possibleAnswersThreshold)
			{
				updateComplexityScore("removeTooManyPossibleAnswers");
			}
			delete choiceDefinitions[tmpId];
		}
	}
	else if($(element).hasClass("galleryitem"))
	{
		updateComplexityScore("removeGalleryQuestion");
	}
}

function updateTableOrMatrixSummary(tableId, type, nb)
{	
	if(type == "col")
	{
		tableAndMatrixDefinitions[tableId].col = tableAndMatrixDefinitions[tableId].col + nb; 
	}
	else if(type == "row")
	{
		tableAndMatrixDefinitions[tableId].row = tableAndMatrixDefinitions[tableId].row + nb;		
	}
	else if(type == "init")
	{
		var tmpTable = {"row": nb, "col": nb, "thresholdColumnsAlready": false, "thresholdRowsAlready":false};
		tableAndMatrixDefinitions[tableId] = tmpTable;
	}
	
	if(tableAndMatrixDefinitions[tableId].row > complexitySettings.rowThreshold && !tableAndMatrixDefinitions[tableId].thresholdRowsAlready)
	{
		updateComplexityScore("addTooManyRows");
		tableAndMatrixDefinitions[tableId].thresholdRowsAlready = true;
	}
	else if(tableAndMatrixDefinitions[tableId].row <= complexitySettings.rowThreshold && tableAndMatrixDefinitions[tableId].thresholdRowsAlready)
	{
		updateComplexityScore("removeTooManyRows");
		tableAndMatrixDefinitions[tableId].thresholdRowsAlready = false;
	}
	
	if(tableAndMatrixDefinitions[tableId].col > complexitySettings.columnThreshold && !tableAndMatrixDefinitions[tableId].thresholdColumnsAlready)
	{
		updateComplexityScore("addTooManyColumns");
		tableAndMatrixDefinitions[tableId].thresholdColumnsAlready = true;
	}
	else if(tableAndMatrixDefinitions[tableId].col <= complexitySettings.columnThreshold && tableAndMatrixDefinitions[tableId].thresholdColumnsAlready)
	{
		updateComplexityScore("removeTooManyColumns");
		tableAndMatrixDefinitions[tableId].thresholdColumnsAlready = false;
	}
}

function updateListSummary(listId, type, nb)
{		
	if(type == "row")
	{
		choiceDefinitions[listId].row = nb; 
	}
	else if(type == "init")
	{
		var tmpTable = {"row": nb, "thresholdPossibleAnswersAlready":false};
		choiceDefinitions[listId] = tmpTable;
	}
	
	if(choiceDefinitions[listId].row > complexitySettings.possibleAnswersThreshold && !choiceDefinitions[listId].thresholdPossibleAnswersAlready)
	{
		updateComplexityScore("addTooManyPossibleAnswers");
		choiceDefinitions[listId].thresholdPossibleAnswersAlready = true;
	}
	else if(choiceDefinitions[listId].row <= complexitySettings.possibleAnswersThreshold && choiceDefinitions[listId].thresholdPossibleAnswersAlready)
	{
		updateComplexityScore("removeTooManyPossibleAnswers");
		choiceDefinitions[listId].thresholdPossibleAnswersAlready = false;
	}
}

function scanSurveyComplexity()
{
	complexityScore = 0;
	surveySummary = {"nbSections": 0, "nbQuestions": 0, "nbDependencies":0, "nbDoubleDependencies":0, 
			"thresholdQuestionsReached":false, "thresholdSectionsReached":false, "thresholdDependenciesReached": false};
	tableAndMatrixDefinitions = {};
	choiceDefinitions = {};
	sourceDependeciesDefinitions = {};

	$("#content > li.survey-element").each(function(){
		
		var id = $(this).attr("id");
		
		if($(this).hasClass("freetextitem") || $(this).hasClass("numberitem")
				|| $(this).hasClass("dateitem") || $(this).hasClass("timeitem") || $(this).hasClass("uploaditem") || $(this).hasClass("emailitem")
				|| $(this).hasClass("regexitem"))
		{
			updateComplexityScore("addSimpleQuestion");
		}
		else if($(this).hasClass("sectionitem"))
		{
			updateComplexityScore("addSectionItem");
		}
		else if($(this).hasClass("textitem") || $(this).hasClass("imageitem") || $(this).hasClass("ruleritem")  || $(this).hasClass("downloaditem") || $(this).hasClass("confirmationitem") || $(this).hasClass("ratingitem"))
		{
			updateComplexityScore("addSimpleItem");
		}
		else if($(this).hasClass("mytableitem"))
		{
			var nbRows = $(this).find(".tabletable tbody tr").size() -1;
			var nbCols = $(this).find(".tabletable tbody tr:first").find("td").size() -1;
			updateComplexityScore("addTableOrMatrixQuestion");
			updateTableOrMatrixSummary(id, "init", 0); 
			updateTableOrMatrixSummary(id, "col", nbCols); 
			updateTableOrMatrixSummary(id, "row", nbRows);
		}
		else if($(this).hasClass("matrixitem"))
		{
			var nbRows = $(this).find(".matrixtable tbody tr").size() -1;
			var nbCols = $(this).find(".matrixtable tbody tr:first").find("td").size() -1;
			updateComplexityScore("addTableOrMatrixQuestion");
			updateTableOrMatrixSummary(id, "init", 0); 
			updateTableOrMatrixSummary(id, "col", nbCols); 
			updateTableOrMatrixSummary(id, "row", nbRows);
		}
		else if($(this).hasClass("singlechoiceitem") || $(this).hasClass("multiplechoiceitem"))
		{
			var nbChoices = $(this).find("input[name^=pashortname]").length;
			updateComplexityScore("addChoiceQuestion");
			updateListSummary(id, "init", nbChoices); 
		}
		else if($(this).hasClass("galleryitem"))
		{
			updateComplexityScore("addGalleryQuestion");
		}
		
		var contentElement = $(this);
		
		if($(contentElement).find("input[type=hidden][name*='dependencies'][value!='']").size() > 0)
		{
				$(contentElement).find("input[type=hidden][name*='dependencies'][value!='']").each(function(idx, elem){
					
					if ($(elem).attr("value"))
					{
						var targets = $(elem).attr("value").split(";");		
						var trigger = $(elem).parent();
						
						targets.pop(); //in order to remove the last element (""), because the "value" attr is formated like "xxx;"
						
						if(sourceDependeciesDefinitions[$(trigger).attr("id")] == null)
			    		{
							sourceDependeciesDefinitions[$(trigger).attr("id")]={"id":id, "targets":targets, "nbMarkers": targets.length};
			    		}
						else
						{
							var tmpTargets = sourceDependeciesDefinitions[$(trigger).attr("id")].targets;
							
							for(var iTarget = 0; iTarget < targets.length ; iTarget++)
							{
								if($.inArray(targets[iTarget], tmpTargets) == -1)
								{
									tmpTargets.push(targets[iTarget]);
									sourceDependeciesDefinitions[$(trigger).attr("id")].targets = tmpTargets;
									sourceDependeciesDefinitions[$(trigger).attr("id")].nbMarkers += 1; 
								}
							}
						}
					}
				});
		}
	});
	
	scanSurveyDependencies();
}

function scanSurveyDependencies()
{	
	var nbDep = 0;
	var nbDoubleDep = 0;
	
	$.each(sourceDependeciesDefinitions, function(key, value) 
	{		
		nbDep = value.nbMarkers + nbDep;
		surveySummary.nbDependencies = nbDep;
		for(var i=0; i< value.nbMarkers ; i++)
		{
			updateComplexityScore("addDependency");
		}
		
		//search if any question is already referenced in another dependecy
		// go again through all dep schema and look if one target is not already in one id
		$.each(value.targets, function(keyTarget, valTarget)
		{	
			$.each(sourceDependeciesDefinitions, function(key2, value2)
			{
				if(value2.id == valTarget)
				{
					updateComplexityScore("addDoubleDependency");
		    		nbDoubleDep++;
		    		surveySummary.nbDoubleDependencies = nbDoubleDep;
				}
			});
		});
	});
}

function resetDependencyScore()
{		
	var nbDep = surveySummary.nbDependencies;
	var nbDoubleDep = surveySummary.nbDoubleDependencies;
	
	for(var i=0; i < nbDep ; i++)
	{
		updateComplexityScore("removeDependency");
	}
		
	for(var ii=0; ii < nbDoubleDep ; ii++)
	{
		updateComplexityScore("removeDoubleDependency");
	}	
}

function scanQuestionDependencies(selectedAnswer)
{
	resetDependencyScore();

	var baseObject = selectedAnswer;
	var id = "";
	
	if ($(selectedAnswer).hasClass("columntrigger")) //in case a matrix column is selected
	{
		id =  $(selectedAnswer).closest("table").find(".trigger").find("input[name^='dependencies']").first().attr("name").substring(12);
		baseObject = $(selectedAnswer).closest("table").find(".trigger");
	}
	else
	{
		id = $(selectedAnswer).find("input[name^='answer']").first().attr("name").substring(6);
	}
	
	delete sourceDependeciesDefinitions[$(baseObject).attr("id")];
	
	if($(baseObject).find("input[name^='dependencies'][value!='']").size() > 0)
	{
		var paID = $(baseObject).find("input[name^='pauid']").val();
		
		$(baseObject).find("input[name^='dependencies'][value!='']").each(function(idx, elem){
				
			var targets = $(elem).val().split(";");			
			targets.pop(); //in order to remove the last element (""), because the "value" attr is formated like "xxx;"
			
			if(sourceDependeciesDefinitions[paID] == null)
    		{
				sourceDependeciesDefinitions[paID]={"id":id, "targets":targets, "nbMarkers": targets.length};
    		}
			else
			{
				var tmpTargets = sourceDependeciesDefinitions[paID].targets;
				
				for(var iTarget = 0; iTarget < targets.length ; iTarget++)
				{
					if($.inArray(targets[iTarget], tmpTargets) == -1)
					{
						tmpTargets.push(targets[iTarget]);
						sourceDependeciesDefinitions[paID].targets = tmpTargets;
						sourceDependeciesDefinitions[paID].nbMarkers += 1; 
					}
				}
			}
		});
	}
	else
	{
		var paID = $(baseObject).find("input[name^='pauid']").val();
		delete sourceDependeciesDefinitions[paID];
	}
	
	scanSurveyDependencies();
}

function removeElementFromDependencies(targetId)
{
	$.each(sourceDependeciesDefinitions, function(key, value) 
	{		
		
		var tmpValue = value;
		
		$.each(value.targets, function(keyTarget, valTarget)
		{			
	    	if(valTarget == targetId)
    		{
	    		tmpValue.nbMarkers -=1;
	    		var indexOf = tmpValue.targets.indexOf(valTarget);
	    		tmpValue.targets.splice(indexOf,1);
	    		updateComplexityScore("removeDependency");
    		}
		});
		
		// if no markers, not any linked dependency is possible
		//remove double dependency if any
		if(tmpValue.nbMarkers == 0)
		{
			$.each(sourceDependeciesDefinitions, function(key, value)
			{
				if(value.targets.indexOf(tmpValue.id) >= 0)
				{
					value.targets.splice(value.targets.indexOf(tmpValue.id));
					updateComplexityScore("removeDoubleDependency");
		    		surveySummary.nbDoubleDependencies = surveySummary.nbDoubleDependencies -1;
				}
			});
			
			delete sourceDependeciesDefinitions[key];
		}
	});
}