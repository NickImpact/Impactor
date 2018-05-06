package com.nickimpact.impactor.api.configuration.keys;

import com.nickimpact.impactor.api.configuration.ConfigAdapter;
import com.nickimpact.impactor.api.configuration.ConfigKey;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class StaticKey<T> implements ConfigKey<T> {
	private T val;

	@Override
	public T get(ConfigAdapter adapter) {
		return val;
	}

	@Override
	public void set(ConfigAdapter adapter, T value) {
		this.val = value;
	}
}
