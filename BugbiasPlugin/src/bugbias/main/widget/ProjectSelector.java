package bugbias.main.widget;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class ProjectSelector extends Composite {

	private Combo combo;
    private static final String SELECT_PROJECT = "Select Project";

	public ProjectSelector(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout(2, false));
		combo = new Combo(this,SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		initInput(null);
		this.pack();
	}

    @Override
    public void dispose() {
        super.dispose();
    }

	public void initInput(List<String> inputs) {
        getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                combo.removeAll();
                if (inputs == null || inputs.isEmpty()) {
                    combo.add(SELECT_PROJECT);
                    combo.select(0);
                } else {
                    inputs.forEach(input -> combo.add(input.toString()));
                    combo.select(0);
                }
            }
        });
	}

	public void setInput(String input) {
        getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (input != null && !Arrays.asList(combo.getItems()).contains(input)) {
                    combo.add(input);
                }
                combo.select(combo.indexOf(input));
            }
        });

	}

	public void addInput(String input) {
        getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (input != null && !Arrays.asList(combo.getItems()).contains(input)) {
                    combo.add(input);
                }
            }
        });
    }

	public String getText() {
	    return (!combo.getText().equals(SELECT_PROJECT)) ? combo.getText() : "" ;
	}

    public void addComboSelectionListener(SelectionListener listener) {
    	combo.addSelectionListener(listener);
    }
}
