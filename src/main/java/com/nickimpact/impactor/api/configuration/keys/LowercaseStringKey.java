package com.nickimpact.impactor.api.configuration.keys;

import com.nickimpact.impactor.api.configuration.ConfigAdapter;
import com.nickimpact.impactor.api.configuration.ConfigKey;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class LowercaseStringKey implements ConfigKey<String> {
	private final String path;
	private final String def;

	@Override
	public String get(ConfigAdapter adapter) {
		return adapter.getString(path, def).toLowerCase();
	}
}
