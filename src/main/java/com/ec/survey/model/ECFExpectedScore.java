package com.ec.survey.model;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

import javax.persistence.*;

/**
 * Represents an expected score (0 to 4), from a Profile to one or several
 * Competencies.
 */
@Entity
@Table(name = "ECF_EXPECTED_SCORE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ECFExpectedScore implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ECFExpectedScoreToProfileEid id;

	@Column(name = "SCORE")
	private int score;

	protected static final Logger logger = Logger.getLogger(ECFExpectedScore.class);

	private ECFExpectedScore() {

	}

	public ECFExpectedScore(ECFExpectedScoreToProfileEid eid, Integer score) {
		this.setECFExpectedScoreToProfileEid(eid);
		this.setScore(score);
	}

	// See inside for the column
	public void setECFExpectedScoreToProfileEid(ECFExpectedScoreToProfileEid eid) {
		this.id = eid;
	}

	public ECFExpectedScoreToProfileEid getECFExpectedScoreToProfileEid() {
		return id;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score >= 0 && score <= 5 ? score : 0;
	}

	/**
	 * Copying the Score, setting it the alreadyCopiedCompetency. 
	 * Does NOT copy the Profile but changes one of its score with the updated score.
	 */
	public ECFExpectedScore copy (ECFCompetency alreadyCopiedCompetency) {
		ECFExpectedScoreToProfileEid eid = new ECFExpectedScoreToProfileEid();
		eid.setECFCompetency(alreadyCopiedCompetency);
		eid.setECFProfile(this.getECFExpectedScoreToProfileEid().getECFProfile());
		ECFExpectedScore theCopy = new ECFExpectedScore(eid, this.getScore());
		this.getECFExpectedScoreToProfileEid().getECFProfile().replaceScore(this.getECFExpectedScoreToProfileEid().getECFCompetency(),
				theCopy);
		return theCopy;
	}

	public ECFExpectedScore copy (ECFProfile alreadyCopiedProfile) {
		ECFExpectedScoreToProfileEid eid = new ECFExpectedScoreToProfileEid();
		eid.setECFProfile(alreadyCopiedProfile);
		eid.setECFCompetency(this.getECFExpectedScoreToProfileEid().getECFCompetency());
		ECFExpectedScore theCopy = new ECFExpectedScore(eid, this.getScore());
		this.getECFExpectedScoreToProfileEid().getECFCompetency().replaceScore(this.getECFExpectedScoreToProfileEid().getECFProfile(),
				theCopy);
		return theCopy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + score;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ECFExpectedScore other = (ECFExpectedScore) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (score != other.score)
			return false;
		return true;
	}


}
