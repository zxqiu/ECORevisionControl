package com.eco.svn;

import com.eco.revision.core.Revision;
import com.eco.revision.core.RevisionData;
import com.eco.revision.dao.RevisionConnector;
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

import java.io.IOException;
import java.util.*;

import static com.eco.revision.resources.RevisionResource.revisionConnector;

/**
 * Created by neo on 8/25/18.
 */
public class SVNUtils {
    public static final Logger _logger = LoggerFactory.getLogger(SVNUtils.class);

    public static Collection updateLog(RevisionConnector revisionConnector, String repo, String branchName,
                                       String user,String password, long startRevision, long endRevision,
                                       boolean doPrint) throws SVNException, IOException {
        DAVRepositoryFactory.setup();

        Collection logEntries = null;
        SVNRepository repository = null;
        repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(repo));
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(user, password);
        repository.setAuthenticationManager(authManager);

        _logger.info("Updating SVN log : " + repo);

        logEntries = repository.log(new String[]{""}, null, startRevision, endRevision, true,true);

        for (Iterator entries = logEntries.iterator(); entries.hasNext(); ) {
            SVNLogEntry logEntry = (SVNLogEntry) entries.next();

            if (doPrint) {
                _logger.info("---------------------------------------------");
                _logger.info("revision: " + logEntry.getRevision());
                _logger.info("author: " + logEntry.getAuthor());
                _logger.info("date: " + logEntry.getDate());
                _logger.info("log message: " + logEntry.getMessage());

                if (logEntry.getChangedPaths().size() > 0) {
                    _logger.info("changed paths:");
                    Set changedPathsSet = logEntry.getChangedPaths().keySet();

                    for (Iterator changedPaths = changedPathsSet.iterator(); changedPaths.hasNext(); ) {
                        SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
                        _logger.info(" "
                                + entryPath.getType()
                                + " "
                                + entryPath.getPath()
                                + ((entryPath.getCopyPath() != null) ? " (from "
                                + entryPath.getCopyPath() + " revision "
                                + entryPath.getCopyRevision() + ")" : ""));
                    }
                }
            }

            saveLog(revisionConnector, branchName, logEntry);
        }

        return logEntries;
    }

    private static void saveLog(RevisionConnector revisionConnector, String branchName, SVNLogEntry svnLogEntry) throws IOException {
        if (revisionConnector == null || svnLogEntry == null) {
            return;
        }

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

        revisionConnector.insert(revision);
    }

    public static void main(String[] arg) throws IOException, SVNException {
        String branchName = "svncontrol";
        SVNConf svnConf = SVNConf.getSVNConf();

        for (SVNBranch svnBranch : svnConf.getBranches()) {
            if (branchName != null && branchName.length() > 0 && branchName.equals(branchName) == false) {
                continue;
            }

            SVNUtils.updateLog(null, svnBranch.getRepo(),
                    svnBranch.getBranchName(), svnConf.getUser(), svnConf.getPassword(),
                    0, -1, true);
        }
    }
}
