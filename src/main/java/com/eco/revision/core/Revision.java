package com.eco.revision.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by neo on 8/12/18.
 */
public class Revision {
    @JsonProperty
    private String id;

    @JsonProperty
    private String branchName;

    @JsonProperty
    private String revisionId;

    @JsonProperty
    private Date time;

    @JsonProperty
    private String author;

    @JsonProperty
    private boolean isCommitted;

    @JsonProperty
    private String committer;

    @JsonProperty
    private String commitId;

    @JsonProperty
    private Date editTime;

    @JsonProperty
    private RevisionData data;

    public Revision(String id, String branchName, String revisionId, Date time, String author, boolean isCommitted, String committer, String commitId, Date editTime, RevisionData data) {
        this.id = id;
        this.branchName = branchName;
        this.revisionId = revisionId;
        this.time = time;
        this.author = author;
        this.isCommitted = isCommitted;
        this.committer = committer;
        this.commitId = commitId;
        this.editTime = editTime;
        this.data = data;
    }

    public Revision() {
    }

    public static String generateID(String branchName, String revisionId) {
        return branchName + revisionId;
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

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public RevisionData getData() {
        return data;
    }

    public void setData(RevisionData data) {
        this.data = data;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }

    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }
}