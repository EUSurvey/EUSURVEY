package com.ec.survey.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@WebAppConfiguration
/*
 * The mere presence of @WebAppConfiguration on a test class ensures that a
 * WebApplicationContext (web.xml) will be loaded for the test, using the default value of
 * "file:src/main/webapp" for the path to the root of the web application (i.e.,
 * the resource base path)
 */
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/mvc-dispatcher-servlet.xml" })
public class TestSurveyControllerRefactored extends AbstractTransactionalTestNGSpringContextTests {
	@Autowired
	WebApplicationContext wac;

	private MockMvc mockMvc;

	@BeforeClass
	public void setup() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).dispatchOptions(false).build();
	}

	@Test
	public void myFirstTest() throws Exception {
		this.mockMvc.perform(get("/home/about")).andDo(print()).andExpect(status().is3xxRedirection());
	}

}