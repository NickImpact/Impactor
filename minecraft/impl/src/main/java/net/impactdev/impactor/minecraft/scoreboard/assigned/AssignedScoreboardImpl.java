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

package net.impactdev.impactor.minecraft.scoreboard.assigned;

import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.scoreboards.AssignedScoreboard;
import net.impactdev.impactor.api.scoreboards.Scoreboard;
import net.impactdev.impactor.api.scoreboards.ScoreboardRenderer;
import net.impactdev.impactor.api.scoreboards.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboards.objectives.Objective;
import net.impactdev.impactor.core.utility.pointers.AbstractPointerCapable;
import net.impactdev.impactor.minecraft.scoreboard.display.lines.ImpactorScoreboardLine;
import net.impactdev.impactor.minecraft.scoreboard.display.objectives.ImpactorObjective;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class AssignedScoreboardImpl extends AbstractPointerCapable implements AssignedScoreboard {

    private final Scoreboard config;
    private final PlatformPlayer viewer;

    private final ScoreboardRenderer renderer;
    private final Objective.Displayed objective;
    private final List<ScoreboardLine.Displayed> lines;

    private final ColorSelector colors = new ColorSelector();

    public AssignedScoreboardImpl(Scoreboard config, PlatformPlayer viewer) {
        this.config = config;
        this.viewer = viewer;

        this.renderer = config.renderer();

        this.objective = this.translate(config.objective(), ImpactorObjective.class).create(this);
        this.lines = config.lines().stream()
                .map(input -> this.translate(input, ImpactorScoreboardLine.class).create(this))
                .toList();
    }

    @Override
    public Scoreboard configuration() {
        return this.config;
    }

    @Override
    public PlatformPlayer viewer() {
        return this.viewer;
    }

    @Override
    public Objective.Displayed objective() {
        return this.objective;
    }

    @Override
    public List<ScoreboardLine.Displayed> lines() {
        return this.lines;
    }

    @Override
    public void open() {
        this.renderer.show(this);
        this.objective().resolver().start(this.objective());
        this.lines().forEach(line -> line.resolver().start(line));
    }

    @Override
    public void hide() {
        this.objective().resolver().shutdown(this.objective());
        this.lines().forEach(line -> line.resolver().shutdown(line));
        this.renderer.hide(this);
    }

    @Override
    public void destroy() {
        this.hide();
    }

    public ColorSelector colors() {
        return this.colors;
    }

    private <I, T extends I> T translate(I input, Class<T> target) {
        return target.cast(input);
    }

    public static final class ColorSelector {

        private int index = 0;

        public ChatFormatting select() {
            return ChatFormatting.values()[this.index++];
        }

    }
}
