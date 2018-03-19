package com.nickimpact.impactor.api.configuration.keys;

import com.nickimpact.impactor.api.configuration.ConfigAdapter;
import com.nickimpact.impactor.api.configuration.ConfigKey;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class LongKey implements ConfigKey<Long> {
	private final String path;
	private final long def;

	@Override
	public Long get(ConfigAdapter adapter) {
		return adapter.getLong(path, def);
	}
}
