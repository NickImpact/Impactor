package com.nickimpact.impactor.api.plugins;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public interface PluginInfo {

	String getID();

	String getName();

	String getVersion();

	String getDescription();

	default Text prefix() {
		return Text.of(TextColors.YELLOW, getName(), " ", TextColors.GRAY, "\u00bb ", TextColors.DARK_AQUA);
	}

	default Text error() {
		return Text.of(TextColors.RED, getName(), " ", TextColors.GRAY, "(", TextColors.RED, "Error", TextColors.GRAY, ") ", TextColors.DARK_RED);
	}

	default Text warning() {
		return Text.of(TextColors.YELLOW, getName(), " ", TextColors.GRAY, "(", TextColors.RED, "Warning", TextColors.GRAY, ") ", TextColors.YELLOW);
	}

	default Text debug() {
		return Text.of(TextColors.YELLOW, getName(), " ", TextColors.GRAY, "(", TextColors.AQUA, "Debug", TextColors.GRAY, ") ", TextColors.DARK_AQUA);
	}
}
