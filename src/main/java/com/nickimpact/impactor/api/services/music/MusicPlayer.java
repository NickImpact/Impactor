package com.nickimpact.impactor.api.services.music;

import com.xxmicloxx.NoteBlockAPI.SongPlayer;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.entity.living.player.Player;
import scala.actors.threadpool.Arrays;

import java.util.Collection;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MusicPlayer {

	private final NBSSong song;
	private final Collection<Player> players;

	private SongPlayer songPlayer;

	public MusicPlayer initialize(SongPlayer songPlayer) throws NullPointerException {
		this.songPlayer = songPlayer;
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

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private NBSSong song;
		private Collection<Player> players;

		public Builder song(NBSSong song) {
			this.song = song;
			return this;
		}

		public Builder players(Player... players) {
			this.players.addAll(Arrays.asList(players));
			return this;
		}

		public MusicPlayer build() throws IllegalStateException {
			if(song == null) {
				throw new IllegalStateException("Song must not be null");
			}

			return new MusicPlayer(song, players);
		}
	}
}
