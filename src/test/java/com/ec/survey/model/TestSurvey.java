package com.ec.survey.model;

import com.ec.survey.model.survey.Survey;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Date;

public class TestSurvey {
	private Survey surveyMaster;
	private String title="Master Survey";
	private String shortName="A000-B111-C222-D333-E444";
	private Date dateCreation = new DateTime(2014,8,19,10,0).toDate();
	private Language lg = new Language("en","English","EnglishName",true);
	private com.ec.survey.model.administration.User  owner = new com.ec.survey.model.administration.User();
	
	
  @Test
  public void checkDefaultValue() {
	  Reporter.log("117 - Security running");
	  Assert.assertEquals(surveyMaster.getTitle(), title,"The expected value is not matching with the actual title");
	  Assert.assertEquals(surveyMaster.getShortname(), shortName,"The expected value is not matching with the actual shortName");
	  Assert.assertEquals(surveyMaster.getCreated(), dateCreation,"The expected value is not matching with the actual Date");

  }
  @AfterClass
  public void afterClass() {
  }

  @BeforeTest
  public void beforeTest() {
	  // init the master Survey to check for the copy and the elemnts 
	  owner.setLogin("userlogin");
	  owner.setPassword("password");
	  surveyMaster = new Survey();
	  surveyMaster.setTitle(title);
	  surveyMaster.setShortname(shortName);
	  surveyMaster.setCreated(dateCreation);	  
	  surveyMaster.setLanguage(lg);
	  surveyMaster.setOwner(owner);
  }

}
