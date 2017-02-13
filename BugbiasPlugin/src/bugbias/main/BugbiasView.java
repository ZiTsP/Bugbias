package bugbias.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import bugbias.main.core.revision.RevAnalyseCore;
import bugbias.main.core.revision.RevisionAnalysers;
import bugbias.main.widget.ProjectSelector;
import bugbias.main.widget.TaskProgress;

public class BugbiasView extends ViewPart {


    @Override
    public void createPartControl(Composite arg0) {
        this.bugbiasViewSetUp(arg0, SWT.NONE);
    }

    @Override
    public void setFocus() {
    }

    private ProjectSelector selector;
    private CTabFolder tab;
    private CTabItem tableTab;
    private RevAnalyseCore revCore;
//    private CTabItem optTab;
    private TaskProgress task;

    private Path targetProject;

    private void bugbiasViewSetUp(Composite parent, int style) {
        Composite composite = new Composite(parent, style);
        composite.setLayout(new GridLayout(1, false));
        selector = new ProjectSelector(composite, SWT.NONE);
        selector.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        selector.addComboSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				getEclipseWorkspaceProjects();
			    try {
			    	if (selector.getText() != null) {
			    		Path project = Paths.get(selector.getText());
			    		if (project == null || project.equals(targetProject)) {
			    			return;
			    		} else {
			    			revCore.setRoot(project);
			    		}
			    	}
			    } catch (java.util.ConcurrentModificationException e) {
			        this.widgetSelected(event);
			    }
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
        tab = new CTabFolder(composite, SWT.NONE);
        tab.setSimple(false);
        tab.setLayoutData(new GridData(GridData.FILL_BOTH));
        tab.setLayout(new GridLayout(1, false));
        {
            tableTab = new CTabItem(tab, SWT.NONE);
            tableTab.setText("Analyse");
            revCore = new RevAnalyseCore(tab, SWT.NONE);
            tableTab.setControl(revCore.getMainWidget());
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
        tab.setSelection(0);
        tab.pack();
        task = new TaskProgress(composite, SWT.NONE);
        task.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        composite.pack();
    }

    public void dispose() {
        this.revCore.dispose();
        this.task.dispose();
        this.tab.dispose();
        this.selector.dispose();
    }

	private void getEclipseWorkspaceProjects() {
		List<String> projectsPath = new ArrayList<>();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
		if (projects == null || projects.length <= 0) {
			return;
		}
		Arrays.stream(projects).forEach(e -> projectsPath.add(e.getLocation().toFile().toPath().toString()));
		selector.initInput(projectsPath);
	}

    @SuppressWarnings("unused")
    private void addDnDInputProject(Composite composite) {
        DropTarget target = new DropTarget(composite, DND.DROP_DEFAULT|DND.DROP_COPY);
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
    }
}
