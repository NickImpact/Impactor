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

import net.impactdev.impactor.api.scoreboards.resolvers.objectives.Objective;
import net.impactdev.impactor.api.scoreboards.resolvers.updaters.resolver.ComponentResolver;
import net.impactdev.impactor.api.scoreboards.players.RelativeObjective;
import net.kyori.adventure.text.Component;

public class ViewedImpactorObjective implements RelativeObjective {

    private final Objective delegate;

    private ViewedImpactorObjective(Objective delegate) {
        this.delegate = delegate;
    }

    public static ViewedImpactorObjective create(Objective objective) {
        return new ViewedImpactorObjective(objective);
    }

    @Override
    public Objective delegate() {
        return this.delegate;
    }

    @Override
    public ComponentResolver resolver() {
        return null;
    }

    @Override
    public Component text() {
//        return this.delegate.resolver().resolve(this.delegate);
        return Component.empty();
    }

    @Override
    public void update() {

    }
}
