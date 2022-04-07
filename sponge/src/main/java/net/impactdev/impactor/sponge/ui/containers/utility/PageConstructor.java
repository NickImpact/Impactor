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

package net.impactdev.impactor.sponge.ui.containers.utility;

import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.pagination.Page;
import net.impactdev.impactor.api.ui.containers.pagination.Pagination;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.impactdev.impactor.sponge.ui.containers.SpongePage;
import net.impactdev.impactor.sponge.ui.containers.components.SizeMapping;
import org.spongepowered.api.item.inventory.type.ViewableInventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PageConstructor {

    public static CircularLinkedList<Page<?>> construct(List<Icon<?>> icons, Pagination parent) {
        CircularLinkedList<Page<?>> pages = new CircularLinkedList<>();
        int max = parent.zone().y() * parent.zone().x();
        int size = icons.size() / max + (icons.size() % max == 0 ? 0 : 1);

        int slot = 0;
        Map<Integer, Icon<?>> working = new HashMap<>();
        for(Icon<?> icon : icons) {
            if(slot < max) {
                int target = parent.calculateTargetSlot(slot, parent.zone(), parent.offsets());
                working.put(target, icon);
                ++slot;
            } else {
                int target = parent.calculateTargetSlot(0, parent.zone(), parent.offsets());

                constructPage(pages, parent, size, working);
                working = new HashMap<>();
                working.put(target, icon);
                slot = 1;
            }
        }

        if(!working.isEmpty()) {
            constructPage(pages, parent, size, working);
        }

        if(pages.empty()) {
            ViewableInventory view = ViewableInventory.builder()
                    .type(SizeMapping.from(parent.layout().dimensions().y()).reference())
                    .completeStructure()
                    .identity(UUID.randomUUID())
                    .plugin(SpongeImpactorPlugin.instance().bootstrapper().container())
                    .build();
            SpongePage page = new SpongePage(view, new HashMap<>());
            page.draw(parent, parent.layout(), parent.updaters(), 1, size);
            pages.append(page);
        }

        return pages;
    }

    private static void constructPage(CircularLinkedList<Page<?>> result, Pagination parent, int size, Map<Integer, Icon<?>> working) {
        ViewableInventory view = ViewableInventory.builder()
                .type(SizeMapping.from(parent.layout().dimensions().y()).reference())
                .completeStructure()
                .identity(UUID.randomUUID())
                .plugin(SpongeImpactorPlugin.instance().bootstrapper().container())
                .build();

        SpongePage page = new SpongePage(view, working);
        page.draw(parent, parent.layout(), parent.updaters(), result.size() + 1, size);
        result.append(page);
    }
}
