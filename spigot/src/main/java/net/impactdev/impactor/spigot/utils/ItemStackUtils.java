package net.impactdev.impactor.spigot.utils;

import net.impactdev.impactor.api.utilities.Builder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ItemStackUtils {

	public static ItemStackBuilder itemBuilder() {
		return new ItemStackBuilder();
	}

	public static class ItemStackBuilder implements Builder<ItemStack, ItemStackBuilder> {

		private ItemStack original;

		private Material material;
		private int amount = 1;
		private short damage = 0;

		private String name;
		private List<String> lore;

		public ItemStackBuilder fromItem(ItemStack item) {
			this.original = item;
			this.amount = item.getAmount();
			this.damage = item.getDurability();
			return this;
		}

		public ItemStackBuilder material(Material material) {
			this.material = material;
			return this;
		}

		public ItemStackBuilder amount(int amount) {
			this.amount = amount;
			return this;
		}

		public ItemStackBuilder damage(short damage) {
			this.damage = damage;
			return this;
		}

		public ItemStackBuilder name(String name) {
			this.name = ChatColor.translateAlternateColorCodes('&', name);
			return this;
		}

		public ItemStackBuilder lore(List<String> lore) {
			this.lore = lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).collect(Collectors.toList());
			return this;
		}

		@Override
		public ItemStackBuilder from(ItemStack input) {
			return null;
		}

		@Override
		public ItemStack build() {
			if(this.original != null) {
				return buildStack(original);
			} else {
				ItemStack stack = new ItemStack(this.material);
				return buildStack(stack);
			}
		}

		private ItemStack buildStack(ItemStack item) {
			item.setAmount(this.amount);
			item.setDurability(this.damage);
			ItemMeta meta = item.getItemMeta();
			if (this.name != null) {
				meta.setDisplayName(this.name);
			}

			if (this.lore != null) {
				meta.setLore(lore);
			}
			item.setItemMeta(meta);
			return item;
		}
	}

	public static ItemStack createSkull(UUID uuid) {
		return SkullCreator.itemFromUuid(uuid);
	}

	public static ItemStack createSkull(String texture) {
		return SkullCreator.itemFromBase64(texture);
	}

	public static ItemStack createSkull(SkullTextures texture) {
		return SkullCreator.itemFromBase64(texture.value);
	}

	/**
	 * A library for the Bukkit API to create player skulls
	 * from names, base64 strings, and texture URLs.
	 *
	 * Does not use any NMS code, and should work across all versions.
	 *
	 * @author Dean B on 12/28/2016.
	 */
	public static class SkullCreator {

		/**
		 * Creates a player skull with a UUID. 1.13 only.
		 *
		 * @param id The Player's UUID
		 * @return The head of the Player
		 */
		public static ItemStack itemFromUuid(UUID id) {
			ItemStack item = getPlayerSkullItem();

			return itemWithUuid(item, id);
		}

		/**
		 * Creates a player skull based on a UUID. 1.13 only.
		 *
		 * @param item The item to apply the name to
		 * @param id The Player's UUID
		 * @return The head of the Player
		 */
		public static ItemStack itemWithUuid(ItemStack item, UUID id) {
			notNull(item, "item");
			notNull(id, "id");

			SkullMeta meta = (SkullMeta) item.getItemMeta();
			meta.setOwningPlayer(Bukkit.getOfflinePlayer(id));
			item.setItemMeta(meta);

			return item;
		}

		/**
		 * Creates a player skull based on a Mojang server URL.
		 *
		 * @param url The URL of the Mojang skin
		 * @return The head associated with the URL
		 */
		public static ItemStack itemFromUrl(String url) {
			ItemStack item = getPlayerSkullItem();

			return itemWithUrl(item, url);
		}


		/**
		 * Creates a player skull based on a Mojang server URL.
		 *
		 * @param item The item to apply the skin to
		 * @param url The URL of the Mojang skin
		 * @return The head associated with the URL
		 */
		public static ItemStack itemWithUrl(ItemStack item, String url) {
			notNull(item, "item");
			notNull(url, "url");

			return itemWithBase64(item, urlToBase64(url));
		}

		/**
		 * Creates a player skull based on a base64 string containing the link to the skin.
		 *
		 * @param base64 The base64 string containing the texture
		 * @return The head with a custom texture
		 */
		public static ItemStack itemFromBase64(String base64) {
			ItemStack item = getPlayerSkullItem();
			return itemWithBase64(item, base64);
		}

		/**
		 * Applies the base64 string to the ItemStack.
		 *
		 * @param item The ItemStack to put the base64 onto
		 * @param base64 The base64 string containing the texture
		 * @return The head with a custom texture
		 */
		public static ItemStack itemWithBase64(ItemStack item, String base64) {
			notNull(item, "item");
			notNull(base64, "base64");

			UUID hashAsId = new UUID(base64.hashCode(), base64.hashCode());
			return Bukkit.getUnsafe().modifyItemStack(item,
					"{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv" + base64 + "\"}]}}}"
			);
		}

		/**
		 * Sets the block to a skull with the given UUID.
		 *
		 * @param block The block to set
		 * @param id The player to set it to
		 */
		public static void blockWithUuid(Block block, UUID id) {
			notNull(block, "block");
			notNull(id, "id");

			setBlockType(block);
			((Skull) block.getState()).setOwningPlayer(Bukkit.getOfflinePlayer(id));
		}

		/**
		 * Sets the block to a skull with the given UUID.
		 *
		 * @param block The block to set
		 * @param url The mojang URL to set it to use
		 */
		public static void blockWithUrl(Block block, String url) {
			notNull(block, "block");
			notNull(url, "url");

			blockWithBase64(block, urlToBase64(url));
		}

		/**
		 * Sets the block to a skull with the given UUID.
		 *
		 * @param block The block to set
		 * @param base64 The base64 to set it to use
		 */
		public static void blockWithBase64(Block block, String base64) {
			notNull(block, "block");
			notNull(base64, "base64");

			UUID hashAsId = new UUID(base64.hashCode(), base64.hashCode());

			String args = String.format(
					"%d %d %d %s",
					block.getX(),
					block.getY(),
					block.getZ(),
					"{Owner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + base64 + "\"}]}}}"
			);

			if (newerApi()) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "data merge block " + args);
			} else {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"blockdata " + args);
			}
		}

		private static boolean newerApi() {
			try {

				Material.valueOf("PLAYER_HEAD");
				return true;

			} catch (IllegalArgumentException e) { // If PLAYER_HEAD doesn't exist
				return false;
			}
		}

		private static ItemStack getPlayerSkullItem() {
			if (newerApi()) {
				return new ItemStack(Material.valueOf("PLAYER_HEAD"));
			} else {
				return new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (byte) 3);
			}
		}

		private static void setBlockType(Block block) {
			try {
				block.setType(Material.valueOf("PLAYER_HEAD"), false);
			} catch (IllegalArgumentException e) {
				block.setType(Material.valueOf("SKULL"), false);
			}
		}

		private static void notNull(Object o, String name) {
			if (o == null) {
				throw new NullPointerException(name + " should not be null!");
			}
		}

		private static String urlToBase64(String url) {

			URI actualUrl;
			try {
				actualUrl = new URI(url);
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
			String toEncode = "{\"textures\":{\"SKIN\":{\"url\":\"" + actualUrl.toString() + "\"}}}";
			return Base64.getEncoder().encodeToString(toEncode.getBytes());
		}
	}
}
