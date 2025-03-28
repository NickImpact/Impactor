package net.impactdev.impactor.api.platform.entity;

import net.impactdev.impactor.api.platform.Identifiable;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.text.Component;

/**
 * Represents an entity existing on the platform.
 *
 * @since 6.0.0
 */
public interface Entity extends Identifiable, Pointered {

    /**
     * The actual name of the entity, regardless of any display name set upon it.
     *
     * @return
     * @since 6.0.0
     */
    Component name();

}
