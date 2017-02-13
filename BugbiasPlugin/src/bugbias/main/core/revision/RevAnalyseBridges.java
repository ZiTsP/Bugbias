package bugbias.main.core.revision;

import java.nio.file.Path;
import java.util.Optional;

import libraries.revisiontrace.RevisionPortals;

public class RevAnalyseBridges {

    private RevAnalyseBridges() {
    }
    
    public static Optional<IAnalyseRevisionBridge> getRevisionAnalysePortal(Path path) {
        switch (RevisionPortals.getType(path).orElse(null)) {
            case GIT:
                return Optional.ofNullable((IAnalyseRevisionBridge) new GitAnalyseBridge(path));
            case SVN:
                return Optional.ofNullable((IAnalyseRevisionBridge) new SvnAnalyseBridge(path));
            default :
                return Optional.empty();
        }
    }
}
