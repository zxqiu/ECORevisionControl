package com.eco.revision.dao;

import com.eco.revision.core.Revision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
        revisionDAO.createTable();

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

    public void createTable() {
        revisionDAO.createTable();
    }

    public void dropTable() {
        revisionDAO.dropTable();
    }

    public void insert(Revision revision) throws IOException {
        revisionDAO.insert(revision.getId(), revision.getBranchName(), revision.getRevisionId(), revision.getTime(),
                revision.getAuthor(), revision.isCommitted(), revision.getCommitter(), revision.getCommitId(),
                revision.getEditTime(), revision.getData().toByteArray());
    }

    public void updateCommitInfo(Revision revision) {
        revisionDAO.updateCommitInfoByID(revision.getId(), revision.isCommitted(), revision.getCommitter(),
                revision.getCommitId(), revision.getEditTime());
    }

    public List<Revision> findAll() {
        return revisionDAO.findAll();
    }

    public List<Revision> findByID(String id) {
        return revisionDAO.findByID(id);
    }

    public void delete(String id) {
        revisionDAO.deleteByID(id);
    }
}
