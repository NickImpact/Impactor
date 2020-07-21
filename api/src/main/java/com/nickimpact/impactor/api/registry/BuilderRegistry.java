package com.nickimpact.impactor.api.registry;

import com.google.common.collect.Maps;
import com.nickimpact.impactor.api.building.Builder;

import java.util.Map;

public class BuilderRegistry {

	private Map<Class<?>, Class<?>> builders = Maps.newHashMap();

	public <V, T extends Builder<V>, U extends Builder<V>> void register(Class<T> clazz, Class<U> builder) {
		this.builders.put(clazz, builder);
	}

	public <V, T extends Builder<V>, U extends Builder<V>> U createFor(Class<T> clazz) {
		try {
			return (U) this.builders.get(clazz).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
