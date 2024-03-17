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

package net.impactdev.impactor.minecraft.scoreboard.display;

import net.impactdev.impactor.api.scoreboards.AssignedScoreboard;
import net.impactdev.impactor.api.scoreboards.ScoreboardRenderer;
import net.impactdev.impactor.api.scoreboards.display.Display;
import net.impactdev.impactor.api.scoreboards.display.Displayable;
import net.impactdev.impactor.api.scoreboards.display.text.ScoreboardComponent;
import net.impactdev.impactor.api.scoreboards.updaters.Updater;
import net.impactdev.impactor.api.scoreboards.updaters.UpdaterConfiguration;
import net.impactdev.impactor.core.utility.pointers.AbstractPointerCapable;
import net.kyori.adventure.text.Component;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractDisplay extends AbstractPointerCapable implements Display {

    private final AssignedScoreboard scoreboard;

    private final Updater updater;
    private final ScoreboardComponent component;
    protected final AtomicReference<Component> text = new AtomicReference<>(Component.empty());

    protected AbstractDisplay(AssignedScoreboard scoreboard, Displayable displayable) {
        this.scoreboard = scoreboard;
        this.component = displayable.component();
        this.text.set(this.component.resolve(this.scoreboard.viewer()));
        this.updater = Optional.ofNullable(displayable.updater()).map(UpdaterConfiguration::generate).orElse(null);
    }

    @Override
    public Component text() {
        return this.text.get();
    }

    @Override
    public Updater updater() {
        return this.updater;
    }

    protected abstract void render(AssignedScoreboard scoreboard, ScoreboardRenderer renderer);
    protected void onTick(AssignedScoreboard scoreboard) {}

    @Override
    public void tick() {
        this.text.set(this.component.resolve(this.scoreboard.viewer()));
        this.onTick(this.scoreboard);
        this.render(this.scoreboard, this.scoreboard.configuration().renderer());
    }
}
