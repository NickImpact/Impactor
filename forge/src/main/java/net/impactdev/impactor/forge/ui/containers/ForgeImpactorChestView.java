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

package net.impactdev.impactor.forge.ui.containers;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import net.impactdev.impactor.adventure.AdventureTranslator;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.views.ChestView;
import net.impactdev.impactor.forge.ui.gooey.GooeyIcon;
import net.impactdev.impactor.ui.containers.views.ImpactorChestView;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public final class ForgeImpactorChestView extends ImpactorChestView {

    private final ChestTemplate template;
    private final GooeyPage delegate;

    private ForgeImpactorChestView(ImpactorChestViewBuilder builder) {
        super(builder);
        ChestTemplate.Builder template = ChestTemplate.builder(this.rows());
        this.layout().elements().forEach((slot, icon) -> {
            template.set(slot, new GooeyIcon(icon));
        });

        this.delegate = GooeyPage.builder()
                .template(this.template = template.build())
                .title(AdventureTranslator.toNative(this.title()))
//                .onClose(action -> view.) // TODO - Add this back to Impactor API, add onClick to Gooey
                .build();
    }

    @Override
    public void set(@Nullable Icon icon, int slot) {
        this.template.set(slot, Optional.ofNullable(icon).map(GooeyIcon::new).orElse(null));

    }

    @Override
    public void open(PlatformPlayer viewer) {
        @Nullable ServerPlayer forge = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(viewer.uuid());
        UIManager.openUIForcefully(Objects.requireNonNull(forge), this.delegate);
    }

    @Override
    public void close(PlatformPlayer viewer) {
        @Nullable ServerPlayer forge = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(viewer.uuid());
        UIManager.closeUI(Objects.requireNonNull(forge));
    }

    public static final class ForgeImpactorChestViewBuilder extends ImpactorChestViewBuilder {
        @Override
        public ChestView build() {
            return new ForgeImpactorChestView(this);
        }
    }
}