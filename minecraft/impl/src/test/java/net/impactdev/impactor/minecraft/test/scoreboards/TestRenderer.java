package net.impactdev.impactor.minecraft.test.scoreboards;

import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.scoreboards.AssignedScoreboard;
import net.impactdev.impactor.api.scoreboards.ScoreboardRenderer;
import net.impactdev.impactor.api.scoreboards.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboards.objectives.Objective;
import net.impactdev.impactor.api.utility.printing.PrettyPrinter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ansi.ANSIComponentSerializer;

import java.util.Optional;

public class TestRenderer implements ScoreboardRenderer {

    private final PrettyPrinter printer = new PrettyPrinter(80);
    private final ANSIComponentSerializer serializer = ANSIComponentSerializer.ansi();

    @Override
    public void objective(PlatformPlayer viewer, Objective.Displayed objective) {
        this.printer.title(this.serializer.serialize(objective.text()));
    }

    @Override
    public void line(PlatformPlayer viewer, ScoreboardLine.Displayed line) {
        this.printer.add(this.serializer.serialize(line.text()));

        Component score = Optional.ofNullable(line.delegate().score().formatter())
                .map(formatter -> formatter.format(line.delegate().score().value()))
                .orElse(Component.text(line.delegate().score().value()));
        this.printer.add("- Score: %s", this.serializer.serialize(score));
        this.printer.add("  * Formatted: %b", line.delegate().score().formatter() != null);
        this.printer.newline();
    }

    @Override
    public void show(AssignedScoreboard scoreboard) {}

    @Override
    public void hide(AssignedScoreboard scoreboard) {}

    @Override
    public void registerTeam(PlatformPlayer viewer) {}

}
