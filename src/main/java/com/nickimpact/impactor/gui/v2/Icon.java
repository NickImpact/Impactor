package com.nickimpact.impactor.gui.v2;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.gui.Clickable;
import lombok.Getter;
import lombok.Setter;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * (Some note will appear here)
 *
 * @author NickImpact (Nick DeGruccio)
 */
public class Icon {

	public static final Icon EMPTY = from(ItemStack.empty());
	public static final Icon BORDER = from(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DISPLAY_NAME, Text.EMPTY).add(Keys.DYE_COLOR, DyeColors.BLACK).build());
	public static final Icon ERROR = from(ItemStack.builder().itemType(ItemTypes.BARRIER).add(Keys.DISPLAY_NAME, Text.of(TextColors.RED, "ERROR")).build());
	public static final Icon CONFIRM = from(ItemStack.builder().itemType(ItemTypes.DYE).add(Keys.DYE_COLOR, DyeColors.LIME).add(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "Confirm Selection")).build());
	public static final Icon CANCEL = from(ItemStack.builder().itemType(ItemTypes.DYE).add(Keys.DYE_COLOR, DyeColors.LIME).add(Keys.DISPLAY_NAME, Text.of(TextColors.RED, "Cancel")).build());

	@Getter @Setter private ItemStack display;
	@Getter private Set<Consumer<Clickable>> listeners;

	public Icon(ItemStack display) {
		this.display = display;
		this.listeners = new HashSet<>();
	}

	public Icon(ItemStack display, Consumer<Clickable>... clickables) {
		this.display = display;
		this.listeners = new HashSet<>(Lists.newArrayList(clickables));
	}

	/**
	 * Creates an icon from an {@link ItemStack}
	 *
	 * @param stack The ItemStack to be represented by this icon
	 * @return The Icon containing the ItemStack as its display
	 */
	public static Icon from(ItemStack stack) {
		return new Icon(stack);
	}

	/**
	 * Appends a listener to an icon
	 *
	 * @param listener The consuming action to run
	 */
	public void addListener(Consumer<Clickable> listener) {
		this.listeners.add(listener);
	}

	/**
	 * Attempts to run each listener attached to the icon
	 *
	 * @param clickable The clickable wrapper with the player and event
	 */
	public void process(Clickable clickable) {
		for(Consumer<Clickable> listener : listeners)
			listener.accept(clickable);
	}
}
