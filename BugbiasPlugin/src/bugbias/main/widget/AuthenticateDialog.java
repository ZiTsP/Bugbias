package bugbias.main.widget;

import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AuthenticateDialog  extends Dialog{
    private Optional<String> auther;
    private Optional<String> password;
    private boolean isOk = false;
    
    public AuthenticateDialog(Shell parent) {
      this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    }

    public AuthenticateDialog(Shell parent, int style) {
      super(parent, style);
      setText("Authenticate Setting");
    }
    
    public Optional<String> getAuther() {
      return auther;
    }
    
    public void setAuther(String auther) {
      this.auther = Optional.ofNullable(auther);
      this.auther.ifPresent(auth -> this.autherText.setText(auth));
    }
    
    public Optional<String> getPassword() {
      return password;
    }
    
    public void setPassword(String password) {
        this.password = Optional.ofNullable(password);
        this.password.ifPresent(pass -> this.passwordText.setText(pass));
    }

    public boolean open() {
      Shell shell = new Shell(getParent(), getStyle());
      shell.setText(getText());
      createContents(shell);
      shell.pack();
      shell.open();
      Display display = getParent().getDisplay();
      while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) {
          display.sleep();
        }
      }
      return isOk;
    }

    private Label messageLabel;
    private static final String MESSAGE_STRING = "Please input auther information.\n(This data is saved only this time.)";
    private Label autherLabel;
    private static final String AUTHER_STRING = "AUTHER  : ";
    private Text autherText;
    private Label passwordLabel;
    private static final String PASSWORD_STRING = "PASSWORD : ";
    private Text passwordText;
    private Button okBtn;
    private Button cancelBtn;

    private void createContents(final Shell shell) {
        GridData wideGridData = new GridData(GridData.FILL_HORIZONTAL);
        wideGridData.horizontalSpan = 2;
        shell.setLayout(new GridLayout(2, true));
        messageLabel = new Label(shell, SWT.NONE);
        messageLabel.setText(MESSAGE_STRING);
        messageLabel.setLayoutData(wideGridData);
        autherLabel = new Label(shell, SWT.NONE);
        autherLabel.setText(AUTHER_STRING);
        autherLabel.setLayoutData(new GridData());
        autherText = new Text(shell, SWT.NONE);
        autherText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        passwordLabel = new Label(shell, SWT.NONE);
        passwordLabel.setText(PASSWORD_STRING);
        passwordLabel.setLayoutData(new GridData());
        passwordText = new Text(shell, SWT.NONE);
        passwordText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        passwordText.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
                if (event.character == SWT.CR) {
                    passwordText.traverse(SWT.TRAVERSE_TAB_NEXT);
                }
            }
        });
        okBtn = new Button(shell, SWT.PUSH);
        okBtn.setText("OK");
        okBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        okBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                auther = Optional.ofNullable(autherText.getText());
                password = Optional.ofNullable(passwordText.getText());
                isOk = true;
                shell.close();
            }
        });
        cancelBtn = new Button(shell, SWT.PUSH);
        cancelBtn.setText("Cancel");
        cancelBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        cancelBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                shell.close();
            }
        });
    }
         
}
