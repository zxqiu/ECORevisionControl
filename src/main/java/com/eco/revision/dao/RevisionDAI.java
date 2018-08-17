package com.eco.revision.dao;

import com.eco.revision.core.Revision;
import com.eco.revision.core.RevisionData;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by neo on 8/16/18.
 * Revision Data Access Interface
 */
public interface RevisionDAI {
    public abstract void createTable();
    public abstract void dropTable();
    public abstract void insert(Revision revision) throws IOException;
    public abstract List<Revision> findAll();
    public abstract List<Revision> findByID(String id);
    public abstract void delete(String id);
    public abstract void updateCommitInfo(Revision revision);
}
