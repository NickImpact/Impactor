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

package net.impactdev.impactor.game.plugin;

import com.google.common.collect.Sets;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.commands.CommandRegistrationEvent;
import net.impactdev.impactor.commands.CommandsModule;
import net.impactdev.impactor.commands.dev.items.BookCommand;
import net.impactdev.impactor.commands.dev.items.ItemKeyArgument;
import net.impactdev.impactor.commands.dev.items.skulls.SkullSkinArgument;
import net.impactdev.impactor.commands.dev.items.skulls.SkullTextureArgument;
import net.impactdev.impactor.commands.dev.messages.ActionBar;
import net.impactdev.impactor.commands.dev.messages.Chat;
import net.impactdev.impactor.commands.dev.messages.Title;
import net.impactdev.impactor.commands.dev.performance.PerformanceCheck;
import net.impactdev.impactor.commands.dev.player.PlayerLocaleTest;
import net.impactdev.impactor.game.items.ItemsModule;
import net.impactdev.impactor.game.ui.UIModule;
import net.impactdev.impactor.modules.ImpactorModule;
import net.impactdev.impactor.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.plugin.ImpactorBootstrapper;

import java.util.Set;

public abstract class GameImpactorPlugin extends BaseImpactorPlugin {

    public GameImpactorPlugin(ImpactorBootstrapper bootstrapper) {
        super(bootstrapper);
    }

    @Override
    protected Set<Class<? extends ImpactorModule>> modules() {
        return Sets.newHashSet(
                ItemsModule.class,
                UIModule.class,
                CommandsModule.class
        );
    }

    @Override
    public void construct() throws Exception {
        super.construct();

        Impactor.instance().events().subscribe(CommandRegistrationEvent.class, event -> {
            this.logger().info("Received registration event, generating...");
            event.register(
                    new ItemKeyArgument(),
                    new BookCommand(),
                    new SkullSkinArgument(),
                    new SkullTextureArgument(),
                    new PlayerLocaleTest(),
                    new PerformanceCheck(),
                    new Chat(),
                    new ActionBar(),
                    new Title()
            );
        });
    }
}
