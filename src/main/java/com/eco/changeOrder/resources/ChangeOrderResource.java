package com.eco.changeOrder.resources;

import com.codahale.metrics.annotation.Timed;
import com.eco.changeOrder.core.Bug;
import com.eco.changeOrder.core.ChangeOrder;
import com.eco.changeOrder.core.ChangeOrderData;
import com.eco.changeOrder.dao.ChangeOrderDAO;
import com.eco.revision.core.*;
import com.eco.revision.dao.RevisionDAI;
import com.eco.revision.resources.RevisionResource;
import com.eco.svn.SVNConf;
import com.eco.utils.exception.GeneralException;
import com.eco.utils.misc.Dict;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

/**
 * Created by neo on 9/1/18.
 */

@Path(Dict.API_V1_PATH + "/" + ChangeOrderResource.PATH_ROOT)
public class ChangeOrderResource {
    public static final String PATH_ROOT = "changeOrders";
    public static final String PATH_GET_ALL = "";
    public static final String PATH_GET = "{" + Dict.ID + "}";
    public static final String PATH_POST = "{" + Dict.ID + "}";
    public static final String PATH_PATCH = "{" + Dict.ID + "}";
    public static final String PATH_DELETE = "{" + Dict.ID + "}";

    private ChangeOrderDAO changeOrderDAO;
    private RevisionDAI revisionDAI;

    public ChangeOrderResource(RevisionDAI revisionDAI, ChangeOrderDAO changeOrderDAO) {
        this.revisionDAI = revisionDAI;
        this.changeOrderDAO = changeOrderDAO;
    }

    @GET
    @Path("/" + PATH_GET_ALL)
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public List<ChangeOrder> getAll() {
        return changeOrderDAO.findAll();
    }

    @GET
    @Path("/" + PATH_GET)
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public ChangeOrder get(@PathParam(Dict.ID) @NotEmpty String id) {
        return changeOrderDAO.findByID(id);
    }

    @POST
    @Path("/" + PATH_POST)
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response insert(@PathParam(Dict.ID) String id
                        , @Valid ChangeOrder changeOrderNew) throws IOException, GeneralException {
        ChangeOrder changeOrder = changeOrderDAO.findByID(id);

        if (changeOrderNew.getData() != null && changeOrderNew.getData().checkBugs() == false) {
            throw new GeneralException(Response.Status.BAD_REQUEST, "Errors exists in bug list");
        }

        if (changeOrder != null) {
            // need to clear all the revisions before insert
            if (changeOrder.getData() != null && changeOrder.getData().getBugs() != null) {
                for (Bug bug : changeOrder.getData().getBugs()) {
                    Revision revision = revisionDAI.findByID(bug.getBranchName(), bug.getRevisionID());

                    if (revision == null || revision.getData() == null) {
                        continue;
                    }

                    CommitStatus commitStatus = revision.getData().getCommitStatusByCommitID(changeOrder.getId());

                    if (commitStatus == null) {
                        continue;
                    }

                    revision.getData().getCommitStatuses().remove(commitStatus);
                    revisionDAI.update(revision.getBranchName(), revision.getRevisionId(), revision.getEditor()
                                    , revision.getEditTime(), revision.getData());
                }
            }

            changeOrder.setAuthor(changeOrderNew.getAuthor());
            changeOrder.setTime(changeOrderNew.getTime());
            changeOrder.setEditor(changeOrderNew.getEditor());
            changeOrder.setEditTime(new Date());
            changeOrder.setData(changeOrderNew.getData());
        } else {
            changeOrder = changeOrderNew;
        }

        changeOrderDAO.create(changeOrder);
        BranchConf branchConf = BranchConfFactory.getBranchConf();
        Set<Branch> branchSet = new HashSet<>(branchConf.getBranches());
        Set<String> branchNames = new HashSet<>();

        for (Branch branch : branchSet) {
            branchNames.add(branch.getBranchName());
        }

        // update revisions
        if (changeOrder.getData() != null && changeOrder.getData().getBugs() != null) {
            for (Bug bug : changeOrder.getData().getBugs()) {
                if (branchNames.contains(bug.getBranchName())
                        && bug.getBranchName() != null && bug.getBranchName().length() > 0
                        && bug.getRevisionID() != null && bug.getRevisionID().length() > 0) {
                    Revision revision = revisionDAI.findByID(bug.getBranchName(), bug.getRevisionID());

                    if (revision == null) {
                        // not found. Sync revisions and try again.
                        RevisionResource.syncRevisions(bug.getBranchName());
                        revision = revisionDAI.findByID(bug.getBranchName(), bug.getRevisionID());

                        if (revision == null) {
                            continue;
                        }
                    }

                    if (revision.getData() == null) {
                        revision.setData(new RevisionData());
                    }

                    if (revision.getData().getCommitStatuses() == null) {
                        revision.getData().setCommitStatuses(new ArrayList<>());
                    }

                    List<CommitStatus> commitStatuses = revision.getData().getCommitStatuses();

                    int i;
                    for (i = 0; i < commitStatuses.size(); i++) {
                        CommitStatus commitStatus = commitStatuses.get(i);
                        if (commitStatus.getBranchName().equals(changeOrder.getBranchName())) {
                            commitStatus.setStatus(Revision.STATUS.COMMITTED.getValue());
                            commitStatus.setCommitID(changeOrder.getId());
                            commitStatus.setComment(bug.getComment());

                            break;
                        }
                    }

                    if (i == commitStatuses.size()) {
                        commitStatuses.add(new CommitStatus(changeOrder.getBranchName()
                                , Revision.STATUS.COMMITTED.getValue()
                                , changeOrder.getId()
                                , bug.getComment())
                        );
                    }

                    revisionDAI.update(bug.getBranchName(), bug.getRevisionID()
                            , changeOrder.getAuthor(), new Date(), revision.getData());
                }
            }
        }

        return Response.ok().build();
    }

    @PATCH
    @Path("/" + PATH_PATCH)
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response update(@PathParam(Dict.ID) @NotEmpty String id
                        , @Valid ChangeOrderData changeOrderData) {
        ChangeOrder changeOrder = changeOrderDAO.findByID(id);
        changeOrder.setData(changeOrderData);
        changeOrderDAO.update(changeOrder);

        return Response.ok().build();
    }

    @DELETE
    @Path("/" + PATH_DELETE)
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response delete(@PathParam(Dict.ID) @NotEmpty String id) throws IOException {
        ChangeOrder changeOrder = changeOrderDAO.findByID(id);

        if (changeOrder == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        ChangeOrderData changeOrderData = changeOrder.getData();

        if (changeOrderData != null) {
            List<Bug> bugList = changeOrderData.getBugs();

            if (bugList != null) {
                for (Bug bug : bugList) {
                    Revision revision = revisionDAI.findByID(bug.getBranchName(), bug.getRevisionID());

                    if (revision == null || revision.getData() == null || revision.getData().getCommitStatuses() == null) {
                        continue;
                    }

                    List<CommitStatus> commitStatuses = revision.getData().getCommitStatuses();

                    int i;
                    for (i = 0; i < commitStatuses.size(); i++) {
                        CommitStatus commitStatus = commitStatuses.get(i);
                        if (commitStatus.getBranchName().equals(changeOrder.getBranchName())) {
                            commitStatus.setStatus(Revision.STATUS.DELETED.getValue());
                            break;
                        }
                    }

                    if (i < commitStatuses.size()) {
                        revisionDAI.update(revision.getBranchName(), revision.getRevisionId(), changeOrder.getAuthor()
                                , new Date(), revision.getData());
                    }
                }
            }
        }

        changeOrderDAO.delete(id);

        return Response.ok().build();
    }

}
