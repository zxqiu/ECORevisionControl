package com.eco.Revision.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by neo on 8/12/18.
 */
public class Revision {
    @JsonProperty
    private String id;

    @JsonProperty
    private Date time;

    @JsonProperty
    private String author;

    @JsonProperty
    private String comment;

    @JsonProperty
    private boolean isCommitted;

    @JsonProperty
    private String committer;

    @JsonProperty
    private int commitId;

    @JsonProperty
    private RevisionData data;

    public Revision(String id, Date time, String author, String comment, boolean isCommitted, String committer, int commitId, RevisionData data) {
        this.id = id;
        this.time = time;
        this.author = author;
        this.comment = comment;
        this.isCommitted = isCommitted;
        this.committer = committer;
        this.commitId = commitId;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isCommitted() {
        return isCommitted;
    }

    public void setCommitted(boolean committed) {
        isCommitted = committed;
    }

    public String getCommitter() {
        return committer;
    }

    public void setCommitter(String committer) {
        this.committer = committer;
    }

    public int getCommitId() {
        return commitId;
    }

    public void setCommitId(int commitId) {
        this.commitId = commitId;
    }

    public RevisionData getData() {
        return data;
    }

    public void setData(RevisionData data) {
        this.data = data;
    }
}
