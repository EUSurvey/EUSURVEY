package com.ec.survey.tools.export;

import java.io.FileInputStream;

import org.apache.commons.lang.NotImplementedException;
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
	void ExportStatistics() throws Exception {
		throw new NotImplementedException();
	}
	
	@Override
	void ExportStatisticsQuiz() throws Exception {
		throw new NotImplementedException();
	}

	@Override
	void ExportAddressBook() throws Exception {
		throw new NotImplementedException();
	}

	@Override
	void ExportActivities() throws Exception {
		throw new NotImplementedException();
	}
	
	@Override
	void ExportTokens() throws Exception {
		throw new NotImplementedException();
	}	

}
