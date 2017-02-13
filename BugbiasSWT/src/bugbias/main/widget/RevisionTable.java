package bugbias.main.widget;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class RevisionTable extends ResultTreeTable implements IWidgetListnerAction {
    
    private static final String[] COLUMN_LABELS = {
            "ID / FileName",
            "Date / BugProbability(FaultProneFiltering)"
    };

    public RevisionTable(Composite parent, int style) {
        super(parent, style);
        this.setLayout(new GridLayout(1, false));
        this.setColumns(Arrays.asList(COLUMN_LABELS));
        this.addKeyListener(new KeyListener() {
            
            @Override
            public void keyReleased(KeyEvent event) {
                if ((event.stateMask & SWT.CTRL) != 0 && (event.keyCode == 'ｓ')) {
                    writeout();
                }
            }
            
            @Override
            public void keyPressed(KeyEvent event) {
            }
        });
    }

    @Override
    public void dispose() {
        super.dispose();
    }
    
    @Override
    public void writeout() {
        FileDialog saveDialog = new FileDialog(this.getShell(), SWT.SAVE);
        saveDialog.setFilterPath(System.getProperty("user.dir"));
        saveDialog.setFilterExtensions(new String[]{".csv"});
        String saveFile = saveDialog.open();
        if (saveFile == null) {
            return;
        }
        Path savePath = Paths.get(saveFile).toAbsolutePath();
        if (Files.exists(savePath)) {
            MessageBox msgBox = new MessageBox(this.getShell(), SWT.YES|SWT.NO);
            msgBox.setText("File is already exist. Do you want override?");
            msgBox.setMessage("SWT.YES|SWT.NO");
            if (msgBox.open() == SWT.NO) {
                return;
            }
        }
        try (BufferedWriter writer = Files.newBufferedWriter(savePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            Tree tree = this.getTree();
            ArrayList<String[]> list = new ArrayList<>();
            Arrays.asList(tree.getColumns()).stream().map(e -> e.getText()).forEach(e -> {
                list.add(e.split("/"));
            });
            int columnLength = list.size();
            int columnDepth = list.stream().map(e -> e.length).max(Comparator.naturalOrder()).get();
            StringJoiner str = new StringJoiner(" , ");
            IntStream.range(0, columnDepth).forEach(depth -> {
                list.forEach(e -> {
                    if (depth < e.length) {
                        str.add(e[depth]);
                    } else {
                        str.add(e[0]);
                    }
                });
            });
            writer.write(str.toString());
            writer.newLine();
            for (TreeItem parent : tree.getItems()) {
                for (String line : getItemText(parent, columnLength, 0)) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    
    private ArrayList<String> getItemText(TreeItem item, int columnLength, int depth) {
        ArrayList<String> list = new ArrayList<>();
        StringJoiner contents = new StringJoiner(" , ");
        contents.add(IntStream.range(0, columnLength * depth).mapToObj(e -> " ").collect(Collectors.joining(" , ")));
        contents.add(IntStream.range(0, columnLength).mapToObj(e -> item.getText(e)).collect(Collectors.joining(" , ")));
        list.add(contents.toString());
        Arrays.asList(item.getItems()).forEach(e -> {
            list.addAll(getItemText(e, columnLength, depth + 1));
        });
        return list;
    }
}