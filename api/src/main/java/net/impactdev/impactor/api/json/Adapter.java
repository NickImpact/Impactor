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
			plugin.getPluginLogger().error(Lists.newArrayList(
				"Unable to parse JSON data, an error will be listed below:"
			));
			plugin.getPluginLogger().error("Unparsable JSON: \n" + json.toString());
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
