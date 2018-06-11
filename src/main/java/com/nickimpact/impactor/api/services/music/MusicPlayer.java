package com.nickimpact.impactor.api.services.music;

import com.xxmicloxx.NoteBlockAPI.SongPlayer;
import lombok.Builder;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Collection;

@Builder
public class MusicPlayer {

	private final SongPlayer songPlayer;

	private final Collection<Player> players;

	public MusicPlayer initialize() throws NullPointerException {
		if(songPlayer == null || players == null) {
			throw new NullPointerException();
		}

		for(Player player : players) {
			songPlayer.addPlayer(player);
		}

		return this;
	}

	public void play() {
		this.songPlayer.setPlaying(true);
	}

	public void stop() {
		this.songPlayer.destroy();
	}
}
