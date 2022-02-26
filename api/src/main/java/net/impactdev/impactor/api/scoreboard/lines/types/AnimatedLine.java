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

package net.impactdev.impactor.api.scoreboard.lines.types;

import net.impactdev.impactor.api.scoreboard.components.ScoreboardAnimation;
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.frames.types.ListeningFrame;
import net.impactdev.impactor.api.scoreboard.frames.types.RefreshingFrame;
import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;

/**
 * Represents a line on a scoreboard that is configured with an animation that'll cycle through
 * a set of attached ScoreboardFrames. This animation acts in the order in which the frames are
 * appended to it, cycling back to the head as soon as it is finished with the last frame.
 *
 * The frames themselves control whether they will update on a queued animation refresh
 * interval. For instance, a {@link RefreshingFrame} will update every time the animated
 * requests an update. In contrast, a {@link ListeningFrame} will not actually update every
 * iteration of the animation, but on its own accord.
 */
public interface AnimatedLine extends ScoreboardLine, Updatable, ScoreboardAnimation {

    /**
     * Constructs a builder that handles the configuration of an animated line. This directly
     * inherits from AnimationBuilder, and does not add any differences as compared to an
     * objective.
     *
     * @see AnimationBuilder
     */
    interface AnimatedBuilder extends ScoreboardAnimation.AnimationBuilder<AnimatedLine> {}

}
