package com.ec.survey.tools;

import java.util.Date;

import com.ec.survey.model.Skin;
import com.ec.survey.model.administration.User;

public class SkinCreator {
	public static Skin createDefaultSkin(User owner)
	{
		Skin s = new Skin();
		s.setName("EUSurvey.css");
		s.setIsPublic(true);
		s.setOwner(owner);
		s.setUpdateDate(new Date());
		
		s.setElementValue(".sectiontitle", "color", "67AA03");
		s.setElementValue(".sectiontitle", "font-size", "20px");
		
		s.setElementValue(".questiontitle", "color", "333");
		s.setElementValue(".questiontitle", "font-size", "13px");
		s.setElementValue(".questiontitle", "font-weight", "normal");
		
		s.setElementValue(".answertext", "color", "333");
		
		s.setElementValue(".questionhelp", "color", "A6A6A6");
		s.setElementValue(".questionhelp", "font-size", "11px");
		
		s.setElementValue(".surveytitle", "font-size", "24px");
		
		s.setElementValue(".linkstitle", "color", "333");
		s.setElementValue(".linkstitle", "font-size", "16px");
		s.setElementValue(".linkstitle", "font-weight", "bold");
		
		s.setElementValue(".info-box", "background-color", "FDF5D9");
		s.setElementValue(".info-box", "font-size", "13px");
		
		s.setElementValue(".runner-content", "background-color", "FFF");
		
		return s;
	}
	
	public static Skin createNewDefaultSkin(User owner)
	{
		Skin s = new Skin();
		s.setName("EUSurveyNew.css");
		s.setIsPublic(true);
		s.setOwner(owner);
		s.setUpdateDate(new Date());
		
		s.setElementValue(".sectiontitle", "color", "004F98");
		s.setElementValue(".sectiontitle", "font-size", "20px");
		
		s.setElementValue(".questiontitle", "color", "333");
		s.setElementValue(".questiontitle", "font-size", "13px");
		s.setElementValue(".questiontitle", "font-weight", "normal");
		
		s.setElementValue(".answertext", "color", "333");
		
		s.setElementValue(".questionhelp", "color", "A6A6A6");
		s.setElementValue(".questionhelp", "font-size", "11px");
		
		s.setElementValue(".surveytitle", "font-size", "24px");
		
		s.setElementValue(".linkstitle", "color", "333");
		s.setElementValue(".linkstitle", "font-size", "16px");
		s.setElementValue(".linkstitle", "font-weight", "bold");
		
		s.setElementValue(".info-box", "background-color", "FDF5D9");
		s.setElementValue(".info-box", "font-size", "13px");
		
		s.setElementValue(".runner-content", "background-color", "FFF");
		
		return s;
	}
}
