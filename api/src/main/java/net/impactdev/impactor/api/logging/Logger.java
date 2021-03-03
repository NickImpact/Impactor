package net.impactdev.impactor.api.logging;

import java.util.List;

public interface Logger {

	void noTag(String message);

	void noTag(List<String> message);

	void info(String message);

	void info(List<String> message);

	void warn(String message);

	void warn(List<String> message);

	void error(String message);

	void error(List<String> message);

	void debug(String message);

	void debug(List<String> message);

}
