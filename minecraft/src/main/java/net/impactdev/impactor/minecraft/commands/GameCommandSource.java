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

package net.impactdev.impactor.minecraft.commands;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.platform.sources.PlatformSource;
import net.impactdev.impactor.core.commands.sources.ImpactorCommandSource;
import net.impactdev.impactor.minecraft.platform.GamePlatform;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

public class GameCommandSource extends ImpactorCommandSource {

    public GameCommandSource(PlatformSource source) {
        super(source);
    }

    @Override
    public boolean communicates(Communicator communicator) {
        if(this.isPlayer()) {
            return true;
        }

        MinecraftServer server = ((GamePlatform) Impactor.instance().platform()).server();
        if(this.isConsole()) {
            switch (communicator) {
                case SUCCESS:
                case FAILURE:
                    return true;
                case INFORM_ADMINS:
                    return server.shouldInformAdmins();
            }
        }

        for(ServerLevel level : server.getAllLevels()) {
            CommandSource entity = level.getEntity(this.uuid());
            if(entity != null) {
                switch (communicator) {
                    case SUCCESS:
                        return entity.acceptsSuccess();
                    case FAILURE:
                        return entity.acceptsFailure();
                    case INFORM_ADMINS:
                        return entity.shouldInformAdmins();
                }
            }
        }

        return false;
    }

}
