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

package net.impactdev.impactor.core.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.CommandTree;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.CommandExecutionException;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.api.events.ImpactorEvent;
import net.impactdev.impactor.api.utility.ExceptionPrinter;
import net.impactdev.impactor.api.utility.printing.PrettyPrinter;
import net.impactdev.impactor.core.commands.event.CommandFrameworkEvents;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.event.PostResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public abstract class ImpactorCommandManager<S> {

    private static final CloudKey<Boolean> PRE_PROCESSED_KEY = SimpleCloudKey.of("__COMMAND_PRE_PROCESSED__", TypeToken.get(Boolean.class));
    private static final CloudKey<String> RAW_INPUT_KEY = SimpleCloudKey.of("__raw_input__", TypeToken.get(String.class));
    private static final CloudKey<Boolean> POST_PROCESSED_KEY = SimpleCloudKey.of("__COMMAND_POST_PROCESSED__", TypeToken.get(Boolean.class));
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            new ThreadFactoryBuilder()
                    .setNameFormat("Impactor Command Executor: %d")
                    .setDaemon(true)
                    .build()
    );

    private final CommandManager<CommandSource> manager;

    public ImpactorCommandManager() {
        Coordinator coordinator = AsynchronousCommandExecutionCoordinator.<CommandSource>newBuilder()
                        .withExecutor(EXECUTOR)
                        .build()
                        ::apply;

        this.manager = this.create(coordinator);
    }

    protected CommandManager<CommandSource> delegate() {
        return this.manager;
    }

    /**
     * This is responsible for initializing all the basic components of the command manager,
     * as well as publishing events in relation to the lifecycle of the manager setup.
     * For instance, we will publish events both for argument parser registrations on top
     * of individual command registration.
     *
     * <p>Beyond registration, this should also set up components of this cloud manager such
     * as exception handling and more.
     */
    public void initialize() {
        try {
            this.manager.registerExceptionHandler(CommandExecutionException.class, (source, e) -> {
                PrettyPrinter printer = new PrettyPrinter(80).wrapTo(80);
                printer.title("Command Execution Exception")
                        .add("An unexpected error was encountered during command processing. This error")
                        .consume(p -> {
                            String contextual = e.getCommandContext() != null ? "alongside its relative context" : "";
                            p.add(contextual + " will now be displayed.");
                        })
                        .hr('-')
                        .consume(p -> {
                            final @Nullable CommandContext<?> context = e.getCommandContext();
                            if (context != null) {
                                p.add("Command Input: %s", context.getRawInputJoined());
                                p.add("During Suggestions: %b", context.isSuggestions());
                                p.add("Pre-processed: %b", context.get(PRE_PROCESSED_KEY));
                                p.add("Post-processed: %b", context.get(POST_PROCESSED_KEY));

                                p.add("Context:");
                                context.asMap()
                                        .entrySet()
                                        .stream()
                                        .filter(entry -> !entry.getKey().equals(PRE_PROCESSED_KEY.getName()) &&
                                                !entry.getKey().equals(POST_PROCESSED_KEY.getName()) &&
                                                !entry.getKey().equals(RAW_INPUT_KEY.getName()))
                                        .forEach(entry -> p.add("  %s: %s",
                                                entry.getKey(),
                                                entry.getValue().toString()));
                                p.newline();
                            }
                        })
                        .add("Encountered Exception Stacktrace:")
                        .add(e);

                printer.log(BaseImpactorPlugin.instance().logger(), PrettyPrinter.Level.ERROR);

                if (source.metadata().acceptsFailure()) {
                    Component detail = Component.text(
                            "An internal error occurred while attempting to perform this command.");
                    Style style = detail.style();

                    final StringWriter writer = new StringWriter();
                    e.getCause().printStackTrace(new PrintWriter(writer));

                    final String trace = writer.toString().replace("\t", Strings.repeat(" ", 4));
                    final Component result = Component.text("Click to copy!").color(NamedTextColor.YELLOW)
                            .append(Component.newline())
                            .append(Component.text(trace));

                    style = style.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, result))
                            .clickEvent(ClickEvent.copyToClipboard(trace));

                    detail = detail.style(style);

                    source.sendMessage(detail);
                }
            });

            this.postAndVerify(CommandFrameworkEvents.captions(this.manager));
            this.postAndVerify(CommandFrameworkEvents.arguments(this.manager));
            this.postAndVerify(CommandFrameworkEvents.commands(this.manager));

            CommandConfirmationManager<CommandSource> confirmations = new CommandConfirmationManager<>(
                    30L,
                    TimeUnit.SECONDS,
                    context -> context.getCommandContext().getSender().sendMessage(Component.text("Click to confirm action!").color(NamedTextColor.YELLOW)),
                    sender -> sender.sendMessage(Component.text("No pending confirmations available...").color(NamedTextColor.RED))
            );
            confirmations.registerConfirmationProcessor(this.manager);
        } catch (PostResult.CompositeException events) {
            PrettyPrinter printer = new PrettyPrinter(80);
            printer.title("Command/Argument Registration Exception")
                    .add("It seems we've encountered a set of errors processing event listeners!")
                    .add("The problematic event listeners will be detailed below...")
                    .newline()
                    .consume(p -> events.result().exceptions().forEach((subscriber, exception) -> {
                        p.hr('-').add(exception);
                    }))
                    .log(BaseImpactorPlugin.instance().logger(), PrettyPrinter.Level.ERROR);
        } catch (Exception e) {
            ExceptionPrinter.print(BaseImpactorPlugin.instance().logger(), e);
        }
    }

    /**
     * Creates the command manager from the platform implementation component,
     * using the created coordinator at the base of this delegation.
     *
     * @param coordinator The coordinator to use for asynchronous execution
     * @return The platform specific command manager reflecting the values of
     * the coordinator, as well as the two mapping interfaces.
     *
     * @see #impactor()
     * @see #platform()
     */
    protected abstract CommandManager<CommandSource> create(Coordinator coordinator);

    protected abstract ToImpactor<S> impactor();

    protected abstract ToNative<S> platform();

    private void postAndVerify(@NotNull ImpactorEvent event) throws PostResult.CompositeException {
        PostResult result = Impactor.instance().events().post(event);
        result.raise();
    }

    @FunctionalInterface
    public interface Coordinator extends Function<CommandTree<CommandSource>, CommandExecutionCoordinator<CommandSource>> {}

    @FunctionalInterface
    public interface ToImpactor<S> extends Function<S, CommandSource> {}

    @FunctionalInterface
    public interface ToNative<S> extends Function<CommandSource, S> {}
}
