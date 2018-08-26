package com.eco.svn;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by neo on 8/25/18.
 */
public class SVNBranch {
    @JsonProperty
    private String branchName;

    @JsonProperty
    private String repo;

    @JsonProperty
    private long lastUpdate;

    public SVNBranch() {}

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
