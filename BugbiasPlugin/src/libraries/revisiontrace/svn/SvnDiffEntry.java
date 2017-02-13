package libraries.revisiontrace.svn;

import java.nio.file.Path;
import java.util.Optional;

import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc2.SvnTarget;

public class SvnDiffEntry {

    private final Optional<Path> localPath;
    private final Optional<SVNURL> remoteUrl;
    
    private final SVNRevision oldRevision;
    private final SVNRevision newRevision;
    
    public SvnDiffEntry(Path localPath, SVNURL remoteUrl, SVNRevision oldRevision, SVNRevision newRevision) {
        this.localPath = Optional.ofNullable(localPath);
        this.remoteUrl = Optional.ofNullable(remoteUrl);
        this.oldRevision = oldRevision;
        this.newRevision = newRevision;
    }

    public Optional<SvnTarget> getNewTarget() {
        if (localPath.isPresent()) {
            return Optional.of(SvnTarget.fromFile(localPath.get().toFile(), newRevision));
        } else if (remoteUrl.isPresent()) {
            return Optional.of(SvnTarget.fromURL(remoteUrl.get(), newRevision));
        } else {
            return Optional.empty();
        }
    }
    
    public Optional<SvnTarget> getOldTarget() {
//        if (localPath.isPresent()) {
//            return Optional.of(SvnTarget.fromFile(localPath.get().toFile(), oldRevision));
//        } else 
            if (remoteUrl.isPresent()) {
            return Optional.of(SvnTarget.fromURL(remoteUrl.get(), oldRevision));
        } else {
            return Optional.empty();
        }
    }
    
    public String getPath() {
        return (this.localPath.isPresent()) ? this.localPath.get().toString() : "";
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof SvnDiffEntry) {
            if (this.localPath.equals(((SvnDiffEntry) o).localPath) 
                    && this.remoteUrl.equals(((SvnDiffEntry) o).remoteUrl)
                    && this.oldRevision.equals(((SvnDiffEntry) o).oldRevision)
                    && this.newRevision.equals(((SvnDiffEntry) o).newRevision)){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return (localPath.hashCode() << oldRevision.hashCode()) + (remoteUrl.hashCode() >>> newRevision.hashCode())  ;
    }
}
