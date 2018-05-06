package com.nickimpact.impactor.logging;

import com.nickimpact.impactor.api.logger.StringLogger;
import com.nickimpact.impactor.api.logger.TextLogger;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

public class ConsoleLogger implements TextLogger {

	private SpongePlugin plugin;
	private ConsoleSource console;
	private StringLogger fallback;

	public ConsoleLogger(SpongePlugin plugin, StringLogger fallback) {
		this.plugin = plugin;
		if(Sponge.isServerAvailable()) {
			this.console = Sponge.getServer().getConsole();
		}
		this.fallback = fallback;
	}

	@Override
	public SpongePlugin getPlugin() {
		return this.plugin;
	}

	@Override
	public void info(String message) {
		this.info(TextSerializers.FORMATTING_CODE.deserialize(message));
	}

	@Override
	public void info(Text message) {
		this.send(Prefixes.INFO, message);
	}

	@Override
	public void warn(String message) {
		this.warn(TextSerializers.FORMATTING_CODE.deserialize(message));
	}

	@Override
	public void warn(Text message) {
		this.send(Prefixes.WARN, message);
	}

	@Override
	public void error(String message) {
		this.error(TextSerializers.FORMATTING_CODE.deserialize(message));
	}

	@Override
	public void error(Text message) {
		this.send(Prefixes.ERROR, message);
	}

	@Override
	public void debug(String message) {
		this.debug(TextSerializers.FORMATTING_CODE.deserialize(message));
	}

	@Override
	public void debug(Text message) {
		this.send(Prefixes.DEBUG, message);
	}

	@Override
	public void send(Prefixes prefix, Text message) {
		if(console != null) {
			console.sendMessage(Text.of(getPrefix(prefix), message));
		} else {
			fallback.send(prefix, message);
		}
	}

	@Override
	public Text getPrefix(Prefixes prefix) {
		switch(prefix) {
			case INFO:
				return plugin.getPluginInfo().prefix();
			case WARN:
				return plugin.getPluginInfo().warning();
			case ERROR:
				return plugin.getPluginInfo().error();
			case DEBUG:
				return plugin.getPluginInfo().debug();
			case NONE:
				return Text.EMPTY;
			default:
				return plugin.getPluginInfo().prefix();
		}
	}
}
