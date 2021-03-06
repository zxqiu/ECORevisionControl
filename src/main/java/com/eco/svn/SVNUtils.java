package com.eco.svn;

import com.eco.revision.core.*;
import com.eco.revision.dao.RevisionConnector;
import com.eco.revision.dao.RevisionDAI;
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

/**
 * Created by neo on 8/25/18.
 */
public class SVNUtils {
    public static final Logger _logger = LoggerFactory.getLogger(SVNUtils.class);

    /**
     * @param revisionDAI
     * @param repo
     * @param branchName
     * @param user
     * @param password
     * @param startRevision exclusive
     * @param endRevision   inclusive
     * @param doPrint
     * @return
     * @throws SVNException
     * @throws IOException
     */
    public static void updateLog(RevisionDAI revisionDAI, String repo, String branchName,
                                 String user, String password, long startRevision, long endRevision,
                                 boolean doPrint) throws SVNException, IOException {
        DAVRepositoryFactory.setup();

        Collection logEntries = null;
        SVNRepository repository = null;
        repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(repo));
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(user, password);
        repository.setAuthenticationManager(authManager);

        _logger.info("Updating SVN log : " + repo);
        _logger.info("Updating SVN log from " + startRevision + " to " + endRevision);

        logEntries = repository.log(new String[]{""}, null, startRevision, endRevision, true, false);

        _logger.info(logEntries.size() + " SVN log fetched.");

        List<Revision> revisions = new ArrayList<>();

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

            if (startRevision != logEntry.getRevision()) {
                revisions.add(new Revision(Revision.generateID(branchName, String.valueOf(logEntry.getRevision()))
                        , branchName
                        , String.valueOf(logEntry.getRevision())
                        , logEntry.getDate()
                        , logEntry.getAuthor()
                        , logEntry.getMessage()
                        , ""
                        , new Date(0)
                        , new RevisionData()));
            }
        }

        if (revisionDAI != null) {
            revisionDAI.insertBatch(revisions);
            _logger.info("SVN log saved.");
        }
    }

    public static void main(String[] arg) throws IOException, SVNException {
        String branchName = "fos_60bmcs";
        BranchConf conf = SVNConf.getConf();

        for (Branch branch : conf.getBranches()) {
            if (branchName != null && branchName.length() > 0 && branchName.equals(branch.getBranchName()) == false) {
                continue;
            }

            SVNUtils.updateLog(null, branch.getRepo(),
                    branch.getBranchName(), branch.getUser(), branch.getPassword(),
                    0, -1, true);
        }
    }
}
