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

package net.impactdev.impactor.minecraft.scoreboard.viewed;

import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.scoreboards.Scoreboard;
import net.impactdev.impactor.scoreboards.viewed.ViewedLine;
import net.impactdev.impactor.scoreboards.viewed.ViewedObjective;
import net.impactdev.impactor.scoreboards.viewed.ViewedScoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public final class ViewedImpactorScoreboard implements ViewedScoreboard {

    private final PlatformPlayer viewer;

    private final Scoreboard delegate;
    private final ViewedObjective objective;
    private final List<ViewedLine> lines;

    public ViewedImpactorScoreboard(Scoreboard delegate, PlatformPlayer viewer) {
        this.viewer = viewer;
        this.delegate = delegate;

        this.objective = ViewedImpactorObjective.create(this.delegate.objective());
        this.lines = delegate.lines()
                .stream()
                .map(ViewedImpactorScoreboardLine::create)
                .collect(Collectors.toList());
    }

    @Override
    public Scoreboard delegate() {
        return this.delegate;
    }

    @Override
    public PlatformPlayer viewer() {
        return this.viewer;
    }

    @Override
    public ViewedObjective objective() {
        return this.objective;
    }

    @Override
    public List<ViewedLine> lines() {
        return this.lines;
    }

    @Override
    public void open() {
        this.delegate.implementation().show(this.viewer(), this.delegate());
    }

    @Override
    public void hide() {
        this.delegate.implementation().hide(this.viewer(), this.delegate());
    }

    @Override
    public void destroy() {
        this.hide();
        this.objective.delegate().resolver().shutdown();
        this.lines.forEach(line -> line.delegate().resolver().shutdown());
    }

    public static final class ViewedScoreboardFactory implements Factory {

        @Override
        public ViewedScoreboard create(@NotNull Scoreboard parent, @NotNull PlatformPlayer viewer) {
            return new ViewedImpactorScoreboard(parent, viewer);
        }

    }

}
