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

package net.impactdev.impactor.minecraft.scoreboard.resolvers;

import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.scheduler.SchedulerTask;
import net.impactdev.impactor.api.scoreboards.ConfigurableScoreboardComponent;
import net.impactdev.impactor.api.scoreboards.updaters.scheduled.SchedulerConfiguration;
import net.impactdev.impactor.api.scoreboards.updaters.scheduled.ScheduledResolver;
import net.kyori.adventure.text.Component;

import java.util.concurrent.atomic.AtomicReference;

public final class ImpactorScheduledResolver extends AbstractResolver implements ScheduledResolver {

    private final SchedulerConfiguration configuration;
    private final AtomicReference<Component> component;
    private final SchedulerTask task;

    private ImpactorScheduledResolver(SchedulerConfiguration configuration, ConfigurableScoreboardComponent.Viewable viewed) {
        super(viewed);
        this.configuration = configuration;
        this.task = configuration.task().schedule(configuration.scheduler(), viewed);
        this.component = new AtomicReference<>(Component.empty());
    }

    @Override
    public Component update(PlatformPlayer viewer) {
        return this.component.updateAndGet(ignore -> this.configuration.provider().resolve(displayable, viewer));
    }

    @Override
    public void shutdown() {
        this.task.cancel();
    }

    @Override
    public SchedulerTask task() {
        return this.task;
    }
}
