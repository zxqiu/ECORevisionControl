package com.eco.svn.core;

import com.eco.revision.core.Branch;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by neo on 8/25/18.
 */
public class SVNBranch implements Branch {
    @JsonProperty
    private String user;

    @JsonProperty
    private String password;

    @JsonProperty
    private String branchName;

    @JsonProperty
    private String repo;

    @JsonProperty
    private long lastUpdate;

    @JsonIgnore
    private String url;

    public SVNBranch() {}

    public String getURL() {
        return "/" + branchName + "?begin=0&end=100";
    }

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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
