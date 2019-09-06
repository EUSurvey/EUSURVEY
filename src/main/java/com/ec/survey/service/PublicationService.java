package com.ec.survey.service;

import com.ec.survey.model.Publication;
import com.ec.survey.model.survey.Survey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublicationService {

	@Autowired
	private SurveyService surveyService;
	
	public boolean hasPublishedAnswers(int surveyId) {
		Survey survey = surveyService.getSurvey(surveyId);		
		Publication publication = survey.getPublication();		
		return publication.isShowContent() || publication.isShowStatistics() || publication.isShowCharts();
	}
}
