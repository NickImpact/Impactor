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

package net.impactdev.impactor.core.permissions;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.events.ImpactorEvent;
import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.api.platform.plugins.PluginMetadata;
import net.impactdev.impactor.api.services.permissions.PermissionsService;
import net.impactdev.impactor.api.services.permissions.SuggestPermissionServiceEvent;
import net.impactdev.impactor.core.modules.ImpactorModule;
import net.impactdev.impactor.core.permissions.register.PermissionsRegistrationProvider;
import net.impactdev.impactor.core.permissions.register.SuggestPermissionServiceEventImpl;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.kyori.event.EventBus;

public final class PermissionsModule implements ImpactorModule {

    @Override
    public void subscribe(EventBus<ImpactorEvent> bus) {
        bus.subscribe(SuggestPermissionServiceEvent.class, event -> {
            PluginMetadata metadata = BaseImpactorPlugin.instance().metadata();

            event.suggest(metadata, ignore -> true, NoOpPermissionsService::new, 0);
            event.suggest(
                    metadata,
                    info -> info.plugin("luckperms").isPresent(),
                    LuckPermsPermissionsService::new,
                    10
            );
        });
    }

    @Override
    public void init(Impactor impactor, PluginLogger logger) throws Exception {
        logger.info("Calculating permissions service...");

        SuggestPermissionServiceEventImpl event = new SuggestPermissionServiceEventImpl();
        impactor.events().post(event);

        PermissionsRegistrationProvider.PermissionServiceSuggestion suggestion = event.provider().suggestion();
        PermissionsService service = suggestion.supplier().get();
        logger.info("Permissions » Selected \"" + service.name() + "\" (Provider: " + suggestion.metadata().name().orElse(suggestion.metadata().id()) + ", Priority = " + suggestion.priority() + ")");
    }
}
