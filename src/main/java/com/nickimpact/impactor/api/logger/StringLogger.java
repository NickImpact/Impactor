package com.nickimpact.impactor.api.logger;

import org.spongepowered.api.text.Text;

public interface StringLogger extends Logger {
	String stripColors(Text text);
}
