package bugbias.main.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import bugbias.main.internal.TreeData;

public class ResultTreeTable extends Composite implements IWidgetListnerAction {

    private Tree tree;
    
    public ResultTreeTable(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(1,false));
        tree = new Tree(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        tree.setLayoutData(new GridData(GridData.FILL_BOTH));
        tree.addKeyListener(new KeyListener() {
            
            @Override
            public void keyReleased(KeyEvent event) {
                if ((event.stateMask & SWT.CTRL) != 0 && (event.keyCode == 'c')) {
                    copy();
                } else if ((event.stateMask & SWT.CTRL) != 0 && (event.keyCode == 'a')) {
                    selectAll();
                }
            }
            
            @Override
            public void keyPressed(KeyEvent event) {
            }
        });
    }

    private void resizeTable() {
        getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                tree.setVisible(false);
                TreeColumn[] columns = tree.getColumns();
                Arrays.stream(columns).forEach(e -> e.pack());
                tree.setVisible(true);
            }
        });
    }
    
    public void setColumns(List<String> columnLabels) {
        getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                tree.setHeaderVisible(true);
                Arrays.asList(tree.getColumns()).forEach(column -> column.dispose());
                columnLabels.forEach(label ->{
                    TreeColumn column = new TreeColumn(tree,SWT.LEFT);
                    column.setText(label);
                });
            }
        });
        resizeTable();
    }
    
    public void setInput(ArrayList<TreeData> list) {
        getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                tree.removeAll();
            }
        });
        addInput(list);
    }
    
    public void addInput(ArrayList<TreeData> list) {
        getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
//                tree.setVisible(false);
                list.forEach(parent -> {
                    deployData(parent, tree);
                });
                tree.redraw();
                resizeTable();
//                tree.setVisible(true);
            }
        });
    }

    public void addInput(TreeData parent) {
        getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
//                tree.setVisible(false);
                deployData(parent, tree);
                tree.redraw();
                resizeTable();
//                tree.setVisible(true);
            }
        });
    }
    
    public void deployData(TreeData data, Object parent) {
        TreeItem treeItem;
        if (parent instanceof Tree) {
            Tree parentData = (Tree) parent;
            treeItem = new TreeItem(parentData, SWT.NONE);
        } else if (parent instanceof TreeItem) {
            TreeItem parentData = (TreeItem) parent;
            treeItem = new TreeItem(parentData, SWT.NONE);
        } else {
            return;
        }
        treeItem.setText(data.getText());
        data.getTextColor().ifPresent(col -> treeItem.setBackground(col));
        data.getBindedData().ifPresent(binded -> treeItem.setData(binded));
        data.getChildren().forEach(child -> deployData(child, treeItem));
    }

    public List<TreeItem> getSelectedItem() {
        TreeItem[] items = tree.getSelection();
        return (items.length > 0)?  Arrays.asList(items) : new ArrayList<>();
    }
    public List<Object> getSelectedItemData() {
        TreeItem[] items = tree.getSelection();
        return (items.length > 0)?  Arrays.asList(items).stream().map(e -> e.getData()).collect(Collectors.toList()) : new ArrayList<>();
    }

    public List<TreeItem> getSelectedParents() {
        TreeItem[] items = tree.getSelection();
        List<TreeItem> parents = new ArrayList<>();
        Arrays.asList(items).forEach(item -> {
            if (item. getParentItem() == null) {
                parents.add(item);
            } else {
                parents.add(item.getParentItem());
            }
        });
        return parents;
    }
    
    public List<TreeItem> getSelectedChildren() {
        TreeItem[] items = tree.getSelection();
        List<TreeItem> children = new ArrayList<>();
        Arrays.asList(items).forEach(item -> {
            if (item. getParentItem() != null) {
                children.add(item);
            } else {
                Arrays.asList(item.getItems()).forEach(child -> children.add(child));
            }
        });
        return children;
    }

    public void addSelectionListener(SelectionListener listener) {
        tree.addSelectionListener(listener);
    }

    protected Tree getTree() {
        return tree;
    }


    @Override
    public void setMenu(Menu menu) {
        this.tree.setMenu(menu);
    };
    
    @Override
    public void clear() {
        tree.removeAll();
    }

    @Override
    public void dispose() {
        Arrays.asList(this.tree.getColumns()).forEach(TreeColumn::dispose);
        Optional<Menu> menu = Optional.ofNullable(this.tree.getMenu());
        menu.ifPresent(m -> Arrays.asList(m.getItems()).forEach(mi -> mi.dispose()));
        menu.ifPresent(m -> m.dispose());
        this.tree.dispose();
    }

    @Override
    public void selectAll() {
        this.tree.selectAll();
    }

    @Override
    public void copy() {
        ArrayList<String> lines = new ArrayList<>();
        int count = tree.getColumnCount();
        Arrays.asList(tree.getSelection()).forEach(item -> {
            lines.add(IntStream.range(0, count).mapToObj(e -> item.getText(e)).collect(Collectors.joining("\t")));
        });
        Clipboard clipboard = new Clipboard(getDisplay());
        clipboard.setContents(new Object[] {String.join("\n", lines)}, new Transfer[] {TextTransfer.getInstance()});
    }

    @Override
    public void writeout() {
    }

}
