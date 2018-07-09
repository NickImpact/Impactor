import com.nickimpact.impactor.api.services.music.MusicPlayer;
import com.nickimpact.impactor.api.services.music.NBSSong;
import com.xxmicloxx.NoteBlockAPI.RadioSongPlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;

public class SoundTest {

	public void test() {
//		MusicPlayer player = MusicPlayer.builder()
//				.songPlayer(new RadioSongPlayer(NBSSong.builder().build().getSong()))
//				.players(Sponge.getServer().getOnlinePlayers())
//				.build();
//
//		try {
//			player.initialize().play();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		MusicPlayer player = MusicPlayer.builder()
				.song(NBSSong.builder()
						.title(Text.of("Howdy"))
						.filename("x")
						.build()
				)
				.build();
	}
}
