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

package net.impactdev.impactor.api.json;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;

import java.lang.reflect.Type;
import java.util.Objects;

public abstract class Adapter<E> implements JsonSerializer<E>, JsonDeserializer<E> {

	protected final ImpactorPlugin plugin;

	public Adapter(ImpactorPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public E deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
		try {
			return ctx.deserialize(json, Objects.requireNonNull(this.getRegistry().get(json.getAsJsonObject().get("type").getAsString().toLowerCase())));
		} catch (Exception e) {
			plugin.getPluginLogger().error("JSON", Lists.newArrayList(
				"Unable to parse JSON data, an error will be listed below:"
			));
			plugin.getPluginLogger().error("JSON", "Unparsable JSON: \n" + json.toString());
			e.printStackTrace();
			throw new JsonParseException(e.getMessage());
		}
	}

	@Override
	public JsonElement serialize(E src, Type type, JsonSerializationContext ctx) {
		JsonElement element = ctx.serialize(src);
		JsonObject obj = (JsonObject) element;
		obj.addProperty("type", src.getClass().isAnnotationPresent(JsonTyping.class) ? src.getClass().getAnnotation(JsonTyping.class).value() : (this.plugin.getMetadata().getID() + "_" + src.getClass().getSimpleName()).toLowerCase());
		return obj;
	}

	protected abstract Registry<E> getRegistry();
}
