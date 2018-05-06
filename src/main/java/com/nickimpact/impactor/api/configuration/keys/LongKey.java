package com.nickimpact.impactor.api.configuration.keys;

import com.nickimpact.impactor.api.configuration.ConfigAdapter;
import com.nickimpact.impactor.api.configuration.ConfigKey;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class LongKey implements ConfigKey<Long> {
	private final String path;
	private long def;

	@Override
	public Long get(ConfigAdapter adapter) {
		return adapter.getLong(path, def);
	}

	@Override
	public void set(ConfigAdapter adapter, Long value) {
		this.def = value;
		adapter.set(path, value);
	}
}
