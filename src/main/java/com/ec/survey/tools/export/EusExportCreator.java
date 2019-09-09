package com.ec.survey.tools.export;

import java.io.FileInputStream;
import org.apache.poi.util.IOUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("eusExportCreator")
@Scope("prototype")
public class EusExportCreator extends ExportCreator {
	
	@Override
	void ExportContent(boolean sync) throws Exception {
		java.io.File zip = surveyService.exportSurvey(form.getSurvey().getShortname(), surveyService, true);
		IOUtils.copy(new FileInputStream(zip), outputStream);
	}
	
	@Override
	void ExportStatistics() throws Exception {}
	
	@Override
	void ExportStatisticsQuiz() throws Exception {}

	@Override
	void ExportAddressBook() throws Exception {}

	@Override
	void ExportActivities() throws Exception {}
	
	@Override
	void ExportTokens() throws Exception {}	

}
