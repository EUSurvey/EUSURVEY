/**
 * 
 */
package com.ec.survey.model.administration;

/**
 */
public enum ComplexityParameters {

	COMPLEXITY_LOW_LEVEL_SCORE("lowScore", 50),
	COMPLEXITY_MEDIUM_LEVEL_SCORE("mediumScore", 100),
	COMPLEXITY_HIGH_LEVEL_SCORE("highScore", 150),
	COMPLEXITY_CRITICAL_LEVEL_SCORE("criticalScore", 200), 
	WEIGHT_SECTION_ITEM("weightSectionItem", 1), 
	WEIGHT_SIMPLE_ITEM("weightSimpleItem", 1),
	WEIGHT_SIMPLE_QUESTION("weightSimpleQuestion", 1),
	WEIGHT_CHOICE_QUESTION("weightChoiceQuestion", 1),
	WEIGHT_GALLERY_QUESTION("weightGalleryQuestion", 5),
	WEIGHT_TABLE_OR_MATRIX_QUESTION("weightTableOrMatrixQuestion", 5),
	WEIGHT_TOO_MANY_ROWS("weightTooManyRows", 10),
	WEIGHT_TOO_MANY_COLUMNS("weightTooManyColumns", 10),
	THRESHOLD_ROW("rowThreshold", 10),
	THRESHOLD_COLUMN("columnThreshold", 10),
	WEIGHT_TOO_MANY_POSSIBLE_ANSWERS("weightTooManyPossibleAnswers", 10),
	THRESHOLD_POSSIBLE_ANSWERS("possibleAnswersThreshold", 10),
	WEIGHT_DEPENCENDY("weightDependency", 5),
	WEIGHT_DOUBLE_DEPENDENCY("weightDoubleDependency", 15),
	THRESHOLD_QUESTION("questionsThreshold", 50),
	THRESHOLD_QUESTION_SCORE("questionsThresholdScore", 5),
	THRESHOLD_SECTION("sectionThreshold", 5),
	THRESHOLD_SECTION_SCORE("sectionThresholdScore", 5),
	THRESHOLD_DEPENDENCIES("dependenciesThreshold", 10),
	THRESHOLD_DEPENDENCIES_SCORE("dependenciesThresholdScore", 10);
	
	private String key;
	private Integer defaultValue;
	
	ComplexityParameters(String key, Integer defaultValue) {
		this.key=key;
		this.defaultValue = defaultValue;
	}
	
	@Override
	public String toString() {
		return key;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public Integer getDefaultValue() {
		return defaultValue;
	}
	
	public void setDefaultValue(Integer defaultValue) {
		this.defaultValue = defaultValue;
	}
}
