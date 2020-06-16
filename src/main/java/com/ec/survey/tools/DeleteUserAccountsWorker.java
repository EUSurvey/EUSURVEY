package com.ec.survey.tools;

import com.ec.survey.model.Access;
import com.ec.survey.model.Archive;
import com.ec.survey.model.Export;
import com.ec.survey.model.attendees.Share;
import com.ec.survey.service.AdministrationService;
import com.ec.survey.service.ArchiveService;
import com.ec.survey.service.AttendeeService;
import com.ec.survey.service.ExportService;
import com.ec.survey.service.SurveyService;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

@Service("deleteUserAccountsWorker")
@Scope("singleton")
public class DeleteUserAccountsWorker implements Runnable {

	protected static final Logger logger = Logger.getLogger(DeleteUserAccountsWorker.class);
	
	@Resource(name="administrationService")
	private AdministrationService administrationService;
	
	@Resource(name="surveyService")
	private SurveyService surveyService;
	
	@Resource(name="attendeeService")
	private AttendeeService attendeeService;
	
	@Resource(name="exportService")
	private ExportService exportService;
	
	@Resource(name="archiveService")
	private ArchiveService archiveService;
		
	@Override
	public void run() {
		try {
			logger.info("DeleteUserAccountsWorker started");
			
			List<Integer> userids = administrationService.getUserAccountsForDeletion();
			
			for (int userid : userids)
			{				
				//Deletion of all Surveys created by the user
				List<Integer> surveys = surveyService.getSurveysForUser(userid);
				for (int survey: surveys)
				{
					surveyService.delete(survey, true, true);
				}
				
				//Deletion of all delete managing rights of the user
				surveys = surveyService.getSurveysWithPrivilegesForUser(userid);
				for (int survey: surveys)
				{
					List<Access> accesses = surveyService.getAccesses(survey);
					for (Access access: accesses)
					{
						if (access.getUser() != null && access.getUser().getId() == userid)
						{
							surveyService.deleteAccess(access);
						}
					}			
				}
				
				//Deletion of all the user's address book contacts
				List<Integer> attendees = attendeeService.getAttendeesForUser(userid);
				for (int attendee: attendees)
				{
					attendeeService.delete(attendee);
				}				
				
				//Deletion of all the user's receiving shares
				List<Share> shares = attendeeService.getPassiveShares(userid);
				for (Share share : shares)
				{
					attendeeService.deleteShare(share.getId());
				}				
				
				//Deletion of all the user's sent shares
				shares = attendeeService.getShares(userid);
				for (Share share : shares)
				{
					attendeeService.deleteShare(share.getId());
				}	
				
				//Deletion of all the user's exports
				List<Export> exports = exportService.getExports(userid, "name", true, false, false);
				for (Export export: exports)
				{
					exportService.deleteExport(export);
				}
				
				//Deletion of all the user's archived surveys
				List<Archive> archives = archiveService.getArchivesForUser(userid);
				for (Archive archive : archives)
				{
					archiveService.delete(archive);
				}
				
				//Deletion of user from USERS table
				administrationService.deleteUser(userid);				
			}
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}		
		logger.info("DeleteUserAccountsWorker completed");
	}
	
}