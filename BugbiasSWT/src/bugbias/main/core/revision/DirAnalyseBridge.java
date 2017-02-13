package bugbias.main.core.revision;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

import bugbias.main.internal.Extensions;
import bugbias.main.internal.TreeData;
import bugbias.main.widget.IOutputView;
import libraries.putils.EpocDateTime;
import libraries.putils.FileUtils;
import libraries.putils.ReduceLength;

public class DirAnalyseBridge {
    public static Optional<DirAnalyseBridge> getNewDirAnalyseBridge(Path path) {
        return (path != null && Files.exists(path)) ? Optional.of(new DirAnalyseBridge(path)) : Optional.empty();
    }

    private DirAnalyseBridge(Path path) {
        root = path.toAbsolutePath();
    }
    private DirAnalyseBridge(Path path, RevAnalyseConfig config) {
        root = path.toAbsolutePath();
        this.config = config;
    }
    
    private final Path root;
    private RevAnalyseConfig config = RevAnalyseConfig.DEFAULT_CONFIG;
    private Optional<IOutputView> output = Optional.empty();

    public void setOutput(IOutputView output) {
        this.output = Optional.ofNullable(output);
    }

    public void setConfig(RevAnalyseConfig config) {
        this.config = (config != null) ? config : this.config;
    }

//    private Optional<RevisionTable> widget = Optional.empty();
//    public void setWidget(RevisionTable widget) {
//        this.widget = Optional.ofNullable(widget);
//    }

    private static final String ANALYSE_MESSAGE = "Analysing : ";
    
    public Optional<TreeData> analyse() {
        if (Files.notExists(root)) {
            return Optional.empty();
        }
        String rootStrs[] = {"(Files) Directory", EpocDateTime.nowToString()};
        TreeData parent = new TreeData(rootStrs, this.root);
        Extensions.extractSpecificType(FileUtils.getNodeFiles(root), config.getExtensions()).forEach(file -> {
            output.ifPresent(view -> view.print(ANALYSE_MESSAGE, file.toString()));
            OptionalDouble result = RevisionAnalysers.analyse(file, config.getAnalyser());
            result.ifPresent(r -> {
                String childStrs[] = {file.toString(), ReduceLength.reduceDecimal(r)};
                parent.addChild(new TreeData(childStrs, RevisionAnalysers.checkCaution(result.getAsDouble(), config.getAnalyser()), file));
            });
            output.ifPresent(view -> view.clear());
        });
        return Optional.of(parent);
    }

    private static final String LEARNIG_MESSAGE = "Learning : ";
    public void learn(List<Object> indexList, boolean isAccurate) {
        indexList.stream().filter(e -> e instanceof Path).map(e -> (Path) e)
                .filter(e -> Files.exists(e) && Files.isDirectory(e))
                .forEach(e -> {
            output.ifPresent(view -> view.print(LEARNIG_MESSAGE, e.toString()));
            RevisionAnalysers.learn(e, isAccurate, config.getAnalyser());
            output.ifPresent(view -> view.clear());
        });
    }
}
