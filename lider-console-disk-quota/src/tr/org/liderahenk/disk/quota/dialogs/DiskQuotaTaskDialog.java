package tr.org.liderahenk.disk.quota.dialogs;

import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.disk.quota.constants.DiskQuotaConstants;
import tr.org.liderahenk.disk.quota.i18n.Messages;

/**
 * Task execution dialog for disk-quota plugin.
 * 
 */
public class DiskQuotaTaskDialog extends DefaultTaskDialog {
	
	private Text txtSoftQuota;
	private Text txtHardQuota;
	private Text txtDiskUsage;
	private Spinner spinnerSoftQuota;
	private Spinner spinnerHardQuota;
	
	public DiskQuotaTaskDialog(Shell parentShell, Set<String> dnSet) {
		super(parentShell, dnSet);
	}

	@Override
	public String createTitle() {
		return Messages.getString("DISK_QUOTA_MANAGEMENT");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(6, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Group currentQuote = new Group(composite, SWT.SHADOW_ETCHED_IN);
		currentQuote.setText(Messages.getString("CURRENT_QUOTA"));
		currentQuote.setLayout(new GridLayout(2, false));
		currentQuote.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 3));
		
		Label separator = new Label(composite, SWT.VERTICAL | SWT.SEPARATOR);
	    separator.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		
		Group changeQuote = new Group(composite, SWT.SHADOW_ETCHED_IN);
		changeQuote.setText(Messages.getString("CHANGE_QUOTA"));
		changeQuote.setLayout(new GridLayout(3, false));
		changeQuote.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 2));
		
		Label lblSoftQuota = new Label(currentQuote, SWT.NONE);
		lblSoftQuota.setText(Messages.getString("SOFT_QUOTA"));
		lblSoftQuota.pack();
		
		txtSoftQuota = new Text(currentQuote, SWT.BORDER);
		txtSoftQuota.setEnabled(false);
		txtSoftQuota.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		txtSoftQuota.pack();
		
		Label lblHardQuota = new Label(currentQuote, SWT.NONE);
		lblHardQuota.setText(Messages.getString("HARD_QUOTA"));
		lblHardQuota.pack();
		
		txtHardQuota = new Text(currentQuote, SWT.BORDER);
		txtHardQuota.setEnabled(false);
		txtHardQuota.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		txtHardQuota.pack();
		
		Label lblDiskUsage = new Label(currentQuote, SWT.NONE);
		lblDiskUsage.setText(Messages.getString("DISK_USAGE"));
		lblDiskUsage.pack();
		
		txtDiskUsage = new Text(currentQuote, SWT.BORDER);
		txtDiskUsage.setEnabled(false);
		txtDiskUsage.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		txtDiskUsage.pack();
		
		currentQuote.pack();
		
		Label lblChangeSoftQuota = new Label(changeQuote, SWT.NONE);
		lblChangeSoftQuota.setText(Messages.getString("SOFT_QUOTA"));
		lblChangeSoftQuota.pack();
		
		spinnerSoftQuota = new Spinner(changeQuote, SWT.BORDER);
		spinnerSoftQuota.setMinimum(DiskQuotaConstants.MIN_VALUE);
		spinnerSoftQuota.setIncrement(1000);
		spinnerSoftQuota.setMaximum(DiskQuotaConstants.MAX_VALUE);
		spinnerSoftQuota.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		spinnerSoftQuota.pack();
		
		Label lblSoftQuotaMB = new Label(changeQuote, SWT.NONE);
		lblSoftQuotaMB.setText("MB");
		lblSoftQuotaMB.pack();
		
		Label lblChangeHardQuota = new Label(changeQuote, SWT.NONE);
		lblChangeHardQuota.setText(Messages.getString("HARD_QUOTA"));
		lblChangeHardQuota.pack();
		
		spinnerHardQuota = new Spinner(changeQuote, SWT.BORDER);
		spinnerHardQuota.setMinimum(DiskQuotaConstants.MIN_VALUE);
		spinnerHardQuota.setIncrement(1000);
		spinnerHardQuota.setMaximum(DiskQuotaConstants.MAX_VALUE);
		spinnerHardQuota.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		spinnerHardQuota.pack();
		
		Label lblHardQuotaMB = new Label(changeQuote, SWT.NONE);
		lblHardQuotaMB.setText("MB");
		lblHardQuotaMB.pack();
		
		changeQuote.pack();
		
		composite.pack();
		
		return null;
	}

	@Override
	public boolean validateBeforeExecution() {
		return true;
	}

	@Override
	public Map<String, Object> getParameterMap() {
		return null;
	}

	@Override
	public String getCommandId() {
		return "CHANGE-QUOTA";
	}

	@Override
	public String getPluginName() {
		return DiskQuotaConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return DiskQuotaConstants.PLUGIN_VERSION;
	}
	
}