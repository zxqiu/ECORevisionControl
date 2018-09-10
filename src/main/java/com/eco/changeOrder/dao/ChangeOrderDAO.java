package com.eco.changeOrder.dao;

import com.eco.changeOrder.core.ChangeOrder;
import com.eco.utils.misc.Dict;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Set;

/**
 * Created by neo on 9/1/18.
 */
public class ChangeOrderDAO extends AbstractDAO<ChangeOrder> {
    public ChangeOrderDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public ChangeOrder findByID(Long id) {
        return get(id);
    }

    public Long create(ChangeOrder changeOrder) {
        return persist(changeOrder).getId();
    }

    public List<ChangeOrder> findAll(int begin, int end) {
        return list(namedQuery(ChangeOrder.CHANGE_ORDER_QUERY_PREFIX + "findAll")
                .setFirstResult(begin)
                .setMaxResults(end - begin + 1)
        );
    }

    public List<ChangeOrder> findByBranch(String branchName, int begin, int end) {
        return list(namedQuery(ChangeOrder.CHANGE_ORDER_QUERY_PREFIX + "findByBranch")
                .setParameter(Dict.BRANCH_NAME, branchName)
                .setFirstResult(begin)
                .setMaxResults(end - begin + 1)
        );
    }

    public List<String> findUniqueBranches() {
        return list(namedQuery(ChangeOrder.CHANGE_ORDER_QUERY_PREFIX + "findBranches"));
    }

    public long findChangeOrderCount(String branchName) {
        List<Object> list;

        if (branchName != null && branchName.length() > 0) {
            list = namedQuery(ChangeOrder.CHANGE_ORDER_QUERY_PREFIX + "findChangeOrderCountByBranch")
                    .setParameter(Dict.BRANCH_NAME, branchName)
                    .list();
        } else {
            list = namedQuery(ChangeOrder.CHANGE_ORDER_QUERY_PREFIX + "findChangeOrderCount")
                    .list();
        }

        if (list == null || list.size() == 0 || list.get(0) == null) {
            return 0;
        }

        return (long) list.get(0);
    }

    public void update(ChangeOrder changeOrder) {
        persist(changeOrder);
    }

    public void delete(Long id) {
        ChangeOrder changeOrder = findByID(id);
        currentSession().delete(changeOrder);
    }
}
