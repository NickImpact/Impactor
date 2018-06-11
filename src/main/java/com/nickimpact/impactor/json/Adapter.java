package com.nickimpact.impactor.json;

import com.google.common.collect.Lists;
import com.google.gson.*;
import com.nickimpact.impactor.api.logger.Logger;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.text.Text;

import java.lang.reflect.Type;

/**
 * This class serves as a way to serialize as well as deserialize any object with GSON.
 * To use this class properly, we must alert GSON that the adapter for this class in
 * question exists, and register it.
 *
 * <p>Note: The main purpose behind this class is to help serialize and deserialize
 * an abstract class. With GSON, we can't serialize and deserialize an abstract extending
 * class properly due to the absence of variables known to the scope. By offering the base
 * class as the adapter element, any inheriting class will be able to know of the variables
 * contained in the higher scope, and have all fields filled properly.</p>
 *
 * @author NickImpact
 */
@RequiredArgsConstructor
public abstract class Adapter<E> implements JsonSerializer<E>, JsonDeserializer<E> {

	private final SpongePlugin plugin;

	@Override
	@SuppressWarnings({"unchecked", "ConstantConditions"})
	public E deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException
	{
		JsonObject obj = (JsonObject)json;
		try {
			return (E) getGson().fromJson(obj, getRegistry().get(obj.get("type").getAsString().toLowerCase()));
		} catch (Exception e) {
			plugin.getLogger().send(Logger.Prefixes.WARN, Lists.newArrayList(
					Text.of("========== JSON Error =========="),
					Text.of("Failed to deserialize JSON data"),
					Text.of("Exception: " + e.getClass().getSimpleName()),
					Text.of("================================")
			));
			throw new JsonParseException(e.getMessage());
		}
	}

	@Override
	public JsonElement serialize(E src, Type type, JsonSerializationContext ctx) {
		return ctx.serialize(src);
	}

	public abstract Gson getGson();

	public abstract Registry getRegistry();
}
