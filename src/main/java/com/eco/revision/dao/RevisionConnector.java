package com.eco.revision.dao;

import com.eco.revision.core.Revision;
import com.eco.revision.core.RevisionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
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
                revision.getAuthor(), revision.getComment(), revision.getEditTime(), revision.getEditor()
                , revision.getData().toByteArray());
    }

    public void insertBatch(List<Revision> revisions) throws IOException {
        if (revisions == null || revisions.size() == 0) {
            return;
        }

        List<String> id = new ArrayList<>();
        List<String> branchName = new ArrayList<>();
        List<String> revisionID = new ArrayList<>();
        List<Date> time = new ArrayList<>();
        List<String> author = new ArrayList<>();
        List<String> comment = new ArrayList<>();
        List<Date> editTime = new ArrayList<>();
        List<String> editor = new ArrayList<>();
        List<byte[]> data = new ArrayList<>();

        for (Revision revision : revisions) {
            id.add(revision.getId());
            branchName.add(revision.getBranchName());
            revisionID.add(revision.getRevisionId());
            time.add(revision.getTime());
            author.add(revision.getAuthor());
            comment.add(revision.getComment());
            editTime.add(revision.getEditTime());
            editor.add(revision.getEditor());
            data.add(revision.getData().toByteArray());
        }

        revisionDAO.insertBatch(id, branchName, revisionID, time, author, comment, editTime, editor, data);
    }

    public void update(String branchName, String revisionID, String editor, Date editTime, RevisionData revisionData) throws IOException {
        revisionDAO.updateByID(Revision.generateID(branchName, revisionID), editor, editTime, revisionData.toByteArray());
    }

    public List<Revision> findAll() {
        return revisionDAO.findAll();
    }

    public List<Revision> findByBranch(String branchName) {
        return revisionDAO.findByBranch(branchName);
    }

    public List<Revision> findByID(String branchName, String revisionID) {
        return revisionDAO.findByID(Revision.generateID(branchName, revisionID));
    }

    public long findLargestRevisionID(String branchName) {
        return revisionDAO.findLargestRevisionID(branchName);
    }

    public List<Revision> findLimitByBranch(String branchName, long begin, long end) {
        return revisionDAO.findLimitByBranch(branchName, begin, end);
    }

    public void delete(String id) {
        revisionDAO.deleteByID(id);
    }
}
