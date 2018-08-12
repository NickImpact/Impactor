package com.nickimpact.impactor.api.logger;

import com.nickimpact.impactor.api.plugins.SpongePlugin;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.function.Supplier;

public interface Logger {

	SpongePlugin getPlugin();

	void info(String message);

	void info(Text message);

	void warn(String message);

	void warn(Text message);

	void error(String message);

	void error(Text message);

	void debug(String message);

	void debug(Text message);

	void send(Prefixes prefix, Text message);

	default void newline() {
		this.send(Prefixes.NONE, Text.EMPTY);
	}

	default void send(Prefixes prefix, List<Text> messages) {
		for(Text text : messages) {
			this.send(prefix, text);
		}
	}

	default void send(Prefixes prefix, Supplier<Text> supplier) {
		this.send(prefix, supplier.get());
	}

	enum Prefixes {
		INFO,
		WARN,
		ERROR,
		DEBUG,
		NONE,
	}
}
