/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2021 NickImpact
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

package net.impactdev.impactor.api.json;

import com.google.common.collect.Maps;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;

import javax.annotation.Nullable;
import java.util.Map;

public class Registry<E> {

	private final ImpactorPlugin plugin;
	private final Map<String, Class<? extends E>> typings = Maps.newHashMap();

	public Registry(ImpactorPlugin plugin) {
		this.plugin = plugin;
	}

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
