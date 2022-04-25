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

package net.impactdev.impactor.sponge.ui.containers.components;

import com.google.common.collect.Maps;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.layouts.Layout;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.SectionedPagination;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.sections.Section;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.impactdev.impactor.sponge.ui.containers.icons.SpongeIcon;
import org.spongepowered.api.item.inventory.Inventory;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class LayoutTranslator {

    public static SlotContext translate(Layout layout) {
        Inventory inventory = Inventory.builder()
                .grid(layout.dimensions().x(), layout.dimensions().y())
                .completeStructure()
                .identity(UUID.randomUUID())
                .plugin(SpongeImpactorPlugin.instance().bootstrapper().container())
                .build();

        layout.elements().forEach((slot, icon) -> inventory.set(slot, ((SpongeIcon) icon).display().provide()));
        return new SlotContext(inventory.slots(), Maps.newHashMap(layout.elements()));
    }

    public static Map<Integer, Icon<?>> translate(Section section) {
        return section.pages().nextOrThrow().drawn();
    }

    public static SlotContext merge(Layout layout, Set<Section> sections) {
        Inventory inventory = Inventory.builder()
                .grid(layout.dimensions().x(), layout.dimensions().y())
                .completeStructure()
                .identity(UUID.randomUUID())
                .plugin(SpongeImpactorPlugin.instance().bootstrapper().container())
                .build();

        Map<Integer, Icon<?>> tracked = Maps.newHashMap(layout.elements());
        layout.elements().forEach((slot, icon) -> inventory.set(slot, ((SpongeIcon) icon).display().provide()));
        sections.forEach(section -> {
            Map<Integer, Icon<?>> translated = translate(section);
            translated.forEach((slot, icon) -> {
                inventory.set(slot, ((SpongeIcon) icon).display().provide());
            });
            tracked.putAll(translated);
        });

        return new SlotContext(inventory.slots(), tracked);
    }

}
