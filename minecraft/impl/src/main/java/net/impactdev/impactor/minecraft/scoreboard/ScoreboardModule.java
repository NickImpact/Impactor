package net.impactdev.impactor.minecraft.scoreboard;

import net.impactdev.impactor.api.providers.FactoryProvider;
import net.impactdev.impactor.core.modules.ImpactorModule;
import net.impactdev.impactor.minecraft.scoreboard.packets.PacketImplementation;
import net.impactdev.impactor.scoreboards.ScoreboardImplementation;

public final class ScoreboardModule implements ImpactorModule {

    @Override
    public void factories(FactoryProvider provider) {
        provider.register(ScoreboardImplementation.Factory.class, new ImplementationFactory());
    }

    private static final class ImplementationFactory implements ScoreboardImplementation.Factory {

        @Override
        public ScoreboardImplementation packets() {
            return new PacketImplementation();
        }

    }
}
