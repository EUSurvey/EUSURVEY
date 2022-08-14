package com.ec.survey.model.survey;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.ec.survey.tools.Tools;

import javax.persistence.*;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;

/**
 * Represents all parameters used inside a scoring rule (quiz feature)
 */
@Entity
@Table(name = "SCORINGITEMS")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ScoringItem implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String uid = UUID.randomUUID().toString();
	private Integer sourceId;
	private int type;
	private int points;
	private boolean correct;
	private Double min;
	private Double max;
	private Date minDate;
	private Date maxDate;
	private String feedback;
	private String value;
	private String value2;
	private int position;
		
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "UID")
	public String getUniqueId() {
		return uid;
	}	
	public void setUniqueId(String uid) {
		this.uid = uid;
	}
	
	@Column(name = "SOURCE_ID")
	public Integer getSourceId() {
		return sourceId;
	}	
	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}
	
	@Column(name = "POINTS")
	public int getPoints() {
		return points;
	}	
	public void setPoints(int points) {
		this.points = points;
	}
	
	@Column(name = "POSITION")
	public int getPosition() {
		return position;
	}	
	public void setPosition(int position) {
		this.position = position;
	}
	
	/**
	 * -1: other
	 * 0: equals
	 * 1: <
	 * 2: <=
	 * 3: >
	 * 4: >=
	 * 5: between
	 * 6: contains
	 * 7: matches
	 * 8: empty
	 */
	@Column(name = "TYPE")
	public Integer getType() {
		return type;
	}	
	public void setType(Integer type) {
		this.type = type != null ? type : 0;
	}
	
	@Column(name = "CORRECT")
	public boolean isCorrect() {
		return correct;
	}
	public void setCorrect(boolean correct) {
		this.correct = correct;
	}
	
	@Column(name = "MIN")
	public Double getMin() {
		return min;
	}
	public void setMin(Double min) {
		this.min = min;
	}
	
	@Column(name = "MAX")
	public Double getMax() {
		return max;
	}
	public void setMax(Double max) {
		this.max = max;
	}
	
	@Column(name = "MINDATE")
	public Date getMinDate() {
		return minDate;
	}
	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}
	
	@Column(name = "MAXDATE")
	public Date getMaxDate() {
		return maxDate;
	}
	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}
	
	@Lob
	@Column(name = "FEEDBACK", length = 40000)
	public String getFeedback() {
		return feedback;
	}
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	
	@Column(name = "VALUE")
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Column(name = "VALUE2")
	public String getValue2() {
		return value2;
	}
	public void setValue2(String value2) {
		this.value2 = value2;
	}
	
	public ScoringItem copy()
	{
		ScoringItem copy = new ScoringItem();
		copy.setUniqueId(getUniqueId());
		copy.setSourceId(id);
		copy.setCorrect(correct);
		copy.setPoints(points);
		copy.setValue(value);
		copy.setValue2(value2);
		copy.setType(type);
		copy.setMin(min);
		copy.setMax(max);
		copy.setMinDate(minDate);
		copy.setMaxDate(maxDate);
		copy.setFeedback(feedback);
		copy.setPosition(position);
		
		return copy;
	}
	public boolean differsFrom(ScoringItem scoringItem) {
		if (scoringItem == null) return true;
		
		if (correct != scoringItem.correct) return true;
		if (points != scoringItem.points) return true;
		if (!Tools.isEqual(value, scoringItem.value)) return true;
		if (!Tools.isEqual(value2, scoringItem.value2)) return true;
		if (type != scoringItem.type) return true;
		if (!Tools.isEqual(min, scoringItem.min)) return true;
		if (!Tools.isEqual(max, scoringItem.max)) return true;
		if (!Tools.isEqual(feedback, scoringItem.feedback)) return true;

		return false;
	}
}