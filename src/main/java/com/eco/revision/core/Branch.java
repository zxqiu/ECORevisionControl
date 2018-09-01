package com.eco.revision.core;

/**
 * Created by neo on 8/31/18.
 */
public interface Branch {
    public abstract String getURL();
    public abstract String getBranchName();
    public abstract void setBranchName(String branchName);
    public abstract String getRepo();
    public abstract void setRepo(String repo);
    public abstract long getLastUpdate();
    public abstract void setLastUpdate(long lastUpdate);
    public abstract String getUser();
    public abstract void setUser(String user);
    public abstract String getPassword();
    public abstract void setPassword(String password);
}
