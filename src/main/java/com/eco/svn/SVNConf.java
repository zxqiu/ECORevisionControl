package com.eco.svn;

import com.eco.revision.core.Branch;
import com.eco.revision.core.BranchConf;
import com.eco.svn.core.SVNBranch;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by neo on 8/25/18.
 */
public class SVNConf implements BranchConf {
    @JsonProperty
    private String userDefault;

    @JsonProperty
    private String passwordDefault;

    @JsonProperty
    private List<SVNBranch> branches;

    @JsonIgnore
    private static String confFile = "SVNConf.json";

    public SVNConf() {}

    private static SVNConf svnConf;
    private static final Lock confReadLock = new ReentrantLock();
    private static final Logger _logger = LoggerFactory.getLogger(SVNConf.class);

    public static SVNConf getConf() throws IOException {
        if (svnConf == null) {
            confReadLock.lock();

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                svnConf = objectMapper.readValue(new File(confFile), SVNConf.class);
            } catch (Exception e) {
                _logger.error(e.getMessage());
                throw e;
            } finally {
                confReadLock.unlock();
            }
        }

        return svnConf;
    }

    @Override
    public List<Branch> getBranches() {
        List<Branch> ret = new ArrayList<>();

        for (SVNBranch svnBranch : branches) {
            ret.add(svnBranch);
        }

        return ret;
    }

    public void setBranches(List<SVNBranch> branches) {
        this.branches = branches;
    }

    @Override
    public String getUserDefault() {
        return userDefault;
    }

    public void setUserDefault(String userDefault) {
        this.userDefault = userDefault;
    }

    @Override
    public String getPasswordDefault() {
        return passwordDefault;
    }

    @Override
    @JsonIgnore
    public String getConfFile() {
        return confFile;
    }

    public void setPasswordDefault(String passwordDefault) {
        this.passwordDefault = passwordDefault;
    }

    public static void main(String[] arg) throws IOException {
        SVNConf svnConf = SVNConf.getConf();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = objectMapper.writeValueAsString(svnConf);
        System.out.println(jsonStr);
    }
}
