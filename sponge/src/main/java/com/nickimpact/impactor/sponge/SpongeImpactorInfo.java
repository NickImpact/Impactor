package com.nickimpact.impactor.sponge;

import com.nickimpact.impactor.api.plugin.PluginInfo;

public class SpongeImpactorInfo implements PluginInfo {
	@Override
	public String getID() {
		return "impactor";
	}

	@Override
	public String getName() {
		return "Impactor";
	}

	@Override
	public String getVersion() {
		return "2.0.0";
	}

	@Override
	public String getDescription() {
		return "A universal API built to handle multiple behind-the-scenes tasks";
	}
}
