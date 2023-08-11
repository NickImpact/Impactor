package net.impactdev.impactor.minecraft.test;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.events.ImpactorEventBus;
import net.impactdev.impactor.api.platform.players.events.ClientConnectionEvent;
import net.impactdev.impactor.api.text.TextProcessor;
import net.impactdev.impactor.api.text.placeholders.PlaceholderComponent;
import net.impactdev.impactor.scoreboards.Scoreboard;
import net.impactdev.impactor.scoreboards.ScoreboardImplementation;
import net.impactdev.impactor.scoreboards.effects.FadeEffect;
import net.impactdev.impactor.scoreboards.objectives.ScoreboardObjective;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public final class ScoreboardTests {

    @Test
    public void create() {
        ScoreboardObjective objective = ScoreboardObjective.builder()
                .constant(PlaceholderComponent.create(Key.key("impactor:name")));

        ScoreboardObjective scheduled = ScoreboardObjective.builder().scheduled(builder -> builder
                .component(Component.text("Impactor Server Diagnostics"))
                .async()
                .interval(50, TimeUnit.MILLISECONDS)
                .effect(FadeEffect.create(90, 3, 0))
                .build()
        );

        ScoreboardObjective listening = ScoreboardObjective.builder().listening(builder -> builder
                .configure(ImpactorEventBus.bus(), ClientConnectionEvent.Join.class)
                .build()
        );

        ScoreboardImplementation implementation = Impactor.instance()
                .factories()
                .provide(ScoreboardImplementation.Factory.class)
                .packets();

        Scoreboard scoreboard = Scoreboard.builder()
                .processor(TextProcessor.mini())
                .implementation(implementation)
                .objective(objective)
                .build();
    }

}
