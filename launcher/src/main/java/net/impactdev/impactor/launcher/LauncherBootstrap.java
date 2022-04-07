package net.impactdev.impactor.launcher;

/**
 * Minimal bootstrap plugin, called by the loader plugin.
 */
public interface LauncherBootstrap {

    /**
     * This method should handle the actual registration of additional event listeners
     * that might handle plugin initialization, while additionally taking care of
     * any components required by construction.
     */
    void construct();

    /**
     * Handles shutdown requirements of the plugin
     */
    void shutdown();

}
