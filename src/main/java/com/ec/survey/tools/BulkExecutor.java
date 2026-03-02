package com.ec.survey.tools;

import com.ec.survey.exception.MessageException;
import com.ec.survey.model.*;
import com.ec.survey.model.administration.LocalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.*;
import com.ec.survey.tools.activity.ActivityRegistry;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service("bulkExecutor")
@Scope("prototype")
public class BulkExecutor implements Runnable {

    @Resource(name="surveyService")
    private SurveyService surveyService;

    @Resource(name="mailService")
    private MailService mailService;

    @Resource(name="sessionFactory")
    private SessionFactory sessionFactory;

    @Resource(name="participationService")
    private ParticipationService participationService;

    @Resource(name="administrationService")
    private AdministrationService administrationService;

    @Resource(name = "activityService")
    protected ActivityService activityService;

    protected @Autowired ServletContext servletContext;

    @Value("${server.prefix}")
    public String serverPrefix;

    public @Value("${sender}") String sender;

    private static final Logger logger = Logger.getLogger(BulkExecutor.class);

    private BulkChange change;

    public void init(BulkChange change) {
        this.change = change;
    }
	
	public void handleException(Exception e)
	{
		logger.error(e.getLocalizedMessage(), e);
		this.change.setError(e.getLocalizedMessage());
        this.change.setFinished(false);

		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();

		this.change = (BulkChange) session.merge(this.change);
		session.update(this.change);

		t.commit();
		session.close();
	}
	
	public void run()
	{
        logger.info("bulk change started");

        try {

            switch (this.change.getOperation()) {
                case AddRemovePrivilegedUsers:
                    ExecuteAddRemovePrivilegedUsers();
                    break;
                case AddRemoveTags:
                    ExecuteAddRemoveTags();
                    break;
                case PublishUnpublish:
                    ExecutePublishUnpublish();
                    break;
                case ChangeOwner:
                    ExecuteChangeOwner();
                    break;
                case DeleteSurveys:
                    ExecuteDeleteSurveys();
                    break;
                default:
                    throw new NotImplementedException();
            }

        } catch (Exception e) {
            handleException(e);
        }

		logger.info("bulk change finished");
	}

    private void ExecuteAddRemovePrivilegedUsers() {
        for (int surveyId : this.change.getSurveyIDs()) {
            Survey survey = surveyService.getSurvey(surveyId);
            try {

                switch (this.change.getPrivilegesMode()) {
                    case Add: case Replace:
                        List<Integer> ids = new ArrayList<>();
                        for (String entry : change.getPrivileges()) {
                            String[] array = entry.split("\\|");
                            String name = array[0];
                            String type = array[1];
                            String accessDraft =  array[2];
                            String accessResults =  array[3];
                            String formManagement =  array[4];
                            String manageInvitations =  array[5];

                            Access access;
                            User user = null;

                            if (type.equalsIgnoreCase("User")) {
                                user = administrationService.getUserForLogin(name);
                                access =  surveyService.getAccess(surveyId, user.getId());
                            } else {
                                access = surveyService.getGroupAccess(surveyId, name);
                            }

                            boolean newAccess = access == null;
                            String oldInfo = "";
                            if (newAccess) {
                                access = new Access();
                                access.setSurvey(survey);
                                if (type.equalsIgnoreCase("User")) {
                                    access.setUser(user);
                                } else {
                                    access.setDepartment(name);
                                }
                            } else {
                                oldInfo = access.getInfo();
                            }
                            access.getLocalPrivileges().put(LocalPrivilege.AccessDraft, Integer.parseInt(accessDraft));
                            access.getLocalPrivileges().put(LocalPrivilege.AccessResults, Integer.parseInt(accessResults));
                            access.getLocalPrivileges().put(LocalPrivilege.FormManagement, Integer.parseInt(formManagement));
                            access.getLocalPrivileges().put(LocalPrivilege.ManageInvitations, Integer.parseInt(manageInvitations));

                            surveyService.saveAccess(access);
                            String newInfo = access.getInfo();
                            if (newAccess) {
                                activityService.log(ActivityRegistry.ID_DELEGATE_MANAGER_ADD, null, newInfo, this.change.getUserId(), survey.getUniqueId());
                            } else {
                                if (!newInfo.equalsIgnoreCase(oldInfo)) {
                                    activityService.log(ActivityRegistry.ID_PRIVILEGES_EDIT, oldInfo, newInfo, this.change.getUserId(), survey.getUniqueId());
                                }
                            }

                            ids.add(access.getId());
                        }

                        if (this.change.getPrivilegesMode() == BulkChange.PrivilegesMode.Replace) {
                            // remove all others
                            List<Access> accesses = surveyService.getAccesses(surveyId);
                            List<Access> toDelete = accesses.stream().filter(a -> !ids.contains(a.getId())).collect(Collectors.toList());
                            for (Access access : toDelete) {
                                surveyService.deleteAccess(access);
                                if (access.getUser() != null) {
                                    activityService.log(ActivityRegistry.ID_PRIVILEGES_DELETE, access.getInfo(), null, this.change.getUserId(), survey.getUniqueId());
                                }
                            }
                        }

                        break;

                    case Remove:
                        for (String entry : change.getPrivileges()) {
                            String[] array = entry.split("\\|");
                            String name = array[0];
                            String type = array[1];

                            Access access;
                            User user = null;

                            if (type.equalsIgnoreCase("User")) {
                                user = administrationService.getUserForLogin(name);
                                access =  surveyService.getAccess(surveyId, user.getId());
                            } else {
                                access = surveyService.getGroupAccess(surveyId, name);
                            }

                            if (access != null) {
                                surveyService.deleteAccess(access);
                                activityService.log(ActivityRegistry.ID_PRIVILEGES_DELETE, access.getInfo(), null, this.change.getUserId(), survey.getUniqueId());
                            }
                        }
                        break;
                }

                change.getSuccesses().add(surveyId);

            } catch (Exception e) {
                change.getFails().add(surveyId);
            }
        }

        change.setFinished(true);
        surveyService.save(change);
    }

    private void ExecuteAddRemoveTags() {
        for (int surveyId : this.change.getSurveyIDs()) {
            Survey survey = surveyService.getSurvey(surveyId, false, false, false, false, true);
            try {

                String oldTags = survey.getTags() == null ? "" : survey.getTags().stream().map(Tag::getName).collect(Collectors.joining(","));

                switch (this.change.getTagsMode()) {
                    case Add: case Replace:

                        if (this.change.getTagsMode() == BulkChange.TagsMode.Replace) {
                            // remove all others
                            List<Tag> toDelete = survey.getTags().stream().filter(t -> !change.getTags().contains(t.getName())).collect(Collectors.toList());
                            for (Tag t : toDelete) {
                                survey.getTags().remove(t);
                            }
                        }

                        for (String tagname : change.getTags()) {
                            Tag tag = surveyService.getTag(tagname);
                            if (tag == null) {
                                tag = surveyService.createTag(tagname);
                            }

                            Optional<Tag> existingTag = survey.getTags().stream().filter(t -> t.getName().equals(tagname)).findFirst();
                            if (existingTag.isEmpty()) {
                                if (survey.getTags().size() > 9) {
                                    throw new MessageException("The survey " + survey.getShortname() + " already has 10 tags");
                                }
                                survey.getTags().add(tag);
                            }
                        }

                        surveyService.update(survey, false);
                        String newTags = survey.getTags() == null ? "" : survey.getTags().stream().map(Tag::getName).collect(Collectors.joining(","));
                        activityService.log(ActivityRegistry.ID_PROPERTIES, "Tags: " + oldTags, "Tags: " + newTags, this.change.getUserId(), survey.getUniqueId());

                        break;

                    case Remove:
                        for (String tagname : change.getTags()) {
                            Optional<Tag> tag = survey.getTags().stream().filter(t -> t.getName().equals(tagname)).findFirst();
                            tag.ifPresent(value -> survey.getTags().remove(value));
                        }
                        surveyService.update(survey, false);
                        String newTagsRemove = survey.getTags() == null ? "" : survey.getTags().stream().map(Tag::getName).collect(Collectors.joining(","));
                        activityService.log(ActivityRegistry.ID_PROPERTIES, "Tags: " + oldTags, "Tags: " + newTagsRemove, this.change.getUserId(), survey.getUniqueId());
                        break;
                }

                change.getSuccesses().add(surveyId);

            } catch (Exception e) {
                change.getFails().add(surveyId);
            }
        }

        change.setFinished(true);
        surveyService.save(change);
    }

    private void ExecutePublishUnpublish() {
        for (int surveyId : this.change.getSurveyIDs()) {
            Survey survey = surveyService.getSurvey(surveyId);

            try {

                switch (this.change.getPublishMode()) {
                    case UnpublishAll:
                        if (survey.getIsActive()) {
                            surveyService.unpublish(survey, true, this.change.getUserId(), false);
                            activityService.log(ActivityRegistry.ID_SURVEY_STATE_MANUAL, "published", "unpublished", this.change.getUserId(), survey.getUniqueId());
                            this.change.getSuccesses().add(surveyId);
                        }
                        break;
                    case PublishNoPendingChanges:
                        if (!survey.getHasPendingChanges() && !survey.getIsActive()) {
                            publish(survey);
                            activityService.log(ActivityRegistry.ID_SURVEY_STATE_MANUAL, "unpublished", "published", this.change.getUserId(), survey.getUniqueId());
                            this.change.getSuccesses().add(surveyId);
                        }
                        break;
                    case PublishApplyPendingChanges:
                        if (survey.getHasPendingChanges() || !survey.getIsActive()) {
                            publish(survey);
                            activityService.log(ActivityRegistry.ID_SURVEY_STATE_MANUAL, "unpublished", "published", this.change.getUserId(), survey.getUniqueId());
                            this.change.getSuccesses().add(surveyId);
                        }
                        break;
                }

            } catch (Exception e) {
                change.getFails().add(surveyId);
            }

        }

        change.setFinished(true);
        surveyService.save(change);
    }

    private void publish(Survey survey) throws Exception {
        Survey published = surveyService.getSurvey(survey.getShortname(), false, false, false, false, null,
                true, false);

        if (published != null) {
            try {
                surveyService.applyChanges(survey, true, this.change.getUserId(), true);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
            }
        } else {
            if (survey.getIsEVote()) {
                List<ParticipationGroup> group = participationService.getAll(survey.getUniqueId());
                if (group.isEmpty()) {
                    throw new MessageException("You cannot publish an eVote survey with no voter file");
                }
            }

            surveyService.publish(survey, -1, -1, true, this.change.getUserId(), false, true);
        }

        surveyService.activate(survey, true, this.change.getUserId());
    }

    private void ExecuteDeleteSurveys() throws MessageException, IOException {
        for (int surveyId : this.change.getSurveyIDs()) {
            try {
                Survey survey = surveyService.getSurvey(surveyId);
                surveyService.markDeleted(this.change.getUserId(), survey.getShortname(), survey.getUniqueId(), survey.getIsPublished());

                if (this.change.getSendEmails()) {
                    SendNotificationEmails(survey);
                }

                change.getSuccesses().add(surveyId);
            } catch (Exception e) {
                change.getFails().add(surveyId);
            }
        }

        change.setFinished(true);
        surveyService.save(change);
    }

    private void SendNotificationEmails(Survey survey) throws IOException, MessageException {
        List<Access> accesses = surveyService.getAccesses(survey.getId());
        for (Access access : accesses) {
            if (access.getUser() != null) {
                String body = "Dear " + access.getUser().getFirstLastName() + ",<br /><br />The following survey was deleted: <br /><br />" + survey.getShortname();

                InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
                String text = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body).replace("[HOST]", serverPrefix);

                mailService.SendHtmlMail(access.getUser().getEmail(), sender, sender, "EUSurvey Deletion", text, null);
            }
        }
    }

    private void ExecuteChangeOwner() {

        for (int surveyId : this.change.getSurveyIDs()) {

            Survey survey = surveyService.getSurvey(surveyId);

            ChangeOwnerRequest changeRequest = new ChangeOwnerRequest();
            changeRequest.setCreated(new Date());
            changeRequest.setSurveyUid(survey.getUniqueId());
            changeRequest.setCode(UUID.randomUUID().toString());
            changeRequest.setAddAsFormManager(this.change.getAddAsFormManager());

            if (!this.change.getNewOwner().contains("@")) {
                changeRequest.setLogin(this.change.getNewOwner());
            } else {
                changeRequest.setEmail(this.change.getNewOwner());
            }

            if (surveyService.sendChangeOwnerRequest(changeRequest)) {
                change.getSuccesses().add(surveyId);
            } else {
                change.getFails().add(surveyId);
            }
        }

        change.setFinished(true);
        surveyService.save(change);
    }

}
