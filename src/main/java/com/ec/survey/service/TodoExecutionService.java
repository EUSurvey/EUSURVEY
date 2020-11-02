package com.ec.survey.service;

import java.util.List;

import com.ec.survey.service.ReportingService.ToDoItem;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Is a scheduled task performing the synchronization between the reporting database and the 'normal' database.
 * A Todo is a synchronization Todo. That is, a task to perform in order to sync the two databases.
 */
@Service
@Configurable
public class TodoExecutionService extends BasicService {

    @Scheduled(fixedDelay=10000) //wait for 10 seconds between calls
    public void doToDosSchedule() throws Exception {
        if (!isReportingDatabaseEnabled() || !isHost2ExecuteScheduledTask()) {
            return;
        }
        List<ToDoItem> todos = reportingService.getToDos();

        if (todos.size() > 0) {
            logger.info("Start executing " + todos.size() + " todos");

            for (ToDoItem todo : todos) {
                try {
                    reportingService.executeToDo(todo, true);
                } catch (Exception e) {
                    logger.error(e.getLocalizedMessage(), e);
                }
            }

            logger.info("Finished executing " + todos.size() + " todos");
        }
        logger.info("No more todos");
    }
}
