package bugbias.main.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class TreeData {
    
    private final String[] strs;
    private final Optional<Object> data;
    private final Optional<Color> color;
    private List<TreeData> children = new ArrayList<>();
    
    @SuppressWarnings("unused")
    private TreeData(String[] strs) {
        this(strs, null, null);
    }
    
    public TreeData(String[] strs, Object data) {
        this(strs, null, data);
    }

    public TreeData(String[] strs, Color color) {
        this(strs, color, null);
    }

    public TreeData(String[] strs, Color color, Object data) {
        this.strs = strs;
        this.color = Optional.ofNullable(color);
        this.data = Optional.ofNullable(data);
    }
    
    public String[] getText() {
        return this.strs;
    }
    
    public Optional<Object> getBindedData() {
        return this.data;
    }
    
    public Optional<Color> getTextColor() {
        return this.color;
    }
    
    public List<TreeData> getChildren() {
        return this.children;
    }
    
    public void addChild(TreeData child) {
        this.children.add(child);
    }
    
    public void addChild(List<TreeData> children) {
        this.children.addAll(children);
    }
    
    public void addChild(String[] strs) {
        this.children.add(new TreeData(strs, null, null));
    }
    
    public void addChild(String[] strs, Object data) {
        this.children.add(new TreeData(strs, null, data));
    }

    public void addChild(String[] strs, Color color) {
        this.children.add(new TreeData(strs, color, null));
    }
    
    public void addChild(String[] strs, Color color, Object data) {
        this.children.add(new TreeData(strs, color, data));
    }

    public void deployAsParentItem(Tree tree) {
        TreeItem item = new TreeItem(tree, SWT.NONE);
        item.setText(this.strs);
        this.data.ifPresent(e -> item.setData(e));
        this.color.ifPresent(e -> item.setBackground(e));
        this.children.forEach(e -> e.deployAsChildItem(item));
    }
    
    public void deployAsChildItem(TreeItem parent) {
        TreeItem child = new TreeItem(parent, SWT.NONE);
        child.setText(this.strs);
        this.data.ifPresent(e -> child.setData(e));
        this.color.ifPresent(e -> child.setBackground(e));
        this.children.forEach(e -> e.deployAsChildItem(child));
    }
}
