package com.ec.survey.controller;

import com.ec.survey.model.Paging;
import com.ec.survey.model.SqlPagination;
import com.ec.survey.model.SurveyFilter;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.SessionService;
import com.ec.survey.service.SurveyService;
import com.ec.survey.service.mapping.PaginationMapper;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Assert;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/springapp.xml")
public class TestSurveyController extends AbstractTestNGSpringContextTests {

	@InjectMocks
	private SurveyController surveyController;
	
	@Mock
	private SurveyService surveySvc;
	
	@Mock
	private SessionService sessionService;
	
	@Mock
	private PaginationMapper paginationMapper;	
	
	private MockMvc mockMvc;
	
	private MockHttpServletRequest request;
	
	
  @SuppressWarnings("deprecation")
@Test
  public void testGetSurvey() throws Exception {
	 
	  Survey s1 = new Survey();
	  s1.setTitle("title 1");
	  s1.setShortname("a000-b000-c000-d000");
	 SurveyFilter svf= new SurveyFilter();
	 User usr = new User();
	 usr.setId(7);
	 usr.setLogin("userlogin");
	 svf.setUser(usr);
	 	 
	 request.setAttribute("USER", usr);
	 // Setup all expected values that has to be test with the result of this action to perform 
	 Mockito.when(sessionService.getCurrentUser(Mockito.any(HttpServletRequest.class))).thenReturn(usr);
	 Mockito.when(sessionService.getSurveyFilter(Mockito.any(HttpServletRequest.class), Mockito.anyBoolean())).thenReturn(svf);
	 Mockito.when(surveySvc.getSurveysIncludingTranslationLanguages(Mockito.any(SurveyFilter.class), Mockito.any(SqlPagination.class), false)).thenReturn(Collections.singletonList(s1));
	 Mockito.when(paginationMapper.toSqlPagination(Mockito.any(Paging.class))).thenReturn(new SqlPagination(1,1));
	 try {
		 	// perform the action  
		 	ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/forms"));
		 	
		 	// check the values from the simulation
			result.andExpect(MockMvcResultMatchers.view().name("forms/forms")).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.forwardedUrl("forms/forms"))
					// check that we have well a Paging Object 
					.andExpect(MockMvcResultMatchers.model().attribute("paging", 
																		Matchers.instanceOf(Paging.class)))
					// check that the Paging object contain 1 Survey as set as the setup level
					.andExpect(MockMvcResultMatchers.model().attribute("paging", 
																		Matchers.hasProperty("items",Matchers.hasSize(1))))
					// check that the title of the survey is well the expcted one
					.andExpect(MockMvcResultMatchers.model().attribute("paging", 
																		Matchers.hasProperty("items",IsIterableContainingInAnyOrder.<Survey> containsInAnyOrder(Matchers.hasProperty("title",Matchers.is(s1.getTitle()))))));				
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	Assert.assertTrue(true);
	 
  }
  
  @BeforeClass
  public void beforeClass() {
	  request =new MockHttpServletRequest();	  
	  MockitoAnnotations.initMocks(this);
	  mockMvc = MockMvcBuilders.standaloneSetup(surveyController).build();
  }

  @BeforeTest
  public void beforeTest() {
  }

}
