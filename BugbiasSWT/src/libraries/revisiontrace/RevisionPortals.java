package libraries.revisiontrace;

import java.nio.file.Path;
import java.util.Optional;

import libraries.revisiontrace.git.GitPortal;
import libraries.revisiontrace.svn.SvnPortal;

public class RevisionPortals {
    
    private RevisionPortals() {
    }
    
    public static Optional<REPOSITORY_TYPE> getType(Path path) {
        if (GitPortal.isGitRootDir(path)) {
            return Optional.of(REPOSITORY_TYPE.GIT);
        } else if (SvnPortal.isSvnRootDir(path)) {
            return Optional.of(REPOSITORY_TYPE.SVN);
        }
        return Optional.empty();
    }

}
