package com.eco.revision.resources;


import com.codahale.metrics.annotation.Timed;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eco.revision.core.Revision;
import com.eco.revision.core.RevisionData;
import com.eco.revision.dao.RevisionConnector;
import com.eco.revision.dao.RevisionDAO;
import com.eco.utils.misc.Dict;

/**
 * Created by neo on 8/16/18.
 */

@Path(Dict.API_V1_PATH + RevisionResource.PATH)
public class RevisionResource {
    public static final String PATH = "/revisions";
    public static final String PATH_GET = "/{" + Dict.BRANCH_NAME + "}/{" + Dict.REVISION_ID + "}";
    public static final String PATH_GET_ALL = "/";
    public static final String PATH_INSERT_FORM = "/";
    public static final String PATH_INSERT_OBJ = "/obj";
    public static final String PATH_DELETE = "/{" + Dict.BRANCH_NAME + "}/{" + Dict.REVISION_ID + "}";

    public static final Logger _logger = LoggerFactory.getLogger(RevisionResource.class);
    public static RevisionConnector revisionConnector;

    public RevisionResource(RevisionDAO revisionDAO) throws Exception {
        RevisionConnector.init(revisionDAO);
        revisionConnector = RevisionConnector.getInstance();
    }

    @GET
    @Timed
    @Path(PATH_GET_ALL)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Revision> getAll() {
        return revisionConnector.findAll();
    }

    @GET
    @Timed
    @Path(PATH_GET)
    @Produces(MediaType.APPLICATION_JSON)
    public Revision get(@PathParam(Dict.BRANCH_NAME) @NotNull String branchName,
                              @PathParam(Dict.REVISION_ID) @NotNull String revisionID) {
        return revisionConnector.findByID(Revision.generateID(branchName, revisionID)).get(0);
    }

    @POST
    @Timed
    @Path(PATH_INSERT_FORM)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertRevision(@FormParam(Dict.BRANCH_NAME) String branchName,
                                   @FormParam(Dict.REVISION_ID) String revisionId,
                                   @FormParam(Dict.TIME) String time,
                                   @FormParam(Dict.AUTHOR) String author,
                                   @FormParam(Dict.COMMENT) String comment,
                                   @FormParam(Dict.STATUS) int status,
                                   @FormParam(Dict.COMMITTER) String committer,
                                   @FormParam(Dict.COMMIT_ID) String commitID,
                                   @FormParam(Dict.EDIT_TIME) String editTime
                                   ) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat(Dict.TIME_FORMAT);
        Date timeParsed = null;
        Date editTimeParsed = null;

        try {
            timeParsed = sdf.parse(time);
            if (editTime != null && editTime.trim().length() == Dict.TIME_FORMAT.length()) {
                editTimeParsed = sdf.parse(editTime);
            }
        } catch (ParseException pe) {
            _logger.error("Error : failed to parse event date : " + pe.getMessage());
            pe.printStackTrace();
            return Response.serverError().build();
        }
        Revision revision = new Revision(Revision.generateID(branchName, revisionId),
                                         branchName,
                                         revisionId,
                                         timeParsed,
                                         author,
                                         status,
                                         committer,
                                         commitID,
                                         editTimeParsed,
                                         new RevisionData(comment));
        try {
            revisionConnector.insert(revision);
        } catch (IOException e) {
            _logger.error("Error : failed to insert new revision : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return Response.ok().build();
    }

    @POST
    @Timed
    @Path(PATH_INSERT_OBJ)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertRevisionObject(Revision revision) throws IOException {
        try {
            revisionConnector.insert(revision);
        } catch (Exception e) {
            _logger.error("Error : failed to insert new revision : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return Response.ok().build();
    }

    @DELETE
    @Timed
    @Path(PATH_DELETE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRevision(@PathParam(Dict.BRANCH_NAME) String branchName,
                                   @PathParam(Dict.REVISION_ID) String revisionID) {
        revisionConnector.delete(Revision.generateID(branchName, revisionID));

        return Response.ok().build();
    }
}
