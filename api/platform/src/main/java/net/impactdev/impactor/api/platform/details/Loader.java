package net.impactdev.impactor.api.platform.details;

import net.impactdev.impactor.loader.Version;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.pointer.Pointer;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.pointer.Pointers;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents the platform which is responsible for loading Impactor.
 *
 * @since 6.0.0
 */
public interface Loader extends Pointered {

    Pointer<Type> TYPE = Pointer.pointer(Type.class, Key.key("loader", "type"));
    Pointer<Version> VERSION = Pointer.pointer(Version.class, Key.key("loader", "version"));
    Pointer<MinecraftVersion> MINECRAFT_VERSION = Pointer.pointer(MinecraftVersion.class, Key.key("loader", "minecraft"));

    @Override
    default @NotNull Pointers pointers() {
        return Pointers.builder()
                .withStatic(TYPE, this.type())
                .withStatic(VERSION, this.version())
                .withDynamic(MINECRAFT_VERSION, () -> this.minecraft().orElse(new MinecraftVersion("N/A", false)))
                .build();
    }

    /**
     * The platform loader type which loaded Impactor. This can and should be bounded only to types denoted by
     * {@link Loader.Type}.
     *
     * @return The loader type
     * @since 6.0.0
     */
    Type type();

    /**
     * Represents the semver version of the loader type.
     *
     * @return The version of the loader platform
     * @since 6.0.0
     */
    Version version();

    /**
     * Specifies the minecraft version represented by the loader. As Impactor is capable of running via velocity,
     * minecraft details are not necessarily always available, as velocity would permit ambiguous minecraft versions.
     * As such, this detail is optionally available.
     *
     * @return The minecraft version represented by the loader, if applicable
     * @since 6.0.0
     */
    Optional<MinecraftVersion> minecraft();

    /**
     * Represents the types of platforms Impactor can be launched by.
     *
     * @since 6.0.0
     */
    enum Type {

        FABRIC("Fabric"),
        NEOFORGE("NeoForge"),
        VELOCITY("Velocity");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String displayName() {
            return this.name;
        }
    }

}
