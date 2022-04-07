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

package net.impactdev.impactor.sponge.commands;

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.services.text.MessageService;
import net.impactdev.impactor.api.ui.containers.icons.DisplayProvider;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.layouts.Layout;
import net.impactdev.impactor.api.ui.containers.pagination.async.AsyncPagination;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.SectionedPagination;
import net.impactdev.impactor.api.ui.containers.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.ui.containers.pagination.updaters.PageUpdaterType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.math.vector.Vector2i;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DevCommand {

    public Command.Parameterized create() {
        return Command.builder()
                .addChild(Command.builder()
                        .executor(context -> {
                            CommandCause cause = context.cause();
                            ServerPlayer target = cause.first(ServerPlayer.class)
                                    .orElseThrow(() -> new CommandException(Component.text("Can only query against a player!")));

                            MessageService<Component> service = Impactor.getInstance().getRegistry().get(MessageService.class);
                            cause.audience().sendMessage(service.parse("&7Opening Sectioned UI Test... "));

                            Icon<ItemStack> border = Icon.builder(ItemStack.class)
                                    .display(new DisplayProvider.Constant<>(ItemStack.builder()
                                            .itemType(ItemTypes.BLACK_STAINED_GLASS_PANE.get())
                                            .add(Keys.CUSTOM_NAME, Component.empty())
                                            .build()
                                    ))
                                    .build();
                            SectionedPagination pagination = SectionedPagination.builder()
                                    .title(MiniMessage.miniMessage().deserialize("<gradient:red:blue>Sectioned Pagination Test</gradient>"))
                                    .provider(Key.key("impactor:sectioned-test"))
                                    .layout(Layout.builder()
                                            .size(6)
                                            .columns(border, 1, 3, 9)
                                            .rows(border, 1, 6)
                                            .slots(border, 30, 31, 32, 33, 34)
                                            .build()
                                    )
                                    .viewer(PlatformPlayer.from(target))
                                    .section()
                                    .dimensions(1, 4)
                                    .offset(Vector2i.ONE)
                                    .contents(this.generate(5))
                                    .updater(PageUpdater.builder()
                                            .type(PageUpdaterType.NEXT)
                                            .slot(46)
                                            .provider(page -> ItemStack.builder()
                                                    .itemType(ItemTypes.ARROW)
                                                    .add(Keys.CUSTOM_NAME, MiniMessage.miniMessage().deserialize("<gradient:red:yellow>Next Page: " + page + "</gradient>"))
                                                    .build())
                                            .build()
                                    )
                                    .updater(PageUpdater.builder()
                                            .type(PageUpdaterType.PREVIOUS)
                                            .slot(1)
                                            .provider(page -> ItemStack.builder()
                                                    .itemType(ItemTypes.ARROW)
                                                    .add(Keys.CUSTOM_NAME, MiniMessage.miniMessage().deserialize("<gradient:red:yellow>Previous Page: " + page + "</gradient>"))
                                                    .build())
                                            .build()
                                    )
                                    .complete()
                                    .section()
                                    .dimensions(5, 2)
                                    .offset(Vector2i.from(3, 1))
                                    .contents(this.generate(20))
                                    .updater(PageUpdater.builder()
                                            .type(PageUpdaterType.NEXT)
                                            .slot(42)
                                            .provider(page -> ItemStack.builder()
                                                    .itemType(ItemTypes.ARROW)
                                                    .add(Keys.CUSTOM_NAME, MiniMessage.miniMessage().deserialize("<gradient:red:yellow>Next Page: " + page + "</gradient>"))
                                                    .build())
                                            .build()
                                    )
                                    .updater(PageUpdater.builder()
                                            .type(PageUpdaterType.PREVIOUS)
                                            .slot(40)
                                            .provider(page -> ItemStack.builder()
                                                    .itemType(ItemTypes.ARROW)
                                                    .add(Keys.CUSTOM_NAME, MiniMessage.miniMessage().deserialize("<gradient:red:yellow>Previous Page: " + page + "</gradient>"))
                                                    .build())
                                            .build()
                                    )
                                    .complete()
                                    .build();

                            pagination.open();
                            return CommandResult.success();
                        })
                        .build(), "sectioned"
                )
                .addChild(Command.builder()
                        .executor(context -> {
                            CommandCause cause = context.cause();
                            ServerPlayer target = cause.first(ServerPlayer.class)
                                    .orElseThrow(() -> new CommandException(Component.text("Can only query against a player!")));

                            MessageService<Component> service = Impactor.getInstance().getRegistry().get(MessageService.class);
                            cause.audience().sendMessage(service.parse("&7Opening Async UI Test... "));

                            Icon<ItemStack> border = Icon.builder(ItemStack.class)
                                    .display(new DisplayProvider.Constant<>(ItemStack.builder()
                                            .itemType(ItemTypes.BLACK_STAINED_GLASS_PANE.get())
                                            .add(Keys.CUSTOM_NAME, Component.empty())
                                            .build()
                                    ))
                                    .build();
                            AsyncPagination pagination = AsyncPagination.builder()
                                    .provider(Key.key("impactor:async-test"))
                                    .viewer(PlatformPlayer.from(target))
                                    .title(MiniMessage.miniMessage().deserialize("<gradient:red:yellow>Async Pagination Test</gradient>"))
                                    .layout(Layout.builder()
                                            .size(5)
                                            .border(border)
                                            .size(6)
                                            .slots(border, 45, 53)
                                            .build()
                                    )
                                    .accumulator(CompletableFuture.completedFuture(Lists.newArrayList()))
                                    .zone(Vector2i.from(7, 3), Vector2i.ONE)
                                    .build();
                            pagination.open();
                            return CommandResult.success();
                        })
                        .build(), "async"
                )
                .addChild(Command.builder()
                        .executor(context -> {
                            CommandCause cause = context.cause();
                            ServerPlayer target = cause.first(ServerPlayer.class)
                                    .orElseThrow(() -> new CommandException(Component.text("Can only query against a player!")));

                            MessageService<Component> service = Impactor.getInstance().getRegistry().get(MessageService.class);
                            cause.audience().sendMessage(service.parse("&7Opening Async UI Test... "));

                            Icon<ItemStack> border = Icon.builder(ItemStack.class)
                                    .display(new DisplayProvider.Constant<>(ItemStack.builder()
                                            .itemType(ItemTypes.BLACK_STAINED_GLASS_PANE.get())
                                            .add(Keys.CUSTOM_NAME, Component.empty())
                                            .build()
                                    ))
                                    .build();
                            AsyncPagination pagination = AsyncPagination.builder()
                                    .provider(Key.key("impactor:async-test"))
                                    .viewer(PlatformPlayer.from(target))
                                    .title(MiniMessage.miniMessage().deserialize("<gradient:red:yellow>Async Pagination Test</gradient>"))
                                    .layout(Layout.builder()
                                            .size(5)
                                            .border(border)
                                            .size(6)
                                            .slots(border, 45, 53)
                                            .build()
                                    )
                                    .accumulator(CompletableFuture.supplyAsync(() -> {
                                        try {
                                            Thread.sleep(6000);
                                        }
                                        catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                        return Lists.newArrayList();
                                    }))
                                    .zone(Vector2i.from(7, 3), Vector2i.ONE)
                                    .build();
                            pagination.open();
                            return CommandResult.success();
                        })
                        .build(), "async-timeout"
                )
                .build();
    }

    private List<Icon<?>> generate(int amount) {
        List<ItemType> items = Sponge.game().registry(RegistryTypes.ITEM_TYPE).stream().collect(Collectors.toList());
        Random rng = new Random();

        List<Icon<?>> results = Lists.newArrayList();
        for(int i = 0; i < amount; i++) {
            results.add(Icon.builder(ItemStack.class)
                    .display(new DisplayProvider.Constant<>(ItemStack.builder()
                            .itemType(items.get(rng.nextInt(items.size())))
                            .add(Keys.CUSTOM_NAME, Component.text("Random Item " + i).color(NamedTextColor.GREEN))
                            .build()
                    ))
                    .build()
            );
        }
        return results;
    }
}
