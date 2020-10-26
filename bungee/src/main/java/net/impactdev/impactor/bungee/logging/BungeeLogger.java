package net.impactdev.impactor.bungee.logging;

import net.impactdev.impactor.api.logging.Logger;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BungeeLogger implements Logger {

	private final java.util.logging.Logger delegate;

	@Override
	public void noTag(String message) {

	}

	@Override
	public void noTag(List<String> message) {

	}

	@Override
	public void info(String message) {
		delegate.info(message);
	}

	@Override
	public void info(List<String> message) {

	}

	@Override
	public void warn(String message) {

	}

	@Override
	public void warn(List<String> message) {

	}

	@Override
	public void error(String message) {

	}

	@Override
	public void error(List<String> message) {

	}

	@Override
	public void debug(String message) {

	}

	@Override
	public void debug(List<String> message) {

	}
}
