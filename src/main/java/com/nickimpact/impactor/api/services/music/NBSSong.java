package com.nickimpact.impactor.api.services.music;

import com.xxmicloxx.NoteBlockAPI.NBSDecoder;
import com.xxmicloxx.NoteBlockAPI.Song;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.text.Text;

import java.io.File;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class NBSSong {

	/** The actual song to be played (will be null if the builder points to an invalid direction) */
	private final Song song;

	/** The title of the song */
	private final Text title;

	/** The location of the song file */
	private final File location;

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private Text title;
		private String filename;

		public Builder title(Text title) {
			this.title = title;
			return this;
		}

		public Builder filename(String filename) {
			this.filename = filename;
			return this;
		}

		public NBSSong build() {
			File location = new File("songs/" + filename + ".nbs");
			Song song = NBSDecoder.parse(location);
			return new NBSSong(song, title, location);
		}
	}
}
