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

package net.impactdev.impactor.sponge.scoreboard.objective.types;

import com.google.common.base.Preconditions;
import net.impactdev.impactor.api.scoreboard.frames.types.ListeningFrame;
import net.impactdev.impactor.api.scoreboard.objective.types.ListeningObjective;
import net.impactdev.impactor.sponge.scoreboard.objective.AbstractSpongeObjective;
import net.kyori.adventure.text.Component;

public class SpongeListeningObjective extends AbstractSpongeObjective implements ListeningObjective {

    private final ListeningFrame<?> frame;

    private SpongeListeningObjective(SpongeListeningObjectiveBuilder builder) {
        this.frame = builder.frame;
    }

    @Override
    public Component getText() {
        return this.frame.getText();
    }

    @Override
    public ListeningFrame.EventHandler<?> getEventHandler() {
        return this.frame.getEventHandler();
    }

    @Override
    public void start() {
        this.frame.initialize(this);
    }

    @Override
    public void update() {
        this.getDelegate().setDisplayName(this.getText());
    }

    @Override
    public void shutdown() {
        this.frame.shutdown();
    }

    public static class SpongeListeningObjectiveBuilder implements ListeningObjectiveBuilder {

        private ListeningFrame<?> frame;

        @Override
        public ListeningObjectiveBuilder frame(ListeningFrame<?> frame) {
            this.frame = frame;
            return this;
        }

        @Override
        public ListeningObjectiveBuilder from(ListeningObjective input) {
            Preconditions.checkArgument(this.frame instanceof SpongeListeningObjective);
            this.frame = ((SpongeListeningObjective)input).frame;

            return this;
        }

        @Override
        public ListeningObjective build() {
            return new SpongeListeningObjective(this);
        }

    }

}
