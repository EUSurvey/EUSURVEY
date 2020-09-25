package com.ec.survey.tools;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.Setting;
import com.ec.survey.service.AnswerService;
import com.ec.survey.service.SchemaService;
import com.ec.survey.service.SettingsService;
import com.ec.survey.service.SurveyService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * This worker class intends to remove IP addresses from AnswerSet table if the
 * answerSet is older than a given gap from today
 */
@Service("answerSetAnonymWorker")
@Scope("singleton")
public class AnswerSetAnonymWorker implements Runnable {

    protected static final Logger logger = Logger.getLogger(AnswerSetAnonymWorker.class);
    protected static final int maxNumberPerRun = 100000;

    @Resource(name = "answerService")
    private AnswerService answerService;

    @Resource(name = "surveyService")
    private SurveyService surveyService;

    @Resource(name = "schemaService")
    private SchemaService schemaService;

    @Resource(name = "sessionFactory")
    protected SessionFactory sessionFactory;

    @Resource(name = "settingsService")
    private SettingsService settingsService;

    @Override
    public void run() {
        logger.info("AnswerSetAnonymWorker started");
        String enabled = settingsService.get(Setting.AnswersAnonymWorkerEnabled);
        if (StringUtils.isNotEmpty(enabled) && enabled.equalsIgnoreCase("true")) {
            String interval = settingsService.get(Setting.AnswersAnonymWorkerInterval);
            if (StringUtils.isNotEmpty(interval) && interval.length() >= 2
                    && (interval.endsWith("y") || interval.endsWith("w") || interval.endsWith("d"))) {
                Calendar todayCalendar = Calendar.getInstance();
                String beginningString = interval.substring(0, interval.length() - 1);
                char endChar = interval.charAt(interval.length() - 1);

                int time = Integer.parseInt(beginningString);

                switch (endChar) {
                    case 'y':
                        todayCalendar.add(Calendar.YEAR, -time);
                        break;
                    case 'w':
                        todayCalendar.add(Calendar.WEEK_OF_YEAR, -time);
                        break;
                    case 'd':
                        todayCalendar.add(Calendar.DAY_OF_WEEK, -time);
                        break;
                    default:
                        todayCalendar.add(Calendar.YEAR, -200);
                }

                Date maxTime = todayCalendar.getTime();

                Date lastAnswerSetAnonymDate = this.schemaService.getLastAnswerSetAnonymDate();

                if (lastAnswerSetAnonymDate == null) {
                    Calendar year0 = Calendar.getInstance();
                    year0.set(0, 0, 0);
                    lastAnswerSetAnonymDate = year0.getTime();
                }

                List<AnswerSet> answerSetsToAnonymise = this.answerService.getAnswerSetsToAnonymize(maxTime, maxNumberPerRun);
                if (answerSetsToAnonymise.size()>=1) {
                    Date newLastAnswerSetAnonymisedDate = answerSetsToAnonymise.get(answerSetsToAnonymise.size() - 1)
                        .getDate();
                    this.answerService.anonymiseAnswerSets(answerSetsToAnonymise);
                    this.schemaService.saveLastAnswerSetAnonymDate(newLastAnswerSetAnonymisedDate);
                    logger.info("AnswerSetAnonymWorker succesfully anonymised " + answerSetsToAnonymise.size() + " answers, last one's date was " + DateFormat.getInstance().format(newLastAnswerSetAnonymisedDate));
                } else {
                    logger.info("AnswerSetAnonymWorker couldn't find any more answers to anonymise before " + DateFormat.getInstance().format(maxTime));
                }
            } else {
                logger.error("Please check that AnswersAnonymWorkerEnabled setting is enabled by schema step 95");
            }
        } else {
            logger.info("AnswerSetAnonymWorker is not enabled");
        }
       
    }
   
}