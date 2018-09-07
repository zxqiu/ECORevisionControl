package com.eco.revision.dao;

import com.eco.revision.core.Revision;
import com.eco.revision.core.RevisionData;
import com.eco.utils.misc.Dict;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Date;
import java.util.List;

/**
 * Created by neo on 8/12/18.
 */
public class RevisionDAO extends AbstractDAO<Revision> {
    private SessionFactory sessionFactory;

    public RevisionDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
    }

    String insert(Revision revision) {
        return persist(revision).getId();
    }

    void insertBatch(List<Revision> revisions) {
        Session session = currentSession();

        for (int i = 0; i < revisions.size(); i++) {
            session.save(revisions.get(i));

            if (i > 0 && i % 200 == 0) {
                session.flush();
                session.clear();
            }
        }
    }

    void updateByID(String branchName, String revisionID, String editor, Date editTime, RevisionData data) {
        Revision revision = findByID(Revision.generateID(branchName, revisionID));
        revision.setEditor(editor);
        revision.setEditTime(editTime);
        revision.setData(data);

        persist(revision);
    }

    List<Revision> findAll() {
        return list(namedQuery(Revision.REVISION_QUERY_PREFIX + "findAll"));
    }

    List<Revision> findByBranch(String branchName) {
        return list(namedQuery(Revision.REVISION_QUERY_PREFIX + "findByBranch")
                    .setParameter(Dict.BRANCH_NAME, branchName)
        );
    }

    List<Revision> findLimitByBranch(String branchName, int begin, int end) {
        return list(namedQuery(Revision.REVISION_QUERY_PREFIX + "findLimitByBranch")
                    .setParameter(Dict.BRANCH_NAME, branchName)
                    .setFirstResult(begin)
                    .setMaxResults(end - begin + 1)
        );
    }

    long findRevisionIDMax(String branchName) {
        List<Object> list = namedQuery(Revision.REVISION_QUERY_PREFIX + "findRevisionIDMax")
                .setParameter(Dict.BRANCH_NAME, branchName)
                .list();

        if (list == null || list.size() == 0 || list.get(0) == null) {
            return 0;
        }

        return (long)list.get(0);
    }

    Revision findByID(String id) {
        return get(id);
    }

    void deleteByID(String id) {
        Revision revision = findByID(id);
        currentSession().delete(revision);
    }
}
