package tr.org.liderahenk.disk.quota.handlers;

import java.util.Set;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.disk.quota.dialogs.DiskQuotaTaskDialog;
import tr.org.liderahenk.liderconsole.core.handlers.MultipleSelectionHandler;

public class DiskQuotaTaskHandler extends MultipleSelectionHandler {

	@Override
	public void executeWithDNSet(Set<String> dnSet) {
		DiskQuotaTaskDialog dialog = new DiskQuotaTaskDialog(Display.getDefault().getActiveShell(), dnSet);
		dialog.create();
		dialog.openWithEventBroker();
	}

}
