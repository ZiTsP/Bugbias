package libraries.revisiontrace.git;

import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;

public class GitPortal {

    private final Optional<Path> root;
    private final Optional<Path> gitDir;
    
    public static boolean isGitRootDir(Path path) {
        return Utils.isGitRootDir(path);
    }
    
    public GitPortal(Path path) {
        gitDir = Utils.getGitDir(path);
        root = Optional.ofNullable(gitDir.orElse(null).toAbsolutePath().getParent());
    }
    
    public Optional<Path> getRepositoryRoot() {
        return this.root;
    }
    
    public Optional<Path> getGitDir() {
        return this.gitDir;
    }
    
    public Optional<RevCommit> getHeadCommit() {
        return (gitDir.isPresent()) ? Utils.getHeadCommit(gitDir.get()) : Optional.empty();
    }
    
    public List<DiffEntry> getHeadEntries() {
        return (gitDir.isPresent()) ? LogTrace.getHeadEntries(gitDir.get()) : new ArrayList<>();
    }

    public List<DiffEntry> getWaitingEntries() {
        return (gitDir.isPresent()) ? LogTrace.getWaitingEntries(gitDir.get()) : new ArrayList<>();
    }
    
    public List<DiffEntry> getDiffEntries(RevCommit commitId) {
        return (gitDir.isPresent()) ? LogTrace.getDiffEntries(gitDir.get(), commitId) : new ArrayList<>();
    }

    public boolean outPutDiff(DiffEntry entry, OutputStream outStream) {
        return (gitDir.isPresent() && entry != null) ? DiffTrace.outputDiff(entry, gitDir.get(), outStream) : false;
    }
    
    public List<String> outPutDiff(DiffEntry entry) {
        return (gitDir.isPresent() && entry != null) ?  DiffTrace.getDiffText(entry, gitDir.get()) : new ArrayList<>();
    }
    
    public String getID(RevCommit commit) {
        return Utils.limitIdLength(commit.getId());
    }
    
    public List<DiffEntry> extractSpecificType(List<DiffEntry> entries, List<String> extensions) {
        return Utils.extractSpecificType(entries, extensions);
    }
}
