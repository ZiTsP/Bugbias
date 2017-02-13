package libraries.revisiontrace.git;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheIterator;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.WorkingTreeIterator;

/**
 * This class is only used by GitPortal.
 * @author zitsp
 */

public final class LogTrace {
    
    private LogTrace() {
    }
    
    protected static final ArrayList<RevCommit> getAllComits(Path gitDir) {
        ArrayList<RevCommit> commits = new ArrayList<>();
        try (Repository repository = new FileRepositoryBuilder().setGitDir(gitDir.toFile()).build();
                Git git = new Git(repository)) {
            Iterable<RevCommit> allCommits = null;
            try {
                allCommits = git.log().all().call();
            } catch (NullPointerException e) {
                allCommits = git.log().call();
            }
            allCommits.forEach(e -> commits.add(e));
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return commits;
    }
    
    protected static final List<Ref> getAllTags(Path gitDir) {
        List<Ref> tags = new ArrayList<>();
        try (Repository repository = new FileRepositoryBuilder().setGitDir(gitDir.toFile()).build();
                Git git = new Git(repository)) {
                tags.addAll(git.tagList().call());
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tags;
    }
    
    protected static final List<DiffEntry> getHeadEntries(Path gitDir) {
        Optional<RevCommit> head = Utils.getHeadCommit(gitDir);
        return (head.isPresent()) ? getDiffEntries(gitDir, head.get()) : new ArrayList<>();
    }
    
    protected static final List<DiffEntry> getWaitingEntries(Path gitDir) {
        List<DiffEntry> list = new ArrayList<>();
        list.addAll(getStagedDiffEntries(gitDir));
        list.addAll(getUnstagedDiffEntries(gitDir));
        List<DiffEntry> entries = new ArrayList<>();
        list.stream().distinct().forEach(e -> entries.add(e));
        return entries;
    }
    
    protected static final List<DiffEntry> getStagedDiffEntries(Path gitDir) {
        List<DiffEntry> entries = new ArrayList<>();
        try (Repository repository = new FileRepositoryBuilder().setGitDir(gitDir.toFile()).build();
                TreeWalk treeWalk = new TreeWalk(repository);
                RevWalk revWalk = new RevWalk(repository);
                DiffFormatter diffFormatter = new DiffFormatter(null)) {
            DirCache cache = repository.readDirCache();
            treeWalk.setRecursive(true);
            treeWalk.addTree(revWalk.parseTree(repository.resolve(Constants.HEAD)));
            AbstractTreeIterator treeIterator = treeWalk.getTree(0, AbstractTreeIterator.class);
            DirCacheIterator dirCacheIterator = new DirCacheIterator(cache);
            diffFormatter.setRepository(repository);
            entries.addAll(diffFormatter.scan(treeIterator, dirCacheIterator));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }
    
    protected static final List<DiffEntry> getUnstagedDiffEntries(Path gitDir) {
        List<DiffEntry> entries = new ArrayList<>();
        try (Repository repository = new FileRepositoryBuilder().setGitDir(gitDir.toFile()).build();
                DiffFormatter diffFormatter = new DiffFormatter(null)) {
            DirCache cache = repository.readDirCache();
            DirCacheIterator dirCacheIterator = new DirCacheIterator(cache);
            WorkingTreeIterator workingTreeIterator = (WorkingTreeIterator) new FileTreeIterator(repository);
            diffFormatter.setRepository(repository);
            entries.addAll(diffFormatter.scan(dirCacheIterator, workingTreeIterator));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }

    protected static final List<DiffEntry> getDiffEntries(Path gitDir, RevCommit commit) {
        List<DiffEntry> diffEntries = new ArrayList<>();
        try (Repository repository = new FileRepositoryBuilder().setGitDir(gitDir.toFile()).build()) {
            List<RevTree> fromTrees = new ArrayList<>();
            Arrays.stream(commit.getParents()).forEach(parent -> {
                try (RevWalk revWalk = new RevWalk(repository)) {
                    ObjectId id = repository.resolve(parent.name());
                    RevCommit oldCommit = revWalk.parseCommit(id);
                    RevTree fromTree = oldCommit.getTree();
                    fromTrees.add(fromTree);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            RevTree toTree = commit.getTree();
            fromTrees.forEach(fromTree -> {
                try (ObjectReader reader = repository.newObjectReader()) {
                    CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                    oldTreeIter.reset(reader, fromTree);
                    CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                    newTreeIter.reset(reader, toTree);
                    try (Git git = new Git(repository)) {
                        List<DiffEntry> diffs = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
                        diffEntries.addAll(diffs);
                    } catch (GitAPIException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return diffEntries;
    }
}
