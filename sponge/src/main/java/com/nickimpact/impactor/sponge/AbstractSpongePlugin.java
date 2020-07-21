package com.nickimpact.impactor.sponge;

import com.nickimpact.impactor.api.plugin.AbstractPlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

public abstract class AbstractSpongePlugin extends AbstractPlugin {

	@Override
	public void handleDisconnect() {
		Sponge.getCommandManager().getOwnedBy(this).forEach(Sponge.getCommandManager()::removeMapping);
		Sponge.getEventManager().unregisterPluginListeners(this);
		Sponge.getScheduler().getScheduledTasks(this).forEach(Task::cancel);
		this.connected = false;
	}
}
