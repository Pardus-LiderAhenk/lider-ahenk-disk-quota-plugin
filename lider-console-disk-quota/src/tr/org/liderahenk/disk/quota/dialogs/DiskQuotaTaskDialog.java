package tr.org.liderahenk.disk.quota.dialogs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.disk.quota.constants.DiskQuotaConstants;
import tr.org.liderahenk.disk.quota.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.ldap.enums.DNType;
import tr.org.liderahenk.liderconsole.core.rest.requests.TaskRequest;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.liderconsole.core.xmpp.notifications.TaskStatusNotification;

/**
 * Task execution dialog for disk-quota plugin.
 * 
 * @author <a href="mailto:mine.dogan@agem.com.tr">Mine Dogan</a>
 * 
 */
public class DiskQuotaTaskDialog extends DefaultTaskDialog {

	private Text txtSoftQuota;
	private Text txtHardQuota;
	private Text txtDiskUsage;
	private Spinner spinnerSoftQuota;
	private Spinner spinnerHardQuota;

	private String softQuota;
	private String hardQuota;
	private String usage;

	private static final Logger logger = LoggerFactory.getLogger(DiskQuotaTaskDialog.class);

	public DiskQuotaTaskDialog(Shell parentShell, Set<String> dnSet) {
		super(parentShell, dnSet);
		subscribeEventHandler(taskStatusNotificationHandler);
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

		try {
			TaskRequest task = new TaskRequest(new ArrayList<String>(getDnSet()), DNType.AHENK, getPluginName(),
					getPluginVersion(), getCommandId(), getParameterMap(), null, null, new Date());
			TaskRestUtils.execute(task);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			Notifier.error(null, Messages.getString("ERROR_ON_EXECUTE"));
		}

		return composite;
	}

	@Override
	public void validateBeforeExecution() throws ValidationException {
	}

	@Override
	public Map<String, Object> getParameterMap() {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put(DiskQuotaConstants.PARAMETERS.SOFT_QUOTA, spinnerSoftQuota.getText());
		parameterMap.put(DiskQuotaConstants.PARAMETERS.HARD_QUOTA, spinnerHardQuota.getText());
		return parameterMap;
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

	class QuotaRunnable implements Runnable {

		private String softQuota;
		private String hardQuota;
		private String usage;

		private Text txtSoftQuota;
		private Text txtHardQuota;
		private Text txtDiskUsage;

		public QuotaRunnable(String softQuota, String hardQuota, String usage, Text txtSoftQuota, Text txtHardQuota,
				Text txtDiskUsage) {
			this.softQuota = softQuota;
			this.hardQuota = hardQuota;
			this.usage = usage;
			this.txtSoftQuota = txtSoftQuota;
			this.txtHardQuota = txtHardQuota;
			this.txtDiskUsage = txtDiskUsage;
		}

		@Override
		public void run() {
			if (txtSoftQuota != null && softQuota != null) {
				txtSoftQuota.setText(softQuota);
				txtSoftQuota.getParent().layout();
			}
			if (txtHardQuota != null && hardQuota != null) {
				txtHardQuota.setText(hardQuota);
				txtHardQuota.getParent().layout();
			}
			if (txtDiskUsage != null && txtDiskUsage != null) {
				txtDiskUsage.setText(usage);
				txtDiskUsage.getParent().layout();
			}
		}

	}

	private EventHandler taskStatusNotificationHandler = new EventHandler() {
		@Override
		public void handleEvent(final Event event) {
			Job job = new Job("TASK") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("QUOTA", 100);
					try {
						TaskStatusNotification taskStatus = (TaskStatusNotification) event
								.getProperty("org.eclipse.e4.data");
						byte[] data = taskStatus.getResult().getResponseData();
						Map<String, Object> responseData = new ObjectMapper().readValue(data, 0, data.length,
								new TypeReference<HashMap<String, Object>>() {
								});

						softQuota = (String) responseData.get("softQuota");
						hardQuota = (String) responseData.get("hardQuota");
						usage = (String) responseData.get("usage");

						Display.getDefault().asyncExec(new QuotaRunnable(softQuota, hardQuota, usage, txtSoftQuota,
								txtHardQuota, txtDiskUsage));

					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						Notifier.error("", Messages.getString("UNEXPECTED_ERROR"));
					}

					monitor.worked(100);
					monitor.done();

					return Status.OK_STATUS;
				}
			};

			job.setUser(true);
			job.schedule();
		}
	};

}