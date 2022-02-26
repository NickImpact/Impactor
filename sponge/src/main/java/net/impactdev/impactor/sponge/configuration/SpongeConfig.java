/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2022 NickImpact
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package net.impactdev.impactor.sponge.configuration;

import net.impactdev.impactor.api.configuration.Config;
import net.impactdev.impactor.api.configuration.ConfigKey;
import net.impactdev.impactor.api.configuration.ConfigurationAdapter;
import net.impactdev.impactor.api.configuration.holder.KeyLoader;
import net.impactdev.impactor.api.configuration.keys.EnduringKey;

public class SpongeConfig implements Config {

	/**
	 * The configurations loaded values.
	 *
	 * <p>The value corresponding to each key is stored at the index defined
	 * by {@link ConfigKey#ordinal()}.</p>
	 */
	private Object[] values = null;

	private final ConfigurationAdapter adapter;
	private final KeyLoader loader;

	public SpongeConfig(ConfigurationAdapter adapter, Object provider) {
		this.adapter = adapter;
		this.loader = new KeyLoader(provider);
		load();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(ConfigKey<T> key) {
		return (T) this.values[key.ordinal()];
	}

	@Override
	public synchronized void load() {
		// if this is a reload operation
		boolean reload = true;

		// if values are null, must be loading for the first time
		if (this.values == null) {
			this.values = new Object[this.loader.size()];
			reload = false;
		}

		for (ConfigKey<?> key : this.loader.keys()) {
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
	public void reload() {
		this.adapter.reload();
		load();
	}
}
