package com.nickimpact.impactor;

import com.nickimpact.impactor.api.plugins.PluginInfo;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * (Some note will appear here)
 *
 * @author NickImpact (Nick DeGruccio)
 */
public class CoreInfo implements PluginInfo {

	public static final String ID = "impactor";
	public static final String NAME = "Impactor API";
	public static final String VERSION = "DEV - 0.0.1.1";
	public static final String DESCRIPTION = "A core system to link plugins to one similar API service";

	// Text to Console output prefixes
	public static final Text PREFIX = Text.of(
			TextColors.YELLOW, "Impactor ", TextColors.DARK_GRAY, "\u00bb ", TextColors.DARK_AQUA
	);

	public static final Text ERROR = Text.of(
			TextColors.YELLOW, "Impactor ", TextColors.DARK_GRAY, "(", TextColors.RED, "Error", TextColors.DARK_GRAY, ") ",
			TextColors.DARK_RED
	);

	public static final Text WARN = Text.of(
			TextColors.YELLOW, "Impactor ", TextColors.DARK_GRAY, "(", TextColors.GOLD, "Warning", TextColors.DARK_GRAY, ") ",
			TextColors.WHITE
	);

	public static final Text DEBUG = Text.of(
			TextColors.YELLOW, "Impactor ", TextColors.DARK_GRAY, "(", TextColors.AQUA, "Debug", TextColors.DARK_GRAY, ") ",
			TextColors.WHITE
	);

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getVersion() {
		return VERSION;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	@Override
	public Text prefix() {
		return PREFIX;
	}

	@Override
	public Text error() {
		return ERROR;
	}

	@Override
	public Text warning() {
		return WARN;
	}

	@Override
	public Text debug() {
		return DEBUG;
	}
}
