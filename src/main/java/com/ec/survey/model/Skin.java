package com.ec.survey.model;

import com.ec.survey.model.administration.User;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "SKINS")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Skin implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private boolean isPublic;
	private Date updateDate;
	private User owner;
	private List<SkinElement> elements = new ArrayList<>();
	
	public Skin()
	{
		createMissingElements();
	}
	
	public void createMissingElements() {
		List<String> allElements = new ArrayList<>();
		allElements.add(".answertext");
		allElements.add(".info-box");
		allElements.add(".link");
		allElements.add(".linkstitle");
		allElements.add(".questionhelp");
		allElements.add(".questiontitle");
		allElements.add(".matrix-header");
		allElements.add(".table-header");
		allElements.add(".sectiontitle");	
		allElements.add(".surveytitle");	
		allElements.add(".right-area");
		allElements.add(".text");		
		
		List<String> existingElements = new ArrayList<>();
		for (SkinElement element : elements)
		{
			existingElements.add(element.getName());
		}
		
		for (String element : allElements)
		{
			if (!existingElements.contains(element))
			{
				elements.add(new SkinElement(element));
			}
		}
	}
		
	@Id
	@Column(name = "SKIN_ID")
	@GeneratedValue
	public Integer getId() {
		return id;
	}	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "FILE_NAME")
	public String getName() {
		return name;
	}	
	public void setName(String name) {
		this.name = name;
	}
	
	@Transient 
	public String getDisplayName()
	{
		if (getOwner().getLogin().equalsIgnoreCase("admin"))
		{
			switch (getName())
			{
				case "EUSurveyNew.css":
					return "EUSurvey";
				case "New Official EC Skin":
					return "EC Official";
				case "ECA Skin":
					return "ECA Official";
				case "EUSurvey.css":
					return "EUSurvey (obsolete)";
				case "Official EC Skin":
					return "EC Official (obsolete)";
				default:
					break;
			}	
		}
		
		return getName();
	}
	
	@Transient
	public String getCss(boolean forPDF) {
		StringBuilder s = new StringBuilder();
		
		for (SkinElement element: elements)
		{
			s.append(element.getCss(forPDF));
		}
		
		return s.toString();
	}	
	
	@Transient
	public String getCss() {
		return getCss(false);
	}	
		
	@Column(name = "ISPUBLIC")
	public boolean getIsPublic() {
		return isPublic;
	}	
	public void setIsPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	
	@Column(name = "UPDATE_DATE")
	public Date getUpdateDate() {
		return updateDate;
	}	
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
	@ManyToOne  
	@JoinColumn(name="OWNER", nullable = false)    
	public User getOwner() {return owner;}  
	public void setOwner(User owner) {this.owner = owner;}  
	
	@Transient
	public String getValue(String item, String key)
	{
		return "";
	}
	
	@OneToMany(targetEntity=SkinElement.class, cascade = CascadeType.ALL  )  
	@Fetch(value = FetchMode.SELECT)
	@OrderBy(value = "name asc")
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<SkinElement> getElements() {
		return elements;
	}
	public void setElements(List<SkinElement> elements) {
		this.elements = elements;
	}
	
	@Transient
	public boolean setElementValue(String name, String key, String value)
	{
		for (SkinElement element: elements)
		{
			if (element.getName().equals(name))
			{
				switch (key) {
					case "color":
						element.setColor(value);
						return true;
					case "background-color":
						element.setBackgroundColor(value);
						return true;
					case "font-family":
						element.setFontFamily(value);
						return true;
					case "font-size":
						element.setFontSize(value);
						return true;
					case "font-weight":
						element.setFontWeight(value);
						return true;
					case "font-style":
						element.setFontStyle(value);
						return true;
					case "border-color":
						return true;
					default:
						break;
				}				
			}
		}
		return false;
	}
	
	@Transient 
	public String getElementValue(String name, String key)
	{
		for (SkinElement element: elements)
		{
			if (element.getName().equals(name))
			{
				switch (key) {
					case "color":
						return element.getColor() != null && element.getColor().length() > 0 ? element.getColor() : getDefault(name, key);
					case "background-color":
						return element.getBackgroundColor() != null && element.getBackgroundColor().length() > 0 ? element.getBackgroundColor() : getDefault(name, key);
					case "font-family":
						return element.getFontFamily() != null && element.getFontFamily().length() > 0 ? element.getFontFamily() : getDefault(name, key);
					case "font-size":
						return element.getFontSize() != null && element.getFontSize().length() > 0 ? element.getFontSize() : getDefault(name, key);
					case "font-weight":
						return element.getFontWeight() != null && element.getFontWeight().length() > 0 ? element.getFontWeight() : getDefault(name, key);
					case "font-style":
						return element.getFontStyle() != null && element.getFontStyle().length() > 0 ? element.getFontStyle() : getDefault(name, key);
					default:
						break;
				}	
			}
		}
		
		return "";
	}
	
	private String getDefault(String name, String key) {
		switch (key) {
			case "color":
				if (name.equals(".sectiontitle")) return "004F98";
				if (name.equals(".link")) return "0088CC";
				if (name.equals(".questionhelp")) return "A6A6A6";
				return "333333";
			case "background-color":
				if (name.equals(".right-area")) return "ffffff";
				return "transparent";
			case "font-family":
				return "sans-serif";
			case "font-size":
				if (name.equals(".info-box")) return "13px";
				if (name.equals(".questionhelp")) return "11px";
				if (name.equals(".linkstitle")) return "16px";
				if (name.equals(".sectiontitle")) return "20px";
				if (name.equals(".surveytitle")) return "24px";
				return "14px";
			case "font-weight":
				if (name.equals(".linkstitle")) return "bold";
				return "normal";
			case "font-style":
				return "normal";
			default:
				break;
		}	
		
		return "";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Skin other = (Skin) obj;
		return (id == other.id);
	}

}
