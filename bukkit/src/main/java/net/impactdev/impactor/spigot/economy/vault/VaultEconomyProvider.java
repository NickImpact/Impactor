package net.impactdev.impactor.spigot.economy.vault;

import net.impactdev.impactor.api.services.economy.EconomyService;
import net.impactdev.impactor.api.services.economy.accounts.Account;
import net.impactdev.impactor.api.services.economy.currency.Currency;
import net.impactdev.impactor.api.services.economy.currency.CurrencyProvider;
import net.impactdev.impactor.api.services.economy.transactions.EconomyResultType;
import net.impactdev.impactor.api.services.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.economy.ImpactorEconomyService;
import net.impactdev.impactor.economy.accounts.accessors.UniqueAccountAccessor;
import net.impactdev.impactor.economy.accounts.accessors.VirtualAccountAccessor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class VaultEconomyProvider extends AbstractEconomy {

    private final EconomyService delegate;
    private final Currency currency;

    public VaultEconomyProvider() {
        this.delegate = new ImpactorEconomyService();
        this.currency = this.delegate.currencies().primary();
    }

    public EconomyService impactor() {
        return this.delegate;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "Impactor Vault Economy Service";
    }

    @Override
    public boolean hasBankSupport() {
        return true;
    }

    @Override
    public int fractionalDigits() {
        return this.currency.decimals();
    }

    @Override
    public String format(double amount) {
        return PlainTextComponentSerializer.plainText().serialize(this.currency.format(BigDecimal.valueOf(amount)));
    }

    @Override
    public String currencyNamePlural() {
        return PlainTextComponentSerializer.plainText().serialize(this.currency.plural());
    }

    @Override
    public String currencyNameSingular() {
        return PlainTextComponentSerializer.plainText().serialize(this.currency.name());
    }

    @Override
    public boolean hasAccount(String playerName) {
        return true;
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return true;
    }

    @Override
    public double getBalance(String playerName) {
        return this.user(playerName).balance().doubleValue();
    }

    @Override
    public double getBalance(String playerName, String world) {
        return this.user(playerName).balance().doubleValue();
    }

    @Override
    public boolean has(String playerName, double amount) {
        return this.getBalance(playerName) >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return this.getBalance(playerName) >= amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return this.translateTransaction(this.user(playerName).withdraw(BigDecimal.valueOf(amount)));
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return this.withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return this.translateTransaction(this.user(playerName).deposit(BigDecimal.valueOf(amount)));
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return this.depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        this.delegate.account(new VirtualAccountAccessor(name), this.currency);
        return new EconomyResponse(0, this.currency.starting().doubleValue(), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        BigDecimal balance = this.delegate.account(new VirtualAccountAccessor(name), this.currency).join().balance();
        return new EconomyResponse(0, balance.doubleValue(), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return true;
    }

    private Account user(String name) {
        UUID target = Bukkit.getServer().getOfflinePlayer(name).getUniqueId();
        return this.delegate.account(new UniqueAccountAccessor(target), this.currency).join();
    }

    private EconomyResponse translateTransaction(EconomyTransaction transaction) {
        EconomyResponse.ResponseType response = transaction.result() == EconomyResultType.SUCCESS ?
                EconomyResponse.ResponseType.SUCCESS : EconomyResponse.ResponseType.FAILURE;

        return new EconomyResponse(
                transaction.amount().doubleValue(),
                transaction.account().balance().doubleValue(),
                response,
                transaction.result() != EconomyResultType.SUCCESS ? transaction.result().name() : null
        );
    }
}
