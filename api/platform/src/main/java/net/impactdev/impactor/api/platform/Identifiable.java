package net.impactdev.impactor.api.platform;

import java.util.UUID;

/**
 * Represents an object which can be identified by a unique identifier.
 *
 * @since 6.0.0
 */
public interface Identifiable {

    /**
     * Supplies the unique identifier registered to the target object.
     *
     * @return The unique identifier
     * @since 6.0.0
     */
    UUID uuid();

}
