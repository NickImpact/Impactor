package net.impactdev.impactor.api.scoreboard.components;

/**
 * Represents a line on a scoreboard that can have its contents queried multiple times for potentially
 * different display information.
 *
 * It is up to the implementation to decide how exactly a line is capable of updating.
 */
public interface Updatable {

    /**
     * The purpose of this method is to initialize and activate the component that tracks and updates
     * the line per the type of line. For instance, this would be where a line that refreshes on an interval
     * would set up its scheduler.
     */
    void start();

    /**
     * Handles the actual updating of the line.
     */
    void update();

    /**
     * The purpose of this method is to deactivate any instance that is no longer needed for the line to
     * update, whether that be the scoreboard was hidden/removed or the player it belonged to logged off.
     * In the event of a scheduled updater, this is where the implementation should deactivate the updater.
     */
    void shutdown();
}
