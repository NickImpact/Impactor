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

package net.impactdev.impactor.core.text;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.api.providers.BuilderProvider;
import net.impactdev.impactor.api.providers.FactoryProvider;
import net.impactdev.impactor.api.providers.ServiceProvider;
import net.impactdev.impactor.api.text.TextProcessor;
import net.impactdev.impactor.api.text.pagination.PaginatedText;
import net.impactdev.impactor.api.text.placeholders.PlaceholderService;
import net.impactdev.impactor.api.text.transforming.TransformableText;
import net.impactdev.impactor.core.modules.ImpactorModule;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.core.text.pagination.ImpactorPaginatedText;
import net.impactdev.impactor.core.text.pagination.PaginationService;
import net.impactdev.impactor.core.text.placeholders.ImpactorPlaceholderService;
import net.impactdev.impactor.core.text.placeholders.ImpactorRegisterPlaceholdersEvent;
import net.impactdev.impactor.core.text.transforming.TransformableTextImpl;
import net.impactdev.impactor.core.utility.events.EventPublisher;
import net.kyori.event.PostResult;

public final class TextModule implements ImpactorModule {
    @Override
    public void factories(FactoryProvider provider) {
        provider.register(TextProcessor.Factory.class, new TextProcessorFactory());
    }

    @Override
    public void services(ServiceProvider provider) {
        provider.register(PlaceholderService.class, new ImpactorPlaceholderService());
        provider.register(PaginationService.class, new PaginationService());
    }

    @Override
    public void builders(BuilderProvider provider) {
        provider.register(PaginatedText.PaginatedTextBuilder.class, ImpactorPaginatedText.ImpactorPaginationBuilder::new);
        provider.register(TransformableText.TransformableTextBuilder.class, TransformableTextImpl.TransformableTextImplBuilder::new);
    }

    @Override
    public void init(Impactor impactor, PluginLogger logger) throws Exception {
        Impactor api = Impactor.instance();
        PlaceholderService service = api.services().provide(PlaceholderService.class);

        BaseImpactorPlugin.instance().logger().info("Firing placeholder registration event");

        EventPublisher.post(new ImpactorRegisterPlaceholdersEvent(service));
    }
}
