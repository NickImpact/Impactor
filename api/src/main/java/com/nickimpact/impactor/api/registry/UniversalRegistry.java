package com.nickimpact.impactor.api.registry;

import java.lang.reflect.Constructor;
import java.util.Optional;

public class UniversalRegistry {

	public <T, U> Optional<T> createFor(Class<T> clazz, U value) {
		try {
			Constructor<T> cons = clazz.getConstructor(value.getClass());
			return Optional.of(cons.newInstance(value));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Optional.empty();
	}
}
