package net.impactdev.impactor.minecraft.test;

import net.impactdev.impactor.scoreboards.Scoreboard;
import net.impactdev.impactor.scoreboards.ScoreboardImplementation;
import net.impactdev.impactor.api.text.transforming.transformers.FadeTransformer;
import net.impactdev.impactor.scoreboards.objectives.Objective;
import net.impactdev.impactor.api.text.transforming.TransformableText;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public final class ScoreboardTests {

    @Test
    public void create() {
        TransformableText test = TransformableText.builder()
                .supplier(() -> Component.text("Impactor Server Diagnostics"))
                .transformer(FadeTransformer.create(90, 3, 0))
                .build();

        Objective objective = Objective.scheduled(
                builder -> builder.text(test::asComponent)
                        .interval(20, TimeUnit.MILLISECONDS)
                        .async()
                        .build()
        );

        Scoreboard scoreboard = Scoreboard.builder()
                .implementation(ScoreboardImplementation.packets())
                .objective(objective)
                .build();
    }

}
