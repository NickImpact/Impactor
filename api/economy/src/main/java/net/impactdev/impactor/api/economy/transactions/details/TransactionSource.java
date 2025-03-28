package net.impactdev.impactor.api.economy.transactions.details;

import java.math.BigDecimal;

public record TransactionSource(Provider provider, BigDecimal amount) {

    public enum Provider {

        Account,
        Bank,
        Credit

    }

}
