package com.nickimpact.impactor;

import com.nickimpact.impactor.api.configuration.AbstractConfig;
import com.nickimpact.impactor.api.configuration.AbstractConfigAdapter;
import com.nickimpact.impactor.api.configuration.ConfigBase;
import com.nickimpact.impactor.commands.PluginCmd;
import com.nickimpact.impactor.api.plugins.ConfigurableSpongePlugin;
import com.nickimpact.impactor.api.plugins.PluginInfo;
import com.nickimpact.impactor.configuration.ConfigKeys;
import lombok.Getter;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import java.nio.file.Path;

/**
 * (Some note will appear here)
 *
 * @author NickImpact (Nick DeGruccio)
 */
@Getter
@Plugin(id = CoreInfo.ID, name = CoreInfo.NAME, version = CoreInfo.VERSION, description = CoreInfo.DESCRIPTION)
public class ImpactorCore extends ConfigurableSpongePlugin {

	@Getter private static ImpactorCore instance;

	@Inject private PluginContainer container;

	@Inject @ConfigDir(sharedRoot = false) private Path configDir;

	private ConfigBase config;

	@Override
	public PluginInfo getPluginInfo() {
		return new CoreInfo();
	}

	@Listener
	public void onPreInit(GamePreInitializationEvent e) {
		instance = this;
		getConsole().ifPresent(console -> console.sendMessage(Text.of(CoreInfo.PREFIX, "Impactor is now initializing...")));
		connect();
	}

	@Listener
	public void onDisconnect(GameStoppingServerEvent e) {
		disconnect();
	}

	@Override
	public void doConnect() {
		getConsole().ifPresent(console -> console.sendMessage(Text.of(CoreInfo.PREFIX, "Loading configuration...")));
		this.config = new AbstractConfig(this, new AbstractConfigAdapter(this), new ConfigKeys(), "assets/core.conf");
		this.config.init();

		getConsole().ifPresent(console -> console.sendMessage(Text.of(CoreInfo.PREFIX, "Registering core commands...")));
		new PluginCmd(this).register(this);
		getConsole().ifPresent(console -> console.sendMessage(Text.of(CoreInfo.PREFIX, "Initialization complete!")));
	}

	@Override
	public void doDisconnect() {

	}
}
