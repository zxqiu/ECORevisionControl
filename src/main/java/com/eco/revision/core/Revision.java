package com.eco.revision.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    private int status;

    @JsonProperty
    private String committer;

    @JsonProperty
    private String commitId;

    @JsonProperty
    private Date editTime;

    @JsonProperty
    private RevisionData data;

    public Revision(String id, String branchName, String revisionId, Date time, String author, int status, String committer, String commitId, Date editTime, RevisionData data) {
        this.id = id;
        this.branchName = branchName;
        this.revisionId = revisionId;
        this.time = time;
        this.author = author;
        this.status = status;
        this.committer = committer;
        this.commitId = commitId;
        this.editTime = editTime;
        this.data = data;
    }

    public Revision() {
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return "";
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
