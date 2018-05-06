package com.nickimpact.impactor.api.logger;

import org.spongepowered.api.text.Text;

public interface TextLogger extends Logger {
	Text getPrefix(Prefixes prefix);
}
