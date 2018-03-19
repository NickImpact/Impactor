package com.nickimpact.impactor.api.plugins;

import org.spongepowered.api.text.Text;

public interface PluginInfo {

	String getID();

	String getName();

	String getVersion();

	String getDescription();

	Text prefix();

	Text error();

	Text warning();

	Text debug();
}
