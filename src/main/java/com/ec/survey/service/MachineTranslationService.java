package com.ec.survey.service;

import com.ec.survey.model.FtpEndPoint;
import com.ec.survey.model.Translation;
import com.ec.survey.model.Translations;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.machinetranslation.Request;
import com.ec.survey.model.machinetranslation.RequestTranslationMessage;
import com.ec.survey.model.machinetranslation.Response;
import com.ec.survey.tools.ConversionTools;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service("machineTranslationService")
@Transactional
public class MachineTranslationService extends BasicService {

	@Resource(name = "eTranslationService")
	private ETranslationService eTranslationService;

	@Resource(name = "translationService")
	private TranslationService translationService;

	@Resource(name = "surveyService")
	private SurveyService surveyService;
	
	@Resource(name = "ftpClient")
	private FtpClient ftpClient;
	
	@Resource(name = "microsoftTranslationService")
	private MicrosoftTranslationService microsoftTranslationService;
	
	@Transactional
	public void saveSuccessResponse(String requestId, String targetLanguage, String translatedText) {

		Request request = getRequest(requestId);
		Response response = new Response();		
		response.setRequest(request);
		response.setTargetLang(targetLanguage);
		
		// build the delivery Path from the path URL that has been set to the request
		String deliveryURL= buildDeliveryURLFromRequest(request);		
		logger.info("machineTransaltionService.saveSuccessResponse set the Delivery URL to "  + deliveryURL);
		
		response.setDeliveryURL(deliveryURL);
		response.setTranslatedText(translatedText);
		addResponse(response);
		updateTranslation(response);
	}

	private void updateTranslation(Response response) {
		logger.info("MachineTranslationService.updateTranslation start to update for " + response.getDeliveryURL() );
		Request request = response.getRequest();
		Integer sourceTranslationsID = request.getTranslationsID();
		Translations sourceTranslations = translationService.getTranslations(sourceTranslationsID);
		Translations targetTranslations = null;
		String targetLang = response.getTargetLang();
		String translatedText = getTranslatedText(response);
		List<Translations> alltranslations = translationService.getTranslationsForSurvey(sourceTranslations.getSurveyId(), true);
		for (Translations trans : alltranslations) {
			if (trans.getLanguage().getCode().equalsIgnoreCase(targetLang)) {
				if (!trans.getComplete()) {
					targetTranslations = trans;
				}

			}
		}

		Map<Integer, String> transMap = ConversionTools.mapFromHTML(translatedText);
		List<Translation> sourceTranslationsList = sourceTranslations.getTranslations();
		List<Translation> translations = targetTranslations.getTranslations();
		Map<String,Integer> existingTargetTranslation = getMap(translations);
		if (targetTranslations != null && targetTranslations.getRequested()) {
			String language = targetLang.toLowerCase();
			for (Translation translation : sourceTranslationsList) {
				Integer id = translation.getId();
				String key = translation.getKey();
				Integer surveyId = translation.getSurveyId();
				String text = transMap.get(id);
				text = text.replace("<br>", "<br />");
				if (!existingTargetTranslation.containsKey(key)) {
					translations.add(new Translation(key, text, language, surveyId, targetTranslations));
				}else {
					String translationId = String.valueOf(existingTargetTranslation.get(key));
					Translation existingTranslation = targetTranslations.getTranslationById(translationId);
					if (existingTranslation.getLabel() == null || existingTranslation.getLabel().isEmpty() ) {
						existingTranslation.setLabel(text);
					}
				}
			}
			targetTranslations.setRequested(false);
			translationService.save(targetTranslations);
		}
	}

	private String getTranslatedText(Response response) {
		logger.info("MachineTranslationService.getTranslatedText started to get translation " +response.getDeliveryURL() );
		String result = "";
		if (response.getTranslatedText() != null && response.getTranslatedText().length() > 0) {
			result = response.getTranslatedText();
		}else {
			
			FtpEndPoint ftpEndPoint = FtpEndPoint.createFromMT(response.getDeliveryURL());
			result = ftpClient.getFile(ftpEndPoint);
		}
		return result;
	}

	private Map<String,Integer> getMap(List<Translation> translationsList) {
		Map<String,Integer> result = new HashMap<>();
		for (Translation translation : translationsList) {
			result.put(translation.getKey(),translation.getId());
		}
		return result;
	}	

	@Transactional
	public void saveErrorResponse(String requestId, String targetLanguage, String errorCode, String errorMessage) {
		Request request = getRequest(requestId);
		request.getTranslationsID();
		Response response = new Response();
		response.setRequest(request);
		response.setTargetLang(targetLanguage);
		response.setErrCode(errorCode);
		response.setErrMsg(errorMessage);
		addResponse(response);
	}
	public boolean translateTranlations(String[] ids, User user, boolean useECMT) {
		boolean result = true ;
		
		Integer sourceTranslationsId = null;
		List<Integer> targetIDs = new ArrayList<>();
		StringBuilder targetLanguage = new StringBuilder();
		if (ids.length < 2) {
			return false;
		}
		Translations translations = translationService.getTranslations(Integer.parseInt(ids[0]));
		
		List<Translations> alltranslations = translationService.getTranslationsForSurvey(translations.getSurveyId(), true);
		if (alltranslations.size() == 1) {
			return false;
		}
		Set<String> idsSet = new HashSet<>(Arrays.asList(ids));
		for (Translations trans : alltranslations) {
			if (idsSet.contains(String.valueOf(trans.getId()))){
				if (trans.getComplete()) {
					sourceTranslationsId = trans.getId();
				} else {
					if (useECMT) 
					{
						// limit languages to EU official languages 
						if (trans.getLanguage().isOfficial()) 
						{
							targetLanguage.append(trans.getLanguage().getCode()).append(",");
							targetIDs.add(trans.getId());
						}
						else 
						{
							result = false;
						}							
					}
					else
					{
						targetLanguage.append(trans.getLanguage().getCode()).append(",");
						targetIDs.add(trans.getId());
					}					
				}
			}
		}
		if (targetLanguage.toString().endsWith(","))
			targetLanguage = new StringBuilder(targetLanguage.substring(0, targetLanguage.length() - 1));
		if ((targetLanguage.length() > 0) && sourceTranslationsId != null) {
			if (useECMT) 
			{
				translateTranlations(sourceTranslationsId, targetLanguage.toString(), user,targetIDs);
			}
			else 
			{
				translateTranlationsWithMicrosoft(sourceTranslationsId, targetLanguage.toString(), user,targetIDs);
			}
			
		}
		return result;
	}

	private void translateTranlationsWithMicrosoft(Integer sourceTranslationsId, String targetLanguage, User user, List<Integer> targetIDs) {
		Translations sourceTranslations = translationService.getTranslations(sourceTranslationsId);

		List<Translation> translationsList = sourceTranslations.getTranslations();
		String sourceLanguage = translationsList.get(0).getLanguage();
     	int size = translationsList.size();
		ArrayList<String>  sourceTexts = new ArrayList<>(size);   
     	Map<Integer,String>  keyMap = new HashMap<>(size);

     	Integer surveyId = sourceTranslations.getSurveyId();
     	
     	int j=0;
		for (Translation translation : translationsList) {
			if (translation.getLabel() != null && !translation.getLabel().isEmpty() && !translation.getLabel().trim().isEmpty()) {
				sourceTexts.add(translation.getLabel());
				keyMap.put(j, translation.getKey());
				j++;
			}
		}
				
		for (Integer targetID : targetIDs) {
			Translations targetTranslations = translationService.getTranslations(targetID);
			String targetLangauge = targetTranslations.getLanguage().getCode();
			targetTranslations.setRequested(true);
			String[] translatedTexts = microsoftTranslationService.translate(sourceTexts.toArray(new String[sourceTexts.size()]), sourceLanguage, targetLangauge );
			List<Translation> translations = targetTranslations.getTranslations();
			Map<String,Integer> existingTargetTranslation = getMap(translations);
            for (int i = 0; i < translatedTexts.length; i++) {
            	String key = keyMap.get(i);
            	String text = translatedTexts[i];
				if (!existingTargetTranslation.containsKey(key)) {
					translations.add(new Translation(key, text, targetLangauge, surveyId, targetTranslations ));
				}else {
					String translationId = String.valueOf(existingTargetTranslation.get(key));
					Translation existingTranslation = targetTranslations.getTranslationById(translationId);
					if (existingTranslation.getLabel() == null || existingTranslation.getLabel().isEmpty() ) {
						existingTranslation.setLabel(text);
					}
				}
			}					
			targetTranslations.setRequested(false);
			translationService.save(targetTranslations);
		}
		
	}

	private void translateTranlations(Integer sourceTranslationsId, String targetLanguage, User user, List<Integer> targetIDs) {

		Translations sourceTranslations = translationService.getTranslations(sourceTranslationsId);
		
		String textToTranslate; 

		List<Translation> translationsList = sourceTranslations.getTranslations();
		String sourceLanguage = translationsList.get(0).getLanguage();

		Map<Integer,String> textToTranslateMap = new HashMap<>();

		for (Translation translation : translationsList) {

			textToTranslateMap.put(translation.getId(), translation.getLabel());
		}
		
        
		textToTranslate = ConversionTools.mapToHTML(textToTranslateMap);
		
        boolean useFTP= true;
		
		String uuid = UUID.randomUUID().toString();

		// save to database
		Request request = new Request();
		request.setSourceLang(sourceLanguage);
		request.setTargetLangs(targetLanguage);
		request.setUniqueId(uuid);
		request.setTranslationsID(sourceTranslationsId);
		request.setEmail(user.getEmail());
		String ftpURI = "";
		String originalFileName =""; 
		String ftpTargetPath="";
		if (!useFTP) {
			request.setText("");
		}else {
			ftpURI = ftpClient.getFtpSourceURL();
			
			logger.debug("MachineTranslationService.translateTranslations Get FTP SOurce " +ftpURI);
			// construct the return ftp path used in the call back to get translated file
			if (ftpURI.endsWith("/")){
				ftpTargetPath = ftpURI;
			}else{
				ftpTargetPath = ftpURI + '/';
			}								
			ftpTargetPath += uuid  + '/';
			
			originalFileName = uuid + ".html";
			try {
				ftpClient.putFile(FtpEndPoint.createFromMT(ftpURI) , originalFileName, textToTranslate);	
			} catch (Exception e) {
				logger.error("Error when try to put file to FTP " + e);
			}
			
			logger.debug("MachineTranslationService.translateTranslations put file to FTP DONE  at location " +ftpURI);
			ftpURI += originalFileName;
			request.setFileURL(ftpURI);
		}

		String username = user.getLogin();
		request.setUsername(username);
		addRequest(request);

		// call ws
		RequestTranslationMessage rtm = new RequestTranslationMessage();
		rtm.setExternalReference(uuid);
		if (useFTP) {
			rtm.setRequestType("doc");
			rtm.setTextToTranslate("");
			rtm.setOriginalFileName(originalFileName);
			rtm.setDocumentToTranslate(ftpURI);	
			rtm.setTargetTranslationPath(ftpTargetPath);
		}else {
			rtm.setRequestType("txt");
			rtm.setTextToTranslate(textToTranslate);
		}
		rtm.setSourceLanguage(sourceLanguage);
		rtm.setTargetLanguage(targetLanguage);
		

		rtm.setUsername(username);
		boolean isOK;
		try {
			isOK = eTranslationService.sendMessage(rtm);	
		} catch (Exception e) {
			isOK = false;
			logger.error("Error when send request for eTranslation " + e);
		}
		
		
		if (isOK) 
		{
			for (Integer targetID : targetIDs) {
				Translations targetTranslation = translationService.getTranslations(targetID);
				targetTranslation.setRequested(true);
				translationService.save(targetTranslation);
			}
		}
	}

	
	private String buildDeliveryURLFromRequest(Request request){
		String result="";
		String startURL= StringUtils.remove(request.getFileURL(), ".html");
		result = startURL + '/' + request.getUniqueId() + '_' + request.getTargetLangs() +".html";
		logger.debug("MachineTranslationService.buildDeliveryURLFromRequest path is " + result);
		return result;
		
	}
	
	@Transactional
	public Request getRequest(Integer id) {
		Session session = sessionFactory.getCurrentSession();
		return (Request) session.get(Request.class, id);
	}

	@Transactional
	public Request getRequest(String uid) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM Request r WHERE r.uniqueId = :uniqueId ");
		query.setParameter("uniqueId", uid);
		return (Request) query.uniqueResult();
	}

	@Transactional
	public void addRequest(Request request) {
		Session session = sessionFactory.getCurrentSession();
		session.save(request);
	}

	@Transactional
	public void saveRequest(Request request) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(request);
	}

	@Transactional
	public void deleteRequest(Request request) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(request);
	}

	@Transactional
	public Response getResponse(Integer id) {
		Session session = sessionFactory.getCurrentSession();
		return (Response) session.get(Response.class, id);
	}

	@Transactional
	public void addResponse(Response response) {
		Session session = sessionFactory.getCurrentSession();
		session.save(response);
	}

	@Transactional
	public void saveResponse(Response response) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(response);
	}

	@Transactional
	public void deleteResponse(Response response) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(response);
	}

	public void microsoftTranslateTranlation(String[] ids, User user) {
		// TODO Auto-generated method stub		
	}
}
