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
import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboard.objective.ScoreboardObjective;
import net.impactdev.impactor.api.utilities.Builder;

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
 * <pre>
 * ImpactorScoreboard scoreboard = ImpactorScoreboard.builder()
 *   .objective(ScoreboardObjective.listening()
 *      .frame(ScoreboardFrame.listening(
 *             TypeToken.get(ServerSideConnectionEvent.class)
 *         )
 *         .text("&e&lImpactDev &7(&b{{impactor:player_count}}&7)")
 *         .bus(PlatformBus.getOrCreate())
 *         .handler((updatable, event) -> {
 *            if(event instanceof ServerSideConnectionEvent.Join
 *               || event instanceof ServerSideConnectionEvent.Disconnect) {
 *               Sponge.server().scheduler().submit(Task.builder()
 *                 .plugin(plugin)
 *                 .execute(updatable::update)
 *                 .delay(Ticks.of(1))
 *                 .build()
 *               );
 *            }
 *         })
 *         .sources(sources)
 *         .build()
 *      )
 *      .build()
 *   )
 * </pre>
 *
 * This scoreboard will come with an {@link ScoreboardObjective} that updates its contents for
 * every instance of both login and disconnect events (Event is based on Sponge). Otherwise, the
 * title of the scoreboard remains static.
 *
 * @param <P> The type of player represented by the platform
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
        return Impactor.getInstance().getRegistry().createBuilder(ScoreboardBuilder.class);
    }

    /**
     * Allows for dynamic building of a scoreboard
     */
    interface ScoreboardBuilder<P> extends Builder<ImpactorScoreboard<P>, ScoreboardBuilder<P>> {

        /**
         * Specifies the objective/title of the scoreboard.
         *
         * @param objective The objective for the scoreboard
         * @return This builder
         */
        LinesComponentBuilder<P> objective(ScoreboardObjective objective);

    }

    interface LinesComponentBuilder<P> {

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

        ScoreboardBuilder<P> complete();

    }

}
