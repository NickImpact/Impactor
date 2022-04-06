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

package net.impactdev.impactor.sponge.ui.containers;

import net.impactdev.impactor.api.ui.icons.Icon;
import net.impactdev.impactor.api.ui.layouts.Layout;
import net.impactdev.impactor.api.ui.pagination.Page;
import net.impactdev.impactor.api.ui.pagination.Pagination;
import net.impactdev.impactor.api.ui.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.ui.pagination.updaters.PageUpdaterType;
import net.impactdev.impactor.sponge.ui.containers.icons.SpongeIcon;
import net.kyori.adventure.util.TriState;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.type.ViewableInventory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class SpongePage implements Page<ViewableInventory> {
    private final ViewableInventory view;
    private final Map<Integer, Icon<?>> icons;

    public SpongePage(ViewableInventory view, Map<Integer, Icon<?>> icons) {
        this.view = view;
        this.icons = icons;
    }

    public void draw(Pagination parent, Layout layout, List<PageUpdater> updaters, int page, int maxPages) {
        layout.elements().forEach((slot, icon) -> {
            view.set(slot, ((SpongeIcon) icon).display().provide());
            icons.put(slot, icon);
        });

        updaters.forEach(updater -> {
            switch (updater.type()) {
                case PREVIOUS:
                case FIRST:
                    if (page == 1 && parent.style() == TriState.FALSE) {
                        return;
                    }
                    break;
                case NEXT:
                case LAST:
                    if (page == maxPages && parent.style() == TriState.FALSE) {
                        return;
                    }
                    break;
            }

            int target = updater.type().translate(page, maxPages);
            Icon<ItemStack> icon = Icon.builder(ItemStack.class)
                    .display(() -> (ItemStack) updater.provider().provide(target))
                    .listener(processor -> {
                        if (!updater.type().equals(PageUpdaterType.CURRENT)) {
                            if (target == page) {
                                return false;
                            }

                            parent.page(target);
                        }
                        return false;
                    })
                    .build();

            view.set(updater.slot(), icon.display().provide());
            this.icons.put(updater.slot(), icon);
        });

        icons.forEach((slot, icon) -> view.set(slot, ((SpongeIcon) icon).display().provide()));
    }

    public ViewableInventory view() {
        return view;
    }

    public Map<Integer, Icon<?>> icons() {
        return icons;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        SpongePage that = (SpongePage) obj;
        return Objects.equals(this.view, that.view) &&
                Objects.equals(this.icons, that.icons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(view, icons);
    }

    @Override
    public String toString() {
        return "SpongePage[" +
                "view=" + view + ", " +
                "icons=" + icons + ']';
    }


}
