package tr.org.liderahenk.disk.quota.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.disk.quota.constants.DiskQuotaConstants;
import tr.org.liderahenk.disk.quota.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.config.ConfigProvider;
import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.dialogs.AppliedPolicyDialog;
import tr.org.liderahenk.liderconsole.core.editorinput.DefaultEditorInput;
import tr.org.liderahenk.liderconsole.core.ldap.enums.DNType;
import tr.org.liderahenk.liderconsole.core.model.AppliedPolicy;
import tr.org.liderahenk.liderconsole.core.model.Command;
import tr.org.liderahenk.liderconsole.core.model.Profile;
import tr.org.liderahenk.liderconsole.core.rest.utils.PolicyRestUtils;
import tr.org.liderahenk.liderconsole.core.utils.IExportableTableViewer;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;

public class DiskQutaPolicyListEditor extends EditorPart {

	private static final Logger logger = LoggerFactory.getLogger(DiskQutaPolicyListEditor.class);

	private TableViewer tableViewer;
	private TableFilter tableFilter;
	private Text txtSearch;
	private Composite buttonComposite;
	private Button btnViewDetail;
	private Button btnRefreshAppliedPolicy;
	private Button btnUserReport;
	private Button btnGroupReport;
	private Button btnAllReport;

	private AppliedPolicy selectedPolicy;

	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(((DefaultEditorInput) input).getLabel());
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parent.setLayout(new GridLayout(1, false));
		createButtonsArea(parent);
		createTableArea(parent);
	}

	private void createTableArea(final Composite parent) {

		createTableFilterArea(parent);

		tableViewer = SWTResourceManager.createTableViewer(parent, new IExportableTableViewer() {
			@Override
			public Composite getButtonComposite() {
				return buttonComposite;
			}

			@Override
			public String getSheetName() {
				return Messages.getString("AGENT_INFO");
			}

			@Override
			public String getReportName() {
				return Messages.getString("AGENT_INFO");
			}
		});
		createTableColumns();
		populateTable();

		// Hook up listeners
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				Object firstElement = selection.getFirstElement();
				if (firstElement instanceof AppliedPolicy) {
					setSelectedPolicy((AppliedPolicy) firstElement);
				}
				btnViewDetail.setEnabled(true);
			}
		});
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				try {
					AppliedPolicy policy = getSelectedPolicy();
					List<Command> commands = PolicyRestUtils.listCommands(policy.getId());
					AppliedPolicyDialog dialog = new AppliedPolicyDialog(parent.getShell(), policy, commands);
					dialog.open();
				} catch (Exception e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		});

		tableFilter = new TableFilter();
		tableViewer.addFilter(tableFilter);
		tableViewer.refresh();
	}

	private void createTableColumns() {

		TableViewerColumn polColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("POLICY"), 400);
		polColumn.getColumn().setAlignment(SWT.LEFT);
		polColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof AppliedPolicy) {
					return ((AppliedPolicy) element).getLabel();
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn softQuotaColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("SOFT_QUOTA") + " (MB)", 200);
		softQuotaColumn.getColumn().setAlignment(SWT.LEFT);
		softQuotaColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof AppliedPolicy) {
					Set<Profile> profiles = ((AppliedPolicy) element).getPolicyAsObj().getProfiles();
					for (Profile profile : profiles) {
						if (profile.getProfileData() != null
								&& profile.getProfileData().get(DiskQuotaConstants.PARAMETERS.SOFT_QUOTA) != null) {
							return profile.getProfileData().get(DiskQuotaConstants.PARAMETERS.SOFT_QUOTA).toString();
						}
					}
					return null;
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn hardQuotaColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("HARD_QUOTA") + " (MB)", 200);
		hardQuotaColumn.getColumn().setAlignment(SWT.LEFT);
		hardQuotaColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof AppliedPolicy) {
					Set<Profile> profiles = ((AppliedPolicy) element).getPolicyAsObj().getProfiles();
					for (Profile profile : profiles) {
						if (profile.getProfileData() != null
								&& profile.getProfileData().get(DiskQuotaConstants.PARAMETERS.HARD_QUOTA) != null) {
							return profile.getProfileData().get(DiskQuotaConstants.PARAMETERS.HARD_QUOTA).toString();
						}
					}
					return null;
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn defaultQuotaColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("DEFAULT_QUOTA") + " (MB)", 200);
		defaultQuotaColumn.getColumn().setAlignment(SWT.LEFT);
		defaultQuotaColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof AppliedPolicy) {
					Set<Profile> profiles = ((AppliedPolicy) element).getPolicyAsObj().getProfiles();
					for (Profile profile : profiles) {
						if (profile.getProfileData() != null
								&& profile.getProfileData().get(DiskQuotaConstants.PARAMETERS.DEFAULT_QUOTA) != null) {
							return profile.getProfileData().get(DiskQuotaConstants.PARAMETERS.DEFAULT_QUOTA).toString();
						}
					}
					return null;
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn createDateColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("CREATE_DATE"), 100);
		createDateColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof AppliedPolicy) {
					return SWTResourceManager.formatDate(((AppliedPolicy) element).getCreateDate());
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn applyDateColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("APPLY_DATE"), 100);
		applyDateColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof AppliedPolicy && ((AppliedPolicy) element).getApplyDate() != null) {
					return SWTResourceManager.formatDate(((AppliedPolicy) element).getApplyDate());
				}
				return "-";
			}
		});

		TableViewerColumn activationDateColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("ACTIVATION_DATE"), 100);
		activationDateColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof AppliedPolicy && ((AppliedPolicy) element).getActivationDate() != null) {
					return SWTResourceManager.formatDate(((AppliedPolicy) element).getActivationDate());
				}
				return "-";
			}
		});

		TableViewerColumn expirationDateColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("EXPIRATION_DATE"), 100);
		expirationDateColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof AppliedPolicy && ((AppliedPolicy) element).getExpirationDate() != null) {
					return SWTResourceManager.formatDate(((AppliedPolicy) element).getExpirationDate());
				}
				return "-";
			}
		});
	}

	private void createTableFilterArea(Composite parent) {
		Composite filterContainer = new Composite(parent, SWT.NONE);
		filterContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		filterContainer.setLayout(new GridLayout(2, false));

		// Search label
		Label lblSearch = new Label(filterContainer, SWT.NONE);
		lblSearch.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblSearch.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblSearch.setText(Messages.getString("SEARCH_FILTER"));

		// Filter table rows
		txtSearch = new Text(filterContainer, SWT.BORDER | SWT.SEARCH);
		txtSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtSearch.setToolTipText(Messages.getString("SEARCH_TASK_TOOLTIP"));
		txtSearch.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				tableFilter.setSearchText(txtSearch.getText());
				tableViewer.refresh();
			}
		});
	}

	public class TableFilter extends ViewerFilter {

		private String searchString;

		public void setSearchText(String s) {
			this.searchString = ".*" + s + ".*";
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (searchString == null || searchString.length() == 0) {
				return true;
			}
			AppliedPolicy pol = (AppliedPolicy) element;
			return pol.getCreateDate().toString().matches(searchString)
					|| pol.getLabel().toLowerCase().contains(searchString.toLowerCase())
					|| (pol.getPolicyAsObj() != null
							? pol.getPolicyAsObj().getDescription().toLowerCase().contains(searchString.toLowerCase())
							: false)
					|| SWTResourceManager.formatDate(pol.getCreateDate()).toString().equals(searchString);
		}
	}

	private void createButtonsArea(final Composite parent) {

		// User based report or Group/OU based report
		Composite comp = new Composite(parent, GridData.FILL);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		comp.setLayout(new GridLayout(4, false));

		Label reportType = new Label(comp, SWT.NONE);
		reportType.setText(Messages.getString("QUOTA_REPORT_TYPE"));

		btnUserReport = new Button(comp, SWT.RADIO);
		btnUserReport.setText(Messages.getString("USER_BASED"));
		btnUserReport.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		btnGroupReport = new Button(comp, SWT.RADIO);
		btnGroupReport.setText(Messages.getString("GROUP_BASED"));
		btnGroupReport.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		btnAllReport = new Button(comp, SWT.RADIO);
		btnAllReport.setText(Messages.getString("REPORT_ALL"));
		btnAllReport.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		btnAllReport.setSelection(true);
		//

		buttonComposite = new Composite(parent, GridData.FILL);
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		buttonComposite.setLayout(new GridLayout(3, false));

		btnViewDetail = new Button(buttonComposite, SWT.NONE);
		btnViewDetail.setText(Messages.getString("VIEW_DETAIL"));
		btnViewDetail.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		btnViewDetail.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/report.png"));
		btnViewDetail.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (null == getSelectedPolicy()) {
					Notifier.warning(null, Messages.getString("PLEASE_SELECT_RECORD"));
					return;
				}
				try {
					AppliedPolicy policy = getSelectedPolicy();
					List<Command> commands = PolicyRestUtils.listCommands(policy.getId());
					AppliedPolicyDialog dialog = new AppliedPolicyDialog(parent.getShell(), policy, commands);
					dialog.open();
				} catch (Exception e1) {
					logger.error(e1.getMessage(), e1);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnRefreshAppliedPolicy = new Button(buttonComposite, SWT.NONE);
		btnRefreshAppliedPolicy.setText(Messages.getString("REFRESH"));
		btnRefreshAppliedPolicy.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/refresh.png"));
		btnRefreshAppliedPolicy.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		btnRefreshAppliedPolicy.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	/**
	 * Get agents and populate the table with them.
	 */
	private void populateTable() {
		try {
			List<AppliedPolicy> policies = null;
			policies = PolicyRestUtils.listAppliedPolicies(null, null, null, null,
					ConfigProvider.getInstance().getInt(LiderConstants.CONFIG.APPLIED_POLICIES_MAX_SIZE),
					DiskQuotaConstants.PLUGIN_NAME, getSelectedDnType());
			tableViewer.setInput(policies != null ? policies : new ArrayList<AppliedPolicy>());
			tableViewer.refresh();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			Notifier.error(null, Messages.getString("ERROR_ON_LIST"));
		}
	}

	private DNType getSelectedDnType() {
		if (btnUserReport.getSelection()) {
			return DNType.USER;
		}
		if (btnGroupReport.getSelection()) {
			return DNType.ORGANIZATIONAL_UNIT;
		}
		return DNType.ALL;
	}

	/**
	 * Re-populate table with policies.
	 * 
	 */
	public void refresh() {
		populateTable();
		tableViewer.refresh();
	}

	@Override
	public void setFocus() {
	}

	public AppliedPolicy getSelectedPolicy() {
		return selectedPolicy;
	}

	public void setSelectedPolicy(AppliedPolicy selectedPolicy) {
		this.selectedPolicy = selectedPolicy;
	}

}
