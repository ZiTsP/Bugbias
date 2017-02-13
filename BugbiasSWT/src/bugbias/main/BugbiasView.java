package bugbias.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import bugbias.main.core.revision.RevAnalyseCore;
import bugbias.main.core.revision.RevisionAnalysers;
import bugbias.main.widget.ProjectSelector;
import bugbias.main.widget.TaskProgress;

public class BugbiasView extends Composite {

    private ProjectSelector selector;
    
    private CTabFolder tab;
//    private CTabItem dirTab;
    private CTabItem revTab;
    private RevAnalyseCore revCore;
//    private CTabItem optTab;
    
    private TaskProgress task;
    
    public BugbiasView(Composite parent, int style) {
        super(parent, style);
        this.setLayout(new GridLayout(1, false));
        selector = new ProjectSelector(this, SWT.NONE);
        selector.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        tab = new CTabFolder(this, SWT.NONE);
        tab.setSimple(false);
        tab.setLayoutData(new GridData(GridData.FILL_BOTH));
        tab.setLayout(new GridLayout(1, false));
        {
            revTab = new CTabItem(tab, SWT.NONE);
            revTab.setText("Analyse");
            revCore = new RevAnalyseCore(tab, SWT.NONE);
            revTab.setControl(revCore.getMainWidget());
        }
        {
//            Option Tab
//            optTab = new CTabItem(tab, SWT.NONE);
//            optTab.setText("Options");
//            Label opt = new Label(tab, SWT.BORDER);
//            opt.setLayoutData(new GridData(GridData.FILL_BOTH));
//            opt.setBackground(new Color(getDisplay(), new RGB(220,220,220)));
//            optTab.setControl(opt);
        }


        DropTarget target = new DropTarget(this, DND.DROP_DEFAULT|DND.DROP_COPY);
        FileTransfer transfer = FileTransfer.getInstance();
        Transfer[] types = new Transfer[]{transfer};
        target.setTransfer(types);
        target.addDropListener(new DropTargetAdapter() {
            public void dragEnter(DropTargetEvent evt){
                evt.detail = DND.DROP_COPY;
            }
            public void drop(DropTargetEvent evt){
                String[] files = (String[])evt.data;
                selector.setInput(files[0]);
                Path path = Paths.get(files[0]);
                Path cfgPath = path.resolve(".bugbias");
                if (Files.notExists(cfgPath)) {
                    try {
                        Files.createDirectories(cfgPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                RevisionAnalysers.init(cfgPath);
                revCore.setRoot(path);
            }
        });
        
        tab.setSelection(0);
        tab.pack();
        task = new TaskProgress(this, SWT.NONE);
        task.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.pack();
    }

    public static void main(String[] args) {
        Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        @SuppressWarnings("unused")
        BugbiasView tree = new BugbiasView(shell, SWT.NONE);
//        shell.pack();
        shell.setSize(1200, 800);
        shell.open();
//        AuthenticateDialog dialog = new AuthenticateDialog(shell);
//        System.out.println(dialog.open());
//        System.out.println(dialog.getPassword());
        while (!shell.isDisposed()) {
          if (!display.readAndDispatch()) {
            display.sleep();
          }
        }
        display.dispose();
    }
}
