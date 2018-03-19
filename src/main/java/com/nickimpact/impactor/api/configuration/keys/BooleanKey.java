package com.nickimpact.impactor.api.configuration.keys;

import com.nickimpact.impactor.api.configuration.ConfigAdapter;
import com.nickimpact.impactor.api.configuration.ConfigKey;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class BooleanKey implements ConfigKey<Boolean> {
	private final String path;
	private final boolean def;

	@Override
	public Boolean get(ConfigAdapter adapter) {
		return adapter.getBoolean(path, def);
	}
}
