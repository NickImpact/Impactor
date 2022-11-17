package net.impactdev.impactor.spigot.economy.vault;

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.services.economy.EconomyService;
import net.impactdev.impactor.api.services.economy.accounts.Account;
import net.impactdev.impactor.api.services.economy.accounts.AccountAccessor;
import net.impactdev.impactor.api.services.economy.currency.Currency;
import net.impactdev.impactor.api.services.economy.currency.CurrencyProvider;
import net.impactdev.impactor.spigot.economy.vault.accounts.VaultPlayerAccountAccessor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VaultEconomyLink implements EconomyService {

    private final Economy delegate;
    private final CurrencyProvider provider;

    public VaultEconomyLink(JavaPlugin plugin) {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if(rsp == null) {
            VaultEconomyProvider delegate = new VaultEconomyProvider();
            this.delegate = delegate;
            this.provider = delegate.impactor().currencies();

            Bukkit.getServicesManager().register(Economy.class, this.delegate, plugin, ServicePriority.Highest);
        } else {
            this.delegate = rsp.getProvider();
            this.provider = new VaultCurrencyProvider(this.delegate);
        }
    }

    @Override
    public String name() {
        return "Vault Economy Link";
    }

    @Override
    public CurrencyProvider currencies() {
        return this.provider;
    }

    @Override
    public CompletableFuture<Account> account(AccountAccessor accessor, Currency currency) {
        return accessor.account(currency);
    }

    @Override
    public CompletableFuture<List<AccountAccessor>> accessors() {
        if(this.delegate instanceof VaultEconomyProvider) {
            return ((VaultEconomyProvider) this.delegate).impactor().accessors();
        }

        return CompletableFuture.supplyAsync(() -> {
            List<AccountAccessor> accessors = Lists.newArrayList();
            for (OfflinePlayer player : Bukkit.getServer().getOfflinePlayers()) {
                accessors.add(new VaultPlayerAccountAccessor(this.delegate, this.currencies().primary(), player.getName()));
            }

            return accessors;
        });
    }
}
