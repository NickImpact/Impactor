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

package net.impactdev.impactor.minecraft.scoreboard.assigned;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.impactdev.impactor.minecraft.api.text.AdventureTranslator;
import net.kyori.adventure.text.Component;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.List;

public final class ScoreboardComponents {

    public static final Scoreboard SCOREBOARD = new Scoreboard();
    public static final String OBJECTIVE_NAME = "»Impactor Objective«";
    public static final String TEAM_NAME_PREFIX = "»Impactor Team - ";

    public static final Objective OBJECTIVE = new Objective(
            SCOREBOARD,
            OBJECTIVE_NAME,
            ObjectiveCriteria.DUMMY,
            AdventureTranslator.toNative(Component.text("Dummy Objective")),
            ObjectiveCriteria.RenderType.INTEGER
    );
    private static final List<PlayerTeam> TEAMS = Lists.newArrayList();
    private static final List<String> FAKE_PLAYER_NAMES = Lists.newArrayList();

    static {
        for(int i = 0; i < 15; i++) {
            String name = String.format("§%s§r", Integer.toHexString(i));
            PlayerTeam team = new PlayerTeam(SCOREBOARD, TEAM_NAME_PREFIX + i);
            SCOREBOARD.addPlayerToTeam(name, team);

            TEAMS.add(team);
            FAKE_PLAYER_NAMES.add(name);
        }
    }

    public static PlayerTeam team(int index) {
        Preconditions.checkArgument(index >= 0 && index < 15);
        return TEAMS.get(index);
    }

    public static String fakeName(int index) {
        Preconditions.checkArgument(index >= 0 && index < 15);
        return FAKE_PLAYER_NAMES.get(index);
    }

}
