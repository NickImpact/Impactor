package net.impactdev.impactor.api.scoreboard.lines.types;

import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.scoreboard.components.TimeConfiguration;
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;
import net.impactdev.impactor.api.utilities.Builder;

import java.util.concurrent.TimeUnit;

public interface RefreshingLine extends ScoreboardLine, Updatable {

    TimeConfiguration getTimingConfig();

    interface RefreshingLineBuilder extends LineBuilder<RefreshingLineBuilder>, Builder<RefreshingLine, RefreshingLineBuilder> {

        RefreshingLineBuilder text(String raw);

        RefreshingLineBuilder rate(long ticks);

        RefreshingLineBuilder rate(long duration, TimeUnit unit);

        RefreshingLineBuilder async();

        RefreshingLineBuilder sources(PlaceholderSources sources);

    }

}
