package net.impactdev.impactor.sponge.platform;

import net.impactdev.impactor.platform.players.ServerPlayerProvider;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.api.Sponge;

import java.util.Optional;
import java.util.UUID;

public class SpongeServerPlayerProvider implements ServerPlayerProvider {
    @Override
    public Optional<ServerPlayer> locate(UUID target) {
        return Sponge.server().player(target).map(sponge -> (ServerPlayer) sponge);
    }
}
