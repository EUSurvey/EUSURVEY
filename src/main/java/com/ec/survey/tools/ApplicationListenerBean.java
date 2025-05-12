package com.ec.survey.tools;

import com.ec.survey.controller.HomeController;
import com.ec.survey.model.*;
import com.ec.survey.model.administration.Role;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.*;

public class ApplicationListenerBean implements ApplicationListener<ContextRefreshedEvent> {
	
	private static final Logger logger = Logger.getLogger(ApplicationListenerBean.class);

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event != null) {
			
			ApplicationContext applicationContext = event.getApplicationContext();
            
            //the event is thrown twice (one for each context), so we only count the last one
            if (applicationContext.getId().endsWith("dispatcher"))
            {           	
                HomeController homeController = (HomeController) applicationContext.getBean("homeController");
	            
	            AdministrationService administrationService = (AdministrationService) applicationContext.getBean("administrationService");
	            SurveyService surveyService = (SurveyService) applicationContext.getBean("surveyService");
	            SchemaService schemaService = (SchemaService) applicationContext.getBean("schemaService");
	            SkinService skinService = (SkinService) applicationContext.getBean("skinService");
	            FileService fileService = (FileService) applicationContext.getBean("fileService");
	                   
	            boolean showEcas=false;
	            showEcas =( homeController.isShowEcas() || homeController.isCasOss());
	            
	            initializeDatabase(administrationService, surveyService, schemaService, skinService, homeController.servletContext, homeController.fileDir, homeController.createStressData != null && homeController.createStressData.equalsIgnoreCase("1"), showEcas, homeController.sender, fileService, homeController.createStressData != null && homeController.createStressData.equalsIgnoreCase("2"));
	        
	        	TaskUpdater taskWorker = (TaskUpdater) applicationContext.getBean("taskWorker");
	        	taskWorker.run();
	        }
        }		
	}
	
	public static void initializeDatabase(AdministrationService administrationService, SurveyService surveyService, SchemaService schemaService, SkinService skinService, ServletContext servletContext, String fileDir, boolean createStressTestData, boolean showecas, String sender, FileService fileService, boolean createNewStressTestData)
	{
	    java.io.File folder = new java.io.File(fileDir);
        if (!folder.exists()) folder.mkdirs();
		List<Role> result = administrationService.getAllRoles();
		if (result.isEmpty()) {
			logger.info("InitializeDatabase No Roles create basic rule with showecas " + showecas);
			RolesCreator.createBasicRoles(administrationService, showecas);
			try {
				UsersCreator.createDefaultUsers(administrationService, createStressTestData || createNewStressTestData, sender);
			} catch (Exception e1) {
				logger.error(e1);
			}
			
			List<Language> langs = surveyService.getLanguages();
			if (langs.isEmpty())
			{
				langs = SurveyCreator.createBasicLanguages();
				surveyService.saveLanguages(langs);		
			}
			
			surveyService.createStatus(4);
			
			try {
			
				User admin = administrationService.getUserForLoginAndInitialize(administrationService.getAdminUser(), false);
				
				//default skin
				Skin s = SkinCreator.createDefaultSkin(admin);
				skinService.save(s);
				
				Language objLang = surveyService.getLanguage("EN");
				
				updateSchema(schemaService, servletContext);
				
				if (createStressTestData)
				{
					User analyst = administrationService.getUserForLogin(administrationService.getStressUser(), false);
					SurveyCreator.createStressTestSurvey(analyst, servletContext, fileDir, surveyService, fileService);
				}
				
				if (createStressTestData || createNewStressTestData)
				{
					User analyst = administrationService.getUserForLogin(administrationService.getStressUser(), false);
					SurveyCreator.createStressTestSurveys(analyst, servletContext, fileDir, surveyService, fileService);
				}
				
				Survey survey = SurveyCreator.createDemoSkinSurvey(admin, objLang);
				surveyService.add(survey, -1);
				surveyService.publish(survey, -1, -1, false, -1, false, false);
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
						
		} else {
			//update schema
			try {
				updateSchema(schemaService, servletContext);
			} catch (IOException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}
		
	}
	
	private static void updateSchema(SchemaService schemaService, ServletContext servletContext) throws IOException {
		
		Status status = schemaService.getStatus();		
		
		if (status == null)
		{
			schemaService.step1();
			status = schemaService.getStatus();
		} 
		
		if (status.getDbversion() < 2)
		{
			schemaService.step2();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 4)
		{
			schemaService.step4();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 5)
		{
			schemaService.step5();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 6)
		{
			schemaService.step6();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 7)
		{
			schemaService.step7();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 8)
		{
			schemaService.step8();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 10)
		{
			schemaService.step10();
			status = schemaService.getStatus();
		}	
		
		if (status.getDbversion() < 11)
		{
			schemaService.step11();
			status = schemaService.getStatus();
		}	
		
		if (status.getDbversion() < 12)
		{
			schemaService.step12();
			status = schemaService.getStatus();
		}	
		
		if (status.getDbversion() < 15)
		{
			schemaService.step15();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 16)
		{
			schemaService.step16();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 17)
		{
			schemaService.step17();
			status = schemaService.getStatus();
		}
		
		// step 18 removed as not needed anymore
		
		if (status.getDbversion() < 19)
		{
			schemaService.step19();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 20)
		{
			logger.info("starting upgrade step 20");
			schemaService.step20();
			status = schemaService.getStatus();
		}	
		
		if (status.getDbversion() < 21)
		{
			schemaService.step21();
			status = schemaService.getStatus();
		}

		//step 22 removed
		
		if (status.getDbversion() < 23)
		{
			schemaService.step23();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 24)
		{
			schemaService.step24();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 25)
		{
			schemaService.step25();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 26)
		{
			schemaService.step26();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 27)
		{				
			schemaService.step27(servletContext);
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 28)
		{				
			schemaService.step28(servletContext);
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 29)
		{
			schemaService.step29();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 30)
		{						
			schemaService.step30();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 31)
		{
			schemaService.step31();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 32)
		{
			schemaService.step32();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 33)
		{
			schemaService.step33();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 34)
		{
			schemaService.step34();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 35)
		{
			schemaService.step35();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 36)
		{
			schemaService.step36();
			status = schemaService.getStatus();
		}
					
		if (status.getDbversion() < 37)
		{
			schemaService.step37();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 38)
		{
			schemaService.step38();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 39)
		{
			schemaService.step39();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 40)
		{
			schemaService.step40();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 41)
		{
			schemaService.step41();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 42)
		{
			logger.info("starting upgrade step 42");
			schemaService.step42();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 43)
		{
			logger.info("starting upgrade step 43");
			schemaService.step43();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 44)
		{
			logger.info("starting upgrade step 44");
			schemaService.step44();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 45)
		{
			logger.info("starting upgrade step 45");
			schemaService.step45();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 46)
		{
			logger.info("starting upgrade step 46");
			schemaService.step46();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 47)
		{
			logger.info("starting upgrade step 47");
			schemaService.step47();
			status = schemaService.getStatus();
		}
		
		// step 48 removed as not needed anymore
		
		if (status.getDbversion() < 49)
		{
			logger.info("starting upgrade step 49");
			schemaService.step49();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 50)
		{
			logger.info("starting upgrade step 50");
			schemaService.step50();
			status = schemaService.getStatus();
		}
	
		if (status.getDbversion() < 51)
		{
			logger.info("starting upgrade step 51");
			schemaService.step51();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 52)
		{
			logger.info("starting upgrade step 52");
			schemaService.step52();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 53)
		{
			logger.info("starting upgrade step 53");
			schemaService.step53();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 54)
		{
			logger.info("starting upgrade step 54");
			schemaService.step54();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 55)
		{
			logger.info("starting upgrade step 55");
			schemaService.step55();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 56)
		{
			logger.info("starting upgrade step 56");
			schemaService.step56();
			status = schemaService.getStatus();
		}
		
		logger.info("special update for OSS release start once again step 42");
		schemaService.createAnswerFullTextForOss();

		if (status.getDbversion() < 57)
		{
			logger.info("starting upgrade step 57");
			schemaService.step57();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 58)
		{
			logger.info("starting upgrade step 58");
			schemaService.step58();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 59)
		{
			logger.info("starting upgrade step 59");
			schemaService.step59();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 60)
		{
			logger.info("starting upgrade step 60");
			schemaService.step60();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 61)
		{
			logger.info("starting upgrade step 61");
			schemaService.step61();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 62)
		{
			logger.info("starting upgrade step 62");
			schemaService.step62();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 63)
		{
			logger.info("starting upgrade step 63");
			schemaService.step63();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 64)
		{
			logger.info("starting upgrade step 64");
			schemaService.step64();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 65)
		{
			logger.info("starting upgrade step 65");
			schemaService.step65();
			status = schemaService.getStatus();
		}

		if (status.getDbversion() < 66)
		{
			logger.info("starting upgrade step 66");
			schemaService.step66();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 67)
		{
			logger.info("starting upgrade step 67");
			schemaService.step67();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 68)
		{
			logger.info("starting upgrade step 68");
			schemaService.step68();
			status = schemaService.getStatus();
		}

		if (status.getDbversion() < 69)
		{
			logger.info("starting upgrade step 69");
			schemaService.step69();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 70)
		{
			logger.info("starting upgrade step 70");
			schemaService.step70();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 71)
		{
			logger.info("starting upgrade step 71");
			schemaService.step71();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 72)
		{
			logger.info("starting upgrade step 72");
			schemaService.step72();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 73)
		{
			logger.info("starting upgrade step 73");
			schemaService.step73();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 74)
		{
			logger.info("starting upgrade step 74");
			schemaService.step74();
			status = schemaService.getStatus();
		}	
		
		if (status.getDbversion() < 75)
		{
			logger.info("starting upgrade step 75");
			schemaService.step75();
			status = schemaService.getStatus();
		}	
		
		if (status.getDbversion() < 76)
		{
			logger.info("starting upgrade step 76");
			schemaService.step76();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 77)
		{
			logger.info("starting upgrade step 77");
			schemaService.step77();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 78)
		{
			logger.info("starting upgrade step 78");
			schemaService.step78();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 79)
		{
			logger.info("starting upgrade step 79");
			schemaService.step79();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 80)
		{
			logger.info("starting upgrade step 80");
			schemaService.step80();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 81)
		{
			logger.info("starting upgrade step 81");
			schemaService.step81();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 82)
		{
			logger.info("starting upgrade step 82");
			schemaService.step82();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 83)
		{
			logger.info("starting upgrade step 83");
			schemaService.step83();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 84)
		{
			logger.info("starting upgrade step 84");
			schemaService.step84();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 85)
		{
			logger.info("starting upgrade step 85");
			schemaService.step85(servletContext);
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 86)
		{
			logger.info("starting upgrade step 86");
			schemaService.step86();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 87)
		{
			logger.info("starting upgrade step 87");
			schemaService.step87();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 88)
		{
			logger.info("starting upgrade step 88");
			schemaService.step88();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 89)
		{
			logger.info("starting upgrade step 89");
			schemaService.step89();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 90)
		{
			logger.info("starting upgrade step 90");
			schemaService.step90();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 91)
		{
			logger.info("starting upgrade step 91");
			schemaService.step91();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 92)
		{
			logger.info("starting upgrade step 92");
			schemaService.step92();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 93)
		{
			logger.info("starting upgrade step 93");
			schemaService.step93();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 94)
		{
			logger.info("starting upgrade step 94");
			schemaService.step94();
			status = schemaService.getStatus();
		}

		if (status.getDbversion() < 95)
		{
			logger.info("starting upgrade step 95");
			schemaService.step95();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 96)
		{
			logger.info("starting upgrade step 96");
			schemaService.step96();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 97)
		{
			logger.info("starting upgrade step 97");
			schemaService.step97();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 98)
		{
			logger.info("starting upgrade step 98");
			schemaService.step98();
			status = schemaService.getStatus();
		}

		if (status.getDbversion() < 99)
		{
			logger.info("starting upgrade step 99");
			schemaService.step99();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 100)
		{
			logger.info("starting upgrade step 100");
			schemaService.step100();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 101)
		{
			logger.info("starting upgrade step 101");
			schemaService.step101();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 102)
		{
			logger.info("starting upgrade step 102");
			schemaService.step102();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 103)
		{
			logger.info("starting upgrade step 103");
			schemaService.step103();
			status = schemaService.getStatus();
		}

		if (status.getDbversion() < 104)
		{
			logger.info("starting upgrade step 104");
			schemaService.step104();
			status = schemaService.getStatus();
		}
		
		schemaService.step105a(); // this step creates settings if they do not exist yet

		if (status.getDbversion() < 105)
		{
			logger.info("starting upgrade step 105");
			schemaService.step105();
			status = schemaService.getStatus();
		}
		
		schemaService.stepAssertAutomaticDraftDeleteExceptions();

		if (status.getDbversion() < 106)
		{
			logger.info("starting upgrade step 106");
			schemaService.step106();
			status = schemaService.getStatus();
		}

		if (status.getDbversion() < 107){
			logger.info("starting upgrade step 107");
			schemaService.step107();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 108){
			logger.info("starting upgrade step 108");
			schemaService.step108();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 109){
			logger.info("starting upgrade step 109");
			schemaService.step109();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 110){
			logger.info("starting upgrade step 110");
			schemaService.step110();
			status = schemaService.getStatus();
		}

		if (status.getDbversion() < 111){
			logger.info("starting upgrade step 111");
			schemaService.step111();
			status = schemaService.getStatus();
		}

		if (status.getDbversion() < 112){
			logger.info("starting upgrade step 112");
			schemaService.step112();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 113){
			logger.info("starting upgrade step 113");
			schemaService.step113();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 114){
			logger.info("starting upgrade step 114");
			schemaService.step114();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 115){
			logger.info("starting upgrade step 115");
			schemaService.step115();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 116){
			logger.info("starting upgrade step 116");
			schemaService.step116();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 117){
			logger.info("starting upgrade step 117");
			schemaService.step117();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 118){
			logger.info("starting upgrade step 118");
			schemaService.step118();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 119){
			logger.info("starting upgrade step 119");
			schemaService.step119();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 120){
			logger.info("starting upgrade step 120");
			schemaService.step120();
			status = schemaService.getStatus();
		}

		if (status.getDbversion() < 121){
			logger.info("starting upgrade step 121");
			schemaService.step121();
			status = schemaService.getStatus();
		}
		
		if (status.getDbversion() < 122){
			logger.info("starting upgrade step 122");
			schemaService.step122();
			status = schemaService.getStatus();
		}

		if (status.getDbversion() < 123){
			logger.info("starting upgrade step 123");
			schemaService.step123();
			status = schemaService.getStatus();
		}

		if (status.getDbversion() < 124){
			logger.info("starting upgrade step 124");
			schemaService.step124();
			status = schemaService.getStatus();
		}

		if (status.getDbversion() < 125){
			logger.info("starting upgrade step 125");
			schemaService.step125();
			status = schemaService.getStatus();
		}

		// the new settings are only created if they do not exist yet
		schemaService.CreateLimitsForExternals();
	}

	public static Survey createSurvey(int answerCount, User user, Language objLang, SurveyService surveyService, AnswerService answerService, String fileDir, boolean init, MessageSource resources, Locale locale, Integer questions, ArchiveService archiveService, BeanFactory context,TaskExecutor taskExecutor, FileService fileService) throws Exception {
		Survey survey = SurveyCreator.createDummySurvey(user, objLang, init, questions);
		survey.setListForm(true);
		survey.getPublication().setShowContent(true);
		survey.getPublication().setShowStatistics(true);
		survey.getPublication().setShowCharts(true);
		survey.getPublication().setShowSearch(true);
	
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 2);
		survey.setEnd(cal.getTime());
		
		survey = surveyService.add(survey, -1);		
		
		surveyService.publish(survey, -1, -1, false, user.getId(), false, false);			
		createDummyAnswers(survey.getShortname(), answerCount, user, fileDir, answerService, surveyService, false, resources, locale, fileService);
		
		return survey;
	}
	
	public static void createDummyAnswers(String shortname, int answerCount, User user, String fileDir, AnswerService answerService, SurveyService surveyService, boolean validate, MessageSource resources, Locale locale, FileService fileService) throws Exception
	{
		if (answerCount <= 0) return;
		
		Survey psurvey = surveyService.getSurvey(shortname, false, false, false, true, null, true, false);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		
		for (int j = 0; j < answerCount; j++) {
			AnswerSet answerSet = SurveyCreator.createDummyAnswerSet(psurvey, user);
			
			if (psurvey.getIsEVote()) {
				answerSet.setDate(cal.getTime());
				cal.add(Calendar.MINUTE, -10);
			}
			
			if (validate)
			{
				Set<String> invisibleElements = new HashSet<>();
				SurveyHelper.validateAnswerSet(answerSet,answerService,invisibleElements, resources, locale, null, null, true, null, fileService);
			}
			
			saveAnswerSet(answerSet, fileDir, answerService, null);
		}
	}
	
	@Transactional
	public static void saveAnswerSet(AnswerSet answerSet, String fileDir, AnswerService answerService, String draftid) throws Exception {
		boolean saved = false;
		
		int counter = 1;
		
		while(!saved)
		{
			try {
				answerService.internalSaveAnswerSet(answerSet, fileDir, draftid, false, true);
				saved = true;
			} catch (org.hibernate.exception.LockAcquisitionException ex)
			{
				logger.info("lock on answerSet table catched; retry counter: " + counter);
				counter++;
								
				if (counter > 60)
				{
					logger.error(ex.getLocalizedMessage(), ex);
					throw ex;
				}
				
				Thread.sleep(1000);
			}
		}		
	}

}
