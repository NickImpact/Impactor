package com.nickimpact.impactor.api.configuration.keys;

import com.nickimpact.impactor.api.configuration.ConfigAdapter;
import com.nickimpact.impactor.api.configuration.ConfigKey;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class StaticKey<T> implements ConfigKey<T> {
	private final T val;

	@Override
	public T get(ConfigAdapter adapter) {
		return val;
	}
}
