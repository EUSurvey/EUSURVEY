package com.ec.survey.tools.export;

import java.io.File;
import java.io.FileInputStream;
import org.apache.poi.util.IOUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("pdfExportCreator")
@Scope("prototype")
public class PdfExportCreator extends ExportCreator {

	@Override
	void ExportCharts() throws Exception {
		File file = pdfService.createChartsPDF(form.getSurvey(), export.getId().toString());
		FileInputStream fis = new FileInputStream(file);
		IOUtils.copy(fis, outputStream);
		fis.close();
		file.delete();
	}
	
	@Override
	void ExportContent(boolean sync) throws Exception {
		File file = pdfService.createAllIndividualResultsPDF(form.getSurvey(), export.getResultFilter());
		FileInputStream fis = new FileInputStream(file);
		IOUtils.copy(fis, outputStream);
		fis.close();
		file.delete();
	}
	
	@Override
	void ExportStatistics() throws Exception {
		File file = pdfService.createStatisticsPDF(form.getSurvey(), export.getId().toString());
		FileInputStream fis = new FileInputStream(file);
		IOUtils.copy(fis, outputStream);
		fis.close();
		file.delete();
	}
	
	@Override
	void ExportStatisticsQuiz() throws Exception {
		File file = pdfService.createStatisticsQuizPDF(form.getSurvey(), export.getId().toString());
		FileInputStream fis = new FileInputStream(file);
		IOUtils.copy(fis, outputStream);
		fis.close();
		file.delete();
	}

	@Override
	void ExportAddressBook() throws Exception {}

	@Override
	void ExportActivities() throws Exception {}
	
	@Override
	void ExportTokens() throws Exception {}	

}
