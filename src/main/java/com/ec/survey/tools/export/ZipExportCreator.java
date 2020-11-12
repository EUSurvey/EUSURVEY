package com.ec.survey.tools.export;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.NotImplementedException;
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
	void ExportContent(boolean sync) throws Exception {
		java.io.File temp = fileService.createTempFile("export" + UUID.randomUUID().toString(), ".zip");
		final OutputStream out = new FileOutputStream(temp);
		final ArchiveOutputStream os = new ArchiveStreamFactory().createArchiveOutputStream("zip", out);
	
		List<File> uploadedfiles = answerService.getAllUploadedFiles(form.getSurvey().getId(), export.getResultFilter(), 1, Integer.MAX_VALUE);
				
		for (String uid : export.getResultFilter().getVisibleQuestions())
		{
			List<String[]> files = answerService.getFilesForQuestion(uid, form.getSurvey().getIsDraft());
			List<String> fileuids = new ArrayList<>();
			for (Object[] item : files)
			{				
				fileuids.add(item[1].toString());
			}			
									
			for (File f : uploadedfiles)
			{				
				if (fileuids.contains(f.getUid()))
				{
					java.io.File source = fileService.getSurveyFile(form.getSurvey().getUniqueId(), f.getUid());		
			    	
			    	if (source.exists())
			    	{
			    		String name = f.getName();
			    		String ext = FilenameUtils.getExtension(name);
			    		name = FilenameUtils.getBaseName(name);		    				
			    		
				    	os.putArchiveEntry(new ZipArchiveEntry(name + "_" + f.getUid() + "." + ext));
				    	IOUtils.copy(new FileInputStream(source), os);
					    os.closeArchiveEntry();
			    	}
				}
			}
		}
		
		os.close();
		out.close();
		
		IOUtils.copy(new FileInputStream(temp), outputStream);
		
		activityService.log(314, null, null, export.getUserId(), export.getSurvey().getUniqueId());
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
