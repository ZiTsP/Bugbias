package libraries.revisiontrace.svn;

import org.tmatesoft.svn.core.wc.SVNRevision;

public class SvnDiffEntry {
    
    private final String filePath;
    private final long revisionNum;
    private final SVNRevision revision;
    
    public SvnDiffEntry(String filePath, SVNRevision revision) {
        this.filePath = filePath;
        this.revision = revision;
        this.revisionNum = revision.getNumber();
    }
    
    public SvnDiffEntry(String filePath, long revisionNum) {
        this.filePath = filePath;
        this.revisionNum = revisionNum;
        this.revision = SVNRevision.create(revisionNum);
    }
    
    public String getFilePath() {
        return this.filePath;
    }
    
    public long getRevisionAsLong() {
        return this.revisionNum;
    }
    
    public SVNRevision getRevision() {
        return this.revision;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof SvnDiffEntry) {
            if (this.filePath.equals(((SvnDiffEntry) o).getFilePath()) && (this.revisionNum == ((SvnDiffEntry) o).getRevisionAsLong())){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.filePath.hashCode() + (int) this.revisionNum;
    }
}
