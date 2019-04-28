package com.nickimpact.impactor.spigot.configuration;

import com.nickimpact.impactor.api.configuration.Config;
import com.nickimpact.impactor.api.configuration.ConfigKey;
import com.nickimpact.impactor.api.configuration.ConfigKeyHolder;
import com.nickimpact.impactor.api.configuration.ConfigurationAdapter;
import com.nickimpact.impactor.api.configuration.keys.EnduringKey;

public class SpigotConfig implements Config {

	/**
	 * The configurations loaded values.
	 *
	 * <p>The value corresponding to each key is stored at the index defined
	 * by {@link ConfigKey#ordinal()}.</p>
	 */
	private Object[] values = null;

	private final ConfigurationAdapter adapter;
	private final ConfigKeyHolder holder;

	public SpigotConfig(ConfigurationAdapter adapter, ConfigKeyHolder holder) {
		this.adapter = adapter;
		this.holder = holder;
		load();
	}

	@Override
	public void reload() {
		this.adapter.reload();
		load();
	}

	@Override
	public synchronized void load() {
		// if this is a reload operation
		boolean reload = true;

		// if values are null, must be loading for the first time
		if (this.values == null) {
			this.values = new Object[holder.getSize()];
			reload = false;
		}

		for (ConfigKey<?> key : holder.getKeys().values()) {
			// don't reload enduring keys.
			if (reload && key instanceof EnduringKey) {
				continue;
			}

			// load the value for the key
			Object value = key.get(this.adapter);
			this.values[key.ordinal()] = value;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(ConfigKey<T> key) {
		return (T) this.values[key.ordinal()];
	}
}
