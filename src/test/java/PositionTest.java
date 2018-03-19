import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PositionTest {

	@Test
	public void position() {
		int players = 18;
		if(players % 20 == 18 || players % 20 == 19) {
			players += 20 - (players % 20);
		}

		assertTrue(players % 20 == 0);
	}
}
