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

package net.impactdev.impactor.forge.bootstrap;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.items.extensions.BookStack;
import net.impactdev.impactor.api.items.types.ItemType;
import net.impactdev.impactor.api.items.types.ItemTypes;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.Layout;
import net.impactdev.impactor.api.ui.containers.layouts.ChestLayout;
import net.impactdev.impactor.api.ui.containers.views.ChestView;
import net.impactdev.impactor.api.ui.containers.views.pagination.Pagination;
import net.impactdev.impactor.api.ui.containers.views.pagination.sectioned.SectionedPagination;
import net.impactdev.impactor.api.ui.containers.views.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.ui.containers.views.pagination.updaters.PageUpdaterType;
import net.impactdev.impactor.forge.ForgeImpactorPlugin;
import net.impactdev.impactor.items.stacks.ImpactorAbstractedItemStack;
import net.impactdev.impactor.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.util.ExceptionPrinter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import org.intellij.lang.annotations.Subst;
import org.spongepowered.math.vector.Vector2i;

import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ForgeRegistrationHandler {

    public static void onCommandRegistration(RegisterCommandsEvent event) {
        BaseImpactorPlugin.instance().logger().info("Registering impactor command...");

        event.getDispatcher().register(literal("impactor")
                .then(literal("items")
                        .then(argument("key", new ResourceLocationArgument())
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    ResourceLocation location = context.getArgument("key", ResourceLocation.class);

                                    @Subst("minecraft") String namespace = location.getNamespace();
                                    @Subst("air") String path = location.getPath();
                                    Key key = Key.key(namespace, path);
                                    ItemType type = ItemType.from(key);
                                    ImpactorItemStack stack = ImpactorItemStack.basic()
                                            .type(type)
                                            .title(text("Impactor Forge Item Test").color(NamedTextColor.GOLD))
                                            .unbreakable()
                                            .build();

                                    ItemStack minecraft = ((ImpactorAbstractedItemStack) stack).toNative();
                                    source.getPlayerOrException().inventory.add(minecraft);
                                    source.getPlayerOrException().inventoryMenu.broadcastChanges();

                                    return 1;
                                })
                        )
                        .then(literal("skull")
                            .then(literal("name")
                                .then(argument("value", StringArgumentType.word())).executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    ImpactorItemStack skull = ImpactorItemStack.skull()
                                            .title(text("Impactor Forge Skull Test").color(TextColor.color(0x42, 0x87, 0xf5)))
                                            .glow()
                                            .unbreakable()
                                            .player()
                                            .of("ISmellGood21")
                                            .complete()
                                            .build();

                                    ItemStack minecraft = ((ImpactorAbstractedItemStack) skull).toNative();
                                    source.getPlayerOrException().inventory.add(minecraft);
                                    source.getPlayerOrException().inventoryMenu.broadcastChanges();

                                    return 1;
                                })
                            )
                            .then(literal("texture")
                                .then(argument("base64", StringArgumentType.word()).executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    ImpactorItemStack skull = ImpactorItemStack.skull()
                                            .title(text("Impactor Forge Skull Test").color(TextColor.color(0x42, 0x87, 0xf5)))
                                            .glow()
                                            .unbreakable()
                                            .player()
                                            .texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmY1OGZiN2NiZjlmOGRjZmMzYmM5ZDYxYzdjYjViMjI5YmY0OWRiMTEwMTMzNmZmZGMyZDA4N2MwYjk0MTYyIn19fQ==")
                                            .complete()
                                            .build();

                                    ItemStack minecraft = ((ImpactorAbstractedItemStack) skull).toNative();
                                    source.getPlayerOrException().inventory.add(minecraft);
                                    source.getPlayerOrException().inventoryMenu.broadcastChanges();

                                    return 1;
                                }))
                            )
                        )
                        .then(literal("book").executes(context -> {
                            ImpactorItemStack book = ImpactorItemStack.book()
                                    .type(BookStack.BookType.WRITTEN)
                                    .title(text("Impactor Forge Book Test")
                                            .style(Style.style().decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                                            .color(TextColor.color(0x42, 0x87, 0xf5))
                                    )
                                    .author("NickImpact")
                                    .unbreakable()
                                    .generation(BookStack.Generation.ORIGINAL)
                                    .pages(text("Hello World!"))
                                    .build();

                            CommandSourceStack source = context.getSource();
                            ItemStack minecraft = ((ImpactorAbstractedItemStack) book).toNative();
                            source.getPlayerOrException().inventory.add(minecraft);
                            source.getPlayerOrException().inventoryMenu.broadcastChanges();

                            return 1;
                        }))
                )
                .then(literal("ui")
                        .then(literal("chest")
                                .executes(context -> {
                                    ChestView view = ChestView.builder()
                                            .provider(Key.key("impactor", "test"))
                                            .title(text("Impactor/Gooey UI Test").color(TextColor.color(0x42, 0x87, 0xf5)))
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
                                                                    .style(Style.style().decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                                                                    .color(TextColor.color(0x42, 0x87, 0xf5))
                                                            )
                                                            .glow()
                                                            .build()
                                                        ).listener(ctx -> {
                                                                ForgeImpactorPlugin.instance().logger().info("Hello World!");
                                                                return false;
                                                        }).build()
                                                    )
                                                    .build()
                                            )
                                            .build();

                                    PlatformPlayer viewer = PlatformPlayer.create(context.getSource().getPlayerOrException().getUUID());
                                    view.open(viewer);

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
                                    .title(text("Impactor/Gooey Pagination Test").color(TextColor.color(0x42, 0x87, 0xf5)))
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
                                        .title(text("Impactor/Gooey Sections Test").color(TextColor.color(0x42,
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
                                ExceptionPrinter.print(ForgeImpactorPlugin.instance(), e);
                            }

                            return 1;
                        }))
                )
        );
    }

}
