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
    private String editor;

    @JsonProperty
    private String commitId;

    @JsonProperty
    private Date editTime;

    @JsonProperty
    private RevisionData data;

    public enum STATUS {
        NEW(0)
        , COMMITTED(1)
        , SKIPPED(2);

        private final int value;
        private STATUS(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static STATUS toSTATUS(int value) {
            for (STATUS s : STATUS.values()) {
                if (value == s.getValue()) {
                    return s;
                }
            }

            return null;
        }
    }

    public Revision(String id, String branchName, String revisionId, Date time, String author, int status, String editor, String commitId, Date editTime, RevisionData data) {
        this.id = id;
        this.branchName = branchName;
        this.revisionId = revisionId;
        this.time = time;
        this.author = author;
        this.status = status;
        this.editor = editor;
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

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
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

    public static void main(String[] arg) {
        Revision testRevision = new Revision("testBranchtestRevision",
                "testBranch",
                "testRevision",
                new Date(123),
                "testAuthor",
                1,
                "testEditor",
                "testCommitID",
                new Date(456),
                new RevisionData("testComment"));

        System.out.println(testRevision.toString());
    }
}
