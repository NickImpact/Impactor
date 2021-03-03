package net.impactdev.impactor.spigot.configuration;

import com.google.common.collect.ImmutableMap;
import net.impactdev.impactor.api.configuration.ConfigKey;
import net.impactdev.impactor.api.configuration.keys.BaseConfigKey;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigKeys {

	private static final Map<String, ConfigKey<?>> KEYS;
	private static final int SIZE;

	static {
		Map<String, ConfigKey<?>> keys = new LinkedHashMap<>();
		Field[] values = ConfigKeys.class.getFields();
		int i = 0;

		for (Field f : values) {
			// ignore non-static fields
			if (!Modifier.isStatic(f.getModifiers())) {
				continue;
			}

			// ignore fields that aren't configkeys
			if (!ConfigKey.class.equals(f.getType())) {
				continue;
			}

			try {
				// get the key instance
				BaseConfigKey<?> key = (BaseConfigKey<?>) f.get(null);
				// set the ordinal value of the key.
				key.ordinal = i++;
				// add the key to the return map
				keys.put(f.getName(), key);
			} catch (Exception e) {
				throw new RuntimeException("Exception processing field: " + f, e);
			}
		}

		KEYS = ImmutableMap.copyOf(keys);
		SIZE = i;
	}

	/**
	 * Gets a map of the keys defined in this class.
	 *
	 * <p>The string key in the map is the {@link Field#getName() field name}
	 * corresponding to each key.</p>
	 *
	 * @return the defined keys
	 */
	public static Map<String, ConfigKey<?>> getKeys() {
		return KEYS;
	}

	/**
	 * Gets the number of defined keys.
	 *
	 * @return how many keys are defined in this class
	 */
	public static int size() {
		return SIZE;
	}

}
