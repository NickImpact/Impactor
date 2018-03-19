package com.nickimpact.impactor.gui;

import com.nickimpact.impactor.ImpactorCore;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * (Some note will appear here)
 *
 * @author NickImpact (Nick DeGruccio)
 */
public abstract class InventoryBase {

	protected Player player;

	private Inventory.Builder builder;
	private Map<Integer, Icon> icons;
	private Inventory inventory;

	private boolean border = false;

	public InventoryBase(Player player){
		this.player = player;
		this.icons = new HashMap<>();
		this.builder = Inventory.builder()
				.property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(getTitle()))
				.property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, getSize()))
				.of(InventoryArchetypes.MENU_GRID)
				.listener(ClickInventoryEvent.class, this::processClick)
				.listener(InteractInventoryEvent.Close.class, this::processClose)
		;
	}

	public abstract Text getTitle();
	public abstract int getSize();

	/**
	 * Retrieves a copy of the current inventory builder.
	 * Typically used before running usage of {@link #buildInventory()} buildInventory()}
	 * to help apply new properties to the viewed inventory
	 *
	 * @return A {@link Inventory.Builder} reference to the current inventory build
	 */
	public Inventory.Builder getInventoryBuilder() {
		return this.builder;
	}

	private void processClick(ClickInventoryEvent event) {
		event.setCancelled(true);
		event.getCause().first(Player.class).ifPresent(player -> {
			event.getTransactions().forEach(transaction -> {
				transaction.getSlot().getProperty(SlotIndex.class, "slotindex").ifPresent(slot -> {
					Icon icon = icons.get(slot.getValue());
					if(icon != null) {
						Sponge.getScheduler().createTaskBuilder()
								.execute(() -> icon.process(new Clickable(player, event)))
								.delayTicks(1)
								.submit(ImpactorCore.getInstance());
					}
				});
			});
		});
	}

	protected void processClose(InteractInventoryEvent.Close event) {}

	/**
	 * Adds a {@link Icon} display to the Mapped table of icons for the UI.
	 *
	 * @param icon The icon we are planning to display
	 */
	public void addIcon(Icon icon) {
		this.icons.put(icon.getSlot(), icon);
	}

	public void removeIcon(int slot) {
		this.icons.remove(slot);
	}

	/**
	 * Attempts to locate a registered icon in the current icon registry for the inventory
	 *
	 * @param slot The slot index of an icon
	 * @return An Optional value, either with an icon or empty when the slot has a null icon
	 */
	public Optional<Icon> getIcon(int slot) {
		return Optional.ofNullable(this.icons.get(slot));
	}

	public void clearIcons(int... slots) {
		for(int slot : slots) {
			this.icons.remove(slot);
		}
	}

	/**
	 * Retrieves a copy of all registered icons
	 *
	 * @return A mapping of all icons with their assigned slot index
	 */
	public Map<Integer, Icon> getAllIcons(){
		return this.icons;
	}

	/**
	 * Draws a border around the inventory, with the top and bottom of the interface
	 * being completely drawn in, and sides with only the walls drawn.
	 *
	 * @param rows The number of rows to draw
	 */
	public void drawBorder(int rows, DyeColor color)
	{
		this.drawBorder(rows, color, Text.EMPTY);
	}

	public void drawBorder(int rows, DyeColor color, Text title) {
		ItemStack border = ItemStack.builder()
				.itemType(ItemTypes.STAINED_GLASS_PANE)
				.add(Keys.DISPLAY_NAME, title)
				.add(Keys.DYE_COLOR, color)
				.build();

		for (int y = 0; y < rows; y++)
		{
			if(y == 0 || y == rows - 1){
				for(int x = 0; x < 9; x++){
					this.addIcon(new Icon(x + (9 * y), border));
				}
			} else {
				this.addIcon(new Icon((9 * y), border));
				this.addIcon(new Icon(8 + (9 * y), border));
			}
		}

		this.border = true;
	}

	/**
	 * Like its counterpart, this method will only update the
	 * specified slot positions, allowing for quicker operation
	 * and less flicker within the UI itself
	 *
	 * Note: Due to a limitation in the Sponge API, we can't dynamically attach
	 *       new listeners to slots on an update. If an item is to change in a
	 *       slot, the listener provided by that original icon will persist.
	 *       Really only use this setup if you are purely doing cosmetic changes,
	 *       or your click listeners are setup to dynamically detect the change
	 *       of the icon.
	 *
	 * @param slots The slot indexes to modify
	 */
	public void updateContents(int... slots){
		GridInventory inv = this.inventory.query(GridInventory.class);
		for(final int sl : slots) {
			Slot slot = inv.getSlot(SlotIndex.of(sl)).orElseThrow(() -> new IllegalArgumentException("Invalid index: " + sl));
			if(this.icons.containsKey(sl))
				slot.set(this.icons.get(sl).getDisplay());
			else
				slot.clear();
		}
	}

	public void updateContents(int min, int max) {
		GridInventory inv = this.inventory.query(GridInventory.class);
		for(int i = min; i < max + 1; i++) {
			final int index = i;
			Slot slot = inv.getSlot(SlotIndex.of(i)).orElseThrow(() -> new IllegalArgumentException("Invalid index: " + index));
			if(this.icons.containsKey(i))
				slot.set(this.icons.get(i).getDisplay());
			else
				slot.clear();
		}
	}

	/**
	 * With the case of an open inventory, we will update all the inventory contents
	 * to their updated slot icons.
	 *
	 * Note: Due to a limitation in the Sponge API, we can't dynamically attach
	 *       new listeners to slots on an update. If an item is to change in a
	 *       slot, the listener provided by that original icon will persist.
	 *       Really only use this setup if you are purely doing cosmetic changes,
	 *       or your click listeners are setup to dynamically detect the change
	 *       of the icon.
	 */
	public void updateContents() {
		GridInventory gridInventory = this.getInventory().query(GridInventory.class);
		gridInventory.clear();
		this.icons.forEach(
				(index, inventoryIcon) ->
				{
					Slot slot = gridInventory.getSlot(SlotIndex.of(index))
							.orElseThrow(() -> new IllegalArgumentException(
									"Invalid index: " + index));
					slot.set(inventoryIcon.getDisplay());
				});
	}

	/**
	 * Forges a Sponge UI. Starts by composing all registered item mappings
	 * to their assigned slots, then proceeds to return the updated inventory.
	 *
	 * @return An {@link Inventory} with all icons sorted
	 */
	public Inventory getInventory() {
		if (this.inventory == null) {
			buildInventory();
		}
		return this.inventory;
	}

	/**
	 * For each and every display icon, register their click events if they have any,
	 * then proceed to establish the inventory. From there, we place each icon in their
	 * assigned slots.
	 */
	private void buildInventory() {
		this.inventory = this.builder.build(ImpactorCore.getInstance());

		GridInventory gridInventory = inventory.query(GridInventory.class);
		this.icons.forEach((index, inventoryIcon) -> {
			Slot slot = gridInventory.getSlot(SlotIndex.of(index)).orElseThrow(() -> new IllegalArgumentException("Invalid index: " + index));
			slot.set(inventoryIcon.getDisplay());
		});
	}
}
