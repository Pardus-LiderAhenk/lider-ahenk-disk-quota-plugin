package tr.org.liderahenk.disk.quota.commands;

import java.util.ArrayList;

import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;
import tr.org.liderahenk.disk.quota.plugininfo.PluginInfoImpl;

/**
 * 
 * @author <a href="mailto:mine.dogan@agem.com.tr">Mine Dogan</a>
 *
 */
public class DiskQuotaCommand implements ICommand {

	private ICommandResultFactory resultFactory;
	private PluginInfoImpl pluginInfo;
	
	@Override
	public ICommandResult execute(ICommandContext context) {
		ICommandResult commandResult = resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this);
		return commandResult;
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this, null);
	}

	@Override
	public String getCommandId() {
		return "CHANGE-QUOTA";
	}

	@Override
	public Boolean executeOnAgent() {
		return true;
	}

	public void setResultFactory(ICommandResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}
	
	public void setPluginInfo(PluginInfoImpl pluginInfo) {
		this.pluginInfo = pluginInfo;
	}

	@Override
	public String getPluginName() {
		return pluginInfo.getPluginName();
	}

	@Override
	public String getPluginVersion() {
		return pluginInfo.getPluginVersion();
	}
}
