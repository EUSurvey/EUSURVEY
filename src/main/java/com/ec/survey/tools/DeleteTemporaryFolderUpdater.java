package com.ec.survey.tools;

import com.ec.survey.service.FileService;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import javax.annotation.Resource;

@Service("deleteTemporaryFoldersWorker")
@Scope("singleton")
public class DeleteTemporaryFolderUpdater implements Runnable {

	protected static final Logger logger = Logger.getLogger(DeleteTemporaryFolderUpdater.class);
	
	@Resource(name="fileService")
	private FileService fileService;
		
	@Override
	public void run() {
		try {
			logger.info("DeleteTemporaryFolderUpdater started");
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -1);
			
			fileService.deleteOldTemporaryFolders(cal.getTime());	
			
			cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -7);
			
			fileService.deleteOldTempFiles(cal.getTime());			
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}		
		logger.info("DeleteTemporaryFolderUpdater completed");
	}
	
}
