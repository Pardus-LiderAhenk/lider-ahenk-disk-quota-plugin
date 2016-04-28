package tr.org.liderahenk.disk.quota.plugininfo;

import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;

public class PluginInfoImpl implements IPluginInfo {
	
	@Override
	public String getPluginName() {
		return "disk-quota";
	}

	@Override
	public String getPluginVersion() {
		return "1.0.0";
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public boolean isMachineOriented() {
		return true;
	}

	@Override
	public boolean isUserOriented() {
		return true;
	}

	@Override
	public boolean isPolicyPlugin() {
		return true;
	}
	
	@Override
	public boolean isxBased() {
		return false;
	}
	
}