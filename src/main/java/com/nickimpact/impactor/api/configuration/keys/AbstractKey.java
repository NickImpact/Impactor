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
	private final Function<ConfigAdapter, T> function;

	@Override
	public T get(ConfigAdapter adapter) {
		return function.apply(adapter);
	}
}
