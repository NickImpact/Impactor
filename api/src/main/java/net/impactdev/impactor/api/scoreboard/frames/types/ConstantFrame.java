package net.impactdev.impactor.api.scoreboard.frames.types;

import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.scoreboard.frames.ScoreboardFrame;
import net.impactdev.impactor.api.utilities.Builder;
import net.kyori.adventure.text.Component;

public interface ConstantFrame extends ScoreboardFrame {

    interface ConstantFrameBuilder extends Builder<ConstantFrame, ConstantFrameBuilder> {

        ConstantFrameBuilder raw(String raw, PlaceholderSources sources);

        ConstantFrameBuilder text(Component text);

    }

}
