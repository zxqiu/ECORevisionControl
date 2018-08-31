package com.eco.revision.resources;


import com.codahale.metrics.annotation.Timed;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.eco.revision.core.CommitStatus;
import com.eco.svn.SVNBranch;
import com.eco.svn.SVNConf;
import com.eco.svn.SVNUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    public static final String PATH_PUT_JSON        = "/{" + Dict.BRANCH_NAME + "}/{" + Dict.REVISION_ID + "}";
    public static final String PATH_DELETE          = "/{" + Dict.BRANCH_NAME + "}/{" + Dict.REVISION_ID + "}";

    public static final Logger _logger = LoggerFactory.getLogger(RevisionResource.class);
    public static RevisionConnector revisionConnector;

    private static final long REVISION_UPDATE_INTERVAL = 30 * 1000; // ms

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
        return utilGetByBranch(branchName);
    }

    public static List<Revision> utilGetByBranch(String branchName) throws IOException, SVNException {
        _syncRevisions(branchName);
        return revisionConnector.findByBranch(branchName);
    }

    public static List<Revision> utilGetLimitByBranch(String branchName, long begin, long end) throws IOException {
        _syncRevisions(branchName);
        return revisionConnector.findLimitByBranch(branchName, begin, end);
    }

    @GET
    @Timed
    @Path(PATH_GET_REVISION)
    @Produces(MediaType.APPLICATION_JSON)
    public Revision getByID(@PathParam(Dict.BRANCH_NAME) @NotNull String branchName,
                              @PathParam(Dict.REVISION_ID) @NotNull String revisionID) throws IOException, SVNException {
        _syncRevisions(branchName);
        List<Revision> ret = revisionConnector.findByID(branchName, revisionID);

        if (ret.size() == 0 ) {
            return null;
        }

        return ret.get(0);
    }

    @PUT
    @Timed
    @Path(PATH_PUT_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam(Dict.BRANCH_NAME) String branchName,
                           @PathParam(Dict.REVISION_ID) String revisionID,
                           UpdateParamWrapper paramWrapper
                           ) throws IOException {
        List<Revision> revisions = revisionConnector.findByID(branchName, revisionID);

        if (revisions == null || revisions.size() == 0) {
            _logger.error("Requested branch name and revision ID combination not found: branch "
                    + branchName + " revision " + revisionID);
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<CommitStatus> newCommitStatuses = paramWrapper.getCommitStatuses();

        Revision revision = revisions.get(0);
        if (revision.getData() == null) {
            revision.setData(new RevisionData(new ArrayList<CommitStatus>()));
        }

        if (revision.getData().getCommitStatuses() == null) {
            revision.getData().setCommitStatuses(new ArrayList<CommitStatus>());
        }

        RevisionData revisionData = revision.getData();
        List<CommitStatus> commitStatuses = revisionData.getCommitStatuses();

        for (CommitStatus commitStatus : newCommitStatuses) {
            int i;
            for (i = 0; i < commitStatuses.size(); i++) {
                CommitStatus cs = commitStatuses.get(i);
                if (cs.getBranchName().equals(commitStatus.getBranchName())) {
                    cs.setStatus(commitStatus.getStatus());
                    cs.setCommitID(commitStatus.getCommitID());
                    cs.setComment(commitStatus.getComment());
                    break;
                }
            }

            if (i == commitStatuses.size()) {
                // new commit status
                commitStatuses.add(commitStatus);
            }
        }

        revisionConnector.update(branchName, revisionID,
                paramWrapper.getEditor(), new Date(), revisionData);

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
                                   @FormParam(Dict.EDITOR) String editor,
                                   @FormParam(Dict.EDIT_TIME) long editTime
                                   ) throws IOException {

        Date timeParsed = new Date(time);
        Date editTimeParsed = new Date(editTime);
        Revision revision = new Revision(Revision.generateID(branchName, revisionId),
                                         branchName,
                                         revisionId,
                                         timeParsed,
                                         author,
                                         comment,
                                         editor,
                                         editTimeParsed,
                                         new RevisionData());
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
                    if (branchName != null && branchName.length() > 0 && branchName.equals(svnBranch.getBranchName()) == false) {
                        continue;
                    } else if (new Date().getTime() - svnBranch.getLastUpdate() < REVISION_UPDATE_INTERVAL) {
                        continue;
                    }

                    Long latestRevisionID = revisionConnector.findLargestRevisionID(svnBranch.getBranchName());
                    String user = (svnBranch.getUser() != null && svnBranch.getUser().length() > 0) ?
                                    svnBranch.getUser() : svnConf.getUserDefault();
                    String password = (svnBranch.getPassword() != null && svnBranch.getPassword().length() > 0) ?
                                    svnBranch.getPassword() : svnConf.getPasswordDefault();

                    if (latestRevisionID != null) {
                        SVNUtils.updateLog(revisionConnector, svnBranch.getRepo(),
                                svnBranch.getBranchName(), user, password,
                                latestRevisionID, -1, false);
                    } else {
                        SVNUtils.updateLog(revisionConnector, svnBranch.getRepo(),
                                svnBranch.getBranchName(), user, password,
                                0, -1, false);
                    }

                    svnBranch.setLastUpdate(new Date().getTime());
                }

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(SVNConf.SVNConfFile), svnConf);
            } catch (SVNException e) {
                _logger.error("Failed to update SVN revision");
                e.printStackTrace();
            } finally {
                revisionUpdateLock.unlock();
            }
        }
    }
}

class UpdateParamWrapper {
    @JsonProperty
    @NotNull
    private String editor;

    @JsonProperty
    @NotNull
    private List<CommitStatus> commitStatuses;

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public List<CommitStatus> getCommitStatuses() {
        return commitStatuses;
    }

    public void setCommitStatuses(List<CommitStatus> commitStatuses) {
        this.commitStatuses = commitStatuses;
    }
}

