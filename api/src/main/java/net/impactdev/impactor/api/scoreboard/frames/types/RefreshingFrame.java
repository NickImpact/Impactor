package net.impactdev.impactor.api.scoreboard.frames.types;

import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.scoreboard.frames.ScoreboardFrame;
import net.impactdev.impactor.api.utilities.Builder;

public interface RefreshingFrame extends ScoreboardFrame.UpdatableFrame {

    interface RefreshingFrameBuilder extends Builder<RefreshingFrame, RefreshingFrameBuilder> {

        RefreshingFrameBuilder raw(String raw);

        RefreshingFrameBuilder sources(PlaceholderSources sources);

    }

}
