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

package net.impactdev.impactor.testing.ui;

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.items.types.ItemTypes;
import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.Layout;
import net.impactdev.impactor.api.ui.containers.views.pagination.Pagination;
import net.impactdev.impactor.api.ui.containers.views.pagination.rules.ContextRuleset;
import net.impactdev.impactor.testing.ui.provided.TestViewProvider;
import net.impactdev.impactor.ui.containers.views.service.ViewingService;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.spongepowered.math.vector.Vector2i;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaginationTests {

    private static final Key TEST_KEY = Key.key("impactor", "test");

    @BeforeAll
    public static void initialize() {
        Impactor.instance().services().register(ViewingService.class, new TestViewProvider());
    }

    @Test
    public void build() {
        Pagination test = Pagination.builder()
                .provider(TEST_KEY)
                .layout(this.basicLayout())
                .zone(Vector2i.from(7, 4), Vector2i.ONE)
                .build();

        assertEquals(Key.key("impactor", "test"), test.namespace());
        assertEquals(Vector2i.from(7, 4), test.zone());
        assertNotNull(test.updaters());
        assertEquals(TriState.NOT_SET, test.style());
        assertEquals(1, test.page());
        assertNotNull(test.pages());
    }

    @Test
    public void buildWithContext() {
        ImpactorItemStack dummy = ImpactorItemStack.empty();
        ContextRuleset rules = ContextRuleset.create();
        rules.setFilter(icon -> icon.context().has(int.class) && icon.context().require(int.class) < 6);
        List<Icon> icons = Lists.newArrayList();
        for(int i = 0; i < 10; i++) {
            icons.add(Icon.builder()
                    .display(() -> dummy)
                    .append(int.class, i + 1)
                    .constant()
                    .build()
            );
        }

        Pagination test = Pagination.builder()
                .provider(TEST_KEY)
                .contents(icons)
                .layout(this.basicLayout())
                .zone(Vector2i.from(7, 4), Vector2i.ONE)
                .ruleset(rules)
                .build();

        Stream<Icon> stream = icons.stream();
        stream = rules.filter(stream);
        stream = rules.sort(stream);
        List<Icon> results = stream.collect(Collectors.toList());
        assertEquals(5, results.size());
        assertEquals(1, results.get(0).context().require(int.class));
    }

    private Layout basicLayout() {
        return Layout.builder()
                .border(Icon.builder()
                        .display(() -> ImpactorItemStack.basic()
                                .type(ItemTypes.BLACK_STAINED_GLASS_PANE)
                                .title(Component.empty())
                                .build()
                        )
                        .build()
                )
                .build();
    }

}
