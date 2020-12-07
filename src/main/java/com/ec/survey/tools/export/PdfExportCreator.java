package com.ec.survey.tools.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.lang.NotImplementedException;
import org.apache.poi.util.IOUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("pdfExportCreator")
@Scope("prototype")
public class PdfExportCreator extends ExportCreator {
	
	@Override
	void exportContent(boolean sync) throws Exception {
		File file = pdfService.createAllIndividualResultsPDF(form.getSurvey(), export.getResultFilter());
		FileInputStream fis = new FileInputStream(file);
		IOUtils.copy(fis, outputStream);
		fis.close();
		Files.delete(file.toPath());
	}
	
	@Override
	void exportStatistics() throws IOException {
		File file = pdfService.createStatisticsPDF(form.getSurvey(), export.getId().toString());
		FileInputStream fis = new FileInputStream(file);
		IOUtils.copy(fis, outputStream);
		fis.close();
		Files.delete(file.toPath());
	}
	
	@Override
	void exportStatisticsQuiz() throws IOException {
		File file = pdfService.createStatisticsQuizPDF(form.getSurvey(), export.getId().toString());
		FileInputStream fis = new FileInputStream(file);
		IOUtils.copy(fis, outputStream);
		fis.close();
		Files.delete(file.toPath());
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

}
