package com.eco.views.resources;

import com.codahale.metrics.annotation.Timed;
import com.eco.revision.dao.RevisionConnector;
import com.eco.revision.dao.RevisionDAO;
import com.eco.revision.resources.RevisionResource;
import com.eco.svn.SVNBranch;
import com.eco.svn.SVNConf;
import com.eco.utils.misc.Dict;
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
    public static final String PATH_ROOT = "/";
    public static final String PATH_BRANCHES = "";
    public static final String PATH_REVISIONS = "/{" + Dict.BRANCH_NAME + "}";

    public static final Logger _logger = LoggerFactory.getLogger(GUI.class);

    public static RevisionConnector revisionConnector = null;

    public GUI(RevisionDAO revisionDAO) throws Exception {
        RevisionConnector.init(revisionDAO);
        GUI.revisionConnector = RevisionConnector.getInstance();
    }

    @GET
    @Timed
    @Path(PATH_BRANCHES)
    @Produces(MediaType.TEXT_HTML)
    public Response branches() throws IOException {
        SVNConf svnConf = SVNConf.getSVNConf();
        List<Map<String, String>> branches = new ArrayList<>();

        for (SVNBranch svnBranch : svnConf.getBranches()) {
            Map<String, String> map = new HashMap<>();
            map.put(Dict.BRANCH_NAME, svnBranch.getBranchName());
            map.put(Dict.URL, "/" + svnBranch.getBranchName() + "?" + Dict.BEGIN + "=0&" + Dict.END + "=100");
            branches.add(map);
        }
        return Response.ok().entity(views.branches.template(branches)).build();
    }

    @GET
    @Timed
    @Path(PATH_REVISIONS)
    @Produces(MediaType.TEXT_HTML)
    public Response revisions(@PathParam(Dict.BRANCH_NAME) @NotNull String branchName
                              , @QueryParam(Dict.BEGIN) @NotNull Long begin
                              , @QueryParam(Dict.END) @NotNull Long end) throws IOException {
        long prevBegin = (begin - 100 < 0) ? 0 : begin - 100;
        long prevEnd = begin - 1;
        long nextBegin = end + 1;
        long nextEnd = end + 100;

        _logger.info(prevBegin + " " + prevEnd + " " + nextBegin + " " + nextEnd + " latest " + revisionConnector.findLargestRevisionID(branchName));

        String urlPrev = (prevEnd < 0) ? "#" :
                String.format(PATH_ROOT + PATH_REVISIONS + branchName + "?" + Dict.BEGIN + "=%d&" + Dict.END + "=%d", prevBegin, prevEnd);
        String urlNext = (nextBegin > revisionConnector.findLargestRevisionID(branchName)) ? "#" :
                String.format(PATH_ROOT + PATH_REVISIONS + branchName + "?" + Dict.BEGIN + "=%d&" + Dict.END + "=%d", nextBegin, nextEnd);
        String urlBranches = PATH_ROOT + PATH_BRANCHES;

        return Response.ok().entity(
                views.revisions.template(RevisionResource.utilGetLimitByBranch(branchName, begin, end)
                                        , urlPrev
                                        , urlNext
                                        , urlBranches)
        ).build();
    }
}
