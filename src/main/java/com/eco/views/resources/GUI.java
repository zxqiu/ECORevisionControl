package com.eco.views.resources;

import com.codahale.metrics.annotation.Timed;
import com.eco.changeOrder.core.ChangeOrder;
import com.eco.changeOrder.dao.ChangeOrderDAO;
import com.eco.revision.core.Branch;
import com.eco.revision.core.BranchConf;
import com.eco.revision.core.BranchConfFactory;
import com.eco.revision.dao.RevisionConnector;
import com.eco.revision.dao.RevisionDAI;
import com.eco.revision.resources.RevisionResource;
import com.eco.utils.misc.Dict;
import io.dropwizard.hibernate.UnitOfWork;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

    private static int entriesPerPage = 10;
    public static final Logger _logger = LoggerFactory.getLogger(GUI.class);

    public static RevisionDAI revisionDAI = null;
    public static ChangeOrderDAO changeOrderDAO = null;

    public GUI(RevisionDAI revisionDAI, ChangeOrderDAO changeOrderDAO) throws Exception {
        GUI.revisionDAI = revisionDAI;
        GUI.changeOrderDAO = changeOrderDAO;
    }

    @GET
    @Timed
    @Path(PATH_BRANCHES)
    @Produces(MediaType.TEXT_HTML)
    @UnitOfWork
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
                                        , "#"
                                        , entriesPerPage
                )
        ).build();
    }

    @GET
    @Timed
    @Path("/" + PATH_REVISIONS)
    @Produces(MediaType.TEXT_HTML)
    @UnitOfWork
    public Response revisions(@PathParam(Dict.BRANCH_NAME) @NotNull String branchName
                              , @QueryParam(Dict.BEGIN) @NotNull Integer begin
                              , @QueryParam(Dict.END) @NotNull Integer end
    ) throws IOException {
        long max = revisionDAI.findRevisionCount(branchName);
        String urlPrev = getPrevUrl(begin, max, PATH_ROOT + "/" + branchName);
        String urlNext = getNextUrl(end, max, PATH_ROOT + "/" + branchName);
        String urlBranches = PATH_ROOT + "/" + PATH_BRANCHES;

        BranchConf branchConf = BranchConfFactory.getBranchConf();

        return Response.ok().entity(
                views.revisions.template(branchName
                                        , "user1000"
                                        , branchConf.getBranches()
                                        , RevisionResource.utilGetLimitByBranch(branchName, begin, end)
                                        , urlNext
                                        , urlPrev
                                        , urlBranches
                                        , entriesPerPage
                )
        ).build();
    }

    @GET
    @Timed
    @Path("/" + PATH_CHANGE_ORDERS)
    @Produces(MediaType.TEXT_HTML)
    @UnitOfWork
    public Response getChangeOrders(
            @QueryParam(Dict.BEGIN) @NotNull Integer begin
            , @QueryParam(Dict.END) @NotNull Integer end
    ) {
        _logger.info("0");
        long max = changeOrderDAO.findChangeOrderCount(null);
        _logger.info("1");
        String urlPrev = getPrevUrl(begin, max, PATH_ROOT + "/" + PATH_CHANGE_ORDERS+ "/");
        String urlNext = getNextUrl(end, max, PATH_ROOT + "/" + PATH_CHANGE_ORDERS+ "/");

        return Response.ok().entity(
                views.changeOrders.template("All"
                                            , "user1000"
                                            , changeOrderDAO.findUniqueBranches()
                                            , changeOrderDAO.findAll(begin, end)
                                            , urlNext
                                            , urlPrev
                                            , entriesPerPage
                )
        ).build();
    }

    @GET
    @Timed
    @Path("/" + PATH_CHANGE_ORDERS_BY_BRANCH)
    @Produces(MediaType.TEXT_HTML)
    @UnitOfWork
    public Response getChangeOrderByBranch(@PathParam(Dict.BRANCH_NAME) @NotEmpty String branchName
            , @QueryParam(Dict.BEGIN) @NotNull Integer begin
            , @QueryParam(Dict.END) @NotNull Integer end
    ) {
        long max = changeOrderDAO.findChangeOrderCount(null);
        String urlPrev = getPrevUrl(begin, max, PATH_ROOT + "/" + PATH_CHANGE_ORDERS + "/" + branchName);
        String urlNext = getNextUrl(end, max, PATH_ROOT + "/" + PATH_CHANGE_ORDERS+ "/" + branchName);

        return Response.ok().entity(
                views.changeOrders.template(
                        branchName
                        , "user1000"
                        , changeOrderDAO.findUniqueBranches()
                        , changeOrderDAO.findByBranch(branchName, begin, end)
                        , urlNext
                        , urlPrev
                        , entriesPerPage
                )
        ).build();
    }

    private String getPrevUrl(int begin, long max, String path) {
        int prevBegin = (begin - entriesPerPage - 1 < 0) ? 0 : begin - entriesPerPage - 1;
        int prevEnd = begin - 1;

        _logger.info("calculate change order display range : prev begin : " + prevBegin + " prev end : " + prevEnd + " latest revision : " + max);

        String urlPrev = (prevEnd <= 0) ? "#" :
                String.format(path + "?" + Dict.BEGIN + "=%d&" + Dict.END + "=%d", prevBegin, prevEnd);

        return urlPrev;
    }

    private String getNextUrl(int end, long max, String path) {
        int nextBegin = end + 1;
        int nextEnd = end + entriesPerPage + 1;

        _logger.info("calculate change order display range : next begin : " + nextBegin + " next end : " + nextEnd + " latest revision : " + max);

        String urlNext = (nextBegin > max) ? "#" :
                String.format(path + "?" + Dict.BEGIN + "=%d&" + Dict.END + "=%d", nextBegin, nextEnd);

        return urlNext;
    }

    @GET
    @Timed
    @Path("/" + PATH_CHANGE_ORDER_BY_ID)
    @Produces(MediaType.TEXT_HTML)
    @UnitOfWork
    public Response getChangeOrderByID(@PathParam(Dict.BRANCH_NAME) @NotEmpty String branchName
                                , @PathParam(Dict.ID) @NotNull Long id) {
        ChangeOrder changeOrder = changeOrderDAO.findByID(id);
        String time = null;

        if (changeOrder != null && changeOrder.getTime() != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            time = simpleDateFormat.format(changeOrder.getTime());
        }

        return Response.ok().entity(
                views.changeOrder.template(branchName
                        , "user1000"
                        , changeOrderDAO.findUniqueBranches()
                        , changeOrder
                        , entriesPerPage
                        , time
                )
        ).build();
    }
}
