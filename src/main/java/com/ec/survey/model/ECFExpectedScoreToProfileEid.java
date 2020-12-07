package com.ec.survey.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.*;

@Embeddable
public class ECFExpectedScoreToProfileEid implements Serializable {
    /**
	 *
	 */
	private static final long serialVersionUID = -4183214707510584371L;

	@ManyToOne
	@JoinColumn(name = "COMPETENCY", referencedColumnName = "COMPETENCY_ID")
	protected ECFCompetency competency;

	@ManyToOne
	@JoinColumn(name = "PROFILE", referencedColumnName = "PROFILE_ID")
	protected ECFProfile profile;

	public ECFProfile getECFProfile() {
		return profile;
	}
	public void setECFProfile(ECFProfile profile) {
		this.profile = profile;
	}

	public ECFCompetency getECFCompetency() {
		return competency;
	}
	public void setECFCompetency(ECFCompetency competency) {
		this.competency = competency;
	}	

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ECFExpectedScoreToProfileEid)) return false;
        ECFExpectedScoreToProfileEid that = (ECFExpectedScoreToProfileEid) o;
        return Objects.equals(getECFCompetency(), that.getECFCompetency()) &&
				Objects.equals(getECFProfile(), that.getECFProfile());
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(getECFCompetency(), getECFProfile());
    }

}