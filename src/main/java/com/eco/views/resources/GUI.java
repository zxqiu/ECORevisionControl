package com.eco.views.resources;

import com.codahale.metrics.annotation.Timed;
import com.eco.branch.resources.BranchResource;
import com.eco.revision.resources.RevisionResource;
import com.eco.svn.SVNBranch;
import com.eco.svn.SVNConf;
import com.eco.utils.misc.Dict;
import org.tmatesoft.svn.core.SVNException;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
    public static final String PATH_BRANCHES = "/";
    public static final String PATH_REVISIONS = "/{" + Dict.BRANCH_NAME + "}";

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
            map.put(Dict.URL, "/" + svnBranch.getBranchName());
            branches.add(map);
        }
        return Response.ok().entity(views.branches.template(branches)).build();
    }

    @GET
    @Timed
    @Path(PATH_REVISIONS)
    @Produces(MediaType.TEXT_HTML)
    public Response revisions(@PathParam(Dict.BRANCH_NAME) @NotNull String branchName) throws IOException, SVNException {
        return Response.ok().entity(views.revisions.template(RevisionResource.utilGetByBranch(branchName))).build();
    }
}
