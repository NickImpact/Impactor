package net.impactdev.impactor.api.platform.details;

import net.impactdev.impactor.loader.logging.PrettyPrinter;
import net.kyori.adventure.pointer.Pointered;

/**
 * Represents a particular detail about the platform.
 *
 * @since 6.0.0
 */
public interface PlatformDetail extends Pointered, PrettyPrinter.IPrettyPrintable {

    /**
     * Formats the details of the particular platform element into a string.
     *
     * @return The string representation of the detail
     * @since 6.0.0
     */
    String format();

}
