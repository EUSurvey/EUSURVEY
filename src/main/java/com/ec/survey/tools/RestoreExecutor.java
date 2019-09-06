package com.ec.survey.tools;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ec.survey.model.Archive;
import com.ec.survey.model.administration.User;
import com.ec.survey.service.AdministrationService;
import com.ec.survey.service.ArchiveService;
import com.ec.survey.service.FileService;
import com.ec.survey.service.SurveyService;

@Service("restoreExecutor")
@Scope("prototype")
public class RestoreExecutor implements Runnable {
	
	@Resource(name="fileService")
	private FileService fileService;
	
	@Resource(name="surveyService")
	private SurveyService surveyService;
	
	@Resource(name="archiveService")
	private ArchiveService archiveService;
	
	@Resource(name="administrationService")
	private AdministrationService administrationService;
		
	private Archive archive;
	private String alias;
	private User user;
	
	private static final Logger logger = Logger.getLogger(RestoreExecutor.class);
	
	public void init(Archive archive, String alias, User user)
	{
		this.archive = archive;
		this.alias = alias;
		this.user = user;
	}
	
	@Transactional
	public void prepare()
	{
		archiveService.markRestoring(archive);
	}
	
	@Transactional
	public void run()
	{
		try {
			if (!user.getId().equals(archive.getUserId()))
			{
				User originalUser = administrationService.getUser(archive.getUserId());
				if (originalUser != null)
				{
					archiveService.restore(archive, originalUser, alias);
				} else {
					archiveService.restore(archive, user, alias);
				}
			} else {		
				archiveService.restore(archive, user, alias);
			}
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);			
			archive.setError(e.getLocalizedMessage());
			archiveService.unmarkRestoring(archive);
		}
	}
	
}
