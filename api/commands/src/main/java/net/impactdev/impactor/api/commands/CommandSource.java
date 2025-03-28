package net.impactdev.impactor.api.commands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/**
 * Represents a reference to some sort of subject capable of sending commands.
 *
 * @since 6.0.0
 */
public record CommandSource(Audience source, CommandSourceStack delegate) implements ForwardingAudience.Single {

    /**
     * Represents the subject bound to this source. This subject is the actual source of execution
     * for the command, and can range from a player, an entity, or the server itself.
     *
     * @return The true subject represented by this source object
     * @since 6.0.0
     */
    @Override
    public Audience source() {
        return this.source;
    }

    @Override
    public @NotNull Audience audience() {
        return this.source;
    }

}
