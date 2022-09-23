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

package net.impactdev.impactor.game.test.ui;

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.items.types.ItemTypes;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.layouts.ChestLayout;
import net.impactdev.impactor.api.ui.containers.views.pagination.Pagination;
import net.impactdev.impactor.api.ui.containers.views.pagination.rules.ContextRuleset;
import net.impactdev.impactor.platform.players.ImpactorPlatformPlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.junit.jupiter.api.Test;
import org.spongepowered.math.vector.Vector2i;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaginationTests {

    private static final Key TEST_KEY = Key.key("impactor", "test");
    private static final PlatformPlayer VIEWER = new ImpactorPlatformPlayer(UUID.randomUUID());

    @Test
    public void build() {
        Pagination test = Pagination.builder()
                .provider(TEST_KEY)
                .viewer(VIEWER)
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
        rules.filter(icon -> icon.context().has(int.class) && icon.context().require(int.class) < 6);
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
                .viewer(VIEWER)
                .contents(icons)
                .layout(this.basicLayout())
                .zone(Vector2i.from(7, 4), Vector2i.ONE)
                .ruleset(rules)
                .build();

        Map<Integer, Icon> results = test.pages().current().icons();
//        assertEquals(5, results.size());
        for(int i = 0; i < 5; i++) {
            assertEquals(i + 1, results.get(i + 10).context().require(int.class));
        }
    }

    @Test
    public void withUpdates() {
        ImpactorItemStack dummy = ImpactorItemStack.empty();
        ContextRuleset rules = ContextRuleset.create();
        rules.filter(icon -> icon.context().has(int.class) && icon.context().require(int.class) < 6);
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
                .viewer(VIEWER)
                .contents(icons)
                .layout(this.basicLayout())
                .zone(Vector2i.from(7, 4), Vector2i.ONE)
                .ruleset(rules)
                .build();

        Map<Integer, Icon> results = test.pages().current().icons();
//        assertEquals(5, results.size());
        for(int i = 0; i < 5; i++) {
            assertEquals(i + 1, results.get(i + 10).context().require(int.class));
        }
        rules.filter(icon -> icon.context().require(int.class) > 5);
        results = test.pages().current().icons();
//        assertEquals(5, results.size());
        for(int i = 0; i < 5; i++) {
            assertEquals(i + 6, results.get(i + 10).context().require(int.class));
        }
    }

//    @Test
//    public void sectioned() {
//        ImpactorItemStack dummy = ImpactorItemStack.empty();
//        ContextRuleset rules = ContextRuleset.create();
//        rules.filter(icon -> icon.context().has(int.class) && icon.context().require(int.class) < 6);
//        List<Icon> icons = Lists.newArrayList();
//        for(int i = 0; i < 10; i++) {
//            icons.add(Icon.builder()
//                    .display(() -> dummy)
//                    .append(int.class, i + 1)
//                    .constant()
//                    .build()
//            );
//        }
//
//        SectionedPagination pagination = SectionedPagination.builder()
//                .provider(TEST_KEY)
//                .viewer(VIEWER)
//                .title(text("Sectioned Pagination UT"))
//                .layout(this.basicLayout())
//                .readonly(true)
//                .section()
//                .contents(icons)
//                .dimensions(7, 2)
//                .offset(Vector2i.ONE)
//                .ruleset(rules)
//                .updater(PageUpdater.builder()
//                        .type(PageUpdaterType.NEXT)
//                        .slot(52)
//                        .provider(page -> ImpactorItemStack.basic()
//                                .type(ItemTypes.PAPER)
//                                .title(text("Next Page (").append(text(page)).append(text(")")))
//                                .build()
//                        )
//                        .build()
//                )
//                .complete()
//                .build();
//
//        assertEquals(TEST_KEY, pagination.namespace());
//        assertEquals(text("Sectioned Pagination UT"), pagination.title());
//        assertTrue(pagination.readonly());
//        assertEquals(1, pagination.sections().size());
//
//        Section section = pagination.at(0);
//        assertNotNull(section);
//        for(int i = 0; i < 5; i++) {
//            assertEquals(i + 1, section.pages().current()
//                    .at(10 + i)
//                    .map(icon -> icon.context().require(int.class))
//                    .orElse(-1)
//            );
//        }
//        assertTrue(section.within(Vector2i.from(5, 3)));
//        assertFalse(section.within(0));
//        assertFalse(section.within(Vector2i.from(5, 0)));
//    }

    @Test
    public void withoutProvider() {
        Exception ex = assertThrows(IllegalStateException.class, () -> Pagination.builder()
                .title(text("Exceptional"))
                .viewer(VIEWER)
                .build());
        assertEquals("Provider was not specified", ex.getMessage());
    }

    private ChestLayout basicLayout() {
        return ChestLayout.builder()
                .size(6)
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
