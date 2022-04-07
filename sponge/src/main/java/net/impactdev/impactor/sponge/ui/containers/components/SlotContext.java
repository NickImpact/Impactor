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

import net.impactdev.impactor.api.ui.containers.icons.Icon;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.math.vector.Vector2i;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class SlotContext {

    /** A set of slots representing the entire inventory */
    private final List<Slot> slots;

    /** Only the mapping of slot positions to icons that we have placed and should track */
    private final Map<Integer, Icon<?>> tracked;

    public SlotContext(List<Slot> slots, Map<Integer, Icon<?>> tracked) {
        this.slots = slots;
        this.tracked = tracked;
    }

    public List<Slot> slots() {
        return this.slots;
    }

    public SlotContext append(Slot slot) {
        this.slots.add(slot);
        return this;
    }

    public SlotContext appendAll(List<Slot> slots) {
        this.slots.addAll(slots);
        return this;
    }

    public Map<Integer, Icon<?>> tracked() {
        return this.tracked;
    }

    public SlotContext track(int slot, @Nullable Icon<?> icon) {
        if(icon == null) {
            this.tracked.remove(slot);
        } else {
            this.tracked.put(slot, icon);
        }

        return this;
    }

    public SlotContext trackAll(Vector2i start, Vector2i size, Map<Integer, Icon<?>> icons) {
        for(int y = start.y(); y < size.y() + start.y(); y++) {
            for(int x = start.x(); x < size.x() + start.x(); x++) {
                int slot = x + (9 * y);
                this.track(slot, icons.get(slot));
            }
        }
        return this;
    }

    public Optional<Icon<?>> locate(int slot) {
        return Optional.ofNullable(this.tracked.get(slot));
    }
}
