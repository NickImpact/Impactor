package com.nickimpact.impactor.api.plugin;

public abstract class AbstractPlugin implements ImpactorPlugin {

	protected boolean connected = false;

	public AbstractPlugin() {
		this.connect();
	}

	@Override
	public boolean isConnected() {
		return this.connected;
	}

	@Override
	public void setConnected() {
		this.connected = true;
	}
}
