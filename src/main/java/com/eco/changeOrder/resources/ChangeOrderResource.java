package com.eco.changeOrder.resources;

import com.codahale.metrics.annotation.Timed;
import com.eco.changeOrder.core.ChangeOrder;
import com.eco.changeOrder.dao.ChangeOrderDAO;
import com.eco.utils.misc.Dict;
import io.dropwizard.hibernate.UnitOfWork;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by neo on 9/1/18.
 */

@Path(Dict.API_V1_PATH + "/" + ChangeOrderResource.PATH_ROOT)
public class ChangeOrderResource {
    public static final String PATH_ROOT = "changeOrder";
    public static final String PATH_GET_ALL = "";
    public static final String PATH_POST = Dict.ID;
    public static final String PATH_PUT = Dict.ID;
    public static final String PATH_DELETE = Dict.ID;

    private ChangeOrderDAO changeOrderDAO;

    public ChangeOrderResource(ChangeOrderDAO changeOrderDAO) {
        this.changeOrderDAO = changeOrderDAO;
    }

    @GET
    @Path("/" + PATH_GET_ALL)
    @Timed
    @UnitOfWork
    @Produces(MediaType.APPLICATION_JSON)
    public List<ChangeOrder> getAll() {
        return changeOrderDAO.findAll();
    }

    @POST
    @Path("/" + PATH_POST)
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response insert(@PathParam(Dict.ID) String id
                        , @Valid ChangeOrder changeOrder) {

        changeOrderDAO.create(changeOrder);
        return Response.ok().build();
    }

    @PUT
    @Path("/" + PATH_PUT)
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response update(@PathParam(Dict.ID) @NotEmpty String id
                        , @Valid ChangeOrder changeOrder) {
        changeOrderDAO.update(changeOrder);

        return Response.ok().build();
    }

    @DELETE
    @Path("/" + PATH_DELETE)
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response delete(@PathParam(Dict.ID) @NotEmpty String id) {
        changeOrderDAO.delete(id);

        return Response.ok().build();
    }
}