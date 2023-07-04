package com.ec.survey.model.delphi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ec.survey.tools.ConversionTools;

public class DelphiComment {
    private final String user;
    private final String text;
    private final String date;
    private final int id;
    private final String answerSetUniqueCode;
    private final List<DelphiComment> replies;
    private final boolean unread;
    private List<String> likes;
    private int numLikes;

    public DelphiComment(String user, String text, Date date, int id, String answerSetUniqueCode, boolean unread, List<String> likes) {
    	this.user = user;
    	this.text = text;
    	this.date = ConversionTools.getFullString(date);
    	this.id = id;
    	this.answerSetUniqueCode = answerSetUniqueCode;
    	this.replies = new ArrayList<>();
    	this.unread = unread;
    	this.likes = likes;
    	this.numLikes = likes.size();
    }
    
    public String getUser() {
        return user;
    }

    public String getText() {
        return text;
    }
    
    public String getDate() {
        return date;
    }
    
    public int getId() {
        return id;
    }

    public String getAnswerSetUniqueCode() { return answerSetUniqueCode; }
    
    public List<DelphiComment> getReplies() {
    	return replies;
    }

    public boolean isUnread() {
        return unread;
    }

    public List<String> getLikes() {
        return this.likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
        this.numLikes = likes.size();
    }

    public int getNumLikes() {
        return this.numLikes;
    }

    //no setter as this shouldn't be set independently from likes
}
