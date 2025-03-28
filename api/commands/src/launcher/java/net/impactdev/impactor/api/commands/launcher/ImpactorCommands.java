package net.impactdev.impactor.api.commands.launcher;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.api.text.TextProcessor;
import net.impactdev.impactor.loader.logging.PrettyPrinter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.brigadier.BrigadierManagerHolder;
import org.incendo.cloud.brigadier.BrigadierSetting;
import org.incendo.cloud.brigadier.CloudBrigadierManager;
import org.incendo.cloud.brigadier.argument.BrigadierMappings;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.exception.CommandExecutionException;
import org.incendo.cloud.exception.handling.ExceptionController;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.processors.cache.GuavaCache;
import org.incendo.cloud.processors.confirmation.ConfirmationConfiguration;
import org.incendo.cloud.processors.confirmation.ConfirmationManager;
import org.incendo.cloud.setting.Configurable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;

@ApiStatus.Internal
public abstract class ImpactorCommands {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            new ThreadFactoryBuilder()
                    .setNameFormat("Impactor Command Executor: %d")
                    .setDaemon(true)
                    .build()
    );

    private final Logger logger;
    private final CommandManager<CommandSource> manager;

    protected ImpactorCommands(final Logger logger) {
        this.logger = logger;
        var executor = ExecutionCoordinator.<CommandSource>builder().executor(EXECUTOR).build();

        this.manager = this.create(executor);
        this.initialize();
    }

    protected abstract CommandManager<CommandSource> create(ExecutionCoordinator<CommandSource> coordinator);

    protected abstract void configure(CommandManager<CommandSource> manager);

    private void initialize() {
        ConfirmationManager<CommandSource> confirmations = ConfirmationManager.confirmationManager(
                ConfirmationConfiguration.<CommandSource>builder()
                        .cache(GuavaCache.of(CacheBuilder.newBuilder()
                                .expireAfterWrite(30, TimeUnit.SECONDS)
                                .build()
                        ))
                        .noPendingCommandNotifier(source -> source.sendMessage(text("No pending confirmations available...").color(NamedTextColor.RED)))
                        .confirmationRequiredNotifier((source, ctx) -> source.sendMessage(text("Click to confirm action!").color(NamedTextColor.YELLOW)))
                        .build()
        );

        this.manager.registerCommandPostProcessor(confirmations.createPostprocessor());
        this.setupBrigadier(this.manager);

        ExceptionController<CommandSource> controller = this.manager.exceptionController();
        controller.registerHandler(CommandExecutionException.class, context -> {
            Component prefix = TextProcessor.mini().parse("<gradient:#ff4c4c:#fbff53>Impactor</gradient> <gray>»");
            Component message = prefix.append(space())
                    .append(text("An internal error occurred while processing that command!")
                            .color(TextColor.color(0xff4c4c))
                    );

            Style style = message.style();

            final StringWriter writer = new StringWriter();
            context.exception().getCause().printStackTrace(new PrintWriter(writer));

            final String trace = writer.toString().replace("\t", Strings.repeat(" ", 4));
            final Component hover = text(trace).append(Component.newline())
                    .append(text("Click to copy!").color(NamedTextColor.YELLOW));

            style = style.hoverEvent(HoverEvent.showText(hover)).clickEvent(ClickEvent.copyToClipboard(trace));
            message = message.style(style);

            context.context().sender().sendMessage(message);
            this.printException(context.exception());
        });
    }

    @SuppressWarnings("unchecked")
    private void setupBrigadier(final CommandManager<CommandSource> manager) {
        if (manager instanceof BrigadierManagerHolder<?, ?> holder && holder.hasBrigadierManager()) {
            CloudBrigadierManager<CommandSource, ?> brigadier = (CloudBrigadierManager<CommandSource, ?>) holder.brigadierManager();

            Configurable<BrigadierSetting> settings = brigadier.settings();
            settings.set(BrigadierSetting.FORCE_EXECUTABLE, true);

            BrigadierMappings<CommandSource, ?> mappings = brigadier.mappings();
//            ServiceLoader<BrigadierMappingProvider> mappers = ServiceLoader.load(BrigadierMappingProvider.class);
//            for (BrigadierMappingProvider provider : mappers) {
//                provider.register(mappings);
//            }
        }
    }

    private void printException(final CommandExecutionException exception) {
        PrettyPrinter printer = new PrettyPrinter(80).wrapTo(80);
        printer.title("Command Execution Exception")
                .add("An unexpected error was encountered during command processing. This error")
                .consume(p -> {
                    String contextual = exception.context() != null ? "alongside its relative context" : "";
                    p.add(contextual + " will now be displayed.");
                })
                .hr('-')
                .consume(p -> {
                    final @Nullable CommandContext<?> context = exception.context();
                    if (context != null) {
                        p.add("Command Input: %s", context.rawInput().input());
                        p.add("During Suggestions: %b", context.isSuggestions());

                        p.add("Context:");
                        context.all().forEach((key, value) -> p.add("  %s: %s", key, value.toString()));
                        p.newline();
                    }
                })
                .add("Encountered Exception Stacktrace:")
                .add(exception);

        printer.log(this.logger, Level.ERROR);
    }
}
