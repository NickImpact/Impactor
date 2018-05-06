package com.nickimpact.impactor.api.configuration.keys;

import com.nickimpact.impactor.api.configuration.ConfigAdapter;
import com.nickimpact.impactor.api.configuration.ConfigKey;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class BooleanKey implements ConfigKey<Boolean> {
	private final String path;
	private boolean def;

	@Override
	public Boolean get(ConfigAdapter adapter) {
		return adapter.getBoolean(path, def);
	}

	@Override
	public void set(ConfigAdapter adapter, Boolean value) {
		this.def = value;
		adapter.set(path, value);
	}
}
