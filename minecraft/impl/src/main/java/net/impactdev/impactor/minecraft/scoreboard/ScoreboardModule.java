package net.impactdev.impactor.minecraft.scoreboard;

import net.impactdev.impactor.api.providers.FactoryProvider;
import net.impactdev.impactor.core.modules.ImpactorModule;
import net.impactdev.impactor.minecraft.scoreboard.packets.PacketImplementation;
import net.impactdev.impactor.minecraft.scoreboard.viewed.ViewedImpactorScoreboard;
import net.impactdev.impactor.scoreboards.ScoreboardImplementation;
import net.impactdev.impactor.scoreboards.viewed.ViewedScoreboard;

public final class ScoreboardModule implements ImpactorModule {

    @Override
    public void factories(FactoryProvider provider) {
        provider.register(ScoreboardImplementation.Factory.class, new ImplementationFactory());
        provider.register(ViewedScoreboard.Factory.class, new ViewedImpactorScoreboard.ViewedScoreboardFactory());
    }

    private static final class ImplementationFactory implements ScoreboardImplementation.Factory {

        @Override
        public ScoreboardImplementation packets() {
            return new PacketImplementation();
        }

    }
}
