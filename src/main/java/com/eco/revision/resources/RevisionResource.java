package com.eco.revision.resources;


import com.codahale.metrics.annotation.Timed;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.eco.svn.SVNBranch;
import com.eco.svn.SVNConf;
import com.eco.svn.SVNUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eco.revision.core.Revision;
import com.eco.revision.core.RevisionData;
import com.eco.revision.dao.RevisionConnector;
import com.eco.revision.dao.RevisionDAO;
import com.eco.utils.misc.Dict;
import org.tmatesoft.svn.core.SVNException;

/**
 * Created by neo on 8/16/18.
 */

@Path(Dict.API_V1_PATH + RevisionResource.PATH_ROOT)
public class RevisionResource {
    public static final String PATH_ROOT            = "/revisions";
    public static final String PATH_GET_BRANCH      = "/{" + Dict.BRANCH_NAME + "}";
    public static final String PATH_GET_REVISION    = "/{" + Dict.BRANCH_NAME + "}/{" + Dict.REVISION_ID + "}";
    public static final String PATH_GET_ALL         = "/";
    public static final String PATH_POST_FORM       = "/";
    public static final String PATH_POST_OBJ        = "/obj";
    public static final String PATH_PUT_FORM        = "/{" + Dict.BRANCH_NAME + "}/{" + Dict.REVISION_ID + "}";
    public static final String PATH_DELETE          = "/{" + Dict.BRANCH_NAME + "}/{" + Dict.REVISION_ID + "}";

    public static final Logger _logger = LoggerFactory.getLogger(RevisionResource.class);
    public static RevisionConnector revisionConnector;

    private static final long REVISION_UPDATE_INTERVAL = 10 * 1000; // ms

    public RevisionResource(RevisionDAO revisionDAO) throws Exception {
        RevisionConnector.init(revisionDAO);
        revisionConnector = RevisionConnector.getInstance();
    }

    @GET
    @Timed
    @Path(PATH_GET_ALL)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Revision> getAll() throws IOException, SVNException {
        _syncRevisions(null);
        return revisionConnector.findAll();
    }

    @GET
    @Timed
    @Path(PATH_GET_BRANCH)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Revision> getByBranch(@PathParam(Dict.BRANCH_NAME) @NotNull String branchName) throws IOException, SVNException {
        _syncRevisions(branchName);
        return revisionConnector.findByBranch(branchName);
    }

    @GET
    @Timed
    @Path(PATH_GET_REVISION)
    @Produces(MediaType.APPLICATION_JSON)
    public Revision getByID(@PathParam(Dict.BRANCH_NAME) @NotNull String branchName,
                              @PathParam(Dict.REVISION_ID) @NotNull String revisionID) throws IOException, SVNException {
        _syncRevisions(branchName);
        List<Revision> ret = revisionConnector.findByID(Revision.generateID(branchName, revisionID));

        if (ret.size() == 0 ) {
            return null;
        }

        return ret.get(0);
    }

    @PUT
    @Timed
    @Path(PATH_PUT_FORM)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response update(@PathParam(Dict.BRANCH_NAME) String branchName,
                           @PathParam(Dict.REVISION_ID) String revisionID,
                           @FormParam(Dict.STATUS) @NotNull int status,
                           @FormParam(Dict.EDITOR) @NotNull String editor,
                           @FormParam(Dict.COMMIT_ID) String commitID,
                           @FormParam(Dict.EDIT_TIME) @NotNull long editTime
                           ) {
        _logger.error("process update");
        Date editTimeParsed = new Date(editTime);

        revisionConnector.update(branchName, revisionID, status, editor, commitID, editTimeParsed);

        return Response.ok().build();
    }

    @POST
    @Timed
    @Path(PATH_POST_FORM)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response insertRevision(@FormParam(Dict.BRANCH_NAME) @NotNull String branchName,
                                   @FormParam(Dict.REVISION_ID) @NotNull String revisionId,
                                   @FormParam(Dict.TIME) @NotNull long time,
                                   @FormParam(Dict.AUTHOR) @NotNull String author,
                                   @FormParam(Dict.COMMENT) String comment,
                                   @FormParam(Dict.STATUS) int status,
                                   @FormParam(Dict.EDITOR) String editor,
                                   @FormParam(Dict.COMMIT_ID) String commitID,
                                   @FormParam(Dict.EDIT_TIME) long editTime
                                   ) throws IOException {

        Date timeParsed = new Date(time);
        Date editTimeParsed = new Date(editTime);
        Revision revision = new Revision(Revision.generateID(branchName, revisionId),
                                         branchName,
                                         revisionId,
                                         timeParsed,
                                         author,
                                         status,
                                         editor,
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
    @Path(PATH_POST_OBJ)
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
        try {
            revisionConnector.delete(Revision.generateID(branchName, revisionID));
        } catch (Exception e) {
            _logger.error("Error : failed to delete revision "
                    + Revision.generateID(branchName, revisionID) + " : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return Response.ok().build();
    }

    private static final Lock revisionUpdateLock = new ReentrantLock();
    private static void _syncRevisions(String branchName) throws IOException {
        SVNConf svnConf = SVNConf.getSVNConf();

        if (revisionUpdateLock.tryLock()) {
            try {
                for (SVNBranch svnBranch : svnConf.getBranches()) {
                    if (branchName != null && branchName.length() > 0 && branchName.equals(branchName) == false) {
                        continue;
                    } else if (new Date().getTime() - svnBranch.getLastUpdate() < REVISION_UPDATE_INTERVAL) {
                        continue;
                    }

                    List<Revision> revisions = revisionConnector.findByBranch(svnBranch.getBranchName());
                    String user = (svnBranch.getUser() != null && svnBranch.getUser().length() > 0) ?
                                    svnBranch.getUser() : svnConf.getUserDefault();
                    String password = (svnBranch.getPassword() != null && svnBranch.getPassword().length() > 0) ?
                                    svnBranch.getPassword() : svnConf.getPasswordDefault();

                    try {
                        if (revisions.size() > 0) {
                            SVNUtils.updateLog(revisionConnector, svnBranch.getRepo(),
                                    svnBranch.getBranchName(), user, password,
                                    Long.valueOf(revisions.get(0).getRevisionId()), -1, false);
                        } else {
                            SVNUtils.updateLog(revisionConnector, svnBranch.getRepo(),
                                    svnBranch.getBranchName(), user, password,
                                    0, -1, false);
                        }
                    } catch (SVNException e) {
                        _logger.warn("Failed to update SVN revision from " +
                                (Long.valueOf(revisions.get(0).getRevisionId())) + " to " + -1);
                        e.printStackTrace();
                    }

                    svnBranch.setLastUpdate(new Date().getTime());
                }

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(SVNConf.SVNConfFile), svnConf);
            } finally {
                revisionUpdateLock.unlock();
            }
        }
    }
}
