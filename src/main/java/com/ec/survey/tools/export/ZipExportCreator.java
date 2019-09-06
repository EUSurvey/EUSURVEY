package com.ec.survey.tools.export;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.ec.survey.model.survey.base.File;

@Service("zipExportCreator")
@Scope("prototype")
public class ZipExportCreator extends ExportCreator {

	protected @Value("${export.fileDir}") String fileDir;
	
	@Override
	void ExportCharts() throws Exception {}
	
	@Override
	void ExportContent(boolean sync) throws Exception {
		java.io.File temp = fileService.createTempFile("export" + UUID.randomUUID().toString(), ".zip");
		final OutputStream out = new FileOutputStream(temp);
		final ArchiveOutputStream os = new ArchiveStreamFactory().createArchiveOutputStream("zip", out);
			
		for (String uid : export.getResultFilter().getVisibleQuestions())
		{
			List<String[]> files = answerService.getFilesForQuestion(uid, form.getSurvey().getIsDraft());
						
			for (Object[] item : files)
			{				
				File f = fileService.get(item[1].toString());
				java.io.File source = fileService.getSurveyFile(form.getSurvey().getUniqueId(), f.getUid());		
		    	
		    	if (source.exists())
		    	{
			    	os.putArchiveEntry(new ZipArchiveEntry(item[0].toString() + "_" + item[1].toString() + "_" + item[2].toString()));
			    	IOUtils.copy(new FileInputStream(source), os);
				    os.closeArchiveEntry();
		    	}				
			}
		}
		
		os.close();
		out.close();
		
		IOUtils.copy(new FileInputStream(temp), outputStream);
		
		activityService.log(314, null, null, export.getUserId(), export.getSurvey().getUniqueId());
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
