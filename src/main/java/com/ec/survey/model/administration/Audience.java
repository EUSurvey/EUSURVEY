/**
 * 
 */
package com.ec.survey.model.administration;

/**
 */
public enum Audience {
	AUDIENCE_LOW("low"), AUDIENCE_MEDIUM("medium"), AUDIENCE_HIGH("high");
	
	private String level;
	
	Audience(String level) {
		this.level = level;
	}

	/**
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}
	
	@Override
	public String toString() {
		return level;
	}
}
