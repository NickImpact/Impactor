package com.nickimpact.impactor.api.plugins;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.CoreInfo;
import com.nickimpact.impactor.ImpactorCore;
import com.nickimpact.impactor.api.logger.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

/**
 * This class represents a plugin built on top of the Sponge system, and serves
 * as a key identifier for the Core itself. We will use this base class as
 * a check against any method requiring it, as well as let it help serve as a
 * way to fully identify a plugin registered on the server through the core.
 *
 * @author NickImpact
 */
public abstract class SpongePlugin {

	public abstract PluginInfo getPluginInfo();

	/**
	 * States how we are to run connection for a plugin
	 */
	public abstract void doConnect();

	/**
	 * States how we are to handle disconnection for a plugin
	 */
	public abstract void doDisconnect();

	/**
	 * States whether or not the plugin has been connected
	 */
	private boolean connected = true;

	/**
	 * Checks whether the plugin has been registered and connected
	 *
	 * @return <code>true</code> if connected, <code>false</code> otherwise
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Attempts to connect the plugin to the core itself. This method is typically
	 * called via any starting phase in Sponge.
	 */
	public void connect() {
		if (!PluginRegistry.isLoaded(getPluginInfo().getID())) {
			connected = true;
			PluginRegistry.register(this);
			init();
		} else {
			if (!isConnected()) {
				connected = true;
				init();
			}
		}
	}

	private void init() {
		try {
			doConnect();
		} catch (Exception e) {
			ImpactorCore.getInstance().getLogger().send(Logger.Prefixes.ERROR, Lists.newArrayList(
						Text.of("======== Plugin Failure ========"),
						Text.of(TextColors.RED, "The plugin with ID ", TextColors.YELLOW, getPluginInfo().getID()),
						Text.of(TextColors.RED, "encountered an error..."),
						Text.of("================================")
			));

			e.printStackTrace();
			connected = false;
			if (PluginRegistry.getPlugin(this).isPresent()) {
				PluginRegistry.unregister(this);
			}
			return;
		}
		ImpactorCore.getInstance().getLogger().send(Logger.Prefixes.INFO, () -> {
			if (this instanceof ImpactorCore) {
				return Text.of("Registered core service plugin: ", TextColors.GREEN, getPluginInfo().getName());
			} else {
				return Text.of("Registered plugin: ", TextColors.GREEN, getPluginInfo().getName());
			}
		});
	}

	public void disconnect() {
		doDisconnect();
		Sponge.getCommandManager().getOwnedBy(this).forEach(Sponge.getCommandManager()::removeMapping);
		Sponge.getEventManager().unregisterPluginListeners(this);
		Sponge.getScheduler().getScheduledTasks(this).forEach(Task::cancel);
		connected = false;
		ImpactorCore.getInstance().getLogger().send(Logger.Prefixes.INFO, () -> {
			if (this instanceof ImpactorCore) {
				return Text.of("Disabled core service plugin: ", TextColors.GREEN, getPluginInfo().getName());
			} else {
				return Text.of("Disabled plugin: ", TextColors.GREEN, getPluginInfo().getName());
			}
		});
	}

	public void reload() {
		disconnect();
		connect();
		ImpactorCore.getInstance().getLogger().info(Text.of("Reloaded plugin: ", TextColors.YELLOW, getPluginInfo().getName()));
	}

	public abstract Logger getLogger();
}
