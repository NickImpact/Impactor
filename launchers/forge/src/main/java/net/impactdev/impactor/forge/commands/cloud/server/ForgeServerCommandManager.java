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

package net.impactdev.impactor.forge.commands.cloud.server;

import cloud.commandframework.CommandTree;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.api.services.permissions.PermissionsService;
import net.impactdev.impactor.forge.commands.cloud.ForgeCloudCommandManager;
import net.impactdev.impactor.forge.commands.cloud.ForgeParserParameters;
import net.impactdev.impactor.forge.commands.cloud.annotations.Center;
import net.impactdev.impactor.forge.commands.cloud.arguments.parsers.ForgeArgumentParsers;
import net.impactdev.impactor.forge.commands.cloud.data.Coordinates;
import net.impactdev.impactor.forge.commands.cloud.data.Message;
import net.impactdev.impactor.forge.commands.cloud.data.MultipleEntitySelector;
import net.impactdev.impactor.forge.commands.cloud.data.MultiplePlayerSelector;
import net.impactdev.impactor.forge.commands.cloud.data.SingleEntitySelector;
import net.impactdev.impactor.forge.commands.cloud.data.SinglePlayerSelector;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Function;

public class ForgeServerCommandManager extends ForgeCloudCommandManager<CommandSource, CommandSourceStack> {

    /**
     * A meta attribute specifying which environments a command should be registered in.
     *
     * <p>The default value is {@link Commands.CommandSelection#ALL}.</p>
     *
     * @since 1.5.0
     */
    public static final CommandMeta.Key<Commands.CommandSelection> META_REGISTRATION_ENVIRONMENT = CommandMeta.Key.of(
            Commands.CommandSelection.class,
            "cloud:registration-environment"
    );

    /**
     * Create a new command manager instance
     *
     * @param executor              Execution coordinator instance. The coordinator is in charge of executing incoming
     *                              commands. Some considerations must be made when picking a suitable execution coordinator
     *                              for your platform. For example, an entirely asynchronous coordinator is not suitable
     *                              when the parsers used in that particular platform are not thread safe. If you have
     *                              commands that perform blocking operations, however, it might not be a good idea to
     *                              use a synchronous execution coordinator. In most cases you will want to pick between
     *                              {@link CommandExecutionCoordinator#simpleCoordinator()} and
     *                              {@link AsynchronousCommandExecutionCoordinator}
     * @param sourceMapper          Maps a {@link SharedSuggestionProvider} to the target command sender type
     * @param backwardsSourceMapper Maps the target command sender type to a {@link SharedSuggestionProvider}
     */
    public ForgeServerCommandManager(
            final @NonNull Function<@NonNull CommandTree<CommandSource>, @NonNull CommandExecutionCoordinator<CommandSource>> executor,
            final @NonNull Function<CommandSourceStack, CommandSource> sourceMapper,
            final @NonNull Function<CommandSource, CommandSourceStack> backwardsSourceMapper
    ) {
        super(
                executor,
                new ForgeServerCommandRegistrationHandler<>(),
                sourceMapper,
                backwardsSourceMapper,
                () -> new CommandSourceStack(
                        net.minecraft.commands.CommandSource.NULL,
                        Vec3.ZERO,
                        Vec2.ZERO,
                        null,
                        4,
                        "",
                        new TextComponent(""),
                        ServerLifecycleHooks.getCurrentServer(),
                        null
                )
        );

        this.registerParsers();
    }

    private void registerParsers() {
        this.getParserRegistry().registerParserSupplier(TypeToken.get(Message.class), params -> ForgeArgumentParsers.message());

        // Location arguments
        this.getParserRegistry().registerAnnotationMapper(
                Center.class,
                (annotation, type) -> ParserParameters.single(ForgeParserParameters.CENTER_INTEGERS, true)
        );
        this.getParserRegistry().registerParserSupplier(
                TypeToken.get(Coordinates.class),
                params -> ForgeArgumentParsers.vec3(params.get(
                        ForgeParserParameters.CENTER_INTEGERS,
                        false
                ))
        );
        this.getParserRegistry().registerParserSupplier(
                TypeToken.get(Coordinates.CoordinatesXZ.class),
                params -> ForgeArgumentParsers.vec2(params.get(
                        ForgeParserParameters.CENTER_INTEGERS,
                        false
                ))
        );
        this.getParserRegistry().registerParserSupplier(
                TypeToken.get(Coordinates.BlockCoordinates.class),
                params -> ForgeArgumentParsers.blockPos()
        );
        this.getParserRegistry().registerParserSupplier(
                TypeToken.get(Coordinates.ColumnCoordinates.class),
                params -> ForgeArgumentParsers.columnPos()
        );

        // Entity selectors
        this.getParserRegistry().registerParserSupplier(
                TypeToken.get(SinglePlayerSelector.class),
                params -> ForgeArgumentParsers.singlePlayerSelector()
        );
        this.getParserRegistry().registerParserSupplier(
                TypeToken.get(MultiplePlayerSelector.class),
                params -> ForgeArgumentParsers.multiplePlayerSelector()
        );
        this.getParserRegistry().registerParserSupplier(
                TypeToken.get(SingleEntitySelector.class),
                params -> ForgeArgumentParsers.singleEntitySelector()
        );
        this.getParserRegistry().registerParserSupplier(
                TypeToken.get(MultipleEntitySelector.class),
                params -> ForgeArgumentParsers.multipleEntitySelector()
        );
    }

    @Override
    public boolean hasPermission(@NonNull CommandSource sender, @NonNull String permission) {
        return Impactor.instance().services().provide(PermissionsService.class).hasPermission(sender.source(), permission);
    }

}
