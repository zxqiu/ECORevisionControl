package com.eco.changeOrder.dao;

import com.eco.changeOrder.core.ChangeOrder;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * Created by neo on 9/1/18.
 */
public class ChangeOrderDAO extends AbstractDAO<ChangeOrder> {
    public ChangeOrderDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public ChangeOrder findByID(String id) {
        return get(id);
    }

    public String create(ChangeOrder changeOrder) {
        return persist(changeOrder).getId();
    }

    public List<ChangeOrder> findAll() {
        return list(namedQuery("com.eco.changeOrder.core.ChangeOrder.findAll"));
    }
}
