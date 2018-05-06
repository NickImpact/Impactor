package com.nickimpact.impactor.api.configuration.keys;

import com.google.common.collect.ImmutableMap;
import com.nickimpact.impactor.api.configuration.ConfigAdapter;
import com.nickimpact.impactor.api.configuration.ConfigKey;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor(staticName = "of")
public class MapKey implements ConfigKey<Map<String, String>> {
	private final String path;

	@Override
	public Map<String, String> get(ConfigAdapter adapter) {
		return ImmutableMap.copyOf(adapter.getMap(path, ImmutableMap.of()));
	}

	@Override
	public void set(ConfigAdapter adapter, Map<String, String> value) {
		throw new IllegalStateException("Unsupported operation");
	}
}
