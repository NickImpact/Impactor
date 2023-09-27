package net.impactdev.impactor.minecraft.utility.adventure;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.processing.CommandContainer;
import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.api.utility.Context;
import net.impactdev.impactor.core.translations.internal.ImpactorTranslations;
import net.kyori.adventure.text.event.ClickCallback;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

@CommandContainer
public final class ClickCallbackCommand {

    public static final String COMMAND_ID = "impactor-callback";

    @CommandMethod(COMMAND_ID + " [id]")
    public void execute(final @NotNull CommandSource source, final @Argument("id") UUID uuid) {
        @Nullable
        final ClickCallbackRegistry.CallbackRegistration registration = ClickCallbackRegistry.INSTANCE
                .query(uuid)
                .orElse(null);

        if(registration == null) {
            ImpactorTranslations.INVALID_CLICK_CALLBACK.send(source, Context.empty());
            return;
        }

        boolean expired = false;
        boolean allow = true;

        // Check use count
        final int allowed = registration.options().uses();
        if(allowed != ClickCallback.UNLIMITED_USES) {
            final int useCount = registration.useCount().incrementAndGet();
            if(useCount >= allowed) {
                expired = true;
                allow = !(useCount > allowed);
            }
        }

        // Check duration expiry
        final Instant now = Instant.now();
        if(now.isAfter(registration.expiration())) {
            expired = true;
            allow = false;
        }

        if(expired) {
            ClickCallbackRegistry.INSTANCE.invalidate(uuid);
        }

        if(allow) {
            registration.callback().accept(source);
        } else {
            ImpactorTranslations.INVALID_CLICK_CALLBACK.send(source, Context.empty());
        }
    }

}
