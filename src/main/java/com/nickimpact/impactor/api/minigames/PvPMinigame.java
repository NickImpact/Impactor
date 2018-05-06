package com.nickimpact.impactor.api.minigames;

public interface PvPMinigame extends Minigame {

	TeamType getMode();

	enum TeamType {
		SOLO("Man"),
		TEAM("Team");

		private String display;

		TeamType(String display) {
			this.display = display;
		}
	}
}
