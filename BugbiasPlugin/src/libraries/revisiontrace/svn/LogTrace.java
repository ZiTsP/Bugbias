package libraries.revisiontrace.svn;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc2.SvnLog;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnRevisionRange;
import org.tmatesoft.svn.core.wc2.SvnTarget;

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
        OptionalLong latestRevisionNum = Utils.getLatestRevision(info);
        if (latestRevisionNum.isPresent() && 1 < latestRevisionNum.getAsLong()){
            SVNRevision headHatRev = SVNRevision.create(latestRevisionNum.getAsLong() - 1);
            return getDiffEntries(info, headHatRev, SVNRevision.HEAD);
        }
        return new ArrayList<>();
    }
    
    protected static List<SvnDiffEntry> getCacheEntries(SvnPortal info) {
        return getDiffEntries(info, SVNRevision.HEAD, SVNRevision.WORKING);
    }

    private static List<SvnDiffEntry> getDiffEntries(SvnPortal info, SVNRevision oldRev, SVNRevision newRev) {
        SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
        List<SvnDiffEntry> entries = new ArrayList<>();
        try {
            svnOperationFactory.setAuthenticationManager(info.getAuthenticate().orElse(null));
            SvnLog log = svnOperationFactory.createLog();
//            SVNURL url;
//            if (info.getRemoteUrl().isPresent()) {
//                url = info.getRemoteUrl().get();
//            } else {
//                return new ArrayList<>();
//            }
//            log.setSingleTarget(SvnTarget.fromURL(url));
            log.setSingleTarget(SvnTarget.fromFile(info.getLocalPath().get().toFile()));
            log.addRange(SvnRevisionRange.create(oldRev, newRev));
            log.setUseMergeHistory(true);
            log.setDiscoverChangedPaths(true);
            SVNLogEntry entry = log.run();
            entry.getChangedPaths().entrySet().stream()
                .map(e -> e.getValue().getPath()).filter(e -> LogTrace.hasExtension(e))
                .forEach(e -> {
                    String remoteRoot = info.getRemoteUrlToString();
                    String localFilepath = SvnPaths.attach(info.getLocalPathToString(), SvnPaths.detach(remoteRoot, e));
                    SVNURL remoteFileUrl;
                    try {
                        remoteFileUrl= SVNURL.parseURIEncoded(SvnPaths.attach(remoteRoot, e));
                    } catch (Exception e1) {
                        remoteFileUrl = null;
                    }
                    entries.add(new SvnDiffEntry(Paths.get(localFilepath), remoteFileUrl, oldRev, newRev));
                });
        } catch (SVNException e) {
            e.printStackTrace();
        } finally {
            svnOperationFactory.dispose();
        }
        return entries;
    }

    private static boolean hasExtension(String path) {
        int index = path.lastIndexOf(".") - path.lastIndexOf("/");
        return (index <= 0) ? false : true;
    }
}
