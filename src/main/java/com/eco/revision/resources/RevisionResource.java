package com.eco.revision.resources;


import com.codahale.metrics.annotation.Timed;
import com.eco.revision.core.Revision;
import com.eco.revision.dao.RevisionConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by neo on 8/16/18.
 */

@Path("/v1/revisions")
public class RevisionResource {
    public static final Logger _logger = LoggerFactory.getLogger(RevisionResource.class);

    public static final RevisionConnector revisionConnector = RevisionConnector.getInstance();

    @GET
    @Timed
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Revision> getAll() {
        return revisionConnector.findAll();
    }
}
