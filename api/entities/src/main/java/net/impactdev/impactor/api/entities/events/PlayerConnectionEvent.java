package net.impactdev.impactor.api.entities.events;

import net.impactdev.impactor.api.entities.Player;
import org.spongepowered.eventgen.annotations.GenerateFactoryMethod;
import org.spongepowered.eventgen.annotations.NoFactoryMethod;

import java.util.UUID;

@NoFactoryMethod
public interface PlayerConnectionEvent {

    /**
     * Specifies the point a connecting player enters the configuration phase.
     */
    @GenerateFactoryMethod
    interface Configure extends PlayerConnectionEvent {

        UUID uuid();

    }

    /**
     * Specifies the point where a player enters the join phase. At this stage, the player has entered the world
     * and is an actual entity.
     */
    @GenerateFactoryMethod
    interface Join extends PlayerConnectionEvent {

        /**
         * The player who just joined the server.
         *
         * @return The joining player
         */
        Player player();

    }

}
