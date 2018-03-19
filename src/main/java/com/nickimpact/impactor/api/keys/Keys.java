package com.nickimpact.impactor.api.keys;

import com.pixelmonmod.pixelmon.storage.NbtKeys;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.util.TypeTokens;

import java.util.function.Predicate;

public class Keys {
	public static final MaybeKey<String, PluginManager> SPRITE_NAME = new MaybeKey<>(
			KeyFactory.makeSingleKey(TypeTokens.STRING_TOKEN, TypeTokens.STRING_VALUE_TOKEN, DataQuery.of(NbtKeys.SPRITE_NAME), "sprite_name", "Sprite Name"),
			x -> x.isLoaded("pixelmon"),
			Sponge.getPluginManager()
	);

	/**
	 * Represents a key which might not always be present. For instance, we want to provide a key for pixelmon,
	 * but this key might not always be available for us to use, or it'd be pointless to try and use. Therefore,
	 * we control this by forcing the user to run {@link #get(Object)}, with the possibility of encountering an
	 * exception if the predicate fails.
	 *
	 * @param <T> The type of the key
	 * @param <U> The object to test the predicate against
	 */
	public static class MaybeKey<T, U> {

		private Key<Value<T>> key;
		private Predicate<U> predicate;
		private U tester;

		MaybeKey(Key<Value<T>> key, Predicate<U> predicate) {
			this.key = key;
			this.predicate = predicate;
			this.tester = null;
		}

		MaybeKey(Key<Value<T>> key, Predicate<U> predicate, U tester) {
			this.key = key;
			this.predicate = predicate;
			this.tester = tester;
		}

		/**
		 * Returns the key assigned to this instance, if the predicate test passes.
		 *
		 * @return The key
		 * @throws Exception If the key is not available for the operation
		 */
		public Key<Value<T>> get() throws Exception {
			if(this.tester == null) {
				throw new Exception("No variable present for predicate check...");
			}
			return this.get(this.tester);
		}

		/**
		 * Returns the key assigned to this instance, if the predicate test passes.
		 *
		 * @param value The variable to use to test the predicate
		 * @return The key
		 * @throws Exception If the key is not available for the operation
		 */
		public Key<Value<T>> get(U value) throws Exception {
			if(this.predicate.test(value)) {
				return this.key;
			}
			throw new Exception("Key not available...");
		}
	}
}
