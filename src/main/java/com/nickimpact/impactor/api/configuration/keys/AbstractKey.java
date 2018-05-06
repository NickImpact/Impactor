package com.nickimpact.impactor.api.configuration.keys;

import com.nickimpact.impactor.api.configuration.ConfigAdapter;
import com.nickimpact.impactor.api.configuration.ConfigKey;
import lombok.AllArgsConstructor;

import java.util.function.Function;

/**
 * (Some note will appear here)
 *
 * @author NickImpact (Nick DeGruccio)
 */
@AllArgsConstructor(staticName = "of")
public class AbstractKey<T> implements ConfigKey<T> {
	private Function<ConfigAdapter, T> function;

	@Override
	public T get(ConfigAdapter adapter) {
		return function.apply(adapter);
	}

	@Override
	public void set(ConfigAdapter adapter, T value) {
		throw new IllegalArgumentException("Unsupported operation");
	}
}
