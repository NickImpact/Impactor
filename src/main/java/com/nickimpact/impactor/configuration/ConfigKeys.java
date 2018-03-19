package com.nickimpact.impactor.configuration;

import com.google.common.collect.ImmutableMap;
import com.nickimpact.impactor.api.configuration.ConfigKey;
import com.nickimpact.impactor.api.configuration.keys.BooleanKey;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * (Some note will appear here)
 *
 * @author NickImpact (Nick DeGruccio)
 */
public class ConfigKeys {

	public static final ConfigKey<Boolean> DEBUG_ENABLED = BooleanKey.of("debug.enabled", false);
	public static final ConfigKey<Boolean> DEBUG_COMMANDS = BooleanKey.of("debug.commands", false);
	public static final ConfigKey<Boolean> DEBUG_INVENTORY = BooleanKey.of("debug.inventory", false);

	private static Map<String, ConfigKey<?>> KEYS = null;
	public static synchronized Map<String, ConfigKey<?>> getAllKeys() {
		if(KEYS == null) {
			Map<String, ConfigKey<?>> keys = new LinkedHashMap<>();

			try {
				Field[] values = ConfigKeys.class.getFields();
				for(Field f : values) {
					if(!Modifier.isStatic(f.getModifiers()))
						continue;

					Object val = f.get(null);
					if(val instanceof ConfigKey<?>)
						keys.put(f.getName(), (ConfigKey<?>) val);
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			KEYS = ImmutableMap.copyOf(keys);
		}

		return KEYS;
	}
}
