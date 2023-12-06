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

package net.impactdev.impactor.minecraft.scoreboard.text;

import com.google.common.collect.ImmutableList;
import net.impactdev.impactor.api.platform.sources.PlatformSource;
import net.impactdev.impactor.api.scoreboards.display.formatters.DisplayFormatter;
import net.impactdev.impactor.api.scoreboards.display.resolvers.text.ComponentElement;
import net.impactdev.impactor.api.scoreboards.display.resolvers.text.ScoreboardComponent;
import net.impactdev.impactor.api.utility.Context;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Optional;

public final class ImpactorScoreboardComponent implements ScoreboardComponent {

    private final List<ComponentElement> elements;

    private ImpactorScoreboardComponent(ComponentElement root) {
        this.elements = ImmutableList.of(root);
    }

    private ImpactorScoreboardComponent(List<ComponentElement> elements, ComponentElement child) {
        this.elements = ImmutableList.<ComponentElement>builder()
                .addAll(elements)
                .add(child)
                .build();
    }

    @Override
    public Component resolve(PlatformSource viewer, Context context) {
        return this.elements.stream()
                .map(element -> Optional.ofNullable(element.formatter())
                        .map(formatter -> {
                            Component result = formatter.format(element.provider().parse(viewer, context));

                            if(formatter instanceof DisplayFormatter.Stateful stateful) {
                                stateful.step();
                            }

                            return result;
                        })
                        .orElse(element.provider().parse(viewer, context))
                )
                .reduce(Component::append)
                .orElse(Component.empty());
    }

    @Override
    public ScoreboardComponent append(ComponentElement element) {
        return new ImpactorScoreboardComponent(this.elements, element);
    }

    @Override
    public List<ComponentElement> elements() {
        return this.elements;
    }

    public static final class ScoreboardComponentFactory implements Factory {

        @Override
        public ScoreboardComponent create(ComponentElement root) {
            return new ImpactorScoreboardComponent(root);
        }

    }
}
