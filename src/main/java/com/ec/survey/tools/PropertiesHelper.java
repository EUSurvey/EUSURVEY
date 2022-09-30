package com.ec.survey.tools;

import com.ec.survey.model.survey.Survey;

public class PropertiesHelper {

    public interface PropertyGetter {
        Object get(Survey survey);
    }

    public static boolean checkForPendingChanges(Survey a, Survey b, PropertyGetter... getters){
        for (PropertyGetter g : getters) {
            if (!Tools.isEqual(g.get(a), g.get(b))){
                return true;
            }
        }
        return false;
    }

}
