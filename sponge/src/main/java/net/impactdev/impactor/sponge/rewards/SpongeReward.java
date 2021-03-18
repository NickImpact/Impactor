package net.impactdev.impactor.sponge.rewards;

import net.impactdev.impactor.api.rewards.Reward;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public interface SpongeReward<T> extends Reward<T, ServerPlayer> {}
