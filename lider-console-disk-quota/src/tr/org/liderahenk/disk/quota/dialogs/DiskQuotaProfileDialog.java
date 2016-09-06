package tr.org.liderahenk.disk.quota.dialogs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.disk.quota.constants.DiskQuotaConstants;
import tr.org.liderahenk.disk.quota.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.dialogs.IProfileDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.model.Profile;

/**
 * Profile definition dialog for Disk Quota plugin.
 * 
 * @author <a href="mailto:mine.dogan@agem.com.tr">Mine Dogan</a>
 *
 */
public class DiskQuotaProfileDialog implements IProfileDialog {
	
	private static final Logger logger = LoggerFactory.getLogger(DiskQuotaProfileDialog.class);
	
	private Spinner spinnerSoftQuota;
	private Spinner spinnerHardQuota;
	
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
		composite.setLayout(new GridLayout(3, false));
		
		Label lblChangeSoftQuota = new Label(composite, SWT.NONE);
		lblChangeSoftQuota.setText(Messages.getString("SOFT_QUOTA"));
		
		GridData data = new GridData(SWT.FILL, SWT.FILL, false, false);
		data.widthHint = 100;
		
		spinnerSoftQuota = new Spinner(composite, SWT.BORDER);
		spinnerSoftQuota.setMinimum(DiskQuotaConstants.MIN_VALUE);
		spinnerSoftQuota.setIncrement(1000);
		spinnerSoftQuota.setMaximum(DiskQuotaConstants.MAX_VALUE);
		spinnerSoftQuota.setLayoutData(data);
		
		if(profile != null && profile.getProfileData() != null) {
			spinnerSoftQuota.setSelection(Integer.parseInt((String) profile.getProfileData().get(DiskQuotaConstants.PARAMETERS.SOFT_QUOTA)));
		}
		
		Label lblSoftQuotaMB = new Label(composite, SWT.NONE);
		lblSoftQuotaMB.setText("MB");
		
		Label lblChangeHardQuota = new Label(composite, SWT.NONE);
		lblChangeHardQuota.setText(Messages.getString("HARD_QUOTA"));
		
		spinnerHardQuota = new Spinner(composite, SWT.BORDER);
		spinnerHardQuota.setMinimum(DiskQuotaConstants.MIN_VALUE);
		spinnerHardQuota.setIncrement(1000);
		spinnerHardQuota.setMaximum(DiskQuotaConstants.MAX_VALUE);
		spinnerHardQuota.setLayoutData(data);
		
		if(profile != null && profile.getProfileData() != null) {
			spinnerHardQuota.setSelection(Integer.parseInt((String) profile.getProfileData().get(DiskQuotaConstants.PARAMETERS.HARD_QUOTA)));
		}
		
		Label lblHardQuotaMB = new Label(composite, SWT.NONE);
		lblHardQuotaMB.setText("MB");
	}
	
	@Override
	public Map<String, Object> getProfileData() throws Exception {
		Map<String, Object> profileData = new HashMap<String, Object>();
		profileData.put(DiskQuotaConstants.PARAMETERS.SOFT_QUOTA, spinnerSoftQuota.getText());
		profileData.put(DiskQuotaConstants.PARAMETERS.HARD_QUOTA, spinnerHardQuota.getText());
		return profileData;
	}

	@Override
	public void validateBeforeSave() throws ValidationException {
	}
	
}
