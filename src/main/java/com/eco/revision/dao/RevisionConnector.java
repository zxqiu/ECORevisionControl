package com.eco.revision.dao;

import com.eco.revision.core.Revision;
import com.eco.revision.core.RevisionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by neo on 8/16/18.
 */
public class RevisionConnector implements RevisionDAI {
    private static final Lock _createLock = new ReentrantLock();
    private static final Logger _logger = LoggerFactory.getLogger(RevisionConnector.class);

    private static RevisionDAO revisionDAO = null;
    private static RevisionConnector _instance = null;

    public static void init(RevisionDAO revisionDAO) throws Exception {
        RevisionConnector.revisionDAO = revisionDAO;

        _createLock.lock();
        try {
            _instance = new RevisionConnector();
        } catch (Exception e) {
            _logger.error("Cannot create RevisionConnector instance");
            e.printStackTrace();
            throw e;
        } finally {
            _createLock.unlock();
        }
    }

    private RevisionConnector() throws Exception {
        if (revisionDAO == null) {
            throw new Exception("RevisionConnector is not initialized");
        }
    }

    public static RevisionConnector getInstance() {
       if (_instance == null) {
           _createLock.lock();
           try {
               _instance = new RevisionConnector();
           } catch (Exception e) {
               _logger.error("Cannot create RevisionConnector instance");
               e.printStackTrace();
           } finally {
               _createLock.unlock();
           }
       }

       return _instance;
    }

    public void insert(Revision revision) throws IOException {
        revisionDAO.insert(revision);
    }

    public void insertBatch(List<Revision> revisions) throws IOException {
        if (revisions == null || revisions.size() == 0) {
            return;
        }

        revisionDAO.insertBatch(revisions);
    }

    public void update(String branchName, String revisionID, String editor, Date editTime, RevisionData revisionData) throws IOException {
        revisionDAO.updateByID(branchName, revisionID, editor, editTime, revisionData);
    }

    public List<Revision> findAll() {
        return revisionDAO.findAll();
    }

    public List<Revision> findByBranch(String branchName) {
        return revisionDAO.findByBranch(branchName);
    }

    public Revision findByID(String branchName, String revisionID) {
        return revisionDAO.findByID(Revision.generateID(branchName, revisionID));
    }

    public long findLargestRevisionID(String branchName) {
        return revisionDAO.findRevisionIDMax(branchName);
    }

    public List<Revision> findLimitByBranch(String branchName, int begin, int end) {
        return revisionDAO.findLimitByBranch(branchName, begin, end);
    }

    public void delete(String id) {
        revisionDAO.deleteByID(id);
    }
}
