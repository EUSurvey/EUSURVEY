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
	void exportContent(boolean sync) throws Exception {
		java.io.File zip = surveyService.exportSurvey(form.getSurvey().getShortname(), surveyService, true);
		IOUtils.copy(new FileInputStream(zip), outputStream);
	}
	
	@Override
	void exportStatistics() throws Exception {
		throw new NotImplementedException();
	}
	
	@Override
	void exportStatisticsQuiz() throws Exception {
		throw new NotImplementedException();
	}

	@Override
	void exportAddressBook() throws Exception {
		throw new NotImplementedException();
	}

	@Override
	void exportActivities() throws Exception {
		throw new NotImplementedException();
	}
	
	@Override
	void exportTokens() throws Exception {
		throw new NotImplementedException();
	}

	@Override
	void exportECFGlobalResults() throws Exception {
		throw new NotImplementedException();
	}

	@Override
	void exportECFProfileResults() throws Exception {
		throw new NotImplementedException();
	}

	@Override
	void exportECFOrganizationalResults() throws Exception {
		throw new NotImplementedException();
	}	

	@Override
	void exportPDFReport() throws Exception {
		throw new NotImplementedException();
	}
}
