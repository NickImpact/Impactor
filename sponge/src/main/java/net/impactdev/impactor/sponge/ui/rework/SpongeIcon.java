package net.impactdev.impactor.sponge.ui.rework;

import net.impactdev.impactor.api.gui.Icon;
import net.impactdev.impactor.api.utilities.Builder;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.menu.handler.ClickHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpongeIcon implements Icon<ItemStack, ClickHandler> {

    private final ItemStack delegate;
    private final Set<ClickHandler> handlers;

    private SpongeIcon(ItemStack delegate) {
        this.delegate = delegate;
        this.handlers = new HashSet<>();
    }

    @Override
    public ItemStack getDisplay() {
        return this.delegate;
    }

    @Override
    public void addListener(ClickHandler listener) {
        this.handlers.add(listener);
    }

    @Override
    public List<ClickHandler> getListeners() {
        return new ArrayList<>(this.handlers);
    }

    public static SpongeIconBuilder builder() {
        return new SpongeIconBuilder();
    }

    public static class SpongeIconBuilder implements Builder<SpongeIcon, SpongeIconBuilder> {

        private ItemStack delegate;
        private Set<ClickHandler> handlers = new HashSet<>();

        public SpongeIconBuilder delegate(ItemStack delegate) {
            this.delegate = delegate;
            return this;
        }

        public SpongeIconBuilder listener(ClickHandler... handlers) {
            this.handlers.addAll(Arrays.asList(handlers));
            return this;
        }

        @Override
        public SpongeIconBuilder from(SpongeIcon input) {
            this.delegate = input.delegate;
            this.handlers = input.handlers;

            return this;
        }

        @Override
        public SpongeIcon build() {
            SpongeIcon result = new SpongeIcon(this.delegate);
            for(ClickHandler handler : this.handlers) {
                result.addListener(handler);
            }

            return result;
        }
    }


}
