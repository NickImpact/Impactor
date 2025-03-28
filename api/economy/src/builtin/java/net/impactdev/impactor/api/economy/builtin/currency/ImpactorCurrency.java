package net.impactdev.impactor.api.economy.builtin.currency;

import com.google.common.base.Strings;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class ImpactorCurrency implements Currency {

    private final Key key;
    private final Naming naming;
    private final Component symbol;
    private final BigDecimal defaultBalance;
    private final int decimals;
    private final BigDecimal minimum;
    private final BigDecimal maximum;

    private final String formattingPattern;

    public ImpactorCurrency(ImpactorCurrencyBuilder builder) {
        this.key = builder.key;
        this.naming = builder.naming;
        this.symbol = builder.symbol;
        this.defaultBalance = builder.defaultBalance;
        this.decimals = builder.decimals;
        this.minimum = builder.minimum;
        this.maximum = builder.maximum;

        StringBuilder sb = new StringBuilder();
        sb.append("#,###");
        if (this.decimals > 0) {
            sb.append(".").append(Strings.repeat("0", this.decimals));
        }

        this.formattingPattern = sb.toString();
    }

    @Override
    public Key key() {
        return this.key;
    }

    @Override
    public Naming naming() {
        return this.naming;
    }

    @Override
    public Component symbol() {
        return this.symbol;
    }

    @Override
    public BigDecimal defaultAccountBalance() {
        return this.defaultBalance;
    }

    @Override
    public int decimals() {
        return this.decimals;
    }

    @Override
    public BigDecimal minimum() {
        return this.minimum;
    }

    @Override
    public BigDecimal maximum() {
        return this.maximum;
    }

    @Override
    public Component format(@NotNull BigDecimal amount, boolean condensed, @NotNull Locale locale) {
        DecimalFormat formatter = new DecimalFormat(this.formattingPattern, new DecimalFormatSymbols(locale));
        TagResolver resolver = this.createTagResolver(amount, locale);

        return null;
    }

    public static final class ImpactorCurrencyBuilder implements Currency.Builder {

        private Key key;
        private Naming naming;
        private Component symbol;
        private BigDecimal defaultBalance;
        private int decimals;
        private BigDecimal minimum;
        private BigDecimal maximum;

        @Override
        public Builder key(@NotNull Key key) {
            this.key = key;
            return this;
        }

        @Override
        public Builder displayName(@NotNull Component singular, @NotNull Component plural) {
            this.naming = new Naming(singular, plural);
            return this;
        }

        @Override
        public Builder symbol(@NotNull Component symbol) {
            this.symbol = symbol;
            return this;
        }

        @Override
        public Builder starting(@NotNull BigDecimal amount) {
            this.defaultBalance = amount;
            return this;
        }

        @Override
        public Builder decimals(int decimals) {
            this.decimals = decimals;
            return this;
        }

        @Override
        public Builder formatting(@NotNull String format) {

            return this;
        }

        @Override
        public Builder minimum(@NotNull BigDecimal minimum) {
            return this;
        }

        @Override
        public Builder maximum(@NotNull BigDecimal maximum) {
            return this;
        }

        @Override
        public Builder boundaries(@NotNull BigDecimal min, @NotNull BigDecimal max) {
            return this;
        }

        @Override
        public Currency build() {
            return new ImpactorCurrency(this);
        }
    }
}
