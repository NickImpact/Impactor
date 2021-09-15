package net.impactdev.impactor.bungee.logging;

import net.impactdev.impactor.api.logging.Logger;

import java.util.List;
import java.util.function.Function;

public class BungeeLogger implements Logger {

	private final java.util.logging.Logger delegate;
	private final Function<String, String> preprocessor = in -> in.replaceAll("[&]", "\u00a7");

	public BungeeLogger(java.util.logging.Logger delegate) {
		this.delegate = delegate;
	}

	@Override
	public void noTag(String message) {
		this.info(message);
	}

	@Override
	public void noTag(List<String> message) {
		this.info(message);
	}

	@Override
	public void info(String message) {
		this.delegate.info(this.preprocessor.apply(message));
	}

	@Override
	public void info(List<String> message) {
		for(String s : message) {
			this.info(s);
		}
	}

	@Override
	public void warn(String message) {
		this.delegate.warning(this.preprocessor.apply(message));
	}

	@Override
	public void warn(List<String> message) {
		for(String s : message) {
			this.warn(s);
		}
	}

	@Override
	public void error(String message) {
		this.delegate.severe(this.preprocessor.apply(message));
	}

	@Override
	public void error(List<String> message) {
		for(String s : message) {
			this.error(s);
		}
	}

	@Override
	public void debug(String message) {
		this.delegate.info(this.preprocessor.apply("&3DEBUG &7\u00bb " + message));
	}

	@Override
	public void debug(List<String> message) {
		for(String s : message) {
			this.debug(s);
		}
	}

}
