package tr.org.liderahenk.disk.quota.dialogs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.disk.quota.constants.DiskQuotaConstants;
import tr.org.liderahenk.disk.quota.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.dialogs.IProfileDialog;
import tr.org.liderahenk.liderconsole.core.model.Profile;

/**
 * Profile definition dialog for Disk Quota plugin.
 * 
 * @author <a href="mailto:mine.dogan@agem.com.tr">Mine Dogan</a>
 *
 */
public class DiskQuotaProfileDialog implements IProfileDialog {
	
	private static final Logger logger = LoggerFactory.getLogger(DiskQuotaProfileDialog.class);
	
	// Widgets
	private Text txtSoftQuota;
	private Text txtHardQuota;
	private Text txtDiskUsage;
	private Spinner spinnerSoftQuota;
	private Spinner spinnerHardQuota;
	
	private String softQuota;
	private String hardQuota;
	private String usage;
	
	@Override
	public void init() {
	}
	
	@Override
	public void createDialogArea(Composite parent, Profile profile) {
		logger.debug("Profile recieved: {}", profile != null ? profile.toString() : null);
		createQuotaInputs(parent, profile);
	}
	
	private void createQuotaInputs(final Composite parent, final Profile profile) {
		
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
		//txtSoftQuota.setText(softQuota);
		txtSoftQuota.pack();
		
		Label lblHardQuota = new Label(currentQuote, SWT.NONE);
		lblHardQuota.setText(Messages.getString("HARD_QUOTA"));
		lblHardQuota.pack();
		
		txtHardQuota = new Text(currentQuote, SWT.BORDER);
		txtHardQuota.setEnabled(false);
		txtHardQuota.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		//txtHardQuota.setText(hardQuota);
		txtHardQuota.pack();
		
		Label lblDiskUsage = new Label(currentQuote, SWT.NONE);
		lblDiskUsage.setText(Messages.getString("DISK_USAGE"));
		lblDiskUsage.pack();
		
		txtDiskUsage = new Text(currentQuote, SWT.BORDER);
		txtDiskUsage.setEnabled(false);
		txtDiskUsage.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		//txtDiskUsage.setText(usage);
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
		
		if(profile != null && profile.getProfileData() != null) {
			spinnerSoftQuota.setSelection(Integer.parseInt((String) profile.getProfileData().get(DiskQuotaConstants.PARAMETERS.SOFT_QUOTA)));
		}
		
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
		
		if(profile != null && profile.getProfileData() != null) {
			spinnerHardQuota.setSelection(Integer.parseInt((String) profile.getProfileData().get(DiskQuotaConstants.PARAMETERS.HARD_QUOTA)));
		}
		
		Label lblHardQuotaMB = new Label(changeQuote, SWT.NONE);
		lblHardQuotaMB.setText("MB");
		lblHardQuotaMB.pack();
		
		changeQuote.pack();
		
		composite.pack();
		
	}
	
	@Override
	public Map<String, Object> getProfileData() throws Exception {
		Map<String, Object> profileData = new HashMap<String, Object>();
		profileData.put(DiskQuotaConstants.PARAMETERS.SOFT_QUOTA, spinnerSoftQuota.getText());
		profileData.put(DiskQuotaConstants.PARAMETERS.HARD_QUOTA, spinnerHardQuota.getText());
		return profileData;
	}
	
}
