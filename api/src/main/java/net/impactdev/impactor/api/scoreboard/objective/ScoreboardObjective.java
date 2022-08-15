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

package net.impactdev.impactor.api.scoreboard.objective;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.scoreboard.components.ScoreboardComponent;
import net.impactdev.impactor.api.scoreboard.objective.types.AnimatedObjective;
import net.impactdev.impactor.api.scoreboard.objective.types.ConstantObjective;
import net.impactdev.impactor.api.scoreboard.objective.types.ListeningObjective;
import net.impactdev.impactor.api.scoreboard.objective.types.RefreshingObjective;
import net.kyori.adventure.text.Component;

/**
 * Represents the objective of a Scoreboard. Effectively, for display-based scoreboards,
 * this objective acts as the title to the scoreboard. Impactor provides 4 different types of
 * objectives, which all provide different means of updating. The following types available are:
 * <ul>
 *     <li>{@link ConstantObjective Constant}</li>
 *     <li>{@link RefreshingObjective Refreshing}</li>
 *     <li>{@link ListeningObjective Listening}</li>
 *     <li>{@link AnimatedObjective Animated}</li>
 * </ul>
 *
 * In order to create any particular instance of these, you'll want to go through
 * this class. So, {@link ScoreboardObjective#constant()} will construct a builder
 * specific to creating a constant based objective.
 */
public interface ScoreboardObjective extends ScoreboardComponent<ScoreboardObjective> {

    /**
     * Gets the component representing the text for the objective.
     *
     * @return The current text for the objective type
     */
    Component getText();

    /**
     * Creates an objective builder that'll be used to configure a constant objective.
     *
     * @return A builder representing a constant objective
     */
    static ConstantObjective.ConstantObjectiveBuilder constant() {
        return Impactor.instance().builders().provide(ConstantObjective.ConstantObjectiveBuilder.class);
    }

    /**
     * Creates an objective builder that'll be used to configure a refreshing objective.
     *
     * @return A builder representing a refreshing objective
     */
    static RefreshingObjective.RefreshingObjectiveBuilder refreshing() {
        return Impactor.instance().builders().provide(RefreshingObjective.RefreshingObjectiveBuilder.class);
    }

    /**
     * Creates an objective builder that'll be used to configure a listening objective.
     *
     * @return A builder representing a listening objective
     */
    static ListeningObjective.ListeningObjectiveBuilder listening() {
        return Impactor.instance().builders().provide(ListeningObjective.ListeningObjectiveBuilder.class);
    }

    /**
     * Creates an objective builder that'll be used to configure an animated objective.
     *
     * @return A builder representing an animated objective
     */
    static AnimatedObjective.AnimatedObjectiveBuilder animated() {
        return Impactor.instance().builders().provide(AnimatedObjective.AnimatedObjectiveBuilder.class);
    }

}
