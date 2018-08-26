package com.eco.utils.svn;

import com.eco.revision.core.Revision;
import com.eco.revision.core.RevisionData;
import com.eco.revision.resources.RevisionResource;
import com.eco.utils.misc.Dict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by neo on 8/25/18.
 */
public class SVNUtils {
    public static final Logger _logger = LoggerFactory.getLogger(SVNUtils.class);

    public static void getLog(String repo, String branchName, String user, String password, long startRevision, long endRevision, boolean doPrint) {
        DAVRepositoryFactory.setup();

        SVNRepository repository = null;
        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(repo));
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(user, password);
            repository.setAuthenticationManager(authManager);

            Collection logEntries = null;

            logEntries = repository.log(new String[]{""}, null, startRevision, endRevision, true, true);
            for (Iterator entries = logEntries.iterator(); entries.hasNext(); ) {
                SVNLogEntry logEntry = (SVNLogEntry) entries.next();

                if (doPrint) {
                    System.out.println("---------------------------------------------");
                    System.out.println("revision: " + logEntry.getRevision());
                    System.out.println("author: " + logEntry.getAuthor());
                    System.out.println("date: " + logEntry.getDate());
                    System.out.println("log message: " + logEntry.getMessage());

                    if (logEntry.getChangedPaths().size() > 0) {
                        System.out.println();
                        System.out.println("changed paths:");
                        Set changedPathsSet = logEntry.getChangedPaths().keySet();

                        for (Iterator changedPaths = changedPathsSet.iterator(); changedPaths.hasNext(); ) {
                            SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
                            System.out.println(" "
                                    + entryPath.getType()
                                    + " "
                                    + entryPath.getPath()
                                    + ((entryPath.getCopyPath() != null) ? " (from "
                                    + entryPath.getCopyPath() + " revision "
                                    + entryPath.getCopyRevision() + ")" : ""));
                        }
                    }
                }

                saveLog(branchName, logEntry);
            }
        } catch (SVNException e) {
            e.printStackTrace();
        }
    }

    private static void saveLog(String branchName, SVNLogEntry svnLogEntry) {
        _logger.info("save log");
        Revision revision = new Revision(Revision.generateID(branchName, String.valueOf(svnLogEntry.getRevision()))
                , branchName
                , String.valueOf(svnLogEntry.getRevision())
                , svnLogEntry.getDate()
                , svnLogEntry.getAuthor()
                , Revision.STATUS.NEW.getValue()
                , ""
                , ""
                , new Date(0)
                , new RevisionData("")
        );

        Response response = ClientBuilder.newClient()
                .target(String.format("http://localhost:8080%s%s%s/"
                        , Dict.API_V1_PATH
                        , RevisionResource.PATH
                        , RevisionResource.PATH_INSERT_OBJ)
                )
                .request()
                .post(Entity.entity(revision, MediaType.APPLICATION_JSON));
    }

    public static void main(String[] arg) {
        SVNUtils.getLog("https://svn.riouxsvn.com/svncontrol/trunk/","svncontrol",
                "", "", 0, -1, true);
    }
}
