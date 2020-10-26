package net.impactdev.impactor.sponge.text.placeholders.provided.tick;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.text.DecimalFormat;

public class TPS {

    private static final DecimalFormat df = new DecimalFormat("#0.00");

    public synchronized static Text getTPS(boolean colored) {
        final double tps = Sponge.getServer().getTicksPerSecond();
        if(colored) {
            TextColor color;

            if (tps > 18) {
                color = TextColors.GREEN;
            } else if (tps > 15) {
                color = TextColors.YELLOW;
            } else {
                color = TextColors.RED;
            }

            return Text.of(color, df.format(tps));
        }

        return Text.of(df.format(tps));
    }

}
