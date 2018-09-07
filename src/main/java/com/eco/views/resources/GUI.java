package com.eco.views.resources;

import com.codahale.metrics.annotation.Timed;
import com.eco.changeOrder.dao.ChangeOrderDAO;
import com.eco.revision.core.Branch;
import com.eco.revision.core.BranchConf;
import com.eco.revision.core.BranchConfFactory;
import com.eco.revision.dao.RevisionConnector;
import com.eco.revision.dao.RevisionDAO;
import com.eco.revision.resources.RevisionResource;
import com.eco.svn.core.SVNBranch;
import com.eco.svn.SVNConf;
import com.eco.utils.misc.Dict;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neo on 8/26/18.
 */
@Path(GUI.PATH_ROOT)
public class GUI {
    public static final String PATH_ROOT = "";
    public static final String PATH_BRANCHES = "";
    public static final String PATH_REVISIONS = "{" + Dict.BRANCH_NAME + "}";
    public static final String PATH_CHANGE_ORDERS = "changeOrders";
    public static final String PATH_CHANGE_ORDERS_BY_BRANCH = "changeOrders/{" + Dict.BRANCH_NAME + "}";
    public static final String PATH_CHANGE_ORDER_BY_ID = "changeOrders/{" + Dict.BRANCH_NAME + "}/{" + Dict.ID + "}";

    public static final Logger _logger = LoggerFactory.getLogger(GUI.class);

    public static RevisionConnector revisionConnector = null;
    public static ChangeOrderDAO changeOrderDAO = null;

    public GUI(RevisionDAO revisionDAO, ChangeOrderDAO changeOrderDAO) throws Exception {
        RevisionConnector.init(revisionDAO);
        GUI.revisionConnector = RevisionConnector.getInstance();
        GUI.changeOrderDAO = changeOrderDAO;
    }

    @GET
    @Timed
    @Path(PATH_BRANCHES)
    @Produces(MediaType.TEXT_HTML)
    public Response branches() throws IOException {
        BranchConf branchConf = BranchConfFactory.getBranchConf();
        List<Map<String, String>> branches = new ArrayList<>();

        for (Branch branch : branchConf.getBranches()) {
            Map<String, String> map = new HashMap<>();
            map.put(Dict.BRANCH_NAME, branch.getBranchName());
            map.put(Dict.URL, PATH_ROOT + "/" + branch.getBranchName() + "?" + Dict.BEGIN + "=0&" + Dict.END + "=100");
            branches.add(map);
        }

        return Response.ok().entity(
                views.revisions.template(""
                                        , ""
                                        , branchConf.getBranches()
                                        , null
                                        , "#"
                                        , "#"
                                        , "#")
        ).build();
    }

    @GET
    @Timed
    @Path("/" + PATH_REVISIONS)
    @Produces(MediaType.TEXT_HTML)
    public Response revisions(@PathParam(Dict.BRANCH_NAME) @NotNull String branchName
                              , @QueryParam(Dict.BEGIN) @NotNull Long begin
                              , @QueryParam(Dict.END) @NotNull Long end) throws IOException {
        long prevBegin = (begin - 100 < 0) ? 0 : begin - 100;
        long prevEnd = begin - 1;
        long nextBegin = end + 1;
        long nextEnd = end + 100;
        long latestRevision = revisionConnector.findLargestRevisionID(branchName);

        _logger.info("calculate display range for " + branchName + " : prev begin : " + prevBegin + " prev end : " + prevEnd + " next begin : " + nextBegin + " next end : " + nextEnd + " latest revision : " + latestRevision);

        String urlPrev = (prevEnd <= 0) ? "#" :
                String.format(PATH_ROOT + "/" + branchName + "?" + Dict.BEGIN + "=%d&" + Dict.END + "=%d", prevBegin, prevEnd);
        String urlNext = (nextBegin > latestRevision) ? "#" :
                String.format(PATH_ROOT + "/" + branchName + "?" + Dict.BEGIN + "=%d&" + Dict.END + "=%d", nextBegin, nextEnd);
        String urlBranches = PATH_ROOT + "/" + PATH_BRANCHES;

        BranchConf branchConf = BranchConfFactory.getBranchConf();

        return Response.ok().entity(
                views.revisions.template(branchName
                                        , "user1000"
                                        , branchConf.getBranches()
                                        , RevisionResource.utilGetLimitByBranch(branchName, begin, end)
                                        , urlNext
                                        , urlPrev
                                        , urlBranches)
        ).build();
    }

    @GET
    @Timed
    @Path("/" + PATH_CHANGE_ORDERS)
    @Produces(MediaType.TEXT_HTML)
    @UnitOfWork
    public Response getChangeOrders() {
        return Response.ok().entity(
                views.changeOrder.template("All"
                                            , "user1000"
                                            , changeOrderDAO.findUniqueBranches()
                                            , changeOrderDAO.findAll()
                                            , "#"
                                            , "#"
                )
        ).build();
    }
}
