package net.impactdev.impactor.sponge.services.economy.accounts;

import net.impactdev.impactor.api.services.economy.accounts.AccountAccessor;
import org.spongepowered.api.service.economy.account.VirtualAccount;

public class SpongeVirtualAccount extends SpongeBaseAccount implements VirtualAccount {

    public SpongeVirtualAccount(AccountAccessor holder) {
        super(holder);
    }

}
