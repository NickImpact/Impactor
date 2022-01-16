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

package net.impactdev.impactor.sponge.scoreboard.lines;

import net.impactdev.impactor.api.scoreboard.components.LineIdentifier;
import net.impactdev.impactor.api.scoreboard.exceptions.ScoreAlreadySetException;
import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;
import net.impactdev.impactor.sponge.scoreboard.SpongeScoreboard;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.scoreboard.objective.Objective;

import java.util.UUID;
import java.util.function.Supplier;

public abstract class AbstractSpongeSBLine implements ScoreboardLine {

    private final Component identifier;
    private final Team team;

    private Score score;

    public AbstractSpongeSBLine() {
        this.team = Team.builder().name(UUID.randomUUID().toString().substring(0, 16)).build();
        this.team.addMember(this.identifier = LineIdentifier.generate());
    }

    public void setup(Scoreboard scoreboard, ServerPlayer target) {
        scoreboard.registerTeam(this.team);
        this.team.setPrefix(this.getText());
    }

    protected static Supplier<ServerPlayer> player(UUID target) {
        return () -> Sponge.server().player(target).orElseThrow(() -> new IllegalStateException("Unable to locate target player"));
    }

    public Score getSpongeScore() {
        return this.score;
    }

    public Team getTeam() {
        return this.team;
    }

    @Override
    public int getScore() {
        return this.score.score();
    }

    public ScoreboardLine assignScore(Objective objective, int score) throws ScoreAlreadySetException {
        if(this.score != null) {
            throw new ScoreAlreadySetException();
        }
        this.score = objective.findOrCreateScore(this.identifier);
        this.score.setScore(score);
        this.score.setLocked(true);
        return this;
    }

}
