package com.eco.svn;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by neo on 8/25/18.
 */
public class SVNConf {
    @JsonProperty
    private String userDefault;

    @JsonProperty
    private String passwordDefault;

    @JsonProperty
    private List<SVNBranch> branches;

    public SVNConf() {}

    private static SVNConf svnConf;
    private static final Lock confReadLock = new ReentrantLock();
    private static final Logger _logger = LoggerFactory.getLogger(SVNConf.class);
    public static final String SVNConfFile = "SVNConf.json";

    public static SVNConf getSVNConf() throws IOException {
        if (svnConf == null) {
            confReadLock.lock();

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                svnConf = objectMapper.readValue(new File(SVNConfFile), SVNConf.class);
            } catch (Exception e) {
                _logger.error(e.getMessage());
                throw e;
            } finally {
                confReadLock.unlock();
            }
        }

        return svnConf;
    }

    public List<SVNBranch> getBranches() {
        return branches;
    }

    public void setBranches(List<SVNBranch> branches) {
        this.branches = branches;
    }

    public String getUserDefault() {
        return userDefault;
    }

    public void setUserDefault(String userDefault) {
        this.userDefault = userDefault;
    }

    public String getPasswordDefault() {
        return passwordDefault;
    }

    public void setPasswordDefault(String passwordDefault) {
        this.passwordDefault = passwordDefault;
    }

    public static void main(String[] arg) throws IOException {
        SVNConf svnConf = SVNConf.getSVNConf();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = objectMapper.writeValueAsString(svnConf);
        System.out.println(jsonStr);
    }
}