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

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.items.extensions.BookStack;
import net.impactdev.impactor.api.items.types.ItemType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.intellij.lang.annotations.Subst;

import static net.kyori.adventure.text.Component.text;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ItemTestCommands implements FabricCommand {

    @Override
    public void register(LiteralArgumentBuilder<CommandSourceStack> parent) {
        parent.then(literal("items")
                .then(literal("keyed")
                        .then(argument("key", new ResourceLocationArgument()).executes(context -> {
                            CommandSourceStack source = context.getSource();
                            ResourceLocation location = context.getArgument("key", ResourceLocation.class);

                            @Subst("minecraft") String namespace = location.getNamespace();
                            @Subst("air") String path = location.getPath();
                            Key key = Key.key(namespace, path);
                            ItemType type = ItemType.from(key);
                            ImpactorItemStack stack = ImpactorItemStack.basic()
                                    .type(type)
                                    .title(text("Impactor Fabric Item Test").color(NamedTextColor.GOLD))
                                    .unbreakable()
                                    .build();

                            ItemStack minecraft = stack.asMinecraftNative();
                            source.getPlayerOrException().inventory.add(minecraft);
                            source.getPlayerOrException().inventoryMenu.broadcastChanges();

                            return 1;
                        }))
                )
                .then(literal("skull")
                        .then(literal("name")
                                .then(argument("profile", EntityArgument.player()).executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    ImpactorItemStack skull = ImpactorItemStack.skull()
                                            .title(text("Impactor Fabric Skull Test").color(TextColor.color(0x42, 0x87, 0xf5)))
                                            .glow()
                                            .unbreakable()
                                            .player()
                                            .of(EntityArgument.getPlayer(context, "profile").getScoreboardName())
                                            .complete()
                                            .build();

                                    ItemStack minecraft = skull.asMinecraftNative();
                                    source.getPlayerOrException().inventory.add(minecraft);
                                    source.getPlayerOrException().inventoryMenu.broadcastChanges();

                                    return 1;
                                }))
                        )
                        .then(literal("texture")
                                .then(argument("base64", StringArgumentType.word()).executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    ImpactorItemStack skull = ImpactorItemStack.skull()
                                            .title(text("Impactor Fabric Skull Test").color(TextColor.color(0x42, 0x87, 0xf5)))
                                            .glow()
                                            .unbreakable()
                                            .player()
                                            .texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmY1OGZiN2NiZjlmOGRjZmMzYmM5ZDYxYzdjYjViMjI5YmY0OWRiMTEwMTMzNmZmZGMyZDA4N2MwYjk0MTYyIn19fQ==")
                                            .complete()
                                            .build();

                                    ItemStack minecraft = skull.asMinecraftNative();
                                    source.getPlayerOrException().inventory.add(minecraft);
                                    source.getPlayerOrException().inventoryMenu.broadcastChanges();

                                    return 1;
                                }))
                        )
                )
                .then(literal("book").executes(context -> {
                    ImpactorItemStack book = ImpactorItemStack.book()
                            .type(BookStack.BookType.WRITTEN)
                            .title(text("Impactor Fabric Book Test")
                                    .style(Style.style().decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                                    .color(TextColor.color(0x42, 0x87, 0xf5))
                            )
                            .author("NickImpact")
                            .unbreakable()
                            .generation(BookStack.Generation.ORIGINAL)
                            .pages(text("Hello World!"))
                            .build();

                    CommandSourceStack source = context.getSource();
                    ItemStack minecraft = book.asMinecraftNative();
                    source.getPlayerOrException().inventory.add(minecraft);
                    source.getPlayerOrException().inventoryMenu.broadcastChanges();

                    return 1;
                }))
        );
    }
}
