package net.impactdev.impactor.minecraft.scoreboard.relative;

import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.scoreboards.players.RelativeAnimatable;
import net.kyori.adventure.text.Component;

public abstract class AbstractRelativeAnimatable implements RelativeAnimatable {

    private final PlatformPlayer viewer;
    private Component component = Component.empty();

    @Override
    public void update() {
        this.component = this.resolver().update(this.viewer);
    }
}
