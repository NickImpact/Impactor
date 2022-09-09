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

import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.items.types.ItemType;
import net.impactdev.impactor.api.items.types.ItemTypes;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.layouts.ChestLayout;
import net.impactdev.impactor.api.ui.containers.views.ChestView;
import net.impactdev.impactor.forge.ForgeImpactorPlugin;
import net.impactdev.impactor.items.stacks.ImpactorAbstractedItemStack;
import net.impactdev.impactor.plugin.AbstractImpactorPlugin;
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

import static net.kyori.adventure.text.Component.text;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ForgeRegistrationHandler {

    public static void onCommandRegistration(RegisterCommandsEvent event) {
        AbstractImpactorPlugin.instance().logger().info("Registering impactor command...");

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
                                                            .title(Component.text("Progress!")
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
                )
        );
    }

}
