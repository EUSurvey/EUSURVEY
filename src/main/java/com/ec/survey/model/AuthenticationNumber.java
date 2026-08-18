package com.ec.survey.model;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "AUTHENTICATIONNUMBER")
public class AuthenticationNumber {

    private int id;
    private int number;
    private String surveyUid;
    private String email;
    private Date created;

    public AuthenticationNumber() {
    }

    public AuthenticationNumber(int number, String email, String surveyUid) {
        this.number = number;
        this.email = email;
        this.surveyUid = surveyUid;
        created = new Date();
    }

    @Id
    @Column(name = "AUTH_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "AUTH_NUMBER")
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Column(name = "AUTH_SURVEYUID")
    public String getSurveyUid() {
        return surveyUid;
    }

    public void setSurveyUid(String surveyUid) {
        this.surveyUid = surveyUid;
    }

    @Column(name = "AUTH_DATE")
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Column(name = "AUTH_EMAIL")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Transient
    public boolean isTooOld() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, -5);
        Date fiveMinutesAgo = cal.getTime();
        return getCreated().before(fiveMinutesAgo);
    }
}
