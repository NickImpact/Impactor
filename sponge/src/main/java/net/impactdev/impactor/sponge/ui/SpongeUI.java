package net.impactdev.impactor.sponge.ui;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.ArrayListMultimap;
import net.impactdev.impactor.api.gui.Layout;
import net.impactdev.impactor.api.gui.UI;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.utilities.Builder;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.impactdev.impactor.sponge.ui.rework.SpongeLayout;
import net.impactdev.impactor.sponge.ui.rework.SpongeIcon;
import net.kyori.adventure.text.TextComponent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.menu.ClickType;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;
import org.spongepowered.api.item.inventory.menu.handler.ClickHandler;
import org.spongepowered.api.item.inventory.menu.handler.SlotClickHandler;
import org.spongepowered.api.item.inventory.type.ViewableInventory;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpongeUI implements UI<ServerPlayer, SpongeIcon> {

    private final ImpactorPlugin plugin;
    private final InventoryMenu viewable;

    private SpongeLayout layout;

    private final CooldownClickHandler listener;
    private final Cache<UUID, Boolean> cooldowns = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .build();

    private SpongeUI(ImpactorPlugin plugin, InventoryMenu viewable) {
        this.plugin = plugin;
        this.viewable = viewable;

        this.listener = new CooldownClickHandler(this);
        this.viewable.registerSlotClick(this.listener);
        this.viewable.setReadOnly(true);
    }

    @Override
    public ImpactorPlugin getPlugin() {
        return this.plugin;
    }

    public InventoryMenu getBackingMenu() {
        return this.viewable;
    }

    public CooldownClickHandler getListener() {
        return this.listener;
    }

    @Override
    public boolean open(ServerPlayer viewer) {
        return this.viewable.open(viewer).isPresent();
    }

    @Override
    public boolean close(ServerPlayer viewer) {
        return viewer.closeInventory();
    }

    @Override
    public SpongeLayout getLayout() {
        return this.layout;
    }

    @Override
    public SpongeUI define(Layout<SpongeIcon> layout) {
        this.layout = (SpongeLayout) layout;

        for (int i = 0; i < this.viewable.inventory().capacity(); i++) {
            final int slot = i;
            layout.getIcon(i).ifPresent(icon -> {
                this.viewable.inventory().set(slot, icon.getDisplay());
                if (icon.getListeners().size() > 0) {
                    icon.getListeners().forEach(listener -> {
                        this.listener.register(slot, listener);
                    });
                }
            });
        }

        return this;
    }

    public static SpongeUIBuilder builder() {
        return new SpongeUIBuilder();
    }

    public static class SpongeUIBuilder implements Builder<SpongeUI, SpongeUIBuilder> {

        private TextComponent title;
        private ViewableInventory view;

        public SpongeUIBuilder title(TextComponent title) {
            this.title = title;
            return this;
        }

        public SpongeUIBuilder view(ViewableInventory view) {
            this.view = view;
            return this;
        }

        @Override
        public SpongeUIBuilder from(SpongeUI input) {
            return null;
        }

        @Override
        public SpongeUI build() {
            InventoryMenu menu = InventoryMenu.of(this.view);
            menu.setTitle(this.title);

            return new SpongeUI(SpongeImpactorPlugin.getInstance(), menu);
        }
    }

    static class CooldownClickHandler implements SlotClickHandler {

        private final SpongeUI parent;
        private final ArrayListMultimap<Integer, ClickHandler> delegates;

        public CooldownClickHandler(SpongeUI parent) {
            this.parent = parent;
            this.delegates = ArrayListMultimap.create();
        }

        public void register(int slot, ClickHandler handler) {
            this.delegates.put(slot, handler);
        }

        public void clear() {
            this.delegates.clear();
        }

        @Override
        public boolean handle(Cause cause, Container container, Slot slot, int slotIndex, ClickType<?> clickType) {
            ServerPlayer player = cause.first(ServerPlayer.class).orElseThrow(() -> new IllegalStateException("Unable to locate viewer that caused click"));
            boolean visible = this.parent.cooldowns.asMap().containsKey(player.uniqueId());

            if (!visible) {
                this.parent.cooldowns.put(player.uniqueId(), true);
                AtomicBoolean result = new AtomicBoolean(true);
                this.delegates.get(slotIndex).forEach(handler -> {
                    Sponge.server().scheduler().submit(Task.builder()
                            .execute(() -> {
                                if (result.get()) {
                                    if (!handler.handle(cause, container, clickType)) {
                                        result.set(false);
                                    }
                                }
                            })
                            .delay(Ticks.of(1))
                            .plugin(SpongeImpactorPlugin.getInstance().getPluginContainer())
                            .build()
                    );
                });
            }

            return false;
        }
    }
}
