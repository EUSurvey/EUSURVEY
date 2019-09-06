package com.ec.survey.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Table(name = "SKINELEM")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SkinElement implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String color;
	private String backgroundColor;
	private String fontFamily;
	private String fontSize;
	private String fontWeight;
	private String fontStyle;
	
	public SkinElement()
	{}
	
	public SkinElement(String name) {
		this.name = name;
	}	
	
	@Id
	@Column(name = "SE_ID")
	@GeneratedValue
	public Integer getId() {
		return id;
	}	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "SE_NAME")
	public String getName() {
		return name;
	}	
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "SE_FG")
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	
	@Column(name = "SE_BG")
	public String getBackgroundColor() {
		return backgroundColor;
	}
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	@Column(name = "SE_FF")
	public String getFontFamily() {
		return fontFamily;
	}
	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}
	
	@Column(name = "SE_FS")
	public String getFontSize() {
		return fontSize;
	}
	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}
	
	@Column(name = "SE_FW")
	public String getFontWeight() {
		return fontWeight;
	}
	public void setFontWeight(String fontWeight) {
		this.fontWeight = fontWeight;
	}
	
	@Column(name = "SE_FST")
	public String getFontStyle() {
		return fontStyle;
	}
	public void setFontStyle(String fontStyle) {
		this.fontStyle = fontStyle;
	}

	@Transient
	public SkinElement copy() {
		SkinElement copy = new SkinElement();
		copy.name = name;
		copy.backgroundColor = backgroundColor;
		copy.color = color;
		copy.fontFamily = fontFamily;
		copy.fontSize = fontSize;
		copy.fontStyle = fontStyle;
		copy.fontWeight = fontWeight;
		return copy;
	}

	@Transient
	public Object getCss() {
		return getCss(false);
	}

	@Transient
	public Object getCss(boolean forPDF) {
		StringBuilder s = new StringBuilder();
		
		s.append(this.name).append(" {");
		s.append("\n");
		
		if (color != null && color.length() > 0)
		{
			s.append("color: ");
			if (!color.startsWith("#"))s.append("#");
			s.append(color).append("; \n");
		}
		
		if (this.name.equalsIgnoreCase(".sectiontitle"))
		{
			if (color != null && color.length() > 0) s.append("border-color: #").append(color).append("; \n");
		}	
				
		if (backgroundColor != null && backgroundColor.length() > 0)
		{
			s.append("background-color: ");
			if (!backgroundColor.startsWith("#") && !backgroundColor.equals("transparent"))s.append("#");
			
			s.append(backgroundColor).append("; \n");
		}
		
		if (forPDF)
		{
			String fontFamilyPDF = fontFamily;
			String fontStylePDF = fontStyle;
			
			if (fontFamilyPDF != null && fontFamilyPDF.length() > 0) {
			
				if (fontFamilyPDF.contains("monospace"))
				{
					fontFamilyPDF = "FreeMono";
				} else if (fontFamilyPDF.contains("sans-serif"))
				{
					fontFamilyPDF = "FreeSans";
				} else if (fontFamilyPDF.contains("serif"))
				{
					fontFamilyPDF = "FreeSerif";
				} else if (fontFamilyPDF.contains("fantasy"))
				{
					fontFamilyPDF = "FreeSansBold";
				} else if (fontFamilyPDF.contains("cursive"))
				{
					fontFamilyPDF = "FreeSans";
					fontStylePDF = "italic";
				} else {
					fontFamilyPDF = "FreeSans";
				}
			}
			
			if (fontFamilyPDF != null && fontFamilyPDF.length() > 0) s.append("font-family: ").append(fontFamilyPDF).append("; \n");
			if (fontStylePDF != null && fontStylePDF.length() > 0) s.append("font-style: ").append(fontStylePDF).append("; \n");
		} else {
			if (fontFamily != null && fontFamily.length() > 0) s.append("font-family: ").append(fontFamily).append("; \n");
			if (fontStyle != null && fontStyle.length() > 0) s.append("font-style: ").append(fontStyle).append("; \n");
		}		
		
		if (fontSize != null && fontSize.length() > 0) s.append("font-size: ").append(fontSize).append("; \n");
		if (fontWeight != null && fontWeight.length() > 0) s.append("font-weight: ").append(fontWeight).append("; \n");
	
		
		s.append("} ");
		s.append("\n\n");
		
		return s.toString();
	}	
	
}
