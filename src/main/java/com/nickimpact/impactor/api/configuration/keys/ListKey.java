package com.nickimpact.impactor.api.configuration.keys;

import com.google.common.collect.ImmutableList;
import com.nickimpact.impactor.api.configuration.ConfigAdapter;
import com.nickimpact.impactor.api.configuration.ConfigKey;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor(staticName = "of")
public class ListKey implements ConfigKey<List<String>> {
	private final String path;
	private List<String> def;

	@Override
	public List<String> get(ConfigAdapter adapter) {
		return ImmutableList.copyOf(adapter.getList(path, def));
	}

	@Override
	public void set(ConfigAdapter adapter, List<String> value) {
		this.def = value;
		adapter.set(path, value);
	}
}
