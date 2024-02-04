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

package net.impactdev.impactor.core.mail.registration;

import net.impactdev.impactor.api.mail.MailService;
import net.impactdev.impactor.api.mail.events.SuggestMailServiceEvent;
import net.impactdev.impactor.api.platform.plugins.PluginMetadata;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import org.jetbrains.annotations.Range;

import java.util.function.Supplier;

public class ImpactorSuggestMailServiceEvent implements SuggestMailServiceEvent {

    private final MailServiceRegistrationProvider provider;

    public ImpactorSuggestMailServiceEvent(MailServiceRegistrationProvider provider) {
        this.provider = provider;
    }

    @Override
    @SuppressWarnings("ConstantValue")
    public void suggest(
            final PluginMetadata suggestor,
            final Supplier<MailService> service,
            final @Range(from = 0, to = Integer.MAX_VALUE) int priority
    ) {
        if(priority < 0) {
            BaseImpactorPlugin.instance().logger().warn(suggestor.name() + " attempted to suggest their mail service" +
                    "with a priority lower than 0 (" + priority + "), this suggestion has been ignored!");

            return;
        }

        this.provider.suggest(suggestor, priority, service);
    }
}
