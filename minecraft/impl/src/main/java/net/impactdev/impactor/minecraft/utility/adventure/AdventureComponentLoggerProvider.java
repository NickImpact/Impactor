package net.impactdev.impactor.minecraft.utility.adventure;

import com.google.auto.service.AutoService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.logger.slf4j.ComponentLoggerProvider;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.util.Locale;

@SuppressWarnings("UnstableApiUsage")
@AutoService(ComponentLoggerProvider.class)
public final class AdventureComponentLoggerProvider implements ComponentLoggerProvider {

    @Override
    public @NotNull ComponentLogger logger(@NotNull LoggerHelper helper, @NotNull String name) {
        return helper.delegating(LoggerFactory.getLogger(name), this::serialize);
    }

    private String serialize(final Component message) {
        final Component rendered = GlobalTranslator.render(message, Locale.getDefault());
        return PlainTextComponentSerializer.plainText().serialize(rendered);
    }

}
