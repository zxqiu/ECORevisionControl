package com.eco.changeOrder.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

public class Bug implements Serializable {
    static final long serialVersionUID = -2741279213192159L;

    @JsonProperty
    private String id;

    @JsonProperty
    private String branchName; // the branch of the fix

    @JsonProperty
    private String revisionID; // the revision of the fix

    @JsonProperty
    private String comment;

    public Bug(String id, String branchName, String revisionID, String comment) {
        this.id = id;
        this.branchName = branchName;
        this.revisionID = revisionID;
        this.comment = comment;
    }

    public Bug() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getRevisionID() {
        return revisionID;
    }

    public void setRevisionID(String revisionID) {
        this.revisionID = revisionID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
