package com.eco.revision.core;

import java.util.List;

/**
 * Created by neo on 8/31/18.
 */
public interface BranchConf {
    public abstract List<Branch> getBranches();
    public abstract String getUserDefault();
    public abstract String getPasswordDefault();
    public abstract String getConfFile();
}
