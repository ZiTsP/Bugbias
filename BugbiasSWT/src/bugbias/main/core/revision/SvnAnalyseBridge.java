package bugbias.main.core.revision;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.stream.Collectors;

import org.tmatesoft.svn.core.wc.SVNRevision;

import bugbias.main.internal.TreeData;
import bugbias.main.widget.AuthenticateDialog;
import bugbias.main.widget.IOutputView;
import bugbias.main.widget.RevisionTable;
import libraries.putils.EpocDateTime;
import libraries.putils.ReduceLength;
import libraries.revisiontrace.LinesExtraction;
import libraries.revisiontrace.svn.SvnDiffEntry;
import libraries.revisiontrace.svn.SvnPortal;

public class SvnAnalyseBridge implements IAnalyseRevisionBridge {

    private final SvnPortal SVN;
    private RevAnalyseConfig config = RevAnalyseConfig.DEFAULT_CONFIG;
    private Optional<IOutputView> output = Optional.empty();
    private Optional<RevisionTable> widget = Optional.empty();

    public SvnAnalyseBridge(Path path) {
        SVN = new SvnPortal(path.toAbsolutePath().toString());
        this.setConfig(RevAnalyseConfig.DEFAULT_CONFIG);
        this.head = SVN.getLatestRevision();
    }
    
    public SvnAnalyseBridge(Path path, RevAnalyseConfig config) {
        SVN = new SvnPortal(path.toAbsolutePath().toString());
        this.setConfig(config);
        this.head = SVN.getLatestRevision();
    }

    private OptionalLong head = OptionalLong.empty();
    
    public boolean isUpdated() {
        OptionalLong head = SVN.getLatestRevision();
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
            return analyseRevision(SVNRevision.create(head.getAsLong()));
        }
        return Optional.empty();
    }


    @Override
    public Optional<TreeData> analyseRevision(Object index) {
        if (index instanceof Long) {
            long revision = (long) index;
            ArrayList<String> list = new ArrayList<>();
            StringBuffer str = new StringBuffer("Rev :");
            list.add(str.append(revision).toString());
            String strs[] = list.toArray(new String[list.size()]);
            TreeData parent = new TreeData(strs, revision);
            parent.addChild(this.analyse(SVN.getDiffEntries(revision)));
            return Optional.of(parent);
        } else if (index instanceof SVNRevision) {
            long revision = ((SVNRevision) index).getNumber();
            ArrayList<String> list = new ArrayList<>();
            StringBuffer str = new StringBuffer("Rev :");
            list.add(str.append(revision).toString());
            String strs[] = list.toArray(new String[list.size()]);
            TreeData parent = new TreeData(strs, revision);
            parent.addChild(this.analyse(SVN.getDiffEntries(revision)));
            return Optional.of(parent);
        }
        return Optional.empty();
    }

    private static final String ANALYSE_MESSAGE = "Analysing : ";

    private List<TreeData> analyse(List<SvnDiffEntry> entries) {
        List<TreeData> children = new ArrayList<>();
        SVN.extractSpecificType(entries, config.getExtensions()).forEach(e -> {
            output.ifPresent(view -> view.print(ANALYSE_MESSAGE, e));
            try {
                Path tmpDiff = Files.createTempFile(DIFF_FILE_PREFIX, null);
                try (OutputStream diffWriter = Files.newOutputStream(tmpDiff)) {
                    BufferedOutputStream buffWriter = new BufferedOutputStream(diffWriter, BUFFER_SIZE);
                    SVN.outPutDiff(e.getFilePath(), e.getRevisionAsLong(), buffWriter);
                    buffWriter.flush();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                OptionalDouble result = RevisionAnalysers.analyse(tmpDiff, config.getAnalyser());
                result.ifPresent(r -> {
                    String strs[] = {e.getFilePath(), ReduceLength.reduceDecimal(r)};
                    TreeData child = new TreeData(strs, RevisionAnalysers.checkCaution(r, config.getAnalyser()), e);
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

    @Override
    public Optional<TreeData> analyseCurrentRevision() {
        if (head.isPresent()) {
            ArrayList<String> list = new ArrayList<>();
            list.add("(SVN) Working Directory");
            list.add(EpocDateTime.nowToString());
            String strs[] = list.toArray(new String[list.size()]);
            TreeData parent = new TreeData(strs, head.getAsLong());
            List<TreeData> children = new ArrayList<>();
            SVN.extractSpecificType(SVN.getCacheEntries(), config.getExtensions()).forEach(e -> {
                output.ifPresent(view -> view.print(ANALYSE_MESSAGE, e));
                try {
                    Path tmpDiff = Files.createTempFile(DIFF_FILE_PREFIX, null);
//                    try (OutputStream diffWriter = Files.newOutputStream(tmpDiff)) {
//                        BufferedOutputStream buffWriter = new BufferedOutputStream(diffWriter, BUFFER_SIZE);
//                        SVN.outPutWorkingDiff(e.getFilePath(), buffWriter);
//                        buffWriter.flush();
//                    } catch (IOException exception) {
//                        exception.printStackTrace();
//                    }
                    Files.write(tmpDiff, LinesExtraction.extractAddedLine(SVN.outPutDiff(e)));
                    OptionalDouble result = RevisionAnalysers.analyse(tmpDiff, config.getAnalyser());
                    result.ifPresent(r -> {
                        String childStrs[] = {e.getFilePath(), ReduceLength.reduceDecimal(r)};
                        TreeData child = new TreeData(childStrs, RevisionAnalysers.checkCaution(r, config.getAnalyser()), e);
                        children.add(child);
                    });
                    Files.delete(tmpDiff);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                output.ifPresent(view -> view.clear());
            });
            parent.addChild(children);
            return Optional.of(parent);
        }
        return Optional.empty();
    }
    
    private static final String LEARNIG_MESSAGE = "Learning : ";
    private static final String DIFF_FILE_PREFIX = "SVNANALYSEBRIDGE-";
    private static final int BUFFER_SIZE = 512 * 1024;
    
    @Override
    public void learn(List<Object> indexList, boolean isAccurate) {
        List<SvnDiffEntry> tmp = new ArrayList<>();
        indexList.forEach(index -> {
            if (index instanceof Long) {
                tmp.addAll(SVN.getDiffEntries((long) index));
            } else if (index instanceof SvnDiffEntry) {
                tmp.add((SvnDiffEntry) index);
            }
        });
        List<SvnDiffEntry> diffEntries = tmp.stream().distinct().collect(Collectors.toList());
        diffEntries.forEach(e -> {
            output.ifPresent(view -> view.print(LEARNIG_MESSAGE, e.getFilePath()));
            try {
                Path tmpDiff = Files.createTempFile(DIFF_FILE_PREFIX, null);
                try (OutputStream diffWriter = Files.newOutputStream(tmpDiff)) {
                    BufferedOutputStream buffWriter = new BufferedOutputStream(diffWriter, BUFFER_SIZE);
                    SVN.outPutDiff(e.getFilePath(), e.getRevisionAsLong(), buffWriter);
                    buffWriter.flush();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
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
        return Optional.ofNullable(SVN.getLatestRevision());
    }


    @Override
    public void setConfig(RevAnalyseConfig config) {
        this.config = (config != null) ? config : this.config;
    }

    @Override
    public void setWidget(RevisionTable widget) {
        this.widget = Optional.ofNullable(widget);
        this.widget.ifPresent(w -> {
            AuthenticateDialog dialog = new AuthenticateDialog(w.getShell());
            config.getAuther().ifPresent(auth -> {
                dialog.setAuther(auth);
                config.getPassword(auth).ifPresent(pass -> dialog.setPassword(pass));
            });
            if (dialog.open() == true) {
                SVN.setAuthenticate(dialog.getAuther().orElse(null), dialog.getPassword().orElse(null));
                config.setAuthenticate(dialog.getAuther().orElse(null), dialog.getPassword().orElse(null));
                this.head = SVN.getLatestRevision();
            }
        });
    }
}
