package bugbias.main.widget;

import java.util.StringJoiner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public final class TaskProgress extends Composite implements  IOutputView{

    private Label label;
	
	public TaskProgress(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout(1, false));
		label = new Label(this, SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

    @Override
    public void dispose() {
        this.label.dispose();
        super.dispose();
    }

    @Override
    public void print(Object obj) {
        getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                label.setText(obj.toString());
            }
        });
    }

    @Override
    public void print(Object... objs) {
        StringJoiner str = new StringJoiner(" ");
        for (Object obj : objs) {
            str.add(obj.toString());
        }
        this.print(str.toString());
    }

    @Override
    public void caution(Object obj) {
        getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                label.setText(obj.toString());
                label.setForeground(new Color(getDisplay(), 255, 0, 0, 0));
            }
        });
    }
    
    @Override
    public void clear() {
        getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                label.setText("");
            }
        });
    }
}