package net.impactdev.impactor.sponge.text.placeholders.provided.tick;

import net.kyori.adventure.text.TextComponent;

import java.text.DecimalFormat;

public interface MeanTickTime {

    DecimalFormat FORMATTER = new DecimalFormat("###.00");

    double get();

    TextComponent getFormatted();

}
