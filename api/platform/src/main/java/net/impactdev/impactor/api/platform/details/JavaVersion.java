package net.impactdev.impactor.api.platform.details;

import net.impactdev.impactor.loader.logging.PrettyPrinter;
import net.kyori.adventure.pointer.Pointer;
import net.kyori.adventure.pointer.Pointers;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.pointer.Pointer.pointer;

public final class JavaVersion implements PlatformDetail {

    private static final Pointer<String> VERSION = pointer(String.class, key("java", "version"));
    private static final Pointer<String> VENDOR = pointer(String.class, key("java", "vendor"));


    private final Pointers pointers = Pointers.builder()
            .withStatic(VERSION, System.getProperty("java.version"))
            .withStatic(VENDOR, System.getProperty("java.vendor"))
            .build();

    @Override
    public @NotNull Pointers pointers() {
        return this.pointers;
    }

    @Override
    public String format() {
        return String.format(
                "Java - %s (%s)",
                pointers.get(VERSION),
                pointers.get(VENDOR)
        );
    }

    @Override
    public void print(PrettyPrinter printer) {
        printer.add(this.format());
    }
}
