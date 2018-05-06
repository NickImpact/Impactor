package com.nickimpact.impactor.api.minigames.teams;

import com.google.common.collect.Lists;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Team {

	protected final List<UUID> members;

	private final TextColor color;

	public Team(Builder builder) {
		this.members = builder.members;
		this.color = builder.color;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private List<UUID> members = Lists.newArrayList();
		private TextColor color = TextColors.GRAY;

		public Builder member(UUID uuid) {
			this.members.add(uuid);
			return this;
		}

		public Builder members(UUID... uuids) {
			this.members.addAll(Arrays.asList(uuids));
			return this;
		}

		public Builder members(List<UUID> uuids) {
			this.members = uuids;
			return this;
		}

		public Builder color(TextColor color) {
			this.color = color;
			return this;
		}

		public Team build() {
			return new Team(this);
		}
	}
}
