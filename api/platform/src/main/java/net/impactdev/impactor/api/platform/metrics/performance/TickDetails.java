package net.impactdev.impactor.api.platform.metrics.performance;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.pointer.Pointer;
import net.kyori.adventure.pointer.Pointered;

/**
 *
 *
 * @since 6.0.0
 */
public interface TickDetails extends Pointered {

    Pointer<Double> TICKS_PER_SECOND = Pointer.pointer(Double.class, Key.key("tick", "per-second"));
    Pointer<Double> MSPT = Pointer.pointer(Double.class, Key.key("tick", "mspt"));

    double ticksPerSecond();

    double mspt();

}
