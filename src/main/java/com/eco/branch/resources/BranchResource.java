package com.eco.branch.resources;

import com.codahale.metrics.annotation.Timed;
import com.eco.revision.resources.RevisionResource;
import com.eco.svn.SVNBranch;
import com.eco.svn.SVNConf;
import com.eco.utils.misc.Dict;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neo on 8/26/18.
 */

@Path(Dict.API_V1_PATH + BranchResource.PATH_ROOT)
public class BranchResource {
    public static final String PATH_ROOT = "/branches";
    public static final String PATH_GET_ALL = "/";

    @GET
    @Timed
    @Path(PATH_GET_ALL)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String, String>> getAll() throws IOException {
        SVNConf svnConf = SVNConf.getSVNConf();
        List<Map<String, String>> branches = new ArrayList<>();

        for (SVNBranch svnBranch : svnConf.getBranches()) {
            Map<String, String> map = new HashMap<>();
            map.put(Dict.BRANCH_NAME, svnBranch.getBranchName());
            map.put(Dict.URL, String.format(Dict.API_V1_PATH +
                            RevisionResource.PATH_ROOT +
                            "/%s",
                    svnBranch.getBranchName()));
            branches.add(map);
        }

        return branches;
    }
}
