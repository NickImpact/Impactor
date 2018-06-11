package com.nickimpact.impactor.configuration;

import com.google.common.collect.ImmutableMap;
import com.nickimpact.impactor.api.configuration.ConfigKey;
import com.nickimpact.impactor.api.configuration.IConfigKeys;
import com.nickimpact.impactor.api.configuration.keys.StringKey;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

public class MsgConfigKeys implements IConfigKeys {
	public static final ConfigKey<String> PAGES_FIRST = StringKey.of("ui.icons.pages.first-page", "&eFirst Page &7(&a{{page}}&7)");
	public static final ConfigKey<String> PAGES_PREV = StringKey.of("ui.icons.pages.previous-page", "&ePrevious Page &7(&a{{page}}&7)");
	public static final ConfigKey<String> PAGES_CURR = StringKey.of("ui.icons.pages.current-page", "&eCurrent Page &7(&a{{page}}&7)");
	public static final ConfigKey<String> PAGES_NEXT = StringKey.of("ui.icons.pages.next-page", "&eNext Page &7(&a{{page}}&7)");
	public static final ConfigKey<String> PAGES_LAST = StringKey.of("ui.icons.pages.last-page", "&eLast Page &7(&a{{page}}&7)");

	private static Map<String, ConfigKey<?>> KEYS = null;

	@Override
	public synchronized Map<String, ConfigKey<?>> getAllKeys() {
		if(KEYS == null) {
			Map<String, ConfigKey<?>> keys = new LinkedHashMap<>();

			try {
				Field[] values = MsgConfigKeys.class.getFields();
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
