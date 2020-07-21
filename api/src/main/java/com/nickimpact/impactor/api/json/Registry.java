package com.nickimpact.impactor.api.json;

import com.google.common.collect.Maps;
import com.nickimpact.impactor.api.plugin.ImpactorPlugin;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
import java.util.Map;

@RequiredArgsConstructor
public class Registry<E> {

	private final ImpactorPlugin plugin;
	private final Map<String, Class<? extends E>> typings = Maps.newHashMap();

	public void register(Class<? extends E> clazz) throws Exception {
		if(!clazz.isAnnotationPresent(JsonTyping.class)) {
			this.register((this.plugin.getMetadata().getID() + "_" + clazz.getSimpleName()).toLowerCase(), clazz);
			return;
		}

		this.register(clazz.getAnnotation(JsonTyping.class).value().toLowerCase(), clazz);
	}

	public void register(String typing, Class<? extends E> clazz) throws Exception {
		if(this.typings.containsKey(typing)) {
			throw new Exception("Identical JSON typing found, aborting type registration for: " + typing);
		}

		this.typings.put(typing, clazz);
	}

	/**
	 * Fetches the class in the registry based on the typing id.
	 *
	 * @param id The typing id of a registered class
	 * @return The class in the registry matching the typing, if any.
	 */
	@Nullable
	public Class<? extends E> get(String id) {
		return typings.get(id);
	}
}
