package com.ec.survey.tools;

import java.nio.file.Files;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ec.survey.model.survey.base.File;
import com.ec.survey.service.FileService;

@Service("fileWorker")
@Scope("singleton")
public class FileUpdater implements Runnable {

	protected static final Logger logger = Logger.getLogger(FileUpdater.class);
	
	@Resource(name = "fileService")
	private FileService fileService;
	
	private @Value("${export.fileDir}") String fileDir;
	
	@Override
	@Transactional
	public void run() {
		try {
			List<File> files = fileService.getAllInvalid();
			
			for (File file: files)
			{
				if (file.getComment() != null && file.getComment().length() > 0)
				{
					java.io.File export = fileService.getSurveyExportFile(file.getComment(), file.getUid(), false);
					Files.deleteIfExists(export.toPath());
				}
				
				//also delete from old file system
				java.io.File f = new java.io.File(fileDir + file.getUid());
				Files.deleteIfExists(f.toPath());
				fileService.delete(file);
			}
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}		
	}
	
}
