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

package net.impactdev.impactor.sponge.ui.icons;

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
