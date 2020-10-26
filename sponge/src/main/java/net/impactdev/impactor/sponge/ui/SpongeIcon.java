package net.impactdev.impactor.sponge.ui;

import net.impactdev.impactor.api.gui.Clickable;
import net.impactdev.impactor.api.gui.Icon;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class SpongeIcon implements Icon<ItemStack, Player, ClickInventoryEvent> {

	private ItemStack display;
	private Set<Consumer<Clickable<Player, ClickInventoryEvent>>> listeners;

	public static final SpongeIcon BORDER = new SpongeIcon(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.BLACK).add(Keys.DISPLAY_NAME, Text.EMPTY).build());

	public SpongeIcon(ItemStack display) {
		this.display = display;
		this.listeners = new HashSet<>();
	}

	@Override
	public ItemStack getDisplay() {
		return this.display;
	}

	@Override
	public void addListener(Consumer<Clickable<Player, ClickInventoryEvent>> listener) {
		this.listeners.add(listener);
	}

	@Override
	public void process(Clickable<Player, ClickInventoryEvent> clickable) {
		for(Consumer<Clickable<Player, ClickInventoryEvent>> listener : listeners) {
			listener.accept(clickable);
		}
	}
}
