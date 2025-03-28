package net.impactdev.impactor.api.commands;

import org.incendo.cloud.CommandManager;

/**
 * Represents the processor responsible for establishing a {@link CommandManager}. This system is effectively
 * responsible for establishing how commands are performed. Internally, this should be based on
 * <a href="https://cloud.incendo.org/">cloud from the incendo group</a>.
 *
 * @since 6.0.0
 */
public interface CommandProcessor {

    /**
     * Provides the {@link CommandManager} associated with this processor.
     *
     * @return A {@link CommandManager}
     * @since 6.0.0
     */
    CommandManager<CommandSource> manager();

}
