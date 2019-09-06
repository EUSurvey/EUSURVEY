package com.ec.survey.service;

import com.ec.survey.model.Language;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Survey;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

//Both two config webapp specify the root where find configuration 
@SuppressWarnings("deprecation")
@WebAppConfiguration("src/test/resources")
@ContextConfiguration("/WEB-INF/spring/mvc-dispatcher-servlet.xml")
@TransactionConfiguration()
@PersistenceContext()
@Transactional(propagation=Propagation.NESTED)
@Test(groups={"broken"})
public class TestSurveyService extends AbstractTransactionalTestNGSpringContextTests {
	
	@Mock
	private SurveyService svc;
	
	private String userLogin="usrutest";
	private String userEmail="usrutest@ec.eu";
	private String userGivenName="User Unit Test";
	private User userCreation;
		
	private String languageCode="en";
	private String languageEnName="English";
	private String languageName="English";
	private Language languageCreation;

	// this will serve to test multiple cases when creating a survey
	// eg missing mandatory value
	
	private Survey surveyToCreate = new Survey();
	private String surveyTitle="Title Unit Test " + DateTime.now().toString("YYYY-MM-ddHHmmss");
	@SuppressWarnings("unused")
	private String surveyUniqueId=UUID.randomUUID().toString();
	private String surveySecurityMode="open";
	private String surveyShortName="New Survey unit test";
	
	
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	@Test(groups={"broken"})
	public void setDataSource(DataSource dataSource) {
		// TODO Auto-generated method stub
		super.setDataSource(dataSource);
		System.out.println("SETDATASOURCE OK");
	}


	
	public TestSurveyService() {
		super();
	}


	/***
	 * This method is called before each test method has to run
	 * We will set here the init of the DB data --> rollback after each method is done
	 * @param mtdh
	 */
	@BeforeMethod()
	public void prepareDBData(java.lang.reflect.Method mtdh){
				
		System.out.println("STARTING prepareDBData");	
		createUser();
		createLanguage();
		
		
		switch (mtdh.getName()) {
		case "createSurveyWithLanguageOk":
			createSurvey(false,UUID.randomUUID().toString());
			break;
		case "createSurveyWithoutLanguageRaisingConstraintException":
			createSurvey(true,UUID.randomUUID().toString());
		case "getSurveyFromDb":
			createSurvey(false,UUID.randomUUID().toString());
			break;
		default:
			System.out.println("prepareDBData for method " + mtdh.getName());
			break;
		}
		
	}

	private void createSurvey(Boolean surveyWithException, String surveyUID){
		
		surveyToCreate = new Survey();
		//surveyToCreate.setId(7);
		surveyToCreate.setUniqueId(surveyUID);
		surveyToCreate.setTitle(surveyTitle);
		surveyToCreate.setTitleSort(surveyTitle);
		surveyToCreate.setSecurity(surveySecurityMode);
		
		surveyToCreate.setOwner(userCreation);
		surveyToCreate.setShortname(surveyShortName);
		
		// if want exception raised then avoid to set the language mandatory field
		if (!surveyWithException)
			surveyToCreate.setLanguage(languageCreation);
		
		surveyToCreate.setIsDraft(true);		
		surveyToCreate.setContact(userCreation.getEmail());
					
		surveyToCreate.getPublication().setAllContributions(true);
		surveyToCreate.getPublication().setAllQuestions(true);
		surveyToCreate.getPublication().setShowCharts(true);
		surveyToCreate.getPublication().setShowContent(true);
		surveyToCreate.getPublication().setShowStatistics(true);
		surveyToCreate.getPublication().setShowSearch(true);
								
		surveyToCreate.getPublication().getFilter().getVisibleQuestions().clear();
		surveyToCreate.getPublication().getFilter().getFilterValues().clear();											
	}


	private void createLanguage() {
		String sql="FROM Language l WHERE l.code=:lg_code";
		Query qry = sessionFactory.getCurrentSession().createQuery(sql);
		qry.setParameter("lg_code", languageCode);
		
		// check if exist
		if (qry.uniqueResult() ==null){
			sql="INSERT INTO eusurveydb.languages(LANGUAGE_ID,LANGUAGE_CODE,LANGUAGE_ENNAME,LANGUAGE_NAME,LANGUAGE_OFFI) SELECT MAX(LANGUAGE_ID) ,:lg_code,:lg_enname,:lg_name,1 FROM eusurveydb.languages";						
			qry = sessionFactory.getCurrentSession().createSQLQuery(sql);
			qry.setParameter("lg_code", languageCode);
			qry.setParameter("lg_enname", languageEnName);
			qry.setParameter("lg_name", languageName);
			qry.executeUpdate();			

			sql="FROM Language l WHERE l.code=:lg_code";
			qry = sessionFactory.getCurrentSession().createQuery(sql);
			qry.setParameter("lg_code", languageCode);
			if(qry.uniqueResult() !=null){
				languageCreation = (Language)qry.uniqueResult();
			}else{
				languageCreation=null;
			}
			
		}else{
			languageCreation = (Language)qry.uniqueResult();

		}
	}


	/***
	 * Create a user for the unit test 
	 */
	private void createUser() {
		
		// check first if user with this login is existing
		String sqlUser ="FROM User u where u.login=:usr_login";
		Query qryUsr = sessionFactory.getCurrentSession().createQuery(sqlUser);
		qryUsr.setParameter("usr_login", userLogin);
		if(qryUsr.uniqueResult()==null){
			System.out.println("createUser -- USER CREATED PREPARE STATMENT TO INSERT USER " );
			sqlUser="INSERT INTO eusurveydb.users (USER_ID,USER_EMAIL,USER_LOGIN,USER_GIVENNAME,VALIDATED) SELECT MAX(user_id)+1,:usr_email, :usr_login,:usr_givenname,1 FROM eusurveydb.users";
			qryUsr = sessionFactory.getCurrentSession().createSQLQuery(sqlUser);
			qryUsr.setParameter("usr_login", userLogin);
			qryUsr.setParameter("usr_email", userEmail);
			qryUsr.setParameter("usr_givenname", userGivenName);
			
			qryUsr.executeUpdate();
			
			System.out.println("createUser -- USER CREATED INSERT USER DONE" );
			sqlUser ="FROM User u WHERE u.login=:usr_login";
			qryUsr = sessionFactory.getCurrentSession().createQuery(sqlUser);
			qryUsr.setParameter("usr_login", userLogin);
			
			System.out.println("createUser -- GET INSERTED USER " + qryUsr.getQueryString());
			@SuppressWarnings("unchecked")
			List<User> lstUsr = qryUsr.list();
			if(lstUsr==null){
				System.out.println("createUser -- NO INSERTED USER " );
			}else {
				for (User user : lstUsr) {
					System.out.println("createUser -- DATA USER " + user.getLogin() +" " + user.getEmail());
					userCreation =user;
				}
			}
		}else{
			userCreation =(User) qryUsr.uniqueResult();
			System.out.println("createUser -- USER ALREADY EXIST " + userCreation.getGivenName());
		}
	}
	

	// check that at least 
	@Test()
	public void createSurveyWithoutLanguageRaisingConstraintException(){
				
		System.out.println("*******CALLING createSurveyWithoutLanguageRaisingConstraintException");
			
		try {
			
			@SuppressWarnings("unused")
			Survey surveyCreated =svc.add(surveyToCreate,-1);
			
			// if here ==> error because mandatory field is missing
			Assert.assertFalse("FAILED THIS TEST CANNOT BE OK DUE TO MISSING MANDATORY VALUE 'LANGUAGE'",false);
			
		} catch (ConstraintViolationException e) {
			Assert.assertTrue(e instanceof ConstraintViolationException);
			Assert.assertTrue(StringUtils.contains(e.getSQLException().getMessage(), "Column 'LANGUAGE' cannot be null"));
		}		
	}

	@Test(groups={"dbFunction"})
	public void createSurveyWithLanguageOk(){
				
		System.out.println("*******CALLING createSurveyWithLanguageOk");												
		Survey surveyCreated =svc.add(surveyToCreate,-1);			
		Assert.assertSame(surveyToCreate, surveyCreated);
	}

	@Test()	
	public void getSurveyFromDb() {
		
		
		System.out.println("getSurveyFromDb COMPARE SURVEY CREATED " + surveyToCreate.getTitle() +" ID - " +  surveyToCreate.getId());
		// first add the survey to the db
		Survey surveyCreated= svc.add(surveyToCreate,-1);
		surveyToCreate.setId(surveyCreated.getId());
		
		// get now from the service
		Survey surveyFromService =svc.getSurvey(surveyToCreate.getId(),"en");
		// if found
		if(surveyFromService!=null){
			Survey surveyByUniqueId= svc.getSurveyByUniqueId(surveyFromService.getUniqueId(), false, true);

			System.out.println("getSurveyFromDb COMPARE SURVEY FROM SERVICE " + surveyByUniqueId.getTitle());
			if( sessionFactory==null)
				Assert.fail("session factroy is null" );
			
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery("FROM Survey s WHERE s.uniqueId = :uid ORDER BY s.id DESC").setString("uid", surveyFromService.getUniqueId());
			
			@SuppressWarnings("unchecked")
			List<Survey> list = query.list();
			if (list.size() > 0)
			{
				System.out.println("Found survey " + list.size());
				Assert.assertTrue(true);			
			}else
			{
				Assert.assertFalse("Error on MySql sql statement ", false);
			}

			Assert.assertTrue("Survey not the same as Expected ", surveyFromService.getId().equals(surveyToCreate.getId()));
			Assert.assertSame(surveyByUniqueId, surveyFromService);
			
		}else{
			System.out.println("No Survey found in DB with id " +surveyToCreate.getId());
			Assert.fail("No Survey Found");
		}
			
	}
	

}
