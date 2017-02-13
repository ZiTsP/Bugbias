package libraries.revisiontrace.git;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public final class Utils {
    
    private Utils() {
    }
    
    protected static final Path GIT_DIR_PATH = Paths.get(".git");
    
    protected static final boolean isGitRootDir(Path path) {
        if (path != null && Files.exists(path) && Files.isDirectory(path)) {
            try {
                return Files.list(path).anyMatch(e -> (Files.isDirectory(path) && e.getFileName().equals(GIT_DIR_PATH)));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    protected static final Optional<Path> getGitDir(Path path) {
        if (path != null && Files.exists(path) && Files.isDirectory(path)) {
            if (path.getFileName().equals(GIT_DIR_PATH)) {
                return Optional.of(path);
            } else {
                try {
                    return Files.list(path)
                            .filter(e -> (Files.isDirectory(e) && e.getFileName().equals(GIT_DIR_PATH)))
                            .findFirst();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return Optional.empty();
    }
    
    protected static final Optional<Path> getRootGitDir(Path path) {
        if (path != null && Files.exists(path) && Files.isDirectory(path)) {
            if (path.getFileName().equals(GIT_DIR_PATH)) {
                return Optional.of(path.getParent());
            } else {
                return (isGitRootDir(path) ? Optional.of(path) : Optional.empty());
            }
        }
        return Optional.empty();
    }

    protected static Optional<RevCommit> getHeadCommit(Path gitDir) {
        return getRevCommit(gitDir, Constants.HEAD);
    }

    protected static Optional<RevCommit> getHeadCaretICommit(Path gitDir) {
        return getRevCommit(gitDir, "HEAD^");
    }
    
    protected static Optional<RevCommit> getRevCommit(Path gitDir, String resolveWord) {
        RevCommit commit = null;
        try (Repository repository = new FileRepositoryBuilder().setGitDir(gitDir.toFile()).build();
            RevWalk revWalk = new RevWalk(repository)) {
            ObjectId id = repository.resolve(resolveWord);
            commit = revWalk.parseCommit(id);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return Optional.ofNullable(commit);
    }

    private static final String ID_COMMIT = "commit ";
    private static final String ID_ANYOBJECT = "AnyObjectId[";
    private static final int DEFAULT_ID_LENGTH = 9;
    
    protected static String limitIdLength(AnyObjectId objectId, int idLength) {
        String id = objectId.toString();
        if (id == null) {
            return "";
        } else if (id.startsWith(ID_ANYOBJECT)) {
            return id.substring(ID_ANYOBJECT.length(), ID_ANYOBJECT.length() + idLength);
        } else if (id.startsWith(ID_COMMIT)) {
            return id.substring(ID_COMMIT.length(), ID_COMMIT.length() + idLength);
        } else {
            return "";
        }
    }
    
    protected static String limitIdLength(AnyObjectId objectId) {
        return limitIdLength(objectId, DEFAULT_ID_LENGTH);
    }
    
    protected static Optional<String> getTagNameFromRef(Ref tag) {
        Optional<String> name = Optional.ofNullable(tag.getName());
        if (name.isPresent()) {
            int index = name.get().lastIndexOf("/");
            return (index > 0) ? Optional.of(name.get().substring(index + 1)) : Optional.empty();
        }
        return Optional.empty();
    }

    public static List<DiffEntry> extractSpecificType(List<DiffEntry> diffEntries, List<String> specificExtensions) {
        List<DiffEntry> newList = new ArrayList<>();
        diffEntries.stream().forEach(entry -> {
            specificExtensions.forEach(extension -> {
                if (entry.getNewPath().endsWith(extension)) {
                    newList.add(entry);
                }
            });
        });
        return newList;
    }
}
