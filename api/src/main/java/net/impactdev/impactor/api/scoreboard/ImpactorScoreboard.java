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

package net.impactdev.impactor.api.scoreboard;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.builders.Builder;
import net.impactdev.impactor.api.scoreboard.frames.ScoreboardFrame;
import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboard.objective.ScoreboardObjective;
import net.impactdev.impactor.api.utilities.context.Context;

import java.util.List;
import java.util.Map;

/**
 * Represents a scoreboard that appears on the side of a user's screen. This scoreboard is built amongst
 * a set of {@link ScoreboardLine lines} as well as an {@link ScoreboardObjective objective} (title).
 * The scoreboard implementations feature non-flicker variants with placeholder parsing support, alongside
 * full support for hex color codes.
 *
 * <p>As a means of efficiency as well as convenience, the objective and each line can have their own
 * means of updating, as well as how they update. For instance, you can have a line that features
 * no updating whatsoever, to save resources for ticking a line that otherwise would never change.
 * Additionally, outside running animations and lines that refresh their contents based on a schedule,
 * the API allows for updating based on event occurrences. For example, imagine a scoreboard with the
 * following creation design (excuse formatting, 80 character limits are fun):
 *
 * <p>
 * <h2>Creating a Scoreboard</h2>
 * To create a scoreboard, you'll want to look at {@link ScoreboardLine}, {@link ScoreboardObjective},
 * and {@link ScoreboardFrame}. These three components provide the means of accessing the many types of
 * line components provided by the Impactor API. This rule of thumb only applies to using the Impactor
 * specific providers. Third-party providers can be constructed as valid components where fit.
 *
 * <p>Below, you can find an example for how to create a scoreboard with this API:
 * <pre>
 * private final ImpactorScoreboard&lt;ServerPlayer&gt; scoreboard = ImpactorScoreboard.&lt;ServerPlayer&gt;builder()
 *             .objective(ScoreboardObjective.constant()
 *                  .text(Component.text("Impactor Scoreboard Demo")
 *                      .color(TextColor.color(255, 215, 0))
 *                  )
 *                  .build()
 *              )
 *             .line(ScoreboardLine.refreshing()
 *                  .text(StringUtils.repeat("\u25A0", 30))
 *                  .rate(1)
 *                  .effects(RGBFadeEffect.builder()
 *                      .frames(90)
 *                      .step(3)
 *                      .start(0)
 *                      .build()
 *                  )
 *                  .async()
 *                  .build(), 15
 *             )
 *             .build()
 * </pre>
 * The following code will create a scoreboard with an objective title, which remains static throughout
 * the scoreboard's lifetime, with the text: Impactor Scoreboard Demo, with a Yellow Gold color. Then,
 * the only line of this example demonstrates a line which refreshes asynchronously, at a rate of 1 tick,
 * with a line that is simply a set of 30 squares. These squares are then run through an RGB fade effect
 * which runs through the visible color spectrum, with each square being a unique color in the set. The
 * line will also be assigned a score of 15, per the integer specified as the second argument to line().
 *
 * <p>Note that the scoreboard is generic, and that's to allow for specification of the type of player that
 * this scoreboard can be assigned to for a particular platform. In this case, the example makes use of
 * ServerPlayer, provided by Sponge API 8.
 *
 * <p>
 * <h2>Assigning a Scoreboard to a Player</h2>
 * To assign a scoreboard to a particular player, you'll want to make use of {@link #assignTo(Object)},
 * where the provided object is the instance of the player that will be viewing that scoreboard.
 *
 * <p>Once assigned, that scoreboard, unless otherwise specified, will be reflective upon them for player
 * specific placeholders. Additionally, this will produce a copy of this scoreboard, so it's expected
 * the user of the API will track scoreboard instances via some form of cache, whether that be just a
 * maintained {@link Map}, or even a cache from Caffeine.
 *
 * <p>
 * <h2>Placeholder Parsing</h2>
 * Most components of the scoreboard API allows for placeholder sources to be provided for possible
 * placeholder parsing. These sources are provided through the {@link Context} interface,
 * which allows for binding a class type with a supplier for the actual object. For all components,
 * the scoreboard will attempt to provide a source representing the viewing player, as long as no
 * currently provided player is already available for that line. So, you can have a line on a scoreboard
 * focus on a player other than the viewer themselves.
 *
 * @param <P> The type of player represented by the platform
 * @since 4.0.0
 */
public interface ImpactorScoreboard<P> {

    /**
     * Represents the title of the scoreboard.
     *
     * @return The scoreboard's title
     */
    ScoreboardObjective getTitle();

    /**
     * Represents the set of lines that a scoreboard will contain.
     *
     * <p>NOTE: A scoreboard is limited to 16 lines, excluding the title. These lines are controlled via their
     * score, set via the respective builder per each type of scoreboard line. If two lines have the same score,
     * the line specified last will overtake the first.
     *
     * @return The scoreboard's lines
     */
    List<ScoreboardLine> getLines();

    /**
     * Applies the scoreboard to the user with the following UUID. This user will be the focus of
     * contextual placeholders that expect an active player for resolution.
     *
     * @param player The user this scoreboard will be assigned to.
     * @return A clone of the current scoreboard, with the relative player assignment
     */
    ImpactorScoreboard<P> assignTo(P player);

    /**
     * Attempts to display the scoreboard to the player.
     *
     * @return <code>true</code> if successfully shown, <code>false</code> if the scoreboard is already
     * being shown or failed to be shown
     */
    boolean show();

    /**
     * Attempts to hide the scoreboard to the player.
     *
     * @return <code>true</code> if successfully hidden, <code>false</code> if the scoreboard is already
     * being hidden or failed to be hidden
     */
    boolean hide();

    /**
     * Enables animations to begin running for the scoreboard.
     */
    void start();

    /**
     * Disables further updating for a scoreboard, effectively cancelling any running tasks
     * responsible for updating values for the particular scoreboard.
     *
     * The typical time to call this is when the player disconnects from the server, as there's no reason
     * to continue trying to update a scoreboard when the player no longer exists. However, this can be
     * called earlier should the need become necessary.
     */
    void shutdown();

    /**
     * Begins construction of a new scoreboard.
     *
     * @return A newly constructed scoreboard builder.
     */
    static <P> ScoreboardBuilder<P> builder() {
        return Impactor.instance().builders().provide(ScoreboardBuilder.class);
    }

    /**
     * Allows for dynamic building of a scoreboard. This builder is responsible for providing the
     * objective of the scoreboard, then delegating to a sub-builder to specify lines. While a build
     * method is provided by this builder, it is expected to throw an UnsupportedOperationException
     * due to the fact that a Scoreboard will require AT LEAST one line to be visible to any
     * assigned player. Therefore, any use of build() from this builder should be delegated to
     * {@link LinesComponentBuilder#build()} via the configuration of the objective.
     *
     * @param <P> The type of player represented by the platform
     */
    interface ScoreboardBuilder<P> extends Builder<ImpactorScoreboard<P>> {

        /**
         * Specifies the objective/title of the scoreboard.
         *
         * @param objective The objective for the scoreboard
         * @return This builder
         */
        LinesComponentBuilder<P> objective(ScoreboardObjective objective);

    }

    /**
     *
     *
     * @param <P> The type of player represented by the platform
     */
    interface LinesComponentBuilder<P> extends Builder<ImpactorScoreboard<P>> {

        /**
         * Appends a single line to the scoreboard. If its score matches another line
         * already added to this scoreboard, the earlier line will ultimately
         * be replaced when constructed.
         *
         * @param line The line to append
         * @param score The score to assign to the line
         * @return This builder
         */
        LinesComponentBuilder<P> line(ScoreboardLine line, int score);

        /**
         * Appends a set of scoreboard lines to the scoreboard.
         *
         * @param lines The line set
         * @return This builder
         */
        LinesComponentBuilder<P> lines(Map<ScoreboardLine, Integer> lines);

    }

}
