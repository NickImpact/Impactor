package com.nickimpact.impactor.api.configuration.keys;

import com.nickimpact.impactor.api.configuration.ConfigAdapter;
import com.nickimpact.impactor.api.configuration.ConfigKey;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class DoubleKey implements ConfigKey<Double> {
	private final String path;
	private double def;

	@Override
	public Double get(ConfigAdapter adapter) {
		return adapter.getDouble(path, def);
	}

	@Override
	public void set(ConfigAdapter adapter, Double value) {
		this.def = value;
		adapter.set(path, value);
	}
}
