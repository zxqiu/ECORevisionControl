package com.eco.revision.dao;

import com.eco.revision.core.Revision;
import com.eco.revision.core.RevisionData;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by neo on 8/16/18.
 * Revision Data GUI Interface
 */
public interface RevisionDAI {
    public abstract void insert(Revision revision) throws IOException;
    public abstract void insertBatch(List<Revision> revisions) throws IOException;
    public abstract List<Revision> findAll();
    public abstract Revision findByID(String branchName, String revisionID);
    public abstract List<Revision> findByBranch(String branchName);
    public abstract long findLargestRevisionID(String branchName);
    public abstract List<Revision> findLimitByBranch(String branchName, int begin, int end);
    public abstract void delete(String id);
    public abstract void update(String branchName, String revisionID,
                                String editor, Date editTime, RevisionData revisionData) throws IOException;
}
