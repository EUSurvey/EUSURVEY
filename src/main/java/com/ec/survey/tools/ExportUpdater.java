package com.ec.survey.tools;

import com.ec.survey.service.ExportService;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("exportWorker")
@Scope("singleton")
public class ExportUpdater implements Runnable {

	protected static final Logger logger = Logger.getLogger(ExportUpdater.class);
	
	@Resource(name="exportService")
	private ExportService exportService;
	
	@Override
	public void run() {
		try {	
			exportService.applyExportTimeout();
			
			logger.info("Starting deletion of old exports");
			exportService.deleteOldExports();			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
}
