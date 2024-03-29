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

package net.impactdev.impactor.minecraft.ui.containers.views;

import com.google.common.base.Suppliers;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.containers.View;
import net.impactdev.impactor.api.ui.containers.processors.ClickProcessor;
import net.impactdev.impactor.api.ui.containers.processors.CloseProcessor;
import net.impactdev.impactor.api.ui.metadata.UIMetadataKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

import java.util.Optional;

public abstract class ImpactorView implements View {

    protected final Key namespace;
    protected final Component title;
    protected final boolean readonly;

    protected final ClickProcessor click;
    protected final CloseProcessor close;

    protected ImpactorView(Key namespace, Component title, boolean readonly, ClickProcessor click, CloseProcessor close) {
        this.namespace = Optional.ofNullable(namespace).orElseThrow(() -> new IllegalStateException("Provider was not specified"));
        this.title = title;
        this.readonly = readonly;
        this.click = click;
        this.close = close;
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
    public boolean readonly() {
        return this.readonly;
    }

    public ClickProcessor clickProcessor() {
        return this.click;
    }

    public CloseProcessor closeProcessor() {
        return this.close;
    }

    @Override
    public void open(PlatformPlayer viewer) {
        viewer.offer(UIMetadataKeys.OPENED_VIEW, Suppliers.memoize(() -> this));
    }

    @Override
    public void close(PlatformPlayer viewer) {
        viewer.offer(UIMetadataKeys.OPENED_VIEW, null);
    }

}
