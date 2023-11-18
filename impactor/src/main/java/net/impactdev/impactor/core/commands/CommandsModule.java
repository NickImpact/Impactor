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

package net.impactdev.impactor.core.commands;

import net.impactdev.impactor.api.events.ImpactorEvent;
import net.impactdev.impactor.core.commands.economy.EconomyCommands;
import net.impactdev.impactor.core.commands.events.RegisterCommandsEvent;
import net.impactdev.impactor.core.commands.pagination.PaginationCommands;
import net.impactdev.impactor.core.commands.translations.TranslationCommands;
import net.impactdev.impactor.core.modules.ImpactorModule;
import net.impactdev.impactor.core.platform.commands.PlatformCommands;
import net.kyori.event.EventBus;

public final class CommandsModule implements ImpactorModule {

    @Override
    public void subscribe(EventBus<ImpactorEvent> bus) {
        bus.subscribe(RegisterCommandsEvent.class, event -> {
            event.register(EconomyCommands.class);
            event.register(PaginationCommands.class);
            event.register(TranslationCommands.class);
            event.register(PlatformCommands.class);
        });
    }
}
