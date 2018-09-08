package com.eco.revision.resources;


import com.codahale.metrics.annotation.Timed;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.eco.changeOrder.core.Bug;
import com.eco.changeOrder.core.ChangeOrder;
import com.eco.changeOrder.dao.ChangeOrderDAO;
import com.eco.revision.core.*;
import com.eco.revision.dao.RevisionDAI;
import com.eco.svn.SVNUtils;
import com.eco.utils.exception.GeneralException;
import com.eco.utils.misc.JsonErrorMsg;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;
import org.hibernate.TransactionException;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eco.revision.dao.RevisionConnector;
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
    public static final String PATH_PATCH_JSON        = "/{" + Dict.BRANCH_NAME + "}/{" + Dict.REVISION_ID + "}";
    public static final String PATH_DELETE          = "/{" + Dict.BRANCH_NAME + "}/{" + Dict.REVISION_ID + "}";

    public static final Logger _logger = LoggerFactory.getLogger(RevisionResource.class);
    public static RevisionDAI revisionDAI;
    public static ChangeOrderDAO changeOrderDAO;

    private static final long REVISION_UPDATE_INTERVAL = 30 * 1000; // ms

    public RevisionResource(RevisionDAI revisionDAI, ChangeOrderDAO changeOrderDAO) throws Exception {
        RevisionResource.revisionDAI = revisionDAI;
        RevisionResource.changeOrderDAO = changeOrderDAO;
    }

    @GET
    @Timed
    @Path(PATH_GET_ALL)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public List<Revision> getAll() throws IOException, SVNException {
        syncRevisions(null);
        return revisionDAI.findAll();
    }

    @GET
    @Timed
    @Path(PATH_GET_BRANCH)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public List<Revision> getByBranch(@PathParam(Dict.BRANCH_NAME) @NotNull String branchName) throws IOException, SVNException {
        return utilGetByBranch(branchName);
    }

    @UnitOfWork
    public static List<Revision> utilGetByBranch(String branchName) throws IOException, SVNException {
        syncRevisions(branchName);
        return revisionDAI.findByBranch(branchName);
    }

    @UnitOfWork
    public static List<Revision> utilGetLimitByBranch(String branchName, int begin, int end) throws IOException {
        syncRevisions(branchName);
        return revisionDAI.findLimitByBranch(branchName, begin, end);
    }

    @GET
    @Timed
    @Path(PATH_GET_REVISION)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Revision getByID(@PathParam(Dict.BRANCH_NAME) @NotNull String branchName,
                              @PathParam(Dict.REVISION_ID) @NotNull String revisionID) throws IOException, SVNException {
        syncRevisions(branchName);
        return revisionDAI.findByID(branchName, revisionID);
    }

    @PATCH
    @Timed
    @Path(PATH_PATCH_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response update(@PathParam(Dict.BRANCH_NAME) @NotEmpty String branchName,
                           @PathParam(Dict.REVISION_ID) @NotEmpty String revisionID,
                           @Valid UpdateParamWrapper paramWrapper
                           ) throws Exception, GeneralException {
        Revision revision = revisionDAI.findByID(branchName, revisionID);

        if (revision == null) {
            _logger.error("Requested branch name and revision ID combination not found: branch "
                    + branchName + " revision " + revisionID);
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<CommitStatus> newCommitStatuses = paramWrapper.getCommitStatuses();

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

            // update change orders
            ChangeOrder changeOrder = changeOrderDAO.findByID(commitStatus.getCommitID());
            if (changeOrder == null) {
                // change order must be exists to add the new commit status
                throw new GeneralException(Response.Status.NOT_ACCEPTABLE
                        , "change order not found with given Commit ID: " + commitStatus.getCommitID());
            }

            if (changeOrder.getData() != null) {
                if (changeOrder.getData().getBugs() == null) {
                    changeOrder.getData().setBugs(new ArrayList<>());
                }

                List<Bug> bugs = changeOrder.getData().getBugs();
                int j;
                for (j = 0; j < bugs.size(); j++) {
                    Bug bug = bugs.get(j);

                    if (bug.getBranchName().equals(revision.getBranchName())
                            && bug.getRevisionID().equals(revision.getRevisionId())) {
                        break;
                    }
                }

                if (j == bugs.size() && commitStatus.getStatus() == Revision.STATUS.COMMITTED.getValue()) {
                    // Add if not exists in bug list of change order and commit status is COMMITTED
                    bugs.add(new Bug(paramWrapper.getBugID(), branchName
                            , revisionID, commitStatus.getComment()));

                    changeOrder.setEditor(paramWrapper.getEditor());
                    changeOrder.setEditTime(new Date());
                    changeOrderDAO.update(changeOrder);
                } else if (j < bugs.size()
                        && (commitStatus.getStatus() == Revision.STATUS.DELETED.getValue()
                        || commitStatus.getStatus() == Revision.STATUS.SKIPPED.getValue())) {
                    // Delete if exists in bug list of change order and commit status is DELETED or SKIPPED
                    bugs.remove(j);

                    changeOrder.setEditor(paramWrapper.getEditor());
                    changeOrder.setEditTime(new Date());
                    changeOrderDAO.update(changeOrder);
                }
            }
        }

        revisionDAI.update(branchName, revisionID,
                paramWrapper.getEditor(), new Date(), revisionData);

        return Response.ok().build();
    }

    @POST
    @Timed
    @Path(PATH_POST_FORM)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @UnitOfWork
    public Response insertRevision(@FormParam(Dict.BRANCH_NAME) @NotEmpty String branchName,
                                   @FormParam(Dict.REVISION_ID) @NotEmpty String revisionId,
                                   @FormParam(Dict.TIME) @NotNull long time,
                                   @FormParam(Dict.AUTHOR) @NotEmpty String author,
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
            revisionDAI.insert(revision);
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
    @UnitOfWork
    public Response insertRevisionObject(@NotNull @Valid Revision revision) throws IOException {
        try {
            revisionDAI.insert(revision);
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
    @UnitOfWork
    public Response deleteRevision(@PathParam(Dict.BRANCH_NAME) @NotEmpty String branchName,
                                   @PathParam(Dict.REVISION_ID) @NotEmpty String revisionID) {
        try {
            revisionDAI.delete(Revision.generateID(branchName, revisionID));
        } catch (Exception e) {
            _logger.error("Error : failed to delete revision "
                    + Revision.generateID(branchName, revisionID) + " : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return Response.ok().build();
    }

    private static final Lock revisionUpdateLock = new ReentrantLock();
    @UnitOfWork
    public static void syncRevisions(String branchName) throws IOException {
        BranchConf branchConf = BranchConfFactory.getBranchConf();

        if (revisionUpdateLock.tryLock()) {
            try {
                for (Branch branch : branchConf.getBranches()) {
                    if (branchName != null && branchName.length() > 0 && branchName.equals(branch.getBranchName()) == false) {
                        continue;
                    } else if (new Date().getTime() - branch.getLastUpdate() < REVISION_UPDATE_INTERVAL) {
                        continue;
                    }

                    Long latestRevisionID = revisionDAI.findLargestRevisionID(branch.getBranchName());
                    String user = (branch.getUser() != null && branch.getUser().length() > 0) ?
                                    branch.getUser() : branchConf.getUserDefault();
                    String password = (branch.getPassword() != null && branch.getPassword().length() > 0) ?
                                    branch.getPassword() : branchConf.getPasswordDefault();

                    if (latestRevisionID != null) {
                        SVNUtils.updateLog(revisionDAI, branch.getRepo(),
                                branch.getBranchName(), user, password,
                                latestRevisionID, -1, false);
                    } else {
                        SVNUtils.updateLog(revisionDAI, branch.getRepo(),
                                branch.getBranchName(), user, password,
                                0, -1, false);
                    }

                    branch.setLastUpdate(new Date().getTime());
                }

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(branchConf.getConfFile()), branchConf);
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
    @NotEmpty(message = "editor cannot be null")
    private String editor;

    @JsonProperty
    @Valid
    private List<CommitStatus> commitStatuses;

    @JsonProperty
    private String bugID;

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

    public String getBugID() {
        return bugID;
    }

    public void setBugID(String bugID) {
        this.bugID = bugID;
    }
}

