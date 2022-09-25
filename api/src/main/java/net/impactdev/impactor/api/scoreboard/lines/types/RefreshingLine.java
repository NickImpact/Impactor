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

import net.impactdev.impactor.api.builders.Builder;
import net.impactdev.impactor.api.scoreboard.components.TimeConfiguration;
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.effects.FrameEffect;
import net.impactdev.impactor.api.scoreboard.effects.RGBFadeEffect;
import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;
import net.impactdev.impactor.api.utilities.context.Context;
import net.kyori.adventure.text.Component;

import java.util.concurrent.TimeUnit;

/**
 * This line is capable of updating its contents over a configured rate of time. This line also allows for its
 * scheduler to be updated asynchronously, to help with server thread management. If you choose to make use
 * of this feature, be sure you provide a thread safe configuration.
 */
public interface RefreshingLine extends ScoreboardLine, Updatable {

    /**
     * Indicates the configuration for line updates for this line.
     *
     * @return the timings config
     */
    TimeConfiguration getTimingConfig();

    interface RefreshingLineBuilder extends Builder<RefreshingLine> {

        /**
         * Indicates a set of raw text that will be parsed into a {@link Component} every
         * update. This can include color codes or placeholders.
         *
         * @param raw The text to parse every update
         * @return The builder
         */
        RefreshingLineBuilder text(String raw);

        /**
         * Allows for providing an effect that can be applied to the line. For example, there is a
         * {@link RGBFadeEffect} which draws the favorable RGB rainbow. These can stack, but some effects
         * might not work well with others!
         *
         * @param effects The effects to be applied to the line
         * @return The builder
         */
        RefreshingLineBuilder effects(FrameEffect... effects);

        /**
         * Indicates how quickly the line will update in a set amount of ticks. This is based on the game clock
         * of 20 ticks / second. Note that if a game delay occurs, this line could be forced to wait longer
         * than anticipated. If configured with {@link #async()}, this will convert the amount of ticks specified
         * to 50 millisecond intervals representing a singular tick.
         *
         * @param ticks The amount of ticks to wait before updating this line
         * @return The builder
         */
        RefreshingLineBuilder rate(long ticks);

        /**
         * Indicates how quickly the line will update based on a unit of time. Unlike {@link #rate(long)}, this
         * method can avoid a potential tick skipping scenario, but is still vulnerable to delays due to game
         * processing. This can be most likely avoided via an async configuration, but no guarantee is made.
         *
         * @param duration The time to wait, reflective of the given time unit
         * @param unit The unit of measure for the duration of time
         * @return The builder
         */
        RefreshingLineBuilder rate(long duration, TimeUnit unit);

        /**
         * Marks that this line should update its contents asynchronously.
         *
         * @return The builder
         */
        RefreshingLineBuilder async();

        /**
         * Allows for providing a set of sources for the line that can be parsed. If not called,
         * this will be substituted with {@link Context#empty()}. When assigned to a
         * player, these sources will append the viewing player to the source list, so long
         * as a player is not already provided in the source stack.
         *
         * @param context The sources placeholders should use outside of the viewing player
         * @return The builder
         */
        RefreshingLineBuilder sources(Context context);

    }

}
