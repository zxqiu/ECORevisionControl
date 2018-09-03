package com.eco.changeOrder.resources;

import com.codahale.metrics.annotation.Timed;
import com.eco.changeOrder.core.ChangeOrder;
import com.eco.changeOrder.dao.ChangeOrderDAO;
import com.eco.utils.misc.Dict;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by neo on 9/1/18.
 */

@Path(Dict.API_V1_PATH + "/" + ChangeOrderResource.PATH_ROOT)
public class ChangeOrderResource {
    public static final String PATH_ROOT = "changeOrder";
    public static final String PATH_GET_ALL = "";

    private ChangeOrderDAO changeOrderDAO;

    public ChangeOrderResource(ChangeOrderDAO changeOrderDAO) {
        this.changeOrderDAO = changeOrderDAO;
    }

    @GET
    @Path(PATH_GET_ALL)
    @Timed
    @UnitOfWork
    @Produces(MediaType.APPLICATION_JSON)
    public List<ChangeOrder> getAll() {
        return changeOrderDAO.findAll();
    }
}
