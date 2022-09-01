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

package net.impactdev.impactor.ui.containers.views;

import net.impactdev.impactor.api.ui.containers.views.ChestView;
import net.impactdev.impactor.api.ui.containers.Layout;
import net.impactdev.impactor.api.ui.containers.processors.ClickProcessor;
import net.impactdev.impactor.api.ui.containers.processors.CloseProcessor;
import net.impactdev.impactor.api.utilities.ComponentManipulator;
import net.impactdev.impactor.api.utilities.context.Context;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

public abstract class ImpactorChestView implements ChestView {

    private final Key namespace;
    private final Component title;
    private final Layout layout;
    private final boolean readonly;

    protected final ClickProcessor click;
    protected final CloseProcessor close;

    protected ImpactorChestView(ImpactorChestViewBuilder builder) {
        this.namespace = builder.namespace;
        this.title = builder.title;
        this.layout = builder.layout;
        this.readonly = builder.readonly;
        this.click = builder.click;
        this.close = builder.close;
    }

    @Override
    public Key namespace() {
        return this.namespace;
    }

    @Override
    public Component title() {
        return this.title;
    }

    @Override
    public Layout layout() {
        return this.layout;
    }

    @Override
    public boolean readonly() {
        return this.readonly;
    }

    protected void writeException(Context context) {
        PrettyPrinter printer = new PrettyPrinter(80);
        printer.newline().add("Exception during Inventory Action").newline().hr();
        printer.add("Namespace: " + this.namespace);
        printer.add("Context:");
        printer.kv("Title", ComponentManipulator.flatten(this.title));
        printer.kv("Read Only", this.readonly);
        context.print(printer);
    }

    public abstract static class ImpactorChestViewBuilder implements ChestViewBuilder {

        private Key namespace;
        private Component title;
        private Layout layout;
        private boolean readonly = true;

        private ClickProcessor click = context -> true;
        private CloseProcessor close = context -> true;

        @Override
        public ChestViewBuilder provider(Key key) {
            this.namespace = key;
            return this;
        }

        @Override
        public ChestViewBuilder title(Component title) {
            this.title = title;
            return this;
        }

        @Override
        public ChestViewBuilder layout(Layout layout) {
            this.layout = layout;
            return this;
        }

        @Override
        public ChestViewBuilder readonly(boolean state) {
            this.readonly = state;
            return this;
        }

        @Override
        public ChestViewBuilder onClick(ClickProcessor processor) {
            this.click = processor;
            return this;
        }

        @Override
        public ChestViewBuilder onClose(CloseProcessor processor) {
            this.close = processor;
            return this;
        }

        @Override
        public ChestViewBuilder from(ChestView parent) {
            return this.provider(parent.namespace())
                    .title(parent.title())
                    .layout(parent.layout())
                    .readonly(parent.readonly())
                    .onClick(((ImpactorChestView) parent).click)
                    .onClose(((ImpactorChestView) parent).close);
        }
    }

}
