package com.ec.survey.tools;

import com.ec.survey.model.*;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.*;
import com.ec.survey.service.FileService;
import com.ec.survey.service.SurveyService;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

public class SurveyCreator {

	static int SurveyCounter = 1;
	private static final Logger logger = Logger.getLogger(SurveyCreator.class);
	
	public static Survey createNewSelfRegistrationSurvey(User owner, Language la, List<Language> langs)
	{
		Survey survey = new Survey();
		survey.setContact(owner.getEmail());
		survey.setIsDraft(true);
		survey.setCreated(new Date());
		survey.setOwner(owner);
		survey.setSecurity("open");
		survey.setShortname("NewSelfRegistrationSurvey");
		survey.setLanguage(la);
		survey.setListForm(false);
		survey.setTitle("Register for EUSurvey!");
		survey.setCaptcha(true);
		
		FreeTextQuestion username = new FreeTextQuestion("Your login", "name", UUID.randomUUID().toString());
		username.setNumRows(1);
		username.setPosition(1);
		username.setOptional(false);
		username.setHelp("The user name must be unique and cannot contain blanks. You can use your email address or any other text that contains only numbers and small/upper characters.");
		survey.getElements().add(username);
		
		FreeTextQuestion firstname = new FreeTextQuestion("Your first name", "firstname", UUID.randomUUID().toString());
		firstname.setNumRows(1);
		firstname.setPosition(2);
		firstname.setOptional(false);
		survey.getElements().add(firstname);
		
		FreeTextQuestion lastname = new FreeTextQuestion("Your last name", "lastname", UUID.randomUUID().toString());
		lastname.setNumRows(1);
		lastname.setPosition(3);
		lastname.setOptional(false);
		survey.getElements().add(lastname);
		
		FreeTextQuestion password = new FreeTextQuestion("Your password", "password", UUID.randomUUID().toString());
		password.setPosition(4);
		password.setOptional(false);
		password.setNumRows(1);
		password.setIsPassword(true);
		survey.getElements().add(password);
		
		EmailQuestion email = new EmailQuestion("Your email address", Constants.EMAIL, UUID.randomUUID().toString());
		email.setPosition(5);
		email.setOptional(false);
		email.setHelp("Please provide a valid email address for account validation.");
		survey.getElements().add(email);
		
		SingleChoiceQuestion language = new SingleChoiceQuestion("Your language", "language", UUID.randomUUID().toString());
		language.setPosition(6);
		language.setOptional(false);
		
		PossibleAnswer l;
		for (Language lang : langs) {
			if (lang.isOfficial())
			{
				l = new PossibleAnswer();
				l.setUniqueId(UUID.randomUUID().toString());
				l.setShortname(lang.getCode());
				l.setTitle(lang.getEnglishName());
				language.getPossibleAnswers().add(l);
			}
		}
		
		survey.getElements().add(language);
		
		return survey;
	}
	
	public static Survey createDemoSkinSurvey(User owner, Language la) {
		Survey survey = new Survey();
		survey.setContact(owner.getEmail());
		survey.setIsDraft(true);
		survey.setCreated(new Date());
		survey.setOwner(owner);
		survey.setSecurity("open");
		survey.setShortname("SkinDemo");
		survey.setLanguage(la);
		survey.setListForm(false);
		survey.setTitle("Skin Preview Survey");
		survey.setCaptcha(false);
		survey.setSectionNumbering(1);
		survey.setQuestionNumbering(1);
		
		int position = 1;
		
		Section section = new Section("This is a section", "section1", UUID.randomUUID().toString());
		section.setPosition(position++);
		section.setLevel(1);
		survey.getElements().add(section);
		
		section = new Section("This is a sub-section", "section1a", UUID.randomUUID().toString());
		section.setPosition(position++);
		section.setLevel(2);
		survey.getElements().add(section);
		
		FreeTextQuestion question = new FreeTextQuestion("This is a free text question", "free1", UUID.randomUUID().toString());
		question.setPosition(position++);
		question.setHelp("This is a help message");
		question.setOptional(false);
		question.setNumRows(1);
		survey.getElements().add(question);
		
		SingleChoiceQuestion choice = new SingleChoiceQuestion("This is a single choice question", "choice1", UUID.randomUUID().toString());
		choice.setPosition(position++);
		choice.setHelp("This is a help message");
		choice.setOptional(false);
		choice.setUseRadioButtons(true);
		for (int i = 1; i < 5; i++) {
			PossibleAnswer a = new PossibleAnswer();
			a.setUniqueId(UUID.randomUUID().toString());
			a.setPosition(i);
			a.setShortname(Constants.SHORTNAME + i);
			a.setTitle("Answer " + Integer.toString(i));
			choice.getPossibleAnswers().add(a);
		}
		survey.getElements().add(choice);
		
		Matrix matrix = new Matrix("This is a matrix", "matrix1", UUID.randomUUID().toString());
		matrix.setPosition(position);
		matrix.setColumns(4);
		matrix.setRows(3);
		matrix.setIsSingleChoice(true);
		EmptyElement dummy = new EmptyElement("empty", "empty");
		dummy.setPosition(0);
		matrix.getChildElements().add(dummy);
		for (int i = 1; i < 6; i++)
		{
			Text text = new Text("Text" + i, UUID.randomUUID().toString());
			text.setPosition(i);
			matrix.getChildElements().add(text);
		}
		survey.getElements().add(matrix);
		
		return survey;
	}
	
	public static Survey createDummyValidationSurvey(User owner, Language l)
	{
		Survey survey = new Survey();
		survey.setContact(owner.getEmail());
		survey.setIsDraft(true);
		survey.setCreated(new Date());
		survey.setOwner(owner);
		survey.setSecurity("open");
		survey.setShortname("ValidationTest");
		survey.setLanguage(l);
		survey.setTitle("Survey to test validation");
		survey.setListForm(true);
		
		int counter = 1;
		
		FreeTextQuestion f = new FreeTextQuestion("FreeText, 4 to 10 characters" , "FreeText", UUID.randomUUID().toString());
		f.setOptional(false);
		f.setNumRows(1);
		f.setMinCharacters(4);
		f.setMaxCharacters(10);
		f.setPosition(counter++);
		survey.getElements().add(f);
		
		NumberQuestion n = new NumberQuestion("Number between 1 and 10", "Number", UUID.randomUUID().toString());
		n.setOptional(false);
		n.setMin(1.0);
		n.setMax(10.0);
		n.setPosition(counter++);
		survey.getElements().add(n);
		
		MultipleChoiceQuestion mc = new MultipleChoiceQuestion("Multiple Choice between 2 and 4 answers", "MultipleChoice", UUID.randomUUID().toString());
		mc.setOptional(false);
		mc.setPosition(counter++);
		mc.setMinChoices(2);
		mc.setMaxChoices(4);
		for (int i = 0; i < 10; i++) {
			PossibleAnswer a = new PossibleAnswer();
			a.setUniqueId(UUID.randomUUID().toString());
			a.setPosition(i);
			a.setShortname(Constants.SHORTNAME + i);
			a.setTitle(Integer.toString(i));
			mc.getPossibleAnswers().add(a);
		}
		survey.getElements().add(mc);
		
		mc = new MultipleChoiceQuestion("Multiple Choice between 2 and 4 answers - Checkboxes", "MultipleChoice2", UUID.randomUUID().toString());
		mc.setOptional(false);
		mc.setUseCheckboxes(true);
		mc.setPosition(counter++);
		mc.setMinChoices(2);
		mc.setMaxChoices(4);
		for (int i = 0; i < 10; i++) {
			PossibleAnswer a = new PossibleAnswer();
			a.setUniqueId(UUID.randomUUID().toString());
			a.setPosition(i);
			a.setShortname(Constants.SHORTNAME + i);
			a.setTitle(Integer.toString(i));
			mc.getPossibleAnswers().add(a);
		}
		survey.getElements().add(mc);
		
		SingleChoiceQuestion sc = new SingleChoiceQuestion("Single Choice", "SingleChoice", UUID.randomUUID().toString());
		sc.setOptional(false);
		sc.setPosition(counter++);
		for (int i = 0; i < 10; i++) {
			PossibleAnswer a = new PossibleAnswer();
			a.setUniqueId(UUID.randomUUID().toString());
			a.setPosition(i);
			a.setShortname(Constants.SHORTNAME + i);
			a.setTitle(Integer.toString(i));
			sc.getPossibleAnswers().add(a);
		}
		survey.getElements().add(sc);
		
		sc = new SingleChoiceQuestion("Single Choice - Radio Buttons", "SingleChoice", UUID.randomUUID().toString());
		sc.setOptional(false);
		sc.setUseRadioButtons(true);
		sc.setPosition(counter++);
		for (int i = 0; i < 10; i++) {
			PossibleAnswer a = new PossibleAnswer();
			a.setUniqueId(UUID.randomUUID().toString());
			a.setPosition(i);
			a.setShortname(Constants.SHORTNAME + i);
			a.setTitle(Integer.toString(i));
			sc.getPossibleAnswers().add(a);
		}
		survey.getElements().add(sc);
		
		DateQuestion d = new DateQuestion("Date question between yesterday and tomorrow", "DateQuestion", UUID.randomUUID().toString());
		d.setOptional(false);
		d.setPosition(counter++);		
		Date now = new Date();  
		Calendar cal = Calendar.getInstance();  
		cal.setTime(now);  
		cal.add(Calendar.DAY_OF_YEAR, -1);  
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date yesterday = cal.getTime();  
		cal = Calendar.getInstance();  
		cal.setTime(now);  
		cal.add(Calendar.DAY_OF_YEAR, 2);   
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		Date tomorrow = cal.getTime();  		
		d.setMin(yesterday);
		d.setMax(tomorrow);
		survey.getElements().add(d);
		
		//Matrix
		
		Matrix matrix = new Matrix("Single Choice Matrix", "Matrix", UUID.randomUUID().toString());
		matrix.setColumns(4);
		matrix.setPosition(counter);
		matrix.setRows(3);
		matrix.setIsSingleChoice(true);
		EmptyElement dummy = new EmptyElement("empty", "empty");
		dummy.setPosition(0);
		matrix.getChildElements().add(dummy);
		for (int i = 1; i < 6; i++)
		{
			Text text = new Text("Text" + i, UUID.randomUUID().toString());
			text.setPosition(i);
			matrix.getChildElements().add(text);
		}
		survey.getElements().add(matrix);
				
		return survey;
	}
	
	public static Survey createDummyPasswordSurvey(User owner, Language l)
	{
		Survey survey = new Survey();
		survey.setContact(owner.getEmail());
		survey.setIsDraft(true);
		survey.setCreated(new Date());
		survey.setOwner(owner);
		survey.setSecurity("secured");
		survey.setPassword("password"); 
		survey.setShortname("PasswordTest");
		survey.setTitle("Password Test Survey");
		survey.setLanguage(l);
		
		Section s = new Section("Section", "section", UUID.randomUUID().toString());
		s.setLevel(1);
		s.setPosition(1);
		survey.getElements().add(s);
		
		return survey;
	}
	
	public static Survey createDummyPagingSurvey(User owner, Language l)
	{
		Survey survey = new Survey();
		survey.setContact(owner.getEmail());
		survey.setIsDraft(true);
		survey.setCreated(new Date());
		survey.setOwner(owner);
		survey.setSecurity("open");
		survey.setShortname("PagingTest");
		survey.setTitle("Paging Test Survey");
		survey.setLanguage(l);
		survey.setListForm(true);
		
		int counter = 1;
		
		for (int i = 1; i < 5; i++)
		{
			Section s = new Section("Section " + i, "section" + i, UUID.randomUUID().toString());
			s.setLevel(1);
			s.setPosition(counter++);
			survey.getElements().add(s);
			
			FreeTextQuestion f = new FreeTextQuestion("Question " + 1, "Q"+1, UUID.randomUUID().toString());
			f.setNumRows(1);
			f.setPosition(counter++);
			f.setOptional(false);
			f.setMinCharacters(1);
			f.setMaxCharacters(10);
			survey.getElements().add(f);
		}
		
		return survey;
	}
	
	public static Survey createDummyNumberingSurvey(User owner, Language la)
	{
		Survey survey = new Survey();
		survey.setContact(owner.getEmail());
		survey.setSectionNumbering(2);
		survey.setQuestionNumbering(3);
		survey.setIsDraft(true);
		survey.setCreated(new Date());
		survey.setOwner(owner);
		survey.setSecurity("open");
		survey.setShortname("NumberingTest");
		survey.setTitle("Numbering Test Survey");
		survey.setLanguage(la);
		survey.setListForm(true);
		
		int counter = 1;
		int qcounter = 1;
		
		for (int i = 1; i < 5; i++)
		{
			Section s = new Section("Section " + i, "section" + i, UUID.randomUUID().toString());
			s.setLevel(1);
			s.setPosition(counter++);
			survey.getElements().add(s);
			
			for (int j = 1; j < 5; j++)
			{
				Section s2 = new Section("Section " + i + j, "section" + i + j, UUID.randomUUID().toString());
				s2.setLevel(2);
				s2.setPosition(counter++);
				survey.getElements().add(s2);
				
				for (int k = 1; k < 5; k++)
				{
					Section s3 = new Section("Section " + i + j + k, "section" + i + j + k, UUID.randomUUID().toString());
					s3.setLevel(3);
					s3.setPosition(counter++);
					survey.getElements().add(s3);
					
					for (int l = 1; l < 5; l++)
					{
					
						FreeTextQuestion f = new FreeTextQuestion("Question " + qcounter, "Q"+qcounter, UUID.randomUUID().toString());
						qcounter++;
						f.setNumRows(1);
						f.setPosition(counter++);
						f.setOptional(false);
						f.setMinCharacters(1);
						f.setMaxCharacters(10);
						survey.getElements().add(f);
					
					}
				}
			}
			
			
			
		}
		
		return survey;
	}
	
	public static Survey createDummyUploadSurvey(User owner, Language la)
	{
		Survey survey = new Survey();
		survey.setContact(owner.getEmail());
		survey.setIsDraft(true);
		survey.setCreated(new Date());
		survey.setOwner(owner);
		survey.setSecurity("open");
		survey.setShortname("UploadTest");
		survey.setTitle("Upload Test Survey");
		survey.setLanguage(la);
		survey.setListForm(true);
		
		int counter = 1;
		
		Section s = new Section("Section", "section", UUID.randomUUID().toString());
		s.setLevel(1);
		s.setPosition(counter++);
		survey.getElements().add(s);
			
		Upload f = new Upload("Please upload a file", Constants.SHORTNAME, UUID.randomUUID().toString());
		f.setPosition(counter++);
		f.setOptional(false);
		survey.getElements().add(f);
		
		f = new Upload("Please upload another file", "shortname2", UUID.randomUUID().toString());
		f.setPosition(counter);
		f.setOptional(false);
		survey.getElements().add(f);
		
		return survey;
	}
	
	public static Survey createDummyDependencySurvey(User owner, Language la)
	{
		Survey survey = new Survey();
		survey.setContact(owner.getEmail());
		survey.setIsDraft(true);
		survey.setCreated(new Date());
		survey.setOwner(owner);
		survey.setSecurity("open");
		survey.setShortname("DependencyTest");
		survey.setTitle("Dependency Test Survey");
		survey.setLanguage(la);
		survey.setListForm(true);
		
		int counter = 1;
		
		SingleChoiceQuestion sc = new SingleChoiceQuestion("Question " + counter, "q" + counter, UUID.randomUUID().toString());
		sc.setPosition(counter);
		sc.setUseRadioButtons(true);
		PossibleAnswer p = new PossibleAnswer();
		p.setUniqueId(UUID.randomUUID().toString());
		p.setPosition(1);
		p.setShortname("p1");
		p.setTitle("Answer 1");
		sc.getPossibleAnswers().add(p);
		PossibleAnswer p2 = new PossibleAnswer();
		p2.setPosition(2);
		p2.setShortname("p2");
		p2.setTitle("Answer 2");
		sc.getPossibleAnswers().add(p2);
		survey.getElements().add(sc);
		
		counter++;
		
		SingleChoiceQuestion sc1b = new SingleChoiceQuestion("Question " + counter, "q" + counter, UUID.randomUUID().toString());
		sc1b.setPosition(counter);
		sc1b.setUseRadioButtons(true);
		PossibleAnswer p1b = new PossibleAnswer();
		p1b.setUniqueId(UUID.randomUUID().toString());
		p1b.setPosition(1);
		p1b.setShortname("p1");
		p1b.setTitle("Answer 1");
		sc1b.getPossibleAnswers().add(p1b);
		PossibleAnswer p2b = new PossibleAnswer();
		p2b.setUniqueId(UUID.randomUUID().toString());
		p2b.setPosition(2);
		p2b.setShortname("p2");
		p2b.setTitle("Answer 2");
		sc1b.getPossibleAnswers().add(p2b);
		survey.getElements().add(sc1b);
		
		counter++;
		
		SingleChoiceQuestion sc2 = new SingleChoiceQuestion("Question " + counter, "q" + counter, UUID.randomUUID().toString());
		sc2.setPosition(counter);
		sc2.setUseRadioButtons(true);
		PossibleAnswer pa = new PossibleAnswer();
		pa.setUniqueId(UUID.randomUUID().toString());
		pa.setPosition(1);
		pa.setShortname("p1");
		pa.setTitle("Answer 1");
		sc2.getPossibleAnswers().add(pa);
		PossibleAnswer pa2 = new PossibleAnswer();
		pa2.setUniqueId(UUID.randomUUID().toString());
		pa2.setPosition(2);
		pa2.setShortname("p2");
		pa2.setTitle("Answer 2");
		sc2.getPossibleAnswers().add(pa2);
		survey.getElements().add(sc2);
		
		counter++;
		
		SingleChoiceQuestion sc3 = new SingleChoiceQuestion("Question " + counter, "q" + counter, UUID.randomUUID().toString());
		sc3.setPosition(counter);
		sc3.setUseRadioButtons(true);
		PossibleAnswer pb = new PossibleAnswer();
		pb.setUniqueId(UUID.randomUUID().toString());
		pb.setPosition(1);
		pb.setShortname("p1");
		pb.setTitle("Answer 1");
		sc3.getPossibleAnswers().add(pb);
		PossibleAnswer pb2 = new PossibleAnswer();
		pb2.setUniqueId(UUID.randomUUID().toString());
		pb2.setPosition(2);
		pb2.setShortname("p2");
		pb2.setTitle("Answer 2");
		sc3.getPossibleAnswers().add(pb2);
		survey.getElements().add(sc3);
		
		p1b.getDependentElements().getDependentElements().add(sc2);
		p.getDependentElements().getDependentElements().add(sc2);
		pa.getDependentElements().getDependentElements().add(sc3);
		
		return survey;
	}
	
	
	public static Survey createDummySurvey(User owner, Language la, boolean init, Integer questions) {

		Survey survey = new Survey();
		survey.setContact(owner.getEmail());
		survey.setIsDraft(true);
		survey.setCreated(new Date());
		survey.setOwner(owner);
		survey.setSecurity("secured");
		
		if (SurveyCounter == 1)
		{
			survey.setSecurity("securedanonymous");
		}
		
		if (init)
		{
			survey.setShortname(Constants.SHORTNAME + SurveyCounter++);
		} else {
			survey.setShortname(UUID.randomUUID().toString());
		}
		survey.setLanguage(la);
		
		if (SurveyCounter % 2 == 0)
		{
			survey.setListForm(true);
		}
		
		survey.setTitle("Fake survey with really long chunks of text, an image, basic fields, an error and multipage actions");
		
		survey.getUsefulLinks().put("0#Google", "http://www.google.de");
		survey.getUsefulLinks().put("1#Wikipedia", "http://de.wikipedia.org");
		
		int counter = 1;

		Section h1 = new Section("General Information", "sectiongeneral", UUID.randomUUID().toString());
		h1.setPosition(counter++);
		survey.getElements().add(h1);
		
		if (questions == null)
		{
			SingleChoiceQuestion question = new SingleChoiceQuestion("Your institution or agency?", Constants.SHORTNAME, UUID.randomUUID().toString());
			question.setUseRadioButtons(true);
			String[] answers = {"European Parliament", "European Economic and Social Committee", "Council", "Committee of Regions", "Commission", "Other", "Court of Justice", "European Agency", "Court of Auditors"};
			for (String answerText : answers) {
				PossibleAnswer answer = new PossibleAnswer();
				answer.setShortname(Constants.SHORTNAME);
				answer.setUniqueId(UUID.randomUUID().toString());
				answer.setTitle(answerText);
				question.getPossibleAnswers().add(answer);
			}
			question.setPosition(counter++);
			survey.getElements().add(question);
	
			FreeTextQuestion question2 = new FreeTextQuestion("Lorem ipsum dolor sit amet?", Constants.SHORTNAME, UUID.randomUUID().toString());
			question2.setNumRows(5); 
			question2.setHelp("Block of help text to describe the field above if need be.");
			question2.setPosition(counter++);
			survey.getElements().add(question2);
	
			Section h2 = new Section("Your comments on the course content", "sectioncomment", UUID.randomUUID().toString());
			h2.setPosition(counter++);
			survey.getElements().add(h2);
	
			MultipleChoiceQuestion question3 = new MultipleChoiceQuestion("Which of these roles apply to you?", Constants.SHORTNAME, UUID.randomUUID().toString());
			String[] answers2 = {"Business manager", "Software developer", "Civil servant", "Bus driver", "President"};
			for (String answerText : answers2) {
				PossibleAnswer answer = new PossibleAnswer();
				answer.setShortname(Constants.SHORTNAME);
				answer.setUniqueId(UUID.randomUUID().toString());
				answer.setTitle(answerText);
				question3.getPossibleAnswers().add(answer);
			}
			question3.setPosition(counter++);
			survey.getElements().add(question3);
	
			SingleChoiceQuestion question4 = new SingleChoiceQuestion("Your favorite color?", Constants.SHORTNAME, UUID.randomUUID().toString());
			String[] answers3 = {"Yellow", "Blue", "Green", "I don't know", "Purple", "Red"};
			for (String answerText : answers3) {
				PossibleAnswer answer = new PossibleAnswer();
				answer.setShortname(Constants.SHORTNAME);
				answer.setUniqueId(UUID.randomUUID().toString());
				answer.setTitle(answerText);
				question4.getPossibleAnswers().add(answer);
			}
			question4.setPosition(counter++);
			survey.getElements().add(question4);
	
			FreeTextQuestion question5 = new FreeTextQuestion("And now another freetext question?", Constants.SHORTNAME, UUID.randomUUID().toString());
			question5.setNumRows(1); 
			question5.setHelp("Block of help text to describe the field above if need be.");
			question5.setPosition(counter);
			survey.getElements().add(question5);
		} else {
			int elementcounter = 0;
			for (int i = 0; i < questions; i++)
			{
				if (elementcounter % 5 == 0)
				{
					SingleChoiceQuestion question = new SingleChoiceQuestion("Your institution or agency?", Constants.SHORTNAME, UUID.randomUUID().toString());
					question.setUseRadioButtons(true);
					String[] answers = {"European Parliament", "European Economic and Social Committee", "Council", "Committee of Regions", "Commission", "Other", "Court of Justice", "European Agency", "Court of Auditors"};
					for (String answerText : answers) {
						PossibleAnswer answer = new PossibleAnswer();
						answer.setShortname(Constants.SHORTNAME);
						answer.setUniqueId(UUID.randomUUID().toString());
						answer.setTitle(answerText);
						question.getPossibleAnswers().add(answer);
					}
					question.setPosition(counter++);
					survey.getElements().add(question);
				} else if (elementcounter % 4 == 0)
				{
					FreeTextQuestion question2 = new FreeTextQuestion("Lorem ipsum dolor sit amet?", Constants.SHORTNAME, UUID.randomUUID().toString());
					question2.setNumRows(5); 
					question2.setHelp("Block of help text to describe the field above if need be.");
					question2.setPosition(counter++);
					survey.getElements().add(question2);
				} else if (elementcounter % 3 == 0)
				{
					MultipleChoiceQuestion question3 = new MultipleChoiceQuestion("Which of these roles apply to you?", Constants.SHORTNAME, UUID.randomUUID().toString());
					String[] answers2 = {"Business manager", "Software developer", "Civil servant", "Bus driver", "President"};
					for (String answerText : answers2) {
						PossibleAnswer answer = new PossibleAnswer();
						answer.setShortname(Constants.SHORTNAME);
						answer.setUniqueId(UUID.randomUUID().toString());
						answer.setTitle(answerText);
						question3.getPossibleAnswers().add(answer);
					}
					question3.setPosition(counter++);
					survey.getElements().add(question3);
				} else if (elementcounter % 2 == 0)
				{
					SingleChoiceQuestion question4 = new SingleChoiceQuestion("Your favorite color?", Constants.SHORTNAME, UUID.randomUUID().toString());
					String[] answers3 = {"Yellow", "Blue", "Green", "I don't know", "Purple", "Red"};
					for (String answerText : answers3) {
						PossibleAnswer answer = new PossibleAnswer();
						answer.setShortname(Constants.SHORTNAME);
						answer.setUniqueId(UUID.randomUUID().toString());
						answer.setTitle(answerText);
						question4.getPossibleAnswers().add(answer);
					}
					question4.setPosition(counter++);
					survey.getElements().add(question4);
				} else {
					FreeTextQuestion question5 = new FreeTextQuestion("And now another freetext question?", Constants.SHORTNAME, UUID.randomUUID().toString());
					question5.setNumRows(1); 
					question5.setHelp("Block of help text to describe the field above if need be.");
					question5.setPosition(counter++);
					survey.getElements().add(question5);
				}				
				
				elementcounter++;
			}
		}
		
		return survey;
	}


	public static AnswerSet createDummyAnswerSet(Survey survey, User user) {
		AnswerSet answerSet = new AnswerSet();
		answerSet.setUniqueCode(UUID.randomUUID().toString());
		answerSet.setDate(new Date());
		answerSet.setInvitationId(UUID.randomUUID().toString());
		answerSet.setUpdateDate(answerSet.getDate());
		answerSet.setSurvey(survey);
		answerSet.setSurveyId(survey.getId());
		answerSet.setLanguageCode("EN");
		answerSet.setResponderEmail(user.getEmail());
		
		Random random = new Random();
		
		for (Question question : survey.getQuestions()) {

			if (question instanceof FreeTextQuestion) {
				Answer answer = new Answer();
				answer.setAnswerSet(answerSet);
				answer.setQuestionId(question.getId());
				answer.setQuestionUniqueId(question.getUniqueId());
				answer.setValue("Hello FreeText");
				answerSet.addAnswer(answer);
			} else if (question instanceof MultipleChoiceQuestion) {
				List<PossibleAnswer> possibleAnswers = ((MultipleChoiceQuestion) question).getPossibleAnswers();
				int index = random.nextInt(possibleAnswers.size());
				int counter = 0;
				for (PossibleAnswer possibleAnswer : ((MultipleChoiceQuestion) question).getPossibleAnswers()) {
					if (counter == index) {
						continue;
					}					
					Answer answer = new Answer();
					answer.setAnswerSet(answerSet);
					answer.setQuestionId(question.getId());
					answer.setQuestionUniqueId(question.getUniqueId());
					answer.setValue(possibleAnswer.getId().toString());
					answer.setPossibleAnswerId(possibleAnswer.getId());
					answer.setPossibleAnswerUniqueId(possibleAnswer.getUniqueId());
					answerSet.addAnswer(answer);
					counter++;
				}
			} else if (question instanceof SingleChoiceQuestion) {
				Answer answer = new Answer();
				answer.setAnswerSet(answerSet);
				answer.setQuestionId(question.getId());
				answer.setQuestionUniqueId(question.getUniqueId());
				List<PossibleAnswer> possibleAnswers = ((SingleChoiceQuestion) question).getPossibleAnswers();				
				int index = random.nextInt(possibleAnswers.size());
				answer.setValue(possibleAnswers.get(index).getId().toString());
				answer.setPossibleAnswerId(possibleAnswers.get(index).getId());
				answer.setPossibleAnswerUniqueId(possibleAnswers.get(index).getUniqueId());
				answerSet.addAnswer(answer);
			} else if (question instanceof Matrix) {
				
				Matrix matrix = (Matrix) question;
				Element firstAnswer = matrix.getAnswers().get(0);
				for (Element matrixquestion : matrix.getQuestions()) {
					Answer answer = new Answer();
					answer.setAnswerSet(answerSet);
					answer.setQuestionId(matrixquestion.getId());
					answer.setQuestionUniqueId(matrixquestion.getUniqueId());
				
					answer.setValue(firstAnswer.getId().toString());
					answer.setPossibleAnswerId(firstAnswer.getId());
					answer.setPossibleAnswerUniqueId(firstAnswer.getUniqueId());
					answerSet.addAnswer(answer);
				}
				
				
			}		
		}
		return answerSet;
	}
	
	public static List<Language> createBasicLanguages() {
		List<Language> languages = new ArrayList<>();
		languages.add(new Language("EN", "English", "English", true));
		languages.add(new Language("DE", "Deutsch", "German", true));
		languages.add(new Language("FR", "Francais", "French", true));
		return languages;
	}
	
	public static Translations createDummyTranslations(Language lang, Translations translations)
	{
		Translations result = new Translations();
		result.setLanguage(lang);
		result.setSurveyId(translations.getSurveyId());
		result.setSurveyUid(translations.getSurveyUid());
		result.setTitle(translations.getTitle() + lang);
		for (Translation translation: translations.getTranslations())
		{
			result.getTranslations().add(new Translation(translation.getKey(), translation.getLabel() + lang.getCode(), lang.getCode(), translations.getSurveyId(), result));
		}
		return result;
	}

	public static void createStressTestSurvey(User analyst, ServletContext servletContext, String fileDir, SurveyService surveyService, FileService fileService) {
		InputStream inputStream = null;
	    try {
	    	logger.warn("creating stress test surveys");
	    	inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/EC/StressTest10.eus");
	    	
	    	String uuid = UUID.randomUUID().toString().replace("/", "");
	        java.io.File file = null;
	        ImportResult result = null;
        	file = fileService.createTempFile("import" + uuid, null); 	        	        
        	FileOutputStream fos = new FileOutputStream(file);
            IOUtils.copy(inputStream, fos);
            fos.close();
            
            result = SurveyExportHelper.importSurvey(file, fileService, analyst.getEmail());
            result.getSurvey().setIsActive(false);
            result.getSurvey().setIsPublished(true);	
            
            for (int i = 1; i < 1000; i++)
            {
            	String uid = UUID.randomUUID().toString();
            	result.getSurvey().setShortname("stress" + i);
            	result.getSurvey().setUniqueId(uid);
            	result.getActiveSurvey().setShortname("stress" + i);
            	result.getActiveSurvey().setUniqueId(uid);
            	int id = surveyService.importSurvey(result, analyst, false);            
            	Survey survey = surveyService.getSurvey(id);            
            	Survey published = surveyService.publish(survey, -1, -1, false, -1, false, true);
            	surveyService.activate(survey, false, -1);
            	
            	published.getPublication().setAllContributions(true);
            	published.getPublication().setAllQuestions(true);
            	published.getPublication().setShowCharts(true);
            	published.getPublication().setShowContent(true);
            	published.getPublication().setShowStatistics(true);
            	published.getPublication().setShowSearch(true);
            	
            	surveyService.update(published, true);
            }
	    	
	    } catch (Exception e)
	    {
	    	logger.error(e.getLocalizedMessage(), e);
	    }
		
	}
	
	public static void createStressTestSurveys(User analyst, ServletContext servletContext, String fileDir, SurveyService surveyService, FileService fileService) {
	    try {
	    	logger.warn("creating stress test surveys");
	    	
	    	createStressTestSurvey("/WEB-INF/Content/EC/StressTest10.eus", analyst, servletContext, fileDir, surveyService, fileService);
	    	createStressTestSurvey("/WEB-INF/Content/EC/StressTest20.eus", analyst, servletContext, fileDir, surveyService, fileService);
	    	createStressTestSurvey("/WEB-INF/Content/EC/StressTest30.eus", analyst, servletContext, fileDir, surveyService, fileService);
	    } catch (Exception e)
	    {
	    	logger.error(e.getLocalizedMessage(), e);
	    }		
	}
	
	private static void createStressTestSurvey(String path, User analyst, ServletContext servletContext, String fileDir, SurveyService surveyService, FileService fileService) throws Exception {
		InputStream inputStream = servletContext.getResourceAsStream(path);
    	
    	String uuid = UUID.randomUUID().toString().replace("/", "");
        java.io.File file = null;
        ImportResult result = null;
    	file = fileService.createTempFile("import" + uuid, null); 	        	        
    	FileOutputStream fos = new FileOutputStream(file);
        IOUtils.copy(inputStream, fos);
        fos.close();
        
        result = SurveyExportHelper.importSurvey(file, fileService, analyst.getEmail());
        result.getSurvey().setIsActive(false);
        result.getSurvey().setIsPublished(true);            
       
    	String uid = UUID.randomUUID().toString();
    	result.getSurvey().setUniqueId(uid);
    	result.getActiveSurvey().setUniqueId(uid);
    	int id = surveyService.importSurvey(result, analyst, false);            
    	Survey survey = surveyService.getSurvey(id);            
    	Survey published = surveyService.publish(survey, -1, -1, false, -1, false, true);
    	surveyService.activate(survey, false, -1);
    	
    	published.getPublication().setAllContributions(true);
    	published.getPublication().setAllQuestions(true);
    	published.getPublication().setShowCharts(true);
    	published.getPublication().setShowContent(true);
    	published.getPublication().setShowStatistics(true);
    	published.getPublication().setShowSearch(true);
    	
    	surveyService.update(published, true);
	}

}
