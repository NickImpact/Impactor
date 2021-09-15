package net.impactdev.impactor.api.scoreboard.frames;


import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.frames.types.ListeningFrame;
import net.impactdev.impactor.api.scoreboard.frames.types.ConstantFrame;
import net.impactdev.impactor.api.scoreboard.frames.types.RefreshingFrame;
import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;
import net.kyori.adventure.text.Component;

public interface ScoreboardFrame {

    Component getText();

    boolean shouldUpdateOnTick();

    static ConstantFrame.ConstantFrameBuilder constant() {
        return Impactor.getInstance().getRegistry().createBuilder(ConstantFrame.ConstantFrameBuilder.class);
    }

    static RefreshingFrame.RefreshingFrameBuilder refreshing() {
        return Impactor.getInstance().getRegistry().createBuilder(RefreshingFrame.RefreshingFrameBuilder.class);
    }

    static <L> ListeningFrame.ListeningFrameBuilder<L> listening(TypeToken<L> type) {
        return Impactor.getInstance().getRegistry().createBuilder(ListeningFrame.ListeningFrameBuilder.class).type(type);
    }

    interface UpdatableFrame extends ScoreboardFrame {

        void initialize(Updatable parent);

        void shutdown();

    }

}
