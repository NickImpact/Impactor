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

package net.impactdev.impactor.fabric.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.items.types.ItemTypes;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.layouts.ChestLayout;
import net.impactdev.impactor.api.ui.containers.views.ChestView;
import net.impactdev.impactor.api.ui.containers.views.pagination.Pagination;
import net.impactdev.impactor.api.ui.containers.views.pagination.sectioned.SectionedPagination;
import net.impactdev.impactor.api.ui.containers.views.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.ui.containers.views.pagination.updaters.PageUpdaterType;
import net.impactdev.impactor.fabric.FabricImpactorPlugin;
import net.impactdev.impactor.util.ExceptionPrinter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.math.vector.Vector2i;

import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.minecraft.commands.Commands.literal;

public class UITestCommands implements FabricCommand {
    @Override
    public void register(LiteralArgumentBuilder<CommandSourceStack> parent) {
        parent.then(literal("ui")
                .then(literal("chest")
                        .executes(context -> {
                            try {
                                ChestView view = ChestView.builder()
                                        .provider(Key.key("impactor", "test"))
                                        .title(text("Impactor/Gooey Fabric UI Test").color(TextColor.color(0x42, 0x87, 0xf5)))
                                        .layout(ChestLayout.builder()
                                                .size(5)
                                                .border(Icon.builder().display(() -> ImpactorItemStack.basic()
                                                        .type(ItemTypes.BLACK_STAINED_GLASS_PANE)
                                                        .title(Component.empty())
                                                        .build()
                                                ).build())
                                                .center(Icon.builder().display(() -> ImpactorItemStack.basic()
                                                                .type(ItemTypes.DIAMOND)
                                                                .title(text("Progress!")
                                                                        .style(Style.style().decoration(TextDecoration.ITALIC,
                                                                                TextDecoration.State.FALSE))
                                                                        .color(TextColor.color(0x42, 0x87, 0xf5))
                                                                )
                                                                .glow()
                                                                .build()
                                                        ).listener(ctx -> {
                                                            FabricImpactorPlugin.instance().logger().info("Hello World!");
                                                            return false;
                                                        }).build()
                                                )
                                                .build()
                                        )
                                        .build();

                                PlatformPlayer viewer = PlatformPlayer.create(context.getSource().getPlayerOrException()
                                        .getUUID());
                                view.open(viewer);

                            } catch (Throwable e) {
                                ExceptionPrinter.print(FabricImpactorPlugin.instance(), e);
                            }
                            return 1;
                        })
                )
                .then(literal("pagination").executes(context -> {
                    List<Icon> icons = Lists.newArrayList();
                    for(int i = 0; i < 35; i++) {
                        final int index = i;
                        icons.add(Icon.builder().display(() -> ImpactorItemStack.basic()
                                .type(ItemTypes.GOLD_NUGGET)
                                .title(text(index + 1))
                                .quantity(index + 1)
                                .lore(text("Test Lore").color(NamedTextColor.GRAY))
                                .build()
                        ).build());
                    }

                    Icon border = Icon.builder().display(() -> ImpactorItemStack.basic()
                            .type(ItemTypes.BLACK_STAINED_GLASS_PANE)
                            .title(Component.empty())
                            .build()
                    ).build();

                    PlatformPlayer viewer = PlatformPlayer.create(context.getSource().getPlayerOrException().getUUID());
                    Pagination pagination = Pagination.builder()
                            .provider(Key.key("impactor", "pagination"))
                            .viewer(viewer)
                            .title(text("Impactor/Gooey Fabric Pagination Test").color(TextColor.color(0x42, 0x87, 0xf5)))
                            .layout(ChestLayout.builder()
                                    .size(6)
                                    .rows(border, 1, 5)
                                    .columns(border, 1, 9)
                                    .build()
                            )
                            .zone(Vector2i.from(7, 3), Vector2i.ONE)
                            .contents(icons)
                            .updater(PageUpdater.builder()
                                    .slot(48)
                                    .type(PageUpdaterType.PREVIOUS)
                                    .provider(target -> ImpactorItemStack.basic()
                                            .type(ItemTypes.ARROW)
                                            .title(text("Previous Page (" + target + ")"))
                                            .glow()
                                            .build()
                                    )
                                    .build()
                            )
                            .updater(PageUpdater.builder()
                                    .slot(50)
                                    .type(PageUpdaterType.NEXT)
                                    .provider(target -> ImpactorItemStack.basic()
                                            .type(ItemTypes.ARROW)
                                            .title(text("Next Page (" + target + ")"))
                                            .glow()
                                            .build()
                                    )
                                    .build()
                            )
                            .build();

                    pagination.open();

                    return 1;
                }))
                .then(literal("sectioned").executes(context -> {
                    try {
                        List<Icon> first = Lists.newArrayList();
                        for (int i = 0; i < 35; i++) {
                            final int index = i;
                            first.add(Icon.builder().display(() -> ImpactorItemStack.basic()
                                    .type(ItemTypes.GOLD_NUGGET)
                                    .title(text(index + 1))
                                    .quantity(index + 1)
                                    .lore(text("Test Lore").color(NamedTextColor.GRAY))
                                    .build()
                            ).build());
                        }
                        List<Icon> second = Lists.newArrayList();
                        for (int i = 0; i < 15; i++) {
                            final int index = i;
                            second.add(Icon.builder().display(() -> ImpactorItemStack.basic()
                                    .type(ItemTypes.DIAMOND)
                                    .title(text("Hello World (" + index + ")"))
                                    .quantity(index + 1)
                                    .lore(text("Test Lore").color(NamedTextColor.GRAY))
                                    .build()
                            ).build());
                        }

                        Icon border = Icon.builder().display(() -> ImpactorItemStack.basic()
                                .type(ItemTypes.BLACK_STAINED_GLASS_PANE)
                                .title(Component.empty())
                                .build()
                        ).build();

                        PlatformPlayer viewer = PlatformPlayer.create(context.getSource().getPlayerOrException()
                                .getUUID());
                        SectionedPagination pagination = SectionedPagination.builder()
                                .provider(Key.key("impactor", "test"))
                                .viewer(viewer)
                                .title(text("Impactor/Gooey Fabric Sections Test").color(TextColor.color(0x42,
                                        0x87,
                                        0xf5)))
                                .layout(ChestLayout.builder()
                                        .rows(border, 1, 4, 6)
                                        .columns(border, 1, 7, 9)
                                        .slots(border, 38, 42)
                                        .build()
                                )
                                .section()
                                .contents(first)
                                .dimensions(Vector2i.from(5, 2))
                                .offset(Vector2i.ONE)
                                .updater(PageUpdater.builder()
                                        .slot(16)
                                        .type(PageUpdaterType.NEXT)
                                        .provider(target -> ImpactorItemStack.basic()
                                                .type(ItemTypes.ARROW)
                                                .title(text("Next Page (" + target + ")"))
                                                .glow()
                                                .build()
                                        )
                                        .build()
                                )
                                .updater(PageUpdater.builder()
                                        .slot(25)
                                        .type(PageUpdaterType.PREVIOUS)
                                        .provider(target -> ImpactorItemStack.basic()
                                                .type(ItemTypes.ARROW)
                                                .title(text("Previous Page (" + target + ")"))
                                                .glow()
                                                .build()
                                        )
                                        .build()
                                )
                                .complete()
                                .section()
                                .contents(second)
                                .dimensions(3, 1)
                                .offset(3, 4)
                                .updater(PageUpdater.builder()
                                        .slot(43)
                                        .type(PageUpdaterType.NEXT)
                                        .provider(target -> ImpactorItemStack.basic()
                                                .type(ItemTypes.ARROW)
                                                .title(text("Next Page (" + target + ")"))
                                                .glow()
                                                .build()
                                        )
                                        .build()
                                )
                                .updater(PageUpdater.builder()
                                        .slot(37)
                                        .type(PageUpdaterType.PREVIOUS)
                                        .provider(target -> ImpactorItemStack.basic()
                                                .type(ItemTypes.ARROW)
                                                .title(text("Previous Page (" + target + ")"))
                                                .glow()
                                                .build()
                                        )
                                        .build()
                                )
                                .complete()
                                .build();

                        pagination.open();
                    } catch (Exception e) {
                        ExceptionPrinter.print(FabricImpactorPlugin.instance(), e);
                    }

                    return 1;
                }))
        );
    }
}
