package tr.org.liderahenk.disk.quota.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import tr.org.liderahenk.disk.quota.constants.DiskQuotaConstants;
import tr.org.liderahenk.disk.quota.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.editorinput.DefaultEditorInput;

public class DiskQuotaPolicyListHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IWorkbenchPage page = window.getActivePage();
		try {
			page.openEditor(new DefaultEditorInput(Messages.getString("DISK_QUOTA_POLICY_LIST")),
					DiskQuotaConstants.EDITORS.DISK_QUOTA_POLICY_LIST_EDITOR);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}

}
