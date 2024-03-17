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

package net.impactdev.impactor.minecraft.test.scoreboards;

import net.impactdev.impactor.api.scoreboards.AssignedScoreboard;
import net.impactdev.impactor.api.scoreboards.ScoreboardRenderer;
import net.impactdev.impactor.api.scoreboards.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboards.objectives.Objective;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ansi.ANSIComponentSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.Optional;

public class TestRenderer implements ScoreboardRenderer {

    public final Logger logger = LoggerFactory.getLogger("Impactor");
    private final Marker marker = MarkerFactory.getMarker("Scoreboards");

    private final ANSIComponentSerializer serializer = ANSIComponentSerializer.ansi();

    @Override
    public void objective(AssignedScoreboard scoreboard, Objective.Displayed objective) {
        this.logger.info(this.marker, this.serializer.serialize(objective.text()));
    }

    @Override
    public void line(AssignedScoreboard scoreboard, ScoreboardLine.Displayed line) {
        this.logger.info(this.marker, this.serializer.serialize(line.text()));

        Component score = Optional.ofNullable(line.delegate().score().formatter())
                .map(formatter -> formatter.format(line.delegate().score().value()))
                .orElse(Component.text(line.delegate().score().value()));
        this.logger.info(this.marker, "- Score: {}", this.serializer.serialize(score));
        this.logger.info(this.marker, "  * Formatted: {}", line.delegate().score().formatter() != null);
        this.logger.info(this.marker, "");
    }

    @Override
    public void createTeam(AssignedScoreboard scoreboard, ScoreboardLine.Displayed line) {

    }

    @Override
    public void destroyTeam(AssignedScoreboard scoreboard, ScoreboardLine.Displayed line) {

    }

    @Override
    public void show(AssignedScoreboard scoreboard) {

    }

    @Override
    public void hide(AssignedScoreboard scoreboard) {

    }

}
