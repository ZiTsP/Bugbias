package libraries.revisiontrace.svn;

import java.util.ArrayList;
import java.util.List;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc2.SvnLog;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnRevisionRange;
import org.tmatesoft.svn.core.wc2.SvnTarget;

import libraries.putils.FileUtils;

public final class LogTrace {

    private LogTrace() {
    }
    protected static List<SvnDiffEntry> getDiffEntries(SvnPortal info, long oldRevNum, long newRevNum) {
        SVNRevision oldRev = SVNRevision.create(oldRevNum);
        SVNRevision newRev = SVNRevision.create(newRevNum);
        return getDiffEntries(info, oldRev, newRev);
    }
    
    protected static List<SvnDiffEntry> getDiffEntries(SvnPortal info, long newRevNum) {
        return (newRevNum > 1) ? getDiffEntries(info, newRevNum - 1, newRevNum) : new ArrayList<>();
    }

    protected static List<SvnDiffEntry> getLastCommitedEntries(SvnPortal info) {
        if (Utils.getLatestRevision(info).isPresent()){
            SVNRevision headHatRev = SVNRevision.create(Utils.getLatestRevision(info).getAsLong() - 1);
            return getDiffEntries(info, headHatRev, SVNRevision.HEAD);
        }
        return new ArrayList<>();
    }
    
    protected static List<SvnDiffEntry> getCacheEntries(SvnPortal info) {
        return getDiffEntries(info, SVNRevision.HEAD, SVNRevision.WORKING);
    }

    private static List<SvnDiffEntry> getDiffEntries(SvnPortal info, SVNRevision oldRev, SVNRevision newRev) {
        SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
        List<SvnDiffEntry> files = new ArrayList<>();
        try {
            svnOperationFactory.setAuthenticationManager(info.getAuthenticate().orElse(null));
            SvnTarget root = SvnTarget.fromURL(info.getUrl().get());
            SvnLog log = svnOperationFactory.createLog();
            log.setSingleTarget(root);
            log.addRange(SvnRevisionRange.create(oldRev, newRev));
            log.setUseMergeHistory(true);
            log.setDiscoverChangedPaths(true);
            SVNLogEntry entry = log.run();
            entry.getChangedPaths().entrySet().stream()
            .map(e -> e.getValue().getPath()).filter(e -> LogTrace.hasExtension(e))
            .forEach(e -> files.add(new SvnDiffEntry(FileUtils.parsePath(info.getUrlString(), e), newRev)));
        } catch (SVNException e) {
            e.printStackTrace();
        } finally {
            svnOperationFactory.dispose();
        }
        return files;
    }

    private static boolean hasExtension(String path) {
        int index = path.lastIndexOf(".") - path.lastIndexOf("/");
        return (index <= 0) ? false : true;
    }
}
