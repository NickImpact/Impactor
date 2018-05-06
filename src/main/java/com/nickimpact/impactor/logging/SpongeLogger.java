package com.nickimpact.impactor.logging;

import com.nickimpact.impactor.api.logger.StringLogger;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.spongepowered.api.text.Text;

import java.util.regex.Pattern;

@RequiredArgsConstructor
public class SpongeLogger implements StringLogger {

	private final SpongePlugin plugin;
	private final Logger logger;

	@Override
	public SpongePlugin getPlugin() {
		return this.plugin;
	}

	@Override
	public void info(String message) {
		this.logger.info(message);
	}

	@Override
	public void info(Text message) {
		this.info(stripColors(message));
	}

	@Override
	public void warn(String message) {
		this.logger.warn(message);
	}

	@Override
	public void warn(Text message) {
		this.warn(stripColors(message));
	}

	@Override
	public void error(String message) {
		this.logger.error(message);
	}

	@Override
	public void error(Text message) {
		this.error(stripColors(message));
	}

	@Override
	public void debug(String message) {
		this.logger.debug(message);
	}

	@Override
	public void debug(Text message) {
		this.debug(stripColors(message));
	}

	@Override
	public void send(Prefixes prefix, Text message) {
		if(prefix == Prefixes.INFO) {
			this.info(message);
		} else if(prefix == Prefixes.WARN) {
			this.warn(message);
		} else if(prefix == Prefixes.ERROR) {
			this.error(message);
		} else {
			this.debug(message);
		}
	}

	@Override
	public String stripColors(Text text) {
		String msg = text.toPlain();
		Pattern code = Pattern.compile("[&][a-fl-o0-9]", Pattern.CASE_INSENSITIVE);
		return msg.replaceAll(code.pattern(), "");
	}
}
