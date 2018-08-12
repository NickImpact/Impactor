package com.nickimpact.impactor.api.services.plan.implementations;

import com.djrapitops.plan.data.plugin.PluginData;
import com.nickimpact.impactor.api.services.plan.implementations.buycraft.Buycraft;

public enum ProvidedDataImplementations {

	Buycraft(new Buycraft());

	private PluginData implementation;

	private ProvidedDataImplementations(PluginData implementation) {
		this.implementation = implementation;
	}

	public PluginData getImplementation() {
		return this.implementation;
	}
}
