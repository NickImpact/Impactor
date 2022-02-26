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

package net.impactdev.impactor.api.ui.pagination.updaters;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
//import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
//import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.Collections;
import java.util.List;

public abstract class PageUpdater {

    private final PageUpdaterType type;
    private final int slot;
    private final Key key;

    public PageUpdater(PageUpdaterType type, int slot, Key key) {
        this.type = type;
        this.slot = slot;
        this.key = key;
    }

    public static PageUpdater.Mini of(PageUpdaterType type, int slot, Key key, String title) {
        return new PageUpdater.Mini(type, slot, key, title, Collections.emptyList());
    }

    public static PageUpdater.Mini of(PageUpdaterType type, int slot, Key key, String title, List<String> lore) {
        return new PageUpdater.Mini(type, slot, key, title, lore);
    }

    public static PageUpdater.Provided of(PageUpdaterType type, int slot, Key key, Component title) {
        return new PageUpdater.Provided(type, slot, key, title, Collections.emptyList());
    }

    public static PageUpdater.Provided of(PageUpdaterType type, int slot, Key key, Component title, List<Component> lore) {
        return new PageUpdater.Provided(type, slot, key, title, lore);
    }

    public PageUpdaterType type() {
        return this.type;
    }

    public int slot() {
        return this.slot;
    }

    public Key key() {
        return this.key;
    }

    public abstract Component title(int target);
    public abstract List<Component> lore(int target);

    public static class Mini extends PageUpdater {

        //private static final MiniMessage mini = MiniMessage.miniMessage();
        private static final MiniMessage mini = MiniMessage.get();

        private final String title;
        private final List<String> lore;

        public Mini(PageUpdaterType type, int slot, Key key, String title, List<String> lore) {
            super(type, slot, key);
            this.title = title;
            this.lore = lore;
        }

        @Override
        public Component title(int target) {
//            TagResolver template = Placeholder.parsed("target-page", "" + target);
            Template template = Template.of("target-page", "" + target);
            return mini.parse(this.title, template);
        }

        @Override
        public List<Component> lore(int target) {
//            TagResolver template = Placeholder.parsed("target-page", "" + target);
            Template template = Template.of("target-page", "" + target);
            return this.lore
                    .stream()
                    .map(line -> mini.parse(line, template))
                    .toList();
        }
    }

    public static class Provided extends PageUpdater {

        private final Component title;
        private final List<Component> lore;

        public Provided(PageUpdaterType type, int slot, Key key, Component title, List<Component> lore) {
            super(type, slot, key);
            this.title = title;
            this.lore = lore;
        }

        @Override
        public Component title(int target) {
            return this.title;
        }

        @Override
        public List<Component> lore(int target) {
            return this.lore;
        }
    }

}
