package net.impactdev.impactor.api.scoreboard.lines.types;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;
import net.impactdev.impactor.api.utilities.Builder;
import net.kyori.adventure.text.Component;

public interface ConstantLine extends ScoreboardLine {

    static ConstantLineBuilder builder() {
        return Impactor.getInstance().getRegistry().createBuilder(ConstantLineBuilder.class);
    }

    interface ConstantLineBuilder extends LineBuilder<ConstantLineBuilder>, Builder<ConstantLine, ConstantLineBuilder> {

        ConstantLineBuilder text(String raw, PlaceholderSources sources);

        ConstantLineBuilder text(Component text);

    }

}
