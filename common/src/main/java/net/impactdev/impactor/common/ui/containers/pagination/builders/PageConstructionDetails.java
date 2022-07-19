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

package net.impactdev.impactor.common.ui.containers.pagination.builders;

import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.pagination.updaters.PageUpdater;
import net.kyori.adventure.util.TriState;
import org.spongepowered.math.vector.Vector2i;

import java.util.List;

public class PageConstructionDetails<E extends Icon<?>> {

    private List<E> icons;
    private Vector2i zone;
    private Vector2i offsets;
    private List<PageUpdater> updaters;
    private TriState style;
    private int page = 1;
    private int total = 1;
    private Vector2i indexes;

    public static <E extends Icon<?>> PageConstructionDetails<E> create() {
        return new PageConstructionDetails<>();
    }

    public List<E> getIcons() {
        return icons;
    }

    public Vector2i getZone() {
        return zone;
    }

    public Vector2i getOffsets() {
        return offsets;
    }

    public List<PageUpdater> getUpdaters() {
        return updaters;
    }

    public TriState getStyle() {
        return style;
    }

    public int getPage() {
        return page;
    }

    public int getTotal() {
        return total;
    }

    public Vector2i getIndexes() {
        return indexes;
    }

    public PageConstructionDetails<E> icons(List<E> icons) {
        this.icons = icons;
        return this;
    }

    public PageConstructionDetails<E> page(int page) {
        this.page = page;
        return this;
    }

    public PageConstructionDetails<E> total(int total) {
        this.total = total;
        return this;
    }

    public PageConstructionDetails<E> indexes(int start, int end) {
        this.indexes = new Vector2i(start, end);
        return this;
    }

    public PageConstructionDetails<E> zone(Vector2i zone) {
        this.zone = zone;
        return this;
    }

    public PageConstructionDetails<E> offsets(Vector2i offsets) {
        this.offsets = offsets;
        return this;
    }

    public PageConstructionDetails<E> updaters(List<PageUpdater> updaters) {
        this.updaters = updaters;
        return this;
    }

    public PageConstructionDetails<E> style(TriState style) {
        this.style = style;
        return this;
    }

}
