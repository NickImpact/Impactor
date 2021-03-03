package net.impactdev.impactor.sponge.text.placeholders.provided.tick;

import org.spongepowered.api.text.Text;

import java.text.DecimalFormat;

public interface MeanTickTime {

    DecimalFormat FORMATTER = new DecimalFormat("###.00");

    double get();

    Text getFormatted();

}
