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

package net.impactdev.impactor.common.ui.pagination.types;

import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.layouts.Layout;
import net.impactdev.impactor.api.ui.containers.pagination.Pagination;
import net.impactdev.impactor.api.ui.containers.pagination.components.Page;
import net.impactdev.impactor.api.ui.containers.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;
import net.impactdev.impactor.common.ui.pagination.builders.ImpactorPaginationBuilder;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.spongepowered.math.vector.Vector2i;

import java.util.List;

public abstract class AbstractPagination implements Pagination {

    private final Key provider;
    protected final PlatformPlayer viewer;

    private final Component title;
    private final Layout layout;
    protected final boolean readonly;

    protected CircularLinkedList<Page<?>> pages;

    private final Vector2i zone;
    private final Vector2i offsets;

    private final List<PageUpdater> updaters;
    private final TriState style;

    private int page = 1;

    public AbstractPagination(ImpactorPaginationBuilder builder) {
        this.provider = builder.provider;
        this.viewer = builder.viewer;
        this.title = builder.title;
        this.layout = builder.layout;
        this.readonly = builder.readonly;
        this.zone = builder.zone;
        this.offsets = builder.offsets;
        this.updaters = builder.updaters;
        this.style = builder.updaterStyle;
    }

    @Override
    public Key provider() {
        return this.provider;
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
    public Vector2i zone() {
        return this.zone;
    }

    @Override
    public Vector2i offsets() {
        return this.offsets;
    }

    @Override
    public int page() {
        return this.page;
    }

    @Override
    public void page(int target) {
        this.page = target;
    }

    @Override
    public List<PageUpdater> updaters() {
        return this.updaters;
    }

    @Override
    public TriState style() {
        return this.style;
    }

    @Override
    public CircularLinkedList<Page<?>> pages() {
        return this.pages;
    }

    protected boolean within(int slot) {
        Vector2i location = new Vector2i(slot % 9, slot / 9);
        return this.greaterOrEqual(location, this.offsets()) &&
                this.lessOrEqual(location, this.zone().sub(this.offsets()));
    }

    private boolean greaterOrEqual(Vector2i query, Vector2i compare) {
        return query.x() >= compare.x() && query.y() >= compare.y();
    }

    private boolean lessOrEqual(Vector2i query, Vector2i compare) {
        return query.x() <= compare.x() && query.y() <= compare.y();
    }

}
