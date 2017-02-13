package libraries.revisiontrace.svn;

import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;



public class SvnPortal {

    private final Optional<String> url;
    private Optional<String> auther;
    private final Optional<SVNURL> svnUrl;
    private Optional<ISVNAuthenticationManager> authenticate;

    public static boolean isSvnRootDir(Path path) {
        return Utils.isSvnRootDir(path);
    }

    public SvnPortal(String url) {
        SVNURL svnUrl;
        try {
            svnUrl = SVNURL.parseURIEncoded(url);
        } catch (SVNException e) {
            svnUrl = Utils.getRemoteUrl(Paths.get(url)).orElse(null);
        }
        this.url = Optional.ofNullable(url);
        this.svnUrl = Optional.ofNullable(svnUrl);
    }
    
    public SvnPortal(String url, String name, String pwd) {
        this(url);
        this.setAuthenticate(name, pwd);
    }
    
    public void setAuthenticate(String name, String pwd) {
        this.auther = Optional.ofNullable(name);
        this.authenticate = Optional.ofNullable(SVNWCUtil.createDefaultAuthenticationManager(name, pwd.toCharArray()));
    }

    public Optional<SVNURL> getUrl() {
        return this.svnUrl;
    }

    public String getUrlString() {
        return (svnUrl.isPresent()) ? svnUrl.get().toString() : url.orElse("");
    }
//    
//    
//    public SVNURL getFileUrl(String filePath) throws SVNException {
//        StringBuffer str = new StringBuffer(this.url.orElse("")).append(filePath);
//        return SVNURL.parseURIEncoded(str.toString());
//    }
    
    
    public Optional<ISVNAuthenticationManager> getAuthenticate() {
        return this.authenticate;
    }
    
    public String getAuthName() {
        return this.auther.orElse("");
    }

    public List<SvnDiffEntry> getLastCommitedEntries() {
        return LogTrace.getLastCommitedEntries(this);
    }

    public List<SvnDiffEntry> getCacheEntries() {
        return LogTrace.getCacheEntries(this);
    }

    public List<SvnDiffEntry> getDiffEntries(long revision) {
        return LogTrace.getDiffEntries(this, revision);
    }

    public boolean outPutDiff(String filePath, long revNum, OutputStream outStream) {
        return (revNum > 1) ? DiffTrace.outPutDiff(this, filePath, revNum -1 , revNum, outStream) : false;
    }

    public List<String> outPutDiff(SvnDiffEntry entry) {
        return (svnUrl.isPresent() && entry != null) ?  DiffTrace.getDiffText(this, entry) : new ArrayList<>();
    }
    public boolean outPutWorkingDiff(String filePath, OutputStream outStream) {
        return DiffTrace.outPutWorkingDiff(this, filePath, outStream);
    }

    
    public List<SvnDiffEntry> extractSpecificType(List<SvnDiffEntry> entries, List<String> extensions) {
        return Utils.extractSpecificType(entries, extensions);
    }
    
    public OptionalLong getLatestRevision() {
        return Utils.getLatestRevision(this);
    }
}
