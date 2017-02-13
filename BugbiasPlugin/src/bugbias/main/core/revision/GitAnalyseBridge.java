package bugbias.main.core.revision;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;

import bugbias.main.internal.TreeData;
import bugbias.main.widget.IOutputView;
import bugbias.main.widget.RevisionTable;
import libraries.putils.EpocDateTime;
import libraries.putils.ReduceLength;
import libraries.revisiontrace.LinesExtraction;
import libraries.revisiontrace.git.GitPortal;

public class GitAnalyseBridge implements IAnalyseRevisionBridge {

    private final GitPortal GIT;
    private RevAnalyseConfig config = RevAnalyseConfig.DEFAULT_CONFIG;
    private Optional<IOutputView> output = Optional.empty();
    @SuppressWarnings("unused")
    private Optional<RevisionTable> widget = Optional.empty();

    public GitAnalyseBridge(Path path) {
        GIT = new GitPortal(path);
        this.setConfig(RevAnalyseConfig.DEFAULT_CONFIG);
        this.head = GIT.getHeadCommit();
    }

    public GitAnalyseBridge(Path path, RevAnalyseConfig config) {
        GIT = new GitPortal(path);
        this.setConfig(config);
        this.head = GIT.getHeadCommit();
    }

    private Optional<RevCommit> head = Optional.empty();

    public boolean isUpdated() {
        Optional<RevCommit> head = GIT.getHeadCommit();
        if (head.isPresent() && !head.equals(this.head)) {
            this.head = head;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Optional<TreeData> analyseLastRevision() {
        if (head.isPresent()) {
            RevCommit commit = this.head.get();
            return analyseRevision(commit);
        }
        return Optional.empty();
    }

    @Override
    public Optional<TreeData> analyseCurrentRevision() {
        if (head.isPresent()) {
            RevCommit commit = this.head.get();
            ArrayList<String> list = new ArrayList<>();
            list.add("(GIT) Working Directory");
            list.add(EpocDateTime.nowToString());
            String strs[] = list.toArray(new String[list.size()]);
            TreeData parent = new TreeData(strs, commit);
            parent.addChild(this.analyse(GIT.getWaitingEntries()));
            return Optional.of(parent);
        }
        return Optional.empty();
    }

    @Override
    public Optional<TreeData> analyseRevision(Object index) {
        if (index instanceof RevCommit) {
            RevCommit commit = (RevCommit) index;
            ArrayList<String> list = new ArrayList<>();
            list.add(new StringBuffer("(GIT)").append(GIT.getID(commit)).toString());
            list.add(EpocDateTime.convertEpocSecToString(commit.getCommitTime()));
            String strs[] = list.toArray(new String[list.size()]);
            TreeData parent = new TreeData(strs, commit);
            List<DiffEntry> entries = GIT.getHeadEntries();
            parent.addChild(this.analyse(entries));
            return Optional.of(parent);
        }
        return Optional.empty();
    }

    private static final String ANALYSE_MESSAGE = "Analysing : ";

    private List<TreeData> analyse(List<DiffEntry> entries) {
        List<TreeData> children = new ArrayList<>();
        GIT.extractSpecificType(entries, config.getExtensions()).forEach(e -> {
            output.ifPresent(view -> view.print(ANALYSE_MESSAGE, e.getNewPath()));
            try {
                Path tmpDiff = Files.createTempFile(DIFF_FILE_PREFIX, null);
                Files.write(tmpDiff, LinesExtraction.extractAddedLine(GIT.outPutDiff(e)));
                OptionalDouble result = RevisionAnalysers.analyse(tmpDiff, config.getAnalyser());
                result.ifPresent(r -> {
                    String strs[] = {e.getNewPath(), ReduceLength.reduceDecimal(r)};
                    TreeData child = new TreeData(strs, RevisionAnalysers.checkCaution(result.getAsDouble(), config.getAnalyser()), e);
                    children.add(child);
                });
                Files.delete(tmpDiff);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            output.ifPresent(view -> view.clear());
        });
        return children;
    }

    private static final String LEARNIG_MESSAGE = "Learning : ";
    private static final String DIFF_FILE_PREFIX = "GITANALYSEBRIDGE-";

    @Override
    public void learn(List<Object> indexList, boolean isAccurate) {
        List<DiffEntry> tmp = new ArrayList<>();
        indexList.forEach(index -> {
            if (index instanceof RevCommit) {
                tmp.addAll(GIT.getDiffEntries((RevCommit) index));
            } else if (index instanceof DiffEntry) {
                tmp.add((DiffEntry) index);
            }
        });
        List<DiffEntry> diffEntries = tmp.stream().distinct().collect(Collectors.toList());
        diffEntries.forEach(e -> {
            output.ifPresent(view -> view.print(LEARNIG_MESSAGE, e.getNewPath()));
            try {
                Path tmpDiff = Files.createTempFile(DIFF_FILE_PREFIX, null);
                Files.write(tmpDiff, LinesExtraction.extractAddedLine(GIT.outPutDiff(e)));
                RevisionAnalysers.learn(tmpDiff, isAccurate, config.getAnalyser());
                Files.delete(tmpDiff);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            output.ifPresent(view -> view.clear());
        });
    }

    @Override
    public void setOutput(IOutputView output) {
        this.output = Optional.ofNullable(output);
    }

    @Override
    public Optional<Object> getLastRevision() {
        return Optional.ofNullable(GIT.getHeadCommit().orElse(null));
    }

    @Override
    public void setConfig(RevAnalyseConfig config) {
        this.config = (config != null) ? config : this.config;
    }

    @Override
    public void setWidget(RevisionTable widget) {
        this.widget = Optional.ofNullable(widget);
    }
}
