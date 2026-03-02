package com.ec.survey.model;

import com.ec.survey.tools.ConversionTools;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "BULKCHANGE")
public class BulkChange implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private Date started;
    private int userId;
    private Operation operation;
    private String error;
    private boolean finished;

    private List<Integer> surveyIDs = new ArrayList<>();
    private List<Integer> successes = new ArrayList<>();
    private List<Integer> fails = new ArrayList<>();
    private String newOwner;
    private boolean addAsFormManager;
    private boolean sendEmails;
    private PublishMode publishMode;
    private PrivilegesMode privilegesMode;
    private List<String> privileges = new ArrayList<>();
    private TagsMode tagsMode;
    private List<String> tags = new ArrayList<>();

    public enum Operation {
        AddRemovePrivilegedUsers,
        AddRemoveTags,
        PublishUnpublish,
        ChangeOwner,
        DeleteSurveys
    }

    public enum TagsMode {
        Add,
        Remove,
        Replace
    }

    public enum PublishMode {
        UnpublishAll,
        PublishNoPendingChanges,
        PublishApplyPendingChanges
    }

    public enum PrivilegesMode {
        Add,
        Replace,
        Remove
    }

    @Id
    @Column(name = "BULK_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = ConversionTools.DateFormat)
    @Column(name = "BULK_DATE")
    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    @Column(name = "BULK_USER")
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Column(name = "BULK_OPERATION")
    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    @Column(name = "BULK_ERROR")
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Column(name = "BULK_FINISHED")
    public boolean getFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Column(name = "BULK_SURVEYS")
    @ElementCollection
    public List<Integer> getSurveyIDs() {
        return surveyIDs;
    }

    public void setSurveyIDs(List<Integer> surveyIDs) {
        this.surveyIDs = surveyIDs;
    }

    @Column(name = "BULK_SUCCESSES")
    @ElementCollection
    public List<Integer> getSuccesses() {
        return successes;
    }

    public void setSuccesses(List<Integer> successes) {
        this.successes = successes;
    }

    @Column(name = "BULK_FAILS")
    @ElementCollection
    public List<Integer> getFails() {
        return fails;
    }

    public void setFails(List<Integer> fails) {
        this.fails = fails;
    }

    @Column(name = "BULK_NEWOWNER")
    public String getNewOwner() {
        return newOwner;
    }

    public void setNewOwner(String newOwner) {
        this.newOwner = newOwner;
    }

    @Column(name = "BULK_ADDASFORMMANAGER")
    public boolean getAddAsFormManager() {
        return addAsFormManager;
    }

    public void setAddAsFormManager(boolean addAsFormManager) {
        this.addAsFormManager = addAsFormManager;
    }

    @Column(name = "BULK_SENDEMAILS")
    public boolean getSendEmails() {
        return sendEmails;
    }

    public void setSendEmails(boolean sendEmails) {
        this.sendEmails = sendEmails;
    }

    @Column(name = "BULK_PUBLISHMODE")
    public PublishMode getPublishMode() {
        return publishMode;
    }

    public void setPublishMode(PublishMode publishMode) {
        this.publishMode = publishMode;
    }

    @Column(name = "BULK_PRIVILEGESMODE")
    public PrivilegesMode getPrivilegesMode() {
        return privilegesMode;
    }

    public void setPrivilegesMode(PrivilegesMode privilegesMode) {
        this.privilegesMode = privilegesMode;
    }

    @Column(name = "BULK_PRIVILEGES")
    @ElementCollection
    public List<String> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<String> privileges) {
        this.privileges = privileges;
    }

    @Column(name = "BULK_TAGSMODE")
    public TagsMode getTagsMode() {
        return tagsMode;
    }

    public void setTagsMode(TagsMode tagsMode) {
        this.tagsMode = tagsMode;
    }

    @Column(name = "BULK_TAGS")
    @ElementCollection
    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
