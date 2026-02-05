package com.ec.survey.tools;

import com.ec.survey.exception.MessageException;
import com.ec.survey.model.Access;
import com.ec.survey.model.Setting;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.service.*;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service("automaticSurveyDeleteWorker")
@Scope("singleton")
public class AutomaticSurveyDeleteWorker implements Runnable {

	protected static final Logger logger = Logger.getLogger(AutomaticSurveyDeleteWorker.class);

    @Resource(name = "surveyService")
    private SurveyService surveyService;

    @Resource(name = "activityService")
    private ActivityService activityService;

    @Resource(name = "administrationService")
    protected AdministrationService administrationService;

    @Resource(name="mailService")
    private MailService mailService;

    @Resource(name="settingsService")
    protected SettingsService settingsService;

    public @Autowired ServletContext servletContext;

    private @Value("${server.prefix}") String host;

	@Override
	public void run() {
		try {
            logger.info("AutomaticSurveyDeleteWorker started");
            String sender = settingsService.get(Setting.InactiveSurveysSender);
            int inactiveSurveysDays = Integer.parseInt(settingsService.get(Setting.InactiveSurveysDays));
            int inactiveSurveysNotification1Days = Integer.parseInt(settingsService.get(Setting.InactiveSurveysNotification1Days));
            int inactiveSurveysNotification2Days = Integer.parseInt(settingsService.get(Setting.InactiveSurveysNotification2Days));
            int inactiveSurveysNotification3Days = Integer.parseInt(settingsService.get(Setting.InactiveSurveysNotification3Days));

            List<Survey> sendFirstEmail = new ArrayList<>();
            List<Survey> sendSecondEmail = new ArrayList<>();
            List<Survey> sendThirdEmail = new ArrayList<>();
            List<Survey> delete = new ArrayList<>();
            List<Survey> reactivated = new ArrayList<>();
            surveyService.getInactiveSurveys(sendFirstEmail, sendSecondEmail, sendThirdEmail, delete, reactivated);

            for (Survey survey : sendFirstEmail) {
                sendNotificationEmail(survey, sender, inactiveSurveysDays - inactiveSurveysNotification1Days, inactiveSurveysNotification1Days, 1);
                surveyService.setDeletionMessageDate(survey.getId(), 1);
            }

            for (Survey survey : sendSecondEmail) {
                sendNotificationEmail(survey, sender, inactiveSurveysDays - inactiveSurveysNotification2Days, inactiveSurveysNotification2Days, 1);
                surveyService.setDeletionMessageDate(survey.getId(), 2);
            }

            for (Survey survey : sendThirdEmail) {
                sendNotificationEmail(survey, sender, inactiveSurveysDays - inactiveSurveysNotification3Days, inactiveSurveysNotification3Days, 1);
                surveyService.setDeletionMessageDate(survey.getId(), 3);
            }

            for (Survey survey : delete) {
                surveyService.markDeleted(-1, survey.getShortname(), survey.getUniqueId(),  survey.getIsPublished());
                sendNotificationEmail(survey, sender, 0,0, 2);
            }

            for (Survey survey : reactivated) {
                surveyService.resetInactiveDates(survey.getId());
                sendNotificationEmail(survey, sender, 0,0, 3);
            }

            logger.info("AutomaticSurveyDeleteWorker finished");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

    private void sendNotificationEmail(Survey survey, String sender, int inactiveSurveysDays, int daysUntilDeletion, int type) throws IOException, MessageException {
        StringBuilder body = new StringBuilder();
        List<User> users = new ArrayList<>();
        users.add(survey.getOwner());
        List<Access> accesses = surveyService.getAccesses(survey.getId());
        List<String> managers = new ArrayList<>();
        for (Access access: accesses) {
            if (access.getUser() != null) {
                managers.add(access.getUser().getFirstLastName());
                if (!users.contains(access.getUser())) {
                    users.add(access.getUser());
                }
            }
        }


        String title = ConversionTools.removeHTMLNoEscape(survey.getTitle());
        if (title.length() > 35) title = title.substring(0, 32) + "...";

        String surveyCreationTime = Tools.formatDate(survey.getCreated(), ConversionTools.DateFormat);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, daysUntilDeletion);
        String deletionDate = Tools.formatDate(cal.getTime(), ConversionTools.DateFormat);

        var url = host + survey.getUniqueId() + "/management/overview";
        var supportLink = host + "home/support?automateddeletion=1";

        for (User user : users) {

            body.append("Dear ").append(user.getFirstLastName()).append(",<br /><br />");

            if (type == 1) {

                body.append("You are listed as the owner/manager of the survey:<br />");
                body.append(title).append("<br />");

                body.append("<a href='").append(url).append("'>").append(url).append("</a><br /><br />");

                body.append("<ul>");
                body.append("<li>Created on: ").append(surveyCreationTime).append("</li>");
                body.append("<li>Current status: Inactive for ").append(inactiveSurveysDays).append(" days</li>");
                body.append("<li>Scheduled deletion: ").append(deletionDate).append(" (end of day, CET)</li>");
                body.append("</ul>");

                body.append("According to our data retention policy (see <a href='").append(host).append("home/tos'>Terms of Service</a>), inactive surveys are automatically deleted after a defined period.");

                body.append("<br /><br /><b>What you can do</b><br /><br />");
                body.append("Delete now (optional) <br />");
                body.append("If you no longer need this survey, you can delete it immediately from the overview page: <br />");
                body.append("<a href='").append(url).append("'>").append(url).append("</a>");

                body.append("<br /><br />Keep your content (before deletion)<br />");
                body.append("<ul>");
                body.append("<li> Export the survey and its data: ").append("<a href='").append(host).append("home/helpauthors#_Toc_10_14").append("'>How to Export a Survey and its Data?</a></li>");
                body.append("</ul>");

                body.append("Reactivate the survey (prevents deletion)<br />");
                body.append("A survey becomes active again, when one of the following happens:");
                body.append("<ol>");
                body.append("<li>You edit the questionnaire or its properties (any change).</li>");
                body.append("<li>A new response is submitted.</li>");
                body.append("</ol>");
                body.append("Once reactivated, your survey will not be deleted automatically.");

                body.append("<br /><br />Need help?");

                body.append("<ul><li>Support form: <a href='").append(supportLink).append("'>").append(supportLink).append("</a></li></ul>");

                title = "Action needed: Your EUSurvey \"" + title + "\" is scheduled for deletion on " + deletionDate;

            } else {
                body.append("We are contacting you regarding the following survey, for which you are listed as the survey owner or manager:<br />");
                body.append("<a href='").append(url).append("'>").append(url).append("</a><br /><br />");
                body.append("Survey owner: ").append(survey.getOwner().getFirstLastName());

                if (!managers.isEmpty()) {
                    body.append("<br />Survey manager(s): ");
                    boolean first = true;
                    for (String manager : managers) {
                        if (!first) body.append(", ");
                        body.append(manager);
                        first = false;
                    }
                }

                if (type == 2) {

                    body.append("<br /><br />This survey was previously inactive and has now been <b>deleted</b> on ").append(Tools.formatDate(new Date(), ConversionTools.DateFormat));
                    body.append(" as per our data retention policy (see <a href='").append(host).append("home/tos'>Terms of Service</a>).");

                    title = "Your survey has been deleted: " + title;

                } else {

                    body.append("<br /><br />This survey was previously inactive but has now been <b>reactivated</b> because a change has been made to the questionnaire or its properties or because a new contribution has been received.");
                    body.append("<br /><br />The survey is now active again and will not be subject to automatic deletion.");

                    title = "Your survey has been reactivated: " + title;

                }

                body.append("<br /><br />For any further questions, please contact us through the support form:<br />");
                body.append("<a href='").append(host).append("/home/support?automateddeletion=1'>Contact us</a>");
            }

            body.append("<br /><br />Kind regards,<br />EUSurvey team");

            String text = mailService.getEUSurveyMailTemplate(body.toString());

            mailService.SendHtmlMail(user.getEmail(), sender, sender, title, text, "Automatic Delete Message");
        }
    }

}
