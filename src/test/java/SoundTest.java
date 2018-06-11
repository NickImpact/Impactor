import com.nickimpact.impactor.api.services.music.MusicPlayer;
import com.nickimpact.impactor.api.services.music.NBSSong;
import com.xxmicloxx.NoteBlockAPI.RadioSongPlayer;
import org.spongepowered.api.Sponge;

public class SoundTest {

	public void test() {
		MusicPlayer player = MusicPlayer.builder()
				.songPlayer(new RadioSongPlayer(NBSSong.builder().build().getSong()))
				.players(Sponge.getServer().getOnlinePlayers())
				.build();

		try {
			player.initialize().play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
