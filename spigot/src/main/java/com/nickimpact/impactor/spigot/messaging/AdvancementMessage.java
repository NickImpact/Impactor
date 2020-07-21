package com.nickimpact.impactor.spigot.messaging;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.nickimpact.impactor.api.utilities.Builder;
import com.nickimpact.impactor.spigot.SpigotImpactorPlugin;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class AdvancementMessage {

	private NamespacedKey key;

	private String icon;
	private String title;
	private String message;
	private AdvancementFrame frame;

	public AdvancementMessage(AdvancementMessageBuilder builder) {
		this.key = new NamespacedKey(SpigotImpactorPlugin.getInstance(), UUID.randomUUID().toString());
		this.icon = builder.icon;
		this.title = builder.title;
		this.message = builder.message;
		this.frame = builder.frame;
	}

	public void sendToPlayer(Player player) {
		Bukkit.getUnsafe().loadAdvancement(key, this.convertToJson());
		Advancement advancement = Bukkit.getAdvancement(key);
		AdvancementProgress progress = player.getAdvancementProgress(advancement);
		progress.awardCriteria(progress.getRemainingCriteria().iterator().next());
		new BukkitRunnable() {
			@Override
			public void run() {
				AdvancementProgress progress2 = player.getAdvancementProgress(advancement);
				progress2.revokeCriteria(progress2.getAwardedCriteria().iterator().next());

				Bukkit.getUnsafe().removeAdvancement(key);
			}
		}.runTaskLater(SpigotImpactorPlugin.getInstance(), 20);
	}

	private String convertToJson() {
		JsonObject json = new JsonObject();

		JsonObject icon = new JsonObject();
		icon.addProperty("item", this.icon);

		JsonObject message = new JsonObject();
		message.add("icon", icon);
		message.addProperty("title", this.title);
		message.addProperty("description", this.message);
		message.addProperty("background", "minecraft:textures/gui/advancements/backgrounds/adventure.png");
		message.addProperty("frame", this.frame.name().toLowerCase());
		message.addProperty("announce_to_chat", false);
		message.addProperty("show_toast", true);
		message.addProperty("hidden", true);

		JsonObject criteria = new JsonObject();
		JsonObject trigger = new JsonObject();

		trigger.addProperty("trigger", "minecraft:impossible");
		criteria.add("impossible", trigger);
		json.add("criteria", criteria);
		json.add("display", message);

		return new GsonBuilder().setPrettyPrinting().create().toJson(json);
	}

	public static AdvancementMessageBuilder builder() {
		return new AdvancementMessageBuilder();
	}

	public static class AdvancementMessageBuilder implements Builder<AdvancementMessage, AdvancementMessageBuilder> {

		private String title;
		private String message;
		private String icon;
		private AdvancementFrame frame;

		public AdvancementMessageBuilder title(String title) {
			this.title = title;
			return this;
		}

		public AdvancementMessageBuilder message(String message) {
			this.message = message;
			return this;
		}

		public AdvancementMessageBuilder icon(String icon) {
			this.icon = icon;
			return this;
		}

		public AdvancementMessageBuilder frame(AdvancementFrame frame) {
			this.frame = frame;
			return this;
		}

		@Override
		public AdvancementMessageBuilder from(AdvancementMessage input) {
			return null;
		}

		@Override
		public AdvancementMessage build() {
			return new AdvancementMessage(this);
		}
	}

	public enum AdvancementFrame {
		GOAL,
		CHALLENGE,
		TASK,
	}

}
