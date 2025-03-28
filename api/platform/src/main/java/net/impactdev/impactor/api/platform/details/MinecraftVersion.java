package net.impactdev.impactor.api.platform.details;

/**
 * Represents minecraft version details.
 *
 * @since 6.0.0
 */
public final class MinecraftVersion {

    private final String version;
    private final boolean snapshot;

    public MinecraftVersion(String version, boolean snapshot) {
        this.version = version;
        this.snapshot = snapshot;
    }

    /**
     * Gets the string representation of the minecraft version.
     *
     * @return
     * @since 6.0.0
     */
    public String version() {
        return this.version;
    }

    /**
     *
     *
     * @return
     * @since 6.0.0
     */
    public boolean isSnapshot() {
        return this.snapshot;
    }

    /**
     *
     *
     * @return
     * @since 6.0.0
     */
    public int protocol() {

    }

}
