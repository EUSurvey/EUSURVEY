package com.ec.survey.tools.activity;

public class ActivityRegistryEntry {

    private final String property;
    private final String event;
    private final String object;
    private final int id;

    public ActivityRegistryEntry(String property, String event, String object, int id){
        this.property = property;
        this.event = event;
        this.object = object;
        this.id = id;
    }

    public String getProperty() {
        return property;
    }

    public String getEvent() {
        return event;
    }

    public String getObject() {
        return object;
    }

    public int getId() {
        return id;
    }
}
