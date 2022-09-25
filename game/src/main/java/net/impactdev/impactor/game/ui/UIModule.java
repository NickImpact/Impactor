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

package net.impactdev.impactor.game.ui;

import net.impactdev.impactor.api.providers.BuilderProvider;
import net.impactdev.impactor.api.providers.FactoryProvider;
import net.impactdev.impactor.api.providers.ServiceProvider;
import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.layouts.ChestLayout;
import net.impactdev.impactor.api.ui.containers.views.pagination.rules.ContextRuleset;
import net.impactdev.impactor.game.ui.containers.ImpactorIcon;
import net.impactdev.impactor.game.ui.containers.layouts.ImpactorChestLayout;
import net.impactdev.impactor.game.ui.containers.views.pagination.ImpactorContextRuleset;
import net.impactdev.impactor.game.ui.containers.views.pagination.layers.ImpactorPage;
import net.impactdev.impactor.modules.ImpactorModule;

@SuppressWarnings("unused")
public class UIModule implements ImpactorModule {

    @Override
    public void factories(FactoryProvider provider) {
        provider.register(ContextRuleset.Factory.class, new ImpactorContextRuleset.ContextRulesetFactory());
    }

    @Override
    public void builders(BuilderProvider provider) {
        provider.register(Icon.IconBuilder.class, ImpactorIcon.ImpactorIconBuilder::new);
        provider.register(ImpactorPage.ImpactorPageBuilder.class, ImpactorPage.ImpactorPageBuilder::new);

        // Layouts
        provider.register(ChestLayout.ChestLayoutBuilder.class, ImpactorChestLayout.ImpactorChestLayoutBuilder::new);
    }

    @Override
    public void services(ServiceProvider provider) {

    }

}
