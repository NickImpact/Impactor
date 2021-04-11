package net.impactdev.impactor.sponge.ui.icons;

import net.kyori.adventure.text.Component;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

public class SpongeIcons {

    public static SpongeIcon BORDER = SpongeIcon.builder()
            .delegate(ItemStack.builder()
                    .itemType(ItemTypes.BLACK_STAINED_GLASS_PANE)
                    .add(Keys.CUSTOM_NAME, Component.empty())
                    .build()
            )
            .build();

}
