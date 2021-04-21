package com.ec.survey.model.survey;

import javax.persistence.Cacheable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.owasp.esapi.errors.ValidationException;

import com.ec.survey.tools.Tools;

/**
 * RankingItem represents a child element in a RankingQuestion
 */
@Entity
@DiscriminatorValue("RANKINGITEM")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class RankingItem extends Element {
	
	private static final long serialVersionUID = 1L;

	public RankingItem(String title, String originaltitle, String shortname, String uid) {
		setTitle(title);
		setOriginalTitle(originaltitle);
		setShortname(shortname);
		setUniqueId(uid);
	}
	
	public RankingItem() {
	}	
	
	public RankingItem copy(String fileDir) throws ValidationException
	{	
		RankingItem copy = new RankingItem();
		copy.setTitle(Tools.filterHTML(this.getTitle()));
		copy.setSourceId(this.getId());
		copy.setOriginalTitle(Tools.filterHTML(this.getOriginalTitle()));
		copy.setShortname(this.getShortname());
		copy.setUniqueId(getUniqueId());
		
		return copy;
	}

	@Override
	public boolean differsFrom(Element other) {
		return !getTitle().equals(other.getTitle()) || !getOriginalTitle().equals(other.getOriginalTitle())
				|| !getShortname().equals(other.getShortname()) || !getUniqueId().equals(other.getUniqueId());
	}
}
