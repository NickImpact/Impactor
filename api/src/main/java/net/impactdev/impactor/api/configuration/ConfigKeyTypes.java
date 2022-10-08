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

import com.google.common.collect.ImmutableMap;
import net.impactdev.impactor.api.configuration.keys.BaseConfigKey;
import net.impactdev.impactor.api.configuration.keys.CustomKey;
import net.impactdev.impactor.api.configuration.keys.EnduringKey;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

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

public class ConfigKeyTypes {

	private static final KeyFactory<Boolean> BOOLEAN = ConfigurationAdapter::getBoolean;
	private static final KeyFactory<String> STRING = ConfigurationAdapter::getString;
	private static final KeyFactory<Integer> INTEGER = ConfigurationAdapter::getInteger;
	private static final KeyFactory<Long> LONG = ConfigurationAdapter::getLong;
	private static final KeyFactory<Double> DOUBLE = ConfigurationAdapter::getDouble;
	private static final KeyFactory<List<String>> LIST = ConfigurationAdapter::getStringList;
	private static final KeyFactory<String> LOWERCASE_STRING = (adapter, path, def) -> adapter.getString(path, def).toLowerCase();
	private static final KeyFactory<Map<String, String>> STRING_MAP = ConfigurationAdapter::getStringMap;

	public static BaseConfigKey<Boolean> booleanKey(ConfigPath path, boolean def) {
		return BOOLEAN.createKey(path, def);
	}

	public static BaseConfigKey<String> stringKey(ConfigPath path, String def) {
		return STRING.createKey(path, def);
	}

	public static BaseConfigKey<String> lowercaseStringKey(ConfigPath path, String def) {
		return LOWERCASE_STRING.createKey(path, def);
	}

	public static BaseConfigKey<Integer> intKey(ConfigPath path, int def) {
		return INTEGER.createKey(path, def);
	}

	public static BaseConfigKey<Long> longKey(ConfigPath path, long def) {
		return LONG.createKey(path, def);
	}

	public static BaseConfigKey<Double> doubleKey(ConfigPath path, double def) {
		return DOUBLE.createKey(path, def);
	}

	public static BaseConfigKey<List<String>> listKey(ConfigPath path, List<String> def) {
		return LIST.createKey(path, def);
	}

	public static BaseConfigKey<Map<String, String>> mapKey(ConfigPath path, ImmutableMap<String, String> def) {
		return STRING_MAP.createKey(path, def);
	}

	public static <T> CustomKey<T> customKey(Function<ConfigurationAdapter, T> function) {
		return new CustomKey<>(function);
	}

	public static <T> EnduringKey<T> enduringKey(ConfigKey<T> delegate) {
		return new EnduringKey<>(delegate);
	}
}
