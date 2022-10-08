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

package net.impactdev.impactor.api.configuration;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.builders.Builder;
import net.impactdev.impactor.api.configuration.keys.EnduringKey;
import net.impactdev.impactor.api.configuration.loader.KeyProvider;

import java.nio.file.Path;

/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

public interface Config {

	/**
	 * Fetches a value from the config using the following key.
	 *
	 * @param key The key used to reference the configurable value
	 * @param <T> The type this key will resolve to when evaluated
	 * @return The evaluated configuration
	 * @throws java.util.NoSuchElementException If the key is not present in the config
	 */
	<T> T get(ConfigKey<T> key);

	/**
	 * Runs the actual loading of configurable values. In retrospect, this simply caches the
	 * config with the current values associated with the config.
	 */
	void load();

	/**
	 * Refreshes the currently cached values with the values now set in the config file. This
	 * only reloads keys not wrapped with {@link EnduringKey EnduringKeys}. Enduring keys
	 * are keys that should never be reloaded, and will be effectively final at the time
	 * of server launch/first configuration load.
	 */
	void reload();

	static ConfigBuilder builder() {
		return Impactor.instance().builders().provide(ConfigBuilder.class);
	}

	interface ConfigBuilder extends Builder<Config> {

		/**
		 * The class that will provide the config keys. This class must be marked
		 * with {@link KeyProvider} for the config to accept the class as a config
		 * key provider. Classes not annotated with this annotation will enforce
		 * an exception at invocation.
		 *
		 * @param provider The class providing config keys
		 * @return This builder, reflecting the given provider
		 * @throws IllegalArgumentException If the class provided is not annotated with
		 * {@link KeyProvider}
		 */
		ConfigBuilder providers(Class<?> provider, Class<?>... children);

		/**
		 * Indicates the path that the config lives at.
		 *
		 * @param path The path to the config file
		 * @return This builder, reflecting the given path
		 */
		ConfigBuilder path(Path path);

		/**
		 * Indicates whether the config will write values not present in the actual file of the config
		 * if they are not present. By default, this will be turned off due to the issue of comments being
		 * whipped from the file during any write action. This issue should be resolved in later versions,
		 * and as such, this option is considered soft-deprecated pending a fix.
		 *
		 * @param supply <code>true</code> to write values if not present, <code>false</code> otherwise
		 * @return This builder, reflecting the supplier state
		 */
		ConfigBuilder supply(boolean supply);

	}
}
