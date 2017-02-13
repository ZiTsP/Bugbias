package libraries.revisiontrace.svn;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;



public class SvnPortal {

    private final Optional<Path> localPath;
    private final Optional<SVNURL> remoteUrl;
//    private final Optional<SVNURL> localUrl;
    private Optional<String> auther = Optional.empty();
    private Optional<ISVNAuthenticationManager> authenticate = Optional.empty();


    public static boolean isSvnRootDir(Path path) {
        return Utils.isSvnRootDir(path);
    }

    public SvnPortal(Path localPath) {
        this.localPath = (localPath != null && Files.exists(localPath)) ? Optional.of(localPath) : Optional.empty();
        this.remoteUrl = Utils.getRemoteUrl(this.localPath);
//        SVNURL svnUrl = null;
//        try {
//            svnUrl = (this.localPath.isPresent()) ? SVNURL.parseURIEncoded(this.localPath.get().toString()) : null;
//        } catch (SVNException e) {
//            svnUrl = null;
//        }
//        this.localUrl = Optional.ofNullable(svnUrl);
//        System.out.println(this.localPath);
//        System.out.println(this.remoteUrl);
//        System.out.println(this.localUrl);
    }

    public SvnPortal(Path localPath, String name, String pwd) {
        this(localPath);
        this.setAuthenticate(name, pwd);
    }

    public void setAuthenticate(String name, String pwd) {
        this.auther = Optional.ofNullable(name);
        this.authenticate = Optional.ofNullable(SVNWCUtil.createDefaultAuthenticationManager(name, pwd.toCharArray()));
    }

    public Optional<Path> getLocalPath() {
        return this.localPath;
    }

    public String getLocalPathToString() {
        return (this.localPath.isPresent()) ? this.localPath.get().toString() : "";
    }

    public Optional<SVNURL> getRemoteUrl() {
        return this.remoteUrl;
    }

    public String getRemoteUrlToString() {
        return (this.remoteUrl.isPresent()) ? this.remoteUrl.get().toString() : "";
    }

//    public Optional<SVNURL> getLocalUrl() {
//        return this.localUrl;
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

//    public boolean outPutDiff(String filePath, long revNum, OutputStream outStream) {
//        return (revNum > 1) ? DiffTrace.outPutDiff(this, filePath, revNum -1 , revNum, outStream) : false;
//    }

    public List<String> outPutDiff(SvnDiffEntry entry) {
        return (entry != null) ?  DiffTrace.getDiffText(this, entry) : new ArrayList<>();
    }

//    public boolean outPutWorkingDiff(String filePath, OutputStream outStream) {
//        return DiffTrace.outPutWorkingDiff(this, filePath, outStream);
//    }

    public List<SvnDiffEntry> extractSpecificType(List<SvnDiffEntry> entries, List<String> extensions) {
        return Utils.extractSpecificType(entries, extensions);
    }

    public OptionalLong getLatestRevision() {
        return Utils.getLatestRevision(this);
    }
}
