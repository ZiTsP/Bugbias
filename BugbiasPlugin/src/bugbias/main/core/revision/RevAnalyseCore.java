package bugbias.main.core.revision;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import bugbias.main.internal.TreeData;
import bugbias.main.widget.IOutputView;
import bugbias.main.widget.RevisionTable;

public class RevAnalyseCore {
    
    private Optional<RevisionTable> widget = Optional.empty();
    private Optional<RevAnalyseConfig> config = Optional.empty();
    private Optional<Path> root = Optional.empty();
    private Optional<IOutputView> output = Optional.empty();

    public RevAnalyseCore(Composite parent, int style) {
        RevisionTable widget = new RevisionTable(parent, style);
        widget.setLayoutData(new GridData(GridData.FILL_BOTH));
        widget.setMenu(initMenu(widget.getShell()));
        this.config = Optional.of(RevAnalyseConfig.getDeafult());
        this.widget = Optional.of(widget);
    }

    public RevAnalyseCore(Composite parent, int style, RevAnalyseConfig config) {
        this(parent, style);
        this.setConfig(config);
    }
    
    public RevAnalyseCore(Composite parent, int style, RevAnalyseConfig config, IOutputView output) {
        this(parent, style, config);
        this.setOutputView(output);
    }
    
    public void setConfig(RevAnalyseConfig config) {
        this.config = (config == null) ? Optional.of(RevAnalyseConfig.DEFAULT_CONFIG) : Optional.of(config);
    }
    
    public void setOutputView(IOutputView output) {
        this.output = Optional.ofNullable(output);
    }

    public RevisionTable getMainWidget() {
        return (widget.isPresent()) ? widget.get() : null;
    }
    
    public void setRoot(Path path) {
        Optional<Path> newPath = Optional.ofNullable(path);
        if (newPath.isPresent() && Files.exists(path) && Files.isDirectory(path)) {
            root = newPath;
            update();
        }
    }
    
    public Optional<Path> getRootPath() {
        return this.root;
    }

    private static final String NOT_VALID_DIRECTORY = "Not Valid Directory";
    private static final String NOT_REPOSITORY = "Not Repository or Not Supported";
    private Optional<IAnalyseRevisionBridge> revisionBridge = Optional.empty();
    private Optional<RevWatchingService> watchService = Optional.empty();
    private Optional<DirAnalyseBridge> directoryBridge = Optional.empty();
    
    
    private void update() {
        killServices();
        widget.ifPresent(RevisionTable::clear);
        directoryBridge = DirAnalyseBridge.getNewDirAnalyseBridge(this.root.get());
        if (!directoryBridge.isPresent()) {
            output.ifPresent(e -> e.print(NOT_VALID_DIRECTORY));
            return;
        }
        revisionBridge = RevAnalyseBridges.getRevisionAnalysePortal(this.root.get());
        if (revisionBridge.isPresent()) {
            widget.ifPresent(widget -> revisionBridge.get().setWidget(widget));
            output.ifPresent(output -> revisionBridge.get().setOutput(output));
            config.ifPresent(config -> revisionBridge.get().setConfig(config));
//            Optional<TreeData> result = revisionBridge.get().analyseLastRevision();
//            result.ifPresent(r -> widget.get().addInput(r));
            watchService = Optional.of(new RevWatchingService(this, (config.isPresent()) ? config.get().getWatchConfig() : RevWatchingConfig.DEFAULT_CONFIG));
            watchService.ifPresent(service -> service.start());
        } else {
            output.ifPresent(e -> e.print(NOT_REPOSITORY));
        }
    }
    
    private void killServices() {
        watchService.ifPresent(service -> service.kill());
    }

    private static final String SELECT_ALL = "Select All";
    private static final String REMOVE_ALL = "Remove All";
    private static final String EXPORT_CSV = "Export in CSV";
    private static final String COPY = "Copy";
//    private static final String SHOW_DIFF= "Show Diff";
//    private static final String PREFERENCE = "Option";
    private static final String RESET_AUTOWATCH = "Reset Auto Watching";
    private static final String ANALYSE_HEAD = "Analyse Revision (Head^ to Head)";
    private static final String ANALYSE_CURRENT = "Analyse Revision (Head to Working)";
    private static final String ANALYSE_FILES = "Analyse all Files in this Project";
    private static final String LEARN_FAULTY = "Learn (as Faulty)";
    private static final String LEARN_ACCURATE = "Learn (as Non-Faulty)";
    
    private Menu initMenu(Decorations parent) {
        Menu menu = new Menu(parent, SWT.POP_UP);
        MenuItem item = new MenuItem(menu, SWT.NONE);
        item.setText(SELECT_ALL);
        item.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widget.ifPresent(RevisionTable::selectAll);
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
        item = new MenuItem(menu, SWT.NONE);
        item.setText(REMOVE_ALL);
        item.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widget.ifPresent(RevisionTable::clear);
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
        item = new MenuItem(menu, SWT.NONE);
        item.setText(EXPORT_CSV);
        item.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widget.ifPresent(w -> w.writeout());
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
        item = new MenuItem(menu, SWT.NONE);
        item.setText(COPY);
        item.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widget.ifPresent(w ->w.copy());
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
//        item = new MenuItem(menu, SWT.SEPARATOR);
//        item = new MenuItem(menu, SWT.NONE);
//        item.setText(SHOW_DIFF);
//        item.addSelectionListener(new SelectionListener() {
//            
//            @Override
//            public void widgetSelected(SelectionEvent arg0) {
//                System.out.println(arg0);
////              
//            }
//            
//            @Override
//            public void widgetDefaultSelected(SelectionEvent arg0) {
//            }
//        });
//        item = new MenuItem(menu, SWT.SEPARATOR);
//        item = new MenuItem(menu, SWT.NONE);
//        item.setText(PREFERENCE);
//        item.addSelectionListener(new SelectionListener() {
//            
//            @Override
//            public void widgetSelected(SelectionEvent arg0) {
//                System.out.println(arg0);
////                open config tab
//            }
//            
//            @Override
//            public void widgetDefaultSelected(SelectionEvent arg0) {
//            }
//        });
        item = new MenuItem(menu, SWT.SEPARATOR);
        item = new MenuItem(menu, SWT.NONE);
        item.setText(RESET_AUTOWATCH);
        item.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                watchService.ifPresent(service -> service.resetSleep());
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
        item = new MenuItem(menu, SWT.SEPARATOR);
        item = new MenuItem(menu, SWT.NONE);
        item.setText(ANALYSE_HEAD);
        item.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widget.ifPresent(table -> {
                    revisionBridge.ifPresent(bridge -> {
                        Optional<TreeData> result = bridge.analyseLastRevision();
                        result.ifPresent(r -> table.addInput(r));
                    });
                });
            
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
        item = new MenuItem(menu, SWT.NONE);
        item.setText(ANALYSE_CURRENT);
        item.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widget.ifPresent(table -> {
                    revisionBridge.ifPresent(bridge -> {
                        Optional<TreeData> result = bridge.analyseCurrentRevision();
                        result.ifPresent(r -> table.addInput(r));
                    });
                });
                watchService.ifPresent(service -> service.resetSleep());
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
        item = new MenuItem(menu, SWT.NONE);
        item.setText(ANALYSE_FILES);
        item.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widget.ifPresent(table -> {
                    directoryBridge.ifPresent(bridge -> {
                        Optional<TreeData> result = bridge.analyse();
                        result.ifPresent(r -> table.addInput(r));
                    });
                });
                watchService.ifPresent(service -> service.resetSleep());
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
        item = new MenuItem(menu, SWT.SEPARATOR);
        item = new MenuItem(menu, SWT.NONE);
        item.setText(LEARN_FAULTY);
        item.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widget.ifPresent(table -> {
                    List<Object> tmp = table.getSelectedItemData();
                    revisionBridge.ifPresent(bridge -> bridge.learn(table.getSelectedItemData(), false));
                    directoryBridge.ifPresent(bridge -> bridge.learn(tmp, false));
                });
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
        item = new MenuItem(menu, SWT.NONE);
        item.setText(LEARN_ACCURATE);
        item.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widget.ifPresent(table -> {
                    List<Object> tmp = table.getSelectedItemData();
                    revisionBridge.ifPresent(bridge -> bridge.learn(table.getSelectedItemData(), true));
                    directoryBridge.ifPresent(bridge -> bridge.learn(tmp, true));
                });
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
        return menu;
    }
    
    public void dispose() {
        killServices();
        widget.ifPresent(RevisionTable::dispose);
    }

    public Optional<IAnalyseRevisionBridge> getRevisionBridge() {
        return this.revisionBridge;
    }

}
