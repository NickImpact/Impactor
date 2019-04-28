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

package com.nickimpact.impactor.api.configuration;

import com.google.common.collect.ImmutableMap;
import com.nickimpact.impactor.api.configuration.keys.BaseConfigKey;
import com.nickimpact.impactor.api.configuration.keys.CustomKey;
import com.nickimpact.impactor.api.configuration.keys.EnduringKey;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ConfigKeyTypes {

	private static final KeyFactory<Boolean> BOOLEAN = ConfigurationAdapter::getBoolean;
	private static final KeyFactory<String> STRING = ConfigurationAdapter::getString;
	private static final KeyFactory<Integer> INTEGER = ConfigurationAdapter::getInteger;
	private static final KeyFactory<Double> DOUBLE = ConfigurationAdapter::getDouble;
	private static final KeyFactory<List<String>> LIST = ConfigurationAdapter::getStringList;
	private static final KeyFactory<String> LOWERCASE_STRING = (adapter, path, def) -> adapter.getString(path, def).toLowerCase();
	private static final KeyFactory<Map<String, String>> STRING_MAP = ConfigurationAdapter::getStringMap;

	public static BaseConfigKey<Boolean> booleanKey(String path, boolean def) {
		return BOOLEAN.createKey(path, def);
	}

	public static BaseConfigKey<String> stringKey(String path, String def) {
		return STRING.createKey(path, def);
	}

	public static BaseConfigKey<String> lowercaseStringKey(String path, String def) {
		return LOWERCASE_STRING.createKey(path, def);
	}

	public static BaseConfigKey<Integer> intKey(String path, int def) {
		return INTEGER.createKey(path, def);
	}

	public static BaseConfigKey<Double> doubleKey(String path, double def) {
		return DOUBLE.createKey(path, def);
	}

	public static BaseConfigKey<List<String>> listKey(String path, List<String> def) {
		return LIST.createKey(path, def);
	}

	public static BaseConfigKey<Map<String, String>> mapKey(String path, ImmutableMap<String, String> def) {
		return STRING_MAP.createKey(path, def);
	}

	public static <T> CustomKey<T> customKey(Function<ConfigurationAdapter, T> function) {
		return new CustomKey<>(function);
	}

	public static <T> EnduringKey<T> enduringKey(ConfigKey<T> delegate) {
		return new EnduringKey<>(delegate);
	}
}
