package net.impactdev.impactor.sponge.services.economy.accounts;

import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.util.UUID;

public class SpongeUniqueAccount extends SpongeBaseAccount implements UniqueAccount {

    private final UUID uuid;

    public SpongeUniqueAccount(final UUID uuid) {
        super(PlatformPlayer.getOrCreate(uuid));
        this.uuid = uuid;
    }

    @Override
    public UUID uniqueId() {
        return this.uuid;
    }
}
