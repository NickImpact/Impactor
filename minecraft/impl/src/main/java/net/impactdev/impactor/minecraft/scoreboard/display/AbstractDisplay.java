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

import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.scoreboards.AssignedScoreboard;
import net.impactdev.impactor.api.scoreboards.ScoreboardPointers;
import net.impactdev.impactor.api.scoreboards.ScoreboardRenderer;
import net.impactdev.impactor.api.scoreboards.display.Display;
import net.impactdev.impactor.api.scoreboards.display.resolvers.ComponentResolver;
import net.impactdev.impactor.api.utility.Context;
import net.kyori.adventure.text.Component;

public abstract class AbstractDisplay implements Display {

    private final AssignedScoreboard scoreboard;
    protected final ComponentResolver resolver;
    private Component display = Component.empty();

    protected AbstractDisplay(AssignedScoreboard scoreboard, ComponentResolver resolver) {
        this.scoreboard = scoreboard;
        this.resolver = resolver;
    }

    protected Context context() {
        return Context.empty();
    }

    @Override
    public ComponentResolver resolver() {
        return this.resolver;
    }

    @Override
    public void resolve() {
        Context context = this.context();
        context.pointer(ScoreboardPointers.ASSIGNED, this.scoreboard);
        context.pointer(PlatformPlayer.PLAYER, this.scoreboard.viewer());

        this.display = this.resolver.resolve(context);
        this.render(this.scoreboard, this.scoreboard.configuration().renderer());
    }

    protected abstract void render(AssignedScoreboard scoreboard, ScoreboardRenderer renderer);

    @Override
    public Component text() {
        return this.display;
    }
}
