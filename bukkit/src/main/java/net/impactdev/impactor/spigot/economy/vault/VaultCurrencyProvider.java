package net.impactdev.impactor.spigot.economy.vault;

import com.google.common.base.Suppliers;
import net.impactdev.impactor.api.services.economy.currency.Currency;
import net.impactdev.impactor.api.services.economy.currency.CurrencyProvider;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("PatternValidation")
public final class VaultCurrencyProvider implements CurrencyProvider {

    private final Economy economy;
    private final Supplier<Currency> currency;

    public VaultCurrencyProvider(Economy economy) {
        this.economy = economy;
        this.currency = Suppliers.memoize(() -> Currency.builder()
                .key(Key.key("impactor", this.economy.currencyNamePlural().toLowerCase()))
                .primary(true)
                .name(text(this.economy.currencyNameSingular()))
                .plural(text(this.economy.currencyNamePlural()))
                .starting(BigDecimal.valueOf(0))
                .symbol(Component.empty())
                .decimals(this.economy.fractionalDigits())
                .build()
        );
    }

    @Override
    public @NotNull Currency primary() {
        return this.currency.get();
    }

    @Override
    public Optional<Currency> currency(Key key) {
        return Optional.ofNullable(this.currency.get())
                .filter(currency -> currency.key().equals(key));
    }

    @Override
    public Set<Currency> registered() {
        return Collections.singleton(this.currency.get());
    }

    @Override
    public CompletableFuture<Boolean> registerCurrency(Currency currency) {
        return CompletableFuture.completedFuture(false);
    }
}
