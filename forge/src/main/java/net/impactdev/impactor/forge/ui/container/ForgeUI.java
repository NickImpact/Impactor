/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2022 NickImpact
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package net.impactdev.impactor.forge.ui.container;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.Template;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import ca.landonjw.gooeylibs2.implementation.GooeyContainer;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.platform.players.PlatformPlayerManager;
import net.impactdev.impactor.api.ui.containers.ImpactorUI;
import net.impactdev.impactor.api.ui.containers.detail.RefreshDetail;
import net.impactdev.impactor.api.ui.containers.icons.ClickProcessor;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.layouts.Layout;
import net.impactdev.impactor.forge.adventure.RelocationTranslator;
import net.impactdev.impactor.forge.ui.container.icons.ForgeIcon;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ForgeUI implements ImpactorUI {

    private final Key namespace;
    private final Component title;
    private final Layout layout;
    private final boolean readonly;

    private final Template template;
    private final GooeyPage page;

    private ForgeUI(ForgeUIBuilder builder) {
        this.namespace = builder.key;
        this.title = builder.title;
        this.layout = builder.layout;
        this.readonly = builder.readonly;
        this.template = this.create();

        this.page = GooeyPage.builder()
                .template(this.template)
                .title(RelocationTranslator.relocated(this.title))
                .build();
    }

    @Override
    public Key namespace() {
        return this.namespace;
    }

    @Override
    public Layout layout() {
        return this.layout;
    }

    @Override
    public void set(@Nullable Icon<?> icon, int slot) {
        if(icon == null) {
            this.page.getTemplate().getSlot(slot).set(ItemStack.EMPTY);
        } else {
            this.page.getTemplate().getSlot(slot).set(((ForgeIcon) icon).getDelegate().getDisplay());
        }
    }

    @Override
    public void open(PlatformPlayer viewer) {
        PlatformPlayerManager<ServerPlayerEntity> manager = (PlatformPlayerManager<ServerPlayerEntity>) Impactor.getInstance().getPlatform().playerManager();
        ServerPlayerEntity player = manager.translate(viewer).orElseThrow(() -> new IllegalStateException("Player not available or found"));
        UIManager.openUIForcefully(player, this.page);
    }

    @Override
    public void close(PlatformPlayer viewer) {
        PlatformPlayerManager<ServerPlayerEntity> manager = (PlatformPlayerManager<ServerPlayerEntity>) Impactor.getInstance().getPlatform().playerManager();
        ServerPlayerEntity player = manager.translate(viewer).orElseThrow(() -> new IllegalStateException("Player not available or found"));
        if(player.containerMenu instanceof GooeyContainer) {
            GooeyContainer container = (GooeyContainer) player.containerMenu;
            if(container.getPage().equals(this.page)) {
                player.closeContainer();
            }
        }
    }

    @Override
    public void refresh(RefreshDetail detail) {

    }

    private ChestTemplate create() {
        ChestTemplate.Builder builder = ChestTemplate.builder(this.layout.dimensions().y());
        this.layout.elements().forEach((slot, icon) -> {
            ForgeIcon actual = (ForgeIcon) icon;
            builder.set(slot, actual.getDelegate());
        });

        return builder.build();
    }

    public static class ForgeUIBuilder implements UIBuilder {

        private Key key;
        private Component title;
        private Layout layout;
        private boolean readonly = true;

        @Override
        public UIBuilder provider(Key key) {
            this.key = key;
            return this;
        }

        @Override
        public UIBuilder title(Component title) {
            this.title = title;
            return this;
        }

        @Override
        public UIBuilder layout(Layout layout) {
            this.layout = layout;
            return this;
        }

        @Override
        public UIBuilder readonly(boolean state) {
            this.readonly = state;
            return this;
        }

        @Override
        public UIBuilder onClick(ClickProcessor processor) {
            return null;
        }

        @Override
        public UIBuilder onClose(CloseProcessor handler) {
            return null;
        }

        @Override
        public UIBuilder from(ImpactorUI input) {
            return this;
        }

        @Override
        public ImpactorUI build() {
            return new ForgeUI(this);
        }
    }
}
