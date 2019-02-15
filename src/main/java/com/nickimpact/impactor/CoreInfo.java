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
	public static final String VERSION = "1.4.4";
	public static final String DESCRIPTION = "A core system to link plugins to one similar API service";

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
}
