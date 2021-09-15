package net.impactdev.impactor.api.scoreboard.components;

import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.kyori.adventure.text.format.NamedTextColor.*;

public class LineIdentifier {

	private static final CircularLinkedList<NamedTextColor> colors = CircularLinkedList.of(BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE);

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public static Component generate() {
		return Component.text(" ").color(colors.next().get());
	}

}
